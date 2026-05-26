# Data Dictionary — CardDemo COBOL Estate

This document catalogs every copybook in `app/cpy/` and sub-application `cpy/` directories, extracting field names, PIC clauses, data types, business meaning, and validation rules. Fields are grouped by business entity.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | ACCOUNT-RECORD | — | Group | Account master record | — |
| 05 | ACCT-ID | PIC 9(11) | Numeric (11 digits) | Unique account identifier | Primary key |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Account status flag (A=Active, I=Inactive) | Single character |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Signed, two decimal places |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Credit limit for the account | Signed, two decimal places |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Cash advance credit limit | Signed, two decimal places |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (10) | Date the account was opened | YYYY-MM-DD format |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Account expiration date | YYYY-MM-DD format |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (10) | Date card was reissued | YYYY-MM-DD format |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current billing cycle credit total | Signed, two decimal places |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current billing cycle debit total | Signed, two decimal places |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account holder's ZIP code | — |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure group identifier (links to DIS-GROUP-RECORD) | FK to DISCGRP |
| 05 | FILLER | PIC X(178) | Filler | Reserved space | — |

---

## 2. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARD-RECORD | — | Group | Credit card master record | — |
| 05 | CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number | 16-digit card number |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric (11) | Associated account ID | FK to ACCOUNT-RECORD |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric (3) | Card verification value | 3-digit CVV |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name embossed on card | — |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Card expiration date | YYYY-MM-DD format |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Card status (Y=Active, N=Inactive) | Single character |
| 05 | FILLER | PIC X(59) | Filler | Reserved space | — |

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARD-XREF-RECORD | — | Group | Links card → customer → account | — |
| 05 | XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (primary key) | FK to CARD-RECORD |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID | FK to CUSTOMER-RECORD |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | FK to ACCOUNT-RECORD |
| 05 | FILLER | PIC X(14) | Filler | Reserved space | — |

### CVCRD01Y.cpy — Credit Card Work Areas

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CC-WORK-AREAS | — | Group | Working storage for CICS card programs | — |
| 10 | CCARD-AID | PIC X(5) | Alphanumeric (5) | Terminal Attention Identifier (keyboard key pressed) | 88-level values: ENTER, CLEAR, PA1, PA2, PFK01-PFK12 |
| 10 | CCARD-NEXT-PROG | PIC X(8) | Alphanumeric (8) | Next program to XCTL to | — |
| 10 | CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric (7) | Next BMS mapset name | — |
| 10 | CCARD-NEXT-MAP | PIC X(7) | Alphanumeric (7) | Next BMS map name | — |
| 10 | CCARD-ERROR-MSG | PIC X(75) | Alphanumeric (75) | Error message for display | — |
| 10 | CCARD-RETURN-MSG | PIC X(75) | Alphanumeric (75) | Return message for display | 88 CCARD-RETURN-MSG-OFF = LOW-VALUES |
| 10 | CC-ACCT-ID | PIC X(11) | Alphanumeric (11) | Working account ID | Redefines as PIC 9(11) |
| 10 | CC-CARD-NUM | PIC X(16) | Alphanumeric (16) | Working card number | Redefines as PIC 9(16) |
| 10 | CC-CUST-ID | PIC X(09) | Alphanumeric (9) | Working customer ID | Redefines as PIC 9(9) |

---

## 3. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CUSTOMER-RECORD | — | Group | Customer master record | — |
| 05 | CUST-ID | PIC 9(09) | Numeric (9) | Unique customer identifier | Primary key |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | — |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | — |
| 05 | CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | — |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Address line 1 | — |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Address line 2 | — |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Address line 3 | — |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric (2) | US state code | Validated via CSLKPCDY 88-level VALID-US-STATE-CD |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric (3) | Country code | — |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | ZIP/postal code | Validated via CSLKPCDY state-zip combo |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone number | Area code validated via CSLKPCDY |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone number | Area code validated via CSLKPCDY |
| 05 | CUST-SSN | PIC 9(09) | Numeric (9) | Social Security Number | 9-digit numeric |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID number | — |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (10) | Date of birth | YYYY-MM-DD; validated as not in future via CSUTLDPY |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | EFT (Electronic Funds Transfer) account ID | — |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric (1) | Primary card holder indicator | Y/N |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3) | FICO credit score | Range 0-999 |
| 05 | FILLER | PIC X(168) | Filler | Reserved space | — |

