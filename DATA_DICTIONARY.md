# DATA_DICTIONARY.md — CardDemo Copybook Data Structures

## Overview

This document catalogs every copybook in the CardDemo COBOL estate, extracting field names, PIC clauses, data types, business meaning, and validation rules. Fields are grouped by business entity.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Unique account identifier | Primary key for ACCTDATA KSDS |
| `ACCT-ACTIVE-STATUS` | `PIC X(01)` | Alpha (1 char) | Account active/inactive status | 'Y'=Active, 'N'=Inactive |
| `ACCT-CURR-BAL` | `PIC S9(10)V99` | Signed decimal (12,2) | Current account balance | Signed; may be negative (credit balance) |
| `ACCT-CREDIT-LIMIT` | `PIC S9(10)V99` | Signed decimal (12,2) | Maximum credit limit | Must be positive |
| `ACCT-CASH-CREDIT-LIMIT` | `PIC S9(10)V99` | Signed decimal (12,2) | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| `ACCT-OPEN-DATE` | `PIC X(10)` | Alpha date (YYYY-MM-DD) | Date account was opened | Must be valid date |
| `ACCT-EXPIRAION-DATE` | `PIC X(10)` | Alpha date (YYYY-MM-DD) | Account expiration date | Must be > ACCT-OPEN-DATE |
| `ACCT-REISSUE-DATE` | `PIC X(10)` | Alpha date (YYYY-MM-DD) | Date of last card reissue | — |
| `ACCT-CURR-CYC-CREDIT` | `PIC S9(10)V99` | Signed decimal (12,2) | Current cycle credit total | Running total, reset per cycle |
| `ACCT-CURR-CYC-DEBIT` | `PIC S9(10)V99` | Signed decimal (12,2) | Current cycle debit total | Running total, reset per cycle |
| `ACCT-ADDR-ZIP` | `PIC X(10)` | Alpha (10 chars) | Account holder ZIP code | Validated against CSLKPCDY lookup |
| `ACCT-GROUP-ID` | `PIC X(10)` | Alpha (10 chars) | Disclosure/interest rate group ID | Foreign key to DISCGRP file |
| `FILLER` | `PIC X(178)` | Filler | Reserved space | — |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `CUST-ID` | `PIC 9(09)` | Numeric (9 digits) | Unique customer identifier | Primary key for CUSTDATA KSDS |
| `CUST-FIRST-NAME` | `PIC X(25)` | Alpha (25 chars) | Customer first name | Required; alpha chars only |
| `CUST-MIDDLE-NAME` | `PIC X(25)` | Alpha (25 chars) | Customer middle name | Optional |
| `CUST-LAST-NAME` | `PIC X(25)` | Alpha (25 chars) | Customer last name | Required; alpha chars only |
| `CUST-ADDR-LINE-1` | `PIC X(50)` | Alpha (50 chars) | Address line 1 | Required |
| `CUST-ADDR-LINE-2` | `PIC X(50)` | Alpha (50 chars) | Address line 2 | Optional |
| `CUST-ADDR-LINE-3` | `PIC X(50)` | Alpha (50 chars) | Address line 3 | Optional |
| `CUST-ADDR-STATE-CD` | `PIC X(02)` | Alpha (2 chars) | State code | 2-letter US state abbreviation |
| `CUST-ADDR-COUNTRY-CD` | `PIC X(03)` | Alpha (3 chars) | Country code | ISO 3-letter country code |
| `CUST-ADDR-ZIP` | `PIC X(10)` | Alpha (10 chars) | ZIP/postal code | Validated in CSLKPCDY |
| `CUST-PHONE-NUM-1` | `PIC X(15)` | Alpha (15 chars) | Primary phone number | — |
| `CUST-PHONE-NUM-2` | `PIC X(15)` | Alpha (15 chars) | Secondary phone number | Optional |
| `CUST-SSN` | `PIC 9(09)` | Numeric (9 digits) | Social Security Number | 9 digits, sensitive PII |
| `CUST-GOVT-ISSUED-ID` | `PIC X(20)` | Alpha (20 chars) | Government-issued ID number | — |
| `CUST-DOB-YYYY-MM-DD` | `PIC X(10)` | Alpha date | Date of birth | Must be valid past date |
| `CUST-EFT-ACCOUNT-ID` | `PIC X(10)` | Alpha (10 chars) | EFT/bank account for auto-payments | — |
| `CUST-PRI-CARD-HOLDER-IND` | `PIC X(01)` | Alpha (1 char) | Primary card holder indicator | 'Y'=Primary, 'N'=Authorized user |
| `CUST-FICO-CREDIT-SCORE` | `PIC 9(03)` | Numeric (3 digits) | FICO credit score | Range 300–850 |
| `FILLER` | `PIC X(168)` | Filler | Reserved space | — |

