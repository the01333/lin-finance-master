package com.puxinxiaolin.finance.biz.service.impl;

import com.puxinxiaolin.common.exception.BizException;
import com.puxinxiaolin.common.exception.ParameterException;
import com.puxinxiaolin.finance.biz.constant.RedisKeyConstant;
import com.puxinxiaolin.finance.biz.domain.MemberBindPhone;
import com.puxinxiaolin.finance.biz.dto.form.PhoneRegisterForm;
import com.puxinxiaolin.finance.biz.enums.SmsCodeEnum;
import com.puxinxiaolin.finance.biz.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberRegServiceImpl implements MemberRegService {
    final MemberLoginService memberLoginService;
    final MemberBindPhoneService memberBindPhoneService;
    final MemberService memberService;
    final TenantService tenantService;
    final RedissonClient redissonClient;
    final TransactionTemplate transactionTemplate;

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

}
