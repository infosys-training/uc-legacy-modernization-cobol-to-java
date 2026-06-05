# CardDemo Data Dictionary

> Field-level documentation for every copybook in the CardDemo estate, grouped by business entity.

## Summary

| Metric | Count |
|--------|-------|
| Total copybooks | 47 |
| `app/cpy/` | 30 |
| `app/app-authorization-ims-db2-mq/cpy/` | 8 |
| `app/app-transaction-type-db2/cpy/` | 2 |
| Business entities | 7 (Account, Customer, Card, Transaction, Authorization, User/Security, System) |

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| ACCT-ID | 9(11) | Numeric | Unique account identifier (11-digit) |
| ACCT-ACTIVE-STATUS | X(01) | Alpha | Account status flag (Y/N) |
| ACCT-CURR-BAL | S9(10)V99 | Signed decimal | Current account balance |
| ACCT-CREDIT-LIMIT | S9(10)V99 | Signed decimal | Maximum credit limit |
| ACCT-CASH-CREDIT-LIMIT | S9(10)V99 | Signed decimal | Cash advance credit limit |
| ACCT-OPEN-DATE | X(10) | Date string | Account open date (YYYY-MM-DD) |
| ACCT-EXPIRAION-DATE | X(10) | Date string | Account expiration date |
| ACCT-REISSUE-DATE | X(10) | Date string | Card reissue date |
| ACCT-CURR-CYC-CREDIT | S9(10)V99 | Signed decimal | Current cycle credit total |
| ACCT-CURR-CYC-DEBIT | S9(10)V99 | Signed decimal | Current cycle debit total |
| ACCT-ADDR-ZIP | X(10) | Alpha | Account holder ZIP code |
| ACCT-GROUP-ID | X(10) | Alpha | Disclosure/interest rate group code |
| FILLER | X(178) | Padding | Reserved space to 300 bytes |

**Validation rules:** Balance fields use COBOL signed decimal (S9V99) — must map to BigDecimal in Java. Status is single character Y/N. ACCT-GROUP-ID links to CVTRA02Y disclosure group.

**VSAM key:** ACCT-ID (bytes 0-10, 11-byte key)

---

### CVACT03Y.cpy — Card Cross-Reference (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| XREF-CARD-NUM | X(16) | Alpha | Card number (cross-reference key) |
| XREF-CUST-ID | 9(09) | Numeric | Customer ID linked to card |
| XREF-ACCT-ID | 9(11) | Numeric | Account ID linked to card |
| FILLER | X(14) | Padding | Reserved |

**Purpose:** Provides the many-to-many mapping between cards, customers, and accounts. Used by nearly every program to navigate from card number to account/customer data.

**VSAM key:** XREF-CARD-NUM (bytes 0-15, 16-byte key)

---

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| TRANCAT-ACCT-ID | 9(11) | Numeric | Account ID |
| TRANCAT-TYPE-CD | X(02) | Alpha | Transaction type code |
| TRANCAT-CD | 9(04) | Numeric | Transaction category code |
| TRAN-CAT-BAL | S9(09)V99 | Signed decimal | Running balance for this category |
| FILLER | X(22) | Padding | Reserved |

**Purpose:** Accumulates balances per account per transaction type/category combination. Updated by CBTRN02C (posting) and CBACT04C (interest calc).

**Composite key:** TRANCAT-ACCT-ID + TRANCAT-TYPE-CD + TRANCAT-CD

---

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| DIS-ACCT-GROUP-ID | X(10) | Alpha | Account group identifier |
| DIS-TRAN-TYPE-CD | X(02) | Alpha | Transaction type code |
| DIS-TRAN-CAT-CD | 9(04) | Numeric | Transaction category code |
| DIS-INT-RATE | S9(04)V99 | Signed decimal | Interest rate for this disclosure group |
| FILLER | X(28) | Padding | Reserved |

**Purpose:** Maps account groups to interest rates by transaction type/category. Used by CBACT04C to compute interest charges.

