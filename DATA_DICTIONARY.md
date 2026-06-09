# CardDemo Data Dictionary

> Complete field-level catalog of all copybooks in the CardDemo COBOL estate, grouped by business entity.

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Primary key — unique account identifier | Must be numeric, non-zero |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alpha (1 char) | Account active/inactive flag | 'Y' = Active, 'N' = Inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12 digits, 2 implied decimal places) | Current account balance | Signed; negative = credit balance. Use BigDecimal in Java |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Maximum credit limit for the account | Must be ≥ 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alpha-date (YYYY-MM-DD) | Date account was opened | Validated via CSUTLDPY (CCYYMMDD format) |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alpha-date (YYYY-MM-DD) | Account expiration date | Must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alpha-date (YYYY-MM-DD) | Date account was last reissued | Must be ≥ ACCT-OPEN-DATE |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current billing cycle total credits | Running total, reset each cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current billing cycle total debits | Running total, reset each cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alpha (10 chars) | Account holder ZIP code | Validated against state+ZIP prefix table in CSLKPCDY |
| ACCT-GROUP-ID | PIC X(10) | Alpha (10 chars) | Disclosure/interest rate group identifier | Links to DISCGRP (CVTRA02Y) for interest rate lookup |
| FILLER | PIC X(178) | Filler | Reserved/padding to RECLN 300 | — |

### 1.2 CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account identifier (part of composite key) | FK to CVACT01Y.ACCT-ID |
| TRANCAT-TYPE-CD | PIC X(02) | Alpha (2 chars) | Transaction type code (part of composite key) | FK to CVTRA03Y.TRAN-TYPE |
| TRANCAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code (part of composite key) | FK to CVTRA04Y.TRAN-CAT-CD |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance for this account+type+category combination | Updated by CBTRN02C during posting |
| FILLER | PIC X(22) | Filler | Reserved | — |

### 1.3 CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alpha (10 chars) | Disclosure group identifier (part of composite key) | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alpha (2 chars) | Transaction type code | FK to CVTRA03Y |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to CVTRA04Y |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6 digits, 2 decimal) | Interest rate for this group+type+category | Annual percentage rate |
| FILLER | PIC X(28) | Filler | Reserved | — |

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| CUST-ID | PIC 9(09) | Numeric (9 digits) | Primary key — unique customer identifier | Must be numeric, non-zero |
| CUST-FIRST-NAME | PIC X(25) | Alpha (25 chars) | Customer first name | Required; non-blank |
| CUST-MIDDLE-NAME | PIC X(25) | Alpha (25 chars) | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alpha (25 chars) | Customer last name | Required; non-blank |
| CUST-ADDR-LINE-1 | PIC X(50) | Alpha (50 chars) | Address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alpha (50 chars) | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alpha (50 chars) | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alpha (2 chars) | US state code | Validated against 50 US state codes + DC/territories in CSLKPCDY (88 VALID-US-STATE-CODE) |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha (3 chars) | ISO country code | 3-letter country code |
| CUST-ADDR-ZIP | PIC X(10) | Alpha (10 chars) | ZIP/postal code | Validated against state+ZIP prefix table in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alpha (15 chars) | Primary phone number | Area code validated against NANPA list in CSLKPCDY (88 VALID-PHONE-AREA-CODE) |
| CUST-PHONE-NUM-2 | PIC X(15) | Alpha (15 chars) | Secondary phone number | Same area code validation |
| CUST-SSN | PIC 9(09) | Numeric (9 digits) | Social Security Number | 9-digit numeric; validated by COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alpha (20 chars) | Government-issued ID number | Free-form |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alpha-date (YYYY-MM-DD) | Date of birth | Validated via CSUTLDPY date paragraphs |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alpha (10 chars) | Electronic fund transfer account ID | Optional — for bill pay |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha (1 char) | Primary card holder indicator | 'Y' = Primary, 'N' = Authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3 digits) | FICO credit score | Valid range: 300–850 |
| FILLER | PIC X(168) | Filler | Reserved to RECLN 500 | — |

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| CARD-NUM | PIC X(16) | Alpha (16 chars) | Card number — primary key | 16-digit card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Foreign key to Account | FK to CVACT01Y.ACCT-ID |
| CARD-CVV-CD | PIC 9(03) | Numeric (3 digits) | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alpha (50 chars) | Name embossed on physical card | Cardholder name |
| CARD-EXPIRAION-DATE | PIC X(10) | Alpha-date (YYYY-MM-DD) | Card expiration date | Must be future date; validated via CSUTLDPY |
| CARD-ACTIVE-STATUS | PIC X(01) | Alpha (1 char) | Card active/inactive flag | 'Y' = Active, 'N' = Inactive |
| FILLER | PIC X(59) | Filler | Reserved to RECLN 150 | — |

