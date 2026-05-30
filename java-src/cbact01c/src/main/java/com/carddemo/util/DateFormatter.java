package com.carddemo.util;

/**
 * Replaces the COBOL assembler program COBDATFT.
 *
 * Converts dates between formats based on the CODATECN copybook:
 * <ul>
 *   <li>Type 1 input: YYYYMMDD</li>
 *   <li>Type 2 input: YYYY-MM-DD</li>
 *   <li>Outtype 1 output: YYYY-MM-DD</li>
 *   <li>Outtype 2 output: YYYYMMDD</li>
 * </ul>
 *
 * In CBACT01C, the call is: type=2 (YYYY-MM-DD in), outtype=2 (YYYYMMDD out).
 * This strips hyphens from the date.
 */
public final class DateFormatter {

    private DateFormatter() {}

    /**
     * Format a date string according to the requested input/output types.
     *
     * @param inputDate the date string (up to 20 chars, padded)
     * @param inputType "1" for YYYYMMDD, "2" for YYYY-MM-DD
     * @param outputType "1" for YYYY-MM-DD, "2" for YYYYMMDD
     * @return the reformatted date string (up to 20 chars)
     */
    public static String format(String inputDate, String inputType, String outputType) {
        if (inputDate == null || inputDate.isBlank()) {
            return inputDate;
        }

        String yyyy;
        String mm;
        String dd;

        if ("1".equals(inputType)) {
            // YYYYMMDD
            yyyy = safeSubstring(inputDate, 0, 4);
            mm = safeSubstring(inputDate, 4, 6);
            dd = safeSubstring(inputDate, 6, 8);
        } else {
            // YYYY-MM-DD (type 2)
            yyyy = safeSubstring(inputDate, 0, 4);
            mm = safeSubstring(inputDate, 5, 7);
            dd = safeSubstring(inputDate, 8, 10);
        }

        if ("1".equals(outputType)) {
            return yyyy + "-" + mm + "-" + dd;
        } else {
            // output type 2: YYYYMMDD
            return yyyy + mm + dd;
        }
    }

    private static String safeSubstring(String s, int start, int end) {
        if (s.length() < end) {
            return s.substring(start);
        }
        return s.substring(start, end);
    }
}
