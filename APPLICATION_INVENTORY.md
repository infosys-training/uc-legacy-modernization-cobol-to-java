# APPLICATION INVENTORY — CardDemo COBOL Estate

> Generated from static analysis of `app/cbl/`, sub-application directories, and `app/jcl/`.

---

## 1. COBOL Programs — Core Application (`app/cbl/`)

### 1.1 Batch Programs

| # | Filename | Program-ID | Purpose | LOC | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|---------|-----|---------------------|----------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read account VSAM file and write to fixed-length, array, and variable-block output files for data extraction | 430 | READ ACCTFILE (VSAM KSDS), WRITE OUT-FILE (PS), WRITE ARRY-FILE (PS), WRITE VBRC-FILE (PS) | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and print card data file (card dump utility) | 178 | READ CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and print card-account cross-reference file | 178 | READ XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Monthly interest calculation — reads category balances, looks up discount group rates, computes interest/fees, updates accounts, writes transactions | 652 | READ TCATBALF (VSAM KSDS), READ XREF-FILE (VSAM KSDS), READ DISCGRP-FILE (VSAM KSDS), READ/REWRITE ACCOUNT-FILE (VSAM KSDS), WRITE TRANSACT-FILE (VSAM KSDS) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and print customer data file (customer dump utility) | 178 | READ CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export all CardDemo master files (customers, accounts, xrefs, transactions, cards) into a single unified export sequential file for branch migration | 582 | READ CUSTOMER-INPUT, ACCOUNT-INPUT, XREF-INPUT, TRANSACTION-INPUT, CARD-INPUT (all VSAM KSDS); WRITE EXPORT-OUTPUT (PS) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Import a branch migration export file, parse records by type, and write to individual output files (customers, accounts, xrefs, transactions) with error handling | 487 | READ EXPORT-INPUT (PS); WRITE CUSTOMER-OUTPUT, ACCOUNT-OUTPUT, XREF-OUTPUT, TRANSACTION-OUTPUT, CARD-OUTPUT, ERROR-OUTPUT (all PS) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | CBSTM03A | Generate credit card statements — reads xrefs, customers, accounts, transactions; produces text and HTML statement files | 924 | READ XREFFILE, CUSTFILE, ACCTFILE, TRNXFILE (all VSAM KSDS via CBSTM03B); WRITE STMT-FILE (PS), WRITE HTML-FILE (PS) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | CBSTM03B | I/O subroutine for CBSTM03A — handles OPEN/READ/CLOSE for transaction, xref, customer, and account files | 230 | OPEN/READ/CLOSE TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE (all VSAM KSDS) | *(none — uses linkage section)* |
| 10 | CBTRN01C.cbl | CBTRN01C | Daily transaction validation — reads daily transactions, looks up card cross-references and account data for validation | 494 | READ DALYTRAN-FILE (PS), READ XREF-FILE, CARD-FILE, ACCOUNT-FILE, CUSTOMER-FILE, TRANSACT-FILE (all VSAM KSDS) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | CBTRN02C | Post daily transactions — validates and posts transactions, updates account balances and category balance records, writes rejects | 731 | READ DALYTRAN-FILE (PS), READ/REWRITE XREF-FILE (VSAM KSDS), READ/REWRITE ACCOUNT-FILE (VSAM KSDS), READ/WRITE/REWRITE TCATBAL-FILE (VSAM KSDS), WRITE TRANSACT-FILE (VSAM KSDS), WRITE DALYREJS-FILE (PS/GDG) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | CBTRN03C | Transaction report generation — reads posted transactions, looks up xref/type/category, produces formatted transaction reports with page/account/grand totals | 649 | READ TRANSACT-FILE (VSAM KSDS), READ XREF-FILE (VSAM KSDS), READ TRANTYPE-FILE (VSAM KSDS), READ TRANCATG-FILE (VSAM KSDS), READ DATE-PARMS-FILE (PS), WRITE REPORT-FILE (PS/GDG) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | COBSWAIT | Batch wait/delay utility — calls assembler MVSWAIT to pause execution for a specified time | 41 | CALL 'MVSWAIT' | *(none)* |
| 14 | CSUTLDTC.cbl | CSUTLDTC | Date validation utility — converts and validates dates using IBM LE CEEDAYS service | 157 | CALL 'CEEDAYS' | *(none)* |

### 1.2 Online (CICS) Programs

