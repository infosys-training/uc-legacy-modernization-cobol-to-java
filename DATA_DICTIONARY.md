# DATA DICTIONARY

> Extracted from all copybooks in `app/cpy/` (30 files). Fields are grouped by business entity.

---

## Account Entity

### CVACT01Y.cpy — ACCOUNT-RECORD (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| ACCT-ID | PIC 9(11) | Numeric | Unique account identifier | Primary key for ACCTDATA VSAM KSDS |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account active/inactive flag | |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed numeric (2 dec) | Current account balance | |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed numeric (2 dec) | Credit limit for the account | |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed numeric (2 dec) | Cash advance credit limit | |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Date the account was opened | Format: YYYY-MM-DD |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | Note: typo "EXPIRAION" in source |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Date account was reissued | |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed numeric (2 dec) | Current cycle credit total | |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed numeric (2 dec) | Current cycle debit total | |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/account group identifier | Links to DISCGRP records |
| FILLER | PIC X(178) | Alphanumeric | Reserved space | Pads record to 300 bytes |

---

## Card Entity

### CVACT02Y.cpy — CARD-RECORD (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Credit card number | Primary key for CARDDATA VSAM KSDS |
| CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | FK to ACCOUNT-RECORD |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on card | |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card active/inactive flag | |
| FILLER | PIC X(59) | Alphanumeric | Reserved space | Pads record to 150 bytes |

### CVACT03Y.cpy — CARD-XREF-RECORD (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number (cross-reference key) | Primary key for CARDXREF VSAM KSDS |
| XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID linked to card | FK to CUSTOMER-RECORD |
| XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID linked to card | FK to ACCOUNT-RECORD |
| FILLER | PIC X(14) | Alphanumeric | Reserved space | Pads record to 50 bytes |

### CVCRD01Y.cpy — CC-WORK-AREAS (Online Working Storage)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| CCARD-AID | PIC X(5) | Alphanumeric | Attention identifier (key pressed) | 88-levels: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to execute | Used in XCTL navigation |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | Next BMS mapset name | |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | Next BMS map name | |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message for screen display | |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return/info message for screen | 88 CCARD-RETURN-MSG-OFF = LOW-VALUES |
| CC-ACCT-ID | PIC X(11) | Alphanumeric | Working account ID | REDEFINES as CC-ACCT-ID-N PIC 9(11) |
| CC-CARD-NUM | PIC X(16) | Alphanumeric | Working card number | REDEFINES as CC-CARD-NUM-N PIC 9(16) |
| CC-CUST-ID | PIC X(09) | Alphanumeric | Working customer ID | REDEFINES as CC-CUST-ID-N PIC 9(9) |

---

## Customer Entity

### CVCUS01Y.cpy — CUSTOMER-RECORD (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| CUST-ID | PIC 9(09) | Numeric | Unique customer identifier | Primary key for CUSTDATA VSAM KSDS |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated against CSLKPCDY lookup |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated via CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | Sensitive PII |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID | |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | Format: YYYY-MM-DD |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account ID | For electronic funds transfer |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder indicator | |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | Alphanumeric | Reserved space | Pads record to 500 bytes |

### CUSTREC.cpy — CUSTOMER-RECORD (Alternate Layout, RECLN 500)

Duplicate of CVCUS01Y with one field name difference:
- `CUST-DOB-YYYYMMDD` instead of `CUST-DOB-YYYY-MM-DD`
- Used by CBSTM03A.CBL for statement processing

---

## Transaction Entity

### CVTRA01Y.cpy — TRAN-CAT-BAL-RECORD (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| TRAN-CAT-KEY (group) | | | Composite key | |
| — TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | FK to ACCOUNT-RECORD |
| — TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to TRAN-TYPE-RECORD |
| — TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to TRAN-CAT-RECORD |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed numeric (2 dec) | Running balance for this category | |
| FILLER | PIC X(22) | Alphanumeric | Reserved space | Pads to 50 bytes |

### CVTRA02Y.cpy — DIS-GROUP-RECORD (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| DIS-GROUP-KEY (group) | | | Composite key | |
| — DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group identifier | Links to ACCT-GROUP-ID |
| — DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | |
| — DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | |
| DIS-INT-RATE | PIC S9(04)V99 | Signed numeric (2 dec) | Interest rate for this group/category | Used by CBACT04C interest calculator |
| FILLER | PIC X(28) | Alphanumeric | Reserved space | Pads to 50 bytes |

### CVTRA03Y.cpy — TRAN-TYPE-RECORD (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code | Primary key for TRANTYPE VSAM |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Transaction type description | |
| FILLER | PIC X(08) | Alphanumeric | Reserved space | Pads to 60 bytes |

