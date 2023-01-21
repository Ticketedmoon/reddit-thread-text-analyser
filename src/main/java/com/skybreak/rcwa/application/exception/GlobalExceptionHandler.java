package com.skybreak.rcwa.application.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = ConstraintViolationException.class)
    public ProblemDetail constraintViolationException(ConstraintViolationException e) {
        log.error(e.getMessage(), e);
        return ProblemDetail.forStatusAndDetail(BAD_REQUEST, e.getMessage());
    }
}
