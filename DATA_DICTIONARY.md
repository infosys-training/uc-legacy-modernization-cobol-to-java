# DATA DICTIONARY — CardDemo Copybook Field Definitions

## Entity Grouping Overview

| Business Entity | Copybook(s) | Record Length | Key Field |
|----------------|-------------|--------------|-----------|
| Account | CVACT01Y.cpy | 300 bytes | ACCT-ID (9(11)) |
| Card | CVACT02Y.cpy | 150 bytes | CARD-NUM (X(16)) |
| Card-Account Cross-Reference | CVACT03Y.cpy | 50 bytes | XREF-CARD-NUM (X(16)) |
| Customer | CVCUS01Y.cpy | 500 bytes | CUST-ID (9(09)) |
| Transaction | CVTRA05Y.cpy | 350 bytes | TRAN-ID (X(16)) |
| Daily Transaction | CVTRA06Y.cpy | 350 bytes | DALYTRAN-ID (X(16)) |
| Transaction Category Balance | CVTRA01Y.cpy | 50 bytes | Composite: ACCT-ID + TYPE-CD + CAT-CD |
| Disclosure Group | CVTRA02Y.cpy | 50 bytes | Composite: GROUP-ID + TYPE-CD + CAT-CD |
| Transaction Type | CVTRA03Y.cpy | 60 bytes | TRAN-TYPE (X(02)) |
| Transaction Category | CVTRA04Y.cpy | 60 bytes | Composite: TYPE-CD + CAT-CD |
| Export Record | CVEXPORT.cpy | 500 bytes | EXPORT-REC-TYPE + SEQUENCE-NUM |
| Report Layout | CVTRA07Y.cpy | Variable | N/A (print format) |

---

## 1. Account Entity — `CVACT01Y.cpy`

Record: `ACCOUNT-RECORD` (01 level) — 300 bytes fixed-length

| Field Name | PIC Clause | Data Type | Offset | Business Meaning | Validation Rules |
|------------|-----------|-----------|--------|------------------|-----------------|
| ACCT-ID | 9(11) | Numeric | 0 | Unique account identifier | Must be 11-digit numeric; primary VSAM key |
| ACCT-ACTIVE-STATUS | X(01) | Alphanumeric | 11 | Account status flag | 'Y' = Active, 'N' = Closed |
| ACCT-CURR-BAL | S9(10)V99 | Signed Decimal | 12 | Current account balance | Signed; can be negative for overdraft |
| ACCT-CREDIT-LIMIT | S9(10)V99 | Signed Decimal | 24 | Credit limit | Must be positive; bal must not exceed |
| ACCT-CASH-CREDIT-LIMIT | S9(10)V99 | Signed Decimal | 36 | Cash advance limit | Subset of credit limit |
| ACCT-OPEN-DATE | X(10) | Date (YYYY-MM-DD) | 48 | Account opening date | Must be valid date format |
| ACCT-EXPIRAION-DATE | X(10) | Date (YYYY-MM-DD) | 58 | Account expiration date | Must be >= open date |
| ACCT-REISSUE-DATE | X(10) | Date (YYYY-MM-DD) | 68 | Card reissue date | Must be valid date |
| ACCT-CURR-CYC-CREDIT | S9(10)V99 | Signed Decimal | 78 | Current cycle credits (payments) | Always positive |
| ACCT-CURR-CYC-DEBIT | S9(10)V99 | Signed Decimal | 90 | Current cycle debits (charges) | Always positive |
| ACCT-ADDR-ZIP | X(10) | Alphanumeric | 102 | Billing ZIP code | Validated against CSUTLDPY lookups |
| ACCT-GROUP-ID | X(10) | Alphanumeric | 112 | Disclosure group membership | Links to DISCGRP file |
| FILLER | X(178) | Filler | 122 | Reserved for future use | Spaces on write |

---

## 2. Card Entity — `CVACT02Y.cpy`

