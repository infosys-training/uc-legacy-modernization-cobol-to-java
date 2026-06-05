# DATA DICTIONARY — CardDemo Copybook Structures

> **Generated:** 2026-06-05 | **Scope:** `app/cpy/`, `app/app-authorization-ims-db2-mq/cpy/`, `app/app-transaction-type-db2/cpy/`

## Summary

| Metric | Count |
|--------|-------|
| Total copybooks | 47 |
| Main copybooks (`app/cpy/`) | 27 |
| Authorization sub-app copybooks | 7 |
| Transaction-type sub-app copybooks | 3 |
| Business entities defined | 6 core + 4 authorization + 3 infrastructure |
| Total field definitions | ~350+ |

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Record (300 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (unsigned) | Primary key — unique account identifier | 11-digit number; VSAM KSDS key at offset 0 |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account active flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Signed; can be negative (overdrawn) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum credit allowed | Must be ≥ 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Cash advance credit limit | Must be ≥ 0; typically ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (date) | Date account was opened | CCYYMMDD format; validated via CSUTLDPY |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (date) | Account expiration date | CCYYMMDD format; must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (date) | Last card reissue date | CCYYMMDD format |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle total credits | Running total; reset at statement cutoff |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle total debits | Running total; reset at statement cutoff |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | Validated against CSLKPCDY state-ZIP table |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/interest rate group | Links to DISCGRP (CVTRA02Y) for interest rates |
| FILLER | PIC X(178) | — | Reserved padding to 300 bytes | — |

### 1.2 CVEXPORT.cpy — Export Account Layout (within EXPORT-RECORD)

| Field | PIC Clause | Data Type | Notes |
|-------|-----------|-----------|-------|
| EXP-ACCT-ID | PIC 9(11) | Numeric | Same as ACCT-ID |
| EXP-ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Same as ACCT-ACTIVE-STATUS |
| EXP-ACCT-CURR-BAL | PIC S9(10)V99 COMP-3 | Packed decimal | Storage-optimized vs CVACT01Y |
| EXP-ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Same as source |
| EXP-ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 COMP-3 | Packed decimal | Storage-optimized |
| EXP-ACCT-OPEN-DATE | PIC X(10) | Date | Same format |
| EXP-ACCT-EXPIRAION-DATE | PIC X(10) | Date | Same format |
| EXP-ACCT-REISSUE-DATE | PIC X(10) | Date | Same format |
| EXP-ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Same as source |
| EXP-ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 COMP | Binary | Storage-optimized |
| EXP-ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Same as source |
| EXP-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Same as source |

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Record (500 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric (unsigned) | Primary key — unique customer identifier | 9-digit number |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | Non-blank required |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | Non-blank required |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | Non-blank required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated against 50 state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | 3-letter ISO code |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | First 2 digits validated against state-ZIP table in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Same validation as PHONE-NUM-1 |
| CUST-SSN | PIC 9(09) | Numeric (unsigned) | Social Security Number | 9 digits; validated for non-zero, proper format in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID number | Optional alternate ID |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (date) | Date of birth | CCYYMMDD format; validated via CSUTLDPY |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/ACH account reference | Optional; for electronic fund transfers |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder flag | 'Y' = primary, 'N' = authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (unsigned) | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | — | Reserved padding to 500 bytes | — |

### 2.2 CUSTREC.cpy — Customer Record (alternate layout, 500 bytes)

Identical field layout to CVCUS01Y.cpy with minor formatting differences (wider indentation). Used by sub-application programs. Field `CUST-DOB-YYYYMMDD` (vs `CUST-DOB-YYYY-MM-DD` in CVCUS01Y) — same 10-byte field, different naming convention.

### 2.3 CVEXPORT.cpy — Export Customer Layout (within EXPORT-RECORD)

| Field | PIC Clause | Data Type | Notes |
|-------|-----------|-----------|-------|
| EXP-CUST-ID | PIC 9(09) COMP | Binary | Compressed vs source PIC 9(09) |
| EXP-CUST-FIRST/MIDDLE/LAST-NAME | PIC X(25) each | Alphanumeric | Same as source |
| EXP-CUST-ADDR-LINE | PIC X(50) OCCURS 3 | Array | Address lines as array vs individual fields |
| EXP-CUST-PHONE-NUM | PIC X(15) OCCURS 2 | Array | Phone numbers as array |
| EXP-CUST-SSN | PIC 9(09) | Numeric | Same as source |
| EXP-CUST-FICO-CREDIT-SCORE | PIC 9(03) COMP-3 | Packed decimal | Storage-optimized |

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Record (150 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Primary key — credit card number | 16-character card number; VSAM KSDS key |
| CARD-ACCT-ID | PIC 9(11) | Numeric (unsigned) | Foreign key to Account | Must exist in ACCTFILE |
| CARD-CVV-CD | PIC 9(03) | Numeric (unsigned) | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name printed on card | Non-blank; typically FIRST + LAST name |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (date) | Card expiration date | CCYYMMDD format |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card active flag | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | — | Reserved padding to 150 bytes | — |

### 3.2 CVACT03Y.cpy — Card-Account Cross-Reference (50 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number — primary key | Must exist in CARDFILE |
| XREF-CUST-ID | PIC 9(09) | Numeric (unsigned) | Customer linkage | Must exist in CUSTFILE |
| XREF-ACCT-ID | PIC 9(11) | Numeric (unsigned) | Account linkage | Must exist in ACCTFILE |
| FILLER | PIC X(14) | — | Reserved padding to 50 bytes | — |

**Relationship:** This is the central join table: `Customer (1) → (N) XREF (N) ← (1) Account`. A customer can have multiple cards; an account can have multiple cardholders.

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Record (350 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Primary key — transaction identifier | Auto-generated; monotonically increasing |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Must exist in TRANTYPE file (CVTRA03Y) |
| TRAN-CAT-CD | PIC 9(04) | Numeric (unsigned) | Transaction category code | Must exist in TRANCATG file (CVTRA04Y) |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction origination source | e.g., 'POS', 'ATM', 'ONLINE', 'PHONE' |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free-text description |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Signed; negative = credit/refund |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (unsigned) | Merchant identifier | 9-digit merchant code |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant business name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used | Must exist in CARDXREF |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric (timestamp) | Transaction origination timestamp | YYYY-MM-DD-HH.MM.SS.FFFFFF format |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric (timestamp) | Processing timestamp | Set by CBTRN02C at posting time |
| FILLER | PIC X(20) | — | Reserved padding to 350 bytes | — |

### 4.2 CVTRA06Y.cpy — Daily Transaction Record (350 bytes)

Same layout as CVTRA05Y with `DALYTRAN-` prefix. This is the input file read by CBTRN02C (POSTTRAN job) before posting to the master TRANSACT file.

| Field | PIC Clause | Equivalent in CVTRA05Y |
|-------|-----------|----------------------|
| DALYTRAN-ID | PIC X(16) | TRAN-ID |
| DALYTRAN-TYPE-CD | PIC X(02) | TRAN-TYPE-CD |
| DALYTRAN-CAT-CD | PIC 9(04) | TRAN-CAT-CD |
| DALYTRAN-SOURCE | PIC X(10) | TRAN-SOURCE |
| DALYTRAN-DESC | PIC X(100) | TRAN-DESC |
| DALYTRAN-AMT | PIC S9(09)V99 | TRAN-AMT |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | TRAN-MERCHANT-ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | TRAN-MERCHANT-NAME |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | TRAN-MERCHANT-CITY |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | TRAN-MERCHANT-ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | TRAN-CARD-NUM |
| DALYTRAN-ORIG-TS | PIC X(26) | TRAN-ORIG-TS |
| DALYTRAN-PROC-TS | PIC X(26) | TRAN-PROC-TS |

### 4.3 CVTRA01Y.cpy — Transaction Category Balance (50 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY (group) | — | Composite key | — | — |
| → TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account identifier | FK to ACCTFILE |
| → TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type | FK to TRANTYPE |
| → TRANCAT-CD | PIC 9(04) | Numeric | Category code | FK to TRANCATG |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance for this acct/type/cat | Updated by CBTRN02C, read by CBACT04C |
| FILLER | PIC X(22) | — | Padding to 50 bytes | — |

### 4.4 CVTRA02Y.cpy — Disclosure Group (50 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| DIS-GROUP-KEY (group) | — | Composite key | — | — |
| → DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group identifier | Links to ACCT-GROUP-ID in CVACT01Y |
| → DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | — |
| → DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Category code | — |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest rate for this group/type/cat | Percentage (e.g., 18.99) |
| FILLER | PIC X(28) | — | Padding to 50 bytes | — |

### 4.5 CVTRA03Y.cpy — Transaction Type (60 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code — primary key | 2-char code (e.g., 'SA'=sale, 'CR'=credit) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Description of transaction type | Non-blank |
| FILLER | PIC X(08) | — | Padding to 60 bytes | — |

### 4.6 CVTRA04Y.cpy — Transaction Category (60 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY (group) | — | Composite key | — | — |
| → TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Parent transaction type | FK to CVTRA03Y |
| → TRAN-CAT-CD | PIC 9(04) | Numeric | Category code within type | — |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | Non-blank |
| FILLER | PIC X(04) | — | Padding to 60 bytes | — |

### 4.7 CVTRA07Y.cpy — Transaction Report Layout (73 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report identifier ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report title ('Daily Transaction Report') |
| REPT-START-DATE / REPT-END-DATE | PIC X(10) | Date | Report date range |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID column |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID column |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code column |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description column |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code column |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description column |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Source column |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount column |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |

---

## 5. Authorization Entity (IMS Segments)

### 5.1 CIPAUSMY.cpy — Pending Authorization Summary (IMS root segment)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account identifier (IMS root key) | Must exist in ACCTFILE |
| PA-CUST-ID | PIC 9(09) | Numeric | Customer identifier | Must exist in CUSTFILE |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric | Overall authorization status | — |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Array (5 × 2) | Status codes for sub-accounts | Up to 5 status slots |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Account credit limit snapshot | — |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Cash advance limit snapshot | — |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Credit balance at auth time | — |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Cash balance at auth time | — |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of approved authorizations | Running counter |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of declined authorizations | Running counter |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Total approved amount | Running total |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Total declined amount | Running total |
| FILLER | PIC X(34) | — | Padding | — |

### 5.2 CIPAUDTY.cpy — Pending Authorization Detail (IMS child segment, 54 lines)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-AUTHORIZATION-KEY (group) | — | Composite | Unique auth key | — |
| → PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (compressed) | — |
| → PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (compressed) | — |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original auth date | YYMMDD format |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original auth time | HHMMSS format |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Must exist in CARDXREF |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type | — |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry date | MMYY format |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type code | — |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message origination | — |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID code | — |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code | '00' = approved (88 PA-AUTH-APPROVED) |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason code | — |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Requested transaction amount | — |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Approved amount | May be ≤ PA-TRANSACTION-AMT |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | MCC code | — |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquiring country | — |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | — |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant identifier | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP | — |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | Linked transaction ID | — |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric | Auth-to-transaction match status | 'P'=pending, 'D'=declined, 'E'=expired, 'M'=matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | Fraud flag | 'F'=confirmed fraud, 'R'=fraud removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric (date) | Fraud report date | — |
| FILLER | PIC X(17) | — | Padding | — |

### 5.3 CCPAURQY.cpy — Authorization Request (MQ message)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Source system |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC S9(10)V99 | Signed decimal | Requested amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | MCC code |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquiring country |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |

### 5.4 CCPAURLY.cpy — Authorization Response (MQ message)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Reason code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### 5.5 CCPAUERY.cpy — Authorization Error Log

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| ERR-DATE | PIC X(06) | Alphanumeric | Error date |
| ERR-TIME | PIC X(06) | Alphanumeric | Error time |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | Program that failed |
| ERR-LOCATION | PIC X(04) | Alphanumeric | Code location |
| ERR-LEVEL | PIC X(01) | Alphanumeric | Severity: 'L'=log, 'I'=info, 'W'=warn, 'C'=critical |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem: 'A'=app, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=file |
| ERR-CODE-1 / ERR-CODE-2 | PIC X(09) each | Alphanumeric | Error codes |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | Error message text |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Triggering event key |

---

## 6. User/Security Entity

### 6.1 CSUSR01Y.cpy — User Security Record (80 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | Primary key — user login ID | 8-char max; stored in USRSEC VSAM |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name | Non-blank |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name | Non-blank |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | **Password (plain text!)** | **SECURITY RISK** — stored unencrypted |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type flag | 'A' = admin, 'U' = regular user |
| SEC-USR-FILLER | PIC X(23) | — | Padding to 80 bytes | — |

> **Security Note:** Passwords are stored in clear text. Modernization MUST implement proper hashing (bcrypt/scrypt) via Spring Security.

---

## 7. Application Infrastructure Copybooks

### 7.1 COCOM01Y.cpy — CICS Communication Area (COMMAREA, 47 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target CICS transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 'A' = admin (88 CDEMO-USRTYP-ADMIN), 'U' = user |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 0 = first entry, 1 = re-entry |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Customer context (for drill-down) |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alphanumeric | Customer name context |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Account context |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status context |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Card context |
| CDEMO-LAST-MAP / CDEMO-LAST-MAPSET | PIC X(7) each | Alphanumeric | Last displayed BMS map/mapset |

### 7.2 CODATECN.cpy — Date Conversion Record (52 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CODATECN-TYPE | PIC X | Alphanumeric | Input format: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date string |
| CODATECN-1INP (REDEFINES) | — | — | YYYYMMDD parser overlay |
| CODATECN-2INP (REDEFINES) | — | — | YYYY-MM-DD parser overlay |
| CODATECN-OUTTYPE | PIC X | Alphanumeric | Output format: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Converted date output |
| CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Error message if conversion fails |

### 7.3 COMEN02Y.cpy — Main Menu Options (101 lines)

Defines 11 menu options for the COMEN01C main menu screen. Each option specifies:

| Sub-field | PIC | Purpose |
|-----------|-----|---------|
| CDEMO-MENU-OPT-NUM | PIC 9(02) | Menu option number (1–11) |
| CDEMO-MENU-OPT-NAME | PIC X(35) | Display label |
| CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Target COBOL program |
| CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Required user type ('U'=user, 'A'=admin) |

**Menu Options:** 1=Account View (COACTVWC), 2=Account Update (COACTUPC), 3=Card List (COCRDLIC), 4=Card View (COCRDSLC), 5=Card Update (COCRDUPC), 6=Transaction List (COTRN00C), 7=Transaction View (COTRN01C), 8=Transaction Add (COTRN02C), 9=Transaction Reports (CORPT00C), 10=Bill Payment (COBIL00C), 11=Pending Authorization View (COPAUS0C)

### 7.4 COADM02Y.cpy — Admin Menu Options (62 lines)

Admin-specific menu option definitions. Same structure as COMEN02Y with different targets (COUSR00C, COUSR01C, COTRTLIC, COTRTUPC).

### 7.5 COTTL01Y.cpy — Screen Title (27 lines)

| Field | PIC Clause | Value |
|-------|-----------|-------|
| CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | 'Thank you for using CCDA application...' |

### 7.6 CSDAT01Y.cpy — Date/Time Working Storage (58 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| WS-CURTIME-HOURS/MINUTE/SECOND/MILSEC | PIC 9(02) each | Numeric | Current time components |
| WS-CURDATE-MM-DD-YY | — | Group | Formatted date MM/DD/YY |
| WS-CURTIME-HH-MM-SS | — | Group | Formatted time HH:MM:SS |
| WS-TIMESTAMP | — | Group | Full timestamp YYYY-MM-DD HH:MM:SS.FFFFFF |

### 7.7 CSMSG01Y.cpy — Common Messages (24 lines)

| Field | PIC Clause | Value |
|-------|-----------|-------|
| CCDA-MSG-THANK-YOU | PIC X(50) | 'Thank you for using CardDemo application...' |
| CCDA-MSG-INVALID-KEY | PIC X(50) | 'Invalid key pressed. Please see below...' |

### 7.8 CSMSG02Y.cpy — Abend Data (35 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| ABEND-CODE | PIC X(4) | Alphanumeric | Abend code |
| ABEND-CULPRIT | PIC X(8) | Alphanumeric | Program that caused abend |
| ABEND-REASON | PIC X(50) | Alphanumeric | Abend reason text |
| ABEND-MSG | PIC X(72) | Alphanumeric | Full abend message |

---

## 8. Validation & Utility Copybooks

### 8.1 CSLKPCDY.cpy — Lookup Code Repository (1,318 lines)

Defines three validation tables via level-88 condition names:

| Validation | Field | Sample Values |
|-----------|-------|---------------|
| VALID-PHONE-AREA-CODE | WS-US-PHONE-AREA-CODE-TO-EDIT PIC XXX | 600+ NANPA area codes ('201' through '989') |
| VALID-US-STATE-CODE | WS-US-STATE-CD-TO-EDIT PIC XX | 50 US state codes ('AK' through 'WY') + territories |
| VALID-STATE-ZIP-PREFIX | WS-US-STATE-AND-FIRST2ZIP PIC X(4) | State + first 2 ZIP digits (e.g., 'NY10', 'CA90') |

### 8.2 CSUTLDWY.cpy — Date Validation Working Storage (89 lines)

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| WS-EDIT-DATE-CCYYMMDD | Group | Date being validated |
| WS-EDIT-DATE-CC | PIC X(2) | Century (88: THIS-CENTURY=20, LAST-CENTURY=19) |
| WS-EDIT-DATE-YY | PIC X(2) | Year within century |
| WS-EDIT-DATE-MM | PIC X(2) | Month |
| WS-EDIT-DATE-DD | PIC X(2) | Day |
| WS-EDIT-VARIABLE-NAME | PIC X(32) | Name of field being validated (for error messages) |
| WS-RETURN-MSG | PIC X(80) | Validation error message |
| FLG-YEAR-ISVALID / FLG-MONTH-ISVALID / FLG-DAY-ISVALID | Level-88 | Validation outcome flags |

### 8.3 CSUTLDPY.cpy — Date Validation Procedures (375 lines)

Reusable paragraphs included via COPY into procedure division:

| Paragraph | Purpose | Validation Rules |
|-----------|---------|-----------------|
| EDIT-DATE-CCYYMMDD | Main entry point | Chains YEAR → MONTH → DAY |
| EDIT-YEAR-CCYY | Year validation | Must be numeric; century must be 19 or 20 |
| EDIT-MONTH | Month validation | Must be numeric 01–12 |
| EDIT-DAY | Day validation | Must be numeric 01–28/29/30/31 (month-dependent, leap-year aware) |
| EDIT-DATE-OF-BIRTH | DOB-specific validation | Must be in the past; century check |

### 8.4 CSSETATY.cpy — Screen Attribute Setting (30 lines)

Macro-style copybook used with COPY REPLACING. Sets BMS screen field attributes (color, protection) based on validation flags:
- If `FLG-(TESTVAR1)-NOT-OK` or `FLG-(TESTVAR1)-BLANK` → set field color to DFHRED
- If blank → also place '*' marker in field

Used 3× in COACTUPC with REPLACING for different field groups.

### 8.5 CSSTRPFY.cpy — String Processing (85 lines)

Reusable string parsing/formatting paragraphs for field stripping and padding.

### 8.6 CVCRD01Y.cpy — Card Working Areas (46 lines)

Working storage for CICS key handling:

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| CCARD-AID | PIC X(5) | AID key pressed (ENTER, CLEAR, PFKnn) |
| CCARD-NEXT-PROG / CCARD-NEXT-MAP | PIC X(8)/X(7) | Next program/map to navigate to |
| CCARD-RETURN-FLAG | PIC X(1) | Return-to-previous indicator |

### 8.7 CVEXPORT.cpy — Multi-Record Export Layout (103 lines)

500-byte export record with REDEFINES structure supporting multiple record types:
- EXPORT-REC-TYPE: 'C'=Customer, 'A'=Account, 'X'=CrossRef
- EXPORT-TIMESTAMP, EXPORT-SEQUENCE-NUM, EXPORT-BRANCH-ID (header fields)
- Three REDEFINES overlays for customer, account, and xref data

---

## 9. DB2 Copybooks (Transaction Type sub-app)

### 9.1 CSDB2RPY.cpy — DB2 Common Procedures (89 lines)

Contains reusable DB2 connectivity procedures:
- `9998-PRIMING-QUERY` — Dummy SELECT 1 FROM SYSIBM.SYSDUMMY1 to verify DB2 connectivity

### 9.2 CSDB2RWY.cpy — DB2 Working Storage

Working storage definitions for DB2 status tracking (SQLCODE display, SQLCA fields).

### 9.3 IMSFUNCS.cpy — IMS Function Codes (26 lines)

| Field | PIC Clause | Value | IMS Operation |
|-------|-----------|-------|--------------|
| FUNC-GU | PIC X(04) | 'GU  ' | Get Unique |
| FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique |
| FUNC-GN | PIC X(04) | 'GN  ' | Get Next |
| FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next |
| FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | PIC X(04) | 'REPL' | Replace |
| FUNC-ISRT | PIC X(04) | 'ISRT' | Insert |
| FUNC-DLET | PIC X(04) | 'DLET' | Delete |

---

## 10. Entity Relationship Summary

```
┌─────────────────┐     1:N     ┌──────────────────┐     N:1     ┌─────────────────┐
│   CUSTOMER      │────────────►│   CARD-XREF      │◄────────────│    ACCOUNT      │
│  CVCUS01Y.cpy   │             │  CVACT03Y.cpy    │             │  CVACT01Y.cpy   │
│  Key: CUST-ID   │             │  Key: XREF-CARD  │             │  Key: ACCT-ID   │
│  RecLen: 500    │             │  RecLen: 50      │             │  RecLen: 300    │
└─────────────────┘             └────────┬─────────┘             └────────┬────────┘
                                         │                                │
                                    1:1  │                           1:N  │
                                         ▼                                ▼
                                ┌──────────────────┐             ┌─────────────────┐
                                │     CARD         │             │   TRAN-CAT-BAL  │
                                │  CVACT02Y.cpy    │             │  CVTRA01Y.cpy   │
                                │  Key: CARD-NUM   │             │  Key: ACCT+TYPE │
                                │  RecLen: 150     │             │  RecLen: 50     │
                                └────────┬─────────┘             └─────────────────┘
                                         │                                ▲
                                    1:N  │                                │
                                         ▼                                │
                                ┌──────────────────┐             ┌─────────────────┐
                                │  TRANSACTION     │────────────►│  DISCLOSURE GRP │
                                │  CVTRA05Y.cpy    │  type+cat   │  CVTRA02Y.cpy   │
                                │  Key: TRAN-ID    │             │  Key: GRP+TYPE  │
                                │  RecLen: 350     │             │  RecLen: 50     │
                                └────────┬─────────┘             └─────────────────┘
                                         │
                              type-cd    │    cat-cd
                          ┌──────────────┴──────────────┐
                          ▼                              ▼
                 ┌──────────────────┐           ┌──────────────────┐
                 │  TRAN-TYPE       │           │  TRAN-CATEGORY   │
                 │  CVTRA03Y.cpy   │           │  CVTRA04Y.cpy   │
                 │  Key: TRAN-TYPE │           │  Key: TYPE+CAT  │
                 │  RecLen: 60     │           │  RecLen: 60     │
                 └──────────────────┘           └──────────────────┘

IMS Hierarchy (separate database):
┌──────────────────┐     1:N     ┌──────────────────┐
│  AUTH SUMMARY    │────────────►│  AUTH DETAIL      │
│  CIPAUSMY.cpy    │             │  CIPAUDTY.cpy    │
│  Root: PA-ACCT   │             │  Key: DATE+TIME  │
└──────────────────┘             └──────────────────┘
```
