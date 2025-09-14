package com.puxinxiaolin.finance.admin.api.controller;

import com.puxinxiaolin.common.dto.ApiResponse;
import com.puxinxiaolin.finance.biz.dto.form.GetBase64CodeForm;
import com.puxinxiaolin.finance.biz.dto.form.GetSmsCodeForm;
import com.puxinxiaolin.finance.biz.service.MemberLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户登录模块")
@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    final MemberLoginService memberLoginService;
    
    @ApiOperation(value = "获取客户端 ID")
    @GetMapping("/getClientId")
    public ApiResponse<String> getClient() {
        return ApiResponse.success(memberLoginService.getClientId());
    }

    @ApiOperation(value = "获取图形验证码")
    @GetMapping("/getBase64Code")
    public ApiResponse<String> getBase64Code(@Validated @ModelAttribute GetBase64CodeForm form) {
        return ApiResponse.success(memberLoginService.getBase64Code(form));
    }

    @ApiOperation(value = "获取短信验证码")
    @GetMapping(value = "/sendSmsCode")
    public ApiResponse<Void> sendSmsCode(@Validated @ModelAttribute GetSmsCodeForm form) {
        memberLoginService.sendSmsCode(form);
        return ApiResponse.success();
    }
    
}
