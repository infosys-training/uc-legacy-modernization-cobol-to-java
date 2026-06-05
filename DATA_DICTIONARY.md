# DATA DICTIONARY — CardDemo COBOL Estate

> **Generated:** 2026-06-05 | **Repository:** `uc-legacy-modernization-cobol-to-java`
> **Total Copybooks:** 61 (30 in `app/cpy/`, 17 in `app/cpy-bms/`, 14 in sub-app directories)

---

## 1. Business Entity Copybooks — `app/cpy/`

Fields are organized by business entity. PIC clauses, inferred data types, business meaning, and validation rules are provided.

---

### 1.1 Account Entity — `CVACT01Y.cpy` (Record Length: 300 bytes)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | ACCT-ID | PIC 9(11) | Numeric (unsigned) | 11 | Primary key — unique account identifier | Must be numeric, non-zero |
| 2 | ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Account active flag | 'Y' = active, 'N' = inactive |
| 3 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | 12 | Current account balance | Signed; implied 2 decimal places; use BigDecimal |
| 4 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | 12 | Credit limit on account | Must be ≥ 0; validated in COACTUPC |
| 5 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | 12 | Cash advance credit limit | Must be ≥ 0; ≤ ACCT-CREDIT-LIMIT |
| 6 | ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (date) | 10 | Date account was opened | Format: YYYY-MM-DD; validated via CSUTLDPY (century 19/20 check) |
| 7 | ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (date) | 10 | Account expiration date | Format: YYYY-MM-DD; must be > ACCT-OPEN-DATE |
| 8 | ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (date) | 10 | Last card reissue date | Format: YYYY-MM-DD |
| 9 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | 12 | Current cycle credit total | Running total of credits this billing cycle |
| 10 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | 12 | Current cycle debit total | Running total of debits this billing cycle |
| 11 | ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | Account ZIP code | Validated against state-ZIP prefix table in CSLKPCDY |
| 12 | ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Disclosure/interest rate group | Links to DISCGRP file for interest rate calculation |
| 13 | FILLER | PIC X(178) | — | 178 | Reserved/unused padding | — |

---

### 1.2 Card Entity — `CVACT02Y.cpy` (Record Length: 150 bytes)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | CARD-NUM | PIC X(16) | Alphanumeric | 16 | Credit card number (primary key) | 16-character card number |
| 2 | CARD-ACCT-ID | PIC 9(11) | Numeric (unsigned) | 11 | Foreign key to Account (CVACT01Y) | Must match valid ACCT-ID |
| 3 | CARD-CVV-CD | PIC 9(03) | Numeric (unsigned) | 3 | Card CVV security code | 3-digit numeric |
| 4 | CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | 50 | Name embossed on physical card | — |
| 5 | CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (date) | 10 | Card expiration date | Format: YYYY-MM-DD |
| 6 | CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Card active flag | 'Y' = active, 'N' = inactive |
| 7 | FILLER | PIC X(59) | — | 59 | Reserved/unused padding | — |

---

### 1.3 Customer Entity — `CVCUS01Y.cpy` (Record Length: 500 bytes)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | CUST-ID | PIC 9(09) | Numeric (unsigned) | 9 | Primary key — customer identifier | Must be numeric, non-zero |
| 2 | CUST-FIRST-NAME | PIC X(25) | Alphanumeric | 25 | Customer first name | Alphabetic required (validated in COACTUPC) |
| 3 | CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | 25 | Customer middle name | Alphabetic optional |
| 4 | CUST-LAST-NAME | PIC X(25) | Alphanumeric | 25 | Customer last name | Alphabetic required |
| 5 | CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | 50 | Street address line 1 | Mandatory field |
| 6 | CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | 50 | Street address line 2 | Optional |
| 7 | CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | 50 | Street address line 3 | Optional |
| 8 | CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | 2 | US state code | Validated against 50 US state codes in CSLKPCDY |
| 9 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | 3 | Country code | — |
| 10 | CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | ZIP/postal code | First 2 digits validated against state-ZIP prefix table |
| 11 | CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | 15 | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| 12 | CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | 15 | Secondary phone number | Area code validated against NANPA list |
| 13 | CUST-SSN | PIC 9(09) | Numeric (unsigned) | 9 | Social Security Number | 9-digit numeric; validated format (XXX-XX-XXXX logic) in COACTUPC |
| 14 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | 20 | Government-issued ID number | — |
| 15 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (date) | 10 | Date of birth | Format: YYYY-MM-DD; validated via EDIT-DATE-OF-BIRTH in CSUTLDPY |
| 16 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | 10 | Electronic funds transfer account | — |
| 17 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | 1 | Primary cardholder indicator | 'Y'/'N' |
| 18 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (unsigned) | 3 | FICO credit score | Range: 300–850; validated in COACTUPC |
| 19 | FILLER | PIC X(168) | — | 168 | Reserved/unused padding | — |

