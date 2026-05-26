# DATA DICTIONARY — CardDemo COBOL Estate

> All copybooks from `app/cpy/` and sub-application `cpy/` directories. Fields are extracted from COBOL `01`-level record definitions with their PIC clauses, inferred data types, business meanings, and validation rules.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Master Record
_Used by: 16 programs (most-referenced data copybook)_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | ACCOUNT-RECORD | — | Group | Full account master record | — |
| 05 | ACCT-ID | 9(11) | Numeric | Unique account identifier | 11-digit numeric key |
| 05 | ACCT-ACTIVE-STATUS | X(01) | Alpha | Account active/inactive flag | 'Y'/'N' |
| 05 | ACCT-CURR-BAL | S9(10)V99 | Signed decimal | Current account balance | Signed, 2 decimal places |
| 05 | ACCT-CREDIT-LIMIT | S9(10)V99 | Signed decimal | Credit limit on account | Must be > 0 |
| 05 | ACCT-CASH-CREDIT-LIMIT | S9(10)V99 | Signed decimal | Cash advance credit limit | Must be > 0 |
| 05 | ACCT-OPEN-DATE | X(10) | Alpha-date | Date account was opened | YYYY-MM-DD format |
| 05 | ACCT-EXPIRAION-DATE | X(10) | Alpha-date | Account expiration date | YYYY-MM-DD format |
| 05 | ACCT-REISSUE-DATE | X(10) | Alpha-date | Card reissue date | YYYY-MM-DD format |
| 05 | ACCT-CURR-CYC-CREDIT | S9(10)V99 | Signed decimal | Current cycle credit total | Running total |
| 05 | ACCT-CURR-CYC-DEBIT | S9(10)V99 | Signed decimal | Current cycle debit total | Running total |
| 05 | ACCT-ADDR-ZIP | X(10) | Alpha | Account holder ZIP code | — |
| 05 | ACCT-GROUP-ID | X(10) | Alpha | Discount/account group code | Links to DISCGRP file |
| 05 | FILLER | X(178) | Alpha | Reserved | — |

### CVACT02Y.cpy — Card Data Record
_Used by: 10 programs_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARD-RECORD | — | Group | Credit card master record | — |
| 05 | CARD-NUM | X(16) | Alpha | Credit card number (PAN) | 16-character primary key |
| 05 | CARD-ACCT-ID | 9(11) | Numeric | Associated account ID | Must exist in ACCTDATA |
| 05 | CARD-CVV-CD | 9(03) | Numeric | Card verification value | 3-digit CVV |
| 05 | CARD-EMBOSSED-NAME | X(50) | Alpha | Name embossed on card | — |
| 05 | CARD-EXPIRAION-DATE | X(10) | Alpha-date | Card expiration date | YYYY-MM-DD |
| 05 | CARD-ACTIVE-STATUS | X(01) | Alpha | Card active/inactive status | 'Y'/'N' |
| 05 | FILLER | X(59) | Alpha | Reserved | — |

### CVACT03Y.cpy — Card Cross-Reference Record
_Used by: 16 programs_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARD-XREF-RECORD | — | Group | Card-to-account cross-reference | — |
| 05 | XREF-CARD-NUM | X(16) | Alpha | Credit card number | Primary key; links to CARDDATA |
| 05 | XREF-CUST-ID | 9(09) | Numeric | Customer ID | Links to CUSTDATA |
| 05 | XREF-ACCT-ID | 9(11) | Numeric | Account ID | Links to ACCTDATA; alternate key |
| 05 | FILLER | X(14) | Alpha | Reserved | — |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Master Record
_Used by: 10 programs_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CUSTOMER-RECORD | — | Group | Full customer master record | — |
| 05 | CUST-ID | 9(09) | Numeric | Unique customer identifier | 9-digit numeric key |
| 05 | CUST-FIRST-NAME | X(25) | Alpha | Customer first name | Alphabetic only |
| 05 | CUST-MIDDLE-NAME | X(25) | Alpha | Customer middle name | — |
| 05 | CUST-LAST-NAME | X(25) | Alpha | Customer last name | Alphabetic only |
| 05 | CUST-ADDR-LINE-1 | X(50) | Alpha | Address line 1 | — |
| 05 | CUST-ADDR-LINE-2 | X(50) | Alpha | Address line 2 | — |
| 05 | CUST-ADDR-LINE-3 | X(50) | Alpha | Address line 3 | — |
| 05 | CUST-ADDR-STATE-CD | X(02) | Alpha | US state code | Must be valid 2-letter state (via CSLKPCDY) |
| 05 | CUST-ADDR-COUNTRY-CD | X(03) | Alpha | Country code | — |
| 05 | CUST-ADDR-ZIP | X(10) | Alpha | ZIP/postal code | State+ZIP validated (via CSLKPCDY) |
| 05 | CUST-PHONE-NUM-1 | X(15) | Alpha | Primary phone number | Format: (NNN)NNN-NNNN; area code validated |
| 05 | CUST-PHONE-NUM-2 | X(15) | Alpha | Secondary phone number | Same validation as primary |
| 05 | CUST-SSN | 9(09) | Numeric | Social Security Number | 9-digit; alternate key in some definitions |
| 05 | CUST-GOVT-ISSUED-ID | X(20) | Alpha | Government-issued ID | — |
| 05 | CUST-DOB-YYYYMMDD | X(10) | Alpha-date | Date of birth | YYYY-MM-DD; validated by CSUTLDPY |
| 05 | CUST-EFT-ACCOUNT-ID | X(10) | Alpha | EFT/bank account ID | — |
| 05 | CUST-PRI-CARD-HOLDER-IND | X(01) | Alpha | Primary card holder indicator | 'Y'/'N' |
| 05 | CUST-FICO-CREDIT-SCORE | 9(03) | Numeric | FICO credit score | 300-850 range |
| 05 | FILLER | X(168) | Alpha | Reserved | — |

