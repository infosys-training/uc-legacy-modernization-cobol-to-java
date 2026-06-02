# Reconciliation Checks by JCL Batch Job

This document details the reconciliation checks for each batch job in `app/jcl/`. For every job, it specifies: what it reads, what it writes, what reconciliation checks should pass (record counts, totals, referential integrity), and what business rules it enforces.

---

## Daily Batch Pipeline Jobs

These jobs run in sequence during the daily batch cycle.

### 1. POSTTRAN.jcl — Post Daily Transactions

**Program:** CBTRN02C

| Direction | DD Name | Dataset | Record Layout | LRECL |
|-----------|---------|---------|---------------|-------|
| Read | DALYTRAN | CARDDEMO.DALYTRAN.PS | CVTRA05Y (350-byte) | 350 |
| Read | XREFFILE | CARDDEMO.CARDXREF.VSAM.KSDS | CVACT03Y (50-byte) | 50 |
| Read/Write | ACCTFILE | CARDDEMO.ACCTDATA.VSAM.KSDS | CVACT01Y (300-byte) | 300 |
| Read/Write | TRANFILE | CARDDEMO.TRANSACT.VSAM.KSDS | CVTRA05Y (350-byte) | 350 |
| Read/Write | TCATBALF | CARDDEMO.TCATBALF.VSAM.KSDS | CVTRA01Y (50-byte) | 50 |
| Write | DALYREJS | CARDDEMO.DALYREJS(+1) | Rejection record (430-byte) | 430 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Input completeness | `count(DALYTRAN)` | Equals expected daily transaction count |
| 2 | Processing completeness | `posted + rejected = count(DALYTRAN)` | Exact |
| 3 | Account balance update | For each account: `new_bal = old_bal + sum(credits) - sum(debits)` | Tolerance: 0.01 |
| 4 | Category balance update | For each (acct, type, cat): `new_cat_bal = old_cat_bal + tran_amt` | Exact |
| 5 | Transaction master growth | `new_TRANSACT_count = old_TRANSACT_count + posted_count` | Exact |
| 6 | Rejection integrity | Each DALYREJS record has a valid XREF lookup failure reason | All have reason code |
| 7 | Card XREF validation | Every posted transaction's TRAN-CARD-NUM exists in XREFFILE | 0 orphaned |

**Business Rules Enforced:**
- Transactions with unknown card numbers (not in XREFFILE) are rejected to DALYREJS
- Account ACCT-CURR-BAL is updated by transaction amount (credit positive, debit negative)
- Transaction category balance (TCATBALF) is maintained per (account, transaction type, category)
- Posted transactions get TRAN-PROC-TS set to current timestamp

---

### 2. INTCALC.jcl — Interest Calculation

**Program:** CBACT04C  
**Parameter:** PARM='2022071800' (processing date YYYYMMDD + sequence)

| Direction | DD Name | Dataset | Record Layout | LRECL |
|-----------|---------|---------|---------------|-------|
| Read | TCATBALF | CARDDEMO.TCATBALF.VSAM.KSDS | CVTRA01Y (50-byte) | 50 |
| Read | XREFFILE | CARDDEMO.CARDXREF.VSAM.KSDS | CVACT03Y (50-byte) | 50 |
| Read | XREFFIL1 | CARDDEMO.CARDXREF.VSAM.AIX.PATH | Alternate index path | 50 |
| Read/Write | ACCTFILE | CARDDEMO.ACCTDATA.VSAM.KSDS | CVACT01Y (300-byte) | 300 |
| Read | DISCGRP | CARDDEMO.DISCGRP.VSAM.KSDS | CVTRA02Y (50-byte) | 50 |
| Write | TRANSACT | CARDDEMO.SYSTRAN(+1) | CVTRA05Y (350-byte) | 350 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Accounts processed | Unique TRANCAT-ACCT-ID from TCATBALF | Matches account count with non-zero category balances |
| 2 | Interest rate lookup | Each (group, type, cat) has matching DISCGRP rate | 0 missing rates |
| 3 | Interest calculation | `interest = cat_balance * (DIS-INT-RATE / 1200)` | Tolerance: 0.01 per transaction |
| 4 | Account balance update | `new_bal = old_bal + sum(interest_transactions)` | Tolerance: 0.01 |
| 5 | System transactions | One TRANSACT per (account, type, cat) with non-zero balance | Count matches non-zero TCATBALF entries |
| 6 | Transaction IDs | All generated TRAN-IDs are unique | 0 duplicates |

