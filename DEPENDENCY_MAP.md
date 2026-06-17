# DEPENDENCY MAP — CardDemo COBOL Estate

> **Scope:** Inter-program calls (CALL, XCTL, LINK), dataset lineage (JCL DD ↔ program file I/O), and end-to-end batch pipeline flow.

---

## 1. Online Navigation Call Graph (CICS XCTL)

All CICS online programs communicate via `EXEC CICS XCTL PROGRAM(...)` passing the COMMAREA (COCOM01Y).

```
COSGN00C  (Sign-On — entry point)
│
├──[Admin user]── XCTL → COADM01C  (Admin Menu Hub)
│                         │
│                         ├── XCTL → COUSR00C  (User List)
│                         │           ├── XCTL → COUSR02C  (User Update)
│                         │           └── XCTL → COUSR03C  (User Delete)
│                         ├── XCTL → COUSR01C  (User Add)
│                         ├── XCTL → COTRTLIC  (Tran Type List — DB2)
│                         │           └── XCTL → COTRTUPC  (Tran Type Update — DB2)
│                         └── return → COSGN00C
│
└──[Regular user]── XCTL → COMEN01C  (Main Menu Hub — 11 targets)
                            │
                            ├── XCTL → COACTVWC  (Account View)
                            ├── XCTL → COACTUPC  (Account Update)
                            ├── XCTL → COCRDLIC  (Credit Card List)
                            │           ├── XCTL → COCRDSLC  (Card Detail View)
                            │           └── XCTL → COCRDUPC  (Card Update)
                            ├── XCTL → COTRN00C  (Transaction List)
                            │           └── XCTL → COTRN01C  (Transaction View)
                            ├── XCTL → COTRN02C  (Transaction Add)
                            ├── XCTL → CORPT00C  (Report Request)
                            ├── XCTL → COBIL00C  (Bill Payment)
                            └── XCTL → COPAUS0C  (Pending Auth Summary — IMS)
                                        ├── LINK → COPAUS1C  (Auth Detail/Update)
                                        └── LINK → COPAUS2C  (Fraud Marking — DB2)
```

### XCTL Target Resolution

Programs use two patterns:
1. **Literal target:** `XCTL PROGRAM('COADM01C')` — direct routing (COSGN00C)
2. **Variable target:** `XCTL PROGRAM(CDEMO-TO-PROGRAM)` — menu-driven routing (COMEN01C, COADM01C, all return-to-menu flows)

| Source Program | XCTL Count | Targets |
|---------------|------------|---------|
| COSGN00C | 2 | `'COADM01C'` (admin), `'COMEN01C'` (user) |
| COMEN01C | 1 | `CDEMO-MENU-OPT-PGMNAME(WS-OPTION)` — any of 11 programs |
| COADM01C | 1 | `CDEMO-MENU-OPT-PGMNAME(WS-OPTION)` — any of 6 admin programs |
| COACTUPC | 1 | `CDEMO-TO-PROGRAM` (return to menu) |
| COACTVWC | 1 | `CDEMO-TO-PROGRAM` (return to menu) |
| COCRDLIC | 3 | `LIT-MENUPGM` (return to menu), `CCARD-NEXT-PROG` (card detail/update) |
| COCRDSLC | 1 | `CDEMO-TO-PROGRAM` (return to menu or list) |
| COCRDUPC | 1 | `CDEMO-TO-PROGRAM` (return to menu or list) |
| COTRTLIC | 2 | `CDEMO-TO-PROGRAM` (return), `LIT-ADDTPGM` (add/update) |
| COTRTUPC | 1 | `CDEMO-TO-PROGRAM` (return to list) |

---

## 2. Batch CALL Graph (Program-to-Program)

### Application-Level Calls

```
CBSTM03A ──CALL 'CBSTM03B'──► CBSTM03B  (I/O submodule, 13 CALL sites)
    Purpose: File open, read, close, write operations for statement generation

CORPT00C ──CALL 'CSUTLDTC'──► CSUTLDTC  (Date validation utility, 2 call sites)
COTRN02C ──CALL 'CSUTLDTC'──► CSUTLDTC  (Date validation utility, 2 call sites)
    Purpose: Validate user-entered dates via LE CEEDAYS conversion
```

### System/Runtime Calls

