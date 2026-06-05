# Dependency Map — CardDemo COBOL Estate

> Generated: 2026-06-05 | Covers inter-program calls, dataset lineage, and batch pipeline flow

---

## 1. Program Call Graph

### 1.1 Online (CICS) Navigation Tree — XCTL Chains

```
COSGN00C (Sign-On — Entry Point)
│
├── [Admin Path] ──XCTL──► COADM01C (Admin Menu)
│   │
│   ├── Option 1 ──XCTL──► COUSR00C (User List)
│   │                         ├──XCTL──► COUSR02C (User Update)
│   │                         └──XCTL──► COUSR03C (User Delete)
│   │
│   ├── Option 2 ──XCTL──► COUSR01C (User Add)
│   │
│   ├── Option 5 ──XCTL──► COTRTLIC (Transaction Type List — DB2)
│   │
│   └── Option 6 ──XCTL──► COTRTUPC (Transaction Type Update — DB2)
│
└── [User Path] ──XCTL──► COMEN01C (Main Menu — Central Hub)
    │
    ├── Option 1  ──XCTL──► COACTVWC (Account View)
    │
    ├── Option 2  ──XCTL──► COACTUPC (Account Update)
    │
    ├── Option 3  ──XCTL──► COCRDLIC (Card List)
    │                         ├──XCTL──► COCRDSLC (Card Detail View)
    │                         └──XCTL──► COCRDUPC (Card Update)
    │
    ├── Option 6  ──XCTL──► COTRN00C (Transaction List)
    │                         └──XCTL──► COTRN01C (Transaction Detail)
    │
    ├── Option 8  ──XCTL──► COTRN02C (Transaction Add)
    │
    ├── Option 9  ──XCTL──► CORPT00C (Report Request)
    │                         └──Submits──► INTRDRJ1.JCL / INTRDRJ2.JCL
    │
    ├── Option 10 ──XCTL──► COBIL00C (Bill Payment)
    │
    └── Option 11 ──XCTL──► COPAUS0C (Auth Summary — IMS)
                              └──LINK──► COPAUS1C (Auth Detail)
                                          └──LINK──► COPAUS2C (Mark Fraud)
```

### 1.2 Batch Program CALL Graph

```
CBACT01C ──CALL──► COBDATFT (Assembler: date formatter)
         ──CALL──► CEE3ABD  (LE: abnormal termination)

CBACT02C ──CALL──► CEE3ABD

CBACT03C ──CALL──► CEE3ABD

CBACT04C ──CALL──► CEE3ABD

CBCUS01C ──CALL──► CEE3ABD

CBEXPORT ──CALL──► CEE3ABD

CBIMPORT ──CALL──► CEE3ABD

CBTRN01C ──CALL──► CEE3ABD

CBTRN02C ──CALL──► CEE3ABD

CBTRN03C ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B (I/O sub-module, called multiple times)

COBSWAIT ──CALL──► MVSWAIT  (Assembler: wait/pause routine)

CSUTLDTC ──CALL──► CEEDAYS  (LE: date conversion service)

CORPT00C ──CALL──► CSUTLDTC (Date utility)
COTRN02C ──CALL──► CSUTLDTC (Date utility)
```

### 1.3 IMS DL/I Call Patterns

| Program | DL/I Functions Used | Database |
|---------|-------------------|----------|
| CBPAUP0C | GN, GNP, DLET | DBPAUTP0 (Pending Auth) |
| COPAUA0C | GU, SCHD, TERM | DBPAUTP0 + DBPAUTX0 |
| COPAUS0C | GU, GNP | DBPAUTP0 (Summary segment) |
| COPAUS1C | GU, GNP, REPL | DBPAUTP0 (Detail segment) |
| COPAUS2C | REPL | DBPAUTP0 (Detail segment) |
| PAUDBLOD | ISRT, GU | DBPAUTP0 |
| PAUDBUNL | GN, GNP | DBPAUTP0 |
| DBUNLDGS | GN, GNP, ISRT (GSAM) | DBPAUTP0 → GSAM output |

### 1.4 MQ Message Flows

```
External Card Network
        │
        ▼ (MQPUT)
┌─────────────────┐
│  Request Queue  │
└────────┬────────┘
         │ MQGET
         ▼
    COPAUA0C (Authorization Decision)
    ├── IMS Lookup (GU account summary)
    ├── Business Rules (limit checks, fraud)
    ├── DB2 INSERT (fraud flag if applicable)
    │
    │ MQPUT1
    ▼
┌─────────────────┐
│  Response Queue  │
└─────────────────┘

COACCT01 ◄──MQGET── Account Inquiry Queue ──MQPUT──► Response
CODATE01 ◄──MQGET── Date Inquiry Queue ──MQPUT──► Response
```

---

## 2. Dataset Lineage

### 2.1 VSAM File Access Map