### CUSTREC.cpy — Customer Display Record (Statement Use)
_Used by: CBSTM03A_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CUSTOMER-RECORD | — | Group | Customer record for statement printing | — |
| 05 | CUST-ID | 9(09) | Numeric | Customer identifier | — |
| 05 | CUST-FIRST-NAME | X(25) | Alpha | First name | — |
| 05 | CUST-MIDDLE-NAME | X(25) | Alpha | Middle name | — |
| 05 | CUST-LAST-NAME | X(25) | Alpha | Last name | — |
| 05 | CUST-ADDR-LINE-1 | X(50) | Alpha | Address line 1 | — |
| 05 | CUST-ADDR-LINE-2 | X(50) | Alpha | Address line 2 | — |
| 05 | CUST-ADDR-LINE-3 | X(50) | Alpha | Address line 3 | — |
| 05 | CUST-ADDR-STATE-CD | X(02) | Alpha | State code | — |
| 05 | CUST-ADDR-COUNTRY-CD | X(03) | Alpha | Country code | — |
| 05 | CUST-ADDR-ZIP | X(10) | Alpha | ZIP code | — |

---

## 3. Transaction Entity

### CVTRA05Y.cpy — Transaction Master Record
_Used by: 11 programs_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-RECORD | — | Group | Transaction master record | — |
| 05 | TRAN-ID | X(16) | Alpha | Unique transaction ID | 16-char composite key |
| 05 | TRAN-TYPE-CD | X(02) | Alpha | Transaction type code | Links to TRANTYPE file |
| 05 | TRAN-CAT-CD | 9(04) | Numeric | Transaction category code | Links to TRANCATG file |
| 05 | TRAN-SOURCE | X(10) | Alpha | Transaction source system | — |
| 05 | TRAN-DESC | X(100) | Alpha | Transaction description | — |
| 05 | TRAN-AMT | S9(9)V99 | Signed decimal | Transaction amount | — |
| 05 | TRAN-CARD-NUM | X(16) | Alpha | Card number used | Links to CARDDATA |
| 05 | TRAN-MERCHANT-ID | 9(09) | Numeric | Merchant identifier | — |
| 05 | TRAN-MERCHANT-NAME | X(50) | Alpha | Merchant name | — |
| 05 | TRAN-MERCHANT-CITY | X(50) | Alpha | Merchant city | — |
| 05 | TRAN-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP code | — |
| 05 | TRAN-ORIG-TS | X(26) | Alpha | Origination timestamp | YYYY-MM-DD HH:MM:SS.ffffff |
| 05 | TRAN-PROC-TS | X(26) | Alpha | Processing timestamp | YYYY-MM-DD HH:MM:SS.ffffff |
| 05 | FILLER | X(20) | Alpha | Reserved | — |

