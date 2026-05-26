# Data Dictionary — CardDemo COBOL Estate

## 1. ACCOUNT Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Unique account identifier | Primary key, indexed |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Account active/inactive flag | 'Y'/'N' expected |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12,2) | Current account balance | Signed; can be negative |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Maximum credit limit | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (12,2) | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric date | Account opening date | Format YYYY-MM-DD |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric date | Account expiration date | Format YYYY-MM-DD; must be > OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric date | Last card reissue date | Format YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (12,2) | Current billing cycle credits | Running total; reset at cycle close |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (12,2) | Current billing cycle debits | Running total; reset at cycle close |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account holder zip code | US zip+4 or international postal code |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Account group/portfolio ID | Links to disclosure group |
| FILLER | PIC X(178) | Filler | Reserved space | Pad to 300 bytes |

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID for category balance | FK to ACCOUNT-RECORD |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK to TRAN-TYPE-RECORD |
| TRANCAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | FK to TRAN-CAT-RECORD |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (11,2) | Running balance for category | Updated by interest calc |
| FILLER | PIC X(22) | Filler | Reserved | Pad to 50 bytes |

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure group identifier | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Composite key part |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | Composite key part |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6,2) | Interest rate for category | Applied during interest calc |
| FILLER | PIC X(28) | Filler | Reserved | Pad to 50 bytes |

---

## 2. CUSTOMER Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric (9) | Unique customer identifier | Primary key, indexed |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | Required; non-blank |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | Required; non-blank |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric (2) | US state code | Validated against state table (CSLKPCDY) |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric (3) | Country code | ISO 3-char |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Postal/zip code | Validated against state+zip (CSLKPCDY) |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone | Area code validated (CSLKPCDY) |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone | Optional |
| CUST-SSN | PIC 9(09) | Numeric (9) | Social Security Number | 9-digit numeric; sensitive PII |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID number | Driver's license / passport |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric date | Date of birth | Format YYYY-MM-DD; validated via CSUTLDPY |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | EFT/bank account for payments | Optional |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric (1) | Primary cardholder indicator | 'Y'/'N' |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3) | FICO credit score | Range 300-850 |
| FILLER | PIC X(168) | Filler | Reserved | Pad to 500 bytes |

### CUSTREC.cpy — Customer Record (Alternate layout, RECLN 500)

Identical fields to CVCUS01Y except `CUST-DOB-YYYYMMDD` (no hyphens in date format). Used by CBSTM03A statement program.

### CSUSR01Y.cpy — User Security Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric (8) | User login ID | Primary key; unique |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | Required |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | Required |
| SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password | Min 4 chars |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric (1) | User type (Admin/User) | 'A' or 'U' |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved | Pad to 80 bytes |

---

## 3. CARD Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (PAN) | Primary key; 16-digit; Luhn checksum |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11) | Associated account ID | FK to ACCOUNT-RECORD |
| CARD-CVV-CD | PIC 9(03) | Numeric (3) | Card Verification Value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name printed on card | Non-blank |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric date | Card expiration date | Format YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Card active/inactive | 'Y'/'N' |
| FILLER | PIC X(59) | Filler | Reserved | Pad to 150 bytes |

### CVACT03Y.cpy — Card Cross-Reference (RECLN 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | Primary key; FK to CARD-RECORD |
| XREF-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID | FK to CUSTOMER-RECORD |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | FK to ACCOUNT-RECORD |
| FILLER | PIC X(14) | Filler | Reserved | Pad to 50 bytes |

---

## 4. TRANSACTION Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Unique transaction ID | Primary key |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK to TRAN-TYPE-RECORD |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | FK to TRAN-CAT-RECORD |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source/channel | e.g., 'POS', 'ATM', 'WEB' |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | Negative for credits |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant identifier | Optional for non-POS |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | Required for POS |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | Required for POS |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant zip code | Optional |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card used for transaction | FK to CARD-RECORD |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original timestamp | ISO format with microseconds |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | Set during batch posting |
| FILLER | PIC X(20) | Filler | Reserved | Pad to 350 bytes |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same structure as CVTRA05Y but prefixed with `DALYTRAN-` instead of `TRAN-`. Used for staging daily transaction batches before posting to master.

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric (2) | Transaction type code | Primary key; e.g., 'SA' (Sale), 'CR' (Credit) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Transaction type description | Descriptive text |
| FILLER | PIC X(08) | Filler | Reserved | Pad to 60 bytes |

### CVTRA04Y.cpy — Transaction Category Type (RECLN 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | Composite key; FK to TRAN-TYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Category code within type | Composite key |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | e.g., 'Retail Purchase', 'Cash Advance' |
| FILLER | PIC X(04) | Filler | Reserved | Pad to 60 bytes |

