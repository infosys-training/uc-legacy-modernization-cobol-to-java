# DATA DICTIONARY — CardDemo COBOL Estate

> Extracted from all copybooks in `app/cpy/`, `app/app-authorization-ims-db2-mq/cpy/`, and `app/app-transaction-type-db2/cpy/`.  
> Fields are grouped by business entity. Data types follow COBOL conventions:  
> `PIC X(n)` = Alphanumeric, `PIC 9(n)` = Numeric display, `PIC S9(n)V99` = Signed decimal, `COMP` = Binary, `COMP-3` = Packed decimal.

---

## 1. Account Entity

### 1.1 Account Record (`CVACT01Y.cpy` → `ACCOUNT-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | ACCT-ID | PIC 9(11) | Numeric | Unique account identifier | Primary key for ACCTDATA VSAM KSDS |
| 2 | ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Account status flag | 'Y' = Active, 'N' = Inactive |
| 3 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed Decimal | Current account balance | Allows negative (credit balance); 2 decimal places |
| 4 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Account credit limit | Maximum credit allowed |
| 5 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Cash advance credit limit | Subset of total credit limit |
| 6 | ACCT-OPEN-DATE | PIC X(10) | Alpha (date) | Date the account was opened | Format: YYYY-MM-DD |
| 7 | ACCT-EXPIRAION-DATE | PIC X(10) | Alpha (date) | Account expiration date | Typo in source ("EXPIRAION") |
| 8 | ACCT-REISSUE-DATE | PIC X(10) | Alpha (date) | Last card reissue date | — |
| 9 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed Decimal | Current billing cycle credits | Accumulated credits this cycle |
| 10 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed Decimal | Current billing cycle debits | Accumulated debits this cycle |
| 11 | ACCT-ADDR-ZIP | PIC X(10) | Alpha | Account holder ZIP code | US ZIP+4 format (5+4 digits) |
| 12 | ACCT-GROUP-ID | PIC X(10) | Alpha | Disclosure/discount group identifier | Links to DIS-GROUP-RECORD for interest rates |
| 13 | FILLER | PIC X(178) | Alpha | Reserved space | Pad to fixed record length (300 bytes total) |

### 1.2 Account Record — Export Format (`CVEXPORT.cpy` → `EXPORT-ACCOUNT-DATA`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | EXP-ACCT-ID | PIC 9(11) | Numeric | Account identifier |
| 2 | EXP-ACCT-ACTIVE-STATUS | PIC X(01) | Alpha | Active status |
| 3 | EXP-ACCT-CURR-BAL | PIC S9(10)V99 COMP-3 | Packed Decimal | Current balance (packed for export efficiency) |
| 4 | EXP-ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Credit limit |
| 5 | EXP-ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 COMP-3 | Packed Decimal | Cash credit limit (packed) |
| 6 | EXP-ACCT-OPEN-DATE | PIC X(10) | Alpha (date) | Open date |
| 7 | EXP-ACCT-EXPIRAION-DATE | PIC X(10) | Alpha (date) | Expiration date |
| 8 | EXP-ACCT-REISSUE-DATE | PIC X(10) | Alpha (date) | Reissue date |
| 9 | EXP-ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed Decimal | Current cycle credit |
| 10 | EXP-ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 COMP | Binary | Current cycle debit (binary) |
| 11 | EXP-ACCT-ADDR-ZIP | PIC X(10) | Alpha | ZIP code |
| 12 | EXP-ACCT-GROUP-ID | PIC X(10) | Alpha | Group ID |

---

## 2. Customer Entity

