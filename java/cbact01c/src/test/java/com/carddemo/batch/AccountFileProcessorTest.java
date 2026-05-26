package com.carddemo.batch;

import com.carddemo.batch.io.AccountFileReader;
import com.carddemo.batch.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Verifies that the Java rewrite of CBACT01C produces results identical
 * to the original COBOL program for a set of sample inputs.
 *
 * All zoned-decimal values use COBOL PIC S9(10)V99 encoding where
 * the last byte carries an ASCII overpunch sign:
 *   '{' = +0, 'A'..'I' = +1..+9, '}' = -0, 'J'..'R' = -1..-9
 */
class AccountFileProcessorTest {

    /** Account with zero debit → triggers 2525.00 default.
     *  bal=194.00, credit=2020.00, cash=1020.00, cyc-credit=0, cyc-debit=0 */
    private static final String ACCT_ZERO_DEBIT =
            "00000000001Y00000001940{00000020200{00000010200{" +
            "2014-11-202025-05-202025-05-20" +
            "00000000000{00000000000{" +
            "A000000000A000000000" + " ".repeat(178);

    /** Account with non-zero debit → no default.
     *  bal=500.00, credit=4000.00, cash=3000.00, cyc-credit=75.00, cyc-debit=120.00 */
    private static final String ACCT_WITH_DEBIT =
            "00000000042N00000005000{00000040000{00000030000{" +
            "2020-01-152026-06-302026-06-30" +
            "00000000750{00000001200{" +
            "B123456789B123456789" + " ".repeat(178);

    /** Account with negative balance (-50.00).
     *  bal=-50.00 (overpunch '}'), credit=10000.00, cash=5000.00,
     *  cyc-credit=100.00, cyc-debit=200.00 */
    private static final String ACCT_NEGATIVE_BAL =
            "00000000099Y00000000500}00000100000{00000050000{" +
            "2019-03-012024-12-312024-12-31" +
            "00000001000{00000002000{" +
            "C999999999C999999999" + " ".repeat(178);

    @TempDir
    Path tempDir;
    Path acctFile;
    Path outFile;
    Path arrayFile;
    Path vbrFile;

    @BeforeEach
    void setUp() {
        acctFile = tempDir.resolve("acctdata.txt");
        outFile = tempDir.resolve("outfile.txt");
        arrayFile = tempDir.resolve("arryfile.txt");
        vbrFile = tempDir.resolve("vbrfile.txt");
    }

    // ========================= Build-record unit tests =========================

    @Test
    void buildOutRecord_zeroDebitDefaultsTo2525() {
        AccountRecord acct = parse(ACCT_ZERO_DEBIT);
        OutAccountRecord out = AccountFileProcessor.buildOutRecord(acct);

        assertEquals(1L, out.acctId());
        assertEquals('Y', out.activeStatus());
        assertEquals(0, new BigDecimal("194.00").compareTo(out.currBal()));
        assertEquals(0, new BigDecimal("2020.00").compareTo(out.creditLimit()));
        assertEquals(0, new BigDecimal("1020.00").compareTo(out.cashCreditLimit()));
        assertEquals("2014-11-20", out.openDate());
        assertEquals("2025-05-20", out.expirationDate());
        assertEquals("20250520", out.reissueDate());
        assertEquals(0, BigDecimal.ZERO.compareTo(out.currCycCredit()));
        assertEquals(0, new BigDecimal("2525.00").compareTo(out.currCycDebit()));
        assertEquals("A000000000", out.groupId());
    }

    @Test
    void buildOutRecord_nonZeroDebitPreserved() {
        AccountRecord acct = parse(ACCT_WITH_DEBIT);
        OutAccountRecord out = AccountFileProcessor.buildOutRecord(acct);

        assertEquals(42L, out.acctId());
        assertEquals(0, new BigDecimal("120.00").compareTo(out.currCycDebit()));
        assertEquals("20260630", out.reissueDate());
    }

    @Test
    void buildOutRecord_negativeBalance() {
        AccountRecord acct = parse(ACCT_NEGATIVE_BAL);
        OutAccountRecord out = AccountFileProcessor.buildOutRecord(acct);

        assertEquals(99L, out.acctId());
        assertEquals(0, new BigDecimal("-50.00").compareTo(out.currBal()));
    }