| Calling Program(s) | Target | Type | Purpose |
|--------------------|--------|------|---------|
| CBACT01C | COBDATFT | Assembler | Date formatting |
| COBSWAIT | MVSWAIT | Assembler | Timed wait for batch step sequencing |
| CSUTLDTC | CEEDAYS | LE runtime | Lilian day conversion for date validation |
| CBACT01C, CBACT02C, CBACT03C, CBCUS01C, CBACT04C, CBTRN02C, CBTRN03C, CBEXPORT, CBSTM03A | CEE3ABD | LE runtime | Abnormal termination handler |
| COPAUA0C | MQOPEN, MQGET, MQPUT1, MQCLOSE | MQ API | Authorization MQ message processing |
| COACCT01, CODATE01 | MQOPEN, MQGET, MQPUT, MQCLOSE | MQ API | Account/date inquiry via MQ |
| PAUDBLOD, PAUDBUNL, DBUNLDGS, CBPAUP0C | CBLTDLI | IMS DL/I | IMS database operations (GU, GN, GNP, DLET, ISRT, REPL) |
| COTRTLIC, COTRTUPC, COPAUA0C, COPAUS2C, COBTUPDT | DSNTIAC (via CSDB2RPY) | DB2 utility | SQL error message formatting |

---

## 3. Copybook Dependency Matrix

Shows which programs include which copybooks (via COPY statement). Only shared copybooks listed; BMS-generated and program-specific copybooks omitted.

| Copybook | Programs Using It | Count |
|----------|-------------------|-------|
| COCOM01Y (commarea) | All 25 CICS programs | 25 |
| COTTL01Y (title) | All CICS programs except menu-only | 22 |
| CSDAT01Y (date/time) | All CICS programs | 22 |
| CSMSG01Y (messages) | All CICS programs | 22 |
| DFHAID (AID keys) | All CICS programs | 22 |
| DFHBMSCA (BMS attrs) | All CICS programs | 22 |
| CSUSR01Y (user record) | COSGN00C, COUSR00–03C, COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | 12 |
| CVCRD01Y (card work areas) | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | 5 |
| CSSTRPFY (store PFKey) | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | 4 |
| CVACT01Y (account rec) | CBACT01C, CBACT04C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, CBTRN02C, CBSTM03A, CBEXPORT | 9 |
| CVACT02Y (card rec) | CBACT02C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBEXPORT | 6 |
| CVACT03Y (xref rec) | CBACT03C, CBACT04C, COACTUPC, COACTVWC, COTRN02C, CBTRN02C, CBTRN03C, CBSTM03A, CBEXPORT, COBIL00C | 10 |
| CVCUS01Y (customer rec) | CBCUS01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, CBEXPORT | 6 |
| CVTRA05Y (transaction rec) | CBACT04C, COTRN00C, COTRN01C, COTRN02C, CORPT00C, CBTRN02C, CBTRN03C, CBEXPORT, COBIL00C | 9 |
| CVTRA06Y (daily tran rec) | CBTRN01C, CBTRN02C | 2 |
| CSLKPCDY (lookup codes) | COACTUPC | 1 |
| CSUTLDWY (date WS) | COACTUPC, CSUTLDTC | 2 |
| CSMSG02Y (abend data) | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC | 4 |
| CIPAUDTY (IMS auth detail) | CBPAUP0C, COPAUA0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL | 7 |
| CIPAUSMY (IMS auth summary) | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL | 7 |
| IMSFUNCS (DL/I functions) | CBPAUP0C, COPAUA0C, DBUNLDGS, PAUDBLOD, PAUDBUNL | 5 |
| CSDB2RPY (DB2 procedures) | COTRTLIC, COTRTUPC, COBTUPDT | 3 |
| CSDB2RWY (DB2 working stg) | COTRTLIC, COTRTUPC, COBTUPDT | 3 |

---

## 4. Dataset Lineage — JCL Jobs ↔ VSAM Files ↔ Programs

### 4a. VSAM File Lifecycle

