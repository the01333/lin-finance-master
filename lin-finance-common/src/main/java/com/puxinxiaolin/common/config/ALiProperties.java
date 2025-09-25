package com.puxinxiaolin.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: 阿里云属性配置
 * @Author: YCcLin
 * @Date: 2025/9/25 10:27
 */
@Configuration
@ConfigurationProperties(prefix = "ali")
@Data
public class ALiProperties {

    private String accessKeyId;

    private String accessKeySecret;

    private ALiSmsConfig sms;

    /**
     * 阿里云短信属性配置
     */
    @Data
    public static class ALiSmsConfig {

        private Boolean enable;

        /**
         * 区域
         */
        private String region;

        /**
         * 端点
         */
        private String endpoint;

    }
}