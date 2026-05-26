# Dependency Map — CardDemo COBOL Estate

## 1. Program Call Graph

### 1.1 Direct CALL Dependencies

```
CBSTM03A ──CALL──► CBSTM03B    (file I/O subroutine, 12 calls)
CBACT01C ──CALL──► COBDATFT    (date formatting, external assembler)
CORPT00C ──CALL──► CSUTLDTC    (date validation utility)
COTRN02C ──CALL──► CSUTLDTC    (date validation utility)
CSUTLDTC ──CALL──► CEEDAYS     (LE date intrinsic)
COBSWAIT ──CALL──► MVSWAIT     (system wait service)

CBACT01C ──CALL──► CEE3ABD     (LE abend)
CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD
CBSTM03A ──CALL──► CEE3ABD
```

### 1.2 CICS XCTL (Transfer Control) Graph

```
COSGN00C ──XCTL──► COADM01C   (admin user login)
COSGN00C ──XCTL──► COMEN01C   (regular user login)

COADM01C ──XCTL──► COUSR00C   (User List)
COADM01C ──XCTL──► COUSR01C   (Add User)
COADM01C ──XCTL──► COUSR02C   (Update User)
COADM01C ──XCTL──► COUSR03C   (Delete User)
COADM01C ──XCTL──► COACTVWC   (Account View)
COADM01C ──XCTL──► COACTUPC   (Account Update)

COMEN01C ──XCTL──► COACTVWC   (Account View)
COMEN01C ──XCTL──► COACTUPC   (Account Update)
COMEN01C ──XCTL──► COCRDLIC   (Card List)
COMEN01C ──XCTL──► COCRDSLC   (Card Detail)
COMEN01C ──XCTL──► COCRDUPC   (Card Update)
COMEN01C ──XCTL──► COBIL00C   (Bill Payment)
COMEN01C ──XCTL──► COTRN00C   (Transaction List)
COMEN01C ──XCTL──► COTRN01C   (Transaction View)
COMEN01C ──XCTL──► COTRN02C   (Transaction Add)
COMEN01C ──XCTL──► CORPT00C   (Reports)
COMEN01C ──XCTL──► COTRTLIC   (Tran Type List - DB2)
COMEN01C ──XCTL──► COTRTUPC   (Tran Type Update - DB2)

COCRDLIC ──XCTL──► COCRDSLC   (drill down to detail)
COCRDLIC ──XCTL──► COCRDUPC   (drill down to update)
COCRDLIC ──XCTL──► COMEN01C   (return to menu)

COCRDSLC ──XCTL──► COMEN01C   (return to menu)
COCRDUPC ──XCTL──► COMEN01C   (return to menu)
COACTVWC ──XCTL──► COMEN01C   (return to menu)
COACTUPC ──XCTL──► COMEN01C   (return to menu)
COBIL00C ──XCTL──► COMEN01C   (return)
COTRN00C ──XCTL──► COMEN01C   (return)
COTRN01C ──XCTL──► COMEN01C   (return)
COTRN02C ──XCTL──► COMEN01C   (return)
CORPT00C ──XCTL──► COMEN01C   (return)
COUSR00C ──XCTL──► COADM01C   (return to admin menu)
COUSR01C ──XCTL──► COADM01C   (return)
COUSR02C ──XCTL──► COADM01C   (return)
COUSR03C ──XCTL──► COADM01C   (return)
```

### 1.3 Authorization Sub-Application Dependencies

```
COPAUA0C ──MQ GET──► Request Queue ──MQ PUT──► Reply Queue
COPAUA0C ──VSAM──► XREFFILE, ACCTFILE, CUSTFILE
COPAUS0C ──XCTL──► COPAUS1C   (drill to detail)
COPAUS0C ──XCTL──► COMEN01C   (return)
COPAUS1C ──XCTL──► COMEN01C   (return)
PAUDBLOD ──IMS CALL──► CBLTDLI (DL/I for IMS DB load)
PAUDBUNL ──IMS CALL──► CBLTDLI (DL/I for IMS DB unload)
DBUNLDGS ──IMS CALL──► CBLTDLI (DL/I for GSAM unload)
```

### 1.4 VSAM-MQ Sub-Application Dependencies

