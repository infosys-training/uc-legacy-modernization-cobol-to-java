package com.cardemo.batch.cbexport.model;

import java.math.BigDecimal;

/**
 * Maps to CVACT01Y.cpy — Account record (RECLN 300).
 */
public record AccountRecord(
        String acctId,
        String activeStatus,
        BigDecimal currentBalance,
        BigDecimal creditLimit,
        BigDecimal cashCreditLimit,
        String openDate,
        String expirationDate,
        String reissueDate,
        BigDecimal currentCycleCredit,
        BigDecimal currentCycleDebit,
        String zipCode,
        String groupId
) {}
