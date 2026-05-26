# DEPENDENCY MAP — CardDemo COBOL Estate

> Inter-program call graphs, dataset lineage, and end-to-end batch pipeline flows.

---

## 1. Inter-Program Call Graph

### 1.1 CALL Dependencies (Program → Program)

Programs that invoke other programs via COBOL `CALL` or CICS `XCTL`/`LINK`:

```
CBSTM03A ──CALL──→ CBSTM03B        (Statement I/O subroutine)
CBSTM03A ──CALL──→ CEE3ABD          (LE abnormal termination)

COTRN02C ──CALL──→ CSUTLDTC         (Date validation utility)
CORPT00C ──CALL──→ CSUTLDTC         (Date validation utility)
COBIL00C ──CALL──→ CSUTLDTC         (Date validation utility — inferred)

CSUTLDTC ──CALL──→ CEEDAYS          (IBM LE date conversion service)
COBSWAIT ──CALL──→ MVSWAIT          (Assembler wait routine)

COPAUA0C ──CALL──→ MQOPEN           (MQ Open queue)
COPAUA0C ──CALL──→ MQGET            (MQ Get message)
COPAUA0C ──CALL──→ MQPUT1           (MQ Put single message)
COPAUA0C ──CALL──→ MQCLOSE          (MQ Close queue)

COACCT01 ──CALL──→ MQOPEN / MQGET / MQPUT / MQCLOSE  (MQ operations)
CODATE01 ──CALL──→ MQOPEN / MQGET / MQPUT / MQCLOSE  (MQ operations)

PAUDBLOD ──CALL──→ CBLTDLI          (IMS DL/I: ISRT, GU)
PAUDBUNL ──CALL──→ CBLTDLI          (IMS DL/I: GN, GNP)
DBUNLDGS ──CALL──→ CBLTDLI          (IMS DL/I: GN, GNP, ISRT to GSAM)
CBPAUP0C ──CALL──→ CBLTDLI          (IMS DL/I: GN, GNP, DLET)
```

### 1.2 CICS Transfer-of-Control (XCTL) Graph

Online programs transfer control via `EXEC CICS XCTL`:

```
COSGN00C ──XCTL──→ COADM01C         (Admin sign-on → Admin menu)
COSGN00C ──XCTL──→ COMEN01C         (User sign-on → Main menu)

COADM01C ──XCTL──→ COUSR00C         (Admin menu → User List)
COADM01C ──XCTL──→ COUSR01C         (Admin menu → User Add)
COADM01C ──XCTL──→ COUSR02C         (Admin menu → User Update)
COADM01C ──XCTL──→ COUSR03C         (Admin menu → User Delete)
COADM01C ──XCTL──→ COTRTLIC         (Admin menu → Tran Type List)
COADM01C ──XCTL──→ COTRTUPC         (Admin menu → Tran Type Maint)

COMEN01C ──XCTL──→ COACTVWC         (Main menu → Account View)
COMEN01C ──XCTL──→ COACTUPC         (Main menu → Account Update)
COMEN01C ──XCTL──→ COCRDLIC         (Main menu → Card List)
COMEN01C ──XCTL──→ COCRDSLC         (Main menu → Card View)
COMEN01C ──XCTL──→ COCRDUPC         (Main menu → Card Update)
COMEN01C ──XCTL──→ COTRN00C         (Main menu → Transaction List)
COMEN01C ──XCTL──→ COTRN01C         (Main menu → Transaction View)
COMEN01C ──XCTL──→ COTRN02C         (Main menu → Transaction Add)
COMEN01C ──XCTL──→ CORPT00C         (Main menu → Transaction Reports)
COMEN01C ──XCTL──→ COBIL00C         (Main menu → Bill Payment)
COMEN01C ──XCTL──→ COPAUS0C         (Main menu → Pending Auth View)

COCRDLIC ──XCTL──→ COCRDSLC         (Card List → Card View)
COCRDSLC ──XCTL──→ COCRDUPC         (Card View → Card Update)
COTRN00C ──XCTL──→ COTRN01C         (Transaction List → Transaction View)
COPAUS0C ──XCTL──→ COPAUS1C         (Auth List → Auth Detail)
COPAUS1C ──LINK──→ COPAUS2C         (Auth Detail → Fraud Update DB2)
COTRTLIC ──XCTL──→ COTRTUPC         (Tran Type List → Tran Type Update)
```

