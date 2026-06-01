package com.carddemo.parser;

import java.util.List;

import static com.carddemo.parser.CobolFieldDefinition.*;

public final class RecordLayouts {

    private RecordLayouts() {
    }

    /** acctdata.txt - CVACT01Y.cpy - ACCOUNT-RECORD (300 bytes) */
    public static CobolRecordLayout accountRecord() {
        return new CobolRecordLayout("ACCOUNT-RECORD", List.of(
                numeric("ACCT-ID", 11),
                alphanumeric("ACCT-ACTIVE-STATUS", 1),
                signedNumeric("ACCT-CURR-BAL", 10, 2),
                signedNumeric("ACCT-CREDIT-LIMIT", 10, 2),
                signedNumeric("ACCT-CASH-CREDIT-LIMIT", 10, 2),
                alphanumeric("ACCT-OPEN-DATE", 10),
                alphanumeric("ACCT-EXPIRAION-DATE", 10),
                alphanumeric("ACCT-REISSUE-DATE", 10),
                signedNumeric("ACCT-CURR-CYC-CREDIT", 10, 2),
                signedNumeric("ACCT-CURR-CYC-DEBIT", 10, 2),
                alphanumeric("ACCT-ADDR-ZIP", 10),
                alphanumeric("ACCT-GROUP-ID", 10),
                filler(178)
        ));
    }

    /** carddata.txt - CVACT02Y.cpy - CARD-RECORD (150 bytes) */
    public static CobolRecordLayout cardRecord() {
        return new CobolRecordLayout("CARD-RECORD", List.of(
                alphanumeric("CARD-NUM", 16),
                numeric("CARD-ACCT-ID", 11),
                numeric("CARD-CVV-CD", 3),
                alphanumeric("CARD-EMBOSSED-NAME", 50),
                alphanumeric("CARD-EXPIRAION-DATE", 10),
                alphanumeric("CARD-ACTIVE-STATUS", 1),
                filler(59)
        ));
    }

    /** cardxref.txt - CVACT03Y.cpy - CARD-XREF-RECORD (50 bytes) */
    public static CobolRecordLayout cardXrefRecord() {
        return new CobolRecordLayout("CARD-XREF-RECORD", List.of(
                alphanumeric("XREF-CARD-NUM", 16),
                numeric("XREF-CUST-ID", 9),
                numeric("XREF-ACCT-ID", 11),
                filler(14)
        ));
    }

    /** custdata.txt - CVCUS01Y.cpy - CUSTOMER-RECORD (500 bytes) */
    public static CobolRecordLayout customerRecord() {
        return new CobolRecordLayout("CUSTOMER-RECORD", List.of(
                numeric("CUST-ID", 9),
                alphanumeric("CUST-FIRST-NAME", 25),
                alphanumeric("CUST-MIDDLE-NAME", 25),
                alphanumeric("CUST-LAST-NAME", 25),
                alphanumeric("CUST-ADDR-LINE-1", 50),
                alphanumeric("CUST-ADDR-LINE-2", 50),
                alphanumeric("CUST-ADDR-LINE-3", 50),
                alphanumeric("CUST-ADDR-STATE-CD", 2),
                alphanumeric("CUST-ADDR-COUNTRY-CD", 3),
                alphanumeric("CUST-ADDR-ZIP", 10),
                alphanumeric("CUST-PHONE-NUM-1", 15),
                alphanumeric("CUST-PHONE-NUM-2", 15),
                numeric("CUST-SSN", 9),
                alphanumeric("CUST-GOVT-ISSUED-ID", 20),
                alphanumeric("CUST-DOB-YYYY-MM-DD", 10),
                alphanumeric("CUST-EFT-ACCOUNT-ID", 10),
                alphanumeric("CUST-PRI-CARD-HOLDER-IND", 1),
                numeric("CUST-FICO-CREDIT-SCORE", 3),
                filler(168)
        ));
    }

    /** dailytran.txt - CVTRA06Y.cpy - DALYTRAN-RECORD (350 bytes) */
    public static CobolRecordLayout dailyTransactionRecord() {
        return new CobolRecordLayout("DALYTRAN-RECORD", List.of(
                alphanumeric("DALYTRAN-ID", 16),
                alphanumeric("DALYTRAN-TYPE-CD", 2),
                numeric("DALYTRAN-CAT-CD", 4),
                alphanumeric("DALYTRAN-SOURCE", 10),
                alphanumeric("DALYTRAN-DESC", 100),
                signedNumeric("DALYTRAN-AMT", 9, 2),
                numeric("DALYTRAN-MERCHANT-ID", 9),
                alphanumeric("DALYTRAN-MERCHANT-NAME", 50),
                alphanumeric("DALYTRAN-MERCHANT-CITY", 50),
                alphanumeric("DALYTRAN-MERCHANT-ZIP", 10),
                alphanumeric("DALYTRAN-CARD-NUM", 16),
                alphanumeric("DALYTRAN-ORIG-TS", 26),
                alphanumeric("DALYTRAN-PROC-TS", 26),
                filler(20)
        ));
    }

    /** discgrp.txt - CVTRA02Y.cpy - DIS-GROUP-RECORD (50 bytes) */
    public static CobolRecordLayout disclosureGroupRecord() {
        return new CobolRecordLayout("DIS-GROUP-RECORD", List.of(
                alphanumeric("DIS-ACCT-GROUP-ID", 10),
                alphanumeric("DIS-TRAN-TYPE-CD", 2),
                numeric("DIS-TRAN-CAT-CD", 4),
                signedNumeric("DIS-INT-RATE", 4, 2),
                filler(28)
        ));
    }

    /** tcatbal.txt - CVTRA01Y.cpy - TRAN-CAT-BAL-RECORD (50 bytes) */
    public static CobolRecordLayout tranCatBalRecord() {
        return new CobolRecordLayout("TRAN-CAT-BAL-RECORD", List.of(
                numeric("TRANCAT-ACCT-ID", 11),
                alphanumeric("TRANCAT-TYPE-CD", 2),
                numeric("TRANCAT-CD", 4),
                signedNumeric("TRAN-CAT-BAL", 9, 2),
                filler(22)
        ));
    }

    /** trancatg.txt - CVTRA04Y.cpy - TRAN-CAT-RECORD (60 bytes) */
    public static CobolRecordLayout tranCatRecord() {
        return new CobolRecordLayout("TRAN-CAT-RECORD", List.of(
                alphanumeric("TRAN-TYPE-CD", 2),
                numeric("TRAN-CAT-CD", 4),
                alphanumeric("TRAN-CAT-TYPE-DESC", 50),
                filler(4)
        ));
    }

    /** trantype.txt - CVTRA03Y.cpy - TRAN-TYPE-RECORD (60 bytes) */
    public static CobolRecordLayout tranTypeRecord() {
        return new CobolRecordLayout("TRAN-TYPE-RECORD", List.of(
                alphanumeric("TRAN-TYPE", 2),
                alphanumeric("TRAN-TYPE-DESC", 50),
                filler(8)
        ));
    }
}
