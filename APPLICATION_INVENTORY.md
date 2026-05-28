# Application Inventory — CardDemo COBOL Estate

> Auto-generated analysis of every COBOL program and JCL job in the CardDemo credit-card management system.

---

## 1  COBOL Programs — Core Application (`app/cbl/`)

### 1.1  Batch Programs

| # | Filename | Purpose | LOC | Key I/O (files read / written) | Copybooks Referenced |
|---|----------|---------|-----|-------------------------------|---------------------|
| 1 | **CBACT01C.cbl** | Read the Account VSAM file and write into multiple output formats (flat, array, variable-block) | 431 | R: ACCTFILE (Account VSAM) · W: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | **CBACT02C.cbl** | Read and print card data file | 179 | R: CARDFILE (Card VSAM) | CVACT02Y |
| 3 | **CBACT03C.cbl** | Read and print account cross-reference data file | 179 | R: XREFFILE (Card-Xref VSAM) | CVACT03Y |
| 4 | **CBACT04C.cbl** | Interest calculator — compute interest and fees per transaction category, update accounts | 653 | R: TCATBALF, XREFFILE, DISCGRP · RW: ACCTFILE · W: TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | **CBCUS01C.cbl** | Read and print customer data file | 179 | R: CUSTFILE (Customer VSAM) | CVCUS01Y |
| 6 | **CBEXPORT.cbl** | Export customer data for branch migration — reads all normalized CardDemo files and creates multi-record export file | 583 | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · W: EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | **CBIMPORT.cbl** | Import customer data from branch migration export — splits multi-record export into normalized target files with validation | 488 | R: EXPFILE · W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | **CBSTM03A.CBL** | Print account statements from transaction data (master report driver) | 925 | R: (via CBSTM03B) · W: STMTFILE, HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | **CBSTM03B.CBL** | File processing sub-module for transaction report (called by CBSTM03A) | 231 | R: TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE · RW: (internal) | _(none — inline definitions)_ |
| 10 | **CBTRN01C.cbl** | Post records from daily transaction file — validate and create transaction records | 495 | R: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | **CBTRN02C.cbl** | Post records from daily transaction file — update transaction master, reject invalid, update category balances and accounts | 732 | R: DALYTRAN, XREFFILE · RW: ACCTFILE, TCATBALF · W: TRANFILE, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | **CBTRN03C.cbl** | Print transaction detail report with page/account/grand totals | 650 | R: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM · W: TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | **COBSWAIT.cbl** | Utility — wait for a specified number of centiseconds (calls assembler MVSWAIT) | 42 | _(none)_ | _(none)_ |
| 14 | **CSUTLDTC.cbl** | Date validation utility — calls LE CEEDAYS to validate date strings | 158 | _(none)_ | _(none)_ |

### 1.2  Online (CICS) Programs

| # | Filename | Purpose | LOC | EXEC CICS | Key I/O (VSAM via CICS) | Copybooks Referenced |
|---|----------|---------|-----|-----------|------------------------|---------------------|
| 1 | **COSGN00C.cbl** | Sign-on screen — authenticate users against USRSEC file | 261 | 10 | R: USRSEC | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | **COMEN01C.cbl** | Main menu for regular users — route to functional screens | 309 | 7 | _(none — navigation only)_ | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | **COADM01C.cbl** | Admin menu for admin users — route to admin screens | 289 | 7 | _(none — navigation only)_ | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | **COACTVWC.cbl** | View account details — display account, card, and customer information | 942 | 15 | R: ACCTDAT, CARDDAT, CARDAIX, CUSTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 5 | **COACTUPC.cbl** | Update account — full account update with field validation (address, phone, SSN, dates) | 4237 | 17 | R: ACCTDAT, CARDDAT, CARDAIX, CUSTDAT · RW: ACCTDAT, CUSTDAT | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSTRPFY, CSUTLDPY |
| 6 | **COCRDLIC.cbl** | List credit cards with pagination and filtering | 1460 | 18 | R: CARDDAT, CARDAIX | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | **COCRDSLC.cbl** | View credit card details — display card, account, and customer info | 888 | 14 | R: CARDDAT, ACCTDAT, CUSTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | **COCRDUPC.cbl** | Update credit card details with validation | 1561 | 12 | R: CARDDAT, ACCTDAT, CUSTDAT · RW: CARDDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | **COTRN00C.cbl** | List transactions from TRANSACT file with pagination | 700 | 10 | R: TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | **COTRN01C.cbl** | View a single transaction from TRANSACT file | 331 | 5 | R: TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | **COTRN02C.cbl** | Add a new transaction — validate card, account; write to TRANSACT and update balances | 784 | 11 | R: ACCTDAT, CARDXREF · W: TRANSACT · RW: ACCTDAT | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | **COBIL00C.cbl** | Bill payment — pay account balance in full and add transaction record | 573 | 13 | R: ACCTDAT, CARDXREF · RW: ACCTDAT · W: TRANSACT | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | **CORPT00C.cbl** | Submit batch transaction reports from online (calls CSUTLDTC for date validation) | 650 | 7 | W: (submits JCL) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | **COUSR00C.cbl** | List all users from USRSEC file with pagination | 696 | 11 | R: USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | **COUSR01C.cbl** | Add a new regular/admin user to USRSEC file | 300 | 5 | W: USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | **COUSR02C.cbl** | Update an existing user in USRSEC file | 415 | 6 | R: USRSEC · RW: USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | **COUSR03C.cbl** | Delete a user from USRSEC file | 360 | 6 | R: USRSEC · DEL: USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2  COBOL Programs — Sub-Applications

