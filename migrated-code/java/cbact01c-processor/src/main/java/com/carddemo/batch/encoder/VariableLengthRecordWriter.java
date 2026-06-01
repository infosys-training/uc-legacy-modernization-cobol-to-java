package com.carddemo.batch.encoder;

import java.io.*;

public class VariableLengthRecordWriter implements Closeable {

    private final DataOutputStream out;

    public VariableLengthRecordWriter(OutputStream outputStream) {
        this.out = new DataOutputStream(outputStream);
    }

    public void writeRecord(byte[] data) throws IOException {
        int totalLen = data.length + 4;
        out.writeShort(totalLen);
        out.writeShort(0);
        out.write(data);
    }

    @Override
    public void close() throws IOException {
        out.close();
    }
}
