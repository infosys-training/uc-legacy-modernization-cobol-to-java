# CardDemo Data Dictionary

This document defines every copybook data structure in the CardDemo application, grouped by business entity.  
Source directory: `app/cpy/`

---

## Account Entity

### CVACT01Y.cpy — ACCOUNT-RECORD (RECLN 300)

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | ACCOUNT-RECORD | — | Group | Account master record |
| 05 | ACCT-ID | PIC 9(11) | Numeric | Account identifier (11-digit) |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Account status flag (e.g. 'Y'=active) |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal | Current account balance |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Credit limit |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | Cash advance credit limit |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Date string | Account open date (YYYY-MM-DD) |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Date string | Account expiration date |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Date string | Card reissue date |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | Current cycle credit total |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | Current cycle debit total |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alpha | Account holder ZIP code |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alpha | Disclosure/interest rate group ID |
| 05 | FILLER | PIC X(178) | — | Reserved space |

---

## Card Entity

### CVACT02Y.cpy — CARD-RECORD (RECLN 150)

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | CARD-RECORD | — | Group | Credit card master record |
| 05 | CARD-NUM | PIC X(16) | Alpha | Card number (16-digit string) |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric | Owning account ID |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alpha | Name embossed on card |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Date string | Card expiration date |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card status flag |
| 05 | FILLER | PIC X(59) | — | Reserved space |

### CVCRD01Y.cpy — CC-WORK-AREAS

Working storage for card cross-reference operations used by online CICS programs.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | CC-WORK-AREAS | — | Group | Card cross-reference working storage |
| 05 | CC-WORK-AREA | — | Group | Main work area |
| 10 | CCARD-AID | PIC X(5) | Alpha | Terminal AID key pressed (88-level values: ENTER, CLEAR, PA1, PA2, PFK01–PFK12) |
| 10 | CCARD-NEXT-PROG | PIC X(8) | Alpha | Next program to transfer control to |
| 10 | CCARD-NEXT-MAPSET | PIC X(7) | Alpha | Next BMS mapset name |
| 10 | CCARD-NEXT-MAP | PIC X(7) | Alpha | Next BMS map name |
| 10 | CCARD-ERROR-MSG | PIC X(75) | Alpha | Error message text |
| 10 | CCARD-RETURN-MSG | PIC X(75) | Alpha | Return message text |
| 10 | CC-ACCT-ID | PIC X(11) | Alpha | Account ID (alpha form) |
| 10 | CC-ACCT-ID-N | PIC 9(11) | Numeric | Account ID (numeric REDEFINES) |
| 10 | CC-CARD-NUM | PIC X(16) | Alpha | Card number (alpha form) |
| 10 | CC-CARD-NUM-N | PIC 9(16) | Numeric | Card number (numeric REDEFINES) |
| 10 | CC-CUST-ID | PIC X(09) | Alpha | Customer ID (alpha form) |
| 10 | CC-CUST-ID-N | PIC 9(9) | Numeric | Customer ID (numeric REDEFINES) |

---

## Customer Entity

### CVCUS01Y.cpy — CUSTOMER-RECORD (RECLN 500)

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | CUSTOMER-RECORD | — | Group | Customer master record |
| 05 | CUST-ID | PIC 9(09) | Numeric | Customer identifier |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alpha | First name |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alpha | Middle name |
| 05 | CUST-LAST-NAME | PIC X(25) | Alpha | Last name |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alpha | Address line 1 |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alpha | Address line 2 |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alpha | Address line 3 |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alpha | ZIP / postal code |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alpha | Primary phone number |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alpha | Secondary phone number |
| 05 | CUST-SSN | PIC 9(09) | Numeric | Social Security Number |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alpha | Government-issued ID |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Date string | Date of birth |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alpha | EFT/ACH account ID |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary cardholder indicator |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score (300–850) |
| 05 | FILLER | PIC X(168) | — | Reserved space |

