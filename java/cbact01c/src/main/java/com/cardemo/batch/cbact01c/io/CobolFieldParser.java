package com.cardemo.batch.cbact01c.io;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Parses COBOL zoned-decimal (DISPLAY) fields from ASCII text.
 * Handles trailing overpunch sign encoding used in EBCDIC-to-ASCII data.
 *
 * <p>Trailing overpunch mapping (EBCDIC convention in ASCII):
 * <pre>
 *   { = +0   A = +1   B = +2   C = +3   D = +4
 *   E = +5   F = +6   G = +7   H = +8   I = +9
 *   } = -0   J = -1   K = -2   L = -3   M = -4
 *   N = -5   O = -6   P = -7   Q = -8   R = -9
 * </pre>
 */
public final class CobolFieldParser {

    private CobolFieldParser() {}

    /**
     * Parses a COBOL PIC S9(n)Vd(d) DISPLAY field with trailing overpunch sign.
     *
     * @param raw            the raw string from the fixed-width record
     * @param impliedDecimal number of implied decimal places (e.g., 2 for V99)
     * @return the parsed BigDecimal value
     */
    public static BigDecimal parseSignedDecimal(String raw, int impliedDecimal) {
        if (raw == null || raw.isEmpty()) {
            return BigDecimal.ZERO;
        }

        char lastChar = raw.charAt(raw.length() - 1);
        String digits = raw.substring(0, raw.length() - 1);
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

        String fullDigits = digits + lastDigit;
        BigDecimal value = new BigDecimal(fullDigits)
                .movePointLeft(impliedDecimal);

        return negative ? value.negate() : value;
    }

    /**
     * Parses an unsigned COBOL PIC 9(n) field.
     */
    public static String parseUnsignedNumeric(String raw) {
        return raw.trim();
    }

    /**
     * Parses a COBOL PIC X(n) alphanumeric field, preserving trailing spaces.
     */
    public static String parseAlphanumeric(String raw) {
        return raw;
    }

    /**
     * Formats a BigDecimal as a COBOL PIC S9(n)V99 DISPLAY field with trailing overpunch.
     *
     * @param value          the value to format
     * @param totalDigits    total number of digits (integer + decimal)
     * @param decimalPlaces  number of decimal places
     * @return formatted string with trailing overpunch sign
     */
    public static String formatSignedDecimal(BigDecimal value, int totalDigits, int decimalPlaces) {
        boolean negative = value.signum() < 0;
        BigDecimal absValue = value.abs();

        long unscaled = absValue.movePointRight(decimalPlaces)
                .setScale(0, java.math.RoundingMode.HALF_UP)
                .longValueExact();

        String digitStr = String.format("%0" + totalDigits + "d", unscaled);
        if (digitStr.length() > totalDigits) {
            throw new ArithmeticException("Value " + value + " exceeds PIC S9("
                    + (totalDigits - decimalPlaces) + ")V" + "9".repeat(decimalPlaces));
        }

        String leadingDigits = digitStr.substring(0, digitStr.length() - 1);
        int lastDigit = digitStr.charAt(digitStr.length() - 1) - '0';
        char overpunch = encodeOverpunch(lastDigit, negative);

        return leadingDigits + overpunch;
    }

    /**
     * Formats a BigDecimal as a plain right-justified numeric string (no sign encoding).
     */
    public static String formatUnsignedNumeric(String value, int width) {
        return String.format("%" + width + "s", value).replace(' ', '0');
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
            default -> throw new IllegalArgumentException(
                    "Invalid overpunch character: '" + c + "'");
        };
    }

    private static char encodeOverpunch(int digit, boolean negative) {
        if (negative) {
            return switch (digit) {
                case 0 -> '}';
                case 1 -> 'J';
                case 2 -> 'K';
                case 3 -> 'L';
                case 4 -> 'M';
                case 5 -> 'N';
                case 6 -> 'O';
                case 7 -> 'P';
                case 8 -> 'Q';
                case 9 -> 'R';
                default -> throw new IllegalArgumentException("Invalid digit: " + digit);
            };
        } else {
            return switch (digit) {
                case 0 -> '{';
                case 1 -> 'A';
                case 2 -> 'B';
                case 3 -> 'C';
                case 4 -> 'D';
                case 5 -> 'E';
                case 6 -> 'F';
                case 7 -> 'G';
                case 8 -> 'H';
                case 9 -> 'I';
                default -> throw new IllegalArgumentException("Invalid digit: " + digit);
            };
        }
    }
}
