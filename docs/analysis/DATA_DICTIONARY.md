# CardDemo — Data Dictionary

Field-level dictionary for every copybook in `app/cpy/` plus the sub-application copybooks
(`app/app-authorization-ims-db2-mq/cpy/`, `app/app-transaction-type-db2/cpy/`). BMS symbolic-map
copybooks (`cpy-bms/`) are generated from the `.bms` sources and are listed only by name (§8).

Conventions:
* **Type** is derived from PIC/USAGE: `X(n)` alphanumeric; `9(n)` unsigned zoned decimal; `S9(n)V99` signed zoned
  decimal with 2 implied decimals; `COMP-3` packed decimal; `COMP`/`BINARY` binary; `+ZZZ,ZZZ.ZZ` numeric-edited.
* **Business meaning** is inferred from names, comments and the programs that use the field.
* **Validation** lists rules found in the COBOL programs that edit the field (mainly `COACTUPC`, `COCRDUPC`,
  `COTRN02C`, `CBTRN02C`, `COPAUA0C`), or 88-level conditions in the copybook itself. "None in source" means
  no program edits the field beyond record layout.
* Java mapping guidance: all monetary `S9(n)V99` fields → `BigDecimal(scale=2)`; ids stored as `9(n)` are
  numeric strings with leading zeros and should be kept as `String` or `long` consistently.

---

## 1. Account entity

### 1.1 `CVACT01Y.cpy` — `ACCOUNT-RECORD` (VSAM `ACCTDATA`, RECLEN 300, key `ACCT-ID` 11@0)

| Field | PIC | Type | Business meaning | Validation / rules |
|---|---|---|---|---|
| `ACCT-ID` | `9(11)` | Zoned numeric | Account number (primary key; also `XREF-ACCT-ID`, `CARD-ACCT-ID`) | COACTUPC `1210-EDIT-ACCOUNT`: mandatory, 11 numeric digits, not all zeros |
| `ACCT-ACTIVE-STATUS` | `X(01)` | Alnum | Account open/closed flag | `1220-EDIT-YESNO`: must be `Y` or `N`; COPAUA0C declines with `4300` (ACCOUNT-CLOSED) when not `Y` |
| `ACCT-CURR-BAL` | `S9(10)V99` | Signed decimal | Current outstanding balance | `1250-EDIT-SIGNED-9V2`: `-99999999.99` style numeric with optional sign; COBIL00C rejects payment when ≤ 0 |
| `ACCT-CREDIT-LIMIT` | `S9(10)V99` | Signed decimal | Total credit limit | Signed-9V2 edit; CBTRN02C rejects tx (reason 102 OVERLIMIT) when `CYC-CREDIT − CYC-DEBIT + TRAN-AMT > LIMIT` |
| `ACCT-CASH-CREDIT-LIMIT` | `S9(10)V99` | Signed decimal | Cash-advance limit | Signed-9V2 edit |
| `ACCT-OPEN-DATE` | `X(10)` | Date `YYYY-MM-DD` | Account opening date | `CSUTLDPY` date edits (valid CCYY/MM/DD, leap-year, LE CEEDAYS check) |
| `ACCT-EXPIRAION-DATE` | `X(10)` | Date `YYYY-MM-DD` | Account expiry (sic: "EXPIRAION") | Date edits; CBTRN02C reason 103 when `TRAN-ORIG-TS(1:10) > EXPIRATION` |
| `ACCT-REISSUE-DATE` | `X(10)` | Date `YYYY-MM-DD` | Last card re-issue date | Date edits |
| `ACCT-CURR-CYC-CREDIT` | `S9(10)V99` | Signed decimal | Credits posted in current cycle | Signed-9V2; CBTRN02C adds positive `TRAN-AMT` |
| `ACCT-CURR-CYC-DEBIT` | `S9(10)V99` | Signed decimal | Debits posted in current cycle | Signed-9V2; CBTRN02C adds negative `TRAN-AMT` |
| `ACCT-ADDR-ZIP` | `X(10)` | Alnum | Account-level ZIP | None in source (customer ZIP is edited instead) |
| `ACCT-GROUP-ID` | `X(10)` | Alnum | Disclosure group id → `DIS-ACCT-GROUP-ID` for interest rate lookup | CBACT04C falls back to group `DEFAULT` when not found |
| `FILLER` | `X(178)` | — | Padding to 300 bytes | — |

### 1.2 `CVTRA01Y.cpy` — `TRAN-CAT-BAL-RECORD` (VSAM `TCATBALF`, RECLEN 50, key 17@0)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `TRANCAT-ACCT-ID` | `9(11)` | Zoned numeric | Account (part 1 of key) | FK to ACCT-ID |
| `TRANCAT-TYPE-CD` | `X(02)` | Alnum | Transaction type code (part 2 of key) | FK to TRAN-TYPE |
| `TRANCAT-CD` | `9(04)` | Zoned numeric | Transaction category code (part 3 of key) | FK to TRAN-CAT-CD |
| `TRAN-CAT-BAL` | `S9(09)V99` | Signed decimal | Running balance for account/type/category; basis for interest | CBTRN02C creates record on first use, else adds amount |
| `FILLER` | `X(22)` | — | — | — |

