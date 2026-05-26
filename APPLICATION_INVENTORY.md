# APPLICATION INVENTORY — CardDemo COBOL Estate

> Generated from analysis of `uc-legacy-modernization-cobol-to-java/app/`

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

### 1.1 Batch Programs

| # | Filename | Lines | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|---------|---------------------|----------------------|
| 1 | **CBACT01C.cbl** | 430 | Read the account VSAM file, extract selected fields, and write into multiple output formats (flat, array, variable-length) | **Read:** ACCTFILE (VSAM KSDS) · **Write:** OUTFILE (seq), ARRYFILE (seq), VBRCFILE (seq) | CVACT01Y, CODATECN |
| 2 | **CBACT02C.cbl** | 178 | Read and print card data file for audit/verification | **Read:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | **CBACT03C.cbl** | 178 | Read and print card cross-reference file | **Read:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | **CBACT04C.cbl** | 652 | Interest calculator — process transaction category balances, compute interest and fees, update accounts, generate system transactions | **Read:** TCATBALF (VSAM KSDS), XREFFILE (VSAM KSDS/AIX), DISCGRP (VSAM KSDS) · **Read/Write:** ACCTFILE (VSAM KSDS) · **Write:** TRANSACT (seq) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | **CBCUS01C.cbl** | 178 | Read and print customer data file | **Read:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | **CBTRN01C.cbl** | 494 | Daily transaction lookup — read daily transactions, validate against cross-reference, card, and account files | **Read:** DALYTRAN (seq PS), CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE (all VSAM KSDS) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 7 | **CBTRN02C.cbl** | 731 | Post daily transactions — validate, post to transaction master, update accounts and category balances, write rejects | **Read:** DALYTRAN (seq PS), XREFFILE (VSAM KSDS) · **Read/Write:** ACCTFILE (VSAM KSDS), TCATBALF (VSAM KSDS) · **Write:** TRANFILE (VSAM KSDS), DALYREJS (GDG seq) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 8 | **CBTRN03C.cbl** | 649 | Transaction report writer — generate daily transaction report with totals by account, page, and grand totals | **Read:** TRANSACT (VSAM KSDS), XREFFILE (VSAM KSDS), TRANTYPE (VSAM KSDS), TRANCATG (VSAM KSDS), DATEPARM (seq) · **Write:** REPTFILE (GDG seq) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 9 | **CBSTM03A.CBL** | 924 | Statement generator — create customer statements with transaction details and HTML output | **Read:** XREFFILE, CUSTFILE, ACCTFILE, TRNXFILE (all VSAM KSDS) · **Write:** STMT-FILE (seq), HTML-FILE (seq) · **Call:** CBSTM03B | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 10 | **CBSTM03B.CBL** | 230 | Statement file I/O sub-module — handles OPEN/READ/WRITE/CLOSE for statement generation on behalf of CBSTM03A | **Read/Write:** TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE (all VSAM) | _(none — uses linkage section)_ |
| 11 | **CBEXPORT.cbl** | 582 | Export customer data from multiple VSAM files into a single multi-record export file for branch migration | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE (all VSAM KSDS) · **Write:** EXPFILE (VSAM KSDS) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 12 | **CBIMPORT.cbl** | 487 | Import data from multi-record export file, split into separate normalized files for target system | **Read:** EXPFILE (VSAM KSDS) · **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT (all seq PS) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 13 | **COBSWAIT.cbl** | 41 | Batch wait utility — calls MVSWAIT to pause execution for a specified interval | **Call:** MVSWAIT | _(none)_ |
| 14 | **CSUTLDTC.cbl** | 157 | Date conversion utility — converts dates between formats using LE callable services | **Call:** CEEDAYS | _(none)_ |

### 1.2 Online (CICS) Programs