### CVTRA06Y.cpy — Daily Transaction Input Record
_Used by: CBTRN01C, CBTRN02C_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | DALYTRAN-RECORD | — | Group | Daily transaction input record | — |
| 05 | DALYTRAN-ID | X(16) | Alpha | Transaction ID | Must be unique |
| 05 | DALYTRAN-TYPE-CD | X(02) | Alpha | Transaction type | Must exist in TRANTYPE |
| 05 | DALYTRAN-CAT-CD | 9(04) | Numeric | Transaction category | Must exist in TRANCATG |
| 05 | DALYTRAN-SOURCE | X(10) | Alpha | Source system | — |
| 05 | DALYTRAN-DESC | X(100) | Alpha | Description | — |
| 05 | DALYTRAN-AMT | S9(9)V99 | Signed decimal | Amount | — |
| 05 | DALYTRAN-CARD-NUM | X(16) | Alpha | Card used | Must exist in CARDXREF |
| 05 | DALYTRAN-MERCHANT-ID | 9(09) | Numeric | Merchant ID | — |
| 05 | DALYTRAN-MERCHANT-NAME | X(50) | Alpha | Merchant name | — |
| 05 | DALYTRAN-MERCHANT-CITY | X(50) | Alpha | Merchant city | — |
| 05 | DALYTRAN-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP | — |
| 05 | DALYTRAN-ORIG-TS | X(26) | Alpha | Origination timestamp | — |
| 05 | DALYTRAN-PROC-TS | X(26) | Alpha | Processing timestamp | — |

### CVTRA01Y.cpy — Transaction Type Record
_Used by: CBACT04C, CBTRN02C_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type definition | — |
| 05 | TRAN-TYPE | X(02) | Alpha | Transaction type code | Primary key |
| 05 | TRAN-TYPE-DESC | X(50) | Alpha | Type description | — |
| 05 | FILLER | X(48) | Alpha | Reserved | — |

### CVTRA02Y.cpy — Transaction Category Record
_Used by: CBACT04C_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category definition | — |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | TRAN-TYPE-CD | X(02) | Alpha | Transaction type | Links to TRANTYPE |
| 10 | TRAN-CAT-CD | 9(04) | Numeric | Category code | — |
| 05 | TRAN-CAT-TYPE-DESC | X(50) | Alpha | Category description | — |
| 05 | FILLER | X(44) | Alpha | Reserved | — |

### CVTRA03Y.cpy — Transaction Type Lookup (Report)
_Used by: CBTRN03C_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | FD-TRAN-TYPE-RECORD | — | Group | Transaction type for report lookup | — |
| 05 | FD-TRAN-TYPE | X(02) | Alpha | Type code key | — |
| 05 | FD-TRAN-TYPE-DESC | X(50) | Alpha | Type description | — |

### CVTRA04Y.cpy — Transaction Category Lookup (Report)
_Used by: CBTRN03C_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | FD-TRAN-CAT-RECORD | — | Group | Category record for report | — |
| 05 | FD-TRAN-CAT-KEY | — | Group | Composite key | — |
| 10 | FD-TRAN-TYPE-CD | X(02) | Alpha | Type code | — |
| 10 | FD-TRAN-CAT-CD | 9(04) | Numeric | Category code | — |
| 05 | FD-TRAN-CAT-TYPE-DESC | X(50) | Alpha | Description | — |

### CVTRA07Y.cpy — Report Date Parameters
_Used by: CBTRN03C_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | DATE-PARM-RECORD | — | Group | Date range for report | — |
| 05 | DATE-PARM-START | X(10) | Alpha-date | Report start date | YYYY-MM-DD |
| 05 | DATE-PARM-END | X(10) | Alpha-date | Report end date | YYYY-MM-DD |

---

## 4. Card/Account Cross-Reference Entity

### CVCRD01Y.cpy — Card Cross-Reference Display Record
_Used by: 7 programs (COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC)_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARD-XREF-RECORD | — | Group | Card cross-reference for CICS display | — |
| 05 | XREF-CARD-NUM | X(16) | Alpha | Card number | Primary key |
| 05 | XREF-CUST-ID | 9(09) | Numeric | Customer ID | Must exist in CUSTDATA |
| 05 | XREF-ACCT-ID | 9(11) | Numeric | Account ID | Must exist in ACCTDATA |

---

## 5. Security / User Entity

### CSUSR01Y.cpy — User Security Record
_Used by: 14 programs_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | SEC-USER-DATA | — | Group | User security record from USRSEC file | — |
| 05 | SEC-USR-ID | X(08) | Alpha | User login ID | Primary key; 8-char max |
| 05 | SEC-USR-FNAME | X(20) | Alpha | User first name | — |
| 05 | SEC-USR-LNAME | X(20) | Alpha | User last name | — |
| 05 | SEC-USR-PWD | X(08) | Alpha | User password | 8-char max (plaintext in file) |
| 05 | SEC-USR-TYPE | X(01) | Alpha | User type | 'A' = Admin, 'U' = Regular |
| 05 | SEC-USR-FILLER | X(23) | Alpha | Reserved | — |

