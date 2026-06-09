# CardDemo Dependency Map

> Program call graph, dataset lineage, and end-to-end batch pipeline flow for the CardDemo COBOL estate.

---

## 1. Online Program Call Graph (CICS XCTL / LINK Chains)

### 1.1 Navigation Tree

```
COSGN00C  (Sign-On — Entry Point)
│
├── Admin Path (CDEMO-USER-TYPE = 'A')
│   └── COADM01C  (Admin Menu Hub)
│       ├── [1] COUSR00C  (User List)
│       │       └── XCTL → COUSR02C  (User Update)
│       │       └── XCTL → COUSR03C  (User Delete)
│       ├── [2] COUSR01C  (User Add)
│       ├── [3] COUSR02C  (User Update)
│       ├── [4] COUSR03C  (User Delete)
│       ├── [5] COTRTLIC  (Transaction Type List — DB2)
│       └── [6] COTRTUPC  (Transaction Type Update — DB2)
│
└── User Path (CDEMO-USER-TYPE = 'U')
    └── COMEN01C  (Main Menu Hub — 11 targets)
        ├── [1]  COACTVWC  (Account View)
        ├── [2]  COACTUPC  (Account Update — 4,236 LOC)
        ├── [3]  COCRDLIC  (Credit Card List)
        │        └── XCTL → COCRDSLC  (Card Detail View)
        │        └── XCTL → COCRDUPC  (Card Update)
        ├── [4]  COCRDSLC  (Credit Card View)
        ├── [5]  COCRDUPC  (Credit Card Update)
        ├── [6]  COTRN00C  (Transaction List)
        │        └── XCTL → COTRN01C  (Transaction View)
        ├── [7]  COTRN01C  (Transaction View)
        ├── [8]  COTRN02C  (Transaction Add)
        ├── [9]  CORPT00C  (Report Request → submits batch JCL)
        ├── [10] COBIL00C  (Bill Payment)
        └── [11] COPAUS0C  (Auth Summary — IMS)
                 └── EXEC CICS LINK → COPAUS1C  (Auth List)
                          └── EXEC CICS LINK → COPAUS2C  (Auth Detail)
```

### 1.2 Inter-Program Transfer Details

| Source Program | Transfer Type | Target Program | Mechanism |
|---------------|---------------|----------------|-----------|
| COSGN00C | XCTL | COADM01C or COMEN01C | Based on CDEMO-USER-TYPE |
| COADM01C | XCTL | COUSR00C/01C/02C/03C, COTRTLIC, COTRTUPC | Via CDEMO-ADMIN-OPT-PGMNAME array |
| COMEN01C | XCTL | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C/01C/02C, CORPT00C, COBIL00C, COPAUS0C | Via CDEMO-MENU-OPT-PGMNAME array |
| COPAUS0C | LINK | COPAUS1C | EXEC CICS LINK (not XCTL) — returns |
| COPAUS1C | LINK | COPAUS2C | EXEC CICS LINK — returns |
| CORPT00C | CALL | CSUTLDTC | Date validation utility |
| COTRN02C | CALL | CSUTLDTC | Date validation utility |
| All CICS progs | XCTL | CDEMO-TO-PROGRAM | Return to calling program (via COMMAREA) |

### 1.3 Key Pattern: Hub-and-Spoke

- **COMEN01C** is the central hub with **11 outbound XCTL dependencies** — highest fan-out in the estate
- **COADM01C** is the admin hub with **6 outbound XCTL dependencies**
- All spoke programs return to their hub via `XCTL PROGRAM(CDEMO-TO-PROGRAM)` stored in COMMAREA

---

## 2. Batch Program Call Graph

### 2.1 CALL Dependencies

```
CBACT01C ──CALL──► COBDATFT  (Assembler: date formatting)
         ──CALL──► CEE3ABD   (LE: abnormal termination)

CBACT02C ──CALL──► CEE3ABD

CBACT03C ──CALL──► CEE3ABD

CBACT04C ──CALL──► CEE3ABD

CBCUS01C ──CALL──► CEE3ABD

CBEXPORT ──CALL──► CEE3ABD

CBIMPORT ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B  (Statement I/O sub-module)
         ──CALL──► CEE3ABD

CBTRN01C ──CALL──► CEE3ABD

CBTRN02C ──CALL──► CEE3ABD

CBTRN03C ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT   (Assembler: timed wait)

CSUTLDTC ──CALL──► CEEDAYS   (LE: date conversion)
```

