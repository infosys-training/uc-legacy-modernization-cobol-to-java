# Data Dictionary — CardDemo Copybooks

> Field-level documentation for every copybook in `app/cpy/`, grouped by business entity.

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | ACCOUNT-RECORD | — | Group | Root account record structure | Fixed 300-byte record |
| 05 | ACCT-ID | PIC 9(11) | Numeric | Unique account identifier | 11-digit numeric, KSDS primary key |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Account active/inactive flag | 'Y' = Active, 'N' = Inactive |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed Decimal | Current account balance | Signed, 2 decimal places, max ±9,999,999,999.99 |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Credit limit for the account | Signed, 2 decimal places |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Cash advance credit limit | Signed, 2 decimal places |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Date the account was opened | Format: YYYY-MM-DD |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | Format: YYYY-MM-DD (note: typo in field name is original) |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Card reissue date | Format: YYYY-MM-DD |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed Decimal | Current billing cycle credits | Running total for cycle |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed Decimal | Current billing cycle debits | Running total for cycle |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP/postal code | Up to 10 characters (supports ZIP+4) |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure group ID for interest rates | Links to DIS-GROUP-RECORD |
| 05 | FILLER | PIC X(178) | Alphanumeric | Reserved padding | Pads record to 300 bytes |

---

## 2. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CARD-RECORD | — | Group | Root card record structure | Fixed 150-byte record |
| 05 | CARD-NUM | PIC X(16) | Alphanumeric | Credit card number | 16-character card PAN |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric | Associated account ID | FK to ACCOUNT-RECORD.ACCT-ID |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit security code |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on card | Cardholder display name |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | Format: YYYY-MM-DD (note: typo is original) |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card active/inactive flag | 'Y' = Active, 'N' = Inactive |
| 05 | FILLER | PIC X(59) | Alphanumeric | Reserved padding | Pads record to 150 bytes |

### CVACT03Y.cpy — Card Cross-Reference (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CARD-XREF-RECORD | — | Group | Cross-reference linking card → customer → account | Fixed 50-byte record |
| 05 | XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Primary key, FK to CARD-RECORD |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID | FK to CUSTOMER-RECORD |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID | FK to ACCOUNT-RECORD |
| 05 | FILLER | PIC X(14) | Alphanumeric | Reserved padding | Pads record to 50 bytes |

---

## 3. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CUSTOMER-RECORD | — | Group | Root customer record structure | Fixed 500-byte record |
| 05 | CUST-ID | PIC 9(09) | Numeric | Unique customer identifier | 9-digit numeric, KSDS primary key |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alphanumeric | Customer first name | Required, max 25 chars |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Customer middle name | Optional, max 25 chars |
| 05 | CUST-LAST-NAME | PIC X(25) | Alphanumeric | Customer last name | Required, max 25 chars |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | Primary street address |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | Apartment, suite, etc. |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | Additional address info |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated against CSLKPCDY state list |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | ISO 3-character country code |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP / postal code | Validated against CSLKPCDY state-zip table |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated via CSLKPCDY |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | Area code validated via CSLKPCDY |
| 05 | CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit numeric, sensitive PII |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID number | Driver's license, passport, etc. |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | Format: YYYY-MM-DD |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | Electronic funds transfer account | Linked bank account for payments |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary cardholder indicator | 'Y' = Primary, 'N' = Authorized user |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range: 300–850 |
| 05 | FILLER | PIC X(168) | Alphanumeric | Reserved padding | Pads record to 500 bytes |

### CUSTREC.cpy — Customer Record (Alternate Layout)

