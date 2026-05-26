# Application Inventory — CardDemo COBOL Estate

## Overview

The CardDemo application is a multi-tier mainframe credit card management system built with COBOL, CICS, VSAM, DB2, IMS, and MQ. It consists of **44 COBOL programs** across 4 application modules, **30+ copybooks**, and **38+ JCL jobs** orchestrated by Control-M scheduling.

**Application modules:**
| Module | Path | Description |
|--------|------|-------------|
| Core CardDemo | `app/` | Main credit card application (online + batch) |
| Authorization (IMS/DB2/MQ) | `app/app-authorization-ims-db2-mq/` | Pending authorization processing via IMS DB and MQ |
| Transaction Type (DB2) | `app/app-transaction-type-db2/` | Transaction type maintenance via DB2 |
| VSAM-MQ Integration | `app/app-vsam-mq/` | Account inquiry and date validation via MQ |

---

## 1. Core Application Programs (`app/cbl/`)

### 1.1 Batch Programs

| # | Filename | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|---------------------|---------------------|
| 1 | `CBACT01C.cbl` | 430 | Read Account VSAM file; write to flat output, array, and variable-length files | **Read:** ACCTFILE (VSAM KSDS) **Write:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | `CBACT02C.cbl` | 178 | Read Card VSAM file sequentially and display records | **Read:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | `CBACT03C.cbl` | 178 | Read Card-Xref VSAM file sequentially and display records | **Read:** CARDXREF (VSAM KSDS) | CVACT03Y |
| 4 | `CBACT04C.cbl` | 652 | Interest calculation — compute interest on account balances, update transactions and accounts | **Read:** XREFFILE, TRANFILE, ACCTFILE **Write:** TRANFILE, ACCTFILE, XREFFILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | `CBCUS01C.cbl` | 178 | Read Customer VSAM file sequentially and display records | **Read:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | `CBEXPORT.cbl` | 582 | Export all VSAM data (customers, accounts, xrefs, transactions, cards) to a single sequential export file | **Read:** CUSTFILE, ACCTFILE, CARDXREF, TRANFILE, CARDFILE **Write:** EXPORT-FILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | `CBIMPORT.cbl` | 487 | Import data from sequential export file back into VSAM files | **Read:** IMPORT-FILE **Write:** CUSTFILE, ACCTFILE, CARDXREF, TRANFILE, CARDFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | `CBTRN01C.cbl` | 494 | Post daily transaction records — validate card, look up account via xref, update balances | **Read:** DALYTRAN (sequential), CUSTFILE, XREFFILE, CARDFILE, ACCTFILE **Write:** TRANFILE, ACCTFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | `CBTRN02C.cbl` | 731 | Validate and reject transactions — check card expiry, overlimit, invalid amounts | **Read:** DALYTRAN, XREFFILE, CARDFILE, ACCTFILE **Write:** TRANFILE, DALYREJS, ACCTFILE | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | `CBTRN03C.cbl` | 649 | Generate transaction reports — daily/monthly summaries with category breakdown | **Read:** TRANFILE, XREFFILE **Write:** Report output files | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | `CBSTM03A.CBL` | 924 | Generate credit card statements — read customer, account, and xref data; call CBSTM03B for page output | **Read:** CUSTFILE, ACCTFILE, XREFFILE **Write:** STMTFILE (statements) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 12 | `CBSTM03B.CBL` | 230 | Statement print subroutine — format and write individual statement pages (called by CBSTM03A) | **Write:** STMTFILE | _(none — uses LINKAGE SECTION)_ |
| 13 | `COBSWAIT.cbl` | 41 | Batch wait/timer utility — calls assembler MVSWAIT routine | **None** (calls MVSWAIT) | _(none)_ |

### 1.2 Online (CICS) Programs

