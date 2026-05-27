# Dependency Map — CardDemo COBOL Estate

## Overview

This document maps inter-program calls, dataset lineage, and end-to-end batch pipeline flows across the CardDemo application.

---

## 1. Inter-Program Call Graph

### 1.1 Batch Program Calls

```
CBSTM03A ──CALL──► CBSTM03B      (Statement generation → page formatting)
CBACT01C ──CALL──► COBDATFT       (Account read → date format conversion)
CORPT00C ──CALL──► CSUTLDTC       (Report selection → date validation)
COTRN02C ──CALL──► CSUTLDTC       (Transaction add → date validation)
COBSWAIT ──CALL──► MVSWAIT        (Wait utility → assembler timer routine)
```

**Abend handler calls (CEE3ABD):**
```
CBACT01C ──CALL──► CEE3ABD
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

### 1.2 CICS Online Program Transfers (XCTL)

All CICS programs communicate via COMMAREA (CARDDEMO-COMMAREA) and transfer control using `EXEC CICS XCTL`.

```
COSGN00C (Sign-on)
  ├──► COADM01C    (Admin login → Admin menu)
  └──► COMEN01C    (Regular login → Main menu)

COADM01C (Admin Menu)
  ├──► COUSR00C    (User List)
  ├──► COUSR01C    (User Add)
  ├──► COUSR02C    (User Update)
  ├──► COUSR03C    (User Delete)
  ├──► COTRTLIC    (Transaction Type List)
  ├──► COTRTUPC    (Transaction Type Update)
  └──► COSGN00C    (Back to sign-on)

COMEN01C (Main Menu) ──► routes to 12 options:
  ├──► COACTVWC    (1. Account View)
  ├──► COACTUPC    (2. Account Update)
  ├──► COCRDLIC    (3. Credit Card List)
  ├──► COCRDUPC    (4. Credit Card Update)
  ├──► COTRN00C    (5. Transaction List)
  ├──► COTRN01C    (6. Transaction View)
  ├──► COTRN02C    (7. Transaction Add)
  ├──► CORPT00C    (8. Reports)
  ├──► COBIL00C    (9. Bill Payment)
  └──► COPAUS0C    (10. Pending Auth View)

COCRDLIC (Card List)
  ├──► COCRDUPC    (Card Update — on selection)
  ├──► COCRDSLC    (Card View — on detail)
  └──► COMEN01C    (Back to menu)

COCRDSLC (Card View)
  └──► COMEN01C / COCRDLIC   (Back to menu/list)

COACTVWC (Account View)
  └──► COMEN01C    (Back to menu)

COACTUPC (Account Update)
  └──► COMEN01C    (Back to menu)

COCRDUPC (Card Update)
  └──► COMEN01C    (Back to menu)

COBIL00C (Bill Payment)
  └──► COMEN01C    (Back to menu)

COPAUS0C (Pending Auth Summary)
  └──► COPAUS1C    (Auth Detail)

COPAUS1C (Auth Detail)
  └──► COPAUS0C    (Back to summary)
```

### 1.3 Authorization Sub-Application Calls

```
COPAUA0C (MQ Async Processor)
  ├── MQ: MQGET from request queue
  ├── VSAM: EXEC CICS READ ACCTDAT, CARDDAT, CARDAIX
  └── MQ: MQPUT1 to response queue

CBPAUP0C (Batch Purge)
  └── IMS DL/I: GN, GNP, DLET on PAUTBDB

DBUNLDGS (GSAM Unload)
  └── IMS DL/I: GN, GNP → GSAM output

PAUDBLOD (DB Load)
  └── IMS DL/I: GU, ISRT ← sequential input

PAUDBUNL (DB Unload)
  └── IMS DL/I: GN, GNP → sequential output
```

---

## 2. Dataset Lineage — VSAM Files

### 2.1 Account File (ACCTDAT / ACCTFILE)

**Dataset:** `AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS`
**Record:** 300 bytes, Key = ACCT-ID (11 bytes, offset 0)

```
Creators / Loaders:
  ACCTFILE.jcl ──REPRO──► ACCTDAT (from AWS.M2.CARDDEMO.ACCTDATA.PS)
  CBIMPORT     ──WRITE──► ACCTDAT

