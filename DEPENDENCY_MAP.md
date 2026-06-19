# DEPENDENCY MAP — CardDemo Call Graph & Dataset Lineage

## 1. Inter-Program Call Graph

### CICS XCTL (Transfer Control) — Online Navigation

```
COSGN00C (Sign-on)
├── COADM01C (Admin Menu)      [if user type = 'A']
│   ├── COUSR00C (User List)
│   │   ├── COUSR01C (User Add)
│   │   ├── COUSR02C (User Update)
│   │   └── COUSR03C (User Delete)
│   ├── COTRTLIC (Tran Type List - DB2)
│   └── COTRTUPC (Tran Type Update - DB2)
└── COMEN01C (Main Menu)       [if user type = 'U']
    ├── COACTVWC (Account View)
    ├── COACTUPC (Account Update)
    ├── COCRDLIC (Card List)
    │   ├── COCRDSLC (Card View)
    │   └── COCRDUPC (Card Update)
    ├── COBIL00C (Bill Payment)
    ├── COTRN00C (Transaction List)
    │   └── COTRN01C (Transaction View)
    ├── COTRN02C (Transaction Add)
    ├── CORPT00C (Report Request)
    └── COADM01C (Admin Menu)   [option 11]
```

### Batch CALL Statements

```
CBACT01C ──CALL──> COBDATFT    (date formatting assembler routine)
CBACT01C ──CALL──> CEE3ABD     (LE abend service)
CBACT02C ──CALL──> CEE3ABD
CBACT04C ──CALL──> CEE3ABD
CBSTM03A ──CALL──> CBSTM03B   (statement file I/O submodule)
COBSWAIT ──CALL──> MVSWAIT    (assembler wait routine)
CSUTLDTC ──CALL──> CEEDAYS    (LE date conversion service)
```

### IMS DL/I Call Relationships (Authorization Sub-App)

```
COPAUA0C ──DL/I──> PAUTDB (Authorization Database)
         ──MQ────> Authorization Request Queue
         ──MQ────> Authorization Reply Queue
         ──DB2───> AUTHFRDS (Fraud Decision Table)

COPAUS0C ──DL/I──> PAUTDB (GU, GNP - browse summary)
COPAUS1C ──DL/I──> PAUTDB (GU, GNP, REPL - detail update)
COPAUS2C ──DB2───> AUTHFRDS (INSERT fraud flag)

CBPAUP0C ──DL/I──> PAUTDB (GN, GNP, DLET - purge expired)
PAUDBLOD ──DL/I──> PAUTDB (ISRT, GU - bulk load)
PAUDBUNL ──DL/I──> PAUTDB (GN, GNP - sequential unload)
DBUNLDGS ──DL/I──> PAUTDB (GN, GNP, ISRT via GSAM)
```

### DB2 SQL Relationships (Transaction Type Sub-App)

```
COTRTLIC ──SQL──> TRNTYPE    (SELECT with CURSOR for pagination)
         ──SQL──> TRNTYCAT   (JOIN for category details)

COTRTUPC ──SQL──> TRNTYPE    (UPDATE, DELETE)
         ──SQL──> TRNTYCAT   (DELETE cascade)

COBTUPDT ──SQL──> TRNTYPE    (batch UPDATE)
```

### MQ Relationships (VSAM-MQ Sub-App)

```
COACCT01 ──MQ──> Account Request Queue  (MQGET)
         ──MQ──> Account Reply Queue    (MQPUT)
         ──VSAM─> ACCTFILE              (READ for response)

CODATE01 ──MQ──> Date Request Queue     (MQGET)
         ──MQ──> Date Reply Queue       (MQPUT)
```

---

## 2. Dataset Lineage (VSAM Files)

### Core VSAM Datasets

| Dataset (DSN) | VSAM Type | Record Len | Key | Defined By | Written By | Read By |
|---------------|-----------|------------|-----|-----------|-----------|---------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | KSDS | 300 | 11,0 | ACCTFILE.jcl | COACTUPC, CBTRN02C, CBACT04C, COTRN02C | CBACT01C, COACTVWC, COACTUPC, CBSTM03A, CBTRN02C, CBACT04C, CBEXPORT, COBIL00C, COACCT01 |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | KSDS | 150 | 16,0 | CARDFILE.jcl | COCRDUPC | CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, CBTRN01C, CBEXPORT |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | KSDS | 500 | 9,0 | CUSTFILE.jcl | (external load) | CBCUS01C, COACTVWC, COCRDSLC, CBSTM03A, CBEXPORT |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | KSDS | 50 | 16,0 | XREFFILE.jcl | (external load) | COACTVWC, CBSTM03A, CBTRN01C, CBTRN02C, CBACT04C, COTRN00C, CBEXPORT |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.AIX.PATH | AIX PATH | 50 | alt | XREFFILE.jcl | — | CBACT04C (via AIX for acct-based lookup) |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | KSDS | 350 | 16,0 | TRANFILE.jcl | CBTRN02C, COTRN02C, COBIL00C | COTRN00C, COTRN01C, CBTRN03C, CBSTM03A, CBEXPORT |
| AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | KSDS | 50 | 17,0 | TCATBALF.jcl | CBTRN02C, CBACT04C | CBACT04C, PRTCATBL.jcl |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | KSDS | 50 | 16,0 | DISCGRP.jcl | (external load) | CBACT04C |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | KSDS | 60 | 2,0 | TRANTYPE.jcl | (external load) | CBTRN03C |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | KSDS | 60 | 6,0 | TRANCATG.jcl | (external load) | CBTRN03C |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | KSDS | 80 | 8,0 | DUSRSECJ.jcl | COUSR01C, COUSR02C | COSGN00C, COUSR00C, COUSR02C, COUSR03C |

