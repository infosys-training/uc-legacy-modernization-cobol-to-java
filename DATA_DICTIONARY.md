# DATA DICTIONARY

## Overview

This document catalogs all copybooks in the CardDemo COBOL estate, extracting field definitions, PIC clauses, data types, inferred business meaning, and validation rules. Fields are grouped by business entity.

---

## 1. ACCOUNT Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | ACCOUNT-RECORD | — | Group | Account master record | — |
| 05 | ACCT-ID | PIC 9(11) | Numeric | Unique account identifier | 11-digit number |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Account status flag | Single character code |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance | Signed, 2 decimal places |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Credit limit for the account | Must be positive |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | Must be positive |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Date account was opened | Date format |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | Date format |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Last card reissue date | Date format |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total | Running total |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total | Running total |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | — |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/interest rate group | Links to DISCGRP |
| 05 | FILLER | PIC X(178) | Filler | Reserved space | — |

---

## 2. CARD Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | CARD-RECORD | — | Group | Credit card master record | — |
| 05 | CARD-NUM | PIC X(16) | Alphanumeric | Credit card number (PAN) | 16 characters |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric | Linked account ID | Must exist in ACCTDAT |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit code |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on card | — |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | Date format |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card active/inactive status | Single character |
| 05 | FILLER | PIC X(59) | Filler | Reserved space | — |

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | CARD-XREF-RECORD | — | Group | Links card to customer and account | — |
| 05 | XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number (key) | Must exist in CARDDAT |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID | Must exist in CUSTDAT |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID | Must exist in ACCTDAT |
| 05 | FILLER | PIC X(14) | Filler | Reserved space | — |

### CVCRD01Y.cpy — Card Work Areas (CICS Screen Control)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | CC-WORK-AREAS | — | Group | Working storage for card screens | — |
| 10 | CCARD-AID | PIC X(5) | Alpha | Attention identifier key pressed | 88-levels: ENTER, CLEAR, PA1, PA2, PFK01-PFK12 |
| 10 | CCARD-NEXT-PROG | PIC X(8) | Alpha | Next program to transfer to | Valid program name |
| 10 | CCARD-NEXT-MAPSET | PIC X(7) | Alpha | Next BMS mapset | Valid mapset name |
| 10 | CCARD-NEXT-MAP | PIC X(7) | Alpha | Next BMS map | Valid map name |
| 10 | CCARD-ERROR-MSG | PIC X(75) | Alpha | Error message for display | — |
| 10 | CCARD-RETURN-MSG | PIC X(75) | Alpha | Return/info message | 88: OFF = LOW-VALUES |
| 10 | CC-ACCT-ID | PIC X(11) | Alpha/Numeric | Account ID work field | REDEFINES as 9(11) |
| 10 | CC-CARD-NUM | PIC X(16) | Alpha/Numeric | Card number work field | REDEFINES as 9(16) |
| 10 | CC-CUST-ID | PIC X(09) | Alpha/Numeric | Customer ID work field | REDEFINES as 9(9) |

---

## 3. CUSTOMER Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | CUSTOMER-RECORD | — | Group | Customer master record | — |
| 05 | CUST-ID | PIC 9(09) | Numeric | Unique customer identifier | 9-digit number |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alpha | Customer first name | — |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alpha | Customer middle name | — |
| 05 | CUST-LAST-NAME | PIC X(25) | Alpha | Customer last name | — |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alpha | Address line 1 | — |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alpha | Address line 2 | — |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alpha | Address line 3 | — |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code | Validated via CSLKPCDY |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code | — |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | Validated via CSLKPCDY |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated via CSLKPCDY |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Area code validated via CSLKPCDY |
| 05 | CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit number |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID | — |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | YYYY-MM-DD format |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account for payments | — |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary cardholder indicator | Y/N |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | 300-850 range |
| 05 | FILLER | PIC X(168) | Filler | Reserved space | — |

### CUSTREC.cpy — Customer Record (Alternate Layout, RECLN 500)