---

## 6. Communication / Framework Entity

### COCOM01Y.cpy — COMMAREA (Inter-Program Communication)
_Used by: 21 programs (most-referenced copybook)_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARDDEMO-COMMAREA | — | Group | CICS communication area shared across all online programs | — |
| 05 | CDEMO-GENERAL-INFO | — | Group | General routing info | — |
| 10 | CDEMO-FROM-TRANID | X(04) | Alpha | Source transaction ID | — |
| 10 | CDEMO-FROM-PROGRAM | X(08) | Alpha | Source program name | — |
| 10 | CDEMO-TO-TRANID | X(04) | Alpha | Target transaction ID | — |
| 10 | CDEMO-TO-PROGRAM | X(08) | Alpha | Target program name | — |
| 10 | CDEMO-USER-ID | X(08) | Alpha | Current logged-in user | — |
| 10 | CDEMO-USER-TYPE | X(01) | Alpha | User type | 'A' or 'U' |
| 10 | CDEMO-PGM-CONTEXT | 9(01) | Numeric | Program re-entry flag | 0=enter, 1=reenter |
| 05 | CDEMO-CUSTOMER-INFO | — | Group | Customer context | — |
| 10 | CDEMO-CUST-ID | 9(09) | Numeric | Customer ID | — |
| 10 | CDEMO-CUST-FNAME | X(25) | Alpha | First name | — |
| 10 | CDEMO-CUST-MNAME | X(25) | Alpha | Middle name | — |
| 10 | CDEMO-CUST-LNAME | X(25) | Alpha | Last name | — |
| 05 | CDEMO-ACCOUNT-INFO | — | Group | Account context | — |
| 10 | CDEMO-ACCT-ID | 9(11) | Numeric | Account ID | — |
| 10 | CDEMO-ACCT-STATUS | X(01) | Alpha | Account status | — |
| 05 | CDEMO-CARD-INFO | — | Group | Card context | — |
| 10 | CDEMO-CARD-NUM | 9(16) | Numeric | Card number | — |
| 05 | CDEMO-MORE-INFO | — | Group | Screen tracking | — |
| 10 | CDEMO-LAST-MAP | X(7) | Alpha | Last BMS map sent | — |
| 10 | CDEMO-LAST-MAPSET | X(7) | Alpha | Last BMS mapset | — |

### COTTL01Y.cpy — Screen Titles
_Used by: 21 programs_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CCDA-SCREEN-TITLE | — | Group | Application title strings | — |
| 05 | CCDA-TITLE01 | X(40) | Alpha | Header line 1 | "AWS Mainframe Modernization" |
| 05 | CCDA-TITLE02 | X(40) | Alpha | Header line 2 | "CardDemo" |
| 05 | CCDA-THANK-YOU | X(40) | Alpha | Thank-you message | — |

### CSDAT01Y.cpy — Date/Time Working Storage
_Used by: 21 programs_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | WS-DATE-TIME | — | Group | Current date/time values | — |
| 05 | WS-CURDATE-YEAR | 9(04) | Numeric | Current year (YYYY) | — |
| 05 | WS-CURDATE-MONTH | 9(02) | Numeric | Current month (MM) | 01-12 |
| 05 | WS-CURDATE-DAY | 9(02) | Numeric | Current day (DD) | 01-31 |
| 05 | WS-CURTIME-HOURS | 9(02) | Numeric | Current hour | 00-23 |
| 05 | WS-CURTIME-MINUTE | 9(02) | Numeric | Current minute | 00-59 |
| 05 | WS-CURTIME-SECOND | 9(02) | Numeric | Current second | 00-59 |
| 05 | WS-TIMESTAMP | X(26) | Alpha | Full timestamp | YYYY-MM-DD HH:MM:SS.ffffff |

### CSMSG01Y.cpy — Standard Application Messages
_Used by: 21 programs_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CCDA-MSG-AREA | — | Group | Standard UI messages | — |
| 05 | CCDA-MSG-THANK-YOU | X(50) | Alpha | Session end message | — |
| 05 | CCDA-MSG-INVALID-KEY | X(50) | Alpha | Invalid PFKey message | — |

