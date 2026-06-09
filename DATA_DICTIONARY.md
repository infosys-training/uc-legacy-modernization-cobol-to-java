# DATA DICTIONARY

> **Application:** CardDemo -- Credit Card Management System (Mainframe)
> **Repository:** infosys-training/uc-legacy-modernization-cobol-to-java
> **Generated:** 2026-06-09

---

## Overview

This dictionary catalogs every copybook in the CardDemo estate, organized by business entity. For each field: name, PIC clause, data type interpretation, inferred business meaning, and validation rules (where applicable).

**Notation:**
- `PIC X(n)` = Alphanumeric, n characters
- `PIC 9(n)` = Unsigned numeric, n digits
- `PIC S9(n)V99` = Signed numeric with implied 2-decimal-place precision
- `COMP` = Binary integer
- `COMP-3` = Packed decimal (BCD)
- Level 88 entries = Condition names (valid values / boolean flags)

---

## 1. Account Entity

### CVACT01Y.cpy -- Account Master Record (300 bytes)

**Source:** `app/cpy/CVACT01Y.cpy`
**Used by:** CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C, and more (13+ programs)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | ACCOUNT-RECORD | -- | Group | Root account record | 300-byte fixed length |
| 05 | ACCT-ID | PIC 9(11) | Numeric | **Primary key** -- Unique account identifier | Required, 11-digit |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Account active flag | 'Y' = Active, 'N' = Inactive |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed Decimal | Current account balance | Signed; currency value with 2 implied decimals |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Maximum credit limit | Must be > 0 for active accounts |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Cash advance credit limit | Subset of total credit limit |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Alpha | Date account was opened | Format: YYYY-MM-DD; validated via EDIT-DATE-CCYYMMDD |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Alpha | Account expiration date | Format: YYYY-MM-DD; must be > open date |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Alpha | Date card was last reissued | Format: YYYY-MM-DD |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed Decimal | Credits posted in current cycle | Running total, reset at cycle close |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed Decimal | Debits posted in current cycle | Running total, reset at cycle close |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alpha | Account holder ZIP code | Validated against state+ZIP table in CSLKPCDY |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alpha | Disclosure/interest rate group | Links to DISCGRP file for rate lookup |
| 05 | FILLER | PIC X(178) | Alpha | Reserved/padding | Fills record to 300 bytes |

---

## 2. Customer Entity

### CVCUS01Y.cpy -- Customer Master Record (500 bytes)

**Source:** `app/cpy/CVCUS01Y.cpy`
**Used by:** CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CUSTOMER-RECORD | -- | Group | Root customer record | 500-byte fixed length |
| 05 | CUST-ID | PIC 9(09) | Numeric | **Primary key** -- Unique customer identifier | Required, 9-digit |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alpha | Customer first name | Required |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alpha | Customer middle name | Optional |
| 05 | CUST-LAST-NAME | PIC X(25) | Alpha | Customer last name | Required |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alpha | Street address line 1 | Required |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alpha | Street address line 2 | Optional |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alpha | Street address line 3 | Optional |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code | Validated against 50 state codes in CSLKPCDY |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code | |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alpha | ZIP / postal code | Cross-validated with state in CSLKPCDY |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alpha | Primary phone number | Area code validated via NANPA list in CSLKPCDY |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alpha | Secondary phone number | Same validation as primary |
| 05 | CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit; validated format (not 000-xx-xxxx, etc.) |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alpha | Government-issued ID number | |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Alpha | Date of birth | Format: YYYY-MM-DD |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alpha | Electronic funds transfer account | For bill payment routing |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary card holder indicator | 'Y'/'N' |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range: 300-850 |
| 05 | FILLER | PIC X(remainder) | Alpha | Reserved | Fills record to 500 bytes |

---

## 3. Card Entity

### CVACT02Y.cpy -- Card Data Record (150 bytes)

**Source:** `app/cpy/CVACT02Y.cpy`
**Used by:** CBACT02C, CBEXPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARD-RECORD | -- | Group | Root card record | 150-byte fixed length |
| 05 | CARD-NUM | PIC X(16) | Alpha | **Primary key** -- Card number | 16-character card number |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric | **Foreign key** -- Links to Account | Must exist in ACCTFILE |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit CVV |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alpha | Name embossed on card | |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Alpha | Card expiration date | Format: YYYY-MM-DD; month+year validated separately |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card active flag | 'Y' = Active, 'N' = Inactive |
| 05 | FILLER | PIC X(remainder) | Alpha | Reserved | Fills to 150 bytes |