Same structure as CVCUS01Y.cpy with field name `CUST-DOB-YYYYMMDD` instead of `CUST-DOB-YYYY-MM-DD`. Used by statement generation programs.

---

## 4. TRANSACTION Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | TRAN-RECORD | — | Group | Transaction master record | — |
| 05 | TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | System-generated |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Must exist in TRANTYPE |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Must exist in TRANCATG |
| 05 | TRAN-SOURCE | PIC X(10) | Alpha | Source of transaction | — |
| 05 | TRAN-DESC | PIC X(100) | Alpha | Transaction description | — |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | — |
| 05 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | — |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name | — |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | — |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | — |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card used for transaction | Must exist in CARDXREF |
| 05 | TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp | ISO timestamp format |
| 05 | TRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | ISO timestamp format |
| 05 | FILLER | PIC X(20) | Filler | Reserved space | — |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | DALYTRAN-RECORD | — | Group | Daily incoming transaction (pre-posting) | — |
| 05 | DALYTRAN-ID | PIC X(16) | Alphanumeric | Daily transaction ID | — |
| 05 | DALYTRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Must exist in TRANTYPE |
| 05 | DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | — |
| 05 | DALYTRAN-SOURCE | PIC X(10) | Alpha | Transaction source | — |
| 05 | DALYTRAN-DESC | PIC X(100) | Alpha | Description | — |
| 05 | DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | — |
| 05 | DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | — |
| 05 | DALYTRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name | — |
| 05 | DALYTRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | — |
| 05 | DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | — |
| 05 | DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number | — |
| 05 | DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp | — |
| 05 | DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | — |
| 05 | FILLER | PIC X(20) | Filler | Reserved | — |

### CVTRA01Y.cpy — Transaction Category Balance Record (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | Running balance per account/type/category | — |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | — |
| 10 | TRANCAT-TYPE-CD | PIC X(02) | Alpha | Transaction type code | — |
| 10 | TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code | — |
| 05 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Category balance amount | Running total |
| 05 | FILLER | PIC X(22) | Filler | Reserved | — |

### CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | DIS-GROUP-RECORD | — | Group | Interest rate by disclosure group | — |
| 05 | DIS-GROUP-KEY | — | Group | Composite key | — |
| 10 | DIS-ACCT-GROUP-ID | PIC X(10) | Alpha | Account group identifier | Links to ACCT-GROUP-ID |
| 10 | DIS-TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | — |
| 10 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | — |
| 05 | DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate (annual %) | — |
| 05 | FILLER | PIC X(28) | Filler | Reserved | — |

### CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type lookup | — |
| 05 | TRAN-TYPE | PIC X(02) | Alpha | Transaction type code (key) | — |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alpha | Type description | — |
| 05 | FILLER | PIC X(08) | Filler | Reserved | — |

### CVTRA04Y.cpy — Transaction Category Record (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category lookup | — |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRAN-TYPE-CD | PIC X(02) | Alpha | Parent transaction type | — |
| 10 | TRAN-CAT-CD | PIC 9(04) | Numeric | Category code within type | — |
| 05 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alpha | Category description | — |
| 05 | FILLER | PIC X(04) | Filler | Reserved | — |