### CUSTREC.cpy — Customer Record (Alternate Layout)

Identical field structure to CVCUS01Y.cpy with minor differences in field naming (CUST-DOB-YYYYMMDD vs CUST-DOB-YYYY-MM-DD). Used by CBSTM03A statement generation.

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-RECORD | — | Group | Transaction master record | — |
| 05 | TRAN-ID | PIC X(16) | Alphanumeric (16) | Unique transaction identifier | Primary key (auto-generated) |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK to TRAN-TYPE-RECORD |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | FK to TRAN-CAT-RECORD |
| 05 | TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source identifier | — |
| 05 | TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | — |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Signed, two decimal places |
| 05 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant identifier | — |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | — |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | — |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | — |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number used | FK to CARD-RECORD |
| 05 | TRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original transaction timestamp | ISO format |
| 05 | TRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | ISO format |
| 05 | FILLER | PIC X(20) | Filler | Reserved space | — |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | DALYTRAN-RECORD | — | Group | Daily incoming transaction record (same structure as TRAN-RECORD) | — |
| 05 | DALYTRAN-ID | PIC X(16) | Alphanumeric (16) | Daily transaction identifier | — |
| 05 | DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | — |
| 05 | DALYTRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | — |
| 05 | DALYTRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source | — |
| 05 | DALYTRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | — |
| 05 | DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | — |
| 05 | DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant ID | — |
| 05 | DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | — |
| 05 | DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | — |
| 05 | DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP | — |
| 05 | DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number used | — |
| 05 | DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original timestamp | — |
| 05 | DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | — |
| 05 | FILLER | PIC X(20) | Filler | Reserved space | — |

### CVTRA01Y.cpy — Transaction Category Balance Record (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | Running balance by account + transaction type + category | — |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | FK to ACCOUNT-RECORD |
| 10 | TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK to TRAN-TYPE-RECORD |
| 10 | TRANCAT-CD | PIC 9(04) | Numeric (4) | Category code | FK to TRAN-CAT-RECORD |
| 05 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance for this category | — |
| 05 | FILLER | PIC X(22) | Filler | Reserved space | — |

### CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | DIS-GROUP-RECORD | — | Group | Interest rate by disclosure group + transaction type + category | — |
| 05 | DIS-GROUP-KEY | — | Group | Composite key | — |
| 10 | DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure group ID | Links to ACCT-GROUP-ID |
| 10 | DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | — |
| 10 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code | — |
| 05 | DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest rate percentage | — |
| 05 | FILLER | PIC X(28) | Filler | Reserved space | — |

### CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type reference data | — |
| 05 | TRAN-TYPE | PIC X(02) | Alphanumeric (2) | Transaction type code (e.g., "01"=Purchase) | Primary key |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Transaction type description | — |
| 05 | FILLER | PIC X(08) | Filler | Reserved space | — |

### CVTRA04Y.cpy — Transaction Category Type Record (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category sub-type reference | — |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Parent transaction type code | FK to TRAN-TYPE-RECORD |
| 10 | TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code within type | — |
| 05 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | — |
| 05 | FILLER | PIC X(04) | Filler | Reserved space | — |

