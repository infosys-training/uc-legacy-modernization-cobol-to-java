package com.cardemo.batch.cbact01c.model;

import java.math.BigDecimal;

/**
 * Corresponds to COBOL OUT-ACCT-REC — the flat output record written to OUTFILE.
 * Mirrors 1300-POPUL-ACCT-RECORD logic including date reformatting and debit defaulting.
 */
public record OutputAccountRecord(
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
        String groupId
) {}
