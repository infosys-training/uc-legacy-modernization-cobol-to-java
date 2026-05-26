# DEPENDENCY_MAP.md — CardDemo Call Graphs & Dataset Lineage

## Overview

This document maps all inter-program dependencies (CALL, XCTL, LINK), dataset lineage through JCL jobs, and end-to-end batch pipeline flows for the CardDemo COBOL estate.

---

## 1. Inter-Program Call Graph

### 1.1 Batch Program Calls

```
CBSTM03A ──CALL──► CBSTM03B        (12 calls — statement file I/O delegation)
CBACT01C ──CALL──► COBDATFT         (1 call — date formatting; external LE routine)
CBACT04C ──(no external calls)──    (self-contained interest calculation)
CBTRN01C ──(no external calls)──    (self-contained transaction posting)
CBTRN02C ──(no external calls)──    (self-contained transaction posting v2)
CBTRN03C ──(no external calls)──    (self-contained report printing)
CBEXPORT ──(no external calls)──    (self-contained export)
CBIMPORT ──(no external calls)──    (self-contained import)
CSUTLDTC ──CALL──► CEEDAYS          (1 call — IBM LE date conversion)
COBSWAIT ──CALL──► MVSWAIT          (1 call — mainframe wait service)
```

### 1.2 CICS Online Program Calls (CALL)

```
CORPT00C ──CALL──► CSUTLDTC         (2 calls — date validation before report submit)
COTRN02C ──CALL──► CSUTLDTC         (2 calls — date validation for new transaction)
```

### 1.3 CICS Online Program Transfers (XCTL)

XCTL transfers control from one CICS program to another, passing the COMMAREA.

```
COSGN00C ──XCTL──► COADM01C         (admin user login → admin menu)
COSGN00C ──XCTL──► COMEN01C         (regular user login → main menu)

COADM01C ──XCTL──► COUSR00C         (admin menu → User List)
COADM01C ──XCTL──► COUSR01C         (admin menu → User Add)
COADM01C ──XCTL──► COUSR02C         (admin menu → User Update)
COADM01C ──XCTL──► COUSR03C         (admin menu → User Delete)
COADM01C ──XCTL──► COTRTLIC         (admin menu → Transaction Type List)
COADM01C ──XCTL──► COTRTUPC         (admin menu → Transaction Type Update)

COMEN01C ──XCTL──► COACTVWC         (menu → Account View)
COMEN01C ──XCTL──► COACTUPC         (menu → Account Update)
COMEN01C ──XCTL──► COCRDLIC         (menu → Credit Card List)
COMEN01C ──XCTL──► COCRDSLC         (menu → Credit Card View)
COMEN01C ──XCTL──► COCRDUPC         (menu → Credit Card Update)
COMEN01C ──XCTL──► COTRN00C         (menu → Transaction List)
COMEN01C ──XCTL──► COTRN01C         (menu → Transaction View)
COMEN01C ──XCTL──► COTRN02C         (menu → Transaction Add)
COMEN01C ──XCTL──► CORPT00C         (menu → Transaction Reports)
COMEN01C ──XCTL──► COBIL00C         (menu → Bill Payment)
COMEN01C ──XCTL──► COPAUS0C         (menu → Pending Authorization View)

COCRDLIC ──XCTL──► COCRDSLC         (card list → card detail)
COCRDLIC ──XCTL──► COCRDUPC         (card list → card update)
COCRDSLC ──XCTL──► COCRDUPC         (card detail → card update)
COACTUPC ──XCTL──► COMEN01C         (account update → return to menu)
COACTVWC ──XCTL──► COMEN01C         (account view → return to menu)
```

### 1.4 Authorization Sub-App Calls

