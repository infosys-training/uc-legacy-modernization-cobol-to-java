# DATA DICTIONARY — CardDemo COBOL Estate

> **Generated:** 2026-05-27  
> **Repository:** `uc-legacy-modernization-cobol-to-java`  
> **Source:** `app/cpy/` — 30 copybooks defining all data structures

---

## 1. ACCOUNT Entity

### 1.1 CVACT01Y.cpy — Account Record (RECLN 300)

| Level | Field Name | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------------|------|-----------------|------------|
| 01 | ACCOUNT-RECORD | — | Group | Root account record | Fixed 300 bytes |
| 05 | ACCT-ID | 9(11) | Numeric | Unique account identifier (11-digit) | Must be numeric |
| 05 | ACCT-ACTIVE-STATUS | X(01) | Alpha | Account status flag | 'Y'=Active, 'N'=Inactive |
| 05 | ACCT-CURR-BAL | S9(10)V99 | Signed Decimal | Current account balance | Signed, 2 decimal places |
| 05 | ACCT-CREDIT-LIMIT | S9(10)V99 | Signed Decimal | Credit limit for the account | Must be > 0 |
| 05 | ACCT-CASH-CREDIT-LIMIT | S9(10)V99 | Signed Decimal | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| 05 | ACCT-OPEN-DATE | X(10) | Date String | Date account was opened | Format: YYYY-MM-DD |
| 05 | ACCT-EXPIRAION-DATE | X(10) | Date String | Account expiration date | Format: YYYY-MM-DD |
| 05 | ACCT-REISSUE-DATE | X(10) | Date String | Last card reissue date | Format: YYYY-MM-DD |
| 05 | ACCT-CURR-CYC-CREDIT | S9(10)V99 | Signed Decimal | Current cycle credits (payments received) | Running total |
| 05 | ACCT-CURR-CYC-DEBIT | S9(10)V99 | Signed Decimal | Current cycle debits (charges made) | Running total |
| 05 | ACCT-ADDR-ZIP | X(10) | Alpha | Account holder's ZIP/postal code | — |
| 05 | ACCT-GROUP-ID | X(10) | Alpha | Disclosure group ID — links to interest rate tables | FK → DIS-GROUP-RECORD |
| 05 | FILLER | X(178) | Filler | Reserved for future use | Pad to 300 bytes |

### 1.2 CVACT02Y.cpy — Card Record (RECLN 150)

| Level | Field Name | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------------|------|-----------------|------------|
| 01 | CARD-RECORD | — | Group | Root card record | Fixed 150 bytes |
| 05 | CARD-NUM | X(16) | Alpha | Card number (16-digit PAN) | Typically numeric, stored as alpha |
| 05 | CARD-ACCT-ID | 9(11) | Numeric | Linked account ID | FK → ACCOUNT-RECORD.ACCT-ID |
| 05 | CARD-CVV-CD | 9(03) | Numeric | Card verification value (CVV) | 3-digit numeric |
| 05 | CARD-EMBOSSED-NAME | X(50) | Alpha | Cardholder name as printed on card | — |
| 05 | CARD-EXPIRAION-DATE | X(10) | Date String | Card expiration date | Format: YYYY-MM-DD |
| 05 | CARD-ACTIVE-STATUS | X(01) | Alpha | Card status flag | 'Y'=Active, 'N'=Inactive |
| 05 | FILLER | X(59) | Filler | Reserved | Pad to 150 bytes |

### 1.3 CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Level | Field Name | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------------|------|-----------------|------------|
| 01 | CARD-XREF-RECORD | — | Group | Maps card → customer + account | Fixed 50 bytes |
| 05 | XREF-CARD-NUM | X(16) | Alpha | Card number (primary key) | FK → CARD-RECORD.CARD-NUM |
| 05 | XREF-CUST-ID | 9(09) | Numeric | Customer ID owning this card | FK → CUSTOMER-RECORD.CUST-ID |
| 05 | XREF-ACCT-ID | 9(11) | Numeric | Account ID linked to this card | FK → ACCOUNT-RECORD.ACCT-ID |
| 05 | FILLER | X(14) | Filler | Reserved | Pad to 50 bytes |

