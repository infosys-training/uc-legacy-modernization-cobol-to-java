# Data Dictionary — CardDemo COBOL Estate

> **47 copybooks** defining data structures across 5 business entities, UI infrastructure, MQ messaging, IMS segments, and shared utilities.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| ACCT-ID | `PIC 9(11)` | Numeric | 0 | Account identifier (primary key) | Must be unique; 11-digit number |
| ACCT-ACTIVE-STATUS | `PIC X(01)` | Alpha | 11 | Account active flag | `'Y'` = active, `'N'` = inactive |
| ACCT-CURR-BAL | `PIC S9(10)V99` | Signed decimal | 12 | Current account balance | Signed; 2 decimal places |
| ACCT-CREDIT-LIMIT | `PIC S9(10)V99` | Signed decimal | 24 | Maximum credit limit | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | `PIC S9(10)V99` | Signed decimal | 36 | Cash advance credit limit | ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | `PIC X(10)` | Alpha-date | 48 | Account opening date | Format: `YYYY-MM-DD` |
| ACCT-EXPIRAION-DATE | `PIC X(10)` | Alpha-date | 58 | Account expiration date | Format: `YYYY-MM-DD`; must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | `PIC X(10)` | Alpha-date | 68 | Last card reissue date | Format: `YYYY-MM-DD` |
| ACCT-CURR-CYC-CREDIT | `PIC S9(10)V99` | Signed decimal | 78 | Current cycle credit total | Running total of credits this cycle |
| ACCT-CURR-CYC-DEBIT | `PIC S9(10)V99` | Signed decimal | 90 | Current cycle debit total | Running total of debits this cycle |
| ACCT-ADDR-ZIP | `PIC X(10)` | Alpha | 102 | Account holder ZIP code | Validated against CSLKPCDY ZIP prefixes |
| ACCT-GROUP-ID | `PIC X(10)` | Alpha | 112 | Disclosure/interest rate group | Links to CVTRA02Y disclosure rates |
| FILLER | `PIC X(178)` | Alpha | 122 | Reserved | Pad to 300 bytes |

**Used by:** CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, COACCT01

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| CUST-ID | `PIC 9(09)` | Numeric | 0 | Customer identifier (primary key) | 9-digit number; unique |
| CUST-FIRST-NAME | `PIC X(25)` | Alpha | 9 | Customer first name | Required |
| CUST-MIDDLE-NAME | `PIC X(25)` | Alpha | 34 | Customer middle name | Optional |
| CUST-LAST-NAME | `PIC X(25)` | Alpha | 59 | Customer last name | Required |
| CUST-ADDR-LINE-1 | `PIC X(50)` | Alpha | 84 | Address line 1 | Required |
| CUST-ADDR-LINE-2 | `PIC X(50)` | Alpha | 134 | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | `PIC X(50)` | Alpha | 184 | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | `PIC X(02)` | Alpha | 234 | US state code | Validated against 50 state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | `PIC X(03)` | Alpha | 236 | Country code | e.g. `'USA'` |
| CUST-ADDR-ZIP | `PIC X(10)` | Alpha | 239 | ZIP/postal code | Validated against CSLKPCDY ZIP prefix table |
| CUST-PHONE-NUM-1 | `PIC X(15)` | Alpha | 249 | Primary phone | Validated: area code checked against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | `PIC X(15)` | Alpha | 264 | Secondary phone | Optional; same validation as primary |
| CUST-SSN | `PIC 9(09)` | Numeric | 279 | Social Security Number | 9-digit; validated for format in COACTUPC |
| CUST-GOVT-ISSUED-ID | `PIC X(20)` | Alpha | 288 | Government-issued ID number | Optional; driver's license, passport, etc. |
| CUST-DOB-YYYY-MM-DD | `PIC X(10)` | Alpha-date | 308 | Date of birth | Format: `YYYY-MM-DD` |
| CUST-EFT-ACCOUNT-ID | `PIC X(10)` | Alpha | 318 | EFT/bank account for payments | Links to external banking system |
| CUST-PRI-CARD-HOLDER-IND | `PIC X(01)` | Alpha | 328 | Primary cardholder indicator | `'Y'` = primary, `'N'` = authorized user |
| CUST-FICO-CREDIT-SCORE | `PIC 9(03)` | Numeric | 329 | FICO credit score | Range: 300–850 |
| FILLER | `PIC X(168)` | Alpha | 332 | Reserved | Pad to 500 bytes |