### CVTRA07Y.cpy — Transaction Report Structures

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | REPORT-NAME-HEADER | — | Group | Report header layout | — |
| 05 | REPT-SHORT-NAME | PIC X(38) | Alpha | Report short name ('DALYREPT') | — |
| 05 | REPT-LONG-NAME | PIC X(41) | Alpha | Report title | — |
| 05 | REPT-START-DATE | PIC X(10) | Alpha | Report period start | Date format |
| 05 | REPT-END-DATE | PIC X(10) | Alpha | Report period end | Date format |
| 01 | TRANSACTION-DETAIL-REPORT | — | Group | Detail line layout | — |
| 05 | TRAN-REPORT-TRANS-ID | PIC X(16) | Alpha | Transaction ID | — |
| 05 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alpha | Account ID | — |
| 05 | TRAN-REPORT-TYPE-CD | PIC X(02) | Alpha | Type code | — |
| 05 | TRAN-REPORT-TYPE-DESC | PIC X(15) | Alpha | Type description | — |
| 05 | TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code | — |
| 05 | TRAN-REPORT-CAT-DESC | PIC X(29) | Alpha | Category description | — |
| 05 | TRAN-REPORT-SOURCE | PIC X(10) | Alpha | Source | — |
| 05 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Transaction amount | — |
| 01 | REPORT-PAGE-TOTALS | — | Group | Page subtotal | — |
| 05 | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page total amount | — |
| 01 | REPORT-ACCOUNT-TOTALS | — | Group | Account subtotal | — |
| 05 | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account total amount | — |
| 01 | REPORT-GRAND-TOTALS | — | Group | Grand total | — |
| 05 | REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total amount | — |

---

## 5. AUTHORIZATION Entity (Sub-Application)

### CIPAUDTY.cpy — Pending Authorization Detail Record

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 05 | PA-AUTHORIZATION-KEY | — | Group | Composite key (date+time) | — |
| 10 | PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (packed) | — |
| 10 | PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (packed) | — |
| 05 | PA-AUTH-ORIG-DATE | PIC X(06) | Alpha | Original date (YYMMDD) | — |
| 05 | PA-AUTH-ORIG-TIME | PIC X(06) | Alpha | Original time (HHMMSS) | — |
| 05 | PA-CARD-NUM | PIC X(16) | Alpha | Card number | Must exist in CARDXREF |
| 05 | PA-AUTH-TYPE | PIC X(04) | Alpha | Authorization type | — |
| 05 | PA-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry (YYMM) | — |
| 05 | PA-MESSAGE-TYPE | PIC X(06) | Alpha | Message type code | — |
| 05 | PA-MESSAGE-SOURCE | PIC X(06) | Alpha | Message source | — |
| 05 | PA-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization ID code | — |
| 05 | PA-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code | 88: '00' = approved |
| 05 | PA-AUTH-RESP-REASON | PIC X(04) | Alpha | Response reason code | — |
| 05 | PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | — |
| 05 | PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Requested amount | — |
| 05 | PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount | — |
| 05 | PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | Merchant category (MCC) | — |
| 05 | PA-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country code | — |
| 05 | PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | Point of sale entry mode | — |
| 05 | PA-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID | — |
| 05 | PA-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name | — |
| 05 | PA-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city | — |
| 05 | PA-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state | — |
| 05 | PA-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP | — |
| 05 | PA-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID | — |
| 05 | PA-MATCH-STATUS | PIC X(01) | Alpha | Transaction match status | 88: P=Pending, D=Declined, E=Expired, M=Matched |
| 05 | PA-AUTH-FRAUD | PIC X(01) | Alpha | Fraud indicator | 88: F=Confirmed, R=Removed |
| 05 | PA-FRAUD-RPT-DATE | PIC X(08) | Alpha | Fraud report date | — |

### CIPAUSMY.cpy — Authorization Summary Record

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 05 | PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID | — |
| 05 | PA-CUST-ID | PIC 9(09) | Numeric | Customer ID | — |
| 05 | PA-AUTH-STATUS | PIC X(01) | Alpha | Overall auth status | — |
| 05 | PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alpha array | Account status codes (5 slots) | — |
| 05 | PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit | — |
| 05 | PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash advance limit | — |
| 05 | PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Current credit balance | — |
| 05 | PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Current cash balance | — |
| 05 | PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of approved auths | — |
| 05 | PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of declined auths | — |
| 05 | PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount | — |
| 05 | PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount | — |

### CCPAURQY.cpy — Authorization Request Message

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 05 | PA-RQ-AUTH-DATE | PIC X(06) | Alpha | Request date | — |
| 05 | PA-RQ-AUTH-TIME | PIC X(06) | Alpha | Request time | — |
| 05 | PA-RQ-CARD-NUM | PIC X(16) | Alpha | Card number | — |
| 05 | PA-RQ-AUTH-TYPE | PIC X(04) | Alpha | Auth type | — |
| 05 | PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry | — |
| 05 | PA-RQ-MESSAGE-TYPE | PIC X(06) | Alpha | Message type | — |
| 05 | PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alpha | Source system | — |
| 05 | PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | — |
| 05 | PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount | — |
| 05 | PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | MCC code | — |
| 05 | PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country | — |
| 05 | PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | — |
| 05 | PA-RQ-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID | — |
| 05 | PA-RQ-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name | — |
| 05 | PA-RQ-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city | — |
| 05 | PA-RQ-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state | — |
| 05 | PA-RQ-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP | — |
| 05 | PA-RQ-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID | — |

### CCPAURLY.cpy — Authorization Reply Message

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 05 | PA-RL-CARD-NUM | PIC X(16) | Alpha | Card number | — |
| 05 | PA-RL-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID | — |
| 05 | PA-RL-AUTH-ID-CODE | PIC X(06) | Alpha | Auth ID code | — |
| 05 | PA-RL-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code | — |
| 05 | PA-RL-AUTH-RESP-REASON | PIC X(04) | Alpha | Response reason | — |
| 05 | PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount | — |

### CCPAUERY.cpy — Error Log Record

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | ERROR-LOG-RECORD | — | Group | Authorization error log entry | — |
| 05 | ERR-DATE | PIC X(06) | Alpha | Error date | — |
| 05 | ERR-TIME | PIC X(06) | Alpha | Error time | — |
| 05 | ERR-APPLICATION | PIC X(08) | Alpha | Application name | — |
| 05 | ERR-PROGRAM | PIC X(08) | Alpha | Program name | — |
| 05 | ERR-LOCATION | PIC X(04) | Alpha | Error location in code | — |
| 05 | ERR-LEVEL | PIC X(01) | Alpha | Severity level | 88: L=Log, I=Info, W=Warning, C=Critical |
| 05 | ERR-SUBSYSTEM | PIC X(01) | Alpha | Subsystem | 88: A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File |
| 05 | ERR-CODE-1 | PIC X(09) | Alpha | Primary error code | — |
| 05 | ERR-CODE-2 | PIC X(09) | Alpha | Secondary error code | — |
| 05 | ERR-MESSAGE | PIC X(50) | Alpha | Error message text | — |
| 05 | ERR-EVENT-KEY | PIC X(20) | Alpha | Event identifier | — |

---

## 6. IMS Database PCB Structures (Sub-Application)

### PAUTBPCB.CPY — Authorization Database PCB

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 05 | PAUT-DBDNAME | PIC X(08) | Alpha | Database name |
| 05 | PAUT-SEG-LEVEL | PIC X(02) | Alpha | Segment level |
| 05 | PAUT-PCB-STATUS | PIC X(02) | Alpha | PCB status code |
| 05 | PAUT-PCB-PROCOPT | PIC X(04) | Alpha | Processing options |
| 05 | PAUT-SEG-NAME | PIC X(08) | Alpha | Segment name |
| 05 | PAUT-KEYFB-NAME | PIC S9(05) COMP | Binary | Key feedback length |
| 05 | PAUT-NUM-SENSEGS | PIC S9(05) COMP | Binary | Number of sensitive segments |

### IMSFUNCS.cpy — IMS Function Codes

| Level | Field Name | PIC Clause | Value | Business Meaning |
|-------|-----------|------------|-------|------------------|
| 05 | FUNC-GU | PIC X(04) | 'GU  ' | Get Unique |
| 05 | FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique |
| 05 | FUNC-GN | PIC X(04) | 'GN  ' | Get Next |
| 05 | FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next |
| 05 | FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent |
| 05 | FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| 05 | FUNC-REPL | PIC X(04) | 'REPL' | Replace |
| 05 | FUNC-ISRT | PIC X(04) | 'ISRT' | Insert |
| 05 | FUNC-DLET | PIC X(04) | 'DLET' | Delete |

