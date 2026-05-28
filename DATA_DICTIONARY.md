# Data Dictionary — CardDemo COBOL Estate

> Complete field-level documentation for every copybook in the CardDemo system,
> grouped by business entity.

---

## 1  Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)
**Record:** `ACCOUNT-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | ACCT-ID | 9(11) | Numeric | Unique account identifier | 11-digit number; primary key for ACCTDATA VSAM |
| 05 | ACCT-ACTIVE-STATUS | X(01) | Alpha | Account status flag | 'Y' = Active, 'N' = Inactive |
| 05 | ACCT-CURR-BAL | S9(10)V99 | Signed decimal | Current account balance | Signed; 2 decimal places |
| 05 | ACCT-CREDIT-LIMIT | S9(10)V99 | Signed decimal | Credit limit | Must be positive |
| 05 | ACCT-CASH-CREDIT-LIMIT | S9(10)V99 | Signed decimal | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| 05 | ACCT-OPEN-DATE | X(10) | Alphanumeric | Date account was opened | Format: YYYY-MM-DD |
| 05 | ACCT-EXPIRAION-DATE | X(10) | Alphanumeric | Account expiration date | Format: YYYY-MM-DD |
| 05 | ACCT-REISSUE-DATE | X(10) | Alphanumeric | Last reissue date | Format: YYYY-MM-DD |
| 05 | ACCT-CURR-CYC-CREDIT | S9(10)V99 | Signed decimal | Current cycle credit total | Running cycle total |
| 05 | ACCT-CURR-CYC-DEBIT | S9(10)V99 | Signed decimal | Current cycle debit total | Running cycle total |
| 05 | ACCT-ADDR-ZIP | X(10) | Alphanumeric | Account ZIP code | US postal code |
| 05 | ACCT-GROUP-ID | X(10) | Alphanumeric | Disclosure group ID | Links to DISCGRP for interest rates |
| 05 | FILLER | X(178) | Filler | Reserved space | Pad to 300-byte record |

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)
**Record:** `CARD-XREF-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | XREF-CARD-NUM | X(16) | Alphanumeric | Credit card number | 16-digit card number; links CARD ↔ ACCOUNT ↔ CUSTOMER |
| 05 | XREF-CUST-ID | 9(09) | Numeric | Customer ID | Must exist in CUSTDATA |
| 05 | XREF-ACCT-ID | 9(11) | Numeric | Account ID | Must exist in ACCTDATA |
| 05 | FILLER | X(14) | Filler | Reserved | Pad to 50-byte record |

---

