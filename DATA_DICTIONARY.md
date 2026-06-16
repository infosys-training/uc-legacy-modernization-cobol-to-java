# DATA DICTIONARY — CardDemo COBOL Estate

> **Total Copybooks:** 47 (29 main `app/cpy/` + 18 sub-application)
> **Core Business Entities:** Account, Customer, Card, Card-XREF, Transaction
> **Infrastructure Copybooks:** COMMAREA, date/time, messages, validation lookups, BMS screen maps

---

## 1. Account Entity

### CVACT01Y.cpy — Account Master Record (300 bytes)

| Field | PIC Clause | Type | Bytes | Offset | Business Meaning | Validation |
|-------|-----------|------|-------|--------|------------------|------------|
| ACCT-ID | PIC 9(11) | Numeric (zoned) | 11 | 0 | Account primary key | Unique, non-zero |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | 11 | Active flag | `'Y'` or `'N'` |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | 12 | 12 | Current account balance | Implied 2 decimal places; signed |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 12 | 24 | Credit limit | ≥ 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 12 | 36 | Cash advance limit | ≥ 0 |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | 10 | 48 | Account open date | Format: CCYY-MM-DD; validated via CSUTLDTC |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | 58 | Expiration date | Format: CCYY-MM-DD *(note: misspelling is in source)* |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | 10 | 68 | Card reissue date | Format: CCYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | 12 | 78 | Current cycle credits | Running sum of cycle payments |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | 12 | 90 | Current cycle debits | Running sum of cycle charges |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | 102 | Account ZIP code | Validated against CSLKPCDY ZIP table |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | 112 | Disclosure/interest group | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| FILLER | PIC X(178) | — | 178 | 122 | Reserved | Unused padding to 300 bytes |

**VSAM File:** `AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS` — Key: ACCT-ID (bytes 0-10)

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Master Record (500 bytes)

| Field | PIC Clause | Type | Bytes | Offset | Business Meaning | Validation |
|-------|-----------|------|-------|--------|------------------|------------|
| CUST-ID | PIC 9(09) | Numeric (zoned) | 9 | 0 | Customer primary key | Unique, non-zero |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | 25 | 9 | First name | Non-empty |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | 25 | 34 | Middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | 25 | 59 | Last name | Non-empty |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | 50 | 84 | Address line 1 | Non-empty |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | 50 | 134 | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | 50 | 184 | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | 2 | 234 | US state code | Validated against 50 state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | 3 | 236 | Country code | ISO 3-letter |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | 239 | ZIP/postal code | Validated against ZIP prefix table in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | 15 | 249 | Primary phone | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | 15 | 264 | Secondary phone | Optional; same validation as phone-1 |
| CUST-SSN | PIC 9(09) | Numeric (zoned) | 9 | 279 | Social Security Number | 9-digit numeric; format validated in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | 20 | 288 | Government-issued ID | Optional |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | 10 | 308 | Date of birth | Format: CCYY-MM-DD; validated via CSUTLDTC |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | 10 | 318 | EFT/bank account link | Optional |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | 1 | 328 | Primary cardholder indicator | `'Y'` or `'N'` |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (zoned) | 3 | 329 | FICO score | Range: 300–850 |
| FILLER | PIC X(168) | — | 168 | 332 | Reserved | Unused padding to 500 bytes |

**VSAM File:** `AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS` — Key: CUST-ID (bytes 0-8)

---

## 3. Card Entity

### CVACT02Y.cpy — Card Master Record (150 bytes)

| Field | PIC Clause | Type | Bytes | Offset | Business Meaning | Validation |
|-------|-----------|------|-------|--------|------------------|------------|
| CARD-NUM | PIC X(16) | Alphanumeric | 16 | 0 | Card number (PAN) | 16-digit; primary key |
| CARD-ACCT-ID | PIC 9(11) | Numeric (zoned) | 11 | 16 | Foreign key to Account | Must exist in ACCTFILE |
| CARD-CVV-CD | PIC 9(03) | Numeric (zoned) | 3 | 27 | CVV security code | 3-digit numeric |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | 50 | 30 | Name on card | Non-empty |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | 80 | Card expiration date | Format: CCYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | 90 | Active flag | `'Y'` or `'N'` |
| FILLER | PIC X(59) | — | 59 | 91 | Reserved | Unused padding to 150 bytes |

