# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System (Mainframe)
> **Estate Total:** 44 COBOL programs | 47 copybooks | 46 JCL jobs | 16 BMS maps
> **Total Lines of COBOL:** ~27,350

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|-----------------------|
| 1 | CBACT01C.cbl | 430 | Read account VSAM file, write to PS/array/VB formats | Batch | READ ACCTFILE; WRITE OUT-FILE, ARRY-FILE, VBRC-FILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Read and display card VSAM file records | Batch | READ CARDFILE | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Read and display card cross-reference file | Batch | READ XREFFILE | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Calculate interest on account balances using disclosure group rates | Batch | READ TCATBAL, XREF, DISCGRP, ACCOUNT; REWRITE ACCOUNT; WRITE TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Read and display customer VSAM file records | Batch | READ CUSTFILE | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Export all entity data (customer, account, card, transaction) to sequential file for branch migration | Batch | READ CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; WRITE EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Import and validate data from export file into separate entity output files | Batch | READ EXPFILE; WRITE CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT | CVEXPORT |
| 8 | CBSTM03A.CBL | 924 | Generate customer statements in text and HTML formats | Batch | READ via CBSTM03B (XREF, CUST, ACCT, TRNX); WRITE STMT-FILE, HTML-FILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | 230 | I/O submodule for CBSTM03A — handles file open/read/close/write operations | Batch (module) | OPEN/READ/CLOSE TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE | — (receives data via LINKAGE) |
| 10 | CBTRN01C.cbl | 494 | Read and validate daily transaction file; display/print records | Batch | READ DALYTRAN | CVTRA06Y |
| 11 | CBTRN02C.cbl | 731 | Post daily transactions: validate against XREF/ACCT, write to master TRANSACT file, reject invalid | Batch | READ DALYTRAN, XREFFILE, ACCTFILE, TCATBALF; WRITE TRANSACT, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | 649 | Generate daily transaction report with type/category lookups and account totals | Batch | READ TRANSACT, XREF, TRANTYPE, TRANCATG, DATEPARM; WRITE REPTFILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COACTUPC.cbl | 4,236 | Account update screen — exhaustive field-level validation (date, SSN, phone, state, ZIP), multi-file update | Online (CICS) | CICS READ ACCTFILE, CARDXREF, CUSTFILE; REWRITE ACCTFILE | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×25), CSSTRPFY |
| 14 | COACTVWC.cbl | 941 | Account view screen — read-only display of account, card, and customer details | Online (CICS) | CICS READ ACCTFILE, CARDXREF, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 15 | COADM01C.cbl | 288 | Admin menu — routes to user management and DB2 transaction type maintenance | Online (CICS) | — (menu navigation only) | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | 572 | Bill payment screen — process payment, update account balance, write transaction | Online (CICS) | CICS READ ACCTFILE, XREFFILE; REWRITE ACCTFILE; STARTBR/READPREV/ENDBR TRANSACT; WRITE TRANSACT | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | 41 | Utility — invoke assembler wait routine for batch step delays | Batch (utility) | CALL MVSWAIT | — |
| 18 | COCRDLIC.cbl | 1,459 | Credit card list screen — paginated VSAM browse with STARTBR/READNEXT/READPREV | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR CARDFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 19 | COCRDSLC.cbl | 887 | Credit card detail view — display card, customer, and account info | Online (CICS) | CICS READ CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 20 | COCRDUPC.cbl | 1,560 | Credit card update — update card details with field validation | Online (CICS) | CICS READ CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 21 | COMEN01C.cbl | 308 | Main menu hub — routes to all 11 user-facing functions via XCTL | Online (CICS) | — (menu navigation only) | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 22 | CORPT00C.cbl | 649 | Report request screen — accept date range parameters, submit batch JCL via internal reader | Online (CICS) | CICS WRITEQ TD; CALL CSUTLDTC for date validation | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | 260 | Sign-on screen — authenticate user against USRSEC VSAM file, route to admin or main menu | Online (CICS) | CICS READ USRSEC | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | COTRN00C.cbl | 699 | Transaction list screen — paginated browse of TRANSACT VSAM file | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN01C.cbl | 330 | Transaction detail view — display single transaction record | Online (CICS) | CICS READ TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | 783 | Transaction add screen — add new transaction with date validation, account/XREF lookup | Online (CICS) | CICS READ ACCTFILE, XREFFILE; STARTBR/READPREV/ENDBR TRANSACT; WRITE TRANSACT | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | 695 | User list screen — paginated browse of security user records | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | 299 | User add screen — create new user in security file | Online (CICS) | CICS WRITE USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR02C.cbl | 414 | User update screen — modify existing user record | Online (CICS) | CICS READ/REWRITE USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | 359 | User delete screen — remove user from security file | Online (CICS) | CICS READ/DELETE USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | 157 | Date validation utility — calls LE CEEDAYS for Lilian day conversion | Batch (utility) | CALL CEEDAYS | CSUTLDWY |

