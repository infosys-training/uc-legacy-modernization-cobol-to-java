package com.cardemo.batch.cbexport.io;

import java.math.BigDecimal;

/**
 * Parses COBOL fixed-width fields from ASCII text lines.
 * Handles zoned decimal with trailing overpunch sign encoding.
 *
 * <p>Trailing overpunch: the last character encodes both the sign and the
 * final digit of a signed numeric field.
 * <pre>
 *   +0..+9  →  {ABCDEFGHI  (or 0..9 for unsigned)
 *   -0..-9  →  }JKLMNOPQR
 * </pre>
 */
public final class CobolFieldParser {

    private CobolFieldParser() {}

    /**
     * Extracts a substring from a fixed-width line, right-padding with
     * spaces if the line is shorter than {@code offset + length}.
     */
    public static String extractField(String line, int offset, int length) {
        int end = offset + length;
        if (offset >= line.length()) {
            return " ".repeat(length);
        }
        if (end > line.length()) {
            return line.substring(offset) + " ".repeat(end - line.length());
        }
        return line.substring(offset, end);
    }

    /**
     * Extracts and trims a PIC X field.
     */
    public static String extractString(String line, int offset, int length) {
        return extractField(line, offset, length).trim();
    }

    /**
     * Extracts an unsigned integer from a PIC 9 field.
     */
    public static int extractInt(String line, int offset, int length) {
        String raw = extractField(line, offset, length).trim();
        if (raw.isEmpty()) {
            return 0;
        }
        return Integer.parseInt(raw);
    }

    /**
     * Extracts a signed decimal from a PIC S9(m)V99 field with trailing
     * overpunch encoding.
     *
     * @param line         the fixed-width text line
     * @param offset       start position (0-based)
     * @param totalLength  total field width in characters (integer digits + 2 implied decimal digits)
     * @param scale        number of implied decimal places (V99 → 2)
     * @return parsed BigDecimal value
     */
    public static BigDecimal extractSignedDecimal(String line, int offset, int totalLength, int scale) {
        String raw = extractField(line, offset, totalLength);
        if (raw.isBlank()) {
            return BigDecimal.ZERO;
        }
        return parseZonedDecimal(raw, scale);
    }

    /**
     * Parses a zoned-decimal string with trailing overpunch sign.
     *
     * @param raw   the raw field text (e.g. "00000001940{")
     * @param scale implied decimal places
     * @return the numeric value
     */
    public static BigDecimal parseZonedDecimal(String raw, int scale) {
        if (raw == null || raw.isBlank()) {
            return BigDecimal.ZERO;
        }

        char last = raw.charAt(raw.length() - 1);
        String prefix = raw.substring(0, raw.length() - 1);
        int sign;
        int lastDigit;

        switch (last) {
            case '{' -> { sign = 1;  lastDigit = 0; }
            case 'A' -> { sign = 1;  lastDigit = 1; }
            case 'B' -> { sign = 1;  lastDigit = 2; }
            case 'C' -> { sign = 1;  lastDigit = 3; }
            case 'D' -> { sign = 1;  lastDigit = 4; }
            case 'E' -> { sign = 1;  lastDigit = 5; }
            case 'F' -> { sign = 1;  lastDigit = 6; }
            case 'G' -> { sign = 1;  lastDigit = 7; }
            case 'H' -> { sign = 1;  lastDigit = 8; }
            case 'I' -> { sign = 1;  lastDigit = 9; }
            case '}' -> { sign = -1; lastDigit = 0; }
            case 'J' -> { sign = -1; lastDigit = 1; }
            case 'K' -> { sign = -1; lastDigit = 2; }
            case 'L' -> { sign = -1; lastDigit = 3; }
            case 'M' -> { sign = -1; lastDigit = 4; }
            case 'N' -> { sign = -1; lastDigit = 5; }
            case 'O' -> { sign = -1; lastDigit = 6; }
            case 'P' -> { sign = -1; lastDigit = 7; }
            case 'Q' -> { sign = -1; lastDigit = 8; }
            case 'R' -> { sign = -1; lastDigit = 9; }
            default -> {
                // No overpunch — treat as unsigned numeric
                sign = 1;
                lastDigit = Character.getNumericValue(last);
                if (lastDigit < 0 || lastDigit > 9) {
                    return BigDecimal.ZERO;
                }
            }
        }

        String digits = prefix + lastDigit;
        BigDecimal value = new BigDecimal(digits).movePointLeft(scale);
        return sign < 0 ? value.negate() : value;
    }
}
