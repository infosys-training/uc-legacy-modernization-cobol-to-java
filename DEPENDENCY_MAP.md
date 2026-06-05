# Dependency Map — CardDemo COBOL Estate

> **Generated:** 2026-06-05 | **Application:** CardDemo — Credit Card Management System  
> **Repository:** infosys-training/uc-legacy-modernization-cobol-to-java

---

## 1. Online Navigation Call Graph (CICS XCTL/LINK Chains)

```
COSGN00C (Sign-On — Entry Point)
│
├─── [Admin Path: CDEMO-USER-TYPE = 'A'] ──► COADM01C (Admin Menu Hub)
│    │
│    ├── Option 1 ──XCTL──► COUSR00C (User List)
│    │                         ├──XCTL──► COUSR02C (User Update)
│    │                         └──XCTL──► COUSR03C (User Delete)
│    │
│    ├── Option 2 ──XCTL──► COUSR01C (User Add)
│    │
│    ├── Option 5 ──XCTL──► COTRTLIC (Tran Type List — DB2)
│    │
│    └── Option 6 ──XCTL──► COTRTUPC (Tran Type Update — DB2)
│
└─── [User Path: CDEMO-USER-TYPE = 'U'] ──► COMEN01C (Main Menu Hub — 11 Options)
     │
     ├── Option  1 ──XCTL──► COACTVWC  (Account View)
     ├── Option  2 ──XCTL──► COACTUPC  (Account Update — 4,236 LOC)
     ├── Option  3 ──XCTL──► COCRDLIC  (Credit Card List)
     │                         ├──XCTL──► COCRDSLC (Card Detail View)
     │                         └──XCTL──► COCRDUPC (Card Update)
     ├── Option  4 ──XCTL──► COCRDSLC  (Credit Card View — direct)
     ├── Option  5 ──XCTL──► COCRDUPC  (Credit Card Update — direct)
     ├── Option  6 ──XCTL──► COTRN00C  (Transaction List)
     │                         └──XCTL──► COTRN01C (Transaction View)
     ├── Option  7 ──XCTL──► COTRN01C  (Transaction View — direct)
     ├── Option  8 ──XCTL──► COTRN02C  (Transaction Add)
     ├── Option  9 ──XCTL──► CORPT00C  (Report Request)
     │                         ├──Submits──► INTRDRJ1.JCL (Tran Report)
     │                         └──Submits──► INTRDRJ2.JCL (Statement Gen)
     ├── Option 10 ──XCTL──► COBIL00C  (Bill Payment)
     └── Option 11 ──XCTL──► COPAUS0C  (Auth Summary — IMS)
                               └──LINK──► COPAUS1C (Auth Detail)
                                            └──LINK──► COPAUS2C (Fraud Flag — DB2)
```

**Key patterns:**
- **Hub-and-spoke:** COMEN01C (12 outbound dependencies) and COADM01C (6 outbound) are the two navigation hubs
- **XCTL vs LINK:** Most navigation uses XCTL (transfer control, no return). The COPAUS chain uses LINK (call/return) because the IMS PSB context must be maintained
- **Batch bridge:** CORPT00C is the only online program that submits batch JCL (INTRDRJ1/J2) via CICS internal reader

---

## 2. Batch Call Graph

```
CBACT01C ──CALL──► COBDATFT (assembler — date formatting)
CBACT01C ──CALL──► CEE3ABD  (LE — abnormal termination)

CBACT02C ──CALL──► CEE3ABD  (LE — abnormal termination)

CBACT03C ──CALL──► CEE3ABD  (LE — abnormal termination)

CBCUS01C ──CALL──► CEE3ABD  (LE — abnormal termination)

CBSTM03A ──CALL──► CBSTM03B (COBOL submodule — file I/O handler)

COBSWAIT ──CALL──► MVSWAIT  (assembler — MVS wait routine)

CSUTLDTC ──CALL──► CEEDAYS  (LE — date conversion to Lilian)

CBPAUP0C ──DLI───► IMS (GN, GNP, DLET on PAUTBPCB)

COPAUA0C ──DLI───► IMS (SCHD, TERM, GU, GNP)
         ──CALL──► MQOPEN, MQGET, MQPUT1, MQCLOSE (MQ API)
         ──SQL───► DB2 (INSERT fraud record)

PAUDBLOD ──DLI───► IMS (ISRT, GU on PAUTBPCB)

PAUDBUNL ──DLI───► IMS (GN, GNP on PAUTBPCB)

DBUNLDGS ──DLI───► IMS (GN, GNP on PAUTBPCB + GSAM ISRT)

COBTUPDT ──SQL───► DB2 (INSERT/UPDATE on TRAN_TYPE)

COTRTLIC ──SQL───► DB2 (CURSOR-based SELECT on TRAN_TYPE, TRAN_CAT)

COTRTUPC ──SQL───► DB2 (INSERT/UPDATE/DELETE on TRAN_TYPE, TRAN_CAT)
```

