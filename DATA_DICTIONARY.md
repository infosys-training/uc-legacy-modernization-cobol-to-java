# DATA DICTIONARY — CardDemo COBOL Estate

## Overview

This document catalogs every data field defined in the CardDemo copybooks, organized by business entity. For each field: name, PIC clause, data type, inferred business meaning, and validation rules (where applicable).

**Copybook Locations:**
- Main: `app/cpy/`
- Authorization sub-app: `app/app-authorization-ims-db2-mq/cpy/`
- Transaction Type DB2 sub-app: `app/app-transaction-type-db2/cpy/`

---

## 1. Account Entity

### CVACT01Y.cpy — Account Master Record (RECLN 300)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Unique account identifier | Primary key; must be numeric |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alpha (1 char) | Account active/inactive flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Signed; can be negative (overpayment) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum credit limit | Must be positive |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric date | Account opening date (YYYY-MM-DD) | Validated via CSUTLDTC/CSUTLDPY |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric date | Account expiration date | Must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric date | Last card reissue date | — |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current billing cycle credit total | Accumulated per cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current billing cycle debit total | Accumulated per cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder zip code | Validated against CSLKPCDY state+zip lookup |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group/portfolio ID | Links to disclosure group (CVTRA02Y) |
| FILLER | PIC X(178) | Padding | Reserved for future use | — |

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number | Primary key; links to CVACT02Y |
| XREF-CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer ID reference | Foreign key to CVCUS01Y |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account ID reference | Foreign key to CVACT01Y |
| FILLER | PIC X(14) | Padding | Reserved | — |

---

## 2. Card Entity

### CVACT02Y.cpy — Card Master Record (RECLN 150)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number | Primary key |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Linked account ID | Foreign key to CVACT01Y |
| CARD-CVV-CD | PIC 9(03) | Numeric (3 digits) | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name printed on card | — |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric date | Card expiration date | Validated via CSUTLDPY |
| CARD-ACTIVE-STATUS | PIC X(01) | Alpha (1 char) | Card active/inactive flag | 'Y' = active |
| FILLER | PIC X(59) | Padding | Reserved | — |

### CVCRD01Y.cpy — Card Work Areas (CICS Online)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CCARD-AID | PIC X(5) | Alpha (5 chars) | Attention Identifier key pressed | 88-level: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alpha (8 chars) | Next program to transfer control to | Set before XCTL |
| CCARD-NEXT-MAPSET | PIC X(7) | Alpha (7 chars) | Next BMS mapset name | — |
| CCARD-NEXT-MAP | PIC X(7) | Alpha (7 chars) | Next BMS map name | — |
| CCARD-ERROR-MSG | PIC X(75) | Alpha (75 chars) | Error message for screen display | — |
| CCARD-RETURN-MSG | PIC X(75) | Alpha (75 chars) | Return/status message for display | 88: CCARD-RETURN-MSG-OFF = LOW-VALUES |
| CC-ACCT-ID | PIC X(11) / 9(11) | Alphanumeric/Numeric | Account ID work field (with REDEFINES) | — |
| CC-CARD-NUM | PIC X(16) / 9(16) | Alphanumeric/Numeric | Card number work field (with REDEFINES) | — |
| CC-CUST-ID | PIC X(09) / 9(9) | Alphanumeric/Numeric | Customer ID work field (with REDEFINES) | — |

---

## 3. Customer Entity

### CVCUS01Y.cpy — Customer Master Record (RECLN 500)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric (9 digits) | Unique customer identifier | Primary key |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | — |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | — |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | — |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Address line 1 | — |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Address line 2 | — |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Address line 3 | — |
| CUST-ADDR-STATE-CD | PIC X(02) | Alpha (2 chars) | US state code | Validated via CSLKPCDY 88-level VALUES |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha (3 chars) | ISO country code | — |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Postal/ZIP code | Validated via CSLKPCDY state+zip lookup |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone number | Area code validated via CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone number | Area code validated via CSLKPCDY |
| CUST-SSN | PIC 9(09) | Numeric (9 digits) | Social Security Number | 9-digit numeric |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID number | — |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric date | Date of birth | Validated via CSUTLDPY date validation |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | Electronic Funds Transfer account ID | For bill payment processing |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha (1 char) | Primary cardholder indicator | 'Y' = primary holder |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3 digits) | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | Padding | Reserved | — |

### CUSTREC.cpy — Customer Record (Alternate Layout, RECLN 500)

