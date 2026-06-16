# DEPENDENCY MAP — CardDemo COBOL Estate

> **Programs Analyzed:** 44 | **Inter-program Links:** ~60 | **VSAM Files:** 8 | **DB2 Tables:** 2 | **IMS Segments:** 2

---

## 1. Online Call Graph (CICS XCTL/LINK Chains)

```
COSGN00C (Sign-On — Entry Point)
│
├── [Admin Path] ──XCTL──► COADM01C (Admin Menu Hub)
│   │
│   ├── Opt 1 ──XCTL──► COUSR00C (User List)
│   │                      ├──XCTL──► COUSR02C (User Update)
│   │                      └──XCTL──► COUSR03C (User Delete)
│   ├── Opt 2 ──XCTL──► COUSR01C (User Add)
│   ├── Opt 3 ──XCTL──► COUSR02C (User Update)
│   ├── Opt 4 ──XCTL──► COUSR03C (User Delete)
│   ├── Opt 5 ──XCTL──► COTRTLIC (Tran Type List — DB2)
│   └── Opt 6 ──XCTL──► COTRTUPC (Tran Type Update — DB2)
│
└── [User Path] ──XCTL──► COMEN01C (Main Menu Hub — 11 targets)
    │
    ├── Opt 1  ──XCTL──► COACTVWC (Account View)
    ├── Opt 2  ──XCTL──► COACTUPC (Account Update — 4,236 LOC)
    │                     └──CALL──► CSUTLDTC (Date Validation)
    ├── Opt 3  ──XCTL──► COCRDLIC (Card List)
    │                      ├──XCTL──► COCRDSLC (Card View)
    │                      └──XCTL──► COCRDUPC (Card Update)
    ├── Opt 6  ──XCTL──► COTRN00C (Transaction List)
    │                      └──XCTL──► COTRN01C (Transaction View)
    ├── Opt 8  ──XCTL──► COTRN02C (Transaction Add)
    │                     └──CALL──► CSUTLDTC (Date Validation)
    ├── Opt 9  ──XCTL──► COBIL00C (Bill Payment)
    ├── Opt 10 ──XCTL──► CORPT00C (Report Request)
    │                     └──WRITEQ TD──► submits batch JCL
    └── Opt 11 ──XCTL──► COPAUS0C (Auth Summary — IMS)
                           └──XCTL──► COPAUS1C (Auth Detail)
                                       └──LINK──► COPAUS2C (Fraud Mark — DB2)
```

### Key Navigation Patterns

| Pattern | Description | Programs |
|---------|-------------|----------|
| **Hub-and-Spoke** | Central menu routes to all child screens | COMEN01C (11 targets), COADM01C (6 targets) |
| **List → Detail** | Browse list, select item, view/edit detail | COCRDLIC→COCRDSLC/COCRDUPC, COTRN00C→COTRN01C, COUSR00C→COUSR02C/03C |
| **IMS Chain** | Linked sequence migrated as a unit | COPAUS0C→COPAUS1C→COPAUS2C (XCTL+LINK) |
| **All programs return to** | Every screen can navigate back to its hub | COMEN01C or COADM01C via CDEMO-TO-PROGRAM |

---

## 2. Batch Call Graph

```
CBACT01C ──CALL──► COBDATFT (Assembler: date formatting)
CBACT02C ──CALL──► CEE3ABD  (LE: abnormal termination)
CBACT03C ──CALL──► CEE3ABD  (LE: abnormal termination)
CBCUS01C ──CALL──► CEE3ABD  (LE: abnormal termination)
CBACT04C ──────── (self-contained; no external calls)
CBTRN01C ──────── (self-contained)
CBTRN02C ──────── (self-contained)
CBTRN03C ──────── (self-contained)
CBEXPORT ──────── (self-contained)
CBIMPORT ──────── (self-contained)
COBSWAIT ──CALL──► MVSWAIT  (Assembler: centisecond wait)
CSUTLDTC ──CALL──► CEEDAYS  (LE: date conversion API)

Sub-Application Batch:
CBPAUP0C ──DLI──► IMS database (GN, GNP, DLET, CHKP)
COBTUPDT ──SQL──► DB2 CARDDEMO.TRANSACTION_TYPE (INSERT, UPDATE, DELETE)
```