**Used by:** CBCUS01C, CBEXPORT, CBIMPORT, CBSTM03A (via CUSTREC), CBTRN01C, COACTUPC, COPAUA0C, COPAUS0C

### CUSTREC.cpy — Customer Record (alternate layout, same structure as CVCUS01Y)

Identical field layout to CVCUS01Y. Used by CBSTM03A for statement generation. Minor formatting difference: `CUST-DOB-YYYYMMDD` vs `CUST-DOB-YYYY-MM-DD`.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| CARD-NUM | `PIC X(16)` | Alpha | 0 | Card number (primary key) | 16-digit PAN |
| CARD-ACCT-ID | `PIC 9(11)` | Numeric | 16 | Foreign key to Account | Must exist in ACCTFILE |
| CARD-CVV-CD | `PIC 9(03)` | Numeric | 27 | Card verification value | 3-digit CVV |
| CARD-EMBOSSED-NAME | `PIC X(50)` | Alpha | 30 | Name embossed on card | Usually FIRST + LAST name |
| CARD-EXPIRAION-DATE | `PIC X(10)` | Alpha-date | 80 | Card expiration date | Format: `YYYY-MM-DD` |
| CARD-ACTIVE-STATUS | `PIC X(01)` | Alpha | 90 | Card active flag | `'Y'` = active, `'N'` = inactive |
| FILLER | `PIC X(59)` | Alpha | 91 | Reserved | Pad to 150 bytes |

**Used by:** CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C

### CVACT03Y.cpy — Card-Account Cross-Reference (RECLN 50)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| XREF-CARD-NUM | `PIC X(16)` | Alpha | 0 | Card number (primary key) | Must exist in CARDFILE |
| XREF-CUST-ID | `PIC 9(09)` | Numeric | 16 | Customer identifier | Foreign key to CUSTFILE |
| XREF-ACCT-ID | `PIC 9(11)` | Numeric | 25 | Account identifier | Foreign key to ACCTFILE |
| FILLER | `PIC X(14)` | Alpha | 36 | Reserved | Pad to 50 bytes |

**Used by:** CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| TRAN-ID | `PIC X(16)` | Alpha | 0 | Transaction identifier (primary key) | System-generated; unique |
| TRAN-TYPE-CD | `PIC X(02)` | Alpha | 16 | Transaction type code | Foreign key to CVTRA03Y |
| TRAN-CAT-CD | `PIC 9(04)` | Numeric | 18 | Transaction category code | Foreign key to CVTRA04Y |
| TRAN-SOURCE | `PIC X(10)` | Alpha | 22 | Transaction source | e.g. `'ONLINE'`, `'POS'`, `'ATM'` |
| TRAN-DESC | `PIC X(100)` | Alpha | 32 | Transaction description | Free-text merchant/purpose |
| TRAN-AMT | `PIC S9(09)V99` | Signed decimal | 132 | Transaction amount | Positive = charge, Negative = credit |
| TRAN-MERCHANT-ID | `PIC 9(09)` | Numeric | 143 | Merchant identifier | MCC or internal ID |
| TRAN-MERCHANT-NAME | `PIC X(50)` | Alpha | 152 | Merchant name | Display name |
| TRAN-MERCHANT-CITY | `PIC X(50)` | Alpha | 202 | Merchant city | Location |
| TRAN-MERCHANT-ZIP | `PIC X(10)` | Alpha | 252 | Merchant ZIP code | Postal code |
| TRAN-CARD-NUM | `PIC X(16)` | Alpha | 262 | Card number used | Foreign key to CARDFILE |
| TRAN-ORIG-TS | `PIC X(26)` | Alpha-timestamp | 278 | Origination timestamp | `YYYY-MM-DD-HH.MM.SS.FFFFFF` |
| TRAN-PROC-TS | `PIC X(26)` | Alpha-timestamp | 304 | Processing timestamp | Set when batch posts the transaction |
| FILLER | `PIC X(20)` | Alpha | 330 | Reserved | Pad to 350 bytes |

