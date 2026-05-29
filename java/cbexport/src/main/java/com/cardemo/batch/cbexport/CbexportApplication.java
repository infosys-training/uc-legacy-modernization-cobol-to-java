package com.cardemo.batch.cbexport;

import com.cardemo.batch.cbexport.service.ExportService;

import java.nio.file.Path;

/**
 * CLI entry point for the Branch Migration Export batch program.
 *
 * <p>Usage: {@code java -jar cbexport.jar <dataDir> <outputFile>}
 *
 * <p>{@code dataDir} must contain: custdata.txt, acctdata.txt,
 * cardxref.txt, dailytran.txt, carddata.txt
 */
public final class CbexportApplication {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: cbexport <dataDir> <outputFile>");
            System.err.println("  dataDir    - directory containing input data files");
            System.err.println("  outputFile - path to the export output file");
            System.exit(1);
        }

        Path dataDir = Path.of(args[0]);
        Path outputFile = Path.of(args[1]);

        Path customerFile = dataDir.resolve("custdata.txt");
        Path accountFile = dataDir.resolve("acctdata.txt");
        Path xrefFile = dataDir.resolve("cardxref.txt");
        Path transactionFile = dataDir.resolve("dailytran.txt");
        Path cardFile = dataDir.resolve("carddata.txt");

        ExportService service = new ExportService();
        try {
            ExportService.ExportStatistics stats = service.runExport(
                    customerFile, accountFile, xrefFile,
                    transactionFile, cardFile, outputFile
            );
            System.exit(stats.total() > 0 ? 0 : 1);
        } catch (Exception e) {
            System.err.println("CBEXPORT: ABENDING PROGRAM");
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace(System.err);
            System.exit(2);
        }
    }
}
