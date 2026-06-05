# DATA DICTIONARY — CardDemo COBOL Estate

> Field-level documentation of all copybooks in the CardDemo application, grouped by business entity.

## Summary

| Metric | Count |
|--------|-------|
| Copybooks Documented | 39 |
| Business Entities | 8 |
| Data Fields | ~200+ |
| VSAM Record Layouts | 9 |
| IMS Segment Layouts | 2 |
| DB2 Table Layouts | 2 |

---

## 1. Account Entity

### CVACT01Y.cpy — Account Master Record

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| ACCT-ID | PIC 9(11) | Numeric | Account identifier (primary key) | 11-digit numeric, VSAM KSDS key at offset 0 |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Active/inactive flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance | Signed with 2 decimal places |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Credit limit | Max $9,999,999,999.99 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | Separate from purchase limit |
| ACCT-OPEN-DATE | PIC X(10) | Alpha | Account open date | Format: CCYYMMDD + filler |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alpha | Account expiration date | Intentional typo in source (EXPIRAION) |
| ACCT-REISSUE-DATE | PIC X(10) | Alpha | Card reissue date | Last reissue timestamp |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credits | Sum of credits this billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debits | Sum of debits this billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alpha | Account ZIP code | 5+4 format or international postal |
| ACCT-GROUP-ID | PIC X(10) | Alpha | Disclosure/interest rate group | Links to DISCGRP for rate lookup |
| FILLER | PIC X(178) | — | Reserved | Pads record to 300 bytes |

**Record size:** 300 bytes | **Used by:** CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COACCT01, COPAUA0C, COPAUS0C

### CVTRA01Y.cpy — Transaction Category Balance Record

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| TRAN-CAT-KEY (group) | — | Group | Composite key | |
| → TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | Foreign key to ACCT-ID |
| → TRANCAT-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Links to TRANTYPE |
| → TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code | Links to TRANCATG |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance for this category | Accumulated by CBTRN02C |
| FILLER | PIC X(22) | — | Reserved | Pads to 50 bytes |

**Record size:** 50 bytes | **Used by:** CBACT04C, CBTRN02C

### CVTRA02Y.cpy — Disclosure/Interest Rate Group Record

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| DIS-GROUP-KEY (group) | — | Group | Composite key | |
| → DIS-ACCT-GROUP-ID | PIC X(10) | Alpha | Group identifier | Links to ACCT-GROUP-ID |
| → DIS-TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type | |
| → DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category | |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Annual interest rate (%) | Used by CBACT04C for interest calc |
| FILLER | PIC X(28) | — | Reserved | Pads to 50 bytes |

**Record size:** 50 bytes | **Used by:** CBACT04C

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Master Record

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| CUST-ID | PIC 9(09) | Numeric | Customer identifier (primary key) | 9-digit numeric |
| CUST-FIRST-NAME | PIC X(25) | Alpha | First name | |
| CUST-MIDDLE-NAME | PIC X(25) | Alpha | Middle name | |
| CUST-LAST-NAME | PIC X(25) | Alpha | Last name | |
| CUST-ADDR-LINE-1 | PIC X(50) | Alpha | Address line 1 | |
| CUST-ADDR-LINE-2 | PIC X(50) | Alpha | Address line 2 | |
| CUST-ADDR-LINE-3 | PIC X(50) | Alpha | Address line 3 | |
| CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code | Validated against CSLKPCDY 50-state table |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code | |
| CUST-ADDR-ZIP | PIC X(10) | Alpha | ZIP code | Validated against CSLKPCDY ZIP prefix table |
| CUST-PHONE-NUM-1 | PIC X(15) | Alpha | Primary phone | Area code validated against CSLKPCDY NANPA table |
| CUST-PHONE-NUM-2 | PIC X(15) | Alpha | Secondary phone | Same validation as primary |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit; validated for non-zero, format checks in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alpha | Government-issued ID | Passport, driver's license, etc. |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alpha | Date of birth | CCYYMMDD format; validated via CSUTLDWY |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alpha | EFT/ACH account link | Electronic funds transfer account |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary cardholder flag | 'Y'/'N' |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | — | Reserved | Pads record to ~500 bytes |

