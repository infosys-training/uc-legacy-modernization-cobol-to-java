package com.carddemo.util;

/**
 * Replaces the COBDATFT assembler program called via:
 *   CALL 'COBDATFT' USING CODATECN-REC
 *
 * The COBOL code sets:
 *   CODATECN-TYPE    = '2'  (input format: YYYY-MM-DD)
 *   CODATECN-OUTTYPE = '2'  (output format: YYYYMMDD)
 *   CODATECN-INP-DATE = the reissue date
 *
 * Then reads back CODATECN-0UT-DATE as the reformatted date.
 *
 * Supported conversions:
 *   Type 1 input: YYYYMMDD    → output depends on outType
 *   Type 2 input: YYYY-MM-DD  → output depends on outType
 *   OutType 1: YYYY-MM-DD
 *   OutType 2: YYYYMMDD
 */
public final class CobolDateFormatter {

    private CobolDateFormatter() {}

    public static String formatDate(String inputDate, char inputType, char outputType) {
        if (inputDate == null || inputDate.isBlank()) {
            return inputDate;
        }

        String yyyy;
        String mm;
        String dd;

        String trimmed = inputDate.trim();

        if (inputType == '1') {
            // YYYYMMDD
            if (trimmed.length() < 8) {
                return trimmed;
            }
            yyyy = trimmed.substring(0, 4);
            mm = trimmed.substring(4, 6);
            dd = trimmed.substring(6, 8);
        } else if (inputType == '2') {
            // YYYY-MM-DD
            if (trimmed.length() < 10) {
                return trimmed;
            }
            yyyy = trimmed.substring(0, 4);
            mm = trimmed.substring(5, 7);
            dd = trimmed.substring(8, 10);
        } else {
            return trimmed;
        }

        if (outputType == '1') {
            return yyyy + "-" + mm + "-" + dd;
        } else if (outputType == '2') {
            return yyyy + mm + dd;
        }
        return trimmed;
    }
}
