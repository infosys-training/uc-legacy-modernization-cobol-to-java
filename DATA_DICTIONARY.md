# CardDemo Data Dictionary

Comprehensive field-level definitions extracted from every copybook in the CardDemo application. Fields are grouped by business entity and include PIC clause, data type, business meaning, and validation rules where applicable.

---

## 1. Account Entity

### CVACT01Y.cpy — `ACCOUNT-RECORD` (Record Length 300)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| ACCT-ID | `PIC 9(11)` | Numeric (11 digits) | Unique account identifier — primary key for ACCTFILE VSAM KSDS | Must be numeric; used as VSAM key |
| ACCT-ACTIVE-STATUS | `PIC X(01)` | Alphanumeric (1) | Account active/inactive flag | COACTUPC validates non-blank |
| ACCT-CURR-BAL | `PIC S9(10)V99` | Signed numeric (12,2) | Current account balance | Updated by CBTRN02C (post transactions), COBIL00C (bill pay) |
| ACCT-CREDIT-LIMIT | `PIC S9(10)V99` | Signed numeric (12,2) | Maximum credit limit on account | COACTUPC validates numeric, > 0 |
| ACCT-CASH-CREDIT-LIMIT | `PIC S9(10)V99` | Signed numeric (12,2) | Cash advance credit limit | COACTUPC validates numeric |
| ACCT-OPEN-DATE | `PIC X(10)` | Alphanumeric (10) | Account open date (YYYY-MM-DD) | COACTUPC validates date format via CSUTLDPY |
| ACCT-EXPIRAION-DATE | `PIC X(10)` | Alphanumeric (10) | Account expiration date (YYYY-MM-DD) | COACTUPC validates date format, must be >= open date |
| ACCT-REISSUE-DATE | `PIC X(10)` | Alphanumeric (10) | Last card reissue date (YYYY-MM-DD) | COACTUPC validates date format |
| ACCT-CURR-CYC-CREDIT | `PIC S9(10)V99` | Signed numeric (12,2) | Current billing cycle credit total | |
| ACCT-CURR-CYC-DEBIT | `PIC S9(10)V99` | Signed numeric (12,2) | Current billing cycle debit total | |
| ACCT-ADDR-ZIP | `PIC X(10)` | Alphanumeric (10) | Account holder zip code | |
| ACCT-GROUP-ID | `PIC X(10)` | Alphanumeric (10) | Account group for interest rate lookup (joined with DISCGRP) | |
| FILLER | `PIC X(178)` | Filler | Padding to 300-byte record length | |

---

## 2. Card Entity

### CVACT02Y.cpy — `CARD-RECORD` (Record Length 150)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| CARD-NUM | `PIC X(16)` | Alphanumeric (16) | Credit card number — primary key for CARDFILE | Must be 16 characters |
| CARD-ACCT-ID | `PIC 9(11)` | Numeric (11) | Associated account ID | Must match existing account in ACCTFILE |
| CARD-CVV-CD | `PIC 9(03)` | Numeric (3) | Card verification value (CVV) | |
| CARD-EMBOSSED-NAME | `PIC X(50)` | Alphanumeric (50) | Name printed on card | COCRDUPC validates non-blank |
| CARD-EXPIRAION-DATE | `PIC X(10)` | Alphanumeric (10) | Card expiration date (YYYY-MM-DD) | COCRDUPC validates date format |
| CARD-ACTIVE-STATUS | `PIC X(01)` | Alphanumeric (1) | Card active/inactive flag | COCRDUPC validates: 'Y' or 'N' |
| FILLER | `PIC X(59)` | Filler | Padding to 150-byte record | |

### CVCRD01Y.cpy — `CC-WORK-AREAS` (CICS screen work area)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| CCARD-AID | `PIC X(5)` | Alphanumeric (5) | Attention Identifier (AID key pressed) | 88-level values: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| CCARD-NEXT-PROG | `PIC X(8)` | Alphanumeric (8) | Next program to XCTL to | |
| CCARD-NEXT-MAPSET | `PIC X(7)` | Alphanumeric (7) | Next BMS mapset name | |
| CCARD-NEXT-MAP | `PIC X(7)` | Alphanumeric (7) | Next BMS map name | |
| CCARD-ERROR-MSG | `PIC X(60)` | Alphanumeric (60) | Error message for screen display | |
| CCARD-AID-ARRAY (16 entries) | `PIC X(5)` | Alphanumeric (5) | Array of AID key identifiers | |

---

## 3. Customer Entity