Identical field structure to CVCUS01Y.cpy. Used by the statement generation program (`CBSTM03A`). The only difference is the field name `CUST-DOB-YYYYMMDD` vs `CUST-DOB-YYYY-MM-DD` (no dashes in the alternate layout).

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | TRAN-RECORD | — | Group | Root transaction record structure | Fixed 350-byte record |
| 05 | TRAN-ID | PIC X(16) | Alphanumeric | Unique transaction identifier | 16-char ID, KSDS primary key |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to TRAN-TYPE-RECORD |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to TRAN-CAT-RECORD |
| 05 | TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source/channel | e.g., POS, ATM, ONLINE |
| 05 | TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | Free-text description |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed Decimal | Transaction amount | Signed, 2 decimal places |
| 05 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | 9-digit merchant number |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | Display name |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | Location of merchant |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | Merchant postal code |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used | FK to CARD-RECORD |
| 05 | TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original transaction timestamp | Format: YYYY-MM-DD-HH.MM.SS.MMMMMM |
| 05 | TRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | When transaction was posted |
| 05 | FILLER | PIC X(20) | Alphanumeric | Reserved padding | Pads record to 350 bytes |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Identical field structure to CVTRA05Y.cpy with `DALYTRAN-` prefix instead of `TRAN-`. Used for the daily incoming transaction file before posting to the master TRANSACT dataset.

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | Balance per transaction category per account | Fixed 50-byte record |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | Account + Type + Category |
| 10 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | FK to ACCOUNT-RECORD |
| 10 | TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to TRAN-TYPE-RECORD |
| 10 | TRANCAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to TRAN-CAT-RECORD |
| 05 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed Decimal | Running balance for this category | Used for interest calculation |
| 05 | FILLER | PIC X(22) | Alphanumeric | Reserved padding | Pads record to 50 bytes |

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | DIS-GROUP-RECORD | — | Group | Interest rate disclosure by group/type/category | Fixed 50-byte record |
| 05 | DIS-GROUP-KEY | — | Group | Composite key | Group + Type + Category |
| 10 | DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group ID | Links to ACCT-GROUP-ID |
| 10 | DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Type of transaction |
| 10 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Category of transaction |
| 05 | DIS-INT-RATE | PIC S9(04)V99 | Signed Decimal | Interest rate for this combination | Annual percentage rate |
| 05 | FILLER | PIC X(28) | Alphanumeric | Reserved padding | Pads record to 50 bytes |

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type lookup | Fixed 60-byte record |
| 05 | TRAN-TYPE | PIC X(02) | Alphanumeric | Transaction type code | Primary key |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Transaction type description | e.g., "Purchase", "Cash Advance" |
| 05 | FILLER | PIC X(08) | Alphanumeric | Reserved padding | Pads record to 60 bytes |

### CVTRA04Y.cpy — Transaction Category Type (RECLN 60)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category type lookup | Fixed 60-byte record |
| 05 | TRAN-CAT-KEY | — | Group | Composite key | Type + Category |
| 10 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to TRAN-TYPE-RECORD |
| 10 | TRAN-CAT-CD | PIC 9(04) | Numeric | Category code within type | Numeric category identifier |
| 05 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category type description | e.g., "Retail Purchase", "Fuel" |
| 05 | FILLER | PIC X(04) | Alphanumeric | Reserved padding | Pads record to 60 bytes |

### CVTRA07Y.cpy — Transaction Report Layout

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | REPORT-NAME-HEADER | — | Group | Report header record | Print layout |
| 05 | REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Short report name | Default: 'DALYREPT' |
| 05 | REPT-LONG-NAME | PIC X(41) | Alphanumeric | Long report name | Default: 'Daily Transaction Report' |
| 05 | REPT-DATE-HEADER | PIC X(12) | Alphanumeric | Date range label | 'Date Range: ' |
| 05 | REPT-START-DATE | PIC X(10) | Alphanumeric | Report start date | Format: YYYY-MM-DD |
| 05 | REPT-END-DATE | PIC X(10) | Alphanumeric | Report end date | Format: YYYY-MM-DD |
| 01 | TRANSACTION-DETAIL-REPORT | — | Group | Detail line layout | Print line 133 chars |
| 05 | TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID | Display field |
| 05 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID | Display field |
| 05 | TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code | Display field |
| 05 | TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description | Truncated for report |
| 05 | TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code | Display field |
| 05 | TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description | Truncated for report |
| 05 | TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Transaction source | Display field |
| 05 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Transaction amount | Formatted with commas, sign |
| 01 | REPORT-PAGE-TOTALS | — | Group | Page total line | Print layout |
| 05 | REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Page subtotal | Formatted with sign |
| 01 | REPORT-ACCOUNT-TOTALS | — | Group | Account total line | Print layout |
| 05 | REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Account subtotal | Formatted with sign |
| 01 | REPORT-GRAND-TOTALS | — | Group | Grand total line | Print layout |
| 05 | REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Grand total | Formatted with sign |

