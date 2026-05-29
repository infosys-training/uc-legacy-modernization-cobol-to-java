package com.cardemo.batch.cbact02c.service;

import com.cardemo.batch.cbact02c.io.CardFileReader;
import com.cardemo.batch.cbact02c.io.CardReportWriter;
import com.cardemo.batch.cbact02c.model.CardRecord;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;

/**
 * Business logic for the CBACT02C batch program.
 *
 * <p>Reads all card records from a KSDS-style indexed file and either
 * displays them (mirroring the original COBOL DISPLAY behavior) or writes
 * them in modernized pipe-delimited format.
 */
public final class CardDisplayService {

    private final PrintStream output;

    public CardDisplayService(PrintStream output) {
        this.output = output;
    }

    /**
     * Reads the card file and displays each record, mirroring the original
     * COBOL paragraph flow:
     * <ol>
     *   <li>0000-CARDFILE-OPEN</li>
     *   <li>1000-CARDFILE-GET-NEXT (loop until EOF)</li>
     *   <li>9000-CARDFILE-CLOSE</li>
     * </ol>
     *
     * @param inputPath  the card data file to read
     * @return the list of records read
     * @throws IOException if file I/O fails
     */
    public List<CardRecord> displayCards(Path inputPath) throws IOException {
        output.println("START OF EXECUTION OF PROGRAM CBACT02C");

        List<CardRecord> records = CardFileReader.readAll(inputPath);
        for (CardRecord record : records) {
            output.println(record);
        }

        output.println("END OF EXECUTION OF PROGRAM CBACT02C");
        return records;
    }

    /**
     * Reads the card file, displays each record, and writes a pipe-delimited
     * report file.
     *
     * @param inputPath  the card data file to read
     * @param outputPath the report output file
     * @return the number of records processed
     * @throws IOException if file I/O fails
     */
    public int processAndWriteReport(Path inputPath, Path outputPath) throws IOException {
        List<CardRecord> records = displayCards(inputPath);
        CardReportWriter.write(records, outputPath);
        output.printf("Wrote %d records to %s%n", records.size(), outputPath);
        return records.size();
    }
}
