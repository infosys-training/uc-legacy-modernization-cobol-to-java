# DEPENDENCY MAP — CardDemo Call Graph, Dataset Lineage & Pipeline Flow

> **Generated:** 2026-06-05 | **Scope:** All COBOL programs, JCL jobs, VSAM/IMS/DB2/MQ data stores

---

## 1. Online Navigation Graph (CICS XCTL / LINK Chains)

```
                        ┌──────────────────────────────────┐
                        │  COSGN00C  (Sign-On)             │
                        │  Reads: USRSEC VSAM              │
                        │  Validates user credentials      │
                        └─────────┬───────────┬────────────┘
                     XCTL (Admin) │           │ XCTL (User)
                                  ▼           ▼
                 ┌────────────────────┐  ┌────────────────────────────┐
                 │ COADM01C           │  │ COMEN01C                   │
                 │ Admin Menu Hub     │  │ Main Menu Hub (11 targets) │
                 └──┬──┬──┬──┬───────┘  └──┬──┬──┬──┬──┬──┬──┬──┬───┘
                    │  │  │  │             │  │  │  │  │  │  │  │
     ┌──────────────┘  │  │  │             │  │  │  │  │  │  │  └──────────┐
     ▼                 │  │  │             │  │  │  │  │  │  │             ▼
 ┌──────────┐          │  │  │             │  │  │  │  │  │  │      ┌───────────┐
 │COUSR00C  │◄─────────┘  │  │             │  │  │  │  │  │  └─────►│COPAUS0C   │
 │User List │          │  │             │  │  │  │  │  │       │Auth Summary│
 │ XCTL↓    │          │  │             │  │  │  │  │  │       │(IMS)       │
 │COUSR02C  │          │  │             │  │  │  │  │  │       │  LINK↓     │
 │(Update)  │          │  │             │  │  │  │  │  │       │COPAUS1C    │
 │COUSR03C  │          │  │             │  │  │  │  │  │       │(Detail)    │
 │(Delete)  │          │  │             │  │  │  │  │  │       │  LINK↓     │
 └──────────┘          │  │             │  │  │  │  │  │       │COPAUS2C    │
                       │  │             │  │  │  │  │  │       │(Fraud/DB2) │
 ┌──────────┐          │  │             │  │  │  │  │  │       └───────────┘
 │COUSR01C  │◄─────────┘  │             │  │  │  │  │  │
 │User Add  │             │             │  │  │  │  │  │
 └──────────┘             │             │  │  │  │  │  │
                          │             │  │  │  │  │  │
 ┌──────────┐             │             │  │  │  │  │  │
 │COTRTLIC  │◄────────────┘             │  │  │  │  │  └──────────────┐
 │TranType  │                           │  │  │  │  │                 ▼
 │List(DB2) │                           │  │  │  │  │          ┌───────────┐
 │ XCTL↓    │                           │  │  │  │  └─────────►│COBIL00C   │
 │COTRTUPC  │                           │  │  │  │             │Bill Pay   │
 │(Upd/DB2) │                           │  │  │  │             └───────────┘
 └──────────┘                           │  │  │  │
                                        │  │  │  └──────────────┐
      ┌─────────────────────────────────┘  │  │                 ▼
      ▼                                    │  │          ┌───────────┐
 ┌──────────┐                              │  │          │CORPT00C   │
 │COACTVWC  │                              │  │          │Report Req │
 │Acct View │                              │  │          │→TDQ INTRDR│
 └──────────┘                              │  │          └───────────┘
                                           │  │
      ┌────────────────────────────────────┘  │
      ▼                                       │
 ┌──────────┐                                 │
 │COACTUPC  │    ┌────────────────────────────┘
 │Acct Upd  │    │
 │(4236 LOC)│    ▼
 └──────────┘  ┌──────────┐    ┌──────────┐    ┌──────────┐
               │COCRDLIC  │───►│COCRDSLC  │    │COTRN00C  │
               │Card List │    │Card View │    │Tran List │
               └──────────┘    └──────────┘    └────┬─────┘
                    │                               │
                    ▼                               ▼
               ┌──────────┐                   ┌──────────┐
               │COCRDUPC  │                   │COTRN01C  │
               │Card Upd  │                   │Tran View │
               └──────────┘                   └──────────┘

               ┌──────────┐
               │COTRN02C  │  (also XCTL target from COMEN01C)
               │Tran Add  │
               │CALLs     │
               │CSUTLDTC  │
               └──────────┘
```

