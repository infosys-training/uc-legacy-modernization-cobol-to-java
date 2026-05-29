package com.cardemo.batch.cbexport.io;

import com.cardemo.batch.cbexport.model.ExportRecord;
import com.cardemo.batch.cbexport.model.ExportRecord.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.StringJoiner;

/**
 * Writes export records as pipe-delimited CSV.
 * Each line starts with: RecordType|Timestamp|SequenceNum|BranchID|RegionCode|…fields…
 */
public final class ExportFileWriter {

    private static final String DELIMITER = "|";

    private ExportFileWriter() {}

    public static void writeAll(Path path, List<ExportRecord> records) throws IOException {
        try (BufferedWriter writer = Files.newBufferedWriter(path)) {
            writer.write(headerLine());
            writer.newLine();
            for (ExportRecord record : records) {
                writer.write(formatRecord(record));
                writer.newLine();
            }
        }
    }

    public static String headerLine() {
        return "RecordType|Timestamp|SequenceNum|BranchID|RegionCode|Data";
    }

    public static String formatRecord(ExportRecord record) {
        StringJoiner sj = new StringJoiner(DELIMITER);
        sj.add(String.valueOf(record.recordType()));
        sj.add(record.timestamp());
        sj.add(String.valueOf(record.sequenceNum()));
        sj.add(record.branchId());
        sj.add(record.regionCode());

        if (record instanceof CustomerExport c) {
            sj.add(String.valueOf(c.custId()));
            sj.add(c.firstName());
            sj.add(c.middleName());
            sj.add(c.lastName());
            sj.add(c.addressLines().get(0));
            sj.add(c.addressLines().get(1));
            sj.add(c.addressLines().get(2));
            sj.add(c.stateCode());
            sj.add(c.countryCode());
            sj.add(c.zipCode());
            sj.add(c.phoneNumbers().get(0));
            sj.add(c.phoneNumbers().get(1));
            sj.add(String.valueOf(c.ssn()));
            sj.add(c.govtIssuedId());
            sj.add(c.dateOfBirth());
            sj.add(c.eftAccountId());
            sj.add(c.primaryCardHolderInd());
            sj.add(String.valueOf(c.ficoCreditScore()));
        } else if (record instanceof AccountExport a) {
            sj.add(a.acctId());
            sj.add(a.activeStatus());
            sj.add(a.currentBalance().toPlainString());
            sj.add(a.creditLimit().toPlainString());
            sj.add(a.cashCreditLimit().toPlainString());
            sj.add(a.openDate());
            sj.add(a.expirationDate());
            sj.add(a.reissueDate());
            sj.add(a.currentCycleCredit().toPlainString());
            sj.add(a.currentCycleDebit().toPlainString());
            sj.add(a.zipCode());
            sj.add(a.groupId());
        } else if (record instanceof XrefExport x) {
            sj.add(x.cardNum());
            sj.add(String.valueOf(x.custId()));
            sj.add(x.acctId());
        } else if (record instanceof TransactionExport t) {
            sj.add(t.tranId());
            sj.add(t.typeCode());
            sj.add(String.valueOf(t.categoryCode()));
            sj.add(t.source());
            sj.add(t.description());
            sj.add(t.amount().toPlainString());
            sj.add(String.valueOf(t.merchantId()));
            sj.add(t.merchantName());
            sj.add(t.merchantCity());
            sj.add(t.merchantZip());
            sj.add(t.cardNum());
            sj.add(t.origTimestamp());
            sj.add(t.procTimestamp());
        } else if (record instanceof CardExport d) {
            sj.add(d.cardNum());
            sj.add(d.acctId());
            sj.add(String.valueOf(d.cvvCode()));
            sj.add(d.embossedName());
            sj.add(d.expirationDate());
            sj.add(d.activeStatus());
        }
        return sj.toString();
    }
}
