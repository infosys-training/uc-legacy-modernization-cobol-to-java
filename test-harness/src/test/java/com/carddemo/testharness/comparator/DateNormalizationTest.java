package com.carddemo.testharness.comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class DateNormalizationTest {

    private FieldByFieldComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new FieldByFieldComparator();
        ToleranceRules config = new ToleranceRules();
        config.setNormalizeDateFormats(true);
        comparator.setToleranceRulesConfig(config);
    }

    @Test
    void shouldWarnWhenDateFormatsMatchSemantically() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("OPEN-DATE", "20250520");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("OPEN-DATE", "2025-05-20");

        Set<ToleranceRule> rules = EnumSet.of(ToleranceRule.NORMALIZE_DATE_FORMATS);
        List<ComparisonResult> results = comparator.compare(expected, actual, rules);

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isFalse();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.WARNING);
        assertThat(result.getToleranceApplied()).contains("NORMALIZE_DATE_FORMATS");
    }

    @Test
    void shouldErrorWhenDateValuesDiffer() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("CLOSE-DATE", "20250520");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("CLOSE-DATE", "2025-06-15");

        Set<ToleranceRule> rules = EnumSet.of(ToleranceRule.NORMALIZE_DATE_FORMATS);
        List<ComparisonResult> results = comparator.compare(expected, actual, rules);

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isFalse();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.ERROR);
    }

    @Test
    void shouldMatchIdenticalDates() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("TRANS-DATE", "2025-05-20");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("TRANS-DATE", "2025-05-20");

        Set<ToleranceRule> rules = EnumSet.of(ToleranceRule.NORMALIZE_DATE_FORMATS);
        List<ComparisonResult> results = comparator.compare(expected, actual, rules);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).isMatch()).isTrue();
    }

    @Test
    void shouldHandleMMddyyyyFormat() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("DUE-DATE", "05/20/2025");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("DUE-DATE", "2025-05-20");

        Set<ToleranceRule> rules = EnumSet.of(ToleranceRule.NORMALIZE_DATE_FORMATS);
        List<ComparisonResult> results = comparator.compare(expected, actual, rules);

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isFalse();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.WARNING);
    }

    @Test
    void shouldNotNormalizeDateForNonDateField() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ACCT-ID", "20250520");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ACCT-ID", "2025-05-20");

        Set<ToleranceRule> rules = EnumSet.of(ToleranceRule.NORMALIZE_DATE_FORMATS);
        List<ComparisonResult> results = comparator.compare(expected, actual, rules);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).isMatch()).isFalse();
        assertThat(results.get(0).getSeverity()).isEqualTo(MismatchSeverity.ERROR);
    }

    @Test
    void shouldRecognizeConfiguredDateFieldNames() {
        ToleranceRules config = new ToleranceRules();
        config.setNormalizeDateFormats(true);
        config.setDateFieldNames(List.of("ACCT-OPEN"));
        comparator.setToleranceRulesConfig(config);

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ACCT-OPEN", "20250520");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ACCT-OPEN", "2025-05-20");

        Set<ToleranceRule> rules = EnumSet.of(ToleranceRule.NORMALIZE_DATE_FORMATS);
        List<ComparisonResult> results = comparator.compare(expected, actual, rules);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getSeverity()).isEqualTo(MismatchSeverity.WARNING);
    }
}
