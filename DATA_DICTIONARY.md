# Data Dictionary — CardDemo COBOL Estate

> Extracted from copybooks in `app/cpy/` and sub-application `cpy/` directories.
> Fields grouped by business entity.

---

## 1. Customer Entity

### 1.1 CVCUS01Y.cpy — Customer Record (VSAM)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `CUST-ID` | `PIC 9(09)` | Numeric (9 digits) | Unique customer identifier | Primary key for CUSTFILE VSAM |
| 2 | `CUST-FIRST-NAME` | `PIC X(25)` | Alphanumeric (25) | Customer first name | |
| 3 | `CUST-MIDDLE-NAME` | `PIC X(25)` | Alphanumeric (25) | Customer middle name | |
| 4 | `CUST-LAST-NAME` | `PIC X(25)` | Alphanumeric (25) | Customer last name | |
| 5 | `CUST-ADDR-LINE-1` | `PIC X(50)` | Alphanumeric (50) | Mailing address line 1 | |
| 6 | `CUST-ADDR-LINE-2` | `PIC X(50)` | Alphanumeric (50) | Mailing address line 2 | |
| 7 | `CUST-ADDR-LINE-3` | `PIC X(50)` | Alphanumeric (50) | Mailing address line 3 | |
| 8 | `CUST-ADDR-STATE-CD` | `PIC X(02)` | Alphanumeric (2) | US state code | Validated against CSLKPCDY state code table |
| 9 | `CUST-ADDR-COUNTRY-CD` | `PIC X(03)` | Alphanumeric (3) | Country code | |
| 10 | `CUST-ADDR-ZIP` | `PIC X(10)` | Alphanumeric (10) | ZIP/postal code | |
| 11 | `CUST-PHONE-NUM-1` | `PIC X(15)` | Alphanumeric (15) | Primary phone number | Area code validated against CSLKPCDY |
| 12 | `CUST-PHONE-NUM-2` | `PIC X(15)` | Alphanumeric (15) | Secondary phone number | Area code validated against CSLKPCDY |
| 13 | `CUST-SSN` | `PIC 9(09)` | Numeric (9 digits) | Social Security Number | Must be numeric when present |
| 14 | `CUST-GOVT-ISSUED-ID` | `PIC X(20)` | Alphanumeric (20) | Government-issued ID (passport, license) | |
| 15 | `CUST-DOB-YYYY-MM-DD` | `PIC X(10)` | Date string (YYYY-MM-DD) | Date of birth | Date validated via CSUTLDPY/CSUTLDWY |
| 16 | `CUST-EFT-ACCOUNT-ID` | `PIC X(10)` | Alphanumeric (10) | Electronic Funds Transfer account ID | |
| 17 | `CUST-PRI-CARD-HOLDER-IND` | `PIC X(01)` | Flag (1 char) | Primary cardholder indicator | 'Y'/'N' flag |
| 18 | `CUST-FICO-CREDIT-SCORE` | `PIC 9(03)` | Numeric (3 digits) | FICO credit score | Range 300–850 typically |
| 19 | `FILLER` | `PIC X(168)` | Filler | Reserved space | Padding to fixed record length |

**Record length:** 500 bytes

### 1.2 CUSTREC.cpy — Customer Record (Statement Generation)

Same structure as CVCUS01Y but used in statement generation (CBSTM03A). Identical field layout.

---

## 2. Account Entity

### 2.1 CVACT01Y.cpy — Account Record (VSAM)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Unique account identifier | Primary key for ACCTFILE VSAM |
| 2 | `ACCT-ACTIVE-STATUS` | `PIC X(01)` | Flag (1 char) | Account status | 'Y' = active, 'N' = inactive |
| 3 | `ACCT-CURR-BAL` | `PIC S9(10)V99` | Signed decimal (12,2) | Current balance | Signed — negative = credit balance |
| 4 | `ACCT-CREDIT-LIMIT` | `PIC S9(10)V99` | Signed decimal (12,2) | Credit limit | |
| 5 | `ACCT-CASH-CREDIT-LIMIT` | `PIC S9(10)V99` | Signed decimal (12,2) | Cash advance credit limit | |
| 6 | `ACCT-OPEN-DATE` | `PIC X(10)` | Date string | Account open date | YYYY-MM-DD format |
| 7 | `ACCT-EXPIRAION-DATE` | `PIC X(10)` | Date string | Account expiration date | YYYY-MM-DD format (note: misspelling in source) |
| 8 | `ACCT-REISSUE-DATE` | `PIC X(10)` | Date string | Last card reissue date | YYYY-MM-DD format |
| 9 | `ACCT-CURR-CYC-CREDIT` | `PIC S9(10)V99` | Signed decimal (12,2) | Current billing cycle credits | Accumulated credits this cycle |
| 10 | `ACCT-CURR-CYC-DEBIT` | `PIC S9(10)V99` | Signed decimal (12,2) | Current billing cycle debits | Accumulated debits this cycle |
| 11 | `ACCT-ADDR-ZIP` | `PIC X(10)` | Alphanumeric (10) | Account ZIP code | Used for disclosure group lookup |
| 12 | `ACCT-GROUP-ID` | `PIC X(10)` | Alphanumeric (10) | Account group identifier | Links to disclosure group (DISCGRP) |
| 13 | `FILLER` | `PIC X(178)` | Filler | Reserved space | Padding to fixed record length |

