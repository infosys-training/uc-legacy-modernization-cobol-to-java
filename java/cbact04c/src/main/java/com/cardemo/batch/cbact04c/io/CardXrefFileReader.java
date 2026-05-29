package com.cardemo.batch.cbact04c.io;

import com.cardemo.batch.cbact04c.model.CardXrefRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads XREF-FILE (card cross-reference, RECLN 50) and indexes by account ID.
 * Layout: XREF-CARD-NUM(16) + XREF-CUST-ID(9) + XREF-ACCT-ID(11) + FILLER(14)
 */
public class CardXrefFileReader {

    private final Path filePath;

    public CardXrefFileReader(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads all records and returns a map keyed by account ID (alternate key).
     * In COBOL, this file is accessed by ALTERNATE RECORD KEY IS FD-XREF-ACCT-ID.
     */
    public Map<String, CardXrefRecord> readAllByAccountId() throws IOException {
        Map<String, CardXrefRecord> map = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                CardXrefRecord rec = parseLine(line);
                map.put(rec.getAccountId(), rec);
            }
        }
        return map;
    }

    public static CardXrefRecord parseLine(String line) {
        // XREF-CARD-NUM PIC X(16) -> pos 0-15
        String cardNum = CobolFieldParser.extractField(line, 0, 16);
        // XREF-CUST-ID PIC 9(09) -> pos 16-24
        String custId = CobolFieldParser.extractField(line, 16, 9);
        // XREF-ACCT-ID PIC 9(11) -> pos 25-35
        String acctId = CobolFieldParser.extractField(line, 25, 11);

        CardXrefRecord rec = new CardXrefRecord();
        rec.setCardNumber(cardNum);
        rec.setCustomerId(custId);
        rec.setAccountId(acctId);
        return rec;
    }
}
