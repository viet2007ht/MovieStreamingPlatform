package com.vanvat.moviestream.exception;

/**
 * Thrown when user input or entity data fails validation rules
 * (e.g. empty title, invalid rating range, invalid year).
 */
public class ValidationException extends Exception {
    public ValidationException(String message) {
        super(message);
    }
}
