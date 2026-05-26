# Dependency Map — CardDemo COBOL Estate

## Overview

This document maps the inter-program call graph, dataset lineage (which JCL jobs read/write which files and which programs process them), and the end-to-end batch pipeline flow for the CardDemo application.

---

## 1. Inter-Program Call Graph

### 1.1 CALL Relationships (Batch)

```
CBACT01C ──CALL──► COBDATFT  (assembler date formatter)
         ──CALL──► CEE3ABD   (LE abend routine)

CBACT02C ──CALL──► CEE3ABD

CBACT03C ──CALL──► CEE3ABD

CBACT04C ──CALL──► CEE3ABD

CBCUS01C ──CALL──► CEE3ABD

CBEXPORT ──CALL──► CEE3ABD

CBIMPORT ──CALL──► CEE3ABD

CBTRN01C ──CALL──► CEE3ABD

CBTRN02C ──CALL──► CEE3ABD

CBTRN03C ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B  (subroutine: file reads for statement)  ×13 calls
         ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT   (assembler wait routine)

CSUTLDTC ──CALL──► CEEDAYS   (LE date intrinsic)
```

### 1.2 CALL Relationships (Online → Batch Utilities)

```
CORPT00C ──CALL──► CSUTLDTC  (date validation)  ×2
COTRN02C ──CALL──► CSUTLDTC  (date validation)  ×2
```

### 1.3 CICS Transfer-of-Control (XCTL / LINK)

```
COSGN00C ─────────► COMEN01C  (regular user menu)
                   ► COADM01C  (admin menu)

COMEN01C ──XCTL──► COACTVWC  (Account View)
         ──XCTL──► COACTUPC  (Account Update)
         ──XCTL──► COCRDLIC  (Credit Card List)
         ──XCTL──► COCRDSLC  (Credit Card View)
         ──XCTL──► COCRDUPC  (Credit Card Update)
         ──XCTL──► COTRN00C  (Transaction List)
         ──XCTL──► COTRN01C  (Transaction View)
         ──XCTL──► COTRN02C  (Transaction Add)
         ──XCTL──► CORPT00C  (Transaction Reports)
         ──XCTL──► COBIL00C  (Bill Payment)
         ──XCTL──► COPAUS0C  (Pending Auth View)

COADM01C ──XCTL──► COUSR00C  (User List)
         ──XCTL──► COUSR01C  (User Add)
         ──XCTL──► COUSR02C  (User Update)
         ──XCTL──► COUSR03C  (User Delete)
         ──XCTL──► COTRTLIC  (Tran Type List — Db2)
         ──XCTL──► COTRTUPC  (Tran Type Update — Db2)

COCRDLIC ──XCTL──► COCRDSLC  (Card Detail from list)
         ──XCTL──► COCRDUPC  (Card Update from list)
         ──XCTL──► COMEN01C  (return to menu)

COCRDSLC ──XCTL──► COMEN01C  (return to menu)

COCRDUPC ──XCTL──► COMEN01C  (return to menu)

COACTVWC ──XCTL──► COMEN01C  (return to menu)

COACTUPC ──XCTL──► COMEN01C  (return to menu)

COPAUS0C ──XCTL──► COMEN01C  (return to menu)

COPAUS1C ──LINK──► COPAUS2C  (mark fraud — Db2 update)

COTRTLIC ──XCTL──► COTRTUPC  (Tran Type Update)
         ──XCTL──► COADM01C  (return to admin menu)
```

### 1.4 MQ Call Relationships

```
COPAUA0C ──MQGET──► Request Queue (inbound authorization request)
         ──MQPUT1─► Reply Queue   (outbound authorization response)

COACCT01 ──MQOPEN/MQGET/MQPUT/MQCLOSE──► Account inquiry via MQ

CODATE01 ──MQOPEN/MQGET/MQPUT/MQCLOSE──► Date inquiry via MQ
```

### 1.5 IMS DL/I Call Relationships

