package com.carddemo.batch.cbact01c.model;

import java.math.BigDecimal;

/**
 * Variable-length records written to VBRCFILE.
 * Two record types are written per account:
 *
 * <pre>
 * Type 1 (12 bytes — VBRC-REC1):
 *   05  VB1-ACCT-ID             PIC 9(11)
 *   05  VB1-ACCT-ACTIVE-STATUS  PIC X(01)
 *
 * Type 2 (39 bytes — VBRC-REC2):
 *   05  VB2-ACCT-ID             PIC 9(11)
 *   05  VB2-ACCT-CURR-BAL       PIC S9(10)V99
 *   05  VB2-ACCT-CREDIT-LIMIT   PIC S9(10)V99
 *   05  VB2-ACCT-REISSUE-YYYY   PIC X(04)
 * </pre>
 */
public sealed interface VbrRecord {

    record Type1(long acctId, String activeStatus) implements VbrRecord {
    }

    record Type2(long acctId, BigDecimal currBal, BigDecimal creditLimit,
                 String reissueYyyy) implements VbrRecord {
    }
}