**Used by:** CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same layout as CVTRA05Y with `DALYTRAN-` prefix. Used as input staging file before posting to master. Fields: DALYTRAN-ID, DALYTRAN-TYPE-CD, DALYTRAN-CAT-CD, DALYTRAN-SOURCE, DALYTRAN-DESC, DALYTRAN-AMT, DALYTRAN-MERCHANT-ID/NAME/CITY/ZIP, DALYTRAN-CARD-NUM, DALYTRAN-ORIG-TS, DALYTRAN-PROC-TS.

**Used by:** CBTRN01C, CBTRN02C

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| TRANCAT-ACCT-ID | `PIC 9(11)` | Numeric | 0 | Account ID (part of composite key) | FK to ACCTFILE |
| TRANCAT-TYPE-CD | `PIC X(02)` | Alpha | 11 | Transaction type (part of key) | FK to TRANTYPE |
| TRANCAT-CD | `PIC 9(04)` | Numeric | 13 | Category code (part of key) | FK to TRANCATG |
| TRAN-CAT-BAL | `PIC S9(09)V99` | Signed decimal | 17 | Balance for this account/type/category | Running total |
| FILLER | `PIC X(22)` | Alpha | 28 | Reserved | Pad to 50 bytes |

**Used by:** CBACT04C, CBTRN02C

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| DIS-ACCT-GROUP-ID | `PIC X(10)` | Alpha | 0 | Account group ID (part of key) | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | `PIC X(02)` | Alpha | 10 | Transaction type (part of key) | FK to TRANTYPE |
| DIS-TRAN-CAT-CD | `PIC 9(04)` | Numeric | 12 | Category code (part of key) | FK to TRANCATG |
| DIS-INT-RATE | `PIC S9(04)V99` | Signed decimal | 16 | Interest rate for this group/type/category | Annual percentage rate |
| FILLER | `PIC X(28)` | Alpha | 22 | Reserved | Pad to 50 bytes |

**Used by:** CBACT04C

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| TRAN-TYPE | `PIC X(02)` | Alpha | 0 | Transaction type code (primary key) | 2-char code, e.g. `'SA'`, `'PU'` |
| TRAN-TYPE-DESC | `PIC X(50)` | Alpha | 2 | Type description | e.g. `'Sale'`, `'Purchase'` |
| FILLER | `PIC X(08)` | Alpha | 52 | Reserved | Pad to 60 bytes |

**Used by:** CBTRN03C, COTRTLIC, COTRTUPC

### CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| TRAN-TYPE-CD | `PIC X(02)` | Alpha | 0 | Transaction type (part of composite key) | FK to CVTRA03Y |
| TRAN-CAT-CD | `PIC 9(04)` | Numeric | 2 | Category code (part of key) | 4-digit |
| TRAN-CAT-TYPE-DESC | `PIC X(50)` | Alpha | 6 | Category description | e.g. `'Grocery'`, `'Gas Station'` |
| FILLER | `PIC X(04)` | Alpha | 56 | Reserved | Pad to 60 bytes |

**Used by:** CBTRN02C, CBTRN03C, COTRTUPC

### CVTRA07Y.cpy — Transaction Report Layout

Report formatting structures (not a data record):

| Structure | Fields | Purpose |
|-----------|--------|---------|
| REPORT-NAME-HEADER | REPT-SHORT-NAME (`'DALYREPT'`), REPT-LONG-NAME, REPT-DATE-HEADER, REPT-START-DATE, REPT-END-DATE | Report header line |
| TRANSACTION-DETAIL-REPORT | TRAN-REPORT-TRANS-ID, ACCOUNT-ID, TYPE-CD/DESC, CAT-CD/DESC, SOURCE, AMT (`PIC -ZZZ,ZZZ,ZZZ.ZZ`) | Detail line |
| TRANSACTION-HEADER-1/2 | Column headers + separator line (`PIC X(133) VALUE ALL '-'`) | Page header |
| REPORT-PAGE-TOTALS | REPT-PAGE-TOTAL (`PIC +ZZZ,ZZZ,ZZZ.ZZ`) | Page subtotal |
| REPORT-ACCOUNT-TOTALS | REPT-ACCOUNT-TOTAL | Account subtotal |
| REPORT-GRAND-TOTALS | REPT-GRAND-TOTAL | Grand total |

