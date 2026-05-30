# Dependency Map — CardDemo COBOL Estate

> Call graph, dataset lineage, and end-to-end batch pipeline flow.

---

## 1. Inter-Program Call Graph

### 1.1 CALL Dependencies (Direct Program Calls)

```
CBACT01C ──CALL──► COBDATFT        (assembler date formatter)
CBACT02C ──CALL──► CEE3ABD         (LE abnormal termination)
CBACT03C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBACT04C ──CALL──► (no external CALL — self-contained interest calc)
CBSTM03A ──CALL──► CBSTM03B        (I/O submodule for file operations)
CBTRN01C ──CALL──► (no external CALL)
CBTRN02C ──CALL──► (no external CALL)
CBTRN03C ──CALL──► (no external CALL)
COBSWAIT ──CALL──► MVSWAIT         (assembler wait routine)
CSUTLDTC ──CALL──► CEEDAYS         (LE date conversion)
CBEXPORT ──CALL──► (no external CALL)
CBIMPORT ──CALL──► (no external CALL)
```

### 1.2 CICS XCTL (Transfer Control) Dependencies

```
COSGN00C ──XCTL──► COADM01C        (admin user → admin menu)
COSGN00C ──XCTL──► COMEN01C        (regular user → main menu)

COADM01C ──XCTL──► COUSR00C        (User List)
COADM01C ──XCTL──► COUSR01C        (User Add)
COADM01C ──XCTL──► COUSR02C        (User Update)
COADM01C ──XCTL──► COUSR03C        (User Delete)
COADM01C ──XCTL──► COTRTLIC        (Transaction Type List — DB2)
COADM01C ──XCTL──► COTRTUPC        (Transaction Type Update — DB2)

COMEN01C ──XCTL──► COACTVWC        (Account View)
COMEN01C ──XCTL──► COACTUPC        (Account Update)
COMEN01C ──XCTL──► COCRDLIC        (Credit Card List)
COMEN01C ──XCTL──► COCRDSLC        (Credit Card View)
COMEN01C ──XCTL──► COCRDUPC        (Credit Card Update)
COMEN01C ──XCTL──► COTRN00C        (Transaction List)
COMEN01C ──XCTL──► COTRN01C        (Transaction View)
COMEN01C ──XCTL──► COTRN02C        (Transaction Add)
COMEN01C ──XCTL──► CORPT00C        (Transaction Reports)
COMEN01C ──XCTL──► COBIL00C        (Bill Payment)
COMEN01C ──XCTL──► COPAUS0C        (Pending Authorization View)

COCRDLIC ──XCTL──► COCRDSLC        (Card List → Card Detail)
COCRDLIC ──XCTL──► COCRDUPC        (Card List → Card Update)

COUSR00C ──XCTL──► COUSR02C        (User List → User Update)
COUSR00C ──XCTL──► COUSR03C        (User List → User Delete)

COTRN00C ──XCTL──► COTRN01C        (Transaction List → Transaction View)

COPAUS0C ──LINK──► COPAUS1C        (Auth Summary → Auth Detail)
```

### 1.3 CICS LINK Dependencies

```
COPAUS0C ──LINK──► COPAUS1C        (Summary view links to detail view)
COPAUS1C ──LINK──► COPAUS2C        (Detail view links to fraud marking)
```

### 1.4 MQ Call Dependencies

```
COPAUA0C ──CALL──► MQOPEN, MQGET, MQPUT1   (Authorization decision via MQ)
COACCT01 ──CALL──► MQOPEN, MQGET, MQPUT, MQCLOSE (Account inquiry via MQ)
CODATE01 ──CALL──► MQOPEN, MQGET, MQPUT, MQCLOSE (Date inquiry via MQ)
```

### 1.5 IMS DL/I Call Dependencies

```
CBPAUP0C ──DLI──► GN, GNP, DLET    (Batch: expired auth purge)
COPAUA0C ──DLI──► GU, SCHD, TERM   (CICS: auth decision — IMS lookup)
COPAUS0C ──DLI──► GU, GNP, SCHD, TERM (CICS: auth summary browse)
COPAUS1C ──DLI──► GU, GNP, REPL    (CICS: auth detail with update)
PAUDBLOD ──CALL──► CBLTDLI (ISRT, GU) (Batch: IMS database load)
PAUDBUNL ──CALL──► CBLTDLI (GN, GNP)  (Batch: IMS database unload)
DBUNLDGS ──CALL──► CBLTDLI (GN, GNP, ISRT) (Batch: GSAM unload)
```

### 1.6 Full Navigation Tree (Online System)

