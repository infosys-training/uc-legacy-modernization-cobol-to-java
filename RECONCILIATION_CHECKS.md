# Reconciliation Checks — CardDemo Batch Jobs

This document defines the reconciliation checks for each batch job in `app/jcl/`. For every job: what it reads, what it writes, what checks must pass, and what business rules it enforces.

---

## 1. VSAM Cluster Definition Jobs

These jobs define and load VSAM KSDS clusters. They run during initial environment setup and are **not** part of the daily batch pipeline.

### ACCTFILE.jcl — Account File Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (3 steps) |
| **Reads** | `AWS.M2.CARDDEMO.ACCTDATA.PS` (sequential backup) |
| **Writes** | `AWS.M2.CARDDEMO.ACCTDAT.VSAM.KSDS` |
| **Record Layout** | CVACT01Y (300 bytes) |

**Reconciliation Checks:**
- [ ] Record count: KSDS count = sequential input count (50)
- [ ] Key integrity: ACCT-ID is unique and ascending
- [ ] Sum check: `SUM(ACCT-CURR-BAL)` = 12,269.00
- [ ] Sum check: `SUM(ACCT-CREDIT-LIMIT)` = 233,711.00
- [ ] All ACCT-ACTIVE-STATUS values are 'Y' or 'N'

---

### CARDFILE.jcl — Card File Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (6 steps) |
| **Reads** | `AWS.M2.CARDDEMO.CARDDATA.PS` |
| **Writes** | `AWS.M2.CARDDEMO.CARDDAT.VSAM.KSDS`, plus AIX (Alternate Index) |
| **Record Layout** | CVACT02Y (150 bytes) |

**Reconciliation Checks:**
- [ ] Record count: KSDS count = input count (50)
- [ ] Key integrity: CARD-NUM is unique (16-byte alpha key)
- [ ] Cross-reference: every CARD-ACCT-ID exists in ACCTFILE
- [ ] All CARD-ACTIVE-STATUS values are 'Y' or 'N'
- [ ] AIX path CARD-ACCT-ID resolves for all records

---

### CUSTFILE.jcl — Customer File Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (3 steps) |
| **Reads** | `AWS.M2.CARDDEMO.CUSTDATA.PS` |
| **Writes** | `AWS.M2.CARDDEMO.CUSTDAT.VSAM.KSDS` |
| **Record Layout** | CVCUS01Y (500 bytes) |

**Reconciliation Checks:**
- [ ] Record count: KSDS count = input count (50)
- [ ] Key integrity: CUST-ID is unique and ascending
- [ ] CUST-FICO-CREDIT-SCORE range: 0–850 for all records
- [ ] All CUST-SSN are 9-digit numerics
- [ ] CUST-DOB-YYYY-MM-DD is valid date format

---

### XREFFILE.jcl — Card Cross-Reference File Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS |
| **Reads** | `AWS.M2.CARDDEMO.CARDXREF.PS` |
| **Writes** | `AWS.M2.CARDDEMO.CXREF.VSAM.KSDS`, plus AIX |
| **Record Layout** | CVACT03Y (50 bytes) |

**Reconciliation Checks:**
- [ ] Record count: KSDS count = input count (50)
- [ ] Key integrity: XREF-CARD-NUM is unique
- [ ] Cross-reference: every XREF-ACCT-ID exists in ACCTFILE
- [ ] Cross-reference: every XREF-CUST-ID exists in CUSTFILE
- [ ] Every XREF-CARD-NUM exists in CARDFILE

---

### TRANFILE.jcl — Transaction File Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (7 steps) |
| **Reads** | `AWS.M2.CARDDEMO.TRANSACT.PS` |
| **Writes** | `AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS`, plus AIX (CARD-NUM, ACCT-ID) |
| **Record Layout** | CVTRA05Y (350 bytes) |

**Reconciliation Checks:**
- [ ] Record count: KSDS count = input count
- [ ] Key integrity: TRAN-ID is unique
- [ ] Sum check: `SUM(TRAN-AMT)` matches pre-load value
- [ ] Cross-reference: TRAN-CARD-NUM exists in CARDXREF

---

