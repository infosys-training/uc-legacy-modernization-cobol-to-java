# CardDemo Batch Job Reconciliation Checks

This document defines reconciliation checks for each batch job in `app/jcl/`. For each job: what it reads, what it writes, what checks must pass, and what business rules it enforces.

---

## Operational Jobs

### CLOSEFIL.jcl — Close CICS Files

| Aspect | Detail |
|---|---|
| **Program** | SDSF (system utility) |
| **Purpose** | Close VSAM files in the CICS region before batch processing |
| **Files Closed** | TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |

**Reconciliation Checks:**
- None — this is an operational prerequisite, not a data-processing job
- **Pre-condition:** CICS region must be running
- **Post-condition:** All listed files should have CLOSED status in CICS

**Business Rules:**
- Files must be closed before any batch job that writes to shared VSAM files (POSTTRAN, INTCALC, COMBTRAN)
- Failure to close files before batch processing can cause VSAM exclusive-control conflicts

---

### OPENFIL.jcl — Open CICS Files

| Aspect | Detail |
|---|---|
| **Program** | SDSF (system utility) |
| **Purpose** | Reopen VSAM files in CICS after batch processing completes |
| **Files Opened** | TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |

**Reconciliation Checks:**
- None — operational step
- **Post-condition:** All listed files should have OPEN/ENABLED status in CICS

---

### WAITSTEP.jcl — Timer Wait

| Aspect | Detail |
|---|---|
| **Program** | COBSWAIT (calls assembler MVSWAIT) |
| **Purpose** | Pause execution for specified centiseconds (PARM value) |
| **Data Files** | None |

**Reconciliation Checks:**
- None — timing utility only

---

## Data File Definition Jobs

These jobs create/define VSAM clusters and load initial data. They are setup jobs, not recurring batch processing.

### ACCTFILE.jcl — Define Account Data VSAM

| Aspect | Detail |
|---|---|
| **Purpose** | Delete/define ACCTDATA VSAM KSDS cluster, load from sequential file |
| **Output** | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS |
| **Key** | ACCT-ID: bytes 1–11, KEYS(11 0) |

**Reconciliation Checks:**
```
CHECK: Record count after load = record count in sequential input
CHECK: First record key = 00000000001 (lowest account ID)
CHECK: Last record key = 00000000050 (highest account ID in sample)
```

### CARDFILE.jcl — Define Card Data VSAM

| Aspect | Detail |
|---|---|
| **Purpose** | Delete/define CARDDATA VSAM KSDS cluster, load from sequential file |
| **Output** | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS |
| **Key** | CARD-NUM: bytes 1–16, KEYS(16 0) |

**Reconciliation Checks:**
```
CHECK: Record count after load = record count in sequential input
CHECK: All CARD-NUM values are 16 characters
```

### CUSTFILE.jcl — Define Customer Data VSAM

| Aspect | Detail |
|---|---|
| **Purpose** | Delete/define CUSTDATA VSAM KSDS cluster, load from sequential file |
| **Output** | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS |
| **Key** | CUST-ID: bytes 1–9, KEYS(9 0) |

### XREFFILE.jcl — Define Card Cross-Reference VSAM

| Aspect | Detail |
|---|---|
| **Purpose** | Delete/define CARDXREF VSAM KSDS cluster, load from sequential file |
| **Output** | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS |
| **Key** | XREF-CARD-NUM: bytes 1–16, KEYS(16 0) |

**Reconciliation Checks:**
```
CHECK: Every XREF-ACCT-ID exists in ACCTDATA
CHECK: Every XREF-CUST-ID exists in CUSTDATA
CHECK: Every XREF-CARD-NUM exists in CARDDATA
```

### TRANFILE.jcl — Define Transaction VSAM

| Aspect | Detail |
|---|---|
| **Purpose** | Delete/define TRANSACT VSAM KSDS cluster |
| **Output** | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS |
| **Key** | TRAN-ID: bytes 1–16, KEYS(16 0) |

### TCATBALF.jcl — Define Transaction Category Balance VSAM

