# Dependency Map — CardDemo COBOL Estate

> Call graph, dataset lineage, and end-to-end batch pipeline flow for 44 programs, 47 copybooks, and 46 JCL jobs.

---

## 1. Online Navigation Tree (CICS XCTL/LINK)

```
COSGN00C (Sign-On — Entry Point)
│
├── [User Type = 'A'] ──XCTL──► COADM01C (Admin Menu)
│   │
│   ├── Opt 1 ──XCTL──► COUSR00C (User List)
│   │                       ├── Select ──XCTL──► COUSR02C (User Update)
│   │                       └── Delete ──XCTL──► COUSR03C (User Delete)
│   │
│   ├── Opt 2 ──XCTL──► COUSR01C (User Add)
│   │
│   ├── Opt 5 ──XCTL──► COTRTLIC (Tran Type List — DB2)
│   │
│   └── Opt 6 ──XCTL──► COTRTUPC (Tran Type Update — DB2)
│
└── [User Type = 'U'] ──XCTL──► COMEN01C (Main Menu — Central Hub)
    │
    ├── Opt 1  ──XCTL──► COACTVWC (Account View)
    ├── Opt 2  ──XCTL──► COACTUPC (Account Update)
    ├── Opt 3  ──XCTL──► COCRDLIC (Card List)
    │                       └── Select ──XCTL──► COCRDSLC (Card View)
    │                                               └── Update ──XCTL──► COCRDUPC (Card Update)
    ├── Opt 6  ──XCTL──► COTRN00C (Transaction List)
    │                       └── Select ──XCTL──► COTRN01C (Transaction View)
    ├── Opt 8  ──XCTL──► COTRN02C (Transaction Add)
    │                       └── CALL ──► CSUTLDTC (Date Utility)
    ├── Opt 9  ──XCTL──► CORPT00C (Report Request)
    │                       └── Submits JCL via Internal Reader (INTRDR)
    ├── Opt 10 ──XCTL──► COBIL00C (Bill Payment)
    └── Opt 11 ──XCTL──► COPAUS0C (Auth Summary — IMS)
                            └── LINK ──► COPAUS1C (Auth Detail — IMS)
                                            └── LINK ──► COPAUS2C (Auth Fraud Mark — DB2)
```

### Key Navigation Patterns

| Pattern | Programs | Mechanism |
|---------|----------|-----------|
| Hub-and-Spoke | COMEN01C → 11 targets | XCTL with COMMAREA |
| Admin Hub | COADM01C → 6 targets | XCTL with COMMAREA |
| IMS Chain | COPAUS0C → COPAUS1C → COPAUS2C | LINK (returns to caller) |
| Batch Bridge | CORPT00C → JCL submission | CICS WRITE to internal reader |
| Back Navigation | All screens → COMEN01C or COADM01C | PF3 XCTL back to menu |

---

## 2. Batch CALL Graph

```
CBACT01C ──CALL──► COBDATFT        (Assembler date formatter)
         ──CALL──► CEE3ABD         (LE abnormal termination)

CBACT02C ──CALL──► CEE3ABD

CBACT03C ──CALL──► CEE3ABD

CBACT04C ──CALL──► CEE3ABD

CBCUS01C ──CALL──► CEE3ABD

CBEXPORT ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B       (I/O submodule — 15 calls)
         ──CALL──► CEE3ABD

CBTRN01C ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT         (Assembler wait routine)

COTRN02C ──CALL──► CSUTLDTC       (Date conversion utility)

CSUTLDTC ──CALL──► CEEDAYS        (LE Lilian date conversion)
```

### External (Non-COBOL) Dependencies

| External Program | Type | Called By | Purpose |
|-----------------|------|-----------|---------|
| COBDATFT | Assembler | CBACT01C | Date formatting for account records |
| MVSWAIT | Assembler | COBSWAIT | Timed wait routine |
| CEE3ABD | LE Runtime | CBACT01C–04C, CBCUS01C, CBEXPORT, CBSTM03A, CBTRN01C | Abnormal termination handler |
| CEEDAYS | LE Runtime | CSUTLDTC | Convert date to Lilian day number |
| DSNTIAC | DB2 Utility | COTRTLIC, COTRTUPC (via CSDB2RPY) | Format SQLCA error messages |
| DFHCSDUP | CICS Utility | CBADMCDJ.jcl | CSD resource definition |

---

## 3. Copybook Dependency Matrix

### Top 15 Most-Referenced Copybooks