### CVCUS01Y.cpy — `CUSTOMER-RECORD` (Record Length 500)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| CUST-ID | `PIC 9(09)` | Numeric (9) | Unique customer identifier — primary key for CUSTFILE | Must be numeric |
| CUST-FIRST-NAME | `PIC X(25)` | Alphanumeric (25) | Customer first name | COACTUPC validates non-blank |
| CUST-MIDDLE-NAME | `PIC X(25)` | Alphanumeric (25) | Customer middle name | |
| CUST-LAST-NAME | `PIC X(25)` | Alphanumeric (25) | Customer last name | COACTUPC validates non-blank |
| CUST-ADDR-LINE-1 | `PIC X(50)` | Alphanumeric (50) | Address line 1 | |
| CUST-ADDR-LINE-2 | `PIC X(50)` | Alphanumeric (50) | Address line 2 | |
| CUST-ADDR-LINE-3 | `PIC X(50)` | Alphanumeric (50) | Address line 3 | |
| CUST-ADDR-STATE-CD | `PIC X(02)` | Alphanumeric (2) | US state code | COACTUPC validates against CSLKPCDY state-code table |
| CUST-ADDR-COUNTRY-CD | `PIC X(03)` | Alphanumeric (3) | Country code | |
| CUST-ADDR-ZIP | `PIC X(10)` | Alphanumeric (10) | Zip / postal code | COACTUPC validates first 5 digits numeric |
| CUST-PHONE-NUM-1 | `PIC X(15)` | Alphanumeric (15) | Primary phone number | COACTUPC validates area code against CSLKPCDY (NANPA list) |
| CUST-PHONE-NUM-2 | `PIC X(15)` | Alphanumeric (15) | Secondary phone number | COACTUPC validates area code against CSLKPCDY |
| CUST-SSN | `PIC 9(09)` | Numeric (9) | Social Security Number | COACTUPC validates 9-digit numeric, non-zero |
| CUST-GOVT-ISSUED-ID | `PIC X(20)` | Alphanumeric (20) | Government-issued ID | |
| CUST-DOB-YYYY-MM-DD | `PIC X(10)` | Alphanumeric (10) | Date of birth (YYYY-MM-DD) | COACTUPC validates date format via CSUTLDPY |
| CUST-EFT-ACCOUNT-ID | `PIC X(10)` | Alphanumeric (10) | Electronic fund transfer account ID | |
| CUST-PRI-CARD-HOLDER-IND | `PIC X(01)` | Alphanumeric (1) | Primary card holder indicator | |
| CUST-FICO-CREDIT-SCORE | `PIC 9(03)` | Numeric (3) | FICO credit score | COACTUPC validates range 300–850 |
| FILLER | `PIC X(168)` | Filler | Padding to 500-byte record | |

### CUSTREC.cpy — `CUSTOMER-RECORD` (alternate layout for statements)

Identical structure to CVCUS01Y.cpy. Used by CBSTM03A for statement generation. Minor differences: `CUST-DOB-YYYYMMDD` (no hyphens in field name), slightly different indentation.

---

## 4. Transaction Entity

### CVTRA05Y.cpy — `TRAN-RECORD` (Record Length 350)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| TRAN-ID | `PIC X(16)` | Alphanumeric (16) | Transaction identifier — part of VSAM key | Auto-generated |
| TRAN-TYPE-CD | `PIC X(02)` | Alphanumeric (2) | Transaction type code (SA, PR, etc.) | Must match TRANTYPE lookup file |
| TRAN-CAT-CD | `PIC 9(04)` | Numeric (4) | Transaction category code | Must match TRANCATG lookup file |
| TRAN-SOURCE | `PIC X(10)` | Alphanumeric (10) | Originating source of transaction | |
| TRAN-DESC | `PIC X(100)` | Alphanumeric (100) | Transaction description | |
| TRAN-AMT | `PIC S9(09)V99` | Signed numeric (11,2) | Transaction amount | COTRN02C/CBTRN02C validate numeric, checked vs. credit limit |
| TRAN-MERCHANT-ID | `PIC 9(09)` | Numeric (9) | Merchant identifier | |
| TRAN-MERCHANT-NAME | `PIC X(50)` | Alphanumeric (50) | Merchant name | |
| TRAN-MERCHANT-CITY | `PIC X(50)` | Alphanumeric (50) | Merchant city | |
| TRAN-MERCHANT-ZIP | `PIC X(10)` | Alphanumeric (10) | Merchant zip code | |
| TRAN-CARD-NUM | `PIC X(16)` | Alphanumeric (16) | Card number associated with transaction | Must match existing card in XREFFILE |
| TRAN-ORIG-TS | `PIC X(26)` | Alphanumeric (26) | Original transaction timestamp (YYYY-MM-DD HH:MM:SS.ffffff) | |
| TRAN-PROC-TS | `PIC X(26)` | Alphanumeric (26) | Processing timestamp | |
| FILLER | `PIC X(20)` | Filler | Padding to 350-byte record | |

### CVTRA06Y.cpy — `DALYTRAN-RECORD` (Record Length 350)

Same layout as TRAN-RECORD (CVTRA05Y) but prefixed with DALYTRAN-. Used for daily transaction input feed files.

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| DALYTRAN-ID | `PIC X(16)` | Alphanumeric (16) | Daily transaction identifier |
| DALYTRAN-TYPE-CD | `PIC X(02)` | Alphanumeric (2) | Transaction type code |
| DALYTRAN-CAT-CD | `PIC 9(04)` | Numeric (4) | Transaction category code |
| DALYTRAN-SOURCE | `PIC X(10)` | Alphanumeric (10) | Transaction source |
| DALYTRAN-DESC | `PIC X(100)` | Alphanumeric (100) | Transaction description |
| DALYTRAN-AMT | `PIC S9(09)V99` | Signed numeric (11,2) | Transaction amount |
| DALYTRAN-MERCHANT-ID | `PIC 9(09)` | Numeric (9) | Merchant identifier |
| DALYTRAN-MERCHANT-NAME | `PIC X(50)` | Alphanumeric (50) | Merchant name |
| DALYTRAN-MERCHANT-CITY | `PIC X(50)` | Alphanumeric (50) | Merchant city |
| DALYTRAN-MERCHANT-ZIP | `PIC X(10)` | Alphanumeric (10) | Merchant zip |
| DALYTRAN-CARD-NUM | `PIC X(16)` | Alphanumeric (16) | Card number |
| DALYTRAN-ORIG-TS | `PIC X(26)` | Alphanumeric (26) | Original timestamp |
| DALYTRAN-PROC-TS | `PIC X(26)` | Alphanumeric (26) | Processing timestamp |
| FILLER | `PIC X(20)` | Filler | Padding to 350 bytes |