| Aspect | Detail |
|---|---|
| **Purpose** | Delete/define TCATBALF VSAM KSDS cluster, load from sequential file |
| **Output** | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS |
| **Key** | Composite: ACCT-ID(11) + TYPE-CD(2) + CAT-CD(4) = KEYS(17 0) |

**Reconciliation Checks:**
```
CHECK: Every TRANCAT-ACCT-ID exists in ACCTDATA
CHECK: SUM(TRAN-CAT-BAL) is a known baseline value for comparison after batch runs
```

### DISCGRP.jcl — Define Disclosure Group VSAM

| Aspect | Detail |
|---|---|
| **Purpose** | Delete/define DISCGRP VSAM KSDS cluster, load from sequential file |
| **Output** | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS |
| **Key** | Composite: GROUP-ID(10) + TYPE-CD(2) + CAT-CD(4) = KEYS(16 0) |

**Reconciliation Checks:**
```
CHECK: All DIS-INT-RATE values are non-negative
CHECK: Unique groups present: A000000000, DEFAULT, ZEROAPR
```

### TRANTYPE.jcl — Define Transaction Type VSAM

| Aspect | Detail |
|---|---|
| **Purpose** | Delete/define TRANTYPE VSAM KSDS, load from sequential file |
| **Output** | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS |
| **Key** | TRAN-TYPE: bytes 1–2, KEYS(2 0) |

**Reconciliation Checks:**
```
CHECK: 7 records loaded (types: 01-07)
CHECK: All TRAN-TYPE values are unique 2-character codes
```

### TRANCATG.jcl — Define Transaction Category VSAM

| Aspect | Detail |
|---|---|
| **Purpose** | Delete/define TRANCATG VSAM KSDS, load from sequential file |
| **Output** | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS |
| **Key** | Composite: TYPE-CD(2) + CAT-CD(4) = KEYS(6 0) |

**Reconciliation Checks:**
```
CHECK: 18 records loaded
CHECK: Every TRAN-TYPE-CD exists in TRANTYPE
```

### TRANIDX.jcl — Define Alternate Index on Card Cross-Reference

| Aspect | Detail |
|---|---|
| **Purpose** | Define AIX (Alternate Index) on CARDXREF by ACCT-ID for account-based lookups |
| **Output** | AWS.M2.CARDDEMO.CARDXREF.VSAM.AIX + PATH |

**Reconciliation Checks:**
```
CHECK: AIX path is accessible
CHECK: Lookup by ACCT-ID returns the correct card numbers
```

### DALYREJS.jcl — Define Daily Rejections GDG Base

| Aspect | Detail |
|---|---|
| **Purpose** | Define GDG (Generation Data Group) base for DALYREJS output from POSTTRAN |

### DUSRSECJ.jcl — Define User Security File

| Aspect | Detail |
|---|---|
| **Purpose** | Create USRSEC VSAM KSDS from in-stream data, load user credentials |
| **Output** | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS |
| **Key** | SEC-USR-ID: bytes 1–8, KEYS(8 0) |

### CBADMCDJ.jcl — CICS Resource Definition

| Aspect | Detail |
|---|---|
| **Purpose** | Define CICS resources (programs, transactions, files, mapsets) for CardDemo |
| **Tool** | DFHCSDUP (CICS System Definition utility) |

---

## Batch Processing Jobs

### READACCT.jcl — Read Account Master (CBACT01C)

| Aspect | Detail |
|---|---|
| **Program** | CBACT01C |
| **Reads** | ACCTDATA VSAM KSDS (300-byte account records) |
| **Writes** | OUTFILE (PSCOMP, LRECL=107), ARRYFILE (ARRYPS, LRECL=110), VBRCFILE (VBPS, RECFM=VB, LRECL=84) |

