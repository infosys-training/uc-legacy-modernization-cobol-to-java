# Dependency Map — CardDemo COBOL Estate

This document maps inter-program calls, dataset lineage (JCL → files → programs), and the end-to-end batch pipeline flow.

---

## 1. Inter-Program Call Graph

### 1.1 CICS Transfer of Control (XCTL / LINK)

The CICS online programs use a navigation pattern driven by the COMMAREA. The sign-on program dispatches to menu programs, which in turn transfer control to functional programs.

```
COSGN00C (Sign-On)
 ├── XCTL → COMEN01C (User Main Menu)         [user type = 'U']
 │          ├── XCTL → COACTVWC (Account View)
 │          ├── XCTL → COACTUPC (Account Update)
 │          ├── XCTL → COCRDLIC (Card List)
 │          ├── XCTL → COCRDSLC (Card View)
 │          ├── XCTL → COCRDUPC (Card Update)
 │          ├── XCTL → COTRN00C (Transaction List)
 │          ├── XCTL → COTRN01C (Transaction View)
 │          ├── XCTL → COTRN02C (Transaction Add)
 │          ├── XCTL → CORPT00C (Reports)
 │          ├── XCTL → COBIL00C (Bill Payment)
 │          └── XCTL → COPAUS0C (Pending Auth View)*
 │
 └── XCTL → COADM01C (Admin Menu)             [user type = 'A']
            ├── XCTL → COUSR00C (User List)
            ├── XCTL → COUSR01C (User Add)
            ├── XCTL → COUSR02C (User Update)
            ├── XCTL → COUSR03C (User Delete)
            ├── XCTL → COTRTLIC (Tran Type List)*
            └── XCTL → COTRTUPC (Tran Type Update)*

   * These programs reside in sub-application directories
```

### 1.2 Batch Program CALL Dependencies

```
CBSTM03A (Statement Driver)
 └── CALL → CBSTM03B (Statement File Processor)

COTRN02C (Transaction Add — online)
 └── CALL → CSUTLDTC (Date Utility)

CORPT00C (Reports — online)
 └── CALL → CSUTLDTC (Date Utility)

COBSWAIT (Wait Utility)
 └── CALL → MVSWAIT (Assembler routine — external)

CBACT01C (Read Account File)
 └── CALL → CEE3ABD (LE Abend handler — external)
 └── CALL → COBDATFT (Date Formatter — assembler, external)

COPAUA0C (Authorization Decision)
 ├── MQ calls: MQOPEN, MQGET, MQPUT1, MQCLOSE
 └── DLI calls: CBLTDLI (IMS DL/I interface)

DBUNLDGS, PAUDBLOD, PAUDBUNL (IMS Batch Utilities)
 └── CALL → CBLTDLI (IMS DL/I interface)
```

### 1.3 Online ↔ Batch Program Sharing

Several online CICS programs share copybooks and indirectly reference the same data entities as batch programs:

| Shared Entity | Online Programs | Batch Programs | Copybook |
|--------------|----------------|----------------|----------|
| Account | COACTVWC, COACTUPC, COBIL00C, COTRN02C | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C | CVACT01Y |
| Card | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C | CVACT02Y |
| Card XRef | COACTUPC, COTRN02C, COPAUA0C | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C | CVACT03Y |
| Customer | COACTVWC, COACTUPC, COCRDSLC, COCRDUPC, COPAUA0C | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, CBSTM03A | CVCUS01Y |
| Transaction | COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C | CVTRA05Y |
| User Security | COSGN00C, COUSR00C-03C | — | CSUSR01Y |

---

## 2. Dataset Lineage Map

### 2.1 VSAM Master Files

