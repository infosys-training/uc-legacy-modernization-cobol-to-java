package com.cardemo.batch;

import com.cardemo.batch.io.AccountFileReader;
import com.cardemo.batch.model.AccountRecord;
import com.cardemo.batch.model.ArrayRecord;
import com.cardemo.batch.model.OutAccountRecord;
import com.cardemo.batch.model.VbrcRecord1;
import com.cardemo.batch.model.VbrcRecord2;

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
 * Verifies that the Java rewrite of CBACT01C produces results identical to
 * the COBOL version for a set of sample account records drawn from the
 * original {@code acctdata.txt} data file.
 */
class Cbact01cTest {

    private Cbact01c program;

    @BeforeEach
    void setUp() {
        program = new Cbact01c();
    }

    // ---------------------------------------------------------------
    // Record parsing — mirrors what COBOL does with READ ... INTO
    // ---------------------------------------------------------------

    @Test
    void parseFirstSampleRecord() {
        // First line of acctdata.txt:
        // 00000000001Y00000001940{00000020200{00000010200{
        //   2014-11-202025-05-202025-05-20
        //   00000000000{00000000000{A000000000...
        String line = "00000000001Y00000001940{00000020200{00000010200{"
                + "2014-11-202025-05-202025-05-20"
                + "00000000000{00000000000{"
                + "          A000000000"
                + " ".repeat(178);

        AccountRecord rec = AccountFileReader.parseLine(line);

        assertEquals(1L, rec.acctId());
        assertEquals("Y", rec.activeStatus());
        assertEquals(0, new BigDecimal("194.00").compareTo(rec.currBal()));
        assertEquals(0, new BigDecimal("2020.00").compareTo(rec.creditLimit()));
        assertEquals(0, new BigDecimal("1020.00").compareTo(rec.cashCreditLimit()));
        assertEquals("2014-11-20", rec.openDate());
        assertEquals("2025-05-20", rec.expirationDate());
        assertEquals("2025-05-20", rec.reissueDate());
        assertEquals(0, BigDecimal.ZERO.compareTo(rec.currCycCredit()));
        assertEquals(0, BigDecimal.ZERO.compareTo(rec.currCycDebit()));
        assertEquals("A000000000", rec.groupId());
    }

    @Test
    void parseSecondSampleRecord() {
        String line = "00000000002Y00000001580{00000061300{00000054480{"
                + "2013-06-192024-08-112024-08-11"
                + "00000000000{00000000000{"
                + "          A000000000"
                + " ".repeat(178);

        AccountRecord rec = AccountFileReader.parseLine(line);

        assertEquals(2L, rec.acctId());
        assertEquals("Y", rec.activeStatus());
        assertEquals(0, new BigDecimal("158.00").compareTo(rec.currBal()));
        assertEquals(0, new BigDecimal("6130.00").compareTo(rec.creditLimit()));
        assertEquals(0, new BigDecimal("5448.00").compareTo(rec.cashCreditLimit()));
        assertEquals("2013-06-19", rec.openDate());
        assertEquals("2024-08-11", rec.expirationDate());
        assertEquals("2024-08-11", rec.reissueDate());
    }

    // ---------------------------------------------------------------
    // 1300-POPUL-ACCT-RECORD — OutAccountRecord
    // ---------------------------------------------------------------

    @Test
    void populateOutRecord_dateConvertedAndDebitDefaulted() {
        AccountRecord acct = sampleAccount(1L, "Y",
                new BigDecimal("194.00"), new BigDecimal("2020.00"),
                new BigDecimal("1020.00"),
                "2014-11-20", "2025-05-20", "2025-05-20",
                BigDecimal.ZERO, BigDecimal.ZERO, "A000000000");

        OutAccountRecord out = program.populateOutRecord(acct);

        assertEquals(1L, out.acctId());
        assertEquals("Y", out.activeStatus());
        assertEquals(0, new BigDecimal("194.00").compareTo(out.currBal()));
        assertEquals(0, new BigDecimal("2020.00").compareTo(out.creditLimit()));
        assertEquals(0, new BigDecimal("1020.00").compareTo(out.cashCreditLimit()));
        assertEquals("2014-11-20", out.openDate());
        assertEquals("2025-05-20", out.expirationDate());
        // Reissue date converted from YYYY-MM-DD to YYYYMMDD, padded to 10
        assertEquals("20250520  ", out.reissueDate());
        assertEquals(0, BigDecimal.ZERO.compareTo(out.currCycCredit()));
        // When debit is zero, COBOL defaults it to 2525.00
        assertEquals(0, new BigDecimal("2525.00").compareTo(out.currCycDebit()));
        assertEquals("A000000000", out.groupId());
    }

