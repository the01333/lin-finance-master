package com.puxinxiaolin.finance.biz.service.impl;

import com.puxinxiaolin.finance.biz.domain.MemberBindPhone;
import com.puxinxiaolin.finance.biz.dto.form.UpdatePasswordForm;
import com.puxinxiaolin.finance.biz.dto.form.UpdatePhoneForm;
import com.puxinxiaolin.finance.biz.mapper.MemberBindPhoneMapper;
import com.puxinxiaolin.finance.biz.service.MemberBindPhoneService;
import com.puxinxiaolin.mybatis.help.MybatisWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.puxinxiaolin.finance.biz.domain.MemberBindPhoneField.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberBindPhoneServiceImpl implements MemberBindPhoneService {
    final MemberBindPhoneMapper memberBindPhoneMapper;
    final PasswordEncoder passwordEncoder;

    /**
     * 根据手机号获取用户信息
     *
     * @param phone
     * @return
     */
    @Override
    public MemberBindPhone getMemberByPhone(String phone) {
        MybatisWrapper<MemberBindPhone> wrapper = new MybatisWrapper<>();
        wrapper.select(MemberId, Phone, Password)
                .whereBuilder()
                .andEq(Phone, phone)
                .andEq(setDisable(false));

        return memberBindPhoneMapper.topOne(wrapper);
    }

    /**
     * 手机号注册
     *
     * @param phone
     * @param memberId
     * @param password
     * @return
     */
    @Override
    public Boolean reg(String phone, Long memberId, String password) {
        MemberBindPhone memberBindPhone = new MemberBindPhone();
        memberBindPhone.initDefault();
        memberBindPhone.setPhone(phone);
        memberBindPhone.setMemberId(memberId);
        memberBindPhone.setPassword(passwordEncoder.encode(password));

        return memberBindPhoneMapper.insert(memberBindPhone) > 0;
    }

    /**
     * 修改密码
     *
     * @param form
     * @return
     */
    @Override
    public Boolean updatePassword(UpdatePasswordForm form) {
        return true;
    }

    /**
     * 根据 memberId 获取用户信息
     *
     * @param memberId
     * @return
     */
    @Override
    public MemberBindPhone getByMemberId(Long memberId) {
        return new MemberBindPhone();
    }

    /**
     * 修改手机号
     *
     * @param form
     * @return
     */
    @Override
    public Boolean updatePhone(UpdatePhoneForm form) {
        return true;
    }

}
