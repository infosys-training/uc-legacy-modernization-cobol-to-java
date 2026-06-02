package com.carddemo.batch.model;

import java.math.BigDecimal;

/**
 * Maps to COBOL copybook CVACT01Y - Account record (300 bytes).
 *
 * Field layout:
 *   ACCT-ID:                PIC 9(11)       positions 1-11
 *   ACCT-ACTIVE-STATUS:     PIC X(01)       position 12
 *   ACCT-CURR-BAL:          PIC S9(10)V99   positions 13-24
 *   ACCT-CREDIT-LIMIT:      PIC S9(10)V99   positions 25-36
 *   ACCT-CASH-CREDIT-LIMIT: PIC S9(10)V99   positions 37-48
 *   ACCT-OPEN-DATE:         PIC X(10)       positions 49-58
 *   ACCT-EXPIRAION-DATE:    PIC X(10)       positions 59-68
 *   ACCT-REISSUE-DATE:      PIC X(10)       positions 69-78
 *   ACCT-CURR-CYC-CREDIT:   PIC S9(10)V99   positions 79-90
 *   ACCT-CURR-CYC-DEBIT:    PIC S9(10)V99   positions 91-102
 *   ACCT-ADDR-ZIP:          PIC X(10)       positions 103-112
 *   ACCT-GROUP-ID:          PIC X(10)       positions 113-122
 *   FILLER:                 PIC X(178)      positions 123-300
 */
public record AccountRecord(
        String acctId,              // PIC 9(11)
        String activeStatus,        // PIC X(01)
        BigDecimal currBal,         // PIC S9(10)V99
        BigDecimal creditLimit,     // PIC S9(10)V99
        BigDecimal cashCreditLimit, // PIC S9(10)V99
        String openDate,            // PIC X(10)  YYYY-MM-DD
        String expirationDate,      // PIC X(10)  YYYY-MM-DD
        String reissueDate,         // PIC X(10)  YYYY-MM-DD
        BigDecimal currCycCredit,   // PIC S9(10)V99
        BigDecimal currCycDebit,    // PIC S9(10)V99
        String addrZip,             // PIC X(10)
        String groupId              // PIC X(10)
) {
    public static final int RECORD_LENGTH = 300;
}
