# DEPENDENCY MAP — CardDemo COBOL Estate

## Overview

This document maps inter-program call/transfer relationships, dataset lineage through JCL jobs, and the end-to-end batch pipeline flow for the CardDemo application.

---

## 1. Program Call Graph

### 1.1 CICS Program Transfer Graph (XCTL / LINK)

The online CICS application uses `EXEC CICS XCTL` for screen-to-screen navigation and `EXEC CICS LINK` for subprogram invocation.

```
                        ┌──────────────────────────────────────────────────────────────────────┐
                        │                     CICS Transaction Flow                            │
                        └──────────────────────────────────────────────────────────────────────┘

                                           COSGN00C
                                         (Sign-on Screen)
                                          /          \
                                   [Admin]            [User]
                                    /                    \
                              COADM01C                COMEN01C
                            (Admin Menu)            (Main Menu)
                           /    |    |   \          /  |  |  |  |  |  |  |  |  |  \
                          /     |    |    \        /   |  |  |  |  |  |  |  |  |   \
                   COUSR00C COUSR01C COUSR02C COUSR03C |  |  |  |  |  |  |  |  |    |
                   (User    (User    (User    (User    |  |  |  |  |  |  |  |  |    |
                    List)    Add)     Update)  Delete)  |  |  |  |  |  |  |  |  |    |
                                                       |  |  |  |  |  |  |  |  |    |
                                              COTRTLIC COTRTUPC |  |  |  |  |  |    |
                                              (Tran Type (Tran   |  |  |  |  |  |    |
                                               List/DB2) Type    |  |  |  |  |  |    |
                                                         CRUD)   |  |  |  |  |  |    |
                                                                 |  |  |  |  |  |    |
                    ┌────────────────────────────────────────────┘  |  |  |  |  |    |
                    |                                               |  |  |  |  |    |
              COACTVWC  COACTUPC  COCRDLIC  COCRDSLC  COCRDUPC     |  |  |  |  |    |
              (Account  (Account  (Card     (Card     (Card        |  |  |  |  |    |
               View)    Update)   List)     View)     Update)      |  |  |  |  |    |
                                                                   |  |  |  |  |    |
                                              COTRN00C  COTRN01C  COTRN02C  |  |    |
                                              (Trans    (Trans    (Trans    |  |    |
                                               List)    View)     Add)     |  |    |
                                                                           |  |    |
                                                                    CORPT00C  |  COPAUS0C
                                                                    (Report   | (Pending
                                                                     Request) |  Auth
                                                                              |  Summary)
                                                                        COBIL00C     |
                                                                        (Bill       COPAUS1C
                                                                         Payment)   (Pending
                                                                                     Auth
                                                                                     Detail)
                                                                                       |
                                                                                   COPAUS2C
                                                                                   (Auth DB2
                                                                                    Writer)
                                                                              [EXEC CICS LINK]
```

### 1.2 Batch Program CALL Dependencies

| Calling Program | Called Program/Service | Call Mechanism | Purpose |
|----------------|----------------------|----------------|---------|
| CBSTM03A | CBSTM03B | CALL 'CBSTM03B' | File I/O subprogram for statement generation |
| CBACT01C | CODATECN (via COPY) | Inline copybook | Date conversion for account output |
| CBTRN03C | CEE3ABD | CALL 'CEE3ABD' | LE abend handler |
| CORPT00C | CSUTLDTC | CALL 'CSUTLDTC' | Date validation utility |
| COTRN02C | CSUTLDTC | CALL 'CSUTLDTC' | Date validation utility |
| CSUTLDTC | CEEDAYS | CALL 'CEEDAYS' | LE callable service for date arithmetic |
| COBSWAIT | MVSWAIT | CALL 'MVSWAIT' | Assembler wait routine |
| COPAUA0C | MQOPEN, MQGET, MQPUT1, MQCLOSE | CALL 'MQxxxx' | MQ API calls for authorization |
| COACCT01 | MQOPEN, MQGET, MQPUT, MQCLOSE | CALL 'MQxxxx' | MQ API calls for account inquiry |
| CODATE01 | MQOPEN, MQGET, MQPUT, MQCLOSE | CALL 'MQxxxx' | MQ API calls for date service |
| DBUNLDGS | CBLTDLI | CALL 'CBLTDLI' | IMS DL/I calls for DB unload |
| PAUDBLOD | CBLTDLI | CALL 'CBLTDLI' | IMS DL/I calls for DB load |
| PAUDBUNL | CBLTDLI | CALL 'CBLTDLI' | IMS DL/I calls for DB unload |
| CBPAUP0C | CBLTDLI | CALL 'CBLTDLI' | IMS DL/I calls for purge |
| COTRTLIC | DSNTIAC (via CSDB2RPY) | CALL DSNTIAC | DB2 error message formatting |
| COTRTUPC | DSNTIAC (via CSDB2RPY) | CALL DSNTIAC | DB2 error message formatting |
| COBTUPDT | (Inline SQL) | EXEC SQL | DB2 CRUD on TRANSACTION_TYPE |

### 1.3 Copybook Dependency Matrix (Top 10 Most-Referenced)

| Copybook | # Programs | Used By |
|----------|-----------|---------|
| COCOM01Y | 18 | All CICS online programs (navigation/COMMAREA) |
| DFHAID | 16 | All CICS programs (AID byte definitions) |
| DFHBMSCA | 16 | All CICS programs (BMS attribute constants) |
| COTTL01Y | 16 | All CICS programs (screen titles) |
| CSDAT01Y | 16 | All CICS programs (date/time fields) |
| CSMSG01Y | 16 | All CICS programs (common messages) |
| CSUSR01Y | 11 | All user mgmt + auth programs |
| CVACT03Y | 10 | Account-related batch + online programs |
| CVACT01Y | 9 | Account processing programs |
| CVCRD01Y | 8 | Card-related CICS programs |

---

## 2. Dataset Lineage — JCL Job to File Mapping

### 2.1 Master VSAM Files

| Dataset (VSAM KSDS) | DSN Pattern | Defined By JCL | Loaded By JCL | Read By Programs | Written/Updated By Programs |
|---------------------|-------------|----------------|---------------|-----------------|---------------------------|
| **Account Master** | AWS.M2.CARDDEMO.ACCTDATA.PS → ACCTDATA.VSAM.KSDS | ACCTFILE.jcl | ACCTFILE.jcl (REPRO) | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBSTM03A/B, CBEXPORT, COACTUPC, COACTVWC, COPAUA0C, COBIL00C, COTRN02C, COACCT01 | CBACT04C (I-O update), COACTUPC (CICS REWRITE) |
| **Card Master** | AWS.M2.CARDDEMO.CARDDATA.PS → CARDDATA.VSAM.KSDS | CARDFILE.jcl | CARDFILE.jcl (REPRO) | CBACT02C, CBTRN01C, CBSTM03A/B, CBEXPORT, COCRDLIC, COCRDSLC, COCRDUPC | COCRDUPC (CICS REWRITE) |
| **Customer Master** | AWS.M2.CARDDEMO.CUSTDATA.PS → CUSTDATA.VSAM.KSDS | CUSTFILE.jcl | CUSTFILE.jcl (REPRO) | CBCUS01C, CBTRN01C, CBSTM03A/B, CBEXPORT, COACTUPC, COACTVWC, COCRDSLC, COPAUA0C | COACTUPC (CICS REWRITE) |
| **Card Cross-Reference** | AWS.M2.CARDDEMO.CARDXREF.PS → CARDXREF.VSAM.KSDS | XREFFILE.jcl | XREFFILE.jcl (REPRO) | CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A/B, CBEXPORT, COACTVWC, COPAUA0C, COBIL00C, COTRN02C | (read-only in current code) |
| **Transaction Master** | AWS.M2.CARDDEMO.TRANSACT.PS → TRANSACT.VSAM.KSDS | TRANFILE.jcl | TRANFILE.jcl (REPRO) | CBTRN03C, CBEXPORT, COTRN00C, COTRN01C | CBTRN02C (WRITE), COTRN02C (CICS WRITE), COBIL00C (CICS WRITE) |
| **Transaction Category Balance** | AWS.M2.CARDDEMO.TCATBAL.PS → TCATBALF.VSAM.KSDS | TCATBALF.jcl | TCATBALF.jcl (REPRO) | CBACT04C | CBACT04C (I-O update), CBTRN02C (I-O update) |
| **Transaction Type** | AWS.M2.CARDDEMO.TRANTYPE.PS → TRANTYPE.VSAM.KSDS | TRANTYPE.jcl | TRANTYPE.jcl (REPRO) | CBTRN03C | (read-only; DB2 version managed separately) |
| **Transaction Category** | AWS.M2.CARDDEMO.TRANCATG.PS → TRANCATG.VSAM.KSDS | TRANCATG.jcl | TRANCATG.jcl (REPRO) | CBTRN03C | (read-only) |
| **Disclosure Group** | AWS.M2.CARDDEMO.DISCGRP.PS → DISCGRP.VSAM.KSDS | DISCGRP.jcl | DISCGRP.jcl (REPRO) | CBACT04C | (read-only) |
| **User Security** | AWS.M2.CARDDEMO.USRSEC.PS → USRSEC.VSAM.KSDS | DUSRSECJ.jcl | DUSRSECJ.jcl (REPRO) | COSGN00C, COUSR00C | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) |

### 2.2 Transient / Intermediate Files

| Dataset | Type | Produced By | Consumed By | Purpose |
|---------|------|-------------|-------------|---------|
| Daily Transaction Feed | PS (seq) | External feed | CBTRN01C, CBTRN02C (via DALYTRAN DD) | Daily card transaction input |
| Daily Rejects | GDG PS | CBTRN02C | Manual review / reconciliation | Rejected transactions |
| Transaction Report | GDG PS | CBTRN03C (via TRANREPT DD) | End users / print | Daily transaction report |
| Statement File | GDG PS | CBSTM03A (via STMTFILE DD) | TXT2PDF1.JCL (PDF conversion) | Credit card statements (text) |
| Statement HTML | PS | CBSTM03A (via HTMLFILE DD) | End users | Credit card statements (HTML) |
| Export File | PS | CBEXPORT | CBIMPORT | Branch migration data |
| Sorted Transactions | PS (temp) | COMBTRAN (SORT) | COMBTRAN (REPRO to VSAM) | Sorted daily transactions |
| TRNXFILE (statement) | VSAM KSDS | CREASTMT (REPRO) | CBSTM03A | Sorted transactions by card+ID |
| IMS GSAM Output | GSAM | DBUNLDGS | External / migration | IMS authorization DB unload |
| IMS Flat Files | PS | PAUDBUNL | PAUDBLOD | IMS authorization DB backup/restore |

### 2.3 DB2 Tables (Transaction Type Sub-App)

| Table | Schema | Managed By JCL | Accessed By Programs |
|-------|--------|----------------|---------------------|
| CARDDEMO.TRANSACTION_TYPE | CARDDEMO | CREADB21.jcl, MNTTRDB2.jcl, TRANEXTR.jcl | COTRTLIC, COTRTUPC, COBTUPDT |
| CARDDEMO.TRANSACTION_TYPE_CATEGORY | CARDDEMO | CREADB21.jcl, TRANEXTR.jcl | COTRTLIC, COTRTUPC |
| CARDDEMO.AUTHFRDS | CARDDEMO | (inline DDL) | COPAUS2C |
| SYSIBM.SYSDUMMY1 | System | N/A | COTRTLIC, COTRTUPC (priming query via CSDB2RPY) |

### 2.4 MQ Queues

| Queue | Used By | Direction | Purpose |
|-------|---------|-----------|---------|
| Authorization Request Queue | COPAUA0C | GET | Receive incoming authorization requests |
| Authorization Response Queue | COPAUA0C | PUT | Send authorization responses |
| Authorization Error Queue | COPAUA0C | PUT | Send error notifications |
| Account Request Queue | COACCT01 | GET | Receive account inquiry requests |
| Account Response Queue | COACCT01 | PUT | Send account data responses |
| Account Error Queue | COACCT01 | PUT | Send error responses |
| Date Request Queue | CODATE01 | GET | Receive date/time requests |
| Date Response Queue | CODATE01 | PUT | Send date/time responses |

---

## 3. End-to-End Batch Pipeline Flow

The CardDemo batch processing follows a daily cycle with the following pipeline stages:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH PIPELINE FLOW                            │
└─────────────────────────────────────────────────────────────────────────┘

  Phase 1: ENVIRONMENT PREPARATION
  ─────────────────────────────────
  ┌──────────────┐
  │ CLOSEFIL.jcl │──→ Close all CICS files (SDSF operator commands)
  └──────────────┘

  Phase 2: DAILY TRANSACTION INTAKE
  ──────────────────────────────────
  ┌──────────────────┐     ┌──────────────────┐
  │ External Daily    │────→│ CBTRN01C         │──→ Validated daily transactions
  │ Transaction Feed  │     │ (Transaction     │    (ready for posting)
  │ (DALYTRAN)        │     │  Validation)     │
  └──────────────────┘     └──────────────────┘
         Reads: DALYTRAN, XREFFILE, CUSTFILE, CARDFILE, ACCTFILE, TRANFILE

  Phase 3: TRANSACTION POSTING
  ────────────────────────────
  ┌──────────────────┐     ┌──────────────────┐
  │ POSTTRAN.jcl     │────→│ CBTRN02C         │──→ Updated: ACCTFILE, TCATBALF
  │                  │     │ (Post Daily      │    Written: TRANFILE, DALYREJS
  └──────────────────┘     │  Transactions)   │
                           └──────────────────┘
         Updates account balances, category balances
         Writes validated transactions to master
         Rejects invalid transactions to DALYREJS (GDG)

  Phase 4: COMBINE & SORT TRANSACTIONS
  ─────────────────────────────────────
  ┌──────────────────┐
  │ COMBTRAN.jcl     │──→ SORT daily transactions → REPRO into transaction VSAM
  └──────────────────┘

  Phase 5: INTEREST CALCULATION
  ─────────────────────────────
  ┌──────────────────┐     ┌──────────────────┐
  │ INTCALC.jcl      │────→│ CBACT04C         │──→ Updated: ACCTFILE (balances)
  │                  │     │ (Interest        │    Written: TRANSACT (interest txns)
  └──────────────────┘     │  Calculation)    │
                           └──────────────────┘
         Reads: TCATBALF, XREFFILE, DISCGRP
         Calculates interest per category using disclosure group rates

  Phase 6: REPORTING
  ──────────────────
  ┌──────────────────┐     ┌──────────────────┐
  │ TRANREPT.jcl     │────→│ SORT → CBTRN03C  │──→ Transaction Report (GDG PS)
  └──────────────────┘     │ (Transaction     │
                           │  Report)         │
                           └──────────────────┘
         Reads: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM

  Phase 7: STATEMENT GENERATION
  ─────────────────────────────
  ┌──────────────────┐     ┌──────────────────┐
  │ CREASTMT.JCL     │────→│ IDCAMS → SORT →  │──→ Statement File (text + HTML)
  │                  │     │ IDCAMS → CBSTM03A│
  └──────────────────┘     └──────────────────┘
         CBSTM03A calls CBSTM03B for file I/O
         Reads: TRNXFILE (sorted), XREFFILE, CUSTFILE, ACCTFILE
         Writes: STMTFILE, HTMLFILE

  ┌──────────────────┐
  │ TXT2PDF1.JCL     │──→ Convert text statements to PDF
  └──────────────────┘

  Phase 8: BACKUP & MAINTENANCE
  ─────────────────────────────
  ┌──────────────────┐
  │ TRANBKP.jcl      │──→ Backup transaction VSAM to GDG, redefine
  └──────────────────┘

  ┌──────────────────┐
  │ TRANEXTR.jcl     │──→ Backup DB2 transaction type/category data
  └──────────────────┘

  Phase 9: ENVIRONMENT RESTORATION
  ─────────────────────────────────
  ┌──────────────────┐
  │ OPENFIL.jcl      │──→ Reopen all CICS files
  └──────────────────┘
