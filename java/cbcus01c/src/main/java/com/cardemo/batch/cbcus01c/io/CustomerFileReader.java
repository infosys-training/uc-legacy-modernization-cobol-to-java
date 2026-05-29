package com.cardemo.batch.cbcus01c.io;

import com.cardemo.batch.cbcus01c.model.CustomerRecord;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Reads fixed-width 500-byte customer records (CVCUS01Y layout) from an ASCII file.
 * Mirrors the sequential READ of the KSDS VSAM file in CBCUS01C.
 *
 * <p>Each line in the input file is one 500-character record. Lines shorter
 * than 500 characters are right-padded with spaces; blank lines are skipped.
 */
public class CustomerFileReader implements Closeable {

    public static final int RECORD_LENGTH = 500;

    private final BufferedReader reader;

    public CustomerFileReader(Path filePath) throws IOException {
        this.reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8);
    }

    /**
     * Reads the next customer record.
     *
     * @return the parsed {@link CustomerRecord}, or {@code null} at end-of-file
     * @throws IOException on I/O error
     */
    public CustomerRecord readNext() throws IOException {
        String line;
        while ((line = reader.readLine()) != null) {
            if (line.isBlank()) {
                continue;
            }
            if (line.length() < RECORD_LENGTH) {
                line = String.format("%-" + RECORD_LENGTH + "s", line);
            }
            return parseRecord(line);
        }
        return null;
    }

    /**
     * Parses a single fixed-width line into a {@link CustomerRecord}.
     * Field offsets match the CVCUS01Y copybook exactly.
     */
    public static CustomerRecord parseRecord(String line) {
        return new CustomerRecord(
                CobolFieldParser.parseUnsignedInt(CobolFieldParser.extractField(line, 0, 9)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 9, 25)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 34, 25)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 59, 25)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 84, 50)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 134, 50)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 184, 50)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 234, 2)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 236, 3)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 239, 10)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 249, 15)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 264, 15)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 279, 9)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 288, 20)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 308, 10)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 318, 10)),
                CobolFieldParser.parseAlphanumeric(CobolFieldParser.extractField(line, 328, 1)),
                CobolFieldParser.parseUnsignedInt(CobolFieldParser.extractField(line, 329, 3))
        );
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
