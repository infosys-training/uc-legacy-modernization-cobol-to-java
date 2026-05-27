# Data Dictionary — CardDemo COBOL Estate

## Overview

This data dictionary catalogs every copybook in the CardDemo application, extracting field names, PIC clauses, data types, business meanings, and validation rules. Fields are grouped by business entity.

**Copybook locations:**
- `app/cpy/` — Core business data structures and screen definitions
- `app/cpy-bms/` — BMS screen map definitions (not covered here — compile-time artifacts)
- `app/app-authorization-ims-db2-mq/cpy/` — IMS/DB2 authorization structures
- `app/app-transaction-type-db2/cpy/` — DB2 transaction type structures

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

Primary VSAM record for credit card accounts. Key = `ACCT-ID`.

| Field | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------|-----------------|------------|
| `ACCT-ID` | `9(11)` | Numeric (11 digits) | Unique account identifier | Key field, must be unique |
| `ACCT-ACTIVE-STATUS` | `X(01)` | Alphanumeric (1) | Account status flag | `'Y'` = Active |
| `ACCT-CURR-BAL` | `S9(10)V99` | Signed decimal (12,2) | Current account balance | Signed — can be negative (overpayment) |
| `ACCT-CREDIT-LIMIT` | `S9(10)V99` | Signed decimal (12,2) | Maximum credit limit | Must be > 0 |
| `ACCT-CASH-CREDIT-LIMIT` | `S9(10)V99` | Signed decimal (12,2) | Cash advance credit limit | Must be ≤ credit limit |
| `ACCT-OPEN-DATE` | `X(10)` | Alphanumeric date | Date account was opened | Format: YYYY-MM-DD |
| `ACCT-EXPIRAION-DATE` | `X(10)` | Alphanumeric date | Account expiration date | Format: YYYY-MM-DD |
| `ACCT-REISSUE-DATE` | `X(10)` | Alphanumeric date | Last card reissue date | Format: YYYY-MM-DD |
| `ACCT-CURR-CYC-CREDIT` | `S9(10)V99` | Signed decimal (12,2) | Current cycle credit (payments) | Running total for billing cycle |
| `ACCT-CURR-CYC-DEBIT` | `S9(10)V99` | Signed decimal (12,2) | Current cycle debit (charges) | Running total for billing cycle |
| `ACCT-ADDR-ZIP` | `X(10)` | Alphanumeric (10) | Account billing ZIP code | US ZIP+4 format |
| `ACCT-GROUP-ID` | `X(10)` | Alphanumeric (10) | Disclosure/interest rate group | Links to DIS-GROUP-RECORD |
| `FILLER` | `X(178)` | Filler | Reserved space | — |

### CVEXPORT.cpy — Export Account Record (within EXPORT-RECORD)

Storage-optimized version used for branch migration export. Uses COMP-3 for numeric fields.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `EXP-ACCT-ID` | `9(11)` | Numeric | Account ID |
| `EXP-ACCT-ACTIVE-STATUS` | `X(01)` | Alphanumeric | Status flag |
| `EXP-ACCT-CURR-BAL` | `S9(10)V99 COMP-3` | Packed decimal | Current balance (compressed) |
| `EXP-ACCT-CREDIT-LIMIT` | `S9(10)V99` | Signed decimal | Credit limit |
| `EXP-ACCT-CASH-CREDIT-LIMIT` | `S9(10)V99 COMP-3` | Packed decimal | Cash credit limit (compressed) |
| `EXP-ACCT-OPEN-DATE` | `X(10)` | Date | Open date |
| `EXP-ACCT-EXPIRAION-DATE` | `X(10)` | Date | Expiration date |
| `EXP-ACCT-REISSUE-DATE` | `X(10)` | Date | Reissue date |
| `EXP-ACCT-CURR-CYC-CREDIT` | `S9(10)V99` | Signed decimal | Cycle credits |
| `EXP-ACCT-CURR-CYC-DEBIT` | `S9(10)V99 COMP` | Binary | Cycle debits (binary compressed) |
| `EXP-ACCT-ADDR-ZIP` | `X(10)` | Alphanumeric | ZIP code |
| `EXP-ACCT-GROUP-ID` | `X(10)` | Alphanumeric | Disclosure group |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

Primary VSAM record for customers. Key = `CUST-ID`. Referenced by batch programs and CBSTM03A.

| Field | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------|-----------------|------------|
| `CUST-ID` | `9(09)` | Numeric (9 digits) | Unique customer identifier | Key field |
| `CUST-FIRST-NAME` | `X(25)` | Alphanumeric (25) | Customer first name | — |
| `CUST-MIDDLE-NAME` | `X(25)` | Alphanumeric (25) | Customer middle name | — |
| `CUST-LAST-NAME` | `X(25)` | Alphanumeric (25) | Customer last name | — |
| `CUST-ADDR-LINE-1` | `X(50)` | Alphanumeric (50) | Address line 1 | — |
| `CUST-ADDR-LINE-2` | `X(50)` | Alphanumeric (50) | Address line 2 | — |
| `CUST-ADDR-LINE-3` | `X(50)` | Alphanumeric (50) | Address line 3 | — |
| `CUST-ADDR-STATE-CD` | `X(02)` | Alphanumeric (2) | US state code | 88-level: VALID-US-STATE-CODE (all 50 states + territories) |
| `CUST-ADDR-COUNTRY-CD` | `X(03)` | Alphanumeric (3) | ISO country code | — |
| `CUST-ADDR-ZIP` | `X(10)` | Alphanumeric (10) | ZIP/postal code | US ZIP+4 format |
| `CUST-PHONE-NUM-1` | `X(15)` | Alphanumeric (15) | Primary phone | Area code validated via CSSETATY |
| `CUST-PHONE-NUM-2` | `X(15)` | Alphanumeric (15) | Secondary phone | — |
| `CUST-SSN` | `9(09)` | Numeric (9 digits) | Social Security Number | — |
| `CUST-GOVT-ISSUED-ID` | `X(20)` | Alphanumeric (20) | Government-issued ID | — |
| `CUST-DOB-YYYY-MM-DD` | `X(10)` | Alphanumeric date | Date of birth | Format: YYYY-MM-DD |
| `CUST-EFT-ACCOUNT-ID` | `X(10)` | Alphanumeric (10) | EFT/bank account for payments | — |
| `CUST-PRI-CARD-HOLDER-IND` | `X(01)` | Alphanumeric (1) | Primary cardholder indicator | `'Y'` = Primary |
| `CUST-FICO-CREDIT-SCORE` | `9(03)` | Numeric (3 digits) | FICO credit score | Range: 300-850 |
| `FILLER` | `X(168)` | Filler | Reserved | — |

