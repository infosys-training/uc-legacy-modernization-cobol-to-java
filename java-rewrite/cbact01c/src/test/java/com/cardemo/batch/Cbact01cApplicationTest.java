package com.cardemo.batch;

import com.cardemo.batch.io.AccountReader;
import com.cardemo.batch.model.AccountRecord;
import com.cardemo.batch.model.ProcessingResult;
import com.cardemo.batch.processor.AccountProcessor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * End-to-end integration test that reads the sample data file,
 * processes all records, and verifies output consistency.
 */
class Cbact01cApplicationTest {

    private static final String SAMPLE_LINE_1 =
            "00000000001Y00000001940{00000020200{00000010200{"
          + "2014-11-202025-05-202025-05-20"
          + "00000000000{00000000000{A000000000";

    private static final String SAMPLE_LINE_2 =
            "00000000002Y00000001580{00000061300{00000054480{"
          + "2013-06-192024-08-112024-08-11"
          + "00000000000{00000000000{A000000000";

    private static final String SAMPLE_LINE_3 =
            "00000000003Y00000001470{00000049090{00000005380{"
          + "2013-08-232024-01-102024-01-10"
          + "00000000000{00000000000{A000000000";

    @Test
    void endToEnd_processesAllRecords(@TempDir Path tempDir) throws IOException {
        Path inputFile = tempDir.resolve("acctdata.txt");
        Files.writeString(inputFile,
                SAMPLE_LINE_1 + "\n" + SAMPLE_LINE_2 + "\n" + SAMPLE_LINE_3 + "\n");

        var processor = new AccountProcessor();

        try (var reader = new AccountReader(inputFile)) {
            List<AccountRecord> records = reader.readAll();
            assertEquals(3, records.size());

            for (AccountRecord acct : records) {
                ProcessingResult result = processor.process(acct);
                assertNotNull(result.outRecord());
                assertNotNull(result.arrayRecord());
                assertNotNull(result.vbrcRecord1());
                assertNotNull(result.vbrcRecord2());
            }
        }
    }

    @Test
    void endToEnd_firstRecordValues(@TempDir Path tempDir) throws IOException {
        Path inputFile = tempDir.resolve("acctdata.txt");
        Files.writeString(inputFile, SAMPLE_LINE_1 + "\n");

        var processor = new AccountProcessor();

        try (var reader = new AccountReader(inputFile)) {
            AccountRecord acct = reader.readNext();
            assertNotNull(acct);

            ProcessingResult result = processor.process(acct);

            // Verify outRecord matches COBOL 1300-POPUL-ACCT-RECORD
            assertEquals("00000000001", result.outRecord().acctId());
            assertEquals("Y", result.outRecord().activeStatus());
            assertEquals(0, new BigDecimal("194.00").compareTo(result.outRecord().currBal()));
            assertEquals(0, new BigDecimal("2020.00").compareTo(result.outRecord().creditLimit()));
            assertEquals(0, new BigDecimal("1020.00").compareTo(result.outRecord().cashCreditLimit()));
            assertEquals("2014-11-20", result.outRecord().openDate());
            assertEquals("2025-05-20", result.outRecord().expirationDate());
            // Reissue date: YYYY-MM-DD → YYYYMMDD, padded to 10
            assertEquals("20250520  ", result.outRecord().reissueDate());
            assertEquals(0, BigDecimal.ZERO.compareTo(result.outRecord().currCycCredit()));
            // Zero debit → default 2525.00
            assertEquals(0, new BigDecimal("2525.00").compareTo(result.outRecord().currCycDebit()));

            // Verify arrayRecord matches COBOL 1400-POPUL-ARRAY-RECORD
            assertEquals("00000000001", result.arrayRecord().acctId());
            assertEquals(0, new BigDecimal("194.00").compareTo(
                    result.arrayRecord().balanceEntries().get(0).currBal()));
            assertEquals(0, new BigDecimal("1005.00").compareTo(
                    result.arrayRecord().balanceEntries().get(0).currCycDebit()));
            assertEquals(0, new BigDecimal("194.00").compareTo(
                    result.arrayRecord().balanceEntries().get(1).currBal()));
            assertEquals(0, new BigDecimal("1525.00").compareTo(
                    result.arrayRecord().balanceEntries().get(1).currCycDebit()));
            assertEquals(0, new BigDecimal("-1025.00").compareTo(
                    result.arrayRecord().balanceEntries().get(2).currBal()));
            assertEquals(0, new BigDecimal("-2500.00").compareTo(
                    result.arrayRecord().balanceEntries().get(2).currCycDebit()));

            // Verify vbrcRecord1 matches COBOL 1500-POPUL-VBRC-RECORD
            assertEquals("00000000001", result.vbrcRecord1().acctId());
            assertEquals("Y", result.vbrcRecord1().activeStatus());

            // Verify vbrcRecord2
            assertEquals("00000000001", result.vbrcRecord2().acctId());
            assertEquals(0, new BigDecimal("194.00").compareTo(result.vbrcRecord2().currBal()));
            assertEquals(0, new BigDecimal("2020.00").compareTo(result.vbrcRecord2().creditLimit()));
            assertEquals("2025", result.vbrcRecord2().reissueYear());
        }
    }

    @Test
    void endToEnd_thirdRecordValues(@TempDir Path tempDir) throws IOException {
        Path inputFile = tempDir.resolve("acctdata.txt");
        Files.writeString(inputFile, SAMPLE_LINE_3 + "\n");

        var processor = new AccountProcessor();

        try (var reader = new AccountReader(inputFile)) {
            AccountRecord acct = reader.readNext();
            ProcessingResult result = processor.process(acct);

            assertEquals("00000000003", result.outRecord().acctId());
            assertEquals(0, new BigDecimal("147.00").compareTo(result.outRecord().currBal()));
            assertEquals(0, new BigDecimal("4909.00").compareTo(result.outRecord().creditLimit()));
            assertEquals(0, new BigDecimal("538.00").compareTo(result.outRecord().cashCreditLimit()));
            assertEquals("20240110  ", result.outRecord().reissueDate());
            assertEquals(0, new BigDecimal("2525.00").compareTo(result.outRecord().currCycDebit()));
            assertEquals("2024", result.vbrcRecord2().reissueYear());
        }
    }

    @Test
    void mainMethod_producesOutputFiles(@TempDir Path tempDir) throws IOException {
        Path inputFile = tempDir.resolve("acctdata.txt");
        Path outputDir = tempDir.resolve("output");
        Files.writeString(inputFile, SAMPLE_LINE_1 + "\n" + SAMPLE_LINE_2 + "\n");

        Cbact01cApplication.main(new String[]{
                inputFile.toString(), outputDir.toString()
        });

        assertTrue(Files.exists(outputDir.resolve("outfile.txt")));
        assertTrue(Files.exists(outputDir.resolve("arryfile.txt")));
        assertTrue(Files.exists(outputDir.resolve("vbrcfile.txt")));

        List<String> outLines = Files.readAllLines(outputDir.resolve("outfile.txt"));
        assertEquals(2, outLines.size());

        List<String> arryLines = Files.readAllLines(outputDir.resolve("arryfile.txt"));
        assertEquals(2, arryLines.size());

        // VBRC writes 2 records per account (VB1 + VB2)
        List<String> vbrcLines = Files.readAllLines(outputDir.resolve("vbrcfile.txt"));
        assertEquals(4, vbrcLines.size());
    }
}
