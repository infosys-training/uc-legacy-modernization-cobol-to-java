package com.carddemo.io;

import com.carddemo.model.AccountRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads the indexed VSAM account file (ACCTFILE).
 *
 * Replaces COBOL:
 *   SELECT ACCTFILE-FILE ASSIGN TO ACCTFILE
 *          ORGANIZATION IS INDEXED
 *          ACCESS MODE IS SEQUENTIAL
 *          RECORD KEY IS FD-ACCT-ID
 *
 * The ASCII data file (acctdata.txt) has fixed-width 300-byte records.
 * Layout matches CVACT01Y.cpy.
 *
 * COBOL signed numeric with trailing overpunch:
 *   PIC S9(10)V99 uses zoned decimal with the sign embedded in the last byte.
 *   '{' = +0, 'A'-'I' = +1..+9, '}' = -0, 'J'-'R' = -1..-9
 */
public class AccountFileReader {

    public List<AccountRecord> readAll(Path filePath) throws IOException {
        List<AccountRecord> records = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                AccountRecord record = parseLine(line);
                records.add(record);
            }
        }

        return records;
    }

    AccountRecord parseLine(String line) {
        // Pad line to 300 characters if shorter
        if (line.length() < 300) {
            line = String.format("%-300s", line);
        }

        AccountRecord rec = new AccountRecord();
        int pos = 0;

        // ACCT-ID PIC 9(11)
        rec.setAcctId(Long.parseLong(line.substring(pos, pos + 11).trim()));
        pos += 11;

        // ACCT-ACTIVE-STATUS PIC X(01)
        rec.setActiveStatus(line.charAt(pos));
        pos += 1;

        // ACCT-CURR-BAL PIC S9(10)V99 (12 chars zoned decimal)
        rec.setCurrentBalance(parseSignedDecimal(line.substring(pos, pos + 12), 2));
        pos += 12;

        // ACCT-CREDIT-LIMIT PIC S9(10)V99
        rec.setCreditLimit(parseSignedDecimal(line.substring(pos, pos + 12), 2));
        pos += 12;

        // ACCT-CASH-CREDIT-LIMIT PIC S9(10)V99
        rec.setCashCreditLimit(parseSignedDecimal(line.substring(pos, pos + 12), 2));
        pos += 12;

        // ACCT-OPEN-DATE PIC X(10)
        rec.setOpenDate(line.substring(pos, pos + 10).trim());
        pos += 10;

        // ACCT-EXPIRAION-DATE PIC X(10)
        rec.setExpirationDate(line.substring(pos, pos + 10).trim());
        pos += 10;

        // ACCT-REISSUE-DATE PIC X(10)
        rec.setReissueDate(line.substring(pos, pos + 10).trim());
        pos += 10;

        // ACCT-CURR-CYC-CREDIT PIC S9(10)V99
        rec.setCurrentCycleCredit(parseSignedDecimal(line.substring(pos, pos + 12), 2));
        pos += 12;

        // ACCT-CURR-CYC-DEBIT PIC S9(10)V99
        rec.setCurrentCycleDebit(parseSignedDecimal(line.substring(pos, pos + 12), 2));
        pos += 12;

        // ACCT-ADDR-ZIP PIC X(10)
        rec.setAddrZip(line.substring(pos, pos + 10).trim());
        pos += 10;

        // ACCT-GROUP-ID PIC X(10)
        rec.setGroupId(line.substring(pos, pos + 10).trim());

        return rec;
    }

    /**
     * Parses a COBOL zoned decimal with trailing overpunch sign.
     *
     * In ASCII mode, the sign is encoded in the last character:
     *   '{' = +0, 'A'=+1, 'B'=+2, ..., 'I'=+9
     *   '}' = -0, 'J'=-1, 'K'=-2, ..., 'R'=-9
     */
    public static BigDecimal parseSignedDecimal(String raw, int decimalPlaces) {
        if (raw == null || raw.isBlank()) {
            return BigDecimal.ZERO;
        }

        char lastChar = raw.charAt(raw.length() - 1);
        String digits = raw.substring(0, raw.length() - 1);
        int lastDigit;
        boolean negative = false;

        if (lastChar >= '0' && lastChar <= '9') {
            lastDigit = lastChar - '0';
        } else if (lastChar == '{') {
            lastDigit = 0;
        } else if (lastChar >= 'A' && lastChar <= 'I') {
            lastDigit = lastChar - 'A' + 1;
        } else if (lastChar == '}') {
            lastDigit = 0;
            negative = true;
        } else if (lastChar >= 'J' && lastChar <= 'R') {
            lastDigit = lastChar - 'J' + 1;
            negative = true;
        } else {
            lastDigit = 0;
        }

        String fullDigits = digits + lastDigit;
        BigDecimal value = new BigDecimal(fullDigits.trim()).movePointLeft(decimalPlaces);
        return negative ? value.negate() : value;
    }
}
