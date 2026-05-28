# DATA DICTIONARY — CardDemo COBOL Copybooks

> Comprehensive field-level catalog of all copybooks in the CardDemo estate.
> Total: **59 copybooks** across `app/cpy/`, `app/cpy-bms/`, and sub-application directories.

---

## 1. ACCOUNT Entity

### 1.1 CVACT01Y.cpy — Account Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Unique account identifier | Primary key in ACCTDATA VSAM KSDS |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Account active/inactive flag | Expected values: 'Y'/'N' |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (10.2) | Current account balance | Signed — can be negative (overdrawn) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (10.2) | Maximum credit limit | |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (10.2) | Maximum cash advance limit | |
| ACCT-OPEN-DATE | PIC X(10) | Date string (10) | Account opening date | Format: YYYY-MM-DD |
| ACCT-EXPIRAION-DATE | PIC X(10) | Date string (10) | Account expiration date | Note: misspelling of "EXPIRATION" in source |
| ACCT-REISSUE-DATE | PIC X(10) | Date string (10) | Date account was last reissued | |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (10.2) | Current billing cycle credit total | |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (10.2) | Current billing cycle debit total | |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account holder ZIP/postal code | |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure/interest group identifier | Links to DIS-GROUP-RECORD |
| FILLER | PIC X(178) | Filler | Reserved/padding to 300 bytes | |

### 1.2 CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (primary key) | Links CARD → CUSTOMER → ACCOUNT |
| XREF-CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer identifier | FK to CUSTOMER-RECORD |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account identifier | FK to ACCOUNT-RECORD |
| FILLER | PIC X(14) | Filler | Reserved/padding to 50 bytes | |

---

## 2. CUSTOMER Entity

### 2.1 CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| CUST-ID | PIC 9(09) | Numeric (9 digits) | Unique customer identifier | Primary key in CUSTDATA VSAM KSDS |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Address line 1 | |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Address line 2 | |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Address line 3 | |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric (2) | US state code | Validated against CSLKPCDY state code list |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric (3) | Country code | |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | ZIP/postal code | Validated against CSLKPCDY state+zip list |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone number | Area code validated against CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone number | Area code validated against CSLKPCDY |
| CUST-SSN | PIC 9(09) | Numeric (9 digits) | Social Security Number | Validated by 1265-EDIT-US-SSN in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID (passport, etc.) | |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Date string (10) | Date of birth | Format: YYYY-MM-DD |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | Electronic Funds Transfer account | |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric (1) | Primary cardholder indicator | 'Y' = primary holder |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3 digits) | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | Filler | Reserved/padding to 500 bytes | |

### 2.2 CUSTREC.cpy — Customer Record (Statement variant, RECLN 500)

Identical structure to CVCUS01Y.cpy except `CUST-DOB-YYYYMMDD` (no hyphens in name). Used by CBSTM03A for statement generation. Same fields and PICs as above.

---

## 3. CARD Entity

### 3.1 CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number | Primary key in CARDDATA VSAM KSDS |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Associated account ID | FK to ACCOUNT-RECORD |
| CARD-CVV-CD | PIC 9(03) | Numeric (3 digits) | Card verification value (CVV) | |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name printed on card | |
| CARD-EXPIRAION-DATE | PIC X(10) | Date string (10) | Card expiration date | Note: misspelling; validated in COCRDUPC |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Card active/inactive flag | 'Y'/'N' |
| FILLER | PIC X(59) | Filler | Reserved/padding to 150 bytes | |

### 3.2 CVCRD01Y.cpy — Card Work Areas (CICS screens)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| CCARD-AID | PIC X(5) | Alphanumeric (5) | Attention identifier (key pressed) | 88-level: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric (8) | Next program to transfer control to | Used in XCTL navigation |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric (7) | Next BMS mapset name | |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric (7) | Next BMS map name | |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric (75) | Error message display area | |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric (75) | Return/status message | 88 CCARD-RETURN-MSG-OFF = LOW-VALUES |
| CC-ACCT-ID | PIC X(11) / 9(11) | Alphanumeric/Numeric | Account ID with REDEFINES | Dual-format: alpha for display, numeric for I/O |
| CC-CARD-NUM | PIC X(16) / 9(16) | Alphanumeric/Numeric | Card number with REDEFINES | Dual-format |
| CC-CUST-ID | PIC X(09) / 9(9) | Alphanumeric/Numeric | Customer ID with REDEFINES | Dual-format |

