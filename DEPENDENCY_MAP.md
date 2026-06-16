# DEPENDENCY MAP — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System  
> **Repo:** `infosys-training/uc-legacy-modernization-cobol-to-java`  
> **Generated:** 2026-06-16

---

## 1. Online Program Call Graph (CICS XCTL / LINK)

### 1.1 Navigation Tree

```
COSGN00C (Sign-On — Entry Point)
│
├──[Admin users]──► COADM01C (Admin Menu Hub)
│                   ├── XCTL ──► COUSR00C (User List)
│                   │               ├── XCTL ──► COUSR02C (User Update)
│                   │               └── XCTL ──► COUSR03C (User Delete)
│                   ├── XCTL ──► COUSR01C (User Add)
│                   ├── XCTL ──► COTRTLIC (Tran Type List — DB2)
│                   └── XCTL ──► COTRTUPC (Tran Type Update — DB2)
│
└──[Regular users]──► COMEN01C (Main Menu Hub — 11 targets)
                      ├── XCTL ──► COACTVWC (Account View)
                      ├── XCTL ──► COACTUPC (Account Update — 4,236 LOC)
                      ├── XCTL ──► COCRDLIC (Card List)
                      │               ├── XCTL ──► COCRDSLC (Card View)
                      │               └── XCTL ──► COCRDUPC (Card Update)
                      ├── XCTL ──► COTRN00C (Transaction List)
                      │               └── XCTL ──► COTRN01C (Transaction View)
                      ├── XCTL ──► COTRN02C (Transaction Add)
                      ├── XCTL ──► CORPT00C (Report Request)
                      │               └── SPOOL ──► JCL INTRDR (batch report)
                      ├── XCTL ──► COBIL00C (Bill Payment)
                      └── XCTL ──► COPAUS0C (Auth Summary — IMS)
                                      └── LINK ──► COPAUS1C (Auth Detail)
                                                     └── LINK ──► COPAUS2C (Fraud Insert — DB2)
```

### 1.2 Inter-Program Dependencies Matrix (Online)

| From Program | To Program | Mechanism | Direction |
|-------------|------------|-----------|-----------|
| COSGN00C | COADM01C | XCTL | Admin path |
| COSGN00C | COMEN01C | XCTL | User path |
| COADM01C | COUSR00C | XCTL | — |
| COADM01C | COUSR01C | XCTL | — |
| COADM01C | COUSR02C | XCTL | — |
| COADM01C | COUSR03C | XCTL | — |
| COADM01C | COTRTLIC | XCTL | — |
| COADM01C | COTRTUPC | XCTL | — |
| COMEN01C | COACTVWC | XCTL | — |
| COMEN01C | COACTUPC | XCTL | — |
| COMEN01C | COCRDLIC | XCTL | — |
| COMEN01C | COCRDSLC | XCTL | — |
| COMEN01C | COCRDUPC | XCTL | — |
| COMEN01C | COTRN00C | XCTL | — |
| COMEN01C | COTRN01C | XCTL | — |
| COMEN01C | COTRN02C | XCTL | — |
| COMEN01C | CORPT00C | XCTL | — |
| COMEN01C | COBIL00C | XCTL | — |
| COMEN01C | COPAUS0C | XCTL | — |
| COCRDLIC | COCRDSLC | XCTL | Card detail |
| COCRDLIC | COCRDUPC | XCTL | Card update |
| COTRN00C | COTRN01C | XCTL | Tran detail |
| COPAUS0C | COPAUS1C | LINK | Auth detail chain |
| COPAUS1C | COPAUS2C | LINK | Fraud insert |
| CORPT00C | JCL INTRDR | SPOOLWRITE | Batch submission |
| All CICS programs | COMEN01C/COADM01C | XCTL (PF3 back) | Return to menu |

### 1.3 Hub Dependency Counts

| Hub Program | Outbound XCTL Targets | Inbound (programs that return here) |
|-------------|----------------------|--------------------------------------|
| COMEN01C | 11 | 11 (all user screens return via PF3) |
| COADM01C | 6 | 6 (all admin screens return via PF3) |
| COSGN00C | 2 | 0 (entry point only) |

