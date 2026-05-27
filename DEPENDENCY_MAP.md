# DEPENDENCY MAP — CardDemo COBOL Estate

> Call graphs, dataset lineage, and batch pipeline flow extracted from source analysis.

---

## 1. Inter-Program Call Graph

### 1.1 CICS Online — XCTL (Transfer Control) Chain

The online CICS system routes users through a menu-driven flow using `EXEC CICS XCTL`:

```
COSGN00C (Signon)
   ├── [Admin user] → COADM01C (Admin Menu)
   │       ├── COUSR00C (User List) ──→ COUSR02C (User Update)
   │       │                         └─→ COUSR03C (User Delete)
   │       ├── COUSR01C (User Add)
   │       ├── COTRTLIC (Tran Type List, DB2) ──→ COTRTUPC (Tran Type Update, DB2)
   │       └── COTRTUPC (Tran Type Maintenance, DB2)
   │
   └── [Regular user] → COMEN01C (Main Menu)
           ├── COACTVWC (Account View)
           ├── COACTUPC (Account Update)
           ├── COCRDLIC (Card List) ──→ COCRDSLC (Card View)
           │                        └─→ COCRDUPC (Card Update)
           ├── COCRDSLC (Card View)
           ├── COCRDUPC (Card Update)
           ├── COTRN00C (Transaction List) ──→ COTRN01C (Transaction View)
           │                                └─→ COTRN02C (Transaction Add)
           ├── COTRN01C (Transaction View)
           ├── COTRN02C (Transaction Add)
           ├── CORPT00C (Transaction Report Submit)
           ├── COBIL00C (Bill Payment)
           └── COPAUS0C (Pending Auth Summary, IMS) ──→ COPAUS1C (Pending Auth Detail)
                                                     └─→ COPAUS2C (Mark Fraud, IMS/DB2)
```

### 1.2 CICS LINK Calls

| Calling Program | Called Program | Purpose |
|----------------|---------------|---------|
| COPAUS1C | COPAUS2C | Mark authorization as fraud via DB2 INSERT |
| COPAUA0C | _(MQ + IMS DL/I)_ | Authorization decision engine (not LINK; standalone CICS program triggered by MQ) |

### 1.3 Batch CALL Statements

| Calling Program | Called Program/Module | Purpose |
|----------------|----------------------|---------|
| CBSTM03A | CBSTM03B | Subroutine: opens and reads VSAM files for statement generation |
| CBACT04C | CSUTLDTC | Date validation via LE CEEDAYS callable service |
| CBTRN02C | _(internal only)_ | No external CALL — all logic via PERFORM |
| DBUNLDGS | _(IMS DL/I calls)_ | Unloads IMS DB via PCB calls |
| PAUDBLOD | _(IMS DL/I calls)_ | Loads IMS DB via PCB calls |
| PAUDBUNL | _(IMS DL/I calls)_ | Unloads IMS DB to flat files |

### 1.4 MQ Message-Driven Invocations

| Trigger | Program | Direction | Queue |
|---------|---------|-----------|-------|
| MQ message arrival | COPAUA0C | GET from request queue, PUT1 to reply queue | Authorization request/reply |
| MQ message arrival | COACCT01 | GET from request queue, PUT to reply queue | Account inquiry request/reply |
| MQ message arrival | CODATE01 | GET from request queue, PUT to reply queue | Date format request/reply |

---

## 2. Dataset Lineage — JCL Jobs ↔ Files ↔ Programs

### 2.1 VSAM Datasets

