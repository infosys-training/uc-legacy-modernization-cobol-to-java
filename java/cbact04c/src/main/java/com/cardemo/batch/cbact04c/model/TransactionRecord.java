package com.cardemo.batch.cbact04c.model;

import java.math.BigDecimal;

/**
 * Transaction record (CVTRA05Y.cpy, RECLN 350).
 * Maps to COBOL copybook TRAN-RECORD.
 */
public class TransactionRecord {

    private String transactionId;     // TRAN-ID PIC X(16)
    private String typeCode;          // TRAN-TYPE-CD PIC X(02)
    private int categoryCode;         // TRAN-CAT-CD PIC 9(04)
    private String source;            // TRAN-SOURCE PIC X(10)
    private String description;       // TRAN-DESC PIC X(100)
    private BigDecimal amount;        // TRAN-AMT PIC S9(09)V99
    private String merchantId;        // TRAN-MERCHANT-ID PIC 9(09)
    private String merchantName;      // TRAN-MERCHANT-NAME PIC X(50)
    private String merchantCity;      // TRAN-MERCHANT-CITY PIC X(50)
    private String merchantZip;       // TRAN-MERCHANT-ZIP PIC X(10)
    private String cardNumber;        // TRAN-CARD-NUM PIC X(16)
    private String origTimestamp;     // TRAN-ORIG-TS PIC X(26)
    private String procTimestamp;     // TRAN-PROC-TS PIC X(26)

    public TransactionRecord() {}

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getTypeCode() { return typeCode; }
    public void setTypeCode(String typeCode) { this.typeCode = typeCode; }

    public int getCategoryCode() { return categoryCode; }
    public void setCategoryCode(int categoryCode) { this.categoryCode = categoryCode; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public String getMerchantId() { return merchantId; }
    public void setMerchantId(String merchantId) { this.merchantId = merchantId; }

    public String getMerchantName() { return merchantName; }
    public void setMerchantName(String merchantName) { this.merchantName = merchantName; }

    public String getMerchantCity() { return merchantCity; }
    public void setMerchantCity(String merchantCity) { this.merchantCity = merchantCity; }

    public String getMerchantZip() { return merchantZip; }
    public void setMerchantZip(String merchantZip) { this.merchantZip = merchantZip; }

    public String getCardNumber() { return cardNumber; }
    public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }

    public String getOrigTimestamp() { return origTimestamp; }
    public void setOrigTimestamp(String origTimestamp) { this.origTimestamp = origTimestamp; }

    public String getProcTimestamp() { return procTimestamp; }
    public void setProcTimestamp(String procTimestamp) { this.procTimestamp = procTimestamp; }

    /**
     * Serializes to a fixed-width line (350 chars) matching COBOL TRAN-RECORD layout.
     */
    public String toFixedWidth() {
        StringBuilder sb = new StringBuilder(350);
        sb.append(padRight(transactionId, 16));
        sb.append(padRight(typeCode, 2));
        sb.append(String.format("%04d", categoryCode));
        sb.append(padRight(source, 10));
        sb.append(padRight(description, 100));
        sb.append(formatSignedDecimal(amount, 9, 2));
        sb.append(String.format("%09d", merchantId != null ? Long.parseLong(merchantId) : 0));
        sb.append(padRight(merchantName, 50));
        sb.append(padRight(merchantCity, 50));
        sb.append(padRight(merchantZip, 10));
        sb.append(padRight(cardNumber, 16));
        sb.append(padRight(origTimestamp, 26));
        sb.append(padRight(procTimestamp, 26));
        while (sb.length() < 350) sb.append(' ');
        return sb.substring(0, 350);
    }

    private static String padRight(String s, int len) {
        if (s == null) s = "";
        if (s.length() >= len) return s.substring(0, len);
        return s + " ".repeat(len - s.length());
    }

    private static final char[] POSITIVE_OVERPUNCH = {'{', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I'};
    private static final char[] NEGATIVE_OVERPUNCH = {'}', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R'};

    private static String formatSignedDecimal(BigDecimal value, int intDigits, int decDigits) {
        if (value == null) value = BigDecimal.ZERO;
        boolean negative = value.signum() < 0;
        BigDecimal abs = value.abs();
        int totalDigits = intDigits + decDigits;
        BigDecimal shifted = abs.movePointRight(decDigits);
        BigDecimal maxValue = new BigDecimal("9".repeat(totalDigits));
        if (shifted.compareTo(maxValue) > 0) {
            throw new ArithmeticException(
                    "Value overflow: magnitude exceeds PIC S9(" + intDigits + ")V" + "9".repeat(decDigits) + " capacity");
        }
        long unscaled = shifted.longValue();
        String digits = String.format("%0" + totalDigits + "d", unscaled);
        if (digits.length() > totalDigits) {
            digits = digits.substring(digits.length() - totalDigits);
        }
        int lastDigit = digits.charAt(digits.length() - 1) - '0';
        char overpunch = negative ? NEGATIVE_OVERPUNCH[lastDigit] : POSITIVE_OVERPUNCH[lastDigit];
        return digits.substring(0, digits.length() - 1) + overpunch;
    }

    @Override
    public String toString() {
        return "TransactionRecord{id=" + transactionId + ", amt=" + amount +
               ", desc=" + description + "}";
    }
}
