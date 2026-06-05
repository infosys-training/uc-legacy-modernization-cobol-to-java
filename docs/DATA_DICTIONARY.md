# Data Dictionary — CardDemo COBOL Estate

> Generated: 2026-06-05 | Source: 47 copybooks across `app/cpy/` and sub-application directories

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Primary key — unique account identifier | Must be numeric, non-zero |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Account active flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Signed; can be negative (overdrawn) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum credit limit | Must be positive |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Date string (YYYY-MM-DD) | Account opening date | Validated via CSUTLDPY/CSUTLDWY |
| ACCT-EXPIRAION-DATE | PIC X(10) | Date string (YYYY-MM-DD) | Account expiration date | Must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Date string (YYYY-MM-DD) | Last reissue date | Must be ≥ ACCT-OPEN-DATE |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle credit total | Accumulated credits this cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current cycle debit total | Accumulated debits this cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account holder ZIP code | Validated against state-ZIP table in CSLKPCDY |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure/interest rate group ID | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| FILLER | PIC X(178) | Padding | Reserved space | — |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric (9 digits) | Primary key — unique customer identifier | Must be numeric, non-zero |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | Required; alphabetic |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | Required; alphabetic |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Street address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Street address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Street address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric (2) | US state code | Validated against 50-state list in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric (3) | Country code | ISO 3-letter code |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | ZIP/postal code | First 2 digits validated against state in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-SSN | PIC 9(09) | Numeric (9 digits) | Social Security Number | 9 digits; no format validation beyond numeric |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID number | Optional |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Date string (YYYY-MM-DD) | Date of birth | Validated via CSUTLDPY; must be in past |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | Electronic funds transfer account | Optional |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Flag (1) | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3 digits) | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Padding | Reserved space | — |

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (PAN) | 16-digit card number |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Foreign key to Account | Must exist in ACCTFILE |
| CARD-CVV-CD | PIC 9(03) | Numeric (3 digits) | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name embossed on card | Derived from customer name |
| CARD-EXPIRAION-DATE | PIC X(10) | Date string (YYYY-MM-DD) | Card expiration date | Must be future date |
| CARD-ACTIVE-STATUS | PIC X(01) | Flag (1) | Card active status | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Padding | Reserved space | — |

---

## 4. Card-Account Cross-Reference

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (key) | Must exist in CARDFILE |
| XREF-CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer ID linkage | Must exist in CUSTFILE |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account ID linkage | Must exist in ACCTFILE |
| FILLER | PIC X(14) | Padding | Reserved space | — |

---

## 5. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Unique transaction identifier | System-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Must exist in TRANTYPE file |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | Must exist in TRANCATG file |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source/channel | e.g., 'POS', 'ATM', 'ONLINE' |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Signed; negative = credit/refund |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant identifier | Required for POS transactions |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | Required for POS transactions |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | Optional |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | Optional |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card used for transaction | Must exist in CARDXREF |
| TRAN-ORIG-TS | PIC X(26) | Timestamp (26) | Transaction origination timestamp | Format: YYYY-MM-DD-HH.MM.SS.NNNNNN |
| TRAN-PROC-TS | PIC X(26) | Timestamp (26) | Transaction processing timestamp | System-assigned at posting time |
| FILLER | PIC X(20) | Padding | Reserved space | — |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric (16) | Daily transaction identifier | Same structure as TRAN-RECORD |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Validated during CBTRN01C |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | Validated during CBTRN01C |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source | — |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric (100) | Description | — |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | — |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant ID | — |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | — |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | — |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP | — |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | — |
| DALYTRAN-ORIG-TS | PIC X(26) | Timestamp (26) | Origination timestamp | — |
| DALYTRAN-PROC-TS | PIC X(26) | Timestamp (26) | Processing timestamp | — |
| FILLER | PIC X(20) | Padding | Reserved | — |

---

## 6. Transaction Reference Data

### CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric (2) | Transaction type code (key) | Unique identifier |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Transaction type description | e.g., 'Purchase', 'Cash Advance' |
| FILLER | PIC X(08) | Padding | Reserved | — |

### CVTRA04Y.cpy — Transaction Category Record (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code (compound key part 1) | Must exist in TRANTYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code (compound key part 2) | Unique within type |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | e.g., 'Grocery', 'Fuel', 'Travel' |
| FILLER | PIC X(04) | Padding | Reserved | — |

### CVTRA01Y.cpy — Transaction Category Balance Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID (compound key part 1) | Must exist in ACCTFILE |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code (compound key part 2) | — |
| TRANCAT-CD | PIC 9(04) | Numeric (4) | Category code (compound key part 3) | — |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance for this category | Updated by CBTRN02C |
| FILLER | PIC X(22) | Padding | Reserved | — |

### CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Account group identifier (compound key part 1) | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type (compound key part 2) | — |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code (compound key part 3) | — |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest rate percentage | e.g., 19.99% |
| FILLER | PIC X(28) | Padding | Reserved | — |

---

## 7. Security/User Entity

### CSUSR01Y.cpy — Security User Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric (8) | User login ID (key) | Unique identifier |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | Required |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | Required |
| SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password (PLAIN TEXT) | ⚠️ Stored unencrypted — security risk |
| SEC-USR-TYPE | PIC X(01) | Flag (1) | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Padding | Reserved | — |

---

## 8. Authorization Entity (IMS Segments)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Root Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal (11) | Account ID (segment key) | Links to ACCTFILE |
| PA-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID | Links to CUSTFILE |
| PA-AUTH-STATUS | PIC X(01) | Flag (1) | Overall authorization status | Active/Inactive |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Array of flags | Status indicators (5 slots) | — |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Credit limit snapshot | — |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Cash advance limit snapshot | — |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Credit balance at auth time | — |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Cash balance at auth time | — |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of approved authorizations | — |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of declined authorizations | — |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Total approved amount | — |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Total declined amount | — |
| FILLER | PIC X(34) | Padding | Reserved | — |

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Dependent Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|------------------|-----------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal (key) | Authorization date (packed) | Segment key part 1 |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal (key) | Authorization time (packed) | Segment key part 2 |
| PA-AUTH-ORIG-DATE | PIC X(06) | Date (YYMMDD) | Original authorization date | — |
| PA-AUTH-ORIG-TIME | PIC X(06) | Time (HHMMSS) | Original authorization time | — |
| PA-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | Must exist in CARDXREF |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric (4) | Authorization type | e.g., 'AUTH', 'SALE' |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric (4) | Card expiry (YYMM) | — |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric (6) | Message type code | ISO 8583 type |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric (6) | Source system identifier | — |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric (6) | Authorization ID code | Approval code when approved |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric (2) | Response code | '00' = approved (88-level) |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric (4) | Response reason code | Decline reason |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric (6) | ISO processing code | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Requested transaction amount | — |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Approved amount | May differ from requested |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric (4) | Merchant category code (MCC) | ISO 18245 |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric (3) | Acquirer country code | ISO 3166 |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric (2) | Point-of-sale entry mode | e.g., 05=chip, 07=contactless |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant ID | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric (2) | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric (9) | Merchant ZIP | — |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction reference ID | — |
| PA-MATCH-STATUS | PIC X(01) | Flag (1) | Match/settlement status | 'P'=Pending, 'D'=Declined, 'E'=Expired, 'M'=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Flag (1) | Fraud indicator | 'F'=Fraud confirmed, 'R'=Fraud removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Date (YYYYMMDD) | Date fraud was reported | — |
| FILLER | PIC X(17) | Padding | Reserved | — |

---

## 9. Authorization Request/Response (MQ Messages)

### CCPAURQY.cpy — Authorization Request Message

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|------------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Date (YYMMDD) | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Time (HHMMSS) | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | ISO message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Source system |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | ISO processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | MCC |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |

### CCPAURLY.cpy — Authorization Response Message

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|------------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization approval code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code ('00' = approved) |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Decline reason code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### CCPAUERY.cpy — Authorization Error Log

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|------------------|
| ERR-DATE | PIC X(06) | Date | Error date |
| ERR-TIME | PIC X(06) | Time | Error time |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | Program name |
| ERR-LOCATION | PIC X(04) | Alphanumeric | Error location code |
| ERR-LEVEL | PIC X(01) | Flag | Severity: L=Log, I=Info, W=Warning, C=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Flag | Subsystem: A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | Primary error code |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | Secondary error code |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | Error message text |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Event correlation key |

---

## 10. Common/Infrastructure Copybooks

### COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target CICS transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | Flag | 'A' = Admin, 'U' = User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 0 = first entry, 1 = re-entry |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer context |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alphanumeric | Customer name context |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account context |
| CDEMO-ACCT-STATUS | PIC X(01) | Flag | Account status context |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card context |
| CDEMO-LAST-MAP / CDEMO-LAST-MAPSET | PIC X(7) each | Alphanumeric | Last BMS map displayed |

### CODATECN.cpy — Date Conversion Record

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|------------------|
| CODATECN-TYPE | PIC X | Flag | Input format: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date string |
| CODATECN-OUTTYPE | PIC X | Flag | Output format: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Output date string |
| CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Error message if conversion fails |

### CSDAT01Y.cpy — Date/Time Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|------------------|
| WS-CURDATE (YEAR/MONTH/DAY) | PIC 9(04)/9(02)/9(02) | Numeric | Current date components |
| WS-CURTIME (HH/MM/SS/MS) | PIC 9(02) each | Numeric | Current time components |
| WS-CURDATE-MM-DD-YY | PIC 9(02)/X/9(02)/X/9(02) | Formatted | Display format MM/DD/YY |
| WS-CURTIME-HH-MM-SS | PIC 9(02)/X/9(02)/X/9(02) | Formatted | Display format HH:MM:SS |
| WS-TIMESTAMP | Combined | Timestamp | YYYY-MM-DD HH:MM:SS.NNNNNN |

### CSUTLDWY.cpy — Date Validation Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|------------------|
| WS-EDIT-DATE-CCYYMMDD | Composite | Date fields | Date being validated (CC+YY+MM+DD) |
| WS-EDIT-DATE-CC | PIC X(2) / 9(2) | Century | 88-levels: THIS-CENTURY(20), LAST-CENTURY(19) |
| WS-EDIT-DATE-MM | PIC X(2) / 9(2) | Month | 88-levels for valid month, 31-day months, February |
| WS-EDIT-DATE-DD | PIC X(2) / 9(2) | Day | 88-levels for valid day ranges |
| WS-EDIT-DATE-FLGS | Composite flags | Validation result | Flags for year/month/day validity |

### CSLKPCDY.cpy — Lookup Code Repository (1,318 lines)

| Section | Content | Business Meaning |
|---------|---------|------------------|
| VALID-PHONE-AREA-CODE | 88-level VALUES list | North American phone area codes (NANPA) |
| WS-US-STATE-CODES | 88-level VALUES list | 50 US state codes |
| WS-US-STATE-ZIP | Table | State + first 2 digits of ZIP validation |

### COTTL01Y.cpy — Screen Title

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|------------------|
| CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | Logout message |

### CSMSG01Y.cpy — Common Messages

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|------------------|
| CCDA-MSG-THANK-YOU | PIC X(50) | Application exit message |
| CCDA-MSG-INVALID-KEY | PIC X(50) | Invalid key press message |

### CSMSG02Y.cpy — Abend Data

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|------------------|
| ABEND-CODE | PIC X(4) | Abend code |
| ABEND-CULPRIT | PIC X(8) | Program causing abend |
| ABEND-REASON | PIC X(50) | Reason description |
| ABEND-MSG | PIC X(72) | Formatted abend message |

---

