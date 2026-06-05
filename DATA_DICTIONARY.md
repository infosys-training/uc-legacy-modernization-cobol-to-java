# Data Dictionary — CardDemo COBOL Estate

> **Generated:** 2026-06-05 | **Application:** CardDemo — Credit Card Management System  
> **Repository:** infosys-training/uc-legacy-modernization-cobol-to-java

---

## Summary

| Metric | Count |
|--------|-------|
| Total Copybooks | 47 |
| Main Copybooks (`app/cpy/`) | 27 |
| Sub-App Copybooks (authorization) | 10 |
| Sub-App Copybooks (transaction-type DB2) | 2 |
| BMS-generated Copybooks (`app/cpy-bms/`) | 17 |
| Core Business Entities | 7 |

---

## Entity Relationship Overview

```
Customer (CVCUS01Y) ──1:N──► Card-XREF (CVACT03Y) ──N:1──► Account (CVACT01Y)
                                    │
                              Card (CVACT02Y)
                                    │
                         Transaction (CVTRA05Y) ──► TranType (CVTRA03Y)
                                                    ──► TranCat (CVTRA04Y)

Auth-Summary (CIPAUSMY) ──1:N──► Auth-Detail (CIPAUDTY)
```

---

## 1. Account Entity

### `CVACT01Y.cpy` — Account Master Record

**Record Size:** 300 bytes | **VSAM:** KSDS, Key = ACCT-ID (11 bytes, offset 0)  
**Used by:** CBACT01C, CBACT04C, CBTRN02C, CBSTM03A, COACTUPC, COACTVWC, COCRDUPC, COCRDSLC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `ACCT-ID` | `9(11)` | Numeric display | 11 | **Primary key** — unique account identifier | Must be numeric, non-zero |
| `ACCT-ACTIVE-STATUS` | `X(01)` | Alphanumeric | 1 | Account active flag | `'Y'` = active, `'N'` = inactive |
| `ACCT-CURR-BAL` | `S9(10)V99` | Signed decimal | 12 | Current account balance | Signed; overpunch-encoded in display format |
| `ACCT-CREDIT-LIMIT` | `S9(10)V99` | Signed decimal | 12 | Maximum credit limit | Must be ≥ 0 |
| `ACCT-CASH-CREDIT-LIMIT` | `S9(10)V99` | Signed decimal | 12 | Cash advance credit limit | Must be ≥ 0 |
| `ACCT-OPEN-DATE` | `X(10)` | Alphanumeric | 10 | Account open date | Format: `YYYY-MM-DD` |
| `ACCT-EXPIRAION-DATE` | `X(10)` | Alphanumeric | 10 | Account expiration date | Format: `YYYY-MM-DD`; must be > open date |
| `ACCT-REISSUE-DATE` | `X(10)` | Alphanumeric | 10 | Last card reissue date | Format: `YYYY-MM-DD` |
| `ACCT-CURR-CYC-CREDIT` | `S9(10)V99` | Signed decimal | 12 | Current cycle credit total | Accumulated credits this cycle |
| `ACCT-CURR-CYC-DEBIT` | `S9(10)V99` | Signed decimal | 12 | Current cycle debit total | Accumulated debits this cycle |
| `ACCT-ADDR-ZIP` | `X(10)` | Alphanumeric | 10 | Account holder ZIP code | Validated against CSLKPCDY ZIP prefixes |
| `ACCT-GROUP-ID` | `X(10)` | Alphanumeric | 10 | Disclosure/interest rate group | Links to DISCGRP file |
| `FILLER` | `X(178)` | Filler | 178 | Reserved space | — |

---

## 2. Customer Entity

### `CVCUS01Y.cpy` — Customer Master Record

