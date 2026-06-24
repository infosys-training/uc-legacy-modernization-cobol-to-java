package com.carddemo.batch;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class AccountRecordTest {

    // First record from app/data/ASCII/acctdata.txt (padded to 300 bytes)
    private static final String RECORD_1 =
            "00000000001Y00000001940{00000020200{00000010200{2014-11-202025-05-202025-05-20"
                    + "00000000000{00000000000{A000000000"
                    + " ".repeat(178);

    // Second record
    private static final String RECORD_2 =
            "00000000002Y00000001580{00000061300{00000054480{2013-06-192024-08-112024-08-11"
                    + "00000000000{00000000000{A000000000"
                    + " ".repeat(178);

    @Test
    void parseFirstRecord() {
        AccountRecord acct = AccountRecord.parse(RECORD_1);

        assertEquals("00000000001", acct.acctId());
        assertEquals("Y", acct.activeStatus());
        assertEquals(new BigDecimal("194.00"), acct.currBal());
        assertEquals(new BigDecimal("2020.00"), acct.creditLimit());
        assertEquals(new BigDecimal("1020.00"), acct.cashCreditLimit());
        assertEquals("2014-11-20", acct.openDate());
        assertEquals("2025-05-20", acct.expirationDate());
        assertEquals("2025-05-20", acct.reissueDate());
        assertEquals(0, acct.currCycCredit().compareTo(BigDecimal.ZERO));
        assertEquals(0, acct.currCycDebit().compareTo(BigDecimal.ZERO));
        assertEquals("A000000000", acct.addrZip());
    }

    @Test
    void parseSecondRecord() {
        AccountRecord acct = AccountRecord.parse(RECORD_2);

        assertEquals("00000000002", acct.acctId());
        assertEquals("Y", acct.activeStatus());
        assertEquals(new BigDecimal("158.00"), acct.currBal());
        assertEquals(new BigDecimal("6130.00"), acct.creditLimit());
        assertEquals(new BigDecimal("5448.00"), acct.cashCreditLimit());
        assertEquals("2013-06-19", acct.openDate());
        assertEquals("2024-08-11", acct.expirationDate());
        assertEquals("2024-08-11", acct.reissueDate());
    }

    @Test
    void parseTooShortThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> AccountRecord.parse("short"));
    }

    @Test
    void parseWithTrailingSpacesTrimmed() {
        // Record without full 300-byte padding (only essential 122 bytes)
        String shortLine = "00000000001Y00000001940{00000020200{00000010200{2014-11-202025-05-202025-05-20"
                + "00000000000{00000000000{A000000000";
        AccountRecord acct = AccountRecord.parse(shortLine);
        assertEquals("00000000001", acct.acctId());
        assertEquals(new BigDecimal("194.00"), acct.currBal());
    }

    @Test
    void recordLengthConstant() {
        assertEquals(300, AccountRecord.RECORD_LENGTH);
    }
}