## 2  Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)
**Record:** `CUSTOMER-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | CUST-ID | 9(09) | Numeric | Unique customer identifier | 9-digit number; primary key |
| 05 | CUST-FIRST-NAME | X(25) | Alpha | First name | Required |
| 05 | CUST-MIDDLE-NAME | X(25) | Alpha | Middle name | Optional |
| 05 | CUST-LAST-NAME | X(25) | Alpha | Last name | Required |
| 05 | CUST-ADDR-LINE-1 | X(50) | Alpha | Address line 1 | Required |
| 05 | CUST-ADDR-LINE-2 | X(50) | Alpha | Address line 2 | Optional |
| 05 | CUST-ADDR-LINE-3 | X(50) | Alpha | Address line 3 | Optional |
| 05 | CUST-ADDR-STATE-CD | X(02) | Alpha | US state code | Validated against CSLKPCDY state list |
| 05 | CUST-ADDR-COUNTRY-CD | X(03) | Alpha | Country code | ISO country code |
| 05 | CUST-ADDR-ZIP | X(10) | Alphanumeric | ZIP code | Validated against CSLKPCDY ZIP table |
| 05 | CUST-PHONE-NUM-1 | X(15) | Alphanumeric | Primary phone number | Area code validated against CSLKPCDY |
| 05 | CUST-PHONE-NUM-2 | X(15) | Alphanumeric | Secondary phone number | Area code validated against CSLKPCDY |
| 05 | CUST-SSN | 9(09) | Numeric | Social Security Number | 9-digit SSN |
| 05 | CUST-GOVT-ISSUED-ID | X(20) | Alphanumeric | Government-issued ID | Optional |
| 05 | CUST-DOB-YYYYMMDD | X(10) | Alphanumeric | Date of birth | Format: YYYY-MM-DD |
| 05 | CUST-EFT-ACCOUNT-ID | X(10) | Alphanumeric | EFT/ACH account ID | For electronic fund transfers |
| 05 | CUST-PRI-CARD-HOLDER-IND | X(01) | Alpha | Primary cardholder indicator | 'Y' = Primary, 'N' = Authorized user |
| 05 | CUST-FICO-CREDIT-SCORE | 9(03) | Numeric | FICO credit score | Range: 300–850 |
| 05 | FILLER | X(168) | Filler | Reserved | Pad to 500-byte record |

### CUSTREC.cpy — Customer Record (alternate layout, RECLN 500)
**Record:** `CUSTOMER-RECORD`

> Same structure as CVCUS01Y.cpy. Used by CBSTM03A statement generation.

---

## 3  Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)
**Record:** `CARD-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | CARD-NUM | X(16) | Alphanumeric | Credit card number | 16-digit; primary key for CARDDATA VSAM |
| 05 | CARD-ACCT-ID | 9(11) | Numeric | Associated account ID | Must exist in ACCTDATA |
| 05 | CARD-CVV-CD | 9(03) | Numeric | Card verification value | 3-digit CVV |
| 05 | CARD-EMBOSSED-NAME | X(50) | Alpha | Name embossed on card | Required |
| 05 | CARD-EXPIRAION-DATE | X(10) | Alphanumeric | Card expiration date | Format: YYYY-MM-DD |
| 05 | CARD-ACTIVE-STATUS | X(01) | Alpha | Card active status | 'Y' = Active, 'N' = Inactive |
| 05 | FILLER | X(59) | Filler | Reserved | Pad to 150-byte record |

---

