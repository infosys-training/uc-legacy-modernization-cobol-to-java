# DATA DICTIONARY — CardDemo COBOL Estate

> **Total Copybooks:** 62 (30 data in `app/cpy/`, 17 BMS-generated in `app/cpy-bms/`, 15 sub-application)
> **Core Business Entities:** Account, Customer, Card, Card-XREF, Transaction

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Master Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric display | 11 | Primary key — unique account identifier | Required; 11-digit numeric |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Account status flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | 12 | Current account balance | Signed; implied 2 decimal places; use BigDecimal in Java |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 12 | Credit limit | Signed; implied 2 decimal places |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 12 | Cash advance credit limit | Signed; implied 2 decimal places |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | 10 | Account open date | CCYY-MM-DD format; validated via CSUTLDPY |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Account expiration date | CCYY-MM-DD format (*note: typo "EXPIRAION" is in source*) |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | 10 | Card reissue date | CCYY-MM-DD format |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | 12 | Current cycle credit total | Accumulated credits this billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | 12 | Current cycle debit total | Accumulated debits this billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | Account ZIP code | Validated against CSLKPCDY ZIP prefixes |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Disclosure/interest rate group | Links to CVTRA02Y (DIS-GROUP-RECORD) |
| FILLER | PIC X(178) | Filler | 178 | Reserved/padding | — |

**VSAM Key:** ACCT-ID (bytes 0–10, length 11)
**Used by:** CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT

### 1.2 CUSTREC.cpy — Customer Record (alternate layout, RECLN 500)

Identical field layout to CVCUS01Y (see Section 2) but uses `CUST-DOB-YYYYMMDD` instead of `CUST-DOB-YYYY-MM-DD`. Used specifically by CBSTM03A for statement generation.

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Master Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric display | 9 | Primary key — customer identifier | Required; 9-digit numeric |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | 25 | Customer first name | Required |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | 25 | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | 25 | Customer last name | Required |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | 50 | Address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | 50 | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | 50 | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | 2 | US state code | Validated via CSLKPCDY (50 valid state codes) |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | 3 | Country code | — |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | ZIP code | Validated via CSLKPCDY (ZIP prefix lookup) |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | 15 | Primary phone | Area code validated via CSLKPCDY (NANPA codes 201–999) |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | 15 | Secondary phone | Same area code validation |
| CUST-SSN | PIC 9(09) | Numeric display | 9 | Social Security Number | 9-digit numeric; validated for format in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | 20 | Government-issued ID | — |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | 10 | Date of birth | YYYY-MM-DD; validated via CSUTLDPY (cannot be in future) |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | 10 | EFT account for auto-pay | — |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | 1 | Primary cardholder indicator | 'Y' or 'N' |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric display | 3 | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Filler | 168 | Reserved/padding | — |

**VSAM Key:** CUST-ID (bytes 0–8, length 9)
**Used by:** CBCUS01C, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | 16 | Credit card number (primary key) | 16-character card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric display | 11 | Foreign key to Account (CVACT01Y) | Must exist in ACCTDATA |
| CARD-CVV-CD | PIC 9(03) | Numeric display | 3 | Card verification value | 3-digit CVV code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | 50 | Name embossed on card | — |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Card expiration date | CCYY-MM-DD (*note: typo in source*) |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Card status | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Filler | 59 | Reserved/padding | — |

**VSAM Key:** CARD-NUM (bytes 0–15, length 16)
**Used by:** CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COPAUS0C, CBEXPORT, CBIMPORT

### 3.2 CVACT03Y.cpy — Card-Account Cross-Reference (RECLN 50)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number (primary key) | Must exist in CARDDATA |
| XREF-CUST-ID | PIC 9(09) | Numeric display | 9 | Customer ID (foreign key to Customer) | Must exist in CUSTDATA |
| XREF-ACCT-ID | PIC 9(11) | Numeric display | 11 | Account ID (foreign key to Account) | Must exist in ACCTDATA |
| FILLER | PIC X(14) | Filler | 14 | Reserved/padding | — |

