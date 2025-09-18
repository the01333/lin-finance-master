package com.puxinxiaolin.finance.biz.dto.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @Description: 生成微信公众号二维码入参
 * @Author: YCcLin
 * @Date: 2025/9/18 0:00
 */
@Data
public class GenerateMpRegCodeForm {

    /**
     * 客户端 id
     */
    @ApiModelProperty(value = "客户端 id")
    @NotBlank(message = "客户端id不能为空")
    @Pattern(regexp = "^[0-9A-Za-z]{6,32}$", message = "clientId非法")
    private String clientId;

}