---

## 2. Batch Program Call Graph

### 2.1 CALL Dependencies

```
CBACT01C ──CALL──► COBDATFT        (assembler: date formatting)
CBACT02C ──CALL──► CEE3ABD         (LE: abnormal termination)
CBACT03C ──CALL──► CEE3ABD         (LE: abnormal termination)
CBCUS01C ──CALL──► CEE3ABD         (LE: abnormal termination)
COBSWAIT ──CALL──► MVSWAIT         (assembler: timed wait)
CSUTLDTC ──CALL──► CEEDAYS         (LE: date-to-Lilian conversion)
```

### 2.2 MQ Program Dependencies

```
COPAUA0C ──CALL──► MQOPEN          (Open request queue)
         ──CALL──► MQGET           (Read auth request)
         ──CALL──► MQPUT1          (Send auth reply)
         ──CICS READ──► XREFFILE, ACCTFILE, CUSTFILE

COACCT01 ──CALL──► MQOPEN / MQGET / MQPUT1
         ──CICS READ──► ACCTFILE, CARDXREF, CUSTFILE

CODATE01 ──CALL──► MQOPEN / MQGET / MQPUT1
         (date conversion only — no file I/O)
```

### 2.3 IMS DL/I Dependencies

```
CBPAUP0C ──DL/I──► DBPAUTP0 (PSB: PSBPAUTB)
         GN (Get Next — sequential summary read)
         GNP (Get Next within Parent — detail read)
         DLET (Delete expired auths)

COPAUA0C ──DL/I──► DBPAUTP0
         GU (Get Unique — direct lookup)
         SCHD (Schedule PSB)
         TERM (Terminate PSB)

COPAUS0C ──DL/I──► DBPAUTP0
         GU, GNP (Browse summary + detail)

COPAUS1C ──DL/I──► DBPAUTP0
         GU, GNP, REPL (Read + update detail)
```

### 2.4 DB2 SQL Dependencies

| Program | DB2 Table | Operations |
|---------|-----------|------------|
| COTRTLIC | TRANSACTION_TYPE | DECLARE CURSOR, OPEN, FETCH, CLOSE, DELETE |
| COTRTUPC | TRANSACTION_TYPE | SELECT, UPDATE, DELETE, INSERT |
| COTRTUPC | TRANSACTION_CATEGORY | SELECT, UPDATE, DELETE, INSERT (cascade) |
| COPAUS2C | AUTHFRDS | INSERT, SELECT |
| COBTUPDT | TRANSACTION_TYPE | INSERT, UPDATE, DELETE |

---

## 3. VSAM Dataset Lineage

### 3.1 File-to-Program Map

| VSAM File (DD Name) | Dataset Name | Writers (batch) | Writers (CICS) | Readers (batch) | Readers (CICS) |
|---------------------|--------------|-----------------|----------------|-----------------|----------------|
| ACCTFILE | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | CBTRN02C (RW), CBACT04C (RW), CBIMPORT (W) | COACTUPC (RW), COBIL00C (RW), COTRN02C (RW) | CBACT01C, CBTRN01C, CBEXPORT | COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, COACCT01 |
| CARDFILE | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CBIMPORT (W) | COCRDUPC (RW) | CBACT02C, CBTRN01C, CBEXPORT | COCRDLIC (browse), COCRDSLC, COPAUS0C |
| CUSTFILE | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CBIMPORT (W) | — | CBCUS01C, CBTRN01C, CBEXPORT | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, COACCT01 |
| XREFFILE (CARDXREF) | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | CBIMPORT (W) | — | CBTRN01C, CBTRN02C, CBACT04C, CBEXPORT, CBTRN03C | COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COACCT01 |
| TRANSACT (TRANFILE) | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | CBTRN02C (W), CBIMPORT (W) | COBIL00C (W), COTRN02C (W) | CBTRN03C, CBEXPORT | COTRN00C (browse), COTRN01C, COBIL00C (browse) |
| DALYTRAN | AWS.M2.CARDDEMO.DALYTRAN.PS | (external input) | — | CBTRN01C, CBTRN02C | — |
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.PS | CBTRN02C (RW) | — | CBACT04C | — |
| TRANTYPE | AWS.M2.CARDDEMO.TRANTYPE.PS | — | — | CBTRN03C | — |
| TRANCATG | AWS.M2.CARDDEMO.TRANCATG.PS | — | — | CBTRN03C | — |
| DISCGRP | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | — | — | CBACT04C | — |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | — | COUSR01C (W), COUSR02C (RW), COUSR03C (DEL) | — | COSGN00C (R), COUSR00C (browse) |
| DALYREJS | AWS.M2.CARDDEMO.DALYREJS | CBTRN02C (W) | — | — | — |
| EXPFILE | AWS.M2.CARDDEMO.EXPORT.DATA | CBEXPORT (W) | — | CBIMPORT (R) | — |

