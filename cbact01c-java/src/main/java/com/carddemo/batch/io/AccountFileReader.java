package com.carddemo.batch.io;

import com.carddemo.batch.model.AccountRecord;
import com.carddemo.batch.util.CobolDecimalParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads fixed-length account records (300 bytes per record) from an ASCII file.
 * Maps to COBOL: SELECT ACCTFILE-FILE ... READ ACCTFILE-FILE INTO ACCOUNT-RECORD.
 */
public class AccountFileReader implements AutoCloseable {

    private final BufferedReader reader;

    public AccountFileReader(Reader reader) {
        this.reader = new BufferedReader(reader);
    }

    /**
     * Read the next account record.
     * @return the parsed AccountRecord, or null if end of file
     */
    public AccountRecord readNext() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }

        // Pad to 300 if shorter (handles trailing spaces trimmed by editors)
        if (line.length() < AccountRecord.RECORD_LENGTH) {
            line = padRight(line, AccountRecord.RECORD_LENGTH);
        }

        return parseRecord(line);
    }

    /**
     * Read all records from the file.
     */
    public List<AccountRecord> readAll() throws IOException {
        List<AccountRecord> records = new ArrayList<>();
        AccountRecord record;
        while ((record = readNext()) != null) {
            records.add(record);
        }
        return records;
    }

    private AccountRecord parseRecord(String line) {
        // Field positions (0-based):
        String acctId = line.substring(0, 11);                          // PIC 9(11)
        String activeStatus = line.substring(11, 12);                   // PIC X(01)
        BigDecimal currBal = CobolDecimalParser.parse(line.substring(12, 24), 2);      // PIC S9(10)V99
        BigDecimal creditLimit = CobolDecimalParser.parse(line.substring(24, 36), 2);  // PIC S9(10)V99
        BigDecimal cashCreditLimit = CobolDecimalParser.parse(line.substring(36, 48), 2); // PIC S9(10)V99
        String openDate = line.substring(48, 58);                       // PIC X(10)
        String expirationDate = line.substring(58, 68);                 // PIC X(10)
        String reissueDate = line.substring(68, 78);                    // PIC X(10)
        BigDecimal currCycCredit = CobolDecimalParser.parse(line.substring(78, 90), 2); // PIC S9(10)V99
        BigDecimal currCycDebit = CobolDecimalParser.parse(line.substring(90, 102), 2); // PIC S9(10)V99
        String addrZip = line.substring(102, 112);                      // PIC X(10)
        String groupId = line.substring(112, 122);                      // PIC X(10)

        return new AccountRecord(
                acctId, activeStatus, currBal, creditLimit, cashCreditLimit,
                openDate, expirationDate, reissueDate,
                currCycCredit, currCycDebit, addrZip, groupId
        );
    }

    private static String padRight(String s, int length) {
        return s + " ".repeat(length - s.length());
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
