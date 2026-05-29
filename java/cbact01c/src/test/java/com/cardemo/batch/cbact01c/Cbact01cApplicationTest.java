package com.cardemo.batch.cbact01c;

import com.cardemo.batch.cbact01c.Cbact01cApplication.ProcessingResult;
import com.cardemo.batch.cbact01c.io.AccountFileReader;
import com.cardemo.batch.cbact01c.io.CobolFieldParser;
import com.cardemo.batch.cbact01c.io.OutputFileWriter;
import com.cardemo.batch.cbact01c.model.*;
import com.cardemo.batch.cbact01c.service.AccountProcessor;
import com.cardemo.batch.cbact01c.service.DateFormatter;

import org.junit.jupiter.api.*;
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
 * JUnit tests verifying the Java rewrite of CBACT01C produces identical
 * results to the COBOL version for a set of sample inputs.
 */
class Cbact01cApplicationTest {

    private static final Path TEST_DATA = Path.of("src/test/resources/test-acctdata.txt");

    // ---------------------------------------------------------------
    // CobolFieldParser tests
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("CobolFieldParser — zoned decimal parsing")
    class CobolFieldParserTests {

        @ParameterizedTest(name = "parseSignedDecimal(\"{0}\", {1}) = {2}")
        @CsvSource({
                "00000001940{, 2, 194.00",
                "00000020200{, 2, 2020.00",
                "00000010200{, 2, 1020.00",
                "00000000000{, 2, 0",
                "00000005250N, 2, -525.05",
                "00000000350{, 2, 35.00",
                "00000001000{, 2, 100.00",
                "00000000500{, 2, 50.00",
        })
        void parseSignedDecimalValues(String raw, int decimals, BigDecimal expected) {
            assertEquals(0, expected.compareTo(
                    CobolFieldParser.parseSignedDecimal(raw, decimals)));
        }

        @Test
        @DisplayName("Positive overpunch characters A-I map to +1 through +9")
        void positiveOverpunchA_through_I() {
            // "00000000001A" → last digit 1 positive → 000000000011 → V99 → 0.11
            assertEquals(0, new BigDecimal("0.11").compareTo(
                    CobolFieldParser.parseSignedDecimal("00000000001A", 2)));
            // "00000000001I" → last digit 9 positive → 000000000019 → V99 → 0.19
            assertEquals(0, new BigDecimal("0.19").compareTo(
                    CobolFieldParser.parseSignedDecimal("00000000001I", 2)));
        }

        @Test
        @DisplayName("Negative overpunch characters J-R map to -1 through -9")
        void negativeOverpunchJ_through_R() {
            assertEquals(0, new BigDecimal("-0.11").compareTo(
                    CobolFieldParser.parseSignedDecimal("00000000001J", 2)));
            assertEquals(0, new BigDecimal("-0.19").compareTo(
                    CobolFieldParser.parseSignedDecimal("00000000001R", 2)));
        }

        @Test
        @DisplayName("Negative zero (}) parses as 0")
        void negativeZero() {
            BigDecimal result = CobolFieldParser.parseSignedDecimal("00000000000}", 2);
            assertEquals(0, BigDecimal.ZERO.compareTo(result.abs()));
        }

        @Test
        @DisplayName("formatSignedDecimal round-trips correctly")
        void formatRoundTrip() {
            String formatted = CobolFieldParser.formatSignedDecimal(
                    new BigDecimal("194.00"), 12, 2);
            assertEquals("00000001940{", formatted);

            formatted = CobolFieldParser.formatSignedDecimal(
                    new BigDecimal("-525.05"), 12, 2);
            assertEquals("00000005250N", formatted);
        }
    }

    // ---------------------------------------------------------------
    // DateFormatter tests — equivalent of COBDATFT
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("DateFormatter — COBDATFT equivalent")
    class DateFormatterTests {

        @Test
        @DisplayName("Type 2→2: YYYY-MM-DD → YYYYMMDD")
        void yyyyMmDd_to_yyyymmdd() {
            assertEquals("20250520", DateFormatter.formatDate("2025-05-20", "2", "2"));
            assertEquals("20241201", DateFormatter.formatDate("2024-12-01", "2", "2"));
            assertEquals("20200101", DateFormatter.formatDate("2020-01-01", "2", "2"));
        }

