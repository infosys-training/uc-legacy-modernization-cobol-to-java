# DATA DICTIONARY — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System  
> **Repo:** `infosys-training/uc-legacy-modernization-cobol-to-java`  
> **Generated:** 2026-06-16  
> **Total Copybooks Cataloged:** 41

---

## Entity Relationship Overview

```
Customer (CVCUS01Y) ──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
                                    │
                              Card (CVACT02Y)
                                    │
                         Transaction (CVTRA05Y) ──► TranType (CVTRA03Y)
                                                    ──► TranCategory (CVTRA04Y)
                                                    ──► TranCatBalance (CVTRA01Y)
                                                         ──► DisclosureGroup (CVTRA02Y)
```

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Master Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| ACCT-ID | PIC 9(11) | Numeric (unsigned) | 11 | Account identifier — primary key | Must be numeric; VSAM KSDS primary key |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Account active flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | 12 | Current account balance | Signed; V99 = 2 implied decimal places |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 12 | Maximum credit limit | Must be positive |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 12 | Cash advance limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | 10 | Account open date | Format: YYYY-MM-DD; validated by CSUTLDTC |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Account expiration date | Format: YYYY-MM-DD; must be > OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | 10 | Last card reissue date | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | 12 | Current cycle credits total | Running total of credits this billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | 12 | Current cycle debits total | Running total of debits this billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | Account holder ZIP code | Validated against CSLKPCDY ZIP prefixes |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Disclosure/interest rate group | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| FILLER | PIC X(178) | Padding | 178 | Reserved/padding to 300 bytes | — |

### 1.2 CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number — primary key | 16-digit card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric | 11 | Parent account ID — FK to CVACT01Y | Must reference valid ACCT-ID |
| CARD-CVV-CD | PIC 9(03) | Numeric | 3 | Card verification value | 3-digit CVV code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | 50 | Name printed on card | — |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Card active flag | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Padding | 59 | Reserved to 150 bytes | — |

### 1.3 CVACT03Y.cpy — Card-Account Cross-Reference (RECLN 50)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number — primary key | Links to CARD-NUM in CVACT02Y |
| XREF-CUST-ID | PIC 9(09) | Numeric | 9 | Customer ID — FK to CVCUS01Y | Must reference valid CUST-ID |
| XREF-ACCT-ID | PIC 9(11) | Numeric | 11 | Account ID — FK to CVACT01Y | Must reference valid ACCT-ID |
| FILLER | PIC X(14) | Padding | 14 | Reserved to 50 bytes | — |

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Master Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| CUST-ID | PIC 9(09) | Numeric | 9 | Customer identifier — primary key | VSAM KSDS primary key |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | 25 | Customer first name | — |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | 25 | Customer middle name | — |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | 25 | Customer last name | — |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | 50 | Address line 1 | — |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | 50 | Address line 2 | — |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | 50 | Address line 3 | — |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | 2 | US state code | Validated against 50 US states in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | 3 | Country code | — |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | ZIP/postal code | Validated against state+ZIP prefix in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | 15 | Primary phone | Area code validated via NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | 15 | Secondary phone | Area code validated via NANPA list |
| CUST-SSN | PIC 9(09) | Numeric | 9 | Social Security Number | 9-digit numeric; validated in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | 20 | Government-issued ID | — |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | 10 | Date of birth | Format: YYYY-MM-DD; validated by CSUTLDTC |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | 10 | EFT/bank account ID | For electronic fund transfer |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | 1 | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | 3 | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | Padding | 168 | Reserved to 500 bytes | — |

### 2.2 CUSTREC.cpy — Alternate Customer Record (RECLN 500)

Identical layout to CVCUS01Y with minor naming difference: `CUST-DOB-YYYYMMDD` instead of `CUST-DOB-YYYY-MM-DD`. Used by legacy batch programs.

---

## 3. Transaction Entity

