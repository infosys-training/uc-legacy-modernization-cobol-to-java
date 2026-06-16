# DATA DICTIONARY — CardDemo Copybook Definitions

## Overview

| Metric | Count |
|--------|-------|
| Total Copybooks | 47 |
| Main Copybooks (`app/cpy/`) | 27 |
| BMS Copybooks (`app/cpy-bms/`) | 17 |
| Authorization Sub-App Copybooks | 9 + 2 BMS |
| Transaction Type Sub-App Copybooks | 2 + 2 BMS |
| Business Entity Copybooks | 12 |
| UI/Infrastructure Copybooks | 35 |

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric | Primary key — unique account identifier | Must be 11-digit numeric |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account active flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance | Signed; max ±9,999,999,999.99 |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Maximum credit limit | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Account open date | CCYYMMDD format (validated by CSUTLDPY) |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | CCYYMMDD format; must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Last reissue date | CCYYMMDD format |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total | Running total for billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total | Running total for billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account ZIP code | Validated against CSLKPCDY state+ZIP table |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/interest rate group | Links to DISCGRP (CVTRA02Y) |
| FILLER | PIC X(178) | Filler | Reserved space | Padding to 300 bytes |

**Used by:** CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, COACCT01, CBSTM03A/B

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric | Primary key — unique customer identifier | 9-digit numeric |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | Non-blank required |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | Non-blank required |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | Non-blank required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated against 50 state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP code | State+ZIP prefix validated against CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Area code validated against NANPA list |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit numeric; validated in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID number | |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | CCYYMMDD format; validated by CSUTLDPY; must indicate age ≥ 18 |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT (electronic funds transfer) account ID | |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder indicator | 'Y'/'N' |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Filler | Reserved space | Padding to 500 bytes |

**Used by:** CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, CBSTM03A/B

### CUSTREC.cpy — Customer Record (Statement Layout, RECLN 500)

Identical field structure to CVCUS01Y but with different field naming for use in statement generation (CBSTM03A). Contains the same fields: CUST-ID through CUST-FICO-CREDIT-SCORE with 168-byte FILLER.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Primary key — card number | 16-character card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric | Foreign key to Account (CVACT01Y) | Must match valid ACCT-ID |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card Verification Value | 3-digit numeric |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on card | |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | CCYYMMDD format |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card active flag | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Filler | Reserved space | Padding to 150 bytes |

**Used by:** CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC

---

## 4. Card-Account Cross-Reference

### CVACT03Y.cpy — Card XREF Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number (key) | Foreign key to CVACT02Y |
| XREF-CUST-ID | PIC 9(09) | Numeric | Customer linkage | Foreign key to CVCUS01Y |
| XREF-ACCT-ID | PIC 9(11) | Numeric | Account linkage | Foreign key to CVACT01Y |
| FILLER | PIC X(14) | Filler | Reserved space | Padding to 50 bytes |

**Used by:** CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, CBSTM03A/B

---

## 5. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | System-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Must match CVTRA03Y.TRAN-TYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Must match CVTRA04Y.TRAN-CAT-CD |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source identifier | |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Signed; max ±999,999,999.99 |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used | Foreign key to CVACT02Y |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Origination timestamp | YYYY-MM-DD HH:MM:SS.ffffff |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | YYYY-MM-DD HH:MM:SS.ffffff |
| FILLER | PIC X(20) | Filler | Reserved space | Padding to 350 bytes |

**Used by:** CBACT04C, CBEXPORT, CBIMPORT, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same structure as CVTRA05Y with prefix `DALYTRAN-` instead of `TRAN-`. Used for daily input file before posting to master.

**Used by:** CBTRN01C, CBTRN02C

### COSTM01.CPY — Transaction Altered Layout for Statements

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| TRNX-KEY (group) | — | — | Composite key for statement reporting |
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number (part of key) |
| TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID (part of key) |
| TRNX-REST (group) | — | — | Transaction details |
| TRNX-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| TRNX-CAT-CD | PIC 9(04) | Numeric | Transaction category code |
| TRNX-SOURCE | PIC X(10) | Alphanumeric | Transaction source |
| TRNX-DESC | PIC X(100) | Alphanumeric | Transaction description |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name |
| TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP |
| TRNX-ORIG-TS | PIC X(26) | Alphanumeric | Origination timestamp |
| TRNX-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp |
| FILLER | PIC X(20) | Filler | Reserved space |

