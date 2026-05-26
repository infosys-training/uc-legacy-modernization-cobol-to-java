# Application Inventory — CardDemo COBOL Estate

## Overview

The CardDemo application is a multi-tier mainframe credit card management system built with COBOL, CICS, VSAM, IMS DB, Db2, and MQ. The estate comprises **44 COBOL programs**, **30+ copybooks**, and **43+ JCL jobs** across the core application and three sub-applications.

---

## 1. COBOL Programs — Core Application (`app/cbl/`)

### 1.1 Batch Programs

| # | Filename | Program ID | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|---------|---------------------|----------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read account VSAM file and write to flat, array, and variable-block output files | **Read:** ACCTFILE (VSAM KSDS) · **Write:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and print card data file | **Read:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and print account cross-reference data file | **Read:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Interest calculator — compute interest and fees on accounts using transaction category balances and disclosure group rates | **Read:** TCATBALF, XREFFILE, DISCGRP · **I-O:** ACCTFILE · **Read:** TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and print customer data file | **Read:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export customer, account, card, transaction, and xref data for branch migration into a single sequential export file | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · **Write:** EXPFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 7 | CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export file into individual VSAM files | **Read:** EXPFILE · **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 8 | CBSTM03A.CBL | CBSTM03A | Print account statements from transaction data (main driver) | **Write:** STMTFILE, HTMLFILE · **Calls:** CBSTM03B | COSTM01, CUSTREC, CVACT01Y, CVACT03Y |
| 9 | CBSTM03B.CBL | CBSTM03B | Subroutine — file processing for transaction statement report | **Read:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | *(none in app/cpy — uses inline definitions)* |
| 10 | CBTRN01C.cbl | CBTRN01C | Post records from daily transaction file to master transaction file, updating accounts | **Read:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE · **I-O:** ACCTFILE, TRANFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVTRA06Y |
| 11 | CBTRN02C.cbl | CBTRN02C | Post daily transactions — validate, reject bad records, update account balances and transaction category balances | **Read:** DALYTRAN, XREFFILE · **I-O:** ACCTFILE, TRANFILE, TCATBALF · **Write:** DALYREJS | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y |
| 12 | CBTRN03C.cbl | CBTRN03C | Print transaction detail report with type and category descriptions | **Read:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM · **Write:** TRANREPT | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | COBSWAIT | Utility — wait for a specified duration (parm in centiseconds) | **Calls:** MVSWAIT (assembler) | *(none)* |
| 14 | CSUTLDTC.cbl | CSUTLDTC | Date utility — convert and validate dates using LE callable services | **Calls:** CEEDAYS | *(none)* |

### 1.2 Online (CICS) Programs

| # | Filename | Program ID | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|---------|---------------------|----------------------|
| 1 | COSGN00C.cbl | COSGN00C | Sign-on screen — authenticate users against USRSEC file | **CICS READ:** USRSEC · **CICS XCTL:** to COMEN01C or COADM01C | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | COMEN01C.cbl | COMEN01C | Main menu for regular users — navigate to sub-functions | **CICS XCTL:** to selected program · **CICS INQUIRE** | COCOM01Y, COMEN01, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COADM01C.cbl | COADM01C | Admin menu for admin users — navigate to admin sub-functions | **CICS XCTL:** to selected program | COADM01, COADM02Y, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTVWC.cbl | COACTVWC | View account details — display account, card, and customer info | **CICS READ:** ACCTDAT, CARDDAT, CUSTDAT | COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, CSSTRPFY, DFHAID, DFHBMSCA |
| 5 | COACTUPC.cbl | COACTUPC | Update account details — validate and rewrite account, card, and customer records | **CICS READ/REWRITE:** ACCTDAT, CARDDAT, CUSTDAT, CARDAIX | COACTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSSTRPFY, CSUTLDPY, CSUTLDWY, CSUSR01Y, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 6 | COCRDLIC.cbl | COCRDLIC | List credit cards with browse/page forward/backward | **CICS STARTBR/READNEXT/READPREV/ENDBR:** CARDDAT, CARDAIX | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 7 | COCRDSLC.cbl | COCRDSLC | View credit card details — display single card and associated account | **CICS READ:** CARDDAT, ACCTDAT | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 8 | COCRDUPC.cbl | COCRDUPC | Update credit card details — validate and rewrite card record | **CICS READ/REWRITE:** CARDDAT, ACCTDAT | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 9 | COTRN00C.cbl | COTRN00C | List transactions from TRANSACT file with browse | **CICS STARTBR/READNEXT/READPREV/ENDBR:** TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | COTRN01C | View a single transaction from TRANSACT file | **CICS READ:** TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | COTRN02C | Add a new transaction to TRANSACT file | **CICS READ:** ACCTDAT, CARDXREF · **CICS WRITE:** TRANSACT · **Calls:** CSUTLDTC | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 12 | COBIL00C.cbl | COBIL00C | Bill payment — pay account balance (full or partial) and write transaction | **CICS READ/REWRITE:** ACCTDAT · **CICS STARTBR/READPREV/ENDBR/WRITE:** TRANSACT | COBIL00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | CORPT00C.cbl | CORPT00C | Transaction reports — submit batch report job via internal reader (INTRDR) | **CICS WRITEQ TD** · **Calls:** CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | COUSR00C | List all users from USRSEC file with browse | **CICS STARTBR/READNEXT/READPREV/ENDBR:** USRSEC | COCOM01Y, COTTL01Y, COUSR00, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | COUSR01C | Add a new user (regular or admin) to USRSEC file | **CICS WRITE:** USRSEC | COCOM01Y, COTTL01Y, COUSR01, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | COUSR02C | Update an existing user in USRSEC file | **CICS READ/REWRITE:** USRSEC | COCOM01Y, COTTL01Y, COUSR02, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | COUSR03C | Delete a user from USRSEC file | **CICS READ/DELETE:** USRSEC | COCOM01Y, COTTL01Y, COUSR03, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. COBOL Programs — Sub-Applications

