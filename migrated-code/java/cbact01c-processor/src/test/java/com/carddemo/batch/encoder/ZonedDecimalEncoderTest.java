package com.carddemo.batch.encoder;

import com.carddemo.parser.CobolSignDecoder;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.*;

class ZonedDecimalEncoderTest {

    private final ZonedDecimalEncoder encoder = new ZonedDecimalEncoder();
    private static final int DIGITS = 12;

    @Test
    void roundTrip_positiveValue() {
        BigDecimal original = new BigDecimal("194.00");
        String encoded = encoder.encode(original, DIGITS);
        BigDecimal decoded = CobolSignDecoder.decode(encoded, 2);
        assertThat(decoded).isEqualByComparingTo(original);
    }

    @Test
    void zeroEncodesWithTrailingOpenBrace() {
        String encoded = encoder.encode(new BigDecimal("0.00"), DIGITS);
        assertThat(encoded.charAt(encoded.length() - 1)).isEqualTo('{');
    }

    @Test
    void positive194_lastCharOpenBrace() {
        // 194.00 → digits 000000019400, last digit 0 → '{'
        String encoded = encoder.encode(new BigDecimal("194.00"), DIGITS);
        assertThat(encoded.charAt(encoded.length() - 1)).isEqualTo('{');
    }

    @Test
    void negative194_lastCharCloseBrace() {
        String encoded = encoder.encode(new BigDecimal("-194.00"), DIGITS);
        assertThat(encoded.charAt(encoded.length() - 1)).isEqualTo('}');
    }

    @Test
    void positiveDigits1Through9() {
        // 0.01 → last digit 1 → 'A', 0.02 → 'B', ..., 0.09 → 'I'
        char[] expected = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
        for (int i = 1; i <= 9; i++) {
            BigDecimal val = new BigDecimal("0.0" + i);
            String encoded = encoder.encode(val, DIGITS);
            assertThat(encoded.charAt(encoded.length() - 1))
                    .as("Last char for 0.0%d", i)
                    .isEqualTo(expected[i - 1]);
        }
    }

    @Test
    void negativeDigits1Through9() {
        // -0.01 → last digit 1 → 'J', -0.02 → 'K', ..., -0.09 → 'R'
        char[] expected = {'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R'};
        for (int i = 1; i <= 9; i++) {
            BigDecimal val = new BigDecimal("-0.0" + i);
            String encoded = encoder.encode(val, DIGITS);
            assertThat(encoded.charAt(encoded.length() - 1))
                    .as("Last char for -0.0%d", i)
                    .isEqualTo(expected[i - 1]);
        }
    }

    @Test
    void roundTrip_negativeValue() {
        BigDecimal original = new BigDecimal("-1025.00");
        String encoded = encoder.encode(original, DIGITS);
        BigDecimal decoded = CobolSignDecoder.decode(encoded, 2);
        assertThat(decoded).isEqualByComparingTo(original);
    }
}
