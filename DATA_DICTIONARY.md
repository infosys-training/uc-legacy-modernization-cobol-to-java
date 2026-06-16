# DATA DICTIONARY — AWS CardDemo

> Comprehensive field-level documentation of all copybooks defining the CardDemo data model.

---

## 1. Core Business Entities

### 1.1 Account Entity — `CVACT01Y.cpy`

**Record Name:** `ACCOUNT-RECORD` | **Record Length:** 300 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric Display | 11 | Account identifier (primary key) |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Active status flag ('Y'/'N') |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed Decimal | 12 | Current account balance (implied 2 decimal places) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | 12 | Maximum credit limit |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | 12 | Cash advance credit limit |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | 10 | Account opening date (YYYY-MM-DD) |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Account expiration date |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | 10 | Card reissue date |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed Decimal | 12 | Current cycle credits total |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed Decimal | 12 | Current cycle debits total |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | Account holder ZIP code |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Disclosure/interest rate group linkage |
| FILLER | PIC X(178) | — | 178 | Reserved space |

**VSAM File:** `AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS` | **Key:** ACCT-ID (bytes 1-11)

---

### 1.2 Card Entity — `CVACT02Y.cpy`

**Record Name:** `CARD-RECORD` | **Record Length:** 150 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | 16 | Credit card number (primary key) |
| CARD-ACCT-ID | PIC 9(11) | Numeric Display | 11 | Foreign key to Account |
| CARD-CVV-CD | PIC 9(03) | Numeric Display | 3 | Card Verification Value |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | 50 | Name printed on card |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Card expiration date |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Active status ('Y'/'N') |
| FILLER | PIC X(59) | — | 59 | Reserved space |

**VSAM File:** `AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS` | **Key:** CARD-NUM (bytes 1-16)

---

### 1.3 Card-Account Cross-Reference — `CVACT03Y.cpy`

**Record Name:** `CARD-XREF-RECORD` | **Record Length:** 50 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number (primary key) |
| XREF-CUST-ID | PIC 9(09) | Numeric Display | 9 | Customer ID linkage |
| XREF-ACCT-ID | PIC 9(11) | Numeric Display | 11 | Account ID linkage |
| FILLER | PIC X(14) | — | 14 | Reserved space |

**VSAM File:** `AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS` | **Key:** XREF-CARD-NUM (bytes 1-16)

---

### 1.4 Customer Entity — `CVCUS01Y.cpy`

**Record Name:** `CUSTOMER-RECORD` | **Record Length:** 500 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| CUST-ID | PIC 9(09) | Numeric Display | 9 | Customer identifier (primary key) |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | 25 | First name |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | 25 | Middle name |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | 25 | Last name |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | 50 | Address line 1 |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | 50 | Address line 2 |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | 50 | Address line 3 |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | 2 | US state code (validated against CSLKPCDY) |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | 3 | Country code |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | ZIP/postal code |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | 15 | Primary phone number |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | 15 | Secondary phone number |
| CUST-SSN | PIC 9(09) | Numeric Display | 9 | Social Security Number |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | 20 | Government-issued ID |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | 10 | Date of birth |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | 10 | Electronic Funds Transfer account |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | 1 | Primary card holder indicator |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric Display | 3 | FICO credit score (300-850) |
| FILLER | PIC X(168) | — | 168 | Reserved space |

**VSAM File:** `AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS` | **Key:** CUST-ID (bytes 1-9)

---

### 1.5 Transaction Entity — `CVTRA05Y.cpy`

**Record Name:** `TRAN-RECORD` | **Record Length:** 350 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | 16 | Transaction identifier |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code (FK to CVTRA03Y) |
| TRAN-CAT-CD | PIC 9(04) | Numeric Display | 4 | Transaction category code (FK to CVTRA04Y) |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Transaction source identifier |
| TRAN-DESC | PIC X(100) | Alphanumeric | 100 | Transaction description |
| TRAN-AMT | PIC S9(09)V99 | Signed Decimal | 11 | Transaction amount (2 decimal places) |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric Display | 9 | Merchant identifier |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP code |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number used |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | 26 | Origination timestamp |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | 26 | Processing timestamp |
| FILLER | PIC X(20) | — | 20 | Reserved space |

**VSAM File:** `AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS` | **Key:** TRAN-ID (bytes 1-16)

---

### 1.6 Daily Transaction — `CVTRA06Y.cpy`

**Record Name:** `DALYTRAN-RECORD` | **Record Length:** 350 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | 16 | Daily transaction ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric Display | 4 | Category code |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Transaction source |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | 100 | Description |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed Decimal | 11 | Transaction amount |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric Display | 9 | Merchant ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | 26 | Origination timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | 26 | Processing timestamp |
| FILLER | PIC X(20) | — | 20 | Reserved space |

