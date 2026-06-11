# CardDemo — Dependency Map

> Call graph, dataset lineage, and end-to-end batch pipeline flow for the CardDemo COBOL estate.

---

## 1. Online Navigation Tree (CICS XCTL / LINK)

All online programs communicate via CICS XCTL (transfer control) or LINK (call-and-return). The COMMAREA (COCOM01Y) carries user/session context across every transfer.

```
COSGN00C (Sign-On — Entry Point)
│
├──[Admin user]──► COADM01C (Admin Menu Hub)
│                  ├── COUSR00C (User List)
│                  │    ├──XCTL──► COUSR02C (User Update)
│                  │    └──XCTL──► COUSR03C (User Delete)
│                  ├── COUSR01C (User Add)
│                  ├── COTRTLIC (Transaction Type List — DB2)
│                  │    └──XCTL──► COMEN01C (return to menu)
│                  └── COTRTUPC (Transaction Type Update — DB2)
│                       └──XCTL──► COTRTLIC / COMEN01C
│
└──[Regular user]──► COMEN01C (Main Menu Hub — 11 XCTL targets)
                     ├── COACTVWC (Account View)
                     │    └──XCTL──► COMEN01C
                     ├── COACTUPC (Account Update — 4,236 LOC)
                     │    └──XCTL──► COMEN01C
                     ├── COCRDLIC (Card List)
                     │    ├──XCTL──► COCRDSLC (Card View)
                     │    ├──XCTL──► COCRDUPC (Card Update)
                     │    └──XCTL──► COMEN01C
                     ├── COCRDSLC (Card View)
                     │    └──XCTL──► COMEN01C
                     ├── COCRDUPC (Card Update)
                     │    └──XCTL──► COMEN01C
                     ├── COTRN00C (Transaction List)
                     │    └──XCTL──► COTRN01C (Transaction View)
                     ├── COTRN01C (Transaction View)
                     ├── COTRN02C (Transaction Add)
                     ├── CORPT00C (Report Request)
                     │    └──WRITEQ TD──► Internal Reader (INTRDRJ1 → INTRDRJ2)
                     ├── COBIL00C (Bill Payment)
                     └── COPAUS0C (Auth Summary — IMS)
                          └──LINK──► COPAUS1C (Auth Detail)
                                      └──LINK──► COPAUS2C (Fraud Flag — DB2)
```

### Navigation Summary

| Source Program | Mechanism | Target(s) |
|---------------|-----------|-----------|
| COSGN00C | XCTL | COADM01C (admin), COMEN01C (user) |
| COADM01C | XCTL | COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC |
| COMEN01C | XCTL | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C |
| COCRDLIC | XCTL | COCRDSLC, COCRDUPC, COMEN01C |
| COCRDSLC | XCTL | COMEN01C |
| COCRDUPC | XCTL | COMEN01C |
| COACTVWC | XCTL | COMEN01C |
| COACTUPC | XCTL | COMEN01C |
| COTRTLIC | XCTL | COMEN01C, COTRTUPC |
| COTRTUPC | XCTL | COTRTLIC, COMEN01C |
| COPAUS0C | SEND MAP | (screen navigation within BMS) |
| COPAUS1C | LINK | COPAUS2C |
| CORPT00C | WRITEQ TD | INTRDRJ1.JCL (internal reader) |

---

## 2. Batch CALL Graph

```
CBACT01C ──CALL──► COBDATFT (assembler: date formatting)
           ──CALL──► CEE3ABD (LE: abnormal termination)

CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B (I/O submodule: 13 calls)
           ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT (assembler: timer wait)

CSUTLDTC ──CALL──► CEEDAYS (LE: date validation)

COTRN02C ──CALL──► CSUTLDTC (date validation — online)
CORPT00C ──CALL──► CSUTLDTC (date validation — online)
```

### IMS DL/I Calls (via CBLTDLI)

| Program | DL/I Functions | IMS Database |
|---------|---------------|--------------|
| CBPAUP0C | GN, GNP, DLET | Pending Auth (summary + detail) |
| PAUDBLOD | ISRT, GU | Pending Auth (load from sequential) |
| PAUDBUNL | GN, GNP | Pending Auth (unload to sequential) |
| DBUNLDGS | GN, GNP, ISRT | GSAM (sequential access) |

### MQ Calls

| Program | MQ Verbs | Queues |
|---------|----------|--------|
| COPAUA0C | MQOPEN, MQGET, MQPUT1, MQCLOSE | Request queue (input), Reply queue (output) |
| COACCT01 | MQOPEN, MQGET, MQPUT, MQCLOSE | Reply queue, Response queue, Error queue (3 queues) |
| CODATE01 | MQOPEN, MQGET, MQPUT, MQCLOSE | Reply queue, Response queue, Error queue (3 queues) |

### DB2 SQL Operations

