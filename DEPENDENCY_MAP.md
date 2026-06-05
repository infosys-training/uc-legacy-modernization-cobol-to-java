# DEPENDENCY MAP — CardDemo COBOL Estate

> Inter-program call graph, CICS navigation chains, dataset lineage, and end-to-end batch pipeline flow.

---

## Table of Contents

- [1. Online Navigation Graph (CICS XCTL/LINK)](#1-online-navigation-graph-cics-xctllink)
- [2. Batch Call Graph (CALL Dependencies)](#2-batch-call-graph-call-dependencies)
- [3. VSAM File Usage Matrix](#3-vsam-file-usage-matrix)
- [4. Dataset Lineage: JCL → File → Program](#4-dataset-lineage-jcl--file--program)
- [5. End-to-End Batch Pipeline Flow](#5-end-to-end-batch-pipeline-flow)
- [6. Copybook Dependency Matrix](#6-copybook-dependency-matrix)
- [7. Sub-Application Dependency Chains](#7-sub-application-dependency-chains)
- [8. Suggested Microservice Boundaries](#8-suggested-microservice-boundaries)

---

## 1. Online Navigation Graph (CICS XCTL/LINK)

All CICS programs communicate via the shared COMMAREA (COCOM01Y.cpy). Navigation uses EXEC CICS XCTL (transfer control — one-way) or EXEC CICS LINK (call/return — used for COPAUS2C fraud marking).

```
COSGN00C (Sign-On — Entry Point)
│
├── [Admin: CDEMO-USER-TYPE = 'A'] ──XCTL──► COADM01C (Admin Menu Hub)
│   │
│   ├── Option 1 ──XCTL──► COUSR00C (User List)
│   │                          ├──XCTL──► COUSR02C (User Update)
│   │                          └──XCTL──► COUSR03C (User Delete)
│   │
│   ├── Option 2 ──XCTL──► COUSR01C (User Add)
│   │
│   ├── Option 3 ──XCTL──► COUSR02C (User Update)
│   │
│   ├── Option 4 ──XCTL──► COUSR03C (User Delete)
│   │
│   ├── Option 5 ──XCTL──► COTRTLIC (Tran Type List — DB2)
│   │                          └──XCTL──► COTRTUPC (Tran Type Update — DB2)
│   │
│   └── Option 6 ──XCTL──► COTRTUPC (Tran Type Maintenance — DB2)
│
└── [User: CDEMO-USER-TYPE = 'U'] ──XCTL──► COMEN01C (Main Menu Hub — 11 options)
    │
    ├── Option 1  ──XCTL──► COACTVWC  (Account View)
    │                          └──XCTL──► [CDEMO-TO-PROGRAM] (return to menu)
    │
    ├── Option 2  ──XCTL──► COACTUPC  (Account Update — 4,236 LOC)
    │                          └──XCTL──► [CDEMO-TO-PROGRAM] (return to menu)
    │
    ├── Option 3  ──XCTL──► COCRDLIC  (Credit Card List)
    │                          ├──XCTL──► COCRDSLC (Card View)
    │                          ├──XCTL──► COCRDUPC (Card Update)
    │                          └──XCTL──► [LIT-MENUPGM] (return to menu)
    │
    ├── Option 4  ──XCTL──► COCRDSLC  (Credit Card View)
    │
    ├── Option 5  ──XCTL──► COCRDUPC  (Credit Card Update)
    │
    ├── Option 6  ──XCTL──► COTRN00C  (Transaction List)
    │                          └──XCTL──► COTRN01C (Transaction View)
    │
    ├── Option 7  ──XCTL──► COTRN01C  (Transaction View)
    │
    ├── Option 8  ──XCTL──► COTRN02C  (Transaction Add)
    │
    ├── Option 9  ──XCTL──► CORPT00C  (Report Request)
    │                          └── submits INTRDRJ1.JCL via CICS internal reader
    │
    ├── Option 10 ──XCTL──► COBIL00C  (Bill Payment)
    │
    └── Option 11 ──XCTL──► COPAUS0C  (Pending Auth Summary — IMS)
                               └──XCTL──► COPAUS1C (Auth Detail — IMS)
                                             └──LINK──► COPAUS2C (Fraud Marking — DB2)
```

### Key Observations

| Pattern | Description |
|---------|-------------|
| **Hub-and-Spoke** | COMEN01C is the central hub with 11 XCTL targets (highest fan-out). COADM01C has 6 targets. |
| **Dynamic XCTL** | Most programs use `PROGRAM(CDEMO-TO-PROGRAM)` — the target is set in COMMAREA at runtime, not hard-coded. |
| **Card Selection Flow** | COCRDLIC → COCRDSLC → COCRDUPC is a 3-step list→view→edit pattern used for credit cards. |
| **IMS Chain** | COPAUS0C → COPAUS1C → COPAUS2C forms a 3-program chain spanning CICS + IMS + DB2. COPAUS1C uses LINK (not XCTL) to call COPAUS2C, allowing return. |
| **Batch Bridge** | CORPT00C is the only online program that triggers batch execution (writes JCL to transient data queue). |

---

## 2. Batch Call Graph (CALL Dependencies)

Batch programs use COBOL `CALL` for inter-program and external routine invocation.

```
CBACT01C ──CALL──► COBDATFT     (Assembler: date formatting)

CBSTM03A ──CALL──► CBSTM03B    (COBOL: statement I/O submodule)
CBSTM03A ──CALL──► CEE3ABD     (LE: abnormal termination handler)

CBACT02C ──CALL──► CEE3ABD     (LE: abnormal termination handler)
CBACT03C ──CALL──► CEE3ABD     (LE: abnormal termination handler)
CBCUS01C ──CALL──► CEE3ABD     (LE: abnormal termination handler)
CBEXPORT ──CALL──► CEE3ABD     (LE: abnormal termination handler)
CBIMPORT ──CALL──► CEE3ABD     (LE: abnormal termination handler)
CBTRN01C ──CALL──► CEE3ABD     (LE: abnormal termination handler)
CBTRN02C ──CALL──► CEE3ABD     (LE: abnormal termination handler)
CBTRN03C ──CALL──► CEE3ABD     (LE: abnormal termination handler)
CBACT04C ──CALL──► CEE3ABD     (LE: abnormal termination handler)

COBSWAIT ──CALL──► MVSWAIT     (Assembler: wait/delay routine)

CSUTLDTC ──CALL──► CEEDAYS    (LE: date conversion, called by CORPT00C, COTRN02C)

COPAUA0C ──CALL──► MQOPEN     (MQ: open queue)
COPAUA0C ──CALL──► MQGET      (MQ: receive message)
COPAUA0C ──CALL──► MQPUT1     (MQ: send one message)
COPAUA0C ──CALL──► MQCLOSE    (MQ: close queue)

COACCT01 ──CALL──► MQOPEN, MQGET, MQPUT, MQCLOSE   (MQ: account inquiry)
CODATE01 ──CALL──► MQOPEN, MQGET, MQPUT, MQCLOSE   (MQ: date inquiry)
```

### External Dependency Summary

| External Routine | Type | Called By | Java Replacement |
|-----------------|------|-----------|-----------------|
| COBDATFT | Assembler | CBACT01C | `java.time.format.DateTimeFormatter` |
| CEE3ABD | LE Runtime | 10 batch programs | `throw new RuntimeException()` / JVM error handling |
| CEEDAYS | LE Runtime | CSUTLDTC | `java.time.LocalDate.toEpochDay()` |
| MVSWAIT | Assembler | COBSWAIT | `Thread.sleep()` |
| MQOPEN/GET/PUT/CLOSE | MQ API | COPAUA0C, COACCT01, CODATE01 | Spring JMS / Amazon SQS SDK |

---

## 3. VSAM File Usage Matrix

### Core VSAM Files — Read/Write Access by Program

| VSAM Dataset | DD Name | Programs That WRITE | Programs That READ |
|-------------|---------|--------------------|--------------------|
| **ACCTDATA.VSAM.KSDS** | ACCTFILE / ACCTDAT | CBACT04C (rewrite), COACTUPC (rewrite), COBIL00C (rewrite), CBIMPORT (write) | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBSTM03A, CBEXPORT, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C |
| **CARDDATA.VSAM.KSDS** | CARDFILE / CARDDAT | COCRDUPC (rewrite), CBIMPORT (write) | CBACT02C, CBTRN01C, CBEXPORT, COCRDLIC, COCRDSLC, COACTVWC |
| **CUSTDATA.VSAM.KSDS** | CUSTFILE / CUSTDAT | CBIMPORT (write) | CBCUS01C, CBTRN01C, CBSTM03A, CBEXPORT, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C |
| **CARDXREF.VSAM.KSDS** | XREFFILE / CCXREF | CBIMPORT (write) | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, CBEXPORT, COACTUPC, COACTVWC, COTRN02C, COBIL00C, COPAUA0C |
| **TRANSACT.VSAM.KSDS** | TRANSACT / TRANFILE | CBTRN02C (write), COBIL00C (write), CBACT04C (write, via SYSTRAN GDG) | CBTRN01C, CBTRN03C, CBSTM03A, CBEXPORT, COTRN00C, COTRN01C |
| **TCATBALF.VSAM.KSDS** | TCATBALF | CBTRN02C (rewrite), CBACT04C (read) | CBACT04C, COTRN02C |
| **TRANTYPE.VSAM.KSDS** | TRANTYPE | (setup via JCL only) | CBTRN03C |
| **TRANCATG.VSAM.KSDS** | TRANCATG | (setup via JCL only) | CBTRN03C |
| **DISCGRP.VSAM.KSDS** | DISCGRP | (setup via JCL only) | CBACT04C |
| **USRSEC.VSAM.KSDS** | USRSEC | COUSR01C (write), COUSR02C (rewrite), COUSR03C (delete) | COSGN00C, COUSR00C |
| **DALYTRAN.PS** | DALYTRAN | (external feed) | CBTRN01C, CBTRN02C |

### File Contention Hot Spots

| File | Concurrent Writers | Risk Level | Notes |
|------|-------------------|------------|-------|
| ACCTDATA | 4 programs | **HIGH** | COACTUPC (online) + CBACT04C, CBTRN02C, COBIL00C — batch window must close CICS files first |
| TRANSACT | 3 programs | **MEDIUM** | CBTRN02C (batch posting) + COBIL00C (online) + CBACT04C (interest) |
| CARDXREF | 1 program (CBIMPORT) | LOW | Read-heavy; 12 programs read, only CBIMPORT writes |

---

## 4. Dataset Lineage: JCL → File → Program

### Data Setup Lineage (Initial Load)

```
                    ┌─ ACCTDATA.PS ─── ACCTFILE.jcl ──REPRO──► ACCTDATA.VSAM.KSDS
                    ├─ CARDDATA.PS ─── CARDFILE.jcl ──REPRO──► CARDDATA.VSAM.KSDS
Source Files (.PS)  ├─ CUSTDATA.PS ─── CUSTFILE.jcl ──REPRO──► CUSTDATA.VSAM.KSDS
  (app/data/)       ├─ CARDXREF.PS ─── XREFFILE.jcl ──REPRO──► CARDXREF.VSAM.KSDS
                    ├─ DALYTRAN.PS ─── TRANFILE.jcl ──REPRO──► TRANSACT.VSAM.KSDS
                    ├─ TRANTYPE.PS ─── TRANTYPE.jcl ──REPRO──► TRANTYPE.VSAM.KSDS
                    ├─ TRANCATG.PS ─── TRANCATG.jcl ──REPRO──► TRANCATG.VSAM.KSDS
                    ├─ TCATBALF.PS ─── TCATBALF.jcl ──REPRO──► TCATBALF.VSAM.KSDS
                    ├─ DISCGRP.PS  ─── DISCGRP.jcl  ──REPRO──► DISCGRP.VSAM.KSDS
                    └─ (inline)    ─── DUSRSECJ.jcl  ──REPRO──► USRSEC.VSAM.KSDS
```

### Batch Processing Lineage

```
Daily Transaction Feed
    │
    ▼
DALYTRAN.PS ──────────────────────────────────────────────────────────────
    │                                                                     │
    ├──► POSTTRAN.jcl ──► CBTRN02C                                       │
    │      ├── READS:  DALYTRAN, XREFFILE                                │
    │      ├── READS/REWRITES: ACCTFILE, TCATBALF                        │
    │      ├── WRITES: TRANSACT.VSAM.KSDS (posted transactions)          │
    │      └── WRITES: DALYREJS(+1) (rejected transactions GDG)          │
    │                                                                     │
    ├──► INTCALC.jcl ──► CBACT04C                                        │
    │      ├── READS:  TCATBALF, XREFFILE (KSDS+AIX), DISCGRP           │
    │      ├── READS/REWRITES: ACCTFILE (updates balance with interest)  │
    │      └── WRITES: SYSTRAN(+1) (system-generated interest txns GDG)  │
    │                                                                     │
    ├──► COMBTRAN.jcl (SORT utility)                                     │
    │      ├── READS:  TRANSACT.BKUP(0) + SYSTRAN(0)                    │
    │      ├── SORTS:  by TRAN-ID ascending                              │
    │      └── WRITES: TRANSACT.COMBINED(+1), then REPRO → TRANSACT VSAM│
    │                                                                     │
    ├──► CREASTMT.JCL ──► CBSTM03A → CBSTM03B                           │
    │      ├── STEP010: SORT TRANSACT by card+date                       │
    │      ├── STEP020: REPRO sorted data to temp KSDS (TRXFL.VSAM.KSDS)│
    │      ├── READS:  TRXFL, XREFFILE, CUSTFILE, ACCTFILE              │
    │      ├── WRITES: STATEMNT.PS (text statements)                     │
    │      └── WRITES: STATEMNT.HTML (HTML statements)                   │
    │                                                                     │
    └──► TRANREPT.jcl ──► CBTRN03C                                      │
           ├── STEP05R: REPROC TRANSACT → TRANSACT.BKUP(+1)             │
           ├── STEP05R: SORT by TRAN-CARD-NUM                            │
           ├── READS:  sorted TRANSACT.DALY(+1), CARDXREF, TRANTYPE,    │
           │           TRANCATG, DATEPARM                                │
           └── WRITES: TRANREPT (daily transaction report)               │
```

### Export/Import Lineage

```
CBEXPORT ──── reads ──► CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE
    │
    └── writes ──► EXPORT.DATA (single sequential file with all entity types)
                       │
                       ▼
CBIMPORT ──── reads ──► EXPORT.DATA
    │
    ├── writes ──► CUSTDATA.IMPORT
    ├── writes ──► ACCTDATA.IMPORT
    ├── writes ──► CARDXREF.IMPORT
    ├── writes ──► TRANSACT.IMPORT
    └── writes ──► IMPORT.ERRORS (invalid records)
```

### IMS Database Lineage (Sub-App)

```
Sequential Files
    │
    ├── PAUTDB.ROOT.FILEO ──► LOADPADB.JCL ──► IMS PAUTHDB (root segments)
    ├── PAUTDB.CHILD.FILEO ──► LOADPADB.JCL ──► IMS PAUTHDBX (child segments)
    │
    │                   ┌── UNLDPADB.JCL ──► PAUTDB.ROOT.FILEO + CHILD.FILEO
IMS Database ──────────┤
    │                   └── UNLDGSAM.JCL ──► PAUTDB.ROOT.GSAM + CHILD.GSAM
    │
    └── DBPAUTP0.jcl ──► IMSDATA.DBPAUTP0 (unload for backup)
```

### CICS File Open/Close Cycle

```
Before Batch:  CLOSEFIL.jcl ── SDSF CEMT SET FIL(...) CLO
                                 ├── TRANSACT
                                 ├── CCXREF
                                 ├── ACCTDAT
                                 ├── CXACAIX
                                 └── USRSEC

[BATCH WINDOW RUNS HERE]

After Batch:   OPENFIL.jcl  ── SDSF CEMT SET FIL(...) OPE
                                 ├── TRANSACT
                                 ├── CCXREF
                                 ├── ACCTDAT
                                 ├── CXACAIX
                                 └── USRSEC
```

---

## 5. End-to-End Batch Pipeline Flow

### Daily Processing Sequence

```
┌──────────────────────────────────────────────────────────────────────┐
│  DAILY BATCH PIPELINE (run during overnight batch window)           │
│                                                                      │
│  Step 1: CLOSEFIL.jcl                                               │
│     Close CICS files to prevent online access during batch          │
│                                                                      │
│  Step 2: POSTTRAN.jcl  ──► PGM=CBTRN02C                            │
│     ├── Input:   DALYTRAN.PS (daily transaction feed)               │
│     ├── Lookup:  CARDXREF.VSAM.KSDS (validate card → account)      │
│     ├── Update:  ACCTDATA.VSAM.KSDS (debit/credit account balance) │
│     ├── Update:  TCATBALF.VSAM.KSDS (category balance accumulators)│
│     ├── Output:  TRANSACT.VSAM.KSDS (posted transactions)          │
│     └── Output:  DALYREJS(+1) (rejected transactions → GDG)        │
│                                                                      │
│  Step 3: INTCALC.jcl  ──► PGM=CBACT04C  PARM='YYYYMMDD00'         │
│     ├── Input:   TCATBALF (category balances by account)            │
│     ├── Lookup:  CARDXREF (AIX path for account lookup)             │
│     ├── Lookup:  DISCGRP (interest rates by group/type/category)    │
│     ├── Update:  ACCTDATA (add interest to account balance)         │
│     └── Output:  SYSTRAN(+1) (system-generated interest txns → GDG)|
│                                                                      │
│  Step 4: COMBTRAN.jcl  (SORT utility — no COBOL)                    │
│     ├── Input:   TRANSACT.BKUP(0) + SYSTRAN(0)                     │
│     ├── Sort:    SORT FIELDS=(TRAN-ID,A)                            │
│     └── Output:  TRANSACT.COMBINED(+1), REPRO → TRANSACT VSAM      │
│                                                                      │
│  Step 5: CREASTMT.JCL  ──► PGM=CBSTM03A (calls CBSTM03B)          │
│     ├── Pre-sort: SORT transactions by card + timestamp             │
│     ├── Input:   sorted transactions, XREF, CUST, ACCT             │
│     ├── Output:  STATEMNT.PS (text format statements)               │
│     └── Output:  STATEMNT.HTML (HTML format statements)             │
│                                                                      │
│  Step 6: TRANREPT.jcl  ──► PGM=CBTRN03C                            │
│     ├── Pre-step: REPROC backup TRANSACT → GDG                     │
│     ├── Pre-sort: SORT by TRAN-CARD-NUM ascending                   │
│     ├── Input:   sorted transactions, XREF, TRANTYPE, TRANCATG     │
│     └── Output:  TRANREPT (daily transaction report)                │
│                                                                      │
│  Step 7: TXT2PDF1.JCL  (optional)                                   │
│     └── Converts STATEMNT.PS → PDF format                           │
│                                                                      │
│  Step 8: OPENFIL.jcl                                                │
│     Re-open CICS files for online access                            │
│                                                                      │
│  Step 9: TRANBKP.jcl  (end-of-day cleanup)                         │
│     ├── REPROC current TRANSACT → TRANSACT.BKUP(+1) GDG            │
│     └── IDCAMS DELETE + DEFINE to reset TRANSACT VSAM               │
└──────────────────────────────────────────────────────────────────────┘
```

### Data Flow Summary

```
DALYTRAN.PS ─┐
             ├─► CBTRN02C ──► TRANSACT.VSAM ──┬──► CBSTM03A ──► Statements
             │         │                        │
             │         └──► ACCTDATA.VSAM ◄─────┤
             │                    │              │
             │              CBACT04C ◄── DISCGRP │
             │                    │              │
             │              SYSTRAN GDG          └──► CBTRN03C ──► Report
             │                    │
             └── DALYREJS GDG     └── COMBTRAN ──► TRANSACT.COMBINED
```

---

## 6. Copybook Dependency Matrix

### Programs → Copybooks Referenced

Programs are grouped by type. Copybook names abbreviated for width.

#### Infrastructure Copybooks (included by all CICS programs)

| Copybook | Count | Description |
|----------|-------|-------------|
| DFHBMSCA | 22 | BMS attribute constants |
| DFHAID | 21 | AID key definitions |
| CSMSG01Y | 21 | Standard message area |
| CSDAT01Y | 21 | Date/time working storage |
| COTTL01Y | 21 | Screen title layout |
| COCOM01Y | 21 | COMMAREA structure |

#### Business Data Copybooks

| Copybook | Count | Programs Using It |
|----------|-------|-------------------|
| CVACT03Y (XREF) | 16 | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C + 2 more |
| CVACT01Y (Account) | 16 | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C, COACCT01 + 2 more |
| CSUSR01Y (User) | 14 | COSGN00C, COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COUSR00C–03C + 3 more |
| CVTRA05Y (Transaction) | 11 | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C |
| CVCUS01Y (Customer) | 10 | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| CVACT02Y (Card) | 10 | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COPAUS0C + 1 more |
| CVCRD01Y (Card WS) | 7 | COCRDLIC, COCRDSLC, COCRDUPC, COACTUPC, COACTVWC + 2 more |

---

## 7. Sub-Application Dependency Chains

### Authorization IMS/DB2/MQ Chain

```
MQ Request Queue ──MQGET──► COPAUA0C (Authorization Decision)
                               │
                  ┌────────────┼──────────────────────────┐
                  │            │                          │
           CICS READ      IMS DL/I GU              DB2 INSERT
           (CARDXREF,     (pending auth             (AUTHFRDS
            ACCTDAT,       summary/detail            fraud table)
            CUSTDAT)       segments)
                  │            │                          │
                  └────────────┼──────────────────────────┘
                               │
                          MQPUT1 ──► MQ Reply Queue

COPAUS0C (Summary Browse) ──XCTL──► COPAUS1C (Detail View/Edit)
                                        │
                                   LINK ──► COPAUS2C (Fraud Marking — DB2)

CBPAUP0C (Batch Purge) ── IMS DL/I GN/GNP/DLET ── Deletes expired auths

IMS Load/Unload Utilities:
  PAUDBLOD ── ISRT/GU ── Load from sequential to IMS
  PAUDBUNL ── GN/GNP  ── Unload IMS to sequential
  DBUNLDGS ── GN/GNP  ── GSAM unload
```

### Transaction Type DB2 Chain

```
COADM01C ──XCTL──► COTRTLIC (List — DB2 cursor pagination)
                       │
                  XCTL ──► COTRTUPC (Update/Insert/Delete — DB2)

MNTTRDB2.jcl ──► COBTUPDT (Batch maintenance from sequential input)

TRANEXTR.jcl ──► DB2 UNLOAD to TRANTYPE.PS + TRANCATG.PS

CREADB21.jcl ──► DDL create tables + initial data load
```

### VSAM/MQ Chain

```
MQ Request Queue ──► COACCT01 (Account Inquiry via MQ)
                        │
                    CICS READ ACCTDAT ──► MQ Reply Queue

MQ Request Queue ──► CODATE01 (Date Inquiry via MQ)
                        │
                    System Date ──► MQ Reply Queue
```

---

## 8. Suggested Microservice Boundaries

Based on the dependency analysis, the following boundaries minimize cross-service data coupling:

```
┌─────────────────────────────────────────────────────────────┐
│                    API GATEWAY / BFF                         │
├─────────┬──────────┬──────────┬──────────┬─────────────────┤
│         │          │          │          │                   │
│  user-  │ account- │  card-   │  txn-    │  authorization-  │
│  auth   │ service  │ service  │ service  │  service         │
│  svc    │          │          │          │                   │
│─────────│──────────│──────────│──────────│─────────────────│
│COSGN00C │COACTUPC  │COCRDLIC  │COTRN00C │COPAUA0C          │
│COADM01C │COACTVWC  │COCRDSLC  │COTRN01C │COPAUS0C          │
│COMEN01C │COACCT01  │COCRDUPC  │COTRN02C │COPAUS1C          │
│COUSR00C │          │          │CBTRN01C │COPAUS2C          │
│COUSR01C │          │          │CBTRN02C │CBPAUP0C          │
│COUSR02C │          │          │COBIL00C │PAUDBLOD          │
│COUSR03C │          │          │         │PAUDBUNL          │
│         │          │          │         │DBUNLDGS          │
├─────────┴──────────┴──────────┴─────────┴─────────────────┤
│                                                             │
│  reporting-service    │  txn-type-service  │ data-migration │
│                       │                    │                │
│  CORPT00C             │  COTRTLIC          │  CBEXPORT      │
│  CBSTM03A/B           │  COTRTUPC          │  CBIMPORT      │
│  CBTRN03C             │  COBTUPDT          │                │
│  CBACT04C             │                    │                │
├───────────────────────┴────────────────────┴────────────────┤
│                                                             │
│  Shared Libraries:  carddemo-validation (CSLKPCDY)          │
│                     carddemo-commons (COCOM01Y DTOs)        │
│                     carddemo-security (CSUSR01Y → JWT)      │
└─────────────────────────────────────────────────────────────┘
```

### Cross-Service Data Dependencies

| From Service | To Service | Shared Data | Communication |
|-------------|-----------|-------------|---------------|
| txn-service | account-service | ACCTDATA (balance updates) | Sync REST call |
| txn-service | card-service | CARDXREF (card validation) | Sync REST call |
| authorization-service | account-service | ACCTDATA (balance check) | Sync REST call |
| authorization-service | card-service | CARDXREF (card lookup) | Sync REST call |
| reporting-service | txn-service | TRANSACT (report data) | Async read |
| reporting-service | account-service | ACCTDATA (statement data) | Async read |
