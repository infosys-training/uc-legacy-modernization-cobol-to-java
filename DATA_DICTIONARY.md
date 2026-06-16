# DATA DICTIONARY — CardDemo COBOL Estate

## Summary

| Metric | Count |
|--------|-------|
| Total copybooks | 47 |
| Main copybooks (`app/cpy/`) | 29 |
| Auth sub-app copybooks | 12 |
| Tran-type sub-app copybooks | 3 |
| VSAM-MQ sub-app copybooks | 3 |
| Business entities covered | Account, Customer, Card, Transaction, Authorization, User Security |

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Record (RECLN 300)

**Used by:** COACTUPC, COACTVWC, CBACT01C, CBACT04C, CBTRN02C, CBSTM03A, CBEXPORT, CBIMPORT, COBIL00C, COPAUA0C, COPAUS0C, COACCT01

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| ACCT-ID | PIC 9(11) | Numeric display | Primary key — 11-digit account identifier | Must be numeric; unique in ACCTDATA KSDS |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account status flag | 'Y' = Active, 'N' = Inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal, 2 implied decimals | Current account balance | Signed; can be negative (overlimit) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Maximum credit limit | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (CCYY-MM-DD) | Date account was opened | Validated via CSUTLDPY date routines (century 19/20 only) |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (CCYY-MM-DD) | Account/card expiration date | Must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (CCYY-MM-DD) | Date card was last reissued | *(no specific validation)* |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current billing cycle credit total | Accumulated from posted transactions |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current billing cycle debit total | Accumulated from posted transactions |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | Validated against CSLKPCDY state+ZIP table |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/interest rate group | Links to DISCGRP file (CVTRA02Y) |
| FILLER | PIC X(178) | Reserved | Padding to RECLN 300 | — |

### 1.2 CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

**Used by:** COACTUPC, COACTVWC, COCRDSLC, CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, CBEXPORT, CBIMPORT, COBIL00C, COPAUA0C, COPAUS0C, COACCT01

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number (primary key) | 16-char card number; key of CARDXREF KSDS |
| XREF-CUST-ID | PIC 9(09) | Numeric display | Customer ID owning this card | Foreign key to CUSTDATA (CVCUS01Y.CUST-ID) |
| XREF-ACCT-ID | PIC 9(11) | Numeric display | Account ID linked to this card | Foreign key to ACCTDATA (CVACT01Y.ACCT-ID) |
| FILLER | PIC X(14) | Reserved | Padding to RECLN 50 | — |

### 1.3 CVTRA01Y.cpy — Transaction Category Balance Record (RECLN 50)

**Used by:** CBACT04C, CBTRN02C

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-CAT-KEY (group) | — | Composite key | Unique key for account + type + category | — |
| ↳ TRANCAT-ACCT-ID | PIC 9(11) | Numeric display | Account ID | FK to ACCTDATA |
| ↳ TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to TRANTYPE (CVTRA03Y) |
| ↳ TRANCAT-CD | PIC 9(04) | Numeric display | Transaction category code | FK to TRANCATG (CVTRA04Y) |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance for this acct/type/category | Updated by CBTRN02C during posting |
| FILLER | PIC X(22) | Reserved | Padding | — |

### 1.4 CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

**Used by:** CBACT04C

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| DIS-GROUP-KEY (group) | — | Composite key | Lookup key for interest rates | — |
| ↳ DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group ID | FK to CVACT01Y.ACCT-GROUP-ID |
| ↳ DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | — |
| ↳ DIS-TRAN-CAT-CD | PIC 9(04) | Numeric display | Transaction category code | — |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Annual interest rate (%) | Applied by CBACT04C interest calc |
| FILLER | PIC X(28) | Reserved | Padding | — |

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Record (RECLN 500)

**Used by:** COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, CBCUS01C, CBTRN01C, CBSTM03A, CBEXPORT, CBIMPORT, COPAUA0C, COPAUS0C

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| CUST-ID | PIC 9(09) | Numeric display | Primary key — 9-digit customer ID | Unique in CUSTDATA KSDS |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | Non-blank required |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | Non-blank required |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | Non-blank required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated against CSLKPCDY 88-level state codes |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code (ISO 3166) | *(no specific validation in code)* |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | First 2 digits validated against state-to-ZIP table in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Area code validated against NANPA list |
| CUST-SSN | PIC 9(09) | Numeric display | Social Security Number | Must be 9-digit numeric in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID (passport/DL) | Optional |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (CCYY-MM-DD) | Date of birth | Validated via CSUTLDPY (century, month 1–12, day range) |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account for auto-pay | Optional |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder indicator | 'Y' = Primary, 'N' = Authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric display | FICO credit score | Range 300–850 (validated in COACTUPC) |
| FILLER | PIC X(168) | Reserved | Padding to RECLN 500 | — |