**Record Size:** 500 bytes | **VSAM:** KSDS, Key = CUST-ID (9 bytes, offset 0)  
**Used by:** CBCUS01C, CBSTM03A, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `CUST-ID` | `9(09)` | Numeric display | 9 | **Primary key** — unique customer identifier | Must be numeric |
| `CUST-FIRST-NAME` | `X(25)` | Alphanumeric | 25 | Customer first name | Alpha characters only (validated in COACTUPC) |
| `CUST-MIDDLE-NAME` | `X(25)` | Alphanumeric | 25 | Customer middle name | Optional; alpha only |
| `CUST-LAST-NAME` | `X(25)` | Alphanumeric | 25 | Customer last name | Alpha characters only |
| `CUST-ADDR-LINE-1` | `X(50)` | Alphanumeric | 50 | Street address line 1 | Alphanumeric |
| `CUST-ADDR-LINE-2` | `X(50)` | Alphanumeric | 50 | Street address line 2 | Optional |
| `CUST-ADDR-LINE-3` | `X(50)` | Alphanumeric | 50 | Street address line 3 | Optional |
| `CUST-ADDR-STATE-CD` | `X(02)` | Alphanumeric | 2 | US state code | Validated against 50+ state codes in CSLKPCDY |
| `CUST-ADDR-COUNTRY-CD` | `X(03)` | Alphanumeric | 3 | Country code | — |
| `CUST-ADDR-ZIP` | `X(10)` | Alphanumeric | 10 | ZIP / postal code | ZIP prefix validated against CSLKPCDY |
| `CUST-PHONE-NUM-1` | `X(15)` | Alphanumeric | 15 | Primary phone number | Format: `(NPA)NXX-XXXX`; area code validated against NANPA list in CSLKPCDY |
| `CUST-PHONE-NUM-2` | `X(15)` | Alphanumeric | 15 | Secondary phone number | Same format validation |
| `CUST-SSN` | `9(09)` | Numeric display | 9 | Social Security Number | 9 digits; validated for format in COACTUPC |
| `CUST-GOVT-ISSUED-ID` | `X(20)` | Alphanumeric | 20 | Government-issued ID | — |
| `CUST-DOB-YYYY-MM-DD` | `X(10)` | Alphanumeric | 10 | Date of birth | Format: `YYYY-MM-DD` |
| `CUST-EFT-ACCOUNT-ID` | `X(10)` | Alphanumeric | 10 | Electronic funds transfer account | — |
| `CUST-PRI-CARD-HOLDER-IND` | `X(01)` | Alphanumeric | 1 | Primary cardholder indicator | `'Y'` / `'N'` |
| `CUST-FICO-CREDIT-SCORE` | `9(03)` | Numeric display | 3 | FICO credit score | Range: 300–850 |
| `FILLER` | `X(168)` | Filler | 168 | Reserved space | — |

### `CUSTREC.cpy` — Customer Record (Statement Generation)

**Used by:** CBSTM03A, CBEXPORT, CBIMPORT

Simplified customer record layout used during statement generation, containing core fields mapped from CVCUS01Y for report formatting.

---

## 3. Card Entity

### `CVACT02Y.cpy` — Card Data Record

**Record Size:** 150 bytes | **VSAM:** KSDS, Key = CARD-NUM (16 bytes, offset 0)  
**Used by:** CBACT02C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `CARD-NUM` | `X(16)` | Alphanumeric | 16 | **Primary key** — credit card number | 16-digit card number |
| `CARD-ACCT-ID` | `9(11)` | Numeric display | 11 | **FK** → Account (ACCT-ID) | Must match existing account |
| `CARD-CVV-CD` | `9(03)` | Numeric display | 3 | Card verification value | 3-digit numeric |
| `CARD-EMBOSSED-NAME` | `X(50)` | Alphanumeric | 50 | Name embossed on card | — |
| `CARD-EXPIRAION-DATE` | `X(10)` | Alphanumeric | 10 | Card expiration date | Format: `YYYY-MM-DD` |
| `CARD-ACTIVE-STATUS` | `X(01)` | Alphanumeric | 1 | Card active flag | `'Y'` = active, `'N'` = inactive |
| `FILLER` | `X(59)` | Filler | 59 | Reserved space | — |

---

## 4. Card–Account Cross-Reference Entity

### `CVACT03Y.cpy` — Cross-Reference Record

