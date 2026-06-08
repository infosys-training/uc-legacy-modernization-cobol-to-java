package com.carddemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * Maps to COBOL copybook CVTRA02Y.cpy — DIS-GROUP-RECORD (50-byte).
 * Links account group + transaction type/category to an interest rate.
 *
 * COBOL layout:
 *   DIS-GROUP-KEY:
 *     DIS-ACCT-GROUP-ID   PIC X(10)
 *     DIS-TRAN-TYPE-CD    PIC X(02)
 *     DIS-TRAN-CAT-CD     PIC 9(04)
 *   DIS-INT-RATE           PIC S9(04)V99
 */
@Entity
@Table(name = "disclosure_groups")
public class DisclosureGroup {

    @EmbeddedId
    private DisclosureGroupId id;

    @Column(name = "interest_rate", precision = 6, scale = 2, nullable = false)
    private BigDecimal interestRate;

    public DisclosureGroup() {}

    public DisclosureGroupId getId() { return id; }
    public void setId(DisclosureGroupId id) { this.id = id; }

    public BigDecimal getInterestRate() { return interestRate; }
    public void setInterestRate(BigDecimal interestRate) { this.interestRate = interestRate; }

    @Embeddable
    public static class DisclosureGroupId implements Serializable {

        @Column(name = "acct_group_id", length = 10)
        private String accountGroupId;

        @Column(name = "tran_type_cd", length = 2)
        private String transactionTypeCode;

        @Column(name = "tran_cat_cd")
        private Integer transactionCategoryCode;

        public DisclosureGroupId() {}

        public String getAccountGroupId() { return accountGroupId; }
        public void setAccountGroupId(String accountGroupId) { this.accountGroupId = accountGroupId; }

        public String getTransactionTypeCode() { return transactionTypeCode; }
        public void setTransactionTypeCode(String transactionTypeCode) { this.transactionTypeCode = transactionTypeCode; }

        public Integer getTransactionCategoryCode() { return transactionCategoryCode; }
        public void setTransactionCategoryCode(Integer transactionCategoryCode) { this.transactionCategoryCode = transactionCategoryCode; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof DisclosureGroupId that)) return false;
            return Objects.equals(accountGroupId, that.accountGroupId)
                    && Objects.equals(transactionTypeCode, that.transactionTypeCode)
                    && Objects.equals(transactionCategoryCode, that.transactionCategoryCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accountGroupId, transactionTypeCode, transactionCategoryCode);
        }
    }
}
