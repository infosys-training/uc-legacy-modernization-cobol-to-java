# DATA DICTIONARY — CardDemo COBOL Estate

## Overview

| Metric | Value |
|--------|-------|
| Total Copybooks | 47 |
| Main Data Copybooks (`app/cpy/`) | 27 |
| Sub-App Copybooks | 12 |
| BMS Map Copybooks | 8 |
| Business Entities | 6 core + 4 authorization + 3 utility |

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Unique account identifier — primary key | Must be unique; 11-digit number |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Account status flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Signed; max ±9,999,999,999.99 |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum credit allowed | Must be positive |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum cash advance allowed | Must be positive; ≤ CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (10) | Date account was opened | Format: YYYY-MM-DD; validated by CSUTLDTC |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Account expiration date | Format: YYYY-MM-DD; must be > OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (10) | Date of last card reissue | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle credit total | Running total, reset at cycle end |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle debit total | Running total, reset at cycle end |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account holder ZIP code | Validated against CSLKPCDY state+ZIP table |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure/interest rate group | Links to CVTRA02Y (DIS-GROUP-RECORD) |
| FILLER | PIC X(178) | Padding | Reserved space | Fills to 300-byte record length |

**VSAM File:** ACCTFILE (KSDS, key = ACCT-ID)
**Used By:** COACTUPC, COACTVWC, CBACT01C, CBACT04C, CBTRN02C, CBSTM03A, COBIL00C, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT, COACCT01

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| CUST-ID | PIC 9(09) | Numeric (9 digits) | Unique customer identifier — primary key | Must be unique; 9-digit number |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | Non-blank required |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | Non-blank required |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Street address line 1 | Non-blank required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Street address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Street address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric (2) | US state code | Validated against 50 state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric (3) | Country code | ISO 3166-1 alpha-3 |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | ZIP/postal code | Validated against state+ZIP prefix table in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-SSN | PIC 9(09) | Numeric (9 digits) | Social Security Number | 9 digits; validated for format in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID number | Free-form |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (10) | Date of birth | Format: YYYY-MM-DD; validated by CSUTLDTC |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | EFT/ACH account reference | Used for electronic fund transfers |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric (1) | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3 digits) | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Padding | Reserved space | Fills to 500-byte record length |

**VSAM File:** CUSTFILE (KSDS, key = CUST-ID)
**Used By:** COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, CBCUS01C, CBSTM03A/B, COPAUA0C, CBEXPORT, CBIMPORT

### CUSTREC.cpy — Customer Record (Alternate Layout for CBSTM03A)

Identical field structure to CVCUS01Y.cpy with slightly different indentation. Used exclusively by the statement generation batch program. Same 500-byte record length.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number — primary key | 16-character card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account foreign key | Must exist in ACCTFILE |
| CARD-CVV-CD | PIC 9(03) | Numeric (3 digits) | Card verification value | 3-digit CVV code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name embossed on card | Non-blank required |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Card active status | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Padding | Reserved space | Fills to 150-byte record length |

**VSAM File:** CARDFILE (KSDS, key = CARD-NUM)
**Used By:** COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C, CBEXPORT, CBIMPORT

### CVACT03Y.cpy — Card Cross-Reference (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number — primary key | Must exist in CARDFILE |
| XREF-CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer foreign key | Must exist in CUSTFILE |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account foreign key | Must exist in ACCTFILE |
| FILLER | PIC X(14) | Padding | Reserved space | Fills to 50-byte record length |

**VSAM File:** CARDXREF (KSDS, key = XREF-CARD-NUM; AIX = CXACAIX on XREF-ACCT-ID)
**Used By:** COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, CBTRN01C, CBTRN02C, CBACT04C, CBSTM03A/B, COPAUA0C, CBEXPORT, CBIMPORT

---

## 4. Transaction Entities

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Transaction ID — primary key | Auto-generated unique ID |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Must exist in CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | Must exist in CVTRA04Y |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source | e.g., 'ONLINE', 'BATCH', 'ATM' |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | Free-form text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Signed; credits positive, debits negative |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant identifier | 9-digit merchant ID |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | Free-form |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | Free-form |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | Free-form |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card used for transaction | Must exist in CARDXREF |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original transaction timestamp | Format: YYYY-MM-DD-HH.MM.SS.NNNNNN |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | Set by CBTRN02C during posting |
| FILLER | PIC X(20) | Padding | Reserved space | Fills to 350-byte record length |

