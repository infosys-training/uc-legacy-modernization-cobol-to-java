# DEPENDENCY MAP — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System  
> **Generated:** 2026-06-02

---

## 1. CICS Online Navigation — Call Graph (XCTL/LINK)

```
                        ┌──────────────────────┐
                        │  COSGN00C (Sign-On)  │
                        └──────────┬───────────┘
                                   │ XCTL
                    ┌──────────────┼──────────────┐
                    ▼                              ▼
        ┌───────────────────┐          ┌───────────────────┐
        │ COADM01C          │          │ COMEN01C          │
        │ (Admin Hub)       │          │ (User Hub)        │
        └───────┬───────────┘          └───────┬───────────┘
                │ XCTL (6 targets)             │ XCTL (11 targets)
                ▼                              ▼
    ┌───────────────────────────┐   ┌─────────────────────────────────────┐
    │ COUSR00C (User List)      │   │ COACTVWC (Account View)             │
    │ COUSR01C (User Add)       │   │ COACTUPC (Account Update)           │
    │ COUSR02C (User Update)    │   │ COCRDLIC (Card List)                │
    │ COUSR03C (User Delete)    │   │ COCRDSLC (Card Detail)              │
    │ COTRTLIC (Tran Type List) │   │ COCRDUPC (Card Update)              │
    │ COTRTUPC (Tran Type Upd)  │   │ COTRN00C (Transaction List)         │
    └───────────────────────────┘   │ COTRN01C (Transaction Detail)       │
                                    │ COTRN02C (Transaction Add)           │
                                    │ CORPT00C (Reports)                   │
                                    │ COBIL00C (Bill Payment)              │
                                    │ COPAUS0C (Pending Auth View)         │
                                    └─────────────────────────────────────┘
```

### 1.1 Authorization Sub-Chain (LINK, not XCTL)

```
    COPAUS0C (Auth Summary)
        │ CICS LINK
        ▼
    COPAUS1C (Auth Detail/Update)
        │ CICS LINK
        ▼
    COPAUS2C (Fraud Mark → DB2 INSERT)
```

> **Migration note:** LINK preserves return context (like a subroutine call), so this 3-program chain should be migrated as a single unit/microservice.

---

## 2. External Program Dependencies (CALL)

| Caller | Called Program | Type | Purpose |
|--------|---------------|------|---------|
| CBACT01C | COBDATFT | Assembler | Date formatting |
| CBACT01C | CEE3ABD | LE Runtime | Abnormal termination |
| CBTRN02C | CEE3ABD | LE Runtime | Abnormal termination |
| CBTRN03C | CEE3ABD | LE Runtime | Abnormal termination |
| COBSWAIT | MVSWAIT | Assembler | MVS STIMER wait |
| CSUTLDTC | CEEDAYS | LE Runtime | Date conversion (Lilian days) |
| CBSTM03A | CBSTM03B | COBOL Module | Statement I/O submodule |
| CBPAUP0C | CBLTDLI | IMS DL/I | IMS database calls |
| PAUDBLOD | CBLTDLI | IMS DL/I | IMS database calls |
| PAUDBUNL | CBLTDLI | IMS DL/I | IMS database calls |
| DBUNLDGS | CBLTDLI | IMS DL/I | IMS/GSAM unload |
| COPAUA0C | MQOPEN/MQGET/MQPUT1/MQCLOSE | MQ API | Message queue operations |
| COACCT01 | MQOPEN/MQGET/MQPUT/MQCLOSE | MQ API | Account inquiry via MQ |
| CODATE01 | MQOPEN/MQGET/MQPUT/MQCLOSE | MQ API | Date inquiry via MQ |

---

## 3. Dataset Lineage — JCL to Program to File

### 3.1 VSAM File Usage Matrix

