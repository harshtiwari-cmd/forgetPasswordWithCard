package com.dukhan.forgot.infrastructure.common.exception;

import com.dukhan.forgot.domain.model.dto.Status;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BarwaHSMCommuicationException.class)
    public ResponseEntity<Status> handleHSMCommunicationException(BarwaHSMCommuicationException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Status("156", ex.getMessage()));
    }

    @ExceptionHandler(BARWAHSMEncryptionException.class)
    public ResponseEntity<Status> handleHSMEncryptionException(BARWAHSMEncryptionException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Status("157", ex.getMessage()));
    }

    @ExceptionHandler(BARWAHSMParsingException.class)
    public ResponseEntity<Status> handleHSMParsingException(BARWAHSMParsingException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Status("158", ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Status> handleValidationException(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new Status("400", message));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Status> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new Status("9999", "Unexpected error: " + ex.getMessage()));
    }
}
