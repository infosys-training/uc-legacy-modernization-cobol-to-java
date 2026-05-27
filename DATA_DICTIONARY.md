# DATA DICTIONARY — CardDemo COBOL Estate

> Extracted from all copybooks in `app/cpy/`, `app/app-authorization-ims-db2-mq/cpy/`, and `app/app-transaction-type-db2/cpy/`.
> Fields are grouped by business entity.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| ACCT-ID | PIC 9(11) | Numeric (11 digits) | Unique account identifier | Primary key; must be 11 digits |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Account status flag | 'Y' = active, 'N' = inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (10.2) | Current account balance | Signed; allows negative (overpayment) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (10.2) | Approved credit limit | Must be > 0 |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal (10.2) | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (10) | Date account was opened | Format YYYY-MM-DD; validated via CSUTLDTC |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Account/card expiration date | Format YYYY-MM-DD; must be > open date |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (10) | Last card reissue date | Format YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal (10.2) | Current cycle credits (payments received) | Accumulated within billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal (10.2) | Current cycle debits (charges posted) | Accumulated within billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account holder ZIP/postal code | Validated against CSLKPCDY state+ZIP table |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure/interest rate group | Links to DIS-GROUP-RECORD (CVTRA02Y) |
| FILLER | PIC X(178) | Filler | Reserved padding to 300 bytes | — |

---

## 2. Customer Entity

### CVCUS01Y.cpy / CUSTREC.cpy — Customer Record (RECLN 500)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| CUST-ID | PIC 9(09) | Numeric (9 digits) | Unique customer identifier | Primary key |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric (25) | Customer first name | Required; non-blank |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric (25) | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric (25) | Customer last name | Required; non-blank |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Street address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Street address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Street address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric (2) | US state code | Validated against CSLKPCDY 88-level state code list |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric (3) | Country code | 3-char ISO country code |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | ZIP/postal code | Validated: state + first 2 digits of ZIP via CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone number | Area code validated against CSLKPCDY NANPA list |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone number | Area code validated against CSLKPCDY NANPA list |
| CUST-SSN | PIC 9(09) | Numeric (9 digits) | Social Security Number | 9-digit numeric; unique per customer |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID (e.g. driver's license) | Optional |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (10) | Date of birth | Format YYYY-MM-DD |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | Electronic Funds Transfer (bank) account ID | Used for autopay/EFT |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric (1) | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3 digits) | FICO credit score | Range 300–850 |
| FILLER | PIC X(168) | Filler | Reserved padding to 500 bytes | — |

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number | Primary key; 16-digit PAN |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Associated account ID | FK to ACCOUNT-RECORD.ACCT-ID |
| CARD-CVV-CD | PIC 9(03) | Numeric (3 digits) | Card verification value | 3-digit CVV code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name printed on card | Usually FIRST + LAST name |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Card expiration date | Format YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric (1) | Card active status | 'Y' = active, 'N' = inactive/cancelled |
| FILLER | PIC X(59) | Filler | Reserved padding to 150 bytes | — |

### CVACT03Y.cpy — Card Cross-Reference (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | FK to CARD-RECORD.CARD-NUM |
| XREF-CUST-ID | PIC 9(09) | Numeric (9 digits) | Customer ID | FK to CUSTOMER-RECORD.CUST-ID |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11 digits) | Account ID | FK to ACCOUNT-RECORD.ACCT-ID |
| FILLER | PIC X(14) | Filler | Reserved padding to 50 bytes | — |

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Unique transaction identifier | System-generated; primary key |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code | FK to TRAN-TYPE-RECORD.TRAN-TYPE |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4 digits) | Transaction category code | FK to TRAN-CAT-RECORD |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source (channel) | e.g. 'POS', 'ATM', 'ONLINE', 'BATCH' |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | Free-text description |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (9.2) | Transaction amount | Signed; negative = credit/refund |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9 digits) | Merchant identifier | — |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | — |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | — |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | — |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number used | FK to CARD-RECORD.CARD-NUM |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Origination timestamp | Format YYYY-MM-DD-HH.MM.SS.FFFFFF |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | Format YYYY-MM-DD-HH.MM.SS.FFFFFF |
| FILLER | PIC X(20) | Filler | Reserved padding to 350 bytes | — |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same structure as CVTRA05Y but prefixed `DALYTRAN-` instead of `TRAN-`. Used for incoming daily batch transaction feed before posting to the main transaction file.

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric (16) | Daily transaction identifier |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Source channel |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric (100) | Description |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal (9.2) | Transaction amount |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant ID |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number used |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Origination timestamp |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp |
| FILLER | PIC X(20) | Filler | Padding |

