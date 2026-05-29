package com.cardemo.batch;

import com.cardemo.batch.io.AccountFileReader;
import com.cardemo.batch.io.OutputFileWriter;
import com.cardemo.batch.model.AccountRecord;
import com.cardemo.batch.model.ArrayRecord;
import com.cardemo.batch.model.ArrayRecord.Entry;
import com.cardemo.batch.model.OutAccountRecord;
import com.cardemo.batch.model.VbrcRecord1;
import com.cardemo.batch.model.VbrcRecord2;
import com.cardemo.batch.util.DateConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Java 17 modernisation of the COBOL batch program {@code CBACT01C.CBL}.
 *
 * <p>The program reads an account master file (VSAM KSDS in COBOL, fixed-width
 * text in Java) and produces three output files:
 * <ol>
 *   <li><b>OUTFILE</b> — flat account extract with a reformatted reissue date
 *       and a defaulted cycle-debit value.</li>
 *   <li><b>ARRYFILE</b> — array-structured balance file (5 balance/debit
 *       pairs per account, indices 4-5 zeroed).</li>
 *   <li><b>VBRCFILE</b> — two variable-length records per account: a short
 *       record (id + status) and a long record (id + balances + reissue
 *       year).</li>
 * </ol>
 */
public final class Cbact01c {

    private static final Logger LOG = Logger.getLogger(Cbact01c.class.getName());

    private static final BigDecimal DEFAULT_CYC_DEBIT = new BigDecimal("2525.00");
    private static final BigDecimal ARR_DEBIT_1 = new BigDecimal("1005.00");
    private static final BigDecimal ARR_DEBIT_2 = new BigDecimal("1525.00");
    private static final BigDecimal ARR_BAL_3 = new BigDecimal("-1025.00");
    private static final BigDecimal ARR_DEBIT_3 = new BigDecimal("-2500.00");

    private int recordsProcessed;

    public int getRecordsProcessed() {
        return recordsProcessed;
    }

    /**
     * Executes the batch job.
     *
     * @param acctFilePath path to the fixed-width account input file
     * @param outFilePath  path for the account extract output
     * @param arryFilePath path for the array-structured output
     * @param vbrcFilePath path for the variable-length record output
     */
    public void execute(Path acctFilePath, Path outFilePath,
                        Path arryFilePath, Path vbrcFilePath) throws IOException {
        LOG.info("START OF EXECUTION OF PROGRAM CBACT01C");
        recordsProcessed = 0;

        try (var reader = new AccountFileReader(acctFilePath);
             var outWriter = new OutputFileWriter(outFilePath);
             var arryWriter = new OutputFileWriter(arryFilePath);
             var vbrcWriter = new OutputFileWriter(vbrcFilePath)) {

            for (AccountRecord acct : reader) {
                displayAccountRecord(acct);

                OutAccountRecord outRec = populateOutRecord(acct);
                outWriter.writeOutRecord(outRec);

                ArrayRecord arrRec = populateArrayRecord(acct);
                arryWriter.writeArrayRecord(arrRec);

                VbrcRecord1 vb1 = populateVbrc1(acct);
                VbrcRecord2 vb2 = populateVbrc2(acct);
                vbrcWriter.writeVbrc1(vb1);
                vbrcWriter.writeVbrc2(vb2);

                recordsProcessed++;
            }
        }

        LOG.info("END OF EXECUTION OF PROGRAM CBACT01C");
    }

    // -- Business-logic methods (package-private for testability) ----------

    /**
     * Mirrors COBOL paragraph {@code 1300-POPUL-ACCT-RECORD}.
     * Converts the reissue date from YYYY-MM-DD to YYYYMMDD and defaults
     * the cycle debit to 2525.00 when the input value is zero.
     */
    OutAccountRecord populateOutRecord(AccountRecord acct) {
        String convertedReissueDate = convertReissueDate(acct.reissueDate());

        BigDecimal cycDebit = acct.currCycDebit().signum() == 0
                ? DEFAULT_CYC_DEBIT
                : acct.currCycDebit();

        return new OutAccountRecord(
                acct.acctId(),
                acct.activeStatus(),
                acct.currBal(),
                acct.creditLimit(),
                acct.cashCreditLimit(),
                acct.openDate(),
                acct.expirationDate(),
                convertedReissueDate,
                acct.currCycCredit(),
                cycDebit,
                acct.groupId());
    }