Identical field structure to CVCUS01Y but with slightly different naming convention (`CUST-DOB-YYYYMMDD` vs `CUST-DOB-YYYY-MM-DD`). Used by statement generation programs.

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Master Record (RECLN 350)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Unique transaction identifier | Primary key |
| TRAN-TYPE-CD | PIC X(02) | Alpha (2 chars) | Transaction type code | Foreign key to CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | Foreign key to CVTRA04Y |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction origination source | E.g., 'POS', 'ONLINE', 'ATM' |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | — |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Positive=debit, Negative=credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card used for transaction | Foreign key to CVACT02Y |
| TRAN-ORIG-TS | PIC X(26) | Timestamp | Transaction origination timestamp | Format: YYYY-MM-DD-HH.MM.SS.MMMMMM |
| TRAN-PROC-TS | PIC X(26) | Timestamp | Transaction processing timestamp | Set during batch posting |
| FILLER | PIC X(20) | Padding | Reserved | — |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same structure as CVTRA05Y with prefix `DALYTRAN-` instead of `TRAN-`. Used as the daily input feed before validation and posting.

### CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alpha (2 chars) | Transaction type code | Primary key |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Type description | — |
| FILLER | PIC X(08) | Padding | Reserved | — |

### CVTRA04Y.cpy — Transaction Category Record (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alpha (2 chars) | Parent transaction type code | Composite key part 1 |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | Composite key part 2 |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | — |
| FILLER | PIC X(04) | Padding | Reserved | — |

### CVTRA01Y.cpy — Transaction Category Balance Record (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account ID (part of composite key) | — |
| TRANCAT-TYPE-CD | PIC X(02) | Alpha (2 chars) | Transaction type code (key part 2) | — |
| TRANCAT-CD | PIC 9(04) | Numeric (4 digits) | Category code (key part 3) | — |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance per category | Updated during posting (CBTRN02C) |
| FILLER | PIC X(22) | Padding | Reserved | — |

### CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Account group/portfolio ID | Composite key part 1 |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alpha (2 chars) | Transaction type code | Composite key part 2 |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Category code | Composite key part 3 |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest rate percentage | Used by interest calculation (CBACT04C) |
| FILLER | PIC X(28) | Padding | Reserved | — |

### COSTM01.CPY — Statement Transaction Record (Reporting Layout)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (composite key part 1) | — |
| TRNX-ID | PIC X(16) | Alphanumeric (16) | Transaction ID (composite key part 2) | — |
| TRNX-TYPE-CD | PIC X(02) | Alpha (2 chars) | Transaction type code | — |
| TRNX-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | — |
| TRNX-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source | — |
| TRNX-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | — |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | — |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant ID | — |
| TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | — |
| TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | — |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | — |
| TRNX-ORIG-TS | PIC X(26) | Timestamp | Origination timestamp | — |
| TRNX-PROC-TS | PIC X(26) | Timestamp | Processing timestamp | — |
| FILLER | PIC X(20) | Padding | Reserved | — |

### CVTRA07Y.cpy — Transaction Report Structures

| Structure | Field | PIC Clause | Business Meaning |
|-----------|-------|-----------|-----------------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME | PIC X(38) | Report short name ('DALYREPT') |
| | REPT-LONG-NAME | PIC X(41) | Report title ('Daily Transaction Report') |
| | REPT-START-DATE | PIC X(10) | Report start date filter |
| | REPT-END-DATE | PIC X(10) | Report end date filter |
| TRANSACTION-DETAIL-REPORT | TRAN-REPORT-TRANS-ID | PIC X(16) | Transaction ID column |
| | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Account ID column |
| | TRAN-REPORT-TYPE-CD | PIC X(02) | Type code column |
| | TRAN-REPORT-TYPE-DESC | PIC X(15) | Type description column |
| | TRAN-REPORT-CAT-CD | PIC 9(04) | Category code column |
| | TRAN-REPORT-CAT-DESC | PIC X(29) | Category description column |
| | TRAN-REPORT-SOURCE | PIC X(10) | Source column |
| | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Amount column (edited) |
| REPORT-PAGE-TOTALS | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Page subtotal |
| REPORT-ACCOUNT-TOTALS | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Account subtotal |
| REPORT-GRAND-TOTALS | REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Grand total |

---

