# APPLICATION INVENTORY — CardDemo COBOL Estate

## Overview

The CardDemo application is a multi-tier mainframe credit card management system consisting of **44 COBOL programs** across 4 sub-applications, **37 JCL jobs** in `app/jcl/`, and scheduled batch orchestration via Control-M.

---

## 1. Programs in `app/cbl/` (Core Application)

### 1.1 Batch Programs

| # | Filename | Program-ID | Purpose | Lines | Key I/O (Files Read/Written) | Copybooks Referenced |
|---|----------|-----------|---------|-------|------------------------------|---------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read account VSAM file and write into multiple output files (sequential, array, variable-length) | 430 | **R:** ACCTFILE (VSAM KSDS) · **W:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and print card data file | 178 | **R:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and print account cross-reference data file | 178 | **R:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Interest calculator — computes interest on account balances using disclosure group rates | 652 | **R:** TCATBALF, XREFFILE, ACCTFILE, DISCGRP, TRANSACT · **W:** TCATBALF, ACCTFILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and print customer data file | 178 | **R:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export customer data for branch migration into multi-record export file | 582 | **R:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · **W:** EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export file, split into normalized files | 487 | **R:** EXPFILE · **W:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBTRN01C.cbl | CBTRN01C | Post records from daily transaction file to master files | 494 | **R:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE · **W:** ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | CBTRN02C.cbl | CBTRN02C | Post daily transactions — validates, rejects bad records, updates category balances | 731 | **R:** DALYTRAN, XREFFILE, ACCTFILE · **W:** TRANFILE, DALYREJS, TCATBALF | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | CBTRN03C | Print the transaction detail report | 649 | **R:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM · **W:** TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | CBSTM03A.CBL | CBSTM03A | Print account statements from transaction data (main driver) | 924 | **R:** (via CBSTM03B) · **W:** STMTFILE, HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 12 | CBSTM03B.CBL | CBSTM03B | File processing subroutine for transaction statement report | 230 | **R:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | (inline) |
| 13 | COBSWAIT.cbl | COBSWAIT | Utility program — wait for specified centiseconds (scheduling delay) | 41 | None (calls MVSWAIT) | — |
| 14 | CSUTLDTC.cbl | CSUTLDTC | Date utility — convert/validate dates using CEEDAYS | 157 | None (callable subroutine) | — |

### 1.2 Online (CICS) Programs

| # | Filename | Program-ID | Purpose | Lines | Key I/O (VSAM via CICS File Control) | Copybooks Referenced |
|---|----------|-----------|---------|-------|--------------------------------------|---------------------|
| 1 | COSGN00C.cbl | COSGN00C | Signon screen — authenticate users via USRSEC file | 260 | USRSEC (READ) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | COMEN01C.cbl | COMEN01C | Main menu for regular users — route to functional programs | 308 | — (navigation only) | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COADM01C.cbl | COADM01C | Admin menu for admin users | 288 | — (navigation only) | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTVWC.cbl | COACTVWC | View account details | 941 | ACCTDAT, CARDXREF, CUSTDAT (READ) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 5 | COACTUPC.cbl | COACTUPC | Update account — accept and process account modifications | 4236 | ACCTDAT (READ/REWRITE), CARDXREF, CUSTDAT | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY, CSSTRPFY, CSUTLDPY |
| 6 | COCRDLIC.cbl | COCRDLIC | List credit cards — browse card records with pagination | 1459 | CARDDAT, CXACAIX (BROWSE) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | COCRDSLC.cbl | COCRDSLC | View credit card detail | 887 | CARDDAT, CUSTDAT (READ) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | COCRDUPC.cbl | COCRDUPC | Update credit card details | 1560 | CARDDAT (READ/REWRITE), CUSTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | COTRN00C.cbl | COTRN00C | List transactions from TRANSACT file with pagination | 699 | TRANSACT (BROWSE) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | COTRN01C | View a single transaction record | 330 | TRANSACT (READ) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | COTRN02C | Add a new transaction record | 783 | TRANSACT (WRITE), ACCTDAT, CARDXREF (READ) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | COBIL00C.cbl | COBIL00C | Bill payment — pay account balance (full/partial) | 572 | ACCTDAT (READ/REWRITE), TRANSACT (WRITE), CARDXREF (READ) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | CORPT00C.cbl | CORPT00C | Submit batch transaction reports via CICS transient data queues | 649 | CSSD TD Queue (WRITEQ) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | COUSR00C | List all users from USRSEC file | 695 | USRSEC (BROWSE) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | COUSR01C | Add a new user to USRSEC file | 299 | USRSEC (WRITE) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | COUSR02C | Update a user in USRSEC file | 414 | USRSEC (READ/REWRITE) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | COUSR03C | Delete a user from USRSEC file | 359 | USRSEC (READ/DELETE) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application: `app/app-authorization-ims-db2-mq/`