    @Test
    void buildArrayRecord_slotsPopulatedCorrectly() {
        AccountRecord acct = parse(ACCT_ZERO_DEBIT);
        ArrayRecord arr = AccountFileProcessor.buildArrayRecord(acct);

        assertEquals(1L, arr.acctId());
        List<ArrayRecord.BalanceEntry> entries = arr.entries();
        assertEquals(5, entries.size());

        // Slot 1: actual balance 194.00, debit = 1005.00
        assertEquals(0, new BigDecimal("194.00").compareTo(entries.get(0).currBal()));
        assertEquals(0, new BigDecimal("1005.00").compareTo(entries.get(0).currCycDebit()));

        // Slot 2: actual balance, debit = 1525.00
        assertEquals(0, new BigDecimal("194.00").compareTo(entries.get(1).currBal()));
        assertEquals(0, new BigDecimal("1525.00").compareTo(entries.get(1).currCycDebit()));

        // Slot 3: hardcoded negatives
        assertEquals(0, new BigDecimal("-1025.00").compareTo(entries.get(2).currBal()));
        assertEquals(0, new BigDecimal("-2500.00").compareTo(entries.get(2).currCycDebit()));

        // Slots 4-5: zeroed
        assertEquals(0, BigDecimal.ZERO.compareTo(entries.get(3).currBal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(entries.get(3).currCycDebit()));
        assertEquals(0, BigDecimal.ZERO.compareTo(entries.get(4).currBal()));
        assertEquals(0, BigDecimal.ZERO.compareTo(entries.get(4).currCycDebit()));
    }

    @Test
    void buildVbRecords() {
        AccountRecord acct = parse(ACCT_ZERO_DEBIT);
        VbRecord1 vb1 = AccountFileProcessor.buildVbRecord1(acct);
        VbRecord2 vb2 = AccountFileProcessor.buildVbRecord2(acct);

        assertEquals(1L, vb1.acctId());
        assertEquals('Y', vb1.activeStatus());

        assertEquals(1L, vb2.acctId());
        assertEquals(0, new BigDecimal("194.00").compareTo(vb2.currBal()));
        assertEquals(0, new BigDecimal("2020.00").compareTo(vb2.creditLimit()));
        assertEquals("2025", vb2.reissueYear());
    }

    // ========================= Writer format tests =========================

    @Test
    void writeOutRecord_format() {
        OutAccountRecord rec = new OutAccountRecord(
                1L, 'Y',
                new BigDecimal("194.00"), new BigDecimal("2020.00"),
                new BigDecimal("1020.00"),
                "2014-11-20", "2025-05-20", "20250520",
                BigDecimal.ZERO, new BigDecimal("2525.00"),
                "A000000000");

        StringWriter sw = new StringWriter();
        AccountFileProcessor.writeOutRecord(new PrintWriter(sw), rec);
        String line = sw.toString().trim();

        assertEquals(
                "00000000001|Y|194.00|2020.00|1020.00|2014-11-20|2025-05-20|20250520|0|2525.00|A000000000",
                line);
    }

    @Test
    void writeArrayRecord_format() {
        ArrayRecord rec = AccountFileProcessor.buildArrayRecord(parse(ACCT_ZERO_DEBIT));

        StringWriter sw = new StringWriter();
        AccountFileProcessor.writeArrayRecord(new PrintWriter(sw), rec);
        String line = sw.toString().trim();

        assertTrue(line.startsWith("00000000001|"));
        String[] parts = line.split("\\|");
        assertEquals(11, parts.length); // ID + 5*(bal + debit)
    }

    @Test
    void writeVbRecords_format() {
        VbRecord1 vb1 = new VbRecord1(1L, 'Y');
        VbRecord2 vb2 = new VbRecord2(1L, new BigDecimal("194.00"),
                new BigDecimal("2020.00"), "2025");

        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        AccountFileProcessor.writeVbRecord1(pw, vb1);
        AccountFileProcessor.writeVbRecord2(pw, vb2);
        pw.flush();

        String[] lines = sw.toString().trim().split("\\R");
        assertEquals(2, lines.length);
        assertEquals("VB1|00000000001|Y", lines[0]);
        assertEquals("VB2|00000000001|194.00|2020.00|2025", lines[1]);
    }

    // ========================= Full pipeline integration tests =========================

    @Test
    void fullPipeline_singleRecord_zeroDebit() throws IOException {
        Files.writeString(acctFile, ACCT_ZERO_DEBIT + "\n");

        var processor = new AccountFileProcessor(acctFile, outFile, arrayFile, vbrFile);
        int count = processor.process();

        assertEquals(1, count);
        assertOutputFiles(1);

        List<String> outLines = Files.readAllLines(outFile);
        assertEquals(1, outLines.size());
        assertTrue(outLines.get(0).contains("2525.00"),
                "Expected default debit 2525.00 in output");
        assertTrue(outLines.get(0).contains("20250520"),
                "Expected reformatted reissue date");

        List<String> vbrLines = Files.readAllLines(vbrFile);
        assertEquals(2, vbrLines.size());
        assertTrue(vbrLines.get(0).startsWith("VB1|"));
        assertTrue(vbrLines.get(1).startsWith("VB2|"));
    }

    @Test
    void fullPipeline_singleRecord_nonZeroDebit() throws IOException {
        Files.writeString(acctFile, ACCT_WITH_DEBIT + "\n");

        var processor = new AccountFileProcessor(acctFile, outFile, arrayFile, vbrFile);
        int count = processor.process();

        assertEquals(1, count);

        List<String> outLines = Files.readAllLines(outFile);
        // Non-zero debit 120.00 is preserved
        assertTrue(outLines.get(0).contains("120.00"),
                "Expected preserved debit 120.00 in output");
        assertFalse(outLines.get(0).contains("2525.00"),
                "Default debit should NOT apply when debit is non-zero");
    }

    @Test
    void fullPipeline_negativeBalance() throws IOException {
        Files.writeString(acctFile, ACCT_NEGATIVE_BAL + "\n");

        var processor = new AccountFileProcessor(acctFile, outFile, arrayFile, vbrFile);
        int count = processor.process();

        assertEquals(1, count);
        List<String> outLines = Files.readAllLines(outFile);
        assertTrue(outLines.get(0).contains("-50.00"),
                "Expected negative balance in output");
    }

    @Test
    void fullPipeline_multipleRecords() throws IOException {
        String input = ACCT_ZERO_DEBIT + "\n"
                + ACCT_WITH_DEBIT + "\n"
                + ACCT_NEGATIVE_BAL + "\n";
        Files.writeString(acctFile, input);

        var processor = new AccountFileProcessor(acctFile, outFile, arrayFile, vbrFile);
        int count = processor.process();

        assertEquals(3, count);
        assertOutputFiles(3);

        List<String> outLines = Files.readAllLines(outFile);
        assertEquals(3, outLines.size());

        List<String> arrLines = Files.readAllLines(arrayFile);
        assertEquals(3, arrLines.size());

        List<String> vbrLines = Files.readAllLines(vbrFile);
        assertEquals(6, vbrLines.size()); // 2 VBR records per account
    }

    @Test
    void fullPipeline_emptyInput() throws IOException {
        Files.writeString(acctFile, "");

        var processor = new AccountFileProcessor(acctFile, outFile, arrayFile, vbrFile);
        int count = processor.process();

        assertEquals(0, count);
        assertEquals(0, Files.readAllLines(outFile).size());
    }

    @Test
    void fullPipeline_withRealSampleData() throws IOException {
        Path realData = Path.of(
                System.getProperty("user.dir")).getParent().getParent()
                .resolve("app/data/ASCII/acctdata.txt");

        if (!Files.exists(realData)) {
            return; // skip if not running from expected directory
        }

        List<String> allLines = Files.readAllLines(realData);
        assertTrue(allLines.size() >= 5, "Expected at least 5 records in sample data");

        Path subsetFile = tempDir.resolve("subset.txt");
        Files.write(subsetFile, allLines.subList(0, 5));

        var processor = new AccountFileProcessor(
                subsetFile, outFile, arrayFile, vbrFile);
        int count = processor.process();

        assertEquals(5, count);
        assertOutputFiles(5);

        // All 5 records have zero debit → all should get 2525.00
        List<String> outLines = Files.readAllLines(outFile);
        for (String line : outLines) {
            assertTrue(line.contains("2525.00"),
                    "All sample records have zero debit → should default to 2525.00");
        }

        // Verify reissue dates were reformatted (no dashes)
        for (String line : outLines) {
            String[] parts = line.split("\\|");
            String reissueDate = parts[7];
            assertFalse(reissueDate.contains("-"),
                    "Reissue date should be in YYYYMMDD format: " + reissueDate);
            assertEquals(8, reissueDate.length(),
                    "Reissue date should be 8 chars: " + reissueDate);
        }
    }

    // ========================= Helpers =========================

    private AccountRecord parse(String line) {
        return AccountFileReader.parseLine(line);
    }

    private void assertOutputFiles(int expectedRecords) throws IOException {
        assertTrue(Files.exists(outFile), "outFile should exist");
        assertTrue(Files.exists(arrayFile), "arrayFile should exist");
        assertTrue(Files.exists(vbrFile), "vbrFile should exist");

        assertEquals(expectedRecords, Files.readAllLines(outFile).size());
        assertEquals(expectedRecords, Files.readAllLines(arrayFile).size());
        assertEquals(expectedRecords * 2, Files.readAllLines(vbrFile).size());
    }
}
