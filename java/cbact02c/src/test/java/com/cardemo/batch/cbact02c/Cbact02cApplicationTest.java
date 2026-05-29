package com.cardemo.batch.cbact02c;

import com.cardemo.batch.cbact02c.io.CardFileReader;
import com.cardemo.batch.cbact02c.io.CardReportWriter;
import com.cardemo.batch.cbact02c.io.CobolFieldParser;
import com.cardemo.batch.cbact02c.model.CardRecord;
import com.cardemo.batch.cbact02c.service.CardDisplayService;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CBACT02C — Card Data Reader / Printer")
class Cbact02cApplicationTest {

    // ---- sample lines from app/data/ASCII/carddata.txt ----
    private static final String SAMPLE_LINE_1 =
            "050002445376574000000000050747Aniya Von" +
            "                                         2023-03-09Y" +
            "                                                           ";

    private static final String SAMPLE_LINE_2 =
            "068358619817151600000000027567Ward Jones" +
            "                                        2025-07-13Y" +
            "                                                           ";

    private static final String SAMPLE_LINE_3 =
            "092387719324733000000000002028Enrico Rosenbaum" +
            "                                  2024-08-11Y" +
            "                                                           ";

    /**
     * Pads a string to exactly 150 characters with trailing spaces.
     */
    private static String padTo150(String s) {
        return String.format("%-150s", s);
    }

    // ========================================================================
    // 1. CobolFieldParser tests
    // ========================================================================

    @Nested
    @DisplayName("CobolFieldParser")
    class FieldParserTests {

        @Test
        @DisplayName("parseAlphanumeric extracts and trims trailing spaces")
        void parseAlphanumericBasic() {
            String line = padTo150("Hello     World");
            assertEquals("Hello", CobolFieldParser.parseAlphanumeric(line, 0, 10));
        }

        @Test
        @DisplayName("parseAlphanumeric preserves leading spaces")
        void parseAlphanumericLeadingSpaces() {
            String line = padTo150("  ABC  ");
            assertEquals("  ABC", CobolFieldParser.parseAlphanumeric(line, 0, 7));
        }

        @Test
        @DisplayName("parseAlphanumeric throws on out-of-bounds offset")
        void parseAlphanumericOutOfBounds() {
            String line = "short";
            assertThrows(IllegalArgumentException.class,
                    () -> CobolFieldParser.parseAlphanumeric(line, 0, 50));
        }

        @Test
        @DisplayName("parseUnsignedNumeric parses PIC 9(11)")
        void parseUnsignedNumeric() {
            String line = padTo150("00000000050");
            assertEquals(50L, CobolFieldParser.parseUnsignedNumeric(line, 0, 11));
        }

        @Test
        @DisplayName("parseUnsignedNumeric returns 0 for all zeros")
        void parseUnsignedNumericZero() {
            String line = padTo150("00000000000");
            assertEquals(0L, CobolFieldParser.parseUnsignedNumeric(line, 0, 11));
        }

        @Test
        @DisplayName("parseUnsignedNumericInt parses PIC 9(03)")
        void parseUnsignedNumericInt() {
            String line = padTo150("747");
            assertEquals(747, CobolFieldParser.parseUnsignedNumericInt(line, 0, 3));
        }

        @Test
        @DisplayName("parseSignedZonedDecimal — positive values with overpunch")
        void signedPositive() {
            // 12345{ → +123450
            String line = padTo150("12345{");
            assertEquals(new BigDecimal("123450"),
                    CobolFieldParser.parseSignedZonedDecimal(line, 0, 6, 0));
        }

        @Test
        @DisplayName("parseSignedZonedDecimal — positive 1-9 overpunch chars")
        void signedPositiveOverpunchAtoI() {
            // 0000A → +00001
            assertEquals(new BigDecimal("1"),
                    CobolFieldParser.parseSignedZonedDecimal(padTo150("0000A"), 0, 5, 0));
            assertEquals(new BigDecimal("9"),
                    CobolFieldParser.parseSignedZonedDecimal(padTo150("0000I"), 0, 5, 0));
        }

