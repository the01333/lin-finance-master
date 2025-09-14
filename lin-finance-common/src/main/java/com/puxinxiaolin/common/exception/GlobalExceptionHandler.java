package com.puxinxiaolin.common.exception;

import com.puxinxiaolin.common.constant.ApiResponseCode;
import com.puxinxiaolin.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @Description: 全局异常处理
 * @Author: YCcLin
 * @Date: 2025/9/14 11:31
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * 处理未匹配到的全部异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleException(Exception e) {
        log.error("Exception: {}", e.getMessage(), e);

        ApiResponse<Object> response = new ApiResponse<>();
        Map<String, String> errors = new HashMap<>();
        errors.put(ApiResponseCode.SERVICE_ERROR.getMessage(), e.getMessage());
        response.error(ApiResponseCode.SERVICE_ERROR.getCode(), errors);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理参数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(ParameterException.class)
    public ResponseEntity<ApiResponse<Object>> apiErrorException(ParameterException e) {
        log.error("ParameterException: {}", e.getMessage(), e);

        ApiResponse<Object> response = new ApiResponse<>();
        response.setMessage(e.getMessage());
        response.error(e.getCode(), e.getFieldErrors());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 处理登录异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(LoginException.class)
    public ResponseEntity<ApiResponse<Object>> apiErrorException(LoginException e) {
        log.error("LoginException: {}", e.getMessage(), e);

        ApiResponse<Object> response = new ApiResponse<>();
        Map<String, String> errors = new HashMap<>();
        errors.put(ApiResponseCode.LOGIN_ERROR.getMessage(), e.getMessage());
        response.error(e.getCode(), errors);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理参数异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BizException.class)
    public ResponseEntity<ApiResponse<Object>> apiErrorException(BizException e) {
        log.error("BizException: {}", e.getMessage(), e);

        ApiResponse<Object> response = new ApiResponse<>();
        Map<String, String> errors = new HashMap<>();
        errors.put(ApiResponseCode.BUSINESS_ERROR.getMessage(), e.getMessage());
        response.error(e.getCode(), errors);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 处理 BindException 异常
     * <p>
     * BindException: 作用于 @Validated @Valid 注解
     * </p>
     * <p>
     * 仅对于表单提交参数进行异常处理, 对于以json格式提交将会失效
     * 只对实体参数进行校验
     * </p>
     * <p>
     * 注: Controller 里的方法必须加上 @Validated 注解
     * </p>
     *
     * @param ex
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("BindException:", ex);

        ApiResponse<Object> response = new ApiResponse<>();
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(p -> {
            errors.put(p.getField(), p.getDefaultMessage());
        });
        response.error(ApiResponseCode.PARAMETER_INVALID.getCode(), errors);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * 处理 MethodArgumentNotValidException 异常
     * MethodArgumentNotValidException-Spring 封装的参数验证异常处理
     * 作用于 @Validated @Valid 注解
     * 接收参数加上 @RequestBody注解（json格式）的异常处理
     *
     * @param ex
     * @return
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("ParameterException:", ex);

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        Map<String, String> errors = new HashMap<>();
        ex.getFieldErrors().forEach(p -> {
            errors.put(p.getField(), p.getDefaultMessage());
        });
        apiResponse.error(ApiResponseCode.PARAMETER_INVALID.getCode(), errors);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

    /**
     * ConstraintViolationException-jsr规范中的验证异常, 嵌套检验问题
     * ConstraintViolationException：作用于 @NotBlank @NotNull @NotEmpty 注解, 校验单个String、Integer、Collection等参数异常处理
     * 注: Controller 上必须添加 @Validated 注解, 不是加在 Controller 的方法上
     * 否则接口单个参数校验无效（RequestParam，PathVariable参数校验）
     *
     * @param ex
     * @return
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> constraintViolationExceptionHandler(ConstraintViolationException ex) {
        log.error("ParameterException:", ex);

        ApiResponse<Object> apiResponse = new ApiResponse<>();
        Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();

        Map<String, String> errors = new HashMap<>();
        violations.forEach(p -> {
            String fieldName = null;
            // 获取字段名称（最后一个元素才是）
            Iterator<Path.Node> nodeIterator = p.getPropertyPath().iterator();
            while (nodeIterator.hasNext()) {
                fieldName = nodeIterator.next().getName();
            }
            errors.put(fieldName, p.getMessage());
        });
        apiResponse.error(ApiResponseCode.PARAMETER_INVALID.getCode(), errors);
        return new ResponseEntity<>(apiResponse, HttpStatus.OK);
    }

}
