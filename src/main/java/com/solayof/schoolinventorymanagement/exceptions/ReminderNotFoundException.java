package com.solayof.schoolinventorymanagement.exceptions;

public class ReminderNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ReminderNotFoundException() {
        super("Reminder not found");
    }

    public ReminderNotFoundException(String message) {
        super(message);
    }

    public ReminderNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
    
}