| # | Filename | Program-ID | Purpose | LOC | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|---------|-----|---------------------|----------------------|
| 1 | COSGN00C.cbl | COSGN00C | Sign-on screen — authenticates users against USRSEC VSAM file, routes to admin or user menu | 260 | EXEC CICS READ (USRSEC), EXEC CICS SEND/RECEIVE MAP, EXEC CICS XCTL | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | COADM01C.cbl | COADM01C | Admin menu — displays admin menu options, routes to sub-programs (user management, DB2 maintenance) | 288 | EXEC CICS SEND/RECEIVE MAP, EXEC CICS XCTL | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COMEN01C.cbl | COMEN01C | Main user menu — displays 11 menu options (account view/update, cards, transactions, reports, bill pay, auth view), routes to sub-programs | 308 | EXEC CICS SEND/RECEIVE MAP, EXEC CICS XCTL, EXEC CICS INQUIRE | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTVWC.cbl | COACTVWC | Account view — displays account details, card data, and customer information by account ID | 941 | EXEC CICS READ (CARDXREF via AIX, ACCTDATA, CUSTDATA), EXEC CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 5 | COACTUPC.cbl | COACTUPC | Account update — edits and updates account and customer records with full field validation (dates, amounts, SSN) | 4236 | EXEC CICS READ/REWRITE (CARDXREF, ACCTDATA, CUSTDATA), EXEC CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY |
| 6 | COCRDLIC.cbl | COCRDLIC | Credit card list — browse and list credit cards with scrolling (PF7/PF8) using STARTBR/READNEXT/READPREV | 1459 | EXEC CICS STARTBR/READNEXT/READPREV/ENDBR (CARDDATA), EXEC CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | COCRDSLC.cbl | COCRDSLC | Credit card detail view — displays single card record with account and customer details | 887 | EXEC CICS READ (CARDDATA, CARDXREF via AIX, ACCTDATA), EXEC CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | COCRDUPC.cbl | COCRDUPC | Credit card update — edit card name, status, expiry with validation, REWRITE card record | 1560 | EXEC CICS READ/REWRITE (CARDDATA, CARDXREF), EXEC CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | COTRN00C.cbl | COTRN00C | Transaction list — browse/list transactions with scrolling using STARTBR/READNEXT/READPREV | 699 | EXEC CICS STARTBR/READNEXT/READPREV/ENDBR (TRANSACT), EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | COTRN01C | Transaction detail view — displays a single transaction record | 330 | EXEC CICS READ (TRANSACT), EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | COTRN02C | Transaction add — adds new transactions with full validation, reads xref for card→account lookup, writes to TRANSACT VSAM | 783 | EXEC CICS READ (CARDXREF via AIX), EXEC CICS STARTBR/READPREV/ENDBR (TRANSACT), EXEC CICS WRITE (TRANSACT), EXEC CICS SEND/RECEIVE MAP, CALL 'CSUTLDTC' | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | CORPT00C.cbl | CORPT00C | Transaction report request — validates date range input, submits batch report job via CICS TDQ (internal reader) | 649 | EXEC CICS WRITEQ TD (JOBS), EXEC CICS SEND/RECEIVE MAP, CALL 'CSUTLDTC' | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | COBIL00C.cbl | COBIL00C | Bill payment — processes bill payments by reading account data, creating payment transactions, updating balances | 572 | EXEC CICS READ/REWRITE (ACCTDATA, CARDXREF via AIX), EXEC CICS STARTBR/READPREV/ENDBR (TRANSACT), EXEC CICS WRITE (TRANSACT), EXEC CICS ASKTIME/FORMATTIME | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | COUSR00C | User list — browse/list security users with scrolling (STARTBR/READNEXT/READPREV on USRSEC) | 695 | EXEC CICS STARTBR/READNEXT/READPREV/ENDBR (USRSEC), EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | COUSR01C | User add — adds a new user record to the USRSEC security file | 299 | EXEC CICS WRITE (USRSEC), EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | COUSR02C | User update — reads and updates an existing user record in USRSEC | 414 | EXEC CICS READ/REWRITE (USRSEC), EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | COUSR03C | User delete — reads and deletes a user record from USRSEC with confirmation | 359 | EXEC CICS READ/DELETE (USRSEC), EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. COBOL Programs — Sub-Applications

