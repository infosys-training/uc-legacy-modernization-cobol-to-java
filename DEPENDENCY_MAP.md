# Dependency Map — CardDemo COBOL Estate

> Inter-program call graph, dataset lineage, and end-to-end batch pipeline flow.

---

## 1. Program Call Graph

### 1.1 CALL Dependencies (Direct Program Invocations)

```
CBSTM03A ──CALL──► CBSTM03B   (File I/O subroutine — open/read/close all data files)
                   CBSTM03B is called 13 times for different file operations

CBSTM03A ──CALL──► CEE3ABD    (LE abnormal termination)

CORPT00C ──CALL──► CSUTLDTC   (Date conversion utility)
COTRN02C ──CALL──► CSUTLDTC   (Date conversion utility)

CSUTLDTC ──CALL──► CEEDAYS    (LE Lilian date conversion)

COBSWAIT ──CALL──► MVSWAIT    (System wait service)
```

### 1.2 CICS Transfer-of-Control (XCTL) — Online Navigation

```
COSGN00C (Sign-on)
  ├──XCTL──► COMEN01C (Main Menu)
  └──XCTL──► COADM01C (Admin Menu, if admin user)

COMEN01C (Main Menu)
  ├──XCTL──► COACTVWC (Account View)
  ├──XCTL──► COCRDLIC (Card List)
  ├──XCTL──► COTRN00C (Transaction List)
  ├──XCTL──► CORPT00C (Reports)
  ├──XCTL──► COBIL00C (Bill Payment)
  └──XCTL──► COSGN00C (Sign-off → back to sign-on)

COADM01C (Admin Menu)
  ├──XCTL──► COUSR00C (User List)
  ├──XCTL──► COTRTLIC (Tran Type List — DB2 sub-app)
  └──XCTL──► COMEN01C (Return to Main Menu)

COACTVWC (Account View)
  └──XCTL──► COACTUPC (Account Update)

COCRDLIC (Card List)
  ├──XCTL──► COCRDSLC (Card Detail)
  └──XCTL──► COCRDUPC (Card Update)

COTRN00C (Transaction List)
  ├──XCTL──► COTRN01C (Transaction View)
  └──XCTL──► COTRN02C (Transaction Add)

COUSR00C (User List)
  ├──XCTL──► COUSR01C (User Add)
  ├──XCTL──► COUSR02C (User Update)
  └──XCTL──► COUSR03C (User Delete)

COPAUS0C (Auth List)
  └──XCTL──► COPAUS1C (Auth Detail)
      └──LINK──► COPAUS2C (DB2 Fraud Update Module)
```

### 1.3 MQ Integration Call Graph

```
COPAUA0C (Authorization Processor)
  ├──CALL──► MQOPEN   (Open request queue)
  ├──CALL──► MQGET    (Get auth request message)
  ├──CALL──► MQPUT1   (Put auth reply message)
  └──CALL──► MQCLOSE  (Close request queue)

COACCT01 (Account Inquiry via MQ)
  ├──CALL──► MQOPEN   (Open input/output/error queues)
  ├──CALL──► MQGET    (Get inquiry request)
  ├──CALL──► MQPUT    (Put reply / error messages)
  └──CALL──► MQCLOSE  (Close all queues)

CODATE01 (Date Inquiry via MQ)
  ├──CALL──► MQOPEN   (Open input/output/error queues)
  ├──CALL──► MQGET    (Get date request)
  ├──CALL──► MQPUT    (Put reply / error messages)
  └──CALL──► MQCLOSE  (Close all queues)
```

### 1.4 IMS DL/I Call Graph

```
CBPAUP0C (Authorization Purge)
  └── IMS DL/I calls: GN (Get Next), GNP (Get Next in Parent)
      Traverses: AUTH-SUMMARY → AUTH-DETAIL segments

PAUDBLOD (IMS DB Load)
  ├── READ INFILE1, INFILE2  (flat file input)
  └── IMS DL/I calls: ISRT (Insert), GU (Get Unique)
      Loads: AUTH-SUMMARY + AUTH-DETAIL segments

PAUDBUNL (IMS DB Unload)
  ├── IMS DL/I calls: GN, GNP  (sequential read)
  └── WRITE OPFILE1, OPFILE2  (flat file output)

DBUNLDGS (IMS to GSAM Unload)
  └── IMS DL/I calls: GN, GNP, ISRT (to GSAM)
```

### 1.5 DB2 Access Map

