# DATA DICTIONARY — CardDemo COBOL Estate

> Comprehensive catalog of all copybook data structures, grouped by business entity.

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Master Record

The primary account record stored in `ACCTDATA.VSAM.KSDS`. Referenced by 16 programs.

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | ACCOUNT-RECORD | *(group)* | Group | Account master record | — |
| 05 | ACCT-ID | 9(11) | Numeric | Unique account identifier (11 digits) | Primary key; must be non-zero |
| 05 | ACCT-ACTIVE-STATUS | X(01) | Alpha | Account status flag | 'Y' = active, 'N' = inactive |
| 05 | ACCT-CURR-BAL | S9(10)V99 | Signed decimal | Current account balance | Signed; can be negative (overpayment) |
| 05 | ACCT-CREDIT-LIMIT | S9(10)V99 | Signed decimal | Credit limit for purchase transactions | Must be > 0 for active accounts |
| 05 | ACCT-CASH-CREDIT-LIMIT | S9(10)V99 | Signed decimal | Cash advance credit limit | Typically ≤ ACCT-CREDIT-LIMIT |
| 05 | ACCT-OPEN-DATE | X(10) | Date string | Account opening date | Format: YYYY-MM-DD |
| 05 | ACCT-EXPIRAION-DATE | X(10) | Date string | Account expiration date | Format: YYYY-MM-DD; must be > open date |
| 05 | ACCT-REISSUE-DATE | X(10) | Date string | Card reissue date | Format: YYYY-MM-DD |
| 05 | ACCT-CURR-CYC-CREDIT | S9(10)V99 | Signed decimal | Current cycle credit (payments received) | Running total for billing cycle |
| 05 | ACCT-CURR-CYC-DEBIT | S9(10)V99 | Signed decimal | Current cycle debit (charges) | Running total for billing cycle |
| 05 | ACCT-ADDR-ZIP | X(10) | Alpha | Account holder ZIP code | Used for disclosure group lookup |
| 05 | ACCT-GROUP-ID | X(10) | Alpha | Disclosure/interest rate group identifier | Links to DISCGRP for rate lookup |
| 05 | FILLER | X(178) | Alpha | Reserved space | Padding for future use |

**Record length:** 300 bytes

### 1.2 CVACT02Y.cpy — Card Record

Credit card record stored in `CARDDATA.VSAM.KSDS`. Referenced by 10 programs.

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | CARD-RECORD | *(group)* | Group | Card master record | — |
| 05 | CARD-NUM | X(16) | Alpha | Credit card number (16 digits) | Primary key; Luhn-checkable |
| 05 | CARD-ACCT-ID | 9(11) | Numeric | Associated account ID | FK to ACCOUNT-RECORD.ACCT-ID |
| 05 | CARD-CVV-CD | 9(03) | Numeric | Card verification value | 3-digit CVV code |
| 05 | CARD-EMBOSSED-NAME | X(50) | Alpha | Name printed on card | Cardholder name |
| 05 | CARD-EXPIRAION-DATE | X(10) | Date string | Card expiration date | Format: YYYY-MM-DD |
| 05 | CARD-ACTIVE-STATUS | X(01) | Alpha | Card active status | 'Y' = active, 'N' = inactive |
| 05 | FILLER | X(59) | Alpha | Reserved space | Padding |

**Record length:** 150 bytes

### 1.3 CVACT03Y.cpy — Card Cross-Reference Record

Links cards to customers and accounts. Stored in `CARDXREF.VSAM.KSDS`. Referenced by 16 programs.

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | CARD-XREF-RECORD | *(group)* | Group | Cross-reference record | — |
| 05 | XREF-CARD-NUM | X(16) | Alpha | Card number | FK to CARD-RECORD.CARD-NUM |
| 05 | XREF-CUST-ID | 9(09) | Numeric | Customer ID | FK to CUSTOMER-RECORD.CUST-ID |
| 05 | XREF-ACCT-ID | 9(11) | Numeric | Account ID | FK to ACCOUNT-RECORD.ACCT-ID |
| 05 | FILLER | X(14) | Alpha | Reserved | Padding |

**Record length:** 50 bytes

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Master Record

