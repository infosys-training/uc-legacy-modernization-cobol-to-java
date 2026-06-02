# DEPENDENCY MAP — CardDemo COBOL Estate

## 1. Program Call Graph

### 1.1 Online Navigation (CICS XCTL / LINK)

The online system is organized as a hub-and-spoke architecture with two menu hubs:

```
COSGN00C (Sign-On — Entry Point)
│
├─── [Admin User] ──XCTL──► COADM01C (Admin Menu Hub)
│                               │
│                               ├── XCTL ──► COUSR00C (User List)
│                               │               ├── XCTL ──► COUSR02C (User Update)
│                               │               └── XCTL ──► COUSR03C (User Delete)
│                               │
│                               ├── XCTL ──► COUSR01C (User Add)
│                               │
│                               ├── XCTL ──► COTRTLIC (Tran Type List — DB2)
│                               │
│                               └── XCTL ──► COTRTUPC (Tran Type Update — DB2)
│
└─── [Regular User] ──XCTL──► COMEN01C (Main Menu Hub — 11 targets)
                                │
                                ├── XCTL ──► COACTVWC (Account View)
                                │
                                ├── XCTL ──► COACTUPC (Account Update)
                                │
                                ├── XCTL ──► COCRDLIC (Card List)
                                │               ├── XCTL ──► COCRDSLC (Card View)
                                │               └── XCTL ──► COCRDUPC (Card Update)
                                │
                                ├── XCTL ──► COTRN00C (Transaction List)
                                │               └── XCTL ──► COTRN01C (Transaction View)
                                │
                                ├── XCTL ──► COTRN02C (Transaction Add)
                                │               └── CALL ──► CSUTLDTC (Date Utility)
                                │
                                ├── XCTL ──► CORPT00C (Report Request)
                                │               ├── CALL ──► CSUTLDTC (Date Utility)
                                │               └── submits ──► INTRDRJ1/J2 (JCL)
                                │
                                ├── XCTL ──► COBIL00C (Bill Payment)
                                │
                                └── XCTL ──► COPAUS0C (Auth Summary — IMS)
                                                ├── LINK ──► COPAUS1C (Auth Detail)
                                                └── LINK ──► COPAUS2C (Fraud Mark — DB2)
```

### 1.2 Batch CALL Graph

```
CBACT01C ──CALL──► COBDATFT (Assembler date formatter)
           CALL──► CEE3ABD  (LE abnormal termination)

CBACT02C ──CALL──► CEE3ABD

CBACT03C ──CALL──► CEE3ABD

CBACT04C ──CALL──► CEE3ABD

CBCUS01C ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B (Statement I/O subroutine)
           CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT  (Assembler wait routine)

CSUTLDTC ──CALL──► CEEDAYS  (LE date conversion)

COTRN02C ──CALL──► CSUTLDTC (Date utility — online calling batch utility)

CORPT00C ──CALL──► CSUTLDTC

COBTUPDT ──(standalone — no CALL dependencies)

CBTRN01C ──(standalone — no CALL dependencies)
CBTRN02C ──(standalone — no CALL dependencies)
CBTRN03C ──CALL──► CEE3ABD
CBEXPORT ──(standalone — no CALL dependencies)
CBIMPORT ──(standalone — no CALL dependencies)
```

### 1.3 IMS/MQ Call Dependencies