---

## 2. CUSTOMER Entity

### 2.1 CVCUS01Y.cpy — Customer Record (RECLN 500)

| Level | Field Name | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------------|------|-----------------|------------|
| 01 | CUSTOMER-RECORD | — | Group | Root customer record | Fixed 500 bytes |
| 05 | CUST-ID | 9(09) | Numeric | Unique customer identifier | Must be numeric, 9 digits |
| 05 | CUST-FIRST-NAME | X(25) | Alpha | Customer first name | — |
| 05 | CUST-MIDDLE-NAME | X(25) | Alpha | Customer middle name | — |
| 05 | CUST-LAST-NAME | X(25) | Alpha | Customer last name | — |
| 05 | CUST-ADDR-LINE-1 | X(50) | Alpha | Address line 1 | — |
| 05 | CUST-ADDR-LINE-2 | X(50) | Alpha | Address line 2 | — |
| 05 | CUST-ADDR-LINE-3 | X(50) | Alpha | Address line 3 | — |
| 05 | CUST-ADDR-STATE-CD | X(02) | Alpha | US state code | Validated against CSLKPCDY state code list |
| 05 | CUST-ADDR-COUNTRY-CD | X(03) | Alpha | Country code | — |
| 05 | CUST-ADDR-ZIP | X(10) | Alpha | ZIP/postal code | Validated via CSLKPCDY state+zip lookup |
| 05 | CUST-PHONE-NUM-1 | X(15) | Alpha | Primary phone number | Area code validated via CSLKPCDY |
| 05 | CUST-PHONE-NUM-2 | X(15) | Alpha | Secondary phone number | Area code validated via CSLKPCDY |
| 05 | CUST-SSN | 9(09) | Numeric | Social Security Number | 9-digit numeric |
| 05 | CUST-GOVT-ISSUED-ID | X(20) | Alpha | Government-issued ID | — |
| 05 | CUST-DOB-YYYY-MM-DD | X(10) | Date String | Date of birth | Format: YYYY-MM-DD, validated via CSUTLDPY |
| 05 | CUST-EFT-ACCOUNT-ID | X(10) | Alpha | Electronic Funds Transfer account for auto-pay | — |
| 05 | CUST-PRI-CARD-HOLDER-IND | X(01) | Alpha | Primary cardholder indicator | 'Y'/'N' |
| 05 | CUST-FICO-CREDIT-SCORE | 9(03) | Numeric | FICO credit score | Range: 300–850 |
| 05 | FILLER | X(168) | Filler | Reserved | Pad to 500 bytes |

### 2.2 CUSTREC.cpy — Customer Record (Alternate Layout)

Identical field structure to CVCUS01Y.cpy but with different formatting. Used by CBSTM03A (statement generation). Notable difference: `CUST-DOB-YYYYMMDD` vs. `CUST-DOB-YYYY-MM-DD`.

---

## 3. TRANSACTION Entities

### 3.1 CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Level | Field Name | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------------|------|-----------------|------------|
| 01 | TRAN-RECORD | — | Group | Primary transaction record | Fixed 350 bytes |
| 05 | TRAN-ID | X(16) | Alpha | Unique transaction identifier | System-generated |
| 05 | TRAN-TYPE-CD | X(02) | Alpha | Transaction type code | FK → TRAN-TYPE-RECORD.TRAN-TYPE |
| 05 | TRAN-CAT-CD | 9(04) | Numeric | Transaction category code | FK → TRAN-CAT-RECORD |
| 05 | TRAN-SOURCE | X(10) | Alpha | Transaction source/channel | e.g., 'POS', 'ONLINE', 'ATM' |
| 05 | TRAN-DESC | X(100) | Alpha | Transaction description/narrative | — |
| 05 | TRAN-AMT | S9(09)V99 | Signed Decimal | Transaction amount | Signed; credits positive, debits negative |
| 05 | TRAN-MERCHANT-ID | 9(09) | Numeric | Merchant identifier | — |
| 05 | TRAN-MERCHANT-NAME | X(50) | Alpha | Merchant business name | — |
| 05 | TRAN-MERCHANT-CITY | X(50) | Alpha | Merchant city | — |
| 05 | TRAN-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP code | — |
| 05 | TRAN-CARD-NUM | X(16) | Alpha | Card number used for transaction | FK → CARD-RECORD.CARD-NUM |
| 05 | TRAN-ORIG-TS | X(26) | Alpha | Original transaction timestamp | ISO-8601-like |
| 05 | TRAN-PROC-TS | X(26) | Alpha | Processing timestamp | ISO-8601-like |
| 05 | FILLER | X(20) | Filler | Reserved | Pad to 350 bytes |

