package com.puxinxiaolin.wx.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * @Description: 微信 access_token 返参封装实体
 * @Author: YCcLin
 * @Date: 2025/9/16 23:38
 */
@Data
public class AccessTokenResult {

    /**
     * 凭证
     */
    @JsonProperty(value = "access_token", required = true)
    private String accessToken;

    /**
     * 凭证失效时间
     */
    @JsonProperty(value = "expires_in", required = true)
    private Integer expiresIn;

    /**
     * 错误码
     */
    @JsonProperty(value = "errcode", required = true)
    private String errCode;

    /**
     * 错误信息
     */
    @JsonProperty(value = "errmsg", required = true)
    private String errMsg;
    
}