### CVTRA01Y.cpy — `TRAN-CAT-BAL-RECORD` (Record Length 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| TRANCAT-ACCT-ID | `PIC 9(11)` | Numeric (11) | Account ID (part of composite key) | Must match ACCTFILE |
| TRANCAT-TYPE-CD | `PIC X(02)` | Alphanumeric (2) | Transaction type code (part of key) | |
| TRANCAT-CD | `PIC 9(04)` | Numeric (4) | Transaction category code (part of key) | |
| TRAN-CAT-BAL | `PIC S9(09)V99` | Signed numeric (11,2) | Running balance for this acct/type/category combination | Updated by CBTRN02C and CBACT04C |
| FILLER | `PIC X(22)` | Filler | Padding to 50 bytes | |

### CVTRA02Y.cpy — `DIS-GROUP-RECORD` (Record Length 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| DIS-ACCT-GROUP-ID | `PIC X(10)` | Alphanumeric (10) | Account group ID (part of composite key) | Must match ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | `PIC X(02)` | Alphanumeric (2) | Transaction type (part of key) | |
| DIS-TRAN-CAT-CD | `PIC 9(04)` | Numeric (4) | Transaction category (part of key) | |
| DIS-INT-RATE | `PIC S9(04)V99` | Signed numeric (6,2) | Interest rate for this group/type/category | Used by CBACT04C to compute interest |
| FILLER | `PIC X(28)` | Filler | Padding to 50 bytes | |

### CVTRA03Y.cpy — `TRAN-TYPE-RECORD` (Record Length 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| TRAN-TYPE | `PIC X(02)` | Alphanumeric (2) | Transaction type code (primary key) | |
| TRAN-TYPE-DESC | `PIC X(50)` | Alphanumeric (50) | Transaction type description | |
| FILLER | `PIC X(08)` | Filler | Padding to 60 bytes | |

### CVTRA04Y.cpy — `TRAN-CAT-RECORD` (Record Length 60)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| TRAN-TYPE-CD | `PIC X(02)` | Alphanumeric (2) | Transaction type code (composite key part 1) | |
| TRAN-CAT-CD | `PIC 9(04)` | Numeric (4) | Transaction category code (composite key part 2) | |
| TRAN-CAT-TYPE-DESC | `PIC X(50)` | Alphanumeric (50) | Category type description | |
| FILLER | `PIC X(04)` | Filler | Padding to 60 bytes | |

