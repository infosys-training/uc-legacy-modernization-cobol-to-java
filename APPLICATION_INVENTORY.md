# Application Inventory — CardDemo COBOL Estate

> **Estate totals:** 44 COBOL programs | 47 copybooks | 46 JCL jobs | 27,350 LOC

---

## 1. COBOL Program Catalog

### 1.1 Main Programs (`app/cbl/`)

| # | Filename | LOC | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----|------|---------|---------|-----------|
| 1 | CBACT01C.cbl | 430 | Batch | Read account VSAM file; write fixed, array, and variable-length output files | **R:** ACCTFILE (VSAM KSDS) **W:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Batch | Read card data file sequentially; display records | **R:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Batch | Read card-account cross-reference file sequentially | **R:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Batch | Calculate interest on account balances using disclosure group rates; generate interest transactions | **R:** TCATBALF, XREFFILE, DISCGRP **I-O:** ACCTFILE **W:** TRANSACT | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Batch | Read customer master file sequentially; display records | **R:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Batch | Export customer, account, card, cross-ref, and transaction data to a single sequential file for branch migration | **R:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE **W:** EXPFILE | CVEXPORT, CVCUS01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVTRA05Y |
| 7 | CBIMPORT.cbl | 487 | Batch | Import multi-entity data from sequential export file with type-based routing and validation; write error log | **R:** EXPFILE **W:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVEXPORT, CVCUS01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVTRA05Y |
| 8 | CBSTM03A.CBL | 924 | Batch | Generate account statements in plain text and HTML from transaction, cross-ref, customer, and account data | **W:** STMTFILE, HTMLFILE **via CBSTM03B:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | 230 | Batch (sub) | I/O submodule for CBSTM03A; opens/reads/closes transaction, cross-ref, customer, and account files | **R:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | *(none — data passed via linkage)* |
| 10 | CBTRN01C.cbl | 494 | Batch | Post records from daily transaction file; cross-validate against customer, card, account, and cross-ref | **R:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | 731 | Batch | Post daily transactions to master transaction file; update account balances and category balances; reject invalid | **R:** DALYTRAN, XREFFILE **I-O:** ACCTFILE, TCATBALF **W:** TRANSACT, DALYREJS | CVTRA06Y, CVACT03Y, CVACT01Y, CVTRA05Y, CVTRA01Y, CVTRA04Y |
| 12 | CBTRN03C.cbl | 649 | Batch | Generate daily transaction report with page/account/grand totals; sort by account then transaction | **R:** TRANSACT, CARDXREF, TRANTYPE, TRANCATG, DATEPARM **W:** REPTFILE (GDG) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COACTUPC.cbl | 4,236 | Online (CICS) | Account update screen — view/modify account details with exhaustive field validation (date, SSN, phone, state, ZIP) | **CICS:** READ/REWRITE ACCTFILE, READ CARDXREF, READ CUSTFILE **BMS:** COACTUP | COACTUP, CSSETATY (×3), CSLKPCDY, CSUTLDPY, CSUTLDWY, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, DFHAID, DFHBMSCA |
| 14 | COACTVWC.cbl | 941 | Online (CICS) | Account view screen — display account details, customer name, card number (read-only) | **CICS:** READ ACCTFILE, READ CARDXREF, READ CUSTFILE **BMS:** COACTVW | COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, DFHAID, DFHBMSCA |
| 15 | COADM01C.cbl | 288 | Online (CICS) | Admin menu screen — displays admin options (user CRUD, DB2 transaction type maintenance); routes via XCTL | **CICS:** SEND/RECEIVE MAP, XCTL to COUSR00C/01C/02C/03C/COTRTLIC/COTRTUPC | COADM02Y, COCOM01Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | 572 | Online (CICS) | Bill payment screen — process payment against account balance; validate card and amount | **CICS:** READ/REWRITE ACCTFILE, READ CARDXREF, WRITE TRANSACT **BMS:** COBIL00 | COBIL00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | 41 | Batch (utility) | Wait routine wrapper — calls assembler MVSWAIT for timed delays | **CALL:** MVSWAIT | *(none)* |
| 18 | COCRDLIC.cbl | 1,459 | Online (CICS) | Credit card list screen — paginated browse of cards by account with STARTBR/READNEXT/READPREV | **CICS:** STARTBR/READNEXT/READPREV/ENDBR CARDFILE, READ CARDXREF, READ ACCTFILE **BMS:** COCRDLI | COCRDLI, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, DFHAID, DFHBMSCA |
| 19 | COCRDSLC.cbl | 887 | Online (CICS) | Credit card detail view — display single card details with account and customer info | **CICS:** READ CARDFILE, READ ACCTFILE, READ CARDXREF, READ CUSTFILE **BMS:** COCRDSL | COCRDSL, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, DFHAID, DFHBMSCA |
| 20 | COCRDUPC.cbl | 1,560 | Online (CICS) | Credit card update — modify card details (embossed name, expiry, status) with validation | **CICS:** READ/REWRITE CARDFILE, READ ACCTFILE, READ CARDXREF, READ CUSTFILE **BMS:** COCRDUP | COCRDUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, DFHAID, DFHBMSCA |
| 21 | COMEN01C.cbl | 308 | Online (CICS) | Main menu hub — displays 11 user options; routes to all user-facing screens via XCTL | **CICS:** SEND/RECEIVE MAP, XCTL to 11 target programs | COMEN02Y, COCOM01Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 22 | CORPT00C.cbl | 649 | Online (CICS) | Report request screen — submit batch JCL jobs (transaction report, statement generation) via internal reader | **CICS:** SEND/RECEIVE MAP, WRITE to INTRDR (internal reader) for JCL submission | CORPT00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | 260 | Online (CICS) | Sign-on screen — authenticate user via USRSEC file; route admin vs. regular user to appropriate menu | **CICS:** READ USRSEC, SEND/RECEIVE MAP, XCTL to COADM01C or COMEN01C | COSGN00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | COTRN00C.cbl | 699 | Online (CICS) | Transaction list screen — paginated browse of transactions with STARTBR/READNEXT/READPREV | **CICS:** STARTBR/READNEXT/READPREV/ENDBR TRANSACT **BMS:** COTRN00 | COTRN00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN01C.cbl | 330 | Online (CICS) | Transaction detail view — display single transaction record (read-only) | **CICS:** READ TRANSACT **BMS:** COTRN01 | COTRN01, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | 783 | Online (CICS) | Transaction add screen — create new transaction with card/account validation and date utility calls | **CICS:** READ CARDXREF, READ ACCTFILE, STARTBR/READPREV/ENDBR TRANSACT, WRITE TRANSACT **CALL:** CSUTLDTC | COTRN02, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | 695 | Online (CICS) | User list screen — paginated browse of USRSEC file with STARTBR/READNEXT/READPREV | **CICS:** STARTBR/READNEXT/READPREV/ENDBR USRSEC **BMS:** COUSR00 | COUSR00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | 299 | Online (CICS) | User add screen — create new regular or admin user in USRSEC file | **CICS:** WRITE USRSEC **BMS:** COUSR01 | COUSR01, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR02C.cbl | 414 | Online (CICS) | User update screen — modify existing user record (name, password, type) | **CICS:** READ/REWRITE USRSEC **BMS:** COUSR02 | COUSR02, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | 359 | Online (CICS) | User delete screen — remove user record from USRSEC file | **CICS:** READ/DELETE USRSEC **BMS:** COUSR03 | COUSR03, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | 157 | Batch (utility) | Date conversion utility — calls LE CEEDAYS for Lilian date conversion; callable subprogram | **CALL:** CEEDAYS | *(none — standalone utility)* |

