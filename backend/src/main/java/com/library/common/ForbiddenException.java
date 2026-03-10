package com.library.common;

/**
 * 权限不足异常
 * 由全局异常处理器捕获并返回 HTTP 403
 */
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String message) {
        super(message);
    }
}
