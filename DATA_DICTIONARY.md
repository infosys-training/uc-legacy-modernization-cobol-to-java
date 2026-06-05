# Data Dictionary — CardDemo COBOL Estate

> **Total Copybooks:** 47 | **Business Entity Groups:** 10 | **Core Record Types:** 5

---

## 1. Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

**Record:** `ACCOUNT-RECORD` | **VSAM File:** ACCTFILE | **Key:** ACCT-ID (11,0) | **Used by:** 13+ programs

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | ACCOUNT-RECORD | — | Group | Account master record | 300-byte KSDS |
| 05 | ACCT-ID | PIC 9(11) | Numeric | Account identifier — primary key | 11-digit numeric |
| 05 | ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Active status flag | 'Y' = active, 'N' = inactive |
| 05 | ACCT-CURR-BAL | PIC S9(10)V99 | Signed Decimal | Current account balance | Signed with 2 decimal places |
| 05 | ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Credit limit | Signed with 2 decimal places |
| 05 | ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed Decimal | Cash advance credit limit | Signed with 2 decimal places |
| 05 | ACCT-OPEN-DATE | PIC X(10) | Alphanumeric | Account open date | Format: CCYYMMDD or YYYY-MM-DD |
| 05 | ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Account expiration date | Note: typo in field name |
| 05 | ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric | Card reissue date | |
| 05 | ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed Decimal | Current cycle credit total | |
| 05 | ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed Decimal | Current cycle debit total | |
| 05 | ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | Account holder ZIP code | |
| 05 | ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Disclosure/interest rate group ID | Links to CVTRA02Y disclosure group |
| 05 | FILLER | PIC X(178) | Filler | Reserved space | Padding to 300 bytes |

### CVACT02Y.cpy — Card Record (RECLN 150)

**Record:** `CARD-RECORD` | **VSAM File:** CARDFILE | **Key:** CARD-NUM (16,0) | **Used by:** 8+ programs

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARD-RECORD | — | Group | Card master record | 150-byte KSDS |
| 05 | CARD-NUM | PIC X(16) | Alphanumeric | Card number — primary key | 16-char card number |
| 05 | CARD-ACCT-ID | PIC 9(11) | Numeric | Account ID — FK to CVACT01Y | |
| 05 | CARD-CVV-CD | PIC 9(03) | Numeric | Card verification value | 3-digit CVV |
| 05 | CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | Name embossed on card | |
| 05 | CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric | Card expiration date | Note: typo in field name |
| 05 | CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | Card active status | 'Y'/'N' |
| 05 | FILLER | PIC X(59) | Filler | Reserved space | Padding to 150 bytes |

### CVACT03Y.cpy — Card Cross-Reference (RECLN 50)

**Record:** `CARD-XREF-RECORD` | **VSAM File:** XREFFILE | **Key:** XREF-CARD-NUM (16,0) | **Used by:** 11+ programs

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARD-XREF-RECORD | — | Group | Card-account-customer cross-reference | 50-byte KSDS |
| 05 | XREF-CARD-NUM | PIC X(16) | Alphanumeric | Card number — primary key | Links to CVACT02Y |
| 05 | XREF-CUST-ID | PIC 9(09) | Numeric | Customer ID — FK to CVCUS01Y | |
| 05 | XREF-ACCT-ID | PIC 9(11) | Numeric | Account ID — FK to CVACT01Y | |
| 05 | FILLER | PIC X(14) | Filler | Reserved space | Padding to 50 bytes |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

