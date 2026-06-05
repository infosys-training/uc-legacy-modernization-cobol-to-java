# DEPENDENCY MAP — CardDemo COBOL Estate

> **Generated:** 2026-06-05 | **Repository:** `uc-legacy-modernization-cobol-to-java`
> **Programs:** 44 | **Inter-program calls/transfers:** 38 | **VSAM files:** 10 | **JCL jobs:** 46

---

## 1. Online Navigation Call Graph (CICS XCTL/LINK Chains)

```
COSGN00C (Sign-On — Entry Point, TRAN: CCRD)
│
├── [User Type = 'A'] ──► COADM01C (Admin Menu Hub, TRAN: CADM)
│   │
│   ├── Option 1 ──XCTL──► COUSR00C (User List)
│   │                         ├── [Select] ──XCTL──► COUSR02C (User Update)
│   │                         │                         └── [Save/Cancel] ──XCTL──► COUSR00C
│   │                         └── [Delete]  ──XCTL──► COUSR03C (User Delete)
│   │                                                   └── [Confirm/Cancel] ──XCTL──► COUSR00C
│   ├── Option 2 ──XCTL──► COUSR01C (User Add)
│   │                         └── [Save/Cancel] ──XCTL──► COADM01C
│   ├── Option 3 ──XCTL──► COUSR02C (User Update — direct)
│   ├── Option 4 ──XCTL──► COUSR03C (User Delete — direct)
│   ├── Option 5 ──XCTL──► COTRTLIC (Transaction Type List — DB2)
│   │                         └── [Select] ──XCTL──► COTRTUPC (Transaction Type Update — DB2)
│   └── Option 6 ──XCTL──► COTRTUPC (Transaction Type Update — direct)
│
└── [User Type = 'U'] ──► COMEN01C (Main Menu Hub, TRAN: CMNU — 12 options)
    │
    ├── Opt 1 ──XCTL──► COACTVWC (Account View)
    │                     └── [Back] ──XCTL──► COMEN01C
    │
    ├── Opt 2 ──XCTL──► COACTUPC (Account Update — LARGEST, 4,236 LOC)
    │                     ├── CICS READ ACCTFILE, CARDXREF, CUSTFILE
    │                     ├── CICS REWRITE ACCTFILE, CARDXREF, CUSTFILE
    │                     └── [Back] ──XCTL──► COMEN01C
    │
    ├── Opt 4 ──XCTL──► COTRN00C (Transaction List)
    │                     └── [Select] ──XCTL──► COTRN01C (Transaction Detail)
    │                                              └── [Back] ──XCTL──► COTRN00C
    │
    ├── Opt 5 ──XCTL──► COTRN02C (Transaction Add)
    │                     ├── CALL 'CSUTLDTC' (date conversion)
    │                     └── [Back] ──XCTL──► COMEN01C
    │
    ├── Opt 6 ──XCTL──► COCRDLIC (Card List)
    │                     ├── [View]   ──XCTL──► COCRDSLC (Card Detail)
    │                     │                        └── [Back] ──XCTL──► COCRDLIC
    │                     └── [Update] ──XCTL──► COCRDUPC (Card Update)
    │                                             └── [Back] ──XCTL──► COCRDLIC
    │
    ├── Opt 7 ──XCTL──► COBIL00C (Bill Payment)
    │                     └── [Back] ──XCTL──► COMEN01C
    │
    ├── Opt 9 ──XCTL──► CORPT00C (Report Request)
    │                     ├── CALL 'CSUTLDTC' (date conversion)
    │                     ├── Submits JCL: INTRDRJ1 → INTRDRJ2 (batch report)
    │                     └── [Back] ──XCTL──► COMEN01C
    │
    ├── Opt 10 ──XCTL──► COPAUS0C (Auth Summary — IMS/CICS)
    │                      ├── EXEC DLI GU/GNP (IMS navigation)
    │                      └── [Select] ──CICS LINK──► COPAUS1C (Auth Detail)
    │                                                    ├── EXEC DLI GU/GNP/REPL
    │                                                    └── ──CICS LINK──► COPAUS2C (Fraud Flag)
    │                                                                         └── EXEC SQL INSERT (AUTHFRDS)
    │
    └── Opt 12 ──XCTL──► COSGN00C (Sign Off → re-enter sign-on)
```

---

## 2. Batch Program Call Graph

