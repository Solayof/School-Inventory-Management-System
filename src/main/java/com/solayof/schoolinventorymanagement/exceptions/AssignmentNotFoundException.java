package com.solayof.schoolinventorymanagement.exceptions;

public class AssignmentNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AssignmentNotFoundException() {
        super("Assignment not found");
    }

    public AssignmentNotFoundException(String message) {
        super(message);
    }

    public AssignmentNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
