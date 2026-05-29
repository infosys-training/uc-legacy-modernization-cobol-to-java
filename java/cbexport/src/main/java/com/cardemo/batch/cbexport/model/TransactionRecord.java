package com.cardemo.batch.cbexport.model;

import java.math.BigDecimal;

/**
 * Maps to CVTRA05Y.cpy — Transaction record (RECLN 350).
 */
public record TransactionRecord(
        String tranId,
        String typeCode,
        int categoryCode,
        String source,
        String description,
        BigDecimal amount,
        int merchantId,
        String merchantName,
        String merchantCity,
        String merchantZip,
        String cardNum,
        String origTimestamp,
        String procTimestamp
) {}
