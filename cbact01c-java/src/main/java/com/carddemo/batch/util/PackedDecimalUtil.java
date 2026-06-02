package com.carddemo.batch.util;

import java.math.BigDecimal;

/**
 * Encodes/decodes COBOL COMP-3 (packed decimal) format.
 *
 * COMP-3 stores two digits per byte, with the last nibble as the sign:
 *   0x0C = positive, 0x0D = negative, 0x0F = unsigned
 *
 * PIC S9(10)V99 COMP-3 = 12 digits + 1 sign nibble = 13 nibbles = 7 bytes.
 */
public final class PackedDecimalUtil {

    private PackedDecimalUtil() {}

    /**
     * Encode a BigDecimal to COMP-3 packed decimal bytes.
     *
     * @param value the value to encode
     * @param totalDigits total number of digits in the PIC clause (e.g., 12 for S9(10)V99)
     * @param scale number of implied decimal places
     * @return byte array in COMP-3 format
     */
    public static byte[] encode(BigDecimal value, int totalDigits, int scale) {
        boolean negative = value.signum() < 0;
        BigDecimal absValue = value.abs().movePointRight(scale);
        String digits = absValue.toBigInteger().toString();

        // Pad to totalDigits
        while (digits.length() < totalDigits) {
            digits = "0" + digits;
        }

        // COMP-3: (totalDigits + 1) nibbles, packed into bytes
        // Total nibbles = totalDigits + 1 (for sign)
        int totalNibbles = totalDigits + 1;
        int byteLength = (totalNibbles + 1) / 2;
        byte[] result = new byte[byteLength];

        // Pack digits left-to-right, sign in last nibble.
        // Total byte slots = byteLength * 2 nibble positions.
        // Sign occupies the very last nibble slot; digits fill the slots before it.
        // Any leftover slot at the front is a padding zero.
        int totalSlots = byteLength * 2;
        int nibbleIndex = totalSlots - totalDigits - 1; // start position for first digit

        for (int i = 0; i < digits.length(); i++) {
            int digitValue = digits.charAt(i) - '0';
            int bytePos = (nibbleIndex + i) / 2;
            if ((nibbleIndex + i) % 2 == 0) {
                result[bytePos] |= (byte) (digitValue << 4);
            } else {
                result[bytePos] |= (byte) digitValue;
            }
        }

        // Set sign nibble (last nibble)
        int signBytePos = byteLength - 1;
        byte signNibble = negative ? (byte) 0x0D : (byte) 0x0C;
        result[signBytePos] = (byte) ((result[signBytePos] & 0xF0) | signNibble);

        return result;
    }

    /**
     * Decode COMP-3 packed decimal bytes to BigDecimal.
     *
     * @param bytes the packed decimal bytes
     * @param scale number of implied decimal places
     * @return the decoded BigDecimal value
     */
    public static BigDecimal decode(byte[] bytes, int scale) {
        StringBuilder digits = new StringBuilder();
        boolean negative = false;

        for (int i = 0; i < bytes.length; i++) {
            int highNibble = (bytes[i] >> 4) & 0x0F;
            int lowNibble = bytes[i] & 0x0F;

            if (i == bytes.length - 1) {
                // Last byte: high nibble is last digit, low nibble is sign
                digits.append(highNibble);
                negative = (lowNibble == 0x0D);
            } else {
                digits.append(highNibble);
                digits.append(lowNibble);
            }
        }

        BigDecimal value = new BigDecimal(digits.toString()).movePointLeft(scale);
        return negative ? value.negate() : value;
    }

    /**
     * Get the byte length for a COMP-3 field given total digits.
     */
    public static int byteLength(int totalDigits) {
        return (totalDigits + 1 + 1) / 2; // +1 for sign nibble, +1 for rounding up
    }
}
