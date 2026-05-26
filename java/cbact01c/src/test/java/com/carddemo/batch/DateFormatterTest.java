package com.carddemo.batch;

import com.carddemo.batch.io.DateFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DateFormatterTest {

    @Test
    void toCompactDate_standard() {
        assertEquals("20250520", DateFormatter.toCompactDate("2025-05-20"));
    }

    @Test
    void toCompactDate_differentDate() {
        assertEquals("20241231", DateFormatter.toCompactDate("2024-12-31"));
    }

    @Test
    void toCompactDate_nullReturnsNull() {
        assertNull(DateFormatter.toCompactDate(null));
    }

    @Test
    void toCompactDate_shortStringPassedThrough() {
        assertEquals("2025", DateFormatter.toCompactDate("2025"));
    }

    @Test
    void extractYear() {
        assertEquals("2025", DateFormatter.extractYear("2025-05-20"));
    }

    @Test
    void extractYear_nullReturnsBlanks() {
        assertEquals("    ", DateFormatter.extractYear(null));
    }
}
