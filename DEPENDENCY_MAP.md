# DEPENDENCY MAP — CardDemo Inter-Program & Dataset Lineage

## 1. Online Navigation Call Graph (CICS XCTL/LINK)

```
COSGN00C (Sign-On — Entry Point, Tran: CC00)
│
├──[Admin User Type 'A']──► COADM01C (Admin Menu, Tran: CA00)
│                           │
│                           ├── XCTL ──► COUSR00C (User List)
│                           │              └── XCTL ──► COUSR02C (User Update)
│                           │              └── XCTL ──► COUSR03C (User Delete)
│                           │
│                           ├── XCTL ──► COUSR01C (User Add)
│                           │
│                           ├── XCTL ──► COTRTLIC (Tran Type List — DB2)
│                           │
│                           └── XCTL ──► COTRTUPC (Tran Type Update — DB2)
│
└──[Regular User Type 'U']──► COMEN01C (Main Menu Hub, Tran: CM00)
                               │
                               ├── XCTL ──► COACTVWC (Account View)
                               │
                               ├── XCTL ──► COACTUPC (Account Update)
                               │
                               ├── XCTL ──► COCRDLIC (Card List)
                               │              └── XCTL ──► COCRDSLC (Card View)
                               │              └── XCTL ──► COCRDUPC (Card Update)
                               │
                               ├── XCTL ──► COTRN00C (Transaction List)
                               │              └── XCTL ──► COTRN01C (Transaction View)
                               │
                               ├── XCTL ──► COTRN02C (Transaction Add)
                               │
                               ├── XCTL ──► CORPT00C (Report Request)
                               │              └── submits INTRDRJ1.JCL → INTRDRJ2.JCL
                               │
                               ├── XCTL ──► COBIL00C (Bill Payment)
                               │
                               └── XCTL ──► COPAUS0C (Auth Summary — IMS)
                                              └── LINK ──► COPAUS1C (Auth Detail)
                                                            └── LINK ──► COPAUS2C (Fraud Flag — DB2)
```

### Navigation Pattern Notes
- **XCTL** = Transfer Control — one-way navigation, no return
- **LINK** = Subroutine call — returns to caller (used in IMS auth chain)
- All online programs pass `CARDDEMO-COMMAREA` (COCOM01Y) for context
- PF3 in any program returns to the calling menu (COMEN01C or COADM01C)

---

## 2. Batch Program Call Graph

```
CBACT01C ──CALL──► COBDATFT (Assembler: date formatting)
     │
     └── Reads ACCTFILE → Writes OUTFILE, ARRYFILE, VBRCFILE

CBACT02C ──CALL──► CEE3ABD (LE: abnormal termination)
     │
     └── Reads CARDFILE → Display

CBACT03C ──CALL──► CEE3ABD (LE: abnormal termination)
     │
     └── Reads XREFFILE → Display

CBACT04C (Interest Calculation — standalone, no external CALL)
     │
     └── Reads TCATBALF, XREFFILE, DISCGRP, TRANSACT → Updates ACCTFILE

CBCUS01C ──CALL──► CEE3ABD (LE: abnormal termination)
     │
     └── Reads CUSTFILE → Display

CBSTM03A ──CALL──► CBSTM03B (I/O submodule)
     │
     └── Reads TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE → Writes STMTFILE, HTML-FILE

CBTRN01C (standalone)
     └── Reads DALYTRAN → Validates

CBTRN02C (standalone)
     └── Reads DALYTRAN → Writes TRANSACT, Updates ACCTFILE, XREFFILE

CBTRN03C (standalone)
     └── Reads TRANFILE, CARDXREF, TRANTYPE, TRANCATG → Writes REPTFILE

CBEXPORT (standalone)
     └── Reads CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE → Writes EXPFILE

CBIMPORT (standalone)
     └── Reads EXPFILE → Writes CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT

COBSWAIT ──CALL──► MVSWAIT (Assembler: timed wait)

CSUTLDTC ──CALL──► CEEDAYS (LE: date conversion)

COPAUA0C ──CALL──► MQOPEN, MQGET, MQPUT1 (MQ API calls)
     │
     └── Reads MQ Queue, XREFFILE, ACCTFILE, CUSTFILE, IMS DB → Writes MQ Response

CBPAUP0C (IMS BMP — standalone DL/I calls)
     └── Reads/Deletes IMS auth segments (GN, GNP, DLET)

PAUDBLOD (IMS BMP — standalone DL/I calls)
     └── Reads flat files → Inserts into IMS DB (ISRT, GU)

PAUDBUNL (IMS BMP — standalone DL/I calls)
     └── Reads IMS DB (GN, GNP) → Writes flat files

DBUNLDGS (IMS BMP — standalone DL/I GSAM)
     └── Reads IMS DB (GN, GNP) → Writes GSAM files (ISRT)
```

