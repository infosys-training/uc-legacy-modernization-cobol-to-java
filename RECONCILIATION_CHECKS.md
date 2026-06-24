# CardDemo Reconciliation Checks

Per-JCL-job documentation of inputs, outputs, reconciliation checks, and business rules enforced.

---

## 1. Daily Batch Pipeline Jobs

These four jobs execute in strict sequence. Each job's output feeds the next.

### 1.1 POSTTRAN.jcl — Post Daily Transactions

| Attribute | Detail |
|-----------|--------|
| **Program** | CBTRN02C |
| **Purpose** | Post daily transactions to master file, update account balances and category balances |

**Reads:**

| DD Name | Dataset | Record Layout | Description |
|---------|---------|---------------|-------------|
| DALYTRAN | AWS.M2.CARDDEMO.DALYTRAN.PS | CVTRA06Y (350 bytes) | Daily transaction input file (300 records) |
| TRANFILE | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | CVTRA05Y (350 bytes) | Transaction master VSAM (insert target) |
| XREFFILE | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | CVACT03Y (36 bytes) | Card-to-account cross-reference lookup |
| ACCTFILE | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | CVACT01Y (300 bytes) | Account master (balance update target) |
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | CVTRA01Y (51 bytes) | Transaction category balance (accumulator) |

**Writes:**

| DD Name | Dataset | Description |
|---------|---------|-------------|
| TRANFILE | TRANSACT.VSAM.KSDS | New transaction records inserted |
| ACCTFILE | ACCTDATA.VSAM.KSDS | ACCT-CURR-BAL updated per transaction |
| TCATBALF | TCATBALF.VSAM.KSDS | Category balances accumulated |
| DALYREJS | AWS.M2.CARDDEMO.DALYREJS(+1) | Rejected transactions (GDG) |

**Reconciliation Checks:**

```
PRE:
  A = count(DALYTRAN)                    — expected: 300
  B = count(TRANSACT)                    — before posting
  C = sum(ACCT-CURR-BAL) over ACCTFILE   — total balance before
  D = sum(DALYTRAN-AMT)                  — total daily amount

POST:
  count(TRANSACT) = B + (A - count(DALYREJS))
  sum(ACCT-CURR-BAL) = C + sum(posted purchase amounts) - sum(posted payment amounts)
  sum(TRAN-CAT-BAL changes) = sum(DALYTRAN-AMT) grouped by (TYPE-CD, CAT-CD)
  count(DALYREJS) + count(posted) = A
  Every posted TRAN-ID is unique in TRANSACT
  Every DALYTRAN-CARD-NUM exists in XREFFILE
```

**Business Rules Enforced:**
- Card number must exist in XREFFILE (reject if orphaned)
- Account must be active (ACCT-ACTIVE-STATUS = 'Y')
- Transaction amount applied to ACCT-CURR-BAL based on type: purchases increase balance, payments decrease
- Category balance in TCATBALF accumulated by ACCT-ID + TYPE-CD + CAT-CD composite key
- Rejected transactions written to DALYREJS with reason code

---

### 1.2 INTCALC.jcl — Interest Calculation

| Attribute | Detail |
|-----------|--------|
| **Program** | CBACT04C |
| **PARM** | `'2022071800'` (processing date YYYYMMDD + '00') |
| **Purpose** | Calculate and apply interest charges to accounts based on disclosure group rates |

**Reads:**

| DD Name | Dataset | Record Layout | Description |
|---------|---------|---------------|-------------|
| TCATBALF | TCATBALF.VSAM.KSDS | CVTRA01Y (51 bytes) | Category balances per account |
| XREFFILE | CARDXREF.VSAM.KSDS | CVACT03Y (36 bytes) | Card-to-account mapping |
| XREFFIL1 | CARDXREF.VSAM.KSDS | CVACT03Y | Alternate index path |
| ACCTFILE | ACCTDATA.VSAM.KSDS | CVACT01Y (300 bytes) | Account master |
| DISCGRP | DISCGRP.VSAM.KSDS | CVTRA02Y (50 bytes) | Interest rate by group + type + category |

