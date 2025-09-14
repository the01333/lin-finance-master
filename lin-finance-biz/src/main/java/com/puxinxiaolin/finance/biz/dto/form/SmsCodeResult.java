package com.puxinxiaolin.finance.biz.dto.form;

import lombok.Data;

import java.util.Date;

/**
 * @Description: 获取短信验证码的返参
 * @Author: YCcLin
 * @Date: 2025/9/14 14:58
 */
@Data
public class SmsCodeResult {

    /**
     * 短信验证码
     */
    private String code;

    /**
     * 短信验证码存储到 redis 的时间
     */
    private Date getTime;

    /**
     * 手机号
     */
    private String phone;

}