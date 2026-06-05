# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Generated:** 2026-06-05 | **Repository:** `uc-legacy-modernization-cobol-to-java`
> **Total Programs:** 44 | **Total LOC:** 30,175 | **Copybooks:** 61 | **JCL Jobs:** 46

---

## 1. Summary Statistics

| Metric | Value |
|--------|-------|
| COBOL programs (main `app/cbl/`) | 31 |
| COBOL programs (sub-app — IMS/DB2/MQ) | 8 |
| COBOL programs (sub-app — DB2) | 3 |
| COBOL programs (sub-app — VSAM/MQ) | 2 |
| Total lines of COBOL | 30,175 |
| Copybooks (`app/cpy/` + sub-app) | 61 |
| BMS screen maps (`app/bms/` + sub-app) | 19 |
| JCL jobs (all directories) | 46 |
| Average LOC per program | 686 |
| Largest program | COACTUPC.cbl (4,236 LOC) |

---

## 2. Program Inventory — `app/cbl/` (31 programs)

### 2.1 Online (CICS) Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 1 | COSGN00C.cbl | 260 | Sign-on / authentication screen — validates user credentials, routes admin vs. regular user | Online (CICS) | CICS SEND/RECEIVE MAP, CICS READ (USRSEC file) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | COADM01C.cbl | 288 | Admin menu hub — presents admin options (user CRUD, tran-type mgmt), routes via XCTL | Online (CICS) | CICS SEND/RECEIVE MAP, CICS XCTL | COADM01, COADM02Y, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COMEN01C.cbl | 308 | Main menu hub — central navigation for all user functions (11 XCTL targets) | Online (CICS) | CICS SEND/RECEIVE MAP, CICS XCTL | COCOM01Y, COMEN01, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTUPC.cbl | 4,236 | Account update — comprehensive field validation (date, SSN, phone, state, ZIP, FICO), reads/rewrites account, card-xref, customer VSAM files | Online (CICS) | CICS READ/REWRITE (ACCTFILE, CARDXREF, CUSTFILE), CICS SEND/RECEIVE MAP | COCOM01Y, COACTUP, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSSTRPFY, CSUSR01Y, CSUTLDPY, CSUTLDWY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 5 | COACTVWC.cbl | 941 | Account view — read-only display of account, card, and customer information | Online (CICS) | CICS READ (ACCTFILE, CARDXREF, CUSTFILE, CARDFILE), CICS SEND/RECEIVE MAP | COCOM01Y, COACTVW, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 6 | COCRDLIC.cbl | 1,459 | Credit card list — paginated browse of cards using STARTBR/READNEXT/READPREV/ENDBR pattern | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR (CARDFILE), CICS SEND/RECEIVE MAP, CICS XCTL | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 7 | COCRDSLC.cbl | 887 | Credit card detail view — displays full card record with customer info | Online (CICS) | CICS READ (CARDFILE, CUSTFILE, CARDXREF), CICS SEND/RECEIVE MAP | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 8 | COCRDUPC.cbl | 1,560 | Credit card update — edit card details with validation, REWRITE to VSAM | Online (CICS) | CICS READ/REWRITE (CARDFILE, CUSTFILE, CARDXREF), CICS SEND/RECEIVE MAP, CICS XCTL | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 9 | COTRN00C.cbl | 699 | Transaction list — paginated browse of transactions by account | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR (TRANSACT), CICS SEND/RECEIVE MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | 330 | Transaction detail view — display single transaction record | Online (CICS) | CICS SEND/RECEIVE MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | 783 | Transaction add — enter new transaction with validation, calls CSUTLDTC for date conversion | Online (CICS) | CICS READ (ACCTFILE, CARDXREF), CICS WRITE (TRANSACT), CICS SEND/RECEIVE MAP | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 12 | CORPT00C.cbl | 649 | Report request — submits batch JCL (INTRDRJ1/J2) via internal reader for report generation | Online (CICS) | CICS SEND/RECEIVE MAP, CICS XCTL, CALL 'CSUTLDTC' | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | COBIL00C.cbl | 572 | Bill payment — process payment transactions against an account | Online (CICS) | CICS READ/REWRITE (ACCTFILE, CARDXREF), CICS WRITE (TRANSACT), CICS SEND/RECEIVE MAP | COBIL00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | 695 | User list — paginated display of security user records, routes to add/update/delete | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR (USRSEC), CICS SEND/RECEIVE MAP, CICS XCTL | COCOM01Y, COTTL01Y, COUSR00, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | 299 | User add — create new security user record | Online (CICS) | CICS WRITE (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, COUSR01, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | 414 | User update — modify existing user record, REWRITE to USRSEC | Online (CICS) | CICS READ/REWRITE (USRSEC), CICS SEND/RECEIVE MAP, CICS XCTL | COCOM01Y, COTTL01Y, COUSR02, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | 359 | User delete — delete security user record from USRSEC VSAM | Online (CICS) | CICS READ/DELETE (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, COUSR03, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 2.2 Batch Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 18 | CBACT01C.cbl | 430 | Account file reader — reads VSAM account file, writes to sequential files in multiple formats (FB, array, VB), calls assembler COBDATFT for date formatting | Batch | READ ACCTFILE, WRITE OUT-FILE/ARRY-FILE/VBRC-FILE, CALL 'COBDATFT', CALL 'CEE3ABD' | CVACT01Y, CODATECN |
| 19 | CBACT02C.cbl | 178 | Card file reader — reads and prints card data file records | Batch | READ CARDFILE, CALL 'CEE3ABD' | CVACT02Y |
| 20 | CBACT03C.cbl | 178 | Cross-reference reader — reads and prints account-card cross-reference records | Batch | READ XREFFILE, CALL 'CEE3ABD' | CVACT03Y |
| 21 | CBACT04C.cbl | 652 | Interest calculation — calculates interest on account balances using category rates and disclosure groups, rewrites account records | Batch | READ TCATBAL/XREF/DISCGRP/ACCOUNT, REWRITE ACCOUNT, WRITE TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 22 | CBCUS01C.cbl | 178 | Customer file reader — reads and prints customer data file records | Batch | READ CUSTFILE, CALL 'CEE3ABD' | CVCUS01Y |
| 23 | CBSTM03A.CBL | 924 | Statement generation (main) — creates customer account statements in plain text and HTML formats from transaction data, uses ALTER/GO TO, COMP/COMP-3, 2D arrays | Batch | OPEN/CLOSE/WRITE STMT-FILE/HTML-FILE, CALL 'CBSTM03B' (12 calls) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 24 | CBSTM03B.CBL | 230 | Statement I/O submodule — called by CBSTM03A, handles all file I/O for xref, customer, account, and transaction files | Batch (submodule) | OPEN/READ/CLOSE (XREFFILE, CUSTFILE, ACCTFILE, TRNXFILE) | _(none — receives data via LINKAGE SECTION)_ |
| 25 | CBTRN01C.cbl | 494 | Transaction file reader — reads transaction file with cross-reference and customer lookups, displays transaction details | Batch | READ TRANSACT/CARDXREF/CUSTFILE/ACCTFILE/CARDFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVTRA06Y |
| 26 | CBTRN02C.cbl | 731 | Transaction posting — posts daily transactions from sequential file to VSAM transaction master and updates account balances | Batch | READ DAILYTRAN/CARDXREF/ACCOUNT, WRITE/REWRITE TRANSACT, REWRITE ACCOUNT | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y |
| 27 | CBTRN03C.cbl | 649 | Transaction reporting — generates daily transaction report with card cross-reference and type lookups, writes to sequential report file | Batch | READ TRANFILE/CARDXREF/TRANTYPE/TRANCATG, WRITE REPTFILE | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y |
| 28 | CBEXPORT.cbl | 582 | Data export — reads all 5 VSAM master files (customer, account, xref, transaction, card) and writes consolidated export file for branch migration | Batch | READ all 5 input files, WRITE EXPORT-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 29 | CBIMPORT.cbl | 487 | Data import — reads export file, validates records, and writes to individual output files with error reporting | Batch | READ EXPFILE, WRITE CUSTOUT/ACCTOUT/XREFOUT/TRNXOUT/ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 30 | COBSWAIT.cbl | 41 | Wait utility — calls assembler MVSWAIT to pause batch job execution for a specified duration | Batch (utility) | CALL 'MVSWAIT' | _(none)_ |
| 31 | CSUTLDTC.cbl | 157 | Date conversion utility — converts date formats using LE service CEEDAYS, callable from online and batch programs | Batch (utility) | CALL 'CEEDAYS' | _(none)_ |