| VSAM Dataset | DD Name(s) | KSDS Key | Defining JCL | Writing Programs | Reading Programs |
|-------------|-----------|----------|--------------|-----------------|-----------------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | ACCTFILE, ACCTDAT | ACCT-ID (11 bytes) | ACCTFILE.jcl | COACTUPC (REWRITE), COBIL00C (REWRITE), CBTRN02C (REWRITE) | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A/B, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COACCT01 |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CARDFILE, CARDDAT | CARD-NUM (16 bytes) | CARDFILE.jcl | COCRDUPC (REWRITE/WRITE) | CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.AIX.KSDS | CARDAIX, CARDXREF | CARD-ACCT-ID (11 bytes) | CARDFILE.jcl | _(built by AIX)_ | COACTUPC, COACTVWC, COBIL00C, COCRDLIC, COTRN02C |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CUSTFILE, CUSTDAT | CUST-ID (9 bytes) | CUSTFILE.jcl | _(CICS online updates)_ | CBCUS01C, CBEXPORT, CBSTM03A/B, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COPAUA0C |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | TRANSACT, TRANFILE | TRAN-ID (16 bytes) | TRANFILE.jcl | CBTRN01C, CBTRN02C, COTRN02C (WRITE), COBIL00C (WRITE) | CBEXPORT, CBTRN03C, COTRN00C, COTRN01C |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | XREFFILE | XREF-CARD-NUM (16 bytes) | XREFFILE.jcl | _(initial load only)_ | CBACT03C, CBACT04C, CBEXPORT, CBSTM03A/B, CBTRN01C, CBTRN02C |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | USRSEC | SEC-USR-ID (8 bytes) | DUSRSECJ.jcl | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | COSGN00C, COADM01C, COUSR00C, COUSR02C, COUSR03C |
| AWS.M2.CARDDEMO.TCATBAL.VSAM.KSDS | TCATBALF | Composite (17 bytes) | TCATBALF.jcl | CBTRN02C (WRITE/REWRITE) | CBACT04C |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | DISCGRP | Composite (16 bytes) | DISCGRP.jcl | _(initial load only)_ | CBACT04C |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | TRANTYPE | TRAN-TYPE (2 bytes) | TRANTYPE.jcl | _(initial load only)_ | CBTRN03C |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | TRANCATG | Composite (6 bytes) | TRANCATG.jcl | _(initial load only)_ | CBTRN03C |

### 2.2 Sequential (PS) and GDG Datasets

| Dataset Pattern | DD Name | Purpose | Writing JCL/Program | Reading JCL/Program |
|----------------|---------|---------|---------------------|---------------------|
| AWS.M2.CARDDEMO.ACCTDATA.PSCOMP | OUTFILE | Flat account data dump | READACCT → CBACT01C | _(external/analysis)_ |
| AWS.M2.CARDDEMO.ACCTDATA.ARRYPS | ARRYFILE | Array-format account dump | READACCT → CBACT01C | _(external/analysis)_ |
| AWS.M2.CARDDEMO.ACCTDATA.VBPS | VBRCFILE | Variable-block account dump | READACCT → CBACT01C | _(external/analysis)_ |
| AWS.M2.CARDDEMO.DALYTRAN.* | DALYTRAN | Daily incoming transactions | _(external feed)_ | POSTTRAN → CBTRN02C |
| AWS.M2.CARDDEMO.DALYREJS.* | DALYREJS | Rejected daily transactions | POSTTRAN → CBTRN02C | _(review/resubmit)_ |
| AWS.M2.CARDDEMO.TRANREPT.* | TRANREPT | Printed transaction report | TRANREPT → CBTRN03C | TXT2PDF1 |
| AWS.M2.CARDDEMO.STATEMNT.PS | STMTFILE | Account statement text | CREASTMT → CBSTM03A | TXT2PDF1 |
| AWS.M2.CARDDEMO.EXPORT.* | EXPFILE | Branch migration export | CBEXPORT.jcl → CBEXPORT | CBIMPORT.jcl → CBIMPORT |
| AWS.M2.CARDDEMO.TRXFL.SEQ | INFILE | Sorted transaction seq file | CREASTMT (SORT) | CREASTMT → CBSTM03A |
| AWS.M2.CARDDEMO.TRXFL.VSAM.KSDS | OUTFILE | Statement-sorted VSAM | CREASTMT (IDCAMS REPRO) | CBSTM03A |
| AWS.M2.CARDDEMO.TRANTYPE.PS | — | Transaction type sequential | TRANEXTR (DB2 extract) | DEFGDGD (GDG init) |
| AWS.M2.CARDDEMO.TRANCATG.PS | — | Transaction category sequential | TRANEXTR (DB2 extract) | DEFGDGD (GDG init) |
| AWS.M2.CARDDEMO.TRANTYPE.BKUP(+n) | — | GDG backup of TRANTYPE | TRANEXTR | _(recovery)_ |
| AWS.M2.CARDDEMO.TRANCATG.PS.BKUP(+n) | — | GDG backup of TRANCATG | TRANEXTR | _(recovery)_ |