### COSTM01.CPY — Statement Transaction Layout (Key-Reordered for Report)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number (first key for sort) |
| TRNX-ID | PIC X(16) | Alphanumeric (16) | Transaction ID (second key) |
| TRNX-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code |
| TRNX-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code |
| TRNX-SOURCE | PIC X(10) | Alphanumeric (10) | Source channel |
| TRNX-DESC | PIC X(100) | Alphanumeric (100) | Description |
| TRNX-AMT | PIC S9(09)V99 | Signed decimal (9.2) | Transaction amount |
| TRNX-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant ID |
| TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name |
| TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city |
| TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP |
| TRNX-ORIG-TS | PIC X(26) | Alphanumeric (26) | Origination timestamp |
| TRNX-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp |
| FILLER | PIC X(20) | Filler | Padding |

---

## 5. Transaction Reference Data

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric (2) | Transaction type code | Primary key; e.g. 'SA' (sale), 'CR' (credit) |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Description of the type | Human-readable label |
| FILLER | PIC X(08) | Filler | Padding | — |

### CVTRA04Y.cpy — Transaction Category Type (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code (composite key part 1) | FK to TRAN-TYPE-RECORD |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code (composite key part 2) | — |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | e.g. 'Retail Purchase', 'Cash Advance' |
| FILLER | PIC X(04) | Filler | Padding | — |

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID (composite key part 1) | FK to ACCOUNT-RECORD |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code (composite key part 2) | FK to TRAN-TYPE-RECORD |
| TRANCAT-CD | PIC 9(04) | Numeric (4) | Category code (composite key part 3) | FK to TRAN-CAT-RECORD |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal (9.2) | Running balance for this acct+type+category | Updated during batch posting |
| FILLER | PIC X(22) | Filler | Padding | — |

### CVTRA02Y.cpy — Disclosure Group / Interest Rate (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Disclosure group ID (composite key part 1) | FK to ACCOUNT-RECORD.ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric (2) | Transaction type code (composite key part 2) | — |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code (composite key part 3) | — |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (4.2) | Interest rate percentage for this group+type+category | Used in CBACT04C interest calculation |
| FILLER | PIC X(28) | Filler | Padding | — |

---

## 6. Report Structures

### CVTRA07Y.cpy — Daily Transaction Report Layout

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report identifier ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report title ('Daily Transaction Report') |
| REPT-DATE-HEADER | PIC X(12) | Alphanumeric | Date range header |
| REPT-START-DATE | PIC X(10) | Alphanumeric | Report start date |
| REPT-END-DATE | PIC X(10) | Alphanumeric | Report end date |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID for report line |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID for report line |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Source channel |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted transaction amount |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total |

---

## 7. Export / Import Entity

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| EXPORT-REC-TYPE | PIC X(1) | Alphanumeric (1) | Record type discriminator ('C'=Customer, 'A'=Account, 'X'=Xref, 'T'=Transaction, 'R'=Card) |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric (26) | Export generation timestamp |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary (4 bytes) | Record sequence within export |
| EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric (4) | Source branch identifier |
| EXPORT-REGION-CODE | PIC X(5) | Alphanumeric (5) | Source region code |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric (460) | Payload — REDEFINES to entity-specific layouts below |

**Redefined sub-structures for each record type:**

- **Customer:** EXP-CUST-ID (PIC 9(9) COMP), EXP-CUST-FIRST-NAME (X(25)), EXP-CUST-LAST-NAME (X(25)), EXP-CUST-ADDR-* fields, EXP-CUST-SSN (9(9) COMP), EXP-CUST-FICO (9(3) COMP), etc.
- **Account:** EXP-ACCT-ID (9(11) COMP), EXP-ACCT-STATUS (X(1)), EXP-ACCT-CURR-BAL (S9(10)V99 COMP-3), EXP-ACCT-CREDIT-LIMIT (S9(10)V99 COMP-3), etc.
- **Transaction:** EXP-TRAN-ID (X(16)), EXP-TRAN-TYPE-CD (X(2)), EXP-TRAN-AMT (S9(9)V99 COMP-3), etc.
- **Card:** EXP-CARD-NUM (X(16)), EXP-CARD-ACCT-ID (9(11) COMP), EXP-CARD-CVV (9(3) COMP), etc.
- **Xref:** EXP-XREF-CARD-NUM (X(16)), EXP-XREF-CUST-ID (9(9) COMP), EXP-XREF-ACCT-ID (9(11) COMP), etc.

---

