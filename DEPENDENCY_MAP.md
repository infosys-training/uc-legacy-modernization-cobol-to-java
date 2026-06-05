# DEPENDENCY MAP — CardDemo COBOL Estate

> Call graph, dataset lineage, and end-to-end batch pipeline flow for the CardDemo application.

---

## 1. Online Navigation Graph (CICS XCTL Chains)

```
COSGN00C (Sign-On — Entry Point)
│
├──► [Admin user] COADM01C (Admin Menu — Hub)
│    ├──► COUSR00C (User List)
│    │    ├──► COUSR02C (User Update)
│    │    └──► COUSR03C (User Delete)
│    ├──► COUSR01C (User Add)
│    ├──► COTRTLIC (Tran Type List — DB2 cursor pagination)
│    │    └──► COTRTUPC (Tran Type Update/Add/Delete — DB2)
│    └──► COTRTUPC (Tran Type Maintenance — DB2)
│
└──► [Regular user] COMEN01C (Main Menu — Hub, 11 targets)
     ├──► COACTVWC (Account View)
     ├──► COACTUPC (Account Update — 4,236 LOC)
     ├──► COCRDLIC (Card List — VSAM browse)
     │    ├──► COCRDSLC (Card Detail View)
     │    └──► COCRDUPC (Card Update)
     ├──► COTRN00C (Transaction List — VSAM browse)
     │    └──► COTRN01C (Transaction View)
     ├──► COTRN02C (Transaction Add)
     ├──► CORPT00C (Report Request — submits batch JCL via TDQ)
     ├──► COBIL00C (Bill Payment)
     └──► COPAUS0C (Auth Summary — IMS browse)
          └──► COPAUS1C (Auth Detail View)
               └──► COPAUS2C (Mark Fraud — DB2 via EXEC CICS LINK)
```

### Navigation Mechanism

| Source | Target | Mechanism | Data Passing |
|--------|--------|-----------|-------------|
| COSGN00C | COADM01C | EXEC CICS XCTL | COMMAREA (COCOM01Y) |
| COSGN00C | COMEN01C | EXEC CICS XCTL | COMMAREA (COCOM01Y) |
| COMEN01C | 11 programs | EXEC CICS XCTL | COMMAREA — target from COMEN02Y table |
| COADM01C | 6 programs | EXEC CICS XCTL | COMMAREA — target from COADM02Y table |
| COUSR00C | COUSR02C/03C | EXEC CICS XCTL | COMMAREA with selected user ID |
| COCRDLIC | COCRDSLC/COCRDUPC | EXEC CICS XCTL | COMMAREA with CARD-NUM |
| COTRN00C | COTRN01C | EXEC CICS XCTL | COMMAREA with TRAN-ID |
| COPAUS1C | COPAUS2C | EXEC CICS LINK | COMMAREA (returns to caller) |
| All programs | Previous screen | XCTL PROGRAM(CDEMO-TO-PROGRAM) | COMMAREA return path |

---

## 2. Batch Call Graph (CALL Statements)

```
CBACT01C ──CALL──► COBDATFT     (Assembler: date formatting)
         ──CALL──► CEE3ABD      (LE: abnormal termination)

CBACT02C ──CALL──► CEE3ABD      (LE: abnormal termination)

CBACT03C ──CALL──► CEE3ABD      (LE: abnormal termination)

CBCUS01C ──CALL──► CEE3ABD      (LE: abnormal termination)

CBSTM03A ──CALL──► CBSTM03B    (COBOL: I/O submodule for statement report)

CORPT00C ──CALL──► CSUTLDTC    (COBOL: date validation utility)
COTRN02C ──CALL──► CSUTLDTC    (COBOL: date validation utility)

CSUTLDTC ──CALL──► CEEDAYS     (LE: Lilian date conversion)

COBSWAIT ──CALL──► MVSWAIT     (Assembler: wait routine)

COPAUA0C ──CALL──► MQOPEN      (IBM MQ: open queue)
         ──CALL──► MQGET       (IBM MQ: get message)
         ──CALL──► MQPUT1      (IBM MQ: put single message)
         ──CALL──► MQCLOSE     (IBM MQ: close queue)

COACCT01 ──CALL──► MQOPEN (×3) (IBM MQ: open 3 queues)
         ──CALL──► MQGET       (IBM MQ: get request)
         ──CALL──► MQPUT (×2)  (IBM MQ: put response + error)
         ──CALL──► MQCLOSE (×3)(IBM MQ: close 3 queues)

CODATE01 ──CALL──► MQOPEN (×3), MQGET, MQPUT (×2), MQCLOSE (×3)

DBUNLDGS ──CALL──► CBLTDLI     (IMS DL/I: GN, GNP, ISRT)
PAUDBLOD ──CALL──► CBLTDLI     (IMS DL/I: ISRT, GU)
PAUDBUNL ──CALL──► CBLTDLI     (IMS DL/I: GN, GNP)
```