### 3.2 CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Identical structure to CVTRA05Y but with `DALYTRAN-` prefix. Represents unposted daily transactions pending validation and posting. Fields mirror TRAN-RECORD 1:1.

### 3.3 CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Level | Field Name | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------------|------|-----------------|------------|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | Running balance per account/type/category | Fixed 50 bytes |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRANCAT-ACCT-ID | 9(11) | Numeric | Account ID | FK → ACCOUNT-RECORD |
| 10 | TRANCAT-TYPE-CD | X(02) | Alpha | Transaction type code | FK → TRAN-TYPE-RECORD |
| 10 | TRANCAT-CD | 9(04) | Numeric | Transaction category code | FK → TRAN-CAT-RECORD |
| 05 | TRAN-CAT-BAL | S9(09)V99 | Signed Decimal | Cumulative balance for this acct/type/category | Used in interest calculation |
| 05 | FILLER | X(22) | Filler | Reserved | Pad to 50 bytes |

### 3.4 CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Level | Field Name | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------------|------|-----------------|------------|
| 01 | DIS-GROUP-RECORD | — | Group | Interest rate definition per group/type/category | Fixed 50 bytes |
| 05 | DIS-GROUP-KEY | — | Group | Composite key | — |
| 10 | DIS-ACCT-GROUP-ID | X(10) | Alpha | Account group identifier | FK → ACCOUNT-RECORD.ACCT-GROUP-ID |
| 10 | DIS-TRAN-TYPE-CD | X(02) | Alpha | Transaction type code | — |
| 10 | DIS-TRAN-CAT-CD | 9(04) | Numeric | Transaction category code | — |
| 05 | DIS-INT-RATE | S9(04)V99 | Signed Decimal | Annual interest rate percentage | e.g., 021599 = 21.59% |
| 05 | FILLER | X(28) | Filler | Reserved | Pad to 50 bytes |

### 3.5 CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Level | Field Name | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------------|------|-----------------|------------|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type lookup | Fixed 60 bytes |
| 05 | TRAN-TYPE | X(02) | Alpha | Type code (primary key) | e.g., '01'=Purchase, '02'=Return |
| 05 | TRAN-TYPE-DESC | X(50) | Alpha | Human-readable description | — |
| 05 | FILLER | X(08) | Filler | Reserved | Pad to 60 bytes |

### 3.6 CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Level | Field Name | PIC Clause | Type | Business Meaning | Validation |
|-------|-----------|------------|------|-----------------|------------|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category lookup | Fixed 60 bytes |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRAN-TYPE-CD | X(02) | Alpha | Parent transaction type code | FK → TRAN-TYPE-RECORD |
| 10 | TRAN-CAT-CD | 9(04) | Numeric | Category code within type | — |
| 05 | TRAN-CAT-TYPE-DESC | X(50) | Alpha | Category description | — |
| 05 | FILLER | X(04) | Filler | Reserved | Pad to 60 bytes |

---

## 4. REPORTING Entities

### 4.1 CVTRA07Y.cpy — Transaction Detail Report Layout (73 lines)

