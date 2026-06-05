# Application Inventory — CardDemo COBOL Estate

> **Generated:** 2026-06-05 | **Application:** CardDemo — Credit Card Management System  
> **Repository:** infosys-training/uc-legacy-modernization-cobol-to-java

---

## Summary

| Metric | Count |
|--------|-------|
| Total COBOL Programs | 44 |
| Main Programs (`app/cbl/`) | 31 |
| Sub-Application Programs | 13 |
| Total Lines of COBOL | ~27,350 |
| Copybooks | 47 |
| JCL Jobs | 46 |
| BMS Screen Maps | 17 |

---

## 1. Main Programs — `app/cbl/`

### 1.1 Batch Programs

| # | Filename | LOC | Purpose | Key I/O (Files Read / Written) | DB2 Tables | Copybooks Referenced |
|---|----------|-----|---------|-------------------------------|------------|---------------------|
| 1 | `CBACT01C.cbl` | 430 | Read account VSAM file, format dates via assembler COBDATFT, and write output in sequential, array, and variable-length formats | **R:** ACCTFILE (KSDS) · **W:** OUTFILE, ARRYFILE, VBRCFILE | — | CVACT01Y, CODATECN |
| 2 | `CBACT02C.cbl` | 178 | Read and display card data file records | **R:** CARDFILE (KSDS) | — | CVACT02Y |
| 3 | `CBACT03C.cbl` | 178 | Read and display account–card cross-reference records | **R:** XREFFILE (KSDS) | — | CVACT03Y |
| 4 | `CBACT04C.cbl` | 652 | Interest calculator — compute interest on account balances using discount-group rates, update accounts, write interest transactions | **R:** TCATBALF (KSDS), XREFFILE (KSDS), DISCGRP (KSDS) · **R/W:** ACCTFILE (KSDS) · **W:** TRANSACT (KSDS) | — | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | `CBCUS01C.cbl` | 178 | Read and display customer master file records | **R:** CUSTFILE (KSDS) | — | CVCUS01Y |
| 6 | `CBEXPORT.cbl` | 582 | Export customer data for branch migration — reads normalized CardDemo files and creates multi-record export file | **R:** CUSTFILE, ACCTFILE, CARDXREF, TRANSACT · **W:** EXPORTFL, REPTFILE | — | CVACT01Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVEXPORT, CUSTREC |
| 7 | `CBIMPORT.cbl` | 487 | Import customer data from branch migration export — split multi-record file into normalized targets with validation | **R:** IMPORTFL · **W:** CUSTFILE, ACCTFILE, CARDXREF, TRANSACT, DALYREJS | — | CVACT01Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVEXPORT, CUSTREC |
| 8 | `CBSTM03A.CBL` | 924 | Statement generation — read cross-ref, customer, account, transaction files and produce text + HTML statements | **R:** XREFFILE, CUSTFILE, ACCTFILE, TRNXFILE · **W:** STMTFILE, HTMLFILE | — | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | `CBSTM03B.CBL` | 230 | I/O submodule for CBSTM03A — handles file OPEN/CLOSE/READ/WRITE operations | **R/W:** Same files as CBSTM03A (called as subprogram) | — | *(inline definitions)* |
| 10 | `CBTRN01C.cbl` | 494 | Read and validate daily transaction file — display records and check cross-references | **R:** DALYTRAN (SEQ), CARDXREF (KSDS) · **W:** TRANSACT (KSDS) | — | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVACT02Y, CVTRA01Y |
| 11 | `CBTRN02C.cbl` | 731 | Post daily transactions — validate against cross-ref and accounts, update balances, write to master transaction file, generate rejections | **R:** DALYTRAN (SEQ), XREFFILE (KSDS) · **R/W:** ACCTFILE (KSDS), TCATBALF (KSDS) · **W:** TRANSACT (KSDS), DALYREJS (SEQ) | — | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | `CBTRN03C.cbl` | 649 | Daily transaction report — read transactions, look up cross-references and transaction types/categories, write formatted report | **R:** TRANSACT (KSDS), CARDXREF (KSDS), TRANTYPE (KSDS), TRANCATG (KSDS) · **W:** REPTFILE (SEQ) | — | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | `COBSWAIT.cbl` | 41 | Assembler-bridge wait program — calls MVSWAIT for batch job pausing | — | — | *(none)* |
| 14 | `CSUTLDTC.cbl` | 157 | Date validation utility — calls LE function CEEDAYS for date conversion and validation | — | — | *(none)* |