---

## 4. Card-Account Cross-Reference Entity

### CVACT03Y.cpy -- Cross-Reference Record (50 bytes)

**Source:** `app/cpy/CVACT03Y.cpy`
**Used by:** CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C (11+ programs)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARD-XREF-RECORD | -- | Group | Cross-reference linking card to account/customer | 50-byte fixed length |
| 05 | XREF-CARD-NUM | PIC X(16) | Alpha | **Primary key** -- Card number | Must exist in CARDFILE |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric | **Foreign key** -- Account identifier | Must exist in ACCTFILE |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric | **Foreign key** -- Customer identifier | Must exist in CUSTFILE |
| 05 | FILLER | PIC X(14) | Alpha | Reserved | |

---

## 5. Transaction Entity

### CVTRA05Y.cpy -- Transaction Master Record (350 bytes)

**Source:** `app/cpy/CVTRA05Y.cpy`
**Used by:** CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COBIL00C, COTRN00C, COTRN01C, COTRN02C, CORPT00C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-RECORD | -- | Group | Transaction record | 350-byte fixed length |
| 05 | TRAN-ID | PIC X(16) | Alpha | **Primary key** -- Transaction identifier | Unique; system-generated |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Links to CVTRA03Y (transaction type lookup) |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Links to CVTRA04Y (category lookup) |
| 05 | TRAN-SOURCE | PIC X(10) | Alpha | Transaction source/channel | |
| 05 | TRAN-DESC | PIC X(100) | Alpha | Transaction description | Free-text |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed Decimal | Transaction amount | Signed; debits negative, credits positive |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alpha | Card number for this transaction | Must exist in CARDXREF |
| 05 | TRAN-MERCHANT-ID | PIC X(09) | Alpha | Merchant identifier | |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name | |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP code | |
| 05 | TRAN-ORIG-TS | PIC X(26) | Alpha | Transaction origination timestamp | Format: YYYY-MM-DD HH:MM:SS.ffffff |
| 05 | TRAN-PROC-TS | PIC X(26) | Alpha | Transaction processing timestamp | Populated at posting time |
| 05 | FILLER | PIC X(remainder) | Alpha | Reserved | Fills to 350 bytes |

### CVTRA06Y.cpy -- Daily Transaction Input Record

**Source:** `app/cpy/CVTRA06Y.cpy`
**Used by:** CBTRN01C, CBTRN02C

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | DALYTRAN-RECORD | -- | Group | Daily transaction input (pre-posting) |
| 05 | DALYTRAN-ID | PIC X(16) | Alpha | Daily transaction ID |
| 05 | DALYTRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| 05 | DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Category code |
| 05 | DALYTRAN-SOURCE | PIC X(10) | Alpha | Source channel |
| 05 | DALYTRAN-DESC | PIC X(100) | Alpha | Description |
| 05 | DALYTRAN-AMT | PIC S9(09)V99 | Signed Decimal | Amount |
| 05 | DALYTRAN-CARD-NUM | PIC X(16) | Alpha | Card number |
| 05 | DALYTRAN-MERCHANT-* | Various | Various | Merchant details |
| 05 | DALYTRAN-ORIG-TS | PIC X(26) | Alpha | Origination timestamp |

---

## 6. Transaction Reference Data

### CVTRA01Y.cpy -- Transaction Category Balance Record

**Source:** `app/cpy/CVTRA01Y.cpy`
**Used by:** CBACT04C, CBTRN02C

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | TRAN-CAT-BAL-RECORD | -- | Group | Running balance by transaction category |
| 05 | TCATBAL-ACCT-ID | PIC 9(11) | Numeric | Account identifier |
| 05 | TCATBAL-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| 05 | TCATBAL-CAT-CD | PIC 9(04) | Numeric | Category code |
| 05 | TCATBAL-BAL | PIC S9(10)V99 | Signed Decimal | Running balance for this category |

### CVTRA02Y.cpy -- Discount/Interest Rate Group Record

