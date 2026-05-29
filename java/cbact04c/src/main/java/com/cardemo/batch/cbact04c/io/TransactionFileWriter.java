package com.cardemo.batch.cbact04c.io;

import com.cardemo.batch.cbact04c.model.TransactionRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes TRANSACT-FILE (output transaction records, RECLN 350) sequentially.
 */
public class TransactionFileWriter {

    private final Path filePath;

    public TransactionFileWriter(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Writes all transaction records to the output file in fixed-width format.
     */
    public void writeAll(List<TransactionRecord> transactions) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
            for (TransactionRecord rec : transactions) {
                writer.write(rec.toFixedWidth());
                writer.newLine();
            }
        }
    }
}
