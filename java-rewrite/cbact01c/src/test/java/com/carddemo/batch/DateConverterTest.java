package com.carddemo.batch;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateConverterTest {

    @Test
    void convertHyphenatedToCompact() {
        // Type '2' (YYYY-MM-DD) → OutType '2' (YYYYMMDD)
        // This is the exact conversion CBACT01C uses for reissue dates
        assertEquals("20250520", DateConverter.convert("2025-05-20", '2', '2'));
    }

    @Test
    void convertCompactToHyphenated() {
        assertEquals("2025-05-20", DateConverter.convert("20250520", '1', '1'));
    }

    @Test
    void convertHyphenatedToHyphenated() {
        assertEquals("2025-05-20", DateConverter.convert("2025-05-20", '2', '1'));
    }

    @Test
    void convertCompactToCompact() {
        assertEquals("20250520", DateConverter.convert("20250520", '1', '2'));
    }

    @Test
    void convertAllSampleReissueDates() {
        // From app/data/ASCII/acctdata.txt — first 5 records' reissue dates
        assertEquals("20250520", DateConverter.convert("2025-05-20", '2', '2'));
        assertEquals("20240811", DateConverter.convert("2024-08-11", '2', '2'));
        assertEquals("20240110", DateConverter.convert("2024-01-10", '2', '2'));
        assertEquals("20231216", DateConverter.convert("2023-12-16", '2', '2'));
        assertEquals("20250309", DateConverter.convert("2025-03-09", '2', '2'));
    }

    @Test
    void shortInputReturnsUnchanged() {
        assertEquals("2025", DateConverter.convert("2025", '2', '2'));
    }
}
