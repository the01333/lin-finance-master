package com.puxinxiaolin.finance.biz.service;

import com.puxinxiaolin.finance.biz.dto.form.GetBase64CodeForm;
import com.puxinxiaolin.finance.biz.dto.form.GetSmsCodeForm;

public interface MemberLoginService {

    /**
     * 校验短信验证码
     *
     * @param phone
     * @param smsCode
     * @param smsCodeType
     * @return
     */
    Boolean checkSmsCode(String phone, String smsCode, String smsCodeType);

    /**
     * 获取客户端 ID
     *
     * @return
     */
    String getClientId();

    /**
     * 获取图形验证码
     *
     * @param form
     * @return
     */
    String getBase64Code(GetBase64CodeForm form);

    /**
     * 获取短信验证码
     *
     * @param form
     */
    void sendSmsCode(GetSmsCodeForm form);

}