**Reconciliation Checks:**
```
CHECK 1 — Record count conservation:
  COUNT(OUTFILE records) = COUNT(ACCTDATA records) = 50
  COUNT(ARRYFILE records) = COUNT(ACCTDATA records) = 50
  COUNT(VBRCFILE records) = 2 × COUNT(ACCTDATA records) = 100

CHECK 2 — cycDebit substitution rule:
  For every record where ACCTDATA.ACCT-CURR-CYC-DEBIT = 0:
    OUTFILE.OUT-ACCT-CURR-CYC-DEBIT = 2525.00
  (In sample data: all 50 records have cycDebit=0)

CHECK 3 — Date reformatting:
  OUTFILE.OUT-ACCT-OPEN-DATE = compact(ACCTDATA.ACCT-OPEN-DATE)
  where compact("YYYY-MM-DD") = "YYYYMMDD  " (10 bytes, blank-padded)

CHECK 4 — Array values are fixed:
  ARRYFILE slots: [1005.00, 1525.00, -1025.00, -2500.00, 0.00]

CHECK 5 — VBR record types:
  Type1 (12 bytes): ACCT-ID + ACTIVE-STATUS
  Type2 (39 bytes): ACCT-ID + CURR-BAL + CREDIT-LIMIT + REISSUE-YEAR(4)
```

**Business Rules:**
- If ACCT-CURR-CYC-DEBIT equals zero, substitute 2525.00 in output
- Dates are reformatted from YYYY-MM-DD to YYYYMMDD via COBDATFT
- Array output always contains 5 fixed values (not derived from account data)
- VBR records come in pairs: Type1 then Type2 for each account

---

### READCARD.jcl — Read Card Data (CBACT02C)

| Aspect | Detail |
|---|---|
| **Program** | CBACT02C |
| **Reads** | CARDDATA VSAM KSDS (150-byte card records) |
| **Writes** | SYSOUT (display output only) |

**Reconciliation Checks:**
```
CHECK 1 — All 50 cards read and displayed
CHECK 2 — DISPLAY output includes CARD-NUM, CARD-ACCT-ID, CARD-ACTIVE-STATUS
```

**Business Rules:**
- Read-only sequential browse of card data
- No output files — display to SYSOUT only

---

### READXREF.jcl — Read Cross-Reference (CBACT03C)

| Aspect | Detail |
|---|---|
| **Program** | CBACT03C |
| **Reads** | CARDXREF VSAM KSDS (50-byte xref records) |
| **Writes** | SYSOUT (display output only) |

**Reconciliation Checks:**
```
CHECK 1 — All 50 xref records read and displayed
CHECK 2 — DISPLAY output includes XREF-CARD-NUM, XREF-CUST-ID, XREF-ACCT-ID
```

---

### READCUST.jcl — Read Customer Data (CBCUS01C)

| Aspect | Detail |
|---|---|
| **Program** | CBCUS01C |
| **Reads** | CUSTDATA VSAM KSDS (500-byte customer records) |
| **Writes** | SYSOUT (display output only) |

**Reconciliation Checks:**
```
CHECK 1 — All 50 customer records read and displayed
CHECK 2 — DISPLAY output includes CUST-ID, CUST-FIRST-NAME, CUST-LAST-NAME
```

---

### POSTTRAN.jcl — Post Daily Transactions (CBTRN02C)

| Aspect | Detail |
|---|---|
| **Program** | CBTRN02C |
| **Reads** | DALYTRAN (350-byte daily transactions), CARDXREF (xref lookup), ACCTDATA (account validation) |
| **Writes** | TRANSACT (posted transactions), TCATBALF (category balance updates), DALYREJS (rejected transactions, LRECL=430, GDG) |

**Reconciliation Checks:**
```
CHECK 1 — Record count conservation:
  COUNT(DALYTRAN) = COUNT(new TRANSACT records) + COUNT(DALYREJS records)
  All input transactions must be either posted or rejected.

CHECK 2 — Balance integrity:
  SUM(ACCTDATA.ACCT-CURR-BAL after) - SUM(ACCTDATA.ACCT-CURR-BAL before)
    = SUM(DALYTRAN.AMT for posted transactions only)

CHECK 3 — Category balance accumulation:
  For each posted transaction with (acct-id, type-cd, cat-cd):
    TCATBALF[key].balance_after = TCATBALF[key].balance_before + TRAN-AMT
  New TCATBALF records are created if the key didn't exist before.

CHECK 4 — Rejection referential integrity:
  Every record in DALYREJS must satisfy one of:
    (a) DALYTRAN-CARD-NUM not found in CARDXREF (VSAM status 23)
    (b) XREF-ACCT-ID not found in ACCTDATA (VSAM status 23)
    (c) ACCT-ACTIVE-STATUS ≠ 'Y'

CHECK 5 — No duplicate transactions:
  All TRAN-IDs in TRANSACT (after posting) must be unique.

CHECK 6 — Transaction file growth:
  COUNT(TRANSACT after) = COUNT(TRANSACT before) + COUNT(posted transactions)
```

