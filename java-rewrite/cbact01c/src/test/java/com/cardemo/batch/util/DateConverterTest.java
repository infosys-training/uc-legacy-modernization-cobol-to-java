package com.cardemo.batch.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class DateConverterTest {

    @ParameterizedTest
    @CsvSource({
        "2025-05-20, 20250520",
        "2024-08-11, 20240811",
        "2024-01-10, 20240110",
        "2014-11-20, 20141120",
        "2000-01-01, 20000101",
        "1999-12-31, 19991231"
    })
    void hyphenatedToCompact(String input, String expected) {
        assertEquals(expected, DateConverter.hyphenatedToCompact(input));
    }

    @ParameterizedTest
    @CsvSource({
        "20250520, 2025-05-20",
        "20240811, 2024-08-11",
        "20000101, 2000-01-01"
    })
    void compactToHyphenated(String input, String expected) {
        assertEquals(expected, DateConverter.compactToHyphenated(input));
    }

    @Test
    void roundTrip() {
        String original = "2025-05-20";
        assertEquals(original,
                DateConverter.compactToHyphenated(
                        DateConverter.hyphenatedToCompact(original)));
    }

    @ParameterizedTest
    @CsvSource({
        "2025-05-20, 2025",
        "2014-11-20, 2014",
        "1999-12-31, 1999"
    })
    void extractYear(String input, String expected) {
        assertEquals(expected, DateConverter.extractYear(input));
    }

    @Test
    void hyphenatedToCompact_nullInput_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> DateConverter.hyphenatedToCompact(null));
    }

    @Test
    void hyphenatedToCompact_shortInput_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> DateConverter.hyphenatedToCompact("2025"));
    }
}
