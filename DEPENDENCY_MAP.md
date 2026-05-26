# DEPENDENCY MAP

## Overview

This document maps the inter-program call graph, dataset lineage through JCL jobs, and end-to-end batch pipeline flow for the CardDemo COBOL estate.

---

## 1. Program Call Graph

### 1.1 Online (CICS) Navigation Flow

```
COSGN00C (Signon)
├── XCTL → COMEN01C (Main Menu - Regular Users)
│   ├── XCTL → COACTVWC (Account View)
│   │   └── XCTL → COMEN01C (back)
│   ├── XCTL → COACTUPC (Account Update)
│   │   └── XCTL → COMEN01C (back)
│   ├── XCTL → COCRDLIC (Credit Card List)
│   │   ├── XCTL → COCRDSLC (Card Detail View)
│   │   │   └── XCTL → COCRDLIC (back)
│   │   └── XCTL → COCRDUPC (Card Update)
│   │       └── XCTL → COCRDLIC (back)
│   ├── XCTL → COTRN00C (Transaction List)
│   │   └── XCTL → COTRN01C (Transaction View)
│   │       └── XCTL → COTRN00C (back)
│   ├── XCTL → COTRN02C (Transaction Add)
│   │   └── XCTL → COMEN01C (back)
│   ├── XCTL → CORPT00C (Transaction Reports)
│   │   ├── CALL CSUTLDTC (Date utility)
│   │   └── XCTL → COMEN01C (back)
│   ├── XCTL → COBIL00C (Bill Payment)
│   │   └── XCTL → COMEN01C (back)
│   └── XCTL → COPAUS0C (Pending Authorization View)
│       ├── XCTL → COPAUS1C (Auth Detail View)
│       │   └── LINK → COPAUS2C (Mark Fraud - DB2)
│       └── XCTL → COMEN01C (back)
└── XCTL → COADM01C (Admin Menu)
    ├── XCTL → COUSR00C (User List)
    │   └── XCTL → COADM01C (back)
    ├── XCTL → COUSR01C (User Add)
    │   └── XCTL → COADM01C (back)
    ├── XCTL → COUSR02C (User Update)
    │   └── XCTL → COADM01C (back)
    ├── XCTL → COUSR03C (User Delete)
    │   └── XCTL → COADM01C (back)
    ├── XCTL → COTRTLIC (Transaction Type List - DB2)
    │   └── XCTL → COTRTUPC (Transaction Type Update - DB2)
    │       └── XCTL → COTRTLIC (back)
    └── XCTL → COADM01C (self - invalid option)
```

### 1.2 Batch Program CALL Dependencies

```
CBSTM03A (Statement Generation)
└── CALL CBSTM03B (File I/O subroutine) — called 11 times
    └── Handles: OPEN/READ/WRITE/REWRITE for ACCTFILE, CUSTFILE, TRNXFILE, XREFFILE

CBACT01C (Read Accounts)
└── CALL COBDATFT (Date formatting utility)

CORPT00C (Report Submission - Online)
└── CALL CSUTLDTC (Date utility) — 2 calls for date range validation

COTRN02C (Add Transaction - Online)
└── CALL CSUTLDTC (Date utility) — 2 calls for date validation

COBSWAIT (Wait Utility)
└── CALL MVSWAIT (System wait service)

CBACT01C, CBACT02C, CBACT03C, CBACT04C, CBCUS01C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, CBIMPORT
└── CALL CEE3ABD (LE Abend routine - error handling)
```

### 1.3 Authorization Sub-Application Dependencies

```
COPAUA0C (Authorization Decision - MQ Listener)
├── CALL MQOPEN (Open request queue)
├── CALL MQGET (Read authorization request)
├── CICS READ: ACCTDAT, CARDXREF, CUSTDAT (validate card/account)
├── CALL MQPUT1 (Send reply)
└── CALL MQCLOSE (Close queue)

COPAUS1C (Auth Detail View)
└── EXEC CICS LINK → COPAUS2C (Mark as fraud via DB2)

PAUDBLOD (IMS DB Load)
└── CALL CBLTDLI: ISRT, GU (IMS DL/I calls)

PAUDBUNL (IMS DB Unload)
└── CALL CBLTDLI: GN, GNP (IMS DL/I calls)

DBUNLDGS (IMS DB Unload to GSAM)
└── CALL CBLTDLI: GN, GNP, ISRT (IMS DL/I calls)
```

