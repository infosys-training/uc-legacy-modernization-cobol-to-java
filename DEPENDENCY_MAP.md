# DEPENDENCY MAP — CardDemo COBOL Estate

> **Call graph, dataset lineage, and end-to-end batch pipeline flow for 44 COBOL programs, 46 JCL jobs, and 12+ VSAM datasets.**

---

## 1. Online Call Graph (CICS XCTL / LINK Chains)

### 1.1 Sign-On → Menu Navigation

```
COSGN00C (Sign-On Entry Point)
│   EXEC CICS READ    → USRSEC file (credential validation)
│   EXEC CICS ASSIGN  → terminal attributes
│
├── [Admin user] ──XCTL──► COADM01C (Admin Menu)
│   │
│   ├── opt 1 ──XCTL──► COUSR00C (User List)
│   │                    ├──XCTL──► COUSR02C (User Update)
│   │                    └──XCTL──► COUSR03C (User Delete)
│   ├── opt 2 ──XCTL──► COUSR01C (User Add)
│   ├── opt 5 ──XCTL──► COTRTLIC (Tran Type List — DB2)
│   └── opt 6 ──XCTL──► COTRTUPC (Tran Type Update — DB2)
│
└── [Regular user] ──XCTL──► COMEN01C (Main Menu Hub — 11 options)
    │
    ├── opt 1  ──XCTL──► COACTVWC  (Account View)
    ├── opt 2  ──XCTL──► COACTUPC  (Account Update — 4,236 LOC)
    ├── opt 3  ──XCTL──► COCRDLIC  (Card List)
    │                     ├──XCTL──► COCRDSLC (Card View)
    │                     └──XCTL──► COCRDUPC (Card Update)
    ├── opt 6  ──XCTL──► COTRN00C  (Transaction List)
    │                     └──XCTL──► COTRN01C (Transaction View)
    ├── opt 8  ──XCTL──► COTRN02C  (Transaction Add)
    ├── opt 9  ──XCTL──► CORPT00C  (Report Request)
    │                     └──WRITEQ TD──► submits INTRDRJ1/J2 JCL
    ├── opt 10 ──XCTL──► COBIL00C  (Bill Payment)
    └── opt 11 ──XCTL──► COPAUS0C  (Auth Summary — IMS)
                          └──LINK──► COPAUS1C (Auth Detail)
                                     └──LINK──► COPAUS2C (Fraud Mark — DB2)
```

### 1.2 CICS Program Dependencies (Adjacency List)

| Source Program | Target Program | Mechanism | Context |
|---------------|---------------|-----------|---------|
| COSGN00C | COADM01C | XCTL | Admin sign-on route |
| COSGN00C | COMEN01C | XCTL | User sign-on route |
| COADM01C | COUSR00C, COUSR01C, COTRTLIC, COTRTUPC | XCTL | Admin menu dispatch |
| COUSR00C | COUSR02C, COUSR03C | XCTL | User list → update/delete |
| COMEN01C | COACTVWC, COACTUPC, COCRDLIC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C | XCTL | Main menu hub dispatch |
| COCRDLIC | COCRDSLC, COCRDUPC | XCTL | Card list → view/update |
| COPAUS0C | COPAUS1C | LINK | Auth summary → detail |
| COPAUS1C | COPAUS2C | LINK | Auth detail → fraud mark |
| CORPT00C | CSUTLDTC | CALL | Date validation subroutine |
| COTRN02C | CSUTLDTC | CALL | Date validation subroutine |

> **Key pattern:** COMEN01C is the hub with 11 outbound XCTL dependencies. COPAUS0C→1C→2C uses LINK (not XCTL) because each level must return control — these must be migrated as a unit.

---

## 2. Batch Call Graph (CALL Statements)

```
CBACT01C ──CALL──► COBDATFT  (assembler: date formatting)
           CALL──► CEE3ABD   (LE: abnormal termination)

CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B  (I/O subroutine: reads TRNX, XREF, CUST, ACCT)
           CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT   (assembler: OS wait)

CSUTLDTC ──CALL──► CEEDAYS   (LE: date conversion)

COPAUA0C ──CALL──► MQOPEN, MQGET, MQPUT1, MQCLOSE  (MQ API)
```

### External Dependencies (Non-COBOL)

