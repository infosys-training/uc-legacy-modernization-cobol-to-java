package com.carddemo.batch.model;

import java.math.BigDecimal;

/**
 * Longer variable-length record (39 bytes) written to the VBR file.
 * Contains account ID, current balance, credit limit, and reissue year.
 */
public record VbRecord2(
        long acctId,
        BigDecimal currBal,
        BigDecimal creditLimit,
        String reissueYear
) {}
