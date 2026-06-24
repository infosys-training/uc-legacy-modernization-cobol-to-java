package com.carddemo.batch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end tests verifying the Java CBACT01C rewrite produces identical
 * logical results to the COBOL version for the sample account data.
 */
class AccountFileProcessorTest {

    private AccountFileProcessor processor;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        processor = new AccountFileProcessor();
    }

    // --- Test data: first 5 records from app/data/ASCII/acctdata.txt ---

    private static final String[] SAMPLE_RECORDS = {
            "00000000001Y00000001940{00000020200{00000010200{2014-11-202025-05-202025-05-20"
                    + "00000000000{00000000000{A000000000" + " ".repeat(178),
            "00000000002Y00000001580{00000061300{00000054480{2013-06-192024-08-112024-08-11"
                    + "00000000000{00000000000{A000000000" + " ".repeat(178),
            "00000000003Y00000001470{00000049090{00000005380{2013-08-232024-01-102024-01-10"
                    + "00000000000{00000000000{A000000000" + " ".repeat(178),
            "00000000004Y00000000400{00000035030{00000027890{2012-11-172023-12-162023-12-16"
                    + "00000000000{00000000000{A000000000" + " ".repeat(178),
            "00000000005Y00000003450{00000038190{00000024300{2012-10-032025-03-092025-03-09"
                    + "00000000000{00000000000{A000000000" + " ".repeat(178),
    };

    private Path createInputFile(String... records) throws IOException {
        Path inputFile = tempDir.resolve("acctdata.txt");
        Files.write(inputFile, List.of(records));
        return inputFile;
    }

    private AccountFileProcessor.ProcessingResult runProcessor(String... records) throws IOException {
        Path inputFile = createInputFile(records);
        Path outFile = tempDir.resolve("outfile.txt");
        Path arryFile = tempDir.resolve("arryfile.txt");
        Path vbrcFile = tempDir.resolve("vbrcfile.txt");
        return processor.process(inputFile, outFile, arryFile, vbrcFile);
    }

    // --- Record count tests ---

    @Test
    void processesAllRecords() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS);
        assertEquals(5, result.recordCount());
        assertEquals(5, result.outRecords().size());
        assertEquals(5, result.arrayRecords().size());
        assertEquals(5, result.vbRecords().size());
    }

    @Test
    void emptyInputProducesNoOutput() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor();
        assertEquals(0, result.recordCount());
    }

    // --- OutRecord business rule tests ---

    @Test
    void outRecord_reissueDateConvertedToCompactFormat() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS[0]);
        AccountFileProcessor.OutAccountRecord out = result.outRecords().get(0);

        // COBOL: MOVE ACCT-REISSUE-DATE TO CODATECN-INP-DATE, type='2'→'2'
        // "2025-05-20" → "20250520"
        assertEquals("20250520", out.reissueDate());
    }

    @Test
    void outRecord_reissueDateConversion_allRecords() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS);

        assertEquals("20250520", result.outRecords().get(0).reissueDate());
        assertEquals("20240811", result.outRecords().get(1).reissueDate());
        assertEquals("20240110", result.outRecords().get(2).reissueDate());
        assertEquals("20231216", result.outRecords().get(3).reissueDate());
        assertEquals("20250309", result.outRecords().get(4).reissueDate());
    }

    @Test
    void outRecord_zeroCycDebitSubstitutedWith2525() throws IOException {
        // All sample records have ACCT-CURR-CYC-DEBIT = 0
        // COBOL: IF ACCT-CURR-CYC-DEBIT EQUAL TO ZERO MOVE 2525.00 TO OUT-ACCT-CURR-CYC-DEBIT
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS[0]);
        AccountFileProcessor.OutAccountRecord out = result.outRecords().get(0);

        assertEquals(new BigDecimal("2525.00"), out.currCycDebit());
    }

    @Test
    void outRecord_nonZeroCycDebitPreserved() throws IOException {
        // Create a record with non-zero debit (change bytes 91-102)
        String recordWithDebit =
                "00000000099Y00000001940{00000020200{00000010200{2014-11-202025-05-202025-05-20"
                        + "00000000000{00000005000{A000000000" + " ".repeat(178);
        // 00000005000{ = +500.00

        AccountFileProcessor.ProcessingResult result = runProcessor(recordWithDebit);
        AccountFileProcessor.OutAccountRecord out = result.outRecords().get(0);

        // Should keep original value, NOT substitute 2525.00
        assertEquals(new BigDecimal("500.00"), out.currCycDebit());
    }

    @Test
    void outRecord_preservesAllFields() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS[0]);
        AccountFileProcessor.OutAccountRecord out = result.outRecords().get(0);

        assertEquals("00000000001", out.acctId());
        assertEquals("Y", out.activeStatus());
        assertEquals(new BigDecimal("194.00"), out.currBal());
        assertEquals(new BigDecimal("2020.00"), out.creditLimit());
        assertEquals(new BigDecimal("1020.00"), out.cashCreditLimit());
        assertEquals("2014-11-20", out.openDate());
        assertEquals("2025-05-20", out.expirationDate());
        assertEquals(0, out.currCycCredit().compareTo(BigDecimal.ZERO));
    }

    // --- ArrayRecord business rule tests ---

    @Test
    void arrayRecord_slot1UsesActualBalanceAndFixedDebit() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS[0]);
        AccountFileProcessor.ArrayRecord arr = result.arrayRecords().get(0);

        assertEquals("00000000001", arr.acctId());
        // Slot 1: balance = actual (194.00), debit = 1005.00
        assertEquals(new BigDecimal("194.00"), arr.pairs().get(0).balance());
        assertEquals(new BigDecimal("1005.00"), arr.pairs().get(0).debit());
    }

    @Test
    void arrayRecord_slot2UsesActualBalanceAndFixedDebit() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS[0]);
        AccountFileProcessor.ArrayRecord arr = result.arrayRecords().get(0);

        // Slot 2: balance = actual (194.00), debit = 1525.00
        assertEquals(new BigDecimal("194.00"), arr.pairs().get(1).balance());
        assertEquals(new BigDecimal("1525.00"), arr.pairs().get(1).debit());
    }

    @Test
    void arrayRecord_slot3UsesFixedNegativeValues() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS[0]);
        AccountFileProcessor.ArrayRecord arr = result.arrayRecords().get(0);

        // Slot 3: balance = -1025.00, debit = -2500.00 (both fixed)
        assertEquals(new BigDecimal("-1025.00"), arr.pairs().get(2).balance());
        assertEquals(new BigDecimal("-2500.00"), arr.pairs().get(2).debit());
    }

    @Test
    void arrayRecord_slots4And5AreZero() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS[0]);
        AccountFileProcessor.ArrayRecord arr = result.arrayRecords().get(0);

        // Slots 4-5: zero (from INITIALIZE in COBOL)
        assertEquals(0, arr.pairs().get(3).balance().compareTo(BigDecimal.ZERO));
        assertEquals(0, arr.pairs().get(3).debit().compareTo(BigDecimal.ZERO));
        assertEquals(0, arr.pairs().get(4).balance().compareTo(BigDecimal.ZERO));
        assertEquals(0, arr.pairs().get(4).debit().compareTo(BigDecimal.ZERO));
    }

    @Test
    void arrayRecord_has5Pairs() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS[0]);
        assertEquals(5, result.arrayRecords().get(0).pairs().size());
    }

    // --- VbRecord tests ---

    @Test
    void vbRecord_containsCorrectFields() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS[0]);
        AccountFileProcessor.VbRecord vb = result.vbRecords().get(0);

        // VB1 fields
        assertEquals("00000000001", vb.acctId());
        assertEquals("Y", vb.activeStatus());
        // VB2 fields
        assertEquals(new BigDecimal("194.00"), vb.currBal());
        assertEquals(new BigDecimal("2020.00"), vb.creditLimit());
        assertEquals("2025", vb.reissueYear());
    }

    @Test
    void vbRecord_reissueYearExtraction() throws IOException {
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS);

        assertEquals("2025", result.vbRecords().get(0).reissueYear());
        assertEquals("2024", result.vbRecords().get(1).reissueYear());
        assertEquals("2024", result.vbRecords().get(2).reissueYear());
        assertEquals("2023", result.vbRecords().get(3).reissueYear());
        assertEquals("2025", result.vbRecords().get(4).reissueYear());
    }

    // --- Output file content tests ---

    @Test
    void outputFilesAreCreated() throws IOException {
        Path inputFile = createInputFile(SAMPLE_RECORDS);
        Path outFile = tempDir.resolve("outfile.txt");
        Path arryFile = tempDir.resolve("arryfile.txt");
        Path vbrcFile = tempDir.resolve("vbrcfile.txt");

        processor.process(inputFile, outFile, arryFile, vbrcFile);

        assertTrue(Files.exists(outFile));
        assertTrue(Files.exists(arryFile));
        assertTrue(Files.exists(vbrcFile));
    }

    @Test
    void outFileHasCorrectLineCount() throws IOException {
        Path inputFile = createInputFile(SAMPLE_RECORDS);
        Path outFile = tempDir.resolve("outfile.txt");
        Path arryFile = tempDir.resolve("arryfile.txt");
        Path vbrcFile = tempDir.resolve("vbrcfile.txt");

        processor.process(inputFile, outFile, arryFile, vbrcFile);

        assertEquals(5, Files.readAllLines(outFile).size());
        assertEquals(5, Files.readAllLines(arryFile).size());
        // VBR file has 2 lines per record (VB1 + VB2)
        assertEquals(10, Files.readAllLines(vbrcFile).size());
    }

    @Test
    void outFileFirstLineMatchesExpected() throws IOException {
        Path inputFile = createInputFile(SAMPLE_RECORDS[0]);
        Path outFile = tempDir.resolve("outfile.txt");
        Path arryFile = tempDir.resolve("arryfile.txt");
        Path vbrcFile = tempDir.resolve("vbrcfile.txt");

        processor.process(inputFile, outFile, arryFile, vbrcFile);

        String firstLine = Files.readAllLines(outFile).get(0);
        // Pipe-delimited: ID|status|bal|limit|cashlimit|open|exp|reissue|credit|debit|group
        assertEquals(
                "00000000001|Y|194.00|2020.00|1020.00|2014-11-20|2025-05-20|20250520|0.00|2525.00|",
                firstLine
        );
    }

    // --- Full sample data integration test ---

    @Test
    void processFullSampleData() throws IOException {
        // Use all 5 sample records and verify complete processing
        AccountFileProcessor.ProcessingResult result = runProcessor(SAMPLE_RECORDS);

        // Verify all balances parsed correctly
        BigDecimal[] expectedBalances = {
                new BigDecimal("194.00"),
                new BigDecimal("158.00"),
                new BigDecimal("147.00"),
                new BigDecimal("40.00"),
                new BigDecimal("345.00"),
        };

        for (int i = 0; i < 5; i++) {
            assertEquals(expectedBalances[i], result.outRecords().get(i).currBal(),
                    "Balance mismatch for record " + (i + 1));
        }

        // Verify credit limits
        BigDecimal[] expectedLimits = {
                new BigDecimal("2020.00"),
                new BigDecimal("6130.00"),
                new BigDecimal("4909.00"),
                new BigDecimal("3503.00"),
                new BigDecimal("3819.00"),
        };

        for (int i = 0; i < 5; i++) {
            assertEquals(expectedLimits[i], result.outRecords().get(i).creditLimit(),
                    "Credit limit mismatch for record " + (i + 1));
        }

        // All sample records have zero debit → all should be substituted to 2525.00
        for (int i = 0; i < 5; i++) {
            assertEquals(new BigDecimal("2525.00"), result.outRecords().get(i).currCycDebit(),
                    "Debit substitution failed for record " + (i + 1));
        }
    }

    // --- Test with actual sample data file ---

    @Test
    void processActualSampleDataFile() throws IOException {
        Path sampleDataFile = Path.of("../../app/data/ASCII/acctdata.txt");
        if (!Files.exists(sampleDataFile)) {
            // Skip if running outside the repo directory structure
            return;
        }

        Path outFile = tempDir.resolve("outfile.txt");
        Path arryFile = tempDir.resolve("arryfile.txt");
        Path vbrcFile = tempDir.resolve("vbrcfile.txt");

        AccountFileProcessor.ProcessingResult result = processor.process(
                sampleDataFile, outFile, arryFile, vbrcFile);

        // The sample file has 50 records
        assertEquals(50, result.recordCount(), "Expected 50 account records in sample file");
        assertEquals(50, result.outRecords().size());
        assertEquals(50, result.arrayRecords().size());
        assertEquals(50, result.vbRecords().size());

        // Verify first record
        assertEquals("00000000001", result.outRecords().get(0).acctId());
        assertEquals(new BigDecimal("194.00"), result.outRecords().get(0).currBal());
        assertEquals("20250520", result.outRecords().get(0).reissueDate());

        // Verify output files have correct line counts
        assertEquals(50, Files.readAllLines(outFile).size());
        assertEquals(50, Files.readAllLines(arryFile).size());
        assertEquals(100, Files.readAllLines(vbrcFile).size()); // 2 lines per record

        // Verify all reissue dates were converted to YYYYMMDD
        for (AccountFileProcessor.OutAccountRecord out : result.outRecords()) {
            assertFalse(out.reissueDate().contains("-"),
                    "Reissue date should be YYYYMMDD, not YYYY-MM-DD: " + out.reissueDate());
            assertEquals(8, out.reissueDate().trim().length(),
                    "Reissue date should be 8 chars: " + out.reissueDate());
        }

        // Verify all array records have 5 pairs
        for (AccountFileProcessor.ArrayRecord arr : result.arrayRecords()) {
            assertEquals(5, arr.pairs().size());
            // Slot 3 is always fixed
            assertEquals(new BigDecimal("-1025.00"), arr.pairs().get(2).balance());
            assertEquals(new BigDecimal("-2500.00"), arr.pairs().get(2).debit());
        }
    }
}