### 1.3 Complete Call Graph (Visual)

```
                                    ┌──────────┐
                                    │ COSGN00C │  (Sign-on)
                                    └────┬─────┘
                           ┌─────────────┴─────────────┐
                           ▼                           ▼
                    ┌──────────┐                ┌──────────┐
                    │ COADM01C │ (Admin Menu)   │ COMEN01C │ (User Menu)
                    └────┬─────┘                └────┬─────┘
          ┌──────┬───────┼───────┬──────┐            │
          ▼      ▼       ▼       ▼      ▼            │
     COUSR00C COUSR01C COUSR02C COUSR03C │            │
     (List)   (Add)    (Update) (Delete) │            │
                                         ▼            │
                                  ┌──────────┐        │
                                  │ COTRTLIC │←───────┤
                                  └────┬─────┘        │
                                       ▼              │
                                  ┌──────────┐        │
                                  │ COTRTUPC │←───────┤
                                  └──────────┘        │
          ┌──────────────┬────────────┬───────────────┤
          ▼              ▼            ▼               │
     COACTVWC       COACTUPC     COBIL00C             │
     (Acct View)    (Acct Upd)   (Bill Pay)           │
                                                      │
          ┌──────────────┬────────────┐               │
          ▼              ▼            ▼               │
     COCRDLIC ──→ COCRDSLC ──→ COCRDUPC              │
     (Card List)  (Card View)  (Card Upd)             │
                                                      │
          ┌──────────────┬────────────┐               │
          ▼              ▼            ▼               │
     COTRN00C ──→ COTRN01C       COTRN02C ──→ CSUTLDTC ──→ CEEDAYS
     (Tran List)  (Tran View)    (Tran Add)                        
                                                      │
          ┌──────────────┐                            │
          ▼              │                            │
     CORPT00C ──→ CSUTLDTC                            │
     (Reports)                                        │
                                                      │
          ┌──────────────┐                            │
          ▼              │                            │
     COPAUS0C ──→ COPAUS1C ──→ COPAUS2C (DB2)         │
     (Auth List)  (Auth Detail) (Fraud Upd)           │
                                                      │
     COBIL00C ←───────────────────────────────────────┘
     (Bill Pay)

     Batch:
     CBSTM03A ──→ CBSTM03B    (Statement generation)
     COBSWAIT ──→ MVSWAIT     (Batch wait utility)

     MQ Programs (standalone):
     COPAUA0C ──→ MQ API (MQOPEN/MQGET/MQPUT1/MQCLOSE)
     COACCT01 ──→ MQ API
     CODATE01 ──→ MQ API

     IMS Programs (standalone):
     PAUDBLOD ──→ CBLTDLI (IMS Load)
     PAUDBUNL ──→ CBLTDLI (IMS Unload)
     DBUNLDGS ──→ CBLTDLI (IMS GSAM Unload)
     CBPAUP0C ──→ CBLTDLI (IMS Purge)
```

---

## 2. Dataset Lineage

### 2.1 Master VSAM File Lineage

Each VSAM KSDS is defined, loaded, and accessed by specific programs and jobs:

#### ACCTDATA.VSAM.KSDS (Account Master)
```
Source:        ACCTDATA.PS (sequential flat file)
Defined by:    ACCTFILE.jcl (IDCAMS DEFINE/REPRO)
Written by:    CBACT04C (interest calc — REWRITE), COBIL00C (bill pay — REWRITE)
Read by:       CBACT01C (extract), CBEXPORT (export), CBTRN01C/02C (validation),
               CBSTM03A/B (statements), COACTVWC/COACTUPC (online view/update),
               COPAUA0C (auth engine), COPAUS0C (auth summary),
               COACCT01 (MQ inquiry), COBIL00C (bill pay)
Backed up by:  (not directly — account data persists across cycles)
```

#### CARDDATA.VSAM.KSDS (Card Master) + AIX
```
Source:        CARDDATA.PS (sequential flat file)
Defined by:    CARDFILE.jcl (IDCAMS DEFINE/REPRO + AIX for ACCTID)
Written by:    COCRDUPC (card update — REWRITE)
Read by:       CBACT02C (dump), CBEXPORT (export), CBTRN01C (validation),
               COCRDLIC (list browse), COCRDSLC (detail view),
               COCRDUPC (update), COACTVWC (account view)
AIX path:      CARDDATA.VSAM.KSDS.ACCTID.PATH (lookup cards by account ID)
```

#### CUSTDATA.VSAM.KSDS (Customer Master)
```
Source:        CUSTDATA.PS (sequential flat file)
Defined by:    CUSTFILE.jcl (IDCAMS DEFINE/REPRO)
Written by:    COACTUPC (customer update — REWRITE)
Read by:       CBCUS01C (dump), CBEXPORT (export), CBSTM03A/B (statements),
               COACTVWC/COACTUPC (online), COCRDSLC (card view),
               COPAUA0C (auth), COPAUS0C (auth summary)
```

#### CARDXREF.VSAM.KSDS (Card Cross-Reference) + AIX
```
Source:        CARDXREF.PS (sequential flat file)
Defined by:    XREFFILE.jcl (IDCAMS DEFINE/REPRO + AIX)
Read by:       CBACT03C (dump), CBEXPORT (export), CBTRN02C (posting),
               CBTRN03C (reports), CBSTM03A/B (statements),
               COACTVWC/COACTUPC (online), COCRDSLC/COCRDUPC (cards),
               COTRN02C (tran add), COBIL00C (bill pay),
               COPAUA0C (auth)
AIX path:      CARDXREF.VSAM.KSDS.ACCTID.PATH (lookup xrefs by account ID)
```

#### TRANSACT.VSAM.KSDS (Transaction Master)
```
Source:        DALYTRAN.PS.INIT (initial daily transactions)
Defined by:    TRANFILE.jcl (IDCAMS DEFINE/REPRO)
Written by:    CBTRN02C (post daily — WRITE), COTRN02C (online add — WRITE),
               COBIL00C (bill pay — WRITE), COMBTRAN (merge — REPRO)
Read by:       CBTRN03C (reports), CBEXPORT (export), CBSTM03A/B (statements),
               COTRN00C (list), COTRN01C (view), COTRN02C (read for seq#)
Backed up by:  TRANBKP.jcl → TRANSACT.BKUP(+1) GDG
Combined by:   COMBTRAN.jcl (SYSTRAN(0) + TRANSACT.BKUP(0) → TRANSACT.VSAM.KSDS)
```

#### TCATBALF.VSAM.KSDS (Category Balance)
```
Source:        TCATBALF.PS (sequential flat file)
Defined by:    TCATBALF.jcl (IDCAMS DEFINE/REPRO)
Written by:    CBTRN02C (post daily — WRITE/REWRITE)
Read by:       CBACT04C (interest calc), CBTRN02C (posting)
Backed up by:  PRTCATBL.jcl → TCATBALF.BKUP(+1) GDG
```

#### TRANTYPE.VSAM.KSDS (Transaction Type Reference)
```
Source:        TRANTYPE.PS (sequential flat file)
Defined by:    TRANTYPE.jcl (IDCAMS DEFINE/REPRO)
Read by:       CBTRN03C (reports)
Maintained by: COBTUPDT (batch DB2 maint), COTRTLIC/COTRTUPC (online DB2)
```

