# Data Dictionary — CardDemo COBOL Estate

> Extracted from all copybooks in `app/cpy/` and sub-application `cpy/` directories.
> Fields are grouped by business entity.

---

## 1. Account Entity

### 1.1 CVACT01Y.cpy — Account Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| ACCT-ID | PIC 9(11) | Numeric (11) | Unique account identifier | Primary key for ACCTDATA VSAM |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alpha (1) | Account active/inactive flag | 'Y'=Active, 'N'=Inactive |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed Decimal (12,2) | Current account balance | Can be negative (credit balance) |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal (12,2) | Maximum credit limit | Must be > 0 for active accounts |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal (12,2) | Maximum cash advance limit | Subset of total credit limit |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric (10) | Date account was opened | Format: YYYY-MM-DD |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Account expiration date | Format: YYYY-MM-DD |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric (10) | Date card was last reissued | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed Decimal (12,2) | Current cycle credit total | Running total of credits in billing cycle |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed Decimal (12,2) | Current cycle debit total | Running total of debits in billing cycle |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | Account holder ZIP code | Used for discount group lookup |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Discount/rate group assignment | Links to DIS-GROUP-RECORD |
| FILLER | PIC X(178) | Filler | Reserved space | Pad to fixed record length |

### 1.2 CVACT03Y.cpy — Card Cross-Reference Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number | Links card to customer/account |
| XREF-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID | FK to CUSTOMER-RECORD |
| XREF-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | FK to ACCOUNT-RECORD |
| FILLER | PIC X(14) | Filler | Reserved space | Pad to fixed record length |

---

## 2. Customer Entity

### 2.1 CVCUS01Y.cpy — Customer Record (Primary)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| CUST-ID | PIC 9(09) | Numeric (9) | Unique customer identifier | Primary key for CUSTDATA VSAM |
| CUST-FIRST-NAME | PIC X(25) | Alpha (25) | Customer first name | Required, alpha-only validated in COACTUPC |
| CUST-MIDDLE-NAME | PIC X(25) | Alpha (25) | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alpha (25) | Customer last name | Required, alpha-only validated |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric (50) | Address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric (50) | Address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric (50) | Address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alpha (2) | US state code | Validated against CSLKPCDY state table |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alpha (3) | Country code | |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric (10) | ZIP/postal code | Validated against CSLKPCDY zip-state table |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric (15) | Primary phone number | Area code validated via CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric (15) | Secondary phone number | Area code validated via CSLKPCDY |
| CUST-SSN | PIC 9(09) | Numeric (9) | Social Security Number | Sensitive PII field |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric (20) | Government-issued ID number | Alternate identification |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric (10) | Date of birth | Format: YYYY-MM-DD |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric (10) | EFT/bank account for payments | Used in bill pay operations |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alpha (1) | Primary cardholder indicator | 'Y'=Primary, 'N'=Authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric (3) | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | Filler | Reserved space | Pad to fixed record length |

### 2.2 CUSTREC.cpy — Customer Record (Alternate Layout)

Identical field structure to CVCUS01Y.cpy with minor formatting differences. Uses `CUST-DOB-YYYYMMDD` (no hyphens) instead of `CUST-DOB-YYYY-MM-DD`.

---

## 3. Card Entity

### 3.1 CVACT02Y.cpy — Card Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| CARD-NUM | PIC X(16) | Alphanumeric (16) | Credit card number | Primary key for CARDDATA VSAM |
| CARD-ACCT-ID | PIC 9(11) | Numeric (11) | Associated account ID | FK to ACCOUNT-RECORD |
| CARD-CVV-CD | PIC 9(03) | Numeric (3) | Card verification value | 3-digit security code |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric (50) | Name embossed on card | Typically first + last name |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric (10) | Card expiration date | Format: YYYY-MM-DD |
| CARD-ACTIVE-STATUS | PIC X(01) | Alpha (1) | Card active/inactive flag | 'Y'=Active, 'N'=Inactive |
| FILLER | PIC X(59) | Filler | Reserved space | Pad to fixed record length |

