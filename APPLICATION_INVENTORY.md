# APPLICATION INVENTORY

> **Application:** CardDemo -- Credit Card Management System (Mainframe)
> **Repository:** infosys-training/uc-legacy-modernization-cobol-to-java
> **Generated:** 2026-06-09

---

## Estate Summary

| Metric | Count |
|--------|-------|
| COBOL Programs | 44 |
| Copybooks (data) | 30 (app/cpy) + 17 (sub-app) = 47 total |
| BMS Map Copybooks | 19 (app/cpy-bms + sub-app cpy-bms) |
| JCL Jobs | 46 |
| Total Lines of COBOL | 30,175 |
| Average LOC per Program | 686 |
| Largest Program | COACTUPC.cbl (4,236 LOC) |

---

## 1. COBOL Program Inventory

### 1.1 Main Programs (`app/cbl/`) -- 31 Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | 430 | Read account file and split into output, array, and variable-block files | Batch | **Read:** ACCTFILE (VSAM KSDS) **Write:** OUTFILE, ARRFILE, VBRFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Read and print card data file | Batch | **Read:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Read and print account cross-reference data file | Batch | **Read:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Calculate interest and fees on account balances | Batch | **Read:** TCATBAL, XREFFILE, DISCGRP, ACCTFILE **Rewrite:** ACCTFILE **Write:** TRANFILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Read and print customer data file | Batch | **Read:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Export complete customer profiles from CardDemo files for branch migration | Batch | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE **Write:** EXPORT-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Import and validate exported data, split into normalized files | Batch | **Read:** EXPORT-INPUT **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | 924 | Generate customer account statements (text + HTML output) | Batch | **Read:** XREFFILE, CUSTFILE, ACCTFILE, TRANSACT **Write:** STMT-FILE, HTML-FILE | COSTM01 |
| 9 | CBSTM03B.CBL | 230 | I/O submodule for statement generation (called by CBSTM03A) | Batch (Module) | File I/O operations delegated from CBSTM03A | COSTM01 |
| 10 | CBTRN01C.cbl | 494 | Validate daily transactions against customer/account/card data | Batch | **Read:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | 731 | Post daily transactions to master file with validation and reject handling | Batch | **Read:** DALYTRAN, XREFFILE **Write:** TRANSACT, DALYREJS **Rewrite:** ACCTFILE, TCATBAL | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | 649 | Generate daily transaction report with category/type lookups | Batch | **Read:** TRANSACT, CARDXREF, TRANTYPE, TRANCATG, DATEPARM **Write:** REPTFILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COACTUPC.cbl | 4,236 | Account update -- exhaustive field validation (date, SSN, phone, state, ZIP) | Online (CICS) | **Read/Write:** ACCTFILE, CARDXREF, CUSTFILE (VSAM) | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y |
| 14 | COACTVWC.cbl | 941 | Account view -- display account details, card cross-references | Online (CICS) | **Read:** ACCTFILE, CARDXREF, CUSTFILE, CARDFILE (VSAM) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 15 | COADM01C.cbl | 288 | Admin menu -- hub for user management and transaction type functions | Online (CICS) | CICS screen navigation (XCTL to COUSR00C-03C, COTRTLIC, COTRTUPC) | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | 572 | Bill payment -- process payments against account balances | Online (CICS) | **Read:** ACCTFILE, CARDXREF, TRANSACT (VSAM) **Write:** TRANSACT **Rewrite:** ACCTFILE | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | 41 | Utility -- invoke assembler wait routine | Batch (Utility) | None (calls MVSWAIT assembler) | None |
| 18 | COCRDLIC.cbl | 1,459 | Credit card list -- paginated browse of cards (STARTBR/READNEXT/READPREV) | Online (CICS) | **Read:** CARDFILE (VSAM KSDS via STARTBR/READNEXT/READPREV/ENDBR) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y |
| 19 | COCRDSLC.cbl | 887 | Credit card view -- display card details with customer info | Online (CICS) | **Read:** CARDFILE, CUSTFILE (VSAM) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y |
| 20 | COCRDUPC.cbl | 1,560 | Credit card update -- edit card details with field validation | Online (CICS) | **Read/Write:** CARDFILE, CUSTFILE (VSAM) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y |
| 21 | COMEN01C.cbl | 308 | Main menu -- central hub routing to 11 user-facing functions | Online (CICS) | CICS screen navigation (XCTL to all user programs) | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 22 | CORPT00C.cbl | 649 | Report request -- validate date parameters and submit batch JCL via INTRDR | Online (CICS) | **Submits:** JCL INTRDRJ1/INTRDRJ2 via internal reader | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | 260 | Sign-on -- entry point; authenticate user, route to admin or main menu | Online (CICS) | **Read:** USRSEC (VSAM -- user security file) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | COTRN00C.cbl | 699 | Transaction list -- paginated browse of transactions | Online (CICS) | **Read:** TRANSACT (VSAM KSDS via STARTBR/READNEXT) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN01C.cbl | 330 | Transaction view -- display single transaction details | Online (CICS) | **Read:** TRANSACT (VSAM) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | 783 | Transaction add -- enter and validate new transactions | Online (CICS) | **Read:** CARDXREF (AIX), CCXREF **Write:** TRANSACT **Rewrite:** ACCTFILE | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | 695 | User list -- paginated browse of security users | Online (CICS) | **Read:** USRSEC (VSAM via STARTBR/READNEXT) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | 299 | User add -- create new user record | Online (CICS) | **Write:** USRSEC (VSAM) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR02C.cbl | 414 | User update -- modify existing user record | Online (CICS) | **Read/Write:** USRSEC (VSAM) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | 359 | User delete -- remove user record | Online (CICS) | **Delete:** USRSEC (VSAM) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | 157 | Date utility -- date validation and conversion (calls CEEDAYS) | Batch (Utility) | None (date calculation only) | CSUTLDPY, CSUTLDWY |

