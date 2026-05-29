package com.carddemo.parser;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CobolRecordParser {

    private final CobolRecordLayout layout;

    public CobolRecordParser(CobolRecordLayout layout) {
        this.layout = layout;
    }

    public Map<String, Object> parse(String line) {
        String padded = padLine(line, layout.getTotalLength());
        Map<String, Object> record = new LinkedHashMap<>();
        int offset = 0;

        for (CobolFieldDefinition field : layout.getFields()) {
            int end = Math.min(offset + field.getLength(), padded.length());

            if (offset >= padded.length()) {
                if (!field.isFiller()) {
                    record.put(field.getJsonName(), getDefaultValue(field));
                }
                offset += field.getLength();
                continue;
            }

            String rawValue = padded.substring(offset, end);

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

    public List<Map<String, Object>> parseFile(Path filePath) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        List<Map<String, Object>> records = new ArrayList<>();
        for (String line : lines) {
            String stripped = stripLineEnding(line);
            if (!stripped.isEmpty()) {
                records.add(parse(stripped));
            }
        }
        return records;
    }

    private static String padLine(String line, int length) {
        if (line.length() >= length) {
            return line;
        }
        StringBuilder sb = new StringBuilder(length);
        sb.append(line);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }

    private static String stripLineEnding(String line) {
        if (line.endsWith("\r")) {
            return line.substring(0, line.length() - 1);
        }
        return line;
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