Customer record stored in `CUSTDATA.VSAM.KSDS`. Referenced by 10 programs.

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | CUSTOMER-RECORD | *(group)* | Group | Customer master record | — |
| 05 | CUST-ID | 9(09) | Numeric | Unique customer identifier | Primary key; 9 digits |
| 05 | CUST-FIRST-NAME | X(25) | Alpha | Customer first name | Required |
| 05 | CUST-MIDDLE-NAME | X(25) | Alpha | Customer middle name | Optional |
| 05 | CUST-LAST-NAME | X(25) | Alpha | Customer last name | Required |
| 05 | CUST-ADDR-LINE-1 | X(50) | Alpha | Address line 1 | Required |
| 05 | CUST-ADDR-LINE-2 | X(50) | Alpha | Address line 2 | Optional |
| 05 | CUST-ADDR-LINE-3 | X(50) | Alpha | Address line 3 | Optional |
| 05 | CUST-ADDR-STATE-CD | X(02) | Alpha | US state code | Validated against CSLKPCDY lookup |
| 05 | CUST-ADDR-COUNTRY-CD | X(03) | Alpha | Country code | 3-letter ISO code |
| 05 | CUST-ADDR-ZIP | X(10) | Alpha | ZIP/postal code | Validated against state via CSLKPCDY |
| 05 | CUST-PHONE-NUM-1 | X(15) | Alpha | Primary phone number | Area code validated via CSLKPCDY |
| 05 | CUST-PHONE-NUM-2 | X(15) | Alpha | Secondary phone number | Optional |
| 05 | CUST-SSN | 9(09) | Numeric | Social Security Number | 9 digits; PII |
| 05 | CUST-GOVT-ISSUED-ID | X(20) | Alpha | Government-issued ID | Driver license or passport |
| 05 | CUST-DOB-YYYY-MM-DD | X(10) | Date string | Date of birth | Format: YYYY-MM-DD |
| 05 | CUST-EFT-ACCOUNT-ID | X(10) | Alpha | EFT (electronic funds transfer) account ID | For automated payments |
| 05 | CUST-PRI-CARD-HOLDER-IND | X(01) | Alpha | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| 05 | CUST-FICO-CREDIT-SCORE | 9(03) | Numeric | FICO credit score | Range: 300–850 |
| 05 | FILLER | X(168) | Alpha | Reserved | Padding |

**Record length:** 500 bytes

### 2.2 CUSTREC.cpy — Customer Record (Alternate Layout)

Identical structure to CVCUS01Y but used by CBSTM03A for statement generation.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | CUSTOMER-RECORD | *(group)* | Group | Same as CVCUS01Y |
| 05 | CUST-ID | 9(09) | Numeric | Customer ID |
| 05 | CUST-FIRST-NAME | X(25) | Alpha | First name |
| 05 | CUST-MIDDLE-NAME | X(25) | Alpha | Middle name |
| 05 | CUST-LAST-NAME | X(25) | Alpha | Last name |
| 05 | CUST-ADDR-LINE-1..3 | X(50) each | Alpha | Address lines |
| 05 | CUST-ADDR-STATE-CD | X(02) | Alpha | State code |
| 05 | CUST-ADDR-COUNTRY-CD | X(03) | Alpha | Country code |
| 05 | CUST-ADDR-ZIP | X(10) | Alpha | ZIP code |
| 05 | CUST-PHONE-NUM-1..2 | X(15) each | Alpha | Phone numbers |
| 05 | CUST-SSN | 9(09) | Numeric | SSN |
| 05 | CUST-GOVT-ISSUED-ID | X(20) | Alpha | Government ID |
| 05 | CUST-DOB-YYYYMMDD | X(10) | Date string | Date of birth |
| 05 | CUST-EFT-ACCOUNT-ID | X(10) | Alpha | EFT account |
| 05 | CUST-PRI-CARD-HOLDER-IND | X(01) | Alpha | Primary cardholder |
| 05 | CUST-FICO-CREDIT-SCORE | 9(03) | Numeric | Credit score |

**Record length:** 500 bytes

---

## 3. Transaction Entity

### 3.1 CVTRA05Y.cpy — Transaction Master Record