### CUSTREC.cpy — Customer Record (Alternate Layout, RECLN 500)

Identical structure to CVCUS01Y but used by CBSTM03A for statement generation. Minor formatting differences (DOB field named `CUST-DOB-YYYYMMDD`).

### CSUSR01Y.cpy — User Security Record (RECLN 80)

VSAM record for application user authentication. Key = `SEC-USR-ID`.

| Field | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------|-----------------|------------|
| `SEC-USR-ID` | `X(08)` | Alphanumeric (8) | User login ID | Key field, unique |
| `SEC-USR-FNAME` | `X(20)` | Alphanumeric (20) | User first name | — |
| `SEC-USR-LNAME` | `X(20)` | Alphanumeric (20) | User last name | — |
| `SEC-USR-PWD` | `X(08)` | Alphanumeric (8) | User password (plaintext) | — |
| `SEC-USR-TYPE` | `X(01)` | Alphanumeric (1) | User type code | `'A'` = Admin, `'U'` = Regular User |
| `SEC-USR-FILLER` | `X(23)` | Filler | Reserved | — |

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

Primary VSAM record for credit cards. Key = `CARD-NUM`.

| Field | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------|-----------------|------------|
| `CARD-NUM` | `X(16)` | Alphanumeric (16) | Credit card number | Key field; Luhn-validated externally |
| `CARD-ACCT-ID` | `9(11)` | Numeric (11) | Associated account ID | FK → ACCT-ID |
| `CARD-CVV-CD` | `9(03)` | Numeric (3) | Card verification value | 3-digit security code |
| `CARD-EMBOSSED-NAME` | `X(50)` | Alphanumeric (50) | Name embossed on card | — |
| `CARD-EXPIRAION-DATE` | `X(10)` | Alphanumeric (10) | Card expiration date | Format: YYYY-MM-DD |
| `CARD-ACTIVE-STATUS` | `X(01)` | Alphanumeric (1) | Card status flag | `'Y'` = Active |
| `FILLER` | `X(59)` | Filler | Reserved | — |

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

Links cards to customers and accounts. Key = `XREF-CARD-NUM`.

| Field | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------|-----------------|------------|
| `XREF-CARD-NUM` | `X(16)` | Alphanumeric (16) | Card number | Key field; FK → CARD-NUM |
| `XREF-CUST-ID` | `9(09)` | Numeric (9) | Customer ID | FK → CUST-ID |
| `XREF-ACCT-ID` | `9(11)` | Numeric (11) | Account ID | FK → ACCT-ID |
| `FILLER` | `X(14)` | Filler | Reserved | — |

### CVCRD01Y.cpy — Card Screen Work Areas

Working storage for CICS card screens. Contains AID key mapping and navigation fields.

| Field | PIC Clause | Type | Business Meaning | Validation (88-level) |
|-------|-----------|------|-----------------|----------------------|
| `CCARD-AID` | `X(5)` | Alphanumeric (5) | AID key pressed | `CCARD-AID-ENTER` = 'ENTER', `CCARD-AID-CLEAR` = 'CLEAR', `CCARD-AID-PFK01`–`CCARD-AID-PFK12` = 'PFK01'–'PFK12' |
| `CCARD-NEXT-PROG` | `X(8)` | Alphanumeric (8) | Next program to transfer to | — |
| `CCARD-NEXT-MAPSET` | `X(7)` | Alphanumeric (7) | Next BMS mapset | — |
| `CCARD-NEXT-MAP` | `X(7)` | Alphanumeric (7) | Next BMS map | — |
| `CCARD-ERROR-MSG` | `X(75)` | Alphanumeric (75) | Error message display | — |

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

Primary VSAM record for posted transactions. Key = `TRAN-ID`.

| Field | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------|-----------------|------------|
| `TRAN-ID` | `X(16)` | Alphanumeric (16) | Unique transaction identifier | Key field, system-generated |
| `TRAN-TYPE-CD` | `X(02)` | Alphanumeric (2) | Transaction type code | FK → TRAN-TYPE |
| `TRAN-CAT-CD` | `9(04)` | Numeric (4) | Transaction category code | FK → TRAN-CAT-CD |
| `TRAN-SOURCE` | `X(10)` | Alphanumeric (10) | Origination source | — |
| `TRAN-DESC` | `X(100)` | Alphanumeric (100) | Transaction description | — |
| `TRAN-AMT` | `S9(09)V99` | Signed decimal (11,2) | Transaction amount | Positive = debit, Negative = credit |
| `TRAN-MERCHANT-ID` | `9(09)` | Numeric (9) | Merchant identifier | — |
| `TRAN-MERCHANT-NAME` | `X(50)` | Alphanumeric (50) | Merchant name | — |
| `TRAN-MERCHANT-CITY` | `X(50)` | Alphanumeric (50) | Merchant city | — |
| `TRAN-MERCHANT-ZIP` | `X(10)` | Alphanumeric (10) | Merchant ZIP code | — |
| `TRAN-CARD-NUM` | `X(16)` | Alphanumeric (16) | Card used for transaction | FK → CARD-NUM |
| `TRAN-ORIG-TS` | `X(26)` | Alphanumeric (26) | Transaction origination timestamp | ISO format |
| `TRAN-PROC-TS` | `X(26)` | Alphanumeric (26) | Transaction processing timestamp | ISO format |
| `FILLER` | `X(20)` | Filler | Reserved | — |

