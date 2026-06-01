package com.carddemo.testharness.comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class FieldByFieldComparatorTest {

    private FieldByFieldComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new FieldByFieldComparator();
    }

    @Test
    void shouldMatchIdenticalRecords() {
        Map<String, Object> record = new LinkedHashMap<>();
        record.put("ACCT-ID", "00000000001");
        record.put("ACCT-ACTIVE-STATUS", "Y");
        record.put("ACCT-CURR-BAL", new BigDecimal("194.00"));

        List<ComparisonResult> results = comparator.compare(record, record, EnumSet.noneOf(ToleranceRule.class));

        assertThat(results).allMatch(ComparisonResult::isMatch);
    }

    @Test
    void shouldDetectMismatchedFields() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ACCT-ID", "00000000001");
        expected.put("ACCT-CURR-BAL", new BigDecimal("194.00"));

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ACCT-ID", "00000000002");
        actual.put("ACCT-CURR-BAL", new BigDecimal("200.00"));

        List<ComparisonResult> results = comparator.compare(expected, actual, EnumSet.noneOf(ToleranceRule.class));

        assertThat(results).hasSize(2);
        assertThat(results.get(0).isMatch()).isFalse();
        assertThat(results.get(1).isMatch()).isFalse();
    }

    @Test
    void shouldApplyTrailingSpacesTolerance() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("NAME", "John   ");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("NAME", "John");

        List<ComparisonResult> withoutTolerance = comparator.compare(
            expected, actual, EnumSet.noneOf(ToleranceRule.class));
        assertThat(withoutTolerance.get(0).isMatch()).isFalse();

        List<ComparisonResult> withTolerance = comparator.compare(
            expected, actual, EnumSet.of(ToleranceRule.TRAILING_SPACES));
        assertThat(withTolerance.get(0).isMatch()).isTrue();
        assertThat(withTolerance.get(0).getToleranceApplied()).contains("TRAILING_SPACES");
    }

    @Test
    void shouldApplyLeadingZerosTolerance() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ACCT-ID", "00000000001");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ACCT-ID", "1");

        List<ComparisonResult> withoutTolerance = comparator.compare(
            expected, actual, EnumSet.noneOf(ToleranceRule.class));
        assertThat(withoutTolerance.get(0).isMatch()).isFalse();

        List<ComparisonResult> withTolerance = comparator.compare(
            expected, actual, EnumSet.of(ToleranceRule.LEADING_ZEROS));
        assertThat(withTolerance.get(0).isMatch()).isTrue();
        assertThat(withTolerance.get(0).getToleranceApplied()).contains("LEADING_ZEROS");
    }

    @Test
    void shouldApplyDecimalPrecisionTolerance() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("BALANCE", new BigDecimal("194.001"));

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("BALANCE", new BigDecimal("194.004"));

        List<ComparisonResult> withTolerance = comparator.compare(
            expected, actual, EnumSet.of(ToleranceRule.DECIMAL_PRECISION));
        assertThat(withTolerance.get(0).isMatch()).isTrue();
        assertThat(withTolerance.get(0).getToleranceApplied()).contains("DECIMAL_PRECISION");
    }

    @Test
    void shouldSkipFillerFieldsWithTolerance() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ACCT-ID", "00000000001");
        expected.put("FILLER", "some data");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ACCT-ID", "00000000001");
        actual.put("FILLER", "different data");

        List<ComparisonResult> results = comparator.compare(
            expected, actual, EnumSet.of(ToleranceRule.FILLER_SKIP));
        assertThat(results).hasSize(1);
        assertThat(results.get(0).getFieldName()).isEqualTo("ACCT-ID");
    }

    @Test
    void shouldHandleNullValues() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("FIELD", null);

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("FIELD", null);

        List<ComparisonResult> results = comparator.compare(
            expected, actual, EnumSet.noneOf(ToleranceRule.class));
        assertThat(results.get(0).isMatch()).isTrue();
    }

    @Test
    void shouldHandleMissingActualField() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("FIELD", "value");

        Map<String, Object> actual = new LinkedHashMap<>();

        List<ComparisonResult> results = comparator.compare(
            expected, actual, EnumSet.noneOf(ToleranceRule.class));
        assertThat(results.get(0).isMatch()).isFalse();
    }

    @Test
    void shouldCombineMultipleToleranceRules() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ID", "007");
        expected.put("NAME", "Bond   ");
        expected.put("BAL", new BigDecimal("100.001"));
        expected.put("FILLER", "xxx");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ID", "7");
        actual.put("NAME", "Bond");
        actual.put("BAL", new BigDecimal("100.004"));
        actual.put("FILLER", "yyy");

        Set<ToleranceRule> allRules = EnumSet.allOf(ToleranceRule.class);
        List<ComparisonResult> results = comparator.compare(expected, actual, allRules);

        assertThat(results).hasSize(3);
        assertThat(results).allMatch(ComparisonResult::isMatch);
    }

    @Test
    void shouldHandleConfigurableDecimalScale() {
        comparator.setDecimalScale(3);

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("BAL", new BigDecimal("100.0011"));

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("BAL", new BigDecimal("100.0014"));

        List<ComparisonResult> results = comparator.compare(
            expected, actual, EnumSet.of(ToleranceRule.DECIMAL_PRECISION));
        assertThat(results.get(0).isMatch()).isTrue();
    }
}
