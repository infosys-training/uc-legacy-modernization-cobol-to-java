# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Estate Size:** 44 COBOL programs | 27,350 LOC | 47 copybooks | 46 JCL jobs | 16 BMS maps

---

## 1. Program Catalog

### 1.1 Main Application Programs (`app/cbl/`) — 27 Programs

#### Batch Programs (16)

| # | Filename | PROGRAM-ID | Purpose | LOC | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|---------|-----|---------------------|---------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read Account file and write into output files | 430 | R: ACCTFILE; W: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and print Card data file | 153 | R: CARDFILE | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and print Card Cross-Reference file | 135 | R: XREFFILE | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Calculate interest on account balances | 621 | R: TCATBALF, XREFFILE, DISCGRP; R/W: ACCTFILE; W: TRANSACT | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and print Customer data file | 178 | R: CUSTFILE | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export Customer Data for Branch Migration | 582 | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; W: EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Import Customer Data from Branch Migration Export | 487 | R: EXPFILE; W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | CBSTM03A | Print Account Statements from Transaction data | 924 | W: STMTFILE, HTMLFILE; CALL: CBSTM03B | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | CBSTM03B | File processing subroutine for Statement Report | 230 | R: TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | _(none — uses LINKAGE)_ |
| 10 | CBTRN01C.cbl | CBTRN01C | Post records from daily transaction file (variant 1) | 494 | R: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | CBTRN02C | Post records from daily transaction file (production) | 731 | R: DALYTRAN, XREFFILE; R/W: ACCTFILE, TCATBALF; W: TRANFILE, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | CBTRN03C | Print daily Transaction Detail Report | 649 | R: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; W: TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | COBSWAIT | Utility: wait for specified centiseconds | 41 | CALL: MVSWAIT | _(none)_ |
| 14 | CSUTLDTC.cbl | CSUTLDTC | Date validation utility subroutine | 157 | CALL: CEEDAYS | _(none)_ |

> **Note:** CBTRN01C and CBTRN02C serve the same function (transaction posting). CBTRN02C is the production version used by the POSTTRAN job with reject handling and category balance updates.

#### Online (CICS) Programs (13 in `app/cbl/`)

| # | Filename | PROGRAM-ID | Purpose | LOC | CICS Operations | Copybooks Referenced |
|---|----------|------------|---------|-----|-----------------|---------------------|
| 15 | COACTUPC.cbl | COACTUPC | Accept and process Account Update | 4,236 | HANDLE ABEND, XCTL, RETURN, RECEIVE MAP, SEND MAP, READ ×5, REWRITE ×2, SEND | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY ×31, CSSTRPFY, CSUTLDPY |
| 16 | COACTVWC.cbl | COACTVWC | Accept and process Account View request | 941 | HANDLE ABEND, XCTL, RETURN, SEND MAP, RECEIVE MAP, READ ×3, SEND TEXT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 17 | COADM01C.cbl | COADM01C | Admin Menu for Admin users | 288 | RETURN, XCTL, SEND, RECEIVE | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | COBIL00C.cbl | COBIL00C | Bill Payment — pay balance in full/partial | 572 | RETURN, ASKTIME, FORMATTIME, SEND, RECEIVE, READ, REWRITE, STARTBR, READPREV, ENDBR, WRITE | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 19 | COCRDLIC.cbl | COCRDLIC | List Credit Cards | 1,459 | XCTL ×3, RETURN, SEND MAP, RECEIVE MAP, STARTBR ×2, READNEXT ×2, READPREV ×2, ENDBR, SEND TEXT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 20 | COCRDSLC.cbl | COCRDSLC | Accept and process Credit Card detail view | 887 | HANDLE ABEND, XCTL, RETURN, SEND MAP, RECEIVE MAP, READ ×2, SEND TEXT, SEND, ABEND | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 21 | COCRDUPC.cbl | COCRDUPC | Accept and process Credit Card update | 1,560 | HANDLE ABEND, XCTL, RETURN, RECEIVE MAP, SEND MAP, READ ×2, REWRITE FILE, SEND, ABEND | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 22 | COMEN01C.cbl | COMEN01C | Main Menu hub for regular users (11 options) | 308 | RETURN, INQUIRE, XCTL, SEND, RECEIVE | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 23 | CORPT00C.cbl | CORPT00C | Print Transaction reports (submits batch JCL) | 649 | RETURN, WRITEQ TD, SEND ×2, RECEIVE; CALL: CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | COSGN00C.cbl | COSGN00C | Sign-on screen for CardDemo Application | 260 | RETURN, RECEIVE, SEND, SEND TEXT, ASSIGN ×2, READ, XCTL ×2 | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 25 | COTRN00C.cbl | COTRN00C | List Transactions from TRANSACT file | 699 | RETURN, SEND ×2, RECEIVE, STARTBR, READNEXT, READPREV, ENDBR | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN01C.cbl | COTRN01C | View a Transaction from TRANSACT file | 330 | RETURN, SEND, RECEIVE, READ | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | COTRN02C.cbl | COTRN02C | Add a new Transaction to TRANSACT file | 783 | RETURN, SEND, RECEIVE, READ ×2, STARTBR, READPREV, ENDBR, WRITE; CALL: CSUTLDTC | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |

#### Security / User Administration (CICS — in `app/cbl/`)

| # | Filename | PROGRAM-ID | Purpose | LOC | CICS Operations | Copybooks Referenced |
|---|----------|------------|---------|-----|-----------------|---------------------|
| 28 | COUSR00C.cbl | COUSR00C | List all users from USRSEC file | 695 | RETURN, SEND ×2, RECEIVE, STARTBR, READNEXT, READPREV, ENDBR | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR01C.cbl | COUSR01C | Add a new user to USRSEC file | 299 | RETURN, SEND, RECEIVE, WRITE | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR02C.cbl | COUSR02C | Update a user in USRSEC file | 414 | RETURN, SEND, RECEIVE, READ, REWRITE | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | COUSR03C.cbl | COUSR03C | Delete a user from USRSEC file | 359 | RETURN, SEND, RECEIVE, READ, DELETE | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

### 1.2 Sub-Application Programs

#### Authorization Module — IMS/DB2/MQ (`app/app-authorization-ims-db2-mq/cbl/`) — 7 Programs

| # | Filename | PROGRAM-ID | Purpose | LOC | Type | Key Operations | Copybooks |
|---|----------|------------|---------|-----|------|----------------|-----------|
| 32 | CBPAUP0C.cbl | CBPAUP0C | Delete expired Pending Authorization messages | 386 | Batch IMS | IMS DL/I: GN, GNP, DLET | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | COPAUA0C | Card Authorization Decision (MQ+IMS+CICS) | 1,026 | CICS/IMS/MQ | MQOPEN, MQGET, MQPUT1, MQCLOSE; CICS READ ×3; IMS GU, SCHD | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 34 | COPAUS0C.cbl | COPAUS0C | Summary View of Authorization Messages | 1,032 | CICS/IMS/BMS | CICS RETURN, SYNCPOINT, SEND, RECEIVE, READ ×3; IMS GU, GNP | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | COPAUS1C | Detail View of Authorization Message | 604 | CICS/IMS/BMS | CICS SEND, RECEIVE, READ; IMS GU, GNP, REPL | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | COPAUS2C | Mark Authorization as Fraud (DB2 insert) | — | CICS/DB2 | EXEC SQL INSERT; CICS SEND, RECEIVE | COCOM01Y, CSDAT01Y, DFHAID, DFHBMSCA |
| 37 | PAUDBLOD.cbl | PAUDBLOD | IMS Database Load for Pending Authorizations | — | Batch IMS | IMS ISRT, GU | CIPAUSMY, CIPAUDTY |
| 38 | PAUDBUNL.cbl | PAUDBUNL | IMS Database Unload | — | Batch IMS | IMS GN, GNP | CIPAUSMY, CIPAUDTY |

#### Transaction Type Module — DB2 (`app/app-transaction-type-db2/cbl/`) — 3 Programs

| # | Filename | PROGRAM-ID | Purpose | LOC | Type | Key Operations | Copybooks |
|---|----------|------------|---------|-----|------|----------------|-----------|
| 39 | COTRTLIC.cbl | COTRTLIC | Transaction Type List (DB2 cursor pagination) | — | CICS/DB2 | EXEC SQL DECLARE CURSOR, OPEN, FETCH, CLOSE | CSDB2RPY, CSDB2RWY, COCOM01Y |
| 40 | COTRTUPC.cbl | COTRTUPC | Transaction Type Update/Delete (DB2) | — | CICS/DB2 | EXEC SQL UPDATE, DELETE | CSDB2RPY, CSDB2RWY, COCOM01Y |
| 41 | CSDB2LOD.cbl | CSDB2LOD | DB2 Table Loader for Transaction Types | — | Batch DB2 | EXEC SQL INSERT | CSDB2RPY, CSDB2RWY |

#### VSAM/MQ Module (`app/app-vsam-mq/cbl/`) — 2 Programs

| # | Filename | PROGRAM-ID | Purpose | LOC | Type | Key Operations | Copybooks |
|---|----------|------------|---------|-----|------|----------------|-----------|
| 42 | COACCT01.cbl | COACCT01 | Account Inquiry via MQ | — | Batch/MQ | MQOPEN, MQGET, MQPUT1 | CVACT01Y |
| 43 | CODATE01.cbl | CODATE01 | Date Inquiry via MQ | — | Batch/MQ | MQOPEN, MQGET, MQPUT1 | — |

