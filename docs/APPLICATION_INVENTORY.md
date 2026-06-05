# APPLICATION INVENTORY — CardDemo COBOL Estate

> Generated from source analysis of `infosys-training/uc-legacy-modernization-cobol-to-java`

## Summary

| Metric | Count |
|--------|------:|
| Total COBOL Programs | 44 |
| Main Programs (`app/cbl/`) | 29 |
| Sub-App Programs | 15 |
| Copybooks (`app/cpy/` + sub-apps) | 47 |
| JCL Jobs (`app/jcl/` + sub-apps) | 51 |
| BMS Maps (`app/bms/` + sub-apps) | 16 |
| Total Lines of COBOL | ~27,970 |

---

## 1. Main Programs — `app/cbl/`

### 1.1 Batch Programs

| # | Filename | LOC | Purpose | Key I/O | Copybooks |
|---|----------|----:|---------|---------|-----------|
| 1 | **CBACT01C.cbl** | 430 | Read Account VSAM file and split into multiple output formats (flat, array, VB) | **R:** ACCTFILE (KSDS) **W:** OUT-FILE, ARRY-FILE, VBRC-FILE | CVACT01Y, CODATECN |
| 2 | **CBACT02C.cbl** | 178 | Read and print Card data file | **R:** CARDFILE (KSDS) | CVACT02Y |
| 3 | **CBACT03C.cbl** | 178 | Read and print Account Cross-Reference data file | **R:** XREFFILE (KSDS) | CVACT03Y |
| 4 | **CBACT04C.cbl** | 652 | Interest calculator — compute interest/fees on account balances using disclosure group rates | **R:** TCATBALF (KSDS), XREFFILE, DISCGRP, ACCTFILE **R/W:** ACCOUNT-FILE (I-O) **W:** TRANSACT-FILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | **CBCUS01C.cbl** | 178 | Read and print Customer data file | **R:** CUSTFILE (KSDS) | CVCUS01Y |
| 6 | **CBEXPORT.cbl** | 582 | Export customer data for branch migration — reads all VSAM files and creates multi-record export file | **R:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE **W:** EXPORT-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | **CBIMPORT.cbl** | 487 | Import customer data from branch migration export — splits multi-record file into normalized targets with validation | **R:** EXPORT-INPUT **W:** CUSTOMER-OUTPUT, ACCOUNT-OUTPUT, XREF-OUTPUT, TRANSACTION-OUTPUT, CARD-OUTPUT, ERROR-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | **CBSTM03A.CBL** | 924 | Generate customer account statements — reads transaction data and produces plain-text and HTML output | **R:** XREFFILE, CUSTFILE, ACCTFILE (via CBSTM03B) **W:** STMT-FILE, HTML-FILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | **CBSTM03B.CBL** | 267 | Subroutine for CBSTM03A — handles file I/O (open/close/read/write) for transaction, xref, customer, and account files | **R:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | *(none — uses shared area from caller)* |
| 10 | **CBTRN01C.cbl** | 494 | Post records from daily transaction file — validate against xref and account | **R:** DALYTRAN (SEQ), CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANSACT | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | **CBTRN02C.cbl** | 731 | Post daily transactions — validate, update account balances, write reject file | **R:** DALYTRAN (SEQ), XREFFILE, ACCTFILE **R/W:** ACCOUNT-FILE (I-O), TCATBAL-FILE (I-O) **W:** TRANSACT-FILE, DALYREJS-FILE | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | **CBTRN03C.cbl** | 649 | Print daily transaction detail report with lookups for type/category descriptions | **R:** TRANSACT (SEQ), XREFFILE, TRANTYPE, TRANCATG, DATEPARM **W:** REPORT-FILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | **COBSWAIT.cbl** | 41 | Utility — wait for specified duration (centiseconds) by calling assembler MVSWAIT | *(none)* | *(none)* |
| 14 | **CSUTLDTC.cbl** | 157 | Utility — date validation subroutine using LE CEEDAYS API | *(none)* | *(none)* |