## 11. Export/Import Structure

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|------------------|
| EXPORT-REC-TYPE | PIC X(1) | Record type identifier (C=Customer, A=Account, T=Transaction, X=Xref, R=Card) |
| EXPORT-TIMESTAMP | PIC X(26) | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Sequential record number |
| EXPORT-BRANCH-ID | PIC X(4) | Target branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | Target region code |
| EXPORT-RECORD-DATA | PIC X(460) | Record payload (REDEFINES per type) |

---

## 12. Reporting Structure

### CVTRA07Y.cpy — Transaction Report Layout

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|------------------|
| REPT-SHORT-NAME | PIC X(38) | Report code ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | 'Daily Transaction Report' |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Transaction ID column |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Account ID column |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Type code column |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Formatted amount |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Account subtotal |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Grand total |

---

## 13. DB2 Working Storage

### CSDB2RWY.cpy — DB2 Common Working Storage

| Purpose | Content |
|---------|---------|
| WS-DUMMY-DB2-INT | DB2 connectivity test variable |
| WS-DISP-SQLCODE | Display-format SQLCODE |
| WS-DB2-CURRENT-ACTION | Current DB2 action description |
| WS-DSNTIAC-* | DSNTIAC utility message formatting fields |
| WS-LONG-MSG / WS-RETURN-MSG | Error message assembly areas |

### CSDB2RPY.cpy — DB2 Common Procedures

| Paragraph | Purpose |
|-----------|---------|
| 9998-PRIMING-QUERY | Verify DB2 connectivity (SELECT 1 FROM SYSIBM.SYSDUMMY1) |
| 9999-FORMAT-DB2-MESSAGE | Format DB2 error messages via DSNTIAC |

---

## 14. IMS PCB Definitions

### PAUTBPCB.CPY — Primary IMS PCB

| Field | Purpose |
|-------|---------|
| PAUT-DBDNAME | Database name |
| PAUT-SEG-LEVEL | Segment level |
| PAUT-PCB-STATUS | DL/I status code |
| PAUT-PCB-PROCOPT | Processing options |
| PAUT-SEG-NAME | Current segment name |
| PAUT-KEYFB | Key feedback area (255 bytes) |

### PADFLPCB.CPY / PASFLPCB.CPY — Alternate PCBs

Same structure as PAUTBPCB for detail (PADFL) and summary (PASFL) segments with varying key feedback lengths.

### IMSFUNCS.cpy — IMS Function Codes

| Constant | Value | Meaning |
|----------|-------|---------|
| FUNC-GU | 'GU  ' | Get Unique |
| FUNC-GHU | 'GHU ' | Get Hold Unique |
| FUNC-GN | 'GN  ' | Get Next |
| FUNC-GHN | 'GHN ' | Get Hold Next |
| FUNC-GNP | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | 'REPL' | Replace |
| FUNC-ISRT | 'ISRT' | Insert |
| FUNC-DLET | 'DLET' | Delete |

---

## 15. Entity Relationship Summary

```
Customer (CVCUS01Y, 500 bytes)
    │
    ├──1:N──► Card-XREF (CVACT03Y, 50 bytes) ──N:1──► Account (CVACT01Y, 300 bytes)
    │                                                       │
    │                                                       ├── ACCT-GROUP-ID ──► Disclosure Group (CVTRA02Y)
    │                                                       │
    │                                                       └── Category Balances (CVTRA01Y, 50 bytes)
    │
    └──────► Card (CVACT02Y, 150 bytes)
                  │
                  └──► Transaction (CVTRA05Y, 350 bytes)
                            │
                            ├──► Transaction Type (CVTRA03Y, 60 bytes)
                            └──► Transaction Category (CVTRA04Y, 60 bytes)

Security User (CSUSR01Y, 80 bytes) — Independent entity

Pending Authorization:
    IMS Root: Summary (CIPAUSMY) ──1:N──► Detail (CIPAUDTY)
    MQ: Request (CCPAURQY) ──► Response (CCPAURLY)
    Error: Log (CCPAUERY)
```