---

### 1.4 Card-Account Cross-Reference — `CVACT03Y.cpy` (Record Length: 50 bytes)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | XREF-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number (primary key) | Links to CVACT02Y.CARD-NUM |
| 2 | XREF-CUST-ID | PIC 9(09) | Numeric (unsigned) | 9 | Customer identifier | Foreign key to CVCUS01Y.CUST-ID |
| 3 | XREF-ACCT-ID | PIC 9(11) | Numeric (unsigned) | 11 | Account identifier | Foreign key to CVACT01Y.ACCT-ID |
| 4 | FILLER | PIC X(14) | — | 14 | Reserved/unused padding | — |

---

### 1.5 Transaction Entity — `CVTRA05Y.cpy` (Record Length: 350 bytes)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | TRAN-ID | PIC X(16) | Alphanumeric | 16 | Transaction identifier (primary key) | Unique transaction ID |
| 2 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | Links to CVTRA03Y.TRAN-TYPE |
| 3 | TRAN-CAT-CD | PIC 9(04) | Numeric (unsigned) | 4 | Transaction category code | Links to CVTRA04Y.TRAN-CAT-CD |
| 4 | TRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Transaction source system | — |
| 5 | TRAN-DESC | PIC X(100) | Alphanumeric | 100 | Transaction description | — |
| 6 | TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | 11 | Transaction amount | Signed; implied 2 decimal places; use BigDecimal |
| 7 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric (unsigned) | 9 | Merchant identifier | — |
| 8 | TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name | — |
| 9 | TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city | — |
| 10 | TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP code | — |
| 11 | TRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number used for transaction | Links to CVACT02Y.CARD-NUM |
| 12 | TRAN-ORIG-TS | PIC X(26) | Alphanumeric (timestamp) | 26 | Transaction origination timestamp | ISO-like timestamp |
| 13 | TRAN-PROC-TS | PIC X(26) | Alphanumeric (timestamp) | 26 | Transaction processing timestamp | Set during posting by CBTRN02C |
| 14 | FILLER | PIC X(20) | — | 20 | Reserved/unused padding | — |

---

### 1.6 Daily Transaction — `CVTRA06Y.cpy` (Record Length: 350 bytes)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | DALYTRAN-ID | PIC X(16) | Alphanumeric | 16 | Daily transaction identifier | — |
| 2 | DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | — |
| 3 | DALYTRAN-CAT-CD | PIC 9(04) | Numeric (unsigned) | 4 | Transaction category code | — |
| 4 | DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Transaction source | — |
| 5 | DALYTRAN-DESC | PIC X(100) | Alphanumeric | 100 | Transaction description | — |
| 6 | DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | 11 | Transaction amount | Signed; implied 2 decimal places |
| 7 | DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric (unsigned) | 9 | Merchant identifier | — |
| 8 | DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name | — |
| 9 | DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city | — |
| 10 | DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP code | — |
| 11 | DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number | — |
| 12 | DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric (timestamp) | 26 | Origination timestamp | — |
| 13 | DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric (timestamp) | 26 | Processing timestamp | — |
| 14 | FILLER | PIC X(20) | — | 20 | Reserved padding | — |

---

### 1.7 Transaction Type — `CVTRA03Y.cpy` (10 lines)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | TRAN-TYPE | PIC X(02) | Alphanumeric | 2 | Transaction type code (primary key) | — |
| 2 | TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Transaction type description | — |

---

### 1.8 Transaction Category — `CVTRA04Y.cpy` (12 lines)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | TRAN-CAT-CD | PIC 9(04) | Numeric (unsigned) | 4 | Category code (primary key) | — |
| 2 | TRAN-CAT-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Parent transaction type code | Foreign key to CVTRA03Y.TRAN-TYPE |
| 3 | TRAN-CAT-DESC | PIC X(50) | Alphanumeric | 50 | Category description | — |

---