### 2.1 Authorization Module (`app/app-authorization-ims-db2-mq/`)

| # | Filename | Program ID | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|----------------|---------|---------------------|----------------------|
| 1 | CBPAUP0C.cbl | CBPAUP0C | Batch (IMS BMP) | Delete expired pending authorization messages from IMS DB | **EXEC DLI:** GN, GNP, DLET, CHKP on PAUT PCB | CIPAUSMY, CIPAUDTY |
| 2 | COPAUA0C.cbl | COPAUA0C | Online (CICS/IMS/MQ) | Card authorization decision — read MQ request, lookup xref/account/customer, make auth decision, write auth to IMS DB, send MQ response | **MQ:** MQOPEN, MQGET, MQPUT1, MQCLOSE · **CICS READ:** CARDXREF, ACCTDAT, CUSTDAT · **EXEC DLI:** GU, REPL, ISRT, SCHD, TERM | CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CVACT01Y, CVACT03Y, CVCUS01Y |
| 3 | COPAUS0C.cbl | COPAUS0C | Online (CICS/IMS/BMS) | Summary view of authorization messages — browse IMS segments, display on BMS map | **EXEC DLI:** GNP, GU, SCHD, TERM · **CICS READ:** CARDXREF, ACCTDAT, CUSTDAT · **CICS SEND/RECEIVE** | CIPAUSMY, CIPAUDTY, COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 4 | COPAUS1C.cbl | COPAUS1C | Online (CICS/IMS/BMS) | Detail view of a single authorization message with update capability | **EXEC DLI:** GU, GNP, REPL, SCHD, TERM · **CICS LINK, SEND, RECEIVE** | CIPAUSMY, CIPAUDTY, COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA |
| 5 | COPAUS2C.cbl | COPAUS2C | Online (CICS/IMS/Db2) | Mark an authorization message as fraud — insert fraud record into Db2 AUTHFRDS table | **EXEC SQL:** INSERT into AUTHFRDS · **CICS ASKTIME/FORMATTIME** | CIPAUDTY |
| 6 | DBUNLDGS.CBL | DBUNLDGS | Batch (IMS DLI/GSAM) | Unload IMS pending authorization DB to GSAM sequential files | **CBLTDLI** calls to read IMS segments, write to GSAM | CIPAUSMY, CIPAUDTY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB |
| 7 | PAUDBLOD.CBL | PAUDBLOD | Batch (IMS DLI) | Load IMS pending authorization DB from flat files | **Read:** INFILE1, INFILE2 · **CBLTDLI** calls to ISRT segments | CIPAUSMY, CIPAUDTY, IMSFUNCS, PAUTBPCB |
| 8 | PAUDBUNL.CBL | PAUDBUNL | Batch (IMS DLI) | Unload IMS pending authorization DB to flat files | **Write:** OUTFIL1, OUTFIL2 · **CBLTDLI** calls to GN segments | CIPAUSMY, CIPAUDTY, IMSFUNCS, PAUTBPCB |

### 2.2 Transaction Type Module (`app/app-transaction-type-db2/`)

