# DEPENDENCY MAP — CardDemo COBOL Estate

> Call graph, dataset lineage, and batch pipeline flows for the CardDemo application.

---

## 1. Inter-Program Call Graph

### 1.1 CICS Transfer Hierarchy (XCTL / LINK)

```
COSGN00C (Sign-on)
  ├── XCTL → COADM01C (Admin Menu)    [if user type = 'A']
  │      ├── XCTL → COUSR00C (User List)
  │      ├── XCTL → COUSR01C (User Add)
  │      ├── XCTL → COUSR02C (User Update)
  │      ├── XCTL → COUSR03C (User Delete)
  │      ├── XCTL → COTRTLIC (Tran Type List — DB2)
  │      │      └── XCTL → COTRTUPC (Tran Type Update — DB2)
  │      │              └── XCTL → COTRTLIC (return)
  │      └── XCTL → COTRTUPC (Tran Type Add — DB2)
  │
  └── XCTL → COMEN01C (Main Menu)     [if user type = 'U']
         ├── XCTL → COACTVWC (Account View)
         │      └── XCTL → (CDEMO-TO-PROGRAM, dynamic)
         ├── XCTL → COACTUPC (Account Update)
         │      └── XCTL → (CDEMO-TO-PROGRAM, dynamic)
         ├── XCTL → COCRDLIC (Credit Card List)
         │      ├── XCTL → COMEN01C (back to menu)
         │      └── XCTL → COCRDSLC / COCRDUPC (via CCARD-NEXT-PROG)
         ├── XCTL → COCRDSLC (Credit Card View)
         │      └── XCTL → (CDEMO-TO-PROGRAM, dynamic)
         ├── XCTL → COCRDUPC (Credit Card Update)
         │      └── XCTL → (CDEMO-TO-PROGRAM, dynamic)
         ├── XCTL → COTRN00C (Transaction List)
         ├── XCTL → COTRN01C (Transaction View)
         ├── XCTL → COTRN02C (Transaction Add)
         ├── XCTL → CORPT00C (Reports)
         ├── XCTL → COBIL00C (Bill Payment)
         └── XCTL → COPAUS0C (Pending Auth View)
                └── XCTL → COPAUS1C (Auth Detail)
                       └── LINK → COPAUS2C (Mark Fraud — DB2)
```

### 1.2 Program-to-Program CALL Statements

| Caller | Callee | Purpose |
|--------|--------|---------|
| CBACT01C | COBDATFT | Date formatting utility |
| CBSTM03A | CBSTM03B | Statement file I/O subroutine |
| CORPT00C | CSUTLDTC | Date validation (report date range) |
| COTRN02C | CSUTLDTC | Date validation (transaction date) |
| COBSWAIT | MVSWAIT | System wait call |
| COPAUA0C | MQOPEN, MQGET, MQPUT1, MQCLOSE | MQ Series API calls |
| COACCT01 | MQOPEN, MQGET, MQPUT, MQCLOSE | MQ Series API calls |
| CODATE01 | MQOPEN, MQGET, MQPUT, MQCLOSE | MQ Series API calls |
| DBUNLDGS, PAUDBLOD, PAUDBUNL | CBLTDLI | IMS DL/I database calls |
| _All batch programs_ | CEE3ABD | LE runtime abend handler |

### 1.3 Menu-to-Program Routing (Dynamic)

The COMMAREA field `CDEMO-TO-PROGRAM` (COCOM01Y) drives dynamic XCTL transfers:

**Main Menu (COMEN02Y) — 11 options:**

| Opt | Label | Target Program |
|-----|-------|----------------|
| 1 | Account View | COACTVWC |
| 2 | Account Update | COACTUPC |
| 3 | Credit Card List | COCRDLIC |
| 4 | Credit Card View | COCRDSLC |
| 5 | Credit Card Update | COCRDUPC |
| 6 | Transaction List | COTRN00C |
| 7 | Transaction View | COTRN01C |
| 8 | Transaction Add | COTRN02C |
| 9 | Reports | CORPT00C |
| 10 | Bill Payment | COBIL00C |
| 11 | Pending Auth View | COPAUS0C |

