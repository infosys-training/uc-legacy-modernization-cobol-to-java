package com.carddemo.batch.model;

import java.math.BigDecimal;

/**
 * Inner element of ARR-ACCT-BAL OCCURS 5 TIMES.
 *
 *   currentBalance:    12 bytes (PIC S9(10)V99 zoned decimal)
 *   currentCycleDebit: 7 bytes  (PIC S9(10)V99 COMP-3 packed decimal)
 */
public record ArrayElement(
        BigDecimal currentBalance,
        BigDecimal currentCycleDebit
) {}
