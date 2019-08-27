package com.github.hugui.lock;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * redis锁的注解
 *
 * @author Mr.HuGui
 * @date 2019-08-26 17:52
 * @since 3.4.0
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RedisLock {

    /**
     * 锁的资源，Redis中的key
     */
    String key() default "";

    /**
     * 状态码
     */
    String code() default "1";

    /**
     * 提示信息
     */
    String msg() default "手速太快了，请稍微休息会儿！";

    /**
     * 持锁时间,单位毫秒
     */
    long keepMills() default 5000;

    /**
     * 当获取失败时候动作
     */
    LockFailAction action() default LockFailAction.GIVE_UP;

    /**
     * 重试的间隔时间,设置GIVE_UP忽略此项
     */
    long sleepMills() default 200;

    /**
     * 重试次数
     */
    int retryTimes() default 3;

    /**
     * 加锁方式，默认为限制用户请求某个接口的频率
     */
    LockMode lockMode() default LockMode.OWN;

    /**
     * 解锁方式
     */
    UnLockMode unlockMode() default UnLockMode.AUTO;

    /**
     * 失败时候动作枚举
     */
    enum LockFailAction {
        /**
         * 放弃
         */
        GIVE_UP,
        /**
         * 继续重试
         */
        RETRY;
    }

    /**
     * 加锁方式
     */
    enum LockMode {
        /**
         * 当前用户
         */
        OWN,
        /**
         * 所有用户
         */
        ALL;
    }

    /**
     * 解锁方式
     */
    enum UnLockMode {
        /**
         * 自动
         */
        AUTO,
        /**
         * 手动
         */
        MANUAL;
    }
}