### External Dependencies (Non-COBOL)

| External Module | Type | Called By | Replacement in Java |
|----------------|------|-----------|---------------------|
| COBDATFT | Assembler | CBACT01C | java.time.format.DateTimeFormatter |
| CEE3ABD | LE runtime | CBACT02C, 03C, CBCUS01C | System.exit() or exception handler |
| CEEDAYS | LE runtime | CSUTLDTC | java.time.LocalDate |
| MVSWAIT | Assembler | COBSWAIT | Thread.sleep() |
| CBLTDLI | IMS DL/I | DBUNLDGS, PAUDBLOD, PAUDBUNL | JPA/JDBC repository calls |
| MQOPEN/GET/PUT/CLOSE | IBM MQ | COPAUA0C, COACCT01, CODATE01 | Spring JMS / JmsTemplate |
| DFHCSDUP | CICS utility | CBADMCDJ.jcl | Spring Boot auto-configuration |
| IDCAMS | VSAM utility | 20+ JCL jobs | DDL scripts / Flyway migrations |
| DFSRRC00 | IMS region controller | CBPAUP0J, DBPAUTP0 | Spring Batch scheduled task |

---

## 3. Dataset Lineage — VSAM File Access

### Write Access (Programs That Modify Files)

| VSAM File | Dataset Name | Writers | Operation |
|-----------|-------------|---------|-----------|
| ACCTFILE | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | CBACT04C | REWRITE (interest update) |
| | | CBTRN02C | REWRITE (balance update after posting) |
| | | COACTUPC | REWRITE (account field update) |
| | | COBIL00C | REWRITE (balance after payment) |
| | | CBIMPORT | WRITE (import new records) |
| CARDFILE | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | COCRDUPC | REWRITE (card detail update) |
| | | CBIMPORT | WRITE (import) |
| CUSTFILE | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CBIMPORT | WRITE (import) |
| CARDXREF | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | CBIMPORT | WRITE (import) |
| TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | CBTRN02C | WRITE (post transactions) |
| | | COBIL00C | WRITE (payment transaction) |
| | | CBIMPORT | WRITE (import) |
| | | CBACT04C | WRITE (interest transactions) |
| DALYTRAN | AWS.M2.CARDDEMO.DALYTRAN.PS | External feed | Sequential input (daily batch) |
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | CBTRN02C | WRITE/REWRITE (category balances) |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | COUSR01C | WRITE (add user) |
| | | COUSR02C | REWRITE (update user) |
| | | COUSR03C | DELETE (delete user) |

### Read Access (Programs That Read Files)

| VSAM File | Readers |
|-----------|---------|
| ACCTFILE | CBACT01C, CBACT04C, CBEXPORT, CBSTM03A, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COACCT01, COPAUA0C, COPAUS0C |
| CARDFILE | CBACT02C, CBEXPORT, CBSTM03A, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC |
| CUSTFILE | CBCUS01C, CBEXPORT, CBSTM03A, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| CARDXREF | CBACT03C, CBACT04C, CBEXPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, COACTUPC, COACTVWC, COTRN02C, COPAUA0C, COPAUS0C |
| TRANSACT | CBSTM03A, CBTRN03C, COTRN00C, COTRN01C |
| DALYTRAN | CBTRN01C, CBTRN02C |
| TCATBALF | CBACT04C, CBTRN02C |
| DISCGRP | CBACT04C |
| TRANTYPE | CBTRN03C |
| TRANCATG | CBTRN03C |
| USRSEC | COSGN00C, COUSR00C, COUSR02C, COUSR03C |

