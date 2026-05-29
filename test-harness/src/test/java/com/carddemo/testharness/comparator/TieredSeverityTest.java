package com.carddemo.testharness.comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class TieredSeverityTest {

    private FieldByFieldComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new FieldByFieldComparator();
        ToleranceRules config = new ToleranceRules();
        config.setNormalizeDateFormats(true);
        config.setAllowPartialArrays(true);
        comparator.setToleranceRulesConfig(config);
    }

    @Test
    void numericMismatchShouldBeError() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("BALANCE", new BigDecimal("100.00"));

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("BALANCE", new BigDecimal("200.00"));

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.noneOf(ToleranceRule.class));

        assertThat(results).hasSize(1);
        assertThat(results.get(0).isMatch()).isFalse();
        assertThat(results.get(0).getSeverity()).isEqualTo(MismatchSeverity.ERROR);
    }

    @Test
    void trailingSpaceDifferenceShouldBeWarning() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("NAME", "John   ");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("NAME", "John");

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.TRAILING_SPACES));

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isTrue();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.WARNING);
    }

    @Test
    void dateFormatDifferenceShouldBeWarning() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("OPEN-DATE", "20250520");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("OPEN-DATE", "2025-05-20");

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.NORMALIZE_DATE_FORMATS));

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isFalse();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.WARNING);
    }

    @Test
    void partialArrayWithZeroExtrasShouldBeWarning() {
        List<BigDecimal> cobolArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00"),
            BigDecimal.ZERO, BigDecimal.ZERO
        ));
        List<BigDecimal> javaArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00")
        ));

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ARR-VALUES", cobolArray);

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ARR-VALUES", javaArray);

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.ALLOW_PARTIAL_ARRAYS));

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isFalse();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.WARNING);
    }

    @Test
    void partialArrayWithNonZeroExtrasShouldBeError() {
        List<BigDecimal> cobolArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00"),
            new BigDecimal("40.00"), new BigDecimal("50.00")
        ));
        List<BigDecimal> javaArray = new ArrayList<>(List.of(
            new BigDecimal("10.00"), new BigDecimal("20.00"), new BigDecimal("30.00")
        ));

        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ARR-VALUES", cobolArray);

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ARR-VALUES", javaArray);

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.ALLOW_PARTIAL_ARRAYS));

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isFalse();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.ERROR);
    }

    @Test
    void shouldCountSeveritiesCorrectly() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("BALANCE", new BigDecimal("100.00"));
        expected.put("NAME", "John   ");
        expected.put("OPEN-DATE", "20250520");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("BALANCE", new BigDecimal("200.00"));
        actual.put("NAME", "John");
        actual.put("OPEN-DATE", "2025-05-20");

        Set<ToleranceRule> rules = EnumSet.of(
            ToleranceRule.TRAILING_SPACES,
            ToleranceRule.NORMALIZE_DATE_FORMATS
        );
        List<ComparisonResult> results = comparator.compare(expected, actual, rules);

        assertThat(ComparisonResult.errorCount(results)).isEqualTo(1);
        assertThat(ComparisonResult.warningCount(results)).isEqualTo(2);
        assertThat(ComparisonResult.infoCount(results)).isEqualTo(0);
    }

    @Test
    void leadingZeroDifferenceShouldBeWarning() {
        Map<String, Object> expected = new LinkedHashMap<>();
        expected.put("ACCT-ID", "00000000001");

        Map<String, Object> actual = new LinkedHashMap<>();
        actual.put("ACCT-ID", "1");

        List<ComparisonResult> results = comparator.compare(expected, actual,
            EnumSet.of(ToleranceRule.LEADING_ZEROS));

        assertThat(results).hasSize(1);
        ComparisonResult result = results.get(0);
        assertThat(result.isMatch()).isTrue();
        assertThat(result.getSeverity()).isEqualTo(MismatchSeverity.WARNING);
    }
}
