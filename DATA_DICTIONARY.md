# Data Dictionary — CardDemo COBOL Estate

## Overview

This document catalogs every copybook in `app/cpy/` and sub-application copybook directories, extracting field names, PIC clauses, data types, inferred business meaning, and validation rules. Fields are grouped by business entity.

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Master Record

**Record:** `ACCOUNT-RECORD` · **Record Length:** 300 bytes · **Used by:** CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C, CBSTM03A

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| ACCT-ID | PIC 9(11) | Numeric (zoned decimal) | Unique account identifier | Primary key; 11 digits |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account active/inactive status | 'Y'=Active, 'N'=Inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Signed; allows negative |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum credit limit | Must be positive |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Cash advance credit limit | Must be ≤ credit limit |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric date | Account opening date | Format: YYYY-MM-DD |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric date | Account expiration date | Format: YYYY-MM-DD |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric date | Card reissue date | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle credit total | Running total for billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle debit total | Running total for billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | US ZIP+4 format |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group/portfolio identifier | Links to discount group |
| FILLER | PIC X(178) | Filler | Reserved/padding | — |

### 1.2 CVACT03Y.cpy — Card-to-Account Cross-Reference Record

**Record:** `CARD-XREF-RECORD` · **Record Length:** 50 bytes · **Used by:** CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, CBSTM03A

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Credit card number (primary key) | 16-digit card number |
| XREF-CUST-ID | PIC 9(09) | Numeric (zoned decimal) | Customer ID owning the card | FK to CUSTOMER-RECORD |
| XREF-ACCT-ID | PIC 9(11) | Numeric (zoned decimal) | Account ID linked to card | FK to ACCOUNT-RECORD |
| FILLER | PIC X(14) | Filler | Reserved/padding | — |

---

## 2. Card Entity

### 2.1 CVACT02Y.cpy — Card Master Record

**Record:** `CARD-RECORD` · **Record Length:** 150 bytes · **Used by:** CBACT02C, CBEXPORT, CBIMPORT, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Credit card number (primary key) | 16-digit; Luhn-checkable |
| CARD-ACCT-ID | PIC 9(11) | Numeric (zoned decimal) | Associated account ID | FK to ACCOUNT-RECORD |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit CVV |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on card | Cardholder name |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric date | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card active/inactive status | 'Y'=Active, 'N'=Inactive |
| FILLER | PIC X(59) | Filler | Reserved/padding | — |

### 2.2 CVCRD01Y.cpy — Card Work Areas (CICS screen control)

**Record:** `CC-WORK-AREAS` · **Used by:** COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| CCARD-AID | PIC X(5) | Alphanumeric | CICS attention identifier | 88-levels: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to transfer control to | Valid CICS program name |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | Next BMS mapset name | — |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | Next BMS map name | — |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message for screen display | — |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return/success message | 88: CCARD-RETURN-MSG-OFF = LOW-VALUES |
| CC-ACCT-ID | PIC X(11) | Alphanumeric | Working account ID | REDEFINES to PIC 9(11) |
| CC-CARD-NUM | PIC X(16) | Alphanumeric | Working card number | REDEFINES to PIC 9(16) |
| CC-CUST-ID | PIC X(09) | Alphanumeric | Working customer ID | REDEFINES to PIC 9(9) |

---

## 3. Customer Entity

### 3.1 CVCUS01Y.cpy — Customer Master Record

**Record:** `CUSTOMER-RECORD` · **Record Length:** 500 bytes · **Used by:** CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, CBSTM03A

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| CUST-ID | PIC 9(09) | Numeric (zoned decimal) | Unique customer identifier | Primary key; 9 digits |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | — |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | — |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | — |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | — |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | — |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | — |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated via CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | — |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | Validated via CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated via CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Area code validated via CSLKPCDY |
| CUST-SSN | PIC 9(09) | Numeric (zoned decimal) | Social Security Number | 9-digit; sensitive PII |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID | — |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric date | Date of birth | Format: YYYY-MM-DD |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT (electronic funds transfer) account | Bank account for payments |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder indicator | 'Y'=Primary, 'N'=Authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | Filler | Reserved/padding | — |

### 3.2 CUSTREC.cpy — Customer Record (Statement variant)

**Record:** `CUSTOMER-RECORD` · **Used by:** CBSTM03A

Identical structure to CVCUS01Y.cpy with minor formatting differences (CUST-DOB-YYYYMMDD instead of CUST-DOB-YYYY-MM-DD). Used specifically in the statement generation subroutine.

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Master Record