## 8. Authorization Entity (IMS/MQ Module)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Root Segment)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal | Account ID | Root segment key |
| PA-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID | FK to customer |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric (1) | Overall authorization status | 'A'=Active, 'I'=Inactive |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alphanumeric array (5×2) | Up to 5 account status indicators | — |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Credit limit at time of authorization | — |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Cash advance limit | — |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Credit balance snapshot | — |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Cash balance snapshot | — |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of approved authorizations | Running counter |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary (halfword) | Count of declined authorizations | Running counter |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Total approved amount | Running total |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal (9.2) | Total declined amount | Running total |
| FILLER | PIC X(34) | Filler | Padding | — |

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Child Segment)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal | Authorization date (key part 1) | Julian date format |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal | Authorization time (key part 2) | HHMMSS format |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric (6) | Original authorization date | YYMMDD |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric (6) | Original authorization time | HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | FK to card record |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric (4) | Authorization type | e.g. 'SALE', 'CASH' |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric (4) | Card expiry date (MMYY) | — |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric (6) | Message type code | ISO 8583 message type |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric (6) | Message source identifier | — |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric (6) | Authorization ID code | — |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric (2) | Response code | 88: '00' = approved |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric (4) | Response reason code | — |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric (6) | Processing code | — |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (10.2) | Requested transaction amount | — |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (10.2) | Approved amount | May differ from requested |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric (4) | MCC (Merchant Category Code) | Industry-standard 4-digit |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric (3) | Acquirer country code | ISO 3166 |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric (2) | Point-of-sale entry mode | ISO 8583 |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant identifier | — |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name | — |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city | — |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric (2) | Merchant state | — |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric (9) | Merchant ZIP | — |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric (15) | Transaction reference ID | — |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric (1) | Auth-to-transaction match status | 88: 'P'=Pending, 'D'=Declined, 'E'=Expired, 'M'=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric (1) | Fraud indicator | 88: 'F'=Fraud confirmed, 'R'=Fraud removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric (8) | Fraud report date | YYYYMMDD |
| FILLER | PIC X(17) | Filler | Padding | — |

### CCPAURQY.cpy — Authorization Request (MQ Message)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric (6) | Request authorization date |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric (6) | Request authorization time |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number to authorize |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric (4) | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric (4) | Card expiry (MMYY) |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric (6) | ISO 8583 message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric (6) | Message source |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric (6) | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited numeric | Transaction amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric (4) | MCC |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric (3) | Acquirer country |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric (2) | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric (15) | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric (22) | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric (13) | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric (2) | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric (9) | Merchant ZIP |

---

## 9. Security Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|-----------|------------------|------------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric (8) | User login ID | Primary key; must be non-blank |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric (20) | User first name | Required |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric (20) | User last name | Required |
| SEC-USR-PWD | PIC X(08) | Alphanumeric (8) | User password | Required; plaintext storage |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric (1) | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved padding to 80 bytes | — |

---

## 10. Application Control Structures

### COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric (4) | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric (8) | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric (4) | Target CICS transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric (8) | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric (8) | Current user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric (1) | User type ('A'=admin, 'U'=user) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric (1) | Program context (0=enter, 1=reenter) |
| CDEMO-CUST-ID | PIC 9(09) | Numeric (9) | Current customer ID (context) |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric (25) | Customer first name (context) |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric (25) | Customer middle name (context) |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric (25) | Customer last name (context) |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric (11) | Current account ID (context) |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric (1) | Account status (context) |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric (16) | Current card number (context) |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric (7) | Last BMS map name displayed |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric (7) | Last BMS mapset name |

### CVCRD01Y.cpy — Credit Card Work Areas

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| CCARD-AID | PIC X(5) | Alphanumeric (5) | Keyboard attention identifier (AID) |
| CCARD-NEXT-PROG | PIC X(8) | Alphanumeric (8) | Next program to XCTL to |
| CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric (7) | Next BMS mapset to display |
| CCARD-NEXT-MAP | PIC X(7) | Alphanumeric (7) | Next BMS map to display |
| CCARD-ERROR-MSG | PIC X(75) | Alphanumeric (75) | Error message to display |
| CCARD-RETURN-MSG | PIC X(75) | Alphanumeric (75) | Return/confirmation message |
| CC-ACCT-ID | PIC X(11) / PIC 9(11) | Both formats | Account ID (with numeric redefine) |
| CC-CARD-NUM | PIC X(16) / PIC 9(16) | Both formats | Card number (with numeric redefine) |
| CC-CUST-ID | PIC X(09) / PIC 9(9) | Both formats | Customer ID (with numeric redefine) |

---

## 11. Utility Copybooks

