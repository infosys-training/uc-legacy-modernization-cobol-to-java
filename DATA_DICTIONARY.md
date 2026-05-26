# Data Dictionary

Field-level definitions extracted from every copybook in the COBOL estate.

---

## Account Entity

### CVACT01Y.cpy — Account Record (RECLN 300)

```
01  ACCOUNT-RECORD.
    05  ACCT-ID                           PIC 9(11).
    05  ACCT-ACTIVE-STATUS                PIC X(01).
    05  ACCT-CURR-BAL                     PIC S9(10)V99.
    05  ACCT-CREDIT-LIMIT                 PIC S9(10)V99.
    05  ACCT-CASH-CREDIT-LIMIT            PIC S9(10)V99.
    05  ACCT-OPEN-DATE                    PIC X(10).
    05  ACCT-EXPIRAION-DATE               PIC X(10).
    05  ACCT-REISSUE-DATE                 PIC X(10).
    05  ACCT-CURR-CYC-CREDIT              PIC S9(10)V99.
    05  ACCT-CURR-CYC-DEBIT               PIC S9(10)V99.
    05  ACCT-ADDR-ZIP                     PIC X(10).
    05  ACCT-GROUP-ID                     PIC X(10).
    05  FILLER                            PIC X(178).
```

| Field | PIC | Bytes | Description |
|-------|-----|------:|-------------|
| ACCT-ID | 9(11) | 11 | Account identifier (numeric key) |
| ACCT-ACTIVE-STATUS | X(01) | 1 | Active/inactive flag |
| ACCT-CURR-BAL | S9(10)V99 | 12 | Current balance (signed, 2 decimal) |
| ACCT-CREDIT-LIMIT | S9(10)V99 | 12 | Credit limit |
| ACCT-CASH-CREDIT-LIMIT | S9(10)V99 | 12 | Cash advance credit limit |
| ACCT-OPEN-DATE | X(10) | 10 | Account open date (YYYY-MM-DD) |
| ACCT-EXPIRAION-DATE | X(10) | 10 | Account expiration date |
| ACCT-REISSUE-DATE | X(10) | 10 | Card reissue date |
| ACCT-CURR-CYC-CREDIT | S9(10)V99 | 12 | Current cycle credit total |
| ACCT-CURR-CYC-DEBIT | S9(10)V99 | 12 | Current cycle debit total |
| ACCT-ADDR-ZIP | X(10) | 10 | Account holder zip code |
| ACCT-GROUP-ID | X(10) | 10 | Disclosure/interest rate group |
| FILLER | X(178) | 178 | Reserved |

### CVACT02Y.cpy — Card Record (RECLN 150)

```
01  CARD-RECORD.
    05  CARD-NUM                          PIC X(16).
    05  CARD-ACCT-ID                      PIC 9(11).
    05  CARD-CVV-CD                       PIC 9(03).
    05  CARD-EMBOSSED-NAME                PIC X(50).
    05  CARD-EXPIRAION-DATE               PIC X(10).
    05  CARD-ACTIVE-STATUS                PIC X(01).
    05  FILLER                            PIC X(59).
```

| Field | PIC | Bytes | Description |
|-------|-----|------:|-------------|
| CARD-NUM | X(16) | 16 | Card number (primary key) |
| CARD-ACCT-ID | 9(11) | 11 | Linked account ID |
| CARD-CVV-CD | 9(03) | 3 | CVV security code |
| CARD-EMBOSSED-NAME | X(50) | 50 | Name embossed on card |
| CARD-EXPIRAION-DATE | X(10) | 10 | Card expiration date |
| CARD-ACTIVE-STATUS | X(01) | 1 | Active/inactive flag |
| FILLER | X(59) | 59 | Reserved |

### CVACT03Y.cpy — Card Cross-Reference Record (RECLN 50)

```
01  CARD-XREF-RECORD.
    05  XREF-CARD-NUM                     PIC X(16).
    05  XREF-CUST-ID                      PIC 9(09).
    05  XREF-ACCT-ID                      PIC 9(11).
    05  FILLER                            PIC X(14).
```

