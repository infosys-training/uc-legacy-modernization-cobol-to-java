package com.carddemo;

import com.carddemo.io.AccountFileReader;
import com.carddemo.io.OutputFileWriter;
import com.carddemo.model.AccountRecord;
import com.carddemo.service.AccountProcessor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

/**
 * Java 17+ migration of COBOL program CBACT01C.cbl.
 *
 * Original COBOL PROCEDURE DIVISION flow:
 *   1. Open ACCTFILE (indexed VSAM, input)
 *   2. Open OUTFILE, ARRYFILE, VBRCFILE (sequential, output)
 *   3. Loop: read each account → display → transform → write 3 output files
 *   4. Close all files
 *
 * Usage:
 *   java -jar cbact01c-java-migration.jar <acctdata-path> <out-dir>
 *
 * Or with defaults:
 *   java -jar cbact01c-java-migration.jar
 *   (reads ../app/data/ASCII/acctdata.txt, writes to ./output/)
 */
public class Cbact01cMain {

    public static void main(String[] args) throws IOException {
        System.out.println("START OF EXECUTION OF PROGRAM CBACT01C (Java)");

        Path inputFile;
        Path outputDir;

        if (args.length >= 2) {
            inputFile = Path.of(args[0]);
            outputDir = Path.of(args[1]);
        } else {
            inputFile = Path.of("../app/data/ASCII/acctdata.txt");
            outputDir = Path.of("output");
        }

        java.nio.file.Files.createDirectories(outputDir);

        // 0000-ACCTFILE-OPEN + READ loop
        AccountFileReader reader = new AccountFileReader();
        List<AccountRecord> accounts = reader.readAll(inputFile);
        System.out.printf("Read %d account records from %s%n", accounts.size(), inputFile);

        // Process all records (1100 through 1575 paragraphs)
        AccountProcessor processor = new AccountProcessor();
        AccountProcessor.ProcessingResult result = processor.process(accounts);

        // Display output (1100-DISPLAY-ACCT-RECORD)
        for (String line : result.displayOutput()) {
            System.out.println(line);
        }

        // Write output files (1350, 1450, 1550, 1575)
        OutputFileWriter writer = new OutputFileWriter();
        Path outFile = outputDir.resolve("outfile.txt");
        Path arryFile = outputDir.resolve("arryfile.txt");
        Path vbrcFile = outputDir.resolve("vbrcfile.txt");
        writer.writeAll(result, outFile, arryFile, vbrcFile);

        System.out.printf("Wrote %d records to %s%n", result.outputRecords().size(), outFile);
        System.out.printf("Wrote %d records to %s%n", result.arrayRecords().size(), arryFile);
        System.out.printf("Wrote %d VB record pairs to %s%n", result.variableRecords1().size(), vbrcFile);

        System.out.println("END OF EXECUTION OF PROGRAM CBACT01C (Java)");
    }
}
