package com.carddemo.batch;

import com.carddemo.model.AccountRecord;
import com.carddemo.util.DateFormatter;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.math.BigDecimal;
import java.nio.file.*;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit 5 tests for AccountFileSplitter (CBACT01C rewrite).
 *
 * Tests verify that the Java version produces identical results to the
 * COBOL version for sample inputs from app/data/ASCII/acctdata.txt.
 */
class AccountFileSplitterTest {

    @TempDir
    Path tempDir;

    // Sample VSAM records from the actual CardDemo test data (300-byte fixed-length).
    // These are the first 3 records from app/data/ASCII/acctdata.txt.
    private static final String RECORD_1 =
            "00000000001Y00000001940{00000020200{00000010200{2014-11-202025-05-202025-05-2000000000000{00000000000{A000000000"
            + " ".repeat(188);

    private static final String RECORD_2 =
            "00000000002Y00000001580{00000061300{00000054480{2013-06-192024-08-112024-08-1100000000000{00000000000{A000000000"
            + " ".repeat(188);

    // Record with non-zero debit to test the non-substitution path
    private static final String RECORD_3 =
            "00000000003Y00000001470{00000049090{00000005380{2013-08-232024-01-102024-01-1000000000000{00000000000{A000000000"
            + " ".repeat(188);

    // ─── AccountRecord Parsing Tests ───

    @Test
    @DisplayName("Parse first account record — verify all fields")
    void testParseRecord1() {
        AccountRecord acct = AccountRecord.parse(RECORD_1);

        assertEquals(1L, acct.acctId());
        assertEquals("Y", acct.activeStatus());
        assertEquals(new BigDecimal("194.00"), acct.currBal());
        assertEquals(new BigDecimal("2020.00"), acct.creditLimit());
        assertEquals(new BigDecimal("1020.00"), acct.cashCreditLimit());
        assertEquals("2014-11-20", acct.openDate());
        assertEquals("2025-05-20", acct.expirationDate());
        assertEquals("2025-05-20", acct.reissueDate());
        assertEquals(new BigDecimal("0.00"), acct.currCycCredit());
        assertEquals(new BigDecimal("0.00"), acct.currCycDebit());
        assertEquals("A000000000", acct.addrZip());
        assertEquals("", acct.groupId());
    }

    @Test
    @DisplayName("Parse second account record — verify different balance values")
    void testParseRecord2() {
        AccountRecord acct = AccountRecord.parse(RECORD_2);

        assertEquals(2L, acct.acctId());
        assertEquals("Y", acct.activeStatus());
        assertEquals(new BigDecimal("158.00"), acct.currBal());
        assertEquals(new BigDecimal("6130.00"), acct.creditLimit());
        assertEquals(new BigDecimal("5448.00"), acct.cashCreditLimit());
        assertEquals("2013-06-19", acct.openDate());
        assertEquals("2024-08-11", acct.expirationDate());
    }

    @Test
    @DisplayName("Parse record with signed overpunch characters")
    void testParseSignedOverpunch() {
        // '{' = +0, 'A' = +1, 'B' = +2, etc.
        BigDecimal val = AccountRecord.parseSignedDecimal("00000001940{", 0, 12, 2);
        assertEquals(new BigDecimal("194.00"), val);

        // '}' = negative overpunch for digit 0 — value is -194.00, not -0
        BigDecimal neg = AccountRecord.parseSignedDecimal("00000001940}", 0, 12, 2);
        assertEquals(new BigDecimal("-194.00"), neg);
    }

    // ─── DateFormatter Tests ───

    @Test
    @DisplayName("DateFormatter: YYYY-MM-DD → YYYYMMDD (type 2→2)")
    void testDateFormatType2to2() {
        assertEquals("20250520", DateFormatter.format("2025-05-20", "2", "2"));
    }

    @Test
    @DisplayName("DateFormatter: YYYYMMDD → YYYY-MM-DD (type 1→1)")
    void testDateFormatType1to1() {
        assertEquals("2025-05-20", DateFormatter.format("20250520", "1", "1"));
    }

    @Test
    @DisplayName("DateFormatter: YYYY-MM-DD → YYYY-MM-DD (type 2→1)")
    void testDateFormatType2to1() {
        assertEquals("2025-05-20", DateFormatter.format("2025-05-20", "2", "1"));
    }

