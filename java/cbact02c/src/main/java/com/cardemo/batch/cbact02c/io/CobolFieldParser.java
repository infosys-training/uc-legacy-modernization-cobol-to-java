package com.cardemo.batch.cbact02c.io;

import java.math.BigDecimal;

/**
 * Parses fixed-width fields from COBOL record layouts.
 *
 * <p>Supports:
 * <ul>
 *   <li>Alphanumeric fields — PIC X(n)</li>
 *   <li>Unsigned numeric fields — PIC 9(n)</li>
 *   <li>Zoned decimal with trailing overpunch sign — PIC S9(n)V9(m) DISPLAY</li>
 * </ul>
 */
public final class CobolFieldParser {

    private CobolFieldParser() {}

    /**
     * Extracts a substring from the given line at the specified offset and length,
     * right-trimmed of trailing spaces.
     */
    public static String parseAlphanumeric(String line, int offset, int length) {
        if (offset + length > line.length()) {
            throw new IllegalArgumentException(
                    "Field at offset %d length %d exceeds record length %d"
                            .formatted(offset, length, line.length()));
        }
        return line.substring(offset, offset + length).stripTrailing();
    }

    /**
     * Parses an unsigned numeric field (PIC 9(n)) and returns its long value.
     */
    public static long parseUnsignedNumeric(String line, int offset, int length) {
        String raw = line.substring(offset, offset + length).trim();
        if (raw.isEmpty()) {
            return 0L;
        }
        return Long.parseLong(raw);
    }

    /**
     * Parses an unsigned numeric field (PIC 9(n)) and returns its int value.
     */
    public static int parseUnsignedNumericInt(String line, int offset, int length) {
        return (int) parseUnsignedNumeric(line, offset, length);
    }

    /**
     * Parses a signed zoned-decimal field (PIC S9(n)V9(m) DISPLAY) using
     * trailing overpunch encoding.
     *
     * <p>Trailing overpunch mapping:
     * <ul>
     *   <li>{@code {} = +0, A-I = +1 to +9</li>
     *   <li>{@code }} = -0, J-R = -1 to -9</li>
     * </ul>
     *
     * @param line            the fixed-width record line
     * @param offset          the 0-based start position
     * @param length          the total field width including the sign character
     * @param impliedDecimals number of implied decimal places (V clause)
     * @return the parsed BigDecimal value
     */
    public static BigDecimal parseSignedZonedDecimal(String line, int offset, int length, int impliedDecimals) {
        String raw = line.substring(offset, offset + length);
        if (raw.isBlank()) {
            return BigDecimal.ZERO;
        }

        char lastChar = raw.charAt(raw.length() - 1);
        String digits = raw.substring(0, raw.length() - 1);
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
                if (Character.isDigit(lastChar)) {
                    lastDigit = lastChar - '0';
                    negative = false;
                } else {
                    throw new IllegalArgumentException("Invalid overpunch character: " + lastChar);
                }
            }
        }

        String fullDigits = digits + lastDigit;
        BigDecimal value = new BigDecimal(fullDigits);
        if (impliedDecimals > 0) {
            value = value.movePointLeft(impliedDecimals);
        }
        return negative ? value.negate() : value;
    }

    /**
     * Formats a digit for COBOL overpunch output (positive sign).
     */
    public static char toPositiveOverpunch(int digit) {
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
            default -> throw new IllegalArgumentException("Digit out of range: " + digit);
        };
    }

    /**
     * Formats a digit for COBOL overpunch output (negative sign).
     */
    public static char toNegativeOverpunch(int digit) {
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
            default -> throw new IllegalArgumentException("Digit out of range: " + digit);
        };
    }
}