**Composite key:** DIS-ACCT-GROUP-ID + DIS-TRAN-TYPE-CD + DIS-TRAN-CAT-CD

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CUST-ID | 9(09) | Numeric | Unique customer identifier (9-digit) |
| CUST-FIRST-NAME | X(25) | Alpha | Customer first name |
| CUST-MIDDLE-NAME | X(25) | Alpha | Customer middle name |
| CUST-LAST-NAME | X(25) | Alpha | Customer last name |
| CUST-ADDR-LINE-1 | X(50) | Alpha | Address line 1 |
| CUST-ADDR-LINE-2 | X(50) | Alpha | Address line 2 |
| CUST-ADDR-LINE-3 | X(50) | Alpha | Address line 3 |
| CUST-ADDR-STATE-CD | X(02) | Alpha | US state code |
| CUST-ADDR-COUNTRY-CD | X(03) | Alpha | Country code |
| CUST-ADDR-ZIP | X(10) | Alpha | ZIP code (5+4 format) |
| CUST-PHONE-NUM-1 | X(15) | Alpha | Primary phone number |
| CUST-PHONE-NUM-2 | X(15) | Alpha | Secondary phone number |
| CUST-SSN | 9(09) | Numeric | Social Security Number |
| CUST-GOVT-ISSUED-ID | X(20) | Alpha | Government-issued ID number |
| CUST-DOB-YYYY-MM-DD | X(10) | Date string | Date of birth |
| CUST-EFT-ACCOUNT-ID | X(10) | Alpha | Electronic funds transfer account |
| CUST-PRI-CARD-HOLDER-IND | X(01) | Alpha | Primary card holder indicator (Y/N) |
| CUST-FICO-CREDIT-SCORE | 9(03) | Numeric | FICO credit score (300-850) |
| FILLER | X(168) | Padding | Reserved to 500 bytes |

**Validation rules (from COACTUPC):**
- CUST-SSN: 9-digit numeric, validated for non-zero
- CUST-ADDR-STATE-CD: Validated against CSLKPCDY state code table
- CUST-ADDR-ZIP: First 2 digits validated against CSLKPCDY state-to-ZIP mapping
- CUST-PHONE-NUM-1/2: Area code (first 3 digits) validated against CSLKPCDY phone area code table
- CUST-DOB-YYYY-MM-DD: Validated via CSUTLDPY (year 1900-2099, valid month/day, leap year check)
- CUST-FICO-CREDIT-SCORE: Range 300-850

**VSAM key:** CUST-ID (bytes 0-8, 9-byte key)

---

### CUSTREC.cpy — Customer Statement Record

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CUST-ID | 9(09) | Numeric | Customer identifier |
| CUST-FIRST-NAME | X(25) | Alpha | First name |
| CUST-MIDDLE-NAME | X(25) | Alpha | Middle name |
| CUST-LAST-NAME | X(25) | Alpha | Last name |

**Purpose:** Subset of customer fields used in statement generation (CBSTM03A). Minimal record for header printing.

---

## 3. Card Entity

### CVACT02Y.cpy — Card Record (RECLN 150)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CARD-NUM | X(16) | Alpha | Card number (primary key) |
| CARD-ACCT-ID | 9(11) | Numeric | Associated account ID |
| CARD-CVV-CD | 9(03) | Numeric | Card verification value (CVV) |
| CARD-EMBOSSED-NAME | X(50) | Alpha | Name printed on card |
| CARD-EXPIRAION-DATE | X(10) | Date string | Card expiration date |
| CARD-ACTIVE-STATUS | X(01) | Alpha | Card status (Y=Active, N=Inactive) |
| FILLER | X(59) | Padding | Reserved to 150 bytes |

**VSAM key:** CARD-NUM (bytes 0-15, 16-byte key)
**AIX:** CARD-ACCT-ID (bytes 16-26, non-unique alternate index)

---

### CVCRD01Y.cpy — Card Work Areas

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CCARD-AID | X(5) | Alpha | AID key pressed (ENTER, CLEAR, PA1, PA2, PFK01-PFK12) |
| CCARD-NEXT-PROG | X(8) | Alpha | Next program to XCTL to |
| CCARD-NEXT-MAPSET | X(7) | Alpha | Next BMS mapset name |
| CCARD-NEXT-MAP | X(7) | Alpha | Next BMS map name |
| CCARD-ERROR-MSG | X(75) | Alpha | Error message to display |
| CCARD-RETURN-MSG | X(75) | Alpha | Return/info message to display |
| CC-ACCT-ID | X(11) / 9(11) | Alpha/Numeric | Working account ID (with REDEFINES) |
| CC-CARD-NUM | X(16) / 9(16) | Alpha/Numeric | Working card number (with REDEFINES) |
| CC-CUST-ID | X(09) / 9(9) | Alpha/Numeric | Working customer ID (with REDEFINES) |

