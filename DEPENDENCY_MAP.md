# DEPENDENCY MAP — CardDemo Call Graph, Dataset Lineage & Batch Pipeline

## Overview

This document maps three dimensions of dependencies across the CardDemo COBOL estate:
1. **Program-to-Program Call Graph** — CALL, XCTL (transfer control), and LINK relationships
2. **Dataset Lineage** — which JCL jobs read/write which files, and which programs process them
3. **End-to-End Batch Pipeline** — the complete daily processing flow

---

## 1. Program-to-Program Call Graph

### 1.1 Online Navigation Tree (CICS XCTL)

CICS online programs transfer control using `EXEC CICS XCTL`, passing data through COMMAREA (COCOM01Y).

```
                            ┌──────────┐
                            │ COSGN00C │  (Sign-on Entry Point)
                            │ TranID:  │
                            │ CCDA     │
                            └────┬─────┘
                                 │ XCTL (based on user type)
                    ┌────────────┴────────────┐
                    ▼                          ▼
              ┌──────────┐              ┌──────────┐
              │ COADM01C │              │ COMEN01C │
              │(Admin Hub│              │(Main Menu│
              │ 6 routes)│              │ 11 routes│
              └────┬─────┘              └────┬─────┘
                   │                         │
    ┌──────┬───────┼──────┬──────┐           │
    ▼      ▼       ▼      ▼      ▼           │
COUSR00C COUSR01C COUSR02C COUSR03C          │
(UserList)(UserAdd)(UserUpd)(UserDel)         │
                   │      │                   │
              COTRTLIC  COTRTUPC              │
              (TranType  (TranType             │
               List/DB2)  Upd/DB2)             │
                                              │
    ┌─────────┬──────────┬──────────┬─────────┤
    │         │          │          │         │
    ▼         ▼          ▼          ▼         │
COACTVWC  COACTUPC  COCRDLIC  COCRDSLC       │
(AcctView)(AcctUpd) (CardList) (CardView)     │
                                              │
    ┌─────────┬──────────┬──────────┬─────────┤
    │         │          │          │         │
    ▼         ▼          ▼          ▼         ▼
COCRDUPC  COTRN00C  COTRN01C  COTRN02C  CORPT00C
(CardUpd) (TranList) (TranView)(TranAdd) (Report)
                                              │
    ┌─────────┬───────────────────────────────┤
    ▼         ▼                               ▼
COBIL00C  COPAUS0C ──LINK──► COPAUS1C ──LINK──► COPAUS2C
(BillPay) (PendAuth           (AuthDetail)      (FraudUpd)
           Summary)                              [DB2]
```

#### COMEN01C Navigation Routes (11 destinations)

| Option | Target Program | Description |
|--------|---------------|-------------|
| 1 | COACTVWC | Account View |
| 2 | COACTUPC | Account Update |
| 3 | COCRDLIC | Credit Card List |
| 4 | COCRDSLC | Credit Card View |
| 5 | COCRDUPC | Credit Card Update |
| 6 | COTRN00C | Transaction List |
| 7 | COTRN01C | Transaction View |
| 8 | COTRN02C | Transaction Add |
| 9 | CORPT00C | Transaction Reports |
| 10 | COBIL00C | Bill Payment |
| 11 | COPAUS0C | Pending Authorization View |

#### COADM01C Admin Routes (6 destinations)

| Option | Target Program | Description |
|--------|---------------|-------------|
| 1 | COUSR00C | User List |
| 2 | COUSR01C | User Add |
| 3 | COUSR02C | User Update |
| 4 | COUSR03C | User Delete |
| 5 | COTRTLIC | Transaction Type List (DB2) |
| 6 | COTRTUPC | Transaction Type Maintenance (DB2) |

### 1.2 CALL / LINK Dependencies

Direct `CALL` and `EXEC CICS LINK` statements create tighter coupling than XCTL.

