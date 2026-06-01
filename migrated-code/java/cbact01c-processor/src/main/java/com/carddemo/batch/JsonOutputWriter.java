package com.carddemo.batch;

import com.carddemo.batch.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.*;
import java.nio.file.*;

/**
 * Writes JSON Lines (.jsonl) output to three separate files:
 *   out-accounts.jsonl, array-records.jsonl, vb-records.jsonl
 */
public class JsonOutputWriter implements OutputWriter {

    private final ObjectMapper mapper = new ObjectMapper();
    private final BufferedWriter outAccountWriter;
    private final BufferedWriter arrayRecordWriter;
    private final BufferedWriter vbRecordWriter;

    public JsonOutputWriter(Path outputDir) throws IOException {
        outAccountWriter = Files.newBufferedWriter(outputDir.resolve("out-accounts.jsonl"));
        arrayRecordWriter = Files.newBufferedWriter(outputDir.resolve("array-records.jsonl"));
        vbRecordWriter = Files.newBufferedWriter(outputDir.resolve("vb-records.jsonl"));
    }

    @Override
    public void writeOutRecord(OutAccountRecord record) throws IOException {
        outAccountWriter.write(mapper.writeValueAsString(record));
        outAccountWriter.newLine();
    }

    @Override
    public void writeArrayRecord(ArrayRecord record) throws IOException {
        arrayRecordWriter.write(mapper.writeValueAsString(record));
        arrayRecordWriter.newLine();
    }

    @Override
    public void writeVbRecord1(VbRecord1 record) throws IOException {
        vbRecordWriter.write(mapper.writeValueAsString(record));
        vbRecordWriter.newLine();
    }

    @Override
    public void writeVbRecord2(VbRecord2 record) throws IOException {
        vbRecordWriter.write(mapper.writeValueAsString(record));
        vbRecordWriter.newLine();
    }

    @Override
    public void close() throws IOException {
        outAccountWriter.close();
        arrayRecordWriter.close();
        vbRecordWriter.close();
    }
}