## 4  Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)
**Record:** `TRAN-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | TRAN-ID | X(16) | Alphanumeric | Unique transaction ID | System-generated |
| 05 | TRAN-TYPE-CD | X(02) | Alphanumeric | Transaction type code | Must exist in TRANTYPE |
| 05 | TRAN-CAT-CD | 9(04) | Numeric | Transaction category code | Must exist in TRANCATG |
| 05 | TRAN-SOURCE | X(10) | Alphanumeric | Transaction source | e.g., 'ONLINE', 'ATM', 'POS' |
| 05 | TRAN-DESC | X(100) | Alpha | Transaction description | Free text |
| 05 | TRAN-AMT | S9(09)V99 | Signed decimal | Transaction amount | Signed; 2 decimal places |
| 05 | TRAN-CARD-NUM | X(16) | Alphanumeric | Card number used | Must exist in CARDDATA |
| 05 | TRAN-MERCHANT-ID | 9(09) | Numeric | Merchant identifier | Merchant reference |
| 05 | TRAN-MERCHANT-NAME | X(50) | Alpha | Merchant name | |
| 05 | TRAN-MERCHANT-CITY | X(50) | Alpha | Merchant city | |
| 05 | TRAN-MERCHANT-ZIP | X(10) | Alphanumeric | Merchant ZIP code | |
| 05 | TRAN-ORIG-TS | X(26) | Alphanumeric | Transaction timestamp | Format: YYYY-MM-DD HH:MM:SS.ffffff |
| 05 | TRAN-PROC-TS | X(26) | Alphanumeric | Processing timestamp | Format: YYYY-MM-DD HH:MM:SS.ffffff |
| 05 | FILLER | X(20) | Filler | Reserved | Pad to 350-byte record |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)
**Record:** `DALYTRAN-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | DALYTRAN-ID | X(16) | Alphanumeric | Daily transaction ID | Assigned at POS/ATM |
| 05 | DALYTRAN-TYPE-CD | X(02) | Alphanumeric | Transaction type code | Validated against TRANTYPE |
| 05 | DALYTRAN-CAT-CD | 9(04) | Numeric | Transaction category | Validated against TRANCATG |
| 05 | DALYTRAN-SOURCE | X(10) | Alphanumeric | Source system | |
| 05 | DALYTRAN-DESC | X(100) | Alpha | Description | |
| 05 | DALYTRAN-AMT | S9(09)V99 | Signed decimal | Amount | |
| 05 | DALYTRAN-CARD-NUM | X(16) | Alphanumeric | Card number | Must exist in CARDDATA |
| 05 | DALYTRAN-MERCHANT-ID | 9(09) | Numeric | Merchant ID | |
| 05 | DALYTRAN-MERCHANT-NAME | X(50) | Alpha | Merchant name | |
| 05 | DALYTRAN-MERCHANT-CITY | X(50) | Alpha | Merchant city | |
| 05 | DALYTRAN-MERCHANT-ZIP | X(10) | Alphanumeric | Merchant ZIP | |
| 05 | DALYTRAN-ORIG-TS | X(26) | Alphanumeric | Original timestamp | |
| 05 | DALYTRAN-PROC-TS | X(26) | Alphanumeric | Processing timestamp | |
| 05 | FILLER | X(20) | Filler | Reserved | |

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)
**Record:** `TRAN-CAT-BAL-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 10 | TRANCAT-ACCT-ID | 9(11) | Numeric | Account ID | Must exist in ACCTDATA |
| 10 | TRANCAT-TYPE-CD | X(02) | Alphanumeric | Transaction type code | Must exist in TRANTYPE |
| 10 | TRANCAT-CD | 9(04) | Numeric | Category code | Must exist in TRANCATG |
| 05 | TRAN-CAT-BAL | S9(09)V99 | Signed decimal | Running balance by category | Updated by CBTRN02C |
| 05 | FILLER | X(22) | Filler | Reserved | |

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)
**Record:** `DIS-GROUP-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 10 | DIS-ACCT-GROUP-ID | X(10) | Alphanumeric | Disclosure group identifier | Links to ACCT-GROUP-ID |
| 10 | DIS-TRAN-TYPE-CD | X(02) | Alphanumeric | Transaction type | |
| 10 | DIS-TRAN-CAT-CD | 9(04) | Numeric | Transaction category | |
| 05 | DIS-INT-RATE | S9(04)V99 | Signed decimal | Interest rate for this group/type/category | Percentage with 2 decimals |
| 05 | FILLER | X(28) | Filler | Reserved | |

### CVTRA03Y.cpy — Transaction Type (RECLN 60)
**Record:** `TRAN-TYPE-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | TRAN-TYPE | X(02) | Alphanumeric | Transaction type code | Primary key |
| 05 | TRAN-TYPE-DESC | X(50) | Alpha | Type description | e.g., 'Purchase', 'Cash Advance' |
| 05 | FILLER | X(08) | Filler | Reserved | |

### CVTRA04Y.cpy — Transaction Category (RECLN 60)
**Record:** `TRAN-CAT-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 10 | TRAN-TYPE-CD | X(02) | Alphanumeric | Transaction type code | FK to TRANTYPE |
| 10 | TRAN-CAT-CD | 9(04) | Numeric | Category code | Composite key with TRAN-TYPE-CD |
| 05 | TRAN-CAT-TYPE-DESC | X(50) | Alpha | Category description | |
| 05 | FILLER | X(04) | Filler | Reserved | |

### CVTRA07Y.cpy — Transaction Report Structures
**Records:** `REPORT-NAME-HEADER`, `TRANSACTION-DETAIL-REPORT`, `TRANSACTION-HEADER-1`, `TRANSACTION-HEADER-2`, `REPORT-PAGE-TOTALS`, `REPORT-ACCOUNT-TOTALS`, `REPORT-GRAND-TOTALS`

> Report formatting structures used by CBTRN03C for transaction detail reports. Contains header labels, column headings, total accumulators, and page counters.

