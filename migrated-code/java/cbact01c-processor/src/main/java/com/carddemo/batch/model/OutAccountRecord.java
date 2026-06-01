package com.carddemo.batch.model;

import java.math.BigDecimal;

/**
 * Maps OUT-ACCT-REC (LRECL=107) — output file record from CBACT01C.
 *
 * Field layout (total 107 bytes):
 *   acctId:             11 bytes (PIC 9(11))
 *   activeStatus:       1 byte  (PIC X(01))
 *   currentBalance:     12 bytes (PIC S9(10)V99 zoned decimal)
 *   creditLimit:        12 bytes (PIC S9(10)V99 zoned decimal)
 *   cashCreditLimit:    12 bytes (PIC S9(10)V99 zoned decimal)
 *   openDate:           10 bytes (PIC X(10))
 *   expirationDate:     10 bytes (PIC X(10))
 *   reissueDate:        10 bytes (PIC X(10))
 *   currentCycleCredit: 12 bytes (PIC S9(10)V99 zoned decimal)
 *   currentCycleDebit:  7 bytes  (PIC S9(10)V99 COMP-3 packed decimal)
 *   groupId:            10 bytes (PIC X(10))
 */
public record OutAccountRecord(
        String acctId,
        String activeStatus,
        BigDecimal currentBalance,
        BigDecimal creditLimit,
        BigDecimal cashCreditLimit,
        String openDate,
        String expirationDate,
        String reissueDate,
        BigDecimal currentCycleCredit,
        BigDecimal currentCycleDebit,
        String groupId
) {}