### 2.1 Authorization Sub-App (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Program-ID | Purpose | LOC | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|---------|-----|----------------|---------------------|----------------------|
| 1 | COPAUA0C.cbl | COPAUA0C | Real-time payment authorization engine — reads MQ request, validates card/account/customer via CICS VSAM reads, makes approve/decline decision, writes auth to IMS DB, sends MQ reply | 1026 | Online (CICS+MQ+IMS) | CALL MQOPEN/MQGET/MQPUT1/MQCLOSE; EXEC CICS READ (CARDXREF, ACCTDATA, CUSTDATA); EXEC CICS WRITEQ | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 2 | COPAUS0C.cbl | COPAUS0C | Pending authorization list screen — browse auth summary records via CICS with scrolling | 1032 | Online (CICS) | EXEC CICS READ (CARDXREF via AIX, ACCTDATA, CUSTDATA); EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 3 | COPAUS1C.cbl | COPAUS1C | Authorization detail view/mark-fraud — view individual auth detail, mark as fraudulent with SYNCPOINT | 604 | Online (CICS) | EXEC CICS READ, EXEC CICS SEND/RECEIVE MAP, EXEC CICS SYNCPOINT, EXEC CICS LINK | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 4 | COPAUS2C.cbl | COPAUS2C | Fraud update via DB2 — executes SQL INSERT/UPDATE on authorization fraud records | 244 | Online (CICS+DB2) | EXEC SQL INSERT/UPDATE (AUTHFRDS table), EXEC CICS ASKTIME/FORMATTIME | CIPAUDTY |
| 5 | CBPAUP0C.cbl | CBPAUP0C | Purge expired pending authorizations — batch IMS DL/I process to find and delete expired auth summary/detail segments | 386 | Batch (IMS DL/I) | IMS GN/GNP/DLET calls via CBLTDLI (PAUT DB) | CIPAUSMY, CIPAUDTY |
| 6 | PAUDBLOD.CBL | PAUDBLOD | Load IMS auth database from sequential files — reads flat files, inserts root and child segments into IMS PAUT database | 369 | Batch (IMS DL/I) | READ INFILE1/INFILE2 (PS); CALL CBLTDLI ISRT/GU (IMS DB) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 7 | PAUDBUNL.CBL | PAUDBUNL | Unload IMS auth database to sequential files — traverses IMS PAUT DB, writes root/child segments to flat files | 317 | Batch (IMS DL/I) | CALL CBLTDLI GN/GNP (IMS DB); WRITE OPFILE1/OPFILE2 (PS) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | DBUNLDGS.CBL | DBUNLDGS | Unload IMS auth database to GSAM — similar to PAUDBUNL but outputs to GSAM segments | 366 | Batch (IMS DL/I+GSAM) | CALL CBLTDLI GN/GNP (IMS DB); CALL CBLTDLI ISRT (GSAM) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 Transaction Type DB2 Sub-App (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Program-ID | Purpose | LOC | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|---------|-----|----------------|---------------------|----------------------|
| 1 | COTRTLIC.cbl | COTRTLIC | Transaction type list — browse DB2 TRANSACTION_TYPE table with cursor, supports list/select/delete via CICS screens | 2098 | Online (CICS+DB2) | EXEC SQL DECLARE/OPEN/FETCH/CLOSE CURSOR; EXEC SQL DELETE; EXEC CICS SEND/RECEIVE MAP, EXEC CICS XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, DCLTRTYP (via INCLUDE) |
| 2 | COTRTUPC.cbl | COTRTUPC | Transaction type update — view/add/update/delete transaction type records in DB2 with full field validation | 1702 | Online (CICS+DB2) | EXEC SQL SELECT/INSERT/UPDATE/DELETE (TRANSACTION_TYPE, TRANSACTION_CATEGORY); EXEC CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT (via INCLUDE) |
| 3 | COBTUPDT.cbl | COBTUPDT | Batch DB2 maintenance — reads a sequential file of insert/update/delete commands and applies them to DB2 TRANSACTION_TYPE table | 237 | Batch (DB2) | READ TR-RECORD (PS); EXEC SQL INSERT/UPDATE/DELETE (TRANSACTION_TYPE) | DCLTRTYP (via SQL INCLUDE) |

### 2.3 VSAM-MQ Sub-App (`app/app-vsam-mq/cbl/`)

| # | Filename | Program-ID | Purpose | LOC | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|---------|-----|----------------|---------------------|----------------------|
| 1 | COACCT01.cbl | COACCT01 | Account inquiry via MQ — receives account inquiry request from MQ queue, reads VSAM account file, sends reply/error via MQ | 620 | Online (CICS+MQ) | CALL MQOPEN/MQGET/MQPUT/MQCLOSE; EXEC CICS READ (ACCTDATA); EXEC CICS RETRIEVE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 2 | CODATE01.cbl | CODATE01 | Date inquiry via MQ — receives date format request from MQ queue, processes date conversion, sends reply via MQ | 524 | Online (CICS+MQ) | CALL MQOPEN/MQGET/MQPUT/MQCLOSE; EXEC CICS RETRIEVE, EXEC CICS ASKTIME/FORMATTIME | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. JCL Job Catalog (`app/jcl/`)

### 3.1 VSAM File Definition / Load Jobs

| # | JCL File | Purpose | Step Sequence | Key Datasets |
|---|----------|---------|---------------|--------------|
| 1 | ACCTFILE.jcl | Define and load Account VSAM KSDS from sequential PS | STEP05 (IDCAMS delete), STEP10 (IDCAMS define), STEP15 (IDCAMS repro) | ACCTDATA.PS → ACCTDATA.VSAM.KSDS |
| 2 | CARDFILE.jcl | Define and load Card VSAM KSDS with AIX from sequential PS | CLCIFIL (SDSF close), STEP05-15 (IDCAMS delete/define/repro), STEP40-60 (IDCAMS AIX define/build/path), OPCIFIL (SDSF open) | CARDDATA.PS → CARDDATA.VSAM.KSDS |
| 3 | CUSTFILE.jcl | Define and load Customer VSAM KSDS from sequential PS | CLCIFIL (SDSF close), STEP05-15 (IDCAMS delete/define/repro), OPCIFIL (SDSF open) | CUSTDATA.PS → CUSTDATA.VSAM.KSDS |
| 4 | XREFFILE.jcl | Define and load Card Cross-Reference VSAM KSDS with AIX from sequential PS | STEP05-15 (IDCAMS delete/define/repro), STEP20-30 (IDCAMS AIX define/build/path) | CARDXREF.PS → CARDXREF.VSAM.KSDS |
| 5 | TRANFILE.jcl | Define and load Transaction VSAM KSDS from initial daily transaction PS | CLCIFIL (SDSF close), STEP05-15 (IDCAMS delete/define/repro), STEP20-30 (IDCAMS AIX define/build/path), OPCIFIL (SDSF open) | DALYTRAN.PS.INIT → TRANSACT.VSAM.KSDS |
| 6 | TRANTYPE.jcl | Define and load Transaction Type VSAM KSDS | STEP05-15 (IDCAMS delete/define/repro) | TRANTYPE.PS → TRANTYPE.VSAM.KSDS |
| 7 | TRANCATG.jcl | Define and load Transaction Category VSAM KSDS | STEP05-15 (IDCAMS delete/define/repro) | TRANCATG.PS → TRANCATG.VSAM.KSDS |
| 8 | TCATBALF.jcl | Define and load Transaction Category Balance VSAM KSDS | STEP05-15 (IDCAMS delete/define/repro) | TCATBALF.PS → TCATBALF.VSAM.KSDS |
| 9 | DISCGRP.jcl | Define and load Disclosure/Discount Group VSAM KSDS | STEP05-15 (IDCAMS delete/define/repro) | DISCGRP.PS → DISCGRP.VSAM.KSDS |
| 10 | TRANIDX.jcl | Define AIX and path for Transaction VSAM KSDS | STEP20-30 (IDCAMS define AIX/build/path) | TRANSACT.VSAM.KSDS (AIX) |
| 11 | REPTFILE.jcl | Define report cluster for generated reports | STEP05 (IDCAMS) | — |
| 12 | DUSRSECJ.jcl | Create and load User Security VSAM KSDS | PREDEL (IEFBR14), STEP01 (IEBGENER create PS), STEP02 (IDCAMS define), STEP03 (IDCAMS repro) | USRSEC.PS → USRSEC.VSAM.KSDS |
| 13 | ESDSRRDS.jcl | Create ESDS and RRDS variants of user security file | PREDEL, STEP01-05 (IEBGENER+IDCAMS) | ESDSRRDS.PS → USRSEC.VSAM.ESDS, USRSEC.VSAM.RRDS |
| 14 | DEFCUST.jcl | Define customer VSAM cluster (alternate definition) | STEP05 (IDCAMS) ×2 | — |
| 15 | DEFGDGB.jcl | Define GDG base entries for backup datasets | STEP05 (IDCAMS) | GDG bases for TRANSACT.BKUP, etc. |
| 16 | DEFGDGD.jcl | Define GDG bases and seed initial generations for TRANTYPE, TRANCATG, DISCGRP backups | STEP10-60 (IDCAMS define + IEBGENER copy) | TRANTYPE.BKUP, TRANCATG.PS.BKUP, DISCGRP.BKUP (GDGs) |
| 17 | DALYREJS.jcl | Define cluster for daily transaction rejects | STEP05 (IDCAMS) | DALYREJS (GDG) |

### 3.2 Batch Processing Jobs

| # | JCL File | Purpose | Step Sequence | Key Datasets |
|---|----------|---------|---------------|--------------|
| 1 | POSTTRAN.jcl | Post daily transactions (core daily batch) | STEP15 (PGM=CBTRN02C) | DALYTRAN.PS, TRANSACT.VSAM.KSDS, CARDXREF.VSAM.KSDS, ACCTDATA.VSAM.KSDS, TCATBALF.VSAM.KSDS, DALYREJS(+1) |
| 2 | INTCALC.jcl | Calculate monthly interest and fees | STEP15 (PGM=CBACT04C) | TCATBALF.VSAM.KSDS, CARDXREF.VSAM.KSDS (+ AIX.PATH), ACCTDATA.VSAM.KSDS, DISCGRP.VSAM.KSDS, SYSTRAN(+1) |
| 3 | TRANREPT.jcl | Generate transaction reports | STEP05R (SORT), STEP10R (PGM=CBTRN03C) | TRANSACT.VSAM.KSDS → sorted → CBTRN03C reads with XREF, TRANTYPE, TRANCATG, DATEPARM → TRANREPT(+1) |
| 4 | CREASTMT.JCL | Create credit card statements (text + HTML) | DELDEF01 (IDCAMS), STEP010 (SORT TRANSACT→SEQ), STEP020 (IDCAMS repro→VSAM), STEP030 (IEFBR14 cleanup), STEP040 (PGM=CBSTM03A) | TRANSACT.VSAM.KSDS → TRXFL.SEQ → TRXFL.VSAM.KSDS; CBSTM03A reads TRXFL+XREF+ACCT+CUST → STATEMNT.PS + STATEMNT.HTML |
| 5 | TRANBKP.jcl | Backup transaction VSAM to GDG | STEP05R (REPRO), STEP05 (IDCAMS delete), STEP10 (IDCAMS repro) | TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1) |
| 6 | COMBTRAN.jcl | Combine system-generated and backed-up transactions | STEP05R (SORT/merge), STEP10 (IDCAMS repro) | SYSTRAN(0) + TRANSACT.BKUP(0) → sorted → TRANSACT.COMBINED(+1) → TRANSACT.VSAM.KSDS |
| 7 | PRTCATBL.jcl | Print/backup category balance file | DELDEF (IEFBR14), STEP05R (REPRO to GDG), STEP10R (SORT to report) | TCATBALF.VSAM.KSDS → TCATBALF.BKUP(+1), TCATBALF.REPT |
| 8 | CBEXPORT.jcl | Run branch migration export | STEP01 (IDCAMS delete old export), STEP02 (PGM=CBEXPORT) | CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA (all VSAM) → EXPORT.DATA |
| 9 | CBIMPORT.jcl | Run branch migration import | STEP01 (PGM=CBIMPORT) | EXPORT.DATA → CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT, TRANSACT.IMPORT, IMPORT.ERRORS |
| 10 | READACCT.jcl | Read/extract account data | PREDEL (IEFBR14), STEP05 (PGM=CBACT01C) | ACCTDATA.VSAM.KSDS → ACCTDATA.PSCOMP, ARRYPS, VBPS |
| 11 | READCARD.jcl | Read/print card data | STEP05 (PGM=CBACT02C) | CARDDATA.VSAM.KSDS |
| 12 | READCUST.jcl | Read/print customer data | STEP05 (PGM=CBCUS01C) | CUSTDATA.VSAM.KSDS |
| 13 | READXREF.jcl | Read/print cross-reference data | STEP05 (PGM=CBACT03C) | CARDXREF.VSAM.KSDS |
| 14 | WAITSTEP.jcl | Execute batch wait/delay | WAIT (PGM=COBSWAIT) | — |
| 15 | TXT2PDF1.JCL | Convert text statement to PDF | TXT2PDF (PGM=IKJEFT1B → TXT2PDF REXX) | STATEMNT.PS → PDF |

