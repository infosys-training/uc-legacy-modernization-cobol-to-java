package com.cardemo.batch.cbact04c.model;

/**
 * Card cross-reference record (CVACT03Y.cpy, RECLN 50).
 * Maps to COBOL copybook CARD-XREF-RECORD.
 */
public class CardXrefRecord {

    private String cardNumber;   // XREF-CARD-NUM PIC X(16)
    private String customerId;   // XREF-CUST-ID PIC 9(09)
    private String accountId;    // XREF-ACCT-ID PIC 9(11)

    public CardXrefRecord() {}

    public CardXrefRecord(String cardNumber, String customerId, String accountId) {
        this.cardNumber = cardNumber;
        this.customerId = customerId;
        this.accountId = accountId;
    }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getCustomerId() { return customerId; }
    public void setCustomerId(String customerId) { this.customerId = customerId; }

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    @Override
    public String toString() {
        return "CardXrefRecord{card=" + cardNumber + ", cust=" + customerId +
               ", acct=" + accountId + "}";
    }
}