| Called Module | Type | Called By | Purpose |
|--------------|------|-----------|---------|
| COBDATFT | Assembler | CBACT01C | Format date fields |
| CEE3ABD | LE Runtime | 9 batch programs | Abnormal termination handler |
| CEEDAYS | LE Runtime | CSUTLDTC | Convert date to Lilian format |
| MVSWAIT | Assembler | COBSWAIT | OS-level wait/sleep |
| MQOPEN/MQGET/MQPUT1/MQCLOSE | MQ API | COPAUA0C, COACCT01, CODATE01 | MQ messaging |
| DSNTIAC | DB2 Utility | COTRTLIC, COTRTUPC (via CSDB2RPY) | Format DB2 error messages |
| DFSRRC00 | IMS Region Controller | JCL (CBPAUP0J, LOADPADB, UNLDPADB, UNLDGSAM) | IMS batch region execution |

---

## 3. Dataset Lineage (JCL → Files → Programs)

### 3.1 VSAM Dataset Map

| Dataset (DD Name) | DSN Pattern | VSAM Type | Writers (Programs) | Readers (Programs) | JCL Define Job |
|-------------------|-------------|-----------|-------------------|-------------------|----------------|
| **ACCTFILE** | AWS.M2.CARDDEMO.ACCT.* | KSDS | CBACT04C (REWRITE), CBTRN02C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE), CBIMPORT (WRITE) | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBSTM03B, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C | ACCTFILE.jcl |
| **CARDFILE** | AWS.M2.CARDDEMO.CARD.* | KSDS | COCRDUPC (REWRITE), CBIMPORT (WRITE) | CBACT02C, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COPAUA0C | CARDFILE.jcl |
| **CUSTFILE** | AWS.M2.CARDDEMO.CUST.* | KSDS | COACTUPC (REWRITE), CBIMPORT (WRITE) | CBCUS01C, CBSTM03B, CBTRN01C, COCRDSLC, COCRDUPC, COACTVWC, COPAUA0C, COPAUS0C | CUSTFILE.jcl |
| **CARDXREF** (XREFFILE) | AWS.M2.CARDDEMO.CARDXREF.* | KSDS | CBIMPORT (WRITE) | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03B, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUS0C | CARDXREF.jcl |
| **TRANSACT** (TRANFILE) | AWS.M2.CARDDEMO.TRANSACT.* | KSDS + AIX | CBTRN02C (WRITE), CBACT04C (WRITE), COBIL00C (WRITE), COTRN02C (WRITE), CBIMPORT (WRITE) | CBTRN03C, COTRN00C, COTRN01C | TRANFILE.jcl |
| **DALYTRAN** | AWS.M2.CARDDEMO.DALYTRAN.* | KSDS | _(external feed)_ | CBTRN01C, CBTRN02C | DALYTRAN.jcl |
| **DALYREJS** | AWS.M2.CARDDEMO.DALYREJS.* | Sequential | CBTRN02C (WRITE) | _(manual review)_ | DALYREJS.jcl |
| **TCATBALF** | AWS.M2.CARDDEMO.TCATBAL.* | KSDS | CBTRN02C (WRITE/REWRITE) | CBACT04C | TCATBALF.jcl |
| **DISCGRP** | AWS.M2.CARDDEMO.DISCGRP.* | KSDS | _(loaded by IDCAMS)_ | CBACT04C | DISCGRP.jcl |
| **TRANTYPE** | AWS.M2.CARDDEMO.TRANTYPE.* | KSDS | _(loaded by IDCAMS)_ | CBTRN03C | TRANTYPE.jcl |
| **TRANCATG** | AWS.M2.CARDDEMO.TRANCATG.* | KSDS | _(loaded by IDCAMS)_ | CBTRN03C | TRANCATG.jcl |
| **USRSEC** | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | KSDS | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | COSGN00C, COUSR00C, COUSR02C, COUSR03C | DUSRSECJ.jcl |

### 3.2 Sequential / Report Files

