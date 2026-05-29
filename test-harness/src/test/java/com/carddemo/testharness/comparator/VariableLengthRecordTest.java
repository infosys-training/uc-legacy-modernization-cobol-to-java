package com.carddemo.testharness.comparator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class VariableLengthRecordTest {

    private FieldByFieldComparator comparator;

    @BeforeEach
    void setUp() {
        comparator = new FieldByFieldComparator();
    }

    @Test
    void shouldStripRdwPrefix() {
        // 12-byte record with 4-byte RDW: total 16 bytes
        // RDW: 0x0010 (16) + 0x0000
        byte[] recordWithRdw = new byte[16];
        recordWithRdw[0] = 0x00;
        recordWithRdw[1] = 0x10; // 16 = total length
        recordWithRdw[2] = 0x00;
        recordWithRdw[3] = 0x00;
        // data bytes
        for (int i = 4; i < 16; i++) {
            recordWithRdw[i] = (byte) (i - 4 + 1);
        }

        byte[] stripped = FieldByFieldComparator.stripRdw(recordWithRdw);

        assertThat(stripped.length).isEqualTo(12);
        assertThat(stripped[0]).isEqualTo((byte) 1);
    }

    @Test
    void shouldNotStripNonRdwRecord() {
        byte[] record = new byte[] { 0x41, 0x42, 0x43, 0x44, 0x45 }; // "ABCDE"

        byte[] result = FieldByFieldComparator.stripRdw(record);

        assertThat(result).isEqualTo(record);
    }

    @Test
    void shouldCompareVariableLengthRecords() {
        // Create two records with RDW prefix
        byte[] rec1 = createRdwRecord(new byte[] { 0x41, 0x42, 0x43, 0x44, 0x45,
            0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C }); // 12 data bytes
        byte[] rec2 = createRdwRecord(new byte[] { 0x41, 0x42, 0x43, 0x44, 0x45,
            0x46, 0x47, 0x48, 0x49, 0x4A, 0x4B, 0x4C }); // same

        List<byte[]> expectedRecords = List.of(rec1);
        List<byte[]> actualRecords = List.of(rec2);

        Set<ToleranceRule> rules = EnumSet.noneOf(ToleranceRule.class);

        List<List<ComparisonResult>> results = comparator.compareVariableLengthRecords(
            expectedRecords, actualRecords, rules, this::extractFields);

        assertThat(results).hasSize(1);
        assertThat(results.get(0)).allMatch(ComparisonResult::isMatch);
    }

    @Test
    void shouldHandleMixedLengthRecords() {
        // 12-byte data record
        byte[] shortData = new byte[12];
        Arrays.fill(shortData, (byte) 0x41);
        byte[] rec1 = createRdwRecord(shortData);

        // 39-byte data record
        byte[] longData = new byte[39];
        Arrays.fill(longData, (byte) 0x42);
        byte[] rec2 = createRdwRecord(longData);

        byte[] stripped1 = FieldByFieldComparator.stripRdw(rec1);
        byte[] stripped2 = FieldByFieldComparator.stripRdw(rec2);

        assertThat(stripped1.length).isEqualTo(12);
        assertThat(stripped2.length).isEqualTo(39);
    }

    @Test
    void shouldMatchRecordsBySequenceIndex() {
        byte[] rec1a = createRdwRecord("RECORD1A".getBytes());
        byte[] rec2a = createRdwRecord("RECORD2ALONGERDATA".getBytes());
        byte[] rec1b = createRdwRecord("RECORD1A".getBytes());
        byte[] rec2b = createRdwRecord("RECORD2ALONGERDATA".getBytes());

        List<byte[]> expected = List.of(rec1a, rec2a);
        List<byte[]> actual = List.of(rec1b, rec2b);

        Set<ToleranceRule> rules = EnumSet.noneOf(ToleranceRule.class);

        List<List<ComparisonResult>> results = comparator.compareVariableLengthRecords(
            expected, actual, rules, this::extractSingleField);

        assertThat(results).hasSize(2);
        for (List<ComparisonResult> recordResults : results) {
            assertThat(recordResults).allMatch(ComparisonResult::isMatch);
        }
    }

    private byte[] createRdwRecord(byte[] data) {
        int totalLen = data.length + 4;
        byte[] record = new byte[totalLen];
        record[0] = (byte) ((totalLen >> 8) & 0xFF);
        record[1] = (byte) (totalLen & 0xFF);
        record[2] = 0x00;
        record[3] = 0x00;
        System.arraycopy(data, 0, record, 4, data.length);
        return record;
    }

    private Map<String, Object> extractFields(byte[] data) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("RAW-DATA", new String(data));
        return fields;
    }

    private Map<String, Object> extractSingleField(byte[] data) {
        Map<String, Object> fields = new LinkedHashMap<>();
        fields.put("CONTENT", new String(data));
        return fields;
    }
}
