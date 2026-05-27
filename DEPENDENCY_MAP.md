# Dependency Map — CardDemo COBOL Estate

> Inter-program call graph, dataset lineage, and batch pipeline flow.

---

## 1. Inter-Program Call Graph

### 1.1 Online (CICS) Program Transfer Graph

The CICS online application uses `EXEC CICS XCTL` (transfer control) for screen-to-screen navigation. The target program is resolved dynamically via menu option tables in COADM02Y.cpy and COMEN02Y.cpy.

```
COSGN00C (Sign-on)
├──XCTL──► COMEN01C (Main Menu)        [if user type = 'U']
│          ├──XCTL──► COACTVWC          [Option 1: Account View]
│          ├──XCTL──► COACTUPC          [Option 2: Account Update]
│          ├──XCTL──► COCRDLIC          [Option 3: Credit Card List]
│          │          ├──XCTL──► COCRDSLC  [Select: Card Detail View]
│          │          └──XCTL──► COCRDUPC  [Update: Card Update]
│          ├──XCTL──► COCRDSLC          [Option 4: Credit Card View]
│          ├──XCTL──► COCRDUPC          [Option 5: Credit Card Update]
│          ├──XCTL──► COTRN00C          [Option 6: Transaction List]
│          ├──XCTL──► COTRN01C          [Option 7: Transaction View]
│          ├──XCTL──► COTRN02C          [Option 8: Transaction Add]
│          ├──XCTL──► CORPT00C          [Option 9: Transaction Reports]
│          ├──XCTL──► COBIL00C          [Option 10: Bill Payment]
│          └──XCTL──► COPAUS0C          [Option 11: Pending Auth View]
│                      └──LINK──► COPAUS1C  [Auth Detail View]
│
└──XCTL──► COADM01C (Admin Menu)       [if user type = 'A']
           ├──XCTL──► COUSR00C          [Option 1: User List]
           ├──XCTL──► COUSR01C          [Option 2: User Add]
           ├──XCTL──► COUSR02C          [Option 3: User Update]
           ├──XCTL──► COUSR03C          [Option 4: User Delete]
           ├──XCTL──► COTRTLIC          [Option 5: Tran Type List (DB2)]
           │          └──XCTL──► COTRTUPC  [Update: Tran Type Update]
           └──XCTL──► COTRTUPC          [Option 6: Tran Type Maint (DB2)]
```

### 1.2 Batch Program CALL Graph

```
CBACT01C ──CALL──► COBDATFT (ASM)       [Date formatting]
         ──CALL──► CEE3ABD (LE)          [Abend on error]

CBACT02C ──CALL──► CEE3ABD

CBACT03C ──CALL──► CEE3ABD

CBACT04C ──CALL──► CEE3ABD

CBCUS01C ──CALL──► CEE3ABD

CBEXPORT ──CALL──► CEE3ABD

CBIMPORT ──CALL──► CEE3ABD

CBTRN01C ──CALL──► CEE3ABD

CBTRN02C ──CALL──► CEE3ABD

CBTRN03C ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT (ASM)        [z/OS wait macro]

CSUTLDTC ──CALL──► CEEDAYS (LE)         [Date-to-Lilian conversion]

CORPT00C ──CALL──► CSUTLDTC             [Date validation for report range]
COTRN02C ──CALL──► CSUTLDTC             [Date validation for new transactions]
```

### 1.3 Authorization Module Call Graph

```
COPAUA0C (Auth Decision - CICS)
├──CALL──► MQOPEN   (MQ API)            [Open request queue]
├──CALL──► MQGET    (MQ API)            [Read auth request]
├──CALL──► MQPUT1   (MQ API)            [Send auth reply]
├──CALL──► MQCLOSE  (MQ API)            [Close queues]
└──IMS DL/I──► Auth DB                  [Update summary/insert detail]

COPAUS2C (Fraud Marking - CICS + DB2)
└──EXEC SQL──► CARDDEMO.AUTHFRDS       [INSERT/UPDATE fraud records]

CBPAUP0C (Purge Expired - IMS Batch)
└──IMS DL/I──► GN/GNP/DLET             [Navigate and delete expired segments]

PAUDBLOD (DB Load - IMS Batch)
└──IMS DL/I──► ISRT/GU                 [Insert root + child segments from files]

PAUDBUNL (DB Unload - IMS Batch)
└──IMS DL/I──► GN/GNP                  [Traverse and write to files]

DBUNLDGS (GSAM Unload - IMS Batch)
└──IMS DL/I──► GN/GNP/ISRT             [Read via GSAM, reorganize]
```