**Record size:** ~500 bytes | **Used by:** CBCUS01C, CBEXPORT, CBIMPORT, CBSTM03A, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C

### CUSTREC.cpy — Customer Record (Alternative Layout)

Identical field structure to CVCUS01Y.cpy with minor formatting differences. Used as an alternative import/export layout.

### CSUSR01Y.cpy — User Security Record

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| SEC-USR-ID | PIC X(08) | Alpha | User login ID | Primary key in USRSEC file |
| SEC-USR-FNAME | PIC X(20) | Alpha | First name | |
| SEC-USR-LNAME | PIC X(20) | Alpha | Last name | |
| SEC-USR-PWD | PIC X(08) | Alpha | Password | Plain text (legacy pattern) |
| SEC-USR-TYPE | PIC X(01) | Alpha | User type | 'A' = admin, 'U' = regular user |
| SEC-USR-FILLER | PIC X(23) | — | Reserved | Pads to 80 bytes |

**Record size:** 80 bytes | **Used by:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| CARD-NUM | PIC X(16) | Alpha | Card number (primary key) | 16-digit card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | Foreign key to ACCT-ID |
| CARD-CVV-CD | PIC 9(03) | Numeric | CVV security code | 3-digit |
| CARD-EMBOSSED-NAME | PIC X(50) | Alpha | Name embossed on card | |
| CARD-EXPIRAION-DATE | PIC X(10) | Alpha | Card expiration date | Intentional typo (EXPIRAION) |
| CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card active flag | 'Y'/'N' |
| FILLER | PIC X(59) | — | Reserved | Pads to 150 bytes |

**Record size:** 150 bytes | **Used by:** CBACT02C, CBEXPORT, CBIMPORT, CBSTM03A, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COTRTLIC, COTRTUPC

### CVACT03Y.cpy — Card Cross-Reference Record

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| XREF-CARD-NUM | PIC X(16) | Alpha | Card number | Primary key; links to CARD-NUM |
| XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID | Foreign key to CUST-ID |
| XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID | Foreign key to ACCT-ID |
| FILLER | PIC X(14) | — | Reserved | Pads to 50 bytes |

**Record size:** 50 bytes | **Used by:** CBACT03C, CBACT04C, CBEXPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, COACTUPC, COACTVWC, COTRN02C, COPAUA0C, COPAUS0C

### CVCRD01Y.cpy — Card Detail Working Storage

Defines working-storage fields for card detail screens. Contains REDEFINES of card number and account ID for numeric operations:

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| CC-ACCT-ID-N | PIC 9(11) | Numeric | Numeric redefines of account ID |
| CC-CARD-NUM-N | PIC 9(16) | Numeric | Numeric redefines of card number |

**Used by:** COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (Master)

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| TRAN-ID | PIC X(16) | Alpha | Transaction identifier | System-generated; unique |
| TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Links to TRANTYPE file |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category | Links to TRANCATG file |
| TRAN-SOURCE | PIC X(10) | Alpha | Origination source | Online, ATM, POS, etc. |
| TRAN-DESC | PIC X(100) | Alpha | Description | Free-text description |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Negative = credit/refund |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | |
| TRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name | |
| TRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP | |
| TRAN-CARD-NUM | PIC X(16) | Alpha | Card used | Foreign key to CARD-NUM |
| TRAN-ORIG-TS | PIC X(26) | Alpha | Origination timestamp | YYYY-MM-DD HH:MM:SS.ffffff |
| TRAN-PROC-TS | PIC X(26) | Alpha | Processing timestamp | Set by CBTRN02C at posting |
| FILLER | PIC X(20) | — | Reserved | Pads to ~350 bytes |

**Record size:** ~350 bytes | **Used by:** CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, COBIL00C, COTRN00C, COTRN01C, COTRN02C, CORPT00C

