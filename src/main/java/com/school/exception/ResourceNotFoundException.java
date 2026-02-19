package com.school.exception;

/**
 * Exception thrown when a requested resource is not found.
 * <p>
 * Handled by {@link GlobalExceptionHandler} to return HTTP 404 responses.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