**VSAM Key:** XREF-CARD-NUM (bytes 0–15, length 16)
**Relationship:** Links Card → Customer → Account (central join table)
**Used by:** CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C, CBSTM03A, CBEXPORT, CBIMPORT

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | 16 | Transaction ID (primary key) | System-generated; unique |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | FK to CVTRA03Y (TRAN-TYPE) |
| TRAN-CAT-CD | PIC 9(04) | Numeric display | 4 | Transaction category code | FK to CVTRA04Y (TRAN-CAT-RECORD) |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Transaction source system | e.g., 'ONLINE', 'BATCH', 'POS' |
| TRAN-DESC | PIC X(100) | Alphanumeric | 100 | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | 11 | Transaction amount | Signed; implied 2 decimal places; use BigDecimal |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric display | 9 | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number used | FK to CARDDATA via XREF |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | 26 | Original transaction timestamp | YYYY-MM-DD-HH.MM.SS.MMMMMM |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | 26 | Processed timestamp | Same format; used as AIX key |
| FILLER | PIC X(20) | Filler | 20 | Reserved/padding | — |

**VSAM Key:** TRAN-ID (bytes 0–15, length 16)
**AIX Key:** TRAN-PROC-TS (bytes 304–329, length 26, NONUNIQUEKEY)
**Used by:** CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C, CBEXPORT, CBIMPORT

### 4.2 CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-------|-----------------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | 16 | Daily transaction ID | — |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | FK to TRANTYPE |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric display | 4 | Category code | FK to TRANCATG |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Source system | — |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | 100 | Description | — |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | 11 | Transaction amount | Signed decimal |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric display | 9 | Merchant ID | — |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name | — |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city | — |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP | — |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number | — |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | 26 | Original timestamp | — |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | 26 | Processed timestamp | — |
| FILLER | PIC X(20) | Filler | 20 | Reserved | — |

**Layout:** Identical field structure to CVTRA05Y but with DALYTRAN- prefix.
**Used by:** CBTRN01C, CBTRN02C (input file for daily batch processing)

### 4.3 COSTM01.CPY — Transaction Altered Layout for Reporting (RECLN 350)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-----------|-----------|-----------|-------|-----------------|
| TRNX-KEY (compound) | — | — | 32 | Composite key: CARD-NUM + TRAN-ID |
| → TRNX-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number (part of key) |
| → TRNX-ID | PIC X(16) | Alphanumeric | 16 | Transaction ID (part of key) |
| TRNX-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type |
| TRNX-CAT-CD | PIC 9(04) | Numeric display | 4 | Category code |
| TRNX-SOURCE | PIC X(10) | Alphanumeric | 10 | Source |
| TRNX-DESC | PIC X(100) | Alphanumeric | 100 | Description |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal | 11 | Amount |
| *(remaining fields same as CVTRA05Y)* | — | — | — | — |

**Used by:** CBSTM03A/CBSTM03B (statement generation — sorts transactions by card number)

---

## 5. Reference Data Entities

### 5.1 CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-----------|-----------|-----------|-------|-----------------|
| TRAN-CAT-KEY (compound) | — | — | 17 | Composite key |
| → TRANCAT-ACCT-ID | PIC 9(11) | Numeric display | 11 | Account ID |
| → TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code |
| → TRANCAT-CD | PIC 9(04) | Numeric display | 4 | Category code |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | 11 | Category balance | 
| FILLER | PIC X(22) | Filler | 22 | Reserved |

**Used by:** CBACT04C, CBTRN02C (accumulate balances per category per account)

### 5.2 CVTRA02Y.cpy — Disclosure Group / Interest Rate (RECLN 50)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-----------|-----------|-----------|-------|-----------------|
| DIS-GROUP-KEY (compound) | — | — | 16 | Composite key |
| → DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Account group ID (links to ACCT-GROUP-ID) |
| → DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code |
| → DIS-TRAN-CAT-CD | PIC 9(04) | Numeric display | 4 | Category code |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | 6 | Interest rate for this group/type/category |
| FILLER | PIC X(28) | Filler | 28 | Reserved |

**Used by:** CBACT04C (look up interest rates by account group and transaction category)

