package com.puxinxiaolin.wx.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.puxinxiaolin.common.exception.ParameterException;
import com.puxinxiaolin.wx.aes.AesException;
import com.puxinxiaolin.wx.config.WxConfig;
import com.puxinxiaolin.wx.dto.MpBaseEventRequest;
import com.puxinxiaolin.wx.dto.MpCommonRequest;
import com.puxinxiaolin.wx.dto.MpSubscribeEventRequest;
import com.puxinxiaolin.wx.dto.MpTextEventRequest;
import com.puxinxiaolin.wx.service.WxMpEventService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class WxMpEventServiceImpl implements WxMpEventService {
    final WxConfig wxConfig;
    final ApplicationContext applicationContext;

    /**
     * 接收公众号事件
     *
     * @param mpCommonRequest
     * @param request
     * @return
     */
    @Override
    public String receiveMpEvent(MpCommonRequest mpCommonRequest, HttpServletRequest request) throws AesException, IOException {
        checkSignature(mpCommonRequest.getSignature(), wxConfig.getMp().getToken(),
                mpCommonRequest.getTimestamp(), mpCommonRequest.getNonce(), null);

        String contentType = request.getHeader("content-type");
        // 如果是 get 请求直接返回 wx 服务器传进来的 echostr
        if (StringUtils.isBlank(contentType)) {
            log.info("WxMpEventServiceImpl.receiveMpEvent.content-type is null.");
            return mpCommonRequest.getEchostr();
        }

        log.info("WxMpEventServiceImpl.receiveMpEvent.content-type:{}", contentType);
        log.info("WxMpEventServiceImpl.receiveMpEvent.mpCommonRequest:{}", mpCommonRequest);

        XmlMapper xmlMapper = new XmlMapper();
        Object obj = xmlMapper.readValue(request.getInputStream(), Object.class);
        ObjectMapper objectMapper = new ObjectMapper();
        log.info("WxMpEventServiceImpl.receiveMpEvent.obj:{}", obj);

        MpBaseEventRequest mpBaseEventRequest = objectMapper.convertValue(obj, MpBaseEventRequest.class);
        if ("text".equals(mpBaseEventRequest.getMsgType())) {
            MpTextEventRequest mpTextEventRequest = objectMapper.convertValue(obj, MpTextEventRequest.class);
            log.info("推送消息:{}", mpTextEventRequest);

            // Spring 的发布订阅模式: 根据传入的消息类型，后续再其他地方监听并处理（参数要一致） 
            applicationContext.publishEvent(mpTextEventRequest);
        } else if ("event".equals(mpBaseEventRequest.getMsgType())) {
            MpSubscribeEventRequest mpSubscribeEventRequest = objectMapper.convertValue(obj, MpSubscribeEventRequest.class);
            log.info("推送消息:{}", mpSubscribeEventRequest);

            // Spring 的发布订阅模式: 根据传入的消息类型，后续再其他地方监听并处理（参数要一致） 
            applicationContext.publishEvent(mpSubscribeEventRequest);
        }
        
        log.info("WxMpEventServiceImpl.receiveMpEvent.mpBaseEventRequest pushes event finished!");
        return mpCommonRequest.getEchostr() == null ? "success" : mpCommonRequest.getEchostr();
    }

    /**
     * 用SHA1算法生成安全签名并校验
     *
     * @param signature
     * @param token
     * @param timestamp
     * @param nonce
     * @param encrypt
     */
    @Override
    public void checkSignature(String signature, String token, String timestamp, String nonce, String encrypt) throws AesException {
        try {
            if (StringUtils.isBlank(signature) || StringUtils.isBlank(token)
                    || StringUtils.isBlank(timestamp) || StringUtils.isBlank(nonce)) {
                throw new ParameterException("签名参数非法");
            }

            String[] array;
            if (StringUtils.isBlank(encrypt)) {
                array = new String[]{token, timestamp, nonce};
            } else {
                array = new String[]{token, timestamp, nonce, encrypt};
            }

            StringBuffer sb = new StringBuffer();
            Arrays.sort(array);
            for (int i = 0; i < array.length; i++) {
                sb.append(array[i]);
            }
            String str = sb.toString();

            // SHA1签名生成
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(str.getBytes());
            byte[] digest = md.digest();

            StringBuffer hexstr = new StringBuffer();
            String shaHex;
            for (int i = 0; i < digest.length; i++) {
                shaHex = Integer.toHexString(digest[i] & 0xFF);
                if (shaHex.length() < 2) {
                    hexstr.append(0);
                }
                hexstr.append(shaHex);
            }

            log.info("WxMpEventServiceImpl.checkSignature.wx_signature:{}, my_signature:{}", signature, hexstr);
            if (!Objects.equals(signature, hexstr.toString())) {
                throw new AesException(AesException.ValidateSignatureError);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new AesException(AesException.ComputeSignatureError);
        }
    }
}
