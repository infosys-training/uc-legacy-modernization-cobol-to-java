# DATA DICTIONARY — CardDemo Copybook Analysis

## Overview

| Metric | Count |
|--------|-------|
| Total copybooks | 47 |
| Main copybooks (`app/cpy/`) | 30 |
| Authorization module copybooks | 9 |
| Transaction type module copybooks | 2 |
| BMS-generated copybooks (`app/cpy-bms/`) | 17 |
| Business entities defined | 6 core + 4 support |

---

## 1. Account Entity

### CVACT01Y.cpy — Account Master Record (RECLN 300)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| ACCT-ID | PIC 9(11) | Numeric display | 1–11 | Primary key — unique account identifier | Must be 11 digits; VSAM KSDS primary key |
| ACCT-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 12 | Account active/inactive flag | Valid values: 'Y' (active), 'N' (inactive) |
| ACCT-CURR-BAL | PIC S9(10)V99 | Signed decimal (12 bytes display) | 13–24 | Current account balance | Signed; implied 2 decimal places; overpunch encoded |
| ACCT-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 25–36 | Maximum credit limit | Must be > 0; signed decimal |
| ACCT-CASH-CREDIT-LIMIT | PIC S9(10)V99 | Signed decimal | 37–48 | Cash advance credit limit | Must be ≤ ACCT-CREDIT-LIMIT |
| ACCT-OPEN-DATE | PIC X(10) | Alphanumeric date | 49–58 | Account opening date | Format: YYYY-MM-DD |
| ACCT-EXPIRAION-DATE | PIC X(10) | Alphanumeric date | 59–68 | Account expiration date | Format: YYYY-MM-DD; must be > ACCT-OPEN-DATE |
| ACCT-REISSUE-DATE | PIC X(10) | Alphanumeric date | 69–78 | Last card reissue date | Format: YYYY-MM-DD |
| ACCT-CURR-CYC-CREDIT | PIC S9(10)V99 | Signed decimal | 79–90 | Current billing cycle credits | Running total; reset at cycle close |
| ACCT-CURR-CYC-DEBIT | PIC S9(10)V99 | Signed decimal | 91–102 | Current billing cycle debits | Running total; reset at cycle close |
| ACCT-ADDR-ZIP | PIC X(10) | Alphanumeric | 103–112 | Account holder ZIP code | Validated against CSLKPCDY ZIP prefix list |
| ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 113–122 | Disclosure/interest rate group | Links to DISCGRP (CVTRA02Y) for rate lookup |
| FILLER | PIC X(178) | Padding | 123–300 | Reserved/unused | — |

---

## 2. Customer Entity

### CVCUS01Y.cpy — Customer Master Record (RECLN 500)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| CUST-ID | PIC 9(09) | Numeric display | 1–9 | Primary key — unique customer identifier | 9 digits; VSAM KSDS primary key |
| CUST-FIRST-NAME | PIC X(25) | Alphanumeric | 10–34 | Customer first name | Required; left-justified, space-padded |
| CUST-MIDDLE-NAME | PIC X(25) | Alphanumeric | 35–59 | Customer middle name | Optional |
| CUST-LAST-NAME | PIC X(25) | Alphanumeric | 60–84 | Customer last name | Required |
| CUST-ADDR-LINE-1 | PIC X(50) | Alphanumeric | 85–134 | Street address line 1 | Required |
| CUST-ADDR-LINE-2 | PIC X(50) | Alphanumeric | 135–184 | Street address line 2 | Optional |
| CUST-ADDR-LINE-3 | PIC X(50) | Alphanumeric | 185–234 | Street address line 3 | Optional |
| CUST-ADDR-STATE-CD | PIC X(02) | Alphanumeric | 235–236 | US state code | Validated against 50 US state codes in CSLKPCDY |
| CUST-ADDR-COUNTRY-CD | PIC X(03) | Alphanumeric | 237–239 | Country code | ISO 3-letter code |
| CUST-ADDR-ZIP | PIC X(10) | Alphanumeric | 240–249 | ZIP/postal code | Format: NNNNN or NNNNN-NNNN; first 3 digits validated against CSLKPCDY |
| CUST-PHONE-NUM-1 | PIC X(15) | Alphanumeric | 250–264 | Primary phone number | Area code validated against NANPA list in CSLKPCDY |
| CUST-PHONE-NUM-2 | PIC X(15) | Alphanumeric | 265–279 | Secondary phone number | Optional; same validation as primary |
| CUST-SSN | PIC 9(09) | Numeric display | 280–288 | Social Security Number | 9 digits; format NNN-NN-NNNN validated in COACTUPC |
| CUST-GOVT-ISSUED-ID | PIC X(20) | Alphanumeric | 289–308 | Government-issued ID (passport, license) | Optional |
| CUST-DOB-YYYY-MM-DD | PIC X(10) | Alphanumeric date | 309–318 | Date of birth | Format: YYYY-MM-DD; validated via CSUTLDPY date routines |
| CUST-EFT-ACCOUNT-ID | PIC X(10) | Alphanumeric | 319–328 | Electronic Funds Transfer account ID | For auto-payment linkage |
| CUST-PRI-CARD-HOLDER-IND | PIC X(01) | Alphanumeric | 329 | Primary cardholder indicator | 'Y' = primary, 'N' = authorized user |
| CUST-FICO-CREDIT-SCORE | PIC 9(03) | Numeric display | 330–332 | FICO credit score | Range: 300–850 |
| FILLER | PIC X(168) | Padding | 333–500 | Reserved/unused | — |