### CVTRA06Y.cpy — Daily Transaction Input Record (RECLN 350)

Input record for daily transaction batch posting. Same structure as CVTRA05Y.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `DALYTRAN-ID` | `X(16)` | Alphanumeric (16) | Daily transaction ID |
| `DALYTRAN-TYPE-CD` | `X(02)` | Alphanumeric (2) | Transaction type code |
| `DALYTRAN-CAT-CD` | `9(04)` | Numeric (4) | Transaction category code |
| `DALYTRAN-SOURCE` | `X(10)` | Alphanumeric (10) | Origination source |
| `DALYTRAN-DESC` | `X(100)` | Alphanumeric (100) | Transaction description |
| `DALYTRAN-AMT` | `S9(09)V99` | Signed decimal (11,2) | Transaction amount |
| `DALYTRAN-MERCHANT-ID` | `9(09)` | Numeric (9) | Merchant ID |
| `DALYTRAN-MERCHANT-NAME` | `X(50)` | Alphanumeric (50) | Merchant name |
| `DALYTRAN-MERCHANT-CITY` | `X(50)` | Alphanumeric (50) | Merchant city |
| `DALYTRAN-MERCHANT-ZIP` | `X(10)` | Alphanumeric (10) | Merchant ZIP |
| `DALYTRAN-CARD-NUM` | `X(16)` | Alphanumeric (16) | Card number |
| `DALYTRAN-ORIG-TS` | `X(26)` | Alphanumeric (26) | Origination timestamp |
| `DALYTRAN-PROC-TS` | `X(26)` | Alphanumeric (26) | Processing timestamp |

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

Running balance by account, type, and category. Compound key.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `TRANCAT-ACCT-ID` | `9(11)` | Numeric (11) | Account ID (part of key) |
| `TRANCAT-TYPE-CD` | `X(02)` | Alphanumeric (2) | Transaction type (part of key) |
| `TRANCAT-CD` | `9(04)` | Numeric (4) | Transaction category (part of key) |
| `TRAN-CAT-BAL` | `S9(09)V99` | Signed decimal (11,2) | Running balance for this category |

### CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

Interest rate configuration by account group, transaction type, and category.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `DIS-ACCT-GROUP-ID` | `X(10)` | Alphanumeric (10) | Account group (part of key) |
| `DIS-TRAN-TYPE-CD` | `X(02)` | Alphanumeric (2) | Transaction type (part of key) |
| `DIS-TRAN-CAT-CD` | `9(04)` | Numeric (4) | Transaction category (part of key) |
| `DIS-INT-RATE` | `S9(04)V99` | Signed decimal (6,2) | Interest rate for this group/type/category |

### CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `TRAN-TYPE` | `X(02)` | Alphanumeric (2) | Transaction type code (key) |
| `TRAN-TYPE-DESC` | `X(50)` | Alphanumeric (50) | Transaction type description |

### CVTRA04Y.cpy — Transaction Category Type Record (RECLN 60)

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `TRAN-TYPE-CD` | `X(02)` | Alphanumeric (2) | Transaction type code (part of key) |
| `TRAN-CAT-CD` | `9(04)` | Numeric (4) | Transaction category code (part of key) |
| `TRAN-CAT-TYPE-DESC` | `X(50)` | Alphanumeric (50) | Category type description |

### CVTRA07Y.cpy — Transaction Report Structures