### 2.1 Programs

| # | Filename | Program-ID | Type | Purpose | Lines | Key I/O | Copybooks |
|---|----------|-----------|------|---------|-------|---------|-----------|
| 1 | CBPAUP0C.cbl | CBPAUP0C | Batch IMS | Delete expired pending authorization messages from IMS DB | 386 | IMS DB (GN/GNP/DLET/CHKP) | CIPAUSMY, CIPAUDTY |
| 2 | COPAUA0C.cbl | COPAUA0C | CICS IMS MQ | Card authorization decision — read request from MQ, validate against IMS/VSAM, send response | 1026 | MQ (GET/PUT1), IMS (SCHD/TERM), VSAM (ACCTDAT, CARDXREF, CUSTDAT) | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 3 | COPAUS0C.cbl | COPAUS0C | CICS IMS BMS | Summary view of authorization messages — browse IMS segments | 1032 | IMS (GNP), BMS Screen | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 4 | COPAUS1C.cbl | COPAUS1C | CICS IMS BMS | Detail view of a single authorization message | 604 | IMS (via LINK), BMS Screen | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 5 | COPAUS2C.cbl | COPAUS2C | CICS IMS DB2 | Mark authorization message as fraud — write to DB2 AUTHFRDS table | 244 | DB2 (SQL INSERT/SELECT), IMS | CIPAUDTY |
| 6 | DBUNLDGS.CBL | DBUNLDGS | Batch IMS | Unload IMS DB segments to GSAM (generalized sequential) files | 366 | IMS (GN/GNP/ISRT) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 7 | PAUDBLOD.CBL | PAUDBLOD | Batch IMS | Load pending authorization data from flat files into IMS DB | 369 | **R:** INFILE1, INFILE2 · IMS (ISRT/GU) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | PAUDBUNL.CBL | PAUDBUNL | Batch IMS | Unload pending authorization data from IMS DB to flat files | 317 | IMS (GN/GNP) · **W:** OUTFIL1, OUTFIL2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

---

## 3. Sub-Application: `app/app-transaction-type-db2/`

| # | Filename | Program-ID | Type | Purpose | Lines | Key I/O | Copybooks |
|---|----------|-----------|------|---------|-------|---------|-----------|
| 1 | COBTUPDT.cbl | COBTUPDT | Batch DB2 | Batch update transaction types from flat file into DB2 | 237 | **R:** INPFILE · DB2 (INSERT/UPDATE) | DCLTRTYP (SQL INCLUDE) |
| 2 | COTRTLIC.cbl | COTRTLIC | CICS DB2 | List transaction types for updates/deletes (online screen) | 2098 | DB2 (SELECT cursor) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, SQLCA, DCLTRTYP |
| 3 | COTRTUPC.cbl | COTRTUPC | CICS DB2 | Accept and process transaction type updates/inserts/deletes | 1702 | DB2 (SELECT/INSERT/UPDATE/DELETE) | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT |

---

## 4. Sub-Application: `app/app-vsam-mq/`

