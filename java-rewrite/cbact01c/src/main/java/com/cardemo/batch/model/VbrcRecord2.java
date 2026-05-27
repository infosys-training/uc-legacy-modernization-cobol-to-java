package com.cardemo.batch.model;

import java.math.BigDecimal;

/**
 * Long variable-length record (39 bytes in COBOL) written to VBRC-FILE.
 * Contains account balance summary and reissue year.
 */
public record VbrcRecord2(
    String acctId,
    BigDecimal currBal,
    BigDecimal creditLimit,
    String reissueYear
) {}
