package com.github.hugui.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

/**
 * redis配置类，使用jackson2JsonRedisSerializer序列化value
 */
@Configuration
@AutoConfigureAfter(RedisProperties.class)
@Slf4j
public class RedisConfig {

    @Resource
    private RedisProperties redisProperties;

    @Value("${spring.cloud.config.profile}")
    private String environment;

    @Bean
    public RedisConnectionFactory jedisConnectionFactory() {
        if (environment.contains("pro") || environment.contains("beta")) {
            log.info("加载redis集群配置");
            return getClusterJedisConnectionFactory();
        } else {
            log.info("加载redis单机配置");
            return getSingleJedisConnectionFactory();
        }
    }

    public JedisConnectionFactory getClusterJedisConnectionFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(Integer.parseInt(redisProperties.getPoolMaxActive()));
        poolConfig.setMaxIdle(Integer.parseInt(redisProperties.getPoolMaxIdle()));
        poolConfig.setMaxWaitMillis(Integer.parseInt(redisProperties.getPoolMaxWait()));
        poolConfig.setMinIdle(Integer.parseInt(redisProperties.getPoolMinIdle()));
        poolConfig.setTestOnBorrow(Boolean.parseBoolean(redisProperties.getTestOnBorrow()));
        JedisConnectionFactory cf = new JedisConnectionFactory(getRedisCluster(), poolConfig);

        cf.setDatabase(Integer.parseInt(redisProperties.getDatabase()));
        cf.setHostName(String.valueOf(redisProperties.getHost()));
        cf.setPort(Integer.parseInt(redisProperties.getPort()));
        cf.setTimeout(Integer.parseInt(redisProperties.getTimeout()));
        return cf;
    }

    @Bean
    public RedisClusterConfiguration getRedisCluster() {

        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration(redisProperties.getNodes());
        redisClusterConfiguration.setMaxRedirects(Integer.valueOf(redisProperties.getMaxAttempts()));
        return redisClusterConfiguration;

    }

    public JedisConnectionFactory getSingleJedisConnectionFactory() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();

        JedisConnectionFactory cf = new JedisConnectionFactory(poolConfig);
        cf.setDatabase(Integer.valueOf(redisProperties.getDatabase()));
        cf.setHostName(String.valueOf(redisProperties.getHost()));
        cf.setPort(Integer.valueOf(redisProperties.getPort()));
        return cf;
    }

    /**
     * redisTemplate 序列化使用的jdkSerializeable, 存储二进制字节码, 所以自定义序列化类
     *
     * @param redisConnectionFactory
     * @return
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        // 使用Jackson2JsonRedisSerialize 替换默认序列化
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        objectMapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        jackson2JsonRedisSerializer.setObjectMapper(objectMapper);

        // 设置value的序列化规则和 key的序列化规则
        redisTemplate.setValueSerializer(jackson2JsonRedisSerializer);
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.afterPropertiesSet();
        return redisTemplate;
    }

}
