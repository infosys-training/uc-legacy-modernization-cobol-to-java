package com.carddemo.model;

import java.math.BigDecimal;

/**
 * Maps COBOL FD ARRY-FILE record: ARR-ARRAY-REC
 *
 * COBOL layout:
 *   05 ARR-ACCT-ID                PIC 9(11)
 *   05 ARR-ACCT-BAL OCCURS 5 TIMES
 *     10 ARR-ACCT-CURR-BAL        PIC S9(10)V99
 *     10 ARR-ACCT-CURR-CYC-DEBIT  PIC S9(10)V99 COMP-3
 *   05 ARR-FILLER                 PIC X(04)
 *
 * The COBOL program only populates indices 1-3 (1-based), leaving 4-5 at zero.
 */
public class ArrayRecord {

    public static final int ARRAY_SIZE = 5;

    private long acctId;
    private final BigDecimal[] balances = new BigDecimal[ARRAY_SIZE];
    private final BigDecimal[] cycleDebits = new BigDecimal[ARRAY_SIZE];

    public ArrayRecord() {
        for (int i = 0; i < ARRAY_SIZE; i++) {
            balances[i] = BigDecimal.ZERO;
            cycleDebits[i] = BigDecimal.ZERO;
        }
    }

    public long getAcctId() { return acctId; }
    public void setAcctId(long acctId) { this.acctId = acctId; }

    public BigDecimal getBalance(int index) { return balances[index]; }
    public void setBalance(int index, BigDecimal value) { balances[index] = value; }

    public BigDecimal getCycleDebit(int index) { return cycleDebits[index]; }
    public void setCycleDebit(int index, BigDecimal value) { cycleDebits[index] = value; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%011d", acctId));
        for (int i = 0; i < ARRAY_SIZE; i++) {
            sb.append(String.format("|bal[%d]=%s,deb[%d]=%s", i, balances[i], i, cycleDebits[i]));
        }
        return sb.toString();
    }
}