### CVTRA06Y.cpy — Daily Transaction Input Record

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| DALYTRAN-ID | PIC X(16) | Alpha | Transaction ID from daily feed | |
| DALYTRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Category code | |
| DALYTRAN-SOURCE | PIC X(10) | Alpha | Source channel | |
| DALYTRAN-DESC | PIC X(100) | Alpha | Description | |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Amount | |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name | |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP | |
| DALYTRAN-CARD-NUM | PIC X(16) | Alpha | Card number | Validated against XREF file |
| DALYTRAN-ORIG-TS | PIC X(26) | Alpha | Origination timestamp | |
| DALYTRAN-PROC-TS | PIC X(26) | Alpha | Processing timestamp | |
| FILLER | PIC X(20) | — | Reserved | |

**Record size:** ~350 bytes | **Used by:** CBTRN01C, CBTRN02C

### CVTRA03Y.cpy — Transaction Type Reference

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| TRAN-TYPE | PIC X(02) | Alpha | Type code (primary key) | e.g., 'SA' = sale, 'CR' = credit |
| TRAN-TYPE-DESC | PIC X(50) | Alpha | Type description | |
| FILLER | PIC X(08) | — | Reserved | Pads to 60 bytes |

**Record size:** 60 bytes | **Used by:** CBTRN03C

### CVTRA04Y.cpy — Transaction Category Reference

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| TRAN-CAT-KEY (group) | — | Group | Composite key | |
| → TRAN-TYPE-CD | PIC X(02) | Alpha | Type code | Foreign key to TRANTYPE |
| → TRAN-CAT-CD | PIC 9(04) | Numeric | Category code | |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alpha | Category description | e.g., 'Gasoline', 'Restaurant' |
| FILLER | PIC X(04) | — | Reserved | Pads to 60 bytes |

**Record size:** 60 bytes | **Used by:** CBTRN03C

### CVTRA07Y.cpy — Transaction Report Layout

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| REPT-SHORT-NAME | PIC X(38) | Alpha | Report short title | |
| REPT-LONG-NAME | PIC X(41) | Alpha | Report full title | |
| REPT-DATE-HEADER | PIC X(12) | Alpha | Date range label | |
| REPT-START-DATE | PIC X(10) | Alpha | Report start date | |
| REPT-END-DATE | PIC X(10) | Alpha | Report end date | |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alpha | Transaction ID (detail line) | |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alpha | Account ID (detail line) | |
| TRAN-REPORT-TYPE-CD/DESC | PIC X(02)/X(15) | Alpha | Type code + description | |
| TRAN-REPORT-CAT-CD/DESC | PIC 9(04)/X(29) | Mixed | Category code + description | |
| TRAN-REPORT-SOURCE | PIC X(10) | Alpha | Source channel | |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount | |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page total amount | |

**Used by:** CBTRN03C

---

## 5. Authorization Entity (IMS)

### CIPAUSMY.cpy — IMS Auth Summary Segment (Root)

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID (segment key) | IMS root segment key |
| PA-CUST-ID | PIC 9(09) | Numeric | Customer ID | |
| PA-AUTH-STATUS | PIC X(01) | Alpha | Authorization status | |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alpha array | Account status history | 5-element status array |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit at auth time | |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash advance limit | |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Credit balance at auth time | |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Cash balance at auth time | |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of approved auths | |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of declined auths | |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount | |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount | |
| FILLER | PIC X(34) | — | Reserved | |

**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL

### CIPAUDTY.cpy — IMS Auth Detail Segment (Child)

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| PA-AUTHORIZATION-KEY (group) | — | Group | Composite segment key | |
| → PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Auth date (compressed) | IMS sequence field |
| → PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Auth time (compressed) | IMS sequence field |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alpha | Original auth date | YYMMDD |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alpha | Original auth time | HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alpha | Card number | |
| PA-AUTH-TYPE | PIC X(04) | Alpha | Authorization type | |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry at auth time | MMYY |
| PA-MESSAGE-TYPE | PIC X(06) | Alpha | Message type code | |
| PA-MESSAGE-SOURCE | PIC X(06) | Alpha | Message source | |
| PA-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization ID code | Returned to merchant |
| PA-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code | 88-level: '00' = approved |
| PA-AUTH-RESP-REASON | PIC X(04) | Alpha | Decline reason code | |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Requested amount | |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount | May differ from requested |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | MCC (merchant category) | ISO 18245 |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country | |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | Chip, swipe, contactless |
| PA-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID | |
| PA-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name | |
| PA-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city | |
| PA-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state | |
| PA-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP | |
| PA-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID | |
| PA-MATCH-STATUS | PIC X(01) | Alpha | Auth-to-transaction match | 88-levels: P=pending, D=declined, E=expired, M=matched |
| PA-AUTH-FRAUD | PIC X(01) | Alpha | Fraud flag | 88-levels: F=confirmed, R=removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alpha | Fraud report date | Set by COPAUS2C |
| FILLER | PIC X(17) | — | Reserved | |

**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL

---

## 6. Authorization Request/Response (MQ Messages)

### CCPAURQY.cpy — Pending Authorization Request

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alpha | Authorization request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Alpha | Authorization request time |
| PA-RQ-CARD-NUM | PIC X(16) | Alpha | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alpha | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry date |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alpha | ISO message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alpha | Source system |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Requested amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | MCC code |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID |

**Used by:** COPAUA0C

### CCPAURLY.cpy — Pending Authorization Response

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| PA-RL-CARD-NUM | PIC X(16) | Alpha | Card number (echo back) |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID (echo back) |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization approval code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alpha | Decline reason |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

**Used by:** COPAUA0C

### CCPAUERY.cpy — Authorization Error Log Record

| Field | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|-------|-----------|-----------|------------------|-------------------|
| ERR-DATE | PIC X(06) | Alpha | Error date | |
| ERR-TIME | PIC X(06) | Alpha | Error time | |
| ERR-APPLICATION | PIC X(08) | Alpha | Application name | |
| ERR-PROGRAM | PIC X(08) | Alpha | Program name | |
| ERR-LOCATION | PIC X(04) | Alpha | Error location code | |
| ERR-LEVEL | PIC X(01) | Alpha | Severity level | 88-levels: L=log, I=info, W=warning, C=critical |
| ERR-SUBSYSTEM | PIC X(01) | Alpha | Subsystem origin | 88-levels: A=app, C=CICS, I=IMS, D=DB2, M=MQ, F=file |
| ERR-CODE-1 | PIC X(09) | Alpha | Primary error code | |
| ERR-CODE-2 | PIC X(09) | Alpha | Secondary error code | |
| ERR-MESSAGE | PIC X(50) | Alpha | Error message text | |
| ERR-EVENT-KEY | PIC X(20) | Alpha | Event correlation key | |

**Used by:** COPAUA0C

---

## 7. Export/Import Entity

### CVEXPORT.cpy — Export Record (Multi-Entity)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| EXPORT-REC-TYPE | PIC X(1) | Alpha | Record type discriminator ('C'=customer, 'A'=account, etc.) |
| EXPORT-TIMESTAMP | PIC X(26) | Alpha | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number |
| EXPORT-BRANCH-ID | PIC X(4) | Alpha | Source branch |
| EXPORT-REGION-CODE | PIC X(5) | Alpha | Geographic region |
| EXPORT-RECORD-DATA | PIC X(460) | Alpha | Polymorphic data area |

**REDEFINES — Customer variant (EXPORT-CUSTOMER-DATA):**

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| EXP-CUST-ID | PIC 9(09) COMP | Customer ID |
| EXP-CUST-FIRST/MIDDLE/LAST-NAME | PIC X(25) each | Customer name |
| EXP-CUST-ADDR-LINES | PIC X(50) OCCURS 3 | Address lines |
| EXP-CUST-ADDR-STATE-CD | PIC X(02) | State code |
| EXP-CUST-SSN | PIC 9(09) | SSN |
| EXP-CUST-FICO-CREDIT-SCORE | PIC 9(03) COMP-3 | FICO score |