---

## 4. TRANSACTION Entity

### 4.1 CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Unique transaction identifier | Primary key in TRANSACT VSAM KSDS |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK to TRAN-TYPE-RECORD |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | FK to TRAN-CAT-RECORD |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction origination source | E.g., "POS", "ATM", "ONLINE" |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | Free-text merchant/transaction details |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (9.2) | Transaction amount | Signed — negative for credits |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant identifier | |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number used | FK to CARD-RECORD via XREF |
| TRAN-ORIG-TS | PIC X(26) | Timestamp string (26) | Transaction origination timestamp | Format: YYYY-MM-DD-HH.MM.SS.FFFFFF |
| TRAN-PROC-TS | PIC X(26) | Timestamp string (26) | Transaction processing timestamp | |
| FILLER | PIC X(20) | Filler | Reserved/padding to 350 bytes | |

### 4.2 CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same structure as CVTRA05Y but with `DALYTRAN-` prefix. Fields: DALYTRAN-ID, DALYTRAN-TYPE-CD, DALYTRAN-CAT-CD, DALYTRAN-SOURCE, DALYTRAN-DESC, DALYTRAN-AMT, DALYTRAN-MERCHANT-ID, DALYTRAN-MERCHANT-NAME, DALYTRAN-MERCHANT-CITY, DALYTRAN-MERCHANT-ZIP, DALYTRAN-CARD-NUM, DALYTRAN-ORIG-TS, DALYTRAN-PROC-TS. Used for incoming daily transaction batches before posting to master.

### 4.3 COSTM01.CPY — Transaction Statement Layout (for reporting)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (part of composite key) | Composite key with TRNX-ID |
| TRNX-ID | PIC X(16) | Alphanumeric (16) | Transaction ID (part of composite key) | |
| TRNX-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | |
| TRNX-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | |
| TRNX-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source | |
| TRNX-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal (9.2) | Transaction amount | |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant ID | |
| TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | |
| TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP | |
| TRNX-ORIG-TS | PIC X(26) | Timestamp (26) | Origination timestamp | |
| TRNX-PROC-TS | PIC X(26) | Timestamp (26) | Processing timestamp | |

### 4.4 CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | Part of composite key |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Part of composite key |
| TRANCAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | Part of composite key |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (9.2) | Running balance for this category | Updated by CBACT04C (interest) and CBTRN02C (posting) |
| FILLER | PIC X(22) | Filler | Padding to 50 bytes | |

### 4.5 CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Account group identifier | Part of composite key |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Part of composite key |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | Part of composite key |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (4.2) | Interest rate for this group/type/category | Used by CBACT04C for interest calculation |
| FILLER | PIC X(28) | Filler | Padding to 50 bytes | |

### 4.6 CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric (2) | Transaction type code | Primary key in TRANTYPE VSAM KSDS |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Transaction type description | E.g., "PURCHASE", "PAYMENT", "CASH ADVANCE" |
| FILLER | PIC X(08) | Filler | Padding to 60 bytes | |

### 4.7 CVTRA04Y.cpy — Transaction Category Record (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Part of composite key |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | Part of composite key |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | E.g., "Retail Purchase", "Cash Withdrawal" |
| FILLER | PIC X(04) | Filler | Padding to 60 bytes | |

### 4.8 CVTRA07Y.cpy — Transaction Report Structures

| Structure | Fields | Purpose |
|-----------|--------|---------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME (X(38)), REPT-LONG-NAME (X(41)), REPT-DATE-HEADER (X(12)), REPT-START-DATE (X(10)), REPT-END-DATE (X(10)) | Report title and date range header |
| TRANSACTION-DETAIL-REPORT | TRAN-REPORT-TRANS-ID (X(16)), TRAN-REPORT-ACCOUNT-ID (X(11)), TRAN-REPORT-TYPE-CD (X(02)), TRAN-REPORT-TYPE-DESC (X(15)), TRAN-REPORT-CAT-CD (9(04)), TRAN-REPORT-CAT-DESC (X(29)), TRAN-REPORT-SOURCE (X(10)), TRAN-REPORT-AMT (-ZZZ,ZZZ,ZZZ.ZZ) | Detail line for each transaction |
| REPORT-PAGE-TOTALS | REPT-PAGE-TOTAL (+ZZZ,ZZZ,ZZZ.ZZ) | Page subtotal |
| REPORT-ACCOUNT-TOTALS | REPT-ACCOUNT-TOTAL (+ZZZ,ZZZ,ZZZ.ZZ) | Account subtotal |
| REPORT-GRAND-TOTALS | REPT-GRAND-TOTAL (+ZZZ,ZZZ,ZZZ.ZZ) | Grand total for report |

