package com.cardemo.batch.util;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Parses COBOL zoned-decimal (DISPLAY) fields stored in ASCII.
 *
 * <p>In ASCII COBOL the sign is overpunched in the last byte:
 * <pre>
 *   Positive 0-9 : { A B C D E F G H I
 *   Negative 0-9 : } J K L M N O P Q R
 *   Unsigned      : last byte is a plain digit '0'-'9'
 * </pre>
 */
public final class ZonedDecimalParser {

    private ZonedDecimalParser() {}

    /**
     * Parses an ASCII zoned-decimal string into a {@link BigDecimal}.
     *
     * @param raw           the raw fixed-width character data
     * @param impliedScale  number of implied decimal places (V99 = 2)
     * @return parsed value
     */
    public static BigDecimal parse(String raw, int impliedScale) {
        if (raw == null || raw.isEmpty()) {
            return BigDecimal.ZERO;
        }

        char[] chars = raw.toCharArray();
        char lastChar = chars[chars.length - 1];

        int lastDigit;
        int sign;

        if (lastChar >= '0' && lastChar <= '9') {
            lastDigit = lastChar - '0';
            sign = 1;
        } else {
            int[] decoded = decodeSignedChar(lastChar);
            sign = decoded[0];
            lastDigit = decoded[1];
        }

        StringBuilder sb = new StringBuilder(chars.length);
        for (int i = 0; i < chars.length - 1; i++) {
            sb.append(chars[i]);
        }
        sb.append((char) ('0' + lastDigit));

        BigDecimal value = new BigDecimal(sb.toString());
        if (impliedScale > 0) {
            value = value.movePointLeft(impliedScale);
        }
        return sign < 0 ? value.negate() : value;
    }

    private static int[] decodeSignedChar(char c) {
        return switch (c) {
            case '{' -> new int[]{1, 0};
            case 'A' -> new int[]{1, 1};
            case 'B' -> new int[]{1, 2};
            case 'C' -> new int[]{1, 3};
            case 'D' -> new int[]{1, 4};
            case 'E' -> new int[]{1, 5};
            case 'F' -> new int[]{1, 6};
            case 'G' -> new int[]{1, 7};
            case 'H' -> new int[]{1, 8};
            case 'I' -> new int[]{1, 9};
            case '}' -> new int[]{-1, 0};
            case 'J' -> new int[]{-1, 1};
            case 'K' -> new int[]{-1, 2};
            case 'L' -> new int[]{-1, 3};
            case 'M' -> new int[]{-1, 4};
            case 'N' -> new int[]{-1, 5};
            case 'O' -> new int[]{-1, 6};
            case 'P' -> new int[]{-1, 7};
            case 'Q' -> new int[]{-1, 8};
            case 'R' -> new int[]{-1, 9};
            default -> throw new IllegalArgumentException(
                    "Invalid zoned-decimal sign character: '" + c + "'");
        };
    }

    /**
     * Formats a {@link BigDecimal} as a zoned-decimal string in ASCII COBOL
     * DISPLAY format with an overpunched sign.
     *
     * @param value        the value to format
     * @param totalDigits  total number of digits (integer + decimal combined)
     * @param impliedScale number of implied decimal places
     * @return zoned-decimal string
     */
    public static String format(BigDecimal value, int totalDigits, int impliedScale) {
        boolean negative = value.signum() < 0;
        BigDecimal abs = value.abs().movePointRight(impliedScale);
        long unscaled = abs.setScale(0, java.math.RoundingMode.HALF_UP).longValueExact();

        String digits = String.format("%0" + totalDigits + "d", unscaled);
        if (digits.length() > totalDigits) {
            throw new ArithmeticException(
                    "Value " + value + " exceeds capacity of PIC S9("
                            + (totalDigits - impliedScale) + ")V" + "9".repeat(impliedScale));
        }

        char lastDigit = digits.charAt(digits.length() - 1);
        int d = lastDigit - '0';

        char signChar;
        if (negative) {
            signChar = (d == 0) ? '}' : (char) ('J' + d - 1);
        } else {
            signChar = (d == 0) ? '{' : (char) ('A' + d - 1);
        }

        return digits.substring(0, digits.length() - 1) + signChar;
    }
}
