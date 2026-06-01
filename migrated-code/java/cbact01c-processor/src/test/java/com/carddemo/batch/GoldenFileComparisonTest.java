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
    void binaryGolden_comp3DebitField() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.COBOL_BINARY);

        byte[] outData = Files.readAllBytes(tempDir.resolve("out-accounts.bin"));
        // COMP-3 debit field starts at offset 90 in each 107-byte record
        // First record debit=2525.00 (default when input is 0)
        // 2525.00 → 13 digits → "0000000252500" → packed: 00 00 00 02 52 50 0C
        byte[] comp3Debit = new byte[7];
        System.arraycopy(outData, 90, comp3Debit, 0, 7);
        assertThat(comp3Debit).isEqualTo(new byte[]{
                0x00, 0x00, 0x00, 0x02, 0x52, 0x50, 0x0C
        });
    }
}