| Dataset (DD Name) | Record Layout | Writers (Programs) | Readers (Programs) |
|-------------------|---------------|--------------------|--------------------|
| **ACCTFILE** (Account) | CVACT01Y (300 bytes) | CBTRN02C, CBACT04C, COACTUPC, COBIL00C, CBIMPORT | CBACT01C, CBEXPORT, CBTRN01C, CBTRN02C, CBSTM03B, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C, CBACT04C, CBIMPORT |
| **CARDFILE** (Card) | CVACT02Y (150 bytes) | COCRDUPC, CBIMPORT | CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, CBIMPORT |
| **CUSTFILE** (Customer) | CVCUS01Y (500 bytes) | CBIMPORT | CBCUS01C, CBEXPORT, CBTRN01C, CBSTM03B, COCRDSLC, COCRDUPC, COACTUPC, COACTVWC |
| **CARDXREF** (Cross-Ref) | CVACT03Y (50 bytes) | CBIMPORT | CBACT03C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03B, COACTUPC, COACTVWC, COBIL00C, COTRN02C, CBACT04C |
| **TRANSACT** (Transaction) | CVTRA05Y (350 bytes) | CBTRN02C, COTRN02C, COBIL00C, CBIMPORT | CBEXPORT, CBTRN03C, CBSTM03B, COTRN00C, COTRN01C, CBACT04C |
| **DALYTRAN** (Daily Trans) | CVTRA06Y (350 bytes) | External feed | CBTRN01C, CBTRN02C |
| **TCATBALF** (Cat Balance) | CVTRA01Y (50 bytes) | CBTRN02C | CBACT04C |
| **TRANTYPE** (Tran Types) | CVTRA03Y (60 bytes) | DB2 load (CREADB21) | CBTRN03C |
| **TRANCATG** (Categories) | CVTRA04Y (60 bytes) | DB2 load (CREADB21) | CBTRN03C |
| **DISCGRP** (Interest Rates) | CVTRA02Y (50 bytes) | Admin setup | CBACT04C |
| **USRSEC** (User Security) | CSUSR01Y (80 bytes) | COUSR01C, COUSR02C, DUSRSECJ | COSGN00C, COUSR00C, COUSR02C, COUSR03C |
| **DALYREJS** (Rejects) | CVTRA06Y variant | CBTRN02C | — (audit trail) |
| **EXPFILE** (Export) | CVEXPORT (500 bytes) | CBEXPORT | CBIMPORT |

### 2.2 JCL Job → Dataset Mapping

| JCL Job | Datasets Read | Datasets Written | Program |
|---------|--------------|-----------------|---------|
| POSTTRAN | DALYTRAN, XREFFILE, ACCTFILE, TCATBALF | TRANFILE, DALYREJS, ACCTFILE(rewrite), TCATBALF(rewrite) | CBTRN02C |
| INTCALC | TCATBALF, XREFFILE, DISCGRP, ACCTFILE, TRANSACT | ACCTFILE (rewrite) | CBACT04C |
| CREASTMT | TRANSACT(sorted), XREFFILE, CUSTFILE, ACCTFILE | STMTFILE, HTMLFILE | CBSTM03A→CBSTM03B |
| TRANREPT | TRANFILE, CARDXREF, TRANTYPE, TRANCATG | TRANREPT (report) | CBTRN03C |
| READACCT | ACCTFILE | OUTFILE, ARRYFILE, VBRCFILE | CBACT01C |
| READCARD | CARDFILE | SYSPRINT | CBACT02C |
| READCUST | CUSTFILE | SYSPRINT | CBCUS01C |
| READXREF | XREFFILE | SYSPRINT | CBACT03C |
| CBEXPORT | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | EXPFILE | CBEXPORT |
| CBIMPORT | EXPFILE | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CBIMPORT |
| COMBTRAN | DALYTRAN files | DALYTRAN (merged/sorted) | SORT |
| TRANBKP | TRANSACT | TRANSACT.BKUP | IDCAMS REPRO |
| DEFGDGD | TRANTYPE, TRANCATG, DISCGRP | GDG backups (+1 generation) | IEBGENER, IDCAMS |

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Processing Pipeline

```
┌────────────────────────────────────────────────────────────────────────┐
│                      DAILY BATCH PROCESSING CYCLE                        │
└────────────────────────────────────────────────────────────────────────┘

     External Feed
          │
          ▼
  ┌───────────────┐
  │  DALYTRAN     │  Daily transaction input file
  │  (VSAM KSDS)  │
  └───────┬───────┘
          │
          ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 1: POSTTRAN.jcl → CBTRN02C (Transaction Posting)          │
│                                                                   │
│  Reads:  DALYTRAN, XREFFILE, ACCTFILE, TCATBALF                  │
│  Writes: TRANSACT (master), DALYREJS (rejects)                   │
│  Updates: ACCTFILE (balances), TCATBALF (category balances)      │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 2: INTCALC.jcl → CBACT04C (Interest Calculation)           │
│                                                                   │
│  Reads:  TCATBALF, XREFFILE, DISCGRP, ACCTFILE, TRANSACT        │
│  Updates: ACCTFILE (interest accrual on balances)                 │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 3: CREASTMT.JCL → SORT + CBSTM03A→CBSTM03B (Statements)   │
│                                                                   │
│  Pre-step: SORT transactions by account                           │
│  Reads:  TRANSACT(sorted), XREFFILE, CUSTFILE, ACCTFILE          │
│  Writes: STMTFILE (text), HTMLFILE (HTML statements)             │
└──────────────────────────────┬──────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────┐
│ STEP 4: TRANREPT.jcl → SORT + CBTRN03C (Daily Report)           │
│                                                                   │
│  Pre-step: SORT transactions                                      │
│  Reads:  TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM       │
│  Writes: TRANREPT (daily transaction report with totals)         │
└─────────────────────────────────────────────────────────────────┘
```

