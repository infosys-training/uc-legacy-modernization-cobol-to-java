# DEPENDENCY MAP — CardDemo Call Graph & Dataset Lineage

## 1. Program Call Graph

### 1.1 CALL Dependencies (inter-program invocations)

```
CBACT01C ──CALL──→ COBDATFT (Assembler date formatter)
         ──CALL──→ CEE3ABD  (LE Abend handler)

CBACT02C ──CALL──→ CEE3ABD
CBACT03C ──CALL──→ CEE3ABD
CBACT04C ──CALL──→ CEE3ABD
CBCUS01C ──CALL──→ CEE3ABD
CBEXPORT ──CALL──→ CEE3ABD
CBIMPORT ──CALL──→ CEE3ABD
CBTRN01C ──CALL──→ CEE3ABD
CBTRN02C ──CALL──→ CEE3ABD
CBTRN03C ──CALL──→ CEE3ABD

CBSTM03A ──CALL──→ CBSTM03B (Statement file processing subroutine, called 13× per run)
         ──CALL──→ CEE3ABD

COBSWAIT ──CALL──→ MVSWAIT  (System wait routine)

CSUTLDTC ──CALL──→ CEEDAYS  (LE date conversion)

CORPT00C ──CALL──→ CSUTLDTC (Date utility subroutine)
COTRN02C ──CALL──→ CSUTLDTC (Date utility subroutine)

COPAUA0C ──CALL──→ MQOPEN   (MQ Open queue)
         ──CALL──→ MQGET    (MQ Get message)
         ──CALL──→ MQPUT1   (MQ Put reply)
         ──CALL──→ MQCLOSE  (MQ Close queue)

COACCT01 ──CALL──→ MQOPEN  (×3 queues)
         ──CALL──→ MQGET
         ──CALL──→ MQPUT   (×2)
         ──CALL──→ MQCLOSE (×3)

CODATE01 ──CALL──→ MQOPEN  (×3 queues)
         ──CALL──→ MQGET
         ──CALL──→ MQPUT   (×2)
         ──CALL──→ MQCLOSE (×3)

DBUNLDGS ──CALL──→ CBLTDLI  (IMS DL/I: GN, GNP, ISRT)
PAUDBLOD ──CALL──→ CBLTDLI  (IMS DL/I: ISRT, GU)
PAUDBUNL ──CALL──→ CBLTDLI  (IMS DL/I: GN, GNP)
```

### 1.2 CICS XCTL (Transfer Control) — Online Navigation

```
COSGN00C ──XCTL──→ COMEN01C (Regular user menu)
         ──XCTL──→ COADM01C (Admin menu)

COMEN01C ──XCTL──→ COACTVWC | COACTUPC | COCRDLIC | COCRDSLC | COCRDUPC
                   | COTRN00C | COTRN01C | COTRN02C | CORPT00C | COBIL00C
                   | COPAUS0C

COADM01C ──XCTL──→ COUSR00C | COUSR01C | COUSR02C | COUSR03C
                   | COTRTLIC | COTRTUPC

COCRDLIC ──XCTL──→ COCRDSLC (View detail)
         ──XCTL──→ COCRDUPC (Update)
         ──XCTL──→ COMEN01C (Back to menu)

COCRDSLC ──XCTL──→ COMEN01C (Back)
COCRDUPC ──XCTL──→ COMEN01C (Back)
COACTVWC ──XCTL──→ COMEN01C (Back)
COACTUPC ──XCTL──→ COMEN01C (Back)

COPAUS0C ──XCTL──→ COMEN01C (Back)
COPAUS1C ──LINK──→ COPAUS2C (Mark fraud — synchronous)
```

### 1.3 Visual Call Graph

```
                    ┌─────────────┐
                    │  COSGN00C   │ (Signon)
                    └──────┬──────┘
                           │ XCTL
              ┌────────────┴────────────┐
              ▼                         ▼
     ┌─────────────┐           ┌─────────────┐
     │  COMEN01C   │           │  COADM01C   │
     │ (User Menu) │           │(Admin Menu) │
     └──────┬──────┘           └──────┬──────┘
            │                         │
    ┌───┬───┼───┬───┬───┐    ┌───┬───┼───┬───┐
    ▼   ▼   ▼   ▼   ▼   ▼    ▼   ▼   ▼   ▼   ▼
  ACTV ACTU CRDL CRDS CRDU  USR0 USR1 USR2 USR3 TRTL
  TRN0 TRN1 TRN2 RPT0 BIL0  TRTU
  PAUS0
    │
    ▼
  PAUS1 ──LINK──→ PAUS2 (DB2 fraud marking)
```

---

## 2. Dataset Lineage — JCL Job ↔ File ↔ Program Mapping

### 2.1 VSAM Datasets

