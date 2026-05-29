package com.carddemo.testharness.parser;

import java.math.BigDecimal;

/**
 * Parses COBOL COMP-3 (packed decimal / BCD) byte arrays into BigDecimal.
 *
 * COMP-3 stores each digit in a half-byte (nibble). The last nibble holds the sign:
 *   0x0C = positive, 0x0D = negative, 0x0F = unsigned.
 * A PIC S9(10)V99 COMP-3 field occupies ceil((12+1)/2) = 7 bytes.
 */
public class PackedDecimalParser {

    /**
     * Computes the storage size in bytes for a packed decimal field.
     * Formula: ceil((digits + 1) / 2)
     */
    public static int storageLength(int totalDigits) {
        return (totalDigits + 2) / 2;
    }

    /**
     * Decodes a COMP-3 packed decimal byte array to BigDecimal.
     * Uses all available digit nibbles (2*length - 1).
     *
     * @param data            raw bytes
     * @param impliedDecimals number of implied decimal places (e.g., 2 for V99)
     * @return parsed BigDecimal value
     */
    public static BigDecimal decode(byte[] data, int impliedDecimals) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Packed decimal data must not be null or empty");
        }
        int maxDigitNibbles = data.length * 2 - 1;
        return decode(data, maxDigitNibbles, impliedDecimals);
    }

    /**
     * Decodes a COMP-3 packed decimal byte array to BigDecimal with a known digit count.
     * When totalDigits is less than the available nibbles, leading nibbles are treated as padding.
     *
     * @param data            raw bytes
     * @param totalDigits     actual number of significant digits in the field
     * @param impliedDecimals number of implied decimal places (e.g., 2 for V99)
     * @return parsed BigDecimal value
     */
    public static BigDecimal decode(byte[] data, int totalDigits, int impliedDecimals) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Packed decimal data must not be null or empty");
        }

        int maxDigitNibbles = data.length * 2 - 1;
        int padNibbles = maxDigitNibbles - totalDigits;
        if (padNibbles < 0) {
            padNibbles = 0;
        }

        StringBuilder digits = new StringBuilder();
        int nibbleIndex = 0;

        for (int i = 0; i < data.length; i++) {
            int b = data[i] & 0xFF;
            int highNibble = (b >> 4) & 0x0F;
            int lowNibble = b & 0x0F;

            if (i < data.length - 1) {
                if (nibbleIndex >= padNibbles) {
                    digits.append(highNibble);
                }
                nibbleIndex++;
                if (nibbleIndex >= padNibbles) {
                    digits.append(lowNibble);
                }
                nibbleIndex++;
            } else {
                if (nibbleIndex >= padNibbles) {
                    digits.append(highNibble);
                }
                // low nibble is sign
            }
        }

        int lastByte = data[data.length - 1] & 0xFF;
        int signNibble = lastByte & 0x0F;
        boolean negative = (signNibble == 0x0D);

        String digitStr = digits.toString();
        if (digitStr.isEmpty()) {
            digitStr = "0";
        }

        BigDecimal result = new BigDecimal(digitStr);
        if (impliedDecimals > 0) {
            result = result.movePointLeft(impliedDecimals);
        }
        if (negative) {
            result = result.negate();
        }

        return result;
    }

    /**
     * Encodes a BigDecimal into COMP-3 packed decimal bytes.
     *
     * @param value           the value to encode
     * @param totalDigits     total number of digits (integer + decimal)
     * @param impliedDecimals number of implied decimal places
     * @param signed          true if the field is signed (PIC S...)
     * @return packed BCD bytes
     */
    public static byte[] encode(BigDecimal value, int totalDigits, int impliedDecimals, boolean signed) {
        BigDecimal scaled = value.movePointRight(impliedDecimals);
        boolean negative = scaled.signum() < 0;
        scaled = scaled.abs();

        String digitStr = scaled.toBigInteger().toString();
        while (digitStr.length() < totalDigits) {
            digitStr = "0" + digitStr;
        }

        int byteLen = (totalDigits + 2) / 2;
        byte[] result = new byte[byteLen];

        // total nibbles in output = byteLen * 2
        // digit nibbles = byteLen * 2 - 1
        int availableDigitNibbles = byteLen * 2 - 1;

        // pad the digit string to fill all available digit nibble positions
        while (digitStr.length() < availableDigitNibbles) {
            digitStr = "0" + digitStr;
        }

        int digitIndex = 0;
        for (int i = 0; i < byteLen; i++) {
            int high;
            int low;
            if (i < byteLen - 1) {
                high = digitStr.charAt(digitIndex++) - '0';
                low = digitStr.charAt(digitIndex++) - '0';
            } else {
                high = digitStr.charAt(digitIndex++) - '0';
                if (signed) {
                    low = negative ? 0x0D : 0x0C;
                } else {
                    low = 0x0F;
                }
            }
            result[i] = (byte) ((high << 4) | low);
        }

        return result;
    }
}