**Purpose:** Shared working-storage for all online CICS card-related programs. Contains screen navigation state and message buffers. 88-level values define valid AID keys.

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| TRAN-ID | X(16) | Alpha | Unique transaction identifier |
| TRAN-TYPE-CD | X(02) | Alpha | Transaction type code (links to CVTRA03Y) |
| TRAN-CAT-CD | 9(04) | Numeric | Transaction category (links to CVTRA04Y) |
| TRAN-SOURCE | X(10) | Alpha | Transaction origin source |
| TRAN-DESC | X(100) | Alpha | Transaction description |
| TRAN-AMT | S9(09)V99 | Signed decimal | Transaction amount |
| TRAN-MERCHANT-ID | 9(09) | Numeric | Merchant identifier |
| TRAN-MERCHANT-NAME | X(50) | Alpha | Merchant name |
| TRAN-MERCHANT-CITY | X(50) | Alpha | Merchant city |
| TRAN-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP code |
| TRAN-CARD-NUM | X(16) | Alpha | Card number used |
| TRAN-ORIG-TS | X(26) | Timestamp | Original transaction timestamp |
| TRAN-PROC-TS | X(26) | Timestamp | Processing timestamp |
| FILLER | X(20) | Padding | Reserved to 350 bytes |

**VSAM key:** TRAN-ID (bytes 0-15, 16-byte key)

---

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Identical structure to CVTRA05Y but with `DALYTRAN-` prefix. Used as input to CBTRN01C (validation) and CBTRN02C (posting). After posting, daily records are merged into the master transaction file.

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| DALYTRAN-ID | X(16) | Alpha | Daily transaction identifier |
| DALYTRAN-TYPE-CD | X(02) | Alpha | Transaction type code |
| DALYTRAN-CAT-CD | 9(04) | Numeric | Transaction category |
| DALYTRAN-SOURCE | X(10) | Alpha | Transaction source |
| DALYTRAN-DESC | X(100) | Alpha | Description |
| DALYTRAN-AMT | S9(09)V99 | Signed decimal | Amount |
| DALYTRAN-MERCHANT-ID | 9(09) | Numeric | Merchant ID |
| DALYTRAN-MERCHANT-NAME | X(50) | Alpha | Merchant name |
| DALYTRAN-MERCHANT-CITY | X(50) | Alpha | Merchant city |
| DALYTRAN-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP |
| DALYTRAN-CARD-NUM | X(16) | Alpha | Card number |
| DALYTRAN-ORIG-TS | X(26) | Timestamp | Original timestamp |
| DALYTRAN-PROC-TS | X(26) | Timestamp | Processing timestamp |
| FILLER | X(20) | Padding | Reserved |

---

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| TRAN-TYPE | X(02) | Alpha | Transaction type code (PK) |
| TRAN-TYPE-DESC | X(50) | Alpha | Type description (e.g., "Purchase", "Cash Advance") |
| FILLER | X(08) | Padding | Reserved |

---

### CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| TRAN-TYPE-CD | X(02) | Alpha | Parent transaction type code |
| TRAN-CAT-CD | 9(04) | Numeric | Category code within type |
| TRAN-CAT-TYPE-DESC | X(50) | Alpha | Category description |
| FILLER | X(04) | Padding | Reserved |

**Composite key:** TRAN-TYPE-CD + TRAN-CAT-CD

---