**Business Rules Enforced:**
- Interest is calculated only for accounts with non-zero category balances
- Interest rate comes from DISCGRP keyed by (ACCT-GROUP-ID, TRAN-TYPE-CD, TRAN-CAT-CD)
- Monthly interest = `balance * annual_rate / 1200`
- Generated transactions are written to a new GDG generation (SYSTRAN)

---

### 3. COMBTRAN.jcl — Combine Transactions

**Programs:** SORT, IDCAMS

| Direction | Step | DD Name | Dataset | LRECL |
|-----------|------|---------|---------|-------|
| Read | STEP05R | SORTIN | CARDDEMO.TRANSACT.BKUP(0) + CARDDEMO.SYSTRAN(0) | 350 |
| Write | STEP05R | SORTOUT | CARDDEMO.TRANSACT.COMBINED(+1) | 350 |
| Read | STEP10 | TRANSACT | CARDDEMO.TRANSACT.COMBINED(+1) | 350 |
| Write | STEP10 | TRANVSAM | CARDDEMO.TRANSACT.VSAM.KSDS | 350 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Combined count | `count(BKUP) + count(SYSTRAN) = count(COMBINED)` | Exact |
| 2 | Sort order | COMBINED sorted by TRAN-ID ascending | Verified |
| 3 | No data loss | SHA-256 of all TRAN-IDs in combined = union of both inputs | Exact |
| 4 | VSAM load | `count(TRANVSAM) = count(COMBINED)` | Exact |

**Business Rules Enforced:**
- Transaction master VSAM is rebuilt from sorted combination of backup and system-generated transactions
- Sort key is TRAN-ID (bytes 1-16, character ascending)

---

### 4. TRANREPT.jcl — Transaction Report

**Programs:** REPROC (proc), SORT, CBTRN03C

| Direction | Step | DD Name | Dataset | LRECL |
|-----------|------|---------|---------|-------|
| Read | STEP05R | FILEIN | CARDDEMO.TRANSACT.VSAM.KSDS | 350 |
| Write | STEP05R | FILEOUT | CARDDEMO.TRANSACT.BKUP(+1) | 350 |
| Read | STEP05R | SORTIN | CARDDEMO.TRANSACT.BKUP(+1) | 350 |
| Write | STEP05R | SORTOUT | CARDDEMO.TRANSACT.DALY(+1) | 350 |
| Read | STEP10R | TRANFILE | CARDDEMO.TRANSACT.DALY(+1) | 350 |
| Read | STEP10R | CARDXREF | CARDDEMO.CARDXREF.VSAM.KSDS | 50 |
| Read | STEP10R | TRANTYPE | CARDDEMO.TRANTYPE.VSAM.KSDS | 60 |
| Read | STEP10R | TRANCATG | CARDDEMO.TRANCATG.VSAM.KSDS | 60 |
| Write | STEP10R | TRANREPT | CARDDEMO.TRANREPT(+1) | 133 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Backup completeness | `count(BKUP) = count(TRANSACT)` | Exact |
| 2 | Date filter | All SORTOUT records have TRAN-PROC-DT within date range (2022-01-01 to 2022-07-06) | 0 out-of-range |
| 3 | Sort order | SORTOUT sorted by TRAN-CARD-NUM ascending | Verified |
| 4 | Report record count | Lines in TRANREPT = header + detail + summary lines | Consistent |
| 5 | Report amount total | Sum of amounts in report = sum of TRAN-AMT in filtered input | Tolerance: 0.01 |
| 6 | Card reference | All card numbers in report exist in CARDXREF | 0 missing |
| 7 | Type descriptions | All TRAN-TYPE-CD in report have matching TRANTYPE entries | 0 missing |

