package com.carddemo.model;

import java.math.BigDecimal;

/**
 * Maps COBOL copybook: CVACT01Y.cpy (ACCOUNT-RECORD, 300 bytes)
 *
 * COBOL layout:
 *   05 ACCT-ID                  PIC 9(11)        → long (11 digits)
 *   05 ACCT-ACTIVE-STATUS       PIC X(01)        → char
 *   05 ACCT-CURR-BAL            PIC S9(10)V99    → BigDecimal (signed, 2 decimal)
 *   05 ACCT-CREDIT-LIMIT        PIC S9(10)V99    → BigDecimal
 *   05 ACCT-CASH-CREDIT-LIMIT   PIC S9(10)V99    → BigDecimal
 *   05 ACCT-OPEN-DATE           PIC X(10)        → String (YYYY-MM-DD)
 *   05 ACCT-EXPIRAION-DATE      PIC X(10)        → String (YYYY-MM-DD)
 *   05 ACCT-REISSUE-DATE        PIC X(10)        → String (YYYY-MM-DD)
 *   05 ACCT-CURR-CYC-CREDIT     PIC S9(10)V99    → BigDecimal
 *   05 ACCT-CURR-CYC-DEBIT      PIC S9(10)V99    → BigDecimal
 *   05 ACCT-ADDR-ZIP            PIC X(10)        → String
 *   05 ACCT-GROUP-ID            PIC X(10)        → String
 *   05 FILLER                   PIC X(178)       → (padding, ignored)
 */
public class AccountRecord {

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
    private String addrZip;
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

    public String getAddrZip() { return addrZip; }
    public void setAddrZip(String addrZip) { this.addrZip = addrZip; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    @Override
    public String toString() {
        return String.format(
                "AccountRecord{acctId=%011d, status=%c, bal=%s, creditLimit=%s, cashLimit=%s, " +
                "open=%s, expiry=%s, reissue=%s, cycCredit=%s, cycDebit=%s, groupId=%s}",
                acctId, activeStatus, currentBalance, creditLimit, cashCreditLimit,
                openDate, expirationDate, reissueDate, currentCycleCredit, currentCycleDebit, groupId);
    }
}