## 5. Authorization Entity (IMS-DB2-MQ Sub-Application)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Segment)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID | — |
| PA-CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer ID | — |
| PA-AUTH-STATUS | PIC X(01) | Alpha (1 char) | Authorization status flag | — |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alpha array | Account status codes (5 occurrences) | — |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Credit limit | — |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Cash advance limit | — |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Current credit balance | — |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Current cash balance | — |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of approved authorizations | — |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of declined authorizations | — |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Total approved authorization amount | — |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Total declined authorization amount | — |

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Segment)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Auth date (compressed key) | Part of composite key |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Auth time (compressed key) | Part of composite key |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original authorization date | — |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original authorization time | — |
| PA-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | — |
| PA-AUTH-TYPE | PIC X(04) | Alpha (4 chars) | Authorization type | — |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric (4) | Card expiry (MMYY) | — |
| PA-MESSAGE-TYPE | PIC X(06) | Alpha (6 chars) | Message type indicator | — |
| PA-MESSAGE-SOURCE | PIC X(06) | Alpha (6 chars) | Message originating source | — |
| PA-AUTH-ID-CODE | PIC X(06) | Alpha (6 chars) | Authorization identification code | Generated upon approval |
| PA-AUTH-RESP-CODE | PIC X(02) | Alpha (2 chars) | Authorization response code | 88: '00' = approved |
| PA-AUTH-RESP-REASON | PIC X(04) | Alpha (4 chars) | Response reason code | — |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric (6 digits) | Processing code | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Requested transaction amount | — |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Approved amount | May differ from requested |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha (4 chars) | Merchant category code (MCC) | Standard ISO 18245 |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alpha (3 chars) | Acquirer country code | — |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric (2 digits) | POS entry mode | — |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant identifier | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alpha (2 chars) | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric (9) | Merchant ZIP code | — |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction identifier | — |
| PA-MATCH-STATUS | PIC X(01) | Alpha (1 char) | Authorization/transaction match status | 88: 'P'=Pending, 'D'=Declined, 'E'=Expired, 'M'=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alpha (1 char) | Fraud indicator | 88: 'F'=Confirmed, 'R'=Removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric | Fraud report date | — |

### CCPAURQY.cpy — Authorization Request (MQ Message)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Request date | — |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Request time | — |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number on request | — |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alpha (4 chars) | Authorization type requested | — |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric (4) | Card expiry | — |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alpha (6 chars) | Message type | — |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alpha (6 chars) | Message source | — |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric (6 digits) | Processing code | — |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Requested amount | — |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha (4 chars) | MCC code | — |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alpha (3 chars) | Acquirer country | — |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric (2 digits) | POS entry mode | — |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant ID | — |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name | — |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city | — |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alpha (2 chars) | Merchant state | — |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric (9) | Merchant ZIP | — |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction ID | — |

### CCPAURLY.cpy — Authorization Response (MQ Message)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | — |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction ID | — |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alpha (6 chars) | Authorization code assigned | — |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alpha (2 chars) | Response code | — |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alpha (4 chars) | Response reason | — |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount | — |

### CCPAUERY.cpy — Authorization Error Log

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| ERR-DATE | PIC X(06) | Alphanumeric | Error date | — |
| ERR-TIME | PIC X(06) | Alphanumeric | Error time | — |
| ERR-APPLICATION | PIC X(08) | Alpha (8 chars) | Application name | — |
| ERR-PROGRAM | PIC X(08) | Alpha (8 chars) | Program that generated error | — |
| ERR-LOCATION | PIC X(04) | Alpha (4 chars) | Error location in code | — |
| ERR-LEVEL | PIC X(01) | Alpha (1 char) | Severity level | 88: 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alpha (1 char) | Subsystem that errored | 88: 'A'=App, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=File |
| ERR-CODE-1 | PIC X(09) | Alphanumeric (9) | Primary error code | — |
| ERR-CODE-2 | PIC X(09) | Alphanumeric (9) | Secondary error code | — |
| ERR-MESSAGE | PIC X(50) | Alphanumeric (50) | Error message text | — |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric (20) | Key of record being processed | — |

---

## 6. IMS Database Structures

### IMSFUNCS.cpy — IMS DL/I Function Codes

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| FUNC-GU | PIC X(04) VALUE 'GU  ' | Constant | Get Unique (direct retrieval) |
| FUNC-GHU | PIC X(04) VALUE 'GHU ' | Constant | Get Hold Unique (for update) |
| FUNC-GN | PIC X(04) VALUE 'GN  ' | Constant | Get Next (sequential) |
| FUNC-GHN | PIC X(04) VALUE 'GHN ' | Constant | Get Hold Next (for update) |
| FUNC-GNP | PIC X(04) VALUE 'GNP ' | Constant | Get Next within Parent |
| FUNC-GHNP | PIC X(04) VALUE 'GHNP' | Constant | Get Hold Next within Parent |
| FUNC-REPL | PIC X(04) VALUE 'REPL' | Constant | Replace segment |
| FUNC-ISRT | PIC X(04) VALUE 'ISRT' | Constant | Insert segment |
| FUNC-DLET | PIC X(04) VALUE 'DLET' | Constant | Delete segment |
| PARMCOUNT | PIC S9(05) COMP-5 VALUE +4 | Binary | DL/I parameter count |