**Business Rules Enforced:**
- Transactions are filtered by processing date range (SYMNAMES parameters)
- Sorted by card number for grouped reporting
- Report includes card holder info via XREF lookup
- Transaction type and category descriptions enriched from lookup tables

---

### 5. CREASTMT.JCL — Create Customer Statements

**Programs:** IDCAMS, SORT, CBSTM03A (calls CBSTM03B)

| Direction | Step | DD Name | Dataset | LRECL |
|-----------|------|---------|---------|-------|
| Read | STEP010 | SORTIN | CARDDEMO.TRANSACT.VSAM.KSDS | 350 |
| Write | STEP010 | SORTOUT | CARDDEMO.TRXFL.SEQ | 350 |
| Write | STEP020 | OUTFILE | CARDDEMO.TRXFL.VSAM.KSDS | 350 |
| Read | STEP040 | TRNXFILE | CARDDEMO.TRXFL.VSAM.KSDS | 350 |
| Read | STEP040 | XREFFILE | CARDDEMO.CARDXREF.VSAM.KSDS | 50 |
| Read | STEP040 | ACCTFILE | CARDDEMO.ACCTDATA.VSAM.KSDS | 300 |
| Read | STEP040 | CUSTFILE | CARDDEMO.CUSTDATA.VSAM.KSDS | 500 |
| Write | STEP040 | STMTFILE | CARDDEMO.STATEMNT.PS | 80 |
| Write | STEP040 | HTMLFILE | CARDDEMO.STATEMNT.HTML | 100 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Transaction sort | TRXFL sorted by (TRAN-CARD-NUM, TRAN-ID) | Verified |
| 2 | VSAM load | `count(TRXFL.VSAM) = count(TRXFL.SEQ)` | Exact |
| 3 | Cards processed | Unique cards in XREFFILE = statements generated | Exact |
| 4 | Statement completeness | Each card with transactions has a statement | 0 missing |
| 5 | Statement balance | Each statement's running total matches account current balance | Tolerance: 0.01 |
| 6 | HTML generation | HTMLFILE record count > 0 and matches STMTFILE count proportionally | Non-zero |
| 7 | Customer info | Every statement includes correct customer name and address | Validated |

**Business Rules Enforced:**
- One statement per card (not per account or customer)
- Transactions grouped and sorted by card number
- Statement includes: customer info (from CUSTFILE via XREF), account summary (from ACCTFILE), transaction detail
- Both text (PS) and HTML formats generated

---

## Data Reader/Writer Jobs

These jobs read VSAM master files and produce sequential outputs for verification.

### 6. READACCT.jcl — Read Account Master

**Program:** CBACT01C

| Direction | Step | DD Name | Dataset | LRECL |
|-----------|------|---------|---------|-------|
| Read | STEP05 | ACCTFILE | CARDDEMO.ACCTDATA.VSAM.KSDS | 300 |
| Write | STEP05 | OUTFILE | CARDDEMO.ACCTDATA.PSCOMP | 107 |
| Write | STEP05 | ARRYFILE | CARDDEMO.ACCTDATA.ARRYPS | 110 |
| Write | STEP05 | VBRCFILE | CARDDEMO.ACCTDATA.VBPS | 84 (VB) |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Output record count | `count(OUTFILE) = count(ACCTFILE)` | Exact |
| 2 | Array record count | `count(ARRYFILE) = count(ACCTFILE)` | Exact |
| 3 | VB record count | `count(VBRCFILE) = 2 * count(ACCTFILE)` | Exact (2 records per input) |
| 4 | OUTFILE size | `file_size = count * 107` | Exact |
| 5 | ARRYFILE size | `file_size = count * 110` | Exact |
| 6 | Debit substitution | If ACCT-CURR-CYC-DEBIT = 0 in input, OUT-ACCT-CURR-CYC-DEBIT = 2525.00 in COMP-3 | Verified |
| 7 | Balance preservation | ACCT-CURR-BAL in OUTFILE (DISPLAY) = ACCT-CURR-BAL in input | Exact |