### COSTM01.CPY — Transaction Altered Layout for Reporting
**Record:** `TRNX-RECORD`

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 10 | TRNX-CARD-NUM | X(16) | Alphanumeric | Card number |
| 10 | TRNX-ID | X(16) | Alphanumeric | Transaction ID |
| 10 | TRNX-TYPE-CD | X(02) | Alphanumeric | Transaction type |
| 10 | TRNX-CAT-CD | 9(04) | Numeric | Category code |
| 10 | TRNX-SOURCE | X(10) | Alphanumeric | Source |
| 10 | TRNX-DESC | X(100) | Alpha | Description |
| 10 | TRNX-AMT | S9(09)V99 | Signed decimal | Amount |
| 10 | TRNX-MERCHANT-ID | 9(09) | Numeric | Merchant ID |
| 10 | TRNX-MERCHANT-NAME | X(50) | Alpha | Merchant name |
| 10 | TRNX-MERCHANT-CITY | X(50) | Alpha | Merchant city |
| 10 | TRNX-MERCHANT-ZIP | X(10) | Alphanumeric | Merchant ZIP |
| 10 | TRNX-ORIG-TS | X(26) | Alphanumeric | Original timestamp |
| 10 | TRNX-PROC-TS | X(26) | Alphanumeric | Processing timestamp |

---

## 5  Security / User Entity

### CSUSR01Y.cpy — User Security Record
**Record:** `SEC-USER-DATA`

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | SEC-USR-ID | X(08) | Alphanumeric | User login ID | Primary key for USRSEC VSAM |
| 05 | SEC-USR-FNAME | X(20) | Alpha | User first name | Required |
| 05 | SEC-USR-LNAME | X(20) | Alpha | User last name | Required |
| 05 | SEC-USR-PWD | X(08) | Alphanumeric | User password | 8 characters |
| 05 | SEC-USR-TYPE | X(01) | Alpha | User type | 'A' = Admin, 'U' = Regular user |
| 05 | SEC-USR-FILLER | X(23) | Filler | Reserved | |

---

## 6  Communication / UI Control Structures

### COCOM01Y.cpy — COMMAREA (Inter-Program Communication)
**Record:** `CARDDEMO-COMMAREA`

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 10 | CDEMO-FROM-TRANID | X(04) | Alphanumeric | Originating CICS transaction ID |
| 10 | CDEMO-FROM-PROGRAM | X(08) | Alphanumeric | Calling program name |
| 10 | CDEMO-TO-TRANID | X(04) | Alphanumeric | Target transaction ID |
| 10 | CDEMO-TO-PROGRAM | X(08) | Alphanumeric | Target program name |
| 10 | CDEMO-USER-ID | X(08) | Alphanumeric | Logged-in user ID |
| 10 | CDEMO-USER-TYPE | X(01) | Alpha | 'A' = Admin, 'U' = User (88-level conditions) |
| 10 | CDEMO-PGM-CONTEXT | 9(01) | Numeric | 0 = First enter, 1 = Re-enter |
| 10 | CDEMO-CUST-ID | 9(09) | Numeric | Current customer ID in context |
| 10 | CDEMO-CUST-FNAME | X(25) | Alpha | Customer first name |
| 10 | CDEMO-CUST-MNAME | X(25) | Alpha | Customer middle name |
| 10 | CDEMO-CUST-LNAME | X(25) | Alpha | Customer last name |
| 10 | CDEMO-ACCT-ID | 9(11) | Numeric | Current account ID in context |
| 10 | CDEMO-ACCT-STATUS | X(01) | Alpha | Account active status |
| 10 | CDEMO-CARD-NUM | 9(16) | Numeric | Current card number |
| 10 | CDEMO-LAST-MAP | X(7) | Alphanumeric | Last BMS map displayed |
| 10 | CDEMO-LAST-MAPSET | X(7) | Alphanumeric | Last BMS mapset |

### COMEN02Y.cpy — Main Menu Options
**Record:** `CARDDEMO-MAIN-MENU-OPTIONS`

> Defines 11 menu entries mapping option numbers → program names → user type restrictions.
> Options: Account View/Update, Credit Card List/View/Update, Transaction List/View/Add, Reports, Bill Payment, Pending Authorization View.

### COADM02Y.cpy — Admin Menu Options
**Record:** `CARDDEMO-ADMIN-MENU-OPTIONS`

> Defines 6 admin menu entries: User List, User Add, User Update, User Delete, Transaction Type List/Update (DB2), Transaction Type Maintenance (DB2).