#### TRANCATG.VSAM.KSDS (Transaction Category Reference)
```
Source:        TRANCATG.PS (sequential flat file)
Defined by:    TRANCATG.jcl (IDCAMS DEFINE/REPRO)
Read by:       CBTRN03C (reports)
```

#### DISCGRP.VSAM.KSDS (Disclosure/Discount Group)
```
Source:        DISCGRP.PS (sequential flat file)
Defined by:    DISCGRP.jcl (IDCAMS DEFINE/REPRO)
Read by:       CBACT04C (interest calculation)
```

#### USRSEC.VSAM.KSDS (User Security)
```
Source:        Inline data in DUSRSECJ.jcl (initial user records)
Defined by:    DUSRSECJ.jcl (IEBGENER + IDCAMS)
Read by:       COSGN00C (sign-on), COUSR00C (list), COUSR02C (update),
               COUSR03C (delete)
Written by:    COUSR01C (add — WRITE), COUSR02C (update — REWRITE),
               COUSR03C (delete — DELETE)
```

### 2.2 GDG (Generation Data Group) Lineage

```
TRANSACT.BKUP         ← TRANBKP.jcl (daily backup of TRANSACT VSAM)
SYSTRAN(+1)           ← INTCALC.jcl (interest-generated transactions)
TRANSACT.COMBINED(+1) ← COMBTRAN.jcl (merged SYSTRAN + TRANSACT.BKUP)
DALYREJS(+1)          ← POSTTRAN.jcl (daily transaction rejects)
TRANREPT(+1)          ← TRANREPT.jcl (transaction reports)
TCATBALF.BKUP(+1)     ← PRTCATBL.jcl (category balance backup)
TRANTYPE.BKUP(+1)     ← DEFGDGD.jcl (transaction type backup)
TRANCATG.PS.BKUP(+1)  ← DEFGDGD.jcl (transaction category backup)
DISCGRP.BKUP(+1)      ← DEFGDGD.jcl (discount group backup)
```

### 2.3 Sequential File Flows

```
DALYTRAN.PS ──→ CBTRN01C (validation) ──→ CBTRN02C (posting)
                                                │
                                                ├──→ TRANSACT.VSAM.KSDS (posted transactions)
                                                └──→ DALYREJS(+1) (rejected transactions)

EXPORT.DATA ←── CBEXPORT (reads 5 VSAM master files)
EXPORT.DATA ──→ CBIMPORT → CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT,
                            TRANSACT.IMPORT, IMPORT.ERRORS
```

---

## 3. JCL Job → Program → Dataset Matrix