**Record:** `CUSTOMER-RECORD` | **VSAM File:** CUSTFILE | **Key:** CUST-ID (9,0) | **Used by:** 6+ programs

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CUSTOMER-RECORD | — | Group | Customer master record | 500-byte KSDS |
| 05 | CUST-ID | PIC 9(09) | Numeric | Customer identifier — primary key | 9-digit |
| 05 | CUST-FIRST-NAME | PIC X(25) | Alphanumeric | First name | |
| 05 | CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | Middle name | |
| 05 | CUST-LAST-NAME | PIC X(25) | Alphanumeric | Last name | |
| 05 | CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | Address line 1 | |
| 05 | CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | Address line 2 | |
| 05 | CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | Address line 3 | |
| 05 | CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | US state code | Validated via CSLKPCDY (50 states) |
| 05 | CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | Country code | |
| 05 | CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | ZIP code | Validated via CSLKPCDY |
| 05 | CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | Primary phone number | Area code validated via CSLKPCDY (NANPA) |
| 05 | CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | Secondary phone number | |
| 05 | CUST-SSN | PIC 9(09) | Numeric | Social Security Number | 9-digit; validated in COACTUPC |
| 05 | CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | Government-issued ID | |
| 05 | CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric | Date of birth | Validated via CSUTLDPY (not future) |
| 05 | CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | EFT/bank account ID | For electronic fund transfers |
| 05 | CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | Primary card holder indicator | |
| 05 | CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric | FICO credit score | Range 300–850 |
| 05 | FILLER | PIC X(168) | Filler | Reserved space | Padding to 500 bytes |

### CUSTREC.cpy — Customer Record (Alternate Layout)

**Record:** `CUSTOMER-RECORD` | **Identical structure to CVCUS01Y** | **Used by:** CBSTM03A batch programs

Same field layout as CVCUS01Y.cpy with minor formatting differences. Field `CUST-DOB-YYYYMMDD` instead of `CUST-DOB-YYYY-MM-DD`.

---

## 3. Card/Credit Working Areas

### CVCRD01Y.cpy — Credit Card Working Areas

**Record:** `CC-WORK-AREAS` | **Used by:** All CICS programs (common COMMAREA extension)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CC-WORK-AREAS | — | Group | Common working area for CICS programs | |
| 05 | CC-WORK-AREA | — | Group | Main work area | |
| 10 | CCARD-AID | PIC X(5) | Alphanumeric | Attention Identifier key pressed | 88-level conditions for ENTER, CLEAR, PA1, PA2, PFK01–PFK12 |
| 10 | CCARD-NEXT-PROG | PIC X(8) | Alphanumeric | Next program to XCTL to | |
| 10 | CCARD-NEXT-MAPSET | PIC X(7) | Alphanumeric | Next BMS mapset | |
| 10 | CCARD-NEXT-MAP | PIC X(7) | Alphanumeric | Next BMS map | |
| 10 | CCARD-ERROR-MSG | PIC X(75) | Alphanumeric | Error message for screen display | |
| 10 | CCARD-RETURN-MSG | PIC X(75) | Alphanumeric | Return/success message | 88: CCARD-RETURN-MSG-OFF = LOW-VALUES |
| 10 | CC-ACCT-ID | PIC X(11) | Alphanumeric | Current account ID in context | REDEFINES as CC-ACCT-ID-N PIC 9(11) |
| 10 | CC-CARD-NUM | PIC X(16) | Alphanumeric | Current card number in context | REDEFINES as CC-CARD-NUM-N PIC 9(16) |
| 10 | CC-CUST-ID | PIC X(09) | Alphanumeric | Current customer ID in context | REDEFINES as CC-CUST-ID-N PIC 9(9) |

---

## 4. Transaction Entity

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

**Record:** `TRAN-CAT-BAL-RECORD` | **VSAM File:** TCATBALF | **Key:** compound (ACCT-ID + TYPE-CD + CAT-CD, 17,0)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-CAT-BAL-RECORD | — | Group | Category balance per account | 50-byte KSDS |
| 05 | TRAN-CAT-KEY | — | Group | Compound key | |
| 10 | TRANCAT-ACCT-ID | PIC 9(11) | Numeric | Account ID | FK to CVACT01Y |
| 10 | TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to CVTRA03Y |
| 10 | TRANCAT-CD | PIC 9(04) | Numeric | Category code | FK to CVTRA04Y |
| 05 | TRAN-CAT-BAL | PIC S9(09)V99 | Signed Decimal | Running balance for this category | Updated by CBTRN02C |
| 05 | FILLER | PIC X(22) | Filler | Reserved space | |

### CVTRA02Y.cpy — Disclosure Group (RECLN 50)