**Used by:** CBSTM03A

---

## 6. Transaction Reference Data

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| TRAN-CAT-KEY (group) | — | — | Composite key | |
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | FK to CVACT01Y |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to CVTRA03Y |
| TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to CVTRA04Y |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance per category per account | Updated by CBTRN02C, read by CBACT04C |
| FILLER | PIC X(22) | Filler | Reserved space | |

**Used by:** CBACT04C, CBTRN02C

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| DIS-GROUP-KEY (group) | — | — | Composite key | |
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group ID | Links to ACCT-GROUP-ID in CVACT01Y |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate for this group/type/category | Percentage value (e.g., 18.99) |
| FILLER | PIC X(28) | Filler | Reserved space | |

**Used by:** CBACT04C

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code (primary key) | 2-char code (e.g., 'SA', 'CR') |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Transaction type description | |
| FILLER | PIC X(08) | Filler | Reserved space | |

**Used by:** CBTRN03C

### CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| TRAN-CAT-KEY (group) | — | — | Composite key | |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | |
| FILLER | PIC X(04) | Filler | Reserved space | |

**Used by:** CBTRN03C

### CVTRA07Y.cpy — Transaction Report Layout (73 lines)

Defines report formatting structures for CBTRN03C:

| Structure | Fields | Purpose |
|-----------|--------|---------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME, REPT-LONG-NAME, REPT-DATE-HEADER, REPT-START-DATE, REPT-END-DATE | Report title and date range header |
| TRANSACTION-DETAIL-REPORT | TRAN-REPORT-TRANS-ID, -ACCOUNT-ID, -TYPE-CD, -TYPE-DESC, -CAT-CD, -CAT-DESC, -SOURCE, -AMT | Detail line format |
| TRANSACTION-HEADER-1/2 | Column headers, separator line | Column headings |
| REPORT-PAGE-TOTALS | REPT-PAGE-TOTAL (PIC +ZZZ,ZZZ,ZZZ.ZZ) | Page subtotal line |
| REPORT-ACCOUNT-TOTALS | REPT-ACCOUNT-TOTAL | Account subtotal line |
| REPORT-GRAND-TOTALS | REPT-GRAND-TOTAL | Grand total line |

---

## 7. User Security

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | User ID (primary key) | 8-char max |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name | |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name | |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | **Password (PLAIN TEXT)** | ⚠️ Security risk: stored unencrypted |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved space | |

**Used by:** COSGN00C, COADM01C, COMEN01C, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COUSR00C–03C, COTRTLIC, COTRTUPC

### UNUSED1Y.cpy — Unused Data Structure (placeholder)

Identical structure to CSUSR01Y but with `UNUSED-` prefix. Appears to be a deprecated/placeholder copybook.

---

## 8. Export/Import

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500, 103 lines)