---

## 3. Card Entity

### CVACT02Y.cpy — Card Master Record (RECLN 150)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| CARD-NUM | PIC X(16) | Alphanumeric | 1–16 | Card number (PAN) — primary key | 16 digits; Luhn check in COACTUPC |
| CARD-ACCT-ID | PIC 9(11) | Numeric display | 17–27 | Foreign key to Account (CVACT01Y) | Must exist in ACCTFILE |
| CARD-CVV-CD | PIC 9(03) | Numeric display | 28–30 | Card Verification Value | 3 digits |
| CARD-EMBOSSED-NAME | PIC X(50) | Alphanumeric | 31–80 | Name embossed on card | Max 50 chars |
| CARD-EXPIRAION-DATE | PIC X(10) | Alphanumeric date | 81–90 | Card expiration date | Format: YYYY-MM-DD; must be future date |
| CARD-ACTIVE-STATUS | PIC X(01) | Alphanumeric | 91 | Card active/inactive flag | 'Y' = active, 'N' = inactive |
| FILLER | PIC X(59) | Padding | 92–150 | Reserved/unused | — |

### CVACT03Y.cpy — Card–Account Cross-Reference (RECLN 50)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| XREF-CARD-NUM | PIC X(16) | Alphanumeric | 1–16 | Card number — VSAM KSDS primary key | Must exist in CARDFILE |
| XREF-CUST-ID | PIC 9(09) | Numeric display | 17–25 | Customer ID foreign key | Must exist in CUSTFILE |
| XREF-ACCT-ID | PIC 9(11) | Numeric display | 26–36 | Account ID foreign key | Must exist in ACCTFILE |
| FILLER | PIC X(14) | Padding | 37–50 | Reserved/unused | — |

---

