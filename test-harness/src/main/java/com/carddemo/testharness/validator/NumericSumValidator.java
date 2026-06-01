package com.carddemo.testharness.validator;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class NumericSumValidator {

    public ValidationResult validate(
            List<Map<String, Object>> records,
            String fieldName,
            BigDecimal expectedSum) {

        BigDecimal actualSum = BigDecimal.ZERO;
        for (Map<String, Object> record : records) {
            Object value = record.get(fieldName);
            if (value instanceof BigDecimal) {
                actualSum = actualSum.add((BigDecimal) value);
            } else if (value != null) {
                try {
                    actualSum = actualSum.add(new BigDecimal(value.toString()));
                } catch (NumberFormatException e) {
                    return ValidationResult.failure(
                        "Non-numeric value for field '" + fieldName + "': " + value);
                }
            }
        }

        if (actualSum.compareTo(expectedSum) == 0) {
            return ValidationResult.success(
                "Sum of '" + fieldName + "' matches: " + actualSum);
        }
        return ValidationResult.failure(
            "Sum mismatch for '" + fieldName + "': expected=" + expectedSum + ", actual=" + actualSum);
    }
}
