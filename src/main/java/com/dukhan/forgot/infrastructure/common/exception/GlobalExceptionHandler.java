package com.dukhan.forgot.infrastructure.common.exception;

import com.dukhan.forgot.infrastructure.common.AppConstant;
import com.dukhan.forgot.infrastructure.common.GenericResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BarwaHSMCommuicationException.class)
    public ResponseEntity<GenericResponse<Object>> handleHSMCommunicationException(BarwaHSMCommuicationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GenericResponse.error("156", ex.getMessage()));
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String errorMsg = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        return ResponseEntity.ok(GenericResponse.error(AppConstant.CARD_LENGTH_ERROR_CODE, errorMsg));
    }
    @ExceptionHandler(BARWAHSMEncryptionException.class)
    public ResponseEntity<GenericResponse<Object>> handleHSMEncryptionException(BARWAHSMEncryptionException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GenericResponse.error("157", ex.getMessage()));
    }

    @ExceptionHandler(BARWAHSMParsingException.class)
    public ResponseEntity<GenericResponse<Object>> handleHSMParsingException(BARWAHSMParsingException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GenericResponse.error("158", ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<GenericResponse<Object>> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(GenericResponse.error("9999", "Unexpected error: " + ex.getMessage()));
    }
}
