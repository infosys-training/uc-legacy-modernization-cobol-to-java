package com.cardemo.batch.processor;

import com.cardemo.batch.model.AccountRecord;
import com.cardemo.batch.model.ArrayAccountRecord;
import com.cardemo.batch.model.ArrayAccountRecord.BalanceEntry;
import com.cardemo.batch.model.OutAccountRecord;
import com.cardemo.batch.model.ProcessingResult;
import com.cardemo.batch.model.VbrcRecord1;
import com.cardemo.batch.model.VbrcRecord2;
import com.cardemo.batch.util.DateConverter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Core business logic ported from CBACT01C paragraphs 1300 through 1500.
 * <p>
 * For each input {@link AccountRecord} the processor produces:
 * <ol>
 *   <li>An {@link OutAccountRecord} &mdash; selected fields with date reformatting
 *       and a default debit of 2525.00 when the input debit is zero.</li>
 *   <li>An {@link ArrayAccountRecord} &mdash; account ID plus five balance/debit
 *       pairs (indices 0-2 populated, 3-4 zeroed).</li>
 *   <li>A {@link VbrcRecord1} &mdash; account ID and active status.</li>
 *   <li>A {@link VbrcRecord2} &mdash; account ID, balance, credit limit and
 *       four-digit reissue year.</li>
 * </ol>
 */
public final class AccountProcessor {

    private static final BigDecimal DEFAULT_DEBIT = new BigDecimal("2525.00");
    private static final BigDecimal ARRAY_DEBIT_1 = new BigDecimal("1005.00");
    private static final BigDecimal ARRAY_DEBIT_2 = new BigDecimal("1525.00");
    private static final BigDecimal ARRAY_BAL_3   = new BigDecimal("-1025.00");
    private static final BigDecimal ARRAY_DEBIT_3 = new BigDecimal("-2500.00");

    public ProcessingResult process(AccountRecord acct) {
        return new ProcessingResult(
                buildOutRecord(acct),
                buildArrayRecord(acct),
                buildVbrc1(acct),
                buildVbrc2(acct)
        );
    }

    /**
     * Mirrors COBOL paragraph 1300-POPUL-ACCT-RECORD.
     * <ul>
     *   <li>Reissue date converted from YYYY-MM-DD to YYYYMMDD (padded to 10 chars
     *       to match the PIC X(10) output field).</li>
     *   <li>If input {@code currCycDebit} is zero, output defaults to 2525.00.</li>
     * </ul>
     */
    OutAccountRecord buildOutRecord(AccountRecord acct) {
        String compactDate = DateConverter.hyphenatedToCompact(acct.reissueDate());
        String reissueDateOut = padRight(compactDate, 10);

        BigDecimal debit = acct.currCycDebit().signum() == 0
                ? DEFAULT_DEBIT
                : acct.currCycDebit();

        return new OutAccountRecord(
                acct.acctId(),
                acct.activeStatus(),
                acct.currBal(),
                acct.creditLimit(),
                acct.cashCreditLimit(),
                acct.openDate(),
                acct.expirationDate(),
                reissueDateOut,
                acct.currCycCredit(),
                debit,
                acct.groupId()
        );
    }

    /**
     * Mirrors COBOL paragraph 1400-POPUL-ARRAY-RECORD.
     * <p>
     * Indices 0 and 1 use the account's current balance; indices 0-2 use
     * hard-coded debit amounts. Indices 3-4 remain zeroed (COBOL INITIALIZE).
     */
    ArrayAccountRecord buildArrayRecord(AccountRecord acct) {
        List<BalanceEntry> entries = List.of(
                new BalanceEntry(acct.currBal(), ARRAY_DEBIT_1),
                new BalanceEntry(acct.currBal(), ARRAY_DEBIT_2),
                new BalanceEntry(ARRAY_BAL_3,    ARRAY_DEBIT_3),
                BalanceEntry.ZERO,
                BalanceEntry.ZERO
        );
        return new ArrayAccountRecord(acct.acctId(), entries);
    }

    /**
     * Mirrors COBOL paragraph 1500-POPUL-VBRC-RECORD (first record).
     */
    VbrcRecord1 buildVbrc1(AccountRecord acct) {
        return new VbrcRecord1(acct.acctId(), acct.activeStatus());
    }

    /**
     * Mirrors COBOL paragraph 1500-POPUL-VBRC-RECORD (second record).
     * Extracts the four-digit year from the reissue date.
     */
    VbrcRecord2 buildVbrc2(AccountRecord acct) {
        String year = DateConverter.extractYear(acct.reissueDate());
        return new VbrcRecord2(
                acct.acctId(),
                acct.currBal(),
                acct.creditLimit(),
                year
        );
    }

    private static String padRight(String s, int width) {
        if (s.length() >= width) return s.substring(0, width);
        return s + " ".repeat(width - s.length());
    }
}