### CVTRA04Y.cpy — TRAN-CAT-RECORD (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| TRAN-CAT-KEY (group) | | | Composite key | |
| — TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to TRAN-TYPE-RECORD |
| — TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | |
| FILLER | PIC X(04) | Alphanumeric | Reserved space | Pads to 60 bytes |

### CVTRA05Y.cpy — TRAN-RECORD (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | Primary key for TRANSACT VSAM |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to TRAN-TYPE-RECORD |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to TRAN-CAT-RECORD |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source/origin | |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | |
| TRAN-AMT | PIC S9(09)V99 | Signed numeric (2 dec) | Transaction amount | |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used | FK to CARD-RECORD |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original transaction timestamp | ISO format |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | ISO format |
| FILLER | PIC X(20) | Alphanumeric | Reserved space | Pads to 350 bytes |

### CVTRA06Y.cpy — DALYTRAN-RECORD (RECLN 350)

Same layout as CVTRA05Y but with `DALYTRAN-` prefix. Used for the daily transaction input file (sequential). Fields mirror TRAN-RECORD exactly:

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| DALYTRAN-ID | PIC X(16) | Daily transaction ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Transaction type code |
| DALYTRAN-CAT-CD | PIC 9(04) | Transaction category code |
| DALYTRAN-SOURCE | PIC X(10) | Transaction source |
| DALYTRAN-DESC | PIC X(100) | Transaction description |
| DALYTRAN-AMT | PIC S9(09)V99 | Transaction amount |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Merchant ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Merchant name |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Merchant city |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Merchant ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Original timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Processing timestamp |
| FILLER | PIC X(20) | Reserved |

### CVTRA07Y.cpy — Reporting Structures

| Structure | Fields | Purpose |
|----------|--------|---------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME PIC X(38), REPT-LONG-NAME PIC X(41), REPT-DATE-HEADER PIC X(12), REPT-START-DATE PIC X(10), REPT-END-DATE PIC X(10) | Report title and date range header |
| TRANSACTION-DETAIL-REPORT | TRAN-REPORT-TRANS-ID PIC X(16), TRAN-REPORT-ACCOUNT-ID PIC X(11), TRAN-REPORT-TYPE-CD PIC X(02), TRAN-REPORT-TYPE-DESC PIC X(15), TRAN-REPORT-CAT-CD PIC 9(04), TRAN-REPORT-CAT-DESC PIC X(29), TRAN-REPORT-SOURCE PIC X(10), TRAN-REPORT-AMT PIC -ZZZ,ZZZ,ZZZ.ZZ | Detail line for each transaction |
| TRANSACTION-HEADER-1 | Column headings for the report | |
| TRANSACTION-HEADER-2 | PIC X(133) VALUE ALL '-' | Separator line |
| REPORT-PAGE-TOTALS | Page-level subtotals | |
| REPORT-ACCOUNT-TOTALS | Account-level subtotals | |
| REPORT-GRAND-TOTALS | Grand totals for entire report | |

---

## Export/Import Entity

### CVEXPORT.cpy — EXPORT-RECORD (500 bytes)

Multi-record layout using REDEFINES for branch data migration.

**Header fields (common to all record types):**

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | Record type indicator (C=Customer, A=Account, T=Transaction, X=Xref, D=Card) |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp (REDEFINES into EXPORT-DATE PIC X(10) + EXPORT-TIME PIC X(15)) |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number within export |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Source branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Source region code |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Record data area (REDEFINES below) |

**REDEFINES structures for EXPORT-RECORD-DATA:**

| Structure | Key Fields | Notes |
|----------|-----------|-------|
| EXPORT-CUSTOMER-DATA | EXP-CUST-ID PIC 9(09) COMP, EXP-CUST-FIRST/MIDDLE/LAST-NAME, EXP-CUST-ADDR-LINES OCCURS 3, EXP-CUST-SSN PIC 9(09), EXP-CUST-FICO-CREDIT-SCORE PIC 9(03) COMP-3 | Uses COMP for ID, COMP-3 for score (storage optimization) |
| EXPORT-ACCOUNT-DATA | EXP-ACCT-ID PIC 9(11), EXP-ACCT-CURR-BAL PIC S9(10)V99 COMP-3, EXP-ACCT-CASH-CREDIT-LIMIT PIC S9(10)V99 COMP-3, EXP-ACCT-CURR-CYC-DEBIT PIC S9(10)V99 COMP | Uses mixed COMP/COMP-3 for numeric fields |
| EXPORT-TRANSACTION-DATA | EXP-TRAN-ID PIC X(16), EXP-TRAN-AMT PIC S9(09)V99 COMP-3, EXP-TRAN-MERCHANT-ID PIC 9(09) COMP | Packed decimal for amounts |
| EXPORT-CARD-XREF-DATA | EXP-XREF-CARD-NUM PIC X(16), EXP-XREF-CUST-ID PIC 9(09), EXP-XREF-ACCT-ID PIC 9(11) COMP | Binary account ID |
| EXPORT-CARD-DATA | EXP-CARD-NUM PIC X(16), EXP-CARD-ACCT-ID PIC 9(11) COMP, EXP-CARD-CVV-CD PIC 9(03) COMP | Binary storage for IDs |