### 1.2 Online (CICS) Programs

| # | Filename | LOC | Purpose | Key I/O (VSAM Files Accessed) | DB2 Tables | Copybooks Referenced |
|---|----------|-----|---------|-------------------------------|------------|---------------------|
| 15 | `COSGN00C.cbl` | 260 | Sign-on / login screen — authenticate user credentials, route to admin or main menu | **R:** USRSEC (KSDS) | — | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | `COADM01C.cbl` | 288 | Admin menu hub — display admin options (user CRUD + DB2 tran type mgmt), route via XCTL | — | — | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | `COMEN01C.cbl` | 308 | Main menu hub — display 11 user options (account, card, transaction, report, bill pay), route via XCTL | — | — | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | `COACTUPC.cbl` | 4,236 | Account update — exhaustive field validation (date, SSN, phone, state, ZIP), update account/customer/card records | **R/W:** ACCTFILE, CUSTFILE, CARDXREF | — | 56 copybooks incl. COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVCRD01Y, CSLKPCDY, CSSETATY (×3 via REPLACING), CSSTRPFY, CSUTLDPY, CSUTLDWY, DFHAID, DFHBMSCA, COACTUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y |
| 19 | `COACTVWC.cbl` | 941 | Account view — display read-only account details with associated cards and customer info | **R:** ACCTFILE, CUSTFILE, CARDXREF, CARDFILE | — | COCOM01Y, CVCRD01Y, COACTVW, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY, DFHAID, DFHBMSCA |
| 20 | `COCRDLIC.cbl` | 1,459 | Credit card list — paginated browse (STARTBR/READNEXT/READPREV/ENDBR) of card records | **R:** CARDFILE (KSDS), CARDXREF (AIX) | — | COCOM01Y, CVCRD01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, DFHAID, DFHBMSCA |
| 21 | `COCRDSLC.cbl` | 887 | Credit card detail view — display card details with customer/account lookup | **R:** CARDFILE, CUSTFILE, ACCTFILE, CARDXREF | — | COCOM01Y, CVCRD01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY, DFHAID, DFHBMSCA |
| 22 | `COCRDUPC.cbl` | 1,560 | Credit card update — validate and update card status, expiration, CVV; linked account/customer updates | **R/W:** CARDFILE, ACCTFILE · **R:** CUSTFILE, CARDXREF | — | COCOM01Y, CVCRD01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVACT01Y, CVCUS01Y, CSSTRPFY, DFHAID, DFHBMSCA |
| 23 | `COTRN00C.cbl` | 699 | Transaction list — browse transaction records with filtering by account | **R:** TRANSACT (KSDS), CARDXREF | — | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | `COTRN01C.cbl` | 330 | Transaction detail view — display individual transaction record details | **R:** TRANSACT | — | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | `COTRN02C.cbl` | 783 | Transaction add — online entry of new transactions with validation | **R:** CARDXREF, ACCTFILE · **W:** TRANSACT, DALYTRAN | — | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 26 | `COBIL00C.cbl` | 572 | Bill payment — process payments against account balance | **R/W:** ACCTFILE · **R:** CARDXREF | — | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 27 | `CORPT00C.cbl` | 649 | Report request — submit batch JCL (INTRDRJ1/INTRDRJ2) for report generation via CICS internal reader | **R:** TRANSACT · **W:** Internal reader (batch JCL submission) | — | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 28 | `COUSR00C.cbl` | 695 | User list — browse user security records with pagination | **R:** USRSEC (KSDS) | — | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | `COUSR01C.cbl` | 299 | User add — add new user security records | **W:** USRSEC | — | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | `COUSR02C.cbl` | 414 | User update — modify existing user security records | **R/W:** USRSEC | — | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | `COUSR03C.cbl` | 359 | User delete — remove user security records | **R/W:** USRSEC | — | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | LOC | Type | Purpose | Subsystems | Key I/O | Copybooks |
|---|----------|-----|------|---------|------------|---------|-----------|
| 32 | `COPAUA0C.cbl` | 1,026 | Online (CICS+IMS+MQ+DB2) | Card authorization decision — receive MQ request, validate against IMS auth DB, check limits, produce approve/decline response | CICS, IMS (DL/I), MQ, DB2 | **MQ:** Request/Reply queues · **IMS:** Auth summary/detail segments · **R:** CARDXREF, ACCTFILE, CUSTFILE | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | `COPAUS0C.cbl` | 1,032 | Online (CICS+IMS) | Authorization summary browse — list pending authorizations for an account with IMS segment traversal | CICS, IMS (DL/I) | **IMS:** Auth summary/detail · **R:** ACCTFILE, CARDXREF, CUSTFILE, CARDFILE | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | `COPAUS1C.cbl` | 604 | Online (CICS+IMS) | Authorization detail with update — view individual auth detail, mark fraud flag via IMS REPL | CICS, IMS (DL/I) | **IMS:** Auth detail (read/update) | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | `COPAUS2C.cbl` | 244 | Online (CICS+DB2) | Fraud confirmation — insert fraud-flag record into DB2 | CICS, DB2 | **DB2:** Fraud table (INSERT) | CIPAUDTY |
| 36 | `CBPAUP0C.cbl` | 386 | Batch (IMS) | Delete expired pending authorization messages — traverse IMS DB, delete expired summary/detail segments | IMS (DL/I) | **IMS:** Auth summary (GN/DLET), Auth detail (GNP/DLET) | CIPAUSMY, CIPAUDTY |
| 37 | `PAUDBLOD.CBL` | 369 | Batch (IMS) | IMS database load — initial load of pending authorization data from flat file into IMS segments | IMS (DL/I) | **R:** Input flat file · **IMS:** ISRT/GU into auth DB | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 38 | `PAUDBUNL.CBL` | 317 | Batch (IMS) | IMS database unload — extract pending authorization data from IMS to flat file | IMS (DL/I) | **IMS:** GN/GNP from auth DB · **W:** Output flat file | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | `DBUNLDGS.CBL` | 366 | Batch (IMS/GSAM) | GSAM unload utility — extract IMS data via GSAM output | IMS (DL/I, GSAM) | **IMS:** GN/GNP · **GSAM:** ISRT to output | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### 2.2 Transaction Type Module — `app/app-transaction-type-db2/cbl/`

