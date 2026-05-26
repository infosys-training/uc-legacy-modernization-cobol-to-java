package com.carddemo.model;

import java.math.BigDecimal;

/**
 * Maps COBOL FD OUT-FILE record: OUT-ACCT-REC
 *
 * This is the flattened output written by paragraph 1300-POPUL-ACCT-RECORD.
 *
 * COBOL layout:
 *   05 OUT-ACCT-ID                 PIC 9(11)
 *   05 OUT-ACCT-ACTIVE-STATUS      PIC X(01)
 *   05 OUT-ACCT-CURR-BAL           PIC S9(10)V99
 *   05 OUT-ACCT-CREDIT-LIMIT       PIC S9(10)V99
 *   05 OUT-ACCT-CASH-CREDIT-LIMIT  PIC S9(10)V99
 *   05 OUT-ACCT-OPEN-DATE          PIC X(10)
 *   05 OUT-ACCT-EXPIRAION-DATE     PIC X(10)
 *   05 OUT-ACCT-REISSUE-DATE       PIC X(10)
 *   05 OUT-ACCT-CURR-CYC-CREDIT    PIC S9(10)V99
 *   05 OUT-ACCT-CURR-CYC-DEBIT     PIC S9(10)V99 COMP-3
 *   05 OUT-ACCT-GROUP-ID           PIC X(10)
 */
public class OutputAccountRecord {

    private long acctId;
    private char activeStatus;
    private BigDecimal currentBalance;
    private BigDecimal creditLimit;
    private BigDecimal cashCreditLimit;
    private String openDate;
    private String expirationDate;
    private String reissueDate;
    private BigDecimal currentCycleCredit;
    private BigDecimal currentCycleDebit;
    private String groupId;

    public long getAcctId() { return acctId; }
    public void setAcctId(long acctId) { this.acctId = acctId; }

    public char getActiveStatus() { return activeStatus; }
    public void setActiveStatus(char activeStatus) { this.activeStatus = activeStatus; }

    public BigDecimal getCurrentBalance() { return currentBalance; }
    public void setCurrentBalance(BigDecimal currentBalance) { this.currentBalance = currentBalance; }

    public BigDecimal getCreditLimit() { return creditLimit; }
    public void setCreditLimit(BigDecimal creditLimit) { this.creditLimit = creditLimit; }

    public BigDecimal getCashCreditLimit() { return cashCreditLimit; }
    public void setCashCreditLimit(BigDecimal cashCreditLimit) { this.cashCreditLimit = cashCreditLimit; }

    public String getOpenDate() { return openDate; }
    public void setOpenDate(String openDate) { this.openDate = openDate; }

    public String getExpirationDate() { return expirationDate; }
    public void setExpirationDate(String expirationDate) { this.expirationDate = expirationDate; }

    public String getReissueDate() { return reissueDate; }
    public void setReissueDate(String reissueDate) { this.reissueDate = reissueDate; }

    public BigDecimal getCurrentCycleCredit() { return currentCycleCredit; }
    public void setCurrentCycleCredit(BigDecimal currentCycleCredit) { this.currentCycleCredit = currentCycleCredit; }

    public BigDecimal getCurrentCycleDebit() { return currentCycleDebit; }
    public void setCurrentCycleDebit(BigDecimal currentCycleDebit) { this.currentCycleDebit = currentCycleDebit; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    @Override
    public String toString() {
        return String.format("%011d|%c|%s|%s|%s|%s|%s|%s|%s|%s|%s",
                acctId, activeStatus, currentBalance, creditLimit, cashCreditLimit,
                openDate, expirationDate, reissueDate, currentCycleCredit, currentCycleDebit, groupId);
    }
}