| Level | Field Name | PIC Clause | Type | Business Meaning |
|-------|-----------|------------|------|-----------------|
| 01 | REPORT-NAME-HEADER | — | Group | Report header block |
| 05 | REPT-SHORT-NAME | X(38) | Alpha | Short name: 'DALYREPT' |
| 05 | REPT-LONG-NAME | X(41) | Alpha | 'Daily Transaction Report' |
| 05 | REPT-START-DATE | X(10) | Date | Report date range start |
| 05 | REPT-END-DATE | X(10) | Date | Report date range end |
| 01 | TRANSACTION-DETAIL-REPORT | — | Group | Detail line layout |
| 05 | TRAN-REPORT-TRANS-ID | X(16) | Alpha | Transaction ID |
| 05 | TRAN-REPORT-ACCOUNT-ID | X(11) | Alpha | Account ID |
| 05 | TRAN-REPORT-TYPE-CD | X(02) | Alpha | Type code |
| 05 | TRAN-REPORT-TYPE-DESC | X(15) | Alpha | Type description |
| 05 | TRAN-REPORT-CAT-CD | 9(04) | Numeric | Category code |
| 05 | TRAN-REPORT-CAT-DESC | X(29) | Alpha | Category description |
| 05 | TRAN-REPORT-SOURCE | X(10) | Alpha | Source channel |
| 05 | TRAN-REPORT-AMT | -ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Formatted amount |
| 01 | REPORT-PAGE-TOTALS | — | Group | Page total line |
| 05 | REPT-PAGE-TOTAL | +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Page subtotal |
| 01 | REPORT-ACCOUNT-TOTALS | — | Group | Account total line |
| 05 | REPT-ACCOUNT-TOTAL | +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Per-account total |
| 01 | REPORT-GRAND-TOTALS | — | Group | Grand total line |
| 05 | REPT-GRAND-TOTAL | +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Overall total |

### 4.2 COSTM01.CPY — Statement Transaction Record (38 lines)

| Level | Field Name | PIC Clause | Type | Business Meaning |
|-------|-----------|------------|------|-----------------|
| 01 | TRNX-RECORD | — | Group | Transaction keyed by card+ID for statements |
| 05 | TRNX-KEY | — | Group | Composite key |
| 10 | TRNX-CARD-NUM | X(16) | Alpha | Card number |
| 10 | TRNX-ID | X(16) | Alpha | Transaction ID |
| 05 | TRNX-REST | — | Group | Non-key fields |
| 10 | TRNX-TYPE-CD | X(02) | Alpha | Transaction type |
| 10 | TRNX-CAT-CD | 9(04) | Numeric | Category code |
| 10 | TRNX-SOURCE | X(10) | Alpha | Source |
| 10 | TRNX-DESC | X(100) | Alpha | Description |
| 10 | TRNX-AMT | S9(09)V99 | Signed Decimal | Amount |
| 10 | TRNX-MERCHANT-ID | 9(09) | Numeric | Merchant ID |
| 10 | TRNX-MERCHANT-NAME | X(50) | Alpha | Merchant name |
| 10 | TRNX-MERCHANT-CITY | X(50) | Alpha | Merchant city |
| 10 | TRNX-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP |
| 10 | TRNX-ORIG-TS | X(26) | Alpha | Original timestamp |
| 10 | TRNX-PROC-TS | X(26) | Alpha | Processing timestamp |

---

## 5. EXPORT/IMPORT Entity

### 5.1 CVEXPORT.cpy — Multi-Record Export Layout (103 lines)

| Level | Field Name | PIC Clause | Type | Business Meaning |
|-------|-----------|------------|------|-----------------|
| 01 | EXPORT-RECORD | — | Group | Envelope for all export record types (500 bytes) |
| 05 | EXPORT-REC-TYPE | X(1) | Alpha | Record type discriminator: 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card |
| 05 | EXPORT-TIMESTAMP | X(26) | Alpha | Export timestamp |
| 05 | EXPORT-SEQUENCE-NUM | 9(9) COMP | Binary | Sequence number within export batch |
| 05 | EXPORT-BRANCH-ID | X(4) | Alpha | Target branch identifier |
| 05 | EXPORT-REGION-CODE | X(5) | Alpha | Target region code |
| 05 | EXPORT-RECORD-DATA | X(460) | Alpha | Record payload (REDEFINES for each type) |

**REDEFINES — Customer Data (`EXPORT-CUSTOMER-DATA`):**

