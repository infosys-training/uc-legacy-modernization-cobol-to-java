package com.carddemo.batch;

import com.carddemo.batch.io.AccountFileReader;
import com.carddemo.batch.io.DateFormatter;
import com.carddemo.batch.model.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 17+ rewrite of COBOL batch program CBACT01C.
 *
 * <p>Reads an indexed account file (VSAM KSDS in COBOL; line-oriented text
 * in this Java version) and writes three output files:</p>
 * <ol>
 *   <li><b>outFile</b> — flat account records with formatted dates and
 *       defaulted debit values</li>
 *   <li><b>arrayFile</b> — array-structured records with 5 balance/debit
 *       pairs per account</li>
 *   <li><b>vbrFile</b> — variable-length records (two per account: a short
 *       VB1 and a longer VB2)</li>
 * </ol>
 *
 * <h3>Business rules preserved from COBOL:</h3>
 * <ul>
 *   <li>Reissue date is reformatted from YYYY-MM-DD to YYYYMMDD
 *       (originally via COBDATFT assembler call)</li>
 *   <li>If current-cycle debit is zero, it defaults to 2525.00</li>
 *   <li>Array slots 1-2 use actual balance; slot 3 uses hardcoded negatives
 *       (-1025.00 / -2500.00); slots 4-5 are zeroed</li>
 * </ul>
 */
public final class AccountFileProcessor {

    private static final BigDecimal DEFAULT_DEBIT = new BigDecimal("2525.00");
    private static final BigDecimal ARRAY_BAL_3 = new BigDecimal("-1025.00");
    private static final BigDecimal ARRAY_DEBIT_1 = new BigDecimal("1005.00");
    private static final BigDecimal ARRAY_DEBIT_2 = new BigDecimal("1525.00");
    private static final BigDecimal ARRAY_DEBIT_3 = new BigDecimal("-2500.00");

    private final Path acctFilePath;
    private final Path outFilePath;
    private final Path arrayFilePath;
    private final Path vbrFilePath;

    public AccountFileProcessor(Path acctFilePath, Path outFilePath,
                                Path arrayFilePath, Path vbrFilePath) {
        this.acctFilePath = acctFilePath;
        this.outFilePath = outFilePath;
        this.arrayFilePath = arrayFilePath;
        this.vbrFilePath = vbrFilePath;
    }

    /**
     * Executes the batch processing. Returns the number of records processed.
     */
    public int process() throws IOException {
        System.out.println("START OF EXECUTION OF PROGRAM CBACT01C");

        int count = 0;
        try (var reader = new AccountFileReader(acctFilePath);
             var outWriter = new PrintWriter(outFilePath.toFile());
             var arrWriter = new PrintWriter(arrayFilePath.toFile());
             var vbrWriter = new PrintWriter(vbrFilePath.toFile())) {

            AccountRecord acct;
            while ((acct = reader.readNext()) != null) {
                displayAccountRecord(acct);

                OutAccountRecord outRec = buildOutRecord(acct);
                writeOutRecord(outWriter, outRec);

                ArrayRecord arrRec = buildArrayRecord(acct);
                writeArrayRecord(arrWriter, arrRec);

                VbRecord1 vb1 = buildVbRecord1(acct);
                VbRecord2 vb2 = buildVbRecord2(acct);
                displayVbRecords(vb1, vb2);
                writeVbRecord1(vbrWriter, vb1);
                writeVbRecord2(vbrWriter, vb2);

                count++;
            }
        }

        System.out.println("END OF EXECUTION OF PROGRAM CBACT01C");
        return count;
    }

    // --- Record building (mirrors COBOL PERFORM paragraphs) ---

