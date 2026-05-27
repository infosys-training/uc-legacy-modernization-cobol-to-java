# DEPENDENCY MAP — CardDemo COBOL Estate

> **Generated:** 2026-05-27  
> **Repository:** `uc-legacy-modernization-cobol-to-java`

---

## 1. Inter-Program Call Graph

### 1.1 Batch Program Calls (CALL statement)

```
CBACT01C ──CALL──► COBDATFT  (Assembler date formatting)
         ──CALL──► CEE3ABD   (LE abnormal termination)

CBACT02C ──CALL──► CEE3ABD

CBACT04C ──CALL──► CEE3ABD

CBCUS01C ──CALL──► CEE3ABD

CBTRN03C ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B  (×13 — file I/O subroutine)
         ──CALL──► CEE3ABD
```

### 1.2 Online Program Calls (CALL statement)

```
CORPT00C ──CALL──► CSUTLDTC  (Date validation — called for start & end date)

COTRN02C ──CALL──► CSUTLDTC  (Date validation — called for start & end date)
```

### 1.3 CICS Navigation (XCTL — Transfer Control)

All CICS programs use `XCTL PROGRAM(CDEMO-TO-PROGRAM)` where `CDEMO-TO-PROGRAM` is set dynamically from the COMMAREA. The static navigation graph is:

```
COSGN00C (Sign-on)
    │
    ├── Regular User ──► COMEN01C (Main Menu)
    │                       │
    │                       ├── Option 01 ──► COACTVWC (View Account)
    │                       ├── Option 02 ──► COACTUPC (Update Account)
    │                       ├── Option 03 ──► COCRDLIC (List Cards)
    │                       │                    ├── PF5 ──► COCRDSLC (View Card)
    │                       │                    └── PF6 ──► COCRDUPC (Update Card)
    │                       ├── Option 04 ──► COCRDSLC (View Card)
    │                       ├── Option 05 ──► COCRDUPC (Update Card)
    │                       ├── Option 06 ──► COTRN00C (List Transactions)
    │                       │                    └── Select ──► COTRN01C (View Transaction)
    │                       ├── Option 07 ──► COTRN01C (View Transaction)
    │                       ├── Option 08 ──► COTRN02C (Add Transaction)
    │                       ├── Option 09 ──► CORPT00C (Transaction Reports)
    │                       ├── Option 10 ──► COBIL00C (Bill Payment)
    │                       └── Option 11 ──► COPAUS0C (Pending Authorizations)
    │                                            └── Select ──► COPAUS1C (Auth Detail)
    │
    └── Admin User ──► COADM01C (Admin Menu)
                          │
                          ├── Option ──► COUSR00C (List Users)
                          │                  ├── PF5 ──► COUSR02C (Update User)
                          │                  └── PF6 ──► COUSR03C (Delete User)
                          └── Option ──► COUSR01C (Add User)
```

### 1.4 Callee Frequency (how many programs call each)

| Callee | Callers | Count |
|--------|---------|-------|
| CEE3ABD | CBACT01C, CBACT02C, CBACT04C, CBCUS01C, CBTRN03C, CBSTM03A | 6 |
| CSUTLDTC | CORPT00C, COTRN02C | 2 |
| CBSTM03B | CBSTM03A | 1 (×13 invocations) |
| COBDATFT | CBACT01C | 1 |

---

## 2. Copybook Dependency Graph

### 2.1 Most-Referenced Copybooks

| Rank | Copybook | Programs Using It | Purpose |
|------|----------|--------------------|---------|
| 1 | DFHAID | 15 | CICS attention identifier constants |
| 2 | DFHBMSCA | 14 | BMS map attribute constants |
| 3 | COTTL01Y | 14 | Screen title |
| 4 | CSDAT01Y | 14 | Date/time work area |
| 5 | CSMSG01Y | 14 | Common messages |
| 6 | COCOM01Y | 13 | COMMAREA layout |
| 7 | CVCRD01Y | 7 | Card work areas (CICS navigation) |
| 8 | CSUSR01Y | 7 | User security record |
| 9 | CVACT01Y | 7 | Account record |
| 10 | CVACT03Y | 6 | Card cross-reference record |
| 11 | CVACT02Y | 5 | Card record |
| 12 | CVCUS01Y | 5 | Customer record |
| 13 | CVTRA05Y | 5 | Transaction record |
| 14 | CSSTRPFY | 7 | PFKey store procedure |
| 15 | CSSETATY | 1 (COACTUPC ×30) | Field attribute setting (template) |