| Dataset (Logical) | VSAM Cluster Name | Define Job | Writers (Batch) | Writers (Online) | Readers (Batch) | Readers (Online) |
|-------------------|-------------------|-----------|-----------------|------------------|-----------------|------------------|
| ACCTFILE | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | ACCTFILE.jcl | CBTRN02C, CBACT04C, CBIMPORT | COACTUPC, COBIL00C, COTRN02C | CBACT01C, CBEXPORT, CBTRN01C, CBTRN02C, CBACT04C | COACTVWC, COACTUPC, COPAUA0C, COPAUS0C, COBIL00C, COTRN02C |
| CARDFILE | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CARDFILE.jcl | CBIMPORT | COCRDUPC | CBEXPORT, CBTRN01C | COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C |
| CUSTFILE | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CUSTFILE.jcl | CBIMPORT | — | CBCUS01C, CBEXPORT, CBTRN01C | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| XREFFILE | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | XREFFILE.jcl | CBIMPORT | — | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C | COACTUPC, COACTVWC, COPAUA0C, COTRN02C, CBSTM03A |
| TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | TRANFILE.jcl | CBTRN02C | COBIL00C, COTRN02C | CBTRN03C, CBEXPORT | COTRN00C, COTRN01C, COBIL00C |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | DUSRSECJ.jcl | — | COUSR01C, COUSR02C, COUSR03C | — | COSGN00C, COUSR00C |
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | TCATBALF.jcl | CBTRN02C, CBACT04C | — | CBACT04C | — |
| TRANTYPE | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | TRANTYPE.jcl | — | — | CBTRN03C | — |
| TRANCATG | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | TRANCATG.jcl | — | — | CBTRN03C | — |
| DISCGRP | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | DISCGRP.jcl | — | — | CBACT04C | — |

### 3.2 Sequential/GDG File Lineage

| File | Produced By | Consumed By | JCL Job |
|------|-------------|-------------|---------|
| DALYTRAN (Daily Tran Input) | External feed | CBTRN01C → CBTRN02C | POSTTRAN.jcl |
| DALYREJS (Rejected Trans) | CBTRN02C | Archive/Review | DALYREJS.jcl (GDG) |
| REPTFILE (Transaction Report) | CBTRN03C | Print/PDF | TRANREPT.jcl → TXT2PDF1.JCL |
| STMT-FILE (Statements) | CBSTM03A | Distribution | CREASTMT.JCL |
| OUT-FILE, ARRY-FILE, VBR-FILE | CBACT01C | Downstream extract | READACCT.jcl |
| EXPORT-OUTPUT | CBEXPORT | CBIMPORT (at target) | CBEXPORT.jcl |

### 3.3 DB2 Table Lineage

| Table | Schema | Writers | Readers |
|-------|--------|---------|---------|
| TRANSACTION_TYPE | CARDDEMO | COBTUPDT (batch), COTRTUPC (online) | COTRTLIC, COTRTUPC |
| TRANSACTION_CATEGORY | CARDDEMO | COTRTUPC (cascade delete) | COTRTLIC, COTRTUPC |
| FRAUD_MARK (implied) | CARDDEMO | COPAUS2C | — |

### 3.4 IMS Database Lineage

| Database | Segments | Writers | Readers |
|----------|----------|---------|---------|
| PAUT (Pending Auth) | Summary (root) + Detail (child) | PAUDBLOD, COPAUA0C, COPAUS1C | COPAUS0C, COPAUS1C, CBPAUP0C, PAUDBUNL, DBUNLDGS |
| PAUT (Purge) | Summary + Detail | — | CBPAUP0C (GN/GNP then DLET) |

---

## 4. End-to-End Batch Pipeline Flow

### 4.1 Daily Processing Pipeline

```
 ┌─────────────────────────────────────────────────────────────────────────────┐
 │                        DAILY BATCH CYCLE                                     │
 │                                                                             │
 │  ┌─────────┐     ┌──────────┐     ┌──────────┐     ┌──────────┐           │
 │  │ CLOSEFIL│────►│ POSTTRAN │────►│ INTCALC  │────►│ CREASTMT │           │
 │  │ (Close  │     │ (Post    │     │ (Interest│     │ (Generate│           │
 │  │  CICS   │     │  Daily   │     │  Calc)   │     │  Stmts)  │           │
 │  │  files) │     │  Trans)  │     │          │     │          │           │
 │  └─────────┘     └──────────┘     └──────────┘     └──────────┘           │
 │                       │                 │                 │                  │
 │                       ▼                 ▼                 ▼                  │
 │                  ┌──────────┐     ┌──────────┐     ┌──────────┐           │
 │                  │ TRANREPT │     │ DALYREJS │     │ TRANBKP  │           │
 │                  │ (Report) │     │ (Archive │     │ (Backup  │           │
 │                  │          │     │  Rejects)│     │  Trans)  │           │
 │                  └──────────┘     └──────────┘     └──────────┘           │
 │                       │                                                     │
 │                       ▼                                                     │
 │                  ┌──────────┐     ┌──────────┐                             │
 │                  │ TXT2PDF1 │────►│ OPENFIL  │                             │
 │                  │ (PDF)    │     │ (Reopen  │                             │
 │                  │          │     │  CICS)   │                             │
 │                  └──────────┘     └──────────┘                             │
 └─────────────────────────────────────────────────────────────────────────────┘
```

