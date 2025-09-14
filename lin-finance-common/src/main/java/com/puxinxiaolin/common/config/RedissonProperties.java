package com.puxinxiaolin.common.config;

import lombok.Data;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: redisson 属性配置
 * @Author: YCcLin
 * @Date: 2025/9/14 18:49
 */
@Data
@ConfigurationProperties(prefix = "redisson")
@ConditionalOnProperty("redisson.password")
public class RedissonProperties {

    /**
     * 超时时间
     */
    private int timeout = 3000;

    private String address;

    private String password;

    private int database = 0;

    /**
     * 连接池最大连接数
     */
    private int connectionPoolSize = 64;

    /**
     * 连接池最小空闲连接数
     */
    private int connectionMinimumIdleSize = 32;

    /**
     * 从节点连接池最大连接数
     */
    private int slaveConnectionPoolSize = 250;

    /**
     * 主节点连接池最大连接数
     */
    private int masterConnectionPoolSize = 250;

    /**
     * 哨兵集群地址
     */
    private String[] sentinelAddresses;

    /**
     * 主节点名称
     */
    private String masterName;

}
