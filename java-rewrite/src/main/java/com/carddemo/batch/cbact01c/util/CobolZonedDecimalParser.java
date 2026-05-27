package com.carddemo.batch.cbact01c.util;

import java.math.BigDecimal;

/**
 * Parses COBOL zoned-decimal (DISPLAY format) fields with sign overpunch
 * on the trailing digit into Java BigDecimal values.
 *
 * COBOL PIC S9(n)V99 DISPLAY stores the sign in the last byte using
 * EBCDIC overpunch encoding. In the ASCII test data files the convention is:
 * <pre>
 *   Positive: { = +0, A = +1, B = +2, ... I = +9
 *   Negative: } = -0, J = -1, K = -2, ... R = -9
 * </pre>
 */
public final class CobolZonedDecimalParser {

    private CobolZonedDecimalParser() {
    }

    /**
     * Parses a COBOL zoned-decimal string with trailing sign overpunch.
     *
     * @param raw            the raw field value from the data file
     * @param integerDigits  number of integer digits (before implied decimal)
     * @param decimalDigits  number of decimal digits (after implied V)
     * @return the parsed BigDecimal value
     */
    public static BigDecimal parse(String raw, int integerDigits, int decimalDigits) {
        if (raw == null || raw.isEmpty()) {
            return BigDecimal.ZERO;
        }

        int totalDigits = integerDigits + decimalDigits;
        if (raw.length() != totalDigits) {
            throw new IllegalArgumentException(
                    "Expected %d characters for PIC S9(%d)V9(%d), got %d: '%s'"
                            .formatted(totalDigits, integerDigits, decimalDigits, raw.length(), raw));
        }

        char lastChar = raw.charAt(raw.length() - 1);
        int lastDigit;
        boolean negative;

        if (lastChar >= '0' && lastChar <= '9') {
            lastDigit = lastChar - '0';
            negative = false;
        } else {
            int[] decoded = decodeOverpunch(lastChar);
            lastDigit = decoded[0];
            negative = decoded[1] != 0;
        }

        String digits = raw.substring(0, raw.length() - 1) + lastDigit;
        StringBuilder sb = new StringBuilder();
        if (negative) {
            sb.append('-');
        }
        sb.append(digits, 0, integerDigits);
        if (decimalDigits > 0) {
            sb.append('.');
            sb.append(digits, integerDigits, totalDigits);
        }

        return new BigDecimal(sb.toString());
    }

    private static int[] decodeOverpunch(char c) {
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
}
