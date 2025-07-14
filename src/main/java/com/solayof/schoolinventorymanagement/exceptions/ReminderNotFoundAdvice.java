package com.solayof.schoolinventorymanagement.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ReminderNotFoundAdvice {
    @ExceptionHandler(ReminderNotFoundException.class)
    public String handleReminderNotFound(ReminderNotFoundException ex) {
        return ex.getMessage();
    }
}
