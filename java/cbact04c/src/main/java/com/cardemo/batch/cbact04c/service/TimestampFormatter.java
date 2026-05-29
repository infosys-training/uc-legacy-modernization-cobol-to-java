package com.cardemo.batch.cbact04c.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * DB2-format timestamp utility.
 * Format: YYYY-MM-DD-HH.MM.SS.HH0000
 *
 * Mirrors the COBOL Z-GET-DB2-FORMAT-TIMESTAMP paragraph.
 */
public final class TimestampFormatter {

    private static final DateTimeFormatter DB2_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd-HH.mm.ss.SS'0000'");

    private TimestampFormatter() {}

    /**
     * Formats a LocalDateTime as a DB2 timestamp string.
     *
     * @param dateTime the date/time to format
     * @return formatted string like "2022-06-10-14.30.45.120000"
     */
    public static String formatDb2Timestamp(LocalDateTime dateTime) {
        return dateTime.format(DB2_FORMAT);
    }

    /**
     * Returns the current timestamp in DB2 format.
     */
    public static String currentDb2Timestamp() {
        return formatDb2Timestamp(LocalDateTime.now());
    }
}
