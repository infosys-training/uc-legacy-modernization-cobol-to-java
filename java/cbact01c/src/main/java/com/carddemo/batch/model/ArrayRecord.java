package com.carddemo.batch.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Output record written to the array file (ARRY-FILE).
 * Mirrors the FD ARR-ARRAY-REC layout in CBACT01C.
 *
 * Contains 5 balance/debit pairs (OCCURS 5 TIMES).
 */
public record ArrayRecord(
        long acctId,
        List<BalanceEntry> entries
) {
    public record BalanceEntry(BigDecimal currBal, BigDecimal currCycDebit) {}

    public static final int NUM_ENTRIES = 5;
}