Primary transaction record in `TRANSACT.VSAM.KSDS`. Referenced by 11 programs.

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | TRAN-RECORD | *(group)* | Group | Transaction record | — |
| 05 | TRAN-ID | X(16) | Alpha | Unique transaction identifier | System-generated; primary key component |
| 05 | TRAN-TYPE-CD | X(02) | Alpha | Transaction type code | FK to TRAN-TYPE-RECORD.TRAN-TYPE |
| 05 | TRAN-CAT-CD | 9(04) | Numeric | Transaction category code | FK to TRAN-CAT-RECORD |
| 05 | TRAN-SOURCE | X(10) | Alpha | Transaction source | e.g., 'POS', 'ATM', 'ONLINE' |
| 05 | TRAN-DESC | X(100) | Alpha | Transaction description | Free-text description |
| 05 | TRAN-AMT | S9(09)V99 | Signed decimal | Transaction amount | Positive = charge, negative = credit |
| 05 | TRAN-MERCHANT-ID | 9(09) | Numeric | Merchant identifier | Links to merchant master |
| 05 | TRAN-MERCHANT-NAME | X(50) | Alpha | Merchant name | Display name |
| 05 | TRAN-MERCHANT-CITY | X(50) | Alpha | Merchant city | Location |
| 05 | TRAN-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP code | Location |
| 05 | TRAN-CARD-NUM | X(16) | Alpha | Card number used | FK to CARD-RECORD.CARD-NUM |
| 05 | TRAN-ORIG-TS | X(26) | Timestamp | Transaction origination timestamp | ISO timestamp format |
| 05 | TRAN-PROC-TS | X(26) | Timestamp | Transaction processing timestamp | Set during batch posting |
| 05 | FILLER | X(20) | Alpha | Reserved | Padding |

**Record length:** 350 bytes (key = TRAN-CARD-NUM + TRAN-ID)

### 3.2 CVTRA06Y.cpy — Daily Transaction Record

Daily transaction input record from `DALYTRAN.PS`. Same structure as CVTRA05Y with DALYTRAN prefix.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | DALYTRAN-RECORD | *(group)* | Group | Daily transaction input record |
| 05 | DALYTRAN-ID | X(16) | Alpha | Transaction ID |
| 05 | DALYTRAN-TYPE-CD | X(02) | Alpha | Transaction type code |
| 05 | DALYTRAN-CAT-CD | 9(04) | Numeric | Transaction category code |
| 05 | DALYTRAN-SOURCE | X(10) | Alpha | Source identifier |
| 05 | DALYTRAN-DESC | X(100) | Alpha | Description |
| 05 | DALYTRAN-AMT | S9(09)V99 | Signed decimal | Amount |
| 05 | DALYTRAN-MERCHANT-ID | 9(09) | Numeric | Merchant ID |
| 05 | DALYTRAN-MERCHANT-NAME | X(50) | Alpha | Merchant name |
| 05 | DALYTRAN-MERCHANT-CITY | X(50) | Alpha | Merchant city |
| 05 | DALYTRAN-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP |
| 05 | DALYTRAN-CARD-NUM | X(16) | Alpha | Card number |
| 05 | DALYTRAN-ORIG-TS | X(26) | Timestamp | Original timestamp |
| 05 | DALYTRAN-PROC-TS | X(26) | Timestamp | Processing timestamp |

**Record length:** 350 bytes

### 3.3 CVTRA01Y.cpy — Transaction Category Balance Record

Running balance per account/type/category in `TCATBALF.VSAM.KSDS`.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | TRAN-CAT-BAL-RECORD | *(group)* | Group | Category balance record |
| 05 | TRAN-CAT-KEY | *(group)* | Group | Composite key |
| 10 | TRANCAT-ACCT-ID | 9(11) | Numeric | Account ID |
| 10 | TRANCAT-TYPE-CD | X(02) | Alpha | Transaction type code |
| 10 | TRANCAT-CD | 9(04) | Numeric | Category code |
| 05 | TRAN-CAT-BAL | S9(09)V99 | Signed decimal | Running balance for this category |

**Record length:** 50 bytes

### 3.4 CVTRA02Y.cpy — Disclosure Group Record

Interest rate disclosure groups in `DISCGRP.VSAM.KSDS`.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | DIS-GROUP-RECORD | *(group)* | Group | Disclosure group record |
| 05 | DIS-GROUP-KEY | *(group)* | Group | Composite key |
| 10 | DIS-ACCT-GROUP-ID | X(10) | Alpha | Account group identifier |
| 10 | DIS-TRAN-TYPE-CD | X(02) | Alpha | Transaction type |
| 10 | DIS-TRAN-CAT-CD | 9(04) | Numeric | Transaction category |
| 05 | DIS-INT-RATE | S9(04)V99 | Signed decimal | Interest rate (%) |

**Record length:** 50 bytes

