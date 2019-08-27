package com.github.hugui.config;

/**
 * 分布式锁 异常类
 */
public class DistributedLockException extends RuntimeException {
    private String code = "0";

    private Object data;

    public DistributedLockException() {
        code = "1";
    }

    public DistributedLockException(String message) {
        super(message);
        code = "1";
    }

    public DistributedLockException(String code, String message) {
        super(message);
        this.code = code;
    }

    public DistributedLockException(String code, String message, Object data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public DistributedLockException(String message, Throwable cause) {
        super(message, cause);
    }

    public DistributedLockException(Throwable cause) {
        super(cause);
    }

    public DistributedLockException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}