| VSAM Dataset | DD Name(s) | Record Layout | JCL Define Job | Programs That Read | Programs That Write/Update |
|-------------|-----------|--------------|----------------|-------------------|--------------------------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | ACCTFILE, ACCTDAT | CVACT01Y (300 bytes) | ACCTFILE.jcl | CBACT01C, CBEXPORT, CBSTM03B, CBTRN01C | CBACT04C (I-O), CBIMPORT, CBTRN02C (I-O), COACTUPC (CICS), COBIL00C (CICS), COTRN02C (CICS) |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CARDFILE, CARDDAT | CVACT02Y (150 bytes) | CARDFILE.jcl | CBACT02C, CBEXPORT, CBTRN01C | CBIMPORT, COCRDLIC (CICS browse), COCRDUPC (CICS), COACTUPC (CICS) |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS.AIX | CARDAIX | CVACT02Y (via path) | CARDFILE.jcl (STEP40-60) | COCRDLIC (CICS), COACTUPC (CICS) | — (maintained by VSAM) |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CUSTFILE, CUSTDAT | CVCUS01Y (500 bytes) | CUSTFILE.jcl | CBCUS01C, CBEXPORT, CBSTM03B, CBTRN01C | CBIMPORT, COACTUPC (CICS) |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | XREFFILE, CARDXREF | CVACT03Y (50 bytes) | XREFFILE.jcl | CBACT03C, CBACT04C, CBEXPORT, CBSTM03B, CBTRN01C, CBTRN02C, CBTRN03C | CBIMPORT, COTRN02C (CICS), COPAUA0C (CICS) |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS.AIX | CXREFD (AIX path) | CVACT03Y (via path) | XREFFILE.jcl (STEP20-30) | COPAUA0C (CICS) | — (maintained by VSAM) |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | TRANFILE, TRANSACT | CVTRA05Y (350 bytes) | TRANFILE.jcl | CBACT04C, CBEXPORT, CBTRN03C | CBTRN01C (I-O), CBTRN02C (I-O), COTRN02C (CICS write), COBIL00C (CICS write), CBIMPORT |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS.AIX | TRANIDX (AIX path) | CVTRA05Y (via path) | TRANIDX.jcl | COTRN00C (CICS browse) | — (maintained by VSAM) |
| AWS.M2.CARDDEMO.TCATBAL.VSAM.KSDS | TCATBALF | CVTRA01Y (50 bytes) | TCATBALF.jcl | CBACT04C | CBTRN02C (I-O) |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | TRANTYPE | CVTRA03Y (60 bytes) | TRANTYPE.jcl | CBTRN03C | — |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | TRANCATG | CVTRA04Y (60 bytes) | TRANCATG.jcl | CBTRN03C | — |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | DISCGRP | CVTRA02Y (50 bytes) | DISCGRP.jcl | CBACT04C | — |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | USRSEC | CSUSR01Y (80 bytes) | DUSRSECJ.jcl | COSGN00C (CICS), COUSR00C (CICS) | COUSR01C (write), COUSR02C (rewrite), COUSR03C (delete) |

### 2.2 Sequential / GDG Files

