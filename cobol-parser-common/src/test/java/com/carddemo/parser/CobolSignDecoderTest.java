package com.carddemo.parser;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CobolSignDecoderTest {

    @Test
    void positiveZero() {
        BigDecimal result = CobolSignDecoder.decode("00000001940{", 2);
        assertThat(result).isEqualByComparingTo("194.00");
    }

    @Test
    void positiveDigitA() {
        BigDecimal result = CobolSignDecoder.decode("0000005047G", 2);
        assertThat(result).isEqualByComparingTo("504.77");
    }

    @Test
    void positiveDigitI() {
        BigDecimal result = CobolSignDecoder.decode("000I", 2);
        assertThat(result).isEqualByComparingTo("0.09");
    }

    @Test
    void negativeZero() {
        BigDecimal result = CobolSignDecoder.decode("0000009190}", 2);
        assertThat(result).isEqualByComparingTo("-919.00");
    }

    @Test
    void negativeDigitJ() {
        BigDecimal result = CobolSignDecoder.decode("001J", 2);
        assertThat(result).isEqualByComparingTo("-0.11");
    }

    @Test
    void negativeDigitR() {
        BigDecimal result = CobolSignDecoder.decode("00R", 2);
        assertThat(result).isEqualByComparingTo("-0.09");
    }

    @Test
    void noDecimalPlaces() {
        BigDecimal result = CobolSignDecoder.decode("0100{", 0);
        assertThat(result).isEqualByComparingTo("1000");
    }

    @Test
    void allPositiveMappings() {
        assertThat(CobolSignDecoder.decode("{", 0)).isEqualByComparingTo("0");
        assertThat(CobolSignDecoder.decode("A", 0)).isEqualByComparingTo("1");
        assertThat(CobolSignDecoder.decode("B", 0)).isEqualByComparingTo("2");
        assertThat(CobolSignDecoder.decode("C", 0)).isEqualByComparingTo("3");
        assertThat(CobolSignDecoder.decode("D", 0)).isEqualByComparingTo("4");
        assertThat(CobolSignDecoder.decode("E", 0)).isEqualByComparingTo("5");
        assertThat(CobolSignDecoder.decode("F", 0)).isEqualByComparingTo("6");
        assertThat(CobolSignDecoder.decode("G", 0)).isEqualByComparingTo("7");
        assertThat(CobolSignDecoder.decode("H", 0)).isEqualByComparingTo("8");
        assertThat(CobolSignDecoder.decode("I", 0)).isEqualByComparingTo("9");
    }

    @Test
    void allNegativeMappings() {
        assertThat(CobolSignDecoder.decode("}", 0)).isEqualByComparingTo("0");
        assertThat(CobolSignDecoder.decode("J", 0)).isEqualByComparingTo("-1");
        assertThat(CobolSignDecoder.decode("K", 0)).isEqualByComparingTo("-2");
        assertThat(CobolSignDecoder.decode("L", 0)).isEqualByComparingTo("-3");
        assertThat(CobolSignDecoder.decode("M", 0)).isEqualByComparingTo("-4");
        assertThat(CobolSignDecoder.decode("N", 0)).isEqualByComparingTo("-5");
        assertThat(CobolSignDecoder.decode("O", 0)).isEqualByComparingTo("-6");
        assertThat(CobolSignDecoder.decode("P", 0)).isEqualByComparingTo("-7");
        assertThat(CobolSignDecoder.decode("Q", 0)).isEqualByComparingTo("-8");
        assertThat(CobolSignDecoder.decode("R", 0)).isEqualByComparingTo("-9");
    }

    @Test
    void plainDigitAsLastChar() {
        BigDecimal result = CobolSignDecoder.decode("0150", 2);
        assertThat(result).isEqualByComparingTo("1.50");
    }

    @Test
    void invalidCharThrowsException() {
        assertThatThrownBy(() -> CobolSignDecoder.decode("00Z", 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Invalid COBOL sign-encoded character");
    }

    @Test
    void nullOrEmptyReturnsZero() {
        assertThat(CobolSignDecoder.decode(null, 2)).isEqualByComparingTo("0.00");
        assertThat(CobolSignDecoder.decode("", 2)).isEqualByComparingTo("0.00");
    }

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
    void shouldDecodePositiveSignCharactersRaw(String raw, String expectedDigits, boolean expectedPositive) {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decodeRaw(raw);
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
    void shouldDecodeNegativeSignCharactersRaw(String raw, String expectedDigits, boolean expectedPositive) {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decodeRaw(raw);
        assertThat(result.digits()).isEqualTo(expectedDigits);
        assertThat(result.positive()).isEqualTo(expectedPositive);
    }

    @Test
    void shouldHandleAllDigitsRaw() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decodeRaw("12345");
        assertThat(result.digits()).isEqualTo("12345");
        assertThat(result.positive()).isTrue();
    }

    @Test
    void shouldHandleSingleCharPositiveRaw() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decodeRaw("{");
        assertThat(result.digits()).isEqualTo("0");
        assertThat(result.positive()).isTrue();
    }

    @Test
    void shouldHandleSingleCharNegativeRaw() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decodeRaw("}");
        assertThat(result.digits()).isEqualTo("0");
        assertThat(result.positive()).isFalse();
    }

    @Test
    void shouldThrowOnNullInputRaw() {
        assertThatThrownBy(() -> CobolSignDecoder.decodeRaw(null))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowOnEmptyInputRaw() {
        assertThatThrownBy(() -> CobolSignDecoder.decodeRaw(""))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldThrowOnInvalidSignCharRaw() {
        assertThatThrownBy(() -> CobolSignDecoder.decodeRaw("123Z"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void shouldDecodeZeroWithPositiveSignRaw() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decodeRaw("0000000000{");
        assertThat(result.digits()).isEqualTo("00000000000");
        assertThat(result.positive()).isTrue();
    }

    @Test
    void shouldDecodeZeroWithNegativeSignRaw() {
        CobolSignDecoder.SignedValue result = CobolSignDecoder.decodeRaw("0000000000}");
        assertThat(result.digits()).isEqualTo("00000000000");
        assertThat(result.positive()).isFalse();
    }
}