### DB2 Table Access

| Table | Program | Operations |
|-------|---------|------------|
| TRANSACTION_TYPE | COTRTLIC | SELECT (cursor), DELETE |
| | COTRTUPC | SELECT, INSERT, UPDATE, DELETE |
| | COBTUPDT | SELECT, INSERT, UPDATE, DELETE |
| TRANSACTION_CATEGORY | COTRTUPC | SELECT, INSERT, DELETE (cascading) |
| AUTHFRDS | COPAUS2C | SELECT, INSERT, UPDATE |

### IMS Database Access

| Database (DBD) | Segment | Program | DL/I Operations |
|----------------|---------|---------|-----------------|
| DBPAUTP0 | Auth Summary (root) | COPAUA0C | GU (lookup by account) |
| | | COPAUS0C | GU, GN (browse) |
| | | COPAUS1C | GU, GNP, REPL (read + update) |
| | | CBPAUP0C | GN (sequential scan), DLET |
| | | PAUDBLOD | ISRT (load), GU (verify) |
| | | PAUDBUNL | GN (unload) |
| | | DBUNLDGS | GN (GSAM unload) |
| | Auth Detail (child) | COPAUA0C | ISRT (write auth record) |
| | | COPAUS0C | GNP (browse within parent) |
| | | COPAUS1C | GNP, REPL |
| | | CBPAUP0C | GNP, DLET |
| | | PAUDBLOD | ISRT |
| | | PAUDBUNL | GNP |
| | | DBUNLDGS | GNP, ISRT |

### MQ Queue Access

| Queue | Program | Operations |
|-------|---------|------------|
| Input Queue (auth requests) | COPAUA0C | MQOPEN, MQGET |
| Output Queue (auth responses) | COPAUA0C | MQOPEN, MQPUT1 |
| Error Queue (auth errors) | COPAUA0C | MQOPEN, MQPUT1 |
| Account Inquiry Request | COACCT01 | MQOPEN, MQGET |
| Account Inquiry Response | COACCT01 | MQOPEN, MQPUT |
| Account Inquiry Error | COACCT01 | MQOPEN, MQPUT |
| Date Inquiry Request | CODATE01 | MQOPEN, MQGET |
| Date Inquiry Response | CODATE01 | MQOPEN, MQPUT |
| Date Inquiry Error | CODATE01 | MQOPEN, MQPUT |

---

## 4. End-to-End Batch Pipeline Flow

### Daily Processing Pipeline

```
                    ┌─────────────────────────────────┐
                    │   External Feed (Daily)          │
                    │   DALYTRAN.PS (daily txn file)   │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
 Step 1:            │  POSTTRAN.jcl                    │
 Post Daily         │  EXEC PGM=CBTRN02C              │
 Transactions       │                                  │
                    │  IN:  DALYTRAN, CARDXREF, ACCT   │
                    │  OUT: TRANSACT (posted txns)     │
                    │       DALYREJS (rejected txns)   │
                    │  UPD: ACCTFILE (balance update)  │
                    │       TCATBALF (category totals) │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
 Step 2:            │  INTCALC.jcl                     │
 Calculate          │  EXEC PGM=CBACT04C               │
 Interest           │  PARM='2022071800' (cycle date)  │
                    │                                  │
                    │  IN:  TCATBALF, CARDXREF,        │
                    │       DISCGRP, ACCTFILE           │
                    │  OUT: TRANSACT (interest txns)   │
                    │  UPD: ACCTFILE (interest applied) │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
 Step 3:            │  CREASTMT.jcl (implied)          │
 Generate           │  EXEC PGM=CBSTM03A → CBSTM03B   │
 Statements         │                                  │
                    │  IN:  CARDXREF, CUSTFILE,        │
                    │       ACCTFILE, TRANSACT          │
                    │  OUT: STMT-FILE (text statements) │
                    │       HTML-FILE (HTML statements) │
                    └──────────────┬──────────────────┘
                                   │
                    ┌──────────────▼──────────────────┐
 Step 4:            │  TRANREPT.jcl                    │
 Transaction        │  STEP05R: SORT (by account/date) │
 Report             │  STEP10R: EXEC PGM=CBTRN03C      │
                    │                                  │
                    │  IN:  TRANSACT, CARDXREF,        │
                    │       TRANTYPE, TRANCATG          │
                    │  OUT: REPTFILE (detail report)   │
                    └─────────────────────────────────┘
```

