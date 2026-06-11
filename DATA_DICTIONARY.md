# CardDemo — Data Dictionary

> Extracted from 47 copybooks (27 main `app/cpy/` + 20 sub-application).
> Fields are grouped by business entity. PIC clauses, data types, and inferred business meanings are provided for each field.

---

## 1. Account Entity

### 1.1 CVACT01Y — Account Record (RECLN 300)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | ACCOUNT-RECORD | — | Group | Root account record | Fixed 300-byte VSAM KSDS record |
| 05 | ACCT-ID | PIC 9(11) | Numeric | Unique account identifier | 11-digit numeric key |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Account status flag | 'Y'=active, 'N'=inactive |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance | Signed, 2 decimal places |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Maximum credit limit | Must be > 0 |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | ≤ ACCT-CREDIT-LIMIT |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Date string | Date account was opened | YYYY-MM-DD format |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Date string | Account expiration date | YYYY-MM-DD format |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Date string | Last card reissue date | YYYY-MM-DD format |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total | Running total for statement period |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total | Running total for statement period |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | US ZIP+4 or international |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Discount/rate group | Links to DIS-GROUP-RECORD |
| 05 | FILLER | PIC X(178) | Filler | Reserved space | Pads to 300 bytes |

### 1.2 CVACT03Y — Card-Account Cross-Reference (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | CARD-XREF-RECORD | — | Group | Card-to-account link | Fixed 50-byte VSAM record |
| 05 | XREF-CARD-NUM | PIC X(16) | Alphanumeric | Credit card number | 16-digit card number (key) |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric | Customer identifier | FK → CUSTOMER-RECORD |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric | Account identifier | FK → ACCOUNT-RECORD |
| 05 | FILLER | PIC X(14) | Filler | Reserved | Pads to 50 bytes |

---

## 2. Customer Entity

### 2.1 CVCUS01Y — Customer Record (RECLN 500)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | CUSTOMER-RECORD | — | Group | Root customer record | Fixed 500-byte VSAM record |
| 05 | CUST-ID | PIC 9(09) | Numeric | Unique customer ID | 9-digit numeric key |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alpha | Customer first name | — |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alpha | Customer middle name | — |
| 05 | CUST-LAST-NAME | PIC X(25) | Alpha | Customer last name | — |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | — |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | — |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | — |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code | Validated by CSLKPCDY 88-level |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code | ISO-3 country code |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | Validated by CSLKPCDY 88-level |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone | Area code validated by CSLKPCDY |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone | Area code validated by CSLKPCDY |
| 05 | CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit; PII |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID | Driver's license, passport, etc. |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Date string | Date of birth | YYYY-MM-DD; validated by CSUTLDPY |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account ID | Electronic funds transfer link |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary cardholder flag | 'Y'/'N' |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range 300–850 |
| 05 | FILLER | PIC X(168) | Filler | Reserved | Pads to 500 bytes |

### 2.2 CUSTREC — Customer Record (Statement variant)

Identical structure to CVCUS01Y but used by CBSTM03A for statement generation. Date field uses `CUST-DOB-YYYYMMDD` (no dashes) instead of `CUST-DOB-YYYY-MM-DD`.

---

## 3. Card Entity

### 3.1 CVACT02Y — Card Record (RECLN 150)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | CARD-RECORD | — | Group | Credit card record | Fixed 150-byte VSAM record |
| 05 | CARD-NUM | PIC X(16) | Alphanumeric | Card number (PAN) | 16-digit; primary key |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | FK → ACCOUNT-RECORD |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit CVV; PII |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alpha | Name embossed on card | — |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Date string | Card expiration date | YYYY-MM-DD format |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card active flag | 'Y'=active, 'N'=inactive |
| 05 | FILLER | PIC X(59) | Filler | Reserved | Pads to 150 bytes |