### CODATECN.cpy — Date Conversion Control Block

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| CODATECN-TYPE | PIC X | Alphanumeric (1) | Input format ('1'=YYYYMMDD, '2'=YYYY-MM-DD) |
| CODATECN-INP-DATE | PIC X(20) | Alphanumeric (20) | Input date string |
| CODATECN-OUTTYPE | PIC X | Alphanumeric (1) | Output format ('1'=YYYY-MM-DD, '2'=YYYYMMDD) |
| CODATECN-0UT-DATE | PIC X(20) | Alphanumeric (20) | Output date string (converted) |
| CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric (38) | Error message if conversion fails |

### CSDAT01Y.cpy — Date/Time Work Areas

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| WS-CURDATE (YEAR/MONTH/DAY) | PIC 9(04), 9(02), 9(02) | Numeric | Current date components |
| WS-CURTIME (H/M/S/MS) | PIC 9(02) each | Numeric | Current time components |
| WS-CURDATE-MM-DD-YY | Formatted | Alphanumeric | Date formatted MM/DD/YY |
| WS-CURTIME-HH-MM-SS | Formatted | Alphanumeric | Time formatted HH:MM:SS |
| WS-TIMESTAMP | Formatted | Alphanumeric | Full timestamp YYYY-MM-DD HH:MM:SS.ffffff |

### CSLKPCDY.cpy — Lookup Code Repository

Contains 88-level condition-name validation tables for:
- **North American phone area codes** — complete NANPA list (~400 valid area codes)
- **US state codes** — all 50 states + territories
- **State + first 2 digits of ZIP** — geographic validation of ZIP vs state

### CSMSG01Y.cpy — Common Messages

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| CCDA-MSG-THANK-YOU | PIC X(50) | Alphanumeric | 'Thank you for using CardDemo application...' |
| CCDA-MSG-INVALID-KEY | PIC X(50) | Alphanumeric | 'Invalid key pressed. Please see below...' |

### CSMSG02Y.cpy — Abend (Abnormal End) Data

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| ABEND-CODE | PIC X(4) | Alphanumeric (4) | CICS abend code |
| ABEND-CULPRIT | PIC X(8) | Alphanumeric (8) | Program that caused the abend |
| ABEND-REASON | PIC X(50) | Alphanumeric (50) | Human-readable reason |
| ABEND-MSG | PIC X(72) | Alphanumeric (72) | Formatted error message |

### COTTL01Y.cpy — Screen Title

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|------------------|
| CCDA-TITLE01 | PIC X(40) | Alphanumeric | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | Alphanumeric | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | Alphanumeric | Thank-you/goodbye message |

---

## 12. Menu Configuration Copybooks

### COMEN02Y.cpy — Main Menu Options (Regular User)

Defines 11 menu options mapping to programs:

| Option | Label | Program |
|--------|-------|---------|
| 1 | Account View | COACTVWC |
| 2 | Account Update | COACTUPC |
| 3 | Credit Card List | COCRDLIC |
| 4 | Credit Card View | COCRDSLC |
| 5 | Credit Card Update | COCRDUPC |
| 6 | Transaction List | COTRN00C |
| 7 | Transaction View | COTRN01C |
| 8 | Transaction Add | COTRN02C |
| 9 | Transaction Reports | CORPT00C |
| 10 | Bill Payment | COBIL00C |
| 11 | Pending Authorization View | COPAUS0C |

### COADM02Y.cpy — Admin Menu Options

Defines 6 admin-only menu options:

| Option | Label | Program |
|--------|-------|---------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |
| 5 | Transaction Type List/Update (DB2) | COTRTLIC |
| 6 | Transaction Type Maintenance (DB2) | COTRTUPC |

---

## 13. DB2 Module Copybooks

### CSDB2RPY.cpy — DB2 Read-Only Parameter Block

Used by COTRTLIC for DB2 cursor-based reads of transaction type data.

### CSDB2RWY.cpy — DB2 Read-Write Parameter Block

Used by COTRTUPC for DB2 INSERT/UPDATE operations on transaction type tables.

---

## 14. Procedure Copybooks (Code Fragments)

| Copybook | Type | Purpose |
|----------|------|---------|
| CSSETATY.cpy | Procedure | Set screen field attribute to red/asterisk if validation fails |
| CSSTRPFY.cpy | Procedure | Map EIBAID to PFKey in CCARD-AID work area (EVALUATE block) |
| CSUTLDPY.cpy | Procedure | Date validation paragraph using LE CEEDAYS callable service |
| CSUTLDWY.cpy | Procedure | Working-storage fields for date validation (CEEDAYS parameters) |
| UNUSED1Y.cpy | (Placeholder) | Empty/reserved copybook; no active data definitions |
