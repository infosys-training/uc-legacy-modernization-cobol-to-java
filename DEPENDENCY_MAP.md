# DEPENDENCY MAP

> **Application:** CardDemo -- Credit Card Management System (Mainframe)
> **Repository:** infosys-training/uc-legacy-modernization-cobol-to-java
> **Generated:** 2026-06-09

---

## 1. Program-to-Program Call Graph

### 1.1 Online Navigation Tree (CICS XCTL / LINK)

```
COSGN00C (Sign-On — Entry Point)
│
├── [Admin Path]
│   └── COADM01C (Admin Menu)
│       ├── COUSR00C (User List)
│       │   ├── COUSR02C (User Update) ← XCTL
│       │   └── COUSR03C (User Delete) ← XCTL
│       ├── COUSR01C (User Add) ← XCTL
│       ├── COTRTLIC (Transaction Type List — DB2) ← XCTL
│       └── COTRTUPC (Transaction Type Update — DB2) ← XCTL
│
└── [User Path]
    └── COMEN01C (Main Menu — Central Hub, 11 targets)
        ├── COACTVWC (Account View) ← XCTL
        ├── COACTUPC (Account Update) ← XCTL
        ├── COCRDLIC (Card List) ← XCTL
        │   ├── COCRDSLC (Card View) ← XCTL
        │   └── COCRDUPC (Card Update) ← XCTL
        ├── COTRN00C (Transaction List) ← XCTL
        │   └── COTRN01C (Transaction View) ← XCTL
        ├── COTRN02C (Transaction Add) ← XCTL
        ├── CORPT00C (Report Request) ← XCTL
        │   └── submits JCL: INTRDRJ1 / INTRDRJ2 (batch reports)
        ├── COBIL00C (Bill Payment) ← XCTL
        └── COPAUS0C (Auth Summary — IMS) ← XCTL
            └── COPAUS1C (Auth Detail — IMS) ← CICS LINK
                └── COPAUS2C (Fraud Update — DB2) ← CICS LINK

All CICS programs return to their caller via EXEC CICS RETURN TRANSID / XCTL.
COSGN00C routes to COADM01C (admin) or COMEN01C (user) based on SEC-USR-TYPE.
```

### 1.2 Batch CALL Graph

```
CBACT01C ──CALL──► COBDATFT    (Assembler date formatter)
CBSTM03A ──CALL──► CBSTM03B   (I/O submodule for statement generation)
CBACT02C ──CALL──► CEE3ABD     (LE abnormal termination)
CBACT03C ──CALL──► CEE3ABD     (LE abnormal termination)
CBCUS01C ──CALL──► CEE3ABD     (LE abnormal termination)
COBSWAIT ──CALL──► MVSWAIT     (Assembler wait routine)
CSUTLDTC ──CALL──► CEEDAYS    (LE date conversion)
```

### 1.3 IMS DL/I Call Chains

```
COPAUA0C ──CBLTDLI──► IMS DB (GU, SCHD, TERM on auth segments)
         ──MQOPEN/MQGET/MQPUT1──► MQ Queue Manager
COPAUS0C ──CBLTDLI──► IMS DB (GU, GNP on auth summary/detail)
COPAUS1C ──CBLTDLI──► IMS DB (GU, GNP, REPL on auth detail)
CBPAUP0C ──CBLTDLI──► IMS DB (GN, GNP, DLET — purge expired)
DBUNLDGS ──CBLTDLI──► IMS DB (GN, GNP) + GSAM (ISRT)
PAUDBLOD ──CBLTDLI──► IMS DB (ISRT, GU — load)
PAUDBUNL ──CBLTDLI──► IMS DB (GN, GNP — unload)
```

### 1.4 MQ Communication

```
COPAUA0C ── MQOPEN/MQGET ──► Auth Request Queue (CCPAURQY format)
         ── MQPUT1       ──► Auth Response Queue (CCPAURLY format)
COACCT01 ── MQGET/MQPUT  ──► Account Inquiry Queue
CODATE01 ── MQGET/MQPUT  ──► Date Service Queue
```