### 5.3 CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-----------|-----------|-----------|-------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | 2 | Transaction type code (primary key) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Transaction type description |
| FILLER | PIC X(08) | Filler | 8 | Reserved |

**VSAM Key:** TRAN-TYPE (bytes 0–1, length 2)
**Used by:** CBTRN03C, COTRTLIC, COTRTUPC

### 5.4 CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-----------|-----------|-----------|-------|-----------------|
| TRAN-CAT-KEY (compound) | — | — | 6 | Composite key |
| → TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code |
| → TRAN-CAT-CD | PIC 9(04) | Numeric display | 4 | Category code |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Category description |
| FILLER | PIC X(04) | Filler | 4 | Reserved |

**VSAM Key:** TRAN-CAT-KEY (bytes 0–5, length 6)
**Used by:** CBTRN03C

---

## 6. Reporting Structures

### 6.1 CVTRA07Y.cpy — Transaction Report Structures

| Structure | Field | PIC Clause | Purpose |
|----------|-------|-----------|---------|
| **REPORT-NAME-HEADER** | REPT-SHORT-NAME | PIC X(38) | Report ID ('DALYREPT') |
| | REPT-LONG-NAME | PIC X(41) | 'Daily Transaction Report' |
| | REPT-START-DATE / REPT-END-DATE | PIC X(10) | Date range for report |
| **TRANSACTION-DETAIL-REPORT** | TRAN-REPORT-TRANS-ID | PIC X(16) | Transaction ID column |
| | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Account ID column |
| | TRAN-REPORT-TYPE-CD | PIC X(02) | Type code |
| | TRAN-REPORT-TYPE-DESC | PIC X(15) | Type description |
| | TRAN-REPORT-CAT-CD | PIC 9(04) | Category code |
| | TRAN-REPORT-CAT-DESC | PIC X(29) | Category description |
| | TRAN-REPORT-SOURCE | PIC X(10) | Source |
| | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Formatted amount (edited) |
| **REPORT-PAGE-TOTALS** | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Page subtotal |
| **REPORT-ACCOUNT-TOTALS** | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Account subtotal |
| **REPORT-GRAND-TOTALS** | REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Grand total |

---

## 7. Security & User Entity

### 7.1 CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | 8 | User ID (primary key) | Required |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | 20 | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | 20 | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | 8 | Password (**plain text!**) | ⚠️ Stored in clear text — must hash in Java migration |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | 1 | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Filler | 23 | Reserved | — |

**Used by:** COSGN00C (authentication), COUSR00C–03C (user CRUD)

### 7.2 UNUSED1Y.cpy — Unused Security Record (identical layout)

Identical field layout to CSUSR01Y with `UNUSED-` prefix. Appears to be a deprecated/unused copy.

---

## 8. CICS Communication & UI Structures

### 8.1 COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|-----------|-----------|-----------|-------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | 4 | Source transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | 8 | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | 4 | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | 8 | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | 8 | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 1 | User type ('A'/'U') |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 1 | Program context flag |
| CDEMO-CUST-ID | PIC 9(09) | Numeric display | 9 | Current customer ID |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | 25 | Customer first name |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | 25 | Customer middle name |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | 25 | Customer last name |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric display | 11 | Current account ID |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | 1 | Account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric display | 16 | Current card number |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | 7 | Last BMS map displayed |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | 7 | Last BMS mapset |

**Used by:** All 21 CICS programs (inter-program data passing)

### 8.2 CVCRD01Y.cpy — Card Working Areas (CICS screen state)

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| CCARD-AID | PIC X(5) | Current AID key (ENTER, CLEAR, PFK01–PFK12, PA1, PA2) |
| CCARD-NEXT-PROG | PIC X(8) | Next program to XCTL to |
| CCARD-NEXT-MAPSET | PIC X(7) | Next BMS mapset |
| CCARD-NEXT-MAP | PIC X(7) | Next BMS map |
| CCARD-ERROR-MSG | PIC X(75) | Error message for screen display |
| CCARD-RETURN-MSG | PIC X(75) | Return/info message for screen |
| CC-ACCT-ID | PIC X(11) / PIC 9(11) | Working account ID (with REDEFINES) |
| CC-CARD-NUM | PIC X(16) / PIC 9(16) | Working card number (with REDEFINES) |
| CC-CUST-ID | PIC X(09) / PIC 9(9) | Working customer ID (with REDEFINES) |

