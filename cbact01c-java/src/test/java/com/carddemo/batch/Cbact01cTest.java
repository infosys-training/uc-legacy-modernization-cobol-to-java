package com.carddemo.batch;

import com.carddemo.batch.io.AccountFileReader;
import com.carddemo.batch.model.AccountRecord;
import com.carddemo.batch.util.CobolDecimalFormatter;
import com.carddemo.batch.util.CobolDecimalParser;
import com.carddemo.batch.util.DateFormatter;
import com.carddemo.batch.util.PackedDecimalUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit tests verifying the Java CBACT01C rewrite produces identical results
 * to the COBOL version for sample inputs.
 */
class Cbact01cTest {

    private static final String SAMPLE_RECORD_1 =
            "00000000001Y00000001940{00000020200{00000010200{2014-11-202025-05-202025-05-2000000000000{00000000000{A000000000          "
            + " ".repeat(178); // 300 chars total

    private static final String SAMPLE_RECORD_2 =
            "00000000002Y00000001580{00000061300{00000054480{2013-06-192024-08-112024-08-1100000000000{00000000000{A000000000          "
            + " ".repeat(178);

    // Record with non-zero cycle debit
    private static final String SAMPLE_RECORD_NONZERO_DEBIT =
            "00000000010Y00000005000{00000030000{00000020000{2015-03-102026-01-152026-01-1500000001000{00000000500{B123456789          "
            + " ".repeat(178);

    // Record with negative balance (-194.00)
    private static final String SAMPLE_RECORD_NEGATIVE =
            "00000000020Y00000001940}00000020200{00000010200{2014-11-202025-05-202025-05-2000000000000{00000000000{A000000000          "
            + " ".repeat(178);