**Business Rules Enforced:**
- OUTFILE: Mixed DISPLAY and COMP-3 format (107 bytes), with debit substitution rule (zero -> 2525.00)
- ARRYFILE: Array structure with 5 entries per record (3 populated, 2 zero-initialized), hardcoded debit values (1005.00, 1525.00, -2500.00)
- VBRCFILE: Variable-length records, 2 per input record (VB1 = 12 bytes, VB2 = 39 bytes)

---

### 7. READCARD.jcl — Read Card Master

**Program:** CBACT02C

| Direction | DD Name | Dataset | LRECL |
|-----------|---------|---------|-------|
| Read | CARDFILE | CARDDEMO.CARDDATA.VSAM.KSDS | 150 |
| Write | OUTFILE | CARDDEMO.CARDDATA.PS | 150 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Record count | `count(OUTFILE) = count(CARDFILE)` | Exact |
| 2 | File size | `file_size = count * 150` | Exact |
| 3 | Content preservation | Byte-for-byte match of KSDS sequential dump | SHA-256 match |

**Business Rules Enforced:**
- Simple sequential read of KSDS and write to flat file — no transformation
- Abnormal termination via CEE3ABD on file errors

---

### 8. READCUST.jcl — Read Customer Master

**Program:** CBCUS01C

| Direction | DD Name | Dataset | LRECL |
|-----------|---------|---------|-------|
| Read | CUSTFILE | CARDDEMO.CUSTDATA.VSAM.KSDS | 500 |
| Write | OUTFILE | CARDDEMO.CUSTDATA.PS | 500 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Record count | `count(OUTFILE) = count(CUSTFILE)` | Exact |
| 2 | File size | `file_size = count * 500` | Exact |
| 3 | Content preservation | Byte-for-byte match | SHA-256 match |

**Business Rules Enforced:**
- Simple sequential read and write — no transformation

---

### 9. READXREF.jcl — Read Cross-Reference

**Program:** CBACT03C

| Direction | DD Name | Dataset | LRECL |
|-----------|---------|---------|-------|
| Read | XREFFILE | CARDDEMO.CARDXREF.VSAM.KSDS | 50 |
| Write | OUTFILE | CARDDEMO.CARDXREF.PS | 50 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Record count | `count(OUTFILE) = count(XREFFILE)` | Exact |
| 2 | File size | `file_size = count * 50` | Exact |
| 3 | Content preservation | Byte-for-byte match | SHA-256 match |

**Business Rules Enforced:**
- Simple sequential read and write — no transformation

---

## Export/Import Jobs

### 10. CBEXPORT.jcl — Export Data for Branch Migration

**Program:** CBEXPORT

| Direction | Step | DD Name | Dataset | LRECL |
|-----------|------|---------|---------|-------|
| Read | STEP02 | CUSTFILE | CARDDEMO.CUSTDATA.VSAM.KSDS | 500 |
| Read | STEP02 | ACCTFILE | CARDDEMO.ACCTDATA.VSAM.KSDS | 300 |
| Read | STEP02 | XREFFILE | CARDDEMO.CARDXREF.VSAM.KSDS | 50 |
| Read | STEP02 | TRANSACT | CARDDEMO.TRANSACT.VSAM.KSDS | 350 |
| Read | STEP02 | CARDFILE | CARDDEMO.CARDDATA.VSAM.KSDS | 150 |
| Write | STEP02 | EXPFILE | CARDDEMO.EXPORT.DATA | 500 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Total export records | `sum(customers + accounts + xrefs + transactions + cards)` | Exact |
| 2 | Record type counts | Count by EXPORT-REC-TYPE matches source file counts | Exact per type |
| 3 | Sequence continuity | EXPORT-SEQUENCE-NUM is monotonically increasing with no gaps | Verified |
| 4 | Branch ID consistency | All records have same EXPORT-BRANCH-ID | Consistent |
| 5 | Timestamp ordering | EXPORT-TIMESTAMP is non-decreasing | Verified |
| 6 | COMP-3 accuracy | EXP-ACCT-CURR-BAL (COMP-3) matches source ACCT-CURR-BAL (DISPLAY) | Exact |
| 7 | COMP accuracy | EXP-TRAN-MERCHANT-ID (COMP) matches source TRAN-MERCHANT-ID (DISPLAY) | Exact |

