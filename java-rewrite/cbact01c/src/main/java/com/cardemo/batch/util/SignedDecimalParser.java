package com.cardemo.batch.util;

import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * Parses and formats COBOL signed-numeric DISPLAY fields that use
 * trailing sign-overpunch encoding.
 * <p>
 * Overpunch mapping (last character of the field):
 * <pre>
 *   Positive: { &rarr; 0, A &rarr; 1, B &rarr; 2, ... I &rarr; 9
 *   Negative: } &rarr; 0, J &rarr; 1, K &rarr; 2, ... R &rarr; 9
 * </pre>
 */
public final class SignedDecimalParser {

    private static final String POS_CHARS = "{ABCDEFGHI";
    private static final String NEG_CHARS = "}JKLMNOPQR";

    private SignedDecimalParser() {}

    /**
     * Parses a raw COBOL signed-numeric field into a {@link BigDecimal}.
     *
     * @param raw           field value from the data file
     * @param decimalPlaces implied decimal places (e.g.&nbsp;2 for {@code V99})
     */
    public static BigDecimal parse(String raw, int decimalPlaces) {
        if (raw == null || raw.isBlank()) {
            return BigDecimal.ZERO;
        }

        String trimmed = raw.strip();
        if (trimmed.isEmpty()) {
            return BigDecimal.ZERO;
        }

        char last = trimmed.charAt(trimmed.length() - 1);
        boolean negative;
        int lastDigit;

        if (last >= '0' && last <= '9') {
            negative = false;
            lastDigit = last - '0';
        } else {
            int posIdx = POS_CHARS.indexOf(last);
            if (posIdx >= 0) {
                negative = false;
                lastDigit = posIdx;
            } else {
                int negIdx = NEG_CHARS.indexOf(last);
                if (negIdx >= 0) {
                    negative = true;
                    lastDigit = negIdx;
                } else {
                    throw new IllegalArgumentException(
                            "Invalid overpunch character: '" + last + "' in field: " + raw);
                }
            }
        }

        String digits = trimmed.substring(0, trimmed.length() - 1) + lastDigit;
        BigDecimal value = new BigDecimal(new BigInteger(digits), decimalPlaces);
        return negative ? value.negate() : value;
    }

    /**
     * Formats a {@link BigDecimal} as a COBOL signed-overpunch string.
     *
     * @param value         the numeric value
     * @param totalDigits   total digit count (integer + decimal, e.g.&nbsp;12 for S9(10)V99)
     * @param decimalPlaces implied decimal places
     */
    public static String format(BigDecimal value, int totalDigits, int decimalPlaces) {
        boolean negative = value.signum() < 0;
        BigInteger unscaled = value.abs().movePointRight(decimalPlaces).toBigInteger();
        String digits = String.format("%0" + totalDigits + "d", unscaled);

        int lastDigit = digits.charAt(digits.length() - 1) - '0';
        char overpunch = negative ? NEG_CHARS.charAt(lastDigit) : POS_CHARS.charAt(lastDigit);
        return digits.substring(0, digits.length() - 1) + overpunch;
    }
}
