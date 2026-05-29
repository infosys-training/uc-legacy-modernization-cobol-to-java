package com.cardemo.batch.cbcus01c.model;

/**
 * Java record mapping to COBOL copybook CVCUS01Y (CUSTOMER-RECORD, RECLN 500).
 *
 * <p>Field layout (fixed-width offsets):
 * <pre>
 *   CUST-ID                 PIC 9(09)   offset  0, length  9
 *   CUST-FIRST-NAME         PIC X(25)   offset  9, length 25
 *   CUST-MIDDLE-NAME        PIC X(25)   offset 34, length 25
 *   CUST-LAST-NAME          PIC X(25)   offset 59, length 25
 *   CUST-ADDR-LINE-1        PIC X(50)   offset 84, length 50
 *   CUST-ADDR-LINE-2        PIC X(50)   offset134, length 50
 *   CUST-ADDR-LINE-3        PIC X(50)   offset184, length 50
 *   CUST-ADDR-STATE-CD      PIC X(02)   offset234, length  2
 *   CUST-ADDR-COUNTRY-CD    PIC X(03)   offset236, length  3
 *   CUST-ADDR-ZIP           PIC X(10)   offset239, length 10
 *   CUST-PHONE-NUM-1        PIC X(15)   offset249, length 15
 *   CUST-PHONE-NUM-2        PIC X(15)   offset264, length 15
 *   CUST-SSN                PIC 9(09)   offset279, length  9
 *   CUST-GOVT-ISSUED-ID     PIC X(20)   offset288, length 20
 *   CUST-DOB-YYYY-MM-DD     PIC X(10)   offset308, length 10
 *   CUST-EFT-ACCOUNT-ID     PIC X(10)   offset318, length 10
 *   CUST-PRI-CARD-HOLDER    PIC X(01)   offset328, length  1
 *   CUST-FICO-CREDIT-SCORE  PIC 9(03)   offset329, length  3
 *   FILLER                  PIC X(168)  offset332, length168
 * </pre>
 */
public record CustomerRecord(
        int custId,
        String firstName,
        String middleName,
        String lastName,
        String addrLine1,
        String addrLine2,
        String addrLine3,
        String stateCd,
        String countryCd,
        String zip,
        String phoneNum1,
        String phoneNum2,
        String ssn,
        String govtIssuedId,
        String dob,
        String eftAccountId,
        String priCardHolderInd,
        int ficoCreditScore
) {

    /** Pipe-delimited CSV header matching field order. */
    public static final String CSV_HEADER = String.join("|",
            "CUST_ID", "FIRST_NAME", "MIDDLE_NAME", "LAST_NAME",
            "ADDR_LINE_1", "ADDR_LINE_2", "ADDR_LINE_3",
            "STATE_CD", "COUNTRY_CD", "ZIP",
            "PHONE_NUM_1", "PHONE_NUM_2",
            "SSN", "GOVT_ISSUED_ID", "DOB", "EFT_ACCOUNT_ID",
            "PRI_CARD_HOLDER_IND", "FICO_CREDIT_SCORE");

    /** Formats this record as a pipe-delimited CSV line. */
    public String toCsvLine() {
        return String.join("|",
                String.valueOf(custId),
                firstName, middleName, lastName,
                addrLine1, addrLine2, addrLine3,
                stateCd, countryCd, zip,
                phoneNum1, phoneNum2,
                ssn, govtIssuedId, dob, eftAccountId,
                priCardHolderInd,
                String.valueOf(ficoCreditScore));
    }
}
