# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System (Mainframe)
> **Estate Size:** 44 COBOL programs | 47 copybooks | 38 JCL jobs (main + sub-app) | 16 BMS maps
> **Total LOC:** ~27,350

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

### 1.1 Batch Programs (11 programs)

| # | Filename | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|--------------------|-----------------------|
| 1 | CBACT01C.cbl | 430 | Read account VSAM file, write to PS/array/VB output files for reporting | **R:** ACCTFILE (VSAM KSDS) **W:** OUTFILE (PS), ARRYFILE (PS), VBRCFILE (VB) | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Read and print card data file | **R:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Read and print account cross-reference data file | **R:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Interest calculator — compute interest/fees on account balances | **R:** TCATBALF, XREFFILE, DISCGRP, ACCTFILE **W:** TRANSACT **RW:** ACCTFILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Read and print customer data file | **R:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Export customer data for branch migration — multi-record export file | **R:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE **W:** EXPFILE (VSAM KSDS) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Import customer data from export file — split into normalized target files with validation | **R:** EXPFILE **W:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBTRN01C.cbl | 494 | Post records from daily transaction file (preliminary version) | **R:** DALYTRAN (sequential), XREFFILE, ACCTFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | CBTRN02C.cbl | 731 | Post records from daily transaction file (production version) — update master VSAM | **R:** DALYTRAN, XREFFILE, ACCTFILE, TCATBALF **W:** TRANFILE, DALYREJS **RW:** ACCTFILE, TCATBALF | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | 649 | Print daily transaction detail report | **R:** TRANSACT, XREFFILE, TRANTYPE, TRANCATG, DATE-PARMS **W:** REPTFILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | COBSWAIT.cbl | 41 | Utility — wait for specified centiseconds (calls ASM MVSWAIT) | None (in-memory only) | *(none)* |

### 1.2 Utility / Sub-Module Programs (1 program)

| # | Filename | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|--------------------|-----------------------|
| 12 | CSUTLDTC.cbl | 157 | Date validation utility — calls LE CEEDAYS API | None (called as subprogram) | *(none — uses LINKAGE SECTION)* |

### 1.3 Online (CICS) Programs (17 programs)

