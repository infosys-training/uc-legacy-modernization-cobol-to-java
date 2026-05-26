# DEPENDENCY MAP — CardDemo COBOL Estate

> Generated from analysis of `uc-legacy-modernization-cobol-to-java/app/`

---

## 1. Program Call Graph

### 1.1 CICS Program Transfer (XCTL) — Online Navigation

```
COSGN00C (Sign-on)
  ├──XCTL──▶ COMEN01C (Main Menu)      [user type 'U']
  └──XCTL──▶ COADM01C (Admin Menu)     [user type 'A']

COMEN01C (Main Menu)
  ├──XCTL──▶ COACTVWC   (Account View)
  ├──XCTL──▶ COACTUPC   (Account Update)
  ├──XCTL──▶ COCRDLIC   (Card List)
  ├──XCTL──▶ COCRDSLC   (Card View)
  ├──XCTL──▶ COCRDUPC   (Card Update)
  ├──XCTL──▶ COTRN00C   (Transaction List)
  ├──XCTL──▶ COTRN01C   (Transaction View)
  ├──XCTL──▶ COTRN02C   (Transaction Add)
  ├──XCTL──▶ CORPT00C   (Transaction Reports)
  ├──XCTL──▶ COBIL00C   (Bill Payment)
  └──XCTL──▶ COPAUS0C   (Pending Auth View)

COADM01C (Admin Menu)
  ├──XCTL──▶ COUSR00C   (User List)
  ├──XCTL──▶ COUSR01C   (User Add)
  ├──XCTL──▶ COUSR02C   (User Update)
  ├──XCTL──▶ COUSR03C   (User Delete)
  ├──XCTL──▶ COTRTLIC   (Tran Type List - DB2)
  └──XCTL──▶ COTRTUPC   (Tran Type Update - DB2)
```

### 1.2 Inter-Program CALL Dependencies

```
CBSTM03A (Statement Generator)
  └──CALL──▶ CBSTM03B (Statement File I/O Sub-module)
               Called 3 times with different operations:
               - M03B-OPEN, M03B-READ, M03B-WRITE, M03B-CLOSE

COBSWAIT (Batch Wait)
  └──CALL──▶ MVSWAIT  (External MVS wait routine)

CSUTLDTC (Date Utility)
  └──CALL──▶ CEEDAYS  (LE date callable service)

COPAUA0C (Card Authorization)
  ├──CALL──▶ MQOPEN   (MQ Open queue)
  └──CALL──▶ MQGET    (MQ Get message)

COACCT01 (Account MQ Processor)
  ├──CALL──▶ MQOPEN   (MQ Open queue)
  ├──CALL──▶ MQGET    (MQ Get message)
  └──CALL──▶ MQPUT    (MQ Put message)

CODATE01 (Date MQ Processor)
  ├──CALL──▶ MQOPEN   (MQ Open queue)
  ├──CALL──▶ MQGET    (MQ Get message)
  └──CALL──▶ MQPUT    (MQ Put message)

COPAUS1C (Auth Detail View)
  └──LINK──▶ COPAUS2C (Mark as Fraud - DB2)

IMS Programs (CBLTDLI calls):
  CBPAUP0C ──CALL──▶ CBLTDLI (GN, GNP, DLET, CHKP)
  PAUDBLOD ──CALL──▶ CBLTDLI (ISRT, GU)
  PAUDBUNL ──CALL──▶ CBLTDLI (GN, GNP)
  DBUNLDGS ──CALL──▶ CBLTDLI (GN, GNP, ISRT)
```

### 1.3 CICS Online Program → File Access Map

```
Program         │ ACCTDAT │ CARDDAT │ CCXREF │ CXACAIX │ USRSEC │ TRANSACT │ CUSTDAT │
────────────────┼─────────┼─────────┼────────┼─────────┼────────┼──────────┼─────────┤
COSGN00C        │         │         │        │         │ READ   │          │         │
COACTVWC        │ READ    │         │ READ   │ READ    │        │          │ READ    │
COACTUPC        │ R/W     │         │ READ   │         │        │          │ R/W     │
COCRDLIC        │         │ BR/RN   │        │         │        │          │         │
COCRDSLC        │         │ READ    │        │         │        │          │ READ    │
COCRDUPC        │         │ R/W     │        │         │        │          │ READ    │
COTRN00C        │         │         │        │         │        │ BR/RN    │         │
COTRN01C        │         │         │        │         │        │ READ     │         │
COTRN02C        │         │         │ READ   │ READ    │        │ BR/W     │         │
COBIL00C        │ R/W     │         │        │ READ    │        │ BR/W     │         │
CORPT00C        │         │         │        │         │        │          │         │ *TDQ
COUSR00C        │         │         │        │         │ BR/RN  │          │         │
COUSR01C        │         │         │        │         │ WRITE  │          │         │
COUSR02C        │         │         │        │         │ R/W    │          │         │
COUSR03C        │         │         │        │         │ R/DEL  │          │         │
```