### 2.3 IMS Database

| Database | PCB / DD | Segments | Programs |
|----------|---------|----------|----------|
| PAUTHDB (Pending Auth) | PAUTBPCB / DDPAUTP0 | Root: CIPAUSMY (Summary), Child: CIPAUDTY (Detail) | **Read:** CBPAUP0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBUNL · **Write:** COPAUA0C, COPAUS2C (REPL), PAUDBLOD · **Delete:** CBPAUP0C |
| PAUTHDB (GSAM paths) | PASFLPCB/PADFLPCB | — | DBUNLDGS (GSAM ISRT) |

### 2.4 DB2 Tables

| Table | Columns (Key) | Programs |
|-------|--------------|----------|
| TRNTYPE | TRNTYPE_CD (PK), TRNTYPE_DESC | COTRTLIC (SELECT/DELETE via cursor), COTRTUPC (SELECT/INSERT/UPDATE), COBTUPDT (INSERT/UPDATE/DELETE) |
| TRNTYCAT | TRNTYCAT_TYPE_CD + TRNTYCAT_CD (PK), TRNTYCAT_DESC | COTRTUPC (SELECT/INSERT/UPDATE) |
| AUTHFRDS (fraud records) | _(authorization key)_ | COPAUS2C (INSERT/UPDATE) |

---

## 3. End-to-End Batch Pipeline Flow

The daily batch cycle processes incoming transactions and produces reports and statements. The pipeline is orchestrated by CA-7 scheduler:

```
┌─────────────────────────────────────────────────────────────────┐
│                     DAILY BATCH CYCLE                           │
│                                                                 │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐  │
│  │ CLOSEFIL │───>│ CBPAUP0J │───>│ POSTTRAN │───>│ WAITSTEP │  │
│  │ Close    │    │ Purge    │    │ Post     │    │ Pause    │  │
│  │ CICS     │    │ Expired  │    │ Daily    │    │ between  │  │
│  │ Files    │    │ Auth     │    │ Trans    │    │ stages   │  │
│  └──────────┘    └──────────┘    └──────────┘    └──────┬───┘  │
│                                                         │      │
│  ┌──────────────────────────────────────────────────────┘      │
│  │                                                              │
│  │  ┌──────────┐                                                │
│  └─>│ OPENFIL  │  Open CICS Files (end of primary chain)       │
│     └──────────┘                                                │
│                                                                 │
│  Secondary chains (reference data reload after WAITSTEP):       │
│                                                                 │
│  CLOSEFIL1 → TRANCATG → WAITSTEP → CLOSEFIL → READACCT →     │
│              (reload     (pause)    (reclose)   READCARD →     │
│               tran                               READCUST →   │
│               categories)                        READXREF     │
│                                                                 │
│  CLOSEFIL2 → TCATBALF  → WAITSTEP → CLOSEFIL → READACCT → ...│
│              (reload                                            │
│               tran cat                                          │
│               balances)                                         │
└─────────────────────────────────────────────────────────────────┘
```

### 3.1 Primary Daily Transaction Pipeline

```
Step 1: CLOSEFIL.jcl
   └── Closes CICS VSAM files (ACCTDAT, CARDDAT, TRANSACT, etc.)
       so batch can get exclusive access.

Step 2: CBPAUP0J.jcl → CBPAUP0C (IMS BMP)
   └── Scans IMS PAUTHDB, deletes expired pending authorization
       records (PA-MATCH-STATUS = 'E').

Step 3: POSTTRAN.jcl → CBTRN02C
   ├── Input:  DALYTRAN (daily transaction feed, sequential)
   ├── Input:  XREFFILE, ACCTFILE (VSAM reads for validation)
   ├── Output: TRANFILE (posted transactions → TRANSACT VSAM)
   ├── Output: DALYREJS (rejected transactions → ESDS)
   └── Output: TCATBALF (updated category balances → VSAM rewrite)

Step 4: WAITSTEP.jcl → COBSWAIT
   └── Pause for CICS file refresh cycle.

Step 5: OPENFIL.jcl
   └── Reopens CICS VSAM files for online access.
```