| File | DD Name | Writers | Readers | Purpose |
|------|---------|---------|---------|---------|
| OUTFILE | OUTFILE | CBACT01C | — | Account data extract (PS COMP) |
| ARRYFILE | ARRYFILE | CBACT01C | — | Account array extract |
| VBRCFILE | VBRCFILE | CBACT01C | — | Variable-length account extract |
| STMTFILE | STMTFILE | CBSTM03A | TXT2PDF1 | Text statements |
| HTMLFILE | HTMLFILE | CBSTM03A | — | HTML statements |
| TRANREPT | TRANREPT | CBTRN03C | — | Transaction detail report |
| EXPFILE | EXPFILE | CBEXPORT | CBIMPORT | Branch migration export |
| ERROUT | ERROUT | CBIMPORT | — | Import error records |
| DATEPARM | DATEPARM | _(config file)_ | CBTRN03C | Report date range parameters |

### 3.3 IMS Databases

| Database | DD Name | Programs | Operations |
|----------|---------|----------|------------|
| Pending Auth (root) | DDPAUTP0 | PAUDBLOD (ISRT), PAUDBUNL (GN), DBUNLDGS (GN), CBPAUP0C (GN/DLET), COPAUA0C (GU), COPAUS0C (GU/GNP), COPAUS1C (GU/REPL) | Hierarchical: summary → detail segments |
| Pending Auth Index | DDPAUTX0 | PAUDBLOD, PAUDBUNL, DBUNLDGS | Secondary index |
| GSAM output | PASFILOP, PADFILOP | DBUNLDGS (ISRT) | Flat-file extract from IMS |

### 3.4 DB2 Tables

| Table | Programs | Operations |
|-------|----------|------------|
| TRANSACTION_TYPE | COTRTLIC (SELECT cursor), COTRTUPC (UPDATE, DELETE), CSDB2LOD (INSERT) | Transaction type reference data |
| TRANSACTION_CATEGORY | COTRTLIC (SELECT), COTRTUPC (DELETE cascade) | Category sub-types |
| SYSIBM.SYSDUMMY1 | COTRTLIC, COTRTUPC (via CSDB2RPY priming query) | DB2 connectivity test |
| Fraud marking table | COPAUS2C (INSERT) | Authorization fraud flags |

### 3.5 MQ Queues

| Queue | Programs | Direction |
|-------|----------|-----------|
| Authorization Request Queue | COPAUA0C | MQGET (consume) |
| Authorization Reply Queue | COPAUA0C | MQPUT1 (produce) |
| Account Inquiry Queue | COACCT01 | MQGET / MQPUT1 |
| Date Inquiry Queue | CODATE01 | MQGET / MQPUT1 |

---

## 4. End-to-End Batch Pipeline Flow

### 4.1 Daily Processing Pipeline

```
┌─────────────────────────────────────────────────────────────────┐
│                     DAILY BATCH CYCLE                           │
│                                                                 │
│  ┌─────────────┐     ┌──────────────┐     ┌──────────────┐    │
│  │  DALYTRAN    │     │   ACCTFILE    │     │  TCATBALF    │    │
│  │ (daily feed) │     │  (accounts)  │     │ (cat balance)│    │
│  └──────┬──────┘     └──────┬───────┘     └──────┬───────┘    │
│         │                   │                     │            │
│         ▼                   ▼                     ▼            │
│  ┌──────────────────────────────────────────────────────┐      │
│  │  Step 1: POSTTRAN.jcl → CBTRN02C                    │      │
│  │  Post daily transactions to master file              │      │
│  │  IN:  DALYTRAN, XREFFILE, ACCTFILE, TCATBALF        │      │
│  │  OUT: TRANSACT (new txns), DALYREJS (rejects)        │      │
│  │  UPD: ACCTFILE (balances), TCATBALF (cat balances)   │      │
│  └──────────────────────┬───────────────────────────────┘      │
│                         │                                      │
│                         ▼                                      │
│  ┌──────────────────────────────────────────────────────┐      │
│  │  Step 2: INTCALC.jcl → CBACT04C                     │      │
│  │  Calculate interest on account balances               │      │
│  │  IN:  TCATBALF, XREFFILE, DISCGRP                    │      │
│  │  UPD: ACCTFILE (interest charges)                     │      │
│  │  OUT: TRANSACT (interest transactions)                │      │
│  └──────────────────────┬───────────────────────────────┘      │
│                         │                                      │
│                         ▼                                      │
│  ┌──────────────────────────────────────────────────────┐      │
│  │  Step 3: CREASTMT.jcl → CBSTM03A → CBSTM03B        │      │
│  │  Generate customer account statements                 │      │
│  │  IN:  XREFFILE, CUSTFILE, ACCTFILE, TRNXFILE         │      │
│  │  OUT: STMTFILE (text), HTMLFILE (HTML)                │      │
│  └──────────────────────┬───────────────────────────────┘      │
│                         │                                      │
│                         ▼                                      │
│  ┌──────────────────────────────────────────────────────┐      │
│  │  Step 4: TRANREPT.jcl → SORT → CBTRN03C            │      │
│  │  Generate daily transaction detail report             │      │
│  │  IN:  TRANSACT, CARDXREF, TRANTYPE, TRANCATG,       │      │
│  │       DATEPARM                                        │      │
│  │  OUT: TRANREPT (report file)                          │      │
│  └──────────────────────────────────────────────────────┘      │
│                                                                 │
│  Optional: TXT2PDF1.JCL (convert STMTFILE → PDF)              │
└─────────────────────────────────────────────────────────────────┘
```