---

## 2. CALL Graph (Inter-Program Calls)

### 2.1 Direct CALL Statements

| Caller | Callee | Mechanism | Purpose |
|--------|--------|-----------|---------|
| CBACT01C | COBDATFT | CALL 'COBDATFT' | Assembler date formatting |
| CBACT02C | CEE3ABD | CALL 'CEE3ABD' | LE abnormal termination on error |
| CBACT03C | CEE3ABD | CALL 'CEE3ABD' | LE abnormal termination on error |
| CBCUS01C | CEE3ABD | CALL 'CEE3ABD' | LE abnormal termination on error |
| COBSWAIT | MVSWAIT | CALL 'MVSWAIT' | Assembler wait routine |
| CSUTLDTC | CEEDAYS | CALL 'CEEDAYS' | LE date conversion utility |
| COTRN02C | CSUTLDTC | CALL 'CSUTLDTC' | Date validation for new transactions |
| CORPT00C | CSUTLDTC | CALL 'CSUTLDTC' | Date validation for report date range |
| COPAUA0C | MQOPEN | CALL 'MQOPEN' | Open MQ request queue |
| COPAUA0C | MQGET | CALL 'MQGET' | Get auth request from MQ |
| COPAUA0C | MQPUT1 | CALL 'MQPUT1' | Put auth response to MQ |
| COPAUA0C | MQCLOSE | CALL 'MQCLOSE' | Close MQ queue |

### 2.2 CICS Transfer of Control

| Source | Target | Mechanism | Context |
|--------|--------|-----------|---------|
| COSGN00C | COADM01C | XCTL | Admin user login |
| COSGN00C | COMEN01C | XCTL | Regular user login |
| COADM01C | COUSR00C–03C | XCTL | Admin → User management |
| COADM01C | COTRTLIC | XCTL | Admin → Transaction type list |
| COADM01C | COTRTUPC | XCTL | Admin → Transaction type update |
| COMEN01C | COACTVWC/COACTUPC/COCRDLIC/etc. | XCTL | Main menu → feature screens |
| COTRTLIC | COTRTUPC | XCTL | List → detail/update |
| COPAUS1C | COPAUS0C | LINK | Detail → summary (return) |

### 2.3 Call Graph Diagram

```
External/Assembler Dependencies:
─────────────────────────────
  COBDATFT ◄── CBACT01C
  CEE3ABD  ◄── CBACT02C, CBACT03C, CBCUS01C
  CEEDAYS  ◄── CSUTLDTC ◄── COTRN02C, CORPT00C
  MVSWAIT  ◄── COBSWAIT

MQ Integration:
─────────────────────────────
  MQOPEN/MQGET/MQPUT1/MQCLOSE ◄── COPAUA0C (Authorization Decision)

Batch Subroutine Calls:
─────────────────────────────
  CBSTM03B ◄── CBSTM03A (Statement I/O submodule)
```

---

## 3. Dataset Lineage

### 3.1 VSAM File Access Matrix

