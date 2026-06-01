package com.carddemo.batch;

import com.carddemo.batch.encoder.Comp3Encoder;
import com.carddemo.batch.encoder.VariableLengthRecordWriter;
import com.carddemo.batch.encoder.ZonedDecimalEncoder;
import com.carddemo.batch.model.*;

import java.io.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class CobolBinaryOutputWriter implements OutputWriter {

    private static final int OUT_RECORD_LEN = 107;
    private static final int ARRAY_RECORD_LEN = 110;
    private static final int ZONED_DECIMAL_DIGITS = 12;
    private static final int COMP3_DIGITS = 12;

    private final Comp3Encoder comp3Encoder = new Comp3Encoder();
    private final ZonedDecimalEncoder zonedEncoder = new ZonedDecimalEncoder();

    private final OutputStream outFileStream;
    private final OutputStream arryFileStream;
    private final VariableLengthRecordWriter vbrcWriter;

    public CobolBinaryOutputWriter(Path outputDir) throws IOException {
        outFileStream = new BufferedOutputStream(
                Files.newOutputStream(outputDir.resolve("out-accounts.bin")));
        arryFileStream = new BufferedOutputStream(
                Files.newOutputStream(outputDir.resolve("array-records.bin")));
        vbrcWriter = new VariableLengthRecordWriter(
                new BufferedOutputStream(
                        Files.newOutputStream(outputDir.resolve("vb-records.bin"))));
    }

    @Override
    public void writeOutRecord(OutAccountRecord rec) throws IOException {
        byte[] buf = new byte[OUT_RECORD_LEN];
        int pos = 0;

        pos = writeAsciiLeftPadZero(buf, pos, rec.acctId(), 11);
        pos = writeAsciiRightPadSpace(buf, pos, rec.activeStatus(), 1);
        pos = writeZonedDecimal(buf, pos, rec.currentBalance());
        pos = writeZonedDecimal(buf, pos, rec.creditLimit());
        pos = writeZonedDecimal(buf, pos, rec.cashCreditLimit());
        pos = writeAsciiRightPadSpace(buf, pos, rec.openDate(), 10);
        pos = writeAsciiRightPadSpace(buf, pos, rec.expirationDate(), 10);
        pos = writeAsciiRightPadSpace(buf, pos, rec.reissueDate(), 10);
        pos = writeZonedDecimal(buf, pos, rec.currentCycleCredit());
        pos = writeComp3(buf, pos, rec.currentCycleDebit());
        writeAsciiRightPadSpace(buf, pos, rec.groupId(), 10);

        outFileStream.write(buf);
    }

    @Override
    public void writeArrayRecord(ArrayRecord rec) throws IOException {
        byte[] buf = new byte[ARRAY_RECORD_LEN];
        int pos = 0;

        pos = writeAsciiLeftPadZero(buf, pos, rec.acctId(), 11);

        for (ArrayElement elem : rec.elements()) {
            pos = writeZonedDecimal(buf, pos, elem.currentBalance());
            pos = writeComp3(buf, pos, elem.currentCycleDebit());
        }

        writeAsciiRightPadSpace(buf, pos, rec.filler(), 4);

        arryFileStream.write(buf);
    }

    @Override
    public void writeVbRecord1(VbRecord1 rec) throws IOException {
        byte[] data = new byte[12];
        int pos = 0;
        pos = writeAsciiLeftPadZero(data, pos, rec.acctId(), 11);
        writeAsciiRightPadSpace(data, pos, rec.activeStatus(), 1);

        vbrcWriter.writeRecord(data);
    }

    @Override
    public void writeVbRecord2(VbRecord2 rec) throws IOException {
        byte[] data = new byte[39];
        int pos = 0;
        pos = writeAsciiLeftPadZero(data, pos, rec.acctId(), 11);
        pos = writeZonedDecimal(data, pos, rec.currentBalance());
        pos = writeZonedDecimal(data, pos, rec.creditLimit());
        writeAsciiRightPadSpace(data, pos, rec.reissueYyyy(), 4);

        vbrcWriter.writeRecord(data);
    }

    @Override
    public void close() throws IOException {
        outFileStream.close();
        arryFileStream.close();
        vbrcWriter.close();
    }

    private int writeAsciiLeftPadZero(byte[] buf, int offset, String value, int width) {
        String padded = value == null ? "" : value;
        while (padded.length() < width) {
            padded = "0" + padded;
        }
        byte[] bytes = padded.substring(0, width).getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(bytes, 0, buf, offset, width);
        return offset + width;
    }

    private int writeAsciiRightPadSpace(byte[] buf, int offset, String value, int width) {
        String padded = value == null ? "" : value;
        while (padded.length() < width) {
            padded = padded + " ";
        }
        byte[] bytes = padded.substring(0, width).getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(bytes, 0, buf, offset, width);
        return offset + width;
    }

    private int writeZonedDecimal(byte[] buf, int offset, BigDecimal value) {
        String encoded = zonedEncoder.encode(value, ZONED_DECIMAL_DIGITS);
        byte[] bytes = encoded.getBytes(StandardCharsets.US_ASCII);
        System.arraycopy(bytes, 0, buf, offset, ZONED_DECIMAL_DIGITS);
        return offset + ZONED_DECIMAL_DIGITS;
    }

    private int writeComp3(byte[] buf, int offset, BigDecimal value) {
        byte[] packed = comp3Encoder.encode(value, COMP3_DIGITS);
        System.arraycopy(packed, 0, buf, offset, packed.length);
        return offset + packed.length;
    }
}