**VSAM File:** TRANSACT (KSDS, key = TRAN-ID; AIX on TRAN-CARD-NUM)
**Used By:** COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C, CBTRN01C, CBTRN02C, CBTRN03C, CBACT04C, CBSTM03A, CBEXPORT, CBIMPORT

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same structure as CVTRA05Y but with `DALYTRAN-` prefix. Used for the daily input transaction file that feeds into CBTRN02C for posting.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric (16) | Daily transaction ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Source system |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric (100) | Description |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Amount |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp |
| FILLER | PIC X(20) | Padding | Reserved |

**File:** DALYTRAN (sequential)
**Used By:** CBTRN01C, CBTRN02C

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-CAT-KEY (group) | — | — | Composite key | — |
| → TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | FK to ACCTFILE |
| → TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK to CVTRA03Y |
| → TRANCAT-CD | PIC 9(04) | Numeric (4) | Category code | FK to CVTRA04Y |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance for this account/type/category | Updated by CBTRN02C, read by CBACT04C |
| FILLER | PIC X(22) | Padding | Reserved | Fills to 50 bytes |

**VSAM File:** TCATBAL (KSDS, composite key)
**Used By:** CBTRN02C, CBACT04C

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| DIS-GROUP-KEY (group) | — | — | Composite key | — |
| → DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Account group ID | Links to ACCT-GROUP-ID in CVACT01Y |
| → DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK to CVTRA03Y |
| → DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code | FK to CVTRA04Y |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest rate percentage | Used by CBACT04C for interest calculation |
| FILLER | PIC X(28) | Padding | Reserved | Fills to 50 bytes |

**VSAM File:** DISCGRP (KSDS, composite key)
**Used By:** CBACT04C

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric (2) | Transaction type code — primary key | 2-character code (e.g., 'PR', 'CA', 'PA') |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Type description | e.g., 'Purchase', 'Cash Advance', 'Payment' |
| FILLER | PIC X(08) | Padding | Reserved | Fills to 60 bytes |

**VSAM File / DB2 Table:** TRANTYPE (KSDS); also DB2 TRANSACTION_TYPE table
**Used By:** CBTRN03C, COTRTLIC, COTRTUPC

### CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| TRAN-CAT-KEY (group) | — | — | Composite key | — |
| → TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK to CVTRA03Y |
| → TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code | 4-digit category within type |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | e.g., 'Retail Purchase', 'Online Purchase' |
| FILLER | PIC X(04) | Padding | Reserved | Fills to 60 bytes |

**VSAM File / DB2 Table:** TRANCATG (KSDS); also DB2 TRANSACTION_CATEGORY table
**Used By:** CBTRN03C, COTRTLIC, COTRTUPC

### CVTRA07Y.cpy — Transaction Report Layout

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| **REPORT-NAME-HEADER** (group) | | | Report header structure |
| → REPT-SHORT-NAME | PIC X(38) | Alphanumeric | VALUE 'DALYREPT' |
| → REPT-LONG-NAME | PIC X(41) | Alphanumeric | VALUE 'Daily Transaction Report' |
| → REPT-DATE-HEADER | PIC X(12) | Alphanumeric | VALUE 'Date Range: ' |
| → REPT-START-DATE | PIC X(10) | Alphanumeric | Start date of report range |
| → REPT-END-DATE | PIC X(10) | Alphanumeric | End date of report range |
| **TRANSACTION-DETAIL-REPORT** (group) | | | Detail line layout |
| → TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID |
| → TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID |
| → TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code |
| → TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description |
| → TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| → TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description |
| → TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Transaction source |
| → TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount |
| **REPORT-PAGE-TOTALS** | | | Page totals structure |

**Used By:** CBTRN03C

