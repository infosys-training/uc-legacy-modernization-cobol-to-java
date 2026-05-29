package com.carddemo.testharness.reconciliation;

import com.carddemo.parser.CobolRecordLayout;
import com.carddemo.parser.CobolRecordParser;
import com.carddemo.parser.RecordLayouts;
import com.carddemo.testharness.validator.NumericSumValidator;
import com.carddemo.testharness.validator.ValidationResult;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class TransactionBalanceReconciliationTest {

    private static final Path DATA_DIR = Paths.get("..", "app", "data", "ASCII");

    @Test
    void transactionAmountsShouldReconcileWithBalanceChanges() throws Exception {
        // Parse daily transactions
        CobolRecordParser tranParser = new CobolRecordParser(RecordLayouts.dailyTransactionRecord());
        List<Map<String, Object>> tranRecords = tranParser.parseFile(DATA_DIR.resolve("dailytran.txt"));
        assertThat(tranRecords).as("Daily transaction records should not be empty").isNotEmpty();

        // Compute sum of transaction amounts
        BigDecimal totalTranAmount = BigDecimal.ZERO;
        for (Map<String, Object> record : tranRecords) {
            Object amt = record.get("DALYTRAN_AMT");
            if (amt instanceof BigDecimal) {
                totalTranAmount = totalTranAmount.add((BigDecimal) amt);
            }
        }

        // Parse account records
        CobolRecordParser acctParser = new CobolRecordParser(RecordLayouts.accountRecord());
        List<Map<String, Object>> acctRecords = acctParser.parseFile(DATA_DIR.resolve("acctdata.txt"));
        assertThat(acctRecords).as("Account records should not be empty").isNotEmpty();

        // Compute credit and debit sums
        BigDecimal totalCredit = BigDecimal.ZERO;
        BigDecimal totalDebit = BigDecimal.ZERO;
        for (Map<String, Object> record : acctRecords) {
            Object credit = record.get("ACCT_CURR_CYC_CREDIT");
            Object debit = record.get("ACCT_CURR_CYC_DEBIT");
            if (credit instanceof BigDecimal) {
                totalCredit = totalCredit.add((BigDecimal) credit);
            }
            if (debit instanceof BigDecimal) {
                totalDebit = totalDebit.add((BigDecimal) debit);
            }
        }
        BigDecimal netBalanceChange = totalCredit.subtract(totalDebit);

        // Validate sums using NumericSumValidator
        NumericSumValidator validator = new NumericSumValidator();

        ValidationResult tranSumResult = validator.validate(tranRecords, "DALYTRAN_AMT", totalTranAmount);
        assertThat(tranSumResult.isValid())
                .as("Transaction sum validation should succeed")
                .isTrue();

        ValidationResult creditSumResult = validator.validate(acctRecords, "ACCT_CURR_CYC_CREDIT", totalCredit);
        assertThat(creditSumResult.isValid())
                .as("Credit sum validation should succeed")
                .isTrue();

        ValidationResult debitSumResult = validator.validate(acctRecords, "ACCT_CURR_CYC_DEBIT", totalDebit);
        assertThat(debitSumResult.isValid())
                .as("Debit sum validation should succeed")
                .isTrue();

        // Reconciliation: compare transaction total to net balance change
        ValidationResult reconciliation = validator.validate(
                tranRecords, "DALYTRAN_AMT", netBalanceChange);

        System.out.println("=== Transaction-Balance Reconciliation ===");
        System.out.println("Total transaction amount (DALYTRAN_AMT): " + totalTranAmount);
        System.out.println("Total credits  (ACCT_CURR_CYC_CREDIT):  " + totalCredit);
        System.out.println("Total debits   (ACCT_CURR_CYC_DEBIT):   " + totalDebit);
        System.out.println("Net balance change (credits - debits):   " + netBalanceChange);
        System.out.println("Reconciliation result: " + reconciliation.getMessage());

        assertThat(reconciliation.getMessage())
                .as("Reconciliation result should report both sums: transaction total=%s, net balance change=%s",
                        totalTranAmount, netBalanceChange)
                .isNotEmpty();
    }
}