### 1.4 VSAM-MQ Module Call Graph

```
COACCT01 (Account Inquiry - CICS + MQ)
├──CALL──► MQOPEN ×3                   [Open request/response/error queues]
├──CALL──► MQGET                       [Get inquiry request]
├──EXEC CICS READ──► ACCTFILE          [Read account data]
├──CALL──► MQPUT  ×2                   [Send response/error]
└──CALL──► MQCLOSE ×3                  [Close all queues]

CODATE01 (Date Service - CICS + MQ)
├──CALL──► MQOPEN ×3
├──CALL──► MQGET
├──EXEC CICS ASKTIME/FORMATTIME        [Get system date/time]
├──CALL──► MQPUT  ×2
└──CALL──► MQCLOSE ×3
```

### 1.5 Transaction Type DB2 Call Graph

```
COBTUPDT (Batch DB2 Maintenance)
├──EXEC SQL INCLUDE DCLTRTYP           [Transaction Type table DCL]
└──EXEC SQL INSERT/UPDATE/DELETE       [CARDDEMO.TRANSACTION_TYPE]

COTRTLIC (Online List - CICS + DB2)
├──EXEC SQL SELECT                     [Cursor-based pagination]
├──EXEC SQL INCLUDE DCLTRTYP
└──XCTL──► COTRTUPC                    [Navigate to update screen]

COTRTUPC (Online Update - CICS + DB2)
├──EXEC SQL INCLUDE DCLTRTYP, DCLTRCAT
├──EXEC SQL SELECT/UPDATE/DELETE       [TRANSACTION_TYPE, TRANSACTION_CATEGORY]
└──CALL DSNTIAC                        [DB2 error formatting]
```

---

## 2. Dataset Lineage — File Flow Map

### 2.1 Master VSAM Files

| Logical Name | Physical DSN Pattern | Format | Key | Created By | Read By | Updated By |
|-------------|---------------------|--------|-----|-----------|---------|------------|
| ACCTFILE | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | VSAM KSDS | ACCT-ID (9,11) | ACCTFILE.jcl (IDCAMS REPRO) | CBACT01C, CBTRN01C, CBTRN02C, CBEXPORT, CBACT04C, COACTUPC*, COACTVWC*, COBIL00C*, COPAUA0C*, COACCT01* | CBTRN02C (REWRITE), CBACT04C (REWRITE), COACTUPC* (REWRITE), COBIL00C* (REWRITE) |
| CARDFILE | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | VSAM KSDS | CARD-NUM (X,16) | CARDFILE.jcl | CBACT02C, CBTRN01C, CBEXPORT, COCRDLIC*, COCRDSLC*, COCRDUPC*, COACTUPC*, COACTVWC* | COCRDUPC* (REWRITE), CBIMPORT (WRITE) |
| CUSTFILE | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | VSAM KSDS | CUST-ID (9,09) | CUSTFILE.jcl | CBCUS01C, CBTRN01C, CBEXPORT, COACTUPC*, COACTVWC*, COCRDSLC*, COCRDUPC*, COPAUA0C* | CBIMPORT (WRITE) |
| XREFFILE | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | VSAM KSDS | XREF-CARD-NUM (X,16) | XREFFILE.jcl | CBACT03C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, CBACT04C, COTRN02C* | CBIMPORT (WRITE) |
| TRANSACT / TRANFILE | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | VSAM KSDS | TRAN-ID (X,16) | TRANFILE.jcl, COMBTRAN.jcl | CBTRN01C, CBTRN03C, CBEXPORT, COTRN00C*, COTRN01C*, COTRN02C* | CBTRN02C (WRITE), CBACT04C (WRITE), COTRN02C* (WRITE), COBIL00C* (WRITE) |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | VSAM KSDS | SEC-USR-ID (X,08) | DUSRSECJ.jcl | COSGN00C*, COUSR00C*, COUSR02C*, COUSR03C* | COUSR01C* (WRITE), COUSR02C* (REWRITE), COUSR03C* (DELETE) |