---

## 3. Sub-Application Programs

### 3.1 Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 32 | COPAUA0C.cbl | 1,026 | Authorization decision engine — processes auth requests via MQ, validates against VSAM files via CICS, logs to IMS DB, responds via MQ. Spans CICS + IMS + MQ | Online (CICS/IMS/MQ) | MQOPEN, MQGET, MQPUT1, CICS READ (CARDXREF/ACCTFILE/CUSTFILE), EXEC DLI GU/SCHD/TERM/ISRT/REPL | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | COPAUS0C.cbl | 1,032 | Pending authorization summary — browse IMS auth summary segments with pagination, display on BMS screen | Online (CICS/IMS) | EXEC DLI GU/GNP/SCHD/TERM, CICS READ (CARDXREF/ACCTFILE/CUSTFILE), CICS SEND/RECEIVE MAP | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | COPAUS1C.cbl | 604 | Pending authorization detail — display/update individual authorization detail with IMS update (REPL) | Online (CICS/IMS) | EXEC DLI GU/GNP/REPL, CICS LINK, CICS SEND/RECEIVE MAP | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS2C.cbl | 244 | Fraud flag insert — marks authorization as fraudulent by inserting into DB2 AUTHFRDS table | Online (CICS/DB2) | EXEC SQL INSERT (AUTHFRDS), CICS RETURN | CIPAUDTY |
| 36 | CBPAUP0C.cbl | 386 | Expired auth purge — batch IMS program to delete expired pending authorizations using GN/GNP/DLET with checkpoint | Batch (IMS) | EXEC DLI GN/GNP/DLET/CHKP | CIPAUSMY, CIPAUDTY |
| 37 | PAUDBLOD.CBL | 369 | IMS database load — loads pending authorization data into IMS database segments | Batch (IMS) | EXEC DLI ISRT/GU, READ input files | CIPAUSMY, CIPAUDTY, IMSFUNCS, PAUTBPCB |
| 38 | PAUDBUNL.CBL | 317 | IMS database unload — extracts all pending authorization records from IMS to sequential files | Batch (IMS) | EXEC DLI GN/GNP, WRITE output files | CIPAUSMY, CIPAUDTY, IMSFUNCS, PAUTBPCB |
| 39 | DBUNLDGS.CBL | 366 | GSAM unload utility — unloads IMS data via GSAM (Generalized Sequential Access Method) for backup/migration | Batch (IMS/GSAM) | EXEC DLI GN/GNP/ISRT (GSAM) | CIPAUSMY, CIPAUDTY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB |

### 3.2 Transaction Type (DB2) — `app/app-transaction-type-db2/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 40 | COTRTLIC.cbl | 2,098 | Transaction type list — cursor-based pagination of DB2 TRNTYPE/TRNTYCAT tables with CICS BMS screen | Online (CICS/DB2) | EXEC SQL OPEN/FETCH/CLOSE CURSOR, CICS SEND/RECEIVE MAP | COCOM01Y, COTRTLI, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 41 | COTRTUPC.cbl | 1,702 | Transaction type update — CRUD operations on DB2 transaction type/category tables with cascading deletes | Online (CICS/DB2) | EXEC SQL SELECT/INSERT/UPDATE/DELETE (TRNTYPE, TRNTYCAT), CICS SEND/RECEIVE MAP | COCOM01Y, COTRTUP, COTTL01Y, CSDAT01Y, CSSTRPFY, CSSETATY, CSUTLDWY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 42 | COBTUPDT.cbl | 237 | Batch DB2 update — batch maintenance of transaction type records in DB2 | Batch (DB2) | EXEC SQL SELECT/UPDATE/INSERT/DELETE (TRNTYPE) | _(inline SQL — no copybooks)_ |

### 3.3 VSAM/MQ — `app/app-vsam-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | 620 | Account inquiry via MQ — receives MQ request, reads VSAM account data, sends MQ reply | Online (CICS/MQ) | MQOPEN, MQGET, MQPUT1, CICS READ (ACCTFILE) | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CVACT01Y |
| 44 | CODATE01.cbl | 524 | Date inquiry via MQ — receives MQ request for system date, returns formatted date via MQ reply | Online (CICS/MQ) | MQOPEN, MQGET, MQPUT1 | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV |

---

## 4. JCL Job Catalog — `app/jcl/` (38 jobs)

### 4.1 Data Setup / VSAM Cluster Definition Jobs

| Job | Lines | Purpose | Step Sequence |
|-----|-------|---------|---------------|
| ACCTFILE.jcl | 65 | Delete/define/load account data VSAM KSDS | STEP05 (IDCAMS DELETE) → STEP10 (IDCAMS DEFINE CLUSTER) → STEP15 (IDCAMS REPRO load from PS) |
| CARDFILE.jcl | 128 | Delete/define/load card data VSAM KSDS with alternate index | CLCIFIL (SDSF close) → STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) → STEP40 (DEFINE AIX) → STEP50 (DEFINE PATH) → STEP60 (BLDINDEX) |
| CUSTFILE.jcl | 84 | Define/load customer data VSAM KSDS | CLCIFIL (SDSF close) → STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) → OPCIFIL (SDSF open) |
| XREFFILE.jcl | 106 | Delete/define/load card-account cross-reference VSAM KSDS with 2 AIX | STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) → STEP20–30 (DEFINE AIX + PATH + BLDINDEX for AIX1) |
| TRANFILE.jcl | 125 | Define/load transaction master VSAM KSDS with AIX | CLCIFIL (SDSF close) → STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) → STEP20–30 (AIX definition) |
| DISCGRP.jcl | 65 | Define/load disclosure group reference VSAM KSDS | STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) |
| TCATBALF.jcl | 65 | Define/load transaction category balance VSAM KSDS | STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) |
| TRANTYPE.jcl | 65 | Define/load transaction type reference VSAM KSDS | STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) |
| TRANCATG.jcl | 65 | Define/load transaction category reference VSAM KSDS | STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) |
| DEFCUST.jcl | 47 | Define customer data file (alternate definition) | STEP05 (IDCAMS DELETE) → STEP05 (IDCAMS DEFINE) |
| DUSRSECJ.jcl | 65 | Define/load user security VSAM KSDS | STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) |
| REPTFILE.jcl | 65 | Define/load report output VSAM file | STEP05 (DELETE) → STEP10 (DEFINE CLUSTER) → STEP15 (REPRO load) |
| TRANIDX.jcl | 58 | Define alternate index on transaction master | STEP20 (DEFINE AIX) → STEP25 (DEFINE PATH) → STEP30 (BLDINDEX) |
| ESDSRRDS.jcl | 65 | Define ESDS and RRDS VSAM clusters (advanced dataset types demo) | STEP05–10 (DEFINE CLUSTER ESDS + RRDS) |

### 4.2 Batch Processing Jobs

| Job | Lines | Purpose | Step Sequence |
|-----|-------|---------|---------------|
| POSTTRAN.jcl | 85 | Post daily transactions to master file | STEP05R (SORT daily transactions) → STEP10 (PGM=CBTRN02C — post transactions, update accounts) |
| INTCALC.jcl | 55 | Calculate interest on account balances | STEP10 (PGM=CBACT04C — read TCATBAL/XREF/DISCGRP/ACCOUNT, calculate, rewrite) |
| CREASTMT.JCL | 97 | Generate customer account statements | DELDEF01 (IDCAMS — define work files) → STEP010 (SORT transactions) → STEP020 (IDCAMS REPRO to VSAM) → STEP030 (IEFBR14 — delete old output) → STEP040 (PGM=CBSTM03A — generate statements) |
| TRANREPT.jcl | 84 | Generate daily transaction report | STEP05R (SORT transactions) → STEP10R (PGM=CBTRN03C — generate report) |
| CBEXPORT.jcl | 72 | Export customer data for migration | STEP01 (IDCAMS — define export file) → STEP02 (PGM=CBEXPORT — read 5 files, write export) |
| CBIMPORT.jcl | 68 | Import data from export file | STEP01 (PGM=CBIMPORT — read export, validate, write output files) |
| READACCT.jcl | 21 | Read and display account file | STEP10 (PGM=CBACT01C) |
| READCARD.jcl | 21 | Read and display card file | STEP10 (PGM=CBACT02C) |
| READCUST.jcl | 21 | Read and display customer file | STEP10 (PGM=CBCUS01C) |
| READXREF.jcl | 21 | Read and display cross-reference file | STEP10 (PGM=CBACT03C) |
| COMBTRAN.jcl | 52 | Combine and sort transactions, load to VSAM | STEP05R (SORT) → STEP10 (IDCAMS REPRO to VSAM) |
| TRANBKP.jcl | 55 | Backup transaction data using GDG | STEP05 (SORT) → STEP10 (IDCAMS REPRO) |
| PRTCATBL.jcl | 55 | Print category balance report | STEP10 (PGM=CBTRN01C) |
| WAITSTEP.jcl | 27 | Execute wait utility for job scheduling | WAIT (PGM=COBSWAIT) |
| DALYREJS.jcl | 32 | Define GDG base for daily rejects | STEP05 (IDCAMS DEFINE GDG) |