**Writes:**

| DD Name | Dataset | Description |
|---------|---------|-------------|
| ACCTFILE | ACCTDATA.VSAM.KSDS | ACCT-CURR-BAL updated with interest |
| TRANSACT | TRANSACT.VSAM.KSDS (NEW) | System-generated interest transactions |

**Reconciliation Checks:**

```
PRE:
  A = sum(ACCT-CURR-BAL) over ACCTFILE
  B = count(accounts with ACCT-CURR-BAL > 0 AND ACCT-ACTIVE-STATUS = 'Y')

POST:
  sum(ACCT-CURR-BAL) = A + sum(interest charges applied)
  For each account:
    interest = sum(TRAN-CAT-BAL(acct) * DIS-INT-RATE(group,type,cat) / 1200)
  Every account with positive balance has interest applied
  Zero/negative balance accounts: no interest charge
  Every ACCT-GROUP-ID matches a DIS-ACCT-GROUP-ID in DISCGRP
  Generated interest transactions have unique TRAN-IDs
```

**Business Rules Enforced:**
- Interest rate = `DIS-INT-RATE / 1200` (annual rate → monthly, stored as S9(04)V99)
- Interest applied per category: each (ACCT-ID, TYPE-CD, CAT-CD) in TCATBALF × matching rate in DISCGRP
- Only active accounts (status 'Y') with positive category balances
- Rate lookup by composite key: ACCT-GROUP-ID + TRAN-TYPE-CD + TRAN-CAT-CD
- System-generated interest transactions written to TRANSACT

---

### 1.3 CREASTMT.jcl — Statement Generation

| Attribute | Detail |
|-----------|--------|
| **Programs** | Step 1: IDCAMS (cluster setup), Step 2: IDCAMS (REPRO to seq), Step 3: SORT, Step 4: CBSTM03A → CBSTM03B |
| **Purpose** | Generate customer statements (text + HTML) for all accounts |

**Step Sequence:**
1. DELDEF01 — IDCAMS: Delete/define TRXFL.VSAM.KSDS and sequential work files
2. STEP01 — IDCAMS: REPRO TRANSACT to sequential + REPRO to temp VSAM
3. STEP05R — SORT: Sort transactions by TRAN-CARD-NUM ascending
4. STEP10R — CBSTM03A: Generate statements (calls CBSTM03B for I/O)

**Reads (CBSTM03A):**

| DD Name | Dataset | Record Layout | Description |
|---------|---------|---------------|-------------|
| XREFFILE | CARDXREF.VSAM.KSDS | CVACT03Y | Card-to-account-to-customer mapping |
| CUSTFILE | CUSTDATA.VSAM.KSDS | CVCUS01Y (500 bytes) | Customer name/address for statements |
| ACCTFILE | ACCTDATA.VSAM.KSDS | CVACT01Y (300 bytes) | Account balance/limits |
| TRNXFILE | Sorted transaction sequential file | CVTRA05Y (350 bytes) | Transactions sorted by card number |

**Writes:**

| DD Name | Dataset | Description |
|---------|---------|-------------|
| STMTFILE | Statement output (text) | Formatted text statements |
| HTMLFILE | Statement output (HTML) | HTML-formatted statements |

**Reconciliation Checks:**

```
POST:
  count(statements) = count(distinct XREF-ACCT-ID in XREFFILE)
  For each statement:
    sum(transaction amounts on statement) = closing balance - opening balance
    page totals sum to account total
    account totals sum to grand total
  Every XREF card number appears in at least one statement
  All CUST-ID lookups succeed (no orphaned XREF records)
  All ACCT-ID lookups succeed
  Grand total = sum(all transaction amounts in TRNXFILE)
```

**Business Rules Enforced:**
- One statement per account (accounts may have multiple cards via XREFFILE)
- Transactions grouped by card number within each statement
- Customer name/address from CUSTFILE for statement header
- Account summary: current balance, credit limit, payment due
- Statement includes text and HTML versions (97 WRITE operations in CBSTM03A)