### 1.9 Transaction Category Balance — `CVTRA01Y.cpy` (13 lines)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | 11 | Category balance amount | Running balance per category |
| 2 | (Additional fields) | — | — | — | Category balance record structure | Used by CBACT04C for interest calculation |

---

### 1.10 Disclosure/Interest Rate Group — `CVTRA02Y.cpy` (13 lines)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | DIS-GROUP-ID | — | Alphanumeric | — | Disclosure group identifier | Links to ACCT-GROUP-ID in CVACT01Y |
| 2 | DIS-INT-RATE | — | Decimal | — | Interest rate for the group | Used by CBACT04C for interest calculation |

---

### 1.11 User Security — `CSUSR01Y.cpy` (26 lines)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|---|-----------|------------|-----------|-------|-----------------|-----------------|
| 1 | SEC-USR-ID | PIC X(08) | Alphanumeric | 8 | User ID (primary key) | — |
| 2 | SEC-USR-FNAME | PIC X(20) | Alphanumeric | 20 | User first name | — |
| 3 | SEC-USR-LNAME | PIC X(20) | Alphanumeric | 20 | User last name | — |
| 4 | SEC-USR-PWD | PIC X(08) | Alphanumeric | 8 | User password | **RISK: Stored in plain text — must hash in migration** |
| 5 | SEC-USR-TYPE | PIC X(01) | Alphanumeric | 1 | User type | 'A' = Admin, 'U' = Regular User |
| 6 | SEC-USR-UPD-DT | PIC X(08) | Alphanumeric (date) | 8 | Last update date | — |
| 7 | SEC-USR-UPD-TM | PIC X(06) | Alphanumeric (time) | 6 | Last update time | — |

---

### 1.12 Customer Record (Alternate) — `CUSTREC.cpy` (26 lines)

Alternate customer record layout used by CBSTM03A for statement generation.

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|---|-----------|------------|-----------|-------|-----------------|
| 1 | CUST-ID-N | PIC 9(09) | Numeric | 9 | Customer ID |
| 2 | CUST-NAME | Group | — | — | Full customer name (first/middle/last) |
| 3 | CUST-ADDR | Group | — | — | Full address (3 lines + state + country + ZIP) |
| 4 | CUST-PHONE | Group | — | — | Phone numbers |

---

### 1.13 Export Record — `CVEXPORT.cpy` (103 lines)

Consolidated export/import record used by CBEXPORT and CBIMPORT.

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|---|-----------|------------|-----------|-------|-----------------|
| 1 | EXPORT-RECORD-TYPE | PIC X(01) | Alphanumeric | 1 | Record type indicator ('C'=customer, 'A'=account, 'X'=xref, 'T'=transaction, 'R'=card) |
| 2 | EXPORT-CUST-RECORD | Group (REDEFINES) | — | — | Customer data when type='C' |
| 3 | EXPORT-ACCT-RECORD | Group (REDEFINES) | — | — | Account data when type='A' |
| 4 | EXPORT-XREF-RECORD | Group (REDEFINES) | — | — | Cross-ref data when type='X' |
| 5 | EXPORT-TRAN-RECORD | Group (REDEFINES) | — | — | Transaction data when type='T' |
| 6 | EXPORT-CARD-RECORD | Group (REDEFINES) | — | — | Card data when type='R' |

---

### 1.14 Unused/Reserved — `UNUSED1Y.cpy` (10 lines)

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|---|-----------|------------|-----------|-------|-----------------|
| 1 | UNUSED-ID | PIC X(08) | Alphanumeric | 8 | Placeholder ID |
| 2 | UNUSED-FNAME | PIC X(20) | Alphanumeric | 20 | Placeholder first name |
| 3 | UNUSED-LNAME | PIC X(20) | Alphanumeric | 20 | Placeholder last name |
| 4 | UNUSED-PWD | PIC X(08) | Alphanumeric | 8 | Placeholder password |
| 5 | UNUSED-TYPE | PIC X(01) | Alphanumeric | 1 | Placeholder type |

---

## 2. Application Infrastructure Copybooks

### 2.1 COMMAREA — `COCOM01Y.cpy` (47 lines)

Inter-program communication area passed via CICS COMMAREA between all online programs.

| # | Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|---|-----------|------------|-----------|-------|-----------------|
| 1 | CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | 4 | Source CICS transaction ID |
| 2 | CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | 8 | Source program name |
| 3 | CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | 4 | Target CICS transaction ID |
| 4 | CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | 8 | Target program name |
| 5 | CDEMO-USER-ID | PIC X(08) | Alphanumeric | 8 | Authenticated user ID |
| 6 | CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 1 | User type: 'A' (admin) or 'U' (user) |
| 7 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 1 | Program context: 0=first entry, 1=re-entry |
| 8 | CDEMO-CUST-ID | PIC 9(09) | Numeric | 9 | Selected customer ID |
| 9 | CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alphanumeric | 75 | Customer name fields |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | 11 | Selected account ID |
| 11 | CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | 1 | Account status |
| 12 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | 16 | Selected card number |
| 13 | CDEMO-LAST-MAP / LAST-MAPSET | PIC X(7) each | Alphanumeric | 14 | Last BMS map/mapset displayed |

---

### 2.2 Admin Menu Options — `COADM02Y.cpy` (62 lines)

Table-driven menu routing for admin functions. Contains 6 options with program name mappings.

| Option | Label | Target Program |
|--------|-------|---------------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |
| 5 | Transaction Type List (DB2) | COTRTLIC |
| 6 | Transaction Type Update (DB2) | COTRTUPC |

---

### 2.3 Main Menu Options — `COMEN02Y.cpy` (101 lines)

Table-driven menu routing for user functions. Contains 12 options with program name and transaction ID mappings.

| Option | Label | Target Program | Transaction ID |
|--------|-------|---------------|----------------|
| 1 | View Account | COACTVWC | CA00 |
| 2 | Update Account | COACTUPC | CA00 |
| 3 | View Statement | — | — |
| 4 | View Transaction | COTRN00C | CT00 |
| 5 | Add Transaction | COTRN02C | CT00 |
| 6 | Credit Card List | COCRDLIC | CC00 |
| 7 | Pay Bill | COBIL00C | CB00 |
| 8 | Batch Admin | — | — |
| 9 | Submit Report | CORPT00C | CR00 |
| 10 | Pending Authorizations | COPAUS0C | CP00 |
| 11 | — | — | — |
| 12 | Sign Off | COSGN00C | CCRD |

---

### 2.4 Screen Title — `COTTL01Y.cpy` (27 lines)

| Field | PIC Clause | Business Meaning |
|-------|------------|-----------------|
| CCDA-TITLE01 | PIC X(40) | Application title line 1 |
| CCDA-TITLE02 | PIC X(40) | Application title line 2 |
| CCDA-PGMNAME | PIC X(08) | Current program name display |
| CCDA-TRANID | PIC X(04) | Current transaction ID display |

---

### 2.5 Date/Time — `CSDAT01Y.cpy` (58 lines)

| Field | PIC Clause | Business Meaning |
|-------|------------|-----------------|
| WS-CURDATE-DATA | Group | Current date components (YYYY, MM, DD, HH, MM, SS) |
| WS-CURTIME-DATA | Group | Current time display fields |

---

### 2.6 Messages — `CSMSG01Y.cpy` (24 lines) & `CSMSG02Y.cpy` (35 lines)

| Field | PIC Clause | Business Meaning |
|-------|------------|-----------------|
| WS-MESSAGE | PIC X(78) | Primary screen message |
| WS-INFO-MSG | PIC X(78) | Informational message |
| WS-ERR-MSG | PIC X(78) | Error message |
| WS-ERR-FLG | PIC X(01) | Error flag: 'Y'/'N' |

---

### 2.7 Date Conversion Utility — `CODATECN.cpy` (52 lines)

Used by CBACT01C for date formatting via assembler COBDATFT call.

| Field | PIC Clause | Business Meaning |
|-------|------------|-----------------|
| CODATECN-DATE-IN | PIC X(08) | Input date (CCYYMMDD) |
| CODATECN-DATE-OUT | PIC X(10) | Output formatted date |

---

## 3. Validation & Lookup Copybooks

### 3.1 Lookup Code Repository — `CSLKPCDY.cpy` (1,318 lines)

Comprehensive validation reference data:

| Section | Lines | Contents | Validation Use |
|---------|-------|----------|---------------|
| NANPA Phone Area Codes | ~700 | 88-level VALUES for all valid North American area codes (201–999) | COACTUPC validates CUST-PHONE-NUM-1/2 area codes |
| US State Codes | ~60 | 88-level VALUES for all 50 US states + DC + territories | COACTUPC validates CUST-ADDR-STATE-CD |
| State-ZIP Prefix Map | ~550 | Maps state code to valid 2-digit ZIP prefixes | COACTUPC validates CUST-ADDR-ZIP against state |