| # | Filename | LOC | Purpose | CICS Resources Accessed | Copybooks Referenced |
|---|----------|-----|---------|------------------------|---------------------|
| 14 | `COSGN00C.cbl` | 260 | Sign-on screen — authenticate user against USRSEC file; route to Admin or Main menu | USRSEC (VSAM read) | COCOM01Y, COSGN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | `COMEN01C.cbl` | 308 | Main menu — display menu options and XCTL to selected program | _(navigation only)_ | COCOM01Y, COMEN02Y, COMEN01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA |
| 16 | `COADM01C.cbl` | 288 | Admin menu — display admin options (user CRUD, DB2 transaction types) and XCTL to selected program | _(navigation only)_ | COCOM01Y, COADM02Y, COADM01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | `COACTVWC.cbl` | 941 | Account View — display account details by account ID, look up via customer filter | ACCTDAT, CARDAIX (VSAM read) | COCOM01Y, COACTVW (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT02Y, CVACT03Y, DFHAID, DFHBMSCA |
| 18 | `COACTUPC.cbl` | 4,236 | Account Update — full CRUD for account records with extensive field validation | ACCTDAT, CARDAIX (VSAM read/update) | COCOM01Y, COACTUP (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CSUTLDWY, CSSETATY (×30+), DFHAID, DFHBMSCA |
| 19 | `COCRDLIC.cbl` | 1,459 | Credit Card List — browse/search cards by account, paginated display | CARDDAT, CARDAIX, ACCTDAT (VSAM browse) | COCOM01Y, COCRDLI (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 20 | `COCRDSLC.cbl` | 887 | Credit Card View — display selected card detail | CARDDAT, ACCTDAT (VSAM read) | COCOM01Y, COCRDSL (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 21 | `COCRDUPC.cbl` | 1,560 | Credit Card Update — edit card details with field-level validation | CARDDAT, ACCTDAT (VSAM read/update) | COCOM01Y, COCRDUP (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 22 | `COTRN00C.cbl` | 699 | Transaction List — paginated browse of TRANSACT file | TRANSACT (VSAM browse) | COCOM01Y, COTRN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | `COTRN01C.cbl` | 330 | Transaction View — display single transaction detail | TRANSACT (VSAM read) | COCOM01Y, COTRN01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | `COTRN02C.cbl` | 783 | Transaction Add — add new transaction with date validation | TRANSACT, CARDDAT, ACCTDAT (VSAM write) | COCOM01Y, COTRN02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVTRA05Y, CSUTLDPY, CSUTLDWY, DFHAID, DFHBMSCA |
| 25 | `CORPT00C.cbl` | 649 | Transaction Report selection — submit batch report requests | TRANSACT (VSAM read), submits internal reader | COCOM01Y, CORPT00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUTLDPY, DFHAID, DFHBMSCA |
| 26 | `COBIL00C.cbl` | 572 | Bill Payment — process bill payments against account balances | ACCTDAT, TRANSACT (VSAM read/write) | COCOM01Y, COBIL00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, DFHAID, DFHBMSCA |
| 27 | `COUSR00C.cbl` | 695 | User List — paginated browse of USRSEC security file | USRSEC (VSAM browse) | COCOM01Y, COUSR00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | `COUSR01C.cbl` | 299 | User Add — add new user to USRSEC file | USRSEC (VSAM write) | COCOM01Y, COUSR01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | `COUSR02C.cbl` | 414 | User Update — modify existing user record | USRSEC (VSAM read/update) | COCOM01Y, COUSR02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | `COUSR03C.cbl` | 359 | User Delete — remove user from USRSEC file | USRSEC (VSAM read/delete) | COCOM01Y, COUSR03 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | `CSUTLDTC.cbl` | 157 | Date utility — date format conversion and validation subroutine (called by COTRN02C, CORPT00C) | _(none — utility)_ | _(none)_ |

---

## 2. Authorization Sub-Application Programs (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|---------------------|
| 32 | `CBPAUP0C.cbl` | 386 | Batch (IMS BMP) | Delete expired pending authorization messages from IMS DB | **IMS DB:** PAUTBDB (DL/I GN, GNP, DLET) | CIPAUDTY, CIPAUSMY, PAUTBPCB |
| 33 | `COPAUA0C.cbl` | 1,026 | Online (CICS/MQ) | Authorization request processor — read MQ queue, validate card/account via VSAM, calculate fraud score, send approval/rejection response | **MQ:** MQOPEN, MQGET, MQPUT1, MQCLOSE **VSAM:** ACCTDAT, CARDDAT, CARDAIX (CICS READ) | CCPAURQY, CCPAURLY, CCPAUERY, CIPAUDTY, CIPAUSMY, COCOM01Y, CSDAT01Y, CSMSG01Y, COTTL01Y, COPAU00 (BMS), DFHAID, DFHBMSCA |
| 34 | `COPAUS0C.cbl` | 1,032 | Online (CICS) | Pending Authorization Summary screen — list pending authorizations from IMS DB via DB2 view | **DB2:** SELECT from AUTHFRDS **VSAM:** ACCTDAT, CARDDAT, CARDAIX | CCPAURQY, CCPAURLY, CIPAUDTY, CIPAUSMY, COCOM01Y, CSDAT01Y, CSMSG01Y, COPAU00 (BMS), COPAU01 (BMS), DFHAID, DFHBMSCA |
| 35 | `COPAUS1C.cbl` | 604 | Online (CICS) | Pending Authorization Detail screen — display single authorization detail, link to fraud analysis | **DB2:** SELECT from AUTHFRDS | CCPAURQY, CCPAURLY, CIPAUDTY, COCOM01Y, COPAU01 (BMS), DFHAID, DFHBMSCA |
| 36 | `COPAUS2C.cbl` | 244 | Online (CICS) | Pending Authorization Schedule — process scheduled authorization review | **DB2/CICS** | CIPAUDTY, CIPAUSMY |
| 37 | `DBUNLDGS.CBL` | 366 | Batch (IMS DLI) | Unload IMS DB segments to GSAM (sequential) output | **IMS DB:** DL/I GN, GNP **Write:** GSAM output | CIPAUDTY, CIPAUSMY, PADFLPCB, PASFLPCB, PAUTBPCB |
| 38 | `PAUDBLOD.CBL` | 369 | Batch (IMS DLI) | Load IMS DB from sequential input file | **Read:** Input sequential file **IMS DB:** DL/I GU, ISRT | CIPAUDTY, CIPAUSMY, PAUTBPCB |
| 39 | `PAUDBUNL.CBL` | 317 | Batch (IMS DLI) | Unload IMS DB to sequential output file | **IMS DB:** DL/I GN, GNP **Write:** Sequential output | CIPAUDTY, CIPAUSMY, PAUTBPCB |

---

## 3. Transaction Type Sub-Application Programs (`app/app-transaction-type-db2/cbl/`)

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|---------------------|
| 40 | `COBTUPDT.cbl` | 237 | Batch (DB2) | Batch update of transaction types in DB2 from input file | **Read:** INPFILE (sequential) **DB2:** INSERT/UPDATE on TRNTYPE table | SQLCA, DCLTRTYP |
| 41 | `COTRTLIC.cbl` | 2,098 | Online (CICS/DB2) | Transaction Type List — paginated browse of DB2 transaction type/category tables with inline editing | **DB2:** SELECT from TRNTYPE, TRNTYCAT | COCOM01Y, COTRTLI (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSDB2RPY, CSDB2RWY, DCLTRTYP, DCLTRCAT, DFHAID, DFHBMSCA |
| 42 | `COTRTUPC.cbl` | 1,702 | Online (CICS/DB2) | Transaction Type Update — add/update/delete transaction types and categories in DB2 | **DB2:** INSERT/UPDATE/DELETE on TRNTYPE, TRNTYCAT | COCOM01Y, COTRTUP (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSDB2RPY, CSDB2RWY, DCLTRTYP, DCLTRCAT, DFHAID, DFHBMSCA |

---

## 4. VSAM-MQ Integration Programs (`app/app-vsam-mq/cbl/`)

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|---------------------|
| 43 | `COACCT01.cbl` | 620 | Online (CICS/MQ) | Account inquiry via MQ — receive account inquiry request from MQ queue, look up VSAM, return response | **MQ:** MQOPEN, MQGET, MQPUT, MQCLOSE **VSAM:** ACCTDAT (CICS READ) | COCOM01Y, CSDAT01Y, DFHAID, DFHBMSCA |
| 44 | `CODATE01.cbl` | 524 | Online (CICS/MQ) | Date validation via MQ — receive date validation request from MQ queue, validate, return response | **MQ:** MQOPEN, MQGET, MQPUT, MQCLOSE | COCOM01Y, CSDAT01Y, DFHAID, DFHBMSCA |

---

## 5. JCL Job Catalog (`app/jcl/`)

### 5.1 VSAM File Definition and Load Jobs

| Job Name | Filename | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| ACCTFILE | `ACCTFILE.jcl` | Define and load Account VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO (load from PS) |
| CARDFILE | `CARDFILE.jcl` | Define and load Card VSAM KSDS with AIX | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE CLUSTER + AIX → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP40: DEFINE AIX → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: SDSF open CICS files |
| CUSTFILE | `CUSTFILE.jcl` | Define and load Customer VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| XREFFILE | `XREFFILE.jcl` | Define and load Card-Xref VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TRANFILE | `TRANFILE.jcl` | Define and load Transaction VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TCATBALF | `TCATBALF.jcl` | Define and load Transaction Category Balance VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TRANCATG | `TRANCATG.jcl` | Define and load Transaction Category VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TRANTYPE | `TRANTYPE.jcl` | Define and load Transaction Type VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| DISCGRP | `DISCGRP.jcl` | Define and load Disclosure Group VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| REPTFILE | `REPTFILE.jcl` | Define ESDS report output file | STEP05: DELETE → STEP10: DEFINE CLUSTER (ESDS) |

### 5.2 Batch Processing Jobs

| Job Name | Filename | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| POSTTRAN | `POSTTRAN.jcl` | Post daily transactions (run CBTRN01C) | STEP01: EXEC PGM=CBTRN01C (with DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE) |
| COMBTRAN | `COMBTRAN.jcl` | Validate/reject transactions (run CBTRN02C) | STEP01: EXEC PGM=CBTRN02C (with DALYTRAN, XREFFILE, CARDFILE, ACCTFILE, TRANFILE, DALYREJS) |
| INTCALC | `INTCALC.jcl` | Calculate monthly interest (run CBACT04C) | STEP01: EXEC PGM=CBACT04C (with XREFFILE, TRANFILE, ACCTFILE) |
| TRANREPT | `TRANREPT.jcl` | Generate transaction reports (run CBTRN03C via proc) | STEP01: EXEC PROC=TRANREPT → PGM=CBTRN03C |
| DALYREJS | `DALYREJS.jcl` | Define daily rejection output ESDS | STEP05: DELETE → STEP10: DEFINE CLUSTER (ESDS) |
| CREASTMT | `CREASTMT.JCL` | Generate credit card statements (run CBSTM03A) | STEP01: EXEC PGM=CBSTM03A (with CUSTFILE, ACCTFILE, XREFFILE, STMTFILE) |
| TRANBKP | `TRANBKP.jcl` | Backup transaction VSAM file to sequential | STEP01: IDCAMS REPRO from VSAM to PS |
| PRTCATBL | `PRTCATBL.jcl` | Print transaction category balances | STEP01: IDCAMS PRINT |

### 5.3 File Utility Jobs

| Job Name | Filename | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| CLOSEFIL | `CLOSEFIL.jcl` | Close CICS VSAM files for batch processing | STEP01: SDSF commands (CEMT SET FIL … CLO) |
| OPENFIL | `OPENFIL.jcl` | Reopen CICS VSAM files after batch | STEP01: SDSF commands (CEMT SET FIL … OPE) |
| WAITSTEP | `WAITSTEP.jcl` | Timed wait between batch steps (run COBSWAIT) | STEP01: EXEC PGM=COBSWAIT |
| READACCT | `READACCT.jcl` | Read account VSAM file (run CBACT01C) | STEP01: EXEC PGM=CBACT01C |
| READCARD | `READCARD.jcl` | Read card VSAM file (run CBACT02C) | STEP01: EXEC PGM=CBACT02C |
| READCUST | `READCUST.jcl` | Read customer VSAM file (run CBCUS01C) | STEP01: EXEC PGM=CBCUS01C |
| READXREF | `READXREF.jcl` | Read card-xref VSAM file (run CBACT03C) | STEP01: EXEC PGM=CBACT03C |
| CBEXPORT | `CBEXPORT.jcl` | Export all VSAM data to sequential file (run CBEXPORT) | STEP01: EXEC PGM=CBEXPORT |
| CBIMPORT | `CBIMPORT.jcl` | Import sequential file back to VSAM (run CBIMPORT) | STEP01: EXEC PGM=CBIMPORT |

### 5.4 Index and Alternate Index Jobs

| Job Name | Filename | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| TRANIDX | `TRANIDX.jcl` | Define alternate index on transaction file | IDCAMS DEFINE AIX + PATH + BLDINDEX |
| ESDSRRDS | `ESDSRRDS.jcl` | Define ESDS/RRDS clusters for special files | IDCAMS DEFINE |

### 5.5 Administrative / Utility Jobs

| Job Name | Filename | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| DEFGDGB | `DEFGDGB.jcl` | Define GDG base for transaction backups | IDCAMS DEFINE GDG |
| DEFGDGD | `DEFGDGD.jcl` | Define GDG base for daily data | IDCAMS DEFINE GDG |
| DEFCUST | `DEFCUST.jcl` | Define Customer VSAM with custom parameters | IDCAMS DEFINE |
| DUSRSECJ | `DUSRSECJ.jcl` | Define and load User Security VSAM file | DELETE → DEFINE → REPRO |
| CBADMCDJ | `CBADMCDJ.jcl` | Admin CICS NEWCOPY for online programs | SDSF commands: CEMT SET PROG NEWCOPY |
| FTPJCL | `FTPJCL.JCL` | FTP file transfer for branch migration | FTP steps |
| INTRDRJ1 | `INTRDRJ1.JCL` | Internal reader submit — JCL for submitting other jobs | Submit via internal reader |
| INTRDRJ2 | `INTRDRJ2.JCL` | Internal reader submit — alternate | Submit via internal reader |
| TXT2PDF1 | `TXT2PDF1.JCL` | Convert text report to PDF | IEBGENER or utility steps |

### 5.6 Authorization Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`)

| Job Name | Filename | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| CBPAUP0J | `CBPAUP0J.jcl` | Delete expired pending authorizations (run CBPAUP0C via IMS BMP) | STEP01: EXEC PGM=DFSRRC00, PARM='BMP,CBPAUP0C,PSBPAUTB' |
| DBPAUTP0 | `DBPAUTP0.jcl` | Unload IMS DB DBPAUTP0 to sequential | STEPDEL: IEFBR14 delete → UNLOAD: DFSRRC00 (DFSURGU0) |
| LOADPADB | `LOADPADB.JCL` | Load IMS pending auth DB from sequential (run PAUDBLOD) | STEP01: EXEC PGM=DFSRRC00, PARM='DLI,PAUDBLOD,PAUTBUNL' |
| UNLDPADB | `UNLDPADB.JCL` | Unload IMS pending auth DB (run PAUDBUNL) | STEP01: EXEC PGM=DFSRRC00, PARM='DLI,PAUDBUNL,PAUTBUNL' |
| UNLDGSAM | `UNLDGSAM.JCL` | Unload IMS DB to GSAM (run DBUNLDGS) | STEP01: EXEC PGM=DFSRRC00, PARM='DLI,DBUNLDGS' |

### 5.7 Transaction Type Sub-Application JCL (`app/app-transaction-type-db2/jcl/`)

| Job Name | Filename | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| CREADB21 | `CREADB21.jcl` | Create DB2 transaction type tables | SQL DDL: CREATE TABLE TRNTYPE, TRNTYCAT |
| MNTTRDB2 | `MNTTRDB2.jcl` | Maintain transaction types in DB2 (run COBTUPDT) | STEP01: EXEC PGM=COBTUPDT (with INPFILE) |
| TRANEXTR | `TRANEXTR.jcl` | Extract transaction types from DB2 to sequential | DB2 UNLOAD utility |

---

## 6. Control-M Scheduling (`app/scheduler/CardDemo.controlm`)

### Daily Pipeline: `DAILY-TransactionBackup`
```
CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
```
- Runs: ALL days
- Flow: Close CICS files → Backup transactions → Wait → Reopen files

### Weekly Pipeline: `WEEKLY-TransactionTypesDBRefresh`
```
MNTTRDB2 → (triggers two parallel Smart Folders)
  ├── DisclosureGroupsRefresh: CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL
  └── TransactionTypesDBRefresh: TRANEXTR
```
- Runs: Saturdays
- Flow: Maintain DB2 types → Refresh disclosure groups + Extract types

### Monthly Pipeline: `MONTHLY-InterestCalculation`
```
CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL
```
- Runs: Monthly
- Flow: Close files → Calculate interest → Combine/validate transactions → Wait → Reopen files