### CUSTREC.cpy — Customer Record (Statement variant)

Identical structure to CVCUS01Y but with field `CUST-DOB-YYYYMMDD` (no dashes) and used in statement generation context.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `CARD-NUM` | `PIC X(16)` | Alpha-numeric (16 chars) | Credit card number | Primary key for CARDDATA KSDS; Luhn-valid |
| `CARD-ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Associated account ID | Foreign key to ACCTDATA |
| `CARD-CVV-CD` | `PIC 9(03)` | Numeric (3 digits) | Card verification value | 3-digit CVV |
| `CARD-EMBOSSED-NAME` | `PIC X(50)` | Alpha (50 chars) | Name embossed on card | — |
| `CARD-EXPIRAION-DATE` | `PIC X(10)` | Alpha date | Card expiration date | Must be future date for active cards |
| `CARD-ACTIVE-STATUS` | `PIC X(01)` | Alpha (1 char) | Card active/inactive status | 'Y'=Active, 'N'=Inactive |
| `FILLER` | `PIC X(59)` | Filler | Reserved space | — |

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `XREF-CARD-NUM` | `PIC X(16)` | Alpha-numeric (16 chars) | Card number (primary key) | Primary key for CARDXREF KSDS |
| `XREF-CUST-ID` | `PIC 9(09)` | Numeric (9 digits) | Customer ID linked to card | Foreign key to CUSTDATA |
| `XREF-ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Account ID linked to card | Foreign key to ACCTDATA; also alternate key |
| `FILLER` | `PIC X(14)` | Filler | Reserved space | — |

### CVCRD01Y.cpy — Card Work Areas (UI context)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| `CCARD-AID` | `PIC X(5)` | Alpha | Attention identifier (ENTER, CLEAR, PFKxx) |
| `CCARD-NEXT-PROG` | `PIC X(8)` | Alpha | Next program to transfer to |
| `CCARD-NEXT-MAPSET` | `PIC X(7)` | Alpha | Next BMS mapset name |
| `CCARD-NEXT-MAP` | `PIC X(7)` | Alpha | Next BMS map name |
| `CCARD-ERROR-MSG` | `PIC X(75)` | Alpha | Error message for display |
| `CCARD-RETURN-MSG` | `PIC X(75)` | Alpha | Return/info message for display |
| `CC-ACCT-ID` | `PIC X(11)` / `PIC 9(11)` | Alpha/Numeric (REDEFINES) | Current account ID in context |
| `CC-CARD-NUM` | `PIC X(16)` / `PIC 9(16)` | Alpha/Numeric (REDEFINES) | Current card number in context |
| `CC-CUST-ID` | `PIC X(09)` / `PIC 9(9)` | Alpha/Numeric (REDEFINES) | Current customer ID in context |

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `TRAN-ID` | `PIC X(16)` | Alpha-numeric (16 chars) | Unique transaction identifier | Primary key for TRANSACT KSDS |
| `TRAN-TYPE-CD` | `PIC X(02)` | Alpha (2 chars) | Transaction type code | Foreign key to TRANTYPE; e.g., 'SA'=Sale |
| `TRAN-CAT-CD` | `PIC 9(04)` | Numeric (4 digits) | Transaction category code | Foreign key to TRANCATG; MCC code |
| `TRAN-SOURCE` | `PIC X(10)` | Alpha (10 chars) | Transaction source system | e.g., 'POS', 'ATM', 'ONLINE' |
| `TRAN-DESC` | `PIC X(100)` | Alpha (100 chars) | Transaction description | Free text |
| `TRAN-AMT` | `PIC S9(09)V99` | Signed decimal (11,2) | Transaction amount | Positive=debit, Negative=credit |
| `TRAN-MERCHANT-ID` | `PIC 9(09)` | Numeric (9 digits) | Merchant identifier | — |
| `TRAN-MERCHANT-NAME` | `PIC X(50)` | Alpha (50 chars) | Merchant name | — |
| `TRAN-MERCHANT-CITY` | `PIC X(50)` | Alpha (50 chars) | Merchant city | — |
| `TRAN-MERCHANT-ZIP` | `PIC X(10)` | Alpha (10 chars) | Merchant ZIP code | — |
| `TRAN-CARD-NUM` | `PIC X(16)` | Alpha-numeric (16 chars) | Card number used | Foreign key to CARDDATA |
| `TRAN-ORIG-TS` | `PIC X(26)` | Alpha timestamp | Original transaction timestamp | ISO-style timestamp |
| `TRAN-PROC-TS` | `PIC X(26)` | Alpha timestamp | Processing timestamp | Set during posting |
| `FILLER` | `PIC X(20)` | Filler | Reserved space | — |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same structure as CVTRA05Y but with `DALYTRAN-` prefix. Used for daily input transaction file (`DALYTRAN.PS`) before posting to master.

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `DALYTRAN-ID` | `PIC X(16)` | Daily transaction identifier |
| `DALYTRAN-TYPE-CD` | `PIC X(02)` | Transaction type code |
| `DALYTRAN-CAT-CD` | `PIC 9(04)` | Transaction category code |
| `DALYTRAN-SOURCE` | `PIC X(10)` | Transaction source |
| `DALYTRAN-DESC` | `PIC X(100)` | Transaction description |
| `DALYTRAN-AMT` | `PIC S9(09)V99` | Transaction amount |
| `DALYTRAN-MERCHANT-ID` | `PIC 9(09)` | Merchant ID |
| `DALYTRAN-MERCHANT-NAME` | `PIC X(50)` | Merchant name |
| `DALYTRAN-MERCHANT-CITY` | `PIC X(50)` | Merchant city |
| `DALYTRAN-MERCHANT-ZIP` | `PIC X(10)` | Merchant ZIP |
| `DALYTRAN-CARD-NUM` | `PIC X(16)` | Card number |
| `DALYTRAN-ORIG-TS` | `PIC X(26)` | Original timestamp |
| `DALYTRAN-PROC-TS` | `PIC X(26)` | Processing timestamp |

