package com.carddemo.batch.cbact01c.model;

import java.math.BigDecimal;

/**
 * Maps to ARR-ARRAY-REC — the array-based output written to ARRYFILE.
 *
 * <pre>
 * 05  ARR-ACCT-ID               PIC 9(11)
 * 05  ARR-ACCT-BAL OCCURS 5 TIMES
 *   10  ARR-ACCT-CURR-BAL       PIC S9(10)V99
 *   10  ARR-ACCT-CURR-CYC-DEBIT PIC S9(10)V99 COMP-3
 * 05  ARR-FILLER                PIC X(04)
 * </pre>
 *
 * Each balance entry holds a current balance and a cycle debit amount.
 */
public record ArrayAccountRecord(
        long acctId,
        BalanceEntry[] balanceEntries
) {
    public static final int OCCURS_COUNT = 5;

    public record BalanceEntry(
            BigDecimal currBal,
            BigDecimal currCycDebit
    ) {
        public static final BalanceEntry ZERO = new BalanceEntry(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
