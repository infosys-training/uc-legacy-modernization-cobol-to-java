# Application Inventory — CardDemo COBOL Estate

> Auto-generated analysis of `uc-legacy-modernization-cobol-to-java`

---

## 1. COBOL Programs — Core Application (`app/cbl/`)

### 1.1 Batch Programs

| # | File | Program ID | Purpose | Key I/O Operations | Copybooks Referenced |
|---|------|-----------|---------|---------------------|----------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read account VSAM file and write to flat files (fixed, array, variable-length) | **R:** ACCTFILE (VSAM) **W:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and display card data file | **R:** CARDFILE (VSAM) | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and display account cross-reference data | **R:** XREFFILE (VSAM) | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Interest calculator — compute interest by transaction category and update account balances | **R:** TCATBALF, XREFFILE, DISCGRP **I-O:** ACCTFILE **W:** TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and display customer data file | **R:** CUSTFILE (VSAM) | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export customer profiles (customer, account, xref, transaction, card data) to a single export file for branch migration | **R:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE **W:** EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export file into individual entity files | **R:** EXPFILE **W:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBTRN01C.cbl | CBTRN01C | Validate daily transactions — cross-reference cards, verify accounts, check customer status | **R:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | CBTRN02C.cbl | CBTRN02C | Post daily transactions — validate, reject invalid, update account balances and transaction category balances | **R:** DALYTRAN, XREFFILE **I-O:** ACCTFILE, TCATBALF **W:** TRANFILE, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | CBTRN03C | Print daily transaction detail report with type/category lookups | **R:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM **W:** TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | COBSWAIT.cbl | COBSWAIT | Utility — wait for specified centiseconds (calls ASM MVSWAIT) | None (calls MVSWAIT) | *(none)* |
| 12 | CSUTLDTC.cbl | CSUTLDTC | Date validation utility — validates dates via LE CEEDAYS API | None (calls CEEDAYS) | *(none)* |

### 1.2 Online (CICS) Programs

| # | File | Program ID | Purpose | CICS Resources Accessed | Copybooks Referenced |
|---|------|-----------|---------|------------------------|----------------------|
| 1 | COACTUPC.cbl | COACTUPC | Account update — accept and process account detail changes via CICS screen | **VSAM:** ACCTFILE, CARDFILE, CUSTFILE (via EXEC CICS READ/REWRITE) | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY |
| 2 | COACTVWC.cbl | COACTVWC | Account view — display account details, card, and customer info | **VSAM:** ACCTFILE, CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 3 | COADM01C.cbl | COADM01C | Admin menu — display and route admin function selections | *(menu navigation only)* | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COBIL00C.cbl | COBIL00C | Bill payment — pay account balance in full or specified amount, create payment transaction | **VSAM:** ACCTFILE (READ/REWRITE), TRANSACT (WRITE) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 5 | COCRDLIC.cbl | COCRDLIC | Credit card list — browse and paginate credit card records by account | **VSAM:** CARDFILE (STARTBR/READNEXT/READPREV/ENDBR) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 6 | COCRDSLC.cbl | COCRDSLC | Credit card detail view — display individual card details | **VSAM:** CARDFILE (READ) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 7 | COCRDUPC.cbl | COCRDUPC | Credit card update — modify card details with optimistic locking | **VSAM:** CARDFILE (READ/REWRITE) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | COMEN01C.cbl | COMEN01C | Main menu — display and route regular user function selections | *(menu navigation only)* | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 9 | CORPT00C.cbl | CORPT00C | Report selection — accept report type and date range, submit batch report jobs via TDQ | **TDQ:** JOBS (WRITEQ TD) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COSGN00C.cbl | COSGN00C | Sign-on — authenticate user credentials against USRSEC file and route to menu | **VSAM:** USRSEC (READ) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 11 | COTRN00C.cbl | COTRN00C | Transaction list — browse and paginate transaction records | **VSAM:** TRANSACT (STARTBR/READNEXT/READPREV/ENDBR) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 12 | COTRN01C.cbl | COTRN01C | Transaction view — display individual transaction details | **VSAM:** TRANSACT (READ) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | COTRN02C.cbl | COTRN02C | Transaction add — create new transaction record with date validation | **VSAM:** TRANSACT (READ/WRITE), CARDXREF (STARTBR/READPREV/ENDBR) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | COUSR00C | User list — browse and paginate security user records | **VSAM:** USRSEC (STARTBR/READNEXT/READPREV/ENDBR) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | COUSR01C | User add — create new admin or regular user in USRSEC file | **VSAM:** USRSEC (WRITE) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | COUSR02C | User update — modify existing user details | **VSAM:** USRSEC (READ/REWRITE) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | COUSR03C | User delete — remove user from USRSEC file | **VSAM:** USRSEC (READ/DELETE) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. COBOL Programs — Sub-Applications

