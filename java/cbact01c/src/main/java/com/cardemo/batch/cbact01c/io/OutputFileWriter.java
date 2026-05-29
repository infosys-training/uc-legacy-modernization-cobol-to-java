package com.cardemo.batch.cbact01c.io;

import com.cardemo.batch.cbact01c.model.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Writes the three output files corresponding to OUTFILE, ARRYFILE, and VBRCFILE
 * in CBACT01C. Uses pipe-delimited CSV format for modern consumption while
 * preserving all field semantics from the COBOL program.
 */
public final class OutputFileWriter {

    private OutputFileWriter() {}

    /**
     * Writes the flat output records (OUTFILE equivalent).
     * Header row describes each field.
     */
    public static void writeOutputFile(Path path, List<OutputAccountRecord> records)
            throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("ACCT_ID|ACTIVE_STATUS|CURR_BAL|CREDIT_LIMIT|CASH_CREDIT_LIMIT"
                    + "|OPEN_DATE|EXPIRATION_DATE|REISSUE_DATE|CURR_CYC_CREDIT"
                    + "|CURR_CYC_DEBIT|GROUP_ID");
            writer.newLine();
            for (OutputAccountRecord rec : records) {
                writer.write(String.join("|",
                        rec.acctId(),
                        rec.activeStatus(),
                        rec.currBal().toPlainString(),
                        rec.creditLimit().toPlainString(),
                        rec.cashCreditLimit().toPlainString(),
                        rec.openDate(),
                        rec.expirationDate(),
                        rec.reissueDate(),
                        rec.currCycCredit().toPlainString(),
                        rec.currCycDebit().toPlainString(),
                        rec.groupId()
                ));
                writer.newLine();
            }
        }
    }

    /**
     * Writes the array records (ARRYFILE equivalent).
     */
    public static void writeArrayFile(Path path, List<ArrayAccountRecord> records)
            throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("ACCT_ID"
                    + "|BAL_1|DEBIT_1|BAL_2|DEBIT_2|BAL_3|DEBIT_3"
                    + "|BAL_4|DEBIT_4|BAL_5|DEBIT_5");
            writer.newLine();
            for (ArrayAccountRecord rec : records) {
                StringBuilder sb = new StringBuilder(rec.acctId());
                for (BalanceEntry entry : rec.balanceEntries()) {
                    sb.append('|').append(entry.currBal().toPlainString());
                    sb.append('|').append(entry.currCycDebit().toPlainString());
                }
                writer.write(sb.toString());
                writer.newLine();
            }
        }
    }

    /**
     * Writes the variable-length records (VBRCFILE equivalent).
     * Two lines per account: a short record (S) and a long record (L).
     */
    public static void writeVariableLengthFile(
            Path path,
            List<VariableRecordShort> shortRecords,
            List<VariableRecordLong> longRecords) throws IOException {
        if (shortRecords.size() != longRecords.size()) {
            throw new IllegalArgumentException(
                    "Short and long record lists must be the same size");
        }
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write("TYPE|ACCT_ID|FIELDS...");
            writer.newLine();
            for (int i = 0; i < shortRecords.size(); i++) {
                VariableRecordShort s = shortRecords.get(i);
                writer.write(String.join("|", "S", s.acctId(), s.activeStatus()));
                writer.newLine();

                VariableRecordLong l = longRecords.get(i);
                writer.write(String.join("|", "L", l.acctId(),
                        l.currBal().toPlainString(),
                        l.creditLimit().toPlainString(),
                        l.reissueYear()));
                writer.newLine();
            }
        }
    }
}