| Caller | Target | Mechanism | Purpose |
|--------|--------|-----------|---------|
| CBSTM03A | CBSTM03B | CALL (×13) | I/O submodule for statement generation |
| CBACT01C | COBDATFT | CALL | Assembler date formatting |
| CBACT01C | CEE3ABD | CALL | LE abnormal termination |
| CBACT04C | CEE3ABD | CALL | LE abnormal termination |
| CBCUS01C | CEE3ABD | CALL | LE abnormal termination |
| CBTRN01C | CEE3ABD | CALL | LE abnormal termination |
| CBTRN02C | CEE3ABD | CALL | LE abnormal termination |
| CBTRN03C | CEE3ABD | CALL | LE abnormal termination |
| COBSWAIT | MVSWAIT | CALL | Assembler wait/delay routine |
| CSUTLDTC | CEEDAYS | CALL | LE date conversion intrinsic |
| COPAUS0C | COPAUS1C | EXEC CICS LINK | Navigate to authorization detail |
| COPAUS1C | COPAUS2C | EXEC CICS LINK | Navigate to fraud update |
| CORPT00C | (JCL via INTRDR) | CICS WRITEQ TD | Submits batch jobs via internal reader |

### 1.3 IMS DL/I Call Graph

IMS programs interact with the IMS hierarchical database through `CALL 'CBLTDLI'`.

| Program | DL/I Functions Used | IMS Segments Accessed |
|---------|-------------------|-----------------------|
| COPAUA0C | GU, GNP, ISRT | CIPAUSMY (summary), CIPAUDTY (detail) |
| COPAUS0C | GU, GNP | CIPAUSMY, CIPAUDTY |
| COPAUS1C | GU, GNP, REPL | CIPAUSMY, CIPAUDTY |
| COPAUS2C | (via DB2 SQL) | CIPAUDTY |
| CBPAUP0C | GN, GNP, DLET | CIPAUSMY, CIPAUDTY |
| PAUDBLOD | GU, ISRT | CIPAUSMY, CIPAUDTY |
| PAUDBUNL | GN, GNP | CIPAUSMY, CIPAUDTY |
| DBUNLDGS | GN, GNP | CIPAUSMY, CIPAUDTY (via GSAM) |

### 1.4 MQ Message Flow

Programs that interact with IBM MQ through `CALL 'MQOPEN'`, `MQGET`, `MQPUT1`, `MQCLOSE`:

```
External Auth     MQPUT        ┌──────────┐      MQGET       ┌──────────┐
System ──────────────────────► │ MQ Queue │ ──────────────► │ COPAUA0C │
                               │(Auth Req) │                 │(Decision)│
                               └──────────┘                 └────┬─────┘
                                                                 │
                               ┌──────────┐     MQPUT1          │
                               │ MQ Queue │ ◄────────────────────┘
                               │(Auth Resp)│
                               └──────────┘

VSAM Account      MQPUT        ┌──────────┐      MQGET       ┌──────────┐
Inquiry  ──────────────────► │ MQ Queue │ ──────────────► │ COACCT01 │
                               │(Acct Req) │                 │(Acct Inq)│
                               └──────────┘                 └────┬─────┘
                                                                 │
                               ┌──────────┐     MQPUT           │
                               │ MQ Queue │ ◄────────────────────┘
                               │(Acct Resp)│
                               └──────────┘

Date/Time         MQPUT        ┌──────────┐      MQGET       ┌──────────┐
Request  ──────────────────► │ MQ Queue │ ──────────────► │ CODATE01 │
                               │(Date Req) │                 │(Date Svc)│
                               └──────────┘                 └────┬─────┘
                                                                 │
                               ┌──────────┐     MQPUT           │
                               │ MQ Queue │ ◄────────────────────┘
                               │(Date Resp)│
                               └──────────┘
```

### 1.5 DB2 SQL Dependencies