### 1.2 Sub-Application Programs

#### Authorization Module (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | LOC | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----|------|---------|---------|-----------|
| 32 | CBPAUP0C.cbl | 386 | Batch (IMS) | Purge expired pending authorization messages from IMS database | **IMS DL/I:** GN, GNP, DLET, CHKP on PAUT-PCB | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | 1,026 | Online (CICS+IMS+MQ) | Card authorization decision — reads request from MQ queue, checks card/account/customer via CICS VSAM + IMS, writes response/error to MQ | **MQ:** MQOPEN/MQGET/MQPUT1 **CICS:** RETRIEVE, READ CARDXREF, ACCTFILE, CUSTFILE **IMS:** GU, SCHD, TERM | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 34 | COPAUS0C.cbl | 1,032 | Online (CICS+IMS) | Pending authorization summary — paginated browse of IMS authorization database with CICS BMS screen | **CICS:** SEND/RECEIVE MAP, READ ACCTFILE/CARDFILE/CARDXREF **IMS DL/I:** GU, GNP, SCHD, TERM | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | 604 | Online (CICS+IMS) | Authorization detail view — display/update single authorization message with IMS REPL and SYNCPOINT | **CICS:** SEND/RECEIVE MAP, LINK, SYNCPOINT **IMS DL/I:** GU, GNP, REPL, SCHD, TERM | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | 244 | Online (CICS+DB2) | Mark authorization as fraud — insert fraud record into DB2 FRAUD table with timestamp | **DB2:** INSERT into FRAUD table **CICS:** ASKTIME, FORMATTIME, RETURN | CIPAUDTY |
| 37 | PAUDBLOD.cbl* | — | Batch (IMS) | IMS database load for pending authorizations (ISRT, GU) | **IMS DL/I:** ISRT, GU | CIPAUSMY, CIPAUDTY |
| 38 | PAUDBUNL.cbl* | — | Batch (IMS) | IMS database unload (GN, GNP) | **IMS DL/I:** GN, GNP | CIPAUSMY, CIPAUDTY |
| 39 | DBUNLDGS.cbl* | — | Batch (IMS) | GSAM unload utility (GN, GNP, ISRT) | **IMS DL/I:** GN, GNP, ISRT | CIPAUSMY |