---

## System / Common Copybooks

### COCOM01Y.cpy — CARDDEMO-COMMAREA

Communication area passed between all online CICS programs via XCTL.

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-----------|-----------|-----------|-----------------|-------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source CICS transaction ID | e.g., CC00, CM00 |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name | |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target CICS transaction ID | |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name | Used by XCTL PROGRAM() |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Logged-in user ID | |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type | 88 CDEMO-USRTYP-ADMIN VALUE 'A', 88 CDEMO-USRTYP-USER VALUE 'U' |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context flag | 88 CDEMO-PGM-ENTER VALUE 0, 88 CDEMO-PGM-REENTER VALUE 1 |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer ID | |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | Customer first name | |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | Customer middle name | |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | Customer last name | |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account ID | |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status | |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card number | |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last BMS map displayed | |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last BMS mapset used | |

### COTTL01Y.cpy — Screen Titles

| Field Name | PIC Clause | Value | Purpose |
|-----------|-----------|-------|---------|
| CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' | Screen header line 1 |
| CCDA-TITLE02 | PIC X(40) | 'CardDemo' | Screen header line 2 |
| CCDA-THANK-YOU | PIC X(40) | 'Thank you for using CCDA application...' | Sign-off message |

### CSDAT01Y.cpy — Date/Time Working Storage

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| WS-CURDATE-YEAR | PIC 9(04) | Current year |
| WS-CURDATE-MONTH | PIC 9(02) | Current month |
| WS-CURDATE-DAY | PIC 9(02) | Current day |
| WS-CURDATE-N | PIC 9(08) | REDEFINES WS-CURDATE as numeric |
| WS-CURTIME-HOURS | PIC 9(02) | Current hours |
| WS-CURTIME-MINUTE | PIC 9(02) | Current minutes |
| WS-CURTIME-SECOND | PIC 9(02) | Current seconds |
| WS-CURTIME-MILSEC | PIC 9(02) | Current milliseconds |
| WS-CURDATE-MM-DD-YY | (group) | Formatted date MM/DD/YY |
| WS-CURTIME-HH-MM-SS | (group) | Formatted time HH:MM:SS |
| WS-TIMESTAMP | (group) | Full timestamp YYYY-MM-DD HH:MM:SS.SSSSSS |

### CSMSG01Y.cpy — Common Messages

| Field Name | PIC Clause | Value | Purpose |
|-----------|-----------|-------|---------|
| CCDA-MSG-THANK-YOU | PIC X(50) | 'Thank you for using CardDemo application...' | Session end message |
| CCDA-MSG-INVALID-KEY | PIC X(50) | 'Invalid key pressed. Please see below...' | Error message for bad key press |

### CSUSR01Y.cpy — SEC-USER-DATA (RECLN 80)

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | User login ID (primary key for USRSEC) |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | User password (stored in clear text) |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type (A=Admin, U=Regular) |
| SEC-USR-FILLER | PIC X(23) | Alphanumeric | Reserved space |

### COADM02Y.cpy — CARDDEMO-ADMIN-MENU-OPTIONS

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| CDEMO-ADMIN-OPT-COUNT | PIC 9(02) VALUE 6 | Number of admin menu options |
| CDEMO-ADMIN-OPT (OCCURS 9) | Group | Admin menu option entries |
| — CDEMO-ADMIN-OPT-NUM | PIC 9(02) | Option number |
| — CDEMO-ADMIN-OPT-NAME | PIC X(35) | Option display name |
| — CDEMO-ADMIN-OPT-PGMNAME | PIC X(08) | Target program name |

**Admin Menu Options:** 1=COUSR00C (User List), 2=COUSR01C (User Add), 3=COUSR02C (User Update), 4=COUSR03C (User Delete), 5=COTRTLIC (Tran Type List/Update Db2), 6=COTRTUPC (Tran Type Maintenance Db2)

