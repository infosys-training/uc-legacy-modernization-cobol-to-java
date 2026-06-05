# CardDemo Application Inventory

> Auto-generated estate catalog for the AWS CardDemo credit card management system.

## Summary

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Main programs (`app/cbl/`) | 31 |
| Sub-application programs | 13 |
| Batch programs | 16 |
| Online (CICS) programs | 21 |
| Sub-app (IMS/DB2/MQ) programs | 7 |
| JCL jobs (`app/jcl/` + sub-apps) | 46 |
| Copybooks | 47 |
| BMS screen maps | 17 |
| Total lines of COBOL | 27,350 |

---

## 1. COBOL Programs — `app/cbl/`

### 1.1 Batch Programs

| # | Filename | LOC | Purpose | Key I/O (Files Read / Written) | Copybooks Referenced |
|---|----------|-----|---------|-------------------------------|----------------------|
| 1 | CBACT01C.cbl | 430 | Read account file and write to multiple output formats (fixed, array, variable-length) | R: ACCTFILE | W: OUT-FILE, ARRY-FILE, VBRC-FILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Read and display card data file | R: CARDFILE | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Read and display account cross-reference file | R: XREFFILE | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Interest calculation — compute interest on account balances using disclosure group rates | R: TCATBAL, XREF, DISCGRP, ACCOUNT | W: TRANSACT | RW: ACCOUNT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Read and display customer data file | R: CUSTFILE | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Export complete customer profiles from CardDemo files into a consolidated sequential export file for branch migration | R: CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD | W: EXPORT-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Import data from consolidated export file back into individual CardDemo VSAM files with validation | R: EXPORT-INPUT | W: CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD, ERROR-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBTRN01C.cbl | 494 | Validate daily transactions against cross-reference, account, and card files | R: DALYTRAN, CUSTOMER, XREF, CARD, ACCOUNT, TRANSACT | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | CBTRN02C.cbl | 731 | Post daily transactions to master transaction file — core batch posting engine | R: DALYTRAN, XREF, ACCOUNT, TCATBAL | W: TRANSACT, DALYREJS | RW: ACCOUNT, TCATBAL | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | 649 | Generate daily transaction report with category/type lookups | R: TRANSACT, XREF, TRANTYPE, TRANCATG, DATE-PARMS | W: REPORT-FILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | CBSTM03A.CBL | 924 | Generate customer statements in text and HTML format — main driver | R: (via CBSTM03B) TRNX, XREF, CUST, ACCT | W: STMT-FILE, HTML-FILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 12 | CBSTM03B.CBL | 230 | I/O submodule for statement generation — handles all file read/write operations | R: TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE | (none — data passed via LINKAGE) |
| 13 | COBSWAIT.cbl | 41 | Wait/delay utility — calls assembler MVSWAIT routine | (none) | (none) |
| 14 | CSUTLDTC.cbl | 157 | Date validation utility — converts and validates dates using LE CEEDAYS | (none) | (none) |

### 1.2 Online (CICS) Programs

| # | Filename | LOC | Purpose | Key I/O (VSAM/DB2) | BMS Map | Copybooks Referenced |
|---|----------|-----|---------|--------------------|---------|-----------------------|
| 15 | COSGN00C.cbl | 260 | Sign-on screen — user authentication, routes to Admin or User menu | R: USRSEC (CICS READ) | COSGN00 | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COADM01C.cbl | 288 | Admin menu — hub for user management and transaction type admin | (none — routing only) | COADM01 | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COMEN01C.cbl | 308 | Main user menu — central hub routing to 11 functional screens | (none — routing only) | COMEN01 | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | COACTUPC.cbl | 4,236 | Account update — exhaustive field validation (date, SSN, phone, state, ZIP, FICO). Largest program in estate | R: ACCTFILE, CARDFILE, CUSTFILE (CICS READ) | RW: ACCTFILE, CUSTFILE (REWRITE) | COACTUP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×39), CSSTRPFY, CSUTLDPY |
| 19 | COACTVWC.cbl | 941 | Account view — read-only account detail display with card and customer info | R: ACCTFILE, CARDFILE, CUSTFILE (CICS READ) | COACTVW | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 20 | COCRDLIC.cbl | 1,459 | Credit card list — paginated browse of card records using STARTBR/READNEXT/ENDBR | R: CARDFILE (CICS STARTBR/READNEXT/ENDBR) | COCRDLI | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 21 | COCRDSLC.cbl | 887 | Credit card detail view — display single card record with account and customer info | R: CARDFILE, ACCTFILE (CICS READ) | COCRDSL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 22 | COCRDUPC.cbl | 1,560 | Credit card update — modify card details with validation | R: CARDFILE (CICS READ) | RW: CARDFILE (REWRITE) | COCRDUP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 23 | COTRN00C.cbl | 699 | Transaction list — paginated transaction browse with STARTBR/READNEXT/READPREV | R: TRANSACT (CICS STARTBR/READNEXT/READPREV/ENDBR) | COTRN00 | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | COTRN01C.cbl | 330 | Transaction detail view — display single transaction record | R: TRANSACT (CICS READ) | COTRN01 | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN02C.cbl | 783 | Transaction add — create new transaction with date validation and cross-reference lookup | R: CARDXREF, TRANSACT (CICS READ/STARTBR/READPREV) | W: TRANSACT (CICS WRITE) | COTRN02 | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 26 | CORPT00C.cbl | 649 | Transaction report request screen — validates date range and submits JCL batch jobs via INTRDR | W: TDQ JOBS (CICS WRITEQ TD) | CORPT00 | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | COBIL00C.cbl | 572 | Bill payment — process payments against account balances | R: ACCTFILE, CARDXREF, TRANSACT (CICS READ/STARTBR/READPREV) | RW: ACCTFILE (REWRITE) | W: TRANSACT (WRITE) | COBIL00 | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 28 | COUSR00C.cbl | 695 | User list — paginated browse of security user records | R: USRSEC (CICS STARTBR/READNEXT/READPREV) | COUSR00 | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR01C.cbl | 299 | User add — create new security user record | W: USRSEC (CICS WRITE) | COUSR01 | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR02C.cbl | 414 | User update — modify existing security user record | R: USRSEC (CICS READ) | RW: USRSEC (REWRITE) | COUSR02 | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | COUSR03C.cbl | 359 | User delete — remove security user record | R: USRSEC (CICS READ) | D: USRSEC (CICS DELETE) | COUSR03 | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | LOC | Classification | Purpose | Key I/O | Copybooks |
|---|----------|-----|---------------|---------|---------|-----------|
| 32 | COPAUA0C.cbl | 1,026 | Online (CICS+IMS+MQ) | Authorization decision engine — reads MQ request, looks up IMS auth history, reads VSAM files, sends MQ reply. Spans 3 subsystems | MQ: MQOPEN/MQGET/MQPUT1/MQCLOSE | IMS: SCHD/TERM/GU/REPL/ISRT | CICS: READ (XREF, ACCT, CUST) | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | COPAUS0C.cbl | 1,032 | Online (CICS+IMS) | Pending authorization summary browse — paginated IMS segment browse with CICS screens | IMS: GU/GNP/SCHD/TERM | CICS: READ (CARD, ACCT, CUST), SYNCPOINT | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | COPAUS1C.cbl | 604 | Online (CICS+IMS) | Pending authorization detail with update capability — browse auth details, mark fraud | IMS: GU/GNP/REPL/SCHD/TERM | CICS: LINK to COPAUS2C, SYNCPOINT | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS2C.cbl | 244 | Online (CICS+DB2) | Fraud flag recording — insert fraud events into DB2 AUTHFRDS table | DB2: EXEC SQL INSERT/SELECT | CICS: ASKTIME/FORMATTIME | CIPAUDTY |
| 36 | CBPAUP0C.cbl | 386 | Batch (IMS) | Purge expired pending authorization records from IMS database | IMS: GN/GNP/DLET/CHKP | CIPAUSMY, CIPAUDTY |
| 37 | PAUDBLOD.CBL | 369 | Batch (IMS) | Load pending authorization data into IMS database from flat files | IMS: ISRT/GU (via CBLTDLI) | R: INFILE1, INFILE2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 38 | PAUDBUNL.CBL | 317 | Batch (IMS) | Unload IMS database to sequential flat files | IMS: GN/GNP (via CBLTDLI) | W: OPFILE1, OPFILE2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | DBUNLDGS.CBL | 366 | Batch (IMS/GSAM) | GSAM unload utility — unload IMS data via GSAM interface | IMS: GN/GNP/ISRT (via CBLTDLI) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 Transaction Type (DB2) — `app/app-transaction-type-db2/cbl/`