### 1.3 `CVTRA02Y.cpy` — `DIS-GROUP-RECORD` (VSAM `DISCGRP`, RECLEN 50, key 16@0)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `DIS-ACCT-GROUP-ID` | `X(10)` | Alnum | Disclosure/pricing group | `DEFAULT` group must exist (CBACT04C fallback) |
| `DIS-TRAN-TYPE-CD` | `X(02)` | Alnum | Transaction type | — |
| `DIS-TRAN-CAT-CD` | `9(04)` | Zoned numeric | Transaction category | — |
| `DIS-INT-RATE` | `S9(04)V99` | Signed decimal | Annual interest rate % for the group/type/category | Used as `(BAL × RATE) / 1200` monthly |
| `FILLER` | `X(28)` | — | — | — |

---

## 2. Customer entity

### 2.1 `CVCUS01Y.cpy` — `CUSTOMER-RECORD` (VSAM `CUSTDATA`, RECLEN 500, key `CUST-ID` 9@0)

`CUSTREC.cpy` is a near-duplicate used by `CBSTM03A` (only difference: `CUST-DOB-YYYYMMDD` name).

| Field | PIC | Type | Business meaning | Validation (COACTUPC unless noted) |
|---|---|---|---|---|
| `CUST-ID` | `9(09)` | Zoned numeric | Customer number (key; also `XREF-CUST-ID`) | `1245-EDIT-NUM-REQD`: 9 digits, non-zero |
| `CUST-FIRST-NAME` | `X(25)` | Alnum | First name | `1225-EDIT-ALPHA-REQD`: mandatory, letters/spaces only |
| `CUST-MIDDLE-NAME` | `X(25)` | Alnum | Middle name | `1235-EDIT-ALPHA-OPT`: optional, letters/spaces |
| `CUST-LAST-NAME` | `X(25)` | Alnum | Last name | Alpha required |
| `CUST-ADDR-LINE-1` | `X(50)` | Alnum | Street address 1 | `1215-EDIT-MANDATORY` |
| `CUST-ADDR-LINE-2` | `X(50)` | Alnum | Street address 2 | Optional |
| `CUST-ADDR-LINE-3` | `X(50)` | Alnum | City | Mandatory |
| `CUST-ADDR-STATE-CD` | `X(02)` | Alnum | US state code | `1270-EDIT-US-STATE-CD`: must be in `VALID-US-STATE-CODE` list (`CSLKPCDY`) |
| `CUST-ADDR-COUNTRY-CD` | `X(03)` | Alnum | ISO country | `1230-EDIT-ALPHANUM-REQD` |
| `CUST-ADDR-ZIP` | `X(10)` | Alnum | ZIP code | Numeric required; `1280-EDIT-US-STATE-ZIP-CD`: state + first 2 ZIP digits must be in `VALID-US-STATE-ZIP-CD2-COMBO` |
| `CUST-PHONE-NUM-1` | `X(15)` | Alnum `(AAA)PPP-LLLL` | Primary phone | `1260-EDIT-US-PHONE-NUM`: area code in `VALID-PHONE-AREA-CODE` (NANPA list), prefix & line numeric, prefix not starting with 0/1 |
| `CUST-PHONE-NUM-2` | `X(15)` | Alnum | Secondary phone | Same as above, optional |
| `CUST-SSN` | `9(09)` | Zoned numeric | Social Security Number | `1265-EDIT-US-SSN`: 9 digits; area part not `000`, `666`, or `900–999`; group ≠ `00`; serial ≠ `0000` |
| `CUST-GOVT-ISSUED-ID` | `X(20)` | Alnum | Driver licence / passport id | `1240-EDIT-ALPHANUM-OPT` |
| `CUST-DOB-YYYY-MM-DD` | `X(10)` | Date | Date of birth | `CSUTLDPY EDIT-DATE-OF-BIRTH`: valid date and not in the future |
| `CUST-EFT-ACCOUNT-ID` | `X(10)` | Alnum | Bank account for EFT/autopay | Alphanumeric optional |
| `CUST-PRI-CARD-HOLDER-IND` | `X(01)` | Alnum | Primary card holder flag | `Y`/`N` |
| `CUST-FICO-CREDIT-SCORE` | `9(03)` | Zoned numeric | FICO score | `1275-EDIT-FICO-SCORE`: numeric, 300 ≤ score ≤ 850 |
| `FILLER` | `X(168)` | — | Padding to 500 | — |

---

## 3. Card entity