Record: `CARD-RECORD` (01 level) — 150 bytes fixed-length

| Field Name | PIC Clause | Data Type | Offset | Business Meaning | Validation Rules |
|------------|-----------|-----------|--------|------------------|-----------------|
| CARD-NUM | X(16) | Alphanumeric | 0 | Credit card number | 16-digit; primary VSAM key; Luhn check implied |
| CARD-ACCT-ID | 9(11) | Numeric | 16 | Parent account ID | Must exist in ACCTFILE |
| CARD-CVV-CD | 9(03) | Numeric | 27 | Card verification value | 3-digit; not displayed in full on screens |
| CARD-EMBOSSED-NAME | X(50) | Alphanumeric | 30 | Name embossed on card | Max 50 chars; typically FIRST LAST |
| CARD-EXPIRAION-DATE | X(10) | Date (YYYY-MM-DD) | 80 | Card expiration | Must be future date for active cards |
| CARD-ACTIVE-STATUS | X(01) | Alphanumeric | 90 | Card active flag | 'Y' = Active, 'N' = Inactive |
| FILLER | X(59) | Filler | 91 | Reserved | Spaces |

---

## 3. Card-Account Cross-Reference — `CVACT03Y.cpy`

Record: `CARD-XREF-RECORD` (01 level) — 50 bytes fixed-length

| Field Name | PIC Clause | Data Type | Offset | Business Meaning | Validation Rules |
|------------|-----------|-----------|--------|------------------|-----------------|
| XREF-CARD-NUM | X(16) | Alphanumeric | 0 | Card number | Primary VSAM key; must exist in CARDFILE |
| XREF-CUST-ID | 9(09) | Numeric | 16 | Customer ID | Must exist in CUSTFILE |
| XREF-ACCT-ID | 9(11) | Numeric | 25 | Account ID | Must exist in ACCTFILE |
| FILLER | X(14) | Filler | 36 | Reserved | Spaces |

---

## 4. Customer Entity — `CVCUS01Y.cpy`

Record: `CUSTOMER-RECORD` (01 level) — 500 bytes fixed-length

| Field Name | PIC Clause | Data Type | Offset | Business Meaning | Validation Rules |
|------------|-----------|-----------|--------|------------------|-----------------|
| CUST-ID | 9(09) | Numeric | 0 | Unique customer identifier | Primary VSAM key; 9-digit |
| CUST-FIRST-NAME | X(25) | Alphanumeric | 9 | Customer first name | Non-blank required |
| CUST-MIDDLE-NAME | X(25) | Alphanumeric | 34 | Middle name | Optional |
| CUST-LAST-NAME | X(25) | Alphanumeric | 59 | Customer last name | Non-blank required |
| CUST-ADDR-LINE-1 | X(50) | Alphanumeric | 84 | Street address line 1 | Required |
| CUST-ADDR-LINE-2 | X(50) | Alphanumeric | 134 | Street address line 2 | Optional |
| CUST-ADDR-LINE-3 | X(50) | Alphanumeric | 184 | Street address line 3 | Optional |
| CUST-ADDR-STATE-CD | X(02) | Alphanumeric | 234 | US state code | Validated against CSUTLDPY state list |
| CUST-ADDR-COUNTRY-CD | X(03) | Alphanumeric | 236 | Country code | 'USA' default |
| CUST-ADDR-ZIP | X(10) | Alphanumeric | 239 | ZIP code (5+4) | Validated against CSUTLDPY |
| CUST-PHONE-NUM-1 | X(15) | Alphanumeric | 249 | Primary phone | Area code validated against CSLKPCDY |
| CUST-PHONE-NUM-2 | X(15) | Alphanumeric | 264 | Secondary phone | Area code validated against CSLKPCDY |
| CUST-SSN | 9(09) | Numeric | 279 | Social Security Number | 9-digit; validated format (not 000, not 666, not 9xx) |
| CUST-GOVT-ISSUED-ID | X(20) | Alphanumeric | 288 | Government ID | Optional |
| CUST-DOB-YYYY-MM-DD | X(10) | Date (YYYY-MM-DD) | 308 | Date of birth | Must be valid date; age >= 18 |
| CUST-EFT-ACCOUNT-ID | X(10) | Alphanumeric | 318 | EFT/ACH account for payments | Optional |
| CUST-PRI-CARD-HOLDER-IND | X(01) | Alphanumeric | 328 | Primary cardholder flag | 'Y' or 'N' |
| CUST-FICO-CREDIT-SCORE | 9(03) | Numeric | 329 | FICO credit score | Range 300-850 |
| FILLER | X(168) | Filler | 332 | Reserved | Spaces |