    private Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        tempDir = Files.createTempDirectory("cbact01c-test");
    }

    @AfterEach
    void tearDown() throws IOException {
        // Clean up temp files
        Files.walk(tempDir)
                .sorted(java.util.Comparator.reverseOrder())
                .forEach(path -> {
                    try { Files.deleteIfExists(path); } catch (IOException ignored) {}
                });
    }

    // =====================================================================
    // CobolDecimalParser Tests
    // =====================================================================

    @Nested
    class CobolDecimalParserTests {

        @ParameterizedTest(name = "parse \"{0}\" with scale {1} = {2}")
        @CsvSource({
            "00000001940{, 2, 194.00",
            "00000020200{, 2, 2020.00",
            "00000010200{, 2, 1020.00",
            "00000000000{, 2, 0.00",
            "0000000194}, 2, -19.40",
            "00000001940}, 2, -194.00",
            "0000000100A, 2, 10.01",
            "0000000100J, 2, -10.01",
            "0000000100I, 2, 10.09",
            "0000000100R, 2, -10.09",
            "00000000500{, 2, 50.00",
            "00000001000{, 2, 100.00"
        })
        void testParseOverpunch(String field, int scale, String expected) {
            BigDecimal result = CobolDecimalParser.parse(field, scale);
            assertEquals(new BigDecimal(expected), result);
        }

        @Test
        void testAllPositiveOverpunchChars() {
            // {=0, A=1, B=2, C=3, D=4, E=5, F=6, G=7, H=8, I=9
            // For PIC S9(10)V99 (12-char field):
            assertEquals(new BigDecimal("0.00"), CobolDecimalParser.parse("00000000000{", 2));
            assertEquals(new BigDecimal("0.01"), CobolDecimalParser.parse("00000000000A", 2));
            assertEquals(new BigDecimal("0.02"), CobolDecimalParser.parse("00000000000B", 2));
            assertEquals(new BigDecimal("0.09"), CobolDecimalParser.parse("00000000000I", 2));
        }

        @Test
        void testAllNegativeOverpunchChars() {
            assertEquals(new BigDecimal("0.00"), CobolDecimalParser.parse("00000000000}", 2).abs());
            assertEquals(new BigDecimal("-0.01"), CobolDecimalParser.parse("00000000000J", 2));
            assertEquals(new BigDecimal("-0.02"), CobolDecimalParser.parse("00000000000K", 2));
            assertEquals(new BigDecimal("-0.09"), CobolDecimalParser.parse("00000000000R", 2));
        }

        @Test
        void testLargeValue() {
            // PIC S9(10)V99 max value: 9999999999.99
            BigDecimal result = CobolDecimalParser.parse("99999999999I", 2);
            assertEquals(new BigDecimal("9999999999.99"), result);
        }
    }

    // =====================================================================
    // CobolDecimalFormatter Tests
    // =====================================================================

    @Nested
    class CobolDecimalFormatterTests {

        @ParameterizedTest(name = "format {0} as S9(10)V99 = \"{1}\"")
        @CsvSource({
            "194.00, 00000001940{",
            "2020.00, 00000020200{",
            "0.00, 00000000000{",
            "-194.00, 00000001940}",
            "10.01, 00000000100A",
            "-10.01, 00000000100J",
        })
        void testFormatOverpunch(String value, String expected) {
            BigDecimal bd = new BigDecimal(value);
            String result = CobolDecimalFormatter.format(bd, 12, 2);
            assertEquals(expected, result);
        }

        @Test
        void testRoundTrip() {
            BigDecimal original = new BigDecimal("194.00");
            String formatted = CobolDecimalFormatter.format(original, 12, 2);
            BigDecimal parsed = CobolDecimalParser.parse(formatted, 2);
            assertEquals(0, original.compareTo(parsed));
        }

        @Test
        void testRoundTripNegative() {
            BigDecimal original = new BigDecimal("-2500.00");
            String formatted = CobolDecimalFormatter.format(original, 12, 2);
            BigDecimal parsed = CobolDecimalParser.parse(formatted, 2);
            assertEquals(0, original.compareTo(parsed));
        }
    }

    // =====================================================================
    // PackedDecimalUtil Tests
    // =====================================================================

    @Nested
    class PackedDecimalTests {

        @Test
        void testEncodePositive() {
            // 2525.00 → integer representation 252500 → packed
            byte[] packed = PackedDecimalUtil.encode(new BigDecimal("2525.00"), 12, 2);
            assertEquals(7, packed.length); // (12+1)/2 rounded up = 7 bytes

            // Decode and verify round-trip
            BigDecimal decoded = PackedDecimalUtil.decode(packed, 2);
            assertEquals(0, new BigDecimal("2525.00").compareTo(decoded));
        }

        @Test
        void testEncodeNegative() {
            byte[] packed = PackedDecimalUtil.encode(new BigDecimal("-2500.00"), 12, 2);
            BigDecimal decoded = PackedDecimalUtil.decode(packed, 2);
            assertEquals(0, new BigDecimal("-2500.00").compareTo(decoded));
        }

        @Test
        void testEncodeZero() {
            byte[] packed = PackedDecimalUtil.encode(BigDecimal.ZERO, 12, 2);
            BigDecimal decoded = PackedDecimalUtil.decode(packed, 2);
            assertEquals(0, BigDecimal.ZERO.compareTo(decoded));
        }

        @Test
        void testSignNibble() {
            byte[] positive = PackedDecimalUtil.encode(new BigDecimal("1.00"), 12, 2);
            assertEquals(0x0C, positive[positive.length - 1] & 0x0F); // positive sign

            byte[] negative = PackedDecimalUtil.encode(new BigDecimal("-1.00"), 12, 2);
            assertEquals(0x0D, negative[negative.length - 1] & 0x0F); // negative sign
        }

        @Test
        void testByteLength() {
            assertEquals(7, PackedDecimalUtil.byteLength(12)); // PIC S9(10)V99 → 12 digits → 7 bytes
        }
    }

    // =====================================================================
    // DateFormatter Tests
    // =====================================================================

    @Nested
    class DateFormatterTests {

        @Test
        void testYyyyMmDdToYyyymmdd() {
            String result = DateFormatter.format("2025-05-20", '2', '2');
            assertEquals("20250520            ", result); // 8 chars + 12 spaces = 20
        }

        @Test
        void testYyyymmddToYyyyMmDd() {
            String result = DateFormatter.format("20250520", '1', '1');
            assertEquals("2025-05-20          ", result); // 10 chars + 10 spaces = 20
        }

        @Test
        void testExtractYear() {
            assertEquals("2025", DateFormatter.extractYear("2025-05-20"));
            assertEquals("2014", DateFormatter.extractYear("2014-11-20"));
        }

        @Test
        void testOutputTruncatedTo10() {
            // When moved to PIC X(10), first 10 chars of YYYYMMDD + spaces = "YYYYMMDD  "
            String result = DateFormatter.format("2025-05-20", '2', '2');
            String truncated = result.substring(0, 10);
            assertEquals("20250520  ", truncated);
        }
    }

    // =====================================================================
    // AccountFileReader Tests
    // =====================================================================

    @Nested
    class AccountFileReaderTests {

        @Test
        void testReadSingleRecord() throws IOException {
            try (AccountFileReader reader = new AccountFileReader(new StringReader(SAMPLE_RECORD_1))) {
                AccountRecord record = reader.readNext();
                assertNotNull(record);
                assertEquals("00000000001", record.acctId());
                assertEquals("Y", record.activeStatus());
                assertEquals(0, new BigDecimal("194.00").compareTo(record.currBal()));
                assertEquals(0, new BigDecimal("2020.00").compareTo(record.creditLimit()));
                assertEquals(0, new BigDecimal("1020.00").compareTo(record.cashCreditLimit()));
                assertEquals("2014-11-20", record.openDate());
                assertEquals("2025-05-20", record.expirationDate());
                assertEquals("2025-05-20", record.reissueDate());
                assertEquals(0, BigDecimal.ZERO.compareTo(record.currCycCredit()));
                assertEquals(0, BigDecimal.ZERO.compareTo(record.currCycDebit()));
                assertEquals("A000000000", record.addrZip());
            }
        }

        @Test
        void testReadMultipleRecords() throws IOException {
            String input = SAMPLE_RECORD_1 + "\n" + SAMPLE_RECORD_2;
            try (AccountFileReader reader = new AccountFileReader(new StringReader(input))) {
                List<AccountRecord> records = reader.readAll();
                assertEquals(2, records.size());
                assertEquals("00000000001", records.get(0).acctId());
                assertEquals("00000000002", records.get(1).acctId());
            }
        }

        @Test
        void testReadEndOfFile() throws IOException {
            try (AccountFileReader reader = new AccountFileReader(new StringReader(SAMPLE_RECORD_1))) {
                assertNotNull(reader.readNext());
                assertNull(reader.readNext()); // EOF
            }
        }

        @Test
        void testReadNegativeBalance() throws IOException {
            try (AccountFileReader reader = new AccountFileReader(new StringReader(SAMPLE_RECORD_NEGATIVE))) {
                AccountRecord record = reader.readNext();
                assertNotNull(record);
                // "00000001940}" → digits "000000019400", movePointLeft(2)=194.00, negated → -194.00
                assertEquals(0, new BigDecimal("-194.00").compareTo(record.currBal()));
            }
        }
    }

    // =====================================================================
    // Integration Test: Full Pipeline
    // =====================================================================

    @Nested
    class IntegrationTests {

        @Test
        void testFullPipelineSingleRecord() throws IOException {
            // Write sample input
            Path inputFile = tempDir.resolve("acctfile.dat");
            Files.writeString(inputFile, SAMPLE_RECORD_1 + "\n", StandardCharsets.ISO_8859_1);

            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            Cbact01cApplication app = new Cbact01cApplication(inputFile, outFile, arryFile, vbrcFile);
            int count = app.execute();

            assertEquals(1, count);
            assertTrue(Files.exists(outFile));
            assertTrue(Files.exists(arryFile));
            assertTrue(Files.exists(vbrcFile));

            // Verify output file size: 107 bytes per record
            assertEquals(107, Files.size(outFile));

            // Verify array file size: 110 bytes per record
            assertEquals(110, Files.size(arryFile));

            // Verify VBRC file size: 12 + 39 = 51 bytes (no RDW)
            assertEquals(51, Files.size(vbrcFile));
        }

        @Test
        void testFullPipelineMultipleRecords() throws IOException {
            Path inputFile = tempDir.resolve("acctfile.dat");
            String input = SAMPLE_RECORD_1 + "\n" + SAMPLE_RECORD_2 + "\n";
            Files.writeString(inputFile, input, StandardCharsets.ISO_8859_1);

            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            Cbact01cApplication app = new Cbact01cApplication(inputFile, outFile, arryFile, vbrcFile);
            int count = app.execute();

            assertEquals(2, count);
            assertEquals(107 * 2, Files.size(outFile));
            assertEquals(110 * 2, Files.size(arryFile));
            assertEquals(51 * 2, Files.size(vbrcFile));
        }

        @Test
        void testOutFileContent() throws IOException {
            Path inputFile = tempDir.resolve("acctfile.dat");
            Files.writeString(inputFile, SAMPLE_RECORD_1 + "\n", StandardCharsets.ISO_8859_1);

            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            new Cbact01cApplication(inputFile, outFile, arryFile, vbrcFile).execute();

            byte[] outBytes = Files.readAllBytes(outFile);
            String outStr = new String(outBytes, 0, 100, StandardCharsets.ISO_8859_1);

            // Verify fields in output
            assertEquals("00000000001", outStr.substring(0, 11));  // ACCT-ID
            assertEquals("Y", outStr.substring(11, 12));           // ACTIVE-STATUS
            assertEquals("00000001940{", outStr.substring(12, 24)); // CURR-BAL
            assertEquals("00000020200{", outStr.substring(24, 36)); // CREDIT-LIMIT
            assertEquals("00000010200{", outStr.substring(36, 48)); // CASH-CREDIT-LIMIT
            assertEquals("2014-11-20", outStr.substring(48, 58));   // OPEN-DATE
            assertEquals("2025-05-20", outStr.substring(58, 68));   // EXPIRATION-DATE
            assertEquals("20250520  ", outStr.substring(68, 78));   // REISSUE-DATE (reformatted!)
            assertEquals("00000000000{", outStr.substring(78, 90)); // CYC-CREDIT

            // CYC-DEBIT is COMP-3 at bytes 90-96 (7 bytes)
            // Since original debit is 0, business rule substitutes 2525.00
            byte[] debitPacked = new byte[7];
            System.arraycopy(outBytes, 90, debitPacked, 0, 7);
            BigDecimal debit = PackedDecimalUtil.decode(debitPacked, 2);
            assertEquals(0, new BigDecimal("2525.00").compareTo(debit));

            // GROUP-ID at bytes 97-106
            String groupId = new String(outBytes, 97, 10, StandardCharsets.ISO_8859_1);
            assertEquals("          ", groupId); // spaces (original was spaces)
        }

        @Test
        void testOutFileNonZeroDebit() throws IOException {
            Path inputFile = tempDir.resolve("acctfile.dat");
            Files.writeString(inputFile, SAMPLE_RECORD_NONZERO_DEBIT + "\n", StandardCharsets.ISO_8859_1);

            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            new Cbact01cApplication(inputFile, outFile, arryFile, vbrcFile).execute();

            byte[] outBytes = Files.readAllBytes(outFile);

            // CYC-DEBIT at bytes 90-96 should be 50.00 (NOT 2525.00 since original is non-zero)
            byte[] debitPacked = new byte[7];
            System.arraycopy(outBytes, 90, debitPacked, 0, 7);
            BigDecimal debit = PackedDecimalUtil.decode(debitPacked, 2);
            assertEquals(0, new BigDecimal("50.00").compareTo(debit));
        }

        @Test
        void testArrayFileContent() throws IOException {
            Path inputFile = tempDir.resolve("acctfile.dat");
            Files.writeString(inputFile, SAMPLE_RECORD_1 + "\n", StandardCharsets.ISO_8859_1);

            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            new Cbact01cApplication(inputFile, outFile, arryFile, vbrcFile).execute();

            byte[] arrBytes = Files.readAllBytes(arryFile);
            assertEquals(110, arrBytes.length);

            String arrStr = new String(arrBytes, StandardCharsets.ISO_8859_1);

            // ARR-ACCT-ID
            assertEquals("00000000001", arrStr.substring(0, 11));

            // Entry 1: balance = 194.00 (DISPLAY), debit = 1005.00 (COMP-3)
            assertEquals("00000001940{", arrStr.substring(11, 23)); // BAL(1)
            byte[] debit1 = new byte[7];
            System.arraycopy(arrBytes, 23, debit1, 0, 7);
            assertEquals(0, new BigDecimal("1005.00").compareTo(PackedDecimalUtil.decode(debit1, 2)));

            // Entry 2: balance = 194.00, debit = 1525.00
            assertEquals("00000001940{", arrStr.substring(30, 42)); // BAL(2)
            byte[] debit2 = new byte[7];
            System.arraycopy(arrBytes, 42, debit2, 0, 7);
            assertEquals(0, new BigDecimal("1525.00").compareTo(PackedDecimalUtil.decode(debit2, 2)));

            // Entry 3: balance = -1025.00, debit = -2500.00
            String bal3 = arrStr.substring(49, 61);
            assertEquals(0, new BigDecimal("-1025.00").compareTo(CobolDecimalParser.parse(bal3, 2)));
            byte[] debit3 = new byte[7];
            System.arraycopy(arrBytes, 61, debit3, 0, 7);
            assertEquals(0, new BigDecimal("-2500.00").compareTo(PackedDecimalUtil.decode(debit3, 2)));

            // Entry 4 and 5: zeros
            String bal4 = arrStr.substring(68, 80);
            assertEquals(0, BigDecimal.ZERO.compareTo(CobolDecimalParser.parse(bal4, 2)));

            // Filler at end (4 bytes of spaces)
            assertEquals("    ", arrStr.substring(106, 110));
        }

        @Test
        void testVbrcFileContent() throws IOException {
            Path inputFile = tempDir.resolve("acctfile.dat");
            Files.writeString(inputFile, SAMPLE_RECORD_1 + "\n", StandardCharsets.ISO_8859_1);

            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            new Cbact01cApplication(inputFile, outFile, arryFile, vbrcFile).execute();

            byte[] vbrBytes = Files.readAllBytes(vbrcFile);
            assertEquals(51, vbrBytes.length); // VB1(12) + VB2(39)

            String vbrStr = new String(vbrBytes, StandardCharsets.ISO_8859_1);

            // VB1 record (12 bytes): ACCT-ID + ACTIVE-STATUS
            assertEquals("00000000001Y", vbrStr.substring(0, 12));

            // VB2 record (39 bytes): ACCT-ID + CURR-BAL + CREDIT-LIMIT + REISSUE-YYYY
            assertEquals("00000000001", vbrStr.substring(12, 23)); // VB2 ACCT-ID
            assertEquals("00000001940{", vbrStr.substring(23, 35)); // VB2 CURR-BAL
            assertEquals("00000020200{", vbrStr.substring(35, 47)); // VB2 CREDIT-LIMIT
            assertEquals("2025", vbrStr.substring(47, 51)); // VB2 REISSUE-YYYY
        }

        @Test
        void testWithRealSampleData() throws IOException {
            // Use actual acctdata.txt from the repo if available
            Path realData = Path.of("../app/data/ASCII/acctdata.txt");
            if (!Files.exists(realData)) {
                realData = Path.of("/home/ubuntu/repos/uc-legacy-modernization-cobol-to-java/app/data/ASCII/acctdata.txt");
            }
            if (!Files.exists(realData)) {
                return; // Skip if sample data not available
            }

            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            Cbact01cApplication app = new Cbact01cApplication(realData, outFile, arryFile, vbrcFile);
            int count = app.execute();

            assertEquals(50, count); // Known: 50 records in sample data
            assertEquals(107 * 50, Files.size(outFile));
            assertEquals(110 * 50, Files.size(arryFile));
            assertEquals(51 * 50, Files.size(vbrcFile));
        }
    }

    // =====================================================================
    // Edge Case Tests
    // =====================================================================

    @Nested
    class EdgeCaseTests {

        @Test
        void testEmptyInputFile() throws IOException {
            Path inputFile = tempDir.resolve("empty.dat");
            Files.writeString(inputFile, "");

            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            Cbact01cApplication app = new Cbact01cApplication(inputFile, outFile, arryFile, vbrcFile);
            int count = app.execute();

            assertEquals(0, count);
            assertEquals(0, Files.size(outFile));
        }

        @Test
        void testDebitZeroSubstitution() {
            // Business rule: if ACCT-CURR-CYC-DEBIT = 0, output becomes 2525.00
            // This is verified in testOutFileContent above
            // Here we just confirm the threshold is exactly zero
            BigDecimal zero = CobolDecimalParser.parse("00000000000{", 2);
            assertEquals(0, BigDecimal.ZERO.compareTo(zero));
        }

        @Test
        void testDateReformatting() {
            // YYYY-MM-DD → YYYYMMDD (type '2' → outtype '2')
            String formatted = DateFormatter.format("2025-05-20", '2', '2');
            // Truncated to 10 chars for PIC X(10): "20250520  "
            assertEquals("20250520  ", formatted.substring(0, 10));
        }

        @Test
        void testOverpunchRoundTripAllDigits() {
            // Verify round-trip for all possible trailing digits
            for (int i = 0; i <= 9; i++) {
                BigDecimal positive = new BigDecimal(i).movePointLeft(2); // 0.00 to 0.09
                String formatted = CobolDecimalFormatter.format(positive, 12, 2);
                BigDecimal parsed = CobolDecimalParser.parse(formatted, 2);
                assertEquals(0, positive.compareTo(parsed),
                        "Round-trip failed for positive digit " + i);

                BigDecimal negative = positive.negate();
                formatted = CobolDecimalFormatter.format(negative, 12, 2);
                parsed = CobolDecimalParser.parse(formatted, 2);
                assertEquals(0, negative.compareTo(parsed),
                        "Round-trip failed for negative digit " + i);
            }
        }
    }
}
