package com.vanvat.moviestream.util;

import com.vanvat.moviestream.exception.ValidationException;

import java.time.Year;

/**
 * Centralized input validation rules. Keeping this in one class means
 * every controller/service enforces the same rules and the report can
 * point to one place for "Validate inputs".
 */
public final class Validator {

    private Validator() {
    }

    public static void requireNonBlank(String value, String fieldName) throws ValidationException {
        if (value == null || value.isBlank()) {
            throw new ValidationException(fieldName + " must not be empty.");
        }
    }

    public static void requireRange(double value, double min, double max, String fieldName)
            throws ValidationException {
        if (value < min || value > max) {
            throw new ValidationException(
                    String.format("%s must be between %.1f and %.1f.", fieldName, min, max));
        }
    }

    public static void requireValidYear(int year) throws ValidationException {
        int currentYear = Year.now().getValue();
        if (year < 1888 || year > currentYear + 1) { // 1888: first known motion picture
            throw new ValidationException(
                    "Release year must be between 1888 and " + (currentYear + 1) + ".");
        }
    }

    public static void requirePositive(int value, String fieldName) throws ValidationException {
        if (value <= 0) {
            throw new ValidationException(fieldName + " must be a positive number.");
        }
    }
}
