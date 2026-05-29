package com.carddemo.golden.parser;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class CobolSignDecoder {

    private CobolSignDecoder() {
    }

    public static BigDecimal decode(String rawValue, int decimalPlaces) {
        if (rawValue == null || rawValue.isEmpty()) {
            return BigDecimal.ZERO.setScale(decimalPlaces, RoundingMode.UNNECESSARY);
        }

        String digits = rawValue.substring(0, rawValue.length() - 1);
        char lastChar = rawValue.charAt(rawValue.length() - 1);

        int lastDigit;
        boolean negative;

        if (lastChar >= '0' && lastChar <= '9') {
            lastDigit = lastChar - '0';
            negative = false;
        } else if (lastChar == '{') {
            lastDigit = 0;
            negative = false;
        } else if (lastChar >= 'A' && lastChar <= 'I') {
            lastDigit = lastChar - 'A' + 1;
            negative = false;
        } else if (lastChar == '}') {
            lastDigit = 0;
            negative = true;
        } else if (lastChar >= 'J' && lastChar <= 'R') {
            lastDigit = lastChar - 'J' + 1;
            negative = true;
        } else {
            throw new IllegalArgumentException(
                    "Invalid COBOL sign-encoded character: '" + lastChar + "' in value: " + rawValue);
        }

        String fullDigits = digits + lastDigit;
        BigDecimal value = new BigDecimal(fullDigits);

        if (decimalPlaces > 0) {
            value = value.movePointLeft(decimalPlaces);
        }

        if (negative) {
            value = value.negate();
        }

        return value.setScale(decimalPlaces, RoundingMode.UNNECESSARY);
    }
}