Legend: R/W = Read/Rewrite, BR/RN = STARTBR/READNEXT, BR/W = Browse + Write, R/DEL = Read/Delete, *TDQ = Transient Data Queue (JOBS)

---

## 2. Dataset Lineage

### 2.1 VSAM Master Files

```
File / Dataset                              │ Defined By   │ Written By         │ Read By
────────────────────────────────────────────┼──────────────┼────────────────────┼──────────────────────
AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS         │ ACCTFILE.jcl │ ACCTFILE.jcl       │ CBACT01C, CBACT04C,
(Account Master)                            │              │ COACTUPC, COBIL00C │ CBTRN01C, CBTRN02C,
                                            │              │                    │ COACTVWC, CBEXPORT,
                                            │              │                    │ CBSTM03A, COPAUS0C
                                            │              │                    │
AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS         │ CARDFILE.jcl │ CARDFILE.jcl       │ CBACT02C, COCRDLIC,
(Card Master)                               │              │ COCRDUPC           │ COCRDSLC, CBTRN01C,
                                            │              │                    │ CBEXPORT
                                            │              │                    │
AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS         │ XREFFILE.jcl │ XREFFILE.jcl       │ CBACT03C, CBACT04C,
(Card Cross-Reference)                      │              │                    │ CBTRN01C, CBTRN02C,
  + AIX on ACCT-ID (.AIX, .AIX.PATH)       │              │                    │ CBTRN03C, COACTVWC,
                                            │              │                    │ COTRN02C, COBIL00C,
                                            │              │                    │ CBEXPORT, CBSTM03A
                                            │              │                    │
AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS         │ CUSTFILE.jcl │ CUSTFILE.jcl       │ CBCUS01C, CBTRN01C,
(Customer Master)                           │              │ COACTUPC           │ COACTVWC, COCRDSLC,
                                            │              │                    │ COCRDUPC, CBEXPORT,
                                            │              │                    │ CBSTM03A
                                            │              │                    │
AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS         │ TRANFILE.jcl │ TRANFILE.jcl,      │ COTRN00C, COTRN01C,
(Transaction Master)                        │ COMBTRAN.jcl │ CBTRN02C, COTRN02C │ CBTRN03C, CBEXPORT,
                                            │              │ COBIL00C           │ CBSTM03A
                                            │              │                    │
AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS        │ TCATBALF.jcl │ TCATBALF.jcl,      │ CBACT04C, PRTCATBL.jcl
(Trans Category Balance)                    │              │ CBTRN02C, CBACT04C │
                                            │              │                    │
AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS         │ DISCGRP.jcl  │ DISCGRP.jcl        │ CBACT04C
(Disclosure Group / Interest Rates)         │              │                    │
                                            │              │                    │
AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS        │ TRANTYPE.jcl │ TRANTYPE.jcl       │ CBTRN03C
(Transaction Type Reference)                │              │                    │
                                            │              │                    │
AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS        │ TRANCATG.jcl │ TRANCATG.jcl      │ CBTRN03C
(Transaction Category Reference)            │              │                    │
                                            │              │                    │
AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS          │ DUSRSECJ.jcl │ DUSRSECJ.jcl,     │ COSGN00C, COUSR00C,
(User Security)                             │              │ COUSR01C, COUSR02C │ COUSR02C, COUSR03C
```

### 2.2 Sequential / GDG Files