---

### 1.4 TRANREPT.jcl — Transaction Report

| Attribute | Detail |
|-----------|--------|
| **Steps** | STEP05R: REPROC (unload TRANSACT), STEP05R: SORT by TRAN-CARD-NUM, STEP10R: CBTRN03C |
| **Purpose** | Generate daily transaction report with totals by account and grand total |

**Reads (CBTRN03C):**

| DD Name | Dataset | Record Layout | Description |
|---------|---------|---------------|-------------|
| TRNXFILE | Sorted transaction sequential | CVTRA05Y (350 bytes) | Transactions sorted by card |
| XREFFILE | CARDXREF.VSAM.KSDS | CVACT03Y | Card-to-account mapping |
| TRANTYPE | TRANTYPE.VSAM.KSDS | CVTRA03Y (60 bytes) | Transaction type descriptions |
| TRANCATG | TRANCATG.VSAM.KSDS | CVTRA04Y (60 bytes) | Transaction category descriptions |

**Writes:**

| DD Name | Dataset | Description |
|---------|---------|-------------|
| REPTFILE | AWS.M2.CARDDEMO.REPTFILE(+1) | Report file (GDG, formatted text) |

**Reconciliation Checks:**

```
POST:
  count(detail lines in report) = count(TRANSACT records)
  sum(report TRAN-REPORT-AMT) = sum(TRAN-AMT in TRANSACT)
  For each page:
    REPT-PAGE-TOTAL = sum(transaction amounts on that page)
  For each account:
    REPT-ACCOUNT-TOTAL = sum(page totals for that account)
  REPT-GRAND-TOTAL = sum(all account totals)
  Every TRAN-TYPE-CD maps to a valid TRANTYPE record
  Every TRAN-CAT-CD maps to a valid TRANCATG record
```

**Business Rules Enforced:**
- Report layout per CVTRA07Y: header, column headers, detail lines, page/account/grand totals
- Amount formatted as `-ZZZ,ZZZ,ZZZ.ZZ` (signed, comma-separated)
- Transactions sorted by card number, then by account
- Type and category descriptions looked up from reference files

---

## 2. Data Export/Import Jobs

### 2.1 CBEXPORT.jcl — Data Export for Branch Migration

| Attribute | Detail |
|-----------|--------|
| **Steps** | STEP01: IDCAMS (define export cluster), STEP02: CBEXPORT |
| **Purpose** | Export all VSAM entity data into a single multi-record sequential file |

**Reads:**

| DD Name | Dataset | Record Layout | Description |
|---------|---------|---------------|-------------|
| CUSTFILE | CUSTDATA.VSAM.KSDS | CVCUS01Y | Customer records |
| ACCTFILE | ACCTDATA.VSAM.KSDS | CVACT01Y | Account records |
| XREFFILE | CARDXREF.VSAM.KSDS | CVACT03Y | Cross-reference records |
| TRANSACT | TRANSACT.VSAM.KSDS | CVTRA05Y | Transaction records |
| CARDFILE | CARDDATA.VSAM.KSDS | CVACT02Y | Card records |

**Writes:**

| DD Name | Dataset | Description |
|---------|---------|-------------|
| EXPFILE | EXPORT.DATA (VSAM cluster) | Multi-record export file (CVEXPORT layout, 500 bytes) |

**Reconciliation Checks:**

```
POST:
  count(EXPFILE records where EXPORT-REC-TYPE = 'C') = count(CUSTFILE)
  count(EXPFILE records where EXPORT-REC-TYPE = 'A') = count(ACCTFILE)
  count(EXPFILE records where EXPORT-REC-TYPE = 'X') = count(XREFFILE)
  count(EXPFILE records where EXPORT-REC-TYPE = 'T') = count(TRANSACT)
  count(EXPFILE records where EXPORT-REC-TYPE = 'D') = count(CARDFILE)
  total EXPFILE records = sum of all above
  EXPORT-SEQUENCE-NUM is monotonically increasing
  All EXPORT-TIMESTAMP values are within the job execution window
```