**Record length:** 300 bytes

### 2.2 CVACT02Y.cpy — Card-Account Record

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Card number | Primary key for CARDFILE |
| 2 | `CARD-ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Associated account ID | Foreign key to ACCT-ID |
| 3 | `CARD-CVV-CD` | `PIC 9(03)` | Numeric (3 digits) | Card verification value | |
| 4 | `CARD-EMBOSSED-NAME` | `PIC X(50)` | Alphanumeric (50) | Name embossed on card | |
| 5 | `CARD-EXPIRAION-DATE` | `PIC X(10)` | Date string | Card expiration date | YYYY-MM-DD format; month/year validated in COCRDUPC |
| 6 | `CARD-ACTIVE-STATUS` | `PIC X(01)` | Flag (1 char) | Card status | 'Y' = active, 'N' = inactive |
| 7 | `FILLER` | `PIC X(59)` | Filler | Reserved space | |

**Record length:** 150 bytes

### 2.3 CVACT03Y.cpy — Card Cross-Reference Record

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `XREF-CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Card number | Primary key; also has AIX on XREF-ACCT-ID |
| 2 | `XREF-CUST-ID` | `PIC 9(09)` | Numeric (9 digits) | Customer ID | Foreign key to CUST-ID |
| 3 | `XREF-ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Account ID | Foreign key to ACCT-ID; has alternate index |
| 4 | `FILLER` | `PIC X(14)` | Filler | Reserved space | |

**Record length:** 50 bytes

---

## 3. Card Entity

### 3.1 CVCRD01Y.cpy — Card Work Areas (CICS Online)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `CCARD-AID` | `PIC X(5)` | Alphanumeric (5) | Attention identifier for PF key mapping | 88-level values: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| 2 | `CCARD-NEXT-PROG` | `PIC X(8)` | Alphanumeric (8) | Next program to transfer to | Used by XCTL navigation |
| 3 | `CCARD-NEXT-MAPSET` | `PIC X(7)` | Alphanumeric (7) | Next BMS mapset name | |
| 4 | `CCARD-NEXT-MAP` | `PIC X(7)` | Alphanumeric (7) | Next BMS map name | |
| 5 | `CCARD-ERROR-MSG` | `PIC X(75)` | Alphanumeric (75) | Error message for screen display | |
| 6 | `CCARD-RETURN-MSG` | `PIC X(75)` | Alphanumeric (75) | Return message for screen display | 88-level CCARD-RETURN-MSG-OFF = LOW-VALUES |
| 7 | `CC-ACCT-ID` | `PIC X(11)` | Alphanumeric (11) | Working account ID | Redefined as `CC-ACCT-ID-N PIC 9(11)` |
| 8 | `CC-CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Working card number | Redefined as `CC-CARD-NUM-N PIC 9(16)` |
| 9 | `CC-CUST-ID` | `PIC X(09)` | Alphanumeric (9) | Working customer ID | |

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Record (Master)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `TRAN-ID` | `PIC X(16)` | Alphanumeric (16) | Unique transaction identifier | Generated from card-num + sequence |
| 2 | `TRAN-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type code | FK to TRAN-TYPE-RECORD |
| 3 | `TRAN-CAT-CD` | `PIC 9(04)` | Numeric (4 digits) | Transaction category code | FK to TRAN-CAT-RECORD |
| 4 | `TRAN-SOURCE` | `PIC X(10)` | Alphanumeric (10) | Transaction source (POS, ATM, online) | |
| 5 | `TRAN-DESC` | `PIC X(100)` | Alphanumeric (100) | Transaction description | |
| 6 | `TRAN-AMT` | `PIC S9(09)V99` | Signed decimal (11,2) | Transaction amount | Signed; negative = credit/refund |
| 7 | `TRAN-MERCHANT-ID` | `PIC 9(09)` | Numeric (9 digits) | Merchant identifier | |
| 8 | `TRAN-MERCHANT-NAME` | `PIC X(50)` | Alphanumeric (50) | Merchant name | |
| 9 | `TRAN-MERCHANT-CITY` | `PIC X(50)` | Alphanumeric (50) | Merchant city | |
| 10 | `TRAN-MERCHANT-ZIP` | `PIC X(10)` | Alphanumeric (10) | Merchant ZIP code | |
| 11 | `TRAN-CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Card used for transaction | FK to CARD-NUM |
| 12 | `TRAN-ORIG-TS` | `PIC X(26)` | Timestamp string | Original transaction timestamp | ISO-format timestamp |
| 13 | `TRAN-PROC-TS` | `PIC X(26)` | Timestamp string | Processing timestamp | Set during batch posting |
| 14 | `FILLER` | `PIC X(20)` | Filler | Reserved space | |