### Data Migration Pipeline

```
  ┌───────────────────────────┐       ┌───────────────────────────┐
  │  CBEXPORT.jcl             │       │  CBIMPORT.jcl             │
  │  EXEC PGM=CBEXPORT        │       │  EXEC PGM=CBIMPORT        │
  │                           │       │                           │
  │  IN: CUSTFILE, ACCTFILE,  │──────►│  IN: EXPORT-FILE          │
  │      CARDXREF, TRANSACT,  │ file  │  OUT: CUSTFILE, ACCTFILE, │
  │      CARDFILE             │       │       CARDXREF, TRANSACT, │
  │  OUT: EXPORT-FILE         │       │       CARDFILE, ERRORS    │
  │  (multi-record format)    │       │  (validates + splits)     │
  └───────────────────────────┘       └───────────────────────────┘
```

### IMS Database Maintenance Pipeline

```
  ┌───────────────────────────┐       ┌───────────────────────────┐
  │  DBPAUTP0.jcl             │       │  CBPAUP0J.jcl             │
  │  IMS DB Unload            │       │  Expired Auth Purge       │
  │  PGM=DFSRRC00 (DFSURGU0) │       │  PGM=DFSRRC00 (BMP)      │
  │                           │       │  EXEC=CBPAUP0C            │
  │  Unloads DBPAUTP0 →       │       │                           │
  │  flat file for backup     │       │  Deletes expired auth     │
  └───────────────────────────┘       │  segments from IMS DB     │
                                      └───────────────────────────┘
  ┌───────────────────────────┐
  │  PAUDBLOD / PAUDBUNL      │
  │  IMS DB Load / Unload     │
  │  (batch via CBLTDLI)      │
  │                           │
  │  PAUDBLOD: flat → IMS     │
  │  PAUDBUNL: IMS → flat     │
  └───────────────────────────┘
```

---

## 5. Subsystem Dependency Map

```
                     ┌─────────────────────────────────────┐
                     │            CICS TP Monitor           │
                     │  (21 online programs)                │
                     │                                      │
                     │  COSGN00C → COMEN01C → 11 programs  │
                     │           → COADM01C → 6 programs   │
                     └───┬───────────┬───────────┬─────────┘
                         │           │           │
              ┌──────────▼──┐  ┌─────▼─────┐  ┌─▼──────────┐
              │    VSAM     │  │   DB2      │  │  IMS       │
              │  (all batch │  │            │  │            │
              │  + 13 CICS) │  │ COTRTLIC   │  │ COPAUA0C   │
              │             │  │ COTRTUPC   │  │ COPAUS0C   │
              │ 9 VSAM      │  │ COPAUS2C   │  │ COPAUS1C   │
              │ KSDS files  │  │            │  │ CBPAUP0C   │
              └─────────────┘  │ Tables:    │  │ DBUNLDGS   │
                               │ TRTYP      │  │ PAUDBLOD   │
                               │ TRCAT      │  │ PAUDBUNL   │
                               │ AUTHFRDS   │  │            │
                               └────────────┘  │ DB: DBPAUTP0│
                                               └────────────┘
                                                      │
                                               ┌──────▼──────┐
                                               │    MQ       │
                                               │             │
                                               │ COPAUA0C    │
                                               │ COACCT01    │
                                               │ CODATE01    │
                                               │             │
                                               │ 3 queue     │
                                               │ pairs (in/  │
                                               │ out/err)    │
                                               └─────────────┘
```

---

## 6. Inter-Program Dependency Count

