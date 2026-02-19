package com.school.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Global exception handler for REST controllers.
 * <p>
 * Translates application exceptions into appropriate HTTP error responses.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles resource not found exceptions and returns HTTP 404.
     *
     * @param ex the exception
     * @return error response with the exception message
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Handles validation exceptions and returns HTTP 400 with field-level error details.
     *
     * @param ex the validation exception
     * @return error response mapping field names to error messages
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(
            MethodArgumentNotValidException ex) {
        var errors = new java.util.HashMap<String, String>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errors.put(e.getField(), e.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    /**
     * Handles duplicate teacher exceptions and returns HTTP 409.
     *
     * @param ex the exception
     * @return error response with the exception message
     */
    @ExceptionHandler(DuplicateTeacherException.class)
    public ResponseEntity<Map<String, String>> handleDuplicateTeacher(
            DuplicateTeacherException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("error", ex.getMessage()));
    }

    /**
     * Catches all unhandled exceptions and returns HTTP 500.
     * <p>
     * Acts as a safety net for unexpected errors not covered by specific handlers.
     *
     * @param ex the exception
     * @return error response with a generic message
     */
    @ExceptionHandler(Throwable.class)
    public ResponseEntity<Map<String, String>> handleUncaught(Throwable ex) {
        log.error("Unexpected error occurred", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "An unexpected error occurred"));
    }
}
