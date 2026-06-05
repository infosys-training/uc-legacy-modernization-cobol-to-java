# DATA DICTIONARY — CardDemo COBOL Estate

> Extracted from all copybooks in `app/cpy/` and sub-application directories

## Summary

| Entity Group | Copybooks | Total Fields |
|-------------|----------:|-------------:|
| Account | 4 | 31 |
| Customer | 2 | 19 |
| Card | 2 | 10 |
| Transaction | 8 | 72 |
| Authorization (IMS) | 5 | 51 |
| UI / Communication | 10 | 48 |
| Utility / Shared | 6 | 40 |
| **Total** | **37** | **~271** |

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Record (300 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `ACCT-ID` | `9(11)` | Numeric | Unique account identifier — primary key of ACCTFILE VSAM KSDS | 11-digit numeric, key offset 0 |
| 2 | `ACCT-ACTIVE-STATUS` | `X(01)` | Alpha | Account status flag | 'Y' = active, 'N' = inactive |
| 3 | `ACCT-CURR-BAL` | `S9(10)V99` | Signed Decimal | Current account balance | Signed, 2 decimal places |
| 4 | `ACCT-CREDIT-LIMIT` | `S9(10)V99` | Signed Decimal | Credit limit for the account | Must be positive |
| 5 | `ACCT-CASH-CREDIT-LIMIT` | `S9(10)V99` | Signed Decimal | Cash advance credit limit | Must be positive |
| 6 | `ACCT-OPEN-DATE` | `X(10)` | Date String | Account opening date | Format: YYYY-MM-DD |
| 7 | `ACCT-EXPIRAION-DATE` | `X(10)` | Date String | Account expiration date | Format: YYYY-MM-DD |
| 8 | `ACCT-REISSUE-DATE` | `X(10)` | Date String | Last card reissue date | Format: YYYY-MM-DD |
| 9 | `ACCT-CURR-CYC-CREDIT` | `S9(10)V99` | Signed Decimal | Current billing cycle credits | Running total, reset on cycle |
| 10 | `ACCT-CURR-CYC-DEBIT` | `S9(10)V99` | Signed Decimal | Current billing cycle debits | Running total, reset on cycle |
| 11 | `ACCT-ADDR-ZIP` | `X(10)` | Alphanumeric | Account holder ZIP/postal code | Validated against CSLKPCDY lookup |
| 12 | `ACCT-GROUP-ID` | `X(10)` | Alphanumeric | Disclosure group identifier — links to interest rate tables | FK to DISCGRP file |
| 13 | `FILLER` | `X(178)` | Padding | Reserved for future use | — |

### 1.2 CVACT03Y.cpy — Card Cross-Reference Record (50 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `XREF-CARD-NUM` | `X(16)` | Alphanumeric | Card number — primary key | 16 chars, FK to CARDFILE |
| 2 | `XREF-CUST-ID` | `9(09)` | Numeric | Customer identifier | 9-digit, FK to CUSTFILE |
| 3 | `XREF-ACCT-ID` | `9(11)` | Numeric | Account identifier | 11-digit, FK to ACCTFILE |
| 4 | `FILLER` | `X(14)` | Padding | Reserved | — |

### 1.3 CVTRA01Y.cpy — Transaction Category Balance (50 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `TRANCAT-ACCT-ID` | `9(11)` | Numeric | Account ID for category balance | Part of composite key |
| 2 | `TRANCAT-TYPE-CD` | `X(02)` | Alphanumeric | Transaction type code | Part of composite key |
| 3 | `TRANCAT-CD` | `9(04)` | Numeric | Transaction category code | Part of composite key |
| 4 | `TRAN-CAT-BAL` | `S9(09)V99` | Signed Decimal | Category balance amount | Running balance per category |
| 5 | `FILLER` | `X(22)` | Padding | Reserved | — |

### 1.4 CVTRA02Y.cpy — Disclosure Group Record (50 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `DIS-ACCT-GROUP-ID` | `X(10)` | Alphanumeric | Account group identifier — primary key | Part of composite key |
| 2 | `DIS-TRAN-TYPE-CD` | `X(02)` | Alphanumeric | Transaction type code | Part of composite key |
| 3 | `DIS-TRAN-CAT-CD` | `9(04)` | Numeric | Transaction category code | Part of composite key |
| 4 | `DIS-INT-RATE` | `S9(04)V99` | Signed Decimal | Interest rate for this group/type/category | Percentage, 2 decimal places |
| 5 | `FILLER` | `X(28)` | Padding | Reserved | — |

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Record (500 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `CUST-ID` | `9(09)` | Numeric | Customer ID — primary key of CUSTFILE VSAM | 9-digit numeric, key offset 0 |
| 2 | `CUST-FIRST-NAME` | `X(25)` | Alpha | Customer first name | Non-empty for active customers |
| 3 | `CUST-MIDDLE-NAME` | `X(25)` | Alpha | Customer middle name | Optional |
| 4 | `CUST-LAST-NAME` | `X(25)` | Alpha | Customer last name | Non-empty for active customers |
| 5 | `CUST-ADDR-LINE-1` | `X(50)` | Alphanumeric | Street address line 1 | Required |
| 6 | `CUST-ADDR-LINE-2` | `X(50)` | Alphanumeric | Street address line 2 | Optional |
| 7 | `CUST-ADDR-LINE-3` | `X(50)` | Alphanumeric | Street address line 3 | Optional |
| 8 | `CUST-ADDR-STATE-CD` | `X(02)` | Alpha | US state code | Validated via CSLKPCDY 88-level |
| 9 | `CUST-ADDR-COUNTRY-CD` | `X(03)` | Alpha | Country code | ISO 3-char |
| 10 | `CUST-ADDR-ZIP` | `X(10)` | Alphanumeric | ZIP/postal code | Validated via CSLKPCDY |
| 11 | `CUST-PHONE-NUM-1` | `X(15)` | Alphanumeric | Primary phone | Area code validated via CSLKPCDY |
| 12 | `CUST-PHONE-NUM-2` | `X(15)` | Alphanumeric | Secondary phone | Optional |
| 13 | `CUST-SSN` | `9(09)` | Numeric | Social Security Number | 9-digit, validated non-zero |
| 14 | `CUST-GOVT-ISSUED-ID` | `X(20)` | Alphanumeric | Government-issued ID (e.g., driver's license) | Optional |
| 15 | `CUST-DOB-YYYY-MM-DD` | `X(10)` | Date String | Date of birth | Format: YYYY-MM-DD, validated via CEEDAYS |
| 16 | `CUST-EFT-ACCOUNT-ID` | `X(10)` | Alphanumeric | Electronic funds transfer account | Optional |
| 17 | `CUST-PRI-CARD-HOLDER-IND` | `X(01)` | Alpha | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| 18 | `CUST-FICO-CREDIT-SCORE` | `9(03)` | Numeric | FICO credit score | Range: 300–850 |
| 19 | `FILLER` | `X(168)` | Padding | Reserved | — |

### 2.2 CUSTREC.cpy — Statement Customer Record (alternate layout used by CBSTM03A)

*(Re-exports CVCUS01Y fields under alias names for report generation)*

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Record (150 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `CARD-NUM` | `X(16)` | Alphanumeric | Card number — primary key of CARDFILE | 16-char, unique |
| 2 | `CARD-ACCT-ID` | `9(11)` | Numeric | Linked account ID | FK to ACCTFILE |
| 3 | `CARD-CVV-CD` | `9(03)` | Numeric | Card Verification Value | 3-digit numeric |
| 4 | `CARD-EMBOSSED-NAME` | `X(50)` | Alphanumeric | Name printed on the card | Non-empty |
| 5 | `CARD-EXPIRAION-DATE` | `X(10)` | Date String | Card expiry date | MM/YY validated by COCRDUPC |
| 6 | `CARD-ACTIVE-STATUS` | `X(01)` | Alpha | Card activation status | 'Y' = active, 'N' = inactive |
| 7 | `FILLER` | `X(59)` | Padding | Reserved | — |

### 3.2 CVCRD01Y.cpy — Internal Card Work Area

*(Contains the same CARD-RECORD structure used as CICS working storage for card screen programs)*

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Record (350 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `TRAN-ID` | `X(16)` | Alphanumeric | Transaction ID — primary key of TRANSACT VSAM | System-generated unique |
| 2 | `TRAN-TYPE-CD` | `X(02)` | Alphanumeric | Transaction type code (e.g., SA=Sale, RF=Refund) | FK to TRANTYPE file |
| 3 | `TRAN-CAT-CD` | `9(04)` | Numeric | Transaction category code | FK to TRANCATG file |
| 4 | `TRAN-SOURCE` | `X(10)` | Alphanumeric | Transaction source (POS, ATM, WEB, etc.) | Enum-like set |
| 5 | `TRAN-DESC` | `X(100)` | Alphanumeric | Transaction description | Free text |
| 6 | `TRAN-AMT` | `S9(09)V99` | Signed Decimal | Transaction amount | Positive = debit, negative = credit |
| 7 | `TRAN-MERCHANT-ID` | `9(09)` | Numeric | Merchant identifier | 9-digit |
| 8 | `TRAN-MERCHANT-NAME` | `X(50)` | Alphanumeric | Merchant name | Free text |
| 9 | `TRAN-MERCHANT-CITY` | `X(50)` | Alphanumeric | Merchant city | Free text |
| 10 | `TRAN-MERCHANT-ZIP` | `X(10)` | Alphanumeric | Merchant ZIP code | 5 or 10 chars |
| 11 | `TRAN-CARD-NUM` | `X(16)` | Alphanumeric | Card number used in transaction | FK to CARDFILE |
| 12 | `TRAN-ORIG-TS` | `X(26)` | Timestamp | Transaction origination timestamp | YYYY-MM-DD-HH.MM.SS.FFFFFF |
| 13 | `TRAN-PROC-TS` | `X(26)` | Timestamp | Transaction processing timestamp | YYYY-MM-DD-HH.MM.SS.FFFFFF |
| 14 | `FILLER` | `X(20)` | Padding | Reserved | — |

### 4.2 CVTRA06Y.cpy — Daily Transaction Record (350 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `DALYTRAN-ID` | `X(16)` | Alphanumeric | Daily transaction ID | Matches TRAN-ID layout |
| 2 | `DALYTRAN-TYPE-CD` | `X(02)` | Alphanumeric | Transaction type code | Same as TRAN-TYPE-CD |
| 3 | `DALYTRAN-CAT-CD` | `9(04)` | Numeric | Transaction category | Same as TRAN-CAT-CD |
| 4 | `DALYTRAN-SOURCE` | `X(10)` | Alphanumeric | Source of transaction | Same as TRAN-SOURCE |
| 5 | `DALYTRAN-DESC` | `X(100)` | Alphanumeric | Description | Same as TRAN-DESC |
| 6 | `DALYTRAN-AMT` | `S9(09)V99` | Signed Decimal | Amount | Same as TRAN-AMT |
| 7 | `DALYTRAN-MERCHANT-ID` | `9(09)` | Numeric | Merchant ID | Same as TRAN-MERCHANT-ID |
| 8 | `DALYTRAN-MERCHANT-NAME` | `X(50)` | Alphanumeric | Merchant name | — |
| 9 | `DALYTRAN-MERCHANT-CITY` | `X(50)` | Alphanumeric | Merchant city | — |
| 10 | `DALYTRAN-MERCHANT-ZIP` | `X(10)` | Alphanumeric | Merchant ZIP | — |
| 11 | `DALYTRAN-CARD-NUM` | `X(16)` | Alphanumeric | Card used | FK to CARDFILE |
| 12 | `DALYTRAN-ORIG-TS` | `X(26)` | Timestamp | Original timestamp | — |
| 13 | `DALYTRAN-PROC-TS` | `X(26)` | Timestamp | Processing timestamp | — |
| 14 | `FILLER` | `X(20)` | Padding | Reserved | — |

### 4.3 CVTRA03Y.cpy — Transaction Type Record (60 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `TRAN-TYPE` | `X(02)` | Alphanumeric | Transaction type code — primary key | 2-char code (SA, RF, etc.) |
| 2 | `TRAN-TYPE-DESC` | `X(50)` | Alphanumeric | Description of the transaction type | Free text |
| 3 | `FILLER` | `X(08)` | Padding | Reserved | — |

### 4.4 CVTRA04Y.cpy — Transaction Category Record (60 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `TRAN-TYPE-CD` | `X(02)` | Alphanumeric | Type code — part of composite key | FK to TRANTYPE |
| 2 | `TRAN-CAT-CD` | `9(04)` | Numeric | Category code — part of composite key | 4-digit |
| 3 | `TRAN-CAT-TYPE-DESC` | `X(50)` | Alphanumeric | Category description | Free text |
| 4 | `FILLER` | `X(04)` | Padding | Reserved | — |

### 4.5 CVTRA07Y.cpy — Date Parameter Record

*(Used by CBTRN03C for report date range filtering)*

| # | Field | PIC Clause | Data Type | Business Meaning |
|---|-------|-----------|-----------|-----------------|
| 1 | `DATEPARM-START` | `X(10)` | Date String | Report start date |
| 2 | `DATEPARM-END` | `X(10)` | Date String | Report end date |

### 4.6 COSTM01.CPY — Statement Report Fields

*(Report formatting structures used by CBSTM03A for plain-text and HTML statement generation)*

| # | Field | PIC Clause | Type | Meaning |
|---|-------|-----------|------|---------|
| 1 | `REPT-SHORT-NAME` | `X(38)` | Alpha | Report identifier ('DALYREPT') |
| 2 | `REPT-LONG-NAME` | `X(41)` | Alpha | Report title ('Daily Transaction Report') |
| 3 | `REPT-DATE-HEADER` | `X(12)` | Alpha | Date range label |
| 4 | `REPT-START-DATE` | `X(10)` | Date | Report period start |
| 5 | `REPT-END-DATE` | `X(10)` | Date | Report period end |
| 6 | `TRAN-REPORT-TRANS-ID` | `X(16)` | Alpha | Transaction ID for report line |
| 7 | `TRAN-REPORT-ACCOUNT-ID` | `X(11)` | Alpha | Account ID for report line |
| 8 | `TRAN-REPORT-TYPE-CD` | `X(02)` | Alpha | Tran type code for report |
| 9 | `TRAN-REPORT-AMT` | `-ZZZ,ZZZ,ZZZ.ZZ` | Edited Numeric | Formatted amount |
| 10 | `REPT-PAGE-TOTAL` | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited Numeric | Page subtotal |
| 11 | `REPT-ACCOUNT-TOTAL` | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited Numeric | Account subtotal |
| 12 | `REPT-GRAND-TOTAL` | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited Numeric | Grand total |

### 4.7 CVEXPORT.cpy — Export Record

*(Multi-record export format used by CBEXPORT/CBIMPORT for data migration)*

---

## 5. Authorization Entity (IMS Subsystem)

### 5.1 CIPAUSMY.cpy — IMS Pending Authorization Summary Segment

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `PA-ACCT-ID` | `S9(11) COMP-3` | Packed Decimal | Account ID — root segment key | IMS root segment key |
| 2 | `PA-CUST-ID` | `9(09)` | Numeric | Customer ID | FK to CUSTFILE |

### 5.2 CIPAUDTY.cpy — IMS Pending Authorization Detail Segment

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `PA-AUTH-DATE-9C` | `S9(05) COMP-3` | Packed Decimal | Authorization date (compressed) | Part of composite key |
| 2 | `PA-AUTH-TIME-9C` | `S9(09) COMP-3` | Packed Decimal | Authorization time (compressed) | Part of composite key |
| 3 | `PA-AUTH-ORIG-DATE` | `X(06)` | Alpha | Original auth date | YYMMDD |
| 4 | `PA-AUTH-ORIG-TIME` | `X(06)` | Alpha | Original auth time | HHMMSS |
| 5 | `PA-CARD-NUM` | `X(16)` | Alphanumeric | Card number | FK to CARDFILE |
| 6 | `PA-AUTH-TYPE` | `X(04)` | Alpha | Authorization type | — |
| 7 | `PA-CARD-EXPIRY-DATE` | `X(04)` | Alpha | Card expiry (MMYY) | — |
| 8 | `PA-MESSAGE-TYPE` | `X(06)` | Alpha | Message type code | — |
| 9 | `PA-MESSAGE-SOURCE` | `X(06)` | Alpha | Source of message | — |
| 10 | `PA-AUTH-ID-CODE` | `X(06)` | Alpha | Authorization ID code | Generated on approval |
| 11 | `PA-AUTH-RESP-CODE` | `X(02)` | Alpha | Response code | `88 PA-AUTH-APPROVED VALUE '00'` |
| 12 | `PA-AUTH-RESP-REASON` | `X(04)` | Alpha | Response reason code | — |
| 13 | `PA-PROCESSING-CODE` | `9(06)` | Numeric | Processing code | — |
| 14 | `PA-TRANSACTION-AMT` | `S9(10)V99 COMP-3` | Packed Decimal | Requested transaction amount | — |
| 15 | `PA-APPROVED-AMT` | `S9(10)V99 COMP-3` | Packed Decimal | Approved amount | — |
| 16 | `PA-MERCHANT-CATAGORY-CODE` | `X(04)` | Alpha | Merchant category code (MCC) | ISO 18245 |
| 17 | `PA-ACQR-COUNTRY-CODE` | `X(03)` | Alpha | Acquirer country code | ISO 3166 |
| 18 | `PA-POS-ENTRY-MODE` | `9(02)` | Numeric | Point-of-sale entry mode | ISO 8583 |
| 19 | `PA-MERCHANT-ID` | `X(15)` | Alphanumeric | Merchant ID | — |
| 20 | `PA-MERCHANT-NAME` | `X(22)` | Alphanumeric | Merchant name | — |
| 21 | `PA-MERCHANT-CITY` | `X(13)` | Alphanumeric | Merchant city | — |
| 22 | `PA-MERCHANT-STATE` | `X(02)` | Alpha | Merchant state | — |
| 23 | `PA-MERCHANT-ZIP` | `X(09)` | Alphanumeric | Merchant ZIP | — |
| 24 | `PA-TRANSACTION-ID` | `X(15)` | Alphanumeric | Transaction ID | — |
| 25 | `PA-MATCH-STATUS` | `X(01)` | Alpha | Match status | `P`=Pending, `D`=Declined, `E`=Expired, `M`=Matched |
| 26 | `PA-AUTH-FRAUD` | `X(01)` | Alpha | Fraud indicator | `F`=Fraud confirmed, `R`=Fraud removed |
| 27 | `PA-FRAUD-RPT-DATE` | `X(08)` | Date String | Fraud report date | YYYYMMDD |
| 28 | `FILLER` | `X(17)` | Padding | Reserved | — |

### 5.3 CCPAURQY.cpy — Authorization Request (MQ Message)

| # | Field | PIC Clause | Data Type | Business Meaning |
|---|-------|-----------|-----------|-----------------|
| 1 | `PA-RQ-AUTH-DATE` | `X(06)` | Alpha | Request date |
| 2 | `PA-RQ-AUTH-TIME` | `X(06)` | Alpha | Request time |
| 3 | `PA-RQ-CARD-NUM` | `X(16)` | Alphanumeric | Card number |
| 4 | `PA-RQ-AUTH-TYPE` | `X(04)` | Alpha | Auth type |
| 5 | `PA-RQ-CARD-EXPIRY-DATE` | `X(04)` | Alpha | Card expiry |
| 6 | `PA-RQ-MESSAGE-TYPE` | `X(06)` | Alpha | Message type |
| 7 | `PA-RQ-MESSAGE-SOURCE` | `X(06)` | Alpha | Message source |
| 8 | `PA-RQ-PROCESSING-CODE` | `9(06)` | Numeric | Processing code |
| 9 | `PA-RQ-TRANSACTION-AMT` | `+9(10).99` | Edited Numeric | Transaction amount |
| 10 | `PA-RQ-MERCHANT-CATAGORY-CODE` | `X(04)` | Alpha | MCC |
| 11 | `PA-RQ-ACQR-COUNTRY-CODE` | `X(03)` | Alpha | Country |
| 12 | `PA-RQ-POS-ENTRY-MODE` | `9(02)` | Numeric | POS entry mode |
| 13 | `PA-RQ-MERCHANT-ID` | `X(15)` | Alphanumeric | Merchant ID |
| 14 | `PA-RQ-MERCHANT-NAME` | `X(22)` | Alphanumeric | Merchant name |
| 15 | `PA-RQ-MERCHANT-CITY` | `X(13)` | Alphanumeric | Merchant city |
| 16 | `PA-RQ-MERCHANT-STATE` | `X(02)` | Alpha | Merchant state |
| 17 | `PA-RQ-MERCHANT-ZIP` | `X(09)` | Alphanumeric | Merchant ZIP |
| 18 | `PA-RQ-TRANSACTION-ID` | `X(15)` | Alphanumeric | Transaction ID |

### 5.4 CCPAURLY.cpy — Authorization Response (MQ Message)

| # | Field | PIC Clause | Data Type | Business Meaning |
|---|-------|-----------|-----------|-----------------|
| 1 | `PA-RL-CARD-NUM` | `X(16)` | Alphanumeric | Card number |
| 2 | `PA-RL-TRANSACTION-ID` | `X(15)` | Alphanumeric | Transaction ID |
| 3 | `PA-RL-AUTH-ID-CODE` | `X(06)` | Alpha | Authorization code |
| 4 | `PA-RL-AUTH-RESP-CODE` | `X(02)` | Alpha | Response code |
| 5 | `PA-RL-AUTH-RESP-REASON` | `X(04)` | Alpha | Reason code |
| 6 | `PA-RL-APPROVED-AMT` | `+9(10).99` | Edited Numeric | Approved amount |

### 5.5 CCPAUERY.cpy — Authorization Error Log

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `ERR-DATE` | `X(06)` | Alpha | Error date | YYMMDD |
| 2 | `ERR-TIME` | `X(06)` | Alpha | Error time | HHMMSS |
| 3 | `ERR-APPLICATION` | `X(08)` | Alpha | Application name | — |
| 4 | `ERR-PROGRAM` | `X(08)` | Alpha | Program that raised error | — |
| 5 | `ERR-LOCATION` | `X(04)` | Alpha | Location code in program | — |
| 6 | `ERR-LEVEL` | `X(01)` | Alpha | Error level | `L`=Log, `I`=Info, `W`=Warning, `C`=Critical |
| 7 | `ERR-SUBSYSTEM` | `X(01)` | Alpha | Subsystem source | `A`=App, `C`=CICS, `I`=IMS, `D`=DB2, `M`=MQ, `F`=File |
| 8 | `ERR-CODE-1` | `X(09)` | Alpha | Primary error code | — |
| 9 | `ERR-CODE-2` | `X(09)` | Alpha | Secondary error code | — |
| 10 | `ERR-MESSAGE` | `X(50)` | Alpha | Error message text | — |
| 11 | `ERR-EVENT-KEY` | `X(20)` | Alpha | Identifier for the failing event | — |

---

## 6. UI / Communication Copybooks

### 6.1 COCOM01Y.cpy — COMMAREA (Inter-Program Communication)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `CDEMO-FROM-TRANID` | `X(04)` | Alpha | Source CICS transaction ID | — |
| 2 | `CDEMO-FROM-PROGRAM` | `X(08)` | Alpha | Source program name | — |
| 3 | `CDEMO-TO-TRANID` | `X(04)` | Alpha | Target CICS transaction ID | — |
| 4 | `CDEMO-TO-PROGRAM` | `X(08)` | Alpha | Target program name | — |
| 5 | `CDEMO-USER-ID` | `X(08)` | Alpha | Logged-in user ID | — |
| 6 | `CDEMO-USER-TYPE` | `X(01)` | Alpha | User type | `88 CDEMO-USRTYP-ADMIN VALUE 'A'`, `88 CDEMO-USRTYP-USER VALUE 'U'` |
| 7 | `CDEMO-PGM-CONTEXT` | `9(01)` | Numeric | Program entry context | `88 CDEMO-PGM-ENTER VALUE 0`, `88 CDEMO-PGM-REENTER VALUE 1` |
| 8 | `CDEMO-CUST-ID` | `9(09)` | Numeric | Customer ID in context | — |
| 9 | `CDEMO-CUST-FNAME` | `X(25)` | Alpha | Customer first name | — |
| 10 | `CDEMO-CUST-MNAME` | `X(25)` | Alpha | Customer middle name | — |
| 11 | `CDEMO-CUST-LNAME` | `X(25)` | Alpha | Customer last name | — |
| 12 | `CDEMO-ACCT-ID` | `9(11)` | Numeric | Account ID in context | — |
| 13 | `CDEMO-ACCT-STATUS` | `X(01)` | Alpha | Account status in context | — |
| 14 | `CDEMO-CARD-NUM` | `9(16)` | Numeric | Card number in context | — |

### 6.2 CSUSR01Y.cpy — User Security Record (80 bytes)

| # | Field | PIC Clause | Data Type | Business Meaning | Validation |
|---|-------|-----------|-----------|-----------------|------------|
| 1 | `SEC-USR-ID` | `X(08)` | Alpha | User login ID — primary key | Unique, non-empty |
| 2 | `SEC-USR-FNAME` | `X(20)` | Alpha | User first name | — |
| 3 | `SEC-USR-LNAME` | `X(20)` | Alpha | User last name | — |
| 4 | `SEC-USR-PWD` | `X(08)` | Alpha | User password (plaintext) | 8-char max |
| 5 | `SEC-USR-TYPE` | `X(01)` | Alpha | User type | 'A' = Admin, 'U' = Regular |
| 6 | `SEC-USR-FILLER` | `X(23)` | Padding | Reserved | — |

### 6.3 CSMSG01Y.cpy — Common Messages

| # | Field | PIC Clause | Business Meaning |
|---|-------|-----------|-----------------|
| 1 | `CCDA-MSG-THANK-YOU` | `X(50)` | Thank you message |
| 2 | `CCDA-MSG-INVALID-KEY` | `X(50)` | Invalid key press message |

### 6.4 COTTL01Y.cpy — Screen Title Area

| # | Field | PIC Clause | Business Meaning |
|---|-------|-----------|-----------------|
| 1 | `CCDA-TITLE01` | `X(40)` | 'AWS Mainframe Modernization' |
| 2 | `CCDA-TITLE02` | `X(40)` | 'CardDemo' |
| 3 | `CCDA-THANK-YOU` | `X(40)` | Session thank-you message |

### 6.5 CSDAT01Y.cpy — Date/Time Work Area

| # | Field | PIC Clause | Data Type | Business Meaning |
|---|-------|-----------|-----------|-----------------|
| 1 | `WS-CURDATE-YEAR` | `9(04)` | Numeric | Current year |
| 2 | `WS-CURDATE-MONTH` | `9(02)` | Numeric | Current month |
| 3 | `WS-CURDATE-DAY` | `9(02)` | Numeric | Current day |
| 4 | `WS-CURDATE-N` | `9(08)` (REDEFINES) | Numeric | Packed date YYYYMMDD |
| 5 | `WS-CURTIME-HOURS` | `9(02)` | Numeric | Current hour |
| 6 | `WS-CURTIME-MINUTE` | `9(02)` | Numeric | Current minute |
| 7 | `WS-CURTIME-SECOND` | `9(02)` | Numeric | Current second |
| 8 | `WS-CURTIME-MILSEC` | `9(02)` | Numeric | Milliseconds |
| 9 | `WS-CURDATE-MM-DD-YY` | *(group)* | Formatted | MM/DD/YY display format |
| 10 | `WS-CURTIME-HH-MM-SS` | *(group)* | Formatted | HH:MM:SS display format |
| 11 | `WS-TIMESTAMP` | *(group)* | Formatted | YYYY-MM-DD HH:MM:SS.ffffff |

### 6.6 COADM02Y.cpy / COMEN02Y.cpy — Menu Options

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| `CDEMO-ADMIN-OPT-COUNT` / `CDEMO-OPT-COUNT` | `9(02)` | Number of menu items |
| `CDEMO-ADMIN-OPT-NUM` / `CDEMO-OPT-NUM` | `9(02)` | Option number (OCCURS 12) |
| `CDEMO-ADMIN-OPT-NAME` / `CDEMO-OPT-NAME` | `X(35)` | Display name |
| `CDEMO-ADMIN-OPT-PGMNAME` / `CDEMO-OPT-PGMNAME` | `X(08)` | Target COBOL program |
| `CDEMO-ADMIN-OPT-USRTYPE` / `CDEMO-OPT-USRTYPE` | `X(01)` | User type restriction |

---

## 7. Utility Copybooks

### 7.1 CSLKPCDY.cpy — Lookup Code Repository

Contains exhaustive 88-level validation tables for:
- **North America phone area codes** (`VALID-PHONE-AREA-CODE VALUES '201', '202', ...`) — ~370 valid area codes
- **US state codes** (`VALID-US-STATE-CODE VALUES 'AL', 'AK', ...`) — all 50 states + territories
- **US state + first 2 digits of ZIP** — cross-validation of state vs. ZIP prefix

### 7.2 CSSETATY.cpy — Screen Attribute Setting (macro)

Used via `COPY REPLACING` pattern in COACTUPC (31 times). Sets BMS screen field attributes dynamically:
```
WS-ATTR-{field} → DFHBMSCA attribute bytes
```

### 7.3 CSSTRPFY.cpy — Strip/Format Utility

Strips leading/trailing spaces from screen input fields.

### 7.4 CSUTLDWY.cpy / CSUTLDPY.cpy — Utility Working Storage / Procedures

Date conversion and validation utility areas used by COACTUPC.

### 7.5 CODATECN.cpy — Date Conversion Record

| # | Field | PIC Clause | Business Meaning | Validation |
|---|-------|-----------|-----------------|------------|
| 1 | `CODATECN-TYPE` | `X` | Input format type | `88 YYYYMMDD-IN VALUE "1"`, `88 YYYY-MM-DD-IN VALUE "2"` |
| 2 | `CODATECN-INP-DATE` | `X(20)` | Input date string | — |
| 3 | `CODATECN-OUTTYPE` | `X` | Output format type | `88 YYYY-MM-DD-OP VALUE "1"`, `88 YYYYMMDD-OP VALUE "2"` |
| 4 | `CODATECN-0UT-DATE` | `X(20)` | Converted output date | — |

### 7.6 UNUSED1Y.cpy

Empty/placeholder copybook — no fields defined.

---

## 8. DB2 Copybooks (Transaction Type Module)

### 8.1 CSDB2RPY.cpy — DB2 Transaction Type Table Row

*(Maps to DB2 TRANSACTION_TYPE table — columns: TRAN_TYPE_CD CHAR(2), TRAN_TYPE_DESC VARCHAR(50))*

### 8.2 CSDB2RWY.cpy — DB2 Transaction Category Table Row

*(Maps to DB2 TRANSACTION_CATEGORY table — composite key: TRAN_TYPE_CD + TRAN_CAT_CD)*

---

## 9. VSAM File Specifications Summary

| File DD Name | VSAM Type | Record Size | Key Length | Key Offset | Copybook |
|-------------|-----------|------------|-----------|-----------|----------|
| ACCTFILE | KSDS | 300 | 11 | 0 | CVACT01Y |
| CUSTFILE | KSDS | 500 | 9 | 0 | CVCUS01Y |
| CARDFILE | KSDS | 150 | 16 | 0 | CVACT02Y |
| CARDXREF | KSDS | 50 | 16 | 0 | CVACT03Y |
| TRANSACT | KSDS | 350 | 16 | 0 | CVTRA05Y |
| DALYTRAN | Sequential | 350 | — | — | CVTRA06Y |
| TCATBALF | KSDS | 50 | 17 | 0 | CVTRA01Y |
| DISCGRP | KSDS | 50 | 16 | 0 | CVTRA02Y |
| TRANTYPE | KSDS | 60 | 2 | 0 | CVTRA03Y |
| TRANCATG | KSDS | 60 | 6 | 0 | CVTRA04Y |
| USRSEC | KSDS | 80 | 8 | 0 | CSUSR01Y |
| EXPORT | KSDS | 500 | 4 | 28 | CVEXPORT |