```
COSGN00C (Sign-On)
├── [Admin] COADM01C (Admin Menu)
│   ├── COUSR00C (User List)
│   │   ├── COUSR02C (User Update)
│   │   └── COUSR03C (User Delete)
│   ├── COUSR01C (User Add)
│   ├── COTRTLIC (Tran Type List — DB2)
│   └── COTRTUPC (Tran Type Update — DB2)
│
└── [User] COMEN01C (Main Menu)
    ├── COACTVWC (Account View)
    ├── COACTUPC (Account Update)
    ├── COCRDLIC (Credit Card List)
    │   ├── COCRDSLC (Card View)
    │   └── COCRDUPC (Card Update)
    ├── COTRN00C (Transaction List)
    │   └── COTRN01C (Transaction View)
    ├── COTRN02C (Transaction Add)
    ├── CORPT00C (Report Request) ──submit──► INTRDRJ1/J2 (batch)
    ├── COBIL00C (Bill Payment)
    └── COPAUS0C (Auth Summary — IMS)
        └── COPAUS1C (Auth Detail)
            └── COPAUS2C (Fraud Mark — DB2)
```

---

## 2. Dataset Lineage

### 2.1 VSAM KSDS Files — Read/Write by Program

| Dataset (Logical Name) | Physical DSN Pattern | Defined By | Written By | Read By |
|------------------------|---------------------|-----------|-----------|---------|
| ACCTFILE / ACCTDAT | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | ACCTFILE.jcl | CBACT04C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE), CBIMPORT (WRITE) | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBSTM03A, CBEXPORT, COACTUPC, COACTVWC, COCRDSLC, COTRN02C, COBIL00C, COPAUA0C, COACCT01 |
| CARDFILE / CARDDAT | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CARDFILE.jcl | COCRDUPC (WRITE/REWRITE), CBIMPORT (WRITE) | CBACT02C, CBTRN01C, CBEXPORT, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COPAUS0C |
| CUSTFILE / CUSTDAT | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CUSTFILE.jcl | CBIMPORT (WRITE) | CBCUS01C, CBTRN01C, CBSTM03A, CBEXPORT, COACTUPC, COACTVWC, COCRDSLC, COPAUA0C |
| XREFFILE / CARDXREF / CXACAIX | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS (+AIX) | XREFFILE.jcl | CBIMPORT (WRITE) | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, CBEXPORT, COACTUPC, COACTVWC, COCRDSLC, COTRN02C, COBIL00C, COPAUA0C |
| TRANFILE / TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS (+AIX) | TRANFILE.jcl | CBTRN02C (WRITE), COTRN02C (WRITE), COBIL00C (WRITE), CBIMPORT (WRITE) | CBTRN03C, CBSTM03A, CBEXPORT, COTRN00C, COTRN01C, COBIL00C |
| DALYTRAN | AWS.M2.CARDDEMO.DALYTRAN.PS | *(flat file — loaded externally)* | *(external feed)* | CBTRN01C, CBTRN02C |
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | TCATBALF.jcl | CBTRN02C (WRITE) | CBACT04C |
| DISCGRP | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | DISCGRP.jcl | *(static reference data)* | CBACT04C |
| TRANTYPE | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | TRANTYPE.jcl | *(static reference data)* | CBTRN03C |
| TRANCATG | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | TRANCATG.jcl | *(static reference data)* | CBTRN03C |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | DUSRSECJ.jcl | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | COSGN00C, COUSR00C, COUSR02C, COUSR03C |
| DALYREJS | AWS.M2.CARDDEMO.DALYREJS.GDG | DALYREJS.jcl | CBTRN02C (WRITE) | *(manual review)* |
| REPTFILE | AWS.M2.CARDDEMO.RPTFILE.GDG | REPTFILE.jcl | CBTRN03C (WRITE) | *(output report)* |

### 2.2 DB2 Tables (Sub-App: `app-transaction-type-db2`)

| Table | Written By | Read By |
|-------|-----------|---------|
| CARDDEMO.TRANSACTION_TYPE | COBTUPDT (INSERT/UPDATE/DELETE), COTRTUPC (UPDATE/DELETE) | COTRTLIC (SELECT with cursor), COTRTUPC (SELECT) |
| CARDDEMO.TRANSACTION_CATEGORY | COTRTUPC (UPDATE/DELETE) | COTRTUPC (SELECT) |

### 2.3 IMS Databases (Sub-App: `app-authorization-ims-db2-mq`)

| Database | Segment | Written By | Read By |
|----------|---------|-----------|---------|
| DBPAUTP0 (PAUT) | Root: CIPAUSMY (Summary) | PAUDBLOD (ISRT), COPAUS1C (REPL) | CBPAUP0C (GN), COPAUA0C (GU), COPAUS0C (GU/GNP), COPAUS1C (GU/GNP), PAUDBUNL (GN/GNP), DBUNLDGS (GN/GNP) |
| DBPAUTP0 (PAUT) | Child: CIPAUDTY (Detail) | PAUDBLOD (ISRT), COPAUS2C (SQL UPDATE) | CBPAUP0C (GNP/DLET), COPAUA0C (GU), COPAUS0C (GNP), COPAUS1C (GNP), PAUDBUNL (GNP), DBUNLDGS (GNP) |

