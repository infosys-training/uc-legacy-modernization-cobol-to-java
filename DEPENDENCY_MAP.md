# CardDemo Dependency Map

> Call graph, dataset lineage, and end-to-end pipeline flow for the CardDemo estate.

---

## 1. Online (CICS) Program Call Graph

### 1.1 Navigation Tree (XCTL Chains)

```
COSGN00C (Sign-On)
├──→ [XCTL] COADM01C (Admin Menu)        [if user type = 'A']
│    ├──→ [XCTL] COUSR00C (User List)
│    ├──→ [XCTL] COUSR01C (User Add)
│    ├──→ [XCTL] COUSR02C (User Update)
│    ├──→ [XCTL] COUSR03C (User Delete)
│    ├──→ [XCTL] COTRTLIC (Tran Type List — DB2)
│    └──→ [XCTL] COTRTUPC (Tran Type Update — DB2)
│
└──→ [XCTL] COMEN01C (Main Menu)          [if user type = 'U']
     ├──→ [XCTL] COACTVWC (Account View)
     ├──→ [XCTL] COACTUPC (Account Update)
     ├──→ [XCTL] COCRDLIC (Card List)
     ├──→ [XCTL] COCRDSLC (Card View)
     ├──→ [XCTL] COCRDUPC (Card Update)
     ├──→ [XCTL] COTRN00C (Transaction List)
     ├──→ [XCTL] COTRN01C (Transaction View)
     ├──→ [XCTL] COTRN02C (Transaction Add)
     ├──→ [XCTL] CORPT00C (Report Request)
     ├──→ [XCTL] COBIL00C (Bill Payment)
     └──→ [XCTL] COPAUS0C (Pending Auth View — IMS)
          └──→ [LINK] COPAUS1C (Auth Detail)
               └──→ [LINK] COPAUS2C (Fraud Recording — DB2)
```

### 1.2 Inter-Program Call Matrix (Online)

| Calling Program | Called Program | Call Type | Purpose |
|----------------|---------------|-----------|---------|
| COSGN00C | COADM01C | XCTL | Route admin to admin menu |
| COSGN00C | COMEN01C | XCTL | Route user to main menu |
| COADM01C | COUSR00C/01C/02C/03C | XCTL | User management screens |
| COADM01C | COTRTLIC | XCTL | Transaction type list |
| COADM01C | COTRTUPC | XCTL | Transaction type update |
| COMEN01C | COACTVWC/COACTUPC | XCTL | Account screens |
| COMEN01C | COCRDLIC/COCRDSLC/COCRDUPC | XCTL | Card screens |
| COMEN01C | COTRN00C/COTRN01C/COTRN02C | XCTL | Transaction screens |
| COMEN01C | CORPT00C | XCTL | Report request |
| COMEN01C | COBIL00C | XCTL | Bill payment |
| COMEN01C | COPAUS0C | XCTL | Pending authorization |
| COPAUS0C | COPAUS1C | LINK | Auth detail drill-down |
| COPAUS1C | COPAUS2C | LINK | Fraud recording |
| CORPT00C | INTRDRJ1/J2 | WRITEQ TD (INTRDR) | Submit JCL batch jobs |
| All user screens | COMEN01C | XCTL (PF3) | Return to menu |
| All admin screens | COADM01C | XCTL (PF3) | Return to admin menu |

### 1.3 MQ-Based Calls

```
External MQ Client
  │
  ├──→ [MQ Request Queue] → COPAUA0C (Auth Decision Engine)
  │                           ├── MQGET (request)
  │                           ├── IMS DL/I: GU/REPL/ISRT (auth DB)
  │                           ├── CICS READ (XREF, ACCT, CUST)
  │                           └── MQPUT1 (response)
  │
  ├──→ [MQ Request Queue] → COACCT01 (Account Inquiry)
  │                           ├── MQGET (request)
  │                           ├── CICS READ (ACCTFILE)
  │                           └── MQPUT (response)
  │
  └──→ [MQ Request Queue] → CODATE01 (Date Service)
                              ├── MQGET (request)
                              └── MQPUT (response)
```

---

## 2. Batch Program Call Graph

### 2.1 Inter-Program Calls (Batch)

| Calling Program | Called Program | Call Type | Purpose |
|----------------|---------------|-----------|---------|
| CBSTM03A | CBSTM03B | CALL (×12) | File I/O submodule (read/write records) |

All other batch programs are standalone — they read/write files but do not CALL other COBOL programs. The batch pipeline is orchestrated by JCL job sequencing.

### 2.2 IMS Batch Programs

| Program | DL/I Calls | Purpose |
|---------|-----------|---------|
| CBPAUP0C | GN, GNP, DLET, CHKP | Purge expired auths |
| PAUDBLOD | ISRT, GU (via CBLTDLI) | Load auth data from flat files |
| PAUDBUNL | GN, GNP (via CBLTDLI) | Unload auth data to flat files |
| DBUNLDGS | GN, GNP, ISRT (via CBLTDLI, GSAM) | GSAM unload utility |

---

## 3. Dataset Lineage

### 3.1 VSAM Dataset Map

| Dataset (Logical Name) | VSAM Cluster | Record Layout | Key | Written By | Read By |
|------------------------|-------------|---------------|-----|-----------|---------|
| ACCTFILE | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | CVACT01Y (300 bytes) | ACCT-ID (11,0) | ACCTFILE.jcl (REPRO), CBACT04C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE), CBIMPORT | CBACT01C, CBACT04C, COACTUPC, COACTVWC, COCRDSLC, COBIL00C, COPAUA0C, COACCT01, CBSTM03A, CBEXPORT |
| CARDFILE | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CVACT02Y (150 bytes) | CARD-NUM (16,0) | CARDFILE.jcl (REPRO), COCRDUPC (REWRITE), CBIMPORT | CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COACTUPC, COPAUS0C, CBEXPORT |
| CUSTFILE | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CVCUS01Y (500 bytes) | CUST-ID (9,0) | CUSTFILE.jcl (REPRO), COACTUPC (REWRITE), CBIMPORT | CBCUS01C, COACTUPC, COACTVWC, COPAUA0C, CBSTM03A, CBEXPORT |
| CARDXREF | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | CVACT03Y (50 bytes) | XREF-CARD-NUM (16,0) | XREFFILE.jcl (REPRO), CBIMPORT | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, COTRN02C, COBIL00C, COPAUA0C, CBSTM03A, CBEXPORT |
| TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | CVTRA05Y (350 bytes) | TRAN-ID (16,0) | TRANFILE.jcl (REPRO), CBTRN02C (WRITE), COTRN02C (WRITE), CBIMPORT | CBTRN03C, COTRN00C, COTRN01C, COBIL00C, CBEXPORT |
| DALYTRAN | AWS.M2.CARDDEMO.DALYTRAN.PS | CVTRA06Y (350 bytes) | N/A (sequential) | External (loaded by COMBTRAN) | CBTRN01C, CBTRN02C |
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | CVTRA01Y (50 bytes) | ACCT+TYPE+CAT (17,0) | TCATBALF.jcl (REPRO), CBTRN02C (REWRITE/WRITE) | CBACT04C, CBTRN02C |
| DISCGRP | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | CVTRA02Y (50 bytes) | GROUP+TYPE+CAT (16,0) | DISCGRP.jcl (REPRO) | CBACT04C |
| TRANTYPE | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | CVTRA03Y (60 bytes) | TRAN-TYPE (2,0) | TRANTYPE.jcl (REPRO) | CBTRN03C |
| TRANCATG | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | CVTRA04Y (60 bytes) | TYPE+CAT (6,0) | TRANCATG.jcl (REPRO) | CBTRN03C |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | CSUSR01Y (80 bytes) | SEC-USR-ID (8,0) | DUSRSECJ.jcl | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| EXPORT-FILE | AWS.M2.CARDDEMO.EXPORT.PS | CVEXPORT (500 bytes) | N/A (sequential) | CBEXPORT | CBIMPORT |

### 3.2 DB2 Table Access

