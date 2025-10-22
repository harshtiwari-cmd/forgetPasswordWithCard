package com.dukhan.forgot.infrastructure.common.exception;

public class RetryAfter24HoursException extends RuntimeException {
    public RetryAfter24HoursException(String message) {
        super(message);
    }
}