## 4. Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| TRAN-ID | PIC X(16) | Alphanumeric | 1–16 | Transaction ID — primary key | System-generated unique ID |
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 17–18 | Transaction type code | Must exist in TRANTYPE file (CVTRA03Y) |
| TRAN-CAT-CD | PIC 9(04) | Numeric display | 19–22 | Transaction category code | Must exist in TRANCATG file (CVTRA04Y) |
| TRAN-SOURCE | PIC X(10) | Alphanumeric | 23–32 | Origination source (POS, ATM, WEB, etc.) | Freeform |
| TRAN-DESC | PIC X(100) | Alphanumeric | 33–132 | Transaction description | Freeform text |
| TRAN-AMT | PIC S9(09)V99 | Signed decimal (11 bytes) | 133–143 | Transaction amount | Signed; credits positive, debits negative |
| TRAN-MERCHANT-ID | PIC 9(09) | Numeric display | 144–152 | Merchant identifier | Numeric |
| TRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 153–202 | Merchant name | Freeform |
| TRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 203–252 | Merchant city | Freeform |
| TRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 253–262 | Merchant ZIP code | Standard ZIP format |
| TRAN-CARD-NUM | PIC X(16) | Alphanumeric | 263–278 | Card number used | Must exist in CARDXREF |
| TRAN-ORIG-TS | PIC X(26) | Alphanumeric timestamp | 279–304 | Transaction origination timestamp | Format: YYYY-MM-DD-HH.MM.SS.MMMMMM |
| TRAN-PROC-TS | PIC X(26) | Alphanumeric timestamp | 305–330 | Transaction processing timestamp | Set by batch posting (CBTRN02C) |
| FILLER | PIC X(20) | Padding | 331–350 | Reserved/unused | — |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| DALYTRAN-ID | PIC X(16) | Alphanumeric | 1–16 | Daily transaction ID | System-generated |
| DALYTRAN-TYPE-CD | PIC X(02) | Alphanumeric | 17–18 | Transaction type code | Same rules as TRAN-TYPE-CD |
| DALYTRAN-CAT-CD | PIC 9(04) | Numeric display | 19–22 | Category code | Same rules as TRAN-CAT-CD |
| DALYTRAN-SOURCE | PIC X(10) | Alphanumeric | 23–32 | Origination source | Same as TRAN-SOURCE |
| DALYTRAN-DESC | PIC X(100) | Alphanumeric | 33–132 | Description | Freeform |
| DALYTRAN-AMT | PIC S9(09)V99 | Signed decimal | 133–143 | Amount | Signed decimal |
| DALYTRAN-MERCHANT-ID | PIC 9(09) | Numeric | 144–152 | Merchant ID | Numeric |
| DALYTRAN-MERCHANT-NAME | PIC X(50) | Alphanumeric | 153–202 | Merchant name | Freeform |
| DALYTRAN-MERCHANT-CITY | PIC X(50) | Alphanumeric | 203–252 | Merchant city | Freeform |
| DALYTRAN-MERCHANT-ZIP | PIC X(10) | Alphanumeric | 253–262 | Merchant ZIP | Standard |
| DALYTRAN-CARD-NUM | PIC X(16) | Alphanumeric | 263–278 | Card number | Must exist in CARDXREF |
| DALYTRAN-ORIG-TS | PIC X(26) | Alphanumeric | 279–304 | Origination timestamp | ISO-like format |
| DALYTRAN-PROC-TS | PIC X(26) | Alphanumeric | 305–330 | Processing timestamp | Set on posting |
| FILLER | PIC X(20) | Padding | 331–350 | Reserved | — |

---

## 5. Transaction Reference Data

### CVTRA01Y.cpy — Transaction Category Balance (RECLN 50)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| TRANCAT-ACCT-ID | PIC 9(11) | Numeric display | 1–11 | Account ID (composite key part 1) | FK to ACCTFILE |
| TRANCAT-TYPE-CD | PIC X(02) | Alphanumeric | 12–13 | Transaction type (composite key part 2) | FK to TRANTYPE |
| TRANCAT-CD | PIC 9(04) | Numeric display | 14–17 | Category code (composite key part 3) | FK to TRANCATG |
| TRAN-CAT-BAL | PIC S9(09)V99 | Signed decimal | 18–28 | Running balance for this category | Updated by CBACT04C |
| FILLER | PIC X(22) | Padding | 29–50 | Reserved | — |

### CVTRA02Y.cpy — Disclosure Group / Interest Rate (RECLN 50)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| DIS-ACCT-GROUP-ID | PIC X(10) | Alphanumeric | 1–10 | Account group ID (composite key part 1) | Links to ACCT-GROUP-ID |
| DIS-TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 11–12 | Transaction type (composite key part 2) | |
| DIS-TRAN-CAT-CD | PIC 9(04) | Numeric display | 13–16 | Category code (composite key part 3) | |
| DIS-INT-RATE | PIC S9(04)V99 | Signed decimal (6 bytes) | 17–22 | Annual interest rate percentage | e.g., 018.99 = 18.99% APR |
| FILLER | PIC X(28) | Padding | 23–50 | Reserved | — |

