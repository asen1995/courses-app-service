package com.school.exception;

/**
 * Exception thrown when attempting to assign a second teacher to a course.
 * <p>
 * Handled by {@link GlobalExceptionHandler} to return HTTP 409 Conflict responses.
 */
public class DuplicateTeacherException extends RuntimeException {

    /**
     * Constructs the exception with a descriptive message.
     *
     * @param message the detail message
     */
    public DuplicateTeacherException(String message) {
        super(message);
    }
}