---

## 5. Transaction Entity — `CVTRA05Y.cpy`

Record: `TRAN-RECORD` (01 level) — 350 bytes fixed-length

| Field Name | PIC Clause | Data Type | Offset | Business Meaning | Validation Rules |
|------------|-----------|-----------|--------|------------------|-----------------|
| TRAN-ID | X(16) | Alphanumeric | 0 | Transaction unique ID | Primary VSAM key; system-generated |
| TRAN-TYPE-CD | X(02) | Alphanumeric | 16 | Transaction type code | Must exist in TRANTYPE file |
| TRAN-CAT-CD | 9(04) | Numeric | 18 | Transaction category code | Must exist in TRANCATG file |
| TRAN-SOURCE | X(10) | Alphanumeric | 22 | Transaction source/channel | e.g. 'ONLINE', 'POS', 'ATM' |
| TRAN-DESC | X(100) | Alphanumeric | 32 | Transaction description | Free text |
| TRAN-AMT | S9(09)V99 | Signed Decimal | 132 | Transaction amount | Signed; credits negative, debits positive |
| TRAN-MERCHANT-ID | 9(09) | Numeric | 143 | Merchant identifier | Numeric; 0 for non-merchant txns |
| TRAN-MERCHANT-NAME | X(50) | Alphanumeric | 152 | Merchant name | Required for POS transactions |
| TRAN-MERCHANT-CITY | X(50) | Alphanumeric | 202 | Merchant city | Optional |
| TRAN-MERCHANT-ZIP | X(10) | Alphanumeric | 252 | Merchant ZIP | Optional |
| TRAN-CARD-NUM | X(16) | Alphanumeric | 262 | Card used for transaction | Must exist in CARDXREF |
| TRAN-ORIG-TS | X(26) | Timestamp | 278 | Original transaction timestamp | ISO format: YYYY-MM-DD-HH.MM.SS.nnnnnn |
| TRAN-PROC-TS | X(26) | Timestamp | 304 | Processing timestamp | Set during batch posting |
| FILLER | X(20) | Filler | 330 | Reserved | Spaces |

---

## 6. Daily Transaction — `CVTRA06Y.cpy`

Record: `DALYTRAN-RECORD` (01 level) — 350 bytes fixed-length

Identical structure to CVTRA05Y but with DALYTRAN- prefix. Used for daily batch input before posting to master transaction file.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| DALYTRAN-ID | X(16) | Alphanumeric | Daily transaction ID |
| DALYTRAN-TYPE-CD | X(02) | Alphanumeric | Transaction type |
| DALYTRAN-CAT-CD | 9(04) | Numeric | Transaction category |
| DALYTRAN-SOURCE | X(10) | Alphanumeric | Source channel |
| DALYTRAN-DESC | X(100) | Alphanumeric | Description |
| DALYTRAN-AMT | S9(09)V99 | Signed Decimal | Amount |
| DALYTRAN-MERCHANT-ID | 9(09) | Numeric | Merchant ID |
| DALYTRAN-MERCHANT-NAME | X(50) | Alphanumeric | Merchant name |
| DALYTRAN-MERCHANT-CITY | X(50) | Alphanumeric | Merchant city |
| DALYTRAN-MERCHANT-ZIP | X(10) | Alphanumeric | Merchant ZIP |
| DALYTRAN-CARD-NUM | X(16) | Alphanumeric | Card number |
| DALYTRAN-ORIG-TS | X(26) | Timestamp | Original timestamp |
| DALYTRAN-PROC-TS | X(26) | Timestamp | Processing timestamp |
| FILLER | X(20) | Filler | Reserved |

