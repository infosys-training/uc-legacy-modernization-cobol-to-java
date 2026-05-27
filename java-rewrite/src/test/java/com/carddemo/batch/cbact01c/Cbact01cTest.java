package com.carddemo.batch.cbact01c;

import com.carddemo.batch.cbact01c.io.AccountFileReader;
import com.carddemo.batch.cbact01c.model.AccountRecord;
import com.carddemo.batch.cbact01c.model.ArrayAccountRecord;
import com.carddemo.batch.cbact01c.model.OutputAccountRecord;
import com.carddemo.batch.cbact01c.model.VbrRecord;
import com.carddemo.batch.cbact01c.util.CobolZonedDecimalParser;
import com.carddemo.batch.cbact01c.util.DateFormatter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests that the Java rewrite of CBACT01C produces identical results
 * to the COBOL version for sample account inputs.
 *
 * Test strategy:
 * <ol>
 *   <li>Unit tests for zoned decimal parsing (COBOL sign overpunch)</li>
 *   <li>Unit tests for date formatting (COBDATFT replacement)</li>
 *   <li>Unit tests for each business rule in AccountProcessor</li>
 *   <li>Integration test reading actual COBOL sample data and verifying outputs</li>
 * </ol>
 */
class Cbact01cTest {

    private AccountProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new AccountProcessor();
    }

    // ========================================================================
    // Zoned Decimal Parsing — COBOL PIC S9(n)V99 with sign overpunch
    // ========================================================================
    @Nested
    @DisplayName("CobolZonedDecimalParser")
    class ZonedDecimalTests {

        @ParameterizedTest(name = "parse \"{0}\" as PIC S9(10)V99 = {1}")
        @CsvSource({
                // Positive values with '{' overpunch (= +0)
                "'00000001940{', 194.00",
                "'00000020200{', 2020.00",
                "'00000010200{', 1020.00",
                "'00000000000{', 0.00",
                // Positive values with letter overpunch
                "'00000000194A', 19.41",    // A = +1
                "'00000000194I', 19.49",    // I = +9
                // Negative values
                "'00000001940}', -194.00",   // } = -0
                "'00000000194J', -19.41",    // J = -1
                "'00000000194R', -19.49",    // R = -9
                // Zero
                "'00000000000{', 0.00",
                // Large values
                "'99999999999{', 9999999999.90",
        })
        void parsesZonedDecimalCorrectly(String raw, String expected) {
            BigDecimal result = CobolZonedDecimalParser.parse(raw, 10, 2);
            assertEquals(new BigDecimal(expected), result);
        }

        @Test
        @DisplayName("rejects wrong-length input")
        void rejectsWrongLength() {
            assertThrows(IllegalArgumentException.class,
                    () -> CobolZonedDecimalParser.parse("123", 10, 2));
        }

        @Test
        @DisplayName("handles null/empty input as zero")
        void handlesNullEmpty() {
            assertEquals(BigDecimal.ZERO, CobolZonedDecimalParser.parse(null, 10, 2));
            assertEquals(BigDecimal.ZERO, CobolZonedDecimalParser.parse("", 10, 2));
        }
    }

    // ========================================================================
    // Date Formatting — COBDATFT replacement
    // ========================================================================
    @Nested
    @DisplayName("DateFormatter")
    class DateFormatterTests {

        @Test
        @DisplayName("YYYY-MM-DD → YYYYMMDD (COBDATFT type 2→2)")
        void convertsHyphenatedToCompact() {
            assertEquals("20250520  ", DateFormatter.toCompactDate("2025-05-20"));
        }

        @Test
        @DisplayName("YYYYMMDD → YYYY-MM-DD (COBDATFT type 1→1)")
        void convertsCompactToHyphenated() {
            assertEquals("2025-05-20", DateFormatter.toHyphenatedDate("20250520"));
        }

        @Test
        @DisplayName("extracts year from YYYY-MM-DD")
        void extractsYear() {
            assertEquals("2025", DateFormatter.extractYear("2025-05-20"));
        }

        @Test
        @DisplayName("handles blank/null dates")
        void handlesBlankDates() {
            assertEquals("          ", DateFormatter.toCompactDate(null));
            assertEquals("          ", DateFormatter.toCompactDate(""));
            assertEquals("          ", DateFormatter.toCompactDate("   "));
        }

        @ParameterizedTest(name = "toCompactDate(\"{0}\") = \"{1}\"")
        @CsvSource({
                "'2014-11-20', '20141120  '",
                "'2013-06-19', '20130619  '",
                "'2024-01-10', '20240110  '",
                "'2023-12-16', '20231216  '",
        })
        void convertsVariousDates(String input, String expected) {
            assertEquals(expected, DateFormatter.toCompactDate(input));
        }
    }

    // ========================================================================
    // AccountProcessor — Business Logic Rules
    // ========================================================================
    @Nested
    @DisplayName("AccountProcessor — Output Record")
    class OutputRecordTests {

        @Test
        @DisplayName("substitutes 2525.00 when curr-cyc-debit is zero")
        void substitutesCycDebitWhenZero() {
            AccountRecord input = makeAccountRecord(1, BigDecimal.ZERO);
            OutputAccountRecord out = processor.toOutputRecord(input);
            assertEquals(new BigDecimal("2525.00"), out.currCycDebit());
        }

        @Test
        @DisplayName("preserves non-zero curr-cyc-debit")
        void preservesNonZeroCycDebit() {
            BigDecimal debit = new BigDecimal("150.75");
            AccountRecord input = makeAccountRecord(1, debit);
            OutputAccountRecord out = processor.toOutputRecord(input);
            assertEquals(debit, out.currCycDebit());
        }

        @Test
        @DisplayName("reformats reissue date YYYY-MM-DD → YYYYMMDD")
        void reformatsReissueDate() {
            AccountRecord input = makeAccountRecord(1, BigDecimal.ZERO);
            OutputAccountRecord out = processor.toOutputRecord(input);
            assertEquals("20250520  ", out.reissueDate());
        }

        @Test
        @DisplayName("copies all other fields unchanged")
        void copiesFieldsUnchanged() {
            AccountRecord input = makeAccountRecord(42, new BigDecimal("100.00"));
            OutputAccountRecord out = processor.toOutputRecord(input);
            assertEquals(42, out.acctId());
            assertEquals("Y", out.activeStatus());
            assertEquals(new BigDecimal("194.00"), out.currBal());
            assertEquals(new BigDecimal("2020.00"), out.creditLimit());
            assertEquals(new BigDecimal("1020.00"), out.cashCreditLimit());
            assertEquals("2014-11-20", out.openDate());
            assertEquals("2025-05-20", out.expirationDate());
            assertEquals(new BigDecimal("0.00"), out.currCycCredit());
            assertEquals("A000000000", out.groupId());
        }
    }

    @Nested
    @DisplayName("AccountProcessor — Array Record")
    class ArrayRecordTests {

        @Test
        @DisplayName("slot 1: actual balance, debit 1005.00")
        void slot1HasActualBalanceAndFixedDebit() {
            AccountRecord input = makeAccountRecord(1, BigDecimal.ZERO);
            ArrayAccountRecord arr = processor.toArrayRecord(input);
            assertEquals(new BigDecimal("194.00"), arr.balanceEntries()[0].currBal());
            assertEquals(new BigDecimal("1005.00"), arr.balanceEntries()[0].currCycDebit());
        }

        @Test
        @DisplayName("slot 2: actual balance, debit 1525.00")
        void slot2HasActualBalanceAndFixedDebit() {
            AccountRecord input = makeAccountRecord(1, BigDecimal.ZERO);
            ArrayAccountRecord arr = processor.toArrayRecord(input);
            assertEquals(new BigDecimal("194.00"), arr.balanceEntries()[1].currBal());
            assertEquals(new BigDecimal("1525.00"), arr.balanceEntries()[1].currCycDebit());
        }

        @Test
        @DisplayName("slot 3: fixed values -1025.00 / -2500.00")
        void slot3HasFixedNegativeValues() {
            AccountRecord input = makeAccountRecord(1, BigDecimal.ZERO);
            ArrayAccountRecord arr = processor.toArrayRecord(input);
            assertEquals(new BigDecimal("-1025.00"), arr.balanceEntries()[2].currBal());
            assertEquals(new BigDecimal("-2500.00"), arr.balanceEntries()[2].currCycDebit());
        }

        @Test
        @DisplayName("slots 4-5: zeroes (COBOL INITIALIZE)")
        void slots4And5AreZero() {
            AccountRecord input = makeAccountRecord(1, BigDecimal.ZERO);
            ArrayAccountRecord arr = processor.toArrayRecord(input);
            assertEquals(BigDecimal.ZERO, arr.balanceEntries()[3].currBal());
            assertEquals(BigDecimal.ZERO, arr.balanceEntries()[3].currCycDebit());
            assertEquals(BigDecimal.ZERO, arr.balanceEntries()[4].currBal());
            assertEquals(BigDecimal.ZERO, arr.balanceEntries()[4].currCycDebit());
        }

        @Test
        @DisplayName("always has exactly 5 entries")
        void alwaysHasFiveEntries() {
            AccountRecord input = makeAccountRecord(1, BigDecimal.ZERO);
            ArrayAccountRecord arr = processor.toArrayRecord(input);
            assertEquals(5, arr.balanceEntries().length);
        }
    }

    @Nested
    @DisplayName("AccountProcessor — VBR Records")
    class VbrRecordTests {

        @Test
        @DisplayName("Type1 contains acct ID and status")
        void type1HasIdAndStatus() {
            AccountRecord input = makeAccountRecord(7, BigDecimal.ZERO);
            VbrRecord.Type1 vbr1 = processor.toVbrType1(input);
            assertEquals(7, vbr1.acctId());
            assertEquals("Y", vbr1.activeStatus());
        }

        @Test
        @DisplayName("Type2 contains acct ID, balance, limit, and reissue year")
        void type2HasFinancialFields() {
            AccountRecord input = makeAccountRecord(7, BigDecimal.ZERO);
            VbrRecord.Type2 vbr2 = processor.toVbrType2(input);
            assertEquals(7, vbr2.acctId());
            assertEquals(new BigDecimal("194.00"), vbr2.currBal());
            assertEquals(new BigDecimal("2020.00"), vbr2.creditLimit());
            assertEquals("2025", vbr2.reissueYyyy());
        }
    }

    // ========================================================================
    // Integration Test — Full Pipeline with Sample Data
    // ========================================================================
    @Nested
    @DisplayName("Integration — Full Pipeline")
    class IntegrationTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("processes sample COBOL account data file and produces correct outputs")
        void processesActualDataFile() throws IOException {
            Path sampleInput = createSampleDataFile();
            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            int count = Cbact01cApplication.process(sampleInput, outFile, arryFile, vbrcFile);
            assertEquals(3, count);

            List<String> outLines = Files.readAllLines(outFile);
            List<String> arryLines = Files.readAllLines(arryFile);
            List<String> vbrcLines = Files.readAllLines(vbrcFile);

            assertEquals(3, outLines.size());
            assertEquals(3, arryLines.size());
            assertEquals(6, vbrcLines.size()); // 2 VBR records per account

            // --- Verify first account (ID=1) ---
            // OUTFILE: acctId|status|bal|limit|cashLimit|openDt|expDt|reissueDt|cycCredit|cycDebit|groupId
            String out1 = outLines.get(0);
            assertTrue(out1.startsWith("00000000001|Y|"), "Record 1 should start with acct ID and status");
            assertTrue(out1.contains("|194.00|"), "Balance should be 194.00");
            assertTrue(out1.contains("|2020.00|"), "Credit limit should be 2020.00");
            assertTrue(out1.contains("|1020.00|"), "Cash credit limit should be 1020.00");
            assertTrue(out1.contains("|20250520  |"), "Reissue date should be YYYYMMDD format");
            // cycDebit was 0, so should be substituted with 2525.00
            assertTrue(out1.contains("|2525.00|"), "Zero cycDebit should become 2525.00");

            // ARRYFILE: acctId|bal1|debit1|bal2|debit2|bal3|debit3|bal4|debit4|bal5|debit5
            String arr1 = arryLines.get(0);
            assertTrue(arr1.startsWith("00000000001|"), "Array record 1 starts with acct ID");
            assertTrue(arr1.contains("|194.00|1005.00|"), "Slot 1: actual bal + 1005.00");
            assertTrue(arr1.contains("|194.00|1525.00|"), "Slot 2: actual bal + 1525.00");
            assertTrue(arr1.contains("|-1025.00|-2500.00|"), "Slot 3: fixed negatives");
            assertTrue(arr1.endsWith("|0.00|0.00|0.00|0.00"), "Slots 4-5: zeroes");

            // VBRCFILE: alternating T1 and T2 records
            assertTrue(vbrcLines.get(0).equals("T1|00000000001|Y"), "VBR Type1 for acct 1");
            assertTrue(vbrcLines.get(1).startsWith("T2|00000000001|"), "VBR Type2 for acct 1");
            assertTrue(vbrcLines.get(1).contains("|194.00|2020.00|2025"), "VBR Type2 financials");
        }

        @Test
        @DisplayName("reads all 50 accounts from actual data file if available")
        void readsActualCobolDataFile() throws IOException {
            Path actualData = Path.of("../app/data/ASCII/acctdata.txt");
            if (!Files.exists(actualData)) {
                actualData = Path.of(System.getProperty("user.dir"))
                        .resolve("../app/data/ASCII/acctdata.txt");
            }
            if (!Files.exists(actualData)) {
                return; // skip if actual data not available
            }

            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            int count = Cbact01cApplication.process(actualData, outFile, arryFile, vbrcFile);
            assertEquals(50, count, "Should process all 50 account records");

            List<String> outLines = Files.readAllLines(outFile);
            List<String> vbrcLines = Files.readAllLines(vbrcFile);
            assertEquals(50, outLines.size());
            assertEquals(100, vbrcLines.size()); // 2 VBR records per account

            // Verify account IDs are sequential 1-50
            for (int i = 0; i < 50; i++) {
                String expectedId = "%011d".formatted(i + 1);
                assertTrue(outLines.get(i).startsWith(expectedId + "|"),
                        "Output line %d should start with acct ID %s".formatted(i, expectedId));
            }

            // All accounts in the sample data have cycDebit=0, so all should get 2525.00
            for (String line : outLines) {
                assertTrue(line.contains("|2525.00|"),
                        "All sample accounts have zero cycDebit, should be substituted with 2525.00");
            }
        }

        @Test
        @DisplayName("produces correct output for account with non-zero cycle debit")
        void handlesNonZeroCycDebit() throws IOException {
            Path input = createRecordWithNonZeroDebit();
            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            Cbact01cApplication.process(input, outFile, arryFile, vbrcFile);

            List<String> outLines = Files.readAllLines(outFile);
            assertEquals(1, outLines.size());
            // cycDebit = 350.75, should NOT be substituted
            assertTrue(outLines.get(0).contains("|350.75|"),
                    "Non-zero cycDebit 350.75 should be preserved");
            assertFalse(outLines.get(0).contains("|2525.00|"),
                    "2525.00 substitution should NOT happen for non-zero debit");
        }

        @Test
        @DisplayName("handles negative balance values correctly")
        void handlesNegativeValues() throws IOException {
            Path input = createRecordWithNegativeBalance();
            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            Cbact01cApplication.process(input, outFile, arryFile, vbrcFile);

            List<String> outLines = Files.readAllLines(outFile);
            assertEquals(1, outLines.size());
            assertTrue(outLines.get(0).contains("|-500.00|"),
                    "Negative balance should be preserved");
        }

        @Test
        @DisplayName("handles empty input file gracefully")
        void handlesEmptyFile() throws IOException {
            Path input = tempDir.resolve("empty.dat");
            Files.createFile(input);
            Path outFile = tempDir.resolve("outfile.dat");
            Path arryFile = tempDir.resolve("arryfile.dat");
            Path vbrcFile = tempDir.resolve("vbrcfile.dat");

            int count = Cbact01cApplication.process(input, outFile, arryFile, vbrcFile);
            assertEquals(0, count);
            assertEquals(0, Files.readAllLines(outFile).size());
        }

        // --- Helper: create sample data in COBOL fixed-width format ---

        private Path createSampleDataFile() throws IOException {
            Path file = tempDir.resolve("acctdata.txt");
            // Three sample records matching CVACT01Y 300-byte layout
            String rec1 = buildRecord("00000000001", "Y",
                    "00000001940{", "00000020200{", "00000010200{",
                    "2014-11-20", "2025-05-20", "2025-05-20",
                    "00000000000{", "00000000000{",
                    "          ", "A000000000");

            String rec2 = buildRecord("00000000002", "Y",
                    "00000001580{", "00000061300{", "00000054480{",
                    "2013-06-19", "2024-08-11", "2024-08-11",
                    "00000000000{", "00000000000{",
                    "          ", "A000000000");

            String rec3 = buildRecord("00000000003", "Y",
                    "00000001470{", "00000049090{", "00000005380{",
                    "2013-08-23", "2024-01-10", "2024-01-10",
                    "00000000000{", "00000000000{",
                    "          ", "A000000000");

            Files.writeString(file, rec1 + "\n" + rec2 + "\n" + rec3 + "\n");
            return file;
        }

        private Path createRecordWithNonZeroDebit() throws IOException {
            Path file = tempDir.resolve("acctdata_debit.txt");
            // cycDebit = 350.75 → overpunch: 00000003507E (E = +5)
            String rec = buildRecord("00000000099", "Y",
                    "00000001000{", "00000050000{", "00000025000{",
                    "2020-01-01", "2025-12-31", "2025-06-15",
                    "00000000000{", "00000003507E",
                    "12345     ", "B000000001");
            Files.writeString(file, rec + "\n");
            return file;
        }

        private Path createRecordWithNegativeBalance() throws IOException {
            Path file = tempDir.resolve("acctdata_neg.txt");
            // currBal = -500.00 → overpunch: 00000005000} (} = -0)
            String rec = buildRecord("00000000088", "Y",
                    "00000005000}", "00000020000{", "00000010000{",
                    "2019-03-15", "2024-06-30", "2024-03-01",
                    "00000000100{", "00000000000{",
                    "54321     ", "C000000002");
            Files.writeString(file, rec + "\n");
            return file;
        }

        private String buildRecord(String acctId, String status,
                                   String bal, String limit, String cashLimit,
                                   String openDate, String expDate, String reissueDate,
                                   String cycCredit, String cycDebit,
                                   String zip, String groupId) {
            StringBuilder sb = new StringBuilder(300);
            sb.append(acctId);           // 11
            sb.append(status);           // 1
            sb.append(bal);              // 12
            sb.append(limit);            // 12
            sb.append(cashLimit);        // 12
            sb.append(openDate);         // 10
            sb.append(expDate);          // 10
            sb.append(reissueDate);      // 10
            sb.append(cycCredit);        // 12
            sb.append(cycDebit);         // 12
            sb.append(padRight(zip, 10));      // 10
            sb.append(padRight(groupId, 10));  // 10
            // Filler to reach 300 bytes
            int remaining = 300 - sb.length();
            sb.append(" ".repeat(Math.max(0, remaining)));
            return sb.toString();
        }

        private String padRight(String s, int width) {
            if (s == null) return " ".repeat(width);
            if (s.length() >= width) return s.substring(0, width);
            return s + " ".repeat(width - s.length());
        }
    }

    // ========================================================================
    // AccountFileReader — Parsing Tests
    // ========================================================================
    @Nested
    @DisplayName("AccountFileReader — Record Parsing")
    class ReaderTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("parses first sample record correctly")
        void parsesFirstSampleRecord() throws IOException {
            Path file = tempDir.resolve("test.dat");
            String rec = buildCobolRecord("00000000001", "Y",
                    "00000001940{", "00000020200{", "00000010200{",
                    "2014-11-20", "2025-05-20", "2025-05-20",
                    "00000000000{", "00000000000{",
                    "          ", "A000000000");
            Files.writeString(file, rec + "\n");

            try (AccountFileReader reader = new AccountFileReader(file)) {
                AccountRecord acct = reader.readNext();
                assertNotNull(acct);
                assertEquals(1, acct.acctId());
                assertEquals("Y", acct.activeStatus());
                assertEquals(new BigDecimal("194.00"), acct.currBal());
                assertEquals(new BigDecimal("2020.00"), acct.creditLimit());
                assertEquals(new BigDecimal("1020.00"), acct.cashCreditLimit());
                assertEquals("2014-11-20", acct.openDate());
                assertEquals("2025-05-20", acct.expirationDate());
                assertEquals("2025-05-20", acct.reissueDate());
                assertEquals(new BigDecimal("0.00"), acct.currCycCredit());
                assertEquals(new BigDecimal("0.00"), acct.currCycDebit());
                assertEquals("A000000000", acct.groupId());

                assertNull(reader.readNext(), "Should return null at EOF");
            }
        }

        @Test
        @DisplayName("reads multiple records sequentially")
        void readsMultipleRecords() throws IOException {
            Path file = tempDir.resolve("multi.dat");
            String rec1 = buildCobolRecord("00000000001", "Y",
                    "00000001940{", "00000020200{", "00000010200{",
                    "2014-11-20", "2025-05-20", "2025-05-20",
                    "00000000000{", "00000000000{", "          ", "A000000000");
            String rec2 = buildCobolRecord("00000000002", "Y",
                    "00000001580{", "00000061300{", "00000054480{",
                    "2013-06-19", "2024-08-11", "2024-08-11",
                    "00000000000{", "00000000000{", "          ", "A000000000");
            Files.writeString(file, rec1 + "\n" + rec2 + "\n");

            try (AccountFileReader reader = new AccountFileReader(file)) {
                List<AccountRecord> records = reader.readAll();
                assertEquals(2, records.size());
                assertEquals(1, records.get(0).acctId());
                assertEquals(2, records.get(1).acctId());
                assertEquals(new BigDecimal("158.00"), records.get(1).currBal());
            }
        }

        private String buildCobolRecord(String acctId, String status,
                                        String bal, String limit, String cashLimit,
                                        String openDate, String expDate, String reissueDate,
                                        String cycCredit, String cycDebit,
                                        String zip, String groupId) {
            StringBuilder sb = new StringBuilder(300);
            sb.append(acctId);
            sb.append(status);
            sb.append(bal);
            sb.append(limit);
            sb.append(cashLimit);
            sb.append(openDate);
            sb.append(expDate);
            sb.append(reissueDate);
            sb.append(cycCredit);
            sb.append(cycDebit);
            sb.append(padRight(zip, 10));
            sb.append(padRight(groupId, 10));
            int remaining = 300 - sb.length();
            sb.append(" ".repeat(Math.max(0, remaining)));
            return sb.toString();
        }

        private String padRight(String s, int width) {
            if (s == null) return " ".repeat(width);
            if (s.length() >= width) return s.substring(0, width);
            return s + " ".repeat(width - s.length());
        }
    }

    // ========================================================================
    // Helpers
    // ========================================================================

    private AccountRecord makeAccountRecord(long acctId, BigDecimal cycDebit) {
        return new AccountRecord(
                acctId,
                "Y",
                new BigDecimal("194.00"),
                new BigDecimal("2020.00"),
                new BigDecimal("1020.00"),
                "2014-11-20",
                "2025-05-20",
                "2025-05-20",
                new BigDecimal("0.00"),
                cycDebit,
                "",
                "A000000000"
        );
    }
}