### TCATBALF.jcl — Transaction Category Balance Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (3 steps) |
| **Reads** | `AWS.M2.CARDDEMO.TCATBAL.PS` |
| **Writes** | `AWS.M2.CARDDEMO.TCATBAL.VSAM.KSDS` |
| **Record Layout** | CVTRA01Y (50 bytes) |

**Reconciliation Checks:**
- [ ] Record count: 50
- [ ] Key integrity: composite key (ACCT-ID + TYPE-CD + CAT-CD) is unique
- [ ] Cross-reference: TRANCAT-ACCT-ID exists in ACCTFILE
- [ ] Sum check: `SUM(TRAN-CAT-BAL)` = 0.00 (initial state)

---

### DISCGRP.jcl — Disclosure Group Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (3 steps) |
| **Reads** | `AWS.M2.CARDDEMO.DISCGRP.PS` |
| **Writes** | `AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS` |
| **Record Layout** | CVTRA02Y (50 bytes) |

**Reconciliation Checks:**
- [ ] Record count: 51
- [ ] Key integrity: DIS-GROUP-KEY (GROUP-ID + TYPE-CD + CAT-CD) is unique
- [ ] DIS-INT-RATE values are non-negative
- [ ] Cross-reference: DIS-TRAN-TYPE-CD exists in TRANTYPE

---

### TRANTYPE.jcl — Transaction Type Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (3 steps) |
| **Reads** | `AWS.M2.CARDDEMO.TRANTYPE.PS` |
| **Writes** | `AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS` |
| **Record Layout** | CVTRA03Y (60 bytes) |

**Reconciliation Checks:**
- [ ] Record count: 7
- [ ] Key integrity: TRAN-TYPE is unique (2-byte code)
- [ ] All type codes are within expected range (01–07)

---

### TRANCATG.jcl — Transaction Category Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (3 steps) |
| **Reads** | `AWS.M2.CARDDEMO.TRANCATG.PS` |
| **Writes** | `AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS` |
| **Record Layout** | CVTRA04Y (60 bytes) |

**Reconciliation Checks:**
- [ ] Record count: 18
- [ ] Key integrity: composite key (TYPE-CD + CAT-CD) is unique
- [ ] Cross-reference: TRAN-TYPE-CD exists in TRANTYPE

---

## 2. Daily Batch Pipeline Jobs

These run in sequence as the daily processing pipeline.

### POSTTRAN.jcl — Post Daily Transactions

| Attribute | Value |
|-----------|-------|
| **Program** | CBTRN02C (step STEP15) |
| **Reads** | DALYTRAN (daily transaction input), CARDXREF, ACCTFILE, TRANSACT |
| **Writes** | TRANSACT (updated), ACCTFILE (balance updated), DALYREJS (rejected records) |
| **Record Layout** | CVTRA06Y input → CVTRA05Y output |

**Business Rules:**
1. Validate DALYTRAN-CARD-NUM exists in CARDXREF
2. Look up ACCT-ID via XREF, verify account is active
3. Post transaction amount to ACCT-CURR-CYC-DEBIT or ACCT-CURR-CYC-CREDIT
4. Write valid transactions to TRANSACT file
5. Write invalid/rejected records to DALYREJS with rejection reason

**Reconciliation Checks:**
- [ ] `count(DALYTRAN) = count(TRANSACT_new) + count(DALYREJS)`
- [ ] `SUM(TRAN-AMT, TRANSACT_new)` = `SUM(DALYTRAN-AMT) - SUM(rejected_amounts)`
- [ ] All TRANSACT records have valid TRAN-CARD-NUM (exists in XREF)
- [ ] ACCTFILE balance changes = sum of posted transaction amounts per account
- [ ] No duplicate TRAN-ID in TRANSACT after posting
- [ ] DALYREJS records have non-empty rejection codes

---

### INTCALC.jcl — Interest Calculation

| Attribute | Value |
|-----------|-------|
| **Program** | CBACT04C (step STEP15, PARM='2022071800') |
| **Reads** | ACCTFILE, TCATBALF (category balances), CARDXREF, DISCGRP (interest rates) |
| **Writes** | ACCTFILE (updated with interest), TCATBALF (updated balances) |

