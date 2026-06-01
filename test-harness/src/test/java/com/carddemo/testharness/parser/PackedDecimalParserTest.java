package com.carddemo.testharness.parser;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PackedDecimalParserTest {

    @Test
    void shouldDecodePositivePackedDecimal() {
        // +2525.00 as PIC S9(10)V99 COMP-3 = 7 bytes, 12 digits
        // Digits: 000000252500, sign C (positive)
        // Layout: [pad=0] 0 0 0 0 0 0 2 5 2 5 0 0 [C]
        // Bytes:  0x00 0x00 0x00 0x02 0x52 0x50 0x0C
        byte[] packed = new byte[] {
            0x00, 0x00, 0x00, 0x02, 0x52, 0x50, 0x0C
        };

        BigDecimal result = PackedDecimalParser.decode(packed, 12, 2);

        assertThat(result).isEqualByComparingTo(new BigDecimal("2525.00"));
    }

    @Test
    void shouldDecodeNegativePackedDecimal() {
        // -2525.00 as PIC S9(10)V99 COMP-3, 12 digits
        byte[] packed = new byte[] {
            0x00, 0x00, 0x00, 0x02, 0x52, 0x50, 0x0D
        };

        BigDecimal result = PackedDecimalParser.decode(packed, 12, 2);

        assertThat(result).isEqualByComparingTo(new BigDecimal("-2525.00"));
    }

    @Test
    void shouldDecodeZeroPackedDecimal() {
        // 0.00 as PIC S9(10)V99 COMP-3, 12 digits
        byte[] packed = new byte[] {
            0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0C
        };

        BigDecimal result = PackedDecimalParser.decode(packed, 12, 2);

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void shouldDecodeUnsignedPackedDecimal() {
        // 1234.56 unsigned (sign nibble 0xF), PIC 9(10)V99, 12 digits
        // Digits: 000000123456
        // Layout: [pad=0] 0 0 0 0 0 1 2 3 4 5 6 [F]
        // Bytes:  0x00 0x00 0x00 0x01 0x23 0x45 0x6F
        byte[] packed = new byte[] {
            0x00, 0x00, 0x00, 0x01, 0x23, 0x45, 0x6F
        };

        BigDecimal result = PackedDecimalParser.decode(packed, 12, 2);

        assertThat(result).isEqualByComparingTo(new BigDecimal("1234.56"));
    }

    @Test
    void shouldEncodePositivePackedDecimal() {
        BigDecimal value = new BigDecimal("2525.00");
        byte[] encoded = PackedDecimalParser.encode(value, 12, 2, true);

        BigDecimal decoded = PackedDecimalParser.decode(encoded, 12, 2);
        assertThat(decoded).isEqualByComparingTo(value);
    }

    @Test
    void shouldEncodeNegativePackedDecimal() {
        BigDecimal value = new BigDecimal("-2525.00");
        byte[] encoded = PackedDecimalParser.encode(value, 12, 2, true);

        BigDecimal decoded = PackedDecimalParser.decode(encoded, 12, 2);
        assertThat(decoded).isEqualByComparingTo(value);
    }

    @Test
    void shouldRejectNullInput() {
        assertThatThrownBy(() -> PackedDecimalParser.decode(null, 2))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldRejectEmptyInput() {
        assertThatThrownBy(() -> PackedDecimalParser.decode(new byte[0], 2))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDecodeSmallValue() {
        // 0.01 as PIC S9(3)V99 COMP-3 = 3 bytes, 5 digits (odd, no padding)
        // Digits: 00001, sign C
        // Layout: 0 0 0 0 1 [C]
        // Bytes: 0x00 0x00 0x1C
        byte[] packed = new byte[] { 0x00, 0x00, 0x1C };

        BigDecimal result = PackedDecimalParser.decode(packed, 5, 2);

        assertThat(result).isEqualByComparingTo(new BigDecimal("0.01"));
    }

    @Test
    void shouldDecodeWithoutTotalDigitsParam() {
        // 5-digit field with odd digit count = no padding
        // PIC S9(3)V99: 5 digits, 3 bytes, 5 digit nibbles (no pad)
        // Value: 123.45 → digits 12345, sign C
        // Bytes: 0x12 0x34 0x5C
        byte[] packed = new byte[] { 0x12, 0x34, 0x5C };

        BigDecimal result = PackedDecimalParser.decode(packed, 2);

        assertThat(result).isEqualByComparingTo(new BigDecimal("123.45"));
    }
}