```
COPAUA0C ──MQ GET──► AUTH-REQ-QUEUE   (receive authorization request)
           IMS GU ──► PAUTSMY/PAUTDTY (lookup auth data)
           CICS READ──► ACCTFILE, CARDXREF, CUSTFILE
           MQ PUT ──► AUTH-REPLY-QUEUE (send response)

COACCT01 ──MQ GET──► ACCT-REQ-QUEUE   (receive account inquiry)
           CICS READ──► ACCTFILE
           MQ PUT ──► ACCT-REPLY-QUEUE (send response)

CODATE01 ──MQ GET──► DATE-REQ-QUEUE   (receive date request)
           MQ PUT ──► DATE-REPLY-QUEUE (send response)

DBUNLDGS ──IMS GN/GNP──► PAUTSMY, PAUTDTY (read IMS segments)
           GSAM ISRT ──► PASFILOP, PADFILOP (write GSAM files)

PAUDBLOD ──GSAM READ──► INFILE1, INFILE2  (read GSAM input)
           IMS ISRT/GU──► PAUTSMY, PAUTDTY (load IMS segments)

CBPAUP0C ──IMS GN/GNP/DLET──► PAUTSMY, PAUTDTY (purge expired)

COTRTLIC ──EXEC SQL──► TRANSACTION_TYPE, TRANSACTION_CATEGORY (DB2 cursor)
COTRTUPC ──EXEC SQL──► TRANSACTION_TYPE, TRANSACTION_CATEGORY (DB2 DML)
COPAUS2C ──EXEC SQL──► FRAUD_FLAGS (DB2 insert)
COBTUPDT ──EXEC SQL──► TRANSACTION_TYPE (DB2 update)
```

### 1.4 Inter-Program Dependency Matrix

| Caller | Callees | Dependency Type |
|--------|---------|----------------|
| COSGN00C | COADM01C, COMEN01C | XCTL (navigation) |
| COADM01C | COUSR00C, COUSR01C, COTRTLIC, COTRTUPC | XCTL |
| COMEN01C | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C | XCTL |
| COUSR00C | COUSR02C, COUSR03C | XCTL |
| COCRDLIC | COCRDSLC, COCRDUPC | XCTL |
| COTRN00C | COTRN01C | XCTL |
| COPAUS0C | COPAUS1C, COPAUS2C | LINK |
| COTRN02C | CSUTLDTC | CALL |
| CORPT00C | CSUTLDTC | CALL |
| CBSTM03A | CBSTM03B | CALL |
| CBACT01C | COBDATFT | CALL |
| CBACT01C–04C, CBCUS01C, CBSTM03A, CBTRN03C | CEE3ABD | CALL (error handler) |
| COBSWAIT | MVSWAIT | CALL |
| CSUTLDTC | CEEDAYS | CALL |
| COTRTLIC, COTRTUPC, COPAUS2C, COBTUPDT | DSNTIAC | CALL (DB2 error formatting) |

---

## 2. Dataset Lineage

### 2.1 VSAM File Access Map

#### ACCTFILE (Account Master)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBTRN02C (REWRITE balance), CBACT04C (REWRITE interest), COACTUPC (REWRITE update), COBIL00C (REWRITE payment), CBIMPORT (WRITE load) | Batch posting, interest calc, online update, bill pay, import |
| **Readers** | COACTVWC, COACTUPC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C, CBACT01C, CBACT04C, CBSTM03A/B, CBEXPORT, COACCT01 | All account-related operations |

#### CARDFILE (Card Master)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | COCRDUPC (REWRITE), CBIMPORT (WRITE) | Online card update, data import |
| **Readers** | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C, CBEXPORT | Card browse/view/update, batch read |

#### CUSTFILE (Customer Master)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | COACTUPC (REWRITE via account update), CBIMPORT (WRITE) | Customer update, data import |
| **Readers** | COACTVWC, COACTUPC, COCRDSLC, COCRDUPC, COPAUA0C, CBCUS01C, CBSTM03A/B, CBEXPORT | Customer lookup for all entity operations |

#### CARDXREF (Card-Account Cross-Reference)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBIMPORT (WRITE) | Data import only |
| **Readers** | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C, COPAUA0C, CBTRN01C, CBTRN02C, CBACT04C, CBSTM03A/B, CBEXPORT | Card-to-account resolution |

#### TRANSACT (Transaction Master)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBTRN02C (WRITE new), COTRN02C (WRITE online add), COBIL00C (WRITE payment), CBIMPORT (WRITE) | Transaction creation |
| **Readers** | COTRN00C (browse), COTRN01C (view), CORPT00C (report dates), CBTRN03C (report), CBACT04C (interest), CBSTM03A (statements), CBEXPORT | All transaction operations |

#### DALYTRAN (Daily Transaction Input)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writer** | External system (input file) | Daily feed from transaction capture |
| **Readers** | CBTRN01C (validate), CBTRN02C (post) | Daily batch processing |