### CVTRA07Y.cpy — Transaction Report Structures

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | REPORT-NAME-HEADER | — | Group | Report header with date range | — |
| 05 | REPT-SHORT-NAME | PIC X(38) | Alphanumeric (38) | Short report name ("DALYREPT") | — |
| 05 | REPT-LONG-NAME | PIC X(41) | Alphanumeric (41) | Full report name ("Daily Transaction Report") | — |
| 05 | REPT-START-DATE | PIC X(10) | Alphanumeric (10) | Report start date | — |
| 05 | REPT-END-DATE | PIC X(10) | Alphanumeric (10) | Report end date | — |
| 01 | TRANSACTION-DETAIL-REPORT | — | Group | Single report line | — |
| 05 | TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric (16) | Transaction ID | — |
| 05 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric (11) | Account ID | — |
| 05 | TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Type code | — |
| 05 | TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric (15) | Type description | — |
| 05 | TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric (4) | Category code | — |
| 05 | TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric (29) | Category description | — |
| 05 | TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric (10) | Source | — |
| 05 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount | — |
| 01 | REPORT-PAGE-TOTALS | — | Group | Page subtotal line | — |
| 05 | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page total amount | — |
| 01 | REPORT-ACCOUNT-TOTALS | — | Group | Account subtotal line | — |
| 05 | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account total amount | — |

---

## 5. User / Security Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | SEC-USER-DATA | — | Group | User authentication and authorization record | — |
| 05 | SEC-USR-ID | PIC X(08) | Alphanumeric (8) | User login ID | Primary key |
| 05 | SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | — |
| 05 | SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | — |
| 05 | SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password | Plain text (8 chars max) |
| 05 | SEC-USR-TYPE | PIC X(01) | Alphanumeric (1) | User type | 'A'=Admin, 'U'=Regular user |
| 05 | SEC-USR-FILLER | PIC X(23) | Filler | Reserved space | — |

---

## 6. Application Control Structures

### COCOM01Y.cpy — COMMAREA (Communication Area)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARDDEMO-COMMAREA | — | Group | Inter-program communication area passed through CICS | — |
| 05 | CDEMO-GENERAL-INFO | — | Group | General session information | — |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric (4) | Source CICS transaction ID | — |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric (8) | Source program name | — |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alphanumeric (4) | Target CICS transaction ID | — |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric (8) | Target program name | — |
| 10 | CDEMO-USER-ID | PIC X(08) | Alphanumeric (8) | Current logged-in user ID | — |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alphanumeric (1) | User type flag | 88 CDEMO-USRTYP-ADMIN = 'A'; 88 CDEMO-USRTYP-USER = 'U' |
| 10 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric (1) | Program context state | 88 CDEMO-PGM-ENTER = 0; 88 CDEMO-PGM-REENTER = 1 |
| 05 | CDEMO-CUSTOMER-INFO | — | Group | Customer context from session | — |
| 10 | CDEMO-CUST-ID | PIC 9(09) | Numeric (9) | Current customer ID | — |
| 10 | CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric (25) | Customer first name | — |
| 10 | CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric (25) | Customer middle name | — |
| 10 | CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric (25) | Customer last name | — |
| 05 | CDEMO-ACCOUNT-INFO | — | Group | Account context from session | — |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric (11) | Current account ID | — |
| 10 | CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric (1) | Current account status | — |
| 05 | CDEMO-CARD-INFO | — | Group | Card context from session | — |
| 10 | CDEMO-CARD-NUM | PIC 9(16) | Numeric (16) | Current card number | — |
| 05 | CDEMO-MORE-INFO | — | Group | Screen navigation state | — |
| 10 | CDEMO-LAST-MAP | PIC X(7) | Alphanumeric (7) | Last displayed BMS map | — |
| 10 | CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric (7) | Last displayed BMS mapset | — |

### COMEN02Y.cpy — Main Menu Options

Defines the 11-option main menu for regular users: Account View, Account Update, Credit Card List, Credit Card View, Credit Card Update, Transaction List, Transaction View, Transaction Add, Transaction Reports, Bill Payment, Pending Authorization View. Each option maps to a program name.

### COADM02Y.cpy — Admin Menu Options

Defines the 6-option admin menu: User List, User Add, User Update, User Delete, Transaction Type List/Update (Db2), Transaction Type Maintenance (Db2). Maps to programs COUSR00C-03C and COTRTLIC/COTRTUPC.

