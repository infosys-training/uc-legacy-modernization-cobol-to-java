package com.cardemo.batch.cbexport.io;

import com.cardemo.batch.cbexport.model.CustomerRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.cardemo.batch.cbexport.io.CobolFieldParser.*;

/**
 * Reads fixed-width customer records (CVCUS01Y, RECLN 500).
 *
 * Layout:
 *   CUST-ID              PIC 9(09)  offset 0   len 9
 *   CUST-FIRST-NAME      PIC X(25)  offset 9   len 25
 *   CUST-MIDDLE-NAME     PIC X(25)  offset 34  len 25
 *   CUST-LAST-NAME       PIC X(25)  offset 59  len 25
 *   CUST-ADDR-LINE-1     PIC X(50)  offset 84  len 50
 *   CUST-ADDR-LINE-2     PIC X(50)  offset 134 len 50
 *   CUST-ADDR-LINE-3     PIC X(50)  offset 184 len 50
 *   CUST-ADDR-STATE-CD   PIC X(02)  offset 234 len 2
 *   CUST-ADDR-COUNTRY-CD PIC X(03)  offset 236 len 3
 *   CUST-ADDR-ZIP        PIC X(10)  offset 239 len 10
 *   CUST-PHONE-NUM-1     PIC X(15)  offset 249 len 15
 *   CUST-PHONE-NUM-2     PIC X(15)  offset 264 len 15
 *   CUST-SSN             PIC 9(09)  offset 279 len 9
 *   CUST-GOVT-ISSUED-ID  PIC X(20)  offset 288 len 20
 *   CUST-DOB-YYYY-MM-DD  PIC X(10)  offset 308 len 10
 *   CUST-EFT-ACCOUNT-ID  PIC X(10)  offset 318 len 10
 *   CUST-PRI-CARD-IND    PIC X(01)  offset 328 len 1
 *   CUST-FICO            PIC 9(03)  offset 329 len 3
 *   FILLER               PIC X(168) offset 332 len 168
 */
public final class CustomerFileReader {

    private CustomerFileReader() {}

    public static List<CustomerRecord> readAll(Path path) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                records.add(parseLine(line));
            }
        }
        return records;
    }

    public static CustomerRecord parseLine(String line) {
        return new CustomerRecord(
                extractInt(line, 0, 9),
                extractString(line, 9, 25),
                extractString(line, 34, 25),
                extractString(line, 59, 25),
                List.of(
                        extractString(line, 84, 50),
                        extractString(line, 134, 50),
                        extractString(line, 184, 50)
                ),
                extractString(line, 234, 2),
                extractString(line, 236, 3),
                extractString(line, 239, 10),
                List.of(
                        extractString(line, 249, 15),
                        extractString(line, 264, 15)
                ),
                extractInt(line, 279, 9),
                extractString(line, 288, 20),
                extractString(line, 308, 10),
                extractString(line, 318, 10),
                extractString(line, 328, 1),
                extractInt(line, 329, 3)
        );
    }
}