### 4.2 Detailed Step-by-Step Flow

| Step | JCL Job | Program | Input Files | Output Files | Dependencies |
|------|---------|---------|-------------|--------------|-------------|
| 1 | CLOSEFIL.jcl | DFHCSDUP | — | — | CICS files closed for batch |
| 2 | POSTTRAN.jcl | CBTRN02C | DALYTRAN, XREF | TRANSACT, DALYREJS; Updates: ACCOUNT, TCATBAL | After CLOSEFIL |
| 3 | INTCALC.jcl | CBACT04C | TCATBAL, XREF, DISCGRP, ACCOUNT | Updated: ACCOUNT, TRANSACT | After POSTTRAN |
| 4 | CREASTMT.JCL | CBSTM03A→CBSTM03B | XREF, CUST, ACCT, TRANSACT | STMT-FILE, HTML output | After INTCALC |
| 5 | TRANREPT.jcl | CBTRN03C | TRANSACT, XREF, TRANTYPE, TRANCATG, DATE-PARMS | REPTFILE | After POSTTRAN |
| 6 | DALYREJS.jcl | IDCAMS | DALYREJS | GDG archive | After POSTTRAN |
| 7 | TRANBKP.jcl | IDCAMS | TRANSACT | GDG backup | After all trans processing |
| 8 | TXT2PDF1.JCL | IEBGENER | REPTFILE | PDF-ready output | After TRANREPT |
| 9 | OPENFIL.jcl | DFHCSDUP | — | — | Last step: reopen CICS files |

### 4.3 Data Migration Pipeline

```
Source System                    Target System
┌──────────┐    ┌──────────┐    ┌──────────┐
│ CBEXPORT │───►│ Transfer │───►│ CBIMPORT │
│ (Export  │    │ (FTP/MQ) │    │ (Import  │
│  5 files │    │          │    │  5 files │
│  → 1)    │    └──────────┘    │  1 → 5)  │
└──────────┘                    └──────────┘
```

### 4.4 IMS Batch Pipelines

```
IMS Database Load:
  PAUDBLOD ──► [INFILE1 + INFILE2] ──► IMS PAUT DB (ISRT)

IMS Database Unload:
  PAUDBUNL ◄── IMS PAUT DB (GN/GNP) ──► [OPFILE1 + OPFILE2]

GSAM Unload:
  DBUNLDGS ◄── IMS PAUT DB (GN/GNP) ──► GSAM sequential output
```

---

## 5. Copybook Dependency Matrix (Top 10 Most Referenced)

| Copybook | # Programs Using It | Programs |
|----------|--------------------:|----------|
| COCOM01Y | 20 | All CICS online programs + COPAUS0C/1C |
| CVACT01Y | 14 | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COPAUA0C, COPAUS0C, COTRN02C, CBSTM03A, COACCT01 |
| COTTL01Y | 13 | All CICS programs with BMS screens |
| DFHAID | 13 | All CICS programs |
| DFHBMSCA | 13 | All CICS programs |
| CSDAT01Y | 12 | All online programs + statement generator |
| CSMSG01Y | 12 | All online programs |
| CVACT03Y | 11 | Programs accessing card-account cross-reference |
| CSUSR01Y | 10 | All user-facing online programs |
| CVCUS01Y | 9 | Customer data access programs |

---

## 6. Inter-Program Communication Patterns

| Pattern | Instances | Description |
|---------|-----------|-------------|
| XCTL (Transfer Control) | 15+ | One-way navigation between CICS programs; no return |
| LINK | 3 | Subroutine-style call with return (COPAUS chain) |
| CALL (static) | 14 | Direct COBOL CALL to external module |
| COMMAREA passing | 20 | Data passed between CICS programs via COCOM01Y |
| MQ messaging | 3 | Async request/reply via IBM MQ (COACCT01, CODATE01, COPAUA0C) |
| DB2 SQL | 4 | Direct SQL (COTRTLIC, COTRTUPC, COBTUPDT, COPAUS2C) |
| IMS DL/I | 7 | Hierarchical DB access (CBPAUP0C, COPAUA0C, COPAUS0C/1C, PAUDBLOD, PAUDBUNL, DBUNLDGS) |
| Internal Reader | 2 | JCL submission from CICS (CORPT00C → INTRDRJ1/J2) |