#### DALYREJS (Daily Rejection Output)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBTRN01C, CBTRN02C | Rejected transaction records |
| **Readers** | External review process | Operations team review |

#### TCATBAL (Transaction Category Balance)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBTRN02C (WRITE/REWRITE) | Balance update during posting |
| **Readers** | CBACT04C | Interest calculation |

#### DISCGRP (Disclosure Group / Interest Rates)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | Setup JCL (IDCAMS REPRO) | Initial data load |
| **Readers** | CBACT04C | Interest rate lookup |

#### TRANTYPE / TRANCATG (Transaction Types & Categories)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | COTRTLIC, COTRTUPC, COBTUPDT (via DB2) | Online/batch maintenance |
| **Readers** | CBTRN03C | Report generation |

#### USRSEC (User Security)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | User CRUD |
| **Readers** | COSGN00C (authentication), COUSR00C (browse) | Sign-on and user management |

### 2.2 JCL Job → Program → File Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH PIPELINE                              │
│                                                                     │
│  POSTTRAN.jcl                                                       │
│  ├── STEP05R: SORT (sort daily transactions)                        │
│  │     Input:  DALYTRAN (daily feed)                                │
│  │     Output: Sorted DALYTRAN                                      │
│  ├── STEP10:  CBTRN01C (validate)                                   │
│  │     Input:  DALYTRAN (sorted), XREFFILE                          │
│  │     Output: Validated transactions, DALYREJS                     │
│  └── STEP15:  CBTRN02C (post)                                      │
│        Input:  DALYTRAN, XREFFILE, ACCTFILE                         │
│        Output: TRANSACT, DALYREJS; Update: ACCTFILE, TCATBAL        │
│                                                                     │
│  INTCALC.jcl                                                        │
│  └── STEP15:  CBACT04C (interest)                                   │
│        Input:  TCATBAL, XREFFILE, DISCGRP, ACCTFILE                 │
│        Output: TRANSACT (interest entries); Update: ACCTFILE         │
│                                                                     │
│  CREASTMT.JCL                                                       │
│  ├── DELDEF01: IDCAMS (define output cluster)                       │
│  ├── STEP010:  SORT (sort transactions by card)                     │
│  │     Input:  TRANSACT                                             │
│  │     Output: TRXFL.SEQ (sorted by card number)                    │
│  ├── STEP020:  IDCAMS (load sorted data)                            │
│  ├── STEP030:  IEFBR14 (catalog)                                    │
│  └── STEP040:  CBSTM03A → CBSTM03B                                 │
│        Input:  TRNX-FILE (sorted), XREF-FILE, CUST-FILE, ACCT-FILE │
│        Output: STMT-FILE (text), HTML-FILE (HTML)                   │
│                                                                     │
│  TRANREPT.jcl                                                       │
│  ├── STEP05R: SORT/REPROC (sort transactions)                       │
│  └── STEP10R: CBTRN03C (report)                                     │
│        Input:  TRANSACT, XREFFILE, TRANTYPE, TRANCATG, DATE-PARMS  │
│        Output: REPORT-FILE (daily transaction report)               │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                    DATA UTILITY JOBS                                 │
│                                                                     │
│  READACCT.jcl ── CBACT01C ── reads ACCTFILE, writes output files    │
│  READCARD.jcl ── CBACT02C ── reads CARDFILE, prints data            │
│  READXREF.jcl ── CBACT03C ── reads XREFFILE, prints data           │
│  READCUST.jcl ── CBCUS01C ── reads CUSTFILE, prints data           │
│  CBEXPORT.jcl ── CBEXPORT ── reads all files → EXPORT-FILE (seq)   │
│  CBIMPORT.jcl ── CBIMPORT ── reads IMPORT-FILE → writes all VSAM   │
│  WAITSTEP.jcl ── COBSWAIT ── utility timer                         │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                    IMS AUTHORIZATION JOBS                            │
│                                                                     │
│  CBPAUP0J.jcl ── DFSRRC00 → CBPAUP0C                               │
│       Purge expired authorizations from IMS PAUTHDB                 │
│                                                                     │
│  LOADPADB.JCL ── DFSRRC00 → PAUDBLOD                               │
│       Load IMS PAUTHDB from GSAM files (INFILE1/INFILE2)            │
│                                                                     │
│  UNLDPADB.JCL ── DFSRRC00 → PAUDBUNL                               │
│       Unload IMS PAUTHDB to sequential files                        │
│                                                                     │
│  UNLDGSAM.JCL ── DFSRRC00 → DBUNLDGS                               │
│       Unload IMS to GSAM (PASFILOP, PADFILOP)                       │
└─────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────┐
│                    VSAM SETUP / MAINTENANCE                         │
│                                                                     │
│  ACCTFILE.jcl ── IDCAMS: DEFINE/DELETE/REPRO ACCTFILE cluster       │
│  CARDFILE.jcl ── IDCAMS: DEFINE CARDFILE + AIX (alt index)         │
│  CUSTFILE.jcl ── IDCAMS: DEFINE CUSTFILE cluster                    │
│  XREFFILE.jcl ── IDCAMS: DEFINE CARDXREF + CXACAIX (AIX)          │
│  TRANFILE.jcl ── IDCAMS: DEFINE TRANSACT + AIX                     │
│  TCATBALF.jcl ── IDCAMS: DEFINE TCATBAL cluster                    │
│  DISCGRP.jcl  ── IDCAMS: DEFINE DISCGRP cluster                    │
│  TRANTYPE.jcl ── IDCAMS: DEFINE TRANTYPE cluster                   │
│  TRANCATG.jcl ── IDCAMS: DEFINE TRANCATG cluster                   │
│  DUSRSECJ.jcl ── IEBGENER + IDCAMS: Load USRSEC from PS           │
│  TRANIDX.jcl  ── IDCAMS: DEFINE AIX on TRANSACT                    │
│  DEFGDGB.jcl  ── IDCAMS: DEFINE GDG bases                          │
│  DEFGDGD.jcl  ── IDCAMS + IEBGENER: Backup DB2 data to GDG        │
│  DALYREJS.jcl ── IDCAMS: DEFINE GDG for rejection files             │
│  REPTFILE.jcl ── IDCAMS: DEFINE GDG for report files                │
│  TRANBKP.jcl  ── REPROC + IDCAMS: Backup/purge TRANSACT           │
│  COMBTRAN.jcl ── SORT + IDCAMS: Merge transaction files             │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Processing Cycle

