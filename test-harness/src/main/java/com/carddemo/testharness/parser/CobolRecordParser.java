package com.carddemo.testharness.parser;

import com.carddemo.testharness.model.RecordLayout;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CobolRecordParser {

    public Map<String, Object> parseLine(String line, RecordLayout layout) {
        String padded = padLine(line, layout.getRecordLength());
        Map<String, Object> record = new LinkedHashMap<>();

        for (CobolFieldDefinition field : layout.getFields()) {
            if (field.isFiller()) {
                continue;
            }

            int end = Math.min(field.getOffset() + field.getLength(), padded.length());
            String raw = padded.substring(field.getOffset(), end);

            Object value = parseField(raw, field);
            record.put(field.getName(), value);
        }

        return record;
    }

    public List<Map<String, Object>> parseFile(Path filePath, RecordLayout layout) throws IOException {
        List<String> lines = Files.readAllLines(filePath);
        List<Map<String, Object>> records = new ArrayList<>();
        for (String line : lines) {
            String stripped = stripLineEnding(line);
            if (!stripped.isEmpty()) {
                records.add(parseLine(stripped, layout));
            }
        }
        return records;
    }

    private Object parseField(String raw, CobolFieldDefinition field) {
        switch (field.getPicType()) {
            case ALPHANUMERIC:
                return trimTrailingSpaces(raw);
            case NUMERIC:
                return raw;
            case SIGNED_DECIMAL:
                return parseSignedDecimal(raw, field.getDecimals());
            default:
                return raw;
        }
    }

    private BigDecimal parseSignedDecimal(String raw, int decimals) {
        CobolSignDecoder.SignedValue sv = CobolSignDecoder.decode(raw);
        BigDecimal value = new BigDecimal(sv.digits());
        if (decimals > 0) {
            value = value.movePointLeft(decimals);
        }
        if (!sv.positive()) {
            value = value.negate();
        }
        return value;
    }

    private String padLine(String line, int length) {
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

    private String stripLineEnding(String line) {
        if (line.endsWith("\r")) {
            return line.substring(0, line.length() - 1);
        }
        return line;
    }

    private String trimTrailingSpaces(String value) {
        int end = value.length();
        while (end > 0 && value.charAt(end - 1) == ' ') {
            end--;
        }
        return value.substring(0, end);
    }
}
