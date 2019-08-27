package com.github.hugui.config;

import com.github.hugui.lock.DistributedLock;
import com.github.hugui.lock.RedisLock;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * Redis分布式锁 切面
 *
 * @author Mr.HuGui
 * @date 2019-08-26 17:52
 * @since 3.4.0
 */
@Order(value = 4)
@Aspect
@Configuration
@ConditionalOnClass(DistributedLock.class)
@AutoConfigureAfter(DistributedLockAutoConfiguration.class)
public class DistributedLockAspectConfiguration {
    private final Logger logger = LoggerFactory.getLogger(DistributedLockAspectConfiguration.class);

    private static final String UNDERLINE = "_";

    @Autowired
    private DistributedLock distributedLock;

    @Pointcut("@annotation(com.github.hugui.lock.RedisLock)")
    private void lockPoint() {

    }

    @Around("lockPoint()")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Method method = ((MethodSignature) pjp.getSignature()).getMethod();
        RedisLock redisLock = method.getAnnotation(RedisLock.class);
        String key = redisLock.key();

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String appId = request.getHeader("appId");
        if (StringUtils.isEmpty(key)) {
            key = request.getRequestURI().replace("/", UNDERLINE);
        }
        if (redisLock.lockMode().equals(RedisLock.LockMode.OWN)) {
            key = key + UNDERLINE + appId;
        }
        key = DistributedLock.LOCK_PREFIX + key;

        int retryTimes = redisLock.action().equals(RedisLock.LockFailAction.RETRY) ? redisLock.retryTimes() : 0;
        boolean lock = distributedLock.lock(key, redisLock.keepMills(), retryTimes, redisLock.sleepMills());
        if (!lock) {
            logger.debug("get lock failed : " + key);
            throw new DistributedLockException(redisLock.code(), redisLock.msg());
        }

        //得到锁-->执行方法-->释放锁
        logger.debug("get lock success : " + key);
        try {
            return pjp.proceed();
        } catch (Exception e) {
            logger.error("execute locked method occurred an exception", e);
        } finally {
            if (RedisLock.UnLockMode.AUTO.equals(redisLock.unlockMode())) {
                boolean releaseResult = distributedLock.releaseLock(key);
                logger.debug("release lock : " + key + (releaseResult ? " success" : " failed"));
            } else {
                logger.debug("don't release lock : " + key);
            }
        }
        return null;
    }
}
