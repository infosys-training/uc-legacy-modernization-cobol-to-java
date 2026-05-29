package com.carddemo.testharness.model;

import com.carddemo.testharness.parser.CobolFieldDefinition;

import java.util.List;

import static com.carddemo.testharness.parser.CobolFieldDefinition.*;

public final class RecordLayouts {

    private RecordLayouts() {}

    public static RecordLayout acctdata() {
        return new RecordLayout("acctdata", List.of(
            numeric("ACCT-ID", 0, 11),
            alphanumeric("ACCT-ACTIVE-STATUS", 11, 1),
            signedDecimal("ACCT-CURR-BAL", 12, 12, 2),
            signedDecimal("ACCT-CREDIT-LIMIT", 24, 12, 2),
            signedDecimal("ACCT-CASH-CREDIT-LIMIT", 36, 12, 2),
            alphanumeric("ACCT-OPEN-DATE", 48, 10),
            alphanumeric("ACCT-EXPIRAION-DATE", 58, 10),
            alphanumeric("ACCT-REISSUE-DATE", 68, 10),
            signedDecimal("ACCT-CURR-CYC-CREDIT", 78, 12, 2),
            signedDecimal("ACCT-CURR-CYC-DEBIT", 90, 12, 2),
            alphanumeric("ACCT-ADDR-ZIP", 102, 10),
            alphanumeric("ACCT-GROUP-ID", 112, 10),
            filler(122, 178)
        ), 300);
    }

    public static RecordLayout carddata() {
        return new RecordLayout("carddata", List.of(
            alphanumeric("CARD-NUM", 0, 16),
            numeric("CARD-ACCT-ID", 16, 11),
            numeric("CARD-CVV-CD", 27, 3),
            alphanumeric("CARD-EMBOSSED-NAME", 30, 50),
            alphanumeric("CARD-EXPIRAION-DATE", 80, 10),
            alphanumeric("CARD-ACTIVE-STATUS", 90, 1),
            filler(91, 59)
        ), 150);
    }

    public static RecordLayout cardxref() {
        return new RecordLayout("cardxref", List.of(
            alphanumeric("XREF-CARD-NUM", 0, 16),
            numeric("XREF-CUST-ID", 16, 9),
            numeric("XREF-ACCT-ID", 25, 11),
            filler(36, 14)
        ), 50);
    }

    public static RecordLayout custdata() {
        return new RecordLayout("custdata", List.of(
            numeric("CUST-ID", 0, 9),
            alphanumeric("CUST-FIRST-NAME", 9, 25),
            alphanumeric("CUST-MIDDLE-NAME", 34, 25),
            alphanumeric("CUST-LAST-NAME", 59, 25),
            alphanumeric("CUST-ADDR-LINE-1", 84, 50),
            alphanumeric("CUST-ADDR-LINE-2", 134, 50),
            alphanumeric("CUST-ADDR-LINE-3", 184, 50),
            alphanumeric("CUST-ADDR-STATE-CD", 234, 2),
            alphanumeric("CUST-ADDR-COUNTRY-CD", 236, 3),
            alphanumeric("CUST-ADDR-ZIP", 239, 10),
            alphanumeric("CUST-PHONE-NUM-1", 249, 15),
            alphanumeric("CUST-PHONE-NUM-2", 264, 15),
            numeric("CUST-SSN", 279, 9),
            alphanumeric("CUST-GOVT-ISSUED-ID", 288, 20),
            alphanumeric("CUST-DOB-YYYY-MM-DD", 308, 10),
            alphanumeric("CUST-EFT-ACCOUNT-ID", 318, 10),
            alphanumeric("CUST-PRI-CARD-HOLDER-IND", 328, 1),
            numeric("CUST-FICO-CREDIT-SCORE", 329, 3),
            filler(332, 168)
        ), 500);
    }

    public static RecordLayout dailytran() {
        return new RecordLayout("dailytran", List.of(
            alphanumeric("DALYTRAN-ID", 0, 16),
            alphanumeric("DALYTRAN-TYPE-CD", 16, 2),
            numeric("DALYTRAN-CAT-CD", 18, 4),
            alphanumeric("DALYTRAN-SOURCE", 22, 10),
            alphanumeric("DALYTRAN-DESC", 32, 100),
            signedDecimal("DALYTRAN-AMT", 132, 11, 2),
            numeric("DALYTRAN-MERCHANT-ID", 143, 9),
            alphanumeric("DALYTRAN-MERCHANT-NAME", 152, 50),
            alphanumeric("DALYTRAN-MERCHANT-CITY", 202, 50),
            alphanumeric("DALYTRAN-MERCHANT-ZIP", 252, 10),
            alphanumeric("DALYTRAN-CARD-NUM", 262, 16),
            alphanumeric("DALYTRAN-ORIG-TS", 278, 26),
            alphanumeric("DALYTRAN-PROC-TS", 304, 26),
            filler(330, 20)
        ), 350);
    }

    public static RecordLayout discgrp() {
        return new RecordLayout("discgrp", List.of(
            alphanumeric("DIS-ACCT-GROUP-ID", 0, 10),
            alphanumeric("DIS-TRAN-TYPE-CD", 10, 2),
            numeric("DIS-TRAN-CAT-CD", 12, 4),
            signedDecimal("DIS-INT-RATE", 16, 6, 2),
            filler(22, 28)
        ), 50);
    }

    public static RecordLayout tcatbal() {
        return new RecordLayout("tcatbal", List.of(
            numeric("TRANCAT-ACCT-ID", 0, 11),
            alphanumeric("TRANCAT-TYPE-CD", 11, 2),
            numeric("TRANCAT-CD", 13, 4),
            signedDecimal("TRAN-CAT-BAL", 17, 11, 2),
            filler(28, 22)
        ), 50);
    }

    public static RecordLayout trancatg() {
        return new RecordLayout("trancatg", List.of(
            alphanumeric("TRAN-TYPE-CD", 0, 2),
            numeric("TRAN-CAT-CD", 2, 4),
            alphanumeric("TRAN-CAT-TYPE-DESC", 6, 50),
            filler(56, 4)
        ), 60);
    }

    public static RecordLayout trantype() {
        return new RecordLayout("trantype", List.of(
            alphanumeric("TRAN-TYPE", 0, 2),
            alphanumeric("TRAN-TYPE-DESC", 2, 50),
            filler(52, 8)
        ), 60);
    }
}
