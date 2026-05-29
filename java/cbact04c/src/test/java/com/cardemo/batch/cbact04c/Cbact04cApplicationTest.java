package com.cardemo.batch.cbact04c;

import com.cardemo.batch.cbact04c.io.*;
import com.cardemo.batch.cbact04c.model.*;
import com.cardemo.batch.cbact04c.service.InterestCalculatorService;
import com.cardemo.batch.cbact04c.service.TimestampFormatter;

import org.junit.jupiter.api.*;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class Cbact04cApplicationTest {

    // =====================================================================
    // CobolFieldParser Tests
    // =====================================================================

    @Nested
    class CobolFieldParserTests {

        @Test
        void testDecodeOverpunchPositiveZero() {
            int[] result = CobolFieldParser.decodeOverpunch('{');
            assertEquals(0, result[0]);
            assertEquals(0, result[1]); // positive
        }

        @Test
        void testDecodeOverpunchPositiveDigits() {
            assertEquals(1, CobolFieldParser.decodeOverpunch('A')[0]);
            assertEquals(0, CobolFieldParser.decodeOverpunch('A')[1]);
            assertEquals(5, CobolFieldParser.decodeOverpunch('E')[0]);
            assertEquals(9, CobolFieldParser.decodeOverpunch('I')[0]);
        }

        @Test
        void testDecodeOverpunchNegativeZero() {
            int[] result = CobolFieldParser.decodeOverpunch('}');
            assertEquals(0, result[0]);
            assertEquals(1, result[1]); // negative
        }

        @Test
        void testDecodeOverpunchNegativeDigits() {
            assertEquals(1, CobolFieldParser.decodeOverpunch('J')[0]);
            assertEquals(1, CobolFieldParser.decodeOverpunch('J')[1]);
            assertEquals(5, CobolFieldParser.decodeOverpunch('N')[0]);
            assertEquals(1, CobolFieldParser.decodeOverpunch('N')[1]);
            assertEquals(9, CobolFieldParser.decodeOverpunch('R')[0]);
            assertEquals(1, CobolFieldParser.decodeOverpunch('R')[1]);
        }

        @Test
        void testDecodeOverpunchAllPositive() {
            char[] chars = {'{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
            for (int i = 0; i < chars.length; i++) {
                int[] result = CobolFieldParser.decodeOverpunch(chars[i]);
                assertEquals(i, result[0], "Digit for '" + chars[i] + "'");
                assertEquals(0, result[1], "Sign for '" + chars[i] + "'");
            }
        }

        @Test
        void testDecodeOverpunchAllNegative() {
            char[] chars = {'}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R'};
            for (int i = 0; i < chars.length; i++) {
                int[] result = CobolFieldParser.decodeOverpunch(chars[i]);
                assertEquals(i, result[0], "Digit for '" + chars[i] + "'");
                assertEquals(1, result[1], "Sign for '" + chars[i] + "'");
            }
        }

        @Test
        void testDecodeOverpunchInvalidChar() {
            assertThrows(IllegalArgumentException.class, () -> CobolFieldParser.decodeOverpunch('X'));
        }

        @Test
        void testEncodeOverpunchPositive() {
            assertEquals('{', CobolFieldParser.encodeOverpunch(0, false));
            assertEquals('A', CobolFieldParser.encodeOverpunch(1, false));
            assertEquals('I', CobolFieldParser.encodeOverpunch(9, false));
        }

        @Test
        void testEncodeOverpunchNegative() {
            assertEquals('}', CobolFieldParser.encodeOverpunch(0, true));
            assertEquals('J', CobolFieldParser.encodeOverpunch(1, true));
            assertEquals('R', CobolFieldParser.encodeOverpunch(9, true));
        }

        @Test
        void testEncodeOverpunchInvalidDigit() {
            assertThrows(IllegalArgumentException.class, () -> CobolFieldParser.encodeOverpunch(10, false));
        }

        @Test
        void testParseSignedDecimalPositiveZero() {
            // "0000000000{" = +0.00 (PIC S9(09)V99)
            BigDecimal result = CobolFieldParser.parseSignedDecimal("0000000000{", 2);
            assertEquals(new BigDecimal("0.00"), result);
        }

        @Test
        void testParseSignedDecimalPositiveValue() {
            // "00000001940{" = +19400 -> 194.00 (PIC S9(10)V99)
            BigDecimal result = CobolFieldParser.parseSignedDecimal("00000001940{", 2);
            assertEquals(new BigDecimal("194.00"), result);
        }

        @Test
        void testParseSignedDecimalWithOverpunchE() {
            // "00000020200{" but let's test with 'E' ending
            // "0000002020E" = 20205 -> 202.05
            BigDecimal result = CobolFieldParser.parseSignedDecimal("0000002020E", 2);
            assertEquals(new BigDecimal("202.05"), result);
        }

        @Test
        void testParseSignedDecimalNegativeValue() {
            // "00000001940}" = -19400 -> -194.00
            BigDecimal result = CobolFieldParser.parseSignedDecimal("00000001940}", 2);
            assertEquals(new BigDecimal("-194.00"), result);
        }

        @Test
        void testParseSignedDecimalNegativeWithOverpunch() {
            // "0000000150J" = -1501 -> -15.01
            BigDecimal result = CobolFieldParser.parseSignedDecimal("0000000150J", 2);
            assertEquals(new BigDecimal("-15.01"), result);
        }

        @Test
        void testParseSignedDecimalFromSampleData() {
            // From acctdata.txt: "00000001940{" = $194.00
            BigDecimal bal = CobolFieldParser.parseSignedDecimal("00000001940{", 2);
            assertEquals(new BigDecimal("194.00"), bal);

            // "00000020200{" PIC S9(10)V99 = $2020.00
            BigDecimal limit = CobolFieldParser.parseSignedDecimal("00000020200{", 2);
            assertEquals(new BigDecimal("2020.00"), limit);
        }

        @Test
        void testParseSignedDecimalRateField() {
            // From discgrp.txt: "00150{" PIC S9(04)V99 = 15.00
            BigDecimal rate = CobolFieldParser.parseSignedDecimal("00150{", 2);
            assertEquals(new BigDecimal("15.00"), rate);
        }

        @Test
        void testParseSignedDecimalZeroRate() {
            // "00000{" = 0.00
            BigDecimal rate = CobolFieldParser.parseSignedDecimal("00000{", 2);
            assertEquals(new BigDecimal("0.00"), rate);
        }

        @Test
        void testParseSignedDecimalHighRate() {
            // "00250{" PIC S9(04)V99 = 25.00
            BigDecimal rate = CobolFieldParser.parseSignedDecimal("00250{", 2);
            assertEquals(new BigDecimal("25.00"), rate);
        }

        @Test
        void testParseSignedDecimalNullAndBlank() {
            assertEquals(BigDecimal.ZERO, CobolFieldParser.parseSignedDecimal(null, 2));
            assertEquals(BigDecimal.ZERO, CobolFieldParser.parseSignedDecimal("", 2));
            assertEquals(BigDecimal.ZERO, CobolFieldParser.parseSignedDecimal("   ", 2));
        }

        @Test
        void testExtractField() {
            String line = "00000000001Y00000001940{";
            assertEquals("00000000001", CobolFieldParser.extractField(line, 0, 11));
            assertEquals("Y", CobolFieldParser.extractField(line, 11, 1));
            assertEquals("00000001940{", CobolFieldParser.extractField(line, 12, 12));
        }

        @Test
        void testExtractFieldOutOfRange() {
            assertEquals("", CobolFieldParser.extractField("abc", 10, 5));
            assertEquals("c", CobolFieldParser.extractField("abc", 2, 5));
        }

        @Test
        void testParseUnsignedNumeric() {
            assertEquals(11, CobolFieldParser.parseUnsignedNumeric("00000000011"));
            assertEquals(0, CobolFieldParser.parseUnsignedNumeric("0000"));
            assertEquals(50, CobolFieldParser.parseUnsignedNumeric("00000000050"));
        }

        @Test
        void testRoundTripEncodeDecode() {
            for (int digit = 0; digit <= 9; digit++) {
                char posChar = CobolFieldParser.encodeOverpunch(digit, false);
                int[] decoded = CobolFieldParser.decodeOverpunch(posChar);
                assertEquals(digit, decoded[0]);
                assertEquals(0, decoded[1]);

                char negChar = CobolFieldParser.encodeOverpunch(digit, true);
                decoded = CobolFieldParser.decodeOverpunch(negChar);
                assertEquals(digit, decoded[0]);
                assertEquals(1, decoded[1]);
            }
        }
    }

    // =====================================================================
    // TranCatBalFileReader Tests
    // =====================================================================

    @Nested
    class TranCatBalFileReaderTests {

        @Test
        void testParseSampleLine() {
            // From tcatbal.txt: "000000000010100010000000000{0000000000000000000000"
            String line = "000000000010100010000000000{0000000000000000000000";
            TranCatBalRecord rec = TranCatBalFileReader.parseLine(line);
            assertEquals("00000000001", rec.getAccountId());
            assertEquals("01", rec.getTypeCode());
            assertEquals(1, rec.getCategoryCode());
            assertEquals(new BigDecimal("0.00"), rec.getBalance());
        }

        @Test
        void testReadFromFile(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("tcatbal.txt");
            Files.writeString(file,
                    "000000000010100010000000000{0000000000000000000000\n" +
                    "000000000020100010000000000{0000000000000000000000\n");
            List<TranCatBalRecord> records = new TranCatBalFileReader(file).readAll();
            assertEquals(2, records.size());
            assertEquals("00000000001", records.get(0).getAccountId());
            assertEquals("00000000002", records.get(1).getAccountId());
        }

        @Test
        void testCompositeKey() {
            TranCatBalRecord rec = new TranCatBalRecord("00000000001", "01", 1, BigDecimal.ZERO);
            assertEquals("00000000001010001", rec.getCompositeKey());
        }
    }

    // =====================================================================
    // CardXrefFileReader Tests
    // =====================================================================

    @Nested
    class CardXrefFileReaderTests {

        @Test
        void testParseSampleLine() {
            // From cardxref.txt: "050002445376574000000005000000000050"
            // Padded to 50 chars: card(16) + cust(9) + acct(11) + filler(14)
            String line = "050002445376574000000005000000000050              ";
            CardXrefRecord rec = CardXrefFileReader.parseLine(line);
            assertEquals("0500024453765740", rec.getCardNumber());
            assertEquals("000000050", rec.getCustomerId());
            assertEquals("00000000050", rec.getAccountId());
        }

        @Test
        void testReadByAccountId(@TempDir Path tempDir) throws IOException {
            Path file = tempDir.resolve("xref.txt");
            Files.writeString(file,
                    "050002445376574000000005000000000050              \n" +
                    "068358619817151600000002700000000027              \n");
            Map<String, CardXrefRecord> map = new CardXrefFileReader(file).readAllByAccountId();
            assertEquals(2, map.size());
            assertNotNull(map.get("00000000050"));
            assertEquals("0500024453765740", map.get("00000000050").getCardNumber());
        }
    }

    // =====================================================================
    // DisclosureGroupFileReader Tests
    // =====================================================================

    @Nested
    class DisclosureGroupFileReaderTests {

        @Test
        void testParseSampleLine() {
            // From discgrp.txt: "A00000000001000100150{0000000000000000000000000000"
            String line = "A00000000001000100150{0000000000000000000000000000";
            DisclosureGroupRecord rec = DisclosureGroupFileReader.parseLine(line);
            assertEquals("A000000000", rec.getAccountGroupId());
            assertEquals("01", rec.getTranTypeCode());
            assertEquals(1, rec.getTranCatCode());
            assertEquals(new BigDecimal("15.00"), rec.getInterestRate());
        }

        @Test
        void testParseZeroRate() {
            // "A00000000002000100000{0000000000000000000000000000"
            String line = "A00000000002000100000{0000000000000000000000000000";
            DisclosureGroupRecord rec = DisclosureGroupFileReader.parseLine(line);
            assertEquals("A000000000", rec.getAccountGroupId());
            assertEquals("02", rec.getTranTypeCode());
            assertEquals(1, rec.getTranCatCode());
            assertEquals(new BigDecimal("0.00"), rec.getInterestRate());
        }

        @Test
        void testLookupWithFallbackToDefault() {
            Map<String, DisclosureGroupRecord> map = new LinkedHashMap<>();

            DisclosureGroupRecord specific = new DisclosureGroupRecord("A000000000", "01", 1, new BigDecimal("1.50"));
            map.put(specific.getCompositeKey(), specific);

            DisclosureGroupRecord defaultRec = new DisclosureGroupRecord("DEFAULT", "01", 1, new BigDecimal("0.75"));
            map.put(defaultRec.getCompositeKey(), defaultRec);

            // Specific lookup succeeds
            DisclosureGroupRecord found = DisclosureGroupFileReader.lookup(map, "A000000000", "01", 1);
            assertNotNull(found);
            assertEquals(new BigDecimal("1.50"), found.getInterestRate());

            // Unknown group falls back to DEFAULT
            DisclosureGroupRecord fallback = DisclosureGroupFileReader.lookup(map, "UNKNOWN", "01", 1);
            assertNotNull(fallback);
            assertEquals(new BigDecimal("0.75"), fallback.getInterestRate());
        }

        @Test
        void testLookupNoDefaultReturnsNull() {
            Map<String, DisclosureGroupRecord> map = new LinkedHashMap<>();
            DisclosureGroupRecord specific = new DisclosureGroupRecord("A000000000", "01", 1, new BigDecimal("1.50"));
            map.put(specific.getCompositeKey(), specific);

            DisclosureGroupRecord result = DisclosureGroupFileReader.lookup(map, "UNKNOWN", "02", 99);
            assertNull(result);
        }
    }

    // =====================================================================
    // AccountFileReader Tests
    // =====================================================================

    @Nested
    class AccountFileReaderTests {

        @Test
        void testParseSampleLine() {
            // From acctdata.txt first line (300 chars):
            // 00000000001Y00000001940{00000020200{00000010200{2014-11-202025-05-202025-05-2000000000000{00000000000{A000000000 + filler
            String line = "00000000001Y00000001940{00000020200{00000010200{2014-11-202025-05-202025-05-2000000000000{00000000000{A000000000";
            // Pad to 300 chars
            line = String.format("%-300s", line);

            AccountRecord rec = AccountFileReader.parseLine(line);
            assertEquals("00000000001", rec.getAccountId());
            assertEquals("Y", rec.getActiveStatus());
            assertEquals(new BigDecimal("194.00"), rec.getCurrentBalance());
            assertEquals(new BigDecimal("2020.00"), rec.getCreditLimit());
            assertEquals(new BigDecimal("1020.00"), rec.getCashCreditLimit());
            assertEquals("2014-11-20", rec.getOpenDate());
            assertEquals("2025-05-20", rec.getExpirationDate());
            assertEquals("2025-05-20", rec.getReissueDate());
            assertEquals(new BigDecimal("0.00"), rec.getCurrentCycleCredit());
            assertEquals(new BigDecimal("0.00"), rec.getCurrentCycleDebit());
            // In the sample data, A000000000 lands in ADDR-ZIP per copybook layout
            assertEquals("A000000000", rec.getAddressZip());
            // GROUP-ID at copybook position 112-121 is spaces in sample data
            assertEquals("          ", rec.getGroupId());
        }

        @Test
        void testRewriteAll(@TempDir Path tempDir) throws IOException {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord rec = new AccountRecord();
            rec.setAccountId("00000000001");
            rec.setActiveStatus("Y");
            rec.setCurrentBalance(new BigDecimal("200.00"));
            rec.setCreditLimit(new BigDecimal("500.00"));
            rec.setCashCreditLimit(new BigDecimal("100.00"));
            rec.setOpenDate("2014-11-20");
            rec.setExpirationDate("2025-05-20");
            rec.setReissueDate("2025-05-20");
            rec.setCurrentCycleCredit(BigDecimal.ZERO);
            rec.setCurrentCycleDebit(BigDecimal.ZERO);
            rec.setAddressZip("12345");
            rec.setGroupId("A000000000");
            accounts.put(rec.getAccountId(), rec);

            Path output = tempDir.resolve("acctdata_out.txt");
            AccountFileReader.rewriteAll(output, accounts);

            List<String> lines = Files.readAllLines(output);
            assertEquals(1, lines.size());
            assertEquals(300, lines.get(0).length());
            assertTrue(lines.get(0).startsWith("00000000001Y"));
        }
    }

    // =====================================================================
    // TransactionRecord Tests
    // =====================================================================

    @Nested
    class TransactionRecordTests {

        @Test
        void testToFixedWidth() {
            TransactionRecord tran = new TransactionRecord();
            tran.setTransactionId("2022-06-10000001");
            tran.setTypeCode("01");
            tran.setCategoryCode(5);
            tran.setSource("System");
            tran.setDescription("Int. for a/c 00000000001");
            tran.setAmount(new BigDecimal("2.43"));
            tran.setMerchantId("000000000");
            tran.setMerchantName("");
            tran.setMerchantCity("");
            tran.setMerchantZip("");
            tran.setCardNumber("0500024453765740");
            tran.setOrigTimestamp("2022-06-10-14.30.45.120000");
            tran.setProcTimestamp("2022-06-10-14.30.45.120000");

            String line = tran.toFixedWidth();
            assertEquals(350, line.length());
            assertTrue(line.startsWith("2022-06-10000001"));
            assertTrue(line.contains("01"));
            assertTrue(line.contains("System"));
        }

        @Test
        void testTransactionIdFormat() {
            String parmDate = "2022-06-10";
            int suffix = 1;
            String tranId = parmDate + String.format("%06d", suffix);
            assertEquals("2022-06-10000001", tranId);
            assertEquals(16, tranId.length());
        }
    }

    // =====================================================================
    // TimestampFormatter Tests
    // =====================================================================

    @Nested
    class TimestampFormatterTests {

        @Test
        void testFormatDb2Timestamp() {
            LocalDateTime dt = LocalDateTime.of(2022, 6, 10, 14, 30, 45, 120_000_000);
            String ts = TimestampFormatter.formatDb2Timestamp(dt);
            assertEquals("2022-06-10-14.30.45.120000", ts);
        }

        @Test
        void testFormatDb2TimestampMidnight() {
            LocalDateTime dt = LocalDateTime.of(2022, 1, 1, 0, 0, 0, 0);
            String ts = TimestampFormatter.formatDb2Timestamp(dt);
            assertEquals("2022-01-01-00.00.00.000000", ts);
        }

        @Test
        void testTimestampLength() {
            String ts = TimestampFormatter.currentDb2Timestamp();
            assertEquals(26, ts.length());
        }

        @Test
        void testTimestampFormat() {
            String ts = TimestampFormatter.currentDb2Timestamp();
            // YYYY-MM-DD-HH.MM.SS.HH0000
            assertTrue(ts.matches("\\d{4}-\\d{2}-\\d{2}-\\d{2}\\.\\d{2}\\.\\d{2}\\.\\d{6}"));
        }
    }

    // =====================================================================
    // Interest Calculation Tests
    // =====================================================================

    @Nested
    class InterestCalculationTests {

        @Test
        void testComputeInterestBasic() {
            // Balance 1000.00, rate 1.50% -> (1000 * 1.50) / 1200 = 1.25
            BigDecimal interest = InterestCalculatorService.computeInterest(
                    new BigDecimal("1000.00"), new BigDecimal("1.50"));
            assertEquals(new BigDecimal("1.25"), interest);
        }

        @Test
        void testComputeInterestZeroBalance() {
            BigDecimal interest = InterestCalculatorService.computeInterest(
                    BigDecimal.ZERO, new BigDecimal("1.50"));
            assertEquals(new BigDecimal("0.00"), interest);
        }

        @Test
        void testComputeInterestZeroRate() {
            BigDecimal interest = InterestCalculatorService.computeInterest(
                    new BigDecimal("5000.00"), BigDecimal.ZERO);
            assertEquals(new BigDecimal("0.00"), interest);
        }

        @Test
        void testComputeInterestNegativeBalance() {
            // Negative balance (-500), rate 2.00% -> (-500 * 2.00) / 1200 = -0.83
            BigDecimal interest = InterestCalculatorService.computeInterest(
                    new BigDecimal("-500.00"), new BigDecimal("2.00"));
            assertEquals(new BigDecimal("-0.83"), interest);
        }

        @Test
        void testComputeInterestLargeBalance() {
            // Balance $100,000, rate 2.50% -> (100000 * 2.50) / 1200 = 208.33
            BigDecimal interest = InterestCalculatorService.computeInterest(
                    new BigDecimal("100000.00"), new BigDecimal("2.50"));
            assertEquals(new BigDecimal("208.33"), interest);
        }

        @Test
        void testComputeInterestSmallValues() {
            // Balance $1.00, rate 0.01% -> (1.00 * 0.01) / 1200 = 0.00 (rounded)
            BigDecimal interest = InterestCalculatorService.computeInterest(
                    new BigDecimal("1.00"), new BigDecimal("0.01"));
            assertEquals(new BigDecimal("0.00"), interest);
        }

        @Test
        void testComputeInterestFormula() {
            // Verify formula: monthlyInterest = (categoryBalance * interestRate) / 1200
            BigDecimal balance = new BigDecimal("5000.00");
            BigDecimal rate = new BigDecimal("1.50");
            BigDecimal expected = balance.multiply(rate)
                    .divide(new BigDecimal("1200"), 2, java.math.RoundingMode.DOWN);
            BigDecimal actual = InterestCalculatorService.computeInterest(balance, rate);
            assertEquals(expected, actual);
        }
    }

    // =====================================================================
    // Account Update Tests
    // =====================================================================

    @Nested
    class AccountUpdateTests {

        @Test
        void testAccountBalanceAdjustment() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord acct = new AccountRecord();
            acct.setAccountId("00000000001");
            acct.setActiveStatus("Y");
            acct.setCurrentBalance(new BigDecimal("194.00"));
            acct.setCreditLimit(new BigDecimal("202.00"));
            acct.setCashCreditLimit(new BigDecimal("102.00"));
            acct.setOpenDate("2014-11-20");
            acct.setExpirationDate("2025-05-20");
            acct.setReissueDate("2025-05-20");
            acct.setCurrentCycleCredit(new BigDecimal("50.00"));
            acct.setCurrentCycleDebit(new BigDecimal("30.00"));
            acct.setAddressZip("12345");
            acct.setGroupId("A000000000");
            accounts.put(acct.getAccountId(), acct);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000001", new CardXrefRecord("1234567890123456", "000000001", "00000000001"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            DisclosureGroupRecord dg = new DisclosureGroupRecord("A000000000", "01", 1, new BigDecimal("1.50"));
            discgrp.put(dg.getCompositeKey(), dg);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 1, new BigDecimal("1000.00")));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            // Interest = (1000 * 1.50) / 1200 = 1.25
            // New balance = 194.00 + 1.25 = 195.25
            assertEquals(new BigDecimal("195.25"), acct.getCurrentBalance());
            assertEquals(BigDecimal.ZERO, acct.getCurrentCycleCredit());
            assertEquals(BigDecimal.ZERO, acct.getCurrentCycleDebit());
        }

        @Test
        void testAccountCycleReset() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord acct = new AccountRecord();
            acct.setAccountId("00000000001");
            acct.setActiveStatus("Y");
            acct.setCurrentBalance(new BigDecimal("500.00"));
            acct.setCreditLimit(new BigDecimal("1000.00"));
            acct.setCashCreditLimit(new BigDecimal("500.00"));
            acct.setOpenDate("2020-01-01");
            acct.setExpirationDate("2025-12-31");
            acct.setReissueDate("2025-06-01");
            acct.setCurrentCycleCredit(new BigDecimal("100.00"));
            acct.setCurrentCycleDebit(new BigDecimal("75.00"));
            acct.setAddressZip("54321");
            acct.setGroupId("A000000000");
            accounts.put(acct.getAccountId(), acct);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000001", new CardXrefRecord("9876543210123456", "000000001", "00000000001"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            DisclosureGroupRecord dg = new DisclosureGroupRecord("A000000000", "01", 1, BigDecimal.ZERO);
            discgrp.put(dg.getCompositeKey(), dg);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 1, new BigDecimal("200.00")));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            // Zero rate means no interest added, balance unchanged
            assertEquals(new BigDecimal("500.00"), acct.getCurrentBalance());
            // But cycle credits/debits should still be zeroed
            assertEquals(BigDecimal.ZERO, acct.getCurrentCycleCredit());
            assertEquals(BigDecimal.ZERO, acct.getCurrentCycleDebit());
        }
    }

    // =====================================================================
    // Default Rate Fallback Tests
    // =====================================================================

    @Nested
    class DefaultRateFallbackTests {

        @Test
        void testFallbackToDefaultGroup() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord acct = new AccountRecord();
            acct.setAccountId("00000000001");
            acct.setActiveStatus("Y");
            acct.setCurrentBalance(new BigDecimal("1000.00"));
            acct.setCreditLimit(new BigDecimal("5000.00"));
            acct.setCashCreditLimit(new BigDecimal("2000.00"));
            acct.setOpenDate("2020-01-01");
            acct.setExpirationDate("2025-12-31");
            acct.setReissueDate("2025-06-01");
            acct.setCurrentCycleCredit(BigDecimal.ZERO);
            acct.setCurrentCycleDebit(BigDecimal.ZERO);
            acct.setAddressZip("00000");
            acct.setGroupId("ZZZZZZZZZZ"); // Group not in discgrp
            accounts.put(acct.getAccountId(), acct);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000001", new CardXrefRecord("1111222233334444", "000000001", "00000000001"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            // Only DEFAULT group
            DisclosureGroupRecord defaultRec = new DisclosureGroupRecord("DEFAULT", "01", 1, new BigDecimal("0.50"));
            discgrp.put(defaultRec.getCompositeKey(), defaultRec);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 1, new BigDecimal("2400.00")));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            // Interest = (2400 * 0.50) / 1200 = 1.00
            assertEquals(new BigDecimal("1001.00"), acct.getCurrentBalance());
            assertEquals(1, service.getOutputTransactions().size());
        }
    }

    // =====================================================================
    // Transaction Record Generation Tests
    // =====================================================================

    @Nested
    class TransactionGenerationTests {

        @Test
        void testTransactionFieldValues() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord acct = new AccountRecord();
            acct.setAccountId("00000000042");
            acct.setActiveStatus("Y");
            acct.setCurrentBalance(new BigDecimal("5000.00"));
            acct.setCreditLimit(new BigDecimal("10000.00"));
            acct.setCashCreditLimit(new BigDecimal("5000.00"));
            acct.setOpenDate("2020-01-01");
            acct.setExpirationDate("2025-12-31");
            acct.setReissueDate("2025-06-01");
            acct.setCurrentCycleCredit(BigDecimal.ZERO);
            acct.setCurrentCycleDebit(BigDecimal.ZERO);
            acct.setAddressZip("10001");
            acct.setGroupId("A000000000");
            accounts.put(acct.getAccountId(), acct);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000042", new CardXrefRecord("5555666677778888", "000000042", "00000000042"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            DisclosureGroupRecord dg = new DisclosureGroupRecord("A000000000", "01", 1, new BigDecimal("2.00"));
            discgrp.put(dg.getCompositeKey(), dg);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            tcatbal.add(new TranCatBalRecord("00000000042", "01", 1, new BigDecimal("6000.00")));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            List<TransactionRecord> txns = service.getOutputTransactions();
            assertEquals(1, txns.size());

            TransactionRecord tran = txns.get(0);
            assertEquals("2022-06-10000001", tran.getTransactionId());
            assertEquals("01", tran.getTypeCode());
            assertEquals(5, tran.getCategoryCode());
            assertEquals("System", tran.getSource());
            assertEquals("Int. for a/c 00000000042", tran.getDescription());
            assertEquals(new BigDecimal("10.00"), tran.getAmount()); // (6000*2)/1200=10
            assertEquals("5555666677778888", tran.getCardNumber());
            assertNotNull(tran.getOrigTimestamp());
            assertNotNull(tran.getProcTimestamp());
        }

        @Test
        void testMultipleTransactionsPerAccount() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord acct = new AccountRecord();
            acct.setAccountId("00000000001");
            acct.setActiveStatus("Y");
            acct.setCurrentBalance(new BigDecimal("100.00"));
            acct.setCreditLimit(new BigDecimal("1000.00"));
            acct.setCashCreditLimit(new BigDecimal("500.00"));
            acct.setOpenDate("2020-01-01");
            acct.setExpirationDate("2025-12-31");
            acct.setReissueDate("2025-06-01");
            acct.setCurrentCycleCredit(BigDecimal.ZERO);
            acct.setCurrentCycleDebit(BigDecimal.ZERO);
            acct.setAddressZip("00000");
            acct.setGroupId("A000000000");
            accounts.put(acct.getAccountId(), acct);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000001", new CardXrefRecord("1234567890123456", "000000001", "00000000001"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            DisclosureGroupRecord dg1 = new DisclosureGroupRecord("A000000000", "01", 1, new BigDecimal("1.50"));
            discgrp.put(dg1.getCompositeKey(), dg1);
            DisclosureGroupRecord dg2 = new DisclosureGroupRecord("A000000000", "01", 2, new BigDecimal("2.50"));
            discgrp.put(dg2.getCompositeKey(), dg2);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 1, new BigDecimal("1200.00")));
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 2, new BigDecimal("2400.00")));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            List<TransactionRecord> txns = service.getOutputTransactions();
            assertEquals(2, txns.size());
            assertEquals("2022-06-10000001", txns.get(0).getTransactionId());
            assertEquals("2022-06-10000002", txns.get(1).getTransactionId());

            // Interest1 = (1200 * 1.50) / 1200 = 1.50
            assertEquals(new BigDecimal("1.50"), txns.get(0).getAmount());
            // Interest2 = (2400 * 2.50) / 1200 = 5.00
            assertEquals(new BigDecimal("5.00"), txns.get(1).getAmount());

            // Account balance should be 100 + 1.50 + 5.00 = 106.50
            assertEquals(new BigDecimal("106.50"), acct.getCurrentBalance());
        }

        @Test
        void testSequentialTransactionIds() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();

            AccountRecord acct1 = createAccount("00000000001", "A000000000", "100.00");
            accounts.put(acct1.getAccountId(), acct1);
            AccountRecord acct2 = createAccount("00000000002", "A000000000", "200.00");
            accounts.put(acct2.getAccountId(), acct2);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000001", new CardXrefRecord("1111111111111111", "000000001", "00000000001"));
            xref.put("00000000002", new CardXrefRecord("2222222222222222", "000000002", "00000000002"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            DisclosureGroupRecord dg = new DisclosureGroupRecord("A000000000", "01", 1, new BigDecimal("1.50"));
            discgrp.put(dg.getCompositeKey(), dg);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 1, new BigDecimal("1200.00")));
            tcatbal.add(new TranCatBalRecord("00000000002", "01", 1, new BigDecimal("2400.00")));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            List<TransactionRecord> txns = service.getOutputTransactions();
            assertEquals(2, txns.size());
            assertEquals("2022-06-10000001", txns.get(0).getTransactionId());
            assertEquals("2022-06-10000002", txns.get(1).getTransactionId());
        }
    }

    // =====================================================================
    // Integration Test with Real Sample Data
    // =====================================================================

    @Nested
    class IntegrationTests {

        private static final String DATA_DIR = "../../app/data/ASCII/";

        @Test
        void testWithRealSampleData() throws IOException {
            Path tcatbalPath = Paths.get(DATA_DIR, "tcatbal.txt");
            Path xrefPath = Paths.get(DATA_DIR, "cardxref.txt");
            Path discgrpPath = Paths.get(DATA_DIR, "discgrp.txt");
            Path acctPath = Paths.get(DATA_DIR, "acctdata.txt");

            if (!Files.exists(tcatbalPath)) {
                // Try absolute path
                tcatbalPath = Paths.get("/home/ubuntu/repos/uc-legacy-modernization-cobol-to-java/app/data/ASCII/tcatbal.txt");
                xrefPath = Paths.get("/home/ubuntu/repos/uc-legacy-modernization-cobol-to-java/app/data/ASCII/cardxref.txt");
                discgrpPath = Paths.get("/home/ubuntu/repos/uc-legacy-modernization-cobol-to-java/app/data/ASCII/discgrp.txt");
                acctPath = Paths.get("/home/ubuntu/repos/uc-legacy-modernization-cobol-to-java/app/data/ASCII/acctdata.txt");
            }

            Assumptions.assumeTrue(Files.exists(tcatbalPath), "Sample data not found");

            List<TranCatBalRecord> tcatbalRecords = new TranCatBalFileReader(tcatbalPath).readAll();
            Map<String, CardXrefRecord> xrefByAcct = new CardXrefFileReader(xrefPath).readAllByAccountId();
            Map<String, DisclosureGroupRecord> discGroupByKey = new DisclosureGroupFileReader(discgrpPath).readAllByKey();
            Map<String, AccountRecord> accountsByKey = new AccountFileReader(acctPath).readAllByAccountId();

            assertEquals(50, tcatbalRecords.size());
            assertEquals(50, xrefByAcct.size());
            assertEquals(50, accountsByKey.size());
            assertTrue(discGroupByKey.size() > 0);

            // Verify first tcatbal record
            TranCatBalRecord firstTcatbal = tcatbalRecords.get(0);
            assertEquals("00000000001", firstTcatbal.getAccountId());
            assertEquals("01", firstTcatbal.getTypeCode());
            assertEquals(1, firstTcatbal.getCategoryCode());

            // Verify first account
            AccountRecord firstAcct = accountsByKey.get("00000000001");
            assertNotNull(firstAcct);
            assertEquals("Y", firstAcct.getActiveStatus());
            assertEquals(new BigDecimal("194.00"), firstAcct.getCurrentBalance());
            // In sample data, group ID (copybook pos 112-121) is spaces;
            // A000000000 is in ADDR-ZIP (copybook pos 102-111)
            assertEquals("A000000000", firstAcct.getAddressZip());

            // Verify disclosure group rate parsing
            DisclosureGroupRecord rate = DisclosureGroupFileReader.lookup(
                    discGroupByKey, "A000000000", "01", 1);
            assertNotNull(rate);
            assertEquals(new BigDecimal("15.00"), rate.getInterestRate());

            // All sample tcatbal balances are 0.00, so no interest transactions
            // would be generated (rate != 0 but balance * rate = 0).
            // We verify file reading and parsing only since sample GROUP-IDs
            // are in the ADDR-ZIP position per copybook layout.
        }

        @Test
        void testWithNonZeroBalances(@TempDir Path tempDir) throws IOException {
            // Create test data with non-zero balances
            Path tcatbalFile = tempDir.resolve("tcatbal.txt");
            Path xrefFile = tempDir.resolve("cardxref.txt");
            Path discgrpFile = tempDir.resolve("discgrp.txt");
            Path acctFile = tempDir.resolve("acctdata.txt");

            // tcatbal: balance 1200.00 -> PIC S9(09)V99 = "0000012000{" (11 chars)
            Files.writeString(tcatbalFile,
                    "000000000010100010000012000{0000000000000000000000\n" +
                    "000000000020100010000024000{0000000000000000000000\n");

            Files.writeString(xrefFile,
                    "123456789012345600000000100000000001              \n" +
                    "987654321098765400000000200000000002              \n");

            // Rate 15.00 (annual) for A000000000/01/0001 -> PIC S9(04)V99 = "00150{"
            Files.writeString(discgrpFile,
                    "A00000000001000100150{0000000000000000000000000000\n");

            // Account 1: bal 500.00, cycle credit 100.00, cycle debit 50.00
            // PIC S9(10)V99: 500.00->"00000005000{", 1000.00->"00000010000{", etc.
            String acct1 = String.format("%-300s", "00000000001Y00000005000{00000010000{00000005000{2020-01-012025-12-312025-06-0100000001000{00000000500{0000000000A000000000");
            // Account 2: bal 1000.00
            String acct2 = String.format("%-300s", "00000000002Y00000010000{00000020000{00000010000{2020-01-012025-12-312025-06-0100000002000{00000001500{0000000000A000000000");
            Files.writeString(acctFile, acct1 + "\n" + acct2 + "\n");

            List<TranCatBalRecord> tcatbalRecords = new TranCatBalFileReader(tcatbalFile).readAll();
            Map<String, CardXrefRecord> xrefByAcct = new CardXrefFileReader(xrefFile).readAllByAccountId();
            Map<String, DisclosureGroupRecord> discGroupByKey = new DisclosureGroupFileReader(discgrpFile).readAllByKey();
            Map<String, AccountRecord> accountsByKey = new AccountFileReader(acctFile).readAllByAccountId();

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbalRecords, xrefByAcct, discGroupByKey, accountsByKey);
            service.process();

            List<TransactionRecord> txns = service.getOutputTransactions();
            assertEquals(2, txns.size());

            // Account 1: interest = (1200.00 * 15.00) / 1200 = 15.00
            assertEquals(new BigDecimal("15.00"), txns.get(0).getAmount());
            // Account 2: interest = (2400.00 * 15.00) / 1200 = 30.00
            assertEquals(new BigDecimal("30.00"), txns.get(1).getAmount());

            // Verify account balances updated
            AccountRecord acctRec1 = accountsByKey.get("00000000001");
            AccountRecord acctRec2 = accountsByKey.get("00000000002");

            // Account 1: original bal 500.00 + interest 15.00 = 515.00
            assertEquals(new BigDecimal("515.00"), acctRec1.getCurrentBalance());
            assertEquals(BigDecimal.ZERO, acctRec1.getCurrentCycleCredit());
            assertEquals(BigDecimal.ZERO, acctRec1.getCurrentCycleDebit());

            // Account 2: original bal 1000.00 + interest 30.00 = 1030.00
            assertEquals(new BigDecimal("1030.00"), acctRec2.getCurrentBalance());
            assertEquals(BigDecimal.ZERO, acctRec2.getCurrentCycleCredit());
            assertEquals(BigDecimal.ZERO, acctRec2.getCurrentCycleDebit());
        }
    }

    // =====================================================================
    // Edge Case Tests
    // =====================================================================

    @Nested
    class EdgeCaseTests {

        @Test
        void testEmptyTcatbalFile() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            List<TranCatBalRecord> tcatbal = new ArrayList<>();

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            assertEquals(0, service.getOutputTransactions().size());
        }

        @Test
        void testAllZeroBalances() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord acct = createAccount("00000000001", "A000000000", "500.00");
            accounts.put(acct.getAccountId(), acct);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000001", new CardXrefRecord("1234567890123456", "000000001", "00000000001"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            DisclosureGroupRecord dg = new DisclosureGroupRecord("A000000000", "01", 1, new BigDecimal("1.50"));
            discgrp.put(dg.getCompositeKey(), dg);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 1, BigDecimal.ZERO));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            // Zero balance * any rate = 0 interest -> still not written since rate != 0 but interest = 0
            // The COBOL checks DIS-INT-RATE NOT = 0, then computes. But result could be 0.
            // Transaction is written even if interest is 0 when rate is non-zero.
            assertEquals(1, service.getOutputTransactions().size());
            assertEquals(new BigDecimal("0.00"), service.getOutputTransactions().get(0).getAmount());
        }

        @Test
        void testAllZeroRates() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord acct = createAccount("00000000001", "A000000000", "500.00");
            accounts.put(acct.getAccountId(), acct);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000001", new CardXrefRecord("1234567890123456", "000000001", "00000000001"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            DisclosureGroupRecord dg = new DisclosureGroupRecord("A000000000", "01", 1, BigDecimal.ZERO);
            discgrp.put(dg.getCompositeKey(), dg);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 1, new BigDecimal("5000.00")));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            // Zero rate -> no interest computation, no transaction
            assertEquals(0, service.getOutputTransactions().size());
            // Balance unchanged
            assertEquals(new BigDecimal("500.00"), acct.getCurrentBalance());
        }

        @Test
        void testNegativeBalanceWithPositiveRate() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord acct = createAccount("00000000001", "A000000000", "1000.00");
            accounts.put(acct.getAccountId(), acct);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000001", new CardXrefRecord("1234567890123456", "000000001", "00000000001"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            DisclosureGroupRecord dg = new DisclosureGroupRecord("A000000000", "01", 1, new BigDecimal("2.00"));
            discgrp.put(dg.getCompositeKey(), dg);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 1, new BigDecimal("-600.00")));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            // Interest = (-600 * 2.00) / 1200 = -1.00
            assertEquals(1, service.getOutputTransactions().size());
            assertEquals(new BigDecimal("-1.00"), service.getOutputTransactions().get(0).getAmount());
            // Balance = 1000 + (-1.00) = 999.00
            assertEquals(new BigDecimal("999.00"), acct.getCurrentBalance());
        }

        @Test
        void testTransactionFileWriteAndRead(@TempDir Path tempDir) throws IOException {
            TransactionRecord tran = new TransactionRecord();
            tran.setTransactionId("2022-06-10000001");
            tran.setTypeCode("01");
            tran.setCategoryCode(5);
            tran.setSource("System");
            tran.setDescription("Int. for a/c 00000000001");
            tran.setAmount(new BigDecimal("1.25"));
            tran.setMerchantId("000000000");
            tran.setMerchantName("");
            tran.setMerchantCity("");
            tran.setMerchantZip("");
            tran.setCardNumber("1234567890123456");
            tran.setOrigTimestamp("2022-06-10-14.30.45.120000");
            tran.setProcTimestamp("2022-06-10-14.30.45.120000");

            Path outFile = tempDir.resolve("transact.txt");
            new TransactionFileWriter(outFile).writeAll(List.of(tran));

            List<String> lines = Files.readAllLines(outFile);
            assertEquals(1, lines.size());
            assertEquals(350, lines.get(0).length());
            assertTrue(lines.get(0).startsWith("2022-06-10000001"));
        }

        @Test
        void testMultipleAccountsWithAccountChange() {
            Map<String, AccountRecord> accounts = new LinkedHashMap<>();
            AccountRecord acct1 = createAccount("00000000001", "A000000000", "100.00");
            AccountRecord acct2 = createAccount("00000000002", "A000000000", "200.00");
            accounts.put(acct1.getAccountId(), acct1);
            accounts.put(acct2.getAccountId(), acct2);

            Map<String, CardXrefRecord> xref = new LinkedHashMap<>();
            xref.put("00000000001", new CardXrefRecord("1111111111111111", "000000001", "00000000001"));
            xref.put("00000000002", new CardXrefRecord("2222222222222222", "000000002", "00000000002"));

            Map<String, DisclosureGroupRecord> discgrp = new LinkedHashMap<>();
            DisclosureGroupRecord dg = new DisclosureGroupRecord("A000000000", "01", 1, new BigDecimal("12.00"));
            discgrp.put(dg.getCompositeKey(), dg);

            List<TranCatBalRecord> tcatbal = new ArrayList<>();
            // Account 1: balance 1200 -> interest = (1200*12)/1200 = 12.00
            tcatbal.add(new TranCatBalRecord("00000000001", "01", 1, new BigDecimal("1200.00")));
            // Account 2: balance 2400 -> interest = (2400*12)/1200 = 24.00
            tcatbal.add(new TranCatBalRecord("00000000002", "01", 1, new BigDecimal("2400.00")));

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbal, xref, discgrp, accounts);
            service.process();

            // Verify both accounts updated
            assertEquals(new BigDecimal("112.00"), acct1.getCurrentBalance()); // 100 + 12
            assertEquals(new BigDecimal("224.00"), acct2.getCurrentBalance()); // 200 + 24
            assertEquals(2, service.getOutputTransactions().size());
        }
    }

    // =====================================================================
    // TransactionFileWriter Tests
    // =====================================================================

    @Nested
    class TransactionFileWriterTests {

        @Test
        void testWriteEmptyList(@TempDir Path tempDir) throws IOException {
            Path outFile = tempDir.resolve("empty.txt");
            new TransactionFileWriter(outFile).writeAll(List.of());
            assertEquals(0, Files.readAllLines(outFile).size());
        }

        @Test
        void testWriteMultipleRecords(@TempDir Path tempDir) throws IOException {
            List<TransactionRecord> txns = new ArrayList<>();
            for (int i = 1; i <= 3; i++) {
                TransactionRecord t = new TransactionRecord();
                t.setTransactionId("2022-06-10" + String.format("%06d", i));
                t.setTypeCode("01");
                t.setCategoryCode(5);
                t.setSource("System");
                t.setDescription("Int. for a/c " + String.format("%011d", i));
                t.setAmount(new BigDecimal(i + ".00"));
                t.setMerchantId("000000000");
                t.setMerchantName("");
                t.setMerchantCity("");
                t.setMerchantZip("");
                t.setCardNumber(String.format("%016d", i));
                t.setOrigTimestamp("2022-06-10-10.00.00.000000");
                t.setProcTimestamp("2022-06-10-10.00.00.000000");
                txns.add(t);
            }

            Path outFile = tempDir.resolve("transact.txt");
            new TransactionFileWriter(outFile).writeAll(txns);

            List<String> lines = Files.readAllLines(outFile);
            assertEquals(3, lines.size());
            for (String line : lines) {
                assertEquals(350, line.length());
            }
        }
    }

    // =====================================================================
    // Helper Methods
    // =====================================================================

    private static AccountRecord createAccount(String acctId, String groupId, String balance) {
        AccountRecord acct = new AccountRecord();
        acct.setAccountId(acctId);
        acct.setActiveStatus("Y");
        acct.setCurrentBalance(new BigDecimal(balance));
        acct.setCreditLimit(new BigDecimal("10000.00"));
        acct.setCashCreditLimit(new BigDecimal("5000.00"));
        acct.setOpenDate("2020-01-01");
        acct.setExpirationDate("2025-12-31");
        acct.setReissueDate("2025-06-01");
        acct.setCurrentCycleCredit(BigDecimal.ZERO);
        acct.setCurrentCycleDebit(BigDecimal.ZERO);
        acct.setAddressZip("00000");
        acct.setGroupId(groupId);
        return acct;
    }

    // =====================================================================
    // Security Vulnerability Tests
    // =====================================================================

    @Nested
    class SecurityTests {

        @TempDir
        Path securityTempDir;

        // --- CVE: Path Traversal (CWE-22) ---

        @Test
        void testPathTraversalInputRejected() {
            // Set allowed base to a confined directory for testing
            Path savedBase = Cbact04cApplication.allowedBase;
            try {
                Cbact04cApplication.allowedBase = securityTempDir;
                // Attempting to escape securityTempDir via ../ should be rejected
                assertThrows(IllegalArgumentException.class, () ->
                        Cbact04cApplication.validateFilePath(
                                securityTempDir.resolve("../etc/passwd").toString(), false));
            } finally {
                Cbact04cApplication.allowedBase = savedBase;
            }
        }

        @Test
        void testPathTraversalOutputRejected() {
            // Output paths must also be confined to allowed base
            Path savedBase = Cbact04cApplication.allowedBase;
            try {
                Cbact04cApplication.allowedBase = securityTempDir;
                assertThrows(IllegalArgumentException.class, () ->
                        Cbact04cApplication.validateFilePath(
                                securityTempDir.resolve("../../tmp/evil.txt").toString(), true));
            } finally {
                Cbact04cApplication.allowedBase = savedBase;
            }
        }

        @Test
        void testPathWithNullByteRejected() {
            assertThrows(IllegalArgumentException.class, () ->
                    Cbact04cApplication.validateFilePath("file\u0000.txt", false));
        }

        @Test
        void testBlankPathRejected() {
            assertThrows(IllegalArgumentException.class, () ->
                    Cbact04cApplication.validateFilePath("", false));
        }

        @Test
        void testNullPathRejected() {
            assertThrows(IllegalArgumentException.class, () ->
                    Cbact04cApplication.validateFilePath(null, false));
        }

        @Test
        void testValidInputPathAccepted() throws IOException {
            Path savedBase = Cbact04cApplication.allowedBase;
            try {
                Cbact04cApplication.allowedBase = securityTempDir;
                Path tempFile = Files.createTempFile(securityTempDir, "test", ".txt");
                assertDoesNotThrow(() ->
                        Cbact04cApplication.validateFilePath(tempFile.toString(), false));
            } finally {
                Cbact04cApplication.allowedBase = savedBase;
            }
        }

        @Test
        void testValidOutputPathAccepted() {
            Path savedBase = Cbact04cApplication.allowedBase;
            try {
                Cbact04cApplication.allowedBase = securityTempDir;
                Path outputFile = securityTempDir.resolve("output.txt");
                assertDoesNotThrow(() ->
                        Cbact04cApplication.validateFilePath(outputFile.toString(), true));
            } finally {
                Cbact04cApplication.allowedBase = savedBase;
            }
        }

        @Test
        void testNonExistentInputFileRejected() {
            Path savedBase = Cbact04cApplication.allowedBase;
            try {
                Cbact04cApplication.allowedBase = securityTempDir;
                assertThrows(IllegalArgumentException.class, () ->
                        Cbact04cApplication.validateFilePath(
                                securityTempDir.resolve("nonexistent.txt").toString(), false));
            } finally {
                Cbact04cApplication.allowedBase = savedBase;
            }
        }

        @Test
        void testAbsolutePathOutsideBaseRejected() {
            // Even absolute paths outside allowed base should be rejected
            Path savedBase = Cbact04cApplication.allowedBase;
            try {
                Cbact04cApplication.allowedBase = securityTempDir;
                assertThrows(IllegalArgumentException.class, () ->
                        Cbact04cApplication.validateFilePath("/etc/shadow", true));
            } finally {
                Cbact04cApplication.allowedBase = savedBase;
            }
        }

        // --- CVE: Date Input Validation ---

        @Test
        void testValidDateAccepted() {
            assertTrue(Cbact04cApplication.isValidDate("2022-06-10"));
            assertTrue(Cbact04cApplication.isValidDate("2025-12-31"));
        }

        @Test
        void testInvalidDateFormatRejected() {
            assertFalse(Cbact04cApplication.isValidDate("06-10-2022"));
            assertFalse(Cbact04cApplication.isValidDate("2022/06/10"));
            assertFalse(Cbact04cApplication.isValidDate("not-a-date"));
            assertFalse(Cbact04cApplication.isValidDate(null));
            assertFalse(Cbact04cApplication.isValidDate(""));
            assertFalse(Cbact04cApplication.isValidDate("20220610"));
        }

        @Test
        void testDateWithInjectionAttemptRejected() {
            assertFalse(Cbact04cApplication.isValidDate("2022-06-10; rm -rf /"));
            assertFalse(Cbact04cApplication.isValidDate("$(whoami)--"));
        }

        // --- CVE: Input Validation / Integer Overflow (CWE-400, CWE-190) ---

        @Test
        void testParseUnsignedNumericOverlongInput() {
            // Exceeds 18-digit limit (Long.MAX_VALUE is 19 digits)
            assertThrows(IllegalArgumentException.class, () ->
                    CobolFieldParser.parseUnsignedNumeric("1234567890123456789"));
        }

        @Test
        void testParseUnsignedNumericNonDigitInput() {
            assertThrows(IllegalArgumentException.class, () ->
                    CobolFieldParser.parseUnsignedNumeric("12abc"));
        }

        @Test
        void testParseUnsignedNumericMaxSafeInput() {
            // 18 digits should succeed
            assertEquals(123456789012345678L,
                    CobolFieldParser.parseUnsignedNumeric("123456789012345678"));
        }

        @Test
        void testParseSignedDecimalOverlongInput() {
            assertThrows(IllegalArgumentException.class, () ->
                    CobolFieldParser.parseSignedDecimal("1234567890123456789", 2));
        }

        // --- CVE: Integer Overflow in formatSignedDecimal (CWE-190) ---

        @Test
        void testTransactionRecordOverflowDetected() {
            TransactionRecord rec = new TransactionRecord();
            rec.setTransactionId("TEST123456789012");
            rec.setTypeCode("01");
            rec.setCategoryCode(5);
            rec.setSource("System");
            rec.setDescription("Overflow test");
            // PIC S9(09)V99 max is 999999999.99 — set a value exceeding it
            rec.setAmount(new BigDecimal("9999999999.99"));
            rec.setMerchantId("000000000");
            rec.setMerchantName("");
            rec.setMerchantCity("");
            rec.setMerchantZip("");
            rec.setCardNumber("0000000000000000");
            rec.setOrigTimestamp("2022-06-10-00.00.00.000000");
            rec.setProcTimestamp("2022-06-10-00.00.00.000000");

            assertThrows(ArithmeticException.class, rec::toFixedWidth);
        }

        @Test
        void testAccountRecordOverflowDetected() {
            AccountRecord acct = new AccountRecord();
            acct.setAccountId("00000000001");
            acct.setActiveStatus("Y");
            // PIC S9(10)V99 max is 9999999999.99 — set a value exceeding it
            acct.setCurrentBalance(new BigDecimal("99999999999.99"));
            acct.setCreditLimit(BigDecimal.ZERO);
            acct.setCashCreditLimit(BigDecimal.ZERO);
            acct.setOpenDate("2020-01-01");
            acct.setExpirationDate("2025-12-31");
            acct.setReissueDate("2025-06-01");
            acct.setCurrentCycleCredit(BigDecimal.ZERO);
            acct.setCurrentCycleDebit(BigDecimal.ZERO);
            acct.setAddressZip("00000");
            acct.setGroupId("TESTGROUP ");

            assertThrows(ArithmeticException.class, acct::toFixedWidth);
        }

        @Test
        void testValidAmountDoesNotOverflow() {
            TransactionRecord rec = new TransactionRecord();
            rec.setTransactionId("TEST123456789012");
            rec.setTypeCode("01");
            rec.setCategoryCode(5);
            rec.setSource("System");
            rec.setDescription("Valid amount test");
            rec.setAmount(new BigDecimal("999999999.99"));
            rec.setMerchantId("000000000");
            rec.setMerchantName("");
            rec.setMerchantCity("");
            rec.setMerchantZip("");
            rec.setCardNumber("0000000000000000");
            rec.setOrigTimestamp("2022-06-10-00.00.00.000000");
            rec.setProcTimestamp("2022-06-10-00.00.00.000000");

            assertDoesNotThrow(rec::toFixedWidth);
        }

        // --- CVE: Mutable Internal State Exposure (CWE-200) ---

        @Test
        void testOutputTransactionsListIsImmutable() {
            List<TranCatBalRecord> tcatbals = new ArrayList<>();
            Map<String, CardXrefRecord> xrefs = new HashMap<>();
            Map<String, DisclosureGroupRecord> discGroups = new HashMap<>();
            Map<String, AccountRecord> accounts = new HashMap<>();

            InterestCalculatorService service = new InterestCalculatorService(
                    "2022-06-10", tcatbals, xrefs, discGroups, accounts);
            service.process();

            List<TransactionRecord> transactions = service.getOutputTransactions();
            assertThrows(UnsupportedOperationException.class, () ->
                    transactions.add(new TransactionRecord()));
        }
    }
}
