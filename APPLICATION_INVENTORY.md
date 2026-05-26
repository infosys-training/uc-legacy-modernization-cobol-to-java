# Application Inventory — CardDemo COBOL Estate

> Generated from analysis of `uc-legacy-modernization-cobol-to-java/app/`

---

## 1. COBOL Programs — `app/cbl/`

### 1.1 Batch Programs

| # | Filename | Lines | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|---------|---------------------|----------------------|
| 1 | **CBACT01C.cbl** | 430 | Read account file and write into multiple output formats (fixed, array, variable-block) | **Read:** ACCTFILE (account VSAM) · **Write:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | **CBACT02C.cbl** | 178 | Read and print card data file | **Read:** CARDFILE (card VSAM) | CVACT02Y |
| 3 | **CBACT03C.cbl** | 178 | Read and print account cross-reference data file | **Read:** XREFFILE (card-xref VSAM) | CVACT03Y |
| 4 | **CBACT04C.cbl** | 652 | Interest and fee calculator — processes transaction category balance file, computes interest/fees, updates account file, writes system-generated transactions | **Read:** TCATBALF, XREFFILE, DISCGRP · **I-O:** ACCTFILE · **Write:** TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | **CBCUS01C.cbl** | 178 | Read and print customer data file | **Read:** CUSTFILE (customer VSAM) | CVCUS01Y |
| 6 | **CBEXPORT.cbl** | 582 | Export customer data from VSAM files to a multi-record export file for branch migration/data transfer | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · **Write:** EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | **CBIMPORT.cbl** | 487 | Import customer data from multi-record export file, split into separate normalized files for target system | **Read:** EXPFILE · **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | **CBSTM03A.CBL** | 924 | Generate account statements in text and HTML — reads cross-ref, customer, account, and transaction files; calls CBSTM03B for file I/O | **Read:** (via CBSTM03B) TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE · **Write:** STMTFILE, HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | **CBSTM03B.CBL** | 230 | File I/O subroutine for statement generation — open/read/close transaction, xref, customer, and account files | **Read:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | *(none — data structures passed via LINKAGE)* |
| 10 | **CBTRN01C.cbl** | 494 | Daily transaction validation — reads daily transactions, looks up cross-reference and account data for validation | **Read:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | **CBTRN02C.cbl** | 731 | Post daily transactions — validates, posts to transaction master, writes rejects, updates account balances and category balances | **Read:** DALYTRAN, XREFFILE · **I-O:** ACCTFILE, TCATBALF · **Write:** TRANFILE, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | **CBTRN03C.cbl** | 649 | Transaction report generator — produces daily transaction report with account totals, page totals, and grand totals | **Read:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM · **Write:** TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | **CSUTLDTC.cbl** | 157 | Date utility — converts dates using CEEDAYS (Lilian date conversion) | *(no file I/O)* | *(none)* |
| 14 | **COBSWAIT.cbl** | 41 | Batch wait utility — calls MVSWAIT to pause execution | *(no file I/O — calls MVSWAIT)* | *(none)* |

### 1.2 Online (CICS) Programs