### 3.2 CVCRD01Y — Card Work Areas (CICS screen control)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | CC-WORK-AREAS | — | Group | Working storage for card screens | — |
| 10 | CCARD-AID | PIC X(5) | Alpha | Current attention identifier key | 88-levels: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| 10 | CCARD-NEXT-PROG | PIC X(8) | Alpha | Next program to XCTL to | Valid program name |
| 10 | CCARD-NEXT-MAPSET | PIC X(7) | Alpha | Next BMS mapset | Valid mapset name |
| 10 | CCARD-NEXT-MAP | PIC X(7) | Alpha | Next BMS map | Valid map name |
| 10 | CCARD-ERROR-MSG | PIC X(75) | Alpha | Error message for display | — |
| 10 | CCARD-RETURN-MSG | PIC X(75) | Alpha | Return/info message | 88: CCARD-RETURN-MSG-OFF = LOW-VALUES |
| 10 | CC-ACCT-ID | PIC X(11) | Alphanumeric | Account ID (screen input) | REDEFINES to PIC 9(11) for numeric use |
| 10 | CC-CARD-NUM | PIC X(16) | Alphanumeric | Card number (screen input) | REDEFINES to PIC 9(16) for numeric use |
| 10 | CC-CUST-ID | PIC X(09) | Alphanumeric | Customer ID (screen input) | REDEFINES to PIC 9(9) for numeric use |

---

## 4. Transaction Entity

### 4.1 CVTRA05Y — Transaction Record (RECLN 350)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | TRAN-RECORD | — | Group | Master transaction record | Fixed 350-byte VSAM record |
| 05 | TRAN-ID | PIC X(16) | Alphanumeric | Transaction ID | System-generated unique key |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | FK → TRAN-TYPE-RECORD |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | FK → TRAN-CAT-RECORD |
| 05 | TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction origination source | e.g., POS, ATM, online |
| 05 | TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free-text description |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Positive=debit, negative=credit |
| 05 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | — |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant business name | — |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | — |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | — |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used | FK → CARD-RECORD |
| 05 | TRAN-ORIG-TS | PIC X(26) | Timestamp | Original transaction timestamp | YYYY-MM-DD-HH.MM.SS.FFFFFF |
| 05 | TRAN-PROC-TS | PIC X(26) | Timestamp | Processing timestamp | YYYY-MM-DD-HH.MM.SS.FFFFFF |
| 05 | FILLER | PIC X(20) | Filler | Reserved | Pads to 350 bytes |

### 4.2 CVTRA06Y — Daily Transaction Record (RECLN 350)

Same layout as CVTRA05Y but with `DALYTRAN-` prefix. Used as input for batch transaction posting (CBTRN02C). Staged in daily batch file before posting to master TRANSACT.

### 4.3 CVTRA01Y — Transaction Category Balance (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | Category balance per account | 50-byte VSAM record |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | FK → ACCOUNT-RECORD |
| 10 | TRANCAT-TYPE-CD | PIC X(02) | Alpha | Transaction type code | FK → TRAN-TYPE-RECORD |
| 10 | TRANCAT-CD | PIC 9(04) | Numeric | Category code | FK → TRAN-CAT-RECORD |
| 05 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance for this category | Updated by CBTRN02C, read by CBACT04C |
| 05 | FILLER | PIC X(22) | Filler | Reserved | — |

### 4.4 CVTRA02Y — Disclosure/Discount Group (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | DIS-GROUP-RECORD | — | Group | Interest rate by group/type/cat | 50-byte VSAM record |
| 05 | DIS-GROUP-KEY | — | Group | Composite key | — |
| 10 | DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group identifier | FK → ACCT-GROUP-ID |
| 10 | DIS-TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type | FK → TRAN-TYPE-RECORD |
| 10 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category | FK → TRAN-CAT-RECORD |
| 05 | DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate percentage | Annual interest rate for this group |
| 05 | FILLER | PIC X(28) | Filler | Reserved | — |

### 4.5 CVTRA03Y — Transaction Type (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type reference | 60-byte VSAM record |
| 05 | TRAN-TYPE | PIC X(02) | Alpha | Type code | Primary key |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alpha | Type description | e.g., "Purchase", "Cash Advance" |
| 05 | FILLER | PIC X(08) | Filler | Reserved | — |

### 4.6 CVTRA04Y — Transaction Category (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category reference | 60-byte VSAM record |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRAN-TYPE-CD | PIC X(02) | Alpha | Parent type code | FK → TRAN-TYPE-RECORD |
| 10 | TRAN-CAT-CD | PIC 9(04) | Numeric | Category code | Part of composite key |
| 05 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alpha | Category description | e.g., "Retail Purchase", "Online Purchase" |
| 05 | FILLER | PIC X(04) | Filler | Reserved | — |

