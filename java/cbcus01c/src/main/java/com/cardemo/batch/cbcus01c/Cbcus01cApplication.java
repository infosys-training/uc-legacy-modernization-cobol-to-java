package com.cardemo.batch.cbcus01c;

import com.cardemo.batch.cbcus01c.io.CustomerFileReader;
import com.cardemo.batch.cbcus01c.model.CustomerRecord;

import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Java 17+ equivalent of COBOL batch program CBCUS01C.
 *
 * <p>Reads a customer data file (VSAM KSDS format, ASCII-encoded fixed-width
 * 500-byte records) and outputs pipe-delimited CSV with headers to stdout.
 *
 * <p>Usage: {@code java Cbcus01cApplication <custfile>}
 */
public class Cbcus01cApplication {

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.err.println("Usage: Cbcus01cApplication <custfile>");
            System.exit(1);
        }

        Path custFilePath = Path.of(args[0]);

        System.out.println("START OF EXECUTION OF PROGRAM CBCUS01C");

        List<CustomerRecord> records = readAllCustomers(custFilePath);
        writeCsv(System.out, records);

        System.out.println("END OF EXECUTION OF PROGRAM CBCUS01C");
    }

    /**
     * Reads all customer records from the given file.
     * Mirrors the PERFORM UNTIL END-OF-FILE loop from CBCUS01C.
     */
    public static List<CustomerRecord> readAllCustomers(Path custFilePath) throws IOException {
        List<CustomerRecord> records = new ArrayList<>();
        try (CustomerFileReader reader = new CustomerFileReader(custFilePath)) {
            CustomerRecord record;
            while ((record = reader.readNext()) != null) {
                records.add(record);
            }
        }
        return records;
    }

    /**
     * Writes customer records as pipe-delimited CSV with a header row.
     */
    public static void writeCsv(PrintStream out, List<CustomerRecord> records) {
        out.println(CustomerRecord.CSV_HEADER);
        for (CustomerRecord record : records) {
            out.println(record.toCsvLine());
        }
    }
}
