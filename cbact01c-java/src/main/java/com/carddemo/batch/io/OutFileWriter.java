package com.carddemo.batch.io;

import com.carddemo.batch.model.AccountRecord;
import com.carddemo.batch.util.CobolDecimalFormatter;
import com.carddemo.batch.util.DateFormatter;
import com.carddemo.batch.util.PackedDecimalUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * Writes the OUT-FILE (OUTFILE) records matching the COBOL FD OUT-FILE layout.
 *
 * Record layout (107 bytes):
 *   OUT-ACCT-ID:                PIC 9(11)        11 bytes
 *   OUT-ACCT-ACTIVE-STATUS:     PIC X(01)         1 byte
 *   OUT-ACCT-CURR-BAL:          PIC S9(10)V99    12 bytes (DISPLAY overpunch)
 *   OUT-ACCT-CREDIT-LIMIT:      PIC S9(10)V99    12 bytes (DISPLAY overpunch)
 *   OUT-ACCT-CASH-CREDIT-LIMIT: PIC S9(10)V99    12 bytes (DISPLAY overpunch)
 *   OUT-ACCT-OPEN-DATE:         PIC X(10)        10 bytes
 *   OUT-ACCT-EXPIRAION-DATE:    PIC X(10)        10 bytes
 *   OUT-ACCT-REISSUE-DATE:      PIC X(10)        10 bytes (YYYYMMDD + 2 spaces)
 *   OUT-ACCT-CURR-CYC-CREDIT:   PIC S9(10)V99    12 bytes (DISPLAY overpunch)
 *   OUT-ACCT-CURR-CYC-DEBIT:    PIC S9(10)V99 COMP-3  7 bytes (packed decimal)
 *   OUT-ACCT-GROUP-ID:          PIC X(10)        10 bytes
 *   Total: 107 bytes
 */
public class OutFileWriter implements AutoCloseable {

    private static final BigDecimal DEFAULT_DEBIT = new BigDecimal("2525.00");
    private static final int RECORD_LENGTH = 107;

    private final OutputStream out;

    public OutFileWriter(OutputStream out) {
        this.out = out;
    }

    /**
     * Write one output record matching COBOL 1300-POPUL-ACCT-RECORD + 1350-WRITE-ACCT-RECORD logic.
     */
    public void writeRecord(AccountRecord acct) throws IOException {
        byte[] record = new byte[RECORD_LENGTH];
        int pos = 0;

        // OUT-ACCT-ID: PIC 9(11)
        copyBytes(record, pos, acct.acctId(), 11);
        pos += 11;

        // OUT-ACCT-ACTIVE-STATUS: PIC X(01)
        copyBytes(record, pos, acct.activeStatus(), 1);
        pos += 1;

        // OUT-ACCT-CURR-BAL: PIC S9(10)V99 DISPLAY
        copyBytes(record, pos, CobolDecimalFormatter.format(acct.currBal(), 12, 2), 12);
        pos += 12;

        // OUT-ACCT-CREDIT-LIMIT: PIC S9(10)V99 DISPLAY
        copyBytes(record, pos, CobolDecimalFormatter.format(acct.creditLimit(), 12, 2), 12);
        pos += 12;

        // OUT-ACCT-CASH-CREDIT-LIMIT: PIC S9(10)V99 DISPLAY
        copyBytes(record, pos, CobolDecimalFormatter.format(acct.cashCreditLimit(), 12, 2), 12);
        pos += 12;

        // OUT-ACCT-OPEN-DATE: PIC X(10)
        copyBytes(record, pos, acct.openDate(), 10);
        pos += 10;

        // OUT-ACCT-EXPIRAION-DATE: PIC X(10)
        copyBytes(record, pos, acct.expirationDate(), 10);
        pos += 10;

        // OUT-ACCT-REISSUE-DATE: PIC X(10) - formatted via COBDATFT (YYYY-MM-DD → YYYYMMDD)
        String formattedDate = DateFormatter.format(acct.reissueDate(), '2', '2');
        // formattedDate is 20 chars; MOVE to PIC X(10) truncates to first 10 chars
        copyBytes(record, pos, formattedDate.substring(0, 10), 10);
        pos += 10;

        // OUT-ACCT-CURR-CYC-CREDIT: PIC S9(10)V99 DISPLAY
        copyBytes(record, pos, CobolDecimalFormatter.format(acct.currCycCredit(), 12, 2), 12);
        pos += 12;

        // OUT-ACCT-CURR-CYC-DEBIT: PIC S9(10)V99 COMP-3
        // Business rule: if debit is zero, substitute 2525.00
        BigDecimal debitValue = acct.currCycDebit().signum() == 0 ? DEFAULT_DEBIT : acct.currCycDebit();
        byte[] packed = PackedDecimalUtil.encode(debitValue, 12, 2);
        System.arraycopy(packed, 0, record, pos, packed.length);
        pos += packed.length;

        // OUT-ACCT-GROUP-ID: PIC X(10)
        copyBytes(record, pos, acct.groupId(), 10);

        out.write(record);
    }

    private void copyBytes(byte[] dest, int offset, String value, int length) {
        byte[] src = padRight(value, length).getBytes(java.nio.charset.StandardCharsets.ISO_8859_1);
        System.arraycopy(src, 0, dest, offset, length);
    }

    private String padRight(String s, int length) {
        if (s == null) {
            return " ".repeat(length);
        }
        if (s.length() >= length) {
            return s.substring(0, length);
        }
        return s + " ".repeat(length - s.length());
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