| Program | SQL Operations | Tables |
|---------|---------------|--------|
| COTRTLIC | SELECT (cursor), DELETE | CARDDEMO.TRANSACTION_TYPE |
| COTRTUPC | SELECT, INSERT, UPDATE, DELETE | CARDDEMO.TRANSACTION_TYPE, CARDDEMO.TRANSACTION_CATEGORY |
| COBTUPDT | INSERT, UPDATE, DELETE | CARDDEMO.TRANSACTION_TYPE |
| COPAUS2C | INSERT, SELECT | CARDDEMO.AUTHFRDS (fraud flags) |

---

## 3. Dataset Lineage

### 3.1 VSAM File Access Map

| VSAM Dataset | VSAM Type | Record Copybook | Programs That WRITE | Programs That READ | Programs That READ/REWRITE |
|-------------|-----------|-----------------|--------------------|--------------------|---------------------------|
| ACCTFILE | KSDS | CVACT01Y (300 bytes) | CBIMPORT | CBACT01C, COACTVWC, COCRDSLC, COPAUA0C, COPAUS0C, CBTRN01C, CBEXPORT, CBSTM03B | COACTUPC, CBACT04C, CBTRN02C, COBIL00C |
| CARDFILE | KSDS+AIX | CVACT02Y (150 bytes) | CBIMPORT | CBACT02C, COACTVWC, COCRDLIC, COCRDSLC, COPAUS0C, CBEXPORT | COCRDUPC |
| CUSTFILE | KSDS | CVCUS01Y (500 bytes) | CBIMPORT | CBCUS01C, COACTUPC, COACTVWC, COPAUA0C, COPAUS0C, CBEXPORT, CBSTM03B | — |
| CARDXREF (XREFFILE) | KSDS+AIX | CVACT03Y (50 bytes) | CBIMPORT | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COTRN02C, COPAUA0C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, CBSTM03B | — |
| TRANSACT | KSDS+AIX | CVTRA05Y (350 bytes) | CBTRN02C, CBACT04C, CBIMPORT | COTRN00C, COTRN01C, CBTRN03C, CBEXPORT, CBSTM03B | COBIL00C |
| USRSEC | KSDS | CSUSR01Y (80 bytes) | COUSR01C | COSGN00C, COUSR00C | COUSR02C, (DELETE: COUSR03C) |
| TCATBALF | KSDS | CVTRA01Y (50 bytes) | — | CBACT04C | CBTRN02C |
| DALYTRAN | Sequential | CVTRA06Y (350 bytes) | (external feed) | CBTRN01C, CBTRN02C | — |
| DISCGRP | KSDS | CVTRA02Y (50 bytes) | — | CBACT04C | — |
| TRANTYPE | KSDS | CVTRA03Y (60 bytes) | — | CBTRN03C | — |
| TRANCATG | KSDS | CVTRA04Y (60 bytes) | — | CBTRN03C | — |

### 3.2 Sequential File Lineage

| Sequential File | Created By | Consumed By |
|----------------|-----------|-------------|
| DALYTRAN (daily transactions) | External batch feed | CBTRN01C (validate), CBTRN02C (post) |
| DALYREJS (daily rejects) | CBTRN02C | Manual review / DALYREJS.jcl (cleanup) |
| STMT-FILE (statements) | CBSTM03A | TXT2PDF1.JCL (PDF conversion) |
| HTML-FILE (HTML statements) | CBSTM03A | External distribution |
| REPTFILE (transaction report) | CBTRN03C | External distribution |
| EXPFILE (export data) | CBEXPORT | CBIMPORT (branch migration) |
| INFILE1 / INFILE2 | Sequential extracts | PAUDBLOD (IMS load) |
| OPFILE1 / OPFILE2 | PAUDBUNL, DBUNLDGS | External / backup |

### 3.3 JCL Job → Dataset → Program Lineage

