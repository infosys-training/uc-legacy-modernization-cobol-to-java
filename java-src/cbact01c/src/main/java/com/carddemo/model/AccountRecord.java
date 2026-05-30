package com.carddemo.model;

import java.math.BigDecimal;

/**
 * Maps the COBOL copybook CVACT01Y — Account entity (300-byte VSAM KSDS record).
 *
 * <pre>
 * COBOL layout:
 *   05  ACCT-ID                  PIC 9(11)
 *   05  ACCT-ACTIVE-STATUS       PIC X(01)
 *   05  ACCT-CURR-BAL            PIC S9(10)V99
 *   05  ACCT-CREDIT-LIMIT        PIC S9(10)V99
 *   05  ACCT-CASH-CREDIT-LIMIT   PIC S9(10)V99
 *   05  ACCT-OPEN-DATE           PIC X(10)
 *   05  ACCT-EXPIRAION-DATE      PIC X(10)
 *   05  ACCT-REISSUE-DATE        PIC X(10)
 *   05  ACCT-CURR-CYC-CREDIT     PIC S9(10)V99
 *   05  ACCT-CURR-CYC-DEBIT      PIC S9(10)V99
 *   05  ACCT-ADDR-ZIP            PIC X(10)
 *   05  ACCT-GROUP-ID            PIC X(10)
 *   05  FILLER                   PIC X(178)
 * </pre>
 */
public record AccountRecord(
        long acctId,
        String activeStatus,
        BigDecimal currBal,
        BigDecimal creditLimit,
        BigDecimal cashCreditLimit,
        String openDate,
        String expirationDate,
        String reissueDate,
        BigDecimal currCycCredit,
        BigDecimal currCycDebit,
        String addrZip,
        String groupId
) {

    /** Total record length in the VSAM file. */
    public static final int RECORD_LENGTH = 300;

    /**
     * Parse a 300-byte fixed-length ASCII line into an AccountRecord.
     * Field positions mirror the COBOL PIC clauses.
     *
     * Signed numeric fields use trailing overpunch sign convention:
     *   '{' = +0, 'A'-'I' = +1..+9
     *   '}' = -0, 'J'-'R' = -1..-9
     */
    public static AccountRecord parse(String line) {
        if (line.length() < RECORD_LENGTH) {
            line = String.format("%-" + RECORD_LENGTH + "s", line);
        }
        int pos = 0;

        long acctId = Long.parseLong(line.substring(pos, pos + 11).trim());
        pos += 11;

        String activeStatus = line.substring(pos, pos + 1);
        pos += 1;

        BigDecimal currBal = parseSignedDecimal(line, pos, 12, 2);
        pos += 12;

        BigDecimal creditLimit = parseSignedDecimal(line, pos, 12, 2);
        pos += 12;

        BigDecimal cashCreditLimit = parseSignedDecimal(line, pos, 12, 2);
        pos += 12;

        String openDate = line.substring(pos, pos + 10).trim();
        pos += 10;

        String expirationDate = line.substring(pos, pos + 10).trim();
        pos += 10;

        String reissueDate = line.substring(pos, pos + 10).trim();
        pos += 10;

        BigDecimal currCycCredit = parseSignedDecimal(line, pos, 12, 2);
        pos += 12;

        BigDecimal currCycDebit = parseSignedDecimal(line, pos, 12, 2);
        pos += 12;

        String addrZip = line.substring(pos, pos + 10).trim();
        pos += 10;

        String groupId = line.substring(pos, pos + 10).trim();

        return new AccountRecord(
                acctId, activeStatus, currBal, creditLimit, cashCreditLimit,
                openDate, expirationDate, reissueDate,
                currCycCredit, currCycDebit, addrZip, groupId
        );
    }

    /**
     * Parse a COBOL signed numeric field with trailing overpunch sign.
     * PIC S9(n)V99 is stored as (n+2) display digits with the last
     * character carrying the sign via overpunch encoding.
     */
    public static BigDecimal parseSignedDecimal(String line, int offset, int length, int scale) {
        String raw = line.substring(offset, offset + length);
        char lastChar = raw.charAt(raw.length() - 1);
        int sign = 1;
        int lastDigit;

        if (lastChar >= '0' && lastChar <= '9') {
            lastDigit = lastChar - '0';
        } else if (lastChar == '{') {
            lastDigit = 0;
        } else if (lastChar == '}') {
            lastDigit = 0;
            sign = -1;
        } else if (lastChar >= 'A' && lastChar <= 'I') {
            lastDigit = lastChar - 'A' + 1;
        } else if (lastChar >= 'J' && lastChar <= 'R') {
            lastDigit = lastChar - 'J' + 1;
            sign = -1;
        } else {
            lastDigit = 0;
        }

        String digits = raw.substring(0, raw.length() - 1) + lastDigit;
        BigDecimal value = new BigDecimal(digits).movePointLeft(scale);
        return sign < 0 ? value.negate() : value;
    }
}
