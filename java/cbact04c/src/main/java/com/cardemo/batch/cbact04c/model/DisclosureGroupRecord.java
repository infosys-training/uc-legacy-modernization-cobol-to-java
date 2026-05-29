package com.cardemo.batch.cbact04c.model;

import java.math.BigDecimal;

/**
 * Disclosure group record (CVTRA02Y.cpy, RECLN 50).
 * Maps to COBOL copybook DIS-GROUP-RECORD.
 */
public class DisclosureGroupRecord {

    private String accountGroupId;  // DIS-ACCT-GROUP-ID PIC X(10)
    private String tranTypeCode;    // DIS-TRAN-TYPE-CD PIC X(02)
    private int tranCatCode;        // DIS-TRAN-CAT-CD PIC 9(04)
    private BigDecimal interestRate; // DIS-INT-RATE PIC S9(04)V99

    public DisclosureGroupRecord() {}

    public DisclosureGroupRecord(String accountGroupId, String tranTypeCode,
                                  int tranCatCode, BigDecimal interestRate) {
        this.accountGroupId = accountGroupId;
        this.tranTypeCode = tranTypeCode;
        this.tranCatCode = tranCatCode;
        this.interestRate = interestRate;
    }

    public String getAccountGroupId() { return accountGroupId; }
    public void setAccountGroupId(String accountGroupId) { this.accountGroupId = accountGroupId; }

    public String getTranTypeCode() { return tranTypeCode; }
    public void setTranTypeCode(String tranTypeCode) { this.tranTypeCode = tranTypeCode; }

    public int getTranCatCode() { return tranCatCode; }
    public void setTranCatCode(int tranCatCode) { this.tranCatCode = tranCatCode; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    public String getCompositeKey() {
        return padRight(accountGroupId, 10) + padRight(tranTypeCode, 2) +
               String.format("%04d", tranCatCode);
    }

    private static String padRight(String s, int len) {
        if (s == null) s = "";
        if (s.length() >= len) return s.substring(0, len);
        return s + " ".repeat(len - s.length());
    }

    @Override
    public String toString() {
        return "DisclosureGroupRecord{group=" + accountGroupId + ", type=" + tranTypeCode +
               ", cat=" + tranCatCode + ", rate=" + interestRate + "}";
    }
}