**Used by:** CBTRN03C

---

## 5. User / Security Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field | PIC Clause | Type | Offset | Business Meaning | Validation |
|-------|-----------|------|--------|-----------------|------------|
| SEC-USR-ID | `PIC X(08)` | Alpha | 0 | User ID (primary key) | 8-char login ID |
| SEC-USR-FNAME | `PIC X(20)` | Alpha | 8 | User first name | Required |
| SEC-USR-LNAME | `PIC X(20)` | Alpha | 28 | User last name | Required |
| SEC-USR-PWD | `PIC X(08)` | Alpha | 48 | Password (plain text) | **RISK:** Stored unencrypted |
| SEC-USR-TYPE | `PIC X(01)` | Alpha | 56 | User type | `'A'` = Admin, `'U'` = Regular |
| SEC-USR-FILLER | `PIC X(23)` | Alpha | 57 | Reserved | Pad to 80 bytes |

**Used by:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COACTUPC, COACTVWC, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC

---

## 6. Authorization Entity (IMS)

### CIPAUSMY.cpy — Pending Authorization Summary Segment

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| PA-SM-CARD-NUM | `PIC X(16)` | Alpha | Card number (segment key) |
| PA-SM-AUTH-COUNT | `PIC 9(04)` | Numeric | Number of pending authorizations |
| PA-SM-TOTAL-AMT | `PIC S9(10)V99` | Signed decimal | Total pending amount |

**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C

### CIPAUDTY.cpy — Pending Authorization Detail Segment

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| PA-DT-AUTH-ID | `PIC X(15)` | Alpha | Authorization ID |
| PA-DT-AUTH-DATE | `PIC X(06)` | Alpha-date | Authorization date (YYMMDD) |
| PA-DT-AUTH-TIME | `PIC X(06)` | Alpha-time | Authorization time (HHMMSS) |
| PA-DT-AUTH-AMT | `PIC S9(10)V99` | Signed decimal | Authorization amount |
| PA-DT-AUTH-STATUS | `PIC X(01)` | Alpha | Status: `'A'`=approved, `'D'`=declined, `'F'`=fraud |
| PA-DT-EXPIRY-DATE | `PIC X(06)` | Alpha-date | Authorization expiry date |

**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C

### CCPAURQY.cpy — Authorization Request (MQ Message)

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| PA-RQ-AUTH-DATE | `PIC X(06)` | Alpha-date | Request date |
| PA-RQ-AUTH-TIME | `PIC X(06)` | Alpha-time | Request time |
| PA-RQ-CARD-NUM | `PIC X(16)` | Alpha | Card number |
| PA-RQ-AUTH-TYPE | `PIC X(04)` | Alpha | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | `PIC X(04)` | Alpha | Card expiry (YYMM) |
| PA-RQ-MESSAGE-TYPE | `PIC X(06)` | Alpha | Message type code |
| PA-RQ-MESSAGE-SOURCE | `PIC X(10)` | Alpha | Originating source |
| PA-RQ-TRANSACTION-AMT | `PIC S9(09)V99` | Signed decimal | Requested amount |
| PA-RQ-MERCHANT-ID | `PIC 9(09)` | Numeric | Merchant ID |

**Used by:** COPAUA0C

### CCPAURLY.cpy — Authorization Response (MQ Message)

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| PA-RL-CARD-NUM | `PIC X(16)` | Alpha | Card number |
| PA-RL-TRANSACTION-ID | `PIC X(15)` | Alpha | Transaction reference |
| PA-RL-AUTH-ID-CODE | `PIC X(06)` | Alpha | Authorization code |
| PA-RL-AUTH-RESP-CODE | `PIC X(02)` | Alpha | Response code (`'00'`=approved) |
| PA-RL-AUTH-RESP-REASON | `PIC X(04)` | Alpha | Reason code |
| PA-RL-APPROVED-AMT | `PIC +9(10).99` | Edited numeric | Approved amount |

**Used by:** COPAUA0C