### 3.2 CVACT03Y.cpy — Card Cross-Reference (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| XREF-CARD-NUM | PIC X(16) | Alpha (16 chars) | Card number — primary key | FK to CVACT02Y.CARD-NUM |
| XREF-CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer who owns the card | FK to CVCUS01Y.CUST-ID |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account linked to card | FK to CVACT01Y.ACCT-ID |
| FILLER | PIC X(14) | Filler | Reserved to RECLN 50 | — |

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| TRAN-ID | PIC X(16) | Alpha (16 chars) | Transaction identifier — primary key | System-generated, unique |
| TRAN-TYPE-CD | PIC X(02) | Alpha (2 chars) | Transaction type code | FK to CVTRA03Y.TRAN-TYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | FK to CVTRA04Y.TRAN-CAT-CD |
| TRAN-SOURCE | PIC X(10) | Alpha (10 chars) | Transaction source (POS, ATM, Online, etc.) | Free-form source identifier |
| TRAN-DESC | PIC X(100) | Alpha (100 chars) | Transaction description | Free-text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11 digits, 2 decimal) | Transaction amount | Signed; positive = debit, negative = credit. Use BigDecimal |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant identifier | Merchant reference number |
| TRAN-MERCHANT-NAME | PIC X(50) | Alpha (50 chars) | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alpha (50 chars) | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alpha (10 chars) | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alpha (16 chars) | Card used for transaction | FK to CVACT02Y.CARD-NUM |
| TRAN-ORIG-TS | PIC X(26) | Alpha-timestamp | Transaction origination timestamp | ISO 8601 format |
| TRAN-PROC-TS | PIC X(26) | Alpha-timestamp | Transaction processing timestamp | Set when batch-posted by CBTRN02C |
| FILLER | PIC X(20) | Filler | Reserved to RECLN 350 | — |

### 4.2 CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same layout as CVTRA05Y but prefixed `DALYTRAN-` instead of `TRAN-`. Used for the daily input feed before posting to the master transaction file.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alpha | Daily transaction ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Category code |
| DALYTRAN-SOURCE | PIC X(10) | Alpha | Source channel |
| DALYTRAN-DESC | PIC X(100) | Alpha | Description |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | Alpha | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Alpha-timestamp | Origination timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Alpha-timestamp | Processing timestamp |
| FILLER | PIC X(20) | Filler | Reserved |

### 4.3 CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| TRAN-TYPE | PIC X(02) | Alpha (2 chars) | Transaction type code — primary key | 2-char code (e.g., 'SA' = Sale, 'CR' = Credit) |
| TRAN-TYPE-DESC | PIC X(50) | Alpha (50 chars) | Human-readable description of the transaction type | — |
| FILLER | PIC X(08) | Filler | Reserved | — |

### 4.4 CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| TRAN-TYPE-CD | PIC X(02) | Alpha (2 chars) | Transaction type (part of composite key) | FK to CVTRA03Y.TRAN-TYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Category code (part of composite key) | E.g., 5411 = Grocery, 5812 = Dining |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alpha (50 chars) | Category description | — |
| FILLER | PIC X(04) | Filler | Reserved | — |

### 4.5 CVTRA07Y.cpy — Transaction Report Layout

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alpha | Report short name ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Alpha | Full name ('Daily Transaction Report') |
| REPT-DATE-HEADER | PIC X(12) | Alpha | 'Date Range: ' header text |
| REPT-START-DATE | PIC X(10) | Alpha-date | Report start date |
| REPT-END-DATE | PIC X(10) | Alpha-date | Report end date |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alpha | Transaction ID in report line |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alpha | Account ID in report line |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alpha | Type code in report line |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alpha | Type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alpha | Category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Alpha | Transaction source |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount with commas |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Report grand total |