| # | Filename | LOC | Type | Purpose | Subsystems | Key I/O | Copybooks |
|---|----------|-----|------|---------|------------|---------|-----------|
| 40 | `COTRTLIC.cbl` | 2,098 | Online (CICS+DB2) | Transaction type list — cursor-based paginated browse of DB2 TRAN_TYPE / TRAN_CAT tables | CICS, DB2 | **DB2:** SELECT with CURSOR on TRAN_TYPE, TRAN_CAT | CSDB2RPY, CSDB2RWY, BMS maps |
| 41 | `COTRTUPC.cbl` | 1,702 | Online (CICS+DB2) | Transaction type update/delete — CRUD operations on DB2 transaction types with cascading deletes | CICS, DB2 | **DB2:** INSERT/UPDATE/DELETE on TRAN_TYPE, TRAN_CAT | CSDB2RPY, CSDB2RWY, BMS maps |
| 42 | `COBTUPDT.cbl` | 237 | Batch (DB2) | Batch transaction type update — read input file and apply updates to DB2 TRAN_TYPE table | DB2 | **R:** INPFILE (SEQ) · **DB2:** INSERT/UPDATE on DCLTRTYP | *(inline SQL includes)* |

### 2.3 VSAM/MQ Module — `app/app-vsam-mq/cbl/`

| # | Filename | LOC | Type | Purpose | Subsystems | Key I/O | Copybooks |
|---|----------|-----|------|---------|------------|---------|-----------|
| 43 | `COACCT01.cbl` | 620 | Online (CICS+MQ) | Account inquiry via MQ — receive account query from MQ, read VSAM, return response | CICS, MQ | **MQ:** Request/Reply queues · **R:** ACCTFILE | CVACT01Y |
| 44 | `CODATE01.cbl` | 524 | Online (CICS+MQ) | Date inquiry via MQ — receive date format request, process date conversion, return response | CICS, MQ | **MQ:** Request/Reply queues | *(inline definitions)* |

---

## 3. Classification Summary

| Classification | Count | % | Programs |
|---------------|-------|---|----------|
| **Batch (Pure)** | 14 | 32% | CBACT01C–04C, CBCUS01C, CBEXPORT, CBIMPORT, CBSTM03A/B, CBTRN01C–03C, COBSWAIT, CSUTLDTC |
| **Online (CICS only)** | 17 | 39% | COSGN00C, COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C–02C, COBIL00C, CORPT00C, COUSR00C–03C |
| **Online (CICS+DB2)** | 3 | 7% | COTRTLIC, COTRTUPC, COPAUS2C |
| **Online (CICS+IMS)** | 2 | 5% | COPAUS0C, COPAUS1C |
| **Online (CICS+IMS+MQ+DB2)** | 1 | 2% | COPAUA0C |
| **Online (CICS+MQ)** | 2 | 5% | COACCT01, CODATE01 |
| **Batch (IMS)** | 4 | 9% | CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS |
| **Batch (DB2)** | 1 | 2% | COBTUPDT |