### 1.5 Program Dependency Adjacency List

| Program | Calls / Transfers To | Called By / Transferred From |
|---------|---------------------|----------------------------|
| COSGN00C | COADM01C, COMEN01C | (entry point, CICS TRANSID CC00) |
| COADM01C | COUSR00C, COUSR01C, COTRTLIC, COTRTUPC | COSGN00C |
| COMEN01C | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C | COSGN00C |
| COACTUPC | (return to COMEN01C) | COMEN01C |
| COACTVWC | (return to COMEN01C) | COMEN01C |
| COCRDLIC | COCRDSLC, COCRDUPC | COMEN01C |
| COCRDSLC | (return to COCRDLIC) | COCRDLIC |
| COCRDUPC | (return to COCRDLIC) | COCRDLIC |
| COTRN00C | COTRN01C | COMEN01C |
| COTRN01C | (return to COTRN00C) | COTRN00C |
| COTRN02C | (return to COMEN01C) | COMEN01C |
| CORPT00C | submits INTRDRJ1/J2 | COMEN01C |
| COBIL00C | (return to COMEN01C) | COMEN01C |
| COUSR00C | COUSR02C, COUSR03C | COADM01C |
| COUSR01C | (return to COADM01C) | COADM01C |
| COUSR02C | (return to COUSR00C) | COUSR00C |
| COUSR03C | (return to COUSR00C) | COUSR00C |
| COTRTLIC | (return to COADM01C) | COADM01C |
| COTRTUPC | (return to COADM01C) | COADM01C |
| COPAUS0C | COPAUS1C | COMEN01C |
| COPAUS1C | COPAUS2C | COPAUS0C |
| COPAUS2C | (return to COPAUS1C) | COPAUS1C |
| CBSTM03A | CBSTM03B | JCL CREASTMT |
| CBACT01C | COBDATFT | JCL READACCT |

---

## 2. Dataset Lineage

### 2.1 VSAM File Ownership Map

| VSAM Dataset | VSAM Type | Key | Record Size | Setup JCL | Writer Programs | Reader Programs |
|-------------|-----------|-----|-------------|-----------|-----------------|-----------------|
| ACCTDATA.VSAM.KSDS (ACCTFILE) | KSDS | ACCT-ID, 9(11), pos 0 | 300 bytes | ACCTFILE.jcl | CBACT04C, CBIMPORT, COACTUPC, COBIL00C, COTRN02C | CBACT01C, CBEXPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C (13+ programs) |
| CARDDATA.VSAM.KSDS (CARDFILE) | KSDS+AIX | CARD-NUM, X(16), pos 0 | 150 bytes | CARDFILE.jcl | COCRDUPC, CBIMPORT | CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COPAUS0C |
| CUSTDATA.VSAM.KSDS (CUSTFILE) | KSDS | CUST-ID, 9(09), pos 0 | 500 bytes | CUSTFILE.jcl | CBIMPORT | CBCUS01C, CBEXPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| CARDXREF.VSAM.KSDS (XREFFILE) | KSDS | XREF-CARD-NUM, X(16), pos 0 | 50 bytes | XREFFILE.jcl | CBIMPORT | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C |
| TRANSACT.VSAM.KSDS | KSDS | TRAN-ID, X(16), pos 0 | 350 bytes | TRANFILE.jcl | CBTRN02C, CBIMPORT, COBIL00C, COTRN02C | CBEXPORT, CBTRN03C, COTRN00C, COTRN01C, COBIL00C |
| USRSEC.VSAM.KSDS | KSDS | SEC-USR-ID, X(08), pos 0 | varies | DUSRSECJ.jcl | COUSR01C, COUSR02C | COSGN00C, COUSR00C, COUSR02C, COUSR03C |
| TCATBALF.VSAM.KSDS (TCATBAL) | KSDS | composite | varies | TCATBALF.jcl | CBTRN02C, CBACT04C | CBACT04C |
| DISCGRP.VSAM.KSDS | KSDS | DIS-ACCT-GROUP-ID | varies | DISCGRP.jcl | (setup only) | CBACT04C |
| TRANTYPE.VSAM.KSDS | KSDS | TRAN-TYPE | varies | TRANTYPE.jcl | (setup only) | CBTRN03C |
| TRANCATG.VSAM.KSDS | KSDS | TRAN-CAT-KEY | varies | TRANCATG.jcl | (setup only) | CBTRN03C |
| DALYREJS.VSAM.KSDS | KSDS | | varies | DALYREJS.jcl | CBTRN02C | (audit trail) |