### 2.2 CUSTREC.cpy — Customer Record (Alternate Layout)

**Note:** Structurally identical to CVCUS01Y.cpy but with slightly different indentation. Used in specific batch contexts.

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Record (RECLN 150)

**Used by:** COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C, CBTRN01C, CBEXPORT, CBIMPORT, COPAUS0C

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Primary key — 16-char card number | Unique in CARDDATA KSDS; matches XREF-CARD-NUM |
| CARD-ACCT-ID | PIC 9(11) | Numeric display | Account this card belongs to | FK to CVACT01Y.ACCT-ID |
| CARD-CVV-CD | PIC 9(03) | Numeric display | CVV security code | 3-digit numeric |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name printed on card | Non-blank validated in COCRDUPC |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (CCYY-MM-DD) | Card expiration date | Date validated via CSUTLDPY |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card status | 'Y' = Active, 'N' = Inactive |
| FILLER | PIC X(59) | Reserved | Padding to RECLN 150 | — |

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Record (RECLN 350)

**Used by:** COTRN00C, COTRN01C, COTRN02C, COBIL00C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, CBEXPORT, CBIMPORT

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Primary key — unique transaction ID | System-generated; unique in TRANSACT KSDS |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to CVTRA03Y.TRAN-TYPE; e.g., 'SA' (sale), 'CR' (credit) |
| TRAN-CAT-CD | PIC 9(04) | Numeric display | Transaction category code | FK to CVTRA04Y; e.g., 5001 (grocery), 5411 (restaurant) |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Origination source | e.g., 'ONLINE', 'POS', 'BATCH' |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description/narrative | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Positive = debit, negative = credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric display | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card used for this transaction | FK to CVACT02Y.CARD-NUM |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Origination timestamp (ISO 8601) | YYYY-MM-DD HH:MM:SS.FFFFFF |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | Set during CBTRN02C posting |
| FILLER | PIC X(20) | Reserved | Padding to RECLN 350 | — |

### 4.2 CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

**Used by:** CBTRN01C, CBTRN02C

Same structure as CVTRA05Y but with `DALYTRAN-` prefix. Represents unposted daily transactions read from DALYTRAN.PS before posting to TRANSACT KSDS.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | Daily transaction ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric display | Category code |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | Source |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | Description |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric display | Merchant ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp |
| FILLER | PIC X(20) | Reserved | Padding |

### 4.3 CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

**Used by:** CBTRN03C, COTRTLIC, COTRTUPC

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code (PK) | 2-char code (e.g., 'SA', 'CR', 'RF') |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Description | e.g., 'Sale', 'Credit', 'Refund' |
| FILLER | PIC X(08) | Reserved | Padding | — |

### 4.4 CVTRA04Y.cpy — Transaction Category Record (RECLN 60)

**Used by:** CBTRN03C, COTRTLIC, COTRTUPC

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-CAT-KEY (group) | — | Composite key | — | — |
| ↳ TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Parent transaction type | FK to CVTRA03Y.TRAN-TYPE |
| ↳ TRAN-CAT-CD | PIC 9(04) | Numeric display | Category code within type | Unique within type |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | e.g., 'Grocery', 'Restaurant', 'Gas Station' |
| FILLER | PIC X(04) | Reserved | Padding | — |

### 4.5 COSTM01.CPY — Statement Transaction Layout

**Used by:** CBSTM03A

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| TRNX-KEY (group) | — | Composite key | Card+Transaction ID |
| ↳ TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number (primary sort) |
| ↳ TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID (secondary sort) |
| TRNX-REST (group) | — | Data fields | — |
| ↳ TRNX-TYPE-CD | PIC X(02) | Alphanumeric | Type code |
| ↳ TRNX-CAT-CD | PIC 9(04) | Numeric | Category code |
| ↳ TRNX-SOURCE | PIC X(10) | Alphanumeric | Source |
| ↳ TRNX-DESC | PIC X(100) | Alphanumeric | Description |
| ↳ TRNX-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| ↳ TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| ↳ TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name |
| ↳ TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city |
| ↳ TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP |
| ↳ TRNX-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp |
| ↳ TRNX-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp |
| ↳ FILLER | PIC X(20) | Reserved | Padding |

### 4.6 CVTRA07Y.cpy — Transaction Report Layout