---

## 4. JCL Job Catalog — `app/jcl/`

### 4.1 Data Setup Jobs (VSAM Cluster Definition via IDCAMS)

| Job | Steps | Purpose | Datasets Managed |
|-----|-------|---------|-----------------|
| `ACCTFILE.jcl` | STEP05→STEP10→STEP15 | Delete/define/load account VSAM KSDS | `AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS` |
| `CARDFILE.jcl` | CLCIFIL→STEP05→STEP10→STEP15→STEP40→STEP50→STEP60→OPCIFIL | Delete/define/load card VSAM KSDS + AIX on ACCT-ID | `AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS` + AIX |
| `CUSTFILE.jcl` | STEP05→STEP10→STEP15 | Delete/define/load customer VSAM KSDS | `AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS` |
| `XREFFILE.jcl` | STEP05→STEP10→STEP15→STEP20 | Delete/define/load card-XREF VSAM KSDS + AIX | `AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS` + AIX |
| `TRANFILE.jcl` | STEP05→STEP10→STEP15→STEP20→STEP30→STEP40→STEP50 | Delete/define/load transaction VSAM KSDS + AIX + path | `AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS` + AIX |
| `DISCGRP.jcl` | STEP05→STEP10→STEP15 | Delete/define/load discount group VSAM KSDS | `AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS` |
| `TCATBALF.jcl` | STEP05→STEP10→STEP15 | Delete/define/load transaction category balance KSDS | `AWS.M2.CARDDEMO.TCATBAL.VSAM.KSDS` |
| `TRANTYPE.jcl` | STEP05→STEP10→STEP15 | Delete/define/load transaction type VSAM KSDS | `AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS` |
| `TRANCATG.jcl` | STEP05→STEP10→STEP15 | Delete/define/load transaction category VSAM KSDS | `AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS` |
| `DEFCUST.jcl` | STEP05→STEP10→STEP15 | Alternative customer file definition | `AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS` |
| `DEFGDGB.jcl` | STEP01→STEP02 | Define GDG base for daily transaction backup | `AWS.M2.CARDDEMO.DALYTRAN.GDG.BASE` |
| `DEFGDGD.jcl` | STEP01–STEP07 | Define GDG bases for statement/report outputs | Multiple GDG bases for statements |

### 4.2 Batch Processing Jobs

| Job | Steps | Program Executed | Purpose |
|-----|-------|-----------------|---------|
| `POSTTRAN.jcl` | STEP01 | `CBTRN02C` | Post daily transactions to master file |
| `INTCALC.jcl` | STEP01 | `CBACT04C` | Calculate interest on account balances |
| `CREASTMT.JCL` | STEP01 | `CBSTM03A` | Generate customer statements (text + HTML) |
| `TRANREPT.jcl` | STEP01 | `CBTRN03C` | Generate daily transaction report |
| `CBEXPORT.jcl` | STEP01 | `CBEXPORT` | Export data for branch migration |
| `CBIMPORT.jcl` | STEP01 | `CBIMPORT` | Import data with validation |
| `READACCT.jcl` | STEP01 | `CBACT01C` | Read and convert account file |
| `READCARD.jcl` | STEP01 | `CBACT02C` | Read and print card data |
| `READCUST.jcl` | STEP01 | `CBCUS01C` | Read and print customer data |
| `READXREF.jcl` | STEP01 | `CBACT03C` | Read and print cross-reference data |
| `PRTCATBL.jcl` | STEP01 | `CBTRN01C` | Print transaction category balances |
| `COMBTRAN.jcl` | STEP01→STEP02 | SORT→MERGE | Combine and sort transaction files |
| `DALYREJS.jcl` | STEP01 | SORT | Sort daily rejection records |
| `TRANBKP.jcl` | STEP01 | IDCAMS REPRO | Backup transaction VSAM to sequential |
| `TRANIDX.jcl` | STEP01→STEP02 | SORT + IDCAMS | Build transaction index |
| `REPTFILE.jcl` | STEP01 | IEBGENER | Copy report file to output |
| `WAITSTEP.jcl` | STEP01 | `COBSWAIT` | Batch wait/pause utility |

