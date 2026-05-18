package com.mani.loan.banking.system.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiError> handleDuplicate(
            DuplicateResourceException ex,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError(
                ex.getCode(),
                ex.getMessage(),
                HttpStatus.CONFLICT.value(),
                request.getRequestURI(),
                Instant.now(),
                MDC.get("traceId")
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        ApiError error = new ApiError(
                "DUPLICATE_RESOURCE",
                "Resource already exists",
                HttpStatus.CONFLICT.value(),
                request.getRequestURI(),
                Instant.now(),
                MDC.get("traceId")
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
}