| Dataset Pattern | DD Name | Record Layout | Producer JCL/Program | Consumer JCL/Program |
|----------------|---------|--------------|---------------------|---------------------|
| AWS.M2.CARDDEMO.DALYTRAN.PS | DALYTRAN | CVTRA06Y (350 bytes) | External feed | POSTTRAN.jcl → CBTRN02C, CBTRN01C |
| AWS.M2.CARDDEMO.DALYREJS.PS(+1) | DALYREJS | CVTRA06Y (350 bytes) | CBTRN02C (writes rejected trans) | Audit review (manual) |
| AWS.M2.CARDDEMO.TRANREPT.PS(+1) | TRANREPT | CVTRA07Y (133 bytes) | TRANREPT.jcl → CBTRN03C | TXT2PDF1.JCL (convert to PDF) |
| AWS.M2.CARDDEMO.STMT.PS(+1) | STMTFILE | COSTM01 | CREASTMT.JCL → CBSTM03A | Statement distribution |
| AWS.M2.CARDDEMO.STMT.HTML(+1) | HTMLFILE | HTML format | CBSTM03A | Web delivery |
| AWS.M2.CARDDEMO.EXPORT.PS | EXPFILE | CVEXPORT (500 bytes) | CBEXPORT.jcl → CBEXPORT | CBIMPORT.jcl → CBIMPORT |
| AWS.M2.CARDDEMO.TRANTYPE.PS | TRANTYPE.PS | 60-byte fixed | TRANEXTR.jcl (Db2 extract) | CBTRN03C (via TRANTYPE DD) |
| AWS.M2.CARDDEMO.TRANCATG.PS | TRANCATG.PS | 60-byte fixed | TRANEXTR.jcl (Db2 extract) | CBTRN03C (via TRANCATG DD) |
| AWS.M2.CARDDEMO.TRANTYPE.BKUP(+1) | GDG backup | 60 bytes | TRANEXTR.jcl STEP10 | Disaster recovery |
| AWS.M2.CARDDEMO.TRANCATG.PS.BKUP(+1) | GDG backup | 60 bytes | TRANEXTR.jcl STEP20 | Disaster recovery |
| AWS.M2.CARDDEMO.ACCTDATA.PS | ACCTDATA.PS | 300-byte flat | Seed data | ACCTFILE.jcl STEP15 (REPRO into VSAM) |
| AWS.M2.CARDDEMO.CARDDATA.PS | CARDDATA.PS | 150-byte flat | Seed data | CARDFILE.jcl STEP15 (REPRO into VSAM) |
| AWS.M2.CARDDEMO.CUSTDATA.PS | CUSTDATA.PS | 500-byte flat | Seed data | CUSTFILE.jcl STEP15 (REPRO into VSAM) |
| AWS.M2.CARDDEMO.CARDXREF.PS | CARDXREF.PS | 50-byte flat | Seed data | XREFFILE.jcl STEP15 (REPRO into VSAM) |

### 2.3 Db2 Tables

| Table | Schema | Programs That Read | Programs That Write/Update |
|-------|--------|-------------------|--------------------------|
| CARDDEMO.TRANSACTION_TYPE | CARDDEMO | COTRTLIC (SELECT), COTRTUPC (SELECT), TRANEXTR.jcl (DSNTIAUL) | COBTUPDT (INSERT/UPDATE/DELETE), COTRTUPC (INSERT/UPDATE), CREADB21.jcl (initial load) |
| CARDDEMO.TRANSACTION_TYPE_CATEGORY | CARDDEMO | COTRTLIC (SELECT), TRANEXTR.jcl (DSNTIAUL) | CREADB21.jcl (initial load) |
| CARDDEMO.AUTHFRDS | CARDDEMO | — | COPAUS2C (INSERT — fraud marking) |
| SYSIBM.SYSDUMMY1 | SYSIBM | CSDB2RPY (priming query) | — |

### 2.4 IMS Databases

| Database | PCB/PSB | Programs That Read | Programs That Write/Update |
|----------|---------|-------------------|--------------------------|
| PAUT (Pending Auth) | PSBPAUTB / PAUTBPCB | CBPAUP0C (GN/GNP), COPAUS0C (GNP/GU), COPAUS1C (GU/GNP), PAUDBUNL (GN), DBUNLDGS (GN) | CBPAUP0C (DLET), COPAUA0C (ISRT/REPL), COPAUS1C (REPL), PAUDBLOD (ISRT) |

### 2.5 MQ Queues

| Queue | Programs That Get | Programs That Put |
|-------|------------------|------------------|
| Authorization Request Queue | COPAUA0C (MQGET), COACCT01 (MQGET), CODATE01 (MQGET) | External systems |
| Authorization Response Queue | External systems | COPAUA0C (MQPUT1) |
| Account Response Queue | External systems | COACCT01 (MQPUT) |
| Date Response Queue | External systems | CODATE01 (MQPUT) |

---

## 3. End-to-End Batch Pipeline Flow

The daily batch processing cycle follows a **Close → Process → Open** pattern to manage CICS file sharing with batch programs.