| # | Filename | Lines | Purpose | Key I/O Operations (CICS) | Copybooks Referenced |
|---|----------|-------|---------|---------------------------|----------------------|
| 1 | **COSGN00C.cbl** | 260 | Sign-on screen — user authentication via USRSEC VSAM file | CICS READ (USRSEC), XCTL to menu | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | **COMEN01C.cbl** | 308 | Main menu — displays menu options, routes to selected program via XCTL | CICS SEND/RECEIVE MAP, XCTL | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | **COADM01C.cbl** | 288 | Admin menu — displays admin options (user CRUD + DB2 tran types) | CICS SEND/RECEIVE, XCTL | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | **COACTVWC.cbl** | 941 | Account view — displays account details with card cross-reference, customer data | CICS READ (CARDXREF AIX, ACCTDATA, CUSTDATA) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 5 | **COACTUPC.cbl** | 4236 | Account update — full account editing with field validation (dates, SSN, amounts) | CICS READ/REWRITE (ACCTDATA, CUSTDATA, CARDXREF AIX) | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY |
| 6 | **COCRDLIC.cbl** | 1459 | Credit card list — browse card records with pagination (STARTBR/READNEXT/READPREV) | CICS STARTBR/READNEXT/READPREV/ENDBR (CARDDATA) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | **COCRDSLC.cbl** | 887 | Credit card detail view — single card record display with customer info | CICS READ (CARDDATA, CARDXREF AIX) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | **COCRDUPC.cbl** | 1560 | Credit card update — edit card details (name, status, expiry) with validation | CICS READ/REWRITE (CARDDATA) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | **COTRN00C.cbl** | 699 | Transaction list — browse transactions with pagination (STARTBR/READNEXT/READPREV) | CICS STARTBR/READNEXT/READPREV/ENDBR (TRANSACT) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | **COTRN01C.cbl** | 330 | Transaction view — single transaction record display | CICS READ (TRANSACT) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | **COTRN02C.cbl** | 783 | Transaction add — add new transactions with validation, assigns transaction IDs | CICS READ (CXACAIX, CCXREF), STARTBR/READPREV/ENDBR, WRITE (TRANSACT) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | **CORPT00C.cbl** | 649 | Transaction reports — submits batch report jobs via internal reader (TDQ) | CICS WRITEQ TD (JOBS), calls CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | **COBIL00C.cbl** | 572 | Bill payment — processes bill payments against accounts | CICS READ/REWRITE (ACCTDATA), READ (CXACAIX), STARTBR/READPREV/ENDBR, WRITE (TRANSACT) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | **COUSR00C.cbl** | 695 | User list — browse user security records with pagination | CICS STARTBR/READNEXT/READPREV/ENDBR (USRSEC) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | **COUSR01C.cbl** | 299 | User add — create new user security records | CICS WRITE (USRSEC) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | **COUSR02C.cbl** | 414 | User update — modify existing user security records | CICS READ/REWRITE (USRSEC) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | **COUSR03C.cbl** | 359 | User delete — remove user security records | CICS READ/DELETE (USRSEC) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 `app-authorization-ims-db2-mq/cbl/` — Payment Authorization (IMS/DB2/MQ)

| # | Filename | Lines | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|---------|----------------|---------------------|----------------------|
| 1 | **COPAUA0C.cbl** | 1026 | Authorization processor — reads requests from MQ queue, validates against VSAM files (xref, account, customer), reads IMS auth DB, makes approval/decline decision, sends reply to MQ | Online (CICS + MQ) | MQ: MQOPEN/MQGET/MQPUT1/MQCLOSE · CICS READ (CARDXREF, ACCTDATA, CUSTDATA) | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 2 | **COPAUS0C.cbl** | 1032 | Pending authorization list — browse authorization summary/detail records | Online (CICS) | CICS READ (CARDXREF, ACCTDATA, CUSTDATA), SYNCPOINT | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 3 | **COPAUS1C.cbl** | 604 | Authorization detail view — view and mark authorization as fraud | Online (CICS) | CICS LINK, SYNCPOINT | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 4 | **COPAUS2C.cbl** | 244 | Fraud update module — updates authorization fraud status in DB2 AUTHFRDS table | Online (CICS + DB2) | EXEC SQL (SELECT, UPDATE on AUTHFRDS) | CIPAUDTY |
| 5 | **CBPAUP0C.cbl** | 386 | Authorization purge — batch cleanup of expired authorizations from IMS DB | Batch (IMS) | IMS DL/I calls (GN, GNP) for auth summary/detail | CIPAUSMY, CIPAUDTY |
| 6 | **PAUDBLOD.CBL** | 369 | IMS DB load — loads authorization data from flat files into IMS database | Batch (IMS) | READ (INFILE1, INFILE2), IMS DL/I (ISRT, GU) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 7 | **PAUDBUNL.CBL** | 317 | IMS DB unload — unloads authorization data from IMS database to flat files | Batch (IMS) | IMS DL/I (GN, GNP), WRITE (OPFILE1, OPFILE2) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | **DBUNLDGS.CBL** | 366 | IMS DB to GSAM unload — unloads authorization data to GSAM sequential files | Batch (IMS) | IMS DL/I (GN, GNP, ISRT to GSAM) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 `app-transaction-type-db2/cbl/` — Transaction Type Maintenance (DB2)

| # | Filename | Lines | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|---------|----------------|---------------------|----------------------|
| 1 | **COTRTLIC.cbl** | 2098 | Transaction type list — browse transaction types from DB2 with CICS pagination using SQL cursors | Online (CICS + DB2) | EXEC SQL (SELECT, cursor OPEN/FETCH/CLOSE on TRNTYPE), SYNCPOINT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, CSDB2RPY |
| 2 | **COTRTUPC.cbl** | 1702 | Transaction type update — CRUD operations on transaction types in DB2 | Online (CICS + DB2) | EXEC SQL (SELECT, INSERT, UPDATE, DELETE on TRNTYPE, TRNCATG), SYNCPOINT | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |
| 3 | **COBTUPDT.cbl** | 237 | Batch DB2 update — reads transaction type records from flat file and performs INSERT/UPDATE/DELETE on DB2 TRNTYPE table | Batch (DB2) | READ (INPFILE), EXEC SQL (INSERT/UPDATE/DELETE on TRNTYPE) | *(inline DCLTRTYP)* |