### CSMSG02Y.cpy — Abend Data
_Used by: 8 programs_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | ABEND-DATA | — | Group | Abend/error information | — |
| 05 | ABEND-CODE | X(4) | Alpha | Abend code | — |
| 05 | ABEND-CULPRIT | X(8) | Alpha | Program causing abend | — |
| 05 | ABEND-REASON | X(50) | Alpha | Abend reason text | — |
| 05 | ABEND-MSG | X(72) | Alpha | Full abend message | — |

---

## 7. Menu / Navigation Entity

### COADM02Y.cpy — Admin Menu Options
_Used by: COADM01C_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARDDEMO-ADMIN-MENU-OPTIONS | — | Group | Admin menu configuration | — |
| 05 | CDEMO-ADMIN-OPT-COUNT | 9(02) | Numeric | Number of admin options | Currently 6 |
| 10 | CDEMO-ADMIN-OPT-NUM | 9(02) | Numeric | Option number | — |
| 10 | CDEMO-ADMIN-OPT-NAME | X(35) | Alpha | Option display name | — |
| 10 | CDEMO-ADMIN-OPT-PGMNAME | X(08) | Alpha | Target program name | — |

### COMEN02Y.cpy — Main Menu Options
_Used by: COMEN01C_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARDDEMO-MAIN-MENU-OPTIONS | — | Group | Regular user menu configuration | — |
| 05 | CDEMO-MENU-OPT-COUNT | 9(02) | Numeric | Number of menu options | Currently 11 |
| 10 | CDEMO-MENU-OPT-NUM | 9(02) | Numeric | Option number | — |
| 10 | CDEMO-MENU-OPT-NAME | X(35) | Alpha | Display name | — |
| 10 | CDEMO-MENU-OPT-PGMNAME | X(08) | Alpha | Target program | — |
| 10 | CDEMO-MENU-OPT-USRTYPE | X(01) | Alpha | Required user type | 'U'/'A' |

---

## 8. Utility / Validation Entities

### CODATECN.cpy — Date Conversion Record
_Used by: CBACT01C_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CODATECN-REC | — | Group | Date format conversion I/O | — |
| 05 | CODATECN-TYPE | X | Alpha | Input format type | '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| 05 | CODATECN-INP-DATE | X(20) | Alpha | Input date string | — |
| 05 | CODATECN-OUTTYPE | X | Alpha | Output format type | '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| 05 | CODATECN-0UT-DATE | X(20) | Alpha | Output date string | — |
| 05 | CODATECN-ERROR-MSG | X(38) | Alpha | Error message | — |

### CSLKPCDY.cpy — Lookup Code Repository
_Used by: COACTUPC_

Contains validation tables for:
- **Phone area codes**: 88-level condition names for ~400 valid North American area codes
- **US state codes**: 88-level condition names for all 50 states + territories
- **State + ZIP prefix**: Cross-validation of state code against first 2 digits of ZIP

### CSSETATY.cpy — Screen Attribute Setting (Template)
_Used by: 40 programs (via COPY REPLACING)_

Template copybook for setting BMS field attributes (color, protection) based on validation flags. Uses COPY REPLACING to substitute field names at compile time.

### CSSTRPFY.cpy — Store PFKey Mapping
_Used by: Online programs_

Maps CICS AID bytes (EIBAID) to COMMAREA PFKey flags for consistent keyboard handling.

### CSUTLDPY.cpy — Date Validation Procedures
_Used by: COACTUPC, COCRDUPC_

Reusable PROCEDURE DIVISION paragraphs for CCYYMMDD date validation including:
- Year validation (century check: 19xx or 20xx only)
- Month validation (1-12)
- Day validation (1-31, month-aware)
- Date-of-birth validation (not in future)

### CSUTLDWY.cpy — Date Validation Working Storage
_Used by: COACTUPC, COCRDUPC_

Working storage fields accompanying CSUTLDPY including edit flags, date components, and error message areas.

---

## 9. Export / Migration Entity

### CVEXPORT.cpy — Export Record Layout
_Used by: CBEXPORT, CBIMPORT_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | EXPORT-RECORD | — | Group | Multi-record export layout | — |
| 05 | EXPORT-RECORD-TYPE | X(02) | Alpha | Record type indicator | 'CU'=Customer, 'AC'=Account, 'XR'=Xref, 'TR'=Transaction, 'CD'=Card |
| 05 | EXPORT-BATCH-ID | X(10) | Alpha | Export batch identifier | — |
| 05 | EXPORT-TIMESTAMP | X(16) | Alpha | Export timestamp | — |
| 05 | EXPORT-SEQUENCE-NUM | 9(04) | Numeric | Sequence within batch | Primary key of export file |
| 05 | EXPORT-DATA | X(468) | Alpha | Record payload area | Content varies by record type |

