package com.carddemo.testharness.comparator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ToleranceRules {

    private boolean decodePacked = true;
    private boolean normalizeDateFormats = true;
    private List<String> dateFieldNames = new ArrayList<>();
    private boolean allowPartialArrays = true;
    private BigDecimal numericTolerance = BigDecimal.ZERO;

    public boolean isDecodePacked() { return decodePacked; }
    public void setDecodePacked(boolean decodePacked) { this.decodePacked = decodePacked; }

    public boolean isNormalizeDateFormats() { return normalizeDateFormats; }
    public void setNormalizeDateFormats(boolean normalizeDateFormats) { this.normalizeDateFormats = normalizeDateFormats; }

    public List<String> getDateFieldNames() { return dateFieldNames; }
    public void setDateFieldNames(List<String> dateFieldNames) { this.dateFieldNames = dateFieldNames; }

    public boolean isAllowPartialArrays() { return allowPartialArrays; }
    public void setAllowPartialArrays(boolean allowPartialArrays) { this.allowPartialArrays = allowPartialArrays; }

    public BigDecimal getNumericTolerance() { return numericTolerance; }
    public void setNumericTolerance(BigDecimal numericTolerance) { this.numericTolerance = numericTolerance; }

    public boolean isDateField(String fieldName) {
        if (dateFieldNames.contains(fieldName)) {
            return true;
        }
        return fieldName != null && fieldName.toUpperCase().contains("DATE");
    }
}
