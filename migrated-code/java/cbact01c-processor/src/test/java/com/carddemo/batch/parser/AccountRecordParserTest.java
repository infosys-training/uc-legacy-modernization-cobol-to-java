package com.carddemo.batch.parser;

import com.carddemo.batch.model.AccountRecord;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class AccountRecordParserTest {

    private final AccountRecordParser parser = new AccountRecordParser();

    private List<String> readLines() throws Exception {
        return Files.readAllLines(Path.of(getClass().getResource("/acctdata.txt").toURI()));
    }

    @Test
    void parseRecord1_allFields() throws Exception {
        List<String> lines = readLines();
        AccountRecord rec = parser.parse(lines.get(0));

        assertThat(rec.acctId()).isEqualTo("00000000001");
        assertThat(rec.activeStatus()).isEqualTo("Y");
        assertThat(rec.currentBalance()).isEqualByComparingTo(new BigDecimal("194.00"));
        assertThat(rec.creditLimit()).isEqualByComparingTo(new BigDecimal("2020.00"));
        assertThat(rec.cashCreditLimit()).isEqualByComparingTo(new BigDecimal("1020.00"));
        assertThat(rec.openDate()).isEqualTo("2014-11-20");
        assertThat(rec.expirationDate()).isEqualTo("2025-05-20");
        assertThat(rec.reissueDate()).isEqualTo("2025-05-20");
        assertThat(rec.currentCycleCredit()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(rec.currentCycleDebit()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(rec.addressZip()).isEqualTo("A000000000");
        assertThat(rec.groupId()).isEmpty();
    }

    @Test
    void parseRecord2_keyFields() throws Exception {
        List<String> lines = readLines();
        AccountRecord rec = parser.parse(lines.get(1));

        assertThat(rec.acctId()).isEqualTo("00000000002");
        assertThat(rec.activeStatus()).isEqualTo("Y");
        assertThat(rec.currentBalance()).isEqualByComparingTo(new BigDecimal("158.00"));
        assertThat(rec.creditLimit()).isEqualByComparingTo(new BigDecimal("6130.00"));
        assertThat(rec.cashCreditLimit()).isEqualByComparingTo(new BigDecimal("5448.00"));
        assertThat(rec.openDate()).isEqualTo("2013-06-19");
        assertThat(rec.expirationDate()).isEqualTo("2024-08-11");
        assertThat(rec.reissueDate()).isEqualTo("2024-08-11");
    }

    @Test
    void parseRecord3_keyFields() throws Exception {
        List<String> lines = readLines();
        AccountRecord rec = parser.parse(lines.get(2));

        assertThat(rec.acctId()).isEqualTo("00000000003");
        assertThat(rec.activeStatus()).isEqualTo("Y");
        assertThat(rec.currentBalance()).isEqualByComparingTo(new BigDecimal("147.00"));
        assertThat(rec.creditLimit()).isEqualByComparingTo(new BigDecimal("4909.00"));
        assertThat(rec.cashCreditLimit()).isEqualByComparingTo(new BigDecimal("538.00"));
    }

    @Test
    void parseAll50Records() throws Exception {
        List<String> lines = readLines();
        assertThat(lines).hasSize(50);

        for (String line : lines) {
            AccountRecord rec = parser.parse(line);
            assertThat(rec).isNotNull();
            assertThat(rec.acctId()).isNotBlank();
        }
    }
}
