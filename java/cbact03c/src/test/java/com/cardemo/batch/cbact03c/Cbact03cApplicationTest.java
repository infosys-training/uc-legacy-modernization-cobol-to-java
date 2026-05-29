package com.cardemo.batch.cbact03c;

import com.cardemo.batch.cbact03c.io.CardXrefFileReader;
import com.cardemo.batch.cbact03c.io.CobolFieldParser;
import com.cardemo.batch.cbact03c.model.CardXrefRecord;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Cbact03cApplicationTest {

    private static final Path REAL_DATA_PATH = Path.of("../../app/data/ASCII/cardxref.txt");

    @Nested
    @DisplayName("CobolFieldParser Tests")
    class CobolFieldParserTests {

        @Test
        @DisplayName("parseAlphanumeric extracts and trims field")
        void parseAlphanumericBasic() {
            String record = "4532100012345678000000050";
            assertEquals("4532100012345678", CobolFieldParser.parseAlphanumeric(record, 0, 16));
        }

        @Test
        @DisplayName("parseAlphanumeric trims trailing spaces")
        void parseAlphanumericTrimsTrailing() {
            String record = "HELLO           rest";
            assertEquals("HELLO", CobolFieldParser.parseAlphanumeric(record, 0, 16));
        }

        @Test
        @DisplayName("parseUnsignedNumeric parses PIC 9(n)")
        void parseUnsignedNumericBasic() {
            String record = "ABCDEFGHIJKLMNOP000000050rest";
            assertEquals(50, CobolFieldParser.parseUnsignedNumeric(record, 16, 9));
        }

        @Test
        @DisplayName("parseUnsignedNumeric handles leading zeros")
        void parseUnsignedNumericLeadingZeros() {
            assertEquals(50, CobolFieldParser.parseUnsignedNumeric("000000050", 0, 9));
            assertEquals(0, CobolFieldParser.parseUnsignedNumeric("000000000", 0, 9));
        }

        @Test
        @DisplayName("parseZonedDecimal handles positive overpunch")
        void parseZonedDecimalPositive() {
            // '{' = +0, 'A' = +1, 'I' = +9
            assertEquals(0, CobolFieldParser.parseZonedDecimal("{", 0, 1));
            assertEquals(1, CobolFieldParser.parseZonedDecimal("A", 0, 1));
            assertEquals(9, CobolFieldParser.parseZonedDecimal("I", 0, 1));
            assertEquals(19, CobolFieldParser.parseZonedDecimal("1I", 0, 2));
            assertEquals(123, CobolFieldParser.parseZonedDecimal("12C", 0, 3));
        }

        @Test
        @DisplayName("parseZonedDecimal handles negative overpunch")
        void parseZonedDecimalNegative() {
            // '}' = -0, 'J' = -1, 'R' = -9
            assertEquals(0, CobolFieldParser.parseZonedDecimal("}", 0, 1));
            assertEquals(-1, CobolFieldParser.parseZonedDecimal("J", 0, 1));
            assertEquals(-9, CobolFieldParser.parseZonedDecimal("R", 0, 1));
            assertEquals(-19, CobolFieldParser.parseZonedDecimal("1R", 0, 2));
            assertEquals(-123, CobolFieldParser.parseZonedDecimal("12L", 0, 3));
        }

        @Test
        @DisplayName("parseZonedDecimal handles plain numeric")
        void parseZonedDecimalPlainNumeric() {
            assertEquals(12345, CobolFieldParser.parseZonedDecimal("12345", 0, 5));
        }

        @Test
        @DisplayName("validateBounds throws on invalid bounds")
        void validateBoundsThrows() {
            assertThrows(IllegalArgumentException.class,
                    () -> CobolFieldParser.parseAlphanumeric("short", 0, 10));
            assertThrows(IllegalArgumentException.class,
                    () -> CobolFieldParser.parseUnsignedNumeric("short", 3, 5));
        }
    }

    @Nested
    @DisplayName("CardXrefFileReader Tests")
    class CardXrefFileReaderTests {

        @Test
        @DisplayName("parseLine parses a 36-char record correctly")
        void parseLineMinLength() {
            String line = "050002445376574000000005000000000050";
            CardXrefRecord rec = CardXrefFileReader.parseLine(line);
            assertEquals("0500024453765740", rec.cardNumber());
            assertEquals(50, rec.customerId());
            assertEquals(50, rec.accountId());
        }

        @Test
        @DisplayName("parseLine parses a full 50-char record with filler")
        void parseLineFullLength() {
            String line = "050002445376574000000005000000000050              ";
            CardXrefRecord rec = CardXrefFileReader.parseLine(line);
            assertEquals("0500024453765740", rec.cardNumber());
            assertEquals(50, rec.customerId());
            assertEquals(50, rec.accountId());
        }

        @Test
        @DisplayName("parseLine throws on short record")
        void parseLineTooShort() {
            assertThrows(IllegalArgumentException.class,
                    () -> CardXrefFileReader.parseLine("tooshort"));
        }

        @Test
        @DisplayName("readAll reads multiple records from file")
        void readAllFromTempFile(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test_xref.txt");
            Files.writeString(file,
                    "050002445376574000000005000000000050\n" +
                    "068358619817151600000002700000000027\n");

            List<CardXrefRecord> records = CardXrefFileReader.readAll(file);
            assertEquals(2, records.size());

            assertEquals("0500024453765740", records.get(0).cardNumber());
            assertEquals(50, records.get(0).customerId());
            assertEquals(50, records.get(0).accountId());

            assertEquals("0683586198171516", records.get(1).cardNumber());
            assertEquals(27, records.get(1).customerId());
            assertEquals(27, records.get(1).accountId());
        }

        @Test
        @DisplayName("readAll skips blank lines")
        void readAllSkipsBlankLines(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("test_blank.txt");
            Files.writeString(file,
                    "050002445376574000000005000000000050\n" +
                    "\n" +
                    "068358619817151600000002700000000027\n");

            List<CardXrefRecord> records = CardXrefFileReader.readAll(file);
            assertEquals(2, records.size());
        }
    }

    @Nested
    @DisplayName("CardXrefRecord Tests")
    class CardXrefRecordTests {

        @Test
        @DisplayName("toPipeDelimited formats correctly")
        void toPipeDelimited() {
            var rec = new CardXrefRecord("0500024453765740", 50, 50);
            assertEquals("0500024453765740|50|50", rec.toPipeDelimited());
        }

        @Test
        @DisplayName("record constants are correct")
        void recordConstants() {
            assertEquals(50, CardXrefRecord.COBOL_RECORD_LENGTH);
            assertEquals(36, CardXrefRecord.MIN_RECORD_LENGTH);
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("full pipeline with synthetic test data")
        void fullPipelineWithTestData(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("synth_xref.txt");
            Files.writeString(file,
                    "111111111111111100000000100000000001\n" +
                    "222222222222222200000000200000000002\n" +
                    "333333333333333300000000300000000003\n");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream original = System.out;
            System.setOut(new PrintStream(baos));

            try {
                int count = Cbact03cApplication.run(file);
                assertEquals(3, count);
            } finally {
                System.setOut(original);
            }

            String output = baos.toString();
            assertTrue(output.contains("START OF EXECUTION OF PROGRAM CBACT03C"));
            assertTrue(output.contains("END OF EXECUTION OF PROGRAM CBACT03C"));
            assertTrue(output.contains("CARD_NUMBER|CUSTOMER_ID|ACCOUNT_ID"));
            assertTrue(output.contains("1111111111111111|1|1"));
            assertTrue(output.contains("2222222222222222|2|2"));
            assertTrue(output.contains("3333333333333333|3|3"));
        }

        @Test
        @DisplayName("application prints header before data")
        void headerBeforeData(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("header_test.txt");
            Files.writeString(file,
                    "050002445376574000000005000000000050\n");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream original = System.out;
            System.setOut(new PrintStream(baos));

            try {
                Cbact03cApplication.run(file);
            } finally {
                System.setOut(original);
            }

            String output = baos.toString();
            int headerIdx = output.indexOf("CARD_NUMBER|CUSTOMER_ID|ACCOUNT_ID");
            int dataIdx = output.indexOf("0500024453765740|50|50");
            assertTrue(headerIdx >= 0, "Header should be present");
            assertTrue(dataIdx > headerIdx, "Data should appear after header");
        }
    }

    @Nested
    @DisplayName("Real Data Tests")
    class RealDataTests {

        @Test
        @DisplayName("process actual cardxref.txt sample data")
        void processRealData() {
            Path dataPath = REAL_DATA_PATH;
            Assumptions.assumeTrue(Files.exists(dataPath),
                    "Real data file not found at " + dataPath);

            List<CardXrefRecord> records = CardXrefFileReader.readAll(dataPath);
            assertFalse(records.isEmpty(), "Should read at least one record");
            assertEquals(50, records.size(), "Sample file contains 50 records");

            // Verify first record
            CardXrefRecord first = records.get(0);
            assertEquals("0500024453765740", first.cardNumber());
            assertEquals(50, first.customerId());
            assertEquals(50, first.accountId());

            // Verify second record
            CardXrefRecord second = records.get(1);
            assertEquals("0683586198171516", second.cardNumber());
            assertEquals(27, second.customerId());
            assertEquals(27, second.accountId());

            // Verify all records have valid card numbers (16 chars, non-blank)
            for (CardXrefRecord rec : records) {
                assertEquals(16, rec.cardNumber().length(),
                        "Card number should be 16 characters");
                assertTrue(rec.customerId() >= 0,
                        "Customer ID should be non-negative");
                assertTrue(rec.accountId() >= 0,
                        "Account ID should be non-negative");
            }
        }

        @Test
        @DisplayName("full application run with real data produces correct output")
        void fullRunWithRealData() {
            Path dataPath = REAL_DATA_PATH;
            Assumptions.assumeTrue(Files.exists(dataPath),
                    "Real data file not found at " + dataPath);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream original = System.out;
            System.setOut(new PrintStream(baos));

            try {
                int count = Cbact03cApplication.run(dataPath);
                assertEquals(50, count);
            } finally {
                System.setOut(original);
            }

            String output = baos.toString();
            assertTrue(output.contains("START OF EXECUTION OF PROGRAM CBACT03C"));
            assertTrue(output.contains("END OF EXECUTION OF PROGRAM CBACT03C"));
            assertTrue(output.contains("CARD_NUMBER|CUSTOMER_ID|ACCOUNT_ID"));

            // Count data lines (header + 50 records + start/end messages)
            String[] lines = output.split("\n");
            // START message + header + 50 records + END message = 53
            assertEquals(53, lines.length);
        }
    }
}
