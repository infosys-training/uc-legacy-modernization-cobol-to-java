package com.carddemo.batch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AccountFileProcessorJsonTest {

    private final AccountFileProcessor processor = new AccountFileProcessor();
    private final ObjectMapper mapper = new ObjectMapper();

    @TempDir
    Path tempDir;

    private Path inputFile() throws Exception {
        return Path.of(getClass().getResource("/acctdata.txt").toURI());
    }

    @Test
    void jsonOutput_threeFilesCreated() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.JSON);

        assertThat(tempDir.resolve("out-accounts.jsonl")).exists();
        assertThat(tempDir.resolve("array-records.jsonl")).exists();
        assertThat(tempDir.resolve("vb-records.jsonl")).exists();
    }

    @Test
    void outAccounts_has50Lines() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.JSON);

        List<String> lines = Files.readAllLines(tempDir.resolve("out-accounts.jsonl"));
        assertThat(lines).hasSize(50);
    }

    @Test
    void arrayRecords_has50Lines() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.JSON);

        List<String> lines = Files.readAllLines(tempDir.resolve("array-records.jsonl"));
        assertThat(lines).hasSize(50);
    }

    @Test
    void vbRecords_has100Lines() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.JSON);

        List<String> lines = Files.readAllLines(tempDir.resolve("vb-records.jsonl"));
        assertThat(lines).hasSize(100);
    }

    @Test
    void firstOutAccount_debitDefault2525() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.JSON);

        String firstLine = Files.readAllLines(tempDir.resolve("out-accounts.jsonl")).get(0);
        JsonNode node = mapper.readTree(firstLine);
        BigDecimal debit = node.get("currentCycleDebit").decimalValue();
        assertThat(debit).isEqualByComparingTo(new BigDecimal("2525.00"));
    }

    @Test
    void firstOutAccount_reissueDateFormatted() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.JSON);

        String firstLine = Files.readAllLines(tempDir.resolve("out-accounts.jsonl")).get(0);
        JsonNode node = mapper.readTree(firstLine);
        assertThat(node.get("reissueDate").asText()).isEqualTo("20250520");
    }

    @Test
    void firstVbRecord_isVb1WithAcctId() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.JSON);

        String firstLine = Files.readAllLines(tempDir.resolve("vb-records.jsonl")).get(0);
        JsonNode node = mapper.readTree(firstLine);
        assertThat(node.get("acctId").asText()).isEqualTo("00000000001");
    }
}