**Validation rules** (enforced in COACTUPC):
- CUST-SSN: 9-digit numeric, validated for format
- CUST-PHONE-NUM-1/2: Area code validated against CSLKPCDY lookup table
- CUST-DOB-YYYY-MM-DD: Validated via CSUTLDWY date edit routines (century, month, day)
- CUST-FICO-CREDIT-SCORE: 3-digit numeric range

### CUSTREC.cpy — CUSTOMER-RECORD (alternate layout)

Identical structure to CVCUS01Y except `CUST-DOB-YYYYMMDD` replaces `CUST-DOB-YYYY-MM-DD`. Used by CBSTM03A for statement generation.

---

## Transaction Entity

### CVTRA05Y.cpy — TRAN-RECORD (RECLN 350)

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | TRAN-RECORD | — | Group | Transaction record |
| 05 | TRAN-ID | PIC X(16) | Alpha | Transaction identifier |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code |
| 05 | TRAN-SOURCE | PIC X(10) | Alpha | Transaction source system |
| 05 | TRAN-DESC | PIC X(100) | Alpha | Transaction description |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed decimal | Transaction amount |
| 05 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP code |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alpha | Card number used |
| 05 | TRAN-ORIG-TS | PIC X(26) | Timestamp | Origination timestamp |
| 05 | TRAN-PROC-TS | PIC X(26) | Timestamp | Processing timestamp |
| 05 | FILLER | PIC X(20) | — | Reserved space |

### CVTRA06Y.cpy — DALYTRAN-RECORD (RECLN 350)

Daily transaction input record. Identical field layout to TRAN-RECORD with `DALYTRAN-` prefix. Used as the daily feed input to CBTRN01C and CBTRN02C.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | DALYTRAN-RECORD | — | Group | Daily transaction input record |
| 05 | DALYTRAN-ID | PIC X(16) | Alpha | Transaction ID |
| 05 | DALYTRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| 05 | DALYTRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code |
| 05 | DALYTRAN-SOURCE | PIC X(10) | Alpha | Source system |
| 05 | DALYTRAN-DESC | PIC X(100) | Alpha | Description |
| 05 | DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| 05 | DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| 05 | DALYTRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name |
| 05 | DALYTRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city |
| 05 | DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP |
| 05 | DALYTRAN-CARD-NUM | PIC X(16) | Alpha | Card number |
| 05 | DALYTRAN-ORIG-TS | PIC X(26) | Timestamp | Origination timestamp |
| 05 | DALYTRAN-PROC-TS | PIC X(26) | Timestamp | Processing timestamp |
| 05 | FILLER | PIC X(20) | — | Reserved space |

### CVTRA01Y.cpy — TRAN-CAT-BAL-RECORD (RECLN 50)

Transaction category balance record — aggregated balance per account/type/category.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | Category balance record |
| 05 | TRAN-CAT-KEY | — | Group | Composite key |
| 10 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID |
| 10 | TRANCAT-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| 10 | TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code |
| 05 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | Aggregated balance for this category |
| 05 | FILLER | PIC X(22) | — | Reserved space |

### CVTRA02Y.cpy — DIS-GROUP-RECORD (RECLN 50)

Disclosure/interest rate group record — maps account groups to interest rates per transaction type/category.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | DIS-GROUP-RECORD | — | Group | Disclosure group record |
| 05 | DIS-GROUP-KEY | — | Group | Composite key |
| 10 | DIS-ACCT-GROUP-ID | PIC X(10) | Alpha | Account group ID (links to ACCT-GROUP-ID) |
| 10 | DIS-TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| 10 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code |
| 05 | DIS-INT-RATE | PIC S9(04)V99 | Signed decimal | Interest rate for this group/type/category |
| 05 | FILLER | PIC X(28) | — | Reserved space |

