# Data Dictionary — CardDemo COBOL Estate

> Field-level catalog of every copybook in the CardDemo application, grouped by business entity.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric | Unique account identifier (11-digit) | Primary key; must be numeric |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Account active/inactive flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance | Signed; allows negative (overlimit) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Maximum credit limit | Must be positive; validated against balance |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | Subset of total credit limit |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Account opening date | CCYYMMDD or CCYY-MM-DD format; validated via CSUTLDPY |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | CCYYMMDD format; must be >= open date |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Date account was last reissued | CCYYMMDD format |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total | Running total of credits in current billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total | Running total of debits in current billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | Validated against US ZIP+4 format |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/interest rate group identifier | Links to DIS-GROUP-RECORD |
| FILLER | PIC X(178) | Filler | Reserved space | Pads record to 300 bytes |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric | Unique customer identifier (9-digit) | Primary key; must be numeric |
| CUST-FIRST-NAME | PIC X(25) | Alpha | Customer first name | Required; non-blank |
| CUST-MIDDLE-NAME | PIC X(25) | Alpha | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alpha | Customer last name | Required; non-blank |
| CUST-ADDR-LINE-1 | PIC X(50) | Alpha | Address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alpha | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alpha | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code | Validated against 50 US state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code | 3-char country identifier |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | Validated against state-ZIP prefix table in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Same validation as primary |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit; validated format (XXX-XX-XXXX pattern) |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID (driver license, etc.) | Optional |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | CCYY-MM-DD format; validated via CSUTLDPY date routines |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account for payments | Optional; used for bill pay |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary cardholder indicator | 'Y'/'N' flag |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Filler | Reserved space | Pads record to 500 bytes |

### CUSTREC.cpy — Customer Record (Alternate Layout, RECLN 500)

Identical field layout to CVCUS01Y.cpy but with different indentation. Used by CBSTM03A (statement generation). Field `CUST-DOB-YYYYMMDD` uses compact format (no hyphens).

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alpha | User login ID | Primary key; 8-char max |
| SEC-USR-FNAME | PIC X(20) | Alpha | User first name | Display only |
| SEC-USR-LNAME | PIC X(20) | Alpha | User last name | Display only |
| SEC-USR-PWD | PIC X(08) | Alpha | User password | 8-char; stored in plain text |
| SEC-USR-TYPE | PIC X(01) | Alpha | User type (admin/regular) | 'A' = admin, 'U' = regular user |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved | Pads record to 80 bytes |

### UNUSED1Y.cpy — Unused Data Record (RECLN 80)

Identical layout to CSUSR01Y.cpy with field names prefixed `UNUSED-`. Placeholder copybook, not actively referenced by any program.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Credit card number (16-digit) | Primary key; Luhn check implied |
| CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | Foreign key → ACCT-ID |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit CVV code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alpha | Name embossed on card | Required for card updates |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | Month/year validated via EDIT-EXPIRY-MON/YEAR |
| CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card active/inactive flag | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Filler | Reserved | Pads record to 150 bytes |

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Primary key; links card to customer and account |
| XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID | Foreign key → CUST-ID |
| XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID | Foreign key → ACCT-ID |
| FILLER | PIC X(14) | Filler | Reserved | Pads record to 50 bytes |

### CVCRD01Y.cpy — Card Work Areas (Screen Processing)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CCARD-AID | PIC X(5) | Alpha | Current AID key pressed | 88-level: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alpha | Next program to transfer to | Used for XCTL navigation |
| CCARD-NEXT-MAPSET | PIC X(7) | Alpha | Next BMS mapset name | Screen navigation |
| CCARD-NEXT-MAP | PIC X(7) | Alpha | Next BMS map name | Screen navigation |
| CCARD-ERROR-MSG | PIC X(75) | Alpha | Error message for screen display | Populated on validation failure |
| CCARD-RETURN-MSG | PIC X(75) | Alpha | Return/info message | 88: CCARD-RETURN-MSG-OFF = LOW-VALUES |
| CC-ACCT-ID | PIC X(11) / 9(11) | Alphanumeric | Account ID work field | REDEFINES for numeric access |
| CC-CARD-NUM | PIC X(16) / 9(16) | Alphanumeric | Card number work field | REDEFINES for numeric access |
| CC-CUST-ID | PIC X(09) / 9(9) | Alphanumeric | Customer ID work field | REDEFINES for numeric access |

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | System-generated; primary key |
| TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Foreign key → TRAN-TYPE; e.g. 'SA' (sale), 'CR' (credit) |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Foreign key → TRAN-CAT-CD |
| TRAN-SOURCE | PIC X(10) | Alpha | Transaction source/channel | E.g., 'POS', 'ONLINE', 'ATM' |
| TRAN-DESC | PIC X(100) | Alpha | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Signed; positive = charge, negative = credit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | Links to merchant master |
| TRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name | Display field |
| TRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | Display field |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | Display field |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used | Foreign key → CARD-NUM |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Transaction origination timestamp | ISO timestamp format |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | Transaction processing timestamp | Set when posted to master |
| FILLER | PIC X(20) | Filler | Reserved | Pads record to 350 bytes |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Identical layout to CVTRA05Y.cpy with `DALYTRAN-` prefix. Used for daily transaction input file processing. Same record length (350 bytes).

