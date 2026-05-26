# Dependency Map

Program-to-program call graphs, dataset lineage, and end-to-end batch pipeline flows.

---

## Online Call Graph (XCTL-based)

```
                                 ┌──────────┐
                                 │ COSGN00C │  (CC00 - Sign-on)
                                 └────┬─────┘
                          ┌───────────┴───────────┐
                          │ XCTL                   │ XCTL
                    ┌─────▼─────┐          ┌──────▼──────┐
                    │ COADM01C  │          │  COMEN01C   │
                    │ (CA00)    │          │  (CM00)     │
                    │ Admin Menu│          │  User Menu  │
                    └─────┬─────┘          └──────┬──────┘
                          │                       │
      ┌───────┬───────┬───┴───┬──────┐    ┌──────┼──────────┬──────────┬──────────┬───────────┬────────┬──────────┬──────────┐
      │       │       │       │      │    │      │          │          │          │           │        │          │          │
  COUSR00C COUSR01C COUSR02C COUSR03C │  COACTVWC COACTUPC COCRDLIC COTRN00C  COTRN02C  COBIL00C  CORPT00C   COPAUS0C
  (CU00)   (CU01)   (CU02)   (CU03)  │  (CAVW)   (CAUP)   (CCLI)   (CT00)    (CT02)    (CB00)    (CR00)     (IMS sub-app)
  List     Add      Update   Delete   │   View    Update    List     List      Add       Bill Pay  Reports
      │                               │                       │        │
      ├─XCTL→ COUSR02C (update)       │              ┌────────┤        │
      └─XCTL→ COUSR03C (delete)       │              │        │        │
                                       │          COCRDSLC COTRN01C    │
                                  COTRTLIC        (CCDL)   (CT01)      │
                                  (DB2 sub-app)    Detail   Detail     │
                                       │              │                │
                                  COTRTUPC        COCRDUPC             │
                                  (DB2 sub-app)   (CCUP)               │
                                                   Update              │
                                                                       │
                                                               COPAUS1C (edit/update)
                                                               COPAUS2C (fraud report, DB2)
```

### XCTL Routing Details

| From Program | To Program | Condition |
|-------------|------------|-----------|
| COSGN00C | COADM01C | User type = 'A' (admin) |
| COSGN00C | COMEN01C | User type = 'U' (regular) |
| COMEN01C | COACTVWC | Menu option 1 — Account View |
| COMEN01C | COACTUPC | Menu option 2 — Account Update |
| COMEN01C | COCRDLIC | Menu option 3 — Credit Card List |
| COMEN01C | COCRDSLC | Menu option 4 — Credit Card View |
| COMEN01C | COCRDUPC | Menu option 5 — Credit Card Update |
| COMEN01C | COTRN00C | Menu option 6 — Transaction List |
| COMEN01C | COTRN01C | Menu option 7 — Transaction View |
| COMEN01C | COTRN02C | Menu option 8 — Transaction Add |
| COMEN01C | CORPT00C | Menu option 9 — Transaction Reports |
| COMEN01C | COBIL00C | Menu option 10 — Bill Payment |
| COMEN01C | COPAUS0C | Menu option 11 — Pending Auth View |
| COADM01C | COUSR00C | Admin option 1 — User List |
| COADM01C | COUSR01C | Admin option 2 — User Add |
| COADM01C | COUSR02C | Admin option 3 — User Update |
| COADM01C | COUSR03C | Admin option 4 — User Delete |
| COADM01C | COTRTLIC | Admin option 5 — Tran Type List (DB2) |
| COADM01C | COTRTUPC | Admin option 6 — Tran Type Maint (DB2) |
| COUSR00C | COUSR02C | Select user for update |
| COUSR00C | COUSR03C | Select user for delete |
| COCRDLIC | COCRDSLC | Select card for detail view |
| COCRDLIC | COCRDUPC | Select card for update |
| COTRN00C | COTRN01C | Select transaction for detail view |

---

## Batch CALL Graph

```
CBSTM03A ──CALL──▶ CBSTM03B          (file I/O subroutine)
CBACT01C ──CALL──▶ COBDATFT          (assembler date format utility)
COTRN02C ──CALL──▶ CSUTLDTC          (date validation via LE services)
CORPT00C ──CALL──▶ CSUTLDTC          (date validation via LE services)
COBSWAIT ──CALL──▶ MVSWAIT           (system wait utility)
CSUTLDTC ──CALL──▶ CEEDAYS           (LE intrinsic — date conversion)
CBACT01C ──CALL──▶ CEE3ABD           (LE intrinsic — abend)
```

### Sub-Application CALL Graphs

```
Authorization (IMS/DB2/MQ):
  COPAUA0C ──CALL──▶ MQOPEN, MQGET, MQPUT1    (MQ Series API)
  COPAUA0C ──EXEC DLI──▶ IMS DB (GU/REPL/ISRT/SCHD/TERM)
  COPAUS0C ──EXEC DLI──▶ IMS DB (GNP/GU/SCHD/TERM)
  COPAUS1C ──EXEC DLI──▶ IMS DB (GU/GNP/REPL)
  COPAUS2C ──EXEC SQL──▶ DB2
  CBPAUP0C ──EXEC DLI──▶ IMS DB (GN/GNP/DLET/CHKP)
  DBUNLDGS ──CALL──▶ CBLTDLI (GN/GNP/ISRT)
  PAUDBLOD ──CALL──▶ CBLTDLI (GU/ISRT)
  PAUDBUNL ──CALL──▶ CBLTDLI (GN/GNP)

VSAM/MQ:
  COACCT01 ──CALL──▶ MQOPEN, MQGET, MQPUT, MQCLOSE
  CODATE01 ──CALL──▶ MQOPEN, MQGET, MQPUT, MQCLOSE

Transaction Type (DB2):
  COTRTLIC ──EXEC SQL──▶ DB2 (cursor SELECT, UPDATE, INSERT, DELETE)
  COTRTUPC ──EXEC SQL──▶ DB2 (SELECT, UPDATE, INSERT, DELETE)
  COBTUPDT ──EXEC SQL──▶ DB2 (batch updates)
```

---

## Dataset Lineage

### VSAM Files — Readers and Writers

| Dataset | DD Name(s) | Defined By | Read By | Written By |
|---------|-----------|------------|---------|------------|
| Account Data | ACCTFILE, ACCTDAT | ACCTFILE.jcl | CBACT01C, COACTVWC, COACTUPC, COBIL00C, CBTRN01C, CBTRN02C, CBACT04C, CBEXPORT, CBSTM03B, COPAUA0C | COACTUPC, CBTRN02C, CBACT04C, COBIL00C |
| Card Data | CARDFILE, CARDDAT | CARDFILE.jcl | CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, CBTRN01C, CBEXPORT | COCRDUPC, COACTUPC |
| Customer Data | CUSTFILE, CUSTDAT | CUSTFILE.jcl | CBCUS01C, COACTVWC, COACTUPC, CBTRN01C, CBEXPORT, CBSTM03B, COPAUA0C | — (read-only in application code) |
| Transaction Data | TRANFILE, TRANSACT | TRANFILE.jcl | COTRN00C, COTRN01C, COBIL00C, CBTRN03C, CBEXPORT | COTRN02C, CBTRN02C, CBACT04C, COBIL00C |
| Card Cross-Ref | XREFFILE, CCXREF, CXACAIX | XREFFILE.jcl | CBACT03C, COACTVWC, COACTUPC, COTRN02C, CBTRN01C, CBTRN02C, CBACT04C, CBEXPORT, CBSTM03B, COPAUA0C | — (read-only in application code) |
| User Security | USRSEC | DUSRSECJ.jcl | COSGN00C, COUSR00C, COUSR02C, COUSR03C | COUSR01C, COUSR02C, COUSR03C |
| Tran Category Balance | TCATBALF | TCATBALF.jcl | CBTRN02C, CBACT04C | CBTRN02C, CBACT04C |
| Disclosure Groups | DISCGRP | DISCGRP.jcl | CBACT04C | — (read-only reference data) |
| Daily Transactions | DALYTRAN | (external input) | CBTRN01C, CBTRN02C | — (input file) |
| Daily Rejects | DALYREJS | DALYREJS.jcl | — | CBTRN02C |
| Transaction Types | TRANTYPE | TRANTYPE.jcl | CBTRN03C, COTRTLIC, COTRTUPC, COBTUPDT | COTRTLIC, COTRTUPC, COBTUPDT |
| Transaction Categories | TRANCATG | TRANCATG.jcl | CBTRN03C | — (reference data) |
| Export File | EXPFILE | CBEXPORT.jcl | CBIMPORT | CBEXPORT |
| Statement Output | STMTFILE, HTMLFILE | CREASTMT.JCL | — | CBSTM03A |
| Report Output | TRANREPT | REPTFILE.jcl | — | CBTRN03C |
| Statement Tran File | TRNXFILE | CREASTMT.JCL | CBSTM03B | — (sorted input from SORT step) |

### Card AIX / Alternate Index Paths

| Base Cluster | AIX Name | Path | Purpose |
|-------------|----------|------|---------|
| CARDDAT | CARDAIX | CARDAIX.PATH | Lookup cards by account ID |
| CCXREF (XREFFILE) | CXACAIX | CXACAIX.PATH | Lookup cross-refs by account ID |

---

## End-to-End Batch Pipeline

```
                    ┌──────────────┐
                    │  DALYTRAN    │  (Daily Transaction Input)
                    └──────┬───────┘
                           │
                    ┌──────▼───────┐
                    │  CBTRN02C    │  POSTTRAN.jcl
                    │  Validate &  │
                    │  Post Trans  │
                    └──┬───┬───┬───┘
                       │   │   │
          ┌────────────┘   │   └────────────┐
          ▼                ▼                 ▼
    ┌──────────┐    ┌──────────┐      ┌──────────┐
    │ TRANSACT │    │ ACCTFILE │      │ DALYREJS │
    │ (posted  │    │ (balance │      │ (rejected│
    │  trans)  │    │  updates)│      │  trans)  │
    └────┬─────┘    └────┬─────┘      └──────────┘
         │               │
         │          ┌────▼─────┐
         │          │ TCATBALF │  (category totals updated)
         │          └────┬─────┘
         │               │
         │    ┌──────────▼──────────┐
         │    │      CBACT04C       │  INTCALC.jcl
         │    │  Interest Calc      │
         │    │  reads TCATBALF +   │
         │    │  DISCGRP + XREFFILE │
         │    └───┬─────────────┬───┘
         │        │             │
         │        ▼             ▼
         │   ┌──────────┐ ┌──────────┐
         │   │ TRANSACT │ │ ACCTFILE │
         │   │(interest │ │(interest │
         │   │ entries) │ │ applied) │
         │   └────┬─────┘ └──────────┘
         │        │
         ├────────┘
         │
    ┌────▼─────────────────────────┐
    │         CBTRN03C             │  TRANREPT.jcl
    │  Transaction Detail Report   │
    │  reads CARDXREF, TRANTYPE,   │
    │  TRANCATG, DATEPARM          │
    └────┬─────────────────────────┘
         │
         ▼
    ┌──────────┐
    │ TRANREPT │  (printed report output)
    └──────────┘

    ┌────────────────────────────────┐
    │       CBSTM03A / CBSTM03B     │  CREASTMT.JCL
    │  Statement Generation          │
    │  reads TRNXFILE, XREFFILE,     │
    │  CUSTFILE, ACCTFILE            │
    └────┬──────────────────┬────────┘
         │                  │
         ▼                  ▼
    ┌──────────┐      ┌──────────┐
    │ STMTFILE │      │ HTMLFILE │
    │(text stmt)│     │(HTML stmt)│
    └──────────┘      └──────────┘

    ┌────────────────────────────────┐
    │          CBEXPORT              │  CBEXPORT.jcl
    │  Export all entity files       │
    │  to multi-record seq file      │
    └────┬───────────────────────────┘
         │
         ▼
    ┌──────────┐
    │  EXPFILE │  (branch migration export)
    └────┬─────┘
         │
    ┌────▼───────────────────────────┐
    │          CBIMPORT              │  CBIMPORT.jcl
    │  Import from export file       │
    │  to normalized target files    │
    └──┬────┬────┬────┬────┬────┬───┘
       │    │    │    │    │    │
       ▼    ▼    ▼    ▼    ▼    ▼
    CUSTOUT ACCTOUT XREFOUT TRNXOUT CARDOUT ERROUT
```

---

## File Processing Lifecycle (Daily Batch Cycle)

```
1. CLOSEFIL.jcl    → Close CICS files (SDSF CEMT SET FILE CLOSE)
2. POSTTRAN.jcl    → CBTRN02C: Validate & post daily transactions
3. INTCALC.jcl     → CBACT04C: Calculate interest on balances
4. TRANREPT.jcl    → CBTRN03C: Generate transaction detail report
5. CREASTMT.JCL    → CBSTM03A: Generate account statements
6. TRANBKP.jcl     → Backup transaction file
7. OPENFIL.jcl     → Reopen CICS files (SDSF CEMT SET FILE OPEN)
```

---

## Technology Stack per Program

| Technology | Programs |
|-----------|----------|
| CICS only | COSGN00C, COMEN01C, COADM01C, COACTUPC, COACTVWC, COTRN00C, COTRN01C, COTRN02C, COCRDLIC, COCRDSLC, COCRDUPC, COBIL00C, CORPT00C, COUSR00C–03C |
| CICS + IMS DL/I | COPAUS0C, COPAUS1C, CBPAUP0C |
| CICS + IMS + MQ | COPAUA0C |
| CICS + DB2 | COTRTLIC, COTRTUPC, COPAUS2C |
| CICS + MQ (VSAM) | COACCT01, CODATE01 |
| Batch (VSAM only) | CBACT01C–04C, CBCUS01C, CBTRN01C–03C, CBSTM03A/B, CBEXPORT, CBIMPORT, COBSWAIT |
| Batch + DB2 | COBTUPDT |
| Batch + IMS DL/I | DBUNLDGS, PAUDBLOD, PAUDBUNL |
| Utility (LE) | CSUTLDTC |