**Business Rules Enforced:**
- Multi-record type export using CVEXPORT.cpy REDEFINES structure
- Record types: C=Customer, A=Account, X=Cross-Reference, T=Transaction, D=Card
- COMP-3 fields for monetary values (storage optimization)
- COMP fields for integer IDs (storage optimization)
- Keyed by EXPORT-BRANCH-ID (bytes 28-31) for KSDS organization

---

### 11. CBIMPORT.jcl — Import Data from Export File

**Program:** CBIMPORT

| Direction | DD Name | Dataset | LRECL |
|-----------|---------|---------|-------|
| Read | EXPFILE | CARDDEMO.EXPORT.DATA | 500 |
| Write | CUSTOUT | CARDDEMO.CUSTDATA.IMPORT | 500 |
| Write | ACCTOUT | CARDDEMO.ACCTDATA.IMPORT | 300 |
| Write | XREFOUT | CARDDEMO.CARDXREF.IMPORT | 50 |
| Write | TRNXOUT | CARDDEMO.TRANSACT.IMPORT | 350 |
| Write | ERROUT | CARDDEMO.IMPORT.ERRORS | 132 |

**Reconciliation Checks:**

| # | Check | Formula | Pass Criteria |
|---|-------|---------|---------------|
| 1 | Round-trip integrity | `exported_count = imported_count + error_count` | Exact |
| 2 | Per-type split | CUSTOUT = customer exports, ACCTOUT = account exports, etc. | Exact per type |
| 3 | Error documentation | All ERROUT records have valid error code and description | Validated |
| 4 | Data fidelity | Imported record field values match original source values | Exact |
| 5 | LRECL compliance | CUSTOUT=500, ACCTOUT=300, XREFOUT=50, TRNXOUT=350 | Exact |
| 6 | COMP-3 decode | Imported monetary fields correctly decode from COMP-3 to DISPLAY | Exact |

**Business Rules Enforced:**
- Reverse of CBEXPORT: splits multi-type EXPFILE into normalized entity files
- Validates record type codes during split
- Invalid/unrecognized records go to ERROUT (LRECL=132)
- Output file formats match original VSAM record layouts

---

## Infrastructure Jobs

### 12. CLOSEFIL.jcl — Close CICS Files

**Program:** SDSF (system utility)

| Action | File | CICS Name |
|--------|------|-----------|
| Close | Transaction Master | TRANSACT |
| Close | Card Cross-Reference | CCXREF |
| Close | Account Master | ACCTDAT |
| Close | XREF Alternate Index | CXACAIX |
| Close | User Security | USRSEC |

**Reconciliation Checks:**

| # | Check | Pass Criteria |
|---|-------|---------------|
| 1 | All files closed | CEMT INQUIRE shows CLOSED status for all 5 files |
| 2 | No active transactions | No CICS tasks holding locks on these files |

**Business Rules Enforced:**
- Files must be closed before batch processing to prevent concurrent access conflicts
- Always runs before the batch pipeline (POSTTRAN, INTCALC, etc.)

---

### 13. OPENFIL.jcl — Open CICS Files

**Program:** SDSF (system utility)

| Action | File | CICS Name |
|--------|------|-----------|
| Open | Transaction Master | TRANSACT |
| Open | Card Cross-Reference | CCXREF |
| Open | Account Master | ACCTDAT |
| Open | XREF Alternate Index | CXACAIX |
| Open | User Security | USRSEC |

**Reconciliation Checks:**