**Business Rules:**
1. For each account, look up disclosure group by ACCT-GROUP-ID
2. For each transaction category balance, apply interest rate from DISCGRP
3. Interest = TRAN-CAT-BAL × DIS-INT-RATE / 1200 (monthly)
4. Add computed interest to ACCT-CURR-BAL
5. Update TCATBALF with new balances including interest

**Reconciliation Checks:**
- [ ] `SUM(ACCT-CURR-BAL, after) - SUM(ACCT-CURR-BAL, before)` = total interest applied
- [ ] Total interest ≥ 0 (no negative interest unless balance is negative)
- [ ] Record counts unchanged (ACCTFILE and TCATBALF same count before/after)
- [ ] Interest rate lookup: every account's group ID has matching DISCGRP entries
- [ ] No account balance exceeds ACCT-CREDIT-LIMIT after interest (warning, not failure)

---

### TRANREPT.jcl — Transaction Report

| Attribute | Value |
|-----------|-------|
| **Program** | SORT (STEP05R) → CBTRN03C (STEP10R) |
| **Reads** | TRANSACT, CARDXREF, TRANTYPE |
| **Writes** | REPTFILE (report output) |

**Business Rules:**
1. SORT step sorts transactions by TRAN-ID
2. CBTRN03C reads sorted transactions and produces formatted report
3. Report includes: transaction details, card holder info via XREF, type descriptions
4. Page headers, totals, and record counts

**Reconciliation Checks:**
- [ ] All TRANSACT records appear in report (count match)
- [ ] Report total amount = `SUM(TRAN-AMT, TRANSACT)`
- [ ] Report record count = TRANSACT record count
- [ ] Every TRAN-TYPE-CD in report has a valid description from TRANTYPE

---

## 3. Statement Generation Jobs

### CREASTMT (via CBSTM03A/CBSTM03B)

*Note: No dedicated JCL file in `app/jcl/` — likely submitted via CORPT00C online or manual JCL submission.*

| Attribute | Value |
|-----------|-------|
| **Program** | CBSTM03A (calls CBSTM03B for I/O) |
| **Reads** | ACCTFILE, CUSTFILE, CARDXREF, TRANSACT |
| **Writes** | Statement text file, Statement HTML file |

**Business Rules:**
1. For each account, look up customer via XREF → CUST-ID
2. Gather all transactions for the statement period
3. Calculate statement balance: opening balance + credits - debits
4. Generate formatted text statement and HTML version

**Reconciliation Checks:**
- [ ] Number of statements generated = number of active accounts
- [ ] Statement balance = ACCT-CURR-BAL for each account
- [ ] All transactions for statement period are included
- [ ] Customer name/address on statement matches CUSTFILE

---

## 4. Data Reader/Printer Jobs

### READACCT.jcl — Account File Reader

| Attribute | Value |
|-----------|-------|
| **Program** | CBACT01C (step STEP05) |
| **Reads** | ACCTFILE (VSAM KSDS) |
| **Writes** | OUTFILE (fixed-format), ARRYFILE (array-format), VBRCFILE (variable-length) |

**Business Rules:**
1. Read each account record sequentially
2. DISPLAY each field to SYSOUT
3. Format date fields (COBDATFT: YYYY-MM-DD → YYYYMMDD)
4. Zero-debit substitution: if ACCT-CURR-CYC-DEBIT = 0, write 2525.00
5. Write 3 output files in different formats

**Reconciliation Checks:**
- [ ] `count(OUTFILE) = count(ARRYFILE) = count(ACCTFILE)` (50)
- [ ] `count(VBRCFILE)` = 2 × count(ACCTFILE) (100 — two VB records per account)
- [ ] OUTFILE field values match ACCTFILE (except formatted dates and zero-debit substitution)
- [ ] ARRYFILE has 5 array slots per record; slots 4-5 are zeroed
- [ ] No records lost or duplicated

---

### READCARD.jcl — Card File Reader

| Attribute | Value |
|-----------|-------|
| **Program** | CBACT02C (step STEP05) |
| **Reads** | CARDFILE (VSAM KSDS) |
| **Writes** | SYSPRINT (console display) |

**Reconciliation Checks:**
- [ ] Number of DISPLAY lines = number of card records (50)
- [ ] Each card number is 16 characters
- [ ] All CARD-ACCT-ID values are valid

---

### READCUST.jcl — Customer File Reader

