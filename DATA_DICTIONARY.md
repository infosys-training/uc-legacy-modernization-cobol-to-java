# DATA_DICTIONARY.md — CardDemo Copybook Field Reference

> **Total Copybooks:** 47 (30 main `app/cpy/` + 17 sub-app)
> **Business Entities:** Account, Customer, Card, Transaction, Authorization, User Security

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

Primary data store for credit card accounts. Key: `ACCT-ID`.

| Field Name | PIC Clause | Data Type | Byte Pos | Business Meaning | Validation Rules |
|------------|-----------|-----------|----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (zoned) | 1–11 | Account primary key | Unique, non-zero |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 12 | Active/Inactive flag | 'Y' or 'N' |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | 13–24 | Current balance | Signed; overpunch encoding; scale=2 |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 25–36 | Credit limit | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 37–48 | Cash advance limit | ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | 49–58 | Account open date | Format: YYYY-MM-DD; validated in COACTUPC |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 59–68 | Account expiration date | Format: YYYY-MM-DD; must be > OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | 69–78 | Card reissue date | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | 79–90 | Current cycle credits | Running total; reset at cycle end |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | 91–102 | Current cycle debits | Running total; reset at cycle end |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | 103–112 | Account billing ZIP | Validated against CSLKPCDY state-ZIP table |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 113–122 | Disclosure/interest rate group | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| FILLER | PIC X(178) | Filler | 123–300 | Reserved | Pads to 300-byte record |

### CVACT03Y.cpy — Card-Account Cross-Reference (RECLN 50)

Links cards to accounts and customers. Key: `XREF-CARD-NUM`.

| Field Name | PIC Clause | Data Type | Byte Pos | Business Meaning | Validation Rules |
|------------|-----------|-----------|----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | 1–16 | Card number (primary key) | 16-digit card number |
| XREF-CUST-ID | PIC 9(09) | Numeric | 17–25 | Customer ID (FK → CVCUS01Y) | Must exist in CUSTFILE |
| XREF-ACCT-ID | PIC 9(11) | Numeric | 26–36 | Account ID (FK → CVACT01Y) | Must exist in ACCTFILE |
| FILLER | PIC X(14) | Filler | 37–50 | Reserved | |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

Master customer data. Key: `CUST-ID`.

| Field Name | PIC Clause | Data Type | Byte Pos | Business Meaning | Validation Rules |
|------------|-----------|-----------|----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric | 1–9 | Customer primary key | Unique, non-zero |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | 10–34 | First name | Non-blank required |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | 35–59 | Middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | 60–84 | Last name | Non-blank required |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | 85–134 | Address line 1 | Non-blank required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | 135–184 | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | 185–234 | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | 235–236 | US state code | Validated against 50 state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | 237–239 | Country code | ISO 3166 |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | 240–249 | ZIP/Postal code | Validated: first 3 digits vs state in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | 250–264 | Primary phone | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | 265–279 | Secondary phone | Optional; same area code validation |
| CUST-SSN | PIC 9(09) | Numeric | 280–288 | Social Security Number | 9-digit numeric; **stored in cleartext** |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | 289–308 | Government-issued ID | Driver's license, passport, etc. |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | 309–318 | Date of birth | Format: YYYY-MM-DD; validated via CSUTLDTC |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | 319–328 | EFT/Bank account | For bill payment ACH |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | 329 | Primary card holder flag | 'Y' or 'N' |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | 330–332 | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Filler | 333–500 | Reserved | |

### CUSTREC.cpy — Customer Record (Alternate Layout for Statements)

Simplified customer layout used by CBSTM03A statement generation.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CUST-ID | PIC 9(09) | Numeric | Customer key |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | First name |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Middle name |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Last name |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | State code |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP code |
| FILLER | PIC X(251) | Filler | Pads to 500 |

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

Physical card data. Key: `CARD-NUM`.

| Field Name | PIC Clause | Data Type | Byte Pos | Business Meaning | Validation Rules |
|------------|-----------|-----------|----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | 1–16 | Card number (primary key) | 16-digit Luhn-valid |
| CARD-ACCT-ID | PIC 9(11) | Numeric | 17–27 | Account ID (FK → CVACT01Y) | Must exist in ACCTFILE |
| CARD-CVV-CD | PIC 9(03) | Numeric | 28–30 | CVV security code | 3-digit numeric |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | 31–80 | Name on card | As embossed on physical card |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | 81–90 | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 91 | Active/Inactive flag | 'Y' or 'N' |
| FILLER | PIC X(59) | Filler | 92–150 | Reserved | |

