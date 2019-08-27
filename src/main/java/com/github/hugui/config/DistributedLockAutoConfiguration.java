package com.github.hugui.config;

import com.github.hugui.lock.DistributedLock;
import com.github.hugui.lock.RedisDistributedLock;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * 配置类
 *
 * @author Mr.HuGui
 * @date 2019-08-26 18:19
 * @since 3.4.0
 */
@Configuration
@AutoConfigureAfter(RedisConfig.class)//在加载配置的类之后再加载当前类
public class DistributedLockAutoConfiguration {

    @Bean
    @ConditionalOnBean(RedisTemplate.class)
    public DistributedLock redisDistributedLock(RedisTemplate<String, Object> redisTemplate) {
        return new RedisDistributedLock(redisTemplate);
    }

}
