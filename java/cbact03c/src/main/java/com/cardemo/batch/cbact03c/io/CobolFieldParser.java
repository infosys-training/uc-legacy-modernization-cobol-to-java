package com.cardemo.batch.cbact03c.io;

import java.util.Map;

/**
 * Utility for parsing COBOL fixed-width fields from string data.
 * Handles PIC X(n) alphanumeric, PIC 9(n) unsigned numeric,
 * and zoned-decimal with trailing overpunch sign.
 */
public final class CobolFieldParser {

    private CobolFieldParser() {}

    private static final Map<Character, int[]> OVERPUNCH_MAP = Map.ofEntries(
            Map.entry('{', new int[]{+1, 0}),
            Map.entry('A', new int[]{+1, 1}),
            Map.entry('B', new int[]{+1, 2}),
            Map.entry('C', new int[]{+1, 3}),
            Map.entry('D', new int[]{+1, 4}),
            Map.entry('E', new int[]{+1, 5}),
            Map.entry('F', new int[]{+1, 6}),
            Map.entry('G', new int[]{+1, 7}),
            Map.entry('H', new int[]{+1, 8}),
            Map.entry('I', new int[]{+1, 9}),
            Map.entry('}', new int[]{-1, 0}),
            Map.entry('J', new int[]{-1, 1}),
            Map.entry('K', new int[]{-1, 2}),
            Map.entry('L', new int[]{-1, 3}),
            Map.entry('M', new int[]{-1, 4}),
            Map.entry('N', new int[]{-1, 5}),
            Map.entry('O', new int[]{-1, 6}),
            Map.entry('P', new int[]{-1, 7}),
            Map.entry('Q', new int[]{-1, 8}),
            Map.entry('R', new int[]{-1, 9})
    );

    /**
     * Extracts a PIC X(n) alphanumeric field: substring trimmed of trailing spaces.
     */
    public static String parseAlphanumeric(String record, int offset, int length) {
        validateBounds(record, offset, length);
        return record.substring(offset, offset + length).stripTrailing();
    }

    /**
     * Parses a PIC 9(n) unsigned numeric field to a long value.
     */
    public static long parseUnsignedNumeric(String record, int offset, int length) {
        validateBounds(record, offset, length);
        String field = record.substring(offset, offset + length).strip();
        if (field.isEmpty()) {
            return 0L;
        }
        return Long.parseLong(field);
    }

    /**
     * Parses a zoned-decimal field with trailing overpunch sign.
     * The last character encodes the sign and the units digit.
     * <p>
     * Overpunch mapping:
     * { = +0, A-I = +1 to +9, } = -0, J-R = -1 to -9
     */
    public static long parseZonedDecimal(String record, int offset, int length) {
        validateBounds(record, offset, length);
        String field = record.substring(offset, offset + length);
        if (field.isBlank()) {
            return 0L;
        }

        char lastChar = field.charAt(field.length() - 1);
        int[] signAndDigit = OVERPUNCH_MAP.get(lastChar);

        if (signAndDigit != null) {
            String leading = field.substring(0, field.length() - 1);
            long value = leading.isEmpty() ? signAndDigit[1] : Long.parseLong(leading) * 10 + signAndDigit[1];
            return value * signAndDigit[0];
        }

        return Long.parseLong(field);
    }

    private static void validateBounds(String record, int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > record.length()) {
            throw new IllegalArgumentException(
                    "Field bounds [%d, %d) exceed record length %d"
                            .formatted(offset, offset + length, record.length()));
        }
    }
}