```
COTRTLIC ──EXEC SQL──► TRNTYPE table (SELECT with cursor for browsing)
COTRTUPC ──EXEC SQL──► TRNTYPE table (SELECT, INSERT, UPDATE, DELETE)
           ──EXEC SQL──► TRNCATG table (SELECT, INSERT, UPDATE, DELETE)
COBTUPDT ──EXEC SQL──► TRNTYPE table (INSERT, UPDATE, DELETE from flat file)
COPAUS2C ──EXEC SQL──► AUTHFRDS table (SELECT, UPDATE — fraud flagging)
```

---

## 2. Dataset Lineage

### 2.1 VSAM File → Program → JCL Mapping

| Dataset (Logical) | VSAM DSN | Programs That READ | Programs That WRITE/UPDATE | JCL That Defines |
|---|---|---|---|---|
| **ACCTFILE** (Account Data) | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | CBACT01C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBSTM03A/B, COACTVWC, COACTUPC, COBIL00C, COPAUA0C, COPAUS0C, COACCT01 | CBACT04C (I-O), CBTRN02C (I-O), COACTUPC (REWRITE), COBIL00C (REWRITE) | ACCTFILE.jcl |
| **CARDFILE** (Card Data) | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC | COCRDUPC (REWRITE) | CARDFILE.jcl |
| **CUSTFILE** (Customer Data) | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CBCUS01C, CBEXPORT, CBTRN01C, CBSTM03A/B, COACTVWC, COACTUPC, COCRDSLC, COPAUA0C, COPAUS0C | COACTUPC (REWRITE) | CUSTFILE.jcl |
| **XREFFILE** (Card Cross-Ref) | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A/B, COACTVWC, COACTUPC, COTRN02C, COBIL00C, COPAUA0C | *(read-only)* | XREFFILE.jcl |
| **TRANSACT** (Transaction Master) | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | CBEXPORT, CBSTM03A/B, COTRN00C, COTRN01C, CBTRN03C | CBACT04C (WRITE), CBTRN02C (WRITE), COTRN02C (WRITE), COBIL00C (WRITE) | TRANFILE.jcl |
| **TCATBALF** (Category Balance) | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | CBACT04C | CBACT04C (I-O), CBTRN02C (I-O) | TCATBALF.jcl |
| **DISCGRP** (Disclosure Group) | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | CBACT04C | *(read-only)* | DISCGRP.jcl |
| **USRSEC** (User Security) | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | COSGN00C, COUSR00C, COUSR02C, COUSR03C | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | DUSRSECJ.jcl |
| **TRANTYPE** | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | CBTRN03C | *(DB2-managed via COBTUPDT)* | TRANTYPE.jcl |
| **TRANCATG** | AWS.M2.CARDDEMO.TRANCATG.PS | CBTRN03C | *(reference data)* | TRANCATG.jcl |

### 2.2 Flat/Sequential Files

| File | Producer | Consumer | JCL |
|------|----------|----------|-----|
| DALYTRAN.PS (Daily Transactions) | External feed | CBTRN01C, CBTRN02C | POSTTRAN.jcl |
| DALYREJS GDG (Daily Rejects) | CBTRN02C | *(end-of-pipe)* | DALYREJS.jcl (GDG def), POSTTRAN.jcl |
| TRANREPT GDG (Transaction Report) | CBTRN03C | *(print output)* | TRANREPT.jcl |
| SYSTRAN GDG (System Transactions) | CBACT04C | COMBTRAN.jcl (merge) | INTCALC.jcl |
| TRANSACT.BKUP GDG | TRANBKP.jcl | COMBTRAN.jcl (merge input) | DEFGDGB.jcl |
| STMTFILE / HTMLFILE | CBSTM03A | *(print/email output)* | CREASTMT.JCL |
| EXPFILE | CBEXPORT | CBIMPORT | CBEXPORT.jcl |
| DATEPARM | *(control card)* | CBTRN03C | TRANREPT.jcl |
| TCATBALF.REPT | PRTCATBL.jcl (SORT) | *(print output)* | PRTCATBL.jcl |
| INFILE1/INFILE2 | *(external)* | PAUDBLOD | LOADPADB.JCL |
| OPFILE1/OPFILE2 | PAUDBUNL | *(archive)* | UNLDPADB.JCL |

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Transaction Processing Pipeline