### 2.4 MQ Queues (Sub-Apps)

| Queue | Producer | Consumer |
|-------|---------|----------|
| Auth Request Queue | *(external system)* | COPAUA0C (MQGET), COACCT01 (MQGET), CODATE01 (MQGET) |
| Auth Reply Queue | COPAUA0C (MQPUT1), COACCT01 (MQPUT), CODATE01 (MQPUT) | *(external system)* |

### 2.5 Output Files

| File | Produced By | Format |
|------|-----------|--------|
| Account report (fixed/array/VB) | CBACT01C | Fixed-length, array, variable-length records |
| Statement file (STMT-FILE) | CBSTM03A | Text report |
| Statement file (HTML-FILE) | CBSTM03A | HTML format |
| Transaction report | CBTRN03C → REPTFILE | Formatted text report with headers/totals |
| Export file | CBEXPORT | Sequential 500-byte multi-record |
| Error file | CBIMPORT | Records that failed validation |

---

## 3. JCL Job → Dataset → Program Lineage

### 3.1 Data Setup Pipeline

```
ACCTFILE.jcl    → Defines ACCTDATA.VSAM.KSDS    (loads from ACCTDATA.PS)
CARDFILE.jcl    → Defines CARDDATA.VSAM.KSDS    (loads from CARDDATA.PS) + AIX by ACCTID
CUSTFILE.jcl    → Defines CUSTDATA.VSAM.KSDS    (loads from CUSTDATA.PS)
XREFFILE.jcl    → Defines CARDXREF.VSAM.KSDS    (loads from CARDXREF.PS) + AIX by ACCTID
TRANFILE.jcl    → Defines TRANSACT.VSAM.KSDS    (loads from DALYTRAN.PS) + AIX
TCATBALF.jcl    → Defines TCATBALF.VSAM.KSDS    (loads from TCATBALF.PS)
DISCGRP.jcl     → Defines DISCGRP.VSAM.KSDS     (loads from DISCGRP.PS)
TRANTYPE.jcl    → Defines TRANTYPE.VSAM.KSDS    (loads from TRANTYPE.PS)
TRANCATG.jcl    → Defines TRANCATG.VSAM.KSDS    (loads from TRANCATG.PS)
DUSRSECJ.jcl    → Defines USRSEC.VSAM.KSDS      (loads from USRSEC.PS)
DEFGDGB.jcl     → Defines GDG bases for batch outputs
DALYREJS.jcl    → Defines GDG base for daily rejects
REPTFILE.jcl    → Defines GDG base for reports
```

### 3.2 End-to-End Batch Processing Pipeline

The daily batch cycle processes transactions through a multi-step pipeline:

```
                    ┌──────────────────────────────────────────────┐
                    │           DAILY BATCH PIPELINE                │
                    └──────────────────────────────────────────────┘

  [External Feed]
       │
       ▼
  DALYTRAN.PS (Daily Transaction flat file)
       │
       ▼
  ┌─────────────────────────────────────────────────────────────┐
  │  STEP 1: POSTTRAN.jcl → CBTRN02C                           │
  │  • Reads DALYTRAN.PS (daily transactions)                   │
  │  • Validates each transaction against CARDXREF and ACCTDAT  │
  │  • Posts valid transactions → TRANSACT.VSAM.KSDS            │
  │  • Writes rejected records → DALYREJS.GDG                   │
  │  • Updates TCATBALF (category balances)                     │
  │  • Updates ACCTDAT (account balances)                       │
  └──────────────┬──────────────────────────────┬───────────────┘
                 │                              │
                 ▼                              ▼
        TRANSACT.VSAM.KSDS            DALYREJS.GDG (rejected)
                 │
       ┌─────────┴──────────┐
       │                    │
       ▼                    ▼
  ┌──────────────┐   ┌──────────────────────────────────────────┐
  │ STEP 2:      │   │  STEP 2a: INTCALC.jcl → CBACT04C        │
  │ TRANREPT.jcl │   │  • Reads TCATBALF (category balances)    │
  │ → SORT       │   │  • Reads CARDXREF, DISCGRP (int. rates)  │
  │ → CBTRN03C   │   │  • Computes interest and fees            │
  │              │   │  • Updates ACCTDAT (new balances)         │
  │ Produces:    │   │  • Writes new transactions → TRANSACT    │
  │ REPTFILE.GDG │   └──────────────────────────────────────────┘
  │ (Daily Rpt)  │
  └──────────────┘
       │
       ▼
  ┌──────────────────────────────────────────────────────────────┐
  │  STEP 3: CREASTMT.JCL → SORT → CBSTM03A                    │
  │  • Sorts transactions by card number                         │
  │  • Reads CARDXREF → CUSTDAT → ACCTDAT → TRANSACT            │
  │  • CBSTM03A calls CBSTM03B for I/O                          │
  │  • Generates text + HTML statements per customer             │
  └──────────────────────────────────────────────────────────────┘
       │
       ▼
  Statement Files (Text + HTML)

  ┌──────────────────────────────────────────────────────────────┐
  │  STEP 4: TRANBKP.jcl (Periodic)                             │
  │  • REPRO TRANSACT to backup GDG                              │
  │  • Optionally delete transaction master                      │
  └──────────────────────────────────────────────────────────────┘

  ┌──────────────────────────────────────────────────────────────┐
  │  AUXILIARY: COMBTRAN.jcl                                     │
  │  • SORT + IDCAMS REPRO to merge transaction files            │
  └──────────────────────────────────────────────────────────────┘
```

