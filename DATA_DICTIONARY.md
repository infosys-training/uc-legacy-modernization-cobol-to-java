# DATA DICTIONARY — CardDemo COBOL Estate

> Comprehensive field-level catalog of all copybooks in the CardDemo application.
> Fields are grouped by business entity. PIC clauses, data types, business meanings, and validation rules are extracted directly from source.

---

## Table of Contents

- [1. Account Entity](#1-account-entity)
- [2. Customer Entity](#2-customer-entity)
- [3. Card Entity](#3-card-entity)
- [4. Card-Account Cross-Reference Entity](#4-card-account-cross-reference-entity)
- [5. Transaction Entity](#5-transaction-entity)
- [6. Transaction Reference Data](#6-transaction-reference-data)
- [7. User Security Entity](#7-user-security-entity)
- [8. Authorization Entity (IMS Sub-App)](#8-authorization-entity-ims-sub-app)
- [9. Infrastructure / Framework Copybooks](#9-infrastructure--framework-copybooks)
- [10. Report Layout Copybooks](#10-report-layout-copybooks)
- [11. Export/Import Entity](#11-exportimport-entity)
- [12. DB2 Declaration Copybooks (Sub-App)](#12-db2-declaration-copybooks-sub-app)

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (VSAM KSDS)

**Record Length:** 300 bytes | **Referenced by:** 16 programs | **VSAM Dataset:** `AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (unsigned) | 11 | Account identifier — primary key | Must be unique; 11-digit numeric |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Account active/inactive flag | `'Y'` = Active, `'N'` = Inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (implied V) | 12 | Current account balance | Signed; 10 integer + 2 decimal digits. **Use BigDecimal in Java (precision=12, scale=2)** |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (implied V) | 12 | Credit limit for the account | Signed decimal; ACCT-CURR-BAL must not exceed this value |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (implied V) | 12 | Cash advance credit limit | Separate limit for cash transactions |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | 10 | Date account was opened | Format: YYYY-MM-DD; validated by CSUTLDTC |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Account expiration date | Format: YYYY-MM-DD; note: typo "EXPIRAION" in source |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | 10 | Date account was last reissued | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (implied V) | 12 | Current cycle credit total | Running total of credits in billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (implied V) | 12 | Current cycle debit total | Running total of debits in billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | Account holder ZIP code | May include ZIP+4 format |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Disclosure/interest rate group | Links to DIS-GROUP-RECORD (CVTRA02Y) for interest rates |
| FILLER | PIC X(178) | Filler | 178 | Reserved/unused space | Pads record to 300 bytes |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (VSAM KSDS)

**Record Length:** 500 bytes | **Referenced by:** 10 programs | **VSAM Dataset:** `AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric (unsigned) | 9 | Customer identifier — primary key | Must be unique; 9-digit numeric |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | 25 | Customer first name | Required; alphabetic characters |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | 25 | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | 25 | Customer last name | Required; alphabetic characters |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | 50 | Address line 1 | Required for active customers |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | 50 | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | 50 | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | 2 | US state code | **Validated against 50 US state codes in CSLKPCDY.cpy** |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | 3 | Country code | 3-letter ISO country code |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | 10 | ZIP/postal code | **Validated: first 3 digits checked against NANPA prefix table in CSLKPCDY** |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | 15 | Primary phone number | **Validated: area code checked against NANPA table in CSLKPCDY** |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | 15 | Secondary phone number | Same validation as PHONE-NUM-1 |
| CUST-SSN | PIC 9(09) | Numeric (unsigned) | 9 | Social Security Number | **Validated in COACTUPC: 9-digit format, non-zero** |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | 20 | Government-issued ID number | Driver's license, passport, etc. |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | 10 | Date of birth | Format: YYYY-MM-DD; validated for logical date range |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | 10 | Electronic funds transfer account | Linked bank account for payments |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | 1 | Primary cardholder indicator | `'Y'` = Primary, `'N'` = Authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (unsigned) | 3 | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | Filler | 168 | Reserved/unused space | Pads record to 500 bytes |

### CUSTREC.cpy — Customer Statement Record (Batch)

**Used by:** CBSTM03A (statement generation)

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| CUSTOMER-NAME | (group) | Group | — | Full customer name for statement header | Assembled from CUST-FIRST + LAST |
| CUSTOMER-ADDRESS | (group) | Group | — | Full address block for statement mailing | Multi-line address layout |

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (VSAM KSDS)

**Record Length:** 150 bytes | **Referenced by:** 10 programs | **VSAM Dataset:** `AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | 16 | Credit card number — primary key | 16-digit card number (stored as text for leading zeros) |
| CARD-ACCT-ID | PIC 9(11) | Numeric (unsigned) | 11 | Foreign key to Account | Must match existing ACCT-ID in CVACT01Y |
| CARD-CVV-CD | PIC 9(03) | Numeric (unsigned) | 3 | Card verification value | 3-digit CVV code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | 50 | Name embossed on physical card | Typically uppercase cardholder name |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 10 | Card expiration date | Format: YYYY-MM-DD; note: typo "EXPIRAION" in source |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 1 | Card active/inactive flag | `'Y'` = Active, `'N'` = Inactive |
| FILLER | PIC X(59) | Filler | 59 | Reserved/unused space | Pads record to 150 bytes |

### CVCRD01Y.cpy — Card Working Storage Areas

**Used by:** 7 programs (CICS online card programs) | **Purpose:** Screen navigation and card selection state

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| CCARD-AID | PIC X(5) | Alphanumeric | 5 | Captured AID key (attention identifier) | 88-level values: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | 8 | Next program to XCTL to | Valid CardDemo program name |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | 7 | Next BMS mapset to display | Valid mapset name |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | 7 | Next BMS map within mapset | Valid map name |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | 75 | Error message display area | Populated on validation failures |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | 75 | Return/success message area | 88-level CCARD-RETURN-MSG-OFF = LOW-VALUES |
| CC-ACCT-ID | PIC X(11) | Alphanumeric | 11 | Selected account ID | REDEFINES to PIC 9(11) via CC-ACCT-ID-N |
| CC-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Selected card number | REDEFINES to PIC 9(16) via CC-CARD-NUM-N |
| CC-CUST-ID | PIC X(09) | Alphanumeric | 9 | Selected customer ID | REDEFINES to PIC 9(9) via CC-CUST-ID-N |

---

## 4. Card-Account Cross-Reference Entity

### CVACT03Y.cpy — Card-Account Cross-Reference Record (VSAM KSDS)

**Record Length:** 50 bytes | **Referenced by:** 16 programs (most referenced data copybook) | **VSAM Dataset:** `AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number — primary key | Links to CARD-NUM in CVACT02Y |
| XREF-CUST-ID | PIC 9(09) | Numeric (unsigned) | 9 | Customer ID — foreign key | Links to CUST-ID in CVCUS01Y |
| XREF-ACCT-ID | PIC 9(11) | Numeric (unsigned) | 11 | Account ID — foreign key | Links to ACCT-ID in CVACT01Y |
| FILLER | PIC X(14) | Filler | 14 | Reserved/unused space | Pads record to 50 bytes |

**Relationships:**
```
Customer (CVCUS01Y) ──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
                                    │
                              Card (CVACT02Y)
```

---

## 5. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (VSAM KSDS)

**Record Length:** 350 bytes | **Referenced by:** 11 programs | **VSAM Dataset:** `AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | 16 | Transaction identifier — primary key | System-generated unique ID |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | Links to TRAN-TYPE in CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric (unsigned) | 4 | Transaction category code | Links to TRAN-CAT-CD in CVTRA04Y |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Transaction source/origin | E.g., POS, ATM, ONLINE, BATCH |
| TRAN-DESC | PIC X(100) | Alphanumeric | 100 | Transaction description | Free-text description of the transaction |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (implied V) | 11 | Transaction amount | **Signed: positive=credit, negative=debit. Use BigDecimal.** |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (unsigned) | 9 | Merchant identifier | Merchant registration number |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name | Business name where transaction occurred |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city | City of merchant location |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP code | ZIP code of merchant location |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number used for transaction | Links to XREF-CARD-NUM in CVACT03Y |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | 26 | Transaction origination timestamp | Format: YYYY-MM-DD-HH.MM.SS.NNNNNN |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | 26 | Transaction processing timestamp | Set when CBTRN02C posts the transaction |
| FILLER | PIC X(20) | Filler | 20 | Reserved/unused space | Pads record to 350 bytes |

### CVTRA06Y.cpy — Daily Transaction Input Record

**Record Length:** 350 bytes | **Used by:** CBTRN01C, CBTRN02C | **Dataset:** `AWS.M2.CARDDEMO.DALYTRAN.PS`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | 16 | Daily transaction ID | Input from daily transaction feed |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code | Must match valid type in TRANTYPE file |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric | 4 | Category code | Must match valid category in TRANCATG file |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | 10 | Source system | Origin of the daily transaction |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | 100 | Description | Free text |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | 11 | Transaction amount | **Signed decimal; use BigDecimal** |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | 9 | Merchant ID | Same as TRAN-MERCHANT-ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 50 | Merchant name | — |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 50 | Merchant city | — |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 10 | Merchant ZIP | — |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number | Must exist in CARDXREF for posting |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | 26 | Origination timestamp | — |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | 26 | Processing timestamp | Set during CBTRN02C posting |
| FILLER | PIC X(20) | Filler | 20 | Reserved | — |

---

## 6. Transaction Reference Data

### CVTRA03Y.cpy — Transaction Type Record (VSAM KSDS / DB2)

**Record Length:** 60 bytes | **VSAM Dataset:** `AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS` | **DB2 Table:** `CARDDEMO.TRANSACTION_TYPE`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | 2 | Transaction type code — primary key | 2-character code (e.g., "SA"=Sale, "RT"=Return) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Transaction type description | Human-readable description |
| FILLER | PIC X(08) | Filler | 8 | Reserved | — |

### CVTRA04Y.cpy — Transaction Category Record (VSAM KSDS / DB2)

**Record Length:** 60 bytes | **VSAM Dataset:** `AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS` | **DB2 Table:** `CARDDEMO.TRANSACTION_CATEGORY`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type code — composite key part 1 | FK to CVTRA03Y.TRAN-TYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric (unsigned) | 4 | Category code — composite key part 2 | 4-digit category within type |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | 50 | Category description | E.g., "Groceries", "Gas", "Travel" |
| FILLER | PIC X(04) | Filler | 4 | Reserved | — |

### CVTRA01Y.cpy — Transaction Category Balance Record (VSAM KSDS)

**Record Length:** 50 bytes | **VSAM Dataset:** `AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (unsigned) | 11 | Account ID — composite key part 1 | FK to CVACT01Y.ACCT-ID |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type — composite key part 2 | FK to CVTRA03Y.TRAN-TYPE |
| TRANCAT-CD | PIC 9(04) | Numeric (unsigned) | 4 | Category code — composite key part 3 | FK to CVTRA04Y.TRAN-CAT-CD |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (implied V) | 11 | Running balance for this category | Updated by CBTRN02C (posting) and CBACT04C (interest) |
| FILLER | PIC X(22) | Filler | 22 | Reserved | — |

### CVTRA02Y.cpy — Disclosure Group / Interest Rate Record (VSAM KSDS)

**Record Length:** 50 bytes | **VSAM Dataset:** `AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 10 | Account group ID — composite key part 1 | Matches ACCT-GROUP-ID in CVACT01Y |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 2 | Transaction type — composite key part 2 | FK to CVTRA03Y |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (unsigned) | 4 | Category code — composite key part 3 | FK to CVTRA04Y |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (implied V) | 6 | Interest rate for this group/type/category | Percentage rate (e.g., 19.99%) |
| FILLER | PIC X(28) | Filler | 28 | Reserved | — |

---

## 7. User Security Entity

### CSUSR01Y.cpy — User Security Record (VSAM KSDS)

**Record Length:** 80 bytes | **Referenced by:** 14 programs | **VSAM Dataset:** `AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | 8 | User login ID — primary key | Must be unique; used for CICS sign-on |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | 20 | User first name | Display name |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | 20 | User last name | Display name |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | 8 | User password | **SECURITY RISK: Stored in plain text. Must implement hashing (bcrypt/scrypt) in Java migration.** |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | 1 | User type flag | `'A'` = Admin (routes to COADM01C), `'U'` = Regular user (routes to COMEN01C) |
| SEC-USR-FILLER | PIC X(23) | Filler | 23 | Reserved | — |

---

## 8. Authorization Entity (IMS Sub-App)

### CIPAUDTY.cpy — Pending Authorization Detail Record (IMS Segment)

**Segment Type:** IMS dependent segment under CIPAUSMY | **Used by:** 8 programs

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | 3 | Authorization date (packed) | Composite key part 1 |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | 5 | Authorization time (packed) | Composite key part 2 |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | 6 | Original auth date (display) | Format: YYMMDD |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | 6 | Original auth time (display) | Format: HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number | Links to CVACT02Y |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | 4 | Authorization type | E.g., SALE, CASH, etc. |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | 4 | Card expiration date | Format: YYMM |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | 6 | ISO message type | E.g., 0100, 0110 |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | 6 | Source of auth request | POS terminal, ATM, etc. |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | 6 | Authorization ID code | Returned to merchant on approval |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | 2 | Response code | 88-level: `'00'` = PA-AUTH-APPROVED |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | 4 | Decline reason code | Populated when not approved |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | 6 | ISO processing code | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | 7 | Requested transaction amount | **COMP-3 packed; use BigDecimal** |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | 7 | Approved amount | May differ from requested (partial approval) |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | 4 | Merchant category code (MCC) | Standard ISO 18245 codes |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | 3 | Acquirer country code | ISO 3166 country code |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | 2 | Point-of-sale entry mode | E.g., 05=chip, 07=contactless |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric | 15 | Merchant ID | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | 22 | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | 13 | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | 2 | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | 9 | Merchant ZIP | — |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | 15 | Transaction ID | Links to posted transaction |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric | 1 | Match/settlement status | `'P'`=Pending, `'D'`=Declined, `'E'`=Expired, `'M'`=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | 1 | Fraud flag | `'F'`=Fraud confirmed, `'R'`=Fraud removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric | 8 | Date fraud was reported | Format: YYYYMMDD |
| FILLER | PIC X(17) | Filler | 17 | Reserved | — |

### CIPAUSMY.cpy — Pending Authorization Summary Record (IMS Root Segment)

**Segment Type:** IMS root segment | **Used by:** 7 programs

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|-----------|-------|-----------------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | 6 | Account ID — root segment key | Links to CVACT01Y |
| PA-CUST-ID | PIC 9(09) | Numeric | 9 | Customer ID | Links to CVCUS01Y |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric | 1 | Overall authorization status | Account-level status summary |
| PA-ACCOUNT-STATUS | PIC X(01) | Alphanumeric | 1 | Account status snapshot | Captured from ACCTDATA at auth time |

### CCPAURQY.cpy — Authorization Request Message (MQ)

**Used by:** COPAUA0C | **Purpose:** MQ request message layout

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | 6 | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | 6 | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | 4 | Auth type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | 4 | Card expiry |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | 6 | ISO message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | 6 | Source system |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | 6 | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | 14 | Transaction amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | 4 | MCC code |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | 3 | Country code |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | 2 | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | 15 | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | 22 | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | 13 | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | 2 | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | 9 | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | 15 | Transaction ID |

### CCPAURLY.cpy — Authorization Reply Message (MQ)

**Used by:** COPAUA0C | **Purpose:** MQ reply message layout

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | 16 | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | 15 | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | 6 | Auth ID code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | 2 | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | 4 | Reason code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | 14 | Approved amount |

### CCPAUERY.cpy — Error Log Record

**Used by:** Authorization sub-app programs | **Purpose:** Structured error logging

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|-----------------|
| ERR-DATE | PIC X(06) | Alphanumeric | 6 | Error date |
| ERR-TIME | PIC X(06) | Alphanumeric | 6 | Error time |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | 8 | Application name |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | 8 | Program name |
| ERR-LOCATION | PIC X(04) | Alphanumeric | 4 | Error location code |
| ERR-LEVEL | PIC X(01) | Alphanumeric | 1 | Error severity: `'L'`=Log, `'I'`=Info, `'W'`=Warning, `'C'`=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | 1 | Subsystem: `'A'`=App, `'C'`=CICS, `'I'`=IMS, `'D'`=DB2, `'M'`=MQ, `'F'`=File |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | 9 | Primary error code |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | 9 | Secondary error code |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | 50 | Error message text |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | 20 | Event key for correlation |

### IMS PCB Copybooks

| Copybook | Purpose |
|----------|---------|
| **PAUTBPCB.CPY** | IMS PCB (Program Communication Block) for pending auth database |
| **PADFLPCB.CPY** | IMS PCB for auth detail GSAM file |
| **PASFLPCB.CPY** | IMS PCB for auth summary GSAM file |
| **IMSFUNCS.cpy** | IMS function code constants (GU, GN, GNP, ISRT, DLET, REPL, etc.) |

---

## 9. Infrastructure / Framework Copybooks

### COCOM01Y.cpy — COMMAREA (Inter-Program Communication)

**Referenced by:** 21 programs | **Purpose:** Passed between all CICS programs via XCTL/LINK

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | 4 | Originating CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | 8 | Originating program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | 4 | Target CICS transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | 8 | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | 8 | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 1 | 88-level: `'A'`=CDEMO-USRTYP-ADMIN, `'U'`=CDEMO-USRTYP-USER |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 1 | 88-level: `0`=CDEMO-PGM-ENTER (first entry), `1`=CDEMO-PGM-REENTER |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | 9 | Selected customer ID |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | 25 | Customer first name |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | 25 | Customer middle name |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | 25 | Customer last name |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | 11 | Selected account ID |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | 1 | Account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | 16 | Selected card number |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | 7 | Last displayed BMS map |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | 7 | Last displayed BMS mapset |

### COTTL01Y.cpy — Screen Title Layout

**Referenced by:** 21 programs

| Field Name | PIC Clause | Value | Business Meaning |
|------------|-----------|-------|-----------------|
| CCDA-TITLE01 | PIC X(40) | `'      AWS Mainframe Modernization       '` | Application title line 1 |
| CCDA-TITLE02 | PIC X(40) | `'              CardDemo                  '` | Application title line 2 |
| CCDA-THANK-YOU | PIC X(40) | `'Thank you for using CCDA application... '` | Sign-off message |

### CSDAT01Y.cpy — Date/Time Working Storage

**Referenced by:** 21 programs | **Purpose:** Standard date/time fields populated via CICS ASKTIME/FORMATTIME

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| WS-CURDATE (group) | — | Group | Current date: YEAR(4) + MONTH(2) + DAY(2) |
| WS-CURDATE-N | PIC 9(08) | Numeric | REDEFINES WS-CURDATE as 8-digit number |
| WS-CURTIME (group) | — | Group | Current time: HH(2) + MM(2) + SS(2) + MS(2) |
| WS-CURDATE-MM-DD-YY | — | Edited | Formatted: MM/DD/YY |
| WS-CURTIME-HH-MM-SS | — | Edited | Formatted: HH:MM:SS |
| WS-TIMESTAMP | — | Edited | Full timestamp: YYYY-MM-DD HH:MM:SS.NNNNNN |

### CSMSG01Y.cpy — Standard Message Area

**Referenced by:** 21 programs | **Purpose:** Screen message display

### CSMSG02Y.cpy — Extended Message Area

**Referenced by:** 8 programs | **Purpose:** Additional message area for programs needing more message space

### COADM02Y.cpy — Admin Menu Options Table

**Referenced by:** COADM01C | **Purpose:** Defines 6 admin menu options with target program names

### COMEN02Y.cpy — Main Menu Options Table

**Referenced by:** COMEN01C | **Purpose:** Defines 11 user menu options with target program names and user-type restrictions

### CSSETATY.cpy — BMS Screen Attribute Setting Macro

**Referenced by:** COACTUPC (3×, via COPY REPLACING) | **Purpose:** Sets BMS field attributes (color/highlighting) based on validation flags. Uses COPY REPLACING with variables: `(TESTVAR1)`, `(SCRNVAR2)`, `(MAPNAME3)`.

### CSSTRPFY.cpy — PF Key Storage Routine

**Referenced by:** 7 programs (via COPY) | **Purpose:** EVALUATE block that maps EIBAID to CCARD-AID-xxx 88-level values (PFK01–PFK12, ENTER, CLEAR, PA1, PA2).

### CODATECN.cpy — Date Conversion Working Storage

**Used by:** CBACT01C | **Purpose:** Input/output areas for date format conversion (YYYYMMDD ↔ YYYY-MM-DD). Contains REDEFINES for both formats.

### CSUTLDPY.cpy — Date Validation Parameters

**Used by:** COACTUPC, COTRN02C | **Purpose:** Working storage for date validation utility (input date, output status, error messages)

### CSUTLDWY.cpy — Date Validation Working Storage

**Used by:** COACTUPC | **Purpose:** Working variables for date validation logic

### CSLKPCDY.cpy — Validation Lookup Data

**Referenced by:** COACTUPC, COCRDSLC | **Purpose:** Contains lookup tables for:
- **US State Codes** — All 50 state abbreviations for address validation
- **ZIP Code Prefixes** — Valid 3-digit ZIP prefixes mapped to states
- **Phone Area Codes** — NANPA (North American Numbering Plan) area code table

### COSTM01.CPY — Statement Generation Working Storage

**Used by:** CBSTM03A | **Purpose:** Working variables for statement generation (counters, totals, formatting)

### UNUSED1Y.cpy — Unused/Placeholder Copybook

**Referenced by:** None | **Purpose:** Placeholder for future use

---

## 10. Report Layout Copybooks

### CVTRA07Y.cpy — Daily Transaction Report Layout

**Used by:** CBTRN03C | **Purpose:** Report headers, detail lines, and totals layout

| Section | Fields | Purpose |
|---------|--------|---------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME, REPT-LONG-NAME, date range fields | Report identification |
| TRANSACTION-DETAIL-REPORT | Trans ID, Account ID, Type, Category, Source, Amount | Detail line per transaction |
| TRANSACTION-HEADER-1/2 | Column headers and separator line | Report formatting |
| REPORT-PAGE-TOTALS | REPT-PAGE-TOTAL (PIC +ZZZ,ZZZ,ZZZ.ZZ) | Page subtotal |
| REPORT-ACCOUNT-TOTALS | REPT-ACCOUNT-TOTAL | Account subtotal |
| REPORT-GRAND-TOTALS | REPT-GRAND-TOTAL | Grand total for report |

---

## 11. Export/Import Entity

### CVEXPORT.cpy — Export Record (Multi-Entity)

**Record Length:** 505 bytes | **Used by:** CBEXPORT, CBIMPORT | **Dataset:** `AWS.M2.CARDDEMO.EXPORT.DATA`

| Field Name | PIC Clause | Data Type | Bytes | Business Meaning |
|------------|-----------|-----------|-------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | 1 | Record type discriminator (C=Customer, A=Account, T=Transaction, X=XREF) |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | 26 | Export timestamp; REDEFINES to date + time components |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | 4 | Sequence number within export |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | 4 | Branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | 5 | Region code |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | 460 | Payload — REDEFINES per entity type |

**REDEFINES variants:**
- `EXPORT-CUSTOMER-DATA` — Customer fields with OCCURS 3 for address lines, OCCURS 2 for phones, COMP-3 for FICO score
- `EXPORT-ACCOUNT-DATA` — Account fields with COMP-3 for balance/credit-limit
- `EXPORT-TRANSACTION-DATA` — Transaction fields with COMP-3 for amount
- `EXPORT-CARD-XREF-DATA` — XREF fields

---

## 12. DB2 Declaration Copybooks (Sub-App)

### CSDB2RWY.cpy / CSDB2RPY.cpy — DB2 Working Storage

**Used by:** COTRTLIC, COTRTUPC | **Purpose:** DB2 cursor state variables, pagination counters, SQLCODE handling

### DCLTRTYP.dcl — DB2 Table Declaration (TRANSACTION_TYPE)

**Included via:** `EXEC SQL INCLUDE DCLTRTYP` | **DB2 Table:** `CARDDEMO.TRANSACTION_TYPE`

| Column | Host Variable | PIC Clause | Data Type |
|--------|--------------|-----------|-----------|
| TR_TYPE | DCL-TR-TYPE | PIC X(02) | CHAR(2) — Primary Key |
| TR_DESCRIPTION | DCL-TR-DESCRIPTION | PIC X(50) | VARCHAR(50) |

### DCLTRCAT.dcl — DB2 Table Declaration (TRANSACTION_CATEGORY)

**Included via:** `EXEC SQL INCLUDE DCLTRCAT` | **DB2 Table:** `CARDDEMO.TRANSACTION_CATEGORY`

### AUTHFRDS.dcl — DB2 Table Declaration (AUTHFRDS — Fraud)

**Included via:** `EXEC SQL INCLUDE AUTHFRDS` | **DB2 Table:** `CARDDEMO.AUTHFRDS`