### PAUTBPCB.CPY — Authorization DB PCB (Program Communication Block)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PAUT-DBDNAME | PIC X(08) | Alpha | Database description name |
| PAUT-SEG-LEVEL | PIC X(02) | Alpha | Segment hierarchy level |
| PAUT-PCB-STATUS | PIC X(02) | Alpha | DL/I status code (spaces=OK, GE=not found, etc.) |
| PAUT-PCB-PROCOPT | PIC X(04) | Alpha | Processing options (A=all, G=get, I=insert, etc.) |
| PAUT-SEG-NAME | PIC X(08) | Alpha | Last accessed segment name |
| PAUT-KEYFB | PIC X(255) | Alpha | Key feedback area |

### PASFLPCB.CPY / PADFLPCB.CPY — GSAM Output PCBs

Same structure as PAUTBPCB for summary file (PASFL) and detail file (PADFL) GSAM output.

---

## 7. DB2 Structures (Transaction Type Sub-Application)

### CSDB2RWY.cpy — DB2 Common Working Storage Variables

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| WS-DISP-SQLCODE | PIC ----9 | Edited numeric | Displayable SQLCODE | — |
| WS-DUMMY-DB2-INT | PIC S9(4) COMP-3 | Packed decimal | Priming query result | — |
| WS-DB2-PROCESSING-FLAG | PIC X(1) | Alpha (1 char) | DB2 processing status | 88: '0'=OK, '1'=Error |
| WS-DB2-CURRENT-ACTION | PIC X(72) | Alphanumeric (72) | Description of current DB2 action | For error messaging |
| WS-DSNTIAC-FORMATTED | Group | Group | DSNTIAC error message structure | — |
| WS-DSNTIAC-MESG-LEN | PIC S9(4) COMP | Binary (halfword) | Message length (720 max) | — |
| WS-DSNTIAC-FMTD-TEXT-LINE | PIC X(72) OCCURS 10 | Alpha array | Formatted error text lines | — |
| WS-DSNTIAC-LRECL | PIC S9(4) COMP | Binary (halfword) | Logical record length (72) | — |

### CSDB2RPY.cpy — DB2 Priming Query & Error Formatting Procedures

Procedural copybook containing:
- `9998-PRIMING-QUERY`: Executes `SELECT 1 FROM SYSIBM.SYSDUMMY1` to verify DB2 connectivity
- `9999-FORMAT-DB2-MESSAGE`: Calls DSNTIAC utility to format SQLCA error messages

---

## 8. System / Common Copybooks

### COCOM01Y.cpy — Communication Area (COMMAREA)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alpha (4 chars) | Originating transaction ID | — |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alpha (8 chars) | Originating program name | — |
| CDEMO-TO-TRANID | PIC X(04) | Alpha (4 chars) | Target transaction ID | — |
| CDEMO-TO-PROGRAM | PIC X(08) | Alpha (8 chars) | Target program name | — |
| CDEMO-USER-ID | PIC X(08) | Alpha (8 chars) | Logged-in user ID | — |
| CDEMO-USER-TYPE | PIC X(01) | Alpha (1 char) | User type | 88: 'A'=Admin, 'U'=User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric (1 digit) | Program entry context | 88: 0=Enter, 1=Re-enter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric (9 digits) | Selected customer ID | — |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric (25) | Customer first name | — |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric (25) | Customer middle name | — |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric (25) | Customer last name | — |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Selected account ID | — |
| CDEMO-ACCT-STATUS | PIC X(01) | Alpha (1 char) | Selected account status | — |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric (16 digits) | Selected card number | — |
| CDEMO-LAST-MAP | PIC X(7) | Alpha (7 chars) | Last displayed BMS map | — |
| CDEMO-LAST-MAPSET | PIC X(7) | Alpha (7 chars) | Last used BMS mapset | — |

### CSUSR01Y.cpy — Security User Record

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alpha (8 chars) | User login ID | Primary key |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password | Plain text storage |
| SEC-USR-TYPE | PIC X(01) | Alpha (1 char) | User type flag | 'A'=Admin, 'U'=Regular |

