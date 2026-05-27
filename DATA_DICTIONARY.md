# Data Dictionary — CardDemo COBOL Estate

## Overview

This document catalogs all copybooks in `app/cpy/` and sub-application copybook directories, extracting field definitions, data types, business meanings, and validation rules. Fields are grouped by business entity.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Master Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric | Unique account identifier (primary key) | Must be 11 digits |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account status flag (Y=Active) | Single character |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed Decimal | Current account balance | Signed, 2 decimal places |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Maximum credit limit | Must be positive |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Date account was opened | Date format YYYY-MM-DD |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | Date format YYYY-MM-DD |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Date card was reissued | Date format YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed Decimal | Current cycle credit total | Running total for billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed Decimal | Current cycle debit total | Running total for billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder zip code | US zip or zip+4 format |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group identifier | Links related accounts |
| FILLER | PIC X(178) | Alphanumeric | Reserved space | — |

**Record Length: 300 bytes**

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Master Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric | Unique customer identifier (primary key) | Must be 9 digits |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | Required, non-blank |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | Required, non-blank |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Primary address line | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Secondary address line | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Tertiary address line | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Must be valid 2-char state code |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | ISO 3-character code |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | Zip/postal code | US zip or zip+4 |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Includes area code |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Optional |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | Must be 9 digits |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID (passport, driver's license) | Optional |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | Format YYYY-MM-DD |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | Electronic funds transfer account | For auto-payments |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder indicator | Y/N |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range 300-850 |
| FILLER | PIC X(168) | Alphanumeric | Reserved space | — |

**Record Length: 500 bytes**

### CUSTREC.cpy — Customer Record (Statement variant)

Same structure as CVCUS01Y.cpy with field name `CUST-DOB-YYYYMMDD` (no dashes in name). Used by the statement generation programs (CBSTM03A/B).

---

## 3. Card Entity

### CVACT02Y.cpy — Card Data Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Card number (primary key) | 16-digit credit card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | Foreign key to Account |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on card | Cardholder display name |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | Format YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card status flag | Y=Active, N=Inactive |
| FILLER | PIC X(59) | Alphanumeric | Reserved space | — |

**Record Length: 150 bytes**

### CVACT03Y.cpy — Card Cross-Reference Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number (primary key) | 16-digit card number |
| XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID foreign key | Links card to customer |
| XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID foreign key | Links card to account |
| FILLER | PIC X(14) | Alphanumeric | Reserved space | — |

**Record Length: 50 bytes**

### CVCRD01Y.cpy — Card Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CCARD-AID | PIC X(5) | Alphanumeric | Attention identifier from terminal | CICS AID key value |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to transfer control to | Valid program name |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | Next BMS mapset name | Valid mapset |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | Next BMS map name | Valid map |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message for display | Free text |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return/status message | Free text |
| CC-ACCT-ID | PIC X(11) | Alphanumeric | Account ID (alphanumeric form) | 11 characters |
| CC-ACCT-ID-N | PIC 9(11) | Numeric | Account ID (numeric redefine) | Must be numeric |
| CC-CARD-NUM | PIC X(16) | Alphanumeric | Card number (alphanumeric form) | 16 characters |
| CC-CARD-NUM-N | PIC 9(16) | Numeric | Card number (numeric redefine) | Must be numeric |
| CC-CUST-ID | PIC X(09) | Alphanumeric | Customer ID (alphanumeric form) | 9 characters |
| CC-CUST-ID-N | PIC 9(9) | Numeric | Customer ID (numeric redefine) | Must be numeric |

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Master Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier (primary key) | System-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Must exist in TRANTYPE file |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Must exist in TRANCATG file |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source system | e.g., "POS", "ATM", "ONLINE" |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed Decimal | Transaction amount | Signed; negative = credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | Acquiring merchant code |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | Display name |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | Location |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant zip code | US zip format |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card used for transaction | Foreign key to Card |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original transaction timestamp | ISO timestamp format |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | ISO timestamp format |
| FILLER | PIC X(20) | Alphanumeric | Reserved space | — |

**Record Length: 350 bytes**

### CVTRA06Y.cpy — Daily Transaction Input Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | Daily transaction ID | Input from external feed |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Validated against TRANTYPE |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Validated against TRANCATG |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | Source system | Originating channel |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | Description | Free text |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed Decimal | Transaction amount | Validated; rejected if invalid |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | Must be valid merchant |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant zip | — |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Must exist in XREF file |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | Origination timestamp | — |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | Set by batch program |
| FILLER | PIC X(20) | Alphanumeric | Reserved | — |

**Record Length: 350 bytes (same layout as TRAN-RECORD)**

### CVTRA01Y.cpy — Transaction Category Balance Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | Foreign key to Account |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type within category | — |
| TRANCAT-CD | PIC 9(04) | Numeric | Category code | — |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed Decimal | Running balance for this category | Updated by CBTRN02C and CBACT04C |
| FILLER | PIC X(22) | Alphanumeric | Reserved | — |

**Record Length: 50 bytes**

### CVTRA02Y.cpy — Disclosure/Discount Group Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| DIS-GROUP-KEY | PIC X(10) | Alphanumeric | Group lookup key | Composite key |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | — |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | — |
| DIS-INT-RATE | PIC S9(04)V99 | Signed Decimal | Interest rate for this group | Percentage (e.g., 18.99) |
| FILLER | PIC X(28) | Alphanumeric | Reserved | — |

**Record Length: 50 bytes**

### CVTRA03Y.cpy — Transaction Type Reference Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code (primary key) | 2-character code |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Description of transaction type | Display text |
| FILLER | PIC X(08) | Alphanumeric | Reserved | — |

**Record Length: 60 bytes**

### CVTRA04Y.cpy — Transaction Category Reference Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-TYPE-CD | PIC X(02) | Alphanumeric | Parent transaction type | Foreign key |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (part of key) | — |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | Display text |
| FILLER | PIC X(04) | Alphanumeric | Reserved | — |

**Record Length: 60 bytes**

### CVTRA07Y.cpy — Transaction Report Layout

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Short report name | Header line |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Full report name | Header line |
| REPT-DATE-HEADER | PIC X(12) | Alphanumeric | Date range header text | — |
| REPT-START-DATE | PIC X(10) | Alphanumeric | Report start date | Format YYYY-MM-DD |
| REPT-END-DATE | PIC X(10) | Alphanumeric | Report end date | Format YYYY-MM-DD |
| TRAN-REPORT-TRAN-ID | PIC X(16) | Alphanumeric | Transaction ID in report | — |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID in report | — |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code in report | — |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description (truncated) | — |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code in report | — |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description | — |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Source in report | — |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Formatted amount | Display format |

### COSTM01.CPY — Statement Transaction Record (used by CBSTM03A/B)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number (key part 1) | — |
| TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID (key part 2) | — |
| TRNX-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | — |
| TRNX-CAT-CD | PIC 9(04) | Numeric | Transaction category code | — |
| TRNX-SOURCE | PIC X(10) | Alphanumeric | Source system | — |
| TRNX-DESC | PIC X(100) | Alphanumeric | Description | — |
| TRNX-AMT | PIC S9(09)V99 | Signed Decimal | Amount | — |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | — |
| TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant zip | — |
| TRNX-ORIG-TS | PIC X(26) | Alphanumeric | Origination timestamp | — |
| TRNX-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | — |
| FILLER | PIC X(20) | Alphanumeric | Reserved | — |

---

## 5. Export/Migration Entity

### CVEXPORT.cpy — Branch Migration Export Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | Record type indicator | C=Customer, A=Account, T=Transaction, X=Xref, R=Card |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp | ISO format |
| EXPORT-DATE | PIC X(10) | Alphanumeric | Export date portion | — |
| EXPORT-TIME | PIC X(15) | Alphanumeric | Export time portion | — |
| EXPORT-SEQUENCE-NUM | PIC 9(9) | Numeric (COMP) | Sequential record number | Auto-incremented |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Source branch identifier | Originating branch |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Region code | Geographic region |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Polymorphic record data | REDEFINES based on REC-TYPE |

**REDEFINES for Customer (type 'C'):**

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| EXP-CUST-ID | PIC 9(09) | Customer ID |
| EXP-CUST-FIRST-NAME | PIC X(25) | First name |
| EXP-CUST-MIDDLE-NAME | PIC X(25) | Middle name |
| EXP-CUST-LAST-NAME | PIC X(25) | Last name |
| EXP-CUST-ADDR-LINE (×3) | PIC X(50) | Address lines |
| EXP-CUST-ADDR-STATE-CD | PIC X(02) | State code |
| EXP-CUST-ADDR-COUNTRY-CD | PIC X(03) | Country code |
| EXP-CUST-ADDR-ZIP | PIC X(10) | Zip code |
| EXP-CUST-PHONE-NUM (×2) | PIC X(15) | Phone numbers |
| EXP-CUST-SSN | PIC 9(09) | SSN |
| EXP-CUST-DOB-YYYY-MM-DD | PIC X(10) | Date of birth |
| EXP-CUST-FICO-CREDIT-SCORE | PIC 9(03) | FICO score |

**REDEFINES for Account (type 'A'), Transaction (type 'T'), Xref (type 'X'), Card (type 'R') follow equivalent structures.**

---

## 6. User Security Entity

### CSUSR01Y.cpy — User Security Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | User ID (primary key) | Required, unique |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name | Required |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name | Required |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | User password | Required, 8 chars |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type | A=Admin, R=Regular |
| SEC-USR-FILLER | PIC X(23) | Alphanumeric | Reserved | — |

**Record Length: 80 bytes**

---

## 7. Application Control & Navigation

### COCOM01Y.cpy — Common Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Calling program name | Set before XCTL/LINK |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target CICS transaction ID | Valid trans ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name | Valid program |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Current user ID | From sign-on |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | Current user type | A or R |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context flag | State management |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer ID | Context for operations |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | Customer first name (display) | Cached for screens |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | Customer middle name | — |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | Customer last name | — |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account ID | Context for operations |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status | Cached |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card number | Context |
| CDEMO-MORE-INFO | PIC X(7) | Alphanumeric | Additional context | — |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last mapset displayed | For navigation |

### COMEN02Y.cpy — Main Menu Options

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CDEMO-MENU-OPT-COUNT | PIC 9(02) | Numeric | Number of active menu options | Currently 11 |
| CDEMO-MENU-OPT-NUM | PIC 9(02) | Numeric | Option number | 1-11 |
| CDEMO-MENU-OPT-NAME | PIC X(35) | Alphanumeric | Option display name | Menu text |
| CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Alphanumeric | Target program for option | Valid CICS program |
| CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Alphanumeric | Required user type | A=Admin only, blank=all |

### COADM02Y.cpy — Admin Menu Options

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CDEMO-ADMIN-OPT-COUNT | PIC 9(02) | Numeric | Number of admin menu options | Currently 6 |
| CDEMO-ADMIN-OPT-NUM | PIC 9(02) | Numeric | Option number | 1-6 |
| CDEMO-ADMIN-OPT-NAME | PIC X(35) | Alphanumeric | Option display name | Menu text |
| CDEMO-ADMIN-OPT-PGMNAME | PIC X(08) | Alphanumeric | Target program | Valid CICS program |

---

## 8. Date/Time & Utilities

### CSDAT01Y.cpy — Current Date/Time Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year (YYYY) | System date |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month (MM) | 01-12 |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day (DD) | 01-31 |
| WS-CURDATE-N | PIC 9(08) | Numeric | Date as YYYYMMDD number | For date arithmetic |
| WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hour | 00-23 |
| WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minute | 00-59 |
| WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current second | 00-59 |
| WS-CURTIME-MILSEC | PIC 9(02) | Numeric | Milliseconds (hundredths) | 00-99 |
| WS-TIMESTAMP | — | Composite | Full timestamp for transaction processing | YYYY-MM-DD-HH.MM.SS.mmmmmm |

### CODATECN.cpy — Date Conversion Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CODATECN-TYPE | PIC X | Alphanumeric | Input date format type | Determines parsing |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date string | Raw date to convert |
| CODATECN-OUTTYPE | PIC X | Alphanumeric | Desired output format | Determines formatting |
| CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Formatted output date | Result of conversion |
| CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Error message if conversion fails | — |

### CSUTLDWY.cpy — Date Edit/Validation Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| WS-EDIT-DATE-CCYY | PIC X(4) | Alphanumeric | Century+Year to validate | — |
| WS-EDIT-DATE-MM | PIC X(2) | Alphanumeric | Month to validate | 01-12 |
| WS-EDIT-DATE-DD | PIC X(2) | Alphanumeric | Day to validate | 01-31 (context-dependent) |
| WS-EDIT-YEAR-FLG | PIC X(01) | Alphanumeric | Year validation flag | Pass/fail |
| WS-EDIT-MONTH | PIC X(01) | Alphanumeric | Month validation flag | Pass/fail |
| WS-EDIT-DAY | PIC X(01) | Alphanumeric | Day validation flag | Pass/fail |
| WS-DATE-FORMAT | PIC X(08) | Alphanumeric | Date format pattern | e.g., 'MMDDYYYY' |

---

## 9. UI & Messaging

### COTTL01Y.cpy — Screen Titles

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|-----------------|
| CCDA-TITLE01 | PIC X(40) | Alphanumeric | Primary screen title |
| CCDA-TITLE02 | PIC X(40) | Alphanumeric | Secondary screen title |
| CCDA-THANK-YOU | PIC X(40) | Alphanumeric | Sign-off message |

### CSMSG01Y.cpy — Common Messages

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|-----------------|
| CCDA-MSG-THANK-YOU | PIC X(50) | Alphanumeric | Thank you message |
| CCDA-MSG-INVALID-KEY | PIC X(50) | Alphanumeric | Invalid key press message |

### CSMSG02Y.cpy — Abend Data

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|-----------------|
| ABEND-CODE | PIC X(4) | Alphanumeric | CICS abend code |
| ABEND-CULPRIT | PIC X(8) | Alphanumeric | Program that abended |
| ABEND-REASON | PIC X(50) | Alphanumeric | Reason text |
| ABEND-MSG | PIC X(72) | Alphanumeric | Full error message |

### CSLKPCDY.cpy — Lookup/Validation Codes

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|-----------------|
| WS-US-PHONE-AREA-CODE-TO-EDIT | PIC XXX | Alphanumeric | Phone area code for validation |
| US-STATE-CODE-TO-EDIT | PIC X(2) | Alphanumeric | State code for validation |
| US-STATE-ZIPCODE-TO-EDIT | PIC X(4)+X(3) | Alphanumeric | Zip code for validation |

---

## 10. Authorization Sub-App Copybooks (`app/app-authorization-ims-db2-mq/cpy/`)

### CCPAURQY.cpy — Authorization Request Message
Defines the MQ request message layout for card authorization including card number, amount, merchant, and authorization type.

### CCPAURLY.cpy — Authorization Reply Message
Defines the MQ reply message layout with authorization decision (approve/decline), reason code, and response data.

### CCPAUERY.cpy — Authorization Error Message
Defines error message structure for authorization processing failures.

### CIPAUSMY.cpy — Pending Auth Summary Segment (IMS)
IMS database segment for authorization summary records (parent segment in PAUTH database).

### CIPAUDTY.cpy — Pending Auth Detail Segment (IMS)
IMS database segment for authorization detail records (child segment in PAUTH database).

### IMSFUNCS.cpy — IMS DL/I Function Codes
Defines DL/I function code constants (GU, GN, GNP, ISRT, DLET, REPL).

### PAUTBPCB.CPY — PAUTH Database PCB
Program Communication Block for the PAUTH IMS database.

### PASFLPCB.CPY — PAUTH Summary Flat File PCB
PCB for GSAM flat file output of summary segments.

### PADFLPCB.CPY — PAUTH Detail Flat File PCB
PCB for GSAM flat file output of detail segments.

---

## 11. Transaction Type DB2 Sub-App Copybooks (`app/app-transaction-type-db2/cpy/`)

### CSDB2RWY.cpy — DB2 Return Code Working Storage
Working storage for DB2 SQLCA return codes and error handling.

### CSDB2RPY.cpy — DB2 Return Code Display
Display formatting for DB2 return codes and messages.

---

## Notes

- **COMP** (binary) and **COMP-3** (packed decimal) usage is found in several records for efficient numeric storage
- **REDEFINES** clauses are used extensively in CVEXPORT.cpy for polymorphic record handling
- The `FILLER` fields ensure fixed record lengths compatible with VSAM KSDS record size definitions
- Date fields use `X(10)` format (YYYY-MM-DD string) rather than packed dates for readability
- The UNUSED1Y.cpy copybook exists in `app/cpy/` but contains no active field definitions
