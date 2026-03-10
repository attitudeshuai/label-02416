package com.library.common;

/**
 * 业务异常
 * 用于所有可预期的业务规则校验失败场景
 * 由全局异常处理器捕获并返回 HTTP 400
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
