package com.cardemo.batch.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Array output record written to the array file (ARRY-FILE).
 * Mirrors ARR-ARRAY-REC in CBACT01C: account ID plus 5 balance/debit pairs.
 */
public record ArrayAccountRecord(
    String acctId,
    List<BalanceEntry> balanceEntries
) {

    public static final int ENTRY_COUNT = 5;

    public record BalanceEntry(BigDecimal currBal, BigDecimal currCycDebit) {

        public static final BalanceEntry ZERO =
                new BalanceEntry(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