### CVTRA03Y.cpy — Transaction Type (RECLN 60)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| TRAN-TYPE | PIC X(02) | Alphanumeric | 1–2 | Transaction type code — primary key | e.g., 'PR'=Purchase, 'CR'=Credit, 'CA'=Cash advance |
| TRAN-TYPE-DESC | PIC X(50) | Alphanumeric | 3–52 | Type description | Human-readable label |
| FILLER | PIC X(08) | Padding | 53–60 | Reserved | — |

### CVTRA04Y.cpy — Transaction Category (RECLN 60)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| TRAN-TYPE-CD | PIC X(02) | Alphanumeric | 1–2 | Transaction type (composite key part 1) | FK to CVTRA03Y |
| TRAN-CAT-CD | PIC 9(04) | Numeric display | 3–6 | Category code (composite key part 2) | e.g., 5011=Electronics, 5411=Grocery |
| TRAN-CAT-TYPE-DESC | PIC X(50) | Alphanumeric | 7–56 | Category description | MCC-style classification |
| FILLER | PIC X(04) | Padding | 57–60 | Reserved | — |

### CVTRA07Y.cpy — Transaction Report Layout

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| REPT-SHORT-NAME | PIC X(38) | Alphanumeric | Report short identifier ('DALYREPT') |
| REPT-LONG-NAME | PIC X(41) | Alphanumeric | Report title ('Daily Transaction Report') |
| REPT-DATE-HEADER | PIC X(12) | Alphanumeric | Date range header label |
| REPT-START-DATE | PIC X(10) | Date | Report period start |
| REPT-END-DATE | PIC X(10) | Date | Report period end |
| TRAN-REPORT-TRANS-ID | PIC X(16) | Alphanumeric | Transaction ID column |
| TRAN-REPORT-ACCOUNT-ID | PIC X(11) | Alphanumeric | Account ID column |
| TRAN-REPORT-TYPE-CD | PIC X(02) | Alphanumeric | Type code column |
| TRAN-REPORT-TYPE-DESC | PIC X(15) | Alphanumeric | Type description |
| TRAN-REPORT-CAT-CD | PIC 9(04) | Numeric | Category code |
| TRAN-REPORT-CAT-DESC | PIC X(29) | Alphanumeric | Category description |
| TRAN-REPORT-SOURCE | PIC X(10) | Alphanumeric | Source |
| TRAN-REPORT-AMT | PIC -ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Formatted amount |
| REPT-PAGE-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Page subtotal |
| REPT-ACCOUNT-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Account subtotal |
| REPT-GRAND-TOTAL | PIC +ZZZ,ZZZ,ZZZ.ZZ | Edited numeric | Grand total |

---

## 6. Security / User Entity

### CSUSR01Y.cpy — User Security Record (RECLN 80)

| Field Name | PIC Clause | Data Type | Byte Position | Business Meaning | Validation Rules |
|------------|-----------|-----------|---------------|-----------------|-----------------|
| SEC-USR-ID | PIC X(08) | Alphanumeric | 1–8 | User login ID — primary key | Unique; uppercase |
| SEC-USR-FNAME | PIC X(20) | Alphanumeric | 9–28 | User first name | Required |
| SEC-USR-LNAME | PIC X(20) | Alphanumeric | 29–48 | User last name | Required |
| SEC-USR-PWD | PIC X(08) | Alphanumeric | 49–56 | Password (⚠️ PLAIN TEXT) | 8 chars max; **SECURITY RISK** — must hash in Java |
| SEC-USR-TYPE | PIC X(01) | Alphanumeric | 57 | User type | 'A' = Admin, 'U' = Regular user |
| SEC-USR-FILLER | PIC X(23) | Padding | 58–80 | Reserved | — |

---

## 7. Authorization Entity (IMS Segments)