### 4.2 Data Setup Pipeline (One-Time / Refresh)

```
┌─────────────────────────────────────────────────────────────────┐
│                  VSAM DATA INITIALIZATION                       │
│                                                                 │
│  Flat Files (PS)          IDCAMS              VSAM Clusters     │
│  ─────────────          ────────              ──────────────    │
│  ACCTDATA.PS  ──repro──► ACCTFILE.jcl  ──►  ACCT.VSAM.KSDS   │
│  CARDDATA.PS  ──repro──► CARDFILE.jcl  ──►  CARD.VSAM.KSDS   │
│  CUSTDATA.PS  ──repro──► CUSTFILE.jcl  ──►  CUST.VSAM.KSDS   │
│  XREFDATA.PS  ──repro──► CARDXREF.jcl ──►  XREF.VSAM.KSDS   │
│  TCATBAL.PS   ──repro──► TCATBALF.jcl ──►  TCATBAL.VSAM      │
│  DISCGRP.PS   ──repro──► DISCGRP.jcl  ──►  DISCGRP.VSAM      │
│  TRANTYPE.PS  ──repro──► TRANTYPE.jcl ──►  TRANTYPE.VSAM     │
│  TRANCATG.PS  ──repro──► TRANCATG.jcl ──►  TRANCATG.VSAM    │
│  USRSEC.PS    ──repro──► DUSRSECJ.jcl ──►  USRSEC.VSAM.KSDS │
│                                                                 │
│  GDG Base: DEFGDGB.jcl (defines generation data group)         │
│  GDG Data: DEFGDGD.jcl (backup TRANTYPE, TRANCATG, DISCGRP)   │
└─────────────────────────────────────────────────────────────────┘
```

### 4.3 Data Migration Pipeline

```
CBEXPORT (EXPDATA.jcl)                    CBIMPORT (IMPDATA.jcl)
┌─────────────────────┐                   ┌─────────────────────┐
│ IN:                 │                   │ IN:                 │
│  CUSTFILE           │    ┌─────────┐   │  EXPFILE            │
│  ACCTFILE           │───►│ EXPFILE │───►│                     │
│  XREFFILE           │    │ (500B   │   │ OUT:                │
│  TRANSACT           │    │  multi- │   │  CUSTOUT            │
│  CARDFILE           │    │  record)│   │  ACCTOUT            │
│                     │    └─────────┘   │  XREFOUT            │
│ Reads all 5 entity  │                   │  TRNXOUT            │
│ files, writes        │                   │  CARDOUT            │
│ unified export       │                   │  ERROUT (errors)    │
└─────────────────────┘                   └─────────────────────┘
```

### 4.4 IMS Authorization Pipeline

