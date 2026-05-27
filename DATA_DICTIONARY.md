# DATA DICTIONARY — CardDemo COBOL Estate

> Comprehensive catalog of all copybooks in the CardDemo application, grouped by business entity.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Master Record (20 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| ACCT-ID | PIC 9(11) | Numeric | Unique account identifier (11-digit) | Primary key for ACCTDATA VSAM |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Account status flag | 'Y' = Active, 'N' = Inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance | Signed; allows negative balances |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Credit limit for the account | Must be positive |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Date account was opened | YYYY-MM-DD format |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | YYYY-MM-DD format |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Last card reissue date | YYYY-MM-DD format |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total | Running total for billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total | Running total for billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account ZIP code | US postal code |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure group identifier | Links to DISCGRP file for interest rates |
| FILLER | PIC X(178) | — | Reserved/padding | — |

**Record size: 300 bytes** | **VSAM key: ACCT-ID (11 bytes at offset 0)**

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (26 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| CUST-ID | PIC 9(09) | Numeric | Unique customer identifier | Primary key for CUSTDATA VSAM |
| CUST-FIRST-NAME | PIC X(25) | Alpha | Customer first name | Required; alpha-only |
| CUST-MIDDLE-NAME | PIC X(25) | Alpha | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alpha | Customer last name | Required; alpha-only |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code | Validated against CSLKPCDY state list |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code | — |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | Validated against CSLKPCDY zip prefixes |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone | Format: (NNN)NNN-NNNN; area code validated |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone | Optional; same format as above |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit numeric |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID | — |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | YYYY-MM-DD; validated via CSUTLDPY |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | Electronic funds transfer account | — |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary cardholder indicator | 'Y'/'N' |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | 300–850 range |
| FILLER | PIC X(168) | — | Reserved/padding | — |

**Record size: 500 bytes** | **VSAM key: CUST-ID (9 bytes at offset 0)**

### CUSTREC.cpy — Customer Record (Statement variant, 26 lines)

Identical layout to CVCUS01Y but used in statement generation (CBSTM03A). Field `CUST-DOB-YYYYMMDD` uses compact date format `PIC X(10)` vs `CUST-DOB-YYYY-MM-DD`.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (14 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Credit card number (16 digits) | Primary key for CARDDATA VSAM |
| CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | Foreign key to ACCTDATA |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit CVV |
| CARD-EMBOSSED-NAME | PIC X(50) | Alpha | Name embossed on card | — |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | YYYY-MM-DD format |
| CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card active status | 'Y'/'N' |
| FILLER | PIC X(59) | — | Reserved/padding | — |

**Record size: 150 bytes** | **VSAM key: CARD-NUM (16 bytes at offset 0)** | **AIX on CARD-ACCT-ID**

### CVACT03Y.cpy — Card–Account Cross-Reference Record (11 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Primary key; links card to customer/account |
| XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID | Foreign key to CUSTDATA |
| XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID | Foreign key to ACCTDATA |
| FILLER | PIC X(14) | — | Reserved/padding | — |

**Record size: 50 bytes** | **VSAM key: XREF-CARD-NUM (16 bytes at offset 0)** | **AIX on XREF-ACCT-ID**

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Master Record (21 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | Primary key for TRANSACT VSAM |
| TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Links to TRANTYPE file |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Links to TRANCATG file |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source system | — |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Signed; negative = credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used | Foreign key to CARDDATA |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original transaction timestamp | ISO format |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | ISO format |
| FILLER | PIC X(20) | — | Reserved/padding | — |

**Record size: 350 bytes** | **VSAM key: TRAN-ID (16 bytes at offset 0)** | **AIX on TRAN-CARD-NUM**

### CVTRA06Y.cpy — Daily Transaction Record (21 lines)

Identical layout to CVTRA05Y but with `DALYTRAN-` prefix. Used for the daily unposted transaction input file before posting to master.

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| DALYTRAN-ID | PIC X(16) | Daily transaction identifier |
| DALYTRAN-TYPE-CD | PIC X(02) | Transaction type code |
| DALYTRAN-CAT-CD | PIC 9(04) | Transaction category code |
| DALYTRAN-SOURCE | PIC X(10) | Source system |
| DALYTRAN-DESC | PIC X(100) | Description |
| DALYTRAN-AMT | PIC S9(09)V99 | Amount |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Merchant ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Merchant name |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Merchant city |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Merchant ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Original timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Processing timestamp |
| FILLER | PIC X(20) | Reserved |

### CVTRA01Y.cpy — Transaction Category Balance Record (13 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | Composite key part 1 |
| TRANCAT-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Composite key part 2 |
| TRANCAT-CD | PIC 9(04) | Numeric | Category code | Composite key part 3 |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance per category | Updated by CBTRN02C |
| FILLER | PIC X(22) | — | Reserved | — |

### CVTRA02Y.cpy — Disclosure/Interest Rate Group Record (13 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group identifier | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | — |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Category code | — |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate (%) | Used by CBACT04C for interest calc |
| FILLER | PIC X(28) | — | Reserved | — |

### CVTRA03Y.cpy — Transaction Type Reference Record (10 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-TYPE | PIC X(02) | Alpha | Transaction type code | Primary key for TRANTYPE VSAM |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Type description | e.g., "Purchase", "Cash Advance" |
| FILLER | PIC X(08) | — | Reserved | — |

### CVTRA04Y.cpy — Transaction Category Reference Record (12 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Composite key part 1 |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Category code | Composite key part 2 |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | e.g., "Retail Purchase", "Online Purchase" |
| FILLER | PIC X(04) | — | Reserved | — |

### CVTRA07Y.cpy — Transaction Report Layout (73 lines)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| REPT-SHORT-NAME | PIC X(38) | Report short title |
| REPT-LONG-NAME | PIC X(41) | Report long title |
| REPT-DATE-HEADER | PIC X(12) | "Date Range:" header |
| REPT-START-DATE | PIC X(10) | Report start date |
| REPT-END-DATE | PIC X(10) | Report end date |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Transaction ID for report line |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Account ID for report line |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Type code |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Category code |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Source |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Formatted amount |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Account subtotal |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Grand total |

### COSTM01.CPY — Statement Transaction Record (38 lines)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| TRNX-CARD-NUM | PIC X(16) | Card number |
| TRNX-ID | PIC X(16) | Transaction ID |
| TRNX-TYPE-CD | PIC X(02) | Type code |
| TRNX-CAT-CD | PIC 9(04) | Category code |
| TRNX-SOURCE | PIC X(10) | Source |
| TRNX-DESC | PIC X(100) | Description |
| TRNX-AMT | PIC S9(09)V99 | Amount |
| TRNX-MERCHANT-ID | PIC 9(09) | Merchant ID |
| TRNX-MERCHANT-NAME | PIC X(50) | Merchant name |
| TRNX-MERCHANT-CITY | PIC X(50) | Merchant city |
| TRNX-MERCHANT-ZIP | PIC X(10) | Merchant ZIP |
| TRNX-ORIG-TS | PIC X(26) | Original timestamp |
| TRNX-PROC-TS | PIC X(26) | Processing timestamp |
| FILLER | PIC X(20) | Reserved |

---

## 5. Export/Migration Entity

### CVEXPORT.cpy — Branch Migration Export Record (103 lines)

Uses REDEFINES to pack multiple record types into a single file:

**Header Fields (common to all record types):**

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| EXPORT-REC-TYPE | PIC X(1) | Record type: 'C'=Customer, 'A'=Account, etc. |
| EXPORT-TIMESTAMP | PIC X(26) | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Sequence number |
| EXPORT-BRANCH-ID | PIC X(4) | Source branch ID |
| EXPORT-REGION-CODE | PIC X(5) | Region code |
| EXPORT-RECORD-DATA | PIC X(460) | Polymorphic data area (REDEFINES below) |

**Customer REDEFINES (EXPORT-CUSTOMER-DATA):**

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| EXP-CUST-ID | PIC 9(09) COMP | Customer ID (binary) |
| EXP-CUST-FIRST-NAME | PIC X(25) | First name |
| EXP-CUST-MIDDLE-NAME | PIC X(25) | Middle name |
| EXP-CUST-LAST-NAME | PIC X(25) | Last name |
| EXP-CUST-ADDR-LINE (×3) | PIC X(50) | Address lines (OCCURS 3) |
| EXP-CUST-ADDR-STATE-CD | PIC X(02) | State code |
| EXP-CUST-ADDR-COUNTRY-CD | PIC X(03) | Country code |
| EXP-CUST-ADDR-ZIP | PIC X(10) | ZIP code |
| EXP-CUST-PHONE-NUM (×2) | PIC X(15) | Phone numbers (OCCURS 2) |
| EXP-CUST-SSN | PIC 9(09) | SSN |
| EXP-CUST-GOVT-ISSUED-ID | PIC X(20) | Government ID |
| EXP-CUST-DOB-YYYY-MM-DD | PIC X(10) | Date of birth |
| EXP-CUST-EFT-ACCOUNT-ID | PIC X(10) | EFT account |
| EXP-CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Primary cardholder |
| EXP-CUST-FICO-CREDIT-SCORE | PIC 9(03) COMP-3 | FICO score (packed decimal) |

**Account REDEFINES (EXPORT-ACCOUNT-DATA):**

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| EXP-ACCT-ID | PIC 9(11) | Account ID |
| EXP-ACCT-ACTIVE-STATUS | PIC X(01) | Active status |
| EXP-ACCT-CURR-BAL | PIC S9(10)V99 COMP-3 | Current balance (packed) |
| EXP-ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Credit limit |
| EXP-ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 COMP-3 | Cash credit limit (packed) |
| EXP-ACCT-OPEN-DATE | PIC X(10) | Open date |
| EXP-ACCT-EXPIRAION-DATE | PIC X(10) | Expiration date |
| EXP-ACCT-REISSUE-DATE | PIC X(10) | Reissue date |
| EXP-ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Cycle credit |

---

## 6. Security / User Entity

### CSUSR01Y.cpy — User Security Record (26 lines)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | User login ID | Primary key for USRSEC VSAM |
| SEC-USR-FNAME | PIC X(20) | Alpha | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alpha | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | User password | Plain text |
| SEC-USR-TYPE | PIC X(01) | Alpha | User type | 'A' = Admin, 'U' = Regular |
| SEC-USR-FILLER | PIC X(23) | — | Reserved/padding | — |

**Record size: 80 bytes** | **VSAM key: SEC-USR-ID (8 bytes at offset 0)**

### UNUSED1Y.cpy — Unused Security Record (10 lines)

Same layout as CSUSR01Y but with `UNUSED-` prefix. Placeholder/template.

---

## 7. Authorization Entity (Sub-Application)

### CIPAUDTY.cpy — Pending Authorization Detail Record

Located in `app/app-authorization-ims-db2-mq/cpy/`. Used for IMS DB segments storing pending card authorizations.

### CIPAUSMY.cpy — Pending Authorization Summary Record

Summary-level authorization data for BMS screen display.

### CCPAURQY.cpy — Authorization Request (MQ message, 36 lines)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| PA-RQ-CARD-NUM | PIC X(16) | Card number |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Transaction ID |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Requested amount |
| PA-RQ-MERCHANT-ID | PIC X(09) | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(50) | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(50) | Merchant city |
| PA-RQ-MERCHANT-ZIP | PIC X(10) | Merchant ZIP |

### CCPAURLY.cpy — Authorization Response (MQ message, 24 lines)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| PA-RL-CARD-NUM | PIC X(16) | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Authorization ID code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Response reason |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Approved amount |

### CCPAUERY.cpy — Authorization Error Log (40 lines)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| ERR-DATE | PIC X(06) | Error date |
| ERR-TIME | PIC X(06) | Error time |
| ERR-APPLICATION | PIC X(08) | Application name |
| ERR-PROGRAM | PIC X(08) | Program name |
| ERR-LOCATION | PIC X(04) | Error location |
| ERR-LEVEL | PIC X(01) | Severity: L/I/W/C |
| ERR-SUBSYSTEM | PIC X(01) | Subsystem: A/C/I/D/M/F |
| ERR-CODE-1 | PIC X(09) | Primary error code |
| ERR-CODE-2 | PIC X(09) | Secondary error code |
| ERR-MESSAGE | PIC X(50) | Error message text |
| ERR-EVENT-KEY | PIC X(20) | Event correlation key |

---

## 8. Application Infrastructure Copybooks

### COCOM01Y.cpy — COMMAREA (Communication Area, 47 lines)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Originating CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Originating program name |
| CDEMO-TO-TRANID | PIC X(04) | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Target program name |
| CDEMO-USER-ID | PIC X(08) | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | User type: 'A' admin / 'U' user |
| CDEMO-PGM-CONTEXT | PIC 9(01) | 0 = first entry, 1 = reentry |
| CDEMO-CUST-ID | PIC 9(09) | Current customer context |
| CDEMO-CUST-FNAME | PIC X(25) | Customer first name (context) |
| CDEMO-CUST-MNAME | PIC X(25) | Customer middle name (context) |
| CDEMO-CUST-LNAME | PIC X(25) | Customer last name (context) |
| CDEMO-ACCT-ID | PIC 9(11) | Current account context |
| CDEMO-ACCT-STATUS | PIC X(01) | Account status context |
| CDEMO-CARD-NUM | PIC 9(16) | Current card context |
| CDEMO-LAST-MAP | PIC X(7) | Last BMS map displayed |
| CDEMO-LAST-MAPSET | PIC X(7) | Last BMS mapset |

### CVCRD01Y.cpy — CICS Screen Control Block (46 lines)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| CCARD-AID | PIC X(5) | Attention ID (ENTER/CLEAR/PF keys) |
| CCARD-NEXT-PROG | PIC X(8) | Next program to transfer to |
| CCARD-NEXT-MAPSET | PIC X(7) | Next BMS mapset |
| CCARD-NEXT-MAP | PIC X(7) | Next BMS map |
| CCARD-ERROR-MSG | PIC X(75) | Error message for screen |
| CCARD-RETURN-MSG | PIC X(75) | Return/info message for screen |
| CC-ACCT-ID | PIC X(11) | Account ID filter (screen input) |
| CC-CARD-NUM | PIC X(16) | Card number filter (screen input) |
| CC-CUST-ID | PIC X(09) | Customer ID filter (screen input) |

### COADM02Y.cpy — Admin Menu Options (62 lines)

Defines 6 admin menu options mapping to programs: COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC.

### COMEN02Y.cpy — Main Menu Options (101 lines)

Defines 11 main menu options: Account View/Update, Credit Card List/View/Update, Transaction List/View/Add, Reports, Bill Payment, Pending Authorization View.

### COTTL01Y.cpy — Screen Title (27 lines)

Screen header constants: "AWS Mainframe Modernization" / "CardDemo".

### CSDAT01Y.cpy — Date/Time Working Storage (58 lines)

Current date/time fields in multiple formats: CCYYMMDD, MM/DD/YY, HH:MM:SS, ISO timestamp.

### CSMSG01Y.cpy — Common Messages (24 lines)

Thank-you and invalid-key messages for screen display.

### CSMSG02Y.cpy — Abend Data Area (35 lines)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| ABEND-CODE | PIC X(4) | Abend code |
| ABEND-CULPRIT | PIC X(8) | Program causing abend |
| ABEND-REASON | PIC X(50) | Reason text |
| ABEND-MSG | PIC X(72) | Full abend message |

### CODATECN.cpy — Date Conversion Record (52 lines)

Input/output structure for date format conversion (YYYYMMDD ↔ YYYY-MM-DD).

### CSLKPCDY.cpy — Lookup Code Repository (1318 lines)

Contains 88-level validation tables for:
- North American phone area codes
- US state codes
- US state + ZIP prefix combinations

### CSSETATY.cpy — Set Attribute Macro (30 lines)

COPY REPLACING template for setting BMS field attributes (color/highlight) based on validation flags.

### CSSTRPFY.cpy — Store PFKey Paragraph (85 lines)

Maps EIBAID values to CCARD-AID flags (ENTER, CLEAR, PA1–PA2, PF01–PF12).

### CSUTLDPY.cpy — Date Validation Procedures (375 lines)

Procedure Division copybook with reusable date validation paragraphs: EDIT-DATE-CCYYMMDD, EDIT-YEAR-CCYY, EDIT-MONTH, EDIT-DAY, EDIT-DATE-OF-BIRTH.

### CSUTLDWY.cpy — Date Validation Working Storage (89 lines)

Working storage for date validation: edit fields, binary date, current date, validation flags.

---

## 9. DB2 Copybooks (Sub-Application)

### CSDB2RPY.cpy / CSDB2RWY.cpy — DB2 Read/Write Structures

Located in `app/app-transaction-type-db2/cpy/`. SQL communication areas for transaction type DB2 operations.

---

*Generated: 2026-05-27 | Source: `uc-legacy-modernization-cobol-to-java`*