| # | Filename | LOC | Classification | Purpose | Key I/O | Copybooks |
|---|----------|-----|---------------|---------|---------|-----------|
| 40 | COTRTLIC.cbl | 2,098 | Online (CICS+DB2) | Transaction type list — cursor-based DB2 pagination with select/update/delete | DB2: DECLARE CURSOR/OPEN/FETCH/CLOSE, DELETE, UPDATE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, DCLTRTYP |
| 41 | COTRTUPC.cbl | 1,702 | Online (CICS+DB2) | Transaction type maintenance — CRUD operations with cascading category deletes | DB2: SELECT/INSERT/UPDATE/DELETE with DCLTRTYP/DCLTRCAT | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT |
| 42 | COBTUPDT.cbl | 237 | Batch (DB2) | Batch DB2 maintenance — load transaction types from file, delete/insert | DB2: DELETE/INSERT/SELECT | R: TR-RECORD | DCLTRTYP |

### 2.3 VSAM/MQ — `app/app-vsam-mq/cbl/`

| # | Filename | LOC | Classification | Purpose | Key I/O | Copybooks |
|---|----------|-----|---------------|---------|---------|-----------|
| 43 | COACCT01.cbl | 620 | Online (CICS+MQ) | Account inquiry via MQ — receive request from MQ, read VSAM account, send response | MQ: MQOPEN/MQGET/MQPUT/MQCLOSE | CICS: RETRIEVE, READ (ACCTFILE) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 44 | CODATE01.cbl | 524 | Online (CICS+MQ) | Date inquiry via MQ — receive date request from MQ, format and return date | MQ: MQOPEN/MQGET/MQPUT/MQCLOSE | CICS: RETRIEVE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. JCL Jobs — `app/jcl/`

### 3.1 VSAM Cluster Definition Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | ACCTFILE.jcl | Define account VSAM KSDS cluster and load data | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER (KEYS 11,0 RECSZ 300) → STEP15: IDCAMS REPRO |
| 2 | CARDFILE.jcl | Define card data VSAM KSDS with alternate index on ACCT-ID | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE → STEP10: DEFINE CLUSTER (KEYS 16,0 RECSZ 150) → STEP15: REPRO → STEP40: DEFINE AIX (KEYS 11,16 NONUNIQUEKEY) → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: SDSF open CICS files |
| 3 | CUSTFILE.jcl | Define customer VSAM KSDS cluster | CLCIFIL: SDSF close → STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → OPCIFIL: SDSF open |
| 4 | XREFFILE.jcl | Define card cross-reference VSAM KSDS with AIX | Similar to CARDFILE — DELETE/DEFINE/REPRO/AIX/PATH/BLDINDEX |
| 5 | TRANFILE.jcl | Define transaction master VSAM KSDS with AIX | CLCIFIL → STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP20-30: AIX/PATH/BLDINDEX → OPCIFIL |
| 6 | DISCGRP.jcl | Define disclosure group VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| 7 | TCATBALF.jcl | Define transaction category balance VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| 8 | TRANTYPE.jcl | Define transaction type VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| 9 | TRANCATG.jcl | Define transaction category VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| 10 | DUSRSECJ.jcl | Define user security VSAM file | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02: IDCAMS DELETE → STEP03: IDCAMS DEFINE |
| 11 | ESDSRRDS.jcl | Define ESDS and RRDS VSAM files | PREDEL → STEP01: IEBGENER → STEP02-05: IDCAMS DEFINE (ESDS, RRDS, etc.) |

### 3.2 Batch Processing Jobs

| # | Job Name | Program Executed | Purpose | Step Sequence |
|---|----------|-----------------|---------|---------------|
| 12 | POSTTRAN.jcl | CBTRN02C | Post daily transactions to master file | STEP15: EXEC PGM=CBTRN02C |
| 13 | INTCALC.jcl | CBACT04C | Calculate interest on account balances | STEP15: EXEC PGM=CBACT04C (PARM=date) |
| 14 | CREASTMT.JCL | CBSTM03A | Generate customer statements (text + HTML) | Multi-step: compile + execute CBSTM03A |
| 15 | TRANREPT.jcl | CBTRN03C | Generate daily transaction report | STEP05R: SORT (via REPROC) → STEP05R: SORT → STEP10R: EXEC PGM=CBTRN03C |
| 16 | CBEXPORT.jcl | CBEXPORT | Export data for branch migration | STEP01: IDCAMS (prepare) → STEP02: EXEC PGM=CBEXPORT |
| 17 | CBIMPORT.jcl | CBIMPORT | Import data from export file | STEP01: EXEC PGM=CBIMPORT |
| 18 | READACCT.jcl | CBACT01C | Read and display account data | PREDEL: IEFBR14 → STEP05: EXEC PGM=CBACT01C |
| 19 | READCARD.jcl | CBACT02C | Read and display card data | STEP05: EXEC PGM=CBACT02C |
| 20 | READCUST.jcl | CBCUS01C | Read and display customer data | STEP05: EXEC PGM=CBCUS01C |
| 21 | READXREF.jcl | CBACT03C | Read and display cross-reference data | STEP05: EXEC PGM=CBACT03C |
| 22 | WAITSTEP.jcl | COBSWAIT | Wait/delay step for job sequencing | WAIT: EXEC PGM=COBSWAIT |