**External dependencies (non-COBOL):**
| Symbol | Type | Called By | Purpose |
|--------|------|-----------|---------|
| `COBDATFT` | Assembler | CBACT01C | Date formatting for account output |
| `MVSWAIT` | Assembler | COBSWAIT | MVS WAIT SVC for batch pausing |
| `CEE3ABD` | LE Runtime | CBACT02C, CBACT03C, CBCUS01C | Abnormal program termination |
| `CEEDAYS` | LE Runtime | CSUTLDTC | Convert date to Lilian day number |
| `DSNTIAC` | DB2 Utility | COTRTLIC, COTRTUPC | Format SQLCA error messages |

---

## 3. Dataset Lineage — VSAM File Access Matrix

### 3.1 Who Reads/Writes Each File

| VSAM Dataset | Key | Writers (programs that modify) | Readers (programs that read only) |
|--------------|-----|-------------------------------|-----------------------------------|
| **ACCTFILE** (Account) | ACCT-ID 9(11) | CBACT04C (REWRITE), CBTRN02C (REWRITE), COACTUPC (REWRITE), COCRDUPC (REWRITE), COBIL00C (REWRITE), CBIMPORT (WRITE) | CBACT01C, CBSTM03A/B, COACTVWC, COCRDSLC, COTRN02C, COPAUA0C, COPAUS0C, CBEXPORT |
| **CARDFILE** (Card) | CARD-NUM X(16) | COCRDUPC (REWRITE), CBIMPORT (WRITE) | CBACT02C, COACTVWC, COCRDLIC, COCRDSLC, COPAUS0C |
| **CUSTFILE** (Customer) | CUST-ID 9(09) | COACTUPC (REWRITE), CBIMPORT (WRITE) | CBCUS01C, CBSTM03A/B, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, CBEXPORT |
| **CARDXREF** (Cross-Ref) | XREF-CARD-NUM X(16) | CBIMPORT (WRITE) | CBACT03C, CBACT04C, CBTRN02C, CBTRN03C, CBSTM03A/B, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C, CBEXPORT |
| **TRANSACT** (Transaction) | TRAN-ID X(16) | CBACT04C (WRITE), CBTRN02C (WRITE), CBIMPORT (WRITE) | CBTRN01C, CBTRN03C, COTRN00C, COTRN01C, CORPT00C, CBEXPORT |
| **DALYTRAN** (Daily Input) | Sequential | COTRN02C-online (WRITE) | CBTRN01C, CBTRN02C-batch |
| **DALYREJS** (Rejections) | Sequential | CBTRN02C (WRITE) | — |
| **TCATBALF** (Cat Balance) | Composite key | CBTRN02C (WRITE/REWRITE) | CBACT04C |
| **TRANTYPE** (Tran Types) | TRAN-TYPE X(02) | — | CBTRN03C |
| **TRANCATG** (Tran Cats) | TRAN-CAT 9(04) | — | CBTRN03C |
| **DISCGRP** (Discount Grp) | GROUP-ID X(10) | — | CBACT04C |
| **USRSEC** (User Security) | USR-ID X(08) | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | COSGN00C, COUSR00C |

### 3.2 JCL Job → Dataset Mapping

