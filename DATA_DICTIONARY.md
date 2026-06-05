# DATA DICTIONARY — CardDemo Copybook Catalog

## Overview

| Metric | Value |
|--------|-------|
| Total Copybooks | 47 |
| Main Copybooks (app/cpy/) | 27 |
| Sub-App Copybooks | 20 |
| Business Entities Defined | 7 core + supporting |

---

## 1. ACCOUNT Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric (unsigned) | Account identifier — primary key | Must be > 0; unique across estate |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account status flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12 display bytes) | Current account balance | Implied 2 decimal places; signed (overpunch trailing) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Maximum credit limit | Must be ≥ 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (date) | Account opening date | Format: CCYYMMDD (validated by CSUTLDPY) |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (date) | Account expiration date | Must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (date) | Card reissue date | |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total | Running total for billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total | Running total for billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | Validated against CSLKPCDY state-ZIP table |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/interest rate group | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| FILLER | PIC X(178) | Padding | Reserved space | |

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID (part of composite key) | FK to CVACT01Y.ACCT-ID |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code (part of key) | FK to CVTRA03Y.TRAN-TYPE |
| TRANCAT-CD | PIC 9(04) | Numeric | Category code (part of key) | FK to CVTRA04Y.TRAN-CAT-CD |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Balance for this account/type/category | Accumulated from posted transactions |
| FILLER | PIC X(22) | Padding | Reserved | |

### CVTRA02Y.cpy — Disclosure Group / Interest Rate (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Group identifier (key) | Links from ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type (part of key) | |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (part of key) | |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate percentage | Applied during INTCALC batch |
| FILLER | PIC X(28) | Padding | Reserved | |

---

## 2. CUSTOMER Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric | Customer identifier — primary key | Must be > 0; unique |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | Non-blank when creating |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | Non-blank when creating |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated against CSLKPCDY 88-level state codes |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | First 2 digits validated against state (CSLKPCDY) |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated against NANPA list (CSLKPCDY) |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Same NANPA validation |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9 digits; validated for format in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID | |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (date) | Date of birth | CCYYMMDD; validated via CSUTLDPY |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account ID | For bill payment |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder indicator | 'Y'/'N' |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Padding | Reserved | |

---

## 3. CARD Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Card number — primary key | 16 digits; Luhn check (implied) |
| CARD-ACCT-ID | PIC 9(11) | Numeric | Account ID — foreign key | FK to CVACT01Y.ACCT-ID |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3 digits |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on card | |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (date) | Card expiration date | Must be future date |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card active status | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Padding | Reserved | |

### CVACT03Y.cpy — Card-Account Cross-Reference (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number — key | FK to CVACT02Y.CARD-NUM |
| XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID | FK to CVCUS01Y.CUST-ID |
| XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID | FK to CVACT01Y.ACCT-ID |
| FILLER | PIC X(14) | Padding | Reserved | |

---

## 4. TRANSACTION Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Transaction ID — primary key | System-generated unique ID |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to CVTRA03Y.TRAN-TYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to CVTRA04Y |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source/channel | |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount | Signed; 2 decimal places |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card used for transaction | FK to CVACT02Y.CARD-NUM |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric (timestamp) | Transaction origination timestamp | YYYY-MM-DD HH:MM:SS.ffffff |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric (timestamp) | Transaction processing timestamp | Set by CBTRN02C at posting |
| FILLER | PIC X(20) | Padding | Reserved | |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | Daily transaction ID | Same layout as CVTRA05Y |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category | |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | Source channel | |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | Description | |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Amount | |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number | |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric (timestamp) | Origination timestamp | |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric (timestamp) | Processing timestamp | |
| FILLER | PIC X(20) | Padding | Reserved | |

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code — key | 2-char code (e.g., 'SA'=sale, 'CR'=credit) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Type description | |
| FILLER | PIC X(08) | Padding | Reserved | |

### CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Type code (composite key part 1) | FK to CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (composite key part 2) | |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | |
| FILLER | PIC X(04) | Padding | Reserved | |

---

## 5. SECURITY / USER Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | User ID — primary key | Up to 8 characters |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name | |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name | |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | User password (PLAIN TEXT) | ⚠️ Stored unencrypted |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type | 'A' = Admin, 'U' = User |
| SEC-USR-FILLER | PIC X(23) | Padding | Reserved | |

---

## 6. AUTHORIZATION Entity (Sub-App)

### CIPAUSMY.cpy — Pending Authorization Summary

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-ACCT-ID | PIC 9(11) | Numeric | Account ID (IMS root segment key) | FK to CVACT01Y |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number | |
| PA-TOTAL-AUTH-AMT | PIC S9(09)V99 | Signed decimal | Total pending authorization amount | |
| PA-AUTH-COUNT | PIC 9(05) | Numeric | Number of pending authorizations | |
| PA-AUTH-STATUS | PIC X(02) | Alphanumeric | Status of authorization batch | |

### CIPAUDTY.cpy — Pending Authorization Detail

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-DTL-AUTH-ID | PIC X(15) | Alphanumeric | Authorization ID (IMS child segment key) | Unique within parent |
| PA-DTL-AUTH-DATE | PIC X(10) | Alphanumeric (date) | Authorization date | CCYYMMDD |
| PA-DTL-AUTH-AMT | PIC S9(09)V99 | Signed decimal | Individual auth amount | |
| PA-DTL-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | |
| PA-DTL-AUTH-STATUS | PIC X(02) | Alphanumeric | Individual auth status | 'AP'=approved, 'DN'=denied, 'PD'=pending |

### CCPAURQY.cpy — Authorization Request (MQ Message)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Request date | YYMMDD |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Request time | HHMMSS |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number | |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type | |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry (YYMM) | |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type code | |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Originating system | |
| PA-RQ-PROCESSING-CODE | PIC X(06) | Alphanumeric | Processing instruction | |

### CCPAURLY.cpy — Authorization Response (MQ Message)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number | |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference | |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID assigned | |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code | '00'=approved, '05'=declined |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Decline reason code | |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount | |

### CCPAUERY.cpy — Authorization Error Log

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| ERR-DATE | PIC X(06) | Alphanumeric | Error date | YYMMDD |
| ERR-TIME | PIC X(06) | Alphanumeric | Error time | HHMMSS |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name | |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | Program generating error | |
| ERR-LOCATION | PIC X(04) | Alphanumeric | Error location in code | |
| ERR-LEVEL | PIC X(01) | Alphanumeric | Severity level | 88: 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem identifier | 88: 'A'=App, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=File |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | Primary error code | |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | Secondary error code | |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | Error description | |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Key of record causing error | |

---

## 7. REPORTING & EXPORT Structures

### CVTRA07Y.cpy — Transaction Report Layout

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report short name ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report full name |
| REPT-DATE-HEADER | PIC X(12) | Alphanumeric | Date range label |
| REPT-START-DATE | PIC X(10) | Alphanumeric | Report start date |
| REPT-END-DATE | PIC X(10) | Alphanumeric | Report end date |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID (detail line) |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Source |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total |