### 1.2 Sub-Application: Authorization IMS/DB2/MQ (`app/app-authorization-ims-db2-mq/cbl/`) -- 8 Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 32 | CBPAUP0C.cbl | 386 | Delete expired pending authorization messages from IMS DB | Batch (IMS) | **IMS:** GN, GNP, DLET on auth summary/detail segments | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | 1,026 | Authorization decision engine -- process auth requests via MQ, lookup IMS, write response | Online (CICS/MQ/IMS) | **MQ:** MQOPEN, MQGET, MQPUT1 **IMS:** GU, SCHD, TERM **Read:** ACCTFILE, CARDXREF, CUSTFILE | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 34 | COPAUS0C.cbl | 1,032 | Pending authorization summary browse -- paginated list view | Online (CICS/IMS) | **IMS:** GU, GNP on auth summary/detail segments | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | 604 | Pending authorization detail view -- view/update individual auth with fraud marking | Online (CICS/IMS) | **IMS:** GU, GNP, REPL on auth detail segment **CICS LINK:** to COPAUS2C | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | 244 | Fraud flag update via DB2 -- mark/unmark fraud on authorization record | Online (CICS/DB2) | **DB2:** EXEC SQL INSERT/UPDATE on fraud tables | CIPAUDTY |
| 37 | DBUNLDGS.CBL | 366 | IMS database unload to GSAM -- extract auth data for external processing | Batch (IMS/GSAM) | **IMS:** GN, GNP **GSAM:** ISRT (write segments to GSAM output) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 38 | PAUDBLOD.CBL | 369 | IMS database load -- load auth summary/detail segments into IMS DB | Batch (IMS) | **IMS:** ISRT, GU (load segments) **Read:** Input flat file | CIPAUSMY, CIPAUDTY, IMSFUNCS |
| 39 | PAUDBUNL.CBL | 317 | IMS database unload -- extract auth data from IMS to flat file | Batch (IMS) | **IMS:** GN, GNP (read segments) **Write:** Output flat file | CIPAUSMY, CIPAUDTY, IMSFUNCS |

