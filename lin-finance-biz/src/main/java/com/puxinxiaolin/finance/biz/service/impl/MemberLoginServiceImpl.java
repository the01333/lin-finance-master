package com.puxinxiaolin.finance.biz.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puxinxiaolin.common.constant.ApiResponseCode;
import com.puxinxiaolin.common.dto.TokenResponse;
import com.puxinxiaolin.common.exception.BizException;
import com.puxinxiaolin.common.exception.ParameterException;
import com.puxinxiaolin.common.service.TokenService;
import com.puxinxiaolin.common.util.DateUtil;
import com.puxinxiaolin.common.util.MyUtil;
import com.puxinxiaolin.finance.biz.constant.RedisKeyConstant;
import com.puxinxiaolin.finance.biz.domain.Member;
import com.puxinxiaolin.finance.biz.domain.MemberBindPhone;
import com.puxinxiaolin.finance.biz.dto.AdminDTO;
import com.puxinxiaolin.finance.biz.dto.form.*;
import com.puxinxiaolin.finance.biz.enums.SmsCodeEnum;
import com.puxinxiaolin.finance.biz.service.MemberBindPhoneService;
import com.puxinxiaolin.finance.biz.service.MemberLoginService;
import com.puxinxiaolin.finance.biz.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberLoginServiceImpl implements MemberLoginService {
    final MemberBindPhoneService memberBindPhoneService;
    final MemberService memberService;
    final TokenService<AdminDTO> adminTokenService;
    final RedisTemplate<String, Object> redisTemplate;
    final PasswordEncoder passwordEncoder;
    final ObjectMapper objectMapper;

    /**
     * 校验短信验证码
     *
     * @param phone
     * @param smsCode
     * @param smsCodeType
     * @return
     */
    @Override
    public Boolean checkSmsCode(String phone, String smsCode, String smsCodeType) {
        String key = RedisKeyConstant.SMS_CODE + smsCodeType + phone;
        SmsCodeResult smsCodeResult = (SmsCodeResult) redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);

        if (Objects.isNull(smsCodeResult) || !Objects.equals(smsCode, smsCodeResult.getCode())) {
            throw new ParameterException("smsCode", "短信验证码错误，请重新获取验证码");
        }
        return true;
    }

    /**
     * 获取客户端 ID
     *
     * @return
     */
    @Override
    public String getClientId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取图形验证码
     *
     * @param form
     * @return
     */
    @Override
    public String getBase64Code(GetBase64CodeForm form) {
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(300, 192, 5, 1000);
        String code = lineCaptcha.getCode();
        log.info("MemberLoginServiceImpl.getBase64Code.clientId:{}, code:{}", form.getClientId(), code);

        redisTemplate.opsForValue()
                .set(RedisKeyConstant.GRAPHIC_VERIFICATION_CODE + form.getClientId(), code, 15, TimeUnit.MINUTES);

        return lineCaptcha.getImageBase64();
    }

    /**
     * 获取短信验证码
     *
     * @param form
     */
    @Override
    public void sendSmsCode(GetSmsCodeForm form) {
        // 1. 先校验图形验证码
        checkBase64Code(form.getClientId(), form.getCode());

        String key = RedisKeyConstant.SMS_CODE + form.getSmsCodeType() + form.getPhone();
        SmsCodeResult smsCodeResult = (SmsCodeResult) redisTemplate.opsForValue().get(key);
        // 2. 校验验证码的获取时间是否是 60 秒以内
        if (Objects.nonNull(smsCodeResult)) {
            Duration duration = DateUtil.getDuration(smsCodeResult.getGetTime(), DateUtil.getSystemTime());
            if (duration.getSeconds() < 60) {
                throw new ParameterException("验证码获取频繁，请稍后重试");
            }
        }

        // 3. 校验手机号是否存在
        MemberBindPhone memberByPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
        if (form.getSmsCodeType().equals(SmsCodeEnum.REG.getCode())
                && Objects.nonNull(memberByPhone)) {
            throw new ParameterException("phone", "该手机号已注册");
        }
        if (form.getSmsCodeType().equals(SmsCodeEnum.LOGIN.getCode())
                && Objects.isNull(memberByPhone)) {
            throw new ParameterException("phone", "该手机号未注册");
        }

        // 4. 生成短信验证码
        int smsCode = MyUtil.getRandom(6);
        smsCodeResult = new SmsCodeResult();
        smsCodeResult.setCode(String.valueOf(smsCode));
        smsCodeResult.setGetTime(DateUtil.getSystemTime());
        smsCodeResult.setPhone(form.getPhone());
        redisTemplate.opsForValue()
                .set(key, smsCodeResult, 15, TimeUnit.MINUTES);
        log.info("MemberLoginServiceImpl.sendSmsCode.clientId:{}, phone:{}, smsCode:{}",
                form.getClientId(), form.getPhone(), smsCode);

        // todo 5. 调用三方发送短信验证码
    }

    /**
     * 手机密码登录
     *
     * @param form
     * @return
     */
    @Override
    public TokenResponse phonePasswordLogin(PhonePasswordLoginForm form) {
        // 1. 校验图形验证码
        checkBase64Code(form.getClientId(), form.getCode());

        // 2. 校验密码
        MemberBindPhone memberByPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
        if (memberByPhone == null || StringUtils.isBlank(memberByPhone.getPassword())) {
            throw new BizException(ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getCode(), ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getMessage());
        }
        if (!passwordEncoder.matches(form.getPassword(), memberByPhone.getPassword())) {
            throw new BizException(ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getCode(), ApiResponseCode.ACCOUNT_PASSWORD_ERROR.getMessage());
        }

        Member member = memberService.get(memberByPhone.getMemberId());
        return loginSuccess(member, form.getClientId());
    }

    /**
     * 登录成功则存储登录信息到 redis
     *
     * @param member
     * @param clientId
     * @return
     */
    @Override
    public TokenResponse loginSuccess(Member member, String clientId) {
        try {
            AdminDTO adminDTO = new AdminDTO();
            adminDTO.setId(member.getId());
            adminDTO.setTenantId(member.getTenantId());
            adminDTO.setSysRoleIds(objectMapper.readValue(member.getSysRoleIds(),
                    new TypeReference<Set<Long>>() {
            }));

            adminTokenService.setToken(adminDTO);
            return adminDTO.getToken();
        } catch (JsonProcessingException e) {
            throw new BizException("登录失败", e);
        }
    }

    /**
     * 手机号短信登录
     *
     * @param form
     * @return
     */
    @Override
    public TokenResponse phoneSmsCodeLogin(PhoneSmsCodeLoginForm form) {
        // 1. 校验短信验证码
        checkSmsCode(form.getPhone(), form.getSmsCode(), SmsCodeEnum.LOGIN.getCode());
        
        // 2. 校验用户信息
        MemberBindPhone memberByPhone = memberBindPhoneService.getMemberByPhone(form.getPhone());
        if (memberByPhone == null) {
            throw new BizException("该手机号未注册");
        }

        Member member = memberService.get(memberByPhone.getMemberId());
        return loginSuccess(member, form.getClientId());
    }

    /**
     * 校验图形验证码
     *
     * @param clientId
     * @param code
     */
    private Boolean checkBase64Code(String clientId, String code) {
        String key = RedisKeyConstant.GRAPHIC_VERIFICATION_CODE + clientId;
        String value = (String) redisTemplate.opsForValue().get(key);
        redisTemplate.delete(key);

        if (!Objects.equals(value, code)) {
            throw new ParameterException("code", "图形验证码错误");
        }
        return true;
    }

}