        @Test
        @DisplayName("parseSignedZonedDecimal — negative values with overpunch")
        void signedNegative() {
            // 12345J → -123451
            assertEquals(new BigDecimal("-123451"),
                    CobolFieldParser.parseSignedZonedDecimal(padTo150("12345J"), 0, 6, 0));
            // 12345R → -123459
            assertEquals(new BigDecimal("-123459"),
                    CobolFieldParser.parseSignedZonedDecimal(padTo150("12345R"), 0, 6, 0));
        }

        @Test
        @DisplayName("parseSignedZonedDecimal — implied decimal places")
        void signedWithImpliedDecimal() {
            // 12345{ with 2 implied decimals → +1234.50
            assertEquals(new BigDecimal("1234.50"),
                    CobolFieldParser.parseSignedZonedDecimal(padTo150("12345{"), 0, 6, 2));
        }

        @Test
        @DisplayName("parseSignedZonedDecimal — negative zero with }")
        void signedNegativeZero() {
            assertEquals(new BigDecimal("0").negate(),
                    CobolFieldParser.parseSignedZonedDecimal(padTo150("0000}"), 0, 5, 0));
        }

        @Test
        @DisplayName("parseSignedZonedDecimal — plain digit as last character")
        void signedPlainDigit() {
            assertEquals(new BigDecimal("12345"),
                    CobolFieldParser.parseSignedZonedDecimal(padTo150("12345"), 0, 5, 0));
        }

        @Test
        @DisplayName("toPositiveOverpunch / toNegativeOverpunch round-trip")
        void overpunchRoundTrip() {
            for (int d = 0; d <= 9; d++) {
                char posChar = CobolFieldParser.toPositiveOverpunch(d);
                char negChar = CobolFieldParser.toNegativeOverpunch(d);
                assertNotEquals(posChar, negChar);
            }
            assertEquals('{', CobolFieldParser.toPositiveOverpunch(0));
            assertEquals('}', CobolFieldParser.toNegativeOverpunch(0));
            assertEquals('I', CobolFieldParser.toPositiveOverpunch(9));
            assertEquals('R', CobolFieldParser.toNegativeOverpunch(9));
        }
    }

    // ========================================================================
    // 2. CardRecord model tests
    // ========================================================================

    @Nested
    @DisplayName("CardRecord model")
    class CardRecordTests {

        @Test
        @DisplayName("record length constant is 150")
        void recordLength() {
            assertEquals(150, CardRecord.RECORD_LENGTH);
        }

        @Test
        @DisplayName("toString contains all fields")
        void toStringFormat() {
            CardRecord rec = new CardRecord("1234567890123456", 50, 747, "John Doe", "2023-03-09", "Y");
            String s = rec.toString();
            assertTrue(s.contains("1234567890123456"));
            assertTrue(s.contains("747"));
            assertTrue(s.contains("John Doe"));
            assertTrue(s.contains("2023-03-09"));
            assertTrue(s.contains("Y"));
        }
    }

    // ========================================================================
    // 3. CardFileReader — input parsing tests
    // ========================================================================

    @Nested
    @DisplayName("CardFileReader — input parsing")
    class InputParsingTests {

        @Test
        @DisplayName("parseLine extracts correct fields from sample line 1")
        void parseSampleLine1() {
            CardRecord rec = CardFileReader.parseLine(SAMPLE_LINE_1);
            assertEquals("0500024453765740", rec.cardNum());
            assertEquals(50, rec.acctId());
            assertEquals(747, rec.cvvCode());
            assertEquals("Aniya Von", rec.embossedName());
            assertEquals("2023-03-09", rec.expirationDate());
            assertEquals("Y", rec.activeStatus());
        }

        @Test
        @DisplayName("parseLine extracts correct fields from sample line 2")
        void parseSampleLine2() {
            CardRecord rec = CardFileReader.parseLine(SAMPLE_LINE_2);
            assertEquals("0683586198171516", rec.cardNum());
            assertEquals(27, rec.acctId());
            assertEquals(567, rec.cvvCode());
            assertEquals("Ward Jones", rec.embossedName());
            assertEquals("2025-07-13", rec.expirationDate());
            assertEquals("Y", rec.activeStatus());
        }