**Record Size:** 50 bytes | **VSAM:** KSDS, Key = XREF-CARD-NUM (16 bytes, offset 0); AIX on XREF-ACCT-ID  
**Used by:** CBACT03C, CBACT04C, CBTRN02C, CBTRN03C, CBSTM03A, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `XREF-CARD-NUM` | `X(16)` | Alphanumeric | 16 | **Primary key** — card number | Must match a CARD-NUM in CVACT02Y |
| `XREF-CUST-ID` | `9(09)` | Numeric display | 9 | **FK** → Customer (CUST-ID) | Must match existing customer |
| `XREF-ACCT-ID` | `9(11)` | Numeric display | 11 | **FK** → Account (ACCT-ID) | Must match existing account |
| `FILLER` | `X(14)` | Filler | 14 | Reserved space | — |

---

## 5. Transaction Entity

### `CVTRA05Y.cpy` — Transaction Record

**Record Size:** 350 bytes | **VSAM:** KSDS, Key = TRAN-ID (16 bytes, offset 0)  
**Used by:** CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, CORPT00C, CBEXPORT, CBIMPORT

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `TRAN-ID` | `X(16)` | Alphanumeric | 16 | **Primary key** — unique transaction ID | System-generated |
| `TRAN-TYPE-CD` | `X(02)` | Alphanumeric | 2 | Transaction type code | Must match TRAN-TYPE in CVTRA03Y |
| `TRAN-CAT-CD` | `9(04)` | Numeric display | 4 | Transaction category code | Must match TRAN-CAT in CVTRA04Y |
| `TRAN-SOURCE` | `X(10)` | Alphanumeric | 10 | Transaction source identifier | — |
| `TRAN-DESC` | `X(100)` | Alphanumeric | 100 | Transaction description | Free text |
| `TRAN-AMT` | `S9(09)V99` | Signed decimal | 11 | Transaction amount | Positive for debits, negative for credits |
| `TRAN-CARD-NUM` | `X(16)` | Alphanumeric | 16 | Card number used | Must match valid card |
| `TRAN-MERCHANT-ID` | `X(09)` | Alphanumeric | 9 | Merchant identifier | — |
| `TRAN-MERCHANT-NAME` | `X(50)` | Alphanumeric | 50 | Merchant name | — |
| `TRAN-MERCHANT-CITY` | `X(20)` | Alphanumeric | 20 | Merchant city | — |
| `TRAN-MERCHANT-ZIP` | `X(10)` | Alphanumeric | 10 | Merchant ZIP code | — |
| `TRAN-ORIG-TS` | `X(26)` | Alphanumeric | 26 | Transaction timestamp | ISO timestamp format |
| `TRAN-PROC-TS` | `X(26)` | Alphanumeric | 26 | Processing timestamp | ISO timestamp format |
| `FILLER` | `X(20)` | Filler | 20 | Reserved space | — |

### `CVTRA06Y.cpy` — Daily Transaction Input Record

**Used by:** CBTRN01C, CBTRN02C

Daily transaction file input layout — simplified record for daily batch input before posting to the master transaction file. Contains card number, amount, type, and originating details.

### `CVTRA07Y.cpy` — Transaction Report Line Record

**Used by:** CBTRN03C

Report output formatting record — defines the print layout for the daily transaction report with column headers and formatted data lines (73 bytes).

---

## 6. Transaction Reference Data

### `CVTRA03Y.cpy` — Transaction Type Record

**Record Size:** Variable | **VSAM:** KSDS  
**Used by:** CBTRN03C

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `TRAN-TYPE` | `X(02)` | Alphanumeric | 2 | **Primary key** — type code (e.g., `'SA'`, `'PU'`) | — |
| `TRAN-TYPE-DESC` | `X(50)` | Alphanumeric | 50 | Type description (e.g., "Sale", "Purchase") | — |

### `CVTRA04Y.cpy` — Transaction Category Record

**VSAM:** KSDS  
**Used by:** CBTRN03C

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `TRAN-CAT-CD` | `9(04)` | Numeric display | 4 | **Primary key** — category code | — |
| `TRAN-CAT-TYPE-CD` | `X(02)` | Alphanumeric | 2 | **FK** → Transaction Type | Must match CVTRA03Y TRAN-TYPE |
| `TRAN-CAT-DESC` | `X(50)` | Alphanumeric | 50 | Category description | — |

