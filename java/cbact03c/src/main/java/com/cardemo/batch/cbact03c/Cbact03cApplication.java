package com.cardemo.batch.cbact03c;

import com.cardemo.batch.cbact03c.io.CardXrefFileReader;
import com.cardemo.batch.cbact03c.model.CardXrefRecord;

import java.nio.file.Path;
import java.util.List;

/**
 * Modernised Java equivalent of COBOL batch program CBACT03C.
 * <p>
 * Reads a KSDS VSAM-style card cross-reference flat file,
 * prints each record as pipe-delimited output with a header row.
 * <p>
 * Usage: {@code java Cbact03cApplication <path-to-cardxref-file>}
 */
public final class Cbact03cApplication {

    static final String HEADER = "CARD_NUMBER|CUSTOMER_ID|ACCOUNT_ID";

    private Cbact03cApplication() {}

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("Usage: Cbact03cApplication <xref-file-path>");
            System.exit(1);
        }

        Path filePath = Path.of(args[0]);
        run(filePath);
    }

    /**
     * Core batch logic extracted for testability.
     *
     * @return the number of records processed
     */
    public static int run(Path filePath) {
        System.out.println("START OF EXECUTION OF PROGRAM CBACT03C");

        List<CardXrefRecord> records = CardXrefFileReader.readAll(filePath);

        System.out.println(HEADER);
        for (CardXrefRecord record : records) {
            System.out.println(record.toPipeDelimited());
        }

        System.out.println("END OF EXECUTION OF PROGRAM CBACT03C");
        return records.size();
    }
}
