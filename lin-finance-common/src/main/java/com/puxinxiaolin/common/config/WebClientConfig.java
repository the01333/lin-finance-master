package com.puxinxiaolin.common.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

/**
 * @Description: WebClient 配置类（微信模块调用需要使用）
 * @Author: YCcLin
 * @Date: 2025/9/16 23:51
 */
@Configuration
@Slf4j
public class WebClientConfig {

    @Bean
    public WebClient webClient() {
        log.info("WebClientConfig init start ...");

        HttpClient httpClient = HttpClient.create();
        WebClient webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();

        log.info("WebClientConfig init end");
        return webClient;
    }

}