**Used by:** COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C, COTRTLIC, COTRTUPC

### 8.3 CSMSG01Y.cpy — Common Messages

| Field Name | PIC Clause | Value |
|-----------|-----------|-------|
| CCDA-MSG-THANK-YOU | PIC X(50) | 'Thank you for using CardDemo application...' |
| CCDA-MSG-INVALID-KEY | PIC X(50) | 'Invalid key pressed. Please see below...' |

### 8.4 CSMSG02Y.cpy — Abend Data Structure

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| ABEND-CODE | PIC X(4) | Abend code |
| ABEND-CULPRIT | PIC X(8) | Program causing abend |
| ABEND-REASON | PIC X(50) | Abend reason text |
| ABEND-MSG | PIC X(72) | Full abend message |

### 8.5 COTTL01Y.cpy — Screen Title Constants

| Field Name | PIC Clause | Value |
|-----------|-----------|-------|
| CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | 'Thank you for using CCDA application...' |

### 8.6 CSDAT01Y.cpy — Date/Time Working Storage

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| WS-CURDATE | PIC 9(08) | Current date (YYYYMMDD) |
| WS-CURTIME | PIC 9(08) | Current time (HHMMSSTT) |
| WS-CURDATE-MM-DD-YY | — | Formatted MM/DD/YY |
| WS-CURTIME-HH-MM-SS | — | Formatted HH:MM:SS |
| WS-TIMESTAMP | — | Formatted YYYY-MM-DD HH:MM:SS.MMMMMM |

---

## 9. Menu Configuration Copybooks

### 9.1 COMEN02Y.cpy — Main Menu Options (11 entries)

| Option # | Program | Description | User Type |
|---------|---------|-------------|-----------|
| 1 | COACTVWC | Account View | Regular |
| 2 | COACTUPC | Account Update | Regular |
| 3 | COCRDLIC | Credit Card List | Regular |
| 4 | COCRDSLC | Credit Card Detail | Regular |
| 5 | COCRDUPC | Credit Card Update | Regular |
| 6 | COTRN00C | Transaction List | Regular |
| 7 | COTRN01C | Transaction View | Regular |
| 8 | COTRN02C | Transaction Add | Regular |
| 9 | CORPT00C | Report Request | Regular |
| 10 | COBIL00C | Bill Payment | Regular |
| 11 | COPAUS0C | Auth Summary | Regular |

### 9.2 COADM02Y.cpy — Admin Menu Options (6 entries)

| Option # | Program | Description |
|---------|---------|-------------|
| 1 | COUSR00C | User List |
| 2 | COUSR01C | User Add |
| 3 | COUSR02C | User Update |
| 4 | COUSR03C | User Delete |
| 5 | COTRTLIC | Transaction Type List |
| 6 | COTRTUPC | Transaction Type Update |

---

## 10. Date Validation Copybooks

### 10.1 CSUTLDWY.cpy — Date Validation Working Storage

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| WS-EDIT-DATE-CCYYMMDD | — | Date being validated (group item) |
| → WS-EDIT-DATE-CC | PIC X(2) / PIC 9(2) | Century (88-level: 19, 20) |
| → WS-EDIT-DATE-YY | PIC X(2) / PIC 9(2) | Year |
| → WS-EDIT-DATE-MM | PIC X(2) / PIC 9(2) | Month (88-level: 1–12) |
| → WS-EDIT-DATE-DD | PIC X(2) / PIC 9(2) | Day (88-level: 1–31) |
| WS-EDIT-DATE-FLGS | — | Validation flags (year/month/day OK/NOT-OK/BLANK) |
| WS-DATE-FORMAT | PIC X(08) | Format mask ('YYYYMMDD') |
| WS-DATE-VALIDATION-RESULT | — | LE CEEDAYS result (severity, message code, result) |