### 3.2 CVCRD01Y.cpy — Card Work Areas (Online UI)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| CCARD-AID | PIC X(5) | Alpha (5) | Attention identifier from terminal | 88-levels: ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| CCARD-NEXT-PROG | PIC X(8) | Alpha (8) | Next program to transfer to | Used in EXEC CICS XCTL |
| CCARD-NEXT-MAPSET | PIC X(7) | Alpha (7) | Next BMS mapset name | |
| CCARD-NEXT-MAP | PIC X(7) | Alpha (7) | Next BMS map name | |
| CCARD-ERROR-MSG | PIC X(75) | Alpha (75) | Error message for display | |
| CCARD-RETURN-MSG | PIC X(75) | Alpha (75) | Return/informational message | 88: CCARD-RETURN-MSG-OFF = LOW-VALUES |
| CC-ACCT-ID | PIC X(11) / 9(11) | Alphanumeric/Numeric | Account ID work field | REDEFINES for numeric access |
| CC-CARD-NUM | PIC X(16) / 9(16) | Alphanumeric/Numeric | Card number work field | REDEFINES for numeric access |
| CC-CUST-ID | PIC X(09) / 9(9) | Alphanumeric/Numeric | Customer ID work field | REDEFINES for numeric access |

---

## 4. Transaction Entity

### 4.1 CVTRA05Y.cpy — Transaction Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| TRAN-ID | PIC X(16) | Alphanumeric (16) | Unique transaction identifier | System-generated, part of VSAM key |
| TRAN-TYPE-CD | PIC X(02) | Alpha (2) | Transaction type code | FK to TRAN-TYPE-RECORD (e.g., 'SA'=Sale) |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | FK to TRAN-CAT-RECORD |
| TRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Source of transaction | e.g., 'POS', 'ONLINE', 'ATM' |
| TRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | Free-text merchant/activity description |
| TRAN-AMT | PIC S9(09)V99 | Signed Decimal (11,2) | Transaction amount | Negative for credits/returns |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant identifier | |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card used for transaction | FK to CARD-RECORD |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original transaction timestamp | ISO format: YYYY-MM-DD-HH.MM.SS.ffffff |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | Set during batch posting |
| FILLER | PIC X(20) | Filler | Reserved space | |

### 4.2 CVTRA06Y.cpy — Daily Transaction Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric (16) | Daily transaction identifier | Input to batch posting |
| DALYTRAN-TYPE-CD | PIC X(02) | Alpha (2) | Transaction type code | Same codes as TRAN-TYPE-CD |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric (10) | Transaction source | |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric (100) | Transaction description | |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed Decimal (11,2) | Transaction amount | |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric (9) | Merchant identifier | |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric (50) | Merchant name | |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric (50) | Merchant city | |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric (10) | Merchant ZIP code | |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card used | |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric (26) | Original timestamp | |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric (26) | Processing timestamp | |
| FILLER | PIC X(20) | Filler | Reserved | |

### 4.3 CVTRA01Y.cpy — Transaction Category Balance Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID | Part of composite key |
| TRANCAT-TYPE-CD | PIC X(02) | Alpha (2) | Transaction type code | Part of composite key |
| TRANCAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | Part of composite key |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed Decimal (11,2) | Running balance for this category | Updated during posting |
| FILLER | PIC X(22) | Filler | Reserved | |

### 4.4 CVTRA02Y.cpy — Discount Group Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric (10) | Account group identifier | Part of composite key, links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alpha (2) | Transaction type code | Part of composite key |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | Part of composite key |
| DIS-INT-RATE | PIC S9(04)V99 | Signed Decimal (6,2) | Interest rate for this group/type/category | Used in CBACT04C interest calculation |
| FILLER | PIC X(28) | Filler | Reserved | |

### 4.5 CVTRA03Y.cpy — Transaction Type Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| TRAN-TYPE | PIC X(02) | Alpha (2) | Transaction type code | Primary key (e.g., 'SA', 'CR', 'CA') |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Description of transaction type | e.g., 'Sale', 'Credit', 'Cash Advance' |
| FILLER | PIC X(08) | Filler | Reserved | |

### 4.6 CVTRA04Y.cpy — Transaction Category Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| TRAN-TYPE-CD | PIC X(02) | Alpha (2) | Transaction type code | Part of composite key |
| TRAN-CAT-CD | PIC 9(04) | Numeric (4) | Transaction category code | Part of composite key |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric (50) | Category description | e.g., 'Retail Purchase', 'Online Purchase' |
| FILLER | PIC X(04) | Filler | Reserved | |

