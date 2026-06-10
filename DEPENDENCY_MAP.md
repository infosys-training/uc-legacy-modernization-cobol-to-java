# Dependency Map

## Overview

This document maps inter-program dependencies (CALL/XCTL/LINK), dataset lineage through JCL jobs, and the end-to-end batch pipeline flow for the CardDemo application.

---

## 1. Online Program Call Graph (CICS XCTL/LINK Chains)

```
COSGN00C (Sign-On — Entry Point)
│
├── [Admin Path] ──XCTL──► COADM01C (Admin Menu)
│   │
│   ├── XCTL ──► COUSR00C (User List)
│   │             ├── XCTL ──► COUSR02C (User Update)
│   │             └── XCTL ──► COUSR03C (User Delete)
│   │
│   ├── XCTL ──► COUSR01C (User Add)
│   │
│   ├── XCTL ──► COTRTLIC (Transaction Type List — DB2)
│   │
│   └── XCTL ──► COTRTUPC (Transaction Type Maintenance — DB2)
│
└── [User Path] ──XCTL──► COMEN01C (Main Menu — Central Hub, 11 targets)
    │
    ├── XCTL ──► COACTVWC (Account View)
    │
    ├── XCTL ──► COACTUPC (Account Update)
    │
    ├── XCTL ──► COCRDLIC (Credit Card List)
    │             ├── XCTL ──► COCRDSLC (Card Detail View)
    │             └── XCTL ──► COCRDUPC (Card Update)
    │
    ├── XCTL ──► COTRN00C (Transaction List)
    │             └── XCTL ──► COTRN01C (Transaction View)
    │
    ├── XCTL ──► COTRN02C (Transaction Add)
    │
    ├── XCTL ──► CORPT00C (Report Request)
    │             └── WRITEQ TD ──► JCL (INTRDRJ1/J2 submitted via internal reader)
    │
    ├── XCTL ──► COBIL00C (Bill Payment)
    │
    └── XCTL ──► COPAUS0C (Pending Auth Summary — IMS)
                  └── LINK ──► COPAUS1C (Auth Detail)
                                └── LINK ──► COPAUS2C (Fraud Mark — DB2)
```

### Authorization Decision Engine (Standalone)

```
MQ Queue (Incoming Auth Requests)
    │
    └── MQGET ──► COPAUA0C (Authorization Decision)
                   ├── IMS GU/SCHD ──► Pending Auth DB (lookup/insert)
                   ├── DB2 INSERT ──► Fraud logging table
                   └── MQPUT1 ──► MQ Queue (Auth Reply)
```

---

## 2. Batch Program Call Graph

```
CBACT01C ──CALL──► COBDATFT (Assembler date formatter)
           ──CALL──► CEE3ABD (LE abnormal termination)

CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B (I/O sub-module, called 9× for different file operations)

COBSWAIT ──CALL──► MVSWAIT (Assembler wait routine)

CSUTLDTC ──CALL──► CEEDAYS (LE intrinsic date conversion)

CORPT00C ──CALL──► CSUTLDTC (Date validation utility)
COTRN02C ──CALL──► CSUTLDTC (Date validation utility)

DBUNLDGS ──CALL──► CBLTDLI (IMS DL/I interface: GN, GNP, ISRT)
PAUDBLOD ──CALL──► CBLTDLI (IMS DL/I interface: ISRT, GU)
PAUDBUNL ──CALL──► CBLTDLI (IMS DL/I interface: GN, GNP)
CBPAUP0C ──EXEC DLI──► GN, GNP, DLET, CHKP (IMS batch)
```

---

## 3. Dataset Lineage — JCL Job to File Mapping

### VSAM Dataset Definitions (Which JCL Jobs Create/Load Which Files)

