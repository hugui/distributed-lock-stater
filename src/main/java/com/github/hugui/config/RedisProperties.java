package com.github.hugui.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "redis")
@Data
public class RedisProperties {
    private String database;// 数据库索引（默认为0）
    private String host;// 服务器地址
    private String port;// 服务器端口
    private String poolMaxActive;// 连接池最大连接数，默认值为8，使用负值表示没有限制
    private String poolMaxWait;// 连接池最大阻塞等待时间，单位毫秒，默认值为-1，表示永不超时
    private String poolMaxIdle;// 连接池中的最大空闲连接，默认值为8
    private String poolMinIdle;// 连接池中的最小空闲连接
    private String timeout;// 连接超时时间（毫秒）
    private String testOnBorrow;//是否在从池中取出连接前进行检验,如果检验失败,则从池中去除连接并尝试取出另一个

    private List<String> nodes;//集群所用 节点以逗号分隔 ip:port,ip:port
    private String maxAttempts; //重试次数


}