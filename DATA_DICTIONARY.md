# Data Dictionary — CardDemo

> Field-level definitions extracted from all copybooks, grouped by business entity.

---

## Table of Contents

1. [Account Entity](#account-entity)
2. [Card Entity](#card-entity)
3. [Card Cross-Reference Entity](#card-cross-reference-entity)
4. [Customer Entity](#customer-entity)
5. [Transaction Entity](#transaction-entity)
6. [Transaction Category Balance](#transaction-category-balance)
7. [Disclosure Group](#disclosure-group)
8. [Transaction Type](#transaction-type)
9. [Transaction Category Type](#transaction-category-type)
10. [Daily Transaction](#daily-transaction)
11. [Transaction Report](#transaction-report)
12. [Statement / Sorted Transaction](#statement--sorted-transaction)
13. [User / Security](#user--security)
14. [Common / Navigation](#common--navigation)
15. [Export (Branch Migration)](#export-branch-migration)
16. [Authorization IMS/DB2](#authorization-imsdb2)
17. [Transaction Type DB2](#transaction-type-db2)
18. [Unused](#unused)

---

## Account Entity

**Source**: `CVACT01Y.cpy` — Data-structure for account entity (RECLN 300)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | ACCOUNT-RECORD | — | Group | Account master record |
| 05 | ACCT-ID | PIC 9(11) | Numeric | Unique account identifier (11 digits) |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account active/inactive status flag |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Credit limit for the account |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Date (YYYY-MM-DD) | Date the account was opened |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Date (YYYY-MM-DD) | Account expiration date |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Date (YYYY-MM-DD) | Date card was last reissued |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account disclosure group identifier |
| 05 | FILLER | PIC X(178) | Filler | Reserved space |

## Card Entity

**Source**: `CVACT02Y.cpy` — Data-structure for card entity (RECLN 150)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CARD-RECORD | — | Group | Card master record |
| 05 | CARD-NUM | PIC X(16) | Alphanumeric | Credit card number (16 digits) |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account identifier |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value code |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on the card |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Date (YYYY-MM-DD) | Card expiration date |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card active/inactive status flag |
| 05 | FILLER | PIC X(59) | Filler | Reserved space |

## Card Cross-Reference Entity

**Source**: `CVACT03Y.cpy` — Data-structure for card xref (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CARD-XREF-RECORD | — | Group | Card-to-customer-to-account cross-reference |
| 05 | XREF-CARD-NUM | PIC X(16) | Alphanumeric | Credit card number (lookup key) |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric | Associated customer identifier |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric | Associated account identifier |
| 05 | FILLER | PIC X(14) | Filler | Reserved space |

## Customer Entity

**Source**: `CVCUS01Y.cpy` — Data-structure for customer entity (RECLN 500)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CUSTOMER-RECORD | — | Group | Customer master record |
| 05 | CUST-ID | PIC 9(09) | Numeric | Unique customer identifier |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name |
| 05 | CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | State code |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number |
| 05 | CUST-SSN | PIC 9(09) | Numeric | Social Security Number |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Date (YYYY-MM-DD) | Date of birth |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account ID |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary card holder indicator |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score |
| 05 | FILLER | PIC X(168) | Filler | Reserved space |

**Note**: `CUSTREC.cpy` contains an identical structure with minor field name differences (e.g., `CUST-DOB-YYYYMMDD` instead of `CUST-DOB-YYYY-MM-DD`). Used by statement programs.

## Transaction Entity

**Source**: `CVTRA05Y.cpy` — Data-structure for transaction record (RECLN 350)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | TRAN-RECORD | — | Group | Transaction master record |
| 05 | TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code |
| 05 | TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction origination source |
| 05 | TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount |
| 05 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used for transaction |
| 05 | TRAN-ORIG-TS | PIC X(26) | Timestamp | Transaction origination timestamp |
| 05 | TRAN-PROC-TS | PIC X(26) | Timestamp | Transaction processing timestamp |
| 05 | FILLER | PIC X(20) | Filler | Reserved space |

## Transaction Category Balance

**Source**: `CVTRA01Y.cpy` — Data-structure for transaction category balance (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | Category balance record |
| 05 | TRAN-CAT-KEY | — | Group | Composite key |
| 10 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account identifier |
| 10 | TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| 10 | TRANCAT-CD | PIC 9(04) | Numeric | Category code |
| 05 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance for this category |
| 05 | FILLER | PIC X(22) | Filler | Reserved space |

## Disclosure Group

**Source**: `CVTRA02Y.cpy` — Data-structure for disclosure group (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | DIS-GROUP-RECORD | — | Group | Disclosure group record |
| 05 | DIS-GROUP-KEY | — | Group | Composite key |
| 10 | DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group identifier |
| 10 | DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| 10 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Category code |
| 05 | DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate for this disclosure group |
| 05 | FILLER | PIC X(28) | Filler | Reserved space |

## Transaction Type

**Source**: `CVTRA03Y.cpy` — Data-structure for transaction type (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type reference record |
| 05 | TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code (primary key) |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Transaction type description |
| 05 | FILLER | PIC X(08) | Filler | Reserved space |

## Transaction Category Type

**Source**: `CVTRA04Y.cpy` — Data-structure for transaction category type (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category record |
| 05 | TRAN-CAT-KEY | — | Group | Composite key |
| 10 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| 10 | TRAN-CAT-CD | PIC 9(04) | Numeric | Category code |
| 05 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category type description |
| 05 | FILLER | PIC X(04) | Filler | Reserved space |

## Daily Transaction

**Source**: `CVTRA06Y.cpy` — Data-structure for daily transaction record (RECLN 350)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | DALYTRAN-RECORD | — | Group | Daily transaction input record |
| 05 | DALYTRAN-ID | PIC X(16) | Alphanumeric | Transaction identifier |
| 05 | DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| 05 | DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code |
| 05 | DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source |
| 05 | DALYTRAN-DESC | PIC X(100) | Alphanumeric | Transaction description |
| 05 | DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount |
| 05 | DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier |
| 05 | DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name |
| 05 | DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city |
| 05 | DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code |
| 05 | DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| 05 | DALYTRAN-ORIG-TS | PIC X(26) | Timestamp | Origination timestamp |
| 05 | DALYTRAN-PROC-TS | PIC X(26) | Timestamp | Processing timestamp |
| 05 | FILLER | PIC X(20) | Filler | Reserved space |

## Transaction Report

**Source**: `CVTRA07Y.cpy` — Reporting data structures for transaction report

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | REPORT-NAME-HEADER | — | Group | Report header information |
| 05 | REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report short name ('DALYREPT') |
| 05 | REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report full name ('Daily Transaction Report') |
| 05 | REPT-DATE-HEADER | PIC X(12) | Alphanumeric | Date range label |
| 05 | REPT-START-DATE | PIC X(10) | Date | Report start date |
| 05 | REPT-END-DATE | PIC X(10) | Date | Report end date |
| 01 | TRANSACTION-DETAIL-REPORT | — | Group | Report detail line |
| 05 | TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID |
| 05 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID |
| 05 | TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| 05 | TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description |
| 05 | TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| 05 | TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description |
| 05 | TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Transaction source |
| 05 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Transaction amount |
| 01 | REPORT-PAGE-TOTALS | — | Group | Page totals line |
| 05 | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| 01 | REPORT-ACCOUNT-TOTALS | — | Group | Account totals line |
| 05 | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |
| 01 | REPORT-GRAND-TOTALS | — | Group | Grand totals line |
| 05 | REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total |

## Statement / Sorted Transaction

**Source**: `COSTM01.CPY` — Transaction altered layout for use in statement reporting

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | TRNX-RECORD | — | Group | Statement transaction record (re-keyed by card+tran ID) |
| 05 | TRNX-KEY | — | Group | Composite key |
| 10 | TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number (primary sort key) |
| 10 | TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID (secondary sort key) |
| 05 | TRNX-REST | — | Group | Non-key fields |
| 10 | TRNX-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| 10 | TRNX-CAT-CD | PIC 9(04) | Numeric | Transaction category code |
| 10 | TRNX-SOURCE | PIC X(10) | Alphanumeric | Transaction source |
| 10 | TRNX-DESC | PIC X(100) | Alphanumeric | Transaction description |
| 10 | TRNX-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount |
| 10 | TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier |
| 10 | TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name |
| 10 | TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city |
| 10 | TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code |
| 10 | TRNX-ORIG-TS | PIC X(26) | Timestamp | Origination timestamp |
| 10 | TRNX-PROC-TS | PIC X(26) | Timestamp | Processing timestamp |
| 10 | FILLER | PIC X(20) | Filler | Reserved space |

## User / Security

**Source**: `CSUSR01Y.cpy` — Security user data

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | SEC-USER-DATA | — | Group | Security user record (RECLN 80) |
| 05 | SEC-USR-ID | PIC X(08) | Alphanumeric | User login ID |
| 05 | SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name |
| 05 | SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name |
| 05 | SEC-USR-PWD | PIC X(08) | Alphanumeric | User password (plain text) |
| 05 | SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type ('A'=Admin, 'U'=Regular) |
| 05 | SEC-USR-FILLER | PIC X(23) | Filler | Reserved space |

## Common / Navigation

### COCOM01Y.cpy — Common communication area (COMMAREA)

Used by all CICS programs for inter-program navigation and data sharing.

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CARDDEMO-COMMAREA | — | Group | CICS COMMAREA structure |
| 05 | CDEMO-GENERAL-INFO | — | Group | General session information |
| 10 | CDEMO-PGM-REENTER | PIC X(01) | Flag | Program re-entry indicator |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Originating CICS transaction ID |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Originating program name |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target CICS transaction ID |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| 10 | CDEMO-USER-ID | PIC X(08) | Alphanumeric | Current signed-on user ID |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type (Admin/Regular) |
| 10 | CDEMO-LAST-MAP | PIC X(07) | Alphanumeric | Last BMS map displayed |
| 10 | CDEMO-LAST-MAPSET | PIC X(07) | Alphanumeric | Last BMS mapset used |

### CVCRD01Y.cpy — Card-level communication work areas

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CC-WORK-AREAS | — | Group | Communication and control work areas |
| 05 | CC-WORK-AREA | — | Group | Primary work area |
| 10 | CCARD-AID | PIC X(5) | Alphanumeric | AID key pressed by user |
| 10 | CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to transfer to |
| 10 | CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | Next BMS mapset |
| 10 | CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | Next BMS map |
| 10 | CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message for display |
| 10 | CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return/status message |
| 10 | CC-ACCT-ID | PIC X(11) | Alphanumeric | Current account ID in context |
| 10 | CC-CARD-NUM | PIC X(16) | Alphanumeric | Current card number in context |
| 10 | CC-CUST-ID | PIC X(09) | Alphanumeric | Current customer ID in context |

**Validation (88-level conditions)**:

| Condition Name | Value | Meaning |
|---------------|-------|---------|
| CCARD-AID-ENTER | 'ENTER' | Enter key pressed |
| CCARD-AID-CLEAR | 'CLEAR' | Clear key pressed |
| CCARD-AID-PA1 | 'PA1  ' | PA1 key pressed |
| CCARD-AID-PA2 | 'PA2  ' | PA2 key pressed |
| CCARD-AID-PFK01 through PFK12 | 'PFK01'–'PFK12' | Function keys F1–F12 |
| CCARD-RETURN-MSG-OFF | LOW-VALUES | No return message |

### COTTL01Y.cpy — Screen title constants

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CCDA-SCREEN-TITLE | — | Group | Standard screen header |
| 05 | CCDA-TITLE01 | PIC X(40) | Alphanumeric | 'AWS Mainframe Modernization' |
| 05 | CCDA-TITLE02 | PIC X(40) | Alphanumeric | 'CardDemo' |
| 05 | CCDA-THANK-YOU | PIC X(40) | Alphanumeric | Thank-you message for sign-off |

### CSDAT01Y.cpy — Date/time work areas

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | WS-DATE-TIME | — | Group | Date/time working storage |
| 05 | WS-CURDATE-DATA | — | Group | Raw date/time from system |
| 10 | WS-CURDATE | — | Group | Current date |
| 15 | WS-CURDATE-YEAR | PIC 9(04) | Numeric | 4-digit year |
| 15 | WS-CURDATE-MONTH | PIC 9(02) | Numeric | Month |
| 15 | WS-CURDATE-DAY | PIC 9(02) | Numeric | Day |
| 10 | WS-CURTIME | — | Group | Current time |
| 15 | WS-CURTIME-HOURS | PIC 9(02) | Numeric | Hours |
| 15 | WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Minutes |
| 15 | WS-CURTIME-SECOND | PIC 9(02) | Numeric | Seconds |
| 15 | WS-CURTIME-MILSEC | PIC 9(02) | Numeric | Milliseconds |
| 05 | WS-CURDATE-MM-DD-YY | — | Group | Formatted date display |
| 05 | WS-TIMESTAMP | — | Group | Full timestamp (YYYY-MM-DD HH:MM:SS.FFFFFF) |

### CSMSG01Y.cpy — Message area

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | WS-MESSAGE-AREA | — | Group | Error and info message storage for screen display |

### CSMSG02Y.cpy — Additional message area

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | WS-MESSAGE-AREA-2 | — | Group | Extended message area for multi-line messages |

### COADM02Y.cpy — Admin menu options

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CARDDEMO-ADMIN-MENU-OPTIONS | — | Group | Admin menu definition |
| 05 | CDEMO-ADMIN-OPT-COUNT | PIC 9(02) | Numeric | Number of admin options (6) |
| 05 | CDEMO-ADMIN-OPTIONS-DATA | — | Group | Option data block |
| 10 | Option 1: 'User List (Security)' → COUSR00C | | | |
| 10 | Option 2: 'User Add (Security)' → COUSR01C | | | |
| 10 | Option 3: 'User Update (Security)' → COUSR02C | | | |
| 10 | Option 4: 'User Delete (Security)' → COUSR03C | | | |
| 10 | Option 5: 'Transaction Type List/Update (Db2)' → COTRTLIC | | | |
| 10 | Option 6: 'Transaction Type Maintenance (Db2)' → COTRTUPC | | | |

### COMEN02Y.cpy — Main menu options

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CARDDEMO-MAIN-MENU-OPTIONS | — | Group | Main menu definition |
| 05 | CDEMO-MENU-OPT-COUNT | PIC 9(02) | Numeric | Number of menu options (12) |
| 10 | Option 1: 'Account View' → COACTVWC (Admin+User) | | | |
| 10 | Option 2: 'Account Update' → COACTUPC (Admin only) | | | |
| 10 | Option 3: 'Credit Card List' → COCRDLIC (Admin+User) | | | |
| 10 | Option 4: 'Credit Card View' → COCRDSLC (Admin+User) | | | |
| 10 | Option 5: 'Credit Card Update' → COCRDUPC (Admin only) | | | |
| 10 | Option 6: 'Transaction List' → COTRN00C (Admin+User) | | | |
| 10 | Option 7: 'Transaction View' → COTRN01C (Admin+User) | | | |
| 10 | Option 8: 'Transaction Add' → COTRN02C (Admin only) | | | |
| 10 | Option 9: 'Transaction Report' → CORPT00C (Admin+User) | | | |
| 10 | Option 10: 'Bill Payment' → COBIL00C (User only) | | | |
| 10 | Option 11: 'Pending Authorization View' → COPAUS0C (User only) | | | |

### CODATECN.cpy — Date conversion constants

Constants for date formatting used by batch programs calling COBDATFT.

### CSLKPCDY.cpy — Lookup code repository

Contains 88-level validation tables for:
- **North America phone area codes** (all valid NANPA area codes)
- **US state codes** (two-letter state abbreviations)
- **US state + first 2 digits of ZIP code** (geographic validation)

### CSSTRPFY.cpy — Store PFKey procedure (paragraph code)

Maps EIBAID values to CCARD-AID condition names in the COMMAREA.

### CSSETATY.cpy — Set attribute procedure (paragraph code)

Sets BMS field color attributes to red for validation errors and marks blank fields with '*'.

### CSUTLDPY.cpy / CSUTLDWY.cpy — Utility date procedures

Date utility paragraphs for date validation and formatting used by CICS programs.

## Export (Branch Migration)

**Source**: `CVEXPORT.cpy` — Multi-record export layout (Total Record Length: 500 bytes)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | EXPORT-RECORD | — | Group | Export record header |
| 05 | EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | Record type: C=Customer, A=Account, T=Transaction, X=Xref, D=Card |
| 05 | EXPORT-TIMESTAMP | PIC X(26) | Timestamp | Export timestamp |
| 05 | EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary numeric | Sequence number within export |
| 05 | EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Source branch identifier |
| 05 | EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Source region code |
| 05 | EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Record payload (REDEFINES per type) |

**REDEFINES for each record type**:

- **EXPORT-CUSTOMER-DATA**: EXP-CUST-ID (COMP), name, address, phone, SSN, DOB, EFT, FICO (COMP-3)
- **EXPORT-ACCOUNT-DATA**: EXP-ACCT-ID, status, balances (COMP-3), dates, ZIP, group ID
- **EXPORT-TRANSACTION-DATA**: EXP-TRAN-ID, type, category, amount (COMP-3), merchant info, timestamps
- **EXPORT-CARD-XREF-DATA**: EXP-XREF-CARD-NUM, CUST-ID, ACCT-ID (COMP)
- **EXPORT-CARD-DATA**: EXP-CARD-NUM, ACCT-ID (COMP), CVV (COMP), embossed name, dates, status

## Authorization IMS/DB2

### CIPAUDTY.cpy — IMS segment: Pending authorization details

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | PA-AUTHORIZATION-KEY | — | Group | Composite key |
| 10 | PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (compressed) |
| 10 | PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (compressed) |
| 05 | PA-AUTH-ORIG-DATE | PIC X(06) | Date (YYMMDD) | Original authorization date |
| 05 | PA-AUTH-ORIG-TIME | PIC X(06) | Time (HHMMSS) | Original authorization time |
| 05 | PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| 05 | PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type code |
| 05 | PA-CARD-EXPIRY-DATE | PIC X(04) | Date (MMYY) | Card expiry |
| 05 | PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type |
| 05 | PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message source |
| 05 | PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID |
| 05 | PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Authorization response code |
| 05 | PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason code |
| 05 | PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| 05 | PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Transaction amount |
| 05 | PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount |
| 05 | PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | Merchant category code |
| 05 | PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country code |
| 05 | PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| 05 | PA-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| 05 | PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |
| 05 | PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city |
| 05 | PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state |
| 05 | PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP |
| 05 | PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |
| 05 | PA-MATCH-STATUS | PIC X(01) | Alphanumeric | Match status |
| 05 | PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | Fraud indicator |
| 05 | PA-FRAUD-RPT-DATE | PIC X(08) | Date | Fraud report date |

**Validation (88-level conditions)**:

| Condition Name | Value | Meaning |
|---------------|-------|---------|
| PA-AUTH-APPROVED | '00' | Authorization approved |
| PA-MATCH-PENDING | 'P' | Match pending |
| PA-MATCH-AUTH-DECLINED | 'D' | Authorization declined |
| PA-MATCH-PENDING-EXPIRED | 'E' | Pending expired |
| PA-MATCHED-WITH-TRAN | 'M' | Matched with transaction |
| PA-FRAUD-CONFIRMED | 'F' | Fraud confirmed |
| PA-FRAUD-REMOVED | 'R' | Fraud flag removed |

### CIPAUSMY.cpy — IMS segment: Pending authorization summary

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account identifier |
| 05 | PA-CUST-ID | PIC 9(09) | Numeric | Customer identifier |
| 05 | PA-AUTH-STATUS | PIC X(01) | Alphanumeric | Authorization status |
| 05 | PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Array | Account status (5 occurrences) |
| 05 | PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit |
| 05 | PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash limit |
| 05 | PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Credit balance |
| 05 | PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Cash balance |
| 05 | PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of approved authorizations |
| 05 | PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of declined authorizations |
| 05 | PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount |
| 05 | PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount |

### CCPAUERY.cpy — Pending authorization error log

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | ERROR-LOG-RECORD | — | Group | Error log entry |
| 05 | ERR-DATE | PIC X(06) | Date | Error date |
| 05 | ERR-TIME | PIC X(06) | Time | Error time |
| 05 | ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name |
| 05 | ERR-PROGRAM | PIC X(08) | Alphanumeric | Program name |
| 05 | ERR-LOCATION | PIC X(04) | Alphanumeric | Error location code |
| 05 | ERR-LEVEL | PIC X(01) | Alphanumeric | Error severity level |
| 05 | ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem source |
| 05 | ERR-CODE-1 | PIC X(09) | Alphanumeric | Primary error code |
| 05 | ERR-CODE-2 | PIC X(09) | Alphanumeric | Secondary error code |
| 05 | ERR-MESSAGE | PIC X(50) | Alphanumeric | Error message text |
| 05 | ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Event key for correlation |

**Validation (88-level conditions)**:

| Condition Name | Value | Meaning |
|---------------|-------|---------|
| ERR-LOG | 'L' | Log-level error |
| ERR-INFO | 'I' | Informational |
| ERR-WARNING | 'W' | Warning |
| ERR-CRITICAL | 'C' | Critical error |
| ERR-APP | 'A' | Application subsystem |
| ERR-CICS | 'C' | CICS subsystem |
| ERR-IMS | 'I' | IMS subsystem |
| ERR-DB2 | 'D' | DB2 subsystem |
| ERR-MQ | 'M' | MQ subsystem |
| ERR-FILE | 'F' | File subsystem |

### CCPAURQY.cpy — Pending authorization request

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | PA-RQ-AUTH-DATE | PIC X(06) | Date | Request date |
| 05 | PA-RQ-AUTH-TIME | PIC X(06) | Time | Request time |
| 05 | PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| 05 | PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| 05 | PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Date (MMYY) | Card expiry |
| 05 | PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type |
| 05 | PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message source |
| 05 | PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| 05 | PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount |
| 05 | PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | Merchant category code |
| 05 | PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country code |
| 05 | PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| 05 | PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| 05 | PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |
| 05 | PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city |
| 05 | PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state |
| 05 | PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP |
| 05 | PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |

### CCPAURLY.cpy — Pending authorization response

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| 05 | PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |
| 05 | PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID code |
| 05 | PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code |
| 05 | PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason |
| 05 | PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### IMSFUNCS.cpy — IMS DL/I function codes

| Level | Field Name | PIC Clause | Value | Business Meaning |
|-------|-----------|------------|-------|-----------------|
| 01 | FUNC-CODES | — | — | IMS DL/I function code constants |
| 05 | FUNC-GU | PIC X(04) | 'GU  ' | Get Unique |
| 05 | FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique |
| 05 | FUNC-GN | PIC X(04) | 'GN  ' | Get Next |
| 05 | FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next |
| 05 | FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent |
| 05 | FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| 05 | FUNC-REPL | PIC X(04) | 'REPL' | Replace |
| 05 | FUNC-ISRT | PIC X(04) | 'ISRT' | Insert |
| 05 | FUNC-DLET | PIC X(04) | 'DLET' | Delete |
| 05 | PARMCOUNT | PIC S9(05) COMP-5 | +4 | Default parameter count |

### IMS PCBs (PADFLPCB.CPY, PASFLPCB.CPY, PAUTBPCB.CPY)

Each PCB (Program Communication Block) follows the same structure:

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|-----------------|
| 05 | *-DBDNAME | PIC X(08) | Database descriptor name |
| 05 | *-SEG-LEVEL | PIC X(02) | Segment level indicator |
| 05 | *-PCB-STATUS | PIC X(02) | Status code from IMS call |
| 05 | *-PCB-PROCOPT | PIC X(04) | Processing options |
| 05 | *-SEG-NAME | PIC X(08) | Segment name |
| 05 | *-KEYFB-NAME | PIC S9(05) COMP | Key feedback length |
| 05 | *-NUM-SENSEGS | PIC S9(05) COMP | Number of sensitive segments |
| 05 | *-KEYFB | PIC X(100–255) | Key feedback area |

- **PADFLPCB**: Auth Detail Full PCB (KEYFB 255 bytes)
- **PASFLPCB**: Auth Summary Full PCB (KEYFB 100 bytes)
- **PAUTBPCB**: Auth Base PCB (KEYFB 255 bytes)

## Transaction Type DB2

### CSDB2RPY.cpy — DB2 priming query procedure

Paragraph `9998-PRIMING-QUERY` that validates DB2 connectivity by executing `SELECT 1 FROM SYSIBM.SYSDUMMY1`.

### CSDB2RWY.cpy — DB2 read/write procedures

Common DB2 error handling and SQLCODE evaluation paragraphs used by transaction type CICS programs.

### DB2 Tables (from DCL files)

**CARDDEMO.TRANSACTION_TYPE** (DCLTRTYP.dcl):

| Column | DB2 Type | COBOL PIC | Business Meaning |
|--------|----------|-----------|-----------------|
| TR_TYPE | CHAR(2) NOT NULL | PIC X(2) | Transaction type code (PK) |
| TR_DESCRIPTION | VARCHAR(50) NOT NULL | PIC X(50) + LEN | Transaction type description |

**CARDDEMO.TRANSACTION_TYPE_CATEGORY** (DCLTRCAT.dcl):

| Column | DB2 Type | COBOL PIC | Business Meaning |
|--------|----------|-----------|-----------------|
| TRC_TYPE_CODE | CHAR(2) NOT NULL | PIC X(2) | Transaction type code (FK) |
| TRC_TYPE_CATEGORY | CHAR(4) NOT NULL | PIC X(4) | Category code (compound PK) |
| TRC_CAT_DATA | VARCHAR(50) NOT NULL | PIC X(50) + LEN | Category description |

**CARDDEMO.AUTHFRDS** (AUTHFRDS.dcl):

| Column | DB2 Type | COBOL PIC | Business Meaning |
|--------|----------|-----------|-----------------|
| CARD_NUM | CHAR(16) NOT NULL | PIC X(16) | Card number (PK) |
| AUTH_TS | TIMESTAMP NOT NULL | PIC X(26) | Authorization timestamp (PK) |
| AUTH_TYPE | CHAR(4) | PIC X(4) | Authorization type |
| CARD_EXPIRY_DATE | CHAR(4) | PIC X(4) | Card expiry |
| MESSAGE_TYPE | CHAR(6) | PIC X(6) | Message type |
| MESSAGE_SOURCE | CHAR(6) | PIC X(6) | Message source |
| AUTH_ID_CODE | CHAR(6) | PIC X(6) | Authorization ID |
| AUTH_RESP_CODE | CHAR(2) | PIC X(2) | Response code |
| AUTH_RESP_REASON | CHAR(4) | PIC X(4) | Response reason |
| PROCESSING_CODE | CHAR(6) | PIC X(6) | Processing code |
| TRANSACTION_AMT | DECIMAL(12,2) | PIC S9(10)V9(2) COMP-3 | Transaction amount |
| APPROVED_AMT | DECIMAL(12,2) | PIC S9(10)V9(2) COMP-3 | Approved amount |
| MERCHANT_CATAGORY_CODE | CHAR(4) | PIC X(4) | Merchant category code |
| ACQR_COUNTRY_CODE | CHAR(3) | PIC X(3) | Acquirer country code |
| POS_ENTRY_MODE | SMALLINT | PIC S9(4) COMP | POS entry mode |
| MERCHANT_ID | CHAR(15) | PIC X(15) | Merchant ID |
| MERCHANT_NAME | VARCHAR(22) | PIC X(22) + LEN | Merchant name |
| MERCHANT_CITY | CHAR(13) | PIC X(13) | Merchant city |
| MERCHANT_STATE | CHAR(2) | PIC X(2) | Merchant state |
| MERCHANT_ZIP | CHAR(9) | PIC X(9) | Merchant ZIP |
| TRANSACTION_ID | CHAR(15) | PIC X(15) | Transaction ID |
| MATCH_STATUS | CHAR(1) | PIC X(1) | Match status |
| AUTH_FRAUD | CHAR(1) | PIC X(1) | Fraud indicator |
| FRAUD_RPT_DATE | DATE | PIC X(10) | Fraud report date |
| ACCT_ID | DECIMAL(11,0) | PIC S9(11)V COMP-3 | Account ID |
| CUST_ID | DECIMAL(9,0) | PIC S9(9)V COMP-3 | Customer ID |

## Unused

**Source**: `UNUSED1Y.cpy` — Identical structure to CSUSR01Y but with UNUSED- prefix

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | UNUSED-DATA | — | Group | Placeholder/deprecated user record |
| 05 | UNUSED-ID | PIC X(08) | Alphanumeric | Unused ID |
| 05 | UNUSED-FNAME | PIC X(20) | Alphanumeric | Unused first name |
| 05 | UNUSED-LNAME | PIC X(20) | Alphanumeric | Unused last name |
| 05 | UNUSED-PWD | PIC X(08) | Alphanumeric | Unused password |
| 05 | UNUSED-TYPE | PIC X(01) | Alphanumeric | Unused type |
| 05 | UNUSED-FILLER | PIC X(23) | Filler | Reserved space |
