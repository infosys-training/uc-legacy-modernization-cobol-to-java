package com.cardemo.batch.cbact01c.service;

import com.cardemo.batch.cbact01c.model.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * Core business logic ported from CBACT01C paragraphs 1300 through 1500.
 * Transforms an input {@link AccountRecord} into the three output structures.
 */
public final class AccountProcessor {

    private static final BigDecimal DEFAULT_DEBIT = new BigDecimal("2525.00");
    private static final BigDecimal ARRAY_DEBIT_1 = new BigDecimal("1005.00");
    private static final BigDecimal ARRAY_DEBIT_2 = new BigDecimal("1525.00");
    private static final BigDecimal ARRAY_BAL_3   = new BigDecimal("-1025.00");
    private static final BigDecimal ARRAY_DEBIT_3 = new BigDecimal("-2500.00");

    private AccountProcessor() {}

    /**
     * 1300-POPUL-ACCT-RECORD: builds the flat output record.
     *
     * <p>Business rules:
     * <ul>
     *   <li>Reissue date is reformatted from YYYY-MM-DD → YYYYMMDD
     *       (via COBDATFT with type 2→2)</li>
     *   <li>If current cycle debit is zero, it defaults to 2525.00</li>
     * </ul>
     */
    public static OutputAccountRecord buildOutputRecord(AccountRecord input) {
        // Date reformatting: YYYY-MM-DD → YYYYMMDD (COBDATFT type 2 in, type 2 out)
        String reformattedReissueDate = DateFormatter.formatDate(
                input.reissueDate(), "2", "2");

        // Debit defaulting: zero → 2525.00
        BigDecimal cycDebit = input.currCycDebit().compareTo(BigDecimal.ZERO) == 0
                ? DEFAULT_DEBIT
                : input.currCycDebit();

        return new OutputAccountRecord(
                input.acctId(),
                input.activeStatus(),
                input.currBal(),
                input.creditLimit(),
                input.cashCreditLimit(),
                input.openDate(),
                input.expirationDate(),
                reformattedReissueDate,
                input.currCycCredit(),
                cycDebit,
                input.groupId()
        );
    }

    /**
     * 1400-POPUL-ARRAY-RECORD: builds the array output record.
     *
     * <p>Populates 5 balance/debit pairs:
     * <ol>
     *   <li>Actual balance, 1005.00</li>
     *   <li>Actual balance, 1525.00</li>
     *   <li>-1025.00, -2500.00</li>
     *   <li>0.00, 0.00 (initialized)</li>
     *   <li>0.00, 0.00 (initialized)</li>
     * </ol>
     */
    public static ArrayAccountRecord buildArrayRecord(AccountRecord input) {
        List<BalanceEntry> entries = List.of(
                new BalanceEntry(input.currBal(), ARRAY_DEBIT_1),
                new BalanceEntry(input.currBal(), ARRAY_DEBIT_2),
                new BalanceEntry(ARRAY_BAL_3, ARRAY_DEBIT_3),
                new BalanceEntry(BigDecimal.ZERO, BigDecimal.ZERO),
                new BalanceEntry(BigDecimal.ZERO, BigDecimal.ZERO)
        );
        return new ArrayAccountRecord(input.acctId(), entries);
    }

    /**
     * 1500-POPUL-VBRC-RECORD: builds the two variable-length records.
     */
    public static VariableRecordShort buildVbrShortRecord(AccountRecord input) {
        return new VariableRecordShort(input.acctId(), input.activeStatus());
    }

    /**
     * Builds the long variable-length record with reissue year extracted
     * from the original YYYY-MM-DD reissue date.
     */
    public static VariableRecordLong buildVbrLongRecord(AccountRecord input) {
        String reissueYear = DateFormatter.extractYear(input.reissueDate());
        return new VariableRecordLong(
                input.acctId(),
                input.currBal(),
                input.creditLimit(),
                reissueYear
        );
    }
}
