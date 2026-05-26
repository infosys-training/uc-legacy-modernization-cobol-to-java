# Dependency Map — CardDemo COBOL Estate

## Overview

This document maps inter-program calls, dataset lineage, and end-to-end batch pipeline flows across the CardDemo application.

---

## 1. Inter-Program Call Graph

### 1.1 Batch Program Calls

```
CBSTM03A ──CALL──► CBSTM03B      (Statement generation → page formatting)
CBACT01C ──CALL──► COBDATFT       (Account read → date format conversion)
CORPT00C ──CALL──► CSUTLDTC       (Report selection → date validation)
COTRN02C ──CALL──► CSUTLDTC       (Transaction add → date validation)
COBSWAIT ──CALL──► MVSWAIT        (Wait utility → assembler timer routine)
```

**Abend handler calls (CEE3ABD):**
```
CBACT01C ──CALL──► CEE3ABD
CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD
CBSTM03A ──CALL──► CEE3ABD
```

### 1.2 CICS Online Program Transfers (XCTL)

All CICS programs communicate via COMMAREA (CARDDEMO-COMMAREA) and transfer control using `EXEC CICS XCTL`.

```
COSGN00C (Sign-on)
  ├──► COADM01C    (Admin login → Admin menu)
  └──► COMEN01C    (Regular login → Main menu)

COADM01C (Admin Menu)
  ├──► COUSR00C    (User List)
  ├──► COUSR01C    (User Add)
  ├──► COUSR02C    (User Update)
  ├──► COUSR03C    (User Delete)
  ├──► COTRTLIC    (Transaction Type List)
  ├──► COTRTUPC    (Transaction Type Update)
  └──► COSGN00C    (Back to sign-on)

COMEN01C (Main Menu) ──► routes to 12 options:
  ├──► COACTVWC    (1. Account View)
  ├──► COACTUPC    (2. Account Update)
  ├──► COCRDLIC    (3. Credit Card List)
  ├──► COCRDUPC    (4. Credit Card Update)
  ├──► COTRN00C    (5. Transaction List)
  ├──► COTRN01C    (6. Transaction View)
  ├──► COTRN02C    (7. Transaction Add)
  ├──► CORPT00C    (8. Reports)
  ├──► COBIL00C    (9. Bill Payment)
  └──► COPAUS0C    (10. Pending Auth View)

COCRDLIC (Card List)
  ├──► COCRDUPC    (Card Update — on selection)
  ├──► COCRDSLC    (Card View — on detail)
  └──► COMEN01C    (Back to menu)

COCRDSLC (Card View)
  └──► COMEN01C / COCRDLIC   (Back to menu/list)

COACTVWC (Account View)
  └──► COMEN01C    (Back to menu)

COACTUPC (Account Update)
  └──► COMEN01C    (Back to menu)

COCRDUPC (Card Update)
  └──► COMEN01C    (Back to menu)

COBIL00C (Bill Payment)
  └──► COMEN01C    (Back to menu)

COPAUS0C (Pending Auth Summary)
  └──► COPAUS1C    (Auth Detail)

COPAUS1C (Auth Detail)
  └──► COPAUS0C    (Back to summary)
```

### 1.3 Authorization Sub-Application Calls

```
COPAUA0C (MQ Async Processor)
  ├── MQ: MQGET from request queue
  ├── VSAM: EXEC CICS READ ACCTDAT, CARDDAT, CARDAIX
  └── MQ: MQPUT1 to response queue

CBPAUP0C (Batch Purge)
  └── IMS DL/I: GN, GNP, DLET on PAUTBDB

DBUNLDGS (GSAM Unload)
  └── IMS DL/I: GN, GNP → GSAM output

PAUDBLOD (DB Load)
  └── IMS DL/I: GU, ISRT ← sequential input

PAUDBUNL (DB Unload)
  └── IMS DL/I: GN, GNP → sequential output
```

---

## 2. Dataset Lineage — VSAM Files

### 2.1 Account File (ACCTDAT / ACCTFILE)

