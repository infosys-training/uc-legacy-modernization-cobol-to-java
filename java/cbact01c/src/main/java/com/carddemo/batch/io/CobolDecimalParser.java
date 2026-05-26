package com.carddemo.batch.io;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Parses COBOL zoned-decimal (DISPLAY) fields in ASCII representation.
 *
 * In COBOL PIC S9(n)V99, the sign is encoded in the last byte using
 * the "overpunch" convention. In ASCII (GnuCOBOL default):
 * <pre>
 *   '{' = +0   'A'..'I' = +1..+9
 *   '}' = -0   'J'..'R' = -1..-9
 * </pre>
 */
public final class CobolDecimalParser {

    private CobolDecimalParser() {}

    /**
     * Parses a COBOL zoned-decimal string with an implied decimal point.
     *
     * @param raw            the raw string from the file (e.g. "00000001940{")
     * @param decimalPlaces  number of implied decimal places (e.g. 2 for V99)
     * @return the parsed BigDecimal value
     */
    public static BigDecimal parseSignedDecimal(String raw, int decimalPlaces) {
        if (raw == null || raw.isEmpty()) {
            return BigDecimal.ZERO;
        }

        char lastChar = raw.charAt(raw.length() - 1);
        int lastDigit;
        boolean negative;

        if (lastChar >= '0' && lastChar <= '9') {
            lastDigit = lastChar - '0';
            negative = false;
        } else if (lastChar == '{') {
            lastDigit = 0;
            negative = false;
        } else if (lastChar == '}') {
            lastDigit = 0;
            negative = true;
        } else if (lastChar >= 'A' && lastChar <= 'I') {
            lastDigit = lastChar - 'A' + 1;
            negative = false;
        } else if (lastChar >= 'J' && lastChar <= 'R') {
            lastDigit = lastChar - 'J' + 1;
            negative = true;
        } else {
            throw new IllegalArgumentException(
                    "Invalid COBOL overpunch character: '" + lastChar + "' in \"" + raw + "\"");
        }

        String digits = raw.substring(0, raw.length() - 1) + lastDigit;
        BigDecimal value = new BigDecimal(digits)
                .movePointLeft(decimalPlaces);

        return negative ? value.negate() : value;
    }

    /**
     * Formats a BigDecimal back into COBOL zoned-decimal DISPLAY format with
     * ASCII overpunch sign encoding.
     *
     * @param value          the value to format
     * @param totalDigits    total number of digits including decimal (e.g. 12 for S9(10)V99)
     * @param decimalPlaces  number of implied decimal places
     * @return the formatted string
     */
    public static String formatSignedDecimal(BigDecimal value, int totalDigits, int decimalPlaces) {
        boolean negative = value.signum() < 0;
        BigDecimal absValue = value.abs();

        long unscaled = absValue.movePointRight(decimalPlaces)
                .setScale(0, java.math.RoundingMode.HALF_UP)
                .longValueExact();

        String digits = String.format("%0" + totalDigits + "d", unscaled);
        if (digits.length() > totalDigits) {
            throw new IllegalArgumentException("Value too large for field: " + value);
        }

        int lastDigit = digits.charAt(digits.length() - 1) - '0';
        char overpunch;
        if (negative) {
            overpunch = lastDigit == 0 ? '}' : (char) ('J' + lastDigit - 1);
        } else {
            overpunch = lastDigit == 0 ? '{' : (char) ('A' + lastDigit - 1);
        }

        return digits.substring(0, digits.length() - 1) + overpunch;
    }
}