| Dataset (VSAM KSDS) | Definition JCL | Load Source | Programs That Read | Programs That Write |
|---------------------|---------------|-------------|-------------------|-------------------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | ACCTFILE.jcl | ACCTDATA (PS) | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBEXPORT, COACTUPC, COACTVWC, COBIL00C, COTRN02C | CBACT04C (REWRITE), CBTRN02C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE), CBIMPORT |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CARDFILE.jcl | CARDDATA (PS) | CBACT02C, CBTRN01C, CBEXPORT, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | COCRDUPC (REWRITE), CBIMPORT |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CUSTFILE.jcl | CUSTDATA (PS) | CBCUS01C, CBEXPORT, CBSTM03A/B, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC | CBIMPORT |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | XREFFILE.jcl | XREFDATA (PS) | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, CBSTM03A/B, COACTUPC, COACTVWC, COTRN02C | CBIMPORT |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | TRANFILE.jcl | TRANSACT (PS) | CBTRN01C, CBTRN03C, CBEXPORT, CBSTM03A/B, COTRN00C, COTRN01C | CBTRN02C, COTRN02C (WRITE), CBIMPORT |
| AWS.M2.CARDDEMO.TCATBAL.VSAM.KSDS | TCATBALF.jcl | TCATBAL (PS) | CBACT04C, CBTRN02C | CBTRN02C (WRITE/REWRITE), CBACT04C (READ) |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | DISCGRP.jcl | DISCGRP (PS) | CBACT04C | — (reference data) |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | TRANTYPE.jcl | TRANTYPE (PS) | CBTRN03C, COTRTLIC, COTRTUPC | COTRTUPC (DB2 equivalent), COBTUPDT |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | TRANCATG.jcl | TRANCATG (PS) | CBTRN03C | — (reference data) |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | DUSRSECJ.jcl | Inline data | COSGN00C, COUSR00C, COUSR02C, COUSR03C | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) |
| AWS.M2.CARDDEMO.DALYREJS.VSAM | DALYREJS.jcl | — | — | CBTRN02C (rejected transactions) |

### Sequential/GDG Datasets

| Dataset | Created By | Read By | Purpose |
|---------|-----------|---------|---------|
| AWS.M2.CARDDEMO.DALYTRAN | External feed | CBTRN01C, CBTRN02C | Incoming daily transactions |
| AWS.M2.CARDDEMO.STATEMNT.PS | CBSTM03A | TXT2PDF1 | Generated statement text |
| AWS.M2.CARDDEMO.STATEMNT.HTML | CBSTM03A | — | Generated statement HTML |
| AWS.M2.CARDDEMO.REPTFILE | CBTRN03C | — | Transaction report output |
| AWS.M2.CARDDEMO.EXPORT.PS | CBEXPORT | CBIMPORT | Consolidated export file |
| AWS.M2.CARDDEMO.TRANTYPE.BKUP(+n) | DEFGDGD.jcl | DEFGDGD.jcl | GDG backup of transaction types |
| AWS.M2.CARDDEMO.TRXFL.SEQ | CREASTMT (SORT) | CREASTMT (IDCAMS) | Sorted transaction temp file |

---

## 4. End-to-End Batch Pipeline Flow

### Daily Processing Cycle

