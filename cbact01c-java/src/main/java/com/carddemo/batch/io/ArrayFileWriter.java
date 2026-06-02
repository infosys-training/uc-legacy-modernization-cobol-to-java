package com.carddemo.batch.io;

import com.carddemo.batch.model.AccountRecord;
import com.carddemo.batch.util.CobolDecimalFormatter;
import com.carddemo.batch.util.PackedDecimalUtil;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;

/**
 * Writes the ARRY-FILE records matching the COBOL FD ARRY-FILE layout.
 *
 * Record layout (110 bytes):
 *   ARR-ACCT-ID:              PIC 9(11)         11 bytes
 *   ARR-ACCT-BAL (OCCURS 5):
 *     ARR-ACCT-CURR-BAL:     PIC S9(10)V99     12 bytes (DISPLAY)
 *     ARR-ACCT-CURR-CYC-DEBIT: PIC S9(10)V99 COMP-3  7 bytes
 *     = 19 bytes per occurrence × 5 = 95 bytes
 *   ARR-FILLER:               PIC X(04)          4 bytes
 *   Total: 11 + 95 + 4 = 110 bytes
 *
 * Business logic from 1400-POPUL-ARRAY-RECORD:
 *   Entry 1: balance = ACCT-CURR-BAL, debit = 1005.00
 *   Entry 2: balance = ACCT-CURR-BAL, debit = 1525.00
 *   Entry 3: balance = -1025.00, debit = -2500.00
 *   Entry 4-5: zeros (from INITIALIZE)
 */
public class ArrayFileWriter implements AutoCloseable {

    private static final int RECORD_LENGTH = 110;
    private static final BigDecimal DEBIT_1 = new BigDecimal("1005.00");
    private static final BigDecimal DEBIT_2 = new BigDecimal("1525.00");
    private static final BigDecimal BAL_3 = new BigDecimal("-1025.00");
    private static final BigDecimal DEBIT_3 = new BigDecimal("-2500.00");

    private final OutputStream out;

    public ArrayFileWriter(OutputStream out) {
        this.out = out;
    }

    /**
     * Write one array record matching COBOL 1400-POPUL-ARRAY-RECORD + 1450-WRITE-ARRY-RECORD.
     */
    public void writeRecord(AccountRecord acct) throws IOException {
        byte[] record = new byte[RECORD_LENGTH];
        int pos = 0;

        // ARR-ACCT-ID: PIC 9(11)
        copyBytes(record, pos, acct.acctId(), 11);
        pos += 11;

        // Entry 1: balance = acct.currBal, debit = 1005.00
        pos = writeArrayEntry(record, pos, acct.currBal(), DEBIT_1);

        // Entry 2: balance = acct.currBal, debit = 1525.00
        pos = writeArrayEntry(record, pos, acct.currBal(), DEBIT_2);

        // Entry 3: balance = -1025.00, debit = -2500.00
        pos = writeArrayEntry(record, pos, BAL_3, DEBIT_3);

        // Entry 4: zeros (from INITIALIZE)
        pos = writeArrayEntry(record, pos, BigDecimal.ZERO, BigDecimal.ZERO);

        // Entry 5: zeros (from INITIALIZE)
        pos = writeArrayEntry(record, pos, BigDecimal.ZERO, BigDecimal.ZERO);

        // ARR-FILLER: PIC X(04) - spaces
        copyBytes(record, pos, "    ", 4);

        out.write(record);
    }

    private int writeArrayEntry(byte[] record, int pos, BigDecimal balance, BigDecimal debit) {
        // ARR-ACCT-CURR-BAL: PIC S9(10)V99 DISPLAY (12 bytes)
        copyBytes(record, pos, CobolDecimalFormatter.format(balance, 12, 2), 12);
        pos += 12;

        // ARR-ACCT-CURR-CYC-DEBIT: PIC S9(10)V99 COMP-3 (7 bytes)
        byte[] packed = PackedDecimalUtil.encode(debit, 12, 2);
        System.arraycopy(packed, 0, record, pos, packed.length);
        pos += packed.length;

        return pos;
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
