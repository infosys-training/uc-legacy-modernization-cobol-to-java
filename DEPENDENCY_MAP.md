# DEPENDENCY MAP — CardDemo COBOL Estate

> Call graphs, dataset lineage, and end-to-end batch pipeline flows.

---

## 1. Inter-Program Call Graph

### 1.1 CALL Dependencies (Static Calls)

```
COACTUPC ──CALL──► CSUTLDTC (date validation utility)
COCRDUPC ──CALL──► CSUTLDTC
COTRN02C ──CALL──► CSUTLDTC
CBSTM03A ──CALL──► CBSTM03B (statement file processor)
CBACT01C ──CALL──► COBDATFT (assembler date format routine)
CBACT01C ──CALL──► CEE3ABD  (LE abnormal termination)
CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD
CSUTLDTC ──CALL──► CEEDAYS  (LE date conversion API)

DBUNLDGS ──CALL──► CBLTDLI  (IMS DL/I interface)
PAUDBLOD ──CALL──► CBLTDLI
PAUDBUNL ──CALL──► CBLTDLI
```

### 1.2 CICS Transfer of Control (XCTL)

Online programs use EXEC CICS XCTL to transfer control between screens. The navigation hub is `COMEN01C` (main menu).

```
COSGN00C ──XCTL──► COMEN01C (after successful login)
                    │
COMEN01C ──XCTL──► COACTVWC (option: Account View)
           XCTL──► COACTUPC (option: Account Update)
           XCTL──► COCRDLIC (option: Card List)
           XCTL──► COTRN00C (option: Transaction List)
           XCTL──► COBIL00C (option: Bill Payment)
           XCTL──► CORPT00C (option: Reports)
           XCTL──► COADM01C (option: Admin — admin users only)
                    │
COADM01C ──XCTL──► COUSR00C (option: User List)
                    │
COUSR00C ──XCTL──► COUSR01C (Add User)
           XCTL──► COUSR02C (Update User)
           XCTL──► COUSR03C (Delete User)

COCRDLIC ──XCTL──► COCRDSLC (Card Selection)
           XCTL──► COCRDUPC (Card Update)

COTRN00C ──XCTL──► COTRN01C (View Transaction)
           XCTL──► COTRN02C (Add Transaction)
```

### 1.3 Full Online Navigation Map

```
┌─────────┐
│ COSGN00C│  Sign-On
└────┬────┘
     │ XCTL
     ▼
┌─────────┐
│ COMEN01C│  Main Menu
└────┬────┘
     │
     ├──► COACTVWC   Account View
     │
     ├──► COACTUPC   Account Update ──CALL──► CSUTLDTC
     │
     ├──► COCRDLIC   Card List
     │       ├──► COCRDSLC   Card Selection
     │       └──► COCRDUPC   Card Update ──CALL──► CSUTLDTC
     │
     ├──► COTRN00C   Transaction List
     │       ├──► COTRN01C   Transaction View
     │       └──► COTRN02C   Transaction Add ──CALL──► CSUTLDTC
     │
     ├──► COBIL00C   Bill Payment
     │
     ├──► CORPT00C   Report Request (submits batch JCL)
     │
     └──► COADM01C   Admin Menu (admin only)
             └──► COUSR00C   User List
                    ├──► COUSR01C   Add User
                    ├──► COUSR02C   Update User
                    └──► COUSR03C   Delete User
```

### 1.4 Authorization Sub-App Call Graph

```
COPAUA0C (Authorization Decision)
    ├── reads MQ request queue
    ├── CALL CBLTDLI → IMS DBPAUTP0
    └── writes MQ response queue

COPAUS0C (Auth Summary) ──XCTL──► COPAUS1C (Auth Detail)
COPAUS1C ──XCTL──► COPAUS2C (Mark Fraud)
COPAUS2C ── EXEC SQL → DB2 AUTHFRDS table

CBPAUP0C (Batch) ── CALL CBLTDLI → IMS DBPAUTP0 (delete expired)

DBUNLDGS ── CALL CBLTDLI → IMS → GSAM output
PAUDBLOD ── CALL CBLTDLI → sequential → IMS
PAUDBUNL ── CALL CBLTDLI → IMS → sequential output
```

### 1.5 Transaction Type Sub-App

```
COTRTLIC (List Types)
    ├── EXEC SQL → CARDDEMO.TRANSACTION_TYPE
    └── XCTL → COTRTUPC (Update Type)

COTRTUPC (Update/Add/Delete Type)
    └── EXEC SQL → CARDDEMO.TRANSACTION_TYPE

COBTUPDT (Batch Update)
    └── EXEC SQL → CARDDEMO.TRANSACTION_TYPE
```

---

## 2. Dataset Lineage

### 2.1 VSAM Dataset Lifecycle

Each VSAM KSDS follows a lifecycle: **Define → Load → Online Access → Batch Processing → Backup**.

```
Sequential PS File ──(JCL IDCAMS REPRO)──► VSAM KSDS ──(CICS online)──► Online Programs
                                                       ──(Batch JCL)───► Batch Programs
                                                       ──(IDCAMS REPRO)► GDG Backup
```

### 2.2 Master VSAM Datasets

| Dataset Name | JCL (Define/Load) | Online Programs (R/W) | Batch Programs (R/W) | Backup JCL |
|-------------|-------------------|----------------------|---------------------|------------|
| **ACCTDATA.VSAM.KSDS** | ACCTFILE.jcl | COACTUPC (R/W), COACTVWC (R), COBIL00C (R/W), COCRDSLC (R), COCRDUPC (R) | CBACT01C (R), CBACT04C (R), CBEXPORT (R), CBIMPORT (W→ACCTOUT), CBSTM03B (R), CBTRN01C (R), CBTRN02C (R) | — |
| **CARDDATA.VSAM.KSDS** | CARDFILE.jcl | COACTUPC (R), COACTVWC (R), COCRDLIC (R), COCRDSLC (R), COCRDUPC (R/W) | CBACT02C (R), CBEXPORT (R), CBIMPORT (W→CARDOUT), CBTRN01C (R) | — |
| **CUSTDATA.VSAM.KSDS** | CUSTFILE.jcl | COACTUPC (R), COACTVWC (R) | CBCUS01C (R), CBEXPORT (R), CBIMPORT (W→CUSTOUT), CBSTM03B (R), CBTRN01C (R) | — |
| **CARDXREF.VSAM.KSDS** | XREFFILE.jcl | COACTUPC (R), COACTVWC (R), COBIL00C (R), COCRDLIC (R), COCRDUPC (R), COTRN02C (R) | CBACT03C (R), CBACT04C (R), CBEXPORT (R), CBIMPORT (W→XREFOUT), CBSTM03B (R), CBTRN01C (R), CBTRN02C (R), CBTRN03C (R), CREASTMT.JCL | — |
| **TRANSACT.VSAM.KSDS** | TRANFILE.jcl | COBIL00C (W), COTRN00C (R), COTRN01C (R), COTRN02C (W) | CBEXPORT (R), CBIMPORT (W→TRNXOUT), CBTRN02C (W), CREASTMT.JCL (R) | TRANBKP.jcl |
| **USRSEC.VSAM.KSDS** | DUSRSECJ.jcl | COSGN00C (R), COUSR00C (R), COUSR01C (W), COUSR02C (R/W), COUSR03C (R/D) | — | — |
| **TCATBALF.VSAM.KSDS** | TCATBALF.jcl | — | CBACT04C (R), CBTRN02C (R/W) | PRTCATBL.jcl |
| **TRANTYPE.VSAM.KSDS** | TRANTYPE.jcl | — | CBTRN03C (R) | DEFGDGD.jcl |
| **TRANCATG.VSAM.KSDS** | TRANCATG.jcl | — | CBTRN03C (R) | DEFGDGD.jcl |
| **DISCGRP.VSAM.KSDS** | DISCGRP.jcl | — | CBACT04C (R) | DEFGDGD.jcl |
| **DALYTRAN.PS** | *(external feed)* | — | CBTRN01C (R), CBTRN02C (R), POSTTRAN.jcl | — |

### 2.3 Derived / Output Datasets

| Dataset | Produced By | Consumed By |
|---------|------------|-------------|
| DALYREJS (GDG) | CBTRN02C (rejected transactions) | Audit/review |
| TRANSACT.BKUP (GDG) | TRANBKP.jcl (IDCAMS REPRO) | COMBTRAN.jcl (combine) |
| TRANSACT.COMBINED (GDG) | COMBTRAN.jcl (SORT+merge) | CREASTMT.JCL |
| STATEMNT.HTML | CBSTM03A (statement generator) | TXT2PDF1.JCL |
| STATEMNT.PS | CBSTM03A | TXT2PDF1.JCL (→ PDF) |
| EXPORT.DATA | CBEXPORT | CBIMPORT |
| IMPORT.ERRORS | CBIMPORT | Error review |
| TCATBALF.REPT | PRTCATBL.jcl (SORT) | Print output |
| TRANTYPE.BKUP (GDG) | DEFGDGD.jcl | Recovery |
| TRANCATG.PS.BKUP (GDG) | DEFGDGD.jcl | Recovery |
| DISCGRP.BKUP (GDG) | DEFGDGD.jcl | Recovery |
| ACCTDATA.PSCOMP | CBACT01C (sequential dump) | Diagnostics |
| ACCTDATA.ARRYPS | CBACT01C (array dump) | Diagnostics |
| ACCTDATA.VBPS | CBACT01C (VBR dump) | Diagnostics |

### 2.4 IMS Database Lineage (Sub-App)

```
DBPAUTP0 (IMS Database)
    │
    ├─── PAUDBLOD ◄── INFILE1, INFILE2 (sequential load)
    │
    ├─── PAUDBUNL ──► OUTFIL1, OUTFIL2 (sequential unload)
    │
    ├─── DBUNLDGS ──► OUTFIL1, OUTFIL2 (GSAM unload)
    │
    ├─── COPAUA0C (online auth decision — R/W)
    ├─── COPAUS0C (online summary browse — R)
    ├─── COPAUS1C (online detail view — R)
    │
    └─── CBPAUP0C (batch delete expired — D)
```

### 2.5 DB2 Table Lineage (Sub-App)

```
CARDDEMO.TRANSACTION_TYPE (DB2 Table)
    │
    ├─── CREADB21.jcl  (CREATE TABLE + initial load)
    ├─── MNTTRDB2.jcl  (weekly maintenance)
    ├─── TRANEXTR.jcl  (extract → sequential PS files)
    ├─── COTRTLIC       (online list — SELECT)
    ├─── COTRTUPC       (online update — INSERT/UPDATE/DELETE)
    └─── COBTUPDT       (batch update)

AUTHFRDS (DB2 Table)
    └─── COPAUS2C       (online mark fraud — INSERT)
```

---

## 3. End-to-End Batch Pipeline Flows

### 3.1 Daily Transaction Processing Pipeline

```
┌──────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH CYCLE                                  │
│                                                                      │
│  Step 1: CLOSEFIL.jcl                                                │
│          └── SDSF: Close CICS files for batch access                 │
│                                                                      │
│  Step 2: TRANBKP.jcl                                                 │
│          ├── REPROC: Housekeeping                                    │
│          ├── IDCAMS: REPRO TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1)  │
│          └── Output: New GDG generation of transaction backup        │
│                                                                      │
│  Step 3: POSTTRAN.jcl                                                │
│          └── CBTRN02C: Read DALYTRAN.PS                              │
│              ├── Validate against CARDXREF, ACCTDATA                 │
│              ├── Update TRANSACT.VSAM.KSDS (write new transactions)  │
│              ├── Update ACCTDATA.VSAM.KSDS (adjust balances)         │
│              ├── Update TCATBALF.VSAM.KSDS (category balances)       │
│              └── Write rejects → DALYREJS(+1)                        │
│                                                                      │
│  Step 4: WAITSTEP.jcl                                                │
│          └── COBSWAIT: Pause between steps                           │
│                                                                      │
│  Step 5: OPENFIL.jcl                                                 │
│          └── SDSF: Reopen CICS files for online access               │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.2 Weekly Reference Data Refresh Pipeline

```
┌──────────────────────────────────────────────────────────────────────┐
│                  WEEKLY BATCH CYCLE (Saturday)                        │
│                                                                      │
│  Step 1: MNTTRDB2.jcl                                                │
│          └── DB2 maintenance on TRANSACTION_TYPE table               │
│                    │                                                  │
│        ┌──────────┴──────────┐                                       │
│        ▼                     ▼                                       │
│  Branch A:                Branch B:                                   │
│  TransactionTypesDBRefresh    DisclosureGroupsRefresh                 │
│                                                                      │
│  TRANEXTR.jcl              CLOSEFIL.jcl                              │
│  └── Extract DB2 →         └── Close CICS files                     │
│      TRANTYPE.PS                                                     │
│      TRANCATG.PS           DISCGRP.jcl                               │
│                            └── IDCAMS: Reload DISCGRP.VSAM.KSDS     │
│                                                                      │
│                            WAITSTEP.jcl                               │
│                            └── Pause                                 │
│                                                                      │
│                            OPENFIL.jcl                                │
│                            └── Reopen CICS files                     │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.3 Monthly Interest Calculation Pipeline