---

## 5. AUTHORIZATION Entity (Sub-Application: IMS/DB2/MQ)

### 5.1 CIPAUSMY.cpy — Pending Authorization Summary (IMS Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal (11) | Account identifier | IMS root segment key |
| PA-CUST-ID | PIC 9(09) | Numeric (9) | Customer identifier | |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric (1) | Overall authorization status | |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alphanumeric array (2×5) | Account status indicators (multi-valued) | |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Credit limit | |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Cash advance limit | |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Current credit balance | |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Current cash balance | |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of approved authorizations | |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of declined authorizations | |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Total approved amount | |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Total declined amount | |
| FILLER | PIC X(34) | Filler | Reserved | |

### 5.2 CIPAUDTY.cpy — Pending Authorization Detail (IMS Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (compressed) | Part of composite key |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (compressed) | Part of composite key |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric (6) | Original auth date (display format) | YYMMDD |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric (6) | Original auth time (display format) | HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric (4) | Authorization type code | |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric (4) | Card expiry (YYMM) | |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric (6) | Message type identifier | |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric (6) | Message origination source | |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric (6) | Authorization ID code | |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric (2) | Authorization response code | 88 PA-AUTH-APPROVED = '00' |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric (4) | Response reason code | |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric (6) | Processing code | |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (10.2) | Requested transaction amount | |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (10.2) | Approved amount | |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric (4) | Merchant category code (MCC) | Note: misspelling of "CATEGORY" |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric (3) | Acquirer country code | |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric (2) | Point-of-sale entry mode | |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant identifier | |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name | |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city | |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric (2) | Merchant state | |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric (9) | Merchant ZIP | |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction identifier | |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric (1) | Authorization match status | 88: P=Pending, D=Declined, E=Expired, M=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric (1) | Fraud indicator | 88: F=Confirmed, R=Removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Date string (8) | Fraud report date | YYYYMMDD |
| FILLER | PIC X(17) | Filler | Reserved | |

### 5.3 CCPAURQY.cpy — Authorization Request (MQ Message)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Date (6) | Request authorization date |
| PA-RQ-AUTH-TIME | PIC X(06) | Time (6) | Request authorization time |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric (4) | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Date (4) | Card expiry YYMM |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric (6) | Message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric (6) | Message source |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric (6) | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Requested amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric (4) | Merchant category |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric (3) | Acquirer country |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric (2) | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric (2) | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric (9) | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction ID |

### 5.4 CCPAURLY.cpy — Authorization Response (MQ Message)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric (6) | Authorization ID code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric (2) | Response code (00=approved) |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric (4) | Response reason code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### 5.5 IMS PCB Copybooks (PADFLPCB, PASFLPCB, PAUTBPCB)

Each defines a Program Communication Block for IMS DL/I access:

| Field Name | PIC Clause | Purpose |
|------------|-----------|---------|
| PCB-DBD-NAME | PIC X(08) | Database description name |
| PCB-SEG-LEVEL | PIC XX | Segment level indicator |
| PCB-STATUS-CODE | PIC XX | DL/I status code (spaces=success, GE=not found) |
| PCB-PROC-OPTIONS | PIC X(04) | Processing options |
| PCB-RESERVE-DLI | PIC S9(05) COMP | Reserved |
| PCB-SEG-NAME-FB | PIC X(08) | Segment name feedback |
| PCB-LENGTH-FB-KEY | PIC S9(05) COMP | Length of feedback key |
| PCB-NUMB-SENS-SEGS | PIC S9(05) COMP | Number of sensitive segments |
| PCB-KEY-FB-AREA | PIC X(30) | Key feedback area |

### 5.6 IMSFUNCS.cpy — IMS Function Codes

Defines constants for IMS DL/I operations: GU (Get Unique), GN (Get Next), GNP (Get Next within Parent), ISRT (Insert), DLET (Delete), REPL (Replace), CHKP (Checkpoint), etc.

---

## 6. USER SECURITY Entity