### CVCRD01Y.cpy — Card Screen Working Storage

Working storage for card-related screen programs. Contains display-formatted fields.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CC-WORK-AREA | Group | — | Container for card screen fields |
| CCARD-AID | PIC X(5) | Alphanumeric | AID key captured (ENTER, CLEAR, PFK01–PFK12) |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to XCTL to |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | Next BMS mapset name |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | Next BMS map name |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message for screen display |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return/info message |

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

Master transaction record. Key: `TRAN-ID`.

| Field Name | PIC Clause | Data Type | Byte Pos | Business Meaning | Validation Rules |
|------------|-----------|-----------|----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | 1–16 | Transaction ID | Unique; system-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 17–18 | Transaction type code | FK → TRAN-TYPE-RECORD |
| TRAN-CAT-CD | PIC 9(04) | Numeric | 19–22 | Transaction category | FK → TRAN-CAT-RECORD |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | 23–32 | Transaction source | Online, batch, POS, etc. |
| TRAN-DESC | PIC X(100) | Alphanumeric | 33–132 | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | 133–143 | Transaction amount | Signed; scale=2; **use BigDecimal** |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | 144–152 | Merchant ID | |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 153–202 | Merchant name | |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 203–252 | Merchant city | |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 253–262 | Merchant ZIP | |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | 263–278 | Card number used | FK → CARD-NUM |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric | 279–304 | Origination timestamp | ISO-like format |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric | 305–330 | Processing timestamp | Set during batch posting |
| FILLER | PIC X(20) | Filler | 331–350 | Reserved | |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Input record for daily batch processing. Same layout as CVTRA05Y.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | Transaction ID |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Category code |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | Source |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | Description |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp |
| FILLER | PIC X(20) | Filler | Reserved |

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

Running balance per account/type/category combination.

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY (composite) | — | Group | Composite key | |
| &nbsp;&nbsp;TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | FK → CVACT01Y |
| &nbsp;&nbsp;TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type | FK → CVTRA03Y |
| &nbsp;&nbsp;TRANCAT-CD | PIC 9(04) | Numeric | Category code | FK → CVTRA04Y |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Category balance | Running total |
| FILLER | PIC X(22) | Filler | Reserved | |

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

Interest rates by account group / transaction type / category.

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| DIS-GROUP-KEY (composite) | — | Group | Composite key | |
| &nbsp;&nbsp;DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group | Links to ACCT-GROUP-ID |
| &nbsp;&nbsp;DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type | |
| &nbsp;&nbsp;DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Category code | |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate (%) | Percentage with 2 decimal places |
| FILLER | PIC X(28) | Filler | Reserved | |

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

Lookup table for transaction type codes.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Type code (PK) — e.g. 'SA' (Sale), 'CR' (Credit) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Type description |
| FILLER | PIC X(08) | Filler | Reserved |

### CVTRA04Y.cpy — Transaction Category (RECLN 60)

Lookup table for transaction categories within types.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| TRAN-CAT-KEY (composite) | — | Group | Composite key |
| &nbsp;&nbsp;TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type (FK → CVTRA03Y) |
| &nbsp;&nbsp;TRAN-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description |
| FILLER | PIC X(04) | Filler | Reserved |

### CVTRA07Y.cpy — Transaction Report Structures

Report formatting layouts used by CBTRN03C.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report short name ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report long name ('Daily Transaction Report') |
| REPT-START-DATE | PIC X(10) | Alphanumeric | Report start date |
| REPT-END-DATE | PIC X(10) | Alphanumeric | Report end date |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Detail: transaction ID |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Detail: account ID |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Detail: type code |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Detail: type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Detail: category code |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Detail: category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Detail: source |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Detail: formatted amount |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total |

---