        @Test
        @DisplayName("parseLine extracts correct fields from sample line 3")
        void parseSampleLine3() {
            CardRecord rec = CardFileReader.parseLine(SAMPLE_LINE_3);
            assertEquals("0923877193247330", rec.cardNum());
            assertEquals(2, rec.acctId());
            assertEquals(28, rec.cvvCode());
            assertEquals("Enrico Rosenbaum", rec.embossedName());
            assertEquals("2024-08-11", rec.expirationDate());
            assertEquals("Y", rec.activeStatus());
        }

        @Test
        @DisplayName("parseLine throws on short record")
        void parseLineShort() {
            assertThrows(IllegalArgumentException.class,
                    () -> CardFileReader.parseLine("too short"));
        }

        @Test
        @DisplayName("readAll reads multiple records from file")
        void readAllFromFile(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test_cards.txt");
            Files.writeString(file, SAMPLE_LINE_1 + "\n" + SAMPLE_LINE_2 + "\n");
            List<CardRecord> records = CardFileReader.readAll(file);
            assertEquals(2, records.size());
            assertEquals("0500024453765740", records.get(0).cardNum());
            assertEquals("0683586198171516", records.get(1).cardNum());
        }

        @Test
        @DisplayName("readAll skips blank lines")
        void readAllSkipsBlank(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test_blank.txt");
            Files.writeString(file, SAMPLE_LINE_1 + "\n\n" + SAMPLE_LINE_2 + "\n");
            List<CardRecord> records = CardFileReader.readAll(file);
            assertEquals(2, records.size());
        }
    }

    // ========================================================================
    // 4. CardReportWriter — output formatting tests
    // ========================================================================

    @Nested
    @DisplayName("CardReportWriter — output formatting")
    class OutputFormattingTests {

        @Test
        @DisplayName("header contains all field names")
        void headerLine() {
            String hdr = CardReportWriter.header();
            assertTrue(hdr.contains("CARD_NUM"));
            assertTrue(hdr.contains("ACCT_ID"));
            assertTrue(hdr.contains("CVV_CODE"));
            assertTrue(hdr.contains("EMBOSSED_NAME"));
            assertTrue(hdr.contains("EXPIRATION_DATE"));
            assertTrue(hdr.contains("ACTIVE_STATUS"));
        }

        @Test
        @DisplayName("formatRecord produces pipe-delimited output")
        void formatRecord() {
            CardRecord rec = new CardRecord("1234567890123456", 50, 747, "John Doe", "2023-03-09", "Y");
            String formatted = CardReportWriter.formatRecord(rec);
            String[] fields = formatted.split("\\|", -1);
            assertEquals(6, fields.length);
            assertEquals("1234567890123456", fields[0]);
            assertEquals("50", fields[1]);
            assertEquals("747", fields[2]);
            assertEquals("John Doe", fields[3]);
            assertEquals("2023-03-09", fields[4]);
            assertEquals("Y", fields[5]);
        }

        @Test
        @DisplayName("write creates file with header + data rows")
        void writeFile(@TempDir Path tempDir) throws IOException {
            Path output = tempDir.resolve("report.csv");
            List<CardRecord> records = List.of(
                    new CardRecord("1111222233334444", 100, 123, "Alice", "2024-01-01", "Y"),
                    new CardRecord("5555666677778888", 200, 456, "Bob", "2025-06-15", "N")
            );
            CardReportWriter.write(records, output);

            List<String> lines = Files.readAllLines(output);
            assertEquals(3, lines.size());
            assertTrue(lines.get(0).startsWith("CARD_NUM|"));
            assertTrue(lines.get(1).startsWith("1111222233334444|"));
            assertTrue(lines.get(2).startsWith("5555666677778888|"));
        }
    }

    // ========================================================================
    // 5. CardDisplayService — business logic tests
    // ========================================================================

    @Nested
    @DisplayName("CardDisplayService — business logic")
    class BusinessLogicTests {

