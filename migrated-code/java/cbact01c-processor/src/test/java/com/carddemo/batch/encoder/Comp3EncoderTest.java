package com.carddemo.batch.encoder;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class Comp3EncoderTest {

    private final Comp3Encoder encoder = new Comp3Encoder();

    // COMP-3 for PIC S9(10)V99 has 12 data digits. The encoder requires an odd
    // totalDigits so a leading pad nibble is added, giving 13 digit slots packed
    // into 7 bytes (6 paired-digit bytes + 1 lastDigit|sign byte).
    private static final int TOTAL_DIGITS = 13;

    @Test
    void encode2525_00() {
        // digits "0000000252500" → packed [0|0][0|0][0|0][0|2][5|2][5|0][0|C]
        byte[] result = encoder.encode(new BigDecimal("2525.00"), TOTAL_DIGITS);
        assertThat(result).hasSize(7);
        assertThat(result).isEqualTo(new byte[]{
                0x00, 0x00, 0x00, 0x02, 0x52, 0x50, 0x0C
        });
    }

    @Test
    void encodeZero() {
        byte[] result = encoder.encode(new BigDecimal("0.00"), TOTAL_DIGITS);
        assertThat(result).hasSize(7);
        assertThat(result).isEqualTo(new byte[]{
                0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x0C
        });
    }

    @Test
    void encodeNegative1025_signNibbleD() {
        byte[] result = encoder.encode(new BigDecimal("-1025.00"), TOTAL_DIGITS);
        assertThat(result).hasSize(7);
        assertThat(result[6] & 0x0F).isEqualTo(0x0D);
    }

    @Test
    void encodeNegative2500() {
        // digits "0000000250000" → packed [0|0][0|0][0|0][0|2][5|0][0|0][0|D]
        byte[] result = encoder.encode(new BigDecimal("-2500.00"), TOTAL_DIGITS);
        assertThat(result).hasSize(7);
        assertThat(result[6] & 0x0F).isEqualTo(0x0D);
        assertThat(result).isEqualTo(new byte[]{
                0x00, 0x00, 0x00, 0x02, 0x50, 0x00, 0x0D
        });
    }

    @Test
    void encodePositive1005() {
        // digits "0000000100500" → packed [0|0][0|0][0|0][0|1][0|0][5|0][0|C]
        byte[] result = encoder.encode(new BigDecimal("1005.00"), TOTAL_DIGITS);
        assertThat(result).hasSize(7);
        assertThat(result).isEqualTo(new byte[]{
                0x00, 0x00, 0x00, 0x01, 0x00, 0x50, 0x0C
        });
    }

    @Test
    void encodePositive1525() {
        // digits "0000000152500" → packed [0|0][0|0][0|0][0|1][5|2][5|0][0|C]
        byte[] result = encoder.encode(new BigDecimal("1525.00"), TOTAL_DIGITS);
        assertThat(result).hasSize(7);
        assertThat(result).isEqualTo(new byte[]{
                0x00, 0x00, 0x00, 0x01, 0x52, 0x50, 0x0C
        });
    }

    @Test
    void encode_evenTotalDigits_throwsIllegalArgument() {
        assertThatThrownBy(() -> encoder.encode(new BigDecimal("2525.00"), 12))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("totalDigits must be odd");
    }
}