### COSTM01.CPY — Transaction Record for Statement Reporting

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRNX-KEY (group) | — | Group | Composite key for statement sorting | |
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Part of key |
| TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID | Part of key |
| TRNX-TYPE-CD | PIC X(02) | Alpha | Transaction type | Same as TRAN-TYPE-CD |
| TRNX-CAT-CD | PIC 9(04) | Numeric | Transaction category | Same as TRAN-CAT-CD |
| TRNX-SOURCE | PIC X(10) | Alpha | Transaction source | Same as TRAN-SOURCE |
| TRNX-DESC | PIC X(100) | Alpha | Description | Same as TRAN-DESC |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal | Amount | Same as TRAN-AMT |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | Same as TRAN-MERCHANT-ID |
| TRNX-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name | Same as TRAN-MERCHANT-NAME |
| TRNX-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | Same as TRAN-MERCHANT-CITY |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | Same as TRAN-MERCHANT-ZIP |
| TRNX-ORIG-TS | PIC X(26) | Alphanumeric | Origination timestamp | Same as TRAN-ORIG-TS |
| TRNX-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | Same as TRAN-PROC-TS |
| FILLER | PIC X(20) | Filler | Reserved | Pad |

### CVTRA01Y.cpy — Transaction Category Balance Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY (group) | — | Group | Composite key (account + type + category) | |
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account identifier | Foreign key → ACCT-ID |
| TRANCAT-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Foreign key → TRAN-TYPE |
| TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code | Foreign key → TRAN-CAT-CD |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance for this category | Updated during transaction posting |
| FILLER | PIC X(22) | Filler | Reserved | Pads to 50 bytes |

### CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| DIS-GROUP-KEY (group) | — | Group | Composite key (group + type + category) | |
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group identifier | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate for this group/type/category | Percentage (e.g. 18.99) |
| FILLER | PIC X(28) | Filler | Reserved | Pads to 50 bytes |

### CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alpha | Transaction type code | Primary key; e.g. 'SA', 'CR', 'FE' |
| TRAN-TYPE-DESC | PIC X(50) | Alpha | Type description | Human-readable description |
| FILLER | PIC X(08) | Filler | Reserved | Pads to 60 bytes |

### CVTRA04Y.cpy — Transaction Category Record (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY (group) | — | Group | Composite key (type + category) | |
| TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Foreign key → TRAN-TYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Part of key |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alpha | Category description | Human-readable description |
| FILLER | PIC X(04) | Filler | Reserved | Pads to 60 bytes |

### CVTRA07Y.cpy — Transaction Report Structures

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alpha | Report identifier ('DALYREPT') | Constant |
| REPT-LONG-NAME | PIC X(41) | Alpha | Report title | Constant |
| REPT-START-DATE | PIC X(10) | Alpha | Report start date | User-supplied parameter |
| REPT-END-DATE | PIC X(10) | Alpha | Report end date | User-supplied parameter |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alpha | Transaction ID in report | Display field |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alpha | Account ID in report | Display field |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alpha | Type code in report | Display field |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alpha | Type description | Looked up from TRANTYPE |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code | Display field |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alpha | Category description | Looked up from TRANCATG |
| TRAN-REPORT-SOURCE | PIC X(10) | Alpha | Transaction source | Display field |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount | Report display |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal | Running total per page |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal | Running total per account |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total | Sum of all transactions |

