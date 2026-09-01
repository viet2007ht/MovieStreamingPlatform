package com.vanvat.moviestream.exception;

/**
 * Wraps low-level I/O failures (file not found, permission denied, corrupt
 * line, etc.) into a single unchecked exception so callers up the stack
 * (service/controller/view) don't need to know about java.io internals.
 */
public class DataAccessException extends RuntimeException {
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataAccessException(String message) {
        super(message);
    }
}
