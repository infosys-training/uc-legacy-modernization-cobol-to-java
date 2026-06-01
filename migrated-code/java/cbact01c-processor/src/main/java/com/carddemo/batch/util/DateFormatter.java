package com.carddemo.batch.util;

/**
 * Replaces COBDATFT assembler subroutine for date format conversion.
 * Mirrors the CODATECN copybook record layout and COBDATFT.asm logic.
 */
public class DateFormatter {

    /**
     * Converts a date string between COBOL date formats.
     * Only same-type conversions are supported, mirroring the original COBDATFT
     * assembler behaviour which only handled these two cases:
     * <ul>
     *   <li>('2','2') — strips dashes: YYYY-MM-DD → YYYYMMDD</li>
     *   <li>('1','1') — adds dashes: YYYYMMDD → YYYY-MM-DD</li>
     * </ul>
     * Cross-type conversions (e.g., inputType='1', outputType='2') are not
     * supported and will throw {@link IllegalArgumentException}.
     *
     * @param input      the date string to convert
     * @param inputType  '2' for YYYY-MM-DD, '1' for YYYYMMDD
     * @param outputType must match inputType
     * @return the converted date string
     * @throws IllegalArgumentException if the type combination is unsupported
     */
    public String convertDate(String input, char inputType, char outputType) {
        if (inputType == '2' && outputType == '2') {
            // YYYY-MM-DD -> YYYYMMDD (strip dashes)
            return input.substring(0, 4) + input.substring(5, 7) + input.substring(8, 10);
        } else if (inputType == '1' && outputType == '1') {
            // YYYYMMDD -> YYYY-MM-DD (add dashes)
            return input.substring(0, 4) + "-" + input.substring(4, 6) + "-" + input.substring(6, 8);
        } else {
            throw new IllegalArgumentException("INVALID INPUT");
        }
    }
}
