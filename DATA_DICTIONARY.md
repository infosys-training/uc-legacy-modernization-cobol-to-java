# DATA DICTIONARY — CardDemo Copybook Estate

> Generated from analysis of `uc-legacy-modernization-cobol-to-java/app/cpy/`

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Unique account identifier | Primary key; must be non-zero |
| `ACCT-ACTIVE-STATUS` | `PIC X(01)` | Alphanumeric (1) | Account active/inactive flag | 'Y' = active, 'N' = inactive |
| `ACCT-CURR-BAL` | `PIC S9(10)V99` | Signed decimal (10.2) | Current account balance | Signed; allows negative (overdrawn) |
| `ACCT-CREDIT-LIMIT` | `PIC S9(10)V99` | Signed decimal (10.2) | Maximum credit limit | Must be positive |
| `ACCT-CASH-CREDIT-LIMIT` | `PIC S9(10)V99` | Signed decimal (10.2) | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| `ACCT-OPEN-DATE` | `PIC X(10)` | Alphanumeric date (10) | Date account was opened | Format: YYYY-MM-DD |
| `ACCT-EXPIRAION-DATE` | `PIC X(10)` | Alphanumeric date (10) | Account expiration date | Format: YYYY-MM-DD; must be > OPEN-DATE |
| `ACCT-REISSUE-DATE` | `PIC X(10)` | Alphanumeric date (10) | Date card was last reissued | Format: YYYY-MM-DD |
| `ACCT-CURR-CYC-CREDIT` | `PIC S9(10)V99` | Signed decimal (10.2) | Current cycle credits (payments received) | Reset each billing cycle |
| `ACCT-CURR-CYC-DEBIT` | `PIC S9(10)V99` | Signed decimal (10.2) | Current cycle debits (charges) | Reset each billing cycle |
| `ACCT-ADDR-ZIP` | `PIC X(10)` | Alphanumeric (10) | Account holder ZIP/postal code | |
| `ACCT-GROUP-ID` | `PIC X(10)` | Alphanumeric (10) | Account group identifier for discount/interest rate lookup | Links to DISCGRP |
| `FILLER` | `PIC X(178)` | Filler | Reserved space to pad to 300-byte record | |

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `CUST-ID` | `PIC 9(09)` | Numeric (9 digits) | Unique customer identifier | Primary key |
| `CUST-FIRST-NAME` | `PIC X(25)` | Alphanumeric (25) | Customer first name | Non-blank for active customers |
| `CUST-MIDDLE-NAME` | `PIC X(25)` | Alphanumeric (25) | Customer middle name | Optional |
| `CUST-LAST-NAME` | `PIC X(25)` | Alphanumeric (25) | Customer last name | Non-blank for active customers |
| `CUST-ADDR-LINE-1` | `PIC X(50)` | Alphanumeric (50) | Street address line 1 | Required |
| `CUST-ADDR-LINE-2` | `PIC X(50)` | Alphanumeric (50) | Street address line 2 | Optional |
| `CUST-ADDR-LINE-3` | `PIC X(50)` | Alphanumeric (50) | Street address line 3 | Optional |
| `CUST-ADDR-STATE-CD` | `PIC X(02)` | Alphanumeric (2) | US state code | Validated against CSLKPCDY state table |
| `CUST-ADDR-COUNTRY-CD` | `PIC X(03)` | Alphanumeric (3) | Country code | |
| `CUST-ADDR-ZIP` | `PIC X(10)` | Alphanumeric (10) | ZIP/postal code | |
| `CUST-PHONE-NUM-1` | `PIC X(15)` | Alphanumeric (15) | Primary phone number | Area code validated against CSLKPCDY |
| `CUST-PHONE-NUM-2` | `PIC X(15)` | Alphanumeric (15) | Secondary phone number | Optional |
| `CUST-SSN` | `PIC 9(09)` | Numeric (9 digits) | Social Security Number | Must be 9 digits |
| `CUST-GOVT-ISSUED-ID` | `PIC X(20)` | Alphanumeric (20) | Government-issued ID (driver's license, passport) | |
| `CUST-DOB-YYYY-MM-DD` | `PIC X(10)` | Alphanumeric date (10) | Date of birth | Format: YYYY-MM-DD; validated via CSUTLDWY |
| `CUST-EFT-ACCOUNT-ID` | `PIC X(10)` | Alphanumeric (10) | Electronic Funds Transfer bank account ID | Used for bill payment |
| `CUST-PRI-CARD-HOLDER-IND` | `PIC X(01)` | Alphanumeric (1) | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| `CUST-FICO-CREDIT-SCORE` | `PIC 9(03)` | Numeric (3 digits) | FICO credit score | Range: 300–850 |
| `FILLER` | `PIC X(168)` | Filler | Reserved space to pad to 500-byte record | |

### 2.2 CUSTREC.cpy — Customer Record (Alternate Layout, RECLN 500)

Identical structure to CVCUS01Y.cpy with minor naming differences:
- `CUST-DOB-YYYYMMDD` instead of `CUST-DOB-YYYY-MM-DD`

Used by the statement generation module (CBSTM03A).

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Credit card number | Primary key; 16-digit card number |
| `CARD-ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Linked account identifier | FK → ACCOUNT-RECORD.ACCT-ID |
| `CARD-CVV-CD` | `PIC 9(03)` | Numeric (3 digits) | Card verification value | 3-digit CVV |
| `CARD-EMBOSSED-NAME` | `PIC X(50)` | Alphanumeric (50) | Name embossed on physical card | |
| `CARD-EXPIRAION-DATE` | `PIC X(10)` | Alphanumeric date (10) | Card expiration date | Format: YYYY-MM-DD |
| `CARD-ACTIVE-STATUS` | `PIC X(01)` | Alphanumeric (1) | Card active/inactive flag | 'Y' = active |
| `FILLER` | `PIC X(59)` | Filler | Reserved space to pad to 150-byte record | |

### 3.2 CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `XREF-CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Card number (primary key) | FK → CARD-RECORD.CARD-NUM |
| `XREF-CUST-ID` | `PIC 9(09)` | Numeric (9 digits) | Customer who owns this card | FK → CUSTOMER-RECORD.CUST-ID |
| `XREF-ACCT-ID` | `PIC 9(11)` | Numeric (11 digits) | Account this card is linked to | FK → ACCOUNT-RECORD.ACCT-ID |
| `FILLER` | `PIC X(14)` | Filler | Reserved space to pad to 50-byte record | |

### 3.3 CVCRD01Y.cpy — Card Work Area (CICS Online)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `CCARD-AID` | `PIC X(5)` | Alphanumeric (5) | Attention Identifier (key pressed) | 88-level values: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| `CCARD-NEXT-PROG` | `PIC X(8)` | Alphanumeric (8) | Next program to transfer control to | Valid CICS program ID |
| `CCARD-NEXT-MAPSET` | `PIC X(7)` | Alphanumeric (7) | Next BMS mapset to display | Valid BMS mapset name |
| `CCARD-NEXT-MAP` | `PIC X(7)` | Alphanumeric (7) | Next BMS map to display | Valid BMS map name |
| `CCARD-ERROR-MSG` | `PIC X(75)` | Alphanumeric (75) | Error message for display | |
| `CCARD-RETURN-MSG` | `PIC X(75)` | Alphanumeric (75) | Return/success message | 88: CCARD-RETURN-MSG-OFF = LOW-VALUES |
| `CC-ACCT-ID` | `PIC X(11)` / `PIC 9(11)` | Alpha/Numeric (11) | Account ID work field with REDEFINES | Allows both character and numeric access |
| `CC-CARD-NUM` | `PIC X(16)` / `PIC 9(16)` | Alpha/Numeric (16) | Card number work field with REDEFINES | Allows both character and numeric access |
| `CC-CUST-ID` | `PIC X(09)` / `PIC 9(9)` | Alpha/Numeric (9) | Customer ID work field with REDEFINES | Allows both character and numeric access |

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `TRAN-ID` | `PIC X(16)` | Alphanumeric (16) | Unique transaction identifier | Primary key |
| `TRAN-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type code | FK → TRAN-TYPE-RECORD.TRAN-TYPE |
| `TRAN-CAT-CD` | `PIC 9(04)` | Numeric (4 digits) | Transaction category code | FK → TRAN-CAT-RECORD |
| `TRAN-SOURCE` | `PIC X(10)` | Alphanumeric (10) | Transaction source (POS, online, etc.) | |
| `TRAN-DESC` | `PIC X(100)` | Alphanumeric (100) | Transaction description | Free-text |
| `TRAN-AMT` | `PIC S9(09)V99` | Signed decimal (9.2) | Transaction amount | Negative for credits/refunds |
| `TRAN-MERCHANT-ID` | `PIC 9(09)` | Numeric (9 digits) | Merchant identifier | |
| `TRAN-MERCHANT-NAME` | `PIC X(50)` | Alphanumeric (50) | Merchant name | |
| `TRAN-MERCHANT-CITY` | `PIC X(50)` | Alphanumeric (50) | Merchant city | |
| `TRAN-MERCHANT-ZIP` | `PIC X(10)` | Alphanumeric (10) | Merchant ZIP code | |
| `TRAN-CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Card used for this transaction | FK → CARD-RECORD.CARD-NUM |
| `TRAN-ORIG-TS` | `PIC X(26)` | Alphanumeric (26) | Timestamp of original transaction | ISO timestamp format |
| `TRAN-PROC-TS` | `PIC X(26)` | Alphanumeric (26) | Timestamp of batch processing | ISO timestamp format |
| `FILLER` | `PIC X(20)` | Filler | Reserved | |

### 4.2 CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same structure as CVTRA05Y but with `DALYTRAN-` prefix. Used for incoming daily transaction files before posting:

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| `DALYTRAN-ID` | `PIC X(16)` | Alphanumeric (16) | Daily transaction identifier |
| `DALYTRAN-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type code |
| `DALYTRAN-CAT-CD` | `PIC 9(04)` | Numeric (4 digits) | Transaction category code |
| `DALYTRAN-SOURCE` | `PIC X(10)` | Alphanumeric (10) | Transaction source |
| `DALYTRAN-DESC` | `PIC X(100)` | Alphanumeric (100) | Transaction description |
| `DALYTRAN-AMT` | `PIC S9(09)V99` | Signed decimal (9.2) | Transaction amount |
| `DALYTRAN-MERCHANT-ID` | `PIC 9(09)` | Numeric (9) | Merchant ID |
| `DALYTRAN-MERCHANT-NAME` | `PIC X(50)` | Alphanumeric (50) | Merchant name |
| `DALYTRAN-MERCHANT-CITY` | `PIC X(50)` | Alphanumeric (50) | Merchant city |
| `DALYTRAN-MERCHANT-ZIP` | `PIC X(10)` | Alphanumeric (10) | Merchant ZIP |
| `DALYTRAN-CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Card number used |
| `DALYTRAN-ORIG-TS` | `PIC X(26)` | Alphanumeric (26) | Original timestamp |
| `DALYTRAN-PROC-TS` | `PIC X(26)` | Alphanumeric (26) | Processing timestamp |
| `FILLER` | `PIC X(20)` | Filler | Reserved |

### 4.3 CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `TRAN-CAT-KEY` | _(group)_ | Group key | Composite key for category balance | |
| `  TRANCAT-ACCT-ID` | `PIC 9(11)` | Numeric (11) | Account this balance belongs to | FK → ACCOUNT-RECORD.ACCT-ID |
| `  TRANCAT-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type code | FK → TRAN-TYPE-RECORD |
| `  TRANCAT-CD` | `PIC 9(04)` | Numeric (4) | Transaction category code | FK → TRAN-CAT-RECORD |
| `TRAN-CAT-BAL` | `PIC S9(09)V99` | Signed decimal (9.2) | Running balance for this category | Updated during batch posting |
| `FILLER` | `PIC X(22)` | Filler | Reserved | |

### 4.4 CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `DIS-GROUP-KEY` | _(group)_ | Group key | Composite key for discount/interest rate | |
| `  DIS-ACCT-GROUP-ID` | `PIC X(10)` | Alphanumeric (10) | Account group identifier | FK → ACCOUNT-RECORD.ACCT-GROUP-ID |
| `  DIS-TRAN-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type | |
| `  DIS-TRAN-CAT-CD` | `PIC 9(04)` | Numeric (4) | Transaction category | |
| `DIS-INT-RATE` | `PIC S9(04)V99` | Signed decimal (4.2) | Interest rate for this group/type combo | Percentage value |
| `FILLER` | `PIC X(28)` | Filler | Reserved | |

### 4.5 CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `TRAN-TYPE` | `PIC X(02)` | Alphanumeric (2) | Transaction type code | Primary key (e.g., 'PR' = purchase, 'CA' = cash advance) |
| `TRAN-TYPE-DESC` | `PIC X(50)` | Alphanumeric (50) | Description of transaction type | |
| `FILLER` | `PIC X(08)` | Filler | Reserved | |

### 4.6 CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `TRAN-CAT-KEY` | _(group)_ | Group key | Composite key | |
| `  TRAN-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type parent code | FK → TRAN-TYPE-RECORD |
| `  TRAN-CAT-CD` | `PIC 9(04)` | Numeric (4) | Category code within type | |
| `TRAN-CAT-TYPE-DESC` | `PIC X(50)` | Alphanumeric (50) | Category description | |
| `FILLER` | `PIC X(04)` | Filler | Reserved | |

### 4.7 CVTRA07Y.cpy — Transaction Report Layout

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| **REPORT-NAME-HEADER** | | | |
| `REPT-SHORT-NAME` | `PIC X(38)` | Alphanumeric | Short report name ('DALYREPT') |
| `REPT-LONG-NAME` | `PIC X(41)` | Alphanumeric | Long report name ('Daily Transaction Report') |
| `REPT-DATE-HEADER` | `PIC X(12)` | Alphanumeric | Date range label |
| `REPT-START-DATE` | `PIC X(10)` | Date | Report start date |
| `REPT-END-DATE` | `PIC X(10)` | Date | Report end date |
| **TRANSACTION-DETAIL-REPORT** | | | |
| `TRAN-REPORT-TRANS-ID` | `PIC X(16)` | Alphanumeric | Transaction ID for report line |
| `TRAN-REPORT-ACCOUNT-ID` | `PIC X(11)` | Alphanumeric | Account ID |
| `TRAN-REPORT-TYPE-CD` | `PIC X(02)` | Alphanumeric | Transaction type code |
| `TRAN-REPORT-TYPE-DESC` | `PIC X(15)` | Alphanumeric | Transaction type description |
| `TRAN-REPORT-CAT-CD` | `PIC 9(04)` | Numeric | Category code |
| `TRAN-REPORT-CAT-DESC` | `PIC X(29)` | Alphanumeric | Category description |
| `TRAN-REPORT-SOURCE` | `PIC X(10)` | Alphanumeric | Transaction source |
| `TRAN-REPORT-AMT` | `PIC -ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Transaction amount (formatted) |
| `REPT-PAGE-TOTAL` | `PIC +ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Page subtotal |
| `REPT-ACCOUNT-TOTAL` | `PIC +ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Account subtotal |
| `REPT-GRAND-TOTAL` | `PIC +ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Grand total |

### 4.8 COSTM01.CPY — Statement Transaction Layout

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| `TRNX-KEY` | _(group)_ | Group key | Composite key for statement lookup |
| `  TRNX-CARD-NUM` | `PIC X(16)` | Alphanumeric (16) | Card number |
| `  TRNX-ID` | `PIC X(16)` | Alphanumeric (16) | Transaction ID |
| `TRNX-TYPE-CD` | `PIC X(02)` | Alphanumeric (2) | Transaction type |
| `TRNX-CAT-CD` | `PIC 9(04)` | Numeric (4) | Category code |
| `TRNX-SOURCE` | `PIC X(10)` | Alphanumeric (10) | Source |
| `TRNX-DESC` | `PIC X(100)` | Alphanumeric (100) | Description |
| `TRNX-AMT` | `PIC S9(09)V99` | Signed decimal (9.2) | Amount |
| `TRNX-MERCHANT-ID` | `PIC 9(09)` | Numeric (9) | Merchant ID |
| `TRNX-MERCHANT-NAME` | `PIC X(50)` | Alphanumeric (50) | Merchant name |
| `TRNX-MERCHANT-CITY` | `PIC X(50)` | Alphanumeric (50) | Merchant city |
| `TRNX-MERCHANT-ZIP` | `PIC X(10)` | Alphanumeric (10) | Merchant ZIP |
| `TRNX-ORIG-TS` | `PIC X(26)` | Alphanumeric (26) | Original timestamp |
| `TRNX-PROC-TS` | `PIC X(26)` | Alphanumeric (26) | Processing timestamp |

---

## 5. Export / Migration Entity

### 5.1 CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

**Header fields (common to all record types):**

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| `EXPORT-REC-TYPE` | `PIC X(1)` | Alphanumeric (1) | Record type discriminator |
| `EXPORT-TIMESTAMP` | `PIC X(26)` | Alphanumeric (26) | Export timestamp (ISO format) |
| `  EXPORT-DATE` | `PIC X(10)` | Date | Export date portion |
| `  EXPORT-TIME` | `PIC X(15)` | Time | Export time portion |
| `EXPORT-SEQUENCE-NUM` | `PIC 9(9) COMP` | Binary (4 bytes) | Sequence number within export |
| `EXPORT-BRANCH-ID` | `PIC X(4)` | Alphanumeric (4) | Originating branch |
| `EXPORT-REGION-CODE` | `PIC X(5)` | Alphanumeric (5) | Region code |

**REDEFINES sections for each entity type:** Customer data, Account data, Transaction data, Card Cross-Reference data, and Card data — each mapping the 460-byte `EXPORT-RECORD-DATA` area to the corresponding entity structure (with some fields using COMP/COMP-3 for storage optimization).

---

## 6. Security Entity

### 6.1 CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| `SEC-USR-ID` | `PIC X(08)` | Alphanumeric (8) | User login ID | Primary key (e.g., ADMIN001, USER0001) |
| `SEC-USR-FNAME` | `PIC X(20)` | Alphanumeric (20) | User first name | |
| `SEC-USR-LNAME` | `PIC X(20)` | Alphanumeric (20) | User last name | |
| `SEC-USR-PWD` | `PIC X(08)` | Alphanumeric (8) | User password | Stored in plaintext |
| `SEC-USR-TYPE` | `PIC X(01)` | Alphanumeric (1) | User type | 'A' = Admin, 'U' = User |
| `SEC-USR-FILLER` | `PIC X(23)` | Filler | Reserved | |

---

## 7. Application Control Structures

### 7.1 COCOM01Y.cpy — Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| `CDEMO-FROM-TRANID` | `PIC X(04)` | Alphanumeric (4) | CICS transaction ID of calling program |
| `CDEMO-FROM-PROGRAM` | `PIC X(08)` | Alphanumeric (8) | Program name that transferred control |
| `CDEMO-TO-TRANID` | `PIC X(04)` | Alphanumeric (4) | Target transaction ID |
| `CDEMO-TO-PROGRAM` | `PIC X(08)` | Alphanumeric (8) | Target program name |
| `CDEMO-USER-ID` | `PIC X(08)` | Alphanumeric (8) | Logged-in user ID |
| `CDEMO-USER-TYPE` | `PIC X(01)` | Alphanumeric (1) | User type (88: 'A' = Admin, 'U' = User) |
| `CDEMO-PGM-CONTEXT` | `PIC 9(01)` | Numeric (1) | Program context (88: 0 = Enter, 1 = Reenter) |
| `CDEMO-CUST-ID` | `PIC 9(09)` | Numeric (9) | Currently selected customer ID |
| `CDEMO-CUST-FNAME` | `PIC X(25)` | Alphanumeric (25) | Customer first name (passed between screens) |
| `CDEMO-CUST-MNAME` | `PIC X(25)` | Alphanumeric (25) | Customer middle name |
| `CDEMO-CUST-LNAME` | `PIC X(25)` | Alphanumeric (25) | Customer last name |
| `CDEMO-ACCT-ID` | `PIC 9(11)` | Numeric (11) | Currently selected account ID |
| `CDEMO-ACCT-STATUS` | `PIC X(01)` | Alphanumeric (1) | Account status flag |
| `CDEMO-CARD-NUM` | `PIC 9(16)` | Numeric (16) | Currently selected card number |
| `CDEMO-LAST-MAP` | `PIC X(7)` | Alphanumeric (7) | Last BMS map displayed |
| `CDEMO-LAST-MAPSET` | `PIC X(7)` | Alphanumeric (7) | Last BMS mapset used |

### 7.2 CODATECN.cpy — Date Conversion Structure

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| `CODATECN-TYPE` | `PIC X` | Alphanumeric (1) | Input format type (88: '1' = YYYYMMDD, '2' = YYYY-MM-DD) |
| `CODATECN-INP-DATE` | `PIC X(20)` | Alphanumeric (20) | Input date (with REDEFINES for both formats) |
| `CODATECN-OUTTYPE` | `PIC X` | Alphanumeric (1) | Output format type (88: '1' = YYYY-MM-DD, '2' = YYYYMMDD) |
| `CODATECN-0UT-DATE` | `PIC X(20)` | Alphanumeric (20) | Output date (with REDEFINES for both formats) |
| `CODATECN-ERROR-MSG` | `PIC X(38)` | Alphanumeric (38) | Error message if conversion fails |

### 7.3 CSDAT01Y.cpy — Date/Time Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| `WS-CURDATE-YEAR` | `PIC 9(04)` | Numeric (4) | Current year (YYYY) |
| `WS-CURDATE-MONTH` | `PIC 9(02)` | Numeric (2) | Current month (MM) |
| `WS-CURDATE-DAY` | `PIC 9(02)` | Numeric (2) | Current day (DD) |
| `WS-CURDATE-N` | `PIC 9(08)` | Numeric (8) | Current date as number (YYYYMMDD) |
| `WS-CURTIME-HOURS` | `PIC 9(02)` | Numeric (2) | Current hours |
| `WS-CURTIME-MINUTE` | `PIC 9(02)` | Numeric (2) | Current minutes |
| `WS-CURTIME-SECOND` | `PIC 9(02)` | Numeric (2) | Current seconds |
| `WS-CURTIME-MILSEC` | `PIC 9(02)` | Numeric (2) | Milliseconds (hundredths) |
| `WS-CURDATE-MM-DD-YY` | _(group)_ | Formatted | Display date (MM/DD/YY) |
| `WS-CURTIME-HH-MM-SS` | _(group)_ | Formatted | Display time (HH:MM:SS) |
| `WS-TIMESTAMP` | _(group)_ | ISO timestamp | Full timestamp (YYYY-MM-DD HH:MM:SS.SSSSSS) |

### 7.4 COTTL01Y.cpy — Screen Title Constants

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| `CCDA-TITLE01` | `PIC X(40)` | Constant | Application title line 1: "AWS Mainframe Modernization" |
| `CCDA-TITLE02` | `PIC X(40)` | Constant | Application title line 2: "CardDemo" |
| `CCDA-THANK-YOU` | `PIC X(40)` | Constant | Sign-off message |

### 7.5 CSMSG01Y.cpy — Common Message Definitions

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| `CCDA-MSG-THANK-YOU` | `PIC X(50)` | Constant | Thank you message for sign-off |
| `CCDA-MSG-INVALID-KEY` | `PIC X(50)` | Constant | Invalid key pressed message |

### 7.6 CSMSG02Y.cpy — Abend Data Structure

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| `ABEND-CODE` | `PIC X(4)` | Alphanumeric (4) | Abend code for error handling |
| `ABEND-CULPRIT` | `PIC X(8)` | Alphanumeric (8) | Program that caused the abend |
| `ABEND-REASON` | `PIC X(50)` | Alphanumeric (50) | Reason for abend |
| `ABEND-MSG` | `PIC X(72)` | Alphanumeric (72) | Formatted abend message |

---

## 8. Menu & Navigation Structures

### 8.1 COMEN02Y.cpy — Main Menu Options

Defines 11 menu options with routing:

| Option # | Menu Item | Target Program | User Type |
|----------|-----------|---------------|-----------|
| 1 | Account View | COACTVWC | U |
| 2 | Account Update | COACTUPC | U |
| 3 | Credit Card List | COCRDLIC | U |
| 4 | Credit Card View | COCRDSLC | U |
| 5 | Credit Card Update | COCRDUPC | U |
| 6 | Transaction List | COTRN00C | U |
| 7 | Transaction View | COTRN01C | U |
| 8 | Transaction Add | COTRN02C | U |
| 9 | Transaction Reports | CORPT00C | U |
| 10 | Bill Payment | COBIL00C | U |
| 11 | Pending Authorization View | COPAUS0C | U |

### 8.2 COADM02Y.cpy — Admin Menu Options

Defines 6 admin menu options:

| Option # | Menu Item | Target Program |
|----------|-----------|---------------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |
| 5 | Transaction Type List/Update (Db2) | COTRTLIC |
| 6 | Transaction Type Maintenance (Db2) | COTRTUPC |

---

## 9. Utility Copybooks

### 9.1 CSLKPCDY.cpy — Lookup Code Repository

Contains embedded lookup tables for validation:
- **US State Codes:** All 50 US states + DC + territories (table of 2-character codes and full names)
- **Phone Area Codes:** Comprehensive table of US area codes mapped to states

Used by COACTUPC for field-level validation during data entry.

### 9.2 CSUTLDWY.cpy — Date Validation Working Storage

Working storage for reusable date validation routines (EDIT-DATE-CCYYMMDD):
- Date component fields (year, month, day)
- Century validation (19xx, 20xx only)
- Month range validation (01–12)
- Day range validation with leap year support
- Date-of-birth validation
- Error flag fields and return messages

### 9.3 CSUTLDPY.cpy — Date Validation Procedure Division

Procedure Division copybook containing reusable paragraphs:
- `EDIT-DATE-CCYYMMDD` — Full date validation
- `EDIT-YEAR-CCYY` — Year/century validation
- `EDIT-MONTH` — Month validation
- `EDIT-DAY` — Day validation with days-per-month table
- `EDIT-DATE-OF-BIRTH` — DOB-specific validation

### 9.4 CSSETATY.cpy — Screen Attribute Settings

Used with COPY ... REPLACING for BMS map attribute manipulation. Sets field attributes (protected, unprotected, bright, dark) on screen fields.

### 9.5 CSSTRPFY.cpy — Strip Function Utility

Provides string stripping/trimming operations for cleaning user input.

### 9.6 UNUSED1Y.cpy — Unused Copybook

Empty/placeholder copybook with no active data definitions.

---

## 10. Summary

| Business Entity | # of Copybooks | # of Fields | Key Copybooks |
|-----------------|---------------|-------------|---------------|
| **Account** | 1 | 12 + filler | CVACT01Y |
| **Customer** | 2 | 17 + filler | CVCUS01Y, CUSTREC |
| **Card** | 3 | 15 + filler | CVACT02Y, CVACT03Y, CVCRD01Y |
| **Transaction** | 8 | 60+ | CVTRA01Y–07Y, COSTM01 |
| **Export** | 1 | 30+ (multi-REDEFINES) | CVEXPORT |
| **Security** | 1 | 6 | CSUSR01Y |
| **Application Control** | 7 | 30+ | COCOM01Y, CODATECN, CSDAT01Y, COTTL01Y, CSMSG01Y, CSMSG02Y, CSLKPCDY |
| **Menu/Navigation** | 2 | 20+ | COMEN02Y, COADM02Y |
| **Utility** | 4 | N/A | CSUTLDWY, CSUTLDPY, CSSETATY, CSSTRPFY |