Header fields common to all record types:

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | Record type discriminator ('C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card) |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Region code |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Payload (REDEFINES per record type) |

REDEFINES structures for each entity type:

- **EXPORT-CUSTOMER-DATA**: Customer fields with COMP/COMP-3 optimization (EXP-CUST-ID PIC 9(09) COMP, EXP-CUST-FICO-CREDIT-SCORE PIC 9(03) COMP-3)
- **EXPORT-ACCOUNT-DATA**: Account fields with COMP-3 for monetary values
- **EXPORT-TRANSACTION-DATA**: Transaction fields with COMP-3 for amounts, COMP for merchant ID
- **EXPORT-CARD-XREF-DATA**: Cross-reference fields
- **EXPORT-CARD-DATA**: Card fields with COMP for numeric fields

**Used by:** CBEXPORT, CBIMPORT

---

## 9. Authorization Entity (IMS Segments)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Root Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID | FK to CVACT01Y |
| PA-CUST-ID | PIC 9(09) | Numeric | Customer ID | FK to CVCUS01Y |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric | Authorization status | |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 TIMES | Alphanumeric | Account status array (5 entries) | |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit | |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash advance limit | |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Current credit balance | |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Current cash balance | |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of approved authorizations | |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of declined authorizations | |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount | |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount | |
| FILLER | PIC X(34) | Filler | Reserved space | |

**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Child Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|-----------------|
| PA-AUTHORIZATION-KEY (group) | — | — | Composite key | |
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (compressed) | |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (compressed) | |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original authorization date | YYMMDD format |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original authorization time | HHMMSS format |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number | FK to CVACT02Y |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type code | |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry date | MMYY format |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type (ISO 8583) | |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message source identifier | |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization identification code | |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Authorization response code | 88: '00' = approved |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason code | |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code (ISO 8583) | |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Requested transaction amount | |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount | |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | Merchant Category Code (MCC) | |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country code | |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID | |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name | |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city | |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state | |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP | |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID | |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric | Match status | 88: 'P'=Pending, 'D'=Declined, 'E'=Expired, 'M'=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | Fraud indicator | 88: 'F'=Confirmed fraud, 'R'=Removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric | Fraud report date | CCYYMMDD |
| FILLER | PIC X(17) | Filler | Reserved space | |

**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL

---

## 10. Authorization MQ Messages

### CCPAURQY.cpy — Authorization Request Message

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | ISO 8583 message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message source |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | Merchant Category Code |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country code |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |

**Used by:** COPAUA0C

### CCPAURLY.cpy — Authorization Response Message

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

**Used by:** COPAUA0C

### CCPAUERY.cpy — Authorization Error Log

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| ERR-DATE | PIC X(06) | Alphanumeric | Error date |
| ERR-TIME | PIC X(06) | Alphanumeric | Error time |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | Program name |
| ERR-LOCATION | PIC X(04) | Alphanumeric | Error location |
| ERR-LEVEL | PIC X(01) | Alphanumeric | Severity (88: 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical) |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem (88: 'A'=App, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=File) |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | Error code 1 |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | Error code 2 |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | Error message |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Event key |

**Used by:** COPAUA0C

---

## 11. IMS Infrastructure Copybooks

### IMSFUNCS.cpy — IMS DL/I Function Codes

| Field Name | PIC Clause | Value | Business Meaning |
|------------|-----------|-------|------------------|
| FUNC-GU | PIC X(04) | 'GU  ' | Get Unique (direct retrieval) |
| FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique (for update) |
| FUNC-GN | PIC X(04) | 'GN  ' | Get Next (sequential forward) |
| FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next |
| FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | PIC X(04) | 'REPL' | Replace (update) |
| FUNC-ISRT | PIC X(04) | 'ISRT' | Insert |
| FUNC-DLET | PIC X(04) | 'DLET' | Delete |
| PARMCOUNT | PIC S9(05) COMP-5 | +4 | Parameter count for CBLTDLI |

### PAUTBPCB.CPY — IMS PCB (Program Communication Block)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| PAUTB-DBDNAME | PIC X(08) | Database name |
| PAUTB-SEG-LEVEL | PIC X(02) | Segment level |
| PAUTB-PCB-STATUS | PIC X(02) | Status code (e.g., 'GE'=not found, '  '=success) |
| PAUTB-PCB-PROCOPT | PIC X(04) | Processing options |
| PAUTB-SEG-NAME | PIC X(08) | Segment name |
| PAUTB-KEYFB-NAME | PIC S9(05) COMP | Key feedback length |
| PAUTB-NUM-SENSEGS | PIC S9(05) COMP | Number of sensitive segments |
| PAUTB-KEYFB | PIC X(255) | Key feedback area |

### PADFLPCB.CPY / PASFLPCB.CPY — Additional IMS PCBs

Same structure as PAUTBPCB with prefixes `PADFL-` and `PASFL-` for the detail and summary databases respectively.

---

## 12. UI/Infrastructure Copybooks

### COCOM01Y.cpy — CICS COMMAREA (Inter-Program Communication)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Current user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type (88: 'A'=Admin, 'U'=User) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context (88: 0=Enter, 1=Reenter) |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Selected customer ID |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alphanumeric | Customer name fields |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Selected account ID |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Selected card number |
| CDEMO-LAST-MAP/MAPSET | PIC X(7) each | Alphanumeric | Last BMS map/mapset displayed |

**Used by:** All 21 CICS programs

### COADM02Y.cpy — Admin Menu Options

Defines 6 admin menu options with program routing: COUSR00C (User List), COUSR01C (User Add), COUSR02C (User Update), COUSR03C (User Delete), COTRTLIC (Transaction Type List/DB2), COTRTUPC (Transaction Type Maintenance/DB2).

### COMEN02Y.cpy — Main Menu Options

Defines 11 main menu options with program routing: COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C.

### CVCRD01Y.cpy — Card Work Areas and AID Processing

Defines CICS AID (Attention Identifier) values for PF keys, program routing fields, account/card/customer ID work areas, error/return message fields.

### COTTL01Y.cpy — Screen Title

| Field | Value |
|-------|-------|
| CCDA-TITLE01 | "AWS Mainframe Modernization" |
| CCDA-TITLE02 | "CardDemo" |
| CCDA-THANK-YOU | "Thank you for using CCDA application..." |

### CSDAT01Y.cpy — Date/Time Working Storage

Defines WS-CURDATE (YYYYMMDD), WS-CURTIME (HHMMSSMS), WS-CURDATE-MM-DD-YY, WS-CURTIME-HH-MM-SS, and WS-TIMESTAMP structures for current date/time handling.

### CSMSG01Y.cpy — Common Messages

| Field | Value |
|-------|-------|
| CCDA-MSG-THANK-YOU | "Thank you for using CardDemo application..." |
| CCDA-MSG-INVALID-KEY | "Invalid key pressed. Please see below..." |

### CSMSG02Y.cpy — Abend Data

| Field | PIC | Purpose |
|-------|-----|---------|
| ABEND-CODE | PIC X(4) | Abend code |
| ABEND-CULPRIT | PIC X(8) | Failing program |
| ABEND-REASON | PIC X(50) | Abend reason |
| ABEND-MSG | PIC X(72) | Abend message |

### CODATECN.cpy — Date Conversion Structure

Defines input/output areas for date format conversion between YYYYMMDD and YYYY-MM-DD formats. Used by COBDATFT assembler routine.

### CSSETATY.cpy — Screen Attribute Setting (COPY REPLACING Macro)

Template copybook used with `COPY REPLACING` to set BMS field attributes. Parameters: `(TESTVAR1)`, `(SCRNVAR2)`, `(MAPNAME3)`. Sets field color to red and marks with '*' when validation fails. Used 39× in COACTUPC, 1× in COTRTUPC.

### CSSTRPFY.cpy — PF Key Storage

Paragraph `YYYY-STORE-PFKEY` that maps EIBAID values to CCARD-AID-PFKxx flags via an EVALUATE statement.

### CSUTLDWY.cpy — Date Validation Working Storage

Defines date edit fields (CC, YY, MM, DD), validation flags (FLG-YEAR-*, FLG-MONTH-*, FLG-DAY-*), 88-level conditions for valid months/days, and date validation result structure.

### CSUTLDPY.cpy — Date Validation Procedures (375 lines)

Contains reusable paragraphs for date validation:
- `EDIT-DATE-CCYYMMDD` — Main entry point
- `EDIT-YEAR-CCYY` — Year/century validation
- `EDIT-MONTH` — Month validation (1–12)
- `EDIT-DAY` — Day validation (handles 28/29/30/31 and leap years)
- `EDIT-DATE-OF-BIRTH` — Age validation (must be 18+)

### CSLKPCDY.cpy — Lookup Code Repository (1,318 lines)

Contains three validation datasets:
1. **NANPA Phone Area Codes** — 88-level condition `VALID-PHONE-AREA-CODE` with all valid North American area codes
2. **US State Codes** — 88-level condition `VALID-US-STATE-CODE` with 50 state codes + territories
3. **State+ZIP Prefix Validation** — 88-level condition `VALID-STATE-ZIP-CD` mapping state codes to valid 2-digit ZIP prefixes

### DB2 Copybooks (`app/app-transaction-type-db2/cpy/`)

| Copybook | Purpose |
|----------|---------|
| CSDB2RWY.cpy | DB2 read/write working storage areas |
| CSDB2RPY.cpy | DB2 response processing areas |

---

## Entity Relationship Summary

```
Customer (CVCUS01Y) ──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
     │                              │                              │
     │                        Card (CVACT02Y)                      │
     │                              │                              │
     │                   Transaction (CVTRA05Y) ──► TranType (CVTRA03Y)
     │                                          ──► TranCat (CVTRA04Y)
     │                                                             │
     │                                              TranCatBal (CVTRA01Y)
     │                                                             │
     │                                              DiscGroup (CVTRA02Y)
     │
     └──► Auth Summary (CIPAUSMY) ──1:N──► Auth Detail (CIPAUDTY)
              [IMS Root Segment]              [IMS Child Segment]

User Security (CSUSR01Y) — standalone, no FK relationships
```
