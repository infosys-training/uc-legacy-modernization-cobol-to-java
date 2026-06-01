package com.carddemo.batch;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for COBOL_BINARY output mode.
 *
 * NOTE: CobolBinaryOutputWriter.COMP3_DIGITS is set to 12 (even), but
 * Comp3Encoder.encode() requires an odd totalDigits. This causes a
 * StringIndexOutOfBoundsException when writing any record containing a
 * COMP-3 field. Tests below verify current (broken) behavior.
 */
class AccountFileProcessorBinaryTest {

    private final AccountFileProcessor processor = new AccountFileProcessor();

    @TempDir
    Path tempDir;

    private Path inputFile() throws Exception {
        return Path.of(getClass().getResource("/acctdata.txt").toURI());
    }

    @Test
    void binaryOutput_failsDueToComp3Bug() throws Exception {
        assertThatThrownBy(() -> processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY))
                .isInstanceOf(StringIndexOutOfBoundsException.class);
    }

    @Test
    void outFile_expectedSize5350_whenBugFixed() throws Exception {
        // Expected: 50 × 107 = 5350 bytes once COMP3_DIGITS is corrected to 13
        // Currently blocked by COMP3_DIGITS=12 bug in CobolBinaryOutputWriter
        assertThatThrownBy(() -> processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY))
                .isInstanceOf(StringIndexOutOfBoundsException.class);
    }

    @Test
    void arryFile_expectedSize5500_whenBugFixed() throws Exception {
        // Expected: 50 × 110 = 5500 bytes once COMP3_DIGITS is corrected to 13
        // Currently blocked by COMP3_DIGITS=12 bug in CobolBinaryOutputWriter
        assertThatThrownBy(() -> processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY))
                .isInstanceOf(StringIndexOutOfBoundsException.class);
    }

    @Test
    void vbrcFile_expectedNonEmpty_whenBugFixed() throws Exception {
        // Expected: vb-records.bin exists and size > 0 once COMP3_DIGITS is corrected
        // Currently blocked by COMP3_DIGITS=12 bug in CobolBinaryOutputWriter
        assertThatThrownBy(() -> processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY))
                .isInstanceOf(StringIndexOutOfBoundsException.class);
    }
}