### 3.2 Reporting Pipeline (On-Demand via CICS)

```
User → CORPT00C (Online — enter date range)
   └── Submits TRANREPT.jcl via Internal Reader (WRITEQ TD)

TRANREPT.jcl:
   Step 1: REPROC → Backup TRANSACT VSAM to GDG
   Step 2: SORT   → Sort transactions by account + date
   Step 3: CBTRN03C → Generate daily transaction report
       ├── Input:  TRANFILE (sorted transactions)
       ├── Input:  CARDXREF, TRANTYPE, TRANCATG (reference lookups)
       └── Output: TRANREPT (printed report file)
```

### 3.3 Statement Generation Pipeline

```
CREASTMT.JCL:
   Step 1: IDCAMS → Define work VSAM KSDS
   Step 2: SORT   → Sort TRANSACT VSAM to SEQ by card+tran-ID
   Step 3: IDCAMS → REPRO sorted SEQ into work VSAM
   Step 4: IEFBR14 → (separator/placeholder)
   Step 5: CBSTM03A → Generate account statements
       ├── CALL CBSTM03B (subroutine: open/read files)
       ├── Input:  TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE
       └── Output: STMTFILE (text), HTMLFILE (HTML)

TXT2PDF1.JCL (optional follow-up):
   Step 1: IKJEFT1B + TXT2PDF REXX → Convert STMTFILE to PDF
```

### 3.4 Export/Import Pipeline

```
CBEXPORT.jcl:
   Step 1: IDCAMS → Delete old export file
   Step 2: CBEXPORT → Read all entity VSAM files, write to EXPFILE

CBIMPORT.jcl:
   Step 1: CBIMPORT → Read EXPFILE, validate, write to individual entity output files
       ├── CUSTOUT (customer), ACCTOUT (account), XREFOUT (xref)
       ├── TRNXOUT (transactions), CARDOUT (cards)
       └── ERROUT (validation errors)
```

### 3.5 Interest Calculation Pipeline

```
INTCALC.jcl:
   Step 1: CBACT04C (PARM='2022071800' — date parameter)
       ├── Input:  TCATBALF (category balances per account)
       ├── Input:  XREFFILE (card-to-account xref)
       ├── Input:  ACCTFILE (account details, group ID)
       ├── Input:  DISCGRP  (interest rates by group+type+category)
       └── Output: TRANSACT (interest/fee transactions written)
```

### 3.6 Data File Initialization Pipeline

```
Dataset setup (run once or for environment reset):

DUSRSECJ.jcl → User security VSAM
ACCTFILE.jcl → Account VSAM
CARDFILE.jcl → Card VSAM + AIX
CUSTFILE.jcl → Customer VSAM
XREFFILE.jcl → Cross-reference VSAM + AIX + PATH
TRANFILE.jcl → Transaction VSAM + AIX
TCATBALF.jcl → Transaction category balance VSAM
TRANCATG.jcl → Transaction category reference VSAM
TRANTYPE.jcl → Transaction type reference VSAM
DISCGRP.jcl  → Disclosure group / interest rate VSAM
DALYREJS.jcl → Daily rejects ESDS
DEFGDGB.jcl  → GDG base definition
DEFGDGD.jcl  → GDG generation initialization
```

---

## 4. Copybook Dependency Matrix

Programs referencing each major copybook:

| Copybook | Used By (Programs) | Count |
|----------|-------------------|-------|
| COCOM01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC | 21 |
| COTTL01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC | 21 |
| CSDAT01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC | 21 |
| CSMSG01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC | 21 |
| DFHAID | All 21 CICS programs | 21 |
| DFHBMSCA | All 21 CICS programs | 21 |
| CSUSR01Y | COACTUPC, COACTVWC, COADM01C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC | 14 |
| CVACT01Y | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C | 12 |
| CVCRD01Y | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC | 7 |
| CVTRA05Y | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C | 11 |
| CVACT03Y | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C | 13 |
| CVCUS01Y | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COPAUA0C, COPAUS0C | 9 |
| CIPAUDTY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL | 8 |
| CIPAUSMY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL | 7 |