**VSAM File:** `AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS` — Key: CARD-NUM (bytes 0-15)
**Alternate Index:** `AWS.M2.CARDDEMO.CARDDATA.VSAM.AIX` — Alt Key: CARD-ACCT-ID

---

## 4. Card-Account Cross-Reference Entity

### CVACT03Y.cpy — Card-Account XREF Record (50 bytes)

| Field | PIC Clause | Type | Bytes | Offset | Business Meaning | Validation |
|-------|-----------|------|-------|--------|------------------|------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | 16 | 0 | Card number (key) | FK to Card (CVACT02Y) |
| XREF-CUST-ID | PIC 9(09) | Numeric (zoned) | 9 | 16 | Customer ID | FK to Customer (CVCUS01Y) |
| XREF-ACCT-ID | PIC 9(11) | Numeric (zoned) | 11 | 25 | Account ID | FK to Account (CVACT01Y) |
| FILLER | PIC X(14) | — | 14 | 36 | Reserved | Padding to 50 bytes |

**VSAM File:** `AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS` — Key: XREF-CARD-NUM (bytes 0-15)
**Alternate Index:** `AWS.M2.CARDDEMO.CARDXREF.VSAM.AIX.PATH` — Alt Key: XREF-ACCT-ID

---

## 5. Transaction Entity

### CVTRA05Y.cpy — Transaction Master Record (350 bytes)

| Field | PIC Clause | Type | Bytes | Offset | Business Meaning | Validation |
|-------|-----------|------|-------|--------|------------------|------------|
| TRAN-ID | PIC X(16) | Alphanumeric | 16 | 0 | Transaction ID (key) | Unique, auto-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | 16 | Transaction type code | FK to TRAN-TYPE-RECORD (CVTRA03Y) |
| TRAN-CAT-CD | PIC 9(04) | Numeric (zoned) | 4 | 18 | Transaction category | FK to TRAN-CAT-RECORD (CVTRA04Y) |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | 22 | Transaction source | e.g., `'ONLINE'`, `'BATCH'` |
| TRAN-DESC | PIC X(100) | Alphanumeric | 100 | 32 | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | 11 | 132 | Transaction amount | Implied 2 decimal; trailing overpunch sign |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (zoned) | 9 | 143 | Merchant identifier | Numeric |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | 152 | Merchant name | Free text |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | 202 | Merchant city | Free text |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | 252 | Merchant ZIP code | Optional |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | 262 | Card number | FK to Card (CVACT02Y) |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | 26 | 278 | Origination timestamp | Format: YYYY-MM-DD-HH.MM.SS.FFFFFF |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | 26 | 304 | Processing timestamp | Format: YYYY-MM-DD-HH.MM.SS.FFFFFF |
| FILLER | PIC X(20) | — | 20 | 330 | Reserved | Padding to 350 bytes |

**VSAM File:** `AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS` — Key: TRAN-ID (bytes 0-15)

### CVTRA06Y.cpy — Daily Transaction Input Record (350 bytes)

Same layout as CVTRA05Y but with `DALYTRAN-` prefix. Used for staging daily transaction input before posting to the master.

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| DALYTRAN-ID | PIC X(16) | Transaction ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Transaction type code |
| DALYTRAN-CAT-CD | PIC 9(04) | Category code |
| DALYTRAN-SOURCE | PIC X(10) | Source identifier |
| DALYTRAN-DESC | PIC X(100) | Description |
| DALYTRAN-AMT | PIC S9(09)V99 | Transaction amount |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Merchant ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Merchant name |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Merchant city |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Merchant ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Origination timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Processing timestamp |

**Sequential File:** `AWS.M2.CARDDEMO.DALYTRAN.PS`

---

## 6. Transaction Reference Data

### CVTRA01Y.cpy — Transaction Category Balance Record (50 bytes)

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|------------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID (part of composite key) |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type (part of composite key) |
| TRANCAT-CD | PIC 9(04) | Numeric | Category code (part of composite key) |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance per account/type/category |
| FILLER | PIC X(22) | — | Padding |

**VSAM File:** `AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS` — Key: composite (ACCT-ID + TYPE-CD + CAT-CD, 17 bytes)

### CVTRA02Y.cpy — Disclosure Group / Interest Rate Record (50 bytes)

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|------------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group ID (composite key part) |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type (composite key part) |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Category (composite key part) |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Annual interest rate (% with 2 decimal places) |
| FILLER | PIC X(28) | — | Padding |