### 4.3 CICS Administration Jobs

| Job | Lines | Purpose | Step Sequence |
|-----|-------|---------|---------------|
| CBADMCDJ.jcl | 167 | CICS CSD (resource definition) batch update — defines all CardDemo CICS resources (programs, transactions, files, mapsets) | STEP1 (PGM=DFHCSDUP — CSD batch utility) |
| CLOSEFIL.jcl | 34 | Close VSAM files in CICS via SDSF console commands | CLCIFIL (PGM=SDSF) |
| OPENFIL.jcl | 34 | Open VSAM files in CICS via SDSF console commands | OPCIFIL (PGM=SDSF) |

### 4.4 GDG and Backup Jobs

| Job | Lines | Purpose | Step Sequence |
|-----|-------|---------|---------------|
| DEFGDGB.jcl | 63 | Define GDG base datasets for versioned backups | STEP05 (IDCAMS DEFINE GDG) |
| DEFGDGD.jcl | 94 | Define DB2 GDG + backup transaction type/category data | STEP10 (DEFINE GDG) → STEP20 (IEBGENER copy TRANTYPE) → STEP30 (DEFINE GDG) → STEP40 (IEBGENER copy TRANCATG) → STEP50 (DEFINE GDG) |

### 4.5 Utility Jobs

| Job | Lines | Purpose | Step Sequence |
|-----|-------|---------|---------------|
| FTPJCL.JCL | 42 | FTP file transfer utility | STEP1 (PGM=FTP — transfer files) |
| TXT2PDF1.JCL | 41 | Convert text statement to PDF | TXT2PDF (PGM=IKJEFT1B — REXX TXT2PDF exec) |
| INTRDRJ1.JCL | 19 | Internal reader — submit job via SYSUT2=INTRDR | IDCAMS (REPRO backup) → STEP01 (IEBGENER — submit INTRDRJ2 via internal reader) |
| INTRDRJ2.JCL | 14 | Internal reader target — secondary job submitted by INTRDRJ1 | IDCAMS (REPRO copy) |

---

## 5. JCL Catalog — Sub-Application Jobs

### 5.1 Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/jcl/` (5 jobs)

| Job | Lines | Purpose | Step Sequence |
|-----|-------|---------|---------------|
| CBPAUP0J.jcl | 46 | Run expired authorization purge (IMS BMP) | STEP01 (PGM=DFSRRC00 — IMS BMP region, runs CBPAUP0C) |
| DBPAUTP0.jcl | 47 | Unload IMS auth database to sequential file | STEPDEL (IEFBR14 — delete old output) → UNLOAD (PGM=DFSRRC00 — IMS unload utility) |
| LOADPADB.JCL | 51 | Load pending authorization IMS database from files | STEP01 (PGM=DFSRRC00 — IMS load, reads INFILE1/INFILE2) |
| UNLDPADB.JCL | 69 | Unload IMS auth database to root/child sequential files | STEP0 (IEFBR14 — allocate output) → STEP01 (PGM=DFSRRC00 — IMS unload) |
| UNLDGSAM.JCL | 53 | Unload IMS auth via GSAM to sequential files | STEP01 (PGM=DFSRRC00 — GSAM unload, writes ROOT.GSAM/CHILD.GSAM) |