### 2.3 `app-vsam-mq/cbl/` — VSAM-MQ Integration

| # | Filename | Lines | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|---------|----------------|---------------------|----------------------|
| 1 | **COACCT01.cbl** | 620 | Account inquiry via MQ — receives account inquiry requests from MQ, reads VSAM account data, sends reply via MQ | Online (CICS + MQ) | CICS RETRIEVE, MQ (MQOPEN/MQGET/MQPUT/MQCLOSE), CICS READ (ACCTDATA) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 2 | **CODATE01.cbl** | 524 | Date inquiry via MQ — receives date inquiry requests from MQ, processes date conversions, sends reply via MQ | Online (CICS + MQ) | CICS RETRIEVE, MQ (MQOPEN/MQGET/MQPUT/MQCLOSE) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. JCL Jobs — `app/jcl/`

### 3.1 VSAM File Definition Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | **ACCTFILE.jcl** | Define and load Account VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO (from ACCTDATA.PS) |
| 2 | **CARDFILE.jcl** | Define and load Card Data VSAM KSDS + AIX | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO → STEP40: DEFINE AIX → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: SDSF open CICS files |
| 3 | **CUSTFILE.jcl** | Define and load Customer VSAM KSDS | CLCIFIL: SDSF close → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO → OPCIFIL: SDSF open |
| 4 | **XREFFILE.jcl** | Define and load Card Cross-Reference VSAM KSDS + AIX | Similar pattern: DELETE → DEFINE → REPRO → AIX → PATH → BLDINDEX |
| 5 | **TRANFILE.jcl** | Define Transaction Master VSAM KSDS | DELETE → DEFINE CLUSTER → REPRO |
| 6 | **TCATBALF.jcl** | Define Transaction Category Balance VSAM KSDS | DELETE → DEFINE CLUSTER → REPRO |
| 7 | **DISCGRP.jcl** | Define Disclosure Group VSAM KSDS | DELETE → DEFINE CLUSTER → REPRO |
| 8 | **TRANTYPE.jcl** | Define Transaction Type VSAM KSDS | DELETE → DEFINE CLUSTER → REPRO |
| 9 | **TRANCATG.jcl** | Define Transaction Category VSAM KSDS | DELETE → DEFINE CLUSTER → REPRO |
| 10 | **DUSRSECJ.jcl** | Define User Security VSAM KSDS + load inline data | PREDEL: IEFBR14 delete → STEP01: IEBGENER create PS from inline → STEP02: IDCAMS DEFINE → STEP03: IDCAMS REPRO |
| 11 | **ESDSRRDS.jcl** | Define User Security ESDS + RRDS (alternative formats) | Similar: create PS → DEFINE ESDS → REPRO → DEFINE RRDS → REPRO |
| 12 | **DEFCUST.jcl** | Define Customer Data VSAM (alternate schema) | DELETE → DEFINE CLUSTER |
| 13 | **REPTFILE.jcl** | Define Report Output VSAM | DELETE → DEFINE CLUSTER |

### 3.2 GDG (Generation Data Group) Definition Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | **DEFGDGB.jcl** | Define GDG bases for batch pipeline | STEP05: IDCAMS DEFINE GDG for TRANSACT.BKUP, TRANSACT.DALY, TRANREPT, TCATBALF.BKUP, SYSTRAN, TRANSACT.COMBINED |
| 2 | **DEFGDGD.jcl** | Define GDG bases for DB2 reference data | STEP10-60: DEFINE GDG + IEBGENER first generation for TRANTYPE.BKUP, TRANCATG.PS.BKUP, DISCGRP.BKUP |
| 3 | **DALYREJS.jcl** | Define GDG base for daily rejects | STEP05: DEFINE GDG for DALYREJS |