```
 ┌──────────────────────────────────────────────────────────────────────────┐
 │                         VSAM FILE LIFECYCLE                              │
 ├──────────────────────────────────────────────────────────────────────────┤
 │                                                                          │
 │  ACCTFILE (Account KSDS)                                                 │
 │    Define: ACCTFILE.jcl (IDCAMS DELETE/DEFINE/REPRO)                     │
 │    Writers: CBACT04C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE), │
 │             CBIMPORT (WRITE)                                             │
 │    Readers: CBACT01C, CBACT04C, CBTRN02C, COACTUPC, COACTVWC,          │
 │             COTRN02C, COBIL00C, CBEXPORT, CBSTM03A, COPAUA0C,          │
 │             COPAUS0C                                                     │
 │                                                                          │
 │  CARDFILE (Card KSDS + AIX)                                              │
 │    Define: CARDFILE.jcl (IDCAMS + AIX for alternate key)                 │
 │    Writers: COCRDUPC (REWRITE), CBIMPORT (WRITE)                        │
 │    Readers: CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC,          │
 │             CBEXPORT                                                     │
 │                                                                          │
 │  CUSTFILE (Customer KSDS)                                                │
 │    Define: CUSTFILE.jcl (IDCAMS)                                         │
 │    Writers: CBIMPORT (WRITE)                                             │
 │    Readers: CBCUS01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC,          │
 │             CBEXPORT, COPAUA0C, COPAUS0C                                │
 │                                                                          │
 │  XREFFILE / CARDXREF (Card Cross-Reference KSDS + AIX)                  │
 │    Define: XREFFILE.jcl (IDCAMS + AIX)                                   │
 │    Writers: CBIMPORT (WRITE)                                             │
 │    Readers: CBACT03C, CBACT04C, CBTRN02C, CBTRN03C, COACTUPC,          │
 │             COACTVWC, COTRN02C, COBIL00C, CBEXPORT, CBSTM03A,          │
 │             COPAUA0C, COPAUS0C                                          │
 │                                                                          │
 │  TRANSACT (Transaction KSDS + AIX)                                       │
 │    Define: TRANFILE.jcl (IDCAMS + AIX)                                   │
 │    Writers: CBTRN02C (WRITE), CBACT04C (WRITE), COTRN02C (WRITE),      │
 │             COBIL00C (WRITE), CBIMPORT (WRITE)                           │
 │    Readers: COTRN00C, COTRN01C, CBTRN03C, CBSTM03A, CBEXPORT,         │
 │             COBIL00C (READPREV for last ID)                              │
 │                                                                          │
 │  USRSEC (User Security KSDS)                                             │
 │    Define: DUSRSECJ.jcl (IDCAMS)                                         │
 │    Writers: COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE)     │
 │    Readers: COSGN00C, COUSR00C, COUSR02C, COUSR03C                     │
 │                                                                          │
 │  DALYTRAN (Daily Transaction — sequential input)                         │
 │    Source: External feed (not VSAM — sequential PS)                      │
 │    Readers: CBTRN01C, CBTRN02C                                          │
 │                                                                          │
 │  TCATBALF (Transaction Category Balance KSDS)                            │
 │    Define: TCATBALF.jcl (IDCAMS)                                         │
 │    Writers: CBACT04C (REWRITE)                                           │
 │    Readers: CBACT04C, CBTRN02C                                          │
 │                                                                          │
 │  DISCGRP (Disclosure Group KSDS)                                         │
 │    Define: DISCGRP.jcl (IDCAMS)                                          │
 │    Readers: CBACT04C                                                     │
 │                                                                          │
 │  TRANTYPE (Transaction Type KSDS)                                        │
 │    Define: TRANTYPE.jcl (IDCAMS)                                         │
 │    Readers: CBTRN03C                                                     │
 │                                                                          │
 │  TRANCATG (Transaction Category KSDS)                                    │
 │    Define: TRANCATG.jcl (IDCAMS)                                         │
 │    Readers: CBTRN03C                                                     │
 └──────────────────────────────────────────────────────────────────────────┘
```

### 4b. JCL Job → Dataset → Program Mapping