| # | Filename | Program-ID | Type | Purpose | Lines | Key I/O | Copybooks |
|---|----------|-----------|------|---------|-------|---------|-----------|
| 1 | COACCT01.cbl | COACCT01 | CICS MQ | Account inquiry via MQ — get request from queue, read VSAM, put response | 620 | MQ (OPEN/GET/PUT/CLOSE ×3 queues), VSAM ACCTDAT (CICS READ) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 2 | CODATE01.cbl | CODATE01 | CICS MQ | Date service via MQ — get request, format date via CICS ASKTIME/FORMATTIME, put response | 524 | MQ (OPEN/GET/PUT/CLOSE ×3 queues) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 5. JCL Jobs in `app/jcl/`

### 5.1 Data Definition / VSAM Cluster Management

| # | Job Name | Purpose | Steps |
|---|----------|---------|-------|
| 1 | ACCTFILE | Delete/define/load Account VSAM KSDS from flat file | STEP05 (IDCAMS DEL), STEP10 (IDCAMS DEF), STEP15 (IDCAMS REPRO) |
| 2 | CARDFILE | Close CICS files, delete/define Card VSAM KSDS + AIX + PATH, BLDINDEX, reopen | CLCIFIL (SDSF), STEP05-STEP60 (IDCAMS), OPCIFIL (SDSF) |
| 3 | CUSTFILE | Close CICS files, delete/define Customer VSAM KSDS, load, reopen | CLCIFIL, STEP05-STEP15 (IDCAMS), OPCIFIL |
| 4 | XREFFILE | Delete/define Card Cross-Reference VSAM KSDS + AIX + PATH, load, build index | STEP05-STEP30 (IDCAMS) |
| 5 | TRANFILE | Close CICS, delete/define Transaction VSAM KSDS + AIX + PATH, load, reopen | CLCIFIL, STEP05-STEP30 (IDCAMS), OPCIFIL |
| 6 | TRANIDX | Create/build alternate indexes on Transaction file | STEP20-STEP30 (IDCAMS) |
| 7 | DISCGRP | Delete/define/load Disclosure Group VSAM KSDS | STEP05-STEP15 (IDCAMS) |
| 8 | TCATBALF | Delete/define/load Transaction Category Balance VSAM KSDS | STEP05-STEP15 (IDCAMS) |
| 9 | TRANTYPE | Delete/define/load Transaction Type VSAM KSDS | STEP05-STEP15 (IDCAMS) |
| 10 | TRANCATG | Delete/define/load Transaction Category VSAM KSDS | STEP05-STEP15 (IDCAMS) |
| 11 | REPTFILE | Define report output dataset | STEP05 (IDCAMS) |
| 12 | DALYREJS | Define daily rejects sequential dataset | STEP05 (IDCAMS) |
| 13 | DUSRSECJ | Create user security VSAM KSDS from sequential file | PREDEL, STEP01 (IEBGENER), STEP02-STEP03 (IDCAMS) |
| 14 | ESDSRRDS | Define ESDS and RRDS VSAM files for testing | PREDEL, STEP01-STEP05 (IDCAMS/IEBGENER) |
| 15 | DEFCUST | Alternative customer file definition | STEP05 (IDCAMS ×2) |
| 16 | DEFGDGB | Define GDG base for transaction backups | STEP05 (IDCAMS) |
| 17 | DEFGDGD | Define GDG base + initial generations with IEBGENER | STEP10-STEP60 (IDCAMS/IEBGENER) |

### 5.2 Batch Processing