### CVCRD01Y.cpy — Card Detail Screen Control
> Working storage for card detail screen navigation: AID key, next program/mapset/map, error message, and current card number context.

### COTTL01Y.cpy — Screen Titles
**Record:** `CCDA-SCREEN-TITLE`

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|-----------------|
| 05 | CCDA-TITLE01 | X(40) | 'AWS Mainframe Modernization' |
| 05 | CCDA-TITLE02 | X(40) | 'CardDemo' |
| 05 | CCDA-THANK-YOU | X(40) | Thank you message |

### CSMSG01Y.cpy — Common Messages
**Record:** `CCDA-COMMON-MESSAGES`

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|-----------------|
| 05 | CCDA-MSG-THANK-YOU | X(50) | Session end message |
| 05 | CCDA-MSG-INVALID-KEY | X(50) | Invalid key press message |

### CSMSG02Y.cpy — Abend Information
> Fields: ABEND-CODE X(4), ABEND-CULPRIT X(8), ABEND-REASON X(50), ABEND-MSG X(72).

---

## 7  Date / Time Structures

### CSDAT01Y.cpy — Date/Time Working Storage
**Record:** `WS-DATE-TIME`

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|-----------------|
| 15 | WS-CURDATE-YEAR | 9(04) | Current year (4-digit) |
| 15 | WS-CURDATE-MONTH | 9(02) | Current month |
| 15 | WS-CURDATE-DAY | 9(02) | Current day |
| 10 | WS-CURDATE-N | 9(08) | Date as numeric YYYYMMDD (REDEFINES) |
| 15 | WS-CURTIME-HOURS | 9(02) | Hours |
| 15 | WS-CURTIME-MINUTE | 9(02) | Minutes |
| 15 | WS-CURTIME-SECOND | 9(02) | Seconds |
| 15 | WS-CURTIME-MILSEC | 9(02) | Milliseconds |
| 05 | WS-CURDATE-MM-DD-YY | — | Formatted MM/DD/YY display |
| 05 | WS-CURTIME-HH-MM-SS | — | Formatted HH:MM:SS display |
| 05 | WS-TIMESTAMP | — | Full timestamp YYYY-MM-DD HH:MM:SS.ffffff |

### CODATECN.cpy — Date Conversion (Assembler Interface)
**Record:** `CODATECN-REC`

> Input/output structure for COBDATFT assembler date formatter. Supports YYYYMMDD ↔ YYYY-MM-DD conversion via REDEFINES.

### CSUTLDPY.cpy — Date Utility Procedure Division
> Procedure division copybook for date validation and computation. Contains paragraphs for CEEDAYS calls, date arithmetic, and format conversion.

### CSUTLDWY.cpy — Date Utility Working Storage
> Working storage copybook for date utility routines. Contains edit masks, intermediate date fields, and validation flags.

---

## 8  Validation / Lookup Tables

### CSLKPCDY.cpy — Lookup Code Repository (1,319 lines)
**Records:** `WS-US-PHONE-AREA-CODE-TO-EDIT`, `US-STATE-CODE-TO-EDIT`, `US-STATE-ZIPCODE-TO-EDIT`

> Comprehensive validation tables:
> 1. **North America phone area codes** — 88-level condition with all valid NANPA area codes
> 2. **US state codes** — 88-level condition with all 50 states + DC + territories
> 3. **US state + first-2-of-ZIP** — 88-level condition for ZIP-to-state validation

### CSSETATY.cpy — Set Attribute Utility
> Procedure division copybook for setting BMS field attributes (protected, unprotected, bright, dark, etc.).

### CSSTRPFY.cpy — String Manipulation Utility
> Procedure division copybook for string padding and trimming operations.

---

## 9  Export / Migration Structures

