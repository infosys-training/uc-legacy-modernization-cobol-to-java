package com.carddemo.testharness.validator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class RecordCountValidatorTest {

    private RecordCountValidator validator;

    @BeforeEach
    void setUp() {
        validator = new RecordCountValidator();
    }

    @Test
    void shouldPassWhenTwoDatasetsHaveSameCount() {
        List<String> dataset1 = List.of("a", "b", "c");
        List<String> dataset2 = List.of("x", "y", "z");

        ValidationResult result = validator.validate(dataset1, dataset2);

        assertThat(result.isValid()).isTrue();
        assertThat(result.getMessage()).contains("3");
    }

    @Test
    void shouldFailWhenTwoDatasetsHaveDifferentCounts() {
        List<String> dataset1 = List.of("a", "b", "c");
        List<String> dataset2 = List.of("x", "y");

        ValidationResult result = validator.validate(dataset1, dataset2);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("3");
        assertThat(result.getMessage()).contains("2");
    }

    @Test
    void shouldPassWhenDatasetMatchesExpectedCount() {
        List<String> dataset = List.of("a", "b", "c");

        ValidationResult result = validator.validate(dataset, 3);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldFailWhenDatasetDoesNotMatchExpectedCount() {
        List<String> dataset = List.of("a", "b");

        ValidationResult result = validator.validate(dataset, 5);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getMessage()).contains("5");
        assertThat(result.getMessage()).contains("2");
    }

    @Test
    void shouldHandleEmptyDatasets() {
        ValidationResult result = validator.validate(List.of(), List.of());
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void shouldHandleEmptyDatasetWithExpectedZero() {
        ValidationResult result = validator.validate(List.of(), 0);
        assertThat(result.isValid()).isTrue();
    }
}
