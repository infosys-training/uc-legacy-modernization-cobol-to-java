package com.cardemo.batch.cbact02c.io;

import com.cardemo.batch.cbact02c.model.CardRecord;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes card records in pipe-delimited CSV format (modernized from the
 * COBOL DISPLAY output).
 */
public final class CardReportWriter {

    private static final String DELIMITER = "|";
    private static final String HEADER = String.join(DELIMITER,
            "CARD_NUM", "ACCT_ID", "CVV_CODE", "EMBOSSED_NAME",
            "EXPIRATION_DATE", "ACTIVE_STATUS");

    private CardReportWriter() {}

    /**
     * Writes the list of card records to the specified output file.
     *
     * @param records the card records to write
     * @param outputPath the file path to write to
     * @throws IOException if the file cannot be written
     */
    public static void write(List<CardRecord> records, Path outputPath) throws IOException {
        try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(outputPath))) {
            writer.println(HEADER);
            for (CardRecord rec : records) {
                writer.println(formatRecord(rec));
            }
        }
    }

    /**
     * Formats a single card record as a pipe-delimited string.
     */
    public static String formatRecord(CardRecord rec) {
        return String.join(DELIMITER,
                rec.cardNum(),
                String.valueOf(rec.acctId()),
                String.valueOf(rec.cvvCode()),
                rec.embossedName().strip(),
                rec.expirationDate(),
                rec.activeStatus());
    }

    /**
     * Returns the header line for the pipe-delimited output.
     */
    public static String header() {
        return HEADER;
    }
}