### 3.1 CVTRA05Y.cpy — Transaction Master Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| TRAN-ID | PIC X(16) | Alphanumeric | 16 | Transaction ID — primary key | VSAM KSDS primary key |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | FK to TRAN-TYPE-RECORD (CVTRA03Y) |
| TRAN-CAT-CD | PIC 9(04) | Numeric | 4 | Transaction category code | FK to TRAN-CAT-RECORD (CVTRA04Y) |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Transaction source system | e.g., 'POS', 'ATM', 'ONLINE' |
| TRAN-DESC | PIC X(100) | Alphanumeric | 100 | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | 11 | Transaction amount | Signed; positive = debit, negative = credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | 9 | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card used for transaction | FK to CARD-NUM (CVACT02Y) |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | 26 | Original timestamp | Format: YYYY-MM-DD-HH.MM.SS.MMMMMM |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | 26 | Processing timestamp | Set when posted by CBTRN02C |
| FILLER | PIC X(20) | Padding | 20 | Reserved to 350 bytes | — |

### 3.2 CVTRA06Y.cpy — Daily Transaction Input Record (RECLN 350)

Same layout as CVTRA05Y with `DALYTRAN-` prefix. Input file for daily batch processing. All fields map 1:1 to CVTRA05Y after validation.

### 3.3 CVTRA03Y.cpy — Transaction Type Reference (RECLN 60)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | 2 | Transaction type code — primary key | e.g., 'SA' (sale), 'RT' (return) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Type description | Human-readable type name |
| FILLER | PIC X(08) | Padding | 8 | Reserved to 60 bytes | — |

### 3.4 CVTRA04Y.cpy — Transaction Category Type (RECLN 60)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| TRAN-CAT-KEY | (Group) | Composite key | 6 | — | — |
| — TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | FK to CVTRA03Y |
| — TRAN-CAT-CD | PIC 9(04) | Numeric | 4 | Category code within type | — |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Category description | — |
| FILLER | PIC X(04) | Padding | 4 | Reserved to 60 bytes | — |

### 3.5 CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| TRAN-CAT-KEY | (Group) | Composite key | 17 | — | — |
| — TRANCAT-ACCT-ID | PIC 9(11) | Numeric | 11 | Account ID | FK to CVACT01Y |
| — TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type | FK to CVTRA03Y |
| — TRANCAT-CD | PIC 9(04) | Numeric | 4 | Category code | FK to CVTRA04Y |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | 11 | Running balance for this category | Updated by CBTRN02C (posting) |
| FILLER | PIC X(22) | Padding | 22 | Reserved to 50 bytes | — |

### 3.6 CVTRA02Y.cpy — Disclosure Group / Interest Rate (RECLN 50)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| DIS-GROUP-KEY | (Group) | Composite key | 16 | — | — |
| — DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Account group ID | Links to ACCT-GROUP-ID (CVACT01Y) |
| — DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type | — |
| — DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | 4 | Transaction category | — |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | 6 | Interest rate (annual %) | Percentage; used by CBACT04C |
| FILLER | PIC X(28) | Padding | 28 | Reserved to 50 bytes | — |

### 3.7 CVTRA07Y.cpy — Transaction Report Layout (print record)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|------------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | 38 | Report identifier ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | 41 | 'Daily Transaction Report' |
| REPT-START-DATE / REPT-END-DATE | PIC X(10) | Alphanumeric | 10 | Report date range |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | 16 | Transaction ID |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | 11 | Account ID |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | 15 | Type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | 4 | Category code |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | 29 | Category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | 10 | Transaction source |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | 16 | Formatted amount |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | 16 | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | 16 | Account subtotal |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | 16 | Grand total |

---

## 4. Authorization Entity (IMS)

### 4.1 CIPAUSMY.cpy — Pending Authorization Summary (IMS Segment)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | 6 | Account ID | IMS segment search key |
| PA-CUST-ID | PIC 9(09) | Numeric | 9 | Customer ID | — |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric | 1 | Authorization status | — |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alphanumeric array | 10 | 5 account status values | — |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | 6 | Credit limit snapshot | — |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | 6 | Cash limit snapshot | — |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | 6 | Credit balance snapshot | — |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | 6 | Cash balance snapshot | — |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | 2 | Approved auth count | — |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | 2 | Declined auth count | — |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | 6 | Total approved amount | — |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | 6 | Total declined amount | — |
| FILLER | PIC X(34) | Padding | 34 | Reserved | — |