### COSTM01.CPY — Statement Transaction Layout (Reordered for CBSTM03A)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| TRNX-KEY (group) | | | Composite key for statement sort |
| → TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number (sort key 1) |
| → TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID (sort key 2) |
| TRNX-REST (group) | | | Remaining transaction fields |
| → TRNX-TYPE-CD | PIC X(02) | Alphanumeric | Type code |
| → TRNX-CAT-CD | PIC 9(04) | Numeric | Category code |
| → TRNX-SOURCE | PIC X(10) | Alphanumeric | Source |
| → TRNX-DESC | PIC X(100) | Alphanumeric | Description |
| → TRNX-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| → TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| → TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name |
| → TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city |
| → TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP |
| → TRNX-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp |
| → TRNX-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp |

**Used By:** CBSTM03A

---

## 5. Authorization Entities (IMS Segments)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Root Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID — segment key | FK to ACCTFILE |
| PA-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID | FK to CUSTFILE |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric | Authorization status | Active/Inactive flag |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Array (5×2) | Account status history | 5 historical status entries |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Credit limit at auth time | Snapshot from ACCTFILE |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Cash advance limit | Snapshot from ACCTFILE |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Credit balance at auth time | Running balance |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Cash balance at auth time | Running balance |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of approved authorizations | Running counter |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of declined authorizations | Running counter |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Total approved amount | Running total |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Total declined amount | Running total |
| FILLER | PIC X(34) | Padding | Reserved | — |

**IMS Database:** PAUTHDB (root segment PAUTSMY)
**Used By:** COPAUA0C, COPAUS0C, COPAUS1C, CBPAUP0C, PAUDBLOD, DBUNLDGS

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Child Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| PA-AUTHORIZATION-KEY (group) | — | — | Composite segment key | — |
| → PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (compressed) | Julian date |
| → PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (compressed) | HHMMSSMMM |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original auth date (display) | MMDDYY |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original auth time (display) | HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number | FK to CARDXREF |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type | e.g., 'AUTH', 'VOID' |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry (MMYY) | Must be valid future date |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type | ISO 8583 message type |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message source | POS, ATM, ONLINE |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID code | Assigned on approval |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code | '00' = approved |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Decline reason code | Populated when declined |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | ISO processing code | 6-digit processing code |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Requested transaction amount | Must be > 0 |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Approved amount | ≤ requested amount |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | Merchant category (MCC) | ISO 18245 MCC |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country code | ISO 3166 |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | ISO 8583 field 22 |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP | — |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference | — |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric | Authorization match status | 88-levels: P=Pending, D=Declined, E=Expired, M=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | Fraud flag | 88-levels: F=Confirmed, R=Removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric | Date fraud was reported | YYYYMMDD |
| FILLER | PIC X(17) | Padding | Reserved | — |

**IMS Database:** PAUTHDB (child segment PAUTDTY under PAUTSMY)
**Used By:** COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, CBPAUP0C, PAUDBLOD, DBUNLDGS

### CCPAURQY.cpy — Pending Authorization Request (MQ Message)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Source (POS/ATM/Online) |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | MCC code |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | Entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference |

**Used By:** COPAUA0C (received via MQ)

### CCPAURLY.cpy — Pending Authorization Response (MQ Message)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code (00=approved) |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Decline reason |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

**Used By:** COPAUA0C (sent via MQ)

### CCPAUERY.cpy — Authorization Error Log Record

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| ERR-DATE | PIC X(06) | Alphanumeric | Error date |
| ERR-TIME | PIC X(06) | Alphanumeric | Error time |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | Program name |
| ERR-LOCATION | PIC X(04) | Alphanumeric | Error location code |
| ERR-LEVEL | PIC X(01) | Alphanumeric | Severity: L=Log, I=Info, W=Warning, C=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem: A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | Primary error code |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | Secondary error code |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | Error message text |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Event correlation key |

**Used By:** COPAUA0C

---

## 6. User/Security Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|------------------|------------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric (8) | User ID — primary key | 1–8 characters |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | Non-blank |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | Non-blank |
| SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password (plaintext) | 1–8 characters |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric (1) | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Padding | Reserved | Fills to 80 bytes |

**VSAM File:** USRSEC (KSDS, key = SEC-USR-ID)
**Used By:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C

