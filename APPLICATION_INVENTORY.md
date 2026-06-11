# CardDemo — Application Inventory

> **Estate:** 44 COBOL programs · 47 copybooks · 46 JCL jobs · 16 BMS maps · 2 assembler modules
> **Total LOC:** 30,175 (COBOL only) · **Languages:** COBOL, JCL, BMS, Assembler

---

## 1. Program Catalog — `app/cbl/` (27 programs, 20,650 LOC)

### 1.1 Online Programs (CICS)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 1 | COSGN00C.cbl | 260 | **Sign-on screen.** Authenticates users via USRSEC VSAM file; routes admins to COADM01C, regular users to COMEN01C. | Online (CICS) | CICS READ (USRSEC), CICS SEND/RECEIVE MAP, CICS XCTL | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | COADM01C.cbl | 288 | **Admin menu hub.** Displays admin function menu (user CRUD, DB2 transaction types); transfers control to selected admin program. | Online (CICS) | CICS SEND/RECEIVE MAP, CICS XCTL | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COMEN01C.cbl | 308 | **Main menu hub.** Central navigation point for all user functions (11 XCTL targets); routes to account, card, transaction, report, billing, and authorization screens. | Online (CICS) | CICS SEND/RECEIVE MAP, CICS XCTL, CICS INQUIRE | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTUPC.cbl | 4,236 | **Account update screen.** Full-screen editor for account and customer data with exhaustive field validation (date, SSN, phone, state, ZIP). Uses COPY REPLACING pattern (39× CSSETATY). Largest program in estate. | Online (CICS) | CICS READ/REWRITE (ACCTFILE, CUSTFILE, CARDXREF), CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×39), CSSTRPFY, CSUTLDPY |
| 5 | COACTVWC.cbl | 941 | **Account view screen.** Read-only display of account details, card info, and customer data with HTML text output capability. | Online (CICS) | CICS READ (CARDFILE, ACCTFILE, CUSTFILE), CICS SEND MAP/SEND TEXT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 6 | COCRDLIC.cbl | 1,459 | **Card list screen.** Paginated browse of credit cards using CICS STARTBR/READNEXT/READPREV/ENDBR pattern. Routes to card detail or update. | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR (CARDFILE), CICS SEND/RECEIVE MAP, CICS XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | COCRDSLC.cbl | 887 | **Card detail view.** Displays single card record with associated account and customer data. | Online (CICS) | CICS READ (CARDFILE, ACCTFILE), CICS SEND MAP/SEND TEXT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | COCRDUPC.cbl | 1,560 | **Card update screen.** Edits card record fields (status, expiry) with validation; rewrites to VSAM. | Online (CICS) | CICS READ/REWRITE (CARDFILE), CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | COTRN00C.cbl | 699 | **Transaction list screen.** Paginated browse of transactions with STARTBR/READNEXT/READPREV pattern. | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR (TRANSACT), CICS SEND/RECEIVE MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | 330 | **Transaction detail view.** Displays single transaction record. | Online (CICS) | CICS READ (TRANSACT), CICS SEND/RECEIVE MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | 783 | **Transaction add screen.** Creates new transaction records with date validation (calls CSUTLDTC). Writes to TRANSACT VSAM. | Online (CICS) | CICS READ (ACCTFILE, CARDXREF), CICS WRITE (TRANSACT), CICS STARTBR/READPREV/ENDBR, CICS SEND/RECEIVE MAP | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | CORPT00C.cbl | 649 | **Report request screen.** Accepts date-range parameters, validates dates via CSUTLDTC, then submits batch JCL (INTRDRJ1/J2) through CICS WRITEQ TD to the internal reader. Bridge between online and batch. | Online (CICS) | CICS WRITEQ TD (JOBS queue), CICS SEND/RECEIVE MAP, CALL CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | COBIL00C.cbl | 572 | **Bill payment screen.** Processes bill payments by reading account/transaction data, creating a payment transaction, and updating the account balance. | Online (CICS) | CICS READ/REWRITE (ACCTFILE), CICS WRITE (TRANSACT), CICS STARTBR/READPREV/ENDBR (TRANSACT), CICS ASKTIME/FORMATTIME | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | 695 | **User list screen.** Paginated browse of USRSEC security file with STARTBR/READNEXT/READPREV. | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | 299 | **User add screen.** Creates new user record in USRSEC VSAM file. | Online (CICS) | CICS WRITE (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | 414 | **User update screen.** Modifies existing user record (password, type) in USRSEC. | Online (CICS) | CICS READ/REWRITE (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | 359 | **User delete screen.** Removes user record from USRSEC with PF5 confirmation. | Online (CICS) | CICS READ/DELETE (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 1.2 Batch Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 18 | CBACT01C.cbl | 430 | **Account file reader.** Reads ACCTFILE VSAM and writes to sequential output files (flat, array, VB formats). Calls assembler COBDATFT for date formatting. | Batch | READ ACCTFILE, WRITE OUT-FILE/ARRY-FILE/VBRC-FILE, CALL COBDATFT, CALL CEE3ABD | CVACT01Y, CODATECN |
| 19 | CBACT02C.cbl | 178 | **Card file reader.** Reads and prints CARDFILE VSAM records. | Batch | READ CARDFILE, CALL CEE3ABD | CVACT02Y |
| 20 | CBACT03C.cbl | 178 | **Cross-reference reader.** Reads and prints XREFFILE VSAM records. | Batch | READ XREFFILE, CALL CEE3ABD | CVACT03Y |
| 21 | CBACT04C.cbl | 652 | **Interest calculation.** Calculates interest on account balances using transaction category balances, discount groups, and cross-references. Updates account records. | Batch | READ TCATBAL/XREF/DISCGRP, READ/REWRITE ACCOUNT, WRITE TRANSACT, CALL CEE3ABD | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 22 | CBCUS01C.cbl | 178 | **Customer file reader.** Reads and prints CUSTFILE VSAM records. | Batch | READ CUSTFILE, CALL CEE3ABD | CVCUS01Y |
| 23 | CBSTM03A.CBL | 924 | **Statement generation (main).** Orchestrates customer statement creation. Reads transactions, cross-references, customers, and accounts via CBSTM03B submodule. Generates text statements and HTML output files. Highest I/O count in estate (~115 WRITE operations). | Batch | OPEN/CLOSE STMT-FILE/HTML-FILE, WRITE (97× statement + HTML lines), CALL CBSTM03B (13×), CALL CEE3ABD | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 24 | CBSTM03B.CBL | 230 | **Statement generation (I/O submodule).** Called by CBSTM03A. Handles OPEN/READ/CLOSE for transaction, cross-reference, customer, and account files. | Batch (submodule) | OPEN/READ/CLOSE TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE | *(none — uses LINKAGE SECTION)* |
| 25 | CBTRN01C.cbl | 494 | **Transaction validation.** Reads daily transactions and validates against cross-reference, account, card, and customer files. | Batch | READ DALYTRAN/XREF/ACCOUNT, OPEN CUSTOMER/CARD/TRANSACT, CALL CEE3ABD | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 26 | CBTRN02C.cbl | 731 | **Transaction posting.** Posts daily transactions to master transaction file. Validates against cross-reference, updates account balances and category balances. Writes rejected records to DALYREJS. | Batch | READ DALYTRAN/XREF/ACCOUNT/TCATBAL, WRITE TRANSACT/DALYREJS, REWRITE ACCOUNT/TCATBAL, CALL CEE3ABD | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 27 | CBTRN03C.cbl | 649 | **Transaction report.** Generates daily transaction report with transaction type and category lookups. Reads date parameters for filtering. | Batch | READ TRANSACT/XREF/TRANTYPE/TRANCATG/DATE-PARMS, WRITE REPORT-FILE, CALL CEE3ABD | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 28 | CBEXPORT.cbl | 582 | **Data export utility.** Reads all 5 core VSAM files (customer, account, xref, transaction, card) and writes combined export records. | Batch | READ CUSTOMER/ACCOUNT/XREF/TRANSACTION/CARD, WRITE EXPORT-OUTPUT, CALL CEE3ABD | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 29 | CBIMPORT.cbl | 487 | **Data import utility.** Reads export file, validates record types, and distributes records to appropriate VSAM output files. Writes errors to ERROR-OUTPUT. | Batch | READ EXPORT-INPUT, WRITE CUSTOMER/ACCOUNT/XREF/TRANSACTION/CARD/ERROR, CALL CEE3ABD | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 30 | COBSWAIT.cbl | 12 | **Wait utility.** Calls assembler MVSWAIT routine for timer control in batch jobs. | Batch (utility) | CALL MVSWAIT | *(none)* |
| 31 | CSUTLDTC.cbl | 38 | **Date validation utility.** Calls LE CEEDAYS for date conversion and validation. Used by COTRN02C and CORPT00C. | Batch (utility) | CALL CEEDAYS | *(none)* |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module — `app/app-authorization-ims-db2-mq/cbl/` (8 programs, 4,997 LOC)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 32 | COPAUA0C.cbl | 1,026 | **Authorization decision engine.** Processes credit card authorization requests from MQ, looks up customer/account data via CICS VSAM reads, evaluates authorization rules, sends reply to MQ. Spans CICS+MQ+VSAM. | Online (CICS/MQ) | MQOPEN/MQGET/MQPUT1/MQCLOSE, CICS READ (XREFFILE, ACCTFILE, CUSTFILE), CICS WRITEQ, CICS RETRIEVE | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | COPAUS0C.cbl | 1,032 | **Auth summary browse.** Displays paginated pending authorization summaries. Reads VSAM + IMS data. | Online (CICS/IMS) | CICS READ (CARDFILE, ACCTFILE, CUSTFILE), CICS SEND/RECEIVE MAP, CICS SYNCPOINT | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | COPAUS1C.cbl | 604 | **Auth detail view/update.** Shows authorization detail records with ability to mark fraud status. Uses CICS LINK to call COPAUS2C. | Online (CICS/IMS) | CICS LINK (COPAUS2C), CICS SEND/RECEIVE MAP, CICS SYNCPOINT | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS2C.cbl | 244 | **Fraud flag writer.** LINKed by COPAUS1C. Inserts fraud flag record into DB2 AUTHFRDS table. | Online (CICS/DB2) | EXEC SQL INSERT/SELECT (AUTHFRDS), CICS ASKTIME/FORMATTIME | CIPAUDTY |
| 36 | CBPAUP0C.cbl | 386 | **Expired auth purge.** Batch IMS program that deletes expired pending authorization summaries and their detail segments. | Batch (IMS) | IMS DL/I GN/GNP/DLET via CBLTDLI | CIPAUSMY, CIPAUDTY |
| 37 | PAUDBLOD.CBL | 369 | **IMS database load.** Loads pending authorization data from sequential files into IMS database segments (ISRT, GU). | Batch (IMS) | READ INFILE1/INFILE2, IMS DL/I ISRT/GU via CBLTDLI | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 38 | PAUDBUNL.CBL | 317 | **IMS database unload.** Unloads IMS database segments to sequential output files. | Batch (IMS) | IMS DL/I GN/GNP via CBLTDLI, WRITE OPFILE1/OPFILE2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 39 | DBUNLDGS.CBL | 366 | **GSAM unload utility.** Unloads IMS data and re-inserts into GSAM (Generalized Sequential Access Method) output. | Batch (IMS/GSAM) | IMS DL/I GN/GNP/ISRT via CBLTDLI | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 Transaction Type Module — `app/app-transaction-type-db2/cbl/` (3 programs, 4,037 LOC)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 40 | COTRTLIC.cbl | 2,098 | **Transaction type list (DB2).** Cursor-based paginated browse of TRANSACTION_TYPE DB2 table with select/update/delete actions. Forward and backward cursor navigation. | Online (CICS/DB2) | EXEC SQL OPEN/FETCH/CLOSE CURSOR, EXEC SQL DELETE, CICS SYNCPOINT, CICS SEND/RECEIVE MAP, CICS XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, SQLCA, DCLTRTYP |
| 41 | COTRTUPC.cbl | 1,702 | **Transaction type update (DB2).** Full CRUD for individual transaction type records. Cascading delete with child record check. Uses COPY REPLACING for CSSETATY. | Online (CICS/DB2) | EXEC SQL SELECT/INSERT/UPDATE/DELETE (TRANSACTION_TYPE, TRANSACTION_CATEGORY), CICS SYNCPOINT, CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT |
| 42 | COBTUPDT.cbl | 237 | **Transaction type batch update.** Reads sequential input file and performs batch INSERT/UPDATE/DELETE on DB2 TRANSACTION_TYPE table. | Batch (DB2) | READ TR-RECORD, EXEC SQL INSERT/UPDATE/DELETE, EXEC SQL INCLUDE DCLTRTYP | DCLTRTYP |

### 2.3 VSAM-MQ Module — `app/app-vsam-mq/cbl/` (2 programs, 1,144 LOC)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | 620 | **Account inquiry via MQ.** CICS-triggered program that receives account inquiry requests from MQ, reads ACCTFILE, and sends response/error to MQ reply/error queues. | Online (CICS/MQ) | MQOPEN/MQGET/MQPUT/MQCLOSE (3 queues), CICS READ (ACCTFILE), CICS RETRIEVE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 44 | CODATE01.cbl | 524 | **Date inquiry via MQ.** CICS-triggered program that receives date inquiry requests from MQ and returns system date/time via MQ. | Online (CICS/MQ) | MQOPEN/MQGET/MQPUT/MQCLOSE (3 queues), CICS ASKTIME/FORMATTIME, CICS RETRIEVE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. BMS Screen Maps — `app/bms/` (16 maps) + Sub-apps (4 maps)

Each BMS map defines a CICS 3270 terminal screen layout paired 1:1 with an online COBOL program.

| BMS Map | Paired Program | Screen Purpose |
|---------|---------------|----------------|
| COSGN00.bms | COSGN00C | Sign-on screen |
| COADM01.bms | COADM01C | Admin menu |
| COMEN01.bms | COMEN01C | Main user menu |
| COACTUP.bms | COACTUPC | Account update form |
| COACTVW.bms | COACTVWC | Account view display |
| COCRDLI.bms | COCRDLIC | Card list browse |
| COCRDSL.bms | COCRDSLC | Card detail view |
| COCRDUP.bms | COCRDUPC | Card update form |
| COTRN00.bms | COTRN00C | Transaction list browse |
| COTRN01.bms | COTRN01C | Transaction detail view |
| COTRN02.bms | COTRN02C | Transaction add form |
| CORPT00.bms | CORPT00C | Report request form |
| COBIL00.bms | COBIL00C | Bill payment form |
| COUSR00.bms | COUSR00C | User list browse |
| COUSR01.bms | COUSR01C | User add form |
| COUSR02.bms | COUSR02C | User update form |
| COUSR03.bms | COUSR03C | User delete confirmation |
| COPAU00.bms | COPAUS0C | Auth summary browse (sub-app) |
| COPAU01.bms | COPAUS1C | Auth detail view (sub-app) |
| COTRTLI.bms | COTRTLIC | Transaction type list (sub-app) |
| COTRTUP.bms | COTRTUPC | Transaction type update (sub-app) |

---

## 4. Assembler Modules — `app/asm/` (2 modules)

| Filename | Purpose | Called By |
|----------|---------|----------|
| COBDATFT.asm | Date format conversion utility (YYYYMMDD ↔ YYYY-MM-DD) | CBACT01C |
| MVSWAIT.asm | Timer/wait control for batch job pacing | COBSWAIT |

---

## 5. JCL Job Catalog — `app/jcl/` (38 jobs) + Sub-apps (8 jobs)

### 5.1 Data Setup Jobs (VSAM Cluster Definition)

These jobs use IDCAMS to define, delete, and load VSAM KSDS clusters from sequential (PS) files.

| JCL Job | Steps | Purpose | Datasets Involved |
|---------|-------|---------|-------------------|
| ACCTFILE.jcl | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | Define and load Account VSAM cluster | ACCTDATA.PS → ACCTFILE VSAM |
| CARDFILE.jcl | CLCIFIL (SDSF CLOSE), STEP05–10 (IDCAMS DEL/DEF), STEP15 (REPRO), STEP40–50 (AIX DEF) | Define Card VSAM cluster with alternate index | CARDDATA.PS → CARDFILE VSAM + AIX |
| CUSTFILE.jcl | CLCIFIL (CLOSE), STEP05–10 (IDCAMS DEL/DEF), STEP15 (REPRO), OPCIFIL (OPEN) | Define and load Customer VSAM cluster | CUSTDATA.PS → CUSTFILE VSAM |
| XREFFILE.jcl | STEP05–10 (IDCAMS DEL/DEF), STEP15 (REPRO), STEP20–30 (AIX DEF/PATH) | Define Card-Account cross-reference with alternate indexes | XREFDATA.PS → XREFFILE VSAM + AIX |
| TRANFILE.jcl | CLCIFIL (CLOSE), STEP05–10 (DEL/DEF), STEP15 (REPRO), STEP20–25 (AIX DEF) | Define Transaction VSAM cluster with alternate index | TRANSACT.PS → TRANSACT VSAM + AIX |
| TRANIDX.jcl | STEP20–30 (IDCAMS DEF) | Define additional alternate indexes for TRANSACT | TRANSACT VSAM AIX paths |
| DISCGRP.jcl | STEP05–10 (DEL/DEF), STEP15 (REPRO) | Define Discount Group reference data cluster | DISCGRP.PS → DISCGRP VSAM |
| TCATBALF.jcl | STEP05–10 (DEL/DEF), STEP15 (REPRO) | Define Transaction Category Balance cluster | TCATBAL.PS → TCATBAL VSAM |
| TRANTYPE.jcl | STEP05–10 (DEL/DEF), STEP15 (REPRO) | Define Transaction Type reference data cluster | TRANTYPE.PS → TRANTYPE VSAM |
| TRANCATG.jcl | STEP05–10 (DEL/DEF), STEP15 (REPRO) | Define Transaction Category reference data cluster | TRANCATG.PS → TRANCATG VSAM |
| DALYREJS.jcl | STEP05 (IDCAMS DELETE) | Delete daily rejects sequential file | DALYREJS.PS |
| REPTFILE.jcl | STEP05 (IDCAMS DELETE) | Delete report output file | REPTFILE.PS |
| DEFCUST.jcl | STEP05 (×2, DEL/DEF) | Alternate customer dataset definition | CUSTFILE VSAM |
| DEFGDGB.jcl | STEP05 (IDCAMS DEF GDG BASE) | Define Generation Data Group base for backups | GDG base entry |
| DEFGDGD.jcl | STEP10–60 (IDCAMS/IEBGENER) | GDG data backup: copies TRANTYPE.PS and TRANCATG.PS to GDG generations | TRANTYPE/TRANCATG → GDG(+1) |
| DUSRSECJ.jcl | PREDEL (IEFBR14), STEP01 (IEBGENER), STEP02–03 (IDCAMS DEL/DEF/REPRO) | Load user security data | USRSEC.PS → USRSEC VSAM |
| ESDSRRDS.jcl | PREDEL, STEP01–05 (IEBGENER/IDCAMS) | Define and load ESDS/RRDS VSAM demo clusters | ESDSRRDS.PS → ESDS/RRDS VSAM |

### 5.2 Batch Processing Jobs

| JCL Job | Steps | Program Executed | Purpose | Input Datasets | Output Datasets |
|---------|-------|-----------------|---------|----------------|-----------------|
| POSTTRAN.jcl | STEP15 | CBTRN02C | Post daily transactions | DALYTRAN, XREFFILE, ACCTFILE, TCATBALF | TRANSACT (output), DALYREJS |
| INTCALC.jcl | STEP15 | CBACT04C | Calculate interest on accounts | TCATBALF, XREFFILE, ACCTFILE, DISCGRP | TRANSACT (output), ACCTFILE (I-O) |
| CREASTMT.JCL | DELDEF01 (IDCAMS), STEP010 (SORT), STEP020 (IDCAMS REPRO), STEP030 (IEFBR14 cleanup), STEP040 | CBSTM03A | Generate customer statements | TRANSACT VSAM (sorted), XREFFILE, CUSTFILE, ACCTFILE | STMT-FILE, HTML-FILE |
| TRANREPT.jcl | STEP05R (REPROC), STEP05R (SORT), STEP10R | CBTRN03C | Daily transaction report | TRANSACT (sorted), CARDXREF, TRANTYPE, TRANCATG, DATEPARM | TRANREPT |
| READACCT.jcl | PREDEL (IEFBR14), STEP05 | CBACT01C | Read/dump account file | ACCTFILE VSAM | ACCTDATA.PSCOMP, ARRYPS, VBPS |
| READCARD.jcl | STEP05 | CBACT02C | Read/dump card file | CARDFILE VSAM | SYSOUT |
| READCUST.jcl | STEP05 | CBCUS01C | Read/dump customer file | CUSTFILE VSAM | SYSOUT |
| READXREF.jcl | STEP05 | CBACT03C | Read/dump cross-reference file | XREFFILE VSAM | SYSOUT |
| CBEXPORT.jcl | STEP01 (IDCAMS DEL), STEP02 | CBEXPORT | Export all core data | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | EXPFILE |
| CBIMPORT.jcl | STEP01 | CBIMPORT | Import from export file | EXPFILE | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT |
| WAITSTEP.jcl | WAIT | COBSWAIT | Timer wait step for batch orchestration | *(none)* | *(none)* |
| COMBTRAN.jcl | STEP05R (SORT), STEP10 (IDCAMS REPRO) | SORT + IDCAMS | Combine/merge transaction files | Multiple TRANSACT sources | TRANSACT VSAM |
| TRANBKP.jcl | STEP05R (REPROC), STEP05–10 (IDCAMS) | REPROC + IDCAMS | Backup transaction VSAM to sequential | TRANSACT VSAM | Backup PS |
| PRTCATBL.jcl | DELDEF (IEFBR14), STEP05R (REPROC), STEP10R (SORT) | SORT | Print/sort category balance data | TCATBALF VSAM | Sorted output |
| CLOSEFIL.jcl | CLCIFIL (SDSF) | SDSF | Close CICS files for batch processing | *(CICS files)* | *(none)* |
| OPENFIL.jcl | OPCIFIL (SDSF) | SDSF | Open CICS files after batch processing | *(CICS files)* | *(none)* |
| CBADMCDJ.jcl | STEP1 (DFHCSDUP) | DFHCSDUP | Load CSD entries for CICS transactions | CSD definitions | DFHCSD |

### 5.3 Utility Jobs

| JCL Job | Steps | Purpose |
|---------|-------|---------|
| FTPJCL.JCL | STEP1 (FTP) | FTP file transfer utility |
| TXT2PDF1.JCL | TXT2PDF (IKJEFT1B) | Convert statement text file to PDF |
| INTRDRJ1.JCL | IDCAMS + STEP01 (IEBGENER→Internal Reader) | Submit INTRDRJ2 via internal reader |
| INTRDRJ2.JCL | IDCAMS | Secondary job submitted by INTRDRJ1 |

### 5.4 Sub-Application JCL

| JCL Job | Directory | Steps | Program | Purpose |
|---------|-----------|-------|---------|---------|
| CBPAUP0J.jcl | app-authorization-ims-db2-mq | STEP01 | CBPAUP0C | Purge expired IMS authorizations |
| DBPAUTP0.jcl | app-authorization-ims-db2-mq | — | IDCAMS | Define IMS dataset |
| LOADPADB.JCL | app-authorization-ims-db2-mq | — | PAUDBLOD | Load IMS pending auth database |
| UNLDPADB.JCL | app-authorization-ims-db2-mq | — | PAUDBUNL | Unload IMS pending auth database |
| UNLDGSAM.JCL | app-authorization-ims-db2-mq | — | DBUNLDGS | GSAM unload utility |
| CREADB21.jcl | app-transaction-type-db2 | — | DB2 DDL | Create DB2 tables and indexes |
| MNTTRDB2.jcl | app-transaction-type-db2 | — | COBTUPDT | Batch maintain transaction types in DB2 |
| TRANEXTR.jcl | app-transaction-type-db2 | — | SORT/REPRO | Extract transactions for DB2 load |

---

## 6. Summary Statistics

| Category | Count |
|----------|-------|
| **Total COBOL Programs** | 44 |
| Online (CICS) programs | 21 (48%) |
| Pure Batch programs | 16 (36%) |
| Sub-app (IMS/DB2/MQ) programs | 7 (16%) |
| **Total Lines of Code** | 30,175 |
| Average LOC/program | 686 |
| Largest program | COACTUPC.cbl (4,236 LOC) |
| **Copybooks** | 47 (27 main + 20 sub-app) |
| **JCL Jobs** | 46 (38 main + 8 sub-app) |
| **BMS Maps** | 20 (16 main + 4 sub-app) |
| **Assembler Modules** | 2 |
