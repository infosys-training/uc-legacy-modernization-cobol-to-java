package com.cardemo.batch.cbact01c.model;

import java.math.BigDecimal;

/**
 * Corresponds to a single occurrence of ARR-ACCT-BAL in the COBOL array record.
 */
public record BalanceEntry(
        BigDecimal currBal,
        BigDecimal currCycDebit
) {}
