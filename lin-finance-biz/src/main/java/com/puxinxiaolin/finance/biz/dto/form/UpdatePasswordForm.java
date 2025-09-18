package com.puxinxiaolin.finance.biz.dto.form;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UpdatePasswordForm {

    /**
     * 旧密码
     */
    @NotBlank(message = "旧密码")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\\\W]{6,18}$", message = "密码长度需在6~18位字符，且必须包含字母和数字！")
    @NotBlank
    private String oldPassword;

    /**
     * 新密码
     */
    @ApiModelProperty(value = "新密码")
    @NotBlank(message = "请输入新密码")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\\\W]{6,18}$", message = "密码长度需在6~18位字符，且必须包含字母和数字！")
    private String password;

    /**
     * 确认新密码
     */
    @ApiModelProperty(value = "确认新密码")
    @NotBlank(message = "请确认新密码")
    @Pattern(regexp = "^(?![0-9]+$)(?![a-zA-Z]+$)[0-9A-Za-z\\\\W]{6,18}$", message = "密码长度需在6~18位字符，且必须包含字母和数字！")
    private String confirmPassword;

}