### 3.2 Data Flow Diagram

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  DALYTRAN    │────►│  CBTRN01C    │────►│  Validation  │
│  (Daily In)  │     │  (Validate)  │     │  (display)   │
└──────────────┘     └──────────────┘     └──────────────┘
       │
       ▼
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│  CBTRN02C    │────►│  TRANSACT    │────►│  CBTRN03C    │────► TRANREPT
│  (Post)      │     │  (Master)    │     │  (Report)    │     (Report)
│              │────►│  ACCTFILE    │     └──────────────┘
│              │     │  (Balances)  │
│              │────►│  TCATBALF    │────►│  CBACT04C    │────► ACCTFILE
│              │     │  (Cat Bal)   │     │  (Interest)  │     (Updated)
│              │────►│  DALYREJS    │     └──────────────┘
│              │     │  (Rejects)   │
└──────────────┘     └──────────────┘
```

---

## 4. JCL Job-to-Program-to-Dataset Lineage

### 4.1 Batch Processing Pipeline

| Pipeline Step | JCL Job | Program | Reads | Writes/Updates |
|--------------|---------|---------|-------|----------------|
| 1. Post Transactions | POSTTRAN.jcl | CBTRN02C | DALYTRAN, XREFFILE | TRANSACT (W), ACCTFILE (RW), TCATBALF (RW), DALYREJS (W) |
| 2. Calculate Interest | INTCALC.jcl | CBACT04C | TCATBALF, XREFFILE, DISCGRP, ACCTFILE | ACCTFILE (RW), TRANSACT (W) |
| 3. Generate Report | TRANREPT.jcl | CBTRN03C | TRANSACT, CARDXREF, TRANTYPE, TRANCATG, DATEPARM | TRANREPT (W) |

### 4.2 Data Setup Pipeline

| JCL Job | Purpose | Utility | Dataset Affected |
|---------|---------|---------|------------------|
| ACCTFILE.jcl | Load account data | IDCAMS | ACCTDATA.VSAM.KSDS |
| CARDFILE.jcl | Load card data + build AIX | IDCAMS | CARDDATA.VSAM.KSDS + AIX |
| CUSTFILE.jcl | Load customer data | IDCAMS | CUSTDATA.VSAM.KSDS |
| XREFFILE.jcl | Load card-xref + build AIX | IDCAMS | CARDXREF.VSAM.KSDS + AIX |
| TRANFILE.jcl | Load transaction master | IDCAMS | TRANSACT.VSAM.KSDS |
| TCATBALF.jcl | Load category balances | IDCAMS | TCATBALF.VSAM.KSDS |
| TRANTYPE.jcl | Load transaction types | IDCAMS | TRANTYPE.VSAM.KSDS |
| TRANCATG.jcl | Load transaction categories | IDCAMS | TRANCATG.VSAM.KSDS |
| DISCGRP.jcl | Load disclosure groups | IDCAMS | DISCGRP.VSAM.KSDS |
| DUSRSECJ.jcl | Create user security file | IEBGENER + IDCAMS | USRSEC.VSAM.KSDS |
| DEFGDGB.jcl | Define GDG bases (6 GDGs) | IDCAMS | TRANSACT.BKUP, DALY, TRANREPT, etc. |
| DEFGDGD.jcl | Define DB2 reference GDGs | IDCAMS + IEBGENER | TRANTYPE.BKUP, TRANCATG.BKUP, DISCGRP.BKUP |
| COMBTRAN.jcl | Combine + sort transactions | SORT + IDCAMS | TRANSACT.COMBINED → TRANSACT.VSAM.KSDS |

### 4.3 Export/Import Pipeline

| JCL Job | Program | Input | Output |
|---------|---------|-------|--------|
| CBEXPORT.jcl | CBEXPORT | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | EXPFILE (multi-record) |
| CBIMPORT.jcl | CBIMPORT | EXPFILE | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT |

### 4.4 DB2 Maintenance Pipeline

| JCL Job | Program/Utility | DB2 Tables |
|---------|----------------|------------|
| CREADB21.jcl | DSNTIAD (DDL) | CREATE TRANSACTION_TYPE, TRANSACTION_CATEGORY + indexes |
| MNTTRDB2.jcl | COBTUPDT | INSERT/UPDATE/DELETE on TRANSACTION_TYPE |
| TRANEXTR.jcl | DSNTIAD (SQL) | SELECT from TRANSACTION_TYPE → flat file |

### 4.5 IMS Maintenance Pipeline

| JCL Job | Program | IMS Database |
|---------|---------|-------------|
| CBPAUP0J.jcl | CBPAUP0C (BMP) | DBPAUTP0 — purge expired auths |
| DBPAUTP0.jcl | DFSURGU0 (ULU) | DBPAUTP0 — unload database |

---

## 5. Copybook Dependency Map

### 5.1 Most-Referenced Copybooks

| Copybook | Used By (count) | Role |
|----------|-----------------|------|
| COCOM01Y | 22 programs | CICS COMMAREA — inter-program data passing |
| DFHAID | 19 programs | CICS AID key definitions (PF keys) |
| DFHBMSCA | 19 programs | BMS screen attribute constants |
| COTTL01Y | 19 programs | Screen title constants |
| CSDAT01Y | 19 programs | Date/time working storage |
| CSMSG01Y | 19 programs | Common screen messages |
| CSUSR01Y | 12 programs | User security record |
| CVACT01Y | 11 programs | Account record layout |
| CVACT03Y | 10 programs | Card-XREF record layout |
| CVCUS01Y | 9 programs | Customer record layout |
| CVACT02Y | 8 programs | Card record layout |
| CVTRA05Y | 7 programs | Transaction record layout |
| CVCRD01Y | 7 programs | CICS screen work areas |
| CSMSG02Y | 7 programs | Abend handling data |

### 5.2 Copybook-to-Program Cross-Reference

| Copybook | Programs Using It |
|----------|-------------------|
| CVACT01Y | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COPAUA0C, COPAUS0C |
| CVACT02Y | CBACT02C, CBEXPORT, CBIMPORT, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C, COTRTLIC, COTRTUPC |
| CVACT03Y | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COPAUA0C, COPAUS0C |
| CVCUS01Y | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| CVTRA05Y | CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COBIL00C, COTRN00C, COTRN01C, COTRN02C |
| CIPAUSMY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C |
| CIPAUDTY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C |

---

## 6. End-to-End Batch Pipeline Flow

### 6.1 Daily Processing Cycle

```
                    ┌─────────────────────────────────────────────┐
                    │            DAILY BATCH PIPELINE              │
                    │        (scheduled via Control-M)             │
                    └─────────────────────────────────────────────┘

