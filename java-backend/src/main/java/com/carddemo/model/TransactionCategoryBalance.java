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
 * Maps to COBOL copybook CVTRA01Y.cpy — TRAN-CAT-BAL-RECORD (50-byte).
 * Running balance per account + transaction type + category.
 *
 * COBOL layout:
 *   TRAN-CAT-KEY:
 *     TRANCAT-ACCT-ID   PIC 9(11)
 *     TRANCAT-TYPE-CD   PIC X(02)
 *     TRANCAT-CD        PIC 9(04)
 *   TRAN-CAT-BAL        PIC S9(09)V99
 */
@Entity
@Table(name = "tran_cat_balances")
public class TransactionCategoryBalance {

    @EmbeddedId
    private TranCatBalanceId id;

    @Column(name = "balance", precision = 11, scale = 2, nullable = false)
    private BigDecimal balance;

    public TransactionCategoryBalance() {}

    public TranCatBalanceId getId() { return id; }
    public void setId(TranCatBalanceId id) { this.id = id; }

    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }

    @Embeddable
    public static class TranCatBalanceId implements Serializable {

        @Column(name = "acct_id")
        private Long accountId;

        @Column(name = "type_cd", length = 2)
        private String typeCode;

        @Column(name = "cat_cd")
        private Integer categoryCode;

        public TranCatBalanceId() {}

        public Long getAccountId() { return accountId; }
        public void setAccountId(Long accountId) { this.accountId = accountId; }

        public String getTypeCode() { return typeCode; }
        public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

        public Integer getCategoryCode() { return categoryCode; }
        public void setCategoryCode(Integer categoryCode) { this.categoryCode = categoryCode; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TranCatBalanceId that)) return false;
            return Objects.equals(accountId, that.accountId)
                    && Objects.equals(typeCode, that.typeCode)
                    && Objects.equals(categoryCode, that.categoryCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(accountId, typeCode, categoryCode);
        }
    }
}