```
┌──────────────────────────────────────────────────────────────┐
│                   DAILY BATCH CYCLE                           │
│                                                              │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Phase 0: INFRASTRUCTURE SETUP (One-Time / As Needed)    │ │
│  │                                                         │ │
│  │  DEFGDGB.jcl  ─── Define GDG base entries               │ │
│  │  DEFGDGD.jcl  ─── Define Db2-related GDG bases          │ │
│  │  REPTFILE.jcl ─── Define report file GDG                 │ │
│  │  DALYREJS.jcl ─── Define daily rejections GDG            │ │
│  │  ACCTFILE.jcl ─── Define Account VSAM                    │ │
│  │  CARDFILE.jcl ─── Define Card VSAM + AIX                 │ │
│  │  CUSTFILE.jcl ─── Define Customer VSAM                   │ │
│  │  XREFFILE.jcl ─── Define XRef VSAM + AIX                 │ │
│  │  TRANFILE.jcl ─── Define Transaction VSAM + AIX          │ │
│  │  TRANIDX.jcl  ─── Define Transaction AIX                 │ │
│  │  TCATBALF.jcl ─── Define Tran Cat Balance VSAM           │ │
│  │  DISCGRP.jcl  ─── Define Disclosure Group VSAM           │ │
│  │  TRANTYPE.jcl ─── Define Tran Type VSAM                  │ │
│  │  TRANCATG.jcl ─── Define Tran Category VSAM              │ │
│  │  DUSRSECJ.jcl ─── Define User Security VSAM              │ │
│  │  CREADB21.jcl ─── Create Db2 tables + load ref data      │ │
│  │  CBADMCDJ.jcl ─── Install CICS CSD definitions           │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Phase 1: CLOSE CICS FILES                               │ │
│  │                                                         │ │
│  │  CLOSEFIL.jcl ─── SDSF → CEMT SET FILE(...) CLOSED      │ │
│  └─────────────────────────────────────────────────────────┘ │
│                           │                                  │
│                           ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Phase 2: DAILY TRANSACTION PROCESSING                   │ │
│  │                                                         │ │
│  │  ┌───────────────────────────────────────────────────┐  │ │
│  │  │ Step 2a: Extract Db2 Reference Data               │  │ │
│  │  │  TRANEXTR.jcl                                     │  │ │
│  │  │    STEP10: Backup TRANTYPE.PS → GDG               │  │ │
│  │  │    STEP20: Backup TRANCATG.PS → GDG               │  │ │
│  │  │    STEP30: Delete old PS files                     │  │ │
│  │  │    STEP40: DSNTIAUL → TRANTYPE.PS                 │  │ │
│  │  │    STEP50: DSNTIAUL → TRANCATG.PS                 │  │ │
│  │  └───────────────────────────────────────────────────┘  │ │
│  │                         │                                │ │
│  │                         ▼                                │ │
│  │  ┌───────────────────────────────────────────────────┐  │ │
│  │  │ Step 2b: Combine Daily Transactions               │  │ │
│  │  │  COMBTRAN.jcl                                     │  │ │
│  │  │    STEP05R: SORT daily transaction file            │  │ │
│  │  │    STEP10:  IDCAMS REPRO sorted → TRANSACT VSAM   │  │ │
│  │  └───────────────────────────────────────────────────┘  │ │
│  │                         │                                │ │
│  │                         ▼                                │ │
│  │  ┌───────────────────────────────────────────────────┐  │ │
│  │  │ Step 2c: Post Daily Transactions                  │  │ │
│  │  │  POSTTRAN.jcl                                     │  │ │
│  │  │    STEP05R: SORT DALYTRAN                         │  │ │
│  │  │    STEP05:  CBTRN02C                              │  │ │
│  │  │      Reads DALYTRAN (daily feed)                  │  │ │
│  │  │      Validates each transaction                   │  │ │
│  │  │      Updates ACCTFILE balances (I-O)              │  │ │
│  │  │      Updates TRANFILE master (I-O)                │  │ │
│  │  │      Updates TCATBALF balances (I-O)              │  │ │
│  │  │      Writes rejects → DALYREJS GDG(+1)           │  │ │
│  │  └───────────────────────────────────────────────────┘  │ │
│  │                         │                                │ │
│  │                         ▼                                │ │
│  │  ┌───────────────────────────────────────────────────┐  │ │
│  │  │ Step 2d: Calculate Interest & Fees                │  │ │
│  │  │  INTCALC.jcl                                      │  │ │
│  │  │    STEP05: CBACT04C                               │  │ │
│  │  │      Reads TCATBALF (category balances)           │  │ │
│  │  │      Reads XREFFILE (cross references)            │  │ │
│  │  │      Reads DISCGRP (interest rates)               │  │ │
│  │  │      Updates ACCTFILE balances (I-O)              │  │ │
│  │  │      Reads TRANSACT for verification              │  │ │
│  │  └───────────────────────────────────────────────────┘  │ │
│  └─────────────────────────────────────────────────────────┘ │
│                           │                                  │
│                           ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Phase 3: REPORTING                                      │ │
│  │                                                         │ │
│  │  ┌───────────────────────────────────────────────────┐  │ │
│  │  │ Step 3a: Transaction Report                       │  │ │
│  │  │  TRANREPT.jcl                                     │  │ │
│  │  │    STEP05R: SORT trans by account                  │  │ │
│  │  │    STEP10R: CBTRN03C                              │  │ │
│  │  │      Reads sorted TRANSACT                        │  │ │
│  │  │      Reads TRANTYPE.PS, TRANCATG.PS              │  │ │
│  │  │      Reads CARDXREF for account lookup            │  │ │
│  │  │      Writes TRANREPT report → GDG(+1)            │  │ │
│  │  └───────────────────────────────────────────────────┘  │ │
│  │                         │                                │ │
│  │                         ▼                                │ │
│  │  ┌───────────────────────────────────────────────────┐  │ │
│  │  │ Step 3b: Statement Generation                     │  │ │
│  │  │  CREASTMT.JCL                                     │  │ │
│  │  │    STEP010: SORT trans by account                  │  │ │
│  │  │    STEP020: REPRO sorted to work file              │  │ │
│  │  │    STEP040: CBSTM03A → CBSTM03B                   │  │ │
│  │  │      Reads TRANSACT, XREF, CUST, ACCT             │  │ │
│  │  │      Writes STMTFILE, HTMLFILE → GDG(+1)          │  │ │
│  │  └───────────────────────────────────────────────────┘  │ │
│  │                         │                                │ │
│  │                         ▼                                │ │
│  │  ┌───────────────────────────────────────────────────┐  │ │
│  │  │ Step 3c: PDF Conversion (Optional)                │  │ │
│  │  │  TXT2PDF1.JCL                                     │  │ │
│  │  │    Convert text reports to PDF format              │  │ │
│  │  └───────────────────────────────────────────────────┘  │ │
│  └─────────────────────────────────────────────────────────┘ │
│                           │                                  │
│                           ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Phase 4: BACKUP & HOUSEKEEPING                          │ │
│  │                                                         │ │
│  │  TRANBKP.jcl                                            │ │
│  │    STEP05R: REPRO TRANSACT → backup PS                  │ │
│  │    STEP05:  REPRO to GDG(+1)                            │ │
│  │    STEP10:  DELETE/DEFINE TRANSACT (reset for next day)  │ │
│  │                                                         │ │
│  │  CBPAUP0J.jcl (Authorization module)                    │ │
│  │    STEP01: CBPAUP0C — delete expired pending auths      │ │
│  └─────────────────────────────────────────────────────────┘ │
│                           │                                  │
│                           ▼                                  │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ Phase 5: REOPEN CICS FILES                              │ │
│  │                                                         │ │
│  │  OPENFIL.jcl ─── SDSF → CEMT SET FILE(...) OPEN ENABLED │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ MIGRATION PIPELINE (On-Demand)                          │ │
│  │                                                         │ │
│  │  CBEXPORT.jcl                                           │ │
│  │    STEP01: REPRO TRANSACT → PS                          │ │
│  │    STEP02: CBEXPORT                                     │ │
│  │      Reads CUSTFILE, ACCTFILE, XREFFILE, TRANSACT,      │ │
│  │            CARDFILE                                     │ │
│  │      Writes unified EXPFILE (500-byte multi-record)     │ │
│  │                       │                                  │ │
│  │                       ▼                                  │ │
│  │  CBIMPORT.jcl                                           │ │
│  │    STEP01: CBIMPORT                                     │ │
│  │      Reads EXPFILE                                      │ │
│  │      Writes CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT,         │ │
│  │            CARDOUT, ERROUT                              │ │
│  └─────────────────────────────────────────────────────────┘ │
│                                                              │
│  ┌─────────────────────────────────────────────────────────┐ │
│  │ DB2 MAINTENANCE (Scheduled)                             │ │
│  │                                                         │ │
│  │  MNTTRDB2.jcl                                           │ │
│  │    STEP1: COBTUPDT (batch Db2 maintenance from file)    │ │
│  └─────────────────────────────────────────────────────────┘ │
└──────────────────────────────────────────────────────────────┘
```