| # | Filename | LOC | Purpose | BMS Map | CICS Operations | Copybooks Referenced |
|---|----------|-----|---------|---------|-----------------|----------------------|
| 13 | COSGN00C.cbl | 260 | Sign-on screen — authenticate user, route to admin/user menu | COSGN00 | READ (USRSEC), SEND/RECEIVE MAP, XCTL | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 14 | COADM01C.cbl | 288 | Admin menu hub — route to admin functions (user CRUD, tran type) | COADM01 | SEND/RECEIVE MAP, XCTL | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COMEN01C.cbl | 308 | Main menu hub for regular users — routes to 11 functions | COMEN01 | SEND/RECEIVE MAP, XCTL, INQUIRE | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COACTUPC.cbl | 4,236 | **Account Update** — field-level validation (date, SSN, phone, state, ZIP), read/rewrite accounts + customers | COACTUP | READ/REWRITE (ACCTFILE, CUSTFILE, CARDXREF via AIX), SEND/RECEIVE MAP, XCTL | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY ×13 |
| 17 | COACTVWC.cbl | 941 | Account View — read-only display of account, card, customer data | COACTVW | READ (ACCTFILE, CARDFILE via AIX, CUSTFILE), SEND MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 18 | COCRDLIC.cbl | 1,459 | Credit Card List — paginated browse of cards (STARTBR/READNEXT/READPREV) | COCRDLI | STARTBR, READNEXT, READPREV, ENDBR (CARDFILE), XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 19 | COCRDSLC.cbl | 887 | Credit Card View — display card details | COCRDSL | READ (CARDFILE, CARDFILE via AIX), SEND MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 20 | COCRDUPC.cbl | 1,560 | Credit Card Update — modify card details, rewrite CARDFILE | COCRDUP | READ/REWRITE (CARDFILE), SEND/RECEIVE MAP, XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 21 | COTRN00C.cbl | 699 | Transaction List — paginated browse of transactions (STARTBR/READNEXT) | COTRN00 | STARTBR, READNEXT, READPREV, ENDBR (TRANSACT), XCTL | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 22 | COTRN01C.cbl | 330 | Transaction View — display single transaction detail | COTRN01 | READ (TRANSACT), SEND MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | COTRN02C.cbl | 783 | Transaction Add — enter new transaction, validate, write to TRANSACT | COTRN02 | READ (CARDXREF, ACCTFILE), STARTBR/READPREV/ENDBR + WRITE (TRANSACT), XCTL | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 24 | CORPT00C.cbl | 649 | Report Request — submit batch JCL via TDQ for transaction reports | CORPT00 | WRITEQ TD (submit JCL), SEND/RECEIVE MAP, XCTL | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COBIL00C.cbl | 572 | Bill Payment — pay account balance, create payment transaction | COBIL00 | READ/REWRITE (ACCTFILE), STARTBR/READPREV/ENDBR + WRITE (TRANSACT), READ (CARDXREF), XCTL | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COUSR00C.cbl | 695 | User List — paginated browse of users in USRSEC file | COUSR00 | STARTBR, READNEXT, READPREV, ENDBR (USRSEC), XCTL | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 27 | COUSR01C.cbl | 299 | User Add — create new admin or regular user in USRSEC | COUSR01 | WRITE (USRSEC), SEND/RECEIVE MAP, XCTL | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR02C.cbl | 414 | User Update — modify existing user record in USRSEC | COUSR02 | READ/REWRITE (USRSEC), SEND/RECEIVE MAP, XCTL | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR03C.cbl | 359 | User Delete — delete user from USRSEC file | COUSR03 | READ/DELETE (USRSEC), SEND/RECEIVE MAP, XCTL | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module — IMS/DB2/MQ (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|--------------------|-----------------------|
| 30 | CBPAUP0C.cbl | 386 | Batch (IMS) | Delete expired pending authorization messages from IMS DB | **IMS:** GN, GNP (read), DLET (delete), CHKP (checkpoint) | CIPAUSMY, CIPAUDTY |
| 31 | COPAUA0C.cbl | 1,026 | Online (CICS+IMS+MQ) | Card authorization decision — spans MQ + IMS + CICS | **MQ:** MQOPEN, MQGET, MQPUT1 **IMS:** SCHD, GU, TERM **CICS:** READ (XREF, ACCT, CUST) | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 32 | COPAUS0C.cbl | 1,032 | Online (CICS+IMS) | Authorization summary browse — paginated IMS segment view | **IMS:** GU, GNP, SCHD, TERM **CICS:** READ (ACCTFILE, CARDFILE, CUSTFILE via AIX), SYNCPOINT | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 33 | COPAUS1C.cbl | 604 | Online (CICS+IMS) | Authorization detail view with update capability | **IMS:** GU, GNP, REPL **CICS:** LINK (COPAUS2C), SYNCPOINT, SCHD, TERM | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | COPAUS2C.cbl | 244 | Online (CICS+IMS+DB2) | Mark authorization message as fraud via DB2 INSERT | **DB2:** INSERT INTO fraud table **CICS:** ASKTIME, FORMATTIME, RETURN | CIPAUDTY |

### 2.2 Transaction Type Module — DB2 (`app/app-transaction-type-db2/cbl/`)

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|--------------------|-----------------------|
| 35 | COBTUPDT.cbl | 237 | Batch (DB2) | Batch update transaction types from input file | **DB2:** INSERT, UPDATE, DELETE (TRANSACTION_TYPE) **File:** OPEN/READ/CLOSE (INPFILE) | *(SQL INCLUDE DCLTRTYP)* |
| 36 | COTRTLIC.cbl | 2,098 | Online (CICS+DB2) | Transaction type list — cursor-based DB2 pagination with select/update/delete actions | **DB2:** DECLARE CURSOR, OPEN, FETCH, SELECT, DELETE **CICS:** SEND/RECEIVE MAP, XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 37 | COTRTUPC.cbl | 1,702 | Online (CICS+DB2) | Transaction type update/delete — cascading deletes with confirmation | **DB2:** SELECT, UPDATE, DELETE (TRANSACTION_TYPE, TRANSACTION_CATEGORY) **CICS:** SEND/RECEIVE MAP, XCTL | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |

### 2.3 VSAM/MQ Module (`app/app-vsam-mq/cbl/`)

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|--------------------|-----------------------|
| 38 | COACCT01.cbl | — | Online (CICS+MQ) | Account inquiry via MQ message queue | **MQ:** MQGET/MQPUT **CICS:** READ (ACCTFILE) | *(MQ copybooks)* |
| 39 | CODATE01.cbl | — | Online (CICS+MQ) | Date inquiry via MQ message queue | **MQ:** MQGET/MQPUT | *(MQ copybooks)* |

---

## 3. BMS Screen Maps (`app/bms/`)

| Map File | Associated Program | Screen Purpose |
|----------|-------------------|----------------|
| COSGN00.bms | COSGN00C | Sign-on / Login screen |
| COADM01.bms | COADM01C | Admin menu |
| COMEN01.bms | COMEN01C | Main user menu |
| COACTUP.bms | COACTUPC | Account update form |
| COACTVW.bms | COACTVWC | Account view (read-only) |
| COCRDLI.bms | COCRDLIC | Credit card list (paginated) |
| COCRDSL.bms | COCRDSLC | Credit card view detail |
| COCRDUP.bms | COCRDUPC | Credit card update form |
| COTRN00.bms | COTRN00C | Transaction list (paginated) |
| COTRN01.bms | COTRN01C | Transaction view detail |
| COTRN02.bms | COTRN02C | Transaction add form |
| CORPT00.bms | CORPT00C | Report request form |
| COBIL00.bms | COBIL00C | Bill payment form |
| COUSR00.bms | COUSR00C | User list (paginated) |
| COUSR01.bms | COUSR01C | User add form |
| COUSR02.bms | COUSR02C | User update form |
| COUSR03.bms | COUSR03C | User delete confirmation |

Sub-application BMS maps:

| Map File | Location | Associated Program | Screen Purpose |
|----------|----------|--------------------|----------------|
| COPAU00.bms | app-authorization-ims-db2-mq/bms/ | COPAUS0C | Authorization summary browse |
| COPAU01.bms | app-authorization-ims-db2-mq/bms/ | COPAUS1C | Authorization detail view |
| COTRTLI.bms | app-transaction-type-db2/bms/ | COTRTLIC | Transaction type list |
| COTRTUP.bms | app-transaction-type-db2/bms/ | COTRTUPC | Transaction type update |

---

## 4. JCL Job Catalog

### 4.1 Main JCL Jobs (`app/jcl/`) — 33 jobs

#### Data Setup — VSAM Cluster Definition (IDCAMS)

| Job | LOC | Purpose | Steps | Key Datasets |
|-----|-----|---------|-------|--------------|
| ACCTFILE.jcl | 65 | Delete/define/load Account VSAM KSDS | STEP05 (DELETE), STEP10 (DEFINE CLUSTER), STEP15 (REPRO) | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS ← .PS |
| CARDFILE.jcl | 128 | Delete/define/load Card VSAM KSDS + AIX + PATH | CLCIFIL (close CICS), STEP05 (DELETE), STEP10 (DEFINE), STEP15 (REPRO), STEP40 (DEFINE AIX), STEP50 (DEFINE PATH), STEP60 (BLDINDEX), OPCIFIL (open CICS) | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS + .AIX |
| CUSTFILE.jcl | 84 | Delete/define/load Customer VSAM KSDS | CLCIFIL, STEP05 (DELETE), STEP10 (DEFINE), STEP15 (REPRO), OPCIFIL | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS ← .PS |
| XREFFILE.jcl | — | Delete/define/load Card-Account XREF VSAM KSDS | DELETE, DEFINE, REPRO | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS |
| DISCGRP.jcl | 65 | Delete/define/load Disclosure Group VSAM KSDS | STEP05 (DELETE), STEP10 (DEFINE), STEP15 (REPRO) | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS ← .PS |
| TCATBALF.jcl | 65 | Delete/define/load Transaction Category Balance VSAM KSDS | STEP05 (DELETE), STEP10 (DEFINE), STEP15 (REPRO) | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS ← .PS |
| TRANTYPE.jcl | — | Delete/define/load Transaction Type VSAM KSDS | DELETE, DEFINE, REPRO | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS |
| TRANCATG.jcl | — | Delete/define/load Transaction Category VSAM KSDS | DELETE, DEFINE, REPRO | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS |
| TRANFILE.jcl | — | Delete/define/load Transaction Master VSAM KSDS | DELETE, DEFINE, REPRO | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS |
| TRANIDX.jcl | — | Define AIX + PATH for Transaction file | DEFINE AIX, DEFINE PATH, BLDINDEX | AWS.M2.CARDDEMO.TRANSACT.VSAM.AIX |
| DEFCUST.jcl | 47 | Legacy cluster definition (alternate naming) | STEP05 (DELETE), STEP05 (DEFINE) | AWS.CCDA.CUSTDATA.CLUSTER |
| DUSRSECJ.jcl | 92 | Create User Security VSAM KSDS from in-stream data | PREDEL (delete PS), STEP01 (create PS via IEBGENER), STEP02 (DEFINE KSDS), STEP03 (REPRO) | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS |
| ESDSRRDS.jcl | 124 | Define ESDS + RRDS variants of user security file (demo) | PREDEL, STEP01 (IEBGENER), STEP02 (DEFINE ESDS), STEP03 (REPRO ESDS), STEP04 (DEFINE RRDS), STEP05 (REPRO RRDS) | .USRSEC.VSAM.ESDS, .USRSEC.VSAM.RRDS |

#### GDG (Generation Data Group) Definition

| Job | LOC | Purpose | Steps | GDGs Defined |
|-----|-----|---------|-------|-------------|
| DEFGDGB.jcl | 63 | Define GDG bases for batch pipeline | STEP05 (IDCAMS ×6 DEFINE GDG) | TRANSACT.BKUP, TRANSACT.DALY, TRANREPT, TCATBALF.BKUP, SYSTRAN, TRANSACT.COMBINED |
| DEFGDGD.jcl | 94 | Define GDG bases + first generations for DB2 reference data | STEP10-60 (DEFINE GDG + IEBGENER) | TRANTYPE.BKUP, TRANCATG.PS.BKUP, DISCGRP.BKUP |
| DALYREJS.jcl | 32 | Define GDG for daily rejects | STEP05 (DEFINE GDG) | DALYREJS |
| REPTFILE.jcl | 32 | Define GDG for report files | STEP05 (DEFINE GDG) | TRANREPT |

#### Batch Processing

| Job | LOC | Purpose | Steps | Program Executed | Key I/O |
|-----|-----|---------|-------|-----------------|---------|
| POSTTRAN.jcl | 45 | Post daily transactions to master | STEP15 (EXEC PGM=CBTRN02C) | CBTRN02C | R: DALYTRAN, XREFFILE, ACCTFILE, TCATBALF W: TRANFILE, DALYREJS |
| INTCALC.jcl | 44 | Interest calculation on accounts | STEP15 (EXEC PGM=CBACT04C, PARM date) | CBACT04C | R: TCATBALF, XREFFILE, ACCTFILE, DISCGRP W: TRANSACT(SYSTRAN) |
| TRANREPT.jcl | — | Generate transaction detail report | STEP (EXEC PGM=CBTRN03C) | CBTRN03C | R: TRANSACT, XREFFILE, TRANTYPE, TRANCATG W: REPTFILE |
| CBEXPORT.jcl | 72 | Export data for branch migration | STEP01 (DEFINE export cluster), STEP02 (EXEC PGM=CBEXPORT) | CBEXPORT | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE W: EXPFILE |
| CBIMPORT.jcl | 68 | Import data from export file | STEP01 (EXEC PGM=CBIMPORT) | CBIMPORT | R: EXPFILE W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT |
| READACCT.jcl | 50 | Read/print account master | PREDEL, STEP05 (EXEC PGM=CBACT01C) | CBACT01C | R: ACCTFILE W: OUTFILE, ARRYFILE, VBRCFILE |
| READCARD.jcl | 31 | Read/print card master | STEP05 (EXEC PGM=CBACT02C) | CBACT02C | R: CARDFILE |
| READCUST.jcl | 30 | Read/print customer master | STEP05 (EXEC PGM=CBCUS01C) | CBCUS01C | R: CUSTFILE |
| READXREF.jcl | 31 | Read/print cross-reference master | STEP05 (EXEC PGM=CBACT03C) | CBACT03C | R: XREFFILE |
| WAITSTEP.jcl | — | Utility — wait step for job scheduling | STEP (EXEC PGM=COBSWAIT) | COBSWAIT | *(none)* |
| PRTCATBL.jcl | 66 | Print transaction category balance | DELDEF, STEP05R (PROC=REPROC), STEP10R (SORT) | *(SORT utility)* | R: TCATBALF W: TCATBALF.REPT |

#### CICS File Management

| Job | LOC | Purpose | Steps |
|-----|-----|---------|-------|
| CLOSEFIL.jcl | 34 | Close VSAM files in CICS region | CLCIFIL (SDSF: close TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC) |
| OPENFIL.jcl | 34 | Open VSAM files in CICS region | OPCIFIL (SDSF: open TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC) |
| CBADMCDJ.jcl | 167 | Define CICS CSD resources for CardDemo | STEP1 (DFHCSDUP): DEFINE mapsets, programs, transactions for entire CardDemo app |

#### Transaction Consolidation

| Job | LOC | Purpose | Steps |
|-----|-----|---------|-------|
| COMBTRAN.jcl | 52 | Sort and combine transaction backup + system-generated transactions | STEP05R (SORT by TRAN-ID), STEP10 (REPRO into TRANSACT VSAM) |
| TRANBKP.jcl | 71 | Backup transaction master, then delete/reload | REPRO (backup), DELETE/DEFINE/REPRO (reload) |

### 4.2 Sub-Application JCL

#### Authorization Module (`app/app-authorization-ims-db2-mq/jcl/`)

| Job | LOC | Purpose | Steps |
|-----|-----|---------|-------|
| CBPAUP0J.jcl | 46 | Execute IMS program to delete expired authorizations | STEP01 (EXEC PGM=DFSRRC00 → CBPAUP0C) |
| DBPAUTP0.jcl | 47 | Unload IMS database DBPAUTP0 | STEPDEL (delete output), UNLOAD (EXEC PGM=DFSRRC00) |

#### Transaction Type Module (`app/app-transaction-type-db2/jcl/`)

| Job | LOC | Purpose | Steps |
|-----|-----|---------|-------|
| CREADB21.jcl | — | Create DB2 tables for transaction types | DDL execution |
| MNTTRDB2.jcl | — | Maintain transaction type DB2 data | Execute COBTUPDT batch update |
| TRANEXTR.jcl | — | Extract transaction type data from DB2 | DB2 UNLOAD utility |

---

## 5. Summary Statistics

| Category | Count | % of Estate |
|----------|-------|-------------|
| Online (CICS) programs | 21 | 48% |
| Batch programs | 16 | 36% |
| Sub-app (IMS/DB2/MQ) | 7 | 16% |
| **Total programs** | **44** | 100% |
| Main copybooks (`app/cpy/`) | 29 | — |
| Sub-app copybooks | 18 | — |
| **Total copybooks** | **47** | — |
| Main JCL jobs | 33 | — |
| Sub-app JCL jobs | 5 | — |
| **Total JCL jobs** | **38** | — |
| BMS screen maps | 21 | — |

| Metric | Value |
|--------|-------|
| Total LOC | ~27,350 |
| Average LOC/program | 622 |
| Largest program | COACTUPC.cbl (4,236 LOC) |
| Programs > 1,000 LOC | 8 (18%) |
| Programs with > 10 copybooks | 12 (27%) |