| JCL Job | Program Executed | Reads (Input) | Writes (Output) |
|---------|-----------------|---------------|-----------------|
| POSTTRAN | CBTRN02C | DALYTRAN.PS, CARDXREF.VSAM, ACCTDATA.VSAM, TCATBALF.VSAM, TRANSACT.VSAM | TRANSACT.VSAM (new txns), ACCTDATA.VSAM (bal update), TCATBALF.VSAM (cat bal update), DALYREJS(+1) |
| INTCALC | CBACT04C | TCATBALF.VSAM, CARDXREF.VSAM+AIX, ACCTDATA.VSAM, DISCGRP.VSAM | ACCTDATA.VSAM (interest), SYSTRAN(+1) |
| TRANREPT | CBTRN03C (+ SORT) | TRANSACT.VSAM (sorted), CARDXREF.VSAM, TRANTYPE.VSAM, TRANCATG.VSAM, DATEPARM | TRANREPT(+1) |
| CREASTMT | CBSTM03A+CBSTM03B | TRANSACT.VSAM (via SORT→SEQ→VSAM), CARDXREF.VSAM, CUSTDATA.VSAM, ACCTDATA.VSAM | STATEMNT.PS, STATEMNT.HTML |
| TRANBKP | *(IDCAMS REPRO)* | TRANSACT.VSAM | TRANSACT.BKUP(+1) |
| COMBTRAN | *(SORT + IDCAMS)* | SYSTRAN(0), TRANSACT.BKUP(0) | TRANSACT.COMBINED(+1), TRANSACT.VSAM (reloaded) |
| READACCT | CBACT01C | ACCTDATA.VSAM | ACCTDATA.PSCOMP, ARRYPS, VBPS |
| READCARD | CBACT02C | CARDDATA.VSAM | *(stdout)* |
| READCUST | CBCUS01C | CUSTDATA.VSAM | *(stdout)* |
| READXREF | CBACT03C | CARDXREF.VSAM | *(stdout)* |
| CBEXPORT | CBEXPORT | CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA (all VSAM) | EXPORT.DATA |
| CBIMPORT | CBIMPORT | EXPORT.DATA | CUSTDATA.IMPORT, ACCTDATA.IMPORT, etc. |
| CLOSEFIL | *(SDSF)* | — | *(CICS file close commands)* |
| OPENFIL | *(SDSF)* | — | *(CICS file open commands)* |
| WAITSTEP | COBSWAIT | — | — |
| PRTCATBL | *(IDCAMS+SORT)* | TCATBALF.VSAM | TCATBALF.BKUP(+1), TCATBALF.REPT |
| MNTTRDB2 | COBTUPDT | TR-RECORD (PS) | DB2 TRANSACTION_TYPE table |

---

## 4. End-to-End Batch Pipeline Flows

### 4.1 Daily Batch Cycle (Control-M: `DAILY-TransactionBackup`)

```
                    ┌─────────────────────────────────────────────────────────┐
                    │                    DAILY BATCH CYCLE                    │
                    │                                                         │
Step 1: CLOSEFIL    │  Close CICS files (ACCTDATA, CARDDATA, CUSTDATA, etc.) │
          │         │                                                         │
Step 2: TRANBKP     │  TRANSACT.VSAM.KSDS ──REPRO──→ TRANSACT.BKUP(+1)      │
          │         │                                                         │
Step 3: WAITSTEP    │  COBSWAIT → MVSWAIT (pause for batch window)           │
          │         │                                                         │
Step 4: OPENFIL     │  Re-open CICS files for online operations              │
                    └─────────────────────────────────────────────────────────┘
```

### 4.2 Daily Transaction Processing (Implicit Pre-Batch)

```
DALYTRAN.PS  ──→  POSTTRAN.jcl (CBTRN02C)
  (Daily input)         │
                        ├──→ TRANSACT.VSAM.KSDS    (posted transactions)
                        ├──→ ACCTDATA.VSAM.KSDS    (balance updates)
                        ├──→ TCATBALF.VSAM.KSDS    (category balance updates)
                        └──→ DALYREJS(+1)          (rejected transactions)
```

### 4.3 Weekly Batch Cycle (Control-M: `WEEKLY-TransactionTypesDBRefresh`)

```
                    ┌─────────────────────────────────────────────────────────┐
                    │                   WEEKLY BATCH CYCLE                    │
                    │                                                         │
Step 1: MNTTRDB2    │  COBTUPDT → Update DB2 TRANSACTION_TYPE table          │
          │         │                                                         │
          ├─────────┼── Triggers SMART Folder: DisclosureGroupsRefresh ──┐    │
          │         │                                                    │    │
          │         │  CLOSEFIL → DISCGRP.jcl → WAITSTEP → OPENFIL      │    │
          │         │  (Close → Reload DISCGRP.VSAM → Wait → Open)      │    │
          │         │                                                    │    │
          └─────────┼── Triggers SMART Folder: TransactionTypesRefresh ──┘    │
                    │                                                         │
                    │  TRANEXTR → Extract from DB2 to VSAM                    │
                    └─────────────────────────────────────────────────────────┘
```

### 4.4 Monthly Batch Cycle (Control-M: `MONTHLY-InterestCalculation`)