### 1.4 VSAM/MQ Sub-Application Dependencies

```
COACCT01 (Account Inquiry via MQ)
├── CICS READ: ACCTDAT
├── MQ GET (request)
└── MQ PUT (reply)

CODATE01 (Date Service via MQ)
├── MQ GET (request)
└── MQ PUT (reply)
```

---

## 2. Dataset Lineage — JCL Jobs

### 2.1 VSAM File Definitions (Setup Jobs)

| Dataset (DD Name) | VSAM Cluster DSN Pattern | Defined By | Type |
|-------------------|--------------------------|------------|------|
| ACCTFILE | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | ACCTFILE.jcl | KSDS |
| CARDFILE | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CARDFILE.jcl | KSDS + AIX |
| CUSTFILE | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CUSTFILE.jcl | KSDS |
| XREFFILE | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | XREFFILE.jcl | KSDS + AIX |
| TRANFILE/TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | TRANFILE.jcl | KSDS + AIX |
| DALYTRAN | AWS.M2.CARDDEMO.DALYTRAN | (input file) | Sequential/KSDS |
| DALYREJS | AWS.M2.CARDDEMO.DALYREJS | DALYREJS.jcl | Sequential |
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | TCATBALF.jcl | KSDS |
| DISCGRP | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | DISCGRP.jcl | KSDS |
| TRANTYPE | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | TRANTYPE.jcl | KSDS |
| TRANCATG | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | TRANCATG.jcl | KSDS |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | DUSRSECJ.jcl | KSDS |
| EXPFILE | AWS.M2.CARDDEMO.EXPORT.SEQ | CBEXPORT.jcl | Sequential |

