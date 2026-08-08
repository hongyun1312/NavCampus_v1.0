package com.hongyun.navcampus.common.exception;

import lombok.Getter;

/**
 * 业务异常。
 * 用于在 Service 层抛出可预期的业务错误，由全局异常处理器统一捕获并返回前端。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final int code;

    /**
     * 默认500状态码的业务异常
     *
     * @param message 异常信息
     */
    public BusinessException(String message) {
        this(500, message);
    }

    /**
     * 自定义状态码的业务异常
     *
     * @param code    状态码
     * @param message 异常信息
     */
    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }
}