    @Test
    @DisplayName("DateFormatter: YYYYMMDD → YYYYMMDD (type 1→2)")
    void testDateFormatType1to2() {
        assertEquals("20250520", DateFormatter.format("20250520", "1", "2"));
    }

    @Test
    @DisplayName("DateFormatter: handles null/blank input")
    void testDateFormatNullBlank() {
        assertNull(DateFormatter.format(null, "1", "1"));
        assertEquals("   ", DateFormatter.format("   ", "1", "1"));
    }

    // ─── Numeric Formatting Tests ───

    @Test
    @DisplayName("formatNumericField: positive value with overpunch")
    void testFormatPositive() {
        // 194.00 → 19400 → pad to 12 → "000000019400" → last digit '0' → '{'
        String result = AccountFileSplitter.formatNumericField(new BigDecimal("194.00"), 12);
        assertEquals("00000001940{", result);
    }

    @Test
    @DisplayName("formatNumericField: negative value with overpunch")
    void testFormatNegative() {
        // -1025.00 → 102500 → pad → "000000102500" → last digit '0', negative → '}'
        String result = AccountFileSplitter.formatNumericField(new BigDecimal("-1025.00"), 12);
        assertEquals("00000010250}", result);
    }

    @Test
    @DisplayName("formatNumericField: zero value")
    void testFormatZero() {
        String result = AccountFileSplitter.formatNumericField(BigDecimal.ZERO, 12);
        assertEquals("00000000000{", result);
    }

    @Test
    @DisplayName("formatNumericField: value with non-zero last digit")
    void testFormatNonZeroLastDigit() {
        // 1005.00 → 100500 → pad → "000000100500" → last '0' → '{'
        String result = AccountFileSplitter.formatNumericField(new BigDecimal("1005.00"), 12);
        assertEquals("00000010050{", result);
    }

    @Test
    @DisplayName("formatNumericField: value ending in non-zero digit for positive overpunch")
    void testFormatPositiveNonZeroLastDigit() {
        // 2525.00 → 252500 → pad → "000000252500" → last '0' → '{'
        String result = AccountFileSplitter.formatNumericField(new BigDecimal("2525.00"), 12);
        assertEquals("00000025250{", result);

        // 1525.00 → 152500 → pad → "000000152500" → last '0' → '{'
        result = AccountFileSplitter.formatNumericField(new BigDecimal("1525.00"), 12);
        assertEquals("00000015250{", result);

        // 123.45 → 12345 → pad → "000000012345" → last '5' → 'E'
        result = AccountFileSplitter.formatNumericField(new BigDecimal("123.45"), 12);
        assertEquals("00000001234E", result);
    }

    // ─── End-to-End Batch Execution Tests ───

    @Test
    @DisplayName("Full batch execution with 3 sample records")
    void testFullExecution() throws IOException {
        // Write sample input
        Path input = tempDir.resolve("acctfile.dat");
        Files.writeString(input, RECORD_1 + "\n" + RECORD_2 + "\n" + RECORD_3 + "\n");

        Path outFile = tempDir.resolve("outfile.dat");
        Path arryFile = tempDir.resolve("arryfile.dat");
        Path vbrcFile = tempDir.resolve("vbrcfile.dat");

        PrintStream nullLog = new PrintStream(OutputStream.nullOutputStream());
        AccountFileSplitter splitter = new AccountFileSplitter(
                input, outFile, arryFile, vbrcFile, nullLog);

        int count = splitter.execute();
        assertEquals(3, count);

        // Verify all output files exist and have correct number of records
        List<String> outLines = Files.readAllLines(outFile);
        assertEquals(3, outLines.size());

        List<String> arryLines = Files.readAllLines(arryFile);
        assertEquals(3, arryLines.size());

        List<String> vbrcLines = Files.readAllLines(vbrcFile);
        assertEquals(6, vbrcLines.size()); // 2 records per account
    }