    /**
     * Mirrors COBOL paragraph {@code 1400-POPUL-ARRAY-RECORD}.
     * Populates indices 1-3 with specific values; 4-5 are zeroed
     * (COBOL INITIALIZE).
     */
    ArrayRecord populateArrayRecord(AccountRecord acct) {
        List<Entry> entries = new ArrayList<>(ArrayRecord.ENTRY_COUNT);
        entries.add(new Entry(acct.currBal(), ARR_DEBIT_1));
        entries.add(new Entry(acct.currBal(), ARR_DEBIT_2));
        entries.add(new Entry(ARR_BAL_3, ARR_DEBIT_3));
        entries.add(Entry.ZERO);
        entries.add(Entry.ZERO);
        return new ArrayRecord(acct.acctId(), List.copyOf(entries));
    }

    /**
     * Mirrors COBOL paragraph {@code 1500-POPUL-VBRC-RECORD} — short record.
     */
    VbrcRecord1 populateVbrc1(AccountRecord acct) {
        return new VbrcRecord1(acct.acctId(), acct.activeStatus());
    }

    /**
     * Mirrors COBOL paragraph {@code 1500-POPUL-VBRC-RECORD} — long record.
     * Extracts the 4-character year from the reissue date.
     */
    VbrcRecord2 populateVbrc2(AccountRecord acct) {
        String reissueYear = acct.reissueDate().length() >= 4
                ? acct.reissueDate().substring(0, 4)
                : acct.reissueDate();

        return new VbrcRecord2(
                acct.acctId(),
                acct.currBal(),
                acct.creditLimit(),
                reissueYear);
    }

    /**
     * Converts a reissue date from {@code YYYY-MM-DD} to {@code YYYYMMDD}
     * by calling the date-conversion utility (replaces {@code COBDATFT}).
     * The COBOL code passes type "2" (YYYY-MM-DD input) and outtype "2"
     * (YYYYMMDD output).  The result is then truncated/padded to 10 chars
     * (matching PIC X(10) of OUT-ACCT-REISSUE-DATE).
     */
    static String convertReissueDate(String reissueDate) {
        String converted = DateConverter.convert(reissueDate, "2", "2");
        if (converted.length() < 10) {
            converted = converted + " ".repeat(10 - converted.length());
        } else if (converted.length() > 10) {
            converted = converted.substring(0, 10);
        }
        return converted;
    }

    private void displayAccountRecord(AccountRecord acct) {
        if (!LOG.isLoggable(Level.FINE)) return;
        LOG.fine("ACCT-ID                 :" + acct.acctId());
        LOG.fine("ACCT-ACTIVE-STATUS      :" + acct.activeStatus());
        LOG.fine("ACCT-CURR-BAL           :" + acct.currBal());
        LOG.fine("ACCT-CREDIT-LIMIT       :" + acct.creditLimit());
        LOG.fine("ACCT-CASH-CREDIT-LIMIT  :" + acct.cashCreditLimit());
        LOG.fine("ACCT-OPEN-DATE          :" + acct.openDate());
        LOG.fine("ACCT-EXPIRAION-DATE     :" + acct.expirationDate());
        LOG.fine("ACCT-REISSUE-DATE       :" + acct.reissueDate());
        LOG.fine("ACCT-CURR-CYC-CREDIT    :" + acct.currCycCredit());
        LOG.fine("ACCT-CURR-CYC-DEBIT     :" + acct.currCycDebit());
        LOG.fine("ACCT-GROUP-ID           :" + acct.groupId());
        LOG.fine("-------------------------------------------------");
    }

    // -- CLI entry point --------------------------------------------------

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.err.println(
                    "Usage: Cbact01c <acctFile> <outFile> <arryFile> <vbrcFile>");
            System.exit(1);
        }

        var program = new Cbact01c();
        program.execute(
                Path.of(args[0]),
                Path.of(args[1]),
                Path.of(args[2]),
                Path.of(args[3]));

        System.out.printf("Processed %d records.%n", program.getRecordsProcessed());
    }
}