        @Test
        @DisplayName("Type 1→1: YYYYMMDD → YYYY-MM-DD")
        void yyyymmdd_to_yyyyMmDd() {
            assertEquals("2025-05-20", DateFormatter.formatDate("20250520", "1", "1"));
        }

        @Test
        @DisplayName("Type 2→1: YYYY-MM-DD → YYYY-MM-DD (identity)")
        void yyyyMmDd_to_yyyyMmDd() {
            assertEquals("2025-05-20", DateFormatter.formatDate("2025-05-20", "2", "1"));
        }

        @Test
        @DisplayName("extractYear returns 4-digit year")
        void extractYear() {
            assertEquals("2025", DateFormatter.extractYear("2025-05-20"));
            assertEquals("2024", DateFormatter.extractYear("2024-12-01"));
        }
    }

    // ---------------------------------------------------------------
    // AccountFileReader tests — input parsing
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("AccountFileReader — ACCTFILE parsing")
    class AccountFileReaderTests {

        @Test
        @DisplayName("Reads all 5 test records")
        void readsAllRecords() throws IOException {
            try (AccountFileReader reader = new AccountFileReader(TEST_DATA)) {
                List<AccountRecord> records = reader.readAll();
                assertEquals(5, records.size());
            }
        }

        @Test
        @DisplayName("Record 1 fields match expected COBOL values")
        void record1Fields() throws IOException {
            try (AccountFileReader reader = new AccountFileReader(TEST_DATA)) {
                AccountRecord r = reader.readNext();
                assertEquals("00000000001", r.acctId());
                assertEquals("Y", r.activeStatus());
                assertEquals(0, new BigDecimal("194.00").compareTo(r.currBal()));
                assertEquals(0, new BigDecimal("2020.00").compareTo(r.creditLimit()));
                assertEquals(0, new BigDecimal("1020.00").compareTo(r.cashCreditLimit()));
                assertEquals("2014-11-20", r.openDate());
                assertEquals("2025-05-20", r.expirationDate());
                assertEquals("2025-05-20", r.reissueDate());
                assertEquals(0, BigDecimal.ZERO.compareTo(r.currCycCredit()));
                assertEquals(0, BigDecimal.ZERO.compareTo(r.currCycDebit()));
            }
        }

        @Test
        @DisplayName("Record 4 (negative balance) parses correctly")
        void record4NegativeBalance() throws IOException {
            try (AccountFileReader reader = new AccountFileReader(TEST_DATA)) {
                // Skip first 3
                reader.readNext();
                reader.readNext();
                reader.readNext();
                AccountRecord r = reader.readNext();
                assertEquals("00000000050", r.acctId());
                assertEquals(0, new BigDecimal("-525.05").compareTo(r.currBal()));
                assertEquals(0, new BigDecimal("7500.00").compareTo(r.creditLimit()));
                assertEquals(0, new BigDecimal("120.00").compareTo(r.currCycCredit()));
                assertEquals(0, new BigDecimal("35.00").compareTo(r.currCycDebit()));
            }
        }
    }

    // ---------------------------------------------------------------
    // AccountProcessor tests — core business logic
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("AccountProcessor — business logic (1300/1400/1500 paragraphs)")
    class AccountProcessorTests {

        private AccountRecord sampleAccount(BigDecimal cycDebit) {
            return new AccountRecord(
                    "00000000001", "Y",
                    new BigDecimal("194.00"), new BigDecimal("2020.00"),
                    new BigDecimal("1020.00"),
                    "2014-11-20", "2025-05-20", "2025-05-20",
                    BigDecimal.ZERO, cycDebit,
                    "A000000000", "          "
            );
        }

        @Test
        @DisplayName("1300: Reissue date reformatted YYYY-MM-DD → YYYYMMDD")
        void outputRecordDateReformatted() {
            OutputAccountRecord out = AccountProcessor.buildOutputRecord(
                    sampleAccount(BigDecimal.ZERO));
            assertEquals("20250520", out.reissueDate());
        }

