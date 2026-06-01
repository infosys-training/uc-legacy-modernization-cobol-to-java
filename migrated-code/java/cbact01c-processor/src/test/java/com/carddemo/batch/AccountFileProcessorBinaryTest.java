package com.carddemo.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

class AccountFileProcessorBinaryTest {

    private final AccountFileProcessor processor = new AccountFileProcessor();

    @TempDir
    Path tempDir;

    private Path inputFile() throws Exception {
        return Path.of(getClass().getResource("/acctdata.txt").toURI());
    }

    @Test
    void binaryOutput_succeeds() throws Exception {
        assertThatNoException()
                .isThrownBy(() -> processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY));
    }

    @Test
    void outFile_expectedSize5350() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY);
        long size = Files.size(tempDir.resolve("out-accounts.bin"));
        assertThat(size).isEqualTo(50L * 107);
    }

    @Test
    void arryFile_expectedSize5500() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY);
        long size = Files.size(tempDir.resolve("array-records.bin"));
        assertThat(size).isEqualTo(50L * 110);
    }

    @Test
    void vbrcFile_nonEmpty() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY);
        long size = Files.size(tempDir.resolve("vb-records.bin"));
        assertThat(size).isGreaterThan(0);
    }
}