### 3.1 `CVACT02Y.cpy` — `CARD-RECORD` (VSAM `CARDDATA`, RECLEN 150, key `CARD-NUM` 16@0; AIX on `CARD-ACCT-ID` 11@16)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `CARD-NUM` | `X(16)` | Alnum (numeric content) | Card number / PAN (key) | COCRDLIC/COCRDSLC/COTRN02C: 16 numeric digits, non-zero |
| `CARD-ACCT-ID` | `9(11)` | Zoned numeric | Owning account | 11 numeric digits |
| `CARD-CVV-CD` | `9(03)` | Zoned numeric | Card verification value (stored in clear — PCI concern) | None in source |
| `CARD-EMBOSSED-NAME` | `X(50)` | Alnum | Cardholder name on card | COCRDUPC: alphabetic + spaces only |
| `CARD-EXPIRAION-DATE` | `X(10)` | Date `YYYY-MM-DD` | Expiry | COCRDUPC: month 1–12, year 4 numeric digits |
| `CARD-ACTIVE-STATUS` | `X(01)` | Alnum | Active flag | `Y`/`N`; COPAUA0C declines `4200` (CARD-NOT-ACTIVE) when not `Y` |
| `FILLER` | `X(59)` | — | — | — |

### 3.2 `CVACT03Y.cpy` — `CARD-XREF-RECORD` (VSAM `CARDXREF`, RECLEN 50, key `XREF-CARD-NUM` 16@0; AIX on `XREF-ACCT-ID`)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `XREF-CARD-NUM` | `X(16)` | Alnum | Card number (key) | CBTRN02C reason 100 "INVALID CARD NUMBER FOUND" when no xref; COPAUA0C `3100` |
| `XREF-CUST-ID` | `9(09)` | Zoned numeric | Customer owning the card | — |
| `XREF-ACCT-ID` | `9(11)` | Zoned numeric | Account the card bills to | CBTRN02C reason 101 "ACCOUNT RECORD NOT FOUND" when account missing |
| `FILLER` | `X(14)` | — | — | — |

### 3.3 `CVCRD01Y.cpy` — `CC-WORK-AREAS` (online working storage shared by card/account screens)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `CCARD-AID` | `X(5)` | Alnum | Last attention key pressed | 88s `CCARD-AID-ENTER`, `-CLEAR`, `-PA1`, `-PA2`, `-PFK01`…`-PFK12` (set by `CSSTRPFY`) |
| `CCARD-NEXT-PROG` / `-NEXT-MAPSET` / `-NEXT-MAP` | `X(8)`/`X(7)`/`X(7)` | Alnum | Navigation target | — |
| `CCARD-ERROR-MSG`, `CCARD-RETURN-MSG` | `X(75)` | Alnum | Screen messages | 88 `CCARD-RETURN-MSG-OFF` = LOW-VALUES |
| `CC-ACCT-ID` / `CC-ACCT-ID-N` | `X(11)` / `9(11)` REDEFINES | Alnum / numeric view | Account id entered on screen | Numeric & non-zero |
| `CC-CARD-NUM` / `CC-CARD-NUM-N` | `X(16)` / `9(16)` | — | Card number entered | Numeric & non-zero |
| `CC-CUST-ID` / `CC-CUST-ID-N` | `X(09)` / `9(9)` | — | Customer id | Numeric |

---

## 4. Transaction entity

### 4.1 `CVTRA05Y.cpy` — `TRAN-RECORD` (VSAM `TRANSACT`, RECLEN 350, key `TRAN-ID` 16@0; AIX on `TRAN-PROC-TS` 26@304)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `TRAN-ID` | `X(16)` | Alnum (numeric content) | Transaction id (key). COTRN02C/COBIL00C generate next id = last key + 1 via READPREV | Numeric |
| `TRAN-TYPE-CD` | `X(02)` | Alnum | Type (FK `TRAN-TYPE`; `02` = bill payment in COBIL00C) | COTRN02C: numeric |
| `TRAN-CAT-CD` | `9(04)` | Zoned numeric | Category (FK `TRAN-CAT-CD`) | Numeric |
| `TRAN-SOURCE` | `X(10)` | Alnum | Origin channel (`POS TERM`, `System`, `TRAN-SOURCE` etc.) | Mandatory in COTRN02C |
| `TRAN-DESC` | `X(100)` | Alnum | Description | Mandatory |
| `TRAN-AMT` | `S9(09)V99` | Signed decimal | Amount (negative = debit convention in CBTRN02C) | COTRN02C: format `-99999999.99` (sign, 8 digits, point, 2 digits) |
| `TRAN-MERCHANT-ID` | `9(09)` | Zoned numeric | Merchant id | Numeric |
| `TRAN-MERCHANT-NAME` | `X(50)` | Alnum | Merchant name | Mandatory |
| `TRAN-MERCHANT-CITY` | `X(50)` | Alnum | Merchant city | Mandatory |
| `TRAN-MERCHANT-ZIP` | `X(10)` | Alnum | Merchant ZIP | Mandatory |
| `TRAN-CARD-NUM` | `X(16)` | Alnum | Card used | 16 numeric; resolved via xref |
| `TRAN-ORIG-TS` | `X(26)` | Timestamp `YYYY-MM-DD HH:MM:SS.nnnnnn` | Original (authorization) timestamp | COTRN02C: date part validated with `CSUTLDTC` mask `YYYY-MM-DD` |
| `TRAN-PROC-TS` | `X(26)` | Timestamp | Processing/posting timestamp (AIX key; report date filter) | As above |
| `FILLER` | `X(20)` | — | — | — |

