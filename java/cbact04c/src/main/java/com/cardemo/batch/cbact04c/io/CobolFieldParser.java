package com.cardemo.batch.cbact04c.io;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Parses COBOL fixed-width fields including zoned decimal with trailing overpunch sign.
 *
 * COBOL zoned decimal trailing overpunch encoding:
 *   Positive: { = +0, A = +1, B = +2, ..., I = +9
 *   Negative: } = -0, J = -1, K = -2, ..., R = -9
 */
public final class CobolFieldParser {

    private CobolFieldParser() {}

    /**
     * Extracts a substring from a fixed-width line.
     *
     * @param line the fixed-width record line
     * @param start 0-based start position
     * @param length number of characters
     * @return the extracted field, or empty string if out of range
     */
    public static String extractField(String line, int start, int length) {
        if (line == null || start >= line.length()) return "";
        int end = Math.min(start + length, line.length());
        return line.substring(start, end);
    }

    /**
     * Parses a COBOL unsigned numeric PIC 9(n) field.
     */
    public static long parseUnsignedNumeric(String field) {
        if (field == null || field.isBlank()) return 0;
        return Long.parseLong(field.trim());
    }

    /**
     * Parses a COBOL signed decimal field with trailing overpunch sign.
     * PIC S9(intDigits)V99 — trailing overpunch on the last character.
     *
     * @param field the raw COBOL field string
     * @param decimalPlaces number of implied decimal places (V99 = 2)
     * @return the parsed BigDecimal value
     */
    public static BigDecimal parseSignedDecimal(String field, int decimalPlaces) {
        if (field == null || field.isBlank()) return BigDecimal.ZERO;

        String trimmed = field.trim();
        if (trimmed.isEmpty()) return BigDecimal.ZERO;

        char lastChar = trimmed.charAt(trimmed.length() - 1);
        String prefix = trimmed.substring(0, trimmed.length() - 1);

        int lastDigit;
        boolean negative;

        if (lastChar >= '0' && lastChar <= '9') {
            lastDigit = lastChar - '0';
            negative = false;
        } else {
            int[] parsed = decodeOverpunch(lastChar);
            lastDigit = parsed[0];
            negative = parsed[1] != 0;
        }

        String digitStr = prefix + lastDigit;
        BigDecimal value = new BigDecimal(digitStr).movePointLeft(decimalPlaces);

        return negative ? value.negate() : value;
    }

    /**
     * Decodes a COBOL trailing overpunch character.
     *
     * @param c the overpunch character
     * @return int[2] where [0] = digit (0-9), [1] = 1 if negative, 0 if positive
     */
    public static int[] decodeOverpunch(char c) {
        return switch (c) {
            case '{' -> new int[]{0, 0};
            case 'A' -> new int[]{1, 0};
            case 'B' -> new int[]{2, 0};
            case 'C' -> new int[]{3, 0};
            case 'D' -> new int[]{4, 0};
            case 'E' -> new int[]{5, 0};
            case 'F' -> new int[]{6, 0};
            case 'G' -> new int[]{7, 0};
            case 'H' -> new int[]{8, 0};
            case 'I' -> new int[]{9, 0};
            case '}' -> new int[]{0, 1};
            case 'J' -> new int[]{1, 1};
            case 'K' -> new int[]{2, 1};
            case 'L' -> new int[]{3, 1};
            case 'M' -> new int[]{4, 1};
            case 'N' -> new int[]{5, 1};
            case 'O' -> new int[]{6, 1};
            case 'P' -> new int[]{7, 1};
            case 'Q' -> new int[]{8, 1};
            case 'R' -> new int[]{9, 1};
            default -> throw new IllegalArgumentException("Invalid overpunch character: " + c);
        };
    }

    /**
     * Encodes a digit (0-9) with sign into a trailing overpunch character.
     *
     * @param digit the digit value (0-9)
     * @param negative true if the value is negative
     * @return the overpunch character
     */
    public static char encodeOverpunch(int digit, boolean negative) {
        char[] positive = {'{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
        char[] negativeChars = {'}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R'};
        if (digit < 0 || digit > 9) {
            throw new IllegalArgumentException("Digit must be 0-9: " + digit);
        }
        return negative ? negativeChars[digit] : positive[digit];
    }
}
