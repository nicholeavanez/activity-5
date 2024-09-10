package com.ecc.nichole.util;

import java.util.InputMismatchException;
import java.util.Scanner;
import org.apache.commons.lang3.StringUtils;

/**
 * Utility class for handling user input and validation.
 */
public class Utils {
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Prompts the user for a string input.
     * 
     * @param prompt the message to display to the user
     * @return the string entered by the user
     */
    public static String getStringInput(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }

    /**
     * Prompts the user for an integer input and ensures it is a positive number.
     * 
     * @param prompt the message to display to the user
     * @return the integer entered by the user
     */
    public static int getIntegerInput(String prompt) {
        int value = 0;
        boolean valid = false;

        while (!valid) {
            try {
                System.out.print(prompt);
                value = scanner.nextInt();
                scanner.nextLine(); // Clear the buffer
                if (value > 0) {
                    valid = true;
                } else {
                    System.out.println("Value must be greater than 0.");
                }
            } catch (InputMismatchException e) {
                scanner.nextLine(); // Clear invalid input
                System.err.println("Invalid input. Please enter a whole number.");
            } catch (Exception e) {
                System.err.println("Unexpected error: " + e.getMessage());
            }
        }

        return value;
    }

    /**
     * Prompts the user for an integer input and ensures it is within the valid range.
     * 
     * @param prompt the message to display to the user
     * @param maxValue the maximum allowed value
     * @return the integer entered by the user
     */
    public static int getValidIndex(String prompt, int maxValue) {
        int value = -1;
        boolean valid = false;

        while (!valid) {
            value = getIntegerInput(prompt);

            if (value > 0 && value <= maxValue) {
                valid = true;
            } else {
                System.out.println("Input value not allowed. It must be between 1 and " + maxValue + ".");
            }
        }

        return value;
    }

    /**
     * Prompts the user for a string input and ensures it meets the length constraints.
     * 
     * @param prompt the message to display to the user
     * @param minLength the minimum required length for the string
     * @param maxLength the maximum allowed length for the string
     * @return the validated string entered by the user
     */
    public static String getValidatedString(String prompt, int minLength, int maxLength) {
        String input = "";
        boolean valid = false;

        while (!valid) {
            input = getStringInput(prompt);

            if (StringUtils.isBlank(input) || input.length() < minLength) {
                System.out.println("String cannot be empty or shorter than " + minLength + " characters.");
            } else if (input.length() > maxLength) {
                System.out.println("String cannot be more than " + maxLength + " characters.");
            } else {
                valid = true;
            }
        }

        return input;
    }

    /**
     * Closes the scanner and terminates the application.
     * 
     * @return true if termination is successful
     */
    public static boolean terminate() {
        scanner.close();
        return true;
    }
}
