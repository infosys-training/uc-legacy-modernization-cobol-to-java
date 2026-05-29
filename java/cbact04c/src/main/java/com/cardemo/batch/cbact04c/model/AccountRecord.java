package com.cardemo.batch.cbact04c.model;

import java.math.BigDecimal;

/**
 * Account master record (CVACT01Y.cpy, RECLN 300).
 * Maps to COBOL copybook ACCOUNT-RECORD.
 */
public class AccountRecord {

    private String accountId;            // ACCT-ID PIC 9(11)
    private String activeStatus;         // ACCT-ACTIVE-STATUS PIC X(01)
    private BigDecimal currentBalance;   // ACCT-CURR-BAL PIC S9(10)V99
    private BigDecimal creditLimit;      // ACCT-CREDIT-LIMIT PIC S9(10)V99
    private BigDecimal cashCreditLimit;  // ACCT-CASH-CREDIT-LIMIT PIC S9(10)V99
    private String openDate;             // ACCT-OPEN-DATE PIC X(10)
    private String expirationDate;       // ACCT-EXPIRAION-DATE PIC X(10)
    private String reissueDate;          // ACCT-REISSUE-DATE PIC X(10)
    private BigDecimal currentCycleCredit; // ACCT-CURR-CYC-CREDIT PIC S9(10)V99
    private BigDecimal currentCycleDebit;  // ACCT-CURR-CYC-DEBIT PIC S9(10)V99
    private String addressZip;           // ACCT-ADDR-ZIP PIC X(10)
    private String groupId;              // ACCT-GROUP-ID PIC X(10)

    public AccountRecord() {}

    public String getAccountId() { return accountId; }
    public void setAccountId(String accountId) { this.accountId = accountId; }

    public String getActiveStatus() { return activeStatus; }
    public void setActiveStatus(String activeStatus) { this.activeStatus = activeStatus; }

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

    public String getAddressZip() { return addressZip; }
    public void setAddressZip(String addressZip) { this.addressZip = addressZip; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    /**
     * Serializes this record back to a fixed-width line (300 chars) for rewrite.
     */
    public String toFixedWidth() {
        StringBuilder sb = new StringBuilder(300);
        sb.append(padRight(accountId, 11));
        sb.append(padRight(activeStatus, 1));
        sb.append(CobolFormatHelper.formatSignedDecimal(currentBalance, 10, 2));
        sb.append(CobolFormatHelper.formatSignedDecimal(creditLimit, 10, 2));
        sb.append(CobolFormatHelper.formatSignedDecimal(cashCreditLimit, 10, 2));
        sb.append(padRight(openDate, 10));
        sb.append(padRight(expirationDate, 10));
        sb.append(padRight(reissueDate, 10));
        sb.append(CobolFormatHelper.formatSignedDecimal(currentCycleCredit, 10, 2));
        sb.append(CobolFormatHelper.formatSignedDecimal(currentCycleDebit, 10, 2));
        sb.append(padRight(addressZip, 10));
        sb.append(padRight(groupId, 10));
        while (sb.length() < 300) sb.append(' ');
        return sb.substring(0, 300);
    }

    private static String padRight(String s, int len) {
        if (s == null) s = "";
        if (s.length() >= len) return s.substring(0, len);
        return s + " ".repeat(len - s.length());
    }

    @Override
    public String toString() {
        return "AccountRecord{acctId=" + accountId + ", status=" + activeStatus +
               ", bal=" + currentBalance + ", group=" + groupId + "}";
    }

    /**
     * Helper for formatting signed decimal fields with COBOL trailing overpunch.
     */
    static class CobolFormatHelper {
        private static final char[] POSITIVE_OVERPUNCH = {'{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
        private static final char[] NEGATIVE_OVERPUNCH = {'}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R'};

        static String formatSignedDecimal(BigDecimal value, int intDigits, int decDigits) {
            if (value == null) value = BigDecimal.ZERO;
            boolean negative = value.signum() < 0;
            BigDecimal abs = value.abs();
            long unscaled = abs.movePointRight(decDigits).longValue();
            int totalDigits = intDigits + decDigits;
            String digits = String.format("%0" + totalDigits + "d", unscaled);
            if (digits.length() > totalDigits) {
                digits = digits.substring(digits.length() - totalDigits);
            }
            int lastDigit = digits.charAt(digits.length() - 1) - '0';
            char overpunch = negative ? NEGATIVE_OVERPUNCH[lastDigit] : POSITIVE_OVERPUNCH[lastDigit];
            return digits.substring(0, digits.length() - 1) + overpunch;
        }
    }
}