**Source:** `app/cpy/CVTRA02Y.cpy`
**Used by:** CBACT04C

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | DIS-INT-RATE-RECORD | -- | Group | Interest/discount rate by group |
| 05 | DIS-ACCT-GROUP-ID | PIC X(10) | Alpha | Group identifier (links to ACCT-GROUP-ID) |
| 05 | DIS-INT-RATE | PIC S9(03)V99 | Signed Decimal | Interest rate percentage |

### CVTRA03Y.cpy -- Transaction Type Lookup

**Source:** `app/cpy/CVTRA03Y.cpy`
**Used by:** CBTRN03C

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | TRAN-TYPE-RECORD | -- | Group | Transaction type definition |
| 05 | TRAN-TYPE | PIC X(02) | Alpha | **Primary key** -- Type code |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alpha | Type description |

### CVTRA04Y.cpy -- Transaction Category Lookup

**Source:** `app/cpy/CVTRA04Y.cpy`
**Used by:** CBTRN03C

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | TRAN-CAT-RECORD | -- | Group | Transaction category definition |
| 05 | TRAN-CAT-KEY | PIC 9(04) | Numeric | **Primary key** -- Category code |
| 05 | TRAN-CAT-TYPE-CD | PIC X(02) | Alpha | Parent type code |
| 05 | TRAN-CAT-DESC | PIC X(50) | Alpha | Category description |

### CVTRA07Y.cpy -- Date Parameter Record

**Source:** `app/cpy/CVTRA07Y.cpy`
**Used by:** CBTRN03C

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | DATE-PARM-RECORD | -- | Group | Report date range parameter |
| 05 | DATE-PARM-START | PIC X(10) | Alpha | Report start date |
| 05 | DATE-PARM-END | PIC X(10) | Alpha | Report end date |

---

## 7. User Security Entity

### CSUSR01Y.cpy -- User Security Record

**Source:** `app/cpy/CSUSR01Y.cpy`
**Used by:** COSGN00C, COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COUSR00C-03C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | SEC-USER-DATA | -- | Group | User security record | |
| 05 | SEC-USR-ID | PIC X(08) | Alpha | **Primary key** -- User login ID | Required; unique |
| 05 | SEC-USR-FNAME | PIC X(20) | Alpha | User first name | |
| 05 | SEC-USR-LNAME | PIC X(20) | Alpha | User last name | |
| 05 | SEC-USR-PWD | PIC X(08) | Alpha | User password | **RISK:** Stored in clear text; no hashing |
| 05 | SEC-USR-TYPE | PIC X(01) | Alpha | User type | 'A' = Admin, 'U' = Regular user |

---

## 8. Pending Authorization Entity (IMS)

### CIPAUSMY.cpy -- Auth Summary Segment (IMS Root)

**Source:** `app/app-authorization-ims-db2-mq/cpy/CIPAUSMY.cpy`
**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | PA-ACCT-ID | PIC S9(11) COMP-3 | Packed Decimal | Account identifier (segment key) |
| 05 | PA-CUST-ID | PIC 9(09) | Numeric | Customer identifier |
| 05 | PA-AUTH-STATUS | PIC X(01) | Alpha | Overall authorization status |
| 05 | PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alpha array | Account status codes (5 slots) |
| 05 | PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed Decimal | Credit limit |
| 05 | PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed Decimal | Cash advance limit |
| 05 | PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed Decimal | Outstanding credit balance |
| 05 | PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed Decimal | Outstanding cash balance |
| 05 | PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of approved authorizations |
| 05 | PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of declined authorizations |
| 05 | PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed Decimal | Total approved auth amount |
| 05 | PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed Decimal | Total declined auth amount |

### CIPAUDTY.cpy -- Auth Detail Segment (IMS Child)