```
COACCT01 ──MQ──► Request Queue (GET account inquiry)
COACCT01 ──VSAM──► ACCTFILE (READ)
COACCT01 ──MQ──► Reply Queue (PUT response)

CODATE01 ──MQ──► Request Queue (GET date request)
CODATE01 ──MQ──► Reply Queue (PUT response)
```

---

## 2. Dataset Lineage (JCL → Files → Programs)

### 2.1 VSAM KSDS Datasets

| Dataset (DD Name) | JCL Define Job | Written By | Read By |
|-------------------|---------------|-----------|---------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS (ACCTFILE/ACCTDAT) | ACCTFILE.jcl | CBIMPORT, COTRN02C (online), COACTUPC (online), COBIL00C (online) | CBACT01C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBSTM03B, COACTVWC, COACTUPC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C, COPAUA0C, COACCT01 |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS (CARDFILE) | CARDFILE.jcl | CBIMPORT | CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COACTUPC |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS.AIX (CARDAIX) | CARDFILE.jcl | *(built by IDCAMS)* | COCRDLIC, COBIL00C, COTRN02C |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS (XREFFILE/CCXREF) | XREFFILE.jcl | CBIMPORT | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBSTM03B, COTRN02C, COPAUA0C |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS (CUSTFILE) | CUSTFILE.jcl | CBIMPORT | CBCUS01C, CBEXPORT, CBTRN01C, CBSTM03B, COCRDSLC, COCRDUPC, COACTVWC, COACTUPC, COPAUA0C |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS (TRANFILE/TRANSACT) | TRANFILE.jcl | CBTRN01C, CBTRN02C, COTRN02C (online), COBIL00C (online) | CBEXPORT, CBTRN03C, COTRN00C, COTRN01C, CORPT00C |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS.AIX | TRANIDX.jcl | *(built by IDCAMS)* | COTRN00C (browse by card) |
| AWS.M2.CARDDEMO.TCATBAL.VSAM.KSDS (TCATBALF) | TCATBALF.jcl | CBACT04C, CBTRN02C | CBACT04C |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS (DISCGRP) | DISCGRP.jcl | *(initial load)* | CBACT04C |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS (USRSEC) | DUSRSECJ.jcl | COUSR01C, COUSR02C (online) | COSGN00C, COUSR00C, COUSR02C, COUSR03C, COADM01C, COMEN01C |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS (TRANTYPE) | TRANTYPE.jcl | *(initial load)* | CBTRN03C |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS (TRANCATG) | TRANCATG.jcl | *(initial load)* | CBTRN03C |

### 2.2 Sequential / GDG Datasets

| Dataset | JCL Job | Written By | Read By |
|---------|---------|-----------|---------|
| Daily Transaction Input (DALYTRAN) | COMBTRAN.jcl (combines) | External feed | CBTRN01C, CBTRN02C |
| Daily Rejects GDG (DALYREJS) | DALYREJS.jcl (define) | CBTRN02C | *(manual review)* |
| Transaction Report (TRANREPT) | TRANREPT.jcl | CBTRN03C | *(printed output)* |
| Report File GDG | REPTFILE.jcl (define) | CBTRN03C, CBSTM03A | *(printed output)* |
| Statement File (STMTFILE) | CREASTMT.JCL | CBSTM03A | *(printed output)* |
| HTML Statement (HTMLFILE) | CREASTMT.JCL | CBSTM03A | *(web delivery)* |
| Export File (EXPFILE) | CBEXPORT.jcl | CBEXPORT | CBIMPORT |
| Error Output (ERROUT) | CBIMPORT.jcl | CBIMPORT | *(manual review)* |

### 2.3 DB2 Tables (Transaction Type Module)

| Table | JCL Create Job | Written By | Read By |
|-------|---------------|-----------|---------|
| TRAN_TYPE | CREADB21.jcl | COTRTUPC, COBTUPDT | COTRTLIC, COTRTUPC |
| TRAN_CATEGORY | CREADB21.jcl | *(loaded by JCL)* | COTRTLIC |

### 2.4 IMS Database (Authorization Module)

