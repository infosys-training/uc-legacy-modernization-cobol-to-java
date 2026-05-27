# Data Dictionary — CardDemo COBOL Estate

> Field-level documentation for every copybook in `app/cpy/` and sub-application directories, grouped by business entity.

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Master Record
*(Used by: CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COACCT01, COPAUA0C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | ACCOUNT-RECORD | — | Group | Account master record (300 bytes) | — |
| 05 | ACCT-ID | PIC 9(11) | Numeric | Unique account identifier | 11-digit number |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Account status flag | 'Y' = Active, 'N' = Inactive |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance | Signed; 2 decimal places |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Maximum credit limit | Must be > 0 |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | Must be ≤ credit limit |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Account opening date | Format: YYYY-MM-DD |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | Format: YYYY-MM-DD |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Card reissue date | Format: YYYY-MM-DD |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total | Running total for billing cycle |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total | Running total for billing cycle |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | US ZIP format (5 or 9 digits) |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/interest rate group | Links to DISCGRP |
| 05 | FILLER | PIC X(178) | — | Reserved space | — |

### 1.2 CVACT03Y.cpy — Card Cross-Reference Record (XREF)
*(Used by: CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COBIL00C, COTRN02C, COPAUA0C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | CARD-XREF-RECORD | — | Group | Cross-reference linking card → customer → account (50 bytes) | — |
| 05 | XREF-CARD-NUM | PIC X(16) | Alphanumeric | Credit card number (primary key) | 16-digit card number |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID | Links to CVCUS01Y |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID | Links to CVACT01Y |
| 05 | FILLER | PIC X(14) | — | Reserved | — |

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Master Record
*(Used by: CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | CUSTOMER-RECORD | — | Group | Customer master record (500 bytes) | — |
| 05 | CUST-ID | PIC 9(09) | Numeric | Unique customer identifier | 9-digit number |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alpha | Customer first name | Non-blank |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alpha | Customer middle name | Optional |
| 05 | CUST-LAST-NAME | PIC X(25) | Alpha | Customer last name | Non-blank |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alpha | Address line 1 | Non-blank |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alpha | Address line 2 | Optional |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alpha | Address line 3 | Optional |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code | Valid 2-letter state code |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code | ISO 3-letter code |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | US ZIP format |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | — |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Optional |
| 05 | CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit SSN |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID (e.g., driver's license) | — |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | Format: YYYY-MM-DD |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | Electronic funds transfer account | For bill payments |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary card holder indicator | 'Y' = Primary |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range: 300–850 |
| 05 | FILLER | PIC X(168) | — | Reserved | — |

### 2.2 CUSTREC.cpy — Customer Record (Alternate Layout)
*(Used by: CBSTM03A)*

Same structure as CVCUS01Y but uses `CUST-DOB-YYYYMMDD` (no dashes) for the date-of-birth field. Used specifically in the statement generation program.

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Data Record
*(Used by: CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | CARD-RECORD | — | Group | Credit card master record (150 bytes) | — |
| 05 | CARD-NUM | PIC X(16) | Alphanumeric | Credit card number (primary key) | 16-digit card number; Luhn check |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | Links to CVACT01Y |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit CVV |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alpha | Name embossed on card | Non-blank |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | Format: YYYY-MM-DD |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card status | 'Y' = Active, 'N' = Inactive |
| 05 | FILLER | PIC X(59) | — | Reserved | — |

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Record (Posted)
*(Used by: CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | TRAN-RECORD | — | Group | Posted transaction record (350 bytes) | — |
| 05 | TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | System-generated |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Links to CVTRA03Y |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Links to CVTRA04Y |
| 05 | TRAN-SOURCE | PIC X(10) | Alphanumeric | Source of transaction | e.g., 'POS', 'ATM', 'ONLINE' |
| 05 | TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free-text |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Positive = debit; Negative = credit |
| 05 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | — |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | — |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used | Links to CVACT02Y |
| 05 | TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original transaction timestamp | ISO timestamp |
| 05 | TRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | ISO timestamp |
| 05 | FILLER | PIC X(20) | — | Reserved | — |

### 4.2 CVTRA06Y.cpy — Daily Transaction Record (Unposted)
*(Used by: CBTRN01C, CBTRN02C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | DALYTRAN-RECORD | — | Group | Daily unposted transaction (350 bytes) | — |
| 05 | DALYTRAN-ID | PIC X(16) | Alphanumeric | Daily transaction ID | — |
| 05 | DALYTRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Must match TRANTYPE |
| 05 | DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Must match TRANCATG |
| 05 | DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source | — |
| 05 | DALYTRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | — |
| 05 | DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | — |
| 05 | DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | — |
| 05 | DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| 05 | DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| 05 | DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | — |
| 05 | DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Must exist in CARDXREF |
| 05 | DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp | — |
| 05 | DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | Set during posting |
| 05 | FILLER | PIC X(20) | — | Reserved | — |

### 4.3 CVTRA01Y.cpy — Transaction Category Balance Record
*(Used by: CBACT04C, CBTRN02C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | Category balance per account (50 bytes) | — |
| 05 | TRANCAT-KEY | — | Group | Composite key | — |
| 10 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | Links to CVACT01Y |
| 10 | TRANCAT-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Links to CVTRA03Y |
| 10 | TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code | Links to CVTRA04Y |
| 05 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance for this category | Updated during posting |
| 05 | FILLER | PIC X(22) | — | Reserved | — |

### 4.4 CVTRA02Y.cpy — Disclosure Group / Interest Rate Record
*(Used by: CBACT04C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | DIS-INT-RATE-RECORD | — | Group | Interest rate by group and category (50 bytes) | — |
| 05 | DIS-KEY | — | Group | Composite key | — |
| 10 | DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group / disclosure group ID | Links to ACCT-GROUP-ID |
| 10 | DIS-TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | — |
| 10 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | — |
| 05 | DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Annual interest rate percentage | e.g., 1899 = 18.99% |
| 05 | FILLER | PIC X(28) | — | Reserved | — |

### 4.5 CVTRA03Y.cpy — Transaction Type Reference Record
*(Used by: CBTRN03C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type lookup (60 bytes) | — |
| 05 | TRAN-TYPE | PIC X(02) | Alpha | Transaction type code (primary key) | e.g., 'SA' = Sale, 'CR' = Credit |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Transaction type description | — |
| 05 | FILLER | PIC X(08) | — | Reserved | — |

### 4.6 CVTRA04Y.cpy — Transaction Category Reference Record
*(Used by: CBTRN03C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category lookup (60 bytes) | — |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Links to CVTRA03Y |
| 10 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | — |
| 05 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | — |
| 05 | FILLER | PIC X(04) | — | Reserved | — |

### 4.7 CVTRA07Y.cpy — Transaction Report Layout
*(Used by: CBTRN03C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | REPORT-NAME-HEADER | — | Group | Report header fields | — |
| 05 | REPT-SHORT-NAME | PIC X(38) | Alpha | Short report title | — |
| 05 | REPT-LONG-NAME | PIC X(41) | Alpha | Full report title | — |
| 05 | REPT-DATE-HEADER | PIC X(12) | Alpha | Date range label | — |
| 05 | REPT-START-DATE | PIC X(10) | Alpha | Report start date | — |
| 05 | REPT-END-DATE | PIC X(10) | Alpha | Report end date | — |
| 01 | TRANSACTION-DETAIL | — | Group | Transaction detail line | — |
| 05 | TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID | — |
| 05 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID | — |
| 05 | TRAN-REPORT-TYPE-CD | PIC X(02) | Alpha | Type code | — |
| 05 | TRAN-REPORT-TYPE-DESC | PIC X(15) | Alpha | Type description | — |
| 05 | TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code | — |
| 05 | TRAN-REPORT-CAT-DESC | PIC X(29) | Alpha | Category description | — |
| 05 | TRAN-REPORT-SOURCE | PIC X(10) | Alpha | Source | — |
| 05 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Amount (formatted) | — |
| 01 | PAGE-TOTAL-LINE | — | Group | Page subtotal | — |
| 05 | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page total | — |
| 05 | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account total | — |
| 05 | REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total | — |

### 4.8 COSTM01.CPY — Statement Transaction Layout
*(Used by: CBSTM03A)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | STATEMENT-TRNX-RECORD | — | Group | Statement transaction record | — |
| 10 | TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number | — |
| 10 | TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID | — |
| 10 | TRNX-TYPE-CD | PIC X(02) | Alpha | Type code | — |
| 10 | TRNX-CAT-CD | PIC 9(04) | Numeric | Category code | — |
| 10 | TRNX-SOURCE | PIC X(10) | Alphanumeric | Source | — |
| 10 | TRNX-DESC | PIC X(100) | Alphanumeric | Description | — |
| 10 | TRNX-AMT | PIC S9(09)V99 | Signed decimal | Amount | — |
| 10 | TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | — |
| 10 | TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| 10 | TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| 10 | TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | — |
| 10 | TRNX-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp | — |
| 10 | TRNX-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | — |

---

## 5. User / Security Entity

### 5.1 CSUSR01Y.cpy — User Security Record
*(Used by: COSGN00C, COADM01C, COMEN01C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | SEC-USER-DATA | — | Group | User security record (80 bytes) | — |
| 05 | SEC-USR-ID | PIC X(08) | Alphanumeric | User login ID (primary key) | Non-blank, unique |
| 05 | SEC-USR-FNAME | PIC X(20) | Alpha | User first name | Non-blank |
| 05 | SEC-USR-LNAME | PIC X(20) | Alpha | User last name | Non-blank |
| 05 | SEC-USR-PWD | PIC X(08) | Alphanumeric | User password | 8 chars |
| 05 | SEC-USR-TYPE | PIC X(01) | Alpha | User type | 'R' = Regular, 'A' = Admin |
| 05 | SEC-USR-FILLER | PIC X(23) | — | Reserved | — |

### 5.2 UNUSED1Y.cpy — Unused User Record Layout

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | UNUSED-ID | PIC X(08) | Alphanumeric | Unused user ID |
| 05 | UNUSED-FNAME | PIC X(20) | Alpha | Unused first name |
| 05 | UNUSED-LNAME | PIC X(20) | Alpha | Unused last name |
| 05 | UNUSED-PWD | PIC X(08) | Alphanumeric | Unused password |
| 05 | UNUSED-TYPE | PIC X(01) | Alpha | Unused user type |

---

## 6. Application / Navigation Entity

### 6.1 COCOM01Y.cpy — COMMAREA (Inter-Program Communication)
*(Used by: nearly all CICS programs)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------|-----------------|-----------------|
| 01 | CARDDEMO-COMMAREA | — | Group | CICS communication area passed between programs | — |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Originating CICS transaction ID | — |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Originating program name | — |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target CICS transaction ID | — |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name | — |
| 10 | CDEMO-USER-ID | PIC X(08) | Alphanumeric | Logged-in user ID | — |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alpha | User type | 'R' or 'A' |
| 10 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context flag | Controls re-entry behavior |
| 10 | CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer ID in context | — |
| 10 | CDEMO-CUST-FNAME | PIC X(25) | Alpha | Current customer first name | — |
| 10 | CDEMO-CUST-MNAME | PIC X(25) | Alpha | Current customer middle name | — |
| 10 | CDEMO-CUST-LNAME | PIC X(25) | Alpha | Current customer last name | — |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account ID in context | — |
| 10 | CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Current account status | — |
| 10 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card number in context | — |
| 10 | CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last BMS map displayed | — |
| 10 | CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last BMS mapset used | — |

### 6.2 CVCRD01Y.cpy — Card Demo Navigation Control Block
*(Used by: COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTUPC)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 10 | CCARD-AID | PIC X(5) | Alphanumeric | AID key pressed |
| 10 | CCARD-LAST-PROG | PIC X(8) | Alphanumeric | Last program executed |
| 10 | CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to execute |
| 10 | CCARD-RETURN-TO-PROG | PIC X(8) | Alphanumeric | Return-to program |
| 10 | CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | Next mapset |
| 10 | CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | Next map |
| 10 | CCARD-RETURN-FLAG | PIC X(1) | Alpha | Return flag |
| 10 | CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message |
| 10 | CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return message |
| 10 | CCARD-FUNCTION | PIC X(1) | Alpha | Function code |
| 10 | CC-ACCT-ID | PIC X(11) | Alphanumeric | Account ID |
| 10 | CC-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| 10 | CC-CUST-ID | PIC X(09) | Alphanumeric | Customer ID |

### 6.3 COMEN02Y.cpy — Main Menu Options Table

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | CDEMO-MENU-OPT-COUNT | PIC 9(02) | Numeric | Number of menu options |
| 15 | CDEMO-MENU-OPT-NUM | PIC 9(02) | Numeric | Option number |
| 15 | CDEMO-MENU-OPT-NAME | PIC X(35) | Alpha | Option display name |
| 15 | CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Alphanumeric | Target program name |
| 15 | CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Alpha | Required user type ('R', 'A', or both) |

### 6.4 COADM02Y.cpy — Admin Menu Options Table

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | CDEMO-ADMIN-OPT-COUNT | PIC 9(02) | Numeric | Number of admin options |
| 15 | CDEMO-ADMIN-OPT-NUM | PIC 9(02) | Numeric | Option number |
| 15 | CDEMO-ADMIN-OPT-NAME | PIC X(35) | Alpha | Option display name |
| 15 | CDEMO-ADMIN-OPT-PGMNAME | PIC X(08) | Alphanumeric | Target program name |

---

## 7. Utility / Date / Message Copybooks

### 7.1 CSDAT01Y.cpy — Date/Time Working Storage
*(Used by: most CICS programs)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 15 | WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year (4-digit) |
| 15 | WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| 15 | WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| 15 | WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours |
| 15 | WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minutes |
| 15 | WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current seconds |
| 10 | WS-TIMESTAMP-DT-YYYY | PIC 9(04) | Numeric | Timestamp year |
| 10 | WS-TIMESTAMP-TM-MS6 | PIC 9(06) | Numeric | Timestamp microseconds |

### 7.2 CODATECN.cpy — Date Conversion Record (for COBDATFT assembler)
*(Used by: CBACT01C)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 10 | CODATECN-TYPE | PIC X | Alpha | Input date type |
| 10 | CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date string |
| 10 | CODATECN-OUTTYPE | PIC X | Alpha | Output date type |
| 10 | CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Output date string |
| 05 | CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Error message from conversion |

### 7.3 COTTL01Y.cpy — Screen Title Literals

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | CCDA-TITLE01 | PIC X(40) | Alpha | Primary screen title |
| 05 | CCDA-TITLE02 | PIC X(40) | Alpha | Secondary screen title |
| 05 | CCDA-THANK-YOU | PIC X(40) | Alpha | Thank-you message |

### 7.4 CSMSG01Y.cpy — Standard Messages

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | CCDA-MSG-THANK-YOU | PIC X(50) | Alpha | Thank-you confirmation message |
| 05 | CCDA-MSG-INVALID-KEY | PIC X(50) | Alpha | Invalid key press message |

### 7.5 CSMSG02Y.cpy — Abend/Error Handling Fields

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | ABEND-CODE | PIC X(4) | Alphanumeric | CICS abend code |
| 05 | ABEND-CULTEFRIT | PIC X(4) | Alphanumeric | Abend culture/error indicator |
| 05 | ABEND-MSG | PIC X(50) | Alphanumeric | Abend error message |

### 7.6 CSSETATY.cpy — Field Attribute Setting Template
*(Used by: COACTUPC — COPY REPLACING pattern for each screen field)*

Template copybook used with `COPY REPLACING` to set BMS field attributes (color, protection) based on validation flags. Not a data record — it generates procedural code.

### 7.7 CSSTRPFY.cpy — Strip Unprintable Characters Utility
*(Used by: COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTUPC)*

Utility copybook that strips unprintable characters from BMS input fields.

### 7.8 CSUTLDPY.cpy — Date Validation Utility Procedures (376 lines)
*(Used by: COACTUPC)*

Contains date validation and conversion procedures using LE callable services (CEEDAYS, CEEDATM).

### 7.9 CSUTLDWY.cpy — Date Edit Working Storage
*(Used by: COACTUPC, COTRTUPC)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 25 | WS-EDIT-DATE-CC | PIC X(2) | Alpha | Century portion of date |
| 25 | WS-EDIT-DATE-YY | PIC X(2) | Alpha | Year portion |
| 20 | WS-EDIT-DATE-MM | PIC X(2) | Alpha | Month portion |
| 20 | WS-EDIT-DATE-DD | PIC X(2) | Alpha | Day portion |
| 10 | WS-EDIT-DATE-BINARY | PIC S9(9) | Signed binary | Lillian date (binary) |
| 20 | WS-EDIT-YEAR-FLG | PIC X(01) | Alpha | Year validity flag |
| 20 | WS-EDIT-MONTH | PIC X(01) | Alpha | Month validity flag |
| 20 | WS-EDIT-DAY | PIC X(01) | Alpha | Day validity flag |

### 7.10 CSLKPCDY.cpy — US Phone/State/ZIP Lookup Tables (1,319 lines)
*(Used by: COACTUPC)*

Large lookup table containing:
- US phone area codes for validation
- US state codes with first-two-digit ZIP ranges
- Used for address and phone number validation in account update

---

## 8. Branch Migration / Export Entity

### 8.1 CVEXPORT.cpy — Export Record Layout
*(Used by: CBEXPORT, CBIMPORT)*

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | EXPORT-REC-TYPE | PIC X(1) | Alpha | Record type: 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'R'=Card |
| 05 | EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp |
| 05 | EXPORT-SEQUENCE-NUM | PIC 9(9) | Numeric | Sequence number within export |
| 05 | EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Source branch ID |
| 05 | EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Source region code |
| 05 | EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Record payload (REDEFINES by type) |

Payload is REDEFINED for each record type with entity-specific sub-fields (EXP-CUST-*, EXP-ACCT-*, EXP-TRAN-*, EXP-XREF-*, EXP-CARD-*).

---

## 9. Authorization Sub-App Copybooks (`app/app-authorization-ims-db2-mq/cpy/`)

### 9.1 CIPAUSMY.cpy — IMS Segment: Pending Authorization Summary (Root)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID (IMS root key) |
| 05 | PA-CUST-ID | PIC 9(09) | Numeric | Customer ID |
| 05 | PA-AUTH-STATUS | PIC X(01) | Alpha | Authorization status |
| 05 | PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alpha array | Account status history (5 entries) |
| 05 | PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit |
| 05 | PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash limit |
| 05 | PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Credit balance |
| 05 | PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Cash balance |
| 05 | PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Approved authorization count |
| 05 | PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Declined authorization count |
| 05 | PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Approved total amount |
| 05 | PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Declined total amount |

### 9.2 CIPAUDTY.cpy — IMS Segment: Pending Authorization Detail (Child)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | PA-AUTHORIZATION-KEY | — | Group | Composite key (packed date+time) |
| 10 | PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (packed) |
| 10 | PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (packed) |
| 05 | PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| 05 | PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| 05 | PA-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code ('00' = approved) |
| 05 | PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Transaction amount |
| 05 | PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount |
| 05 | PA-MATCH-STATUS | PIC X(01) | Alpha | 'P'=Pending, 'D'=Declined, 'E'=Expired, 'M'=Matched |
| 05 | PA-AUTH-FRAUD | PIC X(01) | Alpha | 'F'=Fraud confirmed, 'R'=Fraud removed |
| 05 | PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric | Fraud report date |

### 9.3 CCPAURQY.cpy — Pending Authorization Request (MQ Message)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Authorization request date |
| 05 | PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Authorization request time |
| 05 | PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| 05 | PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| 05 | PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount |
| 05 | PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| 05 | PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |
| 05 | PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |

### 9.4 CCPAURLY.cpy — Pending Authorization Response (MQ Message)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| 05 | PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |
| 05 | PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID code |
| 05 | PA-RL-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code |
| 05 | PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason |
| 05 | PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### 9.5 CCPAUERY.cpy — Pending Authorization Error Log

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------|-----------------|
| 05 | ERR-DATE | PIC X(06) | Alphanumeric | Error date |
| 05 | ERR-TIME | PIC X(06) | Alphanumeric | Error time |
| 05 | ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name |
| 05 | ERR-PROGRAM | PIC X(08) | Alphanumeric | Program name |
| 05 | ERR-LEVEL | PIC X(01) | Alpha | 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical |
| 05 | ERR-SUBSYSTEM | PIC X(01) | Alpha | 'A'=App, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=File |
| 05 | ERR-MESSAGE | PIC X(50) | Alphanumeric | Error message text |

### 9.6 IMS PCB Copybooks

- **PAUTBPCB.CPY** — IMS PCB mask for Pending Auth DB (DBPAUTP0)
- **PASFLPCB.CPY** — IMS PCB for GSAM summary file
- **PADFLPCB.CPY** — IMS PCB for GSAM detail file
- **IMSFUNCS.cpy** — IMS DL/I function code constants (GU, GN, GNP, ISRT, REPL, DLET)

---

## 10. Transaction Type DB2 Sub-App Copybooks (`app/app-transaction-type-db2/cpy/`)

### 10.1 CSDB2RPY.cpy — DB2 Common Procedures
Contains SQL priming query and error handling routines for DB2 connectivity verification.

### 10.2 CSDB2RWY.cpy — DB2 Working Storage
Working storage variables for DB2 SQL operations (SQLCODE, result buffers).

---

## 11. BMS Map Copybooks (`app/cpy-bms/`)

These are auto-generated from BMS map definitions and provide symbolic field names for 3270 screen I/O. Each corresponds to a `.bms` map in `app/bms/`.

| Copybook | BMS Map | Used By | Screen Purpose |
|----------|---------|---------|---------------|
| COSGN00.CPY | COSGN00.bms | COSGN00C | Sign-on screen |
| COMEN01.CPY | COMEN01.bms | COMEN01C | Main menu |
| COADM01.CPY | COADM01.bms | COADM01C | Admin menu |
| COACTVW.CPY | COACTVW.bms | COACTVWC | Account view |
| COACTUP.CPY | COACTUP.bms | COACTUPC | Account update |
| COCRDLI.CPY | COCRDLI.bms | COCRDLIC | Card list |
| COCRDSL.CPY | COCRDSL.bms | COCRDSLC | Card detail |
| COCRDUP.CPY | COCRDUP.bms | COCRDUPC | Card update |
| COTRN00.CPY | COTRN00.bms | COTRN00C | Transaction list |
| COTRN01.CPY | COTRN01.bms | COTRN01C | Transaction view |
| COTRN02.CPY | COTRN02.bms | COTRN02C | Transaction add |
| COBIL00.CPY | COBIL00.bms | COBIL00C | Bill payment |
| CORPT00.CPY | CORPT00.bms | CORPT00C | Report request |
| COUSR00.CPY | COUSR00.bms | COUSR00C | User list |
| COUSR01.CPY | COUSR01.bms | COUSR01C | User add |
| COUSR02.CPY | COUSR02.bms | COUSR02C | User update |
| COUSR03.CPY | COUSR03.bms | COUSR03C | User delete |