### 4.6 COSTM01.CPY — Statement Transaction Layout

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| TRNX-CARD-NUM | PIC X(16) | Alpha | Card number (part of key) |
| TRNX-ID | PIC X(16) | Alpha | Transaction ID (part of key) |
| TRNX-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| TRNX-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRNX-SOURCE | PIC X(10) | Alpha | Source |
| TRNX-DESC | PIC X(100) | Alpha | Description |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| TRNX-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name |
| TRNX-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP |
| TRNX-ORIG-TS | PIC X(26) | Alpha-timestamp | Origination timestamp |
| TRNX-PROC-TS | PIC X(26) | Alpha-timestamp | Processing timestamp |

---

## 5. Security / User Entity

### 5.1 CSUSR01Y.cpy — Security User Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|------------------|
| SEC-USR-ID | PIC X(08) | Alpha (8 chars) | User login ID — primary key | Unique, non-blank |
| SEC-USR-FNAME | PIC X(20) | Alpha (20 chars) | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alpha (20 chars) | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alpha (8 chars) | User password (**PLAIN TEXT** — security risk) | **WARNING:** Stored in clear text. Must implement hashing (bcrypt/scrypt) in Java migration |
| SEC-USR-TYPE | PIC X(01) | Alpha (1 char) | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved | — |

### 5.2 UNUSED1Y.cpy — Unused User Record (identical layout, deprecated)

Same layout as CSUSR01Y with `UNUSED-` prefix. Appears to be a placeholder/deprecated copy.

---

## 6. Export / Migration Entity

### 6.1 CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

Header fields (common to all record types):

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alpha | Record type: 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'R'=Card |
| EXPORT-TIMESTAMP | PIC X(26) | Alpha-timestamp | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary integer | Sequence counter |
| EXPORT-BRANCH-ID | PIC X(4) | Alpha | Branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | Alpha | Region code |
| EXPORT-RECORD-DATA | PIC X(460) | Alpha | Record payload (REDEFINES per type below) |

The `EXPORT-RECORD-DATA` is redefined for each entity type (Customer, Account, Transaction, Card-Xref, Card) with COMP/COMP-3 optimizations for numeric fields.

---

## 7. Communication / Navigation Entities

### 7.1 COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alpha | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alpha | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alpha | Target CICS transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alpha | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alpha | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alpha | 'A' = Admin, 'U' = User (88-level conditions) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 0 = Initial entry, 1 = Re-entry |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer ID in context |
| CDEMO-CUST-FNAME / MNAME / LNAME | PIC X(25) each | Alpha | Customer name fields |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account ID in context |
| CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Current account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card number in context |
| CDEMO-LAST-MAP / MAPSET | PIC X(7) each | Alpha | Last BMS map and mapset displayed |

### 7.2 COADM02Y.cpy — Admin Menu Options

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CDEMO-ADMIN-OPT-COUNT | PIC 9(02) | Numeric | Number of menu options (VALUE 6) |
| CDEMO-ADMIN-OPT-NUM | PIC 9(02) | Numeric (OCCURS 9) | Option number |
| CDEMO-ADMIN-OPT-NAME | PIC X(35) | Alpha (OCCURS 9) | Option display text |
| CDEMO-ADMIN-OPT-PGMNAME | PIC X(08) | Alpha (OCCURS 9) | Target program for XCTL |

Options: 1=COUSR00C (User List), 2=COUSR01C (Add), 3=COUSR02C (Update), 4=COUSR03C (Delete), 5=COTRTLIC (Tran Type List), 6=COTRTUPC (Tran Type Update)

### 7.3 COMEN02Y.cpy — Main User Menu Options

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CDEMO-MENU-OPT-COUNT | PIC 9(02) | Numeric | Number of menu options (VALUE 11) |
| CDEMO-MENU-OPT-NUM | PIC 9(02) | Numeric (OCCURS 12) | Option number |
| CDEMO-MENU-OPT-NAME | PIC X(35) | Alpha (OCCURS 12) | Option display text |
| CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Alpha (OCCURS 12) | Target program for XCTL |
| CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Alpha (OCCURS 12) | Required user type ('U'/'A') |