### 1.3 Sub-Application: Transaction Type DB2 (`app/app-transaction-type-db2/cbl/`) -- 3 Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 40 | COTRTLIC.cbl | 2,098 | Transaction type list -- cursor-based DB2 pagination | Online (CICS/DB2) | **DB2:** DECLARE CURSOR, OPEN, FETCH, CLOSE on TRAN_TYPE/TRAN_CAT tables | CSDB2RPY, CSDB2RWY, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, COTRTLI |
| 41 | COTRTUPC.cbl | 1,702 | Transaction type update/delete -- CRUD with cascading deletes | Online (CICS/DB2) | **DB2:** SELECT, INSERT, UPDATE, DELETE on TRAN_TYPE/TRAN_CAT tables | CSDB2RPY, CSDB2RWY, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, COTRTUP |
| 42 | COBTUPDT.cbl | 237 | Batch transaction type table update utility | Batch (DB2) | **DB2:** UPDATE/INSERT on TRAN_TYPE table | CSDB2RPY, CSDB2RWY |

### 1.4 Sub-Application: VSAM/MQ (`app/app-vsam-mq/cbl/`) -- 2 Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | 620 | Account inquiry via MQ -- receive request, read VSAM, return response | Online (CICS/MQ) | **MQ:** MQOPEN, MQGET, MQPUT **Read:** ACCTFILE (VSAM) | CMQODV, CMQMDV, CVACT01Y |
| 44 | CODATE01.cbl | 524 | Date inquiry via MQ -- receive date format request, return formatted date | Online (CICS/MQ) | **MQ:** MQOPEN, MQGET, MQPUT | CMQODV, CMQMDV |

---

## 2. Program Classification Summary

| Classification | Count | % of Estate | Programs |
|----------------|-------|-------------|----------|
| Online (CICS only) | 16 | 36% | COSGN00C, COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| Online (CICS + DB2) | 3 | 7% | COTRTLIC, COTRTUPC, COPAUS2C |
| Online (CICS + IMS) | 2 | 5% | COPAUS0C, COPAUS1C |
| Online (CICS + MQ + IMS) | 1 | 2% | COPAUA0C |
| Online (CICS + MQ) | 2 | 5% | COACCT01, CODATE01 |
| Batch (Sequential/VSAM) | 12 | 27% | CBACT01C, CBACT02C, CBACT03C, CBACT04C, CBCUS01C, CBEXPORT, CBIMPORT, CBSTM03A, CBSTM03B, CBTRN01C, CBTRN02C, CBTRN03C |
| Batch (IMS) | 4 | 9% | CBPAUP0C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
| Batch (DB2) | 1 | 2% | COBTUPDT |
| Utility | 3 | 7% | COBSWAIT, CSUTLDTC, (CBSTM03B as submodule) |

---

## 3. JCL Job Catalog

### 3.1 Main JCL Jobs (`app/jcl/`) -- 38 Jobs