**Used by:** CBTRN03C

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| **REPORT-NAME-HEADER** | | | |
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report identifier ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report title ('Daily Transaction Report') |
| REPT-DATE-HEADER | PIC X(12) | Alphanumeric | 'Date Range: ' |
| REPT-START-DATE | PIC X(10) | Alphanumeric | Report start date |
| REPT-END-DATE | PIC X(10) | Alphanumeric | Report end date |
| **TRANSACTION-DETAIL-REPORT** | | | |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Source |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount |
| **REPORT-PAGE-TOTALS** | | | |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| **REPORT-ACCOUNT-TOTALS** | | | |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |
| **REPORT-GRAND-TOTALS** | | | |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total |

---

## 5. Authorization Entity (Sub-App)

### 5.1 CIPAUSMY.cpy — IMS Pending Authorization Summary Segment

**Used by:** COPAUA0C, COPAUS0C, COPAUS1C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID (IMS root key) | Links to ACCTDATA |
| PA-CUST-ID | PIC 9(09) | Numeric display | Customer ID | Links to CUSTDATA |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric | Overall auth status | 'A' = Active, 'I' = Inactive |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Array of status flags | Account status history (5 slots) | — |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit at auth time | — |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash limit at auth time | — |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Credit balance at auth time | — |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Cash balance at auth time | — |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of approved auths | — |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of declined auths | — |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount | — |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount | — |
| FILLER | PIC X(34) | Reserved | Padding | — |

### 5.2 CIPAUDTY.cpy — IMS Pending Authorization Detail Segment

**Used by:** COPAUA0C, COPAUS0C, COPAUS1C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| PA-AUTHORIZATION-KEY (group) | — | Composite | IMS segment key | — |
| ↳ PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Auth date (packed) | — |
| ↳ PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Auth time (packed) | — |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original auth date (YYMMDD) | — |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original auth time (HHMMSS) | — |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number | FK to CARDDATA |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type | — |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry (YYMM) | — |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type (ISO 8583) | — |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Originating system/channel | — |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization approval code | Assigned on approval |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code | 88 PA-AUTH-APPROVED VALUE '00' |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Reason for decline | — |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric display | Processing code | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Requested transaction amount | — |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount | May differ from requested |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | MCC (Merchant Category Code) | — |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country code | — |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric display | POS entry mode | — |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant identifier | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP | — |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference | — |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric | Matching status | 88: P=Pending, D=Declined, E=Expired, M=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | Fraud flag | 88: F=Fraud Confirmed, R=Fraud Removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric | Fraud report date | — |
| FILLER | PIC X(17) | Reserved | Padding | — |

### 5.3 CCPAURQY.cpy — Authorization Request (MQ Message)

**Used by:** COPAUA0C

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | ISO 8583 message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Source system |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | MCC |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Country code |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference |

### 5.4 CCPAURLY.cpy — Authorization Response (MQ Message)

**Used by:** COPAUA0C

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Approval code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Reason code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### 5.5 CCPAUERY.cpy — Error Log Record

**Used by:** COPAUA0C, COPAUS0C, COPAUS1C

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| ERR-DATE | PIC X(06) | Alphanumeric | Error date (YYMMDD) | — |
| ERR-TIME | PIC X(06) | Alphanumeric | Error time (HHMMSS) | — |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | Application identifier | — |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | Program that generated error | — |
| ERR-LOCATION | PIC X(04) | Alphanumeric | Location within program | — |
| ERR-LEVEL | PIC X(01) | Alphanumeric | Severity level | 88: L=Log, I=Info, W=Warning, C=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem | 88: A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | Primary error code | — |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | Secondary error code | — |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | Error message text | — |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Related event/record key | — |

### 5.6 IMS PCB Copybooks

| Copybook | Purpose | Key Fields |
|----------|---------|------------|
| PAUTBPCB.CPY | IMS PCB for Pending Auth DB | PAUT-DBDNAME, PAUT-SEG-LEVEL, PAUT-PCB-STATUS, PAUT-PCB-PROCOPT, PAUT-SEG-NAME, PAUT-KEYFB (255 bytes) |
| PASFLPCB.CPY | IMS PCB for Summary DB (alt) | PASFL-DBDNAME, PASFL-PCB-STATUS, PASFL-KEYFB (100 bytes) |
| PADFLPCB.CPY | IMS PCB for Detail DB (alt) | PADFL-DBDNAME, PADFL-PCB-STATUS, PADFL-KEYFB (255 bytes) |
| IMSFUNCS.cpy | IMS DL/I function codes | FUNC-GU, FUNC-GHU, FUNC-GN, FUNC-GHN, FUNC-GNP, FUNC-GHNP, FUNC-REPL, FUNC-ISRT, FUNC-DLET |

---

## 6. User Security Entity

### 6.1 CSUSR01Y.cpy — User Security Record (RECLN 80)