---

## 7. Transaction Category Balance — `CVTRA01Y.cpy`

Record: `TRAN-CAT-BAL-RECORD` (01 level) — 50 bytes fixed-length

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY | (group) | Composite Key | — | Composite primary key |
| TRANCAT-ACCT-ID | 9(11) | Numeric | Account ID component | Must exist in ACCTFILE |
| TRANCAT-TYPE-CD | X(02) | Alphanumeric | Transaction type | Must exist in TRANTYPE |
| TRANCAT-CD | 9(04) | Numeric | Category code | Must exist in TRANCATG |
| TRAN-CAT-BAL | S9(09)V99 | Signed Decimal | Running balance for category | Updated by CBTRN02C and CBACT04C |
| FILLER | X(22) | Filler | Reserved | Spaces |

---

## 8. Disclosure Group — `CVTRA02Y.cpy`

Record: `DIS-GROUP-RECORD` (01 level) — 50 bytes fixed-length

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| DIS-GROUP-KEY | (group) | Composite Key | — | Composite primary key |
| DIS-ACCT-GROUP-ID | X(10) | Alphanumeric | Disclosure group identifier | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | X(02) | Alphanumeric | Transaction type for rate | Must exist in TRANTYPE |
| DIS-TRAN-CAT-CD | 9(04) | Numeric | Transaction category | Must exist in TRANCATG |
| DIS-INT-RATE | S9(04)V99 | Signed Decimal | Interest rate percentage | e.g. 001999 = 19.99% APR |
| FILLER | X(28) | Filler | Reserved | Spaces |

---

## 9. Transaction Type — `CVTRA03Y.cpy`

Record: `TRAN-TYPE-RECORD` (01 level) — 60 bytes fixed-length

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | X(02) | Alphanumeric | Type code (e.g. 'SA'=Sale, 'CR'=Credit) | Primary key; 2-char |
| TRAN-TYPE-DESC | X(50) | Alphanumeric | Human-readable description | Non-blank |
| FILLER | X(08) | Filler | Reserved | Spaces |

---

## 10. Transaction Category — `CVTRA04Y.cpy`

Record: `TRAN-CAT-RECORD` (01 level) — 60 bytes fixed-length

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY | (group) | Composite Key | — | Composite: type + category |
| TRAN-TYPE-CD | X(02) | Alphanumeric | Parent transaction type | FK to TRANTYPE |
| TRAN-CAT-CD | 9(04) | Numeric | Category code within type | Unique within type |
| TRAN-CAT-TYPE-DESC | X(50) | Alphanumeric | Category description | Non-blank |
| FILLER | X(04) | Filler | Reserved | Spaces |

---

## 11. Report Layout — `CVTRA07Y.cpy`

Print format structures for CBTRN03C daily transaction report.

| Structure | Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------|-----------------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME | X(38) | Report short name ('DALYREPT') |
| | REPT-LONG-NAME | X(41) | Full report title |
| | REPT-DATE-HEADER | X(12) | 'Date Range: ' label |
| | REPT-START-DATE | X(10) | Report start date |
| | REPT-END-DATE | X(10) | Report end date |
| TRANSACTION-DETAIL-REPORT | TRAN-REPORT-TRANS-ID | X(16) | Transaction ID |
| | TRAN-REPORT-ACCOUNT-ID | X(11) | Account ID |
| | TRAN-REPORT-TYPE-CD | X(02) | Type code |
| | TRAN-REPORT-TYPE-DESC | X(15) | Type description |
| | TRAN-REPORT-CAT-CD | 9(04) | Category code |
| | TRAN-REPORT-CAT-DESC | X(29) | Category description |
| | TRAN-REPORT-SOURCE | X(10) | Source |

