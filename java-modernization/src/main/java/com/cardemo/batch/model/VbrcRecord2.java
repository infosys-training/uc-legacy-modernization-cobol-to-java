package com.cardemo.batch.model;

import java.math.BigDecimal;

/**
 * Longer variable-length record (39 bytes in COBOL) written to the VBRC file.
 *
 * <pre>
 *   VB2-ACCT-ID              PIC 9(11)
 *   VB2-ACCT-CURR-BAL        PIC S9(10)V99
 *   VB2-ACCT-CREDIT-LIMIT    PIC S9(10)V99
 *   VB2-ACCT-REISSUE-YYYY    PIC X(04)
 * </pre>
 */
public record VbrcRecord2(
        long acctId,
        BigDecimal currBal,
        BigDecimal creditLimit,
        String reissueYear
) {}
