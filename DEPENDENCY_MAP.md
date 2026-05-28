# Dependency Map — CardDemo COBOL Estate

> Call graph, dataset lineage, and batch pipeline flow for the entire CardDemo system.

---

## 1  Program Call Graph

### 1.1  CALL Dependencies (inter-program calls)

```
CBACT01C ──CALL──► COBDATFT (assembler date formatter)
CBACT01C ──CALL──► CEE3ABD  (LE abend)
CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD
CBSTM03A ──CALL──► CBSTM03B (file I/O sub-module)
CBSTM03A ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD
COBSWAIT ──CALL──► MVSWAIT  (assembler wait routine)

CORPT00C ──CALL──► CSUTLDTC (date validation utility)
COTRN02C ──CALL──► CSUTLDTC (date validation utility)

COPAUA0C ──CALL──► MQOPEN   (MQ queue open)
COPAUA0C ──CALL──► MQGET    (MQ message get)
COPAUA0C ──CALL──► MQPUT1   (MQ message put)
COPAUA0C ──CALL──► MQCLOSE  (MQ queue close)

COACCT01 ──CALL──► MQOPEN, MQGET, MQPUT, MQCLOSE
CODATE01 ──CALL──► MQOPEN, MQGET, MQPUT, MQCLOSE

DBUNLDGS ──CALL──► CBLTDLI  (IMS DL/I interface)
PAUDBLOD ──CALL──► CBLTDLI
PAUDBUNL ──CALL──► CBLTDLI
```

### 1.2  CICS XCTL / LINK Navigation (Online Programs)

```
                        ┌─────────────┐
                        │  COSGN00C   │  Sign-on Screen
                        │  (CC00)     │
                        └──────┬──────┘
                               │
                 ┌─────────────┼─────────────┐
                 │ (Admin)     │ (User)       │
                 ▼             ▼              │
          ┌────────────┐ ┌────────────┐       │
          │ COADM01C   │ │ COMEN01C   │       │
          │ Admin Menu │ │ Main Menu  │       │
          └─────┬──────┘ └─────┬──────┘       │
                │              │              │
    ┌───────────┼───────┐      │              │
    │           │       │      │              │
    ▼           ▼       ▼      │              │
 COUSR00C  COUSR01C  COUSR02C  │              │
 User List User Add  User Upd  │              │
    │           │       │      │              │
    ▼           │       │      │              │
 COUSR03C      │       │      │              │
 User Del      │       │      │              │
    │           │       │      │              │
    │     ┌─────┴───────┘      │              │
    │     ▼                    │              │
    │  COTRTLIC ◄──────────────┤              │
    │  TranType List (DB2)     │              │
    │     │                    │              │
    │     ▼                    │              │
    │  COTRTUPC                │              │
    │  TranType Update (DB2)   │              │
    │                          │              │
    └──────────────────────────┘              │
                                             │
    From COMEN01C Main Menu:                 │
    ┌────────────────────────────────────┐    │
    │ 1. COACTVWC  - Account View       │    │
    │ 2. COACTUPC  - Account Update     │    │
    │ 3. COCRDLIC  - Credit Card List   │    │
    │ 4. COCRDSLC  - Credit Card View   │    │
    │ 5. COCRDUPC  - Credit Card Update │    │
    │ 6. COTRN00C  - Transaction List   │    │
    │ 7. COTRN01C  - Transaction View   │    │
    │ 8. COTRN02C  - Transaction Add    │    │
    │ 9. CORPT00C  - Transaction Report │    │
    │10. COBIL00C  - Bill Payment       │    │
    │11. COPAUS0C  - Pending Auth View  │    │
    └────────────────────────────────────┘    │
              │                              │
              ▼                              │
    COPAUS0C ──► COPAUS1C (Auth Detail)      │
              ──► COPAUS2C (Mark Fraud)      │
              ──► COPAUA0C (Auth Decision, via MQ)
```

### 1.3  Copybook Usage Matrix

| Copybook | Used By Programs |
|----------|-----------------|
| **COCOM01Y** (COMMAREA) | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C |
| **CVACT01Y** (Account) | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C |
| **CVACT02Y** (Card) | CBEXPORT, CBIMPORT, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C, CBTRN01C |
| **CVACT03Y** (Xref) | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C |
| **CVTRA05Y** (Transaction) | CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C |
| **CVCUS01Y** (Customer) | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| **CSUSR01Y** (User Security) | COACTUPC, COACTVWC, COADM01C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C |
| **COTTL01Y** (Titles) | All online programs (19 programs) |
| **CSDAT01Y** (Date/Time) | All online programs (19 programs) |
| **DFHAID** (AID keys) | All CICS online programs |
| **DFHBMSCA** (BMS attributes) | All CICS online programs |
| **CSLKPCDY** (Lookup codes) | COACTUPC |
| **CVEXPORT** (Export layout) | CBEXPORT, CBIMPORT |
| **CIPAUSMY** (IMS Auth Summary) | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C |
| **CIPAUDTY** (IMS Auth Detail) | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C |

