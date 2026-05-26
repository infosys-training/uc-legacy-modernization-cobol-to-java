# Application Inventory — CardDemo COBOL Estate

## 1. COBOL Programs (app/cbl/)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | Read Account VSAM file and write to multiple output formats (sequential, array, variable-length) | Batch | **Read:** ACCTFILE (KSDS) · **Write:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | Read and print Card data file | Batch | **Read:** CARDFILE (KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | Read and print Account Cross-Reference file | Batch | **Read:** XREFFILE (KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | Interest calculator — compute interest charges per transaction category | Batch | **Read:** TCATBALF (KSDS), XREFFILE (KSDS), ACCTFILE (KSDS), DISCGRP (KSDS), TRANSACT (seq) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | Read and print Customer data file | Batch | **Read:** CUSTFILE (KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | Export Customer Data for Branch Migration — reads all CardDemo files and creates multi-record export file | Batch | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · **Write:** EXPFILE (KSDS) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | Import Customer Data from Branch Migration Export — splits export file back into normalized targets with validation | Batch | **Read:** EXPFILE (KSDS) · **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | Print Account Statements from Transaction data in plain text and HTML formats | Batch | **Write:** STMTFILE, HTMLFILE · **Call:** CBSTM03B for file I/O | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | File-processing subroutine called by CBSTM03A — handles open/close/read/write for multiple VSAM files | Batch (Subroutine) | **Read:** TRNXFILE (KSDS), XREFFILE (KSDS), CUSTFILE (KSDS), ACCTFILE (KSDS) | *(none — uses LINKAGE SECTION)* |
| 10 | CBTRN01C.cbl | Post records from daily transaction file — validate and write to master transaction file | Batch | **Read:** DALYTRAN (seq), CUSTFILE, XREFFILE, CARDFILE, ACCTFILE · **Write:** TRANFILE (KSDS) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | Post daily transactions — extended version with reject processing and category balance update | Batch | **Read:** DALYTRAN (seq), XREFFILE, ACCTFILE · **Write:** TRANFILE (KSDS), DALYREJS (seq), TCATBALF (KSDS) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | Print Transaction Detail Report with type/category lookups | Batch | **Read:** TRANFILE (seq), CARDXREF (KSDS), TRANTYPE (KSDS), TRANCATG (KSDS), DATEPARM (seq) · **Write:** TRANREPT (seq) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | Utility program — wait for specified centiseconds (calls MVSWAIT) | Batch (Utility) | **Read:** SYSIN | *(none)* |
| 14 | CSUTLDTC.cbl | Date validation utility — calls CEEDAYS API to validate dates | Batch (Utility) | *(none — called as subroutine)* | *(none — self-contained)* |
| 15 | COADM01C.cbl | Admin Menu for administrator users (CICS screen navigation) | Online (CICS) | **VSAM:** USRSEC (READ) | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | Bill Payment — pay account balance and create payment transaction | Online (CICS) | **VSAM:** TRANSACT (WRITE), ACCTDAT (READ/WRITE), CXACAIX (READ) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 17 | COCRDLIC.cbl | List Credit Cards — paginated list, all cards for admin or filtered by account | Online (CICS) | **VSAM:** CARDFILE (BROWSE), CARDAIX (BROWSE via AIX) | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 18 | COCRDSLC.cbl | Credit Card Detail — display card details for a selected card number | Online (CICS) | **VSAM:** CARDFILE (READ), ACCTDAT (READ), CUSTFILE (READ) | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 19 | COCRDUPC.cbl | Credit Card Update — modify card details (status, expiry, etc.) | Online (CICS) | **VSAM:** CARDFILE (READ/WRITE), ACCTDAT (READ), CUSTFILE (READ) | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 20 | COMEN01C.cbl | Main Menu for regular users (CICS screen navigation) | Online (CICS) | **VSAM:** USRSEC (READ) | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 21 | CORPT00C.cbl | Print Transaction Reports — submit batch job via extra-partition TDQ | Online (CICS) | **VSAM:** TRANSACT (BROWSE) · **TDQ:** INTRDR (WRITE) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 22 | COSGN00C.cbl | Sign-on Screen — authenticate user credentials against USRSEC file | Online (CICS) | **VSAM:** USRSEC (READ) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 23 | COTRN00C.cbl | List Transactions — paginated list from TRANSACT file | Online (CICS) | **VSAM:** TRANSACT (BROWSE) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | COTRN01C.cbl | View Transaction — display detail of a selected transaction | Online (CICS) | **VSAM:** TRANSACT (READ) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN02C.cbl | Add Transaction — create a new transaction record with validation | Online (CICS) | **VSAM:** TRANSACT (WRITE), ACCTDAT (READ/WRITE), CCXREF (READ), CXACAIX (READ) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COUSR00C.cbl | List Users — paginated display of all users from USRSEC file | Online (CICS) | **VSAM:** USRSEC (BROWSE) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 27 | COUSR01C.cbl | Add User — add a new Regular/Admin user to USRSEC file | Online (CICS) | **VSAM:** USRSEC (WRITE) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR02C.cbl | Update User — modify user record in USRSEC file | Online (CICS) | **VSAM:** USRSEC (READ/WRITE) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR03C.cbl | Delete User — remove user from USRSEC file | Online (CICS) | **VSAM:** USRSEC (READ/DELETE) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COACTUPC.cbl | Account Update — full account maintenance with extensive field validation | Online (CICS) | **VSAM:** ACCTDAT (READ/WRITE), CUSTFILE (READ), CARDFILE (READ) | COCOM01Y, COACTUP, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CSUTLDWY, CSUTLDPY, CSSETATY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 31 | COACTVWC.cbl | Account View — read-only display of account details with related cards/customer | Online (CICS) | **VSAM:** ACCTDAT (READ), CUSTFILE (READ), CARDFILE (BROWSE) | COCOM01Y, COACTVW, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 app-authorization-ims-db2-mq/ (Authorization Module)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 1 | CBPAUP0C.cbl | Delete Expired Pending Authorization Messages from IMS DB | Batch (IMS) | **IMS DB:** PAUT segments (DL/I calls) | CIPAUSMY, CIPAUDTY |
| 2 | COPAUA0C.cbl | Card Authorization Decision — process auth requests from MQ, lookup account/customer, respond via MQ | Online (CICS/IMS/MQ) | **MQ:** MQOPEN, MQGET, MQPUT1, MQCLOSE · **VSAM:** XREFFILE, ACCTFILE, CUSTFILE | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 3 | COPAUS0C.cbl | Summary View of Authorization Messages (paginated list) | Online (CICS/IMS/BMS) | **IMS DB:** Browse auth messages | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 4 | COPAUS1C.cbl | Detail View of Authorization Message | Online (CICS/IMS/BMS) | **IMS DB:** Read auth detail | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 5 | COPAUS2C.cbl | Mark Authorization Message as Fraud (DB2 update) | Online (CICS/IMS/DB2) | **DB2:** Update fraud flag | CIPAUDTY |
| 6 | DBUNLDGS.CBL | Unload IMS DB segments to sequential file (GSAM) | Batch (IMS) | **IMS DB:** GU/GN calls · **Write:** GSAM output | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 7 | PAUDBLOD.CBL | Load authorization data into IMS DB from flat file | Batch (IMS) | **Read:** Flat file · **IMS DB:** ISRT/GU calls | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | PAUDBUNL.CBL | Unload authorization data from IMS DB to flat file | Batch (IMS) | **IMS DB:** GU/GN calls · **Write:** Flat file | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