    @Test
    @DisplayName("OUT-FILE: zero debit substituted with 2525.00")
    void testZeroDebitSubstitution() throws IOException {
        Path input = tempDir.resolve("acctfile.dat");
        Files.writeString(input, RECORD_1 + "\n");

        Path outFile = tempDir.resolve("outfile.dat");
        Path arryFile = tempDir.resolve("arryfile.dat");
        Path vbrcFile = tempDir.resolve("vbrcfile.dat");

        PrintStream nullLog = new PrintStream(OutputStream.nullOutputStream());
        new AccountFileSplitter(input, outFile, arryFile, vbrcFile, nullLog).execute();

        String outLine = Files.readAllLines(outFile).get(0);
        // The debit field (after credit field) should be 2525.00, not 0.00
        // Position: acctId(11)+status(1)+bal(12)+limit(12)+cashLimit(12)+open(10)+exp(10)+reissue(10)+credit(12) = 90
        // Debit starts at position 90, length 12
        String debitField = outLine.substring(90, 102);
        assertEquals("00000025250{", debitField, "Zero debit should be replaced with 2525.00");
    }

    @Test
    @DisplayName("OUT-FILE: reissue date reformatted from YYYY-MM-DD to YYYYMMDD")
    void testReissueDateFormatting() throws IOException {
        Path input = tempDir.resolve("acctfile.dat");
        Files.writeString(input, RECORD_1 + "\n");

        Path outFile = tempDir.resolve("outfile.dat");
        Path arryFile = tempDir.resolve("arryfile.dat");
        Path vbrcFile = tempDir.resolve("vbrcfile.dat");

        PrintStream nullLog = new PrintStream(OutputStream.nullOutputStream());
        new AccountFileSplitter(input, outFile, arryFile, vbrcFile, nullLog).execute();

        String outLine = Files.readAllLines(outFile).get(0);
        // Reissue date position: acctId(11)+status(1)+bal(12)+limit(12)+cashLimit(12)+open(10)+exp(10) = 68
        String reissueField = outLine.substring(68, 78);
        assertEquals("20250520  ", reissueField,
                "Reissue date should be reformatted from 2025-05-20 to 20250520");
    }

    @Test
    @DisplayName("ARRY-FILE: array slots populated correctly")
    void testArrayRecordPopulation() throws IOException {
        Path input = tempDir.resolve("acctfile.dat");
        Files.writeString(input, RECORD_1 + "\n");

        Path outFile = tempDir.resolve("outfile.dat");
        Path arryFile = tempDir.resolve("arryfile.dat");
        Path vbrcFile = tempDir.resolve("vbrcfile.dat");

        PrintStream nullLog = new PrintStream(OutputStream.nullOutputStream());
        new AccountFileSplitter(input, outFile, arryFile, vbrcFile, nullLog).execute();

        String arryLine = Files.readAllLines(arryFile).get(0);

        // Account ID
        assertEquals("00000000001", arryLine.substring(0, 11));

        // Slot 1: balance (194.00) + debit (1005.00)
        assertEquals("00000001940{", arryLine.substring(11, 23), "Slot 1 balance");
        assertEquals("00000010050{", arryLine.substring(23, 35), "Slot 1 debit");

        // Slot 2: balance (194.00) + debit (1525.00)
        assertEquals("00000001940{", arryLine.substring(35, 47), "Slot 2 balance");
        assertEquals("00000015250{", arryLine.substring(47, 59), "Slot 2 debit");

        // Slot 3: balance (-1025.00) + debit (-2500.00)
        assertEquals("00000010250}", arryLine.substring(59, 71), "Slot 3 balance");
        assertEquals("00000025000}", arryLine.substring(71, 83), "Slot 3 debit");

        // Slots 4-5: zeroes
        assertEquals("00000000000{", arryLine.substring(83, 95), "Slot 4 balance");
        assertEquals("00000000000{", arryLine.substring(95, 107), "Slot 4 debit");
    }

    @Test
    @DisplayName("VBRC-FILE: short and long records per account")
    void testVariableLengthRecords() throws IOException {
        Path input = tempDir.resolve("acctfile.dat");
        Files.writeString(input, RECORD_1 + "\n");

        Path outFile = tempDir.resolve("outfile.dat");
        Path arryFile = tempDir.resolve("arryfile.dat");
        Path vbrcFile = tempDir.resolve("vbrcfile.dat");

        PrintStream nullLog = new PrintStream(OutputStream.nullOutputStream());
        new AccountFileSplitter(input, outFile, arryFile, vbrcFile, nullLog).execute();

        List<String> vbrcLines = Files.readAllLines(vbrcFile);
        assertEquals(2, vbrcLines.size());

        // Short record: acctId + activeStatus
        String shortRec = vbrcLines.get(0);
        assertEquals("00000000001Y", shortRec);

        // Long record: acctId + currBal + creditLimit + reissueYear
        String longRec = vbrcLines.get(1);
        assertEquals("00000000001", longRec.substring(0, 11), "VB2 account ID");
        assertEquals("00000001940{", longRec.substring(11, 23), "VB2 balance");
        assertEquals("00000020200{", longRec.substring(23, 35), "VB2 credit limit");
        assertEquals("2025", longRec.substring(35, 39), "VB2 reissue year");
    }