**Used by:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | Primary key — User login ID | Unique in USRSEC KSDS |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | Password (plain text) | Compared against input in COSGN00C |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Reserved | Padding | — |

### 6.2 UNUSED1Y.cpy — Unused/Legacy User Layout

Identical structure to CSUSR01Y with different field prefix (UNUSED-). Appears to be a deprecated copy.

---

## 7. Common/Utility Copybooks

### 7.1 COCOM01Y.cpy — Communication Area (COMMAREA)

**Used by:** All 25 CICS online programs (passed via EXEC CICS XCTL/LINK)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| **CDEMO-GENERAL-INFO** | | | Navigation/session context |
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Originating CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Originating program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type (88: A=Admin, U=User) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context (88: 0=Enter, 1=Re-enter) |
| **CDEMO-CUSTOMER-INFO** | | | Selected customer context |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer ID |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | First name |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | Middle name |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | Last name |
| **CDEMO-ACCOUNT-INFO** | | | Selected account context |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account ID |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status |
| **CDEMO-CARD-INFO** | | | Selected card context |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card number |
| **CDEMO-MORE-INFO** | | | Screen state |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last BMS map name |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last BMS mapset name |

### 7.2 CSDAT01Y.cpy — Date/Time Working Storage

**Used by:** All CICS online programs

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year (4-digit) |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours |
| WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minutes |
| WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current seconds |
| WS-TIMESTAMP | PIC X(26) | Alphanumeric | Full ISO timestamp (YYYY-MM-DD HH:MM:SS.FFFFFF) |

### 7.3 CODATECN.cpy — Date Conversion Record

**Used by:** CBACT01C, CODATE01

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| CODATECN-TYPE | PIC X | Alphanumeric | Input format (88: '1'=YYYYMMDD, '2'=YYYY-MM-DD) |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date string |
| CODATECN-OUTTYPE | PIC X | Alphanumeric | Output format (88: '1'=YYYY-MM-DD, '2'=YYYYMMDD) |
| CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Output date string |
| CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Error message if conversion fails |

### 7.4 CSLKPCDY.cpy — Validation Lookup Repository

**Used by:** COACTUPC

Contains 88-level condition names for validation:

| Validation Category | Implementation | Record Count |
|-------------------|----------------|--------------|
| **Phone area codes** | 88 VALID-PHONE-AREA-CODE on PIC XXX | ~350 NANPA area codes |
| **US state codes** | 88 VALID-US-STATE-CODE on PIC XX | 50 states + DC + territories |
| **State-to-ZIP prefix** | 88 VALID-STATE-ZIP on PIC XXXX | ~180 state+ZIP-prefix pairs |

### 7.5 Other Utility Copybooks

| Copybook | Purpose | Used By |
|----------|---------|---------|
| COTTL01Y.cpy | Screen title/branding ('AWS Mainframe Modernization — CardDemo') | All CICS programs |
| CSMSG01Y.cpy | Common messages (Thank You, Invalid Key) | All CICS programs |
| CSMSG02Y.cpy | Abend handling work area (code, culprit, reason, message) | All CICS programs |
| CSSETATY.cpy | Screen attribute macro (COPY REPLACING) — sets red color + asterisk on validation errors | COACTUPC (×13 instances) |
| CSSTRPFY.cpy | PFKey evaluation procedure — maps EIBAID to CCARD-AID flags | Most CICS programs |
| CSUTLDPY.cpy | Date validation procedures (EDIT-DATE-CCYYMMDD, EDIT-YEAR-CCYY, EDIT-MONTH, EDIT-DAY, EDIT-DATE-OF-BIRTH) | COACTUPC, COCRDUPC |
| CSUTLDWY.cpy | Date validation working storage (flags, counters, error messages) | COACTUPC, COCRDUPC |
| CVCRD01Y.cpy | Online card working areas (AID flags, next program/map, error/return messages, CC-ACCT-ID, CC-CARD-NUM, CC-CUST-ID) | All card/account CICS programs |
| COADM02Y.cpy | Admin menu option definitions (6 options → program names) | COADM01C |
| COMEN02Y.cpy | Main menu option definitions (11 options → program names) | COMEN01C |
| CVEXPORT.cpy | Multi-record export layout (EXPORT-RECORD with REDEFINES for each entity type) | CBEXPORT, CBIMPORT |

### 7.6 DB2 Sub-App Copybooks

| Copybook | Purpose | Used By |
|----------|---------|---------|
| CSDB2RPY.cpy | DB2 priming query procedure (SELECT 1 FROM SYSIBM.SYSDUMMY1) + error formatting | COTRTLIC, COTRTUPC |
| CSDB2RWY.cpy | DB2 working storage (SQLCODE handling, error flags) | COTRTLIC, COTRTUPC, COBTUPDT |
