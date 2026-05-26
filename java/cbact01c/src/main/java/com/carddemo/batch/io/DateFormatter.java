package com.carddemo.batch.io;

/**
 * Replaces the COBDATFT assembler subroutine used by CBACT01C.
 *
 * The COBOL program calls COBDATFT with type=2 (YYYY-MM-DD input)
 * and outtype=2 (YYYYMMDD output). This class provides equivalent
 * date-format conversion.
 */
public final class DateFormatter {

    private DateFormatter() {}

    /**
     * Converts a date from YYYY-MM-DD format to YYYYMMDD format.
     *
     * @param yyyyMmDd a date string like "2025-05-20"
     * @return the same date as "20250520", or the original string
     *         padded/truncated to 20 chars if the format is unexpected
     */
    public static String toCompactDate(String yyyyMmDd) {
        if (yyyyMmDd == null || yyyyMmDd.length() < 10) {
            return yyyyMmDd;
        }
        String trimmed = yyyyMmDd.trim();
        if (trimmed.length() >= 10 && trimmed.charAt(4) == '-' && trimmed.charAt(7) == '-') {
            return trimmed.substring(0, 4)
                    + trimmed.substring(5, 7)
                    + trimmed.substring(8, 10);
        }
        return yyyyMmDd;
    }

    /**
     * Extracts the 4-digit year from a YYYY-MM-DD date string.
     */
    public static String extractYear(String yyyyMmDd) {
        if (yyyyMmDd == null || yyyyMmDd.length() < 4) {
            return "    ";
        }
        return yyyyMmDd.substring(0, 4);
    }
}
