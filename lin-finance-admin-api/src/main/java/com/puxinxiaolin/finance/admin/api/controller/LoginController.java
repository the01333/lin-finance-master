package com.puxinxiaolin.finance.admin.api.controller;

import com.puxinxiaolin.common.dto.ApiResponse;
import com.puxinxiaolin.common.dto.TokenResponse;
import com.puxinxiaolin.finance.biz.dto.form.*;
import com.puxinxiaolin.finance.biz.service.MemberLoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户登录模块")
@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class LoginController {
    final MemberLoginService memberLoginService;

    /**
     * 获取客户端 ID
     *
     * @return
     */
    @ApiOperation(value = "获取客户端 ID")
    @GetMapping("/getClientId")
    public ApiResponse<String> getClient() {
        return ApiResponse.success(memberLoginService.getClientId());
    }

    /**
     * 获取图形验证码
     *
     * @param form
     * @return
     */
    @ApiOperation(value = "获取图形验证码")
    @GetMapping("/getBase64Code")
    public ApiResponse<String> getBase64Code(@Validated @ModelAttribute GetBase64CodeForm form) {
        return ApiResponse.success(memberLoginService.getBase64Code(form));
    }

    /**
     * 获取短信验证码
     *
     * @param form
     * @return
     */
    @ApiOperation(value = "获取短信验证码")
    @GetMapping(value = "/sendSmsCode")
    public ApiResponse<Void> sendSmsCode(@Validated @ModelAttribute GetSmsCodeForm form) {
        memberLoginService.sendSmsCode(form);
        return ApiResponse.success();
    }

    /**
     * 手机密码登录
     *
     * @param form
     * @return
     */
    @ApiOperation(value = "手机密码登录")
    @PostMapping(value = "/phonePasswordLogin")
    public ApiResponse<TokenResponse> phonePasswordLogin(@Validated @RequestBody PhonePasswordLoginForm form) {
        TokenResponse tokenResponse = memberLoginService.phonePasswordLogin(form);
        return ApiResponse.success(tokenResponse);
    }

    /**
     * 手机号短信登录
     *
     * @param form
     * @return
     */
    @ApiOperation(value = "手机号短信登录")
    @PostMapping("/phoneSmsCodeLogin")
    public ApiResponse<TokenResponse> phoneSmsCodeLogin(@Validated @RequestBody PhoneSmsCodeLoginForm form) {
        return ApiResponse.success(memberLoginService.phoneSmsCodeLogin(form));
    }

    /**
     * 获取客户端token
     *
     * @param request
     * @return
     */
    @GetMapping(value = "/getClientToken")
    public ApiResponse<TokenResponse> getClientToken(@Validated @ModelAttribute GetClientTokenForm request) {
        return ApiResponse.success(memberLoginService.getClientToken(request.getClientId()));
    }
    
}
