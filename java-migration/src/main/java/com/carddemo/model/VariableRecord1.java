package com.carddemo.model;

/**
 * Maps COBOL VBRC-REC1 (variable-length record type 1, 12 bytes).
 *
 * COBOL layout:
 *   05 VB1-ACCT-ID              PIC 9(11)
 *   05 VB1-ACCT-ACTIVE-STATUS   PIC X(01)
 */
public record VariableRecord1(long acctId, char activeStatus) {

    @Override
    public String toString() {
        return String.format("%011d%c", acctId, activeStatus);
    }
}