**Dataset:** `AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS`
**Record:** 300 bytes, Key = ACCT-ID (11 bytes, offset 0)

```
Creators / Loaders:
  ACCTFILE.jcl ──REPRO──► ACCTDAT (from AWS.M2.CARDDEMO.ACCTDATA.PS)
  CBIMPORT     ──WRITE──► ACCTDAT

Readers:
  CBACT01C ── sequential read
  CBACT04C ── random read/update (interest calculation)
  CBTRN01C ── random read/update (post transactions)
  CBTRN02C ── random read/update (validate transactions)
  CBEXPORT ── sequential read
  CBSTM03A ── sequential read (statement generation)

Online Access (CICS):
  COACTUPC ── READ/UPDATE (account update)
  COACTVWC ── READ (account view)
  COCRDLIC ── READ (card list — account lookup)
  COCRDSLC ── READ (card view — account lookup)
  COCRDUPC ── READ (card update — account lookup)
  COBIL00C ── READ/UPDATE (bill payment)
  COTRN02C ── READ (transaction add — account lookup)
  COPAUA0C ── READ (authorization — account verification)
  COACCT01 ── READ (MQ account inquiry)
```

### 2.2 Card File (CARDDAT / CARDFILE)

**Dataset:** `AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS`
**Record:** 150 bytes, Key = CARD-NUM (16 bytes, offset 0)
**AIX:** `AWS.M2.CARDDEMO.CARDDATA.VSAM.AIX` (on ACCT-ID, offset 16, 11 bytes)

```
Creators / Loaders:
  CARDFILE.jcl ──REPRO──► CARDDAT (from AWS.M2.CARDDEMO.CARDDATA.PS)
  CBIMPORT     ──WRITE──► CARDDAT

Readers:
  CBACT02C ── sequential read
  CBEXPORT ── sequential read
  CBTRN01C ── random read (card lookup for posting)
  CBTRN02C ── random read (card validation)

Online Access (CICS):
  COCRDLIC ── BROWSE/READ (card list, via AIX for account lookup)
  COCRDSLC ── READ (card detail)
  COCRDUPC ── READ/UPDATE (card update)
  COTRN02C ── READ (transaction add — card lookup)
  COPAUA0C ── READ (authorization — card verification)
```

### 2.3 Customer File (CUSTFILE)

**Dataset:** `AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS`
**Record:** 500 bytes, Key = CUST-ID (9 bytes, offset 0)

```
Creators / Loaders:
  CUSTFILE.jcl ──REPRO──► CUSTFILE (from AWS.M2.CARDDEMO.CUSTDATA.PS)
  CBIMPORT     ──WRITE──► CUSTFILE

Readers:
  CBCUS01C ── sequential read
  CBEXPORT ── sequential read
  CBTRN01C ── random read (customer lookup)
  CBSTM03A ── sequential read (statement generation)
```

### 2.4 Card Cross-Reference File (XREFFILE / CARDXREF)

**Dataset:** `AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS`
**Record:** 50 bytes, Key = XREF-CARD-NUM (16 bytes, offset 0)

```
Creators / Loaders:
  XREFFILE.jcl ──REPRO──► XREFFILE (from AWS.M2.CARDDEMO.CARDXREF.PS)
  CBIMPORT     ──WRITE──► XREFFILE

Readers:
  CBACT03C ── sequential read
  CBACT04C ── sequential read (interest calc — iterate cards)
  CBEXPORT ── sequential read
  CBTRN01C ── random read (card→account lookup)
  CBTRN02C ── random read (card→account lookup)
  CBTRN03C ── sequential read (report generation)
  CBSTM03A ── sequential read (statement generation)
```

### 2.5 Transaction File (TRANSACT / TRANFILE)

**Dataset:** `AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS`
**Record:** 350 bytes, Key = TRAN-ID (16 bytes, offset 0)

