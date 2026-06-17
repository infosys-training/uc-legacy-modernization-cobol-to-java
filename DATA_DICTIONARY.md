# DATA DICTIONARY — CardDemo COBOL Estate

> **Total Copybooks:** 47 (27 main + 20 sub-application)
> **Source:** `app/cpy/`, `app/app-authorization-ims-db2-mq/cpy/`, `app/app-transaction-type-db2/cpy/`
> BMS-generated copybooks (`cpy-bms/`) are screen layouts and are listed separately.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)
**VSAM File:** ACCTFILE (KSDS, key = ACCT-ID)
**Used by:** CBACT01C, CBACT04C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, CBEXPORT, CBSTM03A, COPAUA0C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account primary key | Must be numeric, unique in KSDS |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Account active flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Signed; updated by bill pay and interest calc |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum credit allowed | Must be > 0 for active accounts |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Cash advance credit limit | Separate from purchase credit limit |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (10) | Date account was opened | Format: YYYY-MM-DD; validated by CSUTLDTC |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Account expiration date | Format: YYYY-MM-DD; validated by CSUTLDTC |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (10) | Last card reissue date | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current billing cycle credits | Reset at statement generation |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current billing cycle debits | Reset at statement generation |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account ZIP code | Validated against CSLKPCDY state+ZIP table |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure/interest rate group | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| FILLER | PIC X(178) | — | Reserved | — |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)
**VSAM File:** CUSTFILE (KSDS, key = CUST-ID)
**Used by:** COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, CBCUS01C, CBEXPORT, COPAUA0C, COPAUS0C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer primary key | Must be numeric, unique in KSDS |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | Must not be blank |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | Must not be blank |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric (2) | US state code | Validated against 50+ state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric (3) | Country code | — |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | ZIP/postal code | First 2 digits validated vs. state in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone | Area code validated against NANPA list |
| CUST-SSN | PIC 9(09) | Numeric (9 digits) | Social Security Number | Must be 9-digit numeric |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID number | — |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (10) | Date of birth | Format: YYYY-MM-DD; validated via CSUTLDTC |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | EFT (electronic funds transfer) account | — |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric (1) | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3 digits) | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | — | Reserved | — |

### CUSTREC.cpy — Customer Record (Statement Generation variant)
**Used by:** CBSTM03A

Identical structure to CVCUS01Y but used specifically in statement generation context. Same field layout, same 500-byte record.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)
**VSAM File:** CARDFILE (KSDS, key = CARD-NUM)
**Used by:** CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, CBEXPORT

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (primary key) | 16-digit card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Foreign key to Account | Must exist in ACCTFILE |
| CARD-CVV-CD | PIC 9(03) | Numeric (3 digits) | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name printed on card | Cardholder name |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Card active flag | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | — | Reserved | — |

### CVACT03Y.cpy — Card Cross-Reference (RECLN 50)
**VSAM File:** XREFFILE (KSDS, key = XREF-CARD-NUM)
**Used by:** CBACT03C, CBACT04C, COACTUPC, COACTVWC, COTRN02C, CBEXPORT, CBTRN02C, CBTRN03C, CBSTM03A, COPAUA0C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (key) | Links to CARD-NUM in CVACT02Y |
| XREF-CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer ID linkage | Foreign key to CVCUS01Y |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account ID linkage | Foreign key to CVACT01Y |
| FILLER | PIC X(14) | — | Reserved | — |

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)
**VSAM File:** TRANSACT (KSDS)
**Used by:** CBACT04C, COTRN00C, COTRN01C, COTRN02C, CORPT00C, CBTRN03C, CBEXPORT, COBIL00C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Transaction unique identifier | System-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Links to TRAN-TYPE in CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | Links to TRAN-CAT-KEY in CVTRA04Y |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source channel | e.g., 'POS', 'ATM', 'ONLINE' |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Signed; negative = credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card used for transaction | Links to XREF-CARD-NUM |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original transaction timestamp | Format: YYYY-MM-DD-HH.MM.SS.NNNNNN |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | Set during POSTTRAN batch |
| FILLER | PIC X(20) | — | Reserved | — |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)
**File:** DALYTRAN (sequential input)
**Used by:** CBTRN01C, CBTRN02C