| Dataset | JCL Setup | Writers (Programs) | Readers (Programs) |
|---------|-----------|-------------------|-------------------|
| **ACCTDATA.VSAM.KSDS** (Account) | ACCTFILE.jcl | CBACT04C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE), CBIMPORT (WRITE) | CBACT01C, CBACT04C, CBSTM03A, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, CBEXPORT, COACCT01 |
| **CARDDATA.VSAM.KSDS** (Card) | CARDFILE.jcl | COCRDUPC (REWRITE), CBIMPORT (WRITE) | CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, COPAUA0C |
| **CUSTDATA.VSAM.KSDS** (Customer) | CUSTFILE.jcl | CBIMPORT (WRITE) | CBCUS01C, CBSTM03A, COACTUPC, COACTVWC, COCRDUPC, COPAUS0C, CBEXPORT |
| **CARDXREF.VSAM.KSDS** (Cross-Ref) | CARDFILE.jcl (AIX) | CBIMPORT (WRITE) | CBACT04C, CBSTM03A, CBTRN02C, COTRN02C, COPAUA0C, COPAUS0C, CBEXPORT, COACCT01 |
| **TRANSACT.VSAM.KSDS** (Transaction) | TRANFILE.jcl | CBTRN02C (WRITE), COTRN02C (WRITE), COBIL00C (WRITE) | CBTRN01C, CBTRN03C, CBSTM03A, COTRN00C, COTRN01C |
| **DALYTRAN.PS** (Daily Transactions) | TRANFILE.jcl | _(external feed)_ | CBTRN02C |
| **USRSEC.VSAM.KSDS** (User Security) | DUSRSECJ.jcl | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | COSGN00C, COUSR00C, COUSR02C, COUSR03C |
| **TCATBALF.VSAM.KSDS** (Category Bal) | TCATBALF.jcl | CBTRN02C (WRITE/REWRITE) | CBACT04C |
| **DISCGRP.VSAM.KSDS** (Disclosure) | DISCGRP.jcl | _(initial load only)_ | CBACT04C |
| **TRANTYPE.VSAM.KSDS** (Tran Types) | TRANTYPE.jcl | _(initial load only)_ | CBTRN03C |
| **TRANCATG.VSAM.KSDS** (Tran Categories) | TRANCATG.jcl | _(initial load only)_ | CBTRN03C |

### 3.2 JCL Job → Dataset → Program Flow

```
┌─────────────────────────────────────────────────────────────────────┐
│                    DATA SETUP LAYER                                 │
│                                                                     │
│  ACCTFILE.jcl ──REPRO──► ACCTDATA.VSAM.KSDS                       │
│  CARDFILE.jcl ──REPRO──► CARDDATA.VSAM.KSDS + CARDDATA.VSAM.AIX   │
│  CUSTFILE.jcl ──REPRO──► CUSTDATA.VSAM.KSDS                       │
│  TRANFILE.jcl ──REPRO──► TRANSACT.VSAM.KSDS                       │
│  DUSRSECJ.jcl ──REPRO──► USRSEC.VSAM.KSDS                        │
│  TCATBALF.jcl ──REPRO──► TCATBALF.VSAM.KSDS                       │
│  DISCGRP.jcl  ──REPRO──► DISCGRP.VSAM.KSDS                       │
│  TRANTYPE.jcl ──REPRO──► TRANTYPE.VSAM.KSDS                       │
│  TRANCATG.jcl ──REPRO──► TRANCATG.VSAM.KSDS                      │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH PIPELINE                             │
│                                                                     │
│  Step 1: POSTTRAN.jcl (PGM=CBTRN02C)                              │
│    reads:  DALYTRAN.PS, CARDXREF, ACCTDATA                         │
│    writes: TRANSACT, DALYREJS (rejects GDG)                        │
│    updates: TCATBALF (category running balances)                    │
│                         │                                           │
│                         ▼                                           │
│  Step 2: INTCALC.jcl (PGM=CBACT04C)                               │
│    reads:  TCATBALF, CARDXREF, DISCGRP, ACCTDATA                   │
│    updates: ACCTDATA (interest applied to balances)                 │
│                         │                                           │
│                         ▼                                           │
│  Step 3: [CREASTMT] (PGM=CBSTM03A → CBSTM03B)                     │
│    reads:  CARDXREF, CUSTDATA, ACCTDATA, TRANSACT                  │
│    writes: STMTFILE (statement output GDG)                         │
│                         │                                           │
│                         ▼                                           │
│  Step 4: TRANREPT.jcl (SORT → PGM=CBTRN03C)                       │
│    reads:  TRANSACT (via SORT), CARDXREF, TRANTYPE, TRANCATG       │
│    writes: REPTFILE (report output GDG)                            │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    MAINTENANCE / BACKUP                             │
│                                                                     │
│  TRANBKP.jcl ── REPRO TRANSACT → TRANSACT.BKUP(+1), then delete   │
│  COMBTRAN.jcl ── SORT+MERGE TRANSACT.BKUP + SYSTRAN → COMBINED    │
│  PRTCATBL.jcl ── SORT TCATBALF → printed report                   │
│  DEFGDGB/D.jcl ── Define GDG bases for versioned backups           │
└─────────────────────────────────────────────────────────────────────┘
                              │
                              ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    DATA MIGRATION                                   │
│                                                                     │
│  CBEXPORT.jcl (PGM=CBEXPORT)                                      │
│    reads: CUSTDATA, ACCTDATA, CARDXREF                              │
│    writes: EXPORT.DATA (sequential, multi-record-type)              │
│                         │                                           │
│                         ▼                                           │
│  CBIMPORT.jcl (PGM=CBIMPORT)                                      │
│    reads: EXPORT.DATA                                               │
│    writes: CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT        │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.3 IMS Database Lineage

```
┌─────────────────────────────────────────────────────────────────────┐
│                   IMS AUTH DATABASE                                  │
│                                                                     │
│  LOADPADB.JCL ──DL/I ISRT──► IMS PAUT DB (Summary + Detail)       │
│                                     │                               │
│  COPAUA0C (online) ──DL/I GU/ISRT/REPL──► IMS PAUT DB             │
│    reads: MQ request queue                                          │
│    reads: ACCTDATA, CARDDATA, CARDXREF (VSAM)                      │
│    writes: MQ reply queue, CICS TDQ (error log)                    │
│                                     │                               │
│  COPAUS0C/1C (online) ──DL/I GU/GNP/REPL──► IMS PAUT DB (read)   │
│  COPAUS2C (online) ──EXEC SQL──► DB2 fraud table                   │
│                                     │                               │
│  CBPAUP0C (batch) ──DL/I GN/GNP/DLET──► IMS PAUT DB (purge)      │
│                                     │                               │
│  UNLDPADB.JCL / DBPAUTP0.jcl ──DL/I──► Flat file unload           │
│  UNLDGSAM.JCL ──DL/I──► GSAM unload                               │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.4 DB2 Table Lineage