**Record:** `TRAN-RECORD` · **Record Length:** 350 bytes · **Used by:** CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | 16-char; system-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to TRAN-TYPE-RECORD |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to TRAN-CAT-RECORD |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction origination source | e.g., 'POS', 'ATM', 'ONLINE' |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free-text merchant description |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Signed; debits negative |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant business name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used in transaction | FK to CARD-RECORD |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric timestamp | Transaction origination timestamp | ISO-style timestamp |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric timestamp | Transaction processing timestamp | ISO-style timestamp |
| FILLER | PIC X(20) | Filler | Reserved/padding | — |

### 4.2 CVTRA06Y.cpy — Daily Transaction Record

**Record:** `DALYTRAN-RECORD` · **Record Length:** 350 bytes · **Used by:** CBTRN01C, CBTRN02C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | Daily transaction identifier | Same layout as TRAN-ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to TRAN-TYPE-RECORD |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | — |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | Origination source | — |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | Description | — |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Amount | — |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | — |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | — |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number | — |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric timestamp | Origination timestamp | — |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric timestamp | Processing timestamp | — |
| FILLER | PIC X(20) | Filler | Reserved | — |

### 4.3 CVTRA01Y.cpy — Transaction Category Balance Record

**Record:** `TRAN-CAT-BAL-RECORD` · **Record Length:** 50 bytes · **Used by:** CBACT04C, CBTRN02C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | Composite key part 1 |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Composite key part 2 |
| TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code | Composite key part 3 |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance for this category | Aggregated amount |
| FILLER | PIC X(22) | Filler | Reserved | — |

### 4.4 CVTRA02Y.cpy — Disclosure/Interest Group Record

**Record:** `DIS-GROUP-RECORD` · **Record Length:** 50 bytes · **Used by:** CBACT04C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group identifier | Composite key part 1 |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Composite key part 2 |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Composite key part 3 |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest rate for this group | Percentage; e.g., 18.99 |
| FILLER | PIC X(28) | Filler | Reserved | — |

### 4.5 CVTRA03Y.cpy — Transaction Type Record

**Record:** `TRAN-TYPE-RECORD` · **Record Length:** 60 bytes · **Used by:** CBTRN03C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code (primary key) | e.g., 'SA'=Sale, 'CR'=Credit |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Description of transaction type | — |
| FILLER | PIC X(08) | Filler | Reserved | — |

### 4.6 CVTRA04Y.cpy — Transaction Category Record

**Record:** `TRAN-CAT-RECORD` · **Record Length:** 60 bytes · **Used by:** CBTRN03C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Composite key part 1 |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Composite key part 2 |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | — |
| FILLER | PIC X(04) | Filler | Reserved | — |

### 4.7 COSTM01.CPY — Statement Transaction Record (re-keyed for statement processing)

**Record:** `TRNX-RECORD` · **Used by:** CBSTM03A

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number (composite key part 1) | — |
| TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID (composite key part 2) | — |
| TRNX-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | — |
| TRNX-CAT-CD | PIC 9(04) | Numeric | Transaction category code | — |
| TRNX-SOURCE | PIC X(10) | Alphanumeric | Source | — |
| TRNX-DESC | PIC X(100) | Alphanumeric | Description | — |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Amount | — |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | — |
| TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | — |
| TRNX-ORIG-TS | PIC X(26) | Alphanumeric timestamp | Origination timestamp | — |
| TRNX-PROC-TS | PIC X(26) | Alphanumeric timestamp | Processing timestamp | — |
| FILLER | PIC X(20) | Filler | Reserved | — |

### 4.8 CVTRA07Y.cpy — Transaction Report Layout

**Records:** `REPORT-NAME-HEADER`, `TRANSACTION-DETAIL-REPORT`, `TRANSACTION-HEADER-1/2`, `REPORT-PAGE-TOTALS`, `REPORT-ACCOUNT-TOTALS`, `REPORT-GRAND-TOTALS` · **Used by:** CBTRN03C

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report short name ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report long name |
| REPT-START-DATE | PIC X(10) | Alphanumeric | Report start date |
| REPT-END-DATE | PIC X(10) | Alphanumeric | Report end date |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID in report line |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID in report line |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code in report line |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Source |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Amount (formatted) |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |

---

## 5. User/Security Entity

### 5.1 CSUSR01Y.cpy — User Security Record