| Field | PIC | Bytes | Description |
|-------|-----|------:|-------------|
| XREF-CARD-NUM | X(16) | 16 | Card number (key) |
| XREF-CUST-ID | 9(09) | 9 | Customer ID |
| XREF-ACCT-ID | 9(11) | 11 | Account ID |
| FILLER | X(14) | 14 | Reserved |

---

## Customer Entity

### CVCUS01Y.cpy — Customer Record (RECLN 500)

```
01  CUSTOMER-RECORD.
    05  CUST-ID                           PIC 9(09).
    05  CUST-FIRST-NAME                   PIC X(25).
    05  CUST-MIDDLE-NAME                  PIC X(25).
    05  CUST-LAST-NAME                    PIC X(25).
    05  CUST-ADDR-LINE-1                  PIC X(50).
    05  CUST-ADDR-LINE-2                  PIC X(50).
    05  CUST-ADDR-LINE-3                  PIC X(50).
    05  CUST-ADDR-STATE-CD                PIC X(02).
    05  CUST-ADDR-COUNTRY-CD              PIC X(03).
    05  CUST-ADDR-ZIP                     PIC X(10).
    05  CUST-PHONE-NUM-1                  PIC X(15).
    05  CUST-PHONE-NUM-2                  PIC X(15).
    05  CUST-SSN                          PIC 9(09).
    05  CUST-GOVT-ISSUED-ID               PIC X(20).
    05  CUST-DOB-YYYY-MM-DD               PIC X(10).
    05  CUST-EFT-ACCOUNT-ID               PIC X(10).
    05  CUST-PRI-CARD-HOLDER-IND          PIC X(01).
    05  CUST-FICO-CREDIT-SCORE            PIC 9(03).
    05  FILLER                            PIC X(168).
```

| Field | PIC | Bytes | Description |
|-------|-----|------:|-------------|
| CUST-ID | 9(09) | 9 | Customer identifier (numeric key) |
| CUST-FIRST-NAME | X(25) | 25 | First name |
| CUST-MIDDLE-NAME | X(25) | 25 | Middle name |
| CUST-LAST-NAME | X(25) | 25 | Last name |
| CUST-ADDR-LINE-1/2/3 | X(50) each | 150 | Address lines |
| CUST-ADDR-STATE-CD | X(02) | 2 | US state code |
| CUST-ADDR-COUNTRY-CD | X(03) | 3 | Country code |
| CUST-ADDR-ZIP | X(10) | 10 | Zip/postal code |
| CUST-PHONE-NUM-1/2 | X(15) each | 30 | Phone numbers |
| CUST-SSN | 9(09) | 9 | Social Security Number |
| CUST-GOVT-ISSUED-ID | X(20) | 20 | Government-issued ID |
| CUST-DOB-YYYY-MM-DD | X(10) | 10 | Date of birth |
| CUST-EFT-ACCOUNT-ID | X(10) | 10 | EFT account identifier |
| CUST-PRI-CARD-HOLDER-IND | X(01) | 1 | Primary cardholder indicator |
| CUST-FICO-CREDIT-SCORE | 9(03) | 3 | FICO credit score |
| FILLER | X(168) | 168 | Reserved |

### CUSTREC.cpy — Customer Record (Statement Variant)

Identical layout to CVCUS01Y except `CUST-DOB-YYYYMMDD` (no dashes) instead of `CUST-DOB-YYYY-MM-DD`. Used by CBSTM03A for statement generation.

---

## Transaction Entity

### CVTRA05Y.cpy — Transaction Record (RECLN 350)

```
01  TRAN-RECORD.
    05  TRAN-ID                           PIC X(16).
    05  TRAN-TYPE-CD                      PIC X(02).
    05  TRAN-CAT-CD                       PIC 9(04).
    05  TRAN-SOURCE                       PIC X(10).
    05  TRAN-DESC                         PIC X(100).
    05  TRAN-AMT                          PIC S9(09)V99.
    05  TRAN-MERCHANT-ID                  PIC 9(09).
    05  TRAN-MERCHANT-NAME                PIC X(50).
    05  TRAN-MERCHANT-CITY                PIC X(50).
    05  TRAN-MERCHANT-ZIP                 PIC X(10).
    05  TRAN-CARD-NUM                     PIC X(16).
    05  TRAN-ORIG-TS                      PIC X(26).
    05  TRAN-PROC-TS                      PIC X(26).
    05  FILLER                            PIC X(20).
```