### CCPAUERY.cpy — Authorization Error Log

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| ERR-DATE | `PIC X(06)` | Alpha-date | Error date |
| ERR-TIME | `PIC X(06)` | Alpha-time | Error time |
| ERR-APPLICATION | `PIC X(08)` | Alpha | Application name |
| ERR-PROGRAM | `PIC X(08)` | Alpha | Program name |
| ERR-LOCATION | `PIC X(04)` | Alpha | Error location in code |
| ERR-LEVEL | `PIC X(01)` | Alpha | Severity: `'L'`=log, `'I'`=info, `'W'`=warning, `'C'`=critical |
| ERR-SUBSYSTEM | `PIC X(01)` | Alpha | Source: `'A'`=app, `'C'`=CICS, `'I'`=IMS, `'D'`=DB2, `'M'`=MQ, `'F'`=file |
| ERR-CODE-1 | `PIC X(09)` | Alpha | Primary error code |
| ERR-CODE-2 | `PIC X(09)` | Alpha | Secondary error code |
| ERR-MESSAGE | `PIC X(50)` | Alpha | Error message text |
| ERR-EVENT-KEY | `PIC X(20)` | Alpha | Related event identifier |

**Used by:** COPAUA0C

---

## 7. Export / Migration Entity

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

Header fields common to all record types:

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| EXPORT-REC-TYPE | `PIC X(1)` | Alpha | Record type: `'C'`=customer, `'A'`=account, `'T'`=transaction, `'X'`=xref, `'D'`=card |
| EXPORT-TIMESTAMP | `PIC X(26)` | Alpha-timestamp | Export timestamp |
| EXPORT-SEQUENCE-NUM | `PIC 9(9) COMP` | Binary | Record sequence within type |
| EXPORT-BRANCH-ID | `PIC X(4)` | Alpha | Originating branch |
| EXPORT-REGION-CODE | `PIC X(5)` | Alpha | Geographic region code |
| EXPORT-RECORD-DATA | `PIC X(460)` | Alpha | Entity-specific data (REDEFINES below) |

REDEFINES structures replicate entity fields from CVCUS01Y, CVACT01Y, CVTRA05Y, CVACT02Y, CVACT03Y with optional COMP/COMP-3 optimizations for numeric fields.

**Used by:** CBEXPORT, CBIMPORT

---

## 8. UI Infrastructure Copybooks

### COCOM01Y.cpy — Communication Area (COMMAREA)

| Field | PIC Clause | Type | Business Meaning |
|-------|-----------|------|-----------------|
| CDEMO-FROM-TRANID | `PIC X(04)` | Alpha | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | `PIC X(08)` | Alpha | Source program name |
| CDEMO-TO-TRANID | `PIC X(04)` | Alpha | Target transaction ID |
| CDEMO-TO-PROGRAM | `PIC X(08)` | Alpha | Target program name |
| CDEMO-USER-ID | `PIC X(08)` | Alpha | Current user ID |
| CDEMO-USER-TYPE | `PIC X(01)` | Alpha | `'A'` = admin (88 CDEMO-USRTYP-ADMIN), `'U'` = user |
| CDEMO-PGM-CONTEXT | `PIC 9(01)` | Numeric | 0 = initial entry, 1 = re-entry |
| CDEMO-CUST-ID | `PIC 9(09)` | Numeric | Selected customer ID |
| CDEMO-CUST-FNAME/MNAME/LNAME | `PIC X(25)` each | Alpha | Customer name fields |
| CDEMO-ACCT-ID | `PIC 9(11)` | Numeric | Selected account ID |
| CDEMO-ACCT-STATUS | `PIC X(01)` | Alpha | Account status |
| CDEMO-CARD-NUM | `PIC 9(16)` | Numeric | Selected card number |
| CDEMO-LAST-MAP / CDEMO-LAST-MAPSET | `PIC X(7)` each | Alpha | Last displayed BMS map/mapset |

**Used by:** All 21 CICS programs

### COMEN02Y.cpy — Main Menu Options

Defines the 11 user-facing menu options with program routing:

| Option | Name | Target Program | User Type |
|--------|------|---------------|-----------|
| 1 | Account View | COACTVWC | U |
| 2 | Account Update | COACTUPC | U |
| 3 | Credit Card List | COCRDLIC | U |
| 4 | Credit Card View | COCRDSLC | U |
| 5 | Credit Card Update | COCRDUPC | U |
| 6 | Transaction List | COTRN00C | U |
| 7 | Transaction View | COTRN01C | U |
| 8 | Transaction Add | COTRN02C | U |
| 9 | Transaction Reports | CORPT00C | U |
| 10 | Bill Payment | COBIL00C | U |
| 11 | Pending Authorization View | COPAUS0C | U |