**Business Rules:**
- Transactions are posted if and only if: card exists in XREF AND account exists AND account is active
- Rejected transactions are written to DALYREJS with additional context (80 bytes of rejection info, LRECL=430 vs 350)
- Account balance is updated: ACCT-CURR-BAL += TRAN-AMT
- Category balance is updated: TRAN-CAT-BAL += TRAN-AMT

---

### INTCALC.jcl — Interest Calculation (CBACT04C)

| Aspect | Detail |
|---|---|
| **Program** | CBACT04C, PARM='2022071800' (date + flags) |
| **Reads** | TCATBALF (category balances), CARDXREF (xref + AIX path), ACCTDATA (account group lookup), DISCGRP (interest rates) |
| **Writes** | TRANSACT/SYSTRAN GDG (interest charge transactions, LRECL=350) |

**Reconciliation Checks:**
```
CHECK 1 — Interest transaction count:
  COUNT(output SYSTRAN records) = COUNT(TCATBALF records with non-zero balance
                                       that have a matching DISCGRP rate)

CHECK 2 — Interest calculation precision:
  For each TCATBALF record:
    Look up: ACCTDATA[acct-id].ACCT-GROUP-ID → group
    Look up: DISCGRP[group, type-cd, cat-cd].DIS-INT-RATE → rate
    interest = TRUNCATE(balance × rate / 1200, 2 decimal places)
  IMPORTANT: Use TRUNCATE (RoundingMode.DOWN), not ROUND.
  COBOL PIC S9(09)V99 truncates — it does not round.

CHECK 3 — Lookup chain integrity:
  Every TCATBALF.TRANCAT-ACCT-ID exists in ACCTDATA
  ACCTDATA.ACCT-GROUP-ID + TCATBALF.TRANCAT-TYPE-CD + TCATBALF.TRANCAT-CD
    must exist in DISCGRP
  If DISCGRP lookup fails, the record should be skipped (not abend)

CHECK 4 — No negative interest rates:
  All DIS-INT-RATE values used must be ≥ 0
```

**Business Rules:**
- Interest is calculated monthly: annual_rate / 12 applied to each category balance
- COBOL truncation semantics: no rounding — use `RoundingMode.DOWN`
- Even 1-cent differences compound across accounts; must match exactly
- PARM field provides the processing date in YYYYMMDD format + 2 flag characters

---

### TRANREPT.jcl — Transaction Report (CBTRN03C)

| Aspect | Detail |
|---|---|
| **Program** | CBTRN03C (Step 10R), preceded by REPRO (Step 05R) and SORT (Step 05R) |
| **Step 05R (REPRO)** | Unload TRANSACT VSAM to sequential backup (GDG) |
| **Step 05R (SORT)** | Filter by date range (PARM-START-DATE to PARM-END-DATE), sort by TRAN-CARD-NUM |
| **Step 10R** | Read sorted/filtered transactions, join with CARDXREF + TRANTYPE + TRANCATG, produce formatted report |
| **Reads** | TRANSACT (filtered), CARDXREF, TRANTYPE, TRANCATG, DATEPARM |
| **Writes** | TRANREPT (formatted report, LRECL=133, GDG) |

**Reconciliation Checks:**
```
CHECK 1 — Date filter accuracy:
  All transactions in report have TRAN-PROC-TS between PARM-START-DATE and PARM-END-DATE
  (Default: 2022-01-01 to 2022-07-06)

CHECK 2 — Sort order:
  Report transactions are ordered by TRAN-CARD-NUM ascending

CHECK 3 — Reference data join completeness:
  Every TRAN-TYPE-CD in report maps to a TRANTYPE.TRAN-TYPE-DESC
  Every TRAN-CAT-CD in report maps to a TRANCATG.TRAN-CAT-TYPE-DESC

CHECK 4 — Transaction count:
  COUNT(transactions in report) = COUNT(TRANSACT records within date range)
```

**Business Rules:**
- Report is a formatted listing of transactions within a date range
- Transactions are sorted by card number for per-card grouping
- Each transaction line includes type description and category description from reference tables

---

### CREASTMT.JCL — Create Statement (CBSTM03A/CBSTM03B)

| Aspect | Detail |
|---|---|
| **Programs** | IDCAMS (Step 01 — define TRXFL), SORT (Step 010 — re-key by card+tran), IDCAMS (Step 020 — REPRO to VSAM), IEFBR14 (Step 030 — delete previous), CBSTM03A (Step 040 — produce statements) |
| **Reads** | TRANSACT VSAM (re-keyed as TRXFL by card_num+tran_id), CARDXREF, ACCTDATA, CUSTDATA |
| **Writes** | STATEMNT.PS (text statement, LRECL=80), STATEMNT.HTML (HTML statement, LRECL=100) |

**Reconciliation Checks:**
```
CHECK 1 — Statement count:
  COUNT(distinct cards in output) = COUNT(distinct XREF-CARD-NUM in CARDXREF
                                         that have transactions in TRANSACT)

CHECK 2 — Transaction completeness:
  For each card with a statement:
    COUNT(transaction lines on statement)
      = COUNT(TRANSACT records for that card number)

CHECK 3 — Customer and account data accuracy:
  Statement header includes:
    - Customer name (from CUSTDATA via XREF-CUST-ID)
    - Account number (from XREF-ACCT-ID)
    - Account balance (from ACCTDATA.ACCT-CURR-BAL)

CHECK 4 — Dual output format:
  Both STATEMNT.PS (text) and STATEMNT.HTML exist and are non-empty

CHECK 5 — SORT re-key accuracy:
  TRXFL records are sorted by TRAN-CARD-NUM (bytes 263–278 of original,
  moved to bytes 1–16) then TRAN-ID
```

**Business Rules:**
- One statement per card that has transactions
- Statements include customer name, address, account balance, and all transactions
- CBSTM03A calls CBSTM03B subroutine for all file I/O
- Uses ALTER/GO TO for control flow — intentionally complex for migration testing
- Output in both plain text and HTML formats

---

### TRANBKP.jcl — Backup Transaction Master

| Aspect | Detail |
|---|---|
| **Purpose** | REPRO TRANSACT VSAM to sequential backup (GDG), then optionally delete records |
| **Reads** | TRANSACT VSAM KSDS |
| **Writes** | TRANSACT.BKUP GDG (sequential backup) |

**Reconciliation Checks:**
```
CHECK 1 — Backup completeness:
  COUNT(records in TRANSACT.BKUP) = COUNT(records in TRANSACT VSAM)

CHECK 2 — Content integrity:
  MD5/SHA checksum of backup matches sequential unload of VSAM
```

---

### COMBTRAN.jcl — Combine Transaction Files

| Aspect | Detail |
|---|---|
| **Purpose** | Sort-merge TRANSACT backup + SYSTRAN (interest transactions) into TRANSACT VSAM |
| **Step 05R (SORT)** | Merge TRANSACT.BKUP(0) + SYSTRAN(0), sort by TRAN-ID |
| **Step 10 (IDCAMS)** | REPRO combined sequential file into TRANSACT VSAM |
| **Reads** | TRANSACT.BKUP (previous backup), SYSTRAN (new interest transactions from INTCALC) |
| **Writes** | TRANSACT.COMBINED GDG, then TRANSACT VSAM |