```
                          ┌──────────────────────────────────────────┐
                          │        DATA SETUP JOBS (one-time)        │
                          │                                          │
  ACCTFILE.jcl ──REPRO──► │  ACCTFILE VSAM                          │
  CARDFILE.jcl ──REPRO──► │  CARDFILE VSAM (+ AIX)                  │
  CUSTFILE.jcl ──REPRO──► │  CUSTFILE VSAM                          │
  XREFFILE.jcl ──REPRO──► │  XREFFILE VSAM (+ AIX)                  │
  TRANFILE.jcl ──REPRO──► │  TRANSACT VSAM (+ AIX)                  │
  TCATBALF.jcl ──REPRO──► │  TCATBALF VSAM                          │
  DISCGRP.jcl  ──REPRO──► │  DISCGRP VSAM                           │
  TRANTYPE.jcl ──REPRO──► │  TRANTYPE VSAM                          │
  TRANCATG.jcl ──REPRO──► │  TRANCATG VSAM                          │
  DUSRSECJ.jcl ──REPRO──► │  USRSEC VSAM                            │
                          └──────────────────────────────────────────┘

                          ┌──────────────────────────────────────────┐
                          │     DAILY BATCH PIPELINE (sequential)    │
                          │                                          │
  DALYTRAN ──────────────►│ POSTTRAN.jcl (CBTRN02C)                  │
                          │   reads: DALYTRAN, XREFFILE, ACCTFILE,   │
                          │          TCATBALF                        │
                          │   writes: TRANSACT, DALYREJS             │
                          │   rewrites: ACCTFILE, TCATBALF           │
                          │                 │                        │
                          │                 ▼                        │
                          │ INTCALC.jcl (CBACT04C)                   │
                          │   reads: TCATBALF, XREFFILE, DISCGRP    │
                          │   rewrites: ACCTFILE                     │
                          │   writes: TRANSACT (interest txns)       │
                          │                 │                        │
                          │                 ▼                        │
                          │ CREASTMT.JCL (SORT → CBSTM03A)           │
                          │   reads: TRANSACT (sorted), XREFFILE,   │
                          │          CUSTFILE, ACCTFILE               │
                          │   writes: STMT-FILE, HTML-FILE           │
                          │                 │                        │
                          │                 ▼                        │
                          │ TRANREPT.jcl (SORT → CBTRN03C)           │
                          │   reads: TRANSACT, CARDXREF, TRANTYPE,  │
                          │          TRANCATG, DATEPARM              │
                          │   writes: TRANREPT                       │
                          └──────────────────────────────────────────┘
```

---

## 4. End-to-End Batch Pipeline Flow

### 4.1 Daily Processing Cycle

The four core batch jobs execute in strict sequence. Each job depends on the outputs of the previous job.

```
Step 1: POSTTRAN.jcl
  ┌─────────────────────────────────────────────────────────────┐
  │ Program: CBTRN02C (731 LOC)                                 │
  │ Function: Post daily transactions to master file             │
  │                                                             │
  │ Input:  DALYTRAN (daily transaction feed — CVTRA06Y)        │
  │         XREFFILE (card-to-account cross-reference)          │
  │         ACCTFILE (account master)                            │
  │         TCATBALF (transaction category balances)             │
  │                                                             │
  │ Output: TRANSACT (master transaction file — new records)     │
  │         DALYREJS (rejected transactions)                     │
  │ Update: ACCTFILE (account balances), TCATBALF (cat balances) │
  └────────────────────────────┬────────────────────────────────┘
                               ▼
Step 2: INTCALC.jcl
  ┌─────────────────────────────────────────────────────────────┐
  │ Program: CBACT04C (652 LOC)                                 │
  │ Function: Calculate and apply interest charges               │
  │                                                             │
  │ Input:  TCATBALF (category balances — updated in Step 1)    │
  │         XREFFILE (cross-reference)                          │
  │         DISCGRP  (discount/interest rate groups)             │
  │         ACCTFILE (account master — updated in Step 1)        │
  │                                                             │
  │ Output: TRANSACT (interest charge transactions)              │
  │ Update: ACCTFILE (apply interest to balances)                │
  └────────────────────────────┬────────────────────────────────┘
                               ▼
Step 3: CREASTMT.JCL
  ┌─────────────────────────────────────────────────────────────┐
  │ Pre-step: SORT TRANSACT by card number (STEP010)            │
  │ Pre-step: REPRO sorted sequential → VSAM KSDS (STEP020)    │
  │                                                             │
  │ Program: CBSTM03A (924 LOC) → CALL CBSTM03B (230 LOC)      │
  │ Function: Generate customer statements in text + HTML        │
  │                                                             │
  │ Input:  TRANSACT (sorted — via CBSTM03B)                    │
  │         XREFFILE (via CBSTM03B)                             │
  │         CUSTFILE (via CBSTM03B)                             │
  │         ACCTFILE (via CBSTM03B)                             │
  │                                                             │
  │ Output: STMT-FILE (text statements)                          │
  │         HTML-FILE (HTML statements)                          │
  └────────────────────────────┬────────────────────────────────┘
                               ▼
Step 4: TRANREPT.jcl
  ┌─────────────────────────────────────────────────────────────┐
  │ Pre-step: REPROC backup of TRANSACT                         │
  │ Pre-step: SORT TRANSACT                                     │
  │                                                             │
  │ Program: CBTRN03C (649 LOC)                                 │
  │ Function: Generate daily transaction report                  │
  │                                                             │
  │ Input:  TRANSACT (sorted transactions)                      │
  │         CARDXREF (cross-reference)                          │
  │         TRANTYPE (transaction type descriptions)             │
  │         TRANCATG (transaction category descriptions)         │
  │         DATEPARM (report date range)                        │
  │                                                             │
  │ Output: TRANREPT (formatted report file)                     │
  └─────────────────────────────────────────────────────────────┘
```

