package com.puxinxiaolin.common.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;

/**
 * @Description: 支持 Ant 风格的路径模式匹配
 * @Author: YCcLin
 * @Date: 2025/9/18 22:06
 */
@Configuration
@ConditionalOnProperty(prefix = "sys", name = "enable-my-security", havingValue = "true")
public class PathMatcherConfig {

    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }

}