### 3.3 Batch Processing Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | **POSTTRAN.jcl** | Post daily transactions | STEP15: EXEC PGM=CBTRN02C — reads DALYTRAN, XREFFILE; updates ACCTFILE, TCATBALF; writes TRANFILE, DALYREJS |
| 2 | **INTCALC.jcl** | Interest and fee calculation | STEP15: EXEC PGM=CBACT04C — reads TCATBALF, XREFFILE, DISCGRP; updates ACCTFILE; writes TRANSACT (system transactions) |
| 3 | **TRANREPT.jcl** | Transaction report generation | STEP15: EXEC PGM=CBTRN03C — reads TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; writes TRANREPT |
| 4 | **CREASTMT.JCL** | Create account statements | STEP15: EXEC PGM=CBSTM03A — reads TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE; writes STMTFILE, HTMLFILE |
| 5 | **COMBTRAN.jcl** | Combine transactions | STEP05R: SORT — merge TRANSACT.BKUP and SYSTRAN → STEP10: IDCAMS REPRO into TRANSACT.VSAM.KSDS |
| 6 | **PRTCATBL.jcl** | Print transaction category balance | DELDEF → STEP05R: REPRO (TCATBALF to flat) → STEP10R: SORT/filter → STEP15R: PRINT |
| 7 | **CBEXPORT.jcl** | Export data for migration | STEP01: IDCAMS DEFINE export VSAM → STEP02: EXEC PGM=CBEXPORT |
| 8 | **CBIMPORT.jcl** | Import data from export file | STEP01: EXEC PGM=CBIMPORT |
| 9 | **WAITSTEP.jcl** | Batch wait step | EXEC PGM=COBSWAIT |

### 3.4 CICS File Management Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | **CLOSEFIL.jcl** | Close CICS files | CLCIFIL: SDSF — closes TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| 2 | **OPENFIL.jcl** | Open CICS files | OPCIFIL: SDSF — opens TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |

### 3.5 CICS Resource Definition & Utility Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | **CBADMCDJ.jcl** | CICS CSD definitions — define all CardDemo programs, mapsets, transactions, and files | STEP1: EXEC PGM=DFHCSDUP — defines LIBRARY, MAPSETs, PROGRAMs, TRANSACTIONs, FILEs in GROUP(CARDDEMO) |
| 2 | **INTRDRJ1.JCL** | Internal reader JCL template 1 — batch report job submitted from CICS | CBTRN03C execution for transaction reporting |
| 3 | **INTRDRJ2.JCL** | Internal reader JCL template 2 — statement generation job submitted from CICS | CBSTM03A execution for statement generation |
| 4 | **FTP JCL.JCL** | FTP file transfer JCL | FTP steps for data transfer |

### 3.6 Data Read/Print Utility Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | **READACCT.jcl** | Read account file | EXEC PGM=CBACT01C |
| 2 | **READCARD.jcl** | Read card file | EXEC PGM=CBACT02C |
| 3 | **READCUST.jcl** | Read customer file | EXEC PGM=CBCUS01C |
| 4 | **READXREF.jcl** | Read cross-reference file | EXEC PGM=CBACT03C |
| 5 | **TRANIDX.jcl** | Define transaction alternate index | IDCAMS DEFINE AIX + PATH + BLDINDEX |
| 6 | **TRANBKP.jcl** | Backup transaction file | IDCAMS REPRO to GDG |
| 7 | **TXT2PDF1.JCL** | Convert text report to PDF | Utility steps |

### 3.7 Sub-Application JCL

#### `app-authorization-ims-db2-mq/jcl/`

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | **CBPAUP0J.jcl** | Run authorization purge batch | EXEC PGM=CBPAUP0C |
| 2 | **DBPAUTP0.jcl** | Populate IMS auth database | EXEC PGM=PAUDBLOD |
| 3 | **LOADPADB.JCL** | Load pending auth database | EXEC PGM=PAUDBLOD |
| 4 | **UNLDPADB.JCL** | Unload pending auth database | EXEC PGM=PAUDBUNL |
| 5 | **UNLDGSAM.JCL** | Unload IMS to GSAM | EXEC PGM=DBUNLDGS |

#### `app-transaction-type-db2/jcl/`

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | **CREADB21.jcl** | Create DB2 tables for transaction types | DDL execution for TRNTYPE and TRNCATG tables |
| 2 | **MNTTRDB2.jcl** | Maintain DB2 transaction types | EXEC PGM=COBTUPDT |
| 3 | **TRANEXTR.jcl** | Extract transaction reference data to DB2 | Data extraction and load steps |

---

## 4. Summary Statistics

| Category | Count |
|----------|-------|
| **Total COBOL programs** | 44 |
| ├─ Batch programs (app/cbl/) | 14 |
| ├─ Online/CICS programs (app/cbl/) | 17 |
| ├─ Authorization sub-app programs | 8 |
| ├─ Transaction type sub-app programs | 3 |
| └─ VSAM-MQ sub-app programs | 2 |
| **Total JCL jobs** | 45+ |
| **Total copybooks** | 30 (app/cpy/) + 19 (sub-app cpy/) |
| **BMS maps** | 18 (app/bms/) + 4 (sub-app bms/) |
| **Total lines of COBOL** | ~27,000+ |