**REDEFINES — Account variant (EXPORT-ACCOUNT-DATA):**

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| EXP-ACCT-ID | PIC 9(11) | Account ID |
| EXP-ACCT-ACTIVE-STATUS | PIC X(01) | Active flag |
| EXP-ACCT-CURR-BAL | PIC S9(10)V99 COMP-3 | Balance (packed) |
| EXP-ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Credit limit |

**Used by:** CBEXPORT, CBIMPORT

---

## 8. Infrastructure / Shared Copybooks

### COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alpha | Source transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alpha | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alpha | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alpha | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alpha | Current user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alpha | User type (A/U) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context flag |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Selected customer ID |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alpha | Customer name fields |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Selected account ID |
| CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Selected card number |
| CDEMO-LAST-MAP | PIC X(7) | Alpha | Last BMS map displayed |
| CDEMO-LAST-MAPSET | PIC X(7) | Alpha | Last BMS mapset |

**Used by:** All 21 CICS online programs

### COMEN02Y.cpy — Main Menu Options

Defines the 11 menu entries displayed by COMEN01C:
1. Account View (COACTVWC), 2. Account Update (COACTUPC), 3. Card List (COCRDLIC), 4. Card Detail (COCRDSLC), 5. Card Update (COCRDUPC), 6. Transaction List (COTRN00C), 7. Transaction View (COTRN01C), 8. Transaction Add (COTRN02C), 9. Report Request (CORPT00C), 10. Bill Payment (COBIL00C), 11. Auth Summary (COPAUS0C)

### COADM02Y.cpy — Admin Menu Options

Defines admin menu entries for COADM01C:
1. User List (COUSR00C), 2. User Add (COUSR01C), 3. User Update (COUSR02C), 4. User Delete (COUSR03C), 5. Transaction Type List (COTRTLIC — DB2), 6. Transaction Type Maintenance (COTRTUPC — DB2)

### COTTL01Y.cpy — Screen Title Constants

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| CCDA-TITLE01 | PIC X(40) | Application title line 1 |
| CCDA-TITLE02 | PIC X(40) | Application title line 2 |
| CCDA-THANK-YOU | PIC X(40) | Thank-you message |

### CSDAT01Y.cpy — Date/Time Working Storage

Provides formatted date/time fields: CCYYMMDD, MM/DD/YY, HH:MM:SS, and ISO timestamp (YYYY-MM-DD HH:MM:SS.ffffff). Used by all CICS programs for header display.

### CSMSG01Y.cpy — Common Messages

Standard messages: thank-you text, invalid-key warning. Used by all CICS programs.

### CSMSG02Y.cpy — Extended Messages

Additional common message constants. Used by account/card/auth update programs.

### CSUTLDWY.cpy — Date Validation Working Storage

Provides working storage for comprehensive date validation: CC, YY, MM, DD components, CCYYMMDD composite, date format string, and validation result fields (severity, message number, result text). Used by COACTUPC and COTRTUPC.

### CSUTLDPY.cpy — Date Utility Parameters

Parameter block for CSUTLDTC date validation subroutine call. Referenced by CSUTLDTC.cbl.

### CSSETATY.cpy — Set Screen Attribute Macro

Used with `COPY REPLACING` pattern (29× in COACTUPC) to set BMS screen field attributes (protected, unprotected, bright, dark, etc.).

### CSSTRPFY.cpy — String Strip Function

Inline COBOL paragraph for stripping leading/trailing spaces from fields. Included via `COPY 'CSSTRPFY'`.

### CSLKPCDY.cpy — Validation Lookup Tables

| Content | Business Meaning |
|---------|------------------|
| US-STATE-CODE-TO-EDIT | 50 US state code table for validation |
| US-STATE-ZIPCODE-TO-EDIT | ZIP prefix to state mapping |
| WS-US-PHONE-AREA-CODE-TO-EDIT | NANPA area code validation table |

**Used by:** COACTUPC (for SSN, phone, state, ZIP validation)

### CODATECN.cpy — Date Conversion Record