```
CBPAUP0C ──CBLTDLI (GN/GNP/DLET)──► PAUTHDB  (purge expired)

DBUNLDGS ──CBLTDLI (GN/GNP)──────► PAUTHDB  (unload to GSAM)
         ──CBLTDLI (ISRT)─────────► GSAM output files

PAUDBLOD ──CBLTDLI (ISRT/GU)─────► PAUTHDB  (load from flat files)

PAUDBUNL ──CBLTDLI (GN/GNP)──────► PAUTHDB  (unload to flat files)
```

---

## 2. Dataset Lineage

### 2.1 VSAM KSDS Datasets

| Dataset (DSN) | DD Name(s) | Defined By (JCL) | Written By | Read By |
|---------------|-----------|-------------------|-----------|---------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | ACCTFILE | ACCTFILE.jcl | ACCTFILE.jcl (REPRO from PS) | CBACT01C, CBACT04C (I-O), CBEXPORT, CBTRN01C, CBTRN02C (I-O), CBSTM03A/B, CICS programs |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CARDFILE, CARDDAT | CARDFILE.jcl | CARDFILE.jcl (REPRO) | CBACT02C, CBEXPORT, CBTRN01C, CICS programs |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.AIX | CARDAIX | CARDFILE.jcl | CARDFILE.jcl (BLDINDEX) | COCRDLIC (STARTBR by account) |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CUSTFILE | CUSTFILE.jcl | CUSTFILE.jcl (REPRO) | CBCUS01C, CBEXPORT, CBTRN01C, CBSTM03A/B, CICS programs |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | XREFFILE, CARDXREF | XREFFILE.jcl | XREFFILE.jcl (REPRO) | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A/B, CICS programs |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.AIX.PATH | XREFFIL1 | XREFFILE.jcl | XREFFILE.jcl (BLDINDEX) | CBACT04C (alternate key access) |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | TRANFILE, TRANSACT | TRANFILE.jcl | CBTRN02C, COMBTRAN.jcl (REPRO) | CBEXPORT, CBTRN01C, CBTRN03C, CREASTMT.jcl (SORT), TRANBKP.jcl (REPROC), TRANREPT.jcl (REPROC), CICS programs |
| AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | TCATBALF | TCATBALF.jcl | TCATBALF.jcl (REPRO), CBTRN02C (I-O) | CBACT04C, CBTRN02C (I-O), PRTCATBL.jcl (REPROC) |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | TRANTYPE | TRANTYPE.jcl | TRANTYPE.jcl (REPRO) | CBTRN03C |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | TRANCATG | TRANCATG.jcl | TRANCATG.jcl (REPRO) | CBTRN03C |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | DISCGRP | DISCGRP.jcl | DISCGRP.jcl (REPRO) | CBACT04C |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | USRSEC | DUSRSECJ.jcl | DUSRSECJ.jcl (REPRO), COUSR01C (WRITE) | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |

### 2.2 Sequential / GDG Datasets

