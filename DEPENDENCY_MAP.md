# Dependency Map — CardDemo COBOL Estate

> Call graph, dataset lineage, and end-to-end batch pipeline flows.

---

## 1. Program Call Graph

### 1.1 Direct CALL Dependencies

```
CBACT01C ──CALL──► COBDATFT (Assembler — date formatting)
CBACT01C ──CALL──► CEE3ABD  (LE — abnormal termination)

CBSTM03A ──CALL──► CBSTM03B (COBOL subroutine — file I/O for statements)

CORPT00C ──CALL──► CSUTLDTC (Date validation utility)
COTRN02C ──CALL──► CSUTLDTC (Date validation utility)

COBSWAIT ──CALL──► MVSWAIT  (Assembler — system wait)

COPAUA0C ──CALL──► MQOPEN / MQGET / MQPUT1 / MQCLOSE  (MQ API)
COACCT01 ──CALL──► MQOPEN / MQGET / MQPUT / MQCLOSE   (MQ API)
CODATE01 ──CALL──► MQOPEN / MQGET / MQPUT / MQCLOSE   (MQ API)

CBPAUP0C ──CALL──► CBLTDLI  (IMS DL/I — delete expired records)
DBUNLDGS ──CALL──► CBLTDLI  (IMS DL/I — unload DB to GSAM)
PAUDBLOD ──CALL──► CBLTDLI  (IMS DL/I — load DB from sequential)
PAUDBUNL ──CALL──► CBLTDLI  (IMS DL/I — unload DB to sequential)
```

### 1.2 CICS XCTL (Transfer Control) — Online Navigation Flow

```
                        ┌─────────────┐
                        │  COSGN00C   │  Sign-on
                        │  (Login)    │
                        └──────┬──────┘
                               │ XCTL
                    ┌──────────┴──────────┐
                    ▼                     ▼
            ┌──────────────┐     ┌──────────────┐
            │  COMEN01C    │     │  COADM01C    │
            │ (User Menu)  │     │ (Admin Menu) │
            └──────┬───────┘     └──────┬───────┘
                   │                    │
         ┌─────┬──┴──┬─────┐    ┌──────┴──────┐
         ▼     ▼     ▼     ▼    ▼             ▼
    COACTVWC COTRN00C COBIL00C COCRDLIC   COUSR00C
    (Acct    (Trans   (Bill   (Card      (User
     View)   List)    Pay)    List)       List)
         │     │              │             │
         ▼     ▼              ▼             ▼
    COACTUPC COTRN01C    COCRDSLC     COUSR01C (Add)
    (Acct    (Trans      (Card        COUSR02C (Update)
     Update)  View)       Detail)     COUSR03C (Delete)
               │              │
               ▼              ▼
          COTRN02C       COCRDUPC
          (Trans Add)    (Card Update)
               │
               ▼
          CORPT00C
          (Reports)
```

### 1.3 CICS LINK Dependencies

```
COPAUS1C ──LINK──► (sub-program for detail view processing)
```

---

## 2. Dataset Lineage — VSAM Files

### 2.1 Core VSAM Datasets

