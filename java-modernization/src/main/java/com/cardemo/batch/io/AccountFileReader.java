package com.cardemo.batch.io;

import com.cardemo.batch.model.AccountRecord;
import com.cardemo.batch.util.ZonedDecimalParser;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.cardemo.batch.model.AccountRecord.*;

/**
 * Reads fixed-width account records from an ASCII data file that mirrors
 * the COBOL VSAM KSDS layout described in {@code CVACT01Y.cpy}.
 */
public final class AccountFileReader implements Closeable, Iterable<AccountRecord> {

    private final BufferedReader reader;

    public AccountFileReader(Path path) throws IOException {
        this.reader = Files.newBufferedReader(path);
    }

    public AccountRecord readNext() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        return parseLine(line);
    }

    public Stream<AccountRecord> stream() {
        Spliterator<AccountRecord> spliterator = Spliterators.spliteratorUnknownSize(
                iterator(), Spliterator.ORDERED | Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false);
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
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
                if (next == null) {
                    done = true;
                    return false;
                }
                return true;
            }

            @Override
            public AccountRecord next() {
                if (!hasNext()) throw new NoSuchElementException();
                AccountRecord result = next;
                next = null;
                return result;
            }
        };
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }

    /**
     * Parses a single fixed-width line into an {@link AccountRecord}.
     * The line may be shorter than 300 chars if trailing filler is trimmed;
     * missing positions are treated as spaces.
     */
    public static AccountRecord parseLine(String line) {
        String padded = padRight(line, RECORD_LENGTH);

        long acctId = Long.parseLong(
                padded.substring(ACCT_ID_OFF, ACCT_ID_OFF + ACCT_ID_LEN));

        String activeStatus = padded.substring(
                ACTIVE_STATUS_OFF, ACTIVE_STATUS_OFF + ACTIVE_STATUS_LEN);

        var currBal = ZonedDecimalParser.parse(
                padded.substring(CURR_BAL_OFF, CURR_BAL_OFF + CURR_BAL_LEN), 2);

        var creditLimit = ZonedDecimalParser.parse(
                padded.substring(CREDIT_LIMIT_OFF, CREDIT_LIMIT_OFF + CREDIT_LIMIT_LEN), 2);

        var cashCreditLimit = ZonedDecimalParser.parse(
                padded.substring(CASH_CREDIT_LIMIT_OFF,
                        CASH_CREDIT_LIMIT_OFF + CASH_CREDIT_LIMIT_LEN), 2);

        String openDate = padded.substring(
                OPEN_DATE_OFF, OPEN_DATE_OFF + OPEN_DATE_LEN).trim();

        String expirationDate = padded.substring(
                EXPIRATION_DATE_OFF, EXPIRATION_DATE_OFF + EXPIRATION_DATE_LEN).trim();

        String reissueDate = padded.substring(
                REISSUE_DATE_OFF, REISSUE_DATE_OFF + REISSUE_DATE_LEN).trim();

        var currCycCredit = ZonedDecimalParser.parse(
                padded.substring(CURR_CYC_CREDIT_OFF,
                        CURR_CYC_CREDIT_OFF + CURR_CYC_CREDIT_LEN), 2);

        var currCycDebit = ZonedDecimalParser.parse(
                padded.substring(CURR_CYC_DEBIT_OFF,
                        CURR_CYC_DEBIT_OFF + CURR_CYC_DEBIT_LEN), 2);

        String addrZip = padded.substring(
                ADDR_ZIP_OFF, ADDR_ZIP_OFF + ADDR_ZIP_LEN).trim();

        String groupId = padded.substring(
                GROUP_ID_OFF, GROUP_ID_OFF + GROUP_ID_LEN).trim();

        return new AccountRecord(
                acctId, activeStatus, currBal, creditLimit, cashCreditLimit,
                openDate, expirationDate, reissueDate,
                currCycCredit, currCycDebit, addrZip, groupId);
    }

    private static String padRight(String s, int len) {
        if (s.length() >= len) return s;
        return s + " ".repeat(len - s.length());
    }
}