\* = CICS online access via EXEC CICS READ/WRITE/REWRITE/DELETE

### 2.2 Reference/Lookup VSAM Files

| Logical Name | Physical DSN Pattern | Format | Created By | Read By |
|-------------|---------------------|--------|-----------|---------|
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.VSAM | VSAM KSDS | TCATBALF.jcl | CBACT04C, CBTRN02C |
| TRANTYPE | AWS.M2.CARDDEMO.TRANTYPE.VSAM | VSAM KSDS | TRANTYPE.jcl | CBTRN03C |
| TRANCATG | AWS.M2.CARDDEMO.TRANCATG.VSAM | VSAM KSDS | TRANCATG.jcl | CBTRN03C |
| DISCGRP | AWS.M2.CARDDEMO.DISCGRP.VSAM | VSAM KSDS | DISCGRP.jcl | CBACT04C |

### 2.3 Transient / Batch I/O Files

| Logical Name | Physical DSN Pattern | Format | Producer JCL/Program | Consumer JCL/Program |
|-------------|---------------------|--------|---------------------|---------------------|
| DALYTRAN | AWS.M2.CARDDEMO.DALYTRAN.PS | Sequential (PS) | External feed (daily card transactions) | CBTRN01C (validate), CBTRN02C (post) |
| DALYREJS | AWS.M2.CARDDEMO.DALYREJS.PS | Sequential (PS) | CBTRN02C (rejected transactions) | *(manual review)* |
| TRANREPT | AWS.M2.CARDDEMO.TRANREPT.PS | Sequential (PS) | CBTRN03C (transaction report) | TXT2PDF1.JCL (convert to PDF) |
| EXPFILE | AWS.M2.CARDDEMO.EXPORT.DATA.PS | Sequential (PS) | CBEXPORT | CBIMPORT |
| DATEPARM | *(parameterized)* | Sequential (PS) | *(manual/scheduler)* | CBTRN03C (report date range) |
| OUTFILE / ARRYFILE / VBRCFILE | AWS.M2.CARDDEMO.ACCTDATA.* | Sequential (PS) | CBACT01C (account data in different formats) | *(analysis/verification)* |

### 2.4 IMS Database Segments

| Database | DBD Name | Segment | Format | Programs |
|----------|----------|---------|--------|----------|
| Auth DB | DBPAUTP0 | Root (Summary) | CIPAUSMY layout | COPAUA0C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS |
| Auth DB | DBPAUTP0 | Child (Detail) | CIPAUDTY layout | COPAUA0C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS |
| Auth DB Index | DBPAUTX0 | *(secondary index)* | | DBPAUTP0.jcl, UNLDPADB.JCL |

### 2.5 DB2 Tables

| Table | Schema | Programs | Operations |
|-------|--------|----------|------------|
| CARDDEMO.TRANSACTION_TYPE | CARDDEMO | COBTUPDT, COTRTLIC, COTRTUPC | SELECT, INSERT, UPDATE, DELETE |
| CARDDEMO.TRANSACTION_CATEGORY | CARDDEMO | COTRTUPC | SELECT (via DCLTRCAT) |
| CARDDEMO.AUTHFRDS | CARDDEMO | COPAUS2C | INSERT, UPDATE |

### 2.6 MQ Queues

| Queue | Direction | Programs |
|-------|-----------|----------|
| Auth Request Queue | Input | COPAUA0C (MQGET), COACCT01 (MQGET), CODATE01 (MQGET) |
| Auth Reply Queue | Output | COPAUA0C (MQPUT1) |
| Account Response Queue | Output | COACCT01 (MQPUT) |
| Account Error Queue | Output | COACCT01 (MQPUT) |
| Date Response Queue | Output | CODATE01 (MQPUT) |
| Date Error Queue | Output | CODATE01 (MQPUT) |

