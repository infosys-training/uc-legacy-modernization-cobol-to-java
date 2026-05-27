package com.cardemo.batch.model;

import java.math.BigDecimal;

/**
 * Input account record corresponding to the CVACT01Y copybook (300-byte VSAM KSDS record).
 *
 * <pre>
 * COBOL layout:
 *   ACCT-ID                PIC 9(11)
 *   ACCT-ACTIVE-STATUS     PIC X(01)
 *   ACCT-CURR-BAL          PIC S9(10)V99
 *   ACCT-CREDIT-LIMIT      PIC S9(10)V99
 *   ACCT-CASH-CREDIT-LIMIT PIC S9(10)V99
 *   ACCT-OPEN-DATE         PIC X(10)
 *   ACCT-EXPIRAION-DATE    PIC X(10)
 *   ACCT-REISSUE-DATE      PIC X(10)
 *   ACCT-CURR-CYC-CREDIT   PIC S9(10)V99
 *   ACCT-CURR-CYC-DEBIT    PIC S9(10)V99
 *   ACCT-ADDR-ZIP          PIC X(10)
 *   ACCT-GROUP-ID          PIC X(10)
 *   FILLER                 PIC X(178)
 * </pre>
 */
public record AccountRecord(
    String acctId,
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
) {}