```
CBACT01C ──CALL──► COBDATFT (assembler: date format conversion)
           ──CALL──► CEE3ABD (LE: abnormal termination)

CBACT02C ──CALL──► CEE3ABD (LE: abnormal termination)

CBACT03C ──CALL──► CEE3ABD (LE: abnormal termination)

CBACT04C ──CALL──► CEE3ABD (LE: abnormal termination)

CBCUS01C ──CALL──► CEE3ABD (LE: abnormal termination)

CBSTM03A ──CALL──► CBSTM03B (I/O submodule — 12 calls for file operations)
           ──CALL──► CEE3ABD (LE: abnormal termination)

CBTRN03C ──CALL──► CEE3ABD (LE: abnormal termination)

CBEXPORT ──CALL──► CEE3ABD (LE: abnormal termination)

COBSWAIT ──CALL──► MVSWAIT (assembler: timer/wait control)

CSUTLDTC ──CALL──► CEEDAYS (LE: date conversion service)

CORPT00C ──CALL──► CSUTLDTC (date conversion — online calling batch utility)
COTRN02C ──CALL──► CSUTLDTC (date conversion)

COPAUA0C ──CALL──► MQOPEN (MQ: open queue)
           ──CALL──► MQGET  (MQ: get message from request queue)
           ──CALL──► MQPUT1 (MQ: put message to reply queue)

COACCT01 ──CALL──► MQOPEN / MQGET / MQPUT1 (MQ operations)
CODATE01 ──CALL──► MQOPEN / MQGET / MQPUT1 (MQ operations)
```

### External Dependencies (Non-COBOL)

| External Program | Type | Called By | Replacement Needed |
|-----------------|------|-----------|-------------------|
| COBDATFT | Assembler | CBACT01C | Java DateTimeFormatter |
| MVSWAIT | Assembler | COBSWAIT | Thread.sleep() |
| CEE3ABD | LE Runtime | CBACT01–04, CBCUS01, CBSTM03A, CBTRN03C, CBEXPORT | RuntimeException / System.exit() |
| CEEDAYS | LE Runtime | CSUTLDTC | java.time API |
| MQOPEN/MQGET/MQPUT1 | MQ API | COPAUA0C, COACCT01, CODATE01 | Spring JMS / SQS |

---

## 3. Dataset Lineage — VSAM File Usage

### 3.1 File-to-Program Matrix

| VSAM File (Dataset) | Writers (Programs that WRITE/REWRITE) | Readers (Programs that READ/STARTBR) |
|---------------------|--------------------------------------|--------------------------------------|
| **ACCTFILE** (Account) | CBACT04C (REWRITE — interest calc), COACTUPC (REWRITE — online update), COBIL00C (REWRITE — bill payment), CBTRN02C (REWRITE — balance update), CBIMPORT (WRITE — import) | CBACT01C, CBACT04C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, CBTRN01C, CBTRN02C, CBSTM03A→CBSTM03B, CBEXPORT, COPAUA0C, COACCT01 |
| **CARDFILE** (Card) | COCRDUPC (REWRITE — online update), CBIMPORT (WRITE — import) | CBACT02C, COACTVWC, COCRDLIC (STARTBR/READNEXT/READPREV), COCRDSLC, COCRDUPC, CBTRN01C, CBEXPORT, COPAUS0C |
| **CUSTFILE** (Customer) | CBIMPORT (WRITE — import) | CBCUS01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, CBTRN01C, CBSTM03A→CBSTM03B, CBEXPORT, COPAUA0C |
| **CARDXREF** (Cross-Ref) | CBIMPORT (WRITE — import) | CBACT03C, CBACT04C, COACTUPC, COACTVWC, COBIL00C, COTRN00C, COTRN02C, CBTRN01C, CBTRN02C, CBSTM03A→CBSTM03B, CBTRN03C, CBEXPORT, COPAUA0C, COPAUS0C |
| **TRANSACT** (Transaction Master) | CBTRN02C (WRITE — post daily trans), CBACT04C (WRITE — interest trans), COBIL00C (WRITE — bill payment), CBIMPORT (WRITE — import) | COTRN00C (STARTBR/READNEXT), COTRN01C, CBTRN01C, CBTRN03C, CBSTM03A→CBSTM03B, CBEXPORT |
| **DALYTRAN** (Daily Transactions) | _(external feed)_ | CBTRN02C (READ — input to posting), POSTTRAN.jcl (SORT input) |
| **TCATBALF** (Tran Cat Balance) | _(reference data)_ | CBACT04C |
| **DISCGRP** (Disclosure Group) | _(reference data)_ | CBACT04C |
| **TRANTYPE** (Transaction Type) | _(reference data)_ | CBTRN03C |
| **TRANCATG** (Transaction Category) | _(reference data)_ | CBTRN03C |
| **USRSEC** (User Security) | COUSR01C (WRITE — add user), COUSR02C (REWRITE — update), COUSR03C (DELETE) | COSGN00C, COUSR00C (STARTBR/READNEXT) |

### 3.2 File Contention Analysis

