package com.cardemo.batch.cbact01c.model;

import java.math.BigDecimal;

/**
 * Corresponds to COBOL VBRC-REC2 — the long variable-length record (39 bytes).
 * Contains account ID, current balance, credit limit, and reissue year.
 */
public record VariableRecordLong(
        String acctId,
        BigDecimal currBal,
        BigDecimal creditLimit,
        String reissueYear
) {}
