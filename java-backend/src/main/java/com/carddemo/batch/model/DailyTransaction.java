package com.carddemo.batch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Maps to COBOL copybook CVTRA06Y.cpy — DALYTRAN-RECORD.
 * Input file for the daily transaction posting batch (CBTRN02C).
 *
 * This represents a pending transaction awaiting validation and posting.
 */
@Entity
@Table(name = "daily_transactions")
public class DailyTransaction {

    @Id
    @Column(name = "tran_id", length = 16)
    private String transactionId;

    @Column(name = "card_num", length = 16, nullable = false)
    private String cardNumber;

    @Column(name = "type_cd", length = 2, nullable = false)
    private String typeCode;

    @Column(name = "cat_cd")
    private Integer categoryCode;

    @Column(name = "source", length = 10)
    private String source;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "amount", precision = 11, scale = 2, nullable = false)
    private BigDecimal amount;

    @Column(name = "merchant_id", length = 9)
    private String merchantId;

    @Column(name = "merchant_name", length = 50)
    private String merchantName;

    @Column(name = "merchant_city", length = 30)
    private String merchantCity;

    @Column(name = "merchant_zip", length = 10)
    private String merchantZip;

    @Column(name = "orig_ts")
    private LocalDateTime originTimestamp;

    @Column(name = "processed", nullable = false)
    private boolean processed = false;

    public DailyTransaction() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

    public Integer getCategoryCode() { return categoryCode; }
    public void setCategoryCode(Integer categoryCode) { this.categoryCode = categoryCode; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getMerchantCity() { return merchantCity; }
    public void setMerchantCity(String merchantCity) { this.merchantCity = merchantCity; }

    public String getMerchantZip() { return merchantZip; }
    public void setMerchantZip(String merchantZip) { this.merchantZip = merchantZip; }

    public LocalDateTime getOriginTimestamp() { return originTimestamp; }
    public void setOriginTimestamp(LocalDateTime originTimestamp) { this.originTimestamp = originTimestamp; }

    public boolean isProcessed() { return processed; }
    public void setProcessed(boolean processed) { this.processed = processed; }
}