### 4.7 CVTRA07Y.cpy — Transaction Report Layout

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| REPT-SHORT-NAME | PIC X(38) | Alpha (38) | Short report name | VALUE 'DALYREPT' |
| REPT-LONG-NAME | PIC X(41) | Alpha (41) | Long report name | VALUE 'Daily Transaction Report' |
| REPT-START-DATE | PIC X(10) | Alpha (10) | Report start date | |
| REPT-END-DATE | PIC X(10) | Alpha (10) | Report end date | |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alpha (16) | Transaction ID in report line | |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alpha (11) | Account ID in report line | |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alpha (2) | Type code in report | |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alpha (15) | Type description | |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric (4) | Category code | |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alpha (29) | Category description | |
| TRAN-REPORT-SOURCE | PIC X(10) | Alpha (10) | Transaction source | |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Formatted amount | |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Page total amount | |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Account total amount | |

### 4.8 COSTM01.CPY — Transaction Key Record (Statement)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| TRNX-CARD-NUM | PIC X(16) | Alphanumeric (16) | Card number | Part of composite key |
| TRNX-ID | PIC X(16) | Alphanumeric (16) | Transaction ID | Part of composite key |
| TRNX-REST | (varies) | — | Remaining transaction fields | Balance of record after key |

---

## 5. Export/Import Entity

### 5.1 CVEXPORT.cpy — Export Record (Multi-Entity Container)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| EXPORT-REC-TYPE | PIC X(1) | Alpha (1) | Record type discriminator | 'C'=Customer, 'A'=Account, 'T'=Transaction, etc. |
| EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric (26) | Export timestamp | REDEFINES to date + time |
| EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary (4) | Sequence number | Auto-incremented |
| EXPORT-BRANCH-ID | PIC X(4) | Alpha (4) | Source branch identifier | |
| EXPORT-REGION-CODE | PIC X(5) | Alpha (5) | Geographic region code | |
| EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric (460) | Polymorphic data area | REDEFINES for each entity type |

**REDEFINES for EXPORT-CUSTOMER-DATA:**

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| EXP-CUST-ID | PIC 9(09) COMP | Binary (4) | Customer ID (packed) |
| EXP-CUST-FIRST-NAME | PIC X(25) | Alpha | First name |
| EXP-CUST-MIDDLE-NAME | PIC X(25) | Alpha | Middle name |
| EXP-CUST-LAST-NAME | PIC X(25) | Alpha | Last name |
| EXP-CUST-ADDR-LINE (×3) | PIC X(50) | Alpha | Address lines (OCCURS 3) |
| EXP-CUST-SSN | PIC 9(09) | Numeric | SSN |
| EXP-CUST-FICO-CREDIT-SCORE | PIC 9(03) COMP-3 | Packed Decimal | FICO score |

**REDEFINES for EXPORT-ACCOUNT-DATA:**

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| EXP-ACCT-ID | PIC 9(11) | Numeric | Account ID |
| EXP-ACCT-CURR-BAL | PIC S9(10)V99 COMP-3 | Packed Decimal | Current balance |
| EXP-ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 COMP-3 | Packed Decimal | Cash credit limit |
| EXP-ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 COMP | Binary | Cycle debit total |

**REDEFINES for EXPORT-TRANSACTION-DATA:**

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| EXP-TRAN-ID | PIC X(16) | Alphanumeric | Transaction ID |
| EXP-TRAN-AMT | PIC S9(09)V99 COMP-3 | Packed Decimal | Amount |
| EXP-TRAN-MERCHANT-ID | PIC 9(09) COMP | Binary | Merchant ID |

---

## 6. Security Entity

### 6.1 CSUSR01Y.cpy — User Security Record

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| SEC-USR-ID | PIC X(08) | Alpha (8) | User login ID | Primary key for USRSEC VSAM |
| SEC-USR-FNAME | PIC X(20) | Alpha (20) | User first name | |
| SEC-USR-LNAME | PIC X(20) | Alpha (20) | User last name | |
| SEC-USR-PWD | PIC X(08) | Alpha (8) | User password | Stored in cleartext (legacy) |
| SEC-USR-TYPE | PIC X(01) | Alpha (1) | User type | 'A'=Admin, 'U'=Regular user |
| SEC-USR-FILLER | PIC X(23) | Filler | Reserved | |