*\* Programs referenced in knowledge notes; not directly present as `.cbl` files in the sub-app directory.*

#### Transaction Type DB2 Module (`app/app-transaction-type-db2/cbl/`)

| # | Filename | LOC | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----|------|---------|---------|-----------|
| 40 | COBTUPDT.cbl | 237 | Batch (DB2) | Batch update of transaction types from input file; DB2 INSERT/UPDATE/DELETE | **R:** INPFILE (sequential) **DB2:** INSERT/UPDATE/DELETE on TRTYP table | *(SQL INCLUDE: DCLTRTYP)* |
| 41 | COTRTLIC.cbl | 2,098 | Online (CICS+DB2) | Transaction type list with cursor-based forward/backward pagination in DB2; select/delete operations | **DB2:** DECLARE/OPEN/FETCH/CLOSE cursors, DELETE, SYNCPOINT **CICS:** SEND/RECEIVE MAP, XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 42 | COTRTUPC.cbl | 1,702 | Online (CICS+DB2) | Transaction type update/add — form-based CRUD on DB2 TRTYP/TRCAT tables with cascading deletes | **DB2:** SELECT/INSERT/UPDATE/DELETE on TRTYP + TRCAT, SYNCPOINT **CICS:** SEND/RECEIVE MAP, XCTL, ABEND | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |

#### VSAM-MQ Module (`app/app-vsam-mq/cbl/`)

| # | Filename | LOC | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----|------|---------|---------|-----------|
| 43 | COACCT01.cbl | 620 | Batch (MQ) | Account inquiry via MQ — reads request from MQ queue, looks up account data, sends response | **MQ:** MQOPEN/MQGET/MQPUT **R:** Account data (via MQ message) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 44 | CODATE01.cbl | 524 | Batch (MQ) | Date inquiry via MQ — processes date conversion requests from MQ queue | **MQ:** MQOPEN/MQGET/MQPUT | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 2. JCL Job Catalog (`app/jcl/`)

### 2.1 Data Setup — VSAM Cluster Definition

| Job | LOC | Purpose | Steps |
|-----|-----|---------|-------|
| ACCTFILE.jcl | 65 | Define Account VSAM KSDS (keys 11,0 recsize 300) | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO from PS |
| CARDFILE.jcl | 128 | Define Card VSAM KSDS + AIX (keys 16,0 recsize 150) | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE cluster+AIX → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP40: DEFINE AIX → STEP50: BLDINDEX |
| CUSTFILE.jcl | 108 | Define Customer VSAM KSDS (keys 9,0 recsize 500) | CLCIFIL: SDSF close CICS → STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → OPCIFIL: SDSF reopen CICS |
| XREFFILE.jcl | 107 | Define Card-Account cross-reference KSDS + 2 AIX | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP20: DEFINE AIX (ACCT) → STEP25: BLDINDEX → STEP30: DEFINE AIX (CUST) |
| TRANFILE.jcl | 125 | Define Transaction master VSAM KSDS + AIX (keys 16,0 recsize 350) | CLCIFIL: close CICS → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → STEP20: DEFINE AIX → STEP25: BLDINDEX |
| DISCGRP.jcl | 64 | Define Disclosure Group VSAM KSDS (keys 16,0 recsize 50) | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TCATBALF.jcl | 64 | Define Transaction Category Balance VSAM KSDS (keys 17,0 recsize 50) | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TRANTYPE.jcl | 64 | Define Transaction Type VSAM KSDS (keys 2,0 recsize 60) | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TRANCATG.jcl | 64 | Define Transaction Category VSAM KSDS (keys 6,0 recsize 60) | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| DUSRSECJ.jcl | 91 | Define User Security VSAM KSDS (recsize 80) | PREDEL: IEFBR14 → STEP01: IEBGENER seed data → STEP02: IDCAMS DEFINE → STEP03: REPRO |
| ESDSRRDS.jcl | 124 | Define ESDS/RRDS demo files | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02: DEFINE ESDS → STEP03: REPRO → STEP04: DEFINE RRDS |
| DEFCUST.jcl | 57 | Alternate customer file definition | STEP05: DELETE → STEP05: DEFINE CLUSTER |
| DEFGDGB.jcl | 30 | Define GDG bases for report/backup files | STEP05: IDCAMS DEFINE GDG |
| DEFGDGD.jcl | 95 | Define DB2-related GDG bases + backup transaction type/category data | STEP10: DEFINE GDG → STEP20: IEBGENER TRANTYPE backup → STEP30: DEFINE GDG → STEP40: IEBGENER TRANCATG backup → STEP50: DEFINE GDG |
| DALYREJS.jcl | 30 | Define GDG base for daily transaction rejects | STEP05: IDCAMS DEFINE GDG |
| REPTFILE.jcl | 30 | Define GDG base for transaction report output | STEP05: IDCAMS DEFINE GDG |