### CVTRA03Y.cpy — TRAN-TYPE-RECORD (RECLN 60)

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type lookup |
| 05 | TRAN-TYPE | PIC X(02) | Alpha | Transaction type code |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alpha | Transaction type description |
| 05 | FILLER | PIC X(08) | — | Reserved space |

### CVTRA04Y.cpy — TRAN-CAT-RECORD (RECLN 60)

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category lookup |
| 05 | TRAN-CAT-KEY | — | Group | Composite key |
| 10 | TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| 10 | TRAN-CAT-CD | PIC 9(04) | Numeric | Category code |
| 05 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alpha | Category description |
| 05 | FILLER | PIC X(04) | — | Reserved space |

### CVTRA07Y.cpy — Report Layout Records

Report formatting structures used by CBTRN03C for the Daily Transaction Report.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | REPORT-NAME-HEADER | — | Group | Report header line |
| 05 | REPT-SHORT-NAME | PIC X(38) | Alpha | Report short name ('DALYREPT') |
| 05 | REPT-LONG-NAME | PIC X(41) | Alpha | Report title ('Daily Transaction Report') |
| 05 | REPT-DATE-HEADER | PIC X(12) | Alpha | 'Date Range: ' label |
| 05 | REPT-START-DATE | PIC X(10) | Date string | Report start date |
| 05 | REPT-END-DATE | PIC X(10) | Date string | Report end date |
| 01 | TRANSACTION-DETAIL-REPORT | — | Group | Detail line layout |
| 05 | TRAN-REPORT-TRANS-ID | PIC X(16) | Alpha | Transaction ID |
| 05 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alpha | Account ID |
| 05 | TRAN-REPORT-TYPE-CD | PIC X(02) | Alpha | Type code |
| 05 | TRAN-REPORT-TYPE-DESC | PIC X(15) | Alpha | Type description |
| 05 | TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| 05 | TRAN-REPORT-CAT-DESC | PIC X(29) | Alpha | Category description |
| 05 | TRAN-REPORT-SOURCE | PIC X(10) | Alpha | Source |
| 05 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Amount (formatted) |
| 01 | REPORT-PAGE-TOTALS | — | Group | Page total line |
| 05 | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page total amount |
| 01 | REPORT-ACCOUNT-TOTALS | — | Group | Account total line |
| 05 | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account total amount |
| 01 | REPORT-GRAND-TOTALS | — | Group | Grand total line |
| 05 | REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total amount |

### COSTM01.CPY — TRNX-RECORD (Statement Layout)

Alternate transaction layout keyed by card number + transaction ID, used by CBSTM03A for statement generation.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | TRNX-RECORD | — | Group | Statement transaction record |
| 05 | TRNX-KEY | — | Group | Composite key |
| 10 | TRNX-CARD-NUM | PIC X(16) | Alpha | Card number (primary sort) |
| 10 | TRNX-ID | PIC X(16) | Alpha | Transaction ID |
| 05 | TRNX-REST | — | Group | Remaining fields |
| 10 | TRNX-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| 10 | TRNX-CAT-CD | PIC 9(04) | Numeric | Category code |
| 10 | TRNX-SOURCE | PIC X(10) | Alpha | Source system |
| 10 | TRNX-DESC | PIC X(100) | Alpha | Description |
| 10 | TRNX-AMT | PIC S9(09)V99 | Signed decimal | Amount |
| 10 | TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| 10 | TRNX-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name |
| 10 | TRNX-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city |
| 10 | TRNX-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP |
| 10 | TRNX-ORIG-TS | PIC X(26) | Timestamp | Origination timestamp |
| 10 | TRNX-PROC-TS | PIC X(26) | Timestamp | Processing timestamp |
| 10 | FILLER | PIC X(20) | — | Reserved space |

---

## Cross-Reference Entity

### CVACT03Y.cpy — CARD-XREF-RECORD (RECLN 50)

