package com.carddemo.batch.cbact01c.io;

import com.carddemo.batch.cbact01c.model.ArrayAccountRecord;
import com.carddemo.batch.cbact01c.model.OutputAccountRecord;
import com.carddemo.batch.cbact01c.model.VbrRecord;

import java.io.BufferedWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes the three output files produced by CBACT01C:
 * <ul>
 *   <li>OUTFILE  — fixed-length account extract</li>
 *   <li>ARRYFILE — array-format account records</li>
 *   <li>VBRCFILE — variable-length records (two types per account)</li>
 * </ul>
 *
 * Output is written as pipe-delimited text (modern equivalent of
 * COBOL fixed-width/COMP-3 binary records).
 */
public final class AccountFileWriter implements AutoCloseable {

    private final BufferedWriter outWriter;
    private final BufferedWriter arryWriter;
    private final BufferedWriter vbrcWriter;

    public AccountFileWriter(Path outFile, Path arryFile, Path vbrcFile) throws IOException {
        this.outWriter = Files.newBufferedWriter(outFile);
        this.arryWriter = Files.newBufferedWriter(arryFile);
        this.vbrcWriter = Files.newBufferedWriter(vbrcFile);
    }

    public void writeOutputRecord(OutputAccountRecord rec) throws IOException {
        outWriter.write(formatOutputRecord(rec));
        outWriter.newLine();
    }

    public void writeArrayRecord(ArrayAccountRecord rec) throws IOException {
        arryWriter.write(formatArrayRecord(rec));
        arryWriter.newLine();
    }

    public void writeVbrRecord(VbrRecord rec) throws IOException {
        vbrcWriter.write(formatVbrRecord(rec));
        vbrcWriter.newLine();
    }

    private String formatOutputRecord(OutputAccountRecord rec) {
        return "%011d|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s".formatted(
                rec.acctId(),
                rec.activeStatus(),
                formatDecimal(rec.currBal()),
                formatDecimal(rec.creditLimit()),
                formatDecimal(rec.cashCreditLimit()),
                rec.openDate(),
                rec.expirationDate(),
                rec.reissueDate(),
                formatDecimal(rec.currCycCredit()),
                formatDecimal(rec.currCycDebit()),
                rec.groupId()
        );
    }

    private String formatArrayRecord(ArrayAccountRecord rec) {
        StringBuilder sb = new StringBuilder();
        sb.append("%011d".formatted(rec.acctId()));
        for (int i = 0; i < ArrayAccountRecord.OCCURS_COUNT; i++) {
            ArrayAccountRecord.BalanceEntry entry = rec.balanceEntries()[i];
            sb.append("|%s|%s".formatted(
                    formatDecimal(entry.currBal()),
                    formatDecimal(entry.currCycDebit())
            ));
        }
        return sb.toString();
    }

    private String formatVbrRecord(VbrRecord rec) {
        if (rec instanceof VbrRecord.Type1 r1) {
            return "T1|%011d|%s".formatted(r1.acctId(), r1.activeStatus());
        } else if (rec instanceof VbrRecord.Type2 r2) {
            return "T2|%011d|%s|%s|%s".formatted(
                    r2.acctId(),
                    formatDecimal(r2.currBal()),
                    formatDecimal(r2.creditLimit()),
                    r2.reissueYyyy()
            );
        }
        throw new IllegalArgumentException("Unknown VbrRecord type: " + rec.getClass());
    }

    private String formatDecimal(BigDecimal value) {
        return value.setScale(2).toPlainString();
    }

    @Override
    public void close() throws IOException {
        outWriter.close();
        arryWriter.close();
        vbrcWriter.close();
    }
}