**File:** `AWS.M2.CARDDEMO.DALYTRAN.PS` (Physical Sequential)

---

## 2. Reference/Lookup Entities

### 2.1 Transaction Category Balance — `CVTRA01Y.cpy`

**Record Name:** `TRAN-CAT-BAL-RECORD` | **Record Length:** 50 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| TRAN-CAT-KEY (group) | — | — | 17 | Composite key |
| → TRANCAT-ACCT-ID | PIC 9(11) | Numeric Display | 11 | Account ID |
| → TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type |
| → TRANCAT-CD | PIC 9(04) | Numeric Display | 4 | Category code |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed Decimal | 11 | Running balance for this category |
| FILLER | PIC X(22) | — | 22 | Reserved space |

**VSAM File:** `AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS` | **Key:** TRAN-CAT-KEY (bytes 1-17)

---

### 2.2 Disclosure Group — `CVTRA02Y.cpy`

**Record Name:** `DIS-GROUP-RECORD` | **Record Length:** 50 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| DIS-GROUP-KEY (group) | — | — | 16 | Composite key |
| → DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Account group (links to ACCT-GROUP-ID) |
| → DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code |
| → DIS-TRAN-CAT-CD | PIC 9(04) | Numeric Display | 4 | Transaction category code |
| DIS-INT-RATE | PIC S9(04)V99 | Signed Decimal | 6 | Interest rate for this group/type/category |
| FILLER | PIC X(28) | — | 28 | Reserved space |

**VSAM File:** `AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS` | **Key:** DIS-GROUP-KEY (bytes 1-16)

---

### 2.3 Transaction Type — `CVTRA03Y.cpy`

**Record Name:** `TRAN-TYPE-RECORD` | **Record Length:** 60 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | 2 | Transaction type code (primary key) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Type description |
| FILLER | PIC X(08) | — | 8 | Reserved space |

**VSAM File:** `AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS` | **Key:** TRAN-TYPE (bytes 1-2)

---

### 2.4 Transaction Category — `CVTRA04Y.cpy`

**Record Name:** `TRAN-CAT-RECORD` | **Record Length:** 60 bytes

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|-----------|-----------|------|-------|-----------------|
| TRAN-CAT-KEY (group) | — | — | 6 | Composite key |
| → TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code |
| → TRAN-CAT-CD | PIC 9(04) | Numeric Display | 4 | Category code |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Category description |
| FILLER | PIC X(04) | — | 4 | Reserved space |

**VSAM File:** `AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS` | **Key:** TRAN-CAT-KEY (bytes 1-6)

---

## 3. Report/Export Structures

### 3.1 Transaction Report Layout — `CVTRA07Y.cpy`

| Structure | Fields | Purpose |
|-----------|--------|---------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME, REPT-LONG-NAME, REPT-DATE-HEADER, REPT-START-DATE, REPT-END-DATE | Report header with date range |
| TRANSACTION-DETAIL-REPORT | TRAN-REPORT-TRANS-ID, ACCOUNT-ID, TYPE-CD, TYPE-DESC, CAT-CD, CAT-DESC, SOURCE, AMT | Detail line layout |
| TRANSACTION-HEADER-1/2 | Column headers and separator line | Table formatting |
| REPORT-PAGE-TOTALS | REPT-PAGE-TOTAL (PIC +ZZZ,ZZZ,ZZZ.ZZ) | Page subtotal |
| REPORT-ACCOUNT-TOTALS | REPT-ACCOUNT-TOTAL | Account subtotal |
| REPORT-GRAND-TOTALS | REPT-GRAND-TOTAL | Grand total |

---

### 3.2 Export Record — `CVEXPORT.cpy`

**Record Name:** `EXPORT-RECORD` | **Record Length:** 500 bytes

| Field Name | PIC Clause | Bytes | Business Meaning |
|-----------|-----------|-------|-----------------|
| EXPORT-RECORD-TYPE | PIC X(01) | 1 | Record type indicator (C=Customer, A=Account, T=Transaction, X=Xref, D=Card) |
| EXPORT-RECORD-SEQ | PIC 9(09) COMP | 4 | Sequence number |
| EXPORT-RECORD-DATA | PIC X(495) | 495 | Record data (REDEFINES per type — see sub-structures below) |

