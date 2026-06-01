package com.carddemo.batch.model;

import java.math.BigDecimal;

/**
 * Maps CVACT01Y.cpy — 300-byte input account record from acctdata.txt.
 *
 * Field layout (total 300 bytes):
 *   acctId:             0-10  (11 bytes, PIC 9(11))
 *   activeStatus:       11    (1 byte, PIC X(01))
 *   currentBalance:     12-23 (12 bytes, PIC S9(10)V99 zoned decimal)
 *   creditLimit:        24-35 (12 bytes, PIC S9(10)V99)
 *   cashCreditLimit:    36-47 (12 bytes, PIC S9(10)V99)
 *   openDate:           48-57 (10 bytes, PIC X(10))
 *   expirationDate:     58-67 (10 bytes, PIC X(10))
 *   reissueDate:        68-77 (10 bytes, PIC X(10))
 *   currentCycleCredit: 78-89 (12 bytes, PIC S9(10)V99)
 *   currentCycleDebit:  90-101 (12 bytes, PIC S9(10)V99)
 *   addressZip:         102-111 (10 bytes, PIC X(10))
 *   groupId:            112-121 (10 bytes, PIC X(10))
 *   FILLER:             122-299 (178 bytes)
 */
public record AccountRecord(
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
        String addressZip,
        String groupId
) {}