| Dataset (DSN) | DD Name(s) | Written By | Read By |
|---------------|-----------|-----------|---------|
| AWS.M2.CARDDEMO.ACCTDATA.PS | ACCTDATA | Initial load data | ACCTFILE.jcl (REPRO source) |
| AWS.M2.CARDDEMO.CARDDATA.PS | CARDDATA | Initial load data | CARDFILE.jcl (REPRO source) |
| AWS.M2.CARDDEMO.CUSTDATA.PS | CUSTDATA | Initial load data | CUSTFILE.jcl (REPRO source) |
| AWS.M2.CARDDEMO.CARDXREF.PS | XREFDATA | Initial load data | XREFFILE.jcl (REPRO source) |
| AWS.M2.CARDDEMO.DALYTRAN.PS | DALYTRAN | External feed (daily) | CBTRN02C (POSTTRAN.jcl) |
| AWS.M2.CARDDEMO.DALYTRAN.PS.INIT | — | Initial seed data | TRANFILE.jcl (REPRO source) |
| AWS.M2.CARDDEMO.DALYREJS(+n) | DALYREJS | CBTRN02C | Reject analysis (manual) |
| AWS.M2.CARDDEMO.SYSTRAN(+n) | SYSTRAN | CBACT04C (interest calc) | COMBTRAN.jcl (SORT merge) |
| AWS.M2.CARDDEMO.TRANSACT.BKUP(+n) | — | TRANBKP.jcl/TRANREPT.jcl (REPROC) | COMBTRAN.jcl (SORT merge), TRANREPT.jcl (SORT) |
| AWS.M2.CARDDEMO.TRANSACT.COMBINED(+n) | — | COMBTRAN.jcl (SORT) | COMBTRAN.jcl (REPRO to VSAM) |
| AWS.M2.CARDDEMO.TRANSACT.DALY(+n) | — | TRANREPT.jcl (SORT) | CBTRN03C |
| AWS.M2.CARDDEMO.TCATBALF.BKUP(+n) | — | PRTCATBL.jcl (REPROC) | PRTCATBL.jcl (SORT) |
| AWS.M2.CARDDEMO.TCATBALF.REPT | — | PRTCATBL.jcl (SORT) | Report viewing |
| AWS.M2.CARDDEMO.ACCTDATA.PSCOMP | OUTFILE | CBACT01C | External consumption |
| AWS.M2.CARDDEMO.ACCTDATA.ARRYPS | ARRYFILE | CBACT01C | External consumption |
| AWS.M2.CARDDEMO.ACCTDATA.VBPS | VBRCFILE | CBACT01C | External consumption |
| AWS.M2.CARDDEMO.EXPORT.DATA | EXPFILE | CBEXPORT | CBIMPORT |
| AWS.M2.CARDDEMO.*.IMPORT | CUSTOUT, ACCTOUT, etc. | CBIMPORT | Target system load |
| AWS.M2.CARDDEMO.IMPORT.ERRORS | ERROUT | CBIMPORT | Error analysis |
| AWS.M2.CARDDEMO.DATEPARM | DATEPARM | Manual/config | CBTRN03C |
| AWS.M2.CARDDEMO.STATEMNT.PS | STMTFILE | CBSTM03A | TXT2PDF1.JCL, viewing |
| AWS.M2.CARDDEMO.STATEMNT.HTML | HTMLFILE | CBSTM03A | Web/email distribution |
| AWS.M2.CARDDEMO.TRXFL.SEQ | — | CREASTMT.JCL (SORT) | CREASTMT.JCL (REPRO to KSDS) |
| AWS.M2.CARDDEMO.TRXFL.VSAM.KSDS | TRNXFILE | CREASTMT.JCL (REPRO) | CBSTM03A |
| AWS.M2.CARDDEMO.TRANTYPE.PS | — | Db2 unload (TRANEXTR) | DEFGDGD.jcl backup |
| AWS.M2.CARDDEMO.TRANCATG.PS | — | Db2 unload (TRANEXTR) | DEFGDGD.jcl backup |
| AWS.M2.CARDDEMO.DISCGRP.PS | — | Db2 unload (TRANEXTR) | DEFGDGD.jcl backup, DISCGRP.jcl load |

### 2.3 IMS Datasets

| Dataset | Written By | Read By |
|---------|-----------|---------|
| OEM.IMS.IMSP.PAUTHDB | PAUDBLOD (load), COPAUA0C (CICS insert) | CBPAUP0C (purge), DBUNLDGS (unload), PAUDBUNL (unload), COPAUS0C/1C (browse) |
| AWS.M2.CARDDEMO.PAUTDB.ROOT.FILEO | PAUDBUNL (unload) | PAUDBLOD (load) |
| AWS.M2.CARDDEMO.PAUTDB.CHILD.FILEO | PAUDBUNL (unload) | PAUDBLOD (load) |
| AWS.M2.CARDDEMO.PAUTDB.ROOT.GSAM | DBUNLDGS (GSAM unload) | External consumption |
| AWS.M2.CARDDEMO.PAUTDB.CHILD.GSAM | DBUNLDGS (GSAM unload) | External consumption |

### 2.4 Db2 Tables