### 2.1  Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | Purpose | Classification | LOC | Key I/O | Copybooks Referenced |
|---|----------|---------|---------------|-----|---------|---------------------|
| 1 | **CBPAUP0C.cbl** | Delete expired pending authorization messages from IMS DB | Batch (IMS) | 387 | DLI: 5 calls · R/DEL: IMS segments | CIPAUSMY, CIPAUDTY |
| 2 | **COPAUA0C.cbl** | Card authorization decision — read MQ request, validate via IMS/CICS, send MQ response | Online (CICS+IMS+MQ) | 1027 | MQ: MQOPEN/GET/PUT1/CLOSE · CICS: 12 · DLI: 8 | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 3 | **COPAUS0C.cbl** | Summary view of pending authorization messages | Online (CICS+IMS) | 1033 | CICS: 10 · DLI: 6 | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 4 | **COPAUS1C.cbl** | Detail view of a single authorization message | Online (CICS+IMS) | 605 | CICS: 8 · DLI: 7 | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 5 | **COPAUS2C.cbl** | Mark authorization message as fraud (updates DB2) | Online (CICS+DB2) | 245 | CICS: 3 · SQL: 4 | CIPAUDTY |
| 6 | **DBUNLDGS.CBL** | Unload IMS database to GSAM sequential file | Batch (IMS) | 367 | DLI: CBLTDLI calls | _(none)_ |
| 7 | **PAUDBLOD.CBL** | Load pending authorization IMS database from flat files | Batch (IMS) | 370 | R: INFILE1, INFILE2 · DLI: CBLTDLI | _(none)_ |
| 8 | **PAUDBUNL.CBL** | Unload pending authorization IMS database to flat files | Batch (IMS) | 318 | W: OUTFIL1, OUTFIL2 · DLI: CBLTDLI | _(none)_ |

### 2.2  Transaction Type (DB2) — `app/app-transaction-type-db2/cbl/`

| # | Filename | Purpose | Classification | LOC | Key I/O | Copybooks Referenced |
|---|----------|---------|---------------|-----|---------|---------------------|
| 1 | **COBTUPDT.cbl** | Batch update of transaction types from input file into DB2 tables | Batch (DB2) | 238 | R: INPFILE · SQL: 5 (INSERT/UPDATE/DELETE) | _(none)_ |
| 2 | **COTRTLIC.cbl** | List transaction types from DB2 for update/delete with pagination | Online (CICS+DB2) | 2099 | CICS: 12 · SQL: 16 | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 3 | **COTRTUPC.cbl** | Accept and process transaction type updates in DB2 | Online (CICS+DB2) | 1703 | CICS: 12 · SQL: 7 | _(inline definitions)_ |

### 2.3  VSAM + MQ — `app/app-vsam-mq/cbl/`

| # | Filename | Purpose | Classification | LOC | Key I/O | Copybooks Referenced |
|---|----------|---------|---------------|-----|---------|---------------------|
| 1 | **COACCT01.cbl** | Account inquiry via MQ messaging | Online (CICS+MQ) | 621 | CICS: 4 · MQ: MQOPEN/GET/PUT/CLOSE | _(inline)_ |
| 2 | **CODATE01.cbl** | Date service via MQ messaging | Online (CICS+MQ) | 525 | CICS: 5 · MQ: MQOPEN/GET/PUT/CLOSE | _(inline)_ |

---

## 3  JCL Job Catalog (`app/jcl/`)

### 3.1  VSAM Dataset Definition & Load Jobs