### CIPAUSMY.cpy — Pending Authorization Summary (IMS Root Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| PA-ACCT-ID | PIC S9(11) COMP-3 | Packed decimal (6 bytes) | Account ID — IMS root key | Links to ACCTFILE |
| PA-CUST-ID | PIC 9(09) | Numeric display | Customer ID | Links to CUSTFILE |
| PA-AUTH-STATUS | PIC X(01) | Alphanumeric | Authorization status | Active/Closed |
| PA-ACCOUNT-STATUS | PIC X(02) OCCURS 5 | Array | Account status codes (up to 5) | Status tracking array |
| PA-CREDIT-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal (6 bytes) | Credit limit snapshot | Cached from account |
| PA-CASH-LIMIT | PIC S9(09)V99 COMP-3 | Packed decimal | Cash advance limit | Cached |
| PA-CREDIT-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Outstanding credit balance | Running total |
| PA-CASH-BALANCE | PIC S9(09)V99 COMP-3 | Packed decimal | Outstanding cash balance | Running total |
| PA-APPROVED-AUTH-CNT | PIC S9(04) COMP | Binary (2 bytes) | Count of approved auths | Incremented on approval |
| PA-DECLINED-AUTH-CNT | PIC S9(04) COMP | Binary (2 bytes) | Count of declined auths | Incremented on decline |
| PA-APPROVED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total approved amount | Sum |
| PA-DECLINED-AUTH-AMT | PIC S9(09)V99 COMP-3 | Packed decimal | Total declined amount | Sum |
| FILLER | PIC X(34) | Padding | Reserved | — |

### CIPAUDTY.cpy — Pending Authorization Detail (IMS Child Segment)

| Field Name | PIC Clause | Data Type | Business Meaning | Validation Rules |
|------------|-----------|-----------|-----------------|-----------------|
| PA-AUTH-DATE-9C | PIC S9(05) COMP-3 | Packed decimal (3 bytes) | Auth date (compressed key) | Part of IMS segment key |
| PA-AUTH-TIME-9C | PIC S9(09) COMP-3 | Packed decimal (5 bytes) | Auth time (compressed key) | Part of IMS segment key |
| PA-AUTH-ORIG-DATE | PIC X(06) | Alphanumeric | Original auth date | Format: MMDDYY |
| PA-AUTH-ORIG-TIME | PIC X(06) | Alphanumeric | Original auth time | Format: HHMMSS |
| PA-CARD-NUM | PIC X(16) | Alphanumeric | Card number | Links to CARDXREF |
| PA-AUTH-TYPE | PIC X(04) | Alphanumeric | Authorization type | e.g., 'SALE', 'CASH', 'RFND' |
| PA-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry (MMYY) | Must match card file |
| PA-MESSAGE-TYPE | PIC X(06) | Alphanumeric | Message type from network | ISO 8583 MTI |
| PA-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Network source | POS/ATM/ECOM |
| PA-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Authorization ID code | Approval code returned |
| PA-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code | '00' = Approved; 88-level |
| PA-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Decline reason code | e.g., 'CRLM', 'EXPR' |
| PA-PROCESSING-CODE | PIC 9(06) | Numeric | ISO processing code | 6-digit |
| PA-TRANSACTION-AMT | PIC S9(10)V99 COMP-3 | Packed decimal (7 bytes) | Requested transaction amount | Signed |
| PA-APPROVED-AMT | PIC S9(10)V99 COMP-3 | Packed decimal | Approved amount | May differ from requested (partial auth) |
| PA-MERCHANT-CATAGORY-CODE | PIC X(04) | Alphanumeric | Merchant category code (MCC) | ISO 18245 |
| PA-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country code | ISO 3166 |
| PA-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode | ISO 8583 field 22 |
| PA-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID | Acquirer-assigned |
| PA-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name | Truncated |
| PA-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city | Truncated |
| PA-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state | US state code |
| PA-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP | 5 or 9 digit |
| PA-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference ID | Unique per transaction |
| PA-MATCH-STATUS | PIC X(01) | Alphanumeric | Match status | 'P'=Pending, 'D'=Declined, 'E'=Expired, 'M'=Matched |
| PA-AUTH-FRAUD | PIC X(01) | Alphanumeric | Fraud flag | 'F'=Confirmed fraud, 'R'=Removed |
| PA-FRAUD-RPT-DATE | PIC X(08) | Alphanumeric date | Date fraud was reported | YYYYMMDD |
| FILLER | PIC X(17) | Padding | Reserved | — |

---

## 8. Authorization MQ Messages

