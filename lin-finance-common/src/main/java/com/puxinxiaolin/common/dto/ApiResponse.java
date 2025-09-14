package com.puxinxiaolin.common.dto;

import com.puxinxiaolin.common.constant.ApiResponseCode;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ApiResponse<T> {
    
    private Boolean success = true;
    private T data;
    private Integer code = 0;
    private String message;
    private Map<String, String> errorMessage;

    public static <T> ApiResponse<T> success() {
        return success(null);
    }
    
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(true);
        apiResponse.setData(data);
        apiResponse.setCode(ApiResponseCode.SUCCESS.getCode());
        apiResponse.setMessage(ApiResponseCode.SUCCESS.getMessage());
        return apiResponse;
    }
    
    public static <T> ApiResponse<T> error(Map<String, String> errors) {
        ApiResponse<T> apiResponse = new ApiResponse<>();
        apiResponse.setSuccess(false);
        apiResponse.setData(null);
        apiResponse.setCode(ApiResponseCode.SERVICE_ERROR.getCode());
        apiResponse.setMessage(ApiResponseCode.SERVICE_ERROR.getMessage());
        apiResponse.setErrorMessage(errors);
        return apiResponse;
    }
    
    public static <T> ApiResponse<T> error(String message) {
        Map<String, String> errors = new HashMap<>();
        errors.put(message, message);
        return error(errors);
    }
    
    public ApiResponse<T> error(String message, T data) {
        this.setSuccess(false);
        this.setCode(ApiResponseCode.SERVICE_ERROR.getCode());
        this.setMessage(ApiResponseCode.SERVICE_ERROR.getMessage());
        this.setData(data);
        return this;
    }

    public void error(Integer code, Map<String, String> errors) {
        this.setSuccess(false);
        this.setCode(code);
        this.setErrorMessage(errors);
        this.setData(data);
    }
    
}