### COTTL01Y.cpy — Screen Title

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CCDA-SCREEN-TITLE | — | Group | Application screen title |
| 05 | CCDA-TITLE01 | PIC X(40) | Alphanumeric (40) | "AWS Mainframe Modernization" |
| 05 | CCDA-TITLE02 | PIC X(40) | Alphanumeric (40) | "CardDemo" |
| 05 | CCDA-THANK-YOU | PIC X(40) | Alphanumeric (40) | Sign-off message |

### CSDAT01Y.cpy — Date/Time Working Storage

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | WS-DATE-TIME | — | Group | Current date and time working storage |
| 10 | WS-CURDATE-YEAR | PIC 9(04) | Numeric (4) | Current year |
| 10 | WS-CURDATE-MONTH | PIC 9(02) | Numeric (2) | Current month |
| 10 | WS-CURDATE-DAY | PIC 9(02) | Numeric (2) | Current day |
| 10 | WS-CURTIME-HOURS | PIC 9(02) | Numeric (2) | Current hours |
| 10 | WS-CURTIME-MINUTE | PIC 9(02) | Numeric (2) | Current minutes |
| 10 | WS-CURTIME-SECOND | PIC 9(02) | Numeric (2) | Current seconds |
| 05 | WS-TIMESTAMP | — | Group | ISO timestamp format (YYYY-MM-DD HH:MM:SS.MMMMMM) |

### CSMSG01Y.cpy — Screen Messages

Common message area used by all CICS programs for displaying informational, warning, and error messages on BMS screens.

### CSMSG02Y.cpy — Abend Data

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | ABEND-DATA | — | Group | Abend handling work area |
| 05 | ABEND-CODE | PIC X(4) | Alphanumeric (4) | Abend code |
| 05 | ABEND-CULPRIT | PIC X(8) | Alphanumeric (8) | Program that caused abend |
| 05 | ABEND-REASON | PIC X(50) | Alphanumeric (50) | Abend reason text |
| 05 | ABEND-MSG | PIC X(72) | Alphanumeric (72) | Formatted abend message |

---

## 7. Date Validation Structures

### CODATECN.cpy — Date Conversion Record

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CODATECN-REC | — | Group | Date format conversion between YYYYMMDD ↔ YYYY-MM-DD | — |
| 05 | CODATECN-IN-REC | — | Group | Input date | — |
| 10 | CODATECN-TYPE | PIC X | Alphanumeric (1) | Input type selector | 88 YYYYMMDD-IN = "1"; 88 YYYY-MM-DD-IN = "2" |
| 10 | CODATECN-INP-DATE | PIC X(20) | Alphanumeric (20) | Input date string | REDEFINES for both formats |
| 05 | CODATECN-OUT-REC | — | Group | Output date | — |
| 10 | CODATECN-OUTTYPE | PIC X | Alphanumeric (1) | Output type selector | 88 YYYY-MM-DD-OP = "1"; 88 YYYYMMDD-OP = "2" |
| 10 | CODATECN-0UT-DATE | PIC X(20) | Alphanumeric (20) | Output date string | — |
| 05 | CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric (38) | Error message from conversion | — |

### CSUTLDWY.cpy — Date Validation Working Storage

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 10 | WS-EDIT-DATE-CCYYMMDD | — | Group | Date being validated |
| 25 | WS-EDIT-DATE-CC | PIC X(2) | Alphanumeric (2) | Century (88 THIS-CENTURY=20, LAST-CENTURY=19) |
| 25 | WS-EDIT-DATE-YY | PIC X(2) | Alphanumeric (2) | Year within century |
| 20 | WS-EDIT-DATE-MM | PIC X(2) | Alphanumeric (2) | Month (88 WS-VALID-MONTH = 1-12; 88 WS-FEBRUARY = 2) |
| 20 | WS-EDIT-DATE-DD | PIC X(2) | Alphanumeric (2) | Day (88 WS-VALID-DAY = 1-31) |
| 10 | WS-EDIT-DATE-FLGS | — | Group | Validation result flags |