Readers:
  CBACT01C ── sequential read
  CBACT04C ── random read/update (interest calculation)
  CBTRN01C ── random read/update (post transactions)
  CBTRN02C ── random read/update (validate transactions)
  CBEXPORT ── sequential read
  CBSTM03A ── sequential read (statement generation)

Online Access (CICS):
  COACTUPC ── READ/UPDATE (account update)
  COACTVWC ── READ (account view)
  COCRDLIC ── READ (card list — account lookup)
  COCRDSLC ── READ (card view — account lookup)
  COCRDUPC ── READ (card update — account lookup)
  COBIL00C ── READ/UPDATE (bill payment)
  COTRN02C ── READ (transaction add — account lookup)
  COPAUA0C ── READ (authorization — account verification)
  COACCT01 ── READ (MQ account inquiry)
```

### 2.2 Card File (CARDDAT / CARDFILE)

**Dataset:** `AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS`
**Record:** 150 bytes, Key = CARD-NUM (16 bytes, offset 0)
**AIX:** `AWS.M2.CARDDEMO.CARDDATA.VSAM.AIX` (on ACCT-ID, offset 16, 11 bytes)

```
Creators / Loaders:
  CARDFILE.jcl ──REPRO──► CARDDAT (from AWS.M2.CARDDEMO.CARDDATA.PS)
  CBIMPORT     ──WRITE──► CARDDAT

Readers:
  CBACT02C ── sequential read
  CBEXPORT ── sequential read
  CBTRN01C ── random read (card lookup for posting)
  CBTRN02C ── random read (card validation)

Online Access (CICS):
  COCRDLIC ── BROWSE/READ (card list, via AIX for account lookup)
  COCRDSLC ── READ (card detail)
  COCRDUPC ── READ/UPDATE (card update)
  COTRN02C ── READ (transaction add — card lookup)
  COPAUA0C ── READ (authorization — card verification)
```

### 2.3 Customer File (CUSTFILE)

**Dataset:** `AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS`
**Record:** 500 bytes, Key = CUST-ID (9 bytes, offset 0)

```
Creators / Loaders:
  CUSTFILE.jcl ──REPRO──► CUSTFILE (from AWS.M2.CARDDEMO.CUSTDATA.PS)
  CBIMPORT     ──WRITE──► CUSTFILE

Readers:
  CBCUS01C ── sequential read
  CBEXPORT ── sequential read
  CBTRN01C ── random read (customer lookup)
  CBSTM03A ── sequential read (statement generation)
```

### 2.4 Card Cross-Reference File (XREFFILE / CARDXREF)

**Dataset:** `AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS`
**Record:** 50 bytes, Key = XREF-CARD-NUM (16 bytes, offset 0)

```
Creators / Loaders:
  XREFFILE.jcl ──REPRO──► XREFFILE (from AWS.M2.CARDDEMO.CARDXREF.PS)
  CBIMPORT     ──WRITE──► XREFFILE

Readers:
  CBACT03C ── sequential read
  CBACT04C ── sequential read (interest calc — iterate cards)
  CBEXPORT ── sequential read
  CBTRN01C ── random read (card→account lookup)
  CBTRN02C ── random read (card→account lookup)
  CBTRN03C ── sequential read (report generation)
  CBSTM03A ── sequential read (statement generation)
```

### 2.5 Transaction File (TRANSACT / TRANFILE)

**Dataset:** `AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS`
**Record:** 350 bytes, Key = TRAN-ID (16 bytes, offset 0)

```
Creators / Loaders:
  TRANFILE.jcl ──REPRO──► TRANFILE (from AWS.M2.CARDDEMO.TRANSACT.PS)
  CBTRN01C     ──WRITE──► TRANFILE (post transactions)
  CBTRN02C     ──WRITE──► TRANFILE (validated transactions)
  CBACT04C     ──WRITE──► TRANFILE (interest transactions)
  CBIMPORT     ──WRITE──► TRANFILE