### 2.1 Authorization Module (`app/app-authorization-ims-db2-mq/cbl/`)

| # | File | Program ID | Classification | Purpose | Key I/O Operations | Copybooks |
|---|------|-----------|----------------|---------|---------------------|-----------|
| 1 | COPAUA0C.cbl | COPAUA0C | Online (CICS + MQ + IMS) | Card authorization decision — receive MQ request, validate card/account, authorize or decline, update IMS DB, send MQ reply | **MQ:** MQOPEN/MQGET/MQPUT1/MQCLOSE **VSAM:** XREFFILE, ACCTFILE, CUSTFILE (EXEC CICS READ) **IMS:** summary/detail update/insert | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 2 | COPAUS0C.cbl | COPAUS0C | Online (CICS) | Summary view of pending authorization messages | **VSAM:** (EXEC CICS READ ×3) | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 3 | COPAUS1C.cbl | COPAUS1C | Online (CICS) | Detail view of authorization message (uses EXEC CICS LINK) | **CICS LINK** to sub-program | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 4 | COPAUS2C.cbl | COPAUS2C | Online (CICS + DB2) | Mark authorization message as fraud — insert/update CARDDEMO.AUTHFRDS table | **DB2:** INSERT/UPDATE on CARDDEMO.AUTHFRDS | CIPAUDTY |
| 5 | CBPAUP0C.cbl | CBPAUP0C | Batch (IMS) | Delete expired pending authorization messages from IMS database | **IMS:** DL/I GN/GNP/DLET on auth summary and detail segments | CIPAUSMY, CIPAUDTY |
| 6 | PAUDBLOD.CBL | PAUDBLOD | Batch (IMS) | Load IMS authorization database from flat files | **R:** INFILE1, INFILE2 **IMS:** DL/I ISRT/GU | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 7 | PAUDBUNL.CBL | PAUDBUNL | Batch (IMS) | Unload IMS authorization database to flat files | **W:** OPFILE1, OPFILE2 **IMS:** DL/I GN/GNP | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | DBUNLDGS.CBL | DBUNLDGS | Batch (IMS) | Unload IMS auth DB to GSAM files and re-insert (reorganization utility) | **IMS:** DL/I GN/GNP/ISRT via GSAM | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 Transaction Type DB2 Module (`app/app-transaction-type-db2/cbl/`)

| # | File | Program ID | Classification | Purpose | Key I/O Operations | Copybooks |
|---|------|-----------|----------------|---------|---------------------|-----------|
| 1 | COBTUPDT.cbl | COBTUPDT | Batch (DB2) | Batch update transaction types from flat file — insert, update, or delete rows in CARDDEMO.TRANSACTION_TYPE | **R:** INPFILE **DB2:** INSERT/UPDATE/DELETE on CARDDEMO.TRANSACTION_TYPE | DCLTRTYP (SQL INCLUDE) |
| 2 | COTRTLIC.cbl | COTRTLIC | Online (CICS + DB2) | List transaction types for update/delete with cursor-based pagination | **DB2:** SELECT on TRANSACTION_TYPE **CICS:** MAP send/receive | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY (SQL INCLUDE) |
| 3 | COTRTUPC.cbl | COTRTUPC | Online (CICS + DB2) | Accept and process transaction type updates/deletes via CICS screen | **DB2:** SELECT/UPDATE/DELETE on TRANSACTION_TYPE, TRANSACTION_CATEGORY **CICS:** MAP send/receive | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT (SQL INCLUDE) |

### 2.3 VSAM-MQ Module (`app/app-vsam-mq/cbl/`)

| # | File | Program ID | Classification | Purpose | Key I/O Operations | Copybooks |
|---|------|-----------|----------------|---------|---------------------|-----------|
| 1 | COACCT01.cbl | COACCT01 | Online (CICS + MQ) | Account inquiry service — receive MQ request, read VSAM account data, send MQ response | **MQ:** MQOPEN/MQGET/MQPUT/MQCLOSE **VSAM:** ACCTFILE (EXEC CICS READ) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 2 | CODATE01.cbl | CODATE01 | Online (CICS + MQ) | Date service — receive MQ request, get current date/time via CICS ASKTIME/FORMATTIME, send MQ response | **MQ:** MQOPEN/MQGET/MQPUT/MQCLOSE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. JCL Job Catalog (`app/jcl/`)