**Source:** `app/app-authorization-ims-db2-mq/cpy/CIPAUDTY.cpy`
**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | PA-AUTHORIZATION-KEY | -- | Group | Composite key for auth detail | |
| 10 | PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed Decimal | Authorization date (packed) | |
| 10 | PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed Decimal | Authorization time (packed) | |
| 05 | PA-AUTH-ORIG-DATE | PIC X(06) | Alpha | Original auth date | YYMMDD |
| 05 | PA-AUTH-ORIG-TIME | PIC X(06) | Alpha | Original auth time | HHMMSS |
| 05 | PA-CARD-NUM | PIC X(16) | Alpha | Card number | |
| 05 | PA-AUTH-TYPE | PIC X(04) | Alpha | Authorization type | |
| 05 | PA-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry | YYMM |
| 05 | PA-MESSAGE-TYPE | PIC X(06) | Alpha | Message type code | |
| 05 | PA-MESSAGE-SOURCE | PIC X(06) | Alpha | Originating source | |
| 05 | PA-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization ID code | |
| 05 | PA-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code | '00' = Approved |
| 05 | PA-AUTH-RESP-REASON | PIC X(04) | Alpha | Decline/approval reason | |
| 05 | PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | |
| 05 | PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed Decimal | Requested amount | |
| 05 | PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed Decimal | Approved amount | |
| 05 | PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | Merchant category (MCC) | |
| 05 | PA-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country | |
| 05 | PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | Point of sale entry mode | |
| 05 | PA-MERCHANT-ID | PIC X(15) | Alpha | Merchant identifier | |
| 05 | PA-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name | |
| 05 | PA-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city | |
| 05 | PA-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state | |
| 05 | PA-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP code | |
| 05 | PA-TRANSACTION-ID | PIC X(15) | Alpha | Transaction reference ID | |
| 05 | PA-MATCH-STATUS | PIC X(01) | Alpha | Match status | 88: P=Pending, D=Declined, E=Expired, M=Matched |
| 05 | PA-AUTH-FRAUD | PIC X(01) | Alpha | Fraud flag | 88: F=Confirmed, R=Removed |
| 05 | PA-FRAUD-RPT-DATE | PIC X(08) | Alpha | Fraud report date | YYYYMMDD |

---

## 9. MQ Message Structures

### CCPAURQY.cpy -- Authorization Request Message

**Source:** `app/app-authorization-ims-db2-mq/cpy/CCPAURQY.cpy`
**Used by:** COPAUA0C

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | PA-RQ-AUTH-DATE | PIC X(06) | Alpha | Request date |
| 05 | PA-RQ-AUTH-TIME | PIC X(06) | Alpha | Request time |
| 05 | PA-RQ-CARD-NUM | PIC X(16) | Alpha | Card number |
| 05 | PA-RQ-AUTH-TYPE | PIC X(04) | Alpha | Authorization type |
| 05 | PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry |
| 05 | PA-RQ-MESSAGE-TYPE | PIC X(06) | Alpha | Message type |
| 05 | PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alpha | Source system |
| 05 | PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| 05 | PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited Numeric | Requested amount |
| 05 | PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | MCC code |
| 05 | PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country |
| 05 | PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| 05 | PA-RQ-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID |
| 05 | PA-RQ-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name |
| 05 | PA-RQ-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city |
| 05 | PA-RQ-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state |
| 05 | PA-RQ-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP |
| 05 | PA-RQ-TRANSACTION-ID | PIC X(15) | Alpha | Transaction reference |

### CCPAURLY.cpy -- Authorization Response Message

**Source:** `app/app-authorization-ims-db2-mq/cpy/CCPAURLY.cpy`
**Used by:** COPAUA0C

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | PA-RL-CARD-NUM | PIC X(16) | Alpha | Card number |
| 05 | PA-RL-TRANSACTION-ID | PIC X(15) | Alpha | Transaction reference |
| 05 | PA-RL-AUTH-ID-CODE | PIC X(06) | Alpha | Auth ID assigned |
| 05 | PA-RL-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code |
| 05 | PA-RL-AUTH-RESP-REASON | PIC X(04) | Alpha | Response reason |
| 05 | PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited Numeric | Approved amount |

### CCPAUERY.cpy -- Authorization Error Log Record

