package com.carddemo.batch.model;

import java.util.List;

/**
 * Maps ARR-ARRAY-REC (LRECL=110) — array output record from CBACT01C.
 *
 * Field layout (total 110 bytes):
 *   acctId:   11 bytes (PIC 9(11))
 *   elements: 5 × (12 + 7) = 95 bytes (OCCURS 5 TIMES)
 *   filler:   4 bytes (PIC X(04))
 */
public record ArrayRecord(
        String acctId,
        List<ArrayElement> elements,
        String filler
) {}
