package com.vanvat.moviestream.exception;

/**
 * Thrown when a requested entity (movie, category, etc.) cannot be found
 * by the given id.
 */
public class NotFoundException extends Exception {
    public NotFoundException(String message) {
        super(message);
    }
}