**Source:** `app/app-authorization-ims-db2-mq/cpy/CCPAUERY.cpy`
**Used by:** COPAUA0C

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 05 | ERR-DATE | PIC X(06) | Alpha | Error date |
| 05 | ERR-TIME | PIC X(06) | Alpha | Error time |
| 05 | ERR-APPLICATION | PIC X(08) | Alpha | Application ID |
| 05 | ERR-PROGRAM | PIC X(08) | Alpha | Program name |
| 05 | ERR-LOCATION | PIC X(04) | Alpha | Error location code |
| 05 | ERR-LEVEL | PIC X(01) | Alpha | Severity | L=Log, I=Info, W=Warning, C=Critical |
| 05 | ERR-SUBSYSTEM | PIC X(01) | Alpha | Subsystem origin | A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File |
| 05 | ERR-CODE-1 | PIC X(09) | Alpha | Primary error code |
| 05 | ERR-CODE-2 | PIC X(09) | Alpha | Secondary error code |
| 05 | ERR-MESSAGE | PIC X(50) | Alpha | Error message text |
| 05 | ERR-EVENT-KEY | PIC X(20) | Alpha | Associated event key |

---

## 10. Application Framework Copybooks

### COCOM01Y.cpy -- CICS Communication Area (COMMAREA)

**Source:** `app/cpy/COCOM01Y.cpy`
**Used by:** All 21 CICS programs

| Level | Field Name | PIC Clause | Data Type | Business Meaning |
|-------|-----------|------------|-----------|-----------------|
| 01 | CARDDEMO-COMMAREA | -- | Group | Inter-program communication buffer |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alpha | Source transaction ID |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alpha | Source program name |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alpha | Target transaction ID |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alpha | Target program name |
| 10 | CDEMO-USER-ID | PIC X(08) | Alpha | Authenticated user ID |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alpha | User type | 88: 'A'=Admin, 'U'=User |
| 10 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program entry context | 88: 0=Enter, 1=Re-enter |
| 10 | CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer context |
| 10 | CDEMO-CUST-FNAME | PIC X(25) | Alpha | Customer first name |
| 10 | CDEMO-CUST-MNAME | PIC X(25) | Alpha | Customer middle name |
| 10 | CDEMO-CUST-LNAME | PIC X(25) | Alpha | Customer last name |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account context |
| 10 | CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Account status |
| 10 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card context |
| 10 | CDEMO-LAST-MAP | PIC X(7) | Alpha | Last BMS map sent |
| 10 | CDEMO-LAST-MAPSET | PIC X(7) | Alpha | Last BMS mapset |

### COADM02Y.cpy -- Admin Menu Options

**Source:** `app/cpy/COADM02Y.cpy`
**Used by:** COADM01C

Defines 6 admin menu options with program routing:

| Option | Description | Target Program |
|--------|-------------|---------------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |
| 5 | Transaction Type List/Update (DB2) | COTRTLIC |
| 6 | Transaction Type Maintenance (DB2) | COTRTUPC |

### COMEN02Y.cpy -- Main Menu Options

**Source:** `app/cpy/COMEN02Y.cpy`
**Used by:** COMEN01C

Defines 11 main menu options:

| Option | Description | Target Program | User Type |
|--------|-------------|---------------|-----------|
| 1 | Account View | COACTVWC | U |
| 2 | Account Update | COACTUPC | U |
| 3 | Credit Card List | COCRDLIC | U |
| 4 | Credit Card View | COCRDSLC | U |
| 5 | Credit Card Update | COCRDUPC | U |
| 6 | Transaction List | COTRN00C | U |
| 7 | Transaction View | COTRN01C | U |
| 8 | Transaction Add | COTRN02C | U |
| 9 | Transaction Reports | CORPT00C | U |
| 10 | Bill Payment | COBIL00C | U |
| 11 | Pending Authorization View | COPAUS0C | U |

### CSDAT01Y.cpy -- Date/Time Working Storage

**Source:** `app/cpy/CSDAT01Y.cpy`
**Used by:** All CICS programs (screen header display)

| Level | Field Name | PIC Clause | Business Meaning |
|-------|-----------|------------|-----------------|
| 05 | WS-CURDATE | Group | Current date (YYYYMMDD) |
| 05 | WS-CURTIME | Group | Current time (HHMMSSMS) |
| 05 | WS-CURDATE-MM-DD-YY | Group | Formatted date (MM/DD/YY) |
| 05 | WS-CURTIME-HH-MM-SS | Group | Formatted time (HH:MM:SS) |
| 05 | WS-TIMESTAMP | Group | Full timestamp (YYYY-MM-DD HH:MM:SS.ffffff) |

### COTTL01Y.cpy -- Screen Title Constants

**Source:** `app/cpy/COTTL01Y.cpy`