### 6.2 UNUSED1Y.cpy — Unused Security Record (Deprecated)

Identical structure to CSUSR01Y but with prefix `UNUSED-`. Not referenced by any active program.

---

## 7. Application Framework Copybooks

### 7.1 COCOM01Y.cpy — Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alpha (4) | Source CICS transaction ID | |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alpha (8) | Source program name | |
| CDEMO-TO-TRANID | PIC X(04) | Alpha (4) | Target CICS transaction ID | |
| CDEMO-TO-PROGRAM | PIC X(08) | Alpha (8) | Target program name | Used in XCTL routing |
| CDEMO-USER-ID | PIC X(08) | Alpha (8) | Authenticated user ID | |
| CDEMO-USER-TYPE | PIC X(01) | Alpha (1) | User type | 88: 'A'=Admin, 'U'=User |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric (1) | Program entry context | 88: 0=Enter, 1=Re-enter |
| CDEMO-CUST-ID | PIC 9(09) | Numeric (9) | Customer ID in context | |
| CDEMO-CUST-FNAME | PIC X(25) | Alpha (25) | Customer first name | |
| CDEMO-CUST-MNAME | PIC X(25) | Alpha (25) | Customer middle name | |
| CDEMO-CUST-LNAME | PIC X(25) | Alpha (25) | Customer last name | |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric (11) | Account ID in context | |
| CDEMO-ACCT-STATUS | PIC X(01) | Alpha (1) | Account status | |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric (16) | Card number in context | |
| CDEMO-LAST-MAP | PIC X(7) | Alpha (7) | Last BMS map displayed | |
| CDEMO-LAST-MAPSET | PIC X(7) | Alpha (7) | Last BMS mapset used | |

### 7.2 COADM02Y.cpy — Admin Menu Options

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| CDEMO-ADMIN-OPT-COUNT | PIC 9(02) | Numeric (2) | Number of admin menu options | VALUE 6 |
| CDEMO-ADMIN-OPT-NUM | PIC 9(02) | Numeric (2) | Option number | OCCURS 9 TIMES |
| CDEMO-ADMIN-OPT-NAME | PIC X(35) | Alpha (35) | Option display name | |
| CDEMO-ADMIN-OPT-PGMNAME | PIC X(08) | Alpha (8) | Target program name | e.g., 'COUSR00C', 'COTRTLIC' |

Options defined: User List, User Add, User Update, User Delete, Transaction Type List/Update (DB2), Transaction Type Maintenance (DB2)

### 7.3 COMEN02Y.cpy — Main Menu Options

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| CDEMO-MENU-OPT-COUNT | PIC 9(02) | Numeric (2) | Number of main menu options | VALUE 11 |
| CDEMO-MENU-OPT-NUM | PIC 9(02) | Numeric (2) | Option number | OCCURS 12 TIMES |
| CDEMO-MENU-OPT-NAME | PIC X(35) | Alpha (35) | Option display name | |
| CDEMO-MENU-OPT-PGMNAME | PIC X(08) | Alpha (8) | Target program name | |
| CDEMO-MENU-OPT-USRTYPE | PIC X(01) | Alpha (1) | Required user type | 'U'=Any user |

Options defined: Account View, Account Update, Credit Card List/View/Update, Transaction List/View/Add, Transaction Reports, Bill Payment, Pending Authorization View

### 7.4 COTTL01Y.cpy — Screen Title

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| CCDA-TITLE01 | PIC X(40) | Alpha | 'AWS Mainframe Modernization' |
| CCDA-TITLE02 | PIC X(40) | Alpha | 'CardDemo' |
| CCDA-THANK-YOU | PIC X(40) | Alpha | Thank you / sign-off message |