Readers:
  CBACT04C ── sequential read (interest calculation)
  CBTRN02C ── read for validation
  CBTRN03C ── sequential read (report generation)
  CBEXPORT ── sequential read

Online Access (CICS):
  COTRN00C ── BROWSE (transaction list)
  COTRN01C ── READ (transaction detail)
  COTRN02C ── WRITE (new transaction)
  COBIL00C ── WRITE (bill payment transactions)

Backup:
  TRANBKP.jcl ──REPRO──► Sequential backup (GDG)
```

### 2.6 Daily Transaction Input (DALYTRAN)

**Dataset:** `AWS.M2.CARDDEMO.DALYTRAN.PS` (sequential)
**Record:** 350 bytes

```
Producer: External feed (card network POS transactions)

Consumers:
  CBTRN01C ── sequential read (post to TRANFILE)
  CBTRN02C ── sequential read (validate, rejects → DALYREJS)
```

### 2.7 User Security File (USRSEC)

**Dataset:** `AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS`
**Record:** 80 bytes, Key = SEC-USR-ID (8 bytes, offset 0)

```
Creator:
  DUSRSECJ.jcl ──REPRO──► USRSEC

Online Access (CICS):
  COSGN00C ── READ (authenticate)
  COUSR00C ── BROWSE (user list)
  COUSR01C ── WRITE (add user)
  COUSR02C ── READ/UPDATE (update user)
  COUSR03C ── READ/DELETE (delete user)
```

### 2.8 Reference / Configuration Files

| Dataset | VSAM Type | JCL Loader | Programs |
|---------|-----------|-----------|----------|
| Transaction Category Balance (`TCATBALF`) | KSDS (50 bytes) | TCATBALF.jcl | CBACT04C (read/update) |
| Transaction Type (`TRANTYPE`) | KSDS (60 bytes) | TRANTYPE.jcl | CBTRN03C (read) |
| Transaction Category (`TRANCATG`) | KSDS (60 bytes) | TRANCATG.jcl | CBTRN03C (read) |
| Disclosure Group (`DISCGRP`) | KSDS (50 bytes) | DISCGRP.jcl | CBACT04C (read — interest rates) |
| Report Output (`REPTFILE`) | ESDS | REPTFILE.jcl | CBTRN03C (write) |
| Daily Rejects (`DALYREJS`) | ESDS | DALYREJS.jcl | CBTRN02C (write) |
| Statement Output (`STMTFILE`) | Sequential | — | CBSTM03A (write), CBSTM03B (write) |

---

## 3. DB2 Table Access (Transaction Type Sub-App)

```
DB2 Tables:
  CARDDEMO.TRANSACTION_TYPE  (TR_TYPE PK)
  CARDDEMO.TRANSACTION_CATEGORY  (TC_TYPE, TC_CATEGORY PK)

Writers:
  COBTUPDT ── INSERT/UPDATE (batch load from file)
  COTRTUPC ── INSERT/UPDATE/DELETE (online CRUD)

Readers:
  COTRTLIC ── SELECT with cursor (paginated list)
  COTRTUPC ── SELECT (detail view)
  COPAUS0C ── SELECT from AUTHFRDS view (authorization fraud analysis)
  COPAUS1C ── SELECT from AUTHFRDS view
```

---

## 4. IMS Database Access (Authorization Sub-App)

```
IMS Database: PAUTBDB (Pending Authorization)
  Segments:
    CIPAUSMY (Summary — root segment, key = PA-ACCT-ID)
    CIPAUDTY (Detail — child segment, key = PA-AUTH-DATE + PA-AUTH-TIME)

Access Programs:
  CBPAUP0C ── GN, GNP, DLET (purge expired)
  DBUNLDGS ── GN, GNP (full unload to GSAM)
  PAUDBLOD ── GU, ISRT (reload from sequential)
  PAUDBUNL ── GN, GNP (unload to sequential)
