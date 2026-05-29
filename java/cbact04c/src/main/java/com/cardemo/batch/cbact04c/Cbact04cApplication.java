package com.cardemo.batch.cbact04c;

import com.cardemo.batch.cbact04c.io.*;
import com.cardemo.batch.cbact04c.model.*;
import com.cardemo.batch.cbact04c.service.InterestCalculatorService;

import java.nio.file.Files;
import java.nio.file.InvalidPathException;
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

        if (!isValidDate(parmDate)) {
            System.err.println("ERROR: --date must be a valid date in YYYY-MM-DD format");
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
            // Validate all file paths to prevent path traversal
            Path tcatbalFile = validateFilePath(tcatbalPath, false);
            Path xrefFile = validateFilePath(xrefPath, false);
            Path discgrpFile = validateFilePath(discgrpPath, false);
            Path accountFile = validateFilePath(accountPath, false);
            Path transactFile = validateFilePath(transactPath, true);

            Path transactDir = transactFile.getParent();
            if (transactDir != null) {
                Files.createDirectories(transactDir);
            }

            // Read input files
            List<TranCatBalRecord> tcatbalRecords =
                    new TranCatBalFileReader(tcatbalFile).readAll();

            Map<String, CardXrefRecord> xrefByAcct =
                    new CardXrefFileReader(xrefFile).readAllByAccountId();

            Map<String, DisclosureGroupRecord> discGroupByKey =
                    new DisclosureGroupFileReader(discgrpFile).readAllByKey();

            Map<String, AccountRecord> accountsByKey =
                    new AccountFileReader(accountFile).readAllByAccountId();

            // Process
            InterestCalculatorService service = new InterestCalculatorService(
                    parmDate, tcatbalRecords, xrefByAcct, discGroupByKey, accountsByKey);
            service.process();

            // Write output
            new TransactionFileWriter(transactFile).writeAll(service.getOutputTransactions());
            AccountFileReader.rewriteAll(Path.of(accountFile + ".updated"), service.getUpdatedAccounts());

            System.out.println("Transactions written: " + service.getOutputTransactions().size());
            System.out.println("Accounts updated: " + service.getUpdatedAccounts().size());

        } catch (Exception e) {
            System.err.println("ABENDING PROGRAM: " + e.getMessage());
            System.exit(999);
        }
    }

    /**
     * Validates that a file path is safe (no path traversal, valid characters).
     * For input files, also verifies the file exists.
     *
     * @param pathStr the file path string to validate
     * @param isOutput true if this is an output file path (does not need to exist)
     * @return the normalized, validated Path
     * @throws IllegalArgumentException if path is invalid or unsafe
     */
    static Path validateFilePath(String pathStr, boolean isOutput) {
        if (pathStr == null || pathStr.isBlank()) {
            throw new IllegalArgumentException("File path cannot be null or blank");
        }

        Path path;
        try {
            path = Paths.get(pathStr).normalize();
        } catch (InvalidPathException e) {
            throw new IllegalArgumentException("Invalid file path: contains illegal characters");
        }

        // Reject paths containing null bytes
        if (pathStr.contains("\0")) {
            throw new IllegalArgumentException("Invalid file path: contains null bytes");
        }

        // Check for path traversal: normalized path must not escape working directory
        // by going above the base reference point
        Path absolutePath = path.toAbsolutePath().normalize();
        if (absolutePath.toString().contains("..")) {
            throw new IllegalArgumentException("Invalid file path: path traversal detected");
        }

        if (!isOutput && !Files.exists(path)) {
            throw new IllegalArgumentException("Input file does not exist: " + path);
        }

        return path;
    }

    /**
     * Validates PARM-DATE format (YYYY-MM-DD).
     */
    static boolean isValidDate(String date) {
        if (date == null || date.length() != 10) return false;
        return date.matches("\\d{4}-\\d{2}-\\d{2}");
    }
}
