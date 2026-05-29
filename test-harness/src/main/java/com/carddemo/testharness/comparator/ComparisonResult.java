package com.carddemo.testharness.comparator;

public class ComparisonResult {

    private final String fieldName;
    private final Object expected;
    private final Object actual;
    private final boolean match;
    private final String toleranceApplied;

    public ComparisonResult(String fieldName, Object expected, Object actual,
                            boolean match, String toleranceApplied) {
        this.fieldName = fieldName;
        this.expected = expected;
        this.actual = actual;
        this.match = match;
        this.toleranceApplied = toleranceApplied;
    }

    public String getFieldName() { return fieldName; }
    public Object getExpected() { return expected; }
    public Object getActual() { return actual; }
    public boolean isMatch() { return match; }
    public String getToleranceApplied() { return toleranceApplied; }

    @Override
    public String toString() {
        return "ComparisonResult{field='" + fieldName + "', expected=" + expected +
               ", actual=" + actual + ", match=" + match +
               (toleranceApplied != null ? ", tolerance=" + toleranceApplied : "") + "}";
    }
}
