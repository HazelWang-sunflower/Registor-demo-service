package com.koi.api.controller.exception;

import com.koi.api.entity.RestBean;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ValidationController {
    @ExceptionHandler(ValidationException.class)
    public RestBean<Void> validateException(ValidationException exception) {
        log.warn("Resolve [{}: {}]", exception.getClass().getName(), exception.getMessage());
        return RestBean.failure(400, "Request parameter is wrong!");
    }
}