### `CVTRA01Y.cpy` — Discount Group / Interest Rate Record

**Used by:** CBACT04C, CBTRN02C

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `DIS-GROUP-ID` | `X(10)` | Alphanumeric | 10 | Discount/interest rate group ID | Links to ACCT-GROUP-ID |
| `DIS-INT-RATE` | `S9(03)V99` | Signed decimal | 5 | Interest rate (annual %) | — |

### `CVTRA02Y.cpy` — Transaction Category Balance Record

**Used by:** CBACT04C

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `TRAN-CAT-BAL` | `S9(10)V99` | Signed decimal | 12 | Accumulated balance per transaction category | — |
| `TRAN-CAT-KEY` | — | — | — | Composite key (account + category) | — |

---

## 7. User Security Entity

### `CSUSR01Y.cpy` — User Security Record

**VSAM:** KSDS, Key = SEC-USR-ID  
**Used by:** COSGN00C, COUSR00C–03C, COADM01C, COMEN01C, all CICS programs (via COMMAREA)

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `SEC-USR-ID` | `X(08)` | Alphanumeric | 8 | **Primary key** — user login ID | Unique, non-blank |
| `SEC-USR-FNAME` | `X(20)` | Alphanumeric | 20 | User first name | — |
| `SEC-USR-LNAME` | `X(20)` | Alphanumeric | 20 | User last name | — |
| `SEC-USR-PWD` | `X(08)` | Alphanumeric | 8 | User password (**plain text**) | ⚠️ Stored in clear text — HIGH RISK |
| `SEC-USR-TYPE` | `X(01)` | Alphanumeric | 1 | User type | `'A'` = Admin, `'U'` = Regular user |
| `SEC-USR-UPD-DT` | `X(08)` | Alphanumeric | 8 | Last update date | — |
| `SEC-USR-UPD-TM` | `X(06)` | Alphanumeric | 6 | Last update time | — |

---

## 8. Authorization Entities (IMS Segments)

### `CIPAUSMY.cpy` — Pending Authorization Summary (IMS Parent Segment)

**IMS Database:** PAUTBPCB | **Segment Type:** Root  
**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, PAUDBLOD, PAUDBUNL, DBUNLDGS

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `PA-ACCT-ID` | `S9(11) COMP-3` | Packed decimal | 6 | Account ID | Links to CVACT01Y |
| `PA-CUST-ID` | `9(09)` | Numeric display | 9 | Customer ID | Links to CVCUS01Y |
| `PA-AUTH-STATUS` | `X(01)` | Alphanumeric | 1 | Authorization status | — |
| `PA-ACCOUNT-STATUS` | `X(02) OCCURS 5` | Alphanumeric | 10 | Account status array (5 entries) | — |
| `PA-CREDIT-LIMIT` | `S9(09)V99 COMP-3` | Packed decimal | 6 | Credit limit for auth check | — |
| `PA-CASH-LIMIT` | `S9(09)V99 COMP-3` | Packed decimal | 6 | Cash advance limit | — |
| `PA-CREDIT-BALANCE` | `S9(09)V99 COMP-3` | Packed decimal | 6 | Current credit balance | — |
| `PA-CASH-BALANCE` | `S9(09)V99 COMP-3` | Packed decimal | 6 | Current cash balance | — |
| `PA-APPROVED-AUTH-CNT` | `S9(04) COMP` | Binary | 2 | Count of approved authorizations | Used in CBPAUP0C expiry check |
| `PA-DECLINED-AUTH-CNT` | `S9(04) COMP` | Binary | 2 | Count of declined authorizations | — |
| `PA-APPROVED-AUTH-AMT` | `S9(09)V99 COMP-3` | Packed decimal | 6 | Total approved authorization amount | — |
| `PA-DECLINED-AUTH-AMT` | `S9(09)V99 COMP-3` | Packed decimal | 6 | Total declined authorization amount | — |
| `FILLER` | `X(34)` | Filler | 34 | Reserved | — |

### `CIPAUDTY.cpy` — Pending Authorization Detail (IMS Child Segment)