| # | Job Name | Purpose | Step Sequence | Programs/Utilities Executed | Datasets Referenced |
|---|----------|---------|---------------|---------------------------|-------------------|
| 1 | ACCTFILE.jcl | Define and load account VSAM file | STEP05: Delete cluster, STEP10: Define KSDS, STEP15: REPRO load | IDCAMS | ACCTDATA.VSAM.KSDS, ACCTDATA.PS |
| 2 | CARDFILE.jcl | Define and load card VSAM file with AIX | CLCIFIL: Close CICS files, STEP05: Delete cluster+AIX, STEP10: Define KSDS, STEP15: REPRO load, STEP40: Define AIX, STEP50: Define PATH, STEP60: BLDINDEX, OPCIFIL: Open CICS files | IDCAMS, SDSF | CARDDATA.VSAM.KSDS, CARDDATA.VSAM.AIX, CARDDATA.PS |
| 3 | CBADMCDJ.jcl | Create CICS CSD resource definitions for CardDemo | STEP1: DFHCSDUP | DFHCSDUP | DFHCSD (CICS CSD) |
| 4 | CBEXPORT.jcl | Export customer data for branch migration | STEP01: Define export VSAM cluster, STEP02: Run export program | IDCAMS, CBEXPORT | CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA, EXPORT.DATA |
| 5 | CBIMPORT.jcl | Import data from export file into normalized files | STEP01: Run import program | CBIMPORT | EXPORT.DATA, CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT, TRANSACT.IMPORT, IMPORT.ERRORS |
| 6 | CLOSEFIL.jcl | Close VSAM files in CICS region | CLCIFIL: Close TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC | SDSF | CICS file resources |
| 7 | COMBTRAN.jcl | Sort and combine transaction files | STEP05R: SORT current+system transactions, STEP10: REPRO to master | SORT, IDCAMS | TRANSACT.BKUP(0), SYSTRAN(0), TRANSACT.COMBINED(+1), TRANSACT.VSAM.KSDS |
| 8 | CREASTMT.JCL | Generate customer statements | Runs CBSTM03A | CBSTM03A | XREFFILE, CUSTFILE, ACCTFILE, TRANSACT, STMT-FILE, HTML-FILE |
| 9 | CUSTFILE.jcl | Define and load customer VSAM file | CLCIFIL: Close CICS, STEP05: Delete, STEP10: Define KSDS, STEP15: REPRO | IDCAMS, SDSF | CUSTDATA.VSAM.KSDS, CUSTDATA.PS |
| 10 | DALYREJS.jcl | Define daily rejection file | Define VSAM cluster for rejected transactions | IDCAMS | DALYREJS.VSAM.KSDS |
| 11 | DEFCUST.jcl | Define customer VSAM cluster (alternate) | Delete/Define KSDS | IDCAMS | CUSTDATA.VSAM.KSDS |
| 12 | DEFGDGB.jcl | Define GDG base for transaction backup | Define GDG base | IDCAMS | TRANSACT.BKUP (GDG) |
| 13 | DEFGDGD.jcl | Define GDG base for daily transactions | Define GDG base | IDCAMS | DALYTRAN (GDG) |
| 14 | DISCGRP.jcl | Define and load discount/interest rate group file | Delete/Define KSDS, REPRO load | IDCAMS | DISCGRP.VSAM.KSDS, DISCGRP.PS |
| 15 | DUSRSECJ.jcl | Define and load user security file | Delete/Define KSDS, REPRO load | IDCAMS | USRSEC.VSAM.KSDS, USRSEC.PS |
| 16 | ESDSRRDS.jcl | Define ESDS and RRDS experimental files | Define ESDS, Define RRDS | IDCAMS | ESDS, RRDS clusters |
| 17 | FTPJCL.JCL | FTP data files to/from mainframe | FTP operations | FTP | Various PS datasets |
| 18 | INTCALC.jcl | Calculate interest on account balances | Runs CBACT04C | CBACT04C | TCATBAL, XREFFILE, DISCGRP, ACCTFILE, TRANFILE |
| 19 | INTRDRJ1.JCL | Internal reader job 1 -- batch report submission | Submitted by CORPT00C | CBTRN03C | TRANSACT, REPTFILE |
| 20 | INTRDRJ2.JCL | Internal reader job 2 -- batch report submission (alternate) | Submitted by CORPT00C | CBTRN03C | TRANSACT, REPTFILE |
| 21 | OPENFIL.jcl | Open VSAM files in CICS region | Open TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC | SDSF | CICS file resources |
| 22 | POSTTRAN.jcl | Post daily transactions to master file | Runs CBTRN02C | CBTRN02C | DALYTRAN, TRANSACT, XREFFILE, DALYREJS, ACCTFILE, TCATBAL |
| 23 | PRTCATBL.jcl | Print transaction category balance file | Runs print utility | Utility | TCATBAL |
| 24 | READACCT.jcl | Read and print account file | Runs CBACT01C | CBACT01C | ACCTFILE |
| 25 | READCARD.jcl | Read and print card file | Runs CBACT02C | CBACT02C | CARDFILE |
| 26 | READCUST.jcl | Read and print customer file | Runs CBCUS01C | CBCUS01C | CUSTFILE |
| 27 | READXREF.jcl | Read and print cross-reference file | Runs CBACT03C | CBACT03C | XREFFILE |
| 28 | REPTFILE.jcl | Define report output file | Define PS dataset | IDCAMS/IEFBR14 | REPTFILE |
| 29 | TCATBALF.jcl | Define and load transaction category balance file | Delete/Define KSDS, REPRO | IDCAMS | TCATBALF.VSAM.KSDS, TCATBALF.PS |
| 30 | TRANBKP.jcl | Backup transaction file to GDG | REPRO to GDG | IDCAMS | TRANSACT.VSAM.KSDS, TRANSACT.BKUP(+1) |
| 31 | TRANCATG.jcl | Define and load transaction category file | Delete/Define KSDS, REPRO | IDCAMS | TRANCATG.VSAM.KSDS, TRANCATG.PS |
| 32 | TRANFILE.jcl | Define and load transaction VSAM file | Close CICS, Delete/Define KSDS, REPRO, Open CICS | IDCAMS, SDSF | TRANSACT.VSAM.KSDS, TRANSACT.PS |
| 33 | TRANIDX.jcl | Define alternate index on transaction file | Define AIX, PATH, BLDINDEX | IDCAMS | TRANSACT.VSAM.AIX |
| 34 | TRANREPT.jcl | Generate transaction report | Runs CBTRN03C | CBTRN03C | TRANSACT, CARDXREF, TRANTYPE, TRANCATG, REPTFILE |
| 35 | TRANTYPE.jcl | Define and load transaction type file | Delete/Define KSDS, REPRO | IDCAMS | TRANTYPE.VSAM.KSDS, TRANTYPE.PS |
| 36 | TXT2PDF1.JCL | Convert text report to PDF | Text-to-PDF conversion | Conversion utility | Text reports, PDF output |
| 37 | WAITSTEP.jcl | Wait/timer step (batch scheduling) | Execute wait program | COBSWAIT | None |
| 38 | XREFFILE.jcl | Define and load card cross-reference VSAM file | Close CICS, Delete/Define KSDS, REPRO, Open CICS | IDCAMS, SDSF | CARDXREF.VSAM.KSDS, CARDXREF.PS |