**Record:** `DIS-GROUP-RECORD` | **VSAM File:** DISCGRP | **Key:** compound (GROUP-ID + TYPE-CD + CAT-CD, 16,0)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | DIS-GROUP-RECORD | — | Group | Interest rate by group/type/category | 50-byte KSDS |
| 05 | DIS-GROUP-KEY | — | Group | Compound key | |
| 10 | DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | Account group ID | Links to ACCT-GROUP-ID |
| 10 | DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | |
| 10 | DIS-TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | |
| 05 | DIS-INT-RATE | PIC S9(04)V99 | Signed Decimal | Interest rate | Used by CBACT04C for interest calc |
| 05 | FILLER | PIC X(28) | Filler | Reserved space | |

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

**Record:** `TRAN-TYPE-RECORD` | **VSAM File:** TRANTYPE | **Key:** TRAN-TYPE (2,0)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-TYPE-RECORD | — | Group | Transaction type reference | 60-byte KSDS |
| 05 | TRAN-TYPE | PIC X(02) | Alphanumeric | Type code — primary key | e.g., "SA" = Sale, "RT" = Return |
| 05 | TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | Type description | |
| 05 | FILLER | PIC X(08) | Filler | Reserved space | |

### CVTRA04Y.cpy — Transaction Category (RECLN 60)

**Record:** `TRAN-CAT-RECORD` | **VSAM File:** TRANCATG | **Key:** compound (TYPE-CD + CAT-CD, 6,0)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-CAT-RECORD | — | Group | Transaction category reference | 60-byte KSDS |
| 05 | TRAN-CAT-KEY | — | Group | Compound key | |
| 10 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to CVTRA03Y |
| 10 | TRAN-CAT-CD | PIC 9(04) | Numeric | Category code | |
| 05 | TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | Category description | |
| 05 | FILLER | PIC X(04) | Filler | Reserved space | |

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

**Record:** `TRAN-RECORD` | **VSAM File:** TRANSACT | **Key:** TRAN-ID (16,0)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRAN-RECORD | — | Group | Posted transaction record | 350-byte KSDS |
| 05 | TRAN-ID | PIC X(16) | Alphanumeric | Transaction ID — primary key | System-generated |
| 05 | TRAN-TYPE-CD | PIC X(02) | Alphanumeric | Transaction type code | FK to CVTRA03Y |
| 05 | TRAN-CAT-CD | PIC 9(04) | Numeric | Transaction category code | FK to CVTRA04Y |
| 05 | TRAN-SOURCE | PIC X(10) | Alphanumeric | Transaction source | e.g., "Online", "POS" |
| 05 | TRAN-DESC | PIC X(100) | Alphanumeric | Transaction description | |
| 05 | TRAN-AMT | PIC S9(09)V99 | Signed Decimal | Transaction amount | Negative = credit |
| 05 | TRAN-MERCHANT-ID | PIC 9(09) | Numeric | Merchant identifier | |
| 05 | TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | Merchant name | |
| 05 | TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | Merchant city | |
| 05 | TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | Merchant ZIP code | |
| 05 | TRAN-CARD-NUM | PIC X(16) | Alphanumeric | Card number used | FK to CVACT02Y |
| 05 | TRAN-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp | ISO 8601 format |
| 05 | TRAN-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | Set by batch posting |
| 05 | FILLER | PIC X(20) | Filler | Reserved space | |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

**Record:** `DALYTRAN-RECORD` | **Sequential File:** DALYTRAN | **Used by:** CBTRN01C, CBTRN02C

Identical structure to CVTRA05Y but with `DALYTRAN-` prefix on all fields. Used as input to the batch posting process (CBTRN02C). The daily file is loaded before being posted to the master TRANSACT file.

### CVTRA07Y.cpy — Transaction Report Layout