Options: 1=COACTVWC, 2=COACTUPC, 3=COCRDLIC, 4=COCRDSLC, 5=COCRDUPC, 6=COTRN00C, 7=COTRN01C, 8=COTRN02C, 9=CORPT00C, 10=COBIL00C, 11=COPAUS0C

---

## 8. Utility / Infrastructure Copybooks

### 8.1 CSDAT01Y.cpy — Date/Time Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year (YYYY) |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month (MM) |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day (DD) |
| WS-CURTIME-HOURS / MINUTE / SECOND / MILSEC | PIC 9(02) each | Numeric | Current time components |
| WS-CURDATE-MM-DD-YY | — | Display | Formatted date MM/DD/YY |
| WS-CURTIME-HH-MM-SS | — | Display | Formatted time HH:MM:SS |
| WS-TIMESTAMP | — | Display | Full timestamp YYYY-MM-DD HH:MM:SS.MMMMMM |

### 8.2 CODATECN.cpy — Date Conversion Record

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CODATECN-TYPE | PIC X | Alpha | Input format: '1' = YYYYMMDD, '2' = YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Alpha | Input date string |
| CODATECN-OUTTYPE | PIC X | Alpha | Output format: '1' = YYYY-MM-DD, '2' = YYYYMMDD |
| CODATECN-0UT-DATE | PIC X(20) | Alpha | Converted output date |
| CODATECN-ERROR-MSG | PIC X(38) | Alpha | Error message if conversion fails |

### 8.3 CSUTLDWY.cpy — Date Validation Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| WS-EDIT-DATE-CC | PIC X(2) / 9(2) | Alpha/Numeric | Century (88 THIS-CENTURY=20, LAST-CENTURY=19) |
| WS-EDIT-DATE-YY | PIC X(2) / 9(2) | Alpha/Numeric | Year within century |
| WS-EDIT-DATE-MM | PIC X(2) / 9(2) | Alpha/Numeric | Month (88 WS-VALID-MONTH VALUES 1–12) |
| WS-EDIT-DATE-DD | PIC X(2) / 9(2) | Alpha/Numeric | Day (88 WS-VALID-DAY VALUES 1–31, WS-FEBRUARY=2) |
| WS-EDIT-DATE-FLGS | — | Group | Validation flags: FLG-YEAR-NOT-OK, FLG-MONTH-NOT-OK, FLG-DAY-NOT-OK, FLG-*-BLANK |
| WS-DATE-FORMAT | PIC X(08) | Alpha | Date format mask (default 'YYYYMMDD') |

### 8.4 CSLKPCDY.cpy — Validation Lookup Tables

| Lookup | Field | PIC | 88-Level Condition | Values |
|--------|-------|-----|-------------------|--------|
| Phone Area Code | WS-US-PHONE-AREA-CODE-TO-EDIT | PIC XXX | VALID-PHONE-AREA-CODE | ~350 NANPA area codes (201–999) |
| US State Code | US-STATE-CODE-TO-EDIT | PIC X(2) | VALID-US-STATE-CODE | 50 state codes + DC + territories (AL, AK, AZ…WY) |
| State+ZIP Prefix | WS-US-STATE-AND-FIRST2-ZIP-TO-EDIT | PIC X(4) | VALID-US-STATE-AND-FIRST2-ZIP | State code + first 2 digits of ZIP (e.g., AL01, AL02…) |

### 8.5 CSMSG01Y.cpy — Common Messages

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| CCDA-MSG-THANK-YOU | PIC X(50) | 'Thank you for using CardDemo application...' |
| CCDA-MSG-INVALID-KEY | PIC X(50) | 'Invalid key pressed. Please see below...' |

### 8.6 CSMSG02Y.cpy — Abend Data

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| ABEND-CODE | PIC X(4) | Abend/error code |
| ABEND-CULPRIT | PIC X(8) | Program that caused the abend |
| ABEND-REASON | PIC X(50) | Reason text |
| ABEND-MSG | PIC X(72) | Full abend message |

