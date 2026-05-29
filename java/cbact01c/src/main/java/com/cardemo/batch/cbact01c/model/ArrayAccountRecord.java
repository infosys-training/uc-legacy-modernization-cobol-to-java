package com.cardemo.batch.cbact01c.model;

import java.util.List;

/**
 * Corresponds to COBOL ARR-ARRAY-REC — the array output record written to ARRYFILE.
 * Contains 5 balance/debit pairs per account (OCCURS 5 TIMES).
 */
public record ArrayAccountRecord(
        String acctId,
        List<BalanceEntry> balanceEntries
) {
    public ArrayAccountRecord {
        if (balanceEntries.size() != 5) {
            throw new IllegalArgumentException("Exactly 5 balance entries required");
        }
    }
}