**VSAM File:** `AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS` — Key: composite (GROUP-ID + TYPE-CD + CAT-CD, 16 bytes)

### CVTRA03Y.cpy — Transaction Type Record (60 bytes)

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|------------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Type code (primary key) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Type description (e.g., "Purchase", "Cash Advance") |
| FILLER | PIC X(08) | — | Padding |

**VSAM File:** `AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS`
**DB2 Table:** `CARDDEMO.TRANSACTION_TYPE` (accessed by COTRTLIC, COTRTUPC, COBTUPDT)

### CVTRA04Y.cpy — Transaction Category Record (60 bytes)

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|------------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Type code (composite key part) |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (composite key part) |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description |
| FILLER | PIC X(04) | — | Padding |

**VSAM File:** `AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS`
**DB2 Table:** `CARDDEMO.TRANSACTION_CATEGORY` (accessed by COTRTUPC)

### CVTRA07Y.cpy — Transaction Report Layout

Print-format copybook defining report headers and detail lines for CBTRN03C daily transaction report:

| Record | Fields | Purpose |
|--------|--------|---------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME, REPT-LONG-NAME, REPT-DATE-HEADER, REPT-START-DATE, REPT-END-DATE | Report header with date range |
| TRANSACTION-DETAIL-REPORT | TRAN-REPORT-TRANS-ID, -ACCOUNT-ID, -TYPE-CD, -TYPE-DESC, -CAT-CD, -CAT-DESC, -SOURCE, -AMT | One line per transaction |
| REPORT-PAGE-TOTALS | REPT-PAGE-TOTAL (PIC +ZZZ,ZZZ,ZZZ.ZZ) | Subtotal per page |
| REPORT-ACCOUNT-TOTALS | REPT-ACCOUNT-TOTAL | Subtotal per account |
| REPORT-GRAND-TOTALS | REPT-GRAND-TOTAL | Grand total for report |

---

## 7. User / Security Entity

### CSUSR01Y.cpy — User Security Record (80 bytes)

| Field | PIC Clause | Type | Bytes | Business Meaning | Validation |
|-------|-----------|------|-------|------------------|------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | 8 | User ID (primary key) | Unique; used for login |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | 20 | User first name | Non-empty |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | 20 | User last name | Non-empty |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | 8 | Password (**plain text!**) | ⚠️ Stored unencrypted; must hash in Java migration |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | 1 | User type | `'A'` = Admin, `'U'` = Regular user |
| SEC-USR-FILLER | PIC X(23) | — | 23 | Reserved | Padding to 80 bytes |

**VSAM File:** `AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS` — Key: SEC-USR-ID (bytes 0-7)

---

## 8. Infrastructure / Common Copybooks

### COCOM01Y.cpy — CICS COMMAREA (Communication Area)

Passed between programs via EXEC CICS XCTL/LINK. Contains navigation state and user session context.

| Group | Field | PIC Clause | Business Meaning |
|-------|-------|-----------|------------------|
| CDEMO-GENERAL-INFO | CDEMO-FROM-TRANID | PIC X(04) | Source transaction ID |
| | CDEMO-FROM-PROGRAM | PIC X(08) | Source program name |
| | CDEMO-TO-TRANID | PIC X(04) | Target transaction ID |
| | CDEMO-TO-PROGRAM | PIC X(08) | Target program name |
| | CDEMO-USER-ID | PIC X(08) | Current user ID |
| | CDEMO-USER-TYPE | PIC X(01) | `'A'` = Admin, `'U'` = User (88-levels) |
| | CDEMO-PGM-CONTEXT | PIC 9(01) | 0 = first entry, 1 = re-entry |
| CDEMO-CUSTOMER-INFO | CDEMO-CUST-ID | PIC 9(09) | Current customer context |
| | CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Customer name display |
| CDEMO-ACCOUNT-INFO | CDEMO-ACCT-ID | PIC 9(11) | Current account context |
| | CDEMO-ACCT-STATUS | PIC X(01) | Account active status |
| CDEMO-CARD-INFO | CDEMO-CARD-NUM | PIC 9(16) | Current card context |
| CDEMO-MORE-INFO | CDEMO-LAST-MAP/MAPSET | PIC X(7) each | Last BMS map displayed |

### CSDAT01Y.cpy — Date/Time Working Storage