| Program | Outgoing Deps | Incoming Deps | Total | Role |
|---------|--------------|---------------|-------|------|
| COMEN01C | 11 (XCTL targets) | 1 (COSGN00C) | 12 | Central user hub |
| COADM01C | 6 (XCTL targets) | 1 (COSGN00C) | 7 | Admin hub |
| COSGN00C | 2 (COADM01C, COMEN01C) | 0 | 2 | Entry point |
| COPAUA0C | 3 (MQ + IMS + CICS) | 1 (COMEN01C) | 4 | Auth decision |
| CBSTM03A | 1 (CALL CBSTM03B) | 0 | 1 | Statement gen |
| CORPT00C | 2 (CALL CSUTLDTC, TDQ submit) | 1 (COMEN01C) | 3 | Report request |
| CBTRN02C | 0 | 1 (POSTTRAN.jcl) | 1 | Transaction posting |
| CBACT04C | 0 | 1 (INTCALC.jcl) | 1 | Interest calc |

---

## 7. Copybook Dependency Fan-Out

Top copybooks by number of programs that reference them:

| Rank | Copybook | # Programs | Scope |
|------|----------|-----------|-------|
| 1 | COCOM01Y (COMMAREA) | 25 | All CICS programs |
| 2 | DFHAID (AID keys) | 21 | All CICS programs |
| 3 | DFHBMSCA (BMS attrs) | 21 | All CICS programs |
| 4 | COTTL01Y (screen title) | 21 | All CICS programs |
| 5 | CSDAT01Y (date/time) | 21 | All CICS programs |
| 6 | CSMSG01Y (messages) | 21 | All CICS programs |
| 7 | CVACT03Y (card XREF) | 13 | Core entity — highest data coupling |
| 8 | CVACT01Y (account) | 13 | Core entity |
| 9 | CVCUS01Y (customer) | 10 | Core entity |
| 10 | CVACT02Y (card) | 10 | Core entity |
| 11 | CVTRA05Y (transaction) | 10 | Core entity |
| 12 | CIPAUDTY (IMS auth detail) | 8 | Authorization module |
| 13 | CIPAUSMY (IMS auth summary) | 7 | Authorization module |
| 14 | CSUSR01Y (user security) | 9 | User management |

---

## 8. JCL-to-Program-to-File Lineage

| JCL Job | Program(s) | Input Files | Output Files | Updated Files |
|---------|-----------|-------------|-------------|---------------|
| POSTTRAN.jcl | CBTRN02C | DALYTRAN, CARDXREF | TRANSACT, DALYREJS | ACCTFILE, TCATBALF |
| INTCALC.jcl | CBACT04C | TCATBALF, CARDXREF, DISCGRP | TRANSACT | ACCTFILE |
| TRANREPT.jcl | SORT, CBTRN03C | TRANSACT, CARDXREF, TRANTYPE, TRANCATG | REPTFILE | — |
| CBEXPORT.jcl | CBEXPORT | CUSTFILE, ACCTFILE, CARDXREF, TRANSACT, CARDFILE | EXPORT-FILE | — |
| CBIMPORT.jcl | CBIMPORT | EXPORT-FILE | CUSTFILE, ACCTFILE, CARDXREF, TRANSACT, CARDFILE, ERRORS | — |
| READACCT.jcl | CBACT01C | ACCTFILE | OUT-FILE, ARRY-FILE, VBRC-FILE | — |
| READCARD.jcl | CBACT02C | CARDFILE | SYSOUT (print) | — |
| READCUST.jcl | CBCUS01C | CUSTFILE | SYSOUT (print) | — |
| READXREF.jcl | CBACT03C | CARDXREF | SYSOUT (print) | — |
| WAITSTEP.jcl | COBSWAIT | — | — | — |
| CBPAUP0J.jcl | CBPAUP0C | IMS DB (DBPAUTP0) | — | IMS DB (deletes expired) |
| DBPAUTP0.jcl | DFSURGU0 | IMS DB (DBPAUTP0) | Flat file (unload) | — |
| CREADB21.jcl | DSNTEP2 | DDL SQL | — | DB2 (CREATE TABLE) |
| MNTTRDB2.jcl | COBTUPDT | Flat file | — | DB2 TRANSACTION_TYPE |
