package com.solayof.schoolinventorymanagement.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CollectorNotFoundAdvice {
    @ExceptionHandler(CollectorNotFoundException.class)
    public String handleCollectorNotFound(CollectorNotFoundException ex) {
        return ex.getMessage();
    }
}