| Group | Field | PIC | Purpose |
|-------|-------|-----|---------|
| WS-CURDATE | WS-CURDATE-YEAR | PIC 9(04) | Current year (from ASKTIME) |
| | WS-CURDATE-MONTH | PIC 9(02) | Current month |
| | WS-CURDATE-DAY | PIC 9(02) | Current day |
| WS-CURTIME | WS-CURTIME-HOURS/MINUTE/SECOND/MILSEC | PIC 9(02) each | Current time components |
| WS-CURDATE-MM-DD-YY | — | — | Formatted MM/DD/YY display string |
| WS-CURTIME-HH-MM-SS | — | — | Formatted HH:MM:SS display string |
| WS-TIMESTAMP | — | — | Full YYYY-MM-DD HH:MM:SS.FFFFFF timestamp |

### CODATECN.cpy — Date Conversion Record

Supports two input formats (`1` = YYYYMMDD, `2` = YYYY-MM-DD) and two output formats. Used by COACTUPC and other programs for date normalization.

### COTTL01Y.cpy — Screen Titles

| Field | Value | Purpose |
|-------|-------|---------|
| CCDA-TITLE01 | `'AWS Mainframe Modernization'` | Application title line 1 |
| CCDA-TITLE02 | `'CardDemo'` | Application title line 2 |
| CCDA-THANK-YOU | `'Thank you for using CCDA application...'` | Exit message |

### CSMSG01Y.cpy — Common Messages

| Field | Value | Purpose |
|-------|-------|---------|
| CCDA-MSG-THANK-YOU | `'Thank you for using CardDemo application...'` | Signoff message |
| CCDA-MSG-INVALID-KEY | `'Invalid key pressed. Please see below...'` | Input error |

### CSMSG02Y.cpy — Abend Data

| Field | PIC Clause | Purpose |
|-------|-----------|---------|
| ABEND-CODE | PIC X(4) | Abend code |
| ABEND-CULPRIT | PIC X(8) | Failing program |
| ABEND-REASON | PIC X(50) | Reason text |
| ABEND-MSG | PIC X(72) | Full error message |

### CVCRD01Y.cpy — Card Working Areas

Runtime work area for online CICS card programs. Contains:
- AID key mappings (ENTER, CLEAR, PA1, PA2, PFK01–PFK12 as 88-level conditions)
- Navigation fields: CCARD-NEXT-PROG, CCARD-NEXT-MAPSET, CCARD-NEXT-MAP
- Error/return messages: CCARD-ERROR-MSG, CCARD-RETURN-MSG (PIC X(75) each)
- Context IDs: CC-ACCT-ID (PIC X(11)), CC-CARD-NUM (PIC X(16)), CC-CUST-ID (PIC X(09)) with REDEFINES for numeric access

---

## 9. Validation Lookup Copybooks

### CSLKPCDY.cpy — State/ZIP/Phone Validation Tables (1,318 LOC)

The largest copybook in the estate. Contains hard-coded validation tables:

| Validation | Content |
|------------|---------|
| **US Phone Area Codes** | 88-level `VALID-PHONE-AREA-CODE` with ~330 NANPA area codes ('201' through '989') |
| **US State Codes** | 88-level `VALID-US-STATE-CODE` with 50 state abbreviations + DC |
| **ZIP Code Prefixes** | 88-level `VALID-ZIP-PREFIX` with valid 3-digit ZIP prefixes per state |

### CSUTLDWY.cpy — Date Validation Working Storage

Provides granular date field editing with extensive 88-level conditions:
- `THIS-CENTURY` (VALUE 20), `LAST-CENTURY` (VALUE 19)
- `WS-VALID-MONTH` (VALUES 1 THROUGH 12)
- `WS-31-DAY-MONTH` (VALUES 1, 3, 5, 7, 8, 10, 12)
- `WS-FEBRUARY` (VALUE 2)
- `WS-VALID-DAY` (VALUES 1 THROUGH 31)
- `WS-DAY-31` (VALUE 31) — for months with 30 days
- Leap year handling via REDEFINES on CC and YY fields

### CSUTLDPY.cpy — Date Validation Procedure

Companion to CSUTLDWY; contains the PERFORM paragraphs:
- `EDIT-DATE-CCYYMMDD` — Master validation entry point
- `EDIT-YEAR-CCYY` — Year range validation
- Month/day cross-validation with leap year logic

### CSSETATY.cpy — Screen Attribute Macro (COPY REPLACING)

