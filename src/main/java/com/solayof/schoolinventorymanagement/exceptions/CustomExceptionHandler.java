package com.solayof.schoolinventorymanagement.exceptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.solayof.schoolinventorymanagement.dtos.ErrorDTO;


@RestControllerAdvice
public class CustomExceptionHandler {
    /**
     * Handles NoResourceFoundException.
     * This method returns a 404 Not Found response with a message indicating the URL was not found.
     * @param ex the NoResourceFoundException that was thrown
     * @return ResponseEntity with a message and HTTP status 404 Not Found
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<String> handleNoResourceFoundException(NoResourceFoundException ex) {
        return new ResponseEntity<String>(
                "URL not found: ", //+ ex.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    /**
     * Handles IllegalArgumentException.
     * This method returns a 400 Bad Request response with the exception message.
     * @param ex the IllegalArgumentException that was thrown
     * @return ResponseEntity with the exception message and HTTP status 400 Bad Request
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException ex) {
        return new ResponseEntity<String>(
                ex.getMessage(),
                HttpStatus.BAD_REQUEST
        );

    }

    /**
     * Handles MethodArgumentNotValidException.
     * This method captures validation errors in request bodies and returns a list of error details.
     * @param ex the MethodArgumentNotValidException that was thrown
     * @return ResponseEntity with a list of ErrorDTO objects and HTTP status 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<ErrorDTO>> handleFieldException(MethodArgumentNotValidException ex) {
        ErrorDTO errorlDto = null;
        List<ErrorDTO> errorDTOs =new ArrayList<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fe: fieldErrors) {
            errorlDto = new ErrorDTO();
            errorlDto.setCode(fe.getField());
            errorlDto.setMessage(fe.getDefaultMessage());
            errorDTOs.add(errorlDto);
        }

        return new ResponseEntity<List<ErrorDTO>>(errorDTOs, HttpStatus.BAD_REQUEST);
    }

    /**
     * Handles MethodArgumentTypeMismatchException.
     * This method captures type mismatch errors in request parameters and returns a detailed error message.
     * @param ex the MethodArgumentTypeMismatchException that was thrown
     * @return ResponseEntity with a map containing error details and HTTP status 400 Bad Request
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        Map<String, String> error = new HashMap<>();

        error.put("error", "Invalid input for parameter: " + ex.getName());
        Class<?> requiredType = ex.getRequiredType();
        error.put("expectedType", requiredType != null ? requiredType.getSimpleName() : "unknown");

        error.put("message", ex.getMessage());

        return new  ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // Return a map with error details
    }

    /**
     * Handles general exceptions that are not specifically caught by other handlers.
     * This method returns a generic error message with a 500 Internal Server Error status.
     * @param ex the exception that was thrown
     * @return ResponseEntity with a generic error message and HTTP status 500
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception ex) {
        return new ResponseEntity<String>(
                "An unexpected error occurred: " + ex.getMessage().substring(0, 40) + "...",
                HttpStatus.INTERNAL_SERVER_ERROR
        ); // Limiting the message length for readability and security
    }
}