**Reconciliation Checks:**
```
CHECK 1 — Record count:
  COUNT(TRANSACT.COMBINED) = COUNT(TRANSACT.BKUP) + COUNT(SYSTRAN)

CHECK 2 — No duplicates:
  All TRAN-IDs in combined output are unique

CHECK 3 — Sort order:
  TRANSACT.COMBINED is sorted by TRAN-ID ascending
```

**Business Rules:**
- This job merges interest-charge transactions back into the main transaction master
- Must run after INTCALC (to have SYSTRAN) and TRANBKP (to have BKUP)
- Sequence: TRANBKP → INTCALC → COMBTRAN

---

### PRTCATBL.jcl — Print Category Balance Report

| Aspect | Detail |
|---|---|
| **Purpose** | Unload TCATBALF VSAM to sequential backup, then produce formatted report via SORT |
| **Reads** | TCATBALF VSAM KSDS |
| **Writes** | TCATBALF.BKUP GDG, TCATBALF.REPT (formatted report) |

**Reconciliation Checks:**
```
CHECK 1 — Backup count:
  COUNT(TCATBALF.BKUP) = COUNT(TCATBALF VSAM records)

CHECK 2 — Report completeness:
  All category balance records appear in the report
```

---

### CBEXPORT.jcl — Export Data for Branch Migration

| Aspect | Detail |
|---|---|
| **Program** | CBEXPORT |
| **Reads** | CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA (all 5 core entity files) |
| **Writes** | EXPORT.DATA (combined export file with record-type prefixes) |

**Reconciliation Checks:**
```
CHECK 1 — Entity counts in export:
  COUNT(customer records in export) = COUNT(CUSTDATA records)
  COUNT(account records in export) = COUNT(ACCTDATA records)
  COUNT(card records in export) = COUNT(CARDDATA records)
  COUNT(xref records in export) = COUNT(CARDXREF records)
  COUNT(transaction records in export) = COUNT(TRANSACT records)

CHECK 2 — Referential integrity preserved:
  All cross-references in export data resolve correctly
```

**Business Rules:**
- Export is a denormalized combined file for branch migration
- Each record is prefixed with a record-type indicator
- All entity relationships must be preserved in export

---

### CBIMPORT.jcl — Import Data from Export

| Aspect | Detail |
|---|---|
| **Program** | CBIMPORT |
| **Reads** | EXPORT.DATA (combined export file) |
| **Writes** | CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT, TRANSACT.IMPORT, CARDDATA.IMPORT, ERROUT (errors) |

**Reconciliation Checks:**
```
CHECK 1 — Round-trip integrity:
  CBEXPORT → CBIMPORT produces identical record counts per entity type

CHECK 2 — Error file:
  ERROUT should be empty for valid export data

CHECK 3 — Data fidelity:
  Field-by-field comparison of imported data matches original VSAM data
```

---

## Batch Job Sequencing

The daily batch cycle follows this sequence:

```
1. CLOSEFIL    — Close CICS files for batch access
2. POSTTRAN    — Post daily transactions (CBTRN02C)
3. TRANBKP     — Backup transaction master
4. INTCALC     — Calculate interest charges (CBACT04C)
5. COMBTRAN    — Merge interest transactions into master
6. TRANREPT    — Generate transaction report (CBTRN03C)
7. PRTCATBL    — Print category balance report
8. OPENFIL     — Reopen CICS files
```

**Sequence-Level Reconciliation:**
```
CHECK: TRANSACT count after full cycle
  = TRANSACT count before
  + COUNT(posted from DALYTRAN)
  + COUNT(interest transactions from INTCALC)

CHECK: SUM(ACCT-CURR-BAL after full cycle)
  = SUM(ACCT-CURR-BAL before)
  + SUM(posted DALYTRAN amounts)
  + SUM(interest charges)

CHECK: No orphan transactions
  Every TRAN-CARD-NUM in final TRANSACT exists in CARDXREF
```

Statement generation (CREASTMT) runs on a separate schedule (monthly):
```
1. CLOSEFIL    — Close CICS files
2. CREASTMT    — Generate statements (CBSTM03A/B)
3. OPENFIL     — Reopen CICS files
```