```
COPAUA0C ──CALL──► MQOPEN/MQGET/MQPUT1/MQCLOSE   (MQ Series API calls)
COPAUA0C ──EXEC DLI──► IMS DB                     (GU/REPL/ISRT/SCHD/TERM)
COPAUA0C ──EXEC CICS──► READ ACCTDAT/CARDXREF/CUSTDAT (VSAM access)

COPAUS0C ──EXEC DLI──► IMS DB                     (GNP/GU/SCHD/TERM)
COPAUS0C ──EXEC CICS──► READ ACCTDAT/CARDXREF/CUSTDAT
COPAUS1C ──EXEC DLI──► IMS DB                     (GU)
COPAUS2C ──EXEC SQL──► Db2 AUTHFRDS table          (UPDATE)
CBPAUP0C ──EXEC DLI──► IMS DB                     (GN/GNP/DLET/CHKP)

PAUDBLOD ──CALL──► CBLTDLI                        (IMS DLI calls: GU/ISRT)
PAUDBUNL ──CALL──► CBLTDLI                        (IMS DLI calls: GN/GNP)
DBUNLDGS ──CALL──► CBLTDLI                        (IMS DLI calls: GN/GNP/ISRT)
```

### 1.5 Transaction Type Sub-App (Db2)

```
COTRTLIC ──EXEC SQL──► Db2                         (SELECT/FETCH/OPEN/CLOSE on TRNTYPE, TRNTYCAT)
COTRTUPC ──EXEC SQL──► Db2                         (SELECT/UPDATE/INSERT/DELETE on TRNTYPE, TRNTYCAT)
COBTUPDT ──EXEC SQL──► Db2                         (SELECT/UPDATE on TRNTYPE)
```

### 1.6 VSAM-MQ Sub-App

```
COACCT01 ──CALL──► MQOPEN/MQGET/MQPUT/MQCLOSE     (MQ Series for account inquiry)
CODATE01 ──CALL──► MQOPEN/MQGET/MQPUT/MQCLOSE     (MQ Series for date validation)
```

---

## 2. CICS Navigation Graph

```
                          ┌─────────────┐
                          │  COSGN00C   │
                          │  (Signon)   │
                          └──────┬──────┘
                   Admin ┌───────┴───────┐ Regular
                         ▼               ▼
                  ┌────────────┐  ┌────────────┐
                  │ COADM01C   │  │ COMEN01C   │
                  │ (Admin Menu)│  │ (Main Menu)│
                  └──────┬─────┘  └─────┬──────┘
         ┌───┬───┬───┬───┤              │
         ▼   ▼   ▼   ▼   ▼              ├──► COACTVWC (Account View)
    COUSR00C │   │   │ COTRTLIC         ├──► COACTUPC (Account Update)
    (Users)  │   │   │ (Tran Types)     ├──► COCRDLIC ──► COCRDSLC ──► COCRDUPC
    COUSR01C │   │ COTRTUPC             │    (Card List)  (Card View)  (Card Update)
    (Add Usr)│   │ (Tran Type Upd)      ├──► COTRN00C (Transaction List)
    COUSR02C │                          ├──► COTRN01C (Transaction View)
    (Upd Usr)│                          ├──► COTRN02C (Transaction Add)
    COUSR03C                            ├──► CORPT00C (Reports)
    (Del Usr)                           ├──► COBIL00C (Bill Payment)
                                        └──► COPAUS0C ──► COPAUS1C ──► COPAUS2C
                                             (Auth Summary) (Auth Detail) (Mark Fraud)
```

---

## 3. Dataset Lineage — JCL Jobs

### 3.1 VSAM Master Files (Created by IDCAMS in definition JCL jobs)

