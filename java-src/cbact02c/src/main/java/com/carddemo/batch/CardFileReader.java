package com.carddemo.batch;

import com.carddemo.model.CardRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Java migration of COBOL program CBACT02C.cbl.
 *
 * CBACT02C reads the CARDFILE (VSAM KSDS, sequential access) and DISPLAYs
 * each card record. This Java equivalent reads a fixed-width flat file
 * (ASCII export of VSAM) and prints each record to stdout.
 */
public class CardFileReader {

    public static List<CardRecord> readCardFile(Path cardFilePath) throws IOException {
        List<CardRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(cardFilePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isEmpty()) {
                    continue;
                }
                records.add(CardRecord.parse(line));
            }
        }
        return records;
    }

    public static void main(String[] args) {
        System.out.println("START OF EXECUTION OF PROGRAM CBACT02C");

        if (args.length < 1) {
            System.err.println("Usage: CardFileReader <carddata-file>");
            System.exit(1);
        }

        Path cardFilePath = Path.of(args[0]);
        try {
            List<CardRecord> records = readCardFile(cardFilePath);
            for (CardRecord record : records) {
                System.out.println(record.toDisplayString());
            }
        } catch (IOException e) {
            System.err.println("ERROR READING CARDFILE");
            System.err.println("FILE STATUS IS: " + e.getMessage());
            System.exit(999);
        }

        System.out.println("END OF EXECUTION OF PROGRAM CBACT02C");
    }
}