```
┌─────────────────────────────────────────────────────────────────────┐
│                   DB2 TABLES                                        │
│                                                                     │
│  CREADB21.jcl ── DDL: CREATE TABLE DCLTRTYP (Transaction Types)    │
│                  DDL: CREATE TABLE DCLTRCAT (Transaction Categories)│
│                  Initial data load via IKJEFT01                     │
│                         │                                           │
│  COTRTLIC ──SQL SELECT/DELETE──► DCLTRTYP, DCLTRCAT (read + delete)│
│  COTRTUPC ──SQL SELECT/INSERT/UPDATE/DELETE──► DCLTRTYP, DCLTRCAT  │
│  COBTUPDT ──SQL SELECT/INSERT/UPDATE/DELETE──► DCLTRTYP (batch)    │
│  COPAUS2C ──SQL INSERT/UPDATE──► fraud flags table                 │
│                                                                     │
│  TRANEXTR.jcl ── Extracts TRANTYPE/TRANCATG data from DB2          │
│  MNTTRDB2.jcl ── BIND plans for DB2 programs                      │
└─────────────────────────────────────────────────────────────────────┘
```

---

## 4. Copybook Usage Matrix

### 4.1 Most-Referenced Copybooks

| Copybook | Programs Using It | Category |
|----------|------------------|----------|
| COCOM01Y | 20 | Application infrastructure (COMMAREA) |
| DFHAID | 18 | CICS AID key definitions |
| DFHBMSCA | 18 | CICS BMS attribute constants |
| COTTL01Y | 18 | Screen title text |
| CSDAT01Y | 18 | Date/time working storage |
| CSMSG01Y | 18 | Common messages |
| CVACT01Y | 12 | Account record layout |
| CVACT03Y | 10 | Card-XREF record layout |
| CVCUS01Y | 8 | Customer record layout |
| CVTRA05Y | 8 | Transaction record layout |
| CSUSR01Y | 7 | User security record |
| CVACT02Y | 5 | Card record layout |
| CSMSG02Y | 5 | Abend data |
| CIPAUSMY | 4 | IMS auth summary segment |
| CIPAUDTY | 4 | IMS auth detail segment |