### 6.1 CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric (8) | User login ID | Primary key in USRSEC VSAM KSDS |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | |
| SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password | Stored in plaintext |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric (1) | User type | 'A'=Admin, 'U'=Regular user |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved/padding to 80 bytes | |

---

## 7. EXPORT / MIGRATION Entity

### 7.1 CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

**Header fields (common to all record types):**

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric (1) | Record type: C=Customer, A=Account, X=Xref, T=Transaction, D=Card |
| EXPORT-TIMESTAMP | PIC X(26) | Timestamp (26) | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary (fullword) | Sequence number within export |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric (4) | Branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric (5) | Region code |

**REDEFINES for each entity type** (within EXPORT-RECORD-DATA, 460 bytes):
- **EXPORT-CUSTOMER-DATA**: Mirrors CVCUS01Y with COMP/COMP-3 optimization (EXP-CUST-ID as COMP, EXP-CUST-FICO-CREDIT-SCORE as COMP-3)
- **EXPORT-ACCOUNT-DATA**: Mirrors CVACT01Y with COMP-3 for balances (EXP-ACCT-CURR-BAL, EXP-ACCT-CASH-CREDIT-LIMIT as COMP-3, EXP-ACCT-CURR-CYC-DEBIT as COMP)
- **EXPORT-TRANSACTION-DATA**: Mirrors CVTRA05Y with COMP-3 for amounts
- **EXPORT-XREF-DATA**: Mirrors CVACT03Y
- **EXPORT-CARD-DATA**: Mirrors CVACT02Y

---

## 8. APPLICATION INFRASTRUCTURE Copybooks

### 8.1 COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|-----------------|-------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric (4) | Originating CICS transaction ID | |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric (8) | Originating program name | |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric (4) | Target CICS transaction ID | |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric (8) | Target program name | |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric (8) | Current user ID | |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric (1) | User type | 88: A=Admin, U=User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric (1) | Program context flag | 88: 0=Enter, 1=Re-enter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric (9) | Selected customer ID | |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alphanumeric | Customer name parts | |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric (11) | Selected account ID | |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric (1) | Account status | |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric (16) | Selected card number | |
| CDEMO-LAST-MAP/MAPSET | PIC X(7) each | Alphanumeric | Last displayed map/mapset | |

### 8.2 CODATECN.cpy — Date Conversion Structure

| Field Name | PIC Clause | Purpose |
|------------|-----------|---------|
| CODATECN-IN-YEAR | PIC X(04) | Input year |
| CODATECN-IN-MONTH | PIC X(02) | Input month |
| CODATECN-IN-DAY | PIC X(02) | Input day |
| CODATECN-1O-YYYY/MM/DD | PIC X | Output YYYY-MM-DD format |
| CODATECN-2O-YYYY/MM/DD | PIC X | Output YYYYMMDD format |
| CODATECN-ERROR-MSG | PIC X(38) | Error message |
| 88 YYYY-MM-DD-OP / YYYYMMDD-OP | VALUE "1"/"2" | Output format selector |

### 8.3 CSDAT01Y.cpy — Date/Time Working Storage

Contains current date (WS-CURDATE: YYYY, MM, DD), current time (WS-CURTIME: HH, MM, SS, MS), formatted date (MM/DD/YY), formatted time (HH:MM:SS), and ISO timestamp (YYYY-MM-DD HH:MM:SS.FFFFFF).

### 8.4 CSLKPCDY.cpy — Lookup Code Repository (1,318 lines)

Contains validation lookup tables compiled into the program:
- **North American phone area codes**: 88-level condition VALID-PHONE-AREA-CODE with ~800 valid area codes
- **US state codes**: 88-level condition VALID-US-STATE-CODE with all 50 states + territories
- **State + ZIP prefix combinations**: Validates first 2 digits of ZIP against state code

### 8.5 COTTL01Y.cpy — Screen Title Structure

| Field Name | PIC Clause | Purpose |
|------------|-----------|---------|
| CCDA-TITLE01 | PIC X(40) | "AWS Mainframe Modernization" |
| CCDA-TITLE02 | PIC X(40) | "CardDemo" |
| CCDA-THANK-YOU | PIC X(40) | Exit message |

### 8.6 CSMSG01Y.cpy — Common Messages

| Field Name | PIC Clause | Purpose |
|------------|-----------|---------|
| CCDA-MSG-THANK-YOU | PIC X(50) | Application thank-you message |
| CCDA-MSG-INVALID-KEY | PIC X(50) | Invalid key pressed message |