---

## 7. COMMON/FRAMEWORK Structures

### COCOM01Y.cpy — Communication Area (COMMAREA)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | CARDDEMO-COMMAREA | — | Group | Inter-program communication area | — |
| 05 | CDEMO-GENERAL-INFO | — | Group | General navigation info | — |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alpha | Source transaction ID | — |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alpha | Source program name | — |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alpha | Target transaction ID | — |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alpha | Target program name | — |
| 10 | CDEMO-USER-ID | PIC X(08) | Alpha | Logged-in user ID | — |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alpha | User type | 88: A=Admin, U=User |
| 10 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program entry context | 88: 0=Enter, 1=Reenter |
| 05 | CDEMO-CUSTOMER-INFO | — | Group | Customer context | — |
| 10 | CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer ID | — |
| 10 | CDEMO-CUST-FNAME | PIC X(25) | Alpha | Customer first name | — |
| 10 | CDEMO-CUST-MNAME | PIC X(25) | Alpha | Customer middle name | — |
| 10 | CDEMO-CUST-LNAME | PIC X(25) | Alpha | Customer last name | — |
| 05 | CDEMO-ACCOUNT-INFO | — | Group | Account context | — |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account ID | — |
| 10 | CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Account status | — |
| 05 | CDEMO-CARD-INFO | — | Group | Card context | — |
| 10 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card number | — |
| 05 | CDEMO-MORE-INFO | — | Group | Additional context | — |
| 10 | CDEMO-LAST-MAP | PIC X(7) | Alpha | Last displayed map | — |
| 10 | CDEMO-LAST-MAPSET | PIC X(7) | Alpha | Last displayed mapset | — |

### CSUSR01Y.cpy — User Security Record

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | SEC-USER-DATA | — | Group | User security record | — |
| 05 | SEC-USR-ID | PIC X(08) | Alpha | User login ID | Unique key |
| 05 | SEC-USR-FNAME | PIC X(20) | Alpha | User first name | — |
| 05 | SEC-USR-LNAME | PIC X(20) | Alpha | User last name | — |
| 05 | SEC-USR-PWD | PIC X(08) | Alpha | User password | — |
| 05 | SEC-USR-TYPE | PIC X(01) | Alpha | User type (A/U) | A=Admin, U=Regular |
| 05 | SEC-USR-FILLER | PIC X(23) | Filler | Reserved | — |

### CSDAT01Y.cpy — Date/Time Working Storage

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | WS-DATE-TIME | — | Group | Date/time work area |
| 10 | WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year |
| 10 | WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| 10 | WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| 10 | WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours |
| 10 | WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minutes |
| 10 | WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current seconds |
| 05 | WS-TIMESTAMP | — | Group | ISO format timestamp |

### CSMSG01Y.cpy — Common Messages

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | CCDA-COMMON-MESSAGES | — | Group | Application messages |
| 05 | CCDA-MSG-THANK-YOU | PIC X(50) | Alpha | Logout message |
| 05 | CCDA-MSG-INVALID-KEY | PIC X(50) | Alpha | Invalid key error |

### CSMSG02Y.cpy — Abend Data

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | ABEND-DATA | — | Group | Abend handling work area |
| 05 | ABEND-CODE | PIC X(4) | Alpha | Abend code |
| 05 | ABEND-CULPRIT | PIC X(8) | Alpha | Program causing abend |
| 05 | ABEND-REASON | PIC X(50) | Alpha | Abend reason description |
| 05 | ABEND-MSG | PIC X(72) | Alpha | Formatted abend message |

