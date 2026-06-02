package com.carddemo.batch.util;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Parses COBOL DISPLAY numeric fields with trailing overpunch sign encoding.
 *
 * COBOL PIC S9(n)V9(m) in DISPLAY format uses trailing overpunch:
 *   Positive: { = 0, A = 1, B = 2, C = 3, D = 4, E = 5, F = 6, G = 7, H = 8, I = 9
 *   Negative: } = 0, J = 1, K = 2, L = 3, M = 4, N = 5, O = 6, P = 7, Q = 8, R = 9
 *
 * The implied decimal point (V) means the last 'scale' digits are fractional.
 */
public final class CobolDecimalParser {

    private CobolDecimalParser() {}

    /**
     * Parse a COBOL signed display numeric field with trailing overpunch.
     *
     * @param field the raw string from the fixed-length record
     * @param scale number of implied decimal places (digits after V)
     * @return the parsed BigDecimal value
     */
    public static BigDecimal parse(String field, int scale) {
        if (field == null || field.isEmpty()) {
            return BigDecimal.ZERO;
        }

        String trimmed = field.trim();
        if (trimmed.isEmpty()) {
            return BigDecimal.ZERO;
        }

        char lastChar = field.charAt(field.length() - 1);
        String digits = field.substring(0, field.length() - 1);
        int lastDigit;
        boolean negative;

        switch (lastChar) {
            case '{' -> { lastDigit = 0; negative = false; }
            case 'A' -> { lastDigit = 1; negative = false; }
            case 'B' -> { lastDigit = 2; negative = false; }
            case 'C' -> { lastDigit = 3; negative = false; }
            case 'D' -> { lastDigit = 4; negative = false; }
            case 'E' -> { lastDigit = 5; negative = false; }
            case 'F' -> { lastDigit = 6; negative = false; }
            case 'G' -> { lastDigit = 7; negative = false; }
            case 'H' -> { lastDigit = 8; negative = false; }
            case 'I' -> { lastDigit = 9; negative = false; }
            case '}' -> { lastDigit = 0; negative = true; }
            case 'J' -> { lastDigit = 1; negative = true; }
            case 'K' -> { lastDigit = 2; negative = true; }
            case 'L' -> { lastDigit = 3; negative = true; }
            case 'M' -> { lastDigit = 4; negative = true; }
            case 'N' -> { lastDigit = 5; negative = true; }
            case 'O' -> { lastDigit = 6; negative = true; }
            case 'P' -> { lastDigit = 7; negative = true; }
            case 'Q' -> { lastDigit = 8; negative = true; }
            case 'R' -> { lastDigit = 9; negative = true; }
            default -> {
                // If last char is a digit (unsigned field), treat as positive
                if (Character.isDigit(lastChar)) {
                    BigDecimal value = new BigDecimal(field);
                    return value.movePointLeft(scale);
                }
                throw new IllegalArgumentException(
                        "Invalid overpunch character: '" + lastChar + "' in field: " + field);
            }
        }

        String fullDigits = digits + lastDigit;
        BigDecimal value = new BigDecimal(fullDigits).movePointLeft(scale);
        return negative ? value.negate() : value;
    }
}
