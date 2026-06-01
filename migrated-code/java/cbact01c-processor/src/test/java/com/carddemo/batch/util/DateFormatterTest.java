package com.carddemo.batch.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

class DateFormatterTest {

    private final DateFormatter formatter = new DateFormatter();

    @Test
    void type2ToType2_stripsDashes() {
        assertThat(formatter.convertDate("2025-05-20", '2', '2')).isEqualTo("20250520");
    }

    @Test
    void type1ToType1_addsDashes() {
        assertThat(formatter.convertDate("20250520", '1', '1')).isEqualTo("2025-05-20");
    }

    @Test
    void crossType2To1_throwsIllegalArgument() {
        assertThatThrownBy(() -> formatter.convertDate("2025-05-20", '2', '1'))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void crossType1To2_throwsIllegalArgument() {
        assertThatThrownBy(() -> formatter.convertDate("20250520", '1', '2'))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void edgeY2k_type2ToType2() {
        assertThat(formatter.convertDate("2000-01-01", '2', '2')).isEqualTo("20000101");
    }

    @Test
    void edgeMaxDate_type2ToType2() {
        assertThat(formatter.convertDate("9999-12-31", '2', '2')).isEqualTo("99991231");
    }
}