### 2.2 IMS DL/I Call Graph

```
DBUNLDGS  ──CALL 'CBLTDLI'──► GN   (Get Next segment)
                             ► GNP  (Get Next within Parent)
                             ► ISRT (Insert into DB2 staging)

PAUDBLOD  ──CALL 'CBLTDLI'──► GU   (Get Unique — root segment)
                             ► ISRT (Insert child segments)

PAUDBUNL  ──CALL 'CBLTDLI'──► GN   (Get Next)
                             ► GNP  (Get Next within Parent)
```

### 2.3 MQ Call Graph

```
COPAUA0C ──CALL──► MQOPEN    (Open request queue)
         ──CALL──► MQGET     (Get auth request message)
         ──CALL──► MQPUT1    (Put auth response message)
         ──CALL──► MQCLOSE   (Close queues)

COACCT01 ──CALL──► MQOPEN ×3 (Request, Response, Error queues)
         ──CALL──► MQGET     (Get account query)
         ──CALL──► MQPUT ×2  (Put response + error)
         ──CALL──► MQCLOSE ×3

CODATE01 ──CALL──► MQOPEN ×3 (Request, Response, Error queues)
         ──CALL──► MQGET     (Get date conversion request)
         ──CALL──► MQPUT ×2  (Put response + error)
         ──CALL──► MQCLOSE ×3
```

### 2.4 External Dependency Summary

| External Module | Type | Called By | Java Replacement |
|----------------|------|-----------|-----------------|
| COBDATFT | Assembler | CBACT01C | DateFormatter utility class |
| CEE3ABD | LE Runtime | 10 batch programs | `System.exit()` / custom AbendHandler |
| CEEDAYS | LE Runtime | CSUTLDTC | `java.time.LocalDate` |
| MVSWAIT | Assembler | COBSWAIT | `Thread.sleep()` |
| MQOPEN/GET/PUT/CLOSE | MQ API | COPAUA0C, COACCT01, CODATE01 | Spring JMS / SQS adapter |
| CBLTDLI | IMS DL/I | DBUNLDGS, PAUDBLOD, PAUDBUNL | JPA Repository / SQL queries |

---

## 3. Dataset Lineage

### 3.1 VSAM File → Program Mapping

#### ACCTDATA.VSAM.KSDS (Account Master)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBACT04C (interest calc), COACTUPC (CICS update), COBIL00C (bill pay), CBTRN02C (posting), CBIMPORT (import) | Balance updates, account modifications |
| **Readers** | COACTVWC, COCRDSLC, COTRN02C, CORPT00C, CBSTM03A, CBACT01C, CBEXPORT, COPAUS0C, COACCT01 + more | Account lookups across online and batch |

#### CARDDATA.VSAM.KSDS (Card Master)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | COCRDUPC (CICS card update), CBIMPORT | Card maintenance |
| **Readers** | COCRDLIC (list), COCRDSLC (view), COACTVWC, CBACT02C, CBEXPORT | Card lookups |

#### CUSTDATA.VSAM.KSDS (Customer Master)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBIMPORT | Bulk load only |
| **Readers** | COACTUPC, COACTVWC, COCRDSLC, COBIL00C, CBSTM03A, CBCUS01C, CBEXPORT, COPAUS0C | Customer lookups |

#### CARDXREF.VSAM.KSDS (Card-Account Cross-Reference)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBIMPORT | Bulk load only |
| **Readers** | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN02C, COBIL00C, CBSTM03A, CBTRN02C, CBTRN03C, CBACT04C, CBEXPORT, COPAUS0C | Universal card→account resolution |

#### TRANSACT.VSAM.KSDS (Transaction Master)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBTRN02C (posting), COBIL00C (bill pay), COTRN02C (CICS add), CBIMPORT | Transaction creation |
| **Readers** | COTRN00C (list), COTRN01C (view), CBSTM03A (statements), CBTRN03C (report), CBEXPORT | Transaction lookups and reporting |