---

### 3.2 Date Validation — `CSUTLDPY.cpy` (375 lines, Procedure Division copybook)

Reusable date validation paragraphs. Included via COPY in COACTUPC and COTRTUPC.

| Paragraph | Purpose | Validation Rules |
|-----------|---------|-----------------|
| EDIT-DATE-CCYYMMDD | Validate complete date | Calls sub-paragraphs below |
| EDIT-YEAR-CCYY | Validate year | Numeric; century must be 19 or 20 |
| EDIT-MONTH | Validate month | Range: 01–12 |
| EDIT-DAY | Validate day | Range: 01–28/29/30/31 based on month + leap year |
| EDIT-DATE-OF-BIRTH | Validate DOB | Must be in the past; reasonable age range |

---

### 3.3 Date Validation Working Storage — `CSUTLDWY.cpy` (89 lines)

Working storage variables for date validation:

| Field | PIC Clause | Business Meaning |
|-------|------------|-----------------|
| WS-EDIT-DATE-CCYYMMDD | PIC X(08) | Date being validated |
| WS-EDIT-DATE-CCYY | PIC 9(04) | Year component |
| WS-EDIT-DATE-MM | PIC 9(02) | Month component |
| WS-EDIT-DATE-DD | PIC 9(02) | Day component |
| WS-EDIT-DATE-IS-VALID / IS-INVALID | 88-level | Validation result flags |
| FLG-YEAR-OK / FLG-MONTH-OK / FLG-DAY-OK | 88-level | Component-level flags |
| WS-EDIT-VARIABLE-NAME | PIC X(25) | Name of field being validated (for error messages) |

---

### 3.4 Screen Attribute Macro — `CSSETATY.cpy` (30 lines)

COPY REPLACING template for setting BMS screen field attributes. Uses macro-style variables:
- `(TESTVAR1)` — field validation flag name
- `(SCRNVAR2)` — BMS screen field name
- `(MAPNAME3)` — BMS map name

Sets field color to RED and adds '*' marker if validation fails. Used 3× in COACTUPC via COPY REPLACING.

---

### 3.5 PFKey Mapping — `CSSTRPFY.cpy` (85 lines, Procedure Division copybook)

Maps CICS EIBAID values to CCARD-AID-xxx flags (defined in CVCRD01Y). Used by all online programs to translate terminal key presses (PF1–PF24, Enter, Clear, PA1, PA2).

---

### 3.6 AID/PFKey Working Storage — `CVCRD01Y.cpy` (46 lines)

Working storage for key-press handling:

| Field | PIC Clause | Business Meaning |
|-------|------------|-----------------|
| CCARD-AID | PIC X(5) | Current AID key identifier |
| 88 CCARD-AID-ENTER | VALUE 'ENTER' | Enter key pressed |
| 88 CCARD-AID-CLEAR | VALUE 'CLEAR' | Clear key pressed |
| 88 CCARD-AID-PFK01–PFK12 | VALUE 'PFK01'–'PFK12' | PF key 1–12 pressed |

---

### 3.7 Date Conversion Utility Parameters — `CSUTLDPY.cpy` (used with `CSUTLDTC.cbl`)

Pass-through parameters for CSUTLDTC date conversion utility (calls LE CEEDAYS).

---

### 3.8 Statement Generation — `COSTM01.CPY` (38 lines)

Working storage for CBSTM03A statement generation program:

| Field | Purpose |
|-------|---------|
| WS-M03B-AREA | Communication area between CBSTM03A and CBSTM03B submodule |
| Statement format fields | Layout for text and HTML statement output |

---

### 3.9 Transaction Report — `CVTRA07Y.cpy` (73 lines)

Report layout structures for CBTRN03C daily transaction report:

| Structure | Purpose |
|-----------|---------|
| REPORT-NAME-HEADER | Report title, date range display |
| TRANSACTION-DETAIL-REPORT | Detail line: trans ID, account, type, category, source, amount |
| TRANSACTION-HEADER-1/2 | Column headers and separator |
| REPORT-PAGE/ACCOUNT/GRAND-TOTALS | Running totals with formatted amounts (PIC +ZZZ,ZZZ,ZZZ.ZZ) |

---

## 4. Sub-Application Copybooks

### 4.1 Authorization (IMS) — `app/app-authorization-ims-db2-mq/cpy/`