| Field | PIC | Bytes | Description |
|-------|-----|------:|-------------|
| TRAN-ID | X(16) | 16 | Transaction identifier |
| TRAN-TYPE-CD | X(02) | 2 | Transaction type code |
| TRAN-CAT-CD | 9(04) | 4 | Transaction category code |
| TRAN-SOURCE | X(10) | 10 | Transaction source |
| TRAN-DESC | X(100) | 100 | Transaction description |
| TRAN-AMT | S9(09)V99 | 11 | Transaction amount (signed, 2 decimal) |
| TRAN-MERCHANT-ID | 9(09) | 9 | Merchant identifier |
| TRAN-MERCHANT-NAME | X(50) | 50 | Merchant name |
| TRAN-MERCHANT-CITY | X(50) | 50 | Merchant city |
| TRAN-MERCHANT-ZIP | X(10) | 10 | Merchant zip code |
| TRAN-CARD-NUM | X(16) | 16 | Card number used |
| TRAN-ORIG-TS | X(26) | 26 | Origination timestamp |
| TRAN-PROC-TS | X(26) | 26 | Processing timestamp |
| FILLER | X(20) | 20 | Reserved |

### CVTRA06Y.cpy — Daily Transaction Record (RECLN 350)

Same layout as CVTRA05Y with field prefix `DALYTRAN-` instead of `TRAN-`. Input format for batch posting programs (CBTRN01C, CBTRN02C).

### CVTRA01Y.cpy — Transaction Category Balance Record (RECLN 50)

```
01  TRAN-CAT-BAL-RECORD.
    05  TRAN-CAT-KEY.
       10 TRANCAT-ACCT-ID                 PIC 9(11).
       10 TRANCAT-TYPE-CD                 PIC X(02).
       10 TRANCAT-CD                      PIC 9(04).
    05  TRAN-CAT-BAL                      PIC S9(09)V99.
    05  FILLER                            PIC X(22).
```

| Field | PIC | Bytes | Description |
|-------|-----|------:|-------------|
| TRANCAT-ACCT-ID | 9(11) | 11 | Account ID (composite key part 1) |
| TRANCAT-TYPE-CD | X(02) | 2 | Transaction type code (key part 2) |
| TRANCAT-CD | 9(04) | 4 | Transaction category code (key part 3) |
| TRAN-CAT-BAL | S9(09)V99 | 11 | Running category balance |

### CVTRA02Y.cpy — Disclosure Group Record (RECLN 50)

```
01  DIS-GROUP-RECORD.
    05  DIS-GROUP-KEY.
       10 DIS-ACCT-GROUP-ID               PIC X(10).
       10 DIS-TRAN-TYPE-CD                PIC X(02).
       10 DIS-TRAN-CAT-CD                 PIC 9(04).
    05  DIS-INT-RATE                      PIC S9(04)V99.
    05  FILLER                            PIC X(28).
```

| Field | PIC | Bytes | Description |
|-------|-----|------:|-------------|
| DIS-ACCT-GROUP-ID | X(10) | 10 | Account group (maps to ACCT-GROUP-ID) |
| DIS-TRAN-TYPE-CD | X(02) | 2 | Transaction type code |
| DIS-TRAN-CAT-CD | 9(04) | 4 | Transaction category code |
| DIS-INT-RATE | S9(04)V99 | 6 | Interest rate (signed, 2 decimal) |

### CVTRA03Y.cpy — Transaction Type Record (RECLN 60)

```
01  TRAN-TYPE-RECORD.
    05  TRAN-TYPE                         PIC X(02).
    05  TRAN-TYPE-DESC                    PIC X(50).
    05  FILLER                            PIC X(08).
```

### CVTRA04Y.cpy — Transaction Category Record (RECLN 60)

```
01  TRAN-CAT-RECORD.
    05  TRAN-CAT-KEY.
       10  TRAN-TYPE-CD                   PIC X(02).
       10  TRAN-CAT-CD                    PIC 9(04).
    05  TRAN-CAT-TYPE-DESC                PIC X(50).
    05  FILLER                            PIC X(04).
```

### CVTRA07Y.cpy — Transaction Report Layout

