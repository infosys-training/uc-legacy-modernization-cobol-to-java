package com.carddemo.testharness.validator;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CrossReferenceValidator {

    public ValidationResult validate(
            List<Map<String, Object>> sourceRecords, String sourceField,
            List<Map<String, Object>> targetRecords, String targetField) {

        Set<String> targetValues = targetRecords.stream()
            .map(r -> String.valueOf(r.get(targetField)))
            .collect(Collectors.toSet());

        Set<String> missing = new LinkedHashSet<>();
        for (Map<String, Object> record : sourceRecords) {
            String value = String.valueOf(record.get(sourceField));
            if (!targetValues.contains(value)) {
                missing.add(value);
            }
        }

        if (missing.isEmpty()) {
            return ValidationResult.success(
                "All " + sourceRecords.size() + " values in '" + sourceField +
                "' exist in target '" + targetField + "'");
        }
        return ValidationResult.failure(
            missing.size() + " values in '" + sourceField +
            "' not found in target '" + targetField + "': " + missing);
    }
}