### 1.2 Online (CICS) Programs

| # | Filename | LOC | Purpose | Key I/O | Copybooks |
|---|----------|----:|---------|---------|-----------|
| 15 | **COSGN00C.cbl** | 260 | Sign-on screen — authenticates users against USRSEC file, routes to Admin or Main menu | **R:** USRSEC (CICS READ) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | **COADM01C.cbl** | 288 | Admin menu — hub for admin functions (User CRUD, Tran Type mgmt) | CICS SEND/RECEIVE MAP, XCTL | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | **COMEN01C.cbl** | 308 | Main menu — hub for 11 user functions (accounts, cards, transactions, reports, etc.) | CICS SEND/RECEIVE MAP, XCTL to 11 targets | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | **COACTVWC.cbl** | 941 | View account details — reads account, card-xref, and customer data | **R:** ACCTFILE, CARDFILE (AIX), CUSTFILE (CICS READ) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 19 | **COACTUPC.cbl** | 4,236 | Account update — **largest program**, exhaustive field validation (date, SSN, phone, state, ZIP), updates account/card/customer | **R/W:** ACCTFILE, CARDFILE (AIX), CUSTFILE (CICS READ/REWRITE) | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY ×38, CSSTRPFY, CSUTLDPY |
| 20 | **COCRDLIC.cbl** | 1,459 | List credit cards — paginated browse (STARTBR/READNEXT/READPREV/ENDBR) | **R:** CARDFILE (CICS STARTBR/READNEXT/READPREV) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 21 | **COCRDSLC.cbl** | 887 | View credit card detail | **R:** CARDFILE, CUSTFILE (CICS READ) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 22 | **COCRDUPC.cbl** | 1,560 | Update credit card — edit name, status, expiry; REWRITE card record | **R/W:** CARDFILE (CICS READ/REWRITE) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 23 | **COTRN00C.cbl** | 699 | List transactions — paginated browse of TRANSACT file | **R:** TRANSACT (CICS STARTBR/READNEXT/READPREV) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | **COTRN01C.cbl** | 330 | View a single transaction from TRANSACT file | **R:** TRANSACT (CICS READ) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | **COTRN02C.cbl** | 783 | Add a new transaction — validate card/account, write to TRANSACT | **R:** CARDXREF (CICS READ), ACCTFILE **W:** TRANSACT (CICS WRITE) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 26 | **CORPT00C.cbl** | 649 | Report request — validates date range parameters and submits batch JCL via TDQ (INTRDRJ1/J2) | **W:** TDQ WRITEQ (internal reader) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | **COBIL00C.cbl** | 572 | Bill payment — pay account balance in full, create transaction record | **R/W:** ACCTFILE (CICS READ/REWRITE), CARDXREF (AIX), TRANSACT (STARTBR/READPREV/WRITE) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 28 | **COUSR00C.cbl** | 695 | List all users — paginated browse of USRSEC file | **R:** USRSEC (CICS STARTBR/READNEXT) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | **COUSR01C.cbl** | 299 | Add a new user (regular or admin) to USRSEC file | **W:** USRSEC (CICS WRITE) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | **COUSR02C.cbl** | 414 | Update a user in USRSEC file | **R/W:** USRSEC (CICS READ/REWRITE) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | **COUSR03C.cbl** | 359 | Delete a user from USRSEC file | **R/W:** USRSEC (CICS READ/DELETE) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O | Copybooks |
|---|----------|----:|---------|---------------|---------|-----------|
| 32 | **COPAUA0C.cbl** | 1,026 | Card authorization decision — reads MQ request, looks up IMS/VSAM data, writes MQ response | Online (CICS+IMS+MQ) | **MQ:** MQOPEN, MQGET (request), MQPUT1 (response) **IMS:** DLI SCHD/GU/TERM **CICS:** READ ACCTFILE, CARDXREF, CUSTFILE | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | **COPAUS0C.cbl** | 1,032 | Summary view of pending authorization messages — browse IMS auth summary/detail segments | Online (CICS+IMS+BMS) | **IMS:** DLI GU/GNP **CICS:** READ ACCTFILE, CARDFILE, CUSTFILE, SEND/RECEIVE MAP | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | **COPAUS1C.cbl** | 604 | Detail view of authorization message with update — mark fraud, view full auth details | Online (CICS+IMS+BMS) | **IMS:** DLI GU/GNP/REPL **CICS:** SYNCPOINT, LINK to COPAUS2C | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | **COPAUS2C.cbl** | 244 | Mark authorization as fraud — insert fraud record into DB2 table | Online (CICS+IMS+DB2) | **DB2:** EXEC SQL INSERT/SELECT **IMS:** DLI (via PCB) | CIPAUDTY |
| 36 | **CBPAUP0C.cbl** | 386 | Purge expired pending authorization messages from IMS database | Batch (IMS) | **IMS:** DLI GN/GNP/DLET, checkpoint | CIPAUSMY, CIPAUDTY |
| 37 | **DBUNLDGS.CBL** | 267 | GSAM unload — unload IMS auth segments to GSAM sequential file | Batch (IMS) | **IMS:** CBLTDLI GN/GNP/ISRT (GSAM) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 38 | **PAUDBLOD.CBL** | *(est.)* | Load pending authorization data into IMS database | Batch (IMS) | **IMS:** DLI ISRT/GU | CIPAUSMY, CIPAUDTY |
| 39 | **PAUDBUNL.CBL** | *(est.)* | Unload pending authorization data from IMS database | Batch (IMS) | **IMS:** DLI GN/GNP | CIPAUSMY, CIPAUDTY |