| Rank | Copybook | References | Category |
|------|----------|-----------|----------|
| 1 | CSSETATY | 39 | Screen attribute macros (COPY REPLACING) |
| 2 | DFHBMSCA | 14 | CICS BMS attribute constants |
| 3 | DFHAID | 14 | CICS attention identifier codes |
| 4 | COCOM01Y | 14 | COMMAREA — inter-program communication |
| 5 | COTTL01Y | 14 | Screen titles |
| 6 | CSDAT01Y | 14 | Date/time working storage |
| 7 | CSMSG01Y | 14 | Common messages |
| 8 | CSUSR01Y | 12 | User security record |
| 9 | CVACT01Y | 11 | Account record |
| 10 | CVACT03Y | 11 | Card-Account cross-reference |
| 11 | CVTRA05Y | 8 | Transaction record |
| 12 | CVACT02Y | 7 | Card record |
| 13 | CVCUS01Y | 6 | Customer record |
| 14 | CVCRD01Y | 4 | Common card work areas |
| 15 | CIPAUDTY | 5 | IMS auth detail segment |

### Program-to-Copybook Reference Map

Programs referencing the most copybooks:

| Program | Copybook Count | Copybooks |
|---------|---------------|-----------|
| COACTUPC | 15 | COACTUP, CSSETATY×3, CSLKPCDY, CSUTLDPY, CSUTLDWY, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, DFHAID, DFHBMSCA |
| COPAUA0C | 16 | CMQODV×2, CMQMDV×2, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| COPAUS0C | 14 | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| COTRTUPC | 13 | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |
| COTRTLIC | 11 | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| COCRDUPC | 11 | COCRDUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, DFHAID, DFHBMSCA |
| COACTVWC | 11 | COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, DFHAID, DFHBMSCA |

---

## 4. Dataset Lineage — VSAM File Access Matrix

### Which Programs Read/Write Which Files

| VSAM File | Writers (UPDATE/REWRITE/WRITE) | Readers (READ/STARTBR) |
|-----------|-------------------------------|------------------------|
| **ACCTFILE** (Account) | CBACT04C (I-O), CBIMPORT (W), COACTUPC (REWRITE), COBIL00C (REWRITE), CBTRN02C (I-O) | CBACT01C, CBSTM03B, CBTRN01C, CBTRN02C, CBEXPORT, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C, COPAUS0C, COPAUA0C |
| **CARDFILE** (Card) | CBIMPORT (W), COCRDUPC (REWRITE) | CBACT02C, CBTRN01C, CBEXPORT, COACTVWC, COCRDLIC, COCRDSLC, COPAUS0C |
| **CUSTFILE** (Customer) | CBIMPORT (W) | CBCUS01C, CBSTM03B, CBTRN01C, CBEXPORT, COACTUPC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| **CARDXREF** (Cross-Ref) | CBIMPORT (W) | CBACT03C, CBACT04C, CBSTM03B, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, COACTUPC, COACTVWC, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C |
| **TRANSACT** (Transaction) | CBACT04C (W), CBTRN02C (W), COBIL00C (WRITE), COTRN02C (WRITE), CBIMPORT (W) | CBTRN03C, CBEXPORT, COTRN00C (browse), COTRN01C |
| **TCATBALF** (Cat Balance) | CBTRN02C (I-O) | CBACT04C |
| **DISCGRP** (Disclosure) | *(setup only via JCL)* | CBACT04C |
| **TRANTYPE** (Tran Type) | *(setup only via JCL)* | CBTRN03C |
| **TRANCATG** (Tran Cat) | *(setup only via JCL)* | CBTRN03C |
| **DALYTRAN** (Daily Tran) | *(external feed)* | CBTRN01C, CBTRN02C |
| **USRSEC** (User Security) | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | COSGN00C, COUSR00C (browse) |

### DB2 Table Access

| Table | Writers | Readers |
|-------|---------|---------|
| **TRTYP** (Transaction Type) | COBTUPDT (INSERT/UPDATE/DELETE), COTRTUPC (INSERT/UPDATE/DELETE) | COTRTLIC (cursor-based SELECT) |
| **TRCAT** (Transaction Category) | COTRTUPC (INSERT/UPDATE/DELETE) | COTRTUPC (SELECT for validation) |
| **FRAUD** | COPAUS2C (INSERT) | *(not read by COBOL programs)* |

### IMS Database Access

| Database/PCB | DL/I Operations | Programs |
|-------------|----------------|----------|
| Pending Auth (PAUT-PCB) | GU, GN, GNP, DLET, CHKP | CBPAUP0C |
| Pending Auth (PAUT-PCB) | GU, GNP, SCHD, TERM | COPAUA0C, COPAUS0C |
| Pending Auth (PAUT-PCB) | GU, GNP, REPL, SCHD, TERM | COPAUS1C |

### MQ Queue Access

| Queue | Operation | Programs |
|-------|-----------|----------|
| Request Queue (WS-REQUEST-QNAME) | MQOPEN, MQGET | COPAUA0C |
| Reply Queue (WS-REPLY-QNAME) | MQOPEN, MQPUT1 | COPAUA0C |
| Error Queue | MQOPEN, MQPUT1 | COPAUA0C |
| Account Inquiry Queue | MQOPEN, MQGET, MQPUT | COACCT01 |
| Date Inquiry Queue | MQOPEN, MQGET, MQPUT | CODATE01 |

---

## 5. JCL-to-Program-to-Dataset Lineage

### Batch Processing Data Flow

```
                    ┌──────────────────────────────────────────────────┐
                    │              EXTERNAL FEEDS                       │
                    │  DALYTRAN.PS  (daily transaction flat file)       │
                    └───────────────────────┬──────────────────────────┘
                                            │
                    ┌───────────────────────▼──────────────────────────┐
                    │  POSTTRAN.jcl → CBTRN02C                         │
                    │  Reads: DALYTRAN, XREFFILE                       │
                    │  Updates: ACCTFILE (I-O), TCATBALF (I-O)         │
                    │  Writes: TRANSACT, DALYREJS (GDG)                │
                    └───────────────────────┬──────────────────────────┘
                                            │
                    ┌───────────────────────▼──────────────────────────┐
                    │  INTCALC.jcl → CBACT04C                          │
                    │  Reads: TCATBALF, XREFFILE, DISCGRP              │
                    │  Updates: ACCTFILE (I-O)                          │
                    │  Writes: TRANSACT (interest transactions)        │
                    └───────────────────────┬──────────────────────────┘
                                            │
              ┌─────────────────────────────┼─────────────────────────────┐
              │                             │                             │
              ▼                             ▼                             ▼
┌─────────────────────────┐  ┌─────────────────────────┐  ┌─────────────────────────┐
│  TRANREPT.jcl            │  │  (No explicit JCL —      │  │  CBEXPORT.jcl            │
│  STEP05R: SORT           │  │   triggered by CORPT00C  │  │  → CBEXPORT              │
│  STEP10R: CBTRN03C       │  │   via internal reader)   │  │  Reads: CUSTFILE,        │
│  Reads: TRANSACT,        │  │  CBSTM03A → CBSTM03B     │  │   ACCTFILE, XREFFILE,    │
│   CARDXREF, TRANTYPE,    │  │  Reads: TRNXFILE,        │  │   TRANSACT, CARDFILE     │
│   TRANCATG, DATEPARM     │  │   XREFFILE, CUSTFILE,    │  │  Writes: EXPFILE         │
│  Writes: TRANREPT (GDG)  │  │   ACCTFILE               │  └─────────────────────────┘
└─────────────────────────┘  │  Writes: STMTFILE,        │
                              │   HTMLFILE                │
                              └─────────────────────────┘
```

### Data Setup JCL Lineage

```
Flat File (PS)              JCL Job              VSAM Cluster (KSDS)
─────────────────           ──────────           ─────────────────────
ACCTDATA.PS        ──►  ACCTFILE.jcl   ──►  ACCTDATA.VSAM.KSDS
CARDDATA.PS        ──►  CARDFILE.jcl   ──►  CARDDATA.VSAM.KSDS
CUSTDATA.PS        ──►  CUSTFILE.jcl   ──►  CUSTDATA.VSAM.KSDS
CARDXREF.PS        ──►  XREFFILE.jcl   ──►  CARDXREF.VSAM.KSDS
TRANSACT.PS        ──►  TRANFILE.jcl   ──►  TRANSACT.VSAM.KSDS
DISCGRP.PS         ──►  DISCGRP.jcl    ──►  DISCGRP.VSAM.KSDS
TCATBAL.PS         ──►  TCATBALF.jcl   ──►  TCATBAL.VSAM.KSDS
TRANTYPE.PS        ──►  TRANTYPE.jcl   ──►  TRANTYPE.VSAM.KSDS
TRANCATG.PS        ──►  TRANCATG.jcl   ──►  TRANCATG.VSAM.KSDS
USRSEC seed data   ──►  DUSRSECJ.jcl   ──►  USRSEC.VSAM.KSDS
```

### Import/Export Data Flow

