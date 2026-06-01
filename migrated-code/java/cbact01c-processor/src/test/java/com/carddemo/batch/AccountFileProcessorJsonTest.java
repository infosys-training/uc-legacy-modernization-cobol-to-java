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
    void nonZeroDebit_preservedInOutput() throws Exception {
        // Build a 300-char record with a non-zero debit (500.00) at bytes 90-101.
        // Field layout: acctId(11) activeStatus(1) balance(12) creditLimit(12)
        //   cashCreditLimit(12) openDate(10) expDate(10) reissueDate(10)
        //   cycleCredit(12) cycleDebit(12) addrZip(10) groupId(10) filler(178)
        String record =
                "00000000099"                   // acctId
                + "Y"                           // activeStatus
                + "00000001000{"                 // currentBalance = 100.00
                + "00000020000{"                 // creditLimit = 2000.00
                + "00000010000{"                 // cashCreditLimit = 1000.00
                + "2024-01-01"                   // openDate
                + "2026-01-01"                   // expirationDate
                + "2025-06-01"                   // reissueDate
                + "00000000000{"                 // currentCycleCredit = 0.00
                + "00000005000{"                 // currentCycleDebit = 500.00 (non-zero)
                + "1234567890"                   // addressZip
                + "GRPTEST   ";                  // groupId
        // Pad to 300 chars
        record = String.format("%-300s", record);

        Path input = tempDir.resolve("nonzero-debit.txt");
        Files.writeString(input, record + System.lineSeparator());

        Path outDir = tempDir.resolve("out");
        processor.process(input, outDir, OutputFormat.JSON);

        String line = Files.readAllLines(outDir.resolve("out-accounts.jsonl")).get(0);
        JsonNode node = mapper.readTree(line);
        BigDecimal debit = node.get("currentCycleDebit").decimalValue();
        // Non-zero debit should be preserved, NOT replaced with 2525.00
        assertThat(debit).isEqualByComparingTo(new BigDecimal("500.00"));
    }

    @Test
    void firstVbRecord_isVb1WithAcctId() throws Exception {
        processor.process(inputFile(), tempDir, OutputFormat.JSON);

        String firstLine = Files.readAllLines(tempDir.resolve("vb-records.jsonl")).get(0);
        JsonNode node = mapper.readTree(firstLine);
        assertThat(node.get("acctId").asText()).isEqualTo("00000000001");
    }
}