### CODATECN.cpy — Date Conversion

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | CODATECN-REC | — | Group | Date conversion I/O | — |
| 05 | CODATECN-IN-REC | — | Group | Input | — |
| 10 | CODATECN-TYPE | PIC X | Alpha | Input format type | 88: "1"=YYYYMMDD, "2"=YYYY-MM-DD |
| 10 | CODATECN-INP-DATE | PIC X(20) | Alpha | Input date string | — |
| 05 | CODATECN-OUT-REC | — | Group | Output | — |
| 10 | CODATECN-OUTTYPE | PIC X | Alpha | Output format type | 88: "1"=YYYY-MM-DD, "2"=YYYYMMDD |
| 10 | CODATECN-0UT-DATE | PIC X(20) | Alpha | Output date string | — |
| 05 | CODATECN-ERROR-MSG | PIC X(38) | Alpha | Error message | — |

### COTTL01Y.cpy — Screen Title

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | CCDA-SCREEN-TITLE | — | Group | Application title block |
| 05 | CCDA-TITLE01 | PIC X(40) | Alpha | 'AWS Mainframe Modernization' |
| 05 | CCDA-TITLE02 | PIC X(40) | Alpha | 'CardDemo' |
| 05 | CCDA-THANK-YOU | PIC X(40) | Alpha | Thank-you message |

### CSLKPCDY.cpy — Lookup/Validation Codes

Contains comprehensive validation tables:
- **North America phone area codes** (~800 valid codes via 88-level conditions)
- **US state codes** (50 states + territories)
- **State + ZIP prefix combinations** (for geographic validation)

### CSSTRPFY.cpy — Store PF Key Routine

Procedural copybook (PROCEDURE DIVISION code). Maps EIBAID to CCARD-AID condition names (PFK01-PFK12, ENTER, CLEAR, PA1, PA2).

### CSSETATY.cpy — Set Field Attributes

Procedural copybook. Template for setting BMS field color to red and marking blank fields with '*' when validation fails.

### CSUTLDPY.cpy — Date Validation Procedures

Procedural copybook containing reusable paragraphs:
- EDIT-DATE-CCYYMMDD — full date validation
- EDIT-YEAR-CCYY — century/year validation
- EDIT-MONTH — month validation (01-12)
- EDIT-DAY — day validation (with leap year)
- EDIT-DATE-OF-BIRTH — DOB-specific rules

---

## 8. MENU CONFIGURATION

### COMEN02Y.cpy — Main Menu Options (Regular Users)

11 menu options mapping to programs: COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C.

### COADM02Y.cpy — Admin Menu Options

6 admin options: COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC.

---

## 9. EXPORT/MIGRATION Structure

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | EXPORT-RECORD | — | Group | Export container record |
| 05 | EXPORT-REC-TYPE | PIC X(1) | Alpha | Record type discriminator |
| 05 | EXPORT-TIMESTAMP | PIC X(26) | Alpha | Export timestamp |
| 05 | EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence counter |
| 05 | EXPORT-BRANCH-ID | PIC X(4) | Alpha | Branch identifier |
| 05 | EXPORT-REGION-CODE | PIC X(5) | Alpha | Region code |
| 05 | EXPORT-RECORD-DATA | PIC X(460) | Alpha | Payload (REDEFINES per type) |

Record types via REDEFINES: Customer, Account, Transaction, Card Cross-Reference, Card.

---

## 10. DB2 Copybooks (Transaction Type Sub-Application)

### CSDB2RPY.cpy — DB2 Common Procedures

Procedural copybook containing:
- `9998-PRIMING-QUERY` — DB2 connectivity verification (SELECT 1 FROM SYSIBM.SYSDUMMY1)
- `9999-FORMAT-DB2-MESSAGE` — Error message formatting via DSNTIAC utility

### CSDB2RWY.cpy — DB2 Working Storage

Working storage for DB2 operations including SQLCA, formatted message areas, and status flags.

---

## 11. UNUSED

### UNUSED1Y.cpy — Placeholder

Identical structure to CSUSR01Y but with UNUSED- prefix. Likely deprecated or reserved for future use.
