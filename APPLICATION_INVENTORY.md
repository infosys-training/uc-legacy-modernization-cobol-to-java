# APPLICATION INVENTORY — CardDemo COBOL Estate

> Generated from static analysis of the `uc-legacy-modernization-cobol-to-java` repository.

## Summary

| Metric | Count |
|--------|-------|
| COBOL Programs | 44 |
| Copybooks | 39 |
| JCL Jobs | 38 |
| BMS Maps | 16 |
| Total Lines of COBOL | ~27,350 |
| Online (CICS) Programs | 21 |
| Batch Programs | 16 |
| Sub-App (IMS/DB2/MQ) Programs | 7 |

---

## 1. Programs — `app/cbl/` (31 programs)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | 430 | Read account VSAM file and write to multiple output formats (fixed, array, variable-length) | Batch | READ ACCTFILE (KSDS), WRITE OUT-FILE, WRITE ARRY-FILE, WRITE VBRC-FILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Read and print card data file | Batch | READ CARDFILE (KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Read and print card-account cross-reference file | Batch | READ XREFFILE (KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Interest calculator — compute interest/fees on account balances by transaction category | Batch | READ TCATBAL, READ XREF, READ DISCGRP, READ/REWRITE ACCOUNT, WRITE TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Read and print customer data file | Batch | READ CUSTFILE (KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Export customer data for branch migration — reads all normalized files, creates multi-record export | Batch | READ CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD; WRITE EXPORT-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Import data from branch migration export — split multi-record file into normalized targets with validation | Batch | READ EXPORT-INPUT; WRITE CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD, ERROR outputs | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | 924 | Generate account statements from transaction data in plain text and HTML formats | Batch | 101 I/O ops — READ XREF, CUST, ACCT, TRNX; WRITE STMT-FILE, HTML-FILE | CVACT01Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVACT02Y |
| 9 | CBSTM03B.CBL | 230 | I/O submodule for CBSTM03A — handles file processing for statement reports | Batch (subroutine) | READ/WRITE file operations delegated from CBSTM03A | CVTRA05Y |
| 10 | CBTRN01C.cbl | 494 | Post records from daily transaction file — validate and enrich transactions | Batch | READ DALYTRAN, READ XREF, READ ACCOUNT; OPEN CUSTFILE, CARDFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | 731 | Post daily transactions — validate against XREF, update account balances, write rejects | Batch | READ DALYTRAN, READ XREF, READ/REWRITE ACCOUNT, READ/WRITE/REWRITE TCATBAL, WRITE TRANSACT, WRITE REJECTS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | 649 | Print daily transaction detail report with page/account/grand totals | Batch | READ TRANSACT, READ XREF, READ TRANTYPE, READ TRANCATG, READ DATE-PARMS, WRITE REPORT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COACTUPC.cbl | 4,236 | Account update — accept and process account updates with exhaustive field validation (date, SSN, phone, state, ZIP) | Online (CICS) | EXEC CICS READ/REWRITE ACCTFILE, CUSTFILE, CARDXREF via alternate index | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×38), CSSTRPFY, CSUTLDPY |
| 14 | COACTVWC.cbl | 941 | Account view — display account details, card cross-references, and customer data | Online (CICS) | EXEC CICS READ CARDXREF (alternate index), ACCTFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 15 | COADM01C.cbl | 288 | Admin menu — navigation hub for admin users (routes to user CRUD + DB2 transaction type mgmt) | Online (CICS) | EXEC CICS SEND/RECEIVE MAP, XCTL to admin sub-programs | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | 572 | Bill payment — pay account balance and record payment transaction | Online (CICS) | EXEC CICS READ/REWRITE ACCTFILE, READ CARDXREF, STARTBR/READPREV/ENDBR/WRITE TRANSACT | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | 41 | Utility — wait program (pause in centiseconds via assembler MVSWAIT) | Batch (utility) | CALL 'MVSWAIT' | (none) |
| 18 | COCRDLIC.cbl | 1,459 | List credit cards with paginated browse (STARTBR/READNEXT/READPREV) | Online (CICS) | EXEC CICS STARTBR/READNEXT/READPREV/ENDBR CARDFILE, SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 19 | COCRDSLC.cbl | 887 | Credit card detail view — display card info with account/customer lookup | Online (CICS) | EXEC CICS READ CARDFILE (primary + alternate index) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 20 | COCRDUPC.cbl | 1,560 | Credit card update — edit card details with validation and REWRITE | Online (CICS) | EXEC CICS READ/REWRITE CARDFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 21 | COMEN01C.cbl | 308 | Main menu — navigation hub for regular users (11 menu options, XCTL to sub-programs) | Online (CICS) | EXEC CICS SEND/RECEIVE MAP, XCTL to 11 target programs | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 22 | CORPT00C.cbl | 649 | Report request — submit batch JCL for transaction reports via TDQ (extra partition) | Online (CICS) | EXEC CICS WRITEQ TD (submit JCL), SEND/RECEIVE MAP; CALL 'CSUTLDTC' for date validation | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | 260 | Sign-on screen — authenticate user against USRSEC file, route to admin or main menu | Online (CICS) | EXEC CICS READ USRSEC, XCTL to COADM01C or COMEN01C | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | COTRN00C.cbl | 699 | Transaction list — browse TRANSACT file with forward/backward paging | Online (CICS) | EXEC CICS STARTBR/READNEXT/READPREV/ENDBR TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN01C.cbl | 330 | Transaction view — display single transaction detail | Online (CICS) | EXEC CICS READ TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | 783 | Transaction add — add new transaction with validation, generate ID via STARTBR/READPREV | Online (CICS) | EXEC CICS READ CARDXREF, ACCTFILE; STARTBR/READPREV/ENDBR/WRITE TRANSACT; CALL 'CSUTLDTC' | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | 695 | User list — browse USRSEC file with paginated display | Online (CICS) | EXEC CICS STARTBR/READNEXT/READPREV/ENDBR USRSEC, XCTL to update/delete | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | 299 | User add — add new user to USRSEC security file | Online (CICS) | EXEC CICS WRITE USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR02C.cbl | 414 | User update — modify existing user in USRSEC file | Online (CICS) | EXEC CICS READ/REWRITE USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | 359 | User delete — remove user from USRSEC file | Online (CICS) | EXEC CICS READ/DELETE USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | 157 | Date validation utility — validate CCYYMMDD dates using LE CEEDAYS service | Batch (utility) | CALL 'CEEDAYS' | CSUTLDPY |