---

## 2  Dataset Lineage — VSAM Files

### 2.1  Dataset Flow Diagram

```
                    ┌─────────────────────────────────────────────────────────────┐
  FLAT FILES        │                    VSAM KSDS FILES                          │
  (app/data/)       │                                                             │
                    │                                                             │
  ACCTDATA.PS ─────►│ ACCTDATA.VSAM.KSDS ◄──RW── CBACT04C (interest calc)        │
                    │    ▲                 ◄──RW── CBTRN02C (post trans)           │
                    │    │ READ             ◄──RW── COACTUPC (acct update)         │
                    │    └── CBACT01C, CBEXPORT, COACTVWC, COBIL00C, COTRN02C     │
                    │                                                             │
  CARDDATA.PS ─────►│ CARDDATA.VSAM.KSDS ◄──RW── COCRDUPC (card update)          │
                    │    ▲ READ                                                   │
                    │    └── CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC     │
                    │    │                                                        │
                    │    └── CARDDATA.VSAM.AIX (Alternate Index on ACCT-ID)        │
                    │         └── Used by COCRDLIC, COACTUPC, COACTVWC            │
                    │                                                             │
  CUSTDATA.PS ─────►│ CUSTDATA.VSAM.KSDS ◄── READ by CBCUS01C, CBEXPORT,        │
                    │                        CBSTM03B, CBTRN01C, COACTUPC,        │
                    │                        COACTVWC, COCRDSLC, COCRDUPC         │
                    │                                                             │
  CARDXREF.PS ─────►│ CARDXREF.VSAM.KSDS ◄── READ by CBACT03C, CBACT04C,        │
                    │                        CBEXPORT, CBSTM03B, CBTRN01C,        │
                    │                        CBTRN02C, CBTRN03C, COBIL00C,        │
                    │                        COTRN02C                             │
                    │    └── CARDXREF.VSAM.AIX (Alternate Index)                   │
                    │                                                             │
  DALYTRAN.PS ─────►│ TRANSACT.VSAM.KSDS ◄──W── CBTRN02C (post)                  │
  (daily input)     │    ▲                  ◄──W── COBIL00C (bill pay)            │
                    │    │ READ              ◄──W── COTRN02C (add txn)            │
                    │    └── CBEXPORT, CBTRN03C, COTRN00C, COTRN01C              │
                    │    └── TRANSACT.VSAM.AIX (Alternate Index)                   │
                    │                                                             │
  TCATBALF.PS ─────►│ TCATBALF.VSAM.KSDS ◄──RW── CBTRN02C                        │
                    │    ▲ READ                                                   │
                    │    └── CBACT04C                                             │
                    │                                                             │
  DISCGRP.PS ──────►│ DISCGRP.VSAM.KSDS ◄── READ by CBACT04C                     │
                    │                                                             │
  TRANTYPE.PS ─────►│ TRANTYPE.VSAM.KSDS ◄── READ by CBTRN03C                    │
                    │                                                             │
  TRANCATG.PS ─────►│ TRANCATG.VSAM.KSDS ◄── READ by CBTRN03C                    │
                    │                                                             │
  USRSEC.PS ───────►│ USRSEC.VSAM.KSDS ◄── R/W/DEL by COSGN00C, COUSR00-03C     │
                    │                                                             │
                    │ EXPORT.DATA ◄──W── CBEXPORT ──► R── CBIMPORT               │
                    │                                                             │
                    └─────────────────────────────────────────────────────────────┘
```

### 2.2  JCL-to-Dataset-to-Program Mapping

