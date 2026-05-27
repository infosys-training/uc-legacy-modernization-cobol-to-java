# DEPENDENCY MAP

## Overview

This document maps the inter-program call graph, CICS transfer control flow, dataset lineage through JCL jobs, and end-to-end batch pipeline flow for the CardDemo application.

---

## 1. Program Call Graph

### 1.1 CALL Relationships (Static Linkage)

```
CBACT01C ──CALL──► COBDATFT (date format utility)
         ──CALL──► CEE3ABD  (LE abend)

CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B (statement file writer subroutine)
         ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT  (system wait service)

CORPT00C ──CALL──► CSUTLDTC (date/time utility)
COTRN02C ──CALL──► CSUTLDTC (date/time utility)

CSUTLDTC ──CALL──► CEEDAYS  (LE date intrinsic)

COPAUA0C ──CALL──► MQOPEN   (MQ Open queue)
         ──CALL──► MQGET    (MQ Get message)
         ──CALL──► MQPUT1   (MQ Put reply)
         ──CALL──► MQCLOSE  (MQ Close queue)

COACCT01 ──CALL──► MQOPEN   (×3 queues)
         ──CALL──► MQGET
         ──CALL──► MQPUT    (×2 queues)
         ──CALL──► MQCLOSE  (×3 queues)

CODATE01 ──CALL──► MQOPEN   (×3 queues)
         ──CALL──► MQGET
         ──CALL──► MQPUT    (×2 queues)
         ──CALL──► MQCLOSE  (×3 queues)

PAUDBLOD ──CALL──► CBLTDLI  (IMS DL/I: ISRT, GU)
PAUDBUNL ──CALL──► CBLTDLI  (IMS DL/I: GN, GNP)
DBUNLDGS ──CALL──► CBLTDLI  (IMS DL/I: GN, GNP, ISRT)
```

### 1.2 CICS Transfer Control (XCTL) — Online Navigation

```
COSGN00C (Sign-on)
    ├──XCTL──► COADM01C  (if Admin user)
    └──XCTL──► COMEN01C  (if Regular user)

COADM01C (Admin Menu)
    └──XCTL──► [CDEMO-ADMIN-OPT-PGMNAME] (dynamic, based on option selected)
                ├── COUSR00C (User List)
                ├── COUSR01C (User Add)
                ├── COUSR02C (User Update)
                ├── COUSR03C (User Delete)
                ├── COTRTLIC (Trans Type List - DB2)
                └── COTRTUPC (Trans Type Update - DB2)

COMEN01C (Main Menu)
    └──XCTL──► [CDEMO-MENU-OPT-PGMNAME] (dynamic, based on option selected)
                ├── COACTVWC (Account View)
                ├── COACTUPC (Account Update)
                ├── COCRDLIC (Credit Card List)
                ├── COCRDSLC (Credit Card View)
                ├── COCRDUPC (Credit Card Update)
                ├── COTRN00C (Transaction List)
                ├── COTRN01C (Transaction View)
                ├── COTRN02C (Transaction Add)
                ├── CORPT00C (Transaction Reports)
                ├── COBIL00C (Bill Payment)
                └── COPAUS0C (Pending Auth View)

COCRDLIC (Card List)
    ├──XCTL──► COMEN01C  (return to menu via LIT-MENUPGM)
    └──XCTL──► [CCARD-NEXT-PROG] (detail navigation)
                ├── COCRDSLC (Card View)
                └── COCRDUPC (Card Update)

COCRDSLC ──XCTL──► [CDEMO-TO-PROGRAM] (return to caller)
COCRDUPC ──XCTL──► [CDEMO-TO-PROGRAM] (return to caller)
COACTUPC ──XCTL──► [CDEMO-TO-PROGRAM] (return to caller)
COACTVWC ──XCTL──► [CDEMO-TO-PROGRAM] (return to caller)
```

### 1.3 Complete Online Navigation Flow

```
┌─────────────┐
│  COSGN00C   │  Sign-on Screen
│  (Entry)    │
└──────┬──────┘
       │
       ├── Admin ──► ┌─────────────┐
       │             │  COADM01C   │  Admin Menu
       │             └──────┬──────┘
       │                    ├──► COUSR00C (User List)
       │                    ├──► COUSR01C (User Add)
       │                    ├──► COUSR02C (User Update)
       │                    ├──► COUSR03C (User Delete)
       │                    ├──► COTRTLIC (Trans Type List/DB2)
       │                    └──► COTRTUPC (Trans Type Maint/DB2)
       │
       └── User ───► ┌─────────────┐
                     │  COMEN01C   │  Main Menu
                     └──────┬──────┘
                            ├──► COACTVWC (Account View)
                            ├──► COACTUPC (Account Update)
                            ├──► COCRDLIC ──► COCRDSLC / COCRDUPC
                            ├──► COTRN00C (Transaction List)
                            ├──► COTRN01C (Transaction View)
                            ├──► COTRN02C (Transaction Add)
                            ├──► CORPT00C (Reports)
                            ├──► COBIL00C (Bill Payment)
                            └──► COPAUS0C ──► COPAUS1C ──► COPAUS2C
```

---

## 2. Dataset Lineage — JCL Jobs ↔ Programs ↔ Files

### 2.1 VSAM Dataset Catalog

| Dataset Name | Type | Key | Record Len | Defined By | Programs Using |
|-------------|------|-----|-----------|-----------|---------------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | KSDS | 11 bytes @ 0 | 300 | ACCTFILE.jcl | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, CBEXPORT, CBIMPORT, CBSTM03A |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | KSDS | 16 bytes @ 0 | 150 | CARDFILE.jcl | CBACT02C, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, CBEXPORT, CBIMPORT |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.AIX | AIX | (alternate) | — | CARDFILE.jcl | COCRDLIC (browse by alt key) |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | KSDS | 16 bytes @ 0 | 50 | XREFFILE.jcl | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COBIL00C, COTRN02C, CBEXPORT, CBIMPORT, CBSTM03A |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | KSDS | 9 bytes @ 0 | 500 | CUSTFILE.jcl | CBCUS01C, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, CBEXPORT, CBIMPORT, CBSTM03A |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | KSDS | 16 bytes @ 0 | 350 | TRANFILE.jcl | CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C, CBEXPORT, CBIMPORT, CBSTM03A |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | KSDS | 8 bytes @ 0 | 80 | DUSRSECJ.jcl | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| AWS.M2.CARDDEMO.TCATBALF.VSAM | KSDS | 17 bytes @ 0 | 50 | TCATBALF.jcl | CBACT04C, CBTRN02C |
| AWS.M2.CARDDEMO.DISCGRP.VSAM | KSDS | 16 bytes @ 0 | 50 | DISCGRP.jcl | CBACT04C |

### 2.2 Sequential (PS) Datasets

| Dataset Name | Format | Used By (Job/Program) | Direction |
|-------------|--------|----------------------|-----------|
| AWS.M2.CARDDEMO.ACCTDATA.PS | Fixed 300 | ACCTFILE.jcl (REPRO source) | Read |
| AWS.M2.CARDDEMO.CARDDATA.PS | Fixed 150 | CARDFILE.jcl (REPRO source) | Read |
| AWS.M2.CARDDEMO.CUSTDATA.PS | Fixed 500 | CUSTFILE.jcl (REPRO source) | Read |
| AWS.M2.CARDDEMO.CARDXREF.PS | Fixed 50 | XREFFILE.jcl (REPRO source) | Read |
| AWS.M2.CARDDEMO.DALYTRAN.PS | Fixed 350 | POSTTRAN.jcl/CBTRN02C | Read |
| AWS.M2.CARDDEMO.DALYTRAN.PS.INIT | Fixed 350 | Initial daily transaction seed | Read |
| AWS.M2.CARDDEMO.TRANTYPE.PS | Fixed 60 | TRANTYPE.jcl, DEFGDGD.jcl | Read |
| AWS.M2.CARDDEMO.TRANCATG.PS | Fixed 60 | TRANCATG.jcl, DEFGDGD.jcl | Read |
| AWS.M2.CARDDEMO.TCATBALF.PS | Fixed 50 | TCATBALF.jcl (REPRO source) | Read |
| AWS.M2.CARDDEMO.DISCGRP.PS | Fixed 50 | DISCGRP.jcl, DEFGDGD.jcl | Read |
| AWS.M2.CARDDEMO.USRSEC.PS | Fixed 80 | DUSRSECJ.jcl | Read |
| AWS.M2.CARDDEMO.STATEMNT.PS | Variable | CBSTM03A output, TXT2PDF1.JCL | Write/Read |
| AWS.M2.CARDDEMO.EXPORT.DATA.PS | Fixed 500 | CBEXPORT/CBIMPORT | Write/Read |
| AWS.M2.CARDDEMO.TRXFL.SEQ | Fixed 350 | CREASTMT.JCL (SORT output) | Write/Read |

### 2.3 GDG (Generation Data Group) Datasets