### 3.3 Utility Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 23 | CLOSEFIL.jcl | Close VSAM files in CICS region | CLCIFIL: SDSF (CEMT SET FIL CLO) |
| 24 | OPENFIL.jcl | Open VSAM files in CICS region | OPCIFIL: SDSF (CEMT SET FIL OPE) |
| 25 | COMBTRAN.jcl | Combine/sort daily transaction files | STEP05R: SORT → STEP10: IDCAMS REPRO |
| 26 | CBADMCDJ.jcl | Create CICS CSD resource definitions for CardDemo | STEP1: DFHCSDUP (define programs, transactions, files, TDQ) |
| 27 | DEFCUST.jcl | Define customer file (alternate definition) | STEP05: IDCAMS (×2) |
| 28 | DEFGDGB.jcl | Define GDG base entries for statement/report versioning | STEP05: IDCAMS DEFINE GDG |
| 29 | DEFGDGD.jcl | Define DB2-related GDG entries | STEP10-60: IDCAMS DEFINE + IEBGENER |
| 30 | DALYREJS.jcl | Define GDG for daily rejection files | STEP05: IDCAMS DEFINE GDG |
| 31 | REPTFILE.jcl | Define GDG for report files | STEP05: IDCAMS DEFINE GDG |
| 32 | TRANBKP.jcl | Backup transaction master via REPRO then delete cluster | STEP05R: REPROC → STEP05: IDCAMS REPRO → STEP10: IDCAMS DELETE |
| 33 | TRANIDX.jcl | Define alternate index on transaction master | STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX |
| 34 | PRTCATBL.jcl | Print transaction category balance file | DELDEF: IEFBR14 → STEP05R: REPROC → STEP10R: SORT |
| 35 | INTRDRJ1.JCL | Internal reader job — submit transaction report batch | (submitted by CORPT00C via CICS TDQ) |
| 36 | INTRDRJ2.JCL | Internal reader job — submit statement generation batch | (submitted by CORPT00C via CICS TDQ) |
| 37 | TXT2PDF1.JCL | Convert text report to PDF | (utility conversion) |
| 38 | FTPJCL.JCL | FTP file transfer utility | (file transfer) |

### 3.4 Sub-Application JCL — `app/app-authorization-ims-db2-mq/jcl/`

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 39 | CBPAUP0J.jcl | Execute expired authorization purge | STEP01: DFSRRC00 (IMS batch: CBPAUP0C) |
| 40 | DBPAUTP0.jcl | Unload IMS pending auth database | STEPDEL: IEFBR14 → UNLOAD: DFSRRC00 |
| 41 | LOADPADB.JCL | Load IMS pending auth database | STEP01: DFSRRC00 (IMS batch: PAUDBLOD) |
| 42 | UNLDPADB.JCL | Unload IMS pending auth database (alt) | STEP0: IEFBR14 → STEP01: DFSRRC00 (PAUDBUNL) |
| 43 | UNLDGSAM.JCL | GSAM unload utility | STEP01: DFSRRC00 (DBUNLDGS) |

### 3.5 Sub-Application JCL — `app/app-transaction-type-db2/jcl/`

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 44 | CREADB21.jcl | Create DB2 tables and load initial data | FREEPLN: IKJEFT01 → CRCRDDB: IKJEFT01 → LDTTYPE: IEFBR14 → RUNTEP2: IKJEFT01 → LDTCCAT: IKJEFT01 |
| 45 | MNTTRDB2.jcl | DB2 maintenance for transaction types | STEP1: IKJEFT01 (COBTUPDT) |
| 46 | TRANEXTR.jcl | Extract transaction type data from DB2 | STEP10-20: IEBGENER → STEP30: IEFBR14 → STEP40-50: IKJEFT01 |