**IMS Database:** PAUTBPCB | **Segment Type:** Dependent of CIPAUSMY  
**Used by:** CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, PAUDBLOD, PAUDBUNL, DBUNLDGS

| Field Name | PIC Clause | Type | Bytes | Business Meaning | Validation Rules |
|------------|-----------|------|-------|------------------|-----------------|
| `PA-AUTH-DATE-9C` | `S9(05) COMP-3` | Packed decimal | 3 | Authorization date (Julian) | Key field in IMS sequence |
| `PA-AUTH-TIME-9C` | `S9(09) COMP-3` | Packed decimal | 5 | Authorization time | Key field in IMS sequence |
| `PA-AUTH-ORIG-DATE` | `X(06)` | Alphanumeric | 6 | Original auth date | Display format |
| `PA-AUTH-ORIG-TIME` | `X(06)` | Alphanumeric | 6 | Original auth time | Display format |
| `PA-CARD-NUM` | `X(16)` | Alphanumeric | 16 | Card number | Links to CVACT02Y |
| `PA-AUTH-TYPE` | `X(04)` | Alphanumeric | 4 | Authorization type | — |
| `PA-CARD-EXPIRY-DATE` | `X(04)` | Alphanumeric | 4 | Card expiry at time of auth | — |
| `PA-MESSAGE-TYPE` | `X(06)` | Alphanumeric | 6 | ISO message type | — |
| `PA-MESSAGE-SOURCE` | `X(06)` | Alphanumeric | 6 | Message source | — |
| `PA-AUTH-ID-CODE` | `X(06)` | Alphanumeric | 6 | Authorization ID code | Returned in response |
| `PA-AUTH-RESP-CODE` | `X(02)` | Alphanumeric | 2 | Response code | `'00'` = approved (88-level) |
| `PA-AUTH-RESP-REASON` | `X(04)` | Alphanumeric | 4 | Decline reason code | — |
| `PA-PROCESSING-CODE` | `9(06)` | Numeric display | 6 | Processing code | — |
| `PA-TRANSACTION-AMT` | `S9(10)V99 COMP-3` | Packed decimal | 7 | Requested transaction amount | — |
| `PA-APPROVED-AMT` | `S9(10)V99 COMP-3` | Packed decimal | 7 | Approved amount | — |
| `PA-MERCHANT-CATAGORY-CODE` | `X(04)` | Alphanumeric | 4 | Merchant category code (MCC) | — |
| `PA-ACQR-COUNTRY-CODE` | `X(03)` | Alphanumeric | 3 | Acquirer country code | — |
| `PA-POS-ENTRY-MODE` | `9(02)` | Numeric display | 2 | POS entry mode | — |
| `PA-MERCHANT-ID` | `X(15)` | Alphanumeric | 15 | Merchant ID | — |
| `PA-MERCHANT-NAME` | `X(22)` | Alphanumeric | 22 | Merchant name | — |
| `PA-MERCHANT-CITY` | `X(13)` | Alphanumeric | 13 | Merchant city | — |
| `PA-MERCHANT-STATE` | `X(02)` | Alphanumeric | 2 | Merchant state | — |
| `PA-MERCHANT-ZIP` | `X(09)` | Alphanumeric | 9 | Merchant ZIP | — |
| `PA-TRANSACTION-ID` | `X(15)` | Alphanumeric | 15 | Transaction ID | — |
| `PA-MATCH-STATUS` | `X(01)` | Alphanumeric | 1 | Auth-transaction match status | `'P'`=Pending, `'D'`=Declined, `'E'`=Expired, `'M'`=Matched |
| `PA-AUTH-FRAUD` | `X(01)` | Alphanumeric | 1 | Fraud flag | `'F'`=Fraud confirmed, `'R'`=Fraud removed |
| `PA-FRAUD-RPT-DATE` | `X(08)` | Alphanumeric | 8 | Fraud report date | — |
| `FILLER` | `X(17)` | Filler | 17 | Reserved | — |

---

## 9. MQ Message Structures

### `CCPAURQY.cpy` — Authorization Request Message