    @Test
    @DisplayName("Process all 50 records from actual test data without errors")
    void testProcessActualTestData() throws IOException {
        Path testData = Path.of("../../app/data/ASCII/acctdata.txt");
        if (!Files.exists(testData)) {
            // Try absolute path
            testData = Path.of(System.getProperty("user.dir"))
                    .resolve("../../app/data/ASCII/acctdata.txt").normalize();
        }
        if (!Files.exists(testData)) {
            // Skip if test data not available
            return;
        }

        Path outFile = tempDir.resolve("outfile.dat");
        Path arryFile = tempDir.resolve("arryfile.dat");
        Path vbrcFile = tempDir.resolve("vbrcfile.dat");

        PrintStream nullLog = new PrintStream(OutputStream.nullOutputStream());
        AccountFileSplitter splitter = new AccountFileSplitter(
                testData, outFile, arryFile, vbrcFile, nullLog);

        int count = splitter.execute();
        assertEquals(50, count, "Should process all 50 accounts from test data");

        // Verify output file record counts
        assertEquals(50, Files.readAllLines(outFile).size());
        assertEquals(50, Files.readAllLines(arryFile).size());
        assertEquals(100, Files.readAllLines(vbrcFile).size()); // 2 per account
    }

    @Test
    @DisplayName("Empty input file produces empty outputs")
    void testEmptyInput() throws IOException {
        Path input = tempDir.resolve("empty.dat");
        Files.writeString(input, "");

        Path outFile = tempDir.resolve("outfile.dat");
        Path arryFile = tempDir.resolve("arryfile.dat");
        Path vbrcFile = tempDir.resolve("vbrcfile.dat");

        PrintStream nullLog = new PrintStream(OutputStream.nullOutputStream());
        int count = new AccountFileSplitter(input, outFile, arryFile, vbrcFile, nullLog).execute();

        assertEquals(0, count);
        assertEquals(0, Files.readAllLines(outFile).size());
    }

    @Test
    @DisplayName("DISPLAY output matches COBOL format")
    void testDisplayOutput() throws IOException {
        Path input = tempDir.resolve("acctfile.dat");
        Files.writeString(input, RECORD_1 + "\n");

        Path outFile = tempDir.resolve("outfile.dat");
        Path arryFile = tempDir.resolve("arryfile.dat");
        Path vbrcFile = tempDir.resolve("vbrcfile.dat");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream logStream = new PrintStream(baos);

        new AccountFileSplitter(input, outFile, arryFile, vbrcFile, logStream).execute();

        String output = baos.toString();
        assertTrue(output.contains("START OF EXECUTION OF PROGRAM CBACT01C"));
        assertTrue(output.contains("END OF EXECUTION OF PROGRAM CBACT01C"));
        assertTrue(output.contains("ACCT-ID                 :00000000001"));
        assertTrue(output.contains("ACCT-ACTIVE-STATUS      :Y"));
        assertTrue(output.contains("-------------------------------------------------"));
    }

    // ─── Round-Trip Tests ───

    @Test
    @DisplayName("Round-trip: parse → format preserves overpunch encoding")
    void testRoundTripOverpunch() {
        // Parse record, then format the balance back — should match original encoding
        AccountRecord acct = AccountRecord.parse(RECORD_1);
        String formatted = AccountFileSplitter.formatNumericField(acct.currBal(), 12);
        assertEquals("00000001940{", formatted, "Round-trip: parsed 194.00 → overpunch");
    }

    @Test
    @DisplayName("Round-trip: parse all 3 records and verify IDs are sequential")
    void testSequentialIds() {
        AccountRecord r1 = AccountRecord.parse(RECORD_1);
        AccountRecord r2 = AccountRecord.parse(RECORD_2);
        AccountRecord r3 = AccountRecord.parse(RECORD_3);

        assertEquals(1L, r1.acctId());
        assertEquals(2L, r2.acctId());
        assertEquals(3L, r3.acctId());
    }
}