### 2.2 GDG (Generation Data Group) Datasets

| GDG Base | Defined By | Written By | Read By | Purpose |
|----------|-----------|------------|---------|---------|
| TRANSACT.BKUP | DEFGDGB.jcl | TRANBKP.jcl (IDCAMS REPRO) | COMBTRAN.jcl | Transaction backup (rolling generations) |
| DALYTRAN | DEFGDGD.jcl | External feed / online COTRN02C | POSTTRAN.jcl (CBTRN02C) | Daily transaction input |
| SYSTRAN | (external) | System-generated transactions | COMBTRAN.jcl | System-generated transactions |
| TRANSACT.COMBINED | COMBTRAN.jcl | COMBTRAN.jcl (SORT+REPRO) | (loaded to TRANSACT VSAM) | Merged transaction file |

### 2.3 Flat/Sequential Datasets

| Dataset | Created By | Consumed By | Purpose |
|---------|-----------|-------------|---------|
| ACCTDATA.PS | (data provisioning) | ACCTFILE.jcl (REPRO) | Flat file seed data for accounts |
| CARDDATA.PS | (data provisioning) | CARDFILE.jcl (REPRO) | Flat file seed data for cards |
| CUSTDATA.PS | (data provisioning) | CUSTFILE.jcl (REPRO) | Flat file seed data for customers |
| CARDXREF.PS | (data provisioning) | XREFFILE.jcl (REPRO) | Flat file seed data for xrefs |
| TRANSACT.PS | (data provisioning) | TRANFILE.jcl (REPRO) | Flat file seed data for transactions |
| USRSEC.PS | (data provisioning) | DUSRSECJ.jcl (REPRO) | Flat file seed data for users |
| EXPORT.DATA | CBEXPORT.jcl (CBEXPORT program) | CBIMPORT.jcl (CBIMPORT program) | Multi-record export for branch migration |
| IMPORT.ERRORS | CBIMPORT.jcl | (manual review) | Import error/reject log |
| REPTFILE | CBTRN03C (TRANREPT.jcl) | TXT2PDF1.JCL | Transaction report output |
| STMT-FILE | CBSTM03A (CREASTMT.JCL) | (distribution) | Statement text output |
| HTML-FILE | CBSTM03A (CREASTMT.JCL) | (distribution) | Statement HTML output |

### 2.4 DB2 Tables

| Table | Accessed By | Operations | JCL |
|-------|-------------|------------|-----|
| TRAN_TYPE | COTRTLIC, COTRTUPC, COBTUPDT | SELECT, INSERT, UPDATE, DELETE (cursor-based) | CREADB21.jcl (DDL), MNTTRDB2.jcl (maintenance), TRANEXTR.jcl (extract) |
| TRAN_CATEGORY | COTRTLIC, COTRTUPC | SELECT, DELETE (cascading) | CREADB21.jcl |
| Fraud flags table | COPAUS2C | INSERT, UPDATE | (within CICS) |

### 2.5 IMS Database Segments

| Segment | DBD | Parent | Accessed By | DL/I Functions |
|---------|-----|--------|------------|----------------|
| Auth Summary (CIPAUSMY) | PAUTBPCB | (root) | COPAUA0C, COPAUS0C, COPAUS1C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS | GU, GN, GNP, ISRT, DLET |
| Auth Detail (CIPAUDTY) | PAUTBPCB | Auth Summary | Same as above + COPAUS2C | GU, GNP, REPL, ISRT, DLET |

---

## 3. JCL-to-Program-to-Dataset Lineage

### 3.1 Data Setup Pipeline