### 8.7 CSMSG02Y.cpy — Abend Data Structure

| Field Name | PIC Clause | Purpose |
|------------|-----------|---------|
| ABEND-CODE | PIC X(4) | Abend code |
| ABEND-CULPRIT | PIC X(8) | Program causing abend |
| ABEND-REASON | PIC X(50) | Abend reason text |
| ABEND-MSG | PIC X(72) | Formatted abend message |

### 8.8 COMEN02Y.cpy — Main Menu Options

Defines 11 menu options with program name mappings:
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

### 8.9 COADM02Y.cpy — Admin Menu Options

Defines 6 admin menu options:
1. User List (Security) → COUSR00C
2. User Add (Security) → COUSR01C
3. User Update (Security) → COUSR02C
4. User Delete (Security) → COUSR03C
5. Transaction Type List/Update (DB2) → COTRTLIC
6. Transaction Type Maintenance (DB2) → COTRTUPC

### 8.10 CSSETATY.cpy — Screen Attribute Settings

Used via COPY REPLACING to set BMS field attributes (bright, dark, protected, unprotected, modified data tag). Included 38 times in COACTUPC with different field name replacements.

### 8.11 CSSTRPFY.cpy — String Formatting Procedures

Paragraph-level copybook providing string manipulation utilities: trimming, padding, and formatting for display fields.

### 8.12 CSUTLDPY.cpy / CSUTLDWY.cpy — Utility Work Areas

CSUTLDPY (375 lines): Utility program data area — date validation constants, error handling areas.
CSUTLDWY (89 lines): Utility work area — working-storage fields for utility operations.

---

## 9. DB2 Infrastructure Copybooks (Sub-Application)

### 9.1 CSDB2RPY.cpy — DB2 Reply Procedures

Contains COBOL procedure paragraphs (not data definitions):
- **9998-PRIMING-QUERY**: Verifies DB2 connectivity via `SELECT 1 FROM SYSIBM.SYSDUMMY1`
- **9999-FORMAT-DB2-MESSAGE**: Calls DSNTIAC to format SQL error messages

### 9.2 CSDB2RWY.cpy — DB2 Rewrite Work Area

Working-storage fields for DB2 operations: SQLCODE handling, DSNTIAC message formatting areas, DB2 connection status flags.

---

## 10. BMS Map Copybooks (`app/cpy-bms/` and sub-app `cpy-bms/`)

These copybooks are auto-generated from BMS macro definitions and define symbolic map structures for CICS 3270 screens.

| Copybook | LOC | Screen | Key I/O Fields |
|----------|-----|--------|---------------|
| COACTUP.CPY | 668 | Account Update | Account ID, status, balances, dates, limits, ZIP, group ID — input/output fields with attribute bytes |
| COACTVW.CPY | 464 | Account View | Same account fields — output only |
| COCRDLI.CPY | 560 | Card List | Array of card numbers with selection indicators |
| COCRDSL.CPY | — | Card Search | Account ID, card number search fields |
| COCRDUP.CPY | 224 | Card Update | Card number, name, status, expiry — input/output |
| COUSR00.CPY | 728 | User List | Array of user IDs, names, types with selection |
| COUSR01.CPY | — | User Add | User ID, name, password, type — input |
| COUSR02.CPY | — | User Update | Same as add — input/output |
| COUSR03.CPY | — | User Delete | Same as view — output with confirm |
| COTRN00.CPY | 728 | Transaction Menu | Transaction ID, card, amount, date — list display |
| COTRN01.CPY | — | Transaction View | Full transaction detail — output |
| COTRN02.CPY | — | Transaction Add | Transaction fields — input |
| COADM01.CPY | 260 | Admin Menu | Menu option selection |
| COBIL00.CPY | 140 | Bill Payment | Account, amount, confirmation |
| CORPT00.CPY | 224 | Reports | Report type, date range — input |
| COSGN00.CPY | 152 | Sign-on | User ID, password — input |
| COMEN01.CPY | — | Main Menu | Menu option selection |
| COPAUS0.CPY | — | Auth Summary List | Authorization summary array (IMS sub-app) |
| COPAUS1.CPY | — | Auth Detail View | Authorization detail fields (IMS sub-app) |
| COTRTLI.CPY | — | Tran Type List | Transaction type array (DB2 sub-app) |
| COTRTUP.CPY | — | Tran Type Update | Transaction type fields (DB2 sub-app) |