**88-level validation values:**
- `THIS-CENTURY` = 20, `LAST-CENTURY` = 19
- `WS-VALID-MONTH` = 1–12
- `WS-31-DAY-MONTH` = 1, 3, 5, 7, 8, 10, 12
- `WS-FEBRUARY` = 2
- `WS-VALID-DAY` = 1–31

### 10.2 CSUTLDPY.cpy — Date Validation Procedure Division Logic

Provides reusable paragraphs:
- `EDIT-DATE-CCYYMMDD` — Full date validation
- `EDIT-YEAR-CCYY` — Year/century validation (19xx, 20xx only)
- `EDIT-MONTH` — Month range validation (1–12)
- `EDIT-DAY` — Day range validation (1–31 with month-dependent rules)
- `EDIT-DAY-MONTH-YEAR` — Cross-field validation (30-day months, Feb 28/29, leap year)
- `EDIT-DATE-LE` — LE CEEDAYS service validation (calls CSUTLDTC)
- `EDIT-DATE-OF-BIRTH` — Future date rejection

### 10.3 CODATECN.cpy — Date Conversion Parameters

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| CODATECN-TYPE | PIC X | Conversion type flag |
| CODATECN-INP-DATE | PIC X(20) | Input date (YYYYMMDD or YYYY-MM-DD variants) |
| CODATECN-OUTTYPE | — | Output format type |
| CODATECN-OUT-DATE | — | Converted output date |

**Used by:** CBACT01C via CALL 'COBDATFT'

---

## 11. Lookup/Validation Copybooks

### 11.1 CSLKPCDY.cpy — Lookup Code Repository

Contains 88-level condition names for:
- **US State codes** — 50 valid 2-character state codes
- **ZIP code prefixes** — Valid ZIP code first-3-digit ranges by state
- **Phone area codes** — NANPA area codes (201–999 range)

Example:
```cobol
05 WS-US-PHONE-AREA-CODE-TO-EDIT  PIC XXX.
   88 VALID-PHONE-AREA-CODE        VALUES 201 THRU 999.
```

### 11.2 CSSETATY.cpy — Screen Attribute Setter (Macro)

Template copybook used with `COPY REPLACING` to set BMS field attributes:
```cobol
COPY CSSETATY REPLACING (TESTVAR1) BY field-name
                        (SCRNVAR2) BY screen-field
                        (MAPNAME3) BY map-name.
```
Sets field to red and marks with `*` when validation fails. Used 25× in COACTUPC.

### 11.3 CSSTRPFY.cpy — Store PFKey Procedure

Maps EIBAID (CICS attention identifiers) to CCARD-AID values using EVALUATE TRUE.
Maps DFHENTER, DFHCLEAR, DFHPA1–PA2, DFHPF1–PF24 to CCARD-AID-xxx condition names.

---

## 12. Export/Import Structure

### 12.1 CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

| Field Name | PIC Clause | Bytes | Purpose |
|-----------|-----------|-------|---------|
| EXPORT-REC-TYPE | PIC X(1) | 1 | Record type: 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=XREF, 'D'=Card |
| EXPORT-TIMESTAMP | PIC X(26) | 26 | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | 4 | Sequence number (COMP = binary) |
| EXPORT-BRANCH-ID | PIC X(4) | 4 | Branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | 5 | Region code |
| EXPORT-RECORD-DATA | PIC X(460) | 460 | Record data (REDEFINES per type) |

**REDEFINES variants:**
- `EXPORT-CUSTOMER-DATA` — Customer fields with COMP-3 for FICO score
- `EXPORT-ACCOUNT-DATA` — Account fields with COMP-3 for balances
- `EXPORT-TRANSACTION-DATA` — Transaction fields with COMP-3 for amount
- `EXPORT-CARD-XREF-DATA` — XREF with COMP for account ID
- `EXPORT-CARD-DATA` — Card fields with COMP for account ID and CVV

**Note:** Export uses storage-optimized formats (COMP, COMP-3) vs. the DISPLAY format of source copybooks.

---

## 13. Authorization Sub-Application Copybooks