### External Dependencies (Assembler/LE)

| Dependency | Type | Called By | Java Replacement |
|------------|------|-----------|-----------------|
| COBDATFT | Assembler date formatter | CBACT01C | `java.time.format.DateTimeFormatter` |
| MVSWAIT | Assembler wait routine | COBSWAIT | `Thread.sleep()` |
| CEE3ABD | LE abnormal termination | CBACT02C, CBACT03C, CBCUS01C | `throw new RuntimeException()` |
| CEEDAYS | LE date-to-Lilian conversion | CSUTLDTC | `java.time.LocalDate.toEpochDay()` |

---

## 3. VSAM File Dependency Matrix

### Which Programs Touch Which Files

| VSAM File | Dataset Name | Readers | Writers | RW (Read-Update-Rewrite) |
|-----------|-------------|---------|---------|--------------------------|
| **ACCTFILE** | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | CBACT01C, CBACT04C, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C, CBTRN01C, CBTRN02C, CBEXPORT | CBIMPORT | COACTUPC, CBACT04C, COBIL00C, CBTRN02C |
| **CARDFILE** | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CBACT02C, COACTVWC, COCRDLIC, COCRDSLC, COPAUS0C, CBEXPORT | CBIMPORT | COCRDUPC |
| **CUSTFILE** | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CBCUS01C, COACTVWC, COACTUPC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, CBEXPORT | CBIMPORT | COACTUPC |
| **CARDXREF** | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | CBACT03C, CBACT04C, COACTVWC, COACTUPC, COCRDLIC, COTRN02C, COBIL00C, COPAUA0C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT | CBIMPORT | — |
| **TRANSACT** | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | COTRN00C, COTRN01C, CBTRN03C, CBEXPORT | COTRN02C, COBIL00C, CBIMPORT | CBTRN02C |
| **USRSEC** | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | COSGN00C, COUSR00C, COUSR02C, COUSR03C | COUSR01C | COUSR02C |
| **TCATBALF** | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | CBACT04C | — | CBTRN02C |
| **DISCGRP** | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | CBACT04C | — | — |

### File Contention Hotspots

| File | Concurrent Access Risk | Note |
|------|----------------------|------|
| **ACCTFILE** | **HIGH** — 13 reader programs + 4 read-write | Most shared resource; 5 programs write during dual-write migration phase |
| **CARDXREF** | **HIGH** — 12 reader programs | Read-heavy; alternate index adds complexity |
| **CUSTFILE** | MEDIUM — 8 readers + 1 read-write | COACTUPC rewrites customer records |
| **TRANSACT** | MEDIUM — sequential + KSDS access | Batch writes (CBTRN02C) and online reads (COTRN00C) |

---

## 4. DB2 Table Dependencies

| Table | Programs | Operations |
|-------|----------|------------|
| CARDDEMO.TRANSACTION_TYPE | COTRTLIC (SELECT, cursor FETCH, DELETE), COTRTUPC (SELECT, UPDATE, DELETE), COBTUPDT (INSERT, UPDATE, DELETE) | Full CRUD |
| CARDDEMO.TRANSACTION_CATEGORY | COTRTUPC (SELECT, DELETE — cascading with type) | Read + cascading delete |
| *(Fraud table)* | COPAUS2C (INSERT via EXEC SQL) | Insert-only |

**DB2 includes used:** `DCLTRTYP` (TRANSACTION_TYPE DCLGEN), `DCLTRCAT` (TRANSACTION_CATEGORY DCLGEN), `SQLCA` (SQL return codes)

---

## 5. IMS Database Dependencies

| IMS DB | Segments | Programs | DL/I Calls |
|--------|----------|----------|------------|
| DBPAUTP0 (Pending Auth) | CIPAUSMY (Summary — parent), CIPAUDTY (Detail — child) | CBPAUP0C (GN, GNP, DLET, CHKP), COPAUA0C (SCHD, GU, TERM), COPAUS0C (GU, GNP, SCHD, TERM), COPAUS1C (GU, GNP, REPL, SCHD, TERM) | Full CRUD |

**IMS PCB:** `PAUT-PCB-NUM` — shared PCB index across all authorization programs

---

## 6. MQ (Message Queue) Dependencies

