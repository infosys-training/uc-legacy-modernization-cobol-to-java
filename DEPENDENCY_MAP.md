# Dependency Map — CardDemo COBOL Estate

## Overview

This document maps program-to-program calls, shared copybook dependencies, dataset lineage through JCL jobs, and end-to-end batch pipeline flow.

---

## 1. Program Call Graph

### Direct CALL Dependencies

```
CBACT01C ──CALL──→ COBDATFT (Assembler date formatter)
         ──CALL──→ CEE3ABD  (LE Abend handler)

CBACT02C ──CALL──→ CEE3ABD

CBACT03C ──CALL──→ CEE3ABD

CBACT04C ──CALL──→ CEE3ABD

CBCUS01C ──CALL──→ CEE3ABD

CBEXPORT ──CALL──→ CEE3ABD

CBIMPORT ──CALL──→ CEE3ABD

CBTRN01C ──CALL──→ CEE3ABD

CBTRN02C ──CALL──→ CEE3ABD

CBTRN03C ──CALL──→ CEE3ABD

CBSTM03A ──CALL──→ CBSTM03B (Subroutine: file I/O for statements)
         ──CALL──→ CEE3ABD

COBSWAIT ──CALL──→ MVSWAIT  (System wait utility)

CSUTLDTC ──CALL──→ CEEDAYS  (LE Date conversion)

CORPT00C ──CALL──→ CSUTLDTC (Date utility)

COTRN02C ──CALL──→ CSUTLDTC (Date utility)

COPAUA0C ──CALL──→ MQOPEN   (MQ Series API)
         ──CALL──→ MQGET
         ──CALL──→ MQPUT1
         ──CALL──→ MQCLOSE

COACCT01 ──CALL──→ MQOPEN
         ──CALL──→ MQGET
         ──CALL──→ MQPUT
         ──CALL──→ MQCLOSE

CODATE01 ──CALL──→ MQOPEN
         ──CALL──→ MQGET
         ──CALL──→ MQPUT
         ──CALL──→ MQCLOSE

DBUNLDGS ──CALL──→ CBLTDLI  (IMS DL/I interface)

PAUDBLOD ──CALL──→ CBLTDLI

PAUDBUNL ──CALL──→ CBLTDLI

CBPAUP0C ──(IMS BMP)── DL/I calls via DFSRRC00
```

### CICS XCTL/LINK (Transfer of Control) Graph

```
COSGN00C (Sign-on)
    ├──XCTL──→ COMEN01C (Regular user menu)
    └──XCTL──→ COADM01C (Admin menu)

COMEN01C (Main Menu)
    ├──XCTL──→ COACTVWC (Account View)
    ├──XCTL──→ COACTUPC (Account Update)
    ├──XCTL──→ COBIL00C (Bill Payment)
    ├──XCTL──→ COTRN00C (Transaction List)
    ├──XCTL──→ COTRN01C (Transaction View)
    ├──XCTL──→ COTRN02C (Transaction Add)
    ├──XCTL──→ COCRDLIC (Card List)
    ├──XCTL──→ COCRDSLC (Card Detail)
    ├──XCTL──→ COCRDUPC (Card Update)
    └──XCTL──→ CORPT00C (Reports)

COADM01C (Admin Menu)
    ├──XCTL──→ COUSR00C (User List)
    ├──XCTL──→ COUSR01C (User Add)
    ├──XCTL──→ COUSR02C (User Update)
    ├──XCTL──→ COUSR03C (User Delete)
    ├──XCTL──→ COTRTLIC (Transaction Type List) [DB2]
    └──XCTL──→ COTRTUPC (Transaction Type Update) [DB2]

COCRDLIC (Card List)
    ├──XCTL──→ COCRDSLC (Card Detail)
    └──XCTL──→ COCRDUPC (Card Update)

COPAUS0C (Auth Summary)
    └──LINK──→ COPAUS1C (Auth Detail)
```

---

## 2. Shared Copybook Dependency Matrix

Programs sharing the same copybooks form implicit dependency clusters:

