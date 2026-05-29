package com.carddemo.testharness.validator;

import java.util.List;

public class RecordCountValidator {

    public ValidationResult validate(List<?> dataset1, List<?> dataset2) {
        int count1 = dataset1.size();
        int count2 = dataset2.size();
        if (count1 == count2) {
            return ValidationResult.success(
                "Record counts match: " + count1);
        }
        return ValidationResult.failure(
            "Record count mismatch: dataset1=" + count1 + ", dataset2=" + count2);
    }

    public ValidationResult validate(List<?> dataset, int expectedCount) {
        int actualCount = dataset.size();
        if (actualCount == expectedCount) {
            return ValidationResult.success(
                "Record count matches expected: " + expectedCount);
        }
        return ValidationResult.failure(
            "Record count mismatch: expected=" + expectedCount + ", actual=" + actualCount);
    }
}