| Segment | JCL Load Job | Written By | Read By |
|---------|-------------|-----------|---------|
| PAUT (root — summary) | LOADPADB.JCL | PAUDBLOD, COPAUA0C | COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBUNL |
| PAUT-DTL (child — detail) | LOADPADB.JCL | PAUDBLOD | COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBUNL |

---

## 3. End-to-End Batch Pipeline Flow

```
┌─────────────────────────────────────────────────────────────────────────────────┐
│                        DAILY BATCH PROCESSING PIPELINE                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐                   │
│  │ External Feed │────►│ COMBTRAN.jcl │────►│ Daily Trans  │                   │
│  │  (raw txns)   │     │  (SORT+REPRO)│     │   File       │                   │
│  └──────────────┘     └──────────────┘     └──────┬───────┘                   │
│                                                    │                            │
│                         ┌──────────────────────────┼──────────────┐             │
│                         ▼                          ▼              ▼             │
│                  ┌─────────────┐          ┌─────────────┐  ┌──────────┐        │
│                  │ POSTTRAN.jcl│          │ TRANREPT.jcl│  │INTCALC.jcl│       │
│                  │ (CBTRN02C)  │          │ (CBTRN03C)  │  │(CBACT04C) │       │
│                  └──────┬──────┘          └──────┬──────┘  └─────┬────┘        │
│                         │                        │               │             │
│              ┌──────────┼──────────┐             ▼               ▼             │
│              ▼          ▼          ▼      ┌───────────┐   ┌───────────┐        │
│       ┌──────────┐┌──────────┐┌────────┐  │Trans Rept │   │TCATBALF   │        │
│       │Transaction││Daily Rejs││TCATBALF│  │(GDG print)│   │(updated)  │        │
│       │Master    ││(GDG)     ││(update)│  └───────────┘   └───────────┘        │
│       │(VSAM)    │└──────────┘└────────┘                                       │
│       └────┬─────┘                                                             │
│            │                                                                    │
│            ▼                                                                    │
│     ┌────────────┐     ┌──────────────┐     ┌──────────────┐                  │
│     │CREASTMT.JCL│────►│ CBSTM03A     │────►│ Statements   │                  │
│     │(SORT+pgm)  │     │ + CBSTM03B   │     │ (TXT + HTML) │                  │
│     └────────────┘     └──────────────┘     └──────────────┘                  │
│                                                                                 │
│     ┌────────────┐     ┌──────────────┐     ┌──────────────┐                  │
│     │TRANBKP.jcl │────►│ REPRO+DELETE │────►│ Backup GDG   │                  │
│     │(end of day) │     │  (IDCAMS)    │     │              │                  │
│     └────────────┘     └──────────────┘     └──────────────┘                  │
│                                                                                 │
├─────────────────────────────────────────────────────────────────────────────────┤
│                       BRANCH MIGRATION PIPELINE                                 │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐                   │
│  │ All VSAM     │────►│ CBEXPORT.jcl │────►│ Export File   │                   │
│  │ Master Files │     │ (CBEXPORT)   │     │ (EXPFILE)    │                   │
│  └──────────────┘     └──────────────┘     └──────┬───────┘                   │
│                                                    │                            │
│                                                    ▼                            │
│                                            ┌──────────────┐                    │
│                                            │ CBIMPORT.jcl │                    │
│                                            │ (CBIMPORT)   │                    │
│                                            └──────┬───────┘                    │
│                                                   │                            │
│                              ┌────────┬───────┬───┴───┬────────┐               │
│                              ▼        ▼       ▼       ▼        ▼               │
│                         CUSTOUT  ACCTOUT XREFOUT TRNXOUT  CARDOUT              │
│                         (+ ERROUT for validation failures)                     │
│                                                                                 │
├─────────────────────────────────────────────────────────────────────────────────┤
│                       AUTHORIZATION PIPELINE (IMS/MQ)                           │
├─────────────────────────────────────────────────────────────────────────────────┤
│                                                                                 │
│  ┌──────────────┐     ┌──────────────┐     ┌──────────────┐                   │
│  │ MQ Request Q │────►│ COPAUA0C     │────►│ MQ Reply Q   │                   │
│  │(auth request)│     │(auth decision)│     │(auth resp)   │                   │
│  └──────────────┘     └──────────────┘     └──────────────┘                   │
│         │                     │                                                 │
│         │              ┌──────┴──────┐                                         │
│         │              ▼             ▼                                          │
│         │        ┌──────────┐  ┌──────────┐                                   │
│         │        │IMS PAUT  │  │VSAM Files│                                   │
│         │        │(log auth)│  │(lookup)  │                                   │
│         │        └──────────┘  └──────────┘                                   │
│         │                                                                       │
│  ┌──────┴───────┐     ┌──────────────┐                                        │
│  │ CBPAUP0J.jcl │────►│ CBPAUP0C     │  (purge expired auths)                 │
│  └──────────────┘     └──────────────┘                                        │
│                                                                                 │
└─────────────────────────────────────────────────────────────────────────────────┘
```

