package com.cardemo.batch.io;

import com.cardemo.batch.model.ArrayAccountRecord;
import com.cardemo.batch.model.ArrayAccountRecord.BalanceEntry;
import com.cardemo.batch.model.OutAccountRecord;
import com.cardemo.batch.model.VbrcRecord1;
import com.cardemo.batch.model.VbrcRecord2;

import java.math.BigDecimal;

/**
 * Formats output records as pipe-delimited text lines for human-readable output.
 */
public final class RecordFormatter {

    private static final String SEP = "|";

    private RecordFormatter() {}

    public static String format(OutAccountRecord r) {
        return String.join(SEP,
                r.acctId(),
                r.activeStatus(),
                r.currBal().toPlainString(),
                r.creditLimit().toPlainString(),
                r.cashCreditLimit().toPlainString(),
                r.openDate(),
                r.expirationDate(),
                r.reissueDate(),
                r.currCycCredit().toPlainString(),
                r.currCycDebit().toPlainString(),
                r.groupId());
    }

    public static String format(ArrayAccountRecord r) {
        var sb = new StringBuilder();
        sb.append(r.acctId());
        for (BalanceEntry e : r.balanceEntries()) {
            sb.append(SEP).append(e.currBal().toPlainString());
            sb.append(SEP).append(e.currCycDebit().toPlainString());
        }
        return sb.toString();
    }

    public static String format(VbrcRecord1 r) {
        return String.join(SEP, r.acctId(), r.activeStatus());
    }

    public static String format(VbrcRecord2 r) {
        return String.join(SEP,
                r.acctId(),
                r.currBal().toPlainString(),
                r.creditLimit().toPlainString(),
                r.reissueYear());
    }
}