### 4.7 CVTRA07Y — Transaction Report Layout

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | REPORT-NAME-HEADER | — | Group | Report title area |
| 05 | REPT-SHORT-NAME | PIC X(38) | Alpha | Report short name ("DALYREPT") |
| 05 | REPT-LONG-NAME | PIC X(41) | Alpha | Report full name ("Daily Transaction Report") |
| 05 | REPT-START-DATE / REPT-END-DATE | PIC X(10) | Date | Date range filter |
| 01 | TRANSACTION-DETAIL-REPORT | — | Group | Detail line layout |
| 05 | TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID column |
| 05 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID column |
| 05 | TRAN-REPORT-TYPE-CD / TYPE-DESC | PIC X(02) / X(15) | Alpha | Type code and description |
| 05 | TRAN-REPORT-CAT-CD / CAT-DESC | PIC 9(04) / X(29) | Mixed | Category code and description |
| 05 | TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Transaction source |
| 05 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Transaction amount (formatted) |
| 01 | REPORT-PAGE-TOTALS | — | Group | Page total line |
| 05 | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Sum for current page |
| 01 | REPORT-ACCOUNT-TOTALS | — | Group | Account total line |
| 01 | REPORT-GRAND-TOTALS | — | Group | Grand total line |

### 4.8 COSTM01 — Statement Transaction Layout

Rekeyed version of CVTRA05Y with composite key `(TRNX-CARD-NUM, TRNX-ID)` for sorted statement output. Same fields as CVTRA05Y under `TRNX-` prefix.

---

## 5. Security / User Entity

### 5.1 CSUSR01Y — User Security Record (RECLN 80)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|------------|-----------|------------------|------------|
| 01 | SEC-USER-DATA | — | Group | User authentication record | 80-byte VSAM record |
| 05 | SEC-USR-ID | PIC X(08) | Alpha | User login ID | Primary key |
| 05 | SEC-USR-FNAME | PIC X(20) | Alpha | First name | — |
| 05 | SEC-USR-LNAME | PIC X(20) | Alpha | Last name | — |
| 05 | SEC-USR-PWD | PIC X(08) | Alpha | Password | Plaintext (security concern) |
| 05 | SEC-USR-TYPE | PIC X(01) | Alpha | User type | 'A'=admin, 'U'=user |
| 05 | SEC-USR-FILLER | PIC X(23) | Filler | Reserved | — |

---

## 6. Export / Data Migration Entity

### 6.1 CVEXPORT — Multi-Record Export Layout (RECLN 500)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | EXPORT-RECORD | — | Group | Export envelope record (500 bytes) |
| 05 | EXPORT-REC-TYPE | PIC X(1) | Alpha | Record type discriminator (C=customer, A=account, T=transaction, X=xref, R=card) |
| 05 | EXPORT-TIMESTAMP | PIC X(26) | Timestamp | Export timestamp |
| 05 | EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number |
| 05 | EXPORT-BRANCH-ID | PIC X(4) | Alpha | Branch identifier |
| 05 | EXPORT-REGION-CODE | PIC X(5) | Alpha | Region code |
| 05 | EXPORT-RECORD-DATA | PIC X(460) | Group (REDEFINES) | Payload — redefines for each entity type |

REDEFINES variants: `EXPORT-CUSTOMER-DATA`, `EXPORT-ACCOUNT-DATA`, `EXPORT-TRANSACTION-DATA`, `EXPORT-CARD-XREF-DATA`, `EXPORT-CARD-DATA` — each mirrors the corresponding entity copybook fields with `EXP-` prefix and COMP/COMP-3 optimizations.

---

## 7. System / Shared Copybooks