| JCL Job | Operation | Datasets Touched |
|---------|-----------|-----------------|
| `ACCTFILE.jcl` | DELETE → DEFINE → REPRO | `ACCTDATA.VSAM.KSDS` ← `ACCTDATA.PS` |
| `CARDFILE.jcl` | DELETE → DEFINE → REPRO → AIX → PATH → BLDINDEX | `CARDDATA.VSAM.KSDS` + AIX ← `CARDDATA.PS` |
| `CUSTFILE.jcl` | DELETE → DEFINE → REPRO | `CUSTDATA.VSAM.KSDS` ← `CUSTDATA.PS` |
| `XREFFILE.jcl` | DELETE → DEFINE → REPRO → AIX | `CARDXREF.VSAM.KSDS` + AIX ← `CARDXREF.PS` |
| `TRANFILE.jcl` | DELETE → DEFINE → REPRO → AIX → PATH → BLDINDEX | `TRANSACT.VSAM.KSDS` + AIX ← `TRANSACT.PS` |
| `DISCGRP.jcl` | DELETE → DEFINE → REPRO | `DISCGRP.VSAM.KSDS` ← `DISCGRP.PS` |
| `TCATBALF.jcl` | DELETE → DEFINE → REPRO | `TCATBAL.VSAM.KSDS` ← `TCATBAL.PS` |
| `TRANTYPE.jcl` | DELETE → DEFINE → REPRO | `TRANTYPE.VSAM.KSDS` ← `TRANTYPE.PS` |
| `TRANCATG.jcl` | DELETE → DEFINE → REPRO | `TRANCATG.VSAM.KSDS` ← `TRANCATG.PS` |
| `DUSRSECJ.jcl` | DELETE → DEFINE → REPRO | `USRSEC.VSAM.KSDS` ← `USRSEC.PS` |
| `POSTTRAN.jcl` | EXEC CBTRN02C | R: DALYTRAN, XREF · R/W: ACCT, TCATBAL · W: TRANSACT, REJS |
| `INTCALC.jcl` | EXEC CBACT04C | R: TCATBAL, XREF, DISCGRP · R/W: ACCT · W: TRANSACT |
| `CREASTMT.JCL` | EXEC CBSTM03A | R: XREF, CUST, ACCT, TRNX · W: STMT, HTML |
| `TRANREPT.jcl` | EXEC CBTRN03C | R: TRANSACT, XREF, TRANTYPE, TRANCATG · W: REPTFILE |
| `CBEXPORT.jcl` | EXEC CBEXPORT | R: CUST, ACCT, XREF, TRANSACT · W: EXPORT, REPT |
| `CBIMPORT.jcl` | EXEC CBIMPORT | R: IMPORT · W: CUST, ACCT, XREF, TRANSACT, REJS |
| `TRANBKP.jcl` | IDCAMS REPRO | TRANSACT.VSAM.KSDS → TRANSACT.BACKUP.PS |
| `COMBTRAN.jcl` | SORT → MERGE | Combine daily transaction segments |
| `DALYREJS.jcl` | SORT | Sort rejection records |

---

## 4. End-to-End Batch Pipeline Flow

### 4.1 Daily Processing Pipeline

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     DAILY BATCH PROCESSING CYCLE                        │
│                    (Managed by Control-M Scheduler)                      │
└─────────────────────────────────────────────────────────────────────────┘

Phase 1: TRANSACTION POSTING
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│ DALYTRAN (input)  │────►│  POSTTRAN.jcl    │────►│ TRANSACT (master)│
│ Daily transaction │     │  (CBTRN02C)      │     │ Posted trans     │
│ file (sequential) │     │                  │     └──────────────────┘
└──────────────────┘     │  Validates cards  │     ┌──────────────────┐
┌──────────────────┐     │  via CARDXREF     │────►│ DALYREJS         │
│ CARDXREF (lookup) │────►│  Updates ACCOUNT  │     │ Rejected records │
└──────────────────┘     │  Updates TCATBAL  │     └──────────────────┘
┌──────────────────┐     │                  │
│ ACCTFILE (R/W)   │◄───►│                  │
└──────────────────┘     │                  │
┌──────────────────┐     │                  │
│ TCATBALF (R/W)   │◄───►│                  │
└──────────────────┘     └──────────────────┘
                                │
                                ▼
Phase 2: INTEREST CALCULATION
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│ TCATBALF (input)  │────►│  INTCALC.jcl     │────►│ TRANSACT (write) │
│ Cat balances      │     │  (CBACT04C)      │     │ Interest trans   │
└──────────────────┘     │                  │     └──────────────────┘
┌──────────────────┐     │  Reads discount  │
│ DISCGRP (rates)   │────►│  group rates     │
└──────────────────┘     │  Computes interest│
┌──────────────────┐     │  per account     │
│ CARDXREF (lookup) │────►│                  │
└──────────────────┘     │                  │
┌──────────────────┐     │                  │
│ ACCTFILE (R/W)   │◄───►│  Updates balance │
└──────────────────┘     └──────────────────┘
                                │
                                ▼
Phase 3: STATEMENT GENERATION
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│ CARDXREF (input)  │────►│  CREASTMT.JCL    │────►│ STMTFILE (text)  │
│ Iterate accounts  │     │  (CBSTM03A →     │     │ Text statements  │
└──────────────────┘     │   CBSTM03B)      │     └──────────────────┘
┌──────────────────┐     │                  │     ┌──────────────────┐
│ CUSTFILE (lookup) │────►│  Calls CBSTM03B  │────►│ HTMLFILE (HTML)  │
└──────────────────┘     │  for all I/O     │     │ HTML statements  │
┌──────────────────┐     │  115+ I/O ops    │     └──────────────────┘
│ ACCTFILE (lookup) │────►│  (highest in     │
└──────────────────┘     │   estate)        │
┌──────────────────┐     │                  │
│ TRANSACT (lookup) │────►│                  │
└──────────────────┘     └──────────────────┘
                                │
                                ▼