```
Flat Files (*.PS)
    │
    ├── ACCTFILE.jcl ── IDCAMS DEFINE + REPRO ──► ACCTDATA.VSAM.KSDS
    ├── CARDFILE.jcl ── IDCAMS DEFINE + REPRO + BLDINDEX ──► CARDDATA.VSAM.KSDS + AIX
    ├── CUSTFILE.jcl ── IDCAMS DEFINE + REPRO ──► CUSTDATA.VSAM.KSDS
    ├── XREFFILE.jcl ── IDCAMS DEFINE + REPRO ──► CARDXREF.VSAM.KSDS
    ├── TRANFILE.jcl ── IDCAMS DEFINE + REPRO ──► TRANSACT.VSAM.KSDS
    ├── DUSRSECJ.jcl ── IDCAMS DEFINE + REPRO ──► USRSEC.VSAM.KSDS
    ├── DISCGRP.jcl ── IDCAMS DEFINE + REPRO ──► DISCGRP.VSAM.KSDS
    ├── TCATBALF.jcl ── IDCAMS DEFINE + REPRO ──► TCATBALF.VSAM.KSDS
    ├── TRANCATG.jcl ── IDCAMS DEFINE + REPRO ──► TRANCATG.VSAM.KSDS
    └── TRANTYPE.jcl ── IDCAMS DEFINE + REPRO ──► TRANTYPE.VSAM.KSDS
```

### 3.2 Daily Batch Processing Pipeline

```
                        ┌─────────────────────────────────────────────┐
                        │           DAILY BATCH CYCLE                  │
                        └─────────────────────────────────────────────┘

Step 1: CLOSEFIL.jcl
        Close CICS files (TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC)
            │
            v
Step 2: POSTTRAN.jcl ── CBTRN02C
        ┌──────────────────────────────────────────┐
        │ Input:  DALYTRAN (GDG daily feed)        │
        │         CARDXREF.VSAM.KSDS (lookup)      │
        │ Output: TRANSACT.VSAM.KSDS (posted)      │
        │         DALYREJS.VSAM.KSDS (rejects)     │
        │ Update: ACCTDATA.VSAM.KSDS (balances)    │
        │         TCATBALF.VSAM.KSDS (cat totals)  │
        └──────────────────────────────────────────┘
            │
            v
Step 3: INTCALC.jcl ── CBACT04C
        ┌──────────────────────────────────────────┐
        │ Input:  TCATBALF, XREFFILE, DISCGRP      │
        │         ACCTDATA (current balances)       │
        │ Output: ACCTDATA (interest applied)       │
        │         TRANFILE (interest transactions)  │
        └──────────────────────────────────────────┘
            │
            v
Step 4: CREASTMT.JCL ── CBSTM03A → CBSTM03B
        ┌──────────────────────────────────────────┐
        │ Input:  XREFFILE, CUSTFILE, ACCTFILE      │
        │         TRANSACT                          │
        │ Output: STMT-FILE (text statements)       │
        │         HTML-FILE (HTML statements)       │
        └──────────────────────────────────────────┘
            │
            v
Step 5: TRANREPT.jcl ── CBTRN03C
        ┌──────────────────────────────────────────┐
        │ Input:  TRANSACT, CARDXREF, TRANTYPE      │
        │         TRANCATG, DATEPARM                │
        │ Output: REPTFILE (daily report)           │
        └──────────────────────────────────────────┘
            │
            v
Step 6: TRANBKP.jcl
        REPRO TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1)
            │
            v
Step 7: OPENFIL.jcl
        Re-open CICS files for online access
```

### 3.3 Transaction Combination / Merge Pipeline

```
TRANSACT.BKUP(0) ─┐
                   ├── COMBTRAN.jcl ── SORT + REPRO ──► TRANSACT.COMBINED(+1)
SYSTRAN(0) ────────┘                                        │
                                                            v
                                                    TRANSACT.VSAM.KSDS
```

### 3.4 Data Export/Import Pipeline

