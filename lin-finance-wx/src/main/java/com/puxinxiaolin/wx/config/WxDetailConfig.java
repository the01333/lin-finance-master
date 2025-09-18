package com.puxinxiaolin.wx.config;

import lombok.Data;

/**
 * @Description: 微信详情配置类
 * @Author: YCcLin
 * @Date: 2025/9/16 23:29
 */
@Data
public class WxDetailConfig {

    /**
     * appId
     */
    private String appId;

    /**
     * 密钥
     */
    private String secret;

    /**
     * 二维码过期时间, 默认 15 分钟
     */
    private Integer codeExpire = 900;

    /**
     * token
     */
    private String token;

    /**
     * 消息加密密钥
     */
    private String encodingAESKey;

}