### CCPAURQY.cpy — Authorization Request Message

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| PA-RQ-AUTH-DATE | PIC X(06) | Alphanumeric | Request date (MMDDYY) |
| PA-RQ-AUTH-TIME | PIC X(06) | Alphanumeric | Request time (HHMMSS) |
| PA-RQ-CARD-NUM | PIC X(16) | Alphanumeric | Card number |
| PA-RQ-AUTH-TYPE | PIC X(04) | Alphanumeric | Auth type |
| PA-RQ-CARD-EXPIRY-DATE | PIC X(04) | Alphanumeric | Card expiry (MMYY) |
| PA-RQ-MESSAGE-TYPE | PIC X(06) | Alphanumeric | ISO message type |
| PA-RQ-MESSAGE-SOURCE | PIC X(06) | Alphanumeric | Source channel |
| PA-RQ-PROCESSING-CODE | PIC 9(06) | Numeric | Processing code |
| PA-RQ-TRANSACTION-AMT | PIC S9(10)V99 | Signed decimal | Requested amount |
| PA-RQ-MERCHANT-CAT-CODE | PIC X(04) | Alphanumeric | Merchant category |
| PA-RQ-ACQR-COUNTRY-CODE | PIC X(03) | Alphanumeric | Acquirer country |
| PA-RQ-POS-ENTRY-MODE | PIC 9(02) | Numeric | POS entry mode |
| PA-RQ-MERCHANT-ID | PIC X(15) | Alphanumeric | Merchant ID |
| PA-RQ-MERCHANT-NAME | PIC X(22) | Alphanumeric | Merchant name |
| PA-RQ-MERCHANT-CITY | PIC X(13) | Alphanumeric | Merchant city |
| PA-RQ-MERCHANT-STATE | PIC X(02) | Alphanumeric | Merchant state |
| PA-RQ-MERCHANT-ZIP | PIC X(09) | Alphanumeric | Merchant ZIP |

### CCPAURLY.cpy — Authorization Response Message

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| PA-RL-CARD-NUM | PIC X(16) | Alphanumeric | Card number (echo) |
| PA-RL-TRANSACTION-ID | PIC X(15) | Alphanumeric | Transaction reference |
| PA-RL-AUTH-ID-CODE | PIC X(06) | Alphanumeric | Approval code |
| PA-RL-AUTH-RESP-CODE | PIC X(02) | Alphanumeric | Response code |
| PA-RL-AUTH-RESP-REASON | PIC X(04) | Alphanumeric | Reason code |
| PA-RL-APPROVED-AMT | PIC +9(10).99 | Edited numeric | Approved amount |

### CCPAUERY.cpy — Authorization Error Log

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| ERR-DATE | PIC X(06) | Alphanumeric | Error date |
| ERR-TIME | PIC X(06) | Alphanumeric | Error time |
| ERR-APPLICATION | PIC X(08) | Alphanumeric | Application name |
| ERR-PROGRAM | PIC X(08) | Alphanumeric | Program name |
| ERR-LOCATION | PIC X(04) | Alphanumeric | Error location (paragraph) |
| ERR-LEVEL | PIC X(01) | Alphanumeric | Severity: 'L'=Log, 'I'=Info, 'W'=Warning, 'C'=Critical |
| ERR-SUBSYSTEM | PIC X(01) | Alphanumeric | Subsystem: 'A'=App, 'C'=CICS, 'I'=IMS, 'D'=DB2, 'M'=MQ, 'F'=File |
| ERR-CODE-1 | PIC X(09) | Alphanumeric | Primary error code |
| ERR-CODE-2 | PIC X(09) | Alphanumeric | Secondary error code |
| ERR-MESSAGE | PIC X(50) | Alphanumeric | Error message text |
| ERR-EVENT-KEY | PIC X(20) | Alphanumeric | Event correlation key |

---

## 9. IMS PCB Structures

### PADFLPCB.CPY — Auth Detail File PCB

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| PADFL-DBDNAME | PIC X(08) | DBD name |
| PADFL-SEG-LEVEL | PIC X(02) | Segment level |
| PADFL-PCB-STATUS | PIC X(02) | DL/I status code |
| PADFL-PCB-PROCOPT | PIC X(04) | Processing options |
| PADFL-SEG-NAME | PIC X(08) | Segment name |
| PADFL-KEYFB | PIC X(255) | Key feedback area |