| File | Write Contexts | Risk Level | Notes |
|------|---------------|------------|-------|
| ACCTFILE | 5 writers (2 batch, 3 online) | **HIGH** | Most contended file — critical for dual-write during migration |
| TRANSACT | 4 writers (3 batch, 1 online) | **HIGH** | Transaction master — must serialize writes during migration |
| CARDFILE | 2 writers (1 online, 1 batch) | MEDIUM | Card updates less frequent |
| CUSTFILE | 1 writer (batch import only) | LOW | Read-heavy; only written during import |
| CARDXREF | 1 writer (batch import only) | LOW | Read-heavy; lookup table |
| USRSEC | 3 writers (all online admin) | LOW | Admin-only; low volume |

---

## 4. JCL Job → Dataset → Program Lineage

### 4.1 Batch Processing Pipeline Flow

```
┌─────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH PIPELINE                          │
│                                                                  │
│  External Feed                                                   │
│  (DALYTRAN.PS) ──────┐                                          │
│                       ▼                                          │
│  ┌──────────────────────────────────┐                           │
│  │ POSTTRAN.jcl                     │                           │
│  │ STEP05R: SORT (sort daily trans) │                           │
│  │ STEP10:  CBTRN02C               │                           │
│  │   reads: DALYTRAN, CARDXREF,    │                           │
│  │          ACCOUNT                 │                           │
│  │   writes: TRANSACT (new trans)  │                           │
│  │   rewrites: ACCOUNT (balances)  │                           │
│  └──────────────┬───────────────────┘                           │
│                  ▼                                               │
│  ┌──────────────────────────────────┐                           │
│  │ INTCALC.jcl                      │                           │
│  │ STEP10: CBACT04C                 │                           │
│  │   reads: TCATBAL, XREF,         │                           │
│  │          DISCGRP, ACCOUNT        │                           │
│  │   rewrites: ACCOUNT (interest)  │                           │
│  │   writes: TRANSACT (int. trans) │                           │
│  └──────────────┬───────────────────┘                           │
│                  ▼                                               │
│  ┌──────────────────────────────────┐                           │
│  │ CREASTMT.JCL                     │                           │
│  │ DELDEF01: IDCAMS (define work)   │                           │
│  │ STEP010:  SORT (sort transact)   │                           │
│  │ STEP020:  IDCAMS (REPRO to VSAM) │                           │
│  │ STEP030:  IEFBR14 (delete old)   │                           │
│  │ STEP040:  CBSTM03A              │                           │
│  │   calls: CBSTM03B (I/O sub)     │                           │
│  │   reads: XREFFILE, CUSTFILE,    │                           │
│  │          ACCTFILE, TRNXFILE      │                           │
│  │   writes: STMTFILE (text stmt)  │                           │
│  │           HTMLFILE (HTML stmt)   │                           │
│  └──────────────┬───────────────────┘                           │
│                  ▼                                               │
│  ┌──────────────────────────────────┐                           │
│  │ TRANREPT.jcl                     │                           │
│  │ STEP05R: SORT (sort transact)    │                           │
│  │ STEP10R: CBTRN03C               │                           │
│  │   reads: TRANFILE, CARDXREF,    │                           │
│  │          TRANTYPE, TRANCATG      │                           │
│  │   writes: TRANREPT (report)     │                           │
│  └──────────────────────────────────┘                           │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 Data Setup Pipeline

```
┌───────────────────────────────────────────┐
│         VSAM CLUSTER DEFINITION           │
│                                            │
│  CLOSEFIL.jcl (close CICS files)          │
│         ▼                                  │
│  ACCTFILE.jcl ──► ACCTDATA VSAM KSDS     │
│  CARDFILE.jcl ──► CARDDATA VSAM KSDS+AIX │
│  CUSTFILE.jcl ──► CUSTDATA VSAM KSDS     │
│  XREFFILE.jcl ──► CARDXREF VSAM KSDS+AIX │
│  TRANFILE.jcl ──► TRANSACT VSAM KSDS+AIX │
│  DISCGRP.jcl  ──► DISCGRP  VSAM KSDS     │
│  TCATBALF.jcl ──► TCATBALF VSAM KSDS     │
│  TRANTYPE.jcl ──► TRANTYPE VSAM KSDS     │
│  TRANCATG.jcl ──► TRANCATG VSAM KSDS     │
│  DUSRSECJ.jcl ──► USRSEC   VSAM KSDS     │
│         ▼                                  │
│  OPENFIL.jcl (open CICS files)            │
└───────────────────────────────────────────┘
```

### 4.3 Export/Import Pipeline

```
CBEXPORT.jcl
  STEP01: IDCAMS (define export file)
  STEP02: PGM=CBEXPORT
    reads: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE
    writes: EXPFILE (consolidated export)
         ▼
CBIMPORT.jcl
  STEP01: PGM=CBIMPORT
    reads: EXPFILE
    writes: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT
```

### 4.4 IMS Database Management Pipeline

```
LOADPADB.JCL ──► Load IMS PAUTHDB from sequential files
         ▼
CBPAUP0J.jcl ──► Purge expired auths (PGM=CBPAUP0C via DFSRRC00)
         ▼
UNLDPADB.JCL ──► Unload IMS PAUTHDB to root/child sequential files
UNLDGSAM.JCL ──► Unload via GSAM (PGM=DBUNLDGS)
DBPAUTP0.jcl ──► Unload IMS to IMSDATA dataset
```

### 4.5 DB2 Management Pipeline

```
CREADB21.jcl ──► Create DB2 tables (TRNTYPE, TRNTYCAT)
DEFGDGD.jcl  ──► GDG backup of TRANTYPE + TRANCATG
MNTTRDB2.jcl ──► Batch CRUD maintenance (PGM=COBTUPDT)
TRANEXTR.jcl ──► Extract DB2 data to sequential (PGM=DSNTEP41)
```

---

## 5. Copybook Dependency Graph

### 5.1 Most-Referenced Copybooks (Cross-Cutting)

| Copybook | Referenced By (count) | Purpose |
|----------|----------------------|---------|
| COCOM01Y | 21 programs | COMMAREA — inter-program communication |
| DFHAID | 17 programs | CICS AID byte definitions |
| DFHBMSCA | 17 programs | CICS BMS screen attributes |
| COTTL01Y | 17 programs | Screen title layout |
| CSDAT01Y | 17 programs | Date/time working storage |
| CSMSG01Y | 17 programs | Message display area |
| CVACT01Y | 13 programs | Account record layout |
| CVACT03Y | 12 programs | Card-account cross-reference layout |
| CVCUS01Y | 9 programs | Customer record layout |
| CVACT02Y | 8 programs | Card record layout |
| CVTRA05Y | 8 programs | Transaction record layout |
| CVCRD01Y | 7 programs | PFKey working storage |
| CSUSR01Y | 7 programs | User security record layout |
| CSSTRPFY | 6 programs | PFKey mapping procedure |
| CIPAUSMY | 6 programs | IMS auth summary segment |
| CIPAUDTY | 7 programs | IMS auth detail segment |

### 5.2 Copybook Usage Heatmap

```
                          Core Entities    Infrastructure     IMS/MQ
Program          CVACT01 CVACT02 CVACT03 CVCUS01 COCOM01 CIPAUS CMQODV
────────────────────────────────────────────────────────────────────────
COACTUPC           ✓                ✓       ✓       ✓
COACTVWC           ✓       ✓       ✓       ✓       ✓
COCRDLIC                   ✓                        ✓
COCRDSLC                   ✓               ✓       ✓
COCRDUPC                   ✓               ✓       ✓
COBIL00C           ✓               ✓               ✓
COTRN02C           ✓               ✓               ✓
CBACT04C           ✓               ✓
CBSTM03A           ✓               ✓
CBEXPORT           ✓       ✓       ✓       ✓
CBIMPORT           ✓       ✓       ✓       ✓
COPAUA0C           ✓               ✓       ✓       ✓       ✓       ✓
COPAUS0C           ✓       ✓       ✓       ✓       ✓       ✓
COPAUS1C                                   ✓       ✓       ✓
```

---

## 6. Inter-Program Dependency Summary

### 6.1 Dependency Counts (Outgoing)

| Program | XCTL Out | LINK Out | CALL Out | Total Dependencies |
|---------|----------|----------|----------|-------------------|
| COMEN01C | 11 | 0 | 0 | 11 |
| COADM01C | 6 | 0 | 0 | 6 |
| COUSR00C | 3 | 0 | 0 | 3 |
| COCRDLIC | 2 | 0 | 0 | 2 |
| CBSTM03A | 0 | 0 | 12 (CBSTM03B) | 1 (unique) |
| COPAUA0C | 0 | 0 | 3 (MQ) | 3 |
| CORPT00C | 1 | 0 | 1 (CSUTLDTC) | 2 |
| COTRN02C | 1 | 0 | 1 (CSUTLDTC) | 2 |
| COPAUS1C | 0 | 1 (COPAUS2C) | 0 | 1 |

### 6.2 Programs With Zero Dependencies (Leaf Nodes)

These programs have no outgoing CALL/XCTL/LINK — they are self-contained:
- CBACT02C, CBACT03C, CBCUS01C (file readers)
- CBACT04C (interest calculation)
- CBTRN01C (transaction reader)
- CBTRN02C (transaction posting)
- CBTRN03C (transaction reporting)
- CBEXPORT, CBIMPORT (data migration)
- COTRN01C (transaction detail view)
- COBIL00C (bill payment)
- COBSWAIT (wait utility)
- COPAUS2C (fraud flag — leaf of IMS chain)
- COBTUPDT (batch DB2 update)