```
01  REPORT-NAME-HEADER.
    05  REPT-SHORT-NAME                   PIC X(38).   "DALYREPT"
    05  REPT-LONG-NAME                    PIC X(41).   "Daily Transaction Report"
    05  REPT-DATE-HEADER                  PIC X(12).   "Date Range: "
    05  REPT-START-DATE                   PIC X(10).
    05  REPT-END-DATE                     PIC X(10).

01  TRANSACTION-DETAIL-REPORT.
    05  TRAN-REPORT-TRANS-ID              PIC X(16).
    05  TRAN-REPORT-ACCOUNT-ID            PIC X(11).
    05  TRAN-REPORT-TYPE-CD               PIC X(02).
    05  TRAN-REPORT-TYPE-DESC             PIC X(15).
    05  TRAN-REPORT-CAT-CD               PIC 9(04).
    05  TRAN-REPORT-CAT-DESC              PIC X(29).
    05  TRAN-REPORT-SOURCE                PIC X(10).
    05  TRAN-REPORT-AMT                   PIC -ZZZ,ZZZ,ZZZ.ZZ.

01  REPORT-PAGE-TOTALS.
    05  REPT-PAGE-TOTAL                   PIC +ZZZ,ZZZ,ZZZ.ZZ.

01  REPORT-ACCOUNT-TOTALS.
    05  REPT-ACCOUNT-TOTAL                PIC +ZZZ,ZZZ,ZZZ.ZZ.

01  REPORT-GRAND-TOTALS.
    05  REPT-GRAND-TOTAL                  PIC +ZZZ,ZZZ,ZZZ.ZZ.
```

### COSTM01.CPY — Transaction Layout for Statements (re-keyed by card+tran-id)

```
01  TRNX-RECORD.
    05  TRNX-KEY.
        10  TRNX-CARD-NUM                 PIC X(16).
        10  TRNX-ID                       PIC X(16).
    05  TRNX-REST.
        10  TRNX-TYPE-CD                  PIC X(02).
        10  TRNX-CAT-CD                   PIC 9(04).
        10  TRNX-SOURCE                   PIC X(10).
        10  TRNX-DESC                     PIC X(100).
        10  TRNX-AMT                      PIC S9(09)V99.
        10  TRNX-MERCHANT-ID              PIC 9(09).
        10  TRNX-MERCHANT-NAME            PIC X(50).
        10  TRNX-MERCHANT-CITY            PIC X(50).
        10  TRNX-MERCHANT-ZIP             PIC X(10).
        10  TRNX-ORIG-TS                  PIC X(26).
        10  TRNX-PROC-TS                  PIC X(26).
        10  FILLER                        PIC X(20).
```

---

## Card Work Area

### CVCRD01Y.cpy — Card Common Work Area

```
01  CC-WORK-AREAS.
   05 CC-WORK-AREA.
      10 CCARD-AID                        PIC X(5).
         88  CCARD-AID-ENTER              VALUE 'ENTER'.
         88  CCARD-AID-CLEAR              VALUE 'CLEAR'.
         88  CCARD-AID-PA1                VALUE 'PA1  '.
         88  CCARD-AID-PA2                VALUE 'PA2  '.
         88  CCARD-AID-PFK01 thru PFK12   VALUE 'PFK01' thru 'PFK12'.
      10  CCARD-NEXT-PROG                PIC X(8).
      10  CCARD-NEXT-MAPSET              PIC X(7).
      10  CCARD-NEXT-MAP                 PIC X(7).
      10  CCARD-ERROR-MSG                PIC X(75).
      10  CCARD-RETURN-MSG               PIC X(75).
         88  CCARD-RETURN-MSG-OFF        VALUE LOW-VALUES.
      10 CC-ACCT-ID                      PIC X(11).
      10 CC-ACCT-ID-N REDEFINES CC-ACCT-ID PIC 9(11).
      10 CC-CARD-NUM                     PIC X(16).
      10 CC-CARD-NUM-N REDEFINES CC-CARD-NUM PIC 9(16).
      10 CC-CUST-ID                      PIC X(09).
      10 CC-CUST-ID-N REDEFINES CC-CUST-ID PIC 9(9).
```

---

## Common / Framework Copybooks