| Copybook | Lines | Purpose | Key Fields |
|----------|-------|---------|------------|
| CIPAUSMY.cpy | — | IMS auth summary segment | Auth summary record (card, date, amount, status) |
| CIPAUDTY.cpy | — | IMS auth detail segment | Auth detail record (transaction-level data) |
| CCPAURQY.cpy | — | MQ auth request message | PA-RQ-CARD-NUM, PA-RQ-AUTH-TYPE, PA-RQ-MERCHANT-*, PA-RQ-AUTH-AMT |
| CCPAURLY.cpy | — | MQ auth reply message | PA-RL-CARD-NUM, PA-RL-AUTH-RESP-CODE, PA-RL-APPROVED-AMT |
| CCPAUERY.cpy | — | Error log record | ERR-DATE/TIME/APPLICATION/PROGRAM/CODE/MESSAGE, subsystem flags (88-level: App/CICS/IMS/DB2/MQ/File) |
| IMSFUNCS.cpy | — | IMS DL/I function codes | Constants for GU, GN, GNP, ISRT, REPL, DLET, etc. |
| PAUTBPCB.CPY | — | IMS PCB mask (primary) | PCB status, segment level, key feedback |
| PADFLPCB.CPY | — | IMS PCB mask (detail segment) | PCB for detail segment navigation |
| PASFLPCB.CPY | — | IMS PCB mask (summary segment) | PCB for summary segment navigation |

### 4.2 Transaction Type (DB2) — `app/app-transaction-type-db2/cpy/`

| Copybook | Lines | Purpose | Key Fields |
|----------|-------|---------|------------|
| CSDB2RPY.cpy | — | DB2 read parameters | SQL communication area fields |
| CSDB2RWY.cpy | — | DB2 read/write parameters | SQL communication area fields |

### 4.3 BMS Copybooks — `app/cpy-bms/` (17 files) + sub-app `cpy-bms/`

Generated from BMS map assemblies. Each `.CPY` file contains the symbolic map layout (field names, lengths, attributes) for the corresponding BMS `.bms` file. Not listed individually as they are auto-generated from BMS source.

---

## 5. Entity Relationship Summary

```
Customer (CVCUS01Y)                    Account (CVACT01Y)
  CUST-ID [PK] ───1:N──► XREF-CUST-ID    ACCT-ID [PK] ◄──N:1── XREF-ACCT-ID
                           │                                        │
                    Card-XREF (CVACT03Y)                           │
                    XREF-CARD-NUM [PK]                             │
                           │                                        │
                    Card (CVACT02Y)                           ACCT-GROUP-ID
                    CARD-NUM [PK]                                   │
                    CARD-ACCT-ID [FK→Account]                Disclosure Group
                           │                                (CVTRA02Y)
                    Transaction (CVTRA05Y)
                    TRAN-ID [PK]
                    TRAN-CARD-NUM [FK→Card]
                    TRAN-TYPE-CD [FK→TranType]
                    TRAN-CAT-CD [FK→TranCategory]
                           │
                    ┌──────┴──────┐
              TranType         TranCategory
            (CVTRA03Y)         (CVTRA04Y)
            TRAN-TYPE [PK]     TRAN-CAT-CD [PK]
                               TRAN-CAT-TYPE-CD [FK→TranType]

  User Security (CSUSR01Y)            Daily Transaction (CVTRA06Y)
  SEC-USR-ID [PK]                     Same layout as CVTRA05Y
                                      (staging file before posting)
```

---

## 6. Data Type Mapping Reference

| COBOL PIC Clause | Java Equivalent | Notes |
|------------------|-----------------|-------|
| PIC 9(n) | `long` or `int` | Unsigned integer |
| PIC S9(n)V99 | `BigDecimal(n+2, 2)` | **MANDATORY** for monetary fields |
| PIC X(n) | `String` | Fixed-length, space-padded |
| PIC X(10) (date) | `LocalDate` | Parse YYYY-MM-DD format |
| PIC X(26) (timestamp) | `LocalDateTime` | ISO-like timestamp |
| PIC X(01) (flag) | `boolean` or `enum` | 'Y'/'N' or 'A'/'U' patterns |
| 88-level VALUES | `enum` | Condition names map to enum constants |
| COMP / COMP-3 | `int` / `BigDecimal` | Binary / packed decimal |
| FILLER | — | Not mapped; padding bytes |
