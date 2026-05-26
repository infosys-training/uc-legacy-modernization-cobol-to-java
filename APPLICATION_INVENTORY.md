# APPLICATION_INVENTORY.md — CardDemo COBOL Estate

## Overview

The CardDemo application is a multi-tier mainframe credit card management system built with COBOL, CICS, VSAM, IMS DB, Db2, and MQ Series. It consists of **44 COBOL programs** across 4 sub-applications, **30 copybooks**, and **38 JCL jobs**.

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

### 1.1 Batch Programs

| # | Filename | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|---------------------|----------------------|
| 1 | **CBACT01C.cbl** (430 LOC) | Read account VSAM file and write into multiple output formats (sequential, array, variable-length) | READ ACCTFILE (KSDS), WRITE OUTFILE (seq), WRITE ARRYFILE (seq), WRITE VBRCFILE (VB seq) | CVACT01Y, CODATECN |
| 2 | **CBACT02C.cbl** (178 LOC) | Read and print card data file | READ CARDFILE (KSDS) → DISPLAY | CVACT02Y |
| 3 | **CBACT03C.cbl** (178 LOC) | Read and print account cross-reference data file | READ XREFFILE (KSDS) → DISPLAY | CVACT03Y |
| 4 | **CBACT04C.cbl** (652 LOC) | Interest calculator — reads transaction category balances, cross-references, discount groups; calculates interest and updates accounts | READ TCATBALF (KSDS), READ XREFFILE (KSDS random), READ ACCTFILE (KSDS random), READ DISCGRP (KSDS random), REWRITE ACCTFILE, WRITE TRANSACT (seq) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | **CBCUS01C.cbl** (178 LOC) | Read and print customer data file | READ CUSTFILE (KSDS) → DISPLAY | CVCUS01Y |
| 6 | **CBEXPORT.cbl** (582 LOC) | Export customer data for branch migration — reads all normalized CardDemo files and creates multi-record export file | READ CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE (all KSDS); WRITE EXPFILE (KSDS) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | **CBIMPORT.cbl** (487 LOC) | Import customer data from branch migration export — reads export file and writes individual entity files | READ EXPFILE (KSDS); WRITE CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT (all seq) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | **CBTRN01C.cbl** (494 LOC) | Post records from daily transaction file — validates and posts transactions to master file | READ DALYTRAN (seq), READ XREFFILE (KSDS random), READ ACCTFILE (KSDS random), WRITE TRANFILE (KSDS), WRITE DALYREJS (seq) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | **CBTRN02C.cbl** (731 LOC) | Post records from daily transaction file with enhanced validation — updates account balances and category balances | READ DALYTRAN (seq), READ XREFFILE (KSDS), READ ACCTFILE (KSDS), WRITE TRANFILE (KSDS), WRITE DALYREJS (seq), READ/REWRITE TCATBALF (KSDS) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | **CBTRN03C.cbl** (649 LOC) | Print the transaction detail report — generates formatted daily transaction report with page/account/grand totals | READ TRANFILE (KSDS seq), READ XREFFILE (KSDS random) → DISPLAY (print) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | **CBSTM03A.CBL** (924 LOC) | Print account statements from transaction data — main statement generation program | READ TRXFL (KSDS), READ XREFFILE (KSDS), READ CUSTFILE (KSDS), READ ACCTFILE (KSDS), WRITE STATEMNT (seq), WRITE HTMLFILE (seq) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 12 | **CBSTM03B.CBL** (230 LOC) | Subroutine for file processing related to transaction report (called by CBSTM03A) | File I/O operations delegated from CBSTM03A via CALL | COSTM01 |
| 13 | **COBSWAIT.cbl** (41 LOC) | Utility program — waits for specified centiseconds (parameter-driven delay) | CALL 'MVSWAIT' | _(none)_ |
| 14 | **CSUTLDTC.cbl** (157 LOC) | Date utility subroutine — converts between date formats using CEEDAYS LE callable service | CALL 'CEEDAYS' | _(none)_ |

### 1.2 Online (CICS) Programs

| # | Filename | Purpose | CICS Operations | Files Accessed (CICS READ/WRITE) | Copybooks Referenced |
|---|----------|---------|-----------------|----------------------------------|----------------------|
| 15 | **COSGN00C.cbl** (260 LOC) | Signon screen for CardDemo application — authenticates users | SEND MAP, RECEIVE MAP, READ USRSEC, XCTL to COADM01C/COMEN01C | COCOM01Y, COSGN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CSSTRPFY |
| 16 | **COADM01C.cbl** (288 LOC) | Admin menu for admin users — displays admin menu options | SEND MAP, RECEIVE MAP, XCTL to sub-programs | COCOM01Y, COADM01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, COADM02Y |
| 17 | **COMEN01C.cbl** (308 LOC) | Main menu for regular users — displays menu and routes to selected function | SEND MAP, RECEIVE MAP, XCTL to selected program | COCOM01Y, COMEN01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, COMEN02Y |
| 18 | **COACTVWC.cbl** (941 LOC) | Account view — accepts and processes account view requests | SEND MAP, RECEIVE MAP, READ ACCTDAT/CARDXREF/CUSTDAT | COCOM01Y, COACTVW (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVCRD01Y, CVACT01Y, CVACT03Y, CVCUS01Y |
| 19 | **COACTUPC.cbl** (4236 LOC) | Account update — accepts and processes account update requests (largest program) | SEND MAP, RECEIVE MAP, READ/REWRITE ACCTDAT/CARDXREF/CUSTDAT | COCOM01Y, COACTUP (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVCRD01Y, CVACT01Y, CVACT03Y, CVCUS01Y, CSUTLDWY, CSSETATY (×30) |
| 20 | **COCRDLIC.cbl** (1459 LOC) | List credit cards — browse and paginate card list with search | SEND MAP, RECEIVE MAP, STARTBR/READNEXT/READPREV/ENDBR CARDDAT, XCTL to COCRDSLC/COCRDUPC | COCOM01Y, COCRDLI (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVCRD01Y, CVACT02Y |
| 21 | **COCRDSLC.cbl** (887 LOC) | Credit card detail view — displays card detail with account/customer info | SEND MAP, RECEIVE MAP, READ CARDDAT/ACCTDAT/CARDXREF, XCTL | COCOM01Y, COCRDSL (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVCRD01Y, CVACT02Y, CVACT01Y, CVACT03Y, CVCUS01Y |
| 22 | **COCRDUPC.cbl** (1560 LOC) | Credit card update — accepts and processes card detail updates | SEND MAP, RECEIVE MAP, READ/REWRITE CARDDAT/ACCTDAT/CARDXREF, XCTL | COCOM01Y, COCRDUP (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVCRD01Y, CVACT02Y, CVACT01Y, CVACT03Y, CVCUS01Y |
| 23 | **COTRN00C.cbl** (699 LOC) | List transactions from TRANSACT file — browse transactions with filtering | SEND MAP, RECEIVE MAP, STARTBR/READNEXT/READPREV/ENDBR TRANSACT, XCTL | COCOM01Y, COTRN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVTRA05Y, CVACT03Y |
| 24 | **COTRN01C.cbl** (330 LOC) | View a transaction from TRANSACT file — displays transaction detail | SEND MAP, RECEIVE MAP, READ TRANSACT | COCOM01Y, COTRN01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVTRA05Y |
| 25 | **COTRN02C.cbl** (783 LOC) | Add a new transaction to TRANSACT file — validates and writes new transaction | SEND MAP, RECEIVE MAP, READ CARDXREF/ACCTDAT/CARDDAT, WRITE TRANSACT | COCOM01Y, COTRN02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVTRA05Y, CVACT02Y, CVACT03Y, CVACT01Y |
| 26 | **CORPT00C.cbl** (649 LOC) | Print transaction reports by submitting batch jobs | SEND MAP, RECEIVE MAP, CALL CSUTLDTC (date validation), START (submit batch) | COCOM01Y, CORPT00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUTLDPY |
| 27 | **COBIL00C.cbl** (572 LOC) | Bill payment — pay account balance in full and partial amounts | SEND MAP, RECEIVE MAP, READ/REWRITE ACCTDAT, READ CARDXREF, STARTBR/READPREV TRANSACT, WRITE TRANSACT | COCOM01Y, COBIL00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT03Y, CVTRA05Y |
| 28 | **COUSR00C.cbl** (695 LOC) | List all users from USRSEC file — browse/paginate user list | SEND MAP, RECEIVE MAP, STARTBR/READNEXT/READPREV/ENDBR USRSEC | COCOM01Y, COUSR00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y |
| 29 | **COUSR01C.cbl** (299 LOC) | Add a new Regular/Admin user to USRSEC file | SEND MAP, RECEIVE MAP, WRITE USRSEC | COCOM01Y, COUSR01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y |
| 30 | **COUSR02C.cbl** (414 LOC) | Update a user in USRSEC file | SEND MAP, RECEIVE MAP, READ/REWRITE USRSEC | COCOM01Y, COUSR02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y |
| 31 | **COUSR03C.cbl** (359 LOC) | Delete a user from USRSEC file | SEND MAP, RECEIVE MAP, READ/DELETE USRSEC | COCOM01Y, COUSR03 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y |

---

## 2. Sub-Application Programs

### 2.1 Authorization Sub-App (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|---------------------|----------------------|
| 32 | **COPAUA0C.cbl** (1026 LOC) | Card authorization decision — processes authorization requests via MQ, validates against IMS DB and VSAM files | Online (CICS/IMS/MQ) | MQOPEN/MQGET/MQPUT1/MQCLOSE (MQ), EXEC DLI GU/REPL/ISRT (IMS), EXEC CICS READ ACCTDAT/CARDXREF/CUSTDAT | CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y, CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV |
| 33 | **COPAUS0C.cbl** (1032 LOC) | Summary view of authorization messages — browses pending authorizations from IMS DB | Online (CICS/IMS/BMS) | EXEC DLI GNP/GU/SCHD/TERM, EXEC CICS READ ACCTDAT/CARDXREF/CUSTDAT, SEND/RECEIVE MAP | COCOM01Y, COPAU00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | **COPAUS1C.cbl** (604 LOC) | Detail view of authorization message — shows individual authorization details | Online (CICS/IMS/BMS) | EXEC DLI GU, EXEC CICS LINK, SEND/RECEIVE MAP | COCOM01Y, COPAU01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | **COPAUS2C.cbl** (244 LOC) | Mark authorization message as fraud — updates fraud flag in Db2 | Online (CICS/IMS/Db2) | EXEC SQL UPDATE AUTHFRDS, EXEC DLI operations | CIPAUDTY |
| 36 | **CBPAUP0C.cbl** (386 LOC) | Delete expired pending authorization messages — IMS batch purge | Batch (IMS) | EXEC DLI GN/GNP/DLET/CHKP | CIPAUSMY, CIPAUDTY |
| 37 | **PAUDBLOD.CBL** (369 LOC) | Load authorization IMS database from sequential file | Batch (IMS) | CALL CBLTDLI (GU/ISRT) — reads seq file, inserts into IMS DB | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 38 | **PAUDBUNL.CBL** (317 LOC) | Unload authorization IMS database to sequential file | Batch (IMS) | CALL CBLTDLI (GN/GNP) — reads IMS DB, writes seq file | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB |
| 39 | **DBUNLDGS.CBL** (366 LOC) | Unload IMS DB to GSAM (Generalized Sequential Access Method) | Batch (IMS) | CALL CBLTDLI (GN/GNP/ISRT) — reads IMS, writes GSAM | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB |

### 2.2 Transaction Type Sub-App (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|---------------------|----------------------|
| 40 | **COTRTLIC.cbl** (2098 LOC) | List transaction types for updates and deletes — Db2-backed CICS list screen | Online (CICS/Db2) | EXEC SQL SELECT/FETCH/OPEN/CLOSE on TRNTYPE/TRNTYCAT tables, SEND/RECEIVE MAP | COTRTLI (BMS), CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA |
| 41 | **COTRTUPC.cbl** (1702 LOC) | Accept and process transaction type updates — Db2-backed CICS update screen | Online (CICS/Db2) | EXEC SQL SELECT/UPDATE/INSERT/DELETE on TRNTYPE/TRNTYCAT, SEND/RECEIVE MAP | COTRTUP (BMS), CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA |
| 42 | **COBTUPDT.cbl** (237 LOC) | Batch update transaction type based on user input — Db2 batch | Batch (Db2) | EXEC SQL SELECT/UPDATE on TRNTYPE | CSDB2RPY, CSDB2RWY |

### 2.3 VSAM-MQ Sub-App (`app/app-vsam-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|---------------------|----------------------|
| 43 | **COACCT01.cbl** (620 LOC) | Account inquiry via MQ — reads account data from VSAM, responds via MQ queues | Batch (VSAM/MQ) | CALL MQOPEN/MQGET/MQPUT/MQCLOSE, VSAM file I/O | CMQODV, CMQMDV, CMQV, CMQTML |
| 44 | **CODATE01.cbl** (524 LOC) | Date validation service via MQ — receives date validation requests via MQ | Batch (VSAM/MQ) | CALL MQOPEN/MQGET/MQPUT/MQCLOSE | CMQODV, CMQMDV, CMQV, CMQTML |

---

## 3. JCL Job Catalog (`app/jcl/`)

### 3.1 VSAM File Definition & Load Jobs

| # | JCL Job | Steps | Purpose | Datasets |
|---|---------|-------|---------|----------|
| 1 | **ACCTFILE.jcl** | STEP05 (IDCAMS), STEP10 (IDCAMS), STEP15 (IDCAMS) | Define and load account VSAM KSDS from sequential file | ACCTDATA.PS → ACCTDATA.VSAM.KSDS |
| 2 | **CARDFILE.jcl** | CLCIFIL (SDSF close), STEP05–STEP15 (IDCAMS), STEP40 (IDCAMS) | Close CICS files, define and load card VSAM KSDS, define AIX | CARDDATA.PS → CARDDATA.VSAM.KSDS |
| 3 | **CUSTFILE.jcl** | CLCIFIL (SDSF close), STEP05–STEP15 (IDCAMS), OPCIFIL (SDSF open) | Close, define/load, reopen customer VSAM KSDS | CUSTDATA.PS → CUSTDATA.VSAM.KSDS |
| 4 | **XREFFILE.jcl** | STEP05–STEP15 (IDCAMS), STEP20–STEP30 (IDCAMS) | Define/load card cross-reference VSAM KSDS + AIX + PATH | CARDXREF.PS → CARDXREF.VSAM.KSDS |
| 5 | **TRANFILE.jcl** | CLCIFIL (SDSF close), STEP05–STEP20 (IDCAMS) | Close, define/load transaction VSAM KSDS, define AIX | DALYTRAN.PS.INIT → TRANSACT.VSAM.KSDS |
| 6 | **TRANTYPE.jcl** | STEP05–STEP15 (IDCAMS) | Define and load transaction type VSAM KSDS | TRANTYPE.PS → TRANTYPE.VSAM.KSDS |
| 7 | **TRANCATG.jcl** | STEP05–STEP15 (IDCAMS) | Define and load transaction category VSAM KSDS | TRANCATG.PS → TRANCATG.VSAM.KSDS |
| 8 | **TCATBALF.jcl** | STEP05–STEP15 (IDCAMS) | Define and load transaction category balance VSAM KSDS | TCATBALF.PS → TCATBALF.VSAM.KSDS |
| 9 | **DISCGRP.jcl** | STEP05–STEP15 (IDCAMS) | Define and load disclosure group VSAM KSDS | DISCGRP.PS → DISCGRP.VSAM.KSDS |
| 10 | **REPTFILE.jcl** | STEP05 (IDCAMS) | Define report output VSAM dataset | TRANSACT.DALY |
| 11 | **TRANIDX.jcl** | STEP20–STEP30 (IDCAMS) | Define AIX and PATH for transaction file | TRANSACT.VSAM.AIX, TRANSACT.VSAM.AIX.PATH |
| 12 | **DUSRSECJ.jcl** | PREDEL (IEFBR14), STEP01 (IEBGENER), STEP02–STEP03 (IDCAMS) | Create and load user security VSAM KSDS from inline data | USRSEC.PS → USRSEC.VSAM.KSDS |
| 13 | **ESDSRRDS.jcl** | PREDEL, STEP01 (IEBGENER), STEP02–STEP04 (IDCAMS) | Define ESDS and RRDS VSAM datasets for testing | ESDSRRDS.PS → USRSEC.VSAM.ESDS |
| 14 | **DEFCUST.jcl** | STEP05 (IDCAMS ×2) | Define customer VSAM cluster (alternate definition) | CUSTDATA.VSAM.KSDS |
| 15 | **DALYREJS.jcl** | STEP05 (IDCAMS) | Define daily rejects GDG dataset | DALYREJS GDG |

### 3.2 Batch Processing Jobs

| # | JCL Job | Steps | Purpose | Datasets |
|---|---------|-------|---------|----------|
| 16 | **READACCT.jcl** | PREDEL (IEFBR14), STEP05 (CBACT01C) | Pre-delete old outputs, run account reader/writer | ACCTDATA.VSAM.KSDS → ACCTDATA.PSCOMP, ACCTDATA.ARRYPS, ACCTDATA.VBPS |
| 17 | **READCARD.jcl** | STEP05 (CBACT02C) | Read and display card data | CARDDATA.VSAM.KSDS |
| 18 | **READCUST.jcl** | STEP05 (CBCUS01C) | Read and display customer data | CUSTDATA.VSAM.KSDS |
| 19 | **READXREF.jcl** | STEP05 (CBACT03C) | Read and display cross-reference data | CARDXREF.VSAM.KSDS |
| 20 | **POSTTRAN.jcl** | STEP15 (CBTRN02C) | Post daily transactions to master file | TRANSACT.VSAM.KSDS, DALYTRAN.PS, CARDXREF.VSAM.KSDS, DALYREJS(+1), ACCTDATA.VSAM.KSDS, TCATBALF.VSAM.KSDS |
| 21 | **INTCALC.jcl** | STEP15 (CBACT04C) | Calculate monthly interest on accounts | TCATBALF.VSAM.KSDS, CARDXREF.VSAM.KSDS, CARDXREF.VSAM.AIX.PATH, ACCTDATA.VSAM.KSDS, DISCGRP.VSAM.KSDS → SYSTRAN(+1) |
| 22 | **TRANREPT.jcl** | STEP05R (REPROC), STEP05R (SORT), STEP10R (CBTRN03C) | Backup transactions, sort by date, print daily report | TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1) → TRANSACT.DALY(+1) |
| 23 | **TRANBKP.jcl** | STEP05R (REPROC), STEP05–STEP10 (IDCAMS) | Backup transaction VSAM to GDG, delete/redefine VSAM | TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1) |
| 24 | **COMBTRAN.jcl** | STEP05R (SORT), STEP10 (IDCAMS) | Combine backup + system transactions, sort, reload VSAM | TRANSACT.BKUP(0) + SYSTRAN(0) → TRANSACT.COMBINED(+1) → TRANSACT.VSAM.KSDS |
| 25 | **PRTCATBL.jcl** | DELDEF (IEFBR14), STEP05R (REPROC), STEP10R (SORT) | Print transaction category balance report | TCATBALF.VSAM.KSDS → TCATBALF.BKUP(+1) → TCATBALF.REPT |
| 26 | **CREASTMT.JCL** | DELDEF01 (IDCAMS), STEP010 (SORT), STEP020 (IDCAMS), STEP030 (IEFBR14) | Create account statements — sort transactions, load VSAM, generate HTML/PS output | TRANSACT.VSAM.KSDS → TRXFL.SEQ → TRXFL.VSAM.KSDS; outputs STATEMNT.HTML, STATEMNT.PS |
| 27 | **CBEXPORT.jcl** | STEP01 (IDCAMS), STEP02 (CBEXPORT) | Run branch migration export program | CUSTDATA/ACCTDATA/CARDXREF/TRANSACT/CARDDATA.VSAM.KSDS → EXPORT.DATA |
| 28 | **CBIMPORT.jcl** | STEP01 (CBIMPORT) | Run branch migration import program | EXPORT.DATA → CUSTDATA/ACCTDATA/CARDXREF/TRANSACT.IMPORT + IMPORT.ERRORS |

### 3.3 GDG Definition Jobs

| # | JCL Job | Steps | Purpose |
|---|---------|-------|---------|
| 29 | **DEFGDGB.jcl** | STEP05 (IDCAMS) | Define GDG base for transaction backup |
| 30 | **DEFGDGD.jcl** | STEP10–STEP50 (IDCAMS/IEBGENER) | Define GDG bases for transaction type and category backups, initial load |

### 3.4 CICS Administration Jobs

| # | JCL Job | Steps | Purpose |
|---|---------|-------|---------|
| 31 | **CBADMCDJ.jcl** | STEP1 (DFHCSDUP) | Define CICS CSD entries (transactions and programs) for CardDemo |
| 32 | **CLOSEFIL.jcl** | CLCIFIL (SDSF) | Close CICS-owned VSAM files for batch processing |
| 33 | **OPENFIL.jcl** | OPCIFIL (SDSF) | Reopen CICS-owned VSAM files after batch processing |
| 34 | **WAITSTEP.jcl** | WAIT (COBSWAIT) | Wait step between close and open (batch pipeline pacing) |

### 3.5 Utility/Transfer Jobs

| # | JCL Job | Steps | Purpose |
|---|---------|-------|---------|
| 35 | **FTPJCL.JCL** | STEP1 (FTP) | FTP file transfer utility |
| 36 | **INTRDRJ1.JCL** | IDCAMS, STEP01 (IEBGENER) | Internal reader — backup and submit INTRDRJ2 |
| 37 | **INTRDRJ2.JCL** | IDCAMS | Internal reader — secondary IDCAMS operations |
| 38 | **TXT2PDF1.JCL** | Steps with SORT/IEBGENER | Convert text report to PDF format |

### 3.6 Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`)

| # | JCL Job | Purpose |
|---|---------|---------|
| 39 | **CBPAUP0J.jcl** | Run expired authorization purge (CBPAUP0C) |
| 40 | **DBPAUTP0.jcl** | IMS DB operations for authorization database |
| 41 | **LOADPADB.JCL** | Load authorization IMS database |
| 42 | **UNLDPADB.JCL** | Unload authorization IMS database |
| 43 | **UNLDGSAM.JCL** | Unload IMS DB via GSAM |

### 3.7 Sub-Application JCL (`app/app-transaction-type-db2/jcl/`)

| # | JCL Job | Purpose |
|---|---------|---------|
| 44 | **CREADB21.jcl** | Create Db2 tables for transaction types |
| 45 | **MNTTRDB2.jcl** | Maintain transaction type Db2 tables (refresh from VSAM) |
| 46 | **TRANEXTR.jcl** | Extract transaction types from Db2 |

---

## 4. Batch Pipeline Orchestration (Control-M)

The batch pipeline is orchestrated by Control-M, defined in `app/scheduler/CardDemo.controlm`:

### Daily Cycle (`DAILY-TransactionBackup`)
```
CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
```

### Weekly Cycle (`WEEKLY-TransactionTypesDBRefresh`)
Two smart folders run on Saturdays:
1. **DisclosureGroupsRefresh**: `CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL`
2. **TransactionTypesDBRefresh**: `TRANEXTR` (after MNTTRDB2 completes)

### Monthly Cycle (`MONTHLY-InterestCalculation`)
```
CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL
```

All cycles follow the **Close → Process → Open** pattern to ensure CICS files are released for batch processing.