---

## 2. Sub-Application Programs

### 2a. Authorization Module (`app/app-authorization-ims-db2-mq/cbl/`) — IMS/DB2/MQ

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|-----------------------|
| 32 | CBPAUP0C.cbl | 386 | Purge expired pending authorizations from IMS database | Batch (IMS) | DL/I GN, GNP, DLET via CBLTDLI | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |
| 33 | COPAUA0C.cbl | 1,026 | Authorization decision engine — reads MQ request, looks up IMS/VSAM/DB2, writes MQ response | Online (CICS + IMS + MQ + DB2) | MQOPEN, MQGET, MQPUT1, MQCLOSE; CICS READ ACCTFILE, CARDXREF, CUSTFILE; EXEC SQL; CICS WRITEQ | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, IMSFUNCS, COPAU00, COPAU01 |
| 34 | COPAUS0C.cbl | 1,032 | Pending authorization summary browse — display IMS data on CICS screen | Online (CICS + IMS) | CICS READ ACCTFILE, CARDXREF, CUSTFILE | CIPAUSMY, COPAU00, COPAU01 |
| 35 | COPAUS1C.cbl | 604 | Pending authorization detail with update capability | Online (CICS + IMS) | DL/I GU, GNP, REPL via CICS LINK; CICS READ | CIPAUDTY, CIPAUSMY, COPAU01 |
| 36 | COPAUS2C.cbl | 244 | Mark authorization as fraud — insert row into DB2 AUTHFRDS table | Online (CICS + DB2) | EXEC SQL INSERT INTO AUTHFRDS | CIPAUDTY |
| 37 | DBUNLDGS.CBL | 366 | Unload IMS database to GSAM sequential file | Batch (IMS) | DL/I GN, GNP, ISRT (to GSAM) via CBLTDLI | CIPAUDTY, CIPAUSMY, IMSFUNCS |
| 38 | PAUDBLOD.CBL | 369 | Load pending authorization data into IMS database | Batch (IMS) | DL/I ISRT, GU via CBLTDLI | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |
| 39 | PAUDBUNL.CBL | 317 | Unload IMS database (pending authorizations) to sequential file | Batch (IMS) | DL/I GN, GNP via CBLTDLI | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |

### 2b. Transaction Type Module (`app/app-transaction-type-db2/cbl/`) — DB2

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|-----------------------|
| 40 | COBTUPDT.cbl | 237 | Batch DB2 update utility for transaction type records | Batch (DB2) | EXEC SQL SELECT, UPDATE, INSERT, DELETE on TRNTYPE/TRNCATG | CSDB2RPY, CSDB2RWY, DCLTRTYP, DCLTRCAT |
| 41 | COTRTLIC.cbl | 2,098 | Transaction type list screen — cursor-based DB2 pagination | Online (CICS + DB2) | EXEC SQL DECLARE/OPEN/FETCH/CLOSE CURSOR on TRNTYPE, TRNCATG | CSDB2RPY, CSDB2RWY, COTRTLI, DCLTRTYP, DCLTRCAT |
| 42 | COTRTUPC.cbl | 1,702 | Transaction type update/add/delete — cascading operations on DB2 tables | Online (CICS + DB2) | EXEC SQL SELECT, INSERT, UPDATE, DELETE on TRNTYPE, TRNCATG | CSDB2RPY, CSDB2RWY, COTRTUP, DCLTRTYP, DCLTRCAT |

### 2c. VSAM/MQ Module (`app/app-vsam-mq/cbl/`)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|-----------------------|
| 43 | COACCT01.cbl | 620 | Account inquiry via MQ — receives request on MQ queue, reads VSAM, returns response | Online (CICS + MQ) | MQOPEN, MQGET, MQPUT, MQCLOSE; CICS READ ACCTFILE | — (inline definitions) |
| 44 | CODATE01.cbl | 524 | Date inquiry service via MQ — processes date format conversion requests | Online (CICS + MQ) | MQOPEN, MQGET, MQPUT, MQCLOSE | — (inline definitions) |

---

## 3. BMS Screen Maps (`app/bms/`)

