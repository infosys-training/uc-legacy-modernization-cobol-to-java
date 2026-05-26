package com.carddemo.io;

import com.carddemo.model.ArrayRecord;
import com.carddemo.model.OutputAccountRecord;
import com.carddemo.model.VariableRecord1;
import com.carddemo.model.VariableRecord2;
import com.carddemo.service.AccountProcessor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes the three output files produced by CBACT01C:
 *   - OUTFILE   (sequential, OutputAccountRecord)
 *   - ARRYFILE  (sequential, ArrayRecord)
 *   - VBRCFILE  (variable-length, VariableRecord1 + VariableRecord2 alternating)
 */
public class OutputFileWriter {

    public void writeAll(AccountProcessor.ProcessingResult result,
                         Path outFilePath, Path arryFilePath, Path vbrcFilePath) throws IOException {

        writeOutputRecords(result, outFilePath);
        writeArrayRecords(result, arryFilePath);
        writeVariableRecords(result, vbrcFilePath);
    }

    private void writeOutputRecords(AccountProcessor.ProcessingResult result, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (OutputAccountRecord rec : result.outputRecords()) {
                writer.write(rec.toString());
                writer.newLine();
            }
        }
    }

    private void writeArrayRecords(AccountProcessor.ProcessingResult result, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (ArrayRecord rec : result.arrayRecords()) {
                writer.write(rec.toString());
                writer.newLine();
            }
        }
    }

    private void writeVariableRecords(AccountProcessor.ProcessingResult result, Path path) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            for (int i = 0; i < result.variableRecords1().size(); i++) {
                VariableRecord1 vb1 = result.variableRecords1().get(i);
                VariableRecord2 vb2 = result.variableRecords2().get(i);
                writer.write(vb1.toString());
                writer.newLine();
                writer.write(vb2.toString());
                writer.newLine();
            }
        }
    }
}