### Sequential/GDG Datasets

| Dataset | Format | Written By | Read By |
|---------|--------|-----------|---------|
| AWS.M2.CARDDEMO.DALYTRAN.PS | FB/350 | (external feed) | CBTRN01C, CBTRN02C |
| AWS.M2.CARDDEMO.DALYREJS(+n) | FB/430 | CBTRN02C | (manual review) |
| AWS.M2.CARDDEMO.TRANSACT.BKUP(+n) | FB/350 | TRANBKP.jcl (REPRO) | COMBTRAN.jcl |
| AWS.M2.CARDDEMO.TRANSACT.DALY(+n) | FB/350 | CREASTMT.JCL | (archive) |
| AWS.M2.CARDDEMO.SYSTRAN(+n) | F/350 | INTCALC.jcl (CBACT04C) | COMBTRAN.jcl |
| AWS.M2.CARDDEMO.TRANSACT.COMBINED(+n) | FB/350 | COMBTRAN.jcl | (reload) |
| AWS.M2.CARDDEMO.TRANREPT(+n) | VB | CBTRN03C | (print/archive) |
| AWS.M2.CARDDEMO.TCATBALF.BKUP(+n) | FB/50 | PRTCATBL.jcl | PRTCATBL.jcl (SORT) |
| AWS.M2.CARDDEMO.TCATBALF.REPT | FB/40 | PRTCATBL.jcl | (print) |

---

## 3. JCL-to-Program Mapping

| JCL Job | Program Executed | Input Datasets | Output Datasets |
|---------|-----------------|---------------|-----------------|
| READACCT.jcl | CBACT01C | ACCTDATA.VSAM.KSDS | ACCTDATA.PSCOMP, ACCTDATA.ARRYPS, ACCTDATA.VBPS |
| READCARD.jcl | CBACT02C | CARDDATA.VSAM.KSDS | SYSOUT (print) |
| READCUST.jcl | CBCUS01C | CUSTDATA.VSAM.KSDS | SYSOUT (print) |
| READXREF.jcl | CBACT03C | CARDXREF.VSAM.KSDS | SYSOUT (print) |
| POSTTRAN.jcl | CBTRN02C | DALYTRAN.PS, CARDXREF.KSDS, ACCTDATA.KSDS | TRANSACT.KSDS, DALYREJS(+1), TCATBALF.KSDS |
| INTCALC.jcl | CBACT04C | TCATBALF.KSDS, CARDXREF.KSDS, ACCTDATA.KSDS, DISCGRP.KSDS | SYSTRAN(+1), ACCTDATA.KSDS (update) |
| CREASTMT.JCL | CBSTM03A | CARDXREF.KSDS, CUSTDATA.KSDS, ACCTDATA.KSDS, TRANSACT.KSDS | STMTFILE, HTMLFILE |
| TRANREPT.jcl | CBTRN03C | TRANSACT.KSDS, CARDXREF.KSDS, TRANTYPE.KSDS, TRANCATG.KSDS | TRANREPT(+1) |
| CBEXPORT.jcl | CBEXPORT | CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA | EXPFILE.VSAM.KSDS |
| CBIMPORT.jcl | CBIMPORT | EXPFILE | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT |

---

## 4. End-to-End Batch Pipeline Flow

### Daily Processing Pipeline (Run sequence matters — each job depends on prior output)

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     DAILY BATCH PIPELINE FLOW                             │
└─────────────────────────────────────────────────────────────────────────┘

     External Feed
          │
          ▼
┌──────────────────┐
│   DALYTRAN.PS    │  (Daily transaction flat file from ATM/POS/Online)
│   FB LRECL=350   │
└────────┬─────────┘
         │
         ▼
