# APPLICATION_INVENTORY.md — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System (Mainframe)
> **Total Programs:** 44 &nbsp;|&nbsp; **Total LOC:** ~27,350 &nbsp;|&nbsp; **Copybooks:** 47 &nbsp;|&nbsp; **JCL Jobs:** 46 &nbsp;|&nbsp; **BMS Maps:** 16

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

### 1.1 Online (CICS) Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 1 | COSGN00C.cbl | 260 | **Sign-On screen** — authenticates users via USRSEC VSAM file; routes admins to COADM01C, regular users to COMEN01C | Online/CICS | EXEC CICS READ (USRSEC), SEND/RECEIVE MAP | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | COADM01C.cbl | 288 | **Admin Menu** — hub for admin functions (user CRUD, DB2 tran-type management); XCTL dispatches to 6 sub-programs | Online/CICS | EXEC CICS XCTL, SEND/RECEIVE MAP | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COMEN01C.cbl | 308 | **Main Menu** — central hub routing to 11 user functions (account, card, transaction, reports, bill pay, auth); heart of online navigation | Online/CICS | EXEC CICS XCTL (11 targets), INQUIRE, SEND/RECEIVE MAP | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTUPC.cbl | 4,236 | **Account Update** — full account/customer editing with exhaustive field validation (date, SSN, phone, state, ZIP); REWRITE to ACCTFILE and CUSTFILE | Online/CICS | EXEC CICS READ (CARDFILE, ACCTFILE, CUSTFILE, XREFFILE), REWRITE (ACCTFILE, CUSTFILE), SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×38), CSSTRPFY, CSUTLDPY |
| 5 | COACTVWC.cbl | 941 | **Account View** — read-only display of account, card, and customer data | Online/CICS | EXEC CICS READ (CARDFILE, ACCTFILE, CUSTFILE), SEND MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 6 | COCRDLIC.cbl | 1,459 | **Credit Card List** — paginated browse of card records using STARTBR/READNEXT/READPREV; dispatches to view/update | Online/CICS | EXEC CICS STARTBR, READNEXT, READPREV, ENDBR (CARDFILE), XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | COCRDSLC.cbl | 887 | **Credit Card View** — detail display of a single card record with customer info | Online/CICS | EXEC CICS READ (CARDFILE, CUSTFILE), SEND MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | COCRDUPC.cbl | 1,560 | **Credit Card Update** — update card attributes (status, embossed name); REWRITE to CARDFILE | Online/CICS | EXEC CICS READ (CARDFILE), REWRITE (CARDFILE), SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | COTRN00C.cbl | 699 | **Transaction List** — paginated browse of transaction records for an account | Online/CICS | EXEC CICS STARTBR, READNEXT, READPREV, ENDBR (TRANSACT), SEND/RECEIVE MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | 330 | **Transaction View** — detail display of a single transaction record | Online/CICS | EXEC CICS READ (TRANSACT), SEND/RECEIVE MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | 783 | **Transaction Add** — create new transaction; validates account/card; WRITE to TRANSACT, updates ACCTFILE balance | Online/CICS | EXEC CICS READ (XREFFILE, ACCTFILE), STARTBR/READPREV/ENDBR (TRANSACT), WRITE (TRANSACT), SEND/RECEIVE MAP; CALL CSUTLDTC | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | CORPT00C.cbl | 649 | **Report Request** — submits batch JCL for transaction/statement reports via CICS WRITEQ TD (internal reader) | Online/CICS | EXEC CICS WRITEQ TD (JOBS), SEND/RECEIVE MAP; CALL CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | COBIL00C.cbl | 572 | **Bill Payment** — process bill payment; reads account, creates transaction, updates balance | Online/CICS | EXEC CICS READ/REWRITE (ACCTFILE), READ (XREFFILE), STARTBR/READPREV/ENDBR (TRANSACT), WRITE (TRANSACT), SEND/RECEIVE MAP | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | 695 | **User List** — paginated browse of user security records | Online/CICS | EXEC CICS STARTBR, READNEXT, READPREV, ENDBR (USRSEC), XCTL, SEND/RECEIVE MAP | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | 299 | **User Add** — create new user security record | Online/CICS | EXEC CICS WRITE (USRSEC), SEND/RECEIVE MAP | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | 414 | **User Update** — modify user security record (password, type) | Online/CICS | EXEC CICS READ/REWRITE (USRSEC), SEND/RECEIVE MAP | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | 359 | **User Delete** — delete user security record after confirmation | Online/CICS | EXEC CICS READ/DELETE (USRSEC), SEND/RECEIVE MAP | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 1.2 Batch Programs

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 18 | CBACT01C.cbl | 430 | **Account File Reader** — reads ACCTFILE VSAM, writes 3 output formats (PS, array, VBR) with date formatting | Batch | READ ACCTFILE; WRITE OUTFILE, ARRYFILE, VBRCFILE; CALL COBDATFT, CEE3ABD | CVACT01Y, CODATECN |
| 19 | CBACT02C.cbl | 178 | **Card File Reader** — reads and prints all card records for diagnostics | Batch | READ CARDFILE; CALL CEE3ABD | CVACT02Y |
| 20 | CBACT03C.cbl | 178 | **Cross-Reference Reader** — reads and prints all card-account XREF records | Batch | READ XREFFILE; CALL CEE3ABD | CVACT03Y |
| 21 | CBACT04C.cbl | 652 | **Interest Calculator** — calculates interest per account using disclosure group rates; updates account balances; writes interest transactions | Batch | READ TCATBALF, XREFFILE, DISCGRP, ACCTFILE; REWRITE ACCTFILE; WRITE TRANSACT; CALL CEE3ABD | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 22 | CBCUS01C.cbl | 178 | **Customer File Reader** — reads and prints all customer records for diagnostics | Batch | READ CUSTFILE; CALL CEE3ABD | CVCUS01Y |
| 23 | CBTRN01C.cbl | 494 | **Daily Transaction Validator** — reads daily transactions, validates against XREF/account; enriches with customer data | Batch | READ DALYTRAN, XREFFILE, ACCTFILE; OPEN CUSTFILE, CARDFILE, TRANSACT; CALL CEE3ABD | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 24 | CBTRN02C.cbl | 731 | **Transaction Poster (POSTTRAN)** — posts daily transactions to master file; updates account balance and TCATBAL; writes rejects | Batch | READ DALYTRAN, XREFFILE, ACCTFILE, TCATBALF; WRITE TRANSACT, DALYREJS; REWRITE ACCTFILE, TCATBALF; CALL CEE3ABD | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 25 | CBTRN03C.cbl | 649 | **Transaction Report (TRANRPT)** — generates daily transaction report with account/page/grand totals | Batch | READ TRANSACT, XREFFILE, TRANTYPE, TRANCATG, DATEPARM; WRITE REPTFILE; CALL CEE3ABD | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 26 | CBSTM03A.CBL | 924 | **Statement Generator (CREASTMT)** — generates customer statements in text + HTML; reads XREF→CUST→ACCT→TRNX chain; highest I/O in estate (~115 WRITE ops) | Batch | OPEN/CLOSE STMT-FILE, HTML-FILE; WRITE (97+ times); CALL CBSTM03B (13×) for file I/O; CALL CEE3ABD | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 27 | CBSTM03B.CBL | 230 | **Statement I/O Submodule** — called by CBSTM03A; handles OPEN/READ/CLOSE for TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | Batch (submodule) | OPEN/READ/CLOSE TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | _(none — uses parameters from caller)_ |
| 28 | CBEXPORT.cbl | 582 | **Data Export** — reads all 5 master files and writes unified export file for branch migration | Batch | READ CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; WRITE EXPFILE; CALL CEE3ABD | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 29 | CBIMPORT.cbl | 487 | **Data Import** — reads export file, validates, and writes to individual output files with error tracking | Batch | READ EXPFILE; WRITE CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT; CALL CEE3ABD | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 30 | COBSWAIT.cbl | 41 | **Wait Utility** — wrapper calling assembler MVSWAIT for timed delays in batch scheduling | Batch (utility) | CALL MVSWAIT | _(none)_ |
| 31 | CSUTLDTC.cbl | 157 | **Date Validation Utility** — converts and validates dates using LE CEEDAYS routine | Batch (utility) | CALL CEEDAYS | _(none)_ |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module — IMS/DB2/MQ (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 32 | COPAUA0C.cbl | 1,026 | **Authorization Decision Engine** — processes auth requests via MQ; validates card/account/customer via CICS; looks up/inserts IMS pending auth records; sends response via MQ | Online/CICS+IMS+MQ | CALL MQOPEN/MQGET/MQPUT1/MQCLOSE; EXEC CICS READ (XREFFILE, ACCTFILE, CUSTFILE); EXEC DLI GU/REPL/ISRT/SCHD/TERM; EXEC CICS WRITEQ | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | COPAUS0C.cbl | 1,032 | **Auth Summary Browse** — CICS screen displaying pending authorization summary; reads IMS segments and VSAM files | Online/CICS+IMS | EXEC CICS READ (CARDFILE, ACCTFILE, CUSTFILE); EXEC DLI GU/GNP/SCHD/TERM; SEND/RECEIVE MAP | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | COPAUS1C.cbl | 604 | **Auth Detail View/Update** — detail display of pending authorization with approve/deny via IMS REPL | Online/CICS+IMS | EXEC CICS LINK; EXEC DLI GU/GNP/REPL; SEND/RECEIVE MAP; EXEC CICS SYNCPOINT | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS2C.cbl | 244 | **Fraud Flag Insert** — inserts fraud flags into DB2 AUTHFRDS table based on denied authorizations | Online/CICS+DB2 | EXEC SQL INSERT/SELECT (AUTHFRDS); EXEC CICS ASKTIME/FORMATTIME | CIPAUDTY |
| 36 | CBPAUP0C.cbl | 386 | **Expired Auth Purge** — batch DL/I program that scans IMS DB, deletes expired pending authorizations | Batch/IMS | EXEC DLI GN/GNP/DLET/CHKP | CIPAUSMY, CIPAUDTY |
| 37 | PAUDBLOD.CBL | 369 | **IMS Database Load** — loads pending authorization IMS database from flat files | Batch/IMS | READ INFILE1, INFILE2; CALL CBLTDLI (ISRT, GU) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 38 | PAUDBUNL.CBL | 317 | **IMS Database Unload** — unloads IMS pending auth DB to flat files for backup | Batch/IMS | CALL CBLTDLI (GN, GNP); WRITE OPFILE1, OPFILE2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | DBUNLDGS.CBL | 366 | **GSAM Unload Utility** — unloads IMS DB via GSAM to sequential files | Batch/IMS | CALL CBLTDLI (GN, GNP, ISRT) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 Transaction Type Module — DB2 (`app/app-transaction-type-db2/cbl/`)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 40 | COTRTLIC.cbl | 2,098 | **Transaction Type List** — CICS paginated browse of DB2 transaction types using cursor-based SQL pagination | Online/CICS+DB2 | EXEC SQL SELECT, OPEN/FETCH/CLOSE CURSOR (TRNTYPE); EXEC CICS SEND/RECEIVE MAP, XCTL, SYNCPOINT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, CSDB2RPY, DCLTRTYP |
| 41 | COTRTUPC.cbl | 1,702 | **Transaction Type Update** — CICS CRUD for transaction types/categories in DB2 with cascading delete; SYNCPOINT after DML | Online/CICS+DB2 | EXEC SQL SELECT/INSERT/UPDATE/DELETE (TRNTYPE, TRNCAT); EXEC CICS SEND/RECEIVE MAP, SYNCPOINT | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT |
| 42 | COBTUPDT.cbl | 237 | **Batch DB2 Loader** — reads flat file of transaction types, inserts/updates DB2 TRNTYPE table | Batch/DB2 | SELECT INPFILE; EXEC SQL INSERT/UPDATE (TRNTYPE); READ TR-RECORD | DCLTRTYP |

### 2.3 VSAM/MQ Module (`app/app-vsam-mq/cbl/`)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | 620 | **Account Inquiry via MQ** — CICS program receiving account inquiry from MQ queue, reads ACCTFILE, sends response | Online/CICS+MQ | CALL MQOPEN/MQGET/MQPUT/MQCLOSE; EXEC CICS READ (ACCTFILE), RETRIEVE, RETURN | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 44 | CODATE01.cbl | 524 | **Date Inquiry via MQ** — CICS program providing date/time services via MQ queue | Online/CICS+MQ | CALL MQOPEN/MQGET/MQPUT/MQCLOSE; EXEC CICS RETRIEVE, ASKTIME, FORMATTIME, RETURN | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. BMS Screen Maps (`app/bms/`)

Each BMS map defines a CICS 3270 terminal screen layout paired 1:1 with an online program.

| BMS Map | Paired Program | Screen Purpose |
|---------|---------------|----------------|
| COSGN00.bms | COSGN00C | Sign-On screen |
| COADM01.bms | COADM01C | Admin Menu |
| COMEN01.bms | COMEN01C | Main Menu |
| COACTUP.bms | COACTUPC | Account Update form |
| COACTVW.bms | COACTVWC | Account View display |
| COCRDLI.bms | COCRDLIC | Credit Card List |
| COCRDSL.bms | COCRDSLC | Credit Card View |
| COCRDUP.bms | COCRDUPC | Credit Card Update |
| COTRN00.bms | COTRN00C | Transaction List |
| COTRN01.bms | COTRN01C | Transaction View |
| COTRN02.bms | COTRN02C | Transaction Add |
| CORPT00.bms | CORPT00C | Report Request |
| COBIL00.bms | COBIL00C | Bill Payment |
| COUSR00.bms | COUSR00C | User List |
| COUSR01.bms | COUSR01C | User Add |
| COUSR02.bms | COUSR02C | User Update |
| COUSR03.bms | COUSR03C | User Delete |

Sub-application BMS maps:

| BMS Map | Directory | Paired Program |
|---------|-----------|---------------|
| COPAU00.bms | app-authorization-ims-db2-mq | COPAUS0C |
| COPAU01.bms | app-authorization-ims-db2-mq | COPAUS1C |
| COTRTLI.bms | app-transaction-type-db2 | COTRTLIC |
| COTRTUP.bms | app-transaction-type-db2 | COTRTUPC |

---

## 4. JCL Job Catalog (`app/jcl/`)

### 4.1 VSAM Cluster Definition Jobs (Data Setup)

| JCL Job | Steps | Purpose | Datasets |
|---------|-------|---------|----------|
| ACCTFILE.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Account KSDS | AWS.M2.CARDDEMO.ACCTDATA.PS → .VSAM.KSDS (KEYS 11 0, RECSIZE 300) |
| CARDFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→STEP40 | Close CICS files, Delete/Define/Load Card KSDS + AIX | AWS.M2.CARDDEMO.CARDDATA.PS → .VSAM.KSDS (KEYS 16 0, RECSIZE 150) |
| CUSTFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→OPCIFIL | Close/Delete/Define/Load Customer KSDS, Reopen | AWS.M2.CARDDEMO.CUSTDATA.PS → .VSAM.KSDS |
| XREFFILE.jcl | STEP05→STEP10→STEP15→STEP20→STEP25 | Delete/Define/Load Card-XREF KSDS + AIX + PATH | AWS.M2.CARDDEMO.CARDXREF.PS → .VSAM.KSDS |
| TRANFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→STEP20 | Close/Delete/Define/Load Transaction Master KSDS | AWS.M2.CARDDEMO.DALYTRAN.PS.INIT → .TRANSACT.VSAM.KSDS |
| TCATBALF.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Transaction Category Balance KSDS | AWS.M2.CARDDEMO.TCATBALF.PS → .VSAM.KSDS |
| DISCGRP.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Disclosure Group KSDS | AWS.M2.CARDDEMO.DISCGRP.PS → .VSAM.KSDS |
| TRANTYPE.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Transaction Type KSDS | AWS.M2.CARDDEMO.TRANTYPE.PS → .VSAM.KSDS |
| TRANCATG.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Transaction Category KSDS | AWS.M2.CARDDEMO.TRANCATG.PS → .VSAM.KSDS |
| TRANIDX.jcl | STEP20→STEP25→STEP30 | Define AIX + PATH on Transaction Master | AWS.M2.CARDDEMO.TRANSACT.VSAM.AIX |

### 4.2 Batch Processing Jobs

| JCL Job | Steps | Purpose | Program Executed | Key Datasets |
|---------|-------|---------|-----------------|--------------|
| POSTTRAN.jcl | STEP15 | Post daily transactions to master | CBTRN02C | DALYTRAN.PS → TRANSACT.VSAM.KSDS, ACCTDATA, TCATBALF, DALYREJS |
| INTCALC.jcl | STEP15 | Calculate interest on balances | CBACT04C (PARM='2022071800') | TCATBALF, XREFFILE, ACCTFILE, DISCGRP → SYSTRAN(+1) |
| CREASTMT.JCL | DELDEF01→STEP010→STEP020→STEP030 | Sort transactions, generate statements | SORT → IDCAMS → IEFBR14 (prep for CBSTM03A) | TRANSACT → TRXFL.SEQ → TRXFL.VSAM → STATEMNT.HTML + .PS |
| TRANREPT.jcl | STEP05R(REPROC)→STEP05R(SORT)→STEP10R | Backup, sort, generate transaction report | REPROC → SORT → CBTRN03C | TRANSACT → .BKUP(+1) → .DALY(+1) → report |
| READACCT.jcl | PREDEL→STEP05 | Read/dump account file | CBACT01C | ACCTDATA.VSAM → .PSCOMP, .ARRYPS, .VBPS |
| READCARD.jcl | STEP05 | Read/dump card file | CBACT02C | CARDDATA.VSAM.KSDS |
| READCUST.jcl | STEP05 | Read/dump customer file | CBCUS01C | CUSTDATA.VSAM.KSDS |
| READXREF.jcl | STEP05 | Read/dump cross-reference file | CBACT03C | CARDXREF.VSAM.KSDS |
| CBEXPORT.jcl | STEP01→STEP02 | Delete old export, export all data | IDCAMS → CBEXPORT | CUST/ACCT/XREF/TRANSACT/CARD → EXPORT.DATA |
| CBIMPORT.jcl | STEP01 | Import data with validation | CBIMPORT | EXPORT.DATA → CUST/ACCT/XREF/TRANSACT.IMPORT + ERRORS |
| WAITSTEP.jcl | WAIT | Timed delay in batch scheduling | COBSWAIT | _(none)_ |
| COMBTRAN.jcl | STEP05R→STEP10 | Combine transaction backup with system tran | SORT → IDCAMS | TRANSACT.BKUP(0) + SYSTRAN(0) → .COMBINED(+1) → .VSAM.KSDS |
| TRANBKP.jcl | STEP05R→STEP05→STEP10 | Backup and clear transaction master | REPROC → IDCAMS (DELETE → DEFINE) | TRANSACT.VSAM → .BKUP(+1), then re-define cluster |
| PRTCATBL.jcl | DELDEF→STEP05R→STEP10R | Print transaction category balance | REPROC → SORT | TCATBALF.VSAM → .BKUP(+1) → .REPT |

### 4.3 Infrastructure/Utility Jobs

| JCL Job | Steps | Purpose |
|---------|-------|---------|
| CLOSEFIL.jcl | CLCIFIL | Close all VSAM files in CICS region via SDSF CEMT commands |
| OPENFIL.jcl | OPCIFIL | Open all VSAM files in CICS region |
| DUSRSECJ.jcl | PREDEL→STEP01→STEP02→STEP03 | Create and load USRSEC VSAM file (user security) |
| ESDSRRDS.jcl | PREDEL→STEP01→STEP02→STEP03→STEP04 | Define ESDS/RRDS test clusters |
| DEFCUST.jcl | STEP05→STEP05 | Define alternate customer file structure |
| DEFGDGB.jcl | STEP05 | Define GDG base entries for backup versioning |
| DEFGDGD.jcl | STEP10→STEP20→STEP30→STEP40→STEP50 | Define DB2 GDG, backup TRANTYPE and TRANCATG |
| DALYREJS.jcl | STEP05 | Define GDG for daily rejects |
| REPTFILE.jcl | STEP05 | Define GDG for report file |
| CBADMCDJ.jcl | STEP1 | CICS CSD batch update (DFHCSDUP) for program/transaction definitions |
| FTPJCL.JCL | STEP1 | FTP job to receive/send files |
| INTRDRJ1.JCL | IDCAMS→STEP01 | Internal reader trigger — submits INTRDRJ2 via SYSOUT INTRDR |
| INTRDRJ2.JCL | IDCAMS | Secondary internal reader job for IMS VSAM setup |
| TXT2PDF1.JCL | TXT2PDF | Convert statement text to PDF format |

### 4.4 Sub-Application JCL Jobs

**Authorization IMS Module** (`app/app-authorization-ims-db2-mq/jcl/`):

| JCL Job | Purpose | Program |
|---------|---------|---------|
| CBPAUP0J.jcl | Execute expired auth purge batch | CBPAUP0C via DFSRRC00 (IMS BMP) |
| DBPAUTP0.jcl | Unload IMS pending auth DB | PAUDBUNL via DFSRRC00 |
| LOADPADB.JCL | Load IMS pending auth DB from files | PAUDBLOD via DFSRRC00 |
| UNLDPADB.JCL | Unload IMS DB to flat files | PAUDBUNL via DFSRRC00 |
| UNLDGSAM.JCL | Unload IMS DB via GSAM | DBUNLDGS via DFSRRC00 |

**Transaction Type DB2 Module** (`app/app-transaction-type-db2/jcl/`):

| JCL Job | Purpose |
|---------|---------|
| CREADB21.jcl | Create/bind DB2 objects (tables, plans, packages) for TRNTYPE/TRNCAT |
| MNTTRDB2.jcl | Maintain DB2 transaction type tables |
| TRANEXTR.jcl | Extract transaction types from DB2 to flat file |

---

## 5. Summary Statistics

| Category | Count |
|----------|-------|
| **Total COBOL Programs** | 44 |
| Online (CICS) | 21 (48%) |
| Batch | 16 (36%) |
| Sub-app (IMS/DB2/MQ) | 7 (16%) |
| **Total Lines of Code** | ~27,350 |
| **Copybooks (app/cpy/)** | 30 |
| **Copybooks (sub-app)** | 17 |
| **Total Copybooks** | 47 |
| **JCL Jobs (app/jcl/)** | 38 |
| **JCL Jobs (sub-app)** | 8 |
| **Total JCL Jobs** | 46 |
| **BMS Maps** | 21 (17 main + 4 sub-app) |
| **VSAM Files** | 9 (ACCTDATA, CARDDATA, CUSTDATA, CARDXREF, TRANSACT, TCATBALF, DISCGRP, TRANTYPE, TRANCATG) |
| **DB2 Tables** | 3 (TRNTYPE, TRNCAT, AUTHFRDS) |
| **IMS Databases** | 2 (DBPAUTP0 root, DBPAUTX0 index) |
| **MQ Queues** | 3 (request, reply, error) |
