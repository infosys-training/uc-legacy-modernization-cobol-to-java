package com.cardemo.batch.cbexport.io;

import com.cardemo.batch.cbexport.model.CardXrefRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.cardemo.batch.cbexport.io.CobolFieldParser.*;

/**
 * Reads fixed-width card cross-reference records (CVACT03Y, RECLN 50).
 *
 * Layout:
 *   XREF-CARD-NUM  PIC X(16)   offset 0   len 16
 *   XREF-CUST-ID   PIC 9(09)   offset 16  len 9
 *   XREF-ACCT-ID   PIC 9(11)   offset 25  len 11
 *   FILLER          PIC X(14)   offset 36  len 14
 */
public final class CardXrefFileReader {

    private CardXrefFileReader() {}

    public static List<CardXrefRecord> readAll(Path path) throws IOException {
        List<CardXrefRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                records.add(parseLine(line));
            }
        }
        return records;
    }

    public static CardXrefRecord parseLine(String line) {
        return new CardXrefRecord(
                extractString(line, 0, 16),
                extractInt(line, 16, 9),
                extractString(line, 25, 11)
        );
    }
}