```
File / Dataset                              │ Created By          │ Consumed By
────────────────────────────────────────────┼─────────────────────┼──────────────────
AWS.M2.CARDDEMO.DALYTRAN.PS                │ External feed       │ CBTRN01C, CBTRN02C
(Daily Transaction Input)                   │                     │ (via POSTTRAN.jcl)
                                            │                     │
AWS.M2.CARDDEMO.DALYREJS(+1)              │ CBTRN02C            │ Audit/review
(Rejected Transactions - GDG)               │ (via POSTTRAN.jcl)  │
                                            │                     │
AWS.M2.CARDDEMO.SYSTRAN(+1)               │ CBACT04C            │ COMBTRAN.jcl
(System-Generated Transactions - GDG)       │ (via INTCALC.jcl)   │
                                            │                     │
AWS.M2.CARDDEMO.TRANSACT.BKUP(+1)         │ TRANBKP.jcl         │ COMBTRAN.jcl
(Transaction Backup - GDG)                  │                     │
                                            │                     │
AWS.M2.CARDDEMO.TRANSACT.COMBINED(+1)     │ COMBTRAN.jcl (SORT) │ Reloaded to VSAM
(Merged Transactions)                       │                     │
                                            │                     │
AWS.M2.CARDDEMO.TRANREPT(+1)              │ CBTRN03C            │ TXT2PDF1.JCL
(Transaction Report - GDG)                  │ (via TRANREPT.jcl)  │ (PDF conversion)
                                            │                     │
AWS.M2.CARDDEMO.TCATBALF.BKUP(+1)        │ PRTCATBL.jcl (REPRO)│ PRTCATBL.jcl (SORT)
(Category Balance Backup)                   │                     │
                                            │                     │
AWS.M2.CARDDEMO.TCATBALF.REPT             │ PRTCATBL.jcl (SORT) │ Print/review
(Category Balance Report)                   │                     │
                                            │                     │
AWS.M2.CARDDEMO.ACCTDATA.PSCOMP           │ CBACT01C            │ External consumers
AWS.M2.CARDDEMO.ACCTDATA.ARRYPS           │ (via READACCT.jcl)  │
AWS.M2.CARDDEMO.ACCTDATA.VBPS             │                     │
(Account Data Extracts)                     │                     │
                                            │                     │
STMT-FILE, HTML-FILE                        │ CBSTM03A            │ Customer mailing /
(Customer Statements)                       │ (via CREASTMT.JCL)  │ web publishing
```

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Transaction Processing Pipeline

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH PROCESSING CYCLE                         │
│                                                                         │
│  ① DATA SETUP (run once / as needed)                                   │
│     ACCTFILE.jcl ──▶ Define & load Account VSAM                        │
│     CARDFILE.jcl ──▶ Define & load Card VSAM + AIX                     │
│     CUSTFILE.jcl ──▶ Define & load Customer VSAM                       │
│     XREFFILE.jcl ──▶ Define & load Cross-Ref VSAM + AIX               │
│     TRANFILE.jcl ──▶ Define & load Transaction VSAM                    │
│     TRANTYPE.jcl ──▶ Define & load Transaction Type VSAM              │
│     TRANCATG.jcl ──▶ Define & load Transaction Category VSAM          │
│     TCATBALF.jcl ──▶ Define & load Category Balance VSAM              │
│     DISCGRP.jcl  ──▶ Define & load Disclosure Group VSAM              │
│     DUSRSECJ.jcl ──▶ Define & load User Security VSAM                 │
│     DEFGDGB.jcl  ──▶ Define all GDG bases                             │
│     DEFGDGD.jcl  ──▶ Define reference data GDGs                       │
│                                                                         │
│  ② CICS OPERATIONS (online, during business hours)                     │
│     OPENFIL.jcl ──▶ Open VSAM files in CICS region                    │
│     User sessions via CICS transactions:                                │
│       COSGN00C → COMEN01C → functional programs                        │
│       (Account, Card, Transaction CRUD, Bill Payment)                  │
│     CLOSEFIL.jcl ──▶ Close VSAM files for batch                       │
│                                                                         │
│  ③ TRANSACTION BACKUP                                                   │
│     TRANBKP.jcl                                                         │
│       └──▶ REPRO from TRANSACT.VSAM.KSDS                              │
│             to TRANSACT.BKUP(+1)                                        │
│                                                                         │
│  ④ POST DAILY TRANSACTIONS                                             │
│     POSTTRAN.jcl (PGM=CBTRN02C)                                        │
│       ├── Input:  DALYTRAN.PS (daily transaction feed)                 │
│       ├── Lookup: CARDXREF.VSAM.KSDS                                   │
│       ├── Update: TRANSACT.VSAM.KSDS (insert new transactions)        │
│       ├── Update: ACCTDATA.VSAM.KSDS (update account balances)        │
│       ├── Update: TCATBALF.VSAM.KSDS (update category balances)       │
│       └── Output: DALYREJS(+1) (rejected transactions)                │
│                                                                         │
│  ⑤ INTEREST CALCULATION                                                 │
│     INTCALC.jcl (PGM=CBACT04C, PARM=date)                             │
│       ├── Input:  TCATBALF.VSAM.KSDS (category balances)              │
│       ├── Lookup: CARDXREF.VSAM.KSDS.AIX.PATH                         │
│       ├── Lookup: DISCGRP.VSAM.KSDS (interest rates)                  │
│       ├── Update: ACCTDATA.VSAM.KSDS (apply interest/fees)            │
│       └── Output: SYSTRAN(+1) (system-generated transactions)         │
│                                                                         │
│  ⑥ COMBINE TRANSACTIONS                                                │
│     COMBTRAN.jcl                                                        │
│       ├── Input:  TRANSACT.BKUP(0) (backup from step ③)               │
│       ├── Input:  SYSTRAN(0) (system trans from step ⑤)               │
│       ├── SORT:   Merge and sort by key                                │
│       └── Output: TRANSACT.VSAM.KSDS (reload combined data)           │
│                                                                         │
│  ⑦ TRANSACTION REPORTING                                                │
│     TRANREPT.jcl (PGM=CBTRN03C)                                        │
│       ├── Input:  TRANSACT.VSAM.KSDS                                   │
│       ├── Lookup: CARDXREF.VSAM.KSDS, TRANTYPE, TRANCATG              │
│       ├── Input:  DATE-PARMS (date range)                              │
│       └── Output: TRANREPT(+1) (daily report)                         │
│                                                                         │
│  ⑧ CATEGORY BALANCE REPORTING                                          │
│     PRTCATBL.jcl                                                        │
│       ├── Input:  TCATBALF.VSAM.KSDS                                  │
│       ├── Step1:  REPRO to TCATBALF.BKUP(+1)                          │
│       ├── Step2:  SORT with field formatting                           │
│       └── Output: TCATBALF.REPT (formatted report)                    │
│                                                                         │
│  ⑨ CUSTOMER STATEMENT GENERATION                                       │
│     CREASTMT.JCL (PGM=CBSTM03A)                                        │
│       ├── Input:  CARDXREF, CUSTDATA, ACCTDATA, TRANSACT VSAM         │
│       ├── Call:   CBSTM03B (file I/O sub-module)                       │
│       └── Output: STMT-FILE, HTML-FILE                                 │
│                                                                         │
│  ⑩ DATA READ / AUDIT JOBS (as needed)                                  │
│     READACCT.jcl (PGM=CBACT01C) ──▶ Extract account data              │
│     READCARD.jcl (PGM=CBACT02C) ──▶ Print card data                   │
│     READCUST.jcl (PGM=CBCUS01C) ──▶ Print customer data               │
│     READXREF.jcl (PGM=CBACT03C) ──▶ Print cross-ref data              │
│                                                                         │
│  ⑪ DATA EXPORT / IMPORT (branch migration)                             │
│     CBEXPORT.jcl (PGM=CBEXPORT) ──▶ Export all data to multi-record   │
│     CBIMPORT.jcl (PGM=CBIMPORT) ──▶ Import from export file           │
│                                                                         │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Authorization Sub-System Pipeline