### COMEN02Y.cpy — CARDDEMO-MAIN-MENU-OPTIONS

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| CDEMO-MENU-OPT-COUNT | PIC 9(02) VALUE 11 | Number of main menu options |
| CDEMO-MENU-OPT (OCCURS 12) | Group | Main menu option entries |
| — CDEMO-MENU-OPT-NUM | PIC 9(02) | Option number |
| — CDEMO-MENU-OPT-NAME | PIC X(35) | Option display name |
| — CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Target program name |
| — CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Required user type |

**Main Menu Options:** 1=COACTVWC (Account View), 2=COACTUPC (Account Update), 3=COCRDLIC (Credit Card List), 4=COCRDSLC (Credit Card View), 5=COCRDUPC (Credit Card Update), 6=COTRN00C (Transaction List), 7=COTRN01C (Transaction View), 8=COTRN02C (Transaction Add), 9=CORPT00C (Transaction Reports), 10=COBIL00C (Bill Payment), 11=COPAUS0C (Pending Authorization View)

### CODATECN.cpy — CODATECN-REC (Date Conversion)

| Field Name | PIC Clause | Purpose | Validation |
|-----------|-----------|---------|-----------|
| CODATECN-TYPE | PIC X | Input date format type | 88 YYYYMMDD-IN VALUE "1", 88 YYYY-MM-DD-IN VALUE "2" |
| CODATECN-INP-DATE | PIC X(20) | Input date string | REDEFINES: CODATECN-1INP (YYYY/MM/DD), CODATECN-2INP (YYYY-MM-DD) |
| CODATECN-OUTTYPE | PIC X | Output date format type | 88 YYYY-MM-DD-OP VALUE "1", 88 YYYYMMDD-OP VALUE "2" |
| CODATECN-0UT-DATE | PIC X(20) | Output date string | REDEFINES: CODATECN-1OUT (YYYY-MM-DD), CODATECN-2OUT (YYYYMMDD) |
| CODATECN-ERROR-MSG | PIC X(38) | Error message from conversion | |

### COSTM01.CPY — TRNX-RECORD (Statement Transaction Layout)

Altered transaction layout for statement reporting. Key = TRNX-CARD-NUM + TRNX-ID (composite).

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| TRNX-CARD-NUM | PIC X(16) | Card number (part of composite key) |
| TRNX-ID | PIC X(16) | Transaction ID (part of composite key) |
| TRNX-TYPE-CD | PIC X(02) | Transaction type |
| TRNX-CAT-CD | PIC 9(04) | Transaction category |
| TRNX-SOURCE | PIC X(10) | Transaction source |
| TRNX-DESC | PIC X(100) | Description |
| TRNX-AMT | PIC S9(09)V99 | Amount |
| TRNX-MERCHANT-ID | PIC 9(09) | Merchant ID |
| TRNX-MERCHANT-NAME | PIC X(50) | Merchant name |
| TRNX-MERCHANT-CITY | PIC X(50) | Merchant city |
| TRNX-MERCHANT-ZIP | PIC X(10) | Merchant ZIP |
| TRNX-ORIG-TS | PIC X(26) | Original timestamp |
| TRNX-PROC-TS | PIC X(26) | Processing timestamp |
| FILLER | PIC X(20) | Reserved |

### CSLKPCDY.cpy — Lookup Code Repository

Contains 88-level validation tables for:
1. **North American phone area codes** — `VALID-PHONE-AREA-CODE` with hundreds of valid 3-digit codes
2. **US state codes** — `VALID-US-STATE-CODE` with all 50 states + territories
3. **US state + ZIP prefix** — For geographic validation

Used by COACTUPC for input validation of customer addresses and phone numbers.

### CSMSG02Y.cpy — Additional Common Messages

Extended message definitions used by programs requiring more detailed user feedback (COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUS0C, COPAUS1C, COTRTUPC).

### CSSETATY.cpy — Set Attribute Copy

Used with `COPY REPLACING` to set BMS field attributes programmatically. Heavily used by COACTUPC (38 times) and COTRTUPC for dynamic screen field attribute manipulation (protected, unprotected, highlighted, etc.).

### CSSTRPFY.cpy — Strip Field Copy

Utility copybook for stripping trailing spaces from screen input fields. Used by online programs that need to clean up BMS map input before processing.

### CSUTLDPY.cpy — Date Utility Parameters (Display)

Working storage for the CSUTLDTC date validation utility — display format parameters.

### CSUTLDWY.cpy — Date Utility Parameters (Working)

Working storage for the CSUTLDTC date validation utility — internal working format parameters. Used by COACTUPC and COTRTUPC.

### UNUSED1Y.cpy — Unused Copybook

Placeholder/unused copybook retained in the codebase. Contains no active field definitions.