**Business Rules Enforced:**
- Record type codes: C=Customer, A=Account, X=Cross-ref, T=Transaction, D=Card
- CVEXPORT layout uses COMP/COMP-3 for numeric fields (different byte widths than DISPLAY)
- Sequence number tracks export order for import verification
- Branch ID and region code populated per configuration

---

### 2.2 CBIMPORT.jcl — Data Import with Validation

| Attribute | Detail |
|-----------|--------|
| **Program** | CBIMPORT |
| **Purpose** | Read multi-record export file and split into normalized entity files |

**Reads:**

| DD Name | Dataset | Record Layout | Description |
|---------|---------|---------------|-------------|
| EXPFILE | EXPORT.DATA | CVEXPORT (500 bytes) | Multi-record export file |

**Writes:**

| DD Name | Dataset | Description |
|---------|---------|-------------|
| CUSTOUT | Customer output (PS) | Normalized customer records |
| ACCTOUT | Account output (PS) | Normalized account records |
| XREFOUT | Cross-reference output (PS) | Normalized XREF records |
| TRNXOUT | Transaction output (PS) | Normalized transaction records |
| ERROUT | Error/rejection output (PS) | Records that failed validation |

**Reconciliation Checks:**

```
POST:
  count(CUSTOUT) + count(ACCTOUT) + count(XREFOUT) + count(TRNXOUT) + count(ERROUT)
    = count(EXPFILE)
  count(ERROUT) = 0 for clean imports
  For each entity output file:
    Record count matches EXPORT-REC-TYPE count in EXPFILE
  Cross-reference integrity:
    Every XREFOUT.ACCT-ID exists in ACCTOUT
    Every XREFOUT.CUST-ID exists in CUSTOUT
  Monetary field sums: sum(ACCTOUT.CURR-BAL) = sum(EXPFILE accounts' CURR-BAL)
```

**Business Rules Enforced:**
- Record type routing based on EXPORT-REC-TYPE
- COMP/COMP-3 → DISPLAY format conversion during import
- Referential integrity validation (orphan detection → ERROUT)
- Sequence number gap detection

---

## 3. Utility/Read Jobs

### 3.1 READACCT.jcl — Account File Read/Transform

| Attribute | Detail |
|-----------|--------|
| **Steps** | PREDEL: Delete prior outputs, STEP05: CBACT01C |
| **Purpose** | Read account VSAM file, produce 3 output files with transformed data |

**Reads:**

| DD Name | Dataset | Record Layout | Description |
|---------|---------|---------------|-------------|
| ACCTFILE | ACCTDATA.VSAM.KSDS | CVACT01Y (300 bytes) | Account master |

**Writes:**

| DD Name | Dataset | Description |
|---------|---------|-------------|
| OUTFILE | ACCTDATA.PSCOMP | Standard account output (fixed-width) |
| ARRYFILE | ACCTDATA.ARRYPS | Array record output (5 balance/debit pairs per account) |
| VBRCFILE | ACCTDATA.VBPS | Variable-length VB records (VB1 + VB2 per account) |

**Reconciliation Checks:**

```
POST:
  count(OUTFILE records) = count(ACCTFILE records) = 50
  count(ARRYFILE records) = count(ACCTFILE records)
  count(VBRCFILE records) = 2 × count(ACCTFILE records)  [VB1 + VB2 per account]
  For each OUTFILE record:
    If original ACCT-CURR-CYC-DEBIT = 0 → output debit = 2525.00
    Reissue date converted from YYYY-MM-DD to YYYYMMDD
  For each ARRYFILE record:
    Slot 1: balance = ACCT-CURR-BAL, debit = 1005.00
    Slot 2: balance = ACCT-CURR-BAL, debit = 1525.00
    Slot 3: balance = -1025.00, debit = -2500.00 (fixed)
    Slots 4-5: zero
```