### COSTM01.CPY — Statement Transaction Layout (Keyed)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | TRNX-RECORD | — | Group | Transaction record with card-keyed access | For statement generation |
| 05 | TRNX-KEY | — | Group | Composite key | Card Number + Transaction ID |
| 10 | TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Part of composite key |
| 10 | TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID | Part of composite key |
| 05 | TRNX-REST | — | Group | Transaction details | Same fields as CVTRA05Y |
| 10 | TRNX-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | Type identifier |
| 10 | TRNX-CAT-CD | PIC 9(04) | Numeric | Transaction category code | Category identifier |
| 10 | TRNX-SOURCE | PIC X(10) | Alphanumeric | Transaction source | Channel of origin |
| 10 | TRNX-DESC | PIC X(100) | Alphanumeric | Transaction description | Free text |
| 10 | TRNX-AMT | PIC S9(09)V99 | Signed Decimal | Transaction amount | Signed decimal |
| 10 | TRNX-MERCHANT-ID | PIC 9(09) | Numeric | Merchant ID | Merchant identifier |
| 10 | TRNX-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | Display name |
| 10 | TRNX-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | Location |
| 10 | TRNX-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP | Postal code |
| 10 | TRNX-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp | Transaction time |
| 10 | TRNX-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | Posting time |
| 10 | FILLER | PIC X(20) | Alphanumeric | Reserved | Padding |

---

## 5. Security Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | SEC-USER-DATA | — | Group | User authentication and authorization record | Fixed 80-byte record |
| 05 | SEC-USR-ID | PIC X(08) | Alphanumeric | User login ID | Primary key, 8 chars max |
| 05 | SEC-USR-FNAME | PIC X(20) | Alphanumeric | User first name | Display name |
| 05 | SEC-USR-LNAME | PIC X(20) | Alphanumeric | User last name | Display name |
| 05 | SEC-USR-PWD | PIC X(08) | Alphanumeric | User password | Stored in plaintext (legacy) |
| 05 | SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type flag | 'A' = Admin, 'U' = Regular user |
| 05 | SEC-USR-FILLER | PIC X(23) | Alphanumeric | Reserved padding | Pads record to 80 bytes |

### UNUSED1Y.cpy — Unused Data Record (Legacy)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | UNUSED-DATA | — | Group | Deprecated/unused record layout | Likely legacy security record |
| 05 | UNUSED-ID | PIC X(08) | Alphanumeric | ID field | Not in use |
| 05 | UNUSED-FNAME | PIC X(20) | Alphanumeric | First name | Not in use |
| 05 | UNUSED-LNAME | PIC X(20) | Alphanumeric | Last name | Not in use |
| 05 | UNUSED-PWD | PIC X(08) | Alphanumeric | Password | Not in use |
| 05 | UNUSED-TYPE | PIC X(01) | Alphanumeric | Type flag | Not in use |
| 05 | UNUSED-FILLER | PIC X(23) | Alphanumeric | Padding | Not in use |

---

## 6. Shared / Infrastructure Copybooks

### COCOM01Y.cpy — COMMAREA (Communication Area)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CARDDEMO-COMMAREA | — | Group | Inter-program communication block | Passed via CICS COMMAREA |
| 05 | CDEMO-GENERAL-INFO | — | Group | General navigation context | — |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source transaction ID | CICS transaction code |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name | Calling program |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target transaction ID | Destination transaction |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name | Program to XCTL to |
| 10 | CDEMO-USER-ID | PIC X(08) | Alphanumeric | Current user ID | Logged-in user |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type | 88: 'A' = Admin, 'U' = User |
| 10 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program context flag | 88: 0 = Enter, 1 = Re-enter |
| 05 | CDEMO-CUSTOMER-INFO | — | Group | Customer context | — |
| 10 | CDEMO-CUST-ID | PIC 9(09) | Numeric | Customer ID in context | Passed between programs |
| 10 | CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | Customer first name | Display context |
| 10 | CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | Customer middle name | Display context |
| 10 | CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | Customer last name | Display context |
| 05 | CDEMO-ACCOUNT-INFO | — | Group | Account context | — |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | Account ID in context | Passed between programs |
| 10 | CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status | Active/Inactive |
| 05 | CDEMO-CARD-INFO | — | Group | Card context | — |
| 10 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | Card number in context | Passed between programs |
| 05 | CDEMO-MORE-INFO | — | Group | Map tracking | — |
| 10 | CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last BMS map displayed | Screen tracking |
| 10 | CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last BMS mapset used | Screen tracking |