| Level | Field Name | PIC Clause | Notes |
|-------|-----------|------------|-------|
| 10 | EXP-CUST-ID | 9(09) COMP | Binary for storage optimization |
| 10 | EXP-CUST-FIRST-NAME | X(25) | — |
| 10 | EXP-CUST-MIDDLE-NAME | X(25) | — |
| 10 | EXP-CUST-LAST-NAME | X(25) | — |
| 10 | EXP-CUST-ADDR-LINES | OCCURS 3 | Array of 3 × X(50) address lines |
| 10 | EXP-CUST-SSN | 9(09) | — |
| 10 | EXP-CUST-FICO-CREDIT-SCORE | 9(03) COMP-3 | Packed decimal |

**REDEFINES — Account Data (`EXPORT-ACCOUNT-DATA`):**

| Level | Field Name | PIC Clause | Notes |
|-------|-----------|------------|-------|
| 10 | EXP-ACCT-ID | 9(11) | — |
| 10 | EXP-ACCT-CURR-BAL | S9(10)V99 COMP-3 | Packed for storage |
| 10 | EXP-ACCT-CASH-CREDIT-LIMIT | S9(10)V99 COMP-3 | Packed |
| 10 | EXP-ACCT-CURR-CYC-DEBIT | S9(10)V99 COMP | Binary |

**REDEFINES — Transaction Data (`EXPORT-TRANSACTION-DATA`):**

| Level | Field Name | PIC Clause | Notes |
|-------|-----------|------------|-------|
| 10 | EXP-TRAN-AMT | S9(09)V99 COMP-3 | Packed for efficiency |
| 10 | EXP-TRAN-MERCHANT-ID | 9(09) COMP | Binary |

_(Card and Cross-Reference REDEFINES follow same pattern as CVACT02Y/CVACT03Y)_

---

## 6. UI / COMMUNICATION Entities

### 6.1 CVCRD01Y.cpy — Credit Card Work Areas (46 lines)

| Level | Field Name | PIC Clause | Type | Business Meaning |
|-------|-----------|------------|------|-----------------|
| 01 | CC-WORK-AREAS | — | Group | CICS communication area for card operations |
| 10 | CCARD-AID | X(5) | Alpha | Attention Identifier (key pressed) |
| 88 | CCARD-AID-ENTER | VALUE 'ENTER' | Condition | Enter key pressed |
| 88 | CCARD-AID-CLEAR | VALUE 'CLEAR' | Condition | Clear key pressed |
| 88 | CCARD-AID-PFK01–12 | VALUE 'PFKnn' | Condition | Function keys 1–12 |
| 10 | CCARD-NEXT-PROG | X(8) | Alpha | Next program to XCTL to |
| 10 | CCARD-NEXT-MAPSET | X(7) | Alpha | Next BMS mapset |
| 10 | CCARD-NEXT-MAP | X(7) | Alpha | Next BMS map |
| 10 | CCARD-ERROR-MSG | X(75) | Alpha | Error message for display |
| 10 | CCARD-RETURN-MSG | X(75) | Alpha | Return/informational message |
| 10 | CC-ACCT-ID / CC-ACCT-ID-N | X(11) / 9(11) | Alpha/Numeric | Current account context |
| 10 | CC-CARD-NUM / CC-CARD-NUM-N | X(16) / 9(16) | Alpha/Numeric | Current card context |
| 10 | CC-CUST-ID / CC-CUST-ID-N | X(09) / 9(9) | Alpha/Numeric | Current customer context |

### 6.2 COCOM01Y.cpy — Common Communication Area (47 lines)

Defines `CDEMO-CDA-AREA` — the CICS COMMAREA passed between all online programs. Contains:
- Program name, mapset, map identifiers
- User ID and type (Regular/Admin)
- Re-entry flag, confirmation code
- Current account, card, customer context
- Error/information messages

### 6.3 COMEN02Y.cpy — Menu Options (101 lines)

Defines the 12-entry menu structure for regular users:

| Option | Description | Program | User Type |
|--------|------------|---------|-----------|
| 01 | Account View | COACTVWC | U |
| 02 | Account Update | COACTUPC | U |
| 03 | Credit Card List | COCRDLIC | U |
| 04 | Credit Card View | COCRDSLC | U |
| 05 | Credit Card Update | COCRDUPC | U |
| 06 | Transaction List | COTRN00C | U |
| 07 | Transaction View | COTRN01C | U |
| 08 | Transaction Add | COTRN02C | U |
| 09 | Transaction Reports | CORPT00C | U |
| 10 | Bill Payment | COBIL00C | U |
| 11 | Pending Authorization View | COPAUS0C | U |