| GDG Base | Purpose | Defined By | Written By |
|----------|---------|-----------|-----------|
| AWS.M2.CARDDEMO.DALYREJS.GDG | Daily transaction rejects | DALYREJS.jcl | CBTRN02C |
| AWS.M2.CARDDEMO.TRANREPT.GDG | Transaction reports | REPTFILE.jcl | CBTRN03C |
| AWS.M2.CARDDEMO.TRANTYPE.BKUP | Transaction type backup | DEFGDGD.jcl | IEBGENER |
| AWS.M2.CARDDEMO.TRANCATG.PS.BKUP | Trans category backup | DEFGDGD.jcl | IEBGENER |
| AWS.M2.CARDDEMO.DISCGRP.BKUP | Disclosure group backup | DEFGDGD.jcl | IEBGENER |

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Processing Pipeline

```
┌────────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH PROCESSING PIPELINE                       │
└────────────────────────────────────────────────────────────────────────┘

Phase 1: CLOSE FILES
┌──────────┐
│CLOSEFIL  │  Close VSAM files in CICS region
│(SDSF)    │  ► CEMT SET FIL(...) CLO
└────┬─────┘
     │
     ▼
Phase 2: POST DAILY TRANSACTIONS
┌──────────┐     ┌──────────────┐     ┌───────────────┐
│DALYTRAN  │────►│ POSTTRAN.jcl │────►│   CBTRN02C    │
│(.PS)     │     │  PGM=CBTRN02C│     │               │
│(input)   │     └──────────────┘     │ Reads: DALYTRAN│
└──────────┘                          │ Reads: XREFFILE│
                                      │ Reads: ACCTFILE│
                                      │ Writes: TRANFILE│
                                      │ Writes: DALYREJS│
                                      │ Writes: TCATBALF│
                                      └───────┬────────┘
                                              │
                                              ▼
Phase 3: CALCULATE INTEREST
┌──────────┐     ┌──────────────┐     ┌───────────────┐
│TCATBALF  │────►│ INTCALC.jcl  │────►│   CBACT04C    │
│(input)   │     │ PGM=CBACT04C │     │               │
└──────────┘     └──────────────┘     │ Reads: TCATBALF│
                                      │ Reads: XREFFILE│
                                      │ Reads: DISCGRP │
                                      │ Reads: ACCTFILE│
                                      │ Writes: TRANSACT│
                                      └───────┬────────┘
                                              │
                                              ▼
Phase 4: GENERATE REPORTS
┌──────────┐     ┌──────────────┐     ┌───────────────┐
│TRANSACT  │────►│TRANREPT.jcl  │────►│   SORT        │──►┌──────────┐
│(VSAM)    │     │              │     │ (sequence)    │   │ CBTRN03C │
└──────────┘     └──────────────┘     └───────────────┘   │          │
                                                          │Reads: TRANFILE│
                                                          │Reads: CARDXREF│
                                                          │Reads: TRANTYPE│
                                                          │Reads: TRANCATG│
                                                          │Writes: TRANREPT│
                                                          └─────┬────┘
                                                                │
                                                                ▼
Phase 5: CREATE STATEMENTS (Monthly/Cycle)
┌──────────┐     ┌──────────────┐     ┌───────────────┐
│TRANSACT  │────►│CREASTMT.JCL  │────►│   SORT        │──►IDCAMS──►┌──────────┐
│(VSAM)    │     │              │     │ (extract/sort)│           │CBSTM03A  │
└──────────┘     └──────────────┘     └───────────────┘           │          │
                                                                  │Reads: TRNXFILE│
                                                                  │Reads: XREFFILE│
                                                                  │Reads: ACCTFILE│
                                                                  │Reads: CUSTFILE│
                                                                  │Writes: STATEMNT│
                                                                  │Calls: CBSTM03B│
                                                                  └─────┬────┘
                                                                        │
                                                                        ▼
Phase 6: CONVERT TO PDF (Optional)
┌──────────┐     ┌──────────────┐
│STATEMNT  │────►│TXT2PDF1.JCL  │──► PDF output
│(.PS)     │     │PGM=IKJEFT1B  │
└──────────┘     └──────────────┘

Phase 7: BACKUP & PURGE
┌──────────┐     ┌──────────────┐
│TRANSACT  │────►│ TRANBKP.jcl  │──► Backup to GDG, DELETE old records
│(VSAM)    │     │ IDCAMS       │
└──────────┘     └──────────────┘

Phase 8: REOPEN FILES
┌──────────┐
│ OPENFIL  │  Open VSAM files in CICS region
│ (SDSF)   │  ► CEMT SET FIL(...) OPE
└──────────┘
```

### 3.2 Branch Migration Pipeline

