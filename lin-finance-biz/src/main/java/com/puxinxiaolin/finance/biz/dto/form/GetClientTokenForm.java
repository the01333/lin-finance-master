package com.puxinxiaolin.finance.biz.dto.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 获取客户端 token 入参
 * @Author: YCcLin
 * @Date: 2025/9/18 21:59
 */
@Data
public class GetClientTokenForm {

    /**
     * 客户端id
     */
    @Pattern(regexp = "^[0-9a-zA-Z]{6,32}$", message = "clientId非法")
    @NotBlank(message = "客户端id不能为空")
    private String clientId;

}