**Record:** `SEC-USER-DATA` · **Record Length:** 80 bytes · **Used by:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COADM01C, COMEN01C

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | User login ID (primary key) | Max 8 characters |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | User password | Max 8 characters; sensitive |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type | 'A'=Admin, 'U'=Regular user |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved | — |

---

## 6. Authorization Entity (Sub-Application)

### 6.1 CIPAUSMY.cpy — IMS Pending Authorization Summary Segment

Used in IMS DB root segment for the PAUTHDB database.

*(Structure inferred from usage in COPAUS0C/COPAUS1C — summary-level fields for authorization message browsing.)*

### 6.2 CIPAUDTY.cpy — IMS Pending Authorization Detail Segment

**Parent:** IMS PAUTHDB · **Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (numeric) | Julian or YYDDD format |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (numeric) | HHMMSSmmm |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original authorization date | YYMMDD |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original authorization time | HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number | — |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type | — |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry date | YYMM |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type | — |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message source system | — |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID code | — |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Authorization response code | 88: '00'=Approved |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason code | — |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Transaction amount | — |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Approved amount | — |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | Merchant category code (MCC) | — |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country code | — |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | — |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant identifier | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP | — |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID | — |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric | Authorization match status | 88: 'P'=Pending, 'D'=Declined, 'E'=Expired, 'M'=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | Fraud flag | 88: 'F'=Fraud confirmed, 'R'=Fraud removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric | Fraud report date | YYYYMMDD |
| FILLER | PIC X(17) | Filler | Reserved | — |

### 6.3 CCPAURQY.cpy — Pending Authorization Request (MQ message)

**Used by:** COPAUA0C

Contains fields for authorization request received from MQ: card number, transaction amount, merchant data, authorization type. Used as the inbound message structure for the card authorization decision program.

### 6.4 CCPAURLY.cpy — Pending Authorization Reply (MQ message)

**Used by:** COPAUA0C

Contains fields for authorization reply sent to MQ: response code, approved amount, reason code. Used as the outbound message structure.

### 6.5 CCPAUERY.cpy — Pending Authorization Error (MQ message)

**Used by:** COPAUA0C

Contains error response fields when authorization processing fails.

---

## 7. Export/Migration Entity

### 7.1 CVEXPORT.cpy — Export Record (Branch Migration)

**Record:** `EXPORT-RECORD` · **Used by:** CBEXPORT, CBIMPORT

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | Record type discriminator | 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp | REDEFINES to date + time |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number | — |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Originating branch ID | — |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Region code | — |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Polymorphic data area | REDEFINES by entity type |

**REDEFINES variants:**
- `EXPORT-CUSTOMER-DATA` — Customer fields (EXP-CUST-*) with COMP-3 for FICO score
- `EXPORT-ACCOUNT-DATA` — Account fields (EXP-ACCT-*) with COMP-3 for balances
- `EXPORT-TRANSACTION-DATA` — Transaction fields (EXP-TRAN-*) with COMP-3 for amounts
- `EXPORT-CARD-XREF-DATA` — Cross-reference fields

---

## 8. Application Control Copybooks

### 8.1 COCOM01Y.cpy — COMMAREA (Inter-program Communication)

**Record:** `CARDDEMO-COMMAREA` · **Used by:** Almost all CICS programs

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Originating CICS transaction ID | — |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Originating program name | — |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target CICS transaction ID | — |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name | — |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Current user ID | — |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type | 88: 'A'=Admin, 'U'=User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context flag | 88: 0=Enter, 1=Re-enter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Customer ID in context | — |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | Customer first name | — |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | Customer middle name | — |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | Customer last name | — |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Account ID in context | — |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status | — |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Card number in context | — |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last displayed map | — |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last displayed mapset | — |

### 8.2 COADM02Y.cpy — Admin Menu Options

**Record:** `CARDDEMO-ADMIN-MENU-OPTIONS` · **Used by:** COADM01C

Defines 6 admin menu options (User List, User Add, User Update, User Delete, Tran Type List, Tran Type Maintenance) with option number, description, and target program name.

### 8.3 COMEN02Y.cpy — Main Menu Options

**Record:** `CARDDEMO-MAIN-MENU-OPTIONS` · **Used by:** COMEN01C

Defines 11 main menu options (Account View/Update, Card List/View/Update, Transaction List/View/Add, Reports, Bill Payment, Pending Authorization View) with user-type restrictions.

### 8.4 COTTL01Y.cpy — Screen Title