### CVEXPORT.cpy — Multi-Record Export Layout (104 lines)
**Record:** `EXPORT-RECORD`

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|-----------------|
| 05 | EXPORT-REC-TYPE | X(1) | Record type: 'H'=Header, 'C'=Customer, 'A'=Account, 'X'=Xref, 'T'=Transaction, 'R'=Card, 'F'=Footer |
| 05 | EXPORT-TIMESTAMP | X(26) | Export timestamp |
| 05 | EXPORT-HEADER-DATA | — | Header record fields (version, source branch, record counts) |
| 05 | EXPORT-CUSTOMER-DATA | — | Customer fields (mirrors CVCUS01Y) |
| 05 | EXPORT-ACCOUNT-DATA | — | Account fields (mirrors CVACT01Y) |
| 05 | EXPORT-XREF-DATA | — | Cross-reference fields (mirrors CVACT03Y) |
| 05 | EXPORT-TRANSACTION-DATA | — | Transaction fields (mirrors CVTRA05Y) |
| 05 | EXPORT-CARD-DATA | — | Card fields (mirrors CVACT02Y) |
| 05 | EXPORT-FOOTER-DATA | — | Footer with totals and checksum |

---

## 10  Authorization (IMS/DB2) Structures

### CIPAUSMY.cpy — IMS Segment: Pending Authorization Summary

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|-----------------|
| 05 | PA-ACCT-ID | S9(11) | Account ID (signed COMP) |
| 05 | PA-CUST-ID | 9(09) | Customer ID |
| 05 | PA-AUTH-STATUS | X(01) | Authorization status |
| 05 | PA-ACCOUNT-STATUS | X(02) | Account status |
| 05 | PA-CREDIT-LIMIT | S9(09)V99 | Credit limit |
| 05 | PA-AVAILABLE-CREDIT | S9(09)V99 | Available credit |
| 05 | PA-PENDING-AUTH-TOTAL | S9(09)V99 | Total pending authorizations |
| 05 | PA-PENDING-AUTH-COUNT | S9(05) | Count of pending authorizations |

### CIPAUDTY.cpy — IMS Segment: Pending Authorization Details

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|-----------------|
| 05 | PA-AUTH-ORIG-DATE | X(06) | Authorization date |
| 05 | PA-AUTH-ORIG-TIME | X(06) | Authorization time |
| 05 | PA-CARD-NUM | X(16) | Card number |
| 05 | PA-AUTH-TYPE | X(04) | Authorization type |
| 05 | PA-AUTH-AMOUNT | S9(09)V99 | Authorization amount |
| 05 | PA-MERCHANT-ID | X(12) | Merchant ID |
| 05 | PA-AUTH-RESP-CODE | X(02) | Response code |
| 05 | PA-AUTH-STATUS-FLAG | X(01) | Status: A=Approved, D=Declined, F=Fraud |

### CCPAURQY.cpy — Pending Authorization MQ Request
> MQ message layout for authorization requests: date, time, card number, auth type, expiry, CVV, amount, merchant details (13+ fields).

### CCPAURLY.cpy — Pending Authorization MQ Response
> MQ message layout for authorization responses: card number, transaction ID, auth code, response code, response reason.

### CCPAUERY.cpy — Pending Authorization Error Log
**Record:** `ERROR-LOG-RECORD`
> Error logging: date, time, application, program, location, error type, error code, error message.

### IMS PCB Copybooks

| Copybook | Record | Purpose |
|----------|--------|---------|
| PAUTBPCB.CPY | PAUTBPCB | PCB for pending auth base database |
| PADFLPCB.CPY | PADFLPCB | PCB for pending auth detail database |
| PASFLPCB.CPY | PASFLPCB | PCB for pending auth summary database |
| IMSFUNCS.cpy | FUNC-CODES | IMS DL/I function codes (GU, GHU, GN, GHN, GNP, ISRT, REPL, DLET, etc.) |

---

## 11  DB2 Structures

### CSDB2RWY.cpy — DB2 Working Storage
> Common DB2 working storage: SQLCODE display field, processing flags, current action description, DSNTIAC message area.

### CSDB2RPY.cpy — DB2 Procedure Division
> Common DB2 procedure division paragraphs: SQLCODE check routines, error handling, and DB2 return code processing.

---

## 12  Unused / Placeholder

### UNUSED1Y.cpy — Unused Data Structure
**Record:** `UNUSED-DATA`
> Placeholder structure with ID, first name, last name, password, and type fields. Not referenced by any active program.