### 2.2 Transaction Type Module — `app/app-transaction-type-db2/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O | Copybooks |
|---|----------|----:|---------|---------------|---------|-----------|
| 40 | **COTRTLIC.cbl** | 2,098 | Transaction type list/update — cursor-based pagination of DB2 TRANSACTION_TYPE table | Online (CICS+DB2+BMS) | **DB2:** EXEC SQL DECLARE CURSOR/OPEN/FETCH/CLOSE/UPDATE/DELETE | CSDB2RPY, CSDB2RWY |
| 41 | **COTRTUPC.cbl** | 1,702 | Transaction type maintenance — add/update/delete with cascading deletes | Online (CICS+DB2+BMS) | **DB2:** EXEC SQL INSERT/UPDATE/DELETE (cascading) | CSDB2RPY, CSDB2RWY |
| 42 | **COBTUPDT.cbl** | 237 | Batch maintenance of transaction types in DB2 | Batch (DB2) | **DB2:** EXEC SQL INSERT/UPDATE/DELETE | *(DB2 DCL)* |

### 2.3 VSAM-MQ Module — `app/app-vsam-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O | Copybooks |
|---|----------|----:|---------|---------------|---------|-----------|
| 43 | **COACCT01.cbl** | 620 | Account details inquiry via MQ — receives MQ request, reads VSAM, sends MQ response | Online (CICS+MQ) | **MQ:** MQGET/MQPUT1 **CICS:** READ ACCTFILE | *(MQ copybooks)* |
| 44 | **CODATE01.cbl** | 524 | System date inquiry via MQ — CDRD transaction | Online (CICS+MQ) | **MQ:** MQGET/MQPUT1 | *(MQ copybooks)* |

---

## 3. JCL Job Catalog — `app/jcl/`

### 3.1 VSAM Cluster Definition Jobs

