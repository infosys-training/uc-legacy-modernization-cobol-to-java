package com.cardemo.batch.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SignedDecimalParserTest {

    @ParameterizedTest
    @CsvSource({
        "00000001940{, 194.00",
        "00000020200{, 2020.00",
        "00000010200{, 1020.00",
        "00000000000{, 0.00",
        "00000001580{, 158.00",
        "00000061300{, 6130.00",
        "00000054480{, 5448.00",
        "00000049090{, 4909.00",
        "00000005380{, 538.00"
    })
    void parse_positiveValues(String raw, String expected) {
        assertEquals(new BigDecimal(expected), SignedDecimalParser.parse(raw, 2));
    }

    @ParameterizedTest
    @CsvSource({
        "00000001940}, -194.00",
        "00000001025}, -102.50",
        "00000000100J, -10.01"
    })
    void parse_negativeValues(String raw, String expected) {
        assertEquals(new BigDecimal(expected), SignedDecimalParser.parse(raw, 2));
    }

    @ParameterizedTest
    @CsvSource({
        "00000001940A, 194.01",
        "00000001940I, 194.09",
        "00000001940B, 194.02"
    })
    void parse_positiveOverpunchDigits(String raw, String expected) {
        assertEquals(new BigDecimal(expected), SignedDecimalParser.parse(raw, 2));
    }

    @Test
    void parse_blankInput_returnsZero() {
        assertEquals(BigDecimal.ZERO, SignedDecimalParser.parse("", 2));
        assertEquals(BigDecimal.ZERO, SignedDecimalParser.parse("   ", 2));
        assertEquals(BigDecimal.ZERO, SignedDecimalParser.parse(null, 2));
    }

    @Test
    void format_positiveValue() {
        BigDecimal value = new BigDecimal("194.00");
        String formatted = SignedDecimalParser.format(value, 12, 2);
        assertEquals("00000001940{", formatted);
    }

    @Test
    void format_negativeValue() {
        BigDecimal value = new BigDecimal("-194.00");
        String formatted = SignedDecimalParser.format(value, 12, 2);
        assertEquals("00000001940}", formatted);
    }

    @Test
    void format_zero() {
        String formatted = SignedDecimalParser.format(BigDecimal.ZERO, 12, 2);
        assertEquals("00000000000{", formatted);
    }

    @Test
    void roundTrip() {
        BigDecimal original = new BigDecimal("6130.00");
        String formatted = SignedDecimalParser.format(original, 12, 2);
        BigDecimal parsed = SignedDecimalParser.parse(formatted, 2);
        assertEquals(0, original.compareTo(parsed));
    }

    @Test
    void invalidOverpunch_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> SignedDecimalParser.parse("0000000194Z", 2));
    }
}