**Admin Menu (COADM02Y) — 6 options:**

| Opt | Label | Target Program |
|-----|-------|----------------|
| 1 | User List | COUSR00C |
| 2 | User Add | COUSR01C |
| 3 | User Update | COUSR02C |
| 4 | User Delete | COUSR03C |
| 5 | Tran Type List | COTRTLIC |
| 6 | Tran Type Update | COTRTUPC |

---

## 2. Copybook Dependency Matrix

| Copybook | Referenced By (Programs) | Count |
|----------|--------------------------|-------|
| COCOM01Y | COSGN00C, COADM01C, COMEN01C, COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC | 21 |
| COTTL01Y | All CICS programs (screen title) | 21 |
| CSDAT01Y | All CICS programs (date/time) | 21 |
| CSMSG01Y | All CICS programs (messages) | 21 |
| DFHAID | All CICS programs (AID bytes) | 21 |
| DFHBMSCA | All CICS programs (BMS attributes) | 21 |
| CSUSR01Y | COSGN00C, COADM01C, COMEN01C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC | 9 |
| CVCRD01Y | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC | 7 |
| CVACT01Y | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBSTM03A, COACTVWC, COACTUPC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C, COACCT01 | 14 |
| CVACT02Y | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COPAUS0C | 10 |
| CVACT03Y | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, COACTVWC, COACTUPC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C | 14 |
| CVCUS01Y | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COACTUPC, COCRDSLC, COPAUA0C, COPAUS0C | 9 |
| CVTRA05Y | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C | 11 |
| CSMSG02Y | COACTVWC, COACTUPC, COCRDSLC, COCRDUPC, COPAUS0C, COPAUS1C, COTRTUPC | 7 |
| CSLKPCDY | COACTUPC | 1 |
| CSUTLDPY | COACTUPC | 1 |
| CVEXPORT | CBEXPORT, CBIMPORT | 2 |

---

## 3. Dataset Lineage — JCL-to-Program-to-File Mapping

### 3.1 VSAM Dataset Lifecycle