```
┌─────────────────────────────────────────────────────────────────────────┐
│                   AUTHORIZATION PROCESSING                              │
│                                                                         │
│  REAL-TIME (CICS/MQ):                                                  │
│    MQ Request Queue ──▶ COPAUA0C ──▶ IMS Auth DB                       │
│                           ├── CICS READ: XREF, ACCT                    │
│                           ├── DLI: GU (auth check)                     │
│                           └──▶ MQ Response Queue                       │
│                                                                         │
│  ONLINE INQUIRY:                                                        │
│    COPAUS0C (Summary) ──▶ COPAUS1C (Detail) ──LINK──▶ COPAUS2C (Fraud) │
│                                                                         │
│  BATCH MAINTENANCE:                                                     │
│    DBPAUTP0.jcl ──▶ PAUDBLOD (Initial Load)                           │
│    UNLDPADB.JCL ──▶ PAUDBUNL (Unload to Flat)                         │
│    UNLDGSAM.JCL ──▶ DBUNLDGS (Unload to GSAM)                         │
│    CBPAUP0J.jcl ──▶ CBPAUP0C (Purge Expired)                          │
└─────────────────────────────────────────────────────────────────────────┘
```

### 3.3 Transaction Type DB2 Sub-System

```
┌─────────────────────────────────────────────────────────────────────────┐
│                TRANSACTION TYPE MANAGEMENT (DB2)                        │
│                                                                         │
│  SETUP:                                                                 │
│    CREADB21.jcl ──▶ Create DB2 tables                                  │
│    TRANEXTR.jcl ──▶ COBTUPDT (Load from VSAM)                         │
│                                                                         │
│  ONLINE (CICS/DB2):                                                    │
│    COTRTLIC ──▶ List/browse transaction types (DB2 cursor paging)      │
│    COTRTUPC ──▶ Update/delete transaction types (DB2 CRUD)             │
│                                                                         │
│  BATCH (DB2):                                                           │
│    COBTUPDT ──▶ Bulk INSERT/UPDATE/DELETE from flat file               │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Copybook Dependency Matrix

| Copybook | Used By (program count) | Key Consumers |
|----------|------------------------|---------------|
| **COCOM01Y** | 18+ | All CICS online programs |
| **DFHAID** | 17+ | All CICS programs (AID key definitions) |
| **DFHBMSCA** | 17+ | All CICS programs (BMS attributes) |
| **COTTL01Y** | 17+ | All CICS programs (screen titles) |
| **CSDAT01Y** | 17+ | All CICS programs (date/time) |
| **CSMSG01Y** | 17+ | All CICS programs (messages) |
| **CVACT01Y** | 10+ | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COTRN02C, COBIL00C, CBEXPORT, CBIMPORT, CBSTM03A, COACCT01 |
| **CVACT03Y** | 10+ | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, COACTVWC, COTRN02C, COBIL00C, CBEXPORT, CBIMPORT, CBSTM03A, COPAUS0C |
| **CVACT02Y** | 7+ | CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, CBTRN01C, CBEXPORT, CBIMPORT, COPAUS0C |
| **CVCUS01Y** | 7+ | CBCUS01C, CBTRN01C, COACTVWC, COACTUPC, CBEXPORT, CBIMPORT |
| **CVTRA05Y** | 9+ | CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C, CBEXPORT, CBIMPORT |
| **CVCRD01Y** | 6+ | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC |
| **CSUSR01Y** | 7 | COSGN00C, COMEN01C, COADM01C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| **CSMSG02Y** | 6 | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUS0C, COPAUS1C |
| **CSUTLDWY** | 2 | COACTUPC, COTRTUPC |
| **CVTRA01Y** | 2 | CBACT04C, CBTRN02C |
| **CVTRA06Y** | 2 | CBTRN01C, CBTRN02C |
| **CVEXPORT** | 2 | CBEXPORT, CBIMPORT |

---

## 5. JCL-to-Program Execution Map

| JCL Job | Program Executed | VSAM Files Accessed |
|---------|-----------------|---------------------|
| READACCT.jcl | CBACT01C | ACCTDATA.VSAM.KSDS → PSCOMP, ARRYPS, VBPS |
| READCARD.jcl | CBACT02C | CARDDATA.VSAM.KSDS |
| READCUST.jcl | CBCUS01C | CUSTDATA.VSAM.KSDS |
| READXREF.jcl | CBACT03C | CARDXREF.VSAM.KSDS |
| INTCALC.jcl | CBACT04C | TCATBALF, CARDXREF(.AIX.PATH), DISCGRP, ACCTDATA → SYSTRAN |
| POSTTRAN.jcl | CBTRN02C | DALYTRAN.PS, CARDXREF → TRANSACT, ACCTDATA, TCATBALF, DALYREJS |
| TRANREPT.jcl | CBTRN03C | TRANSACT, CARDXREF, TRANTYPE, TRANCATG → TRANREPT |
| CREASTMT.JCL | CBSTM03A | CARDXREF, CUSTDATA, ACCTDATA, TRANSACT → STMT-FILE, HTML-FILE |
| CBEXPORT.jcl | CBEXPORT | CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA → EXPFILE |
| CBIMPORT.jcl | CBIMPORT | EXPFILE → CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT |
| WAITSTEP.jcl | COBSWAIT | _(none — wait utility)_ |
| CBPAUP0J.jcl | CBPAUP0C | IMS Auth DB |
| DBPAUTP0.jcl | PAUDBLOD | Flat files → IMS Auth DB |
| UNLDPADB.JCL | PAUDBUNL | IMS Auth DB → Flat files |
| UNLDGSAM.JCL | DBUNLDGS | IMS Auth DB → GSAM files |
| TRANEXTR.jcl | COBTUPDT | Flat file → DB2 TRANSACTION_TYPE table |
