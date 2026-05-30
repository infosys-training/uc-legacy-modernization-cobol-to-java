package com.carddemo.batch;

import com.carddemo.model.CardRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CardFileReaderTest {

    private static final Path CARD_DATA_FILE =
            Path.of("../../app/data/ASCII/carddata.txt");

    // --- Golden-file integration tests ---

    @Test
    void testReadAllRecords() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        assertEquals(50, records.size(), "Card data file should contain 50 records");
    }

    @Test
    void testFirstRecord() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        CardRecord first = records.get(0);

        assertEquals("0500024453765740", first.cardNum());
        assertEquals(50, first.acctId());
        assertEquals(747, first.cvvCode());
        assertEquals("Aniya Von", first.embossedName());
        assertEquals("2023-03-09", first.expirationDate());
        assertEquals("Y", first.activeStatus());
    }

    @Test
    void testLastRecord() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        CardRecord last = records.get(49);

        assertEquals("9805583408996588", last.cardNum());
        assertEquals(40, last.acctId());
        assertEquals(908, last.cvvCode());
        assertEquals("Davon Emmerich", last.embossedName());
        assertEquals("2023-10-27", last.expirationDate());
        assertEquals("Y", last.activeStatus());
    }

    @Test
    void testMiddleRecord() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        CardRecord mid = records.get(24);

        assertEquals("5787351228879339", mid.cardNum());
        assertEquals(47, mid.acctId());
        assertEquals(67, mid.cvvCode());
        assertEquals("Rigoberto Hoeger", mid.embossedName());
        assertEquals("2025-08-23", mid.expirationDate());
        assertEquals("Y", mid.activeStatus());
    }

    @Test
    void testAllRecordsHaveActiveStatusY() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        for (CardRecord r : records) {
            assertEquals("Y", r.activeStatus(),
                    "Card " + r.cardNum() + " should have active status Y");
        }
    }

    @Test
    void testAllCardNumbersAre16Chars() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        for (CardRecord r : records) {
            assertEquals(16, r.cardNum().length(),
                    "Card number should be 16 characters");
        }
    }

    @Test
    void testAllCvvCodesAreThreeDigits() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        for (CardRecord r : records) {
            assertTrue(r.cvvCode() >= 0 && r.cvvCode() <= 999,
                    "CVV code should be between 0 and 999, got " + r.cvvCode());
        }
    }

    @Test
    void testNoEmptyEmbossedNames() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        for (CardRecord r : records) {
            assertFalse(r.embossedName().isEmpty(),
                    "Card " + r.cardNum() + " should have a non-empty embossed name");
        }
    }

    @Test
    void testExpirationDateFormat() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        for (CardRecord r : records) {
            assertTrue(r.expirationDate().matches("\\d{4}-\\d{2}-\\d{2}"),
                    "Expiration date should match YYYY-MM-DD, got: "
                            + r.expirationDate());
        }
    }

    @Test
    void testUniqueCardNumbers() throws IOException {
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);
        long distinctCount = records.stream()
                .map(CardRecord::cardNum)
                .distinct()
                .count();
        assertEquals(records.size(), distinctCount,
                "All card numbers should be unique");
    }

    // --- Record parsing unit tests ---

    @Test
    void testParseValidRecord() {
        String line = "0500024453765740" // CARD-NUM (16)
                + "00000000050"          // CARD-ACCT-ID (11)
                + "747"                  // CARD-CVV-CD (3)
                + "Aniya Von                                         " // EMBOSSED-NAME (50)
                + "2023-03-09"           // EXPIRAION-DATE (10)
                + "Y"                    // ACTIVE-STATUS (1)
                + "                                                           "; // FILLER (59)

        CardRecord record = CardRecord.parse(line);

        assertEquals("0500024453765740", record.cardNum());
        assertEquals(50, record.acctId());
        assertEquals(747, record.cvvCode());
        assertEquals("Aniya Von", record.embossedName());
        assertEquals("2023-03-09", record.expirationDate());
        assertEquals("Y", record.activeStatus());
    }

    @Test
    void testParseTooShortRecord() {
        assertThrows(IllegalArgumentException.class,
                () -> CardRecord.parse("short"));
    }

    @Test
    void testParseZeroAcctId() {
        String line = "1234567890123456"
                + "00000000000"
                + "000"
                + String.format("%-50s", "Test Name")
                + "2025-01-01"
                + "N"
                + String.format("%-59s", "");

        CardRecord record = CardRecord.parse(line);
        assertEquals(0, record.acctId());
        assertEquals(0, record.cvvCode());
        assertEquals("N", record.activeStatus());
    }

    @Test
    void testParseMaxValues() {
        String line = "9999999999999999"
                + "99999999999"
                + "999"
                + String.format("%-50s", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX")
                + "9999-12-31"
                + "Y"
                + String.format("%-59s", "");

        CardRecord record = CardRecord.parse(line);
        assertEquals("9999999999999999", record.cardNum());
        assertEquals(99999999999L, record.acctId());
        assertEquals(999, record.cvvCode());
    }

    // --- Display output tests ---

    @Test
    void testToDisplayStringLength() {
        String line = "0500024453765740"
                + "00000000050"
                + "747"
                + "Aniya Von                                         "
                + "2023-03-09"
                + "Y"
                + "                                                           ";

        CardRecord record = CardRecord.parse(line);
        String display = record.toDisplayString();
        assertEquals(CardRecord.RECORD_LENGTH, display.length(),
                "Display output should be exactly 150 characters");
    }

    @Test
    void testToDisplayStringMatchesInput() throws IOException {
        List<String> lines = Files.readAllLines(CARD_DATA_FILE);
        List<CardRecord> records = CardFileReader.readCardFile(CARD_DATA_FILE);

        for (int i = 0; i < records.size(); i++) {
            String original = lines.get(i);
            String displayed = records.get(i).toDisplayString();
            assertEquals(original, displayed,
                    "Display output for record " + i + " should match input line");
        }
    }

    // --- Empty file test ---

    @Test
    void testReadEmptyFile(@TempDir Path tempDir) throws IOException {
        Path emptyFile = tempDir.resolve("empty.txt");
        Files.createFile(emptyFile);

        List<CardRecord> records = CardFileReader.readCardFile(emptyFile);
        assertTrue(records.isEmpty(), "Empty file should yield empty list");
    }

    // --- Single record file test ---

    @Test
    void testReadSingleRecordFile(@TempDir Path tempDir) throws IOException {
        String line = "1234567890123456"
                + "00000000001"
                + "123"
                + String.format("%-50s", "John Doe")
                + "2025-12-31"
                + "Y"
                + String.format("%-59s", "");

        Path singleFile = tempDir.resolve("single.txt");
        Files.writeString(singleFile, line + "\n");

        List<CardRecord> records = CardFileReader.readCardFile(singleFile);
        assertEquals(1, records.size());
        assertEquals("John Doe", records.get(0).embossedName());
    }
}
