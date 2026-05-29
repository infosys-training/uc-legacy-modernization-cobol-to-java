package com.cardemo.batch.cbexport.model;

/**
 * Maps to CVACT02Y.cpy — Card record (RECLN 150).
 */
public record CardRecord(
        String cardNum,
        String acctId,
        int cvvCode,
        String embossedName,
        String expirationDate,
        String activeStatus
) {}