### 7.1 COCOM01Y — Communication Area (COMMAREA)

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | CARDDEMO-COMMAREA | — | Group | CICS inter-program communication area |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alpha | Source CICS transaction ID |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alpha | Source program name |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alpha | Target CICS transaction ID |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alpha | Target program name |
| 10 | CDEMO-USER-ID | PIC X(08) | Alpha | Authenticated user ID |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alpha | 88: CDEMO-USRTYP-ADMIN='A', CDEMO-USRTYP-USER='U' |
| 10 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 88: CDEMO-PGM-ENTER=0, CDEMO-PGM-REENTER=1 |
| 10 | CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer ID context |
| 10 | CDEMO-CUST-FNAME / MNAME / LNAME | PIC X(25) | Alpha | Customer name context |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account ID context |
| 10 | CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Current account status |
| 10 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card number context |
| 10 | CDEMO-LAST-MAP / LAST-MAPSET | PIC X(7) | Alpha | Previous screen navigation state |

### 7.2 COADM02Y — Admin Menu Options

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | CARDDEMO-ADMIN-MENU-OPTIONS | — | Group | Admin function menu definition |
| 05 | CDEMO-ADMIN-OPT-COUNT | PIC 9(02) | Numeric | Number of active options (VALUE 6) |
| 10 | CDEMO-ADMIN-OPT-NUM | PIC 9(02) | Numeric | Option number |
| 10 | CDEMO-ADMIN-OPT-NAME | PIC X(35) | Alpha | Option display text |
| 10 | CDEMO-ADMIN-OPT-PGMNAME | PIC X(08) | Alpha | Target program name |

Options: 1=COUSR00C (User List), 2=COUSR01C (User Add), 3=COUSR02C (User Update), 4=COUSR03C (User Delete), 5=COTRTLIC (Tran Type List/DB2), 6=COTRTUPC (Tran Type Maintenance/DB2).

### 7.3 COMEN02Y — Main Menu Options

Same structure as COADM02Y but with 11 options for regular users. Targets: COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C.

### 7.4 COTTL01Y — Screen Title

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | CCDA-SCREEN-TITLE | — | Group | Standard screen header |
| 05 | CCDA-TITLE01 | PIC X(40) | Alpha | Line 1: "AWS Mainframe Modernization" |
| 05 | CCDA-TITLE02 | PIC X(40) | Alpha | Line 2: "CardDemo" |
| 05 | CCDA-THANK-YOU | PIC X(40) | Alpha | Exit message text |

### 7.5 CSDAT01Y — Date/Time Constants

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 01 | WS-DATE-TIME | — | Group | System date/time working storage |
| 10 | WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year |
| 10 | WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| 10 | WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| 10 | WS-CURTIME-HOURS / MINUTE / SECOND / MILSEC | PIC 9(02) | Numeric | Time components |
| 05 | WS-CURDATE-MM-DD-YY | — | Group | Formatted MM/DD/YY display |
| 05 | WS-CURTIME-HH-MM-SS | — | Group | Formatted HH:MM:SS display |
| 05 | WS-TIMESTAMP | — | Group | Full YYYY-MM-DD HH:MM:SS.FFFFFF timestamp |

### 7.6 CSUTLDWY — Date Validation Working Storage

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 10 | WS-EDIT-DATE-CCYYMMDD | — | Group | Date to validate (century+year+month+day) |
| 25 | WS-EDIT-DATE-CC | PIC X(2) | Alpha | Century; 88: THIS-CENTURY=20, LAST-CENTURY=19 |
| 25 | WS-EDIT-DATE-YY | PIC X(2) | Alpha | Year within century |
| 20 | WS-EDIT-DATE-MM | PIC X(2) | Alpha | Month; 88: WS-VALID-MONTH=1–12, WS-31-DAY-MONTH, WS-FEBRUARY=2 |
| 20 | WS-EDIT-DATE-DD | PIC X(2) | Alpha | Day; 88: WS-VALID-DAY=1–31, WS-DAY-31, WS-DAY-30, WS-DAY-29 |
| 10 | WS-EDIT-DATE-FLGS | — | Group | Validation result flags |
| 20 | WS-EDIT-YEAR-FLG | PIC X(01) | Alpha | 88: FLG-YEAR-ISVALID, FLG-YEAR-NOT-OK, FLG-YEAR-BLANK |
| 20 | WS-EDIT-MONTH | PIC X(01) | Alpha | 88: FLG-MONTH-ISVALID, FLG-MONTH-NOT-OK, FLG-MONTH-BLANK |
| 20 | WS-EDIT-DAY | PIC X(01) | Alpha | 88: FLG-DAY-ISVALID, FLG-DAY-NOT-OK, FLG-DAY-BLANK |

