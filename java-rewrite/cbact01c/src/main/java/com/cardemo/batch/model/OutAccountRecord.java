package com.cardemo.batch.model;

import java.math.BigDecimal;

/**
 * Output record written to the sequential output file (OUT-FILE).
 * Mirrors the OUT-ACCT-REC structure in CBACT01C.
 */
public record OutAccountRecord(
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
