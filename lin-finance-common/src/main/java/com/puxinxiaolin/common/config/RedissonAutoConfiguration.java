package com.puxinxiaolin.common.config;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @Description: redisson 自动配置类
 * @Author: YCcLin
 * @Date: 2025/9/14 18:50
 */
@Configuration
@ConditionalOnClass(Config.class)
// 启用使用 @ConfigurationProperties 的类, 注册为 Spring 中的 Bean
@EnableConfigurationProperties(RedissonProperties.class)
public class RedissonAutoConfiguration {

    @Resource
    private RedissonProperties redissonProperties;

    /**
     * 哨兵模式自动装配
     *
     * @return
     */
    @ConditionalOnProperty("redisson.master-name")
    @Bean
    public RedissonClient redissonSentinel() {
        Config config = new Config();
        SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                .addSentinelAddress(redissonProperties.getSentinelAddresses())
                .setMasterName(redissonProperties.getMasterName())
                .setTimeout(redissonProperties.getTimeout())
                .setMasterConnectionPoolSize(redissonProperties.getMasterConnectionPoolSize())
                .setSlaveConnectionPoolSize(redissonProperties.getSlaveConnectionPoolSize());

        if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
            sentinelServersConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }

    /**
     * 单机模式自动装配
     *
     * @return
     */
    @ConditionalOnProperty("redisson.address")
    @Bean
    public RedissonClient redissonSingle() {
        Config config = new Config();
        config.setCodec(new JsonJacksonCodec());
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(redissonProperties.getAddress())
                .setTimeout(redissonProperties.getTimeout())
                .setConnectionPoolSize(redissonProperties.getConnectionPoolSize())
                .setConnectionMinimumIdleSize(redissonProperties.getConnectionMinimumIdleSize());

        if (StringUtils.isNotBlank(redissonProperties.getPassword())) {
            singleServerConfig.setPassword(redissonProperties.getPassword());
        }
        return Redisson.create(config);
    }

}