| Attribute | Value |
|-----------|-------|
| **Program** | CBCUS01C (step STEP05) |
| **Reads** | CUSTFILE (VSAM KSDS) |
| **Writes** | SYSPRINT (console display) |

**Reconciliation Checks:**
- [ ] Number of DISPLAY lines = number of customer records (50)
- [ ] All CUST-ID values are unique
- [ ] SSN format: 9 digits

---

### READXREF.jcl — Cross-Reference Reader

| Attribute | Value |
|-----------|-------|
| **Program** | CBACT03C (step STEP05) |
| **Reads** | CARDXREF (VSAM KSDS) |
| **Writes** | SYSPRINT (console display) |

**Reconciliation Checks:**
- [ ] Number of DISPLAY lines = number of XREF records (50)
- [ ] All XREF-CARD-NUM exist in CARDFILE
- [ ] All XREF-ACCT-ID exist in ACCTFILE

---

## 5. Data Migration Jobs

### CBEXPORT.jcl — Export Customer Profiles

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (STEP01) → CBEXPORT (STEP02) |
| **Reads** | ACCTFILE, CUSTFILE, CARDFILE, CARDXREF, TRANSACT |
| **Writes** | Export sequential file |

**Business Rules:**
1. Concatenate customer, account, card, and transaction data per customer
2. Write export record with type prefix identifying each sub-record
3. Records are sorted by CUST-ID

**Reconciliation Checks:**
- [ ] Export file size = sum of all input record bytes + header/type prefixes
- [ ] Total customer records in export = CUSTFILE count
- [ ] Total account records in export = ACCTFILE count
- [ ] Checksum: SHA-256 of all key fields matches pre-export computation
- [ ] Every customer in CUSTFILE appears in export

---

### CBIMPORT.jcl — Import Records

| Attribute | Value |
|-----------|-------|
| **Program** | CBIMPORT (step STEP01) |
| **Reads** | Export sequential file |
| **Writes** | ACCTFILE, CUSTFILE, CARDFILE, CARDXREF, TRANSACT |

**Business Rules:**
1. Parse type prefix to determine target VSAM file
2. Validate record format before writing
3. Write to appropriate VSAM file via WRITE/REWRITE
4. Report import counts per entity type

**Reconciliation Checks:**
- [ ] Post-import record counts match export counts per entity
- [ ] `SUM(ACCT-CURR-BAL, post-import)` = `SUM(ACCT-CURR-BAL, export)`
- [ ] All cross-reference integrity checks pass (XREF → ACCT, XREF → CUST, CARD → ACCT)
- [ ] No orphan records (every card has an account, every XREF has both card and customer)

---

## 6. Utility Jobs

### COMBTRAN.jcl — Combine/Sort Transactions

| Attribute | Value |
|-----------|-------|
| **Program** | SORT (STEP05R) → IDCAMS (STEP10) |
| **Reads** | Multiple transaction files |
| **Writes** | Combined sorted transaction file |

**Reconciliation Checks:**
- [ ] Output count = sum of all input counts
- [ ] Output is sorted by TRAN-ID
- [ ] `SUM(TRAN-AMT, output)` = sum of all input TRAN-AMT

---

### TRANBKP.jcl — Transaction Backup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (2 steps: export + verify) |
| **Reads** | TRANSACT (VSAM KSDS) |
| **Writes** | Backup sequential file |

**Reconciliation Checks:**
- [ ] Backup record count = KSDS record count
- [ ] Backup file byte size = record_count × 350
- [ ] SHA-256 of backup matches SHA-256 of KSDS export

---

### DEFGDGB.jcl — Define GDG Base

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS |
| **Purpose** | Define Generation Data Group base for versioned files |

**Reconciliation Checks:**
- [ ] GDG base defined successfully (IDCAMS RC = 0)
- [ ] LIMIT parameter set correctly

---

### DEFGDGD.jcl — Define GDG + Backup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (STEP10, STEP30) + IEBGENER (STEP20, STEP40) |
| **Reads** | TRANTYPE.PS, TRANCATG.PS |
| **Writes** | TRANTYPE.BKUP(+1), TRANCATG.PS.BKUP(+1), refreshed KSDS |

