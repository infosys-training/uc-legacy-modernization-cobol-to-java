package com.carddemo.testharness.comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class OccursArrayTest {

    private FieldByFieldComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new FieldByFieldComparator();
        ToleranceRules config = new ToleranceRules();
        config.setAllowPartialArrays(true);
        comparator.setToleranceRulesConfig(config);
    }

    @Test
    void shouldWarnWhenCobolHasMoreZeroElements() {
        // COBOL: 5 elements, Java: 3 elements, extras are zero
        List<BigDecimal> cobolArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00"),
            BigDecimal.ZERO, BigDecimal.ZERO
        ));
        List<BigDecimal> javaArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00")
        ));

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ACCT-DEBIT-ARR", cobolArray);

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ACCT-DEBIT-ARR", javaArray);

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.ALLOW_PARTIAL_ARRAYS));

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isFalse();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.WARNING);
    }

    @Test
    void shouldErrorWhenCobolHasMoreNonZeroElements() {
        // COBOL: 5 elements, Java: 3 elements, extras are non-zero
        List<BigDecimal> cobolArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00"),
            new BigDecimal("40.00"), new BigDecimal("50.00")
        ));
        List<BigDecimal> javaArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00")
        ));

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ACCT-DEBIT-ARR", cobolArray);

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ACCT-DEBIT-ARR", javaArray);

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.ALLOW_PARTIAL_ARRAYS));

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isFalse();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.ERROR);
    }

    @Test
    void shouldMatchEqualArrays() {
        List<BigDecimal> arr = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00")
        ));

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("VALUES", arr);

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("VALUES", new ArrayList<>(arr));

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.ALLOW_PARTIAL_ARRAYS));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).isMatch()).isTrue();
    }

    @Test
    void shouldErrorOnElementMismatch() {
        List<BigDecimal> cobolArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("999.00"), new BigDecimal("30.00")
        ));
        List<BigDecimal> javaArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00")
        ));

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("VALUES", cobolArray);

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("VALUES", javaArray);

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.ALLOW_PARTIAL_ARRAYS));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).isMatch()).isFalse();
        assertThat(results.get(0).getSeverity()).isEqualTo(MismatchSeverity.ERROR);
    }

    @Test
    void shouldHandleStringArraysWithSpaceExtras() {
        List<String> cobolArray = new ArrayList<>(List.of("A", "B", "C", "   ", ""));
        List<String> javaArray = new ArrayList<>(List.of("A", "B", "C"));

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("CODES", cobolArray);

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("CODES", javaArray);

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.ALLOW_PARTIAL_ARRAYS));

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isFalse();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.WARNING);
    }
}
