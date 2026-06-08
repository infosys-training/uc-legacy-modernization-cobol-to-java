package com.carddemo.batch.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

/**
 * Tracks rejected daily transactions (DALYREJS-FILE equivalent).
 * Populated during the PostTransaction batch when validation fails.
 */
@Entity
@Table(name = "transaction_rejects")
public class TransactionReject {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tran_id", length = 16, nullable = false)
    private String transactionId;

    @Column(name = "fail_reason_code")
    private Integer failReasonCode;

    @Column(name = "fail_reason_desc", length = 200)
    private String failReasonDescription;

    @Column(name = "rejected_at", nullable = false)
    private LocalDateTime rejectedAt;

    public TransactionReject() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public Integer getFailReasonCode() { return failReasonCode; }
    public void setFailReasonCode(Integer failReasonCode) { this.failReasonCode = failReasonCode; }

    public String getFailReasonDescription() { return failReasonDescription; }
    public void setFailReasonDescription(String failReasonDescription) { this.failReasonDescription = failReasonDescription; }

    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public void setRejectedAt(LocalDateTime rejectedAt) { this.rejectedAt = rejectedAt; }
}
