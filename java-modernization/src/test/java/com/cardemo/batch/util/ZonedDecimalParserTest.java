package com.cardemo.batch.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ZonedDecimalParserTest {

    @ParameterizedTest
    @CsvSource({
            "00000001940{, 2,  194.00",
            "00000020200{, 2, 2020.00",
            "00000010200{, 2, 1020.00",
            "00000000000{, 2,    0.00",
            "00000061300{, 2, 6130.00",
            "00000054480{, 2, 5448.00",
    })
    void parsePositiveZonedDecimal(String raw, int scale, String expected) {
        BigDecimal result = ZonedDecimalParser.parse(raw, scale);
        assertEquals(new BigDecimal(expected), result);
    }

    @ParameterizedTest
    @CsvSource({
            "0000000100}, 2,  -10.00",
            "0000000250J, 2,  -25.01",
            "00000001940R, 2, -194.09",
    })
    void parseNegativeZonedDecimal(String raw, int scale, String expected) {
        BigDecimal result = ZonedDecimalParser.parse(raw, scale);
        assertEquals(0, new BigDecimal(expected).compareTo(result),
                "Expected " + expected + " but got " + result);
    }

    @ParameterizedTest
    @CsvSource({
            "0000001234A, 2, 123.41",
            "0000001234B, 2, 123.42",
            "0000001234I, 2, 123.49",
    })
    void parsePositiveNonZeroLastDigit(String raw, int scale, String expected) {
        BigDecimal result = ZonedDecimalParser.parse(raw, scale);
        assertEquals(0, new BigDecimal(expected).compareTo(result));
    }

    @Test
    void parseUnsignedDigit() {
        BigDecimal result = ZonedDecimalParser.parse("000000012345", 2);
        assertEquals(0, new BigDecimal("123.45").compareTo(result));
    }

    @Test
    void parseNullReturnsZero() {
        assertEquals(BigDecimal.ZERO, ZonedDecimalParser.parse(null, 2));
    }

    @Test
    void parseEmptyReturnsZero() {
        assertEquals(BigDecimal.ZERO, ZonedDecimalParser.parse("", 2));
    }

    @Test
    void formatPositive() {
        String result = ZonedDecimalParser.format(new BigDecimal("194.00"), 12, 2);
        assertEquals("00000001940{", result);
    }

    @Test
    void formatNegative() {
        String result = ZonedDecimalParser.format(new BigDecimal("-1025.00"), 12, 2);
        assertEquals("00000010250}", result);
    }

    @Test
    void formatZero() {
        String result = ZonedDecimalParser.format(BigDecimal.ZERO, 12, 2);
        assertEquals("00000000000{", result);
    }

    @Test
    void roundTrip() {
        BigDecimal original = new BigDecimal("6130.00");
        String formatted = ZonedDecimalParser.format(original, 12, 2);
        BigDecimal parsed = ZonedDecimalParser.parse(formatted, 2);
        assertEquals(0, original.compareTo(parsed));
    }
}
