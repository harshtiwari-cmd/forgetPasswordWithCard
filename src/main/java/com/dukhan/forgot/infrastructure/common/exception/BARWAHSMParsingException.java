package com.dukhan.forgot.infrastructure.common.exception;

public class BARWAHSMParsingException extends RuntimeException {
    public BARWAHSMParsingException(String code, String message) {
        super(code + ":" + message);
    }
}