### 2.2 app-transaction-type-db2/ (Transaction Type DB2 Module)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 1 | COBTUPDT.cbl | Update Transaction Type based on user input (DB2) | Online (CICS/DB2) | **DB2:** UPDATE TRAN_TYPE table | CSDB2RPY, CSDB2RWY |
| 2 | COTRTLIC.cbl | List Transaction Types — paginated with DB2 cursors, supports select/delete/update | Online (CICS/DB2) | **DB2:** DECLARE CURSOR, FETCH, DELETE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y |
| 3 | COTRTUPC.cbl | Transaction Type Update — full CRUD operations on transaction type records | Online (CICS/DB2) | **DB2:** SELECT, UPDATE, INSERT | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y |

### 2.3 app-vsam-mq/ (VSAM-MQ Module)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 1 | COACCT01.cbl | Account inquiry via MQ — receives account lookup requests from queue, reads VSAM, responds via MQ | Batch (MQ) | **MQ:** MQOPEN, MQGET, MQPUT1, MQCLOSE · **VSAM:** ACCTFILE (READ) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 2 | CODATE01.cbl | Date service via MQ — receives date validation requests, processes, responds via MQ | Batch (MQ) | **MQ:** MQOPEN, MQGET, MQPUT1, MQCLOSE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. JCL Job Catalog (app/jcl/)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|--------------|
| 1 | ACCTFILE.jcl | Define Account VSAM KSDS file (delete/define/load) | STEP05 (IDCAMS delete) → STEP10 (IDCAMS define) → STEP15 (IDCAMS repro/load) |
| 2 | CARDFILE.jcl | Define Card VSAM KSDS + AIX files | CLCIFIL (SDSF close) → STEP05-STEP60 (IDCAMS delete/define/build AIX) → OPCIFIL (SDSF open) |
| 3 | CBADMCDJ.jcl | Install CICS CSD definitions for CardDemo programs | STEP1 (DFHCSDUP — CSD batch utility) |
| 4 | CBEXPORT.jcl | Run Branch Migration Export batch job | STEP01 (IDCAMS define export file) → STEP02 (PGM=CBEXPORT) |
| 5 | CBIMPORT.jcl | Run Branch Migration Import batch job | STEP01 (PGM=CBIMPORT) |
| 6 | CLOSEFIL.jcl | Close CICS-managed files via SDSF | CLCIFIL (SDSF) |
| 7 | COMBTRAN.jcl | Combine/sort daily transactions into master | STEP05R (SORT) → STEP10 (IDCAMS repro) |
| 8 | CREASTMT.JCL | Create Account Statements (sort + statement program) | DELDEF01 (IDCAMS) → STEP010 (SORT) → STEP020 (IDCAMS) → STEP030 (IEFBR14) → STEP040 (PGM=CBSTM03A) |
| 9 | CUSTFILE.jcl | Define Customer VSAM KSDS file | CLCIFIL (SDSF) → STEP05-STEP15 (IDCAMS) → OPCIFIL (SDSF) |
| 10 | DALYREJS.jcl | Define GDG base for daily rejection files | STEP05 (IDCAMS) |
| 11 | DEFCUST.jcl | Define Customer Data File (alternate) | STEP05 (IDCAMS define) → STEP05 (IDCAMS load) |
| 12 | DEFGDGB.jcl | Define GDG bases for batch output | STEP05 (IDCAMS) |
| 13 | DEFGDGD.jcl | Define DB2-related GDGs and seed datasets | STEP10-STEP60 (IDCAMS define/IEBGENER copy alternating) |
| 14 | DISCGRP.jcl | Define Disclosure Group VSAM KSDS file | STEP05 (IDCAMS delete) → STEP10 (IDCAMS define) → STEP15 (IDCAMS load) |
| 15 | DUSRSECJ.jcl | Define User Security VSAM file + load initial data | PREDEL (IEFBR14) → STEP01 (IEBGENER) → STEP02 (IDCAMS define) → STEP03 (IDCAMS repro) |
| 16 | ESDSRRDS.jcl | Define ESDS and RRDS VSAM datasets (test/demo) | PREDEL (IEFBR14) → STEP01-STEP05 (IEBGENER/IDCAMS) |
| 17 | FTPJCL.JCL | FTP file transfer job | STEP1 (PGM=FTP) |
| 18 | INTCALC.jcl | Run Interest Calculator batch job | STEP15 (PGM=CBACT04C, PARM=date) |
| 19 | INTRDRJ1.JCL | Internal reader — submit JCL via IDCAMS + IEBGENER | IDCAMS → STEP01 (IEBGENER) |
| 20 | INTRDRJ2.JCL | Internal reader — submit JCL via IDCAMS | IDCAMS (IDCAMS) |
| 21 | OPENFIL.jcl | Open CICS-managed files via SDSF | OPCIFIL (SDSF) |
| 22 | POSTTRAN.jcl | Post daily transactions to master file | STEP15 (PGM=CBTRN02C) |
| 23 | PRTCATBL.jcl | Print Transaction Category Balance report | DELDEF (IEFBR14) → STEP05R (REPROC) → STEP10R (SORT) |
| 24 | READACCT.jcl | Read and display Account file | PREDEL (IEFBR14) → STEP05 (PGM=CBACT01C) |
| 25 | READCARD.jcl | Read and display Card file | STEP05 (PGM=CBACT02C) |
| 26 | READCUST.jcl | Read and display Customer file | STEP05 (PGM=CBCUS01C) |
| 27 | READXREF.jcl | Read and display Cross-Reference file | STEP05 (PGM=CBACT03C) |
| 28 | REPTFILE.jcl | Define GDG for report output files | STEP05 (IDCAMS) |
| 29 | TCATBALF.jcl | Define Transaction Category Balance VSAM KSDS | STEP05 (IDCAMS delete) → STEP10 (IDCAMS define) → STEP15 (IDCAMS load) |
| 30 | TRANBKP.jcl | Backup Transaction Master (REPRO + delete/redefine) | STEP05R (REPROC) → STEP05 (IDCAMS repro) → STEP10 (IDCAMS delete/define) |
| 31 | TRANCATG.jcl | Define Transaction Category VSAM KSDS | STEP05 (delete) → STEP10 (define) → STEP15 (load) |
| 32 | TRANFILE.jcl | Define Transaction Master VSAM KSDS + AIX | CLCIFIL (SDSF) → STEP05-STEP30 (IDCAMS) → OPCIFIL (SDSF) |
| 33 | TRANIDX.jcl | Define Alternate Index on Transaction Master | STEP20-STEP30 (IDCAMS define AIX/path/build) |
| 34 | TRANREPT.jcl | Run Transaction Detail Report | STEP05R (REPROC/SORT) → STEP10R (PGM=CBTRN03C) |
| 35 | TRANTYPE.jcl | Define Transaction Type VSAM KSDS | STEP05 (delete) → STEP10 (define) → STEP15 (load) |
| 36 | TXT2PDF1.JCL | Convert text report to PDF | TXT2PDF (PGM=IKJEFT1B — TSO batch) |
| 37 | WAITSTEP.jcl | Execute wait utility for job scheduling | WAIT (PGM=COBSWAIT) |
| 38 | XREFFILE.jcl | Define Cross-Reference VSAM KSDS + AIX | STEP05-STEP30 (IDCAMS delete/define/AIX/path/build) |

