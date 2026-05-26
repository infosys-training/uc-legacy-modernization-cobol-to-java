# DATA DICTIONARY — CardDemo Copybook Structures

## Overview

This document catalogs all copybooks in `app/cpy/` (and sub-application `cpy/` directories), extracting field definitions, PIC clauses, data types, inferred business meaning, and validation rules. Fields are grouped by business entity.

---

## 1. ACCOUNT Entity

### CVACT01Y.cpy — Account Master Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Unique account identifier | Primary key, must be numeric |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Account status flag (Active/Inactive/Closed) | 'Y'=Active, 'N'=Inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Can be negative (overpayment) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum credit limit | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Date string (YYYY-MM-DD) | Date account was opened | Must be valid date |
| ACCT-EXPIRAION-DATE | PIC X(10) | Date string (YYYY-MM-DD) | Account expiration date | Must be ≥ ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Date string (YYYY-MM-DD) | Last card reissue date | — |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle credit (payments received) | — |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle debit (charges) | — |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account holder ZIP/postal code | Validated against state-zip table in CSLKPCDY |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure/interest rate group identifier | Links to DISCGRP file |
| FILLER | PIC X(178) | Padding | Reserved space | — |

---

## 2. CUSTOMER Entity

### CVCUS01Y.cpy — Customer Master Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric (9 digits) | Unique customer identifier | Primary key |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | Required, non-blank |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | Required, non-blank |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Street address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Street address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Street address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric (2) | US state code | Validated against state code table (CSLKPCDY) |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric (3) | Country code | — |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | ZIP/postal code | Validated: state+first-2-of-zip must match (CSLKPCDY) |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone number | Area code validated (CSLKPCDY) |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone number | Area code validated (CSLKPCDY) |
| CUST-SSN | PIC 9(09) | Numeric (9 digits) | Social Security Number | 9-digit numeric |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID (driver's license, passport) | — |
| CUST-DOB-YYYYMMDD | PIC X(10) | Date string | Date of birth | Valid date, must indicate age ≥ 18 |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | EFT/bank account for autopay | — |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric (1) | Primary cardholder indicator | 'Y'/'N' |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3 digits) | FICO credit score | Range 300-850 |
| FILLER | PIC X(168) | Padding | Reserved space | — |

### CUSTREC.cpy — Customer Record (alternative layout, same structure)

Same fields as CVCUS01Y but used in statement generation context.

---

## 3. CARD Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number | Primary key, 16 digits, Luhn-checkable |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11) | Associated account ID | FK → ACCOUNT-RECORD |
| CARD-CVV-CD | PIC 9(03) | Numeric (3) | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name embossed on card | — |
| CARD-EXPIRAION-DATE | PIC X(10) | Date string | Card expiration date | MM/YY or YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Card active status | 'Y'=Active, 'N'=Inactive |
| FILLER | PIC X(59) | Padding | Reserved space | — |

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | FK → CARD-RECORD |
| XREF-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID | FK → CUSTOMER-RECORD |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | FK → ACCOUNT-RECORD |
| FILLER | PIC X(14) | Padding | Reserved | — |

---

## 4. TRANSACTION Entity

### CVTRA05Y.cpy — Transaction Master Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Unique transaction identifier | Primary key |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK → TRAN-TYPE-RECORD |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | FK → TRAN-CAT-RECORD |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source (POS, ATM, Online, etc.) | — |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | — |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Positive=charge, Negative=credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number used | FK → CARD-RECORD |
| TRAN-ORIG-TS | PIC X(26) | Timestamp | Original transaction timestamp | ISO 8601 format |
| TRAN-PROC-TS | PIC X(26) | Timestamp | Processing timestamp | ISO 8601 format |
| FILLER | PIC X(20) | Padding | Reserved | — |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same structure as CVTRA05Y with prefix `DALYTRAN-` instead of `TRAN-`. Used for daily batch input before posting to master.

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | FK → ACCOUNT-RECORD |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK → TRAN-TYPE-RECORD |
| TRANCAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | — |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance for this category on this account | — |
| FILLER | PIC X(22) | Padding | — | — |

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Account group / disclosure group ID | FK from ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type for this rate | — |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category for this rate | — |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest rate (APR) for this group/type/category | — |
| FILLER | PIC X(28) | Padding | — | — |

### CVTRA03Y.cpy — Transaction Type Reference (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric (2) | Transaction type code (e.g., 'SA'=Sale, 'CR'=Credit) | Primary key |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Human-readable type description | — |
| FILLER | PIC X(08) | Padding | — | — |

### CVTRA04Y.cpy — Transaction Category Reference (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Parent transaction type | Composite key part 1 |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code within type | Composite key part 2 |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | — |
| FILLER | PIC X(04) | Padding | — | — |

### CVTRA07Y.cpy — Transaction Report Structures

Report formatting structures: `REPORT-NAME-HEADER`, `TRANSACTION-DETAIL-REPORT`, `TRANSACTION-HEADER-1`. Used for print layout in CBTRN03C.

---

## 5. AUTHORIZATION Entity (IMS Segments)

### CIPAUSMY.cpy — Pending Authorization Summary Segment

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID (root segment key) | — |
| PA-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID | — |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric (1) | Overall authorization status | — |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Array of status codes | Historical account statuses | — |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit at auth time | — |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash limit at auth time | — |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Credit balance at auth time | — |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Cash balance at auth time | — |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of approved authorizations | — |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of declined authorizations | — |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount | — |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount | — |

### CIPAUDTY.cpy — Pending Authorization Detail Segment

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (packed) | Segment key part 1 |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (packed) | Segment key part 2 |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original date (YYMMDD) | — |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original time (HHMMSS) | — |
| PA-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | FK → CARD |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric (4) | Authorization type code | — |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric (4) | Card expiry (YYMM) | — |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric (6) | ISO 8583 message type | — |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric (6) | Message source system | — |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric (2) | Response code ('00'=Approved) | 88-level: PA-AUTH-APPROVED |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric (4) | Reason code for decline | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Requested transaction amount | — |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount | — |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant identifier | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric (2) | Merchant state | — |

---

## 6. EXPORT / MIGRATION Entity

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric (1) | Record type: 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card | Required |
| EXPORT-TIMESTAMP | PIC X(26) | Timestamp | Export timestamp | ISO 8601 |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence counter | Auto-increment |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric (4) | Exporting branch identifier | — |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric (5) | Geographic region code | — |
| EXPORT-RECORD-DATA | PIC X(460) | Polymorphic | Record payload (REDEFINES by type) | — |

Uses REDEFINES for type-specific overlays: `EXPORT-CUSTOMER-DATA`, `EXPORT-ACCOUNT-DATA`, `EXPORT-TRANSACTION-DATA`, `EXPORT-CARD-XREF-DATA`, `EXPORT-CARD-DATA`. Storage-optimized with COMP/COMP-3 fields.

---

## 7. SECURITY Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric (8) | User login ID | Primary key, uppercase |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password | Min 4 chars (plaintext storage) |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric (1) | User type | 'A'=Admin, 'U'=Regular user |
| SEC-USR-FILLER | PIC X(23) | Padding | Reserved | — |

---

## 8. APPLICATION CONTROL Copybooks

### COCOM01Y.cpy — Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type ('A'=Admin, 'U'=User) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context (0=Enter, 1=Re-enter) |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer ID in context |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account ID in context |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Current account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card number in context |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last BMS map displayed |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last BMS mapset used |

### COADM02Y.cpy — Admin Menu Options (6 options)

Defines admin menu entries: User List, User Add, User Update, User Delete, Transaction Type List (DB2), Transaction Type Maintenance (DB2).

### COMEN02Y.cpy — Main Menu Options (11 options)

Defines user menu entries: Account View, Account Update, Credit Card List/View/Update, Transaction List/View/Add, Transaction Reports, Bill Payment, Pending Authorization View.

### COTTL01Y.cpy — Screen Title Constants

Application title: "AWS Mainframe Modernization — CardDemo"

### CSDAT01Y.cpy — Date/Time Working Storage

Date and timestamp formatting fields for display (MM/DD/YY, HH:MM:SS, ISO timestamp).

### CSMSG01Y.cpy — Common Messages

Standard UI messages (thank-you, invalid-key).

### CSMSG02Y.cpy — Abend Data Work Areas

Fields: ABEND-CODE, ABEND-CULPRIT, ABEND-REASON, ABEND-MSG.

### CVCRD01Y.cpy — CICS Control Work Areas

AID key mapping (ENTER, CLEAR, PA1/PA2, PF1-PF12), navigation control (CCARD-NEXT-PROG, CCARD-NEXT-MAP/MAPSET), error/return messages.

### CSSETATY.cpy — Set Attribute Template (Procedure Division)

Reusable COPY REPLACING template for setting BMS field colors (red) on validation errors.

### CSSTRPFY.cpy — Store PFKey Paragraph (Procedure Division)

Maps EIBAID byte to named PFKey values in COMMAREA via EVALUATE block.

### CSUTLDPY.cpy / CSUTLDWY.cpy — Date Validation (Procedure Division / Working Storage)

Reusable date validation paragraphs (EDIT-DATE-CCYYMMDD, EDIT-YEAR, EDIT-MONTH, EDIT-DAY, leap-year checks).

### CSLKPCDY.cpy — Lookup Code Repository (1318 lines)

Contains 88-level validation tables for:
- North American phone area codes (800+ valid codes)
- US state codes (50 states + territories)
- State + first-2-of-zip code cross-validation

### CODATECN.cpy — Date Conversion Parameters

Input/output structures for date format conversion (YYYYMMDD ↔ YYYY-MM-DD).

### COSTM01.CPY — Statement Report Layout

Working storage for account statement report generation.

### UNUSED1Y.cpy — Placeholder

Empty/unused copybook reserved for future use.

---

## 9. Sub-Application Copybooks

### Authorization IMS (`app/app-authorization-ims-db2-mq/cpy/`)

| Copybook | Purpose |
|---------|---------|
| CCPAURQY.cpy | MQ Authorization Request message layout |
| CCPAURLY.cpy | MQ Authorization Reply message layout |
| CCPAUERY.cpy | MQ Authorization Error message layout |
| IMSFUNCS.cpy | IMS DL/I function codes (GU, GN, GNP, ISRT, DLET, etc.) |
| PAUTBPCB.CPY | IMS PCB mask for Authorization DB |
| PASFLPCB.CPY | IMS PCB mask for secondary index (flat) |
| PADFLPCB.CPY | IMS PCB mask for detail flat segments |

### Transaction Type DB2 (`app/app-transaction-type-db2/cpy/`)

| Copybook | Purpose |
|---------|---------|
| CSDB2RWY.cpy | DB2 working storage for return codes and SQLCA interpretation |
| CSDB2RPY.cpy | DB2 read parameters for cursor management |
| COTRTLI.cpy / COTRTUP.cpy | BMS map copybooks for transaction type screens |
| DCLTRTYP.dcl | DB2 DCLGEN for TRNTYPE table |
| DCLTRCAT.dcl | DB2 DCLGEN for TRNTYCAT table |