---

## 5. Export/Import Entity

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

**Header fields** (common to all record types):

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alpha | Record type indicator | 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp | ISO format; REDEFINES to DATE + TIME |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Packed numeric | Sequence number within export | Auto-incremented |
| EXPORT-BRANCH-ID | PIC X(4) | Alpha | Branch identifier for migration | Identifies source branch |
| EXPORT-REGION-CODE | PIC X(5) | Alpha | Region code | Geographic region |
| EXPORT-RECORD-DATA | PIC X(460) | Group/Redefines | Payload area | REDEFINES for each record type |

**Payload variants** (via REDEFINES on EXPORT-RECORD-DATA):
- **EXPORT-CUSTOMER-DATA**: EXP-CUST-ID (COMP), name, address, phone, SSN, DOB, FICO (COMP-3)
- **EXPORT-ACCOUNT-DATA**: EXP-ACCT-ID, status, balances (COMP-3/COMP), dates, ZIP, group
- **EXPORT-TRANSACTION-DATA**: EXP-TRAN-ID, type, amount (COMP-3), merchant, timestamps
- **EXPORT-CARD-XREF-DATA**: EXP-XREF-CARD-NUM, CUST-ID, ACCT-ID (COMP)
- **EXPORT-CARD-DATA**: EXP-CARD-NUM, ACCT-ID (COMP), CVV (COMP), name, expiry, status

---

## 6. Authorization Entity (Sub-App: `app-authorization-ims-db2-mq`)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Root Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account identifier | IMS root segment key |
| PA-CUST-ID | PIC 9(09) | Numeric | Customer ID | Foreign key → CUST-ID |
| PA-AUTH-STATUS | PIC X(01) | Alpha | Authorization status | Active/inactive |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alpha array | Account status codes (5 slots) | Multi-value status |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Credit limit | Same as account |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash advance limit | |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Current credit balance | |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Current cash balance | |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of approved authorizations | Running counter |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Count of declined authorizations | Running counter |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount | Running total |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount | Running total |
| FILLER | PIC X(34) | Filler | Reserved | |

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Child Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (numeric) | Part of segment key |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (numeric) | Part of segment key |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alpha | Original auth date (display) | YYMMDD format |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alpha | Original auth time (display) | HHMMSS format |
| PA-CARD-NUM | PIC X(16) | Alpha | Card number | Foreign key → CARD-NUM |
| PA-AUTH-TYPE | PIC X(04) | Alpha | Authorization type | E.g., 'SALE', 'CASH' |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry (MMYY) | Validated against card record |
| PA-MESSAGE-TYPE | PIC X(06) | Alpha | Message type code | Protocol-specific |
| PA-MESSAGE-SOURCE | PIC X(06) | Alpha | Message source system | Originating system |
| PA-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization ID code | Assigned on approval |
| PA-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code | 88: '00' = approved |
| PA-AUTH-RESP-REASON | PIC X(04) | Alpha | Response reason code | Detail code for decline |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | ISO 8583 processing code |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Requested transaction amount | |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount | May differ from requested |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | Merchant category code (MCC) | ISO 18245 MCC |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country code | ISO 3166 |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | Chip/swipe/manual |
| PA-MERCHANT-ID | PIC X(15) | Alpha | Merchant identifier | |
| PA-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name | |
| PA-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city | |
| PA-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state | |
| PA-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP | |
| PA-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID | Unique per auth |
| PA-MATCH-STATUS | PIC X(01) | Alpha | Auth/transaction match status | 88: 'P'=Pending, 'D'=Declined, 'E'=Expired, 'M'=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alpha | Fraud indicator | 88: 'F'=Fraud confirmed, 'R'=Fraud removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alpha | Fraud report date | CCYYMMDD; set by COPAUS2C |
| FILLER | PIC X(17) | Filler | Reserved | |

### CCPAURQY.cpy — Authorization Request Message (MQ)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-RQ-CARD-NUM | PIC X(16) | Alpha | Card number | Incoming auth request |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry | MMYY |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alpha | Authorization type | |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | ISO 8583 |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Requested amount | |
| PA-RQ-MERCHANT-CATAGORY-CD | PIC X(04) | Alpha | MCC code | |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country | |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID | |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name | |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city | |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state | |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP | |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID | |

### CCPAURLY.cpy — Authorization Response Message (MQ)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alpha | Card number | Echo from request |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID | Echo from request |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization ID | Assigned on approval |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code | '00' = approved |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alpha | Reason code | Detail for decline |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount | May differ from request |

### CCPAUERY.cpy — Authorization Error Log Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| ERR-DATE | PIC X(06) | Alpha | Error date | YYMMDD |
| ERR-TIME | PIC X(06) | Alpha | Error time | HHMMSS |
| ERR-APPLICATION | PIC X(08) | Alpha | Application name | 'CARDDEMO' |
| ERR-PROGRAM | PIC X(08) | Alpha | Program name | Originating program |
| ERR-LOCATION | PIC X(04) | Alpha | Code location | Paragraph/section ID |
| ERR-LEVEL | PIC X(01) | Alpha | Severity level | 88: 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alpha | Subsystem that failed | 88: 'A'=App, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=File |
| ERR-CODE-1 | PIC X(09) | Alpha | Primary error code | System-specific (EIBRESP, SQLCODE, etc.) |
| ERR-CODE-2 | PIC X(09) | Alpha | Secondary error code | Additional detail |
| ERR-MESSAGE | PIC X(50) | Alpha | Error message text | Human-readable |
| ERR-EVENT-KEY | PIC X(20) | Alpha | Key of record being processed | For troubleshooting |

---

## 7. IMS Infrastructure Copybooks (Sub-App: `app-authorization-ims-db2-mq`)

### IMSFUNCS.cpy — IMS DL/I Function Codes

| Field Name | PIC Clause | Value | Business Meaning |
|-----------|-----------|-------|-----------------|
| FUNC-GU | PIC X(04) | 'GU  ' | Get Unique (direct read) |
| FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique (read for update) |
| FUNC-GN | PIC X(04) | 'GN  ' | Get Next (sequential read) |
| FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next |
| FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | PIC X(04) | 'REPL' | Replace (update in place) |
| FUNC-ISRT | PIC X(04) | 'ISRT' | Insert segment |
| FUNC-DLET | PIC X(04) | 'DLET' | Delete segment |
| PARMCOUNT | PIC S9(05) COMP-5 | +4 | DL/I call parameter count |

### PAUTBPCB.CPY / PASFLPCB.CPY / PADFLPCB.CPY — IMS PCB Masks

Standard IMS PCB (Program Communication Block) layouts for PAUT, PASFL, and PADFL databases:

| Field Name | PIC Clause | Business Meaning |
|-----------|-----------|-----------------|
| *-DBDNAME | PIC X(08) | Database name |
| *-SEG-LEVEL | PIC X(02) | Segment level indicator |
| *-PCB-STATUS | PIC X(02) | Status code from last DL/I call |
| *-PCB-PROCOPT | PIC X(04) | Processing options |
| *-SEG-NAME | PIC X(08) | Segment name from last call |
| *-KEYFB-NAME | PIC S9(05) COMP | Key feedback length |
| *-NUM-SENSEGS | PIC S9(05) COMP | Number of sensitive segments |
| *-KEYFB | PIC X(100–255) | Key feedback area |

---

## 8. DB2 Copybooks (Sub-App: `app-transaction-type-db2`)

### CSDB2RPY.cpy — DB2 Common Procedures

Contains the `9998-PRIMING-QUERY` paragraph that validates DB2 connectivity with `SELECT 1 FROM SYSIBM.SYSDUMMY1`.

### CSDB2RWY.cpy — DB2 Working Storage

Working storage variables for DB2 processing (SQLCODE display fields, return codes, etc.).

---

## 9. Application Framework Copybooks

### COCOM01Y.cpy — Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alpha | Source CICS transaction ID | Navigation tracking |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alpha | Source program name | Navigation tracking |
| CDEMO-TO-TRANID | PIC X(04) | Alpha | Target CICS transaction ID | Navigation tracking |
| CDEMO-TO-PROGRAM | PIC X(08) | Alpha | Target program name | Navigation tracking |
| CDEMO-USER-ID | PIC X(08) | Alpha | Logged-in user ID | Set at sign-on |
| CDEMO-USER-TYPE | PIC X(01) | Alpha | User type | 88: 'A'=Admin, 'U'=User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program re-entry context | 88: 0=Enter, 1=Reenter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer context | Set during navigation |
| CDEMO-CUST-FNAME | PIC X(25) | Alpha | Customer first name | Display cache |
| CDEMO-CUST-MNAME | PIC X(25) | Alpha | Customer middle name | Display cache |
| CDEMO-CUST-LNAME | PIC X(25) | Alpha | Customer last name | Display cache |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account context | Set during navigation |
| CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Account status | Cached from account record |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card context | Set during navigation |
| CDEMO-LAST-MAP | PIC X(7) | Alpha | Last BMS map displayed | Navigation history |
| CDEMO-LAST-MAPSET | PIC X(7) | Alpha | Last BMS mapset used | Navigation history |

