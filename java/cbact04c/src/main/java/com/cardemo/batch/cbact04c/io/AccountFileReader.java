package com.cardemo.batch.cbact04c.io;

import com.cardemo.batch.cbact04c.model.AccountRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads ACCOUNT-FILE (account master, RECLN 300) and supports keyed lookup + rewrite.
 * Layout: ACCT-ID(11) + ACCT-ACTIVE-STATUS(1) + ACCT-CURR-BAL(12 signed) +
 *         ACCT-CREDIT-LIMIT(12 signed) + ACCT-CASH-CREDIT-LIMIT(12 signed) +
 *         ACCT-OPEN-DATE(10) + ACCT-EXPIRAION-DATE(10) + ACCT-REISSUE-DATE(10) +
 *         ACCT-CURR-CYC-CREDIT(12 signed) + ACCT-CURR-CYC-DEBIT(12 signed) +
 *         ACCT-ADDR-ZIP(10) + ACCT-GROUP-ID(10) + FILLER(178)
 */
public class AccountFileReader {

    private final Path filePath;

    public AccountFileReader(Path filePath) {
        this.filePath = filePath;
    }

    /**
     * Reads all account records and returns a map keyed by account ID.
     */
    public Map<String, AccountRecord> readAllByAccountId() throws IOException {
        Map<String, AccountRecord> map = new LinkedHashMap<>();
        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                AccountRecord rec = parseLine(line);
                map.put(rec.getAccountId(), rec);
            }
        }
        return map;
    }

    public static AccountRecord parseLine(String line) {
        int pos = 0;
        AccountRecord rec = new AccountRecord();

        // ACCT-ID PIC 9(11) -> 11 chars
        rec.setAccountId(CobolFieldParser.extractField(line, pos, 11)); pos += 11;
        // ACCT-ACTIVE-STATUS PIC X(01) -> 1 char
        rec.setActiveStatus(CobolFieldParser.extractField(line, pos, 1)); pos += 1;
        // ACCT-CURR-BAL PIC S9(10)V99 -> 12 chars (10+2=12 digits, trailing overpunch)
        rec.setCurrentBalance(CobolFieldParser.parseSignedDecimal(CobolFieldParser.extractField(line, pos, 12), 2)); pos += 12;
        // ACCT-CREDIT-LIMIT PIC S9(10)V99 -> 12 chars
        rec.setCreditLimit(CobolFieldParser.parseSignedDecimal(CobolFieldParser.extractField(line, pos, 12), 2)); pos += 12;
        // ACCT-CASH-CREDIT-LIMIT PIC S9(10)V99 -> 12 chars
        rec.setCashCreditLimit(CobolFieldParser.parseSignedDecimal(CobolFieldParser.extractField(line, pos, 12), 2)); pos += 12;
        // ACCT-OPEN-DATE PIC X(10) -> 10 chars
        rec.setOpenDate(CobolFieldParser.extractField(line, pos, 10)); pos += 10;
        // ACCT-EXPIRAION-DATE PIC X(10) -> 10 chars
        rec.setExpirationDate(CobolFieldParser.extractField(line, pos, 10)); pos += 10;
        // ACCT-REISSUE-DATE PIC X(10) -> 10 chars
        rec.setReissueDate(CobolFieldParser.extractField(line, pos, 10)); pos += 10;
        // ACCT-CURR-CYC-CREDIT PIC S9(10)V99 -> 12 chars
        rec.setCurrentCycleCredit(CobolFieldParser.parseSignedDecimal(CobolFieldParser.extractField(line, pos, 12), 2)); pos += 12;
        // ACCT-CURR-CYC-DEBIT PIC S9(10)V99 -> 12 chars
        rec.setCurrentCycleDebit(CobolFieldParser.parseSignedDecimal(CobolFieldParser.extractField(line, pos, 12), 2)); pos += 12;
        // ACCT-ADDR-ZIP PIC X(10) -> 10 chars
        rec.setAddressZip(CobolFieldParser.extractField(line, pos, 10)); pos += 10;
        // ACCT-GROUP-ID PIC X(10) -> 10 chars
        rec.setGroupId(CobolFieldParser.extractField(line, pos, 10));

        return rec;
    }

    /**
     * Rewrites the account file with updated records.
     * Emulates COBOL REWRITE by writing all records back.
     */
    public static void rewriteAll(Path outputPath, Map<String, AccountRecord> accounts) throws IOException {
        var lines = new java.util.ArrayList<String>();
        for (AccountRecord rec : accounts.values()) {
            lines.add(rec.toFixedWidth());
        }
        Files.write(outputPath, lines);
    }
}