```
Creators / Loaders:
  TRANFILE.jcl ──REPRO──► TRANFILE (from AWS.M2.CARDDEMO.TRANSACT.PS)
  CBTRN01C     ──WRITE──► TRANFILE (post transactions)
  CBTRN02C     ──WRITE──► TRANFILE (validated transactions)
  CBACT04C     ──WRITE──► TRANFILE (interest transactions)
  CBIMPORT     ──WRITE──► TRANFILE

Readers:
  CBACT04C ── sequential read (interest calculation)
  CBTRN02C ── read for validation
  CBTRN03C ── sequential read (report generation)
  CBEXPORT ── sequential read

Online Access (CICS):
  COTRN00C ── BROWSE (transaction list)
  COTRN01C ── READ (transaction detail)
  COTRN02C ── WRITE (new transaction)
  COBIL00C ── WRITE (bill payment transactions)

Backup:
  TRANBKP.jcl ──REPRO──► Sequential backup (GDG)
```

### 2.6 Daily Transaction Input (DALYTRAN)

**Dataset:** `AWS.M2.CARDDEMO.DALYTRAN.PS` (sequential)
**Record:** 350 bytes

```
Producer: External feed (card network POS transactions)

Consumers:
  CBTRN01C ── sequential read (post to TRANFILE)
  CBTRN02C ── sequential read (validate, rejects → DALYREJS)
```

### 2.7 User Security File (USRSEC)

**Dataset:** `AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS`
**Record:** 80 bytes, Key = SEC-USR-ID (8 bytes, offset 0)

```
Creator:
  DUSRSECJ.jcl ──REPRO──► USRSEC

Online Access (CICS):
  COSGN00C ── READ (authenticate)
  COUSR00C ── BROWSE (user list)
  COUSR01C ── WRITE (add user)
  COUSR02C ── READ/UPDATE (update user)
  COUSR03C ── READ/DELETE (delete user)
```

### 2.8 Reference / Configuration Files

| Dataset | VSAM Type | JCL Loader | Programs |
|---------|-----------|-----------|----------|
| Transaction Category Balance (`TCATBALF`) | KSDS (50 bytes) | TCATBALF.jcl | CBACT04C (read/update) |
| Transaction Type (`TRANTYPE`) | KSDS (60 bytes) | TRANTYPE.jcl | CBTRN03C (read) |
| Transaction Category (`TRANCATG`) | KSDS (60 bytes) | TRANCATG.jcl | CBTRN03C (read) |
| Disclosure Group (`DISCGRP`) | KSDS (50 bytes) | DISCGRP.jcl | CBACT04C (read — interest rates) |
| Report Output (`REPTFILE`) | ESDS | REPTFILE.jcl | CBTRN03C (write) |
| Daily Rejects (`DALYREJS`) | ESDS | DALYREJS.jcl | CBTRN02C (write) |
| Statement Output (`STMTFILE`) | Sequential | — | CBSTM03A (write), CBSTM03B (write) |

---

## 3. DB2 Table Access (Transaction Type Sub-App)

```
DB2 Tables:
  CARDDEMO.TRANSACTION_TYPE  (TR_TYPE PK)
  CARDDEMO.TRANSACTION_CATEGORY  (TC_TYPE, TC_CATEGORY PK)

Writers:
  COBTUPDT ── INSERT/UPDATE (batch load from file)
  COTRTUPC ── INSERT/UPDATE/DELETE (online CRUD)

Readers:
  COTRTLIC ── SELECT with cursor (paginated list)
  COTRTUPC ── SELECT (detail view)
  COPAUS0C ── SELECT from AUTHFRDS view (authorization fraud analysis)
  COPAUS1C ── SELECT from AUTHFRDS view
```

---

## 4. IMS Database Access (Authorization Sub-App)

```
IMS Database: PAUTBDB (Pending Authorization)
  Segments:
    CIPAUSMY (Summary — root segment, key = PA-ACCT-ID)
    CIPAUDTY (Detail — child segment, key = PA-AUTH-DATE + PA-AUTH-TIME)

Access Programs:
  CBPAUP0C ── GN, GNP, DLET (purge expired)
  DBUNLDGS ── GN, GNP (full unload to GSAM)
  PAUDBLOD ── GU, ISRT (reload from sequential)
  PAUDBUNL ── GN, GNP (unload to sequential)
```