| Program | DB2 Tables | Operations |
|---------|-----------|------------|
| COTRTLIC | DCLTRTYP (Transaction Type) | DECLARE CURSOR, OPEN, FETCH, CLOSE |
| COTRTUPC | DCLTRTYP, DCLTRCAT (Transaction Category) | SELECT, INSERT, UPDATE, DELETE |
| COBTUPDT | DCLTRTYP | DECLARE CURSOR, OPEN, FETCH, UPDATE, CLOSE |
| COPAUS2C | (Fraud update table) | INSERT, SELECT, UPDATE, DECLARE CURSOR |

---

## 2. Dataset Lineage

### 2.1 VSAM File Inventory

| VSAM Dataset | DD Name | Key | RECLN | Entity | Defining JCL | Programs That Read | Programs That Write |
|-------------|---------|-----|-------|--------|-------------|-------------------|-------------------|
| CARDDEMO.ACCTDATA.VSAM.KSDS | ACCTFILE | ACCT-ID (11 bytes) | 300 | Account | ACCTFILE.jcl | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, COACCT01, CBSTM03B, CBEXPORT | CBACT04C (REWRITE), CBTRN02C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE) |
| CARDDEMO.CARDDATA.VSAM.KSDS | CARDFILE | CARD-NUM (16 bytes) | 150 | Card | CARDFILE.jcl | CBACT02C, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, CBEXPORT | COCRDUPC (REWRITE) |
| CARDDEMO.CUSTDATA.VSAM.KSDS | CUSTFILE | CUST-ID (9 bytes) | 500 | Customer | CUSTFILE.jcl | CBCUS01C, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, CBSTM03B, CBEXPORT | COACTUPC (REWRITE) |
| CARDDEMO.CARDXREF.VSAM.KSDS | XREFFILE | XREF-CARD-NUM (16 bytes) | 50 | Card-XREF | XREFFILE.jcl | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, CBSTM03B, CBEXPORT | — |
| CARDDEMO.TRANSACT.VSAM.KSDS | TRANSACT / TRANFILE | TRAN-ID (16 bytes) | 350 | Transaction | TRANFILE.jcl | CBTRN03C, COTRN00C, COTRN01C, CBSTM03B, CBEXPORT | CBACT04C, CBTRN02C, COBIL00C, COTRN02C |
| CARDDEMO.USRSEC.VSAM.KSDS | USRSEC | SEC-USR-ID (8 bytes) | 80 | User | DUSRSECJ.jcl | COSGN00C, COUSR00C–03C | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) |
| CARDDEMO.DISCGRP.VSAM.KSDS | DISCGRP | DIS-GROUP-KEY (16 bytes) | 50 | Disclosure | DISCGRP.jcl | CBACT04C | — |
| CARDDEMO.TCATBALF.VSAM.KSDS | TCATBALF | TRAN-CAT-KEY (17 bytes) | 50 | TranCatBal | TCATBALF.jcl | CBACT04C, CBTRN02C | CBTRN02C (WRITE/REWRITE) |
| CARDDEMO.TRANTYPE.VSAM.KSDS | TRANTYPE | TRAN-TYPE (2 bytes) | 60 | TranType | TRANTYPE.jcl | CBTRN03C | — |
| CARDDEMO.TRANCATG.VSAM.KSDS | TRANCATG | TRAN-CAT-KEY (6 bytes) | 60 | TranCat | TRANCATG.jcl | CBTRN03C | — |

### 2.2 Non-VSAM (PS/Sequential) Datasets