---

## 4. Copybook Dependency Matrix

Programs grouped by the number of copybooks they reference (from `app/cpy/` and sub-application `cpy/` directories):

| Copybook | Used By (Programs) | Count |
|----------|-------------------|-------|
| COCOM01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC | 21 |
| COTTL01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC | 21 |
| CSDAT01Y | (same 21 CICS programs as COCOM01Y) | 21 |
| CSMSG01Y | (same 21 CICS programs as COCOM01Y) | 21 |
| DFHAID | (same 21 CICS programs — IBM supplied) | 21 |
| DFHBMSCA | (same 21 CICS programs — IBM supplied) | 21 |
| CVACT01Y | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C, COACCT01 | 13 |
| CVACT03Y | CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN02C, CBTRN03C, COACTUPC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C | 11 |
| CVCUS01Y | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C | 10 |
| CVTRA05Y | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C | 11 |
| CSUSR01Y | COACTUPC, COADM01C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC | 13 |
| CVCRD01Y | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC | 7 |
| CVACT02Y | CBACT02C, CBEXPORT, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, CBTRN01C, COPAUS0C, COTRTLIC | 9 |
| CSMSG02Y | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUS0C, COPAUS1C, COTRTUPC | 7 |
| CSSTRPFY | COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COTRTLIC, COTRTUPC | 7 |
| CIPAUSMY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL | 7 |
| CIPAUDTY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL | 8 |