---

## 3. External Program Dependencies

| External Program | Type | Called By | Purpose |
|-----------------|------|-----------|---------|
| COBDATFT | Assembler | CBACT01C | Date formatting utility |
| CEE3ABD | LE Runtime | CBACT02C, CBACT03C, CBCUS01C | Forced abnormal termination |
| CEEDAYS | LE Runtime | CSUTLDTC | Lilian date conversion |
| MVSWAIT | Assembler | COBSWAIT | Timed wait (milliseconds) |
| MQOPEN | MQ API | COPAUA0C | Open MQ queue |
| MQGET | MQ API | COPAUA0C | Read message from queue |
| MQPUT1 | MQ API | COPAUA0C | Write message to queue |
| DSNTIAC | DB2 Utility | COTRTLIC, COTRTUPC | Format DB2 error messages |
| DFHCSDUP | CICS Utility | CBADMCDJ (JCL) | CSD resource definition |
| DFSRRC00 | IMS Region Controller | CBPAUP0J, LOADPADB, UNLDPADB, UNLDGSAM, DBPAUTP0 (JCL) | BMP execution |

---

## 4. Dataset Lineage — VSAM Files

### 4.1 ACCTFILE (AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS)

| Operation | Programs/Jobs | Context |
|-----------|--------------|---------|
| **DEFINE** | ACCTFILE.jcl (IDCAMS) | KSDS, keys(11,0), recsize(300) |
| **LOAD** | ACCTFILE.jcl (REPRO from .PS) | Initial data load |
| **READ** | CBACT01C, CBACT04C, CBSTM03A/B, CBEXPORT, COACTUPC, COACTVWC, COCRDSLC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C, COACCT01 | 13 programs total |
| **WRITE/UPDATE** | CBACT04C, CBTRN02C, CBIMPORT, COACTUPC, COBIL00C | 5 programs |

### 4.2 CARDFILE (AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS)

| Operation | Programs/Jobs | Context |
|-----------|--------------|---------|
| **DEFINE** | CARDFILE.jcl (IDCAMS) | KSDS + AIX, keys(16,0), recsize(150) |
| **LOAD** | CARDFILE.jcl (REPRO from .PS) | Initial load |
| **READ** | CBACT02C, CBEXPORT, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C, CBSTM03A | 7 programs |
| **WRITE/UPDATE** | CBIMPORT, COCRDUPC | 2 programs |

### 4.3 CUSTFILE (AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS)

| Operation | Programs/Jobs | Context |
|-----------|--------------|---------|
| **DEFINE** | CUSTFILE.jcl (IDCAMS) | KSDS, recsize(500) |
| **LOAD** | CUSTFILE.jcl (REPRO from .PS) | Initial load |
| **READ** | CBCUS01C, CBEXPORT, CBSTM03A/B, COACTUPC, COPAUA0C, COPAUS0C | 6 programs |
| **WRITE/UPDATE** | CBIMPORT | 1 program |

### 4.4 CARDXREF (AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS)

| Operation | Programs/Jobs | Context |
|-----------|--------------|---------|
| **DEFINE** | XREFFILE.jcl (IDCAMS) | KSDS + AIX, keys(16,0), recsize(50) |
| **LOAD** | XREFFILE.jcl (REPRO from .PS) | Initial load |
| **READ** | CBACT03C, CBACT04C, CBEXPORT, CBSTM03A/B, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COCRDSLC, COBIL00C, COTRN00C, COTRN02C, COPAUA0C | 11+ programs |
| **WRITE/UPDATE** | CBIMPORT, CBTRN02C | 2 programs |

### 4.5 TRANSACT (AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS)

| Operation | Programs/Jobs | Context |
|-----------|--------------|---------|
| **DEFINE** | TRANFILE.jcl (IDCAMS) | KSDS, recsize(350) |
| **LOAD** | TRANFILE.jcl, COMBTRAN.jcl | Initial + merged load |
| **READ** | CBACT04C, CBEXPORT, CBTRN03C, COTRN00C, COTRN01C, CREASTMT.JCL (SORT) | 5+ programs |
| **WRITE/UPDATE** | CBTRN02C, CBIMPORT, COTRN02C, COBIL00C | 4 programs |

### 4.6 Additional VSAM Files