### 3.5 CVTRA03Y.cpy — Transaction Type Record

Transaction type lookup in `TRANTYPE.VSAM.KSDS`.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | TRAN-TYPE-RECORD | *(group)* | Group | Type lookup record |
| 05 | TRAN-TYPE | X(02) | Alpha | Type code (primary key) |
| 05 | TRAN-TYPE-DESC | X(50) | Alpha | Type description |

**Record length:** 60 bytes

### 3.6 CVTRA04Y.cpy — Transaction Category Record

Transaction category lookup in `TRANCATG.VSAM.KSDS`.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | TRAN-CAT-RECORD | *(group)* | Group | Category lookup record |
| 05 | TRAN-CAT-KEY | *(group)* | Group | Composite key |
| 10 | TRAN-TYPE-CD | X(02) | Alpha | Transaction type code |
| 10 | TRAN-CAT-CD | 9(04) | Numeric | Category code |
| 05 | TRAN-CAT-TYPE-DESC | X(50) | Alpha | Category description |

**Record length:** 60 bytes

### 3.7 CVTRA07Y.cpy — Transaction Report Layout

Print layout for transaction detail reports generated by CBTRN03C.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | REPORT-NAME-HEADER | *(group)* | Group | Report header |
| 05 | REPT-SHORT-NAME | X(38) | Alpha | Report short name |
| 05 | REPT-LONG-NAME | X(41) | Alpha | Report full name |
| 05 | REPT-DATE-HEADER | X(12) | Alpha | Date range header |
| 05 | REPT-START-DATE | X(10) | Date | Report start date |
| 05 | REPT-END-DATE | X(10) | Date | Report end date |
| 01 | TRANSACTION-DETAIL-REPORT | *(group)* | Group | Detail line |
| 05 | TRAN-REPORT-TRANS-ID | X(16) | Alpha | Transaction ID |
| 05 | TRAN-REPORT-ACCOUNT-ID | X(11) | Alpha | Account ID |
| 05 | TRAN-REPORT-TYPE-CD | X(02) | Alpha | Type code |
| 05 | TRAN-REPORT-TYPE-DESC | X(15) | Alpha | Type description |
| 05 | TRAN-REPORT-CAT-CD | 9(04) | Numeric | Category code |
| 05 | TRAN-REPORT-CAT-DESC | X(29) | Alpha | Category description |
| 05 | TRAN-REPORT-SOURCE | X(10) | Alpha | Transaction source |
| 05 | TRAN-REPORT-AMT | -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount |

### 3.8 COSTM01.CPY — Statement Transaction Record

Transaction record layout used by statement generation (CBSTM03A/B).

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | TRNX-RECORD | *(group)* | Group | Statement transaction |
| 05 | TRNX-KEY | *(group)* | Group | Composite key |
| 10 | TRNX-CARD-NUM | X(16) | Alpha | Card number |
| 10 | TRNX-ID | X(16) | Alpha | Transaction ID |
| 05 | TRNX-REST | *(group)* | Group | Data fields |
| 10 | TRNX-TYPE-CD | X(02) | Alpha | Type code |
| 10 | TRNX-CAT-CD | 9(04) | Numeric | Category code |
| 10 | TRNX-SOURCE | X(10) | Alpha | Source |
| 10 | TRNX-DESC | X(100) | Alpha | Description |
| 10 | TRNX-AMT | S9(09)V99 | Signed decimal | Amount |
| 10 | TRNX-MERCHANT-ID | 9(09) | Numeric | Merchant ID |
| 10 | TRNX-MERCHANT-NAME | X(50) | Alpha | Merchant name |
| 10 | TRNX-MERCHANT-CITY | X(50) | Alpha | Merchant city |
| 10 | TRNX-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP |
| 10 | TRNX-ORIG-TS | X(26) | Timestamp | Original timestamp |
| 10 | TRNX-PROC-TS | X(26) | Timestamp | Processed timestamp |

---

## 4. Authorization Entity (Sub-App)

### 4.1 CIPAUDTY.cpy — Authorization Detail Record