### COCOM01Y.cpy — Communication Area (COMMAREA)

```
01 CARDDEMO-COMMAREA.
   05 CDEMO-GENERAL-INFO.
      10 CDEMO-FROM-TRANID               PIC X(04).
      10 CDEMO-FROM-PROGRAM              PIC X(08).
      10 CDEMO-TO-TRANID                 PIC X(04).
      10 CDEMO-TO-PROGRAM                PIC X(08).
      10 CDEMO-USER-ID                   PIC X(08).
      10 CDEMO-USER-TYPE                 PIC X(01).
         88 CDEMO-USRTYP-ADMIN           VALUE 'A'.
         88 CDEMO-USRTYP-USER            VALUE 'U'.
      10 CDEMO-PGM-CONTEXT               PIC 9(01).
         88 CDEMO-PGM-ENTER              VALUE 0.
         88 CDEMO-PGM-REENTER            VALUE 1.
   05 CDEMO-CUSTOMER-INFO.
      10 CDEMO-CUST-ID                   PIC 9(09).
      10 CDEMO-CUST-FNAME                PIC X(25).
      10 CDEMO-CUST-MNAME                PIC X(25).
      10 CDEMO-CUST-LNAME                PIC X(25).
   05 CDEMO-ACCOUNT-INFO.
      10 CDEMO-ACCT-ID                   PIC 9(11).
      10 CDEMO-ACCT-STATUS               PIC X(01).
   05 CDEMO-CARD-INFO.
      10 CDEMO-CARD-NUM                  PIC 9(16).
   05 CDEMO-MORE-INFO.
      10  CDEMO-LAST-MAP                 PIC X(7).
      10  CDEMO-LAST-MAPSET              PIC X(7).
```

### COADM02Y.cpy — Admin Menu Options

Defines 6 admin menu options mapping to programs:
1. User List (Security) → COUSR00C
2. User Add (Security) → COUSR01C
3. User Update (Security) → COUSR02C
4. User Delete (Security) → COUSR03C
5. Transaction Type List/Update (Db2) → COTRTLIC
6. Transaction Type Maintenance (Db2) → COTRTUPC

### COMEN02Y.cpy — Regular User Menu Options

Defines 11 regular-user menu options:
1. Account View → COACTVWC
2. Account Update → COACTUPC
3. Credit Card List → COCRDLIC
4. Credit Card View → COCRDSLC
5. Credit Card Update → COCRDUPC
6. Transaction List → COTRN00C
7. Transaction View → COTRN01C
8. Transaction Add → COTRN02C
9. Transaction Reports → CORPT00C
10. Bill Payment → COBIL00C
11. Pending Authorization View → COPAUS0C

### COTTL01Y.cpy — Screen Titles

```
01 CCDA-SCREEN-TITLE.
  05 CCDA-TITLE01    PIC X(40).   "AWS Mainframe Modernization"
  05 CCDA-TITLE02    PIC X(40).   "CardDemo"
  05 CCDA-THANK-YOU  PIC X(40).   "Thank you for using CCDA application..."
```

### CSDAT01Y.cpy — Date/Time Working Storage

```
01 WS-DATE-TIME.
  05 WS-CURDATE-DATA.
    10  WS-CURDATE.
      15  WS-CURDATE-YEAR         PIC 9(04).
      15  WS-CURDATE-MONTH        PIC 9(02).
      15  WS-CURDATE-DAY          PIC 9(02).
    10 WS-CURDATE-N REDEFINES WS-CURDATE PIC 9(08).
    10  WS-CURTIME.
      15  WS-CURTIME-HOURS        PIC 9(02).
      15  WS-CURTIME-MINUTE       PIC 9(02).
      15  WS-CURTIME-SECOND       PIC 9(02).
      15  WS-CURTIME-MILSEC       PIC 9(02).
    10 WS-CURTIME-N REDEFINES WS-CURTIME PIC 9(08).
  05 WS-CURDATE-MM-DD-YY          (formatted MM/DD/YY).
  05 WS-CURTIME-HH-MM-SS          (formatted HH:MM:SS).
  05 WS-TIMESTAMP                  (YYYY-MM-DD HH:MM:SS.MMMMMM).
```

### CSMSG01Y.cpy — Common Messages

