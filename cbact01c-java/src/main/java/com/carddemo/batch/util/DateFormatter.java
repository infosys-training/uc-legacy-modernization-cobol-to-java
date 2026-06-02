package com.carddemo.batch.util;

/**
 * Replaces the COBDATFT assembler program for date formatting.
 *
 * Supports conversions matching CODATECN copybook:
 *   Type '1' input: YYYYMMDD
 *   Type '2' input: YYYY-MM-DD
 *   OutType '1' output: YYYY-MM-DD
 *   OutType '2' output: YYYYMMDD
 */
public final class DateFormatter {

    private DateFormatter() {}

    /**
     * Format a date string according to CODATECN conventions.
     *
     * @param inputDate the input date string (up to 20 chars from CODATECN-INP-DATE)
     * @param inputType '1' for YYYYMMDD, '2' for YYYY-MM-DD
     * @param outputType '1' for YYYY-MM-DD output, '2' for YYYYMMDD output
     * @return formatted date string padded to 20 characters (matching CODATECN-0UT-DATE)
     */
    public static String format(String inputDate, char inputType, char outputType) {
        String yyyy;
        String mm;
        String dd;

        // Parse input based on type
        if (inputType == '1') {
            // YYYYMMDD format
            yyyy = inputDate.substring(0, 4);
            mm = inputDate.substring(4, 6);
            dd = inputDate.substring(6, 8);
        } else if (inputType == '2') {
            // YYYY-MM-DD format
            yyyy = inputDate.substring(0, 4);
            mm = inputDate.substring(5, 7);
            dd = inputDate.substring(8, 10);
        } else {
            throw new IllegalArgumentException("Unsupported input type: " + inputType);
        }

        // Format output based on type
        String result;
        if (outputType == '1') {
            // YYYY-MM-DD
            result = yyyy + "-" + mm + "-" + dd;
        } else if (outputType == '2') {
            // YYYYMMDD
            result = yyyy + mm + dd;
        } else {
            throw new IllegalArgumentException("Unsupported output type: " + outputType);
        }

        // Pad to 20 characters (CODATECN-0UT-DATE is PIC X(20))
        return padRight(result, 20);
    }

    /**
     * Get the 4-character year from a YYYY-MM-DD date string.
     */
    public static String extractYear(String dateYyyyMmDd) {
        if (dateYyyyMmDd == null || dateYyyyMmDd.length() < 4) {
            return "    ";
        }
        return dateYyyyMmDd.substring(0, 4);
    }

    private static String padRight(String s, int length) {
        if (s.length() >= length) {
            return s.substring(0, length);
        }
        return s + " ".repeat(length - s.length());
    }
}