| File | Dataset Name | Programs |
|------|-------------|----------|
| TCATBALF | AWS.M2.CARDDEMO.TCATBAL.VSAM.KSDS | R: CBACT04C |
| DISCGRP | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | R: CBACT04C |
| TRANTYPE | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | R: CBTRN03C, TRANREPT.jcl |
| TRANCATG | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | R: CBTRN03C, TRANREPT.jcl |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | RW: COSGN00C, COUSR00C–03C |
| DALYTRAN | AWS.M2.CARDDEMO.DALYTRAN | R: CBTRN01C, CBTRN02C, POSTTRAN.jcl |

---

## 5. JCL Job → Dataset → Program Flow

### 5.1 Daily Batch Pipeline

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     DAILY BATCH PIPELINE                                  │
│                                                                           │
│  ┌──────────┐     ┌──────────────┐     ┌──────────────┐     ┌────────┐ │
│  │POSTTRAN  │────►│   INTCALC    │────►│  CREASTMT    │────►│TRANREPT│ │
│  │(CBTRN02C)│     │ (CBACT04C)   │     │ (CBSTM03A/B)│     │(CBTRN03C)│
│  └────┬─────┘     └──────┬───────┘     └──────┬───────┘     └────┬───┘ │
│       │                   │                    │                   │      │
│  Reads:              Reads:              Reads:              Reads:       │
│  • DALYTRAN          • TCATBALF          • TRNXFILE          • TRANFILE  │
│                      • XREFFILE          • XREFFILE          • CARDXREF  │
│  Writes:             • DISCGRP           • ACCTFILE          • TRANTYPE  │
│  • TRANSACT          • TRANSACT          • CUSTFILE          • TRANCATG  │
│                                                                           │
│  Updates:            Updates:            Writes:              Writes:     │
│  • ACCTFILE          • ACCTFILE          • STMTFILE          • REPTFILE  │
│  • XREFFILE                              • HTML-FILE                     │
│                                                                           │
└─────────────────────────────────────────────────────────────────────────┘
```

### 5.2 Data Setup Pipeline (One-time/Refresh)

```
ACCTFILE.jcl ─► Define KSDS + Load from AWS.M2.CARDDEMO.ACCTDATA.PS
CARDFILE.jcl ─► Define KSDS + AIX + Load from AWS.M2.CARDDEMO.CARDDATA.PS
CUSTFILE.jcl ─► Define KSDS + Load from AWS.M2.CARDDEMO.CUSTDATA.PS
XREFFILE.jcl ─► Define KSDS + AIX + Load from AWS.M2.CARDDEMO.CARDXREF.PS
TRANFILE.jcl ─► Define KSDS + Load from AWS.M2.CARDDEMO.TRANSACT.PS
TCATBALF.jcl ─► Define + Load category balances
DISCGRP.jcl  ─► Define + Load disclosure/interest rates
TRANTYPE.jcl ─► Define + Load transaction types
TRANCATG.jcl ─► Define + Load transaction categories
```

### 5.3 Export/Import Pipeline

```
CBEXPORT.jcl
  STEP01: IDCAMS verify files
  STEP02: PGM=CBEXPORT
    Reads: CUSTFILE + ACCTFILE + XREFFILE + TRANSACT + CARDFILE
    Writes: AWS.M2.CARDDEMO.EXPORT.DATA
         │
         ▼
CBIMPORT.jcl
  STEP01: PGM=CBIMPORT
    Reads: AWS.M2.CARDDEMO.EXPORT.DATA
    Writes: CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT,
            TRANSACT.IMPORT, IMPORT.ERRORS
```

### 5.4 IMS Database Pipeline

```
LOADPADB.JCL ─► PGM=DFSRRC00 (PAUDBLOD)
  Reads: PAUTDB.ROOT.FILEO + PAUTDB.CHILD.FILEO → Inserts into IMS PAUTHDB

UNLDPADB.JCL ─► PGM=DFSRRC00 (PAUDBUNL)
  Reads: IMS PAUTHDB → Writes: PAUTDB.ROOT.FILEO + PAUTDB.CHILD.FILEO

UNLDGSAM.JCL ─► PGM=DFSRRC00 (DBUNLDGS)
  Reads: IMS PAUTHDB → Writes: PAUTDB.ROOT.GSAM + PAUTDB.CHILD.GSAM

CBPAUP0J.jcl ─► PGM=DFSRRC00 (CBPAUP0C)
  Reads/Deletes: Expired auth records from IMS PAUTHDB