| # | Job Name | Purpose | Steps |
|---|----------|---------|-------|
| 1 | POSTTRAN | Post daily transactions to master | STEP15 (PGM=CBTRN02C) |
| 2 | INTCALC | Calculate interest on accounts | STEP15 (PGM=CBACT04C, PARM='2022071800') |
| 3 | COMBTRAN | Sort and combine transaction backup with system-generated trans, reload to VSAM | STEP05R (SORT), STEP10 (IDCAMS REPRO) |
| 4 | TRANREPT | Sort transactions and produce report | STEP05R (SORT), STEP10R (PGM=CBTRN03C) |
| 5 | TRANBKP | Back up transaction VSAM to GDG generation | STEP05R (REPROC), STEP05/STEP10 (IDCAMS) |
| 6 | PRTCATBL | Print category balance report (sorted) | DELDEF, STEP05R (REPROC), STEP10R (SORT) |
| 7 | READACCT | Read and display account file | PREDEL, STEP05 (PGM=CBACT01C) |
| 8 | READCARD | Read and display card file | STEP05 (PGM=CBACT02C) |
| 9 | READCUST | Read and display customer file | STEP05 (PGM=CBCUS01C) |
| 10 | READXREF | Read and display cross-reference file | STEP05 (PGM=CBACT03C) |
| 11 | CREASTMT | Create account statements (sort, define, generate) | DELDEF01, STEP010 (SORT), STEP020 (IDCAMS), STEP030 (IEFBR14), STEP040 (PGM=CBSTM03A) |
| 12 | CBEXPORT | Export customer data for branch migration | STEP01 (IDCAMS DEF export cluster), STEP02 (PGM=CBEXPORT) |
| 13 | CBIMPORT | Import customer data from export file | STEP01 (PGM=CBIMPORT) |
| 14 | WAITSTEP | Wait utility step for scheduling | WAIT (PGM=COBSWAIT) |

### 5.3 Infrastructure / Utility

| # | Job Name | Purpose | Steps |
|---|----------|---------|-------|
| 1 | CLOSEFIL | Close VSAM files in CICS region (TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC) | CLCIFIL (SDSF) |
| 2 | OPENFIL | Open VSAM files in CICS region | OPCIFIL (SDSF) |
| 3 | CBADMCDJ | Define CICS CSD resources (programs, mapsets, transactions) for CardDemo | STEP1 (PGM=DFHCSDUP) |
| 4 | FTPJCL | FTP file transfer utility | STEP1 (PGM=FTP) |
| 5 | INTRDRJ1 | Internal reader job — submit another JCL dynamically | IDCAMS, STEP01 (IEBGENER) |
| 6 | INTRDRJ2 | Internal reader — IDCAMS operations | IDCAMS |
| 7 | TXT2PDF1 | Convert text report to PDF via IKJEFT1B | TXT2PDF |

### 5.4 Sub-Application JCL

#### Authorization IMS (`app/app-authorization-ims-db2-mq/jcl/`)

| Job | Purpose |
|-----|---------|
| CBPAUP0J.jcl | Run CBPAUP0C (delete expired authorizations) |
| DBPAUTP0.jcl | Define/initialize IMS authorization DB |
| LOADPADB.JCL | Load authorization IMS DB from flat files |
| UNLDPADB.JCL | Unload authorization IMS DB to flat files |
| UNLDGSAM.JCL | Unload IMS DB via GSAM |

#### Transaction Type DB2 (`app/app-transaction-type-db2/jcl/`)

| Job | Purpose |
|-----|---------|
| CREADB21.jcl | Create DB2 tables for transaction types/categories |
| MNTTRDB2.jcl | Maintain (refresh) transaction type DB2 tables |
| TRANEXTR.jcl | Extract transaction types from DB2 to flat file |

---

## 6. Control-M Scheduled Pipelines

Source: `app/scheduler/CardDemo.controlm`

### DAILY — TransactionBackup
```
CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
```
Runs: Every day | Closes CICS files, backs up transactions to GDG, waits, reopens files.

### WEEKLY — TransactionTypesDBRefresh
```
MNTTRDB2 → (triggers two parallel SMART folders)
```
Runs: Saturdays | Refreshes DB2 transaction type tables.

### WEEKLY — DisclosureGroupsRefresh (SMART_FOLDER, depends on MNTTRDB2)
```
CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL
```
Runs: Saturdays (after MNTTRDB2) | Refreshes disclosure group VSAM file.

### WEEKLY — TransactionTypesDBRefresh Extract (SMART_FOLDER)
```
TRANEXTR
```
Runs: Saturdays (after MNTTRDB2) | Extracts refreshed types to flat file.

### MONTHLY — InterestCalculation
```
CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL
```
Runs: Monthly | Closes files, calculates interest, combines transactions, reopens.
