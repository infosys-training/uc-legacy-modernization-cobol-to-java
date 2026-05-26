package com.carddemo;

import com.carddemo.io.AccountFileReader;
import com.carddemo.model.AccountRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parity test: verifies that the Java file reader produces identical field values
 * to what the COBOL program would read from the VSAM account file.
 *
 * Known values from acctdata.txt line 1:
 *   00000000001Y00000001940{00000020200{00000010200{2014-11-20...
 *
 * PIC S9(10)V99 = 12 chars, implied decimal after 10th digit.
 * "00000001940{" → digits "000000019400" → V99 → 194.00
 * The trailing '{' = overpunch for +0.
 */
class AccountFileReaderTest {

    @Test
    void readsFirstRecordCorrectly() throws Exception {
        Path sampleFile = Path.of(getClass().getClassLoader()
                .getResource("acctdata-sample.txt").toURI());

        AccountFileReader reader = new AccountFileReader();
        List<AccountRecord> records = reader.readAll(sampleFile);

        assertThat(records).hasSizeGreaterThanOrEqualTo(1);

        AccountRecord first = records.get(0);
        assertThat(first.getAcctId()).isEqualTo(1L);
        assertThat(first.getActiveStatus()).isEqualTo('Y');
        // 00000001940{ → 000000019400 → S9(10)V99 → 194.00
        assertThat(first.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("194.00"));
        // 00000020200{ → 000000202000 → 2020.00
        assertThat(first.getCreditLimit()).isEqualByComparingTo(new BigDecimal("2020.00"));
        // 00000010200{ → 000000102000 → 1020.00
        assertThat(first.getCashCreditLimit()).isEqualByComparingTo(new BigDecimal("1020.00"));
        assertThat(first.getOpenDate()).isEqualTo("2014-11-20");
        assertThat(first.getExpirationDate()).isEqualTo("2025-05-20");
        assertThat(first.getReissueDate()).isEqualTo("2025-05-20");
        assertThat(first.getCurrentCycleCredit()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(first.getCurrentCycleDebit()).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void readsMultipleRecords() throws Exception {
        Path sampleFile = Path.of(getClass().getClassLoader()
                .getResource("acctdata-sample.txt").toURI());

        AccountFileReader reader = new AccountFileReader();
        List<AccountRecord> records = reader.readAll(sampleFile);

        assertThat(records).hasSize(5);

        // Second record: 00000000002Y00000001580{00000061300{...
        AccountRecord second = records.get(1);
        assertThat(second.getAcctId()).isEqualTo(2L);
        assertThat(second.getActiveStatus()).isEqualTo('Y');
        // 00000001580{ → 000000015800 → 158.00
        assertThat(second.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("158.00"));
        // 00000061300{ → 000000613000 → 6130.00
        assertThat(second.getCreditLimit()).isEqualByComparingTo(new BigDecimal("6130.00"));
    }

    @Test
    void parseSignedDecimalWithOverpunch() {
        // '{' = +0: "00000001940{" → digits "000000019400" → V99 → 194.00
        assertThat(AccountFileReader.parseSignedDecimal("00000001940{", 2))
                .isEqualByComparingTo(new BigDecimal("194.00"));

        // 'A' = +1: "00000000194A" → digits "000000001941" → V99 → 19.41
        assertThat(AccountFileReader.parseSignedDecimal("00000000194A", 2))
                .isEqualByComparingTo(new BigDecimal("19.41"));

        // 'J' = -1: "00000001940J" → digits "000000019401" → V99 → -194.01
        assertThat(AccountFileReader.parseSignedDecimal("00000001940J", 2))
                .isEqualByComparingTo(new BigDecimal("-194.01"));
    }
}