Step 1: POSTTRAN ────────────────────────────────────────────────────
  JCL: POSTTRAN.jcl
  PGM: CBTRN02C (731 LOC)
  IN:  DALYTRAN (daily transactions from POS/ATM/online)
       XREFFILE (card-to-account cross-reference)
  OUT: TRANSACT (posted to master)
       ACCTFILE (balance updated: CURR-BAL, CYC-DEBIT/CREDIT)
       TCATBALF (category balances updated)
       DALYREJS (rejected transactions)
  LOGIC: For each daily tran → validate card/account via XREF →
         if valid: write to TRANSACT, update ACCTFILE balance,
         update TCATBALF; if invalid: write to DALYREJS

Step 2: INTCALC ─────────────────────────────────────────────────────
  JCL: INTCALC.jcl
  PGM: CBACT04C (652 LOC)
  IN:  TCATBALF (category balances from Step 1)
       XREFFILE (card-to-account mapping)
       DISCGRP  (interest rate per group/type/category)
       ACCTFILE (current account data)
  OUT: ACCTFILE (interest/fees applied to balance)
       TRANSACT (interest transactions generated)
  LOGIC: For each account → for each tran category with balance →
         look up disclosure group interest rate →
         compute interest = balance × rate / 365 →
         create interest transaction, update account balance

