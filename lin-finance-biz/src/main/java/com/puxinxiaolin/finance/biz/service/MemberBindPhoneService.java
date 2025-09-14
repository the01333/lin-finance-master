package com.puxinxiaolin.finance.biz.service;

import com.puxinxiaolin.finance.biz.domain.MemberBindPhone;
import com.puxinxiaolin.finance.biz.dto.form.UpdatePasswordForm;
import com.puxinxiaolin.finance.biz.dto.form.UpdatePhoneForm;

public interface MemberBindPhoneService {

    /**
     * 根据手机号获取用户信息
     *
     * @param phone
     * @return
     */
    MemberBindPhone getMemberByPhone(String phone);

    /**
     * 手机号注册
     *
     * @param phone
     * @param memberId
     * @param password
     * @return
     */
    Boolean reg(String phone, Long memberId, String password);

    /**
     * 修改密码
     *
     * @param form
     * @return
     */
    Boolean updatePassword(UpdatePasswordForm form);

    /**
     * 根据 memberId 获取用户信息
     *
     * @param userId
     * @return
     */
    MemberBindPhone getByMemberId(Long userId);

    /**
     * 修改手机号
     *
     * @param form
     * @return
     */
    Boolean updatePhone(UpdatePhoneForm form);

}