### 3.3 Data Migration Pipeline

```
  ┌──────────────────────────────────────────────────────────┐
  │  EXPORT: CBEXPORT.jcl → CBEXPORT                         │
  │  • Reads all 5 VSAM files (CUST, ACCT, XREF, TRAN, CARD)│
  │  • Writes unified EXPORT-OUTPUT (500-byte records)        │
  │  • Tags each record by type (C/A/T/X/D)                   │
  └──────────────────────┬───────────────────────────────────┘
                         │
                         ▼
                    EXPORT.DATA.PS
                         │
                         ▼
  ┌──────────────────────────────────────────────────────────┐
  │  IMPORT: CBIMPORT.jcl → CBIMPORT                         │
  │  • Reads EXPORT-INPUT (500-byte records)                  │
  │  • Splits by record type into 5 output files              │
  │  • Validates and writes errors to ERROR-OUTPUT            │
  └──────────────────────────────────────────────────────────┘
```

### 3.4 Authorization Sub-App Pipeline

```
  ┌──────────────────────────────────────────────────────────┐
  │  IMS DB LOAD: LOADPADB.JCL → PAUDBLOD                    │
  │  • Reads flat input files (INFILE1, INFILE2)              │
  │  • Inserts root + child segments into IMS PAUT DB         │
  └──────────────────────────────────────────────────────────┘

  [Runtime: MQ-triggered auth flow]
  External Auth Request → MQ Queue → COPAUA0C
       │
       ├── CICS READ: CARDXREF, ACCTDAT, CUSTDAT
       ├── IMS GU: PAUT DB (auth summary)
       └── MQ PUT: Auth Response → Reply Queue

  ┌──────────────────────────────────────────────────────────┐
  │  PURGE: CBPAUP0J.jcl → CBPAUP0C                          │
  │  • Scans IMS PAUT DB (GN/GNP)                            │
  │  • Deletes expired auth detail segments (DLET)            │
  └──────────────────────────────────────────────────────────┘

  ┌──────────────────────────────────────────────────────────┐
  │  UNLOAD: UNLDPADB.JCL → PAUDBUNL                         │
  │  • Reads IMS PAUT DB (GN/GNP)                            │
  │  • Writes flat files (OPFIL1, OPFIL2)                     │
  └──────────────────────────────────────────────────────────┘
```

---

## 4. Copybook Dependency Matrix

### Programs → Copybooks Used

| Copybook | Used By (Program Count) | Programs |
|----------|------------------------|----------|
| COCOM01Y | 18 | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C–03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC |
| COTTL01Y | 17 | All CICS online programs |
| CSDAT01Y | 17 | All CICS online programs |
| CSMSG01Y | 17 | All CICS online programs |
| DFHAID | 16 | All CICS online programs |
| DFHBMSCA | 16 | All CICS online programs |
| CSUSR01Y | 10 | COACTUPC, COADM01C, COCRDLIC, COCRDSLC, COCRDUPC, COSGN00C, COUSR00C–03C, COTRTLIC, COTRTUPC |
| CVACT01Y | 10 | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COTRN02C, COPAUA0C, COPAUS0C, COACCT01 |
| CVACT03Y | 10 | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COTRN02C, COPAUA0C, COPAUS0C |
| CVTRA05Y | 9 | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C |
| CVCUS01Y | 8 | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| CVACT02Y | 7 | CBACT02C, CBEXPORT, CBIMPORT, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C, COTRTLIC |
| CVCRD01Y | 6 | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC |
| CIPAUSMY | 6 | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
| CIPAUDTY | 6 | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
| CSSTRPFY | 6 | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC |
