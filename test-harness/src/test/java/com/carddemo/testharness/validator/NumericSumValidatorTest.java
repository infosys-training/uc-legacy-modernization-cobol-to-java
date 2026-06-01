package com.carddemo.testharness.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class NumericSumValidatorTest {

    private NumericSumValidator validator;

    @BeforeEach
    void setUp() {
        validator = new NumericSumValidator();
    }

    @Test
    void shouldPassWhenSumMatchesExpected() {
        List<Map<String, Object>> records = List.of(
            Map.of("AMOUNT", new BigDecimal("100.00")),
            Map.of("AMOUNT", new BigDecimal("200.50")),
            Map.of("AMOUNT", new BigDecimal("49.50"))
        );

        ValidationResult result = validator.validate(records, "AMOUNT", new BigDecimal("350.00"));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailWhenSumDoesNotMatch() {
        List<Map<String, Object>> records = List.of(
            Map.of("AMOUNT", new BigDecimal("100.00")),
            Map.of("AMOUNT", new BigDecimal("200.00"))
        );

        ValidationResult result = validator.validate(records, "AMOUNT", new BigDecimal("999.99"));

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("999.99");
        assertThat(result.getMessage()).contains("300.00");
    }

    @Test
    void shouldHandleNegativeAmounts() {
        List<Map<String, Object>> records = List.of(
            Map.of("AMOUNT", new BigDecimal("500.00")),
            Map.of("AMOUNT", new BigDecimal("-200.00"))
        );

        ValidationResult result = validator.validate(records, "AMOUNT", new BigDecimal("300.00"));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldHandleEmptyRecords() {
        List<Map<String, Object>> records = List.of();

        ValidationResult result = validator.validate(records, "AMOUNT", BigDecimal.ZERO);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldHandleMissingField() {
        List<Map<String, Object>> records = List.of(
            Map.of("OTHER", new BigDecimal("100.00"))
        );

        ValidationResult result = validator.validate(records, "AMOUNT", BigDecimal.ZERO);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldHandleStringNumericValues() {
        List<Map<String, Object>> records = List.of(
            Map.of("COUNT", "100"),
            Map.of("COUNT", "200")
        );

        ValidationResult result = validator.validate(records, "COUNT", new BigDecimal("300"));

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailOnNonNumericStringValues() {
        List<Map<String, Object>> records = List.of(
            Map.of("FIELD", "abc")
        );

        ValidationResult result = validator.validate(records, "FIELD", BigDecimal.ZERO);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("Non-numeric");
    }
}