        @Test
        @DisplayName("1300: Zero debit defaults to 2525.00")
        void outputRecordDebitDefaultedWhenZero() {
            OutputAccountRecord out = AccountProcessor.buildOutputRecord(
                    sampleAccount(BigDecimal.ZERO));
            assertEquals(0, new BigDecimal("2525.00").compareTo(out.currCycDebit()));
        }

        @Test
        @DisplayName("1300: Non-zero debit is preserved")
        void outputRecordDebitPreservedWhenNonZero() {
            OutputAccountRecord out = AccountProcessor.buildOutputRecord(
                    sampleAccount(new BigDecimal("35.00")));
            assertEquals(0, new BigDecimal("35.00").compareTo(out.currCycDebit()));
        }

        @Test
        @DisplayName("1300: All other fields pass through unchanged")
        void outputRecordFieldsPassThrough() {
            OutputAccountRecord out = AccountProcessor.buildOutputRecord(
                    sampleAccount(BigDecimal.ZERO));
            assertEquals("00000000001", out.acctId());
            assertEquals("Y", out.activeStatus());
            assertEquals(0, new BigDecimal("194.00").compareTo(out.currBal()));
            assertEquals(0, new BigDecimal("2020.00").compareTo(out.creditLimit()));
            assertEquals(0, new BigDecimal("1020.00").compareTo(out.cashCreditLimit()));
            assertEquals("2014-11-20", out.openDate());
            assertEquals("2025-05-20", out.expirationDate());
            assertEquals(0, BigDecimal.ZERO.compareTo(out.currCycCredit()));
            assertEquals("          ", out.groupId());
        }

        @Test
        @DisplayName("1400: Array record has 5 entries with correct hardcoded values")
        void arrayRecordPopulation() {
            AccountRecord input = sampleAccount(BigDecimal.ZERO);
            ArrayAccountRecord arr = AccountProcessor.buildArrayRecord(input);

            assertEquals("00000000001", arr.acctId());
            List<BalanceEntry> entries = arr.balanceEntries();
            assertEquals(5, entries.size());

            // Entry 1: actual balance, 1005.00
            assertEquals(0, new BigDecimal("194.00").compareTo(entries.get(0).currBal()));
            assertEquals(0, new BigDecimal("1005.00").compareTo(entries.get(0).currCycDebit()));

            // Entry 2: actual balance, 1525.00
            assertEquals(0, new BigDecimal("194.00").compareTo(entries.get(1).currBal()));
            assertEquals(0, new BigDecimal("1525.00").compareTo(entries.get(1).currCycDebit()));

            // Entry 3: -1025.00, -2500.00
            assertEquals(0, new BigDecimal("-1025.00").compareTo(entries.get(2).currBal()));
            assertEquals(0, new BigDecimal("-2500.00").compareTo(entries.get(2).currCycDebit()));

            // Entries 4-5: zeros (INITIALIZE)
            assertEquals(0, BigDecimal.ZERO.compareTo(entries.get(3).currBal()));
            assertEquals(0, BigDecimal.ZERO.compareTo(entries.get(3).currCycDebit()));
            assertEquals(0, BigDecimal.ZERO.compareTo(entries.get(4).currBal()));
            assertEquals(0, BigDecimal.ZERO.compareTo(entries.get(4).currCycDebit()));
        }

        @Test
        @DisplayName("1500: VBR short record contains ID and status")
        void vbrShortRecord() {
            AccountRecord input = sampleAccount(BigDecimal.ZERO);
            VariableRecordShort s = AccountProcessor.buildVbrShortRecord(input);
            assertEquals("00000000001", s.acctId());
            assertEquals("Y", s.activeStatus());
        }

        @Test
        @DisplayName("1500: VBR long record contains ID, balance, limit, reissue year")
        void vbrLongRecord() {
            AccountRecord input = sampleAccount(BigDecimal.ZERO);
            VariableRecordLong l = AccountProcessor.buildVbrLongRecord(input);
            assertEquals("00000000001", l.acctId());
            assertEquals(0, new BigDecimal("194.00").compareTo(l.currBal()));
            assertEquals(0, new BigDecimal("2020.00").compareTo(l.creditLimit()));
            assertEquals("2025", l.reissueYear());
        }
    }