### PASFLPCB.CPY — Auth Summary File PCB

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| PASFL-DBDNAME | PIC X(08) | DBD name |
| PASFL-PCB-STATUS | PIC X(02) | DL/I status code |
| PASFL-KEYFB | PIC X(100) | Key feedback area |

### PAUTBPCB.CPY — Auth Database PCB

| Field Name | PIC Clause | Business Meaning |
|------------|-----------|-----------------|
| PAUT-DBDNAME | PIC X(08) | DBD name |
| PAUT-PCB-STATUS | PIC X(02) | DL/I status code |
| PAUT-KEYFB | PIC X(255) | Key feedback area |

### IMSFUNCS.cpy — IMS DL/I Function Codes

| Field Name | PIC Clause | Value | Business Meaning |
|------------|-----------|-------|-----------------|
| FUNC-GU | PIC X(04) | 'GU  ' | Get Unique (random access) |
| FUNC-GHU | PIC X(04) | 'GHU ' | Get Hold Unique (for update) |
| FUNC-GN | PIC X(04) | 'GN  ' | Get Next (sequential) |
| FUNC-GHN | PIC X(04) | 'GHN ' | Get Hold Next |
| FUNC-GNP | PIC X(04) | 'GNP ' | Get Next within Parent |
| FUNC-GHNP | PIC X(04) | 'GHNP' | Get Hold Next within Parent |
| FUNC-REPL | PIC X(04) | 'REPL' | Replace (update) |
| FUNC-ISRT | PIC X(04) | 'ISRT' | Insert |
| FUNC-DLET | PIC X(04) | 'DLET' | Delete |

---

## 10. Application Infrastructure Copybooks

### COCOM01Y.cpy — CICS Communication Area (COMMAREA)

| Field Name | PIC Clause | Data Type | Business Meaning |
|------------|-----------|-----------|-----------------|
| CDEMO-FROM-TRANID | PIC X(04) | Alphanumeric | Source CICS transaction ID |
| CDEMO-FROM-PROGRAM | PIC X(08) | Alphanumeric | Source program name |
| CDEMO-TO-TRANID | PIC X(04) | Alphanumeric | Target transaction ID |
| CDEMO-TO-PROGRAM | PIC X(08) | Alphanumeric | Target program name |
| CDEMO-USER-ID | PIC X(08) | Alphanumeric | Logged-in user ID |
| CDEMO-USER-TYPE | PIC X(01) | Alphanumeric | 'A'=Admin, 'U'=User (88-level) |
| CDEMO-PGM-CONTEXT | PIC 9(01) | Numeric | 0=Enter, 1=Re-enter (88-level) |
| CDEMO-CUST-ID | PIC 9(09) | Numeric | Customer context |
| CDEMO-CUST-FNAME | PIC X(25) | Alphanumeric | Customer first name |
| CDEMO-CUST-MNAME | PIC X(25) | Alphanumeric | Customer middle name |
| CDEMO-CUST-LNAME | PIC X(25) | Alphanumeric | Customer last name |
| CDEMO-ACCT-ID | PIC 9(11) | Numeric | Account context |
| CDEMO-ACCT-STATUS | PIC X(01) | Alphanumeric | Account status |
| CDEMO-CARD-NUM | PIC 9(16) | Numeric | Card number context |
| CDEMO-LAST-MAP | PIC X(7) | Alphanumeric | Last BMS map displayed |
| CDEMO-LAST-MAPSET | PIC X(7) | Alphanumeric | Last BMS mapset |

### COADM02Y.cpy — Admin Menu Options

Defines the admin navigation table with 6 options routing to: COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC.

### COMEN02Y.cpy — Main Menu Options

Defines the user navigation table with 11 options routing to: COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C.

### CSDAT01Y.cpy — Date/Time Working Storage

Provides formatted date structures: WS-CURDATE (YYYYMMDD), WS-CURDATE-MM-DD-YY, WS-CURTIME-HH-MM-SS, WS-TIMESTAMP (YYYY-MM-DD HH:MM:SS.MMMMMM).