```
┌─────────────────────────────────────────────────────────────────┐
│              IMS AUTHORIZATION SUBSYSTEM                        │
│                                                                 │
│  LOADPADB.JCL → PAUDBLOD          (load IMS database)          │
│       IN: INFILE1 (root), INFILE2 (child)                      │
│       OUT: DDPAUTP0, DDPAUTX0 (IMS DBs)                       │
│                                                                 │
│  [Online Processing]                                            │
│  MQ Request Queue ──► COPAUA0C (auth decision)                 │
│       Reads: ACCTFILE, CUSTFILE, CARDXREF (CICS)               │
│       Reads: IMS pending auth DB (DL/I GU)                     │
│       Writes: IMS pending auth DB (DL/I ISRT)                  │
│       Writes: MQ Reply Queue                                    │
│                                                                 │
│  COPAUS0C → COPAUS1C → COPAUS2C (online browse/update/fraud)   │
│                                                                 │
│  CBPAUP0J.jcl → CBPAUP0C          (purge expired auths)       │
│       IMS DL/I: GN, GNP, DLET                                  │
│                                                                 │
│  UNLDPADB.JCL → PAUDBUNL          (unload IMS → flat files)   │
│       OUT: PAUTDB.ROOT.FILEO, PAUTDB.CHILD.FILEO              │
│                                                                 │
│  UNLDGSAM.JCL → DBUNLDGS          (GSAM unload)               │
│       OUT: PAUTDB.ROOT.GSAM, PAUTDB.CHILD.GSAM                │
│                                                                 │
│  DBPAUTP0.jcl                      (DB utility unload)         │
│       OUT: IMSDATA.DBPAUTP0                                     │
└─────────────────────────────────────────────────────────────────┘
```

---

## 5. Copybook Dependency Matrix

### 5.1 Most-Referenced Copybooks (by program count)

| Copybook | Programs Using It | Role |
|----------|------------------|------|
| COCOM01Y | 20 | CICS communication area (passed via XCTL/LINK) |
| DFHAID | 15 | CICS AID byte definitions |
| DFHBMSCA | 14 | BMS symbolic cursor positioning |
| COTTL01Y | 15 | Screen title constants |
| CSDAT01Y | 15 | Date/time working storage |
| CSMSG01Y | 15 | Common UI messages |
| CSUSR01Y | 10 | User security record |
| CVACT01Y | 10 | Account record layout |
| CVACT03Y | 10 | Card cross-reference layout |
| CVCUS01Y | 8 | Customer record layout |
| CVTRA05Y | 7 | Transaction record layout |
| CVCRD01Y | 6 | CICS work areas (AID keys, navigation) |
| CVACT02Y | 6 | Card record layout |
| CIPAUSMY | 5 | IMS pending auth summary segment |
| CIPAUDTY | 5 | IMS pending auth detail segment |

### 5.2 Programs with Highest Copybook Count

| Program | Unique Copybooks | Notes |
|---------|-----------------|-------|
| COACTUPC | 15 + 31×CSSETATY (REPLACING) | Highest — uses COPY REPLACING extensively |
| COPAUA0C | 14 (+ 8 MQ copybooks) | Spans MQ, IMS, and core entities |
| COPAUS0C | 14 | IMS/BMS integration |
| COACTVWC | 15 | Account view with all entity lookups |
| COCRDSLC | 13 | Card detail with customer lookup |
| COCRDUPC | 13 | Card update with validation |
| CBEXPORT | 6 | All entity copybooks for multi-record export |
| CBIMPORT | 6 | All entity copybooks for import validation |

---

## 6. Shared Resource Contention Points

### 6.1 Files Accessed by Both Batch and Online

| File | Batch Writers | Online Writers | Contention Risk |
|------|--------------|----------------|-----------------|
| **ACCTFILE** | CBTRN02C, CBACT04C, CBIMPORT | COACTUPC, COBIL00C, COTRN02C | **HIGH** — batch updates compete with online updates |
| **CARDFILE** | CBIMPORT | COCRDUPC | Medium |
| **TRANSACT** | CBTRN02C, CBACT04C, CBIMPORT | COTRN02C, COBIL00C | **HIGH** — batch posts and online adds |
| **CUSTFILE** | CBIMPORT | COACTUPC | Medium |

### 6.2 Hub Programs (Highest Fan-Out)

| Program | Outbound Dependencies | Type |
|---------|----------------------|------|
| COMEN01C | 11 programs (XCTL targets) | Navigation hub |
| COADM01C | 6 programs (XCTL targets) | Admin hub |
| CBSTM03A | 1 program (CBSTM03B via CALL) + 4 files | I/O delegation |
| COPAUA0C | 3 VSAM files + IMS DB + 2 MQ queues | Cross-subsystem |

---

*Generated from analysis of CALL/XCTL/LINK statements in 44 COBOL programs, DD statements in 46 JCL jobs, and EXEC CICS/SQL/DLI commands.*