Same structure as CVTRA05Y but with `DALYTRAN-` prefix. Represents incoming daily transactions before posting.

| Key Fields | PIC Clause | Business Meaning |
|-----------|-----------|------------------|
| DALYTRAN-ID | PIC X(16) | Daily transaction ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Transaction type code |
| DALYTRAN-AMT | PIC S9(09)V99 | Transaction amount |
| DALYTRAN-CARD-NUM | PIC X(16) | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Origination timestamp |

### COSTM01.CPY — Transaction Record (Statement Layout)
**Used by:** CBSTM03A

Re-keyed transaction layout with `TRNX-` prefix, ordered by CARD-NUM + TRAN-ID as composite key for statement generation sorting.

| Key Fields | PIC Clause | Business Meaning |
|-----------|-----------|------------------|
| TRNX-CARD-NUM | PIC X(16) | Card number (primary sort key) |
| TRNX-ID | PIC X(16) | Transaction ID (secondary key) |
| TRNX-AMT | PIC S9(09)V99 | Transaction amount |

---

## 5. Transaction Reference Data

### CVTRA03Y.cpy — Transaction Type (RECLN 60)
**VSAM File:** TRANTYPE (KSDS, key = TRAN-TYPE)
**DB2 Table:** TRNTYPE (in transaction-type-db2 sub-app)
**Used by:** CBTRN03C, COTRTLIC, COTRTUPC

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric (2) | Transaction type code (PK) | e.g., 'SA' = sale, 'CR' = credit |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Type description | — |
| FILLER | PIC X(08) | — | Reserved | — |

### CVTRA04Y.cpy — Transaction Category (RECLN 60)
**VSAM File:** TRANCATG (KSDS, key = TRAN-TYPE-CD + TRAN-CAT-CD)
**DB2 Table:** TRNCATG (in transaction-type-db2 sub-app)
**Used by:** CBTRN03C, COTRTLIC, COTRTUPC

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code (FK) | Must exist in CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Category code | Composite key with type code |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | — |
| FILLER | PIC X(04) | — | Reserved | — |

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)
**VSAM File:** TCATBALF (KSDS)
**Used by:** CBACT04C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | FK to CVACT01Y |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type | FK to CVTRA03Y |
| TRANCAT-CD | PIC 9(04) | Numeric (4) | Category code | Part of composite key |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance by category | Updated during interest calc |
| FILLER | PIC X(22) | — | Reserved | — |

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)
**VSAM File:** DISCGRP (KSDS)
**Used by:** CBACT04C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Account group ID | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type | — |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code | Composite key |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest rate (%) | Applied during INTCALC batch |
| FILLER | PIC X(28) | — | Reserved | — |

### CVTRA07Y.cpy — Report Headers & Detail Line
**Used by:** CBTRN03C (report generation)

Defines report formatting structures:
- `REPORT-NAME-HEADER` — report title, date range
- `TRANSACTION-DETAIL-REPORT` — per-transaction print line
- `TRANSACTION-HEADER-1/2` — column headers
- `REPORT-PAGE-TOTALS` / `REPORT-ACCOUNT-TOTALS` / `REPORT-GRAND-TOTALS` — summary lines

---

## 6. User / Security Entity

### CSUSR01Y.cpy — Security User Record (RECLN 80)
**VSAM File:** USRSEC (KSDS, key = SEC-USR-ID)
**Used by:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COADM01C, COMEN01C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric (8) | User login ID (PK) | Must not be blank |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password | Plaintext storage |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric (1) | User type | 'A' = admin, 'U' = regular user |
| SEC-USR-FILLER | PIC X(23) | — | Reserved | — |

### UNUSED1Y.cpy — Unused Record Layout
Appears to be a deprecated or placeholder variant similar to CSUSR01Y with slightly different field names. Not actively referenced.

---

## 7. Application Infrastructure Copybooks