```

### 3.1 Batch Pipeline Data Flow Summary

```
Daily Feed ──→ CBTRN01C ──→ CBTRN02C ──→ CBACT04C ──→ CBTRN03C ──→ CBSTM03A
(validate)     (post)       (interest)    (report)     (statements)
    │              │             │             │              │
    ▼              ▼             ▼             ▼              ▼
 [DALYTRAN]   [TRANFILE]    [ACCTFILE]   [TRANREPT]    [STMTFILE]
              [ACCTFILE]    [TRANSACT]                  [HTMLFILE]
              [TCATBALF]
              [DALYREJS]
```

---

## 4. IMS Authorization Sub-Pipeline

```
  Online Authorization Flow:
  ─────────────────────────
  External System ──MQ──→ COPAUA0C ──→ CICS Read (XREF, ACCT, CUST)
                                   ──→ Validate & Authorize
                                   ──→ MQ Response
                                   ──→ COPAUS2C (DB2 fraud record)

  IMS DB Maintenance:
  ───────────────────
  LOADPADB.JCL → PAUDBLOD ──→ Load flat files into IMS DB
  UNLDPADB.JCL → PAUDBUNL ──→ Unload IMS DB to flat files
  UNLDGSAM.JCL → DBUNLDGS ──→ Unload IMS DB to GSAM
  CBPAUP0J.jcl → CBPAUP0C ──→ Purge expired authorizations

  Online Inquiry:
  ───────────────
  COPAUS0C (Summary List) → COPAUS1C (Detail) → COPAUS2C (DB2 Writer)
```

---

## 5. Branch Migration Pipeline

```
  Export:
  ──────
  CBEXPORT.jcl → CBEXPORT ──→ Reads all master VSAM files
                           ──→ Writes unified EXPFILE (500-byte records)
                               Record types: C=Customer, A=Account,
                               T=Transaction, X=Card Xref, D=Card

  Import:
  ──────
  CBIMPORT.jcl → CBIMPORT ──→ Reads EXPFILE
                           ──→ Splits into: CUSTOUT, ACCTOUT, XREFOUT,
                               TRNXOUT, CARDOUT, ERROUT
```

---

## 6. VSAM Cluster Initialization Dependencies

Several JCL jobs must run in a specific order during initial environment setup:

```
  1. DEFGDGB.jcl    ──→ Define GDG bases (statements, reports, rejects)
  2. DEFGDGD.jcl    ──→ Define DB2-related GDG bases
  3. DALYREJS.jcl   ──→ Define daily reject GDG base
  4. REPTFILE.jcl   ──→ Define report GDG base
  5. ACCTFILE.jcl   ──→ Define & load Account VSAM
  6. CARDFILE.jcl   ──→ Define & load Card VSAM + AIX
  7. CUSTFILE.jcl   ──→ Define & load Customer VSAM
  8. XREFFILE.jcl   ──→ Define & load Cross-Ref VSAM + AIX
  9. TRANFILE.jcl   ──→ Define & load Transaction VSAM + AIX
  10. TRANTYPE.jcl   ──→ Define & load Transaction Type VSAM
  11. TRANCATG.jcl   ──→ Define & load Transaction Category VSAM
  12. TCATBALF.jcl   ──→ Define & load Category Balance VSAM
  13. DISCGRP.jcl    ──→ Define & load Disclosure Group VSAM
  14. DUSRSECJ.jcl   ──→ Define & load User Security VSAM
  15. CREADB21.jcl   ──→ Create DB2 tables and load data
  16. CBADMCDJ.jcl   ──→ Define CICS resources (CSD)
  17. OPENFIL.jcl    ──→ Open all files in CICS
```