### 3.3 Utility/Infrastructure Jobs

| # | JCL File | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | CLOSEFIL.jcl | Close CICS files for batch window (via SDSF) | CLCIFIL (PGM=SDSF) |
| 2 | OPENFIL.jcl | Re-open CICS files after batch window (via SDSF) | OPCIFIL (PGM=SDSF) |
| 3 | CBADMCDJ.jcl | CICS CSD batch update — define programs/transactions/mapsets in CICS CSD | STEP1 (PGM=DFHCSDUP) |
| 4 | FTPJCL.JCL | FTP file transfer utility | STEP1 (PGM=FTP) |
| 5 | INTRDRJ1.JCL | Internal reader job 1 — backup FTP test file, submit INTRDRJ2 via internal reader | IDCAMS + STEP01 (IEBGENER → INTRDR) |
| 6 | INTRDRJ2.JCL | Internal reader job 2 — secondary backup of FTP test file | IDCAMS |

### 3.4 Sub-Application JCL

| # | JCL File | Location | Purpose | Key Steps |
|---|----------|----------|---------|-----------|
| 1 | CBPAUP0J.jcl | app-authorization-ims-db2-mq/jcl/ | Run expired auth purge batch (CBPAUP0C) | PGM=CBPAUP0C with IMS DL/I |
| 2 | DBPAUTP0.jcl | app-authorization-ims-db2-mq/jcl/ | IMS DB processing for auth data | IMS batch region |
| 3 | LOADPADB.JCL | app-authorization-ims-db2-mq/jcl/ | Load IMS auth database from files (PAUDBLOD) | PGM=PAUDBLOD |
| 4 | UNLDPADB.JCL | app-authorization-ims-db2-mq/jcl/ | Unload IMS auth database to files (PAUDBUNL) | PGM=PAUDBUNL |
| 5 | UNLDGSAM.JCL | app-authorization-ims-db2-mq/jcl/ | Unload IMS auth database to GSAM (DBUNLDGS) | PGM=DBUNLDGS |
| 6 | MNTTRDB2.jcl | app-transaction-type-db2/jcl/ | DB2 transaction type maintenance batch | PGM=COBTUPDT |
| 7 | TRANEXTR.jcl | app-transaction-type-db2/jcl/ | Extract transaction types from DB2 | DB2 utility |
| 8 | CREADB21.jcl | app-transaction-type-db2/jcl/ | Create DB2 tables for transaction types | DB2 DDL |