| Table | Programs That Read | Programs That Write | JCL That Manages |
|-------|-------------------|--------------------|--------------------|
| TRTYP (Transaction Types) | COTRTLIC (CURSOR), COTRTUPC (SELECT) | COTRTUPC (INSERT/UPDATE/DELETE), COBTUPDT (DELETE/INSERT) | CREADB21.jcl, MNTTRDB2.jcl, TRANEXTR.jcl |
| TRCAT (Transaction Categories) | COTRTLIC (join), COTRTUPC (SELECT) | COTRTUPC (INSERT/DELETE cascade) | CREADB21.jcl |
| AUTHFRDS (Fraud Records) | COPAUS2C (SELECT) | COPAUS2C (INSERT) | (created by CREADB21.jcl) |

### 3.3 IMS Database Access

| Database | Segment | Programs That Read | Programs That Write |
|----------|---------|-------------------|---------------------|
| PAUTHDBS | CIPAUSMY (Summary) | COPAUA0C (GU), COPAUS0C (GU/GNP), COPAUS1C (GU), CBPAUP0C (GN), PAUDBUNL (GN), DBUNLDGS (GN) | COPAUA0C (REPL/ISRT), PAUDBLOD (ISRT) |
| PAUTHDBD | CIPAUDTY (Detail) | COPAUA0C (GU), COPAUS0C (GNP), COPAUS1C (GNP), CBPAUP0C (GNP), PAUDBUNL (GNP), DBUNLDGS (GNP) | COPAUA0C (ISRT), COPAUS1C (REPL), CBPAUP0C (DLET), PAUDBLOD (ISRT) |

### 3.4 MQ Queue Access

| Queue | Direction | Program | Purpose |
|-------|-----------|---------|---------|
| PAUTH.REQUEST | GET | COPAUA0C | Receive authorization requests |
| PAUTH.RESPONSE | PUT | COPAUA0C | Send authorization responses |
| PAUTH.ERROR | PUT | COPAUA0C | Log authorization errors |
| ACCTINQ.REQUEST | GET | COACCT01 | Receive account inquiry requests |
| ACCTINQ.RESPONSE | PUT | COACCT01 | Send account inquiry responses |
| ACCTINQ.ERROR | PUT | COACCT01 | Log errors |
| DATEINQ.REQUEST | GET | CODATE01 | Receive date format requests |
| DATEINQ.RESPONSE | PUT | CODATE01 | Send formatted dates |
| DATEINQ.ERROR | PUT | CODATE01 | Log errors |

---

## 4. End-to-End Batch Pipeline Flow

### 4.1 Daily Transaction Processing Pipeline

```
┌─────────────────────────────────────────────────────────────────────────┐
│  DAILY BATCH PIPELINE (sequential — each job depends on prior job)     │
└─────────────────────────────────────────────────────────────────────────┘

  Step 1: COMBTRAN.jcl
  ┌──────────────────────┐
  │ Combine/Sort daily   │    Input:  Multiple daily transaction files
  │ transaction files    │──→ Output: AWS.M2.CARDDEMO.DALYTRAN.PS
  │ (SORT + IDCAMS)      │            (consolidated, sorted by card#)
  └──────────┬───────────┘
             │
  Step 2: POSTTRAN.jcl (CBTRN02C)
  ┌──────────▼───────────┐
  │ Post daily trans to  │    Input:  DALYTRAN, XREF, ACCOUNT, TCATBAL
  │ master file          │──→ Output: TRANSACT (new records)
  │ Validate + write     │           ACCOUNT (updated balances)
  │                      │           TCATBAL (updated category bals)
  │                      │           DALYREJS (rejected transactions)
  └──────────┬───────────┘
             │
  Step 3: INTCALC.jcl (CBACT04C)
  ┌──────────▼───────────┐
  │ Calculate interest   │    Input:  TCATBAL, XREF, DISCGRP, ACCOUNT
  │ on account balances  │──→ Output: TRANSACT (interest trans)
  │ Apply disclosure     │           ACCOUNT (updated with interest)
  │ group rates          │
  └──────────┬───────────┘
             │
  Step 4: CREASTMT.JCL (CBSTM03A → CBSTM03B)
  ┌──────────▼───────────┐
  │ Generate customer    │    Input:  TRNX (sorted trans), XREF,
  │ statements           │           CUST, ACCT
  │ Text + HTML format   │──→ Output: STMT-FILE (text statements)
  │                      │           HTML-FILE (HTML statements)
  └──────────┬───────────┘
             │
  Step 5: TRANREPT.jcl (CBTRN03C)
  ┌──────────▼───────────┐
  │ Generate daily       │    Input:  TRANSACT, XREF, TRANTYPE, TRANCATG
  │ transaction report   │──→ Output: REPORT-FILE (formatted report)
  │ With type/category   │           (sorted by account, with subtotals)
  │ lookups              │
  └──────────────────────┘
```

