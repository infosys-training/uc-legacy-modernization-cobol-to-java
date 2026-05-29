package com.cardemo.batch.cbact01c.io;

import com.cardemo.batch.cbact01c.model.AccountRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads COBOL fixed-width account records (300 bytes per line) from the
 * ASCII-encoded account data file. Corresponds to the ACCTFILE-FILE SELECT
 * and READ logic in CBACT01C.
 *
 * <p>Record layout (CVACT01Y copybook):
 * <pre>
 * Offset  Length  Field                   PIC
 * 0       11      ACCT-ID                 9(11)
 * 11       1      ACCT-ACTIVE-STATUS      X(01)
 * 12      12      ACCT-CURR-BAL           S9(10)V99
 * 24      12      ACCT-CREDIT-LIMIT       S9(10)V99
 * 36      12      ACCT-CASH-CREDIT-LIMIT  S9(10)V99
 * 48      10      ACCT-OPEN-DATE          X(10)
 * 58      10      ACCT-EXPIRAION-DATE     X(10)
 * 68      10      ACCT-REISSUE-DATE       X(10)
 * 78      12      ACCT-CURR-CYC-CREDIT    S9(10)V99
 * 90      12      ACCT-CURR-CYC-DEBIT     S9(10)V99
 * 102     10      ACCT-ADDR-ZIP           X(10)
 * 112     10      ACCT-GROUP-ID           X(10)
 * 122    178      FILLER                  X(178)
 * </pre>
 */
public final class AccountFileReader implements AutoCloseable {

    private static final int RECORD_LENGTH = 300;

    private final BufferedReader reader;

    public AccountFileReader(Path filePath) throws IOException {
        this.reader = Files.newBufferedReader(filePath);
    }

    public AccountRecord readNext() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }

        // Pad short lines to full record length
        if (line.length() < RECORD_LENGTH) {
            line = String.format("%-" + RECORD_LENGTH + "s", line);
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
        String acctId           = line.substring(0, 11);
        String activeStatus     = line.substring(11, 12);
        BigDecimal currBal      = CobolFieldParser.parseSignedDecimal(line.substring(12, 24), 2);
        BigDecimal creditLimit  = CobolFieldParser.parseSignedDecimal(line.substring(24, 36), 2);
        BigDecimal cashCreditLt = CobolFieldParser.parseSignedDecimal(line.substring(36, 48), 2);
        String openDate         = line.substring(48, 58);
        String expirationDate   = line.substring(58, 68);
        String reissueDate      = line.substring(68, 78);
        BigDecimal currCycCr    = CobolFieldParser.parseSignedDecimal(line.substring(78, 90), 2);
        BigDecimal currCycDb    = CobolFieldParser.parseSignedDecimal(line.substring(90, 102), 2);
        String addrZip          = line.substring(102, 112);
        String groupId          = line.substring(112, 122);

        return new AccountRecord(
                acctId, activeStatus, currBal, creditLimit, cashCreditLt,
                openDate, expirationDate, reissueDate,
                currCycCr, currCycDb, addrZip, groupId
        );
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