```
┌────────────────────────────────────────────────────────────────────────┐
│                    BRANCH MIGRATION PIPELINE                             │
└────────────────────────────────────────────────────────────────────────┘

EXPORT (Source Branch):
┌──────────┐     ┌──────────────┐     ┌───────────────┐
│All VSAM  │────►│CBEXPORT.jcl  │────►│   CBEXPORT    │──► EXPORT.DATA.PS
│files     │     │              │     │ Reads 5 files │    (500-byte records)
└──────────┘     └──────────────┘     └───────────────┘

                         ║ (FTP/network transfer)
                         ▼

IMPORT (Target Branch):
┌──────────────┐     ┌──────────────┐     ┌───────────────┐
│EXPORT.DATA.PS│────►│CBIMPORT.jcl  │────►│   CBIMPORT    │──► 5 output files
│(transferred) │     │              │     │ + ERROUT      │    + error log
└──────────────┘     └──────────────┘     └───────────────┘
```

### 3.3 File Definition Pipeline (Initial Setup)

```
Order of execution for initial environment setup:

1. DEFGDGB.jcl    ─── Define all GDG bases
2. ACCTFILE.jcl   ─── Define + load Account VSAM
3. CARDFILE.jcl   ─── Define + load Card VSAM + AIX
4. CUSTFILE.jcl   ─── Define + load Customer VSAM
5. XREFFILE.jcl   ─── Define + load Cross-Reference VSAM + AIX
6. TRANFILE.jcl   ─── Define + load Transaction VSAM + AIX
7. TRANIDX.jcl    ─── Define additional AIX on Transaction
8. TCATBALF.jcl   ─── Define + load Category Balance
9. DISCGRP.jcl    ─── Define + load Disclosure Group
10. TRANTYPE.jcl  ─── Define + load Transaction Type
11. TRANCATG.jcl  ─── Define + load Transaction Category
12. DUSRSECJ.jcl  ─── Define + load User Security
13. DALYREJS.jcl  ─── Define GDG for rejects
14. REPTFILE.jcl  ─── Define GDG for reports
15. CBADMCDJ.jcl  ─── Load CICS CSD resource definitions
16. OPENFIL.jcl   ─── Open all files in CICS
```

---

## 4. Cross-Reference: Programs ↔ Copybooks

| Copybook | Used By Programs |
|----------|-----------------|
| CVACT01Y | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COBIL00C, COTRN02C, CBEXPORT, CBIMPORT |
| CVACT02Y | CBACT02C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBEXPORT, CBIMPORT |
| CVACT03Y | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COBIL00C, CBEXPORT, CBIMPORT |
| CVCRD01Y | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |
| CVCUS01Y | CBCUS01C, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, CBEXPORT, CBIMPORT |
| CVTRA05Y | CBACT04C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, CBEXPORT, CBIMPORT |
| CVTRA06Y | CBTRN01C, CBTRN02C |
| CVTRA01Y | CBACT04C, CBTRN02C |
| CVTRA02Y | CBACT04C |
| CVTRA03Y | CBTRN03C |
| CVTRA04Y | CBTRN03C |
| CVTRA07Y | CBTRN03C |
| CVEXPORT | CBEXPORT, CBIMPORT |
| COCOM01Y | All online programs (16) |
| COTTL01Y | All online programs (16) |
| CSDAT01Y | All online programs (16) |
| CSMSG01Y | All online programs (16) |
| DFHAID | All online programs (16) |
| DFHBMSCA | All online programs (16) |
| CSUSR01Y | COSGN00C, COADM01C, COMEN01C, COUSR00C-03C |
| COADM02Y | COADM01C |
| COMEN02Y | COMEN01C |
| CSLKPCDY | COACTUPC |
| CSSETATY | COACTUPC (×39 via REPLACING) |
| CODATECN | CBACT01C |
| COSTM01 | CBSTM03A |
| CSUTLDPY | COACTUPC |
| CSUTLDWY | COACTUPC |
| CSSTRPFY | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |

---

## 5. Inter-Program Data Flow (COMMAREA)

All online CICS programs communicate via the **CARDDEMO-COMMAREA** (COCOM01Y.cpy):

```
COSGN00C ──[CDEMO-USER-ID, CDEMO-USER-TYPE]──► COADM01C / COMEN01C
COMEN01C ──[CDEMO-CUST-ID, CDEMO-ACCT-ID, CDEMO-CARD-NUM]──► Detail programs
COCRDLIC ──[CDEMO-CARD-NUM, CDEMO-ACCT-ID]──► COCRDSLC / COCRDUPC
COACTVWC ◄──[CDEMO-ACCT-ID]──── COMEN01C
COTRN00C ──[TRAN-ID via WS]──► COTRN01C
```

The COMMAREA carries:
- User identity (ID + type A/U)
- Navigation context (from/to program + transaction)
- Business context (customer ID, account ID, card number)
- UI state (last map, program context enter/re-enter)
