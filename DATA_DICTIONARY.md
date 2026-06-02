# DATA DICTIONARY — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System  
> **Total Copybooks:** 47 (27 main `app/cpy/` + 20 sub-application)  
> **Generated:** 2026-06-02

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Master Record (300 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric | Primary key — unique account identifier | 11-digit number, must exist in KSDS |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account status flag | 'Y' = Active, 'N' = Inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed Decimal | Current account balance | Signed; negative = credit |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Maximum credit limit | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Cash advance credit limit | ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Date String | Date account was opened | Format: YYYY-MM-DD |
| ACCT-EXPIRAION-DATE | PIC X(10) | Date String | Account expiration date | Format: YYYY-MM-DD; must be > OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Date String | Last card reissue date | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed Decimal | Current billing cycle credits | Running total, reset per cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed Decimal | Current billing cycle debits | Running total, reset per cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account billing ZIP code | Validated against state+ZIP table in CSLKPCDY |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Discount/interest rate group identifier | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| FILLER | PIC X(178) | Reserved | Unused space for future expansion | — |

**Used by:** 13+ programs (CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COPAUA0C, COPAUS0C, COTRN02C, CBSTM03A, CBTRN01C)

### 1.2 CVTRA01Y.cpy — Transaction Category Balance Record (50 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY (group) | — | Group | Composite key | — |
| → TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID (FK to CVACT01Y) | Must exist in Account file |
| → TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Must exist in TRAN-TYPE-RECORD |
| → TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code | Must exist in TRAN-CAT-RECORD |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed Decimal | Running balance for this category | Updated by CBTRN02C posting |
| FILLER | PIC X(22) | Reserved | Unused | — |

**Used by:** CBACT04C, CBTRN02C

### 1.3 CVTRA02Y.cpy — Discount Group Record (50 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| DIS-GROUP-KEY (group) | — | Group | Composite key | — |
| → DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Group ID (matches ACCT-GROUP-ID) | Links to CVACT01Y |
| → DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type | FK to CVTRA03Y |
| → DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category | FK to CVTRA04Y |
| DIS-INT-RATE | PIC S9(04)V99 | Signed Decimal | Interest rate (annual %) | Typically 0.00–29.99 |
| FILLER | PIC X(28) | Reserved | Unused | — |

**Used by:** CBACT04C

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Master Record (500 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric | Primary key — unique customer identifier | 9-digit number |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | Non-empty |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | Non-empty |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | Non-empty |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated against 50 codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | Default 'USA' |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP/postal code | Validated: first 2 digits match state in CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Same validation as phone 1 |
| CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9 digits; not 000-xx-xxxx or xxx-00-xxxx |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID | Optional |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Date String | Date of birth | Format: YYYY-MM-DD; must be in past |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account for payments | Optional |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder indicator | 'Y' = Primary, 'N' = Authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Reserved | Unused | — |

**Used by:** 9+ programs (CBCUS01C, CBEXPORT, CBIMPORT, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, CBTRN01C)

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Record (150 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | Card number (PAN) | 16 characters; Luhn check implied |
| CARD-ACCT-ID | PIC 9(11) | Numeric | FK to Account | Must exist in CVACT01Y |
| CARD-CVV-CD | PIC 9(03) | Numeric | Card Verification Value | 3-digit code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on card | Non-empty |
| CARD-EXPIRAION-DATE | PIC X(10) | Date String | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card status | 'Y' = Active, 'N' = Inactive |
| FILLER | PIC X(59) | Reserved | Unused | — |

**Used by:** 8+ programs (CBACT02C, CBEXPORT, CBIMPORT, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBTRN01C)

### 3.2 CVACT03Y.cpy — Card-Account Cross Reference Record (50 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number (primary key) | Must exist in CVACT02Y |
| XREF-CUST-ID | PIC 9(09) | Numeric | FK to Customer | Must exist in CVCUS01Y |
| XREF-ACCT-ID | PIC 9(11) | Numeric | FK to Account | Must exist in CVACT01Y |
| FILLER | PIC X(14) | Reserved | Unused | — |

**Used by:** 11+ programs (CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COPAUA0C, COPAUS0C, COTRN02C, CBSTM03A)

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Master Record (350 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | System-generated |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to CVTRA04Y |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source | e.g., 'ONLINE', 'POS', 'ATM' |
| TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free text |
| TRAN-AMT | PIC S9(09)V99 | Signed Decimal | Transaction amount | Negative = credit/refund |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | 9-digit code |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card used for transaction | FK to CVACT02Y |
| TRAN-ORIG-TS | PIC X(26) | Timestamp | Original transaction timestamp | ISO format with microseconds |
| TRAN-PROC-TS | PIC X(26) | Timestamp | Processing timestamp | Set during batch posting |
| FILLER | PIC X(20) | Reserved | Unused | — |

**Used by:** 7+ programs (CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, COTRN00C, COTRN01C, COTRN02C, CBSTM03A)

### 4.2 CVTRA06Y.cpy — Daily Transaction Input Record (350 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | Daily transaction ID | — |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Must match CVTRA03Y |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Category code | Must match CVTRA04Y |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | Source channel | — |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | Description | — |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed Decimal | Amount | Validated: within credit limit |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | — |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | — |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | — |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | — |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Validated against XREF |
| DALYTRAN-ORIG-TS | PIC X(26) | Timestamp | Original timestamp | — |
| DALYTRAN-PROC-TS | PIC X(26) | Timestamp | Processing timestamp | — |
| FILLER | PIC X(20) | Reserved | Unused | — |

**Used by:** CBTRN01C, CBTRN02C (daily transaction posting pipeline)

### 4.3 CVTRA03Y.cpy — Transaction Type Reference (60 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code (PK) | 2-character code |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Type description | e.g., 'PURCHASE', 'PAYMENT' |
| FILLER | PIC X(08) | Reserved | Unused | — |

**Used by:** CBTRN03C, COTRTLIC, COTRTUPC, COBTUPDT

### 4.4 CVTRA04Y.cpy — Transaction Category Reference (60 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| TRAN-CAT-KEY (group) | — | Group | Composite key | — |
| → TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Parent type code | FK to CVTRA03Y |
| → TRAN-CAT-CD | PIC 9(04) | Numeric | Category code within type | — |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | e.g., 'GROCERY', 'GAS STATION' |
| FILLER | PIC X(04) | Reserved | Unused | — |

**Used by:** CBTRN03C, COTRTLIC, COTRTUPC

---

## 5. Security/User Entity

### 5.1 CSUSR01Y.cpy — Security User Record (80 bytes)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | User login ID (PK) | Unique, non-empty |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name | — |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name | — |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | User password ⚠️ PLAIN TEXT | **Security risk**: stored unencrypted |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Reserved | Unused | — |

**Used by:** 10+ programs (COSGN00C, COUSR00C–03C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C, COTRTLIC, COTRTUPC)

---

## 6. Communication/Infrastructure Copybooks

### 6.1 COCOM01Y.cpy — Inter-Program COMMAREA (120+ bytes)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Current user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 'A' = Admin, 'U' = User (88-level) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 0 = First entry, 1 = Re-entry |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Context: customer ID |
| CDEMO-CUST-FNAME/MNAME/LNAME | PIC X(25) each | Alphanumeric | Context: customer name |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Context: account ID |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Context: account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Context: card number |
| CDEMO-LAST-MAP/MAPSET | PIC X(7) each | Alphanumeric | Last displayed map |

**Used by:** 12+ online programs (all CICS programs)

### 6.2 CVCRD01Y.cpy — Card Working Areas

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CCARD-AID | PIC X(5) | Alphanumeric | Attention Identifier (ENTER, CLEAR, PFKnn) |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to XCTL to |
| CCARD-NEXT-MAPSET/MAP | PIC X(7) | Alphanumeric | Next BMS map to display |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message buffer |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return/info message buffer |
| CC-ACCT-ID | PIC X(11) / 9(11) | Alphanumeric/Numeric | Working account ID (REDEFINES) |
| CC-CARD-NUM | PIC X(16) / 9(16) | Alphanumeric/Numeric | Working card number (REDEFINES) |
| CC-CUST-ID | PIC X(09) / 9(9) | Alphanumeric/Numeric | Working customer ID (REDEFINES) |

**Used by:** COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC

### 6.3 CSDAT01Y.cpy — Date/Time Working Storage

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| WS-CURTIME-HOURS/MINUTE/SECOND/MILSEC | PIC 9(02) each | Numeric | Current time components |
| WS-CURDATE-MM-DD-YY | Formatted | Display | Date as MM/DD/YY |
| WS-CURTIME-HH-MM-SS | Formatted | Display | Time as HH:MM:SS |
| WS-TIMESTAMP | Formatted | Display | Full timestamp YYYY-MM-DD HH:MM:SS.NNNNNN |

**Used by:** 10+ programs (all online + CBSTM03A)

---

## 7. Validation/Lookup Copybooks

### 7.1 CSLKPCDY.cpy — Master Validation Lookup Table

| Section | Content | Validation Applied To |
|---------|---------|----------------------|
| Phone Area Codes | 350+ NANPA area codes | CUST-PHONE-NUM-1, CUST-PHONE-NUM-2 |
| US State Codes | 50 state codes + DC, PR, GU, VI, AS | CUST-ADDR-STATE-CD |
| State-ZIP Prefix Map | State + first 2 ZIP digits | CUST-ADDR-ZIP cross-referenced with state |

**Used by:** COACTUPC (primary consumer — exhaustive field validation)

### 7.2 CSSETATY.cpy — Screen Attribute Setting Macro

Used with COPY REPLACING to set BMS field attributes (color, protection, highlighting) for multiple field groups. Used 3× in COACTUPC with different field prefixes.

### 7.3 CSSTRPFY.cpy — String Padding/Formatting Utility

String manipulation routines for formatting display fields.

### 7.4 CODATECN.cpy — Date Conversion Record

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| CODATECN-TYPE | PIC X | Input format: '1' = YYYYMMDD, '2' = YYYY-MM-DD |
| CODATECN-INP-DATE | PIC X(20) | Input date value |
| CODATECN-OUTTYPE | PIC X | Output format selector |
| CODATECN-0UT-DATE | PIC X(20) | Converted date output |
| CODATECN-ERROR-MSG | PIC X(38) | Error message if conversion fails |

**Used by:** CBACT01C (via COBDATFT assembler call)

---

## 8. Menu Configuration Copybooks

### 8.1 COADM02Y.cpy — Admin Menu Options

Defines 6 admin menu choices: User List, User Add, User Update, User Delete, Transaction Type List (DB2), Transaction Type Maintenance (DB2).

### 8.2 COMEN02Y.cpy — Main Menu Options

Defines 11 user menu choices: Account View/Update, Credit Card List/View/Update, Transaction List/View/Add, Reports, Bill Payment, Pending Authorization View.

---

## 9. Sub-Application Copybooks

### 9.1 Authorization Module (`app/app-authorization-ims-db2-mq/cpy/`)

| Copybook | Entity | Key Fields |
|----------|--------|-----------|
| CIPAUSMY.cpy | Authorization Summary (IMS root segment) | AUTH-CARD-NUM, AUTH-TIMESTAMP, AUTH-STATUS, AUTH-AMOUNT |
| CIPAUDTY.cpy | Authorization Detail (IMS child segment) | AUTH-DTL-TYPE, AUTH-DTL-MERCHANT, AUTH-DTL-RESPONSE-CD |
| CCPAURQY.cpy | Authorization Request (MQ message) | REQ-CARD-NUM, REQ-AMOUNT, REQ-MERCHANT-ID |
| CCPAURLY.cpy | Authorization Reply (MQ message) | RPL-AUTH-CODE, RPL-RESPONSE-CD, RPL-REASON-CD |
| CCPAUERY.cpy | Authorization Error (MQ message) | ERR-CODE, ERR-REASON, ERR-MSG |
| IMSFUNCS.cpy | IMS DL/I function codes | FUNC-GU, FUNC-GN, FUNC-GNP, FUNC-ISRT, FUNC-DLET, FUNC-REPL |
| PAUTBPCB.CPY | IMS PCB (Program Communication Block) | PAUT-PCB-STATUS, PAUT-PCB-NUM |
| PASFLPCB.CPY | IMS Secondary PCB | — |
| PADFLPCB.CPY | IMS Detail PCB | — |

### 9.2 Transaction Type Module (`app/app-transaction-type-db2/cpy/`)

| Copybook | Entity | Key Fields |
|----------|--------|-----------|
| CSDB2RPY.cpy | DB2 Reply area | SQL return codes |
| CSDB2RWY.cpy | DB2 Working area | SQLCODE, SQLSTATE, SQL-ROWS-AFFECTED |

### 9.3 Export/Import Entity

### CVEXPORT.cpy — Multi-Record Export Layout (500 bytes)

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| EXPORT-REC-TYPE | PIC X(1) | Record type: 'C'=Customer, 'A'=Account, 'X'=XREF, 'T'=Transaction |
| EXPORT-TIMESTAMP | PIC X(26) | Export timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Sequence number |
| EXPORT-BRANCH-ID | PIC X(4) | Originating branch |
| EXPORT-REGION-CODE | PIC X(5) | Region code |
| EXPORT-RECORD-DATA | PIC X(460) | Record payload (REDEFINES per type) |

---

## 10. Report Layout Copybooks

### 10.1 CVTRA07Y.cpy — Transaction Report Layout

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Report identifier ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Report title |
| REPT-START-DATE / REPT-END-DATE | PIC X(10) | Report date range |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Transaction ID column |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Account ID column |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Type code column |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Formatted amount |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Account subtotal |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Grand total |

### 10.2 COSTM01.CPY — Statement Generation Layout

Used by CBSTM03A/B for statement output formatting (text and HTML record layouts).

### 10.3 COTTL01Y.cpy — Screen Title

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | Sign-off message |

---

## 11. Entity Relationship Summary

```
┌─────────────────┐        ┌─────────────────┐
│   CUSTOMER      │        │    ACCOUNT      │
│  (CVCUS01Y)     │        │   (CVACT01Y)    │
│  PK: CUST-ID    │        │  PK: ACCT-ID    │
└────────┬────────┘        └────────┬────────┘
         │ 1:N                      │ 1:N
         ▼                          ▼
┌─────────────────────────────────────────────┐
│          CARD-XREF (CVACT03Y)               │
│  PK: XREF-CARD-NUM                         │
│  FK: XREF-CUST-ID → CUSTOMER               │
│  FK: XREF-ACCT-ID → ACCOUNT                │
└────────────────────┬────────────────────────┘
                     │ 1:1
                     ▼
              ┌─────────────────┐
              │     CARD        │
              │   (CVACT02Y)    │
              │ PK: CARD-NUM    │
              └────────┬────────┘
                       │ 1:N
                       ▼
              ┌─────────────────┐       ┌─────────────────┐
              │  TRANSACTION    │──────►│ TRAN-TYPE       │
              │  (CVTRA05Y)     │       │ (CVTRA03Y)      │
              │  PK: TRAN-ID    │       └─────────────────┘
              └─────────────────┘              │
                                               ▼
                                       ┌─────────────────┐
                                       │ TRAN-CATEGORY   │
                                       │ (CVTRA04Y)      │
                                       └─────────────────┘
```