#### USRSEC.VSAM.KSDS (User Security)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | COUSR01C (add), COUSR02C (update), COUSR03C (delete) | Admin user management |
| **Readers** | COSGN00C (login), COUSR00C (list), COUSR02C (update), COUSR03C (delete) | Authentication and user browse |

#### TCATBALF.VSAM.KSDS (Transaction Category Balance)

| Operation | Programs | Context |
|-----------|----------|---------|
| **Writers** | CBTRN02C (posting updates), CBACT04C (interest updates) | Running balance maintenance |
| **Readers** | CBTRN02C, CBACT04C | Balance lookups during posting |

#### Reference Files (Read-Only in Batch)

| Dataset | Copybook | Programs |
|---------|----------|----------|
| DISCGRP.VSAM.KSDS | CVTRA02Y | CBACT04C (interest rate lookup) |
| TRANTYPE.VSAM.KSDS | CVTRA03Y | CBTRN03C (report type descriptions) |
| TRANCATG.VSAM.KSDS | CVTRA04Y | CBTRN03C (report category descriptions) |

### 3.2 JCL Job → Dataset Lineage

```
┌─────────────────────────────────────────────────────────┐
│                    DATA SETUP JOBS                       │
│                                                         │
│  ACCTFILE.jcl:  ACCTDATA.PS ─────► ACCTDATA.VSAM.KSDS │
│  CARDFILE.jcl:  CARDDATA.PS ─────► CARDDATA.VSAM.KSDS │
│  CUSTFILE.jcl:  CUSTDATA.PS ─────► CUSTDATA.VSAM.KSDS │
│  XREFFILE.jcl:  CARDXREF.PS ─────► CARDXREF.VSAM.KSDS│
│  TRANFILE.jcl:  DALYTRAN.PS.INIT ► TRANSACT.VSAM.KSDS │
│  TCATBALF.jcl:  TCATBALF.PS ─────► TCATBALF.VSAM.KSDS│
│  DISCGRP.jcl:   DISCGRP.PS ──────► DISCGRP.VSAM.KSDS │
│  TRANTYPE.jcl:  TRANTYPE.PS ─────► TRANTYPE.VSAM.KSDS│
│  TRANCATG.jcl:  TRANCATG.PS ─────► TRANCATG.VSAM.KSDS│
│  DUSRSECJ.jcl:  (inline data) ───► USRSEC.VSAM.KSDS  │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│                  BATCH PROCESSING                        │
│                                                         │
│  POSTTRAN.jcl (CBTRN02C):                               │
│    IN:  DALYTRAN.PS, XREFFILE, ACCTFILE, TCATBALF       │
│    OUT: TRANSACT.VSAM.KSDS, SYSTRAN(+1), DALYREJS(+1)  │
│                                                         │
│  INTCALC.jcl (CBACT04C):                                │
│    IN:  TCATBALF, XREFFILE, ACCTFILE, DISCGRP           │
│    OUT: SYSTRAN(+1) (interest txns)                     │
│                                                         │
│  COMBTRAN.jcl (SORT + IDCAMS):                          │
│    IN:  TRANSACT.BKUP(0), SYSTRAN(0)                    │
│    OUT: TRANSACT.COMBINED(+1) → TRANSACT.VSAM.KSDS     │
│                                                         │
│  CREASTMT.JCL (SORT → CBSTM03A):                       │
│    IN:  TRANSACT.VSAM.KSDS, XREFFILE, ACCTFILE, CUSTFILE│
│    OUT: STATEMNT.PS, STATEMNT.HTML                      │
│                                                         │
│  TRANREPT.jcl (REPROC → SORT → CBTRN03C):              │
│    IN:  TRANSACT.VSAM.KSDS, CARDXREF, TRANTYPE, TRANCATG│
│    OUT: REPTFILE                                        │
│                                                         │
│  TRANBKP.jcl (REPROC + IDCAMS):                         │
│    IN:  TRANSACT.VSAM.KSDS                              │
│    OUT: TRANSACT.BKUP(+1)                               │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│               EXPORT / IMPORT                            │
│                                                         │
│  CBEXPORT.jcl (CBEXPORT):                               │
│    IN:  CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE│
│    OUT: EXPORT.DATA                                     │
│                                                         │
│  CBIMPORT.jcl (CBIMPORT):                               │
│    IN:  EXPORT.DATA                                     │
│    OUT: CUSTDATA.IMPORT, ACCTDATA.IMPORT,               │
│         CARDXREF.IMPORT, TRANSACT.IMPORT, IMPORT.ERRORS │
└─────────────────────────────────────────────────────────┘
```