### 4.2 CIPAUDTY.cpy — Pending Authorization Detail (IMS Segment)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| PA-AUTHORIZATION-KEY | (Group) | Composite key | — | — | — |
| — PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | 3 | Auth date (compressed) | IMS segment sequence key |
| — PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | 5 | Auth time (compressed) | — |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | 6 | Original auth date | Format: YYMMDD |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | 6 | Original auth time | Format: HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number | — |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | 4 | Authorization type | — |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | 4 | Card expiry | Format: YYMM |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | 6 | ISO 8583 message type | — |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | 6 | Message source system | — |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | 6 | Authorization ID code | — |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | 2 | Response code | 88 PA-AUTH-APPROVED = '00' |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | 4 | Response reason code | — |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | 6 | ISO processing code | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | 7 | Requested amount | — |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | 7 | Approved amount | May differ from requested |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | 4 | MCC code | — |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | 3 | Acquirer country | — |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | 2 | POS entry mode | — |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric | 15 | Merchant ID | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | 22 | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | 13 | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | 2 | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | 9 | Merchant ZIP | — |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | 15 | Transaction ID | — |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric | 1 | Match status | 88: 'P'=Pending, 'D'=Declined, 'E'=Expired, 'M'=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | 1 | Fraud flag | 88: 'F'=Confirmed, 'R'=Removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric | 8 | Fraud report date | Format: YYYYMMDD |
| FILLER | PIC X(17) | Padding | 17 | Reserved | — |

### 4.3 CCPAURQY.cpy — Authorization MQ Request Message

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|------------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | 6 | Request date (YYMMDD) |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | 6 | Request time (HHMMSS) |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | 4 | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | 4 | Card expiry |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | 6 | ISO message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | 6 | Source system |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | 6 | ISO processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | 14 | Requested amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | 4 | MCC code |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | 3 | Acquirer country |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | 2 | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | 15 | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | 22 | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | 13 | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | 2 | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | 9 | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | 15 | Transaction ID |

### 4.4 CCPAURLY.cpy — Authorization MQ Response Message

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|------------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number (echo) |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | 15 | Transaction ID (echo) |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | 6 | Authorization ID code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | 2 | Response code ('00'=approved) |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | 4 | Response reason |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | 14 | Approved amount |

### 4.5 CCPAUERY.cpy — Authorization Error Log Record

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| ERR-DATE | PIC X(06) | Alphanumeric | 6 | Error date | YYMMDD |
| ERR-TIME | PIC X(06) | Alphanumeric | 6 | Error time | HHMMSS |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | 8 | Application name | — |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | 8 | Program name | — |
| ERR-LOCATION | PIC X(04) | Alphanumeric | 4 | Error location code | — |
| ERR-LEVEL | PIC X(01) | Alphanumeric | 1 | Severity | 88: 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | 1 | Subsystem source | 88: 'A'=App, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=File |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | 9 | Primary error code | — |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | 9 | Secondary error code | — |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | 50 | Error message text | — |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | 20 | Correlating event key | — |

---

## 5. User Security Entity

### 5.1 CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation |
|------------|-----------|-----------|-------|------------------|------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | 8 | User ID — primary key | VSAM KSDS key; e.g., 'ADMIN001' |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | 20 | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | 20 | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | 8 | Password (plain text) | **SECURITY RISK: stored unencrypted** |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | 1 | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Padding | 23 | Reserved to 80 bytes | — |

---

## 6. Application Framework Copybooks

### 6.1 COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | 4 | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | 8 | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | 4 | Target CICS transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | 8 | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | 8 | Current user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 1 | 88: 'A'=Admin, 'U'=User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 1 | 88: 0=Enter, 1=Re-enter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | 9 | Customer ID in context |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alphanumeric | 75 | Customer name in context |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | 11 | Account ID in context |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | 1 | Account status in context |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | 16 | Card number in context |
| CDEMO-LAST-MAP | PIC X(07) | Alphanumeric | 7 | Last displayed BMS map |
| CDEMO-LAST-MAPSET | PIC X(07) | Alphanumeric | 7 | Last used BMS mapset |