#### Utility — GSAM Unload (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | PROGRAM-ID | Purpose | LOC | Type | Key Operations |
|---|----------|------------|---------|-----|------|----------------|
| 44 | DBUNLDGS.cbl | DBUNLDGS | GSAM unload utility for IMS data | — | Batch IMS | IMS GN, GNP, ISRT |

---

### 1.3 Program Classification Summary

| Classification | Count | % | Programs |
|---------------|-------|---|----------|
| **Online (CICS)** | 21 | 48% | COSGN00C, COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C–03C, COTRTLIC, COTRTUPC, COPAUS0C, COPAUS1C, COPAUS2C |
| **Pure Batch** | 16 | 36% | CBACT01C–04C, CBCUS01C, CBEXPORT, CBIMPORT, CBSTM03A/B, CBTRN01C–03C, COBSWAIT, CSUTLDTC, CSDB2LOD |
| **Sub-app (IMS/MQ)** | 7 | 16% | CBPAUP0C, COPAUA0C, PAUDBLOD, PAUDBUNL, DBUNLDGS, COACCT01, CODATE01 |

---

## 2. JCL Job Catalog (`app/jcl/` and sub-application directories) — 46 Jobs

### 2.1 Data Setup Jobs — VSAM Cluster Definition

| Job | Purpose | Steps |
|-----|---------|-------|
| ACCTFILE.jcl | Define/load Account VSAM KSDS | CLCIFIL(SDSF) → STEP05(IDCAMS delete) → STEP10(IDCAMS define) → STEP15(IDCAMS repro ACCTDATA→ACCTVSAM) → OPCIFIL(SDSF open) |
| CARDFILE.jcl | Define/load Card VSAM KSDS | CLCIFIL(SDSF) → STEP05(IDCAMS delete) → STEP10(IDCAMS define) → STEP15(IDCAMS repro CARDDATA→CARDVSAM) → OPCIFIL(SDSF open) |
| CARDXREF.jcl | Define/load Card Cross-Reference VSAM KSDS | CLCIFIL(SDSF) → STEP05(IDCAMS delete) → STEP10(IDCAMS define) → STEP15(IDCAMS repro XREFDATA→XREFVSAM) → OPCIFIL(SDSF open) |
| CUSTFILE.jcl | Define/load Customer VSAM KSDS | CLCIFIL(SDSF) → STEP05(IDCAMS delete) → STEP10(IDCAMS define) → STEP15(IDCAMS repro CUSTDATA→CUSTVSAM) → OPCIFIL(SDSF open) |
| DISCGRP.jcl | Define/load Disclosure Group VSAM KSDS | STEP05(IDCAMS delete) → STEP10(IDCAMS define) → STEP15(IDCAMS repro DISCGRP→DISCVSAM) |
| TCATBALF.jcl | Define/load Transaction Category Balance VSAM | STEP05(IDCAMS delete) → STEP10(IDCAMS define) → STEP15(IDCAMS repro TCATBAL→TCATBALV) |
| TRANTYPE.jcl | Define/load Transaction Type VSAM KSDS | STEP05(IDCAMS delete) → STEP10(IDCAMS define) → STEP15(IDCAMS repro TRANTYPE→TTYPVSAM) |
| TRANCATG.jcl | Define/load Transaction Category VSAM KSDS | STEP05(IDCAMS delete) → STEP10(IDCAMS define) → STEP15(IDCAMS repro TRANCATG→TCATVSAM) |
| TRANFILE.jcl | Define/load Transaction VSAM KSDS (with AIX) | CLCIFIL(SDSF) → STEP05–STEP30(IDCAMS define, AIX, PATH) → OPCIFIL(SDSF open) |
| TRANIDX.jcl | Define Transaction VSAM Alternate Index | STEP20(IDCAMS delete) → STEP25(IDCAMS define AIX) → STEP30(IDCAMS build index) |
| DALYTRAN.jcl | Define Daily Transaction file | STEP05(IDCAMS delete) → STEP10(IDCAMS define) |
| DALYREJS.jcl | Define Daily Rejects file | STEP05(IDCAMS delete) |
| REPTFILE.jcl | Define Report output file | STEP05(IDCAMS delete) |
| DEFCUST.jcl | Define Customer VSAM (alternate) | STEP05(IDCAMS) ×2 |
| DEFGDGB.jcl | Define GDG Base for backups | STEP05(IDCAMS define GDG) |
| DEFGDGD.jcl | Backup and define GDG datasets | STEP10–STEP60(IDCAMS/IEBGENER: backup TRANTYPE.PS, TRANCATG.PS, DISCGRP.PS) |

### 2.2 Batch Processing Jobs

