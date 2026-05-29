package com.cardemo.batch.cbact01c.model;

/**
 * Corresponds to COBOL VBRC-REC1 — the short variable-length record (12 bytes).
 * Contains account ID and active status.
 */
public record VariableRecordShort(
        String acctId,
        String activeStatus
) {}
