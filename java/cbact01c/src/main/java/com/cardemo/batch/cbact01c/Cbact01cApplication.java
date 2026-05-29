package com.cardemo.batch.cbact01c;

import com.cardemo.batch.cbact01c.io.AccountFileReader;
import com.cardemo.batch.cbact01c.io.OutputFileWriter;
import com.cardemo.batch.cbact01c.model.*;
import com.cardemo.batch.cbact01c.service.AccountProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 17+ equivalent of COBOL batch program CBACT01C.
 *
 * <p>Reads an account data file (VSAM KSDS format, ASCII-encoded fixed-width
 * 300-byte records) and produces three output files:
 * <ol>
 *   <li><b>OUTFILE</b> — Flat account records with reformatted dates and
 *       defaulted debit values</li>
 *   <li><b>ARRYFILE</b> — Array records with 5 balance/debit pairs per account</li>
 *   <li><b>VBRCFILE</b> — Variable-length records (short + long per account)</li>
 * </ol>
 *
 * <p>Usage: {@code java Cbact01cApplication <acctfile> <outfile> <arryfile> <vbrcfile>}
 */
public class Cbact01cApplication {

    public static void main(String[] args) throws IOException {
        if (args.length < 4) {
            System.err.println(
                    "Usage: Cbact01cApplication <acctfile> <outfile> <arryfile> <vbrcfile>");
            System.exit(1);
        }

        Path acctFilePath = Path.of(args[0]);
        Path outFilePath  = Path.of(args[1]);
        Path arryFilePath = Path.of(args[2]);
        Path vbrcFilePath = Path.of(args[3]);

        System.out.println("START OF EXECUTION OF PROGRAM CBACT01C");

        ProcessingResult result = processAccountFile(acctFilePath);

        OutputFileWriter.writeOutputFile(outFilePath, result.outputRecords);
        OutputFileWriter.writeArrayFile(arryFilePath, result.arrayRecords);
        OutputFileWriter.writeVariableLengthFile(vbrcFilePath,
                result.vbrShortRecords, result.vbrLongRecords);

        System.out.println("END OF EXECUTION OF PROGRAM CBACT01C");
    }

    /**
     * Processes the entire account file and returns all output structures.
     * This method encapsulates the main PERFORM UNTIL loop from CBACT01C.
     */
    public static ProcessingResult processAccountFile(Path acctFilePath) throws IOException {
        List<OutputAccountRecord> outputRecords = new ArrayList<>();
        List<ArrayAccountRecord> arrayRecords = new ArrayList<>();
        List<VariableRecordShort> vbrShortRecords = new ArrayList<>();
        List<VariableRecordLong> vbrLongRecords = new ArrayList<>();

        try (AccountFileReader reader = new AccountFileReader(acctFilePath)) {
            AccountRecord account;
            while ((account = reader.readNext()) != null) {
                displayAccountRecord(account);

                outputRecords.add(AccountProcessor.buildOutputRecord(account));
                arrayRecords.add(AccountProcessor.buildArrayRecord(account));
                vbrShortRecords.add(AccountProcessor.buildVbrShortRecord(account));
                vbrLongRecords.add(AccountProcessor.buildVbrLongRecord(account));
            }
        }

        return new ProcessingResult(outputRecords, arrayRecords,
                vbrShortRecords, vbrLongRecords);
    }

    private static void displayAccountRecord(AccountRecord acct) {
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
     * Container for all output data produced by processing the account file.
     */
    public record ProcessingResult(
            List<OutputAccountRecord> outputRecords,
            List<ArrayAccountRecord> arrayRecords,
            List<VariableRecordShort> vbrShortRecords,
            List<VariableRecordLong> vbrLongRecords
    ) {}
}
