package com.cardemo.batch.io;

import com.cardemo.batch.model.AccountRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountReaderTest {

    private static final String SAMPLE_LINE_1 =
            "00000000001Y00000001940{00000020200{00000010200{"
          + "2014-11-202025-05-202025-05-20"
          + "00000000000{00000000000{A000000000";

    private static final String SAMPLE_LINE_2 =
            "00000000002Y00000001580{00000061300{00000054480{"
          + "2013-06-192024-08-112024-08-11"
          + "00000000000{00000000000{A000000000";

    @Test
    void parseLine_firstRecord() {
        AccountRecord r = AccountReader.parseLine(SAMPLE_LINE_1);

        assertEquals("00000000001", r.acctId());
        assertEquals("Y", r.activeStatus());
        assertEquals(0, new BigDecimal("194.00").compareTo(r.currBal()));
        assertEquals(0, new BigDecimal("2020.00").compareTo(r.creditLimit()));
        assertEquals(0, new BigDecimal("1020.00").compareTo(r.cashCreditLimit()));
        assertEquals("2014-11-20", r.openDate());
        assertEquals("2025-05-20", r.expirationDate());
        assertEquals("2025-05-20", r.reissueDate());
        assertEquals(0, BigDecimal.ZERO.compareTo(r.currCycCredit()));
        assertEquals(0, BigDecimal.ZERO.compareTo(r.currCycDebit()));
        assertEquals("A000000000", r.addrZip());
    }

    @Test
    void parseLine_secondRecord() {
        AccountRecord r = AccountReader.parseLine(SAMPLE_LINE_2);

        assertEquals("00000000002", r.acctId());
        assertEquals(0, new BigDecimal("158.00").compareTo(r.currBal()));
        assertEquals(0, new BigDecimal("6130.00").compareTo(r.creditLimit()));
        assertEquals(0, new BigDecimal("5448.00").compareTo(r.cashCreditLimit()));
        assertEquals("2013-06-19", r.openDate());
        assertEquals("2024-08-11", r.expirationDate());
        assertEquals("2024-08-11", r.reissueDate());
    }

    @Test
    void readAll_fromFile(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("acctdata.txt");
        Files.writeString(file, SAMPLE_LINE_1 + "\n" + SAMPLE_LINE_2 + "\n");

        try (var reader = new AccountReader(file)) {
            List<AccountRecord> records = reader.readAll();
            assertEquals(2, records.size());
            assertEquals("00000000001", records.get(0).acctId());
            assertEquals("00000000002", records.get(1).acctId());
        }
    }

    @Test
    void readAll_fromSampleResource() throws IOException {
        Path sample = Path.of("src/test/resources/testdata/acctdata_sample.txt");
        if (!Files.exists(sample)) {
            return; // skip if running from different working dir
        }
        try (var reader = new AccountReader(sample)) {
            List<AccountRecord> records = reader.readAll();
            assertEquals(3, records.size());
        }
    }

    @Test
    void iterator_supportsForEach(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("acctdata.txt");
        Files.writeString(file, SAMPLE_LINE_1 + "\n");

        int count = 0;
        try (var reader = new AccountReader(file)) {
            for (AccountRecord r : reader) {
                assertNotNull(r);
                count++;
            }
        }
        assertEquals(1, count);
    }
}