```

### 5.5 Statement Pipeline Detail

```
CREASTMT.JCL
  DELDEF01: IDCAMS — delete/define temp TRXFL.VSAM.KSDS
  STEP010:  SORT — extract transactions from TRANSACT.VSAM.KSDS → TRXFL.SEQ
  STEP020:  IDCAMS REPRO — load sorted data into TRXFL.VSAM.KSDS
  STEP030:  IEFBR14 — delete old STATEMNT.HTML + STATEMNT.PS
  STEP040:  PGM=CBSTM03A — generate statements
              Reads: TRXFL.VSAM, CARDXREF, ACCTDATA, CUSTDATA
              Writes: STATEMNT.PS, STATEMNT.HTML
         │
         ▼ (optional)
TXT2PDF1.JCL
  TXT2PDF: Convert STATEMNT.PS → PDF output
```

---

## 6. GDG (Generation Data Group) Lineage

| GDG Base | Defined By | Written By | Read By |
|----------|-----------|-----------|---------|
| AWS.M2.CARDDEMO.TRANSACT.BKUP | DEFGDGB.jcl | TRANBKP.jcl, TRANREPT.jcl | COMBTRAN.jcl |
| AWS.M2.CARDDEMO.TRANSACT.DALY | DEFGDGB.jcl | TRANREPT.jcl (SORT) | TRANREPT.jcl (CBTRN03C) |
| AWS.M2.CARDDEMO.SYSTRAN | DEFGDGB.jcl | External (system) | COMBTRAN.jcl |
| AWS.M2.CARDDEMO.TRANSACT.COMBINED | DEFGDGB.jcl | COMBTRAN.jcl | COMBTRAN.jcl (→VSAM) |
| AWS.M2.CARDDEMO.DALYREJS | DALYREJS.jcl | CBTRN02C (rejects) | Manual review |

---

## 7. Inter-Program Dependency Matrix (Adjacency)

| Program | Depends On (calls/XCTLs to) | Depended On By (called/XCTLed from) |
|---------|----------------------------|-------------------------------------|
| COSGN00C | — | Entry point |
| COADM01C | COSGN00C | COUSR00C–03C, COTRTLIC, COTRTUPC |
| COMEN01C | COSGN00C | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C–02C, CORPT00C, COBIL00C, COPAUS0C |
| COACTUPC | COMEN01C | — |
| COACTVWC | COMEN01C | — |
| COCRDLIC | COMEN01C | COCRDSLC, COCRDUPC |
| COCRDSLC | COCRDLIC, COMEN01C | — |
| COCRDUPC | COCRDLIC, COMEN01C | — |
| COTRN00C | COMEN01C | COTRN01C |
| COTRN01C | COTRN00C, COMEN01C | — |
| COTRN02C | COMEN01C | — |
| CORPT00C | COMEN01C | Submits INTRDRJ1/J2 |
| COBIL00C | COMEN01C | — |
| COUSR00C | COADM01C | COUSR02C, COUSR03C |
| COUSR01C | COADM01C | — |
| COUSR02C | COUSR00C | — |
| COUSR03C | COUSR00C | — |
| COTRTLIC | COADM01C | — |
| COTRTUPC | COADM01C | — |
| COPAUS0C | COMEN01C | COPAUS1C |
| COPAUS1C | COPAUS0C | COPAUS2C |
| COPAUS2C | COPAUS1C | — |
| COPAUA0C | MQ trigger | — |
| CBSTM03A | CREASTMT.JCL | CBSTM03B |
| CBSTM03B | CBSTM03A | — |
| CBTRN02C | POSTTRAN.jcl | — |
| CBACT04C | INTCALC.jcl | — |
| CBTRN03C | TRANREPT.jcl | — |
| CBEXPORT | CBEXPORT.jcl | — |
| CBIMPORT | CBIMPORT.jcl | — |

---

## 8. Shared Resource Contention Points

| Resource | Concurrent Writers | Risk |
|----------|-------------------|------|
| ACCTFILE | COACTUPC, COBIL00C, CBACT04C, CBTRN02C, CBIMPORT | **HIGH** — 5 writers; requires sequencing or locking |
| TRANSACT | COTRN02C, CBTRN02C, COBIL00C, CBIMPORT | **MEDIUM** — 4 writers |
| CARDXREF | CBTRN02C, CBIMPORT | LOW — 2 writers (batch only) |
| CARDFILE | COCRDUPC, CBIMPORT | LOW — 2 writers |
| USRSEC | COUSR01C, COUSR02C, COUSR03C | LOW — admin-only, sequential |
