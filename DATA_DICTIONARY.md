# DATA DICTIONARY

## Overview

This document catalogs all copybooks in `app/cpy/` (30 files) and sub-application copybook directories. Fields are grouped by business entity with PIC clauses, data types, business meanings, and validation rules extracted from the source.

---

## 1. Account Entity

### Copybook: CVACT01Y.cpy
**Description:** Data structure for Account entity (record length 300 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric | Unique account identifier | 11-digit numeric, primary key for ACCTDATA VSAM |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account status flag | 'Y' = Active, 'N' = Inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance | Signed, 2 decimal places |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Maximum credit limit | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Account opening date | Format: YYYY-MM-DD |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | Format: YYYY-MM-DD, must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Card reissue date | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total | Accumulated during billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total | Accumulated during billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account billing ZIP code | US ZIP+4 format |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/interest rate group | Links to DIS-GROUP-RECORD |
| FILLER | PIC X(178) | Filler | Reserved space | Pad to 300 bytes |

---

## 2. Card Entity

### Copybook: CVACT02Y.cpy
**Description:** Data structure for Card entity (record length 150 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Credit card number | 16-digit card number, primary key for CARDDATA VSAM |
| CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | Must exist in ACCTDATA |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Cardholder name on card | Name as embossed |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card active/inactive flag | 'Y' = Active, 'N' = Inactive |
| FILLER | PIC X(59) | Filler | Reserved space | Pad to 150 bytes |

### Copybook: CVACT03Y.cpy
**Description:** Data structure for Card Cross-Reference (record length 50 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number (cross-reference key) | Must exist in CARDDATA |
| XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID linked to card | Must exist in CUSTDATA |
| XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID linked to card | Must exist in ACCTDATA |
| FILLER | PIC X(14) | Filler | Reserved space | Pad to 50 bytes |

### Copybook: CVCRD01Y.cpy
**Description:** Credit card work areas for online programs

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CCARD-AID | PIC X(5) | Alphanumeric | Attention Identifier key pressed | 88-levels: ENTER, CLEAR, PA1, PA2, PFK01-PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to transfer to | Valid program name |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | Next BMS mapset | Valid mapset name |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | Next BMS map | Valid map name |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message for display | Free text |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return/confirmation message | Free text |
| CC-ACCT-ID | PIC X(11) | Alphanumeric | Account ID work area | Numeric content, REDEFINES to PIC 9(11) |
| CC-CARD-NUM | PIC X(16) | Alphanumeric | Card number work area | Numeric content, REDEFINES to PIC 9(16) |
| CC-CUST-ID | PIC X(09) | Alphanumeric | Customer ID work area | Numeric content, REDEFINES to PIC 9(9) |

---

## 3. Customer Entity

### Copybook: CVCUS01Y.cpy
**Description:** Data structure for Customer entity (record length 500 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric | Unique customer identifier | 9-digit numeric, primary key for CUSTDATA VSAM |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | Non-blank |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | Non-blank |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | Non-blank |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated against CSLKPCDY state code list |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | 3-char ISO country code |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | Validated against CSLKPCDY ZIP prefix list |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated against CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Area code validated against CSLKPCDY |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit; validated in COACTUPC (format XXX-XX-XXXX) |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID number | Free text |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | Format: YYYY-MM-DD |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account for payments | Bank account reference |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder indicator | 'Y' = Primary, 'N' = Authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range: 300-850 |
| FILLER | PIC X(168) | Filler | Reserved space | Pad to 500 bytes |

### Copybook: CUSTREC.cpy
**Description:** Alternate customer record layout (same structure as CVCUS01Y)

Same field definitions as CVCUS01Y.cpy with minor naming difference (CUST-DOB-YYYYMMDD vs CUST-DOB-YYYY-MM-DD).

---

## 4. Transaction Entity

### Copybook: CVTRA05Y.cpy
**Description:** Data structure for Transaction record (record length 350 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | 16-char, auto-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Must exist in TRANTYPE file |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Must exist in TRANCATG file |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source system | e.g., 'POS', 'ATM', 'ONLINE' |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Positive=debit, Negative=credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | 9-digit numeric |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | Free text |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | Free text |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | ZIP format |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card used for transaction | Must exist in CARDXREF |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Transaction origination timestamp | ISO timestamp format |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | Transaction processing timestamp | Set by posting program |
| FILLER | PIC X(20) | Filler | Reserved space | Pad to 350 bytes |

### Copybook: CVTRA06Y.cpy
**Description:** Data structure for Daily Transaction record (record length 350 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | Daily transaction identifier | Same format as TRAN-ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Must exist in TRANTYPE file |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Must exist in TRANCATG file |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | Source system | Same as TRAN-SOURCE |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free text |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Same rules as TRAN-AMT |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | Same as TRAN-MERCHANT-ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | Free text |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | Free text |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | ZIP format |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Must exist in CARDXREF |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | Origination timestamp | ISO timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | Set during posting |
| FILLER | PIC X(20) | Filler | Reserved space | Pad to 350 bytes |

### Copybook: CVTRA01Y.cpy
**Description:** Data structure for Transaction Category Balance (record length 50 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID (composite key part 1) | Must exist in ACCTDATA |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type (composite key part 2) | Must exist in TRANTYPE |
| TRANCAT-CD | PIC 9(04) | Numeric | Category code (composite key part 3) | Must exist in TRANCATG |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance for this category | Updated by CBTRN02C |
| FILLER | PIC X(22) | Filler | Reserved space | Pad to 50 bytes |

### Copybook: CVTRA02Y.cpy
**Description:** Data structure for Disclosure Group (record length 50 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group identifier (key part 1) | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type (key part 2) | Valid type code |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (key part 3) | Valid category code |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate for this group/type/category | Percentage (e.g., 18.99) |
| FILLER | PIC X(28) | Filler | Reserved space | Pad to 50 bytes |

### Copybook: CVTRA03Y.cpy
**Description:** Data structure for Transaction Type (record length 60 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code | Primary key |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Type description | e.g., 'Purchase', 'Cash Advance' |
| FILLER | PIC X(08) | Filler | Reserved space | Pad to 60 bytes |

### Copybook: CVTRA04Y.cpy
**Description:** Data structure for Transaction Category Type (record length 60 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code (composite key part 1) | Must exist in TRANTYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (composite key part 2) | Unique within type |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | e.g., 'Retail', 'Travel' |
| FILLER | PIC X(04) | Filler | Reserved space | Pad to 60 bytes |

### Copybook: CVTRA07Y.cpy
**Description:** Report data structures for transaction detail report

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report short name | 'DALYREPT' |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report full title | 'Daily Transaction Report' |
| REPT-START-DATE | PIC X(10) | Alphanumeric | Report date range start | YYYY-MM-DD |
| REPT-END-DATE | PIC X(10) | Alphanumeric | Report date range end | YYYY-MM-DD |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID for report line | From TRAN-ID |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID for report line | From XREF |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code | From TRAN-TYPE-CD |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description (truncated) | Lookup from TRANTYPE |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code | From TRAN-CAT-CD |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description (truncated) | Lookup from TRANCATG |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Source | From TRAN-SOURCE |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount | Display format |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal | Running sum |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal | Running sum |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total | Final sum |

---

## 5. Security / User Entity

### Copybook: CSUSR01Y.cpy
**Description:** Data structure for User Security record (record length 80 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | User login ID | Primary key for USRSEC VSAM, unique |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name | Non-blank |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name | Non-blank |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | User password | Non-blank, stored in cleartext |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type/role | 'A' = Admin, 'U' = Regular User |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved space | Pad to 80 bytes |

---

## 6. Communication / Control Structures

### Copybook: COCOM01Y.cpy
**Description:** COMMAREA — inter-program communication area for CICS programs

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Originating CICS transaction ID | Valid CICS trans ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Calling program name | Valid program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target CICS transaction ID | Valid CICS trans ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name | Valid program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Current user ID | From sign-on |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type | 88: 'A' = Admin, 'U' = User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context state | 88: 0 = Enter, 1 = Re-enter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Customer ID in context | Passed between programs |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | Customer first name | Passed for display |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | Customer middle name | Passed for display |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | Customer last name | Passed for display |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Account ID in context | Passed between programs |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status | Active/Inactive |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Card number in context | Passed between programs |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last BMS map displayed | For screen return |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last BMS mapset used | For screen return |

---

## 7. Menu Configuration

### Copybook: COADM02Y.cpy
**Description:** Admin menu options configuration (6 options)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CDEMO-ADMIN-OPT-COUNT | PIC 9(02) | Numeric | Number of admin menu options | Currently 6 |
| CDEMO-ADMIN-OPT-NUM | PIC 9(02) | Numeric | Option sequence number | 1-6 |
| CDEMO-ADMIN-OPT-NAME | PIC X(35) | Alphanumeric | Option display name | Menu label |
| CDEMO-ADMIN-OPT-PGMNAME | PIC X(08) | Alphanumeric | Program to XCTL to | Valid program name |

**Admin Menu Options:**
1. User List (Security) → COUSR00C
2. User Add (Security) → COUSR01C
3. User Update (Security) → COUSR02C
4. User Delete (Security) → COUSR03C
5. Transaction Type List/Update (Db2) → COTRTLIC
6. Transaction Type Maintenance (Db2) → COTRTUPC

### Copybook: COMEN02Y.cpy
**Description:** Main menu options configuration (11 options)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CDEMO-MENU-OPT-COUNT | PIC 9(02) | Numeric | Number of main menu options | Currently 11 |
| CDEMO-MENU-OPT-NUM | PIC 9(02) | Numeric | Option sequence number | 1-11 |
| CDEMO-MENU-OPT-NAME | PIC X(35) | Alphanumeric | Option display name | Menu label |
| CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Alphanumeric | Target program | Valid program |
| CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Alphanumeric | Required user type | 'U' = Any user |

**Main Menu Options:**
1. Account View → COACTVWC
2. Account Update → COACTUPC
3. Credit Card List → COCRDLIC
4. Credit Card View → COCRDSLC
5. Credit Card Update → COCRDUPC
6. Transaction List → COTRN00C
7. Transaction View → COTRN01C
8. Transaction Add → COTRN02C
9. Transaction Reports → CORPT00C
10. Bill Payment → COBIL00C
11. Pending Authorization View → COPAUS0C

---

## 8. Date/Time Structures

### Copybook: CSDAT01Y.cpy
**Description:** Date/time working storage fields

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year | YYYY |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month | 01-12 |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day | 01-31 |
| WS-CURDATE-N | PIC 9(08) | Numeric (redefines) | Date as number | YYYYMMDD |
| WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours | 00-23 |
| WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minutes | 00-59 |
| WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current seconds | 00-59 |
| WS-CURTIME-MILSEC | PIC 9(02) | Numeric | Milliseconds | 00-99 |
| WS-TIMESTAMP | (composite) | Alphanumeric | Full ISO timestamp | YYYY-MM-DD HH:MM:SS.mmmmmm |

### Copybook: CODATECN.cpy
**Description:** Date conversion utility record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CODATECN-TYPE | PIC X | Alphanumeric | Input date format type | '1' = YYYYMMDD, '2' = YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date string | Based on type |
| CODATECN-OUTTYPE | PIC X | Alphanumeric | Output date format type | '1' = YYYY-MM-DD out, '2' = YYYYMMDD out |
| CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Output date string | Converted format |
| CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Error message if conversion fails | Blank = success |

---

## 9. Validation / Lookup Structures

### Copybook: CSLKPCDY.cpy
**Description:** Lookup code repository — 1,318 lines of validation data

Contains three validation tables:
1. **North America Phone Area Codes** — 88-level condition names for all valid NANPA area codes (201-999)
2. **US State Codes** — 88-level condition names for all valid 2-letter state abbreviations
3. **US State + ZIP Prefix** — 88-level conditions for first 2 digits of ZIP by state

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| WS-US-PHONE-AREA-CODE-TO-EDIT | PIC XXX | Alphanumeric | Area code to validate | 88 VALID-PHONE-AREA-CODE |
| WS-US-STATE-CD-TO-EDIT | PIC XX | Alphanumeric | State code to validate | 88 VALID-US-STATE-CODE |
| WS-US-STATE-ZIP-TO-EDIT | PIC X(04) | Alphanumeric | State + ZIP prefix | 88 VALID-US-STATE-ZIP |

### Copybook: CSSETATY.cpy
**Description:** Screen attribute setting template (used with REPLACING)

Template for setting field attributes (color, highlight) based on validation status. Used 39 times in COACTUPC.cbl via COPY REPLACING to set individual field error indicators.

### Copybook: CSSTRPFY.cpy
**Description:** String padding/formatting utility

Used to strip trailing spaces and format display fields.

---

## 10. Export/Import Structure

### Copybook: CVEXPORT.cpy
**Description:** Multi-record export layout for branch migration (record length 500 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | Record type indicator | 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp | ISO format |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Record sequence number | Auto-incremented |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Source branch identifier | Branch code |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Region code | Region identifier |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Polymorphic record data | REDEFINES per type |

**REDEFINES structures:** EXPORT-CUSTOMER-DATA, EXPORT-ACCOUNT-DATA, EXPORT-TRANSACTION-DATA, EXPORT-CARD-XREF-DATA, EXPORT-CARD-DATA — each maps the 460-byte area to the respective entity fields with COMP/COMP-3 optimizations.

---

## 11. Message / Display Structures

### Copybook: COTTL01Y.cpy
**Description:** Screen title constants

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CCDA-TITLE01 | PIC X(40) | Alphanumeric | Title line 1 | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | Alphanumeric | Title line 2 | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | Alphanumeric | Sign-off message | Thank you message |

### Copybook: CSMSG01Y.cpy
**Description:** Common messages

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CCDA-MSG-THANK-YOU | PIC X(50) | Alphanumeric | Thank you message | Static text |
| CCDA-MSG-INVALID-KEY | PIC X(50) | Alphanumeric | Invalid key message | Static text |

### Copybook: CSMSG02Y.cpy
**Description:** Abend (abnormal end) data area

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| ABEND-CODE | PIC X(4) | Alphanumeric | Abend code | CICS/system abend code |
| ABEND-CULPRIT | PIC X(8) | Alphanumeric | Program causing abend | Program name |
| ABEND-REASON | PIC X(50) | Alphanumeric | Reason description | Free text |
| ABEND-MSG | PIC X(72) | Alphanumeric | Full abend message | Formatted for display |

---

## 12. Statement Structures

### Copybook: COSTM01.CPY
**Description:** Statement processing work areas (used by CBSTM03A)

Contains working storage for statement generation including page breaks, totals, and formatting.

---

## 13. Utility Structures

### Copybook: CSUTLDPY.cpy
**Description:** Date utility parameters (used by CSUTLDTC)

### Copybook: CSUTLDWY.cpy
**Description:** Date utility working storage

### Copybook: UNUSED1Y.cpy
**Description:** Reserved/unused copybook placeholder

---

## 14. Sub-Application Copybooks

### Authorization Module (`app/app-authorization-ims-db2-mq/cpy/`)

| Copybook | Description | Key Fields |
|----------|-------------|-----------|
| CIPAUDTY.cpy | Authorization data type definition | Auth message fields, timestamps, amounts |
| CCPAURQY.cpy | Authorization request structure | Request card, amount, merchant |
| CCPAURLY.cpy | Authorization reply structure | Approval/decline code, reason |
| CIPAUSMY.cpy | Authorization summary structure | Summary counts, totals |
| CCPAUERY.cpy | Authorization error structure | Error codes, messages |
| IMSFUNCS.cpy | IMS DL/I function codes | GN, GNP, GU, ISRT function constants |
| PADFLPCB.CPY | IMS PCB for PADFLDBD database | DB PCB status, key feedback |
| PASFLPCB.CPY | IMS PCB for PASFLDBD database | DB PCB status, key feedback |
| PAUTBPCB.CPY | IMS PCB for PAUTBUNL PSB | Unload PCB definitions |

### Transaction Type DB2 Module (`app/app-transaction-type-db2/cpy/`)

| Copybook | Description | Key Fields |
|----------|-------------|-----------|
| CSDB2RPY.cpy | DB2 return/reason code structure | SQLCODE, SQLSTATE, error handling |
| CSDB2RWY.cpy | DB2 read/write working storage | Cursor management, fetch status |

---

## 15. Data Type Legend

| PIC Pattern | COBOL Type | Java Equivalent | Notes |
|------------|-----------|----------------|-------|
| PIC 9(n) | Unsigned numeric | int/long | Display numeric |
| PIC S9(n)V99 | Signed decimal | BigDecimal | 2 implied decimal places |
| PIC S9(n)V99 COMP-3 | Packed decimal | BigDecimal | BCD storage, half-byte per digit |
| PIC 9(n) COMP | Binary | int/long | Full-word binary |
| PIC X(n) | Alphanumeric | String | Fixed-length character |
| PIC X(n) VALUE | Constant | static final String | Initialized literal |
| 88 level | Condition name | enum/boolean | Named value checks |