### CSUTLDPY.cpy — Date Validation Procedures

Procedure division copybook providing reusable paragraphs: EDIT-DATE-CCYYMMDD, EDIT-YEAR-CCYY, EDIT-MONTH, EDIT-DAY, EDIT-DATE-OF-BIRTH (future date check). Used by COACTUPC and COTRTUPC.

### CSSETATY.cpy — Field Error Attribute Setting

Procedure copybook template that sets BMS field color to red and displays '*' when a field fails validation. Uses REPLACING for generic field name substitution.

### CSSTRPFY.cpy — Store PFKey

Procedure copybook that maps EIBAID (terminal Attention ID byte) to CCARD-AID values (ENTER, CLEAR, PA1, PA2, PFK01-PFK12) using an EVALUATE structure. Used by all CICS online programs.

---

## 8. Lookup / Validation Data

### CSLKPCDY.cpy — Lookup Code Repository

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | WS-US-PHONE-AREA-CODE-TO-EDIT | PIC XXX | Alphanumeric (3) | Phone area code to validate | 88 VALID-PHONE-AREA-CODE: ~350 valid North American area codes |
| 01 | WS-US-STATE-CD-TO-EDIT | PIC XX | Alphanumeric (2) | US state code to validate | 88 VALID-US-STATE-CD: all 50 states + DC + territories |
| 01 | US-STATE-ZIPCODE-TO-EDIT | — | Group | State + first 2 ZIP digits | — |
| 02 | US-STATE-AND-FIRST-ZIP2 | PIC X(4) | Alphanumeric (4) | Combined state+zip prefix | 88 VALID-US-STATE-ZIP-CD2-COMBO: ~150 valid combinations |

---

## 9. Export / Migration Structures

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | EXPORT-RECORD | — | Group | Multi-type export record for branch migration |
| 05 | EXPORT-REC-TYPE | PIC X(1) | Alphanumeric (1) | Record type (C=Customer, A=Account, T=Transaction, X=Xref, D=Card) |
| 05 | EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric (26) | Export timestamp |
| 05 | EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary (4 bytes) | Sequence number |
| 05 | EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric (4) | Source branch ID |
| 05 | EXPORT-REGION-CODE | PIC X(5) | Alphanumeric (5) | Region code |
| 05 | EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric (460) | Record payload (REDEFINES for each entity type) |

Uses REDEFINES for: EXPORT-CUSTOMER-DATA, EXPORT-ACCOUNT-DATA, EXPORT-TRANSACTION-DATA, EXPORT-CARD-XREF-DATA, EXPORT-CARD-DATA. COMP and COMP-3 fields are used for storage optimization in the export format.

### COSTM01.CPY — Statement Record

Used by CBSTM03A for formatting account statement output. Contains fields for statement header, detail lines, and formatting.

---

## 10. Authorization Module Structures (Sub-Application)

### CIPAUSMY.cpy — IMS Authorization Summary Segment

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | PAUT-SUMMARY-SEGMENT | — | Group | IMS root segment for pending authorization |
| 05 | PAUT-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number |
| 05 | PAUT-SUMRY-STATUS | — | Group | Summary status and counters |

### CIPAUDTY.cpy — IMS Authorization Detail Segment

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | PAUT-DETAIL-SEGMENT | — | Group | IMS child segment for individual auth records |
| 05 | PAUT-DTL-AUTH-ID | — | Group | Authorization identifier |
| 05 | PAUT-DTL-AMT | — | Group | Authorization amount fields |
| 05 | PAUT-DTL-STATUS | — | Group | Approval/decline status |

