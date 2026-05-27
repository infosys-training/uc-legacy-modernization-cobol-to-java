package com.carddemo.batch.cbact01c;

import com.carddemo.batch.cbact01c.model.AccountRecord;
import com.carddemo.batch.cbact01c.model.ArrayAccountRecord;
import com.carddemo.batch.cbact01c.model.ArrayAccountRecord.BalanceEntry;
import com.carddemo.batch.cbact01c.model.OutputAccountRecord;
import com.carddemo.batch.cbact01c.model.VbrRecord;
import com.carddemo.batch.cbact01c.util.DateFormatter;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * Core business logic extracted from CBACT01C.cbl.
 *
 * For each input account record, produces:
 * <ol>
 *   <li>An {@link OutputAccountRecord} — flat extract with date reformatting</li>
 *   <li>An {@link ArrayAccountRecord} — 5-element balance array with fixed test values</li>
 *   <li>A {@link VbrRecord.Type1} — short record (acct ID + status)</li>
 *   <li>A {@link VbrRecord.Type2} — medium record (acct ID + bal + limit + year)</li>
 * </ol>
 *
 * Business rules preserved from COBOL:
 * <ul>
 *   <li>If ACCT-CURR-CYC-DEBIT = 0, substitute 2525.00 (paragraph 1300)</li>
 *   <li>Reissue date: YYYY-MM-DD → YYYYMMDD via COBDATFT (paragraph 1300)</li>
 *   <li>Array slots 1-2: actual balance, fixed debits (1005.00, 1525.00)</li>
 *   <li>Array slot 3: fixed values (-1025.00, -2500.00)</li>
 *   <li>Array slots 4-5: zeroes (INITIALIZE)</li>
 *   <li>VBR Type2 reissue year: extracted from WS-ACCT-REISSUE-YYYY</li>
 * </ul>
 */
public class AccountProcessor {

    private static final Logger LOG = Logger.getLogger(AccountProcessor.class.getName());

    private static final BigDecimal DEFAULT_CYC_DEBIT = new BigDecimal("2525.00");
    private static final BigDecimal ARR_DEBIT_1 = new BigDecimal("1005.00");
    private static final BigDecimal ARR_DEBIT_2 = new BigDecimal("1525.00");
    private static final BigDecimal ARR_BAL_3 = new BigDecimal("-1025.00");
    private static final BigDecimal ARR_DEBIT_3 = new BigDecimal("-2500.00");

    /**
     * Transforms an input account record into the output record.
     * Mirrors COBOL paragraphs 1300-POPUL-ACCT-RECORD.
     */
    public OutputAccountRecord toOutputRecord(AccountRecord input) {
        String reformattedReissueDate = DateFormatter.toCompactDate(input.reissueDate());

        BigDecimal cycDebit = input.currCycDebit();
        if (cycDebit.signum() == 0) {
            cycDebit = DEFAULT_CYC_DEBIT;
        }

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
     * Transforms an input account record into the array record.
     * Mirrors COBOL paragraph 1400-POPUL-ARRAY-RECORD.
     */
    public ArrayAccountRecord toArrayRecord(AccountRecord input) {
        BalanceEntry[] entries = new BalanceEntry[ArrayAccountRecord.OCCURS_COUNT];

        entries[0] = new BalanceEntry(input.currBal(), ARR_DEBIT_1);
        entries[1] = new BalanceEntry(input.currBal(), ARR_DEBIT_2);
        entries[2] = new BalanceEntry(ARR_BAL_3, ARR_DEBIT_3);
        entries[3] = BalanceEntry.ZERO;
        entries[4] = BalanceEntry.ZERO;

        return new ArrayAccountRecord(input.acctId(), entries);
    }

    /**
     * Creates the short VBR record (Type1).
     * Mirrors COBOL paragraph 1500-POPUL-VBRC-RECORD (VB1 portion).
     */
    public VbrRecord.Type1 toVbrType1(AccountRecord input) {
        return new VbrRecord.Type1(input.acctId(), input.activeStatus());
    }

    /**
     * Creates the medium VBR record (Type2).
     * Mirrors COBOL paragraph 1500-POPUL-VBRC-RECORD (VB2 portion).
     */
    public VbrRecord.Type2 toVbrType2(AccountRecord input) {
        String reissueYear = DateFormatter.extractYear(input.reissueDate());
        return new VbrRecord.Type2(
                input.acctId(),
                input.currBal(),
                input.creditLimit(),
                reissueYear
        );
    }

    /**
     * Logs account record fields to stdout.
     * Mirrors COBOL paragraph 1100-DISPLAY-ACCT-RECORD.
     */
    public void displayRecord(AccountRecord rec) {
        LOG.info("ACCT-ID                 :" + String.format("%011d", rec.acctId()));
        LOG.info("ACCT-ACTIVE-STATUS      :" + rec.activeStatus());
        LOG.info("ACCT-CURR-BAL           :" + rec.currBal().toPlainString());
        LOG.info("ACCT-CREDIT-LIMIT       :" + rec.creditLimit().toPlainString());
        LOG.info("ACCT-CASH-CREDIT-LIMIT  :" + rec.cashCreditLimit().toPlainString());
        LOG.info("ACCT-OPEN-DATE          :" + rec.openDate());
        LOG.info("ACCT-EXPIRAION-DATE     :" + rec.expirationDate());
        LOG.info("ACCT-REISSUE-DATE       :" + rec.reissueDate());
        LOG.info("ACCT-CURR-CYC-CREDIT    :" + rec.currCycCredit().toPlainString());
        LOG.info("ACCT-CURR-CYC-DEBIT     :" + rec.currCycDebit().toPlainString());
        LOG.info("ACCT-GROUP-ID           :" + rec.groupId());
        LOG.info("-------------------------------------------------");
    }
}
