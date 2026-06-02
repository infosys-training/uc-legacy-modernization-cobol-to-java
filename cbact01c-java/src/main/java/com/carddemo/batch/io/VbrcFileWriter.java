package com.carddemo.batch.io;

import com.carddemo.batch.model.AccountRecord;
import com.carddemo.batch.util.CobolDecimalFormatter;
import com.carddemo.batch.util.DateFormatter;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Writes the VBRC-FILE (variable-length records) matching the COBOL FD VBRC-FILE layout.
 *
 * COBOL RECORDING MODE V means each record is preceded by a 4-byte Record Descriptor Word (RDW)
 * on mainframe. For this rewrite, we write raw variable-length records as the COBOL program
 * sets WS-RECD-LEN and moves data to VBR-REC before WRITE.
 *
 * Two records per input account:
 *   VB1 (12 bytes): VB1-ACCT-ID PIC 9(11) + VB1-ACCT-ACTIVE-STATUS PIC X(01)
 *   VB2 (39 bytes): VB2-ACCT-ID PIC 9(11) + VB2-ACCT-CURR-BAL PIC S9(10)V99
 *                   + VB2-ACCT-CREDIT-LIMIT PIC S9(10)V99 + VB2-ACCT-REISSUE-YYYY PIC X(04)
 */
public class VbrcFileWriter implements AutoCloseable {

    private static final int VB1_LENGTH = 12;
    private static final int VB2_LENGTH = 39;

    private final OutputStream out;
    private final boolean writeRdw;

    /**
     * @param out output stream
     * @param writeRdw if true, prefix each record with a 4-byte RDW (mainframe VB format);
     *                 if false, write raw records with length prefix as 2-byte short (GnuCOBOL compatible)
     */
    public VbrcFileWriter(OutputStream out, boolean writeRdw) {
        this.out = out;
        this.writeRdw = writeRdw;
    }

    public VbrcFileWriter(OutputStream out) {
        this(out, false);
    }

    /**
     * Write VB1 and VB2 records for one account.
     * Maps to COBOL 1500-POPUL-VBRC-RECORD + 1550-WRITE-VB1-RECORD + 1575-WRITE-VB2-RECORD.
     */
    public void writeRecords(AccountRecord acct) throws IOException {
        writeVb1(acct);
        writeVb2(acct);
    }

    private void writeVb1(AccountRecord acct) throws IOException {
        byte[] record = new byte[VB1_LENGTH];
        copyBytes(record, 0, acct.acctId(), 11);
        copyBytes(record, 11, acct.activeStatus(), 1);

        writeVariableRecord(record, VB1_LENGTH);
    }

    private void writeVb2(AccountRecord acct) throws IOException {
        byte[] record = new byte[VB2_LENGTH];
        int pos = 0;

        // VB2-ACCT-ID: PIC 9(11)
        copyBytes(record, pos, acct.acctId(), 11);
        pos += 11;

        // VB2-ACCT-CURR-BAL: PIC S9(10)V99 (DISPLAY overpunch)
        copyBytes(record, pos, CobolDecimalFormatter.format(acct.currBal(), 12, 2), 12);
        pos += 12;

        // VB2-ACCT-CREDIT-LIMIT: PIC S9(10)V99 (DISPLAY overpunch)
        copyBytes(record, pos, CobolDecimalFormatter.format(acct.creditLimit(), 12, 2), 12);
        pos += 12;

        // VB2-ACCT-REISSUE-YYYY: PIC X(04) - year extracted from reissue date
        String year = DateFormatter.extractYear(acct.reissueDate());
        copyBytes(record, pos, year, 4);

        writeVariableRecord(record, VB2_LENGTH);
    }

    private void writeVariableRecord(byte[] data, int length) throws IOException {
        if (writeRdw) {
            // Mainframe RDW: 4 bytes (2-byte length including RDW + 2 bytes zeros)
            ByteBuffer rdw = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
            rdw.putShort((short) (length + 4));
            rdw.putShort((short) 0);
            out.write(rdw.array());
        }
        out.write(data, 0, length);
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
