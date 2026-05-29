package com.cardemo.batch.cbact03c.model;

/**
 * Java record mapping to COBOL copybook CVACT03Y.cpy CARD-XREF-RECORD (RECLN 50).
 *
 * <pre>
 * 05 XREF-CARD-NUM   PIC X(16)  — Card number (alphanumeric)
 * 05 XREF-CUST-ID    PIC 9(09)  — Customer ID (unsigned numeric)
 * 05 XREF-ACCT-ID    PIC 9(11)  — Account ID (unsigned numeric)
 * 05 FILLER           PIC X(14)
 * </pre>
 *
 * @param cardNumber  16-character card number
 * @param customerId  9-digit customer ID
 * @param accountId   11-digit account ID
 */
public record CardXrefRecord(
        String cardNumber,
        long customerId,
        long accountId
) {

    /** Full COBOL record length including filler. */
    public static final int COBOL_RECORD_LENGTH = 50;

    /** Minimum length required (without filler). */
    public static final int MIN_RECORD_LENGTH = 36;

    public String toPipeDelimited() {
        return cardNumber + "|" + customerId + "|" + accountId;
    }
}