### 2.2 Batch Processing Jobs

| Job | LOC | Purpose | Steps |
|-----|-----|---------|-------|
| POSTTRAN.jcl | 56 | Post daily transactions (executes CBTRN02C) | STEP15: EXEC PGM=CBTRN02C — DD: TRANFILE, DALYTRAN, XREFFILE, DALYREJS, ACCTFILE, TCATBALF |
| INTCALC.jcl | 42 | Calculate interest on accounts (executes CBACT04C) | STEP15: EXEC PGM=CBACT04C PARM='2022071800' — DD: TCATBALF, XREFFILE, ACCTFILE, DISCGRP, TRANSACT |
| TRANREPT.jcl | 75 | Generate daily transaction report | STEP05R: REPROC backup → STEP05R: SORT transactions → STEP10R: EXEC PGM=CBTRN03C — DD: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM, TRANREPT |
| READACCT.jcl | 48 | Read and list account file (executes CBACT01C) | PREDEL: IEFBR14 cleanup → STEP05: EXEC PGM=CBACT01C — DD: ACCTFILE, OUTFILE, ARRYFILE, VBRCFILE |
| READCARD.jcl | 25 | Read and list card file (executes CBACT02C) | STEP05: EXEC PGM=CBACT02C — DD: CARDFILE |
| READCUST.jcl | 25 | Read and list customer file (executes CBCUS01C) | STEP05: EXEC PGM=CBCUS01C — DD: CUSTFILE |
| READXREF.jcl | 25 | Read and list cross-ref file (executes CBACT03C) | STEP05: EXEC PGM=CBACT03C — DD: XREFFILE |
| CBEXPORT.jcl | 62 | Export all entity data for branch migration | STEP01: IDCAMS allocate export file → STEP02: EXEC PGM=CBEXPORT — DD: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE, EXPFILE |
| CBIMPORT.jcl | 48 | Import entity data from export file | STEP01: EXEC PGM=CBIMPORT — DD: EXPFILE, CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT |
| WAITSTEP.jcl | 24 | Execute timed wait (COBSWAIT wrapper) | WAIT: EXEC PGM=COBSWAIT |
| COMBTRAN.jcl | 49 | Combine and sort transactions from multiple sources into VSAM master | STEP05R: SORT merge → STEP10: IDCAMS REPRO to VSAM |
| TRANBKP.jcl | 50 | Backup transaction master and clear for new cycle | STEP05R: REPROC backup → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE |
| PRTCATBL.jcl | 56 | Print transaction category balance file (sorted) | DELDEF: IEFBR14 → STEP05R: REPROC → STEP10R: SORT |

### 2.3 Operations Jobs

| Job | LOC | Purpose | Steps |
|-----|-----|---------|-------|
| CLOSEFIL.jcl | 30 | Close VSAM files in CICS region before maintenance | CLCIFIL: SDSF — issues CEMT SET FIL(xxx) CLO |
| OPENFIL.jcl | 30 | Reopen VSAM files in CICS region after maintenance | OPCIFIL: SDSF — issues CEMT SET FIL(xxx) OPE |
| TRANIDX.jcl | 50 | Define/rebuild alternate indexes on transaction file | STEP20: DEFINE AIX → STEP25: BLDINDEX → STEP30: DEFINE PATH |
| CBADMCDJ.jcl | 167 | CICS CSD resource definition — defines programs, transactions, files, mapsets for CardDemo in CICS | STEP1: EXEC PGM=DFHCSDUP — DEFINE/ALTER PROGRAM, TRANSACTION, FILE, MAPSET entries |