    @Test
    void populateOutRecord_nonZeroDebitPreserved() {
        AccountRecord acct = sampleAccount(2L, "Y",
                new BigDecimal("158.00"), new BigDecimal("6130.00"),
                new BigDecimal("5448.00"),
                "2013-06-19", "2024-08-11", "2024-08-11",
                BigDecimal.ZERO, new BigDecimal("300.00"), "A000000000");

        OutAccountRecord out = program.populateOutRecord(acct);

        // Non-zero debit should pass through, not be replaced by 2525.00
        assertEquals(0, new BigDecimal("300.00").compareTo(out.currCycDebit()));
    }

    @Test
    void populateOutRecord_reissueDateConversion() {
        AccountRecord acct = sampleAccount(3L, "Y",
                new BigDecimal("147.00"), new BigDecimal("4909.00"),
                new BigDecimal("538.00"),
                "2013-08-23", "2024-01-10", "2024-01-10",
                BigDecimal.ZERO, BigDecimal.ZERO, "A000000000");

        OutAccountRecord out = program.populateOutRecord(acct);
        assertEquals("20240110  ", out.reissueDate());
    }

    // ---------------------------------------------------------------
    // 1400-POPUL-ARRAY-RECORD — ArrayRecord
    // ---------------------------------------------------------------

