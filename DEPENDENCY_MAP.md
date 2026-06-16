# DEPENDENCY MAP — CardDemo COBOL Estate

> **Generated:** 2026-06-16 | **Programs:** 44 | **JCL Jobs:** 46 | **VSAM Files:** 12+

---

## Table of Contents

1. [Online Navigation Call Graph (CICS XCTL)](#1-online-navigation-call-graph-cics-xctl)
2. [Batch Program Call Graph](#2-batch-program-call-graph)
3. [External / Assembler Dependencies](#3-external--assembler-dependencies)
4. [VSAM Dataset Lineage](#4-vsam-dataset-lineage)
5. [JCL → Program → File Mapping](#5-jcl--program--file-mapping)
6. [End-to-End Batch Pipeline Flow](#6-end-to-end-batch-pipeline-flow)
7. [Copybook Dependency Matrix](#7-copybook-dependency-matrix)
8. [Suggested Microservice Boundaries](#8-suggested-microservice-boundaries)

---

## 1. Online Navigation Call Graph (CICS XCTL)

Programs are connected via `EXEC CICS XCTL` (transfer control — no return) and `EXEC CICS LINK` (subroutine call — returns).

```
COSGN00C (Sign-On — Entry Point, TRANSID CC00)
│
├──► [SEC-USR-TYPE = 'A'] ──XCTL──► COADM01C (Admin Menu)
│                                     ├──XCTL──► COUSR00C (User List)
│                                     │            ├──XCTL──► COUSR02C (User Update)
│                                     │            └──XCTL──► COUSR03C (User Delete)
│                                     ├──XCTL──► COUSR01C (User Add)
│                                     ├──XCTL──► COTRTLIC (Tran Type List — DB2)
│                                     │            └──XCTL──► COTRTUPC (Tran Type Update — DB2)
│                                     └──XCTL──► back to COMEN01C options
│
└──► [SEC-USR-TYPE = 'U'] ──XCTL──► COMEN01C (Main Menu — Hub, 11 XCTL targets)
                                      │
                                      ├──XCTL──► COACTVWC (Account View)
                                      ├──XCTL──► COACTUPC (Account Update — 4,236 LOC)
                                      ├──XCTL──► COCRDLIC (Card List)
                                      │            ├──XCTL──► COCRDSLC (Card View)
                                      │            └──XCTL──► COCRDUPC (Card Update)
                                      ├──XCTL──► COTRN00C (Transaction List)
                                      │            └──XCTL──► COTRN01C (Transaction View)
                                      ├──XCTL──► COTRN02C (Transaction Add)
                                      ├──XCTL──► CORPT00C (Report Request)
                                      │            └──WRITEQ TD──► INTRDRJ1/J2 (batch JCL)
                                      ├──XCTL──► COBIL00C (Bill Payment)
                                      └──XCTL──► COPAUS0C (Auth Summary — IMS)
                                                   └──XCTL──► COPAUS1C (Auth Detail — IMS)
                                                                └──LINK──► COPAUS2C (Fraud Mark — DB2)
```

### XCTL Adjacency List

| Source Program | Target Program(s) | Mechanism |
|----------------|-------------------|-----------|
| COSGN00C | COADM01C, COMEN01C | XCTL (conditional on user type) |
| COADM01C | COUSR00C, COUSR01C, COTRTLIC, COTRTUPC | XCTL |
| COMEN01C | COACTVWC, COACTUPC, COCRDLIC, COTRN00C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C | XCTL |
| COCRDLIC | COMEN01C, COCRDSLC, COCRDUPC | XCTL |
| COCRDSLC | COCRDLIC | XCTL (back) |
| COCRDUPC | COCRDLIC | XCTL (back) |
| COACTVWC | COMEN01C | XCTL (back) |
| COACTUPC | COMEN01C | XCTL (back) |
| COTRN00C | COTRN01C, COMEN01C | XCTL |
| COTRN01C | COTRN00C | XCTL (back) |
| COTRN02C | COMEN01C | XCTL (back) |
| COBIL00C | COMEN01C | XCTL (back) |
| CORPT00C | COMEN01C | XCTL (back) |
| COUSR00C | COUSR02C, COUSR03C, COADM01C | XCTL |
| COUSR01C | COADM01C | XCTL (back) |
| COUSR02C | COUSR00C | XCTL (back) |
| COUSR03C | COUSR00C | XCTL (back) |
| COPAUS0C | COPAUS1C, COMEN01C | XCTL |
| COPAUS1C | COPAUS0C, COPAUS2C | XCTL / LINK |
| COTRTLIC | COTRTUPC, COADM01C | XCTL |
| COTRTUPC | COTRTLIC | XCTL (back) |

---

## 2. Batch Program Call Graph

```
CBACT01C ──CALL──► COBDATFT (assembler date formatter)
           ──CALL──► CEE3ABD (LE abnormal termination)

CBACT02C ──CALL──► CEE3ABD

CBACT03C ──CALL──► CEE3ABD

CBACT04C ──CALL──► CEE3ABD

CBCUS01C ──CALL──► CEE3ABD

CBEXPORT ──CALL──► CEE3ABD

CBIMPORT ──CALL──► CEE3ABD

CBTRN01C ──CALL──► CEE3ABD

CBTRN02C ──CALL──► CEE3ABD

CBTRN03C ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B (file I/O subroutine)

COBSWAIT ──CALL──► MVSWAIT (assembler wait)

CSUTLDTC ──CALL──► CEEDAYS (LE date conversion)

CORPT00C ──CALL──► CSUTLDTC (date utility)

COTRN02C ──CALL──► CSUTLDTC (date utility)
```

### IMS DL/I Call Graph (Authorization Module)

```
COPAUA0C ──DL/I──► PAUTH IMS DB (GU, SCHD, TERM)
           ──MQ───► Auth Request Queue (MQGET)
           ──MQ───► Auth Response Queue (MQPUT1)

COPAUS0C ──DL/I──► PAUTH IMS DB (GU, GNP — browse summary)

COPAUS1C ──DL/I──► PAUTH IMS DB (GU, GNP, REPL — view/update detail)

CBPAUP0C ──DL/I──► PAUTH IMS DB (GN, GNP, DLET — purge expired)

PAUDBLOD ──DL/I──► PAUTH IMS DB (ISRT, GU — initial load)

PAUDBUNL ──DL/I──► PAUTH IMS DB (GN, GNP — unload)

DBUNLDGS ──DL/I──► PAUTH IMS DB (GN, GNP)
          ──GSAM──► Sequential output (ISRT)
```

---

## 3. External / Assembler Dependencies

| Called Module | Type | Calling Program(s) | Purpose | Modernization Action |
|---------------|------|---------------------|---------|----------------------|
| `COBDATFT` | Assembler | CBACT01C | Date formatting | Replace with `java.time.format.DateTimeFormatter` |
| `CEE3ABD` | LE Runtime | CBACT01C–04C, CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C–03C | Abnormal termination | Replace with `throw new RuntimeException()` or `System.exit()` |
| `CEEDAYS` | LE Runtime | CSUTLDTC | Julian-to-Lilian date conversion | Replace with `java.time.LocalDate` |
| `MVSWAIT` | Assembler | COBSWAIT | Timed wait / delay | Replace with `Thread.sleep()` |
| `DSNTIAC` | DB2 Utility | CSDB2RPY (via COTRTLIC, COTRTUPC) | Format DB2 error messages | Replace with JDBC `SQLException.getMessage()` |
| `DFHCSDUP` | CICS Utility | CBADMCDJ (JCL) | CSD resource definition | N/A — deployment config |
| `DFSRRC00` | IMS Region Controller | CBPAUP0J, LOADPADB, UNLDPADB, etc. | IMS BMP execution | Replace with Spring Batch job launcher |

---

## 4. VSAM Dataset Lineage

### File → Program Access Matrix

| VSAM File (Dataset) | Key | RECLN | Programs That WRITE | Programs That READ | Programs That BROWSE |
|----------------------|-----|------:|----------------------|---------------------|----------------------|
| **ACCTFILE** (`ACCTDATA.VSAM.KSDS`) | ACCT-ID (11) | 300 | CBACT04C, CBTRN02C, COACTUPC, COBIL00C | CBACT01C, CBEXPORT, CBSTM03A/B, CBTRN01C, CBTRN02C, COACTVWC, COTRN02C, COPAUA0C, COPAUS0C | — |
| **CARDFILE** (`CARDDATA.VSAM.KSDS`) | CARD-NUM (16) | 150 | COCRDUPC | CBACT02C, CBEXPORT, CBTRN01C, COCRDSLC | COCRDLIC |
| **CUSTFILE** (`CUSTDATA.VSAM.KSDS`) | CUST-ID (9) | 500 | *(none online)* | CBCUS01C, CBEXPORT, CBSTM03A/B, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C | — |
| **CARDXREF** (`CARDXREF.VSAM.KSDS`) | XREF-CARD-NUM (16) | 50 | *(none online)* | CBACT03C, CBACT04C, CBEXPORT, CBSTM03A/B, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COTRN02C, COBIL00C, COPAUA0C | — |
| **TRANSACT** (`TRANSACT.VSAM.KSDS`) | TRAN-ID (16) | 350 | CBTRN01C, CBTRN02C, COTRN02C, COBIL00C | CBTRN03C, CBSTM03A/B | COTRN00C |
| **USRSEC** (`USRSEC.VSAM.KSDS`) | SEC-USR-ID (8) | 80 | COUSR01C | COSGN00C, COUSR02C, COUSR03C | COUSR00C |
| **TCATBALF** (`TCATBAL.VSAM.KSDS`) | Composite (17) | 50 | CBTRN02C | CBACT04C | — |
| **DISCGRP** (`DISCGRP.VSAM.KSDS`) | Composite (16) | 50 | *(static)* | CBACT04C | — |
| **TRANTYPE** (`TRANTYPE.VSAM.KSDS`) | TRAN-TYPE (2) | 60 | *(static)* | CBTRN03C | — |
| **TRANCATG** (`TRANCATG.VSAM.KSDS`) | Composite (6) | 60 | *(static)* | CBTRN03C | — |
| **DALYTRAN** (`DALYTRAN.PS`) | *(sequential)* | 350 | *(external feed)* | CBTRN01C, CBTRN02C | — |
| **EXPFILE** (`EXPORT.DATA`) | Composite (4+) | 500 | CBEXPORT | CBIMPORT | — |

### Alternate Index (AIX) Relationships

| Base File | AIX Name | AIX Key | AIX Key Offset | PATH |
|-----------|----------|---------|:--------------:|------|
| CARDFILE | CARDAIX | CARD-ACCT-ID | 16 | CARDFILP |
| CARDXREF | XREFAIX | XREF-ACCT-ID | 25 | XREFFILP |
| TRANSACT | TRANAIX | TRAN-CARD-NUM | (via TRANIDX.jcl) | TRANFILP |

---

## 5. JCL → Program → File Mapping

### Data Setup Pipeline (IDCAMS)

```
┌─────────────────────────────────────────────────────────────────┐
│ VSAM Cluster Definition & Loading                               │
│                                                                 │
│  ACCTFILE.jcl ──IDCAMS──► ACCTDATA.VSAM.KSDS  ◄── ACCTDATA.PS │
│  CARDFILE.jcl ──IDCAMS──► CARDDATA.VSAM.KSDS + AIX + PATH      │
│  CUSTFILE.jcl ──IDCAMS──► CUSTDATA.VSAM.KSDS  ◄── CUSTDATA.PS  │
│  XREFFILE.jcl ──IDCAMS──► CARDXREF.VSAM.KSDS + AIX + PATH      │
│  TRANFILE.jcl ──IDCAMS──► TRANSACT.VSAM.KSDS  ◄── TRANSACT.PS  │
│  DISCGRP.jcl  ──IDCAMS──► DISCGRP.VSAM.KSDS   ◄── DISCGRP.PS  │
│  TCATBALF.jcl ──IDCAMS──► TCATBAL.VSAM.KSDS   ◄── TCATBAL.PS  │
│  TRANTYPE.jcl ──IDCAMS──► TRANTYPE.VSAM.KSDS  ◄── TRANTYPE.PS  │
│  TRANCATG.jcl ──IDCAMS──► TRANCATG.VSAM.KSDS  ◄── TRANCATG.PS │
│  DUSRSECJ.jcl ──IDCAMS──► USRSEC.VSAM.KSDS    ◄── USRSEC.PS   │
└─────────────────────────────────────────────────────────────────┘
```

### Batch Processing Job → File Flow

| JCL Job | Program | Input Files | Output Files |
|---------|---------|-------------|--------------|
| COMBTRAN | CBTRN01C | DALYTRAN (seq), CUSTFILE, XREFFILE, CARDFILE, ACCTFILE | TRANSACT |
| POSTTRAN | CBTRN02C | DALYTRAN, XREFFILE | TRANSACT, ACCTFILE, TCATBALF |
| INTCALC | CBACT04C | TCATBALF, XREFFILE, DISCGRP, ACCTFILE | ACCTFILE (updated), TRANSACT |
| CREASTMT | CBSTM03A→B | TRNXFILE, CUSTFILE, XREFFILE, ACCTFILE | STMTFILE, HTMLFILE |
| TRANREPT | CBTRN03C | TRANSACT, XREFFILE, TRANTYPE, TRANCATG | REPTFILE |
| CBEXPORT | CBEXPORT | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | EXPFILE |
| CBIMPORT | CBIMPORT | EXPFILE | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT |
| READACCT | CBACT01C | ACCTFILE | OUTFILE, ARRYFILE, VBRCFILE |
| READCARD | CBACT02C | CARDFILE | SYSOUT |
| READCUST | CBCUS01C | CUSTFILE | SYSOUT |
| READXREF | CBACT03C | XREFFILE | SYSOUT |
| TRANBKP | *(IDCAMS)* | TRANSACT | GDG backup |

### IMS Batch Job → Database Flow

| JCL Job | Program | IMS DB | Operation |
|---------|---------|--------|-----------|
| LOADPADB | PAUDBLOD | DBPAUTP0 (PAUTH) | Load (ISRT) |
| UNLDPADB | PAUDBUNL | DBPAUTP0 (PAUTH) | Unload (GN/GNP) |
| UNLDGSAM | DBUNLDGS | DBPAUTP0 (PAUTH) | GSAM unload |
| CBPAUP0J | CBPAUP0C | DBPAUTP0 (PAUTH) | Purge expired (DLET) |
| DBPAUTP0 | DFSURGU0 | DBPAUTP0 | DB download (utility) |

### DB2 Job → Table Flow

| JCL Job | Purpose | Tables |
|---------|---------|--------|
| CREADB21 | Create DB2 tables | TRANSACTION_TYPE, TRANSACTION_CATEGORY |
| MNTTRDB2 | Populate DB2 tables | TRANSACTION_TYPE, TRANSACTION_CATEGORY |
| TRANEXTR | Extract to sequential | TRANSACTION_TYPE → sequential file |

---

## 6. End-to-End Batch Pipeline Flow

### Daily Processing Pipeline

```
                    ┌──────────────┐
                    │  External    │
                    │  Daily Feed  │
                    │ (DALYTRAN.PS)│
                    └──────┬───────┘
                           │
                           ▼
         ┌─────────────────────────────────────┐
Step 1:  │  COMBTRAN (CBTRN01C)                │
         │  Validate & combine daily txns       │
         │  IN: DALYTRAN + CUST + XREF + CARD  │
         │  OUT: TRANSACT (validated records)   │
         └─────────────────┬───────────────────┘
                           │
                           ▼
         ┌─────────────────────────────────────┐
Step 2:  │  POSTTRAN (CBTRN02C)                │
         │  Post txns, update balances          │
         │  IN: DALYTRAN + XREFFILE            │
         │  UPDATE: ACCTFILE, TCATBALF          │
         │  OUT: TRANSACT (posted records)      │
         └─────────────────┬───────────────────┘
                           │
                    ┌──────┴───────┐
                    ▼              ▼
   ┌────────────────────┐  ┌────────────────────┐
   │ Step 3a: INTCALC   │  │ Step 3b: TRANREPT  │
   │ (CBACT04C)         │  │ (CBTRN03C)         │
   │ Interest calc       │  │ Daily txn report   │
   │ IN: TCATBAL, XREF, │  │ IN: TRANSACT, XREF │
   │     DISCGRP, ACCT  │  │     TRANTYPE,       │
   │ UPDATE: ACCTFILE   │  │     TRANCATG        │
   │ OUT: TRANSACT (int) │  │ OUT: REPTFILE       │
   └────────┬───────────┘  └────────────────────┘
            │
            ▼
   ┌────────────────────┐
   │ Step 4: CREASTMT   │
   │ (CBSTM03A→CBSTM03B)│
   │ Statement generation│
   │ IN: TRNX, CUST,    │
   │     XREF, ACCT     │
   │ OUT: STMTFILE (txt) │
   │      HTMLFILE (html)│
   └────────────────────┘
```

### Data Migration Pipeline

```
   ┌────────────────────┐         ┌────────────────────┐
   │  CBEXPORT           │  ───►  │  CBIMPORT           │
   │  5 VSAM inputs ──►  │  wire  │  ──► 5 outputs      │
   │  1 EXPFILE output   │  xfer  │  + ERROUT            │
   └────────────────────┘         └────────────────────┘

   Inputs:                         Outputs:
   CUSTFILE ─┐                     ┌─► CUSTOUT
   ACCTFILE ─┤                     ├─► ACCTOUT
   XREFFILE ─┼──► EXPFILE ──►     ├─► XREFOUT
   TRANSACT ─┤   (500-byte        ├─► TRNXOUT
   CARDFILE ─┘    multi-record)    ├─► CARDOUT
                                   └─► ERROUT (rejects)
```

### IMS Maintenance Pipeline

```
   ┌────────────────┐    ┌────────────────┐    ┌────────────────┐
   │ LOADPADB        │    │ CBPAUP0J        │    │ UNLDPADB        │
   │ (PAUDBLOD)      │    │ (CBPAUP0C)      │    │ (PAUDBUNL)      │
   │ Initial DB load │    │ Purge expired   │    │ DB unload       │
   │ PS → IMS DB     │    │ auths from DB   │    │ IMS DB → PS     │
   └────────────────┘    └────────────────┘    └────────────────┘
```

---

## 7. Copybook Dependency Matrix

### Most-Referenced Copybooks (Shared Data Structures)

| Copybook | # Programs | Referenced By |
|----------|:----------:|---------------|
| `COCOM01Y` | 21 | All CICS online programs |
| `DFHAID` | 17 | All CICS programs (AID byte constants) |
| `DFHBMSCA` | 17 | All CICS programs (BMS attributes) |
| `COTTL01Y` | 17 | All CICS programs (screen titles) |
| `CSDAT01Y` | 17 | All CICS programs (date/time display) |
| `CSMSG01Y` | 17 | All CICS programs (message area) |
| `CVACT01Y` | 13 | Account record — most cross-cutting data entity |
| `CVACT03Y` | 11 | Card-XREF — critical join table |
| `CSUSR01Y` | 8 | User security record |
| `CVTRA05Y` | 8 | Transaction record |
| `CVCUS01Y` | 8 | Customer record |
| `CVACT02Y` | 6 | Card record |
| `CIPAUSMY` | 6 | IMS auth summary (auth module only) |
| `CIPAUDTY` | 6 | IMS auth detail (auth module only) |
| `CSSTRPFY` | 5 | PFKey store procedure |
| `CVCRD01Y` | 5 | Card work area (AID mapping) |

### Copybook Usage per Program

| Program | # Copybooks | Key Copybooks |
|---------|:-----------:|---------------|
| COACTUPC | 18 | CSUTLDWY, CVCRD01Y, CSLKPCDY, COCOM01Y, CSSETATY (×38), CSSTRPFY, CSUTLDPY + 11 more |
| COPAUA0C | 14 | CMQODV, CMQMDV, CMQV, CCPAURQY, CCPAURLY, CIPAUSMY, CIPAUDTY + 7 more |
| COPAUS0C | 14 | COCOM01Y, COPAU00, CIPAUSMY, CIPAUDTY, CVACT01Y, CVCUS01Y + 8 more |
| COACTVWC | 14 | COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y + 9 more |
| COCRDUPC | 12 | COCOM01Y, CVACT02Y, CVCUS01Y + 9 more |
| COCRDLIC | 10 | COCOM01Y, CVACT02Y, CVCRD01Y + 7 more |

---

## 8. Suggested Microservice Boundaries

Based on call graph clustering and data affinity:

```
┌─────────────────────────────────────────────────────────────┐
│ Account Service                                              │
│ Programs: COACTUPC, COACTVWC, COACCT01                      │
│ Data: ACCTFILE, CUSTFILE                                     │
│ APIs: GET/PUT /accounts/{id}, GET /accounts/{id}/customer    │
├─────────────────────────────────────────────────────────────┤
│ Card Service                                                 │
│ Programs: COCRDLIC, COCRDSLC, COCRDUPC                      │
│ Data: CARDFILE, CARDXREF                                     │
│ APIs: GET /cards, GET/PUT /cards/{num}                       │
├─────────────────────────────────────────────────────────────┤
│ Transaction Service                                          │
│ Programs: COTRN00C, COTRN01C, COTRN02C, COBIL00C            │
│ Batch: CBTRN01C, CBTRN02C                                   │
│ Data: TRANSACT, TCATBALF, DALYTRAN                           │
│ APIs: GET /transactions, POST /transactions, POST /payments  │
├─────────────────────────────────────────────────────────────┤
│ Authorization Service                                        │
│ Programs: COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, CBPAUP0C │
│ Data: IMS PAUTH DB → auth_summary + auth_detail tables       │
│ APIs: POST /authorizations, GET /authorizations/{id}         │
│ Messaging: MQ request/response → Spring JMS/AMQP             │
├─────────────────────────────────────────────────────────────┤
│ User Service                                                 │
│ Programs: COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C │
│ Data: USRSEC → users table                                   │
│ APIs: POST /auth/login, CRUD /users                          │
│ ⚠️  Passwords stored in plain text — must add hashing        │
├─────────────────────────────────────────────────────────────┤
│ Report Service                                               │
│ Programs: CORPT00C, CBTRN03C, CBSTM03A, CBSTM03B           │
│ Data: reads from all VSAM files (read-only)                  │
│ APIs: POST /reports/transactions, POST /reports/statements    │
├─────────────────────────────────────────────────────────────┤
│ Reference Data Service                                       │
│ Programs: COTRTLIC, COTRTUPC, COBTUPDT                      │
│ Data: DB2 TRANSACTION_TYPE, TRANSACTION_CATEGORY tables      │
│ APIs: CRUD /transaction-types, CRUD /transaction-categories  │
├─────────────────────────────────────────────────────────────┤
│ Data Migration Service                                       │
│ Programs: CBEXPORT, CBIMPORT                                 │
│ Data: All VSAM files → EXPFILE (multi-record)                │
│ APIs: POST /migration/export, POST /migration/import         │
└─────────────────────────────────────────────────────────────┘
```

---

*End of Dependency Map*
