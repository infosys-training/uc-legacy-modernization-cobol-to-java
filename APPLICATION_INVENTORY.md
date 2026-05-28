# APPLICATION INVENTORY — CardDemo COBOL Estate

> Auto-generated analysis of the CardDemo credit-card demonstration application.
> Total: **44 COBOL programs**, **59 copybooks**, **51 JCL jobs**, **1 Control-M scheduler definition**.

---

## 1. COBOL Program Inventory

### 1.1 Main Programs (`app/cbl/`)

| # | Filename | LOC | Purpose (inferred) | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|--------------------|----------------|--------------------|---------------------|
| 1 | CBACT01C.cbl | 430 | Read account VSAM KSDS file, write to flat (PS), array, and variable-block output files | Batch | **R:** ACCTFILE (VSAM KSDS) **W:** OUTFILE (PS), ARRYFILE (PS), VBRCFILE (VB PS) | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Read and display card data from VSAM KSDS file | Batch | **R:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Read and display cross-reference data from VSAM KSDS file | Batch | **R:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Calculate interest on account balances using transaction category balance file, cross-ref, discount groups | Batch | **R:** TCATBALF (VSAM KSDS), XREFFILE (VSAM KSDS, RANDOM + AIX), ACCTFILE (VSAM KSDS, RANDOM), DISCGRP (VSAM KSDS, RANDOM) **W:** TRANSACT (GDG +1) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Read and display customer data from VSAM KSDS file | Batch | **R:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Export all customer/account/xref/transaction/card data into a single export flat file for branch migration | Batch | **R:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE (all VSAM KSDS) **W:** EXPFILE (sequential) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Import branch migration export file, split into separate entity output files with validation | Batch | **R:** EXPFILE (sequential) **W:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | 924 | Generate account statements from transaction data — plain text and HTML output. Complex mainframe addressing with ALTER/GO TO and 2D arrays | Batch | **R:** XREFFILE, CUSTFILE, ACCTFILE, TRNXFILE (via CBSTM03B) **W:** STMTFILE (text), HTMLFILE (HTML) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | 230 | Subroutine for statement processing — opens/reads transaction, xref, customer, account files for CBSTM03A | Batch (module) | **R:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | *(none)* |
| 10 | CBTRN01C.cbl | 494 | Daily transaction validation — reads daily transactions, looks up xref and accounts for validation | Batch | **R:** DALYTRAN (sequential), CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE (all VSAM KSDS) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | 731 | Post daily transactions — validates and posts to transaction master, writes rejects | Batch | **R:** DALYTRAN (sequential), XREFFILE, ACCTFILE (VSAM KSDS) **W:** TRANFILE (VSAM KSDS), DALYREJS (GDG +1), TCATBALF (VSAM KSDS) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | 649 | Transaction reporting — produces detailed transaction reports with lookups to xref, tran-type, tran-category | Batch | **R:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM (all VSAM KSDS) **W:** TRANREPT (report file) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | 41 | Wait/pause utility — calls MVSWAIT for timed delay between batch steps | Batch (utility) | *(none)* | *(none)* |
| 14 | CSUTLDTC.cbl | 157 | Date utility — converts and validates dates in various formats | Batch (utility) | *(none)* | *(none)* |
| 15 | COACTUPC.cbl | 4,236 | Account update — full CRUD on account records via CICS BMS screens with extensive field validation (SSN, dates, amounts) | Online (CICS) | **CICS:** READ/REWRITE ACCTFILE, READ CARDXREF, READ CUSTFILE. **BMS:** COACTUP map | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×38), CSSTRPFY, CSUTLDPY |
| 16 | COACTVWC.cbl | 941 | Account view — read-only display of account, card, and customer data via CICS BMS | Online (CICS) | **CICS:** READ ACCTFILE, READ CARDXREF, READ CUSTFILE. **BMS:** COACTVW map | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 17 | COCRDLIC.cbl | 1,459 | Credit card list — paginated browse of card records via CICS BMS with forward/backward scrolling | Online (CICS) | **CICS:** STARTBR/READNEXT/READPREV/ENDBR CARDFILE. **BMS:** COCRDLI map | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 18 | COCRDSLC.cbl | 887 | Credit card search/view — search by account+card, display card details and customer info | Online (CICS) | **CICS:** READ CARDFILE, READ CUSTFILE. **BMS:** COCRDSL map | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 19 | COCRDUPC.cbl | 1,560 | Credit card update — edit card details (name, status, expiry) with validation and VSAM rewrite | Online (CICS) | **CICS:** READ/REWRITE CARDFILE, READ CUSTFILE. **BMS:** COCRDUP map | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 20 | COADM01C.cbl | 288 | Admin menu — displays admin-only menu options, routes to selected sub-program via XCTL | Online (CICS) | **CICS:** SEND/RECEIVE MAP, XCTL. **BMS:** COADM01 map | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 21 | COBIL00C.cbl | 572 | Bill payment — process bill payments with CICS file I/O including account read/rewrite and transaction write | Online (CICS) | **CICS:** READ/REWRITE ACCTFILE, STARTBR/READPREV/ENDBR TRANSACT, WRITE TRANSACT. **BMS:** COBIL00 map | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 22 | COMEN01C.cbl | 308 | Main menu — displays primary application menu, routes to selected program via XCTL. Checks CICS program availability with INQUIRE | Online (CICS) | **CICS:** SEND/RECEIVE MAP, XCTL, INQUIRE PROGRAM. **BMS:** COMEN01 map | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | 260 | Sign-on screen — authenticates users against USRSEC VSAM file, routes to main menu or admin menu | Online (CICS) | **CICS:** READ USRSEC file, XCTL to COMEN01C/COADM01C. **BMS:** COSGN00 map | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | CORPT00C.cbl | 649 | Transaction reports — report parameter input screen, submits batch report jobs to internal reader | Online (CICS) | **CICS:** WRITEQ TD (internal reader), SEND/RECEIVE MAP. **BMS:** CORPT00 map. **CALL:** CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN00C.cbl | 699 | Transaction list — paginated browse of transaction records via CICS with forward/backward scrolling | Online (CICS) | **CICS:** STARTBR/READNEXT/READPREV/ENDBR TRANSACT. **BMS:** COTRN00 map | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN01C.cbl | 330 | Transaction view — display single transaction record details | Online (CICS) | **CICS:** READ TRANSACT. **BMS:** COTRN01 map | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | COTRN02C.cbl | 783 | Transaction add — input and validate new transaction, write to VSAM transaction file with bill-payment support | Online (CICS) | **CICS:** READ CARDXREF (AIX), READ CARDXREF, STARTBR/READPREV/ENDBR TRANSACT, WRITE TRANSACT, READ ACCTFILE. **BMS:** COTRN02 map. **CALL:** CSUTLDTC | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 28 | COUSR00C.cbl | 695 | User list — paginated browse of user security records with forward/backward scrolling | Online (CICS) | **CICS:** STARTBR/READNEXT/READPREV/ENDBR USRSEC. **BMS:** COUSR00 map | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR01C.cbl | 299 | User add — input new user security record and write to USRSEC VSAM file | Online (CICS) | **CICS:** WRITE USRSEC. **BMS:** COUSR01 map | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR02C.cbl | 414 | User update — read and rewrite user security record | Online (CICS) | **CICS:** READ/REWRITE USRSEC. **BMS:** COUSR02 map | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | COUSR03C.cbl | 359 | User delete — read and delete user security record with confirmation screen | Online (CICS) | **CICS:** READ/DELETE USRSEC. **BMS:** COUSR03 map | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 1.2 Sub-Application: Authorization (IMS/DB2/MQ) (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|---------------------|
| 32 | CBPAUP0C.cbl | 386 | Purge expired authorization records from IMS database | Batch (IMS) | **IMS:** GN/GNP (CBLTDLI) on auth summary and detail segments **W:** OUTFIL1, OUTFIL2 | CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 33 | COPAUA0C.cbl | 1,026 | Authorization processing — CICS/IMS/MQ integration for payment authorization with MQ request/reply pattern | Online (CICS+IMS+MQ) | **MQ:** MQOPEN, MQGET, MQPUT1, MQCLOSE (3 queues: error, input, output) **CICS:** EXEC CICS statements **IMS:** CBLTDLI calls | CMQODV, CMQMDV, CMQGMOV, CMQPMOV, CCPAURQY, CCPAURLY, CIPAUSMY, CIPAUDTY |
| 34 | COPAUS0C.cbl | 1,032 | Authorization summary list — CICS BMS screen for browsing authorization summaries from IMS database | Online (CICS+IMS) | **CICS:** SEND/RECEIVE MAP, XCTL **IMS:** CBLTDLI calls | CIPAUSMY, CIPAUDTY, IMSFUNCS, PADFLPCB, COCOM01Y, DFHAID, DFHBMSCA, COTTL01Y, CSDAT01Y, CSMSG01Y, COPAUS0 |
| 35 | COPAUS1C.cbl | 604 | Authorization detail view — display single authorization detail record from IMS | Online (CICS+IMS) | **CICS:** SEND/RECEIVE MAP **IMS:** CBLTDLI calls | CIPAUSMY, CIPAUDTY, IMSFUNCS, PASFLPCB, COCOM01Y, DFHAID, DFHBMSCA, COTTL01Y, CSDAT01Y, CSMSG01Y, COPAUS1 |
| 36 | COPAUS2C.cbl | 244 | Authorization DB2 lookup — query DB2 authorization tables from CICS | Online (CICS+DB2) | **DB2:** EXEC SQL SELECT from authorization tables **CICS:** SEND/RECEIVE MAP | CIPAUSMY, CIPAUDTY, COCOM01Y |
| 37 | DBUNLDGS.CBL | 366 | GSAM unload — unload IMS database segments to sequential (GSAM) output file | Batch (IMS/GSAM) | **IMS:** CBLTDLI GU/GN on PCB **W:** GSAM output file | CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 38 | PAUDBLOD.CBL | 369 | DB2 load — load authorization data from flat file into DB2 tables | Batch (DB2) | **R:** Input flat file **DB2:** INSERT INTO authorization tables | CIPAUSMY, CIPAUDTY |
| 39 | PAUDBUNL.CBL | 317 | DB2 unload — extract authorization data from DB2 tables to flat file | Batch (DB2) | **DB2:** SELECT FROM authorization tables **W:** Output flat file | CIPAUSMY, CIPAUDTY |

### 1.3 Sub-Application: Transaction Type (DB2) (`app/app-transaction-type-db2/cbl/`)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|---------------------|
| 40 | COBTUPDT.cbl | 237 | Batch update of transaction types — reads flat file, performs DB2 INSERT/UPDATE/DELETE | Batch (DB2) | **R:** INPFILE (sequential) **DB2:** INSERT, UPDATE, DELETE on TRAN_TYPE table | *(none — inline definitions)* |
| 41 | COTRTLIC.cbl | 2,098 | Transaction type list — paginated CICS BMS browse of transaction types from DB2 with delete capability | Online (CICS+DB2) | **DB2:** SELECT, DELETE from TRAN_TYPE **CICS:** SEND/RECEIVE MAP, SYNCPOINT. **BMS:** COTRTLI map | *(uses EXEC SQL inline)* |
| 42 | COTRTUPC.cbl | 1,702 | Transaction type update — CICS BMS screen for updating transaction type records in DB2 | Online (CICS+DB2) | **DB2:** SELECT, UPDATE, INSERT, DELETE from TRAN_TYPE **CICS:** SEND/RECEIVE MAP, SYNCPOINT. **BMS:** COTRTUP map | *(uses EXEC SQL inline)* |

### 1.4 Sub-Application: VSAM/MQ (`app/app-vsam-mq/cbl/`)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|---------------------|
| 43 | COACCT01.cbl | 620 | Account processing via MQ — CICS-started task that reads account requests from MQ queue, processes VSAM account records, sends replies | Online (CICS+MQ) | **MQ:** MQOPEN (×3), MQGET, MQPUT (×2), MQCLOSE (×3) **CICS:** RETRIEVE, READ ACCTFILE, RETURN | CVACT01Y |
| 44 | CODATE01.cbl | 524 | Date utility via MQ — CICS-started task providing date conversion services through MQ request/reply queues | Online (CICS+MQ) | **MQ:** MQOPEN (×3), MQGET, MQPUT (×2), MQCLOSE (×3) **CICS:** RETRIEVE, ASKTIME, FORMATTIME, RETURN | *(none)* |

---

## 2. Program Classification Summary

| Classification | Count | Programs |
|----------------|-------|----------|
| **Batch** | 14 | CBACT01C, CBACT02C, CBACT03C, CBACT04C, CBCUS01C, CBEXPORT, CBIMPORT, CBSTM03A, CBSTM03B, CBTRN01C, CBTRN02C, CBTRN03C, COBSWAIT, CSUTLDTC |
| **Batch (IMS)** | 2 | CBPAUP0C, DBUNLDGS |
| **Batch (DB2)** | 3 | COBTUPDT, PAUDBLOD, PAUDBUNL |
| **Online (CICS)** | 17 | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COADM01C, COBIL00C, COMEN01C, COSGN00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| **Online (CICS+IMS)** | 2 | COPAUS0C, COPAUS1C |
| **Online (CICS+IMS+MQ)** | 1 | COPAUA0C |
| **Online (CICS+DB2)** | 3 | COPAUS2C, COTRTLIC, COTRTUPC |
| **Online (CICS+MQ)** | 2 | COACCT01, CODATE01 |

---

## 3. JCL Job Catalog

### 3.1 Main Jobs (`app/jcl/`)

| # | Job Name | Purpose | Step Sequence | Key Datasets |
|---|----------|---------|---------------|--------------|
| 1 | ACCTFILE.jcl | Define and load Account VSAM KSDS from flat file | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | ACCTDATA.PS → ACCTDATA.VSAM.KSDS |
| 2 | CARDFILE.jcl | Define and load Card Data VSAM KSDS with AIX | CLCIFIL (SDSF close), STEP05–STEP15 (IDCAMS DEL/DEF/REPRO), STEP40–STEP60 (IDCAMS AIX DEL/DEF/BLDINDEX), OPCIFIL (SDSF open) | CARDDATA.PS → CARDDATA.VSAM.KSDS + AIX |
| 3 | CBADMCDJ.jcl | CICS CSD (Communication System Definition) management — define programs, maps, files, transactions | Multiple DFHCSDUP steps | CICS CSD definitions |
| 4 | CBEXPORT.jcl | Execute branch data export program | STEP15 (PGM=CBEXPORT) | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE → EXPFILE |
| 5 | CBIMPORT.jcl | Execute branch data import program | STEP15 (PGM=CBIMPORT) | EXPFILE → CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT |
| 6 | CLOSEFIL.jcl | Close CICS files for batch processing | OPCIFIL (SDSF) | CICS file commands |
| 7 | COMBTRAN.jcl | Combine daily transaction files into master | STEP05 (SORT), STEP10 (IDCAMS REPRO) | DALYTRAN → TRANSACT.VSAM.KSDS |
| 8 | CREASTMT.JCL | Create account statements | DELDEF01 (IDCAMS), STEP010 (SORT), STEP020 (IDCAMS), STEP030 (IEFBR14), STEP040 (PGM=CBSTM03A) | TRANSACT → STMTFILE (text) + HTMLFILE (HTML) |
| 9 | CUSTFILE.jcl | Define and load Customer VSAM KSDS | STEP05–STEP20 (IDCAMS DEL/DEF/REPRO), STEP25–STEP30 (AIX) | CUSTDATA.PS → CUSTDATA.VSAM.KSDS + AIX |
| 10 | DALYREJS.jcl | Define GDG base for daily rejected transactions | STEP05 (IDCAMS DEFINE GDG) | DALYREJS GDG |
| 11 | DEFCUST.jcl | Alternate customer file definitions | STEP05+ (IDCAMS) | CUSTDATA VSAM files |
| 12 | DEFGDGB.jcl | Define GDG base for transaction backup | STEP05 (IDCAMS DEFINE GDG) | TRANSACT.BKUP GDG |
| 13 | DEFGDGD.jcl | Define GDG base for daily transactions | STEP05 (IDCAMS DEFINE GDG) | SYSTRAN GDG |
| 14 | DISCGRP.jcl | Define and load Disclosure Group VSAM KSDS | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | DISCGRP.PS → DISCGRP.VSAM.KSDS |
| 15 | DUSRSECJ.jcl | Define and load User Security VSAM KSDS | STEP05–STEP15 (IDCAMS DEL/DEF/REPRO) | USRSEC.PS → USRSEC.VSAM.KSDS |
| 16 | ESDSRRDS.jcl | Define ESDS and RRDS VSAM files for user security | Multiple IDCAMS + REPRO steps | USRSEC.VSAM.ESDS, USRSEC.VSAM.RRDS |
| 17 | FTPJCL.JCL | FTP file transfer | STEP1 (PGM=FTP) | FTP transfer |
| 18 | INTCALC.jcl | Execute interest calculation program | STEP15 (PGM=CBACT04C, PARM='2022071800') | TCATBALF, XREFFILE (+ AIX), ACCTFILE, DISCGRP, TRANSACT (GDG +1) |
| 19 | INTRDRJ1.JCL | Internal reader job 1 — backup and submit INTRDRJ2 | IDCAMS (DELETE), STEP01 (IEBGENER copy + submit) | FTP.TEST → FTP.TEST.BKUP |
| 20 | INTRDRJ2.JCL | Internal reader job 2 — backup copy | IDCAMS | FTP.TEST.BKUP → FTP.TEST.BKUP.INTRDR |
| 21 | OPENFIL.jcl | Open CICS files after batch processing | OPCIFIL (SDSF) | CICS file commands |
| 22 | POSTTRAN.jcl | Post daily transactions to master | STEP15 (PGM=CBTRN02C) | DALYTRAN, TRANFILE, XREFFILE, DALYREJS (GDG), ACCTFILE, TCATBALF |
| 23 | PRTCATBL.jcl | Print transaction category balance report | DELDEF (IEFBR14), STEP05R (REPROC), STEP10R (SORT) | TCATBALF.VSAM.KSDS → TCATBALF.REPT |
| 24 | READACCT.jcl | Read and extract account data | PREDEL (IEFBR14), STEP05 (PGM=CBACT01C) | ACCTDATA.VSAM.KSDS → PS, ARRYPS, VBPS |
| 25 | READCARD.jcl | Read and display card data | STEP05 (PGM=CBACT02C) | CARDDATA.VSAM.KSDS |
| 26 | READCUST.jcl | Read and display customer data | STEP05 (PGM=CBCUS01C) | CUSTDATA.VSAM.KSDS |
| 27 | READXREF.jcl | Read and display cross-reference data | STEP05 (PGM=CBACT03C) | CARDXREF.VSAM.KSDS |
| 28 | REPTFILE.jcl | Define GDG base for report files | STEP05 (IDCAMS DEFINE GDG) | Report GDG |
| 29 | TCATBALF.jcl | Define and load Transaction Category Balance VSAM KSDS | STEP05–STEP15 (IDCAMS DEL/DEF/REPRO) | TCATBALF.PS → TCATBALF.VSAM.KSDS |
| 30 | TRANBKP.jcl | Backup and clear transaction master | STEP05R (REPROC), STEP05 (IDCAMS REPRO), STEP10 (IDCAMS DELETE) | TRANSACT.VSAM.KSDS → TRANSACT.BKUP (GDG +1) |
| 31 | TRANCATG.jcl | Define and load Transaction Category VSAM KSDS | STEP05–STEP15 (IDCAMS DEL/DEF/REPRO) | TRANCATG.PS → TRANCATG.VSAM.KSDS |
| 32 | TRANFILE.jcl | Define Transaction Master VSAM KSDS with AIX | CLCIFIL (SDSF), STEP05–STEP30 (IDCAMS DEL/DEF/REPRO/AIX), OPCIFIL (SDSF) | DALYTRAN.PS → TRANSACT.VSAM.KSDS + AIX |
| 33 | TRANIDX.jcl | Define alternate indexes on Transaction Master | STEP20–STEP30 (IDCAMS DEFINE AIX/PATH/BLDINDEX) | TRANSACT.VSAM.KSDS AIX |
| 34 | TRANREPT.jcl | Generate transaction report | STEP05R (REPROC + SORT), STEP10R (PGM=CBTRN03C) | TRANSACT.VSAM.KSDS → TRANSACT.DALY (GDG +1) → report |
| 35 | TRANTYPE.jcl | Define and load Transaction Type VSAM KSDS | STEP05–STEP15 (IDCAMS DEL/DEF/REPRO) | TRANTYPE.PS → TRANTYPE.VSAM.KSDS |
| 36 | TXT2PDF1.JCL | Convert text statement to PDF | TXT2PDF (PGM=IKJEFT1B with TXT2PDF REXX) | STATEMNT.PS → PDF |
| 37 | WAITSTEP.jcl | Execute wait/pause utility | WAIT (PGM=COBSWAIT) | *(none)* |
| 38 | XREFFILE.jcl | Define and load Cross-Reference VSAM KSDS with AIX | STEP05–STEP30 (IDCAMS DEL/DEF/REPRO/AIX) | CARDXREF.PS → CARDXREF.VSAM.KSDS + AIX |

### 3.2 Sub-Application JCL (`app/app-*/jcl/`)

| # | Job Name | Sub-App | Purpose | Step Sequence | Key Datasets |
|---|----------|---------|---------|---------------|--------------|
| 39 | CBPAUP0J.jcl | auth-ims-db2-mq | Execute authorization purge program | STEP15 (PGM=CBPAUP0C, IMS region) | IMS DB auth segments |
| 40 | DBPAUTP0.jcl | auth-ims-db2-mq | Authorization DB2 processing | STEP15 (PGM) | DB2 auth tables |
| 41 | LOADPADB.JCL | auth-ims-db2-mq | Load authorization data into DB2 | STEP (PGM=PAUDBLOD) | Flat file → DB2 tables |
| 42 | UNLDGSAM.JCL | auth-ims-db2-mq | Unload IMS database to GSAM | STEP (PGM=DBUNLDGS) | IMS DB → GSAM file |
| 43 | UNLDPADB.JCL | auth-ims-db2-mq | Unload authorization DB2 to flat file | STEP (PGM=PAUDBUNL) | DB2 tables → flat file |
| 44 | CREADB21.jcl | tran-type-db2 | Create DB2 transaction type table and load data | DDL + LOAD steps | DB2 TRAN_TYPE table |
| 45 | MNTTRDB2.jcl | tran-type-db2 | Maintain transaction type DB2 table | STEP (PGM=COBTUPDT) | INPFILE → DB2 TRAN_TYPE |
| 46 | TRANEXTR.jcl | tran-type-db2 | Extract transaction types from DB2 | Multiple IDCAMS + DB2 UNLOAD steps | DB2 → TRANTYPE.PS |

### 3.3 Sample JCL (`samples/jcl/`) — *Not production; provided as usage examples*

| Job Name | Purpose |
|----------|---------|
| CBACT01C.jcl | Sample run of CBACT01C |
| CBACT02C.jcl | Sample run of CBACT02C |
| CBACT03C.jcl | Sample run of CBACT03C |
| CBSTM03A.jcl | Sample run of statement generator |
| COMPILE.jcl | Sample compile/link JCL |

---

## 4. Control-M Scheduler Definitions (`app/scheduler/CardDemo.controlm`)

Three scheduled batch flows are defined:

### 4.1 DAILY — Transaction Backup
**Schedule:** Daily, all months
**Flow:** `CLOSEFIL` → `TRANBKP` → `WAITSTEP` → `OPENFIL`

### 4.2 WEEKLY — Transaction Types DB Refresh
**Schedule:** Saturday, all months
**Flow:** `MNTTRDB2` → (triggers two parallel smart folders):
- **DisclosureGroupsRefresh:** `CLOSEFIL` → `DISCGRP` → `WAITSTEP` → `OPENFIL`
- **TransactionTypesDBRefresh:** `TRANEXTR`

### 4.3 MONTHLY — Interest Calculation
**Schedule:** Monthly, all months
**Flow:** `CLOSEFIL` → `INTCALC` → `COMBTRAN` → `WAITSTEP` → `OPENFIL`

---

## 5. Technology Stack Summary

| Technology | Usage | Programs |
|-----------|-------|----------|
| **VSAM KSDS** | Primary data storage (indexed sequential) | All batch + most CICS programs |
| **VSAM AIX** | Alternate indexes for secondary key access | CARDFILE, XREFFILE, TRANFILE |
| **VSAM ESDS/RRDS** | Entry-sequenced and relative-record storage | ESDSRRDS.jcl (user security) |
| **GDG** | Generation data groups for historical versions | TRANSACT.BKUP, DALYREJS, SYSTRAN |
| **CICS** | Online transaction processing (3270 BMS screens) | 17 main + 5 sub-app programs |
| **IMS (DL/I)** | Hierarchical database access | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS |
| **DB2** | Relational database access | COPAUS2C, COBTUPDT, COTRTLIC, COTRTUPC, PAUDBLOD, PAUDBUNL |
| **MQ Series** | Asynchronous message queuing | COPAUA0C, COACCT01, CODATE01 |
| **BMS Maps** | CICS screen definitions (3270 terminal) | All CICS online programs |
| **Control-M** | Batch job scheduling | Daily/Weekly/Monthly pipelines |
| **SORT** | DFSORT/SYNCSORT for data ordering | COMBTRAN, CREASTMT, PRTCATBL, TRANREPT |
| **IDCAMS** | VSAM file utility (define, delete, repro) | Most file-definition JCL |
| **IEFBR14** | Null program for catalog operations | CREASTMT, PRTCATBL, READACCT |
| **SDSF** | System Display and Search Facility (open/close CICS files) | CLOSEFIL, OPENFIL, CARDFILE, TRANFILE |