| # | Filename | Program ID | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|----------------|---------|---------------------|----------------------|
| 1 | COTRTLIC.cbl | COTRTLIC | Online (CICS/Db2) | List transaction types from Db2 for updates and deletes, with cursor-based pagination | **EXEC SQL:** SELECT, DELETE on TRANSACTION_TYPE, TRANSACTION_TYPE_CATEGORY · **CICS SEND/RECEIVE MAP** | COCOM01Y, COTRTLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 2 | COTRTUPC.cbl | COTRTUPC | Online (CICS/Db2) | Add/update transaction types in Db2 | **EXEC SQL:** INSERT, UPDATE, SELECT on TRANSACTION_TYPE · **CICS SEND/RECEIVE MAP** | COCOM01Y, COTRTUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSETATY, CSSTRPFY, CSUTLDWY, CSUSR01Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 3 | COBTUPDT.cbl | COBTUPDT | Batch (Db2) | Batch update transaction types from input file — insert, update, or delete based on action code | **Read:** INPFILE · **EXEC SQL:** INSERT, UPDATE, DELETE on TRANSACTION_TYPE | *(inline Db2 declarations)* |

### 2.3 VSAM/MQ Module (`app/app-vsam-mq/`)

| # | Filename | Program ID | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|----------------|---------|---------------------|----------------------|
| 1 | COACCT01.cbl | COACCT01 | Online (CICS/MQ) | Account inquiry via MQ — receive account query from request queue, read VSAM, send response | **CICS READ:** ACCTDAT · **MQ:** MQOPEN, MQGET, MQPUT, MQCLOSE | CVACT01Y, CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV |
| 2 | CODATE01.cbl | CODATE01 | Online (CICS/MQ) | Date service via MQ — receive date format request, return formatted date/time | **CICS ASKTIME/FORMATTIME** · **MQ:** MQOPEN, MQGET, MQPUT, MQCLOSE | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV |

---

## 3. JCL Jobs — Core Application (`app/jcl/`)

### 3.1 VSAM File Definition Jobs

| # | Job Name | File | Purpose | Step Sequence |
|---|----------|------|---------|---------------|
| 1 | ACCTFILE.jcl | ACCTDATA | Define account VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO from PS |
| 2 | CARDFILE.jcl | CARDDATA | Define card data VSAM KSDS with AIX | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO → STEP40: DEFINE AIX → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: SDSF open CICS files |
| 3 | CUSTFILE.jcl | CUSTDATA | Define customer VSAM KSDS | CLCIFIL: Close CICS → STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → OPCIFIL: Open CICS |
| 4 | XREFFILE.jcl | CARDXREF | Define card cross-reference VSAM KSDS with AIX | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX |
| 5 | TRANFILE.jcl | TRANSACT | Define transaction master VSAM KSDS with AIX | CLCIFIL: Close CICS → STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX → OPCIFIL: Open CICS |
| 6 | TCATBALF.jcl | TCATBALF | Define transaction category balance VSAM KSDS | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO |
| 7 | TRANTYPE.jcl | TRANTYPE | Define transaction type VSAM KSDS | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO |
| 8 | TRANCATG.jcl | TRANCATG | Define transaction category VSAM KSDS | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO |
| 9 | DISCGRP.jcl | DISCGRP | Define disclosure group VSAM KSDS | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO |
| 10 | DUSRSECJ.jcl | USRSEC | Define user security VSAM KSDS | PREDEL: IEFBR14 → STEP01: IEBGENER (PS→PS) → STEP02: IDCAMS DELETE/DEFINE → STEP03: IDCAMS REPRO |
| 11 | ESDSRRDS.jcl | ESDS/RRDS | Define ESDS and RRDS files | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02: IDCAMS (ESDS) → STEP03: IDCAMS (RRDS) |
| 12 | DEFCUST.jcl | CUSTDATA | Alternate customer file definition | STEP05: DELETE → STEP05: DEFINE CLUSTER |
| 13 | TRANIDX.jcl | TRANSACT AIX | Define alternate index on transaction master | STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX |

### 3.2 Batch Processing Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 14 | POSTTRAN.jcl | Post daily transactions — run CBTRN02C | CLCIFIL: Close CICS files → STEP05R: SORT daily trans → STEP05: CBTRN02C → OPCIFIL: Open CICS files |
| 15 | INTCALC.jcl | Calculate interest — run CBACT04C | CLCIFIL: Close CICS files → STEP05: CBACT04C → OPCIFIL: Open CICS files |
| 16 | TRANREPT.jcl | Generate transaction report — run CBTRN03C | STEP05R: REPROC → STEP05R: SORT → STEP10R: CBTRN03C |
| 17 | PRTCATBL.jcl | Print transaction category balance report | DELDEF: IEFBR14 → STEP05R: REPROC → STEP10R: SORT |
| 18 | READACCT.jcl | Read account file — run CBACT01C | PREDEL: IEFBR14 → STEP05: CBACT01C |
| 19 | READCARD.jcl | Read card file — run CBACT02C | STEP05: CBACT02C |
| 20 | READCUST.jcl | Read customer file — run CBCUS01C | STEP05: CBCUS01C |
| 21 | READXREF.jcl | Read cross-reference file — run CBACT03C | STEP05: CBACT03C |
| 22 | CBEXPORT.jcl | Export data for branch migration | STEP01: IDCAMS (REPRO TRANSACT to PS) → STEP02: CBEXPORT |
| 23 | CBIMPORT.jcl | Import data from branch migration | STEP01: CBIMPORT |
| 24 | CREASTMT.JCL | Create account statements | DELDEF01: IDCAMS → STEP010: SORT → STEP020: IDCAMS REPRO → STEP030: IEFBR14 → STEP040: CBSTM03A |
| 25 | COMBTRAN.jcl | Combine/sort transactions into master | STEP05R: SORT → STEP10: IDCAMS REPRO |
| 26 | TRANBKP.jcl | Backup and reset transaction master | STEP05R: REPROC → STEP05: IDCAMS REPRO → STEP10: IDCAMS DELETE/DEFINE |
| 27 | DALYREJS.jcl | Define GDG for daily rejection file | STEP05: IDCAMS DEFINE GDG |
| 28 | WAITSTEP.jcl | Execute wait utility (COBSWAIT) | WAIT: COBSWAIT |

### 3.3 Infrastructure & Utility Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 29 | CLOSEFIL.jcl | Close CICS files before batch processing | CLCIFIL: SDSF (CEMT SET FIL ... CLO) |
| 30 | OPENFIL.jcl | Open CICS files after batch processing | OPCIFIL: SDSF (CEMT SET FIL ... OPE ENA) |
| 31 | CBADMCDJ.jcl | Install CSD definitions for CICS programs | STEP1: DFHCSDUP |
| 32 | DEFGDGB.jcl | Define GDG bases for backup files | STEP05: IDCAMS DEFINE GDG |
| 33 | DEFGDGD.jcl | Define GDG bases for Db2 extract files | STEP10-STEP60: IDCAMS DEFINE GDG + IEBGENER seed |
| 34 | REPTFILE.jcl | Define GDG for report files | STEP05: IDCAMS DEFINE GDG |
| 35 | FTPJCL.JCL | FTP file transfer | STEP1: FTP |
| 36 | TXT2PDF1.JCL | Convert text reports to PDF | TXT2PDF: IKJEFT1B (REXX exec) |
| 37 | INTRDRJ1.JCL | Internal reader — trigger another JCL | IDCAMS → STEP01: IEBGENER to INTRDR |
| 38 | INTRDRJ2.JCL | Internal reader — create IMS physical VSAM | IDCAMS |

### 3.4 Sub-Application JCL

#### Authorization Module (`app/app-authorization-ims-db2-mq/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 39 | CBPAUP0J.jcl | Execute IMS BMP to delete expired authorizations | STEP01: DFSRRC00 (BMP,CBPAUP0C,PSBPAUTB) |
| 40 | DBPAUTP0.jcl | Unload IMS auth DB using DFSURGU0 utility | STEPDEL: IEFBR14 → UNLOAD: DFSRRC00 |
| 41 | LOADPADB.JCL | Load IMS auth DB from flat files | STEP01: DFSRRC00 (BMP,PAUDBLOD,PSBPAUTB) |
| 42 | UNLDGSAM.JCL | Unload IMS auth DB to GSAM files | STEP01: DFSRRC00 (DLI,DBUNLDGS,DLIGSAMP) |
| 43 | UNLDPADB.JCL | Unload IMS auth DB to flat files | STEP0: IEFBR14 → STEP01: DFSRRC00 (DLI,PAUDBUNL,PAUTBUNL) |

#### Transaction Type Module (`app/app-transaction-type-db2/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 44 | CREADB21.jcl | Create Db2 database, tables, and load reference data | FREEPLN: IKJEFT01 (free plans) → CRCRDDB: IKJEFT01 (create DB) → LDTTYPE: load tran types → LDTCCAT: load tran categories |
| 45 | MNTTRDB2.jcl | Maintain Db2 transaction type table (batch insert/update/delete) | STEP1: IKJEFT01 → RUN PROGRAM(COBTUPDT) |
| 46 | TRANEXTR.jcl | Extract Db2 reference data (tran types & categories) to PS files for report generation | STEP10: IEBGENER backup → STEP20: IEBGENER backup → STEP30: IEFBR14 delete → STEP40: IKJEFT01/DSNTIAUL extract types → STEP50: IKJEFT01/DSNTIAUL extract categories |