Authorization message stored in IMS database `DBPAUTP0`.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | *(implied)* | — | Group | Authorization detail segment |
| 05 | PA-AUTHORIZATION-KEY | *(group)* | Group | Composite key |
| 10 | PA-AUTH-DATE-9C | S9(05) | Packed numeric | Authorization date (Julian) |
| 10 | PA-AUTH-TIME-9C | S9(09) | Packed numeric | Authorization time |
| 05 | PA-AUTH-ORIG-DATE | X(06) | Alpha | Original date (MMDDYY) |
| 05 | PA-AUTH-ORIG-TIME | X(06) | Alpha | Original time (HHMMSS) |
| 05 | PA-CARD-NUM | X(16) | Alpha | Card number |
| 05 | PA-AUTH-TYPE | X(04) | Alpha | Authorization type |
| 05 | PA-CARD-EXPIRY-DATE | X(04) | Alpha | Card expiry (YYMM) |
| 05 | PA-MESSAGE-TYPE | X(06) | Alpha | Message type |
| 05 | PA-MESSAGE-SOURCE | X(06) | Alpha | Source system |
| 05 | PA-AUTH-ID-CODE | X(06) | Alpha | Authorization ID code |
| 05 | PA-AUTH-RESP-CODE | X(02) | Alpha | Response code (00=approved) |
| 05 | PA-AUTH-RESP-REASON | X(04) | Alpha | Decline reason code |
| 05 | PA-PROCESSING-CODE | 9(06) | Numeric | Processing code |
| 05 | PA-TRANSACTION-AMT | S9(10)V99 | Signed decimal | Requested amount |
| 05 | PA-APPROVED-AMT | S9(10)V99 | Signed decimal | Approved amount |
| 05 | PA-MERCHANT-CATAGORY-CODE | X(04) | Alpha | MCC code |
| 05 | PA-ACQR-COUNTRY-CODE | X(03) | Alpha | Acquirer country |
| 05 | PA-POS-ENTRY-MODE | 9(02) | Numeric | POS entry mode |
| 05 | PA-MERCHANT-ID | X(15) | Alpha | Merchant ID |
| 05 | PA-MERCHANT-NAME | X(22) | Alpha | Merchant name |
| 05 | PA-MERCHANT-CITY | X(13) | Alpha | Merchant city |
| 05 | PA-MERCHANT-STATE | X(02) | Alpha | Merchant state |
| 05 | PA-MERCHANT-ZIP | X(09) | Alpha | Merchant ZIP |
| 05 | PA-TRANSACTION-ID | X(15) | Alpha | Transaction ID |
| 05 | PA-MATCH-STATUS | X(01) | Alpha | Match status flag |
| 05 | PA-AUTH-FRAUD | X(01) | Alpha | Fraud indicator |
| 05 | PA-FRAUD-RPT-DATE | X(08) | Alpha | Fraud report date |

### 4.2 CIPAUSMY.cpy — Authorization Summary Record

Account-level authorization summary in IMS root segment.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 05 | PA-ACCT-ID | S9(11) | Signed numeric | Account ID |
| 05 | PA-CUST-ID | 9(09) | Numeric | Customer ID |
| 05 | PA-AUTH-STATUS | X(01) | Alpha | Authorization status |
| 05 | PA-ACCOUNT-STATUS | X(02) | Alpha | Account status |
| 05 | PA-CREDIT-LIMIT | S9(09)V99 | Signed decimal | Credit limit |
| 05 | PA-CASH-LIMIT | S9(09)V99 | Signed decimal | Cash advance limit |
| 05 | PA-CREDIT-BALANCE | S9(09)V99 | Signed decimal | Current credit balance |
| 05 | PA-CASH-BALANCE | S9(09)V99 | Signed decimal | Current cash balance |
| 05 | PA-APPROVED-AUTH-CNT | S9(04) | Signed numeric | Approved auth count |
| 05 | PA-DECLINED-AUTH-CNT | S9(04) | Signed numeric | Declined auth count |
| 05 | PA-APPROVED-AUTH-AMT | S9(09)V99 | Signed decimal | Total approved amount |
| 05 | PA-DECLINED-AUTH-AMT | S9(09)V99 | Signed decimal | Total declined amount |

### 4.3 CCPAURQY.cpy — Authorization Request

