package com.cardemo.batch.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Corresponds to the {@code ARR-ARRAY-REC} structure written to the
 * array output file.
 *
 * <p>Field layout:
 * <pre>
 *   ARR-ACCT-ID               PIC 9(11)
 *   ARR-ACCT-BAL OCCURS 5 TIMES:
 *     ARR-ACCT-CURR-BAL       PIC S9(10)V99
 *     ARR-ACCT-CURR-CYC-DEBIT PIC S9(10)V99  (COMP-3 in COBOL)
 *   ARR-FILLER                PIC X(04)
 * </pre>
 *
 * @param acctId  the 11-digit account identifier
 * @param entries exactly 5 balance/debit pairs
 */
public record ArrayRecord(long acctId, List<Entry> entries) {

    public static final int ENTRY_COUNT = 5;

    public record Entry(BigDecimal currBal, BigDecimal currCycDebit) {
        public static final Entry ZERO = new Entry(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
