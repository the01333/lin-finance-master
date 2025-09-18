package com.puxinxiaolin.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description: swagger 配置属性
 * @Author: YCcLin
 * @Date: 2025/9/17 19:45
 */
@ConfigurationProperties(prefix = "swagger")
@Component
@Data
public class SwaggerProperties {

    /**
     * 是否开启 Swagger
     */
    private Boolean enable = false;

    /**
     * Swagger 应用名
     */
    private String name;

    /**
     * Swagger 描述信息
     */
    private String description;

    /**
     * Swagger 版本
     */
    private String version;

    /**
     * 接口前缀
     */
    private String pathMapping;

}