### COSTM01.CPY — Statement Transaction Record

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| TRNX-CARD-NUM | X(16) | Alpha | Card number (part of composite key) |
| TRNX-ID | X(16) | Alpha | Transaction ID (part of composite key) |
| TRNX-TYPE-CD | X(02) | Alpha | Transaction type code |
| TRNX-CAT-CD | 9(04) | Numeric | Category code |
| TRNX-SOURCE | X(10) | Alpha | Source |
| TRNX-DESC | X(100) | Alpha | Description |
| TRNX-AMT | S9(09)V99 | Signed decimal | Amount |
| TRNX-MERCHANT-ID | 9(09) | Numeric | Merchant ID |
| TRNX-MERCHANT-NAME | X(50) | Alpha | Merchant name |
| TRNX-MERCHANT-CITY | X(50) | Alpha | Merchant city |
| TRNX-MERCHANT-ZIP | X(10) | Alpha | Merchant ZIP |
| TRNX-ORIG-TS | X(26) | Timestamp | Original timestamp |
| TRNX-PROC-TS | X(26) | Timestamp | Processing timestamp |
| FILLER | X(20) | Padding | Reserved |

**Purpose:** Altered layout of transaction data sorted by card number + transaction ID for statement printing.

---

### CVTRA07Y.cpy — Transaction Report Structures

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| REPT-SHORT-NAME | X(38) | Alpha | Report identifier ("DALYREPT") |
| REPT-LONG-NAME | X(41) | Alpha | Report title ("Daily Transaction Report") |
| REPT-START-DATE | X(10) | Date | Report start date |
| REPT-END-DATE | X(10) | Date | Report end date |
| TRAN-REPORT-TRANS-ID | X(16) | Alpha | Transaction ID in report |
| TRAN-REPORT-ACCOUNT-ID | X(11) | Alpha | Account ID in report |
| TRAN-REPORT-TYPE-CD | X(02) | Alpha | Type code with description |
| TRAN-REPORT-CAT-CD | 9(04) | Numeric | Category code with description |
| TRAN-REPORT-SOURCE | X(10) | Alpha | Source |
| TRAN-REPORT-AMT | -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount |
| REPT-PAGE-TOTAL | +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| REPT-ACCOUNT-TOTAL | +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |
| REPT-GRAND-TOTAL | +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total |

**Purpose:** Defines the print layout for CBTRN03C daily transaction reports, including headers, detail lines, and summary totals.

---

## 5. Authorization Entity (IMS/DB2/MQ)

### CIPAUSMY.cpy — IMS Segment: Pending Auth Summary (Root)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PA-ACCT-ID | S9(11) COMP-3 | Packed decimal | Account identifier |
| PA-CUST-ID | 9(09) | Numeric | Customer identifier |
| PA-AUTH-STATUS | X(01) | Alpha | Authorization status |
| PA-ACCOUNT-STATUS (×5) | X(02) OCCURS 5 | Alpha array | Account status history (5 entries) |
| PA-CREDIT-LIMIT | S9(09)V99 COMP-3 | Packed decimal | Credit limit |
| PA-CASH-LIMIT | S9(09)V99 COMP-3 | Packed decimal | Cash advance limit |
| PA-CREDIT-BALANCE | S9(09)V99 COMP-3 | Packed decimal | Available credit balance |
| PA-CASH-BALANCE | S9(09)V99 COMP-3 | Packed decimal | Available cash balance |
| PA-APPROVED-AUTH-CNT | S9(04) COMP | Binary | Count of approved authorizations |
| PA-DECLINED-AUTH-CNT | S9(04) COMP | Binary | Count of declined authorizations |
| PA-APPROVED-AUTH-AMT | S9(09)V99 COMP-3 | Packed decimal | Total approved amount |
| PA-DECLINED-AUTH-AMT | S9(09)V99 COMP-3 | Packed decimal | Total declined amount |
| FILLER | X(34) | Padding | Reserved |

**IMS hierarchy:** Root segment. PA-ACCT-ID is the root key. Storage uses COMP-3 (packed BCD) for efficient IMS storage.

---

