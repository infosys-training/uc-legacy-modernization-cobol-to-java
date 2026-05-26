package com.carddemo.batch;

import com.carddemo.batch.io.CobolDecimalParser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class CobolDecimalParserTest {

    @ParameterizedTest(name = "parse \"{0}\" → {1}")
    @CsvSource({
            // Positive zero (overpunch '{') — PIC S9(10)V99
            "00000000000{, 0.00",
            // From sample data: 00000001940{ = digits 000000019400, V99 → 194.00
            "00000001940{, 194.00",
            "00000020200{, 2020.00",
            // Overpunch A = +1
            "0000000005A, 0.51",
            // Overpunch I = +9
            "0000000010I, 1.09",
            // Negative zero (overpunch '}')
            "00000000000}, 0.00",
            // Overpunch J = -1: digits 000000010251, V99 → -102.51
            "00000001025J, -102.51",
            // Overpunch R = -9: digits 000000025009, V99 → -250.09
            "00000002500R, -250.09",
    })
    void parseSignedDecimal(String raw, String expected) {
        BigDecimal result = CobolDecimalParser.parseSignedDecimal(raw, 2);
        assertEquals(new BigDecimal(expected), result);
    }

    @Test
    void parseNullReturnsZero() {
        assertEquals(BigDecimal.ZERO, CobolDecimalParser.parseSignedDecimal(null, 2));
        assertEquals(BigDecimal.ZERO, CobolDecimalParser.parseSignedDecimal("", 2));
    }

    @Test
    void formatRoundTrip() {
        BigDecimal value = new BigDecimal("1940.00");
        String formatted = CobolDecimalParser.formatSignedDecimal(value, 12, 2);
        // 1940.00 → unscaled 194000 → 12-digit "000000194000" → overpunch "00000019400{"
        assertEquals("00000019400{", formatted);
        assertEquals(0, value.compareTo(CobolDecimalParser.parseSignedDecimal(formatted, 2)));
    }

    @Test
    void formatNegativeRoundTrip() {
        BigDecimal value = new BigDecimal("-1025.00");
        String formatted = CobolDecimalParser.formatSignedDecimal(value, 12, 2);
        // -1025.00 → unscaled 102500 → "000000102500" → overpunch "00000010250}"
        assertEquals("00000010250}", formatted);
        assertEquals(0, value.compareTo(CobolDecimalParser.parseSignedDecimal(formatted, 2)));
    }

    @Test
    void invalidOverpunchThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> CobolDecimalParser.parseSignedDecimal("00000000000Z", 2));
    }
}