**Reconciliation Checks:**
- [ ] Backup GDG generation created (RC = 0)
- [ ] Backup record counts match source counts
- [ ] Refreshed KSDS has same record count as backup

---

### PRTCATBL.jcl — Print Category Balances

| Attribute | Value |
|-----------|-------|
| **Program** | SORT (STEP10R) |
| **Reads** | TCATBAL |
| **Writes** | Sorted/formatted output |

**Reconciliation Checks:**
- [ ] Output record count = TCATBAL record count (50)
- [ ] Output sorted by composite key

---

### DUSRSECJ.jcl — User Security File Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IEBGENER (STEP01) → IDCAMS (STEP02, STEP03) |
| **Reads** | User security sequential file |
| **Writes** | `AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS` |

**Reconciliation Checks:**
- [ ] KSDS record count = input sequential count
- [ ] User IDs are unique
- [ ] All users have non-empty password fields (CSUSR01Y layout)

---

### WAITSTEP.jcl — Wait Utility

| Attribute | Value |
|-----------|-------|
| **Program** | COBSWAIT → MVSWAIT (assembler) |
| **Purpose** | Insert delay between batch steps |

**Reconciliation Checks:**
- [ ] Program completes with RC = 0
- [ ] No data files modified

---

### CLOSEFIL.jcl / OPENFIL.jcl — CICS File Control

| Attribute | Value |
|-----------|-------|
| **Program** | SDSF |
| **Purpose** | Close/Open CICS-managed VSAM files for batch access |

**Reconciliation Checks:**
- [ ] All target files in expected state after execution
- [ ] No data corruption (record counts unchanged)

---

### DALYREJS.jcl — Daily Rejects File Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS |
| **Purpose** | Define/clear the daily rejects VSAM cluster |

**Reconciliation Checks:**
- [ ] Cluster defined successfully
- [ ] File is empty after initialization

---

### REPTFILE.jcl — Report File Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS |
| **Purpose** | Define the report output cluster |

**Reconciliation Checks:**
- [ ] Cluster defined successfully

---

### CBADMCDJ.jcl — CICS CSD Update

| Attribute | Value |
|-----------|-------|
| **Program** | DFHCSDUP |
| **Purpose** | Define/update CICS resource definitions (CSD) |

**Reconciliation Checks:**
- [ ] All program, transaction, and file definitions loaded (RC ≤ 4)
- [ ] No duplicate resource definitions (or duplicates handled via REPLACE)

---

### ESDSRRDS.jcl — ESDS/RRDS Setup

| Attribute | Value |
|-----------|-------|
| **Program** | IEBGENER → IDCAMS |
| **Purpose** | Define ESDS and RRDS VSAM clusters for user security alternate access |

**Reconciliation Checks:**
- [ ] ESDS loaded with correct record count
- [ ] RRDS accessible via relative record number

---

### TRANIDX.jcl — Transaction Index Rebuild

| Attribute | Value |
|-----------|-------|
| **Program** | IDCAMS (3 steps) |
| **Purpose** | Rebuild alternate indexes on TRANSACT file |

**Reconciliation Checks:**
- [ ] AIX record count matches base KSDS count
- [ ] AIX path accessible for key lookup

---

## 7. Reconciliation Summary Matrix

| Job | Input Count | Output Count | Sum Check | Cross-Ref | Unique Key |
|-----|-------------|--------------|-----------|-----------|------------|
| ACCTFILE | 50 | 50 | BAL, LIMIT | — | ACCT-ID |
| CARDFILE | 50 | 50 | — | ACCT-ID | CARD-NUM |
| CUSTFILE | 50 | 50 | — | — | CUST-ID |
| XREFFILE | 50 | 50 | — | ACCT, CUST | XREF-CARD-NUM |
| POSTTRAN | 300 | ≤300+rejects | AMT | CARD-NUM | TRAN-ID |
| INTCALC | 50 accts | 50 accts | Interest Δ | GROUP-ID | ACCT-ID |
| TRANREPT | varies | varies | AMT total | TYPE-CD | — |
| READACCT | 50 | 50/50/100 | — | — | ACCT-ID |
| CBEXPORT | all files | 1 export | Per-entity | All | CUST-ID |
| CBIMPORT | 1 export | all files | Per-entity | All | Per-entity |