### Core Data Copybooks

| Copybook | Used By (Programs) |
|----------|-------------------|
| CVACT01Y (Account) | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C, COACCT01 |
| CVACT02Y (Card) | CBACT02C, CBEXPORT, CBIMPORT, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C, COTRTLIC |
| CVACT03Y (Xref) | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C |
| CVCUS01Y (Customer) | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| CVTRA05Y (Transaction) | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, COTRN00C, COTRN01C, COTRN02C, CORPT00C |
| CVTRA06Y (Daily Trans) | CBTRN01C, CBTRN02C |
| COCOM01Y (COMMAREA) | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC |

### UI/Infrastructure Copybooks

| Copybook | Used By |
|----------|---------|
| DFHAID | All online CICS programs (16 programs) |
| DFHBMSCA | All online CICS programs (16 programs) |
| COTTL01Y | All online CICS programs |
| CSDAT01Y | All online CICS programs |
| CSMSG01Y | All online CICS programs |
| CSUSR01Y | Most online CICS programs (security context) |
| CVCRD01Y | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC |

---

## 3. Dataset Lineage — JCL to Program Mapping

### Account Data Pipeline

```
[ACCTFILE.jcl]           [READACCT.jcl]           [INTCALC.jcl]
  IDCAMS REPRO              CBACT01C                 CBACT04C
  ACCTDATA.PS ──load──→ ACCTDATA.VSAM.KSDS ──read──→ (interest calc)
                              │                        │
                              │──read──→ CBEXPORT      │──rewrite──→ ACCTDATA.VSAM.KSDS
                              │──read──→ CBTRN01C      │──write──→ SYSTRAN
                              │──read/write──→ CBTRN02C
                              │──read──→ CBSTM03A/B
                              └──read──→ COACTVWC, COACTUPC, COTRN02C (CICS)
```

### Card Data Pipeline

```
[CARDFILE.jcl]
  IDCAMS REPRO
  CARDDATA.PS ──load──→ CARDDATA.VSAM.KSDS
                              │──read──→ CBACT02C (READCARD.jcl)
                              │──read──→ CBEXPORT
                              │──read──→ CBTRN01C
                              └──read──→ COCRDLIC, COCRDSLC, COCRDUPC (CICS)
```

### Customer Data Pipeline

```
[CUSTFILE.jcl]
  IDCAMS REPRO
  CUSTDATA.PS ──load──→ CUSTDATA.VSAM.KSDS
                              │──read──→ CBCUS01C (READCUST.jcl)
                              │──read──→ CBEXPORT
                              │──read──→ CBSTM03A/B
                              └──read──→ COACTVWC, COACTUPC (CICS)
```

### Cross-Reference Pipeline

```
[XREFFILE.jcl]
  IDCAMS REPRO
  CARDXREF.PS ──load──→ CARDXREF.VSAM.KSDS
                              │──read──→ CBACT03C (READXREF.jcl)
                              │──read──→ CBACT04C (INTCALC.jcl)
                              │──read──→ CBTRN01C, CBTRN02C
                              │──read──→ CBTRN03C (TRANREPT.jcl)
                              │──read──→ CBEXPORT
                              │──read──→ CBSTM03A/B
                              └──read──→ COACTVWC, COPAUA0C (CICS)
```

### Transaction Pipeline

```
                                   [POSTTRAN.jcl]
DALYTRAN.PS ──────────────────────→ CBTRN02C
                                      │
                                      ├──write──→ TRANSACT.VSAM.KSDS
                                      ├──write──→ DALYREJS (rejected)
                                      ├──rewrite──→ ACCTDATA.VSAM.KSDS
                                      └──rewrite──→ TCATBALF.VSAM.KSDS
                                      
[TRANFILE.jcl]                    [COMBTRAN.jcl]
  IDCAMS ──define──→ TRANSACT.VSAM.KSDS ←──SORT/REPRO──── TRANSACT.BKUP + SYSTRAN
                          │
                          │──read──→ CBTRN03C ──write──→ TRANREPT (TRANREPT.jcl)
                          │──read──→ CBSTM03A ──write──→ STATEMNT.PS + STATEMNT.HTML (CREASTMT.JCL)
                          │──read──→ CBEXPORT ──write──→ EXPORT.DATA (CBEXPORT.jcl)
                          └──read/write──→ COTRN00C, COTRN01C, COTRN02C, COBIL00C (CICS)

[TRANBKP.jcl]
  TRANSACT.VSAM.KSDS ──REPRO──→ TRANSACT.BKUP (then delete/redefine master)
```

