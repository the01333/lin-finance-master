package com.puxinxiaolin.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Data
@ConfigurationProperties(prefix = "security")
public class SecurityConfig {

    /**
     * 是否启用安全系统
     */
    private Boolean enable = false;

    /**
     * 获取用户信息
     * 1. 从 token 中获取
     * 2. 从网关传递的请求头 user 中获取
     */
    private String getUserType = "token";

    /**
     * token 过期时间（秒）
     */
    private Integer expire = 3600;

    /**
     * 白名单
     */
    private List<String> ignores;
    
}
