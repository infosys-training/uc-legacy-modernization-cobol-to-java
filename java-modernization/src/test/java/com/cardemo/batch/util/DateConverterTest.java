package com.cardemo.batch.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

class DateConverterTest {

    @ParameterizedTest
    @CsvSource({
            "2025-05-20, 2, 2, 20250520",
            "2024-08-11, 2, 2, 20240811",
            "2024-01-10, 2, 2, 20240110",
            "2023-12-16, 2, 2, 20231216",
    })
    void convertYyyyMmDdToYyyymmdd(String input, String inType,
                                    String outType, String expected) {
        assertEquals(expected, DateConverter.convert(input, inType, outType));
    }

    @ParameterizedTest
    @CsvSource({
            "20250520, 1, 1, 2025-05-20",
            "20240811, 1, 1, 2024-08-11",
    })
    void convertYyyymmddToYyyyMmDd(String input, String inType,
                                    String outType, String expected) {
        assertEquals(expected, DateConverter.convert(input, inType, outType));
    }

    @Test
    void convertYyyymmddIdentity() {
        assertEquals("20250520", DateConverter.convert("20250520", "1", "2"));
    }

    @Test
    void convertYyyyMmDdIdentity() {
        assertEquals("2025-05-20", DateConverter.convert("2025-05-20", "2", "1"));
    }

    @Test
    void invalidTypeThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> DateConverter.convert("2025-05-20", "3", "2"));
    }

    @Test
    void invalidDateThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> DateConverter.convert("not-a-date", "2", "2"));
    }
}