## 5. Authorization Entity (Sub-App: `app/app-authorization-ims-db2-mq/cpy/`)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Root Segment)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| PA-SM-CARD-NUM | PIC X(16) | Alphanumeric | Card number (segment key) |
| PA-SM-IN-PROCESS | PIC X(01) | Alphanumeric | Processing flag |
| PA-SM-AUTH-COUNT | PIC 9(04) | Numeric | Count of pending auths |
| PA-SM-TOTAL-AMT | PIC S9(10)V99 | Signed decimal | Total pending amount |
| PA-SM-LAST-AUTH-DATE | PIC X(10) | Alphanumeric | Date of last authorization |

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Child Segment)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| PA-DT-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference ID |
| PA-DT-AUTH-DATE | PIC X(10) | Alphanumeric | Authorization date |
| PA-DT-AUTH-TIME | PIC X(08) | Alphanumeric | Authorization time |
| PA-DT-AUTH-TYPE | PIC X(04) | Alphanumeric | Auth type (SALE, RFND, etc.) |
| PA-DT-AUTH-AMT | PIC S9(10)V99 | Signed decimal | Authorization amount |
| PA-DT-RESP-CODE | PIC X(02) | Alphanumeric | Response code (AP=Approved, DN=Denied) |
| PA-DT-EXPIRY-DATE | PIC X(10) | Alphanumeric | Auth expiry date |

### CCPAURQY.cpy — Authorization Request (MQ Message)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Request date |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Request time |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry (MMYY) |
| PA-RQ-TRANSACTION-AMT | PIC S9(10)V99 | Signed decimal | Requested amount |

### CCPAURLY.cpy — Authorization Response (MQ Message)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Reason code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### CCPAUERY.cpy — Authorization Error Log

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| ERR-DATE | PIC X(06) | Alphanumeric | Error date |
| ERR-TIME | PIC X(06) | Alphanumeric | Error time |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | Program name |
| ERR-LOCATION | PIC X(04) | Alphanumeric | Error location |
| ERR-LEVEL | PIC X(01) | Alphanumeric | Severity (L=Log, I=Info, W=Warning, C=Critical) |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem (A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File) |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | Primary error code |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | Secondary error code |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | Error description |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Related business key |

### IMS PCB Copybooks

| Copybook | Purpose |
|----------|---------|
| PAUTBPCB.CPY | PCB mask for pending auth IMS database |
| PASFLPCB.CPY | PCB mask for GSAM summary file |
| PADFLPCB.CPY | PCB mask for GSAM detail file |
| IMSFUNCS.cpy | IMS DL/I function code constants (GU, GN, GNP, ISRT, DLET, REPL, etc.) |

---

## 6. User Security Entity

### CSUSR01Y.cpy — User Security Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | User ID (PK) | Unique |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | First name | |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | Last name | |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | Password | ⚠️ **Stored as cleartext PIC X(08)** |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type | 'A' = Admin, 'U' = User |
| SEC-USR-UPD-DT | PIC X(08) | Alphanumeric | Last update date | YYYYMMDD |
| SEC-USR-UPD-BY | PIC X(08) | Alphanumeric | Updated by user | |

---

## 7. Common/Shared Copybooks

### COCOM01Y.cpy — CICS Communication Area (COMMAREA)

Passed between all online programs via CICS XCTL/LINK.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Current user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 'A' (Admin) or 'U' (User) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 0=Enter, 1=Re-enter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Selected customer ID |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | Customer first name |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | Customer middle name |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | Customer last name |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Selected account ID |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Selected card number |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last BMS map displayed |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last BMS mapset |

### CSDAT01Y.cpy — Date/Time Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours |
| WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minutes |
| WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current seconds |
| WS-CURDATE-MM-DD-YY | Group | Display | Formatted MM/DD/YY |
| WS-CURTIME-HH-MM-SS | Group | Display | Formatted HH:MM:SS |
| WS-TIMESTAMP | Group | Display | YYYY-MM-DD HH:MM:SS.mmmmmm |

### CODATECN.cpy — Date Conversion Record

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CODATECN-TYPE | PIC X | Alphanumeric | Input format: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date string |
| CODATECN-OUTTYPE | PIC X | Alphanumeric | Output format: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Output date string |
| CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Conversion error message |

### CSMEN02Y.cpy / COADM02Y.cpy — Menu Option Tables

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CDEMO-MENU-OPT-COUNT | PIC 9(02) | Numeric | Number of menu options (11 for main, 6 for admin) |
| CDEMO-MENU-OPT-NUM | PIC 9(02) | Numeric | Option number |
| CDEMO-MENU-OPT-NAME | PIC X(35) | Alphanumeric | Option display name |
| CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Alphanumeric | Required user type (COMEN02Y only) |

### CSMSG01Y.cpy / CSMSG02Y.cpy — Message Areas