```
01 CCDA-COMMON-MESSAGES.
  05 CCDA-MSG-THANK-YOU         PIC X(50).  "Thank you for using CardDemo..."
  05 CCDA-MSG-INVALID-KEY       PIC X(50).  "Invalid key pressed..."
```

### CSMSG02Y.cpy — Abend Data

```
01  ABEND-DATA.
  05  ABEND-CODE                PIC X(4).
  05  ABEND-CULPRIT             PIC X(8).
  05  ABEND-REASON              PIC X(50).
  05  ABEND-MSG                 PIC X(72).
```

### CSUSR01Y.cpy — User Security Record

```
01 SEC-USER-DATA.
  05 SEC-USR-ID                 PIC X(08).
  05 SEC-USR-FNAME              PIC X(20).
  05 SEC-USR-LNAME              PIC X(20).
  05 SEC-USR-PWD                PIC X(08).
  05 SEC-USR-TYPE               PIC X(01).
  05 SEC-USR-FILLER             PIC X(23).
```

### CSSETATY.cpy — Set Attributes (COPY REPLACING template)

Template used with COPY REPLACING to set BMS field attributes (color = DFHRED) for validation errors. Parameterized by `TESTVAR1`, `SCRNVAR2`, `MAPNAME3`.

### CSSTRPFY.cpy — Store PF Key

Procedure Division paragraph that maps EIBAID to CCARD-AID-xxx flags (ENTER, CLEAR, PA1, PA2, PFK01–PFK12).

### CSUTLDWY.cpy — Date Validation Working Storage

Defines `WS-EDIT-DATE-CCYYMMDD` with century/year/month/day breakdown, validation flags (`FLG-YEAR-ISVALID`, `FLG-MONTH-ISVALID`, `FLG-DAY-ISVALID`), and `WS-DATE-VALIDATION-RESULT` for LE date service responses.

### CSUTLDPY.cpy — Date Validation Procedure Division

Procedure Division paragraphs: `EDIT-DATE-CCYYMMDD`, `EDIT-YEAR-CCYY`, `EDIT-MONTH`, `EDIT-DAY`, `EDIT-DAY-MONTH-YEAR` (leap year checks), `EDIT-DATE-LE` (calls CSUTLDTC for LE verification).

### CSLKPCDY.cpy — Phone Area Code / State Code Lookups

Defines `WS-US-PHONE-AREA-CODE-TO-EDIT PIC XXX` with 88-level `VALID-PHONE-AREA-CODE` containing all valid North American area codes (~370 values). Used by COACTUPC for input validation.

### CODATECN.cpy — Date Conversion Record

```
01  CODATECN-REC.
    05  CODATECN-IN-REC.
        10  CODATECN-TYPE             PIC X.
            88  YYYYMMDD-IN           VALUE "1".
            88  YYYY-MM-DD-IN         VALUE "2".
        10  CODATECN-INP-DATE         PIC X(20).
    05  CODATECN-OUT-REC.
        10  CODATECN-OUTTYPE          PIC X.
            88  YYYY-MM-DD-OP         VALUE "1".
            88  YYYYMMDD-OP           VALUE "2".
        10  CODATECN-0UT-DATE         PIC X(20).
    05  CODATECN-ERROR-MSG            PIC X(38).
```

### UNUSED1Y.cpy — Unused/Reserved Record

```
01 UNUSED-DATA.
  05 UNUSED-ID       PIC X(08).
  05 UNUSED-FNAME    PIC X(20).
  05 UNUSED-LNAME    PIC X(20).
  05 UNUSED-PWD      PIC X(08).
  05 UNUSED-TYPE     PIC X(01).
  05 UNUSED-FILLER   PIC X(23).
```

---

## Export Entity

### CVEXPORT.cpy — Multi-Record Export Layout (RECLN 500)

```
01  EXPORT-RECORD.
    05  EXPORT-REC-TYPE                   PIC X(1).
    05  EXPORT-TIMESTAMP                  PIC X(26).
    05  EXPORT-SEQUENCE-NUM               PIC 9(9) COMP.
    05  EXPORT-BRANCH-ID                  PIC X(4).
    05  EXPORT-REGION-CODE                PIC X(5).
    05  EXPORT-RECORD-DATA                PIC X(460).
```

