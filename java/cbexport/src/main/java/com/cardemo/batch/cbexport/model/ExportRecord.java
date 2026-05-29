package com.cardemo.batch.cbexport.model;

import java.math.BigDecimal;
import java.util.List;

/**
 * Sealed interface for all export record types.
 * Maps to CVEXPORT.cpy — Export record with common header fields.
 */
public sealed interface ExportRecord
        permits ExportRecord.CustomerExport,
                ExportRecord.AccountExport,
                ExportRecord.XrefExport,
                ExportRecord.TransactionExport,
                ExportRecord.CardExport {

    char recordType();
    String timestamp();
    int sequenceNum();
    String branchId();
    String regionCode();

    record CustomerExport(
            String timestamp,
            int sequenceNum,
            String branchId,
            String regionCode,
            int custId,
            String firstName,
            String middleName,
            String lastName,
            List<String> addressLines,
            String stateCode,
            String countryCode,
            String zipCode,
            List<String> phoneNumbers,
            int ssn,
            String govtIssuedId,
            String dateOfBirth,
            String eftAccountId,
            String primaryCardHolderInd,
            int ficoCreditScore
    ) implements ExportRecord {
        @Override
        public char recordType() { return 'C'; }
    }

    record AccountExport(
            String timestamp,
            int sequenceNum,
            String branchId,
            String regionCode,
            String acctId,
            String activeStatus,
            BigDecimal currentBalance,
            BigDecimal creditLimit,
            BigDecimal cashCreditLimit,
            String openDate,
            String expirationDate,
            String reissueDate,
            BigDecimal currentCycleCredit,
            BigDecimal currentCycleDebit,
            String zipCode,
            String groupId
    ) implements ExportRecord {
        @Override
        public char recordType() { return 'A'; }
    }

    record XrefExport(
            String timestamp,
            int sequenceNum,
            String branchId,
            String regionCode,
            String cardNum,
            int custId,
            String acctId
    ) implements ExportRecord {
        @Override
        public char recordType() { return 'X'; }
    }

    record TransactionExport(
            String timestamp,
            int sequenceNum,
            String branchId,
            String regionCode,
            String tranId,
            String typeCode,
            int categoryCode,
            String source,
            String description,
            BigDecimal amount,
            int merchantId,
            String merchantName,
            String merchantCity,
            String merchantZip,
            String cardNum,
            String origTimestamp,
            String procTimestamp
    ) implements ExportRecord {
        @Override
        public char recordType() { return 'T'; }
    }

    record CardExport(
            String timestamp,
            int sequenceNum,
            String branchId,
            String regionCode,
            String cardNum,
            String acctId,
            int cvvCode,
            String embossedName,
            String expirationDate,
            String activeStatus
    ) implements ExportRecord {
        @Override
        public char recordType() { return 'D'; }
    }
}
