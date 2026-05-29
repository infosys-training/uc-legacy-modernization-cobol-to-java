package com.cardemo.batch.cbact04c.io;

import com.cardemo.batch.cbact04c.model.TranCatBalRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads TCATBAL-FILE (transaction category balance, RECLN 50) sequentially.
 * Layout: TRANCAT-ACCT-ID(11) + TRANCAT-TYPE-CD(2) + TRANCAT-CD(4) + TRAN-CAT-BAL(11 signed) + FILLER(22)
 */
public class TranCatBalFileReader {

    private final Path filePath;

    public TranCatBalFileReader(Path filePath) {
        this.filePath = filePath;
    }

    public List<TranCatBalRecord> readAll() throws IOException {
        List<TranCatBalRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                records.add(parseLine(line));
            }
        }
        return records;
    }

    public static TranCatBalRecord parseLine(String line) {
        // TRANCAT-ACCT-ID PIC 9(11) -> pos 0-10
        String acctId = CobolFieldParser.extractField(line, 0, 11);
        // TRANCAT-TYPE-CD PIC X(02) -> pos 11-12
        String typeCd = CobolFieldParser.extractField(line, 11, 2);
        // TRANCAT-CD PIC 9(04) -> pos 13-16
        int catCd = (int) CobolFieldParser.parseUnsignedNumeric(CobolFieldParser.extractField(line, 13, 4));
        // TRAN-CAT-BAL PIC S9(09)V99 -> 11 chars (9+2=11 digits, trailing overpunch) -> pos 17-27
        String balField = CobolFieldParser.extractField(line, 17, 11);

        TranCatBalRecord rec = new TranCatBalRecord();
        rec.setAccountId(acctId);
        rec.setTypeCode(typeCd);
        rec.setCategoryCode(catCd);
        rec.setBalance(CobolFieldParser.parseSignedDecimal(balField, 2));
        return rec;
    }
}