### CIPAUDTY.cpy — IMS Segment: Pending Auth Detail (Dependent)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PA-AUTH-DATE-9C | S9(05) COMP-3 | Packed decimal | Authorization date (compressed key) |
| PA-AUTH-TIME-9C | S9(09) COMP-3 | Packed decimal | Authorization time (compressed key) |
| PA-AUTH-ORIG-DATE | X(06) | Alpha | Original auth date (MMDDYY) |
| PA-AUTH-ORIG-TIME | X(06) | Alpha | Original auth time (HHMMSS) |
| PA-CARD-NUM | X(16) | Alpha | Card number |
| PA-AUTH-TYPE | X(04) | Alpha | Authorization type |
| PA-CARD-EXPIRY-DATE | X(04) | Alpha | Card expiration (MMYY) |
| PA-MESSAGE-TYPE | X(06) | Alpha | Message type code |
| PA-MESSAGE-SOURCE | X(06) | Alpha | Message source |
| PA-AUTH-ID-CODE | X(06) | Alpha | Authorization ID code |
| PA-AUTH-RESP-CODE | X(02) | Alpha | Response code (88: '00' = approved) |
| PA-AUTH-RESP-REASON | X(04) | Alpha | Response reason code |
| PA-PROCESSING-CODE | 9(06) | Numeric | Processing code |
| PA-TRANSACTION-AMT | S9(10)V99 COMP-3 | Packed decimal | Requested transaction amount |
| PA-APPROVED-AMT | S9(10)V99 COMP-3 | Packed decimal | Approved amount |
| PA-MERCHANT-CATAGORY-CODE | X(04) | Alpha | Merchant category code (MCC) |
| PA-ACQR-COUNTRY-CODE | X(03) | Alpha | Acquirer country code |
| PA-POS-ENTRY-MODE | 9(02) | Numeric | POS entry mode |
| PA-MERCHANT-ID | X(15) | Alpha | Merchant identifier |
| PA-MERCHANT-NAME | X(22) | Alpha | Merchant name |
| PA-MERCHANT-CITY | X(13) | Alpha | Merchant city |
| PA-MERCHANT-STATE | X(02) | Alpha | Merchant state |
| PA-MERCHANT-ZIP | X(09) | Alpha | Merchant ZIP |
| PA-TRANSACTION-ID | X(15) | Alpha | Transaction ID |
| PA-MATCH-STATUS | X(01) | Alpha | Match status flag |
| PA-AUTH-FRAUD | X(01) | Alpha | Fraud indicator |
| PA-FRAUD-RPT-DATE | X(08) | Alpha | Fraud report date |
| FILLER | X(17) | Padding | Reserved |

**88-level validation rules:**
- PA-AUTH-RESP-CODE: `'00'` = PA-AUTH-APPROVED
- PA-MATCH-STATUS: `'P'` = Pending, `'D'` = Declined, `'E'` = Expired, `'M'` = Matched
- PA-AUTH-FRAUD: `'F'` = Fraud confirmed, `'R'` = Fraud removed

**IMS hierarchy:** Dependent segment under CIPAUSMY. Key: PA-AUTH-DATE-9C + PA-AUTH-TIME-9C.

---

### CCPAURQY.cpy — Authorization Request (MQ Message)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PA-RQ-AUTH-DATE | X(06) | Alpha | Request date |
| PA-RQ-AUTH-TIME | X(06) | Alpha | Request time |
| PA-RQ-CARD-NUM | X(16) | Alpha | Card number |
| PA-RQ-AUTH-TYPE | X(04) | Alpha | Authorization type |
| PA-RQ-CARD-EXPIRY-DATE | X(04) | Alpha | Card expiry |
| PA-RQ-MESSAGE-TYPE | X(06) | Alpha | Message type |
| PA-RQ-MESSAGE-SOURCE | X(06) | Alpha | Message source |
| PA-RQ-PROCESSING-CODE | 9(06) | Numeric | Processing code |
| PA-RQ-TRANSACTION-AMT | +9(10).99 | Edited numeric | Transaction amount |
| PA-RQ-MERCHANT-CATAGORY-CODE | X(04) | Alpha | MCC |
| PA-RQ-ACQR-COUNTRY-CODE | X(03) | Alpha | Acquirer country |
| PA-RQ-POS-ENTRY-MODE | 9(02) | Numeric | POS entry mode |
| PA-RQ-MERCHANT-ID | X(15) | Alpha | Merchant ID |
| PA-RQ-MERCHANT-NAME | X(22) | Alpha | Merchant name |
| PA-RQ-MERCHANT-CITY | X(13) | Alpha | Merchant city |
| PA-RQ-MERCHANT-STATE | X(02) | Alpha | Merchant state |
| PA-RQ-MERCHANT-ZIP | X(09) | Alpha | Merchant ZIP |
| PA-RQ-TRANSACTION-ID | X(15) | Alpha | Transaction ID |

