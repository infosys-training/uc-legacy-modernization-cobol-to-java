package com.cardemo.batch.model;

/**
 * Short variable-length record (12 bytes in COBOL) written to the VBRC file.
 *
 * <pre>
 *   VB1-ACCT-ID              PIC 9(11)
 *   VB1-ACCT-ACTIVE-STATUS   PIC X(01)
 * </pre>
 */
public record VbrcRecord1(long acctId, String activeStatus) {}