| # | Job Name | JCL File | Purpose | Step Sequence | Key Datasets |
|---|----------|----------|---------|---------------|-------------|
| 1 | ACCTFILE | ACCTFILE.jcl | Delete/define/load Account VSAM KSDS | STEP05 (IDCAMS DEL) → STEP10 (IDCAMS DEF) → STEP15 (IDCAMS REPRO) | ACCTDATA.PS → ACCTDATA.VSAM.KSDS |
| 2 | CARDFILE | CARDFILE.jcl | Delete/define/load Card VSAM KSDS + AIX + PATH | CLCIFIL (SDSF close) → STEP05–10 (DEL/DEF) → STEP15 (REPRO) → STEP40–60 (AIX/PATH/BLDINDEX) → OPCIFIL (SDSF open) | CARDDATA.PS → CARDDATA.VSAM.KSDS |
| 3 | CUSTFILE | CUSTFILE.jcl | Define/load Customer VSAM KSDS | CLCIFIL → STEP05–15 (DEL/DEF/REPRO) → OPCIFIL | CUSTDATA.PS → CUSTDATA.VSAM.KSDS |
| 4 | XREFFILE | XREFFILE.jcl | Delete/define/load Cross-Reference VSAM KSDS + AIX + PATH | STEP05–30 (DEL/DEF/REPRO/AIX/PATH/BLDINDEX) | CARDXREF.PS → CARDXREF.VSAM.KSDS |
| 5 | TRANFILE | TRANFILE.jcl | Define Transaction Master VSAM KSDS + AIX | CLCIFIL → STEP05–30 (DEL/DEF/REPRO/AIX/PATH/BLDINDEX) → OPCIFIL | DALYTRAN.PS.INIT → TRANSACT.VSAM.KSDS |
| 6 | TCATBALF | TCATBALF.jcl | Define Transaction Category Balance VSAM KSDS | STEP05–15 (DEL/DEF/REPRO) | TCATBALF.PS → TCATBALF.VSAM.KSDS |
| 7 | TRANCATG | TRANCATG.jcl | Define Transaction Category VSAM KSDS | STEP05–15 (DEL/DEF/REPRO) | TRANCATG.PS → TRANCATG.VSAM.KSDS |
| 8 | TRANTYPE | TRANTYPE.jcl | Define Transaction Type VSAM KSDS | STEP05–15 (DEL/DEF/REPRO) | TRANTYPE.PS → TRANTYPE.VSAM.KSDS |
| 9 | DISCGRP | DISCGRP.jcl | Define Disclosure Group VSAM KSDS | STEP05–15 (DEL/DEF/REPRO) | DISCGRP.PS → DISCGRP.VSAM.KSDS |
| 10 | DUSRSECJ | DUSRSECJ.jcl | Define User Security VSAM KSDS | PREDEL → STEP01–03 (GEN/DEL/DEF/REPRO) | USRSEC.PS → USRSEC.VSAM.KSDS |
| 11 | DEFCUST | DEFCUST.jcl | Define Customer data file (alternate) | STEP05 (DEL/DEF) | _(IDCAMS only)_ |
| 12 | ESDSRRDS | ESDSRRDS.jcl | Define ESDS and RRDS variants of USRSEC | PREDEL → STEP01–05 | ESDSRRDS.PS → USRSEC.VSAM.ESDS, USRSEC.VSAM.RRDS |

### 3.2  Batch Processing Jobs

| # | Job Name | JCL File | Purpose | Step Sequence | COBOL Program(s) |
|---|----------|----------|---------|---------------|------------------|
| 1 | POSTTRAN | POSTTRAN.jcl | Post daily transactions to master file | STEP15 (CBTRN02C) | CBTRN02C |
| 2 | INTCALC | INTCALC.jcl | Calculate interest on accounts | STEP15 (CBACT04C) | CBACT04C |
| 3 | COMBTRAN | COMBTRAN.jcl | Combine transaction backup with system transactions | STEP05R (SORT) → STEP10 (IDCAMS REPRO) | _(SORT/IDCAMS only)_ |
| 4 | CREASTMT | CREASTMT.JCL | Create account statements | DELDEF01 → STEP010 (SORT) → STEP020 (IDCAMS) → STEP030 (IEFBR14) → STEP040 (CBSTM03A) | CBSTM03A |
| 5 | TRANREPT | TRANREPT.jcl | Print transaction detail report | STEP05R (REPROC) → STEP05R (SORT) → STEP10R (CBTRN03C) | CBTRN03C |
| 6 | PRTCATBL | PRTCATBL.jcl | Print transaction category balance file | DELDEF → STEP05R (REPROC) → STEP10R (SORT) | _(SORT only)_ |
| 7 | TRANBKP | TRANBKP.jcl | REPRO and delete transaction master (backup) | STEP05R (REPROC) → STEP05 (IDCAMS DEL) → STEP10 (IDCAMS REPRO) | _(IDCAMS only)_ |
| 8 | READACCT | READACCT.jcl | Read accounts and write output files | PREDEL → STEP05 (CBACT01C) | CBACT01C |
| 9 | READCARD | READCARD.jcl | Read and print card data | STEP05 (CBACT02C) | CBACT02C |
| 10 | READCUST | READCUST.jcl | Read and print customer data | STEP05 (CBCUS01C) | CBCUS01C |
| 11 | READXREF | READXREF.jcl | Read and print cross-reference data | STEP05 (CBACT03C) | CBACT03C |
| 12 | CBEXPORT | CBEXPORT.jcl | Export customer data for branch migration | STEP01 (IDCAMS) → STEP02 (CBEXPORT) | CBEXPORT |
| 13 | CBIMPORT | CBIMPORT.jcl | Import data from branch migration export | STEP01 (CBIMPORT) | CBIMPORT |
| 14 | WAITSTEP | WAITSTEP.jcl | Wait step (pause batch flow) | WAIT (COBSWAIT) | COBSWAIT |

### 3.3  Infrastructure / Utility Jobs

| # | Job Name | JCL File | Purpose | Step Sequence |
|---|----------|----------|---------|---------------|
| 1 | CLOSEFIL | CLOSEFIL.jcl | Close CICS files for batch processing | CLCIFIL (SDSF) |
| 2 | OPENFIL | OPENFIL.jcl | Open CICS files after batch processing | OPCIFIL (SDSF) |
| 3 | CBADMCDJ | CBADMCDJ.jcl | Create CICS CSD resource definitions for CardDemo | STEP1 (DFHCSDUP) |
| 4 | DEFGDGB | DEFGDGB.jcl | Define GDG base entries | STEP05 (IDCAMS) |
| 5 | DEFGDGD | DEFGDGD.jcl | Define DB2-related GDG entries and seed initial generations | STEP10–60 (IDCAMS/IEBGENER) |
| 6 | DALYREJS | DALYREJS.jcl | Define GDG for daily reject file | STEP05 (IDCAMS) |
| 7 | REPTFILE | REPTFILE.jcl | Define GDG for report files | STEP05 (IDCAMS) |
| 8 | TRANIDX | TRANIDX.jcl | Define AIX on transaction master | STEP20–30 (IDCAMS) |
| 9 | FTPJCLS | FTPJCL.JCL | FTP file transfer | STEP1 (FTP) |
| 10 | INTRDRJ1 | INTRDRJ1.JCL | Internal reader job 1 (chain to INTRDRJ2) | IDCAMS → STEP01 (IEBGENER) |
| 11 | INTRDRJ2 | INTRDRJ2.JCL | Internal reader job 2 | IDCAMS |
| 12 | TXT2PDF1 | TXT2PDF1.JCL | Convert text statement to PDF | TXT2PDF (IKJEFT1B) |

### 3.4  Sub-Application JCL

#### Authorization (IMS) — `app/app-authorization-ims-db2-mq/jcl/`

| Job Name | JCL File | Purpose | Steps |
|----------|----------|---------|-------|
| CBPAUP0J | CBPAUP0J.jcl | Run pending auth purge via IMS | STEP01 (DFSRRC00 → CBPAUP0C) |
| DBPAUTP0 | DBPAUTP0.jcl | Unload pending auth IMS DB | STEPDEL (IEFBR14) → UNLOAD (DFSRRC00) |
| LOADPADB | LOADPADB.JCL | Load pending auth IMS DB | STEP01 (DFSRRC00 → PAUDBLOD) |
| UNLDGSAM | UNLDGSAM.JCL | Unload pending auth to GSAM | STEP01 (DFSRRC00 → DBUNLDGS) |
| UNLDPADB | UNLDPADB.JCL | Unload pending auth DB to flat files | STEP0 (IEFBR14) → STEP01 (DFSRRC00 → PAUDBUNL) |

#### Transaction Type (DB2) — `app/app-transaction-type-db2/jcl/`

| Job Name | JCL File | Purpose | Steps |
|----------|----------|---------|-------|
| CREADB2 | CREADB21.jcl | Create DB2 tables and load reference data | FREEPLN → CRCRDDB → LDTTYPE → RUNTEP2 → LDTCCAT (IKJEFT01) |
| MNTTRDB2 | MNTTRDB2.jcl | Run DB2 maintenance for transaction types | STEP1 (IKJEFT01 → COBTUPDT) |
| TRANEXTR | TRANEXTR.jcl | Extract transaction types from DB2 to flat files | STEP10–50 (IEBGENER/IKJEFT01) |

---

## 4  Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Batch programs | 14 (core) + 5 (auth IMS) + 1 (DB2) = **20** |
| Online (CICS) programs | 17 (core) + 5 (auth) + 2 (DB2) + 2 (MQ) = **24** (includes 2 that are utilities) |
| Total JCL jobs | 46 |
| VSAM definition/load jobs | 12 |
| Batch processing jobs | 14 |
| Infrastructure/utility jobs | 12 |
| Sub-application JCL jobs | 8 |
| Total lines of COBOL | ~28,500 |
| BMS map definitions | 18 (in `app/bms/` + sub-apps) |
| Copybooks | 41 (across `app/cpy/` + sub-apps) |