    @Test
    void populateArrayRecord_structure() {
        BigDecimal bal = new BigDecimal("194.00");
        AccountRecord acct = sampleAccount(1L, "Y",
                bal, BigDecimal.ZERO, BigDecimal.ZERO,
                "2014-11-20", "2025-05-20", "2025-05-20",
                BigDecimal.ZERO, BigDecimal.ZERO, "A000000000");

        ArrayRecord arr = program.populateArrayRecord(acct);

        assertEquals(1L, arr.acctId());
        assertEquals(5, arr.entries().size());

        // Index 0 (COBOL index 1): bal = ACCT-CURR-BAL, debit = 1005.00
        assertEquals(0, bal.compareTo(arr.entries().get(0).currBal()));
        assertEquals(0, new BigDecimal("1005.00").compareTo(
                arr.entries().get(0).currCycDebit()));

        // Index 1 (COBOL index 2): bal = ACCT-CURR-BAL, debit = 1525.00
        assertEquals(0, bal.compareTo(arr.entries().get(1).currBal()));
        assertEquals(0, new BigDecimal("1525.00").compareTo(
                arr.entries().get(1).currCycDebit()));

        // Index 2 (COBOL index 3): bal = -1025.00, debit = -2500.00
        assertEquals(0, new BigDecimal("-1025.00").compareTo(
                arr.entries().get(2).currBal()));
        assertEquals(0, new BigDecimal("-2500.00").compareTo(
                arr.entries().get(2).currCycDebit()));

        // Indices 3-4 (COBOL 4-5): zeroed by INITIALIZE
        assertEquals(0, BigDecimal.ZERO.compareTo(arr.entries().get(3).currBal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(arr.entries().get(3).currCycDebit()));
        assertEquals(0, BigDecimal.ZERO.compareTo(arr.entries().get(4).currBal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(arr.entries().get(4).currCycDebit()));
    }

    // ---------------------------------------------------------------
    // 1500-POPUL-VBRC-RECORD — VbrcRecord1 & VbrcRecord2
    // ---------------------------------------------------------------

    @Test
    void populateVbrc1() {
        AccountRecord acct = sampleAccount(1L, "Y",
                new BigDecimal("194.00"), BigDecimal.ZERO, BigDecimal.ZERO,
                "2014-11-20", "2025-05-20", "2025-05-20",
                BigDecimal.ZERO, BigDecimal.ZERO, "A000000000");

        VbrcRecord1 vb1 = program.populateVbrc1(acct);
        assertEquals(1L, vb1.acctId());
        assertEquals("Y", vb1.activeStatus());
    }

    @Test
    void populateVbrc2() {
        AccountRecord acct = sampleAccount(1L, "Y",
                new BigDecimal("194.00"), new BigDecimal("2020.00"),
                new BigDecimal("1020.00"),
                "2014-11-20", "2025-05-20", "2025-05-20",
                BigDecimal.ZERO, BigDecimal.ZERO, "A000000000");

        VbrcRecord2 vb2 = program.populateVbrc2(acct);
        assertEquals(1L, vb2.acctId());
        assertEquals(0, new BigDecimal("194.00").compareTo(vb2.currBal()));
        assertEquals(0, new BigDecimal("2020.00").compareTo(vb2.creditLimit()));
        assertEquals("2025", vb2.reissueYear());
    }

    @Test
    void populateVbrc2_differentYear() {
        AccountRecord acct = sampleAccount(4L, "Y",
                new BigDecimal("40.00"), new BigDecimal("3503.00"),
                new BigDecimal("2789.00"),
                "2012-11-17", "2023-12-16", "2023-12-16",
                BigDecimal.ZERO, BigDecimal.ZERO, "A000000000");

        VbrcRecord2 vb2 = program.populateVbrc2(acct);
        assertEquals("2023", vb2.reissueYear());
    }

    // ---------------------------------------------------------------
    // End-to-end: read sample file, process, verify output files
    // ---------------------------------------------------------------

    @Test
    void endToEnd_processesAllRecords(@TempDir Path tempDir) throws IOException {
        Path sampleFile = tempDir.resolve("acctdata.txt");
        Path outFile = tempDir.resolve("outfile.txt");
        Path arryFile = tempDir.resolve("arryfile.txt");
        Path vbrcFile = tempDir.resolve("vbrcfile.txt");

        // Write 3 sample records
        Files.writeString(sampleFile, String.join("\n",
                padToRecord("00000000001Y00000001940{00000020200{00000010200{"
                        + "2014-11-202025-05-202025-05-20"
                        + "00000000000{00000000000{          A000000000"),
                padToRecord("00000000002Y00000001580{00000061300{00000054480{"
                        + "2013-06-192024-08-112024-08-11"
                        + "00000000000{00000000000{          A000000000"),
                padToRecord("00000000003Y00000001470{00000049090{00000005380{"
                        + "2013-08-232024-01-102024-01-10"
                        + "00000000000{00000000000{          A000000000"))
                + "\n");

        program.execute(sampleFile, outFile, arryFile, vbrcFile);

        assertEquals(3, program.getRecordsProcessed());

        // Verify OUTFILE has 3 lines
        List<String> outLines = Files.readAllLines(outFile);
        assertEquals(3, outLines.size());

        // Verify first output line values
        String[] fields = outLines.get(0).split("\\|");
        assertEquals("00000000001", fields[0]);  // acctId
        assertEquals("Y", fields[1]);             // activeStatus
        assertEquals("194.00", fields[2]);         // currBal
        assertEquals("2020.00", fields[3]);        // creditLimit
        assertEquals("1020.00", fields[4]);        // cashCreditLimit
        assertEquals("2014-11-20", fields[5]);     // openDate
        assertEquals("2025-05-20", fields[6]);     // expirationDate
        assertEquals("20250520  ", fields[7]);     // reissueDate (converted)
        assertEquals("0.00", fields[8]);           // currCycCredit
        assertEquals("2525.00", fields[9]);        // currCycDebit (defaulted)
        assertEquals("A000000000", fields[10]);    // groupId

        // Verify ARRYFILE has 3 lines
        List<String> arryLines = Files.readAllLines(arryFile);
        assertEquals(3, arryLines.size());

        // Verify first array line
        String[] arrFields = arryLines.get(0).split("\\|");
        assertEquals("00000000001", arrFields[0]);
        assertEquals("194.00", arrFields[1]);    // bal[0]
        assertEquals("1005.00", arrFields[2]);   // debit[0]
        assertEquals("194.00", arrFields[3]);    // bal[1]
        assertEquals("1525.00", arrFields[4]);   // debit[1]
        assertEquals("-1025.00", arrFields[5]);  // bal[2]
        assertEquals("-2500.00", arrFields[6]);  // debit[2]
        assertEquals("0", arrFields[7]);         // bal[3]
        assertEquals("0", arrFields[8]);         // debit[3]
        assertEquals("0", arrFields[9]);         // bal[4]
        assertEquals("0", arrFields[10]);        // debit[4]

        // Verify VBRCFILE has 6 lines (2 per record)
        List<String> vbrcLines = Files.readAllLines(vbrcFile);
        assertEquals(6, vbrcLines.size());

        // First VB1
        String[] vb1Fields = vbrcLines.get(0).split("\\|");
        assertEquals("VB1", vb1Fields[0]);
        assertEquals("00000000001", vb1Fields[1]);
        assertEquals("Y", vb1Fields[2]);

        // First VB2
        String[] vb2Fields = vbrcLines.get(1).split("\\|");
        assertEquals("VB2", vb2Fields[0]);
        assertEquals("00000000001", vb2Fields[1]);
        assertEquals("194.00", vb2Fields[2]);
        assertEquals("2020.00", vb2Fields[3]);
        assertEquals("2025", vb2Fields[4]);
    }

    @Test
    void endToEnd_emptyFile(@TempDir Path tempDir) throws IOException {
        Path sampleFile = tempDir.resolve("acctdata.txt");
        Path outFile = tempDir.resolve("outfile.txt");
        Path arryFile = tempDir.resolve("arryfile.txt");
        Path vbrcFile = tempDir.resolve("vbrcfile.txt");

        Files.writeString(sampleFile, "");

        program.execute(sampleFile, outFile, arryFile, vbrcFile);

        assertEquals(0, program.getRecordsProcessed());
        assertEquals(0, Files.readAllLines(outFile).size());
    }

    @Test
    void endToEnd_withRealSampleFile(@TempDir Path tempDir) throws IOException {
        Path sampleFile = Path.of(
                getClass().getClassLoader()
                        .getResource("testdata/sample_acctdata.txt").getPath());
        Path outFile = tempDir.resolve("outfile.txt");
        Path arryFile = tempDir.resolve("arryfile.txt");
        Path vbrcFile = tempDir.resolve("vbrcfile.txt");

        program.execute(sampleFile, outFile, arryFile, vbrcFile);

        assertEquals(5, program.getRecordsProcessed());

        List<String> outLines = Files.readAllLines(outFile);
        assertEquals(5, outLines.size());

        // Verify all accounts have defaulted debit (all are zero in sample)
        for (String line : outLines) {
            String[] fields = line.split("\\|");
            assertEquals("2525.00", fields[9],
                    "Zero debit should be defaulted to 2525.00");
        }

        // Verify VBRC file has 10 lines (2 per record)
        List<String> vbrcLines = Files.readAllLines(vbrcFile);
        assertEquals(10, vbrcLines.size());

        // Verify record 4 reissue year
        String[] vb2Line4 = vbrcLines.get(7).split("\\|");
        assertEquals("2023", vb2Line4[4]);

        // Verify record 5 values
        String[] outFields5 = outLines.get(4).split("\\|");
        assertEquals("00000000005", outFields5[0]);
        assertEquals("345.00", outFields5[2]);   // currBal
        assertEquals("3819.00", outFields5[3]);  // creditLimit
        assertEquals("2430.00", outFields5[4]);  // cashCreditLimit
    }

    @Test
    void convertReissueDate_yyyyMmDdToYyyymmdd() {
        assertEquals("20250520  ", Cbact01c.convertReissueDate("2025-05-20"));
        assertEquals("20240110  ", Cbact01c.convertReissueDate("2024-01-10"));
        assertEquals("20231216  ", Cbact01c.convertReissueDate("2023-12-16"));
    }

    // ---------------------------------------------------------------
    // Helpers
    // ---------------------------------------------------------------

    private static AccountRecord sampleAccount(
            long id, String status, BigDecimal bal, BigDecimal creditLimit,
            BigDecimal cashCreditLimit, String openDate, String expDate,
            String reissueDate, BigDecimal cycCredit, BigDecimal cycDebit,
            String groupId) {

        return new AccountRecord(id, status, bal, creditLimit, cashCreditLimit,
                openDate, expDate, reissueDate, cycCredit, cycDebit, "", groupId);
    }

    private static String padToRecord(String s) {
        if (s.length() >= AccountRecord.RECORD_LENGTH) {
            return s.substring(0, AccountRecord.RECORD_LENGTH);
        }
        return s + " ".repeat(AccountRecord.RECORD_LENGTH - s.length());
    }
}