**Business Rules Enforced:**
- Zero debit substitution: IF ACCT-CURR-CYC-DEBIT = 0 THEN output 2525.00
- Date format conversion: YYYY-MM-DD → YYYYMMDD (via COBDATFT assembler)
- Array record: fixed values in slot 3 (-1025.00 / -2500.00), actual balance in slots 1-2
- VB record split: VB1 = status fields, VB2 = monetary fields + reissue year

---

### 3.2 READCARD.jcl — Card File Read

| Attribute | Detail |
|-----------|--------|
| **Program** | CBACT02C |
| **Purpose** | Read and display card VSAM file records |

**Reads:** CARDFILE (CVACT02Y, 150 bytes)
**Writes:** SYSOUT (display only)

**Reconciliation Checks:**

```
POST:
  count(records read) = count(CARDFILE records) = 50
  All CARD-NUM values are 16 characters
  All CARD-CVV-CD values are 3-digit numeric
  All CARD-ACTIVE-STATUS in {'Y', 'N'}
```

---

### 3.3 READXREF.jcl — Cross-Reference File Read

| Attribute | Detail |
|-----------|--------|
| **Program** | CBACT03C |
| **Purpose** | Read and display card-account cross-reference file |

**Reads:** XREFFILE (CVACT03Y, 36 bytes)
**Writes:** SYSOUT (display only)

**Reconciliation Checks:**

```
POST:
  count(records read) = count(XREFFILE records) = 50
  Every XREF-ACCT-ID exists in ACCTFILE
  Every XREF-CUST-ID exists in CUSTFILE
  Every XREF-CARD-NUM exists in CARDFILE
```

---

### 3.4 READCUST.jcl — Customer File Read

| Attribute | Detail |
|-----------|--------|
| **Program** | CBCUS01C |
| **Purpose** | Read and display customer VSAM file records |

**Reads:** CUSTFILE (CVCUS01Y, 500 bytes)
**Writes:** SYSOUT (display only)

**Reconciliation Checks:**

```
POST:
  count(records read) = count(CUSTFILE records) = 50
  All CUST-SSN values are 9-digit numeric
  All CUST-FICO-CREDIT-SCORE in range [0, 999]
  All CUST-ADDR-STATE-CD are valid US state codes
```

---

## 4. VSAM Cluster Definition Jobs

These jobs define, delete, and load VSAM KSDS clusters. They are idempotent (DELETE + DEFINE + REPRO).

### 4.1 ACCTFILE.jcl — Account VSAM Setup

| Step | Operation | Description |
|------|-----------|-------------|
| STEP05 | IDCAMS DELETE | Delete existing ACCTDATA.VSAM.KSDS |
| STEP10 | IDCAMS DEFINE CLUSTER | Define KSDS: KEY(11,0), RECLN 300 |
| STEP15 | IDCAMS REPRO | Load from sequential PS to VSAM |

**Reconciliation:** `count(VSAM after REPRO) = count(sequential input) = 50`

### 4.2 CARDFILE.jcl — Card VSAM Setup

| Step | Operation | Description |
|------|-----------|-------------|
| STEP05 | IDCAMS DELETE | Delete CARDDATA.VSAM.KSDS + AIX |
| STEP10 | IDCAMS DEFINE CLUSTER | Define KSDS: KEY(16,0), RECLN 150 |
| STEP15 | IDCAMS REPRO | Load card data |
| STEP20 | IDCAMS DEFINE AIX | Alternate index on CARD-ACCT-ID |
| STEP25 | IDCAMS DEFINE PATH | Define path for AIX access |
| STEP30 | IDCAMS BLDINDEX | Build alternate index |

**Reconciliation:** `count(VSAM) = count(PS input) = 50; AIX covers all records`

### 4.3 CUSTFILE.jcl — Customer VSAM Setup

| Step | Operation | Description |
|------|-----------|-------------|
| STEP05 | IDCAMS DELETE | Delete CUSTDATA.VSAM.KSDS |
| STEP10 | IDCAMS DEFINE CLUSTER | Define KSDS: KEY(9,0), RECLN 500 |
| STEP15 | IDCAMS REPRO | Load customer data |

