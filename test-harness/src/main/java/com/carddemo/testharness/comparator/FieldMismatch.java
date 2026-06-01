package com.carddemo.testharness.comparator;

public class FieldMismatch {

    private final String fieldName;
    private final Object expected;
    private final Object actual;
    private final MismatchSeverity severity;
    private final String note;

    public FieldMismatch(String fieldName, Object expected, Object actual,
                         MismatchSeverity severity, String note) {
        this.fieldName = fieldName;
        this.expected = expected;
        this.actual = actual;
        this.severity = severity;
        this.note = note;
    }

    public String getFieldName() { return fieldName; }
    public Object getExpected() { return expected; }
    public Object getActual() { return actual; }
    public MismatchSeverity getSeverity() { return severity; }
    public String getNote() { return note; }

    @Override
    public String toString() {
        return "FieldMismatch{field='" + fieldName + "', expected=" + expected +
               ", actual=" + actual + ", severity=" + severity +
               (note != null ? ", note='" + note + "'" : "") + "}";
    }
}