**Record length:** 350 bytes

### 4.2 CVTRA06Y.cpy — Daily Transaction Record

Same structure as CVTRA05Y but with `DALYTRAN-` prefix. Used for the daily batch input file before posting.

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `DALYTRAN-ID` | `PIC X(16)` | Alphanumeric (16) | Daily transaction ID |
| 2 | `DALYTRAN-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type code |
| 3 | `DALYTRAN-CAT-CD` | `PIC 9(04)` | Numeric (4) | Category code |
| 4 | `DALYTRAN-SOURCE` | `PIC X(10)` | Alphanumeric (10) | Source |
| 5 | `DALYTRAN-DESC` | `PIC X(100)` | Alphanumeric (100) | Description |
| 6 | `DALYTRAN-AMT` | `PIC S9(09)V99` | Signed decimal (11,2) | Amount |
| 7 | `DALYTRAN-MERCHANT-ID` | `PIC 9(09)` | Numeric (9) | Merchant ID |
| 8 | `DALYTRAN-MERCHANT-NAME` | `PIC X(50)` | Alphanumeric (50) | Merchant name |
| 9 | `DALYTRAN-MERCHANT-CITY` | `PIC X(50)` | Alphanumeric (50) | Merchant city |
| 10 | `DALYTRAN-MERCHANT-ZIP` | `PIC X(10)` | Alphanumeric (10) | Merchant ZIP |
| 11 | `DALYTRAN-CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Card number |
| 12 | `DALYTRAN-ORIG-TS` | `PIC X(26)` | Timestamp string | Original timestamp |
| 13 | `DALYTRAN-PROC-TS` | `PIC X(26)` | Timestamp string | Processing timestamp |
| 14 | `FILLER` | `PIC X(20)` | Filler | Reserved |

**Record length:** 350 bytes

### 4.3 CVTRA01Y.cpy — Transaction Category Balance Record

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `TRANCAT-ACCT-ID` | `PIC 9(11)` | Numeric (11) | Account identifier | Composite key part 1 |
| 2 | `TRANCAT-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type code | Composite key part 2 |
| 3 | `TRANCAT-CD` | `PIC 9(04)` | Numeric (4) | Transaction category code | Composite key part 3 |
| 4 | `TRAN-CAT-BAL` | `PIC S9(09)V99` | Signed decimal (11,2) | Category balance amount | Running total by acct+type+category |
| 5 | `FILLER` | `PIC X(22)` | Filler | Reserved | |

**Record length:** 50 bytes

### 4.4 CVTRA02Y.cpy — Disclosure Group Record

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `DIS-ACCT-GROUP-ID` | `PIC X(10)` | Alphanumeric (10) | Account group identifier | Composite key part 1 |
| 2 | `DIS-TRAN-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type code | Composite key part 2 |
| 3 | `DIS-TRAN-CAT-CD` | `PIC 9(04)` | Numeric (4) | Category code | Composite key part 3 |
| 4 | `DIS-INT-RATE` | `PIC S9(04)V99` | Signed decimal (6,2) | Interest rate for this category/group | Used by CBACT04C for interest calc |
| 5 | `FILLER` | `PIC X(28)` | Filler | Reserved | |

**Record length:** 50 bytes

### 4.5 CVTRA03Y.cpy — Transaction Type Reference Record

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `TRAN-TYPE` | `PIC X(02)` | Alphanumeric (2) | Transaction type code | Primary key |
| 2 | `TRAN-TYPE-DESC` | `PIC X(50)` | Alphanumeric (50) | Type description | e.g., "Purchase", "Cash Advance" |
| 3 | `FILLER` | `PIC X(08)` | Filler | Reserved | |

