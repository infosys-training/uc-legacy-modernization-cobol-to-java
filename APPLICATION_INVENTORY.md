# APPLICATION INVENTORY — CardDemo COBOL Estate

## Overview

| Metric | Count |
|--------|-------|
| Total COBOL Programs | 44 |
| Main Programs (`app/cbl/`) | 31 |
| Sub-Application Programs | 13 |
| Copybooks | 47 |
| JCL Jobs | 54 (46 main + 8 sub-app) |
| BMS Maps (`app/bms/`) | 16 |
| Assembler Modules (`app/asm/`) | 2 |
| Total Lines of COBOL | ~27,350 |

### Classification Summary

| Classification | Count | % |
|----------------|-------|---|
| Online (CICS) | 21 | 48% |
| Batch | 16 | 36% |
| Sub-App (IMS/DB2/MQ) | 7 | 16% |

---

## 1. Main COBOL Programs — `app/cbl/`

### 1.1 Online (CICS) Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 1 | COSGN00C.cbl | 260 | Sign-on entry point — authenticates users, routes to admin or main menu | Online/CICS | CICS READ (USRSEC), SEND/RECEIVE MAP | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, DFHATTR |
| 2 | COADM01C.cbl | 288 | Admin menu hub — routes to user management and DB2 transaction type screens | Online/CICS | CICS SEND/RECEIVE MAP | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COMEN01C.cbl | 308 | Main menu hub — central navigation to 11 user functions via XCTL | Online/CICS | CICS SEND/RECEIVE MAP | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTVWC.cbl | 941 | Account view — displays account details, card info, customer data (read-only) | Online/CICS | CICS READ (ACCTFILE, CARDXREF, CUSTFILE), SEND MAP, SEND TEXT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 5 | COACTUPC.cbl | 4,236 | Account update — **largest program**; exhaustive field validation (date, SSN, phone, state, ZIP); updates account, customer, and card-xref records | Online/CICS | CICS READ (ACCTFILE, CARDXREF, CUSTFILE), REWRITE (ACCTFILE, CUSTFILE), SEND MAP | CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×39), CSUTLDPY |
| 6 | COCRDLIC.cbl | 1,459 | Credit card list — paginated browse of card records using STARTBR/READNEXT/READPREV | Online/CICS | CICS STARTBR, READNEXT, READPREV, ENDBR (CARDFILE), SEND MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, COCRDLI, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y |
| 7 | COCRDSLC.cbl | 887 | Credit card view — displays card details with account, customer, and XREF lookups | Online/CICS | CICS READ (CARDFILE, CARDXREF), SEND MAP, SEND TEXT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 8 | COCRDUPC.cbl | 1,560 | Credit card update — modifies card records with validation | Online/CICS | CICS READ (CARDFILE), REWRITE (CARDFILE), SEND MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 9 | COTRN00C.cbl | 699 | Transaction list — paginated browse of transactions using STARTBR/READNEXT | Online/CICS | CICS STARTBR, READNEXT, READPREV, ENDBR (TRANSACT), SEND MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | 330 | Transaction view — displays individual transaction detail | Online/CICS | CICS READ (TRANSACT), SEND/RECEIVE MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | 783 | Transaction add — creates new transaction records with date validation | Online/CICS | CICS READ (CARDXREF, ACCTFILE), STARTBR/READPREV/ENDBR (TRANSACT), WRITE (TRANSACT) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | CORPT00C.cbl | 649 | Report request — submits batch JCL (INTRDRJ1/J2) for report generation via internal reader | Online/CICS | CICS WRITEQ TD (internal reader), SEND/RECEIVE MAP | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | COBIL00C.cbl | 572 | Bill payment — processes payments against account balances | Online/CICS | CICS READ (CARDXREF, ACCTFILE), REWRITE (ACCTFILE), STARTBR/READPREV/ENDBR (TRANSACT), WRITE (TRANSACT) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | 695 | User list — paginated browse of security user records | Online/CICS | CICS STARTBR, READNEXT, READPREV, ENDBR (USRSEC), SEND MAP | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | 299 | User add — creates new user security records | Online/CICS | CICS WRITE (USRSEC), SEND/RECEIVE MAP | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, DFHATTR |
| 16 | COUSR02C.cbl | 414 | User update — modifies existing user security records | Online/CICS | CICS READ (USRSEC), REWRITE (USRSEC), SEND/RECEIVE MAP | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | 359 | User delete — removes user security records | Online/CICS | CICS READ (USRSEC), DELETE (USRSEC), SEND/RECEIVE MAP | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 1.2 Batch Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 18 | CBACT01C.cbl | 430 | Read account VSAM file, write to flat files (PS, array, VB formats); calls assembler date formatter | Batch | READ (ACCTFILE), WRITE (OUTFILE, ARRYFILE, VBRCFILE) | CVACT01Y, CODATECN |
| 19 | CBACT02C.cbl | 178 | Read and print card data file | Batch | READ (CARDFILE) | CVACT02Y |
| 20 | CBACT03C.cbl | 178 | Read and print account cross-reference data file | Batch | READ (XREFFILE) | CVACT03Y |
| 21 | CBACT04C.cbl | 652 | **Interest calculator** — reads category balances, cross-references, discount groups, and accounts; calculates and applies interest; writes transaction records | Batch | READ (TCATBALF, XREFFILE, DISCGRP, ACCTFILE), REWRITE (ACCTFILE), WRITE (TRANSACT) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 22 | CBCUS01C.cbl | 178 | Read and print customer data file | Batch | READ (CUSTFILE) | CVCUS01Y |
| 23 | CBTRN01C.cbl | 494 | Post daily transactions — reads daily transaction file, validates against XREF and account | Batch | READ (DALYTRAN, XREFFILE, CARDFILE, ACCTFILE, TRANFILE, CUSTFILE) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 24 | CBTRN02C.cbl | 731 | **Post daily transactions (main)** — validates, rejects invalid transactions, updates account balances and category balances, writes to master transaction file | Batch | READ (DALYTRAN, XREFFILE, ACCTFILE, TCATBALF), WRITE (TRANFILE, DALYREJS, TCATBALF), REWRITE (TCATBALF, ACCTFILE) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 25 | CBTRN03C.cbl | 649 | **Daily transaction report** — generates formatted report with transaction types, categories, page/account/grand totals | Batch | READ (TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM), WRITE (TRANREPT) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 26 | CBSTM03A.CBL | 924 | **Statement generation (driver)** — generates customer statements in text and HTML format; calls CBSTM03B for I/O | Batch | WRITE (STMTFILE, HTMLFILE), CALL CBSTM03B (×13) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 27 | CBSTM03B.CBL | 230 | Statement generation (I/O submodule) — handles all file reads for statement generation | Batch | READ (TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE) | *(none — uses caller's data areas)* |
| 28 | CBEXPORT.cbl | 582 | **Data export** — exports customer, account, card, XREF, and transaction records to a multi-record export file for branch migration | Batch | READ (CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE), WRITE (EXPFILE) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 29 | CBIMPORT.cbl | 487 | **Data import** — reads multi-record export file and splits into normalized target files with validation; writes errors to reject file | Batch | READ (EXPFILE), WRITE (CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 30 | CSUTLDTC.cbl | 157 | Date utility — validates and converts dates using LE CEEDAYS function | Utility | CALL CEEDAYS | *(none)* |
| 31 | COBSWAIT.cbl | 41 | Wait routine — calls assembler MVSWAIT for timed delay | Utility | CALL MVSWAIT | *(none)* |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module — `app/app-authorization-ims-db2-mq/cbl/`

Programs in this sub-application deal with pending authorization management using IMS hierarchical database, DB2 relational database, and MQ messaging.

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 32 | COPAUA0C.cbl | 1,026 | **Authorization decision engine** — receives MQ authorization requests, looks up account/customer via CICS VSAM + IMS, applies fraud rules, sends MQ response; spans CICS+IMS+DB2+MQ | Online/CICS+IMS+MQ+DB2 | MQ (MQOPEN, MQGET, MQPUT1, MQCLOSE), CICS READ (CARDXREF, ACCTFILE, CUSTFILE), CICS WRITEQ, IMS DL/I | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | COPAUS0C.cbl | 1,032 | Pending authorization summary — CICS browse of pending authorizations with IMS lookup | Online/CICS+IMS | CICS READ (CARDXREF, ACCTFILE, CUSTFILE), CICS SEND/RECEIVE MAP, IMS DL/I (GU, GNP) | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | COPAUS1C.cbl | 604 | Pending authorization detail — displays and updates individual authorization records via IMS | Online/CICS+IMS | CICS SEND/RECEIVE MAP, IMS DL/I (GU, GNP, REPL) | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS2C.cbl | 244 | Fraud flag update — marks authorizations as fraudulent via DB2 INSERT | Online/CICS+DB2 | EXEC SQL (INSERT, SELECT, UPDATE, DECLARE CURSOR) | DFHBMSCA, CIPAUDTY |
| 36 | CBPAUP0C.cbl | 386 | Expired authorization purge — batch IMS program that deletes expired pending authorizations | Batch/IMS | IMS DL/I (GN, GNP, DLET) | CIPAUSMY, CIPAUDTY |
| 37 | PAUDBLOD.CBL | 369 | IMS database load — loads pending authorization data into IMS database | Batch/IMS | READ (INFILE1, INFILE2), IMS DL/I (ISRT, GU) via CBLTDLI | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 38 | PAUDBUNL.CBL | 317 | IMS database unload — extracts pending authorization data from IMS to flat files | Batch/IMS | IMS DL/I (GN, GNP) via CBLTDLI, WRITE (OUTFIL1, OUTFIL2) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | DBUNLDGS.CBL | 366 | GSAM unload — extracts IMS data via GSAM (Generalized Sequential Access Method) to flat files | Batch/IMS | IMS DL/I via CBLTDLI, WRITE (GSAM output) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 Transaction Type Module — `app/app-transaction-type-db2/cbl/`

Programs manage transaction type reference data stored in DB2 tables.

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 40 | COTRTLIC.cbl | 2,098 | Transaction type list — cursor-based paginated browse of DB2 TRNTYPE/TRNCAT tables | Online/CICS+DB2 | EXEC SQL (DECLARE CURSOR, OPEN, FETCH, CLOSE), CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSDB2RWY, SQLCA, DCLTRTYP |
| 41 | COTRTUPC.cbl | 1,702 | Transaction type update — CRUD operations on DB2 transaction type/category with cascading deletes | Online/CICS+DB2 | EXEC SQL (SELECT, INSERT, UPDATE, DELETE), CICS SEND/RECEIVE MAP | CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, DCLTRTYP, DCLTRCAT |
| 42 | COBTUPDT.cbl | 237 | Batch transaction type update — batch DB2 utility for bulk transaction type maintenance | Batch/DB2 | EXEC SQL (DECLARE CURSOR, OPEN, FETCH, UPDATE, CLOSE) | DCLTRTYP |

### 2.3 VSAM-MQ Module — `app/app-vsam-mq/cbl/`

Programs provide MQ-based inquiry services against VSAM data.

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | 620 | Account inquiry via MQ — receives MQ request, reads VSAM account file, sends MQ response | Online/MQ | MQ (MQOPEN ×3, MQGET, MQPUT ×2, MQCLOSE ×3), CICS READ (ACCTFILE) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 44 | CODATE01.cbl | 524 | Date inquiry via MQ — receives MQ request, returns formatted date/time via MQ response | Online/MQ | MQ (MQOPEN ×3, MQGET, MQPUT ×2, MQCLOSE ×3) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. BMS Maps — `app/bms/`

Each BMS map defines a 3270 terminal screen layout paired with a CICS program.

| # | BMS Map | Paired Program | Screen Purpose |
|---|---------|---------------|----------------|
| 1 | COSGN00.bms | COSGN00C | Sign-on screen |
| 2 | COADM01.bms | COADM01C | Admin menu |
| 3 | COMEN01.bms | COMEN01C | Main menu |
| 4 | COACTUP.bms | COACTUPC | Account update form |
| 5 | COACTVW.bms | COACTVWC | Account view display |
| 6 | COCRDLI.bms | COCRDLIC | Credit card list |
| 7 | COCRDSL.bms | COCRDSLC | Credit card detail view |
| 8 | COCRDUP.bms | COCRDUPC | Credit card update form |
| 9 | COTRN00.bms | COTRN00C | Transaction list |
| 10 | COTRN01.bms | COTRN01C | Transaction detail view |
| 11 | COTRN02.bms | COTRN02C | Transaction add form |
| 12 | CORPT00.bms | CORPT00C | Report request screen |
| 13 | COBIL00.bms | COBIL00C | Bill payment screen |
| 14 | COUSR00.bms | COUSR00C | User list |
| 15 | COUSR01.bms | COUSR01C | User add form |
| 16 | COUSR02.bms | COUSR02C | User update form |
| 17 | COUSR03.bms *(implied)* | COUSR03C | User delete confirmation |

Sub-application BMS maps:

| # | BMS Map | Location | Paired Program |
|---|---------|----------|----------------|
| 18 | COPAU00.bms | app-authorization-ims-db2-mq/bms/ | COPAUS0C |
| 19 | COPAU01.bms | app-authorization-ims-db2-mq/bms/ | COPAUS1C |
| 20 | COTRTLI.bms | app-transaction-type-db2/bms/ | COTRTLIC |
| 21 | COTRTUP.bms | app-transaction-type-db2/bms/ | COTRTUPC |

---

## 4. JCL Job Catalog — `app/jcl/`

### 4.1 VSAM Cluster Definition Jobs

These jobs use IDCAMS to delete, define, and load VSAM KSDS clusters from PS (Physical Sequential) flat files.

| # | JCL Job | Steps | Purpose | Datasets Managed |
|---|---------|-------|---------|-----------------|
| 1 | ACCTFILE.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load account VSAM KSDS | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS (keys=11, recsize=300) |
| 2 | CARDFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→STEP40→STEP50→STEP60→OPCIFIL | Close CICS files, delete/define/load card VSAM KSDS + AIX (alternate index), reopen CICS files | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS (keys=16, recsize=150) + AIX |
| 3 | CUSTFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→OPCIFIL | Close CICS, delete/define/load customer VSAM KSDS, reopen | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS |
| 4 | XREFFILE.jcl | STEP05→STEP10→STEP15→STEP20→STEP25→STEP30 | Delete/define/load card cross-reference KSDS + AIX | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS |
| 5 | TRANFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→STEP20→STEP25→STEP30→OPCIFIL | Delete/define/load transaction KSDS + AIX, manage CICS files | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS |
| 6 | DISCGRP.jcl | STEP05→STEP10→STEP15 | Delete/define/load disclosure group VSAM | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS |
| 7 | TCATBALF.jcl | STEP05→STEP10→STEP15 | Delete/define/load transaction category balance VSAM | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS |
| 8 | TRANTYPE.jcl | STEP05→STEP10→STEP15 | Delete/define/load transaction type VSAM | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS |
| 9 | TRANCATG.jcl | STEP05→STEP10→STEP15 | Delete/define/load transaction category VSAM | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS |
| 10 | REPTFILE.jcl | STEP05 | Delete/define report output VSAM | AWS.M2.CARDDEMO.REPTFILE |
| 11 | DALYREJS.jcl | STEP05 | Delete daily rejects dataset | AWS.M2.CARDDEMO.DALYREJS |
| 12 | TRANIDX.jcl | STEP20→STEP25→STEP30 | Define AIX and PATH for transaction file | AWS.M2.CARDDEMO.TRANSACT.VSAM.AIX |

### 4.2 Batch Processing Jobs

| # | JCL Job | Steps | Program Executed | Purpose | Input Datasets | Output Datasets |
|---|---------|-------|-----------------|---------|---------------|----------------|
| 13 | POSTTRAN.jcl | STEP15 | CBTRN02C | Post daily transactions | DALYTRAN, XREFFILE, ACCTFILE, TCATBALF | TRANFILE, DALYREJS |
| 14 | INTCALC.jcl | STEP15 | CBACT04C | Calculate interest on accounts | TCATBALF, XREFFILE, ACCTFILE, DISCGRP | TRANSACT |
| 15 | CREASTMT.JCL | DELDEF01→STEP010→STEP020→STEP030→STEP040 | SORT → IDCAMS → IEFBR14 → CBSTM03A | Generate customer statements (sort transactions, load VSAM, delete old output, run statement gen) | TRANSACT.VSAM.KSDS, XREFFILE, ACCTFILE, CUSTFILE | STMTFILE, HTMLFILE |
| 16 | TRANREPT.jcl | STEP05R→STEP10R | SORT → CBTRN03C | Sort transactions then generate daily transaction report | TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM | TRANREPT |
| 17 | CBEXPORT.jcl | STEP01→STEP02 | IDCAMS → CBEXPORT | Delete old export, run data export for branch migration | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | EXPFILE |
| 18 | CBIMPORT.jcl | STEP01 | CBIMPORT | Import data from branch migration export | EXPFILE | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT |
| 19 | READACCT.jcl | PREDEL→STEP05 | IEFBR14 → CBACT01C | Delete old output, read account file | ACCTFILE | OUTFILE, ARRYFILE, VBRCFILE |
| 20 | READCARD.jcl | STEP05 | CBACT02C | Read and print card data | CARDFILE | SYSOUT |
| 21 | READCUST.jcl | STEP05 | CBCUS01C | Read and print customer data | CUSTFILE | SYSOUT |
| 22 | READXREF.jcl | STEP05 | CBACT03C | Read and print cross-reference data | XREFFILE | SYSOUT |
| 23 | PRTCATBL.jcl | DELDEF→STEP05R→STEP10R | IEFBR14 → SORT (via REPROC) → SORT | Print transaction category balances | TCATBALF | Sorted output |
| 24 | COMBTRAN.jcl | STEP05R→STEP10 | SORT → IDCAMS | Combine and sort transactions, load into VSAM | TRANSACT → TRANVSAM | TRANSACT.VSAM.KSDS |
| 25 | TRANBKP.jcl | STEP05R→STEP05→STEP10 | REPROC → IDCAMS (×2) | Backup transaction data | TRANSACT | Backup dataset |
| 26 | WAITSTEP.jcl | WAIT | COBSWAIT | Timed wait step for scheduling | — | — |

### 4.3 Infrastructure / Utility Jobs

| # | JCL Job | Purpose |
|---|---------|---------|
| 27 | CLOSEFIL.jcl | Close CICS files via SDSF command |
| 28 | OPENFIL.jcl | Open CICS files via SDSF command |
| 29 | DUSRSECJ.jcl | Create and load user security VSAM KSDS from inline data |
| 30 | ESDSRRDS.jcl | Create ESDS and RRDS VSAM clusters, load from PS |
| 31 | DEFCUST.jcl | Define customer-related VSAM clusters |
| 32 | DEFGDGB.jcl | Define GDG (Generation Data Group) base entries |
| 33 | DEFGDGD.jcl | Backup reference data (TRANTYPE, TRANCATG, DISCGRP) to GDG generations via IEBGENER |
| 34 | CBADMCDJ.jcl | Install CSD (CICS System Definition) entries via DFHCSDUP |
| 35 | FTPJCL.JCL | FTP file transfer utility |
| 36 | INTRDRJ1.JCL | Internal reader job 1 — IDCAMS backup + submit INTRDRJ2 via internal reader |
| 37 | INTRDRJ2.JCL | Internal reader job 2 — IDCAMS backup (chained from INTRDRJ1) |
| 38 | TXT2PDF1.JCL | Convert text statement files to PDF via TXT2PDF REXX exec |

### 4.4 Sub-Application JCL Jobs

#### Authorization Module — `app/app-authorization-ims-db2-mq/jcl/`

| # | JCL Job | Steps | Program Executed | Purpose |
|---|---------|-------|-----------------|---------|
| 39 | CBPAUP0J.jcl | STEP01 | DFSRRC00 → CBPAUP0C | Run IMS BMP to purge expired authorizations |
| 40 | DBPAUTP0.jcl | STEPDEL→UNLOAD | IEFBR14 → DFSRRC00 | Delete old unload, then IMS unload of DBPAUTP0 |
| 41 | LOADPADB.JCL | STEP01 | DFSRRC00 → PAUDBLOD | Load IMS pending authorization database |
| 42 | UNLDGSAM.JCL | STEP01 | DFSRRC00 → DBUNLDGS | Unload IMS database via GSAM |
| 43 | UNLDPADB.JCL | STEP0→STEP01 | IEFBR14 → DFSRRC00 → PAUDBUNL | Delete old output, then IMS database unload |

#### Transaction Type Module — `app/app-transaction-type-db2/jcl/`

| # | JCL Job | Steps | Purpose |
|---|---------|-------|---------|
| 44 | CREADB21.jcl | FREEPLN→CRCRDDB→LDTTYPE→RUNTEP2→... | Create DB2 objects (free plans, create tables, load transaction types/categories) |
| 45 | MNTTRDB2.jcl | — | DB2 maintenance for transaction type tables |
| 46 | TRANEXTR.jcl | — | Extract transaction data from DB2 |

---

## 5. Assembler Modules — `app/asm/`

| Module | Called By | Purpose |
|--------|----------|---------|
| COBDATFT.asm | CBACT01C | Date formatting utility (converts between date formats) |
| MVSWAIT.asm | COBSWAIT | Wait/delay routine (MVS STIMER WAIT) |

## 6. Other Artifacts

| Directory | Contents |
|-----------|----------|
| `app/cpy-bms/` | 17 BMS-generated copybooks (screen I/O data structures for each BMS map) |
| `app/csd/CARDDEMO.CSD` | CICS System Definition — defines transactions, programs, files, maps |
| `app/ctl/REPROCT.ctl` | Control card for SORT/REPORT procedures |
| `app/proc/REPROC.prc` | JCL procedure for SORT-based reporting |
| `app/proc/TRANREPT.prc` | JCL procedure for transaction report |
| `app/data/EBCDIC/` | 14 EBCDIC-encoded flat files (PS datasets) |
| `app/data/ASCII/` | 9 ASCII-encoded data files (test/development) |
| `app/scheduler/` | Control-M and CA-7 scheduling definitions |
| `app/catlg/LISTCAT.txt` | VSAM catalog listing |