### Transaction Reference Data

```
[TRANTYPE.jcl]
  TRANTYPE.PS ──load──→ TRANTYPE.VSAM.KSDS ──read──→ CBTRN03C

[TRANCATG.jcl]
  TRANCATG.PS ──load──→ TRANCATG.VSAM.KSDS ──read──→ CBTRN03C

[TCATBALF.jcl]
  TCATBALF.PS ──load──→ TCATBALF.VSAM.KSDS ──read/write──→ CBACT04C, CBTRN02C

[DISCGRP.jcl]
  DISCGRP.PS ──load──→ DISCGRP.VSAM.KSDS ──read──→ CBACT04C
```

### User Security Pipeline

```
[DUSRSECJ.jcl]
  USRSEC.PS ──load──→ USRSEC.VSAM.KSDS
                          │──read──→ COSGN00C (authentication)
                          │──read/write──→ COUSR00C, COUSR01C, COUSR02C, COUSR03C (CICS)
```

### Export/Import Pipeline

```
[CBEXPORT.jcl]
  All VSAM files ──read──→ CBEXPORT ──write──→ EXPORT.DATA

[CBIMPORT.jcl]
  EXPORT.DATA ──read──→ CBIMPORT ──write──→ CUSTDATA.IMPORT
                                           ACCTDATA.IMPORT
                                           CARDXREF.IMPORT
                                           TRANSACT.IMPORT
                                           IMPORT.ERRORS
```

### Authorization (IMS) Pipeline

```
[LOADPADB.JCL]
  PAUTDB.ROOT.FILEO ──→ PAUDBLOD ──ISRT──→ IMS PAUTHDB
  PAUTDB.CHILD.FILEO ──→

[UNLDPADB.JCL]
  IMS PAUTHDB ──→ PAUDBUNL ──write──→ PAUTDB.ROOT.FILEO
                                       PAUTDB.CHILD.FILEO

[UNLDGSAM.JCL]
  IMS PAUTHDB ──→ DBUNLDGS ──write──→ PAUTDB.ROOT.GSAM
                                       PAUTDB.CHILD.GSAM

[CBPAUP0J.jcl]
  IMS PAUTHDB ──→ CBPAUP0C (delete expired records)
```

---

## 4. End-to-End Batch Pipeline Flow