```

---

## 5. MQ Queue Access

```
Authorization Queue (COPAUA0C):
  MQGET ◄── Request Queue (authorization requests from card network)
  MQPUT1 ──► Response Queue (approval/decline responses)

Account Inquiry Queue (COACCT01):
  MQGET ◄── Inquiry Request Queue
  MQPUT ──► Inquiry Response Queue

Date Validation Queue (CODATE01):
  MQGET ◄── Date Validation Request Queue
  MQPUT ──► Date Validation Response Queue
```

---

## 6. End-to-End Batch Pipeline Flows

### 6.1 Daily Transaction Processing Pipeline

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  CLOSEFIL   │────►│   TRANBKP   │────►│  WAITSTEP   │────►┌─────────────┐
│ Close CICS  │     │ Backup VSAM │     │  Timer wait │     │  OPENFIL    │
│   files     │     │  to GDG     │     │             │     │ Reopen CICS │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘

Orchestrated by: Control-M DAILY-TransactionBackup folder (runs ALL days)
```

**Detailed data flow:**
```
External Card Network
       │
       ▼
  DALYTRAN.PS (daily transaction file)
       │
       ├──► CBTRN02C (COMBTRAN.jcl) ──► DALYREJS (rejects)
       │         │
       │         ▼
       │    TRANFILE (validated transactions)
       │
       └──► CBTRN01C (POSTTRAN.jcl) ──► TRANFILE (posted)
                 │                          │
                 ▼                          ▼
            ACCTFILE (balance updates)  TRANFILE (committed)
```

### 6.2 Monthly Interest Calculation Pipeline

```
┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐     ┌─────────────┐
│  CLOSEFIL   │────►│   INTCALC   │────►│  COMBTRAN   │────►│  WAITSTEP   │────►│  OPENFIL    │
│ Close CICS  │     │ CBACT04C    │     │ CBTRN02C    │     │  Timer wait │     │ Reopen CICS │
│   files     │     │ Calc interest│    │ Validate    │     │             │     │    files    │
└─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘     └─────────────┘

Orchestrated by: Control-M MONTHLY-InterestCalculation folder
```

**Detailed data flow:**
```
XREFFILE ──►┐
DISCGRP  ──►├──► CBACT04C (INTCALC) ──► TRANFILE (interest transactions)
TRANFILE ──►│                      ──► ACCTFILE (updated balances)
ACCTFILE ──►┘                      ──► TCATBALF (category balance updates)
                    │
                    ▼
              CBTRN02C (COMBTRAN) ──► TRANFILE (validated)
                                 ──► DALYREJS (rejects)
```

### 6.3 Weekly Transaction Type Refresh Pipeline

```
┌─────────────┐
│  MNTTRDB2   │──────────────────────────────────┐
│ COBTUPDT    │                                  │
│ Update DB2  │                                  │
└─────────────┘                                  │
       │                                         │
       ├──► SMART_FOLDER: DisclosureGroupsRefresh│
       │    CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL
       │                                         │
       └──► SMART_FOLDER: TransactionTypesDBRefresh
            TRANEXTR (extract types from DB2)

Orchestrated by: Control-M WEEKLY-TransactionTypesDBRefresh folder (runs Saturdays)
```

### 6.4 Statement Generation Pipeline (Ad-hoc)

```
CUSTFILE  ──►┐
ACCTFILE  ──►├──► CBSTM03A ──CALL──► CBSTM03B ──► STMTFILE (statements)
XREFFILE  ──►┘

Triggered by: CREASTMT.JCL
```

### 6.5 Report Generation Pipeline (Ad-hoc)

```
TRANFILE  ──►┐
XREFFILE  ──►├──► CBTRN03C ──► REPTFILE (transaction reports)
TRANTYPE  ──►│
TRANCATG  ──►┘

Triggered by: TRANREPT.jcl (via CORPT00C online submission)
```

### 6.6 Data Export/Import Pipeline (Ad-hoc)

