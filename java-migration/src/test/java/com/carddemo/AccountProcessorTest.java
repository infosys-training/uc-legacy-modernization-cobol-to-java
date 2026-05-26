package com.carddemo;

import com.carddemo.io.AccountFileReader;
import com.carddemo.model.AccountRecord;
import com.carddemo.model.ArrayRecord;
import com.carddemo.model.OutputAccountRecord;
import com.carddemo.model.VariableRecord1;
import com.carddemo.model.VariableRecord2;
import com.carddemo.service.AccountProcessor;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Parity tests for the AccountProcessor business logic.
 *
 * These tests verify that the Java code produces identical results to the
 * COBOL program CBACT01C for the same input data.
 */
class AccountProcessorTest {

    @Test
    void outputRecordMatchesCobolLogic() throws Exception {
        Path sampleFile = Path.of(getClass().getClassLoader()
                .getResource("acctdata-sample.txt").toURI());

        AccountFileReader reader = new AccountFileReader();
        List<AccountRecord> accounts = reader.readAll(sampleFile);

        AccountProcessor processor = new AccountProcessor();
        AccountProcessor.ProcessingResult result = processor.process(accounts);

        assertThat(result.outputRecords()).hasSize(5);

        OutputAccountRecord first = result.outputRecords().get(0);

        assertThat(first.getAcctId()).isEqualTo(1L);
        assertThat(first.getActiveStatus()).isEqualTo('Y');
        assertThat(first.getCurrentBalance()).isEqualByComparingTo(new BigDecimal("194.00"));
        assertThat(first.getCreditLimit()).isEqualByComparingTo(new BigDecimal("2020.00"));
        assertThat(first.getCashCreditLimit()).isEqualByComparingTo(new BigDecimal("1020.00"));
        assertThat(first.getOpenDate()).isEqualTo("2014-11-20");
        assertThat(first.getExpirationDate()).isEqualTo("2025-05-20");

        // Reissue date reformatted: YYYY-MM-DD → YYYYMMDD via COBDATFT
        assertThat(first.getReissueDate()).isEqualTo("20250520");

        // Business rule: when cycle debit is zero, set to 2525.00
        assertThat(first.getCurrentCycleDebit()).isEqualByComparingTo(new BigDecimal("2525.00"));
    }

    @Test
    void arrayRecordHasCorrectHardcodedValues() throws Exception {
        Path sampleFile = Path.of(getClass().getClassLoader()
                .getResource("acctdata-sample.txt").toURI());

        AccountFileReader reader = new AccountFileReader();
        List<AccountRecord> accounts = reader.readAll(sampleFile);

        AccountProcessor processor = new AccountProcessor();
        AccountProcessor.ProcessingResult result = processor.process(accounts);

        ArrayRecord first = result.arrayRecords().get(0);
        assertThat(first.getAcctId()).isEqualTo(1L);

        // COBOL hardcoded values in 1400-POPUL-ARRAY-RECORD
        // Balance at index 0 = ACCT-CURR-BAL = 194.00
        assertThat(first.getBalance(0)).isEqualByComparingTo(new BigDecimal("194.00"));
        assertThat(first.getCycleDebit(0)).isEqualByComparingTo(new BigDecimal("1005.00"));

        assertThat(first.getBalance(1)).isEqualByComparingTo(new BigDecimal("194.00"));
        assertThat(first.getCycleDebit(1)).isEqualByComparingTo(new BigDecimal("1525.00"));

        assertThat(first.getBalance(2)).isEqualByComparingTo(new BigDecimal("-1025.00"));
        assertThat(first.getCycleDebit(2)).isEqualByComparingTo(new BigDecimal("-2500.00"));

        // Indices 3-4 remain zero (INITIALIZE in COBOL)
        assertThat(first.getBalance(3)).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(first.getCycleDebit(3)).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(first.getBalance(4)).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(first.getCycleDebit(4)).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void variableRecordsPopulatedCorrectly() throws Exception {
        Path sampleFile = Path.of(getClass().getClassLoader()
                .getResource("acctdata-sample.txt").toURI());

        AccountFileReader reader = new AccountFileReader();
        List<AccountRecord> accounts = reader.readAll(sampleFile);

        AccountProcessor processor = new AccountProcessor();
        AccountProcessor.ProcessingResult result = processor.process(accounts);

        VariableRecord1 vb1 = result.variableRecords1().get(0);
        assertThat(vb1.acctId()).isEqualTo(1L);
        assertThat(vb1.activeStatus()).isEqualTo('Y');

        VariableRecord2 vb2 = result.variableRecords2().get(0);
        assertThat(vb2.acctId()).isEqualTo(1L);
        assertThat(vb2.currentBalance()).isEqualByComparingTo(new BigDecimal("194.00"));
        assertThat(vb2.creditLimit()).isEqualByComparingTo(new BigDecimal("2020.00"));
        assertThat(vb2.reissueYear()).isEqualTo("2025");
    }

    @Test
    void displayOutputContainsAllFields() throws Exception {
        Path sampleFile = Path.of(getClass().getClassLoader()
                .getResource("acctdata-sample.txt").toURI());

        AccountFileReader reader = new AccountFileReader();
        List<AccountRecord> accounts = reader.readAll(sampleFile);

        AccountProcessor processor = new AccountProcessor();
        AccountProcessor.ProcessingResult result = processor.process(accounts);

        // 12 display lines per record (11 fields + separator), 5 records
        assertThat(result.displayOutput()).hasSize(60);
        assertThat(result.displayOutput().get(0)).startsWith("ACCT-ID");
        assertThat(result.displayOutput().get(11)).contains("----");
    }

    @Test
    void allFiveRecordsProcessed() throws Exception {
        Path sampleFile = Path.of(getClass().getClassLoader()
                .getResource("acctdata-sample.txt").toURI());

        AccountFileReader reader = new AccountFileReader();
        List<AccountRecord> accounts = reader.readAll(sampleFile);

        AccountProcessor processor = new AccountProcessor();
        AccountProcessor.ProcessingResult result = processor.process(accounts);

        assertThat(result.outputRecords()).hasSize(5);
        assertThat(result.arrayRecords()).hasSize(5);
        assertThat(result.variableRecords1()).hasSize(5);
        assertThat(result.variableRecords2()).hasSize(5);

        long[] expectedIds = {1, 2, 3, 4, 5};
        for (int i = 0; i < 5; i++) {
            assertThat(result.outputRecords().get(i).getAcctId()).isEqualTo(expectedIds[i]);
        }
    }
}