| Map Name | File | Associated Program | Screen Purpose |
|----------|------|--------------------|----------------|
| COACTUP | COACTUP.bms | COACTUPC | Account Update form |
| COACTVW | COACTVW.bms | COACTVWC | Account View display |
| COADM01 | COADM01.bms | COADM01C | Admin Menu |
| COBIL00 | COBIL00.bms | COBIL00C | Bill Payment form |
| COCRDLI | COCRDLI.bms | COCRDLIC | Credit Card List |
| COCRDSL | COCRDSL.bms | COCRDSLC | Credit Card Detail View |
| COCRDUP | COCRDUP.bms | COCRDUPC | Credit Card Update form |
| COMEN01 | COMEN01.bms | COMEN01C | Main Menu |
| CORPT00 | CORPT00.bms | CORPT00C | Report Request form |
| COSGN00 | COSGN00.bms | COSGN00C | Sign-On screen |
| COTRN00 | COTRN00.bms | COTRN00C | Transaction List |
| COTRN01 | COTRN01.bms | COTRN01C | Transaction Detail View |
| COTRN02 | COTRN02.bms | COTRN02C | Transaction Add form |
| COUSR00 | COUSR00.bms | COUSR00C | User List |
| COUSR01 | COUSR01.bms | COUSR01C | User Add form |
| COUSR02 | COUSR02.bms | COUSR02C | User Update form |
| COUSR03 | COUSR03.bms | COUSR03C | User Delete confirmation |

Sub-application BMS maps: `COPAU00.bms`, `COPAU01.bms` (authorization), `COTRTLI.bms`, `COTRTUP.bms` (transaction type DB2).

---

## 4. JCL Job Catalog (`app/jcl/`)

### 4a. Data Setup Jobs — VSAM Cluster Definition (via IDCAMS)

| Job | Steps | Purpose | Datasets Managed |
|-----|-------|---------|------------------|
| ACCTFILE.jcl | STEP05→STEP10→STEP15 | Delete/define/load Account KSDS cluster | AWS.M2.CARDDEMO.ACCTDATA.PS → ACCTVSAM |
| CARDFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→STEP40→STEP50 | Close CICS files, delete/define/load Card KSDS + AIX | CARDDATA → CARDVSAM |
| CUSTFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→OPCIFIL | Close files, delete/define/load Customer KSDS, reopen | CUSTDATA → CUSTVSAM |
| XREFFILE.jcl | STEP05→STEP10→STEP15→STEP20→STEP25→STEP30 | Delete/define/load Card-XREF KSDS + AIX | XREFDATA → XREFVSAM |
| TRANFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→STEP20→STEP25 | Close, delete/define/load Transaction KSDS + AIX | TRANSACT → TRANVSAM |
| DISCGRP.jcl | STEP05→STEP10→STEP15 | Delete/define/load Disclosure Group KSDS | DISCGRP → DISCVSAM |
| TCATBALF.jcl | STEP05→STEP10→STEP15 | Delete/define/load Transaction Category Balance KSDS | TCATBAL → TCATBALV |
| TRANCATG.jcl | STEP05→STEP10→STEP15 | Delete/define/load Transaction Category KSDS | TRANCATG → TCATVSAM |
| TRANTYPE.jcl | STEP05→STEP10→STEP15 | Delete/define/load Transaction Type KSDS | TRANTYPE → TTYPVSAM |
| REPTFILE.jcl | STEP05 | Delete/define Report output file | Report ESDS |
| DALYREJS.jcl | STEP05 | Delete/define Daily Rejects file | DALYREJS ESDS |
| DEFCUST.jcl | STEP05 | Define additional Customer dataset | CUSTDATA alternate |
| DEFGDGB.jcl | STEP05 | Define GDG base for backups | GDG bases for TRANTYPE, TRANCATG, DISCGRP |
| DEFGDGD.jcl | STEP10→STEP20→STEP30→STEP40→STEP50→STEP60 | Backup current reference data to GDG generations | TRANTYPE, TRANCATG, DISCGRP → GDG(+1) |
| DUSRSECJ.jcl | PREDEL→STEP01→STEP02→STEP03 | Create and load User Security VSAM KSDS | USRSEC.PS → USRSEC.VSAM.KSDS |
| ESDSRRDS.jcl | PREDEL→STEP01→STEP02→STEP03→STEP04→STEP05 | Create ESDS/RRDS variants of user security file | ESDSRRDS.PS → USRSEC.VSAM.ESDS + RRDS |
| TRANIDX.jcl | STEP20→STEP25→STEP30 | Define alternate indexes for transaction file | AIX path definitions |

### 4b. Batch Processing Jobs