```
┌─────────────────────────────────────────────────────────────────────────┐
│                         DAILY BATCH PIPELINE                            │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  ┌──────────────┐    ┌──────────────────┐    ┌─────────────────────┐   │
│  │ DALYTRAN     │    │ POSTTRAN.jcl     │    │ Outputs:            │   │
│  │ (Daily Feed) │───►│ PGM=CBTRN02C     │───►│ • TRANSACT (posted) │   │
│  └──────────────┘    │                  │    │ • DALYREJS (rejects)│   │
│                      │ Also reads:      │    │ • ACCOUNT (updated) │   │
│                      │ • XREFFILE       │    │ • TCATBAL (updated) │   │
│                      │ • ACCTFILE       │    └─────────────────────┘   │
│                      │ • TCATBALF       │              │               │
│                      └──────────────────┘              ▼               │
│                                                                         │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ INTCALC.jcl — PGM=CBACT04C                                      │  │
│  │ Reads: TCATBAL, XREF, DISCGRP, ACCOUNT                          │  │
│  │ Writes: ACCOUNT (interest applied), TRANSACT (interest entries)  │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                              │                                          │
│                              ▼                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ CREASTMT.JCL — SORT + PGM=CBSTM03A (calls CBSTM03B)             │  │
│  │ Reads: TRANSACT (sorted), XREF, CUSTOMER, ACCOUNT               │  │
│  │ Writes: STATEMNT.PS (text), STATEMNT.HTML                        │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                              │                                          │
│                              ▼                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ TRANREPT.jcl — SORT + PGM=CBTRN03C                              │  │
│  │ Reads: TRANSACT (sorted), CARDXREF, TRANTYPE, TRANCATG          │  │
│  │ Writes: REPTFILE (daily transaction report)                      │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                              │                                          │
│                              ▼                                          │
│  ┌──────────────────────────────────────────────────────────────────┐  │
│  │ TXT2PDF1.JCL — Convert STATEMNT.PS to PDF                        │  │
│  └──────────────────────────────────────────────────────────────────┘  │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### Data Migration Pipeline

```
┌──────────────────────────────────────────────────────────────────┐
│              BRANCH DATA MIGRATION PIPELINE                       │
├──────────────────────────────────────────────────────────────────┤
│                                                                    │
│  EXPORT (Source Branch):                                           │
│  ┌─────────────┐     ┌──────────────────┐     ┌──────────────┐  │
│  │ CUSTFILE    │     │ CBEXPORT.jcl     │     │ EXPORT.PS    │  │
│  │ ACCTFILE    │────►│ PGM=CBEXPORT     │────►│ (consolidated│  │
│  │ XREFFILE    │     │                  │     │  stream)     │  │
│  │ TRANSACT    │     └──────────────────┘     └──────────────┘  │
│  │ CARDFILE    │                                      │          │
│  └─────────────┘                                      ▼          │
│                                                                    │
│  IMPORT (Target Branch):                                           │
│  ┌──────────────┐     ┌──────────────────┐     ┌─────────────┐  │
│  │ EXPORT.PS    │────►│ CBIMPORT.jcl     │────►│ CUSTOUT     │  │
│  │              │     │ PGM=CBIMPORT     │     │ ACCTOUT     │  │
│  │              │     │                  │     │ XREFOUT     │  │
│  │              │     │ Also writes:     │     │ TRNXOUT     │  │
│  │              │     │ • ERROUT (errors)│     │ CARDOUT     │  │
│  └──────────────┘     └──────────────────┘     └─────────────┘  │
│                                                                    │
└──────────────────────────────────────────────────────────────────┘
```

### IMS Batch Pipeline (Authorization Sub-System)

```
┌──────────────────────────────────────────────────────────────────┐
│              IMS AUTHORIZATION DATABASE MAINTENANCE                │
├──────────────────────────────────────────────────────────────────┤
│                                                                    │
│  Load:     PAUDBLOD.CBL ──ISRT/GU──► IMS Pending Auth DB         │
│                                                                    │
│  Unload:   PAUDBUNL.CBL ──GN/GNP──► Sequential flat file         │
│            DBUNLDGS.CBL  ──GN/GNP/ISRT──► GSAM output file       │
│                                                                    │
│  Purge:    CBPAUP0C.cbl ──GN/GNP/DLET──► Remove expired auths   │
│                                                                    │
│  Online:   COPAUA0C ──MQ GET──► Process request                   │
│                      ──IMS GU──► Lookup/Insert auth               │
│                      ──DB2 INSERT──► Log fraud                     │
│                      ──MQ PUT──► Send reply                        │
│                                                                    │
└──────────────────────────────────────────────────────────────────┘
```

---

## 5. Copybook Dependency Matrix

### Most Referenced Copybooks (by count of including programs)

| Copybook | Programs Using It | Purpose |
|----------|------------------|---------|
| COCOM01Y | 20 | CICS communication area (all online programs) |
| DFHAID | 16 | CICS attention identifier definitions |
| DFHBMSCA | 16 | BMS attribute constants |
| COTTL01Y | 16 | Screen title constants |
| CSDAT01Y | 16 | Date-time working storage |
| CSMSG01Y | 16 | Common messages |
| CSUSR01Y | 10 | Security user record |
| CVACT01Y | 10 | Account record layout |
| CVACT03Y | 10 | Card-XREF record layout |
| CVTRA05Y | 8 | Transaction record layout |
| CVCUS01Y | 7 | Customer record layout |
| CVACT02Y | 6 | Card record layout |
| CIPAUDTY | 7 | Pending auth detail (IMS sub-app) |
| CSMSG02Y | 5 | Abend handling data |
| CIPAUSMY | 5 | Pending auth summary (IMS sub-app) |

---

## 6. External Dependency Summary

| External Program | Type | Called By | Purpose |
|-----------------|------|-----------|---------|
| COBDATFT | Assembler | CBACT01C | Date formatting |
| CEE3ABD | LE Runtime | CBACT01–04C, CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01–03C | Abnormal termination handler |
| CEEDAYS | LE Runtime | CSUTLDTC | Date to Lilian day number conversion |
| MVSWAIT | Assembler | COBSWAIT | Timed wait/sleep |
| CBLTDLI | IMS Runtime | DBUNLDGS, PAUDBLOD, PAUDBUNL | IMS DL/I call interface |
| DFHCSDUP | CICS Utility | CBADMCDJ.jcl | CSD batch update utility |
| IDCAMS | VSAM Utility | 20+ JCL jobs | VSAM cluster management |
| IEBGENER | MVS Utility | 5 JCL jobs | Sequential dataset copy |
| SORT | DFSORT | COMBTRAN, CREASTMT, PRTCATBL, TRANREPT | Data sorting |
| FTP | TCP/IP | FTPJCL | File transfer |
| IKJEFT1B | TSO | TXT2PDF1 | Execute REXX (TXT2PDF) |
| SDSF | z/OS | CLOSEFIL, OPENFIL, CARDFILE, CUSTFILE, TRANFILE | Operator console commands |
