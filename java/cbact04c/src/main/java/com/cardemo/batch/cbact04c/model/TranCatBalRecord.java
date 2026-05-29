package com.cardemo.batch.cbact04c.model;

import java.math.BigDecimal;

/**
 * Transaction category balance record (CVTRA01Y.cpy, RECLN 50).
 * Maps to COBOL copybook TRAN-CAT-BAL-RECORD.
 */
public class TranCatBalRecord {

    private String accountId;       // TRANCAT-ACCT-ID PIC 9(11)
    private String typeCode;        // TRANCAT-TYPE-CD PIC X(02)
    private int categoryCode;       // TRANCAT-CD PIC 9(04)
    private BigDecimal balance;     // TRAN-CAT-BAL PIC S9(09)V99

    public TranCatBalRecord() {}

    public TranCatBalRecord(String accountId, String typeCode, int categoryCode, BigDecimal balance) {
        this.accountId = accountId;
        this.typeCode = typeCode;
        this.categoryCode = categoryCode;
        this.balance = balance;
    }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

    public int getCategoryCode() { return categoryCode; }
    public void setCategoryCode(int categoryCode) { this.categoryCode = categoryCode; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    public String getCompositeKey() {
        return accountId + typeCode + String.format("%04d", categoryCode);
    }

    @Override
    public String toString() {
        return "TranCatBalRecord{acctId=" + accountId + ", type=" + typeCode +
               ", cat=" + categoryCode + ", bal=" + balance + "}";
    }
}
