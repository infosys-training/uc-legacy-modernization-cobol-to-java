# CardDemo Application Inventory

> Full catalog of the CardDemo COBOL estate: **44 programs**, **47 copybooks**, **46 JCL jobs**.

---

## 1. Estate Summary

| Metric | Value |
|--------|-------|
| Total COBOL programs | 44 |
| Total lines of COBOL | 27,350 |
| Online (CICS) programs | 21 (48%) |
| Pure batch programs | 16 (36%) |
| Sub-app programs (IMS/DB2/MQ) | 7 (16%) |
| Copybooks | 47 |
| JCL jobs | 46 |
| BMS screen maps | 16 |
| Average LOC/program | 622 |
| Largest program | COACTUPC.cbl — 4,236 LOC |

---

## 2. COBOL Programs — Main Application (`app/cbl/`)

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|-----------------------|
| 1 | CBACT01C.cbl | 430 | Read account VSAM file and produce flat-file extracts (PS, array, VB formats) | Batch | **Read:** ACCTFILE (VSAM KSDS) **Write:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN, CSMSG02Y |
| 2 | CBACT02C.cbl | 178 | Read and display card data VSAM file | Batch | **Read:** CARDFILE (VSAM KSDS) | CVACT02Y, CSMSG02Y |
| 3 | CBACT03C.cbl | 178 | Read and display card cross-reference VSAM file | Batch | **Read:** XREFFILE (VSAM KSDS) | CVACT03Y, CSMSG02Y |
| 4 | CBACT04C.cbl | 652 | Calculate and apply interest charges to account balances | Batch | **Read:** XREFFILE, ACCTFILE, DISCGRP, TCATBALF **Write:** TRANSACT (daily interest transactions), ACCTFILE (update balances) | CVACT01Y, CVACT03Y, CVTRA02Y, CVTRA05Y, CVTRA06Y, CSMSG02Y |
| 5 | CBCUS01C.cbl | 178 | Read and display customer data VSAM file | Batch | **Read:** CUSTFILE (VSAM KSDS) | CVCUS01Y, CSMSG02Y |
| 6 | CBEXPORT.cbl | 582 | Export all VSAM entity files into a single sequential export file for branch migration | Batch | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE **Write:** EXPFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVEXPORT, CSMSG02Y |
| 7 | CBIMPORT.cbl | 487 | Import data from sequential export file back into individual entity flat files | Batch | **Read:** EXPFILE **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVEXPORT, CSMSG02Y |
| 8 | CBSTM03A.CBL | 924 | Generate customer account statements — master driver program (reads transactions, accounts, customers, produces statement output) | Batch | **Read:** TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE **Write:** STMTFILE **Call:** CBSTM03B | CVACT01Y, CVACT03Y, CVCUS01Y, COSTM01, CSMSG02Y |
| 9 | CBSTM03B.CBL | 230 | Statement generation sub-module — formats and writes individual statement line items | Batch | Called by CBSTM03A; writes formatted lines to statement file | COSTM01 |
| 10 | CBTRN01C.cbl | 494 | Read daily transaction flat file and load into VSAM transaction master | Batch | **Read:** DALYTRAN (sequential) **Write:** TRANSACT (VSAM KSDS), DALYREJS (rejected records) | CVACT03Y, CVTRA05Y, CVTRA06Y, COSTM01, CSMSG02Y, CSDAT01Y, CODATECN |
| 11 | CBTRN02C.cbl | 731 | Post daily transactions — validate, update account balances, update category balances | Batch | **Read:** DALYTRAN, XREFFILE, ACCTFILE, TCATBALF **Write:** TRANFILE (VSAM), DALYREJS, ACCTFILE (balance update), TCATBALF (category balance update) | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y, CSMSG02Y |
| 12 | CBTRN03C.cbl | 649 | Generate daily transaction report sorted by account and transaction type | Batch | **Read:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG **Write:** RPTFILE (report output) | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y, CSMSG02Y |
| 13 | COACTUPC.cbl | 4,236 | Account update — full CICS screen for viewing and modifying account details (status, limits, dates, ZIP). Largest and most complex program | Online (CICS) | **CICS:** READ/REWRITE ACCTFILE, READ CARDXREF, READ CUSTFILE, SEND/RECEIVE MAP COACTUP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, COACTUP, CSSETATY ×3, CSSTRPFY, CSUTLDPY, CSUTLDWY, CSLKPCDY, DFHAID, DFHBMSCA + more (58 total COPY) |
| 14 | COACTVWC.cbl | 941 | Account view — read-only CICS screen displaying account, card, and customer details | Online (CICS) | **CICS:** READ ACCTFILE, STARTBR/READNEXT/ENDBR CARDXREF, READ CUSTFILE, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, COACTVW, CSSTRPFY, DFHAID, DFHBMSCA + more (16 total COPY) |
| 15 | COADM01C.cbl | 288 | Admin menu hub — presents admin options and XCTL transfers to selected admin program | Online (CICS) | **CICS:** SEND/RECEIVE MAP COADM01, XCTL to admin sub-programs | COCOM01Y, COADM02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, COADM01, CSSTRPFY, DFHAID, DFHBMSCA, CSMSG02Y |
| 16 | COBIL00C.cbl | 572 | Bill payment — CICS screen for processing credit card bill payments against account balances | Online (CICS) | **CICS:** READ/REWRITE ACCTFILE, READ CARDXREF, READ CUSTFILE, WRITE TRANSACT, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, COBIL00, CSSTRPFY, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | 41 | Assembler wait wrapper — calls MVSWAIT to pause execution for a given duration | Batch (utility) | **CALL:** MVSWAIT | CSMSG02Y |
| 18 | COCRDLIC.cbl | 1,459 | Credit card list — CICS browse/list screen for credit cards with scrolling (STARTBR/READNEXT/READPREV) | Online (CICS) | **CICS:** STARTBR/READNEXT/READPREV/ENDBR CARDFILE, READ CARDXREF, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT02Y, CVACT03Y, COCRDLI, CSSTRPFY, DFHAID, DFHBMSCA + more (14 total COPY) |
| 19 | COCRDSLC.cbl | 887 | Credit card detail view — CICS screen to view a single card's full details | Online (CICS) | **CICS:** READ CARDFILE, READ CARDXREF, READ ACCTFILE, READ CUSTFILE, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, COCRDSL, CSSTRPFY, DFHAID, DFHBMSCA + more (16 total COPY) |
| 20 | COCRDUPC.cbl | 1,560 | Credit card update — CICS screen for modifying card details (status, embossed name, expiration) | Online (CICS) | **CICS:** READ/REWRITE CARDFILE, READ CARDXREF, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, COCRDUP, CSSETATY, CSSTRPFY, CSUTLDPY, CSUTLDWY, DFHAID, DFHBMSCA, CSLKPCDY |
| 21 | COMEN01C.cbl | 308 | Main user menu hub — presents 11 user menu options and XCTL transfers to selected program | Online (CICS) | **CICS:** SEND/RECEIVE MAP COMEN01, XCTL to user sub-programs | COCOM01Y, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, COMEN01, CSSTRPFY, DFHAID, DFHBMSCA, CSMSG02Y |
| 22 | CORPT00C.cbl | 649 | Transaction report request — CICS screen to select report parameters and submit batch JCL for report generation | Online (CICS) | **CICS:** SEND/RECEIVE MAP, WRITEQ TD, READQ TS **CALL:** CSUTLDTC | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CORPT00, CSSTRPFY, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | 260 | Sign-on screen — CICS login with user ID/password validation against USRSEC file | Online (CICS) | **CICS:** READ USRSEC, SEND/RECEIVE MAP COSGN00 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, COSGN00, CSSTRPFY, DFHAID, DFHBMSCA, UNUSED1Y |
| 24 | COTRN00C.cbl | 699 | Transaction list — CICS browse screen listing transactions with scrolling | Online (CICS) | **CICS:** STARTBR/READNEXT/READPREV/ENDBR TRANSACT, READ CARDXREF, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT03Y, CVTRA05Y, COTRN00, CSSTRPFY, DFHAID |
| 25 | COTRN01C.cbl | 330 | Transaction detail view — CICS screen to view a single transaction | Online (CICS) | **CICS:** READ TRANSACT, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, COTRN01, CSSTRPFY, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | 783 | Transaction add — CICS screen to manually add a new transaction record | Online (CICS) | **CICS:** READ CARDXREF, READ ACCTFILE, WRITE TRANSACT, SEND/RECEIVE MAP **CALL:** CSUTLDTC | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, COTRN02, CSSTRPFY, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | 695 | User list — CICS admin screen to browse security users with scrolling | Online (CICS) | **CICS:** STARTBR/READNEXT/READPREV/ENDBR USRSEC, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, COUSR00, CSSTRPFY, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | 299 | User add — CICS admin screen to create a new security user record | Online (CICS) | **CICS:** WRITE USRSEC, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, COUSR01, CSSTRPFY, DFHAID, DFHBMSCA, CSMSG02Y |
| 29 | COUSR02C.cbl | 414 | User update — CICS admin screen to modify an existing security user | Online (CICS) | **CICS:** READ/REWRITE USRSEC, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, COUSR02, CSSTRPFY, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | 359 | User delete — CICS admin screen to delete a security user | Online (CICS) | **CICS:** READ/DELETE USRSEC, SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, COUSR03, CSSTRPFY, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | 157 | Date validation utility — callable module that validates and converts date formats | Batch (utility) | No file I/O — pure computation | CSUTLDPY |

## 3. COBOL Programs — Sub-Applications

### 3.1 Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|-----------------------|
| 32 | CBPAUP0C.cbl | 386 | Authorization batch processor — reads authorization request file and writes response file | Batch (IMS) | **Read/Write:** Sequential files | CIPAUDTY, CIPAUSMY, CSMSG02Y |
| 33 | COPAUA0C.cbl | 1,026 | Authorization MQ adapter — receives auth requests via MQ, processes via IMS DL/I, sends responses via MQ | Online (CICS+MQ+IMS) | **MQ:** MQOPEN, MQGET, MQPUT1, MQCLOSE **CICS:** READ/WRITE **IMS DL/I:** GU, GN, GNP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CIPAUDTY, CIPAUSMY, CCPAUERY, CCPAURLY, CCPAURQY, CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, DFHAID, DFHBMSCA |
| 34 | COPAUS0C.cbl | 1,032 | Pending authorization summary — CICS screen to view authorization summary by account (hub of auth chain) | Online (CICS) | **CICS:** READ, SEND/RECEIVE MAP, XCTL | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COPAU00, CSSTRPFY, CIPAUSMY, DFHAID, DFHBMSCA + more (15 total COPY) |
| 35 | COPAUS1C.cbl | 604 | Pending authorization list — CICS screen listing pending authorizations with scrolling | Online (CICS) | **CICS:** STARTBR/READNEXT/ENDBR, SEND/RECEIVE MAP, XCTL | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CIPAUSMY, COPAU01, CSSTRPFY, DFHAID, DFHBMSCA + more (11 total COPY) |
| 36 | COPAUS2C.cbl | 244 | Pending authorization detail — CICS screen showing detailed authorization info | Online (CICS) | **CICS:** SEND/RECEIVE MAP | CIPAUDTY, CIPAUSMY, CSMSG02Y |
| 37 | DBUNLDGS.CBL | 366 | IMS database unload (GN/GNP) — reads IMS segments and writes to DB2 staging tables | Batch (IMS→DB2) | **IMS DL/I:** GN, GNP (CBLTDLI calls) **DB2:** EXEC SQL INSERT | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB, CSDB2RPY |
| 38 | PAUDBLOD.CBL | 369 | IMS database load — reads flat file and loads into IMS hierarchical database | Batch (IMS) | **IMS DL/I:** GU, ISRT (CBLTDLI calls) **Read:** Sequential input file | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PAUTBPCB |
| 39 | PAUDBUNL.CBL | 317 | IMS database unload — reads IMS segments and writes to sequential flat file | Batch (IMS) | **IMS DL/I:** GN, GNP (CBLTDLI calls) **Write:** Sequential output file | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB |

### 3.2 Transaction Type (DB2) — `app/app-transaction-type-db2/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|-----------------------|
| 40 | COBTUPDT.cbl | 237 | Transaction type batch update — batch utility to update DB2 transaction type table | Batch (DB2) | **DB2:** EXEC SQL SELECT, UPDATE, INSERT, DELETE | CSDB2RWY |
| 41 | COTRTLIC.cbl | 2,098 | Transaction type list — CICS screen to browse DB2 transaction types with cursor-based scrolling | Online (CICS+DB2) | **DB2:** EXEC SQL SELECT (cursor-based) **CICS:** SEND/RECEIVE MAP, XCTL | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSDB2RWY, COTRTLI, CSSTRPFY, DFHAID, DFHBMSCA + more (12 total COPY) |
| 42 | COTRTUPC.cbl | 1,702 | Transaction type update — CICS screen to add/modify/delete DB2 transaction types | Online (CICS+DB2) | **DB2:** EXEC SQL SELECT, UPDATE, INSERT, DELETE **CICS:** SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSDB2RWY, COTRTUP, CSSTRPFY, CSSETATY, CSUTLDPY, CSUTLDWY, DFHAID, DFHBMSCA + more (15 total COPY) |

### 3.3 VSAM/MQ — `app/app-vsam-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|--------------------|-----------------------|
| 43 | COACCT01.cbl | 620 | Account MQ adapter — receives account queries via MQ, reads VSAM, and returns responses via MQ | Batch (MQ+VSAM) | **MQ:** MQOPEN ×3, MQGET, MQPUT ×2, MQCLOSE ×3 **VSAM:** READ ACCTFILE | CVACT01Y, CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CSMSG02Y, COCOM01Y |
| 44 | CODATE01.cbl | 524 | Date service MQ adapter — receives date conversion requests via MQ, processes, and returns formatted dates via MQ | Batch (MQ) | **MQ:** MQOPEN ×3, MQGET, MQPUT ×2, MQCLOSE ×3 | CODATECN, CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CSMSG02Y |