---

## 4. Control-M Scheduler Pipeline (`app/scheduler/CardDemo.controlm`)

### 4.1 DAILY — Transaction Backup (`DAILY-TransactionBackup`)
Runs: **Every day**
```
CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
```
- Close CICS files → Backup transaction VSAM → Wait → Re-open CICS files

### 4.2 WEEKLY — Transaction Types DB2 Refresh (`WEEKLY-TransactionTypesDBRefresh`)
Runs: **Saturdays**
```
MNTTRDB2
```
- → Then triggers DisclosureGroupsRefresh and TransactionTypesDBRefresh SMART folders

### 4.3 WEEKLY — Disclosure Groups Refresh (SMART Folder)
Runs: **Saturdays** (after MNTTRDB2)
```
CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL
```
- Close files → Reload disclosure group VSAM → Wait → Re-open files

### 4.4 WEEKLY — Transaction Types DB Refresh (SMART Folder)
Runs: **Saturdays** (after MNTTRDB2)
```
TRANEXTR
```
- Extract transaction types from DB2

### 4.5 MONTHLY — Interest Calculation (`MONTHLY-InterestCalculation`)
Runs: **Monthly**
```
CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL
```
- Close files → Calculate interest (CBACT04C) → Combine transactions → Wait → Re-open files

---

## 5. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Core batch programs (`app/cbl/`) | 14 |
| Core online/CICS programs (`app/cbl/`) | 17 |
| Authorization sub-app programs | 8 |
| Transaction Type DB2 sub-app programs | 3 |
| VSAM-MQ sub-app programs | 2 |
| Total JCL jobs (`app/jcl/`) | 38 |
| Sub-application JCL jobs | 8 |
| BMS screen maps | 18 + 2 (sub-app) |
| Copybooks (`app/cpy/`) | 30 |
| Sub-application copybooks | 12 |
| Control-M scheduled pipelines | 5 (3 folders + 2 SMART folders) |
| Total lines of COBOL | ~26,000+ |
