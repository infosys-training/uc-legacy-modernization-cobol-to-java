package com.carddemo.batch.encoder;

import java.math.BigDecimal;

public class Comp3Encoder {

    public byte[] encode(BigDecimal value, int totalDigits) {
        if (totalDigits % 2 == 0) {
            throw new IllegalArgumentException("totalDigits must be odd, but was " + totalDigits);
        }
        boolean negative = value.signum() < 0;
        BigDecimal abs = value.abs();
        String digits = abs.movePointRight(2).toBigInteger().toString();

        while (digits.length() < totalDigits) {
            digits = "0" + digits;
        }

        int byteLen = (totalDigits + 2) / 2;
        byte[] result = new byte[byteLen];

        int digitIdx = 0;
        for (int i = 0; i < byteLen - 1; i++) {
            int high = digits.charAt(digitIdx++) - '0';
            int low = digits.charAt(digitIdx++) - '0';
            result[i] = (byte) ((high << 4) | low);
        }

        int lastDigit = digits.charAt(digitIdx) - '0';
        int sign = negative ? 0x0D : 0x0C;
        result[byteLen - 1] = (byte) ((lastDigit << 4) | sign);

        return result;
    }
}