| Queue | Direction | Program | Operations |
|-------|-----------|---------|------------|
| Authorization Request Queue | Inbound | COPAUA0C | MQOPEN, MQGET |
| Authorization Reply Queue | Outbound | COPAUA0C | MQPUT1 |

**MQ Copybooks Used:** CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV
**Message Formats:** CCPAURQY (request), CCPAURLY (response), CCPAUERY (error)

---

## 7. Dataset Lineage — JCL → Program → File

### Data Load Pipeline (One-time setup)

```
ACCTFILE.jcl ──IDCAMS──► AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS
                          (source: .ACCTDATA.PS flat file)
CARDFILE.jcl ──IDCAMS──► AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS + AIX
                          (source: .CARDDATA.PS flat file)
CUSTFILE.jcl ──IDCAMS──► AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS
                          (source: .CUSTDATA.PS flat file)
DUSRSECJ.jcl ──IEBGENER+IDCAMS──► AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS
                          (source: in-stream user data)
DISCGRP.jcl  ──IDCAMS──► AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS
TCATBALF.jcl ──IDCAMS──► AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS
```

### Daily Batch Pipeline (Production cycle)

```
                   AWS.M2.CARDDEMO.DALYTRAN.PS
                              │
Step 1: POSTTRAN.jcl          ▼
        PGM=CBTRN02C ──R──► DALYTRAN (daily input)
                      ──R──► XREFFILE (card-to-account lookup)
                      ──R──► ACCTFILE (account validation)
                      ──RW─► TCATBALF (update category balances)
                      ──W──► TRANSACT (post to master)
                      ──W──► DALYREJS (rejected transactions)
                              │
Step 2: INTCALC.jcl           ▼
        PGM=CBACT04C ──R──► TCATBALF (category balances)
                      ──R──► XREFFILE (card-to-account)
                      ──R──► DISCGRP  (interest rates)
                      ──RW─► ACCTFILE (update balances)
                      ──W──► SYSTRAN  (system-generated transactions GDG)
                              │
Step 3: COMBTRAN.jcl          ▼
        PGM=SORT     ──R──► SYSTRAN + TRANSACT.BKUP
                      ──W──► TRANSACT.COMBINED
                              │
Step 4: TRANREPT.jcl          ▼
        PGM=CBTRN03C ──R──► TRANSACT (transaction master)
                      ──R──► XREFFILE (card-to-account)
                      ──R──► TRANTYPE (type descriptions)
                      ──R──► TRANCATG (category descriptions)
                      ──W──► REPTFILE (GDG — transaction report)
```

### Data Export/Import Pipeline

```
CBEXPORT.jcl
  PGM=CBEXPORT ──R──► CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE
                ──W──► EXPFILE (VSAM KSDS — multi-record export)
                              │
CBIMPORT.jcl                  ▼
  PGM=CBIMPORT ──R──► EXPFILE
                ──W──► CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT
```

### Backup / Maintenance Pipeline

```
TRANBKP.jcl ──IDCAMS──► REPRO TRANSACT → TRANSACT.BKUP(+1)
PRTCATBL.jcl ──IDCAMS──► REPRO TCATBALF → TCATBALF.BKUP(+1)
                         SORT → TCATBALF.REPT (formatted report)
```

---

## 8. GDG (Generation Data Group) Lineage

| GDG Base | Defined By | Written By | Read By | Generations |
|----------|-----------|-----------|---------|-------------|
| AWS.M2.CARDDEMO.TRANSACT.BKUP | DEFGDGB | TRANBKP | COMBTRAN | 10 |
| AWS.M2.CARDDEMO.TRANSACT.DALY | DEFGDGB | — | — | 10 |
| AWS.M2.CARDDEMO.TRANREPT | DEFGDGB, REPTFILE | CBTRN03C | — | 10 |
| AWS.M2.CARDDEMO.TCATBALF.BKUP | DEFGDGB | PRTCATBL | PRTCATBL | 10 |
| AWS.M2.CARDDEMO.SYSTRAN | DEFGDGB | CBACT04C | COMBTRAN | 10 |
| AWS.M2.CARDDEMO.TRANSACT.COMBINED | DEFGDGB | COMBTRAN | — | 10 |
| AWS.M2.CARDDEMO.DALYREJS | DALYREJS | CBTRN02C | — | — |
| AWS.M2.CARDDEMO.TRANTYPE.BKUP | DEFGDGD | DEFGDGD | — | 10 |
| AWS.M2.CARDDEMO.TRANCATG.PS.BKUP | DEFGDGD | DEFGDGD | — | 10 |
| AWS.M2.CARDDEMO.DISCGRP.BKUP | DEFGDGD | DEFGDGD | — | 10 |