        @Test
        @DisplayName("displayCards prints start/end banners and each record")
        void displayCardsBanners(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test.txt");
            Files.writeString(file, SAMPLE_LINE_1 + "\n");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CardDisplayService svc = new CardDisplayService(new PrintStream(baos));
            List<CardRecord> records = svc.displayCards(file);

            String output = baos.toString();
            assertTrue(output.contains("START OF EXECUTION OF PROGRAM CBACT02C"));
            assertTrue(output.contains("END OF EXECUTION OF PROGRAM CBACT02C"));
            assertTrue(output.contains("Aniya Von"));
            assertEquals(1, records.size());
        }

        @Test
        @DisplayName("processAndWriteReport writes pipe-delimited output")
        void processAndWriteReport(@TempDir Path tempDir) throws IOException {
            Path input = tempDir.resolve("input.txt");
            Path output = tempDir.resolve("output.csv");
            Files.writeString(input, SAMPLE_LINE_1 + "\n" + SAMPLE_LINE_2 + "\n");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CardDisplayService svc = new CardDisplayService(new PrintStream(baos));
            int count = svc.processAndWriteReport(input, output);

            assertEquals(2, count);
            assertTrue(Files.exists(output));
            List<String> lines = Files.readAllLines(output);
            assertEquals(3, lines.size());
        }

        @Test
        @DisplayName("displayCards with empty file returns empty list")
        void displayCardsEmpty(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("empty.txt");
            Files.writeString(file, "");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CardDisplayService svc = new CardDisplayService(new PrintStream(baos));
            List<CardRecord> records = svc.displayCards(file);

            assertTrue(records.isEmpty());
            String output = baos.toString();
            assertTrue(output.contains("START OF EXECUTION OF PROGRAM CBACT02C"));
            assertTrue(output.contains("END OF EXECUTION OF PROGRAM CBACT02C"));
        }

        @Test
        @DisplayName("displayCards throws IOException for missing file")
        void displayCardsMissingFile() {
            CardDisplayService svc = new CardDisplayService(System.out);
            assertThrows(IOException.class, () -> svc.displayCards(Path.of("/nonexistent/file.txt")));
        }
    }

    // ========================================================================
    // 6. Integration tests — full pipeline
    // ========================================================================

    @Nested
    @DisplayName("Integration — full pipeline")
    class IntegrationTests {

        @Test
        @DisplayName("end-to-end: read → display → write report")
        void endToEnd(@TempDir Path tempDir) throws IOException {
            Path input = tempDir.resolve("cards.txt");
            Path output = tempDir.resolve("report.csv");
            Files.writeString(input,
                    SAMPLE_LINE_1 + "\n" +
                    SAMPLE_LINE_2 + "\n" +
                    SAMPLE_LINE_3 + "\n");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CardDisplayService svc = new CardDisplayService(new PrintStream(baos));
            int count = svc.processAndWriteReport(input, output);

            assertEquals(3, count);

            // Verify console output
            String console = baos.toString();
            assertTrue(console.contains("START OF EXECUTION OF PROGRAM CBACT02C"));
            assertTrue(console.contains("END OF EXECUTION OF PROGRAM CBACT02C"));
            assertTrue(console.contains("Aniya Von"));
            assertTrue(console.contains("Ward Jones"));
            assertTrue(console.contains("Enrico Rosenbaum"));

            // Verify report file
            List<String> reportLines = Files.readAllLines(output);
            assertEquals(4, reportLines.size()); // header + 3 data rows
            assertTrue(reportLines.get(0).contains("CARD_NUM"));
            assertTrue(reportLines.get(1).contains("0500024453765740"));
            assertTrue(reportLines.get(2).contains("0683586198171516"));
            assertTrue(reportLines.get(3).contains("0923877193247330"));
        }

        @Test
        @DisplayName("single record round-trip: parse → format preserves data")
        void singleRecordRoundTrip() {
            CardRecord rec = CardFileReader.parseLine(SAMPLE_LINE_1);
            String formatted = CardReportWriter.formatRecord(rec);

            assertTrue(formatted.contains("0500024453765740"));
            assertTrue(formatted.contains("50"));
            assertTrue(formatted.contains("747"));
            assertTrue(formatted.contains("Aniya Von"));
            assertTrue(formatted.contains("2023-03-09"));
            assertTrue(formatted.contains("Y"));
        }
    }

