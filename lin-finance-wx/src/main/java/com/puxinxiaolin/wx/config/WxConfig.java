package com.puxinxiaolin.wx.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @Description: 微信配置类
 * @Author: YCcLin
 * @Date: 2025/9/16 23:29
 */
@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WxConfig {

    /**
     * 公众号配置
     */
    private WxDetailConfig mp;

    /**
     * 小程序配置
     */
    private WxDetailConfig miniApp;

}