### COADM02Y.cpy — Admin Menu Options

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CARDDEMO-ADMIN-MENU-OPTIONS | — | Group | Admin menu dispatch table | 6 options defined |
| 05 | CDEMO-ADMIN-OPT-COUNT | PIC 9(02) | Numeric | Number of admin options | Currently 6 |
| 05 | CDEMO-ADMIN-OPTIONS (array) | — | Group | Option array (9 max) | REDEFINES over data block |
| 15 | CDEMO-ADMIN-OPT-NUM | PIC 9(02) | Numeric | Option number | Menu selection number |
| 15 | CDEMO-ADMIN-OPT-NAME | PIC X(35) | Alphanumeric | Option display name | e.g., "User List (Security)" |
| 15 | CDEMO-ADMIN-OPT-PGMNAME | PIC X(08) | Alphanumeric | Target program name | e.g., "COUSR00C" |

### COMEN02Y.cpy — Main Menu Options

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CARDDEMO-MAIN-MENU-OPTIONS | — | Group | Main menu dispatch table | 11 options defined |
| 05 | CDEMO-MENU-OPT-COUNT | PIC 9(02) | Numeric | Number of menu options | Currently 11 |
| 05 | CDEMO-MENU-OPTIONS (array) | — | Group | Option array (12 max) | REDEFINES over data block |
| 15 | CDEMO-MENU-OPT-NUM | PIC 9(02) | Numeric | Option number | 1–11 |
| 15 | CDEMO-MENU-OPT-NAME | PIC X(35) | Alphanumeric | Option display name | e.g., "Account View" |
| 15 | CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Alphanumeric | Target program name | e.g., "COACTVWC" |
| 15 | CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Alphanumeric | Required user type | 'U' = Any user |

### COTTL01Y.cpy — Screen Title

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CCDA-SCREEN-TITLE | — | Group | Application title block | Used on all BMS maps |
| 05 | CCDA-TITLE01 | PIC X(40) | Alphanumeric | Title line 1 | "AWS Mainframe Modernization" |
| 05 | CCDA-TITLE02 | PIC X(40) | Alphanumeric | Title line 2 | "CardDemo" |
| 05 | CCDA-THANK-YOU | PIC X(40) | Alphanumeric | Exit message | "Thank you for using CCDA application..." |

### CSDAT01Y.cpy — Date/Time Working Storage

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | WS-DATE-TIME | — | Group | Date and time work areas | Used by all online programs |
| 10 | WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year | 4-digit year |
| 10 | WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month | 01–12 |
| 10 | WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day | 01–31 |
| 10 | WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours | 00–23 |
| 10 | WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minutes | 00–59 |
| 10 | WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current seconds | 00–59 |
| 10 | WS-CURTIME-MILSEC | PIC 9(02) | Numeric | Milliseconds | 00–99 |
| 05 | WS-TIMESTAMP | — | Group | Full timestamp | Format: YYYY-MM-DD HH:MM:SS.MMMMMM |

### CSMSG01Y.cpy — Common Messages

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CCDA-COMMON-MESSAGES | — | Group | Shared UI messages | Used by all online programs |
| 05 | CCDA-MSG-THANK-YOU | PIC X(50) | Alphanumeric | Thank-you message | "Thank you for using CardDemo application..." |
| 05 | CCDA-MSG-INVALID-KEY | PIC X(50) | Alphanumeric | Invalid key message | "Invalid key pressed. Please see below..." |

### CSMSG02Y.cpy — Abend Data

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | ABEND-DATA | — | Group | Abend (abnormal end) information | Error handling |
| 05 | ABEND-CODE | PIC X(4) | Alphanumeric | Abend code | System or application code |
| 05 | ABEND-CULPRIT | PIC X(8) | Alphanumeric | Program causing abend | Program name |
| 05 | ABEND-REASON | PIC X(50) | Alphanumeric | Abend reason description | Free-text explanation |
| 05 | ABEND-MSG | PIC X(72) | Alphanumeric | Formatted abend message | Display message |