| Dataset | DD Name | Purpose | Created By | Consumed By |
|---------|---------|---------|-----------|-------------|
| CARDDEMO.DALYTRAN | DALYTRAN | Daily transaction input file | External (card network) | CBTRN01C, CBTRN02C (POSTTRAN.jcl) |
| CARDDEMO.DALYREJS | DALYREJS | Rejected daily transactions | CBTRN02C | Operations review |
| CARDDEMO.TRANREPT | TRANREPT | Daily transaction report (printable) | CBTRN03C (TRANREPT.jcl) | Operations / Management |
| CARDDEMO.STMTFILE | STMTFILE | Customer statements (text) | CBSTM03A (CREASTMT.JCL) | Print / Mail |
| CARDDEMO.HTMLFILE | HTMLFILE | Customer statements (HTML) | CBSTM03A (CREASTMT.JCL) | Web portal |
| CARDDEMO.DATEPARM | DATEPARM | Date parameter for report | Control card | CBTRN03C |
| CARDDEMO.EXPFILE | EXPFILE | Multi-record export file | CBEXPORT | CBIMPORT |
| CARDDEMO.ACCTDATA.PS | — | Account flat file (PS format) | Data setup | ACCTFILE.jcl (IDCAMS REPRO) |
| CARDDEMO.CARDDATA.PS | — | Card flat file | Data setup | CARDFILE.jcl |
| CARDDEMO.CUSTDATA.PS | — | Customer flat file | Data setup | CUSTFILE.jcl |
| CARDDEMO.CARDXREF.PS | — | Cross-reference flat file | Data setup | XREFFILE.jcl |
| CARDDEMO.PAUTDB.ROOT.FILEO | OUTFIL1 | IMS root segment (summary) extract | UNLDPADB.JCL / PAUDBUNL | LOADPADB.JCL / PAUDBLOD |
| CARDDEMO.PAUTDB.CHILD.FILEO | OUTFIL2 | IMS child segment (detail) extract | UNLDPADB.JCL / PAUDBUNL | LOADPADB.JCL / PAUDBLOD |

### 2.3 JCL Job → Dataset → Program Lineage

```
                                    ┌──────────────────────────┐
                                    │  VSAM CLUSTER DEFINITION │
                                    │  (ACCTFILE, CARDFILE,    │
                                    │   CUSTFILE, XREFFILE,    │
                                    │   TRANFILE, etc. JCL)    │
                                    └────────────┬─────────────┘
                                                 │ IDCAMS REPRO from PS files
                                                 ▼
┌─────────────┐   reads    ┌────────────────┐   reads    ┌─────────────┐
│ DALYTRAN    │ ──────────►│  POSTTRAN.jcl  │ ──────────►│ ACCTFILE    │
│(Daily Input)│            │  (CBTRN02C)    │            │ CARDXREF    │
└─────────────┘            └───────┬────────┘            │ TCATBALF    │
                                   │ writes               └─────────────┘
                    ┌──────────────┼──────────────┐
                    ▼              ▼               ▼
             ┌───────────┐ ┌──────────┐    ┌──────────────┐
             │ TRANFILE  │ │ DALYREJS │    │ TCATBALF     │
             │ (master)  │ │ (rejects)│    │ (cat balance)│
             └─────┬─────┘ └──────────┘    └──────┬───────┘
                   │                               │
                   ▼                               ▼
            ┌──────────────┐               ┌──────────────┐
            │ TRANREPT.jcl │               │  INTCALC.jcl │
            │ (CBTRN03C)   │               │  (CBACT04C)  │
            └──────┬───────┘               └──────┬───────┘
                   │ writes                        │ writes
                   ▼                               ▼
            ┌──────────────┐               ┌──────────────┐
            │ TRANREPT     │               │ TRANSACT     │
            │(daily report)│               │ (new interest│
            └──────────────┘               │  transactions│
                                           └──────┬───────┘
                   ┌───────────────────────────────┘
                   ▼
            ┌──────────────┐
            │ CREASTMT.JCL │
            │ (SORT+       │
            │  CBSTM03A)   │
            └──────┬───────┘
                   │ writes
            ┌──────┴──────┐
            ▼             ▼
     ┌──────────┐  ┌──────────┐
     │ STMTFILE │  │ HTMLFILE │
     │(text stmt│  │(HTML stmt│
     └──────────┘  └──────────┘
```

