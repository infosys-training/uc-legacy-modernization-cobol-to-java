package com.carddemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Maps to COBOL copybook CVACT03Y.cpy — CARD-XREF-RECORD (50-byte VSAM KSDS).
 * Cross-reference linking Card Number → Customer ID + Account ID.
 *
 * COBOL layout:
 *   XREF-CARD-NUM  PIC X(16)  — Primary key
 *   XREF-CUST-ID   PIC 9(09)  — FK to Customer
 *   XREF-ACCT-ID   PIC 9(11)  — FK to Account
 */
@Entity
@Table(name = "card_xref")
public class CardXref {

    @Id
    @Column(name = "card_num", length = 16)
    private String cardNumber;

    @Column(name = "cust_id", nullable = false)
    private Long customerId;

    @Column(name = "acct_id", nullable = false)
    private Long accountId;

    public CardXref() {}

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
}