    // ---------------------------------------------------------------
    // End-to-end integration test
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("End-to-end integration — full pipeline")
    class IntegrationTests {

        @TempDir
        Path tempDir;

        @Test
        @DisplayName("Processes all 5 test records and produces correct output files")
        void fullPipelineTest() throws IOException {
            ProcessingResult result = Cbact01cApplication.processAccountFile(TEST_DATA);

            assertEquals(5, result.outputRecords().size());
            assertEquals(5, result.arrayRecords().size());
            assertEquals(5, result.vbrShortRecords().size());
            assertEquals(5, result.vbrLongRecords().size());

            // --- Verify output records ---

            // Record 1: zero debit → defaults to 2525.00
            OutputAccountRecord out1 = result.outputRecords().get(0);
            assertEquals("00000000001", out1.acctId());
            assertEquals("20250520", out1.reissueDate());
            assertEquals(0, new BigDecimal("2525.00").compareTo(out1.currCycDebit()));

            // Record 2: zero debit → defaults to 2525.00
            OutputAccountRecord out2 = result.outputRecords().get(1);
            assertEquals("00000000002", out2.acctId());
            assertEquals("20240811", out2.reissueDate());
            assertEquals(0, new BigDecimal("2525.00").compareTo(out2.currCycDebit()));

            // Record 3: zero debit → defaults to 2525.00
            OutputAccountRecord out3 = result.outputRecords().get(2);
            assertEquals("00000000003", out3.acctId());
            assertEquals("20240110", out3.reissueDate());
            assertEquals(0, new BigDecimal("2525.00").compareTo(out3.currCycDebit()));

            // Record 4: non-zero debit (35.00) → preserved
            OutputAccountRecord out4 = result.outputRecords().get(3);
            assertEquals("00000000050", out4.acctId());
            assertEquals(0, new BigDecimal("-525.05").compareTo(out4.currBal()));
            assertEquals("20241201", out4.reissueDate());
            assertEquals(0, new BigDecimal("35.00").compareTo(out4.currCycDebit()));
            assertEquals(0, new BigDecimal("120.00").compareTo(out4.currCycCredit()));

            // Record 5: zero balance + zero debit → debit defaults to 2525.00
            OutputAccountRecord out5 = result.outputRecords().get(4);
            assertEquals("00000000099", out5.acctId());
            assertEquals(0, BigDecimal.ZERO.compareTo(out5.currBal()));
            assertEquals("20250101", out5.reissueDate());
            assertEquals(0, new BigDecimal("2525.00").compareTo(out5.currCycDebit()));

            // --- Verify array records ---
            ArrayAccountRecord arr4 = result.arrayRecords().get(3);
            assertEquals("00000000050", arr4.acctId());
            // Entry 1: actual balance (-525.05), 1005.00
            assertEquals(0, new BigDecimal("-525.05").compareTo(
                    arr4.balanceEntries().get(0).currBal()));
            assertEquals(0, new BigDecimal("1005.00").compareTo(
                    arr4.balanceEntries().get(0).currCycDebit()));

            // --- Verify VBR records ---
            VariableRecordShort vbrS = result.vbrShortRecords().get(0);
            assertEquals("00000000001", vbrS.acctId());
            assertEquals("Y", vbrS.activeStatus());

            VariableRecordLong vbrL = result.vbrLongRecords().get(3);
            assertEquals("00000000050", vbrL.acctId());
            assertEquals(0, new BigDecimal("-525.05").compareTo(vbrL.currBal()));
            assertEquals(0, new BigDecimal("7500.00").compareTo(vbrL.creditLimit()));
            assertEquals("2024", vbrL.reissueYear());
        }

