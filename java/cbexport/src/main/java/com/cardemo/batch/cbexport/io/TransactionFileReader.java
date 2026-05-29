package com.cardemo.batch.cbexport.io;

import com.cardemo.batch.cbexport.model.TransactionRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.cardemo.batch.cbexport.io.CobolFieldParser.*;

/**
 * Reads fixed-width transaction records (CVTRA05Y, RECLN 350).
 *
 * Layout:
 *   TRAN-ID             PIC X(16)       offset 0    len 16
 *   TRAN-TYPE-CD        PIC X(02)       offset 16   len 2
 *   TRAN-CAT-CD         PIC 9(04)       offset 18   len 4
 *   TRAN-SOURCE         PIC X(10)       offset 22   len 10
 *   TRAN-DESC           PIC X(100)      offset 32   len 100
 *   TRAN-AMT            PIC S9(09)V99   offset 132  len 12
 *   TRAN-MERCHANT-ID    PIC 9(09)       offset 144  len 9
 *   TRAN-MERCHANT-NAME  PIC X(50)       offset 153  len 50
 *   TRAN-MERCHANT-CITY  PIC X(50)       offset 203  len 50
 *   TRAN-MERCHANT-ZIP   PIC X(10)       offset 253  len 10
 *   TRAN-CARD-NUM       PIC X(16)       offset 263  len 16
 *   TRAN-ORIG-TS        PIC X(26)       offset 279  len 26
 *   TRAN-PROC-TS        PIC X(26)       offset 305  len 26
 *   FILLER              PIC X(20)       offset 331  len 20
 *
 *   S9(09)V99 zoned decimal = 11 chars in the file.
 */
public final class TransactionFileReader {

    private TransactionFileReader() {}

    public static List<TransactionRecord> readAll(Path path) throws IOException {
        List<TransactionRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                records.add(parseLine(line));
            }
        }
        return records;
    }

    public static TransactionRecord parseLine(String line) {
        return new TransactionRecord(
                extractString(line, 0, 16),
                extractString(line, 16, 2),
                extractInt(line, 18, 4),
                extractString(line, 22, 10),
                extractString(line, 32, 100),
                extractSignedDecimal(line, 132, 11, 2),
                extractInt(line, 143, 9),
                extractString(line, 152, 50),
                extractString(line, 202, 50),
                extractString(line, 252, 10),
                extractString(line, 262, 16),
                extractString(line, 278, 26),
                extractString(line, 304, 26)
        );
    }
}