### 7.5 CSDAT01Y.cpy — Date/Time Work Fields

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| WS-CURDATE-YEAR | PIC 9(04) | Numeric | Current year (YYYY) |
| WS-CURDATE-MONTH | PIC 9(02) | Numeric | Current month (MM) |
| WS-CURDATE-DAY | PIC 9(02) | Numeric | Current day (DD) |
| WS-CURDATE-N | PIC 9(08) | Numeric | Date as integer YYYYMMDD |
| WS-CURTIME-HOURS | PIC 9(02) | Numeric | Current hours |
| WS-CURTIME-MINUTE | PIC 9(02) | Numeric | Current minutes |
| WS-CURTIME-SECOND | PIC 9(02) | Numeric | Current seconds |
| WS-CURTIME-MILSEC | PIC 9(02) | Numeric | Milliseconds (hundredths) |
| WS-TIMESTAMP | PIC (26) | Formatted | ISO timestamp (YYYY-MM-DD HH:MM:SS.ffffff) |

### 7.6 CSMSG01Y.cpy — Common Messages

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| CCDA-MSG-THANK-YOU | PIC X(50) | Alpha | Thank-you message |
| CCDA-MSG-INVALID-KEY | PIC X(50) | Alpha | Invalid key press message |

### 7.7 CSMSG02Y.cpy — Abend Data

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| ABEND-CODE | PIC X(4) | Alpha | Abend code |
| ABEND-CULPRIT | PIC X(8) | Alpha | Program causing abend |
| ABEND-REASON | PIC X(50) | Alpha | Reason description |
| ABEND-MSG | PIC X(72) | Alpha | Formatted abend message |

### 7.8 CSUTLDWY.cpy — Date Edit/Validation Work Fields

| Field Name | PIC Clause | Data Type | Business Meaning | Validation / Notes |
|------------|-----------|-----------|------------------|-------------------|
| WS-EDIT-DATE-CC | PIC X(2) / 9(2) | Alpha/Numeric | Century | 88: THIS-CENTURY=20, LAST-CENTURY=19 |
| WS-EDIT-DATE-YY | PIC X(2) / 9(2) | Alpha/Numeric | Year (2-digit) | |
| WS-EDIT-DATE-MM | PIC X(2) / 9(2) | Alpha/Numeric | Month | 88: WS-VALID-MONTH VALUES 1–12, WS-31-DAY-MONTH, WS-FEBRUARY |
| WS-EDIT-DATE-DD | PIC X(2) / 9(2) | Alpha/Numeric | Day | 88: WS-VALID-DAY VALUES 1–31, WS-DAY-31, WS-DAY-30, WS-DAY-29, WS-VALID-FEB-DAY 1–28 |
| WS-EDIT-DATE-CCYYMMDD-N | PIC 9(8) | Numeric | Full date as integer | REDEFINES composite |
| WS-EDIT-DATE-BINARY | PIC S9(9) BINARY | Binary | Lilian date value | For CEEDAYS comparison |
| WS-CURRENT-DATE-YYYYMMDD | PIC X(8) / 9(8) | Alpha/Numeric | Current date | For future-date validation |

### 7.9 CSLKPCDY.cpy — Lookup Code Repository

Contains 88-level validation tables for:
- **North American phone area codes** — comprehensive list of valid 3-digit area codes (VALID-PHONE-AREA-CODE)
- **US state codes** — 2-letter state abbreviations (VALID-US-STATE-CD)
- **US ZIP-to-state mapping** — first 2 digits of ZIP mapped to states

### 7.10 CODATECN.cpy — Date Conversion Record

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| CODATECN-TYPE | PIC X | Alpha | Input format type (88: '1'=YYYYMMDD, '2'=YYYY-MM-DD) |
| CODATECN-INP-DATE | PIC X(20) | Alpha | Input date string |
| CODATECN-OUTTYPE | PIC X | Alpha | Output format type (88: '1'=YYYY-MM-DD, '2'=YYYYMMDD) |
| CODATECN-0UT-DATE | PIC X(20) | Alpha | Output date string |
| CODATECN-ERROR-MSG | PIC X(38) | Alpha | Error message if conversion fails |

---

## 8. Authorization Sub-Application Copybooks

### 8.1 CCPAURQY.cpy — Authorization Request

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alpha | Authorization date (MMDDYY) |
| PA-RQ-AUTH-TIME | PIC X(06) | Alpha | Authorization time (HHMMSS) |
| PA-RQ-CARD-NUM | PIC X(16) | Alpha | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alpha | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alpha | Card expiry (MMYY) |
| PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited Numeric | Requested transaction amount |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alpha | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alpha | Merchant name |
| PA-RQ-TRANSACTION-ID | PIC X(15) | Alpha | Transaction reference ID |