### 7.7 CSUTLDPY — Date Validation Procedure

Procedure-division copybook (not data). Contains paragraphs: `EDIT-DATE-CCYYMMDD`, `EDIT-YEAR-CCYY`, `EDIT-MONTH`, `EDIT-DAY`, `EDIT-DATE-OF-BIRTH`. Validates dates in CCYYMMDD format with leap year support.

### 7.8 CSLKPCDY — Lookup Code Repository (1,318 LOC)

Contains 88-level validation tables for:
- **WS-US-PHONE-AREA-CODE-TO-EDIT** PIC XXX — Valid North American area codes (~350 values sourced from NANPA)
- **WS-US-STATE-CODE-TO-EDIT** PIC XX — Valid US state codes (50 states + DC + territories)
- **WS-US-STATE-ZIPCD-RANGE** — Valid state+ZIP prefix combinations

### 7.9 CSSETATY — Attribute-Setting Macro

Template copybook used with COPY REPLACING. Sets BMS field color attribute to red for invalid fields and asterisk for blank required fields. Parameters: `(TESTVAR1)`, `(SCRNVAR2)`, `(MAPNAME3)`.

### 7.10 CSSTRPFY — PF Key Store Paragraph

Procedure-division copybook. Maps CICS EIBAID to CCARD-AID 88-level values (ENTER, CLEAR, PA1, PA2, PFK01–PFK12). Uses EVALUATE TRUE pattern.

### 7.11 CSMSG01Y — Common Messages

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|------------------|
| 05 | CCDA-MSG-THANK-YOU | PIC X(50) | Logout message |
| 05 | CCDA-MSG-INVALID-KEY | PIC X(50) | Invalid key press message |

### 7.12 CSMSG02Y — Abend Data

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|------------------|
| 05 | ABEND-CODE | PIC X(4) | Abend code |
| 05 | ABEND-CULPRIT | PIC X(8) | Program that caused abend |
| 05 | ABEND-REASON | PIC X(50) | Reason text |
| 05 | ABEND-MSG | PIC X(72) | Full abend message |

### 7.13 CODATECN — Date Conversion Record

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|------------------|
| 01 | CODATECN-REC | — | Date conversion I/O record |
| 10 | CODATECN-TYPE | PIC X | Input format: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| 10 | CODATECN-INP-DATE | PIC X(20) | Input date (multiple REDEFINES for format variants) |
| 10 | CODATECN-OUTTYPE | PIC X | Output format: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| 10 | CODATECN-0UT-DATE | PIC X(20) | Converted output date |
| 05 | CODATECN-ERROR-MSG | PIC X(38) | Conversion error message |

### 7.14 UNUSED1Y — Unused/Deprecated

Same layout as CSUSR01Y with `UNUSED-` prefix. Not referenced by any active program.

---

## 8. Authorization Entity (Sub-Application)

### 8.1 CIPAUSMY — IMS Auth Summary Segment

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 05 | PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID |
| 05 | PA-CUST-ID | PIC 9(09) | Numeric | Customer ID |
| 05 | PA-AUTH-STATUS | PIC X(01) | Alpha | Authorization status |
| 05 | PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alpha array | Account status codes (5 slots) |
| 05 | PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit |
| 05 | PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash limit |
| 05 | PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Credit balance |
| 05 | PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Cash balance |

### 8.2 CIPAUDTY — IMS Auth Detail Segment

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|------------------|
| 05 | PA-AUTHORIZATION-KEY | — | Group | Composite key (date+time packed) |
| 10 | PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed | Packed auth date |
| 10 | PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed | Packed auth time |
| 05 | PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| 05 | PA-AUTH-TYPE | PIC X(04) | Alpha | Authorization type |
| 05 | PA-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry |
| 05 | PA-MESSAGE-TYPE | PIC X(06) | Alpha | Message type |
| 05 | PA-AUTH-RESP-CODE | PIC X(02) | Alpha | 88: PA-AUTH-APPROVED='00' |
| 05 | PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Transaction amount |
| 05 | PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount |
| 05 | PA-MERCHANT-ID / NAME / CITY / STATE / ZIP | Various | Various | Merchant details |
| 05 | PA-MATCH-STATUS | PIC X(01) | Alpha | 88: P=pending, D=declined, E=expired, M=matched |
| 05 | PA-AUTH-FRAUD | PIC X(01) | Alpha | 88: F=fraud confirmed, R=fraud removed |
| 05 | PA-FRAUD-RPT-DATE | PIC X(08) | Date | Fraud report date |

