package com.cardemo.batch.cbexport.io;

import com.cardemo.batch.cbexport.model.CardRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.cardemo.batch.cbexport.io.CobolFieldParser.*;

/**
 * Reads fixed-width card records (CVACT02Y, RECLN 150).
 *
 * Layout:
 *   CARD-NUM             PIC X(16)   offset 0   len 16
 *   CARD-ACCT-ID         PIC 9(11)   offset 16  len 11
 *   CARD-CVV-CD          PIC 9(03)   offset 27  len 3
 *   CARD-EMBOSSED-NAME   PIC X(50)   offset 30  len 50
 *   CARD-EXPIRAION-DATE  PIC X(10)   offset 80  len 10
 *   CARD-ACTIVE-STATUS   PIC X(01)   offset 90  len 1
 *   FILLER               PIC X(59)   offset 91  len 59
 */
public final class CardFileReader {

    private CardFileReader() {}

    public static List<CardRecord> readAll(Path path) throws IOException {
        List<CardRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                records.add(parseLine(line));
            }
        }
        return records;
    }

    public static CardRecord parseLine(String line) {
        return new CardRecord(
                extractString(line, 0, 16),
                extractString(line, 16, 11),
                extractInt(line, 27, 3),
                extractString(line, 30, 50),
                extractString(line, 80, 10),
                extractString(line, 90, 1)
        );
    }
}
