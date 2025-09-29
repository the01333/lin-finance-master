package com.puxinxiaolin.wx.service.impl;

import com.puxinxiaolin.wx.constant.WxApiConstant;
import com.puxinxiaolin.wx.dto.AccessTokenResult;
import com.puxinxiaolin.wx.dto.MpQrCodeCreateRequest;
import com.puxinxiaolin.wx.dto.MpQrCodeCreateResult;
import com.puxinxiaolin.wx.service.WxService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class WxServiceImpl implements WxService {
    final WebClient webClient;
    // 重试次数
    int retry = 3;

    /**
     * 获取公众号 token
     *
     * @param appid
     * @param secret
     * @return
     */
    @Override
    public AccessTokenResult getMpAccessToken(String appid, String secret) {
        String url = String.format(WxApiConstant.ACCESS_TOKEN_API, appid, secret);
        return webClient.get().uri(url)
                .retrieve()
                .bodyToMono(AccessTokenResult.class)
                .retry(retry)
                .block();
    }

    /**
     * 创建公众号二维码
     *
     * @param accessToken
     * @param request
     * @return
     */
    @Override
    public MpQrCodeCreateResult createMpQrcodeCreate(String accessToken, MpQrCodeCreateRequest request) {
        String url = String.format(WxApiConstant.MP_QRCODE_CREATE, accessToken);
        MpQrCodeCreateResult result = webClient.post().uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(request))
                .retrieve()
                .bodyToMono(MpQrCodeCreateResult.class)
                .retry(retry)
                .block();
        if (Objects.isNull(result) || StringUtils.isBlank(result.getTicket())) {
            return result;
        }

        // 详情见文档: https://developers.weixin.qq.com/doc/offiaccount/Account_Management/Generating_a_Parametric_QR_Code.html
        result.setQrCodeUrl("https://mp.weixin.qq.com/cgi-bin/showqrcode?ticket=" + result.getTicket());
        return result;
    }

}