**Sub-structures (via REDEFINES):**
- `EXPORT-CUSTOMER-DATA` — Maps CVCUS01Y fields with COMP/COMP-3 optimization
- `EXPORT-ACCOUNT-DATA` — Maps CVACT01Y fields with COMP-3 for monetary values
- `EXPORT-TRANSACTION-DATA` — Maps CVTRA05Y fields
- `EXPORT-CARD-XREF-DATA` — Maps CVACT03Y fields
- `EXPORT-CARD-DATA` — Maps CVACT02Y fields

---

### 3.3 Statement Transaction Layout — `COSTM01.CPY`

**Record Name:** `TRNX-RECORD` | **Record Length:** 350 bytes (keyed by CARD-NUM + TRAN-ID)

| Field Name | PIC Clause | Bytes | Business Meaning |
|-----------|-----------|-------|-----------------|
| TRNX-KEY (group) | — | 32 | Composite key for statement retrieval |
| → TRNX-CARD-NUM | PIC X(16) | 16 | Card number |
| → TRNX-ID | PIC X(16) | 16 | Transaction ID |
| TRNX-TYPE-CD | PIC X(02) | 2 | Transaction type |
| TRNX-CAT-CD | PIC 9(04) | 4 | Category code |
| TRNX-SOURCE | PIC X(10) | 10 | Source |
| TRNX-DESC | PIC X(100) | 100 | Description |
| TRNX-AMT | PIC S9(09)V99 | 11 | Amount |
| TRNX-MERCHANT-ID | PIC 9(09) | 9 | Merchant ID |
| TRNX-MERCHANT-NAME | PIC X(50) | 50 | Merchant name |
| TRNX-MERCHANT-CITY | PIC X(50) | 50 | Merchant city |
| TRNX-MERCHANT-ZIP | PIC X(10) | 10 | Merchant ZIP |
| TRNX-ORIG-TS | PIC X(26) | 26 | Origination timestamp |
| TRNX-PROC-TS | PIC X(26) | 26 | Processing timestamp |
| FILLER | PIC X(20) | 20 | Reserved |

---

## 4. System/Common Copybooks

### 4.1 COMMAREA — `COCOM01Y.cpy`

**Record Name:** `CARDDEMO-COMMAREA` — Inter-program communication area for all CICS programs.

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| CDEMO-FROM-TRANID | PIC X(04) | Source transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Target program name |
| CDEMO-USER-ID | PIC X(08) | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | User type ('A'=Admin, 'U'=User) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Program context (0=Enter, 1=Re-enter) |
| CDEMO-CUST-ID | PIC 9(09) | Customer ID in context |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Customer name fields |
| CDEMO-ACCT-ID | PIC 9(11) | Account ID in context |
| CDEMO-ACCT-STATUS | PIC X(01) | Account status |
| CDEMO-CARD-NUM | PIC 9(16) | Card number in context |
| CDEMO-LAST-MAP/MAPSET | PIC X(7) each | Last displayed map info |

---

### 4.2 User Security — `CSUSR01Y.cpy`

**Record Name:** `SEC-USER-DATA` | **Record Length:** 80 bytes

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| SEC-USR-ID | PIC X(08) | User login ID (primary key) |
| SEC-USR-FNAME | PIC X(20) | First name |
| SEC-USR-LNAME | PIC X(20) | Last name |
| SEC-USR-PWD | PIC X(08) | Password (⚠️ stored in plain text) |
| SEC-USR-TYPE | PIC X(01) | User type ('A'=Admin, 'U'=Regular) |
| SEC-USR-FILLER | PIC X(23) | Reserved |

**VSAM File:** `AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS` | **Key:** SEC-USR-ID

---

### 4.3 Card Work Area — `CVCRD01Y.cpy`

**Record Name:** `CC-WORK-AREAS` — Working storage for card/account/customer navigation in CICS.

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| CCARD-AID | PIC X(5) | Attention Identifier (ENTER, CLEAR, PFK01-12, PA1-2) |
| CCARD-NEXT-PROG | PIC X(8) | Next program to XCTL to |
| CCARD-NEXT-MAPSET | PIC X(7) | Next BMS mapset |
| CCARD-NEXT-MAP | PIC X(7) | Next BMS map |
| CCARD-ERROR-MSG | PIC X(75) | Error message for display |
| CCARD-RETURN-MSG | PIC X(75) | Return/info message |
| CC-ACCT-ID | PIC X(11) | Account ID work field |
| CC-CARD-NUM | PIC X(16) | Card number work field |
| CC-CUST-ID | PIC X(09) | Customer ID work field |

---

### 4.4 Screen Title — `COTTL01Y.cpy`

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| CCDA-TITLE01 | PIC X(40) | Line 1: "AWS Mainframe Modernization" |
| CCDA-TITLE02 | PIC X(40) | Line 2: "CardDemo" |
| CCDA-THANK-YOU | PIC X(40) | Signoff message |