Record types via REDEFINES on `EXPORT-RECORD-DATA`:

| Type Code | REDEFINES Name | Source Copybook | Key Fields |
|-----------|---------------|-----------------|------------|
| C | EXPORT-CUSTOMER-DATA | CVCUS01Y | EXP-CUST-ID (COMP), EXP-CUST-FICO-CREDIT-SCORE (COMP-3) |
| A | EXPORT-ACCOUNT-DATA | CVACT01Y | EXP-ACCT-CURR-BAL (COMP-3), EXP-ACCT-CASH-CREDIT-LIMIT (COMP-3), EXP-ACCT-CURR-CYC-DEBIT (COMP) |
| X | EXPORT-CARD-XREF-DATA | CVACT03Y | EXP-XREF-ACCT-ID (COMP) |
| T | EXPORT-TRANSACTION-DATA | CVTRA05Y | EXP-TRAN-AMT (COMP-3), EXP-TRAN-MERCHANT-ID (COMP) |
| D | EXPORT-CARD-DATA | CVACT02Y | EXP-CARD-ACCT-ID (COMP), EXP-CARD-CVV-CD (COMP) |

---

## Sub-Application Copybooks

### Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cpy/`

| Copybook | Purpose | Key Fields |
|----------|---------|------------|
| CCPAURQY.cpy | Pending authorization request | PA-RQ-CARD-NUM X(16), PA-RQ-TRANSACTION-AMT +9(10).99, PA-RQ-MERCHANT-ID X(15), PA-RQ-TRANSACTION-ID X(15) |
| CCPAURLY.cpy | Pending authorization response | PA-RL-CARD-NUM X(16), PA-RL-AUTH-RESP-CODE X(02), PA-RL-APPROVED-AMT +9(10).99 |
| CCPAUERY.cpy | Error log record | ERR-DATE X(06), ERR-LEVEL X(01) [L/I/W/C], ERR-SUBSYSTEM X(01) [A/C/I/D/M/F], ERR-MESSAGE X(50) |
| CIPAUSMY.cpy | IMS segment — auth summary | PA-ACCT-ID S9(11) COMP-3, PA-CREDIT-LIMIT/CASH-LIMIT/CREDIT-BALANCE/CASH-BALANCE S9(09)V99 COMP-3, PA-APPROVED/DECLINED-AUTH-CNT S9(04) COMP |
| CIPAUDTY.cpy | IMS segment — auth detail | PA-CARD-NUM X(16), PA-TRANSACTION-AMT/APPROVED-AMT S9(10)V99 COMP-3, PA-AUTH-RESP-CODE X(02), PA-MATCH-STATUS X(01) [P/D/E/M], PA-AUTH-FRAUD X(01) [F/R] |
| IMSFUNCS.cpy | IMS DL/I function codes | FUNC-GU/GHU/GN/GHN/GNP/GHNP/REPL/ISRT/DLET PIC X(04) |
| PAUTBPCB.CPY | IMS PCB mask — auth database | PAUT-DBDNAME X(08), PAUT-PCB-STATUS X(02), PAUT-KEYFB X(255) |
| PASFLPCB.CPY | IMS PCB mask — secondary index (full) | PASFL-DBDNAME X(08), PASFL-PCB-STATUS X(02), PASFL-KEYFB X(100) |
| PADFLPCB.CPY | IMS PCB mask — secondary index (detail) | PADFL-DBDNAME X(08), PADFL-PCB-STATUS X(02), PADFL-KEYFB X(255) |

### Transaction Type (DB2) — `app/app-transaction-type-db2/cpy/`

| Copybook | Purpose | Key Fields |
|----------|---------|------------|
| CSDB2RWY.cpy | DB2 common working storage | WS-DISP-SQLCODE PIC ----9, WS-DB2-PROCESSING-FLAG X(1) [88 WS-DB2-OK/'0', WS-DB2-ERROR/'1'], WS-DSNTIAC-FORMATTED (10 x 72-char lines) |
| CSDB2RPY.cpy | DB2 common procedures | 9998-PRIMING-QUERY (SELECT 1 FROM SYSIBM.SYSDUMMY1), 9999-FORMAT-DB2-MESSAGE (CALL DSNTIAC) |
