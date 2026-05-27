package com.cardemo.batch.io;

import com.cardemo.batch.model.AccountRecord;
import com.cardemo.batch.util.SignedDecimalParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * Reads fixed-width account records matching the CVACT01Y copybook layout.
 * <p>
 * Field offsets (0-based) and lengths:
 * <pre>
 *   0..10   (11) ACCT-ID
 *  11       ( 1) ACCT-ACTIVE-STATUS
 *  12..23   (12) ACCT-CURR-BAL           S9(10)V99
 *  24..35   (12) ACCT-CREDIT-LIMIT       S9(10)V99
 *  36..47   (12) ACCT-CASH-CREDIT-LIMIT  S9(10)V99
 *  48..57   (10) ACCT-OPEN-DATE
 *  58..67   (10) ACCT-EXPIRAION-DATE
 *  68..77   (10) ACCT-REISSUE-DATE
 *  78..89   (12) ACCT-CURR-CYC-CREDIT    S9(10)V99
 *  90..101  (12) ACCT-CURR-CYC-DEBIT     S9(10)V99
 * 102..111  (10) ACCT-ADDR-ZIP
 * 112..121  (10) ACCT-GROUP-ID
 * 122..299 (178) FILLER
 * </pre>
 */
public final class AccountReader implements AutoCloseable, Iterable<AccountRecord> {

    private static final int ACCT_ID_OFF = 0,   ACCT_ID_LEN = 11;
    private static final int STATUS_OFF  = 11,  STATUS_LEN  = 1;
    private static final int BAL_OFF     = 12,  BAL_LEN     = 12;
    private static final int CRLIM_OFF   = 24,  CRLIM_LEN   = 12;
    private static final int CASHLIM_OFF = 36,  CASHLIM_LEN = 12;
    private static final int OPEN_OFF    = 48,  OPEN_LEN    = 10;
    private static final int EXPIR_OFF   = 58,  EXPIR_LEN   = 10;
    private static final int REISS_OFF   = 68,  REISS_LEN   = 10;
    private static final int CREDIT_OFF  = 78,  CREDIT_LEN  = 12;
    private static final int DEBIT_OFF   = 90,  DEBIT_LEN   = 12;
    private static final int ZIP_OFF     = 102, ZIP_LEN     = 10;
    private static final int GROUP_OFF   = 112, GROUP_LEN   = 10;

    private final BufferedReader reader;

    public AccountReader(Path filePath) throws IOException {
        this.reader = Files.newBufferedReader(filePath);
    }

    public AccountRecord readNext() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        return parseLine(line);
    }

    public List<AccountRecord> readAll() throws IOException {
        var records = new ArrayList<AccountRecord>();
        AccountRecord record;
        while ((record = readNext()) != null) {
            records.add(record);
        }
        return records;
    }

    static AccountRecord parseLine(String line) {
        return new AccountRecord(
            field(line, ACCT_ID_OFF, ACCT_ID_LEN),
            field(line, STATUS_OFF,  STATUS_LEN),
            decimal(line, BAL_OFF,    BAL_LEN),
            decimal(line, CRLIM_OFF,  CRLIM_LEN),
            decimal(line, CASHLIM_OFF, CASHLIM_LEN),
            field(line, OPEN_OFF,   OPEN_LEN),
            field(line, EXPIR_OFF,  EXPIR_LEN),
            field(line, REISS_OFF,  REISS_LEN),
            decimal(line, CREDIT_OFF, CREDIT_LEN),
            decimal(line, DEBIT_OFF,  DEBIT_LEN),
            field(line, ZIP_OFF,    ZIP_LEN),
            field(line, GROUP_OFF,  GROUP_LEN)
        );
    }

    private static String field(String line, int offset, int length) {
        int end = Math.min(offset + length, line.length());
        return offset >= line.length() ? "" : line.substring(offset, end).strip();
    }

    private static BigDecimal decimal(String line, int offset, int length) {
        String raw = field(line, offset, length);
        return raw.isEmpty() ? BigDecimal.ZERO : SignedDecimalParser.parse(raw, 2);
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    @Override
    public Iterator<AccountRecord> iterator() {
        return new Iterator<>() {
            private AccountRecord next;
            private boolean done;

            @Override
            public boolean hasNext() {
                if (done) return false;
                if (next != null) return true;
                try {
                    next = readNext();
                    if (next == null) { done = true; return false; }
                    return true;
                } catch (IOException e) {
                    throw new RuntimeException("Error reading account file", e);
                }
            }

            @Override
            public AccountRecord next() {
                if (!hasNext()) throw new NoSuchElementException();
                AccountRecord r = next;
                next = null;
                return r;
            }
        };
    }
}
