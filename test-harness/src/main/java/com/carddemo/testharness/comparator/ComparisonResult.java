package com.carddemo.testharness.comparator;

import java.util.List;

public class ComparisonResult {

    private final String fieldName;
    private final Object expected;
    private final Object actual;
    private final boolean match;
    private final String toleranceApplied;
    private final MismatchSeverity severity;
    private final String note;

    public ComparisonResult(String fieldName, Object expected, Object actual,
                            boolean match, String toleranceApplied) {
        this(fieldName, expected, actual, match, toleranceApplied,
             match ? null : MismatchSeverity.ERROR, null);
    }

    public ComparisonResult(String fieldName, Object expected, Object actual,
                            boolean match, String toleranceApplied,
                            MismatchSeverity severity, String note) {
        this.fieldName = fieldName;
        this.expected = expected;
        this.actual = actual;
        this.match = match;
        this.toleranceApplied = toleranceApplied;
        this.severity = severity;
        this.note = note;
    }

    public String getFieldName() { return fieldName; }
    public Object getExpected() { return expected; }
    public Object getActual() { return actual; }
    public boolean isMatch() { return match; }
    public String getToleranceApplied() { return toleranceApplied; }
    public MismatchSeverity getSeverity() { return severity; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        return "ComparisonResult{field='" + fieldName + "', expected=" + expected +
               ", actual=" + actual + ", match=" + match +
               (toleranceApplied != null ? ", tolerance=" + toleranceApplied : "") +
               (severity != null ? ", severity=" + severity : "") +
               (note != null ? ", note='" + note + "'" : "") + "}";
    }

    public static int errorCount(List<ComparisonResult> results) {
        return (int) results.stream()
            .filter(r -> r.getSeverity() == MismatchSeverity.ERROR)
            .count();
    }

    public static int warningCount(List<ComparisonResult> results) {
        return (int) results.stream()
            .filter(r -> r.getSeverity() == MismatchSeverity.WARNING)
            .count();
    }

    public static int infoCount(List<ComparisonResult> results) {
        return (int) results.stream()
            .filter(r -> r.getSeverity() == MismatchSeverity.INFO)
            .count();
    }
}
