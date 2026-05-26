package com.carddemo.service;

import com.carddemo.model.AccountRecord;
import com.carddemo.model.ArrayRecord;
import com.carddemo.model.OutputAccountRecord;
import com.carddemo.model.VariableRecord1;
import com.carddemo.model.VariableRecord2;
import com.carddemo.util.CobolDateFormatter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Replicates the business logic of CBACT01C.cbl PROCEDURE DIVISION.
 *
 * For each account record read from the input file, the COBOL program:
 *   1. Displays the record (1100-DISPLAY-ACCT-RECORD)
 *   2. Populates an output record with date reformatting (1300-POPUL-ACCT-RECORD)
 *      - If ACCT-CURR-CYC-DEBIT == 0, overrides it with 2525.00
 *      - Calls COBDATFT to reformat reissue date from YYYY-MM-DD to YYYYMMDD
 *   3. Writes the output record (1350-WRITE-ACCT-RECORD)
 *   4. Populates an array record with hardcoded test values (1400-POPUL-ARRAY-RECORD)
 *      - Index 1: bal=account balance, debit=1005.00
 *      - Index 2: bal=account balance, debit=1525.00
 *      - Index 3: bal=-1025.00,        debit=-2500.00
 *   5. Writes the array record (1450-WRITE-ARRY-RECORD)
 *   6. Populates two variable-length records (1500-POPUL-VBRC-RECORD)
 *   7. Writes both VB records (1550/1575-WRITE-VBx-RECORD)
 */
public class AccountProcessor {

    public record ProcessingResult(
            List<OutputAccountRecord> outputRecords,
            List<ArrayRecord> arrayRecords,
            List<VariableRecord1> variableRecords1,
            List<VariableRecord2> variableRecords2,
            List<String> displayOutput
    ) {}

    public ProcessingResult process(List<AccountRecord> inputRecords) {
        List<OutputAccountRecord> outputRecords = new ArrayList<>();
        List<ArrayRecord> arrayRecords = new ArrayList<>();
        List<VariableRecord1> vbRecords1 = new ArrayList<>();
        List<VariableRecord2> vbRecords2 = new ArrayList<>();
        List<String> displayOutput = new ArrayList<>();

        for (AccountRecord acct : inputRecords) {
            // 1100-DISPLAY-ACCT-RECORD
            displayOutput.addAll(displayAccountRecord(acct));

            // 1300-POPUL-ACCT-RECORD + 1350-WRITE-ACCT-RECORD
            OutputAccountRecord outRec = populateOutputRecord(acct);
            outputRecords.add(outRec);

            // 1400-POPUL-ARRAY-RECORD + 1450-WRITE-ARRY-RECORD
            ArrayRecord arrRec = populateArrayRecord(acct);
            arrayRecords.add(arrRec);

            // 1500-POPUL-VBRC-RECORD + 1550/1575-WRITE
            VariableRecord1 vb1 = populateVbRecord1(acct);
            VariableRecord2 vb2 = populateVbRecord2(acct);
            vbRecords1.add(vb1);
            vbRecords2.add(vb2);
        }

        return new ProcessingResult(outputRecords, arrayRecords, vbRecords1, vbRecords2, displayOutput);
    }

    /**
     * 1100-DISPLAY-ACCT-RECORD: display all fields to console.
     */
    List<String> displayAccountRecord(AccountRecord acct) {
        List<String> lines = new ArrayList<>();
        lines.add("ACCT-ID                 :" + String.format("%011d", acct.getAcctId()));
        lines.add("ACCT-ACTIVE-STATUS      :" + acct.getActiveStatus());
        lines.add("ACCT-CURR-BAL           :" + acct.getCurrentBalance());
        lines.add("ACCT-CREDIT-LIMIT       :" + acct.getCreditLimit());
        lines.add("ACCT-CASH-CREDIT-LIMIT  :" + acct.getCashCreditLimit());
        lines.add("ACCT-OPEN-DATE          :" + acct.getOpenDate());
        lines.add("ACCT-EXPIRAION-DATE     :" + acct.getExpirationDate());
        lines.add("ACCT-REISSUE-DATE       :" + acct.getReissueDate());
        lines.add("ACCT-CURR-CYC-CREDIT    :" + acct.getCurrentCycleCredit());
        lines.add("ACCT-CURR-CYC-DEBIT     :" + acct.getCurrentCycleDebit());
        lines.add("ACCT-GROUP-ID           :" + acct.getGroupId());
        lines.add("-------------------------------------------------");
        return lines;
    }

