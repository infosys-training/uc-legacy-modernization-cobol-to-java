package com.carddemo.testharness.comparator;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldByFieldComparator {

    private int decimalScale = 2;

    public void setDecimalScale(int decimalScale) {
        this.decimalScale = decimalScale;
    }

    public List<ComparisonResult> compare(
            Map<String, Object> expected,
            Map<String, Object> actual,
            Set<ToleranceRule> toleranceRules) {

        List<ComparisonResult> results = new ArrayList<>();

        for (Map.Entry<String, Object> entry : expected.entrySet()) {
            String fieldName = entry.getKey();
            Object expectedValue = entry.getValue();
            Object actualValue = actual.get(fieldName);

            if (toleranceRules.contains(ToleranceRule.FILLER_SKIP)
                    && fieldName.equals("FILLER")) {
                continue;
            }

            boolean match;
            String toleranceApplied = null;

            if (expectedValue == null && actualValue == null) {
                match = true;
            } else if (expectedValue == null || actualValue == null) {
                match = false;
            } else if (expectedValue instanceof BigDecimal && actualValue instanceof BigDecimal) {
                if (toleranceRules.contains(ToleranceRule.DECIMAL_PRECISION)) {
                    BigDecimal exp = ((BigDecimal) expectedValue).setScale(decimalScale, RoundingMode.HALF_UP);
                    BigDecimal act = ((BigDecimal) actualValue).setScale(decimalScale, RoundingMode.HALF_UP);
                    match = exp.compareTo(act) == 0;
                    toleranceApplied = "DECIMAL_PRECISION(scale=" + decimalScale + ")";
                } else {
                    match = ((BigDecimal) expectedValue).compareTo((BigDecimal) actualValue) == 0;
                }
            } else if (expectedValue instanceof String && actualValue instanceof String) {
                String expStr = (String) expectedValue;
                String actStr = (String) actualValue;

                if (toleranceRules.contains(ToleranceRule.TRAILING_SPACES)) {
                    expStr = expStr.stripTrailing();
                    actStr = actStr.stripTrailing();
                    toleranceApplied = "TRAILING_SPACES";
                }

                if (toleranceRules.contains(ToleranceRule.LEADING_ZEROS)) {
                    expStr = stripLeadingZeros(expStr);
                    actStr = stripLeadingZeros(actStr);
                    toleranceApplied = toleranceApplied != null
                            ? toleranceApplied + ",LEADING_ZEROS" : "LEADING_ZEROS";
                }

                match = expStr.equals(actStr);
            } else {
                match = expectedValue.equals(actualValue);
            }

            results.add(new ComparisonResult(fieldName, expectedValue, actualValue, match, toleranceApplied));
        }

        return results;
    }

    private String stripLeadingZeros(String value) {
        String stripped = value.replaceFirst("^0+", "");
        return stripped.isEmpty() ? "0" : stripped;
    }
}