### 2.2 Programs with Most Copybook References

| Program | Copybooks | Count |
|---------|-----------|-------|
| COACTUPC | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY(×30), CSSTRPFY, CSUTLDPY | 18 unique |
| COCRDLIC | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY | 11 |
| COACTVWC | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY | 15 |
| COCRDSLC | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY | 13 |
| COCRDUPC | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY | 13 |

---

## 3. Dataset Lineage

### 3.1 VSAM Cluster → JCL → Program Map

| VSAM Cluster | DD Name | Defined By JCL | Loaded From | Read By Programs | Written By Programs |
|-------------|---------|----------------|-------------|-----------------|-------------------|
| ACCTDATA.VSAM.KSDS | ACCTFILE / ACCTDAT | ACCTFILE.jcl | ACCTDATA.PS | CBACT01C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBSTM03A/B, COACTVWC, COACTUPC, COTRN02C, COBIL00C | COACTUPC, COBIL00C |
| CARDDATA.VSAM.KSDS | CARDFILE / CARDDAT | CARDFILE.jcl | CARDDATA.PS | CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COACTUPC | COCRDUPC |
| CUSTDATA.VSAM.KSDS | CUSTFILE / CUSTDAT | CUSTFILE.jcl | CUSTDATA.PS | CBCUS01C, CBEXPORT, CBTRN01C, CBSTM03A/B, COACTVWC, COACTUPC, COCRDSLC, COCRDUPC | _(read-only in core)_ |
| CARDXREF.VSAM.KSDS | XREFFILE / CARDXREF / CARDAIX | XREFFILE.jcl | CARDXREF.PS | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A/B, COACTVWC, COACTUPC, COTRN02C | _(read-only)_ |
| TRANSACT.VSAM.KSDS | TRANSACT / TRANFILE | TRANFILE.jcl | DALYTRAN.PS.INIT | CBEXPORT, CBTRN03C, COTRN00C, COTRN01C | CBTRN01C, CBTRN02C, COTRN02C, COBIL00C |
| TCATBALF.VSAM.KSDS | TCATBALF | TCATBALF.jcl | TCATBALF.PS | CBACT04C, CBTRN02C | CBACT04C, CBTRN02C |
| DISCGRP.VSAM.KSDS | DISCGRP | DISCGRP.jcl | DISCGRP.PS | CBACT04C | _(reference data)_ |
| TRANTYPE.VSAM.KSDS | TRANTYPE | TRANTYPE.jcl | TRANTYPE.PS | CBTRN03C | _(reference data)_ |
| TRANCATG.VSAM.KSDS | TRANCATG | TRANCATG.jcl | TRANCATG.PS | CBTRN03C | _(reference data)_ |
| USRSEC.VSAM.KSDS | USRSEC | DUSRSECJ.jcl | USRSEC.PS | COSGN00C, COUSR00C, COUSR02C, COUSR03C | COUSR01C, COUSR02C, COUSR03C |
| EXPORT.DATA | EXPFILE | (defined in CBEXPORT.jcl STEP01) | — | CBIMPORT | CBEXPORT |

### 3.2 GDG (Generation Data Groups) — Backup/Output Lineage

| GDG Base | Written By Job | Content | Consumed By |
|----------|---------------|---------|-------------|
| TRANSACT.BKUP | TRANBKP.jcl | Transaction master backup | COMBTRAN.jcl |
| SYSTRAN | INTCALC.jcl | Interest-generated transactions | COMBTRAN.jcl |
| TRANSACT.COMBINED | COMBTRAN.jcl | Merged monthly transactions | _(archive)_ |
| DALYREJS | POSTTRAN.jcl | Rejected daily transactions | _(review/audit)_ |
| TRANREPT | TRANREPT.jcl | Transaction detail report | _(output)_ |
| TCATBALF.BKUP | PRTCATBL.jcl | Category balance backup | _(archive)_ |
| DISCGRP.BKUP | DEFGDGD.jcl | Disclosure group backup | _(archive)_ |
| TRANTYPE.BKUP | DEFGDGD.jcl | Transaction type backup | _(archive)_ |
| TRANCATG.PS.BKUP | DEFGDGD.jcl | Transaction category backup | _(archive)_ |