```
┌──────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH PROCESSING PIPELINE                    │
└──────────────────────────────────────────────────────────────────────┘

Phase 1: FILE SETUP
  ┌─────────────┐   ┌─────────────┐   ┌──────────────┐
  │ CLOSEFIL.jcl│──►│  Close CICS │──►│ Files avail  │
  │ (SDSF)      │   │  files      │   │ for batch    │
  └─────────────┘   └─────────────┘   └──────────────┘
                                             │
Phase 2: TRANSACTION POSTING                 ▼
  ┌──────────────────────────────────────────────────────────┐
  │  POSTTRAN.jcl  →  PGM=CBTRN02C                          │
  │                                                          │
  │  INPUT:                    OUTPUT:                       │
  │  ├─ DALYTRAN.PS           ├─ TRANFILE (VSAM update)     │
  │  ├─ XREFFILE (VSAM)      ├─ ACCTFILE (VSAM update)     │
  │  └─ ACCTFILE (VSAM)      ├─ TCATBALF (VSAM update)     │
  │                           └─ DALYREJS (GDG — rejects)   │
  └──────────────────────────────────────────────────────────┘
                         │
Phase 3: INTEREST CALCULATION
                         ▼
  ┌──────────────────────────────────────────────────────────┐
  │  INTCALC.jcl  →  PGM=CBACT04C                           │
  │                                                          │
  │  INPUT:                    OUTPUT:                       │
  │  ├─ TCATBALF (VSAM)      ├─ ACCTFILE (VSAM update)     │
  │  ├─ XREFFILE (VSAM)      └─ SYSTRAN (GDG — new txns)   │
  │  └─ DISCGRP (VSAM)                                      │
  └──────────────────────────────────────────────────────────┘
                         │
Phase 4: MERGE TRANSACTIONS
                         ▼
  ┌──────────────────────────────────────────────────────────┐
  │  COMBTRAN.jcl  →  SORT + IDCAMS REPRO                   │
  │                                                          │
  │  INPUT:                    OUTPUT:                       │
  │  ├─ TRANSACT.BKUP (GDG)  └─ TRANSACT.VSAM.KSDS         │
  │  └─ SYSTRAN (GDG)            (updated master)           │
  └──────────────────────────────────────────────────────────┘
                         │
Phase 5: REPORTING                                            
                         ▼
  ┌──────────────────────────────────────────────────────────┐
  │  TRANREPT.jcl  →  PGM=CBTRN03C                          │
  │                                                          │
  │  INPUT:                    OUTPUT:                       │
  │  ├─ TRANFILE (VSAM)       └─ TRANREPT (GDG — report)    │
  │  ├─ CARDXREF (VSAM)                                     │
  │  ├─ TRANTYPE (VSAM)                                     │
  │  ├─ TRANCATG (VSAM)                                     │
  │  └─ DATEPARM (control)                                  │
  └──────────────────────────────────────────────────────────┘
                         │
Phase 6: STATEMENT GENERATION
                         ▼
  ┌──────────────────────────────────────────────────────────┐
  │  CREASTMT.JCL  →  PGM=CBSTM03A                          │
  │                                                          │
  │  INPUT:                    OUTPUT:                       │
  │  ├─ TRNXFILE (TRANSACT)   ├─ STMTFILE (text statement)  │
  │  ├─ XREFFILE (VSAM)      └─ HTMLFILE (HTML statement)   │
  │  ├─ CUSTFILE (VSAM)                                     │
  │  └─ ACCTFILE (VSAM)                                     │
  └──────────────────────────────────────────────────────────┘
                         │
Phase 7: BACKUP & REOPEN
                         ▼
  ┌─────────────┐   ┌──────────────┐   ┌──────────────┐
  │ TRANBKP.jcl │──►│ Backup to    │──►│ OPENFIL.jcl  │
  │ (REPRO)     │   │ TRANSACT.BKUP│   │ (SDSF open)  │
  └─────────────┘   └──────────────┘   └──────────────┘
```

### 3.2 Data Migration Pipeline

```
┌──────────────────────────────────────────────────────────────┐
│                   EXPORT / IMPORT PIPELINE                     │
└──────────────────────────────────────────────────────────────┘

  CBEXPORT.jcl → PGM=CBEXPORT
  ┌──────────────────────────────┐        ┌──────────────────┐
  │ Read from VSAM:              │        │ EXPFILE          │
  │ ├─ CUSTFILE                  │───────►│ (multi-record    │
  │ ├─ ACCTFILE                  │        │  export file)    │
  │ ├─ XREFFILE                  │        └────────┬─────────┘
  │ ├─ TRANSACT                  │                 │
  │ └─ CARDFILE                  │                 │
  └──────────────────────────────┘                 ▼

  CBIMPORT.jcl → PGM=CBIMPORT
  ┌──────────────────────────────┐        ┌──────────────────┐
  │ Read EXPFILE                 │        │ Normalized files:│
  │ (split by record type)       │───────►│ ├─ CUSTOUT       │
  │                              │        │ ├─ ACCTOUT       │
  │                              │        │ ├─ XREFOUT       │
  └──────────────────────────────┘        │ ├─ TRNXOUT       │
                                          │ ├─ CARDOUT       │
                                          │ └─ ERROUT        │
                                          └──────────────────┘
```