| Job | Purpose | Steps | Program(s) Executed |
|-----|---------|-------|-------------------|
| **POSTTRAN.jcl** | Post daily transactions | STEP15(CBTRN02C) | CBTRN02C |
| **INTCALC.jcl** | Calculate interest on accounts | STEP15(CBACT04C, PARM='2022071800') | CBACT04C |
| **CREASTMT.jcl** | Generate account statements | _(references CBSTM03A)_ | CBSTM03A→CBSTM03B |
| **TRANREPT.jcl** | Generate transaction report | STEP05R(REPROC/SORT) → STEP10R(CBTRN03C) | SORT, CBTRN03C |
| READACCT.jcl | Read and print Account data | PREDEL(IEFBR14) → STEP05(CBACT01C) | CBACT01C |
| READCARD.jcl | Read and print Card data | STEP05(CBACT02C) | CBACT02C |
| READCUST.jcl | Read and print Customer data | STEP05(CBCUS01C) | CBCUS01C |
| READXREF.jcl | Read and print Cross-Reference data | STEP05(CBACT03C) | CBACT03C |
| PRTCATBL.jcl | Print Transaction Category Balances | DELDEF(IEFBR14) → STEP05R(REPROC) → STEP10R(SORT) | SORT |
| TRANBKP.jcl | Backup Transaction file | STEP05R(REPROC) → STEP05(IDCAMS delete) → STEP10(IDCAMS define) | IDCAMS |
| WAITSTEP.jcl | Wait utility step | WAIT(COBSWAIT) | COBSWAIT |
| TXT2PDF1.JCL | Convert text statement to PDF | TXT2PDF(IKJEFT1B) | TXT2PDF REXX |

### 2.3 Data Migration Jobs

| Job | Purpose | Steps | Program(s) |
|-----|---------|-------|------------|
| EXPDATA.jcl | Export data for branch migration | _(executes CBEXPORT)_ | CBEXPORT |
| IMPDATA.jcl | Import data with validation | _(executes CBIMPORT)_ | CBIMPORT |

### 2.4 User Security Setup

| Job | Purpose | Steps |
|-----|---------|-------|
| DUSRSECJ.jcl | Define/load User Security VSAM | PREDEL(IEFBR14) → STEP01(IEBGENER inline data) → STEP02(IDCAMS delete) → STEP03(IDCAMS repro USRSEC.PS→USRSEC.VSAM.KSDS) |
| ESDSRRDS.jcl | Define ESDS/RRDS security datasets | PREDEL(IEFBR14) → STEP01(IEBGENER) → STEP02–STEP05(IDCAMS define ESDS, RRDS, repro) |

### 2.5 FTP / Internal Reader Jobs

| Job | Purpose | Steps |
|-----|---------|-------|
| FTPJCL.JCL | FTP file transfer | STEP1(FTP) |
| INTRDRJ1.JCL | Internal reader: backup and submit INTRDRJ2 | IDCAMS(repro backup) → STEP01(IEBGENER→INTRDR) |
| INTRDRJ2.JCL | Internal reader: second-stage backup | IDCAMS(repro) |
| OPENFIL.jcl | Open CICS files via SDSF | OPCIFIL(SDSF) |

### 2.6 Sub-Application JCL

#### IMS Authorization (`app/app-authorization-ims-db2-mq/jcl/`)

| Job | Purpose | Steps |
|-----|---------|-------|
| CBPAUP0J.jcl | Execute IMS expired auth purge | STEP01(DFSRRC00 → CBPAUP0C) |
| DBPAUTP0.jcl | Unload IMS Pending Auth database | STEPDEL(IEFBR14) → UNLOAD(DFSRRC00) |
| LOADPADB.JCL | Load IMS Pending Auth database | STEP01(DFSRRC00 → PAUDBLOD) |
| UNLDGSAM.JCL | Unload IMS data via GSAM | STEP01(DFSRRC00 → DBUNLDGS) |
| UNLDPADB.JCL | Unload IMS Pending Auth to flat files | STEP0(IEFBR14) → STEP01(DFSRRC00 → PAUDBUNL) |

#### DB2 Transaction Type (`app/app-transaction-type-db2/jcl/`)

| Job | Purpose | Steps |
|-----|---------|-------|
| CREADB21.jcl | Create DB2 objects (tables, plans) | FREEPLN(IKJEFT01) → multiple BIND/DDL steps |

### 2.7 Daily Batch Pipeline Sequence

```
POSTTRAN.jcl (CBTRN02C)
    ↓ writes TRANSACT, updates ACCOUNT balances
INTCALC.jcl (CBACT04C)
    ↓ calculates interest, rewrites ACCOUNT
CREASTMT.jcl (CBSTM03A → CBSTM03B)
    ↓ generates STMT-FILE + HTML-FILE
TRANREPT.jcl (SORT → CBTRN03C)
    ↓ generates TRANREPT report file
```

---

*Generated from analysis of 44 COBOL programs and 46 JCL jobs in the CardDemo estate.*