Standard screen message fields used across all online programs.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CCARD-MSG-THANK-YOU | PIC X(38) | Alphanumeric | Thank you message |
| CCARD-MSG-INVALID-KEY | PIC X(38) | Alphanumeric | Invalid key pressed message |
| CCARD-MSG-DEFAULT | PIC X(38) | Alphanumeric | Default message |

### COTTL01Y.cpy — Screen Titles

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CCDA-TITLE01 | PIC X(40) | Alphanumeric | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | Alphanumeric | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | Alphanumeric | Sign-off message |

---

## 8. Validation / Lookup Copybooks

### CSLKPCDY.cpy — Lookup Code Repository (1,318 lines)

Largest copybook. Contains hardcoded validation tables:

| Section | Content | Usage |
|---------|---------|-------|
| NANPA Phone Area Codes | 88-level VALUES for ~350 valid area codes | Validates CUST-PHONE-NUM-1/2 in COACTUPC |
| US State Codes | 88-level VALUES for 50 states + territories | Validates CUST-ADDR-STATE-CD |
| State-ZIP Prefix Map | 88-level VALUES mapping state → valid first 2-3 ZIP digits | Cross-validates ZIP vs state |

### CSUTLDWY.cpy — Date Validation Working Storage (89 lines)

Working storage fields for the CSUTLDTC date utility.

### CSUTLDPY.cpy — Date Validation Procedure Division (375 lines)

COPY REPLACING-compatible procedure division code for inline date validation.

### CSSETATY.cpy — Screen Attribute Setting Macro (30 lines)

Used with COPY REPLACING to set BMS field attributes (color, highlight, protection). COACTUPC uses this 38 times.

### CSSTRPFY.cpy — String/Field Processing (85 lines)

Common procedure division paragraphs for field trimming and formatting.

---

## 9. Export/Import Copybooks

### CVEXPORT.cpy — Export Record (103 lines)

Unified export record used by CBEXPORT/CBIMPORT for data migration.

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| EXPORT-RECORD-TYPE | PIC X(01) | Alphanumeric | 'C'=Customer, 'A'=Account, 'X'=Xref, 'T'=Transaction, 'D'=Card |
| EXPORT-RECORD-DATA | PIC X(500) | Alphanumeric | Raw record data (max entity size) |
| COSTM01.CPY | — | — | Statement generation constants/working storage |

---

## 10. DB2 Module Copybooks (`app/app-transaction-type-db2/cpy/`)

### CSDB2RWY.cpy — DB2 Return Code Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| DB2-SQLCODE-OK | value 0 | Constant | Successful SQL |
| DB2-SQLCODE-NOT-FOUND | value 100 | Constant | Row not found |
| DB2-SQLCODE-DUP | value -803 | Constant | Duplicate key |

### CSDB2RPY.cpy — DB2 Return Code Procedure Division

Inline error handling paragraphs for DB2 SQLCODE checking.

---

## 11. Data Relationships (Entity-Relationship Map)

```
Customer (CVCUS01Y)          Account (CVACT01Y)
  CUST-ID [PK]                 ACCT-ID [PK]
       │                            │
       │ 1:N                   N:1  │
       ▼                            ▼
    Card-XREF (CVACT03Y)────────────┘
    XREF-CARD-NUM [PK]
    XREF-CUST-ID [FK]
    XREF-ACCT-ID [FK]
            │
            │ 1:1
            ▼
       Card (CVACT02Y)
       CARD-NUM [PK]
       CARD-ACCT-ID [FK]
            │
            │ 1:N
            ▼
    Transaction (CVTRA05Y)
    TRAN-ID [PK]
    TRAN-CARD-NUM [FK]
    TRAN-TYPE-CD [FK]──────► TranType (CVTRA03Y) TRAN-TYPE [PK]
    TRAN-CAT-CD [FK]───────► TranCat (CVTRA04Y) TRAN-TYPE-CD+TRAN-CAT-CD [PK]
            │
            ▼
    TranCatBal (CVTRA01Y)    DiscGroup (CVTRA02Y)
    ACCT-ID+TYPE+CAT [PK]    GROUP-ID+TYPE+CAT [PK]
                              DIS-INT-RATE

    Auth Summary (CIPAUSMY)──1:N──► Auth Detail (CIPAUDTY)
    PA-SM-CARD-NUM [PK]             PA-DT-TRANSACTION-ID
```

---

## 12. UNUSED1Y.cpy

Empty/placeholder copybook (10 lines, no fields). Likely reserved for future use.
