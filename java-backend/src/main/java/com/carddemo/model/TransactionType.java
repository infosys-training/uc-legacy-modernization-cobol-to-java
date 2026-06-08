package com.carddemo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Maps to COBOL copybook CVTRA03Y.cpy — TRAN-TYPE-RECORD (60-byte).
 *
 * COBOL layout:
 *   TRAN-TYPE       PIC X(02)  — Primary key
 *   TRAN-TYPE-DESC  PIC X(50)
 */
@Entity
@Table(name = "transaction_types")
public class TransactionType {

    @Id
    @Column(name = "type_cd", length = 2)
    private String typeCode;

    @Column(name = "type_desc", length = 50)
    private String description;

    public TransactionType() {}

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
