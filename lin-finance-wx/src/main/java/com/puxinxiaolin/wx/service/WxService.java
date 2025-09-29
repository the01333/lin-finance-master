package com.puxinxiaolin.wx.service;

import com.puxinxiaolin.wx.dto.AccessTokenResult;
import com.puxinxiaolin.wx.dto.MpQrCodeCreateRequest;
import com.puxinxiaolin.wx.dto.MpQrCodeCreateResult;

public interface WxService {

    /**
     * 获取公众号 token
     *
     * @param appid
     * @param secret
     * @return
     */
    AccessTokenResult getMpAccessToken(String appid, String secret);

    /**
     * 创建公众号二维码
     *
     * @param accessToken
     * @param request
     * @return
     */
    MpQrCodeCreateResult createMpQrcodeCreate(String accessToken, MpQrCodeCreateRequest request);

}
