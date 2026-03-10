package com.library.common;

/**
 * 资源不存在异常
 * 由全局异常处理器捕获并返回 HTTP 404
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