| VSAM Dataset | Definition JCL | Loaded From | Programs That Read | Programs That Write/Update |
|-------------|----------------|-------------|-------------------|---------------------------|
| `ACCTDATA.VSAM.KSDS` | ACCTFILE.jcl | ACCTDATA.PS | CBACT01C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C; COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C | CBACT04C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE) |
| `CARDDATA.VSAM.KSDS` | CARDFILE.jcl | CARDDATA.PS | CBACT02C, CBEXPORT; COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C | COCRDUPC (REWRITE) |
| `CUSTDATA.VSAM.KSDS` | CUSTFILE.jcl | CUSTDATA.PS | CBCUS01C, CBEXPORT, CBSTM03A; COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C | — (read-only in current programs) |
| `CARDXREF.VSAM.KSDS` | XREFFILE.jcl | CARDXREF.PS | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A; COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN00C, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C | — (read-only) |
| `TRANSACT.VSAM.KSDS` | TRANFILE.jcl | DALYTRAN.PS.INIT | CBEXPORT, CBTRN03C, COTRN00C, COTRN01C, COBIL00C | CBTRN01C (WRITE), CBTRN02C (WRITE), COTRN02C (WRITE), COBIL00C (WRITE) |
| `USRSEC.VSAM.KSDS` | DUSRSECJ.jcl | inline data | COSGN00C (READ), COUSR00C (STARTBR/READNEXT) | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) |
| `TCATBALF.VSAM.KSDS` | TCATBALF.jcl | TCATBALF.PS | CBACT04C, CBTRN02C | CBACT04C (implicit via balance update), CBTRN02C (REWRITE) |
| `DISCGRP.VSAM.KSDS` | DISCGRP.jcl | DISCGRP.PS | CBACT04C | — (read-only) |
| `TRANTYPE.VSAM.KSDS` | TRANTYPE.jcl | TRANTYPE.PS | — (online via CICS) | — |
| `TRANCATG.VSAM.KSDS` | TRANCATG.jcl | TRANCATG.PS | — (online via CICS) | — |
| `TRXFL.VSAM.KSDS` | CREASTMT.JCL | sorted from TRANSACT | CBSTM03A | — |

### 3.2 Sequential/GDG Datasets

| Dataset | Created By | Read By | Purpose |
|---------|-----------|---------|---------|
| `DALYTRAN.PS` | External feed | CBTRN01C, CBTRN02C | Daily inbound transaction feed |
| `DALYREJS(+n)` | CBTRN01C/CBTRN02C (POSTTRAN.jcl) | — (review) | Rejected daily transactions |
| `SYSTRAN(+n)` | CBACT04C (INTCALC.jcl) | COMBTRAN.jcl (SORT input) | System-generated interest transactions |
| `TRANSACT.BKUP(+n)` | TRANBKP.jcl / TRANREPT.jcl (REPROC) | COMBTRAN.jcl (SORT input) | Transaction file backup (GDG) |
| `TRANSACT.COMBINED(+n)` | COMBTRAN.jcl (SORT output) | COMBTRAN.jcl STEP10 (reload to VSAM) | Combined backup + system transactions |
| `TRANSACT.DALY(+n)` | TRANREPT.jcl (SORT output) | CBTRN03C (TRANREPT.jcl STEP10R) | Sorted daily transactions for reporting |
| `TCATBALF.BKUP(+n)` | PRTCATBL.jcl (REPROC) | PRTCATBL.jcl (SORT input) | Category balance backup |
| `TCATBALF.REPT` | PRTCATBL.jcl (SORT output) | — (printed report) | Category balance report |
| `EXPORT.DATA` | CBEXPORT (CBEXPORT.jcl) | CBIMPORT (CBIMPORT.jcl) | Branch migration export |
| `STATEMNT.HTML` | CBSTM03A (CREASTMT.JCL) | — (distributed) | HTML account statements |
| `STATEMNT.PS` | CBSTM03A (CREASTMT.JCL) | — (printed) | Print-format statements |
| `TRXFL.SEQ` | CREASTMT.JCL SORT | CREASTMT.JCL IDCAMS (load to VSAM) | Intermediate sorted transaction file |

---

## 4. End-to-End Batch Pipeline Flows

### 4.1 Daily Batch Cycle (Control-M: `DAILY-TransactionBackup`)