### 3.1 Batch Execution Sequence (Typical Daily Run)

1. **CLOSEFIL.jcl** — Close CICS-managed files for exclusive batch access
2. **COMBTRAN.jcl** — Sort and combine incoming daily transactions
3. **POSTTRAN.jcl** — Post daily transactions to master file (CBTRN02C)
4. **INTCALC.jcl** — Calculate interest charges (CBACT04C)
5. **TRANREPT.jcl** — Generate daily transaction report (CBTRN03C)
6. **CREASTMT.JCL** — Generate account statements (CBSTM03A + CBSTM03B)
7. **TRANBKP.jcl** — Backup transaction master
8. **OPENFIL.jcl** — Reopen files for CICS online access

### 3.2 One-Time Setup Sequence (Initial Install)

1. **DEFGDGB.jcl** — Define GDG bases
2. **ACCTFILE.jcl** — Define Account VSAM
3. **CARDFILE.jcl** — Define Card VSAM + AIX
4. **CUSTFILE.jcl** — Define Customer VSAM
5. **XREFFILE.jcl** — Define Cross-Reference VSAM + AIX
6. **TRANFILE.jcl** — Define Transaction Master VSAM + AIX
7. **TRANIDX.jcl** — Define Transaction AIX
8. **TCATBALF.jcl** — Define Transaction Category Balance
9. **DISCGRP.jcl** — Define Disclosure Group
10. **TRANTYPE.jcl** — Define Transaction Type
11. **TRANCATG.jcl** — Define Transaction Category
12. **DUSRSECJ.jcl** — Define User Security + load initial data
13. **DALYREJS.jcl** — Define Daily Rejects GDG
14. **REPTFILE.jcl** — Define Report File GDG
15. **CBADMCDJ.jcl** — Install CICS CSD program definitions
16. **OPENFIL.jcl** — Open all files in CICS

---

## 4. Copybook Usage Matrix

| Copybook | Used By (count) | Programs |
|----------|----------------|----------|
| COCOM01Y | 17 | All CICS online programs |
| COTTL01Y | 17 | All CICS online programs |
| CSDAT01Y | 17 | All CICS online programs |
| CSMSG01Y | 17 | All CICS online programs |
| DFHAID | 17 | All CICS online programs |
| DFHBMSCA | 17 | All CICS online programs |
| CVCRD01Y | 9 | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC + 2 |
| CSUSR01Y | 11 | COSGN00C, COADM01C, COMEN01C, COUSRxxC (4), COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC |
| CVACT01Y | 12 | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COTRN02C, COBIL00C, COACTVWC, COACTUPC, COPAUA0C |
| CVACT03Y | 11 | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COTRN02C, COBIL00C, COPAUA0C |
| CVACT02Y | 8 | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC |
| CVCUS01Y | 8 | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COCRDSLC, COCRDUPC, COACTUPC, COPAUA0C |
| CVTRA05Y | 8 | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C |
| CSMSG02Y | 7 | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUS0C, COPAUS1C, COTRTUPC |
| CIPAUSMY | 6 | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
| CIPAUDTY | 7 | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
| CSSETATY | 2 | COACTUPC (25 uses), COTRTUPC |
| CSUTLDWY | 2 | COACTUPC, COTRTUPC |
| CSUTLDPY | 1 | COACTUPC |
| CSLKPCDY | 1 | COACTUPC |
| CVEXPORT | 2 | CBEXPORT, CBIMPORT |