| # | Check | Pass Criteria |
|---|-------|---------------|
| 1 | All files opened | CEMT INQUIRE shows OPEN/ENABLED status for all 5 files |
| 2 | Online transactions resume | At least one successful CICS inquiry after open |

**Business Rules Enforced:**
- Files must be reopened after batch processing to resume online CICS operations
- Always runs after the batch pipeline completes

---

## VSAM Cluster Definition Jobs

These jobs use IDCAMS to define, delete, and load VSAM clusters. They are setup/maintenance jobs, not data processing jobs.

### 14. ACCTFILE.jcl — Define Account VSAM Cluster
- **Steps:** IDCAMS DELETE + DEFINE CLUSTER + REPRO (load from sequential)
- **Cluster:** KSDS, KEY(11,0), RECORDSIZE(300,300)
- **Check:** `count(KSDS) = count(sequential source)`

### 15. CARDFILE.jcl — Define Card VSAM Cluster
- **Steps:** SDSF CLOSE + IDCAMS DELETE + DEFINE + REPRO + AIX DEFINE + AIX BUILD + SDSF OPEN
- **Cluster:** KSDS, KEY(16,0), RECORDSIZE(150,150)
- **Check:** `count(KSDS) = count(sequential source)`, AIX successfully built

### 16. CUSTFILE.jcl — Define Customer VSAM Cluster
- **Steps:** SDSF CLOSE + IDCAMS DELETE + DEFINE + REPRO + SDSF OPEN
- **Cluster:** KSDS, KEY(9,0), RECORDSIZE(500,500)
- **Check:** `count(KSDS) = count(sequential source)`

### 17. XREFFILE.jcl — Define Cross-Reference VSAM Cluster
- **Steps:** IDCAMS DELETE + DEFINE + REPRO + AIX DEFINE + AIX BUILD + PATH DEFINE
- **Cluster:** KSDS, KEY(16,0), RECORDSIZE(50,50)
- **Check:** `count(KSDS) = count(sequential source)`, AIX path accessible

### 18. TRANFILE.jcl — Define Transaction Master VSAM Cluster
- **Steps:** SDSF CLOSE + IDCAMS DELETE + DEFINE + REPRO + AIX DEFINE + BUILD + PATH + SDSF OPEN
- **Cluster:** KSDS, KEY(16,0), RECORDSIZE(350,350)
- **Check:** `count(KSDS) = count(sequential source)`, AIX path accessible

### 19. DISCGRP.jcl — Define Disclosure Group VSAM Cluster
- **Cluster:** KSDS, KEY(16,0), RECORDSIZE(50,50)
- **Check:** `count(KSDS) = count(sequential source)`

### 20. TCATBALF.jcl — Define Transaction Category Balance VSAM Cluster
- **Cluster:** KSDS, KEY(17,0), RECORDSIZE(50,50)
- **Check:** `count(KSDS) = count(sequential source)`

### 21. TRANCATG.jcl — Define Transaction Category VSAM Cluster
- **Cluster:** KSDS, KEY(6,0), RECORDSIZE(60,60)
- **Check:** `count(KSDS) = count(sequential source)`

### 22. TRANTYPE.jcl — Define Transaction Type VSAM Cluster
- **Cluster:** KSDS, KEY(2,0), RECORDSIZE(60,60)
- **Check:** `count(KSDS) = count(sequential source)`

---

## Utility & Support Jobs

### 23. TRANBKP.jcl — Backup Transaction Master
- **Steps:** REPROC (VSAM → sequential), IDCAMS DELETE + REDEFINE
- **Check:** `count(BKUP) = count(TRANSACT)` before delete, SHA-256 match

### 24. PRTCATBL.jcl — Print Transaction Category Balance
- **Steps:** REPROC (VSAM → sequential), SORT (format for print)
- **Check:** `count(output) = count(TCATBALF)`, sorted correctly

### 25. DALYREJS.jcl — Define GDG for Daily Rejections
- **Steps:** IDCAMS DEFINE GDG BASE
- **Check:** GDG base defined with correct LIMIT