```
                    ┌─────────────────────────────────────────────────────────┐
                    │                  MONTHLY BATCH CYCLE                    │
                    │                                                         │
Step 1: CLOSEFIL    │  Close CICS files for batch window                     │
          │         │                                                         │
Step 2: INTCALC     │  CBACT04C: Interest Calculation                        │
          │         │    TCATBALF.VSAM ──→ category balances                 │
          │         │    DISCGRP.VSAM ──→ interest rates                     │
          │         │    CARDXREF.VSAM ──→ card-account mapping              │
          │         │    ──→ ACCTDATA.VSAM (interest charges applied)         │
          │         │    ──→ SYSTRAN(+1) (interest transactions)             │
          │         │                                                         │
Step 3: COMBTRAN    │  Combine Transactions                                  │
          │         │    SYSTRAN(0) + TRANSACT.BKUP(0)                       │
          │         │    ──SORT/MERGE──→ TRANSACT.COMBINED(+1)               │
          │         │    ──REPRO──→ TRANSACT.VSAM.KSDS (reloaded)            │
          │         │                                                         │
Step 4: WAITSTEP    │  Batch wait                                            │
          │         │                                                         │
Step 5: OPENFIL     │  Re-open CICS files                                    │
                    └─────────────────────────────────────────────────────────┘
```

### 4.5 Ad-Hoc / On-Demand Flows

```
Transaction Reports (from CICS CORPT00C):
  CORPT00C ──TDQ──→ INTRDR ──→ TRANREPT.jcl ──→ CBTRN03C ──→ TRANREPT(+1)

Statement Generation (manual):
  CREASTMT.JCL: SORT TRANSACT → SEQ → VSAM → CBSTM03A(+CBSTM03B) → STATEMNT.PS + HTML

Branch Data Migration:
  CBEXPORT.jcl: 5 VSAM masters → EXPORT.DATA
  CBIMPORT.jcl: EXPORT.DATA → 5 individual output files
```

---

## 5. Copybook Usage Matrix

| Copybook | Used By Programs |
|----------|-----------------|
| COCOM01Y | COADM01C, COMEN01C, COSGN00C, COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC |
| COTTL01Y | COADM01C, COMEN01C, COSGN00C, COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COPAUS1C, COTRTLIC, COTRTUPC |
| CSDAT01Y | All online programs (date/time display) |
| CSMSG01Y | All online programs (common messages) |
| DFHAID | All CICS programs (AID key values) |
| DFHBMSCA | All CICS programs (BMS attribute constants) |
| CVACT01Y | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBSTM03A, COACTVWC, COACTUPC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C, COACCT01 |
| CVACT02Y | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, COPAUS0C, COTRTLIC |
| CVACT03Y | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, COACTVWC, COACTUPC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C |
| CVCUS01Y | CBCUS01C, CBEXPORT, CBIMPORT, COACTVWC, COACTUPC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C |
| CVTRA05Y | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C |
| CVTRA01Y | CBACT04C, CBTRN02C |
| CVTRA02Y | CBACT04C |
| CVTRA03Y | CBTRN03C |
| CVTRA04Y | CBTRN03C |
| CVTRA06Y | CBTRN01C, CBTRN02C |
| CVTRA07Y | CBTRN03C |
| CVCRD01Y | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC, COTRTUPC |
| CSUSR01Y | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COACTUPC, COACTVWC |
| CUSTREC | CBSTM03A |
| COSTM01 | CBSTM03A |
| CVEXPORT | CBEXPORT, CBIMPORT |
| CODATECN | CBACT01C |
| CSLKPCDY | COACTUPC |
| CSUTLDWY | COACTUPC, COTRTUPC |
| CIPAUSMY | COPAUA0C, COPAUS0C, COPAUS1C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS |
| CIPAUDTY | COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS |
| CCPAURQY | COPAUA0C |
| CCPAURLY | COPAUA0C |
| CCPAUERY | COPAUA0C |
