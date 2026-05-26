package com.carddemo.batch.model;

import java.math.BigDecimal;

/**
 * Output record written to the flat account output file (OUT-FILE).
 * Mirrors the FD OUT-ACCT-REC layout in CBACT01C.
 */
public record OutAccountRecord(
        long acctId,
        char activeStatus,
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
