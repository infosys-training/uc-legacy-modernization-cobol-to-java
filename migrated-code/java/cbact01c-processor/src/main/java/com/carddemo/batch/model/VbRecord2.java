package com.carddemo.batch.model;

import java.math.BigDecimal;

/**
 * Maps VBRC-REC2 (39 bytes) — variable-length record type 2 from CBACT01C.
 *
 *   acctId:        11 bytes (PIC 9(11))
 *   currentBalance: 12 bytes (PIC S9(10)V99 zoned decimal)
 *   creditLimit:    12 bytes (PIC S9(10)V99 zoned decimal)
 *   reissueYyyy:    4 bytes  (PIC X(04))
 */
public record VbRecord2(
        String acctId,
        BigDecimal currentBalance,
        BigDecimal creditLimit,
        String reissueYyyy
) {}