| Dataset Name | VSAM Type | Key | Defined By (JCL) | Written By | Read By |
|-------------|-----------|-----|-------------------|-----------|---------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | KSDS | ACCT-ID (11 bytes) | ACCTFILE.jcl | CBTRN02C (I-O), CBACT04C (I-O), CBIMPORT, COACTUPC (REWRITE) | CBACT01C, CBTRN01C, CBTRN02C, CBACT04C, CBSTM03B, CBEXPORT, COACTVWC, COACTUPC, COBIL00C |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | KSDS | CARD-NUM (16 bytes) | CARDFILE.jcl | CBIMPORT, COCRDUPC (REWRITE), COUSR01C (WRITE) | CBACT02C, CBTRN01C, CBEXPORT, COCRDLIC, COCRDSLC, COCRDUPC |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | KSDS | XREF-CARD-NUM (16 bytes) | XREFFILE.jcl | CBIMPORT | CBACT03C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03B, CBEXPORT, COACTVWC, COACTUPC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | KSDS | CUST-ID (9 bytes) | CUSTFILE.jcl | CBIMPORT, COACTUPC (REWRITE) | CBCUS01C, CBTRN01C, CBSTM03B, CBEXPORT, COACTVWC, COACTUPC, COCRDSLC, COCRDUPC |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | KSDS | TRAN-ID (16 bytes) | TRANFILE.jcl | CBTRN02C, CBACT04C, COTRN02C (WRITE), COBIL00C (WRITE) | CBTRN03C, CBEXPORT, CBSTM03B, COTRN00C, COTRN01C, COTRN02C, COBIL00C |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | KSDS | SEC-USR-ID (8 bytes) | DUSRSECJ.jcl | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | KSDS | Composite (17 bytes) | TCATBALF.jcl | CBTRN02C (I-O) | CBACT04C |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | KSDS | TRAN-TYPE (2 bytes) | TRANTYPE.jcl | *(loaded from PS)* | CBTRN03C |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | KSDS | Composite (6 bytes) | TRANCATG.jcl | *(loaded from PS)* | CBTRN03C |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | KSDS | Composite (16 bytes) | DISCGRP.jcl | *(loaded from PS)* | CBACT04C |

### 2.2 Alternate Indexes (AIX)

| AIX Dataset | Base Dataset | AIX Key | Defined By | Used By |
|------------|-------------|---------|-----------|---------|
| CARDXREF AIX (by ACCT-ID) | CARDXREF.VSAM.KSDS | XREF-ACCT-ID | XREFFILE.jcl | COACTVWC, COACTUPC, COBIL00C, COTRN02C |
| CARDDATA AIX (by ACCT-ID) | CARDDATA.VSAM.KSDS | CARD-ACCT-ID | CARDFILE.jcl | COCRDLIC |
| TRANSACT AIX (by CARD-NUM) | TRANSACT.VSAM.KSDS | TRAN-CARD-NUM | TRANFILE.jcl | COTRN00C |

### 2.3 Sequential Datasets

| Dataset Name | Format | Created By | Consumed By |
|-------------|--------|-----------|-------------|
| AWS.M2.CARDDEMO.DALYTRAN.PS | Sequential | External (daily feed) | CBTRN01C, CBTRN02C (via POSTTRAN job) |
| AWS.M2.CARDDEMO.DALYREJS.PS | Sequential | CBTRN02C | Manual review / audit |
| AWS.M2.CARDDEMO.STATEMNT.PS | Sequential | CBSTM03A (via CREASTMT job) | TXT2PDF1 |
| AWS.M2.CARDDEMO.EXPORT.DATA.PS | Sequential | CBEXPORT | CBIMPORT (branch migration) |
| AWS.M2.CARDDEMO.TRANTYPE.PS | Sequential | External / GDG backup | TRANTYPE.jcl (REPRO into VSAM) |
| AWS.M2.CARDDEMO.TRANCATG.PS | Sequential | External / GDG backup | TRANCATG.jcl (REPRO into VSAM) |
| AWS.M2.CARDDEMO.DISCGRP.PS | Sequential | External / GDG backup | DISCGRP.jcl (REPRO into VSAM) |
| AWS.M2.CARDDEMO.USRSEC.PS | Sequential | External | DUSRSECJ.jcl (REPRO into VSAM) |
| AWS.M2.CARDDEMO.ACCTDATA.PSCOMP | Sequential | CBACT01C | Downstream / audit |
| AWS.M2.CARDDEMO.TRANREPT | Sequential | CBTRN03C | Print / archive |

### 2.4 GDG (Generation Data Group) Datasets