| Job | Purpose | Steps |
|-----|---------|-------|
| **ACCTFILE.jcl** | Define Account VSAM KSDS cluster | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER (keys 11/0, recsize 300) → STEP15: IDCAMS REPRO from flat file |
| **CARDFILE.jcl** | Define Card VSAM KSDS with alternate index on Account ID | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE → STEP10: DEFINE CLUSTER (keys 16/0, recsize 150) → STEP15: REPRO → STEP40: DEFINE AIX (keys 11/16) → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: SDSF open CICS files |
| **CUSTFILE.jcl** | Define Customer VSAM KSDS cluster | CLCIFIL: close CICS → STEP05: DELETE → STEP10: DEFINE CLUSTER (keys 9/0, recsize 500) → STEP15: REPRO → OPCIFIL: open CICS |
| **XREFFILE.jcl** | Define Card Cross-Reference VSAM KSDS | Similar DELETE → DEFINE (keys 16/0, recsize 50) → REPRO pattern |
| **TRANFILE.jcl** | Define Transaction VSAM KSDS | DELETE → DEFINE (keys 16/0, recsize 350) → REPRO |
| **TRANIDX.jcl** | Define Transaction alternate index (by Account ID) | DEFINE AIX → DEFINE PATH → BLDINDEX |
| **DISCGRP.jcl** | Define Disclosure Group VSAM KSDS | DELETE → DEFINE (keys 16/0, recsize 50) → REPRO |
| **TCATBALF.jcl** | Define Transaction Category Balance VSAM KSDS | DELETE → DEFINE (keys 17/0, recsize 50) → REPRO |
| **TRANTYPE.jcl** | Define Transaction Type VSAM KSDS | DELETE → DEFINE (keys 2/0, recsize 60) → REPRO |
| **TRANCATG.jcl** | Define Transaction Category VSAM KSDS | DELETE → DEFINE (keys 6/0, recsize 60) → REPRO |
| **REPTFILE.jcl** | Define Report output dataset | DELETE → DEFINE sequential |
| **READCARD.jcl** | Read Card data (standalone utility) | EXEC PGM=CBACT02C |
| **READCUST.jcl** | Read Customer data (standalone utility) | EXEC PGM=CBCUS01C |
| **READACCT.jcl** | Read Account data (standalone utility) | EXEC PGM=CBACT01C |
| **READXREF.jcl** | Read Cross-reference data (standalone utility) | EXEC PGM=CBACT03C |
| **DEFCUST.jcl** | Define Customer file (alternate) | IDCAMS DEFINE |
| **DEFGDGB.jcl** | Define GDG base for backup | IDCAMS DEFINE GDG BASE |
| **DEFGDGD.jcl** | Define GDG base for daily data | IDCAMS DEFINE GDG BASE |

### 3.2 Batch Processing Jobs

| Job | Purpose | Steps |
|-----|---------|-------|
| **POSTTRAN.jcl** | Post daily transactions | EXEC PGM=CBTRN02C — reads DALYTRAN, writes TRANSACT, updates ACCOUNT |
| **INTCALC.jcl** | Calculate interest and fees | EXEC PGM=CBACT04C — reads TCATBAL, XREF, DISCGRP; rewrites ACCOUNT; writes TRANSACT |
| **CREASTMT.JCL** | Generate account statements | EXEC PGM=CBSTM03A — reads XREF, CUST, ACCT, TRNX; writes STMT + HTML |
| **TRANREPT.jcl** | Generate daily transaction report | EXEC PGM=CBTRN03C — reads TRANSACT, XREF, TRANTYPE; writes REPORT |
| **CBEXPORT.jcl** | Export data for branch migration | STEP01: IDCAMS define export cluster → STEP02: EXEC PGM=CBEXPORT |
| **CBIMPORT.jcl** | Import data from migration export | STEP01: EXEC PGM=CBIMPORT |
| **COMBTRAN.jcl** | Combine and sort transactions | STEP05R: SORT current + system transactions → STEP10: IDCAMS REPRO to VSAM |
| **DALYREJS.jcl** | Define daily reject file | IDCAMS DEFINE |
| **TRANBKP.jcl** | Backup transaction file to GDG | IDCAMS REPRO to GDG(+1) |
| **PRTCATBL.jcl** | Print category balance file | IDCAMS PRINT |
| **WAITSTEP.jcl** | Utility wait step | EXEC PGM=COBSWAIT |
| **DUSRSECJ.jcl** | Define User Security VSAM file | IDCAMS DELETE → DEFINE → REPRO |