| Job | Steps | Program Executed | Purpose | Key Datasets |
|-----|-------|-----------------|---------|--------------|
| POSTTRAN.jcl | STEP15 | CBTRN02C | Post daily transactions to master file | IN: DALYTRAN, XREFFILE, ACCTFILE, TCATBALF; OUT: TRANSACT, DALYREJS |
| INTCALC.jcl | STEP15 | CBACT04C | Calculate interest on account balances | IN: TCATBALF, XREFFILE, ACCTFILE, DISCGRP; OUT: TRANSACT |
| CREASTMT.JCL | DELDEF01→STEP010→STEP020→STEP030→STEP040 | SORT → IDCAMS → IEFBR14 → CBSTM03A | Sort transactions, generate customer statements (text + HTML) | IN: TRANSACT; OUT: STMTFILE, HTMLFILE |
| TRANREPT.jcl | STEP05R→STEP10R | SORT → CBTRN03C | Sort and generate daily transaction report | IN: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; OUT: TRANREPT |
| READACCT.jcl | PREDEL→STEP05 | CBACT01C | Read account file, produce PS/array/VB exports | IN: ACCTFILE; OUT: PSCOMP, ARRYPS, VBPS |
| READCARD.jcl | STEP05 | CBACT02C | Read and display card file | IN: CARDFILE |
| READCUST.jcl | STEP05 | CBCUS01C | Read and display customer file | IN: CUSTFILE |
| READXREF.jcl | STEP05 | CBACT03C | Read and display cross-reference file | IN: XREFFILE |
| CBEXPORT.jcl | STEP01→STEP02 | IDCAMS → CBEXPORT | Define output, export all entity data | IN: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; OUT: EXPFILE |
| CBIMPORT.jcl | STEP01 | CBIMPORT | Import and validate export data | IN: EXPFILE; OUT: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT |
| COMBTRAN.jcl | STEP05R→STEP10 | SORT → IDCAMS | Sort and combine transactions into VSAM | IN: source file; OUT: TRANVSAM |
| PRTCATBL.jcl | DELDEF→STEP05R→STEP10R | IEFBR14 → REPROC → SORT | Print transaction category balance report | IN: TCATBALF; OUT: sorted report |
| TRANBKP.jcl | STEP05R→STEP05→STEP10 | REPROC → IDCAMS | Backup transaction VSAM data | Transaction backup/export |
| WAITSTEP.jcl | WAIT | COBSWAIT | Execute timed wait for batch step sequencing | — |

### 4c. Utility / Infrastructure Jobs

| Job | Purpose |
|-----|---------|
| CBADMCDJ.jcl | CICS CSD (system definition) update — install program/transaction definitions via DFHCSDUP |
| CLOSEFIL.jcl | Close CICS-managed files via SDSF operator commands |
| OPENFIL.jcl | Open CICS-managed files via SDSF operator commands |
| FTPJCL.JCL | FTP transfer job for remote data exchange |
| INTRDRJ1.JCL | Internal reader chain step 1 — backup data + submit INTRDRJ2 |
| INTRDRJ2.JCL | Internal reader chain step 2 — secondary backup operation |
| TXT2PDF1.JCL | Convert text statement file to PDF format via TXT2PDF REXX utility |

### 4d. Sub-Application JCL

#### Authorization IMS Jobs (`app/app-authorization-ims-db2-mq/jcl/`)

| Job | Steps | Purpose |
|-----|-------|---------|
| CBPAUP0J.jcl | STEP01 (DFSRRC00→CBPAUP0C) | Purge expired pending authorizations from IMS DB |
| DBPAUTP0.jcl | STEPDEL→UNLOAD (DFSRRC00) | Delete old unload file, unload IMS PAUT database |
| LOADPADB.JCL | STEP01 (DFSRRC00→PAUDBLOD) | Load pending authorization data into IMS DB |
| UNLDGSAM.JCL | STEP01 (DFSRRC00→DBUNLDGS) | Unload IMS DB to GSAM sequential file |
| UNLDPADB.JCL | STEP0→STEP01 (DFSRRC00→PAUDBUNL) | Delete old, unload pending auth IMS DB |

#### Transaction Type DB2 Jobs (`app/app-transaction-type-db2/jcl/`)

| Job | Steps | Purpose |
|-----|-------|---------|
| CREADB21.jcl | FREEPLN→CRCRDDB→LDTTYPE→RUNTEP2→LDTCCAT | Free DB2 plan, create DB2 tables (TRNTYPE, TRNCATG), load initial data, bind plans |
| MNTTRDB2.jcl | STEP1 (IKJEFT01) | Maintain transaction type DB2 tables via TSO |
| TRANEXTR.jcl | STEP10→STEP20→STEP30→STEP40→STEP50 | Extract transaction data from VSAM to DB2 via IEBGENER + SQL |

---

## 5. Summary Statistics

| Category | Count |
|----------|-------|
| **COBOL Programs (main)** | 31 |
| **COBOL Programs (sub-apps)** | 13 |
| **Total COBOL Programs** | **44** |
| Online (CICS) | 25 |
| Batch | 16 |
| Batch modules/utilities | 3 |
| **Copybooks (main app/cpy/)** | 27 |
| **Copybooks (sub-app)** | 20 |
| **Total Copybooks** | **47** |
| **JCL Jobs (main app/jcl/)** | 38 |
| **JCL Jobs (sub-apps)** | 8 |
| **Total JCL Jobs** | **46** |
| **BMS Maps (main)** | 17 |
| **BMS Maps (sub-app)** | 4 |
| **Total BMS Maps** | **21** |
