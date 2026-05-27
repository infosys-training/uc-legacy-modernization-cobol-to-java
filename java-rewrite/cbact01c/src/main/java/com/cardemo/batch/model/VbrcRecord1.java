package com.cardemo.batch.model;

/**
 * Short variable-length record (12 bytes in COBOL) written to VBRC-FILE.
 * Contains only the account identifier and active status.
 */
public record VbrcRecord1(
    String acctId,
    String activeStatus
) {}
