# Application Inventory — CardDemo COBOL Estate

## Overview

The CardDemo application is a multi-tier mainframe credit card management system built on COBOL, CICS, VSAM, IMS DB, Db2, and MQ. It consists of **44 COBOL programs** across the main application and three sub-applications, **30 copybooks**, and **46 JCL jobs**. The application manages credit card accounts, customers, transactions, authorizations, and user security.

---

## 1. Main Application Programs (`app/cbl/`)

### 1.1 Batch Programs

| # | Filename | Program ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|---------------------|----------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read the account VSAM file and write into flat files (PS, array, variable-block) | Batch | **Read:** ACCTFILE (VSAM KSDS) · **Write:** OUTFILE (PS), ARRYFILE (array PS), VBRCFILE (VB PS) | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and print card data file | Batch | **Read:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and print account cross-reference data file | Batch | **Read:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Interest calculator — compute interest charges per account based on category balances and discount groups | Batch | **Read:** TCATBALF (VSAM KSDS), XREFFILE (VSAM KSDS/AIX), DISCGRP (VSAM KSDS) · **Read/Update:** ACCTFILE (VSAM KSDS, I-O) · **Write:** TRANSACT (VSAM KSDS) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and print customer data file | Batch | **Read:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export customer, account, cross-ref, transaction, and card data for branch migration | Batch | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE (all VSAM KSDS) · **Write:** EXPFILE (sequential) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export file | Batch | **Read:** EXPFILE (sequential) · **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT (sequential) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | CBSTM03A | Print account statements from transaction data (main driver) | Batch | **Write:** STMT-FILE (statement PS), HTML-FILE (HTML output) · **Calls:** CBSTM03B for file reads | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | CBSTM03B | Subroutine — file processing for statement report (reads transaction, xref, customer, account files) | Batch (subroutine) | **Read:** TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE (all VSAM KSDS) | *(none in app/cpy — uses passed areas)* |
| 10 | CBTRN01C.cbl | CBTRN01C | Validate daily transactions against cross-ref and account files | Batch | **Read:** DALYTRAN (sequential), XREFFILE, ACCTFILE, CARDFILE, CUSTFILE, TRANSACT (VSAM) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | CBTRN02C | Post records from daily transaction file — update accounts, write transactions, generate rejects | Batch | **Read:** DALYTRAN (PS) · **Read/Update:** ACCTFILE (I-O), TCATBALF (I-O) · **Write:** TRANSACT, DALYREJS (GDG) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | CBTRN03C | Print the daily transaction detail report | Batch | **Read:** TRANSACT, XREFFILE, TRANTYPE, TRANCATG, DATEPARM (all VSAM/sequential) · **Write:** REPORT-FILE (GDG) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | COBSWAIT | Utility program — wait for a specified duration (parm in centiseconds) | Batch (utility) | *(none)* · **Calls:** MVSWAIT | *(none)* |
| 14 | CSUTLDTC.cbl | CSUTLDTC | Date validation and conversion utility — callable subroutine | Batch (utility/subroutine) | *(none)* · **Calls:** CEEDAYS (LE date intrinsic) | *(none)* |

### 1.2 Online (CICS) Programs