### 5.2 Transaction Type (DB2) — `app/app-transaction-type-db2/jcl/` (3 jobs)

| Job | Lines | Purpose | Step Sequence |
|-----|-------|---------|---------------|
| CREADB21.jcl | 84 | Create DB2 tables and bind plans | FREEPLN (IKJEFT01 — free old plans) → CRCRDDB (IKJEFT01 — create tables via DDL) → PREPBPLN (IKJEFT01 — prep/bind DBRM) |
| MNTTRDB2.jcl | 51 | Maintain transaction type DB2 records (batch CRUD) | STEP01 (PGM=COBTUPDT — batch update) |
| TRANEXTR.jcl | 38 | Extract transaction types from DB2 to sequential file | STEP01 (PGM=DSNTEP41 — DB2 SQL extract) |

---

## 6. Additional Assets

### 6.1 BMS Screen Maps — `app/bms/` (17 maps) + sub-app (2 maps)

| Map | Associated Program | Screen Purpose |
|-----|--------------------|----------------|
| COSGN00.bms | COSGN00C | Sign-on screen |
| COADM01.bms | COADM01C | Admin menu |
| COMEN01.bms | COMEN01C | Main menu |
| COACTUP.bms | COACTUPC | Account update form |
| COACTVW.bms | COACTVWC | Account view display |
| COCRDLI.bms | COCRDLIC | Card list browse |
| COCRDSL.bms | COCRDSLC | Card detail view |
| COCRDUP.bms | COCRDUPC | Card update form |
| COTRN00.bms | COTRN00C | Transaction list |
| COTRN01.bms | COTRN01C | Transaction detail |
| COTRN02.bms | COTRN02C | Transaction add form |
| CORPT00.bms | CORPT00C | Report request |
| COBIL00.bms | COBIL00C | Bill payment |
| COUSR00.bms | COUSR00C | User list |
| COUSR01.bms | COUSR01C | User add |
| COUSR02.bms | COUSR02C | User update |
| COUSR03.bms | COUSR03C | User delete |
| COPAU00.bms | COPAUS0C | Auth summary (IMS sub-app) |
| COPAU01.bms | COPAUS1C | Auth detail (IMS sub-app) |

### 6.2 Data Files — `app/data/`

| File (ASCII) | EBCDIC Equivalent | Entity | Record Length |
|--------------|-------------------|--------|---------------|
| acctdata.txt | AWS.M2.CARDDEMO.ACCTDATA.PS | Account | 300 |
| carddata.txt | AWS.M2.CARDDEMO.CARDDATA.PS | Card | 150 |
| custdata.txt | AWS.M2.CARDDEMO.CUSTDATA.PS | Customer | 500 |
| cardxref.txt | AWS.M2.CARDDEMO.CARDXREF.PS | Card-Account XREF | 50 |
| dailytran.txt | AWS.M2.CARDDEMO.DALYTRAN.PS | Daily Transaction | 350 |
| discgrp.txt | AWS.M2.CARDDEMO.DISCGRP.PS | Disclosure Group | — |
| tcatbal.txt | AWS.M2.CARDDEMO.TCATBALF.PS | Transaction Category Balance | — |
| trantype.txt | AWS.M2.CARDDEMO.TRANTYPE.PS | Transaction Type | — |
| trancatg.txt | AWS.M2.CARDDEMO.TRANCATG.PS | Transaction Category | — |

### 6.3 Assembler Programs — `app/asm/`

| Program | Purpose | Called By |
|---------|---------|----------|
| COBDATFT | Date format conversion utility | CBACT01C |
| MVSWAIT | Timer/wait control for batch jobs | COBSWAIT |

### 6.4 CSD Definitions — `app/csd/`, sub-app `csd/`

| File | Purpose |
|------|---------|
| CRDDEMO.csd | Main CardDemo CICS resource definitions (programs, transactions, files, mapsets) |
| CRDDEMO2.csd | IMS/DB2/MQ sub-app CICS resource definitions |
| CRDDEMOD.csd | DB2 sub-app CICS resource definitions |