**Records:** `REPORT-NAME-HEADER`, `TRANSACTION-DETAIL-REPORT`, `TRANSACTION-HEADER-1` | **Used by:** CBTRN03C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | REPORT-NAME-HEADER | — | Group | Report header line | |
| 05 | REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Short report name | VALUE 'DALYREPT' |
| 05 | REPT-LONG-NAME | PIC X(41) | Alphanumeric | Long report name | VALUE 'Daily Transaction Report' |
| 05 | REPT-DATE-HEADER | PIC X(12) | Alphanumeric | Date label | VALUE 'Date Range: ' |
| 05 | REPT-START-DATE | PIC X(10) | Alphanumeric | Report start date | |
| 05 | REPT-END-DATE | PIC X(10) | Alphanumeric | Report end date | |
| 01 | TRANSACTION-DETAIL-REPORT | — | Group | Detail line | |
| 05 | TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID | |
| 05 | TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID | |
| 05 | TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code | |
| 05 | TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description | |
| 05 | TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code | |
| 05 | TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description | |
| 05 | TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Source | |
| 05 | TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited Numeric | Formatted amount | |

---

## 5. Authorization Entity (IMS Sub-app)

### CCPAURQY.cpy — Pending Authorization Request

**Used by:** COPAUA0C (MQ request message)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Authorization date | YYMMDD format |
| 05 | PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Authorization time | HHMMSS format |
| 05 | PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number | |
| 05 | PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type | |
| 05 | PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry | YYMM |
| 05 | PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type code | |
| 05 | PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message source | |
| 05 | PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | |
| 05 | PA-RQ-TRANSACTION-AMT | PIC +9(10).99 | Edited Numeric | Requested amount | |
| 05 | PA-RQ-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | MCC code | ISO 18245 merchant category |
| 05 | PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country | |
| 05 | PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | |
| 05 | PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID | |
| 05 | PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name | |
| 05 | PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city | |
| 05 | PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state | |
| 05 | PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP | |
| 05 | PA-RQ-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID | |

### CCPAURLY.cpy — Pending Authorization Response

**Used by:** COPAUA0C (MQ response message)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number | |
| 05 | PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID | |
| 05 | PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID code | |
| 05 | PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code | '00' = approved |
| 05 | PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason | |
| 05 | PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited Numeric | Approved amount | |

### CIPAUDTY.cpy — IMS Segment: Pending Authorization Detail

**IMS Segment:** PAUTH detail | **Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | PA-AUTHORIZATION-KEY | — | Group | Segment sequence key | |
| 10 | PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed Decimal | Auth date (packed) | Compact storage |
| 10 | PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed Decimal | Auth time (packed) | |
| 05 | PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original auth date | YYMMDD |
| 05 | PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original auth time | HHMMSS |
| 05 | PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number | |
| 05 | PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type | |
| 05 | PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry | YYMM |
| 05 | PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type | |
| 05 | PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Message source | |
| 05 | PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Auth ID code | |
| 05 | PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code | 88: PA-AUTH-APPROVED VALUE '00' |
| 05 | PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Response reason | |
| 05 | PA-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code | |
| 05 | PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed Decimal | Transaction amount | |
| 05 | PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed Decimal | Approved amount | |
| 05 | PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | MCC code | |
| 05 | PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country | |
| 05 | PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | |
| 05 | PA-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID | |
| 05 | PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name | |
| 05 | PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city | |
| 05 | PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state | |
| 05 | PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP | |
| 05 | PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction ID | |
| 05 | PA-MATCH-STATUS | PIC X(01) | Alphanumeric | Match status | 88: P=Pending, D=Declined, E=Expired, M=Matched |
| 05 | PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | Fraud flag | 88: F=Confirmed, R=Removed |
| 05 | PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric | Fraud report date | YYYYMMDD |
| 05 | FILLER | PIC X(17) | Filler | Reserved | |

### CIPAUSMY.cpy — IMS Segment: Pending Authorization Summary

