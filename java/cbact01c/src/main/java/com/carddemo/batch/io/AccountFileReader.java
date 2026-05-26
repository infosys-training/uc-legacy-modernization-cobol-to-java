package com.carddemo.batch.io;

import com.carddemo.batch.model.AccountRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Reads 300-byte fixed-length account records from the input file.
 * Mirrors the COBOL READ ACCTFILE-FILE INTO ACCOUNT-RECORD logic.
 *
 * The input file uses one record per line (ASCII/text representation of the
 * VSAM KSDS records). Each line is exactly 300 characters (padded with spaces).
 */
public final class AccountFileReader implements AutoCloseable {

    private final BufferedReader reader;

    public AccountFileReader(Path inputFile) throws IOException {
        this.reader = Files.newBufferedReader(inputFile);
    }

    /**
     * Reads the next account record, or {@code null} if end-of-file.
     */
    public AccountRecord readNext() throws IOException {
        String line = reader.readLine();
        if (line == null) {
            return null;
        }
        // Pad to 300 chars if shorter (defensive)
        if (line.length() < AccountRecord.RECORD_LENGTH) {
            line = String.format("%-" + AccountRecord.RECORD_LENGTH + "s", line);
        }
        return parseLine(line);
    }

    /**
     * Returns a Stream of all account records in the file.
     */
    public Stream<AccountRecord> stream() {
        Iterator<AccountRecord> iterator = new Iterator<>() {
            private AccountRecord next;
            private boolean done = false;

            @Override
            public boolean hasNext() {
                if (done) return false;
                if (next != null) return true;
                try {
                    next = readNext();
                    if (next == null) {
                        done = true;
                        return false;
                    }
                    return true;
                } catch (IOException e) {
                    throw new java.io.UncheckedIOException(e);
                }
            }

            @Override
            public AccountRecord next() {
                if (!hasNext()) throw new NoSuchElementException();
                AccountRecord result = next;
                next = null;
                return result;
            }
        };

        Spliterator<AccountRecord> spliterator = Spliterators.spliteratorUnknownSize(
                iterator, Spliterator.ORDERED | Spliterator.NONNULL);
        return StreamSupport.stream(spliterator, false);
    }

    public static AccountRecord parseLine(String line) {
        int pos = 0;

        // ACCT-ID PIC 9(11)
        long acctId = Long.parseLong(line.substring(pos, pos + 11).trim());
        pos += 11;

        // ACCT-ACTIVE-STATUS PIC X(01)
        char activeStatus = line.charAt(pos);
        pos += 1;

        // ACCT-CURR-BAL PIC S9(10)V99
        BigDecimal currBal = CobolDecimalParser.parseSignedDecimal(line.substring(pos, pos + 12), 2);
        pos += 12;

        // ACCT-CREDIT-LIMIT PIC S9(10)V99
        BigDecimal creditLimit = CobolDecimalParser.parseSignedDecimal(line.substring(pos, pos + 12), 2);
        pos += 12;

        // ACCT-CASH-CREDIT-LIMIT PIC S9(10)V99
        BigDecimal cashCreditLimit = CobolDecimalParser.parseSignedDecimal(line.substring(pos, pos + 12), 2);
        pos += 12;

        // ACCT-OPEN-DATE PIC X(10)
        String openDate = line.substring(pos, pos + 10);
        pos += 10;

        // ACCT-EXPIRAION-DATE PIC X(10)
        String expirationDate = line.substring(pos, pos + 10);
        pos += 10;

        // ACCT-REISSUE-DATE PIC X(10)
        String reissueDate = line.substring(pos, pos + 10);
        pos += 10;

        // ACCT-CURR-CYC-CREDIT PIC S9(10)V99
        BigDecimal currCycCredit = CobolDecimalParser.parseSignedDecimal(line.substring(pos, pos + 12), 2);
        pos += 12;

        // ACCT-CURR-CYC-DEBIT PIC S9(10)V99
        BigDecimal currCycDebit = CobolDecimalParser.parseSignedDecimal(line.substring(pos, pos + 12), 2);
        pos += 12;

        // ACCT-ADDR-ZIP PIC X(10)
        String addrZip = line.substring(pos, pos + 10);
        pos += 10;

        // ACCT-GROUP-ID PIC X(10)
        String groupId = line.substring(pos, pos + 10);

        return new AccountRecord(
                acctId, activeStatus, currBal, creditLimit, cashCreditLimit,
                openDate, expirationDate, reissueDate,
                currCycCredit, currCycDebit, addrZip, groupId
        );
    }

    @Override
    public void close() throws IOException {
        reader.close();
    }
}