Template copybook used with `COPY REPLACING` to set BMS field attributes (color, protection) based on validation flags. Used 13× in COACTUPC and in COTRTUPC. Parameters:
- `(TESTVAR1)` — validation flag field name
- `(SCRNVAR2)` — BMS screen field name
- `(MAPNAME3)` — BMS map name

### CSSTRPFY.cpy — PF-Key Store Procedure

EVALUATE block mapping EIBAID (CICS attention identifier byte) to CCARD-AID-xxx 88-level conditions (ENTER, CLEAR, PA1, PA2, PFK01–PFK12). Included as `COPY 'CSSTRPFY'` by most CICS programs.

---

## 10. Menu / Navigation Copybooks

### COADM02Y.cpy — Admin Menu Options

6 menu items mapping option numbers to target programs:

| Option | Label | Target Program |
|--------|-------|----------------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |
| 5 | Transaction Type List/Update (Db2) | COTRTLIC |
| 6 | Transaction Type Update (Db2) | COTRTUPC |

### COMEN02Y.cpy — Main User Menu Options

11 menu items for regular users:

| Option | Label | Target Program | Access |
|--------|-------|----------------|--------|
| 1 | Account View | COACTVWC | User |
| 2 | Account Update | COACTUPC | User |
| 3 | Credit Card List | COCRDLIC | User |
| 4 | Credit Card View | COCRDSLC | User |
| 5 | Credit Card Update | COCRDUPC | User |
| 6 | Transaction List | COTRN00C | User |
| 7 | Transaction View | COTRN01C | User |
| 8 | Transaction Add | COTRN02C | User |
| 9 | Bill Payment | COBIL00C | User |
| 10 | Report Request | CORPT00C | User |
| 11 | Authorization Summary | COPAUS0C | User |

---

## 11. Sub-Application Copybooks

### Authorization Module (`app/app-authorization-ims-db2-mq/cpy/`)

| Copybook | LOC | Purpose |
|----------|-----|---------|
| CIPAUSMY.cpy | 31 | IMS segment — Pending Authorization Summary (parent segment) |
| CIPAUDTY.cpy | 54 | IMS segment — Pending Authorization Detail (child segment) |
| CCPAURQY.cpy | 36 | Authorization request message layout (MQ inbound) |
| CCPAURLY.cpy | 24 | Authorization response message layout (MQ outbound) |
| CCPAUERY.cpy | 40 | Authorization error log record |
| COPAU00.cpy | 764 | BMS map data for authorization summary screen |
| COPAU01.cpy | 344 | BMS map data for authorization detail screen |
| IMSFUNCS.cpy | 26 | IMS function code constants |

### Transaction Type Module (`app/app-transaction-type-db2/cpy/`)

| Copybook | LOC | Purpose |
|----------|-----|---------|
| COTRTLI.cpy | 500 | BMS map data for transaction type list screen |
| COTRTUP.cpy | 200 | BMS map data for transaction type update screen |
| CSDB2RPY.cpy | 89 | DB2 return code working storage |
| CSDB2RWY.cpy | — | DB2 read working storage (SQL INCLUDE) |
| CSUTLDWY.cpy | — | Date validation (shared copy, same as main) |
| DCLTRTYP | — | DB2 DCLGEN for TRANSACTION_TYPE table |
| DCLTRCAT | — | DB2 DCLGEN for TRANSACTION_CATEGORY table |

### Data Migration Copybook

| Copybook | LOC | Purpose |
|----------|-----|---------|
| CVEXPORT.cpy | ~120 | Export record layout — 505-byte multi-type record with REDEFINES for Customer, Account, Transaction, Card-XREF data; includes COMP/COMP-3 fields for export efficiency |
| CUSTREC.cpy | ~30 | Alternate customer record layout (same fields as CVCUS01Y) |

---

## 12. Entity Relationship Summary

```
Customer (CVCUS01Y)
    │
    ├──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
    │                │                              │
    │          Card (CVACT02Y)              Disclosure Group (CVTRA02Y)
    │                │                              │
    │         Transaction (CVTRA05Y)       TranCat Balance (CVTRA01Y)
    │                │
    │         TranType (CVTRA03Y) ── TranCategory (CVTRA04Y)
    │
    └──────► User Security (CSUSR01Y) [separate domain]
```

**Migration Note:** All monetary fields use `PIC S9(n)V99` with trailing overpunch sign encoding. Java migration **must** use `java.math.BigDecimal` — never `double` or `float`.
