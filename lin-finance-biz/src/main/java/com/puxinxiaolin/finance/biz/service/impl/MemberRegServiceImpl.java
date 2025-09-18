package com.puxinxiaolin.finance.biz.service.impl;

import com.puxinxiaolin.common.dto.TokenResponse;
import com.puxinxiaolin.common.exception.BizException;
import com.puxinxiaolin.common.exception.ParameterException;
import com.puxinxiaolin.common.service.TokenService;
import com.puxinxiaolin.finance.biz.config.ObjectConvertor;
import com.puxinxiaolin.finance.biz.constant.RedisKeyConstant;
import com.puxinxiaolin.finance.biz.domain.MemberBindPhone;
import com.puxinxiaolin.finance.biz.domain.MemberBindWxOpenId;
import com.puxinxiaolin.finance.biz.dto.AdminDTO;
import com.puxinxiaolin.finance.biz.dto.form.PhoneRegisterForm;
import com.puxinxiaolin.finance.biz.dto.vo.GenerateMpRegCodeVo;
import com.puxinxiaolin.finance.biz.enums.SmsCodeEnum;
import com.puxinxiaolin.finance.biz.service.*;
import com.puxinxiaolin.wx.config.WxConfig;
import com.puxinxiaolin.wx.dto.AccessTokenResult;
import com.puxinxiaolin.wx.dto.MpQrCodeCreateRequest;
import com.puxinxiaolin.wx.dto.MpQrCodeCreateResult;
import com.puxinxiaolin.wx.dto.MpSubscribeEventRequest;
import com.puxinxiaolin.wx.service.WxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRegServiceImpl implements MemberRegService {
    final MemberLoginService memberLoginService;
    final MemberBindPhoneService memberBindPhoneService;
    final MemberService memberService;
    final TenantService tenantService;
    final WxService wxService;
    final MemberBindWxOpenIdService memberBindWxOpenIdService;
    final WxConfig wxConfig;
    final RedissonClient redissonClient;
    final TransactionTemplate transactionTemplate;
    final ObjectConvertor objectConvertor;
    final TokenService<AdminDTO> tokenService;
    final RedisTemplate<String, Object> redisTemplate;

    /**
     * 手机号注册
     *
     * @param form
     * @return
     */
    @Override
    public Long phoneReg(PhoneRegisterForm form) {
        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            throw new ParameterException("两次输入的密码不一致");
        }

        String phone = form.getPhone();

        // 1. 校验短信验证码
        memberLoginService.checkSmsCode(phone, form.getSmsCode(), SmsCodeEnum.REG.getCode());

        // 分布式锁, 防止同一手机号重复注册
        RLock rLock = redissonClient.getLock(RedisKeyConstant.PHONE_CHANGE + phone);
        try {
            rLock.lock();

            MemberBindPhone memberByPhone = memberBindPhoneService.getMemberByPhone(phone);
            if (Objects.nonNull(memberByPhone)) {
                throw new BizException("该手机号已注册");
            }

            // 手动事务, 减小锁力度
            Long memberId = transactionTemplate.execute(transactionStatus -> {
                // 2. 新增租户记录
                Long tenantId = tenantService.add();
                Long id = memberService.reg(tenantId);
                if (id <= 0) {
                    throw new BizException("注册异常");
                }

                // 3. 新增用户绑定手机记录
                memberBindPhoneService.reg(phone, id, form.getPassword());
                return id;
            });
            if (Objects.isNull(memberId)) {
                throw new BizException("注册失败");
            }

            return memberId;
        } catch (Exception e) {
            throw new BizException("注册失败");
        } finally {
            rLock.unlock();
        }
    }

    /**
     * 生成微信公众号二维码
     *
     * @param clientId
     * @return
     */
    @Override
    public GenerateMpRegCodeVo generateMpRegCode(String clientId) {
        AccessTokenResult accessTokenResult = wxService.getMpAccessToken(wxConfig.getMp().getAppId(), wxConfig.getMp().getSecret());

        MpQrCodeCreateRequest request = new MpQrCodeCreateRequest();
        request.setExpireSeconds(wxConfig.getMp().getCodeExpire());
        request.setActionName("QR_STR_SCENE");
        request.setActionInfo(request.new ActionInfo());
        request.getActionInfo().setScene(request.new scene());
        request.getActionInfo().getScene().setSceneStr("ScanReg_" + wxConfig.getMp().getAppId() + "_" + clientId);
        MpQrCodeCreateResult response = wxService.createMpQrcodeCreate(accessTokenResult.getAccessToken(), request);

        return objectConvertor.toGenerateMpRegCodeResponse(response);
    }

    /**
     * 处理微信公众号关注事件
     *
     * @param request
     */
    @EventListener
    @Override
    public void handleMpSubscribeEventRequest(MpSubscribeEventRequest request) {
        log.info("处理微信公众号事件: {}", request.getEvent());

        if ("subscribe".equals(request.getEvent())
                && StringUtils.isNotBlank(request.getEventKey())) {
            String[] keys = request.getEventKey().split("_");
            if ("qrcode".equals(keys[0]) && "ScanReg".equals(keys[1])) {
                log.info("处理微信公众号关注事件的 appid: {}, clientId: {}", keys[2], keys[3]);
                registerByOpenId(keys[2], keys[3], request.getToUserName());
            }
        } else if ("SCAN".equals(request.getEvent())
                && StringUtils.isNotBlank(request.getEventKey())) {
            String[] keys = request.getEventKey().split("_");
            if ("ScanReg".equals(keys[0])) {
                log.info("处理微信公众号关注事件的 appid: {}, clientId: {}", keys[1], keys[2]);
                registerByOpenId(keys[1], keys[2], request.getToUserName());
            }
        }
    }

    /**
     * 通过 openId 注册
     *
     * @param appId
     * @param clientId
     * @param openId
     */
    @Override
    public TokenResponse registerByOpenId(String appId, String clientId, String openId) {
        Long memberId = scReg(appId, openId);

        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(memberId);
        tokenService.setToken(adminDTO);
        
        // 存入 redis
        redisTemplate.opsForValue().set(RedisKeyConstant.CLIENT_TOKEN_KEY + clientId, adminDTO.getToken(), 10, TimeUnit.MINUTES);
        
        return adminDTO.getToken(); 
    }

    /**
     * 扫码注册
     *
     * @param appId
     * @param openId
     * @return
     */
    @Override
    public Long scReg(String appId, String openId) {
        MemberBindWxOpenId memberBindWxOpenId = memberBindWxOpenIdService.get(appId, openId);
        if (Objects.nonNull(memberBindWxOpenId)) {
            return memberBindWxOpenId.getMemberId();
        }

        Long memberId = transactionTemplate.execute(transactionStatus -> {
            Long tenantId = tenantService.add();
            Long id = memberService.reg(tenantId);
            memberBindWxOpenIdService.reg(appId, openId, id);
            return id;
        });
        if (Objects.isNull(memberId)) {
            throw new BizException("注册失败");
        }
        
        return memberId;
    }

}