**IMS Segment:** PAUTH summary (root) | **Used by:** COPAUS0C, COPAUS1C, CBPAUP0C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | PA-ACCT-ID | PIC S9(11) COMP-3 | Packed Decimal | Account ID | Root segment key |
| 05 | PA-CUST-ID | PIC 9(09) | Numeric | Customer ID | |
| 05 | PA-AUTH-STATUS | PIC X(01) | Alphanumeric | Overall auth status | |
| 05 | PA-ACCOUNT-STATUS | PIC X(02) | Alphanumeric | Account status array | OCCURS 5 TIMES |
| 05 | PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed Decimal | Credit limit | |
| 05 | PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed Decimal | Cash limit | |
| 05 | PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed Decimal | Credit balance | |
| 05 | PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed Decimal | Cash balance | |
| 05 | PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary | Approved auth count | |
| 05 | PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary | Declined auth count | |
| 05 | PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed Decimal | Total approved amount | |
| 05 | PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed Decimal | Total declined amount | |
| 05 | FILLER | PIC X(34) | Filler | Reserved | |

### CCPAUERY.cpy — Error Log Record

**Used by:** COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, CBPAUP0C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | ERROR-LOG-RECORD | — | Group | Cross-subsystem error log | |
| 05 | ERR-DATE | PIC X(06) | Alphanumeric | Error date | YYMMDD |
| 05 | ERR-TIME | PIC X(06) | Alphanumeric | Error time | HHMMSS |
| 05 | ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name | |
| 05 | ERR-PROGRAM | PIC X(08) | Alphanumeric | Program name | |
| 05 | ERR-LOCATION | PIC X(04) | Alphanumeric | Error location code | |
| 05 | ERR-LEVEL | PIC X(01) | Alphanumeric | Severity level | 88: L=Log, I=Info, W=Warning, C=Critical |
| 05 | ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem identifier | 88: A=App, C=CICS, I=IMS, D=DB2, M=MQ, F=File |
| 05 | ERR-CODE-1 | PIC X(09) | Alphanumeric | Primary error code | |
| 05 | ERR-CODE-2 | PIC X(09) | Alphanumeric | Secondary error code | |
| 05 | ERR-MESSAGE | PIC X(50) | Alphanumeric | Error message text | |
| 05 | ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Event correlation key | |

### IMSFUNCS.cpy — IMS DL/I Function Codes

**Used by:** All IMS programs (CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, PAUDBLOD, PAUDBUNL, DBUNLDGS)

| Level | Field Name | PIC Clause | Value | Business Meaning |
|-------|-----------|------------|-------|-----------------|
| 05 | FUNC-GU | PIC X(04) | 'GU  ' | Get Unique — direct segment retrieval |
| 05 | FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique — for update/delete |
| 05 | FUNC-GN | PIC X(04) | 'GN  ' | Get Next — sequential retrieval |
| 05 | FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next — sequential for update |
| 05 | FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent — child segments |
| 05 | FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| 05 | FUNC-REPL | PIC X(04) | 'REPL' | Replace — update segment |
| 05 | FUNC-ISRT | PIC X(04) | 'ISRT' | Insert — add new segment |
| 05 | FUNC-DLET | PIC X(04) | 'DLET' | Delete — remove segment |
| 05 | PARMCOUNT | PIC S9(05) COMP-5 | +4 | DL/I parameter count |

---

## 6. User/Security Entity

### CSUSR01Y.cpy — Security User Data

**Record:** `SEC-USER-DATA` | **VSAM File:** USRSEC | **Key:** SEC-USR-ID (8,0) | **Used by:** COSGN00C, COUSR00C–03C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | SEC-USER-DATA | — | Group | User security record | 80-byte KSDS |
| 05 | SEC-USR-ID | PIC X(08) | Alphanumeric | User ID — primary key | |
| 05 | SEC-USR-FNAME | PIC X(20) | Alphanumeric | First name | |
| 05 | SEC-USR-LNAME | PIC X(20) | Alphanumeric | Last name | |
| 05 | SEC-USR-PWD | PIC X(08) | Alphanumeric | Password | Plain text (legacy) |
| 05 | SEC-USR-TYPE | PIC X(01) | Alphanumeric | User type | 'A' = Admin, 'U' = Regular |
| 05 | SEC-USR-FILLER | PIC X(23) | Filler | Reserved | Padding to 80 bytes |

---

## 7. Common/Shared Copybooks

### COCOM01Y.cpy — Communication Area (COMMAREA)