### 2.1 Customer Record (`CVCUS01Y.cpy` → `CUSTOMER-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | CUST-ID | PIC 9(09) | Numeric | Unique customer identifier | Primary key for CUSTDATA VSAM KSDS |
| 2 | CUST-FIRST-NAME | PIC X(25) | Alpha | Customer first name | — |
| 3 | CUST-MIDDLE-NAME | PIC X(25) | Alpha | Customer middle name | — |
| 4 | CUST-LAST-NAME | PIC X(25) | Alpha | Customer last name | — |
| 5 | CUST-ADDR-LINE-1 | PIC X(50) | Alpha | Address line 1 | — |
| 6 | CUST-ADDR-LINE-2 | PIC X(50) | Alpha | Address line 2 | — |
| 7 | CUST-ADDR-LINE-3 | PIC X(50) | Alpha | Address line 3 | — |
| 8 | CUST-ADDR-STATE-CD | PIC X(02) | Alpha | US state code | Validated against state code lookup (CSLKPCDY) |
| 9 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code | — |
| 10 | CUST-ADDR-ZIP | PIC X(10) | Alpha | ZIP code | Validated against state+ZIP lookup (CSLKPCDY) |
| 11 | CUST-PHONE-NUM-1 | PIC X(15) | Alpha | Primary phone number | Area code validated against NANPA list (CSLKPCDY) |
| 12 | CUST-PHONE-NUM-2 | PIC X(15) | Alpha | Secondary phone number | Area code validated (CSLKPCDY) |
| 13 | CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit; validated for 000000000, range checks in COACTUPC |
| 14 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alpha | Government-issued ID (passport/driver's license) | — |
| 15 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Alpha (date) | Date of birth | Format: YYYY-MM-DD |
| 16 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alpha | EFT/ACH account for electronic fund transfer | — |
| 17 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary cardholder indicator | 'Y' = Primary, 'N' = Authorized user |
| 18 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | 300-850 range |
| 19 | FILLER | PIC X(168) | Alpha | Reserved space | Pad to fixed record length (500 bytes total) |

### 2.2 Customer Record — Statement Format (`CUSTREC.cpy` → `CUSTOMER-RECORD`)

Identical field layout to CVCUS01Y except:
- `CUST-DOB-YYYYMMDD` (no hyphens) instead of `CUST-DOB-YYYY-MM-DD`

### 2.3 Customer Record — Export Format (`CVEXPORT.cpy` → `EXPORT-CUSTOMER-DATA`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | EXP-CUST-ID | PIC 9(09) COMP | Binary | Customer ID (binary for export) |
| 2 | EXP-CUST-FIRST-NAME | PIC X(25) | Alpha | First name |
| 3 | EXP-CUST-MIDDLE-NAME | PIC X(25) | Alpha | Middle name |
| 4 | EXP-CUST-LAST-NAME | PIC X(25) | Alpha | Last name |
| 5 | EXP-CUST-ADDR-LINE (×3) | PIC X(50) | Alpha | Address lines (OCCURS 3) |
| 6 | EXP-CUST-ADDR-STATE-CD | PIC X(02) | Alpha | State code |
| 7 | EXP-CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha | Country code |
| 8 | EXP-CUST-ADDR-ZIP | PIC X(10) | Alpha | ZIP code |
| 9 | EXP-CUST-PHONE-NUM (×2) | PIC X(15) | Alpha | Phone numbers (OCCURS 2) |
| 10 | EXP-CUST-SSN | PIC 9(09) | Numeric | SSN |
| 11 | EXP-CUST-GOVT-ISSUED-ID | PIC X(20) | Alpha | Gov ID |
| 12 | EXP-CUST-DOB-YYYY-MM-DD | PIC X(10) | Alpha (date) | Date of birth |
| 13 | EXP-CUST-EFT-ACCOUNT-ID | PIC X(10) | Alpha | EFT account |
| 14 | EXP-CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha | Primary holder flag |
| 15 | EXP-CUST-FICO-CREDIT-SCORE | PIC 9(03) COMP-3 | Packed Decimal | FICO score (packed) |

---

## 3. Card Entity

### 3.1 Card Record (`CVACT02Y.cpy` → `CARD-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | CARD-NUM | PIC X(16) | Alpha | Credit card number | Primary key for CARDDATA VSAM KSDS |
| 2 | CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | Foreign key → ACCOUNT-RECORD |
| 3 | CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit CVV |
| 4 | CARD-EMBOSSED-NAME | PIC X(50) | Alpha | Name embossed on card | — |
| 5 | CARD-EXPIRAION-DATE | PIC X(10) | Alpha (date) | Card expiration date | Month/year validated in COCRDUPC |
| 6 | CARD-ACTIVE-STATUS | PIC X(01) | Alpha | Card active status | 'Y' = Active, 'N' = Inactive |
| 7 | FILLER | PIC X(59) | Alpha | Reserved space | Pad to 150-byte record |

### 3.2 Card Cross-Reference Record (`CVACT03Y.cpy` → `CARD-XREF-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | XREF-CARD-NUM | PIC X(16) | Alpha | Card number | Primary key for CARDXREF VSAM KSDS |
| 2 | XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID | Foreign key → CUSTOMER-RECORD |
| 3 | XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID | Foreign key → ACCOUNT-RECORD |
| 4 | FILLER | PIC X(14) | Alpha | Reserved space | Pad to 50-byte record |

### 3.3 Card Screen Data (`CVCRD01Y.cpy`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | CC-ACCT-ID / CC-ACCT-ID-N | PIC X(11) / PIC 9(11) | Alpha/Numeric | Account ID (alphanumeric REDEFINES numeric for screen input) |
| 2 | CC-CARD-NUM / CC-CARD-NUM-N | PIC X(16) / PIC 9(16) | Alpha/Numeric | Card number (alphanumeric REDEFINES numeric for screen input) |

---

## 4. Transaction Entity

### 4.1 Transaction Record (`CVTRA05Y.cpy` → `TRAN-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | TRAN-ID | PIC X(16) | Alpha | Unique transaction identifier | Primary key for TRANSACT VSAM KSDS |
| 2 | TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type code | FK → TRAN-TYPE-RECORD; e.g., 'SA' = Sale |
| 3 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | FK → TRAN-CAT-RECORD |
| 4 | TRAN-SOURCE | PIC X(10) | Alpha | Transaction source/channel | e.g., 'POS', 'ONLINE', 'ATM' |
| 5 | TRAN-DESC | PIC X(100) | Alpha | Transaction description | Free-text description |
| 6 | TRAN-AMT | PIC S9(09)V99 | Signed Decimal | Transaction amount | Signed; credits negative, debits positive |
| 7 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | — |
| 8 | TRAN-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name | — |
| 9 | TRAN-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city | — |
| 10 | TRAN-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP code | — |
| 11 | TRAN-CARD-NUM | PIC X(16) | Alpha | Card number used | FK → CARD-RECORD |
| 12 | TRAN-ORIG-TS | PIC X(26) | Alpha (timestamp) | Original transaction timestamp | ISO timestamp format |
| 13 | TRAN-PROC-TS | PIC X(26) | Alpha (timestamp) | Processing timestamp | Set when transaction is posted |
| 14 | FILLER | PIC X(20) | Alpha | Reserved space | Pad to 350-byte record |

### 4.2 Transaction Record — Statement Format (`COSTM01.CPY` → `TRNX-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | TRNX-CARD-NUM | PIC X(16) | Alpha | Card number (part of compound key) |
| 2 | TRNX-ID | PIC X(16) | Alpha | Transaction ID (part of compound key) |
| 3 | TRNX-TYPE-CD | PIC X(02) | Alpha | Transaction type code |
| 4 | TRNX-CAT-CD | PIC 9(04) | Numeric | Category code |
| 5 | TRNX-SOURCE | PIC X(10) | Alpha | Source |
| 6 | TRNX-DESC | PIC X(100) | Alpha | Description |
| 7 | TRNX-AMT | PIC S9(09)V99 | Signed Decimal | Amount |
| 8 | TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID |
| 9 | TRNX-MERCHANT-NAME | PIC X(50) | Alpha | Merchant name |
| 10 | TRNX-MERCHANT-CITY | PIC X(50) | Alpha | Merchant city |
| 11 | TRNX-MERCHANT-ZIP | PIC X(10) | Alpha | Merchant ZIP |
| 12 | TRNX-ORIG-TS | PIC X(26) | Alpha (timestamp) | Original timestamp |
| 13 | TRNX-PROC-TS | PIC X(26) | Alpha (timestamp) | Processing timestamp |

> **Note:** `TRNX-RECORD` has a compound key of `TRNX-CARD-NUM + TRNX-ID` (32 bytes) compared to `TRAN-RECORD` which keys on `TRAN-ID` alone.

### 4.3 Daily Transaction Record (`CVTRA06Y.cpy` → `DALYTRAN-RECORD`)

Identical field layout to TRAN-RECORD but with `DALYTRAN-` prefix. This is the daily inbound transaction file before posting.

### 4.4 Transaction Type Reference (`CVTRA03Y.cpy` → `TRAN-TYPE-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | TRAN-TYPE | PIC X(02) | Alpha | Transaction type code | Primary key for TRANTYPE VSAM KSDS |
| 2 | TRAN-TYPE-DESC | PIC X(50) | Alpha | Transaction type description | e.g., 'Purchase', 'Cash Advance', 'Payment' |
| 3 | FILLER | PIC X(08) | Alpha | Reserved | 60-byte record |

### 4.5 Transaction Category Reference (`CVTRA04Y.cpy` → `TRAN-CAT-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type (compound key part 1) | FK → TRAN-TYPE-RECORD |
| 2 | TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (compound key part 2) | Primary key for TRANCATG VSAM KSDS |
| 3 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alpha | Category description | e.g., 'Groceries', 'Gas', 'Dining' |
| 4 | FILLER | PIC X(04) | Alpha | Reserved | 60-byte record |

### 4.6 Transaction Category Balance (`CVTRA01Y.cpy` → `TRAN-CAT-BAL-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID (compound key part 1) | FK → ACCOUNT-RECORD |
| 2 | TRANCAT-TYPE-CD | PIC X(02) | Alpha | Transaction type (compound key part 2) | FK → TRAN-TYPE-RECORD |
| 3 | TRANCAT-CD | PIC 9(04) | Numeric | Category code (compound key part 3) | FK → TRAN-CAT-RECORD |
| 4 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed Decimal | Accumulated balance for this category | Running balance per account/type/category |
| 5 | FILLER | PIC X(22) | Alpha | Reserved | 50-byte record |

### 4.7 Disclosure/Discount Group (`CVTRA02Y.cpy` → `DIS-GROUP-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | DIS-ACCT-GROUP-ID | PIC X(10) | Alpha | Account group ID (compound key part 1) | FK → ACCT-GROUP-ID |
| 2 | DIS-TRAN-TYPE-CD | PIC X(02) | Alpha | Transaction type (compound key part 2) | FK → TRAN-TYPE-RECORD |
| 3 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Category code (compound key part 3) | FK → TRAN-CAT-RECORD |
| 4 | DIS-INT-RATE | PIC S9(04)V99 | Signed Decimal | Interest rate for this group/type/category | Percentage rate (e.g., 18.99) |
| 5 | FILLER | PIC X(28) | Alpha | Reserved | 50-byte record |

### 4.8 Transaction Report Layout (`CVTRA07Y.cpy`)

| # | Field/Group Name | PIC Clause | Data Type | Business Meaning |
|---|-----------------|------------|-----------|-----------------|
| 1 | REPT-SHORT-NAME | PIC X(38) | Alpha | Report short title |
| 2 | REPT-LONG-NAME | PIC X(41) | Alpha | Report long title |
| 3 | REPT-DATE-HEADER | PIC X(12) | Alpha | "Date Range:" header label |
| 4 | REPT-START-DATE | PIC X(10) | Alpha | Report start date |
| 5 | REPT-END-DATE | PIC X(10) | Alpha | Report end date |
| 6 | TRAN-REPORT-TRANS-ID | PIC X(16) | Alpha | Transaction ID in report line |
| 7 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alpha | Account ID in report line |
| 8 | TRAN-REPORT-TYPE-CD | PIC X(02) | Alpha | Type code |
| 9 | TRAN-REPORT-TYPE-DESC | PIC X(15) | Alpha | Type description |
| 10 | TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| 11 | TRAN-REPORT-CAT-DESC | PIC X(29) | Alpha | Category description |
| 12 | TRAN-REPORT-SOURCE | PIC X(10) | Alpha | Transaction source |
| 13 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Transaction amount (formatted) |
| 14 | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Page subtotal |
| 15 | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Account subtotal |
| 16 | REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Grand total |

### 4.9 Export Wrapper Record (`CVEXPORT.cpy` → `EXPORT-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | EXPORT-REC-TYPE | PIC X(1) | Alpha | Record type: 'C'=Customer, 'A'=Account, 'X'=Xref, 'T'=Transaction, 'R'=Card |
| 2 | EXPORT-TIMESTAMP | PIC X(26) | Alpha | Export timestamp |
| 3 | EXPORT-DATE | PIC X(10) | Alpha | Date portion (REDEFINES) |
| 4 | EXPORT-TIME | PIC X(15) | Alpha | Time portion (REDEFINES) |
| 5 | EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number |
| 6 | EXPORT-BRANCH-ID | PIC X(4) | Alpha | Branch identifier |
| 7 | EXPORT-REGION-CODE | PIC X(5) | Alpha | Region code |
| 8 | EXPORT-RECORD-DATA | PIC X(460) | Alpha | Polymorphic data area — REDEFINES for Customer/Account/Transaction/Card |

---

## 5. Security / User Entity

### 5.1 User Security Record (`CSUSR01Y.cpy` → `SEC-USER-DATA`)

| # | Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|---|-----------|------------|-----------|-----------------|-------------------|
| 1 | SEC-USR-ID | PIC X(08) | Alpha | User login ID | Primary key for USRSEC VSAM KSDS |
| 2 | SEC-USR-FNAME | PIC X(20) | Alpha | User first name | — |
| 3 | SEC-USR-LNAME | PIC X(20) | Alpha | User last name | — |
| 4 | SEC-USR-PWD | PIC X(08) | Alpha | User password | Stored in plain text (legacy) |
| 5 | SEC-USR-TYPE | PIC X(01) | Alpha | User type | 'A' = Admin, 'U' = Regular user |
| 6 | SEC-USR-FILLER | PIC X(23) | Alpha | Reserved | 80-byte record |

---

## 6. Authorization Entity (Sub-App)

### 6.1 Pending Auth Summary (`CIPAUSMY.cpy`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | PA-SM-CARD-NUM | PIC X(16) | Alpha | Card number |
| 2 | PA-SM-TRANS-COUNT | PIC S9(09) COMP-3 | Packed Decimal | Total pending auth count for this card |
| 3 | PA-SM-TOTAL-AMT | PIC S9(10)V99 COMP-3 | Packed Decimal | Total pending authorization amount |

### 6.2 Pending Auth Detail (`CIPAUDTY.cpy`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed Decimal | Auth date (compressed key part 1) |
| 2 | PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed Decimal | Auth time (compressed key part 2) |
| 3 | PA-AUTH-ORIG-DATE | PIC X(06) | Alpha | Original auth date (YYMMDD) |
| 4 | PA-AUTH-ORIG-TIME | PIC X(06) | Alpha | Original auth time (HHMMSS) |
| 5 | PA-CARD-NUM | PIC X(16) | Alpha | Card number |
| 6 | PA-AUTH-TYPE | PIC X(04) | Alpha | Authorization type |
| 7 | PA-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry (YYMM) |
| 8 | PA-MESSAGE-TYPE | PIC X(06) | Alpha | Message type code |
| 9 | PA-MESSAGE-SOURCE | PIC X(06) | Alpha | Message source |
| 10 | PA-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization ID code (approval code) |
| 11 | PA-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code (00=approved, etc.) |
| 12 | PA-AUTH-RESP-REASON | PIC X(04) | Alpha | Response reason code |
| 13 | PA-PROCESSING-CODE | PIC 9(06) | Numeric | ISO 8583 processing code |
| 14 | PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed Decimal | Requested transaction amount |
| 15 | PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed Decimal | Approved amount |
| 16 | PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | Merchant category code (MCC) |
| 17 | PA-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country code |
| 18 | PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode (chip/swipe/manual) |
| 19 | PA-MERCHANT-ID | PIC X(15) | Alpha | Merchant identifier |
| 20 | PA-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name |
| 21 | PA-MERCHANT-CITY | PIC X(13) | Alpha | Merchant city |
| 22 | PA-MERCHANT-STATE | PIC X(02) | Alpha | Merchant state |
| 23 | PA-MERCHANT-ZIP | PIC X(09) | Alpha | Merchant ZIP |
| 24 | PA-TRANSACTION-ID | PIC X(15) | Alpha | Transaction reference ID |

### 6.3 Auth MQ Request (`CCPAURQY.cpy`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | PA-RQ-AUTH-DATE | PIC X(06) | Alpha | Request date |
| 2 | PA-RQ-AUTH-TIME | PIC X(06) | Alpha | Request time |
| 3 | PA-RQ-CARD-NUM | PIC X(16) | Alpha | Card number |
| 4 | PA-RQ-AUTH-TYPE | PIC X(04) | Alpha | Auth type |
| 5 | PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry |
| 6 | PA-RQ-MESSAGE-TYPE | PIC X(06) | Alpha | Message type |
| 7 | PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alpha | Message source |
| 8 | PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| 9 | PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited Numeric | Transaction amount |
| 10 | PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alpha | MCC |
| 11 | PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alpha | Acquirer country |
| 12 | PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| 13 | PA-RQ-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID |
| 14 | PA-RQ-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name |
| 15 | PA-RQ-MERCHANT-CITY | PIC X(13) | Alpha | City |
| 16 | PA-RQ-MERCHANT-STATE | PIC X(02) | Alpha | State |
| 17 | PA-RQ-MERCHANT-ZIP | PIC X(09) | Alpha | ZIP |
| 18 | PA-RQ-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID |

### 6.4 Auth MQ Reply (`CCPAURLY.cpy`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | PA-RL-CARD-NUM | PIC X(16) | Alpha | Card number |
| 2 | PA-RL-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID |
| 3 | PA-RL-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization approval code |
| 4 | PA-RL-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code |
| 5 | PA-RL-AUTH-RESP-REASON | PIC X(04) | Alpha | Response reason |
| 6 | PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited Numeric | Approved amount |

### 6.5 Auth Error Log (`CCPAUERY.cpy` → `ERROR-LOG-RECORD`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | ERR-DATE | PIC X(06) | Alpha | Error date |
| 2 | ERR-TIME | PIC X(06) | Alpha | Error time |
| 3 | ERR-APPLICATION | PIC X(08) | Alpha | Application ID |
| 4 | ERR-PROGRAM | PIC X(08) | Alpha | Program that generated error |
| 5 | ERR-LOCATION | PIC X(04) | Alpha | Location code within program |
| 6 | ERR-LEVEL | PIC X(01) | Alpha | Error severity level |
| 7 | ERR-SUBSYSTEM | PIC X(01) | Alpha | Subsystem code (D=DB2, M=MQ, etc.) |
| 8 | ERR-CODE-1 | PIC X(09) | Alpha | Primary error code |
| 9 | ERR-CODE-2 | PIC X(09) | Alpha | Secondary error code |
| 10 | ERR-MESSAGE | PIC X(50) | Alpha | Error message text |
| 11 | ERR-EVENT-KEY | PIC X(20) | Alpha | Event key for correlation |

---

## 7. Application Infrastructure Copybooks

### 7.1 Communication Area (`COCOM01Y.cpy` → `CARDDEMO-COMMAREA`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | CDEMO-FROM-TRANID | PIC X(04) | Alpha | Source CICS transaction ID |
| 2 | CDEMO-FROM-PROGRAM | PIC X(08) | Alpha | Source program name |
| 3 | CDEMO-TO-TRANID | PIC X(04) | Alpha | Target CICS transaction ID |
| 4 | CDEMO-TO-PROGRAM | PIC X(08) | Alpha | Target program name |
| 5 | CDEMO-USER-ID | PIC X(08) | Alpha | Logged-in user ID |
| 6 | CDEMO-USER-TYPE | PIC X(01) | Alpha | User type ('A'=Admin, 'U'=User) |
| 7 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context (0=Enter, 1=Re-enter) |
| 8 | CDEMO-CUST-ID | PIC 9(09) | Numeric | Current customer ID in context |
| 9 | CDEMO-CUST-FNAME | PIC X(25) | Alpha | Customer first name in context |
| 10 | CDEMO-CUST-MNAME | PIC X(25) | Alpha | Customer middle name in context |
| 11 | CDEMO-CUST-LNAME | PIC X(25) | Alpha | Customer last name in context |
| 12 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | Current account ID in context |
| 13 | CDEMO-ACCT-STATUS | PIC X(01) | Alpha | Account status in context |
| 14 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | Current card number in context |
| 15 | CDEMO-LAST-MAP | PIC X(7) | Alpha | Last BMS map displayed |
| 16 | CDEMO-LAST-MAPSET | PIC X(7) | Alpha | Last BMS mapset used |

### 7.2 Date/Time Working Storage (`CSDAT01Y.cpy` → `WS-DATE-TIME`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year |
| 2 | WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month |
| 3 | WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day |
| 4 | WS-CURDATE-N | PIC 9(08) | Numeric (REDEFINES) | Date as number YYYYMMDD |
| 5 | WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours |
| 6 | WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minutes |
| 7 | WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current seconds |
| 8 | WS-CURTIME-MILSEC | PIC 9(02) | Numeric | Current centiseconds |
| 9 | WS-CURDATE-MM-DD-YY | Group | Formatted | Date in MM/DD/YY display format |
| 10 | WS-CURTIME-HH-MM-SS | Group | Formatted | Time in HH:MM:SS display format |
| 11 | WS-TIMESTAMP | Group | Formatted | Full timestamp YYYY-MM-DD HH:MM:SS.mmmmmm |

### 7.3 Date Conversion (`CODATECN.cpy` → `CODATECN-REC`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | CODATECN-TYPE | PIC X | Alpha | Input format: '1'=YYYYMMDD, '2'=YYYY-MM-DD |
| 2 | CODATECN-INP-DATE | PIC X(20) | Alpha | Input date string |
| 3 | CODATECN-OUTTYPE | PIC X | Alpha | Output format: '1'=YYYY-MM-DD, '2'=YYYYMMDD |
| 4 | CODATECN-0UT-DATE | PIC X(20) | Alpha | Output date string |
| 5 | CODATECN-ERROR-MSG | PIC X(38) | Alpha | Error message if conversion fails |

### 7.4 Date Validation Working Storage (`CSUTLDWY.cpy`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | WS-EDIT-DATE-CCYYMMDD | Group | Group | Date being validated (CC+YY+MM+DD) |
| 2 | WS-EDIT-DATE-BINARY | PIC S9(9) BINARY | Binary | Binary representation for CEEDAYS |
| 3 | WS-CURRENT-DATE | Group | Group | System current date for comparison |
| 4 | WS-EDIT-YEAR-FLG / WS-EDIT-MONTH / WS-EDIT-DAY | PIC X(01) each | Alpha | Individual component validity flags |
| 5 | WS-DATE-FORMAT | PIC X(08) | Alpha | Date format pattern |
| 6 | WS-DATE-VALIDATION-RESULT | Group | Group | Validation result (severity, message, etc.) |

### 7.5 Lookup Code Repository (`CSLKPCDY.cpy`)

| # | Field Name | PIC Clause | Data Type | Business Meaning |
|---|-----------|------------|-----------|-----------------|
| 1 | WS-US-PHONE-AREA-CODE-TO-EDIT | PIC XXX | Alpha | Phone area code to validate |
| 2 | 88 VALID-PHONE-AREA-CODE | VALUES '201'-'999' | 88 Condition | Valid North American area codes (NANPA list) |
| 3 | 88 VALID-US-STATE-CODE | VALUES 'AL'-'WY' | 88 Condition | Valid US state abbreviations |
| 4 | 88 VALID-US-STATE-ZIP-COMBO | VALUES (state+ZIP pairs) | 88 Condition | Valid state + first-2-digits of ZIP |

### 7.6 Screen Title (`COTTL01Y.cpy` → `CCDA-SCREEN-TITLE`)

| # | Field Name | PIC Clause | Business Meaning |
|---|-----------|------------|-----------------|
| 1 | CCDA-TITLE01 | PIC X(40) | Line 1: 'AWS Mainframe Modernization' |
| 2 | CCDA-TITLE02 | PIC X(40) | Line 2: 'CardDemo' |
| 3 | CCDA-THANK-YOU | PIC X(40) | Sign-off message |

### 7.7 Common Messages (`CSMSG01Y.cpy` → `CCDA-COMMON-MESSAGES`)

| # | Field Name | PIC Clause | Business Meaning |
|---|-----------|------------|-----------------|
| 1 | CCDA-MSG-THANK-YOU | PIC X(50) | Thank-you/goodbye message |
| 2 | CCDA-MSG-INVALID-KEY | PIC X(50) | Invalid function key pressed message |

### 7.8 Menu Options

#### Main Menu (`COMEN02Y.cpy` → `CARDDEMO-MAIN-MENU-OPTIONS`)

| Option # | Name | Target Program | Access |
|----------|------|---------------|--------|
| 1 | Account View | COACTVWC | User |
| 2 | Account Update | COACTUPC | User |
| 3 | Credit Card List | COCRDLIC | User |
| 4 | Credit Card View | COCRDSLC | User |
| 5 | Credit Card Update | COCRDUPC | User |
| 6 | Transaction List | COTRN00C | User |
| 7 | Transaction View | COTRN01C | User |
| 8 | Transaction Add | COTRN02C | User |
| 9 | Transaction Reports | CORPT00C | User |
| 10 | Bill Payment | COBIL00C | User |
| 11 | Pending Authorization View | COPAUS0C | User |

#### Admin Menu (`COADM02Y.cpy` → `CARDDEMO-ADMIN-MENU-OPTIONS`)

| Option # | Name | Target Program |
|----------|------|---------------|
| 1 | User List (Security) | COUSR00C |
| 2 | User Add (Security) | COUSR01C |
| 3 | User Update (Security) | COUSR02C |
| 4 | User Delete (Security) | COUSR03C |
| 5 | Transaction Type List/Update (Db2) | COTRTLIC |
| 6 | Transaction Type Maintenance (Db2) | COTRTUPC |

### 7.9 Unused/Reserved (`UNUSED1Y.cpy` → `UNUSED-DATA`)

Identical layout to SEC-USER-DATA but with `UNUSED-` prefix. Reserved for future use.
