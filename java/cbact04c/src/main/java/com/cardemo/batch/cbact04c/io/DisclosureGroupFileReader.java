package com.cardemo.batch.cbact04c.io;

import com.cardemo.batch.cbact04c.model.DisclosureGroupRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads DISCGRP-FILE (disclosure group/interest rates, RECLN 50) and indexes by composite key.
 * Layout: DIS-ACCT-GROUP-ID(10) + DIS-TRAN-TYPE-CD(2) + DIS-TRAN-CAT-CD(4) + DIS-INT-RATE(6 signed) + FILLER(28)
 */
public class DisclosureGroupFileReader {

    private static final String DEFAULT_GROUP_PREFIX = "DEFAULT   ";

    private final Path filePath;

    public DisclosureGroupFileReader(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads all records and returns a map keyed by composite key (groupId + typeCode + catCode).
     */
    public Map<String, DisclosureGroupRecord> readAllByKey() throws IOException {
        Map<String, DisclosureGroupRecord> map = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                DisclosureGroupRecord rec = parseLine(line);
                map.put(rec.getCompositeKey(), rec);
            }
        }
        return map;
    }

    /**
     * Looks up interest rate by account group, transaction type, and category.
     * Falls back to DEFAULT group if account's group not found (COBOL status '23' fallback).
     */
    public static DisclosureGroupRecord lookup(Map<String, DisclosureGroupRecord> map,
                                                String groupId, String typeCode, int catCode) {
        String key = padRight(groupId, 10) + padRight(typeCode, 2) + String.format("%04d", catCode);
        DisclosureGroupRecord rec = map.get(key);
        if (rec != null) return rec;

        String defaultKey = padRight("DEFAULT", 10) + padRight(typeCode, 2) + String.format("%04d", catCode);
        return map.get(defaultKey);
    }

    public static DisclosureGroupRecord parseLine(String line) {
        // DIS-ACCT-GROUP-ID PIC X(10) -> pos 0-9
        String groupId = CobolFieldParser.extractField(line, 0, 10);
        // DIS-TRAN-TYPE-CD PIC X(02) -> pos 10-11
        String typeCd = CobolFieldParser.extractField(line, 10, 2);
        // DIS-TRAN-CAT-CD PIC 9(04) -> pos 12-15
        int catCd = (int) CobolFieldParser.parseUnsignedNumeric(CobolFieldParser.extractField(line, 12, 4));
        // DIS-INT-RATE PIC S9(04)V99 -> 6 chars (4+2=6 digits, trailing overpunch) -> pos 16-21
        String rateField = CobolFieldParser.extractField(line, 16, 6);

        DisclosureGroupRecord rec = new DisclosureGroupRecord();
        rec.setAccountGroupId(groupId);
        rec.setTranTypeCode(typeCd);
        rec.setTranCatCode(catCd);
        rec.setInterestRate(CobolFieldParser.parseSignedDecimal(rateField, 2));
        return rec;
    }

    private static String padRight(String s, int len) {
        if (s == null) s = "";
        if (s.length() >= len) return s.substring(0, len);
        return s + " ".repeat(len - s.length());
    }
}
