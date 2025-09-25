package com.puxinxiaolin.common.service.impl;

import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.sdk.service.dysmsapi20170525.models.SendSmsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.puxinxiaolin.common.exception.BizException;
import com.puxinxiaolin.common.service.SmsCommonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
@Slf4j
@ConditionalOnBean(AsyncClient.class)
public class ALiSmsServiceImpl implements SmsCommonService {
    final AsyncClient asyncClient;
    final ObjectMapper objectMapper;
    
    @Override
    public void sendSms(String signName, String templateCode, String phoneNumbers, Map<String, String> templateParam) {
        try {
            SendSmsRequest request = SendSmsRequest.builder()
                    .signName(signName)
                    .templateCode(templateCode)
                    .phoneNumbers(phoneNumbers)
                    .templateParam(objectMapper.writeValueAsString(templateParam))
                    .build();
            CompletableFuture<SendSmsResponse> response = asyncClient.sendSms(request);
            SendSmsResponse resp = response.get();
            
            log.info("短信发送结果：{}", objectMapper.writeValueAsString(resp));
        } catch (Exception e) {
            throw new BizException("发送短信验证码失败", e);
        }
    }
    
}
