package com.puxinxiaolin.finance.admin.api.controller;

import com.puxinxiaolin.common.dto.ApiResponse;
import com.puxinxiaolin.finance.biz.dto.form.PhoneRegisterForm;
import com.puxinxiaolin.finance.biz.service.MemberRegService;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户注册模块")
@RestController
@RequestMapping
@Slf4j
@RequiredArgsConstructor
public class RegController {
    final MemberRegService memberRegService;

    /**
     * 手机号注册
     *
     * @param form
     * @return
     */
    @PostMapping("/phoneReg")
    public ApiResponse<Long> phoneReg(@Validated @RequestBody PhoneRegisterForm form) {
        return ApiResponse.success(memberRegService.phoneReg(form));
    }
    
}
