package com.puxinxiaolin.common.exception;

import com.puxinxiaolin.common.constant.ApiResponseCode;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description: 异常基类
 * @Author: YCcLin
 * @Date: 2025/9/14 11:28
 */
@Setter
@Getter
public class BaseException extends RuntimeException {

    private static final long serialVersionUID = 2612992235262400823L;

    private Integer code = null;

    public BaseException(String message) {
        super(message);
        this.code = ApiResponseCode.SERVICE_ERROR.getCode();
    }

    public BaseException(String message, Throwable t) {
        super(message, t);
        this.code = ApiResponseCode.SERVICE_ERROR.getCode();
    }

    public BaseException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(Integer code, String message, Throwable t) {
        super(message, t);
        this.code = code;
    }

}