| GDG Base | Defined By | Members Created By | Contents |
|----------|-----------|-------------------|---------|
| AWS.M2.CARDDEMO.TRANTYPE.BKUP | DEFGDGD.jcl | DEFGDGD (IEBGENER) | Transaction type backup generations |
| AWS.M2.CARDDEMO.TRANCATG.PS.BKUP | DEFGDGD.jcl | DEFGDGD (IEBGENER) | Transaction category backup generations |
| AWS.M2.CARDDEMO.DISCGRP.BKUP | DEFGDGD.jcl | DEFGDGD (IEBGENER) | Disclosure group backup generations |
| AWS.M2.CARDDEMO.TRANSACT.BKUP | DEFGDGB.jcl | TRANBKP (SORT) | Transaction file backup generations |

---

## 3. End-to-End Batch Pipeline Flows

### 3.1 Daily Transaction Processing Pipeline

```
                         External
                         Feed
                           │
                           ▼
                   ┌───────────────┐
                   │  DALYTRAN.PS  │  Daily transaction file
                   └───────┬───────┘
                           │
            ┌──────────────┴──────────────┐
            ▼                             ▼
   ┌─────────────────┐          ┌─────────────────┐
   │  CLOSEFIL.jcl   │          │  (Validation)   │
   │  Close CICS     │          │  CBTRN01C       │
   │  files          │          │  (optional)     │
   └────────┬────────┘          └─────────────────┘
            ▼
   ┌─────────────────┐
   │  POSTTRAN.jcl   │
   │  PGM=CBTRN02C   │──────► DALYREJS.PS (rejected transactions)
   │                  │
   │  Reads:          │
   │  · DALYTRAN      │
   │  · XREFFILE      │
   │  Updates:        │
   │  · ACCTFILE (I-O)│
   │  · TCATBALF (I-O)│
   │  Writes:         │
   │  · TRANFILE      │
   └────────┬─────────┘
            ▼
   ┌─────────────────┐
   │  TRANBKP.jcl    │  Backup transaction file
   └────────┬─────────┘
            ▼
   ┌─────────────────┐
   │  WAITSTEP.jcl   │  Pause (COBSWAIT)
   └────────┬─────────┘
            ▼
   ┌─────────────────┐
   │  OPENFIL.jcl    │  Reopen CICS files
   └─────────────────┘
```

### 3.2 Monthly Interest Calculation Pipeline

```
   ┌─────────────────┐
   │  CLOSEFIL.jcl   │  Close CICS files
   └────────┬─────────┘
            ▼
   ┌─────────────────┐
   │  INTCALC.jcl    │
   │  PGM=CBACT04C   │
   │                  │
   │  Reads:          │
   │  · TCATBALF      │  (category balances)
   │  · XREFFILE      │  (card-account xref)
   │  · DISCGRP       │  (interest rates)
   │  Updates:        │
   │  · ACCTFILE (I-O)│  (add interest charges)
   │  Writes:         │
   │  · TRANSACT      │  (interest transactions)
   └────────┬─────────┘
            ▼
   ┌─────────────────┐
   │  COMBTRAN.jcl   │
   │  SORT + REPRO   │  Merge interest transactions
   │                  │  into main TRANSACT VSAM
   └────────┬─────────┘
            ▼
   ┌─────────────────┐
   │  WAITSTEP.jcl   │
   └────────┬─────────┘
            ▼
   ┌─────────────────┐
   │  OPENFIL.jcl    │  Reopen CICS files
   └─────────────────┘
```

### 3.3 Statement Generation Pipeline

```
   ┌───────────────────────┐
   │  CREASTMT.JCL         │
   │                       │
   │  Step 1: IDCAMS       │  Delete/define temp KSDS
   │  Step 2: SORT         │  Sort TRANSACT VSAM → seq file
   │  Step 3: IDCAMS       │  REPRO seq → temp KSDS
   │  Step 4: IEFBR14      │  Allocate output files
   │  Step 5: CBSTM03A     │
   │    └──CALL──► CBSTM03B│  (reads TRNXFILE, XREFFILE,
   │                       │   CUSTFILE, ACCTFILE)
   │    Writes:            │
   │    · STMTFILE (text)  │
   │    · HTMLFILE (HTML)  │
   └───────────┬───────────┘
               ▼
   ┌───────────────────────┐
   │  TXT2PDF1.JCL         │  Convert statement to PDF
   │  PGM=IKJEFT1B         │
   │  (TXT2PDF REXX exec)  │
   └───────────────────────┘
```

