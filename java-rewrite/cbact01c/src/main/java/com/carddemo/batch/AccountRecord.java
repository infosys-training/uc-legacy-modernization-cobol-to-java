package com.carddemo.batch;

import java.math.BigDecimal;

/**
 * Maps to COBOL copybook CVACT01Y — Account Record (RECLN 300).
 *
 * Field layout:
 *   Bytes  1-11:  ACCT-ID                PIC 9(11)
 *   Byte  12:     ACCT-ACTIVE-STATUS     PIC X(01)
 *   Bytes 13-24:  ACCT-CURR-BAL          PIC S9(10)V99
 *   Bytes 25-36:  ACCT-CREDIT-LIMIT      PIC S9(10)V99
 *   Bytes 37-48:  ACCT-CASH-CREDIT-LIMIT PIC S9(10)V99
 *   Bytes 49-58:  ACCT-OPEN-DATE         PIC X(10)
 *   Bytes 59-68:  ACCT-EXPIRAION-DATE    PIC X(10)
 *   Bytes 69-78:  ACCT-REISSUE-DATE      PIC X(10)
 *   Bytes 79-90:  ACCT-CURR-CYC-CREDIT   PIC S9(10)V99
 *   Bytes 91-102: ACCT-CURR-CYC-DEBIT    PIC S9(10)V99
 *   Bytes 103-112: ACCT-ADDR-ZIP         PIC X(10)
 *   Bytes 113-122: ACCT-GROUP-ID         PIC X(10)
 *   Bytes 123-300: FILLER                PIC X(178)
 */
public record AccountRecord(
        String acctId,              // PIC 9(11)
        String activeStatus,        // PIC X(01)
        BigDecimal currBal,         // PIC S9(10)V99
        BigDecimal creditLimit,     // PIC S9(10)V99
        BigDecimal cashCreditLimit, // PIC S9(10)V99
        String openDate,            // PIC X(10) — YYYY-MM-DD
        String expirationDate,      // PIC X(10) — YYYY-MM-DD
        String reissueDate,         // PIC X(10) — YYYY-MM-DD
        BigDecimal currCycCredit,   // PIC S9(10)V99
        BigDecimal currCycDebit,    // PIC S9(10)V99
        String addrZip,             // PIC X(10)
        String groupId              // PIC X(10)
) {
    public static final int RECORD_LENGTH = 300;

    /**
     * Parse an AccountRecord from a fixed-length 300-byte ASCII string.
     */
    public static AccountRecord parse(String line) {
        if (line.length() < 102) {
            throw new IllegalArgumentException(
                    "Account record too short: " + line.length() + " bytes (need at least 102)");
        }

        // Pad to full record length if needed (trailing spaces may be trimmed)
        String padded = line.length() < RECORD_LENGTH
                ? line + " ".repeat(RECORD_LENGTH - line.length())
                : line;

        return new AccountRecord(
                padded.substring(0, 11),                                              // ACCT-ID
                padded.substring(11, 12),                                             // ACCT-ACTIVE-STATUS
                CobolDecimalParser.parseSignedDecimal(padded.substring(12, 24), 2),   // ACCT-CURR-BAL
                CobolDecimalParser.parseSignedDecimal(padded.substring(24, 36), 2),   // ACCT-CREDIT-LIMIT
                CobolDecimalParser.parseSignedDecimal(padded.substring(36, 48), 2),   // ACCT-CASH-CREDIT-LIMIT
                padded.substring(48, 58),                                             // ACCT-OPEN-DATE
                padded.substring(58, 68),                                             // ACCT-EXPIRAION-DATE
                padded.substring(68, 78),                                             // ACCT-REISSUE-DATE
                CobolDecimalParser.parseSignedDecimal(padded.substring(78, 90), 2),   // ACCT-CURR-CYC-CREDIT
                CobolDecimalParser.parseSignedDecimal(padded.substring(90, 102), 2),  // ACCT-CURR-CYC-DEBIT
                padded.substring(102, 112),                                           // ACCT-ADDR-ZIP
                padded.substring(112, 122).trim()                                     // ACCT-GROUP-ID
        );
    }
}