### 6.4 COADM02Y.cpy — Admin Menu Options (62 lines)

Defines the admin menu structure with user management (COUSR00C–03C) options.

---

## 7. UTILITY / INFRASTRUCTURE Entities

### 7.1 CSUSR01Y.cpy — User Security Record (26 lines)

| Level | Field Name | PIC Clause | Type | Business Meaning |
|-------|-----------|------------|------|-----------------|
| 01 | SEC-USER-DATA | — | Group | User authentication record (RECLN 80) |
| 05 | SEC-USR-ID | X(08) | Alpha | User login ID |
| 05 | SEC-USR-FNAME | X(20) | Alpha | First name |
| 05 | SEC-USR-LNAME | X(20) | Alpha | Last name |
| 05 | SEC-USR-PWD | X(08) | Alpha | Password (plaintext) |
| 05 | SEC-USR-TYPE | X(01) | Alpha | User type: 'R'=Regular, 'A'=Admin |
| 05 | SEC-USR-FILLER | X(23) | Filler | Reserved |

### 7.2 CSDAT01Y.cpy — Date/Time Work Area (58 lines)

| Level | Field Name | PIC Clause | Type | Business Meaning |
|-------|-----------|------------|------|-----------------|
| 01 | WS-DATE-TIME | — | Group | Current date/time storage |
| 10 | WS-CURDATE | 9(08) | Numeric | YYYYMMDD |
| 10 | WS-CURTIME | 9(08) | Numeric | HHMMSSMS |
| 05 | WS-CURDATE-MM-DD-YY | — | Group | Formatted date for display |
| 05 | WS-CURTIME-HH-MM-SS | — | Group | Formatted time for display |
| 05 | WS-TIMESTAMP | — | Group | Full ISO-like timestamp (YYYY-MM-DD HH:MM:SS.ssssss) |

### 7.3 CSUTLDWY.cpy — Date Validation Working Storage (89 lines)

| Level | Field Name | PIC Clause | Type | Business Meaning |
|-------|-----------|------------|------|-----------------|
| 10 | WS-EDIT-DATE-CCYYMMDD | — | Group | Date being validated |
| 25 | WS-EDIT-DATE-CC | X(2) / 9(2) | Alpha/Numeric | Century (88 THIS-CENTURY=20, LAST-CENTURY=19) |
| 25 | WS-EDIT-DATE-YY | X(2) / 9(2) | Alpha/Numeric | Year within century |
| 20 | WS-EDIT-DATE-MM | X(2) / 9(2) | Alpha/Numeric | Month (88 WS-VALID-MONTH: 1–12, WS-31-DAY-MONTH, WS-FEBRUARY) |
| 20 | WS-EDIT-DATE-DD | X(2) / 9(2) | Alpha/Numeric | Day (88 WS-VALID-DAY: 1–31, WS-DAY-31, WS-DAY-30, WS-DAY-29) |
| 10 | WS-EDIT-DATE-FLGS | — | Group | Validation result flags |
| 88 | WS-EDIT-DATE-IS-VALID | VALUE LOW-VALUES | Condition | Date passes all checks |
| 88 | WS-EDIT-DATE-IS-INVALID | VALUE '000' | Condition | Date fails validation |

### 7.4 CSUTLDPY.cpy — Date Validation Procedures (375 lines)

Procedure Division copybook implementing:
- `EDIT-DATE-CCYYMMDD` — Full date validation
- `EDIT-YEAR-CCYY` — Century/year validation (19xx/20xx)
- `EDIT-MONTH` — Month validation (1–12)
- `EDIT-DAY` — Day validation (28/29/30/31 depending on month, leap year logic)
- `EDIT-DATE-OF-BIRTH` — DOB-specific validation (must be in the past)

### 7.5 CSLKPCDY.cpy — Lookup Code Repository (1318 lines)

Contains validation lookup tables defined as 88-level conditions:

| Section | Field | Content |
|---------|-------|---------|
| Phone Area Codes | WS-US-PHONE-AREA-CODE-TO-EDIT (PIC XXX) | 88 VALID-PHONE-AREA-CODE — complete NANPA area code list |
| US State Codes | _(similar structure)_ | All 50 states + territories |
| State + ZIP Prefix | _(similar structure)_ | State code + first 2 digits of ZIP for validation |

### 7.6 CSSTRPFY.cpy — PFKey Store Procedure (85 lines)

Procedure Division copybook `YYYY-STORE-PFKEY`. Maps EIBAID (CICS attention identifier) to CCARD-AID values via EVALUATE TRUE. Handles ENTER, CLEAR, PA1, PA2, PF1–PF24.

### 7.7 CSSETATY.cpy — Field Attribute Setting (30 lines)

Procedure Division copybook template for setting field attributes (color = red, filler = '*') on validation errors. Uses template variables `(TESTVAR1)`, `(SCRNVAR2)`, `(MAPNAME3)` — expanded via COBOL REPLACE at compile time.

### 7.8 COTTL01Y.cpy — Screen Title (27 lines)

| Level | Field Name | PIC Clause | Value |
|-------|-----------|------------|-------|
| 01 | CCDA-SCREEN-TITLE | — | Group |
| 05 | CCDA-TITLE01 | X(40) | 'AWS Mainframe Modernization' |
| 05 | CCDA-TITLE02 | X(40) | 'CardDemo' |
| 05 | CCDA-THANK-YOU | X(40) | 'Thank you for using CCDA application...' |

### 7.9 CSMSG01Y.cpy — Common Messages (24 lines)

| Level | Field Name | PIC Clause | Value |
|-------|-----------|------------|-------|
| 05 | CCDA-MSG-THANK-YOU | X(50) | Thank-you message |
| 05 | CCDA-MSG-INVALID-KEY | X(50) | Invalid key pressed message |

### 7.10 CSMSG02Y.cpy — Abend Data (35 lines)

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|-----------------|
| 01 | ABEND-DATA | — | Abend (abnormal end) diagnostic area |
| 05 | ABEND-CODE | X(4) | Abend code |
| 05 | ABEND-CULPRIT | X(8) | Program that caused abend |
| 05 | ABEND-REASON | X(50) | Descriptive reason |
| 05 | ABEND-MSG | X(72) | Full error message |

### 7.11 CODATECN.cpy — Date Conversion (52 lines)

Working storage for date format conversion between COBOL internal dates and display formats. Used by CBACT01C for date formatting in batch output.

### 7.12 UNUSED1Y.cpy — Unused/Placeholder (10 lines)

Placeholder copybook with generic fields (UNUSED-ID, UNUSED-FNAME, etc.). Not referenced by any program.

---

## 8. Entity Relationship Summary

```
CUSTOMER-RECORD (CUST-ID)
    │
    ├── 1:N ── CARD-XREF-RECORD (XREF-CUST-ID → CUST-ID)
    │               │
    │               └── 1:1 ── CARD-RECORD (CARD-NUM = XREF-CARD-NUM)
    │               └── 1:1 ── ACCOUNT-RECORD (ACCT-ID = XREF-ACCT-ID)
    │
    └── (via XREF)

ACCOUNT-RECORD (ACCT-ID)
    │
    ├── N:1 ── DIS-GROUP-RECORD (ACCT-GROUP-ID → DIS-ACCT-GROUP-ID)
    │
    ├── 1:N ── TRAN-CAT-BAL-RECORD (TRANCAT-ACCT-ID → ACCT-ID)
    │
    └── 1:N ── TRAN-RECORD (via XREF → CARD-NUM → TRAN-CARD-NUM)

TRAN-RECORD
    │
    ├── N:1 ── TRAN-TYPE-RECORD (TRAN-TYPE-CD → TRAN-TYPE)
    │
    └── N:1 ── TRAN-CAT-RECORD (TRAN-TYPE-CD + TRAN-CAT-CD → TRAN-CAT-KEY)

SEC-USER-DATA (SEC-USR-ID)
    └── Independent entity — no FK to business data
```