```
                    External
                    System
                      │
                      ▼
               ┌──────────────┐
               │  DALYTRAN     │  Daily transaction input file
               │  (sequential) │  (from card networks, ATMs, POS)
               └──────┬───────┘
                      │
          ┌───────────▼────────────┐
          │  POSTTRAN.jcl          │
          │  Step 1: SORT          │  Sort by card number
          │  Step 2: CBTRN01C      │  Validate: check XREF, verify card
          │  Step 3: CBTRN02C      │  Post: write TRANSACT, update ACCOUNT
          │                        │  Rejects → DALYREJS
          └───────────┬────────────┘
                      │
                      ▼
            ┌─────────────────┐    ┌──────────────┐
            │  TRANSACT       │    │  ACCTFILE     │  (balance updated)
            │  (VSAM KSDS)    │    │  (VSAM KSDS)  │
            └────────┬────────┘    └───────┬───────┘
                     │                     │
          ┌──────────▼─────────────────────▼──┐
          │  INTCALC.jcl                       │
          │  Step: CBACT04C                    │
          │  - Read TCATBAL, XREF, DISCGRP     │
          │  - Calculate interest per account   │
          │  - Write interest entries to TRANSACT│
          │  - Update ACCOUNT balances          │
          └──────────┬─────────────────────────┘
                     │
          ┌──────────▼─────────────┐
          │  CREASTMT.JCL          │
          │  Step 1: SORT          │  Sort TRANSACT by card number
          │  Step 2: CBSTM03A      │  Generate statements
          │          → CBSTM03B    │  (I/O subroutine)
          │  Reads: TRNX, XREF,    │
          │         CUST, ACCT      │
          │  Writes: STMT (text)    │
          │          HTML (report)  │
          └──────────┬─────────────┘
                     │
          ┌──────────▼─────────────┐
          │  TRANREPT.jcl          │
          │  Step 1: SORT          │  Sort transactions
          │  Step 2: CBTRN03C      │  Generate daily report
          │  Reads: TRANSACT, XREF, │
          │         TRANTYPE,       │
          │         TRANCATG        │
          │  Writes: REPORT-FILE    │
          └────────────────────────┘
```