```
Export:
  CUSTFILE  ──►┐
  ACCTFILE  ──►│
  XREFFILE  ──►├──► CBEXPORT ──► EXPORT-FILE (sequential, 500-byte records)
  TRANFILE  ──►│
  CARDFILE  ──►┘

Import:
  EXPORT-FILE ──► CBIMPORT ──►┌─► CUSTFILE
                              ├─► ACCTFILE
                              ├─► XREFFILE
                              ├─► TRANFILE
                              └─► CARDFILE

Triggered by: CBEXPORT.jcl / CBIMPORT.jcl
```

### 6.7 IMS Authorization Maintenance Pipeline

```
Online (continuous):
  MQ Request Queue ──► COPAUA0C ──► MQ Response Queue
                           │
                           ▼
                       IMS PAUTBDB (via CICS DL/I)

Batch maintenance:
  CBPAUP0C (purge expired) ──► IMS PAUTBDB
  PAUDBUNL (unload) ──► Sequential file
  PAUDBLOD (reload) ◄── Sequential file
  DBUNLDGS (GSAM unload) ──► GSAM output

Triggered by: CBPAUP0J.jcl, UNLDPADB.JCL, LOADPADB.JCL, UNLDGSAM.JCL
```

### 6.8 JCL Job Frequency Classification

The following table classifies every JCL job by execution frequency, based on evidence from `app/scheduler/CardDemo.controlm` (Control-M definitions) and `app/scheduler/CardDemo.ca7` (CA-7 trigger chains).

| JCL Job | Frequency | Pipeline | Evidence | Purpose |
|---------|-----------|----------|----------|---------|
| **CLOSEFIL.jcl** | Daily | DAILY-TransactionBackup | Control-M: `DAYS="ALL"`, Description: "DAILY CLOSEFIL" | Close CICS files for batch window |
| **TRANBKP.jcl** | Daily | DAILY-TransactionBackup | Control-M: `DAYS="ALL"`, chained after CLOSEFIL | Backup VSAM transaction file to GDG |
| **WAITSTEP.jcl** | Daily | DAILY-TransactionBackup | Control-M: `DAYS="ALL"`, chained after TRANBKP | Timer wait between batch steps |
| **OPENFIL.jcl** | Daily | DAILY-TransactionBackup | Control-M: `DAYS="ALL"`, terminal job in chain | Reopen CICS files after batch window |
| **POSTTRAN.jcl** | Daily | Daily Transaction Processing | CA-7: triggered by CBPAUP0J (SCHID=030) | Post validated transactions to VSAM |
| **CBPAUP0J.jcl** | Daily | Daily Transaction Processing | CA-7: triggered by CLOSEFIL (SCHID=030) | Purge expired pending authorizations |
| **COMBTRAN.jcl** | Monthly | MONTHLY-InterestCalculation | Control-M: Monthly folder, chained after INTCALC | Combine & validate interest transactions |
| **MNTTRDB2** (inline) | Weekly | WEEKLY-TransactionTypesDBRefresh | Control-M: folder runs Saturdays (`DAYS="SA"`) | Update transaction types in DB2 |
| **DISCGRP.jcl** | Weekly | WEEKLY-DisclosureGroupsRefresh | Control-M: `DAYS="SA"`, chained after MNTTRDB2→CLOSEFIL | Refresh disclosure groups from DB2 |
| **TRANEXTR** (inline) | Weekly | WEEKLY-TransactionTypesDBRefresh | Control-M: `DAYS="SA"`, chained after MNTTRDB2 | Extract transaction types from DB2 to VSAM |
| **TRANTYPE.jcl** | Weekly | Weekly Ref Data Refresh | CA-7: triggered by CLOSEFIL in weekly chain | Load transaction type reference data |
| **TRANCATG.jcl** | Weekly | Weekly Ref Data Refresh | CA-7: triggered by CLOSEFIL1 (SCHID=031) | Load transaction category data |
| **TCATBALF.jcl** | Weekly | Weekly Ref Data Refresh | CA-7: triggered by CLOSEFIL2 (SCHID=032) | Load transaction category balance file |
| **INTCALC.jcl** | Monthly | MONTHLY-InterestCalculation | Control-M: folder "MONTHLY-InterestCalculation" | Calculate monthly interest charges |
| **CREASTMT.JCL** | Monthly | Statement Generation | CA-7: triggered by CLOSEFIL→CREASTMT chain | Generate monthly customer statements |
| **TXT2PDF1.JCL** | Monthly | Statement Generation | CA-7: triggered by CREASTMT (SCHID=030) | Convert statement text to PDF |
| **PRTCATBL.jcl** | Monthly | Monthly Reporting | CA-7: triggered after TXT2PDF1 chain | Print category balance report |
| **READACCT.jcl** | Monthly | Monthly Validation | CA-7: triggered after monthly CLOSEFIL chain | Sequential read/audit of account file |
| **READCARD.jcl** | Monthly | Monthly Validation | CA-7: chained after READACCT | Sequential read/audit of card file |
| **READCUST.jcl** | Monthly | Monthly Validation | CA-7: chained after READCARD | Sequential read/audit of customer file |
| **READXREF.jcl** | Monthly | Monthly Validation | CA-7: chained after READCUST | Sequential read/audit of cross-reference file |
| **TRANREPT.jcl** | On-Demand | Report Generation | No scheduler entry; triggered via CORPT00C online | Generate transaction reports |
| **CBEXPORT.jcl** | On-Demand | Data Export | No scheduler entry; manual/migration trigger | Export all VSAM files to sequential |
| **CBIMPORT.jcl** | On-Demand | Data Import | No scheduler entry; manual/migration trigger | Import sequential file to VSAM |
| **ACCTFILE.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE/REPRO | Define and load account VSAM cluster |
| **CARDFILE.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE/REPRO | Define and load card VSAM cluster |
| **CUSTFILE.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE/REPRO | Define and load customer VSAM cluster |
| **TRANFILE.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE/REPRO | Define and load transaction VSAM cluster |
| **XREFFILE.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE/REPRO | Define and load cross-reference VSAM cluster |
| **DALYREJS.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE | Define daily rejects VSAM cluster |
| **REPTFILE.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE | Define report output file |
| **TRANIDX.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE AIX | Define transaction alternate index |
| **DUSRSECJ.jcl** | On-Demand | Security Setup | No scheduler entry; IDCAMS DEFINE/REPRO | Define and load user security file |
| **DEFCUST.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE | Define customer VSAM (alternate) |
| **DEFGDGB.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE GDG | Define GDG base for backup files |
| **DEFGDGD.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE GDG | Define GDG base for daily files |
| **ESDSRRDS.jcl** | On-Demand | Initial Setup | No scheduler entry; IDCAMS DEFINE ESDS/RRDS | Define ESDS/RRDS VSAM clusters |
| **CBADMCDJ.jcl** | On-Demand | Utility | No scheduler entry; admin card maintenance | Admin card data maintenance utility |
| **FTPJCL.JCL** | On-Demand | Data Transfer | No scheduler entry; FTP transfer | Transfer files to/from remote systems |
| **INTRDRJ1.JCL** | On-Demand | Utility | No scheduler entry; internal reader | Internal reader job submission (1) |
| **INTRDRJ2.JCL** | On-Demand | Utility | No scheduler entry; internal reader | Internal reader job submission (2) |
| **LOADPADB.JCL** | On-Demand | IMS Maintenance | No scheduler entry; reload after maintenance | Load pending auth IMS database |
| **UNLDPADB.JCL** | On-Demand | IMS Maintenance | No scheduler entry; unload for backup | Unload pending auth IMS database |
| **UNLDGSAM.JCL** | On-Demand | IMS Maintenance | No scheduler entry; GSAM unload | Unload IMS database via GSAM |
| **DBPAUTP0.jcl** | On-Demand | IMS Maintenance | No scheduler entry; DB provisioning | Provision IMS pending auth database |