| # | Filename | Program ID | Purpose | Classification | Key I/O (CICS commands) | Copybooks Referenced |
|---|----------|-----------|---------|----------------|------------------------|----------------------|
| 15 | COSGN00C.cbl | COSGN00C | Sign-on screen for the CardDemo application | Online (CICS) | SEND/RECEIVE MAP, READ USRSEC file | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, DFHATTR |
| 16 | COMEN01C.cbl | COMEN01C | Main menu for regular users | Online (CICS) | SEND MAP, XCTL to sub-programs, INQUIRE | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COADM01C.cbl | COADM01C | Admin menu for administrator users | Online (CICS) | SEND MAP, XCTL to admin sub-programs | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | COACTVWC.cbl | COACTVWC | View account details — accept and display account information | Online (CICS) | READ ACCTDAT/CUSTDAT/CARDXREF/CARDDAT files, SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 19 | COACTUPC.cbl | COACTUPC | Update account details — accept and process account updates (credit limits, status, etc.) | Online (CICS) | READ/REWRITE ACCTDAT, CUSTDAT files, SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×30), CSSTRPFY, CSUTLDPY |
| 20 | COCRDLIC.cbl | COCRDLIC | List credit cards — browse and navigate card records | Online (CICS) | STARTBR/READNEXT/READPREV/ENDBR on CARDDAT/CARDAIX, SEND MAP, XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, COCRDLI, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 21 | COCRDSLC.cbl | COCRDSLC | View credit card detail — accept and display card information | Online (CICS) | READ CARDDAT/ACCTDAT/CARDXREF/CUSTDAT, SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 22 | COCRDUPC.cbl | COCRDUPC | Update credit card detail — accept and process card updates (name, status, expiry) | Online (CICS) | READ/REWRITE CARDDAT file, SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 23 | COTRN00C.cbl | COTRN00C | List transactions from TRANSACT file | Online (CICS) | STARTBR/READNEXT/ENDBR on TRANSACT, SEND MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | COTRN01C.cbl | COTRN01C | View a single transaction from TRANSACT file | Online (CICS) | READ TRANSACT file, SEND/RECEIVE MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN02C.cbl | COTRN02C | Add a new transaction to TRANSACT file | Online (CICS) | WRITE TRANSACT file, READ ACCTDAT/CARDXREF, SEND/RECEIVE MAP · **Calls:** CSUTLDTC | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 26 | COBIL00C.cbl | COBIL00C | Bill payment — pay account balance in full or partial amount | Online (CICS) | READ/UPDATE ACCTDAT, WRITE TRANSACT, SEND/RECEIVE MAP | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | CORPT00C.cbl | CORPT00C | Submit batch transaction report via CICS — writes JCL to transient data queue | Online (CICS) | WRITEQ TD (submit JCL), SEND MAP · **Calls:** CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 28 | COUSR00C.cbl | COUSR00C | List all users from USRSEC file | Online (CICS) | STARTBR/READNEXT/ENDBR on USRSEC, SEND MAP | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR01C.cbl | COUSR01C | Add a new regular/admin user to USRSEC file | Online (CICS) | WRITE USRSEC file, SEND/RECEIVE MAP | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, DFHATTR |
| 30 | COUSR02C.cbl | COUSR02C | Update an existing user in USRSEC file | Online (CICS) | READ/REWRITE USRSEC file, SEND/RECEIVE MAP | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | COUSR03C.cbl | COUSR03C | Delete a user from USRSEC file | Online (CICS) | READ/DELETE USRSEC file, SEND/RECEIVE MAP | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 Authorization / IMS / Db2 / MQ (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Program ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|---------------------|----------------------|
| 32 | CBPAUP0C.cbl | CBPAUP0C | Delete expired pending authorization messages from IMS DB | Batch (IMS) | IMS DL/I calls (GN, GNP, DLET) on PAUTHDB | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | COPAUA0C | Card authorization decision program — reads MQ request, validates card/account, sends MQ reply | Online (CICS/MQ/IMS) | MQGET (request queue), MQPUT1 (reply queue), CICS READ CARDXREF/ACCTDAT/CUSTDAT | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 34 | COPAUS0C.cbl | COPAUS0C | Summary view of pending authorization messages — browse IMS segments via CICS | Online (CICS/IMS/BMS) | CICS STARTBR/READNEXT/ENDBR on IMS-backed files, SEND MAP, SYNCPOINT | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | COPAUS1C | Detail view of a single authorization message | Online (CICS/IMS/BMS) | CICS LINK, READ IMS segments, SEND MAP | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | COPAUS2C | Mark an authorization message as fraud — updates Db2 table | Online (CICS/Db2) | EXEC SQL SELECT/UPDATE/INSERT on PAUTH_FRAUD table, ASKTIME/FORMATTIME | CIPAUDTY |
| 37 | DBUNLDGS.CBL | DBUNLDGS | Unload IMS PAUTHDB to GSAM sequential files (root + child segments) | Batch (IMS/GSAM) | IMS DL/I calls (GN, GNP), GSAM ISRT | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 38 | PAUDBLOD.CBL | PAUDBLOD | Load IMS PAUTHDB from flat files (root + child segments) | Batch (IMS) | READ INFILE1/INFILE2 (sequential), IMS DL/I calls (ISRT, GU) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | PAUDBUNL.CBL | PAUDBUNL | Unload IMS PAUTHDB to flat files (root + child segments) | Batch (IMS) | IMS DL/I calls (GN, GNP), WRITE OPFILE1/OPFILE2 (sequential) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

### 2.2 Transaction Type / Db2 (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Program ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|---------------------|----------------------|
| 40 | COTRTLIC.cbl | COTRTLIC | List transaction types for updates and deletes — CICS screen with Db2 cursor | Online (CICS/Db2) | EXEC SQL DECLARE CURSOR/OPEN/FETCH on TRANSACTION_TYPE, SEND/RECEIVE MAP, XCTL | CSDB2RWY, SQLCA, DCLTRTYP |
| 41 | COTRTUPC.cbl | COTRTUPC | Accept and process transaction type updates — CICS screen with Db2 DML | Online (CICS/Db2) | EXEC SQL SELECT/UPDATE on TRANSACTION_TYPE and TRAN_CATEGORY, SEND/RECEIVE MAP | DCLTRTYP, DCLTRCAT |
| 42 | COBTUPDT.cbl | COBTUPDT | Batch update of transaction type table from input file | Batch (Db2) | READ TR-RECORD (sequential), EXEC SQL INSERT/UPDATE/DELETE on TRANSACTION_TYPE | DCLTRTYP (via EXEC SQL INCLUDE) |

### 2.3 VSAM / MQ (`app/app-vsam-mq/cbl/`)

| # | Filename | Program ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|---------------------|----------------------|
| 43 | COACCT01.cbl | COACCT01 | Account inquiry via MQ — receives MQ request, reads VSAM account, sends MQ reply | Online (CICS/MQ) | MQOPEN/MQGET/MQPUT/MQCLOSE, CICS READ ACCTDAT, CICS RETRIEVE | CVACT01Y |
| 44 | CODATE01.cbl | CODATE01 | Date inquiry via MQ — receives MQ request, computes date, sends MQ reply | Online (CICS/MQ) | MQOPEN/MQGET/MQPUT/MQCLOSE, CICS ASKTIME/FORMATTIME, CICS RETRIEVE | *(none in app/cpy)* |

---

## 3. JCL Job Catalog (`app/jcl/`)

### 3.1 VSAM File Definition Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | ACCTFILE | Delete, define, and load Account VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO from PS |
| 2 | CARDFILE | Delete, define, and load Card Data VSAM KSDS with AIX | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO → STEP40: DEFINE AIX → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: SDSF open CICS files |
| 3 | CUSTFILE | Define Customer VSAM KSDS | CLCIFIL: SDSF close → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO → OPCIFIL: SDSF open |
| 4 | XREFFILE | Delete, define, and load Cross-Reference VSAM KSDS with AIX | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO from PS → STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX |
| 5 | TRANFILE | Define Transaction Master VSAM KSDS with AIX | CLCIFIL: SDSF close → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO from PS → STEP20–STEP30: AIX/PATH/BLDINDEX → OPCIFIL: SDSF open |
| 6 | TCATBALF | Define Transaction Category Balance VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO from PS |
| 7 | TRANCATG | Define Transaction Category VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO from PS |
| 8 | TRANTYPE | Define Transaction Type VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO from PS |
| 9 | DISCGRP | Define Disclosure Group VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO from PS |
| 10 | TRANIDX | Define AIX on Transaction Master | STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX |
| 11 | DEFCUST | Define Customer Data File (alternate) | STEP05: DELETE → STEP05: DEFINE |
| 12 | DUSRSECJ | Define User Security VSAM KSDS | PREDEL: IEFBR14 delete → STEP01: IEBGENER create PS → STEP02: IDCAMS DEFINE → STEP03: IDCAMS REPRO |
| 13 | ESDSRRDS | Define ESDS and RRDS VSAM files | PREDEL → STEP01: IEBGENER → STEP02–03: DEFINE+REPRO ESDS → STEP04–05: DEFINE+REPRO RRDS |

### 3.2 Batch Processing Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 14 | READACCT | Read and export Account file | PREDEL: IEFBR14 → STEP05: PGM=CBACT01C (reads ACCTFILE VSAM, writes PS/ARRY/VB files) |
| 15 | READCARD | Read and print Card file | STEP05: PGM=CBACT02C (reads CARDFILE VSAM) |
| 16 | READCUST | Read and print Customer file | STEP05: PGM=CBCUS01C (reads CUSTFILE VSAM) |
| 17 | READXREF | Read and print Cross-Ref file | STEP05: PGM=CBACT03C (reads XREFFILE VSAM) |
| 18 | INTCALC | Interest calculation | STEP15: PGM=CBACT04C (reads TCATBALF/XREF/DISCGRP, updates ACCTFILE, writes TRANSACT GDG) |
| 19 | POSTTRAN | Post daily transactions | STEP15: PGM=CBTRN02C (reads DALYTRAN, updates ACCTFILE/TCATBALF, writes TRANSACT/DALYREJS) |
| 20 | TRANREPT | Generate daily transaction report | STEP05R: REPROC backup TRANSACT → STEP05R: SORT transactions → STEP10R: PGM=CBTRN03C (produces report) |
| 21 | CREASTMT | Create account statements | DELDEF01: IDCAMS cleanup → STEP010: SORT TRANSACT → STEP020: REPRO to KSDS → STEP030: IEFBR14 delete old → STEP040: PGM=CBSTM03A (produces statement PS + HTML) |
| 22 | CBEXPORT | Export data for branch migration | STEP01: IDCAMS delete old export → STEP02: PGM=CBEXPORT (reads all VSAM files, writes export file) |
| 23 | CBIMPORT | Import data from branch migration | STEP01: PGM=CBIMPORT (reads export file, writes customer/account/xref/transaction/error files) |
| 24 | WAITSTEP | Wait utility | WAIT: PGM=COBSWAIT |
| 25 | COMBTRAN | Combine transaction backup with system transactions | STEP05R: SORT merge TRANSACT.BKUP + SYSTRAN → STEP10: IDCAMS REPRO combined into TRANSACT VSAM |

### 3.3 Backup and GDG Management Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 26 | TRANBKP | Backup and clear Transaction Master | STEP05R: REPROC VSAM→GDG backup → STEP05: IDCAMS DELETE records → STEP10: IDCAMS redefine |
| 27 | DEFGDGB | Define GDG base entries for backups | STEP05: IDCAMS DEFINE GDG bases |
| 28 | DEFGDGD | Define Db2 GDG + backup reference data | STEP10–60: IDCAMS DEFINE GDG + IEBGENER backup for TRANTYPE/TRANCATG/DISCGRP |
| 29 | DALYREJS | Define GDG for daily rejects | STEP05: IDCAMS DEFINE GDG |
| 30 | REPTFILE | Define GDG for report files | STEP05: IDCAMS DEFINE GDG |
| 31 | PRTCATBL | Print Transaction Category Balance report | DELDEF: IEFBR14 → STEP05R: REPROC → STEP10R: SORT and print |

### 3.4 CICS File Control Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 32 | CLOSEFIL | Close files in CICS region | CLCIFIL: SDSF (CEMT SET FIL CLO) |
| 33 | OPENFIL | Open files in CICS region | OPCIFIL: SDSF (CEMT SET FIL OPE) |

### 3.5 CSD and Utility Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 34 | CBADMCDJ | Add CardDemo CICS CSD definitions (transactions and programs) | STEP1: PGM=DFHCSDUP |
| 35 | FTPJCL | FTP transfer utility | STEP1: PGM=FTP |
| 36 | TXT2PDF1 | Convert text statement file to PDF | TXT2PDF: PGM=IKJEFT1B running TXT2PDF REXX |
| 37 | INTRDRJ1 | Internal reader — chain job submission (REPRO + submit INTRDRJ2) | IDCAMS: REPRO → STEP01: IEBGENER to INTRDR |
| 38 | INTRDRJ2 | Internal reader — secondary job (REPRO backup) | IDCAMS: REPRO |
| 39 | TRANEXTR | Extract transaction type reference data from Db2 | STEP10–20: IEBGENER backups → STEP30: IEFBR14 delete → STEP40: IKJEFT01 Db2 unload |

### 3.6 Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 40 | CBPAUP0J | Run IMS batch — purge expired pending authorizations | STEP01: PGM=DFSRRC00 (IMS BMP, runs CBPAUP0C) |
| 41 | DBPAUTP0 | Unload IMS PAUTHDB to sequential file via DL/I | STEPDEL: IEFBR14 → UNLOAD: PGM=DFSRRC00 (runs PAUDBUNL or DBUNLDGS) |
| 42 | LOADPADB | Load IMS PAUTHDB from flat files | STEP01: PGM=DFSRRC00 (runs PAUDBLOD) |
| 43 | UNLDGSAM | Unload IMS PAUTHDB to GSAM files | STEP01: PGM=DFSRRC00 (runs DBUNLDGS) |
| 44 | UNLDPADB | Unload IMS PAUTHDB to flat files | STEP0: IEFBR14 pre-delete → STEP01: PGM=DFSRRC00 (runs PAUDBUNL) |

### 3.7 Sub-Application JCL (`app/app-transaction-type-db2/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 45 | CREADB21 | Create Db2 TRANSACTION_TYPE table, bind plan | FREEPLN: IKJEFT01 free plan → CRCRDDB: IKJEFT01 create table → LDTTYPE: IEFBR14 → RUNTEP2: IKJEFT01 bind |
| 46 | MNTTRDB2 | Maintain transaction types via Db2 batch (runs COBTUPDT) | STEP1: PGM=IKJEFT01 (DSNTEP2, runs COBTUPDT) |

---

## 4. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Batch programs | 18 |
| Online (CICS) programs | 20 |
| CICS/IMS/MQ hybrid programs | 6 |
| Total lines of COBOL code | ~30,175 |
| Copybooks (app/cpy/) | 30 |
| Sub-app copybooks | 8 |
| JCL jobs (app/jcl/) | 38 |
| Sub-app JCL jobs | 8 |
| VSAM datasets managed | 10 |
| Db2 tables accessed | 3 |
| IMS databases | 1 (PAUTHDB) |
| MQ queues | 3+ (request/reply/error) |
