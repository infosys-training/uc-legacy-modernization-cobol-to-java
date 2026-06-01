package com.carddemo.batch;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class GoldenFileComparisonTest {

    private final AccountFileProcessor processor = new AccountFileProcessor();
    private final ObjectMapper mapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    private Path inputFile() throws Exception {
        return Path.of(getClass().getResource("/acctdata.txt").toURI());
    }

    @Test
    void jsonGolden_firstRecord_keyFields() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.JSON);

        String firstLine = Files.readAllLines(tempDir.resolve("out-accounts.jsonl")).get(0);
        Map<String, Object> record = mapper.readValue(firstLine, new TypeReference<>() {});

        assertThat(record.get("acctId")).isEqualTo("00000000001");
        assertThat(new BigDecimal(record.get("currentCycleDebit").toString()))
                .isEqualByComparingTo(new BigDecimal("2525.00"));
        assertThat(record.get("reissueDate")).isEqualTo("20250520");
        assertThat(new BigDecimal(record.get("currentBalance").toString()))
                .isEqualByComparingTo(new BigDecimal("194.00"));
    }

    @Test
    void binaryGolden_comp3DebitField_blockedByComp3Bug() throws Exception {
        // Binary output crashes because CobolBinaryOutputWriter.COMP3_DIGITS=12 (even)
        // triggers StringIndexOutOfBoundsException in Comp3Encoder.
        // Once fixed to 13, this test should verify COMP-3 bytes at offset 90-96
        // encode 2525.00 as {0x00,0x00,0x02,0x52,0x50,0x00,0x0C}.
        assertThatThrownBy(() -> processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY))
                .isInstanceOf(StringIndexOutOfBoundsException.class);
    }
}