### 13.1 CIPAUSMY.cpy — Pending Authorization Summary (IMS Segment)

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| PA-SM-CARD-NUM | PIC X(16) | Card number |
| PA-SM-TRANSACTION-ID | PIC X(15) | Transaction ID |
| PA-SM-AUTH-DATE | PIC X(06) | Authorization date |
| PA-SM-AUTH-TIME | PIC X(06) | Authorization time |
| PA-SM-STATUS | PIC X(02) | Status code |

### 13.2 CIPAUDTY.cpy — Pending Authorization Detail (IMS Segment)

Contains detailed authorization fields including amounts, merchant data, and decision codes.

### 13.3 CCPAURQY.cpy — Authorization Request Message (MQ)

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Card expiry |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Message source |

### 13.4 CCPAURLY.cpy — Authorization Response Message (MQ)

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Authorization ID |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Response code (approve/decline) |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Reason code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Approved amount (edited) |

### 13.5 CCPAUERY.cpy — Authorization Error Log

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| ERR-DATE | PIC X(06) | Error date |
| ERR-TIME | PIC X(06) | Error time |
| ERR-APPLICATION | PIC X(08) | Application ID |
| ERR-PROGRAM | PIC X(08) | Program name |
| ERR-LEVEL | PIC X(01) | Severity: L=Log, I=Info, W=Warning, C=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Subsystem: A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File |
| ERR-CODE-1 / ERR-CODE-2 | PIC X(09) | Error codes |
| ERR-MESSAGE | PIC X(50) | Error message text |
| ERR-EVENT-KEY | PIC X(20) | Related event key |

### 13.6 IMS PCB/Function Copybooks

| Copybook | Purpose |
|---------|---------|
| IMSFUNCS.cpy | IMS DL/I function codes (GU, GN, GNP, ISRT, DLET, REPL) |
| PAUTBPCB.CPY | IMS PCB for PAUT database (auth summary/detail segments) |
| PASFLPCB.CPY | IMS PCB for PASFL database (auth summary flat) |
| PADFLPCB.CPY | IMS PCB for PADFL database (auth detail flat) |

---

## 14. DB2 Include Copybooks (Transaction Type Sub-App)

### 14.1 CSDB2RWY.cpy — DB2 Working Storage (SQLCA + host variables)

DB2 working storage areas, SQLCA include, and host variable declarations for transaction type queries.

### 14.2 CSDB2RPY.cpy — DB2 Procedure Division (error reporting)

DB2 error reporting paragraphs for SQL return code handling.

### 14.3 DCLTRTYP / DCLTRCAT — DB2 Table DCLGENs (included via EXEC SQL INCLUDE)

| Table | Key Columns | Description |
|-------|------------|-------------|
| TR_TYPE (DCLTRTYP) | TRAN-TYPE PIC X(02) | Transaction type master — maps to CVTRA03Y |
| TR_CAT (DCLTRCAT) | TRAN-TYPE-CD + TRAN-CAT-CD | Transaction category — maps to CVTRA04Y |

---

## 15. Entity Relationship Summary

```
Customer (CVCUS01Y)
    │ CUST-ID ──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
    │                        │ XREF-CARD-NUM                    │ ACCT-GROUP-ID
    │                        │                                   │
    │                   Card (CVACT02Y)                    Disclosure Group
    │                   CARD-ACCT-ID ──► ACCT-ID           (CVTRA02Y)
    │                        │                             DIS-INT-RATE
    │                        │
    │                  Transaction (CVTRA05Y)
    │                  TRAN-CARD-NUM ──► XREF-CARD-NUM
    │                  TRAN-TYPE-CD  ──► Tran Type (CVTRA03Y)
    │                  TRAN-CAT-CD   ──► Tran Category (CVTRA04Y)
    │
    │                  Tran Cat Balance (CVTRA01Y)
    │                  TRANCAT-ACCT-ID ──► ACCT-ID
    │
    └── Auth Request/Response (CCPAURQY/CCPAURLY)
        Auth Summary/Detail (CIPAUSMY/CIPAUDTY) [IMS segments]
```
