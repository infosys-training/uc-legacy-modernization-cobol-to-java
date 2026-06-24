package com.carddemo.batch;

/**
 * Replaces the COBDATFT assembler routine for date format conversion.
 *
 * Supports the same type codes as CODATECN copybook:
 *   Type '1' = YYYYMMDD (compact)
 *   Type '2' = YYYY-MM-DD (hyphenated)
 */
public final class DateConverter {

    private DateConverter() {}

    /**
     * Convert a date string between COBOL date formats.
     *
     * @param inputDate  the input date string
     * @param inputType  '1' for YYYYMMDD, '2' for YYYY-MM-DD
     * @param outputType '1' for YYYY-MM-DD, '2' for YYYYMMDD
     * @return the converted date string, or the input unchanged if conversion fails
     */
    public static String convert(String inputDate, char inputType, char outputType) {
        String yyyy, mm, dd;

        if (inputType == '2') {
            // YYYY-MM-DD → extract parts
            if (inputDate.length() < 10) {
                return inputDate;
            }
            yyyy = inputDate.substring(0, 4);
            mm = inputDate.substring(5, 7);
            dd = inputDate.substring(8, 10);
        } else if (inputType == '1') {
            // YYYYMMDD → extract parts
            if (inputDate.length() < 8) {
                return inputDate;
            }
            yyyy = inputDate.substring(0, 4);
            mm = inputDate.substring(4, 6);
            dd = inputDate.substring(6, 8);
        } else {
            return inputDate;
        }

        if (outputType == '1') {
            // Output YYYY-MM-DD
            return yyyy + "-" + mm + "-" + dd;
        } else if (outputType == '2') {
            // Output YYYYMMDD
            return yyyy + mm + dd;
        }

        return inputDate;
    }
}