Step 3: TRANREPT ────────────────────────────────────────────────────
  JCL: TRANREPT.jcl
  PGM: CBTRN03C (649 LOC)
  IN:  TRANSACT (all transactions including interest from Step 2)
       CARDXREF (for account lookup by card)
       TRANTYPE (transaction type descriptions)
       TRANCATG (transaction category descriptions)
       DATEPARM (report date range parameters)
  OUT: TRANREPT (formatted daily transaction report)
  LOGIC: For each transaction in date range → look up card-xref →
         look up type/category descriptions →
         write detail line → accumulate page/account/grand totals

                    ┌─────────────────────────────────────────────┐
                    │         SUPPORTING BATCH JOBS                │
                    └─────────────────────────────────────────────┘

TRANBKP.jcl   — Backup TRANSACT master before next day's processing
COMBTRAN.jcl  — Combine system-generated transactions with backup
WAITSTEP.jcl  — Pipeline pacing (delay between steps)
```

### 6.2 Data Export/Import Pipeline

```
CBEXPORT.jcl (PGM=CBEXPORT)
  ├── Reads: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE
  └── Writes: EXPFILE (multi-record export for branch migration)

CBIMPORT.jcl (PGM=CBIMPORT)
  ├── Reads: EXPFILE
  └── Writes: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT
```

### 6.3 IMS Authorization Pipeline

```
COPAUA0C (CICS, event-driven via MQ)
  ├── MQ MQGET: receive auth request
  ├── CICS READ: XREFFILE, ACCTFILE, CUSTFILE
  ├── IMS DL/I: check authorization history
  ├── Business logic: evaluate fraud rules
  └── MQ MQPUT1: send auth response

CBPAUP0J.jcl (PGM=CBPAUP0C, scheduled batch)
  ├── IMS GN/GNP: read summary + detail segments
  ├── Evaluate: is auth expired?
  └── IMS DLET: delete expired records
```

---

## 7. Suggested Modernization Microservice Boundaries

Based on the dependency analysis, natural service boundaries emerge:

| Microservice | Programs | Shared Data | External Deps |
|-------------|----------|-------------|---------------|
| **user-auth-service** | COSGN00C, COUSR00C-03C, COMEN01C, COADM01C | USRSEC | None |
| **account-service** | COACTUPC, COACTVWC, COACCT01 | ACCTFILE, CUSTFILE | MQ (COACCT01) |
| **card-service** | COCRDLIC, COCRDSLC, COCRDUPC | CARDFILE, XREFFILE | — |
| **transaction-service** | COTRN00C-02C, CBTRN01C-02C, COBIL00C | TRANSACT, DALYTRAN, TCATBALF | — |
| **reporting-service** | CORPT00C, CBTRN03C | TRANSACT (read-only) | — |
| **transaction-type-service** | COTRTLIC, COTRTUPC, COBTUPDT | DB2: TRANSACTION_TYPE/CATEGORY | DB2 |
| **authorization-service** | COPAUA0C, COPAUS0C-2C, CBPAUP0C | IMS: DBPAUTP0; DB2: AUTHFRDS | IMS, MQ, DB2 |
| **data-migration-tools** | CBEXPORT, CBIMPORT | All VSAM files (temporary) | — |