| Dataset Name | Type | Record Len | Defined By | Written By | Read By |
|-------------|------|-----------|-----------|-----------|---------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | KSDS | 300 | ACCTFILE.jcl | CBACT04C, CBTRN01C, CBTRN02C, COACTUPC, COBIL00C | CBACT01C, CBACT04C, CBEXPORT, CBTRN02C, COACTVWC, COACTUPC, COTRN02C, COBIL00C, COPAUA0C, COACCT01 |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | KSDS | 150 | CARDFILE.jcl | — (master data) | CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.AIX | AIX | 150 | CARDFILE.jcl | (auto-maintained) | COCRDLIC (browse by account) |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | KSDS | 50 | XREFFILE.jcl | — (master data) | CBACT03C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, COACTVWC, COACTUPC, COTRN02C, COBIL00C, COPAUA0C |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | KSDS | 500 | CUSTFILE.jcl | — (master data) | CBCUS01C, CBEXPORT, CBTRN01C, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | KSDS | 350 | TRANFILE.jcl | CBTRN01C, CBTRN02C, COTRN02C, COBIL00C, COMBTRAN.jcl | CBEXPORT, CBTRN03C, COTRN00C, COTRN01C |
| AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | KSDS | 50 | TCATBALF.jcl | CBACT04C, CBTRN02C | CBACT04C |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | KSDS | 50 | DISCGRP.jcl | — (reference data) | CBACT04C |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | KSDS | 60 | TRANTYPE.jcl | — (reference data) | CBTRN03C |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | KSDS | 60 | TRANCATG.jcl | — (reference data) | CBTRN03C |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | KSDS | 80 | DUSRSECJ.jcl | COUSR01C, COUSR02C | COSGN00C, COUSR00C, COUSR02C, COUSR03C |
| AWS.M2.CARDDEMO.EXPORT.DATA | KSDS | 500 | CBEXPORT.jcl | CBEXPORT | CBIMPORT |

### 2.2 Sequential / GDG Datasets

| Dataset Name | Type | Written By | Read By |
|-------------|------|-----------|---------|
| AWS.M2.CARDDEMO.DALYTRAN.PS | Sequential | External (daily feed) | CBTRN01C (via POSTTRAN.jcl), CBTRN02C |
| AWS.M2.CARDDEMO.DALYREJS.PS | Sequential | CBTRN02C | — (error review) |
| AWS.M2.CARDDEMO.TRANSACT.BKUP(n) | GDG | TRANBKP.jcl (IDCAMS REPRO) | COMBTRAN.jcl (SORT input) |
| AWS.M2.CARDDEMO.TRANSACT.COMBINED(n) | GDG | COMBTRAN.jcl (SORT output) | COMBTRAN.jcl STEP10 (REPRO to VSAM) |
| AWS.M2.CARDDEMO.SYSTRAN(n) | GDG | CBACT04C (interest trans) | COMBTRAN.jcl (merge input) |
| TRANREPT (report file) | Sequential | CBTRN03C | TXT2PDF1.JCL |
| STMTFILE / HTMLFILE | Sequential | CBSTM03A | External (print/email) |
| AWS.M2.CARDDEMO.*.IMPORT | Sequential | CBIMPORT | — (load to VSAM) |
| AWS.M2.CARDDEMO.IMPORT.ERRORS | Sequential | CBIMPORT | — (error review) |

### 2.3 DB2 Tables

| Table | Schema | Written By | Read By |
|-------|--------|-----------|---------|
| TRNTYPE | — | COBTUPDT, COTRTUPC, MNTTRDB2.jcl | COTRTLIC, COTRTUPC, TRANEXTR.jcl |
| TRNTYCAT | — | COTRTUPC, MNTTRDB2.jcl | COTRTUPC |
| AUTHFRDS | — | COPAUS2C | COPAUS2C (check existing) |

### 2.4 IMS Databases

| DBD Name | Segments | Written By | Read By |
|----------|----------|-----------|---------|
| DBPAUTP0 | PA-SUMMARY (root), PA-DETAIL (child) | PAUDBLOD, COPAUA0C | CBPAUP0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBUNL |

### 2.5 MQ Queues

| Queue | Purpose | Put By | Get By |
|-------|---------|--------|--------|
| AUTH.REQUEST.Q | Authorization requests from acquirer | External system | COPAUA0C, COACCT01 |
| AUTH.REPLY.Q | Authorization responses | COPAUA0C, COACCT01 | External system |
| AUTH.ERROR.Q | Error/dead-letter | COPAUA0C | Admin monitoring |

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Processing Pipeline

