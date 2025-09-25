package com.puxinxiaolin.common.config;

import com.aliyun.auth.credentials.Credential;
import com.aliyun.auth.credentials.provider.StaticCredentialProvider;
import com.aliyun.sdk.service.dysmsapi20170525.AsyncClient;
import darabonba.core.client.ClientOverrideConfiguration;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description: aliyun 短信配置类
 * @Author: YCcLin
 * @Date: 2025/9/25 10:34
 */
@Configuration
@ConditionalOnProperty(prefix = "ali.sms", name = "enable", havingValue = "true")
@RequiredArgsConstructor
public class ALiSmsConfig {

    final ALiProperties aLiProperties;

    @Bean
    public AsyncClient asyncClient() {
        StaticCredentialProvider provider = StaticCredentialProvider.create(Credential.builder()
                .accessKeyId(aLiProperties.getAccessKeyId())
                .accessKeySecret(aLiProperties.getAccessKeySecret())
                .build());

        return AsyncClient.builder()
                .region(aLiProperties.getSms().getRegion())
                .credentialsProvider(provider)
                .overrideConfiguration(ClientOverrideConfiguration.create()
                        .setEndpointOverride(aLiProperties.getSms().getEndpoint())
                ).build();
    }

}