### 4.3 CICS Management Jobs

| Job | Steps | Purpose |
|-----|-------|---------|
| `CBADMCDJ.jcl` | STEP1→STEP2→STEP3 | Define all CICS resources (mapsets, programs, transactions, files, TDQs) for CardDemo |
| `CLOSEFIL.jcl` | STEP01 | Close CICS files via CEMT commands |
| `OPENFIL.jcl` | STEP01 | Open CICS files via CEMT commands |
| `ESDSRRDS.jcl` | Multiple | Define ESDS/RRDS VSAM clusters for CICS |
| `DUSRSECJ.jcl` | STEP05→STEP10→STEP15 | Define user security VSAM file |

### 4.4 Utility Jobs

| Job | Steps | Purpose |
|-----|-------|---------|
| `FTPJCL.JCL` | STEP01 | FTP transfer of datasets |
| `TXT2PDF1.JCL` | STEP01 | Convert text output to PDF |
| `INTRDRJ1.JCL` | Single | Internal reader JCL for transaction report (submitted by CORPT00C) |
| `INTRDRJ2.JCL` | Single | Internal reader JCL for statement generation (submitted by CORPT00C) |

### 4.5 Sub-Application JCL — `app/app-authorization-ims-db2-mq/jcl/`

| Job | Steps | Program | Purpose |
|-----|-------|---------|---------|
| `CBPAUP0J.jcl` | STEP01 | `CBPAUP0C` (via DFSRRC00 BMP) | Execute IMS program to delete expired authorizations |
| `DBPAUTP0.jcl` | STEPDEL→UNLOAD | DFSRRC00/DFSURGU0 | Unload IMS pending authorization database |
| `LOADPADB.JCL` | STEP01 | `PAUDBLOD` (via DFSRRC00 BMP) | Load IMS pending authorization database |
| `UNLDPADB.JCL` | STEP01 | `PAUDBUNL` (via DFSRRC00 BMP) | Unload IMS pending authorization database |
| `UNLDGSAM.JCL` | STEP01 | `DBUNLDGS` (via DFSRRC00 BMP) | GSAM unload of IMS data |

### 4.6 Sub-Application JCL — `app/app-transaction-type-db2/jcl/`

| Job | Steps | Purpose |
|-----|-------|---------|
| `CREADB21.jcl` | Multiple | Create DB2 tables (TRAN_TYPE, TRAN_CAT) and load initial data |
| `MNTTRDB2.jcl` | STEP01 | Execute `COBTUPDT` for batch DB2 transaction type maintenance |
| `TRANEXTR.jcl` | STEP01 | Extract transaction type data from DB2 to flat file |

---

## 5. BMS Screen Maps — `app/bms/`

| Map | Associated Program | Screen Purpose |
|-----|--------------------|---------------|
| `COSGN00.bms` | COSGN00C | Login/sign-on screen |
| `COADM01.bms` | COADM01C | Admin menu |
| `COMEN01.bms` | COMEN01C | Main menu |
| `COACTUP.bms` | COACTUPC | Account update form |
| `COACTVW.bms` | COACTVWC | Account view display |
| `COCRDLI.bms` | COCRDLIC | Credit card list |
| `COCRDSL.bms` | COCRDSLC | Credit card detail |
| `COCRDUP.bms` | COCRDUPC | Credit card update form |
| `COTRN00.bms` | COTRN00C | Transaction list |
| `COTRN01.bms` | COTRN01C | Transaction detail |
| `COTRN02.bms` | COTRN02C | Transaction add form |
| `COBIL00.bms` | COBIL00C | Bill payment screen |
| `CORPT00.bms` | CORPT00C | Report request screen |
| `COUSR00.bms` | COUSR00C | User list |
| `COUSR01.bms` | COUSR01C | User add form |
| `COUSR02.bms` | COUSR02C | User update form |
| `COUSR03.bms` | COUSR03C | User delete confirmation |

Sub-application BMS maps (in respective `bms/` directories):

| Map | Associated Program | Screen Purpose |
|-----|--------------------|---------------|
| `COPAU00.bms` | COPAUS0C | Authorization summary list |
| `COPAU01.bms` | COPAUS1C | Authorization detail view |
| `COTRTLI.bms` | COTRTLIC | Transaction type list (DB2) |
| `COTRTUP.bms` | COTRTUPC | Transaction type update (DB2) |
