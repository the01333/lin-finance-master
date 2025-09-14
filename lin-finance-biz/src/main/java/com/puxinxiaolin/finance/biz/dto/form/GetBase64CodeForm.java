package com.puxinxiaolin.finance.biz.dto.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class GetBase64CodeForm {

    /**
     * 客户端 ID
     */
    @ApiModelProperty(value = "客户端 ID")
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9]{6,32}$", message = "clientId 非法")
    private String clientId;

}
