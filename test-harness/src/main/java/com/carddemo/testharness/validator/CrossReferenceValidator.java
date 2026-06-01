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

    /**
     * Validates that every transaction type code in the source records exists in the
     * trantype reference file.
     */
    public ValidationResult validateTransactionTypeFK(
            List<Map<String, Object>> transactionRecords, String typeCodeField,
            List<Map<String, Object>> trantypeRecords, String trantypeCodeField) {

        Set<String> validTypes = trantypeRecords.stream()
            .map(r -> String.valueOf(r.get(trantypeCodeField)))
            .collect(Collectors.toSet());

        Set<String> missing = new LinkedHashSet<>();
        for (Map<String, Object> record : transactionRecords) {
            String typeCode = String.valueOf(record.get(typeCodeField));
            if (!validTypes.contains(typeCode)) {
                missing.add(typeCode);
            }
        }

        if (missing.isEmpty()) {
            return ValidationResult.success(
                "All transaction type codes exist in trantype reference");
        }
        return ValidationResult.failure(
            missing.size() + " transaction type codes not found in trantype: " + missing);
    }

    /**
     * Validates that every (type-code, category-code) pair in the source records exists
     * in the trancatg reference file.
     */
    public ValidationResult validateTransactionCategoryFK(
            List<Map<String, Object>> transactionRecords,
            String typeCodeField, String categoryCodeField,
            List<Map<String, Object>> trancatgRecords,
            String trancatgTypeField, String trancatgCategoryField) {

        Set<String> validPairs = trancatgRecords.stream()
            .map(r -> String.valueOf(r.get(trancatgTypeField)) + "|"
                    + String.valueOf(r.get(trancatgCategoryField)))
            .collect(Collectors.toSet());

        Set<String> missing = new LinkedHashSet<>();
        for (Map<String, Object> record : transactionRecords) {
            String pair = String.valueOf(record.get(typeCodeField)) + "|"
                        + String.valueOf(record.get(categoryCodeField));
            if (!validPairs.contains(pair)) {
                missing.add(pair);
            }
        }

        if (missing.isEmpty()) {
            return ValidationResult.success(
                "All (type-code, category-code) pairs exist in trancatg reference");
        }
        return ValidationResult.failure(
            missing.size() + " (type-code, category-code) pairs not found in trancatg: " + missing);
    }

    /**
     * Validates tcatbal referential integrity:
     * - Every TRANCAT-ACCT-ID exists in acctdata
     * - Every TRANCAT-TYPE-CD exists in trantype
     * - Every (TYPE-CD, CAT-CD) exists in trancatg
     */
    public ValidationResult validateTcatbalIntegrity(
            List<Map<String, Object>> tcatbalRecords,
            String acctIdField, String typeCodeField, String catCodeField,
            List<Map<String, Object>> acctdataRecords, String acctdataIdField,
            List<Map<String, Object>> trantypeRecords, String trantypeCodeField,
            List<Map<String, Object>> trancatgRecords,
            String trancatgTypeField, String trancatgCatField) {

        Set<String> validAcctIds = acctdataRecords.stream()
            .map(r -> String.valueOf(r.get(acctdataIdField)))
            .collect(Collectors.toSet());

        Set<String> validTypeCodes = trantypeRecords.stream()
            .map(r -> String.valueOf(r.get(trantypeCodeField)))
            .collect(Collectors.toSet());

        Set<String> validCatPairs = trancatgRecords.stream()
            .map(r -> String.valueOf(r.get(trancatgTypeField)) + "|"
                    + String.valueOf(r.get(trancatgCatField)))
            .collect(Collectors.toSet());

        Set<String> missingAcctIds = new LinkedHashSet<>();
        Set<String> missingTypeCodes = new LinkedHashSet<>();
        Set<String> missingCatPairs = new LinkedHashSet<>();

        for (Map<String, Object> record : tcatbalRecords) {
            String acctId = String.valueOf(record.get(acctIdField));
            String typeCode = String.valueOf(record.get(typeCodeField));
            String catCode = String.valueOf(record.get(catCodeField));

            if (!validAcctIds.contains(acctId)) {
                missingAcctIds.add(acctId);
            }
            if (!validTypeCodes.contains(typeCode)) {
                missingTypeCodes.add(typeCode);
            }
            String pair = typeCode + "|" + catCode;
            if (!validCatPairs.contains(pair)) {
                missingCatPairs.add(pair);
            }
        }

        if (missingAcctIds.isEmpty() && missingTypeCodes.isEmpty() && missingCatPairs.isEmpty()) {
            return ValidationResult.success(
                "All tcatbal records have valid references to acctdata, trantype, and trancatg");
        }

        StringBuilder msg = new StringBuilder("Tcatbal integrity failures:");
        if (!missingAcctIds.isEmpty()) {
            msg.append(" Missing acct-ids: ").append(missingAcctIds).append(".");
        }
        if (!missingTypeCodes.isEmpty()) {
            msg.append(" Missing type-codes: ").append(missingTypeCodes).append(".");
        }
        if (!missingCatPairs.isEmpty()) {
            msg.append(" Missing (type,cat) pairs: ").append(missingCatPairs).append(".");
        }
        return ValidationResult.failure(msg.toString());
    }

    /**
     * Validates disclosure group completeness:
     * For every distinct ACCT-GROUP-ID in acctdata x every (TYPE-CD, CAT-CD) in trancatg,
     * either (GROUP-ID, TYPE-CD, CAT-CD) exists in discgrp OR ('DEFAULT', TYPE-CD, CAT-CD) exists.
     */
    public ValidationResult validateDisclosureGroupCompleteness(
            List<Map<String, Object>> acctdataRecords, String groupIdField,
            List<Map<String, Object>> trancatgRecords,
            String trancatgTypeField, String trancatgCatField,
            List<Map<String, Object>> discgrpRecords,
            String discgrpGroupField, String discgrpTypeField, String discgrpCatField) {

        Set<String> groupIds = acctdataRecords.stream()
            .map(r -> String.valueOf(r.get(groupIdField)))
            .collect(Collectors.toSet());

        Set<String> catPairs = trancatgRecords.stream()
            .map(r -> String.valueOf(r.get(trancatgTypeField)) + "|"
                    + String.valueOf(r.get(trancatgCatField)))
            .collect(Collectors.toSet());

        Set<String> discgrpKeys = discgrpRecords.stream()
            .map(r -> String.valueOf(r.get(discgrpGroupField)) + "|"
                    + String.valueOf(r.get(discgrpTypeField)) + "|"
                    + String.valueOf(r.get(discgrpCatField)))
            .collect(Collectors.toSet());

        Set<String> missing = new LinkedHashSet<>();
        for (String groupId : groupIds) {
            for (String catPair : catPairs) {
                String specificKey = groupId + "|" + catPair;
                String defaultKey = "DEFAULT|" + catPair;
                if (!discgrpKeys.contains(specificKey) && !discgrpKeys.contains(defaultKey)) {
                    missing.add("(" + groupId + ", " + catPair.replace("|", ", ") + ")");
                }
            }
        }

        if (missing.isEmpty()) {
            return ValidationResult.success(
                "All group-id x (type-cd, cat-cd) combinations have disclosure group entries");
        }
        return ValidationResult.failure(
            missing.size() + " disclosure group entries missing: " + missing);
    }
}