### 6.2 CVCRD01Y.cpy — CICS Screen Work Areas

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|------------------|
| CCARD-AID | PIC X(5) | Alphanumeric | 5 | 88-level AID key (ENTER, CLEAR, PFK01-12) |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | 8 | Next program for XCTL |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | 7 | Next BMS mapset |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | 7 | Next BMS map |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | 75 | Error message for display |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | 75 | Return/info message |
| CC-ACCT-ID | PIC X(11) / 9(11) | Redefines | 11 | Account filter |
| CC-CARD-NUM | PIC X(16) / 9(16) | Redefines | 16 | Card filter |
| CC-CUST-ID | PIC X(09) / 9(9) | Redefines | 9 | Customer filter |

### 6.3 COTTL01Y.cpy — Screen Title Constants

| Field Name | PIC Clause | Value | Business Meaning |
|------------|-----------|-------|------------------|
| CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' | Screen header line 1 |
| CCDA-TITLE02 | PIC X(40) | 'CardDemo' | Screen header line 2 |
| CCDA-THANK-YOU | PIC X(40) | 'Thank you for using CCDA...' | Sign-off message |

### 6.4 CSDAT01Y.cpy — Date/Time Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year (YYYY) |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month (MM) |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day (DD) |
| WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours (HH) |
| WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Minutes (MM) |
| WS-CURTIME-SECOND | PIC 9(02) | Numeric | Seconds (SS) |
| WS-CURDATE-MM-DD-YY | (Group) | Formatted | Display date: MM/DD/YY |
| WS-CURTIME-HH-MM-SS | (Group) | Formatted | Display time: HH:MM:SS |
| WS-TIMESTAMP | (Group) | Formatted | ISO timestamp: YYYY-MM-DD HH:MM:SS.MMMMMM |

### 6.5 CODATECN.cpy — Date Conversion Utility

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| CODATECN-TYPE | PIC X | Alphanumeric | 88: '1'=YYYYMMDD input, '2'=YYYY-MM-DD input |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date string |
| CODATECN-OUTTYPE | PIC X | Alphanumeric | 88: '1'=YYYY-MM-DD output, '2'=YYYYMMDD output |
| CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Converted output date |
| CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Error message if conversion fails |

### 6.6 CSMSG01Y.cpy — Common Messages

| Field Name | PIC Clause | Value | Business Meaning |
|------------|-----------|-------|------------------|
| CCDA-MSG-THANK-YOU | PIC X(50) | 'Thank you for using CardDemo application...' | Sign-off message |
| CCDA-MSG-INVALID-KEY | PIC X(50) | 'Invalid key pressed. Please see below...' | PF key error |

### 6.7 CSMSG02Y.cpy — Abend Data

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| ABEND-CODE | PIC X(04) | Alphanumeric | Abend code |
| ABEND-CULPRIT | PIC X(08) | Alphanumeric | Program causing abend |
| ABEND-REASON | PIC X(50) | Alphanumeric | Abend reason text |
| ABEND-MSG | PIC X(72) | Alphanumeric | Formatted abend message |

---

## 7. Menu Definition Copybooks

### 7.1 COMEN02Y.cpy — Main Menu Options (11 entries)

| Option # | Label | Target Program | Access |
|----------|-------|----------------|--------|
| 1 | Account View | COACTVWC | User |
| 2 | Account Update | COACTUPC | User |
| 3 | Credit Card List | COCRDLIC | User |
| 4 | Credit Card View | COCRDSLC | User |
| 5 | Credit Card Update | COCRDUPC | User |
| 6 | Transaction List | COTRN00C | User |
| 7 | Transaction View | COTRN01C | User |
| 8 | Transaction Add | COTRN02C | User |
| 9 | Transaction Reports | CORPT00C | User |
| 10 | Bill Payment | COBIL00C | User |
| 11 | Pending Authorization View | COPAUS0C | User |

### 7.2 COADM02Y.cpy — Admin Menu Options (6 entries)

