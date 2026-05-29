package com.cardemo.batch.cbexport.io;

import com.cardemo.batch.cbexport.model.AccountRecord;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static com.cardemo.batch.cbexport.io.CobolFieldParser.*;

/**
 * Reads fixed-width account records (CVACT01Y, RECLN 300).
 *
 * Layout:
 *   ACCT-ID               PIC 9(11)       offset 0   len 11
 *   ACCT-ACTIVE-STATUS    PIC X(01)       offset 11  len 1
 *   ACCT-CURR-BAL         PIC S9(10)V99   offset 12  len 13  (10+2+overpunch)
 *   ACCT-CREDIT-LIMIT     PIC S9(10)V99   offset 25  len 13
 *   ACCT-CASH-CREDIT-LIM  PIC S9(10)V99   offset 38  len 13
 *   ACCT-OPEN-DATE        PIC X(10)       offset 51  len 10
 *   ACCT-EXPIRAION-DATE   PIC X(10)       offset 61  len 10
 *   ACCT-REISSUE-DATE     PIC X(10)       offset 71  len 10
 *   ACCT-CURR-CYC-CREDIT  PIC S9(10)V99   offset 81  len 13
 *   ACCT-CURR-CYC-DEBIT   PIC S9(10)V99   offset 94  len 13
 *   ACCT-ADDR-ZIP         PIC X(10)       offset 107 len 10
 *   ACCT-GROUP-ID         PIC X(10)       offset 117 len 10
 *   FILLER                PIC X(178)      offset 127 len 178  (total=305? no)
 *
 *   Note: S9(10)V99 zoned decimal = 12 digits displayed.
 *   PIC S9(10)V99 = 12 character positions (10 integer + 2 decimal).
 *   But the sign is embedded in the trailing overpunch, so still 12 chars.
 */
public final class AccountFileReader {

    private static final int ACCT_ID_OFFSET = 0;
    private static final int ACCT_ID_LEN = 11;
    private static final int STATUS_OFFSET = 11;
    private static final int STATUS_LEN = 1;
    private static final int CURR_BAL_OFFSET = 12;
    private static final int SIGNED_DECIMAL_LEN = 12; // S9(10)V99 = 12 chars
    private static final int CREDIT_LIMIT_OFFSET = 24;
    private static final int CASH_CREDIT_OFFSET = 36;
    private static final int OPEN_DATE_OFFSET = 48;
    private static final int DATE_LEN = 10;
    private static final int EXPIRY_DATE_OFFSET = 58;
    private static final int REISSUE_DATE_OFFSET = 68;
    private static final int CYC_CREDIT_OFFSET = 78;
    private static final int CYC_DEBIT_OFFSET = 90;
    private static final int ZIP_OFFSET = 102;
    private static final int ZIP_LEN = 10;
    private static final int GROUP_OFFSET = 112;
    private static final int GROUP_LEN = 10;

    private AccountFileReader() {}

    public static List<AccountRecord> readAll(Path path) throws IOException {
        List<AccountRecord> records = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) continue;
                records.add(parseLine(line));
            }
        }
        return records;
    }

    public static AccountRecord parseLine(String line) {
        return new AccountRecord(
                extractString(line, ACCT_ID_OFFSET, ACCT_ID_LEN),
                extractString(line, STATUS_OFFSET, STATUS_LEN),
                extractSignedDecimal(line, CURR_BAL_OFFSET, SIGNED_DECIMAL_LEN, 2),
                extractSignedDecimal(line, CREDIT_LIMIT_OFFSET, SIGNED_DECIMAL_LEN, 2),
                extractSignedDecimal(line, CASH_CREDIT_OFFSET, SIGNED_DECIMAL_LEN, 2),
                extractString(line, OPEN_DATE_OFFSET, DATE_LEN),
                extractString(line, EXPIRY_DATE_OFFSET, DATE_LEN),
                extractString(line, REISSUE_DATE_OFFSET, DATE_LEN),
                extractSignedDecimal(line, CYC_CREDIT_OFFSET, SIGNED_DECIMAL_LEN, 2),
                extractSignedDecimal(line, CYC_DEBIT_OFFSET, SIGNED_DECIMAL_LEN, 2),
                extractString(line, ZIP_OFFSET, ZIP_LEN),
                extractString(line, GROUP_OFFSET, GROUP_LEN)
        );
    }
}
