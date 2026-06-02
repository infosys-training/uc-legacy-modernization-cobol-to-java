package com.carddemo.batch;

import com.carddemo.batch.io.AccountFileReader;
import com.carddemo.batch.io.ArrayFileWriter;
import com.carddemo.batch.io.OutFileWriter;
import com.carddemo.batch.io.VbrcFileWriter;
import com.carddemo.batch.model.AccountRecord;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Java 17+ rewrite of COBOL program CBACT01C.
 *
 * CBACT01C reads an account VSAM KSDS file sequentially and produces three output files:
 *   1. OUTFILE  - Fixed-length records with selected fields (includes COMP-3 debit)
 *   2. ARRYFILE - Fixed-length records with repeated balance array entries
 *   3. VBRCFILE - Variable-length records (short status + longer financial summary)
 *
 * Business rules:
 *   - Reissue date is reformatted from YYYY-MM-DD to YYYYMMDD (via COBDATFT replacement)
 *   - If ACCT-CURR-CYC-DEBIT is zero, substitute 2525.00 in OUTFILE
 *   - Array entries use hardcoded debit values: 1005.00, 1525.00, -2500.00
 *   - Array entry 3 uses hardcoded balance: -1025.00
 *   - VBRC file extracts just the year from reissue date
 */
public class Cbact01cApplication {

    private final Path inputFile;
    private final Path outFile;
    private final Path arryFile;
    private final Path vbrcFile;

    public Cbact01cApplication(Path inputFile, Path outFile, Path arryFile, Path vbrcFile) {
        this.inputFile = inputFile;
        this.outFile = outFile;
        this.arryFile = arryFile;
        this.vbrcFile = vbrcFile;
    }

    /**
     * Execute the batch program.
     * @return number of records processed
     */
    public int execute() throws IOException {
        System.out.println("START OF EXECUTION OF PROGRAM CBACT01C");

        int recordCount = 0;

        try (AccountFileReader reader = new AccountFileReader(
                     Files.newBufferedReader(inputFile, StandardCharsets.ISO_8859_1));
             OutFileWriter outWriter = new OutFileWriter(
                     new BufferedOutputStream(Files.newOutputStream(outFile)));
             ArrayFileWriter arryWriter = new ArrayFileWriter(
                     new BufferedOutputStream(Files.newOutputStream(arryFile)));
             VbrcFileWriter vbrcWriter = new VbrcFileWriter(
                     new BufferedOutputStream(Files.newOutputStream(vbrcFile)))) {

            AccountRecord record;
            while ((record = reader.readNext()) != null) {
                displayRecord(record);
                outWriter.writeRecord(record);
                arryWriter.writeRecord(record);
                vbrcWriter.writeRecords(record);
                recordCount++;
            }
        }

        System.out.println("END OF EXECUTION OF PROGRAM CBACT01C");
        return recordCount;
    }

    private void displayRecord(AccountRecord acct) {
        System.out.println("ACCT-ID                 :" + acct.acctId());
        System.out.println("ACCT-ACTIVE-STATUS      :" + acct.activeStatus());
        System.out.println("ACCT-CURR-BAL           :" + formatDisplay(acct.currBal()));
        System.out.println("ACCT-CREDIT-LIMIT       :" + formatDisplay(acct.creditLimit()));
        System.out.println("ACCT-CASH-CREDIT-LIMIT  :" + formatDisplay(acct.cashCreditLimit()));
        System.out.println("ACCT-OPEN-DATE          :" + acct.openDate());
        System.out.println("ACCT-EXPIRAION-DATE     :" + acct.expirationDate());
        System.out.println("ACCT-REISSUE-DATE       :" + acct.reissueDate());
        System.out.println("ACCT-CURR-CYC-CREDIT    :" + formatDisplay(acct.currCycCredit()));
        System.out.println("ACCT-CURR-CYC-DEBIT     :" + formatDisplay(acct.currCycDebit()));
        System.out.println("ACCT-GROUP-ID           :" + acct.groupId());
        System.out.println("-------------------------------------------------");
    }

    private String formatDisplay(java.math.BigDecimal value) {
        return com.carddemo.batch.util.CobolDecimalFormatter.format(value, 12, 2);
    }

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: Cbact01cApplication <acctfile> <outfile> <arryfile> <vbrcfile>");
            System.exit(1);
        }

        Path inputFile = Path.of(args[0]);
        Path outFile = Path.of(args[1]);
        Path arryFile = Path.of(args[2]);
        Path vbrcFile = Path.of(args[3]);

        try {
            Cbact01cApplication app = new Cbact01cApplication(inputFile, outFile, arryFile, vbrcFile);
            int count = app.execute();
            System.out.println("Processed " + count + " records.");
        } catch (IOException e) {
            System.err.println("ERROR: " + e.getMessage());
            System.exit(999);
        }
    }
}
