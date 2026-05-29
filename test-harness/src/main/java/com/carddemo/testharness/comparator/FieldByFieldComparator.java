package com.carddemo.testharness.comparator;

import com.carddemo.testharness.parser.PackedDecimalParser;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FieldByFieldComparator {

    private static final List<DateTimeFormatter> DATE_FORMATS = List.of(
        DateTimeFormatter.ofPattern("yyyy-MM-dd"),
        DateTimeFormatter.ofPattern("yyyyMMdd"),
        DateTimeFormatter.ofPattern("MM/dd/yyyy")
    );

    private int decimalScale = 2;
    private ToleranceRules toleranceRulesConfig = new ToleranceRules();

    public void setDecimalScale(int decimalScale) {
        this.decimalScale = decimalScale;
    }

    public void setToleranceRulesConfig(ToleranceRules toleranceRulesConfig) {
        this.toleranceRulesConfig = toleranceRulesConfig;
    }

    public ToleranceRules getToleranceRulesConfig() {
        return toleranceRulesConfig;
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

            ComparisonResult result = compareField(fieldName, expectedValue, actualValue, toleranceRules);
            results.add(result);
        }

        return results;
    }

    private ComparisonResult compareField(String fieldName, Object expectedValue,
                                          Object actualValue, Set<ToleranceRule> toleranceRules) {
        if (expectedValue == null && actualValue == null) {
            return new ComparisonResult(fieldName, null, null, true, null);
        }
        if (expectedValue == null || actualValue == null) {
            return new ComparisonResult(fieldName, expectedValue, actualValue, false, null,
                MismatchSeverity.ERROR, "Missing value");
        }

        // COMP-3 packed decimal handling
        if (toleranceRules.contains(ToleranceRule.DECODE_PACKED) && toleranceRulesConfig.isDecodePacked()) {
            if (expectedValue instanceof byte[] || actualValue instanceof byte[]) {
                return comparePackedDecimal(fieldName, expectedValue, actualValue);
            }
        }

        // Date normalization
        if (toleranceRules.contains(ToleranceRule.NORMALIZE_DATE_FORMATS)
                && toleranceRulesConfig.isNormalizeDateFormats()
                && toleranceRulesConfig.isDateField(fieldName)) {
            if (expectedValue instanceof String && actualValue instanceof String) {
                ComparisonResult dateResult = compareDateValues(fieldName,
                    (String) expectedValue, (String) actualValue);
                if (dateResult != null) {
                    return dateResult;
                }
            }
        }

        // OCCURS array comparison
        if (expectedValue instanceof List && actualValue instanceof List) {
            return compareArrays(fieldName, (List<?>) expectedValue, (List<?>) actualValue, toleranceRules);
        }

        // BigDecimal comparison
        if (expectedValue instanceof BigDecimal && actualValue instanceof BigDecimal) {
            return compareBigDecimals(fieldName, (BigDecimal) expectedValue,
                (BigDecimal) actualValue, toleranceRules);
        }

        // String comparison
        if (expectedValue instanceof String && actualValue instanceof String) {
            return compareStrings(fieldName, (String) expectedValue,
                (String) actualValue, toleranceRules);
        }

        // Fallback
        boolean match = expectedValue.equals(actualValue);
        return new ComparisonResult(fieldName, expectedValue, actualValue, match, null);
    }

    private ComparisonResult comparePackedDecimal(String fieldName, Object expectedValue, Object actualValue) {
        BigDecimal expDecimal = toDecimalFromPacked(expectedValue);
        BigDecimal actDecimal = toDecimalFromPacked(actualValue);

        if (expDecimal == null || actDecimal == null) {
            return new ComparisonResult(fieldName, expectedValue, actualValue, false, null,
                MismatchSeverity.ERROR, "Cannot decode packed decimal");
        }

        BigDecimal diff = expDecimal.subtract(actDecimal).abs();
        boolean withinTolerance = diff.compareTo(toleranceRulesConfig.getNumericTolerance()) <= 0;
        return new ComparisonResult(fieldName, expDecimal, actDecimal, withinTolerance,
            "DECODE_PACKED", withinTolerance ? null : MismatchSeverity.ERROR, null);
    }

    private BigDecimal toDecimalFromPacked(Object value) {
        if (value instanceof BigDecimal) {
            return (BigDecimal) value;
        }
        if (value instanceof byte[]) {
            return PackedDecimalParser.decode((byte[]) value, 2);
        }
        if (value instanceof String) {
            try {
                return new BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    private ComparisonResult compareDateValues(String fieldName, String expected, String actual) {
        LocalDate expDate = tryParseDate(expected);
        LocalDate actDate = tryParseDate(actual);

        if (expDate != null && actDate != null) {
            if (expDate.equals(actDate)) {
                boolean formatsDiffer = !expected.equals(actual);
                if (formatsDiffer) {
                    return new ComparisonResult(fieldName, expected, actual, false,
                        "NORMALIZE_DATE_FORMATS", MismatchSeverity.WARNING,
                        "Date format differs but semantic value matches");
                }
                return new ComparisonResult(fieldName, expected, actual, true, "NORMALIZE_DATE_FORMATS");
            } else {
                return new ComparisonResult(fieldName, expected, actual, false, null,
                    MismatchSeverity.ERROR, "Date values differ");
            }
        }
        return null;
    }

    private LocalDate tryParseDate(String value) {
        for (DateTimeFormatter fmt : DATE_FORMATS) {
            try {
                return LocalDate.parse(value, fmt);
            } catch (DateTimeParseException e) {
                // try next format
            }
        }
        return null;
    }

    private ComparisonResult compareArrays(String fieldName, List<?> expected,
                                           List<?> actual, Set<ToleranceRule> toleranceRules) {
        int minLen = Math.min(expected.size(), actual.size());
        boolean allMatch = true;
        StringBuilder notes = new StringBuilder();

        for (int i = 0; i < minLen; i++) {
            if (!elementsEqual(expected.get(i), actual.get(i))) {
                allMatch = false;
                notes.append("Element ").append(i).append(" differs. ");
            }
        }

        if (expected.size() > actual.size()) {
            boolean extrasAreDefault = true;
            for (int i = actual.size(); i < expected.size(); i++) {
                if (!isZeroOrSpaces(expected.get(i))) {
                    extrasAreDefault = false;
                    break;
                }
            }
            if (toleranceRules.contains(ToleranceRule.ALLOW_PARTIAL_ARRAYS)
                    && toleranceRulesConfig.isAllowPartialArrays()) {
                if (extrasAreDefault) {
                    return new ComparisonResult(fieldName, expected, actual, false,
                        "ALLOW_PARTIAL_ARRAYS", MismatchSeverity.WARNING,
                        "COBOL has " + expected.size() + " elements, Java has " + actual.size()
                            + "; extra COBOL elements are zeros/spaces");
                } else {
                    return new ComparisonResult(fieldName, expected, actual, false, null,
                        MismatchSeverity.ERROR,
                        "COBOL has " + expected.size() + " elements, Java has " + actual.size()
                            + "; extra COBOL elements contain non-default values");
                }
            }
            allMatch = false;
        } else if (actual.size() > expected.size()) {
            allMatch = false;
        }

        if (!allMatch && notes.length() > 0) {
            return new ComparisonResult(fieldName, expected, actual, false, null,
                MismatchSeverity.ERROR, notes.toString().trim());
        }
        return new ComparisonResult(fieldName, expected, actual, allMatch, null);
    }

    private boolean elementsEqual(Object a, Object b) {
        if (a == null && b == null) return true;
        if (a == null || b == null) return false;
        if (a instanceof BigDecimal && b instanceof BigDecimal) {
            return ((BigDecimal) a).compareTo((BigDecimal) b) == 0;
        }
        return a.equals(b);
    }

    private boolean isZeroOrSpaces(Object value) {
        if (value == null) return true;
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value).compareTo(BigDecimal.ZERO) == 0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue() == 0.0;
        }
        if (value instanceof String) {
            String s = (String) value;
            return s.trim().isEmpty() || s.equals("0") || s.matches("^0+$");
        }
        return false;
    }

    private ComparisonResult compareBigDecimals(String fieldName, BigDecimal expected,
                                                BigDecimal actual, Set<ToleranceRule> toleranceRules) {
        String toleranceApplied = null;
        boolean match;

        if (toleranceRules.contains(ToleranceRule.DECIMAL_PRECISION)) {
            BigDecimal exp = expected.setScale(decimalScale, RoundingMode.HALF_UP);
            BigDecimal act = actual.setScale(decimalScale, RoundingMode.HALF_UP);
            match = exp.compareTo(act) == 0;
            toleranceApplied = "DECIMAL_PRECISION(scale=" + decimalScale + ")";
        } else {
            match = expected.compareTo(actual) == 0;
        }

        if (!match) {
            BigDecimal diff = expected.subtract(actual).abs();
            if (diff.compareTo(toleranceRulesConfig.getNumericTolerance()) <= 0) {
                return new ComparisonResult(fieldName, expected, actual, true,
                    "NUMERIC_TOLERANCE(" + toleranceRulesConfig.getNumericTolerance() + ")");
            }
            return new ComparisonResult(fieldName, expected, actual, false, toleranceApplied,
                MismatchSeverity.ERROR, "Numeric value differs beyond tolerance");
        }

        return new ComparisonResult(fieldName, expected, actual, true, toleranceApplied);
    }

    private ComparisonResult compareStrings(String fieldName, String expected,
                                            String actual, Set<ToleranceRule> toleranceRules) {
        String expStr = expected;
        String actStr = actual;
        String toleranceApplied = null;
        boolean hadTrailingSpaceDiff = false;
        boolean hadLeadingZeroDiff = false;

        if (toleranceRules.contains(ToleranceRule.TRAILING_SPACES)) {
            String expTrimmed = expStr.stripTrailing();
            String actTrimmed = actStr.stripTrailing();
            if (!expStr.equals(actStr) && expTrimmed.equals(actTrimmed)) {
                hadTrailingSpaceDiff = true;
            }
            expStr = expTrimmed;
            actStr = actTrimmed;
            toleranceApplied = "TRAILING_SPACES";
        }

        if (toleranceRules.contains(ToleranceRule.LEADING_ZEROS)) {
            String expStripped = stripLeadingZeros(expStr);
            String actStripped = stripLeadingZeros(actStr);
            if (!expStr.equals(actStr) && expStripped.equals(actStripped)) {
                hadLeadingZeroDiff = true;
            }
            expStr = expStripped;
            actStr = actStripped;
            toleranceApplied = toleranceApplied != null
                    ? toleranceApplied + ",LEADING_ZEROS" : "LEADING_ZEROS";
        }

        boolean match = expStr.equals(actStr);
        if (match && (hadTrailingSpaceDiff || hadLeadingZeroDiff)) {
            String note = hadTrailingSpaceDiff && hadLeadingZeroDiff
                ? "Trailing space and leading zero difference"
                : hadTrailingSpaceDiff ? "Trailing space difference"
                : "Leading zero difference";
            return new ComparisonResult(fieldName, expected, actual, true,
                toleranceApplied, MismatchSeverity.WARNING, note);
        }
        return new ComparisonResult(fieldName, expected, actual, match, toleranceApplied);
    }

    /**
     * Compares variable-length records that may have a 4-byte RDW prefix.
     * RDW: first 2 bytes = record length (big-endian), next 2 bytes = zeros.
     */
    public List<List<ComparisonResult>> compareVariableLengthRecords(
            List<byte[]> expectedRecords, List<byte[]> actualRecords,
            Set<ToleranceRule> toleranceRules,
            java.util.function.Function<byte[], Map<String, Object>> fieldExtractor) {

        List<List<ComparisonResult>> allResults = new ArrayList<>();
        int count = Math.min(expectedRecords.size(), actualRecords.size());

        for (int i = 0; i < count; i++) {
            byte[] expRaw = stripRdw(expectedRecords.get(i));
            byte[] actRaw = stripRdw(actualRecords.get(i));

            Map<String, Object> expFields = fieldExtractor.apply(expRaw);
            Map<String, Object> actFields = fieldExtractor.apply(actRaw);

            allResults.add(compare(expFields, actFields, toleranceRules));
        }

        return allResults;
    }

    /**
     * Detects and strips a 4-byte RDW (Record Descriptor Word) prefix if present.
     * RDW: bytes[0..1] = record length (big-endian), bytes[2..3] = 0x0000.
     */
    public static byte[] stripRdw(byte[] record) {
        if (record.length >= 4) {
            int declaredLen = ((record[0] & 0xFF) << 8) | (record[1] & 0xFF);
            boolean zerosFollow = record[2] == 0 && record[3] == 0;
            if (zerosFollow && declaredLen == record.length) {
                return Arrays.copyOfRange(record, 4, record.length);
            }
        }
        return record;
    }

    private String stripLeadingZeros(String value) {
        String stripped = value.replaceFirst("^0+", "");
        return stripped.isEmpty() ? "0" : stripped;
    }
}