### 3.3 IMS Authorization Pipeline

```
┌──────────────────────────────────────────────────────────────┐
│              IMS AUTHORIZATION BATCH PIPELINE                  │
└──────────────────────────────────────────────────────────────┘

  LOADPADB.JCL → PGM=PAUDBLOD
  ┌──────────────┐        ┌──────────────┐
  │ INFILE1      │───────►│ IMS Auth DB  │
  │ INFILE2      │  ISRT  │ (PAUTBPCB)   │
  │ (flat files) │        └──────┬───────┘
  └──────────────┘               │
                                 │  Online Processing
                                 ▼
  COPAUA0C (CICS+MQ) reads from IMS Auth DB
  ├─ MQGET request from queue
  ├─ Validate against CARDXREF, ACCTDATA, CUSTDATA
  ├─ Read auth summary/detail from IMS
  ├─ Make approve/decline decision
  └─ MQPUT1 reply to queue

  CBPAUP0J.jcl → PGM=CBPAUP0C
  ┌──────────────┐        ┌──────────────┐
  │ IMS Auth DB  │───────►│ Purged       │
  │ (read auth   │  DLET  │ expired      │
  │  records)    │        │ records      │
  └──────────────┘        └──────────────┘

  UNLDPADB.JCL → PGM=PAUDBUNL
  ┌──────────────┐        ┌──────────────┐
  │ IMS Auth DB  │───────►│ OPFILE1      │
  │ (GN/GNP)     │        │ OPFILE2      │
  └──────────────┘        │ (flat files) │
                          └──────────────┘
```

---

## 4. Shared Copybook Dependency Matrix

Shows which copybooks are used by which programs (top 15 most-shared copybooks):

| Copybook | # Users | Programs |
|----------|---------|----------|
| **COCOM01Y** | 20+ | All CICS programs + COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC |
| **COTTL01Y** | 18 | All CICS programs (screen titles) |
| **CSDAT01Y** | 18 | All CICS programs (date/time) |
| **CSMSG01Y** | 17 | All CICS programs (common messages) |
| **DFHAID** | 17 | All CICS programs (AID key definitions) |
| **DFHBMSCA** | 17 | All CICS programs (BMS character attributes) |
| **CSUSR01Y** | 10 | COSGN00C, COMEN01C, COADM01C, COUSR00-03C, COCRDLIC, COTRTLIC, COTRTUPC |
| **CVACT01Y** | 10 | CBACT01C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBSTM03A, COACTVWC, COACTUPC, COBIL00C, COPAUA0C |
| **CVACT03Y** | 9 | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, COTRN02C, COPAUS0C |
| **CVCUS01Y** | 8 | CBCUS01C, CBEXPORT, CBTRN01C, COACTVWC, COACTUPC, COCRDSLC, COPAUA0C, COPAUS0C |
| **CVTRA05Y** | 8 | CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C |
| **CVCRD01Y** | 7 | COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COTRTLIC, COTRTUPC, COPAUS0C |
| **CSMSG02Y** | 6 | COACTUPC, COCRDSLC, COCRDUPC, COPAUS0C, COPAUS1C, COTRTUPC |
| **CSSTRPFY** | 5 | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC |
| **CIPAUSMY** | 5 | CBPAUP0C, COPAUA0C, COPAUS0C, PAUDBLOD, PAUDBUNL |

---

## 5. CICS File-to-DDname Mapping

(From CBADMCDJ.jcl CSD definitions)

| CICS File Name | DDname | VSAM Dataset | Programs |
|----------------|--------|--------------|----------|
| ACCTDAT | ACCTFILE | ACCTDATA.VSAM.KSDS | Account view/update/batch |
| CARDDAT | CARDFILE | CARDDATA.VSAM.KSDS | Card list/detail/update |
| CUSTDAT | CUSTFILE | CUSTDATA.VSAM.KSDS | Customer data access |
| CCXREF | XREFFILE | CARDXREF.VSAM.KSDS | Cross-reference lookups |
| CXACAIX | XREFFIL1 | CARDXREF.VSAM.AIX.PATH | Xref alternate index (by acct) |
| TRANSACT | TRANFILE | TRANSACT.VSAM.KSDS | Transaction read/write |
| USRSEC | USRSECFIL | USRSEC.VSAM.KSDS | User security/authentication |
| TCATBAL | TCATBALF | TCATBALF.VSAM.KSDS | Category balance |
| DISCGRP | DISCGRP | DISCGRP.VSAM.KSDS | Disclosure group reference |