Phase 4: TRANSACTION REPORTING
┌──────────────────┐     ┌──────────────────┐     ┌──────────────────┐
│ TRANSACT (input)  │────►│  TRANREPT.jcl    │────►│ REPTFILE (report)│
│ Master trans file │     │  (CBTRN03C)      │     │ Formatted report │
└──────────────────┘     │                  │     └──────────────────┘
┌──────────────────┐     │  Looks up type   │
│ CARDXREF (lookup) │────►│  and category    │
└──────────────────┘     │  descriptions    │
┌──────────────────┐     │                  │
│ TRANTYPE (lookup) │────►│                  │
└──────────────────┘     │                  │
┌──────────────────┐     │                  │
│ TRANCATG (lookup) │────►│                  │
└──────────────────┘     └──────────────────┘
```

### 4.2 Periodic Processing

```
WEEKLY:   CBEXPORT.jcl (CBEXPORT) ── Export customer/account/card/transaction data
                                      for branch migration

AD-HOC:   CBIMPORT.jcl (CBIMPORT) ── Import and validate migration data into
                                      CardDemo VSAM files

IMS MAINT: CBPAUP0J.jcl (CBPAUP0C) ── Purge expired pending authorizations
           LOADPADB.JCL (PAUDBLOD) ── Load IMS auth database from flat file
           UNLDPADB.JCL (PAUDBUNL) ── Unload IMS auth database to flat file
           UNLDGSAM.JCL (DBUNLDGS) ── GSAM-based IMS unload

DB2 MAINT: CREADB21.jcl            ── Create/reload DB2 transaction type tables
           MNTTRDB2.jcl (COBTUPDT) ── Batch maintenance of transaction types
           TRANEXTR.jcl            ── Extract transaction types to flat file
```

---

## 5. Copybook Dependency Matrix

### Programs → Copybooks (which programs include which copybooks)

| Copybook | Programs Using It | Count |
|----------|-------------------|-------|
| `COCOM01Y` | COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CORPT00C, COUSR00C–03C, COPAUS0C, COPAUS1C | 19 |
| `COTTL01Y` | All 17 main CICS programs + COPAUS0C, COPAUS1C | 19 |
| `CSDAT01Y` | All 17 main CICS programs + COPAUS0C, COPAUS1C | 19 |
| `CSMSG01Y` | All 17 main CICS programs + COPAUS0C, COPAUS1C | 19 |
| `CSUSR01Y` | COSGN00C, COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COUSR00C–03C, COPAUS0C, COPAUS1C | 16 |
| `DFHAID` | All CICS programs | 19 |
| `DFHBMSCA` | All CICS programs | 19 |
| `CVACT01Y` | CBACT01C, CBACT04C, CBSTM03A, CBTRN02C, COACTUPC, COACTVWC, COCRDUPC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT | 13 |
| `CVACT03Y` | CBACT03C, CBACT04C, CBTRN02C, CBTRN03C, CBSTM03A, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT | 16 |
| `CVACT02Y` | CBACT02C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C | 6 |
| `CVCUS01Y` | CBCUS01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COPAUS0C, CBEXPORT, CBIMPORT | 10 |
| `CVTRA05Y` | CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, CORPT00C, CBEXPORT, CBIMPORT | 10 |
| `CIPAUSMY` | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, PAUDBLOD, PAUDBUNL, DBUNLDGS | 7 |
| `CIPAUDTY` | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, PAUDBLOD, PAUDBUNL, DBUNLDGS | 8 |
| `CSLKPCDY` | COACTUPC | 1 |
| `CSSETATY` | COACTUPC (×3 via REPLACING) | 1 |
| `UNUSED1Y` | *(none)* | 0 |

---

## 6. Inter-Program Dependency Summary

### 6.1 Most-Connected Programs (by dependency count)

| Rank | Program | Outbound Deps | Inbound Deps | Total | Role |
|------|---------|--------------|--------------|-------|------|
| 1 | COMEN01C | 11 (XCTL targets) | 1 (COSGN00C) | 12 | Main menu hub |
| 2 | COADM01C | 6 (XCTL targets) | 1 (COSGN00C) | 7 | Admin menu hub |
| 3 | COACTUPC | 3 (VSAM files) | 2 (COMEN01C, COADM01C) | 5 | Account update |
| 4 | COPAUA0C | 4 (IMS+MQ+DB2+VSAM) | 0 | 4 | Auth decision |
| 5 | CORPT00C | 2 (JCL submissions) | 1 (COMEN01C) | 3 | Report bridge |

### 6.2 Shared Data Coupling (programs sharing VSAM files)

ACCTFILE is the most heavily shared resource, accessed by **19 programs** (6 writers, 13 readers), making it the highest-risk dataset for migration.

CARDXREF is the second most shared at **16 programs**, serving as the universal lookup bridge between cards and accounts.