---

## 10. Authorization Sub-Application Entities

### CIPAUDTY.cpy — Authorization Detail Record
_Used by: 8 programs in authorization sub-app_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | PAUT-DETAIL-RECORD | — | Group | Pending authorization detail | — |
| 05 | PAUT-CARD-NUM | X(16) | Alpha | Card number | — |
| 05 | PAUT-TRAN-AMT | S9(9)V99 | Signed decimal | Authorization amount | — |
| 05 | PAUT-TRAN-DT | X(10) | Alpha-date | Transaction date | — |
| 05 | PAUT-TRAN-TM | X(08) | Alpha | Transaction time | — |
| 05 | PAUT-MERCHANT-ID | 9(09) | Numeric | Merchant ID | — |
| 05 | PAUT-AUTH-CD | X(06) | Alpha | Authorization code | — |
| 05 | PAUT-STATUS | X(02) | Alpha | Authorization status | 'AP'=Approved, 'DN'=Denied, 'FR'=Fraud |

### CIPAUSMY.cpy — Authorization Summary Record

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | PAUT-SUMMARY-RECORD | — | Group | Authorization summary for list view | — |
| 05 | PAUT-CARD-NUM | X(16) | Alpha | Card number | — |
| 05 | PAUT-TRAN-DT | X(10) | Alpha-date | Date | — |
| 05 | PAUT-AUTH-CD | X(06) | Alpha | Auth code | — |
| 05 | PAUT-STATUS | X(02) | Alpha | Status | — |

### CCPAURQY.cpy / CCPAURLY.cpy / CCPAUERY.cpy — MQ Message Layouts

Request, Reply, and Error message structures for MQ-based authorization messaging between CICS and IMS.

---

## 11. Transaction Type DB2 Sub-Application Entities

### CSDB2RPY.cpy — DB2 Read Parameters

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | DB2-READ-PARMS | — | Group | DB2 cursor read parameters | — |
| 05 | DB2-SQLCODE | S9(09) COMP | Numeric | SQL return code | 0=OK, 100=not found |
| 05 | DB2-SQLSTATE | X(05) | Alpha | SQL state | — |

### CSDB2RWY.cpy — DB2 Write Parameters

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | DB2-WRITE-PARMS | — | Group | DB2 write/update parameters | — |
| 05 | DB2-SQLCODE | S9(09) COMP | Numeric | SQL return code | — |
| 05 | DB2-ROWS-AFFECTED | S9(09) COMP | Numeric | Rows affected by DML | — |

---

## 12. Statement Entity

### COSTM01.CPY — Statement Control Record
_Used by: CBSTM03A_

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | STATEMENT-CONTROL | — | Group | Statement generation control fields | — |
| 05 | STMT-PAGE-COUNT | S9(4) COMP | Numeric | Current page number | — |
| 05 | STMT-LINE-COUNT | S9(4) COMP | Numeric | Current line on page | — |
| 05 | STMT-LINES-PER-PAGE | S9(4) COMP | Numeric | Max lines per page | Typically 60 |

---

## 13. VSAM Dataset Summary

| Dataset Name | Organization | Record Size | Key | Business Content |
|-------------|-------------|-------------|-----|-----------------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | KSDS | 300 | ACCT-ID (11,0) | Account master |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | KSDS | 150 | CARD-NUM (16,0) | Card master |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | KSDS | 50 | XREF-CARD-NUM (16,0) | Card↔Account xref |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | KSDS | 500 | CUST-ID (9,0) | Customer master |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | KSDS | 350 | TRAN-ID (16,0) | Transaction master |
| AWS.M2.CARDDEMO.USRSEC.PS → VSAM | KSDS | 80 | SEC-USR-ID (8,0) | User security |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | KSDS | 100 | TRAN-TYPE (2,0) | Transaction types |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | KSDS | 100 | TRAN-CAT-KEY (6,0) | Transaction categories |
| AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | KSDS | varies | TRAN-CAT-KEY | Category balances |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | KSDS | varies | DISCGRP-KEY | Discount groups |
| AWS.M2.CARDDEMO.DALYTRAN.PS | Sequential | 350 | — | Daily transaction input |
| AWS.M2.CARDDEMO.EXPORT.DATA | KSDS | 500 | SEQUENCE-NUM (4,28) | Export multi-record file |