### 3.2 Sub-Application JCL Jobs -- 8 Jobs

| # | Job Name | Location | Purpose | Programs Executed |
|---|----------|----------|---------|-------------------|
| 39 | CBPAUP0J.jcl | app/app-authorization-ims-db2-mq/jcl/ | Execute IMS batch to delete expired authorizations | DFSRRC00 (BMP) -> CBPAUP0C |
| 40 | DBPAUTP0.jcl | app/app-authorization-ims-db2-mq/jcl/ | Unload IMS DBD DBPAUTP0 | IEFBR14, DFSRRC00 (ULU) |
| 41 | LOADPADB.JCL | app/app-authorization-ims-db2-mq/jcl/ | Load pending authorization IMS database | DFSRRC00 -> PAUDBLOD |
| 42 | UNLDGSAM.JCL | app/app-authorization-ims-db2-mq/jcl/ | Unload IMS auth DB to GSAM | DFSRRC00 -> DBUNLDGS |
| 43 | UNLDPADB.JCL | app/app-authorization-ims-db2-mq/jcl/ | Unload pending authorization IMS database | DFSRRC00 -> PAUDBUNL |
| 44 | CREADB21.jcl | app/app-transaction-type-db2/jcl/ | Create DB2 tables for transaction types | DB2 DDL |
| 45 | MNTTRDB2.jcl | app/app-transaction-type-db2/jcl/ | Maintain transaction type DB2 tables | COBTUPDT |
| 46 | TRANEXTR.jcl | app/app-transaction-type-db2/jcl/ | Extract transaction type data from DB2 | DB2 utility |

---

## 4. External Dependencies

| Dependency | Type | Called By | Purpose |
|------------|------|-----------|---------|
| COBDATFT | Assembler | CBACT01C | Date formatting |
| CEE3ABD | LE Runtime | CBACT02C, CBACT03C, CBCUS01C | Abnormal termination |
| CEEDAYS | LE Runtime | CSUTLDTC | Date conversion (Julian/Gregorian) |
| MVSWAIT | Assembler | COBSWAIT | System wait/timer |
| MQOPEN/MQGET/MQPUT1 | IBM MQ API | COPAUA0C, COACCT01, CODATE01 | Message queue operations |
| CBLTDLI | IMS DL/I | DBUNLDGS, PAUDBLOD, PAUDBUNL, CBPAUP0C | IMS database access |
| DFHCSDUP | CICS Utility | CBADMCDJ.jcl | CSD resource definition |
| DFSRRC00 | IMS Region Controller | IMS JCL jobs | IMS BMP/DLI batch execution |