---

## 5. MQ Queue Access

```
Authorization Queue (COPAUA0C):
  MQGET ◄── Request Queue (authorization requests from card network)
  MQPUT1 ──► Response Queue (approval/decline responses)

Account Inquiry Queue (COACCT01):
  MQGET ◄── Inquiry Request Queue
  MQPUT ──► Inquiry Response Queue

Date Validation Queue (CODATE01):
  MQGET ◄── Date Validation Request Queue
  MQPUT ──► Date Validation Response Queue
```

---

## 6. End-to-End Batch Pipeline Flows

### 6.1 Daily Transaction Processing Pipeline

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  CLOSEFIL   │────►│   TRANBKP   │────►│  WAITSTEP   │────►┌─────────────┐
│ Close CICS  │     │ Backup VSAM │     │  Timer wait │     │  OPENFIL    │
│   files     │     │  to GDG     │     │             │     │ Reopen CICS │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘

Orchestrated by: Control-M DAILY-TransactionBackup folder (runs ALL days)
```

**Detailed data flow:**
```
External Card Network
       │
       ▼
  DALYTRAN.PS (daily transaction file)
       │
       ├──► CBTRN02C (COMBTRAN.jcl) ──► DALYREJS (rejects)
       │         │
       │         ▼
       │    TRANFILE (validated transactions)
       │
       └──► CBTRN01C (POSTTRAN.jcl) ──► TRANFILE (posted)
                 │                          │
                 ▼                          ▼
            ACCTFILE (balance updates)  TRANFILE (committed)
```

### 6.2 Monthly Interest Calculation Pipeline

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  CLOSEFIL   │────►│   INTCALC   │────►│  COMBTRAN   │────►│  WAITSTEP   │────►│  OPENFIL    │
│ Close CICS  │     │ CBACT04C    │     │ CBTRN02C    │     │  Timer wait │     │ Reopen CICS │
│   files     │     │ Calc interest│    │ Validate    │     │             │     │    files    │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘

Orchestrated by: Control-M MONTHLY-InterestCalculation folder
```

**Detailed data flow:**
```
XREFFILE ──►┐
DISCGRP  ──►├──► CBACT04C (INTCALC) ──► TRANFILE (interest transactions)
TRANFILE ──►│                      ──► ACCTFILE (updated balances)
ACCTFILE ──►┘                      ──► TCATBALF (category balance updates)
                    │
                    ▼
              CBTRN02C (COMBTRAN) ──► TRANFILE (validated)
                                 ──► DALYREJS (rejects)
```

### 6.3 Weekly Transaction Type Refresh Pipeline

```
┌─────────────┐
│  MNTTRDB2   │──────────────────────────────────┐
│ COBTUPDT    │                                  │
│ Update DB2  │                                  │
└─────────────┘                                  │
       │                                         │
       ├──► SMART_FOLDER: DisclosureGroupsRefresh│
       │    CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL
       │                                         │
       └──► SMART_FOLDER: TransactionTypesDBRefresh
            TRANEXTR (extract types from DB2)

Orchestrated by: Control-M WEEKLY-TransactionTypesDBRefresh folder (runs Saturdays)
```

### 6.4 Statement Generation Pipeline (Ad-hoc)

```
CUSTFILE  ──►┐
ACCTFILE  ──►├──► CBSTM03A ──CALL──► CBSTM03B ──► STMTFILE (statements)
XREFFILE  ──►┘

Triggered by: CREASTMT.JCL
```

### 6.5 Report Generation Pipeline (Ad-hoc)

```
TRANFILE  ──►┐
XREFFILE  ──►├──► CBTRN03C ──► REPTFILE (transaction reports)
TRANTYPE  ──►│
TRANCATG  ──►┘

Triggered by: TRANREPT.jcl (via CORPT00C online submission)
```

### 6.6 Data Export/Import Pipeline (Ad-hoc)

