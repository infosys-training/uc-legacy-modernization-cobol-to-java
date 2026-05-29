package com.carddemo.testharness.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class CrossReferenceValidatorTest {

    private CrossReferenceValidator validator;

    @BeforeEach
    void setUp() {
        validator = new CrossReferenceValidator();
    }

    @Test
    void shouldPassWhenAllReferencesExist() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("XREF-CARD-NUM", "4000000000000001"),
            Map.of("XREF-CARD-NUM", "4000000000000002")
        );
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("CARD-NUM", "4000000000000001"),
            Map.of("CARD-NUM", "4000000000000002"),
            Map.of("CARD-NUM", "4000000000000003")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "XREF-CARD-NUM",
            targetRecords, "CARD-NUM");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailWhenReferencesAreMissing() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("XREF-CARD-NUM", "4000000000000001"),
            Map.of("XREF-CARD-NUM", "4000000000000099")
        );
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("CARD-NUM", "4000000000000001")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "XREF-CARD-NUM",
            targetRecords, "CARD-NUM");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("4000000000000099");
    }

    @Test
    void shouldPassWithEmptySource() {
        List<Map<String, Object>> sourceRecords = List.of();
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("CARD-NUM", "4000000000000001")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "XREF-CARD-NUM",
            targetRecords, "CARD-NUM");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailWithEmptyTarget() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("XREF-CARD-NUM", "4000000000000001")
        );
        List<Map<String, Object>> targetRecords = List.of();

        ValidationResult result = validator.validate(
            sourceRecords, "XREF-CARD-NUM",
            targetRecords, "CARD-NUM");

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void shouldHandleDuplicateSourceReferences() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("CARD-NUM", "4000000000000001"),
            Map.of("CARD-NUM", "4000000000000001"),
            Map.of("CARD-NUM", "4000000000000002")
        );
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("ACCT-ID", "4000000000000001"),
            Map.of("ACCT-ID", "4000000000000002")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "CARD-NUM",
            targetRecords, "ACCT-ID");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldReportMultipleMissingReferences() {
        List<Map<String, Object>> sourceRecords = List.of(
            Map.of("ID", "A"),
            Map.of("ID", "B"),
            Map.of("ID", "C")
        );
        List<Map<String, Object>> targetRecords = List.of(
            Map.of("REF", "A")
        );

        ValidationResult result = validator.validate(
            sourceRecords, "ID",
            targetRecords, "REF");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("2 values");
    }
}
