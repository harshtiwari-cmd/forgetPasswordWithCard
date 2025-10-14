package com.dukhan.forgot.infrastructure.common.exception;

public class MiddlewareException extends RuntimeException {
    private final String code;

    public MiddlewareException(String code, String message) {
        super(message);
        this.code = code;
    }

    public MiddlewareException(String message) {
        super(message);
        this.code = "GENERIC_ERROR";
    }

    public String getCode() {
        return code;
    }
}
