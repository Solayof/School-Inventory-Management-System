package com.solayof.schoolinventorymanagement.exceptions;

public class CategoryNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    public CategoryNotFoundException() {
        super("Category not found");
    }
   public CategoryNotFoundException(String message) {
        super(message);
    }
    
    public CategoryNotFoundException(String message, Throwable cause) {
        super(message, cause);
    } 
}
