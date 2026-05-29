package com.cardemo.batch.cbact02c.io;

import com.cardemo.batch.cbact02c.model.CardRecord;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

/**
 * Reads a fixed-width card data file (150-byte records) and produces
 * {@link CardRecord} instances.
 *
 * <p>Mirrors the COBOL sequential read of a KSDS VSAM file.
 */
public final class CardFileReader {

    private CardFileReader() {}

    /**
     * Field layout from CVACT02Y copybook.
     */
    private static final int CARD_NUM_OFFSET = 0;
    private static final int CARD_NUM_LENGTH = 16;

    private static final int ACCT_ID_OFFSET = 16;
    private static final int ACCT_ID_LENGTH = 11;

    private static final int CVV_CD_OFFSET = 27;
    private static final int CVV_CD_LENGTH = 3;

    private static final int EMBOSSED_NAME_OFFSET = 30;
    private static final int EMBOSSED_NAME_LENGTH = 50;

    private static final int EXPIRATION_DATE_OFFSET = 80;
    private static final int EXPIRATION_DATE_LENGTH = 10;

    private static final int ACTIVE_STATUS_OFFSET = 90;
    private static final int ACTIVE_STATUS_LENGTH = 1;

    /**
     * Parses a single fixed-width line into a {@link CardRecord}.
     *
     * @param line a 150-character record line
     * @return the parsed CardRecord
     * @throws IllegalArgumentException if the line is shorter than 150 characters
     */
    public static CardRecord parseLine(String line) {
        if (line.length() < CardRecord.RECORD_LENGTH) {
            throw new IllegalArgumentException(
                    "Record too short: expected %d but got %d characters"
                            .formatted(CardRecord.RECORD_LENGTH, line.length()));
        }

        String cardNum = CobolFieldParser.parseAlphanumeric(line, CARD_NUM_OFFSET, CARD_NUM_LENGTH);
        long acctId = CobolFieldParser.parseUnsignedNumeric(line, ACCT_ID_OFFSET, ACCT_ID_LENGTH);
        int cvvCode = CobolFieldParser.parseUnsignedNumericInt(line, CVV_CD_OFFSET, CVV_CD_LENGTH);
        String embossedName = CobolFieldParser.parseAlphanumeric(line, EMBOSSED_NAME_OFFSET, EMBOSSED_NAME_LENGTH);
        String expirationDate = CobolFieldParser.parseAlphanumeric(line, EXPIRATION_DATE_OFFSET, EXPIRATION_DATE_LENGTH);
        String activeStatus = CobolFieldParser.parseAlphanumeric(line, ACTIVE_STATUS_OFFSET, ACTIVE_STATUS_LENGTH);

        return new CardRecord(cardNum, acctId, cvvCode, embossedName, expirationDate, activeStatus);
    }

    /**
     * Reads all records from the given fixed-width card file.
     *
     * @param filePath path to the card data file
     * @return list of parsed CardRecord objects
     * @throws IOException if the file cannot be read
     */
    public static List<CardRecord> readAll(Path filePath) throws IOException {
        try (Stream<String> lines = Files.lines(filePath)) {
            return lines
                    .filter(line -> !line.isBlank())
                    .map(CardFileReader::parseLine)
                    .toList();
        }
    }
}