| JCL Job | Writes Dataset | Read By Program(s) | Purpose |
|---------|---------------|---------------------|---------|
| ACCTFILE | ACCTDATA.VSAM.KSDS | CBACT01C, CBACT04C, CBEXPORT, CBSTM03B, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C | Account master |
| CARDFILE | CARDDATA.VSAM.KSDS + AIX | CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COACTUPC, COACTVWC | Card master |
| CUSTFILE | CUSTDATA.VSAM.KSDS | CBCUS01C, CBEXPORT, CBSTM03B, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC | Customer master |
| XREFFILE | CARDXREF.VSAM.KSDS + AIX | CBACT03C, CBACT04C, CBEXPORT, CBSTM03B, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, COTRN02C | Card-Account-Customer cross-reference |
| TRANFILE | TRANSACT.VSAM.KSDS + AIX | CBEXPORT, CBTRN03C, COTRN00C, COTRN01C | Transaction master |
| TCATBALF | TCATBALF.VSAM.KSDS | CBACT04C, CBTRN02C | Transaction category balances |
| DISCGRP | DISCGRP.VSAM.KSDS | CBACT04C | Disclosure groups (interest rates) |
| TRANTYPE | TRANTYPE.VSAM.KSDS | CBTRN03C | Transaction type reference |
| TRANCATG | TRANCATG.VSAM.KSDS | CBTRN03C | Transaction category reference |
| DUSRSECJ | USRSEC.VSAM.KSDS | COSGN00C, COUSR00-03C | User security |
| POSTTRAN | (runs CBTRN02C) | → Writes TRANSACT, DALYREJS; Updates ACCTDATA, TCATBALF | Daily transaction posting |
| INTCALC | (runs CBACT04C) | → Writes TRANSACT; Updates ACCTDATA | Interest/fee calculation |
| CREASTMT | (runs CBSTM03A) | → Writes STMTFILE, HTMLFILE | Statement generation |
| TRANREPT | (runs CBTRN03C) | → Writes TRANREPT report | Transaction report |
| CBEXPORT | (runs CBEXPORT) | → Writes EXPORT.DATA | Branch migration export |
| CBIMPORT | (runs CBIMPORT) | → Writes CUSTDATA/ACCTDATA/XREF/TRANSACT/CARD .IMPORT | Branch migration import |

---

## 3  End-to-End Batch Pipeline Flow

### 3.1  Control-M Scheduled Pipelines

The batch pipeline is orchestrated by Control-M (`app/scheduler/CardDemo.controlm`) in three cycles:

#### Daily Cycle — Transaction Backup
```
CLOSEFIL ──► TRANBKP ──► WAITSTEP ──► OPENFIL
   │            │            │            │
   │  Close     │  REPRO     │  Pause     │  Reopen
   │  CICS      │  TRANSACT  │  for CICS  │  CICS
   │  files     │  to GDG    │  refresh   │  files
   │            │  backup    │            │
```

#### Weekly Cycle — Reference Data Refresh
```
MNTTRDB2 ──► CLOSEFIL ──► DISCGRP ──► WAITSTEP ──► OPENFIL
   │            │            │            │            │
   │ DB2 tran   │  Close     │ Refresh    │  Pause     │ Reopen
   │ type maint │  files     │ disclosure │            │ files
   │            │            │ groups     │            │

         TRANEXTR (parallel after MNTTRDB2)
            │
            │ Extract tran types
            │ from DB2 to flat
```

#### Monthly Cycle — Interest & Statement Processing
```
CLOSEFIL ──► INTCALC ──► COMBTRAN ──► WAITSTEP ──► OPENFIL
   │            │            │            │            │
   │  Close     │  Run       │  Merge     │  Pause     │  Reopen
   │  CICS      │  CBACT04C  │  backup +  │            │  CICS
   │  files     │  interest  │  system    │            │  files
   │            │  calculator│  trans     │            │
```

### 3.2  Full Transaction Processing Pipeline (Logical Flow)

