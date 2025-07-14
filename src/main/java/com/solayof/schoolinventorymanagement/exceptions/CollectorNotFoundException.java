package com.solayof.schoolinventorymanagement.exceptions;

public class CollectorNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public CollectorNotFoundException() {
        super("Collector not found");
    }

    public CollectorNotFoundException(String message) {
        super(message);
    }

    public CollectorNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