### CSDAT01Y.cpy — Date/Time Working Storage

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours |
| WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minutes |
| WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current seconds |
| WS-CURTIME-MILSEC | PIC 9(02) | Numeric | Current milliseconds |
| WS-CURDATE-MM-DD-YY | Group | Edited date | Formatted MM/DD/YY |
| WS-CURTIME-HH-MM-SS | Group | Edited time | Formatted HH:MM:SS |
| WS-TIMESTAMP | Group | Timestamp | YYYY-MM-DD HH:MM:SS.MMMMMM |

### CODATECN.cpy — Date Conversion Record

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CODATECN-TYPE | PIC X | Alpha (1 char) | Input format: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date string |
| CODATECN-OUTTYPE | PIC X | Alpha (1 char) | Output format: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Output date string |
| CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Conversion error message |

### COTTL01Y.cpy — Screen Title Constants

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CCDA-TITLE01 | PIC X(40) | Constant | Application title line 1 ('AWS Mainframe Modernization') |
| CCDA-TITLE02 | PIC X(40) | Constant | Application title line 2 ('CardDemo') |
| CCDA-THANK-YOU | PIC X(40) | Constant | Logout thank-you message |

### CSMSG01Y.cpy — Common Screen Messages

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CCDA-MSG-THANK-YOU | PIC X(50) | Constant | Thank-you message |
| CCDA-MSG-INVALID-KEY | PIC X(50) | Constant | Invalid key press message |

### CSMSG02Y.cpy — Abend Data Structure

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| ABEND-CODE | PIC X(4) | Alpha (4 chars) | CICS abend code |
| ABEND-CULPRIT | PIC X(8) | Alpha (8 chars) | Program that abended |
| ABEND-REASON | PIC X(50) | Alphanumeric (50) | Abend reason text |
| ABEND-MSG | PIC X(72) | Alphanumeric (72) | Formatted abend message |

### COADM02Y.cpy — Admin Menu Options

Defines 6 admin options: User List (COUSR00C), User Add (COUSR01C), User Update (COUSR02C), User Delete (COUSR03C), Transaction Type List (COTRTLIC), Transaction Type Maintenance (COTRTUPC).

### COMEN02Y.cpy — Main Menu Options

Defines 11 user menu options: Account View (COACTVWC), Account Update (COACTUPC), Credit Card List (COCRDLIC), Credit Card View (COCRDSLC), Credit Card Update (COCRDUPC), Transaction List (COTRN00C), Transaction View (COTRN01C), Transaction Add (COTRN02C), Transaction Reports (CORPT00C), Bill Payment (COBIL00C), Pending Authorization View (COPAUS0C).

### CSLKPCDY.cpy — Lookup Code Repository

Contains 88-level validation tables for:
- **North American phone area codes** — complete list of valid 3-digit area codes
- **US state codes** — valid 2-letter state abbreviations
- **State + ZIP prefix combinations** — valid 4-character state code + first 2 ZIP digits

### CSSETATY.cpy — Field Attribute Setting (Parameterized)

Parameterized copybook using REPLACING to set BMS field attributes (color, protection) based on validation flags. Used with `COPY CSSETATY REPLACING (TESTVAR1) BY ...` for each editable field on CICS screens.

### CSSTRPFY.cpy — PFKey Storage Procedure

Procedural copybook containing `YYYY-STORE-PFKEY` paragraph — maps EIBAID attention identifiers to application-level PFKey values in CVCRD01Y work area.

### CSUTLDPY.cpy — Date Validation Procedures

Procedural copybook containing reusable paragraphs:
- `EDIT-DATE-CCYYMMDD` — master date validation entry
- `EDIT-YEAR-CCYY` — year/century validation (19xx–20xx only)
- `EDIT-MONTH` — month validation (01–12)
- `EDIT-DAY` — day validation with leap year handling
- `EDIT-DATE-OF-BIRTH` — birth date validation

### CSUTLDWY.cpy — Date Validation Working Storage

Accompanying working storage for CSUTLDPY procedures (edit flags, parsed date fields).

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

Polymorphic record structure using REDEFINES for branch migration export:
- Header: EXPORT-REC-TYPE, EXPORT-TIMESTAMP, EXPORT-SEQUENCE-NUM, EXPORT-BRANCH-ID, EXPORT-REGION-CODE
- Customer data (REC-TYPE 'C'): full customer fields with COMP-3 optimizations
- Account data (REC-TYPE 'A'): full account fields with COMP-3 for balances
- Transaction data (REC-TYPE 'T'): full transaction fields
- Card XREF data (REC-TYPE 'X'): cross-reference fields
- Card data (REC-TYPE 'D'): full card fields

### UNUSED1Y.cpy — Unused/Deprecated User Record

Legacy user data structure — appears to be superseded by CSUSR01Y.