    /**
     * 1300-POPUL-ACCT-RECORD: populate output record.
     *
     * Key business rules:
     *   - Reissue date is reformatted from YYYY-MM-DD to YYYYMMDD via COBDATFT
     *   - If cycle debit is zero, override with 2525.00
     */
    OutputAccountRecord populateOutputRecord(AccountRecord acct) {
        OutputAccountRecord out = new OutputAccountRecord();
        out.setAcctId(acct.getAcctId());
        out.setActiveStatus(acct.getActiveStatus());
        out.setCurrentBalance(acct.getCurrentBalance());
        out.setCreditLimit(acct.getCreditLimit());
        out.setCashCreditLimit(acct.getCashCreditLimit());
        out.setOpenDate(acct.getOpenDate());
        out.setExpirationDate(acct.getExpirationDate());

        // CALL 'COBDATFT' — reformat reissue date: YYYY-MM-DD (type 2) → YYYYMMDD (outtype 2)
        String reformattedDate = CobolDateFormatter.formatDate(acct.getReissueDate(), '2', '2');
        out.setReissueDate(reformattedDate);

        out.setCurrentCycleCredit(acct.getCurrentCycleCredit());

        // Business rule: if debit is zero, set to 2525.00
        if (acct.getCurrentCycleDebit().compareTo(BigDecimal.ZERO) == 0) {
            out.setCurrentCycleDebit(new BigDecimal("2525.00"));
        } else {
            out.setCurrentCycleDebit(acct.getCurrentCycleDebit());
        }

        out.setGroupId(acct.getGroupId());
        return out;
    }

    /**
     * 1400-POPUL-ARRAY-RECORD: populate array with hardcoded test values.
     *
     * COBOL logic (1-based indices):
     *   ARR-ACCT-CURR-BAL(1) = ACCT-CURR-BAL,   ARR-ACCT-CURR-CYC-DEBIT(1) = 1005.00
     *   ARR-ACCT-CURR-BAL(2) = ACCT-CURR-BAL,   ARR-ACCT-CURR-CYC-DEBIT(2) = 1525.00
     *   ARR-ACCT-CURR-BAL(3) = -1025.00,         ARR-ACCT-CURR-CYC-DEBIT(3) = -2500.00
     *   Indices 4-5 remain at zero (INITIALIZE ARR-ARRAY-REC clears them)
     */
    ArrayRecord populateArrayRecord(AccountRecord acct) {
        ArrayRecord arr = new ArrayRecord();
        arr.setAcctId(acct.getAcctId());

        // Index 0 (COBOL index 1)
        arr.setBalance(0, acct.getCurrentBalance());
        arr.setCycleDebit(0, new BigDecimal("1005.00"));

        // Index 1 (COBOL index 2)
        arr.setBalance(1, acct.getCurrentBalance());
        arr.setCycleDebit(1, new BigDecimal("1525.00"));

        // Index 2 (COBOL index 3)
        arr.setBalance(2, new BigDecimal("-1025.00"));
        arr.setCycleDebit(2, new BigDecimal("-2500.00"));

        // Indices 3-4 (COBOL 4-5) remain zero from constructor
        return arr;
    }

    /**
     * 1500-POPUL-VBRC-RECORD: populate variable-length record type 1.
     */
    VariableRecord1 populateVbRecord1(AccountRecord acct) {
        return new VariableRecord1(acct.getAcctId(), acct.getActiveStatus());
    }

    /**
     * 1500-POPUL-VBRC-RECORD: populate variable-length record type 2.
     * VB2-ACCT-REISSUE-YYYY comes from WS-ACCT-REISSUE-YYYY (first 4 chars of reissue date).
     */
    VariableRecord2 populateVbRecord2(AccountRecord acct) {
        String reissueYear = "";
        if (acct.getReissueDate() != null && acct.getReissueDate().length() >= 4) {
            reissueYear = acct.getReissueDate().substring(0, 4);
        }
        return new VariableRecord2(
                acct.getAcctId(),
                acct.getCurrentBalance(),
                acct.getCreditLimit(),
                reissueYear
        );
    }
}