### 2.2 Dataset Read/Write by Program

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                        DATASET FLOW MATRIX                                   │
├─────────────────┬────────────────────────────────────────────────────────────┤
│ Dataset         │ Writers (W)              │ Readers (R)                      │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ ACCTFILE        │ CBIMPORT(W)              │ CBACT01C(R), CBACT04C(R),        │
│                 │                          │ CBTRN01C(R), CBTRN02C(R),        │
│                 │                          │ CBEXPORT(R), CBSTM03B(R),        │
│                 │                          │ COACTVWC(R), COACTUPC(R/W),      │
│                 │                          │ COTRN02C(R), COBIL00C(R/W)       │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ CARDFILE        │ CBIMPORT(W)              │ CBACT02C(R), CBTRN01C(R),        │
│                 │                          │ CBEXPORT(R), COCRDLIC(R),        │
│                 │                          │ COCRDSLC(R), COCRDUPC(R/W),      │
│                 │                          │ COACTVWC(R)                      │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ CUSTFILE        │ CBIMPORT(W)              │ CBCUS01C(R), CBTRN01C(R),        │
│                 │                          │ CBEXPORT(R), CBSTM03B(R),        │
│                 │                          │ COCRDSLC(R), COCRDUPC(R),        │
│                 │                          │ COACTVWC(R), COACTUPC(R)         │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ XREFFILE        │ CBIMPORT(W)              │ CBACT03C(R), CBACT04C(R),        │
│                 │                          │ CBTRN01C(R), CBTRN02C(R),        │
│                 │                          │ CBTRN03C(R), CBEXPORT(R),        │
│                 │                          │ CBSTM03B(R), COACTVWC(R),        │
│                 │                          │ COACTUPC(R), COTRN02C(R),        │
│                 │                          │ COBIL00C(R), COPAUA0C(R)         │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ TRANSACT        │ CBTRN02C(W),             │ CBACT04C(R), CBTRN03C(R),        │
│                 │ COBIL00C(W),             │ CBEXPORT(R), COTRN00C(R),        │
│                 │ COTRN02C(W)              │ COTRN01C(R), CBSTM03B(R)        │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ DALYTRAN        │ (External input)         │ CBTRN01C(R), CBTRN02C(R)         │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ DALYREJS        │ CBTRN02C(W)              │ (Archive/review)                 │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ TCATBALF        │ CBACT04C(W),             │ (Reporting)                      │
│                 │ CBTRN02C(W)              │                                  │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ DISCGRP         │ (Setup/reference)        │ CBACT04C(R)                      │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ TRANTYPE        │ COBTUPDT(W-DB2)          │ CBTRN03C(R)                      │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ TRANCATG        │ (Setup/reference)        │ CBTRN03C(R)                      │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ USRSEC          │ COUSR01C(W),             │ COSGN00C(R), COUSR00C(R),        │
│                 │ COUSR02C(W),             │ COUSR02C(R), COUSR03C(R)         │
│                 │ COUSR03C(D)              │                                  │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ EXPFILE         │ CBEXPORT(W)              │ CBIMPORT(R)                      │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ HTMLFILE        │ CBSTM03A(W)              │ (Delivery/FTP)                   │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ STMTFILE        │ CBSTM03A(W)              │ (Delivery/Print)                 │
├─────────────────┼──────────────────────────┼──────────────────────────────────┤
│ TRANREPT        │ CBTRN03C(W)              │ (Print/PDF via TXT2PDF1)         │
└─────────────────┴──────────────────────────┴──────────────────────────────────┘
```

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Transaction Processing Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH CYCLE                                  │
└─────────────────────────────────────────────────────────────────────┘

Phase 1: File Preparation
─────────────────────────
  CLOSEFIL.jcl ──→ Close CICS files for batch access
  
Phase 2: Transaction Ingestion
──────────────────────────────
  External System ──→ DALYTRAN (Daily Transaction Input File)
  
  COMBTRAN.jcl ──→ SORT: Merge/sort daily transactions
                    IDCAMS: Load sorted transactions into VSAM

Phase 3: Transaction Posting
────────────────────────────
  POSTTRAN.jcl ──→ CBTRN02C:
                     • READ DALYTRAN (new transactions)
                     • READ XREFFILE (validate card → account mapping)
                     • READ ACCTFILE (validate account exists/active)
                     • WRITE TRANSACT (post valid transactions)
                     • WRITE DALYREJS (rejected transactions)
                     • WRITE TCATBALF (update category balances)

Phase 4: Interest Calculation
─────────────────────────────
  INTCALC.jcl ──→ CBACT04C (date parameter):
                     • READ XREFFILE, ACCTFILE
                     • READ DISCGRP (interest rates by group)
                     • READ TRANSACT (transaction history)
                     • WRITE TCATBALF (interest entries)
                     • WRITE TRANSACT (interest transactions)

Phase 5: Reporting
──────────────────
  TRANREPT.jcl ──→ SORT: Sort transactions by account/date
                    CBTRN03C: Generate transaction detail report
                     • READ TRANFILE, CARDXREF, TRANTYPE, TRANCATG
                     • WRITE TRANREPT (report file)
  
  TXT2PDF1.JCL ──→ Convert report to PDF

Phase 6: Statement Generation
─────────────────────────────
  CREASTMT.JCL ──→ SORT: Extract and sort transactions
                    IDCAMS: Reload sorted data
                    CBSTM03A → CBSTM03B:
                     • READ TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE
                     • WRITE STMTFILE (statement text)
                     • WRITE HTMLFILE (statement HTML)

Phase 7: Backup & Archive
──────────────────────────
  TRANBKP.jcl ──→ REPROC + IDCAMS: Backup transaction file to GDG

Phase 8: Reopen Files
─────────────────────
  OPENFIL.jcl ──→ Reopen CICS files for online access
```

### 3.2 Data Migration Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                    BRANCH MIGRATION FLOW                              │
└─────────────────────────────────────────────────────────────────────┘

Export (Source Branch):
  CBEXPORT.jcl ──→ CBEXPORT:
    • READ: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE
    • WRITE: EXPFILE (multi-record export with header/types)
    
  FTPJCL.JCL ──→ FTP: Transfer EXPFILE to target system