Report formatting structures for transaction reporting.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `REPT-SHORT-NAME` | `X(38)` | Alphanumeric | Report short name (default: 'DALYREPT') |
| `REPT-LONG-NAME` | `X(41)` | Alphanumeric | Report title (default: 'Daily Transaction Report') |
| `REPT-DATE-HEADER` | `X(12)` | Alphanumeric | Date range header label |
| `REPT-START-DATE` | `X(10)` | Alphanumeric | Report start date |
| `REPT-END-DATE` | `X(10)` | Alphanumeric | Report end date |
| `TRAN-REPORT-TRANS-ID` | `X(16)` | Alphanumeric | Transaction ID (report line) |
| `TRAN-REPORT-ACCOUNT-ID` | `X(11)` | Alphanumeric | Account ID (report line) |
| `TRAN-REPORT-TYPE-CD` | `X(02)` | Alphanumeric | Type code (report line) |
| `TRAN-REPORT-AMT` | `-ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Formatted amount |
| `REPT-PAGE-TOTAL` | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Page subtotal |
| `REPT-ACCOUNT-TOTAL` | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Account subtotal |
| `REPT-GRAND-TOTAL` | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Grand total |

### COSTM01.CPY — Statement Transaction Record

Alternate transaction layout keyed by card+transaction for statement generation.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `TRNX-CARD-NUM` | `X(16)` | Alphanumeric (16) | Card number (part of compound key) |
| `TRNX-ID` | `X(16)` | Alphanumeric (16) | Transaction ID (part of compound key) |
| `TRNX-TYPE-CD` | `X(02)` | Alphanumeric (2) | Transaction type |
| `TRNX-CAT-CD` | `9(04)` | Numeric (4) | Category code |
| `TRNX-SOURCE` | `X(10)` | Alphanumeric (10) | Source |
| `TRNX-DESC` | `X(100)` | Alphanumeric (100) | Description |
| `TRNX-AMT` | `S9(09)V99` | Signed decimal | Amount |
| `TRNX-MERCHANT-ID` | `9(09)` | Numeric (9) | Merchant ID |
| `TRNX-MERCHANT-NAME` | `X(50)` | Alphanumeric (50) | Merchant name |
| `TRNX-MERCHANT-CITY` | `X(50)` | Alphanumeric (50) | Merchant city |
| `TRNX-MERCHANT-ZIP` | `X(10)` | Alphanumeric (10) | Merchant ZIP |
| `TRNX-ORIG-TS` | `X(26)` | Alphanumeric (26) | Origination timestamp |
| `TRNX-PROC-TS` | `X(26)` | Alphanumeric (26) | Processing timestamp |

---

## 5. Authorization Entity (IMS)

### CCPAURQY.cpy — Pending Authorization Request (MQ Message)

MQ message structure for incoming authorization requests.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `PA-RQ-AUTH-DATE` | `X(06)` | Alphanumeric | Authorization request date |
| `PA-RQ-AUTH-TIME` | `X(06)` | Alphanumeric | Authorization request time |
| `PA-RQ-CARD-NUM` | `X(16)` | Alphanumeric | Card number to authorize |
| `PA-RQ-AUTH-TYPE` | `X(04)` | Alphanumeric | Authorization type code |
| `PA-RQ-CARD-EXPIRY-DATE` | `X(04)` | Alphanumeric | Card expiry date |
| `PA-RQ-MESSAGE-TYPE` | `X(06)` | Alphanumeric | Message type indicator |
| `PA-RQ-MESSAGE-SOURCE` | `X(06)` | Alphanumeric | Message source system |
| `PA-RQ-PROCESSING-CODE` | `9(06)` | Numeric | Processing code |
| `PA-RQ-TRANSACTION-AMT` | `+9(10).99` | Edited numeric | Transaction amount |
| `PA-RQ-MERCHANT-CATAGORY-CODE` | `X(04)` | Alphanumeric | MCC (Merchant Category Code) |
| `PA-RQ-ACQR-COUNTRY-CODE` | `X(03)` | Alphanumeric | Acquirer country code |
| `PA-RQ-POS-ENTRY-MODE` | `9(02)` | Numeric | POS entry mode |
| `PA-RQ-MERCHANT-ID` | `X(15)` | Alphanumeric | Merchant ID |
| `PA-RQ-MERCHANT-NAME` | `X(22)` | Alphanumeric | Merchant name |
| `PA-RQ-MERCHANT-CITY` | `X(13)` | Alphanumeric | Merchant city |
| `PA-RQ-MERCHANT-STATE` | `X(02)` | Alphanumeric | Merchant state |
| `PA-RQ-MERCHANT-ZIP` | `X(09)` | Alphanumeric | Merchant ZIP |
| `PA-RQ-TRANSACTION-ID` | `X(15)` | Alphanumeric | Transaction ID |

### CIPAUDTY.cpy — IMS Pending Authorization Detail Segment

IMS hierarchical segment for authorization detail records.

| Field | PIC Clause | Type | Business Meaning | Validation (88-level) |
|-------|-----------|------|------------------|-----------------------|
| `PA-AUTH-DATE-9C` | `S9(05) COMP-3` | Packed decimal | Auth date (packed) | — |
| `PA-AUTH-TIME-9C` | `S9(09) COMP-3` | Packed decimal | Auth time (packed) | — |
| `PA-AUTH-ORIG-DATE` | `X(06)` | Alphanumeric | Original auth date | — |
| `PA-AUTH-ORIG-TIME` | `X(06)` | Alphanumeric | Original auth time | — |
| `PA-CARD-NUM` | `X(16)` | Alphanumeric | Card number | — |
| `PA-AUTH-TYPE` | `X(04)` | Alphanumeric | Authorization type | — |
| `PA-CARD-EXPIRY-DATE` | `X(04)` | Alphanumeric | Card expiry date | — |
| `PA-MESSAGE-TYPE` | `X(06)` | Alphanumeric | Message type | — |
| `PA-MESSAGE-SOURCE` | `X(06)` | Alphanumeric | Message source | — |
| `PA-AUTH-ID-CODE` | `X(06)` | Alphanumeric | Authorization ID code | — |
| `PA-AUTH-RESP-CODE` | `X(02)` | Alphanumeric | Response code | `PA-AUTH-APPROVED` = '00' |
| `PA-AUTH-RESP-REASON` | `X(04)` | Alphanumeric | Decline reason code | — |
| `PA-PROCESSING-CODE` | `9(06)` | Numeric | Processing code | — |
| `PA-TRANSACTION-AMT` | `S9(10)V99 COMP-3` | Packed decimal | Transaction amount | — |
| `PA-APPROVED-AMT` | `S9(10)V99 COMP-3` | Packed decimal | Approved amount | — |
| `PA-MERCHANT-CATAGORY-CODE` | `X(04)` | Alphanumeric | MCC code | — |
| `PA-ACQR-COUNTRY-CODE` | `X(03)` | Alphanumeric | Acquirer country | — |
| `PA-POS-ENTRY-MODE` | `9(02)` | Numeric | POS entry mode | — |
| `PA-MERCHANT-ID` | `X(15)` | Alphanumeric | Merchant ID | — |
| `PA-MERCHANT-NAME` | `X(22)` | Alphanumeric | Merchant name | — |
| `PA-MERCHANT-CITY` | `X(13)` | Alphanumeric | Merchant city | — |
| `PA-MERCHANT-STATE` | `X(02)` | Alphanumeric | Merchant state | — |
| `PA-MERCHANT-ZIP` | `X(09)` | Alphanumeric | Merchant ZIP | — |
| `PA-TRANSACTION-ID` | `X(15)` | Alphanumeric | Transaction ID | — |
| `PA-MATCH-STATUS` | `X(01)` | Alphanumeric | Match/reconciliation status | `PA-MATCH-PENDING` = 'P', `PA-MATCH-AUTH-DECLINED` = 'D', `PA-MATCH-PENDING-EXPIRED` = 'E', `PA-MATCHED-WITH-TRAN` = 'M' |
| `PA-AUTH-FRAUD` | `X(01)` | Alphanumeric | Fraud flag | `PA-FRAUD-CONFIRMED` = 'F', `PA-FRAUD-REMOVED` = 'R' |
| `PA-FRAUD-RPT-DATE` | `X(08)` | Alphanumeric | Fraud report date | — |

### CIPAUSMY.cpy — IMS Pending Authorization Summary Segment

Parent segment in IMS hierarchy containing account-level authorization summary.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `PA-ACCT-ID` | `S9(11) COMP-3` | Packed decimal | Account ID |
| `PA-CUST-ID` | `9(09)` | Numeric | Customer ID |
| `PA-AUTH-STATUS` | `X(01)` | Alphanumeric | Authorization status |
| `PA-ACCOUNT-STATUS` | `X(02) OCCURS 5` | Array (5×2) | Account status history |
| `PA-CREDIT-LIMIT` | `S9(09)V99 COMP-3` | Packed decimal | Credit limit |
| `PA-CASH-LIMIT` | `S9(09)V99 COMP-3` | Packed decimal | Cash advance limit |
| `PA-CREDIT-BALANCE` | `S9(09)V99 COMP-3` | Packed decimal | Credit balance |
| `PA-CASH-BALANCE` | `S9(09)V99 COMP-3` | Packed decimal | Cash balance |
| `PA-APPROVED-AUTH-CNT` | `S9(04) COMP` | Binary | Count of approved authorizations |
| `PA-DECLINED-AUTH-CNT` | `S9(04) COMP` | Binary | Count of declined authorizations |
| `PA-APPROVED-AUTH-AMT` | `S9(09)V99 COMP-3` | Packed decimal | Total approved amount |
| `PA-DECLINED-AUTH-AMT` | `S9(09)V99 COMP-3` | Packed decimal | Total declined amount |

---

## 6. Communication & Navigation

### COCOM01Y.cpy — CardDemo Communication Area (COMMAREA)

Shared inter-program communication structure passed via CICS XCTL COMMAREA.

| Field | PIC Clause | Type | Business Meaning | Validation (88-level) |
|-------|-----------|------|------------------|-----------------------|
| `CDEMO-FROM-TRANID` | `X(04)` | Alphanumeric | Originating CICS transaction ID | — |
| `CDEMO-FROM-PROGRAM` | `X(08)` | Alphanumeric | Originating program name | — |
| `CDEMO-TO-TRANID` | `X(04)` | Alphanumeric | Target CICS transaction ID | — |
| `CDEMO-TO-PROGRAM` | `X(08)` | Alphanumeric | Target program name | — |
| `CDEMO-USER-ID` | `X(08)` | Alphanumeric | Logged-in user ID | — |
| `CDEMO-USER-TYPE` | `X(01)` | Alphanumeric | User type | `CDEMO-USRTYP-ADMIN` = 'A', `CDEMO-USRTYP-USER` = 'U' |
| `CDEMO-CUST-ID` | `9(09)` | Numeric | Selected customer ID | — |
| `CDEMO-ACCT-ID` | `9(11)` | Numeric | Selected account ID | — |
| `CDEMO-CARD-NUM` | `9(16)` | Numeric | Selected card number | — |

### COMEN02Y.cpy — Menu Options Configuration

Defines the 11 available menu options with program routing (`CDEMO-MENU-OPT-COUNT` = 11).

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `CDEMO-MENU-OPT-COUNT` | `9(02)` | Numeric | Total menu option count (VALUE 11) |
| `CDEMO-MENU-OPT-NUM` | `9(02)` | Numeric | Menu option number (1–11) |
| `CDEMO-MENU-OPT-NAME` | `X(35)` | Alphanumeric | Menu option display name |
| `CDEMO-MENU-OPT-PGMNAME` | `X(08)` | Alphanumeric | Target program name |
| `CDEMO-MENU-OPT-USRTYPE` | `X(01)` | Alphanumeric | Required user type |

**Menu option mapping:**
| # | Name | Program | User Type |
|---|------|---------|-----------|
| 1 | Account View | COACTVWC | U |
| 2 | Account Update | COACTUPC | U |
| 3 | Credit Card List | COCRDLIC | U |
| 4 | Credit Card View | COCRDSLC | U |
| 5 | Credit Card Update | COCRDUPC | U |
| 6 | Transaction List | COTRN00C | U |
| 7 | Transaction View | COTRN01C | U |
| 8 | Transaction Add | COTRN02C | U |
| 9 | Transaction Reports | CORPT00C | U |
| 10 | Bill Payment | COBIL00C | U |
| 11 | Pending Authorization View | COPAUS0C | U |

---

## 7. Utility & Infrastructure Copybooks

### CSDAT01Y.cpy — Date/Time Working Storage

Standard date/time fields used by all online programs.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `WS-CURDATE-YEAR` | `9(04)` | Numeric | Current year |
| `WS-CURDATE-MONTH` | `9(02)` | Numeric | Current month |
| `WS-CURDATE-DAY` | `9(02)` | Numeric | Current day |
| `WS-CURDATE-N` | `9(08)` (REDEFINES) | Numeric | Numeric date YYYYMMDD |
| `WS-CURTIME-HOURS` | `9(02)` | Numeric | Current hours |
| `WS-CURTIME-MINUTE` | `9(02)` | Numeric | Current minutes |
| `WS-CURTIME-SECOND` | `9(02)` | Numeric | Current seconds |
| `WS-TIMESTAMP` | `X(26)` (composite) | Alphanumeric | ISO timestamp format |

### CODATECN.cpy — Date Conversion Utility

Used by CBACT01C for date format conversion (called via COBDATFT).

### CSMSG01Y.cpy / CSMSG02Y.cpy — Message Structures

Standard message display areas used by CICS screens for error/info messages.

### COTTL01Y.cpy — Screen Title Configuration

Screen title constants: "AWS Mainframe Modernization" and "CardDemo" branding.

### CSUTLDPY.cpy / CSUTLDWY.cpy — Date Edit/Validation Copybooks

Extensive date validation logic with 88-level conditions:
- `THIS-CENTURY` / `LAST-CENTURY` — Century validation (19xx/20xx)
- `WS-VALID-MONTH` — Month 01–12
- `FLG-YEAR-ISVALID` / `FLG-MONTH-ISVALID` / `FLG-DAY-ISVALID` — Component validity flags
- Validates: year, month, day including leap year and days-per-month

### CSSETATY.cpy — State/Phone Area Code Validation

Lookup tables for validation:
- `VALID-PHONE-AREA-CODE` — All North American area codes (800+ values from NANPA)
- `VALID-US-STATE-CODE` — All US states and territory codes

### UNUSED1Y.cpy — Unused Record Structure

Placeholder record structure (identical layout to CSUSR01Y). Not referenced by any program.

---

## 8. IMS Infrastructure Copybooks

### IMSFUNCS.cpy — IMS DL/I Function Codes

| Field | PIC Clause | Value | Business Meaning |
|-------|-----------|-------|-----------------|
| `FUNC-GU` | `X(04)` | 'GU  ' | Get Unique (direct retrieval) |
| `FUNC-GHU` | `X(04)` | 'GHU ' | Get Hold Unique (for update) |
| `FUNC-GN` | `X(04)` | 'GN  ' | Get Next (sequential) |
| `FUNC-GHN` | `X(04)` | 'GHN ' | Get Hold Next (for update) |
| `FUNC-GNP` | `X(04)` | 'GNP ' | Get Next within Parent |
| `FUNC-GHNP` | `X(04)` | 'GHNP' | Get Hold Next within Parent |
| `FUNC-REPL` | `X(04)` | 'REPL' | Replace segment |
| `FUNC-ISRT` | `X(04)` | 'ISRT' | Insert segment |
| `FUNC-DLET` | `X(04)` | 'DLET' | Delete segment |

### PAUTBPCB.CPY / PADFLPCB.CPY / PASFLPCB.CPY — IMS PCB Masks

Program Communication Block masks for IMS database access.

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `*-DBDNAME` | `X(08)` | Alphanumeric | Database Description name |
| `*-SEG-LEVEL` | `X(02)` | Alphanumeric | Segment level number |
| `*-PCB-STATUS` | `X(02)` | Alphanumeric | PCB status code |
| `*-PCB-PROCOPT` | `X(04)` | Alphanumeric | Processing options |
| `*-SEG-NAME` | `X(08)` | Alphanumeric | Segment name |
| `*-KEYFB` | `X(100–255)` | Alphanumeric | Key feedback area |

---

## 9. DB2 Structures (Transaction Type Sub-App)

### DCLTRTYP (SQL INCLUDE) — Transaction Type Table

DB2 table: `CARDDEMO.TRANSACTION_TYPE`

| Column | COBOL PIC | SQL Type | Business Meaning |
|--------|-----------|----------|-----------------|
| `TR_TYPE` | `X(02)` | CHAR(2) | Transaction type code (PK) |
| `TR_DESCRIPTION` | `X(50)` | VARCHAR(50) | Transaction type description |

### DCLTRCAT (SQL INCLUDE) — Transaction Category Table

DB2 table: `CARDDEMO.TRANSACTION_CATEGORY`

| Column | COBOL PIC | SQL Type | Business Meaning |
|--------|-----------|----------|-----------------|
| `TC_TYPE` | `X(02)` | CHAR(2) | Transaction type (PK, FK) |
| `TC_CATEGORY` | `9(04)` | INTEGER | Category code (PK) |
| `TC_DESCRIPTION` | `X(50)` | VARCHAR(50) | Category description |

### CSDB2RWY.cpy — DB2 Common Working Storage

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `WS-DISP-SQLCODE` | `----9` | Edited numeric | Display SQLCODE |
| `WS-DUMMY-DB2-INT` | `S9(4) COMP-3` | Packed decimal | DB2 connectivity test variable |
| `WS-DB2-PROCESSING-FLAG` | `X(1)` | Alphanumeric | DB2 status flag: `WS-DB2-OK` = '0', `WS-DB2-ERROR` = '1' |
| `WS-DB2-CURRENT-ACTION` | `X(72)` | Alphanumeric | Current DB2 action description |
| `WS-DSNTIAC-FORMATTED` | Group | Group | DSNTIAC error message (10×72-char lines) |

### CSDB2RPY.cpy — DB2 Common Procedures

Contains `9998-PRIMING-QUERY` paragraph to verify DB2 connectivity (SELECT 1 FROM SYSIBM.SYSDUMMY1) and `9999-FORMAT-DB2-MESSAGE` paragraph to format SQL error messages via DSNTIAC utility.

---

## 10. Export/Migration Structure

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

Supports 5 record types in a single sequential file for branch migration:

| `EXPORT-REC-TYPE` Value | Record Type | REDEFINES Target |
|------------------------|-------------|-----------------|
| `'C'` | Customer | `EXPORT-CUSTOMER-DATA` |
| `'A'` | Account | `EXPORT-ACCOUNT-DATA` |
| `'T'` | Transaction | `EXPORT-TRANSACTION-DATA` |
| `'X'` | Card Cross-Reference | `EXPORT-CARD-XREF-DATA` |
| `'D'` | Card | `EXPORT-CARD-DATA` |

Common header fields:
| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| `EXPORT-REC-TYPE` | `X(1)` | Alphanumeric | Record type discriminator |
| `EXPORT-TIMESTAMP` | `X(26)` | Alphanumeric | Export timestamp |
| `EXPORT-SEQUENCE-NUM` | `9(9) COMP` | Binary | Record sequence number |
| `EXPORT-BRANCH-ID` | `X(4)` | Alphanumeric | Source branch ID |
| `EXPORT-REGION-CODE` | `X(5)` | Alphanumeric | Region code |

---

## 11. PII (Personally Identifiable Information) Fields

This section catalogs all fields containing personally identifiable information across the estate. These fields require special handling during modernization: encryption at rest, masking in logs, access controls, and GDPR/CCPA compliance.

### 11.1 High-Sensitivity PII (Direct Identifiers)

| Copybook | Field | PIC Clause | PII Category | Risk Level | Notes |
|----------|-------|-----------|--------------|------------|-------|
| `CVCUS01Y.cpy` | `CUST-SSN` | `9(09)` | Social Security Number | **Critical** | Stored unencrypted in VSAM; must be encrypted or tokenized in target |
| `CVCUS01Y.cpy` | `CUST-GOVT-ISSUED-ID` | `X(20)` | Government ID (passport/DL) | **Critical** | Alternative national identifier |
| `CVCUS01Y.cpy` | `CUST-DOB-YYYY-MM-DD` | `X(10)` | Date of Birth | **High** | Combined with name = identity theft risk |
| `CVACT02Y.cpy` | `CARD-NUM` | `X(16)` | Credit Card Number (PAN) | **Critical** | PCI-DSS regulated; must never be stored in cleartext |
| `CVACT02Y.cpy` | `CARD-CVV-CD` | `9(03)` | Card Verification Value | **Critical** | PCI-DSS prohibits storage post-authorization |
| `CSUSR01Y.cpy` | `SEC-USR-PWD` | `X(08)` | User Password | **Critical** | Stored in plaintext; must be hashed in target system |
| `CUSTREC.cpy` | `CUST-SSN` | `9(09)` | Social Security Number | **Critical** | Duplicate layout of CVCUS01Y (used by CBSTM03A) |

### 11.2 Medium-Sensitivity PII (Indirect Identifiers / Contact Info)

| Copybook | Field | PIC Clause | PII Category | Risk Level | Notes |
|----------|-------|-----------|--------------|------------|-------|
| `CVCUS01Y.cpy` | `CUST-FIRST-NAME` | `X(25)` | Person Name | **Medium** | Combined with other fields = identity |
| `CVCUS01Y.cpy` | `CUST-MIDDLE-NAME` | `X(25)` | Person Name | **Medium** | — |
| `CVCUS01Y.cpy` | `CUST-LAST-NAME` | `X(25)` | Person Name | **Medium** | — |
| `CVCUS01Y.cpy` | `CUST-ADDR-LINE-1` | `X(50)` | Physical Address | **Medium** | Street address |
| `CVCUS01Y.cpy` | `CUST-ADDR-LINE-2` | `X(50)` | Physical Address | **Medium** | — |
| `CVCUS01Y.cpy` | `CUST-ADDR-LINE-3` | `X(50)` | Physical Address | **Medium** | — |
| `CVCUS01Y.cpy` | `CUST-ADDR-ZIP` | `X(10)` | ZIP/Postal Code | **Medium** | Can narrow to household level |
| `CVCUS01Y.cpy` | `CUST-PHONE-NUM-1` | `X(15)` | Phone Number | **Medium** | Primary contact |
| `CVCUS01Y.cpy` | `CUST-PHONE-NUM-2` | `X(15)` | Phone Number | **Medium** | Secondary contact |
| `CVCUS01Y.cpy` | `CUST-EFT-ACCOUNT-ID` | `X(10)` | Bank Account Number | **High** | EFT routing — financial PII |
| `CVACT02Y.cpy` | `CARD-EMBOSSED-NAME` | `X(50)` | Cardholder Name | **Medium** | Name as printed on card |
| `CVACT02Y.cpy` | `CARD-EXPIRAION-DATE` | `X(10)` | Card Expiration | **Medium** | PCI-DSS sensitive when combined with PAN |
| `CSUSR01Y.cpy` | `SEC-USR-ID` | `X(08)` | User Login ID | **Low** | May correlate to employee identity |
| `CSUSR01Y.cpy` | `SEC-USR-FNAME` | `X(20)` | Person Name | **Medium** | System user first name |
| `CSUSR01Y.cpy` | `SEC-USR-LNAME` | `X(20)` | Person Name | **Medium** | System user last name |

### 11.3 Account/Financial Identifiers

| Copybook | Field | PIC Clause | PII Category | Risk Level | Notes |
|----------|-------|-----------|--------------|------------|-------|
| `CVACT01Y.cpy` | `ACCT-ID` | `9(11)` | Account Number | **High** | Unique financial account identifier |
| `CVACT01Y.cpy` | `ACCT-CURR-BAL` | `S9(10)V99` | Financial Balance | **Medium** | Sensitive financial data |
| `CVACT03Y.cpy` | `XREF-CARD-NUM` | `X(16)` | Credit Card Number | **Critical** | PAN in cross-reference file |
| `CVACT03Y.cpy` | `XREF-CUST-ID` | `9(09)` | Customer ID | **Medium** | Links to full customer record |
| `CVTRA05Y.cpy` | `TRAN-CARD-NUM` | `X(16)` | Credit Card Number | **Critical** | PAN stored in every transaction record |
| `CVTRA05Y.cpy` | `TRAN-AMT` | `S9(09)V99` | Transaction Amount | **Low** | Financial data — sensitive in aggregate |
| `CVTRA06Y.cpy` | `DALYTRAN-CARD-NUM` | `X(16)` | Credit Card Number | **Critical** | PAN in daily transaction input |
| `COSTM01.CPY` | `TRNX-CARD-NUM` | `X(16)` | Credit Card Number | **Critical** | PAN in statement transaction record |
| `COCOM01Y.cpy` | `CDEMO-CARD-NUM` | `9(16)` | Credit Card Number | **Critical** | PAN passed in CICS COMMAREA |
| `COCOM01Y.cpy` | `CDEMO-CUST-ID` | `9(09)` | Customer ID | **Medium** | Customer identifier in transit |

### 11.4 PII in Export/Migration Structures

| Copybook | Field | PIC Clause | PII Category | Risk Level | Notes |
|----------|-------|-----------|--------------|------------|-------|
| `CVEXPORT.cpy` | `EXP-CUST-FIRST-NAME` | `X(25)` | Person Name | **Medium** | Name in export file |
| `CVEXPORT.cpy` | `EXP-CUST-LAST-NAME` | `X(25)` | Person Name | **Medium** | — |
| `CVEXPORT.cpy` | `EXP-CUST-SSN` | `9(09)` | Social Security Number | **Critical** | SSN in flat export file — high exposure risk |
| `CVEXPORT.cpy` | `EXP-CUST-PHONE-NUM` | `X(15)` (×2) | Phone Number | **Medium** | — |
| `CVEXPORT.cpy` | `EXP-CARD-NUM` | `X(16)` | Credit Card Number | **Critical** | PAN in export — must encrypt in transit |
| `CVEXPORT.cpy` | `EXP-TRAN-CARD-NUM` | `X(16)` | Credit Card Number | **Critical** | PAN in transaction export |

### 11.5 PII in Authorization Structures (IMS/MQ)

| Copybook | Field | PIC Clause | PII Category | Risk Level | Notes |
|----------|-------|-----------|--------------|------------|-------|
| `CCPAURQY.cpy` | `PA-RQ-CARD-NUM` | `X(16)` | Credit Card Number | **Critical** | PAN in MQ auth request message |
| `CIPAUDTY.cpy` | `PA-CARD-NUM` | `X(16)` | Credit Card Number | **Critical** | PAN in IMS auth detail segment |
| `CIPAUDTY.cpy` | `PA-MERCHANT-ID` | `X(15)` | Merchant Identifier | **Low** | Business identifier, not personal PII |
| `CIPAUSMY.cpy` | `PA-ACCT-ID` | `S9(11) COMP-3` | Account Number | **High** | Account ID in IMS summary segment |

### 11.6 Summary: PII Exposure by VSAM File

| VSAM File | Critical PII Fields | Programs with Access | Compliance Concern |
|-----------|--------------------|--------------------|-------------------|
| **CUSTFILE** | SSN, Govt ID, DOB, Name, Address, Phone | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, CBSTM03A | GDPR Art. 17 (right to erasure), CCPA |
| **CARDDAT** | Card Number (PAN), CVV, Expiration | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C/02C, COCRDLIC/SLC/UPC, COPAUA0C | PCI-DSS Req. 3 (protect stored data) |
| **TRANSACT** | Card Number (PAN) per transaction | CBTRN01C/02C/03C, CBACT04C, COTRN00C/01C/02C, CBEXPORT | PCI-DSS — PAN in every record |
| **XREFFILE** | Card Number (PAN), Customer ID | CBACT03C/04C, CBTRN01C/02C/03C, CBSTM03A, CBEXPORT | Links PAN to customer identity |
| **USRSEC** | Password (plaintext), User names | COSGN00C, COUSR00C/01C/02C/03C | Credential exposure risk |
| **EXPORT-FILE** | All PII (SSN, PAN, names, addresses) | CBEXPORT, CBIMPORT | Highest risk — all PII in one sequential file |
| **IMS PAUTBDB** | Card Number (PAN), Account ID | CBPAUP0C, COPAUA0C, DBUNLDGS, PAUDBLOD/PAUDBUNL | PAN in IMS segments |
| **MQ Queues** | Card Number (PAN), Auth details | COPAUA0C, COACCT01, CODATE01 | PAN in transit (MQ messages) |

### 11.7 Modernization Recommendations for PII

| Priority | Action | Affected Components |
|----------|--------|-------------------|
| **P0** | Tokenize or encrypt PAN (card numbers) at rest — currently stored in plaintext across 6+ files | CARDDAT, TRANSACT, XREFFILE, EXPORT-FILE, IMS PAUTBDB |
| **P0** | Remove CVV storage entirely — PCI-DSS prohibits retention post-authorization | CARDDAT (CARD-CVV-CD) |
| **P0** | Hash passwords — currently plaintext in USRSEC | USRSEC (SEC-USR-PWD) |
| **P1** | Encrypt SSN at rest — implement field-level encryption or tokenization | CUSTFILE (CUST-SSN), EXPORT-FILE |
| **P1** | Encrypt export file in transit — contains all PII in a single flat file | CBEXPORT/CBIMPORT pipeline |
| **P2** | Implement data masking for non-production environments | All files containing PII |
| **P2** | Add access audit logging for PII field reads | All CICS programs reading CUSTFILE/CARDDAT |
| **P3** | Implement GDPR right-to-erasure capability | CUSTFILE, CARDDAT, TRANSACT, XREFFILE (cascading delete) |