```
Export:
  CUSTFILE  ──►┐
  ACCTFILE  ──►│
  XREFFILE  ──►├──► CBEXPORT ──► EXPORT-FILE (sequential, 500-byte records)
  TRANFILE  ──►│
  CARDFILE  ──►┘

Import:
  EXPORT-FILE ──► CBIMPORT ──►┌─► CUSTFILE
                              ├─► ACCTFILE
                              ├─► XREFFILE
                              ├─► TRANFILE
                              └─► CARDFILE

Triggered by: CBEXPORT.jcl / CBIMPORT.jcl
```

### 6.7 IMS Authorization Maintenance Pipeline

```
Online (continuous):
  MQ Request Queue ──► COPAUA0C ──► MQ Response Queue
                           │
                           ▼
                       IMS PAUTBDB (via CICS DL/I)

Batch maintenance:
  CBPAUP0C (purge expired) ──► IMS PAUTBDB
  PAUDBUNL (unload) ──► Sequential file
  PAUDBLOD (reload) ◄── Sequential file
  DBUNLDGS (GSAM unload) ──► GSAM output

Triggered by: CBPAUP0J.jcl, UNLDPADB.JCL, LOADPADB.JCL, UNLDGSAM.JCL
```

---

## 7. CICS Transaction / Program Mapping

| Transaction ID | Program | Function |
|---------------|---------|----------|
| CDSG | COSGN00C | Sign-on |
| CADM | COADM01C | Admin menu |
| CMEN | COMEN01C | Main menu |
| CAAV | COACTVWC | Account View |
| CAAU | COACTUPC | Account Update |
| CACL | COCRDLIC | Card List |
| CACS | COCRDSLC | Card Select |
| CACU | COCRDUPC | Card Update |
| CATL | COTRN00C | Transaction List |
| CATV | COTRN01C | Transaction View |
| CATA | COTRN02C | Transaction Add |
| CARP | CORPT00C | Reports |
| CABP | COBIL00C | Bill Payment |
| CAUL | COUSR00C | User List |
| CAUA | COUSR01C | User Add |
| CAUU | COUSR02C | User Update |
| CAUD | COUSR03C | User Delete |
| CPAS | COPAUS0C | Pending Auth Summary |
| CPA1 | COPAUS1C | Pending Auth Detail |
| CPA2 | COPAUS2C | Pending Auth Schedule |
| CPAA | COPAUA0C | Auth Async Processor |
| CTTT | COTRTLIC | Transaction Type List |
| CTTU | COTRTUPC | Transaction Type Update |
| CAC1 | COACCT01 | Account MQ Inquiry |
| CDT1 | CODATE01 | Date MQ Validation |

---

## 8. Consolidated File-to-Program Matrix

| VSAM File | Batch Writers | Batch Readers | Online Writers | Online Readers |
|-----------|--------------|--------------|----------------|----------------|
| ACCTDAT | CBIMPORT, CBTRN01C, CBTRN02C, CBACT04C | CBACT01C, CBACT04C, CBEXPORT, CBSTM03A | COACTUPC, COBIL00C | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COACCT01 |
| CARDDAT | CBIMPORT | CBACT02C, CBEXPORT, CBTRN01C, CBTRN02C | COCRDUPC | COCRDLIC, COCRDSLC, COTRN02C, COPAUA0C |
| CUSTFILE | CBIMPORT | CBCUS01C, CBEXPORT, CBTRN01C, CBSTM03A | — | — |
| XREFFILE | CBIMPORT | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A | — | COCRDLIC (via AIX) |
| TRANFILE | CBIMPORT, CBTRN01C, CBTRN02C, CBACT04C | CBACT04C, CBTRN03C, CBEXPORT | COTRN02C, COBIL00C | COTRN00C, COTRN01C |
| USRSEC | (DUSRSECJ.jcl) | — | COUSR01C | COSGN00C, COUSR00C, COUSR02C, COUSR03C |
| DALYTRAN | (external feed) | CBTRN01C, CBTRN02C | — | — |