```
                  ┌─────────────────────────────────────────────────────────┐
  Sequential      │              VSAM (KSDS)                                │    Programs
  (PS) Files      │                                                         │    Processing
  ───────────     │                                                         │    ──────────
                  │                                                         │
  ACCTDATA.PS ──→ │ ACCTFILE.jcl ──→ ACCTDATA.VSAM.KSDS ──────────────────→│──→ CBACT01C (read)
                  │                                                         │    CBACT04C (read/rewrite)
                  │                                                         │    CBEXPORT (read)
                  │                                                         │    CBSTM03A (read)
                  │                                                         │    COACTUPC (read/rewrite via CICS)
                  │                                                         │    COACTVWC (read via CICS)
                  │                                                         │    COBIL00C (read/rewrite via CICS)
                  │                                                         │    COTRN02C (read via CICS)
                  │                                                         │
  CARDDATA.PS ──→ │ CARDFILE.jcl ──→ CARDDATA.VSAM.KSDS + AIX ────────────→│──→ CBACT02C (read)
                  │                                                         │    CBEXPORT (read)
                  │                                                         │    COCRDLIC (browse via CICS)
                  │                                                         │    COCRDSLC (read via CICS)
                  │                                                         │    COCRDUPC (read/rewrite via CICS)
                  │                                                         │    COACTVWC (read via CICS)
                  │                                                         │
  CUSTDATA.PS ──→ │ CUSTFILE.jcl ──→ CUSTDATA.VSAM.KSDS ──────────────────→│──→ CBCUS01C (read)
                  │                                                         │    CBEXPORT (read)
                  │                                                         │    CBSTM03A (read)
                  │                                                         │    COACTUPC (read via CICS)
                  │                                                         │    COACTVWC (read via CICS)
                  │                                                         │
  CARDXREF.PS ──→ │ XREFFILE.jcl ──→ CARDXREF.VSAM.KSDS + AIX ───────────→│──→ CBACT03C (read)
                  │                                                         │    CBACT04C (read)
                  │                                                         │    CBEXPORT (read)
                  │                                                         │    CBTRN03C (read)
                  │                                                         │    CBSTM03A (read)
                  │                                                         │    COACTVWC (read via CICS)
                  │                                                         │
  TRANSACT.PS ──→ │ TRANFILE.jcl ──→ TRANSACT.VSAM.KSDS + AIX ────────────→│──→ CBEXPORT (read)
                  │                                                         │    CBTRN03C (read)
                  │                                                         │    COTRN00C (browse via CICS)
                  │                                                         │    COTRN01C (read via CICS)
                  │                                                         │    COBIL00C (write via CICS)
                  │                                                         │
  TRANTYPE.PS ──→ │ TRANTYPE.jcl ──→ TRANTYPE.VSAM.KSDS ──────────────────→│──→ CBTRN03C (read)
                  │                                                         │
  TRANCATG.PS ──→ │ TRANCATG.jcl ──→ TRANCATG.VSAM.KSDS ─────────────────→│──→ CBTRN03C (read)
                  │                                                         │
  USRSEC.PS ────→ │ DUSRSECJ.jcl ──→ USRSEC.VSAM.KSDS ───────────────────→│──→ COSGN00C (read via CICS)
                  │                                                         │    COUSR00-03C (CRUD via CICS)
                  │                                                         │
  TCATBAL.PS ───→ │ TCATBALF.jcl ──→ TCATBALF.VSAM.KSDS ─────────────────→│──→ CBACT04C (read)
                  │                                                         │    CBTRN02C (write)
                  │                                                         │
  DISCGRP.PS ───→ │ DISCGRP.jcl ──→ DISCGRP.VSAM.KSDS ───────────────────→│──→ CBACT04C (read)
                  └─────────────────────────────────────────────────────────┘
```

### 3.2 JCL-to-Program Execution Map

| JCL Job | Program Executed | Input Datasets | Output Datasets |
|---------|-----------------|----------------|-----------------|
| READACCT.jcl | CBACT01C | ACCTDATA.VSAM.KSDS | ACCTDATA.PSCOMP, ACCTDATA.ARRYPS, ACCTDATA.VBPS |
| READCARD.jcl | CBACT02C | CARDDATA.VSAM.KSDS | SYSOUT |
| READCUST.jcl | CBCUS01C | CUSTDATA.VSAM.KSDS | SYSOUT |
| READXREF.jcl | CBACT03C | CARDXREF.VSAM.KSDS | SYSOUT |
| INTCALC.jcl | CBACT04C | TCATBALF, XREFFILE, ACCTFILE, DISCGRP | TRANSACT (new interest transactions) |
| POSTTRAN.jcl | CBTRN02C | TRANFILE, DALYTRAN, XREFFILE, ACCTFILE | DALYREJS, TCATBALF |
| TRANREPT.jcl | SORT → CBTRN03C | TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM | TRANREPT (report GDG) |
| CREASTMT.JCL | SORT → CBSTM03A | TRANSACT, CARDXREF, ACCTDATA, CUSTDATA | STMTFILE, HTMLFILE |
| CBEXPORT.jcl | CBEXPORT | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | EXPORT.DATA |
| CBIMPORT.jcl | CBIMPORT | EXPORT.DATA | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT |
| CBPAUP0J.jcl | CBPAUP0C (IMS) | IMS PAUTHDB | — (deletes expired records) |
| UNLDPADB.JCL | PAUDBUNL (IMS) | IMS PAUTHDB | PAUTDB.ROOT.FILEO, PAUTDB.CHILD.FILEO |
| UNLDGSAM.JCL | DBUNLDGS (IMS) | IMS PAUTHDB | PAUTDB.ROOT.GSAM, PAUTDB.CHILD.GSAM |
| LOADPADB.JCL | PAUDBLOD (IMS) | PAUTDB.ROOT.FILEO, PAUTDB.CHILD.FILEO | IMS PAUTHDB |
| MNTTRDB2.jcl | COBTUPDT (DB2) | INPFILE | DB2 TRANTYPE table |
| CREADB21.jcl | IKJEFT01 (DB2) | DDL scripts | DB2 TRANTYPE + TRANCATG tables |
| TRANEXTR.jcl | IEBGENER + IKJEFT01 | DB2 tables | TRANTYPE.BKUP, TRANCATG.PS.BKUP |