```
Export:
  CUSTFILE + ACCTFILE + XREFFILE + TRANSACT + CARDFILE
       │
       └── CBEXPORT.jcl ── CBEXPORT ──► EXPORT.DATA (multi-record VSAM)

Import:
  EXPORT.DATA
       │
       └── CBIMPORT.jcl ── CBIMPORT
             ├──► CUSTDATA.IMPORT (flat, LRECL=500)
             ├──► ACCTDATA.IMPORT (flat, LRECL=300)
             ├──► CARDXREF.IMPORT (flat, LRECL=50)
             ├──► TRANSACT.IMPORT (flat, LRECL=350)
             └──► IMPORT.ERRORS (flat, LRECL=132)
```

### 3.5 IMS Database Management Pipeline

```
Load:
  Input flat file ── LOADPADB.JCL ── DFSRRC00 → PAUDBLOD ──► IMS DB (auth segments)

Unload (standard):
  IMS DB ── UNLDPADB.JCL ── DFSRRC00 → PAUDBUNL ──► Output flat file

Unload (GSAM):
  IMS DB ── UNLDGSAM.JCL ── DFSRRC00 → DBUNLDGS ──► GSAM sequential file

Purge expired:
  IMS DB ── CBPAUP0J.jcl ── DFSRRC00 (BMP) → CBPAUP0C ──► IMS DB (expired records deleted)
```

### 3.6 Report Pipeline

```
Online request:
  CORPT00C ── EXEC CICS WRITEQ(INTRDR) ──► INTRDRJ1.JCL / INTRDRJ2.JCL
                                                  │
                                                  v
                                            CBTRN03C ──► REPTFILE
                                                              │
                                                              v
                                                   TXT2PDF1.JCL ──► PDF output
```

---

## 4. Cross-Cutting Dependencies

### 4.1 Copybook Usage Heatmap (Most Referenced)

| Copybook | # Programs Using | Role |
|----------|-----------------|------|
| COCOM01Y | 21 | CICS COMMAREA -- all online programs |
| DFHAID | 17 | CICS AID key constants |
| DFHBMSCA | 17 | CICS BMS screen attributes |
| CSDAT01Y | 17 | Date/time working storage |
| COTTL01Y | 17 | Screen title constants |
| CSMSG01Y | 17 | Message line 1 |
| CVACT01Y | 13 | Account record -- most shared data entity |
| CVACT03Y | 11 | Card-account cross-reference |
| CSUSR01Y | 10 | User security record |
| CVCUS01Y | 10 | Customer record |
| CVACT02Y | 8 | Card record |
| CVTRA05Y | 10 | Transaction master record |
| CIPAUSMY | 7 | IMS auth summary segment |
| CIPAUDTY | 8 | IMS auth detail segment |

### 4.2 Program Coupling Metrics

| Program | Direct Dependencies (outgoing) | Depended On By (incoming) | Hub Score |
|---------|-------------------------------|--------------------------|-----------|
| COMEN01C | 11 | 1 (COSGN00C) | **Central Hub** |
| COADM01C | 6 | 1 (COSGN00C) | Admin Hub |
| COSGN00C | 2 | 0 (entry point) | Gateway |
| COPAUA0C | 3 subsystems (MQ, IMS, VSAM) | 0 | Integration Hub |
| COCRDLIC | 2 | 1 | List Hub |
| COUSR00C | 2 | 1 | User Hub |
| CBSTM03A | 1 (CBSTM03B) | 1 (CREASTMT.JCL) | Batch Module |

### 4.3 Shared File Contention Points

Files written by multiple programs (potential contention during parallel execution):

| File | Writers | Risk |
|------|---------|------|
| ACCTDATA.VSAM.KSDS | CBACT04C, CBIMPORT, COACTUPC, COBIL00C, COTRN02C (batch + online) | **HIGH** -- Batch and online writers; requires CICS file close/open cycle |
| TRANSACT.VSAM.KSDS | CBTRN02C, CBIMPORT, COBIL00C, COTRN02C (batch + online) | **HIGH** -- Same contention pattern |
| CARDDATA.VSAM.KSDS | COCRDUPC, CBIMPORT | LOW -- Limited writers |
| USRSEC.VSAM.KSDS | COUSR01C, COUSR02C | LOW -- Admin-only access |