---

### 4.5 Date/Time — `CSDAT01Y.cpy`

Working storage for current date and time formatting.

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| WS-CURDATE (YEAR/MONTH/DAY) | PIC 9(04)/9(02)/9(02) | Current date components |
| WS-CURTIME (HH/MM/SS/MS) | PIC 9(02) each | Current time components |
| WS-CURDATE-MM-DD-YY | Formatted | Display format MM/DD/YY |
| WS-CURTIME-HH-MM-SS | Formatted | Display format HH:MM:SS |
| WS-TIMESTAMP | Formatted | Full timestamp YYYY-MM-DD HH:MM:SS.ffffff |

---

### 4.6 Common Messages — `CSMSG01Y.cpy`

| Field Name | Value | Purpose |
|-----------|-------|---------|
| CCDA-MSG-THANK-YOU | "Thank you for using CardDemo application..." | Signoff message |
| CCDA-MSG-INVALID-KEY | "Invalid key pressed. Please see below..." | Error feedback |

---

### 4.7 Abend Data — `CSMSG02Y.cpy`

**Record Name:** `ABEND-DATA` — Work areas for abnormal termination handling.

| Field Name | PIC Clause | Purpose |
|-----------|-----------|---------|
| ABEND-CODE | PIC X(4) | Abend code |
| ABEND-CULPRIT | PIC X(8) | Program causing abend |
| ABEND-REASON | PIC X(50) | Reason text |
| ABEND-MSG | PIC X(72) | Formatted abend message |

---

### 4.8 Menu Definitions

#### Admin Menu — `COADM02Y.cpy`

**Record Name:** `CARDDEMO-ADMIN-MENU-OPTIONS` (6 options)

| Option # | Name | Target Program |
|----------|------|----------------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |
| 5 | Transaction Type List/Update (Db2) | COTRTLIC |
| 6 | Transaction Type Maintenance (Db2) | COTRTUPC |

#### Main Menu — `COMEN02Y.cpy`

**Record Name:** `CARDDEMO-MAIN-MENU-OPTIONS` (11 options)

| Option # | Name | Target Program | User Type |
|----------|------|----------------|-----------|
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

---

### 4.9 Validation Lookup — `CSLKPCDY.cpy`

Contains validation tables for:
- **US State Codes** — All 50 state abbreviations + DC
- **ZIP Code Prefixes** — Valid ZIP prefix ranges by state
- **Phone Area Codes** — NANPA area code validation list

Used by: COACTUPC (account/customer update validation)

---

### 4.10 String Parsing — `CSSTRPFY.cpy`

Utility copybook for string manipulation: field stripping, trimming, and formatting operations used across card/account display programs.

---

### 4.11 Screen Attribute Setting — `CSSETATY.cpy`

COPY REPLACING macro for setting BMS screen field attributes (color, intensity, protection). Used 3× in COACTUPC with different field prefixes to manage attribute arrays.

---

### 4.12 Date Validation Utilities

- **`CSUTLDPY.cpy`** — Parameter definitions for date validation utility (CSUTLDTC)
- **`CSUTLDWY.cpy`** — Working storage for date calculation operations
- **`CODATECN.cpy`** — Date formatting constants and conversion tables

---

### 4.13 Unused/Legacy — `UNUSED1Y.cpy`

**Record Name:** `UNUSED-DATA` — Legacy user record layout (same structure as CSUSR01Y). Retained for backward compatibility.

---

### 4.14 Customer Record (Alternate) — `CUSTREC.cpy`

Alternate customer record layout with identical structure to CVCUS01Y but with slightly different field naming (CUST-DOB-YYYYMMDD vs CUST-DOB-YYYY-MM-DD). Used by some programs for compatibility.

---

## 5. Sub-Application Copybooks

### 5.1 Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cpy/`

#### IMS Summary Segment — `CIPAUSMY.cpy`

**Segment:** Pending Authorization Summary (IMS root segment)

| Field Name | PIC Clause | Type | Business Meaning |
|-----------|-----------|------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed Decimal | Account ID |
| PA-CUST-ID | PIC 9(09) | Numeric Display | Customer ID |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric | Authorization status |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Array | Account status history (5 entries) |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed Decimal | Credit limit |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed Decimal | Cash advance limit |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed Decimal | Credit balance |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed Decimal | Cash balance |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Approved authorization count |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Declined authorization count |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed Decimal | Total approved amount |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed Decimal | Total declined amount |

#### IMS Detail Segment — `CIPAUDTY.cpy`