---

## 3. End-to-End Batch Pipeline

### 3.1 Daily Processing Flow

The daily batch pipeline runs in this order, with each step dependent on the prior step's output:

```
 ══════════════════════════════════════════════════════════════════
 STEP 1: POSTTRAN                        (Post Daily Transactions)
 ══════════════════════════════════════════════════════════════════
   JCL:     POSTTRAN.jcl
   Program: CBTRN02C (731 LOC)
   Input:   DALYTRAN (daily feed from card network)
            XREFFILE (card → account cross-reference)
            ACCTFILE (account master)
            TCATBALF (category balances)
   Output:  TRANFILE (posted transactions → master file)
            DALYREJS (rejected transactions)
            ACCTFILE (updated balances via REWRITE)
            TCATBALF (updated category balances via WRITE/REWRITE)
   Logic:   - Validate each daily transaction against XREF
            - Update account current balance
            - Update transaction category running balance
            - Write rejects for invalid card/account combos

 ══════════════════════════════════════════════════════════════════
 STEP 2: INTCALC                         (Interest Calculation)
 ══════════════════════════════════════════════════════════════════
   JCL:     INTCALC.jcl
   Program: CBACT04C (652 LOC)
   Input:   TCATBALF (category balances from STEP 1)
            XREFFILE (cross-reference)
            DISCGRP  (disclosure/interest rate groups)
            ACCTFILE (account master)
   Output:  TRANSACT (interest charge transactions)
            ACCTFILE (updated with interest via REWRITE)
   Logic:   - For each category balance, look up interest rate
            - Calculate interest = balance × rate / 365
            - Create interest transaction records
            - Update account balance with interest charges

 ══════════════════════════════════════════════════════════════════
 STEP 3: CREASTMT                        (Statement Generation)
 ══════════════════════════════════════════════════════════════════
   JCL:     CREASTMT.JCL
   Sub-Steps:
     DELDEF01: IDCAMS — delete/define statement VSAM
     STEP010:  SORT — sort transactions by card number + tran ID
     STEP020:  IDCAMS — REPRO sorted PS into VSAM
     STEP030:  IEFBR14 — delete old output files
     STEP040:  CBSTM03A (924 LOC) → calls CBSTM03B (230 LOC)
   Input:   TRNXFILE (sorted transactions)
            XREFFILE, ACCTFILE, CUSTFILE (master files)
   Output:  STMTFILE (text statements)
            HTMLFILE (HTML statements)
   Logic:   - For each card: look up customer + account
            - Format statement header, transaction details
            - Calculate statement balance
            - Generate text and HTML output

 ══════════════════════════════════════════════════════════════════
 STEP 4: TRANREPT                        (Daily Report)
 ══════════════════════════════════════════════════════════════════
   JCL:     TRANREPT.jcl
   Sub-Steps:
     STEP05R: SORT — sort transactions for reporting
     STEP10R: CBTRN03C (649 LOC)
   Input:   TRANFILE (master transactions)
            CARDXREF, TRANTYPE, TRANCATG (reference data)
            DATEPARM (date range parameter)
   Output:  TRANREPT (formatted daily transaction report)
   Logic:   - Sort transactions by account
            - Look up type and category descriptions
            - Format detail lines, page totals, account totals
            - Calculate grand total

 ══════════════════════════════════════════════════════════════════
 OPTIONAL: CBEXPORT / CBIMPORT           (Branch Data Migration)
 ══════════════════════════════════════════════════════════════════
   JCL:     CBEXPORT.jcl / CBIMPORT.jcl
   Programs: CBEXPORT (582 LOC) / CBIMPORT (487 LOC)
   Purpose: Export/import all entity data for branch migration
   Input/Output: All master VSAM files ↔ EXPFILE (multi-record flat file)
```

### 3.2 Pipeline Dependency Chain

```
DALYTRAN ─► POSTTRAN ─► INTCALC ─► CREASTMT ─► TRANREPT
  (input)   (CBTRN02C)  (CBACT04C) (CBSTM03A)  (CBTRN03C)
             │            │          │            │
             ├─TRANFILE──►│──────────│────────────┘
             ├─ACCTFILE──►│──────────┘
             ├─TCATBALF──►│
             └─DALYREJS   └─TRANSACT──►CREASTMT
```

### 3.3 IMS Database Pipeline (Authorization Sub-Application)

```
 ══════════════════════════════════════════════════════════════════
 LOAD: LOADPADB.JCL → PAUDBLOD
   Input:  Flat files (root + child segments)
   Output: IMS PAUTHDB database (CIPAUSMY + CIPAUDTY segments)

 ONLINE: Real-time authorization via COPAUA0C (MQ-driven)
   Input:  MQ authorization request
   Process: CICS reads → IMS lookup → fraud rules → MQ response

 PURGE: CBPAUP0J.jcl → CBPAUP0C
   Purpose: Delete expired pending authorizations from IMS

 UNLOAD: UNLDPADB.JCL → PAUDBUNL (or DBUNLDGS via GSAM)
   Input:  IMS PAUTHDB database
   Output: Flat files for backup/migration
 ══════════════════════════════════════════════════════════════════
```

### 3.4 Online-to-Batch Handoff

CICS online program CORPT00C triggers batch report generation by writing JCL to the CICS internal reader:

```
CORPT00C (Online)
    │
    │ EXEC CICS WRITEQ TD (INTRDRJ1)
    ▼
INTRDRJ1.JCL (Submitted to internal reader)
    │
    │ IDCAMS backup + submit INTRDRJ2
    ▼
INTRDRJ2.JCL (Chained job)
    │
    │ IDCAMS backup
    ▼
Batch report generation pipeline
```

---

## 4. Copybook Dependency Matrix

### 4.1 Most Referenced Copybooks (by program count)

| Rank | Copybook | Programs Using It | Purpose |
|------|----------|-------------------|---------|
| 1 | COCOM01Y | 21 programs | COMMAREA — all CICS programs |
| 2 | DFHAID | 19 programs | CICS AID byte definitions |
| 3 | DFHBMSCA | 19 programs | BMS attribute constants |
| 4 | COTTL01Y | 18 programs | Screen title |
| 5 | CSDAT01Y | 18 programs | Date/time working storage |
| 6 | CSMSG01Y | 18 programs | Common messages |
| 7 | CSUSR01Y | 14 programs | User security record |
| 8 | CVACT03Y | 14 programs | Card cross-reference record |
| 9 | CVACT01Y | 12 programs | Account record |
| 10 | CVCUS01Y | 10 programs | Customer record |
| 11 | CVTRA05Y | 10 programs | Transaction record |
| 12 | CVACT02Y | 8 programs | Card record |
| 13 | CVCRD01Y | 7 programs | Card work areas / AID processing |
| 14 | CIPAUDTY | 8 programs | IMS authorization detail |
| 15 | CIPAUSMY | 7 programs | IMS authorization summary |

### 4.2 Programs with Most Copybook Dependencies

| Program | Unique Copybooks | Total COPY Statements (incl. REPLACING) |
|---------|-----------------|----------------------------------------|
| COACTUPC | 19 unique | 58 (39× CSSETATY REPLACING) |
| COTRTLIC | 13 unique | 13 |
| COACTVWC | 14 unique | 14 |
| COPAUA0C | 14 unique | 14 |
| COPAUS0C | 13 unique | 13 |
| COCRDSLC | 14 unique | 14 |
| COCRDUPC | 14 unique | 14 |
| COTRTUPC | 12 unique | 13 (1× CSSETATY REPLACING) |
| COCRDLIC | 12 unique | 12 |