---

## 4. JCL Job Catalog (`app/jcl/`)

### 4.1 Data Setup Jobs (VSAM File Definition)

| Job Name | Purpose | Step Sequence | Key Datasets |
|----------|---------|---------------|--------------|
| ACCTFILE.jcl | Define account VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO (load from PS) | `ACCTDATA.VSAM.KSDS` ← `ACCTDATA.PS` |
| CARDFILE.jcl | Define card data VSAM KSDS + AIX | CLCIFIL: Close CICS files → STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP40: DEFINE AIX → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: Open CICS files | `CARDDATA.VSAM.KSDS` ← `CARDDATA.PS`; AIX on ACCT-ID |
| CUSTFILE.jcl | Define customer VSAM KSDS | CLCIFIL → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → OPCIFIL | `CUSTDATA.VSAM.KSDS` ← `CUSTDATA.PS` |
| XREFFILE.jcl | Define card cross-reference VSAM KSDS + AIX | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → STEP20-30: AIX definition | `CARDXREF.VSAM.KSDS` ← `CARDXREF.PS` |
| TRANFILE.jcl | Define transaction master VSAM KSDS + AIX | CLCIFIL → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → STEP20-30: AIX → OPCIFIL | `TRANSACT.VSAM.KSDS` ← `DALYTRAN.PS.INIT` |
| TCATBALF.jcl | Define transaction category balance VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO | `TCATBALF.VSAM.KSDS` ← `TCATBALF.PS` |
| DISCGRP.jcl | Define disclosure group VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO | `DISCGRP.VSAM.KSDS` ← `DISCGRP.PS` |
| TRANTYPE.jcl | Define transaction type VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO | `TRANTYPE.VSAM.KSDS` ← `TRANTYPE.PS` |
| TRANCATG.jcl | Define transaction category VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO | `TRANCATG.VSAM.KSDS` ← `TRANCATG.PS` |
| DUSRSECJ.jcl | Define user security VSAM KSDS | PREDEL → STEP01: IEBGENER (create PS) → STEP02: IDCAMS DELETE → STEP03: IDCAMS REPRO | `USRSEC.VSAM.KSDS` ← `USRSEC.PS` |
| DEFCUST.jcl | Alternate customer file definition | STEP05: DELETE → STEP05: DEFINE | `CUSTDATA` alternate definition |