---

## 2. Sub-Application Programs

### 2a. `app/app-authorization-ims-db2-mq/cbl/` — Authorization Module (8 programs)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 32 | CBPAUP0C.cbl | 386 | Delete expired pending authorization messages from IMS database | Batch (IMS) | DL/I GN, GNP (read summaries + details), DLET (delete expired) | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | 1,026 | Card authorization decision — read MQ request, look up XREF/ACCT/CUST, write IMS auth record, send MQ reply | Online (CICS+IMS+MQ) | MQOPEN, MQGET, MQPUT1, MQCLOSE; EXEC CICS READ XREF/ACCT/CUST; EXEC CICS WRITEQ; DL/I via IMS | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 34 | COPAUS0C.cbl | 1,032 | Authorization summary view — browse IMS auth summary segments with pagination | Online (CICS+IMS) | EXEC CICS SEND/RECEIVE MAP; DL/I GU, GNP (IMS reads); EXEC CICS READ CARDXREF/ACCT/CUST | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | 604 | Authorization detail view — display single auth record with update capability | Online (CICS+IMS) | EXEC CICS LINK (to COPAUS2C); DL/I GU, GNP, REPL (IMS read/update) | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | 244 | Mark authorization as fraud — insert fraud flag into DB2 AUTHFRDS table | Online (CICS+IMS+DB2) | EXEC SQL SELECT, INSERT, UPDATE (AUTHFRDS table) | CIPAUDTY |
| 37 | DBUNLDGS.CBL | 366 | GSAM unload utility — unload IMS database segments to sequential file | Batch (IMS) | DL/I GN, GNP (read segments), ISRT (GSAM write) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 38 | PAUDBLOD.CBL | 369 | IMS database load — load auth summary and detail segments from flat files | Batch (IMS) | READ INFILE1, READ INFILE2; DL/I ISRT (insert root + child), GU (verify) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | PAUDBUNL.CBL | 317 | IMS database unload — extract auth segments to flat files | Batch (IMS) | DL/I GN, GNP (read all segments); WRITE OPFIL1, OPFIL2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

### 2b. `app/app-transaction-type-db2/cbl/` — Transaction Type Module (3 programs)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 40 | COBTUPDT.cbl | 237 | Batch update of transaction types from flat file input — insert, update, delete via DB2 | Batch (DB2) | READ flat file; EXEC SQL SELECT, INSERT, UPDATE, DELETE on TRANSACTION_TYPE | (inline SQL INCLUDE) |
| 41 | COTRTLIC.cbl | 2,098 | List transaction types with DB2 cursor-based pagination and inline delete | Online (CICS+DB2) | EXEC SQL DECLARE/OPEN/FETCH/CLOSE CURSOR; EXEC SQL DELETE; EXEC CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 42 | COTRTUPC.cbl | 1,702 | Transaction type update/add/delete — full CRUD via DB2 with cascading deletes | Online (CICS+DB2) | EXEC SQL SELECT, INSERT, UPDATE, DELETE (TRANSACTION_TYPE, TRANSACTION_CATEGORY); EXEC CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |

### 2c. `app/app-vsam-mq/cbl/` — VSAM/MQ Module (2 programs)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | 620 | Account inquiry via MQ — receive MQ request, read ACCTFILE, send MQ response | Online (CICS+MQ) | MQOPEN (3 queues), MQGET, MQPUT (2), MQCLOSE (3); EXEC CICS READ ACCTFILE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 44 | CODATE01.cbl | 524 | Date inquiry via MQ — receive MQ request, process date, send MQ response | Online (CICS+MQ) | MQOPEN (3 queues), MQGET, MQPUT (2), MQCLOSE (3); EXEC CICS RETRIEVE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. JCL Job Catalog — `app/jcl/` (33 jobs)

### 3a. VSAM Cluster Definition Jobs

| Job | Purpose | Steps |
|-----|---------|-------|
| ACCTFILE.jcl | Delete/define/load Account VSAM KSDS (keys 11,0, recsize 300) | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO |
| CARDFILE.jcl | Delete/define/load Card Data VSAM KSDS (keys 16,0, recsize 150) + AIX by ACCTID | CLCIFIL: SDSF CLOSE → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → STEP40+: AIX DEFINE/BLDINDEX/PATH |
| CUSTFILE.jcl | Delete/define/load Customer VSAM KSDS | CLCIFIL: CLOSE → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → OPCIFIL: OPEN |
| XREFFILE.jcl | Delete/define/load Card-XREF VSAM KSDS + AIX by ACCTID | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → STEP20-30: AIX DEFINE/BLDINDEX/PATH |
| TRANFILE.jcl | Delete/define/load Transaction VSAM KSDS + AIX | CLCIFIL: CLOSE → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → STEP20+: AIX |
| TRANIDX.jcl | Define alternate indexes on Transaction file | STEP20: DEFINE AIX → STEP25: BLDINDEX → STEP30: DEFINE PATH |
| DISCGRP.jcl | Delete/define/load Disclosure Group reference VSAM | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TCATBALF.jcl | Delete/define/load Transaction Category Balance VSAM | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TRANTYPE.jcl | Delete/define/load Transaction Type reference VSAM | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| TRANCATG.jcl | Delete/define/load Transaction Category reference VSAM | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| DUSRSECJ.jcl | Define User Security VSAM file from flat input | PREDEL → STEP01: IEBGENER → STEP02-03: IDCAMS DEFINE/REPRO |
| DEFCUST.jcl | Alternative customer VSAM cluster definition | STEP05: DELETE → STEP05: DEFINE |
| REPTFILE.jcl | Define report output dataset | STEP05: IDCAMS |
| DALYREJS.jcl | Define daily rejects dataset | STEP05: IDCAMS |
| ESDSRRDS.jcl | Define ESDS/RRDS test clusters | (multiple IDCAMS steps) |

### 3b. Batch Processing Jobs

| Job | Purpose | Steps | Program Executed |
|-----|---------|-------|-----------------|
| POSTTRAN.jcl | Post daily transactions to master file | STEP15: EXEC PGM=CBTRN02C | CBTRN02C |
| INTCALC.jcl | Calculate interest on account balances | STEP15: EXEC PGM=CBACT04C,PARM='2022071800' | CBACT04C |
| TRANREPT.jcl | Generate daily transaction report | STEP05R: SORT → STEP10R: EXEC PGM=CBTRN03C | CBTRN03C |
| CBEXPORT.jcl | Export data for branch migration | STEP01: IDCAMS (alloc) → STEP02: EXEC PGM=CBEXPORT | CBEXPORT |
| CBIMPORT.jcl | Import data from branch migration | STEP01: EXEC PGM=CBIMPORT | CBIMPORT |
| READACCT.jcl | Read and print account data | STEP05: EXEC PGM=CBACT01C | CBACT01C |
| READCARD.jcl | Read and print card data | STEP05: EXEC PGM=CBACT02C | CBACT02C |
| READCUST.jcl | Read and print customer data | STEP05: EXEC PGM=CBCUS01C | CBCUS01C |
| READXREF.jcl | Read and print cross-reference data | STEP05: EXEC PGM=CBACT03C | CBACT03C |
| PRTCATBL.jcl | Print transaction category balances | STEP05R: SORT → STEP05: EXEC PGM=CBACT01C | CBACT01C |
| WAITSTEP.jcl | Wait/delay utility step | WAIT: EXEC PGM=COBSWAIT | COBSWAIT |

