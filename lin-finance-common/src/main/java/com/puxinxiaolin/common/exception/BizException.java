package com.puxinxiaolin.common.exception;

import com.puxinxiaolin.common.constant.ApiResponseCode;

/**
 * @Description: 业务处理异常
 * @Author: YCcLin
 * @Date: 2025/9/14 11:28
 */
public class BizException extends BaseException {

    private static final long serialVersionUID = 628904681759624791L;

    public BizException(String message) {
        super(ApiResponseCode.BUSINESS_ERROR.getCode(), message);
    }

    public BizException(int code, String message) {
        super(code, message);
    }

    public BizException(String message, Throwable t) {
        super(message, t);
    }
}