### 4.2 Batch Processing Jobs

| Job Name | Purpose | Step Sequence | Key Datasets |
|----------|---------|---------------|--------------|
| POSTTRAN.jcl | Post daily transactions to master file | STEP15: PGM=CBTRN02C | **In:** TRANSACT, DALYTRAN, XREFFILE, ACCTFILE, TCATBALF **Out:** SYSTRAN(+1), DALYREJS |
| INTCALC.jcl | Calculate interest on account balances | STEP15: PGM=CBACT04C PARM='2022071800' | **In:** TCATBALF, XREFFILE, ACCTFILE, DISCGRP **Out:** SYSTRAN(+1) |
| CREASTMT.JCL | Generate customer statements | DELDEF01: IDCAMS → STEP010: SORT (TRANSACT→SEQ) → STEP020: IDCAMS REPRO → STEP030: Delete old statements → STEP040: PGM=CBSTM03A | **In:** TRANSACT, XREFFILE, ACCTFILE, CUSTFILE **Out:** STATEMNT.PS, STATEMNT.HTML |
| TRANREPT.jcl | Generate daily transaction report | STEP05R: REPROC (backup TRANSACT) → STEP05R: SORT → STEP10R: PGM=CBTRN03C | **In:** TRANSACT, CARDXREF, TRANTYPE, TRANCATG **Out:** RPTFILE |
| COMBTRAN.jcl | Combine transaction backup + system transactions | STEP05R: SORT (merge TRANSACT.BKUP + SYSTRAN) → STEP10: IDCAMS REPRO | **In:** TRANSACT.BKUP(0), SYSTRAN(0) **Out:** TRANSACT.COMBINED(+1) |
| TRANBKP.jcl | Backup and clear transaction master | STEP05R: REPROC → STEP05: IDCAMS DELETE → STEP10: IDCAMS REDEFINE | `TRANSACT.BKUP(+1)` ← `TRANSACT.VSAM.KSDS` |
| READACCT.jcl | Read/export account data to flat files | PREDEL → STEP05: PGM=CBACT01C | **In:** ACCTDATA.VSAM.KSDS **Out:** PSCOMP, ARRYPS, VBPS |
| READCARD.jcl | Read card data file | STEP05: PGM=CBACT02C | **In:** CARDDATA.VSAM.KSDS |
| READCUST.jcl | Read customer data file | STEP05: PGM=CBCUS01C | **In:** CUSTDATA.VSAM.KSDS |
| READXREF.jcl | Read cross-reference file | STEP05: PGM=CBACT03C | **In:** CARDXREF.VSAM.KSDS |
| CBEXPORT.jcl | Export all entities to migration file | STEP01: IDCAMS verify → STEP02: PGM=CBEXPORT | **In:** All 5 VSAM files **Out:** EXPORT.DATA |
| CBIMPORT.jcl | Import data from migration file | STEP01: PGM=CBIMPORT | **In:** EXPORT.DATA **Out:** Individual entity import files |
| WAITSTEP.jcl | Wait step utility | WAIT: PGM=COBSWAIT | No datasets — timed wait |

### 4.3 GDG and Infrastructure Jobs