Links card numbers to customer IDs and account IDs.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | CARD-XREF-RECORD | — | Group | Card cross-reference record |
| 05 | XREF-CARD-NUM | PIC X(16) | Alpha | Card number (key) |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID |
| 05 | FILLER | PIC X(14) | — | Reserved space |

---

## Export / Import Entity

### CVEXPORT.cpy — EXPORT-RECORD (RECLN 500)

Multi-record-type export file layout for branch migration. Uses REDEFINES to overlay the same 460-byte data area with different record structures.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | EXPORT-RECORD | — | Group | Export record |
| 05 | EXPORT-REC-TYPE | PIC X(1) | Alpha | Record type: C=Customer, A=Account, X=Xref, T=Transaction, D=Card |
| 05 | EXPORT-TIMESTAMP | PIC X(26) | Timestamp | Export timestamp |
| 05 | EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number |
| 05 | EXPORT-BRANCH-ID | PIC X(4) | Alpha | Source branch ID |
| 05 | EXPORT-REGION-CODE | PIC X(5) | Alpha | Source region code |
| 05 | EXPORT-RECORD-DATA | PIC X(460) | Alpha | Record payload (REDEFINES below) |

**REDEFINES overlay for Customer (type 'C'):**

| Level | Field | PIC Clause | Type |
|---|---|---|---|
| 05 | EXPORT-CUSTOMER-DATA | REDEFINES | — |
| 10 | EXP-CUST-ID | PIC 9(09) COMP | Binary |
| 10 | EXP-CUST-FIRST-NAME | PIC X(25) | Alpha |
| 10 | EXP-CUST-MIDDLE-NAME | PIC X(25) | Alpha |
| 10 | EXP-CUST-LAST-NAME | PIC X(25) | Alpha |
| 10 | EXP-CUST-ADDR-LINE (×3) | PIC X(50) | Alpha |
| 10 | EXP-CUST-ADDR-STATE-CD | PIC X(02) | Alpha |
| 10 | EXP-CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha |
| 10 | EXP-CUST-ADDR-ZIP | PIC X(10) | Alpha |
| 10 | EXP-CUST-PHONE-NUM (×2) | PIC X(15) | Alpha |
| 10 | EXP-CUST-SSN | PIC 9(09) | Numeric |
| 10 | EXP-CUST-GOVT-ISSUED-ID | PIC X(20) | Alpha |
| 10 | EXP-CUST-DOB-YYYY-MM-DD | PIC X(10) | Date |
| 10 | EXP-CUST-EFT-ACCOUNT-ID | PIC X(10) | Alpha |
| 10 | EXP-CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha |
| 10 | EXP-CUST-FICO-CREDIT-SCORE | PIC 9(03) COMP-3 | Packed decimal |

**REDEFINES overlay for Account (type 'A'):**

| Level | Field | PIC Clause | Type |
|---|---|---|---|
| 05 | EXPORT-ACCOUNT-DATA | REDEFINES | — |
| 10 | EXP-ACCT-ID | PIC 9(11) | Numeric |
| 10 | EXP-ACCT-ACTIVE-STATUS | PIC X(01) | Alpha |
| 10 | EXP-ACCT-CURR-BAL | PIC S9(10)V99 COMP-3 | Packed decimal |
| 10 | EXP-ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal |
| 10 | EXP-ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 COMP-3 | Packed decimal |
| 10 | EXP-ACCT-OPEN-DATE | PIC X(10) | Date |
| 10 | EXP-ACCT-EXPIRAION-DATE | PIC X(10) | Date |
| 10 | EXP-ACCT-REISSUE-DATE | PIC X(10) | Date |
| 10 | EXP-ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal |
| 10 | EXP-ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 COMP | Binary |
| 10 | EXP-ACCT-ADDR-ZIP | PIC X(10) | Alpha |
| 10 | EXP-ACCT-GROUP-ID | PIC X(10) | Alpha |