### UNUSED1Y.cpy — Unused Data Record (RECLN 80)

Identical structure to CSUSR01Y with `UNUSED-` prefix. Placeholder/template copybook, not referenced by any program.

---

## 7. Application Infrastructure Copybooks

### COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target CICS transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Signed-on user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 88: 'A'=Admin, 'U'=User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 88: 0=Enter, 1=Re-enter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer context |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alphanumeric | Customer name context |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account context |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status context |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card context |
| CDEMO-LAST-MAP/MAPSET | PIC X(7) each | Alphanumeric | Last BMS map/mapset sent |

**Used By:** All 21 CICS programs

### COMEN02Y.cpy — Main Menu Options (11 entries)

Defines menu items with program names for XCTL navigation:
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

### COADM02Y.cpy — Admin Menu Options (6 entries)

1. User List (Security) → COUSR00C
2. User Add (Security) → COUSR01C
3. User Update (Security) → COUSR02C
4. User Delete (Security) → COUSR03C
5. Transaction Type List/Update (DB2) → COTRTLIC
6. Transaction Type Maintenance (DB2) → COTRTUPC

### CVCRD01Y.cpy — CICS Card Work Areas

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| CCARD-AID | PIC X(5) | CICS attention ID (88-levels: ENTER, CLEAR, PA1–PA2, PFK01–PFK12) |
| CCARD-NEXT-PROG | PIC X(8) | Next program for XCTL |
| CCARD-NEXT-MAPSET | PIC X(7) | Next BMS mapset |
| CCARD-NEXT-MAP | PIC X(7) | Next BMS map |
| CCARD-ERROR-MSG | PIC X(75) | Error message buffer |
| CCARD-RETURN-MSG | PIC X(75) | Return message buffer |
| CC-ACCT-ID | PIC X(11) | Working account ID |
| CC-CARD-NUM | PIC X(16) | Working card number |
| CC-CUST-ID | PIC X(09) | Working customer ID |

**Used By:** COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COBIL00C

### COTTL01Y.cpy — Screen Titles

| Field Name | Value | Purpose |
|------------|-------|---------|
| CCDA-TITLE01 | 'AWS Mainframe Modernization' | Screen header line 1 |
| CCDA-TITLE02 | 'CardDemo' | Screen header line 2 |
| CCDA-THANK-YOU | 'Thank you for using CCDA application...' | Sign-off message |

### CSDAT01Y.cpy — Date/Time Working Storage

Provides formatted date/time fields: WS-CURDATE (YYYYMMDD), WS-CURTIME (HHMMSSMS), WS-CURDATE-MM-DD-YY, WS-CURTIME-HH-MM-SS, WS-TIMESTAMP (YYYY-MM-DD HH:MM:SS.NNNNNN).

### CSMSG01Y.cpy — Message Area

Standard message display field for screen messages and error reporting.

### CSMSG02Y.cpy — Extended Message Area

Extended message area with additional message fields.

### CSSETATY.cpy — Screen Attribute Settings

BMS screen attribute byte definitions — used via `COPY REPLACING` for dynamic field attributes (protected, unprotected, bright, dark, etc.).

### CSSTRPFY.cpy — String/PF Key Processing

Common PF key handling and string processing utilities.

### CSLKPCDY.cpy — Validation Lookup Tables

Contains comprehensive validation data:
- **North American phone area codes** — Full NANPA list (~400 codes) with 88-level VALID-PHONE-AREA-CODE
- **US state codes** — All 50 states + territories with 88-level VALID-STATE-CODE
- **State + ZIP prefix** — State code to ZIP prefix mapping for cross-validation

### CSUTLDPY.cpy — Date Validation Parameters

Working storage for date validation utility (CSUTLDTC).

### CSUTLDWY.cpy — Date Validation Working Storage

Additional working storage for date conversion routines.

### CODATECN.cpy — Date Conversion Constants

Date format conversion constants used by CBACT01C.

---

## 8. Data Export Entity

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| EXPORT-REC-TYPE | PIC X(1) | Record type identifier |
| EXPORT-TIMESTAMP | PIC X(26) | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Sequence number |
| EXPORT-BRANCH-ID | PIC X(4) | Branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | Region code |
| EXPORT-RECORD-DATA | PIC X(460) | Record payload (REDEFINES for each entity) |

