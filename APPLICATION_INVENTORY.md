# APPLICATION INVENTORY — CardDemo COBOL Estate

## Summary

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Main programs (`app/cbl/`) | 31 |
| Sub-app programs | 13 |
| Total lines of COBOL | 30,175 |
| JCL jobs | 46 |
| BMS screen maps | 16 |
| Copybooks | 47 |

---

## 1. Main Application Programs (`app/cbl/`)

### 1.1 Online (CICS) Programs

| # | Filename | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|---------------------|---------------------|
| 1 | COSGN00C.cbl | 260 | **Sign-on screen** — Authenticates users against USRSEC file; routes admins to COADM01C or users to COMEN01C via XCTL | CICS READ (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | COADM01C.cbl | 288 | **Admin menu hub** — Presents 6 admin options (User CRUD, Tran-Type DB2 list/maintenance); dispatches via XCTL | CICS SEND/RECEIVE MAP, XCTL to target programs | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COMEN01C.cbl | 308 | **Main menu hub** — Central navigation for 11 user functions (acct view/update, card list/view/update, tran list/view/add, reports, bill pay, pending auth); XCTL to targets | CICS SEND/RECEIVE MAP, CICS INQUIRE, XCTL to 11 programs | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTVWC.cbl | 941 | **Account view** — Displays account, customer, and card data for a given account ID; read-only screen with field stripping | CICS READ (ACCTDAT, CARDXREF by AIX, CUSTDAT) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 5 | COACTUPC.cbl | 4,236 | **Account update** — Full field-level validation (date, SSN, phone, state, ZIP, FICO) for account and customer update; largest program in estate | CICS READ/REWRITE (ACCTDAT, CARDXREF, CUSTDAT); CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×13 via COPY REPLACING) |
| 6 | COCRDLIC.cbl | 1,459 | **Credit card list** — Paginated browse of cards using STARTBR/READNEXT/READPREV/ENDBR pattern on CARDDAT by account; dispatches to detail/update | CICS STARTBR, READNEXT, READPREV, ENDBR (CARDDAT); CICS XCTL (COCRDSLC, COCRDUPC) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | COCRDSLC.cbl | 887 | **Credit card view** — Displays card detail with customer info; read-only | CICS READ (CARDDAT by key, CARDDAT by AIX) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | COCRDUPC.cbl | 1,560 | **Credit card update** — Validates and updates card fields (name, status, expiry); rewrites CARDDAT record | CICS READ/REWRITE (CARDDAT) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | COTRN00C.cbl | 699 | **Transaction list** — Paginated browse of processed transactions using STARTBR/READNEXT/READPREV; routes to detail view | CICS STARTBR, READNEXT, READPREV, ENDBR (TRANSACT) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | 330 | **Transaction view** — Displays single transaction detail; read-only | CICS READ (TRANSACT) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | 783 | **Transaction add** — Validates and writes new transaction; validates card via XREF, calls CSUTLDTC for date validation | CICS READ (CARDXREF by AIX, CARDXREF), CICS WRITE (TRANSACT); CALL CSUTLDTC | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | CORPT00C.cbl | 649 | **Report request** — Date range input for daily transaction report; submits batch JCL via internal reader (INTRDRJ1/J2); calls CSUTLDTC for date validation | CICS SEND/RECEIVE; submits JCL to INTRDR; CALL CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | COBIL00C.cbl | 572 | **Bill payment** — Processes bill payment: looks up account via XREF, reads last transaction, writes new payment transaction, updates account balance | CICS READ/REWRITE (ACCTDAT), CICS READ (CARDXREF by AIX), CICS STARTBR/READPREV/ENDBR + WRITE (TRANSACT) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | 695 | **User list** — Paginated browse of USRSEC file with STARTBR/READNEXT/READPREV | CICS STARTBR, READNEXT, READPREV, ENDBR (USRSEC) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | 299 | **User add** — Creates new user security record | CICS WRITE (USRSEC) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | 414 | **User update** — Reads and rewrites user security record (password, type) | CICS READ/REWRITE (USRSEC) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | 359 | **User delete** — Reads user, confirms with PF5, then deletes from USRSEC | CICS READ/DELETE (USRSEC) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 1.2 Batch Programs

| # | Filename | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|---------------------|---------------------|
| 18 | CBACT01C.cbl | 430 | **Account file reader** — Reads ACCTFILE VSAM KSDS sequentially; writes fixed-length, array, and variable-length output files; calls COBDATFT assembler for date formatting | READ (ACCTFILE), WRITE (OUTFILE, ARRYFILE, VBRCFILE); CALL COBDATFT | CVACT01Y, CODATECN |
| 19 | CBACT02C.cbl | 178 | **Card file reader** — Reads and displays CARDFILE VSAM KSDS contents | READ (CARDFILE); CALL CEE3ABD (abend) | CVACT02Y |
| 20 | CBACT03C.cbl | 178 | **Cross-reference reader** — Reads and displays CARDXREF VSAM KSDS contents | READ (XREFFILE); CALL CEE3ABD (abend) | CVACT03Y |
| 21 | CBACT04C.cbl | 652 | **Interest calculation** — Reads transaction category balances; looks up discount/interest rates; computes interest and fees; rewrites account records | READ (TCATBALF, XREFFILE, DISCGRP), READ/REWRITE (ACCOUNT), WRITE (TRANSACT) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 22 | CBCUS01C.cbl | 178 | **Customer file reader** — Reads and displays CUSTFILE VSAM KSDS contents | READ (CUSTFILE); CALL CEE3ABD (abend) | CVCUS01Y |
| 23 | CBTRN01C.cbl | 494 | **Daily transaction enricher** — Reads daily transactions, enriches with XREF/customer/card/account data for validation | READ (DALYTRAN, XREFFILE, CUSTFILE, CARDFILE, ACCTFILE, TRANSACT) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 24 | CBTRN02C.cbl | 731 | **Transaction posting** — Validates and posts daily transactions to master TRANSACT file; writes rejects to DALYREJS; updates account balance and category balance | READ (DALYTRAN, XREFFILE), READ/REWRITE (ACCOUNT, TCATBAL), WRITE (TRANSACT, DALYREJS) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 25 | CBTRN03C.cbl | 649 | **Transaction report** — Generates daily transaction report with page/account/grand totals; looks up tran type and category descriptions | READ (TRANSACT, CARDXREF, TRANTYPE, TRANCATG, DATEPARM), WRITE (REPTFILE) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 26 | CBSTM03A.CBL | 924 | **Statement generation (main)** — Generates customer statements in text and HTML format; iterates through XREF → customer → account → transactions; calls CBSTM03B for file I/O | CALL CBSTM03B; OPEN/CLOSE (STMT-FILE, HTML-FILE); WRITE (STMTFILE, HTMLFILE) — 118 I/O ops (highest in estate) | COSTM01 (via ALTER), CVACT01Y, CVACT03Y, CVCUS01Y, CVTRA05Y |
| 27 | CBSTM03B.CBL | 230 | **Statement generation (I/O submodule)** — Called by CBSTM03A; handles OPEN/READ/CLOSE for TRNX, XREF, CUST, and ACCT files | OPEN/READ/CLOSE (TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE) | *(none — receives data via LINKAGE SECTION)* |
| 28 | CBEXPORT.cbl | 582 | **Data export** — Reads all 5 VSAM master files; creates unified sequential export file with typed records (C=Customer, A=Account, X=XREF, T=Transaction, D=Card) | READ (CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE), WRITE (EXPORT-OUTPUT) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 29 | CBIMPORT.cbl | 487 | **Data import** — Reads unified export file; parses record types; validates; writes to individual output files and error file | READ (EXPORT-INPUT), WRITE (CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 30 | COBSWAIT.cbl | 41 | **Wait utility** — Thin wrapper that calls MVSWAIT assembler routine for timed delays | CALL MVSWAIT | *(none)* |
| 31 | CSUTLDTC.cbl | 157 | **Date validation utility** — Validates dates by calling LE CEEDAYS routine; used by CORPT00C and COTRN02C | CALL CEEDAYS | *(none)* |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module — IMS/DB2/MQ (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|---------------------|
| 32 | COPAUA0C.cbl | 1,026 | Online (CICS+IMS+MQ) | **Authorization decision engine** — Retrieves auth request from MQ; looks up account/customer/XREF in VSAM; checks IMS pending auth DB; sends approval/decline via MQ response | MQOPEN, MQGET, MQPUT1; EXEC DLI SCHD/GU/TERM; CICS READ (ACCTDAT, CARDXREF, CUSTDAT); CICS RETRIEVE | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | COPAUS0C.cbl | 1,032 | Online (CICS+IMS) | **Pending auth summary browse** — Paginated list of pending authorizations from IMS DB; displays summary (account, status, limits, balances) | EXEC DLI GU/GNP/SCHD/TERM; CICS READ (ACCTDAT, CARDXREF, CUSTDAT); CICS SEND/RECEIVE/SYNCPOINT | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | COPAUS1C.cbl | 604 | Online (CICS+IMS) | **Pending auth detail with update** — Shows auth detail from IMS; allows status update (approve/decline) and REPL back to IMS | EXEC DLI GU/GNP/REPL/SCHD/TERM; CICS LINK (COPAUS2C); CICS SYNCPOINT | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS2C.cbl | 244 | Online (CICS+DB2) | **Fraud flag via DB2** — Marks authorization as fraudulent by inserting record into DB2 AUTHFRDS table | EXEC SQL INSERT/SELECT (AUTHFRDS); CICS ASKTIME/FORMATTIME | CIPAUDTY |
| 36 | CBPAUP0C.cbl | 386 | Batch (IMS) | **Expired auth purge** — Scans IMS pending auth DB; deletes expired authorization segments; checkpoints periodically | EXEC DLI GN/GNP/DLET/CHKP | CIPAUSMY, CIPAUDTY |
| 37 | PAUDBLOD.CBL | 369 | Batch (IMS) | **IMS database load** — Initial load of pending authorization IMS database from flat file | CALL CBLTDLI (ISRT, GU) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 38 | PAUDBUNL.CBL | 317 | Batch (IMS) | **IMS database unload** — Unloads all segments from pending auth IMS DB to flat file | CALL CBLTDLI (GN, GNP) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | DBUNLDGS.CBL | 366 | Batch (IMS+GSAM) | **GSAM unload** — Unloads IMS data via GSAM (Generalized Sequential Access Method) to sequential file | CALL CBLTDLI (GN, GNP, ISRT) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 Transaction Type Module — DB2 (`app/app-transaction-type-db2/cbl/`)

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|---------------------|
| 40 | COTRTLIC.cbl | 2,098 | Online (CICS+DB2) | **Transaction type list** — Cursor-based paginated browse of TRANSACTION_TYPE and TRANSACTION_CATEGORY DB2 tables; supports forward/backward paging | EXEC SQL DECLARE/OPEN/FETCH/CLOSE CURSOR; CICS SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COTRTLI, CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA |
| 41 | COTRTUPC.cbl | 1,702 | Online (CICS+DB2) | **Transaction type maintenance** — CRUD for transaction types/categories via DB2; supports cascading deletes (type + all categories) | EXEC SQL SELECT/INSERT/UPDATE/DELETE; CICS SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COTRTUP, CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA, CVCRD01Y, COADM02Y |
| 42 | COBTUPDT.cbl | 237 | Batch (DB2) | **Batch transaction type update** — Batch update of transaction type records via DB2 SQL | EXEC SQL SELECT/UPDATE/DELETE | CSDB2RWY |

### 2.3 VSAM/MQ Module (`app/app-vsam-mq/cbl/`)

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|---------------------|
| 43 | COACCT01.cbl | 620 | Online (CICS+MQ) | **Account inquiry via MQ** — Receives account inquiry request from MQ; looks up account data in VSAM; returns response via MQ | MQ GET/PUT; CICS READ (ACCTDAT, CARDXREF) | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 44 | CODATE01.cbl | 524 | Online (CICS+MQ) | **Date inquiry via MQ** — Receives date format request from MQ; performs date conversion; returns formatted date via MQ | MQ GET/PUT; CICS ASKTIME/FORMATTIME | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, CODATECN |

---

## 3. Classification Summary

| Classification | Count | Programs |
|---------------|-------|----------|
| **Online (CICS only)** | 17 | COSGN00C, COADM01C, COMEN01C, COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| **Online (CICS+DB2)** | 3 | COTRTLIC, COTRTUPC, COPAUS2C |
| **Online (CICS+IMS)** | 2 | COPAUS0C, COPAUS1C |
| **Online (CICS+IMS+MQ)** | 1 | COPAUA0C |
| **Online (CICS+MQ)** | 2 | COACCT01, CODATE01 |
| **Batch (VSAM)** | 11 | CBACT01C, CBACT02C, CBACT03C, CBACT04C, CBCUS01C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, CBSTM03B, CBEXPORT, CBIMPORT |
| **Batch (IMS)** | 4 | CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS |
| **Batch (DB2)** | 1 | COBTUPDT |
| **Utility** | 3 | COBSWAIT, CSUTLDTC |

---

## 4. BMS Screen Maps (`app/bms/`)

Each BMS map defines a 3270 terminal screen layout paired with its CICS program.

| BMS Map | Paired Program | Screen Purpose |
|---------|---------------|----------------|
| COSGN00.bms | COSGN00C | Sign-on screen |
| COADM01.bms | COADM01C | Admin menu |
| COMEN01.bms | COMEN01C | Main user menu |
| COACTVW.bms | COACTVWC | Account view |
| COACTUP.bms | COACTUPC | Account update |
| COCRDLI.bms | COCRDLIC | Credit card list |
| COCRDSL.bms | COCRDSLC | Credit card view |
| COCRDUP.bms | COCRDUPC | Credit card update |
| COTRN00.bms | COTRN00C | Transaction list |
| COTRN01.bms | COTRN01C | Transaction view |
| COTRN02.bms | COTRN02C | Transaction add |
| CORPT00.bms | CORPT00C | Report request |
| COBIL00.bms | COBIL00C | Bill payment |
| COUSR00.bms | COUSR00C | User list |
| COUSR01.bms | COUSR01C | User add |
| COUSR02.bms | COUSR02C | User update |
| COUSR03.bms | COUSR03C | User delete |

Sub-app BMS maps: `COPAU00.bms`, `COPAU01.bms` (authorization), `COTRTLI.bms`, `COTRTUP.bms` (transaction type DB2).

---

## 5. JCL Job Catalog (`app/jcl/`)

### 5.1 VSAM Cluster Definition Jobs

| JCL Job | Steps | Purpose | Datasets |
|---------|-------|---------|----------|
| ACCTFILE.jcl | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | Define and load ACCTDATA KSDS from PS | ACCTDATA.PS → ACCTDATA.VSAM.KSDS |
| CARDFILE.jcl | CLCIFIL (SDSF CLOSE), STEP05–15 (IDCAMS DEL/DEF/REPRO), STEP40 (IDCAMS AIX) | Define CARDDATA KSDS with alternate index | CARDDATA.PS → CARDDATA.VSAM.KSDS |
| CUSTFILE.jcl | CLCIFIL (SDSF CLOSE), STEP05–15 (IDCAMS DEL/DEF/REPRO), OPCIFIL (SDSF OPEN) | Define CUSTDATA KSDS | CUSTDATA.PS → CUSTDATA.VSAM.KSDS |
| XREFFILE.jcl | STEP05–15 (IDCAMS DEL/DEF/REPRO), STEP20–30 (IDCAMS AIX/PATH) | Define CARDXREF KSDS with alternate indexes | CARDXREF.PS → CARDXREF.VSAM.KSDS + AIX |
| TRANFILE.jcl | CLCIFIL (SDSF CLOSE), STEP05–15 (IDCAMS DEL/DEF/REPRO), STEP20 (IDCAMS AIX) | Define TRANSACT KSDS with AIX on processed timestamp | DALYTRAN.PS.INIT → TRANSACT.VSAM.KSDS |
| DISCGRP.jcl | STEP05–15 (IDCAMS DEL/DEF/REPRO) | Define disclosure/interest rate group KSDS | DISCGRP.PS → DISCGRP.VSAM.KSDS |
| TCATBALF.jcl | STEP05–15 (IDCAMS DEL/DEF/REPRO) | Define transaction category balance KSDS | TCATBALF.PS → TCATBALF.VSAM.KSDS |
| TRANTYPE.jcl | STEP05–15 (IDCAMS DEL/DEF/REPRO) | Define transaction type KSDS | TRANTYPE.PS → TRANTYPE.VSAM.KSDS |
| TRANCATG.jcl | STEP05–15 (IDCAMS DEL/DEF/REPRO) | Define transaction category KSDS | TRANCATG.PS → TRANCATG.VSAM.KSDS |
| DALYREJS.jcl | STEP05 (IDCAMS DEFINE) | Define daily rejects GDG cluster | DALYREJS GDG |
| REPTFILE.jcl | STEP05 (IDCAMS DEFINE) | Define report output file | Report output |
| DEFCUST.jcl | STEP05 (IDCAMS ×2) | Define/reload customer VSAM cluster | CUSTDATA VSAM |
| ESDSRRDS.jcl | PREDEL, STEP01–04 | Define ESDS/RRDS demo clusters | ESDSRRDS test data |
| TRANIDX.jcl | STEP20–30 (IDCAMS) | Create/build alternate index on TRANSACT | TRANSACT AIX |

### 5.2 Batch Processing Jobs

| JCL Job | Program Executed | Steps | Purpose | Input Datasets | Output Datasets |
|---------|-----------------|-------|---------|----------------|-----------------|
| POSTTRAN.jcl | CBTRN02C | STEP15 | Post daily transactions | DALYTRAN.PS, CARDXREF.VSAM, ACCTDATA.VSAM, TCATBALF.VSAM | TRANSACT.VSAM, DALYREJS(+1) |
| INTCALC.jcl | CBACT04C | STEP15 (PARM='2022071800') | Calculate interest and fees | TCATBALF.VSAM, CARDXREF.VSAM, DISCGRP.VSAM, ACCTDATA.VSAM | SYSTRAN(+1) |
| CREASTMT.JCL | CBSTM03A (via SORT+load) | DELDEF01, STEP010 (SORT), STEP020 (REPRO), STEP030+ | Generate customer statements | TRANSACT.VSAM → TRXFL.SEQ → TRXFL.VSAM | STATEMNT.HTML, STATEMNT.PS |
| TRANREPT.jcl | CBTRN03C (via SORT) | STEP05R (REPRO), STEP05R (SORT w/ date filter) | Generate daily transaction report | TRANSACT.VSAM → sorted/filtered file | Report output |
| READACCT.jcl | CBACT01C | PREDEL, STEP05 | Read account file, write formatted output | ACCTDATA.VSAM.KSDS | ACCTDATA.PSCOMP, ACCTDATA.ARRYPS, ACCTDATA.VBPS |
| READCARD.jcl | CBACT02C | STEP05 | Read and print card data | CARDDATA.VSAM.KSDS | SYSOUT |
| READCUST.jcl | CBCUS01C | STEP05 | Read and print customer data | CUSTDATA.VSAM.KSDS | SYSOUT |
| READXREF.jcl | CBACT03C | STEP05 | Read and print cross-reference data | CARDXREF.VSAM.KSDS | SYSOUT |
| CBEXPORT.jcl | CBEXPORT | STEP01 (IDCAMS), STEP02 (PGM) | Export all VSAM data to sequential file | All 5 VSAM KSDS files | EXPORT.DATA |
| CBIMPORT.jcl | CBIMPORT | STEP01 | Import from sequential file to individual datasets | EXPORT.DATA | CUSTDATA/ACCTDATA/CARDXREF/TRANSACT.IMPORT, IMPORT.ERRORS |
| PRTCATBL.jcl | REPROC+SORT | DELDEF, STEP05R (REPRO), STEP10R (SORT) | Print category balance report | TCATBALF.VSAM.KSDS | TCATBALF.REPT |
| WAITSTEP.jcl | COBSWAIT | WAIT | Execute timed wait | *(none)* | *(none)* |

### 5.3 Utility & Infrastructure Jobs

| JCL Job | Purpose |
|---------|---------|
| DEFGDGB.jcl | Define GDG base entries (for versioned backup datasets) |
| DEFGDGD.jcl | Define GDG + backup TRANTYPE and TRANCATG via IEBGENER |
| COMBTRAN.jcl | Sort and merge backed-up + system-generated transactions into TRANSACT.COMBINED |
| TRANBKP.jcl | Backup transaction VSAM file via REPRO; delete/redefine cluster |
| OPENFIL.jcl | Open CICS files via SDSF |
| CLOSEFIL.jcl | Close CICS files via SDSF |
| CBADMCDJ.jcl | Define CICS CSD entries (transaction definitions) via DFHCSDUP |
| DUSRSECJ.jcl | Create and load user security file (USRSEC) with default users |
| INTRDRJ1.JCL | Internal reader job 1 — REPRO backup + submit INTRDRJ2 via INTRDR |
| INTRDRJ2.JCL | Internal reader job 2 — Backup of backup via IDCAMS REPRO |
| FTPJCL.JCL | FTP transfer job |
| TXT2PDF1.JCL | Convert text statement to PDF via TXT2PDF utility |

### 5.4 Sub-Application JCL

**Authorization (IMS/DB2/MQ):**

| JCL Job | Purpose |
|---------|---------|
| LOADPADB.JCL | Load pending authorization IMS database (PAUDBLOD) |
| UNLDPADB.JCL | Unload pending authorization IMS database (PAUDBUNL) |
| UNLDGSAM.JCL | Unload via GSAM (DBUNLDGS) |
| CBPAUP0J.jcl | Purge expired authorizations (CBPAUP0C) |
| DBPAUTP0.jcl | IMS database utility processing |

**Transaction Type (DB2):**

| JCL Job | Purpose |
|---------|---------|
| CREADB21.jcl | Create DB2 tables (TRNTYPE, TRNTYCAT) and indexes |
| MNTTRDB2.jcl | Maintain transaction type DB2 data (COBTUPDT) |
| TRANEXTR.jcl | Extract transaction type data from DB2 |