**Record length:** 60 bytes

### 4.6 CVTRA04Y.cpy — Transaction Category Reference Record

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `TRAN-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type code | Composite key part 1 |
| 2 | `TRAN-CAT-CD` | `PIC 9(04)` | Numeric (4) | Category code | Composite key part 2 |
| 3 | `TRAN-CAT-TYPE-DESC` | `PIC X(50)` | Alphanumeric (50) | Category description | e.g., "Retail Purchase", "Gas Station" |
| 4 | `FILLER` | `PIC X(04)` | Filler | Reserved | |

**Record length:** 60 bytes

### 4.7 CVTRA07Y.cpy — Transaction Report Layout

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `REPT-SHORT-NAME` | `PIC X(38)` | Alphanumeric | Report short name ('DALYREPT') |
| 2 | `REPT-LONG-NAME` | `PIC X(41)` | Alphanumeric | Report title ('Daily Transaction Report') |
| 3 | `REPT-DATE-HEADER` | `PIC X(12)` | Alphanumeric | Date range label |
| 4 | `REPT-START-DATE` | `PIC X(10)` | Date string | Report start date |
| 5 | `REPT-END-DATE` | `PIC X(10)` | Date string | Report end date |
| 6 | `TRAN-REPORT-TRANS-ID` | `PIC X(16)` | Alphanumeric | Transaction ID in report line |
| 7 | `TRAN-REPORT-ACCOUNT-ID` | `PIC X(11)` | Alphanumeric | Account ID in report line |
| 8 | `TRAN-REPORT-TYPE-CD` | `PIC X(02)` | Alphanumeric | Type code in report line |
| 9 | `TRAN-REPORT-TYPE-DESC` | `PIC X(15)` | Alphanumeric | Type description |
| 10 | `TRAN-REPORT-CAT-CD` | `PIC 9(04)` | Numeric | Category code |
| 11 | `TRAN-REPORT-CAT-DESC` | `PIC X(29)` | Alphanumeric | Category description |
| 12 | `TRAN-REPORT-SOURCE` | `PIC X(10)` | Alphanumeric | Transaction source |
| 13 | `TRAN-REPORT-AMT` | `PIC -ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Formatted amount |