Incoming authorization request message (from MQ).

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 05 | PA-RQ-AUTH-DATE | X(06) | Alpha | Request date |
| 05 | PA-RQ-AUTH-TIME | X(06) | Alpha | Request time |
| 05 | PA-RQ-CARD-NUM | X(16) | Alpha | Card number |
| 05 | PA-RQ-AUTH-TYPE | X(04) | Alpha | Auth type |
| 05 | PA-RQ-CARD-EXPIRY-DATE | X(04) | Alpha | Card expiry |
| 05 | PA-RQ-MESSAGE-TYPE | X(06) | Alpha | Message type |
| 05 | PA-RQ-MESSAGE-SOURCE | X(06) | Alpha | Source system |
| 05 | PA-RQ-PROCESSING-CODE | 9(06) | Numeric | Processing code |
| 05 | PA-RQ-TRANSACTION-AMT | +9(10).99 | Edited numeric | Transaction amount |
| 05 | PA-RQ-MERCHANT-CATAGORY-CODE | X(04) | Alpha | MCC |
| 05 | PA-RQ-ACQR-COUNTRY-CODE | X(03) | Alpha | Acquirer country |
| 05 | PA-RQ-POS-ENTRY-MODE | 9(02) | Numeric | POS entry mode |
| 05 | PA-RQ-MERCHANT-ID | X(15) | Alpha | Merchant ID |
| 05 | PA-RQ-MERCHANT-NAME | X(22) | Alpha | Merchant name |
| 05 | PA-RQ-MERCHANT-CITY | X(13) | Alpha | City |
| 05 | PA-RQ-MERCHANT-STATE | X(02) | Alpha | State |
| 05 | PA-RQ-MERCHANT-ZIP | X(09) | Alpha | ZIP |
| 05 | PA-RQ-TRANSACTION-ID | X(15) | Alpha | Transaction ID |

### 4.4 CCPAURLY.cpy — Authorization Response

Authorization response message (to MQ).

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 05 | PA-RL-CARD-NUM | X(16) | Alpha | Card number |
| 05 | PA-RL-TRANSACTION-ID | X(15) | Alpha | Transaction ID |
| 05 | PA-RL-AUTH-ID-CODE | X(06) | Alpha | Auth approval code |
| 05 | PA-RL-AUTH-RESP-CODE | X(02) | Alpha | Response code |
| 05 | PA-RL-AUTH-RESP-REASON | X(04) | Alpha | Decline reason |
| 05 | PA-RL-APPROVED-AMT | +9(10).99 | Edited numeric | Approved amount |

### 4.5 CCPAUERY.cpy — Authorization Error Log

Error logging for authorization processing.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | ERROR-LOG-RECORD | *(group)* | Group | Error record |
| 05 | ERR-DATE | X(06) | Alpha | Error date |
| 05 | ERR-TIME | X(06) | Alpha | Error time |
| 05 | ERR-APPLICATION | X(08) | Alpha | Application name |
| 05 | ERR-PROGRAM | X(08) | Alpha | Program name |
| 05 | ERR-LOCATION | X(04) | Alpha | Error location in code |
| 05 | ERR-LEVEL | X(01) | Alpha | Severity level |
| 05 | ERR-SUBSYSTEM | X(01) | Alpha | Subsystem code |
| 05 | ERR-CODE-1 | X(09) | Alpha | Error code 1 |
| 05 | ERR-CODE-2 | X(09) | Alpha | Error code 2 |
| 05 | ERR-MESSAGE | X(50) | Alpha | Error message |
| 05 | ERR-EVENT-KEY | X(20) | Alpha | Event key |

---

## 5. User / Security Entity

### 5.1 CSUSR01Y.cpy — User Security Record

User authentication record in `USRSEC.VSAM.KSDS`. Referenced by 14 programs.

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|-----------------|
| 01 | SEC-USER-DATA | *(group)* | Group | User security record | — |
| 05 | SEC-USR-ID | X(08) | Alpha | User login ID | Primary key; unique |
| 05 | SEC-USR-FNAME | X(20) | Alpha | User first name | Required |
| 05 | SEC-USR-LNAME | X(20) | Alpha | User last name | Required |
| 05 | SEC-USR-PWD | X(08) | Alpha | User password | 8 chars; plaintext storage |
| 05 | SEC-USR-TYPE | X(01) | Alpha | User type | 'A' = admin, 'R' = regular |
| 05 | SEC-USR-FILLER | X(23) | Alpha | Reserved | Padding |

**Record length:** 80 bytes

### 5.2 UNUSED1Y.cpy — Unused Data Record

Placeholder/deprecated copy of user record structure. Not referenced by any program.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | UNUSED-DATA | *(group)* | Group | Unused record |
| 05 | UNUSED-ID..TYPE | various | — | Mirrors CSUSR01Y layout |

---

## 6. Application Framework Copybooks