```
┌──────────────────────────────────────────────────────────────────────┐
│                  MONTHLY BATCH CYCLE                                  │
│                                                                      │
│  Step 1: CLOSEFIL.jcl                                                │
│          └── Close CICS files                                        │
│                                                                      │
│  Step 2: INTCALC.jcl                                                 │
│          └── CBACT04C: Interest calculator                           │
│              ├── Read ACCTDATA, XREFFILE, DISCGRP, TCATBALF         │
│              ├── Compute interest per account using group rates       │
│              └── Write interest transactions → TRANSACT              │
│                                                                      │
│  Step 3: COMBTRAN.jcl                                                │
│          ├── SORT: Merge TRANSACT.BKUP(0) + SYSTRAN(0)              │
│          └── IDCAMS: Load combined → TRANSACT.VSAM.KSDS             │
│                                                                      │
│  Step 4: WAITSTEP.jcl                                                │
│          └── Pause                                                   │
│                                                                      │
│  Step 5: OPENFIL.jcl                                                 │
│          └── Reopen CICS files                                       │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.4 Statement Generation Pipeline (Ad-Hoc / Triggered from Online)

```
CORPT00C (online) ──submit JCL──► CREASTMT.JCL
                                   │
                                   ├── DELDEF01: Delete/define temp VSAM
                                   ├── STEP010:  SORT transactions
                                   ├── STEP020:  IDCAMS REPRO → temp KSDS
                                   ├── STEP030:  IEFBR14 (placeholder)
                                   └── STEP040:  CBSTM03A
                                                  ├── Read sorted transactions
                                                  ├── CALL CBSTM03B
                                                  │    ├── Read XREFFILE
                                                  │    ├── Read CUSTFILE
                                                  │    └── Read ACCTFILE
                                                  └── Write STATEMNT.HTML + PS
                                                       │
                                                       ▼
                                                  TXT2PDF1.JCL
                                                  └── Convert PS → PDF
```

### 3.5 Branch Migration Pipeline

```
CBEXPORT.jcl                         CBIMPORT.jcl
├── STEP01: IDCAMS (define output)   ├── STEP01: CBIMPORT
└── STEP02: CBEXPORT                 │    ├── Read EXPORT.DATA
     ├── Read CUSTFILE               │    ├── Write CUSTOUT
     ├── Read ACCTFILE               │    ├── Write ACCTOUT
     ├── Read XREFFILE               │    ├── Write XREFOUT
     ├── Read TRANSACT               │    ├── Write TRNXOUT
     ├── Read CARDFILE               │    ├── Write CARDOUT
     └── Write EXPORT.DATA           │    └── Write ERROUT (errors)
                                     │
          EXPORT.DATA ───────────────┘
```

---

## 4. Shared Copybook Dependency Matrix

Programs referencing each core copybook:

| Copybook | Programs Using It | Count |
|----------|------------------|-------|
| COCOM01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC + 2 more | 21 |
| CSDAT01Y | *(same 21 online programs)* | 21 |
| COTTL01Y | *(same 21 online programs)* | 21 |
| CSMSG01Y | *(same 21 online programs)* | 21 |
| CVACT01Y | COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COTRN02C, CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C + 3 more | 16 |
| CVACT03Y | COACTUPC, COACTVWC, COBIL00C, COCRDLIC, COCRDUPC, COTRN02C, CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C + 2 more | 16 |
| CSUSR01Y | COADM01C, COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC + 6 more | 14 |
| CVTRA05Y | COBIL00C, COTRN00C, COTRN01C, COTRN02C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C + 1 more | 11 |
| CVCUS01Y | COACTUPC, COACTVWC, CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C + 4 more | 10 |
| CVACT02Y | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C, CBEXPORT, CBIMPORT, COTRTLIC + 1 more | 10 |
| CVCRD01Y | COACTUPC, COCRDUPC, COCRDLIC, COTRTLIC, COTRTUPC + 2 more | 7 |
| CSSTRPFY | COACTUPC, COCRDUPC, COTRTLIC, COTRTUPC + 3 more | 7 |