**Record:** `CARDDEMO-COMMAREA` | **Used by:** All CICS programs (passed via EXEC CICS XCTL/LINK)

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | CARDDEMO-COMMAREA | — | Group | Inter-program communication area | |
| 05 | CDEMO-GENERAL-INFO | — | Group | Navigation context | |
| 10 | CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source transaction ID | |
| 10 | CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name | |
| 10 | CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target transaction ID | |
| 10 | CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name | |
| 10 | CDEMO-USER-ID | PIC X(08) | Alphanumeric | Logged-in user ID | |
| 10 | CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | User type | 88: CDEMO-USRTYP-ADMIN = 'A', CDEMO-USRTYP-USER = 'U' |
| 10 | CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | Program entry context | 88: CDEMO-PGM-ENTER = 0, CDEMO-PGM-REENTER = 1 |
| 05 | CDEMO-CUSTOMER-INFO | — | Group | Current customer context | |
| 10 | CDEMO-CUST-ID | PIC 9(09) | Numeric | Customer ID | |
| 10 | CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | Customer first name | |
| 10 | CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | Customer middle name | |
| 10 | CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | Customer last name | |
| 05 | CDEMO-ACCOUNT-INFO | — | Group | Current account context | |
| 10 | CDEMO-ACCT-ID | PIC 9(11) | Numeric | Account ID | |
| 10 | CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status | |
| 05 | CDEMO-CARD-INFO | — | Group | Current card context | |
| 10 | CDEMO-CARD-NUM | PIC 9(16) | Numeric | Card number | |
| 05 | CDEMO-MORE-INFO | — | Group | Additional context | |
| 10 | CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last BMS map displayed | |
| 10 | CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last BMS mapset | |

### COMEN02Y.cpy — Main Menu Options

**Record:** `CARDDEMO-MAIN-MENU-OPTIONS` | **Used by:** COMEN01C

Defines 11 menu options with OCCURS 12 array. Each entry: option number (PIC 9(02)), name (PIC X(35)), program name (PIC X(08)), user type (PIC X(01)). Options: Account View → COACTVWC, Account Update → COACTUPC, Credit Card List → COCRDLIC, Credit Card View → COCRDSLC, Credit Card Update → COCRDUPC, Transaction List → COTRN00C, Transaction View → COTRN01C, Transaction Add → COTRN02C, Transaction Reports → CORPT00C, Bill Payment → COBIL00C, Pending Authorization View → COPAUS0C.

### COADM02Y.cpy — Admin Menu Options

**Record:** `CARDDEMO-ADMIN-MENU-OPTIONS` | **Used by:** COADM01C

Defines 6 admin options with OCCURS 9 array. Options: User List → COUSR00C, User Add → COUSR01C, User Update → COUSR02C, User Delete → COUSR03C, Transaction Type List/Update (DB2) → COTRTLIC, Transaction Type Maintenance (DB2) → COTRTUPC.

### COTTL01Y.cpy — Screen Title

**Record:** `CCDA-SCREEN-TITLE` | **Used by:** All CICS programs

| Level | Field Name | PIC Clause | Value | Business Meaning |
|-------|-----------|------------|-------|-----------------|
| 05 | CCDA-TITLE01 | PIC X(40) | 'AWS Mainframe Modernization' | Title line 1 |
| 05 | CCDA-TITLE02 | PIC X(40) | 'CardDemo' | Title line 2 |
| 05 | CCDA-THANK-YOU | PIC X(40) | 'Thank you for using CCDA...' | Sign-off message |

### CSDAT01Y.cpy — Date/Time Display

Working storage for formatted date and time display on CICS screens.

### CSMSG01Y.cpy — Message Area (Short)

Working storage for single error/informational message display (PIC X(78)).

### CSMSG02Y.cpy — Message Area (Long)

Working storage for extended messages requiring multiple lines.

### CSSETATY.cpy — Screen Attribute Setting Macro

Inline COBOL paragraph used via COPY REPLACING to set BMS screen field attributes (color, protection, intensity). Used 113+ times in COACTUPC alone. Pattern: `COPY CSSETATY REPLACING ==:TAG:== BY ==fieldname==`.