### COSTM01.CPY — Statement Transaction Layout (keyed for CBSTM03A)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number (key part 1) |
| TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID (key part 2) |
| TRNX-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type |
| TRNX-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRNX-SOURCE | PIC X(10) | Alphanumeric | Source |
| TRNX-DESC | PIC X(100) | Alphanumeric | Description |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name |
| TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP |

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | Record type discriminator ('C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card) |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number (COMP = 4 bytes binary) |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Region code |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Record payload (REDEFINES per type) |

---

## 8. UI / COMMUNICATION Copybooks

### COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type ('A'=Admin, 'U'=User) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 0=first entry, 1=re-entry |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer context |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alphanumeric | Customer name context |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account context |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Current account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card context |
| CDEMO-LAST-MAP/MAPSET | PIC X(7) each | Alphanumeric | BMS map tracking |

### COMEN02Y.cpy — Main Menu Options Table

Defines 11 menu options (Account View/Update, Card List/View/Update, Transaction List/View/Add, Reports, Bill Payment, Pending Auth) with program names and user-type access control.

### COADM02Y.cpy — Admin Menu Options Table

Defines 6 admin options (User List/Add/Update/Delete, Transaction Type List, Transaction Type Maintenance) with program names.

---

## 9. UTILITY Copybooks

| Copybook | Purpose | Key Contents |
|----------|---------|--------------|
| COTTL01Y.cpy | Screen title | Application title strings ("AWS Mainframe Modernization - CardDemo") |
| CSDAT01Y.cpy | Date/time working storage | WS-CURDATE (PIC 9(08)), WS-CURTIME, WS-TIMESTAMP structures |
| CSMSG01Y.cpy | Message area | Standard message display fields for BMS screens |
| CSMSG02Y.cpy | Extended messages | Additional message fields for multi-message screens |
| CSSETATY.cpy | Screen attribute macro | Used with COPY REPLACING to set BMS field attributes (color, protection, highlight) |
| CSSTRPFY.cpy | PF-key processing | Stores and evaluates EIBAID (ENTER, CLEAR, PF1–PF24) into named flags |
| CSLKPCDY.cpy | Lookup code repository | NANPA phone area codes (88-level VALUES), US state codes, state-ZIP prefix validation |
| CSUTLDWY.cpy | Date validation (working storage) | Flags and work fields for date edit paragraphs |
| CSUTLDPY.cpy | Date validation (procedure) | Reusable paragraphs: EDIT-DATE-CCYYMMDD, EDIT-YEAR-CCYY, EDIT-MONTH, EDIT-DAY, EDIT-DATE-OF-BIRTH |
| CODATECN.cpy | Date conversion record | Input/output structure for assembler date formatter (COBDATFT); handles YYYYMMDD ↔ YYYY-MM-DD |
| CVCRD01Y.cpy | Card work areas | AID flags, navigation fields (CCARD-NEXT-PROG, CCARD-NEXT-MAP), account/card/customer IDs |
| CUSTREC.cpy | Alternate customer layout | Simplified customer record structure |
| UNUSED1Y.cpy | Deprecated user structure | Same layout as CSUSR01Y — marked unused |

---

## 10. DB2 Copybooks (Sub-App: app/app-transaction-type-db2/cpy/)

| Copybook | Purpose |
|----------|---------|
| CSDB2RPY.cpy | DB2 response/reply working storage for COTRTUPC |
| CSDB2RWY.cpy | DB2 read/write working storage for COTRTLIC |
| DCLTRTYP.dcl | DB2 DECLARE for TRNTYPE table (TR_TYPE, TR_DESC) |
| DCLTRCAT.dcl | DB2 DECLARE for TRNTYCAT table (category catalog) |

---

## 11. IMS Copybooks (Sub-App: app/app-authorization-ims-db2-mq/cpy/)

| Copybook | Purpose |
|----------|---------|
| IMSFUNCS.cpy | IMS DL/I function codes (GU, GN, GNP, ISRT, DLET, REPL, CHKP, SCHD, TERM) |
| PAUTBPCB.CPY | IMS PCB mask for pending authorization database |
| PASFLPCB.CPY | IMS PCB mask for summary flat-file output |
| PADFLPCB.CPY | IMS PCB mask for detail flat-file output |

---

## Entity Relationship Summary

```
Customer (CVCUS01Y)
    │
    ├── 1:N → Card-XREF (CVACT03Y) ── N:1 → Account (CVACT01Y)
    │                                            │
    │                                            ├── Disclosure Group (CVTRA02Y)
    │                                            │     via ACCT-GROUP-ID
    │                                            │
    │                                            └── Category Balance (CVTRA01Y)
    │                                                  per type+category
    │
    └── Card (CVACT02Y)
          │
          └── Transaction (CVTRA05Y) ── → Tran Type (CVTRA03Y)
                                        └── → Tran Category (CVTRA04Y)

IMS Hierarchy (Pending Authorizations):
    Auth Summary (CIPAUSMY) — root segment, keyed by ACCT-ID
        └── Auth Detail (CIPAUDTY) — child segment, per authorization
```