### 6.1 COCOM01Y.cpy — COMMAREA (Communication Area)

Shared inter-program communication block passed via CICS COMMAREA. Referenced by 21 programs.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | CARDDEMO-COMMAREA | *(group)* | Group | Application communication area |
| 05 | CDEMO-GENERAL-INFO | *(group)* | Group | Navigation/session data |
| 10 | CDEMO-FROM-TRANID | X(04) | Alpha | Source transaction ID |
| 10 | CDEMO-FROM-PROGRAM | X(08) | Alpha | Source program name |
| 10 | CDEMO-TO-TRANID | X(04) | Alpha | Target transaction ID |
| 10 | CDEMO-TO-PROGRAM | X(08) | Alpha | Target program name |
| 10 | CDEMO-USER-ID | X(08) | Alpha | Logged-in user ID |
| 10 | CDEMO-USER-TYPE | X(01) | Alpha | User type (A/R) |
| 10 | CDEMO-PGM-CONTEXT | 9(01) | Numeric | Program context flag |
| 05 | CDEMO-CUSTOMER-INFO | *(group)* | Group | Selected customer |
| 10 | CDEMO-CUST-ID | 9(09) | Numeric | Customer ID |
| 10 | CDEMO-CUST-FNAME | X(25) | Alpha | First name |
| 10 | CDEMO-CUST-MNAME | X(25) | Alpha | Middle name |
| 10 | CDEMO-CUST-LNAME | X(25) | Alpha | Last name |
| 05 | CDEMO-ACCOUNT-INFO | *(group)* | Group | Selected account |
| 10 | CDEMO-ACCT-ID | 9(11) | Numeric | Account ID |
| 10 | CDEMO-ACCT-STATUS | X(01) | Alpha | Account status |
| 05 | CDEMO-CARD-INFO | *(group)* | Group | Selected card |
| 10 | CDEMO-CARD-NUM | 9(16) | Numeric | Card number |
| 05 | CDEMO-MORE-INFO | *(group)* | Group | Screen state |
| 10 | CDEMO-LAST-MAP | X(7) | Alpha | Last BMS map used |
| 10 | CDEMO-LAST-MAPSET | X(7) | Alpha | Last BMS mapset |

### 6.2 CSDAT01Y.cpy — Date/Time Work Fields

System date and time working storage. Referenced by 21 programs.

Key fields: `WS-CURDATE` (YYYYMMDD), `WS-CURTIME` (HHMMSSCC), formatted variants (MM/DD/YY, HH:MM:SS), `WS-TIMESTAMP` (ISO format).

### 6.3 COTTL01Y.cpy — Screen Title Fields

BMS screen title area. Referenced by 21 programs.

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|------------------|
| 01 | CCDA-SCREEN-TITLE | *(group)* | Title block |
| 05 | CCDA-TITLE01 | X(40) | Screen title line 1 |
| 05 | CCDA-TITLE02 | X(40) | Screen title line 2 |
| 05 | CCDA-THANK-YOU | X(40) | Thank-you/status message |

### 6.4 CSMSG01Y.cpy — Common Messages

Standard application messages. Referenced by 21 programs.

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|------------------|
| 01 | CCDA-COMMON-MESSAGES | *(group)* | Message block |
| 05 | CCDA-MSG-THANK-YOU | X(50) | Thank-you message |
| 05 | CCDA-MSG-INVALID-KEY | X(50) | Invalid key message |

### 6.5 CSMSG02Y.cpy — Extended Messages

Additional validation and error messages. Referenced by 8 programs. Contains VALUE clauses with message text for field-level validations.

### 6.6 COMEN02Y.cpy — Main Menu Options

Menu option definitions for COMEN01C main menu. Contains hardcoded menu entries (option number, description, target program, user type).

### 6.7 COADM02Y.cpy — Admin Menu Options

Admin-specific menu definitions for COADM01C.

---

## 7. Utility & Lookup Copybooks

### 7.1 CSLKPCDY.cpy — State/ZIP/Area Code Lookup Tables

Largest copybook (1,318 lines). Contains hardcoded lookup tables:
- US state codes and names
- State-to-ZIP code range mappings
- Area code-to-state mappings

Used for customer address and phone number validation.

### 7.2 CSSTRPFY.cpy — String Processing Functions

Reusable PERFORM paragraphs for string operations (uppercase conversion, trimming, padding). Referenced by 7 programs.

