package com.carddemo.batch.util;

import java.math.BigDecimal;

/**
 * Formats BigDecimal values back to COBOL DISPLAY format with trailing overpunch sign.
 *
 * PIC S9(n)V9(m) → total display width = n + m characters, with trailing overpunch.
 */
public final class CobolDecimalFormatter {

    private static final char[] POSITIVE_OVERPUNCH = {'{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
    private static final char[] NEGATIVE_OVERPUNCH = {'}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R'};

    private CobolDecimalFormatter() {}

    /**
     * Format a BigDecimal to COBOL DISPLAY format with trailing overpunch.
     *
     * @param value the value to format
     * @param totalDigits total number of digits (integer + decimal)
     * @param scale number of decimal digits
     * @return formatted string of length totalDigits
     */
    public static String format(BigDecimal value, int totalDigits, int scale) {
        boolean negative = value.signum() < 0;
        BigDecimal absValue = value.abs();

        // Move decimal point right by scale to get integer representation
        BigDecimal shifted = absValue.movePointRight(scale);
        String digits = shifted.toBigInteger().toString();

        // Pad with leading zeros to totalDigits length
        while (digits.length() < totalDigits) {
            digits = "0" + digits;
        }

        // Truncate if too long (should not happen with valid data)
        if (digits.length() > totalDigits) {
            digits = digits.substring(digits.length() - totalDigits);
        }

        // Replace last digit with overpunch character
        int lastDigitValue = digits.charAt(digits.length() - 1) - '0';
        char overpunch = negative ? NEGATIVE_OVERPUNCH[lastDigitValue] : POSITIVE_OVERPUNCH[lastDigitValue];

        return digits.substring(0, digits.length() - 1) + overpunch;
    }
}