┌──────────────────────────────────────────────────────────────────────┐
│  STEP 1: POSTTRAN.jcl  →  PGM=CBTRN02C                              │
│                                                                       │
│  INPUT:  DALYTRAN.PS, CARDXREF.KSDS, ACCTDATA.KSDS                   │
│  PROCESS: Validate card xref exists, validate account active,         │
│           post to transaction master, update category balances         │
│  OUTPUT: TRANSACT.VSAM.KSDS (append), TCATBALF.KSDS (update),        │
│          DALYREJS(+1) (rejects), ACCTDATA.KSDS (bal update)           │
└────────┬─────────────────────────────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────────────────────────────┐
│  STEP 2: INTCALC.jcl  →  PGM=CBACT04C  (PARM='YYYYMMDD00')         │
│                                                                       │
│  INPUT:  TCATBALF.KSDS, CARDXREF.KSDS (+AIX), ACCTDATA.KSDS,        │
│          DISCGRP.KSDS                                                 │
│  PROCESS: For each account category balance, look up disclosure       │
│           group interest rate; compute interest; generate system       │
│           transactions for interest/fees; update account balance       │
│  OUTPUT: SYSTRAN(+1), ACCTDATA.KSDS (balance updated)                │
└────────┬─────────────────────────────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────────────────────────────┐
│  STEP 3: CREASTMT.JCL  →  PGM=CBSTM03A  (calls CBSTM03B)           │
│                                                                       │
│  INPUT:  CARDXREF.KSDS, CUSTDATA.KSDS, ACCTDATA.KSDS,               │
│          TRANSACT.KSDS                                                │
│  PROCESS: For each customer/account, gather transactions for           │
│           statement period; format text and HTML output                 │
│  OUTPUT: STMTFILE (text statements), HTMLFILE (HTML statements)       │
└────────┬─────────────────────────────────────────────────────────────┘
         │
         ▼
┌──────────────────────────────────────────────────────────────────────┐
│  STEP 4: TRANREPT.jcl  →  PGM=CBTRN03C                              │
│                                                                       │
│  INPUT:  TRANSACT.KSDS, CARDXREF.KSDS, TRANTYPE.KSDS,               │
│          TRANCATG.KSDS, DATEPARM                                      │
│  PROCESS: Generate formatted daily transaction detail report,          │
│           join type/category descriptions, sort by account/date        │
│  OUTPUT: TRANREPT(+1) (print-ready report)                           │
└──────────────────────────────────────────────────────────────────────┘
```

### Periodic Maintenance Pipeline

```
┌─────────────────────────────────────────────────────────────────────────┐
│                   PERIODIC MAINTENANCE FLOW                               │
└─────────────────────────────────────────────────────────────────────────┘

  Weekly/Monthly:
  ┌──────────────┐      ┌──────────────┐      ┌──────────────┐
  │  TRANBKP.jcl │  →   │ COMBTRAN.jcl │  →   │ TRANFILE.jcl │
  │ Backup master│      │ Merge bkup + │      │ Reload clean │
  │ REPRO to GDG │      │ system trans  │      │ VSAM cluster │
  └──────────────┘      └──────────────┘      └──────────────┘

  On Demand:
  ┌──────────────┐      ┌──────────────┐
  │ CBEXPORT.jcl │  →   │ CBIMPORT.jcl │
  │ Full estate  │      │ Restore to   │
  │ to export    │      │ target env   │
  └──────────────┘      └──────────────┘
```

---

## 5. Shared Copybook Dependency Heat Map

Programs sharing the most copybooks (indicating tight coupling):

| Copybook | Used By (Count) | Programs |
|----------|----------------|----------|
| COCOM01Y | 21 | All CICS online programs |
| COTTL01Y | 17 | All CICS programs with screen titles |
| CSDAT01Y | 17 | All CICS programs needing date display |
| CSMSG01Y | 17 | All CICS programs with messages |
| CSUSR01Y | 17 | All CICS programs needing user context |
| DFHAID | 17 | All CICS programs (AID key detection) |
| DFHBMSCA | 17 | All CICS programs (BMS attributes) |
| CVCRD01Y | 6 | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN02C |
| CVACT01Y | 6 | CBACT01C, COACTVWC, CBACT04C, CBSTM03A, CBTRN02C, CBEXPORT |
| CVACT02Y | 6 | CBACT02C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBEXPORT |
| CVACT03Y | 6 | CBACT03C, COACTVWC, CBACT04C, CBSTM03A, CBTRN02C, CBTRN03C |
| CVTRA05Y | 5 | CBACT04C, CBTRN02C, CBTRN03C, COTRN02C, CORPT00C |
| CSSTRPFY | 4 | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC (string parsing) |
| CVCUS01Y | 4 | CBCUS01C, COACTVWC, COCRDSLC, CBEXPORT |

---

## 6. CICS File Definitions (from CBADMCDJ.jcl)

| CICS File Name | Physical Dataset | Access Mode |
|---------------|-----------------|-------------|
| ACCTDAT | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | Read/Update |
| CARDDAT | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | Read/Update |
| CUSTDAT | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | Read |
| CCXREF | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | Read |
| CXACAIX | AWS.M2.CARDDEMO.CARDXREF.VSAM.AIX.PATH | Read (AIX) |
| TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | Read/Write |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | Read/Update/Delete |
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | Read/Update |
| TRANTYPE | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | Read |
| TRANCATG | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | Read |
| DISCGRP | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | Read |

---

*Generated: 2026-06-19 | Source: infosys-training/uc-legacy-modernization-cobol-to-java*