### CCPAURQY.cpy — Authorization Request

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number for authorization |
| 05 | PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Requesting transaction ID |
| 05 | PA-RQ-REQUEST-AMT | PIC +9(10).99 | Edited numeric | Requested authorization amount |
| 05 | PA-RQ-MERCHANT-ID | PIC X(09) | Alphanumeric (9) | Merchant identifier |
| 05 | PA-RQ-TIMESTAMP | PIC X(20) | Alphanumeric (20) | Request timestamp |

### CCPAURLY.cpy — Authorization Response

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | PA-RL-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number |
| 05 | PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction ID |
| 05 | PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric (6) | Authorization ID code |
| 05 | PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric (2) | Response code (AP=Approved, DC=Declined) |
| 05 | PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric (4) | Decline reason code |
| 05 | PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### CCPAUERY.cpy — Authorization Error Log

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | ERROR-LOG-RECORD | — | Group | Error log entry |
| 05 | ERR-DATE | PIC X(06) | Alphanumeric (6) | Error date |
| 05 | ERR-TIME | PIC X(06) | Alphanumeric (6) | Error time |
| 05 | ERR-APPLICATION | PIC X(08) | Alphanumeric (8) | Application name |
| 05 | ERR-PROGRAM | PIC X(08) | Alphanumeric (8) | Program name |
| 05 | ERR-LEVEL | PIC X(01) | Alphanumeric (1) | Severity: L=Log, I=Info, W=Warning, C=Critical |
| 05 | ERR-SUBSYSTEM | PIC X(01) | Alphanumeric (1) | Subsystem: A=App, C=CICS, I=IMS, D=Db2, M=MQ, F=File |
| 05 | ERR-CODE-1 | PIC X(09) | Alphanumeric (9) | Primary error code |
| 05 | ERR-CODE-2 | PIC X(09) | Alphanumeric (9) | Secondary error code |
| 05 | ERR-MESSAGE | PIC X(50) | Alphanumeric (50) | Error message |

### IMS PCB/PSB Copybooks

- **PAUTBPCB.CPY** — IMS PCB mask for pending authorization database
- **PASFLPCB.CPY** — IMS PCB mask for authorization summary GSAM file
- **PADFLPCB.CPY** — IMS PCB mask for authorization detail GSAM file
- **IMSFUNCS.cpy** — IMS DL/I function code constants (GU, GN, GNP, ISRT, DLET, REPL, CHKP)

---

## 11. Transaction Type Db2 Structures (Sub-Application)

### CSDB2RWY.cpy — Db2 Working Storage

Common Db2 working storage variables including SQLCODE display fields, error handling areas, and DSNTIAC utility parameters.

### CSDB2RPY.cpy — Db2 Common Procedures

Procedure division copybook providing: 9998-PRIMING-QUERY (Db2 connectivity test via `SELECT 1 FROM SYSIBM.SYSDUMMY1`) and 9999-FORMAT-DB2-MESSAGE (format SQL error using DSNTIAC utility).

### DCLTRTYP.dcl — Db2 TRANSACTION_TYPE Table Declaration

| Column | COBOL Field | PIC Clause | Db2 Type | Business Meaning |
|--------|------------|------------|----------|-----------------|
| TR_TYPE | TR-TYPE | PIC X(02) | CHAR(2) | Transaction type code |
| TR_DESCRIPTION | TR-DESCRIPTION | PIC X(50) | VARCHAR(50) | Type description |

### DCLTRCAT.dcl — Db2 TRANSACTION_TYPE_CATEGORY Table Declaration

| Column | COBOL Field | PIC Clause | Db2 Type | Business Meaning |
|--------|------------|------------|----------|-----------------|
| TRC_TYPE_CODE | TRC-TYPE-CODE | PIC X(02) | CHAR(2) | Transaction type code (FK) |
| TRC_TYPE_CATEGORY | TRC-TYPE-CATEGORY | PIC X(04) | CHAR(4) | Category code |
| TRC_CAT_DATA | TRC-CAT-DATA | PIC X(50) | VARCHAR(50) | Category description |

---

## 12. Unused / Reserved

### UNUSED1Y.cpy

Empty/placeholder copybook with no field definitions. Reserved for future use.