---

### CCPAURLY.cpy — Authorization Response (MQ Message)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| PA-RL-CARD-NUM | X(16) | Alpha | Card number |
| PA-RL-TRANSACTION-ID | X(15) | Alpha | Transaction ID |
| PA-RL-AUTH-ID-CODE | X(06) | Alpha | Authorization ID |
| PA-RL-AUTH-RESP-CODE | X(02) | Alpha | Response code |
| PA-RL-AUTH-RESP-REASON | X(04) | Alpha | Response reason |
| PA-RL-APPROVED-AMT | +9(10).99 | Edited numeric | Approved amount |

---

### CCPAUERY.cpy — Authorization Error Log

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| ERR-DATE | X(06) | Alpha | Error date |
| ERR-TIME | X(06) | Alpha | Error time |
| ERR-APPLICATION | X(08) | Alpha | Application name |
| ERR-PROGRAM | X(08) | Alpha | Program name |
| ERR-LOCATION | X(04) | Alpha | Location code |
| ERR-LEVEL | X(01) | Alpha | Error level |
| ERR-SUBSYSTEM | X(01) | Alpha | Subsystem indicator |
| ERR-CODE-1 | X(09) | Alpha | Primary error code |
| ERR-CODE-2 | X(09) | Alpha | Secondary error code |
| ERR-MESSAGE | X(50) | Alpha | Error message |
| ERR-EVENT-KEY | X(20) | Alpha | Event key for tracking |

**88-level values:**
- ERR-LEVEL: L=Log, I=Info, W=Warning, C=Critical
- ERR-SUBSYSTEM: A=Application, C=CICS, I=IMS, D=DB2, M=MQ, F=File

---

### IMS Database PCBs

**PASFLPCB.CPY — Auth Summary PCB:**

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| PASFL-DBDNAME | X(08) | Database name |
| PASFL-SEG-LEVEL | X(02) | Segment level |
| PASFL-PCB-STATUS | X(02) | Status code (spaces=success, GE=not found) |
| PASFL-PCB-PROCOPT | X(04) | Processing options |
| PASFL-SEG-NAME | X(08) | Segment name |
| PASFL-KEYFB | X(255) | Key feedback area |

**PADFLPCB.CPY — Auth Detail PCB:** Same structure as PASFLPCB with `PADFL-` prefix.

**IMSFUNCS.cpy — IMS Function Codes:**

| Code | Value | Meaning |
|------|-------|---------|
| FUNC-GU | 'GU  ' | Get Unique (direct access by key) |
| FUNC-GHU | 'GHU ' | Get Hold Unique (for update) |
| FUNC-GN | 'GN  ' | Get Next (sequential) |
| FUNC-GHN | 'GHN ' | Get Hold Next |
| FUNC-GNP | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | 'REPL' | Replace (update) |
| FUNC-ISRT | 'ISRT' | Insert |
| FUNC-DLET | 'DLET' | Delete |

---

## 6. User/Security Entity

### CSUSR01Y.cpy — Security User Data (RECLN 80)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| SEC-USR-ID | X(08) | Alpha | User login ID |
| SEC-USR-FNAME | X(20) | Alpha | User first name |
| SEC-USR-LNAME | X(20) | Alpha | User last name |
| SEC-USR-PWD | X(08) | Alpha | User password (plain text) |
| SEC-USR-TYPE | X(01) | Alpha | User type (A=Admin, U=User) |
| SEC-USR-FILLER | X(23) | Padding | Reserved |

**Security note:** Passwords stored in plain text. Must be hashed in migration.

---

### UNUSED1Y.cpy — Unused User Record (Legacy)

Same structure as CSUSR01Y with `UNUSED-` prefix. Not referenced by any active program.

---

## 7. System/Infrastructure Copybooks