        @Test
        @DisplayName("Output files are written with correct structure")
        void outputFilesWrittenCorrectly() throws IOException {
            ProcessingResult result = Cbact01cApplication.processAccountFile(TEST_DATA);

            Path outFile  = tempDir.resolve("outfile.csv");
            Path arryFile = tempDir.resolve("arryfile.csv");
            Path vbrcFile = tempDir.resolve("vbrcfile.csv");

            OutputFileWriter.writeOutputFile(outFile, result.outputRecords());
            OutputFileWriter.writeArrayFile(arryFile, result.arrayRecords());
            OutputFileWriter.writeVariableLengthFile(vbrcFile,
                    result.vbrShortRecords(), result.vbrLongRecords());

            // OUTFILE: header + 5 data rows
            List<String> outLines = Files.readAllLines(outFile);
            assertEquals(6, outLines.size());
            assertTrue(outLines.get(0).startsWith("ACCT_ID|"));

            // First data row
            String[] fields = outLines.get(1).split("\\|");
            assertEquals("00000000001", fields[0]);
            assertEquals("Y", fields[1]);
            assertEquals("194.00", fields[2]);
            assertEquals("20250520", fields[7]);
            assertEquals("2525.00", fields[9]);

            // Record 4 data row (non-zero debit preserved)
            String[] fields4 = outLines.get(4).split("\\|");
            assertEquals("00000000050", fields4[0]);
            assertEquals("-525.05", fields4[2]);
            assertEquals("35.00", fields4[9]);

            // ARRYFILE: header + 5 data rows
            List<String> arryLines = Files.readAllLines(arryFile);
            assertEquals(6, arryLines.size());

            // VBRCFILE: header + 10 data rows (2 per account)
            List<String> vbrcLines = Files.readAllLines(vbrcFile);
            assertEquals(11, vbrcLines.size());
            assertTrue(vbrcLines.get(1).startsWith("S|"));
            assertTrue(vbrcLines.get(2).startsWith("L|"));
        }

        @Test
        @DisplayName("CLI main method writes all output files")
        void mainMethodEndToEnd() throws IOException {
            Path outFile  = tempDir.resolve("out.csv");
            Path arryFile = tempDir.resolve("arry.csv");
            Path vbrcFile = tempDir.resolve("vbrc.csv");

            Cbact01cApplication.main(new String[]{
                    TEST_DATA.toString(),
                    outFile.toString(),
                    arryFile.toString(),
                    vbrcFile.toString()
            });

            assertTrue(Files.exists(outFile));
            assertTrue(Files.exists(arryFile));
            assertTrue(Files.exists(vbrcFile));
            assertTrue(Files.size(outFile) > 0);
            assertTrue(Files.size(arryFile) > 0);
            assertTrue(Files.size(vbrcFile) > 0);
        }
    }

    // ---------------------------------------------------------------
    // Consistency check: process real sample data
    // ---------------------------------------------------------------

    @Nested
    @DisplayName("Real data consistency — all 50 records from acctdata.txt")
    class RealDataTests {

        private static final Path REAL_DATA = Path.of(
                "../../app/data/ASCII/acctdata.txt");

        @Test
        @DisplayName("All 50 accounts process without errors")
        void processAll50Accounts() throws IOException {
            if (!Files.exists(REAL_DATA)) {
                // Skip if real data not available (CI without full repo)
                return;
            }
            ProcessingResult result = Cbact01cApplication.processAccountFile(REAL_DATA);
            assertEquals(50, result.outputRecords().size());
            assertEquals(50, result.arrayRecords().size());
            assertEquals(50, result.vbrShortRecords().size());
            assertEquals(50, result.vbrLongRecords().size());

            // Every output record should have YYYYMMDD formatted reissue date
            for (OutputAccountRecord out : result.outputRecords()) {
                assertEquals(8, out.reissueDate().length(),
                        "Reissue date should be YYYYMMDD format for acct " + out.acctId());
            }

            // Every output record with zero input debit should have 2525.00
            try (AccountFileReader reader = new AccountFileReader(REAL_DATA)) {
                List<AccountRecord> inputs = reader.readAll();
                for (int i = 0; i < inputs.size(); i++) {
                    if (inputs.get(i).currCycDebit().compareTo(BigDecimal.ZERO) == 0) {
                        assertEquals(0,
                                new BigDecimal("2525.00").compareTo(
                                        result.outputRecords().get(i).currCycDebit()),
                                "Debit should default to 2525.00 for acct "
                                        + inputs.get(i).acctId());
                    }
                }
            }
        }
    }
}