---

## 9. Copybook Dependency Matrix (Top 15 Most-Referenced)

| Copybook | Referenced By (count) | Programs |
|----------|----------------------|----------|
| COCOM01Y | 21 | All CICS programs — COMMAREA structure |
| DFHAID | 20 | All CICS programs — AID key definitions |
| DFHBMSCA | 20 | All CICS programs — BMS symbolic attributes |
| COTTL01Y | 20 | All CICS programs — screen titles |
| CSDAT01Y | 20 | All CICS programs — date/time working storage |
| CSMSG01Y | 20 | All CICS programs — common messages |
| CSUSR01Y | 12 | User-context programs — user security record |
| CVCRD01Y | 9 | Card/account programs — card work areas |
| CVACT01Y | 8 | Account programs — account record |
| CVACT03Y | 8 | Cross-reference programs — XREF record |
| CVTRA05Y | 7 | Transaction programs — transaction record |
| CVCUS01Y | 6 | Customer-touching programs — customer record |
| CVACT02Y | 6 | Card programs — card record |
| CSMSG02Y | 5 | Programs with ABEND handling |
| CIPAUSMY/CIPAUDTY | 5 | All authorization module programs |

---

## 10. Microservice Boundary Recommendations

Based on the dependency analysis, the following microservice boundaries minimize cross-service data coupling:

```
┌─────────────────────────────────────────────────────────┐
│                    API Gateway                           │
│                  (replaces BMS maps)                     │
└───┬────────┬────────┬─────────┬─────────┬──────────┬───┘
    │        │        │         │         │          │
    ▼        ▼        ▼         ▼         ▼          ▼
┌──────┐ ┌──────┐ ┌───────┐ ┌──────┐ ┌───────┐ ┌────────┐
│ Auth │ │ Acct │ │ Card  │ │ Txn  │ │ Report│ │TranType│
│ Svc  │ │ Svc  │ │ Svc   │ │ Svc  │ │ Svc   │ │  Svc   │
│      │ │      │ │       │ │      │ │       │ │ (DB2)  │
│COSGN │ │COACTU│ │COCRDLI│ │COTRN │ │CORPT00│ │COTRTLI │
│COUSR │ │COACTV│ │COCRDS │ │COTRN │ │CBTRN03│ │COTRTU  │
│COADM │ │COACC │ │COCRDU │ │COBIL │ │       │ │COBTUPT │
│COMEN │ │      │ │       │ │CBTRN │ │       │ │        │
│      │ │      │ │       │ │CBACT4│ │       │ │        │
└──┬───┘ └──┬───┘ └───┬───┘ └──┬───┘ └───┬───┘ └────────┘
   │        │         │        │         │
   ▼        ▼         ▼        ▼         ▼
┌─────────────────────────────────────────────────────────┐
│              PostgreSQL (Aurora) — Schema-per-Service     │
│   users | accounts,customers | cards,xref | transactions │
└─────────────────────────────────────────────────────────┘
```

### Cross-Service Data Flows

| From | To | Data Exchanged | Current Mechanism | Future Mechanism |
|------|----|---------------|-------------------|-----------------|
| Card Svc → Account Svc | Account lookup for card ops | CARDXREF → ACCTFILE | VSAM READ | REST API call |
| Transaction Svc → Account Svc | Balance update on payment | ACCTFILE REWRITE | VSAM RW | Synchronous REST or event |
| Auth Svc → Account Svc | Credit check during auth | ACCTFILE READ | CICS READ | REST API call |
| Report Svc → Transaction Svc | Transaction data for reports | TRANSACT READ | VSAM READ | Database query (read replica) |
| Auth Decision → IMS DB | Auth message lookup | DL/I calls | IMS PCB | Spring Data JPA |
| Auth Decision → MQ | Request/reply queue | MQGET/MQPUT | IBM MQ | Amazon SQS FIFO |