### 26. DEFGDGB.jcl — Define GDG Bases
- **Steps:** IDCAMS DEFINE GDG for TRANSACT.BKUP, SYSTRAN
- **Check:** GDG bases defined with correct LIMIT and SCRATCH attributes

### 27. DEFGDGD.jcl — Define DB2 GDG
- **Steps:** IDCAMS DEFINE GDG + IEBGENER model DSCB creation for 3 GDG bases
- **Check:** GDG bases and model DSCBs defined

### 28. REPTFILE.jcl — Define Report GDG
- **Steps:** IDCAMS DEFINE GDG for TRANREPT
- **Check:** GDG base defined

### 29. DUSRSECJ.jcl — Define User Security File
- **Steps:** IEFBR14 (predelete) + IEBGENER (sequential load) + IDCAMS (VSAM DEFINE + REPRO)
- **Check:** Security file loaded with initial user records

### 30. ESDSRRDS.jcl — Define ESDS/RRDS Files
- **Steps:** IEFBR14 + IEBGENER + IDCAMS (multiple DEFINE CLUSTER for ESDS and RRDS types)
- **Check:** All clusters defined successfully

### 31. TRANIDX.jcl — Define Transaction AIX
- **Steps:** IDCAMS DEFINE AIX + BLDINDEX + DEFINE PATH
- **Check:** AIX built with correct alternate key, path accessible

### 32. WAITSTEP.jcl — Wait Step
- **Program:** COBSWAIT (calls MVSWAIT assembler)
- **Check:** Job completes within timeout, return code = 0

### 33. CBADMCDJ.jcl — CICS CSD Definitions
- **Program:** DFHCSDUP
- **Check:** All CICS resource definitions loaded (MAPSET, PROGRAM, FILE, TRANSACTION)

### 34. DEFCUST.jcl — Define Customer Data File (alternate)
- **Steps:** IDCAMS DEFINE CLUSTER for customer data
- **Check:** Cluster defined successfully

### 35-36. INTRDRJ1.JCL / INTRDRJ2.JCL — Internal Reader Jobs
- **Program:** IDCAMS, IEBGENER
- **Purpose:** Submit jobs via internal reader for CICS report generation
- **Check:** Jobs submitted and executed, return code <= 4

### 37. FTPJCL.JCL — FTP Transfer
- **Program:** FTP
- **Purpose:** Transfer files between systems
- **Check:** FTP transfer completes with RC=0

### 38. TXT2PDF1.JCL — Convert Text to PDF
- **Program:** IKJEFT1B (TSO batch)
- **Check:** PDF file generated, non-zero size

---

## Cross-Job Reconciliation Matrix

This matrix shows which data files are shared between jobs and must remain consistent.

| File | Written By | Read By | Consistency Check |
|------|-----------|---------|-------------------|
| ACCTFILE | POSTTRAN, INTCALC, CBIMPORT | READACCT, INTCALC, CREASTMT, CBEXPORT, POSTTRAN | Balance checksum after each writer |
| CARDFILE | CBIMPORT | READCARD, CBEXPORT | Count after load |
| CUSTFILE | CBIMPORT | READCUST, CREASTMT, CBEXPORT | Count after load |
| CARDXREF | CBIMPORT | READXREF, POSTTRAN, INTCALC, TRANREPT, CREASTMT, CBEXPORT | Count after load, RI against ACCTFILE and CUSTFILE |
| TRANSACT | POSTTRAN, COMBTRAN | TRANREPT, CREASTMT, CBEXPORT, TRANBKP | Count and TRAN-AMT sum after each update |
| TCATBALF | POSTTRAN | INTCALC, PRTCATBL | CAT-BAL sum after POSTTRAN |
| DALYTRAN | External feed | POSTTRAN | Count before processing |
| DISCGRP | Initial load | INTCALC | Rate table completeness |
| EXPFILE | CBEXPORT | CBIMPORT | Round-trip count parity |
