package com.cardemo.batch.cbact02c;

import com.cardemo.batch.cbact02c.service.CardDisplayService;

import java.io.IOException;
import java.nio.file.Path;

/**
 * CLI entry point for the modernized CBACT02C batch program.
 *
 * <p>Usage:
 * <pre>
 *   java -jar cbact02c.jar &lt;input-file&gt; [output-file]
 * </pre>
 *
 * <p>If only the input file is provided, card records are displayed to
 * stdout (matching the original COBOL behavior). If an output file is
 * also specified, a pipe-delimited report is written.
 */
public final class Cbact02cApplication {

    private Cbact02cApplication() {}

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: cbact02c <input-file> [output-file]");
            System.exit(1);
        }

        Path inputPath = Path.of(args[0]);
        CardDisplayService service = new CardDisplayService(System.out);

        try {
            if (args.length >= 2) {
                Path outputPath = Path.of(args[1]);
                int count = service.processAndWriteReport(inputPath, outputPath);
                System.out.printf("Successfully processed %d card records.%n", count);
            } else {
                service.displayCards(inputPath);
            }
        } catch (IOException e) {
            System.err.println("ERROR READING CARDFILE: " + e.getMessage());
            System.err.println("ABENDING PROGRAM");
            System.exit(999);
        }
    }
}