---

## 5. Data Flow Summary Diagram

```
                     ┌─────────────────┐
                     │  External Feed  │
                     │ (Daily Trans)   │
                     └────────┬────────┘
                              │
                              ▼
                     ┌─────────────────┐
                     │  DALYTRAN.PS    │
                     │  (Sequential)   │
                     └────────┬────────┘
                              │
                 ┌────────────┼────────────┐
                 ▼            ▼            ▼
          ┌──────────┐ ┌──────────┐ ┌──────────┐
          │ CBTRN01C │ │ CBTRN02C │ │  SORT    │
          │ (Post)   │ │ (Validate│ │(COMBTRAN)│
          └────┬─────┘ │ & Post)  │ └────┬─────┘
               │       └────┬─────┘      │
               │            │            │
               ▼            ▼            ▼
     ┌───────────────────────────────────────────┐
     │           VSAM Master Files               │
     │                                           │
     │  ACCTFILE ◄──► CBACT04C (Interest Calc)   │
     │  CARDFILE       TRANFILE                  │
     │  CUSTFILE       XREFFILE                  │
     │  TCATBALF       DISCGRP                   │
     └───────────────┬───────────────────────────┘
                     │
         ┌───────────┼───────────┐
         ▼           ▼           ▼
   ┌──────────┐ ┌──────────┐ ┌──────────┐
   │ CBTRN03C │ │ CBSTM03A │ │ CBEXPORT │
   │ (Report) │ │(Statement│ │ (Export) │
   └────┬─────┘ │ Driver)  │ └────┬─────┘
        │       └────┬─────┘      │
        │            │            │
        ▼            ▼            ▼
   TRANREPT.PS  STMTFILE.PS  EXPFILE.PS
   (GDG)        (GDG)        (Migration)
                                  │
                                  ▼
                            ┌──────────┐
                            │ CBIMPORT │
                            │ (Import) │
                            └──────────┘
```