### 2.4 Sub-Application Jobs

#### Authorization IMS (`app/app-authorization-ims-db2-mq/jcl/`)

| Job | LOC | Purpose | Steps |
|-----|-----|---------|-------|
| CBPAUP0J.jcl | 60 | Purge expired pending authorizations (IMS BMP) | STEP01: EXEC PGM=DFSRRC00 (IMS BMP region) running CBPAUP0C |
| DBPAUTP0.jcl | 56 | Unload IMS pending authorization database | STEPDEL: IEFBR14 cleanup → UNLOAD: EXEC PGM=DFSRRC00 running PAUDBUNL |

#### Transaction Type DB2 (`app/app-transaction-type-db2/jcl/`)

| Job | LOC | Purpose | Steps |
|-----|-----|---------|-------|
| CREADB21.jcl | 80 | Create DB2 tables (TRTYP, TRCAT) and bind plans | FREEPLN: IKJEFT01 free plan → CRCRDDB: IKJEFT01 run DDL |
| TRANEXTR.jcl | 122 | Extract/backup DB2 transaction type data to GDG | STEP10: IEBGENER TRANTYPE → STEP20: IEBGENER TRANCATG → STEP30: IEFBR14 → STEP40: IKJEFT01 |
| MNTTRDB2.jcl | 48 | Maintain DB2 transaction tables (batch INSERT/UPDATE/DELETE) | STEP1: IKJEFT01 runs COBTUPDT via DB2 TSO |

---

## 3. Summary Statistics

### Programs by Classification

| Classification | Count | % | LOC Range |
|---------------|-------|---|-----------|
| Online (CICS) | 21 | 48% | 244 – 4,236 |
| Pure Batch | 16 | 36% | 41 – 924 |
| Sub-app (IMS/DB2/MQ) | 7 | 16% | 237 – 1,032 |
| **Total** | **44** | **100%** | **41 – 4,236** |

### JCL Jobs by Category

| Category | Count | Purpose |
|----------|-------|---------|
| Data Setup (VSAM/GDG) | 16 | Define clusters, GDG bases, load seed data |
| Batch Processing | 13 | Execute COBOL batch programs |
| Operations/Utilities | 4 | CICS file open/close, CSD admin, index rebuild |
| Sub-app (IMS/DB2) | 5 | IMS BMP, DB2 DDL, DB2 maintenance |
| **Total** | **38** | *(8 JCL in sub-app dirs not counted in app/jcl/ 30)* |

### VSAM File Inventory

| VSAM Cluster | DSN Pattern | Key | RecSize | Primary Entity |
|-------------|-------------|-----|---------|----------------|
| ACCTDATA.VSAM.KSDS | AWS.M2.CARDDEMO.ACCTDATA.* | 11,0 | 300 | Account |
| CARDDATA.VSAM.KSDS | AWS.M2.CARDDEMO.CARDDATA.* | 16,0 | 150 | Card |
| CUSTDATA.VSAM.KSDS | AWS.M2.CARDDEMO.CUSTDATA.* | 9,0 | 500 | Customer |
| CARDXREF.VSAM.KSDS | AWS.M2.CARDDEMO.CARDXREF.* | 16,0 | 50 | Card-Account XREF |
| TRANSACT.VSAM.KSDS | AWS.M2.CARDDEMO.TRANSACT.* | 16,0 | 350 | Transaction |
| DISCGRP.VSAM.KSDS | AWS.M2.CARDDEMO.DISCGRP.* | 16,0 | 50 | Disclosure Group |
| TCATBAL.VSAM.KSDS | AWS.M2.CARDDEMO.TCATBAL.* | 17,0 | 50 | Tran Category Balance |
| TRANTYPE.VSAM.KSDS | AWS.M2.CARDDEMO.TRANTYPE.* | 2,0 | 60 | Transaction Type |
| TRANCATG.VSAM.KSDS | AWS.M2.CARDDEMO.TRANCATG.* | 6,0 | 60 | Transaction Category |
| USRSEC.VSAM.KSDS | AWS.M2.CARDDEMO.USRSEC.* | 8,0 | 80 | User Security |