### 3.2 Periodic / On-Demand Jobs

```
WEEKLY:
  CBEXPORT.jcl → CBEXPORT
    Read: CUSTFILE, ACCTFILE, CARDFILE, XREFFILE, TRANSACT
    Write: EXPORT-FILE (sequential, multi-record format via CVEXPORT.cpy)

ON-DEMAND (from CORPT00C online):
  INTRDRJ1.JCL → TRANREPT pipeline (transaction report)
  INTRDRJ2.JCL → CREASTMT pipeline (statement generation)

IMS MAINTENANCE:
  CBPAUP0J.jcl → CBPAUP0C  (purge expired authorizations — runs nightly)
  LOADPADB.JCL → PAUDBLOD  (IMS database load — initial setup or recovery)
  UNLDPADB.JCL → PAUDBUNL  (IMS database unload — backup)
  UNLDGSAM.JCL → DBUNLDGS  (IMS to GSAM unload — migration)

DATA MIGRATION:
  CBIMPORT.jcl → CBIMPORT
    Read: IMPORT-FILE (sequential, CVEXPORT format)
    Write: ACCTFILE, CARDFILE, CUSTFILE, XREFFILE, TRANSACT

DB2 MAINTENANCE:
  DEFGDGD.jcl — Backup DB2 TRANTYPE/TRANCATG/DISCGRP to GDG
  BINDPLAN.JCL — Bind DB2 plans for COTRTLIC/COTRTUPC
```

### 3.3 MQ Message Flows

```
Authorization Flow (Real-Time):

  External Card Network
         │
         ▼
  ┌──────────────────┐
  │  AUTH-REQ-QUEUE   │  (MQ queue)
  │  Message:         │
  │  CCPAURQY format  │
  └────────┬─────────┘
           │
           ▼
  ┌──────────────────┐
  │  COPAUA0C         │  Authorization Decision Program
  │  1. MQGET request │
  │  2. IMS GU (auth  │  Lookup account/card/customer
  │     summary)      │
  │  3. CICS READ     │  Read ACCTFILE, CARDXREF, CUSTFILE
  │     (VSAM files)  │
  │  4. Evaluate:     │  Check limits, status, fraud flags
  │     approve/deny  │
  │  5. IMS ISRT/REPL │  Update auth records
  │  6. MQPUT response│
  └────────┬─────────┘
           │
           ▼
  ┌──────────────────┐
  │  AUTH-REPLY-QUEUE │  (MQ queue)
  │  Message:         │
  │  CCPAURLY format  │
  └──────────────────┘

Account Inquiry Flow:

  COACCT01: MQGET(ACCT-REQ) → READ(ACCTFILE) → MQPUT(ACCT-REPLY)

Date Inquiry Flow:

  CODATE01: MQGET(DATE-REQ) → get system date → MQPUT(DATE-REPLY)
```

---

## 4. Copybook Dependency Matrix

### Programs → Copybooks Referenced

