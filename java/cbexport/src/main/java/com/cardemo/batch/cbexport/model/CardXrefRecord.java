package com.cardemo.batch.cbexport.model;

/**
 * Maps to CVACT03Y.cpy — Card cross-reference record (RECLN 50).
 */
public record CardXrefRecord(
        String cardNum,
        int custId,
        String acctId
) {}