---

## 4. End-to-End Batch Pipeline Flow

### 4.1 Daily Batch Cycle

```
 ╔══════════════════════════════════════════════════════════════╗
 ║                    DAILY BATCH PIPELINE                      ║
 ║                                                              ║
 ║  Step 1: POSTTRAN (CBTRN02C)                                 ║
 ║  ┌──────────────────────────────────────────────────┐        ║
 ║  │ Read DALYTRAN.PS (daily input feed)               │        ║
 ║  │ For each transaction:                             │        ║
 ║  │   ► Validate card via CARDXREF lookup             │        ║
 ║  │   ► Read account from ACCTFILE                    │        ║
 ║  │   ► Update ACCT-CURR-BAL, ACCT-CURR-CYC-DEBIT    │        ║
 ║  │   ► Update TCATBALF (category running balance)    │        ║
 ║  │   ► Write to TRANSACT master                      │        ║
 ║  │   ► Write rejects to DALYREJS GDG                 │        ║
 ║  └──────────────────────────────────────────────────┘        ║
 ║                          │                                    ║
 ║                          ▼                                    ║
 ║  Step 2: INTCALC (CBACT04C)                                  ║
 ║  ┌──────────────────────────────────────────────────┐        ║
 ║  │ Read TCATBALF (category balances)                 │        ║
 ║  │ For each account+category:                        │        ║
 ║  │   ► Lookup interest rate from DISCGRP             │        ║
 ║  │   ► Calculate interest = balance × rate / 365     │        ║
 ║  │   ► Write interest transaction to SYSTRAN GDG     │        ║
 ║  │   ► Update ACCTFILE balance with interest charge  │        ║
 ║  └──────────────────────────────────────────────────┘        ║
 ║                          │                                    ║
 ║                          ▼                                    ║
 ║  Step 3: COMBTRAN (SORT + IDCAMS)                             ║
 ║  ┌──────────────────────────────────────────────────┐        ║
 ║  │ Merge TRANSACT.BKUP + SYSTRAN → COMBINED         │        ║
 ║  │ Reload TRANSACT.VSAM.KSDS from COMBINED           │        ║
 ║  └──────────────────────────────────────────────────┘        ║
 ║                          │                                    ║
 ║                          ▼                                    ║
 ║  Step 4: CREASTMT (SORT → CBSTM03A → CBSTM03B)              ║
 ║  ┌──────────────────────────────────────────────────┐        ║
 ║  │ Sort TRANSACT by card+date                        │        ║
 ║  │ For each customer/account:                        │        ║
 ║  │   ► Lookup customer info from CUSTFILE             │        ║
 ║  │   ► Lookup account info from ACCTFILE              │        ║
 ║  │   ► CALL CBSTM03B to format statement lines        │        ║
 ║  │   ► Write STATEMNT.PS (text) + STATEMNT.HTML       │        ║
 ║  └──────────────────────────────────────────────────┘        ║
 ║                          │                                    ║
 ║                          ▼                                    ║
 ║  Step 5: TRANREPT (REPROC → SORT → CBTRN03C)                 ║
 ║  ┌──────────────────────────────────────────────────┐        ║
 ║  │ Backup TRANSACT → BKUP GDG                        │        ║
 ║  │ Sort transactions by account+type                  │        ║
 ║  │ For each transaction:                              │        ║
 ║  │   ► Lookup type desc from TRANTYPE                 │        ║
 ║  │   ► Lookup category desc from TRANCATG             │        ║
 ║  │   ► Format report line (amount, totals)            │        ║
 ║  │   ► Write REPTFILE with page/account/grand totals  │        ║
 ║  └──────────────────────────────────────────────────┘        ║
 ║                          │                                    ║
 ║                          ▼                                    ║
 ║  Step 6: TRANBKP (REPROC + IDCAMS)                           ║
 ║  ┌──────────────────────────────────────────────────┐        ║
 ║  │ Backup TRANSACT → TRANSACT.BKUP(+1) GDG           │        ║
 ║  │ Clear and redefine TRANSACT.VSAM.KSDS for next day │        ║
 ║  └──────────────────────────────────────────────────┘        ║
 ╚══════════════════════════════════════════════════════════════╝
```