### CVTRA01Y.cpy — Transaction Category Balance Record (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `TRAN-CAT-KEY` (group) | — | — | Composite key | — |
| `  TRANCAT-ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Account ID | Foreign key to ACCTDATA |
| `  TRANCAT-TYPE-CD` | `PIC X(02)` | Alpha (2 chars) | Transaction type code | Foreign key to TRANTYPE |
| `  TRANCAT-CD` | `PIC 9(04)` | Numeric (4 digits) | Transaction category code | — |
| `TRAN-CAT-BAL` | `PIC S9(09)V99` | Signed decimal (11,2) | Running balance for acct/type/category | Updated during transaction posting |
| `FILLER` | `PIC X(22)` | Filler | Reserved | — |

### CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `DIS-GROUP-KEY` (group) | — | — | Composite key | — |
| `  DIS-ACCT-GROUP-ID` | `PIC X(10)` | Alpha (10 chars) | Account group for interest rate | Foreign key from ACCT-GROUP-ID |
| `  DIS-TRAN-TYPE-CD` | `PIC X(02)` | Alpha (2 chars) | Transaction type code | — |
| `  DIS-TRAN-CAT-CD` | `PIC 9(04)` | Numeric (4 digits) | Transaction category code | — |
| `DIS-INT-RATE` | `PIC S9(04)V99` | Signed decimal (6,2) | Interest rate percentage | Annual rate |
| `FILLER` | `PIC X(28)` | Filler | Reserved | — |

### CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `TRAN-TYPE` | `PIC X(02)` | Alpha (2 chars) | Transaction type code | Primary key for TRANTYPE KSDS |
| `TRAN-TYPE-DESC` | `PIC X(50)` | Alpha (50 chars) | Transaction type description | e.g., 'Purchase', 'Cash Advance' |
| `FILLER` | `PIC X(08)` | Filler | Reserved | — |

### CVTRA04Y.cpy — Transaction Category Record (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `TRAN-CAT-KEY` (group) | — | — | Composite key | — |
| `  TRAN-TYPE-CD` | `PIC X(02)` | Alpha (2 chars) | Transaction type code | Foreign key to TRANTYPE |
| `  TRAN-CAT-CD` | `PIC 9(04)` | Numeric (4 digits) | Transaction category code | MCC-style code |
| `TRAN-CAT-TYPE-DESC` | `PIC X(50)` | Alpha (50 chars) | Category description | e.g., 'Grocery Stores', 'Airlines' |
| `FILLER` | `PIC X(04)` | Filler | Reserved | — |

### CVTRA07Y.cpy — Transaction Report Data Structures

| Structure | Key Fields | Purpose |
|-----------|-----------|---------|
| `REPORT-NAME-HEADER` | REPT-SHORT-NAME, REPT-LONG-NAME, REPT-START-DATE, REPT-END-DATE | Report header with date range |
| `TRANSACTION-DETAIL-REPORT` | TRAN-REPORT-TRANS-ID, TRAN-REPORT-ACCOUNT-ID, TRAN-REPORT-TYPE-CD, TRAN-REPORT-AMT | Detail line for each transaction |
| `TRANSACTION-HEADER-1/2` | — | Column headers and separator line |
| `REPORT-PAGE-TOTALS` | REPT-PAGE-TOTAL `PIC +ZZZ,ZZZ,ZZZ.ZZ` | Per-page subtotal |
| `REPORT-ACCOUNT-TOTALS` | REPT-ACCOUNT-TOTAL `PIC +ZZZ,ZZZ,ZZZ.ZZ` | Per-account subtotal |
| `REPORT-GRAND-TOTALS` | REPT-GRAND-TOTAL `PIC +ZZZ,ZZZ,ZZZ.ZZ` | Grand total |

---

## 5. User Security Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| `SEC-USR-ID` | `PIC X(08)` | Alpha (8 chars) | User login ID | Primary key for USRSEC KSDS |
| `SEC-USR-FNAME` | `PIC X(20)` | Alpha (20 chars) | User first name | Required |
| `SEC-USR-LNAME` | `PIC X(20)` | Alpha (20 chars) | User last name | Required |
| `SEC-USR-PWD` | `PIC X(08)` | Alpha (8 chars) | User password | 8-char max; stored in cleartext |
| `SEC-USR-TYPE` | `PIC X(01)` | Alpha (1 char) | User type | 'A'=Admin, 'U'=Regular user |
| `SEC-USR-FILLER` | `PIC X(23)` | Filler | Reserved | — |

---

## 6. Communication & UI Support

### COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| `CDEMO-FROM-TRANID` | `PIC X(04)` | Alpha | Source CICS transaction ID |
| `CDEMO-FROM-PROGRAM` | `PIC X(08)` | Alpha | Source program name |
| `CDEMO-TO-TRANID` | `PIC X(04)` | Alpha | Target CICS transaction ID |
| `CDEMO-TO-PROGRAM` | `PIC X(08)` | Alpha | Target program name |
| `CDEMO-USER-ID` | `PIC X(08)` | Alpha | Logged-in user ID |
| `CDEMO-USER-TYPE` | `PIC X(01)` | Alpha | User type ('A'=Admin, 'U'=User) |
| `CDEMO-PGM-CONTEXT` | `PIC 9(01)` | Numeric | 0=first entry, 1=re-entry |
| `CDEMO-CUST-ID` | `PIC 9(09)` | Numeric | Customer ID in context |
| `CDEMO-CUST-FNAME/MNAME/LNAME` | `PIC X(25)` each | Alpha | Customer name in context |
| `CDEMO-ACCT-ID` | `PIC 9(11)` | Numeric | Account ID in context |
| `CDEMO-ACCT-STATUS` | `PIC X(01)` | Alpha | Account status in context |
| `CDEMO-CARD-NUM` | `PIC 9(16)` | Numeric | Card number in context |
| `CDEMO-LAST-MAP` | `PIC X(7)` | Alpha | Last displayed BMS map |
| `CDEMO-LAST-MAPSET` | `PIC X(7)` | Alpha | Last displayed BMS mapset |

### COADM02Y.cpy — Admin Menu Options

Defines admin menu with 6 options linking to programs: COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC.

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `CDEMO-ADMIN-OPT-COUNT` | `PIC 9(02) VALUE 6` | Number of admin menu options |
| `CDEMO-ADMIN-OPT-NUM` | `PIC 9(02)` | Option number (1–6) |
| `CDEMO-ADMIN-OPT-NAME` | `PIC X(35)` | Option display name |
| `CDEMO-ADMIN-OPT-PGMNAME` | `PIC X(08)` | Target program name |

### COMEN02Y.cpy — Main Menu Options

Defines regular user menu with 11 options.

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `CDEMO-MENU-OPT-COUNT` | `PIC 9(02) VALUE 11` | Number of menu options |
| `CDEMO-MENU-OPT-NUM` | `PIC 9(02)` | Option number (1–11) |
| `CDEMO-MENU-OPT-NAME` | `PIC X(35)` | Option display name |
| `CDEMO-MENU-OPT-PGMNAME` | `PIC X(08)` | Target program name |
| `CDEMO-MENU-OPT-USRTYPE` | `PIC X(01)` | Required user type ('U' or 'A') |

### COTTL01Y.cpy — Screen Title

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `CCDA-TITLE01` | `PIC X(40)` | Line 1: 'AWS Mainframe Modernization' |
| `CCDA-TITLE02` | `PIC X(40)` | Line 2: 'CardDemo' |
| `CCDA-THANK-YOU` | `PIC X(40)` | Thank you message |

### CSDAT01Y.cpy — Date/Time Work Areas

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `WS-CURDATE-YEAR` | `PIC 9(04)` | Current year (4 digits) |
| `WS-CURDATE-MONTH` | `PIC 9(02)` | Current month |
| `WS-CURDATE-DAY` | `PIC 9(02)` | Current day |
| `WS-CURDATE-N` | `PIC 9(08)` (REDEFINES) | Date as 8-digit numeric |
| `WS-CURTIME-HOURS/MINUTE/SECOND/MILSEC` | `PIC 9(02)` each | Time components |
| `WS-CURDATE-MM-DD-YY` | formatted group | MM/DD/YY display format |
| `WS-CURTIME-HH-MM-SS` | formatted group | HH:MM:SS display format |

### CSMSG01Y.cpy — Common Messages

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `CCDA-MSG-THANK-YOU` | `PIC X(50)` | 'Thank you for using CardDemo application...' |
| `CCDA-MSG-INVALID-KEY` | `PIC X(50)` | 'Invalid key pressed. Please see below...' |

### CSMSG02Y.cpy — Abend Data

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `ABEND-CODE` | `PIC X(4)` | Abend code (e.g., 'ASRA') |
| `ABEND-CULPRIT` | `PIC X(8)` | Program that caused abend |
| `ABEND-REASON` | `PIC X(50)` | Reason description |
| `ABEND-MSG` | `PIC X(72)` | Full abend message |

---

## 7. Date Conversion

### CODATECN.cpy — Date Conversion Work Areas

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `CODATECN-TYPE` | `PIC X` | Input format: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| `CODATECN-INP-DATE` | `PIC X(20)` | Input date string |
| `CODATECN-OUTTYPE` | `PIC X` | Output format: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| `CODATECN-0UT-DATE` | `PIC X(20)` | Output date string |
| `CODATECN-ERROR-MSG` | `PIC X(38)` | Error message if conversion fails |

---

## 8. Export/Import Structures

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| `EXPORT-REC-TYPE` | `PIC X(1)` | Alpha | Record type ('C'=Customer, 'A'=Account, etc.) |
| `EXPORT-TIMESTAMP` | `PIC X(26)` | Alpha | Export timestamp (REDEFINES to date+time) |
| `EXPORT-SEQUENCE-NUM` | `PIC 9(9) COMP` | Binary | Sequential record number |
| `EXPORT-BRANCH-ID` | `PIC X(4)` | Alpha | Source branch identifier |
| `EXPORT-REGION-CODE` | `PIC X(5)` | Alpha | Source region code |
| `EXPORT-RECORD-DATA` | `PIC X(460)` | Alpha | Record data (REDEFINES per type) |

**REDEFINES for Customer Data:**

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `EXP-CUST-ID` | `PIC 9(09) COMP` | Customer ID (packed binary) |
| `EXP-CUST-FIRST-NAME` | `PIC X(25)` | First name |
| `EXP-CUST-MIDDLE-NAME` | `PIC X(25)` | Middle name |
| `EXP-CUST-LAST-NAME` | `PIC X(25)` | Last name |
| `EXP-CUST-ADDR-LINE` | `PIC X(50)` ×3 (OCCURS) | Address lines |
| `EXP-CUST-PHONE-NUM` | `PIC X(15)` ×2 (OCCURS) | Phone numbers |

---

## 9. Validation Support

### CSSETATY.cpy — Field Attribute Setting (Procedural Copybook)

Used via COPY REPLACING to set BMS field attributes (color/protection) based on validation flags. Sets fields to red with asterisk indicator if validation fails, or green/normal if valid. Used extensively in COACTUPC (30 replacements).

### CSSTRPFY.cpy — Strip/Pad Function

Utility copybook for string manipulation — strips leading/trailing spaces and pads fields.

### CSLKPCDY.cpy — ZIP Code Lookup Table (1318 LOC)

Massive table of US state+ZIP code prefix combinations for address validation. Contains ~500+ entries mapping 2-letter state codes + first 2 digits of ZIP to validate address consistency.

### CSUTLDPY.cpy — Date Utility Parameters (375 LOC)

Work areas for the CSUTLDTC date validation subroutine, including Lilian date conversion parameters and LE callable service interfaces.

### CSUTLDWY.cpy — Date Utility Work Area

Additional work area for date utility operations used in COACTUPC.

---

## 10. Statement Generation

### COSTM01.CPY — Statement Work Areas

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `WS-M03B-AREA` | Group | Communication area for CBSTM03B subroutine |
| Statement format fields | Various | HTML and plain-text statement line formats |

---

## 11. Authorization Sub-App Copybooks (`app/app-authorization-ims-db2-mq/cpy/`)

### CIPAUSMY.cpy — IMS Pending Authorization Summary Segment

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| `PA-ACCT-ID` | `PIC S9(11) COMP-3` | Packed decimal | Account ID (root segment key) |
| `PA-CUST-ID` | `PIC 9(09)` | Numeric | Customer ID |
| `PA-AUTH-STATUS` | `PIC X(01)` | Alpha | Authorization status |
| `PA-ACCOUNT-STATUS` | `PIC X(02)` OCCURS 5 | Alpha array | Account status history (5 slots) |
| `PA-CREDIT-LIMIT` | `PIC S9(09)V99 COMP-3` | Packed decimal | Credit limit |
| `PA-CASH-LIMIT` | `PIC S9(09)V99 COMP-3` | Packed decimal | Cash advance limit |
| `PA-CREDIT-BALANCE` | `PIC S9(09)V99 COMP-3` | Packed decimal | Outstanding credit balance |
| `PA-CASH-BALANCE` | `PIC S9(09)V99 COMP-3` | Packed decimal | Outstanding cash balance |
| `PA-APPROVED-AUTH-CNT` | `PIC S9(04) COMP` | Binary | Count of approved authorizations |
| `PA-DECLINED-AUTH-CNT` | `PIC S9(04) COMP` | Binary | Count of declined authorizations |
| `PA-APPROVED-AUTH-AMT` | `PIC S9(09)V99 COMP-3` | Packed decimal | Total approved amount |
| `PA-DECLINED-AUTH-AMT` | `PIC S9(09)V99 COMP-3` | Packed decimal | Total declined amount |

### CIPAUDTY.cpy — IMS Pending Authorization Detail Segment

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| `PA-AUTH-DATE-9C` | `PIC S9(05) COMP-3` | Packed decimal | Authorization date (IMS key part 1) |
| `PA-AUTH-TIME-9C` | `PIC S9(09) COMP-3` | Packed decimal | Authorization time (IMS key part 2) |
| `PA-AUTH-ORIG-DATE` | `PIC X(06)` | Alpha | Original auth date (display) |
| `PA-AUTH-ORIG-TIME` | `PIC X(06)` | Alpha | Original auth time (display) |
| `PA-CARD-NUM` | `PIC X(16)` | Alpha | Card number |
| `PA-AUTH-TYPE` | `PIC X(04)` | Alpha | Authorization type |
| `PA-CARD-EXPIRY-DATE` | `PIC X(04)` | Alpha | Card expiry (MMYY) |
| `PA-MESSAGE-TYPE` | `PIC X(06)` | Alpha | Message type code |
| `PA-MESSAGE-SOURCE` | `PIC X(06)` | Alpha | Message source system |
| `PA-AUTH-ID-CODE` | `PIC X(06)` | Alpha | Authorization ID code |
| `PA-AUTH-RESP-CODE` | `PIC X(02)` | Alpha | Response code ('00'=Approved) |
| `PA-AUTH-RESP-REASON` | `PIC X(04)` | Alpha | Response reason code |
| `PA-PROCESSING-CODE` | `PIC 9(06)` | Numeric | Processing code |
| `PA-TRANSACTION-AMT` | `PIC S9(10)V99 COMP-3` | Packed decimal | Requested transaction amount |
| `PA-APPROVED-AMT` | `PIC S9(10)V99 COMP-3` | Packed decimal | Approved amount |
| `PA-MERCHANT-CATAGORY-CODE` | `PIC X(04)` | Alpha | Merchant category code (MCC) |
| `PA-ACQR-COUNTRY-CODE` | `PIC X(03)` | Alpha | Acquirer country code |
| `PA-POS-ENTRY-MODE` | `PIC 9(02)` | Numeric | POS entry mode (chip/swipe/manual) |
| `PA-MERCHANT-ID` | `PIC X(15)` | Alpha | Merchant ID |
| `PA-MERCHANT-NAME` | `PIC X(22)` | Alpha | Merchant name |
| `PA-MERCHANT-CITY` | `PIC X(13)` | Alpha | Merchant city |
| `PA-MERCHANT-STATE` | `PIC X(02)` | Alpha | Merchant state |
| `PA-MERCHANT-ZIP` | `PIC X(09)` | Alpha | Merchant ZIP |
| `PA-TRANSACTION-ID` | `PIC X(15)` | Alpha | Original transaction ID |
| `PA-MATCH-STATUS` | `PIC X(01)` | Alpha | 'P'=Pending, 'E'=Expired, 'M'=Matched |
| `PA-AUTH-FRAUD` | `PIC X(01)` | Alpha | 'F'=Fraud confirmed, 'R'=Fraud removed |
| `PA-FRAUD-RPT-DATE` | `PIC X(08)` | Alpha | Fraud report date |

### CCPAURQY.cpy — Authorization Request (MQ Message)

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `PA-RQ-AUTH-DATE` | `PIC X(06)` | Request date |
| `PA-RQ-AUTH-TIME` | `PIC X(06)` | Request time |
| `PA-RQ-CARD-NUM` | `PIC X(16)` | Card number |
| `PA-RQ-AUTH-TYPE` | `PIC X(04)` | Authorization type |
| `PA-RQ-CARD-EXPIRY-DATE` | `PIC X(04)` | Card expiry |
| `PA-RQ-TRANSACTION-AMT` | `PIC +9(10).99` | Transaction amount |
| `PA-RQ-MERCHANT-ID/NAME/CITY/STATE/ZIP` | Various | Merchant details |
| `PA-RQ-TRANSACTION-ID` | `PIC X(15)` | Transaction ID |

### CCPAURLY.cpy — Authorization Response (MQ Message)

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `PA-RL-CARD-NUM` | `PIC X(16)` | Card number |
| `PA-RL-TRANSACTION-ID` | `PIC X(15)` | Transaction ID |
| `PA-RL-AUTH-ID-CODE` | `PIC X(06)` | Auth approval code |
| `PA-RL-AUTH-RESP-CODE` | `PIC X(02)` | Response code |
| `PA-RL-AUTH-RESP-REASON` | `PIC X(04)` | Response reason |
| `PA-RL-APPROVED-AMT` | `PIC +9(10).99` | Approved amount |

### CCPAUERY.cpy — Authorization Error Log

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| `ERR-DATE` | `PIC X(06)` | Error date |
| `ERR-TIME` | `PIC X(06)` | Error time |
| `ERR-APPLICATION` | `PIC X(08)` | Application name |
| `ERR-PROGRAM` | `PIC X(08)` | Program name |
| `ERR-LEVEL` | `PIC X(01)` | 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical |
| `ERR-SUBSYSTEM` | `PIC X(01)` | 'A'=App, 'C'=CICS, 'I'=IMS, 'D'=Db2, 'M'=MQ, 'F'=File |
| `ERR-CODE-1/2` | `PIC X(09)` each | Error codes |
| `ERR-MESSAGE` | `PIC X(50)` | Error description |
| `ERR-EVENT-KEY` | `PIC X(20)` | Event correlation key |

---

## 12. Db2 Transaction Type Sub-App Copybooks (`app/app-transaction-type-db2/cpy/`)

### CSDB2RPY.cpy — Db2 Read Parameters

Work areas for Db2 SELECT operations on TRNTYPE and TRNTYCAT tables.

### CSDB2RWY.cpy — Db2 Read/Write Parameters

Work areas for Db2 INSERT/UPDATE/DELETE operations on TRNTYPE and TRNTYCAT tables.

---

## 13. Unused Copybook

### UNUSED1Y.cpy

Contains placeholder fields — not referenced by any active program. Candidate for removal.