### 8.2 CCPAURLY.cpy — Authorization Reply

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-RL-CARD-NUM | PIC X(16) | Alpha | Card number |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alpha | Transaction ID |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alpha | Authorization ID code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code ('00'=Approved) |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alpha | Decline reason code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited Numeric | Approved amount |

### 8.3 CIPAUSMY.cpy — Authorization Summary (IMS Segment)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed Decimal | Account ID |
| PA-CUST-ID | PIC 9(09) | Numeric | Customer ID |
| PA-AUTH-STATUS | PIC X(01) | Alpha | Authorization status |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Alpha | Account status array |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed Decimal | Credit limit |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed Decimal | Cash limit |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed Decimal | Credit balance |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed Decimal | Cash balance |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Approved authorization count |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Declined authorization count |

### 8.4 CIPAUDTY.cpy — Authorization Detail (IMS Segment)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed Decimal | Auth date (packed) — composite key |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed Decimal | Auth time (packed) — composite key |
| PA-CARD-NUM | PIC X(16) | Alpha | Card number |
| PA-AUTH-RESP-CODE | PIC X(02) | Alpha | Response code (88: '00'=Approved) |
| PA-TRANSACTION-AMT | PIC +9(10).99 | Edited Numeric | Transaction amount |
| PA-MATCH-STATUS | PIC X(01) | Alpha | 88: P=Pending, D=Declined, E=Expired, M=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alpha | 88: F=Fraud confirmed, R=Fraud removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alpha | Fraud report date |

### 8.5 CCPAUERY.cpy — Error Log Record

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| ERR-DATE | PIC X(06) | Alpha | Error date |
| ERR-TIME | PIC X(06) | Alpha | Error time |
| ERR-APPLICATION | PIC X(08) | Alpha | Application name |
| ERR-PROGRAM | PIC X(08) | Alpha | Program name |
| ERR-LEVEL | PIC X(01) | Alpha | Severity (88: L=Log, I=Info, W=Warning, C=Critical) |
| ERR-SUBSYSTEM | PIC X(01) | Alpha | Subsystem (88: A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File) |
| ERR-MESSAGE | PIC X(50) | Alpha | Error message text |

### 8.6 IMS PCB Copybooks (PAUTBPCB.CPY, PASFLPCB.CPY, PADFLPCB.CPY)

These define IMS Program Communication Blocks with standard fields: DBDNAME, SEG-LEVEL, PCB-STATUS, PCB-PROCOPT, SEG-NAME, KEYFB-NAME, NUM-SENSEGS, KEYFB.

### 8.7 IMSFUNCS.cpy — IMS Function Codes

| Field Name | PIC Clause | Value | Meaning |
|------------|-----------|-------|---------|
| FUNC-GU | PIC X(04) | 'GU  ' | Get Unique |
| FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique |
| FUNC-GN | PIC X(04) | 'GN  ' | Get Next |
| FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next |
| FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | PIC X(04) | 'REPL' | Replace |
| FUNC-ISRT | PIC X(04) | 'ISRT' | Insert |
| FUNC-DLET | PIC X(04) | 'DLET' | Delete |

---

## 9. Transaction Type DB2 Copybooks

### 9.1 CSDB2RWY.cpy — DB2 Working Storage Variables

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|------------------|
| WS-DISP-SQLCODE | PIC ----9 | Edited Numeric | Displayable SQLCODE |
| WS-DB2-PROCESSING-FLAG | PIC X(1) | Alpha | 88: '0'=OK, '1'=Error |
| WS-DB2-CURRENT-ACTION | PIC X(72) | Alpha | Current DB2 action description |
| WS-DSNTIAC-FORMATTED | (complex) | — | DSNTIAC formatted error message (10 × 72-char lines) |

### 9.2 CSDB2RPY.cpy — DB2 Priming Query Procedure

Contains SQL `SELECT 1 FROM SYSIBM.SYSDUMMY1` to verify DB2 connectivity before main processing. Includes DSNTIAC call for formatted error messages.