```
┌──────────────────────────────────────────────────────────────────┐
│ 1. SETUP: One-time VSAM dataset definition and initial load      │
│    ACCTFILE → CARDFILE → CUSTFILE → XREFFILE → TRANFILE →       │
│    TCATBALF → TRANCATG → TRANTYPE → DISCGRP → DUSRSECJ         │
│    DEFGDGB (GDG bases) → DEFGDGD (DB2 GDGs)                    │
│    CBADMCDJ (CICS CSD definitions)                              │
└──────────────────────────────────────┬───────────────────────────┘
                                       │
                                       ▼
┌──────────────────────────────────────────────────────────────────┐
│ 2. ONLINE: Users interact via CICS 3270 screens                  │
│    COSGN00C → COMEN01C/COADM01C → functional screens            │
│    (Account/Card/Transaction/User CRUD operations)              │
│    Each screen reads/writes VSAM files via EXEC CICS            │
│    COTRN02C adds transactions → TRANSACT VSAM                   │
│    COBIL00C processes bill payments → updates ACCTDATA          │
└──────────────────────────────────────┬───────────────────────────┘
                                       │
                                       ▼
┌──────────────────────────────────────────────────────────────────┐
│ 3. DAILY BATCH: Transaction posting and backup                   │
│    CLOSEFIL ─► close CICS files                                  │
│    POSTTRAN ─► CBTRN02C: Post daily transactions                │
│      Input:  DALYTRAN.PS (daily feed)                           │
│      Output: TRANSACT VSAM (posted), DALYREJS GDG (rejects)    │
│      Update: ACCTDATA (balances), TCATBALF (category balances)  │
│    TRANBKP  ─► Backup TRANSACT to GDG                          │
│    WAITSTEP ─► Pause for CICS newcopy                           │
│    OPENFIL  ─► Reopen CICS files                                │
└──────────────────────────────────────┬───────────────────────────┘
                                       │
                                       ▼
┌──────────────────────────────────────────────────────────────────┐
│ 4. WEEKLY BATCH: Reference data maintenance                      │
│    MNTTRDB2  ─► DB2 transaction type maintenance                │
│    TRANEXTR  ─► Extract DB2 data to flat files                  │
│    CLOSEFIL  ─► Close CICS files                                │
│    DISCGRP   ─► Refresh disclosure groups VSAM                  │
│    WAITSTEP  ─► Pause                                           │
│    OPENFIL   ─► Reopen CICS files                               │
└──────────────────────────────────────┬───────────────────────────┘
                                       │
                                       ▼
┌──────────────────────────────────────────────────────────────────┐
│ 5. MONTHLY BATCH: Interest calculation and reporting             │
│    CLOSEFIL  ─► Close CICS files                                │
│    INTCALC   ─► CBACT04C: Calculate interest/fees per account   │
│      Input:  TCATBALF, XREFFILE, DISCGRP                       │
│      Update: ACCTDATA (post interest), TRANSACT (interest txns) │
│    COMBTRAN  ─► Merge backup + system transactions              │
│    CREASTMT  ─► CBSTM03A: Generate account statements          │
│      Output: STMTFILE (text), HTMLFILE (HTML)                   │
│    TRANREPT  ─► CBTRN03C: Print transaction detail reports      │
│      Output: TRANREPT report file                               │
│    PRTCATBL  ─► Print category balance report                   │
│    WAITSTEP  ─► Pause                                           │
│    OPENFIL   ─► Reopen CICS files                               │
└──────────────────────────────────────┬───────────────────────────┘
                                       │
                                       ▼
┌──────────────────────────────────────────────────────────────────┐
│ 6. AD-HOC: Migration and utility                                 │
│    CBEXPORT  ─► Export all data to multi-record file            │
│    CBIMPORT  ─► Import from export file into target files       │
│    READACCT/READCARD/READCUST/READXREF ─► Data verification     │
│    TXT2PDF1  ─► Convert statements to PDF                       │
└──────────────────────────────────────────────────────────────────┘
```

---

## 4  IMS Database Dependencies (Authorization Sub-Application)

```
IMS Databases:
  DBPAUTP0.dbd  ─► Pending Authorization Primary DB
  DBPAUTX0.dbd  ─► Pending Authorization Index DB
  PADFLDBD.DBD  ─► Authorization Detail Field DB
  PASFLDBD.DBD  ─► Authorization Summary Field DB

PSBs (Program Specification Blocks):
  PSBPAUTB.psb  ─► Used by CBPAUP0C (batch purge)
  PSBPAUTL.psb  ─► Used by COPAUA0C (online auth)
  PAUTBUNL.PSB  ─► Used by PAUDBUNL (unload)
  DLIGSAMP.PSB  ─► Used by DBUNLDGS (GSAM unload)

Program → IMS Database Flow:
  COPAUA0C ──DLI──► DBPAUTP0 (read/update auth records)
  COPAUS0C ──DLI──► DBPAUTP0 (read summary)
  COPAUS1C ──DLI──► DBPAUTP0 (read detail)
  CBPAUP0C ──DLI──► DBPAUTP0 (delete expired)
  PAUDBLOD ──DLI──► DBPAUTP0 (load from flat)
  PAUDBUNL ──DLI──► DBPAUTP0 (unload to flat)
  DBUNLDGS ──DLI──► DBPAUTP0 (unload to GSAM)
```

---

## 5  DB2 Dependencies (Transaction Type Sub-Application)

```
DB2 Tables:
  TRNTYPE  (DDL: TRNTYPE.ddl)   ─► Transaction types
  TRNTYCAT (DDL: TRNTYCAT.ddl)  ─► Transaction type categories
  AUTHFRDS (DDL: AUTHFRDS.ddl)  ─► Authorization fraud records

Program → DB2 Table Flow:
  COTRTLIC ──SQL──► TRNTYPE, TRNTYCAT (SELECT, DELETE)
  COTRTUPC ──SQL──► TRNTYPE, TRNTYCAT (SELECT, INSERT, UPDATE, DELETE)
  COBTUPDT ──SQL──► TRNTYPE, TRNTYCAT (INSERT, UPDATE, DELETE)
  COPAUS2C ──SQL──► AUTHFRDS (INSERT — mark as fraud)
```

---

## 6  MQ Dependencies

```
COPAUA0C ──MQ──► Authorization Request Queue  (MQGET)
         ──MQ──► Authorization Response Queue (MQPUT1)

COACCT01 ──MQ──► Account Inquiry Queue (MQGET/MQPUT)
CODATE01 ──MQ──► Date Service Queue    (MQGET/MQPUT)
```