### 4.2 Copybook-to-Program Cross-Reference (Business Data)

| Copybook | Used By |
|----------|---------|
| CVACT01Y (Account) | CBACT01C, CBACT04C, CBSTM03A, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT, COACCT01 |
| CVACT02Y (Card) | CBACT02C, CBACT04C, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C |
| CVACT03Y (XREF) | CBACT04C, CBSTM03A, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT |
| CVCUS01Y (Customer) | CBCUS01C, CBSTM03A, COACTUPC, COACTVWC, COCRDUPC, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT |
| CVTRA05Y (Transaction) | CBTRN01C, CBTRN03C, CBSTM03A, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C |
| CVTRA06Y (Daily Tran) | CBTRN02C |
| CVTRA01Y (Cat Balance) | CBACT04C, CBTRN02C |
| CVTRA02Y (Disclosure) | CBACT04C |
| CVTRA03Y (Tran Type) | CBTRN03C |
| CVTRA04Y (Tran Cat) | CBTRN03C |

---

## 5. Inter-Program Dependency Count

| Program | Depends On (count) | Depended On By (count) | Total Dependencies |
|---------|-------------------|----------------------|-------------------|
| COMEN01C | 1 (COSGN00C) | 11 (all menu targets) | 12 |
| COACTUPC | 1 (COMEN01C) | 0 | 1 |
| COPAUA0C | MQOPEN/GET/PUT1/CLOSE, IMS DLI, 3 VSAM files | COPAUS0C (via LINK chain) | 8 |
| COSGN00C | 0 (entry point) | 2 (COADM01C, COMEN01C) | 2 |
| COADM01C | 1 (COSGN00C) | 5 (COUSR00C-03C, COTRTLIC) | 6 |
| CBTRN02C | CSUTLDTC, 5 VSAM files | POSTTRAN.jcl | 7 |
| CBSTM03A | CBSTM03B, 4 VSAM files | CREASTMT JCL | 6 |
| CBACT04C | 4 VSAM files | INTCALC.jcl | 5 |
| CSUTLDTC | CEEDAYS | COTRN02C, CORPT00C | 3 |

---

## 6. End-to-End Batch Pipeline Flow

### 6.1 Daily Processing Sequence

