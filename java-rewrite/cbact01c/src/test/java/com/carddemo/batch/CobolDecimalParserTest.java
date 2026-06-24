package com.carddemo.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CobolDecimalParserTest {

    @Test
    void parsePositiveZero() {
        // '{' = positive 0 → "00000001940{" = +194.00 (not 0!)
        BigDecimal result = CobolDecimalParser.parseSignedDecimal("00000001940{", 2);
        assertEquals(new BigDecimal("194.00"), result);
    }

    @Test
    void parsePositiveZeroAllZeros() {
        // All zeros with positive overpunch
        BigDecimal result = CobolDecimalParser.parseSignedDecimal("00000000000{", 2);
        assertEquals(0, result.compareTo(BigDecimal.ZERO));
    }

    @Test
    void parseNegativeWithTrailingZero() {
        // '}' = negative 0 → "00000001940}" = -194.00
        BigDecimal result = CobolDecimalParser.parseSignedDecimal("00000001940}", 2);
        assertEquals(new BigDecimal("-194.00"), result);
    }

    @Test
    void parsePositiveNonZeroLastDigit() {
        // 'A' = positive 1 → "0000000194A" would be 19.41
        BigDecimal result = CobolDecimalParser.parseSignedDecimal("00000001941A", 2);
        // digits = "000000019411", value = 194.11
        assertEquals(new BigDecimal("194.11"), result);
    }

    @ParameterizedTest
    @CsvSource({
            "00000020200{, 2, 2020.00",
            "00000010200{, 2, 1020.00",
            "00000001580{, 2, 158.00",
            "00000001470{, 2, 147.00",
            "00000000400{, 2, 40.00",
            "00000003450{, 2, 345.00",
    })
    void parseSampleAccountBalances(String raw, int scale, String expected) {
        BigDecimal result = CobolDecimalParser.parseSignedDecimal(raw, scale);
        assertEquals(new BigDecimal(expected), result);
    }

    @ParameterizedTest
    @CsvSource({
            "'{', 0, 1",    // positive zero
            "'}', 0, -1",   // negative zero (sign is negative, digit 0)
            "'A', 1, 1",
            "'I', 9, 1",
            "'J', 1, -1",
            "'R', 9, -1",
    })
    void overpunchCharacterDecoding(char overpunch, int expectedDigit, int expectedSign) {
        String raw = "0000000000" + overpunch;
        BigDecimal result = CobolDecimalParser.parseSignedDecimal(raw, 0);
        assertEquals(expectedDigit, result.abs().intValue());
        if (expectedSign < 0 && expectedDigit != 0) {
            assertTrue(result.signum() < 0, "Expected negative sign for " + overpunch);
        }
    }

    @Test
    void formatPositive() {
        String result = CobolDecimalParser.formatSignedDecimal(new BigDecimal("194.00"), 12, 2);
        assertEquals("00000001940{", result);
    }

    @Test
    void formatNegative() {
        String result = CobolDecimalParser.formatSignedDecimal(new BigDecimal("-194.00"), 12, 2);
        assertEquals("00000001940}", result);
    }

    @Test
    void formatRoundTrip() {
        String original = "00000020200{";
        BigDecimal parsed = CobolDecimalParser.parseSignedDecimal(original, 2);
        String formatted = CobolDecimalParser.formatSignedDecimal(parsed, 12, 2);
        assertEquals(original, formatted);
    }

    @Test
    void formatNegativeNonZeroLastDigit() {
        String result = CobolDecimalParser.formatSignedDecimal(new BigDecimal("-2500.00"), 12, 2);
        assertEquals("00000025000}", result);
    }

    @Test
    void parseNullOrEmpty() {
        assertEquals(BigDecimal.ZERO, CobolDecimalParser.parseSignedDecimal(null, 2));
        assertEquals(BigDecimal.ZERO, CobolDecimalParser.parseSignedDecimal("", 2));
    }
}