| Table | Accessed By | Operations |
|-------|-----------|------------|
| CARDDEMO.TRANSACTION_TYPE | COTRTLIC (SELECT), COTRTUPC (SELECT/UPDATE), COBTUPDT (INSERT/UPDATE/DELETE), TRANEXTR.jcl (unload) | Full CRUD |
| CARDDEMO.TRAN_CATEGORY | COTRTUPC (SELECT) | Read-only |
| PAUTH_FRAUD | COPAUS2C (SELECT/INSERT/UPDATE) | Fraud flagging |

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Batch Cycle

The daily batch processing follows a strict sequence orchestrated by Control-M:

```
┌─────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH PIPELINE                              │
│                                                                     │
│  Phase 1: CLOSE                                                     │
│  ┌──────────┐                                                       │
│  │CLOSEFIL  │  Close VSAM files in CICS region                     │
│  │(SDSF)    │  (CEMT SET FIL CLO for all files)                    │
│  └────┬─────┘                                                       │
│       │                                                             │
│  Phase 2: BACKUP                                                    │
│  ┌────▼─────┐                                                       │
│  │TRANBKP   │  Backup Transaction Master VSAM → GDG                │
│  │(REPROC + │  Then delete/redefine TRANSACT VSAM                  │
│  │ IDCAMS)  │                                                       │
│  └────┬─────┘                                                       │
│       │                                                             │
│  Phase 3: POST DAILY TRANSACTIONS                                   │
│  ┌────▼─────┐    ┌──────────┐    ┌──────────────┐                  │
│  │POSTTRAN  │───►│CBTRN02C  │───►│Outputs:      │                  │
│  │(JCL)     │    │(program) │    │• TRANSACT    │(new transactions)│
│  │          │    │          │    │• DALYREJS GDG│(daily rejects)   │
│  │Inputs:   │    │Updates:  │    └──────────────┘                  │
│  │• DALYTRAN│    │• ACCTFILE│(account balances)                    │
│  │• XREFFILE│    │• TCATBALF│(category balances)                   │
│  │• ACCTFILE│    │          │                                       │
│  │• TCATBALF│    └──────────┘                                       │
│  └────┬─────┘                                                       │
│       │                                                             │
│  Phase 4: COMBINE TRANSACTIONS                                      │
│  ┌────▼─────┐                                                       │
│  │COMBTRAN  │  SORT merge: TRANSACT.BKUP + SYSTRAN → COMBINED     │
│  │(SORT +   │  REPRO COMBINED → TRANSACT VSAM                     │
│  │ IDCAMS)  │                                                       │
│  └────┬─────┘                                                       │
│       │                                                             │
│  Phase 5: REPORTING                                                 │
│  ┌────▼─────┐    ┌──────────┐                                      │
│  │TRANREPT  │───►│CBTRN03C  │──► Transaction Detail Report (GDG)  │
│  │(REPROC + │    │(program) │                                      │
│  │ SORT)    │    │Reads: TRANSACT, XREFFILE, TRANTYPE, TRANCATG,  │
│  │          │    │       DATEPARM                                  │
│  └────┬─────┘    └──────────┘                                      │
│       │                                                             │
│  Phase 6: OPEN                                                      │
│  ┌────▼─────┐                                                       │
│  │OPENFIL   │  Open VSAM files in CICS region                     │
│  │(SDSF)    │  (CEMT SET FIL OPE for all files)                   │
│  └──────────┘                                                       │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2 Monthly/Periodic Batch Cycle

```
┌─────────────────────────────────────────────────────────────────────┐
│                 PERIODIC BATCH PIPELINE                              │
│                                                                     │
│  Interest Calculation (Monthly)                                     │
│  ┌──────────┐    ┌──────────┐    ┌──────────────┐                  │
│  │INTCALC   │───►│CBACT04C  │───►│Outputs:      │                  │
│  │(JCL)     │    │(program) │    │• SYSTRAN GDG │(interest txns)   │
│  │Inputs:   │    │Updates:  │    └──────────────┘                  │
│  │• TCATBALF│    │• ACCTFILE│(updated balances)                    │
│  │• XREFFILE│    │          │                                       │
│  │• DISCGRP │    └──────────┘                                       │
│  └──────────┘                                                       │
│                                                                     │
│  Statement Generation (Monthly)                                     │
│  ┌──────────┐    ┌──────────┐    ┌──────────────┐                  │
│  │CREASTMT  │───►│CBSTM03A  │───►│Outputs:      │                  │
│  │(JCL)     │    │calls     │    │• STATEMNT.PS │(text statements) │
│  │Prep:     │    │CBSTM03B  │    │• STATEMNT.HTML│(HTML statements)│
│  │• SORT    │    │(×13)     │    └──────┬───────┘                  │
│  │• REPRO   │    │Reads:    │           │                           │
│  │          │    │• TRNXFILE│    ┌──────▼───────┐                  │
│  │          │    │• XREFFILE│    │TXT2PDF1      │                  │
│  │          │    │• CUSTFILE│    │(JCL)         │──► PDF output    │
│  │          │    │• ACCTFILE│    └──────────────┘                  │
│  └──────────┘    └──────────┘                                       │
│                                                                     │
│  Category Balance Report                                            │
│  ┌──────────┐                                                       │
│  │PRTCATBL  │  REPROC TCATBALF → GDG backup → SORT → report       │
│  └──────────┘                                                       │
│                                                                     │
│  IMS Authorization Purge                                            │
│  ┌──────────┐    ┌──────────┐                                      │
│  │CBPAUP0J  │───►│CBPAUP0C  │  Delete expired pending auths       │
│  │(IMS BMP) │    │(program) │  from IMS PAUTHDB                   │
│  └──────────┘    └──────────┘                                       │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.3 Data Migration Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                 BRANCH MIGRATION PIPELINE                           │
│                                                                     │
│  Export (Source Branch)                                              │
│  ┌──────────┐    ┌──────────┐    ┌──────────────┐                  │
│  │CBEXPORT  │───►│CBEXPORT  │───►│EXPORT.DATA   │                  │
│  │(JCL)     │    │(program) │    │(unified      │                  │
│  │Reads:    │    │          │    │ sequential)  │                  │
│  │• CUSTFILE│    │Merges all│    └──────┬───────┘                  │
│  │• ACCTFILE│    │entities  │           │                           │
│  │• XREFFILE│    │into typed│    ┌──────▼───────┐                  │
│  │• TRANSACT│    │records   │    │FTP/Transfer  │                  │
│  │• CARDFILE│    └──────────┘    └──────┬───────┘                  │
│  └──────────┘                           │                           │
│                                         │                           │
│  Import (Target Branch)                 │                           │
│  ┌──────────┐    ┌──────────┐    ┌──────▼───────┐                  │
│  │CBIMPORT  │───►│CBIMPORT  │◄───│EXPORT.DATA   │                  │
│  │(JCL)     │    │(program) │    └──────────────┘                  │
│  │Writes:   │    │Splits by │                                       │
│  │• CUSTOUT │    │rec type  │                                       │
│  │• ACCTOUT │    │& writes  │                                       │
│  │• XREFOUT │    │per-entity│                                       │
│  │• TRNXOUT │    │files     │                                       │
│  │• ERROUT  │    └──────────┘                                       │
│  └──────────┘                                                       │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.4 IMS Database Maintenance Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│              IMS DATABASE MAINTENANCE                                │
│                                                                     │
│  Unload (for backup/migration):                                     │
│  UNLDPADB.JCL → PAUDBUNL → flat files (ROOT.FILEO + CHILD.FILEO)  │
│  UNLDGSAM.JCL → DBUNLDGS → GSAM files (ROOT.GSAM + CHILD.GSAM)    │
│                                                                     │
│  Reload (from backup):                                              │
│  LOADPADB.JCL → PAUDBLOD ← flat files (ROOT.FILEO + CHILD.FILEO)  │
│                                                                     │
│  Purge expired:                                                     │
│  CBPAUP0J.jcl → CBPAUP0C → delete expired records from PAUTHDB    │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 4. CICS Online Data Flow

