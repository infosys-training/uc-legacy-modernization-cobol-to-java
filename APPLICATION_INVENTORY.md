# APPLICATION INVENTORY — CardDemo COBOL Estate

> Auto-generated analysis of the CardDemo mainframe credit card management application.
> **Estate Size:** 44 COBOL programs | 47 copybooks | 46 JCL jobs | 16 BMS maps | ~27,350 LOC

---

## Table of Contents

- [1. Main Programs (app/cbl/)](#1-main-programs-appcbl)
- [2. Sub-Application: Authorization IMS/DB2/MQ](#2-sub-application-authorization-imsdb2mq)
- [3. Sub-Application: Transaction Type DB2](#3-sub-application-transaction-type-db2)
- [4. Sub-Application: VSAM/MQ](#4-sub-application-vsammq)
- [5. JCL Job Catalog (app/jcl/)](#5-jcl-job-catalog-appjcl)
- [6. Sub-Application JCL Jobs](#6-sub-application-jcl-jobs)

---

## 1. Main Programs (app/cbl/)

### Online (CICS) Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|--------------------|
| 1 | **COSGN00C.cbl** | 260 | **Sign-On Screen** — Entry point for the application. Validates user credentials against USRSEC VSAM file. Routes admins to COADM01C, regular users to COMEN01C. | Online (CICS) | CICS READ: USRSEC (user security file) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | **COADM01C.cbl** | 288 | **Admin Menu** — Hub screen for administrative functions. Presents 6 options (user CRUD, transaction type management). Uses XCTL to route to selected admin program. | Online (CICS) | None (menu routing only) | COADM01, COADM02Y, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | **COMEN01C.cbl** | 308 | **Main Menu** — Central hub for regular users. Presents 11 options (account, card, transaction, reports, billing, auth). Uses XCTL to route to each function. | Online (CICS) | None (menu routing only) | COCOM01Y, COMEN01, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | **COACTUPC.cbl** | 4,236 | **Account Update** — Largest program in estate. Full account editing with exhaustive field validation (date, SSN, phone, state code, ZIP). Reads/writes 3 VSAM files. Uses COPY REPLACING (CSSETATY) for BMS attribute macros. | Online (CICS) | CICS READ/REWRITE: ACCTDAT, CARDXREF, CUSTDAT; CICS STARTBR/READNEXT/READPREV/ENDBR on CARDXREF | COACTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSSTRPFY, CSUSR01Y, CSUTLDPY, CSUTLDWY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 5 | **COACTVWC.cbl** | 941 | **Account View** — Read-only display of account details including linked cards and customer information. Paginated card browse via STARTBR/READNEXT. | Online (CICS) | CICS READ: ACCTDAT, CARDXREF, CUSTDAT, CARDDAT; CICS STARTBR/READNEXT/ENDBR on card file | COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 6 | **COCRDLIC.cbl** | 1,459 | **Credit Card List** — Paginated browse of credit cards. Uses STARTBR/READNEXT/READPREV/ENDBR pattern for forward/backward scrolling. Supports selection for view or update. | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR: CARDDAT (via AIX) | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 7 | **COCRDSLC.cbl** | 887 | **Credit Card View** — Read-only display of selected credit card details with linked customer and account information. | Online (CICS) | CICS READ: CARDDAT, CARDXREF, CUSTDAT | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 8 | **COCRDUPC.cbl** | 1,560 | **Credit Card Update** — Edit credit card details (embossed name, expiry, status). Validates card data and rewrites CARDDAT VSAM. | Online (CICS) | CICS READ/REWRITE: CARDDAT; CICS READ: CARDXREF, CUSTDAT | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 9 | **COTRN00C.cbl** | 699 | **Transaction List** — Paginated browse of transactions. Supports forward/backward scrolling with date/card-number filtering. | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR: TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | **COTRN01C.cbl** | 330 | **Transaction View** — Read-only detail display of a selected transaction record. | Online (CICS) | CICS READ: TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | **COTRN02C.cbl** | 783 | **Transaction Add** — Add new daily transactions. Validates card number via XREF lookup, checks account status, writes to DALYTRAN and updates TCATBALF. Calls CSUTLDTC for date validation. | Online (CICS) | CICS READ: CARDXREF, ACCTDAT; CICS WRITE: DALYTRAN; CICS READ/REWRITE: TCATBALF | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 12 | **CORPT00C.cbl** | 649 | **Report Request** — Online form to request batch report generation. Submits JCL (INTRDRJ1/INTRDRJ2) via CICS internal reader. Calls CSUTLDTC for date validation. | Online (CICS) | CICS WRITEQ TD (transient data queue for JCL submission) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | **COBIL00C.cbl** | 572 | **Bill Payment** — Process bill payments against an account. Reads account/XREF, validates, writes transaction, and updates account balance. | Online (CICS) | CICS READ/REWRITE: ACCTDAT; CICS READ: CARDXREF; CICS WRITE: TRANSACT | COBIL00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | **COUSR00C.cbl** | 695 | **User List** — Paginated browse of user security records. Supports selection for update or delete. | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR: USRSEC | COCOM01Y, COTTL01Y, COUSR00, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | **COUSR01C.cbl** | 299 | **User Add** — Add new user security records (user ID, password, user type). | Online (CICS) | CICS WRITE: USRSEC | COCOM01Y, COTTL01Y, COUSR01, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | **COUSR02C.cbl** | 414 | **User Update** — Update existing user security records (password, type). | Online (CICS) | CICS READ/REWRITE: USRSEC | COCOM01Y, COTTL01Y, COUSR02, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | **COUSR03C.cbl** | 359 | **User Delete** — Delete user security records with confirmation. | Online (CICS) | CICS READ/DELETE: USRSEC | COCOM01Y, COTTL01Y, COUSR03, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### Batch Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|--------------------|
| 18 | **CBACT01C.cbl** | 430 | **Account File Reader** — Reads ACCTDATA VSAM KSDS and writes account records to sequential output files (flat, array, and variable-length formats). Calls assembler COBDATFT for date formatting. | Batch | READ: ACCTFILE (VSAM KSDS); WRITE: OUTFILE, ARRYFILE, VBRCFILE | CODATECN, CVACT01Y |
| 19 | **CBACT02C.cbl** | 178 | **Card File Reader** — Reads CARDDATA VSAM and displays card records. Diagnostic/dump utility. | Batch | READ: CARDFILE (VSAM KSDS) | CVACT02Y |
| 20 | **CBACT03C.cbl** | 178 | **Cross-Reference File Reader** — Reads CARDXREF VSAM and displays XREF records. Diagnostic/dump utility. | Batch | READ: XREFFILE (VSAM KSDS) | CVACT03Y |
| 21 | **CBACT04C.cbl** | 652 | **Interest Calculation** — Calculates interest on account balances using disclosure group rates. Reads TCATBALF for category balances, DISCGRP for interest rates, and updates ACCTDATA. Generates system transactions. | Batch | READ: TCATBALF, XREFFILE (KSDS+AIX), DISCGRP, ACCTFILE; WRITE: TRANSACT (system-generated txns); REWRITE: ACCTFILE | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y |
| 22 | **CBCUS01C.cbl** | 178 | **Customer File Reader** — Reads CUSTDATA VSAM and displays customer records. Diagnostic/dump utility. | Batch | READ: CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 23 | **CBSTM03A.CBL** | 924 | **Statement Generation (Main)** — Generates customer account statements in both text and HTML formats. Reads transaction, XREF, customer, and account files. Calls CBSTM03B as I/O submodule. Highest I/O density in estate (115 I/O ops). | Batch | READ: (via CBSTM03B) TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE; WRITE: STMTFILE (text), HTMLFILE (HTML) | COSTM01, CUSTREC, CVACT01Y, CVACT03Y |
| 24 | **CBSTM03B.CBL** | 230 | **Statement Generation (I/O Submodule)** — Called by CBSTM03A. Handles all file OPEN/READ/CLOSE operations for statement generation. | Batch (submodule) | OPEN/READ/CLOSE: TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | (none — data passed via LINKAGE SECTION) |
| 25 | **CBTRN01C.cbl** | 494 | **Daily Transaction Processor** — Reads DALYTRAN sequential file and enriches with customer/account/card data from VSAM files. Pre-processing step for posting. | Batch | READ: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANSACT | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVTRA06Y |
| 26 | **CBTRN02C.cbl** | 731 | **Transaction Posting** — Posts daily transactions from DALYTRAN to TRANSACT master file. Validates via XREF, updates account balances and TCATBALF category balances. Writes rejected transactions to DALYREJS. | Batch | READ: DALYTRAN, XREFFILE; READ/REWRITE: ACCTFILE, TCATBALF; WRITE: TRANFILE (VSAM), DALYREJS (rejects GDG) | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y |
| 27 | **CBTRN03C.cbl** | 649 | **Transaction Report** — Generates a daily transaction report. Reads sorted transaction file, enriches with XREF/type/category data, writes formatted report. | Batch | READ: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; WRITE: TRANREPT (report file) | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y |
| 28 | **CBEXPORT.cbl** | 582 | **Data Export** — Exports all core VSAM files (customer, account, XREF, transaction, card) into a single sequential export file for branch migration/backup. | Batch | READ: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE (all VSAM); WRITE: EXPFILE (sequential) | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 29 | **CBIMPORT.cbl** | 487 | **Data Import** — Reads export file and splits into separate output files per entity type. Validates record types and writes errors to ERROUT. | Batch | READ: EXPFILE; WRITE: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 30 | **COBSWAIT.cbl** | 41 | **Wait Utility** — Calls assembler MVSWAIT to pause batch job execution for a specified interval. Used for job scheduling timing. | Batch (utility) | CALL: MVSWAIT (assembler) | (none) |
| 31 | **CSUTLDTC.cbl** | 157 | **Date Validation Utility** — Validates and converts dates using LE intrinsic CEEDAYS. Called by CORPT00C and COTRN02C. | Batch (shared utility) | CALL: CEEDAYS (LE date conversion) | (none) |

---

## 2. Sub-Application: Authorization IMS/DB2/MQ

**Directory:** `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|--------------------|
| 32 | **COPAUA0C.cbl** | 1,026 | **Authorization Decision** — Most architecturally complex program. Receives auth requests via MQ, looks up card/account/customer via CICS READ and IMS DL/I (GU), decides approve/decline, sends reply via MQ, logs to DB2 AUTHFRDS table. Spans MQ + IMS + CICS + DB2. | Online (CICS/IMS/MQ/DB2) | MQ: MQOPEN, MQGET (request queue), MQPUT1 (reply queue), MQCLOSE; CICS READ: CARDXREF, ACCTDAT, CUSTDAT; IMS DL/I: GU, SCHD, TERM | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, CMQGMOV, CMQMDV, CMQODV, CMQPMOV, CMQTML, CMQV, CVACT01Y, CVACT03Y, CVCUS01Y |
| 33 | **COPAUS0C.cbl** | 1,032 | **Pending Auth Summary** — Browse pending authorization records from IMS database. Paginated display with IMS GU/GNP navigation. | Online (CICS/IMS) | IMS DL/I: GU, GNP (pending auth summary segments) | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 34 | **COPAUS1C.cbl** | 604 | **Pending Auth Detail** — View and update detail of a pending authorization from IMS. Supports marking status changes. | Online (CICS/IMS) | IMS DL/I: GU, GNP, REPL (auth detail segments) | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA |
| 35 | **COPAUS2C.cbl** | 244 | **Fraud Marking** — Marks an authorization as fraudulent by inserting/updating DB2 AUTHFRDS table. | Online (CICS/DB2) | DB2: INSERT INTO CARDDEMO.AUTHFRDS; UPDATE CARDDEMO.AUTHFRDS | CIPAUDTY |
| 36 | **CBPAUP0C.cbl** | 386 | **Expired Auth Purge** — Batch program to delete expired pending authorizations from IMS database. Uses GN/GNP to traverse, DLET to remove expired records. | Batch (IMS) | IMS DL/I: GN (root), GNP (child), DLET (expired records) | CIPAUDTY, CIPAUSMY |
| 37 | **PAUDBLOD.CBL** | 369 | **IMS Database Load** — Loads pending authorization data from sequential files into IMS database segments. | Batch (IMS) | READ: INFILE1 (root data), INFILE2 (child data); IMS DL/I: ISRT, GU | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |
| 38 | **PAUDBUNL.CBL** | 317 | **IMS Database Unload** — Unloads IMS pending auth database to sequential files for backup/migration. | Batch (IMS) | IMS DL/I: GN (root), GNP (child); WRITE: OPFILE1, OPFILE2 | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |
| 39 | **DBUNLDGS.CBL** | 366 | **GSAM Unload Utility** — Unloads IMS database using GSAM (Generalized Sequential Access Method) for data extraction. | Batch (IMS/GSAM) | IMS DL/I: GN, GNP, ISRT (GSAM output) | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB |

---

## 3. Sub-Application: Transaction Type DB2

**Directory:** `app/app-transaction-type-db2/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|--------------------|
| 40 | **COTRTLIC.cbl** | 2,098 | **Transaction Type List** — Online paginated browse of transaction types from DB2 TRANSACTION_TYPE table. Uses DB2 cursors (forward/backward) for scrolling. Supports inline update/delete. | Online (CICS/DB2) | DB2: DECLARE CURSOR, FETCH (forward + backward), SELECT COUNT, UPDATE, DELETE on CARDDEMO.TRANSACTION_TYPE | CSDB2RWY (inline — EXEC SQL INCLUDE) |
| 41 | **COTRTUPC.cbl** | 1,702 | **Transaction Type Update** — CRUD operations for transaction types in DB2. SELECT/INSERT/UPDATE/DELETE on TRANSACTION_TYPE and TRANSACTION_CATEGORY tables. | Online (CICS/DB2) | DB2: SELECT, INSERT, UPDATE, DELETE on CARDDEMO.TRANSACTION_TYPE; includes DCLTRTYP, DCLTRCAT | CSDB2RPY (inline — EXEC SQL INCLUDE) |
| 42 | **COBTUPDT.cbl** | 237 | **Batch Transaction Type Maintenance** — Batch loader for DB2 TRANSACTION_TYPE table. Reads sequential input file, performs INSERT/UPDATE/DELETE based on action code. | Batch (DB2) | READ: INPFILE (sequential); DB2: INSERT, UPDATE, DELETE on CARDDEMO.TRANSACTION_TYPE | (none — inline SQL) |

---

## 4. Sub-Application: VSAM/MQ

**Directory:** `app/app-vsam-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|--------------------|
| 43 | **COACCT01.cbl** | 620 | **Account Inquiry via MQ** — Receives account inquiry requests via MQ, reads account data from VSAM ACCTDATA, returns results via MQ reply. | Online (CICS/MQ) | MQ: MQOPEN, MQGET, MQPUT, MQCLOSE; CICS READ: ACCTDAT | CVACT01Y |
| 44 | **CODATE01.cbl** | 524 | **Date Inquiry via MQ** — Receives date inquiry requests via MQ, retrieves system date, returns formatted response via MQ reply. | Online (CICS/MQ) | MQ: MQOPEN, MQGET, MQPUT, MQCLOSE | (none) |

---

## 5. JCL Job Catalog (app/jcl/)

### Data Setup Jobs (VSAM Cluster Definition & Initial Load)

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 1 | **ACCTFILE.jcl** | Define and load Account VSAM KSDS cluster | STEP05: IDCAMS DELETE (ignore errors) → STEP10: IDCAMS DEFINE CLUSTER (ACCTDATA.VSAM.KSDS) → STEP15: IDCAMS REPRO from ACCTDATA.PS |
| 2 | **CARDFILE.jcl** | Define and load Card VSAM KSDS with alternate index | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER (CARDDATA.VSAM.KSDS) → STEP15: IDCAMS REPRO → STEP40: IDCAMS DEFINE ALTERNATEINDEX (by ACCTID) |
| 3 | **CUSTFILE.jcl** | Define and load Customer VSAM KSDS | CLCIFIL: SDSF close CICS file → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER (CUSTDATA.VSAM.KSDS) → STEP15: IDCAMS REPRO → OPCIFIL: SDSF open CICS file |
| 4 | **XREFFILE.jcl** | Define Card Cross-Reference VSAM KSDS with AIX and PATH | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER (CARDXREF.VSAM.KSDS) → STEP15: IDCAMS REPRO → STEP20: DEFINE ALTERNATEINDEX → STEP25: DEFINE PATH → STEP30: IDCAMS BLDINDEX |
| 5 | **TRANFILE.jcl** | Define Transaction Master VSAM KSDS with AIX | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE → STEP10: DEFINE CLUSTER (TRANSACT.VSAM.KSDS) → STEP15: REPRO from DALYTRAN.PS.INIT → STEP20: DEFINE ALTERNATEINDEX |
| 6 | **TRANTYPE.jcl** | Define Transaction Type VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: DEFINE CLUSTER (TRANTYPE.VSAM.KSDS) → STEP15: REPRO from TRANTYPE.PS |
| 7 | **TRANCATG.jcl** | Define Transaction Category VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: DEFINE CLUSTER (TRANCATG.VSAM.KSDS) → STEP15: REPRO from TRANCATG.PS |
| 8 | **TCATBALF.jcl** | Define Transaction Category Balance VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: DEFINE CLUSTER (TCATBALF.VSAM.KSDS) → STEP15: REPRO from TCATBALF.PS |
| 9 | **DISCGRP.jcl** | Define Disclosure Group VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: DEFINE CLUSTER (DISCGRP.VSAM.KSDS) → STEP15: REPRO from DISCGRP.PS |
| 10 | **DUSRSECJ.jcl** | Define and load User Security VSAM KSDS | PREDEL: IEFBR14 delete PS → STEP01: IEBGENER create USRSEC.PS (inline data) → STEP02: IDCAMS DEFINE CLUSTER (USRSEC.VSAM.KSDS) → STEP03: IDCAMS REPRO |
| 11 | **DEFCUST.jcl** | Define alternate Customer VSAM cluster | STEP05: IDCAMS DEFINE CLUSTER (CUSTDATA.CLUSTER) |
| 12 | **ESDSRRDS.jcl** | Define ESDS and RRDS VSAM clusters for user security | PREDEL → STEP01: IEBGENER → STEP02: DEFINE ESDS → STEP03: REPRO → STEP04: DEFINE RRDS → STEP05: REPRO |

### GDG (Generation Data Group) Definition Jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 13 | **DEFGDGB.jcl** | Define GDG bases for batch output versioning | STEP05: IDCAMS DEFINE GDG bases for TRANSACT.BKUP, SYSTRAN, DALYREJS, TRANSACT.DALY, TRANSACT.COMBINED |
| 14 | **DEFGDGD.jcl** | Define GDGs and backup transaction reference data | STEP10–STEP60: Define GDGs + IEBGENER backup for TRANTYPE, TRANCATG, DISCGRP |
| 15 | **DALYREJS.jcl** | Define GDG for daily rejected transactions | STEP05: IDCAMS DEFINE GDG (DALYREJS) |
| 16 | **REPTFILE.jcl** | Define GDG for report output files | STEP05: IDCAMS DEFINE GDG (REPTFILE) |

### Batch Processing Jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 17 | **POSTTRAN.jcl** | Post daily transactions (core batch pipeline step 1) | STEP15: EXEC PGM=CBTRN02C — reads DALYTRAN, writes TRANSACT, updates ACCTFILE/TCATBALF, rejects to DALYREJS GDG |
| 18 | **INTCALC.jcl** | Calculate interest on accounts (core batch pipeline step 2) | STEP15: EXEC PGM=CBACT04C PARM='2022071800' — reads TCATBALF, XREFFILE, DISCGRP, ACCTFILE; writes SYSTRAN GDG |
| 19 | **CREASTMT.JCL** | Generate customer statements (core batch pipeline step 3) | DELDEF01: DEFINE temp KSDS → STEP010: SORT transactions → STEP020: REPRO to temp KSDS → STEP030: IEFBR14 delete old statements → STEP040: EXEC PGM=CBSTM03A — generates STMTFILE + HTMLFILE |
| 20 | **TRANREPT.jcl** | Generate daily transaction report (core batch pipeline step 4) | STEP05R: REPROC backup TRANSACT → STEP05R: SORT by card number → STEP10R: EXEC PGM=CBTRN03C — reads sorted transactions + XREF + types + categories, writes report |
| 21 | **READACCT.jcl** | Read and dump account VSAM data | PREDEL: delete old output → STEP05: EXEC PGM=CBACT01C — reads ACCTDATA VSAM, writes PS/ARRY/VB output files |
| 22 | **READCARD.jcl** | Read and dump card VSAM data | STEP05: EXEC PGM=CBACT02C — reads CARDDATA VSAM |
| 23 | **READCUST.jcl** | Read and dump customer VSAM data | STEP05: EXEC PGM=CBCUS01C — reads CUSTDATA VSAM |
| 24 | **READXREF.jcl** | Read and dump cross-reference VSAM data | STEP05: EXEC PGM=CBACT03C — reads CARDXREF VSAM |
| 25 | **CBEXPORT.jcl** | Export all core VSAM files to single sequential file | STEP01: IDCAMS DEFINE export cluster → STEP02: EXEC PGM=CBEXPORT — reads 5 VSAM files, writes EXPORT.DATA |
| 26 | **CBIMPORT.jcl** | Import from export file into separate entity files | STEP01: EXEC PGM=CBIMPORT — reads EXPORT.DATA, writes per-entity output files + error file |
| 27 | **COMBTRAN.jcl** | Combine and sort transaction backups | STEP05R: SORT merging TRANSACT.BKUP(0) + SYSTRAN(0) → STEP10: REPRO combined output to TRANSACT VSAM |
| 28 | **TRANBKP.jcl** | Backup and redefine transaction master | STEP05R: REPROC to TRANSACT.BKUP GDG → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER (fresh TRANSACT VSAM) |
| 29 | **PRTCATBL.jcl** | Print transaction category balance report | DELDEF: delete old report → STEP05R: REPROC TCATBALF → STEP10R: SORT by acct/type/category → output to TCATBALF.REPT |
| 30 | **TRANIDX.jcl** | Define AIX and PATH on Transaction VSAM | STEP20: DEFINE ALTERNATEINDEX → STEP25: DEFINE PATH → STEP30: BLDINDEX |
| 31 | **WAITSTEP.jcl** | Wait step for job scheduling | WAIT: EXEC PGM=COBSWAIT — pauses execution |

### CICS File Management Jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 32 | **CLOSEFIL.jcl** | Close CICS files for batch processing | CLCIFIL: SDSF CEMT SET FIL CLO for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| 33 | **OPENFIL.jcl** | Open CICS files after batch processing | OPCIFIL: SDSF CEMT SET FIL OPE for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |

### CICS Administration Jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 34 | **CBADMCDJ.jcl** | Define CICS CSD resources for CardDemo | STEP1: DFHCSDUP — DEFINE LIBRARY, MAPSETs, PROGRAMs, TRANSACTIONs, FILEs for all CardDemo CICS resources |

### Utility Jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 35 | **INTRDRJ1.JCL** | Internal reader job (step 1) — backup and submit | IDCAMS: REPRO backup → STEP01: IEBGENER submit INTRDRJ2 via internal reader |
| 36 | **INTRDRJ2.JCL** | Internal reader job (step 2) — secondary backup | IDCAMS: REPRO secondary backup |
| 37 | **FTPJCL.JCL** | FTP file transfer utility | STEP1: FTP — transfer files to/from mainframe |
| 38 | **TXT2PDF1.JCL** | Convert text statement to PDF | TXT2PDF: IKJEFT1B + TXT2PDF REXX exec — converts STATEMNT.PS to PDF |

---

## 6. Sub-Application JCL Jobs

### Authorization IMS/DB2/MQ Jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 39 | **CBPAUP0J.jcl** | Purge expired pending authorizations | STEP01: DFSRRC00 (IMS BMP) → runs CBPAUP0C against IMS database |
| 40 | **DBPAUTP0.jcl** | Unload IMS pending auth database | STEPDEL: delete output → UNLOAD: DFSRRC00 — IMS unload to IMSDATA.DBPAUTP0 |
| 41 | **LOADPADB.JCL** | Load IMS pending auth database | STEP01: DFSRRC00 — loads from ROOT/CHILD sequential files into IMS segments |
| 42 | **UNLDGSAM.JCL** | GSAM unload of IMS database | STEP01: DFSRRC00 — GSAM unload to ROOT.GSAM/CHILD.GSAM |
| 43 | **UNLDPADB.JCL** | Standard unload of IMS pending auth database | STEP0: IEFBR14 delete old output → STEP01: DFSRRC00 — unload to ROOT.FILEO/CHILD.FILEO |

### Transaction Type DB2 Jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 44 | **CREADB21.jcl** | Create DB2 tables and load initial data | FREEPLN: free existing plan → CRCRDDB: IKJEFT01 run DDL (DB2CREAT) → LDTTYPE: prep → RUNTEP2: load data |
| 45 | **MNTTRDB2.jcl** | Batch maintain transaction types in DB2 | STEP1: IKJEFT01 → runs COBTUPDT program against DB2 with input file |
| 46 | **TRANEXTR.jcl** | Extract transaction type data from DB2 to sequential | STEP10–20: IEBGENER backup existing PS files → STEP30: IEFBR14 delete old PS → STEP40–50: IKJEFT01 DSNTEP4 UNLOAD to TRANTYPE.PS and TRANCATG.PS |