    // ========================================================================
    // 7. Real data tests — process actual sample data
    // ========================================================================

    @Nested
    @DisplayName("Real data — app/data/ASCII/carddata.txt")
    class RealDataTests {

        private static final Path REAL_DATA_PATH = Path.of("../../app/data/ASCII/carddata.txt");

        private boolean realDataAvailable() {
            return Files.exists(REAL_DATA_PATH);
        }

        @Test
        @DisplayName("reads all 50 records from real card data file")
        void readAllRealRecords() throws IOException {
            if (!realDataAvailable()) {
                System.out.println("Skipping: real data file not found at " + REAL_DATA_PATH);
                return;
            }
            List<CardRecord> records = CardFileReader.readAll(REAL_DATA_PATH);
            assertEquals(50, records.size(), "Expected 50 records in carddata.txt");
        }

        @Test
        @DisplayName("first record matches expected values")
        void firstRecordValues() throws IOException {
            if (!realDataAvailable()) {
                System.out.println("Skipping: real data file not found at " + REAL_DATA_PATH);
                return;
            }
            List<CardRecord> records = CardFileReader.readAll(REAL_DATA_PATH);
            CardRecord first = records.get(0);
            assertEquals("0500024453765740", first.cardNum());
            assertEquals(50, first.acctId());
            assertEquals(747, first.cvvCode());
            assertEquals("Aniya Von", first.embossedName());
            assertEquals("2023-03-09", first.expirationDate());
            assertEquals("Y", first.activeStatus());
        }

        @Test
        @DisplayName("all records have 16-character card numbers")
        void allCardNumbersCorrectLength() throws IOException {
            if (!realDataAvailable()) {
                System.out.println("Skipping: real data file not found at " + REAL_DATA_PATH);
                return;
            }
            List<CardRecord> records = CardFileReader.readAll(REAL_DATA_PATH);
            for (CardRecord rec : records) {
                assertEquals(16, rec.cardNum().length(),
                        "Card number should be 16 chars: " + rec.cardNum());
            }
        }

        @Test
        @DisplayName("all records have valid active status (Y or N)")
        void allActiveStatusValid() throws IOException {
            if (!realDataAvailable()) {
                System.out.println("Skipping: real data file not found at " + REAL_DATA_PATH);
                return;
            }
            List<CardRecord> records = CardFileReader.readAll(REAL_DATA_PATH);
            for (CardRecord rec : records) {
                assertTrue("Y".equals(rec.activeStatus()) || "N".equals(rec.activeStatus()),
                        "Active status should be Y or N: " + rec.activeStatus());
            }
        }

        @Test
        @DisplayName("all records have valid expiration date format")
        void allExpirationDatesValid() throws IOException {
            if (!realDataAvailable()) {
                System.out.println("Skipping: real data file not found at " + REAL_DATA_PATH);
                return;
            }
            List<CardRecord> records = CardFileReader.readAll(REAL_DATA_PATH);
            for (CardRecord rec : records) {
                assertTrue(rec.expirationDate().matches("\\d{4}-\\d{2}-\\d{2}"),
                        "Expiration date should match YYYY-MM-DD: " + rec.expirationDate());
            }
        }

        @Test
        @DisplayName("full pipeline with real data produces report")
        void fullPipelineRealData(@TempDir Path tempDir) throws IOException {
            if (!realDataAvailable()) {
                System.out.println("Skipping: real data file not found at " + REAL_DATA_PATH);
                return;
            }
            Path output = tempDir.resolve("real_report.csv");
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            CardDisplayService svc = new CardDisplayService(new PrintStream(baos));
            int count = svc.processAndWriteReport(REAL_DATA_PATH, output);

            assertEquals(50, count);
            List<String> reportLines = Files.readAllLines(output);
            assertEquals(51, reportLines.size()); // header + 50 records
        }
    }
}