### CODATECN.cpy — Date Conversion Record

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CODATECN-REC | — | Group | Date format conversion work area | Used by COBDATFT assembler routine |
| 05 | CODATECN-IN-REC | — | Group | Input date | — |
| 10 | CODATECN-TYPE | PIC X | Alphanumeric | Input format type | 88: '1' = YYYYMMDD, '2' = YYYY-MM-DD |
| 10 | CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date value | Raw date string |
| 05 | CODATECN-OUT-REC | — | Group | Output date | — |
| 10 | CODATECN-OUTTYPE | PIC X | Alphanumeric | Output format type | 88: '1' = YYYY-MM-DD, '2' = YYYYMMDD |
| 10 | CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Output date value | Formatted date string |
| 05 | CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Error message | Conversion error details |

### CSUTLDWY.cpy — Date Validation Working Storage

Working storage fields for date validation logic in CSUTLDPY.cpy. Defines `WS-EDIT-DATE-CCYYMMDD` with century/year/month/day breakdown, validation flags (`FLG-YEAR-ISVALID`, `FLG-MONTH-ISVALID`, `FLG-DAY-ISVALID`), leap year logic, and 88-level conditions for valid ranges.

### CSUTLDPY.cpy — Date Validation Procedures

Procedure Division copybook containing reusable paragraphs: `EDIT-DATE-CCYYMMDD`, `EDIT-YEAR-CCYY`, `EDIT-MONTH`, `EDIT-DAY`, `EDIT-DATE-OF-BIRTH`. Validates century (19/20 only), month (1–12), day (1–31 with month/leap-year awareness).

### CSLKPCDY.cpy — Lookup Code Repository

Large reference data copybook containing:
- **North American phone area codes** — 88-level condition `VALID-PHONE-AREA-CODE` with ~350 valid area codes
- **US state codes** — 88-level condition `VALID-US-STATE-CODE` with all 50 states + territories
- **State + ZIP prefix combinations** — 88-level condition for geographic validation

### CSSTRPFY.cpy — Store PF Key Procedure

Procedure Division copybook that maps CICS AID bytes (EIBAID) to application-level PF key identifiers using EVALUATE/WHEN. Maps DFHENTER → CCARD-AID-ENTER, DFHCLEAR → CCARD-AID-CLEAR, DFHPF1–PF24 → CCARD-AID-PFK01–PFK12.

### CSSETATY.cpy — Set Field Attributes Procedure

Procedure Division copybook using COPY REPLACING to dynamically set BMS field attributes (color, protection, error indicators) based on validation flags. Used in COACTUPC for field-level error highlighting.

### CVCRD01Y.cpy — Credit Card Work Areas

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | CC-WORK-AREAS | — | Group | CICS program work area for card operations | — |
| 10 | CCARD-AID | PIC X(5) | Alphanumeric | Current AID key pressed | 88-levels for each PF key |
| 10 | CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to transfer to | XCTL target |
| 10 | CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | Next BMS mapset | Screen navigation |
| 10 | CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | Next BMS map | Screen navigation |
| 10 | CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message for display | Shown on screen |
| 10 | CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return/info message | Status messages |
| 10 | CC-ACCT-ID | PIC X(11) | Alphanumeric | Account ID work field | Numeric REDEFINES available |
| 10 | CC-CARD-NUM | PIC X(16) | Alphanumeric | Card number work field | Numeric REDEFINES available |
| 10 | CC-CUST-ID | PIC X(09) | Alphanumeric | Customer ID work field | Numeric REDEFINES available |

### CVEXPORT.cpy — Multi-Record Export Layout

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|-------|-----------|------------|-----------|------------------|------------------|
| 01 | EXPORT-RECORD | — | Group | Branch migration export record | Fixed 500-byte record |
| 05 | EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | Record type discriminator | 'C'=Customer, 'A'=Account, 'T'=Transaction, 'X'=Xref, 'D'=Card |
| 05 | EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp | When record was exported |
| 05 | EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number | Ordering within export |
| 05 | EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Source branch identifier | Originating branch |
| 05 | EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Geographic region code | Regional classification |
| 05 | EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Polymorphic data area | REDEFINES for each entity type |

Uses REDEFINES to overlay entity-specific structures (Customer, Account, Transaction, Card Xref, Card) with COMP/COMP-3 optimizations for numeric fields in the export format.