### 4.8 COSTM01.CPY — Transaction Record (Statement Generation)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `TRNX-CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Card number (key part 1) |
| 2 | `TRNX-ID` | `PIC X(16)` | Alphanumeric (16) | Transaction ID (key part 2) |
| 3 | `TRNX-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Type code |
| 4 | `TRNX-CAT-CD` | `PIC 9(04)` | Numeric (4) | Category code |
| 5 | `TRNX-SOURCE` | `PIC X(10)` | Alphanumeric (10) | Source |
| 6 | `TRNX-DESC` | `PIC X(100)` | Alphanumeric (100) | Description |
| 7 | `TRNX-AMT` | `PIC S9(09)V99` | Signed decimal | Amount |
| 8 | `TRNX-MERCHANT-ID` | `PIC 9(09)` | Numeric (9) | Merchant ID |
| 9 | `TRNX-MERCHANT-NAME` | `PIC X(50)` | Alphanumeric (50) | Merchant name |
| 10 | `TRNX-MERCHANT-CITY` | `PIC X(50)` | Alphanumeric (50) | Merchant city |
| 11 | `TRNX-MERCHANT-ZIP` | `PIC X(10)` | Alphanumeric (10) | Merchant ZIP |
| 12 | `TRNX-ORIG-TS` | `PIC X(26)` | Timestamp | Original timestamp |
| 13 | `TRNX-PROC-TS` | `PIC X(26)` | Timestamp | Processing timestamp |
| 14 | `FILLER` | `PIC X(20)` | Filler | Reserved |

**Key structure:** `TRNX-CARD-NUM + TRNX-ID` (32-byte composite key)

---

## 5. Communication & Control Structures

### 5.1 COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| | **CDEMO-GENERAL-INFO** | | Group | Navigation and control | |
| 1 | `CDEMO-FROM-TRANID` | `PIC X(04)` | Alphanumeric (4) | Source CICS transaction ID | |
| 2 | `CDEMO-FROM-PROGRAM` | `PIC X(08)` | Alphanumeric (8) | Source program name | |
| 3 | `CDEMO-TO-TRANID` | `PIC X(04)` | Alphanumeric (4) | Target CICS transaction ID | |
| 4 | `CDEMO-TO-PROGRAM` | `PIC X(08)` | Alphanumeric (8) | Target program name | Used for XCTL routing |
| 5 | `CDEMO-USER-ID` | `PIC X(08)` | Alphanumeric (8) | Logged-in user ID | From sign-on |
| 6 | `CDEMO-USER-TYPE` | `PIC X(01)` | Flag (1 char) | User type | 88: 'A' = Admin, 'U' = User |
| | **CDEMO-CUSTOMER-INFO** | | Group | Customer context | |
| 7 | `CDEMO-CUST-ID` | `PIC 9(09)` | Numeric (9) | Current customer ID | |
| 8 | `CDEMO-CUST-FNAME` | `PIC X(25)` | Alphanumeric (25) | Customer first name | |
| 9 | `CDEMO-CUST-MNAME` | `PIC X(25)` | Alphanumeric (25) | Customer middle name | |
| 10 | `CDEMO-CUST-LNAME` | `PIC X(25)` | Alphanumeric (25) | Customer last name | |
| | **CDEMO-ACCOUNT-INFO** | | Group | Account context | |
| 11 | `CDEMO-ACCT-ID` | `PIC 9(11)` | Numeric (11) | Current account ID | |
| 12 | `CDEMO-ACCT-STATUS` | `PIC X(01)` | Flag (1 char) | Account status | |
| | **CDEMO-CARD-INFO** | | Group | Card context | |
| 13 | `CDEMO-CARD-NUM` | `PIC 9(16)` | Numeric (16) | Current card number | |

### 5.2 CSUSR01Y.cpy — User Security Record

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|------------|-----------|-----------|------------------|-------------------|
| 1 | `SEC-USR-ID` | `PIC X(08)` | Alphanumeric (8) | User login ID | Primary key for USRSEC VSAM |
| 2 | `SEC-USR-FNAME` | `PIC X(20)` | Alphanumeric (20) | User first name | |
| 3 | `SEC-USR-LNAME` | `PIC X(20)` | Alphanumeric (20) | User last name | |
| 4 | `SEC-USR-PWD` | `PIC X(08)` | Alphanumeric (8) | User password | Plaintext; max 8 chars |
| 5 | `SEC-USR-TYPE` | `PIC X(01)` | Flag (1 char) | User type | 'A' = admin, 'U' = regular user |
| 6 | `SEC-USR-FILLER` | `PIC X(23)` | Filler | Reserved | |

**Record length:** 80 bytes

---

## 6. Export / Migration Structure

### 6.1 CVEXPORT.cpy — Export Record

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| | **Header fields** | | | |
| 1 | `EXPORT-REC-TYPE` | `PIC X(1)` | Flag | Record type: 'C'=Customer, 'A'=Account, etc. |
| 2 | `EXPORT-TIMESTAMP` | `PIC X(26)` | Timestamp | Export timestamp |
| 3 | `EXPORT-SEQUENCE-NUM` | `PIC 9(9) COMP` | Binary numeric | Sequence number |
| 4 | `EXPORT-BRANCH-ID` | `PIC X(4)` | Alphanumeric | Branch identifier |
| 5 | `EXPORT-REGION-CODE` | `PIC X(5)` | Alphanumeric | Region code |
| 6 | `EXPORT-RECORD-DATA` | `PIC X(460)` | Alphanumeric | Record payload (redefines follow) |
| | **EXPORT-CUSTOMER-DATA** (redefines EXPORT-RECORD-DATA) | | | |
| 7 | `EXP-CUST-ID` | `PIC 9(09) COMP` | Binary numeric | Customer ID |
| 8 | `EXP-CUST-FIRST-NAME` | `PIC X(25)` | Alphanumeric | First name |
| 9 | `EXP-CUST-LAST-NAME` | `PIC X(25)` | Alphanumeric | Last name |
| 10 | `EXP-CUST-SSN` | `PIC 9(09)` | Numeric | SSN |
| 11 | `EXP-CUST-DOB-YYYY-MM-DD` | `PIC X(10)` | Date | Date of birth |
| 12 | `EXP-CUST-FICO-CREDIT-SCORE` | `PIC 9(03) COMP-3` | Packed decimal | FICO score |
| | **EXPORT-ACCOUNT-DATA** (redefines EXPORT-RECORD-DATA) | | | |
| 13 | `EXP-ACCT-ID` | `PIC 9(11)` | Numeric | Account ID |
| 14 | `EXP-ACCT-ACTIVE-STATUS` | `PIC X(01)` | Flag | Active status |
| 15 | `EXP-ACCT-CURR-BAL` | `PIC S9(10)V99 COMP-3` | Packed decimal | Current balance |
| 16 | `EXP-ACCT-CREDIT-LIMIT` | `PIC S9(10)V99` | Signed decimal | Credit limit |
| 17 | `EXP-ACCT-CASH-CREDIT-LIMIT` | `PIC S9(10)V99 COMP-3` | Packed decimal | Cash credit limit |

---

## 7. Utility & UI Copybooks

### 7.1 CSDAT01Y.cpy — Date/Time Structure

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `WS-CURDATE-YEAR` | `PIC 9(04)` | Numeric (4) | Current year (YYYY) |
| 2 | `WS-CURDATE-MONTH` | `PIC 9(02)` | Numeric (2) | Current month (MM) |
| 3 | `WS-CURDATE-DAY` | `PIC 9(02)` | Numeric (2) | Current day (DD) |

### 7.2 CSUTLDWY.cpy — Date Edit Working Storage

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|---|------------|-----------|-----------|------------------|------------|
| 1 | `WS-EDIT-DATE-CC` | `PIC X(2)` | Alphanumeric | Century portion | 88: THIS-CENTURY=20, LAST-CENTURY=19 |
| 2 | `WS-EDIT-DATE-YY` | `PIC X(2)` | Alphanumeric | Year portion | |
| 3 | `WS-EDIT-DATE-MM` | `PIC X(2)` | Alphanumeric | Month portion | 88: WS-VALID-MONTH = 1–12, WS-31-DAY-MONTH, WS-FEBRUARY |
| 4 | `WS-EDIT-DATE-DD` | `PIC X(2)` | Alphanumeric | Day portion | 88: WS-VALID-DAY = 1–31, WS-DAY-31, WS-DAY-30, WS-DAY-29, WS-VALID-FEB-DAY = 1–28 |
| 5 | `WS-EDIT-DATE-BINARY` | `PIC S9(9) BINARY` | Binary | Lilian date (for date arithmetic) | |

### 7.3 CSUTLDPY.cpy — Date Validation Procedures

Contains inline paragraph code for `EDIT-DATE-CCYYMMDD`, `EDIT-YEAR-CCYY`, `EDIT-MONTH-MM`, `EDIT-DAY-DD` with validation rules:
- Year must be numeric, must not be blank
- Month must be 1–12
- Day must be valid for the given month (handles 28/29/30/31-day months)
- Leap year validation for February

### 7.4 CSMSG01Y.cpy — Common Messages

| # | Field Name | PIC Clause | Value | Business Meaning |
|---|------------|-----------|-------|------------------|
| 1 | `CCDA-MSG-THANK-YOU` | `PIC X(50)` | 'Thank you for using CardDemo application...' | Exit message |
| 2 | `CCDA-MSG-INVALID-KEY` | `PIC X(50)` | 'Invalid key pressed. Please see below...' | Invalid key error |

### 7.5 CSMSG02Y.cpy — Abend Handling

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `ABEND-CODE` | `PIC X(4)` | Alphanumeric | Abend code |
| 2 | `ABEND-CULPRIT` | `PIC X(8)` | Alphanumeric | Program causing abend |
| 3 | `ABEND-REASON` | `PIC X(50)` | Alphanumeric | Reason description |
| 4 | `ABEND-MSG` | `PIC X(72)` | Alphanumeric | Full error message |

### 7.6 CSLKPCDY.cpy — Lookup Code Repository

Contains validation tables for:
- **US Phone Area Codes** — table of valid 3-digit area codes (200+ entries)
- **US State Codes** — table of valid 2-character state codes (50 states + territories)
- Used by COACTUPC for address and phone validation

### 7.7 COADM02Y.cpy — Admin Menu Options

Menu option definitions for the admin screen navigation.

### 7.8 COMEN02Y.cpy — Main Menu Options

Menu option definitions for the main menu screen navigation.

### 7.9 COTTL01Y.cpy — Screen Titles

Title and header text for CICS screen displays.

### 7.10 CSSETATY.cpy — Screen Attribute Setting

Template copybook with REPLACING directives for setting BMS field attributes (color highlighting for errors).

### 7.11 CSSTRPFY.cpy — PF Key Store Procedure

Inline procedure that maps EIBAID to CCARD-AID values for all PF keys (PF1–PF15, Enter, Clear, PA1, PA2).

### 7.12 CODATECN.cpy — Date Conversion

Date format conversion utility fields.

### 7.13 UNUSED1Y.cpy — Unused Structure

| # | Field Name | PIC Clause | Data Type | Notes |
|---|------------|-----------|-----------|-------|
| 1 | `UNUSED-ID` | `PIC X(08)` | Alphanumeric | Inactive |
| 2 | `UNUSED-FNAME` | `PIC X(20)` | Alphanumeric | Inactive |
| 3 | `UNUSED-LNAME` | `PIC X(20)` | Alphanumeric | Inactive |
| 4 | `UNUSED-PWD` | `PIC X(08)` | Alphanumeric | Inactive |
| 5 | `UNUSED-TYPE` | `PIC X(01)` | Alphanumeric | Inactive |

Identical layout to CSUSR01Y — appears to be a deprecated/dead copy.

---

## 8. Sub-Application Copybooks

### 8.1 Authorization (IMS/DB2/MQ)

#### CIPAUSMY.cpy — Authorization Summary Record

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `PA-ACCT-ID` | `PIC S9(11) COMP-3` | Packed decimal | Account ID |
| 2 | `PA-CUST-ID` | `PIC 9(09)` | Numeric | Customer ID |
| 3 | `PA-AUTH-STATUS` | `PIC X(01)` | Flag | Authorization status |
| 4 | `PA-ACCOUNT-STATUS` | `PIC X(02) OCCURS 5` | Array | Account status array (5 entries) |
| 5 | `PA-CREDIT-LIMIT` | `PIC S9(09)V99 COMP-3` | Packed decimal | Credit limit |
| 6 | `PA-CASH-LIMIT` | `PIC S9(09)V99 COMP-3` | Packed decimal | Cash limit |
| 7 | `PA-CREDIT-BALANCE` | `PIC S9(09)V99 COMP-3` | Packed decimal | Credit balance |
| 8 | `PA-CASH-BALANCE` | `PIC S9(09)V99 COMP-3` | Packed decimal | Cash balance |
| 9 | `PA-APPROVED-AUTH-CNT` | `PIC S9(04) COMP` | Binary | Approved auth count |
| 10 | `PA-DECLINED-AUTH-CNT` | `PIC S9(04) COMP` | Binary | Declined auth count |
| 11 | `PA-APPROVED-AUTH-AMT` | `PIC S9(09)V99 COMP-3` | Packed decimal | Total approved amount |
| 12 | `PA-DECLINED-AUTH-AMT` | `PIC S9(09)V99 COMP-3` | Packed decimal | Total declined amount |

#### CIPAUDTY.cpy — Authorization Detail Record

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `PA-AUTH-DATE-9C` | `PIC S9(05) COMP-3` | Packed decimal | Authorization date (compressed) |
| 2 | `PA-AUTH-TIME-9C` | `PIC S9(09) COMP-3` | Packed decimal | Authorization time (compressed) |
| 3 | `PA-AUTH-ORIG-DATE` | `PIC X(06)` | Date string | Original date |
| 4 | `PA-AUTH-ORIG-TIME` | `PIC X(06)` | Time string | Original time |
| 5 | `PA-CARD-NUM` | `PIC X(16)` | Alphanumeric | Card number |
| 6 | `PA-AUTH-TYPE` | `PIC X(04)` | Alphanumeric | Authorization type |
| 7 | `PA-CARD-EXPIRY-DATE` | `PIC X(04)` | Date string | Card expiry (MMYY) |
| 8 | `PA-MESSAGE-TYPE` | `PIC X(06)` | Alphanumeric | Message type |
| 9 | `PA-MESSAGE-SOURCE` | `PIC X(06)` | Alphanumeric | Message source |
| 10 | `PA-AUTH-ID-CODE` | `PIC X(06)` | Alphanumeric | Authorization ID code |
| 11 | `PA-AUTH-RESP-CODE` | `PIC X(02)` | Alphanumeric | Response code |

#### CCPAURQY.cpy — Authorization Request Message

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `PA-RQ-AUTH-DATE` | `PIC X(06)` | Date | Request date |
| 2 | `PA-RQ-AUTH-TIME` | `PIC X(06)` | Time | Request time |
| 3 | `PA-RQ-CARD-NUM` | `PIC X(16)` | Alphanumeric | Card number |
| 4 | `PA-RQ-AUTH-TYPE` | `PIC X(04)` | Alphanumeric | Auth type |
| 5 | `PA-RQ-CARD-EXPIRY-DATE` | `PIC X(04)` | Date | Card expiry |
| 6 | `PA-RQ-MESSAGE-TYPE` | `PIC X(06)` | Alphanumeric | Message type |
| 7 | `PA-RQ-MESSAGE-SOURCE` | `PIC X(06)` | Alphanumeric | Source |
| 8 | `PA-RQ-PROCESSING-CODE` | `PIC 9(06)` | Numeric | Processing code |
| 9 | `PA-RQ-TRANSACTION-AMT` | `PIC +9(10).99` | Edited numeric | Transaction amount |
| 10 | `PA-RQ-MERCHANT-CATAGORY-CODE` | `PIC X(04)` | Alphanumeric | Merchant category code |
| 11 | `PA-RQ-ACQR-COUNTRY-CODE` | `PIC X(03)` | Alphanumeric | Acquirer country code |
| 12 | `PA-RQ-POS-ENTRY-MODE` | `PIC 9(02)` | Numeric | POS entry mode |

#### CCPAURLY.cpy — Authorization Reply Message

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `PA-RL-CARD-NUM` | `PIC X(16)` | Alphanumeric | Card number |
| 2 | `PA-RL-TRANSACTION-ID` | `PIC X(15)` | Alphanumeric | Transaction ID |
| 3 | `PA-RL-AUTH-ID-CODE` | `PIC X(06)` | Alphanumeric | Auth ID code |
| 4 | `PA-RL-AUTH-RESP-CODE` | `PIC X(02)` | Alphanumeric | Response code |
| 5 | `PA-RL-AUTH-RESP-REASON` | `PIC X(04)` | Alphanumeric | Response reason |
| 6 | `PA-RL-APPROVED-AMT` | `PIC +9(10).99` | Edited numeric | Approved amount |

#### CCPAUERY.cpy — Error Log Record

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|------------|-----------|-----------|------------------|
| 1 | `ERR-DATE` | `PIC X(06)` | Date | Error date |
| 2 | `ERR-TIME` | `PIC X(06)` | Time | Error time |
| 3 | `ERR-APPLICATION` | `PIC X(08)` | Alphanumeric | Application name |
| 4 | `ERR-PROGRAM` | `PIC X(08)` | Alphanumeric | Program name |
| 5 | `ERR-LOCATION` | `PIC X(04)` | Alphanumeric | Error location |
| 6 | `ERR-LEVEL` | `PIC X(01)` | Flag | Severity: 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical |
| 7 | `ERR-SUBSYSTEM` | `PIC X(01)` | Flag | Subsystem code |

#### IMS PCB Copybooks

- **PAUTBPCB.CPY** — Primary IMS PCB for authorization database
- **PASFLPCB.CPY** — Secondary IMS PCB (summary file)
- **PADFLPCB.CPY** — Detail file IMS PCB
- **IMSFUNCS.cpy** — IMS DL/I function codes (GU, GHU, GN, GHN, GNP, GHNP, REPL, ISRT, DLET)

### 8.2 Transaction Type DB2 Copybooks

- **CSDB2RPY.cpy** — DB2 priming query (SELECT 1 FROM SYSIBM.SYSDUMMY1)
- **CSDB2RWY.cpy** — DB2 common working storage variables
- **DCLTRTYP** (inline) — DB2 TRNTYPE table DCLGEN
- **DCLTRCAT** (inline) — DB2 TRNCATG table DCLGEN

---

## 9. VSAM Dataset Summary

| Dataset Name | Record Format | Key | Record Size | Copybook |
|-------------|--------------|-----|-------------|----------|
| ACCTDATA.VSAM.KSDS | KSDS | ACCT-ID (11 bytes, offset 0) | 300 | CVACT01Y |
| CARDDATA.VSAM.KSDS | KSDS | CARD-NUM (16 bytes, offset 0) | 150 | CVACT02Y |
| CARDXREF.VSAM.KSDS | KSDS + AIX | XREF-CARD-NUM (16 bytes) + AIX on XREF-ACCT-ID | 50 | CVACT03Y |
| CUSTDATA.VSAM.KSDS | KSDS | CUST-ID (9 bytes, offset 0) | 500 | CVCUS01Y |
| TRANSACT.VSAM.KSDS | KSDS | TRAN-CARD-NUM + TRAN-ID (32 bytes) | 350 | CVTRA05Y |
| TCATBALF.VSAM.KSDS | KSDS | ACCT-ID+TYPE+CAT (17 bytes) | 50 | CVTRA01Y |
| TRANTYPE.VSAM.KSDS | KSDS | TRAN-TYPE (2 bytes) | 60 | CVTRA03Y |
| TRANCATG.VSAM.KSDS | KSDS | TYPE+CAT (6 bytes) | 60 | CVTRA04Y |
| DISCGRP.VSAM.KSDS | KSDS | GROUP+TYPE+CAT (16 bytes) | 50 | CVTRA02Y |
| USRSEC.VSAM.KSDS | KSDS | SEC-USR-ID (8 bytes) | 80 | CSUSR01Y |
