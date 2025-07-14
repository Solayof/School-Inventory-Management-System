package com.solayof.schoolinventorymanagement.exceptions;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class AssignmentNotFoundAdvice {
    @ExceptionHandler(AssignmentNotFoundException.class)
    public String handleAssignmentNotFound(AssignmentNotFoundException ex) {
        return ex.getMessage();
    }
}
