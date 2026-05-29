package com.cardemo.batch.io;

import com.cardemo.batch.model.ArrayRecord;
import com.cardemo.batch.model.OutAccountRecord;
import com.cardemo.batch.model.VbrcRecord1;
import com.cardemo.batch.model.VbrcRecord2;

import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Writes output records as pipe-delimited text.  Each writer mirrors one of
 * the three COBOL output files (OUTFILE, ARRYFILE, VBRCFILE).
 */
public final class OutputFileWriter implements Closeable {

    private final PrintWriter writer;

    public OutputFileWriter(Path path) throws IOException {
        this.writer = new PrintWriter(Files.newBufferedWriter(path));
    }

    public void writeOutRecord(OutAccountRecord rec) {
        writer.printf("%011d|%s|%s|%s|%s|%s|%s|%s|%s|%s|%s%n",
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
                rec.groupId());
    }

    public void writeArrayRecord(ArrayRecord rec) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%011d", rec.acctId()));
        for (ArrayRecord.Entry entry : rec.entries()) {
            sb.append('|').append(entry.currBal().toPlainString());
            sb.append('|').append(entry.currCycDebit().toPlainString());
        }
        writer.println(sb);
    }

    public void writeVbrc1(VbrcRecord1 rec) {
        writer.printf("VB1|%011d|%s%n", rec.acctId(), rec.activeStatus());
    }

    public void writeVbrc2(VbrcRecord2 rec) {
        writer.printf("VB2|%011d|%s|%s|%s%n",
                rec.acctId(),
                rec.currBal().toPlainString(),
                rec.creditLimit().toPlainString(),
                rec.reissueYear());
    }

    @Override
    public void close() throws IOException {
        writer.close();
    }
}