### 4.2 `CVTRA06Y.cpy` — `DALYTRAN-RECORD` (sequential `DALYTRAN.PS`, RECLEN 350)

Same layout as `TRAN-RECORD` with `DALYTRAN-` prefix; input to `CBTRN01C`/`CBTRN02C`. Rejected records are written
to `DALYREJS` as `DALYTRAN-RECORD` + `WS-VALIDATION-FAIL-REASON 9(04)` + `-DESC X(76)` (reason codes `100` invalid card,
`101` account not found, `102` overlimit, `103` after expiration).

### 4.3 `CVTRA03Y.cpy` — `TRAN-TYPE-RECORD` (VSAM `TRANTYPE`, RECLEN 60, key 2@0)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `TRAN-TYPE` | `X(02)` | Alnum | Transaction type code | COTRTUPC: numeric, non-zero, mandatory |
| `TRAN-TYPE-DESC` | `X(50)` | Alnum | Description | Mandatory |
| `FILLER` | `X(08)` | — | — | — |

### 4.4 `CVTRA04Y.cpy` — `TRAN-CAT-RECORD` (VSAM `TRANCATG`, RECLEN 60, key 6@0)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `TRAN-TYPE-CD` | `X(02)` | Alnum | Parent type | FK to TRAN-TYPE (DB2 `ON DELETE RESTRICT`) |
| `TRAN-CAT-CD` | `9(04)` | Zoned numeric | Category code | — |
| `TRAN-CAT-TYPE-DESC` | `X(50)` | Alnum | Category description | — |
| `FILLER` | `X(04)` | — | — | — |

### 4.5 `COSTM01.CPY` — `TRNX-RECORD` (statement input KSDS `TRXFL.VSAM.KSDS`, key `TRNX-CARD-NUM`+`TRNX-ID` 32@0)

Transaction re-keyed by card for statement grouping: `TRNX-KEY` (`TRNX-CARD-NUM X(16)`, `TRNX-ID X(16)`) + `TRNX-REST`
(same fields as `TRAN-RECORD` from `TYPE-CD` to `PROC-TS`, minus card number).

### 4.6 `CVTRA07Y.cpy` — Transaction report layouts (`CBTRN03C`)

| Structure | Notable fields | Type |
|---|---|---|
| `REPORT-NAME-HEADER` | `REPT-SHORT-NAME X(38)` = `DALYREPT`, `REPT-LONG-NAME X(41)` = "Daily Transaction Report", `REPT-START-DATE`/`REPT-END-DATE X(10)` | Print header |
| `TRANSACTION-DETAIL-REPORT` | `TRAN-REPORT-TRANS-ID X(16)`, `-ACCOUNT-ID X(11)`, `-TYPE-CD X(02)`, `-TYPE-DESC X(15)`, `-CAT-CD 9(04)`, `-CAT-DESC X(29)`, `-SOURCE X(10)`, `TRAN-REPORT-AMT PIC -ZZZ,ZZZ,ZZZ.ZZ` | Detail line (numeric-edited amount) |
| `TRANSACTION-HEADER-1/2` | Column captions; `X(133)` dash line | Headers |
| `REPORT-PAGE-TOTALS` / `REPORT-ACCOUNT-TOTALS` / `REPORT-GRAND-TOTALS` | `REPT-PAGE-TOTAL`, `REPT-ACCOUNT-TOTAL`, `REPT-GRAND-TOTAL PIC +ZZZ,ZZZ,ZZZ.ZZ` | Totals (numeric-edited) |

---

## 5. Authorization entity (sub-application `app-authorization-ims-db2-mq`)

### 5.1 `CIPAUSMY.cpy` — Pending-authorization summary (IMS root segment `PAUTSUM0`, DB2-like mirror in `AUTHFRDS` for ACCT/CUST)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `PA-ACCT-ID` | `S9(11) COMP-3` | Packed | Account (segment key `ACCNTID`) | Populated from `XREF-ACCT-ID` |
| `PA-CUST-ID` | `9(09)` | Zoned | Customer | — |
| `PA-AUTH-STATUS` | `X(01)` | Alnum | Overall status | — |
| `PA-ACCOUNT-STATUS` | `X(02) OCCURS 5` | Array | Up to 5 status codes | — |
| `PA-CREDIT-LIMIT`, `PA-CASH-LIMIT` | `S9(09)V99 COMP-3` | Packed decimal | Limits copied from account | — |
| `PA-CREDIT-BALANCE`, `PA-CASH-BALANCE` | `S9(09)V99 COMP-3` | Packed decimal | Balances incl. pending auths | COPAUA0C: `INSUFFICIENT-FUND` → reason `4100` when available credit < amount |
| `PA-APPROVED-AUTH-CNT`, `PA-DECLINED-AUTH-CNT` | `S9(04) COMP` | Binary halfword | Counters | Incremented per decision |
| `PA-APPROVED-AUTH-AMT`, `PA-DECLINED-AUTH-AMT` | `S9(09)V99 COMP-3` | Packed | Accumulated amounts | — |
| `FILLER` | `X(34)` | — | — | — |

