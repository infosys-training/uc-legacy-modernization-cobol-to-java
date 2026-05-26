package com.carddemo;

import com.carddemo.util.CobolDateFormatter;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for the COBDATFT assembler replacement.
 *
 * The COBOL program calls COBDATFT with:
 *   CODATECN-TYPE = '2' (YYYY-MM-DD input)
 *   CODATECN-OUTTYPE = '2' (YYYYMMDD output)
 */
class CobolDateFormatterTest {

    @Test
    void convertYyyyMmDdToYyyymmdd() {
        // This is exactly what CBACT01C does: type=2, outType=2
        assertThat(CobolDateFormatter.formatDate("2025-05-20", '2', '2'))
                .isEqualTo("20250520");
    }

    @Test
    void convertYyyymmddToYyyyMmDd() {
        assertThat(CobolDateFormatter.formatDate("20250520", '1', '1'))
                .isEqualTo("2025-05-20");
    }

    @Test
    void convertYyyyMmDdToYyyyMmDd() {
        assertThat(CobolDateFormatter.formatDate("2025-05-20", '2', '1'))
                .isEqualTo("2025-05-20");
    }

    @Test
    void convertYyyymmddToYyyymmdd() {
        assertThat(CobolDateFormatter.formatDate("20250520", '1', '2'))
                .isEqualTo("20250520");
    }

    @Test
    void handlesNullAndBlank() {
        assertThat(CobolDateFormatter.formatDate(null, '2', '2')).isNull();
        assertThat(CobolDateFormatter.formatDate("", '2', '2')).isEqualTo("");
        assertThat(CobolDateFormatter.formatDate("   ", '2', '2')).isEqualTo("   ");
    }
}