**Record:** `CCDA-SCREEN-TITLE` · **Used by:** Most CICS programs

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CCDA-TITLE01 | PIC X(40) | Alphanumeric | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | Alphanumeric | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | Alphanumeric | Sign-off message |

### 8.5 CSDAT01Y.cpy — Date/Time Work Area

**Record:** `WS-DATE-TIME` · **Used by:** Most CICS programs

Provides current date/time fields in multiple formats: YYYYMMDD, MM/DD/YY, HH:MM:SS, and full ISO timestamp.

### 8.6 CODATECN.cpy — Date Conversion Work Area

**Record:** `CODATECN-REC` · **Used by:** CBACT01C

Provides input/output areas for date format conversion between YYYYMMDD and YYYY-MM-DD formats. Includes REDEFINES for different parsing layouts and an error message field.

### 8.7 CSMSG01Y.cpy — Common Messages

**Record:** `CCDA-COMMON-MESSAGES` · **Used by:** Most CICS programs

| Field | Business Meaning |
|-------|-----------------|
| CCDA-MSG-THANK-YOU | Thank you message |
| CCDA-MSG-INVALID-KEY | Invalid key pressed message |

### 8.8 CSMSG02Y.cpy — Abend Data Work Area

**Record:** `ABEND-DATA` · **Used by:** CICS programs with abend handling

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| ABEND-CODE | PIC X(4) | Abend code |
| ABEND-CULPRIT | PIC X(8) | Program causing abend |
| ABEND-REASON | PIC X(50) | Abend reason text |
| ABEND-MSG | PIC X(72) | Formatted abend message |

### 8.9 CSLKPCDY.cpy — Lookup Code Repository (Validation)

**Record:** `WS-US-PHONE-AREA-CODE-TO-EDIT` + state codes + ZIP prefixes · **Used by:** COACTUPC

Contains 88-level condition names for:
- **North American phone area codes** (~400 valid area codes)
- **US state codes** (50 states + territories)
- **State-to-ZIP prefix mapping** for address validation

### 8.10 CSSETATY.cpy — Field Attribute Setting (Template)

**Used by:** COACTUPC (via COPY REPLACING ×30+)

Template copybook for setting BMS field attributes (color, protection) based on validation flags. Uses COPY REPLACING to parameterize field names.

### 8.11 CSSTRPFY.cpy — Store PFKey Handler

**Paragraph:** `YYYY-STORE-PFKEY` · **Used by:** CICS programs with PFK handling

EVALUATE block mapping EIBAID values to CCARD-AID-* condition names for all PF keys (PF1–PF24 mapped to PFK01–PFK12).

### 8.12 CSUTLDPY.cpy — Date Utility Parameters

**Used by:** COACTUPC

Large copybook (375 lines) with date validation utility parameters and working storage for the CSUTLDTC subroutine.

### 8.13 CSUTLDWY.cpy — Date Utility Working Storage

**Used by:** COACTUPC

Working storage areas for date calculation and validation.

### 8.14 UNUSED1Y.cpy — Unused Placeholder

**Record:** `UNUSED-DATA` · **Not referenced by any program**

Placeholder copybook with minimal fields — likely reserved for future use.

---

## 9. Sub-Application Copybooks

### 9.1 Authorization Sub-App (`app/app-authorization-ims-db2-mq/cpy/`)

| Copybook | Purpose |
|----------|---------|
| CIPAUSMY.cpy | IMS PAUTHDB root segment (summary) |
| CIPAUDTY.cpy | IMS PAUTHDB child segment (detail) — see Section 6.2 |
| CCPAURQY.cpy | MQ authorization request message layout |
| CCPAURLY.cpy | MQ authorization reply message layout |
| CCPAUERY.cpy | MQ authorization error message layout |
| IMSFUNCS.cpy | IMS DL/I function code constants (GN, GNP, GU, ISRT, DLET, etc.) |

### 9.2 Transaction Type Sub-App (`app/app-transaction-type-db2/cpy/`)

| Copybook | Purpose |
|----------|---------|
| CSDB2RWY.cpy | Db2 read/write working storage areas |
| CSDB2RPY.cpy | Db2 read-only (report) working storage areas |

### 9.3 PCB Copybooks (IMS Program Communication Blocks)

Referenced via `COPY` in IMS programs but stored in system libraries:

| Copybook | Purpose |
|----------|---------|
| PAUTBPCB | PCB for PAUTHDB (root) |
| PASFLPCB | PCB for GSAM summary output file |
| PADFLPCB | PCB for GSAM detail output file |
