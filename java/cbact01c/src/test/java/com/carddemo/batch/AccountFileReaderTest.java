package com.carddemo.batch;

import com.carddemo.batch.io.AccountFileReader;
import com.carddemo.batch.model.AccountRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AccountFileReaderTest {

    /**
     * Account #1 from acctdata.txt.
     * PIC S9(10)V99 values: 00000001940{ → 194.00, 00000020200{ → 2020.00, etc.
     */
    private static final String SAMPLE_LINE_1 =
            "00000000001Y00000001940{00000020200{00000010200{" +
            "2014-11-202025-05-202025-05-20" +
            "00000000000{00000000000{" +
            "A000000000" +
            "A000000000" +
            " ".repeat(178);

    @Test
    void parseSingleRecord() {
        AccountRecord rec = AccountFileReader.parseLine(SAMPLE_LINE_1);

        assertEquals(1L, rec.acctId());
        assertEquals('Y', rec.activeStatus());
        assertEquals(0, new BigDecimal("194.00").compareTo(rec.currBal()));
        assertEquals(0, new BigDecimal("2020.00").compareTo(rec.creditLimit()));
        assertEquals(0, new BigDecimal("1020.00").compareTo(rec.cashCreditLimit()));
        assertEquals("2014-11-20", rec.openDate());
        assertEquals("2025-05-20", rec.expirationDate());
        assertEquals("2025-05-20", rec.reissueDate());
        assertEquals(0, BigDecimal.ZERO.compareTo(rec.currCycCredit()));
        assertEquals(0, BigDecimal.ZERO.compareTo(rec.currCycDebit()));
        assertEquals("A000000000", rec.groupId());
    }

    @Test
    void readMultipleRecords(@TempDir Path tempDir) throws IOException {
        // Account #2: bal=158.00, credit=6130.00
        String line2 =
                "00000000002Y00000001580{00000061300{00000054480{" +
                "2013-06-192024-08-112024-08-11" +
                "00000000000{00000000000{" +
                "A000000000A000000000" +
                " ".repeat(178);

        Path file = tempDir.resolve("acctdata.txt");
        Files.writeString(file, SAMPLE_LINE_1 + "\n" + line2 + "\n");

        try (var reader = new AccountFileReader(file)) {
            List<AccountRecord> records = reader.stream().toList();
            assertEquals(2, records.size());
            assertEquals(1L, records.get(0).acctId());
            assertEquals(2L, records.get(1).acctId());
            assertEquals(0, new BigDecimal("158.00").compareTo(records.get(1).currBal()));
        }
    }

    @Test
    void emptyFileReturnsNoRecords(@TempDir Path tempDir) throws IOException {
        Path file = tempDir.resolve("empty.txt");
        Files.writeString(file, "");

        try (var reader = new AccountFileReader(file)) {
            assertNull(reader.readNext());
        }
    }
}
