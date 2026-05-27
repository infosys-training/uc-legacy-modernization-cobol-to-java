package com.cardemo.batch;

import com.cardemo.batch.io.AccountReader;
import com.cardemo.batch.io.RecordFormatter;
import com.cardemo.batch.model.ProcessingResult;
import com.cardemo.batch.processor.AccountProcessor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Java 17 rewrite of COBOL batch program CBACT01C.
 * <p>
 * Reads an account data file (fixed-width CVACT01Y layout), processes each
 * record, and writes results to three output files:
 * <ul>
 *   <li>{@code outfile.txt}  &mdash; selected account fields with date conversion</li>
 *   <li>{@code arryfile.txt} &mdash; array records with balance/debit pairs</li>
 *   <li>{@code vbrcfile.txt} &mdash; short and long variable-length records</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <pre>
 *   java -jar cbact01c.jar &lt;input-file&gt; &lt;output-dir&gt;
 * </pre>
 */
public final class Cbact01cApplication {

    private Cbact01cApplication() {}

    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java -jar cbact01c.jar <input-file> <output-dir>");
            System.exit(1);
        }

        Path inputFile = Path.of(args[0]);
        Path outputDir = Path.of(args[1]);
        Files.createDirectories(outputDir);

        Path outFilePath  = outputDir.resolve("outfile.txt");
        Path arryFilePath = outputDir.resolve("arryfile.txt");
        Path vbrcFilePath = outputDir.resolve("vbrcfile.txt");

        System.out.println("START OF EXECUTION OF PROGRAM CBACT01C");

        var processor = new AccountProcessor();
        int count = 0;

        try (var reader    = new AccountReader(inputFile);
             var outWriter  = Files.newBufferedWriter(outFilePath);
             var arryWriter = Files.newBufferedWriter(arryFilePath);
             var vbrcWriter = Files.newBufferedWriter(vbrcFilePath)) {

            for (var account : reader) {
                ProcessingResult result = processor.process(account);

                displayAccountRecord(account);

                writeLine(outWriter,  RecordFormatter.format(result.outRecord()));
                writeLine(arryWriter, RecordFormatter.format(result.arrayRecord()));
                writeLine(vbrcWriter, RecordFormatter.format(result.vbrcRecord1()));
                writeLine(vbrcWriter, RecordFormatter.format(result.vbrcRecord2()));

                count++;
            }
        }

        System.out.printf("Processed %d account record(s).%n", count);
        System.out.println("END OF EXECUTION OF PROGRAM CBACT01C");
    }

    private static void displayAccountRecord(
            com.cardemo.batch.model.AccountRecord acct) {
        System.out.printf("ACCT-ID                 :%s%n", acct.acctId());
        System.out.printf("ACCT-ACTIVE-STATUS      :%s%n", acct.activeStatus());
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

    private static void writeLine(BufferedWriter w, String line) throws IOException {
        w.write(line);
        w.newLine();
    }
}