### Daily Processing Cycle

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        DAILY BATCH CYCLE                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  1. CLOSEFIL.jcl ─── Close CICS files for batch window                 │
│         │                                                               │
│  2. POSTTRAN.jcl ─── Post daily transactions                           │
│         │   DALYTRAN.PS → CBTRN02C → TRANSACT.VSAM.KSDS               │
│         │                          → ACCTDATA.VSAM.KSDS (balances)     │
│         │                          → TCATBALF.VSAM.KSDS (category bal) │
│         │                          → DALYREJS (rejects)                │
│         │                                                               │
│  3. INTCALC.jcl ─── Calculate interest                                 │
│         │   TCATBALF + XREF + ACCTDATA + DISCGRP → CBACT04C           │
│         │                          → ACCTDATA.VSAM.KSDS (interest)     │
│         │                          → SYSTRAN (interest transactions)    │
│         │                                                               │
│  4. COMBTRAN.jcl ─── Combine system + master transactions              │
│         │   SYSTRAN + TRANSACT.BKUP → SORT → TRANSACT.COMBINED        │
│         │                          → TRANSACT.VSAM.KSDS (REPRO back)   │
│         │                                                               │
│  5. TRANREPT.jcl ─── Generate transaction report                       │
│         │   TRANSACT → SORT → CBTRN03C → TRANREPT                     │
│         │                                                               │
│  6. CREASTMT.JCL ─── Create statements                                 │
│         │   TRANSACT → SORT → CBSTM03A → STATEMNT.PS + .HTML          │
│         │                                                               │
│  7. TRANBKP.jcl ─── Backup and purge transaction master                │
│         │   TRANSACT.VSAM.KSDS → REPRO → TRANSACT.BKUP               │
│         │                      → DELETE/REDEFINE (reset for next day)  │
│         │                                                               │
│  8. OPENFIL.jcl ─── Re-open CICS files after batch                    │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### One-Time / Periodic Jobs

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    SETUP / PERIODIC JOBS                                 │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  INITIAL SETUP:                                                         │
│    DEFGDGB.jcl ──→ Define GDG bases                                    │
│    ACCTFILE.jcl ──→ Define + Load Account VSAM                         │
│    CARDFILE.jcl ──→ Define + Load Card VSAM (with AIX)                 │
│    CUSTFILE.jcl ──→ Define + Load Customer VSAM                        │
│    XREFFILE.jcl ──→ Define + Load Xref VSAM (with AIX)                │
│    TRANFILE.jcl ──→ Define Transaction VSAM (with AIX)                 │
│    TCATBALF.jcl ──→ Define Transaction Cat Balance VSAM                │
│    TRANTYPE.jcl ──→ Define Transaction Type VSAM                       │
│    TRANCATG.jcl ──→ Define Transaction Category VSAM                   │
│    DISCGRP.jcl ──→ Define Disclosure Group VSAM                        │
│    DUSRSECJ.jcl ──→ Define User Security VSAM                          │
│    CREADB21.jcl ──→ Create DB2 tables + Load transaction types         │
│                                                                         │
│  BRANCH MIGRATION:                                                      │
│    CBEXPORT.jcl ──→ Export all data to flat file                       │
│    CBIMPORT.jcl ──→ Import flat file to VSAM                           │
│                                                                         │
│  IMS DATABASE MANAGEMENT:                                               │
│    LOADPADB.JCL ──→ Load IMS auth database                             │
│    UNLDPADB.JCL ──→ Unload IMS to flat files                           │
│    UNLDGSAM.JCL ──→ Unload IMS to GSAM                                 │
│    CBPAUP0J.jcl ──→ Purge expired auth messages                        │
│                                                                         │
│  DB2 MAINTENANCE:                                                       │
│    MNTTRDB2.jcl ──→ Batch update transaction types                     │
│    TRANEXTR.jcl ──→ Extract/backup transaction types                   │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 5. Inter-Program Dependency Counts

| Program | Depends On (calls/reads from) | Depended On By |
|---------|------------------------------|----------------|
| CBTRN02C | DALYTRAN, XREFFILE, ACCTFILE, TCATBALF | POSTTRAN.jcl, COMBTRAN.jcl (via TRANSACT) |
| CBACT04C | TCATBALF, XREFFILE, ACCTFILE, DISCGRP | INTCALC.jcl, COMBTRAN.jcl (via SYSTRAN) |
| CBSTM03A | CBSTM03B, Transaction data, XREF, CUST, ACCT | CREASTMT.JCL |
| CBSTM03B | TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | CBSTM03A (called as subroutine) |
| CBTRN03C | TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM | TRANREPT.jcl |
| CBEXPORT | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | CBEXPORT.jcl |
| CBIMPORT | EXPORT.DATA | CBIMPORT.jcl |
| COSGN00C | USRSEC file | All online programs (gateway) |
| COMEN01C | *(menu only)* | All regular-user online programs |
| COADM01C | *(menu only)* | All admin online programs |
| COPAUA0C | MQ queues, XREF, Account, Customer | MQ infrastructure |
| CSUTLDTC | CEEDAYS (LE) | CORPT00C, COTRN02C |
