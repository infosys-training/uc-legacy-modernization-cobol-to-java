package com.carddemo.golden.parser;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

public class CobolAsciiRecordParser {

    private final CobolRecordLayout layout;

    public CobolAsciiRecordParser(CobolRecordLayout layout) {
        this.layout = layout;
    }

    public Map<String, Object> parse(String line) {
        Map<String, Object> record = new LinkedHashMap<>();
        int offset = 0;

        for (CobolFieldDefinition field : layout.getFields()) {
            int end = Math.min(offset + field.getLength(), line.length());

            if (offset >= line.length()) {
                if (!field.isFiller()) {
                    record.put(field.getJsonName(), getDefaultValue(field));
                }
                offset += field.getLength();
                continue;
            }

            String rawValue = line.substring(offset, end);

            if (field.isFiller()) {
                offset += field.getLength();
                continue;
            }

            switch (field.getType()) {
                case ALPHANUMERIC -> record.put(field.getJsonName(), trimRight(rawValue));
                case NUMERIC -> record.put(field.getJsonName(), rawValue);
                case SIGNED_NUMERIC -> {
                    BigDecimal decoded = CobolSignDecoder.decode(rawValue, field.getDecimals());
                    record.put(field.getJsonName(), decoded);
                }
            }

            offset += field.getLength();
        }

        return record;
    }

    private static String trimRight(String s) {
        int end = s.length();
        while (end > 0 && s.charAt(end - 1) == ' ') {
            end--;
        }
        return s.substring(0, end);
    }

    private static Object getDefaultValue(CobolFieldDefinition field) {
        return switch (field.getType()) {
            case ALPHANUMERIC -> "";
            case NUMERIC -> "0";
            case SIGNED_NUMERIC -> BigDecimal.ZERO.setScale(field.getDecimals());
        };
    }
}
