package com.carddemo.batch.model;

/**
 * Maps VBRC-REC1 (12 bytes) — variable-length record type 1 from CBACT01C.
 *
 *   acctId:       11 bytes (PIC 9(11))
 *   activeStatus: 1 byte  (PIC X(01))
 */
public record VbRecord1(
        String acctId,
        String activeStatus
) {}
