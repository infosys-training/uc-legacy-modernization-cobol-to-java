package com.carddemo.batch.encoder;

import java.math.BigDecimal;

public class ZonedDecimalEncoder {

    private static final char[] POSITIVE_SIGNS = {'{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
    private static final char[] NEGATIVE_SIGNS = {'}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R'};

    public String encode(BigDecimal value, int totalDigits) {
        boolean negative = value.signum() < 0;
        String digits = value.abs().movePointRight(2).toBigInteger().toString();

        while (digits.length() < totalDigits) {
            digits = "0" + digits;
        }

        int lastDigit = digits.charAt(digits.length() - 1) - '0';
        char signChar = negative ? NEGATIVE_SIGNS[lastDigit] : POSITIVE_SIGNS[lastDigit];

        return digits.substring(0, digits.length() - 1) + signChar;
    }
}