---

## 3. End-to-End Batch Pipeline Flow

The CardDemo batch processing follows a daily cycle with distinct phases:

### 3.1 One-Time Setup Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                     INITIAL ENVIRONMENT SETUP                       │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  1. DEFGDGB.jcl ────► Define GDG bases for backup versioning       │
│  2. DEFCUST.jcl ────► Define customer VSAM clusters                │
│  3. ACCTFILE.jcl ───► Define & load Account VSAM from PS           │
│  4. CARDFILE.jcl ───► Define & load Card VSAM + alt indexes        │
│  5. CUSTFILE.jcl ───► Define & load Customer VSAM                  │
│  6. XREFFILE.jcl ───► Define & load Cross-Ref VSAM + alt indexes   │
│  7. TRANFILE.jcl ───► Define & load Transaction VSAM + alt indexes │
│  8. TCATBALF.jcl ───► Define & load Tran Cat Balance VSAM          │
│  9. TRANTYPE.jcl ───► Define & load Transaction Type VSAM          │
│ 10. TRANCATG.jcl ───► Define & load Transaction Category VSAM     │
│ 11. DISCGRP.jcl ────► Define & load Discount Group VSAM            │
│ 12. DUSRSECJ.jcl ──► Define & load User Security VSAM              │
│ 13. DALYREJS.jcl ──► Define Daily Rejects file                     │
│ 14. REPTFILE.jcl ──► Define Report output file                     │
│ 15. CBADMCDJ.jcl ──► Define CICS CSD entries for online programs   │
│ 16. OPENFIL.jcl ───► Open all VSAM files in CICS                   │
│                                                                     │
│  [DB2 Sub-app]: CREADB21.jcl ──► Create Transaction Type DB2 tables│
│  [IMS Sub-app]: LOADPADB.JCL ──► Load IMS Auth database            │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.2 Daily Processing Pipeline

```
┌─────────────────────────────────────────────────────────────────────┐
│                   DAILY BATCH PROCESSING CYCLE                      │
├─────────────────────────────────────────────────────────────────────┤
│                                                                     │
│  PHASE 1: PREPARATION                                               │
│  ┌──────────────────────────┐                                       │
│  │ CLOSEFIL.jcl             │  Close VSAM files in CICS            │
│  │ DEFGDGD.jcl              │  Backup TRANTYPE, TRANCATG, DISCGRP  │
│  │ TRANBKP.jcl              │  Backup Transaction file             │
│  └──────────┬───────────────┘                                       │
│             │                                                       │
│  PHASE 2: TRANSACTION POSTING                                       │
│  ┌──────────▼───────────────┐                                       │
│  │ POSTTRAN.jcl             │  PGM=CBTRN02C                        │
│  │  Input:  DALYTRAN (daily)│  Read daily transaction feed          │
│  │  Lookup: XREFFILE        │  Validate card → account mapping     │
│  │  I-O:    ACCTFILE        │  Update account balances              │
│  │  I-O:    TCATBALF        │  Update category balances             │
│  │  Output: TRANFILE        │  Write posted transactions            │
│  │  Output: DALYREJS        │  Write rejected transactions          │
│  └──────────┬───────────────┘                                       │
│             │                                                       │
│  PHASE 3: INTEREST CALCULATION                                      │
│  ┌──────────▼───────────────┐                                       │
│  │ INTCALC.jcl              │  PGM=CBACT04C                        │
│  │  Input:  TCATBALF        │  Read category balances               │
│  │  Input:  XREFFILE        │  Cross-reference lookups              │
│  │  Input:  DISCGRP         │  Interest rate by group               │
│  │  I-O:    ACCTFILE        │  Update account with interest         │
│  │  Output: TRANSACT        │  Write interest transactions          │
│  └──────────┬───────────────┘                                       │
│             │                                                       │
│  PHASE 4: CONSOLIDATION                                             │
│  ┌──────────▼───────────────┐                                       │
│  │ COMBTRAN.jcl             │  SORT + IDCAMS                        │
│  │  Sort and merge daily    │  Combine into TRANSACT VSAM           │
│  │  transactions            │                                       │
│  └──────────┬───────────────┘                                       │
│             │                                                       │
│  PHASE 5: REPORTING                                                 │
│  ┌──────────▼───────────────┐                                       │
│  │ TRANREPT.jcl             │  SORT + PGM=CBTRN03C                  │
│  │  Sort transactions       │  Generate detail report               │
│  │  Read: TRANTYPE,TRANCATG │  Lookup type/category descriptions    │
│  │  Output: TRANREPT (text) │                                       │
│  ├──────────┬───────────────┤                                       │
│  │ TXT2PDF1.JCL             │  Convert report text → PDF            │
│  ├──────────┬───────────────┤                                       │
│  │ PRTCATBL.jcl             │  Print category balance summary       │
│  └──────────┬───────────────┘                                       │
│             │                                                       │
│  PHASE 6: DIAGNOSTICS (Optional)                                    │
│  ┌──────────▼───────────────┐                                       │
│  │ READACCT.jcl (CBACT01C)  │  Dump account data                    │
│  │ READCARD.jcl (CBACT02C)  │  Dump card data                       │
│  │ READCUST.jcl (CBCUS01C)  │  Dump customer data                   │
│  │ READXREF.jcl (CBACT03C)  │  Dump cross-ref data                  │
│  └──────────┬───────────────┘                                       │
│             │                                                       │
│  PHASE 7: REOPEN                                                    │
│  ┌──────────▼───────────────┐                                       │
│  │ OPENFIL.jcl              │  Reopen VSAM files in CICS           │
│  └──────────────────────────┘                                       │
│                                                                     │
└─────────────────────────────────────────────────────────────────────┘
```