```
 ┌─────────────────────────────────────────────────────────────────────┐
 │                     DAILY BATCH CYCLE                               │
 │                                                                     │
 │  1. CLOSEFIL.jcl         Close CICS VSAM files                     │
 │         │                                                           │
 │         ▼                                                           │
 │  2. TRANBKP.jcl          Backup TRANSACT.VSAM.KSDS                 │
 │     ├── REPROC            → TRANSACT.BKUP(+1)                      │
 │     └── IDCAMS            Delete/redefine TRANSACT KSDS             │
 │         │                                                           │
 │         ▼                                                           │
 │  3. WAITSTEP.jcl         Delay (COBSWAIT program)                  │
 │         │                                                           │
 │         ▼                                                           │
 │  4. OPENFIL.jcl          Reopen CICS VSAM files                    │
 │                                                                     │
 └─────────────────────────────────────────────────────────────────────┘
```

### 4.2 Daily Transaction Posting (triggered outside Control-M)

```
 ┌─────────────────────────────────────────────────────────────────────┐
 │               DAILY TRANSACTION POSTING                             │
 │                                                                     │
 │  POSTTRAN.jcl (PGM=CBTRN02C)                                       │
 │                                                                     │
 │  Input:                                                             │
 │    DALYTRAN.PS ──────────────────────┐                              │
 │    CARDXREF.VSAM.KSDS (lookup) ──────┤                              │
 │    ACCTDATA.VSAM.KSDS (lookup+update)┤                              │
 │    TCATBALF.VSAM.KSDS (update) ──────┤                              │
 │                                      ▼                              │
 │                              ┌──────────────┐                       │
 │                              │   CBTRN02C   │                       │
 │                              └───────┬──────┘                       │
 │                           ┌──────────┼──────────┐                   │
 │                           ▼          ▼          ▼                   │
 │                 TRANSACT.VSAM.KSDS  DALYREJS(+1)  TCATBALF update   │
 │                 (posted trans)      (rejects)     (balance update)   │
 │                                                                     │
 └─────────────────────────────────────────────────────────────────────┘
```

### 4.3 Daily Transaction Report

```
 ┌─────────────────────────────────────────────────────────────────────┐
 │               DAILY TRANSACTION REPORT                              │
 │                                                                     │
 │  TRANREPT.jcl                                                       │
 │                                                                     │
 │  Step 1: REPROC     TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1)        │
 │  Step 2: SORT       TRANSACT.BKUP(+1) → TRANSACT.DALY(+1) (sorted)│
 │  Step 3: CBTRN03C   TRANSACT.DALY(+1) → printed report             │
 │                     + CARDXREF.VSAM.KSDS (lookup)                   │
 │                                                                     │
 └─────────────────────────────────────────────────────────────────────┘
```

### 4.4 Monthly Interest Calculation Cycle (Control-M: `MONTHLY-InterestCalculation`)

```
 ┌─────────────────────────────────────────────────────────────────────┐
 │               MONTHLY INTEREST CALCULATION CYCLE                    │
 │                                                                     │
 │  1. CLOSEFIL.jcl          Close CICS VSAM files                    │
 │         │                                                           │
 │         ▼                                                           │
 │  2. INTCALC.jcl (PGM=CBACT04C, PARM=date)                         │
 │     Input:                                                          │
 │       TCATBALF.VSAM.KSDS ─────┐                                    │
 │       CARDXREF.VSAM.KSDS ─────┤  Interest                          │
 │       CARDXREF.VSAM.AIX.PATH ─┤  Calculation                       │
 │       ACCTDATA.VSAM.KSDS ─────┤  ───► ACCTDATA updated             │
 │       DISCGRP.VSAM.KSDS ──────┘       SYSTRAN(+1) generated        │
 │         │                                                           │
 │         ▼                                                           │
 │  3. COMBTRAN.jcl                                                    │
 │     Input: TRANSACT.BKUP(0) + SYSTRAN(0)                           │
 │     SORT/merge → TRANSACT.COMBINED(+1)                              │
 │     Reload → TRANSACT.VSAM.KSDS                                    │
 │         │                                                           │
 │         ▼                                                           │
 │  4. WAITSTEP.jcl          Delay                                    │
 │         │                                                           │
 │         ▼                                                           │
 │  5. OPENFIL.jcl           Reopen CICS VSAM files                   │
 │                                                                     │
 └─────────────────────────────────────────────────────────────────────┘
```

