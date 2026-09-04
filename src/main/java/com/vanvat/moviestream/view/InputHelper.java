package com.vanvat.moviestream.view;

import java.util.Scanner;

/**
 * Thin wrapper around Scanner that re-prompts on bad input instead of
 * crashing — this is the "Validate inputs" / "Handle exceptions" story
 * for the view layer specifically (business-rule validation lives in
 * util.Validator instead).
 */
public class InputHelper {

    private final Scanner scanner;

    public InputHelper(Scanner scanner) {
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public int readInt(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Integer.parseInt(raw);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    public double readDouble(String prompt) {
        while (true) {
            String raw = readLine(prompt);
            try {
                return Double.parseDouble(raw);
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }
}
