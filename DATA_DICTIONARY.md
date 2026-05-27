# DATA_DICTIONARY.md — CardDemo Copybook Field Reference

> Auto-generated from all 30 copybooks in `app/cpy/`.
> Fields grouped by business entity; business meanings inferred from context.

---

## Table of Contents

1. [Account Entity](#1-account-entity)
2. [Customer Entity](#2-customer-entity)
3. [Card Entity](#3-card-entity)
4. [Card Cross-Reference Entity](#4-card-cross-reference-entity)
5. [Transaction Entity](#5-transaction-entity)
6. [Transaction Reference Data](#6-transaction-reference-data)
7. [Transaction Reporting](#7-transaction-reporting)
8. [User Security Entity](#8-user-security-entity)
9. [CICS Communication (COMMAREA)](#9-cics-communication-commarea)
10. [CICS Working Storage — UI & Navigation](#10-cics-working-storage--ui--navigation)
11. [Date/Time Structures](#11-datetime-structures)
12. [Export/Import (Migration)](#12-exportimport-migration)
13. [Utility Copybooks](#13-utility-copybooks)

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

Used by: CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | `9(11)` | Numeric, 11 digits | Unique account identifier; primary key of ACCTFILE | Must be numeric; used as VSAM primary key |
| ACCT-ACTIVE-STATUS | `X(01)` | Alphanumeric, 1 char | Account status flag | Expected values: 'Y' (active), 'N' (inactive) |
| ACCT-CURR-BAL | `S9(10)V99` | Signed decimal, 12 digits (10.2) | Current account balance | Signed; updated by transactions and payments |
| ACCT-CREDIT-LIMIT | `S9(10)V99` | Signed decimal, 12 digits (10.2) | Maximum credit limit for the account | Must be positive; validated in COACTUPC |
| ACCT-CASH-CREDIT-LIMIT | `S9(10)V99` | Signed decimal, 12 digits (10.2) | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | `X(10)` | Alphanumeric, 10 chars | Date account was opened | Format: YYYY-MM-DD; validated via CSUTLDPY |
| ACCT-EXPIRAION-DATE | `X(10)` | Alphanumeric, 10 chars | Account expiration date | Format: YYYY-MM-DD (note: original misspelling preserved) |
| ACCT-REISSUE-DATE | `X(10)` | Alphanumeric, 10 chars | Date account was last reissued | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | `S9(10)V99` | Signed decimal, 12 digits (10.2) | Total credits in current billing cycle | Reset each cycle |
| ACCT-CURR-CYC-DEBIT | `S9(10)V99` | Signed decimal, 12 digits (10.2) | Total debits in current billing cycle | Reset each cycle |
| ACCT-ADDR-ZIP | `X(10)` | Alphanumeric, 10 chars | Account holder ZIP/postal code | US ZIP+4 or international postal code |
| ACCT-GROUP-ID | `X(10)` | Alphanumeric, 10 chars | Disclosure/interest rate group assignment | Links to DISCGRP for interest calculation |
| FILLER | `X(178)` | Filler | Reserved for future use | Padding to reach 300-byte record length |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

Used by: CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CUST-ID | `9(09)` | Numeric, 9 digits | Unique customer identifier; primary key of CUSTFILE | Must be numeric; used as VSAM primary key |
| CUST-FIRST-NAME | `X(25)` | Alphanumeric, 25 chars | Customer first name | Required; non-blank |
| CUST-MIDDLE-NAME | `X(25)` | Alphanumeric, 25 chars | Customer middle name | Optional |
| CUST-LAST-NAME | `X(25)` | Alphanumeric, 25 chars | Customer last name | Required; non-blank |
| CUST-ADDR-LINE-1 | `X(50)` | Alphanumeric, 50 chars | Primary street address | Required |
| CUST-ADDR-LINE-2 | `X(50)` | Alphanumeric, 50 chars | Secondary address line | Optional |
| CUST-ADDR-LINE-3 | `X(50)` | Alphanumeric, 50 chars | Tertiary address line | Optional |
| CUST-ADDR-STATE-CD | `X(02)` | Alphanumeric, 2 chars | US state code | Validated against CSLKPCDY state code table |
| CUST-ADDR-COUNTRY-CD | `X(03)` | Alphanumeric, 3 chars | ISO country code | 3-character country code |
| CUST-ADDR-ZIP | `X(10)` | Alphanumeric, 10 chars | ZIP/postal code | US ZIP+4 format or international |
| CUST-PHONE-NUM-1 | `X(15)` | Alphanumeric, 15 chars | Primary phone number | Area code validated against CSLKPCDY in COACTUPC |
| CUST-PHONE-NUM-2 | `X(15)` | Alphanumeric, 15 chars | Secondary phone number | Area code validated against CSLKPCDY in COACTUPC |
| CUST-SSN | `9(09)` | Numeric, 9 digits | Social Security Number | Must be 9 numeric digits |
| CUST-GOVT-ISSUED-ID | `X(20)` | Alphanumeric, 20 chars | Government-issued identification (e.g., driver's license) | Optional alternate ID |
| CUST-DOB-YYYY-MM-DD | `X(10)` | Alphanumeric, 10 chars | Date of birth | Format: YYYY-MM-DD; validated via CSUTLDPY date routines |
| CUST-EFT-ACCOUNT-ID | `X(10)` | Alphanumeric, 10 chars | Electronic Funds Transfer bank account | Used for automatic payments |
| CUST-PRI-CARD-HOLDER-IND | `X(01)` | Alphanumeric, 1 char | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| CUST-FICO-CREDIT-SCORE | `9(03)` | Numeric, 3 digits | FICO credit score | Range: 300–850 |
| FILLER | `X(168)` | Filler | Reserved | Padding to 500-byte record |

### CUSTREC.cpy — Customer Record (Statement Variant)

Used by: CBSTM03A

Identical structure to CVCUS01Y with minor naming differences (CUST-DOB-YYYYMMDD vs CUST-DOB-YYYY-MM-DD). Used specifically in statement generation.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

Used by: CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | `X(16)` | Alphanumeric, 16 chars | Credit card number; primary key of CARDFILE | 16-digit card number; VSAM primary key |
| CARD-ACCT-ID | `9(11)` | Numeric, 11 digits | Associated account identifier | Must exist in ACCTFILE; used for AIX lookup |
| CARD-CVV-CD | `9(03)` | Numeric, 3 digits | Card Verification Value (security code) | 3-digit CVV |
| CARD-EMBOSSED-NAME | `X(50)` | Alphanumeric, 50 chars | Name embossed on physical card | Validated for non-blank in COCRDUPC |
| CARD-EXPIRAION-DATE | `X(10)` | Alphanumeric, 10 chars | Card expiration date | Format: YYYY-MM-DD (note: original misspelling) |
| CARD-ACTIVE-STATUS | `X(01)` | Alphanumeric, 1 char | Card active/inactive status | 'Y' = active, 'N' = inactive |
| FILLER | `X(59)` | Filler | Reserved | Padding to 150-byte record |

---

## 4. Card Cross-Reference Entity

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

Used by: CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | `X(16)` | Alphanumeric, 16 chars | Card number; primary key of XREFFILE | Links card to customer and account |
| XREF-CUST-ID | `9(09)` | Numeric, 9 digits | Customer who owns this card | Must exist in CUSTFILE |
| XREF-ACCT-ID | `9(11)` | Numeric, 11 digits | Account associated with this card | Must exist in ACCTFILE; alternate index key |
| FILLER | `X(14)` | Filler | Reserved | Padding to 50-byte record |

---

## 5. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

Used by: CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | `X(16)` | Alphanumeric, 16 chars | Unique transaction identifier; primary key of TRANSACT | System-generated; VSAM primary key |
| TRAN-TYPE-CD | `X(02)` | Alphanumeric, 2 chars | Transaction type code | Must exist in TRANTYPE reference file |
| TRAN-CAT-CD | `9(04)` | Numeric, 4 digits | Transaction category code | Must exist in TRANCATG reference file |
| TRAN-SOURCE | `X(10)` | Alphanumeric, 10 chars | Source of the transaction (e.g., POS, ATM, online) | Informational |
| TRAN-DESC | `X(100)` | Alphanumeric, 100 chars | Transaction description / memo | Free-text description |
| TRAN-AMT | `S9(09)V99` | Signed decimal, 11 digits (9.2) | Transaction amount | Signed: positive=debit, negative=credit |
| TRAN-MERCHANT-ID | `9(09)` | Numeric, 9 digits | Merchant identifier | Numeric merchant code |
| TRAN-MERCHANT-NAME | `X(50)` | Alphanumeric, 50 chars | Merchant business name | Informational |
| TRAN-MERCHANT-CITY | `X(50)` | Alphanumeric, 50 chars | Merchant city location | Informational |
| TRAN-MERCHANT-ZIP | `X(10)` | Alphanumeric, 10 chars | Merchant ZIP/postal code | Informational |
| TRAN-CARD-NUM | `X(16)` | Alphanumeric, 16 chars | Card number used for this transaction | Must exist in CARDFILE |
| TRAN-ORIG-TS | `X(26)` | Alphanumeric, 26 chars | Original transaction timestamp | Format: YYYY-MM-DD-HH.MM.SS.NNNNNN |
| TRAN-PROC-TS | `X(26)` | Alphanumeric, 26 chars | Processing timestamp (when posted) | Format: YYYY-MM-DD-HH.MM.SS.NNNNNN |
| FILLER | `X(20)` | Filler | Reserved | Padding to 350-byte record |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Used by: CBTRN01C, CBTRN02C

Identical layout to CVTRA05Y with `DALYTRAN-` prefix. Represents daily incoming transactions before posting to the master file.

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| DALYTRAN-ID | `X(16)` | Alphanumeric | Daily transaction identifier |
| DALYTRAN-TYPE-CD | `X(02)` | Alphanumeric | Transaction type code |
| DALYTRAN-CAT-CD | `9(04)` | Numeric | Transaction category code |
| DALYTRAN-SOURCE | `X(10)` | Alphanumeric | Transaction source |
| DALYTRAN-DESC | `X(100)` | Alphanumeric | Transaction description |
| DALYTRAN-AMT | `S9(09)V99` | Signed decimal | Transaction amount |
| DALYTRAN-MERCHANT-ID | `9(09)` | Numeric | Merchant identifier |
| DALYTRAN-MERCHANT-NAME | `X(50)` | Alphanumeric | Merchant name |
| DALYTRAN-MERCHANT-CITY | `X(50)` | Alphanumeric | Merchant city |
| DALYTRAN-MERCHANT-ZIP | `X(10)` | Alphanumeric | Merchant ZIP code |
| DALYTRAN-CARD-NUM | `X(16)` | Alphanumeric | Card number |
| DALYTRAN-ORIG-TS | `X(26)` | Alphanumeric | Original timestamp |
| DALYTRAN-PROC-TS | `X(26)` | Alphanumeric | Processing timestamp |
| FILLER | `X(20)` | Filler | Reserved |

### COSTM01.CPY — Statement Transaction Record

Used by: CBSTM03A

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| TRAN-KEY (group) | — | Group | Composite transaction key |
| → TRAN-CARD-NUM | `X(16)` | Alphanumeric | Card number |
| → TRAN-ORIG-TS | `X(26)` | Alphanumeric | Original timestamp |
| TRAN-AMT | `S9(09)V99` | Signed decimal | Transaction amount |
| TRAN-TYPE-CD | `X(02)` | Alphanumeric | Transaction type code |
| TRAN-CAT-CD | `9(04)` | Numeric | Category code |
| TRAN-SOURCE | `X(10)` | Alphanumeric | Source channel |
| TRAN-DESC | `X(100)` | Alphanumeric | Description |
| TRAN-MERCHANT-ID | `9(09)` | Numeric | Merchant ID |
| TRAN-MERCHANT-NAME | `X(50)` | Alphanumeric | Merchant name |
| TRAN-MERCHANT-CITY | `X(50)` | Alphanumeric | Merchant city |
| TRAN-MERCHANT-ZIP | `X(10)` | Alphanumeric | Merchant ZIP |
| TRAN-PROC-TS | `X(26)` | Alphanumeric | Processing timestamp |
| FILLER | `X(20)` | Filler | Reserved |

---

## 6. Transaction Reference Data

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

Used by: CBACT04C, CBTRN02C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY (group) | — | Group | Composite key for category balance | |
| → TRANCAT-ACCT-ID | `9(11)` | Numeric, 11 digits | Account identifier | Must exist in ACCTFILE |
| → TRANCAT-TYPE-CD | `X(02)` | Alphanumeric, 2 chars | Transaction type code | Must exist in TRANTYPE |
| → TRANCAT-CD | `9(04)` | Numeric, 4 digits | Transaction category code | Must exist in TRANCATG |
| TRAN-CAT-BAL | `S9(09)V99` | Signed decimal, 11 digits | Running balance for this category/type/account | Updated during transaction posting |
| FILLER | `X(22)` | Filler | Reserved | |

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

Used by: CBACT04C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| DIS-GROUP-KEY (group) | — | Group | Composite key for interest rate lookup | |
| → DIS-ACCT-GROUP-ID | `X(10)` | Alphanumeric, 10 chars | Account group identifier | Links to ACCT-GROUP-ID in account |
| → DIS-TRAN-TYPE-CD | `X(02)` | Alphanumeric, 2 chars | Transaction type code | |
| → DIS-TRAN-CAT-CD | `9(04)` | Numeric, 4 digits | Transaction category code | |
| DIS-INT-RATE | `S9(04)V99` | Signed decimal, 6 digits (4.2) | Annual interest rate for this group/type/category | Used by CBACT04C for interest calculation |
| FILLER | `X(28)` | Filler | Reserved | |

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

Used by: CBTRN03C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | `X(02)` | Alphanumeric, 2 chars | Transaction type code; primary key | VSAM primary key |
| TRAN-TYPE-DESC | `X(50)` | Alphanumeric, 50 chars | Human-readable description of the type | Used in report headers |
| FILLER | `X(08)` | Filler | Reserved | |

### CVTRA04Y.cpy — Transaction Category (RECLN 60)

Used by: CBTRN03C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY (group) | — | Group | Composite key | |
| → TRAN-TYPE-CD | `X(02)` | Alphanumeric, 2 chars | Transaction type code | |
| → TRAN-CAT-CD | `9(04)` | Numeric, 4 digits | Transaction category code | |
| TRAN-CAT-TYPE-DESC | `X(50)` | Alphanumeric, 50 chars | Human-readable description of the category | Used in report detail lines |
| FILLER | `X(04)` | Filler | Reserved | |

---

## 7. Transaction Reporting

### CVTRA07Y.cpy — Report Layout Structures

Used by: CBTRN03C

| Structure / Field | PIC Clause | Data Type | Business Meaning |
|-------------------|-----------|-----------|-----------------|
| **REPORT-NAME-HEADER** | | Group | Report masthead |
| → REPT-SHORT-NAME | `X(38)` | Alphanumeric | Report identifier ('DALYREPT') |
| → REPT-LONG-NAME | `X(41)` | Alphanumeric | Report title ('Daily Transaction Report') |
| → REPT-DATE-HEADER | `X(12)` | Alphanumeric | 'Date Range: ' label |
| → REPT-START-DATE | `X(10)` | Alphanumeric | Report start date |
| → REPT-END-DATE | `X(10)` | Alphanumeric | Report end date |
| **TRANSACTION-DETAIL-REPORT** | | Group | Detail line (133 chars) |
| → TRAN-REPORT-TRANS-ID | `X(16)` | Alphanumeric | Transaction ID |
| → TRAN-REPORT-ACCOUNT-ID | `X(11)` | Alphanumeric | Account ID |
| → TRAN-REPORT-TYPE-CD | `X(02)` | Alphanumeric | Type code |
| → TRAN-REPORT-TYPE-DESC | `X(15)` | Alphanumeric | Type description |
| → TRAN-REPORT-CAT-CD | `9(04)` | Numeric | Category code |
| → TRAN-REPORT-CAT-DESC | `X(29)` | Alphanumeric | Category description |
| → TRAN-REPORT-SOURCE | `X(10)` | Alphanumeric | Transaction source |
| → TRAN-REPORT-AMT | `-ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Formatted amount |
| **REPORT-PAGE-TOTALS** | | Group | Page subtotal line |
| → REPT-PAGE-TOTAL | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Page total amount |
| **REPORT-ACCOUNT-TOTALS** | | Group | Account subtotal line |
| → REPT-ACCOUNT-TOTAL | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Account total amount |
| **REPORT-GRAND-TOTALS** | | Group | Grand total line |
| → REPT-GRAND-TOTAL | `+ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Grand total amount |

---

## 8. User Security Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

Used by: COACTUPC, COACTVWC, COADM01C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, COSGN00C, COUSR00C–03C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | `X(08)` | Alphanumeric, 8 chars | User login identifier; primary key of USRSEC | Must be unique; required |
| SEC-USR-FNAME | `X(20)` | Alphanumeric, 20 chars | User first name | Required for new users |
| SEC-USR-LNAME | `X(20)` | Alphanumeric, 20 chars | User last name | Required for new users |
| SEC-USR-PWD | `X(08)` | Alphanumeric, 8 chars | User password (stored in clear text) | Required; max 8 characters |
| SEC-USR-TYPE | `X(01)` | Alphanumeric, 1 char | User type/role | 'A' = admin (CDEMO-USRTYP-ADMIN), 'U' = regular user |
| SEC-USR-FILLER | `X(23)` | Filler | Reserved | Padding to 80-byte record |

### UNUSED1Y.cpy — Unused Data Structure (RECLN 80)

Not referenced by any program. Identical layout to CSUSR01Y with `UNUSED-` prefix. Appears to be a deprecated copy.

---

## 9. CICS Communication (COMMAREA)

### COCOM01Y.cpy — CICS COMMAREA Structure

Used by: All 17 online (CICS) programs

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| **CDEMO-GENERAL-INFO** (group) | | | Program navigation state | |
| → CDEMO-FROM-TRANID | `X(04)` | Alphanumeric, 4 chars | Source CICS transaction ID | Set before XCTL |
| → CDEMO-FROM-PROGRAM | `X(08)` | Alphanumeric, 8 chars | Source program name | Set before XCTL |
| → CDEMO-TO-TRANID | `X(04)` | Alphanumeric, 4 chars | Target CICS transaction ID | Used by receiving program |
| → CDEMO-TO-PROGRAM | `X(08)` | Alphanumeric, 8 chars | Target program name | Used by receiving program |
| → CDEMO-USER-ID | `X(08)` | Alphanumeric, 8 chars | Authenticated user ID | Set at sign-on; propagated |
| → CDEMO-USER-TYPE | `X(01)` | Alphanumeric, 1 char | User role | 88 CDEMO-USRTYP-ADMIN VALUE 'A'; 88 CDEMO-USRTYP-USER VALUE 'U' |
| → CDEMO-PGM-CONTEXT | `9(01)` | Numeric, 1 digit | Program entry context | 88 CDEMO-PGM-ENTER VALUE 0; 88 CDEMO-PGM-REENTER VALUE 1 |
| **CDEMO-CUSTOMER-INFO** (group) | | | Selected customer context | |
| → CDEMO-CUST-ID | `9(09)` | Numeric, 9 digits | Current customer ID | Passed between programs |
| → CDEMO-CUST-FNAME | `X(25)` | Alphanumeric, 25 chars | Customer first name | Display context |
| → CDEMO-CUST-MNAME | `X(25)` | Alphanumeric, 25 chars | Customer middle name | Display context |
| → CDEMO-CUST-LNAME | `X(25)` | Alphanumeric, 25 chars | Customer last name | Display context |
| **CDEMO-ACCOUNT-INFO** (group) | | | Selected account context | |
| → CDEMO-ACCT-ID | `9(11)` | Numeric, 11 digits | Current account ID | Passed between programs |
| → CDEMO-ACCT-STATUS | `X(01)` | Alphanumeric, 1 char | Account active status | Passed for display/filter |
| **CDEMO-CARD-INFO** (group) | | | Selected card context | |
| → CDEMO-CARD-NUM | `9(16)` | Numeric, 16 digits | Current card number | Passed between programs |
| **CDEMO-MORE-INFO** (group) | | | UI state tracking | |
| → CDEMO-LAST-MAP | `X(7)` | Alphanumeric, 7 chars | Last BMS map displayed | Used for SEND MAP |
| → CDEMO-LAST-MAPSET | `X(7)` | Alphanumeric, 7 chars | Last BMS mapset used | Used for SEND MAP |

### CVCRD01Y.cpy — Card Work Areas (CICS Online)

Used by: COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CCARD-AID | `X(5)` | Alphanumeric, 5 chars | Attention Identifier (mapped from EIBAID) |
| — 88-level conditions | — | — | CCARD-AID-ENTER, CCARD-AID-CLEAR, CCARD-AID-PA1/PA2, CCARD-AID-PFK01–PFK12 |
| CCARD-NEXT-PROG | `X(8)` | Alphanumeric, 8 chars | Next program to XCTL to |
| CCARD-NEXT-MAPSET | `X(7)` | Alphanumeric, 7 chars | Next BMS mapset to display |
| CCARD-NEXT-MAP | `X(7)` | Alphanumeric, 7 chars | Next BMS map to send |
| CCARD-ERROR-MSG | `X(75)` | Alphanumeric, 75 chars | Error message for display |
| CCARD-RETURN-MSG | `X(75)` | Alphanumeric, 75 chars | Return/confirmation message |
| CC-ACCT-ID | `X(11)` | Alphanumeric, 11 chars | Working account ID (with REDEFINES to `9(11)`) |
| CC-CARD-NUM | `X(16)` | Alphanumeric, 16 chars | Working card number (with REDEFINES to `9(16)`) |
| CC-CUST-ID | `X(09)` | Alphanumeric, 9 chars | Working customer ID (with REDEFINES to `9(9)`) |

---

## 10. CICS Working Storage — UI & Navigation

### COTTL01Y.cpy — Screen Titles

Used by: All 17 CICS online programs

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CCDA-TITLE01 | `X(40)` | Alphanumeric | Main application title ('AWS Mainframe Modernization CardDemo') |
| CCDA-TITLE02 | `X(25)` | Alphanumeric | Subtitle line |

### CSDAT01Y.cpy — Date/Time Working Storage

Used by: All 17 CICS online programs

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| WS-CURDATE | `X(08)` | Alphanumeric, 8 chars | Current date YYYYMMDD (from CURRENT-DATE) |
| WS-CURDATE-YEAR | `X(04)` | Alphanumeric, 4 chars | Year component |
| WS-CURDATE-MONTH | `X(02)` | Alphanumeric, 2 chars | Month component |
| WS-CURDATE-DAY | `X(02)` | Alphanumeric, 2 chars | Day component |
| WS-CURTIME | `X(08)` | Alphanumeric, 8 chars | Current time HHMMSSNN |
| WS-CURTIME-HOURS | `X(02)` | Alphanumeric, 2 chars | Hours |
| WS-CURTIME-MINUTE | `X(02)` | Alphanumeric, 2 chars | Minutes |
| WS-CURTIME-SECOND | `X(02)` | Alphanumeric, 2 chars | Seconds |
| WS-CURTIME-DTEFMT | `X(10)` | Alphanumeric, 10 chars | Formatted date (MM/DD/YYYY) |
| WS-CURTIME-TIMFMT | `X(08)` | Alphanumeric, 8 chars | Formatted time (HH:MM:SS) |

### CSMSG01Y.cpy — Common Messages

Used by: All 17 CICS online programs

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CCDA-MSG-THANK-YOU | `X(50)` | Alphanumeric | 'Thank you for using CardDemo application' |
| CCDA-MSG-INVALID-KEY | `X(50)` | Alphanumeric | 'Invalid key pressed. Please see below ...' |

### CSMSG02Y.cpy — Abend Data

Used by: COACTUPC, COACTVWC, COCRDSLC, COCRDUPC

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| ABEND-CODE | `X(4)` | Alphanumeric, 4 chars | CICS abend code |
| ABEND-CULPRIT | `X(8)` | Alphanumeric, 8 chars | Program that caused the abend |
| ABEND-REASON | `X(50)` | Alphanumeric, 50 chars | Reason text |
| ABEND-MSG | `X(72)` | Alphanumeric, 72 chars | Full abend message |

### COMEN02Y.cpy — Main Menu Options Table

Used by: COMEN01C

Defines 11 menu options for regular users. Each option has:

| Field per Option | PIC Clause | Meaning |
|-----------------|-----------|---------|
| Option Number | `9(02)` | Menu selection number (1–11) |
| Description | `X(35)` | Menu item display text |
| Program Name | `X(08)` | COBOL program to XCTL to |

**Menu Options:** (1) View Account, (2) Update Account, (3) View Card, (4) Card List, (5) Card Update, (6) Bill Payment, (7) Transaction List, (8) Transaction View, (9) Transaction Add, (10) Sign Off, (11) Report

### COADM02Y.cpy — Admin Menu Options Table

Used by: COADM01C

Defines 6 admin-only menu options. Structure same as COMEN02Y.

**Menu Options:** (1) User List (COUSR00C), (2) User Add (COUSR01C), (3) User Update (COUSR02C), (4) User Delete (COUSR03C), (5) Card List (COTRTLIC), (6) Card Update (COTRTUPC)

---

## 11. Date/Time Structures

### CSUTLDWY.cpy — Date Validation Working Storage

Used by: COACTUPC

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| WS-EDIT-DATE-CCYYMMDD (group) | | Group | Date being validated | |
| → WS-EDIT-DATE-CC | `X(2)` | Alphanumeric | Century (with REDEFINES to `9(2)`) | 88 THIS-CENTURY VALUE 20; 88 LAST-CENTURY VALUE 19 |
| → WS-EDIT-DATE-YY | `X(2)` | Alphanumeric | Year within century | |
| → WS-EDIT-DATE-MM | `X(2)` | Alphanumeric | Month (with REDEFINES to `9(2)`) | 88 WS-VALID-MONTH VALUES 1 THROUGH 12 |
| → WS-EDIT-DATE-DD | `X(2)` | Alphanumeric | Day (with REDEFINES to `9(2)`) | 88 WS-VALID-DAY VALUES 1 THROUGH 31 |
| WS-EDIT-DATE-CCYYMMDD-N | `9(8)` | Numeric (REDEFINES) | Numeric date for computation | |
| WS-EDIT-DATE-BINARY | `S9(9) BINARY` | Binary integer | Lillian date (days since epoch) | Computed via CEEDAYS |
| WS-CURRENT-DATE (group) | | Group | System current date for comparison | |
| WS-EDIT-DATE-FLGS (group) | | Group | Validation result flags | |
| → WS-EDIT-YEAR-FLG | `X(01)` | Flag | Year validation status | 88 FLG-YEAR-ISVALID VALUE LOW-VALUES; 88 FLG-YEAR-NOT-OK VALUE '0'; 88 FLG-YEAR-BLANK VALUE 'B' |
| → WS-EDIT-MONTH | `X(01)` | Flag | Month validation status | 88 FLG-MONTH-ISVALID, FLG-MONTH-NOT-OK, FLG-MONTH-BLANK |
| → WS-EDIT-DAY | `X(01)` | Flag | Day validation status | 88 FLG-DAY-ISVALID, FLG-DAY-NOT-OK, FLG-DAY-BLANK |
| WS-DATE-FORMAT | `X(08)` | Alphanumeric | Date format mask | VALUE 'YYYYMMDD' |
| WS-DATE-VALIDATION-RESULT | | Group | Detailed validation output | |
| → WS-SEVERITY | `X(04)` / `9(4)` | Severity code | LE condition severity | 0 = success |
| → WS-MSG-NO | `X(04)` / `9(4)` | Message number | LE condition message number | |
| → WS-RESULT | `X(15)` | Alphanumeric | Human-readable result | |
| → WS-DATE | `X(10)` | Alphanumeric | Echoed test date | |

### CODATECN.cpy — Date Conversion Record

Used by: CBACT01C

Contains working storage fields for date format conversion between mainframe date formats (Julian, Gregorian, packed).

---

## 12. Export/Import (Migration)

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

Used by: CBEXPORT, CBIMPORT

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| **EXPORT-RECORD** (top-level) | | Group | Polymorphic record via REDEFINES |
| EXPORT-REC-TYPE | `X(1)` | Alphanumeric | Record type discriminator: 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card |
| EXPORT-TIMESTAMP | `X(26)` | Alphanumeric | Export timestamp |
| → EXPORT-DATE | `X(10)` | Alphanumeric | Date component (REDEFINES) |
| → EXPORT-TIME | `X(15)` | Alphanumeric | Time component (REDEFINES) |
| EXPORT-SEQUENCE-NUM | `9(9) COMP` | Binary integer | Sequence number within export |
| EXPORT-BRANCH-ID | `X(4)` | Alphanumeric | Originating branch ID |
| EXPORT-REGION-CODE | `X(5)` | Alphanumeric | Region code |
| EXPORT-RECORD-DATA | `X(460)` | Alphanumeric | Generic data area (REDEFINES below) |

**REDEFINES for EXPORT-RECORD-DATA:**

| REDEFINES Name | Notable Fields | Storage Differences from Base |
|---------------|----------------|------------------------------|
| EXPORT-CUSTOMER-DATA | EXP-CUST-ID `9(09) COMP`, EXP-CUST-FICO-CREDIT-SCORE `9(03) COMP-3`, OCCURS 3 for addr, OCCURS 2 for phone | Binary/packed decimal compression |
| EXPORT-ACCOUNT-DATA | EXP-ACCT-CURR-BAL `S9(10)V99 COMP-3`, EXP-ACCT-CASH-CREDIT-LIMIT `COMP-3`, EXP-ACCT-CURR-CYC-DEBIT `COMP` | Mixed COMP and COMP-3 |
| EXPORT-TRANSACTION-DATA | EXP-TRAN-AMT `S9(09)V99 COMP-3`, EXP-TRAN-MERCHANT-ID `9(09) COMP` | Packed/binary compression |
| EXPORT-CARD-XREF-DATA | EXP-XREF-ACCT-ID `9(11) COMP` | Binary account ID |
| EXPORT-CARD-DATA | EXP-CARD-ACCT-ID `9(11) COMP`, EXP-CARD-CVV-CD `9(03) COMP` | Binary compression |

---

## 13. Utility Copybooks

### CSLKPCDY.cpy — Lookup Code Repository (1,318 lines)

Used by: COACTUPC

Contains two large hardcoded lookup tables:

1. **US Phone Area Codes** — Array of valid 3-digit area codes used to validate customer phone numbers
2. **US State Codes** — Array of valid 2-character state abbreviations used to validate customer addresses

### CSSETATY.cpy — Screen Field Attribute Setting Template

Used by: COACTUPC (via COPY REPLACING, 34 times)

A parameterized code template that sets BMS map field attributes (color to red, value to '*') when validation flags indicate an error. Uses COPY REPLACING with `(TESTVAR1)`, `(SCRNVAR2)`, `(MAPNAME3)` placeholders.

### CSSTRPFY.cpy — Store PFKey Paragraph

Used by: COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC

Maps EIBAID (CICS attention identifier) to a human-readable PFKey value in the CCARD-AID field via EVALUATE TRUE. Maps ENTER, CLEAR, PA1, PA2, PF1–PF24 to CCARD-AID-xxx condition names.

### CSUTLDPY.cpy — Date Validation Procedures (375 lines)

Used by: COACTUPC

Contains reusable PROCEDURE DIVISION paragraphs for date validation:
- `EDIT-DATE-CCYYMMDD` — Master date validation entry point
- `EDIT-YEAR-CCYY` — Validates century (19 or 20 only) and year
- `EDIT-MONTH` — Validates month (1–12)
- `EDIT-DAY` — Validates day with month-specific rules (28/29/30/31)
- `EDIT-DATE-OF-BIRTH` — Validates DOB is in the past

Validation messages are built using STRING with field name context.

---

*Generated from `uc-legacy-modernization-cobol-to-java` repository, commit tip of `main`.*