### 3.4 Transaction Report Pipeline

```
   ┌───────────────────────┐
   │  TRANREPT.jcl         │
   │                       │
   │  Step 1: SORT         │  Sort/filter transactions
   │  Step 2: CBTRN03C     │
   │    Reads:             │
   │    · TRANFILE         │  (sorted transactions)
   │    · CARDXREF         │  (card→account xref)
   │    · TRANTYPE         │  (type descriptions)
   │    · TRANCATG         │  (category descriptions)
   │    · DATEPARM         │  (date range parameters)
   │    Writes:            │
   │    · TRANREPT         │  (formatted report)
   └───────────────────────┘
```

### 3.5 Weekly Reference Data Refresh Pipeline

```
   ┌─────────────────────┐
   │  TRANEXTR.jcl       │  Extract/backup from DB2
   │  (DB2 sub-app)      │
   │                     │
   │  Step 1-2: IEBGENER │  Backup TRANTYPE.PS
   │                     │  and TRANCATG.PS
   │  Step 3: IEFBR14    │  Allocate
   │  Step 4-5: IKJEFT01 │  DB2 extract via DSNTEP4
   └────────┬────────────┘
            ▼
   ┌─────────────────────┐
   │  MNTTRDB2.jcl       │  Maintain DB2 binds
   └────────┬────────────┘
            ▼
   ┌─────────────────────┐
   │  CLOSEFIL.jcl       │
   └────────┬────────────┘
            ▼
   ┌─────────────────────┐
   │  DISCGRP.jcl        │  Reload disclosure groups
   │  IDCAMS REPRO       │  from PS → VSAM KSDS
   └────────┬────────────┘
            ▼
   ┌─────────────────────┐
   │  WAITSTEP.jcl       │
   └────────┬────────────┘
            ▼
   ┌─────────────────────┐
   │  OPENFIL.jcl        │  Reopen CICS files
   └─────────────────────┘
```

### 3.6 Branch Migration Pipeline

```
   Export:                           Import:
   ┌──────────────────┐             ┌──────────────────┐
   │  CBEXPORT.jcl    │             │  CBIMPORT.jcl    │
   │  PGM=CBEXPORT    │             │  PGM=CBIMPORT    │
   │                  │             │                  │
   │  Reads:          │             │  Reads:          │
   │  · CUSTFILE      │             │  · EXPFILE       │
   │  · ACCTFILE      │──EXPFILE──► │                  │
   │  · XREFFILE      │             │  Writes:         │
   │  · TRANSACT      │             │  · CUSTOUT       │
   │  · CARDFILE      │             │  · ACCTOUT       │
   │                  │             │  · XREFOUT       │
   │  Writes:         │             │  · TRNXOUT       │
   │  · EXPFILE       │             │  · CARDOUT       │
   └──────────────────┘             │  · ERROUT        │
                                    └──────────────────┘
```

---

## 4. Copybook Dependency Matrix

### 4.1 Most-Referenced Copybooks (cross-cutting)

| Copybook | Used By (count) | Programs |
|----------|----------------|----------|
| COCOM01Y | 16 | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C |
| COTTL01Y | 15 | All CICS programs with BMS screens |
| CSDAT01Y | 15 | All CICS programs with BMS screens |
| CSMSG01Y | 15 | All CICS programs with BMS screens |
| DFHAID | 14 | All CICS programs (AID key definitions) |
| DFHBMSCA | 14 | All CICS programs (BMS attribute constants) |
| CSUSR01Y | 11 | COSGN00C, COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COUSR00C–03C |
| CVACT01Y | 11 | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, CBSTM03A, COACCT01, COPAUA0C |
| CVACT03Y | 10 | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C |
| CVCUS01Y | 8 | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C |
| CVTRA05Y | 8 | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C |

### 4.2 Program → Copybook Reference Matrix (Main Programs)

| Program | Data Copybooks | BMS Copybooks | Utility Copybooks |
|---------|---------------|---------------|-------------------|
| COACTUPC | CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CVCRD01Y, CSLKPCDY | COACTUP, DFHAID, DFHBMSCA | CSSETATY, CSSTRPFY, CSUTLDPY, CSUTLDWY, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y |
| COACTVWC | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, COCOM01Y, CVCRD01Y | COACTVW, DFHAID, DFHBMSCA | CSSTRPFY, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y |
| CBTRN02C | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y | *(none — batch)* | *(none)* |
| CBACT04C | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y | *(none — batch)* | *(none)* |
| CBEXPORT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT | *(none — batch)* | *(none)* |

---

## 5. Inter-System Dependencies

```
┌─────────────────────────────────────────────────────────────────────┐
│                        CICS Region                                  │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐           │
│  │ COSGN00C │→ │ COMEN01C │→ │ COTRN00C │→ │ COTRN02C │           │
│  │          │  │ COADM01C │  │ COTRN01C │  │ COBIL00C │           │
│  └──────────┘  └──────────┘  │ CORPT00C │  │ COACTUPC │           │
│                              └──────────┘  │ COACTVWC │           │
│                                            │ COCRDLIC │           │
│                                            │ COCRDSLC │           │
│                                            │ COCRDUPC │           │
│                                            │ COUSR00C │           │
│                                            │ COUSR01C │           │
│                                            │ COUSR02C │           │
│                                            │ COUSR03C │           │
│                                            └──────────┘           │
│         │                                       │                  │
│         │ VSAM I/O                              │ WRITEQ TD       │
│         ▼                                       ▼                  │
│  ┌──────────────────────────────┐    ┌─────────────────┐          │
│  │  VSAM KSDS Files            │    │  TD Queue       │          │
│  │  ACCTDATA, CARDDATA,        │    │  (batch submit) │          │
│  │  CARDXREF, CUSTDATA,        │    └────────┬────────┘          │
│  │  TRANSACT, USRSEC,          │             │                    │
│  │  TCATBALF                   │             │                    │
│  └──────────────────────────────┘             │                    │
│         ▲                                     │                    │
│         │                                     ▼                    │
└─────────┼──────────────────────────────────────────────────────────┘
          │                              Batch Region
          │                    ┌────────────────────────────┐
          │                    │ POSTTRAN (CBTRN02C)        │
          └────────────────────│ INTCALC  (CBACT04C)        │
                               │ CREASTMT (CBSTM03A/B)      │
                               │ TRANREPT (CBTRN03C)        │
                               │ CBEXPORT / CBIMPORT        │
                               │ READACCT/CARD/CUST/XREF    │
                               └────────────────────────────┘
                                        │
                                        ▼
                               ┌────────────────────┐
                               │  MQ Series         │
                               │  (Authorization)   │
                               │  COPAUA0C           │
                               │  COACCT01           │
                               │  CODATE01           │
                               └────────────────────┘
                                        │
                                        ▼
                               ┌────────────────────┐
                               │  IMS DB            │
                               │  CBPAUP0C          │
                               │  DBUNLDGS          │
                               │  PAUDBLOD          │
                               │  PAUDBUNL          │
                               └────────────────────┘
                                        │
                                        ▼
                               ┌────────────────────┐
                               │  DB2               │
                               │  COBTUPDT          │
                               │  COTRTLIC          │
                               │  COTRTUPC          │
                               │  COPAUS2C          │
                               └────────────────────┘
```