**REDEFINES overlay for Transaction (type 'T'):**

| Level | Field | PIC Clause | Type |
|---|---|---|---|
| 05 | EXPORT-TRANSACTION-DATA | REDEFINES | — |
| 10 | EXP-TRAN-ID | PIC X(16) | Alpha |
| 10 | EXP-TRAN-TYPE-CD | PIC X(02) | Alpha |
| 10 | EXP-TRAN-CAT-CD | PIC 9(04) | Numeric |
| 10 | EXP-TRAN-SOURCE | PIC X(10) | Alpha |
| 10 | EXP-TRAN-DESC | PIC X(100) | Alpha |
| 10 | EXP-TRAN-AMT | PIC S9(09)V99 COMP-3 | Packed decimal |
| 10 | EXP-TRAN-MERCHANT-ID | PIC 9(09) COMP | Binary |
| 10 | EXP-TRAN-MERCHANT-NAME | PIC X(50) | Alpha |
| 10 | EXP-TRAN-MERCHANT-CITY | PIC X(50) | Alpha |
| 10 | EXP-TRAN-MERCHANT-ZIP | PIC X(10) | Alpha |
| 10 | EXP-TRAN-CARD-NUM | PIC X(16) | Alpha |
| 10 | EXP-TRAN-ORIG-TS | PIC X(26) | Timestamp |
| 10 | EXP-TRAN-PROC-TS | PIC X(26) | Timestamp |

**REDEFINES overlay for Cross-Reference (type 'X'):**

| Level | Field | PIC Clause | Type |
|---|---|---|---|
| 05 | EXPORT-CARD-XREF-DATA | REDEFINES | — |
| 10 | EXP-XREF-CARD-NUM | PIC X(16) | Alpha |
| 10 | EXP-XREF-CUST-ID | PIC 9(09) | Numeric |
| 10 | EXP-XREF-ACCT-ID | PIC 9(11) COMP | Binary |

**REDEFINES overlay for Card (type 'D'):**

| Level | Field | PIC Clause | Type |
|---|---|---|---|
| 05 | EXPORT-CARD-DATA | REDEFINES | — |
| 10 | EXP-CARD-NUM | PIC X(16) | Alpha |
| 10 | EXP-CARD-ACCT-ID | PIC 9(11) COMP | Binary |
| 10 | EXP-CARD-CVV-CD | PIC 9(03) COMP | Binary |
| 10 | EXP-CARD-EMBOSSED-NAME | PIC X(50) | Alpha |
| 10 | EXP-CARD-EXPIRAION-DATE | PIC X(10) | Date |
| 10 | EXP-CARD-ACTIVE-STATUS | PIC X(01) | Alpha |

---

## User Security Entity

### CSUSR01Y.cpy — SEC-USER-DATA (RECLN 80)

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | SEC-USER-DATA | — | Group | User security record |
| 05 | SEC-USR-ID | PIC X(08) | Alpha | User login ID |
| 05 | SEC-USR-FNAME | PIC X(20) | Alpha | User first name |
| 05 | SEC-USR-LNAME | PIC X(20) | Alpha | User last name |
| 05 | SEC-USR-PWD | PIC X(08) | Alpha | User password (plaintext) |
| 05 | SEC-USR-TYPE | PIC X(01) | Alpha | User type ('A'=admin, 'U'=regular) |
| 05 | SEC-USR-FILLER | PIC X(23) | — | Reserved space |

---

## Common / Infrastructure Copybooks

### COCOM01Y.cpy — CARDDEMO-COMMAREA

