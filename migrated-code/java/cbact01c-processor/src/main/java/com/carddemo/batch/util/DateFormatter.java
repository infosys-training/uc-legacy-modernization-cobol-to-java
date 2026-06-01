package com.carddemo.batch.util;

/**
 * Replaces COBDATFT assembler subroutine for date format conversion.
 * Mirrors the CODATECN copybook record layout and COBDATFT.asm logic.
 */
public class DateFormatter {

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