### 7.3 CSSETATY.cpy — Set Attribute Functions

BMS attribute-setting routines for screen field highlighting (error, normal, bright). Referenced by 2 programs.

### 7.4 CSUTLDPY.cpy — Date Utility Paragraphs

Date validation and conversion routines (376 lines). Used by CSUTLDTC.

### 7.5 CSUTLDWY.cpy — Date Utility Work Fields

Working storage for date validation including edit masks, flags, and result fields.

### 7.6 CODATECN.cpy — Date Conversion Record

Input/output structure for date format conversion between YYYYMMDD, MMDDYY, and formatted displays.

### 7.7 CVCRD01Y.cpy — Card Display Fields

Working fields for credit card display operations (account ID numeric, card number numeric).

---

## 8. Data Migration Copybooks

### 8.1 CVEXPORT.cpy — Export Record Layout

Composite export record (104 lines, 64 fields) for branch migration. Contains:
- Export header (record type, timestamp, sequence, branch, region)
- Customer data section (mirrors CVCUS01Y)
- Account data section (mirrors CVACT01Y)
- Card cross-reference section (mirrors CVACT03Y)
- Card data section (mirrors CVACT02Y)
- Transaction data section (mirrors CVTRA05Y)

---

## 9. IMS/DB2 Infrastructure Copybooks (Sub-App)

### 9.1 IMSFUNCS.cpy — IMS DL/I Function Codes

Standard DL/I function codes for IMS database calls.

| Field | Value | Meaning |
|-------|-------|---------|
| FUNC-GU | 'GU  ' | Get Unique |
| FUNC-GHU | 'GHU ' | Get Hold Unique |
| FUNC-GN | 'GN  ' | Get Next |
| FUNC-GHN | 'GHN ' | Get Hold Next |
| FUNC-GNP | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | 'REPL' | Replace |
| FUNC-ISRT | 'ISRT' | Insert |
| FUNC-DLET | 'DLET' | Delete |

### 9.2 PAUTBPCB.CPY / PADFLPCB.CPY / PASFLPCB.CPY — IMS PCB Masks

Program Communication Block masks for IMS database access:
- **PAUTBPCB**: Primary authorization DB PCB
- **PADFLPCB**: Authorization detail full-function DB PCB
- **PASFLPCB**: Authorization summary full-function DB PCB

Each contains: DBD name, segment level, status code, processing option, segment name, key feedback area.

### 9.3 CSDB2RPY.cpy / CSDB2RWY.cpy — DB2 Interface

DB2 read and write interface definitions for the transaction type sub-app. Contains SQLCA and host variable declarations.

---

## 10. BMS Map Copybooks

Located in `app/cpy-bms/` — generated from BMS map source. Each contains input (I) and output (O) variants of the map structure with field length, data, and attribute byte fields. These are machine-generated and not documented here in detail.

| Copybook | BMS Map | Screen |
|----------|---------|--------|
| COACTUP.CPY | COACTUP.bms | Account Update |
| COACTVW.CPY | COACTVW.bms | Account View |
| COADM01.CPY | COADM01.bms | Admin Menu |
| COBIL00.CPY | COBIL00.bms | Bill Payment |
| COCRDLI.CPY | COCRDLI.bms | Card List |
| COCRDSL.CPY | COCRDSL.bms | Card Selection |
| COCRDUP.CPY | COCRDUP.bms | Card Update |
| COMEN01.CPY | COMEN01.bms | Main Menu |
| CORPT00.CPY | CORPT00.bms | Report Request |
| COSGN00.CPY | COSGN00.bms | Sign-On |
| COTRN00.CPY | COTRN00.bms | Transaction List |
| COTRN01.CPY | COTRN01.bms | Transaction View |
| COTRN02.CPY | COTRN02.bms | Transaction Add |
| COUSR00.CPY | COUSR00.bms | User List |
| COUSR01.CPY | COUSR01.bms | User Add |
| COUSR02.CPY | COUSR02.bms | User Update |
| COUSR03.CPY | COUSR03.bms | User Delete |
| COPAU00.cpy | COPAU00.bms | Auth Summary (sub-app) |
| COPAU01.cpy | COPAU01.bms | Auth Detail (sub-app) |
| COTRTLI.cpy | COTRTLI.bms | Tran Type List (sub-app) |
| COTRTUP.cpy | COTRTUP.bms | Tran Type Update (sub-app) |
