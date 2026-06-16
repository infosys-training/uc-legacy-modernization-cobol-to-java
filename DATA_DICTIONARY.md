# DATA DICTIONARY — CardDemo COBOL Estate

> **Generated:** 2026-06-16 | **Total Copybooks:** 41 data + 21 BMS = 62  
> **Business Entities:** Account, Customer, Card, Transaction, Authorization, Security, Export

---

## Table of Contents

1. [Account Entity](#1-account-entity)
2. [Customer Entity](#2-customer-entity)
3. [Card Entity](#3-card-entity)
4. [Card–Account Cross-Reference Entity](#4-cardaccount-cross-reference-entity)
5. [Transaction Entity](#5-transaction-entity)
6. [Transaction Metadata Entities](#6-transaction-metadata-entities)
7. [Authorization Entity (IMS)](#7-authorization-entity-ims)
8. [User Security Entity](#8-user-security-entity)
9. [Inter-Program Communication (COMMAREA)](#9-inter-program-communication-commarea)
10. [UI / Screen Support Copybooks](#10-ui--screen-support-copybooks)
11. [Utility / Shared Copybooks](#11-utility--shared-copybooks)
12. [Export / Migration Structure](#12-export--migration-structure)
13. [DB2 Procedure Copybooks](#13-db2-procedure-copybooks)
14. [IMS Infrastructure Copybooks](#14-ims-infrastructure-copybooks)
15. [MQ Infrastructure Copybooks](#15-mq-infrastructure-copybooks)

---

## 1. Account Entity

### `CVACT01Y.cpy` — Account Record (RECLN 300)

| Field Name | PIC Clause | Type | Business Meaning | Validation |
|------------|-----------|------|------------------|------------|
| `ACCT-ID` | `9(11)` | Numeric | Unique account identifier (11-digit) | Primary key; must be numeric |
| `ACCT-ACTIVE-STATUS` | `X(01)` | Alpha | Account status flag | 'Y' = active |
| `ACCT-CURR-BAL` | `S9(10)V99` | Signed decimal | Current account balance | Signed; 2 decimal places |
| `ACCT-CREDIT-LIMIT` | `S9(10)V99` | Signed decimal | Maximum credit limit | Must be > 0 |
| `ACCT-CASH-CREDIT-LIMIT` | `S9(10)V99` | Signed decimal | Maximum cash advance limit | Must be > 0 |
| `ACCT-OPEN-DATE` | `X(10)` | Date string | Account opening date | Format: YYYY-MM-DD; validated by CSUTLDPY |
| `ACCT-EXPIRAION-DATE` | `X(10)` | Date string | Account expiration date | Format: YYYY-MM-DD; must be > open date |
| `ACCT-REISSUE-DATE` | `X(10)` | Date string | Card reissue date | Format: YYYY-MM-DD |
| `ACCT-CURR-CYC-CREDIT` | `S9(10)V99` | Signed decimal | Current billing cycle credits | Accumulated within cycle |
| `ACCT-CURR-CYC-DEBIT` | `S9(10)V99` | Signed decimal | Current billing cycle debits | Accumulated within cycle |
| `ACCT-ADDR-ZIP` | `X(10)` | Alpha | Account holder ZIP code | Validated against CSLKPCDY ZIP prefixes |
| `ACCT-GROUP-ID` | `X(10)` | Alpha | Discount / rate group identifier | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| `FILLER` | `X(178)` | — | Reserved space | Padding to 300 bytes |

**VSAM Key:** ACCT-ID (11 bytes, offset 0)  
**Used by:** CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBEXPORT, CBIMPORT, CBSTM03A/B, COACTUPC, COACTVWC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C/1C

---

## 2. Customer Entity

### `CVCUS01Y.cpy` — Customer Record (RECLN 500)

| Field Name | PIC Clause | Type | Business Meaning | Validation |
|------------|-----------|------|------------------|------------|
| `CUST-ID` | `9(09)` | Numeric | Unique customer ID (9-digit) | Primary key |
| `CUST-FIRST-NAME` | `X(25)` | Alpha | Customer first name | Required; non-blank |
| `CUST-MIDDLE-NAME` | `X(25)` | Alpha | Customer middle name | Optional |
| `CUST-LAST-NAME` | `X(25)` | Alpha | Customer last name | Required; non-blank |
| `CUST-ADDR-LINE-1` | `X(50)` | Alpha | Address line 1 | Required |
| `CUST-ADDR-LINE-2` | `X(50)` | Alpha | Address line 2 | Optional |
| `CUST-ADDR-LINE-3` | `X(50)` | Alpha | Address line 3 | Optional |
| `CUST-ADDR-STATE-CD` | `X(02)` | Alpha | US state code | Validated against 50 states in CSLKPCDY |
| `CUST-ADDR-COUNTRY-CD` | `X(03)` | Alpha | Country code | e.g., 'US ' |
| `CUST-ADDR-ZIP` | `X(10)` | Alpha | ZIP / postal code | Validated against state-ZIP prefix table in CSLKPCDY |
| `CUST-PHONE-NUM-1` | `X(15)` | Alpha | Primary phone number | Validated against state-phone prefix in CSLKPCDY |
| `CUST-PHONE-NUM-2` | `X(15)` | Alpha | Secondary phone number | Optional |
| `CUST-SSN` | `9(09)` | Numeric | Social Security Number | **PII** — 9 digits, validated for numeric |
| `CUST-GOVT-ISSUED-ID` | `X(20)` | Alpha | Government-issued ID (e.g., passport) | Optional |
| `CUST-DOB-YYYY-MM-DD` | `X(10)` | Date string | Date of birth | Validated by EDIT-DATE-OF-BIRTH (cannot be future) |
| `CUST-EFT-ACCOUNT-ID` | `X(10)` | Alpha | EFT / bank account ID for payments | Optional |
| `CUST-PRI-CARD-HOLDER-IND` | `X(01)` | Alpha | Primary cardholder indicator | 'Y' / 'N' |
| `CUST-FICO-CREDIT-SCORE` | `9(03)` | Numeric | FICO credit score | Range: 300–850 |
| `FILLER` | `X(168)` | — | Reserved space | Padding to 500 bytes |

### `CUSTREC.cpy` — Customer Record (Alternate layout, RECLN 500)

Identical field structure to CVCUS01Y.cpy. Used specifically by `CBSTM03A.CBL` for statement generation. Field-by-field copy with same PIC clauses.

**VSAM Key:** CUST-ID (9 bytes, offset 0)  
**Used by:** CBEXPORT, CBIMPORT, CBTRN01C, CBSTM03A, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C/1C

---

## 3. Card Entity

### `CVACT02Y.cpy` — Card Record (RECLN 150)

| Field Name | PIC Clause | Type | Business Meaning | Validation |
|------------|-----------|------|------------------|------------|
| `CARD-NUM` | `X(16)` | Alpha | Card number (16-character PAN) | Primary key; **PII/PCI** |
| `CARD-ACCT-ID` | `9(11)` | Numeric | Linked account identifier | FK → ACCT-ID |
| `CARD-CVV-CD` | `9(03)` | Numeric | Card verification value | 3 digits; **PCI-sensitive** |
| `CARD-EMBOSSED-NAME` | `X(50)` | Alpha | Name printed on card | Non-blank |
| `CARD-EXPIRAION-DATE` | `X(10)` | Date string | Card expiration date | Format: YYYY-MM-DD |
| `CARD-ACTIVE-STATUS` | `X(01)` | Alpha | Card active flag | 'Y' = active, 'N' = inactive |
| `FILLER` | `X(59)` | — | Reserved | Padding to 150 bytes |

**VSAM Key:** CARD-NUM (16 bytes, offset 0)  
**AIX:** CARD-ACCT-ID (alternate index for account-based card lookup)  
**Used by:** CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC

---

## 4. Card–Account Cross-Reference Entity

### `CVACT03Y.cpy` — Card–Account XREF (RECLN 50)

| Field Name | PIC Clause | Type | Business Meaning | Validation |
|------------|-----------|------|------------------|------------|
| `XREF-CARD-NUM` | `X(16)` | Alpha | Card number (FK → CARD-NUM) | Primary key |
| `XREF-CUST-ID` | `9(09)` | Numeric | Customer ID (FK → CUST-ID) | Must exist in CUSTFILE |
| `XREF-ACCT-ID` | `9(11)` | Numeric | Account ID (FK → ACCT-ID) | Must exist in ACCTFILE |
| `FILLER` | `X(14)` | — | Reserved | Padding to 50 bytes |

**VSAM Key:** XREF-CARD-NUM (16 bytes, offset 0)  
**AIX:** XREF-ACCT-ID (alternate index for account-based lookup)  
**Used by:** CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A/B, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COTRN02C, COBIL00C, COPAUA0C

---

## 5. Transaction Entity

### `CVTRA05Y.cpy` — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Type | Business Meaning | Validation |
|------------|-----------|------|------------------|------------|
| `TRAN-ID` | `X(16)` | Alpha | Transaction identifier | Primary key (auto-generated sequence) |
| `TRAN-TYPE-CD` | `X(02)` | Alpha | Transaction type code | FK → TRAN-TYPE (CVTRA03Y) |
| `TRAN-CAT-CD` | `9(04)` | Numeric | Transaction category code | FK → TRAN-CAT-CD (CVTRA04Y) |
| `TRAN-SOURCE` | `X(10)` | Alpha | Transaction source (POS, ATM, Online) | Informational |
| `TRAN-DESC` | `X(100)` | Alpha | Transaction description | Free text |
| `TRAN-AMT` | `S9(09)V99` | Signed decimal | Transaction amount | Signed; credits positive, debits negative |
| `TRAN-MERCHANT-ID` | `9(09)` | Numeric | Merchant identifier | Informational |
| `TRAN-MERCHANT-NAME` | `X(50)` | Alpha | Merchant name | Informational |
| `TRAN-MERCHANT-CITY` | `X(50)` | Alpha | Merchant city | Informational |
| `TRAN-MERCHANT-ZIP` | `X(10)` | Alpha | Merchant ZIP code | Informational |
| `TRAN-CARD-NUM` | `X(16)` | Alpha | Card used for transaction | FK → CARD-NUM; **PII** |
| `TRAN-ORIG-TS` | `X(26)` | Timestamp | Original transaction timestamp | YYYY-MM-DD-HH.MM.SS.FFFFFF |
| `TRAN-PROC-TS` | `X(26)` | Timestamp | Processing timestamp | YYYY-MM-DD-HH.MM.SS.FFFFFF |
| `FILLER` | `X(20)` | — | Reserved | Padding to 350 bytes |

### `CVTRA06Y.cpy` — Daily Transaction Record (RECLN 350)

Identical layout to CVTRA05Y but prefixed with `DALYTRAN-` instead of `TRAN-`. Represents raw daily batch input before validation/posting.

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `DALYTRAN-ID` | `X(16)` | Alpha | Daily transaction ID |
| `DALYTRAN-TYPE-CD` | `X(02)` | Alpha | Transaction type code |
| `DALYTRAN-CAT-CD` | `9(04)` | Numeric | Category code |
| `DALYTRAN-SOURCE` | `X(10)` | Alpha | Source identifier |
| `DALYTRAN-DESC` | `X(100)` | Alpha | Description |
| `DALYTRAN-AMT` | `S9(09)V99` | Signed decimal | Amount |
| `DALYTRAN-MERCHANT-ID` | `9(09)` | Numeric | Merchant ID |
| `DALYTRAN-MERCHANT-NAME` | `X(50)` | Alpha | Merchant name |
| `DALYTRAN-MERCHANT-CITY` | `X(50)` | Alpha | Merchant city |
| `DALYTRAN-MERCHANT-ZIP` | `X(10)` | Alpha | Merchant ZIP |
| `DALYTRAN-CARD-NUM` | `X(16)` | Alpha | Card number |
| `DALYTRAN-ORIG-TS` | `X(26)` | Timestamp | Original timestamp |
| `DALYTRAN-PROC-TS` | `X(26)` | Timestamp | Processing timestamp |
| `FILLER` | `X(20)` | — | Reserved |

### `COSTM01.CPY` — Transaction Record for Statement Reporting

Repackaged transaction record with composite key for statement generation.

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `TRNX-CARD-NUM` | `X(16)` | Alpha | Card number (part of composite key) |
| `TRNX-ID` | `X(16)` | Alpha | Transaction ID (part of composite key) |
| `TRNX-TYPE-CD` | `X(02)` | Alpha | Transaction type |
| `TRNX-CAT-CD` | `9(04)` | Numeric | Category code |
| `TRNX-SOURCE` | `X(10)` | Alpha | Source |
| `TRNX-DESC` | `X(100)` | Alpha | Description |
| `TRNX-AMT` | `S9(09)V99` | Signed decimal | Amount |
| `TRNX-MERCHANT-ID` | `9(09)` | Numeric | Merchant ID |
| `TRNX-MERCHANT-NAME` | `X(50)` | Alpha | Merchant name |
| `TRNX-MERCHANT-CITY` | `X(50)` | Alpha | Merchant city |
| `TRNX-MERCHANT-ZIP` | `X(10)` | Alpha | Merchant ZIP |
| `TRNX-ORIG-TS` | `X(26)` | Timestamp | Original timestamp |
| `TRNX-PROC-TS` | `X(26)` | Timestamp | Processing timestamp |

**Used by:** CBSTM03A (statement generation)

---

## 6. Transaction Metadata Entities

### `CVTRA01Y.cpy` — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Type | Business Meaning | Validation |
|------------|-----------|------|------------------|------------|
| `TRANCAT-ACCT-ID` | `9(11)` | Numeric | Account ID (part of composite key) | FK → ACCT-ID |
| `TRANCAT-TYPE-CD` | `X(02)` | Alpha | Transaction type (part of key) | FK → TRAN-TYPE |
| `TRANCAT-CD` | `9(04)` | Numeric | Category code (part of key) | FK → TRAN-CAT-CD |
| `TRAN-CAT-BAL` | `S9(09)V99` | Signed decimal | Running balance per category | Updated by CBTRN02C |
| `FILLER` | `X(22)` | — | Reserved | |

**VSAM Key:** Composite (TRANCAT-ACCT-ID + TRANCAT-TYPE-CD + TRANCAT-CD, 17 bytes)

### `CVTRA02Y.cpy` — Discount Group (RECLN 50)

| Field Name | PIC Clause | Type | Business Meaning | Validation |
|------------|-----------|------|------------------|------------|
| `DIS-ACCT-GROUP-ID` | `X(10)` | Alpha | Account group (part of composite key) | FK → ACCT-GROUP-ID |
| `DIS-TRAN-TYPE-CD` | `X(02)` | Alpha | Transaction type (part of key) | FK → TRAN-TYPE |
| `DIS-TRAN-CAT-CD` | `9(04)` | Numeric | Category code (part of key) | FK → TRAN-CAT-CD |
| `DIS-INT-RATE` | `S9(04)V99` | Signed decimal | Interest rate for this group/type/category | Percentage (e.g., 18.99) |
| `FILLER` | `X(28)` | — | Reserved | |

**Used by:** CBACT04C (interest calculation)

### `CVTRA03Y.cpy` — Transaction Type (RECLN 60)

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `TRAN-TYPE` | `X(02)` | Alpha | Transaction type code (primary key) |
| `TRAN-TYPE-DESC` | `X(50)` | Alpha | Description (e.g., "Purchase", "Cash Advance") |
| `FILLER` | `X(08)` | — | Reserved |

### `CVTRA04Y.cpy` — Transaction Category (RECLN 60)

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `TRAN-TYPE-CD` | `X(02)` | Alpha | Type code (part of composite key) |
| `TRAN-CAT-CD` | `9(04)` | Numeric | Category code (part of key) |
| `TRAN-CAT-TYPE-DESC` | `X(50)` | Alpha | Category description |
| `FILLER` | `X(04)` | — | Reserved |

### `CVTRA07Y.cpy` — Transaction Report Layout

Print-formatted structure for the daily transaction detail report.

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `REPT-SHORT-NAME` | `X(38)` | Alpha | Report short name ('DALYREPT') |
| `REPT-LONG-NAME` | `X(41)` | Alpha | Full report name |
| `REPT-DATE-HEADER` | `X(12)` | Alpha | Date range header literal |
| `REPT-START-DATE` | `X(10)` | Date | Report start date |
| `REPT-END-DATE` | `X(10)` | Date | Report end date |
| `TRAN-REPORT-TRANS-ID` | `X(16)` | Alpha | Transaction ID |
| `TRAN-REPORT-ACCOUNT-ID` | `X(11)` | Alpha | Account ID |
| `TRAN-REPORT-TYPE-CD` | `X(02)` | Alpha | Type code |
| `TRAN-REPORT-TYPE-DESC` | `X(15)` | Alpha | Type description |
| `TRAN-REPORT-CAT-CD` | `9(04)` | Numeric | Category code |
| `TRAN-REPORT-CAT-DESC` | `X(29)` | Alpha | Category description |
| `TRAN-REPORT-SOURCE` | `X(10)` | Alpha | Source |
| `TRAN-REPORT-AMT` | `-ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Formatted amount |
| `REPT-PAGE-TOTAL` | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Page total |
| `REPT-ACCOUNT-TOTAL` | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Account total |
| `REPT-GRAND-TOTAL` | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Grand total |

**Used by:** CBTRN03C (transaction report print)

---

## 7. Authorization Entity (IMS)

### `CIPAUSMY.cpy` — Pending Authorization Summary (IMS Root Segment)

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `PA-ACCT-ID` | `S9(11) COMP-3` | Packed decimal | Account ID (root key) |
| `PA-CUST-ID` | `9(09)` | Numeric | Customer ID |
| `PA-AUTH-STATUS` | `X(01)` | Alpha | Overall authorization status |
| `PA-ACCOUNT-STATUS` | `X(02) OCCURS 5` | Alpha array | Account status codes (5 entries) |
| `PA-CREDIT-LIMIT` | `S9(09)V99 COMP-3` | Packed decimal | Credit limit |
| `PA-CASH-LIMIT` | `S9(09)V99 COMP-3` | Packed decimal | Cash advance limit |
| `PA-CREDIT-BALANCE` | `S9(09)V99 COMP-3` | Packed decimal | Credit balance |
| `PA-CASH-BALANCE` | `S9(09)V99 COMP-3` | Packed decimal | Cash balance |
| `PA-APPROVED-AUTH-CNT` | `S9(04) COMP` | Binary | Count of approved authorizations |
| `PA-DECLINED-AUTH-CNT` | `S9(04) COMP` | Binary | Count of declined authorizations |
| `PA-APPROVED-AUTH-AMT` | `S9(09)V99 COMP-3` | Packed decimal | Total approved amount |
| `PA-DECLINED-AUTH-AMT` | `S9(09)V99 COMP-3` | Packed decimal | Total declined amount |
| `FILLER` | `X(34)` | — | Reserved |

### `CIPAUDTY.cpy` — Pending Authorization Detail (IMS Dependent Segment)

| Field Name | PIC Clause | Type | Business Meaning | Validation |
|------------|-----------|------|------------------|------------|
| `PA-AUTH-DATE-9C` | `S9(05) COMP-3` | Packed | Auth date (part of segment key) | |
| `PA-AUTH-TIME-9C` | `S9(09) COMP-3` | Packed | Auth time (part of segment key) | |
| `PA-AUTH-ORIG-DATE` | `X(06)` | Alpha | Original date (display) | MMDDYY |
| `PA-AUTH-ORIG-TIME` | `X(06)` | Alpha | Original time (display) | HHMMSS |
| `PA-CARD-NUM` | `X(16)` | Alpha | Card number | FK → CARD-NUM |
| `PA-AUTH-TYPE` | `X(04)` | Alpha | Authorization type | |
| `PA-CARD-EXPIRY-DATE` | `X(04)` | Alpha | Card expiry (YYMM) | |
| `PA-MESSAGE-TYPE` | `X(06)` | Alpha | ISO message type | |
| `PA-MESSAGE-SOURCE` | `X(06)` | Alpha | Message source system | |
| `PA-AUTH-ID-CODE` | `X(06)` | Alpha | Authorization ID code | |
| `PA-AUTH-RESP-CODE` | `X(02)` | Alpha | Response code | `88 PA-AUTH-APPROVED VALUE '00'` |
| `PA-AUTH-RESP-REASON` | `X(04)` | Alpha | Response reason code | |
| `PA-PROCESSING-CODE` | `9(06)` | Numeric | ISO processing code | |
| `PA-TRANSACTION-AMT` | `S9(10)V99 COMP-3` | Packed decimal | Requested amount | |
| `PA-APPROVED-AMT` | `S9(10)V99 COMP-3` | Packed decimal | Approved amount | |
| `PA-MERCHANT-CATAGORY-CODE` | `X(04)` | Alpha | MCC code | |
| `PA-ACQR-COUNTRY-CODE` | `X(03)` | Alpha | Acquirer country code | |
| `PA-POS-ENTRY-MODE` | `9(02)` | Numeric | POS entry mode | |
| `PA-MERCHANT-ID` | `X(15)` | Alpha | Merchant ID | |
| `PA-MERCHANT-NAME` | `X(22)` | Alpha | Merchant name | |
| `PA-MERCHANT-CITY` | `X(13)` | Alpha | Merchant city | |
| `PA-MERCHANT-STATE` | `X(02)` | Alpha | Merchant state | |
| `PA-MERCHANT-ZIP` | `X(09)` | Alpha | Merchant ZIP | |
| `PA-TRANSACTION-ID` | `X(15)` | Alpha | Transaction ID | |
| `PA-MATCH-STATUS` | `X(01)` | Alpha | Match status | `88` values: P(ending), D(eclined), E(xpired), M(atched) |
| `PA-AUTH-FRAUD` | `X(01)` | Alpha | Fraud indicator | `88` values: F(raud confirmed), R(emoved) |
| `PA-FRAUD-RPT-DATE` | `X(08)` | Alpha | Fraud report date | YYYYMMDD |
| `FILLER` | `X(17)` | — | Reserved | |

### `CCPAURQY.cpy` — Authorization Request (MQ Message)

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `PA-RQ-AUTH-DATE` | `X(06)` | Alpha | Request date |
| `PA-RQ-AUTH-TIME` | `X(06)` | Alpha | Request time |
| `PA-RQ-CARD-NUM` | `X(16)` | Alpha | Card number |
| `PA-RQ-AUTH-TYPE` | `X(04)` | Alpha | Auth type |
| `PA-RQ-CARD-EXPIRY-DATE` | `X(04)` | Alpha | Card expiry |
| `PA-RQ-MESSAGE-TYPE` | `X(06)` | Alpha | ISO message type |
| `PA-RQ-MESSAGE-SOURCE` | `X(06)` | Alpha | Source system |
| `PA-RQ-PROCESSING-CODE` | `9(06)` | Numeric | Processing code |
| `PA-RQ-TRANSACTION-AMT` | `+9(10).99` | Edited numeric | Transaction amount |
| `PA-RQ-MERCHANT-CATAGORY-CODE` | `X(04)` | Alpha | MCC code |
| `PA-RQ-ACQR-COUNTRY-CODE` | `X(03)` | Alpha | Acquirer country |
| `PA-RQ-POS-ENTRY-MODE` | `9(02)` | Numeric | POS entry mode |
| `PA-RQ-MERCHANT-ID` | `X(15)` | Alpha | Merchant ID |
| `PA-RQ-MERCHANT-NAME` | `X(22)` | Alpha | Merchant name |
| `PA-RQ-MERCHANT-CITY` | `X(13)` | Alpha | Merchant city |
| `PA-RQ-MERCHANT-STATE` | `X(02)` | Alpha | Merchant state |
| `PA-RQ-MERCHANT-ZIP` | `X(09)` | Alpha | Merchant ZIP |
| `PA-RQ-TRANSACTION-ID` | `X(15)` | Alpha | Transaction ID |

### `CCPAURLY.cpy` — Authorization Response (MQ Message)

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `PA-RL-CARD-NUM` | `X(16)` | Alpha | Card number |
| `PA-RL-TRANSACTION-ID` | `X(15)` | Alpha | Transaction ID |
| `PA-RL-AUTH-ID-CODE` | `X(06)` | Alpha | Authorization ID |
| `PA-RL-AUTH-RESP-CODE` | `X(02)` | Alpha | Response code |
| `PA-RL-AUTH-RESP-REASON` | `X(04)` | Alpha | Response reason |
| `PA-RL-APPROVED-AMT` | `+9(10).99` | Edited numeric | Approved amount |

### `CCPAUERY.cpy` — Authorization Error Log

| Field Name | PIC Clause | Type | Business Meaning | Validation |
|------------|-----------|------|------------------|------------|
| `ERR-DATE` | `X(06)` | Alpha | Error date | |
| `ERR-TIME` | `X(06)` | Alpha | Error time | |
| `ERR-APPLICATION` | `X(08)` | Alpha | Application name | |
| `ERR-PROGRAM` | `X(08)` | Alpha | Program name | |
| `ERR-LOCATION` | `X(04)` | Alpha | Code location | |
| `ERR-LEVEL` | `X(01)` | Alpha | Severity level | `88` values: L(og), I(nfo), W(arning), C(ritical) |
| `ERR-SUBSYSTEM` | `X(01)` | Alpha | Subsystem origin | `88` values: A(pp), C(ICS), I(MS), D(B2), M(Q), F(ile) |
| `ERR-CODE-1` | `X(09)` | Alpha | Primary error code | |
| `ERR-CODE-2` | `X(09)` | Alpha | Secondary error code | |
| `ERR-MESSAGE` | `X(50)` | Alpha | Error message text | |
| `ERR-EVENT-KEY` | `X(20)` | Alpha | Event correlation key | |

---

## 8. User Security Entity

### `CSUSR01Y.cpy` — User Security Record (RECLN 80)

| Field Name | PIC Clause | Type | Business Meaning | Validation / Notes |
|------------|-----------|------|------------------|--------------------|
| `SEC-USR-ID` | `X(08)` | Alpha | User login ID | Primary key |
| `SEC-USR-FNAME` | `X(20)` | Alpha | First name | |
| `SEC-USR-LNAME` | `X(20)` | Alpha | Last name | |
| `SEC-USR-PWD` | `X(08)` | Alpha | Password | ⚠️ **SECURITY RISK**: stored in plain text |
| `SEC-USR-TYPE` | `X(01)` | Alpha | User type | 'A' = Admin, 'U' = Regular User |
| `SEC-USR-FILLER` | `X(23)` | — | Reserved | |

**⚠️ Modernization Note:** Passwords are stored in plain text. Migration must implement hashing (e.g., bcrypt) and enforce password complexity rules.

### `UNUSED1Y.cpy` — Unused Record Layout

Identical structure to CSUSR01Y with `UNUSED-` prefix. Appears to be a deprecated/placeholder copy.

---

## 9. Inter-Program Communication (COMMAREA)

### `COCOM01Y.cpy` — CICS COMMAREA Structure

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `CDEMO-FROM-TRANID` | `X(04)` | Alpha | Source transaction ID |
| `CDEMO-FROM-PROGRAM` | `X(08)` | Alpha | Source program name |
| `CDEMO-TO-TRANID` | `X(04)` | Alpha | Target transaction ID |
| `CDEMO-TO-PROGRAM` | `X(08)` | Alpha | Target program name |
| `CDEMO-USER-ID` | `X(08)` | Alpha | Logged-in user ID |
| `CDEMO-USER-TYPE` | `X(01)` | Alpha | User type (A/U) |
| `CDEMO-PGM-CONTEXT` | `X(256)` | Alpha | Program-specific context data |
| `CDEMO-CUSTOMER-INFO` | *(group)* | — | Customer-related context fields |
| `CDEMO-ACCOUNT-INFO` | *(group)* | — | Account-related context fields |
| `CDEMO-CARD-INFO` | *(group)* | — | Card-related context fields |
| `CDEMO-MORE-INFO` | *(group)* | — | Pagination and filter context |

**Used by:** All 21 CICS online programs for inter-program data passing via XCTL

### `CVCRD01Y.cpy` — Card/AID Work Area

| Field Name | PIC Clause | Type | Business Meaning |
|------------|-----------|------|------------------|
| `CCARD-AID` | `X(5)` | Alpha | Attention Identifier (key pressed) |
| (88-level conditions) | — | — | ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| `CCARD-NEXT-PROG` | `X(8)` | Alpha | Next program to XCTL to |
| `CCARD-NEXT-MAPSET` | `X(7)` | Alpha | Next BMS mapset |
| `CCARD-NEXT-MAP` | `X(7)` | Alpha | Next BMS map |
| `CCARD-ERROR-MSG` | `X(75)` | Alpha | Error message for display |
| `CCARD-RETURN-MSG` | `X(75)` | Alpha | Return message |
| `CC-ACCT-ID` | `X(11)` | Alpha | Account ID (work field) |
| `CC-CARD-NUM` | `X(16)` | Alpha | Card number (work field) |
| `CC-CUST-ID` | `X(09)` | Alpha | Customer ID (work field) |

---

## 10. UI / Screen Support Copybooks

### `COTTL01Y.cpy` — Screen Titles

| Field Name | PIC Clause | Value | Purpose |
|------------|-----------|-------|---------|
| `CCDA-TITLE01` | `X(40)` | `'AWS Mainframe Modernization'` | Header line 1 |
| `CCDA-TITLE02` | `X(40)` | `'CardDemo'` | Header line 2 |
| `CCDA-THANK-YOU` | `X(40)` | `'Thank you for using CCDA...'` | Exit message |

### `CSDAT01Y.cpy` — Date/Time Working Storage

Provides formatted date/time variables for screen display.

| Field Name | PIC Clause | Format | Purpose |
|------------|-----------|--------|---------|
| `WS-CURDATE-YEAR` | `9(04)` | YYYY | Current year |
| `WS-CURDATE-MONTH` | `9(02)` | MM | Current month |
| `WS-CURDATE-DAY` | `9(02)` | DD | Current day |
| `WS-CURDATE-MM-DD-YY` | *(group)* | MM/DD/YY | Formatted date for display |
| `WS-CURTIME-HH-MM-SS` | *(group)* | HH:MM:SS | Formatted time for display |
| `WS-TIMESTAMP` | *(group)* | YYYY-MM-DD HH:MM:SS.FFFFFF | Full timestamp |

### `CSMSG01Y.cpy` / `CSMSG02Y.cpy` — Message Area Structures

Standard message display areas used by CICS programs for user feedback messages.

### `COADM02Y.cpy` — Admin Menu Option Definitions

Defines admin menu option numbers, names, and target program names.

### `COMEN02Y.cpy` — Main Menu Option Definitions

| Field Pattern | PIC Clause | Purpose |
|---------------|-----------|---------|
| `CDEMO-MENU-OPT-NUM` | `9(02)` | Menu option number (01–12) |
| `CDEMO-MENU-OPT-NAME` | `X(35)` | Option display name |
| `CDEMO-MENU-OPT-PGMNAME` | `X(08)` | Target COBOL program |
| `CDEMO-MENU-OPT-USRTYPE` | `X(01)` | Required user type ('A'=admin, 'U'=user) |

12 menu options defined, including: Account View, Account Update, Card List, Card View, Card Update, Transaction List, Transaction Add, Bill Payment, Report Request, Pending Auth View, Transaction Type List.

### `CODATECN.cpy` — Date Conversion Parameters

Used by CBACT01C for assembler date formatting call (COBDATFT).

### `CSSETATY.cpy` — Screen Attribute Setting (COPY REPLACING)

Template copybook used with `COPY REPLACING` (38× in COACTUPC) to set screen field attributes (color, protection) based on validation flags.

### `CSSTRPFY.cpy` — PFKey Store Paragraph

Procedural copybook that maps EIBAID to CCARD-AID values (EVALUATE TRUE with all 24 PF keys).

### `CSLKPCDY.cpy` — State/ZIP/Phone Validation Lookup (1,318 lines)

Comprehensive reference table for field validation:
- All 50 US state codes
- Valid ZIP code prefixes per state
- Valid phone area code prefixes per state

**Used by:** COACTUPC for address/phone validation

---

## 11. Utility / Shared Copybooks

### `CSUTLDWY.cpy` — Date Validation Working Storage (89 lines)

Working storage fields used by CSUTLDPY date validation procedures.

| Field Name | PIC Clause | Purpose |
|------------|-----------|---------|
| `WS-EDIT-DATE-CCYYMMDD` | *(group)* | Date under validation (CC, YY, MM, DD) |
| `WS-EDIT-DATE-FLGS` | *(group)* | Validation result flags |
| `WS-DATE-FORMAT` | `X(08)` | Date format mask ('YYYYMMDD') |
| `WS-DATE-VALIDATION-RESULT` | *(group)* | LE validation result |

88-level conditions:
- `THIS-CENTURY` (VALUE 20), `LAST-CENTURY` (VALUE 19)
- `WS-VALID-MONTH` (VALUES 1 THROUGH 12)
- `WS-31-DAY-MONTH` (VALUES 1, 3, 5, 7, 8, 10, 12)
- `WS-FEBRUARY` (VALUE 2)
- `WS-VALID-DAY` (VALUES 1 THROUGH 31)

### `CSUTLDPY.cpy` — Date Validation Procedures (375 lines)

Procedure Division copybook implementing comprehensive date validation:
- `EDIT-DATE-CCYYMMDD` — Main entry point
- `EDIT-YEAR-CCYY` — Century/year validation (19xx or 20xx only)
- `EDIT-MONTH` — Month range validation (1–12)
- `EDIT-DAY` — Day range validation (1–31, month-aware, leap-year-aware)
- `EDIT-DAY-MONTH-YEAR` — Cross-field validation (e.g., no Feb 30)
- `EDIT-DATE-OF-BIRTH` — DOB cannot be in future (uses CURRENT-DATE intrinsic)

---

## 12. Export / Migration Structure

### `CVEXPORT.cpy` — Multi-Record Export Layout (RECLN 500)

Polymorphic record structure using REDEFINES for branch migration data transfer.

**Header fields (common to all record types):**

| Field Name | PIC Clause | Type | Purpose |
|------------|-----------|------|---------|
| `EXPORT-REC-TYPE` | `X(1)` | Alpha | Record type identifier (C=Customer, A=Account, T=Transaction, X=Xref, R=Card) |
| `EXPORT-TIMESTAMP` | `X(26)` | Timestamp | Export timestamp |
| `EXPORT-SEQUENCE-NUM` | `9(9) COMP` | Binary | Sequence number |
| `EXPORT-BRANCH-ID` | `X(4)` | Alpha | Source branch ID |
| `EXPORT-REGION-CODE` | `X(5)` | Alpha | Region code |
| `EXPORT-RECORD-DATA` | `X(460)` | Alpha | Record payload (REDEFINES below) |

**REDEFINES variants:** EXPORT-CUSTOMER-DATA, EXPORT-ACCOUNT-DATA, EXPORT-TRANSACTION-DATA, EXPORT-CARD-XREF-DATA, EXPORT-CARD-DATA — each uses COMP/COMP-3 for storage optimization.

---

## 13. DB2 Procedure Copybooks

### `CSDB2RPY.cpy` — DB2 Read Procedures (89 lines)

Procedure Division copybook for DB2 read-only operations:
- `9998-PRIMING-QUERY` — Connectivity check (`SELECT 1 FROM SYSIBM.SYSDUMMY1`)
- `9999-FORMAT-DB2-MESSAGE` — Error formatting via DSNTIAC utility

### `CSDB2RWY.cpy` — DB2 Read/Write Procedures

Extended DB2 procedures including INSERT, UPDATE, DELETE error handling.

**Used by:** COTRTLIC, COTRTUPC, COBTUPDT

---

## 14. IMS Infrastructure Copybooks

### `IMSFUNCS.cpy` — IMS DL/I Function Codes

| Field Name | PIC Clause | Value | Purpose |
|------------|-----------|-------|---------|
| `FUNC-GU` | `X(04)` | 'GU  ' | Get Unique |
| `FUNC-GHU` | `X(04)` | 'GHU ' | Get Hold Unique |
| `FUNC-GN` | `X(04)` | 'GN  ' | Get Next |
| `FUNC-GHN` | `X(04)` | 'GHN ' | Get Hold Next |
| `FUNC-GNP` | `X(04)` | 'GNP ' | Get Next within Parent |
| `FUNC-GHNP` | `X(04)` | 'GHNP' | Get Hold Next within Parent |
| `FUNC-REPL` | `X(04)` | 'REPL' | Replace |
| `FUNC-ISRT` | `X(04)` | 'ISRT' | Insert |
| `FUNC-DLET` | `X(04)` | 'DLET' | Delete |
| `PARMCOUNT` | `S9(05) COMP-5` | +4 | DL/I parameter count |

### `PAUTBPCB.CPY` — IMS PCB for PAUTH Database

| Field Name | PIC Clause | Purpose |
|------------|-----------|---------|
| `PAUT-DBDNAME` | `X(08)` | Database name |
| `PAUT-SEG-LEVEL` | `X(02)` | Segment level indicator |
| `PAUT-PCB-STATUS` | `X(02)` | Status code (e.g., 'GE'=not found, 'GB'=end) |
| `PAUT-PCB-PROCOPT` | `X(04)` | Processing options |
| `PAUT-SEG-NAME` | `X(08)` | Last accessed segment name |
| `PAUT-KEYFB` | `X(255)` | Key feedback area |

### `PASFLPCB.CPY` — IMS PCB for Sequential (GSAM) File

Same PCB structure as PAUTBPCB, for GSAM sequential output. Key feedback area: `X(100)`.

### `PADFLPCB.CPY` — IMS PCB for Detail File

Same PCB structure for detail segment access. Key feedback area: `X(255)`.

---

## 15. MQ Infrastructure Copybooks

The authorization module references IBM MQ copybooks (not custom):

| Copybook | Purpose |
|----------|---------|
| `CMQV` | MQ constants and literals |
| `CMQODV` | Object Descriptor (MQOD) |
| `CMQMDV` | Message Descriptor (MQMD) |
| `CMQTML` | Trigger Message Layout |
| `CMQPMOV` | Put Message Options (MQPMO) |
| `CMQGMOV` | Get Message Options (MQGMO) |

**Used by:** COPAUA0C, COACCT01, CODATE01

---

*End of Data Dictionary*