### 6.9 Frequency Summary

| Frequency | Job Count | Characteristics |
|-----------|-----------|-----------------|
| **Daily** | 6 | Core batch window: close files → backup → process → reopen. Runs 365 days/year. |
| **Weekly** | 6 | Reference data refresh from DB2. Runs Saturdays during extended maintenance window. |
| **Monthly** | 9 | Interest calculation, statement generation, file audits, balance reports. Runs end-of-cycle. |
| **On-Demand** | 24 | Initial setup (IDCAMS), migrations (export/import), maintenance (IMS), utilities. Manual trigger only. |

**Key observations:**
- Jobs like CLOSEFIL, WAITSTEP, and OPENFIL are **shared utility jobs** — they appear in daily, weekly, and monthly pipelines but are counted once at their highest frequency.
- COMBTRAN runs monthly as part of the interest calculation pipeline (validates interest-generated transactions).
- The CA-7 scheduler uses SCHID values to distinguish pipeline instances (030 = daily, 031/032 = weekly sub-chains).
- All 24 on-demand jobs lack scheduler entries entirely — they exist for initial provisioning, ad-hoc migrations, or manual maintenance.

---

## 7. CICS Transaction / Program Mapping

| Transaction ID | Program | Function |
|---------------|---------|----------|
| CDSG | COSGN00C | Sign-on |
| CADM | COADM01C | Admin menu |
| CMEN | COMEN01C | Main menu |
| CAAV | COACTVWC | Account View |
| CAAU | COACTUPC | Account Update |
| CACL | COCRDLIC | Card List |
| CACS | COCRDSLC | Card Select |
| CACU | COCRDUPC | Card Update |
| CATL | COTRN00C | Transaction List |
| CATV | COTRN01C | Transaction View |
| CATA | COTRN02C | Transaction Add |
| CARP | CORPT00C | Reports |
| CABP | COBIL00C | Bill Payment |
| CAUL | COUSR00C | User List |
| CAUA | COUSR01C | User Add |
| CAUU | COUSR02C | User Update |
| CAUD | COUSR03C | User Delete |
| CPAS | COPAUS0C | Pending Auth Summary |
| CPA1 | COPAUS1C | Pending Auth Detail |
| CPA2 | COPAUS2C | Pending Auth Schedule |
| CPAA | COPAUA0C | Auth Async Processor |
| CTTT | COTRTLIC | Transaction Type List |
| CTTU | COTRTUPC | Transaction Type Update |
| CAC1 | COACCT01 | Account MQ Inquiry |
| CDT1 | CODATE01 | Date MQ Validation |

---

## 8. Consolidated File-to-Program Matrix

| VSAM File | Batch Writers | Batch Readers | Online Writers | Online Readers |
|-----------|--------------|--------------|----------------|----------------|
| ACCTDAT | CBIMPORT, CBTRN01C, CBTRN02C, CBACT04C | CBACT01C, CBACT04C, CBEXPORT, CBSTM03A | COACTUPC, COBIL00C | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN02C, COPAUA0C, COACCT01 |
| CARDDAT | CBIMPORT | CBACT02C, CBEXPORT, CBTRN01C, CBTRN02C | COCRDUPC | COCRDLIC, COCRDSLC, COTRN02C, COPAUA0C |
| CUSTFILE | CBIMPORT | CBCUS01C, CBEXPORT, CBTRN01C, CBSTM03A | — | — |
| XREFFILE | CBIMPORT | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A | — | COCRDLIC (via AIX) |
| TRANFILE | CBIMPORT, CBTRN01C, CBTRN02C, CBACT04C | CBACT04C, CBTRN03C, CBEXPORT | COTRN02C, COBIL00C | COTRN00C, COTRN01C |
| USRSEC | (DUSRSECJ.jcl) | — | COUSR01C | COSGN00C, COUSR00C, COUSR02C, COUSR03C |
| DALYTRAN | (external feed) | CBTRN01C, CBTRN02C | — | — |