**Used by:** COMEN01C

### COADM02Y.cpy — Admin Menu Options

Defines 6 admin menu options:

| Option | Name | Target Program |
|--------|------|---------------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |
| 5 | Transaction Type List/Update (Db2) | COTRTLIC |
| 6 | Transaction Type Maintenance (Db2) | COTRTUPC |

**Used by:** COADM01C

### COTTL01Y.cpy — Screen Titles

| Field | PIC Clause | Value | Purpose |
|-------|-----------|-------|---------|
| CCDA-TITLE01 | `PIC X(40)` | `'AWS Mainframe Modernization'` | Header line 1 |
| CCDA-TITLE02 | `PIC X(40)` | `'CardDemo'` | Header line 2 |
| CCDA-THANK-YOU | `PIC X(40)` | `'Thank you for using CCDA application...'` | Logout message |

**Used by:** All CICS programs

### CSDAT01Y.cpy — Date/Time Working Storage

Defines `WS-DATE-TIME` structure with fields for: current date (YYYYMMDD), current time (HHMMSScc), formatted date (MM/DD/YY), formatted time (HH:MM:SS), and DB2-format timestamp (YYYY-MM-DD HH:MM:SS.FFFFFF).

**Used by:** All CICS programs

### CSMSG01Y.cpy — Common Messages

| Field | Value | Purpose |
|-------|-------|---------|
| CCDA-MSG-THANK-YOU | `'Thank you for using CardDemo application...'` | Exit message |
| CCDA-MSG-INVALID-KEY | `'Invalid key pressed. Please see below...'` | Error message |

**Used by:** All CICS programs

### CSMSG02Y.cpy — Abend Data

| Field | PIC Clause | Purpose |
|-------|-----------|---------|
| ABEND-CODE | `PIC X(4)` | Abend code |
| ABEND-CULPRIT | `PIC X(8)` | Program causing abend |
| ABEND-REASON | `PIC X(50)` | Reason text |
| ABEND-MSG | `PIC X(72)` | Formatted abend message |

**Used by:** COACTUPC, COCRDUPC, COTRTUPC, COPAUS0C, COPAUS1C

### CVCRD01Y.cpy — Common Card Work Areas

Defines `CC-WORK-AREAS` for CICS card-related programs:
- `CCARD-AID`: AID key indicator with 88-level conditions (ENTER, CLEAR, PFK01–PFK12)
- `CCARD-NEXT-PROG`, `CCARD-NEXT-MAPSET`, `CCARD-NEXT-MAP`: navigation control
- `CCARD-ERROR-MSG`, `CCARD-RETURN-MSG`: message display areas
- `CC-ACCT-ID`, `CC-CARD-NUM`, `CC-CUST-ID`: key lookup fields with numeric REDEFINES

**Used by:** COADM01C, COMEN01C, COTRTLIC, COTRTUPC

### CODATECN.cpy — Date Conversion Record

Date fields used for assembler COBDATFT date formatting calls.

**Used by:** CBACT01C

---

## 9. Validation Lookup Copybooks

### CSLKPCDY.cpy — State/ZIP/Phone Lookup Tables (1,318 lines)

The largest copybook. Contains:
- **50 US state codes** with state names and valid ZIP prefixes
- **ZIP code prefix ranges** for each state (3-digit prefixes mapped to states)
- **NANPA area code list** for phone number validation
- Used extensively by COACTUPC for field-level validation

### CSUTLDPY.cpy — Date Validation Procedures (375 lines)

Reusable PERFORM paragraphs for date validation:
- Leap year calculation
- Month/day range validation
- Date comparison logic
- Used with COPY REPLACING to parameterize field prefixes

### CSUTLDWY.cpy — Date Validation Working Storage

Working storage fields for date validation routines. Companion to CSUTLDPY.

**Used by:** COACTUPC, COTRTUPC

### CSSETATY.cpy — Screen Attribute Setting (referenced 39 times)

