package com.carddemo.batch;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Main entry point for the CBACT01C Java rewrite.
 *
 * Usage: java -jar cbact01c.jar <acctfile> <outfile> <arryfile> <vbrcfile>
 *
 * Equivalent to running the COBOL CBACT01C program with JCL DD allocations:
 *   //ACCTFILE DD DSN=...   → acctfile argument
 *   //OUTFILE  DD DSN=...   → outfile argument
 *   //ARRYFILE DD DSN=...   → arryfile argument
 *   //VBRCFILE DD DSN=...   → vbrcfile argument
 */
public class Cbact01cApp {

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: cbact01c <acctfile> <outfile> <arryfile> <vbrcfile>");
            System.exit(1);
        }

        Path acctFile = Path.of(args[0]);
        Path outFile = Path.of(args[1]);
        Path arryFile = Path.of(args[2]);
        Path vbrcFile = Path.of(args[3]);

        AccountFileProcessor processor = new AccountFileProcessor();
        try {
            AccountFileProcessor.ProcessingResult result = processor.process(
                    acctFile, outFile, arryFile, vbrcFile);
            System.out.println("Processed " + result.recordCount() + " account records.");
        } catch (IOException e) {
            System.err.println("ABENDING PROGRAM");
            System.err.println("Error: " + e.getMessage());
            System.exit(999);
        }
    }
}