### 4.5 Weekly Disclosure Groups Refresh (Control-M: `WEEKLY-DisclosureGroupsRefresh`)

```
CLOSEFIL → DISCGRP.jcl (reload DISCGRP.PS → DISCGRP.VSAM.KSDS) → WAITSTEP → OPENFIL
```

### 4.6 Weekly Transaction Types Db2 Refresh (Control-M: `WEEKLY-TransactionTypesDBRefresh`)

```
MNTTRDB2.jcl (maintain Db2 tables) → TRANEXTR.jcl (extract from Db2)
```

### 4.7 Statement Generation

```
 ┌─────────────────────────────────────────────────────────────────────┐
 │               STATEMENT GENERATION                                  │
 │                                                                     │
 │  CREASTMT.JCL                                                       │
 │                                                                     │
 │  Step 1: IDCAMS     Delete old TRXFL.VSAM.KSDS                     │
 │  Step 2: SORT       TRANSACT.VSAM.KSDS → TRXFL.SEQ (sorted)        │
 │  Step 3: IDCAMS     TRXFL.SEQ → TRXFL.VSAM.KSDS                    │
 │  Step 4: IEFBR14    Clean up old statement files                    │
 │                                                                     │
 │  Then run CBSTM03A (statement program):                             │
 │    TRXFL.VSAM.KSDS + CARDXREF + CUSTDAT + ACCTDAT                  │
 │    → STATEMNT.HTML + STATEMNT.PS                                    │
 │                     (CBSTM03A calls CBSTM03B 12× for I/O)          │
 │                                                                     │
 └─────────────────────────────────────────────────────────────────────┘
```

### 4.8 Branch Migration Export/Import

```
 CBEXPORT.jcl                           CBIMPORT.jcl
 ┌──────────────────────┐               ┌──────────────────────┐
 │ CBEXPORT program     │               │ CBIMPORT program     │
 │                      │               │                      │
 │ CUSTDATA.VSAM.KSDS ──┤               │ EXPORT.DATA ─────────┤
 │ ACCTDATA.VSAM.KSDS ──┤               │                      │
 │ CARDXREF.VSAM.KSDS ──┤──► EXPORT.DATA│──► CUSTDATA.IMPORT   │
 │ TRANSACT.VSAM.KSDS ──┤               │──► ACCTDATA.IMPORT   │
 │ CARDDATA.VSAM.KSDS ──┘               │──► CARDXREF.IMPORT   │
 │                      │               │──► TRANSACT.IMPORT    │
 └──────────────────────┘               │──► IMPORT.ERRORS      │
                                        └──────────────────────┘
```

---

## 5. Copybook Dependency Matrix

| Copybook | Used By Programs |
|----------|-----------------|
| **COCOM01Y** | COSGN00C, COADM01C, COMEN01C, COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C–03C, COPAUS0C, COPAUS1C (18 programs) |
| **COTTL01Y** | All 18 CICS programs (screen title) |
| **CSDAT01Y** | All 18 CICS programs (date/time) |
| **CSMSG01Y** | All 18 CICS programs (messages) |
| **CSMSG02Y** | All 18 CICS programs (abend data) |
| **CVACT01Y** | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C (14 programs) |
| **CVACT02Y** | CBACT02C, CBEXPORT, CBIMPORT, COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C, COPAUS0C (8 programs) |
| **CVACT03Y** | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN00C, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C (17 programs) |
| **CVCUS01Y** | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C (10 programs) |
| **CVCRD01Y** | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC (5 programs) |
| **CVTRA05Y** | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C (10 programs) |
| **CSUSR01Y** | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C (5 programs) |
| **CVEXPORT** | CBEXPORT, CBIMPORT (2 programs) |
| **CSSETATY** | COACTUPC (30 replacements — attribute setting) |
| **CSLKPCDY** | COACTUPC (ZIP code validation) |