REDEFINES structures mirror the core entities (Customer, Account, Transaction, Card-XREF, Card) with COMP/COMP-3 optimization for numeric fields.

**Used By:** CBEXPORT, CBIMPORT

---

## 9. DB2 Infrastructure Copybooks

### CSDB2RWY.cpy — DB2 Common Working Storage

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|------------------|
| WS-DISP-SQLCODE | PIC ----9 | Formatted SQLCODE for display |
| WS-DUMMY-DB2-INT | PIC S9(4) COMP-3 | Dummy variable for connectivity test |
| WS-DB2-PROCESSING-FLAG | PIC X(1) | 88: '0'=OK, '1'=Error |
| WS-DB2-CURRENT-ACTION | PIC X(72) | Current DB2 action description |
| WS-DSNTIAC-FORMATTED | — | DSNTIAC message buffer (10×72 char lines) |

### CSDB2RPY.cpy — DB2 Common Procedures

Contains `9998-PRIMING-QUERY` (DB2 connectivity test via `SELECT 1 FROM SYSIBM.SYSDUMMY1`) and `9999-FORMAT-DB2-MESSAGE` (DSNTIAC error formatting).

**Used By:** COTRTLIC, COTRTUPC, COPAUS2C, COBTUPDT

### IMSFUNCS.cpy — IMS DL/I Function Codes

| Field Name | Value | IMS Function |
|------------|-------|-------------|
| FUNC-GU | 'GU  ' | Get Unique |
| FUNC-GHU | 'GHU ' | Get Hold Unique |
| FUNC-GN | 'GN  ' | Get Next |
| FUNC-GHN | 'GHN ' | Get Hold Next |
| FUNC-GNP | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | 'REPL' | Replace |
| FUNC-ISRT | 'ISRT' | Insert |
| FUNC-DLET | 'DLET' | Delete |

**Used By:** DBUNLDGS, PAUDBLOD

---

## 10. Entity Relationship Summary

```
Customer (CVCUS01Y)
    │
    └──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
                  │                              │
                  │                              ├── Disclosure Group (CVTRA02Y)
                  │                              │     via ACCT-GROUP-ID
                  │                              │
                  │                              └── TranCat Balance (CVTRA01Y)
                  │                                    via ACCT-ID + TYPE + CAT
                  │
              Card (CVACT02Y)
                  │
           Transaction (CVTRA05Y) ──► TranType (CVTRA03Y)
                  │                         │
                  │                    TranCategory (CVTRA04Y)
                  │
           Daily Transaction (CVTRA06Y)
                  │
           Auth Summary (CIPAUSMY) ──► Auth Detail (CIPAUDTY)
              [IMS root]                  [IMS child]
```

### VSAM File → Copybook → DB Table Mapping

| VSAM File | DD Name | Copybook | Record Length | Key Field | Org |
|-----------|---------|----------|---------------|-----------|-----|
| Account | ACCTFILE | CVACT01Y | 300 | ACCT-ID | KSDS |
| Customer | CUSTFILE | CVCUS01Y | 500 | CUST-ID | KSDS |
| Card | CARDFILE | CVACT02Y | 150 | CARD-NUM | KSDS |
| Card-XREF | CARDXREF | CVACT03Y | 50 | XREF-CARD-NUM | KSDS |
| Transaction | TRANSACT | CVTRA05Y | 350 | TRAN-ID | KSDS |
| TranCat Balance | TCATBAL | CVTRA01Y | 50 | Composite | KSDS |
| Disclosure Group | DISCGRP | CVTRA02Y | 50 | Composite | KSDS |
| Tran Type | TRANTYPE | CVTRA03Y | 60 | TRAN-TYPE | KSDS |
| Tran Category | TRANCATG | CVTRA04Y | 60 | Composite | KSDS |
| User Security | USRSEC | CSUSR01Y | 80 | SEC-USR-ID | KSDS |
| Daily Trans | DALYTRAN | CVTRA06Y | 350 | Sequential | SEQ |
| Daily Rejects | DALYREJS | — | Variable | Sequential | SEQ |
