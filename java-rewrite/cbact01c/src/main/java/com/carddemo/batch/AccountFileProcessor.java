package com.carddemo.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Java rewrite of COBOL batch program CBACT01C.
 *
 * Reads the account VSAM file (sequential) and writes 3 output files:
 *   1. OUTFILE  — Account records with reformatted reissue date and debit substitution
 *   2. ARRYFILE — Array records with 5 balance/debit pairs per account
 *   3. VBRCFILE — Variable-length records (VB1: ID+status, VB2: ID+bal+limit+year)
 *
 * Business rules (from COBOL source):
 *   - Reissue date is converted from YYYY-MM-DD to YYYYMMDD via COBDATFT (DateConverter)
 *   - If ACCT-CURR-CYC-DEBIT is zero, substitute 2525.00 in the output record
 *   - Array record slots 1-2 use actual balance; slot 3 uses fixed -1025.00
 *   - Array record debit slots use fixed values: 1005.00, 1525.00, -2500.00
 *   - Slots 4-5 of the array are left at zero (INITIALIZE)
 */
public class AccountFileProcessor {

    private static final BigDecimal DEBIT_SUBSTITUTE = new BigDecimal("2525.00");
    private static final BigDecimal ARR_DEBIT_1 = new BigDecimal("1005.00");
    private static final BigDecimal ARR_DEBIT_2 = new BigDecimal("1525.00");
    private static final BigDecimal ARR_BAL_3 = new BigDecimal("-1025.00");
    private static final BigDecimal ARR_DEBIT_3 = new BigDecimal("-2500.00");

    /** Result of processing — holds all output records for verification. */
    public record ProcessingResult(
            List<OutAccountRecord> outRecords,
            List<ArrayRecord> arrayRecords,
            List<VbRecord> vbRecords,
            int recordCount
    ) {}

    /** Output account record (maps to OUT-ACCT-REC in COBOL). */
    public record OutAccountRecord(
            String acctId,
            String activeStatus,
            BigDecimal currBal,
            BigDecimal creditLimit,
            BigDecimal cashCreditLimit,
            String openDate,
            String expirationDate,
            String reissueDate,       // Reformatted: YYYYMMDD
            BigDecimal currCycCredit,
            BigDecimal currCycDebit,   // Substituted to 2525.00 if original is zero
            String groupId
    ) {}

    /** Array record (maps to ARR-ARRAY-REC in COBOL). */
    public record ArrayRecord(
            String acctId,
            List<BalanceDebitPair> pairs  // 5 elements
    ) {}

    public record BalanceDebitPair(BigDecimal balance, BigDecimal debit) {}

    /** Variable-length record (maps to VB1 + VB2 in COBOL). */
    public record VbRecord(
            // VB1 fields (12 bytes in COBOL)
            String acctId,
            String activeStatus,
            // VB2 fields (39 bytes in COBOL)
            BigDecimal currBal,
            BigDecimal creditLimit,
            String reissueYear   // First 4 chars of reissue date (YYYY)
    ) {}

