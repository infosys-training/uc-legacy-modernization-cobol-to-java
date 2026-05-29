package com.carddemo.testharness.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CobolSignDecoderTest {

    @ParameterizedTest
    @CsvSource({
        "123{, 1230, true",
        "123A, 1231, true",
        "123B, 1232, true",
        "123C, 1233, true",
        "123D, 1234, true",
        "123E, 1235, true",
        "123F, 1236, true",
        "123G, 1237, true",
        "123H, 1238, true",
        "123I, 1239, true"
    })
    void shouldDecodePositiveSignCharacters(String raw, String expectedDigits, boolean expectedPositive) {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decode(raw);
        assertThat(result.digits()).isEqualTo(expectedDigits);
        assertThat(result.positive()).isEqualTo(expectedPositive);
    }

    @ParameterizedTest
    @CsvSource({
        "123}, 1230, false",
        "123J, 1231, false",
        "123K, 1232, false",
        "123L, 1233, false",
        "123M, 1234, false",
        "123N, 1235, false",
        "123O, 1236, false",
        "123P, 1237, false",
        "123Q, 1238, false",
        "123R, 1239, false"
    })
    void shouldDecodeNegativeSignCharacters(String raw, String expectedDigits, boolean expectedPositive) {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decode(raw);
        assertThat(result.digits()).isEqualTo(expectedDigits);
        assertThat(result.positive()).isEqualTo(expectedPositive);
    }

    @Test
    void shouldHandleAllDigitsRaw() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decode("12345");
        assertThat(result.digits()).isEqualTo("12345");
        assertThat(result.positive()).isTrue();
    }

    @Test
    void shouldHandleSingleCharPositive() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decode("{");
        assertThat(result.digits()).isEqualTo("0");
        assertThat(result.positive()).isTrue();
    }

    @Test
    void shouldHandleSingleCharNegative() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decode("}");
        assertThat(result.digits()).isEqualTo("0");
        assertThat(result.positive()).isFalse();
    }

    @Test
    void shouldThrowOnNullInput() {
        assertThatThrownBy(() -> CobolSignDecoder.decode(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowOnEmptyInput() {
        assertThatThrownBy(() -> CobolSignDecoder.decode(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowOnInvalidSignChar() {
        assertThatThrownBy(() -> CobolSignDecoder.decode("123Z"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDecodeZeroWithPositiveSign() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decode("0000000000{");
        assertThat(result.digits()).isEqualTo("00000000000");
        assertThat(result.positive()).isTrue();
    }

    @Test
    void shouldDecodeZeroWithNegativeSign() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decode("0000000000}");
        assertThat(result.digits()).isEqualTo("00000000000");
        assertThat(result.positive()).isFalse();
    }
}
