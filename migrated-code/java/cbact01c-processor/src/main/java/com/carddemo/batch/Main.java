package com.carddemo.batch;

import java.nio.file.Path;

public class Main {

    public static void main(String[] args) {
        if (args.length < 2) {
            System.err.println("Usage: cbact01c-processor <input-file> <output-dir> [--output-format=json|cobol-binary]");
            System.exit(1);
        }

        Path inputFile = Path.of(args[0]);
        Path outputDir = Path.of(args[1]);
        OutputFormat format = OutputFormat.JSON;

        if (args.length >= 3 && args[2].startsWith("--output-format=")) {
            String fmt = args[2].substring("--output-format=".length());
            format = switch (fmt) {
                case "json" -> OutputFormat.JSON;
                case "cobol-binary" -> OutputFormat.COBOL_BINARY;
                default -> throw new IllegalArgumentException("Unknown format: " + fmt);
            };
        }

        try {
            new AccountFileProcessor().process(inputFile, outputDir, format);
        } catch (Exception e) {
            System.err.println("ABENDING PROGRAM: " + e.getMessage());
            System.exit(16);
        }
    }
}