| JCL Job | Program | Input Datasets (DD READ) | Output Datasets (DD WRITE) |
|---------|---------|--------------------------|----------------------------|
| POSTTRAN.jcl | CBTRN02C | DALYTRAN, XREFFILE, ACCTFILE, TCATBALF | TRANSACT, DALYREJS |
| INTCALC.jcl | CBACT04C | TCATBALF, XREFFILE, XREFFIL1, ACCTFILE, DISCGRP | TRANSACT |
| CREASTMT.JCL | SORT → IDCAMS → CBSTM03A | TRANSACT.VSAM.KSDS → TRXFL.SEQ → TRXFL.VSAM.KSDS | STMTFILE, HTMLFILE |
| TRANREPT.jcl | SORT → CBTRN03C | TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM | TRANREPT |
| READACCT.jcl | CBACT01C | ACCTFILE | PSCOMP, ARRYPS, VBPS |
| READCARD.jcl | CBACT02C | CARDFILE | — (display only) |
| READCUST.jcl | CBCUS01C | CUSTFILE | — (display only) |
| READXREF.jcl | CBACT03C | XREFFILE | — (display only) |
| CBEXPORT.jcl | CBEXPORT | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | EXPFILE |
| CBIMPORT.jcl | CBIMPORT | EXPFILE | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT |
| LOADPADB.JCL | PAUDBLOD | Sequential input file | IMS PAUT database (ISRT) |
| UNLDPADB.JCL | PAUDBUNL | IMS PAUT database (GN/GNP) | Sequential output file |
| UNLDGSAM.JCL | DBUNLDGS | IMS PAUT database (GN/GNP) | GSAM output file |
| CBPAUP0J.jcl | CBPAUP0C | IMS PAUT database (GN/GNP) | IMS PAUT database (DLET) |
| CREADB21.jcl | IKJEFT01 (TSO) | — | DB2 tables TRNTYPE, TRNCATG (DDL + load) |
| TRANEXTR.jcl | IEBGENER → IKJEFT01 | VSAM transaction data | DB2 tables (via SQL) |
| TXT2PDF1.JCL | IKJEFT1B (TXT2PDF) | STATEMNT.PS | PDF output |

---

## 5. End-to-End Batch Pipeline Flow

### Daily Processing Pipeline

```
                        ┌─────────────────────────┐
                        │   External Daily Feed    │
                        │      (DALYTRAN file)     │
                        └───────────┬─────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│  STEP 1: POSTTRAN.jcl → CBTRN02C (Transaction Posting)            │
│                                                                     │
│  Input:  DALYTRAN (daily transactions — sequential)                 │
│          XREFFILE (card-account cross-reference)                    │
│          ACCTFILE (account master)                                   │
│          TCATBALF (category balances)                               │
│                                                                     │
│  Process: Validate each daily transaction against XREF & ACCT      │
│           Write valid transactions to master TRANSACT file          │
│           Write rejects to DALYREJS                                 │
│                                                                     │
│  Output: TRANSACT (posted transactions — VSAM KSDS)                │
│          DALYREJS (rejected transactions)                           │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│  STEP 2: INTCALC.jcl → CBACT04C (Interest Calculation)            │
│                                                                     │
│  Input:  TCATBALF (category balances per account)                   │
│          XREFFILE (card→account mapping)                            │
│          ACCTFILE (account master — current balances)               │
│          DISCGRP  (disclosure group — interest rates)               │
│                                                                     │
│  Process: For each account+category, apply interest rate from       │
│           disclosure group, compute interest amount, create         │
│           interest transaction, update account balance              │
│                                                                     │
│  Output: TRANSACT (interest transactions written)                   │
│          ACCTFILE (account balances updated via REWRITE)            │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│  STEP 3: CREASTMT.JCL → SORT + CBSTM03A/CBSTM03B                  │
│                                                                     │
│  Sub-steps:                                                         │
│    STEP010 — SORT: Extract & sort TRANSACT by card+tran-ID         │
│    STEP020 — IDCAMS REPRO: Load sorted data into TRXFL VSAM        │
│    STEP030 — IEFBR14: Delete old statement/HTML output files       │
│    STEP040 — CBSTM03A: Generate statements (text + HTML)           │
│                                                                     │
│  Input:  TRXFL.VSAM (sorted transactions)                          │
│          via CBSTM03B: XREF-FILE, CUST-FILE, ACCT-FILE            │
│                                                                     │
│  Process: For each card: look up customer, account; format          │
│           statement header; list transactions; compute totals;      │
│           generate text and HTML statement files                    │
│                                                                     │
│  Output: STATEMNT.PS (text statements)                              │
│          STATEMNT.HTML (HTML statements)                            │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│  STEP 4: TRANREPT.jcl → SORT + CBTRN03C (Transaction Report)      │
│                                                                     │
│  Sub-steps:                                                         │
│    STEP05R — SORT: Sort transaction file                            │
│    STEP10R — CBTRN03C: Generate daily transaction report            │
│                                                                     │
│  Input:  TRANFILE (sorted transactions)                             │
│          CARDXREF (card-account mapping)                            │
│          TRANTYPE (transaction type descriptions)                   │
│          TRANCATG (transaction category descriptions)               │
│          DATEPARM (report date range parameter)                     │
│                                                                     │
│  Process: Read each transaction, look up type/category              │
│           descriptions, format report lines with page/account/      │
│           grand totals                                              │
│                                                                     │
│  Output: TRANREPT (formatted report file)                           │
└─────────────────────────────────────────────────────────────────────┘
```