### 3.1 Sub-Application JCL (app-authorization-ims-db2-mq/jcl/)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|--------------|
| 1 | CBPAUP0J.jcl | Run pending auth purge in IMS/DC region | STEP01 (DFSRRC00 → CBPAUP0C) |
| 2 | DBPAUTP0.jcl | Unload IMS DB for authorization | STEPDEL (IEFBR14) → UNLOAD (DFSRRC00) |
| 3 | LOADPADB.JCL | Load authorization data into IMS DB | STEP01 (DFSRRC00 → PAUDBLOD) |
| 4 | UNLDGSAM.JCL | Unload IMS DB segments to GSAM | STEP01 (DFSRRC00 → DBUNLDGS) |
| 5 | UNLDPADB.JCL | Unload authorization DB to flat file | STEP0 (IEFBR14) → STEP01 (DFSRRC00 → PAUDBUNL) |

### 3.2 Sub-Application JCL (app-transaction-type-db2/jcl/)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|--------------|
| 1 | CREADB21.jcl | Create DB2 tables and load transaction type data | FREEPLN (IKJEFT01) → CRCRDDB (IKJEFT01) → LDTTYPE (IEFBR14) → RUNTEP2 (IKJEFT01) → LDTCCAT (IKJEFT01) |
| 2 | MNTTRDB2.jcl | Maintain transaction type DB2 tables | STEP1 (IKJEFT01 — TSO/DB2 batch) |
| 3 | TRANEXTR.jcl | Extract transaction type data from DB2 to flat files | STEP10-STEP50 (IEBGENER/IEFBR14/IKJEFT01) |

---

## 4. Summary Statistics

| Metric | Count |
|--------|-------|
| **Total COBOL Programs** | 44 |
| Batch Programs (app/cbl/) | 14 |
| Online/CICS Programs (app/cbl/) | 17 |
| Authorization Module Programs | 8 |
| Transaction Type DB2 Programs | 3 |
| VSAM-MQ Module Programs | 2 |
| **Total JCL Jobs** | 46 |
| Main JCL jobs (app/jcl/) | 38 |
| Authorization JCL | 5 |
| Transaction Type DB2 JCL | 3 |
| **Total Copybooks (app/cpy/)** | 30 |
| **Total Lines of COBOL** | ~28,000 |
