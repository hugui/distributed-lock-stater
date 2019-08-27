package com.github.hugui.lock;

/**
 * 分布式锁，顶级接口
 * 扩展只需要实现此接口即可
 *
 * @author Mr.HuGui
 * @date 2019-08-26 17:52
 * @since 3.4.0
 */
public interface DistributedLock {

    long TIMEOUT_MILLIS = 30000;

    int RETRY_TIMES = Integer.MAX_VALUE;

    long SLEEP_MILLIS = 500;

    String LOCK_PREFIX = "LOCK";

    /**
     * 加锁
     *
     * @return 是否成功
     */
    boolean lock(String key);

    boolean lock(String key, int retryTimes);

    boolean lock(String key, int retryTimes, long sleepMillis);

    boolean lock(String key, long expire);

    boolean lock(String key, long expire, int retryTimes);

    boolean lock(String key, long expire, int retryTimes, long sleepMillis);

    /**
     * 释放锁
     *
     * @param key 键值
     * @return 是否成功
     */
    boolean releaseLock(String key);
}