| Field Name | Value | Business Meaning |
|-----------|-------|-----------------|
| CCDA-TITLE01 | "AWS Mainframe Modernization" | Application title line 1 |
| CCDA-TITLE02 | "CardDemo" | Application title line 2 |
| CCDA-THANK-YOU | "Thank you for using CCDA application..." | Logout message |

### CODATECN.cpy -- Date Conversion Record

**Source:** `app/cpy/CODATECN.cpy`
**Used by:** CBACT01C (passed to COBDATFT assembler)

Defines input/output record for date format conversion between YYYYMMDD and YYYY-MM-DD formats.

### CSLKPCDY.cpy -- Validation Lookup Repository

**Source:** `app/cpy/CSLKPCDY.cpy`
**Used by:** COACTUPC

Contains three lookup tables:
1. **NANPA phone area codes** -- 300+ valid North American area codes (level 88 VALID-PHONE-AREA-CODE)
2. **US state codes** -- 50 state abbreviations + DC, territories (level 88 VALID-US-STATE-CD)
3. **State-to-ZIP prefix mapping** -- First 2 digits of ZIP validated against state code

### CVEXPORT.cpy -- Export/Import Record Layout

**Source:** `app/cpy/CVEXPORT.cpy`
**Used by:** CBEXPORT, CBIMPORT

Multi-record type layout for data migration between branches. Contains record type indicator and union of customer, account, xref, transaction, and card record layouts.

---

## 11. IMS Infrastructure Copybooks

### IMSFUNCS.cpy -- IMS DL/I Function Codes

**Source:** `app/app-authorization-ims-db2-mq/cpy/IMSFUNCS.cpy`

| Field Name | Value | DL/I Function |
|-----------|-------|--------------|
| FUNC-GU | 'GU  ' | Get Unique (direct read) |
| FUNC-GHU | 'GHU ' | Get Hold Unique (read for update) |
| FUNC-GN | 'GN  ' | Get Next (sequential read) |
| FUNC-GHN | 'GHN ' | Get Hold Next |
| FUNC-GNP | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | 'REPL' | Replace (update) |
| FUNC-ISRT | 'ISRT' | Insert |
| FUNC-DLET | 'DLET' | Delete |

### PCB Copybooks (PAUTBPCB, PASFLPCB, PADFLPCB)

IMS Program Communication Block definitions for the three PCBs used in the authorization database:

| Copybook | PCB Name | Purpose |
|----------|----------|---------|
| PAUTBPCB.CPY | PAUTBPCB | Primary auth database PCB |
| PASFLPCB.CPY | PASFLPCB | Sequential forward log PCB |
| PADFLPCB.CPY | PADFLPCB | Detail forward log PCB |

Each contains: DBDNAME, SEG-LEVEL, PCB-STATUS, PCB-PROCOPT, SEG-NAME, KEYFB fields.

---

## 12. DB2 Copybooks

### CSDB2RPY.cpy / CSDB2RWY.cpy -- DB2 Communication Areas

**Source:** `app/app-transaction-type-db2/cpy/`
**Used by:** COTRTLIC, COTRTUPC, COBTUPDT

Working storage and result areas for DB2 SQL communication (SQLCA equivalent fields, cursor management variables).

---

## 13. Data Entity Relationships

```
Customer (CVCUS01Y)
    |
    | CUST-ID (1:N)
    v
Card-XREF (CVACT03Y) --- XREF-ACCT-ID (N:1) ---> Account (CVACT01Y)
    |                                                    |
    | XREF-CARD-NUM (1:1)                               | ACCT-GROUP-ID
    v                                                    v
Card (CVACT02Y)                                  Discount Group (CVTRA02Y)
    |
    | CARD-NUM
    v
Transaction (CVTRA05Y) --- TRAN-TYPE-CD ---> Transaction Type (CVTRA03Y)
    |                  --- TRAN-CAT-CD  ---> Transaction Category (CVTRA04Y)
    |
    v
Tran Category Balance (CVTRA01Y) --- per Account + Type + Category

Auth Summary (CIPAUSMY) --- PA-ACCT-ID ---> Account (CVACT01Y)
    |
    | 1:N (IMS parent-child)
    v
Auth Detail (CIPAUDTY) --- PA-CARD-NUM ---> Card-XREF
```