**Used by:** COPAUA0C (MQ input)

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|------------|-----------|------|-------|------------------|
| `PA-RQ-AUTH-DATE` | `X(06)` | Alphanumeric | 6 | Request date |
| `PA-RQ-AUTH-TIME` | `X(06)` | Alphanumeric | 6 | Request time |
| `PA-RQ-CARD-NUM` | `X(16)` | Alphanumeric | 16 | Card number |
| `PA-RQ-AUTH-TYPE` | `X(04)` | Alphanumeric | 4 | Authorization type |
| `PA-RQ-CARD-EXPIRY-DATE` | `X(04)` | Alphanumeric | 4 | Card expiry |
| `PA-RQ-MESSAGE-TYPE` | `X(06)` | Alphanumeric | 6 | ISO message type |
| `PA-RQ-MESSAGE-SOURCE` | `X(06)` | Alphanumeric | 6 | Source system |
| `PA-RQ-PROCESSING-CODE` | `9(06)` | Numeric display | 6 | Processing code |
| `PA-RQ-TRANSACTION-AMT` | `+9(10).99` | Edited numeric | 14 | Transaction amount |
| `PA-RQ-MERCHANT-CATAGORY-CODE` | `X(04)` | Alphanumeric | 4 | MCC |
| `PA-RQ-ACQR-COUNTRY-CODE` | `X(03)` | Alphanumeric | 3 | Country code |
| `PA-RQ-POS-ENTRY-MODE` | `9(02)` | Numeric display | 2 | POS entry mode |
| `PA-RQ-MERCHANT-ID` | `X(15)` | Alphanumeric | 15 | Merchant ID |
| `PA-RQ-MERCHANT-NAME` | `X(22)` | Alphanumeric | 22 | Merchant name |
| `PA-RQ-MERCHANT-CITY` | `X(13)` | Alphanumeric | 13 | Merchant city |
| `PA-RQ-MERCHANT-STATE` | `X(02)` | Alphanumeric | 2 | Merchant state |
| `PA-RQ-MERCHANT-ZIP` | `X(09)` | Alphanumeric | 9 | Merchant ZIP |
| `PA-RQ-TRANSACTION-ID` | `X(15)` | Alphanumeric | 15 | Transaction ID |

### `CCPAURLY.cpy` — Authorization Response Message

**Used by:** COPAUA0C (MQ output)

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|------------|-----------|------|-------|------------------|
| `PA-RL-CARD-NUM` | `X(16)` | Alphanumeric | 16 | Card number (echo back) |
| `PA-RL-TRANSACTION-ID` | `X(15)` | Alphanumeric | 15 | Transaction ID (echo back) |
| `PA-RL-AUTH-ID-CODE` | `X(06)` | Alphanumeric | 6 | Authorization ID assigned |
| `PA-RL-AUTH-RESP-CODE` | `X(02)` | Alphanumeric | 2 | Response code (`'00'`=approved) |
| `PA-RL-AUTH-RESP-REASON` | `X(04)` | Alphanumeric | 4 | Decline reason |
| `PA-RL-APPROVED-AMT` | `+9(10).99` | Edited numeric | 14 | Approved amount |

### `CCPAUERY.cpy` — Authorization Error Log Record

**Used by:** COPAUA0C

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|------------|-----------|------|-------|------------------|
| `ERR-DATE` | `X(06)` | Alphanumeric | 6 | Error date |
| `ERR-TIME` | `X(06)` | Alphanumeric | 6 | Error time |
| `ERR-APPLICATION` | `X(08)` | Alphanumeric | 8 | Application name |
| `ERR-PROGRAM` | `X(08)` | Alphanumeric | 8 | Program name |
| `ERR-LOCATION` | `X(04)` | Alphanumeric | 4 | Error location code |
| `ERR-LEVEL` | `X(01)` | Alphanumeric | 1 | Severity: `'L'`=Log, `'I'`=Info, `'W'`=Warning, `'C'`=Critical |
| `ERR-SUBSYSTEM` | `X(01)` | Alphanumeric | 1 | Subsystem: `'A'`=App, `'C'`=CICS, `'I'`=IMS, `'D'`=DB2, `'M'`=MQ, `'F'`=File |
| `ERR-CODE-1` | `X(09)` | Alphanumeric | 9 | Primary error code |
| `ERR-CODE-2` | `X(09)` | Alphanumeric | 9 | Secondary error code |
| `ERR-MESSAGE` | `X(50)` | Alphanumeric | 50 | Error message text |
| `ERR-EVENT-KEY` | `X(20)` | Alphanumeric | 20 | Event correlation key |