**Reconciliation:** `count(VSAM) = count(PS input) = 50`

### 4.4 XREFFILE.jcl — Cross-Reference VSAM Setup

| Step | Operation | Description |
|------|-----------|-------------|
| STEP05 | IDCAMS DELETE | Delete CARDXREF.VSAM.KSDS |
| STEP10 | IDCAMS DEFINE CLUSTER | Define KSDS: KEY(16,0), RECLN 50 |
| STEP15 | IDCAMS REPRO | Load cross-reference data |
| STEP20 | IDCAMS DEFINE AIX | Alternate index on XREF-ACCT-ID |

**Reconciliation:** `count(VSAM) = count(PS input) = 50; every XREF-ACCT-ID exists in ACCTFILE`

### 4.5 TRANFILE.jcl — Transaction Master VSAM Setup

| Step | Operation | Description |
|------|-----------|-------------|
| STEP05 | IDCAMS DELETE | Delete TRANSACT.VSAM.KSDS |
| STEP10 | IDCAMS DEFINE CLUSTER | Define KSDS: KEY(16,0), RECLN 350 |
| STEP15 | IDCAMS REPRO | Load initial transactions |

**Reconciliation:** `count(VSAM) = count(PS input); all TRAN-IDs unique`

### 4.6 TCATBALF.jcl — Transaction Category Balance VSAM Setup

| Step | Operation | Description |
|------|-----------|-------------|
| STEP05 | IDCAMS DELETE | Delete TCATBALF.VSAM.KSDS |
| STEP10 | IDCAMS DEFINE CLUSTER | Define KSDS: KEY(17,0), RECLN 51 |
| STEP15 | IDCAMS REPRO | Load category balances |

**Reconciliation:** `count(VSAM) = count(PS input) = 50; composite key (ACCT-ID+TYPE+CAT) unique`

### 4.7 DISCGRP.jcl — Disclosure Group VSAM Setup

**Reconciliation:** `count(VSAM) = 51; composite key (GROUP-ID+TYPE+CAT) unique; all DIS-INT-RATE ≥ 0`

### 4.8 TRANTYPE.jcl — Transaction Type VSAM Setup

**Reconciliation:** `count(VSAM) = 7; TRAN-TYPE unique`

### 4.9 TRANCATG.jcl — Transaction Category VSAM Setup

**Reconciliation:** `count(VSAM) = 18; composite key (TYPE-CD+CAT-CD) unique`

### 4.10 TRANIDX.jcl — Transaction Alternate Index

| Step | Operation | Description |
|------|-----------|-------------|
| STEP20 | IDCAMS DEFINE AIX | Alternate index on TRAN-CARD-NUM |
| STEP25 | IDCAMS DEFINE PATH | Define path for AIX |
| STEP30 | IDCAMS BLDINDEX | Build alternate index |

**Reconciliation:** `AIX record count = TRANSACT record count`

---

## 5. Backup and Maintenance Jobs

### 5.1 TRANBKP.jcl — Transaction Backup

| Step | Operation | Description |
|------|-----------|-------------|
| STEP05R | REPROC | Unload TRANSACT VSAM to sequential file |
| STEP05 | IDCAMS DELETE | Delete TRANSACT VSAM cluster |
| STEP10 | IDCAMS DEFINE CLUSTER | Redefine empty TRANSACT cluster |

**Reconciliation:**

```
POST:
  count(backup sequential file) = count(TRANSACT before backup)
  MD5(backup file) stored for audit
  TRANSACT VSAM is empty after job
```

### 5.2 COMBTRAN.jcl — Combine Transactions

| Step | Operation | Description |
|------|-----------|-------------|
| STEP05R | SORT | Merge TRANSACT.BKUP(0) + SYSTRAN(0), sort by TRAN-ID ascending |
| STEP10 | IDCAMS REPRO | Load sorted result into TRANSACT VSAM |

**Reconciliation:**