### COCOM01Y.cpy — COMMAREA (Communication Area)
**Used by:** All CICS programs (passed between programs via XCTL/LINK)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric (4) | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric (8) | Calling program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric (4) | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric (8) | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric (8) | Current user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric (1) | 'A' = admin, 'U' = user |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric (1) | 0 = enter, 1 = re-enter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric (9) | Current customer context |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric (11) | Current account context |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric (16) | Current card context |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric (7) | Last displayed BMS map |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric (7) | Last displayed mapset |

### COADM02Y.cpy — Admin Menu Options
Defines 6 admin menu items mapping option numbers to program names (COUSR00C–COUSR03C, COTRTLIC, COTRTUPC).

### COMEN02Y.cpy — Main Menu Options
Defines 11 user menu items mapping option numbers to program names (COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C).

### COTTL01Y.cpy — Screen Title Block
Defines application title strings: "AWS Mainframe Modernization" / "CardDemo".

### CSDAT01Y.cpy — Date/Time Working Storage
Provides standardized date/time structures: `WS-CURDATE` (YYYYMMDD), `WS-CURTIME` (HHMMSSMS), formatted variants (MM/DD/YY, HH:MM:SS), and full timestamp (YYYY-MM-DD HH:MM:SS.NNNNNN).

### CSMSG01Y.cpy — Common Messages
Two standard messages: thank-you and invalid-key-pressed.

### CSMSG02Y.cpy — Abend Data
Working storage for abend (abnormal end) handling: ABEND-CODE, ABEND-CULPRIT, ABEND-REASON, ABEND-MSG.

### CVCRD01Y.cpy — Card Work Areas
CICS screen interaction control block for card programs. Contains AID key mapping (88-level conditions for ENTER, CLEAR, PA1, PA2, PF1–PF12), next-program routing, error/return messages, and account/card/customer ID working fields.

### CODATECN.cpy — Date Conversion Record
Input/output structure for date format conversion (YYYYMMDD ↔ YYYY-MM-DD). Used by assembler routine COBDATFT.

### CSSETATY.cpy — Screen Attribute Setter (Macro)
COPY REPLACING macro used 25 times in COACTUPC. Sets BMS field color to red and marks with '*' when validation fails.

### CSSTRPFY.cpy — Store PFKey Paragraph
Maps EIBAID (CICS attention identifier) to application-level PFKey constants. Used by COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC.

### CSUTLDWY.cpy — Date Validation Working Storage
Working storage variables for date validation: year/month/day edit fields, century flags (88-level THIS-CENTURY/LAST-CENTURY), input-error flags, date-of-birth validation fields. Accompanies CSUTLDPY.

### CSUTLDPY.cpy — Date Validation Procedures
Procedure division paragraphs for date validation: EDIT-DATE-CCYYMMDD, EDIT-YEAR-CCYY, EDIT-MONTH, EDIT-DAY, EDIT-DATE-OF-BIRTH. Validates century (19xx/20xx only), month (1–12), day (leap year aware).

### CSLKPCDY.cpy — Lookup Code Repository (1,318 lines)
Master validation lookup containing:
1. **NANPA phone area codes** — 88-level VALID-PHONE-AREA-CODE with ~400 values
2. **US state codes** — 88-level VALID-US-STATE-CODE with 50 states + territories
3. **State + ZIP prefix** — 88-level condition matching state code to valid first-2-digit ZIP ranges

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)
REDEFINES structure supporting 4 record types in a single file:
- Customer data (EXP-CUST-*)
- Account data (EXP-ACCT-*) — uses COMP-3 for some amounts
- Transaction data (EXP-TRAN-*) — uses COMP-3 for amounts
- Card cross-reference (EXP-XREF-*) — uses COMP for account ID

---

## 8. Authorization Sub-Application Copybooks

### CIPAUSMY.cpy — Pending Auth Summary (IMS Root Segment)
| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID |
| PA-CUST-ID | PIC 9(09) | Numeric | Customer ID |
| PA-AUTH-STATUS | PIC X(01) | Alpha | Authorization status |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Array | Status array (5 slots) |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash advance limit |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Current credit balance |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Cash balance |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of approved auths |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of declined auths |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount |

### CIPAUDTY.cpy — Pending Auth Detail (IMS Child Segment)
| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Auth date (compressed) |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Auth time (compressed) |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code ('00' = approved) |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Requested amount |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount |
| PA-MERCHANT-* | Various | Various | Merchant details (ID, name, city, state, ZIP) |
| PA-MATCH-STATUS | PIC X(01) | Alpha | 'P'=pending, 'D'=declined, 'E'=expired, 'M'=matched |
| PA-AUTH-FRAUD | PIC X(01) | Alpha | 'F'=fraud confirmed, 'R'=fraud removed |

### CCPAURQY.cpy — Authorization Request (MQ message)
| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| PA-RQ-CARD-NUM | PIC X(16) | Card number |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Requested amount |
| PA-RQ-MERCHANT-* | Various | Merchant details |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Transaction reference |

### CCPAURLY.cpy — Authorization Response (MQ message)
| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| PA-RL-CARD-NUM | PIC X(16) | Card number |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Response code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Approved amount |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Authorization ID |

### CCPAUERY.cpy — Error Log Record
| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| ERR-DATE | PIC X(06) | Error date |
| ERR-APPLICATION | PIC X(08) | Application name |
| ERR-PROGRAM | PIC X(08) | Program name |
| ERR-LEVEL | PIC X(01) | 'L'=log, 'I'=info, 'W'=warn, 'C'=critical |
| ERR-SUBSYSTEM | PIC X(01) | 'A'=app, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=file |
| ERR-MESSAGE | PIC X(50) | Error message text |

### IMSFUNCS.cpy — IMS DL/I Function Codes
Constants for IMS calls: GU, GHU, GN, GHN, GNP, GHNP, REPL, ISRT, DLET.

### PCB Copybooks (PADFLPCB, PASFLPCB, PAUTBPCB)
IMS Program Communication Blocks containing DB name, segment level, status code, processing options, segment name, and key feedback area. One PCB per IMS database accessed.

---

## 9. DB2 Sub-Application Copybooks

### CSDB2RPY.cpy — DB2 Common Procedures
Contains reusable DB2 paragraphs: priming query (SELECT 1 FROM SYSIBM.SYSDUMMY1), error formatting via DSNTIAC utility, and SQL error handling.

### CSDB2RWY.cpy — DB2 Working Storage
Working storage for DB2 operations: SQLCODE display field, error status flags, formatted message areas.

### DCLTRTYP.dcl — Transaction Type DB2 Table Declaration
COBOL host variable declaration for TRNTYPE table (same fields as CVTRA03Y).

### DCLTRCAT.dcl — Transaction Category DB2 Table Declaration
COBOL host variable declaration for TRNCATG table (same fields as CVTRA04Y).

---

## 10. Entity Relationship Summary

```
Customer (CVCUS01Y)     1:N     Card-XREF (CVACT03Y)     N:1     Account (CVACT01Y)
    PK: CUST-ID          ────►    XREF-CUST-ID     ◄────         PK: ACCT-ID
                                   XREF-ACCT-ID
                                   XREF-CARD-NUM
                                        │
                                        │ 1:1
                                        ▼
                                Card (CVACT02Y)
                                 PK: CARD-NUM
                                 FK: CARD-ACCT-ID
                                        │
                                        │ 1:N
                                        ▼
                              Transaction (CVTRA05Y)
                                FK: TRAN-CARD-NUM
                                FK: TRAN-TYPE-CD  ──────► TranType (CVTRA03Y)
                                FK: TRAN-CAT-CD   ──────► TranCategory (CVTRA04Y)
                                                                │
                                                    TranCatBalance (CVTRA01Y)
                                                    DisclosureGroup (CVTRA02Y)

        IMS Hierarchy:
        Auth Summary (CIPAUSMY)  ──parent──►  Auth Detail (CIPAUDTY)
           FK: PA-ACCT-ID                      FK: PA-CARD-NUM

        Security:
        User (CSUSR01Y) — standalone, no FK relationships
```
