package com.cardemo.batch.processor;

import com.cardemo.batch.model.*;
import com.cardemo.batch.model.ArrayAccountRecord.BalanceEntry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountProcessorTest {

    private AccountProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new AccountProcessor();
    }

    private AccountRecord sampleAccount(BigDecimal debit) {
        return new AccountRecord(
                "00000000001",
                "Y",
                new BigDecimal("194.00"),
                new BigDecimal("2020.00"),
                new BigDecimal("1020.00"),
                "2014-11-20",
                "2025-05-20",
                "2025-05-20",
                BigDecimal.ZERO,
                debit,
                "A000000000",
                ""
        );
    }

    private AccountRecord sampleAccount() {
        return sampleAccount(BigDecimal.ZERO);
    }

    @Nested
    class OutRecordTests {

        @Test
        void fieldsAreCopiedCorrectly() {
            OutAccountRecord out = processor.process(sampleAccount()).outRecord();

            assertEquals("00000000001", out.acctId());
            assertEquals("Y", out.activeStatus());
            assertEquals(0, new BigDecimal("194.00").compareTo(out.currBal()));
            assertEquals(0, new BigDecimal("2020.00").compareTo(out.creditLimit()));
            assertEquals(0, new BigDecimal("1020.00").compareTo(out.cashCreditLimit()));
            assertEquals("2014-11-20", out.openDate());
            assertEquals("2025-05-20", out.expirationDate());
            assertEquals(0, BigDecimal.ZERO.compareTo(out.currCycCredit()));
        }

        @Test
        void reissueDateConvertedToCompactFormat() {
            OutAccountRecord out = processor.process(sampleAccount()).outRecord();
            // YYYY-MM-DD -> YYYYMMDD padded to 10 chars
            assertEquals("20250520  ", out.reissueDate());
        }

        @Test
        void zeroDebitDefaultsTo2525() {
            OutAccountRecord out = processor.process(sampleAccount(BigDecimal.ZERO)).outRecord();
            assertEquals(0, new BigDecimal("2525.00").compareTo(out.currCycDebit()));
        }

        @Test
        void nonZeroDebitPreserved() {
            BigDecimal debit = new BigDecimal("500.00");
            OutAccountRecord out = processor.process(sampleAccount(debit)).outRecord();
            assertEquals(0, debit.compareTo(out.currCycDebit()));
        }

        @Test
        void groupIdCopied() {
            OutAccountRecord out = processor.process(sampleAccount()).outRecord();
            assertEquals("", out.groupId());
        }
    }

    @Nested
    class ArrayRecordTests {

        @Test
        void hasFiveEntries() {
            ArrayAccountRecord arr = processor.process(sampleAccount()).arrayRecord();
            assertEquals(5, arr.balanceEntries().size());
        }

        @Test
        void index0_usesAccountBalAndDebit1005() {
            ArrayAccountRecord arr = processor.process(sampleAccount()).arrayRecord();
            BalanceEntry e = arr.balanceEntries().get(0);
            assertEquals(0, new BigDecimal("194.00").compareTo(e.currBal()));
            assertEquals(0, new BigDecimal("1005.00").compareTo(e.currCycDebit()));
        }

        @Test
        void index1_usesAccountBalAndDebit1525() {
            ArrayAccountRecord arr = processor.process(sampleAccount()).arrayRecord();
            BalanceEntry e = arr.balanceEntries().get(1);
            assertEquals(0, new BigDecimal("194.00").compareTo(e.currBal()));
            assertEquals(0, new BigDecimal("1525.00").compareTo(e.currCycDebit()));
        }

        @Test
        void index2_usesHardcodedNegatives() {
            ArrayAccountRecord arr = processor.process(sampleAccount()).arrayRecord();
            BalanceEntry e = arr.balanceEntries().get(2);
            assertEquals(0, new BigDecimal("-1025.00").compareTo(e.currBal()));
            assertEquals(0, new BigDecimal("-2500.00").compareTo(e.currCycDebit()));
        }

        @Test
        void indices3and4_areZero() {
            ArrayAccountRecord arr = processor.process(sampleAccount()).arrayRecord();
            for (int i = 3; i <= 4; i++) {
                BalanceEntry e = arr.balanceEntries().get(i);
                assertEquals(0, BigDecimal.ZERO.compareTo(e.currBal()));
                assertEquals(0, BigDecimal.ZERO.compareTo(e.currCycDebit()));
            }
        }

        @Test
        void acctIdCopied() {
            ArrayAccountRecord arr = processor.process(sampleAccount()).arrayRecord();
            assertEquals("00000000001", arr.acctId());
        }
    }

    @Nested
    class VbrcRecordTests {

        @Test
        void vbrc1_containsIdAndStatus() {
            VbrcRecord1 vb1 = processor.process(sampleAccount()).vbrcRecord1();
            assertEquals("00000000001", vb1.acctId());
            assertEquals("Y", vb1.activeStatus());
        }

        @Test
        void vbrc2_containsBalancesAndYear() {
            VbrcRecord2 vb2 = processor.process(sampleAccount()).vbrcRecord2();
            assertEquals("00000000001", vb2.acctId());
            assertEquals(0, new BigDecimal("194.00").compareTo(vb2.currBal()));
            assertEquals(0, new BigDecimal("2020.00").compareTo(vb2.creditLimit()));
            assertEquals("2025", vb2.reissueYear());
        }
    }

    @Nested
    class IntegrationTests {

        @Test
        void processSecondSampleRecord() {
            AccountRecord acct2 = new AccountRecord(
                    "00000000002",
                    "Y",
                    new BigDecimal("158.00"),
                    new BigDecimal("6130.00"),
                    new BigDecimal("5448.00"),
                    "2013-06-19",
                    "2024-08-11",
                    "2024-08-11",
                    BigDecimal.ZERO,
                    BigDecimal.ZERO,
                    "A000000000",
                    ""
            );

            ProcessingResult result = processor.process(acct2);

            // Out record
            assertEquals("20240811  ", result.outRecord().reissueDate());
            assertEquals(0, new BigDecimal("2525.00").compareTo(
                    result.outRecord().currCycDebit()));

            // Array record
            assertEquals(0, new BigDecimal("158.00").compareTo(
                    result.arrayRecord().balanceEntries().get(0).currBal()));
            assertEquals(0, new BigDecimal("158.00").compareTo(
                    result.arrayRecord().balanceEntries().get(1).currBal()));

            // VB records
            assertEquals("2024", result.vbrcRecord2().reissueYear());
            assertEquals(0, new BigDecimal("6130.00").compareTo(
                    result.vbrcRecord2().creditLimit()));
        }

        @Test
        void processRecordWithNonZeroDebit() {
            AccountRecord acct = new AccountRecord(
                    "00000000010",
                    "Y",
                    new BigDecimal("159.00"),
                    new BigDecimal("5401.00"),
                    new BigDecimal("4442.00"),
                    "2015-09-13",
                    "2023-01-27",
                    "2023-01-27",
                    new BigDecimal("100.50"),
                    new BigDecimal("250.75"),
                    "B000000000",
                    "GRP001"
            );

            ProcessingResult result = processor.process(acct);

            // Non-zero debit should be preserved
            assertEquals(0, new BigDecimal("250.75").compareTo(
                    result.outRecord().currCycDebit()));
            // Non-zero credit should be preserved
            assertEquals(0, new BigDecimal("100.50").compareTo(
                    result.outRecord().currCycCredit()));
            // Group ID should be copied
            assertEquals("GRP001", result.outRecord().groupId());
            // Reissue date conversion
            assertEquals("20230127  ", result.outRecord().reissueDate());
        }

        @Test
        void processMultipleRecords_allProduceValidResults() {
            List<AccountRecord> records = List.of(
                    sampleAccount(),
                    sampleAccount(new BigDecimal("100.00")),
                    sampleAccount(new BigDecimal("-50.00"))
            );

            for (AccountRecord acct : records) {
                ProcessingResult result = processor.process(acct);
                assertNotNull(result.outRecord());
                assertNotNull(result.arrayRecord());
                assertNotNull(result.vbrcRecord1());
                assertNotNull(result.vbrcRecord2());
                assertEquals(5, result.arrayRecord().balanceEntries().size());
            }
        }
    }
}