### CSSTRPFY.cpy — Store PF Key Paragraph

Procedure division copybook that translates EIBAID (CICS attention identifier byte) into the CCARD-AID working storage field via EVALUATE statement. Maps DFHENTER, DFHCLEAR, DFHPA1–PA2, DFHPF1–PF24 to CCARD-AID-xxx condition names.

### CSUTLDWY.cpy — Date Validation Working Storage

Working storage fields for date validation: CCYYMMDD decomposition, century/year/month/day flags, leap year checks, LE CEEDAYS result structure.

### CSUTLDPY.cpy — Date Validation Procedure Division

Reusable procedure division paragraphs: EDIT-DATE-CCYYMMDD, EDIT-YEAR-CCYY, EDIT-MONTH, EDIT-DAY, EDIT-DATE-OF-BIRTH. Validates date ranges, leap years, future dates. Calls CSUTLDTC for LE date verification.

### CSLKPCDY.cpy — Lookup Codes

Contains validation tables: 50 US state codes, ZIP code area prefixes, NANPA phone area codes. Used by COACTUPC for customer address/phone validation.

### CODATECN.cpy — Date Conversion

**Record:** `CODATECN-REC` | **Used by:** COACTUPC, CODATE01

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | CODATECN-IN-REC | — | Group | Input record | |
| 10 | CODATECN-TYPE | PIC X | Alphanumeric | Conversion type | 88: "1" = YYYYMMDD, "2" = YYYY-MM-DD |
| 10 | CODATECN-INP-DATE | PIC X(20) | Alphanumeric | Input date | REDEFINES for both formats |
| 05 | CODATECN-OUT-REC | — | Group | Output record | |
| 10 | CODATECN-OUTTYPE | PIC X | Alphanumeric | Output type | 88: "1" = YYYY-MM-DD, "2" = YYYYMMDD |
| 10 | CODATECN-0UT-DATE | PIC X(20) | Alphanumeric | Output date | REDEFINES for both formats |
| 05 | CODATECN-ERROR-MSG | PIC X(38) | Alphanumeric | Error message | |

---

## 8. DB2 Copybooks

### CSDB2RWY.cpy — DB2 Common Working Storage

**Used by:** COTRTLIC, COTRTUPC, COBTUPDT, COPAUA0C

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 05 | WS-DB2-COMMON-VARS | — | Group | DB2 working variables | |
| 10 | WS-DISP-SQLCODE | PIC ----9 | Edited Numeric | Displayable SQLCODE | |
| 10 | WS-DUMMY-DB2-INT | PIC S9(4) COMP-3 | Packed Decimal | Priming query result | VALUE 0 |
| 10 | WS-DB2-PROCESSING-FLAG | PIC X(1) | Alphanumeric | DB2 status flag | 88: WS-DB2-OK = '0', WS-DB2-ERROR = '1' |
| 10 | WS-DB2-CURRENT-ACTION | PIC X(72) | Alphanumeric | Current DB2 action description | |
| 05 | WS-DSNTIAC-FORMATTED | — | Group | DSNTIAC message buffer | |
| 10 | WS-DSNTIAC-MESG-LEN | PIC S9(4) COMP | Binary | Message length | VALUE +720 |
| 10 | WS-DSNTIAC-FMTD-TEXT | — | Group | Formatted text | |
| 15 | WS-DSNTIAC-FMTD-TEXT-LINE | PIC X(72) | Alphanumeric | Text line | OCCURS 10 TIMES |
| 05 | WS-DSNTIAC-LRECL | PIC S9(4) COMP | Binary | Record length | VALUE +72 |

### CSDB2RPY.cpy — DB2 Common Procedures

Procedure division copybook containing: `9998-PRIMING-QUERY` (SELECT 1 FROM SYSIBM.SYSDUMMY1 connectivity test) and `9999-FORMAT-DB2-MESSAGE` (DSNTIAC error formatting utility).

---

## 9. Export Entity

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

