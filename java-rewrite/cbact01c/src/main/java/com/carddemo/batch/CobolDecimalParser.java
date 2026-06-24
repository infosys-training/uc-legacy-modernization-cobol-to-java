package com.carddemo.batch;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Map;

/**
 * Parses COBOL DISPLAY-format signed numeric fields with trailing overpunch encoding.
 *
 * COBOL PIC S9(n)V99 is stored in DISPLAY format as n+2 bytes, where the last byte
 * uses an overpunch character to encode both the last digit and the sign:
 *
 *   Positive: { = 0, A = 1, B = 2, C = 3, D = 4, E = 5, F = 6, G = 7, H = 8, I = 9
 *   Negative: } = 0, J = 1, K = 2, L = 3, M = 4, N = 5, O = 6, P = 7, Q = 8, R = 9
 */
public final class CobolDecimalParser {

    private static final Map<Character, int[]> OVERPUNCH = Map.ofEntries(
            // Positive overpunch: char -> [digit, sign(+1)]
            Map.entry('{', new int[]{0, 1}),
            Map.entry('A', new int[]{1, 1}),
            Map.entry('B', new int[]{2, 1}),
            Map.entry('C', new int[]{3, 1}),
            Map.entry('D', new int[]{4, 1}),
            Map.entry('E', new int[]{5, 1}),
            Map.entry('F', new int[]{6, 1}),
            Map.entry('G', new int[]{7, 1}),
            Map.entry('H', new int[]{8, 1}),
            Map.entry('I', new int[]{9, 1}),
            // Negative overpunch: char -> [digit, sign(-1)]
            Map.entry('}', new int[]{0, -1}),
            Map.entry('J', new int[]{1, -1}),
            Map.entry('K', new int[]{2, -1}),
            Map.entry('L', new int[]{3, -1}),
            Map.entry('M', new int[]{4, -1}),
            Map.entry('N', new int[]{5, -1}),
            Map.entry('O', new int[]{6, -1}),
            Map.entry('P', new int[]{7, -1}),
            Map.entry('Q', new int[]{8, -1}),
            Map.entry('R', new int[]{9, -1})
    );

    private CobolDecimalParser() {}

    /**
     * Parse a COBOL PIC S9(n)V9(scale) DISPLAY-format field with trailing overpunch.
     *
     * @param raw   the raw string from the COBOL record (e.g., "00000001940{")
     * @param scale number of implied decimal places (digits after V)
     * @return the parsed BigDecimal value
     */
    public static BigDecimal parseSignedDecimal(String raw, int scale) {
        if (raw == null || raw.isEmpty()) {
            return BigDecimal.ZERO;
        }

        char lastChar = raw.charAt(raw.length() - 1);
        int[] decoded = OVERPUNCH.get(lastChar);

        if (decoded != null) {
            String digits = raw.substring(0, raw.length() - 1) + decoded[0];
            BigDecimal value = new BigDecimal(digits).movePointLeft(scale);
            return decoded[1] < 0 ? value.negate() : value;
        }

        // No overpunch — treat as unsigned numeric
        return new BigDecimal(raw).movePointLeft(scale);
    }

    /**
     * Format a BigDecimal as a COBOL PIC S9(n)V9(scale) DISPLAY-format string with
     * trailing overpunch encoding.
     *
     * @param value       the value to format
     * @param totalDigits total number of display digits (n + scale)
     * @param scale       number of implied decimal places
     * @return the overpunch-encoded string
     */
    public static String formatSignedDecimal(BigDecimal value, int totalDigits, int scale) {
        boolean negative = value.signum() < 0;
        BigDecimal absValue = value.abs().movePointRight(scale);
        String digits = absValue.toBigInteger().toString();

        // Pad to totalDigits
        while (digits.length() < totalDigits) {
            digits = "0" + digits;
        }

        // Apply overpunch to last digit
        int lastDigit = digits.charAt(digits.length() - 1) - '0';
        char overpunch;
        if (negative) {
            overpunch = "}JKLMNOPQR".charAt(lastDigit);
        } else {
            overpunch = "{ABCDEFGHI".charAt(lastDigit);
        }

        return digits.substring(0, digits.length() - 1) + overpunch;
    }
}
