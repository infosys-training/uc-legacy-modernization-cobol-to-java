package com.cardemo.batch.model;

import java.math.BigDecimal;

/**
 * Corresponds to the {@code OUT-ACCT-REC} structure written to the
 * sequential output file.
 *
 * <p>Field layout:
 * <pre>
 *   OUT-ACCT-ID                PIC 9(11)
 *   OUT-ACCT-ACTIVE-STATUS     PIC X(01)
 *   OUT-ACCT-CURR-BAL          PIC S9(10)V99
 *   OUT-ACCT-CREDIT-LIMIT      PIC S9(10)V99
 *   OUT-ACCT-CASH-CREDIT-LIMIT PIC S9(10)V99
 *   OUT-ACCT-OPEN-DATE         PIC X(10)
 *   OUT-ACCT-EXPIRAION-DATE    PIC X(10)
 *   OUT-ACCT-REISSUE-DATE      PIC X(10)
 *   OUT-ACCT-CURR-CYC-CREDIT   PIC S9(10)V99
 *   OUT-ACCT-CURR-CYC-DEBIT    PIC S9(10)V99  (COMP-3 in COBOL)
 *   OUT-ACCT-GROUP-ID          PIC X(10)
 * </pre>
 */
public record OutAccountRecord(
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
        String groupId
) {}