| Option # | Label | Target Program |
|----------|-------|----------------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |
| 5 | Transaction Type List/Update (DB2) | COTRTLIC |
| 6 | Transaction Type Maintenance (DB2) | COTRTUPC |

---

## 8. Export/Migration Copybook

### 8.1 CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

**Header fields (common to all record types):**

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|------------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | 1 | Record type: 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=XREF, 'D'=Card |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | 26 | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | 4 | Sequence number |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | 4 | Source branch ID |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | 5 | Region code |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | 460 | Entity-specific payload (REDEFINES) |

**Payload structures use REDEFINES** for Customer, Account, Transaction, Card-XREF, and Card data — mirrors the primary entity copybooks with COMP-3 storage optimization for numeric fields.

---

## 9. DB2-Specific Copybooks

### 9.1 CSDB2RWY.cpy — DB2 Common Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| WS-DISP-SQLCODE | PIC ----9 | Edited numeric | Display-formatted SQLCODE |
| WS-DUMMY-DB2-INT | PIC S9(4) COMP-3 | Packed decimal | DB2 connectivity test value |
| WS-DB2-PROCESSING-FLAG | PIC X(1) | Alphanumeric | 88: '0'=OK, '1'=Error |
| WS-DB2-CURRENT-ACTION | PIC X(72) | Alphanumeric | Current DB2 action description |
| WS-DSNTIAC-FORMATTED | (Group) | — | DSNTIAC formatted error message (10 × 72-char lines) |

### 9.2 CSDB2RPY.cpy — DB2 Common Procedures (COPY in PROCEDURE DIVISION)

Contains reusable paragraphs: `9998-PRIMING-QUERY` (connectivity test via `SELECT 1 FROM SYSIBM.SYSDUMMY1`) and `9999-FORMAT-DB2-MESSAGE` (error formatting via DSNTIAC utility).

---

## 10. IMS-Specific Copybooks

### 10.1 IMSFUNCS.cpy — IMS DL/I Function Code Constants

| Field Name | PIC Clause | Value | DL/I Function |
|------------|-----------|-------|---------------|
| FUNC-GU | PIC X(04) | 'GU  ' | Get Unique (direct retrieval) |
| FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique (for update) |
| FUNC-GN | PIC X(04) | 'GN  ' | Get Next (sequential) |
| FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next (for update) |
| FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | PIC X(04) | 'REPL' | Replace (update) |
| FUNC-ISRT | PIC X(04) | 'ISRT' | Insert |
| FUNC-DLET | PIC X(04) | 'DLET' | Delete |
| PARMCOUNT | PIC S9(05) COMP-5 | +4 | Standard DL/I parameter count |

---

## 11. Validation Lookup Copybooks

### 11.1 CSLKPCDY.cpy — US Validation Reference Data (1,318 lines)

Contains 88-level condition-name tables for:
- **Phone area codes** — Full NANPA (North American Numbering Plan) area code list (~400 valid codes)
- **US state codes** — All 50 states + DC + territories
- **ZIP code prefixes** — State-to-ZIP-prefix mapping for cross-validation

Used exclusively by COACTUPC.cbl for exhaustive address/phone validation.

### 11.2 CSUTLDWY.cpy — Date Validation Working Storage (89 lines)

Working storage fields for the CSUTLDTC date validation utility program. Contains date component variables and LE CEEDAYS interface parameters.

### 11.3 CSUTLDPY.cpy — Date Validation Procedures (375 lines)

COPY'd into PROCEDURE DIVISION. Contains date validation logic using LE intrinsic functions. Used by COACTUPC and COCRDUPC for date field validation.

### 11.4 CSSETATY.cpy — Screen Attribute Macros (30 lines)

Used via `COPY CSSETATY REPLACING` to set BMS field attributes (ASKIP, PROT, UNPROT, BRT, NORM, DRK). COACTUPC uses this 3 times with different field prefixes.

---

## 12. Unused/Legacy Copybooks

### 12.1 UNUSED1Y.cpy — Deprecated User Record

Identical structure to CSUSR01Y with generic `UNUSED-` prefix. Not referenced by any active program.