    /**
     * Process the account file and write 3 output files. Returns the processing result
     * for test verification.
     */
    public ProcessingResult process(Path inputFile, Path outFile, Path arryFile, Path vbrcFile)
            throws IOException {

        List<OutAccountRecord> outRecords = new ArrayList<>();
        List<ArrayRecord> arrayRecords = new ArrayList<>();
        List<VbRecord> vbRecords = new ArrayList<>();
        int recordCount = 0;

        System.out.println("START OF EXECUTION OF PROGRAM CBACT01C");

        try (BufferedReader reader = Files.newBufferedReader(inputFile);
             BufferedWriter outWriter = Files.newBufferedWriter(outFile);
             BufferedWriter arryWriter = Files.newBufferedWriter(arryFile);
             BufferedWriter vbrcWriter = Files.newBufferedWriter(vbrcFile)) {

            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }

                AccountRecord acct = AccountRecord.parse(line);
                recordCount++;

                // 1100-DISPLAY-ACCT-RECORD equivalent
                displayAccountRecord(acct);

                // 1300-POPUL-ACCT-RECORD — build output record
                OutAccountRecord outRec = buildOutRecord(acct);
                outRecords.add(outRec);

                // 1350-WRITE-ACCT-RECORD
                writeOutRecord(outWriter, outRec);

                // 1400-POPUL-ARRAY-RECORD
                ArrayRecord arrRec = buildArrayRecord(acct);
                arrayRecords.add(arrRec);

                // 1450-WRITE-ARRY-RECORD
                writeArrayRecord(arryWriter, arrRec);

                // 1500-POPUL-VBRC-RECORD + 1550/1575-WRITE
                VbRecord vbRec = buildVbRecord(acct);
                vbRecords.add(vbRec);
                writeVbRecords(vbrcWriter, vbRec);
            }
        }

        System.out.println("END OF EXECUTION OF PROGRAM CBACT01C");

        return new ProcessingResult(outRecords, arrayRecords, vbRecords, recordCount);
    }

    private void displayAccountRecord(AccountRecord acct) {
        System.out.println("ACCT-ID                 :" + acct.acctId());
        System.out.println("ACCT-ACTIVE-STATUS      :" + acct.activeStatus());
        System.out.println("ACCT-CURR-BAL           :" + acct.currBal());
        System.out.println("ACCT-CREDIT-LIMIT       :" + acct.creditLimit());
        System.out.println("ACCT-CASH-CREDIT-LIMIT  :" + acct.cashCreditLimit());
        System.out.println("ACCT-OPEN-DATE          :" + acct.openDate());
        System.out.println("ACCT-EXPIRAION-DATE     :" + acct.expirationDate());
        System.out.println("ACCT-REISSUE-DATE       :" + acct.reissueDate());
        System.out.println("ACCT-CURR-CYC-CREDIT    :" + acct.currCycCredit());
        System.out.println("ACCT-CURR-CYC-DEBIT     :" + acct.currCycDebit());
        System.out.println("ACCT-GROUP-ID           :" + acct.groupId());
        System.out.println("-------------------------------------------------");
    }

    /**
     * Build the output account record (1300-POPUL-ACCT-RECORD logic).
     *
     * Business rules:
     * - Reissue date converted from YYYY-MM-DD to YYYYMMDD
     * - If cycle debit is zero, substitute 2525.00
     */
    OutAccountRecord buildOutRecord(AccountRecord acct) {
        // Date conversion: COBDATFT with type='2' (YYYY-MM-DD input), outtype='2' (YYYYMMDD output)
        String reformattedReissueDate = DateConverter.convert(acct.reissueDate(), '2', '2');

        // Debit substitution rule
        BigDecimal outDebit = acct.currCycDebit().signum() == 0
                ? DEBIT_SUBSTITUTE
                : acct.currCycDebit();

        return new OutAccountRecord(
                acct.acctId(),
                acct.activeStatus(),
                acct.currBal(),
                acct.creditLimit(),
                acct.cashCreditLimit(),
                acct.openDate(),
                acct.expirationDate(),
                reformattedReissueDate,
                acct.currCycCredit(),
                outDebit,
                acct.groupId()
        );
    }

    /**
     * Build the array record (1400-POPUL-ARRAY-RECORD logic).
     *
     * Slots 1-2: actual balance + fixed debit constants
     * Slot 3: fixed negative balance + fixed negative debit
     * Slots 4-5: zero (from INITIALIZE)
     */
    ArrayRecord buildArrayRecord(AccountRecord acct) {
        List<BalanceDebitPair> pairs = List.of(
                new BalanceDebitPair(acct.currBal(), ARR_DEBIT_1),
                new BalanceDebitPair(acct.currBal(), ARR_DEBIT_2),
                new BalanceDebitPair(ARR_BAL_3, ARR_DEBIT_3),
                new BalanceDebitPair(BigDecimal.ZERO, BigDecimal.ZERO),
                new BalanceDebitPair(BigDecimal.ZERO, BigDecimal.ZERO)
        );
        return new ArrayRecord(acct.acctId(), pairs);
    }

    /**
     * Build the variable-length record (1500-POPUL-VBRC-RECORD logic).
     */
    VbRecord buildVbRecord(AccountRecord acct) {
        // WS-ACCT-REISSUE-YYYY: first 4 chars of reissue date (YYYY from YYYY-MM-DD)
        String reissueYear = acct.reissueDate().length() >= 4
                ? acct.reissueDate().substring(0, 4)
                : acct.reissueDate();

        return new VbRecord(
                acct.acctId(),
                acct.activeStatus(),
                acct.currBal(),
                acct.creditLimit(),
                reissueYear
        );
    }

    // --- File writers ---

    private void writeOutRecord(BufferedWriter writer, OutAccountRecord rec) throws IOException {
        // Pipe-delimited output matching COBOL field order
        writer.write(String.join("|",
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
                rec.groupId()
        ));
        writer.newLine();
    }

    private void writeArrayRecord(BufferedWriter writer, ArrayRecord rec) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(rec.acctId());
        for (BalanceDebitPair pair : rec.pairs()) {
            sb.append("|").append(pair.balance().toPlainString());
            sb.append("|").append(pair.debit().toPlainString());
        }
        writer.write(sb.toString());
        writer.newLine();
    }

    private void writeVbRecords(BufferedWriter writer, VbRecord rec) throws IOException {
        // VB1: short record (ID + status)
        writer.write("VB1|" + rec.acctId() + "|" + rec.activeStatus());
        writer.newLine();
        // VB2: long record (ID + balance + limit + reissue year)
        writer.write("VB2|" + rec.acctId() + "|" + rec.currBal().toPlainString()
                + "|" + rec.creditLimit().toPlainString() + "|" + rec.reissueYear());
        writer.newLine();
    }
}
