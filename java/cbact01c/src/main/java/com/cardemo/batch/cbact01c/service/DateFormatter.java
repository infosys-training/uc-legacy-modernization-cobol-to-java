package com.cardemo.batch.cbact01c.service;

/**
 * Java equivalent of the COBOL COBDATFT assembler routine.
 * Converts date strings between YYYY-MM-DD and YYYYMMDD formats
 * as specified by the CODATECN copybook.
 *
 * <p>Type codes:
 * <ul>
 *   <li>Type 1: YYYYMMDD (compact)</li>
 *   <li>Type 2: YYYY-MM-DD (with separators)</li>
 * </ul>
 */
public final class DateFormatter {

    private DateFormatter() {}

    /**
     * Converts a date from one COBOL format to another.
     *
     * @param inputDate  the date string to convert
     * @param inputType  "1" for YYYYMMDD, "2" for YYYY-MM-DD
     * @param outputType "1" for YYYY-MM-DD output, "2" for YYYYMMDD output
     * @return the reformatted date string
     */
    public static String formatDate(String inputDate, String inputType, String outputType) {
        String yyyy;
        String mm;
        String dd;

        if ("1".equals(inputType)) {
            // YYYYMMDD input
            yyyy = inputDate.substring(0, 4);
            mm = inputDate.substring(4, 6);
            dd = inputDate.substring(6, 8);
        } else if ("2".equals(inputType)) {
            // YYYY-MM-DD input
            yyyy = inputDate.substring(0, 4);
            mm = inputDate.substring(5, 7);
            dd = inputDate.substring(8, 10);
        } else {
            throw new IllegalArgumentException("Unknown input type: " + inputType);
        }

        if ("1".equals(outputType)) {
            // YYYY-MM-DD output
            return yyyy + "-" + mm + "-" + dd;
        } else if ("2".equals(outputType)) {
            // YYYYMMDD output
            return yyyy + mm + dd;
        } else {
            throw new IllegalArgumentException("Unknown output type: " + outputType);
        }
    }

    /**
     * Extracts the 4-digit year from a YYYY-MM-DD date string.
     */
    public static String extractYear(String dateYyyyMmDd) {
        return dateYyyyMmDd.substring(0, 4);
    }
}
