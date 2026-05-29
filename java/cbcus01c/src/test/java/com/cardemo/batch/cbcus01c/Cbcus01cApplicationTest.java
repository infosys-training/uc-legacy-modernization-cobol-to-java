package com.cardemo.batch.cbcus01c;

import com.cardemo.batch.cbcus01c.io.CobolFieldParser;
import com.cardemo.batch.cbcus01c.io.CustomerFileReader;
import com.cardemo.batch.cbcus01c.model.CustomerRecord;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class Cbcus01cApplicationTest {

    // First record from custdata.txt (known values verified against COBOL copybook offsets)
    private static final String RECORD_1 =
            "000000001Immanuel                 Madeline                 "
          + "Kessler                  "
          + "618 Deshaun Route                                 "
          + "Apt. 802                                          "
          + "Altenwerthshire                                   "
          + "NCUSA12546     "
          + "(908)119-8310  (373)693-8684  "
          + "020973888"
          + "00000000000049368437"
          + "1961-06-08"
          + "0053581756"
          + "Y274"
          + " ".repeat(168);

    @Nested
    @DisplayName("CobolFieldParser Tests")
    class CobolFieldParserTests {

        @Test
        @DisplayName("extractField returns correct substring")
        void extractField_basic() {
            String line = "ABCDEFGHIJ";
            assertEquals("ABC", CobolFieldParser.extractField(line, 0, 3));
            assertEquals("DEF", CobolFieldParser.extractField(line, 3, 3));
            assertEquals("J", CobolFieldParser.extractField(line, 9, 1));
        }

        @Test
        @DisplayName("extractField pads short lines with spaces")
        void extractField_shortLine() {
            String line = "ABC";
            String result = CobolFieldParser.extractField(line, 0, 5);
            assertEquals("ABC  ", result);
            assertEquals(5, result.length());
        }

        @Test
        @DisplayName("extractField returns spaces when offset exceeds line length")
        void extractField_beyondLine() {
            String line = "ABC";
            String result = CobolFieldParser.extractField(line, 10, 5);
            assertEquals("     ", result);
        }

        @Test
        @DisplayName("parseUnsignedInt parses numeric strings")
        void parseUnsignedInt_basic() {
            assertEquals(1, CobolFieldParser.parseUnsignedInt("000000001"));
            assertEquals(274, CobolFieldParser.parseUnsignedInt("274"));
            assertEquals(0, CobolFieldParser.parseUnsignedInt("000"));
        }

        @Test
        @DisplayName("parseUnsignedInt handles blank input")
        void parseUnsignedInt_blank() {
            assertEquals(0, CobolFieldParser.parseUnsignedInt("   "));
        }

        @Test
        @DisplayName("parseAlphanumeric trims trailing spaces")
        void parseAlphanumeric_trim() {
            assertEquals("Immanuel", CobolFieldParser.parseAlphanumeric("Immanuel                 "));
            assertEquals("", CobolFieldParser.parseAlphanumeric("     "));
        }

        @Test
        @DisplayName("parseAlphanumeric preserves leading spaces")
        void parseAlphanumeric_preserveLeading() {
            assertEquals("  Hello", CobolFieldParser.parseAlphanumeric("  Hello   "));
        }

        @Test
        @DisplayName("parseSignedDecimal handles plain digits")
        void parseSignedDecimal_plainDigits() {
            assertEquals(new BigDecimal("123.45"),
                    CobolFieldParser.parseSignedDecimal("12345", 2));
        }

        @Test
        @DisplayName("parseSignedDecimal handles positive overpunch")
        void parseSignedDecimal_positiveOverpunch() {
            // 1234E = +12345 with 2 implied decimals = 123.45
            assertEquals(new BigDecimal("123.45"),
                    CobolFieldParser.parseSignedDecimal("1234E", 2));
        }

        @Test
        @DisplayName("parseSignedDecimal handles negative overpunch")
        void parseSignedDecimal_negativeOverpunch() {
            // 1234N = -12345 with 2 implied decimals = -123.45
            assertEquals(new BigDecimal("-123.45"),
                    CobolFieldParser.parseSignedDecimal("1234N", 2));
        }

        @Test
        @DisplayName("parseSignedDecimal handles zero with overpunch")
        void parseSignedDecimal_zeroOverpunch() {
            assertEquals(BigDecimal.ZERO.setScale(2),
                    CobolFieldParser.parseSignedDecimal("0000{", 2).setScale(2));
        }

        @Test
        @DisplayName("parseSignedDecimal handles blank input")
        void parseSignedDecimal_blank() {
            assertEquals(BigDecimal.ZERO, CobolFieldParser.parseSignedDecimal("     ", 2));
        }

        @Test
        @DisplayName("formatSignedDecimal round-trips through parseSignedDecimal")
        void formatSignedDecimal_roundTrip() {
            BigDecimal original = new BigDecimal("123.45");
            String formatted = CobolFieldParser.formatSignedDecimal(original, 7, 2);
            BigDecimal parsed = CobolFieldParser.parseSignedDecimal(formatted, 2);
            assertEquals(0, original.compareTo(parsed));
        }

        @Test
        @DisplayName("formatSignedDecimal encodes negative values")
        void formatSignedDecimal_negative() {
            BigDecimal value = new BigDecimal("-99.99");
            String formatted = CobolFieldParser.formatSignedDecimal(value, 6, 2);
            assertTrue(formatted.endsWith("R")); // -9 -> R
            BigDecimal parsed = CobolFieldParser.parseSignedDecimal(formatted, 2);
            assertEquals(0, value.compareTo(parsed));
        }

        @Test
        @DisplayName("Invalid overpunch character throws exception")
        void parseSignedDecimal_invalidOverpunch() {
            assertThrows(IllegalArgumentException.class,
                    () -> CobolFieldParser.parseSignedDecimal("123X", 2));
        }
    }

    @Nested
    @DisplayName("CustomerRecord Tests")
    class CustomerRecordTests {

        @Test
        @DisplayName("Record 1 field extraction matches known data")
        void record1_fieldExtraction() {
            CustomerRecord record = CustomerFileReader.parseRecord(RECORD_1);

            assertEquals(1, record.custId());
            assertEquals("Immanuel", record.firstName());
            assertEquals("Madeline", record.middleName());
            assertEquals("Kessler", record.lastName());
            assertEquals("618 Deshaun Route", record.addrLine1());
            assertEquals("Apt. 802", record.addrLine2());
            assertEquals("Altenwerthshire", record.addrLine3());
            assertEquals("NC", record.stateCd());
            assertEquals("USA", record.countryCd());
            assertEquals("12546", record.zip());
            assertEquals("(908)119-8310", record.phoneNum1());
            assertEquals("(373)693-8684", record.phoneNum2());
            assertEquals("020973888", record.ssn());
            assertEquals("00000000000049368437", record.govtIssuedId());
            assertEquals("1961-06-08", record.dob());
            assertEquals("0053581756", record.eftAccountId());
            assertEquals("Y", record.priCardHolderInd());
            assertEquals(274, record.ficoCreditScore());
        }

        @Test
        @DisplayName("All 18 fields are correctly positioned (offset verification)")
        void record1_offsetVerification() {
            // Verify the total record length is 500
            assertEquals(500, RECORD_1.length());

            // Verify field boundaries by checking raw substrings before parsing
            assertEquals("000000001", RECORD_1.substring(0, 9));
            assertEquals("Immanuel                 ", RECORD_1.substring(9, 34));
            assertEquals("Madeline                 ", RECORD_1.substring(34, 59));
            assertEquals("Kessler                  ", RECORD_1.substring(59, 84));
            assertEquals("NC", RECORD_1.substring(234, 236));
            assertEquals("USA", RECORD_1.substring(236, 239));
            assertEquals("Y", RECORD_1.substring(328, 329));
            assertEquals("274", RECORD_1.substring(329, 332));
        }

        @Test
        @DisplayName("toCsvLine produces pipe-delimited output")
        void toCsvLine_format() {
            CustomerRecord record = CustomerFileReader.parseRecord(RECORD_1);
            String csv = record.toCsvLine();

            String[] fields = csv.split("\\|", -1);
            assertEquals(18, fields.length);
            assertEquals("1", fields[0]);
            assertEquals("Immanuel", fields[1]);
            assertEquals("Kessler", fields[3]);
            assertEquals("NC", fields[7]);
            assertEquals("Y", fields[16]);
            assertEquals("274", fields[17]);
        }

        @Test
        @DisplayName("CSV header has 18 columns")
        void csvHeader_columnCount() {
            String[] headers = CustomerRecord.CSV_HEADER.split("\\|", -1);
            assertEquals(18, headers.length);
            assertEquals("CUST_ID", headers[0]);
            assertEquals("FICO_CREDIT_SCORE", headers[17]);
        }
    }

    @Nested
    @DisplayName("CustomerFileReader Tests")
    class CustomerFileReaderTests {

        @Test
        @DisplayName("Reads all records from test data file")
        void readTestDataFile() throws IOException {
            Path testFile = Path.of("src/test/resources/test-custdata.txt");
            if (!Files.exists(testFile)) {
                testFile = Path.of(System.getProperty("user.dir"),
                        "src/test/resources/test-custdata.txt");
            }
            try (CustomerFileReader reader = new CustomerFileReader(testFile)) {
                int count = 0;
                CustomerRecord record;
                while ((record = reader.readNext()) != null) {
                    count++;
                    assertTrue(record.custId() > 0,
                            "Record " + count + " should have a positive customer ID");
                }
                assertEquals(3, count, "Test file should contain 3 records");
            }
        }

        @Test
        @DisplayName("Skips blank lines")
        void skipsBlankLines(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("blank-test.txt");
            Files.writeString(file,
                    RECORD_1 + "\n\n" + RECORD_1 + "\n",
                    StandardCharsets.UTF_8);

            try (CustomerFileReader reader = new CustomerFileReader(file)) {
                assertNotNull(reader.readNext());
                assertNotNull(reader.readNext());
                assertNull(reader.readNext());
            }
        }

        @Test
        @DisplayName("Pads short lines to 500 characters")
        void padsShortLines(@TempDir Path tempDir) throws IOException {
            String shortRecord = "000000099ShortName";
            Path file = tempDir.resolve("short-test.txt");
            Files.writeString(file, shortRecord + "\n", StandardCharsets.UTF_8);

            try (CustomerFileReader reader = new CustomerFileReader(file)) {
                CustomerRecord record = reader.readNext();
                assertNotNull(record);
                assertEquals(99, record.custId());
                assertEquals("ShortName", record.firstName());
            }
        }

        @Test
        @DisplayName("Returns null on empty file")
        void emptyFile(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("empty.txt");
            Files.writeString(file, "", StandardCharsets.UTF_8);

            try (CustomerFileReader reader = new CustomerFileReader(file)) {
                assertNull(reader.readNext());
            }
        }

        @Test
        @DisplayName("parseRecord handles record with all spaces in optional fields")
        void parseRecord_spaceFilled() {
            String record = "000000042" + " ".repeat(491);
            CustomerRecord parsed = CustomerFileReader.parseRecord(record);
            assertEquals(42, parsed.custId());
            assertEquals("", parsed.firstName());
            assertEquals("", parsed.lastName());
            assertEquals(0, parsed.ficoCreditScore());
        }
    }

    @Nested
    @DisplayName("Integration Tests")
    class IntegrationTests {

        @Test
        @DisplayName("Integration test with real custdata.txt")
        void integrationWithRealData() throws IOException {
            Path realFile = Path.of("../../app/data/ASCII/custdata.txt");
            if (!Files.exists(realFile)) {
                realFile = Path.of(System.getProperty("user.dir"),
                        "../../app/data/ASCII/custdata.txt");
            }
            if (!Files.exists(realFile)) {
                System.out.println("Skipping integration test: custdata.txt not found");
                return;
            }

            List<CustomerRecord> records = Cbcus01cApplication.readAllCustomers(realFile);
            assertEquals(50, records.size(), "custdata.txt should have 50 records");

            // Verify first record
            CustomerRecord first = records.get(0);
            assertEquals(1, first.custId());
            assertEquals("Immanuel", first.firstName());
            assertEquals("Kessler", first.lastName());
            assertEquals("NC", first.stateCd());
            assertEquals("1961-06-08", first.dob());

            // Verify second record
            CustomerRecord second = records.get(1);
            assertEquals(2, second.custId());
            assertEquals("Enrico", second.firstName());
            assertEquals("Rosenbaum", second.lastName());

            // Verify all records have positive IDs and valid FICO scores
            for (int i = 0; i < records.size(); i++) {
                CustomerRecord r = records.get(i);
                assertTrue(r.custId() > 0,
                        "Record " + (i + 1) + " should have positive ID");
                assertTrue(r.ficoCreditScore() >= 0 && r.ficoCreditScore() <= 999,
                        "Record " + (i + 1) + " FICO score should be 0-999, was " + r.ficoCreditScore());
            }
        }

        @Test
        @DisplayName("CSV output includes header and all records")
        void csvOutput() throws IOException {
            Path realFile = Path.of("../../app/data/ASCII/custdata.txt");
            if (!Files.exists(realFile)) {
                realFile = Path.of(System.getProperty("user.dir"),
                        "../../app/data/ASCII/custdata.txt");
            }
            if (!Files.exists(realFile)) {
                System.out.println("Skipping CSV output test: custdata.txt not found");
                return;
            }

            List<CustomerRecord> records = Cbcus01cApplication.readAllCustomers(realFile);

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            PrintStream ps = new PrintStream(baos, true, StandardCharsets.UTF_8);
            Cbcus01cApplication.writeCsv(ps, records);

            String output = baos.toString(StandardCharsets.UTF_8);
            String[] lines = output.split("\n");

            // Header + 50 records
            assertEquals(51, lines.length);
            assertTrue(lines[0].startsWith("CUST_ID|"));
            assertEquals(18, lines[0].split("\\|", -1).length);
            assertEquals(18, lines[1].split("\\|", -1).length);
        }

        @Test
        @DisplayName("Last record in custdata.txt is parsed correctly")
        void lastRecord() throws IOException {
            Path realFile = Path.of("../../app/data/ASCII/custdata.txt");
            if (!Files.exists(realFile)) {
                realFile = Path.of(System.getProperty("user.dir"),
                        "../../app/data/ASCII/custdata.txt");
            }
            if (!Files.exists(realFile)) {
                System.out.println("Skipping last record test: custdata.txt not found");
                return;
            }

            List<CustomerRecord> records = Cbcus01cApplication.readAllCustomers(realFile);
            CustomerRecord last = records.get(records.size() - 1);
            assertTrue(last.custId() > 0);
            assertFalse(last.firstName().isEmpty());
            assertFalse(last.lastName().isEmpty());
        }
    }

    @Nested
    @DisplayName("Edge Case Tests")
    class EdgeCaseTests {

        @Test
        @DisplayName("Customer ID with leading zeros")
        void leadingZeroCustId() {
            String record = "000000001" + " ".repeat(491);
            CustomerRecord parsed = CustomerFileReader.parseRecord(record);
            assertEquals(1, parsed.custId());
        }

        @Test
        @DisplayName("Maximum FICO score (999)")
        void maxFicoScore() {
            StringBuilder sb = new StringBuilder();
            sb.append("000000001");  // custId (9)
            sb.append(" ".repeat(320)); // fields up to offset 329
            sb.append("999");        // fico score
            sb.append(" ".repeat(168)); // filler
            assertEquals(500, sb.length());
            CustomerRecord parsed = CustomerFileReader.parseRecord(sb.toString());
            assertEquals(999, parsed.ficoCreditScore());
        }

        @Test
        @DisplayName("Zero FICO score")
        void zeroFicoScore() {
            StringBuilder sb = new StringBuilder();
            sb.append("000000001");  // custId (9)
            sb.append(" ".repeat(320)); // fields up to offset 329
            sb.append("000");        // fico score
            sb.append(" ".repeat(168)); // filler
            assertEquals(500, sb.length());
            CustomerRecord parsed = CustomerFileReader.parseRecord(sb.toString());
            assertEquals(0, parsed.ficoCreditScore());
        }

        @Test
        @DisplayName("Single character fields (priCardHolderInd)")
        void singleCharField() {
            CustomerRecord record = CustomerFileReader.parseRecord(RECORD_1);
            assertEquals(1, record.priCardHolderInd().length());
            assertEquals("Y", record.priCardHolderInd());
        }

        @Test
        @DisplayName("SSN is treated as string (preserves leading zeros)")
        void ssnPreservesLeadingZeros() {
            CustomerRecord record = CustomerFileReader.parseRecord(RECORD_1);
            assertEquals("020973888", record.ssn());
        }

        @Test
        @DisplayName("Multiple records from temp file")
        void multipleRecords(@TempDir Path tempDir) throws IOException {
            String rec1 = "000000001" + "Alice" + " ".repeat(20) + " ".repeat(466);
            String rec2 = "000000002" + "Bob" + " ".repeat(22) + " ".repeat(466);
            // Ensure records are exactly 500 chars
            rec1 = rec1.substring(0, 500);
            rec2 = rec2.substring(0, 500);

            Path file = tempDir.resolve("multi.txt");
            Files.writeString(file, rec1 + "\n" + rec2 + "\n", StandardCharsets.UTF_8);

            List<CustomerRecord> records = Cbcus01cApplication.readAllCustomers(file);
            assertEquals(2, records.size());
            assertEquals(1, records.get(0).custId());
            assertEquals(2, records.get(1).custId());
            assertEquals("Alice", records.get(0).firstName());
            assertEquals("Bob", records.get(1).firstName());
        }
    }
}
