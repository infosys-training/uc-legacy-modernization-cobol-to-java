package com.carddemo.batch.cbact01c.util;

/**
 * Replaces the COBDATFT assembler program called by CBACT01C.
 *
 * Converts between date formats as specified by CODATECN copybook:
 * <pre>
 *   Type 1 input:  YYYYMMDD      →  Type 1 output: YYYY-MM-DD
 *   Type 2 input:  YYYY-MM-DD    →  Type 2 output: YYYYMMDD
 * </pre>
 *
 * CBACT01C uses: input type=2 (YYYY-MM-DD), output type=2 (YYYYMMDD)
 * to convert the account reissue date.
 */
public final class DateFormatter {

    private DateFormatter() {
    }

    /**
     * Converts YYYY-MM-DD to YYYYMMDD (strips dashes).
     * Equivalent to COBDATFT with input type=2, output type=2.
     */
    public static String toCompactDate(String hyphenatedDate) {
        if (hyphenatedDate == null || hyphenatedDate.isBlank()) {
            return "          ";
        }
        String trimmed = hyphenatedDate.trim();
        if (trimmed.length() >= 10 && trimmed.charAt(4) == '-' && trimmed.charAt(7) == '-') {
            String yyyy = trimmed.substring(0, 4);
            String mm = trimmed.substring(5, 7);
            String dd = trimmed.substring(8, 10);
            return padRight(yyyy + mm + dd, 10);
        }
        return padRight(trimmed, 10);
    }

    /**
     * Converts YYYYMMDD to YYYY-MM-DD (inserts dashes).
     * Equivalent to COBDATFT with input type=1, output type=1.
     */
    public static String toHyphenatedDate(String compactDate) {
        if (compactDate == null || compactDate.isBlank()) {
            return "          ";
        }
        String trimmed = compactDate.trim();
        if (trimmed.length() >= 8) {
            String yyyy = trimmed.substring(0, 4);
            String mm = trimmed.substring(4, 6);
            String dd = trimmed.substring(6, 8);
            return yyyy + "-" + mm + "-" + dd;
        }
        return padRight(trimmed, 10);
    }

    /**
     * Extracts the YYYY portion from a YYYY-MM-DD date.
     * Used for VBR-REC2's VB2-ACCT-REISSUE-YYYY field.
     */
    public static String extractYear(String hyphenatedDate) {
        if (hyphenatedDate == null || hyphenatedDate.length() < 4) {
            return "    ";
        }
        return hyphenatedDate.substring(0, 4);
    }

    private static String padRight(String s, int width) {
        if (s.length() >= width) {
            return s.substring(0, width);
        }
        return s + " ".repeat(width - s.length());
    }
}
