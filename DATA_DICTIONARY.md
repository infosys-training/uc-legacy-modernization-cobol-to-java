# DATA DICTIONARY — CardDemo COBOL Estate

> **47 copybooks** defining data structures across 6 business entities and supporting infrastructure.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (300 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| ACCT-ID | PIC 9(11) | Numeric | Primary key — unique account identifier | Must be unique across ACCTFILE KSDS |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Account active flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance | Signed; updated by CBTRN02C, CBACT04C |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Maximum credit allowed | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alpha | Date account was opened | CCYYMMDD format; validated by CSUTLDPY |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alpha | Account expiration date | CCYYMMDD format |
| ACCT-REISSUE-DATE | PIC X(10) | Alpha | Card reissue date | CCYYMMDD format |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total | Reset per billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total | Reset per billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alpha | Account holder ZIP code | Validated against CSLKPCDY |
| ACCT-GROUP-ID | PIC X(10) | Alpha | Disclosure/interest rate group | Links to CVTRA02Y (DIS-GROUP-RECORD) |
| FILLER | PIC X(178) | Alpha | Reserved space | — |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (500 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| CUST-ID | PIC 9(09) | Numeric | Primary key — customer identifier | Must be unique across CUSTFILE |
| CUST-FIRST-NAME | PIC X(25) | Alpha | Customer first name | — |
| CUST-MIDDLE-NAME | PIC X(25) | Alpha | Customer middle name | — |
| CUST-LAST-NAME | PIC X(25) | Alpha | Customer last name | — |
| CUST-ADDR-LINE-1 | PIC X(50) | Alpha | Address line 1 | — |
| CUST-ADDR-LINE-2 | PIC X(50) | Alpha | Address line 2 | — |
| CUST-ADDR-LINE-3 | PIC X(50) | Alpha | Address line 3 | — |
| CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code | Validated against 50-state list in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code | — |
| CUST-ADDR-ZIP | PIC X(10) | Alpha | ZIP / postal code | Prefix validated via CSLKPCDY state-ZIP mapping |
| CUST-PHONE-NUM-1 | PIC X(15) | Alpha | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alpha | Secondary phone number | Area code validated against NANPA list |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit numeric |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alpha | Government-issued ID | — |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alpha | Date of birth | YYYY-MM-DD; validated by CSUTLDPY (age, leap year) |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alpha | EFT (electronic fund transfer) account | — |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary card holder indicator | 'Y' = primary |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | Alpha | Reserved space | — |

### CUSTREC.cpy — Customer Record (Batch variant, 500 bytes)

Identical structure to CVCUS01Y but with minor naming differences (used by CBSTM03A):

| Field | PIC Clause | Notes |
|-------|-----------|-------|
| CUST-DOB-YYYYMMDD | PIC X(10) | Slightly different name from CVCUS01Y's CUST-DOB-YYYY-MM-DD |

> All other fields are identical to CVCUS01Y.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (150 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| CARD-NUM | PIC X(16) | Alpha | Credit card number | 16-digit PAN |
| CARD-ACCT-ID | PIC 9(11) | Numeric | Foreign key → Account | Must exist in ACCTFILE |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit CVV |
| CARD-EMBOSSED-NAME | PIC X(50) | Alpha | Name embossed on card | — |
| CARD-EXPIRAION-DATE | PIC X(10) | Alpha | Card expiration date | CCYYMMDD format |
| CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card active flag | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Alpha | Reserved space | — |

### CVACT03Y.cpy — Card Cross-Reference (50 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| XREF-CARD-NUM | PIC X(16) | Alpha | Card number (primary key) | Links to CVACT02Y |
| XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID | Links to CVCUS01Y |
| XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID | Links to CVACT01Y |
| FILLER | PIC X(14) | Alpha | Reserved space | — |

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (350 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| TRAN-ID | PIC X(16) | Alpha | Transaction identifier | System-generated unique ID |
| TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | Links to CVTRA03Y (TRAN-TYPE-RECORD) |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Links to CVTRA04Y (TRAN-CAT-RECORD) |
| TRAN-SOURCE | PIC X(10) | Alpha | Origination source | — |
| TRAN-DESC | PIC X(100) | Alpha | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Signed; positive = credit, negative = debit |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alpha | Card used for transaction | Links to CVACT02Y |
| TRAN-ORIG-TS | PIC X(26) | Alpha | Origination timestamp | ISO 8601 format |
| TRAN-PROC-TS | PIC X(26) | Alpha | Processing timestamp | ISO 8601 format |
| FILLER | PIC X(20) | Alpha | Reserved space | — |

### CVTRA06Y.cpy — Daily Transaction Record (350 bytes)

Same structure as CVTRA05Y with `DALYTRAN-` prefix. Used as the input feed for batch posting:

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| DALYTRAN-ID | PIC X(16) | Daily transaction ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Transaction type |
| DALYTRAN-AMT | PIC S9(09)V99 | Transaction amount |
| DALYTRAN-CARD-NUM | PIC X(16) | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Origination timestamp |
| _(remaining fields identical to CVTRA05Y)_ | | |

### CVTRA01Y.cpy — Transaction Category Balance (50 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID (part of composite key) | Links to CVACT01Y |
| TRANCAT-TYPE-CD | PIC X(02) | Alpha | Transaction type code (composite key) | Links to CVTRA03Y |
| TRANCAT-CD | PIC 9(04) | Numeric | Category code (composite key) | Links to CVTRA04Y |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Running balance per category | Updated by CBTRN02C, CBACT04C |
| FILLER | PIC X(22) | Alpha | Reserved space | — |

### CVTRA02Y.cpy — Disclosure Group (50 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alpha | Account group ID (composite key) | Links to ACCT-GROUP-ID in CVACT01Y |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type (composite key) | — |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (composite key) | — |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate for this group/type/category | Percentage value |
| FILLER | PIC X(28) | Alpha | Reserved space | — |

### CVTRA03Y.cpy — Transaction Type (60 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| TRAN-TYPE | PIC X(02) | Alpha | Transaction type code (primary key) | — |
| TRAN-TYPE-DESC | PIC X(50) | Alpha | Type description | — |
| FILLER | PIC X(08) | Alpha | Reserved | — |

### CVTRA04Y.cpy — Transaction Category (60 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| TRAN-TYPE-CD | PIC X(02) | Alpha | Type code (composite key part 1) | — |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (composite key part 2) | — |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alpha | Category description | — |
| FILLER | PIC X(04) | Alpha | Reserved | — |

### CVTRA07Y.cpy — Transaction Report Layout (73 lines)

Defines report headers and detail line formats:

| Structure | Fields | Purpose |
|-----------|--------|---------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME, REPT-LONG-NAME, REPT-START-DATE, REPT-END-DATE | Report title and date range |
| TRANSACTION-DETAIL-REPORT | TRAN-REPORT-TRANS-ID, TRAN-REPORT-ACCOUNT-ID, TRAN-REPORT-TYPE-CD/DESC, TRAN-REPORT-CAT-CD/DESC, TRAN-REPORT-SOURCE, TRAN-REPORT-AMT | Detail line layout |
| REPORT-PAGE-TOTALS | REPT-PAGE-TOTAL PIC +ZZZ,ZZZ,ZZZ.ZZ | Page subtotal |
| REPORT-ACCOUNT-TOTALS | REPT-ACCOUNT-TOTAL | Account subtotal |
| REPORT-GRAND-TOTALS | REPT-GRAND-TOTAL | Grand total |

### COSTM01.CPY — Statement Transaction Layout (38 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| TRNX-CARD-NUM | PIC X(16) | Alpha | Card number (key part 1) |
| TRNX-ID | PIC X(16) | Alpha | Transaction ID (key part 2) |
| TRNX-TYPE-CD | PIC X(02) | Alpha | Type code |
| TRNX-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRNX-SOURCE | PIC X(10) | Alpha | Source |
| TRNX-DESC | PIC X(100) | Alpha | Description |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| TRNX-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name |
| TRNX-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP |
| TRNX-ORIG-TS | PIC X(26) | Alpha | Origination timestamp |
| TRNX-PROC-TS | PIC X(26) | Alpha | Processing timestamp |
| FILLER | PIC X(20) | Alpha | Reserved |

---

## 5. Authorization Entity (IMS)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Segment)

Root segment of the IMS hierarchical database for pending authorizations.

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| _(IMS PCB and summary fields)_ | — | — | Summary-level authorization data |

### CIPAUDTY.cpy — Pending Authorization Detail (54 lines, IMS Segment)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (key part 1) | — |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (key part 2) | — |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alpha | Original authorization date | YYMMDD |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alpha | Original authorization time | HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alpha | Card number | Links to CVACT02Y |
| PA-AUTH-TYPE | PIC X(04) | Alpha | Authorization type | — |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry | YYMM |
| PA-MESSAGE-TYPE | PIC X(06) | Alpha | ISO 8583 message type | — |
| PA-MESSAGE-SOURCE | PIC X(06) | Alpha | Message source system | — |
| PA-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization ID code | Generated by COPAUA0C |
| PA-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code | 88 PA-AUTH-APPROVED VALUE '00' |
| PA-AUTH-RESP-REASON | PIC X(04) | Alpha | Response reason | — |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Requested amount | — |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount | ≤ PA-TRANSACTION-AMT |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | Merchant category (MCC) | — |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country | — |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | — |
| PA-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID | — |
| PA-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP | — |
| PA-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID | — |

### CCPAURQY.cpy — Authorization Request (MQ Message, 36 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alpha | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Alpha | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Alpha | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alpha | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry date |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alpha | ISO 8583 message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alpha | Source system |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | Merchant category code |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country code |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID |

### CCPAURLY.cpy — Authorization Response (MQ Message, 24 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alpha | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization ID code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alpha | Response reason |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### CCPAUERY.cpy — Authorization Error Log (40 lines)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| ERR-DATE | PIC X(06) | Alpha | Error date | YYMMDD |
| ERR-TIME | PIC X(06) | Alpha | Error time | HHMMSS |
| ERR-APPLICATION | PIC X(08) | Alpha | Application name | — |
| ERR-PROGRAM | PIC X(08) | Alpha | Program name | — |
| ERR-LOCATION | PIC X(04) | Alpha | Error location code | — |
| ERR-LEVEL | PIC X(01) | Alpha | Error severity | 88: 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alpha | Subsystem origin | 88: 'A'=App, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=File |
| ERR-CODE-1 | PIC X(09) | Alpha | Primary error code | — |
| ERR-CODE-2 | PIC X(09) | Alpha | Secondary error code | — |
| ERR-MESSAGE | PIC X(50) | Alpha | Error message text | — |
| ERR-EVENT-KEY | PIC X(20) | Alpha | Event correlation key | — |

---

## 6. IMS PCB Structures

### PADFLPCB.CPY — Pending Auth Detail PCB

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PADFL-DBDNAME | PIC X(08) | Alpha | DBD name |
| PADFL-SEG-LEVEL | PIC X(02) | Alpha | Segment level |
| PADFL-PCB-STATUS | PIC X(02) | Alpha | DL/I status code |
| PADFL-PCB-PROCOPT | PIC X(04) | Alpha | Processing options |
| PADFL-SEG-NAME | PIC X(08) | Alpha | Segment name |
| PADFL-KEYFB | PIC X(255) | Alpha | Key feedback area |

### PASFLPCB.CPY — Pending Auth Summary PCB

Same structure as PADFLPCB with `PASFL-` prefix and KEYFB of PIC X(100).

### PAUTBPCB.CPY — Pending Auth Base PCB

Same structure as PADFLPCB with `PAUT-` prefix.

---

## 7. Application Infrastructure Copybooks

### COCOM01Y.cpy — CICS Communication Area (47 lines)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alpha | Source CICS transaction ID | — |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alpha | Source program name | — |
| CDEMO-TO-TRANID | PIC X(04) | Alpha | Target transaction ID | — |
| CDEMO-TO-PROGRAM | PIC X(08) | Alpha | Target program name | — |
| CDEMO-USER-ID | PIC X(08) | Alpha | Logged-in user ID | — |
| CDEMO-USER-TYPE | PIC X(01) | Alpha | User type | 88: 'A'=Admin, 'U'=User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context | 88: 0=Enter, 1=Reenter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Selected customer ID | — |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alpha | Customer name fields | — |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Selected account ID | — |
| CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Account status | — |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Selected card number | — |
| CDEMO-LAST-MAP | PIC X(7) | Alpha | Last displayed BMS map | — |
| CDEMO-LAST-MAPSET | PIC X(7) | Alpha | Last BMS mapset | — |

### CSUSR01Y.cpy — User Security Record (80 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| SEC-USR-ID | PIC X(08) | Alpha | User ID (primary key) | — |
| SEC-USR-FNAME | PIC X(20) | Alpha | First name | — |
| SEC-USR-LNAME | PIC X(20) | Alpha | Last name | — |
| SEC-USR-PWD | PIC X(08) | Alpha | Password (plain text) | — |
| SEC-USR-TYPE | PIC X(01) | Alpha | User type | 'A'=Admin, 'U'=Regular |
| SEC-USR-FILLER | PIC X(23) | Alpha | Reserved | — |

### CVCRD01Y.cpy — CICS Work Areas (46 lines)

| Field | PIC Clause | Data Type | Business Meaning | Validation |
|-------|-----------|-----------|-----------------|------------|
| CCARD-AID | PIC X(5) | Alpha | Mapped AID key | 88 values: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alpha | Next program to transfer to | — |
| CCARD-NEXT-MAPSET | PIC X(7) | Alpha | Next BMS mapset | — |
| CCARD-NEXT-MAP | PIC X(7) | Alpha | Next BMS map | — |
| CCARD-ERROR-MSG | PIC X(75) | Alpha | Error message for display | — |
| CCARD-RETURN-MSG | PIC X(75) | Alpha | Return message for display | 88: LOW-VALUES = off |
| CC-ACCT-ID | PIC X(11) | Alpha | Working account ID | — |
| CC-CARD-NUM | PIC X(16) | Alpha | Working card number | — |
| CC-CUST-ID | PIC X(09) | Alpha | Working customer ID | — |

### COADM02Y.cpy — Admin Menu Options (62 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CDEMO-ADMIN-OPT-COUNT | PIC 9(02) | Numeric | Number of admin options (VALUE 6) |
| CDEMO-ADMIN-OPT (OCCURS 9) | — | Group | Array of menu entries |
| — CDEMO-ADMIN-OPT-NUM | PIC 9(02) | Numeric | Option number |
| — CDEMO-ADMIN-OPT-NAME | PIC X(35) | Alpha | Option display name |
| — CDEMO-ADMIN-OPT-PGMNAME | PIC X(08) | Alpha | Target program name |

Options: 1=COUSR00C (User List), 2=COUSR01C (User Add), 3=COUSR02C (User Update), 4=COUSR03C (User Delete), 5=COTRTLIC (Tran Type List/DB2), 6=COTRTUPC (Tran Type Maintenance/DB2)

### COMEN02Y.cpy — Main Menu Options (101 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CDEMO-MENU-OPT-COUNT | PIC 9(02) | Numeric | Number of menu options (VALUE 11) |
| CDEMO-MENU-OPT (OCCURS 12) | — | Group | Array of menu entries |
| — CDEMO-MENU-OPT-NUM | PIC 9(02) | Numeric | Option number |
| — CDEMO-MENU-OPT-NAME | PIC X(35) | Alpha | Option display name |
| — CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Alpha | Target program name |
| — CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Alpha | Required user type ('U'=all users) |

Options: 1=COACTVWC, 2=COACTUPC, 3=COCRDLIC, 4=COCRDSLC, 5=COCRDUPC, 6=COTRN00C, 7=COTRN01C, 8=COTRN02C, 9=CORPT00C, 10=COBIL00C, 11=COPAUS0C

### COTTL01Y.cpy — Screen Title (27 lines)

| Field | PIC Clause | Value | Purpose |
|-------|-----------|-------|---------|
| CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' | Screen title line 1 |
| CCDA-TITLE02 | PIC X(40) | 'CardDemo' | Screen title line 2 |
| CCDA-THANK-YOU | PIC X(40) | 'Thank you for using CCDA application...' | Goodbye message |

### CSDAT01Y.cpy — Date/Time Working Storage (58 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| WS-CURTIME-HOURS/MINUTE/SECOND/MILSEC | PIC 9(02) each | Numeric | Current time components |
| WS-CURDATE-MM-DD-YY | Group | — | Formatted date (MM/DD/YY) |
| WS-CURTIME-HH-MM-SS | Group | — | Formatted time (HH:MM:SS) |
| WS-TIMESTAMP | Group | — | ISO timestamp (YYYY-MM-DD HH:MM:SS.ffffff) |

### CSMSG01Y.cpy — Common Messages (24 lines)

| Field | PIC Clause | Value |
|-------|-----------|-------|
| CCDA-MSG-THANK-YOU | PIC X(50) | 'Thank you for using CardDemo application...' |
| CCDA-MSG-INVALID-KEY | PIC X(50) | 'Invalid key pressed. Please see below...' |

### CSMSG02Y.cpy — Abend Data (35 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| ABEND-CODE | PIC X(4) | Alpha | Abend code |
| ABEND-CULPRIT | PIC X(8) | Alpha | Program causing abend |
| ABEND-REASON | PIC X(50) | Alpha | Abend reason text |
| ABEND-MSG | PIC X(72) | Alpha | Formatted abend message |

### CODATECN.cpy — Date Conversion Record (52 lines)

| Field | PIC Clause | Business Meaning | Validation |
|-------|-----------|-----------------|------------|
| CODATECN-TYPE | PIC X | Input format type | 88: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Input date | — |
| CODATECN-OUTTYPE | PIC X | Output format type | 88: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| CODATECN-0UT-DATE | PIC X(20) | Output date | — |
| CODATECN-ERROR-MSG | PIC X(38) | Error message | Non-blank if invalid |

---

## 8. Validation / Utility Copybooks

### CSLKPCDY.cpy — Lookup Code Repository (1,318 lines)

Contains static validation tables:

| Validation Set | Field | Values | Purpose |
|---------------|-------|--------|---------|
| Phone area codes | WS-US-PHONE-AREA-CODE-TO-EDIT PIC XXX | 88 VALID-PHONE-AREA-CODE: ~380 NANPA codes ('201'...'989') | Validate North American phone area codes |
| US state codes | _(defined within)_ | 50 US states + territories | Validate CUST-ADDR-STATE-CD |
| State-ZIP mapping | _(defined within)_ | State + first 2 ZIP digits | Cross-validate state against ZIP prefix |

### CSUTLDWY.cpy — Date Validation Working Storage (89 lines)

Working storage variables for date validation logic (century, year, month, day, leap year checks).

### CSUTLDPY.cpy — Date Validation Procedures (375 lines)

Procedure Division copybook with reusable paragraphs:

| Paragraph | Purpose |
|-----------|---------|
| EDIT-DATE-CCYYMMDD | Main date validation entry point |
| EDIT-YEAR-CCYY | Validate century and year |
| EDIT-MONTH | Validate month (01–12) |
| EDIT-DAY | Validate day (accounts for month length and leap year) |
| EDIT-DATE-OF-BIRTH | Validate DOB (must be in the past) |

### CSSETATY.cpy — Screen Attribute Setter (30 lines)

Generic copybook used with `COPY REPLACING` to set BMS screen field attributes (color, protection) based on validation flags. Used 31+ times in COACTUPC.

### CSSTRPFY.cpy — PFKey Storage Procedure (85 lines)

Maps EIBAID (CICS attention identifier) to CCARD-AID values via EVALUATE TRUE statement. Maps DFHENTER, DFHCLEAR, DFHPA1/2, DFHPF1–PF24 to logical key names.

### UNUSED1Y.cpy — Unused Data Structure (10 lines)

Placeholder copybook — identical layout to CSUSR01Y with `UNUSED-` prefix. Not referenced by any program.

---

## 9. DB2 Infrastructure Copybooks

### CSDB2RPY.cpy — DB2 Common Procedures (89 lines)

Contains reusable DB2 procedures including:
- `9998-PRIMING-QUERY` — Connectivity test (`SELECT 1 FROM SYSIBM.SYSDUMMY1`)
- `9999-FORMAT-DB2-MESSAGE` — DSNTIAC error formatting

### CSDB2RWY.cpy — DB2 Common Working Storage (46 lines)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| WS-DISP-SQLCODE | PIC ----9 | Edited numeric | Display SQLCODE |
| WS-DUMMY-DB2-INT | PIC S9(4) COMP-3 | Packed decimal | Priming query target |
| WS-DB2-PROCESSING-FLAG | PIC X(1) | Alpha | 88: '0'=OK, '1'=Error |
| WS-DB2-CURRENT-ACTION | PIC X(72) | Alpha | Current DB2 action description |
| WS-DSNTIAC-FORMATTED | Group | — | DSNTIAC message construction area |
| WS-DSNTIAC-FMTD-TEXT-LINE | PIC X(72) OCCURS 10 | Alpha | Formatted error text lines |

---

## 10. Export/Import Copybook

### CVEXPORT.cpy — Multi-Record Export Layout (103 lines, 500 bytes)

Defines a unified export record with REDEFINES for each entity type:

| Structure | Prefix | Source Entity | Storage Optimization |
|-----------|--------|--------------|---------------------|
| EXPORT-RECORD (header) | EXPORT- | — | REC-TYPE (X/1), TIMESTAMP (X/26), SEQUENCE-NUM (9/9 COMP), BRANCH-ID (X/4), REGION-CODE (X/5) |
| EXPORT-CUSTOMER-DATA | EXP-CUST- | CVCUS01Y | CUST-ID as 9(9) COMP, FICO as 9(3) COMP-3, OCCURS 3 for address lines |
| EXPORT-ACCOUNT-DATA | EXP-ACCT- | CVACT01Y | CURR-BAL as COMP-3, CASH-CREDIT as COMP-3, CYC-DEBIT as COMP |
| EXPORT-TRANSACTION-DATA | EXP-TRAN- | CVTRA05Y | AMT as COMP-3, MERCHANT-ID as COMP |
| EXPORT-CARD-XREF-DATA | EXP-XREF- | CVACT03Y | ACCT-ID as 9(11) COMP |
| EXPORT-CARD-DATA | EXP-CARD- | CVACT02Y | ACCT-ID as COMP, CVV as COMP |

---

## 11. Entity Relationship Summary

```
Customer (CVCUS01Y)
    │
    ├──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
    │                                            │
    │                                      DisclosureGroup (CVTRA02Y)
    │                                            │
    └──────── Card (CVACT02Y)                    │
                  │                              │
                  └───► Transaction (CVTRA05Y) ──┤
                            │                    │
                      TranType (CVTRA03Y)   TranCatBal (CVTRA01Y)
                            │
                      TranCategory (CVTRA04Y)

    Authorization (CIPAUDTY) ──► AuthRequest (CCPAURQY) / AuthResponse (CCPAURLY)
                              └─► ErrorLog (CCPAUERY)

    User Security (CSUSR01Y)   [Standalone]
    Export Record (CVEXPORT)    [Composite of all entities]
```

---

*Generated from analysis of 47 copybooks across app/cpy/, app/cpy-bms/, and sub-application copybook directories.*