---

## 4. End-to-End Batch Pipeline Flows

### 4.1 Daily Transaction Posting Pipeline

```
                        ┌─────────────────────┐
                        │   External System    │
                        │  (Daily Trans Feed)  │
                        └──────────┬──────────┘
                                   │ PS file
                                   ▼
              ┌────────────────────────────────────────────────┐
  Job 1       │  COMBTRAN.jcl                                  │
  (Merge)     │  STEP05R: SORT — merge multiple daily feeds    │
              │  STEP10:  REPRO into DALYTRAN VSAM             │
              └────────────────────┬───────────────────────────┘
                                   │
                                   ▼
              ┌────────────────────────────────────────────────┐
  Job 2       │  POSTTRAN.jcl                                  │
  (Post)      │  STEP15: CBTRN02C                              │
              │   Input:  DALYTRAN, TRANFILE, XREFFILE, ACCTFILE│
              │   Output: DALYREJS (GDG +1), TCATBALF (updated) │
              └──────────┬──────────────────┬──────────────────┘
                         │                  │
                         ▼                  ▼
              ┌──────────────┐    ┌──────────────────┐
              │  DALYREJS    │    │  TCATBALF (VSAM) │
              │  (Rejects    │    │  (Category Bal   │
              │   GDG)       │    │   updated)       │
              └──────────────┘    └────────┬─────────┘
                                           │
                                           ▼
              ┌────────────────────────────────────────────────┐
  Job 3       │  INTCALC.jcl                                   │
  (Interest)  │  STEP15: CBACT04C (PARM=date)                  │
              │   Input:  TCATBALF, XREFFILE, ACCTFILE, DISCGRP │
              │   Output: TRANSACT (interest transactions)      │
              │           ACCTFILE (balances updated)            │
              └────────────────────┬───────────────────────────┘
                                   │
                                   ▼
              ┌────────────────────────────────────────────────┐
  Job 4       │  TRANREPT.jcl                                  │
  (Report)    │  STEP05R: SORT transaction file                 │
              │  STEP10R: CBTRN03C                              │
              │   Input:  TRANFILE, CARDXREF, TRANTYPE, TRANCATG│
              │   Output: TRANREPT (report GDG +1)              │
              └────────────────────────────────────────────────┘
```

### 4.2 Account Statement Pipeline

```
              ┌────────────────────────────────────────────────┐
  Job 1       │  CREASTMT.JCL                                  │
  (Stmt)      │  DELDEF01: IDCAMS — define temp TRXFL VSAM     │
              │  STEP010:  SORT — sort TRANSACT by card number  │
              │  STEP020:  REPRO — load sorted into TRXFL       │
              │  STEP030:  IEFBR14 — delete old reports         │
              │  STEP040:  CBSTM03A (calls CBSTM03B)            │
              │   Input:  TRXFL, XREFFILE, ACCTFILE, CUSTFILE   │
              │   Output: STMTFILE (text), HTMLFILE (HTML)      │
              └────────────────────┬───────────────────────────┘
                                   │
                                   ▼
              ┌────────────────────────────────────────────────┐
  Job 2       │  TXT2PDF1.JCL                                  │
  (PDF)       │  TXT2PDF:  IKJEFT1B — convert text to PDF      │
              │   Input:  STATEMNT.PS                           │
              │   Output: PDF                                   │
              └────────────────────────────────────────────────┘
```

