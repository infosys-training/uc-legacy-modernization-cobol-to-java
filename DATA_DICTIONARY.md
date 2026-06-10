# Data Dictionary

## Overview

This document catalogs all data structures defined in copybooks under `app/cpy/` and sub-application copybook directories. Fields are grouped by business entity with their PIC clauses, inferred data types, business meanings, and validation rules.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (300 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Unique account identifier (primary key) | Must be non-zero |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Account active flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Signed; can be negative (overdrawn) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum credit limit | Must be positive |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Cash advance credit limit | Must be <= ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (10) | Date account was opened | Format: YYYY-MM-DD; validated by CSUTLDTC |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Account expiration date | Format: YYYY-MM-DD; must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (10) | Date account was last reissued | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle credit total | Running total for billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle debit total | Running total for billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account holder ZIP code | Validated against state-ZIP table in CSLKPCDY |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Discount/interest rate group | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| FILLER | PIC X(178) | Filler | Reserved padding | N/A |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (500 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric (9 digits) | Unique customer identifier (primary key) | Must be non-zero |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | Required; alphabetic |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | Required; alphabetic |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Primary address line | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Secondary address line | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Tertiary address line | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric (2) | US state code | Validated against 50 state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric (3) | Country code | ISO 3166 alpha-3 |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | ZIP/postal code | Validated: first 2 digits must match state (CSLKPCDY) |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone number | Area code validated against NANPA list (CSLKPCDY) |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone number | Area code validated against NANPA list (CSLKPCDY) |
| CUST-SSN | PIC 9(09) | Numeric (9 digits) | Social Security Number | Must be 9 digits; validated in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID number | Optional alternate ID |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (10) | Date of birth | Format: YYYY-MM-DD; validated by CSUTLDTC |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | EFT/direct deposit account ID | For electronic payment routing |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric (1) | Primary cardholder indicator | 'Y' = primary holder |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3 digits) | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Filler | Reserved padding | N/A |

### CUSTREC.cpy — Customer Record (Alternate Layout for Statement Generation)

Identical field layout to CVCUS01Y with minor naming differences (e.g., `CUST-DOB-YYYYMMDD` vs `CUST-DOB-YYYY-MM-DD`). Used specifically by CBSTM03A statement generation program.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (150 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number (primary key) | 16-digit card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Associated account ID (FK to CVACT01Y) | Must exist in ACCTFILE |
| CARD-CVV-CD | PIC 9(03) | Numeric (3 digits) | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name embossed on physical card | Derived from customer name |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Card active flag | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Filler | Reserved padding | N/A |

---

## 4. Card-Account Cross-Reference Entity

### CVACT03Y.cpy — Cross-Reference Record (50 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (primary key) | Must exist in CARDFILE |
| XREF-CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer ID (FK to CVCUS01Y) | Must exist in CUSTFILE |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account ID (FK to CVACT01Y) | Must exist in ACCTFILE |
| FILLER | PIC X(14) | Filler | Reserved padding | N/A |

---

## 5. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (350 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Unique transaction identifier | System-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Must exist in TRANTYPE file (CVTRA03Y) |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | Must exist in TRANCATG file (CVTRA04Y) |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction origination source | E.g., POS, ATM, ONLINE |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Signed; negative = credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant identifier | Required for purchases |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | Required for purchases |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | Required for purchases |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | Optional |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card used for transaction | Must exist in XREF |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original transaction timestamp | Format: YYYY-MM-DD-HH.MM.SS.NNNNNN |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | Set by batch posting (CBTRN02C) |
| FILLER | PIC X(20) | Filler | Reserved padding | N/A |

### CVTRA06Y.cpy — Daily Transaction Record (350 bytes)

Same layout as CVTRA05Y with `DALYTRAN-` prefix. Represents incoming unposted transactions before validation and posting by CBTRN02C.

### COSTM01.CPY — Transaction Record (Statement Format)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (part of composite key) | Composite key with TRNX-ID |
| TRNX-ID | PIC X(16) | Alphanumeric (16) | Transaction ID (part of composite key) | Composite key with TRNX-CARD-NUM |
| TRNX-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Same as TRAN-TYPE-CD |
| TRNX-CAT-CD | PIC 9(04) | Numeric (4) | Category code | Same as TRAN-CAT-CD |
| TRNX-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source | Same as TRAN-SOURCE |
| TRNX-DESC | PIC X(100) | Alphanumeric (100) | Description | Same as TRAN-DESC |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Amount | Same as TRAN-AMT |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant ID | Same as TRAN-MERCHANT-ID |
| TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | Same as TRAN-MERCHANT-NAME |
| TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | Same as TRAN-MERCHANT-CITY |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP | Same as TRAN-MERCHANT-ZIP |
| TRNX-ORIG-TS | PIC X(26) | Alphanumeric (26) | Origination timestamp | Same as TRAN-ORIG-TS |
| TRNX-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | Same as TRAN-PROC-TS |
| FILLER | PIC X(20) | Filler | Reserved | N/A |

---

## 6. Transaction Reference Entities

### CVTRA03Y.cpy — Transaction Type Record (60 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric (2) | Transaction type code (primary key) | E.g., 'SA' (sale), 'CR' (credit) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Type description | Human-readable label |
| FILLER | PIC X(08) | Filler | Reserved | N/A |

### CVTRA04Y.cpy — Transaction Category Record (60 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type (composite key part 1) | FK to CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code (composite key part 2) | Unique within type |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | E.g., "Groceries", "Gas" |
| FILLER | PIC X(04) | Filler | Reserved | N/A |

### CVTRA01Y.cpy — Transaction Category Balance Record (50 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID (composite key part 1) | FK to CVACT01Y |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Type code (composite key part 2) | FK to CVTRA03Y |
| TRANCAT-CD | PIC 9(04) | Numeric (4) | Category code (composite key part 3) | FK to CVTRA04Y |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance by category | Updated by CBTRN02C and CBACT04C |
| FILLER | PIC X(22) | Filler | Reserved | N/A |

### CVTRA02Y.cpy — Discount/Interest Group Record (50 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Account group ID (composite key part 1) | Links from ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Type code (composite key part 2) | FK to CVTRA03Y |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code (composite key part 3) | FK to CVTRA04Y |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest/discount rate percentage | Applied by CBACT04C |
| FILLER | PIC X(28) | Filler | Reserved | N/A |

---

## 7. Pending Authorization Entity (IMS)

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (internal) | Julian date format |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (internal) | Time in centiseconds |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric (6) | Original authorization date | Format: YYMMDD |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric (6) | Original authorization time | Format: HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | FK to CARDFILE |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric (4) | Authorization type | E.g., 'SALE', 'CASH' |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric (4) | Card expiry | Format: YYMM |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric (6) | Message type indicator | ISO 8583 message type |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric (6) | Originating system | E.g., 'POS', 'ATM' |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric (6) | Authorization ID | Approval code returned to merchant |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric (2) | Response code | '00' = approved (88-level) |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric (4) | Decline reason code | Set when response != '00' |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric (6) | Processing code | ISO 8583 field |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Requested transaction amount | Must be positive |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Approved amount | May differ from requested (partial approval) |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric (4) | Merchant Category Code (MCC) | ISO 18245 |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric (3) | Acquirer country code | ISO 3166 |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric (2) | POS entry mode | E.g., 05=chip, 07=contactless |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant ID | Acquirer-assigned |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name | Truncated to 22 chars |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city | Truncated to 13 chars |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric (2) | Merchant state | US state code |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric (9) | Merchant ZIP | 5 or 9 digit |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | External transaction reference | Network-assigned |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric (1) | Settlement match status | P=Pending, D=Declined, E=Expired, M=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric (1) | Fraud indicator | F=Fraud confirmed, R=Fraud removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric (8) | Date fraud was reported | Format: YYYYMMDD |
| FILLER | PIC X(17) | Filler | Reserved | N/A |

### CCPAURQY.cpy — Authorization Request (MQ Message)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric (6) | Request date | Format: YYMMDD |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric (6) | Request time | Format: HHMMSS |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | Must be active |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric (4) | Authorization type | E.g., 'SALE' |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric (4) | Card expiry | Must not be expired |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric (6) | ISO 8583 message type | Incoming message format |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric (6) | Message source | Network identifier |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric (6) | Processing code | Determines transaction type |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric (13) | Requested amount | Display format |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric (4) | MCC | ISO 18245 |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric (3) | Acquirer country | ISO 3166 |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric (2) | POS entry mode | Entry method |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant ID | Acquirer-assigned |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name | From request |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city | From request |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric (2) | Merchant state | From request |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric (9) | Merchant ZIP | From request |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Network transaction ID | Unique per request |

### CCPAURLY.cpy — Authorization Reply (MQ Message)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | Echo from request |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction ID | Echo from request |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric (6) | Authorization code | Generated on approval |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric (2) | Response code | '00' = approved |

---

## 8. Security/User Entity

### CSUSR01Y.cpy — Security User Record (80 bytes)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric (8) | User login ID (primary key) | Unique; required |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | Required |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | Required |
| SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password (**plain text**) | **SECURITY RISK**: stored unencrypted |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric (1) | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved | N/A |

---

## 9. Communication/Control Structures

### COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric (4) | Originating transaction ID | CICS transaction code |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric (8) | Originating program name | Calling program |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric (4) | Target transaction ID | Destination transaction |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric (8) | Target program name | Destination program |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric (8) | Current user ID | From sign-on |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric (1) | User type flag | 88: 'A' = Admin, 'U' = User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric (1) | Program entry context | 88: 0 = first entry, 1 = re-entry |
| CDEMO-CUST-ID | PIC 9(09) | Numeric (9) | Selected customer ID | Passed between screens |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric (25) | Customer first name | Display context |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric (25) | Customer middle name | Display context |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric (25) | Customer last name | Display context |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric (11) | Selected account ID | Passed between screens |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric (1) | Account status | Active/Inactive |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric (16) | Selected card number | Passed between screens |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric (7) | Last BMS map displayed | Screen navigation state |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric (7) | Last BMS mapset used | Screen navigation state |

### COADM02Y.cpy — Admin Menu Options

Defines 6 admin menu options with program routing (COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC).

### COMEN02Y.cpy — Main Menu Options

Defines 11 user menu options routing to: COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C.

---

## 10. Report Structures

### CVTRA07Y.cpy — Transaction Report Layout

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report short title |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report full title |
| REPT-DATE-HEADER | PIC X(12) | Alphanumeric | Date range header label |
| REPT-START-DATE | PIC X(10) | Alphanumeric | Report start date |
| REPT-END-DATE | PIC X(10) | Alphanumeric | Report end date |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID column |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID column |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code column |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code column |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Transaction source |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount |

---

## 11. Export/Import Structure

### CVEXPORT.cpy — Export Record (Multi-Type)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric (1) | Record type discriminator | 'C'=Customer, 'A'=Account, 'X'=XREF, 'T'=Transaction |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric (26) | Export timestamp | When record was exported |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary (4 bytes) | Sequence number | Monotonically increasing |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric (4) | Branch identifier | Source branch code |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric (5) | Region code | Geographic region |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric (460) | Embedded entity record | REDEFINES by type |

**REDEFINES for Customer type** (EXP-CUST-* prefix) includes full customer data with OCCURS for address lines (3) and phone numbers (2).

---

## 12. Date/Time Structures

### CSDAT01Y.cpy — Working Storage Date-Time

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric (4) | Current year (YYYY) |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric (2) | Current month (MM) |
| WS-CURDATE-DAY | PIC 9(02) | Numeric (2) | Current day (DD) |
| WS-CURTIME-HOURS | PIC 9(02) | Numeric (2) | Current hours (HH) |
| WS-CURTIME-MINUTE | PIC 9(02) | Numeric (2) | Current minutes (MM) |
| WS-CURTIME-SECOND | PIC 9(02) | Numeric (2) | Current seconds (SS) |
| WS-TIMESTAMP | — | Composite | Full ISO timestamp (YYYY-MM-DD HH:MM:SS.NNNNNN) |

### CODATECN.cpy — Date Conversion Interface

Used by CBACT01C to call assembler date formatter (COBDATFT). Supports YYYYMMDD and YYYY-MM-DD input/output formats with error message field.

### CSUTLDWY.cpy — Date Validation Working Storage

Provides date validation fields with 88-level conditions for valid months (1-12), 31-day months, February, and day ranges (1-31). Used by COACTUPC and COTRN02C for date field validation.

---

## 13. Validation Lookup Data

### CSLKPCDY.cpy — Lookup Code Repository

Contains three validation tables:
1. **North America Phone Area Codes** — Full NANPA list of valid 3-digit area codes (88-level `VALID-PHONE-AREA-CODE`)
2. **US State Codes** — 50 valid 2-character state codes (88-level `VALID-US-STATE-CODE`)
3. **State-ZIP Prefix Mapping** — First 2 digits of ZIP mapped to valid states (88-level validation)

---

## 14. IMS Infrastructure Structures

### IMSFUNCS.cpy — IMS DL/I Function Codes

| Field Name | PIC Clause | Value | Purpose |
|------------|-----------|-------|---------|
| FUNC-GU | PIC X(04) | 'GU  ' | Get Unique (direct read) |
| FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique (read for update) |
| FUNC-GN | PIC X(04) | 'GN  ' | Get Next (sequential read) |
| FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next |
| FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | PIC X(04) | 'REPL' | Replace (update segment) |
| FUNC-ISRT | PIC X(04) | 'ISRT' | Insert (add segment) |
| FUNC-DLET | PIC X(04) | 'DLET' | Delete (remove segment) |
| PARMCOUNT | PIC S9(05) COMP-5 | +4 | Parameter count for CBLTDLI |

### PADFLPCB.CPY — IMS PCB (Program Communication Block)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| PADFL-DBDNAME | PIC X(08) | Database Description name |
| PADFL-SEG-LEVEL | PIC X(02) | Segment level number |
| PADFL-PCB-STATUS | PIC X(02) | Status code ('  ' = success, 'GE' = not found) |
| PADFL-PCB-PROCOPT | PIC X(04) | Processing options |
| PADFL-SEG-NAME | PIC X(08) | Current segment name |
| PADFL-KEYFB | PIC X(255) | Key feedback area |

---

## 15. Miscellaneous

### COTTL01Y.cpy — Screen Title Constants

Application-wide title strings for BMS screens ("AWS Mainframe Modernization", "CardDemo").

### CSMSG01Y.cpy — Common Messages

Standard messages: thank-you text, invalid key messages.

### CSMSG02Y.cpy — Abend Data Structure

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| ABEND-CODE | PIC X(4) | CICS abend code |
| ABEND-CULPRIT | PIC X(8) | Program causing abend |
| ABEND-REASON | PIC X(50) | Reason text |
| ABEND-MSG | PIC X(72) | Full error message |

### CSSETATY.cpy — Screen Attribute Setting (Macro)

Used with COPY REPLACING to set BMS field attributes. Applied 3 times in COACTUPC with different field prefixes for protected/unprotected/error fields.

### CSSTRPFY.cpy — PF Key Processing (Macro)

Standard PF key interpretation logic mapping EIBAID values to application PF key flags (PFK01, PFK05, PFK10).

### UNUSED1Y.cpy — Unused/Deprecated Structure

Placeholder structure with generic field names. Not referenced by any active program.

---

## Entity Relationship Summary

```
Customer (CVCUS01Y)
    │
    ├──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
    │                                              │
    │                                              ├──► Tran-Cat-Balance (CVTRA01Y)
    │                                              │
    │                                              └──► Discount Group (CVTRA02Y)
    │
    └──────► Card (CVACT02Y)
                  │
                  └──► Transaction (CVTRA05Y) ──► Tran-Type (CVTRA03Y)
                                              ──► Tran-Category (CVTRA04Y)
                  │
                  └──► Pending Auth (CIPAUDTY) ──► Auth Request (CCPAURQY)
                                                ──► Auth Reply (CCPAURLY)

Security User (CSUSR01Y) — Independent entity for application access control
```