### COADM02Y.cpy — Admin Menu Options

Defines 6 admin menu options: User List, User Add, User Update, User Delete, Transaction Type List (DB2), Transaction Type Maintenance (DB2). Maps option numbers to program names (COUSR00C–COUSR03C, COTRTLIC, COTRTUPC).

### COMEN02Y.cpy — Main Menu Options

Defines 11 main menu options: Account View, Account Update, Credit Card List/View/Update, Transaction List/View/Add, Transaction Reports, Bill Payment, Pending Authorization View. Maps to program names with user-type access ('U' = all users).

### COTTL01Y.cpy — Screen Title Constants

| Field Name | PIC Clause | Value | Business Meaning |
|-----------|-----------|-------|-----------------|
| CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' | Screen header line 1 |
| CCDA-TITLE02 | PIC X(40) | 'CardDemo' | Screen header line 2 |
| CCDA-THANK-YOU | PIC X(40) | 'Thank you for using CCDA...' | Sign-off message |

### CSDAT01Y.cpy — Date/Time Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|-----------------|
| WS-CURDATE (group) | — | Group | Current date: YEAR(4)+MONTH(2)+DAY(2) |
| WS-CURTIME (group) | — | Group | Current time: HH+MM+SS+MS |
| WS-CURDATE-MM-DD-YY | — | Group | Formatted date: MM/DD/YY |
| WS-CURTIME-HH-MM-SS | — | Group | Formatted time: HH:MM:SS |
| WS-TIMESTAMP | — | Group | Full timestamp: YYYY-MM-DD HH:MM:SS.MMMMMM |

### CSMSG01Y.cpy / CSMSG02Y.cpy — Message Working Storage

Standard message display areas and error message formatting fields used across all CICS programs.

### CSLKPCDY.cpy — Lookup Code Repository

Contains comprehensive validation tables:
- **North American phone area codes**: 600+ valid NANPA area codes (88-level VALID-PHONE-AREA-CODE)
- **US state codes**: All 50 states + territories
- **State-ZIP prefix mapping**: First 2 digits of ZIP validated against state

### CSSETATY.cpy — Screen Attribute Setting (COPY REPLACING)

Template paragraph that sets BMS field color to red and marks with '*' when validation fails. Used with COPY REPLACING to generate field-specific validation display logic.

### CSSTRPFY.cpy — PFKey Store Procedure

Maps CICS AID bytes (EIBAID) to application-level PFKey flags (CCARD-AID-ENTER, CCARD-AID-PFK01–PFK12). Handles PF1–PF24 with PF13–PF24 aliased to PF1–PF12.

### CSUTLDPY.cpy — Date Validation Procedures (Procedure Division)

Reusable date validation paragraphs:
- **EDIT-DATE-CCYYMMDD**: Master date validator
- **EDIT-YEAR-CCYY**: Year validation (1900–2099)
- **EDIT-MONTH**: Month validation (1–12)
- **EDIT-DAY**: Day validation (1–31)
- **EDIT-DAY-MONTH-YEAR**: Cross-field validation (leap year, 30/31-day months)

### CSUTLDWY.cpy — Date Validation Working Storage

Working storage fields used by CSUTLDPY date validation paragraphs.

### CODATECN.cpy — Date Conversion Record

| Field Name | PIC Clause | Data Type | Business Meaning |
|-----------|-----------|-----------|-----------------|
| CODATECN-TYPE | PIC X | Alpha | Input format: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Alpha | Input date string |
| CODATECN-OUTTYPE | PIC X | Alpha | Output format: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| CODATECN-0UT-DATE | PIC X(20) | Alpha | Output date string |
| CODATECN-ERROR-MSG | PIC X(38) | Alpha | Conversion error message |
