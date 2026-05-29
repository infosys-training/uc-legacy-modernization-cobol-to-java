package com.cardemo.batch.cbact04c.service;

import com.cardemo.batch.cbact04c.io.*;
import com.cardemo.batch.cbact04c.model.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Core business logic for batch interest calculation.
 * Mirrors COBOL program CBACT04C PROCEDURE DIVISION.
 *
 * Processing flow:
 * 1. Read TCATBAL records sequentially
 * 2. On account change: update previous account balance, reset accumulators
 * 3. Look up interest rate from disclosure group (with DEFAULT fallback)
 * 4. Compute monthly interest: (categoryBalance * interestRate) / 1200
 * 5. Write interest transaction records
 * 6. After EOF: final account update
 */
public class InterestCalculatorService {

    private static final BigDecimal TWELVE_HUNDRED = new BigDecimal("1200");
    private static final String INTEREST_TRAN_TYPE = "01";
    private static final int INTEREST_TRAN_CAT = 5;
    private static final String INTEREST_SOURCE = "System";

    private final String parmDate;
    private final List<TranCatBalRecord> tranCatBalRecords;
    private final Map<String, CardXrefRecord> xrefByAcct;
    private final Map<String, DisclosureGroupRecord> discGroupByKey;
    private final Map<String, AccountRecord> accountsByKey;

    private final List<TransactionRecord> outputTransactions = new ArrayList<>();
    private int tranIdSuffix = 0;

    public InterestCalculatorService(String parmDate,
                                      List<TranCatBalRecord> tranCatBalRecords,
                                      Map<String, CardXrefRecord> xrefByAcct,
                                      Map<String, DisclosureGroupRecord> discGroupByKey,
                                      Map<String, AccountRecord> accountsByKey) {
        this.parmDate = parmDate;
        this.tranCatBalRecords = tranCatBalRecords;
        this.xrefByAcct = xrefByAcct;
        this.discGroupByKey = discGroupByKey;
        this.accountsByKey = accountsByKey;
    }

    /**
     * Executes the interest calculation batch process.
     */
    public void process() {
        String lastAcctNum = "";
        boolean firstTime = true;
        BigDecimal totalInterest = BigDecimal.ZERO;
        AccountRecord currentAccount = null;
        CardXrefRecord currentXref = null;

        System.out.println("START OF EXECUTION OF PROGRAM CBACT04C");

        for (TranCatBalRecord tcatbal : tranCatBalRecords) {
            System.out.println(tcatbal);

            String acctId = tcatbal.getAccountId();

            if (!acctId.equals(lastAcctNum)) {
                if (!firstTime && currentAccount != null) {
                    updateAccount(currentAccount, totalInterest);
                } else {
                    firstTime = false;
                }

                totalInterest = BigDecimal.ZERO;
                lastAcctNum = acctId;

                currentAccount = accountsByKey.get(acctId);
                if (currentAccount == null) {
                    System.out.println("ACCOUNT NOT FOUND: " + acctId);
                    throw new RuntimeException("ERROR READING ACCOUNT FILE: " + acctId);
                }

                currentXref = xrefByAcct.get(acctId);
                if (currentXref == null) {
                    System.out.println("ACCOUNT NOT FOUND IN XREF: " + acctId);
                    throw new RuntimeException("ERROR READING XREF FILE: " + acctId);
                }
            }

            String groupId = currentAccount.getGroupId();
            DisclosureGroupRecord discGroup = DisclosureGroupFileReader.lookup(
                    discGroupByKey, groupId, tcatbal.getTypeCode(), tcatbal.getCategoryCode());

            if (discGroup == null) {
                System.out.println("DISCLOSURE GROUP RECORD MISSING for group=" + groupId +
                        " type=" + tcatbal.getTypeCode() + " cat=" + tcatbal.getCategoryCode());
                System.out.println("DEFAULT GROUP ALSO NOT FOUND");
                throw new RuntimeException("ERROR READING DISCLOSURE GROUP FILE");
            }

            if (discGroup.getInterestRate().compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal monthlyInterest = computeInterest(tcatbal.getBalance(), discGroup.getInterestRate());
                totalInterest = totalInterest.add(monthlyInterest);
                writeInterestTransaction(acctId, monthlyInterest, currentXref.getCardNumber());
            }
        }

        // Final account update after EOF
        if (currentAccount != null) {
            updateAccount(currentAccount, totalInterest);
        }

        System.out.println("END OF EXECUTION OF PROGRAM CBACT04C");
    }

    /**
     * Computes monthly interest: (categoryBalance * interestRate) / 1200
     */
    public static BigDecimal computeInterest(BigDecimal categoryBalance, BigDecimal interestRate) {
        return categoryBalance.multiply(interestRate).divide(TWELVE_HUNDRED, 2, RoundingMode.HALF_UP);
    }

    /**
     * Updates account: adds total interest to current balance, zeros cycle credits/debits.
     * Mirrors COBOL 1050-UPDATE-ACCOUNT paragraph.
     */
    private void updateAccount(AccountRecord account, BigDecimal totalInterest) {
        account.setCurrentBalance(account.getCurrentBalance().add(totalInterest));
        account.setCurrentCycleCredit(BigDecimal.ZERO);
        account.setCurrentCycleDebit(BigDecimal.ZERO);
    }

    /**
     * Creates and records an interest transaction.
     * Mirrors COBOL 1300-B-WRITE-TX paragraph.
     */
    private void writeInterestTransaction(String acctId, BigDecimal monthlyInterest, String cardNumber) {
        tranIdSuffix++;
        String tranId = parmDate + String.format("%06d", tranIdSuffix);

        TransactionRecord tran = new TransactionRecord();
        tran.setTransactionId(tranId);
        tran.setTypeCode(INTEREST_TRAN_TYPE);
        tran.setCategoryCode(INTEREST_TRAN_CAT);
        tran.setSource(INTEREST_SOURCE);
        tran.setDescription("Int. for a/c " + acctId);
        tran.setAmount(monthlyInterest);
        tran.setMerchantId("000000000");
        tran.setMerchantName("");
        tran.setMerchantCity("");
        tran.setMerchantZip("");
        tran.setCardNumber(cardNumber);

        String timestamp = TimestampFormatter.currentDb2Timestamp();
        tran.setOrigTimestamp(timestamp);
        tran.setProcTimestamp(timestamp);

        outputTransactions.add(tran);
    }

    public List<TransactionRecord> getOutputTransactions() {
        return outputTransactions;
    }

    public Map<String, AccountRecord> getUpdatedAccounts() {
        return accountsByKey;
    }
}