**Record:** `EXPORT-RECORD` | **Used by:** CBEXPORT, CBIMPORT

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | EXPORT-RECORD | — | Group | Export record envelope | 500-byte sequential |
| 05 | EXPORT-REC-TYPE | PIC X(1) | Alphanumeric | Record type indicator | C=Customer, A=Account, T=Transaction, X=Xref, D=Card |
| 05 | EXPORT-TIMESTAMP | PIC X(26) | Alphanumeric | Export timestamp | REDEFINES to date + time |
| 05 | EXPORT-SEQUENCE-NUM | PIC 9(9) COMP | Binary | Sequence number | |
| 05 | EXPORT-BRANCH-ID | PIC X(4) | Alphanumeric | Branch identifier | |
| 05 | EXPORT-REGION-CODE | PIC X(5) | Alphanumeric | Region code | |
| 05 | EXPORT-RECORD-DATA | PIC X(460) | Alphanumeric | Record payload | REDEFINES for each type |

Contains REDEFINES overlays for: `EXPORT-CUSTOMER-DATA` (with COMP fields), `EXPORT-ACCOUNT-DATA` (with COMP-3 balances), `EXPORT-TRANSACTION-DATA`, `EXPORT-CARD-XREF-DATA`, `EXPORT-CARD-DATA`.

### COSTM01.CPY — Statement Transaction Layout (RECLN 350)

**Record:** `TRNX-RECORD` | **Used by:** CBSTM03A, CBSTM03B

| Level | Field Name | PIC Clause | Data Type | Business Meaning | Validation/Notes |
|-------|-----------|------------|-----------|-----------------|-----------------|
| 01 | TRNX-RECORD | — | Group | Statement transaction record | 350-byte KSDS |
| 05 | TRNX-KEY | — | Group | Compound key | |
| 10 | TRNX-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Part of compound key |
| 10 | TRNX-ID | PIC X(16) | Alphanumeric | Transaction ID | Part of compound key |
| 05 | TRNX-REST | — | Group | Transaction data | |
| 10 | TRNX-TYPE-CD | PIC X(02) | Alphanumeric | Type code | |
| 10 | TRNX-CAT-CD | PIC 9(04) | Numeric | Category code | |
| 10 | TRNX-SOURCE | PIC X(10) | Alphanumeric | Source | |
| 10 | TRNX-DESC | PIC X(100) | Alphanumeric | Description | |
| 10 | TRNX-AMT | PIC S9(09)V99 | Signed Decimal | Amount | |
| 10 | TRNX-MERCHANT-* | various | various | Merchant fields | Same as CVTRA05Y |
| 10 | TRNX-ORIG-TS | PIC X(26) | Alphanumeric | Original timestamp | |
| 10 | TRNX-PROC-TS | PIC X(26) | Alphanumeric | Processing timestamp | |

---

## 10. Other Copybooks

### UNUSED1Y.cpy — Unused Data Structure

**Record:** `UNUSED-DATA` | Identical layout to CSUSR01Y with generic field names. Not referenced by any program.

---

## Entity Relationship Summary

```
Customer (CVCUS01Y, 500B) ──1:N──► Card-XREF (CVACT03Y, 50B) ──N:1──► Account (CVACT01Y, 300B)
         PK: CUST-ID                   PK: XREF-CARD-NUM                  PK: ACCT-ID
                                              │
                                     Card (CVACT02Y, 150B)
                                       PK: CARD-NUM
                                              │
                              Transaction (CVTRA05Y, 350B)
                                PK: TRAN-ID  FK: TRAN-CARD-NUM
                                       │                │
                          TranType (CVTRA03Y, 60B)   TranCat (CVTRA04Y, 60B)
                            PK: TRAN-TYPE              PK: TYPE-CD + CAT-CD
                                       │
                          DiscGroup (CVTRA02Y, 50B)
                            PK: GROUP-ID + TYPE-CD + CAT-CD
                                       │
                          CatBalance (CVTRA01Y, 50B)
                            PK: ACCT-ID + TYPE-CD + CAT-CD

IMS Hierarchy (PAUTH database):
  Summary (CIPAUSMY) ──parent──► Detail (CIPAUDTY)
    PK: ACCT-ID                   SEQ: DATE + TIME
```