**Segment:** Pending Authorization Detail (IMS dependent segment)

| Field Name | PIC Clause | Type | Business Meaning |
|-----------|-----------|------|-----------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed Decimal | Authorization date (compressed) |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed Decimal | Authorization time (compressed) |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original date (YYMMDD) |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original time (HHMMSS) |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type code |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry (YYMM) |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message source |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID code |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code ('00'=Approved) |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed Decimal | Requested transaction amount |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed Decimal | Approved amount |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | Merchant category (MCC) |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |

#### Authorization Request (MQ message) — `CCPAURQY.cpy`

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Card expiry |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Message source |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Transaction amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | MCC |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Acquirer country |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Transaction ID |

#### Authorization Response (MQ message) — `CCPAURLY.cpy`

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Authorization ID |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Response reason |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Approved amount |

#### Error Log — `CCPAUERY.cpy`

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| ERR-DATE | PIC X(06) | Error date |
| ERR-TIME | PIC X(06) | Error time |
| ERR-APPLICATION | PIC X(08) | Application name |
| ERR-PROGRAM | PIC X(08) | Program name |
| ERR-LOCATION | PIC X(04) | Code location |
| ERR-LEVEL | PIC X(01) | Severity (L/I/W/C) |
| ERR-SUBSYSTEM | PIC X(01) | Subsystem (A/C/I/D/M/F) |
| ERR-CODE-1/2 | PIC X(09) each | Error codes |
| ERR-MESSAGE | PIC X(50) | Error message text |
| ERR-EVENT-KEY | PIC X(20) | Event correlation key |

#### IMS Function Codes — `IMSFUNCS.cpy`

Constants for IMS DL/I function codes (GU, GN, GNP, ISRT, REPL, DLET, etc.)

#### PCB Definitions — `PADFLPCB.CPY`, `PASFLPCB.CPY`, `PAUTBPCB.CPY`

Program Communication Blocks for IMS database access:
- PADFLPCB — PCB for authorization detail segment
- PASFLPCB — PCB for authorization summary segment (sequential)
- PAUTBPCB — PCB for authorization table segment

---

### 5.2 Transaction Type (DB2) — `app/app-transaction-type-db2/cpy/`

#### DB2 Procedures — `CSDB2RPY.cpy`

Common DB2 procedures including:
- `9998-PRIMING-QUERY` — Connectivity verification (`SELECT 1 FROM SYSIBM.SYSDUMMY1`)
- `9999-FORMAT-DB2-MESSAGE` — Error message formatting via DSNTIAC utility

#### DB2 Working Storage — `CSDB2RWY.cpy`

Working storage variables for DB2 operations: SQLCODE display fields, DSNTIAC formatted output areas, cursor state management.

---

## 6. Entity Relationship Diagram

```
┌─────────────────┐         ┌──────────────────────┐         ┌─────────────────┐
│   CUSTOMER      │         │   CARD-XREF          │         │    ACCOUNT      │
│   (CVCUS01Y)    │◄───────►│   (CVACT03Y)         │◄───────►│   (CVACT01Y)    │
│   Key: CUST-ID  │  1:N    │   Key: XREF-CARD-NUM │  N:1    │   Key: ACCT-ID  │
│   RECLN: 500    │         │   RECLN: 50          │         │   RECLN: 300    │
└─────────────────┘         └──────────┬───────────┘         └────────┬────────┘
                                       │                              │
                                       │ 1:1                          │ via GROUP-ID
                                       ▼                              ▼
                            ┌──────────────────────┐         ┌─────────────────┐
                            │      CARD            │         │  DISCLOSURE GRP │
                            │   (CVACT02Y)         │         │  (CVTRA02Y)     │
                            │   Key: CARD-NUM      │         │  Key: GRP+TYPE  │
                            │   RECLN: 150         │         │  RECLN: 50      │
                            └──────────┬───────────┘         └─────────────────┘
                                       │
                                       │ via CARD-NUM
                                       ▼
                            ┌──────────────────────┐
                            │    TRANSACTION       │
                            │   (CVTRA05Y)         │────────►  TRAN-TYPE (CVTRA03Y)
                            │   Key: TRAN-ID       │────────►  TRAN-CAT  (CVTRA04Y)
                            │   RECLN: 350         │
                            └──────────┬───────────┘
                                       │
                                       │ aggregated by
                                       ▼
                            ┌──────────────────────┐
                            │  TRAN-CAT-BALANCE    │
                            │   (CVTRA01Y)         │
                            │  Key: ACCT+TYPE+CAT  │
                            │   RECLN: 50          │
                            └──────────────────────┘
```