### 4.2 Data Setup Pipeline (One-Time / Refresh)

```
  ACCTFILE.jcl ──→ Define + Load Account VSAM
  CARDFILE.jcl ──→ Define + Load Card VSAM (+ AIX on ACCT-ID)
  CUSTFILE.jcl ──→ Define + Load Customer VSAM
  XREFFILE.jcl ──→ Define + Load Cross-Reference VSAM (+ AIX)
  TRANFILE.jcl ──→ Define + Load Transaction VSAM (+ AIX)
  DISCGRP.jcl  ──→ Define + Load Disclosure Group VSAM
  TCATBALF.jcl ──→ Define + Load Category Balance VSAM
  TRANTYPE.jcl ──→ Define + Load Transaction Type VSAM
  TRANCATG.jcl ──→ Define + Load Transaction Category VSAM
  DUSRSECJ.jcl ──→ Define + Load User Security VSAM
  CREADB21.jcl ──→ Create DB2 tables (TRTYP, TRCAT) + load data
  CBADMCDJ.jcl ──→ Define CICS CSD resources (programs, files, TDQs)
```

### 4.3 IMS Authorization Pipeline

```
  LOADPADB.JCL (PAUDBLOD)  ──→ Load IMS auth database from flat files
  CBPAUP0J.jcl (CBPAUP0C)  ──→ Purge expired auth records
  UNLDPADB.JCL (PAUDBUNL)  ──→ Unload IMS database to flat files
  UNLDGSAM.JCL (DBUNLDGS)  ──→ GSAM unload utility
  DBPAUTP0.jcl              ──→ Alternate DB unload path
```

### 4.4 Data Migration Pipeline

```
  CBEXPORT.jcl (CBEXPORT) ──→ Export all VSAM files to CVEXPORT format
  CBIMPORT.jcl (CBIMPORT) ──→ Import from CVEXPORT back to VSAM files
```

---

## 5. Shared Resource Dependencies

### 5.1 Copybook Usage Matrix (Top 15 Most Referenced)

| Copybook | Used By (Count) | Purpose |
|----------|---------|---------|
| COCOM01Y | 20 programs | CICS communication area — shared session state |
| DFHAID | 14 programs | CICS AID byte constants (PF keys) |
| DFHBMSCA | 14 programs | CICS BMS attribute constants |
| COTTL01Y | 14 programs | Screen title constants |
| CSDAT01Y | 14 programs | Date/time working storage |
| CSMSG01Y | 14 programs | Common error messages |
| CSUSR01Y | 10 programs | User security record layout |
| CVACT01Y | 11 programs | Account record layout |
| CVACT03Y | 12 programs | Card cross-reference layout |
| CVCUS01Y | 7 programs | Customer record layout |
| CVACT02Y | 7 programs | Card record layout |
| CVTRA05Y | 8 programs | Transaction record layout |
| CVCRD01Y | 8 programs | Card work areas |
| CSSTRPFY | 8 programs | String padding utility |
| CSSETATY | 2 programs (but ×39 in COACTUPC) | Screen attribute setter macro |

### 5.2 VSAM File Contention Map

Files accessed by multiple programs that may require concurrent access coordination:

| File | Online Writers | Batch Writers | Contention Risk |
|------|---------------|---------------|----------------|
| ACCTFILE | COACTUPC, COBIL00C | CBACT04C, CBTRN02C, CBIMPORT | **HIGH** — written by 2 online + 3 batch programs |
| CARDFILE | COCRDUPC | CBIMPORT | LOW |
| CUSTFILE | COACTUPC | CBIMPORT | MEDIUM |
| TRANSACT | COTRN02C, COBIL00C | CBTRN02C, CBACT04C, CBIMPORT | **HIGH** — written by 2 online + 3 batch programs |
| USRSEC | COUSR01C, COUSR02C, COUSR03C | — | LOW (admin only) |
| TCATBALF | — | CBTRN02C | LOW |