Inter-program communication area passed between CICS programs via EXEC CICS XCTL / RETURN TRANSID.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | CARDDEMO-COMMAREA | — | Group | CICS communication area |
| 05 | CDEMO-GENERAL-INFO | — | Group | General navigation data |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alpha | Source transaction ID |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alpha | Source program name |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alpha | Target transaction ID |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alpha | Target program name |
| 10 | CDEMO-USER-ID | PIC X(08) | Alpha | Signed-on user ID |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alpha | User type (88: 'A'=Admin, 'U'=User) |
| 10 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context (88: 0=Enter, 1=Reenter) |
| 05 | CDEMO-CUSTOMER-INFO | — | Group | Selected customer data |
| 10 | CDEMO-CUST-ID | PIC 9(09) | Numeric | Customer ID |
| 10 | CDEMO-CUST-FNAME | PIC X(25) | Alpha | Customer first name |
| 10 | CDEMO-CUST-MNAME | PIC X(25) | Alpha | Customer middle name |
| 10 | CDEMO-CUST-LNAME | PIC X(25) | Alpha | Customer last name |
| 05 | CDEMO-ACCOUNT-INFO | — | Group | Selected account data |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | Account ID |
| 10 | CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Account status |
| 05 | CDEMO-CARD-INFO | — | Group | Selected card data |
| 10 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | Card number |
| 05 | CDEMO-MORE-INFO | — | Group | Map navigation |
| 10 | CDEMO-LAST-MAP | PIC X(7) | Alpha | Last BMS map displayed |
| 10 | CDEMO-LAST-MAPSET | PIC X(7) | Alpha | Last BMS mapset |

### COTTL01Y.cpy — CCDA-SCREEN-TITLE

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | CCDA-SCREEN-TITLE | — | Group | Screen header titles |
| 05 | CCDA-TITLE01 | PIC X(40) | Alpha | Line 1: 'AWS Mainframe Modernization' |
| 05 | CCDA-TITLE02 | PIC X(40) | Alpha | Line 2: 'CardDemo' |
| 05 | CCDA-THANK-YOU | PIC X(40) | Alpha | Sign-off message |

### CSDAT01Y.cpy — WS-DATE-TIME

Current date/time working storage with multiple format representations.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | WS-DATE-TIME | — | Group | Date/time work area |
| 10 | WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year (YYYY) |
| 10 | WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| 10 | WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| 10 | WS-CURDATE-N | PIC 9(08) | Numeric | YYYYMMDD (REDEFINES) |
| 10 | WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hour |
| 10 | WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minute |
| 10 | WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current second |
| 05 | WS-CURDATE-MM-DD-YY | — | Group | MM/DD/YY formatted date |
| 05 | WS-CURTIME-HH-MM-SS | — | Group | HH:MM:SS formatted time |
| 05 | WS-TIMESTAMP | — | Group | YYYY-MM-DD HH:MM:SS.NNNNNN timestamp |

### CSMSG01Y.cpy — CCDA-COMMON-MESSAGES

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | CCDA-COMMON-MESSAGES | — | Group | Common message constants |
| 05 | CCDA-MSG-THANK-YOU | PIC X(50) | Alpha | Thank-you message |
| 05 | CCDA-MSG-INVALID-KEY | PIC X(50) | Alpha | Invalid key pressed message |

### CSMSG02Y.cpy — ABEND-DATA

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | ABEND-DATA | — | Group | Abend handling work area |
| 05 | ABEND-CODE | PIC X(4) | Alpha | Abend code |
| 05 | ABEND-CULPRIT | PIC X(8) | Alpha | Program causing abend |
| 05 | ABEND-REASON | PIC X(50) | Alpha | Abend reason text |
| 05 | ABEND-MSG | PIC X(72) | Alpha | Abend message |

### CODATECN.cpy — CODATECN-REC

Date conversion record with input/output formats and REDEFINES for YYYYMMDD vs YYYY-MM-DD.