```
┌──────────────────────────────────────────────────────────────┐
│                    CICS ONLINE FLOW                           │
│                                                              │
│  3270 Terminal                                               │
│       │                                                      │
│       ▼                                                      │
│  ┌─────────┐                                                 │
│  │COSGN00C │  Sign-on → reads USRSEC VSAM                   │
│  │(Sign-on)│                                                 │
│  └────┬────┘                                                 │
│       │                                                      │
│  ┌────▼────────┐     ┌────────────┐                          │
│  │COMEN01C     │     │COADM01C    │                          │
│  │(User Menu)  │     │(Admin Menu)│                          │
│  └────┬────────┘     └────┬───────┘                          │
│       │                    │                                  │
│  ┌────▼────┐          ┌───▼────┐                             │
│  │Account  │          │Security│                             │
│  │COACTVWC │◄─VSAM──►│COUSR00C│◄─VSAM──► USRSEC            │
│  │COACTUPC │  reads   │COUSR01C│  CRUD                      │
│  │         │  ACCTDAT │COUSR02C│                             │
│  │         │  CUSTDAT │COUSR03C│                             │
│  │         │  CARDXREF│        │                             │
│  └─────────┘          └────────┘                             │
│       │                                                      │
│  ┌────▼────┐     ┌─────────┐     ┌─────────┐               │
│  │Card     │     │Transact │     │Reports  │               │
│  │COCRDLIC │     │COTRN00C │     │CORPT00C │──WRITEQ TD──► │
│  │COCRDSLC │     │COTRN01C │     │(submits │  batch JCL    │
│  │COCRDUPC │     │COTRN02C │     │ batch)  │               │
│  │         │     │         │     └─────────┘               │
│  │◄─VSAM──►│     │◄─VSAM──►│                               │
│  │ CARDDAT │     │TRANSACT │     ┌─────────┐               │
│  │ CARDAIX │     │ACCTDAT  │     │BillPay  │               │
│  │ ACCTDAT │     │CARDXREF │     │COBIL00C │               │
│  │ CUSTDAT │     │         │     │◄─VSAM──►│               │
│  │ CARDXREF│     │         │     │ ACCTDAT │               │
│  └─────────┘     └─────────┘     │ TRANSACT│               │
│                                   └─────────┘               │
│                                                              │
│  ┌──────────────────────────────────┐                        │
│  │ Authorization Sub-App            │                        │
│  │ COPAUA0C ◄─MQ──► Auth Queues    │                        │
│  │          ◄─VSAM─► CARDXREF,     │                        │
│  │                    ACCTDAT,      │                        │
│  │                    CUSTDAT       │                        │
│  │ COPAUS0C/1C ◄─IMS─► PAUTHDB    │                        │
│  │ COPAUS2C ◄─Db2──► PAUTH_FRAUD  │                        │
│  └──────────────────────────────────┘                        │
│                                                              │
│  ┌──────────────────────────────────┐                        │
│  │ Tran Type Sub-App (Db2)          │                        │
│  │ COTRTLIC ◄─Db2──► TRANSACTION_  │                        │
│  │ COTRTUPC         TYPE, TRAN_    │                        │
│  │                   CATEGORY      │                        │
│  └──────────────────────────────────┘                        │
└──────────────────────────────────────────────────────────────┘
```

---

## 5. Copybook Dependency Matrix

| Copybook | Programs Using It (count) |
|----------|--------------------------|
| COCOM01Y | 17 (all CICS programs) |
| COTTL01Y | 15 |
| CSDAT01Y | 15 |
| CSMSG01Y | 15 |
| DFHAID | 14 |
| DFHBMSCA | 14 |
| CVACT01Y | 13 |
| CVACT03Y | 12 |
| CSUSR01Y | 10 |
| CVCUS01Y | 9 |
| CVTRA05Y | 8 |
| CVACT02Y | 7 |
| CVCRD01Y | 5 |
| CSMSG02Y | 5 |
| CSSTRPFY | 5 |
| CIPAUDTY | 8 (sub-app) |
| CIPAUSMY | 6 (sub-app) |