```
All 5 VSAM files ──► CBEXPORT.jcl (CBEXPORT) ──► EXPFILE (sequential)
                                                         │
EXPFILE (sequential) ──► CBIMPORT.jcl (CBIMPORT) ──► CUSTOUT, ACCTOUT,
                                                      XREFOUT, TRNXOUT,
                                                      CARDOUT, ERROUT
```

---

## 6. End-to-End Batch Pipeline Flow

### Daily Processing Cycle

```
Phase 1: DATA INGESTION
──────────────────────────────────────────────────────────
  External System → DALYTRAN.PS (daily transaction flat file)
                     │
Phase 2: TRANSACTION POSTING (POSTTRAN.jcl)
──────────────────────────────────────────────────────────
  CBTRN02C reads DALYTRAN
    ├── Validates card via XREFFILE
    ├── Updates ACCTFILE balances
    ├── Updates TCATBALF category balances
    ├── Writes valid transactions → TRANSACT
    └── Writes rejected records → DALYREJS (GDG)

Phase 3: INTEREST CALCULATION (INTCALC.jcl)
──────────────────────────────────────────────────────────
  CBACT04C reads TCATBALF + XREFFILE + DISCGRP
    ├── Calculates interest per account per category
    ├── Updates ACCTFILE with interest charges
    └── Writes interest transactions → TRANSACT

Phase 4: REPORTING (TRANREPT.jcl + CORPT00C)
──────────────────────────────────────────────────────────
  SORT: merge/sort TRANSACT by account + transaction
  CBTRN03C reads sorted TRANSACT + CARDXREF + TRANTYPE + TRANCATG
    └── Writes TRANREPT (GDG) — paginated daily transaction report

  CBSTM03A/B reads TRNXFILE + XREFFILE + CUSTFILE + ACCTFILE
    ├── Writes STMTFILE (plain text statements)
    └── Writes HTMLFILE (HTML format statements)

Phase 5: MAINTENANCE (periodic)
──────────────────────────────────────────────────────────
  TRANBKP.jcl: backup TRANSACT → TRANSACT.BKUP(+1), clear master
  COMBTRAN.jcl: merge multiple transaction sources → TRANSACT
  CBEXPORT.jcl: export all entities → EXPFILE for branch migration
```

### Scheduling Dependencies (Control-M)

```
Daily:    POSTTRAN → INTCALC → TRANREPT
                              → CREASTMT (statement generation)
Weekly:   CBEXPORT (branch data export)
Monthly:  Full statement cycle (CBSTM03A)
Ad-hoc:   CBIMPORT (data import with validation)
```

---

## 7. Cross-Subsystem Integration Points

| Integration Point | Source | Target | Mechanism | Data Flow |
|-------------------|--------|--------|-----------|-----------|
| Auth Decision | MQ Request Queue | COPAUA0C → IMS + VSAM + MQ Reply | MQ trigger → CICS RETRIEVE → DL/I + VSAM READ → MQ PUT1 |
| Report Submission | CORPT00C (CICS) | TRANREPT.jcl / CREASTMT.jcl | WRITE to CICS Internal Reader (INTRDR) |
| Online-to-Batch Shared Files | CICS programs | Batch programs | VSAM files (ACCTFILE, TRANSACT, CARDFILE, etc.) — requires CICS file CLOSE/OPEN cycle |
| DB2 Online-to-Batch | COTRTLIC/COTRTUPC (CICS) | COBTUPDT (batch) | Shared DB2 tables (TRTYP, TRCAT) |
| IMS Online-to-Batch | COPAUS0C/1C (CICS) | CBPAUP0C (BMP batch) | Shared IMS database (Pending Auth) |

---

## 8. Dependency Counts Summary

### Inbound Dependencies (programs that depend on this program)

| Program | Inbound Deps | Dependents |
|---------|-------------|------------|
| COMEN01C | 12 | All user-facing CICS programs return here |
| CSUTLDTC | 2 | COTRN02C, COTRTUPC (date validation) |
| CBSTM03B | 1 | CBSTM03A (I/O submodule) |
| COADM01C | 6 | All admin CICS programs return here |
| COSGN00C | 0 | Entry point — no inbound program deps |

### Outbound Dependencies (programs this program calls/transfers to)

| Program | Outbound Deps | Targets |
|---------|--------------|---------|
| COMEN01C | 11 | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C |
| COADM01C | 6 | COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC |
| CBSTM03A | 2 | CBSTM03B (×15 calls), CEE3ABD |
| COPAUS0C | 2 | COPAUS1C (LINK), IMS DL/I |
| COPAUS1C | 2 | COPAUS2C (LINK), IMS DL/I |
| CBACT01C | 2 | COBDATFT (CALL), CEE3ABD (CALL) |