### 8.3 CCPAURQY — Auth Request (MQ Message)

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|------------------|
| 05 | PA-RQ-AUTH-DATE | PIC X(06) | Request date |
| 05 | PA-RQ-AUTH-TIME | PIC X(06) | Request time |
| 05 | PA-RQ-CARD-NUM | PIC X(16) | Card number |
| 05 | PA-RQ-AUTH-TYPE | PIC X(04) | Auth type |
| 05 | PA-RQ-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Request amount |
| 05 | PA-RQ-MERCHANT-* | Various | Merchant details |
| 05 | PA-RQ-TRANSACTION-ID | PIC X(15) | Transaction ID |

### 8.4 CCPAURLY — Auth Response (MQ Message)

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|------------------|
| 05 | PA-RL-CARD-NUM | PIC X(16) | Card number |
| 05 | PA-RL-TRANSACTION-ID | PIC X(15) | Transaction ID |
| 05 | PA-RL-AUTH-ID-CODE | PIC X(06) | Authorization code |
| 05 | PA-RL-AUTH-RESP-CODE | PIC X(02) | Response code |
| 05 | PA-RL-AUTH-RESP-REASON | PIC X(04) | Reason code |
| 05 | PA-RL-APPROVED-AMT | PIC +9(10).99 | Approved amount |

### 8.5 CCPAUERY — Auth Error Log

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|------------------|
| 05 | ERR-DATE | PIC X(06) | Error date |
| 05 | ERR-TIME | PIC X(06) | Error time |
| 05 | ERR-APPLICATION | PIC X(08) | Application name |
| 05 | ERR-PROGRAM | PIC X(08) | Program name |
| 05 | ERR-LEVEL | PIC X(01) | 88: L=Log, I=Info, W=Warning, C=Critical |
| 05 | ERR-SUBSYSTEM | PIC X(01) | 88: A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File |
| 05 | ERR-CODE-1 / ERR-CODE-2 | PIC X(09) | Error codes |
| 05 | ERR-MESSAGE | PIC X(50) | Error message text |

---

## 9. DB2 Entities (Sub-Application)

### 9.1 DCLTRTYP — Transaction Type (DB2 table: CARDDEMO.TRANSACTION_TYPE)

Used by COTRTLIC, COTRTUPC, COBTUPDT via `EXEC SQL INCLUDE DCLTRTYP`. Maps the DB2 TRANSACTION_TYPE table.

### 9.2 DCLTRCAT — Transaction Category (DB2 table: CARDDEMO.TRANSACTION_CATEGORY)

Used by COTRTUPC via `EXEC SQL INCLUDE DCLTRCAT`. Maps the DB2 TRANSACTION_CATEGORY table.

### 9.3 CSDB2RWY / CSDB2RPY — DB2 Working Storage / Procedure Division

DB2-specific utility copybooks used by COTRTLIC for cursor management and SQL error handling.

---

## 10. MQ Copybooks (IBM-Supplied)

Used by COACCT01, CODATE01, COPAUA0C:

| Copybook | Purpose |
|----------|---------|
| CMQV | MQ constants and structures |
| CMQODV | MQ Object Descriptor |
| CMQMDV | MQ Message Descriptor |
| CMQGMOV | MQ Get Message Options |
| CMQPMOV | MQ Put Message Options |
| CMQTML | MQ Trigger Message Layout |

---

## 11. IMS Copybooks (Sub-Application)

| Copybook | Purpose |
|----------|---------|
| IMSFUNCS | IMS DL/I function codes (GN, GNP, GU, ISRT, DLET) |
| PAUTBPCB | IMS PCB for auth top database |
| PASFLPCB | IMS PCB for auth summary segment |
| PADFLPCB | IMS PCB for auth detail segment |