### 3.1 VSAM File Definition & Loading Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | ACCTFILE.jcl | Define and load Account VSAM KSDS from flat file | STEP05 (IDCAMS delete), STEP10 (IDCAMS define), STEP15 (IDCAMS REPRO load from ACCTDATA) |
| 2 | CARDFILE.jcl | Define and load Card VSAM KSDS with alternate indexes | CLCIFIL (SDSF close files), STEP05–STEP15 (delete/define/REPRO), STEP40–STEP60 (alt index define/build/path), OPCIFIL (SDSF open files) |
| 3 | CUSTFILE.jcl | Define and load Customer VSAM KSDS | CLCIFIL (close), STEP05–STEP15 (delete/define/REPRO from CUSTDATA), OPCIFIL (open) |
| 4 | XREFFILE.jcl | Define and load Card Cross-Reference VSAM KSDS with alt indexes | STEP05–STEP15 (delete/define/REPRO from XREFDATA), STEP20–STEP30 (alt index define/build/path) |
| 5 | TRANFILE.jcl | Define and load Transaction VSAM KSDS with alt indexes | CLCIFIL (close), STEP05–STEP15 (delete/define/REPRO from TRANSACT), STEP20–STEP30 (alt indexes), OPCIFIL (open) |
| 6 | TCATBALF.jcl | Define and load Transaction Category Balance VSAM | STEP05–STEP15 (delete/define/REPRO from TCATBAL) |
| 7 | TRANTYPE.jcl | Define and load Transaction Type VSAM | STEP05–STEP15 (delete/define/REPRO from TRANTYPE) |
| 8 | TRANCATG.jcl | Define and load Transaction Category VSAM | STEP05–STEP15 (delete/define/REPRO from TRANCATG) |
| 9 | DISCGRP.jcl | Define and load Discount Group VSAM | STEP05–STEP15 (delete/define/REPRO from DISCGRP) |
| 10 | DALYREJS.jcl | Define Daily Rejects file (VSAM) | STEP05 (IDCAMS define) |
| 11 | REPTFILE.jcl | Define Report output file (VSAM) | STEP05 (IDCAMS define) |

### 3.2 Batch Processing Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | READACCT.jcl | Read and print account data | PREDEL (IEFBR14 pre-delete output), STEP05 (PGM=CBACT01C — read ACCTFILE, write OUTFILE/ARRYFILE/VBRCFILE) |
| 2 | READCARD.jcl | Read and print card data | STEP05 (PGM=CBACT02C — read CARDFILE) |
| 3 | READCUST.jcl | Read and print customer data | STEP05 (PGM=CBCUS01C — read CUSTFILE) |
| 4 | READXREF.jcl | Read and print cross-reference data | STEP05 (PGM=CBACT03C — read XREFFILE) |
| 5 | POSTTRAN.jcl | Post daily transactions | STEP15 (PGM=CBTRN02C — process DALYTRAN, update ACCTFILE/TCATBALF, write TRANFILE/DALYREJS) |
| 6 | INTCALC.jcl | Calculate interest on accounts | STEP15 (PGM=CBACT04C, PARM='2022071800' — read TCATBALF/XREFFILE/DISCGRP, update ACCTFILE, write TRANSACT) |
| 7 | TRANREPT.jcl | Generate transaction detail report | STEP05R (SORT transactions), STEP10R (PGM=CBTRN03C — read sorted TRANFILE/CARDXREF/TRANTYPE/TRANCATG/DATEPARM, write TRANREPT) |
| 8 | CBEXPORT.jcl | Export data for branch migration | STEP01 (IDCAMS define export file), STEP02 (PGM=CBEXPORT — read all entity files, write EXPFILE) |
| 9 | CBIMPORT.jcl | Import data from branch migration | STEP01 (PGM=CBIMPORT — read EXPFILE, write individual entity files + ERROUT) |
| 10 | WAITSTEP.jcl | Execute wait utility | WAIT (PGM=COBSWAIT — timer delay) |
| 11 | CREASTMT.JCL | Create customer statements | DELDEF01 (IDCAMS), STEP010 (SORT transactions), STEP020 (IDCAMS REPRO), STEP030 (IEFBR14 cleanup), STEP040 (PGM=CBSTM03A — generate statements) |