---

## 10. Application Infrastructure Copybooks

### `COCOM01Y.cpy` — CICS Communication Area (COMMAREA)

**Used by:** All 17 CICS online programs

| Field Name | PIC Clause | Type | Bytes | Business Meaning |
|------------|-----------|------|-------|------------------|
| `CDEMO-FROM-TRANID` | `X(04)` | Alphanumeric | 4 | Source transaction ID |
| `CDEMO-FROM-PROGRAM` | `X(08)` | Alphanumeric | 8 | Source program name |
| `CDEMO-TO-TRANID` | `X(04)` | Alphanumeric | 4 | Target transaction ID |
| `CDEMO-TO-PROGRAM` | `X(08)` | Alphanumeric | 8 | Target program name |
| `CDEMO-USER-ID` | `X(08)` | Alphanumeric | 8 | Logged-in user ID |
| `CDEMO-USER-TYPE` | `X(01)` | Alphanumeric | 1 | User type: `'A'`=Admin, `'U'`=User |
| `CDEMO-PGM-CONTEXT` | `9(01)` | Numeric display | 1 | `0`=Initial entry, `1`=Re-enter |
| `CDEMO-CUST-ID` | `9(09)` | Numeric display | 9 | Current customer context |
| `CDEMO-ACCT-ID` | `9(11)` | Numeric display | 11 | Current account context |
| `CDEMO-CARD-NUM` | `9(16)` | Numeric display | 16 | Current card context |
| `CDEMO-LAST-MAP` | `X(07)` | Alphanumeric | 7 | Last BMS map displayed |
| `CDEMO-LAST-MAPSET` | `X(07)` | Alphanumeric | 7 | Last BMS mapset |

### `CODATECN.cpy` — Date Conversion Record

**Used by:** CBACT01C (with assembler COBDATFT)

Provides input/output structures for date format conversion between `YYYYMMDD` and `YYYY-MM-DD` formats. Contains REDEFINES for multiple date layout interpretations.

### `COTTL01Y.cpy` — Screen Title Line

**Used by:** All CICS programs

Standard title line for BMS screens with application name and version.

### `CSDAT01Y.cpy` — Date Display Fields

**Used by:** All CICS programs

Current date/time display fields for screen headers.

### `CSMSG01Y.cpy` — Message Area (Short)

**Used by:** All CICS programs

Short message display area for user feedback (success/error messages).

### `CSMSG02Y.cpy` — Message Area (Long)

**Used by:** Selected CICS programs (COACTVWC, COCRDSLC, COCRDUPC, COPAUS0C, COPAUS1C)

Extended message display area for detailed error descriptions.

### `COADM02Y.cpy` — Admin Menu Options

**Used by:** COADM01C

Defines 6 admin menu options (User List/Add/Update/Delete, Tran Type List, Tran Type Maintenance) with target program names (COUSR00C–03C, COTRTLIC, COTRTUPC).

### `COMEN02Y.cpy` — Main Menu Options

**Used by:** COMEN01C

Defines 11 main menu options (Account View/Update, Card List/View/Update, Transaction List/View/Add, Reports, Bill Payment, Auth Summary) with target program names and access levels (`'U'` for user, `'A'` for admin).

### `CVCRD01Y.cpy` — Card View Working Storage

**Used by:** COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC

Working-storage variables for card display screens.

### `CVEXPORT.cpy` — Export File Record Layout

**Used by:** CBEXPORT, CBIMPORT

Multi-record export layout with record-type indicator for customer, account, card-xref, and transaction records. Used for branch migration data exchange.

---

## 11. Validation & Lookup Copybooks