Most-referenced copybook in the estate. Used with `COPY REPLACING` to set BMS field attributes (DFHBMASK values) for screen fields. Each invocation replaces field-name prefixes to apply attribute settings to different screen fields.

**Used by:** COACTUPC (×3 invocations), COCRDUPC, COTRTUPC, and other CICS programs via REPLACING pattern

### CSSTRPFY.cpy — String Stripping Procedures

Utility PERFORM paragraphs for stripping leading/trailing spaces from fields.

**Used by:** COTRTLIC, COTRTUPC

---

## 10. DB2 Copybooks (Sub-Application)

### CSDB2RPY.cpy — DB2 Common Procedures

Reusable DB2 paragraphs:
- `9998-PRIMING-QUERY`: connectivity test via `SELECT 1 FROM SYSIBM.SYSDUMMY1`
- `9999-FORMAT-DB2-MESSAGE`: format SQLCA errors using DSNTIAC utility
- Provides standardized DB2 error handling for COTRTLIC and COTRTUPC

### CSDB2RWY.cpy — DB2 Working Storage

Working storage for DB2 operations: SQLCODE display fields, DSNTIAC formatted output area, error flags.

**Used by:** COTRTLIC, COTRTUPC

### DCLTRTYP / DCLTRCAT — DB2 DCLGEN Structures

SQL INCLUDE members for DB2 table declarations:
- **TRTYP**: Transaction type table (TYPE-CODE CHAR(2), TYPE-DESC VARCHAR(50))
- **TRCAT**: Transaction category table (TYPE-CODE CHAR(2), CAT-CODE INT, CAT-DESC VARCHAR(50))

---

## 11. MQ Copybooks

Standard IBM MQ COBOL copybooks used by COPAUA0C, COACCT01, CODATE01:

| Copybook | Purpose |
|----------|---------|
| CMQV | MQ constants and return codes |
| CMQODV | MQ Object Descriptor (queue names, object types) |
| CMQMDV | MQ Message Descriptor (message ID, correlation ID, format) |
| CMQTML | MQ Trigger Message Layout |
| CMQPMOV | MQ Put Message Options |
| CMQGMOV | MQ Get Message Options |

---

## 12. BMS Map Copybooks

Generated from BMS map definitions in `app/bms/`. Each CICS program has a paired BMS copybook:

| Copybook | BMS Source | Screen Purpose |
|----------|-----------|----------------|
| COSGN00 | COSGN00.bms | Sign-on screen |
| COADM01 | COADM01.bms | Admin menu |
| COMEN01 | COMEN01.bms | Main menu |
| COACTVW | COACTVW.bms | Account view |
| COACTUP | COACTUP.bms | Account update |
| COBIL00 | COBIL00.bms | Bill payment |
| COCRDLI | COCRDLI.bms | Card list |
| COCRDSL | COCRDSL.bms | Card detail |
| COCRDUP | COCRDUP.bms | Card update |
| COTRN00 | COTRN00.bms | Transaction list |
| COTRN01 | COTRN01.bms | Transaction view |
| COTRN02 | COTRN02.bms | Transaction add |
| CORPT00 | CORPT00.bms | Report request |
| COUSR00–03 | COUSR0x.bms | User CRUD screens |
| COPAU00/01 | COPAU0x.bms | Auth summary/detail |
| COTRTLI | COTRTLI.bms | DB2 tran type list |
| COTRTUP | COTRTUP.bms | DB2 tran type update |
| COSTM01 | — | Statement format |

---

## 13. Entity Relationship Summary

```
Customer (CVCUS01Y)
    │
    ├──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
    │                                             │
    │                                             ├── Disclosure Group (CVTRA02Y)
    │                                             │       via ACCT-GROUP-ID
    │                                             │
    │                                             └── Tran Cat Balance (CVTRA01Y)
    │                                                     via ACCT-ID + TYPE + CAT
    │
    └──► Card (CVACT02Y)
              │
              └──► Transaction (CVTRA05Y) ──► Tran Type (CVTRA03Y)
                                           ──► Tran Category (CVTRA04Y)
                                           
User Security (CSUSR01Y) — standalone; no FK relationships

Pending Auth Summary (CIPAUSMY) ──1:N──► Pending Auth Detail (CIPAUDTY)
    └── keyed by CARD-NUM; links to Card entity
```