| Field | PIC Clause | Business Meaning |
|-------|-----------|------------------|
| CODATECN-TYPE | PIC X | Conversion type flag |
| CODATECN-INP-DATE | PIC X(20) | Input date (multiple formats via REDEFINES) |
| CODATECN-0UT-DATE | PIC X(20) | Output date (multiple formats via REDEFINES) |
| CODATECN-ERROR-MSG | PIC X(38) | Conversion error message |

**Used by:** CBACT01C (batch date formatting via CALL 'COBDATFT')

### IMSFUNCS.cpy — IMS DL/I Function Codes

| Field | Value | Meaning |
|-------|-------|---------|
| FUNC-GU | 'GU  ' | Get Unique (direct read) |
| FUNC-GHU | 'GHU ' | Get Hold Unique (read for update) |
| FUNC-GN | 'GN  ' | Get Next (sequential read) |
| FUNC-GHN | 'GHN ' | Get Hold Next |
| FUNC-GNP | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | 'REPL' | Replace (update) |
| FUNC-ISRT | 'ISRT' | Insert |
| FUNC-DLET | 'DLET' | Delete |
| PARMCOUNT | +4 COMP-5 | Standard DL/I call parameter count |

**Used by:** DBUNLDGS, PAUDBLOD, PAUDBUNL

### MQ Copybooks (IBM-supplied)

| Copybook | Purpose |
|----------|---------|
| CMQV | MQ constants and return codes |
| CMQODV | MQ Object Descriptor |
| CMQMDV | MQ Message Descriptor |
| CMQGMOV | MQ Get Message Options |
| CMQPMOV | MQ Put Message Options |
| CMQTML | MQ Trigger Message |

**Used by:** COACCT01, CODATE01, COPAUA0C

---

## 9. DB2 Table Layouts (via EXEC SQL INCLUDE)

### DCLTRTYP — Transaction Type Table

| Column | SQL Type | COBOL PIC | Business Meaning |
|--------|---------|-----------|------------------|
| TRTYP_CD | CHAR(2) | PIC X(02) | Type code (PK) |
| TRTYP_DESC | VARCHAR(50) | PIC X(50) | Type description |

**Used by:** COTRTLIC, COTRTUPC, COBTUPDT

### DCLTRCAT — Transaction Category Table

| Column | SQL Type | COBOL PIC | Business Meaning |
|--------|---------|-----------|------------------|
| TRCAT_CD | DECIMAL(4) | PIC 9(04) | Category code (PK part) |
| TRCAT_TYPE_CD | CHAR(2) | PIC X(02) | Type code (PK part, FK) |
| TRCAT_DESC | VARCHAR(50) | PIC X(50) | Category description |

**Used by:** COTRTUPC

### AUTHFRDS — Authorization Fraud Table (DB2)

| Column | SQL Type | Business Meaning |
|--------|---------|------------------|
| Card number, auth date/time | Keys | Identifies specific authorization |
| Fraud flag | CHAR(1) | 'F'=confirmed fraud, 'R'=removed |
| Report date | CHAR(8) | Date fraud was reported |

**Used by:** COPAUS2C

---

## 10. Copybook-to-Program Cross-Reference

| Copybook | Programs Using It |
|----------|-------------------|
| CVACT01Y | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COACCT01, COPAUA0C, COPAUS0C |
| CVACT02Y | CBACT02C, CBEXPORT, CBIMPORT, CBSTM03A, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC |
| CVACT03Y | CBACT03C, CBACT04C, CBEXPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, COACTUPC, COACTVWC, COTRN02C, COPAUA0C, COPAUS0C |
| CVCUS01Y | CBCUS01C, CBEXPORT, CBIMPORT, CBSTM03A, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| CVTRA05Y | CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, COBIL00C, COTRN00C, COTRN01C, COTRN02C, CORPT00C |
| COCOM01Y | All 21 CICS programs + COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC |
| DFHAID | All CICS programs (AID byte constants) |
| DFHBMSCA | All CICS programs (BMS attribute constants) |
| COTTL01Y | All CICS programs |
| CSDAT01Y | All CICS programs |
| CSMSG01Y | All CICS programs |
| CSUSR01Y | COSGN00C, COUSR00-03C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC |
| CIPAUSMY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
| CIPAUDTY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
