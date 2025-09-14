package com.puxinxiaolin.common.exception;

import com.puxinxiaolin.common.constant.ApiResponseCode;

/**
 * @Description: 登录异常
 * @Author: YCcLin
 * @Date: 2025/9/14 11:29
 */
public class LoginException extends BaseException {

    private static final long serialVersionUID = 979094253305695687L;

    public LoginException(String message) {
        super(ApiResponseCode.LOGIN_ERROR.getCode(), message);
    }

    public LoginException(String message, Throwable t) {
        super(message, t);
    }

}