### COCOM01Y.cpy — Communication Area (COMMAREA)

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| CDEMO-FROM-TRANID | X(04) | Alpha | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | X(08) | Alpha | Source program name |
| CDEMO-TO-TRANID | X(04) | Alpha | Target transaction ID |
| CDEMO-TO-PROGRAM | X(08) | Alpha | Target program name |
| CDEMO-USER-ID | X(08) | Alpha | Logged-in user ID |
| CDEMO-USER-TYPE | X(01) | Alpha | User type (88: A=Admin, U=User) |
| CDEMO-PGM-CONTEXT | 9(01) | Numeric | Program context (88: 0=Enter, 1=Reenter) |
| CDEMO-CUST-ID | 9(09) | Numeric | Customer ID in context |
| CDEMO-CUST-FNAME | X(25) | Alpha | Customer first name |
| CDEMO-CUST-MNAME | X(25) | Alpha | Customer middle name |
| CDEMO-CUST-LNAME | X(25) | Alpha | Customer last name |
| CDEMO-ACCT-ID | 9(11) | Numeric | Account ID in context |
| CDEMO-ACCT-STATUS | X(01) | Alpha | Account status |
| CDEMO-CARD-NUM | 9(16) | Numeric | Card number in context |
| CDEMO-LAST-MAP | X(7) | Alpha | Last BMS map displayed |
| CDEMO-LAST-MAPSET | X(7) | Alpha | Last BMS mapset |

**Purpose:** Passed between CICS programs via XCTL/LINK. Contains user session state, navigation context, and the currently selected customer/account/card.

---

### CSDAT01Y.cpy — Date/Time Work Areas

| Field | PIC Clause | Data Type | Business Meaning |
|-------|-----------|-----------|-----------------|
| WS-CURDATE-YEAR | 9(04) | Numeric | Current year (YYYY) |
| WS-CURDATE-MONTH | 9(02) | Numeric | Current month (MM) |
| WS-CURDATE-DAY | 9(02) | Numeric | Current day (DD) |
| WS-CURTIME-HOURS | 9(02) | Numeric | Current hours |
| WS-CURTIME-MINUTE | 9(02) | Numeric | Current minutes |
| WS-CURTIME-SECOND | 9(02) | Numeric | Current seconds |
| WS-CURDATE-MM-DD-YY | X(08) | Edited | Formatted date (MM/DD/YY) |
| WS-CURTIME-HH-MM-SS | X(08) | Edited | Formatted time (HH:MM:SS) |
| WS-TIMESTAMP | X(26) | Timestamp | Full timestamp (YYYY-MM-DD HH:MM:SS.SSSSSS) |

---

### CSLKPCDY.cpy — Lookup Code Repository (1,318 lines)

Contains three validation tables implemented as 88-level condition names:

1. **Phone area codes** (WS-US-PHONE-AREA-CODE-TO-EDIT): 300+ valid North American area codes
2. **US state codes** (WS-US-STATE-CODE-TO-EDIT): All 50 states + territories
3. **State-to-ZIP prefix mapping** (WS-US-STATE-AND-FIRST2-ZIP): Maps state codes to valid ZIP code first-2-digit ranges

Used by COACTUPC for exhaustive field validation.

---

### CSUTLDWY.cpy — Date Validation Working Storage

Contains working-storage fields for the date validation paragraphs in CSUTLDPY. Includes flags for validation status (FLG-YEAR-NOT-OK, FLG-MONTH-NOT-OK, FLG-DAY-NOT-OK) and edit message buffers.

### CSUTLDPY.cpy — Date Validation Procedures (375 lines)

Reusable procedure-division paragraphs for date validation:
- **EDIT-DATE-CCYYMMDD**: Main entry — validates complete date
- **EDIT-YEAR-CCYY**: Year validation (1900-2099, century check)
- **EDIT-MONTH**: Month 01-12 validation
- **EDIT-DAY**: Day validation with month-specific maximums + leap year
- **EDIT-DATE-OF-BIRTH**: DOB-specific validation (must be in past)

**Note:** This is a *procedure division* copybook (contains paragraphs, not data definitions). Included via COPY in the PROCEDURE DIVISION of COACTUPC.

---

### CSSETATY.cpy — Screen Attribute Setting (Macro)

