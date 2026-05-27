package com.carddemo.batch.cbact01c.io;

import com.carddemo.batch.cbact01c.model.AccountRecord;
import com.carddemo.batch.cbact01c.util.CobolZonedDecimalParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads COBOL fixed-length account records (CVACT01Y layout, 300 bytes/line)
 * from the ASCII data file. Replaces VSAM KSDS sequential READ.
 */
public final class AccountFileReader implements AutoCloseable {

    private final BufferedReader reader;

    public AccountFileReader(Path inputFile) throws IOException {
        this.reader = Files.newBufferedReader(inputFile);
    }

    public AccountRecord readNext() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        if (line.length() < AccountRecord.RECORD_LENGTH) {
            line = padRight(line, AccountRecord.RECORD_LENGTH);
        }
        return parseRecord(line);
    }

    public List<AccountRecord> readAll() throws IOException {
        List<AccountRecord> records = new ArrayList<>();
        AccountRecord record;
        while ((record = readNext()) != null) {
            records.add(record);
        }
        return records;
    }

    private AccountRecord parseRecord(String line) {
        int pos = 0;

        long acctId = Long.parseLong(line.substring(pos, pos + 11).trim());
        pos += 11;

        String activeStatus = line.substring(pos, pos + 1);
        pos += 1;

        BigDecimal currBal = CobolZonedDecimalParser.parse(line.substring(pos, pos + 12), 10, 2);
        pos += 12;

        BigDecimal creditLimit = CobolZonedDecimalParser.parse(line.substring(pos, pos + 12), 10, 2);
        pos += 12;

        BigDecimal cashCreditLimit = CobolZonedDecimalParser.parse(line.substring(pos, pos + 12), 10, 2);
        pos += 12;

        String openDate = line.substring(pos, pos + 10).trim();
        pos += 10;

        String expirationDate = line.substring(pos, pos + 10).trim();
        pos += 10;

        String reissueDate = line.substring(pos, pos + 10).trim();
        pos += 10;

        BigDecimal currCycCredit = CobolZonedDecimalParser.parse(line.substring(pos, pos + 12), 10, 2);
        pos += 12;

        BigDecimal currCycDebit = CobolZonedDecimalParser.parse(line.substring(pos, pos + 12), 10, 2);
        pos += 12;

        String addrZip = line.substring(pos, pos + 10).trim();
        pos += 10;

        String groupId = line.substring(pos, pos + 10).trim();

        return new AccountRecord(
                acctId, activeStatus, currBal, creditLimit, cashCreditLimit,
                openDate, expirationDate, reissueDate,
                currCycCredit, currCycDebit, addrZip, groupId
        );
    }

    private static String padRight(String s, int width) {
        return s + " ".repeat(width - s.length());
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