| # | Filename | Lines | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|---------|---------------------|----------------------|
| 1 | **COSGN00C.cbl** | 260 | Sign-on screen — user authentication against USRSEC file, route to menu | **CICS READ:** USRSEC · **CICS XCTL:** COMEN01C/COADM01C | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | **COMEN01C.cbl** | 308 | Main menu — display and route user to selected functional area | **CICS XCTL:** target programs · **CICS INQUIRE** | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | **COADM01C.cbl** | 288 | Admin menu — display admin options and route to admin functions | **CICS XCTL:** target admin programs | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | **COACTVWC.cbl** | 941 | Account view — display account details with card cross-ref and customer info | **CICS READ:** ACCTDAT, CARDXREF (by AIX), CUSTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 5 | **COACTUPC.cbl** | 4236 | Account update — edit and update account and customer data with full field validation | **CICS READ/REWRITE:** ACCTDAT, CUSTDAT · **CICS READ:** CARDXREF | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY |
| 6 | **COCRDLIC.cbl** | 1459 | Credit card list — browse and display list of credit cards with paging | **CICS STARTBR/READNEXT/READPREV/ENDBR:** CARDDAT · **CICS XCTL** | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | **COCRDSLC.cbl** | 887 | Credit card detail view — display card details with customer data | **CICS READ:** CARDDAT, CUSTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | **COCRDUPC.cbl** | 1560 | Credit card update — edit and update card data | **CICS READ/REWRITE:** CARDDAT · **CICS READ:** CUSTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | **COTRN00C.cbl** | 699 | Transaction list — browse transactions with paging support | **CICS STARTBR/READNEXT/READPREV/ENDBR:** TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | **COTRN01C.cbl** | 330 | Transaction view — display a single transaction's details | **CICS READ:** TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | **COTRN02C.cbl** | 783 | Transaction add — add a new transaction record | **CICS READ:** CXACAIX/CCXREF · **CICS STARTBR/READPREV/ENDBR/WRITE:** TRANSACT | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | **CORPT00C.cbl** | 649 | Transaction report request — date selection screen, submit report JCL to internal reader | **CICS WRITEQ TD:** JOBS (internal reader) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | **COBIL00C.cbl** | 572 | Bill payment — process bill payments, create transaction, update account balance | **CICS READ/REWRITE:** ACCTDAT · **CICS READ:** CXACAIX · **CICS STARTBR/READPREV/ENDBR/WRITE:** TRANSACT | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | **COUSR00C.cbl** | 695 | User list — browse user security records with paging | **CICS STARTBR/READNEXT/READPREV:** USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | **COUSR01C.cbl** | 299 | User add — add new user security record | **CICS WRITE:** USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | **COUSR02C.cbl** | 414 | User update — update existing user security record | **CICS READ/REWRITE:** USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | **COUSR03C.cbl** | 359 | User delete — delete user security record | **CICS READ/DELETE:** USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Lines | Type | Purpose | Key I/O | Copybooks |
|---|----------|-------|------|---------|---------|-----------|
| 1 | **CBPAUP0C.cbl** | 386 | Batch (IMS) | Delete expired pending authorization messages from IMS DB | **EXEC DLI:** GN, GNP, DLET, CHKP on Auth DB | CIPAUSMY, CIPAUDTY |
| 2 | **COPAUA0C.cbl** | 1026 | Online (CICS/IMS/MQ) | Card authorization decision — read MQ request, look up card/account, query IMS auth DB, write MQ reply | **MQ:** MQOPEN, MQGET (request queue) · **CICS READ:** XREF, ACCT files · **EXEC DLI:** SCHD, GU, TERM | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY |
| 3 | **COPAUS0C.cbl** | 1032 | Online (CICS/IMS/BMS) | Summary view of pending authorization messages with paging | **EXEC DLI:** GNP · **CICS READ:** ACCT files · **CICS SEND/RECEIVE** | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y |
| 4 | **COPAUS1C.cbl** | 604 | Online (CICS/IMS/BMS) | Detail view of single authorization message, mark-as-fraud capability | **EXEC DLI:** GU, GNP, REPL · **CICS LINK:** COPAUS2C | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID |
| 5 | **COPAUS2C.cbl** | 244 | Online (CICS/IMS/DB2) | Mark authorization message as fraud — update DB2 fraud records table | **EXEC SQL:** INSERT/UPDATE AUTHFRDS · **EXEC DLI** (implicit) | CIPAUDTY |
| 6 | **PAUDBLOD.CBL** | 369 | Batch (IMS) | Load pending authorization data from flat files into IMS DB | **Read:** INFILE1, INFILE2 (seq) · **CALL CBLTDLI:** ISRT, GU | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 7 | **PAUDBUNL.CBL** | 317 | Batch (IMS) | Unload IMS authorization DB to flat files | **CALL CBLTDLI:** GN, GNP · **Write:** OPFILE1, OPFILE2 (seq) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | **DBUNLDGS.CBL** | 366 | Batch (IMS) | Unload IMS authorization DB to GSAM files for backup | **CALL CBLTDLI:** GN, GNP, ISRT (GSAM) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 Transaction Type DB2 Module (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Lines | Type | Purpose | Key I/O | Copybooks |
|---|----------|-------|------|---------|---------|-----------|
| 1 | **COTRTLIC.cbl** | 2098 | Online (CICS/DB2) | List transaction types with paging via DB2 cursors — supports update and delete selection | **EXEC SQL:** SELECT with cursor on TRANSACTION_TYPE · **CICS SEND/RECEIVE** | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CSDB2RWY, DCLTRTYP |
| 2 | **COTRTUPC.cbl** | 1702 | Online (CICS/DB2) | Transaction type update/delete — CRUD on TRANSACTION_TYPE and TRANSACTION_CATEGORY DB2 tables | **EXEC SQL:** SELECT, INSERT, UPDATE, DELETE on TRNTYPE/TRNTYCAT · **CICS SEND/RECEIVE** | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, DCLTRTYP, DCLTRCAT |
| 3 | **COBTUPDT.cbl** | 237 | Batch (DB2) | Batch update transaction types from flat file input — INSERT, UPDATE, DELETE based on action codes | **Read:** TR-RECORD (seq) · **EXEC SQL:** INSERT, UPDATE, DELETE on TRANSACTION_TYPE | DCLTRTYP |

### 2.3 VSAM-MQ Module (`app/app-vsam-mq/cbl/`)

| # | Filename | Lines | Type | Purpose | Key I/O | Copybooks |
|---|----------|-------|------|---------|---------|-----------|
| 1 | **COACCT01.cbl** | 620 | Online (CICS/MQ) | Account-level MQ message processor — read request from MQ queue, look up account, write response to MQ queue | **MQ:** MQOPEN, MQGET, MQPUT · **CICS READ:** ACCT file · **CICS RETRIEVE** | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 2 | **CODATE01.cbl** | 524 | Online (CICS/MQ) | Date-based MQ message processor — similar to COACCT01 but for date-related queries | **MQ:** MQOPEN, MQGET, MQPUT · **CICS READ/RETRIEVE** | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. JCL Job Catalog (`app/jcl/`)

### 3.1 Data Definition & VSAM Setup Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | **ACCTFILE.jcl** | Delete/define account VSAM KSDS, load from flat file | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO |
| 2 | **CARDFILE.jcl** | Delete/define card VSAM KSDS + AIX + PATH, load from flat, build AIX | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO → STEP40: DEFINE AIX → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: SDSF open CICS files |
| 3 | **CUSTFILE.jcl** | Delete/define customer VSAM KSDS, load from flat file | CLCIFIL: SDSF close → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → OPCIFIL: SDSF open |
| 4 | **XREFFILE.jcl** | Delete/define card cross-reference VSAM KSDS + AIX, load, build AIX | Similar pattern with AIX for account ID |
| 5 | **TRANFILE.jcl** | Delete/define transaction master VSAM KSDS, load from flat | DELETE → DEFINE → REPRO |
| 6 | **TRANTYPE.jcl** | Delete/define transaction type VSAM KSDS, load from flat | DELETE → DEFINE → REPRO |
| 7 | **TRANCATG.jcl** | Delete/define transaction category VSAM KSDS, load from flat | DELETE → DEFINE → REPRO |
| 8 | **TCATBALF.jcl** | Delete/define transaction category balance VSAM KSDS, load from flat | DELETE → DEFINE → REPRO |
| 9 | **DISCGRP.jcl** | Delete/define disclosure group VSAM KSDS, load from flat | DELETE → DEFINE → REPRO |
| 10 | **DUSRSECJ.jcl** | Create user security PS file from in-stream data, define USRSEC VSAM KSDS, load | PREDEL: IEFBR14 → STEP01: IEBGENER (in-stream data) → STEP02: IDCAMS DEFINE → STEP03: IDCAMS REPRO |
| 11 | **ESDSRRDS.jcl** | Create ESDS and RRDS variants of user security VSAM for testing | PREDEL → STEP01: IEBGENER → STEP02: DEFINE ESDS → STEP03: REPRO → STEP04: DEFINE RRDS → STEP05: REPRO |
| 12 | **DEFCUST.jcl** | Alternative customer VSAM definition (different naming convention) | STEP05: DELETE → STEP05: DEFINE |
| 13 | **TRANIDX.jcl** | Define alternate index on transaction file | DEFINE AIX → DEFINE PATH → BLDINDEX |

### 3.2 GDG Definition Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 14 | **DEFGDGB.jcl** | Define all GDG bases for batch processing | STEP05: DEFINE GDG (TRANSACT.BKUP, TRANSACT.DALY, TRANREPT, TCATBALF.BKUP, SYSTRAN, TRANSACT.COMBINED) |
| 15 | **DEFGDGD.jcl** | Define GDGs for DB2 reference data + initial generation load | STEP10–60: DEFINE GDG + IEBGENER for TRANTYPE.BKUP, TRANCATG.PS.BKUP, DISCGRP.BKUP |
| 16 | **DALYREJS.jcl** | Define GDG base for daily rejects | STEP05: DEFINE GDG (DALYREJS) |
| 17 | **REPTFILE.jcl** | Define GDG base for transaction report output | DEFINE GDG (TRANREPT) |

### 3.3 Batch Processing Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 18 | **READACCT.jcl** | Run CBACT01C — read account file, write multiple output formats | PREDEL: IEFBR14 (clean output) → STEP05: PGM=CBACT01C |
| 19 | **READCARD.jcl** | Run CBACT02C — read and print card data | STEP05: PGM=CBACT02C |
| 20 | **READCUST.jcl** | Run CBCUS01C — read and print customer data | STEP05: PGM=CBCUS01C |
| 21 | **READXREF.jcl** | Run CBACT03C — read and print cross-reference data | STEP05: PGM=CBACT03C |
| 22 | **INTCALC.jcl** | Run CBACT04C — interest and fee calculation | STEP15: PGM=CBACT04C (with PARM='2022071800') |
| 23 | **POSTTRAN.jcl** | Run CBTRN02C — post daily transactions, write rejects | STEP15: PGM=CBTRN02C |
| 24 | **TRANREPT.jcl** | Run CBTRN03C — generate daily transaction report | PGM=CBTRN03C |
| 25 | **CREASTMT.JCL** | Run CBSTM03A — create customer statements | PGM=CBSTM03A |
| 26 | **PRTCATBL.jcl** | Print transaction category balance file (sort + format) | DELDEF → STEP05R: PROC=REPROC (REPRO from VSAM) → STEP10R: SORT |
| 27 | **CBEXPORT.jcl** | Run CBEXPORT — export data to multi-record file | STEP01: IDCAMS (define export cluster) → STEP02: PGM=CBEXPORT |
| 28 | **CBIMPORT.jcl** | Run CBIMPORT — import from export file to normalized output | STEP01: PGM=CBIMPORT |
| 29 | **COMBTRAN.jcl** | Combine transaction backups and system transactions, reload to VSAM | STEP05R: SORT (merge TRANSACT.BKUP + SYSTRAN) → STEP10: IDCAMS REPRO to VSAM |
| 30 | **TRANBKP.jcl** | Backup transaction VSAM to GDG | REPRO from VSAM to GDG generation |
| 31 | **WAITSTEP.jcl** | Run COBSWAIT — batch wait/delay utility | PGM=COBSWAIT |

### 3.4 CICS Administration Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 32 | **CBADMCDJ.jcl** | Create CICS CSD resources — define all mapsets, programs, and transactions for CardDemo | STEP1: PGM=DFHCSDUP with DEFINE commands |
| 33 | **OPENFIL.jcl** | Open VSAM files in CICS region | OPCIFIL: SDSF CEMT SET FIL ... OPE |
| 34 | **CLOSEFIL.jcl** | Close VSAM files in CICS region | CLCIFIL: SDSF CEMT SET FIL ... CLO |

### 3.5 Utility & Internal Reader Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 35 | **INTRDRJ1.JCL** | Internal reader JCL — template for batch job submission from CICS (report generation) | PGM=CBTRN03C (submitted via CORPT00C) |
| 36 | **INTRDRJ2.JCL** | Internal reader JCL — alternate batch job submission template | Similar to INTRDRJ1 |
| 37 | **FTPJCL.JCL** | FTP transfer utility JCL | FTP commands for data transfer |
| 38 | **TXT2PDF1.JCL** | Convert text report to PDF | PGM=TXT2PDF |

### 3.6 Sub-Application JCL

#### Authorization Module (`app/app-authorization-ims-db2-mq/jcl/`)

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 39 | **CBPAUP0J.jcl** | Run CBPAUP0C — purge expired auth messages | PGM=DFSRRC00 (IMS BMP) |
| 40 | **DBPAUTP0.jcl** | Initial load of IMS auth DB from flat files | PGM=DFSRRC00 (IMS BMP) calling PAUDBLOD |
| 41 | **LOADPADB.JCL** | Load pending auth DB | IMS DB load |
| 42 | **UNLDPADB.JCL** | Unload pending auth DB | IMS DB unload via PAUDBUNL |
| 43 | **UNLDGSAM.JCL** | Unload auth DB to GSAM | PGM=DFSRRC00 calling DBUNLDGS |

#### Transaction Type DB2 Module (`app/app-transaction-type-db2/jcl/`)

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 44 | **CREADB21.jcl** | Create DB2 tables for transaction types | EXEC SQL DDL |
| 45 | **TRANEXTR.jcl** | Extract transaction type data from VSAM to DB2 | PGM=COBTUPDT |
| 46 | **MNTTRDB2.jcl** | Maintain transaction type DB2 tables | Maintenance steps |

---

## 4. Summary Statistics

| Metric | Count |
|--------|-------|
| **Total COBOL programs** | 44 |
| — Main app (`app/cbl/`) | 31 |
| — Authorization module | 8 |
| — Transaction type DB2 module | 3 |
| — VSAM-MQ module | 2 |
| **Batch programs** | 17 |
| **Online (CICS) programs** | 22 |
| **Batch/IMS programs** | 3 |
| **Online (CICS/MQ) programs** | 2 |
| **Total JCL jobs** | 46 |
| **Copybooks (main app/cpy/)** | 30 |
| **BMS map definitions** | 17+ |
| **Total lines of COBOL** | ~26,000+ |
