package com.cardemo.batch.model;

import java.math.BigDecimal;

/**
 * Maps the COBOL copybook {@code CVACT01Y} — the 300-byte account master
 * record read from the VSAM KSDS file.
 *
 * <p>Field layout (all DISPLAY unless noted):
 * <pre>
 *   ACCT-ID                  PIC 9(11)
 *   ACCT-ACTIVE-STATUS       PIC X(01)
 *   ACCT-CURR-BAL            PIC S9(10)V99
 *   ACCT-CREDIT-LIMIT        PIC S9(10)V99
 *   ACCT-CASH-CREDIT-LIMIT   PIC S9(10)V99
 *   ACCT-OPEN-DATE           PIC X(10)
 *   ACCT-EXPIRAION-DATE      PIC X(10)
 *   ACCT-REISSUE-DATE        PIC X(10)
 *   ACCT-CURR-CYC-CREDIT     PIC S9(10)V99
 *   ACCT-CURR-CYC-DEBIT      PIC S9(10)V99
 *   ACCT-ADDR-ZIP            PIC X(10)
 *   ACCT-GROUP-ID            PIC X(10)
 *   FILLER                   PIC X(178)
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

    /** Total record length in the fixed-width file. */
    public static final int RECORD_LENGTH = 300;

    // Field offsets and lengths (0-based)
    public static final int ACCT_ID_OFF = 0;
    public static final int ACCT_ID_LEN = 11;
    public static final int ACTIVE_STATUS_OFF = 11;
    public static final int ACTIVE_STATUS_LEN = 1;
    public static final int CURR_BAL_OFF = 12;
    public static final int CURR_BAL_LEN = 12;
    public static final int CREDIT_LIMIT_OFF = 24;
    public static final int CREDIT_LIMIT_LEN = 12;
    public static final int CASH_CREDIT_LIMIT_OFF = 36;
    public static final int CASH_CREDIT_LIMIT_LEN = 12;
    public static final int OPEN_DATE_OFF = 48;
    public static final int OPEN_DATE_LEN = 10;
    public static final int EXPIRATION_DATE_OFF = 58;
    public static final int EXPIRATION_DATE_LEN = 10;
    public static final int REISSUE_DATE_OFF = 68;
    public static final int REISSUE_DATE_LEN = 10;
    public static final int CURR_CYC_CREDIT_OFF = 78;
    public static final int CURR_CYC_CREDIT_LEN = 12;
    public static final int CURR_CYC_DEBIT_OFF = 90;
    public static final int CURR_CYC_DEBIT_LEN = 12;
    public static final int ADDR_ZIP_OFF = 102;
    public static final int ADDR_ZIP_LEN = 10;
    public static final int GROUP_ID_OFF = 112;
    public static final int GROUP_ID_LEN = 10;
}