### 5.2 `CIPAUDTY.cpy` — Authorization detail (IMS child segment `PAUTDTL1`, key `PAUT9CTS`)

| Field | PIC | Type | Business meaning | Validation / 88s |
|---|---|---|---|---|
| `PA-AUTH-DATE-9C` / `PA-AUTH-TIME-9C` | `S9(05) COMP-3` / `S9(09) COMP-3` | Packed | Segment key (9's-complement timestamp so newest sorts first) | — |
| `PA-AUTH-ORIG-DATE` / `-TIME` | `X(06)` | `YYMMDD` / `HHMMSS` | Original auth date/time | — |
| `PA-CARD-NUM` | `X(16)` | Alnum | Card | Must exist in `CCXREF` else `3100` |
| `PA-AUTH-TYPE` | `X(04)` | Alnum | Auth type | — |
| `PA-CARD-EXPIRY-DATE` | `X(04)` | `MMYY` | Card expiry from message | — |
| `PA-MESSAGE-TYPE`, `PA-MESSAGE-SOURCE` | `X(06)` | Alnum | ISO message type / source | — |
| `PA-AUTH-ID-CODE` | `X(06)` | Alnum | Approval code | — |
| `PA-AUTH-RESP-CODE` | `X(02)` | Alnum | Response code | 88 `PA-AUTH-APPROVED` = `'00'`; COPAUA0C uses `05` for decline |
| `PA-AUTH-RESP-REASON` | `X(04)` | Alnum | Reason | `0000` ok, `3100` card/acct/cust not found, `4100` insufficient funds, `4200` card inactive, `4300` account closed, `5100` card fraud, `5200` merchant fraud, `9000` other |
| `PA-PROCESSING-CODE` | `9(06)` | Zoned | ISO processing code | — |
| `PA-TRANSACTION-AMT`, `PA-APPROVED-AMT` | `S9(10)V99 COMP-3` | Packed | Requested / approved amounts | — |
| `PA-MERCHANT-CATAGORY-CODE` | `X(04)` | Alnum | MCC | — |
| `PA-ACQR-COUNTRY-CODE` | `X(03)` | Alnum | Acquirer country | — |
| `PA-POS-ENTRY-MODE` | `9(02)` | Zoned | POS entry mode | — |
| `PA-MERCHANT-ID/-NAME/-CITY/-STATE/-ZIP` | `X(15)/X(22)/X(13)/X(02)/X(09)` | Alnum | Merchant | — |
| `PA-TRANSACTION-ID` | `X(15)` | Alnum | Network transaction id | — |
| `PA-MATCH-STATUS` | `X(01)` | Alnum | Matching to posted tx | 88s `P` pending, `D` declined, `E` pending-expired, `M` matched |
| `PA-AUTH-FRAUD` | `X(01)` | Alnum | Fraud marker | 88s `F` confirmed, `R` removed (toggled in COPAUS1C, persisted by COPAUS2C) |
| `PA-FRAUD-RPT-DATE` | `X(08)` | Date | Fraud report date | — |
| `FILLER` | `X(17)` | — | — | — |

### 5.3 `CCPAURQY.cpy` — MQ authorization request (fixed-format EBCDIC message)

`PA-RQ-AUTH-DATE X(06)`, `PA-RQ-AUTH-TIME X(06)`, `PA-RQ-CARD-NUM X(16)`, `PA-RQ-AUTH-TYPE X(04)`, `PA-RQ-CARD-EXPIRY-DATE X(04)`,
`PA-RQ-MESSAGE-TYPE X(06)`, `PA-RQ-MESSAGE-SOURCE X(06)`, `PA-RQ-PROCESSING-CODE 9(06)`, `PA-RQ-TRANSACTION-AMT +9(10).99` (numeric-edited),
`PA-RQ-MERCHANT-CATAGORY-CODE X(04)`, `PA-RQ-ACQR-COUNTRY-CODE X(03)`, `PA-RQ-POS-ENTRY-MODE 9(02)`, `PA-RQ-MERCHANT-ID X(15)`,
`PA-RQ-MERCHANT-NAME X(22)`, `PA-RQ-MERCHANT-CITY X(13)`, `PA-RQ-MERCHANT-STATE X(02)`, `PA-RQ-MERCHANT-ZIP X(09)`, `PA-RQ-TRANSACTION-ID X(15)`.

### 5.4 `CCPAURLY.cpy` — MQ authorization reply

`PA-RL-CARD-NUM X(16)`, `PA-RL-TRANSACTION-ID X(15)`, `PA-RL-AUTH-ID-CODE X(06)`, `PA-RL-AUTH-RESP-CODE X(02)`, `PA-RL-AUTH-RESP-REASON X(04)`,
`PA-RL-APPROVED-AMT +9(10).99`.

### 5.5 `CCPAUERY.cpy` — `ERROR-LOG-RECORD` (written to TD queue `CSSL`)

`ERR-DATE X(06)`, `ERR-TIME X(06)`, `ERR-APPLICATION X(08)`, `ERR-PROGRAM X(08)`, `ERR-LOCATION X(04)`,
`ERR-LEVEL X(01)` (88s `L` log, `I` info, `W` warning, `C` critical), `ERR-SUBSYSTEM X(01)` (88s `A` app, `C` CICS, `I` IMS, `D` DB2, `M` MQ, `F` file),
`ERR-CODE-1/2 X(09)`, `ERR-MESSAGE X(50)`, `ERR-EVENT-KEY X(20)`.

### 5.6 IMS support copybooks

| Copybook | Content |
|---|---|
| `IMSFUNCS.cpy` | DL/I function codes `FUNC-GU`, `GHU`, `GN`, `GHN`, `GNP`, `GHNP`, `REPL`, `ISRT`, `DLET` (`X(04)`), `PARMCOUNT S9(05) COMP-5 = 4` |
| `PAUTBPCB.CPY` | DB PCB mask: `PAUT-DBDNAME X(08)`, `-SEG-LEVEL X(02)`, `-PCB-STATUS X(02)` (`'  '` ok, `GE` not found, `GB` end), `-PCB-PROCOPT X(04)`, `-SEG-NAME X(08)`, `-KEYFB-NAME`/`-NUM-SENSEGS S9(05) COMP`, `-KEYFB X(255)` |
| `PASFLPCB.CPY` / `PADFLPCB.CPY` | GSAM PCB masks for root/child unload files (same shape, key feedback `X(100)`/`X(255)`) |

### 5.7 DB2 `CARDDEMO.AUTHFRDS` (DCLGEN `dcl/AUTHFRDS.dcl`)

Relational copy of the detail segment for fraud analytics: `CARD_NUM CHAR(16)`, `AUTH_TS TIMESTAMP` (PK), amounts `DECIMAL(12,2)`,
`POS_ENTRY_MODE SMALLINT`, `MERCHANT_NAME VARCHAR(22)`, `MATCH_STATUS`, `AUTH_FRAUD CHAR(1)`, `FRAUD_RPT_DATE DATE`,
`ACCT_ID DECIMAL(11)`, `CUST_ID DECIMAL(9)`.

---

## 6. Security / user entity

### 6.1 `CSUSR01Y.cpy` — `SEC-USER-DATA` (VSAM `USRSEC`, RECLEN 80, key `SEC-USR-ID` 8@0)

| Field | PIC | Type | Business meaning | Validation |
|---|---|---|---|---|
| `SEC-USR-ID` | `X(08)` | Alnum | Login id (key) | COSGN00C/COUSR01C: mandatory; upper-cased |
| `SEC-USR-FNAME`, `SEC-USR-LNAME` | `X(20)` | Alnum | Names | Mandatory (COUSR01C/02C) |
| `SEC-USR-PWD` | `X(08)` | Alnum | **Plaintext password** — compared with `=` in COSGN00C. Must be replaced by salted hash in target. | Mandatory |
| `SEC-USR-TYPE` | `X(01)` | Alnum | `A` admin, `U` user | Mandatory; drives menu routing |
| `SEC-USR-FILLER` | `X(23)` | — | — | — |

`UNUSED1Y.cpy` (`UNUSED-DATA`) is an identical, unreferenced layout (candidate for retirement).

---

## 7. CICS / common infrastructure copybooks

### 7.1 `COCOM01Y.cpy` — `CARDDEMO-COMMAREA` (passed on every XCTL/RETURN TRANSID)

| Field | PIC | Meaning / rules |
|---|---|---|
| `CDEMO-FROM-TRANID` / `CDEMO-TO-TRANID` | `X(04)` | Calling / target CICS transaction |
| `CDEMO-FROM-PROGRAM` / `CDEMO-TO-PROGRAM` | `X(08)` | Calling / target program |
| `CDEMO-USER-ID` | `X(08)` | Signed-on user |
| `CDEMO-USER-TYPE` | `X(01)` | 88 `CDEMO-USRTYP-ADMIN` = `A`, `CDEMO-USRTYP-USER` = `U` |
| `CDEMO-PGM-CONTEXT` | `9(01)` | 88 `CDEMO-PGM-ENTER` = 0 (first entry), `CDEMO-PGM-REENTER` = 1 |
| `CDEMO-CUSTOMER-INFO`: `CDEMO-CUST-ID 9(09)`, `-CUST-FNAME/-MNAME/-LNAME X(25)` | | Customer context carried between screens |
| `CDEMO-ACCOUNT-INFO`: `CDEMO-ACCT-ID 9(11)`, `CDEMO-ACCT-STATUS X(01)` | | Account context |
| `CDEMO-CARD-INFO`: `CDEMO-CARD-NUM 9(16)` | | Card context |
| `CDEMO-MORE-INFO`: `CDEMO-LAST-MAP X(7)`, `CDEMO-LAST-MAPSET X(7)` | | Return navigation |

### 7.2 Menu tables

| Copybook | Content |
|---|---|
| `COMEN02Y.cpy` | `CARDDEMO-MAIN-MENU-OPTIONS`: `CDEMO-MENU-OPT-COUNT 9(02)` = 11; per option `-NUM 9(02)`, `-NAME X(35)`, `-PGMNAME X(08)`, `-USRTYPE X(01)` (`U`/`A`). Targets COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C |
| `COADM02Y.cpy` | `CARDDEMO-ADMIN-MENU-OPTIONS`: count 6 → COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC |

### 7.3 Screen / message helpers

| Copybook | Fields | Notes |
|---|---|---|
| `COTTL01Y.cpy` | `CCDA-TITLE01 X(40)` "AWS Mainframe Modernization", `CCDA-TITLE02 X(40)` "CardDemo", `CCDA-THANK-YOU X(40)` | Screen titles |
| `CSMSG01Y.cpy` | `CCDA-MSG-THANK-YOU X(50)`, `CCDA-MSG-INVALID-KEY X(50)` | Common messages |
| `CSMSG02Y.cpy` | `ABEND-CODE X(4)`, `ABEND-CULPRIT X(8)`, `ABEND-REASON X(50)`, `ABEND-MSG X(72)` | Abend reporting |
| `CSDAT01Y.cpy` | `WS-CURDATE-YEAR 9(04)`, `-MONTH/-DAY 9(02)`, `WS-CURTIME-HOURS/MINUTE/SECOND/MILSEC 9(02)`, edited `WS-CURDATE-MM-DD-YY` (`MM/DD/YY`), `WS-CURTIME-HH-MM-SS`, `WS-TIMESTAMP` (`YYYY-MM-DD HH:MM:SS.nnnnnn`) | Current date/time work area |
| `CSSTRPFY.cpy` | Procedural: `EVALUATE TRUE WHEN EIBAID = DFHENTER/DFHCLEAR/DFHPA1/PA2/DFHPF1..PF24 → SET CCARD-AID-xxx TO TRUE` | AID key mapping |
| `CSSETATY.cpy` | Procedural template used with `COPY ... REPLACING ==(TESTVAR1)==, ==(SCRNVAR2)==, ==(MAPNAME3)==`: if flag NOT-OK/BLANK and re-enter → set attribute `DFHRED`, put `*` in blank field | Screen attribute generator (COACTUPC) |
| `CODATECN.cpy` | `CODATECN-TYPE X` (88 `YYYYMMDD-IN`=1, `YYYY-MM-DD-IN`=2), `CODATECN-INP-DATE X(20)` with two REDEFINES, `CODATECN-OUTTYPE`, `CODATECN-0UT-DATE X(20)`, `CODATECN-ERROR-MSG X(38)` | Parameter block for assembler `COBDATFT` |

### 7.4 Validation lookup copybooks

| Copybook | Content | Rule |
|---|---|---|
| `CSLKPCDY.cpy` (1,318 lines) | `WS-US-PHONE-AREA-CODE-TO-EDIT X(3)` with 88 `VALID-PHONE-AREA-CODE` (NANPA list), `VALID-GENERAL-PURP-CODE`, `VALID-EASY-RECOG-AREA-CODE`; `US-STATE-CODE-TO-EDIT X(2)` with 88 `VALID-US-STATE-CODE` (50 states + DC + territories); `US-STATE-AND-FIRST-ZIP2 X(4)` with 88 `VALID-US-STATE-ZIP-CD2-COMBO` | Membership tests for phone area code, state, and state/ZIP-prefix consistency |
| `CSUTLDWY.cpy` | `WS-EDIT-DATE-CCYYMMDD` (`CC`, `YY`, `MM`, `DD` each `X(2)` with numeric REDEFINES); 88s `THIS-CENTURY`=20, `LAST-CENTURY`=19, `WS-VALID-MONTH` 1–12, `WS-31-DAY-MONTH` (1,3,5,7,8,10,12), `WS-FEBRUARY`=2, `WS-VALID-DAY` 1–31, `WS-DAY-31/30/29`, `WS-VALID-FEB-DAY` 1–28; flags `FLG-YEAR/MONTH/DAY-ISVALID/-NOT-OK/-BLANK`; `WS-DATE-VALIDATION-RESULT` (severity, msg no, result, date, mask) | Working storage for `CSUTLDPY` |
| `CSUTLDPY.cpy` (procedural, 375 lines) | Paragraphs `EDIT-DATE-CCYYMMDD`, `EDIT-YEAR-CCYY` (century must be 19/20), `EDIT-MONTH`, `EDIT-DAY`, `EDIT-DAY-MONTH-YEAR` (30/31-day months, Feb 29 only in leap year `(YYYY mod 4 = 0 and mod 100 ≠ 0) or mod 400 = 0`), `EDIT-DATE-LE` (calls `CSUTLDTC`/CEEDAYS), `EDIT-DATE-OF-BIRTH` (must not be future) | Reusable date validation |

### 7.5 DB2 support copybooks (`app-transaction-type-db2/cpy/`)

| Copybook | Content |
|---|---|
| `CSDB2RWY.cpy` | `WS-DISP-SQLCODE PIC ----9` (edited), `WS-DB2-PROCESSING-FLAG X(1)` (88 `WS-DB2-OK`=0, `WS-DB2-ERROR`=1), `WS-DB2-CURRENT-ACTION X(72)`, DSNTIAC message buffer (`10 × X(72)`), `WS-DSNTIAC-ERR-CD 9(02)` |
| `CSDB2RPY.cpy` | Procedural: evaluates `SQLCODE` after each statement, formats message via `DSNTIAC`, sets `WS-DB2-ERROR` |
| DCLGEN `DCLTRTYP` | `TR-TYPE CHAR(2)`, `TR-DESCRIPTION CHAR(50)` ↔ `CARDDEMO.TRANSACTION_TYPE` |
| DCLGEN `DCLTRCAT` | `TRC-TYPE-CODE CHAR(2)`, `TRC-TYPE-CATEGORY DECIMAL(4)`, `TRC-CAT-DATA CHAR(50)` ↔ `CARDDEMO.TRANSACTION_TYPE_CATEGORY` |

### 7.6 Export / import interchange — `CVEXPORT.cpy` (`EXPORT-RECORD`, RECLEN 500)

| Field | PIC | Meaning |
|---|---|---|
| `EXPORT-REC-TYPE` | `X(1)` | `C` customer, `A` account, `T` transaction, `X` xref, `D` card (CBEXPORT/CBIMPORT) |
| `EXPORT-TIMESTAMP` | `X(26)` (REDEFINES date `X(10)`, sep, time `X(15)`) | Export time |
| `EXPORT-SEQUENCE-NUM` | `9(9) COMP` | Sequence |
| `EXPORT-BRANCH-ID`, `EXPORT-REGION-CODE` | `X(4)`, `X(5)` | Branch metadata |
| `EXPORT-RECORD-DATA` | `X(460)` | Payload, REDEFINED as `EXPORT-CUSTOMER-DATA`, `-ACCOUNT-DATA`, `-TRANSACTION-DATA`, `-CARD-XREF-DATA`, `-CARD-DATA` |

Payload layouts mirror the entity copybooks but **change USAGE** (e.g. `EXP-CUST-ID 9(09) COMP`, `EXP-ACCT-CURR-BAL S9(10)V99 COMP-3`,
`EXP-ACCT-CURR-CYC-DEBIT S9(10)V99 COMP`, `EXP-TRAN-AMT S9(09)V99 COMP-3`, `EXP-CUST-FICO-CREDIT-SCORE 9(03) COMP-3`,
`EXP-CUST-ADDR-LINES OCCURS 3`, `EXP-CUST-PHONE-NUMS OCCURS 2`). Any Java reader must apply per-field packed/binary decoding.

---

## 8. BMS symbolic-map copybooks (generated)

`cpy-bms/`: `COACTUP`, `COACTVW`, `COADM01`, `COBIL00`, `COCRDLI`, `COCRDSL`, `COCRDUP`, `COMEN01`, `CORPT00`, `COSGN00`, `COTRN00`,
`COTRN01`, `COTRN02`, `COUSR00`, `COUSR01`, `COUSR02`, `COUSR03`; auth `COPAU00`, `COPAU01`; DB2 `COTRTLI`, `COTRTUP`.
Each defines `<map>I` (input: `L` length `S9(4) COMP`, `F` flag `X`, `A` attribute, `I` data) and `<map>O` (output) redefinitions
for every named field; field lengths follow the entity fields above (e.g. `ACCTSIDI X(11)`, `CARDSIDI X(16)`).

## 9. Cross-entity key relationships

```
CUSTOMER-RECORD.CUST-ID  1 ──< CARD-XREF-RECORD.XREF-CUST-ID
ACCOUNT-RECORD.ACCT-ID   1 ──< CARD-XREF-RECORD.XREF-ACCT-ID   (AIX CXACAIX)
CARD-RECORD.CARD-NUM     1 ──1 CARD-XREF-RECORD.XREF-CARD-NUM
CARD-RECORD.CARD-ACCT-ID   ──> ACCOUNT-RECORD.ACCT-ID          (AIX on CARDDATA)
TRAN-RECORD.TRAN-CARD-NUM  ──> CARD-RECORD.CARD-NUM
TRAN-RECORD.(TYPE-CD,CAT-CD) ──> TRAN-CAT-RECORD ──> TRAN-TYPE-RECORD
TRAN-CAT-BAL-RECORD.(ACCT-ID,TYPE-CD,CAT-CD) ──> ACCOUNT + TRAN-CAT
DIS-GROUP-RECORD.(ACCT-GROUP-ID,TYPE-CD,CAT-CD) <── ACCOUNT-RECORD.ACCT-GROUP-ID
PAUTSUM0(PA-ACCT-ID) 1 ──< PAUTDTL1 (IMS parent/child) ; PA-CARD-NUM ──> CARD-XREF-RECORD
```