### Auxiliary Batch Flows

```
Data Export Pipeline:
  CBEXPORT.jcl → CBEXPORT
    Reads: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE
    Writes: EXPFILE (multi-record-type sequential file via CVEXPORT layout)

Data Import Pipeline:
  CBIMPORT.jcl → CBIMPORT
    Reads: EXPFILE
    Writes: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT (validated entity files)

IMS Load/Unload Pipeline:
  LOADPADB.JCL → PAUDBLOD (sequential → IMS ISRT)
  UNLDPADB.JCL → PAUDBUNL (IMS GN/GNP → sequential)
  UNLDGSAM.JCL → DBUNLDGS (IMS → GSAM sequential)
  CBPAUP0J.jcl → CBPAUP0C (purge expired IMS records via DLET)

DB2 Provisioning:
  CREADB21.jcl → Create TRNTYPE/TRNCATG tables, bind plans, load initial data
  TRANEXTR.jcl → Extract VSAM transaction data into DB2 tables

Report Post-Processing:
  TXT2PDF1.JCL → Convert text statements to PDF (REXX utility)
  PRTCATBL.jcl → Print category balance report (SORT)
```

---

## 6. Cross-Subsystem Integration Points

| Integration | Programs | Direction | Data Exchanged |
|-------------|----------|-----------|----------------|
| CICS ↔ VSAM | 21 online programs | Bidirectional | All business entities via EXEC CICS READ/WRITE |
| CICS ↔ IMS | COPAUA0C, COPAUS0C, COPAUS1C | Read/Write | Pending auth summary & detail segments |
| CICS ↔ DB2 | COTRTLIC, COTRTUPC, COPAUA0C, COPAUS2C | Bidirectional | Transaction types, categories, fraud flags |
| CICS ↔ MQ | COPAUA0C, COACCT01, CODATE01 | Bidirectional | Auth requests/responses, account inquiries |
| Batch ↔ VSAM | 16 batch programs | Bidirectional | File-level READ/WRITE/REWRITE |
| Batch ↔ IMS | PAUDBLOD, PAUDBUNL, DBUNLDGS, CBPAUP0C | Bidirectional | IMS database load/unload/purge |
| Online ↔ Batch | CORPT00C → JCL INTRDR | One-way (submit) | Report parameters via TD queue |

### Critical Integration: COPAUA0C (Authorization Decision)
This single program spans **4 subsystems** simultaneously:
1. **MQ** — MQOPEN/MQGET/MQPUT1/MQCLOSE (read auth request, write auth response)
2. **IMS** — DL/I calls for pending authorization lookup
3. **DB2** — SQL queries for fraud/authorization history
4. **CICS/VSAM** — READ ACCTFILE, CARDXREF, CUSTFILE for real-time validation

---

## 7. Suggested Microservice Boundaries

Based on the dependency analysis, natural seams exist at:

| Microservice | Programs | Shared Data | Coupling |
|-------------|----------|-------------|----------|
| **user-auth** | COSGN00C, COUSR00–03C, COADM01C, COMEN01C | USRSEC | Low (isolated file) |
| **account** | COACTUPC, COACTVWC, COACCT01 | ACCTFILE, CUSTFILE, XREFFILE | High (3 files shared) |
| **card** | COCRDLIC, COCRDSLC, COCRDUPC | CARDFILE, CUSTFILE | Medium |
| **transaction** | COTRN00–02C, CBTRN01–02C, COBIL00C | TRANSACT, DALYTRAN, XREFFILE, ACCTFILE | High |
| **reporting** | CORPT00C, CBTRN03C, CBSTM03A/B | TRANSACT (read-only) | Low (read-only) |
| **tran-type** | COTRTLIC, COTRTUPC, COBTUPDT | DB2 TRNTYPE/TRNCATG | Low (isolated DB2) |
| **authorization** | COPAUA0C, COPAUS0–2C, CBPAUP0C, PAUDBLOD/UNL, DBUNLDGS | IMS PAUT DB, MQ queues | Low (own subsystems) |
| **data-migration** | CBEXPORT, CBIMPORT | All files (read) | Utility (retire after migration) |
