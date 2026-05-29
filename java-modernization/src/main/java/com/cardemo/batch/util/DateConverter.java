package com.cardemo.batch.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Replaces the COBOL subroutine {@code COBDATFT} that converts between
 * date formats described in the {@code CODATECN} copybook.
 *
 * <p>Supported conversions:
 * <ul>
 *   <li>Type 1 (YYYYMMDD)  &rarr; Type 1 (YYYY-MM-DD) or Type 2 (YYYYMMDD)</li>
 *   <li>Type 2 (YYYY-MM-DD) &rarr; Type 1 (YYYY-MM-DD) or Type 2 (YYYYMMDD)</li>
 * </ul>
 */
public final class DateConverter {

    private static final DateTimeFormatter ISO_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter BASIC_DATE = DateTimeFormatter.BASIC_ISO_DATE;

    private DateConverter() {}

    /**
     * Converts a date string between COBOL date formats.
     *
     * @param inputDate  the date string to convert
     * @param inputType  "1" for YYYYMMDD, "2" for YYYY-MM-DD
     * @param outputType "1" for YYYY-MM-DD, "2" for YYYYMMDD
     * @return the converted date string, or the original if parsing fails
     */
    public static String convert(String inputDate, String inputType, String outputType) {
        if (inputDate == null || inputDate.isBlank()) {
            return inputDate;
        }

        LocalDate date = parseInput(inputDate.trim(), inputType);

        return switch (outputType) {
            case "1" -> date.format(ISO_DATE);
            case "2" -> date.format(BASIC_DATE);
            default -> throw new IllegalArgumentException(
                    "Unsupported output type: " + outputType);
        };
    }

    private static LocalDate parseInput(String input, String type) {
        try {
            return switch (type) {
                case "1" -> LocalDate.parse(input, BASIC_DATE);
                case "2" -> LocalDate.parse(input, ISO_DATE);
                default -> throw new IllegalArgumentException(
                        "Unsupported input type: " + type);
            };
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(
                    "Cannot parse date '" + input + "' with type " + type, e);
        }
    }
}