### `CSLKPCDY.cpy` — State / ZIP / Phone Area Code Lookup Tables

**Record Size:** 1,318 lines | **Used by:** COACTUPC

Contains three hardcoded validation tables:
- **US State Codes:** All 50 states + DC, territories (`AL`, `AK`, ..., `WY`)
- **ZIP Code Prefixes:** Valid 3-digit ZIP prefixes mapped to area codes
- **NANPA Area Codes:** Valid North American telephone area codes (200+ entries)

### `CSSETATY.cpy` — Screen Attribute Macro (COPY REPLACING)

**Used by:** COACTUPC (included 3 times via `COPY REPLACING`)

Defines BMS screen field attributes (color, protection, brightness) as a template. Used with COBOL's `COPY ... REPLACING` to generate attribute-setting code for different field groups.

### `CSSTRPFY.cpy` — String Parsing Functions

**Used by:** COACTVWC, COCRDLIC, COCRDSLC

String manipulation utilities for parsing and formatting display fields.

### `CSUTLDPY.cpy` — Date Validation Working Storage

**Used by:** COACTUPC (and CSUTLDTC utility)

Working-storage variables for date validation including leap-year checking, month/day range validation. 375 lines of date arithmetic support.

### `CSUTLDWY.cpy` — Date Validation Extension

**Used by:** COACTUPC

Additional date validation working storage (89 lines) extending CSUTLDPY with century/year calculations.

### `COSTM01.CPY` — Statement Output Layout

**Used by:** CBSTM03A

Defines the text format layout for customer statements with header, detail, and footer sections.

### `UNUSED1Y.cpy` — Unused Copybook

Not referenced by any program. Candidate for removal.

---

## 12. DB2 Copybooks

### `CSDB2RPY.cpy` — DB2 Common Read Procedures

**Used by:** COTRTLIC, COTRTUPC

Common DB2 query execution procedures including SQLCA evaluation, error formatting via DSNTIAC, and cursor management.

### `CSDB2RWY.cpy` — DB2 Common Read/Write Procedures

**Used by:** COTRTLIC, COTRTUPC

Extended DB2 procedures for INSERT/UPDATE/DELETE operations with SQLCA error handling.

---

## 13. IMS Infrastructure Copybooks

### `IMSFUNCS.cpy` — IMS DL/I Function Codes

**Used by:** PAUDBLOD, PAUDBUNL, DBUNLDGS

Defines IMS function codes as named constants: `GU`, `GHU`, `GN`, `GHN`, `GNP`, `GHNP`, `REPL`, `ISRT`, `DLET`.

### `PAUTBPCB.CPY` — IMS PCB for Auth Database

**Used by:** PAUDBLOD, PAUDBUNL, DBUNLDGS

Program Communication Block (PCB) mask for the pending authorization IMS database.

### `PASFLPCB.CPY` — IMS PCB (Sequential)

**Used by:** DBUNLDGS

PCB mask for sequential (GSAM) output in IMS unload operations.

### `PADFLPCB.CPY` — IMS PCB (Detail)

**Used by:** DBUNLDGS

PCB mask for the detail segment of the pending authorization database.

---

## 14. Data Type Reference

| COBOL PIC | Java Equivalent | Size (bytes) | Notes |
|-----------|----------------|--------------|-------|
| `PIC X(n)` | `String` | n | Fixed-length alphanumeric; space-padded |
| `PIC 9(n)` | `long` / `BigDecimal` | n | Numeric display (zoned decimal) |
| `PIC S9(n)V99` | `BigDecimal(n+2, 2)` | n+2 | **Signed decimal — use BigDecimal, never double/float** |
| `PIC S9(n) COMP` | `int` / `short` | 2 or 4 | Binary integer |
| `PIC S9(n)V99 COMP-3` | `BigDecimal` | ⌈(n+3)/2⌉ | Packed decimal — common in IMS segments |
| `PIC +9(n).99` | `BigDecimal` | n+4 | Edited numeric for display/MQ messages |
| `PIC X(01)` (88-level) | `enum` / `boolean` | 1 | Condition-name flags (e.g., `'Y'`/`'N'`) |
