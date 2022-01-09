package com.kesen.demo.messagegateway.redis;

/**
 * @author KESEN
 */

public enum Token {
    SUCCESS,
    FAILED;

    public boolean isSuccess() {
        return this.equals(SUCCESS);
    }

    public boolean isFailed() {
        return this.equals(FAILED);
    }
}