### 3.2 Periodic/On-Demand Jobs

```
┌─────────────────────────────────────────────────────────┐
│ WEEKLY: CBEXPORT.jcl → CBEXPORT                          │
│  Exports all entity data to single sequential file       │
│  for branch office migration                             │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ ON-DEMAND: CBIMPORT.jcl → CBIMPORT                       │
│  Imports multi-record file back into individual files     │
│  with validation and error reporting                     │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ MONTHLY: DEFGDGD.jcl                                     │
│  Backs up TRANTYPE, TRANCATG, DISCGRP to GDG             │
│  generations for disaster recovery                       │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│ AD-HOC: INTRDRJ1/J2 (submitted by CORPT00C online)       │
│  Internal reader jobs for on-demand report generation    │
└─────────────────────────────────────────────────────────┘
```

### 3.3 IMS Database Maintenance Pipeline

```
┌─────────────────────────────────────────────────────────┐
│ LOADPADB.JCL → PAUDBLOD                                  │
│  Load pending authorization IMS DB from sequential files │
└──────────────────────────────┬──────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────┐
│ (Online processing: COPAUA0C handles auth decisions)     │
│  MQGET request → IMS lookup → business rules → MQPUT1   │
└──────────────────────────────┬──────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────┐
│ CBPAUP0J.jcl → CBPAUP0C                                 │
│  Purge expired authorization records from IMS DB         │
└──────────────────────────────┬──────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────┐
│ UNLDPADB.JCL → PAUDBUNL  /  UNLDGSAM.JCL → DBUNLDGS    │
│  Unload IMS database to sequential files for backup      │
└─────────────────────────────────────────────────────────┘
```

---

## 4. Inter-Program Dependency Matrix

### 4.1 Programs with Most Dependencies (Hub Programs)

| Program | Depends On | Depended On By | Total Connections |
|---------|-----------|----------------|-------------------|
| COMEN01C | COSGN00C | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C | 12 |
| COADM01C | COSGN00C | COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC | 7 |
| CBSTM03A | — | CREASTMT.jcl | 2 (calls CBSTM03B) |
| CBTRN02C | — | POSTTRAN.jcl | 1 (called by JCL, touches most files) |
| COPAUA0C | MQ, IMS, DB2 | External network | 3 subsystems |

### 4.2 Shared Dataset Dependencies (Critical Integration Points)

| Dataset | # Writers | # Readers | Risk Level |
|---------|-----------|-----------|------------|
| ACCTFILE | 5 | 13 | **HIGH** — Most-shared resource |
| CARDXREF | 1 | 11 | MEDIUM — Heavy read contention |
| TRANSACT | 3 | 5 | MEDIUM — Written by batch and online |
| CUSTFILE | 1 | 6 | LOW — Single writer |
| CARDFILE | 2 | 8 | LOW — Low write frequency |

### 4.3 External Dependencies

| Dependency | Type | Called By | Replacement Needed |
|-----------|------|-----------|-------------------|
| COBDATFT | Assembler routine | CBACT01C | Java DateFormatter utility |
| MVSWAIT | Assembler routine | COBSWAIT | Thread.sleep() or ScheduledExecutor |
| CEE3ABD | LE runtime | 10 batch programs | Exception handling (try/catch) |
| CEEDAYS | LE runtime | CSUTLDTC | java.time.LocalDate |
| DSNTIAC | DB2 utility | COTRTLIC, COTRTUPC | JDBC error handling |
| DFSRRC00 | IMS region controller | IMS batch JCL | Spring Boot + JPA |

---

## 5. Copybook Usage Frequency

| Copybook | # Programs Using It | Category |
|----------|--------------------| ---------|
| COCOM01Y | 21 | CICS communication area |
| DFHAID | 17 | CICS attention identifiers |
| DFHBMSCA | 17 | BMS symbolic attribute constants |
| COTTL01Y | 17 | Screen titles |
| CSDAT01Y | 17 | Date/time working storage |
| CSMSG01Y | 17 | Common messages |
| CSUSR01Y | 12 | Security user record |
| CVACT01Y | 11 | Account record |
| CVACT03Y | 10 | Card cross-reference |
| CVTRA05Y | 10 | Transaction record |
| CVCUS01Y | 8 | Customer record |
| CVACT02Y | 7 | Card record |
| CSMSG02Y | 5 | Abend data |
| CSSETATY | 1 (×39 uses) | Screen attribute macro (COPY REPLACING) |