```
                    External Feed
                        │
                        ▼
               ┌─────────────────┐
               │  DALYTRAN.PS    │  (Daily transaction input file)
               │  Sequential     │
               └────────┬────────┘
                        │
    ┌───────────────────┼───────────────────┐
    │           POSTTRAN.jcl                │
    │           PGM=CBTRN02C               │
    │                                       │
    │  ┌─────────┐  ┌──────────┐           │
    │  │CARDXREF │  │ACCTDATA  │ (lookup)  │
    │  └─────────┘  └──────────┘           │
    │                                       │
    │  Output:                              │
    │  ├── TRANSACT.VSAM  (posted txns)    │
    │  ├── DALYREJS(+1)   (rejects)        │
    │  └── TCATBALF.VSAM  (cat balances)   │
    └───────────────────┬───────────────────┘
                        │
                        ▼
    ┌───────────────────┼───────────────────┐
    │           INTCALC.jcl                 │
    │           PGM=CBACT04C               │
    │                                       │
    │  ┌──────────┐  ┌──────────┐          │
    │  │TCATBALF  │  │DISCGRP   │ (rates)  │
    │  └──────────┘  └──────────┘          │
    │  ┌──────────┐  ┌──────────┐          │
    │  │CARDXREF  │  │ACCTDATA  │ (update) │
    │  └──────────┘  └──────────┘          │
    │                                       │
    │  Output: ACCTDATA updated with        │
    │          calculated interest           │
    └───────────────────┬───────────────────┘
                        │
                        ▼
    ┌───────────────────┼───────────────────┐
    │           CREASTMT                    │
    │           PGM=CBSTM03A → CBSTM03B    │
    │                                       │
    │  ┌──────────┐  ┌──────────┐          │
    │  │CARDXREF  │  │CUSTDATA  │          │
    │  └──────────┘  └──────────┘          │
    │  ┌──────────┐  ┌──────────┐          │
    │  │ACCTDATA  │  │TRANSACT  │          │
    │  └──────────┘  └──────────┘          │
    │                                       │
    │  Output: STMTFILE(+1) (statements)   │
    └───────────────────┬───────────────────┘
                        │
                        ▼
    ┌───────────────────┼───────────────────┐
    │           TRANREPT.jcl                │
    │           SORT → PGM=CBTRN03C        │
    │                                       │
    │  ┌──────────┐  ┌──────────┐          │
    │  │TRANSACT  │  │CARDXREF  │          │
    │  │(sorted)  │  └──────────┘          │
    │  └──────────┘  ┌──────────┐          │
    │                │TRANTYPE  │          │
    │  ┌──────────┐  └──────────┘          │
    │  │TRANCATG  │                        │
    │  └──────────┘                        │
    │                                       │
    │  Output: REPTFILE(+1) (daily report) │
    └───────────────────┬───────────────────┘
                        │
                        ▼
    ┌───────────────────┼───────────────────┐
    │           TRANBKP.jcl (end of day)    │
    │           REPRO → DELETE              │
    │                                       │
    │  TRANSACT.VSAM → TRANSACT.BKUP(+1)   │
    │  Then purge master for next day       │
    └───────────────────────────────────────┘
```

### 6.2 Migration Data Flow

```
    ┌──────────────────────────────────────┐
    │  CBEXPORT.jcl (PGM=CBEXPORT)        │
    │                                      │
    │  CUSTDATA ──┐                        │
    │  ACCTDATA ──┼──► EXPORT.DATA         │
    │  CARDXREF ──┘    (sequential file)   │
    └──────────────────┬───────────────────┘
                       │
                       ▼
    ┌──────────────────────────────────────┐
    │  CBIMPORT.jcl (PGM=CBIMPORT)        │
    │                                      │
    │  EXPORT.DATA ──► CUSTDATA.IMPORT     │
    │                  ACCTDATA.IMPORT     │
    │                  CARDXREF.IMPORT     │
    └──────────────────────────────────────┘
```

### 6.3 Authorization Message Flow (MQ/IMS)

```
    ┌────────────────┐
    │  MQ Request    │
    │  Queue         │
    └───────┬────────┘
            │ MQGET
            ▼
    ┌────────────────────────────────────────┐
    │  COPAUA0C (Authorization Decision)     │
    │                                        │
    │  1. MQGET auth request                 │
    │  2. READ CARDXREF → get ACCT-ID       │
    │  3. READ ACCTDATA → check limits      │
    │  4. READ CUSTDATA → verify customer   │
    │  5. DLI GU/ISRT → write IMS auth rec  │
    │  6. MQPUT1 → send response            │
    └───────┬───────────────┬────────────────┘
            │               │
       MQPUT1          DLI ISRT
            │               │
            ▼               ▼
    ┌───────────────┐ ┌─────────────────────┐
    │  MQ Reply     │ │  IMS PAUT Database  │
    │  Queue        │ │  (Summary + Detail) │
    └───────────────┘ └──────────┬──────────┘
                                 │
                          DLI GU/GNP
                                 │
                      ┌──────────┴──────────┐
                      │                     │
                      ▼                     ▼
              ┌───────────────┐     ┌───────────────┐
              │ COPAUS0C      │     │ CBPAUP0C      │
              │ (Summary View)│     │ (Batch Purge) │
              │  │ LINK        │     │ DLI GN/DLET  │
              │  ▼             │     └───────────────┘
              │ COPAUS1C      │
              │ (Detail View) │
              │  │ LINK        │
              │  ▼             │
              │ COPAUS2C      │
              │ (Fraud/DB2)   │
              └───────────────┘
```