```
POST:
  count(TRANSACT after) = count(BKUP input) + count(SYSTRAN input) - count(duplicate TRAN-IDs)
  TRANSACT sorted by TRAN-ID ascending
  No duplicate TRAN-IDs in final VSAM
```

### 5.3 PRTCATBL.jcl — Print Category Balances

| Step | Operation | Description |
|------|-----------|-------------|
| DELDEF | IEFBR14 | Delete prior output |
| STEP05R | REPROC | Unload TCATBALF to sequential |
| STEP10R | SORT | Sort by ACCT-ID + TYPE-CD + CAT-CD ascending |

**Reconciliation:** `count(sorted output) = count(TCATBALF VSAM)`

---

## 6. GDG and Infrastructure Jobs

### 6.1 DEFGDGB.jcl — Define GDG Bases

Defines 6 Generation Data Group bases for versioned output files:
- TRANSACT.BKUP — Transaction backups
- SYSTRAN — System-generated transactions
- DALYREJS — Daily rejection files
- STMTFILE — Statement output
- REPTFILE — Report output
- HTMLFILE — HTML statement output

**Reconciliation:** All 6 GDG bases defined with LIMIT(50)

### 6.2 DEFGDGD.jcl — Define DB2 GDG

Defines GDG bases for DB2-related backup/export files.

### 6.3 DALYREJS.jcl — Define Daily Rejections GDG

Defines GDG base for rejected transaction files.

### 6.4 REPTFILE.jcl — Define Report GDG

Defines GDG base for transaction report output.

---

## 7. CICS Control Jobs

### 7.1 OPENFIL.jcl — Open CICS Files

Opens all VSAM files in CICS for online transaction processing using SDSF commands.

### 7.2 CLOSEFIL.jcl — Close CICS Files

Closes all VSAM files in CICS before batch processing window.

### 7.3 CBADMCDJ.jcl — CICS CSD Definitions

Defines all CardDemo resources in the CICS System Definition file:
- MAPSET definitions for all 16 BMS maps
- PROGRAM definitions for all 21 CICS programs
- FILE definitions for all VSAM files
- TRANSACTION definitions

**Reconciliation:** All 16 MAPSETs + 21 PROGRAMs + all FILEs defined in CARDDEMO group

---

## 8. Cross-File Integrity Checks (Run Before and After Every Batch Cycle)

These checks should pass at all times and must be verified before and after each daily batch run:

| Check | SQL Equivalent | Expected |
|-------|---------------|----------|
| Every XREF card has an account | `XREF.ACCT-ID ∈ ACCT.ACCT-ID` | 0 orphans |
| Every XREF card has a customer | `XREF.CUST-ID ∈ CUST.CUST-ID` | 0 orphans |
| Every card exists in XREF | `CARD.CARD-NUM ∈ XREF.XREF-CARD-NUM` | 0 orphans |
| Every transaction card exists in XREF | `TRAN.CARD-NUM ∈ XREF.XREF-CARD-NUM` | 0 orphans |
| Every TCATBAL account exists | `TCATBAL.ACCT-ID ∈ ACCT.ACCT-ID` | 0 orphans |
| Every DISCGRP group exists in accounts | `DISCGRP.GROUP-ID ∈ ACCT.GROUP-ID` | 0 orphans |
| Every TRANCATG type has a TRANTYPE | `TRANCATG.TYPE-CD ∈ TRANTYPE.TYPE` | 0 orphans |
| Account keys unique | `count(distinct ACCT-ID) = count(ACCTFILE)` | Equal |
| Card keys unique | `count(distinct CARD-NUM) = count(CARDFILE)` | Equal |
| Transaction keys unique | `count(distinct TRAN-ID) = count(TRANSACT)` | Equal |

**Implementation:** These checks are automated in `test-harness/carddemo_harness/reconciliation.py` and can be run via:

```bash
cd test-harness
python run_tests.py --suite reconciliation
```

Current baseline (50 accounts, 50 cards, 50 customers, 300 daily transactions): **27/27 checks passing**.