    /**
     * 1300-POPUL-ACCT-RECORD: builds the OUT-FILE record.
     */
    static OutAccountRecord buildOutRecord(AccountRecord acct) {
        String formattedReissueDate = DateFormatter.toCompactDate(acct.reissueDate());

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
                formattedReissueDate,
                acct.currCycCredit(),
                debit,
                acct.groupId()
        );
    }

    /**
     * 1400-POPUL-ARRAY-RECORD: builds the ARRY-FILE record.
     */
    static ArrayRecord buildArrayRecord(AccountRecord acct) {
        List<ArrayRecord.BalanceEntry> entries = new ArrayList<>(ArrayRecord.NUM_ENTRIES);

        // Slot 1: actual balance, hardcoded debit 1005.00
        entries.add(new ArrayRecord.BalanceEntry(acct.currBal(), ARRAY_DEBIT_1));
        // Slot 2: actual balance, hardcoded debit 1525.00
        entries.add(new ArrayRecord.BalanceEntry(acct.currBal(), ARRAY_DEBIT_2));
        // Slot 3: hardcoded balance -1025.00, hardcoded debit -2500.00
        entries.add(new ArrayRecord.BalanceEntry(ARRAY_BAL_3, ARRAY_DEBIT_3));
        // Slots 4-5: zeroed (from INITIALIZE)
        entries.add(new ArrayRecord.BalanceEntry(BigDecimal.ZERO, BigDecimal.ZERO));
        entries.add(new ArrayRecord.BalanceEntry(BigDecimal.ZERO, BigDecimal.ZERO));

        return new ArrayRecord(acct.acctId(), List.copyOf(entries));
    }

    /**
     * 1500-POPUL-VBRC-RECORD: builds the short VB1 record.
     */
    static VbRecord1 buildVbRecord1(AccountRecord acct) {
        return new VbRecord1(acct.acctId(), acct.activeStatus());
    }

    /**
     * 1500-POPUL-VBRC-RECORD: builds the longer VB2 record.
     */
    static VbRecord2 buildVbRecord2(AccountRecord acct) {
        String reissueYear = DateFormatter.extractYear(acct.reissueDate());
        return new VbRecord2(
                acct.acctId(),
                acct.currBal(),
                acct.creditLimit(),
                reissueYear
        );
    }

    // --- Display (mirrors COBOL DISPLAY statements) ---

    private static void displayAccountRecord(AccountRecord acct) {
        System.out.printf("ACCT-ID                 :%011d%n", acct.acctId());
        System.out.printf("ACCT-ACTIVE-STATUS      :%c%n", acct.activeStatus());
        System.out.printf("ACCT-CURR-BAL           :%s%n", acct.currBal().toPlainString());
        System.out.printf("ACCT-CREDIT-LIMIT       :%s%n", acct.creditLimit().toPlainString());
        System.out.printf("ACCT-CASH-CREDIT-LIMIT  :%s%n", acct.cashCreditLimit().toPlainString());
        System.out.printf("ACCT-OPEN-DATE          :%s%n", acct.openDate());
        System.out.printf("ACCT-EXPIRAION-DATE     :%s%n", acct.expirationDate());
        System.out.printf("ACCT-REISSUE-DATE       :%s%n", acct.reissueDate());
        System.out.printf("ACCT-CURR-CYC-CREDIT    :%s%n", acct.currCycCredit().toPlainString());
        System.out.printf("ACCT-CURR-CYC-DEBIT     :%s%n", acct.currCycDebit().toPlainString());
        System.out.printf("ACCT-GROUP-ID           :%s%n", acct.groupId());
        System.out.println("-------------------------------------------------");
    }

    private static void displayVbRecords(VbRecord1 vb1, VbRecord2 vb2) {
        System.out.printf("VBRC-REC1:%011d%c%n", vb1.acctId(), vb1.activeStatus());
        System.out.printf("VBRC-REC2:%011d%s%s%s%n",
                vb2.acctId(),
                vb2.currBal().toPlainString(),
                vb2.creditLimit().toPlainString(),
                vb2.reissueYear());
    }

    // --- Writers (mirrors COBOL WRITE statements) ---

    /**
     * Writes a pipe-delimited OUT-FILE record.
     */
    static void writeOutRecord(PrintWriter writer, OutAccountRecord rec) {
        writer.printf("%011d|%c|%s|%s|%s|%s|%s|%s|%s|%s|%s%n",
                rec.acctId(),
                rec.activeStatus(),
                rec.currBal().toPlainString(),
                rec.creditLimit().toPlainString(),
                rec.cashCreditLimit().toPlainString(),
                rec.openDate(),
                rec.expirationDate(),
                rec.reissueDate(),
                rec.currCycCredit().toPlainString(),
                rec.currCycDebit().toPlainString(),
                rec.groupId());
    }

    /**
     * Writes a pipe-delimited ARRY-FILE record.
     */
    static void writeArrayRecord(PrintWriter writer, ArrayRecord rec) {
        var sb = new StringBuilder();
        sb.append(String.format("%011d", rec.acctId()));
        for (var entry : rec.entries()) {
            sb.append('|').append(entry.currBal().toPlainString());
            sb.append('|').append(entry.currCycDebit().toPlainString());
        }
        writer.println(sb);
    }

    /**
     * Writes a VB1 record line to the VBR file.
     */
    static void writeVbRecord1(PrintWriter writer, VbRecord1 rec) {
        writer.printf("VB1|%011d|%c%n", rec.acctId(), rec.activeStatus());
    }

    /**
     * Writes a VB2 record line to the VBR file.
     */
    static void writeVbRecord2(PrintWriter writer, VbRecord2 rec) {
        writer.printf("VB2|%011d|%s|%s|%s%n",
                rec.acctId(),
                rec.currBal().toPlainString(),
                rec.creditLimit().toPlainString(),
                rec.reissueYear());
    }

    // --- Main entry point ---

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.err.println("Usage: AccountFileProcessor <acctFile> <outFile> <arrayFile> <vbrFile>");
            System.exit(1);
        }

        var processor = new AccountFileProcessor(
                Path.of(args[0]), Path.of(args[1]),
                Path.of(args[2]), Path.of(args[3]));

        int count = processor.process();
        System.out.printf("Processed %d account records.%n", count);
    }
}