| Copybook | Used By (count) | Programs |
|----------|-----------------|----------|
| COCOM01Y | 19 | All CICS programs except CSUTLDTC, COBSWAIT, COBTUPDT |
| DFHAID | 17 | All BMS-based CICS programs |
| DFHBMSCA | 17 | All BMS-based CICS programs |
| COTTL01Y | 17 | All BMS-based CICS programs |
| CSDAT01Y | 17 | All BMS-based CICS programs |
| CSMSG01Y | 17 | All BMS-based CICS programs |
| CSUSR01Y | 10 | COSGN00C, COADM01C, COMEN01C, COACTUPC, COUSR00C–03C, COCRDSLC, COCRDUPC |
| CVACT01Y | 12 | COACTUPC, COACTVWC, CBACT01C, CBACT04C, CBSTM03A, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT, COACCT01, CBTRN02C, COBIL00C |
| CVACT03Y | 11 | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, CBACT04C, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COPAUA0C, CBEXPORT |
| CVACT02Y | 8 | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C, CBACT02C, CBEXPORT, CBIMPORT |
| CVCUS01Y | 6 | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, CBEXPORT |
| CVTRA05Y | 9 | COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C, CBTRN01C, CBTRN02C, CBACT04C, CBTRN03C |
| CVCRD01Y | 6 | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COBIL00C |
| CSMSG02Y | 6 | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUS0C, COPAUS1C |
| CSSTRPFY | 5 | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |
| CIPAUDTY | 6 | COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, CBPAUP0C, DBUNLDGS, PAUDBLOD |
| CIPAUSMY | 6 | COPAUA0C, COPAUS0C, COPAUS1C, CBPAUP0C, DBUNLDGS, PAUDBLOD |
| CVEXPORT | 2 | CBEXPORT, CBIMPORT |
| IMSFUNCS | 2 | DBUNLDGS, PAUDBLOD |

---

## 5. Dependency Clustering (Modernization Boundaries)

### Cluster 1: Account Domain
```
Programs: COACTUPC, COACTVWC, COACCT01
Shared Data: ACCTFILE, CUSTFILE, CARDXREF
Copybooks: CVACT01Y, CVCUS01Y, CVACT03Y, CVCRD01Y
External: MQ (COACCT01 only)
```

### Cluster 2: Card Domain
```
Programs: COCRDLIC, COCRDSLC, COCRDUPC
Shared Data: CARDFILE, ACCTFILE, CUSTFILE, CARDXREF
Copybooks: CVACT02Y, CVACT01Y, CVCUS01Y, CVACT03Y, CVCRD01Y
```

### Cluster 3: Transaction Domain
```
Programs: COTRN00C, COTRN01C, COTRN02C, CBTRN01C, CBTRN02C, COBIL00C
Shared Data: TRANSACT, DALYTRAN, ACCTFILE, CARDXREF, TCATBAL
Copybooks: CVTRA05Y, CVTRA06Y, CVACT01Y, CVACT03Y, CVTRA01Y
```

### Cluster 4: Authorization Domain
```
Programs: COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, CBPAUP0C, PAUDBLOD, DBUNLDGS
Shared Data: IMS PAUTHDB (PAUTSMY/PAUTDTY), ACCTFILE, CARDXREF
Copybooks: CIPAUSMY, CIPAUDTY, CCPAURQY, CCPAURLY, CCPAUERY, IMSFUNCS
External: IMS DL/I, MQ, DB2 (COPAUS2C)
```

### Cluster 5: User/Auth Domain
```
Programs: COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C
Shared Data: USRSEC
Copybooks: CSUSR01Y
```

### Cluster 6: Reporting Domain
```
Programs: CORPT00C, CBSTM03A, CBSTM03B, CBTRN03C
Shared Data: TRANSACT, XREFFILE, CUSTFILE, ACCTFILE, TRANTYPE, TRANCATG
Copybooks: CVTRA05Y, CVTRA07Y, CVTRA03Y, CVTRA04Y, COSTM01, CUSTREC
```

### Cluster 7: Transaction Type Domain (DB2)
```
Programs: COTRTLIC, COTRTUPC, COBTUPDT
Shared Data: DB2 TRANSACTION_TYPE, TRANSACTION_CATEGORY
Copybooks: CSDB2RWY, CSDB2RPY
```

### Cluster 8: Data Migration
```
Programs: CBEXPORT, CBIMPORT
Shared Data: All VSAM files + EXPORT-FILE
Copybooks: CVEXPORT + all entity copybooks
```

### Cross-Cluster Dependencies
- **Account ↔ Card**: Both read ACCTFILE, CUSTFILE, CARDXREF
- **Transaction → Account**: CBTRN02C updates ACCTFILE balance
- **Authorization → Account/Card**: COPAUA0C reads ACCTFILE, CARDXREF, CUSTFILE
- **Reporting → Transaction/Account**: CBSTM03A, CBTRN03C read from multiple clusters
- **Data Migration → All**: CBEXPORT/CBIMPORT touch all VSAM files
