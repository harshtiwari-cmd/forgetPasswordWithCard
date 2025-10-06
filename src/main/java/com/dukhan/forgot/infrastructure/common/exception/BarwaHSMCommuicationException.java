package com.dukhan.forgot.infrastructure.common.exception;

public class BarwaHSMCommuicationException extends RuntimeException {
    public BarwaHSMCommuicationException(String code, String message) {
        super(code + ":" + message);
    }
}