| Level | Field | PIC Clause | Type | Business Meaning |
|---|---|---|---|---|
| 01 | CODATECN-REC | — | Group | Date conversion record |
| 05 | CODATECN-IN-REC | — | Group | Input date |
| 10 | CODATECN-TYPE | PIC X | Alpha | Input type (88: '1'=YYYYMMDD, '2'=YYYY-MM-DD) |
| 10 | CODATECN-INP-DATE | PIC X(20) | Alpha | Input date value |
| 05 | CODATECN-OUT-REC | — | Group | Output date |
| 10 | CODATECN-OUTTYPE | PIC X | Alpha | Output type (88: '1'=YYYY-MM-DD, '2'=YYYYMMDD) |
| 10 | CODATECN-0UT-DATE | PIC X(20) | Alpha | Output date value |
| 05 | CODATECN-ERROR-MSG | PIC X(38) | Alpha | Conversion error message |

### COADM02Y.cpy — CARDDEMO-ADMIN-MENU-OPTIONS

Admin menu option definitions (6 options). Each option: number PIC 9(02), name PIC X(35), program PIC X(08).

Options: (1) User List — COUSR00C, (2) User Add — COUSR01C, (3) User Update — COUSR02C, (4) User Delete — COUSR03C, (5) Transaction Type List/Update (DB2) — COTRTLIC, (6) Transaction Type Maintenance (DB2) — COTRTUPC.

### COMEN02Y.cpy — CARDDEMO-MAIN-MENU-OPTIONS

Main menu option definitions (11 options). Each option: number PIC 9(02), name PIC X(35), program PIC X(08), type PIC X(01).

Options: (1) Account View — COACTVWC, (2) Account Update — COACTUPC, (3) Credit Card List — COCRDLIC, (4) Credit Card View — COCRDSLC, (5) Credit Card Update — COCRDUPC, (6) Transaction List — COTRN00C, (7) Transaction View — COTRN01C, (8) Transaction Add — COTRN02C, (9) Transaction Reports — CORPT00C, (10) Bill Payment — COBIL00C, (11) Pending Authorization View — COPAUS0C.

### CSUTLDWY.cpy — Date Edit Working Storage

Date validation working storage used by CSUTLDPY procedure copybook. Contains:
- WS-EDIT-DATE-CCYYMMDD with REDEFINES for CC, YY, MM, DD (numeric and alpha)
- 88-level conditions: THIS-CENTURY (20), LAST-CENTURY (19), WS-VALID-MONTH (1–12), WS-31-DAY-MONTH, WS-FEBRUARY
- WS-EDIT-DATE-FLGS with validity flags for year, month, day
- WS-DATE-VALIDATION-RESULT with severity, message code, result text

### CSUTLDPY.cpy — Date Edit Procedure Division

Procedure division copybook with reusable paragraphs for CCYYMMDD date validation:
- EDIT-DATE-CCYYMMDD: Entry point
- EDIT-YEAR-CCYY: Validate century (19 or 20) and year
- EDIT-MONTH: Validate month (1–12)
- EDIT-DAY: Validate day with month-specific rules (28/29/30/31)
- EDIT-DATE-OF-BIRTH: Validate DOB is in the past

### CSSTRPFY.cpy — Store PF Key

Procedure division copybook that maps EIBAID to CCARD-AID 88-level conditions (ENTER, CLEAR, PA1, PA2, PFK01–PFK12). Also maps PF13–PF24 to PFK01–PFK12.

### CSSETATY.cpy — Set Attribute Utility

Procedure division copybook macro for setting BMS field attributes. Sets field color to red and displays '*' for blank/error fields on screen re-entry.

### CSLKPCDY.cpy — Lookup Code Repository (1318 lines)

Large lookup table containing:
1. **North America phone area codes** (WS-US-PHONE-AREA-CODE-TO-EDIT PIC XXX with 88 VALID-PHONE-AREA-CODE VALUES)
2. **US state codes** validation
3. **State + ZIP prefix** mapping

Used by COACTUPC for phone number and address validation.

### UNUSED1Y.cpy — Unused Placeholder

Unused data structure with same layout as SEC-USER-DATA but all fields prefixed with UNUSED-.