### 3.3 Utility & Infrastructure Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | CLOSEFIL.jcl | Close CICS files via SDSF | CLCIFIL (PGM=SDSF — issue CICS close file commands) |
| 2 | OPENFIL.jcl | Open CICS files via SDSF | OPCIFIL (PGM=SDSF — issue CICS open file commands) |
| 3 | COMBTRAN.jcl | Combine/sort daily transactions into VSAM | STEP05R (SORT), STEP10 (IDCAMS REPRO into TRANVSAM) |
| 4 | PRTCATBL.jcl | Print transaction category balance report | DELDEF (IEFBR14), STEP05R (REPROC), STEP10R (SORT) |
| 5 | TRANBKP.jcl | Backup transaction file | STEP05R (REPROC), STEP05 (IDCAMS delete old backup), STEP10 (IDCAMS REPRO backup) |
| 6 | TRANIDX.jcl | Rebuild transaction alternate indexes | STEP20–STEP30 (IDCAMS define/build/path alt indexes) |
| 7 | DEFGDGB.jcl | Define GDG bases for reference data | STEP05 (IDCAMS define GDG base) |
| 8 | DEFGDGD.jcl | Backup reference data to GDG datasets | STEP10–STEP60 (alternate IDCAMS define + IEBGENER copy for TRANTYPE, TRANCATG, DISCGRP) |
| 9 | DEFCUST.jcl | Define customer-related VSAM clusters | STEP05 (IDCAMS define) ×2 |
| 10 | DUSRSECJ.jcl | Define and load User Security VSAM | PREDEL (IEFBR14), STEP01 (IEBGENER create PS), STEP02 (IDCAMS define KSDS), STEP03 (IDCAMS REPRO) |
| 11 | ESDSRRDS.jcl | Define and load ESDS/RRDS demo datasets | PREDEL, STEP01 (IEBGENER), STEP02–STEP05 (IDCAMS define/REPRO for ESDS + RRDS) |
| 12 | CBADMCDJ.jcl | Define CICS CSD group entries | STEP1 (PGM=DFHCSDUP — CICS CSD update) |
| 13 | FTPJCL.JCL | FTP file transfer utility | STEP1 (PGM=FTP) |
| 14 | INTRDRJ1.JCL | Internal reader job chaining (step 1) | IDCAMS (REPRO backup), STEP01 (IEBGENER submit INTRDRJ2 via internal reader) |
| 15 | INTRDRJ2.JCL | Internal reader job chaining (step 2) | IDCAMS (REPRO second backup) |
| 16 | TXT2PDF1.JCL | Convert text report to PDF | TXT2PDF (PGM=IKJEFT1B — invoke TXT2PDF REXX exec on statement file) |

### 3.4 Sub-Application JCL

#### Authorization Module (`app/app-authorization-ims-db2-mq/jcl/`)

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | CBPAUP0J.jcl | Run expired authorization purge batch | STEP01 (PGM=DFSRRC00 — IMS BMP executing CBPAUP0C) |
| 2 | DBPAUTP0.jcl | Unload IMS auth database for backup | STEPDEL (IEFBR14 cleanup), UNLOAD (PGM=DFSRRC00 — IMS unload utility) |
| 3 | LOADPADB.JCL | Load IMS auth database from flat files | STEP01 (PGM=DFSRRC00 — IMS BMP executing PAUDBLOD, reads INFILE1/INFILE2) |
| 4 | UNLDPADB.JCL | Unload IMS auth database to flat files | STEP0 (IEFBR14 pre-delete), STEP01 (PGM=DFSRRC00 — IMS BMP executing PAUDBUNL, writes OUTFIL1/OUTFIL2) |
| 5 | UNLDGSAM.JCL | Unload IMS auth DB via GSAM | STEP01 (PGM=DFSRRC00 — IMS BMP executing DBUNLDGS, writes to GSAM files) |

#### Transaction Type DB2 Module (`app/app-transaction-type-db2/jcl/`)

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | CREADB21.jcl | Create DB2 tables for transaction types | FREEPLN (free DB2 plan), CRCRDDB (create tables via DDL), LDTTYPE (pre-condition check), RUNTEP2 (load data) |
| 2 | MNTTRDB2.jcl | Maintain DB2 transaction type data | STEP1 (PGM=IKJEFT01 — run DB2 batch via TSO, BIND/execute with INPFILE) |
| 3 | TRANEXTR.jcl | Extract/backup transaction reference data | STEP10–STEP20 (IEBGENER backup TRANTYPE/TRANCATG to GDG), STEP30 (cleanup), STEP40 (DB2 UNLOAD via IKJEFT01) |

---

## 4. Summary Statistics

| Metric | Count |
|--------|-------|
| **Total COBOL programs** | 44 |
| Batch programs (core) | 12 |
| Online/CICS programs (core) | 17 |
| Sub-app programs (auth-ims-db2-mq) | 8 |
| Sub-app programs (tran-type-db2) | 3 |
| Sub-app programs (vsam-mq) | 2 |
| **Total JCL jobs** | 43 |
| Core JCL jobs | 38 |
| Sub-app JCL jobs (auth) | 5 |
| Sub-app JCL jobs (tran-type-db2) | 3 |
| **Total lines of COBOL** | ~32,000 |
| **Copybooks** | 42 (core) + 14 (sub-app) |
| **BMS Maps** | 18 (core) + 4 (sub-app) |
