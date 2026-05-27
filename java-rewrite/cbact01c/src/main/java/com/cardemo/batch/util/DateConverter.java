package com.cardemo.batch.util;

/**
 * Replaces the COBDATFT assembler subroutine for date format conversion.
 * <p>
 * Supports the same two conversion paths used in CBACT01C:
 * <ul>
 *   <li>Type 1 input (YYYYMMDD) &rarr; Type 1 output (YYYY-MM-DD)</li>
 *   <li>Type 2 input (YYYY-MM-DD) &rarr; Type 2 output (YYYYMMDD)</li>
 * </ul>
 */
public final class DateConverter {

    private DateConverter() {}

    /**
     * Converts {@code YYYY-MM-DD} to {@code YYYYMMDD}.
     * Matches COBDATFT behaviour for input-type&nbsp;2, output-type&nbsp;2.
     */
    public static String hyphenatedToCompact(String date) {
        if (date == null || date.length() < 10) {
            throw new IllegalArgumentException("Invalid YYYY-MM-DD date: " + date);
        }
        return date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10);
    }

    /**
     * Converts {@code YYYYMMDD} to {@code YYYY-MM-DD}.
     * Matches COBDATFT behaviour for input-type&nbsp;1, output-type&nbsp;1.
     */
    public static String compactToHyphenated(String date) {
        if (date == null || date.length() < 8) {
            throw new IllegalArgumentException("Invalid YYYYMMDD date: " + date);
        }
        return date.substring(0, 4) + "-" + date.substring(4, 6) + "-" + date.substring(6, 8);
    }

    /**
     * Extracts the four-digit year from a {@code YYYY-MM-DD} date string.
     */
    public static String extractYear(String hyphenatedDate) {
        if (hyphenatedDate == null || hyphenatedDate.length() < 4) {
            throw new IllegalArgumentException("Invalid date for year extraction: " + hyphenatedDate);
        }
        return hyphenatedDate.substring(0, 4);
    }
}