### COTTL01Y.cpy — Screen Title Constants

Defines application titles: 'AWS Mainframe Modernization' and 'CardDemo'.

### CSMSG01Y.cpy — Common Messages

Standard messages: 'Thank you for using CardDemo application...' and 'Invalid key pressed...'.

### CSMSG02Y.cpy — Abend Data Structure

Fields: ABEND-CODE PIC X(4), ABEND-CULPRIT PIC X(8), ABEND-REASON PIC X(50), ABEND-MSG PIC X(72).

### CSSETATY.cpy — Screen Attribute Setting (Used with COPY REPLACING)

Macro copybook used 3× in COACTUPC with COPY REPLACING to set BMS field attributes (protected, unprotected, highlighted) for different screen states.

### CSLKPCDY.cpy — Validation Lookup Data

Contains 88-level condition names for:
- **US State codes** (50 values: AL, AK, AZ, ..., WY)
- **ZIP code prefixes** (3-digit area codes mapped to ranges)
- **Phone area codes** (full NANPA list: 201–999 valid codes)
- **General purpose codes** (201–999 valid ranges)

### CSUTLDWY.cpy / CSUTLDPY.cpy — Date Validation Utilities

Working storage and procedure division copybooks for date validation using LE CEEDAYS function.

### CODATECN.cpy — Date Format Conversion Record

Used by CBACT01C to format dates via assembler routine COBDATFT.

### CVEXPORT.cpy — Export File Record Layout

Defines the combined export record structure used by CBEXPORT/CBIMPORT programs.

### CUSTREC.cpy — Alternative Customer Record

Alternative/legacy customer record layout.

### COSTM01.CPY — Statement Generation Constants

Constants and working storage for statement generation (CBSTM03A).

### UNUSED1Y.cpy — Unused/Reserved

Empty or placeholder copybook.

---

## 11. DB2 Module Copybooks

### CSDB2RPY.cpy — DB2 Common Procedures

Contains DB2 connectivity verification (priming query: `SELECT 1 FROM SYSIBM.SYSDUMMY1`) and DSNTIAC error formatting utility call.

### CSDB2RWY.cpy — DB2 Working Storage

DB2-specific working storage variables for SQLCODE handling and message formatting.

---

## 12. Entity Relationship Summary

```
┌──────────────────┐         ┌──────────────────┐
│  CUSTOMER        │ 1    N  │  CARD-XREF       │
│  (CVCUS01Y)      │────────►│  (CVACT03Y)      │
│  PK: CUST-ID     │         │  PK: XREF-CARD-NUM│
└──────────────────┘         │  FK: XREF-CUST-ID │
                             │  FK: XREF-ACCT-ID │
                             └────────┬─────────┘
                                      │ N:1
                             ┌────────▼─────────┐
                             │  ACCOUNT          │
                             │  (CVACT01Y)       │
                             │  PK: ACCT-ID      │
                             │  FK: ACCT-GROUP-ID │──► DISCGRP (CVTRA02Y)
                             └────────┬─────────┘
                                      │ 1:N
                             ┌────────▼─────────┐
                             │  CARD             │
                             │  (CVACT02Y)       │
                             │  PK: CARD-NUM     │
                             │  FK: CARD-ACCT-ID │
                             └────────┬─────────┘
                                      │ 1:N
                             ┌────────▼─────────┐
                             │  TRANSACTION      │
                             │  (CVTRA05Y)       │
                             │  PK: TRAN-ID      │
                             │  FK: TRAN-CARD-NUM│
                             │  FK: TRAN-TYPE-CD │──► TRAN-TYPE (CVTRA03Y)
                             │  FK: TRAN-CAT-CD  │──► TRAN-CAT (CVTRA04Y)
                             └──────────────────┘

┌──────────────────┐
│  AUTH SUMMARY    │ (IMS Root)
│  (CIPAUSMY)      │
│  Key: PA-ACCT-ID │
│       1:N        │
│  ┌────────────┐  │
│  │ AUTH DETAIL │  │ (IMS Child)
│  │ (CIPAUDTY) │  │
│  │ Key: DATE+ │  │
│  │      TIME  │  │
│  └────────────┘  │
└──────────────────┘
```
