package com.cardemo.batch.cbact03c.io;

import com.cardemo.batch.cbact03c.model.CardXrefRecord;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reads a fixed-width card cross-reference file and produces {@link CardXrefRecord} instances.
 * <p>
 * COBOL FD layout (50 bytes total):
 * <pre>
 * FD-XREF-CARD-NUM  PIC X(16)  offset 0
 * FD-XREF-DATA      PIC X(34)  offset 16
 *   -> XREF-CUST-ID PIC 9(09)  offset 16
 *   -> XREF-ACCT-ID PIC 9(11)  offset 25
 *   -> FILLER        PIC X(14)  offset 36
 * </pre>
 */
public final class CardXrefFileReader {

    private static final int CARD_NUM_OFFSET = 0;
    private static final int CARD_NUM_LENGTH = 16;
    private static final int CUST_ID_OFFSET = 16;
    private static final int CUST_ID_LENGTH = 9;
    private static final int ACCT_ID_OFFSET = 25;
    private static final int ACCT_ID_LENGTH = 11;

    private CardXrefFileReader() {}

    /**
     * Parses a single fixed-width line into a {@link CardXrefRecord}.
     * Accepts lines of at least {@link CardXrefRecord#MIN_RECORD_LENGTH} characters.
     */
    public static CardXrefRecord parseLine(String line) {
        if (line.length() < CardXrefRecord.MIN_RECORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Record too short: expected at least %d chars, got %d"
                            .formatted(CardXrefRecord.MIN_RECORD_LENGTH, line.length()));
        }

        String cardNumber = CobolFieldParser.parseAlphanumeric(line, CARD_NUM_OFFSET, CARD_NUM_LENGTH);
        long customerId = CobolFieldParser.parseUnsignedNumeric(line, CUST_ID_OFFSET, CUST_ID_LENGTH);
        long accountId = CobolFieldParser.parseUnsignedNumeric(line, ACCT_ID_OFFSET, ACCT_ID_LENGTH);

        return new CardXrefRecord(cardNumber, customerId, accountId);
    }

    /**
     * Reads all records from the given file path.
     */
    public static List<CardXrefRecord> readAll(Path filePath) {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines
                    .filter(line -> !line.isBlank())
                    .map(CardXrefFileReader::parseLine)
                    .toList();
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read cross-reference file: " + filePath, e);
        }
    }
}
