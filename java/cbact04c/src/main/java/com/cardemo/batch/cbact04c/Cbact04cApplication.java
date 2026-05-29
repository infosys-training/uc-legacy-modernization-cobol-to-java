package com.cardemo.batch.cbact04c;

import com.cardemo.batch.cbact04c.io.*;
import com.cardemo.batch.cbact04c.model.*;
import com.cardemo.batch.cbact04c.service.InterestCalculatorService;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * CLI entry point for the CBACT04C batch interest calculator.
 *
 * Usage: java -jar cbact04c.jar --date=2022-06-10
 *            [--tcatbal=path] [--xref=path] [--discgrp=path]
 *            [--account=path] [--transact=path]
 *
 * Modernized from COBOL program CBACT04C.CBL.
 */
public class Cbact04cApplication {

    public static void main(String[] args) {
        String parmDate = null;
        String tcatbalPath = null;
        String xrefPath = null;
        String discgrpPath = null;
        String accountPath = null;
        String transactPath = null;

        for (String arg : args) {
            if (arg.startsWith("--date=")) {
                parmDate = arg.substring("--date=".length());
            } else if (arg.startsWith("--tcatbal=")) {
                tcatbalPath = arg.substring("--tcatbal=".length());
            } else if (arg.startsWith("--xref=")) {
                xrefPath = arg.substring("--xref=".length());
            } else if (arg.startsWith("--discgrp=")) {
                discgrpPath = arg.substring("--discgrp=".length());
            } else if (arg.startsWith("--account=")) {
                accountPath = arg.substring("--account=".length());
            } else if (arg.startsWith("--transact=")) {
                transactPath = arg.substring("--transact=".length());
            }
        }

        if (parmDate == null || parmDate.isBlank()) {
            System.err.println("ERROR: --date parameter is required (e.g., --date=2022-06-10)");
            System.exit(1);
        }

        // Default data paths relative to project root
        String dataDir = "../../app/data/ASCII/";
        if (tcatbalPath == null) tcatbalPath = dataDir + "tcatbal.txt";
        if (xrefPath == null) xrefPath = dataDir + "cardxref.txt";
        if (discgrpPath == null) discgrpPath = dataDir + "discgrp.txt";
        if (accountPath == null) accountPath = dataDir + "acctdata.txt";
        if (transactPath == null) transactPath = "output/transact.txt";

        try {
            Path transactDir = Paths.get(transactPath).getParent();
            if (transactDir != null) {
                java.nio.file.Files.createDirectories(transactDir);
            }

            // Read input files
            List<TranCatBalRecord> tcatbalRecords =
                    new TranCatBalFileReader(Paths.get(tcatbalPath)).readAll();

            Map<String, CardXrefRecord> xrefByAcct =
                    new CardXrefFileReader(Paths.get(xrefPath)).readAllByAccountId();

            Map<String, DisclosureGroupRecord> discGroupByKey =
                    new DisclosureGroupFileReader(Paths.get(discgrpPath)).readAllByKey();

            Map<String, AccountRecord> accountsByKey =
                    new AccountFileReader(Paths.get(accountPath)).readAllByAccountId();

            // Process
            InterestCalculatorService service = new InterestCalculatorService(
                    parmDate, tcatbalRecords, xrefByAcct, discGroupByKey, accountsByKey);
            service.process();

            // Write output
            new TransactionFileWriter(Paths.get(transactPath)).writeAll(service.getOutputTransactions());
            AccountFileReader.rewriteAll(Paths.get(accountPath + ".updated"), service.getUpdatedAccounts());

            System.out.println("Transactions written: " + service.getOutputTransactions().size());
            System.out.println("Accounts updated: " + service.getUpdatedAccounts().size());

        } catch (Exception e) {
            System.err.println("ABENDING PROGRAM: " + e.getMessage());
            e.printStackTrace();
            System.exit(999);
        }
    }
}