---

## 12. Export Record — `CVEXPORT.cpy`

Multi-record export layout (500 bytes) using REDEFINES for polymorphic records:

### Header Fields (Common to all record types)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| EXPORT-REC-TYPE | X(1) | Alphanumeric | Record type: 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card |
| EXPORT-TIMESTAMP | X(26) | Timestamp | Export timestamp |
| EXPORT-SEQUENCE-NUM | 9(9) COMP | Binary | Sequence number for ordering |
| EXPORT-BRANCH-ID | X(4) | Alphanumeric | Originating branch |
| EXPORT-REGION-CODE | X(5) | Alphanumeric | Region code |
| EXPORT-RECORD-DATA | X(460) | Alphanumeric | Polymorphic data area |

### Customer Variant (EXPORT-CUSTOMER-DATA REDEFINES EXPORT-RECORD-DATA)

Uses COMP/COMP-3 for numeric fields (storage optimization vs. base copybook).

### Account Variant (EXPORT-ACCOUNT-DATA REDEFINES EXPORT-RECORD-DATA)

Mirrors CVACT01Y with COMP-3 for monetary fields.

### Transaction Variant (EXPORT-TRANSACTION-DATA REDEFINES EXPORT-RECORD-DATA)

Mirrors CVTRA05Y with COMP-3 for TRAN-AMT.

---

## 13. Common/Shared Copybooks

### COCOM01Y.cpy — Communication Area (COMMAREA)

Used by all CICS programs for inter-program data passing:

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| CDEMO-FROM-TRANID | X(4) | Calling transaction ID |
| CDEMO-FROM-PROGRAM | X(8) | Calling program name |
| CDEMO-TO-TRANID | X(4) | Target transaction ID |
| CDEMO-TO-PROGRAM | X(8) | Target program name |
| CDEMO-PGM-REENTER | X(1) | Re-entry flag |
| CDEMO-USER-ID | X(8) | Signed-on user ID |
| CDEMO-USER-TYPE | X(1) | 'A'=Admin, 'U'=User |

### CSUSR01Y.cpy — User Security Record

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| SEC-USR-ID | X(8) | User ID (primary key) |
| SEC-USR-FNAME | X(20) | First name |
| SEC-USR-LNAME | X(20) | Last name |
| SEC-USR-PWD | X(8) | Password (plain text) |
| SEC-USR-TYPE | X(1) | User type ('A'/'U') |

### CVCRD01Y.cpy — Card Work Areas (COMMAREA extension)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| CCARD-AID | X(5) | Attention ID (ENTER, CLEAR, PFKnn) |
| CCARD-NEXT-PROG | X(8) | Next program to transfer to |
| CCARD-NEXT-MAPSET | X(7) | Next BMS mapset |
| CCARD-NEXT-MAP | X(7) | Next BMS map |
| CCARD-ERROR-MSG | X(75) | Error message to display |
| CCARD-RETURN-MSG | X(75) | Return/success message |
| CC-ACCT-ID | X(11) | Working account ID |
| CC-CARD-NUM | X(16) | Working card number |
| CC-CUST-ID | X(09) | Working customer ID |

### COTTL01Y.cpy — Screen Title Layout

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| CCDA-TITLE01 | X(40) | Application title line 1 |
| CCDA-TITLE02 | X(40) | Application title line 2 |
| CCDA-TITLE-PGM-NAME | X(8) | Current program name |

### CSDAT01Y.cpy — Date Fields

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| WS-CURDATE | X(08) | Current date (YYYYMMDD) |
| WS-CURTIME | X(08) | Current time (HHMMSSss) |