### 3.3 Periodic/Ad-Hoc Pipelines

```
Branch Migration Export/Import:
  CBEXPORT.jcl ──► CBIMPORT.jcl
  (CUSTFILE + ACCTFILE + XREFFILE + TRANSACT + CARDFILE) ──► EXPFILE ──► (entity files)

Statement Generation:
  CREASTMT.JCL: SORT ──► IDCAMS REPRO ──► PGM=CBSTM03A ──► Statement + HTML files

IMS Auth DB Maintenance:
  UNLDPADB.JCL (PAUDBUNL) ──► flat files ──► LOADPADB.JCL (PAUDBLOD)
  CBPAUP0J.jcl (CBPAUP0C) ──► Purge expired authorizations
  DBPAUTP0.jcl ──► IMS DB unload for backup

DB2 Transaction Type Maintenance:
  TRANEXTR.jcl ──► Backup + extract transaction type data
  MNTTRDB2.jcl ──► Execute DB2 bind/maintenance
```

---

## 4. Copybook Dependency Matrix

Shows which copybooks are used by which programs (core `app/cbl/` only):

| Copybook | Used By Programs |
|----------|-----------------|
| COCOM01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| COTTL01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| CSDAT01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| CSMSG01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| DFHAID | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| DFHBMSCA | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| CSUSR01Y | COACTUPC, COADM01C, COCRDLIC, COMEN01C, COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| CVACT01Y | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C |
| CVACT03Y | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C |
| CVCUS01Y | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC |
| CVTRA05Y | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C |
| CVCRD01Y | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |
| CVACT02Y | CBACT02C, CBEXPORT, CBIMPORT, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |
| CSMSG02Y | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC |
| CSSTRPFY | COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |
| CSSETATY | COACTUPC (×3 with REPLACING) |
| CSUTLDWY | COACTUPC |
| CSLKPCDY | COACTUPC |
| CVTRA06Y | CBTRN01C, CBTRN02C |
| CVTRA01Y | CBACT04C, CBTRN02C |
| CVTRA02Y | CBACT04C |
| CVTRA03Y | CBTRN03C |
| CVTRA04Y | CBTRN03C |
| CVTRA07Y | CBTRN03C |
| CVEXPORT | CBEXPORT, CBIMPORT |
| CODATECN | CBACT01C |
| COADM02Y | COADM01C |
| COMEN02Y | COMEN01C |
| UNUSED1Y | *(not referenced by any program)* |