---

## 4. End-to-End Batch Pipeline Flows

### 4.1 Daily Transaction Processing

```
                    ┌─────────────────────────────┐
                    │   ONLINE CICS TRANSACTIONS   │
                    │ (COTRN02C adds to TRANSACT)  │
                    └───────────┬─────────────────┘
                                │
                                ▼
┌──────────┐    ┌───────────────────────┐    ┌──────────────────┐
│ Daily    │    │  CBTRN01C             │    │  Rejected        │
│ Tran     │───►│  Validate daily trans │    │  Transactions    │
│ Input    │    │  against master files │    │  (review)        │
│(DALYTRAN)│    └───────────┬───────────┘    └──────────────────┘
└──────────┘                │                         ▲
                            ▼                         │
                 ┌───────────────────────┐            │
                 │  CBTRN02C             │────────────┘
                 │  Post transactions:   │
                 │  - Update ACCT bal    │
                 │  - Update TCATBALF    │
                 │  - Write TRANSACT     │
                 │  - Write DALYREJS     │
                 └───────────┬───────────┘
                             │
                 ┌───────────▼───────────┐
                 │  TRANBKP.jcl          │
                 │  Backup TRANSACT to   │
                 │  TRANSACT.BKUP GDG    │
                 └───────────┬───────────┘
                             │
                 ┌───────────▼───────────┐
                 │  CBTRN03C             │
                 │  Print transaction    │
                 │  detail report        │
                 │  → TRANREPT GDG       │
                 └───────────────────────┘
```

### 4.2 Monthly Interest Calculation

```
    ┌──────────────────────────────────────┐
    │  Control-M: MONTHLY-InterestCalc     │
    │  CLOSEFIL → INTCALC → COMBTRAN →    │
    │  WAITSTEP → OPENFIL                  │
    └──────────────────────────────────────┘

    ┌──────────┐     ┌─────────────┐     ┌───────────┐
    │ TCATBALF │────►│  CBACT04C   │────►│ SYSTRAN   │
    │ (balance)│     │  Interest   │     │ (new int  │
    │ DISCGRP  │────►│  Calculator │     │  trans)   │
    │ (rates)  │     │             │     └─────┬─────┘
    │ ACCTFILE │────►│  Per-account│           │
    │ XREFFILE │────►│  per-categ  │           │
    └──────────┘     └─────────────┘           │
                                               ▼
    ┌──────────────┐     ┌──────────────────────────┐
    │ TRANSACT.BKUP│────►│  COMBTRAN.jcl (SORT)     │
    │ (daily bkup) │     │  Merge SYSTRAN + BKUP    │
    └──────────────┘     │  → TRANSACT.COMBINED GDG │
                         └──────────────────────────┘
```

### 4.3 Branch Migration Export/Import

```
    EXPORT FLOW:
    ┌──────────────────────────────────────┐
    │  CBEXPORT.jcl                        │
    │  STEP01: IDCAMS define EXPORT.DATA   │
    │  STEP02: PGM=CBEXPORT               │
    └──────────────────────────────────────┘

    ┌──────────┐
    │ CUSTFILE │──┐
    │ ACCTFILE │──┤
    │ XREFFILE │──┼──► CBEXPORT ──► EXPORT.DATA
    │ TRANSACT │──┤    (multi-record VSAM)
    │ CARDFILE │──┘
    └──────────┘

    IMPORT FLOW:
    ┌──────────────────────────────────────┐
    │  CBIMPORT.jcl                        │
    │  STEP01: PGM=CBIMPORT               │
    └──────────────────────────────────────┘

    EXPORT.DATA ──► CBIMPORT ──┬──► CUSTOUT
                               ├──► ACCTOUT
                               ├──► XREFOUT
                               ├──► TRNXOUT
                               ├──► CARDOUT
                               └──► ERROUT (validation errors)
```

### 4.4 Account Statement Generation