### CVTRA07Y.cpy — Report Structures (Transaction Report)

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| REPT-SHORT-NAME | `PIC X(38)` | Alphanumeric (38) | Report short name (value: 'DALYREPT') |
| REPT-LONG-NAME | `PIC X(41)` | Alphanumeric (41) | Report long name (value: 'Daily Transaction Report') |
| REPT-DATE-HEADER | `PIC X(12)` | Alphanumeric (12) | Date range label |
| REPT-START-DATE | `PIC X(10)` | Alphanumeric (10) | Report start date |
| REPT-END-DATE | `PIC X(10)` | Alphanumeric (10) | Report end date |
| TRAN-REPORT-TRANS-ID | `PIC X(16)` | Alphanumeric (16) | Transaction ID in report line |
| TRAN-REPORT-ACCOUNT-ID | `PIC X(11)` | Alphanumeric (11) | Account ID in report line |
| TRAN-REPORT-TYPE-CD | `PIC X(02)` | Alphanumeric (2) | Type code in report line |
| TRAN-REPORT-TYPE-DESC | `PIC X(15)` | Alphanumeric (15) | Type description |
| TRAN-REPORT-CAT-CD | `PIC 9(04)` | Numeric (4) | Category code in report line |
| TRAN-REPORT-CAT-DESC | `PIC X(29)` | Alphanumeric (29) | Category description |
| TRAN-REPORT-SOURCE | `PIC X(10)` | Alphanumeric (10) | Transaction source |
| TRAN-REPORT-AMT | `PIC -ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Formatted transaction amount |
| REPT-PAGE-TOTAL | `PIC +ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Page subtotal |
| REPT-ACCOUNT-TOTAL | `PIC +ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Account subtotal |
| REPT-GRAND-TOTAL | `PIC +ZZZ,ZZZ,ZZZ.ZZ` | Edited numeric | Grand total for report |

### COSTM01.CPY — `TRNX-RECORD` (Statement-Sorted Transaction, Record Length 350)

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| TRNX-CARD-NUM | `PIC X(16)` | Alphanumeric (16) | Card number (part of composite key) |
| TRNX-ID | `PIC X(16)` | Alphanumeric (16) | Transaction ID (part of composite key) |
| TRNX-TYPE-CD | `PIC X(02)` | Alphanumeric (2) | Transaction type code |
| TRNX-CAT-CD | `PIC 9(04)` | Numeric (4) | Transaction category code |
| TRNX-SOURCE | `PIC X(10)` | Alphanumeric (10) | Transaction source |
| TRNX-DESC | `PIC X(100)` | Alphanumeric (100) | Transaction description |
| TRNX-AMT | `PIC S9(09)V99` | Signed numeric (11,2) | Transaction amount |
| TRNX-MERCHANT-ID | `PIC 9(09)` | Numeric (9) | Merchant ID |
| TRNX-MERCHANT-NAME | `PIC X(50)` | Alphanumeric (50) | Merchant name |
| TRNX-MERCHANT-CITY | `PIC X(50)` | Alphanumeric (50) | Merchant city |
| TRNX-MERCHANT-ZIP | `PIC X(10)` | Alphanumeric (10) | Merchant zip |
| TRNX-ORIG-TS | `PIC X(26)` | Alphanumeric (26) | Original timestamp |
| TRNX-PROC-TS | `PIC X(26)` | Alphanumeric (26) | Processing timestamp |
| FILLER | `PIC X(20)` | Filler | Padding |

---

## 5. Cross-Reference Entity

### CVACT03Y.cpy — `CARD-XREF-RECORD` (Record Length 50)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| XREF-CARD-NUM | `PIC X(16)` | Alphanumeric (16) | Card number — primary key | Must match CARDFILE |
| XREF-CUST-ID | `PIC 9(09)` | Numeric (9) | Customer ID linked to this card | Must match CUSTFILE |
| XREF-ACCT-ID | `PIC 9(11)` | Numeric (11) | Account ID linked to this card | Must match ACCTFILE |
| FILLER | `PIC X(14)` | Filler | Padding to 50 bytes | |

---

## 6. Export/Import Entity

### CVEXPORT.cpy — `EXPORT-RECORD` (Record Length 500)

**Header Fields:**

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| EXPORT-REC-TYPE | `PIC X(1)` | Alphanumeric (1) | Record type: C=Customer, A=Account, T=Transaction, X=Xref, D=Card |
| EXPORT-TIMESTAMP | `PIC X(26)` | Alphanumeric (26) | Export timestamp |
| EXPORT-DATE | `PIC X(10)` | Alphanumeric (10) | Export date (REDEFINES part of timestamp) |
| EXPORT-TIME | `PIC X(15)` | Alphanumeric (15) | Export time (REDEFINES part of timestamp) |
| EXPORT-SEQUENCE-NUM | `PIC 9(9) COMP` | Binary (4 bytes) | Sequence number within export |
| EXPORT-BRANCH-ID | `PIC X(4)` | Alphanumeric (4) | Source branch identifier |
| EXPORT-REGION-CODE | `PIC X(5)` | Alphanumeric (5) | Source region code |
| EXPORT-RECORD-DATA | `PIC X(460)` | Alphanumeric (460) | Payload data (REDEFINES for each entity type) |

**EXPORT-CUSTOMER-DATA** (REDEFINES EXPORT-RECORD-DATA):

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| EXP-CUST-ID | `PIC 9(09) COMP` | Binary (4 bytes) | Customer ID (packed) |
| EXP-CUST-FIRST-NAME | `PIC X(25)` | Alphanumeric | First name |
| EXP-CUST-MIDDLE-NAME | `PIC X(25)` | Alphanumeric | Middle name |
| EXP-CUST-LAST-NAME | `PIC X(25)` | Alphanumeric | Last name |
| EXP-CUST-ADDR-LINE (OCCURS 3) | `PIC X(50)` | Alphanumeric | Address lines |
| EXP-CUST-ADDR-STATE-CD | `PIC X(02)` | Alphanumeric | State code |
| EXP-CUST-ADDR-COUNTRY-CD | `PIC X(03)` | Alphanumeric | Country code |
| EXP-CUST-ADDR-ZIP | `PIC X(10)` | Alphanumeric | Zip code |
| EXP-CUST-PHONE-NUM (OCCURS 2) | `PIC X(15)` | Alphanumeric | Phone numbers |
| EXP-CUST-SSN | `PIC 9(09)` | Numeric | SSN |
| EXP-CUST-GOVT-ISSUED-ID | `PIC X(20)` | Alphanumeric | Government ID |
| EXP-CUST-DOB-YYYY-MM-DD | `PIC X(10)` | Alphanumeric | Date of birth |
| EXP-CUST-EFT-ACCOUNT-ID | `PIC X(10)` | Alphanumeric | EFT account |
| EXP-CUST-PRI-CARD-HOLDER-IND | `PIC X(01)` | Alphanumeric | Primary cardholder flag |
| EXP-CUST-FICO-CREDIT-SCORE | `PIC 9(03) COMP-3` | Packed decimal (2 bytes) | FICO score |

**EXPORT-ACCOUNT-DATA** (REDEFINES EXPORT-RECORD-DATA):

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| EXP-ACCT-ID | `PIC 9(11)` | Numeric | Account ID |
| EXP-ACCT-ACTIVE-STATUS | `PIC X(01)` | Alphanumeric | Active status |
| EXP-ACCT-CURR-BAL | `PIC S9(10)V99 COMP-3` | Packed decimal | Current balance |
| EXP-ACCT-CREDIT-LIMIT | `PIC S9(10)V99` | Signed numeric | Credit limit |
| EXP-ACCT-CASH-CREDIT-LIMIT | `PIC S9(10)V99 COMP-3` | Packed decimal | Cash credit limit |
| EXP-ACCT-OPEN-DATE | `PIC X(10)` | Alphanumeric | Open date |
| EXP-ACCT-EXPIRAION-DATE | `PIC X(10)` | Alphanumeric | Expiration date |
| EXP-ACCT-REISSUE-DATE | `PIC X(10)` | Alphanumeric | Reissue date |
| EXP-ACCT-CURR-CYC-CREDIT | `PIC S9(10)V99` | Signed numeric | Cycle credit |
| EXP-ACCT-CURR-CYC-DEBIT | `PIC S9(10)V99 COMP` | Binary | Cycle debit |
| EXP-ACCT-ADDR-ZIP | `PIC X(10)` | Alphanumeric | Zip code |
| EXP-ACCT-GROUP-ID | `PIC X(10)` | Alphanumeric | Group ID |

**EXPORT-TRANSACTION-DATA** (REDEFINES EXPORT-RECORD-DATA):

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| EXP-TRAN-ID | `PIC X(16)` | Alphanumeric | Transaction ID |
| EXP-TRAN-TYPE-CD | `PIC X(02)` | Alphanumeric | Type code |
| EXP-TRAN-CAT-CD | `PIC 9(04)` | Numeric | Category code |
| EXP-TRAN-SOURCE | `PIC X(10)` | Alphanumeric | Source |
| EXP-TRAN-DESC | `PIC X(100)` | Alphanumeric | Description |
| EXP-TRAN-AMT | `PIC S9(09)V99 COMP-3` | Packed decimal | Amount |
| EXP-TRAN-MERCHANT-ID | `PIC 9(09) COMP` | Binary | Merchant ID |
| EXP-TRAN-MERCHANT-NAME | `PIC X(50)` | Alphanumeric | Merchant name |
| EXP-TRAN-MERCHANT-CITY | `PIC X(50)` | Alphanumeric | Merchant city |
| EXP-TRAN-MERCHANT-ZIP | `PIC X(10)` | Alphanumeric | Merchant zip |
| EXP-TRAN-CARD-NUM | `PIC X(16)` | Alphanumeric | Card number |
| EXP-TRAN-ORIG-TS | `PIC X(26)` | Alphanumeric | Original timestamp |
| EXP-TRAN-PROC-TS | `PIC X(26)` | Alphanumeric | Processing timestamp |

**EXPORT-CARD-XREF-DATA** (REDEFINES EXPORT-RECORD-DATA):

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| EXP-XREF-CARD-NUM | `PIC X(16)` | Alphanumeric | Card number |
| EXP-XREF-CUST-ID | `PIC 9(09)` | Numeric | Customer ID |
| EXP-XREF-ACCT-ID | `PIC 9(11) COMP` | Binary | Account ID |

**EXPORT-CARD-DATA** (REDEFINES EXPORT-RECORD-DATA):

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| EXP-CARD-NUM | `PIC X(16)` | Alphanumeric | Card number |
| EXP-CARD-ACCT-ID | `PIC 9(11) COMP` | Binary | Account ID |
| EXP-CARD-CVV-CD | `PIC 9(03) COMP` | Binary | CVV |
| EXP-CARD-EMBOSSED-NAME | `PIC X(50)` | Alphanumeric | Embossed name |
| EXP-CARD-EXPIRAION-DATE | `PIC X(10)` | Alphanumeric | Expiration date |
| EXP-CARD-ACTIVE-STATUS | `PIC X(01)` | Alphanumeric | Active status |

---

## 7. Common / Infrastructure Copybooks

### COCOM01Y.cpy — `CARDDEMO-COMMAREA` (Inter-Program Communication Area)

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| CDEMO-FROM-TRANID | `PIC X(04)` | Alphanumeric (4) | Source transaction ID |
| CDEMO-FROM-PROGRAM | `PIC X(08)` | Alphanumeric (8) | Source program name |
| CDEMO-TO-TRANID | `PIC X(04)` | Alphanumeric (4) | Target transaction ID |
| CDEMO-TO-PROGRAM | `PIC X(08)` | Alphanumeric (8) | Target program name |
| CDEMO-USER-ID | `PIC X(08)` | Alphanumeric (8) | Logged-in user ID |
| CDEMO-USER-TYPE | `PIC X(01)` | Alphanumeric (1) | User type (88: 'A'=Admin, 'U'=User) |
| CDEMO-PGM-CONTEXT | `PIC 9(01)` | Numeric (1) | Program context (88: 0=ENTER, 1=REENTER) |
| CDEMO-CUST-ID | `PIC 9(09)` | Numeric (9) | Selected customer ID |
| CDEMO-CUST-FNAME | `PIC X(25)` | Alphanumeric (25) | Customer first name |
| CDEMO-CUST-MNAME | `PIC X(25)` | Alphanumeric (25) | Customer middle name |
| CDEMO-CUST-LNAME | `PIC X(25)` | Alphanumeric (25) | Customer last name |
| CDEMO-ACCT-ID | `PIC 9(11)` | Numeric (11) | Selected account ID |
| CDEMO-ACCT-STATUS | `PIC X(01)` | Alphanumeric (1) | Account status |
| CDEMO-CARD-NUM | `PIC 9(16)` | Numeric (16) | Selected card number |
| CDEMO-LAST-MAP | `PIC X(7)` | Alphanumeric (7) | Last displayed BMS map |
| CDEMO-LAST-MAPSET | `PIC X(7)` | Alphanumeric (7) | Last displayed BMS mapset |

### CSUSR01Y.cpy — `SEC-USER-DATA` (Security User Record, Record Length 80)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|:-----------|:-----------|:----------|:-----------------|:-----------------|
| SEC-USR-ID | `PIC X(08)` | Alphanumeric (8) | User login ID (primary key for USRSEC) | Must be non-blank |
| SEC-USR-FNAME | `PIC X(20)` | Alphanumeric (20) | User first name | |
| SEC-USR-LNAME | `PIC X(20)` | Alphanumeric (20) | User last name | |
| SEC-USR-PWD | `PIC X(08)` | Alphanumeric (8) | User password (cleartext) | COSGN00C compares with input |
| SEC-USR-TYPE | `PIC X(01)` | Alphanumeric (1) | User type: 'A'=Admin, 'U'=Regular | Determines menu routing |
| SEC-USR-FILLER | `PIC X(23)` | Filler | Padding to 80 bytes | |

### COTTL01Y.cpy — `CCDA-SCREEN-TITLE` (Screen Title Constants)

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| CCDA-TITLE01 | `PIC X(40)` | Alphanumeric | Title line 1 (value: 'AWS Mainframe Modernization') |
| CCDA-TITLE02 | `PIC X(40)` | Alphanumeric | Title line 2 (value: 'CardDemo') |
| CCDA-THANK-YOU | `PIC X(40)` | Alphanumeric | Thank-you message on exit |

### CSDAT01Y.cpy — `WS-DATE-TIME` (Date/Time Working Storage)

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| WS-CURDATE-YEAR | `PIC 9(04)` | Numeric (4) | Current year |
| WS-CURDATE-MONTH | `PIC 9(02)` | Numeric (2) | Current month |
| WS-CURDATE-DAY | `PIC 9(02)` | Numeric (2) | Current day |
| WS-CURDATE-N | `PIC 9(08)` | Numeric (8) | Date as YYYYMMDD numeric (REDEFINES) |
| WS-CURTIME-HOURS | `PIC 9(02)` | Numeric (2) | Current hours |
| WS-CURTIME-MINUTE | `PIC 9(02)` | Numeric (2) | Current minutes |
| WS-CURTIME-SECOND | `PIC 9(02)` | Numeric (2) | Current seconds |
| WS-CURTIME-MILSEC | `PIC 9(02)` | Numeric (2) | Milliseconds |
| WS-CURDATE-MM | `PIC 9(02)` | Numeric | Formatted month (MM/DD/YY) |
| WS-CURDATE-DD | `PIC 9(02)` | Numeric | Formatted day |
| WS-CURDATE-YY | `PIC 9(02)` | Numeric | Formatted year (2-digit) |
| WS-TIMESTAMP (composite) | various | Alphanumeric | Full timestamp (YYYY-MM-DD HH:MM:SS.ffffff) |
| WS-TIMESTAMP-TM-MS6 | `PIC 9(06)` | Numeric (6) | Microseconds in timestamp |

### CSMSG01Y.cpy — `CCDA-COMMON-MESSAGES`

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| CCDA-MSG-THANK-YOU | `PIC X(50)` | Alphanumeric | Thank-you message constant |
| CCDA-MSG-INVALID-KEY | `PIC X(50)` | Alphanumeric | Invalid key pressed message |

### CSMSG02Y.cpy — `ABEND-DATA` (Abend Error Handling)

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| ABEND-CODE | `PIC X(4)` | Alphanumeric (4) | Abend code |
| ABEND-CULPRIT | `PIC X(8)` | Alphanumeric (8) | Program causing abend |
| ABEND-REASON | `PIC X(50)` | Alphanumeric (50) | Abend reason description |
| ABEND-MSG | `PIC X(72)` | Alphanumeric (72) | Full abend message |

### CODATECN.cpy — `CODATECN-REC` (Date Conversion Utility)

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| CODATECN-TYPE | `PIC X` | Alphanumeric (1) | Input type: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| CODATECN-INP-DATE | `PIC X(20)` | Alphanumeric (20) | Input date string |
| CODATECN-1YYYY | `PIC XXXX` | Alphanumeric (4) | Year from YYYYMMDD input (REDEFINES) |
| CODATECN-1MM | `PIC XX` | Alphanumeric (2) | Month from YYYYMMDD input |
| CODATECN-1DD | `PIC XX` | Alphanumeric (2) | Day from YYYYMMDD input |
| CODATECN-OUTTYPE | `PIC X` | Alphanumeric (1) | Output type: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| CODATECN-0UT-DATE | `PIC X(20)` | Alphanumeric (20) | Output date string |
| CODATECN-ERROR-MSG | `PIC X(38)` | Alphanumeric (38) | Error message from conversion |

### CSUTLDWY.cpy — Date Validation Working Storage

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| WS-EDIT-DATE-CC | `PIC X(2)` | Alphanumeric (2) | Century (88: 20=THIS-CENTURY, 19=LAST-CENTURY) |
| WS-EDIT-DATE-YY | `PIC X(2)` | Alphanumeric (2) | Year within century |
| WS-EDIT-DATE-CCYY-N | `PIC 9(4)` | Numeric (4) | Full year numeric (REDEFINES) |
| WS-EDIT-DATE-MM | `PIC X(2)` | Alphanumeric (2) | Month (88: 1–12 valid, Feb/31-day checks) |
| WS-EDIT-DATE-DD | `PIC X(2)` | Alphanumeric (2) | Day (88: 1–31 valid, 29/30/31 edge cases) |
| WS-EDIT-DATE-BINARY | `PIC S9(9) BINARY` | Binary | Lilian date for LE CEEDAYS call |
| WS-CURRENT-DATE-YYYYMMDD | `PIC X(8)` | Alphanumeric (8) | Current system date for comparison |
| WS-EDIT-DATE-FLGS (composite) | `PIC X(01)` each | Flags | Validation result flags per component |
| WS-DATE-FORMAT | `PIC X(08)` | Alphanumeric (8) | Date format mask (default: 'YYYYMMDD') |

### CSLKPCDY.cpy — Lookup Code Repository (1318 lines)

Contains embedded 88-level validation tables:

| Section | Field | Business Meaning |
|:--------|:------|:-----------------|
| North America Phone Area Codes | `WS-US-PHONE-AREA-CODE-TO-EDIT PIC XXX` with 88 `VALID-PHONE-AREA-CODE` | ~350 valid NANPA area codes from 201–989 |
| US State Codes | `WS-US-STATE-CD-TO-EDIT PIC XX` with 88 `VALID-US-STATE-CD` | All 50 states + DC + territories |
| State + Zip Prefix | `WS-US-STATE-ZIP-TO-EDIT PIC X(4)` with 88 `VALID-US-STATE-ZIP` | State code + first 2 digits of zip for validation |

### CSSETATY.cpy — Screen Field Attribute Setter (Template Copybook)

Template copybook using replacement variables `(TESTVAR1)`, `(SCRNVAR2)`, `(MAPNAME3)`. Sets DFHRED attribute on error fields and `'*'` marker on blank fields. Included multiple times per program with REPLACING.

### CSSTRPFY.cpy — Store PF Key (Procedure Division Copybook)

Maps CICS EIBAID (attention identifier byte) to `CCARD-AID` values via EVALUATE statement. Maps DFHENTER, DFHCLEAR, DFHPA1–2, DFHPF1–24 to corresponding `CCARD-AID-xxx` 88-level values.

### CSUTLDPY.cpy — Date Validation (Procedure Division Copybook)

Contains reusable paragraphs:
- `EDIT-DATE-CCYYMMDD` — orchestrates full date validation
- `EDIT-YEAR-CCYY` — validates century (19 or 20) and year numeric
- `EDIT-MONTH` — validates month 1–12
- `EDIT-DAY` — validates day 1–31
- `EDIT-DAY-MONTH-YEAR` — cross-validates day vs. month (31-day, 30-day, February/leap year)

### COADM02Y.cpy — `CARDDEMO-ADMIN-MENU-OPTIONS`

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| CDEMO-ADMIN-OPT-COUNT | `PIC 9(02)` | Numeric (2) | Number of admin options (VALUE 6) |
| CDEMO-ADMIN-OPT-NUM (OCCURS 9) | `PIC 9(02)` | Numeric | Option number |
| CDEMO-ADMIN-OPT-NAME (OCCURS 9) | `PIC X(35)` | Alphanumeric | Option label (User List, User Add, etc.) |
| CDEMO-ADMIN-OPT-PGMNAME (OCCURS 9) | `PIC X(08)` | Alphanumeric | Target program name |

Options: 1=COUSR00C (User List), 2=COUSR01C (User Add), 3=COUSR02C (User Update), 4=COUSR03C (User Delete), 5=COTRTLIC (Transaction Type List/DB2), 6=COTRTUPC (Transaction Type Maintenance/DB2)

### COMEN02Y.cpy — `CARDDEMO-MAIN-MENU-OPTIONS`

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| CDEMO-MENU-OPT-COUNT | `PIC 9(02)` | Numeric (2) | Number of menu options (VALUE 11) |
| CDEMO-MENU-OPT-NUM (OCCURS 12) | `PIC 9(02)` | Numeric | Option number |
| CDEMO-MENU-OPT-NAME (OCCURS 12) | `PIC X(35)` | Alphanumeric | Option label |
| CDEMO-MENU-OPT-PGMNAME (OCCURS 12) | `PIC X(08)` | Alphanumeric | Target program name |
| CDEMO-MENU-OPT-USRTYPE (OCCURS 12) | `PIC X(01)` | Alphanumeric | Required user type |

Options: 1=COACTVWC, 2=COACTUPC, 3=COCRDLIC, 4=COCRDSLC, 5=COCRDUPC, 6=COTRN00C, 7=COTRN01C, 8=COTRN02C, 9=CORPT00C, 10=COBIL00C, 11=COPAUS0C

### UNUSED1Y.cpy — `UNUSED-DATA`

| Field Name | PIC Clause | Data Type | Business Meaning |
|:-----------|:-----------|:----------|:-----------------|
| UNUSED-ID | `PIC X(08)` | Alphanumeric (8) | Unused user ID |
| UNUSED-FNAME | `PIC X(20)` | Alphanumeric (20) | Unused first name |
| UNUSED-LNAME | `PIC X(20)` | Alphanumeric (20) | Unused last name |
| UNUSED-PWD | `PIC X(08)` | Alphanumeric (8) | Unused password |
| UNUSED-TYPE | `PIC X(01)` | Alphanumeric (1) | Unused type |
| UNUSED-FILLER | `PIC X(23)` | Filler | Padding |

Same structure as SEC-USER-DATA (CSUSR01Y). Appears to be an unused placeholder copy.

---

## 8. BMS Screen Copybooks (`app/cpy-bms/`)

BMS (Basic Mapping Support) copybooks define CICS terminal screen layouts. Each generates an input map (suffix `I`) and output map (suffix `O`) with fields corresponding to screen positions.

| Copybook | Map Name | Associated Program | Screen Description |
|:---------|:---------|:-------------------|:-------------------|
| COACTUP.CPY | COACTUP | COACTUPC | Account update screen |
| COACTVW.CPY | COACTVW | COACTVWC | Account view screen |
| COADM01.CPY | COADM01 | COADM01C | Admin menu screen |
| COBIL00.CPY | COBIL00 | COBIL00C | Bill payment screen |
| COCRDLI.CPY | COCRDLI | COCRDLIC | Card list screen |
| COCRDSL.CPY | COCRDSL | COCRDSLC | Card detail screen |
| COCRDUP.CPY | COCRDUP | COCRDUPC | Card update screen |
| COMEN01.CPY | COMEN01 | COMEN01C | Main menu screen |
| CORPT00.CPY | CORPT00 | CORPT00C | Report submission screen |
| COSGN00.CPY | COSGN00 | COSGN00C | Signon screen |
| COTRN00.CPY | COTRN00 | COTRN00C | Transaction list screen |
| COTRN01.CPY | COTRN01 | COTRN01C | Transaction view screen |
| COTRN02.CPY | COTRN02 | COTRN02C | Add transaction screen |
| COUSR00.CPY | COUSR00 | COUSR00C | User list screen |
| COUSR01.CPY | COUSR01 | COUSR01C | User add screen |
| COUSR02.CPY | COUSR02 | COUSR02C | User update screen |
| COUSR03.CPY | COUSR03 | COUSR03C | User delete screen |

---

## 9. DB2 Table Declarations (`app/app-*/dcl/`)

### AUTHFRDS.dcl — `CARDDEMO.AUTHFRDS` (Fraud Authorization Records)

| Column | SQL Type | COBOL PIC | Business Meaning |
|:-------|:---------|:----------|:-----------------|
| CARD_NUM | CHAR(16) NOT NULL | `PIC X(16)` | Card number (PK part) |
| AUTH_TS | TIMESTAMP NOT NULL | `PIC X(26)` | Authorization timestamp (PK part) |
| AUTH_TYPE | CHAR(4) | `PIC X(4)` | Authorization type |
| CARD_EXPIRY_DATE | CHAR(4) | `PIC X(4)` | Card expiry (MMYY) |
| MESSAGE_TYPE | CHAR(6) | `PIC X(6)` | Message type code |
| MESSAGE_SOURCE | CHAR(6) | `PIC X(6)` | Message source |
| AUTH_ID_CODE | CHAR(6) | `PIC X(6)` | Authorization ID code |
| AUTH_RESP_CODE | CHAR(2) | `PIC X(2)` | Response code |
| AUTH_RESP_REASON | CHAR(4) | `PIC X(4)` | Response reason |
| PROCESSING_CODE | CHAR(6) | `PIC X(6)` | Processing code |
| TRANSACTION_AMT | DECIMAL(12,2) | `PIC S9(10)V9(2) COMP-3` | Transaction amount |
| APPROVED_AMT | DECIMAL(12,2) | `PIC S9(10)V9(2) COMP-3` | Approved amount |
| MERCHANT_CATAGORY_CODE | CHAR(4) | `PIC X(4)` | Merchant category code |
| ACQR_COUNTRY_CODE | CHAR(3) | `PIC X(3)` | Acquirer country code |
| POS_ENTRY_MODE | SMALLINT | `PIC S9(4) COMP` | POS entry mode |
| MERCHANT_ID | CHAR(15) | `PIC X(15)` | Merchant identifier |
| MERCHANT_NAME | VARCHAR(22) | `PIC X(22)` | Merchant name |
| MERCHANT_CITY | CHAR(13) | `PIC X(13)` | Merchant city |
| MERCHANT_STATE | CHAR(2) | `PIC X(2)` | Merchant state |
| MERCHANT_ZIP | CHAR(9) | `PIC X(9)` | Merchant zip |
| TRANSACTION_ID | CHAR(15) | `PIC X(15)` | Transaction identifier |
| MATCH_STATUS | CHAR(1) | `PIC X(1)` | Match status flag |
| AUTH_FRAUD | CHAR(1) | `PIC X(1)` | Fraud indicator |
| FRAUD_RPT_DATE | DATE | `PIC X(10)` | Fraud report date |
| ACCT_ID | DECIMAL(11,0) | `PIC S9(11)V COMP-3` | Account identifier |
| CUST_ID | DECIMAL(9,0) | `PIC S9(9)V COMP-3` | Customer identifier |

### DCLTRTYP.dcl — `CARDDEMO.TRANSACTION_TYPE`

| Column | SQL Type | COBOL PIC | Business Meaning |
|:-------|:---------|:----------|:-----------------|
| TR_TYPE | CHAR(2) NOT NULL | `PIC X(2)` | Transaction type code (PK) |
| TR_DESCRIPTION | VARCHAR(50) NOT NULL | `PIC X(50)` (with length field) | Type description |

### DCLTRCAT.dcl — `CARDDEMO.TRANSACTION_TYPE_CATEGORY`

| Column | SQL Type | COBOL PIC | Business Meaning |
|:-------|:---------|:----------|:-----------------|
| TRC_TYPE_CODE | CHAR(2) NOT NULL | `PIC X(2)` | Type code (PK part, FK to TRANSACTION_TYPE) |
| TRC_TYPE_CATEGORY | CHAR(4) NOT NULL | `PIC X(4)` | Category code (PK part) |
| TRC_CAT_DATA | VARCHAR(50) NOT NULL | `PIC X(50)` (with length field) | Category description |