Template copybook used with `COPY REPLACING`:
```cobol
COPY CSSETATY REPLACING (TESTVAR1) BY ACCT-ID
                        (SCRNVAR2) BY ACTIDINI
                        (MAPNAME3) BY COACTUPI
```
Sets BMS field color to red if field is in error; displays `*` for blank required fields. COACTUPC uses this 39× for each validated field.

---

### CSSTRPFY.cpy — String Padding/Formatting Utility (85 lines)

Procedure division copybook providing paragraph `STRIP-PADDING-FROM-FIELDS` — removes trailing spaces from BMS input fields. Used by all screen programs for input cleanup.

---

### CSMSG01Y.cpy — Common Messages

| Field | Value | Purpose |
|-------|-------|---------|
| CCDA-MSG-THANK-YOU | "Thank you for using CardDemo application..." | Session end message |
| CCDA-MSG-INVALID-KEY | "Invalid key pressed. Please see below..." | Input error message |

---

### CSMSG02Y.cpy — Abend Data

| Field | PIC Clause | Purpose |
|-------|-----------|---------|
| ABEND-CODE | X(4) | Abend code |
| ABEND-CULPRIT | X(8) | Program causing abend |
| ABEND-REASON | X(50) | Reason text |
| ABEND-MSG | X(72) | Full abend message |

---

### COTTL01Y.cpy — Screen Titles

| Field | Value |
|-------|-------|
| CCDA-TITLE01 | "AWS Mainframe Modernization" |
| CCDA-TITLE02 | "CardDemo" |
| CCDA-THANK-YOU | "Thank you for using CCDA application..." |

---

### COADM02Y.cpy — Admin Menu Options

Defines 6 admin menu entries routing to COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC.

### COMEN02Y.cpy — Main Menu Options

Defines 11 user menu entries routing to COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C.

### CODATECN.cpy — Date Conversion Record

Converts between YYYYMMDD and YYYY-MM-DD formats via REDEFINES. Used by CODATE01 (MQ date service).

---

## 8. Export Entity

### CVEXPORT.cpy — Multi-Record Export Layout (103 lines, RECLN 500)

| Field | PIC Clause | Business Meaning |
|-------|-----------|-----------------|
| EXPORT-REC-TYPE | X(1) | Record type discriminator |
| EXPORT-TIMESTAMP | X(26) | Export timestamp |
| EXPORT-SEQUENCE-NUM | 9(9) COMP | Sequence number (binary) |
| EXPORT-BRANCH-ID | X(4) | Branch identifier |
| EXPORT-REGION-CODE | X(5) | Region code |
| EXPORT-RECORD-DATA | X(460) | Polymorphic data area |

Contains REDEFINES for 5 record types:
1. **EXPORT-CUSTOMER-DATA** — mirrors CVCUS01Y with COMP-3 optimizations
2. **EXPORT-ACCOUNT-DATA** — mirrors CVACT01Y with COMP-3 for balances
3. **EXPORT-TRANSACTION-DATA** — mirrors CVTRA05Y with COMP-3 for amounts
4. **EXPORT-CARD-XREF-DATA** — mirrors CVACT03Y with COMP for ACCT-ID
5. **EXPORT-CARD-DATA** — mirrors CVACT02Y with COMP for IDs

**Purpose:** Unified export format for branch data migration. Uses storage-optimized COMP/COMP-3 fields.

---

## 9. DB2 Table Definitions

### DCLTRTYP (DB2 INCLUDE) — Transaction Type Table

| Column | SQL Type | Maps To |
|--------|----------|---------|
| TRAN_TYPECODE | CHAR(2) | TRAN-TYPE in CVTRA03Y |
| TRAN_TYPE_DESC | VARCHAR(50) | TRAN-TYPE-DESC |

### DCLTRCAT (DB2 INCLUDE) — Transaction Category Table

| Column | SQL Type | Maps To |
|--------|----------|---------|
| TRAN_TYPECODE | CHAR(2) | TRAN-TYPE-CD in CVTRA04Y |
| TRAN_CATCODE | INT | TRAN-CAT-CD |
| TRAN_CAT_DESC | VARCHAR(50) | TRAN-CAT-TYPE-DESC |
