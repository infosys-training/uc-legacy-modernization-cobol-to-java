package com.carddemo.batch.parser;

import com.carddemo.batch.model.AccountRecord;
import com.carddemo.parser.CobolSignDecoder;
import java.math.BigDecimal;

/**
 * Parses 300-char fixed-length lines from acctdata.txt into AccountRecord.
 * Uses CobolSignDecoder for trailing overpunch sign-encoded zoned decimal fields.
 */
public class AccountRecordParser {
    private static final int RECORD_LENGTH = 300;

    public AccountRecord parse(String line) {
        String padded = line.length() >= RECORD_LENGTH
                ? line
                : String.format("%-" + RECORD_LENGTH + "s", line);

        String acctId = padded.substring(0, 11);
        String activeStatus = padded.substring(11, 12);
        BigDecimal currentBalance = CobolSignDecoder.decode(padded.substring(12, 24), 2);
        BigDecimal creditLimit = CobolSignDecoder.decode(padded.substring(24, 36), 2);
        BigDecimal cashCreditLimit = CobolSignDecoder.decode(padded.substring(36, 48), 2);
        String openDate = padded.substring(48, 58).trim();
        String expirationDate = padded.substring(58, 68).trim();
        String reissueDate = padded.substring(68, 78).trim();
        BigDecimal currentCycleCredit = CobolSignDecoder.decode(padded.substring(78, 90), 2);
        BigDecimal currentCycleDebit = CobolSignDecoder.decode(padded.substring(90, 102), 2);
        String addressZip = padded.substring(102, 112).trim();
        String groupId = padded.substring(112, 122).trim();

        return new AccountRecord(acctId, activeStatus, currentBalance, creditLimit,
                cashCreditLimit, openDate, expirationDate, reissueDate,
                currentCycleCredit, currentCycleDebit, addressZip, groupId);
    }
}