| Job Name | Purpose | Step Sequence | Key Datasets |
|----------|---------|---------------|--------------|
| DEFGDGB.jcl | Define GDG base clusters | STEP05: IDCAMS DEFINE GDG | GDG bases for TRANSACT.BKUP, SYSTRAN, DALYREJS, etc. |
| DEFGDGD.jcl | Define DB2-related GDG and backup | STEP10-50: IDCAMS + IEBGENER | TRANTYPE.BKUP, TRANCATG.PS.BKUP, DISCGRP.BKUP |
| DALYREJS.jcl | Define GDG for daily rejects | STEP05: IDCAMS | `DALYREJS` GDG base |
| REPTFILE.jcl | Define GDG for report output | STEP05: IDCAMS | Report output GDG base |
| TRANIDX.jcl | Define alternate index on transaction master | STEP20-30: IDCAMS | `TRANSACT.VSAM.AIX` |
| ESDSRRDS.jcl | Define ESDS and RRDS VSAM files | PREDEL → STEP01-05: Define ESDS/RRDS clusters | `USRSEC.VSAM.ESDS`, `USRSEC.VSAM.RRDS` |

### 4.4 CICS Administration Jobs

| Job Name | Purpose | Step Sequence | Key Datasets |
|----------|---------|---------------|--------------|
| CBADMCDJ.jcl | Define all CICS resources (programs, mapsets, transactions, files) | STEP1: PGM=DFHCSDUP | CICS CSD definitions for all CardDemo resources |
| CLOSEFIL.jcl | Close VSAM files in CICS region | CLCIFIL: PGM=SDSF | CEMT SET FIL ... CLO |
| OPENFIL.jcl | Open VSAM files in CICS region | OPCIFIL: PGM=SDSF | CEMT SET FIL ... OPE |

### 4.5 Utility and Reporting Jobs

| Job Name | Purpose | Step Sequence | Key Datasets |
|----------|---------|---------------|--------------|
| PRTCATBL.jcl | Print transaction category balance report | DELDEF → STEP05R: REPROC → STEP10R: SORT | `TCATBALF.REPT` |
| TXT2PDF1.JCL | Convert text statement to PDF | TXT2PDF: PGM=IKJEFT1B | **In:** STATEMNT.PS **Out:** PDF output |
| FTPJCL.JCL | FTP transfer utility | STEP1: PGM=FTP | File transfer |
| INTRDRJ1.JCL | Internal reader — trigger secondary JCL | IDCAMS → STEP01: IEBGENER (submit INTRDRJ2) | Chain to INTRDRJ2 |
| INTRDRJ2.JCL | Internal reader — create IMS VSAM files | IDCAMS: Backup + define | IMS database files |

### 4.6 Sub-Application JCL

Sub-application JCL jobs reside in their respective directories (`app/app-authorization-ims-db2-mq/jcl/`, etc.) and handle IMS database management, DB2 table operations, and MQ queue definitions. These are referenced from the sub-application COBOL programs described in Section 3.

---

## 5. BMS Screen Maps (`app/bms/`)

| Map Name | Used By | Screen Purpose |
|----------|---------|----------------|
| COSGN00.bms | COSGN00C | Sign-on / Login |
| COADM01.bms | COADM01C | Admin menu |
| COMEN01.bms | COMEN01C | Main user menu |
| COACTUP.bms | COACTUPC | Account update |
| COACTVW.bms | COACTVWC | Account view |
| COCRDLI.bms | COCRDLIC | Credit card list |
| COCRDSL.bms | COCRDSLC | Credit card detail |
| COCRDUP.bms | COCRDUPC | Credit card update |
| COTRN00.bms | COTRN00C | Transaction list |
| COTRN01.bms | COTRN01C | Transaction view |
| COTRN02.bms | COTRN02C | Transaction add |
| CORPT00.bms | CORPT00C | Report request |
| COBIL00.bms | COBIL00C | Bill payment |
| COUSR00.bms | COUSR00C | User list |
| COUSR01.bms | COUSR01C | User add |
| COUSR02.bms | COUSR02C | User update |