Import (Target Branch):
  CBIMPORT.jcl ──→ CBIMPORT:
    • READ: EXPFILE
    • WRITE: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT
    • WRITE: ERROUT (failed records)
```

### 3.3 Authorization (IMS) Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                    AUTHORIZATION DB MANAGEMENT                        │
└─────────────────────────────────────────────────────────────────────┘

Real-time (CICS):
  MQ Request ──→ COPAUA0C (Authorization Decision)
    • READ: CARDXREF, ACCTDAT, CUSTDAT
    • Approve/Decline based on limits
    • CICS WRITEQ: Store authorization record
    ──→ MQ Reply

Batch Maintenance:
  CBPAUP0J.jcl ──→ CBPAUP0C: Purge expired pending auths (IMS BMP)
  
  UNLDPADB.JCL ──→ PAUDBUNL: Unload IMS DB to flat files
  UNLDGSAM.JCL ──→ DBUNLDGS: Unload IMS DB to GSAM
  LOADPADB.JCL ──→ PAUDBLOD: Reload IMS DB from flat files
```

### 3.4 DB2 Transaction Type Maintenance

```
┌─────────────────────────────────────────────────────────────────────┐
│                    DB2 TABLE MANAGEMENT                               │
└─────────────────────────────────────────────────────────────────────┘

Setup:
  CREADB21.jcl ──→ Create DB2 table TRTYP (DDL)
  MNTTRDB2.jcl ──→ BIND DB2 plan for application programs

Batch Load:
  TRANEXTR.jcl ──→ IEBGENER: Extract TRANTYPE/TRANCATG to backup
  COBTUPDT (batch) ──→ READ INPFILE; EXEC SQL INSERT/UPDATE TRTYP

Online Maintenance:
  COTRTLIC ──→ COTRTUPC: List/Update/Delete transaction types via CICS screens
```

---

## 4. Shared Copybook Dependencies

```
                        COPYBOOK USAGE HEATMAP
                        
Copybook        │ Programs Using It
────────────────┼──────────────────────────────────────────────
COCOM01Y        │ 19 programs (all CICS online programs)
COTTL01Y        │ 17 programs (all screen-based programs)
CSDAT01Y        │ 17 programs (all needing date/time)
CSMSG01Y        │ 17 programs (all needing messages)
DFHAID          │ 16 programs (all CICS with keyboard input)
DFHBMSCA        │ 15 programs (all CICS with BMS maps)
CSUSR01Y        │ 12 programs (user security handling)
CVCRD01Y        │  7 programs (card navigation/work areas)
CVACT01Y        │ 12 programs (account record access)
CVACT03Y        │ 10 programs (card xref access)
CVTRA05Y        │ 10 programs (transaction record access)
CVCUS01Y        │  8 programs (customer record access)
CVACT02Y        │  7 programs (card record access)
CSMSG02Y        │  7 programs (abend handling)
CSSTRPFY        │  7 programs (PF key mapping)
CIPAUDTY        │  7 programs (authorization detail)
CIPAUSMY        │  5 programs (authorization summary)
```

---

## 5. Inter-System Integration Points

| Integration | Direction | Mechanism | Programs Involved |
|-------------|-----------|-----------|-------------------|
| Authorization Requests | Inbound | MQ (request queue) | COPAUA0C |
| Authorization Replies | Outbound | MQ (reply queue) | COPAUA0C |
| Account Inquiries | Inbound | MQ (request queue) | COACCT01 |
| Account Replies | Outbound | MQ (reply queue) | COACCT01 |
| Date Service | Inbound/Outbound | MQ | CODATE01 |
| Fraud Marking | DB2 | EXEC SQL UPDATE | COPAUS2C |
| Transaction Types | DB2 | EXEC SQL CRUD | COTRTLIC, COTRTUPC, COBTUPDT |
| IMS Auth Database | IMS DL/I | CBLTDLI calls | PAUDBLOD, PAUDBUNL, DBUNLDGS |
| File Transfer | FTP | FTPJCL.JCL | External systems |
| Batch Report Trigger | CICS TD Queue | WRITEQ TD | CORPT00C → batch jobs |