### 3c. File Maintenance Jobs

| Job | Purpose | Steps |
|-----|---------|-------|
| CLOSEFIL.jcl | Close CICS-managed files via SDSF | CLCIFIL: EXEC PGM=SDSF |
| OPENFIL.jcl | Open CICS-managed files via SDSF | OPCIFIL: EXEC PGM=SDSF |
| COMBTRAN.jcl | Combine/merge daily transactions into master | STEP05R: SORT → STEP10: IDCAMS REPRO |
| TRANBKP.jcl | Backup transaction file | STEP05R: SORT → STEP05: IDCAMS DELETE → STEP10: IDCAMS REPRO |
| DEFGDGB.jcl | Define GDG base for versioned datasets | STEP05: IDCAMS DEFINE GDG |
| DEFGDGD.jcl | Define GDG data generations | STEP10-50: IDCAMS/IEBGENER steps |
| CBADMCDJ.jcl | CICS resource definitions for CardDemo | STEP1: EXEC PGM=DFHCSDUP |

### 3d. Sub-Application JCL — `app/app-authorization-ims-db2-mq/jcl/` (2 jobs)

| Job | Purpose | Steps |
|-----|---------|-------|
| CBPAUP0J.jcl | Execute IMS batch to delete expired authorizations | STEP01: EXEC PGM=DFSRRC00,PARM='BMP,CBPAUP0C,PSBPAUTB' |
| DBPAUTP0.jcl | Unload IMS database DBPAUTP0 to flat file | STEPDEL: IEFBR14 → UNLOAD: EXEC PGM=DFSRRC00 (DFSURGU0) |

### 3e. Sub-Application JCL — `app/app-transaction-type-db2/jcl/` (3 jobs)

| Job | Purpose | Steps |
|-----|---------|-------|
| CREADB21.jcl | Create DB2 tables (TRANSACTION_TYPE, TRANSACTION_CATEGORY) and indexes | STEP01: EXEC PGM=IKJEFT01 (DSNTEP2) — DDL CREATE TABLE/INDEX |
| MNTTRDB2.jcl | Maintain DB2 transaction type data (insert/update) | STEP01: EXEC PGM=COBTUPDT | COBTUPDT |
| TRANEXTR.jcl | Extract transaction types from DB2 to flat file | STEP01: EXEC PGM=IKJEFT01 (DSNTEP2) — SQL SELECT |

---

## 4. BMS Maps — `app/bms/` (16 maps)

| Map | Associated Program | Screen Purpose |
|-----|--------------------|----------------|
| COSGN00.bms | COSGN00C | Sign-on screen |
| COADM01.bms | COADM01C | Admin menu |
| COMEN01.bms | COMEN01C | Main user menu |
| COACTVW.bms | COACTVWC | Account view |
| COACTUP.bms | COACTUPC | Account update |
| COCRDLI.bms | COCRDLIC | Credit card list |
| COCRDSL.bms | COCRDSLC | Credit card detail view |
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

Sub-application BMS maps (`app/app-authorization-ims-db2-mq/bms/`):

| Map | Associated Program | Screen Purpose |
|-----|--------------------|----------------|
| COPAU00.bms | COPAUS0C | Auth summary view |
| COPAU01.bms | COPAUS1C | Auth detail view |

---

## 5. Data Files — `app/data/`

| File (ASCII name) | EBCDIC Dataset Name | Record Size | Content |
|--------------------|---------------------|-------------|---------|
| acctdata.txt | AWS.M2.CARDDEMO.ACCTDATA.PS | 300 bytes | Account master records |
| carddata.txt | AWS.M2.CARDDEMO.CARDDATA.PS | 150 bytes | Card records |
| custdata.txt | AWS.M2.CARDDEMO.CUSTDATA.PS | 500 bytes | Customer records |
| cardxref.txt | AWS.M2.CARDDEMO.CARDXREF.PS | 50 bytes | Card-account cross-references |
| dailytran.txt | AWS.M2.CARDDEMO.DALYTRAN.PS | 350 bytes | Daily transaction input |
| discgrp.txt | AWS.M2.CARDDEMO.DISCGRP.PS | 50 bytes | Disclosure/interest rate groups |
| tcatbal.txt | AWS.M2.CARDDEMO.TCATBALF.PS | 50 bytes | Transaction category balances |
| trantype.txt | AWS.M2.CARDDEMO.TRANTYPE.PS | 60 bytes | Transaction type reference |
| trancatg.txt | AWS.M2.CARDDEMO.TRANCATG.PS | 60 bytes | Transaction category reference |