### 8.7 COTTL01Y.cpy — Screen Title

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | Sign-off message |

### 8.8 CVCRD01Y.cpy — Card Work Areas (CICS runtime)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| CCARD-AID | PIC X(5) | AID key identifier (88 conditions for ENTER, CLEAR, PA1, PA2, PFK01–PFK12) |
| CCARD-NEXT-PROG | PIC X(8) | Next program to transfer to |
| CCARD-NEXT-MAPSET / MAP | PIC X(7) | Next BMS mapset/map |
| CCARD-ERROR-MSG | PIC X(75) | Screen error message |
| CCARD-RETURN-MSG | PIC X(75) | Screen return message |
| CC-ACCT-ID | PIC X(11) / 9(11) | Working account ID |
| CC-CARD-NUM | PIC X(16) / 9(16) | Working card number |
| CC-CUST-ID | PIC X(09) / 9(9) | Working customer ID |

### 8.9 CSSETATY.cpy — Screen Attribute Setter (COPY REPLACING macro)

Used via `COPY CSSETATY REPLACING ==TESTVAR1== BY ==fieldname== ...` to set BMS screen field attributes (color to red if validation error, asterisk if blank). Used 3× in COACTUPC alone.

### 8.10 CSSTRPFY.cpy — PFKey Storage (COPY REPLACING)

Maps EIBAID (CICS attention identifier) to application-level PFKey flags via an EVALUATE block. Translates DFHPF1–DFHPF24 to CCARD-AID-PFK01–PFK12.

### 8.11 CSUTLDPY.cpy — Date Validation Paragraphs (Procedure Division)

Reusable paragraphs for validating dates in CCYYMMDD format:
- `EDIT-DATE-CCYYMMDD` — Main entry; calls sub-paragraphs
- `EDIT-YEAR-CCYY` — Validates century/year (numeric, range 1900–2099)
- `EDIT-MONTH` — Validates month (1–12, handles 31/30/28-29 day months)
- `EDIT-DAY` — Validates day including leap year calculation
- `EDIT-DATE-OF-BIRTH` — Validates DOB is in the past

---

## 9. Entity Relationship Summary

```
Customer (CVCUS01Y)
   │
   ├──1:N──► Card-XREF (CVACT03Y)
   │              │
   │              ├──N:1──► Account (CVACT01Y)
   │              │              │
   │              │              └──1:N──► TranCatBal (CVTRA01Y)
   │              │                            │
   │              │                            └──► DiscGroup (CVTRA02Y)
   │              │
   │              └──N:1──► Card (CVACT02Y)
   │
   └──► Transaction (CVTRA05Y) ──► TranType (CVTRA03Y)
                                 ──► TranCat (CVTRA04Y)

Security User (CSUSR01Y) — Independent entity, no FK relationships
```

**VSAM File to Copybook Mapping:**

| VSAM Dataset | Copybook | Record Length | Key |
|-------------|----------|---------------|-----|
| ACCTDATA.VSAM.KSDS | CVACT01Y | 300 | ACCT-ID (11,0) |
| CARDDATA.VSAM.KSDS | CVACT02Y | 150 | CARD-NUM (16,0) |
| CARDXREF.VSAM.KSDS | CVACT03Y | 50 | XREF-CARD-NUM (16,0) |
| CUSTDATA.VSAM.KSDS | CVCUS01Y | 500 | CUST-ID (9,0) |
| TRANSACT.VSAM.KSDS | CVTRA05Y | 350 | TRAN-ID (16,0) |
| TCATBALF.VSAM.KSDS | CVTRA01Y | 50 | TRANCAT-ACCT-ID+TYPE-CD+CD (17,0) |
| DISCGRP.VSAM.KSDS | CVTRA02Y | 50 | DIS-ACCT-GROUP-ID+TYPE-CD+CAT-CD (16,0) |
| TRANTYPE.VSAM.KSDS | CVTRA03Y | 60 | TRAN-TYPE (2,0) |
| TRANCATG.VSAM.KSDS | CVTRA04Y | 60 | TRAN-TYPE-CD+TRAN-CAT-CD (6,0) |
| USRSEC.VSAM.KSDS | CSUSR01Y | 80 | SEC-USR-ID (8,0) |