### 3.3 File Management Jobs

| Job | Purpose | Steps |
|-----|---------|-------|
| **CLOSEFIL.jcl** | Close all CICS files | SDSF CEMT SET FIL CLO for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| **OPENFIL.jcl** | Open all CICS files | SDSF CEMT SET FIL OPE for all datasets |

### 3.4 CICS Resource Definition

| Job | Purpose | Steps |
|-----|---------|-------|
| **CBADMCDJ.jcl** | Create CICS resource definitions (CSD) | EXEC PGM=DFHCSDUP — defines all mapsets, programs, and transactions for CardDemo |

### 3.5 Utility Jobs

| Job | Purpose | Steps |
|-----|---------|-------|
| **FTPJCL.JCL** | FTP file transfer | FTP operations |
| **TXT2PDF1.JCL** | Convert text to PDF | EXEC PGM=TXT2PDF |
| **INTRDRJ1.JCL** | Internal reader — submit batch report (monthly) | JCL submitted via CORPT00C TDQ |
| **INTRDRJ2.JCL** | Internal reader — submit batch report (custom dates) | JCL submitted via CORPT00C TDQ |
| **ESDSRRDS.jcl** | Define ESDS and RRDS VSAM datasets | IDCAMS DEFINE (ESDS + RRDS) |

### 3.6 Sub-Application JCL

#### Authorization Module — `app/app-authorization-ims-db2-mq/jcl/`

| Job | Purpose | Steps |
|-----|---------|-------|
| **CBPAUP0J.jcl** | Purge expired authorizations | EXEC PGM=CBPAUP0C (IMS BMP) |
| **DBPAUTP0.jcl** | Process authorization database | IMS utility |
| **LOADPADB.JCL** | Load pending authorization IMS DB | EXEC PGM=PAUDBLOD |
| **UNLDPADB.JCL** | Unload pending authorization IMS DB | EXEC PGM=PAUDBUNL |
| **UNLDGSAM.JCL** | Unload IMS via GSAM | EXEC PGM=DBUNLDGS |

#### Transaction Type Module — `app/app-transaction-type-db2/jcl/`

| Job | Purpose | Steps |
|-----|---------|-------|
| **CREADB21.jcl** | Create DB2 tables for transaction types | EXEC SQL DDL |
| **MNTTRDB2.jcl** | Maintain transaction type DB2 data | EXEC PGM=COBTUPDT |
| **TRANEXTR.jcl** | Extract transaction type data | DB2 UNLOAD |

---

## 4. Classification Summary

| Classification | Count | Programs |
|---------------|------:|----------|
| **Batch** | 16 | CBACT01C, CBACT02C, CBACT03C, CBACT04C, CBCUS01C, CBEXPORT, CBIMPORT, CBSTM03A, CBSTM03B, CBTRN01C, CBTRN02C, CBTRN03C, COBSWAIT, CSUTLDTC, CBPAUP0C, COBTUPDT |
| **Online (CICS)** | 17 | COSGN00C, COADM01C, COMEN01C, COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| **Online (CICS+IMS)** | 2 | COPAUS0C, COPAUS1C |
| **Online (CICS+IMS+MQ)** | 1 | COPAUA0C |
| **Online (CICS+IMS+DB2)** | 1 | COPAUS2C |
| **Online (CICS+DB2)** | 2 | COTRTLIC, COTRTUPC |
| **Online (CICS+MQ)** | 2 | COACCT01, CODATE01 |
| **Batch (IMS)** | 3 | CBPAUP0C, DBUNLDGS, PAUDBLOD/PAUDBUNL |