```
    ┌──────────────────────────────────────┐
    │  CREASTMT.JCL                        │
    │  DELDEF01: IDCAMS define TRXFL VSAM  │
    │  STEP010: SORT TRANSACT → TRXFL.SEQ  │
    │  STEP020: IDCAMS REPRO → TRXFL.VSAM  │
    │  STEP030: IEFBR14 (dummy step)       │
    │  STEP040: PGM=CBSTM03A              │
    └──────────────────────────────────────┘

    ┌──────────┐
    │ TRXFL    │──┐
    │ (sorted) │  │
    │ XREFFILE │──┤
    │ CUSTFILE │──┼──► CBSTM03A ──┬──► STMTFILE (text)
    │ ACCTFILE │──┘    │          └──► HTMLFILE (HTML)
    └──────────┘       │
                       └──CALL──► CBSTM03B (×13)
                                  (file I/O subroutine)
```

### 4.5 Daily Scheduler Flow (Control-M)

```
DAILY-TransactionBackup:
    CLOSEFIL ──► TRANBKP ──► WAITSTEP ──► OPENFIL
       │            │
       │            └─ REPRO TRANSACT.VSAM → TRANSACT.BKUP(+1)
       │               DELETE/DEFINE TRANSACT.VSAM
       │
       └─ CEMT SET FILE(*) CLOSED

WEEKLY-TransactionTypesDBRefresh:
    MNTTRDB2 ──► CLOSEFIL ──► DISCGRP ──► WAITSTEP ──► OPENFIL
       │                          │                        │
       │                          └─ Refresh DISCGRP VSAM  │
       │                                                    └─ TRANEXTR (parallel)
       └─ Refresh Db2 TRNTYPE/TRNCATG tables

MONTHLY-InterestCalculation:
    CLOSEFIL ──► INTCALC ──► COMBTRAN ──► WAITSTEP ──► OPENFIL
                    │            │
                    │            └─ SORT + merge SYSTRAN + BKUP
                    └─ PGM=CBACT04C (interest calc)
```

---

## 5. Technology Dependency Summary

```
┌──────────────────────────────────────────────────────┐
│                    CICS TP Monitor                    │
│  ┌─────────────────────────────────────────────────┐ │
│  │  BMS Maps (21)  │  COMMAREA  │  DFHAID/DFHBMSCA│ │
│  └─────────────────┴────────────┴─────────────────┘ │
│  COSGN00C → COMEN01C / COADM01C                     │
│  → COACTVWC / COACTUPC / COCRDLIC / COCRDSLC        │
│  → COCRDUPC / COTRN00C / COTRN01C / COTRN02C        │
│  → COBIL00C / CORPT00C / COUSR00-03C                │
│  → COPAUS0C / COPAUS1C / COPAUS2C (IMS sub-app)     │
│  → COTRTLIC / COTRTUPC (Db2 sub-app)                │
└────────────────────────┬─────────────────────────────┘
                         │ VSAM Files
┌────────────────────────▼─────────────────────────────┐
│                  VSAM (KSDS/AIX)                     │
│  ACCTDATA · CARDDATA · CUSTDATA · CARDXREF           │
│  TRANSACT · TCATBALF · DISCGRP · TRANTYPE · TRANCATG│
│  USRSEC · EXPORT.DATA                                │
└────────────────────────┬─────────────────────────────┘
                         │ Batch Access
┌────────────────────────▼─────────────────────────────┐
│               JCL Batch Jobs                         │
│  READACCT → CBACT01C  │  POSTTRAN → CBTRN02C        │
│  READCARD → CBACT02C  │  TRANREPT → CBTRN03C        │
│  READCUST → CBCUS01C  │  CREASTMT → CBSTM03A        │
│  READXREF → CBACT03C  │  CBEXPORT → CBEXPORT        │
│  INTCALC  → CBACT04C  │  CBIMPORT → CBIMPORT        │
└──────────────────────────────────────────────────────┘
                         │
┌────────────────────────▼─────────────────────────────┐
│            External Dependencies                     │
│  CEE3ABD (LE runtime) · COBDATFT (Assembler)         │
│  CEEDAYS (LE intrinsic) · IDCAMS / SORT / IEBGENER  │
│  DFHCSDUP (CICS CSD) · SDSF (file open/close)       │
│  IMS DL/I (sub-app) · Db2 (sub-app) · MQ (sub-app)  │
└──────────────────────────────────────────────────────┘
```