### 4.2 Dataset Flow Through Pipeline

```
DALYTRAN.PS ───► POSTTRAN ───► TRANSACT.VSAM.KSDS ───┬──► CREASTMT ──► STATEMNT.PS
                    │                                  │                   STATEMNT.HTML
                    ├──► ACCTFILE (balance update)      │
                    ├──► TCATBALF (cat bal update)      ├──► TRANREPT ──► REPTFILE
                    └──► DALYREJS(+1) (rejects)        │
                                                       └──► TRANBKP ───► TRANSACT.BKUP(+1)
                 INTCALC ───► SYSTRAN(+1)
                    │
                    └──► ACCTFILE (interest charges)

                 COMBTRAN: TRANSACT.BKUP + SYSTRAN → TRANSACT.COMBINED → TRANSACT.VSAM.KSDS
```

---

## 5. Dependency Metrics Summary

### 5.1 Most-Connected Programs (by dependency count)

| Rank | Program | Outbound Deps | Inbound Deps | Total | Role |
|------|---------|--------------|--------------|-------|------|
| 1 | COMEN01C | 11 | 1 | 12 | Main menu hub |
| 2 | COADM01C | 6 | 1 | 7 | Admin menu hub |
| 3 | CBTRN02C | 0 | 1 | 6 | Writes 4 files, reads 4 files |
| 4 | CBSTM03A | 1 (CBSTM03B) | 1 | 6 | Reads 4 files, writes 2 |
| 5 | CBACT04C | 0 | 1 | 5 | Reads 4 files, writes 2 |
| 6 | COACTUPC | 0 | 1 | 4 | Reads/writes 3 VSAM files |
| 7 | CBEXPORT | 0 | 1 | 6 | Reads 5 files, writes 1 |
| 8 | COPAUA0C | 0 | 0 | 4 | MQ + IMS + CICS (3 subsystems) |

### 5.2 Most-Accessed Datasets

| Rank | Dataset | Programs Accessing | Writers | Readers |
|------|---------|-------------------|---------|---------|
| 1 | CARDXREF.VSAM.KSDS | 14 | 1 | 13 |
| 2 | ACCTDATA.VSAM.KSDS | 14 | 5 | 13 |
| 3 | CARDDATA.VSAM.KSDS | 8 | 2 | 8 |
| 4 | CUSTDATA.VSAM.KSDS | 7 | 1 | 6 |
| 5 | TRANSACT.VSAM.KSDS | 9 | 4 | 5 |
| 6 | USRSEC.VSAM.KSDS | 6 | 3 | 5 |
| 7 | TCATBALF.VSAM.KSDS | 3 | 2 | 2 |

### 5.3 Modernization Migration Units (programs that must migrate together)

| Unit | Programs | Reason |
|------|----------|--------|
| Auth Chain | COPAUS0C → COPAUS1C → COPAUS2C | CICS LINK chain; shared COMMAREA |
| Auth+MQ | COPAUA0C + COACCT01 + CODATE01 | Shared MQ queue infrastructure |
| Statement | CBSTM03A + CBSTM03B | CALL dependency |
| User CRUD | COUSR00C + COUSR01C + COUSR02C + COUSR03C | Shared USRSEC file, admin flow |
| Card Suite | COCRDLIC + COCRDSLC + COCRDUPC | Shared CARDFILE, navigation chain |
| Tran Suite | COTRN00C + COTRN01C + COTRN02C | Shared TRANSACT, navigation chain |
| IMS Batch | PAUDBLOD + PAUDBUNL + DBUNLDGS | Shared IMS PCBs and segments |
| Daily Batch | CBTRN02C → CBACT04C → CBSTM03A → CBTRN03C | Sequential pipeline dependency |
