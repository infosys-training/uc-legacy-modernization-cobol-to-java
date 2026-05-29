package com.cardemo.batch.cbexport.service;

import com.cardemo.batch.cbexport.io.*;
import com.cardemo.batch.cbexport.model.*;
import com.cardemo.batch.cbexport.model.ExportRecord.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Orchestrates the branch migration export pipeline:
 * reads all five input files, creates export records with
 * common header fields, writes pipe-delimited output, and
 * tracks statistics.
 */
public final class ExportService {

    private static final String BRANCH_ID = "0001";
    private static final String REGION_CODE = "NORTH";
    private static final DateTimeFormatter TS_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SS");

    private int sequenceCounter;
    private int customerCount;
    private int accountCount;
    private int xrefCount;
    private int transactionCount;
    private int cardCount;

    public ExportService() {
        this.sequenceCounter = 0;
    }

    public record ExportStatistics(
            int customers, int accounts, int xrefs,
            int transactions, int cards, int total
    ) {}

    public ExportStatistics runExport(
            Path customerFile,
            Path accountFile,
            Path xrefFile,
            Path transactionFile,
            Path cardFile,
            Path outputFile
    ) throws IOException {
        String timestamp = generateTimestamp();
        List<ExportRecord> exportRecords = new ArrayList<>();

        System.out.println("CBEXPORT: Starting Customer Data Export");
        System.out.println("CBEXPORT: Export Timestamp: " + timestamp);

        // 1. Export Customers
        System.out.println("CBEXPORT: Processing customer records");
        List<CustomerRecord> customers = CustomerFileReader.readAll(customerFile);
        for (CustomerRecord c : customers) {
            exportRecords.add(createCustomerExport(c, timestamp));
        }
        customerCount = customers.size();
        System.out.println("CBEXPORT: Customers exported: " + customerCount);

        // 2. Export Accounts
        System.out.println("CBEXPORT: Processing account records");
        List<AccountRecord> accounts = AccountFileReader.readAll(accountFile);
        for (AccountRecord a : accounts) {
            exportRecords.add(createAccountExport(a, timestamp));
        }
        accountCount = accounts.size();
        System.out.println("CBEXPORT: Accounts exported: " + accountCount);

        // 3. Export Cross-References
        System.out.println("CBEXPORT: Processing cross-reference records");
        List<CardXrefRecord> xrefs = CardXrefFileReader.readAll(xrefFile);
        for (CardXrefRecord x : xrefs) {
            exportRecords.add(createXrefExport(x, timestamp));
        }
        xrefCount = xrefs.size();
        System.out.println("CBEXPORT: Cross-references exported: " + xrefCount);

        // 4. Export Transactions
        System.out.println("CBEXPORT: Processing transaction records");
        List<TransactionRecord> transactions = TransactionFileReader.readAll(transactionFile);
        for (TransactionRecord t : transactions) {
            exportRecords.add(createTransactionExport(t, timestamp));
        }
        transactionCount = transactions.size();
        System.out.println("CBEXPORT: Transactions exported: " + transactionCount);

        // 5. Export Cards
        System.out.println("CBEXPORT: Processing card records");
        List<CardRecord> cards = CardFileReader.readAll(cardFile);
        for (CardRecord d : cards) {
            exportRecords.add(createCardExport(d, timestamp));
        }
        cardCount = cards.size();
        System.out.println("CBEXPORT: Cards exported: " + cardCount);

        // Write output
        ExportFileWriter.writeAll(outputFile, exportRecords);

        int total = customerCount + accountCount + xrefCount
                + transactionCount + cardCount;

        // Display statistics
        System.out.println("CBEXPORT: Export completed");
        System.out.println("CBEXPORT: Customers Exported: " + customerCount);
        System.out.println("CBEXPORT: Accounts Exported: " + accountCount);
        System.out.println("CBEXPORT: XRefs Exported: " + xrefCount);
        System.out.println("CBEXPORT: Transactions Exported: " + transactionCount);
        System.out.println("CBEXPORT: Cards Exported: " + cardCount);
        System.out.println("CBEXPORT: Total Records Exported: " + total);

        return new ExportStatistics(customerCount, accountCount, xrefCount,
                transactionCount, cardCount, total);
    }

    String generateTimestamp() {
        return LocalDateTime.now().format(TS_FORMATTER);
    }

    private int nextSequence() {
        return ++sequenceCounter;
    }

    CustomerExport createCustomerExport(CustomerRecord c, String timestamp) {
        return new CustomerExport(
                timestamp, nextSequence(), BRANCH_ID, REGION_CODE,
                c.custId(), c.firstName(), c.middleName(), c.lastName(),
                c.addressLines(), c.stateCode(), c.countryCode(), c.zipCode(),
                c.phoneNumbers(), c.ssn(), c.govtIssuedId(), c.dateOfBirth(),
                c.eftAccountId(), c.primaryCardHolderInd(), c.ficoCreditScore()
        );
    }

    AccountExport createAccountExport(AccountRecord a, String timestamp) {
        return new AccountExport(
                timestamp, nextSequence(), BRANCH_ID, REGION_CODE,
                a.acctId(), a.activeStatus(), a.currentBalance(),
                a.creditLimit(), a.cashCreditLimit(),
                a.openDate(), a.expirationDate(), a.reissueDate(),
                a.currentCycleCredit(), a.currentCycleDebit(),
                a.zipCode(), a.groupId()
        );
    }

    XrefExport createXrefExport(CardXrefRecord x, String timestamp) {
        return new XrefExport(
                timestamp, nextSequence(), BRANCH_ID, REGION_CODE,
                x.cardNum(), x.custId(), x.acctId()
        );
    }

    TransactionExport createTransactionExport(TransactionRecord t, String timestamp) {
        return new TransactionExport(
                timestamp, nextSequence(), BRANCH_ID, REGION_CODE,
                t.tranId(), t.typeCode(), t.categoryCode(), t.source(),
                t.description(), t.amount(), t.merchantId(),
                t.merchantName(), t.merchantCity(), t.merchantZip(),
                t.cardNum(), t.origTimestamp(), t.procTimestamp()
        );
    }

    CardExport createCardExport(CardRecord d, String timestamp) {
        return new CardExport(
                timestamp, nextSequence(), BRANCH_ID, REGION_CODE,
                d.cardNum(), d.acctId(), d.cvvCode(),
                d.embossedName(), d.expirationDate(), d.activeStatus()
        );
    }
}