### CVTRA07Y.cpy — Transaction Detail Report Layout

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report short name | VALUE 'DALYREPT' |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report full title | VALUE 'Daily Transaction Report' |
| REPT-DATE-HEADER | PIC X(12) | Alphanumeric | Date range label | VALUE 'Date Range: ' |
| REPT-START-DATE | PIC X(10) | Alphanumeric date | Report start date | |
| REPT-END-DATE | PIC X(10) | Alphanumeric date | Report end date | |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID in report | |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID in report | |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code | |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description (abbrev) | |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code | |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description | |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Transaction source | |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount | Negative sign suppressed left |

### COSTM01.CPY — Transaction Record (Statement Reporting, keyed by card+tran)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (key part 1) | Composite key |
| TRNX-ID | PIC X(16) | Alphanumeric (16) | Transaction ID (key part 2) | Composite key |
| TRNX-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | |
| TRNX-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | |
| TRNX-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source | |
| TRNX-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal (11,2) | Transaction amount | |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant ID | |
| TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | |
| TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant zip | |
| TRNX-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original timestamp | |
| TRNX-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | |

---

## 5. SYSTEM / INFRASTRUCTURE Copybooks

### COCOM01Y.cpy — CICS COMMAREA (Communication Area)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric (4) | Source transaction ID | CICS transaction code |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric (8) | Source program name | 8-char COBOL program |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric (4) | Target transaction ID | |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric (8) | Target program name | Program to XCTL to |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric (8) | Logged-in user ID | FK to SEC-USER-DATA |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric (1) | User type | 88 values: 'A' (Admin) / 'U' (User) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric (1) | Program entry context | 88: 0=ENTER, 1=REENTER |
| CDEMO-CUST-ID | PIC 9(09) | Numeric (9) | Selected customer ID | Passed between programs |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric (25) | Customer first name | Display purposes |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric (25) | Customer middle name | |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric (25) | Customer last name | |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric (11) | Selected account ID | |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric (1) | Account status | |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric (16) | Selected card number | |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric (7) | Last displayed BMS map | |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric (7) | Last displayed mapset | |

### CVCRD01Y.cpy — Card Work Areas (CICS navigation state)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CCARD-AID | PIC X(5) | Alphanumeric (5) | Last AID key pressed | 88 values: ENTER, CLEAR, PA1/PA2, PFK01-PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric (8) | Next program to transfer to | |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric (7) | Next BMS mapset | |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric (7) | Next BMS map | |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric (75) | Error message to display | |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric (75) | Return/status message | 88: OFF = LOW-VALUES |

### COADM02Y.cpy — Admin Menu Options (6 options)

Defines the menu structure for admin users. Each option entry has: option number (PIC 9(02)), description (PIC X(35)), program name (PIC X(08)).

### COMEN02Y.cpy — Main Menu Options (11 options)

Defines the menu structure for regular users. Each option has: number, description, program name, and access-type indicator (PIC X(01) — 'U' for User, 'A' for Admin, 'B' for Both).

### CODATECN.cpy — Date Conversion Input/Output

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CODATECN-TYPE | PIC X | Alphanumeric (1) | Input format type | 88: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric (20) | Input date string | |
| CODATECN-1YYYY | PIC XXXX | Alphanumeric (4) | Year (YYYYMMDD format) | REDEFINES |
| CODATECN-1MM | PIC XX | Alphanumeric (2) | Month | |
| CODATECN-1DD | PIC XX | Alphanumeric (2) | Day | |

### CSUTLDWY.cpy — Date Validation Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| WS-EDIT-DATE-CC | PIC X(2) / PIC 9(2) | Century | 88: 20=THIS-CENTURY, 19=LAST-CENTURY |
| WS-EDIT-DATE-YY | PIC X(2) / PIC 9(2) | Year within century | |
| WS-EDIT-DATE-CCYY-N | PIC 9(4) | Full year numeric | REDEFINES CC+YY |
| WS-EDIT-DATE-MM-N | PIC 9(2) | Month | 88: 1-12 valid; 1,3,5,7,8,10,12 = 31-day; 2 = February |
| WS-EDIT-DATE-DD-N | PIC 9(2) | Day | 88: 1-31 valid; 31 = DAY-31 |

### CSDAT01Y.cpy — Current Date/Time Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric (4) | Current year | |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric (2) | Current month | |
| WS-CURDATE-DAY | PIC 9(02) | Numeric (2) | Current day | |
| WS-CURTIME-HOURS | PIC 9(02) | Numeric (2) | Current hours | |
| WS-CURTIME-MINUTE | PIC 9(02) | Numeric (2) | Current minutes | |
| WS-CURTIME-SECOND | PIC 9(02) | Numeric (2) | Current seconds | |
| WS-CURTIME-MILSEC | PIC 9(02) | Numeric (2) | Current milliseconds | |

