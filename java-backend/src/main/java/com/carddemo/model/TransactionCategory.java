package com.carddemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

/**
 * Maps to COBOL copybook CVTRA04Y.cpy — TRAN-CAT-RECORD (60-byte).
 *
 * COBOL layout:
 *   TRAN-CAT-KEY:
 *     TRAN-TYPE-CD        PIC X(02)
 *     TRAN-CAT-CD         PIC 9(04)
 *   TRAN-CAT-TYPE-DESC    PIC X(50)
 */
@Entity
@Table(name = "transaction_categories")
public class TransactionCategory {

    @EmbeddedId
    private TransactionCategoryId id;

    @Column(name = "cat_desc", length = 50)
    private String description;

    public TransactionCategory() {}

    public TransactionCategoryId getId() { return id; }
    public void setId(TransactionCategoryId id) { this.id = id; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Embeddable
    public static class TransactionCategoryId implements Serializable {

        @Column(name = "type_cd", length = 2)
        private String typeCode;

        @Column(name = "cat_cd")
        private Integer categoryCode;

        public TransactionCategoryId() {}

        public TransactionCategoryId(String typeCode, Integer categoryCode) {
            this.typeCode = typeCode;
            this.categoryCode = categoryCode;
        }

        public String getTypeCode() { return typeCode; }
        public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

        public Integer getCategoryCode() { return categoryCode; }
        public void setCategoryCode(Integer categoryCode) { this.categoryCode = categoryCode; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof TransactionCategoryId that)) return false;
            return Objects.equals(typeCode, that.typeCode) && Objects.equals(categoryCode, that.categoryCode);
        }

        @Override
        public int hashCode() {
            return Objects.hash(typeCode, categoryCode);
        }
    }
}