### 4.3 Branch Migration Pipeline

```
  Source Mainframe                              Target Mainframe
  ────────────────                              ────────────────
              ┌───────────────────┐
  Job         │  CBEXPORT.jcl     │
  (Export)    │  STEP02: CBEXPORT │
              │   Reads: All 5    │
              │   master files    │
              │   Writes: EXPORT  │
              └────────┬──────────┘
                       │  FTP/Transfer
                       ▼
              ┌───────────────────┐
  Job         │  CBIMPORT.jcl     │
  (Import)    │  STEP01: CBIMPORT │
              │   Reads: EXPORT   │
              │   Writes: 5       │
              │   master files    │
              │   + ERROUT        │
              └───────────────────┘
```

### 4.4 IMS Authorization DB Maintenance Pipeline

```
              ┌───────────────────────────────────────────────┐
  Unload      │  UNLDPADB.JCL (or UNLDGSAM.JCL)              │
              │  PAUDBUNL/DBUNLDGS → IMS DL/I GN/GNP         │
              │   Output: ROOT.FILEO + CHILD.FILEO (or GSAM)  │
              └────────────────────┬──────────────────────────┘
                                   │
                                   ▼
              ┌───────────────────────────────────────────────┐
  Reload      │  LOADPADB.JCL                                 │
              │  PAUDBLOD → IMS DL/I ISRT                      │
              │   Input: ROOT.FILEO + CHILD.FILEO              │
              └────────────────────────────────────────────────┘

              ┌───────────────────────────────────────────────┐
  Purge       │  CBPAUP0J.jcl                                 │
              │  CBPAUP0C → IMS DL/I DLET                      │
              │   Deletes expired authorizations               │
              └────────────────────────────────────────────────┘
```

### 4.5 DB2 Transaction Type Maintenance Pipeline

```
              ┌───────────────────────────────────────────────┐
  Initial     │  CREADB21.jcl                                 │
  Setup       │  Create DB2 tables + bind + load seed data     │
              └────────────────────────────────────────────────┘

              ┌───────────────────────────────────────────────┐
  Batch       │  MNTTRDB2.jcl → COBTUPDT                     │
  Maintain    │  Process input file: A(dd)/D(elete) records    │
              └────────────────────────────────────────────────┘

              ┌───────────────────────────────────────────────┐
  Backup      │  TRANEXTR.jcl                                 │
              │  IEBGENER backup PS → GDG; DB2 UNLOAD          │
              └────────────────────────────────────────────────┘
```

---

## 5. Shared Resource Dependencies

### 5.1 Files Accessed by Multiple Pipelines

| Resource | Accessed By (Pipelines) | Contention Risk |
|----------|------------------------|-----------------|
| ACCTDATA VSAM | Daily posting, Interest calc, Statements, Export, Online CICS | **HIGH** — requires Close-Process-Open cycle for batch updates |
| TRANSACT VSAM | Daily posting, Interest calc, Reports, Statements, Export, Online CICS | **HIGH** — read by batch, written by online |
| CARDXREF VSAM | Reports, Statements, Interest calc, Export, Online CICS | MEDIUM — read-only in batch |
| CARDDATA VSAM | Export, Online CICS | LOW — written only by online |
| CUSTDATA VSAM | Export, Statements, Online CICS | LOW — read-only in batch |

### 5.2 CICS File Close/Open Coordination

Before batch jobs can update VSAM files also used by CICS:
1. `CLOSEFIL.jcl` → SDSF CEMT SET FIL(...) CLO
2. _Run batch job_
3. `OPENFIL.jcl` → SDSF CEMT SET FIL(...) OPE

This applies to: ACCTDATA, CARDDATA, TRANSACT, CARDXREF, CUSTDATA.

---

*Generated: 2026-05-27 | Source: `uc-legacy-modernization-cobol-to-java`*
