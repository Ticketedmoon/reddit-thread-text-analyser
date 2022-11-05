package com.skybreak.rcwa.application.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ResponseEntity<?> constraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        Map<String, Object> payload = Map.of(
            "status", HttpStatus.BAD_REQUEST.value(),
            "message", e.getMessage()
        );
        return ResponseEntity.badRequest().body(payload);
    }
}