```
┌──────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH CYCLE                                   │
├──────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ① CLOSEFIL ─── Close CICS VSAM files for exclusive batch access     │
│       │                                                               │
│       ▼                                                               │
│  ② POSTTRAN (CBTRN02C) ─── Read DALYTRAN.PS                         │
│       │                     Validate each transaction                 │
│       │                     Write valid → TRANSACT VSAM               │
│       │                     Write invalid → DALYREJS                  │
│       │                     Update TCATBALF balances                  │
│       │                     Update ACCTDATA balances                  │
│       ▼                                                               │
│  ③ TRANBKP ─── REPRO TRANSACT VSAM → GDG backup generation          │
│       │                                                               │
│       ▼                                                               │
│  ④ TRANREPT (SORT + CBTRN03C) ─── Sort transactions by date         │
│       │                             Produce daily transaction report  │
│       ▼                                                               │
│  ⑤ WAITSTEP (COBSWAIT) ─── Scheduling delay                         │
│       │                                                               │
│       ▼                                                               │
│  ⑥ OPENFIL ─── Reopen CICS VSAM files for online access             │
│                                                                       │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.2 Monthly Processing Pipeline

```
┌──────────────────────────────────────────────────────────────────────┐
│                   MONTHLY BATCH CYCLE                                  │
├──────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ① CLOSEFIL                                                          │
│       │                                                               │
│       ▼                                                               │
│  ② INTCALC (CBACT04C) ─── For each account:                         │
│       │                     Read XREF → get cards                     │
│       │                     Read TCATBALF → get category balances     │
│       │                     Read DISCGRP → get interest rates         │
│       │                     Calculate interest per category           │
│       │                     Generate interest transactions            │
│       │                     Update account balance                    │
│       ▼                                                               │
│  ③ COMBTRAN ─── SORT merge:                                         │
│       │           - TRANSACT.BKUP(0) (current transactions)          │
│       │           - SYSTRAN(0) (system-generated interest trans)      │
│       │         REPRO combined → TRANSACT VSAM                       │
│       ▼                                                               │
│  ④ WAITSTEP                                                          │
│       │                                                               │
│       ▼                                                               │
│  ⑤ OPENFIL                                                          │
│                                                                       │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.3 Weekly Processing Pipeline

```
┌──────────────────────────────────────────────────────────────────────┐
│                   WEEKLY BATCH CYCLE (Saturdays)                       │
├──────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  ① MNTTRDB2 ─── Refresh DB2 TRNTYPE/TRNTYCAT tables                 │
│       │                                                               │
│       ├────────────┐                                                  │
│       ▼            ▼                                                  │
│  [SMART_FOLDER 1]  [SMART_FOLDER 2]                                  │
│                                                                       │
│  CLOSEFIL          TRANEXTR ─── Extract types                        │
│     │               to flat file for distribution                     │
│     ▼                                                                 │
│  DISCGRP ─── Reload disclosure groups                                │
│     │                                                                 │
│     ▼                                                                 │
│  WAITSTEP                                                            │
│     │                                                                 │
│     ▼                                                                 │
│  OPENFIL                                                             │
│                                                                       │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.4 Branch Migration Pipeline (On-Demand)

```
┌──────────────────────────────────────────────────────────────────────┐
│                   BRANCH MIGRATION (EXPORT/IMPORT)                     │
├──────────────────────────────────────────────────────────────────────┤
│                                                                       │
│  SOURCE BRANCH:                                                       │
│  CBEXPORT.jcl → CBEXPORT program                                    │
│    Reads: CUSTFILE + ACCTFILE + XREFFILE + TRANSACT + CARDFILE       │
│    Writes: EXPORT.DATA (multi-record VSAM, 500-byte records)         │
│                                                                       │
│  ─── File Transfer (FTP/Network) ───                                 │
│                                                                       │
│  TARGET BRANCH:                                                       │
│  CBIMPORT.jcl → CBIMPORT program                                    │
│    Reads: EXPORT.DATA                                                │
│    Writes: CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT,        │
│            TRANSACT.IMPORT, (CARDOUT), IMPORT.ERRORS                 │
│    Then: Load normalized files into target VSAM clusters             │
│                                                                       │
└──────────────────────────────────────────────────────────────────────┘
```

### 3.5 Statement Generation Pipeline (Monthly)

```
CREASTMT.JCL:
  DELDEF01 (cleanup) → STEP010 (SORT transactions by account)
  → STEP020 (IDCAMS define output) → STEP030 (IEFBR14 dummy)
  → STEP040 (CBSTM03A)
       │
       └──CALL──→ CBSTM03B (×13 calls per account group)
                   Reads: TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE
                   Writes: STMTFILE (text), HTMLFILE (HTML)
```

---

## 4. Copybook Dependency Matrix

| Copybook | Used By (Program Count) | Key Programs |
|---------|------------------------|-------------|
| COCOM01Y | 17 | All CICS programs |
| DFHAID | 16 | All CICS programs |
| DFHBMSCA | 16 | All CICS programs |
| COTTL01Y | 15 | All CICS programs |
| CSDAT01Y | 15 | All CICS programs |
| CSMSG01Y | 15 | All CICS programs |
| CSUSR01Y | 10 | Security-related CICS programs |
| CVACT01Y | 10 | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COTRN02C, COBIL00C, COPAUA0C |
| CVACT03Y | 10 | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COTRN02C |
| CVTRA05Y | 8 | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COTRN00C, COTRN01C, COTRN02C |
| CVACT02Y | 7 | CBACT02C, CBEXPORT, CBIMPORT, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC |
| CVCUS01Y | 7 | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COCRDSLC, COCRDUPC |
| CVCRD01Y | 6 | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC |
| CIPAUSMY | 6 | CBPAUP0C, COPAUA0C, COPAUS0C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
| CIPAUDTY | 7 | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, DBUNLDGS, PAUDBLOD |
| CSSTRPFY | 7 | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC |
