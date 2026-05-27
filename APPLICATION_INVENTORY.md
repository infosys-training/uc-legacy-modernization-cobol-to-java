# Application Inventory — CardDemo

> Comprehensive catalog of all COBOL programs and JCL jobs in the CardDemo credit card management system.

---

## Table of Contents

1. [Main Application — Batch Programs](#main-application--batch-programs)
2. [Main Application — CICS Online Programs](#main-application--cics-online-programs)
3. [Authorization Sub-Application (IMS/DB2/MQ)](#authorization-sub-application-imsdb2mq)
4. [Transaction-Type Sub-Application (DB2)](#transaction-type-sub-application-db2)
5. [VSAM-MQ Sub-Application](#vsam-mq-sub-application)
6. [JCL Job Catalog](#jcl-job-catalog)
7. [BMS Maps](#bms-maps)
8. [Procedure Libraries](#procedure-libraries)
9. [Scheduler Configurations](#scheduler-configurations)

---

## Main Application — Batch Programs

| Filename | Program-ID | Lines | Purpose | Key I/O | Copybooks Referenced |
|----------|-----------|-------|---------|---------|---------------------|
| CBACT01C.cbl | CBACT01C | 430 | Read the account VSAM file and write into sequential output files (compressed, array, variable-block formats) | VSAM: ACCTDATA (read); Sequential: PSCOMP, ARRYPS, VBPS (write) | CODATECN, CVACT01Y |
| CBACT02C.cbl | CBACT02C | 178 | Read and display all records from the card data VSAM file | VSAM: CARDDATA (read) | CVACT02Y |
| CBACT03C.cbl | CBACT03C | 178 | Read and display all records from the card cross-reference VSAM file | VSAM: CARDXREF (read) | CVACT03Y |
| CBACT04C.cbl | CBACT04C | 652 | Interest calculator — compute interest on account balances using disclosure group rates, write interest transactions | VSAM: TCATBALF (read/write), CARDXREF (read via AIX), ACCTDATA (read/write), DISCGRP (read), TRANSACT (write via GDG) | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y |
| CBCUS01C.cbl | CBCUS01C | 178 | Read and display all records from the customer data VSAM file | VSAM: CUSTDATA (read) | CVCUS01Y |
| CBEXPORT.cbl | CBEXPORT | 582 | Export customer, account, card, cross-reference, and transaction data to a multi-record sequential export file for branch migration | VSAM: CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA (all read); Sequential: EXPORT.DATA (write) | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| CBIMPORT.cbl | CBIMPORT | 487 | Import data from a multi-record export file back into separate sequential files, with error handling | Sequential: EXPORT.DATA (read); CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT, TRANSACT.IMPORT (write); IMPORT.ERRORS (write) | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| CBSTM03A.CBL | CBSTM03A | 924 | Create account statements in both plain text and HTML format by reading transactions, cross-references, accounts, and customers | VSAM: TRXFL (read), CARDXREF (read), ACCTDATA (read), CUSTDATA (read); Sequential: STATEMNT.PS (write), STATEMNT.HTML (write) | COSTM01, CUSTREC, CVACT01Y, CVACT03Y |
| CBSTM03B.CBL | CBSTM03B | 230 | Subroutine called by CBSTM03A — opens and manages file I/O for transaction, cross-reference, customer, and account files | VSAM: TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE | _(none — defines structures inline)_ |
| CBTRN01C.cbl | CBTRN01C | 494 | Read daily transaction file and post valid transactions to the master transaction file after cross-reference and account validation | Sequential: DALYTRAN (read); VSAM: CUSTDATA (read), CARDXREF (read), CARDDATA (read), ACCTDATA (read), TRANSACT (write) | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVTRA06Y |
| CBTRN02C.cbl | CBTRN02C | 731 | Post daily transactions: validate, update account balances, update transaction category balances, reject invalid transactions | Sequential: DALYTRAN (read), DALYREJS (write via GDG); VSAM: TRANSACT (write), CARDXREF (read), ACCTDATA (read/write), TCATBALF (read/write) | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y |
| CBTRN03C.cbl | CBTRN03C | 649 | Generate daily transaction report with type/category descriptions, page/account/grand totals | VSAM: TRANSACT (read), CARDXREF (read), TRANTYPE (read), TRANCATG (read); Sequential: REPORT-FILE (write), DATE-PARMS (read) | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y |
| COBSWAIT.cbl | COBSWAIT | 41 | Utility — invoke MVSWAIT system call to pause batch job execution (used as a wait step between CICS file close/open operations) | _(none)_ | _(none)_ |
| CSUTLDTC.cbl | CSUTLDTC | 157 | Utility — date validation and conversion using Language Environment CEEDAYS service | _(none — called as subroutine)_ | _(none)_ |

## Main Application — CICS Online Programs

| Filename | Program-ID | Lines | Purpose | Key I/O | Copybooks Referenced |
|----------|-----------|-------|---------|---------|---------------------|
| COACTUPC.cbl | COACTUPC | 4236 | Accept and process account updates — the most complex CICS program; handles viewing and updating account details, credit limits, status with extensive field validation | CICS Files: ACCTDAT (read/write), CARDXREF (read), CUSTDAT (read), CARDDAT (read), USRSEC (read) | CSSTRPFY, CSUTLDWY, COACTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CSUTLDPY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| COACTVWC.cbl | COACTVWC | 941 | View account details — display account, card, and customer information in read-only mode | CICS Files: ACCTDAT (read), CARDXREF (read), CUSTDAT (read), CARDDAT (read) | CSSTRPFY, COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| COADM01C.cbl | COADM01C | 288 | Admin menu — display and route admin menu options (User List, User Add, User Update, User Delete, Transaction Type List, Transaction Type Maintenance) | CICS Files: USRSEC (read) | COADM01, COADM02Y, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COBIL00C.cbl | COBIL00C | 572 | Bill payment — accept card number, validate against cross-reference and account, create a bill payment transaction | CICS Files: CARDXREF (read), ACCTDAT (read), TRANSACT (write) | COBIL00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COCRDLIC.cbl | COCRDLIC | 1459 | Credit card list — browse/search credit cards with pagination, navigate to card detail or card update programs | CICS Files: CARDDAT (browse), CARDXREF (read), CUSTDAT (read) | CSSTRPFY, COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| COCRDSLC.cbl | COCRDSLC | 887 | Credit card detail/search — display detailed card information with associated customer and account data | CICS Files: CARDDAT (read), CARDXREF (read), CUSTDAT (read), ACCTDAT (read) | CSSTRPFY, COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| COCRDUPC.cbl | COCRDUPC | 1560 | Credit card update — modify card details (embossed name, status, expiration date) with validation | CICS Files: CARDDAT (read/write), CARDXREF (read), CUSTDAT (read), ACCTDAT (read) | CSSTRPFY, COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| COMEN01C.cbl | COMEN01C | 308 | Main menu — display application menu and route to selected program based on user role (Regular/Admin) | CICS Files: USRSEC (read) | COCOM01Y, COMEN01, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| CORPT00C.cbl | CORPT00C | 649 | Transaction report — accept date range and generate on-screen transaction listing with pagination | CICS Files: TRANSACT (browse) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COSGN00C.cbl | COSGN00C | 260 | Sign-on — authenticate user credentials against USRSEC file, route to admin or regular menu | CICS Files: USRSEC (read) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COTRN00C.cbl | COTRN00C | 699 | Transaction list — browse transactions with search by account, card, or transaction ID with pagination | CICS Files: TRANSACT (browse), CARDXREF (read) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COTRN01C.cbl | COTRN01C | 330 | Transaction detail — display a single transaction record in read-only mode | CICS Files: TRANSACT (read) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COTRN02C.cbl | COTRN02C | 783 | Transaction add — accept and validate new transaction data, write to TRANSACT file, update account balances | CICS Files: TRANSACT (write), ACCTDAT (read/write), CARDXREF (read), TCATBALF (read/write) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COUSR00C.cbl | COUSR00C | 695 | User list — browse security user records with pagination | CICS Files: USRSEC (browse) | COCOM01Y, COTTL01Y, COUSR00, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR01C.cbl | COUSR01C | 299 | User add — accept and create new security user record | CICS Files: USRSEC (write) | COCOM01Y, COTTL01Y, COUSR01, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR02C.cbl | COUSR02C | 414 | User update — modify existing security user record (name, password, type) | CICS Files: USRSEC (read/write) | COCOM01Y, COTTL01Y, COUSR02, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR03C.cbl | COUSR03C | 359 | User delete — confirm and delete security user record | CICS Files: USRSEC (read/delete) | COCOM01Y, COTTL01Y, COUSR03, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

## Authorization Sub-Application (IMS/DB2/MQ)

| Filename | Program-ID | Lines | Purpose | Key I/O | Copybooks Referenced |
|----------|-----------|-------|---------|---------|---------------------|
| CBPAUP0C.cbl | CBPAUP0C | 386 | Batch — read pending authorization records from IMS database files and process updates | IMS DB Files: OPFILE1, OPFILE2 (read/write) | CIPAUDTY, CIPAUSMY |
| COPAUA0C.cbl | COPAUA0C | 1026 | CICS — authorization processing engine; receive authorization requests via MQ, validate against accounts/customers, respond with approval/decline via MQ | CICS Files: ACCTDAT, CARDXREF, CUSTDAT (read); MQ Queues: request (read), response (write) | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, CMQGMOV, CMQMDV, CMQODV, CMQPMOV, CMQTML, CMQV, CVACT01Y, CVACT03Y, CVCUS01Y |
| COPAUS0C.cbl | COPAUS0C | 1032 | CICS — pending authorization summary screen; display list of pending authorizations for review with pagination | CICS Files: IMS-based auth data (read); BMS Map: COPAU00 | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| COPAUS1C.cbl | COPAUS1C | 604 | CICS — pending authorization detail screen; view a single authorization record, link to fraud processing | CICS Files: Auth data (read); BMS Map: COPAU01 | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA |
| COPAUS2C.cbl | COPAUS2C | 244 | CICS — authorization fraud recording; write fraud status to DB2 AUTHFRDS table | DB2: AUTHFRDS (insert/update); EXEC SQL: 4 statements | CIPAUDTY |
| DBUNLDGS.CBL | DBUNLDGS | 366 | Batch IMS — unload GSAM (Generalized Sequential Access Method) records from IMS database to sequential files | IMS DB: PADFL, PASFL databases (read via CBLTDLI); Sequential: OPFILE1, OPFILE2 (write) | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB |
| PAUDBLOD.CBL | PAUDBLOD | 369 | Batch IMS — load pending authorization data from sequential files into IMS database | Sequential: INFILE1, INFILE2 (read); IMS DB (write via CBLTDLI insert) | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |
| PAUDBUNL.CBL | PAUDBUNL | 317 | Batch IMS — unload pending authorization data from IMS database to sequential files | IMS DB (read via CBLTDLI); Sequential: OPFILE1, OPFILE2 (write) | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |

## Transaction-Type Sub-Application (DB2)

| Filename | Program-ID | Lines | Purpose | Key I/O | Copybooks Referenced |
|----------|-----------|-------|---------|---------|---------------------|
| COBTUPDT.cbl | COBTUPDT | 237 | Batch — update DB2 transaction type tables from sequential file input | DB2: TRANSACTION_TYPE, TRANSACTION_TYPE_CATEGORY (EXEC SQL: 5); Sequential: TR-RECORD (read) | _(inline SQL declarations)_ |
| COTRTLIC.cbl | COTRTLIC | 2098 | CICS — list transaction types for updates and deletes; browse DB2 tables with pagination, navigate to update screen | DB2: TR_TYPE, TR_TYPE_CATEGORY (EXEC SQL: 16); CICS: 12 commands | _(inline — uses CSDB2RPY, CSDB2RWY procedures)_ |
| COTRTUPC.cbl | COTRTUPC | 1702 | CICS — accept and process transaction type updates; add, modify, delete transaction type records in DB2 | DB2: TR_TYPE (EXEC SQL: 7); CICS: 12 commands | _(inline — uses CSDB2RPY, CSDB2RWY procedures)_ |

## VSAM-MQ Sub-Application

| Filename | Program-ID | Lines | Purpose | Key I/O | Copybooks Referenced |
|----------|-----------|-------|---------|---------|---------------------|
| COACCT01.cbl | COACCT01 | 620 | CICS — account record processor via MQ; receive account update requests from MQ queue, process against VSAM account file, respond via MQ | CICS Files: ACCTDAT (read/write); MQ Queues: request (read), response (write) | CVACT01Y |
| CODATE01.cbl | CODATE01 | 524 | CICS — date validation service via MQ; receive date validation requests from MQ queue, validate date format and logic, respond via MQ | MQ Queues: request (read), response (write) | _(none)_ |

---

## JCL Job Catalog

### Data Definition & Setup Jobs

| Job Name | Description | Steps | Key Datasets |
|----------|------------|-------|-------------|
| ACCTFILE | Delete/define account data VSAM file | STEP05: IDCAMS (delete), STEP10: IDCAMS (define KSDS), STEP15: IDCAMS (REPRO load from PS) | ACCTDATA.PS → ACCTDATA.VSAM.KSDS |
| CARDFILE | Delete/define card data VSAM file with AIX | CLCIFIL: SDSF (close CICS), STEP05-STEP60: IDCAMS (delete/define KSDS, define AIX, build AIX path), OPCIFIL: SDSF (open CICS) | CARDDATA.PS → CARDDATA.VSAM.KSDS |
| CUSTFILE | Define customer VSAM file | CLCIFIL: SDSF, STEP05-STEP15: IDCAMS (delete/define/REPRO), OPCIFIL: SDSF | CUSTDATA.PS → CUSTDATA.VSAM.KSDS |
| XREFFILE | Delete/define cross-reference VSAM file with AIX | STEP05-STEP30: IDCAMS (delete/define KSDS, define AIX, build path) | CARDXREF.PS → CARDXREF.VSAM.KSDS |
| TRANFILE | Define transaction master VSAM with AIX | CLCIFIL: SDSF, STEP05-STEP30: IDCAMS (delete/define KSDS, AIX, path), OPCIFIL: SDSF | DALYTRAN.PS.INIT → TRANSACT.VSAM.KSDS |
| TCATBALF | Define transaction category balance VSAM | STEP05-STEP15: IDCAMS (delete/define/REPRO) | TCATBALF.PS → TCATBALF.VSAM.KSDS |
| TRANCATG | Define transaction category VSAM | STEP05-STEP15: IDCAMS (delete/define/REPRO) | TRANCATG.PS → TRANCATG.VSAM.KSDS |
| TRANTYPE | Define transaction type VSAM | STEP05-STEP15: IDCAMS (delete/define/REPRO) | TRANTYPE.PS → TRANTYPE.VSAM.KSDS |
| DISCGRP | Define disclosure group VSAM | STEP05-STEP15: IDCAMS (delete/define/REPRO) | DISCGRP.PS → DISCGRP.VSAM.KSDS |
| TRANIDX | Define AIX on transaction master | STEP20-STEP30: IDCAMS (define AIX, define path, build index) | TRANSACT.VSAM.KSDS AIX |
| DEFCUST | Define customer data file (alternate definition) | STEP05: IDCAMS (delete), STEP05: IDCAMS (define) | CUSTDATA |
| DUSRSECJ | Define user security VSAM file | PREDEL: IEFBR14, STEP01: IEBGENER (create PS), STEP02-STEP03: IDCAMS (define/REPRO) | USRSEC.PS → USRSEC.VSAM.KSDS |
| ESDSRRDS | Define ESDS/RRDS files for user security | PREDEL: IEFBR14, STEP01-STEP05: IEBGENER/IDCAMS (PS, ESDS, RRDS) | ESDSRRDS.PS, USRSEC.VSAM.ESDS, USRSEC.VSAM.RRDS |

### GDG Definition Jobs

| Job Name | Description | Steps | Key Datasets |
|----------|------------|-------|-------------|
| DEFGDGB | Define GDG bases for backups | STEP05: IDCAMS | TRANSACT.BKUP, SYSTRAN, TRANSACT.COMBINED, TRANSACT.DALY, DALYREJS bases |
| DEFGDGD | Define DB2-related GDG bases and initial load | STEP10-STEP60: IDCAMS/IEBGENER pairs | TRANTYPE.PS/BKUP, TRANCATG.PS/BKUP, DISCGRP.PS/BKUP |
| DALYREJS | Define GDG for daily rejects | STEP05: IDCAMS | DALYREJS GDG base |
| REPTFILE | Define GDG for report files | STEP05: IDCAMS | Report GDG base |

### Batch Processing Jobs

| Job Name | Description | Steps | Key Datasets |
|----------|------------|-------|-------------|
| CLOSEFIL | Close CICS files for batch processing | CLCIFIL: SDSF | _(CICS file control)_ |
| OPENFIL | Open CICS files after batch processing | OPCIFIL: SDSF | _(CICS file control)_ |
| WAITSTEP | Wait step — pause execution between batch steps | WAIT: PGM=COBSWAIT | LOADLIB |
| INTCALC | Interest calculator — monthly interest computation | STEP15: PGM=CBACT04C, PARM='2022071800' | TCATBALF.VSAM.KSDS, CARDXREF.VSAM.KSDS (+ AIX.PATH), ACCTDATA.VSAM.KSDS, DISCGRP.VSAM.KSDS, SYSTRAN(+1) |
| POSTTRAN | Post transactions — daily transaction posting | STEP15: PGM=CBTRN02C | TRANSACT.VSAM.KSDS, DALYTRAN.PS, CARDXREF.VSAM.KSDS, DALYREJS(+1), ACCTDATA.VSAM.KSDS, TCATBALF.VSAM.KSDS |
| TRANBKP | Backup and clear transaction master | STEP05R: PROC=REPROC (REPRO to GDG), STEP05/STEP10: IDCAMS (delete records) | TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1) |
| COMBTRAN | Combine transaction backup with system transactions | STEP05R: SORT (merge/sort), STEP10: IDCAMS (REPRO combined into VSAM) | TRANSACT.BKUP(0), SYSTRAN(0) → TRANSACT.COMBINED(+1) → TRANSACT.VSAM.KSDS |
| TRANREPT | Generate daily transaction report | STEP05R: PROC=REPROC (backup), STEP05R: SORT (filter/sort by date), STEP10R: PGM=CBTRN03C (report) | TRANSACT.VSAM.KSDS, CARDXREF, TRANTYPE, TRANCATG, DATEPARM → TRANREPT(+1) |
| CREASTMT | Create account statements | DELDEF01: IDCAMS (define KSDS), STEP010: SORT (transform), STEP020: IDCAMS (REPRO), STEP030: IEFBR14, STEP040: PGM=CBSTM03A | TRANSACT.VSAM.KSDS → TRXFL.SEQ → TRXFL.VSAM.KSDS; STATEMNT.PS, STATEMNT.HTML |
| PRTCATBL | Print transaction category balance file | DELDEF: IEFBR14, STEP05R: PROC=REPROC (backup), STEP10R: SORT (format/print) | TCATBALF.VSAM.KSDS → TCATBALF.BKUP(+1), TCATBALF.REPT |
| READACCT | Read account data | PREDEL: IEFBR14, STEP05: PGM=CBACT01C | ACCTDATA.VSAM.KSDS → PSCOMP, ARRYPS, VBPS |
| READCARD | Read card data | STEP05: PGM=CBACT02C | CARDDATA.VSAM.KSDS |
| READCUST | Read customer data | STEP05: PGM=CBCUS01C | CUSTDATA.VSAM.KSDS |
| READXREF | Read cross-reference file | STEP05: PGM=CBACT03C | CARDXREF.VSAM.KSDS |
| CBEXPORT | Export data for branch migration | STEP01: IDCAMS (delete old export), STEP02: PGM=CBEXPORT | CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA → EXPORT.DATA |
| CBIMPORT | Import branch migration data | STEP01: PGM=CBIMPORT | EXPORT.DATA → CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT, TRANSACT.IMPORT |

### CICS Administration Jobs

| Job Name | Description | Steps | Key Datasets |
|----------|------------|-------|-------------|
| CBADMCDJ | Define CICS CSD entries for CardDemo programs and files | STEP1: PGM=DFHCSDUP | DFHCSD (CICS System Definition) |

### Utility / Transfer Jobs

| Job Name | Description | Steps | Key Datasets |
|----------|------------|-------|-------------|
| FTPJCL | FTP file transfer | STEP1: PGM=FTP | _(FTP transfer)_ |
| INTRDRJ1 | Internal reader job 1 — backup FTP test file and submit job 2 | IDCAMS: backup, STEP01: IEBGENER (submit INTRDRJ2) | FTP.TEST → FTP.TEST.BKUP |
| INTRDRJ2 | Internal reader job 2 — backup FTP test backup | IDCAMS: backup | FTP.TEST.BKUP → FTP.TEST.BKUP.INTRDR |
| TXT2PDF1 | Convert text statement to PDF | TXT2PDF: PGM=IKJEFT1B (REXX exec) | STATEMNT.PS → PDF |

### Sub-Application JCL — Authorization (IMS/DB2/MQ)

| Job Name | Description | Steps | Key Datasets |
|----------|------------|-------|-------------|
| CBPAUP0J | Batch pending authorization update processing | STEP15: PGM=CBPAUP0C | Auth IMS database files |
| DBPAUTP0 | Define IMS GSAM files for pending authorization | STEP01-STEP03: IEBGENER/IDCAMS | Auth GSAM files |
| LOADPADB | Load pending authorization data into IMS database | STEP01: PGM=PAUDBLOD | Sequential files → IMS database |
| UNLDGSAM | Unload GSAM records from IMS | STEP01: PGM=DBUNLDGS | IMS database → Sequential files |
| UNLDPADB | Unload pending authorization database | STEP01: PGM=PAUDBUNL | IMS database → Sequential files |

### Sub-Application JCL — Transaction Type (DB2)

| Job Name | Description | Steps | Key Datasets |
|----------|------------|-------|-------------|
| CREADB21 | Create DB2 tables for transaction types | Multiple steps: IKJEFT01 (DDL execution) | DB2 tables: TRANSACTION_TYPE, TRANSACTION_TYPE_CATEGORY |
| MNTTRDB2 | Maintain transaction type DB2 tables from VSAM | Multiple steps: IEBGENER (backup), IKJEFT01 (DB2 refresh) | TRANTYPE.PS, TRANCATG.PS → DB2 tables |
| TRANEXTR | Extract transaction types from DB2 to sequential files | STEP10-STEP50: IEBGENER/IKJEFT01 | DB2 → TRANTYPE.PS, TRANCATG.PS |

---

## BMS Maps

| Map Name | Associated Program | Screen Purpose |
|----------|-------------------|----------------|
| COACTUP.bms | COACTUPC.cbl | Account update screen |
| COACTVW.bms | COACTVWC.cbl | Account view screen |
| COADM01.bms | COADM01C.cbl | Admin menu screen |
| COBIL00.bms | COBIL00C.cbl | Bill payment screen |
| COCRDLI.bms | COCRDLIC.cbl | Credit card list screen |
| COCRDSL.bms | COCRDSLC.cbl | Credit card detail/search screen |
| COCRDUP.bms | COCRDUPC.cbl | Credit card update screen |
| COMEN01.bms | COMEN01C.cbl | Main menu screen |
| CORPT00.bms | CORPT00C.cbl | Transaction report screen |
| COSGN00.bms | COSGN00C.cbl | Sign-on screen |
| COTRN00.bms | COTRN00C.cbl | Transaction list screen |
| COTRN01.bms | COTRN01C.cbl | Transaction detail screen |
| COTRN02.bms | COTRN02C.cbl | Transaction add screen |
| COUSR00.bms | COUSR00C.cbl | User list screen |
| COUSR01.bms | COUSR01C.cbl | User add screen |
| COUSR02.bms | COUSR02C.cbl | User update screen |
| COUSR03.bms | COUSR03C.cbl | User delete screen |

Each BMS map has a corresponding generated copybook in `app/cpy-bms/` (e.g., `COACTUP.CPY`).

## Procedure Libraries

| Procedure | Purpose |
|-----------|---------|
| REPROC.prc | REPRO utility — uses IDCAMS to load or unload VSAM files. Accepts FILEIN/FILEOUT DD overrides and CNTLLIB parameter. |
| TRANREPT.prc | Transaction report procedure — chains REPROC (backup transaction file to GDG), SORT (filter by date, sort by card number), and CBTRN03C (generate report). |

## Scheduler Configurations

### Control-M (CardDemo.controlm)

Three batch pipeline schedules defined as Control-M folders:

- **DAILY-TransactionBackup** (runs ALL days): CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
- **WEEKLY-TransactionTypesDBRefresh** (runs Saturday): MNTTRDB2 → parallel sub-folders:
  - **TransactionTypesDBRefresh**: TRANEXTR
  - **DisclosureGroupsRefresh**: CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL
- **MONTHLY-InterestCalculation** (monthly): CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL

### CA-7 (CardDemo.ca7)

Legacy CA-7 scheduler definition with triggered job chains:
- CLOSEFIL triggers CBPAUP0J → POSTTRAN → WAITSTEP → OPENFIL
- CLOSEFIL also triggers TRANTYPE → WAITSTEP → OPENFIL (separate chain)