### COTTL01Y.cpy — Screen Title Constants

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CCDA-TITLE01 | PIC X(40) | Alphanumeric | Title line 1 | VALUE 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | Alphanumeric | Title line 2 | VALUE 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | Alphanumeric | Exit message | VALUE 'Thank you for using...' |

### CSMSG01Y.cpy — Common Messages

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| CCDA-MSG-THANK-YOU | PIC X(50) | Alphanumeric | Thank-you message | |
| CCDA-MSG-INVALID-KEY | PIC X(50) | Alphanumeric | Invalid key message | |

### CSMSG02Y.cpy — Abend Data

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| ABEND-CODE | PIC X(4) | Alphanumeric (4) | Abend code | |
| ABEND-CULPRIT | PIC X(8) | Alphanumeric (8) | Program causing abend | |
| ABEND-REASON | PIC X(50) | Alphanumeric (50) | Abend reason text | |
| ABEND-MSG | PIC X(72) | Alphanumeric (72) | Formatted abend message | |

### CSLKPCDY.cpy — Lookup Code Repository (1318 lines)

Contains:
1. **Phone Area Codes** — 88-level VALUE list of valid North American area codes
2. **US State Codes** — 88-level VALUE list of 50 state abbreviations
3. **State + Zip Prefix** — 88-level cross-reference for state/zip validation

Used by COACTUPC for field-level validation of customer address and phone data.

### CSSTRPFY.cpy — Store PF Key (Procedure Division)

Maps EIBAID to COMMAREA AID field. EVALUATE block translating DFHENTER/DFHCLEAR/DFHPA1-2/DFHPF1-12 to CCARD-AID-xxx values.

### CSSETATY.cpy — Set Attribute (Procedure Division with REPLACING)

Template copybook used with COPY...REPLACING to set BMS field attributes (color to red, value to '*') when validation fails. Parameterized via TESTVAR1, SCRNVAR2, MAPNAME3 replacement tokens.

---

## 6. EXPORT Entity (Branch Migration)

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

**Header fields (common to all record types):**

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric (1) | Record type discriminator | 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric (26) | Export timestamp | ISO format |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary integer | Sequence counter | Auto-incremented |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric (4) | Source branch ID | |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric (5) | Region code | |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric (460) | Record payload (REDEFINES) | Interpreted by REC-TYPE |

Record-type-specific data uses REDEFINES on EXPORT-RECORD-DATA with COMP/COMP-3 optimization for numeric fields.

---

## 7. AUTHORIZATION Entity (IMS Sub-Application)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID | |
| PA-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID | |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric | Authorization status | |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alphanumeric array | Account status codes | 5 occurrences |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Credit limit | |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Cash limit | |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Credit balance | |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (11,2) | Cash balance | |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Approved auth count | |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Declined auth count | |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount | |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount | |

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Auth date (key) | Part of segment key |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Auth time (key) | Part of segment key |
| PA-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric (4) | Authorization type | |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric (6) | Authorization ID code | |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric (2) | Response code | 88: '00'=APPROVED |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric (4) | Decline reason | |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Requested amount | |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (12,2) | Approved amount | |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant ID | |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name | |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric (1) | Match status | 88: P=Pending, D=Declined, E=Expired, M=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric (1) | Fraud indicator | 88: F=Confirmed, R=Removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric (8) | Date fraud reported | |

### CCPAURQY.cpy — Authorization MQ Request

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Request date | |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Request time | |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number | |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount | |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID | |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID | |

### CCPAURLY.cpy — Authorization MQ Response

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number | |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID | |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID | |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code | '00'=Approved |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Reason code | |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount | |

### CCPAUERY.cpy — Authorization Error Log

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-----------|-----------|-----------|-----------------|-----------------|
| ERR-DATE | PIC X(06) | Alphanumeric | Error date | |
| ERR-TIME | PIC X(06) | Alphanumeric | Error time | |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name | |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | Program name | |
| ERR-LEVEL | PIC X(01) | Alphanumeric | Severity level | 88: L=Log, I=Info, W=Warning, C=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem | 88: A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | Primary error code | |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | Secondary error code | |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | Error description | |

---

## 8. Summary Statistics

| Metric | Value |
|--------|-------|
| Total copybooks analyzed | 30+ (app/cpy/) + 8 (sub-apps) |
| Total data fields documented | ~200 |
| Business entities identified | 7 (Account, Customer, Card, Transaction, Export, Authorization, System) |
| Record lengths | 50–500 bytes |
| Storage formats used | Display, COMP (binary), COMP-3 (packed decimal), Edited numeric |