### CSMSG01Y.cpy / CSMSG02Y.cpy — Message Areas

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| WS-MESSAGE | X(80) | Primary screen message |
| WS-ERR-FLG | X(01) | Error flag ('Y'/'N') |

---

## 14. Validation Copybooks

### CSLKPCDY.cpy — Lookup Code Validation

Contains extensive validation tables:
- **VALID-PHONE-AREA-CODE**: 350+ valid North American area codes (88-level condition names)
- **VALID-GENERAL-PURP-CODE**: Valid general purpose codes
- Purpose: Phone number validation in COACTUPC and account update programs

### CSUTLDWY.cpy — Day-of-Week Validation

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| WS-DAYS-DATA | X(63) | Monday through Sunday names |
| WS-DAY-NAME (OCCURS 7) | X(9) | Day name lookup |

### CSUTLDPY.cpy — Date/Utility Parameters

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| WS-DATE-TO-TEST | X(10) | Date input for validation |
| WS-DATE-RESULT | X(1) | Validation result ('V'=valid, 'I'=invalid) |
| WS-DATE-FORMAT | X(10) | Format spec (YYYY-MM-DD) |

### CUSTREC.cpy — Customer Record (Statement Generation)

Variant of CVCUS01Y used specifically by CBSTM03A for statement generation.

### COSTM01.CPY — Statement Control Record

Statement generation parameters for CBSTM03A.

---

## 15. Sub-Application Copybooks

### Authorization (IMS/DB2/MQ)

| Copybook | Business Meaning |
|----------|-----------------|
| CCPAUERY.cpy | Authorization query parameters |
| CCPAURLY.cpy | Authorization reply structure |
| CCPAURQY.cpy | Authorization request structure |
| CIPAUDTY.cpy | IMS authorization detail segment |
| CIPAUSMY.cpy | IMS authorization summary segment |
| IMSFUNCS.cpy | IMS DL/I function codes (GU, GN, GNP, REPL, DLET, ISRT) |
| PADFLPCB.cpy | IMS PCB (Program Communication Block) for detail DB |
| PASFLPCB.cpy | IMS PCB for summary DB |
| PAUTBPCB.cpy | IMS PCB for auth transaction DB |

### Transaction Type (DB2)

| Copybook | Business Meaning |
|----------|-----------------|
| CSDB2RPY.cpy | DB2 reply/result area (SQLCODE, row data) |
| CSDB2RWY.cpy | DB2 work area (SQL statement, host variables) |

---

## 16. Menu Option Copybooks

### COADM02Y.cpy — Admin Menu Options

| Option # | Option Name | Target Program | Function |
|----------|-------------|---------------|----------|
| 1 | User List | COUSR00C | Browse user security records |
| 2 | User Add | COUSR01C | Create new user |
| 3 | User Update | COUSR02C | Modify user |
| 4 | User Delete | COUSR03C | Remove user |
| 5 | Tran Type List | COTRTLIC | Browse transaction types (DB2) |
| 6 | Tran Type Update | COTRTUPC | Update transaction types (DB2) |

### COMEN02Y.cpy — Main Menu Options

| Option # | Option Name | Target Program | Function |
|----------|-------------|---------------|----------|
| 1 | Account View | COACTVWC | View account details |
| 2 | Account Update | COACTUPC | Edit account |
| 3 | Card List | COCRDLIC | Browse cards |
| 4 | Card View | COCRDSLC | View card detail |
| 5 | Card Update | COCRDUPC | Edit card |
| 6 | Bill Payment | COBIL00C | Process payment |
| 7 | Transaction List | COTRN00C | Browse transactions |
| 8 | Transaction View | COTRN01C | View transaction |
| 9 | Transaction Add | COTRN02C | Enter transaction |
| 10 | Batch Reports | CORPT00C | Submit report jobs |
| 11 | Admin Menu | COADM01C | Admin functions |

---

*Generated: 2026-06-19 | Source: infosys-training/uc-legacy-modernization-cobol-to-java*