### 4.2 Data Export/Import Pipeline

```
CBEXPORT.jcl (CBEXPORT)                    CBIMPORT.jcl (CBIMPORT)
┌────────────────────────────┐              ┌────────────────────────────┐
│ Reads all 5 core VSAM:    │              │ Reads EXPFILE              │
│  CUSTFILE, ACCTFILE,       │──EXPFILE──►  │ Validates record types     │
│  XREFFILE, TRANSACT,       │              │ Distributes to:            │
│  CARDFILE                  │              │  CUSTOUT, ACCTOUT,         │
│ Writes combined EXPFILE    │              │  XREFOUT, TRNXOUT          │
│ (CVEXPORT layout)          │              │ Writes ERROUT (rejects)    │
└────────────────────────────┘              └────────────────────────────┘
```

### 4.3 IMS Authorization Pipeline

```
LOADPADB.JCL (PAUDBLOD)                    CBPAUP0J.jcl (CBPAUP0C)
┌────────────────────────────┐              ┌────────────────────────────┐
│ Sequential → IMS DB        │              │ Purge expired auth records │
│ INFILE1 → summary segments │              │ GN/GNP to find expired     │
│ INFILE2 → detail segments  │              │ DLET to remove             │
└────────────────────────────┘              └────────────────────────────┘

UNLDPADB.JCL (PAUDBUNL)                    UNLDGSAM.JCL (DBUNLDGS)
┌────────────────────────────┐              ┌────────────────────────────┐
│ IMS DB → Sequential        │              │ IMS → GSAM output          │
│ GN/GNP reads all segments  │              │ GN/GNP reads + ISRT to     │
│ OPFILE1 ← summaries        │              │ GSAM sequential output     │
│ OPFILE2 ← details          │              └────────────────────────────┘
└────────────────────────────┘
```

### 4.4 Online-to-Batch Bridge

```
CORPT00C (CICS Online)
│  User enters date range on CORPT00 BMS map
│  CALL CSUTLDTC (validate dates)
│  CICS WRITEQ TD → Internal Reader queue
│
├──► INTRDRJ1.JCL
│      IDCAMS (backup)
│      IEBGENER → submits INTRDRJ2.JCL to internal reader
│
└──► INTRDRJ2.JCL
       IDCAMS (secondary processing)
```

---

## 5. Inter-Program Dependency Summary

### 5.1 Dependency Counts (Inbound + Outbound)

| Program | Inbound Deps | Outbound Deps | Total | Role |
|---------|-------------|---------------|-------|------|
| COMEN01C | 12 (all return to menu) | 11 (XCTL targets) | 23 | Central hub |
| COADM01C | 1 (from COSGN00C) | 6 (admin targets) | 7 | Admin hub |
| COSGN00C | 0 (entry point) | 2 (COADM01C, COMEN01C) | 2 | Entry point |
| COACTUPC | 1 (from COMEN01C) | 1 (return to COMEN01C) | 2 | Leaf |
| CBSTM03A | 1 (from CREASTMT.jcl) | 1 (CALL CBSTM03B) | 2 | Batch orchestrator |
| CBSTM03B | 1 (from CBSTM03A) | 0 | 1 | I/O submodule |
| COPAUS1C | 1 (from COPAUS0C LINK) | 1 (LINK COPAUS2C) | 2 | IMS chain |
| COPAUS2C | 1 (from COPAUS1C) | 0 | 1 | Leaf (DB2 writer) |
| CSUTLDTC | 2 (COTRN02C, CORPT00C) | 1 (CALL CEEDAYS) | 3 | Shared utility |
| CEE3ABD | 10 (all batch programs) | 0 | 10 | LE abend handler |

### 5.2 Shared Copybook Dependencies

Most-referenced copybooks across the estate:

| Copybook | Referenced By (count) | Purpose |
|----------|----------------------|---------|
| COCOM01Y | 21 programs | CICS communication area |
| DFHBMSCA | 21 programs | BMS attribute constants (IBM-supplied) |
| DFHAID | 21 programs | AID key constants (IBM-supplied) |
| COTTL01Y | 19 programs | Screen title line |
| CSDAT01Y | 19 programs | Date/time working storage |
| CSMSG01Y | 19 programs | Common messages |
| CVACT01Y | 13 programs | Account record |
| CVACT03Y | 12 programs | Card cross-reference |
| CVCUS01Y | 8 programs | Customer record |
| CVACT02Y | 7 programs | Card record |
| CVTRA05Y | 7 programs | Transaction record |
| CSUSR01Y | 7 programs | User security record |
| CVCRD01Y | 6 programs | Card work areas |
| CSSTRPFY | 6 programs | PF key store paragraph |
