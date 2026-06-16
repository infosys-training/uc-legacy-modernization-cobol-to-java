# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Application:** CardDemo — Mainframe credit card management system
> **Total:** 44 COBOL programs | 62 copybooks | 21 BMS maps | 46 JCL jobs | ~30,175 LOC

---

## 1. COBOL Program Inventory

### 1.1 Main Programs (`app/cbl/`) — 31 Programs

#### Batch Programs (16)

| # | Filename | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|-------------------|---------------------|
| 1 | CBACT01C.cbl | 430 | Read account VSAM file; write sequential output, array, and variable-length record files | READ ACCTFILE (KSDS), WRITE OUT-FILE, ARRY-FILE, VBRC-FILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Read and print card data file | READ CARDFILE (KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Read and print account cross-reference file | READ XREFFILE (KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Interest calculator — compute interest/fees on account balances using disclosure group rates | READ TCATBAL, XREF, DISCGRP; REWRITE ACCOUNT; WRITE TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Read and print customer data file | READ CUSTFILE (KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Export customer data for branch migration — reads all entity files and creates multi-record export | READ CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD; WRITE EXPORT-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Import customer data from branch export — splits export file into normalized target files with checksum validation | READ EXPORT-INPUT; WRITE CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD, ERROR-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBTRN01C.cbl | 494 | Post records from daily transaction file (variant 1) | READ DALYTRAN, CUSTOMER, XREF, CARD; WRITE TRANSACT; REWRITE ACCOUNT | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | CBTRN02C.cbl | 731 | Post daily transactions with rejection handling — processes daily transactions, updates master, creates category balances | READ DALYTRAN, TRANSACT, XREF; WRITE DALYREJS, TCATBAL; REWRITE ACCOUNT | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | 649 | Print transaction detail report — generates formatted report with page/account/grand totals | READ TRANSACT, XREF, TRANTYPE, TRANCATG, DATE-PARMS; WRITE REPORT-FILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | CBSTM03A.CBL | 924 | Generate account statements from transaction data — produces text and HTML output | READ TRNX, XREF, CUST, ACCT (via CBSTM03B); WRITE STMT-FILE, HTML-FILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 12 | CBSTM03B.CBL | 230 | File I/O subroutine for statement generation — called by CBSTM03A for all file operations | OPEN/READ/CLOSE TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE | *(none — uses LINKAGE SECTION)* |
| 13 | COBSWAIT.cbl | 41 | Utility program to wait for specified centiseconds | *(no file I/O)* | *(none)* |
| 14 | CSUTLDTC.cbl | 157 | Date validation utility — validates dates using LE CEEDAYS service | *(no file I/O)* | *(none)* |
| 15 | CBEXPORT.cbl | — | *(see row 6)* | — | — |
| 16 | CBIMPORT.cbl | — | *(see row 7)* | — | — |

> **Note:** Rows 15-16 are duplicates removed; actual unique batch programs = 14.

#### Online (CICS) Programs (17)

| # | Filename | LOC | Purpose | Key CICS I/O | Copybooks Referenced | BMS Map |
|---|----------|-----|---------|-------------|---------------------|---------|
| 1 | COSGN00C.cbl | 260 | Sign-on screen — authenticate user credentials | READ USRSEC; XCTL | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | COSGN00 |
| 2 | COADM01C.cbl | 288 | Admin menu — hub for admin functions (6 options) | XCTL to COUSR00C–03C, COTRTLIC, COTRTUPC | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | COADM01 |
| 3 | COMEN01C.cbl | 308 | Main menu — hub for regular users (11 options) | XCTL to 11 programs | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | COMEN01 |
| 4 | COACTUPC.cbl | 4,236 | Account update — field-by-field edit with exhaustive validation (date, SSN, phone, state, ZIP) | READ/REWRITE ACCTDAT, CARDXREF, CUSTFILE; SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×25 via COPY REPLACING) | COACTUP |
| 5 | COACTVWC.cbl | 941 | Account view — display account details (read-only) | READ ACCTDAT, CARDDAT, XREFDAT, CUSTDAT; SEND MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY | COACTVW |
| 6 | COCRDLIC.cbl | 1,459 | List credit cards — paginated browse with STARTBR/READNEXT/READPREV/ENDBR | STARTBR, READNEXT, READPREV, ENDBR on CARDDAT; SEND MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY | COCRDLI |
| 7 | COCRDSLC.cbl | 887 | Credit card detail view — display card and customer information | READ CARDDAT, CUSTDAT; SEND MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY | COCRDSL |
| 8 | COCRDUPC.cbl | 1,560 | Credit card update — edit card details and rewrite | READ/REWRITE CARDDAT, READ CUSTDAT; SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY | COCRDUP |
| 9 | COTRN00C.cbl | 699 | List transactions — paginated browse of TRANSACT file | STARTBR, READNEXT, READPREV, ENDBR on TRANSACT; SEND MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA | COTRN00 |
| 10 | COTRN01C.cbl | 330 | View transaction — display single transaction detail | READ TRANSACT; SEND MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA | COTRN01 |
| 11 | COTRN02C.cbl | 783 | Add new transaction — validate and write to TRANSACT file | READ ACCTDAT, XREFDAT; STARTBR/READPREV/ENDBR (for ID generation); WRITE TRANSACT | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA | COTRN02 |
| 12 | CORPT00C.cbl | 649 | Report request — submit batch JCL for transaction report generation | WRITEQ TD (submit to INTRDR); CALL CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA | CORPT00 |
| 13 | COBIL00C.cbl | 572 | Bill payment — process credit card payment against account balance | READ/REWRITE ACCTDAT; READ XREFDAT; STARTBR/READPREV/ENDBR; WRITE TRANSACT | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA | COBIL00 |
| 14 | COUSR00C.cbl | 695 | List users — paginated browse of USRSEC file | STARTBR, READNEXT, READPREV, ENDBR on USRSEC; SEND MAP | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | COUSR00 |
| 15 | COUSR01C.cbl | 299 | Add user — create new regular/admin user in USRSEC | WRITE USRSEC; SEND/RECEIVE MAP | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | COUSR01 |
| 16 | COUSR02C.cbl | 414 | Update user — modify existing user in USRSEC | READ/REWRITE USRSEC; SEND/RECEIVE MAP | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | COUSR02 |
| 17 | COUSR03C.cbl | 359 | Delete user — remove user from USRSEC | READ/DELETE USRSEC; SEND/RECEIVE MAP | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | COUSR03 |

### 1.2 Sub-Application Programs

#### Authorization IMS/DB2/MQ (`app/app-authorization-ims-db2-mq/cbl/`) — 8 Programs

| # | Filename | LOC | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----|------|---------|---------|-----------|
| 1 | COPAUA0C.cbl | 1,026 | CICS/IMS/MQ | Card authorization decision — receives MQ request, reads account/customer/xref, returns approval/decline via MQ | MQOPEN, MQGET, MQPUT1, MQCLOSE; CICS READ (account, xref, customer) | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 2 | COPAUS0C.cbl | 1,032 | CICS/IMS/BMS | Summary view of pending authorization messages | CICS READ (3×); SYNCPOINT; SEND/RECEIVE MAP; XCTL | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 3 | COPAUS1C.cbl | 604 | CICS/IMS/BMS | Detail view of single authorization message | CICS LINK; SEND/RECEIVE MAP; SYNCPOINT; XCTL | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 4 | COPAUS2C.cbl | 244 | CICS/IMS/DB2 | Mark authorization message as fraud | EXEC SQL INSERT/SELECT (4×); CICS ASKTIME/FORMATTIME | CIPAUDTY |
| 5 | CBPAUP0C.cbl | 386 | Batch/IMS | Delete expired pending authorization messages from IMS database | IMS DL/I calls (implied via LINKAGE) | CIPAUSMY, CIPAUDTY |
| 6 | PAUDBLOD.CBL | 369 | Batch/IMS | Load authorization data into IMS database | READ INFILE1, INFILE2; CALL CBLTDLI (ISRT, GU) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 7 | PAUDBUNL.CBL | 317 | Batch/IMS | Unload authorization data from IMS database | CALL CBLTDLI (GN, GNP); WRITE OPFILE1, OPFILE2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | DBUNLDGS.CBL | 366 | Batch/IMS | GSAM unload utility — unload IMS segments to flat files | CALL CBLTDLI (GN, GNP, ISRT) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

#### Transaction Type DB2 (`app/app-transaction-type-db2/cbl/`) — 3 Programs

| # | Filename | LOC | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----|------|---------|---------|-----------|
| 1 | COTRTLIC.cbl | 2,098 | CICS/DB2 | List transaction types — cursor-based pagination with DB2 SELECT, delete and update capability | EXEC SQL (SELECT, DELETE, UPDATE, INCLUDE SQLCA); CICS SEND/RECEIVE MAP; SYNCPOINT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, DCLTRTYP |
| 2 | COTRTUPC.cbl | 1,702 | CICS/DB2 | Transaction type update — accept and process updates/deletes with cascading operations | EXEC SQL (SELECT, UPDATE, DELETE, INSERT, INCLUDE DCLTRTYP, DCLTRCAT); CICS SEND/RECEIVE MAP; SYNCPOINT; HANDLE ABEND | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT |
| 3 | COBTUPDT.cbl | 237 | Batch/DB2 | Batch update of transaction types from input file | READ TR-RECORD (INPFILE); EXEC SQL (SELECT, UPDATE, INSERT) | DCLTRTYP |

#### VSAM/MQ (`app/app-vsam-mq/cbl/`) — 2 Programs

| # | Filename | LOC | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----|------|---------|---------|-----------|
| 1 | COACCT01.cbl | 620 | CICS/MQ | Account inquiry via MQ — open request/reply queues, get account data, respond | MQOPEN (3×), MQGET, MQPUT (2×), MQCLOSE (3×); CICS READ, RETRIEVE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 2 | CODATE01.cbl | 524 | CICS/MQ | Date inquiry via MQ — process date validation/conversion requests | MQOPEN (3×), MQGET, MQPUT (2×), MQCLOSE (3×); CICS ASKTIME/FORMATTIME, RETRIEVE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

### 1.3 Summary by Classification

| Classification | Count | % | Total LOC |
|---------------|-------|---|-----------|
| Online (CICS) | 21 | 48% | ~18,300 |
| Pure Batch | 14 | 32% | ~6,400 |
| Batch/IMS | 5 | 11% | ~1,824 |
| CICS/IMS/MQ/DB2 | 2 | 4.5% | ~1,270 |
| Utility | 2 | 4.5% | ~198 |
| **Total** | **44** | **100%** | **~30,175** |

---

## 2. BMS Screen Maps (21 Maps)

Each online CICS program has a paired BMS map defining the 3270 terminal screen layout.

| BMS Map | Paired Program | Screen Purpose | Directory |
|---------|---------------|----------------|-----------|
| COSGN00.bms | COSGN00C | Sign-on screen | app/bms/ |
| COADM01.bms | COADM01C | Admin menu | app/bms/ |
| COMEN01.bms | COMEN01C | Main user menu | app/bms/ |
| COACTUP.bms | COACTUPC | Account update form | app/bms/ |
| COACTVW.bms | COACTVWC | Account view display | app/bms/ |
| COBIL00.bms | COBIL00C | Bill payment form | app/bms/ |
| COCRDLI.bms | COCRDLIC | Credit card list | app/bms/ |
| COCRDSL.bms | COCRDSLC | Credit card detail | app/bms/ |
| COCRDUP.bms | COCRDUPC | Credit card update | app/bms/ |
| COTRN00.bms | COTRN00C | Transaction list | app/bms/ |
| COTRN01.bms | COTRN01C | Transaction detail | app/bms/ |
| COTRN02.bms | COTRN02C | Add transaction | app/bms/ |
| CORPT00.bms | CORPT00C | Report request | app/bms/ |
| COUSR00.bms | COUSR00C | User list | app/bms/ |
| COUSR01.bms | COUSR01C | Add user | app/bms/ |
| COUSR02.bms | COUSR02C | Update user | app/bms/ |
| COUSR03.bms | COUSR03C | Delete user | app/bms/ |
| COPAU00.bms | COPAUS0C | Auth summary | app/app-authorization-ims-db2-mq/bms/ |
| COPAU01.bms | COPAUS1C | Auth detail | app/app-authorization-ims-db2-mq/bms/ |
| COTRTLI.bms | COTRTLIC | Tran type list | app/app-transaction-type-db2/bms/ |
| COTRTUP.bms | COTRTUPC | Tran type update | app/app-transaction-type-db2/bms/ |

---

## 3. JCL Job Catalog (46 Jobs)

### 3.1 Main JCL Jobs (`app/jcl/`) — 38 Jobs

#### VSAM Data Setup Jobs

| Job Name | Purpose | Key Steps |
|----------|---------|-----------|
| ACCTFILE.jcl | Define account VSAM KSDS | STEP05: IDCAMS DELETE cluster; STEP10: IDCAMS DEFINE CLUSTER (KEYS 11 0) |
| CARDFILE.jcl | Define card VSAM KSDS + AIX | CLCIFIL: SDSF close CICS files; STEP05: IDCAMS DELETE cluster+AIX; STEP10: DEFINE CLUSTER |
| CUSTFILE.jcl | Define customer VSAM KSDS | CLCIFIL: SDSF close CUSTDAT; STEP05: IDCAMS DELETE; STEP10: DEFINE CLUSTER |
| XREFFILE.jcl | Define card cross-reference KSDS + AIX | STEP05: IDCAMS DELETE cluster+AIX; STEP10: DEFINE CLUSTER |
| TRANFILE.jcl | Define transaction master VSAM KSDS + AIX | CLCIFIL: SDSF close TRANSACT+CXACAIX; STEP05: DELETE cluster+AIX; STEP10: DEFINE |
| TRANIDX.jcl | Define AIX on transaction processed timestamp | STEP20: DEFINE ALTERNATEINDEX (KEYS 26 304, NONUNIQUEKEY); STEP25: DEFINE PATH |
| TCATBALF.jcl | Define transaction category balance KSDS | STEP05: DELETE; STEP10: DEFINE CLUSTER (KEYS 17 0) |
| DISCGRP.jcl | Define disclosure group KSDS | STEP05: DELETE; STEP10: DEFINE CLUSTER (KEYS 16 0) |
| TRANTYPE.jcl | Define transaction type KSDS | STEP05: DELETE; STEP10: DEFINE CLUSTER (KEYS 2 0) |
| TRANCATG.jcl | Define transaction category KSDS | STEP05: DELETE; STEP10: DEFINE CLUSTER (KEYS 6 0) |
| DEFCUST.jcl | Alternative customer VSAM definition (older format) | STEP05: DELETE; STEP10: DEFINE CLUSTER (KEYS 10 0, RECSIZE 500) |
| ESDSRRDS.jcl | Define ESDS/RRDS test datasets | PREDEL: IEFBR14 delete; STEP01: IEBGENER load |

#### GDG Definition Jobs

| Job Name | Purpose | Key Steps |
|----------|---------|-----------|
| DEFGDGB.jcl | Define GDG bases for batch processing | STEP05: IDCAMS DEFINE GDG — TRANSACT.BKUP (5 gen), TRANSACT.DALY (5 gen), TRANREPT (5 gen) |
| DEFGDGD.jcl | Define DB2-related GDGs | STEP10: DEFINE GDG TRANTYPE.BKUP (5 gen); STEP20: IEBGENER create first generation |
| DALYREJS.jcl | Define GDG for daily rejects | STEP05: IDCAMS DEFINE GDG DALYREJS (5 gen) |
| REPTFILE.jcl | Define GDG for report files | STEP05: IDCAMS DEFINE GDG TRANREPT (10 gen) |

#### Batch Processing Jobs

| Job Name | Purpose | Key Steps | Program Executed |
|----------|---------|-----------|-----------------|
| POSTTRAN.jcl | Post daily transactions | STEP15: EXEC PGM=CBTRN02C | CBTRN02C |
| INTCALC.jcl | Calculate interest | STEP15: EXEC PGM=CBACT04C, PARM='2022071800' | CBACT04C |
| CREASTMT.JCL | Create customer statements | DELDEF01: IDCAMS setup; STEP (implied): EXEC CBSTM03A | CBSTM03A |
| TRANREPT.jcl | Transaction report | STEP05R: PROC REPROC (unload VSAM); STEP05R: SORT; STEP15: EXEC CBTRN03C | CBTRN03C |
| COMBTRAN.jcl | Combine transaction files | STEP05R: SORT (merge BKUP + SYSTRAN → COMBINED) | SORT utility |
| TRANBKP.jcl | Backup transaction master | STEP05R: PROC REPROC (VSAM→PS); STEP05: IDCAMS DELETE/REDEFINE | REPROC proc |
| PRTCATBL.jcl | Print transaction category balance | DELDEF: IEFBR14 delete; STEP05R: PROC REPROC (VSAM→PS report) | REPROC proc |

#### Data Load/Read Jobs

| Job Name | Purpose | Key Steps | Program Executed |
|----------|---------|-----------|-----------------|
| READACCT.jcl | Read account master to sequential file | PREDEL: IEFBR14 delete output; STEP05: EXEC PGM=CBACT01C | CBACT01C |
| READCARD.jcl | Read card master file | STEP05: EXEC PGM=CBACT02C | CBACT02C |
| READCUST.jcl | Read customer master file | STEP05: EXEC PGM=CBCUS01C | CBCUS01C |
| READXREF.jcl | Read cross-reference file | STEP05: EXEC PGM=CBACT03C | CBACT03C |
| DUSRSECJ.jcl | Define and load user security file | PREDEL: IEFBR14; STEP01: IEBGENER (in-stream user data → PS file) | IEBGENER |
| CBEXPORT.jcl | Export customer data for migration | STEP01: IDCAMS DEFINE export VSAM; STEP02: EXEC CBEXPORT | CBEXPORT |
| CBIMPORT.jcl | Import customer data | STEP01: EXEC PGM=CBIMPORT | CBIMPORT |

#### CICS Control Jobs

| Job Name | Purpose | Key Steps |
|----------|---------|-----------|
| CBADMCDJ.jcl | Create/update CICS CSD definitions for CardDemo | STEP1: EXEC PGM=DFHCSDUP — DEFINE/INSTALL CICS resources |
| CLOSEFIL.jcl | Close VSAM files in CICS region | CLCIFIL: SDSF — CEMT SET FIL(…) CLO for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| OPENFIL.jcl | Open VSAM files in CICS region | OPCIFIL: SDSF — CEMT SET FIL(…) OPE for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |

#### Utility Jobs

| Job Name | Purpose | Key Steps |
|----------|---------|-----------|
| WAITSTEP.jcl | Wait for specified centiseconds | WAIT: EXEC PGM=COBSWAIT, PARM=00003600 (36 seconds) |
| FTPJCL.JCL | FTP file transfer | STEP1: EXEC PGM=FTP — transfer to/from mainframe |
| INTRDRJ1.JCL | Internal reader trigger (job 1) | IDCAMS REPRO; STEP01: IEBGENER → submit INTRDRJ2 via INTRDR |
| INTRDRJ2.JCL | Internal reader trigger (job 2) | IDCAMS REPRO (chained from INTRDRJ1) |
| TXT2PDF1.JCL | Convert text to PDF | TXT2PDF: EXEC PGM=IKJEFT1B — %TXT2PDF REXX exec |

### 3.2 Sub-Application JCL Jobs — 8 Jobs

#### Authorization IMS Jobs (`app/app-authorization-ims-db2-mq/jcl/`) — 5 Jobs

| Job Name | Purpose | Key Steps |
|----------|---------|-----------|
| CBPAUP0J.jcl | Purge expired auth messages | STEP01: EXEC PGM=DFSRRC00 (IMS batch) → CBPAUP0C |
| DBPAUTP0.jcl | Unload IMS auth database | STEPDEL: IEFBR14 delete; UNLOAD: EXEC PGM=DFSRRC00 → IMS UNLOAD |
| LOADPADB.JCL | Load auth data into IMS DB | STEP01: EXEC PGM=DFSRRC00 → PAUDBLOD |
| UNLDGSAM.JCL | GSAM unload utility | STEP01: EXEC PGM=DFSRRC00 → DBUNLDGS |
| UNLDPADB.JCL | Unload IMS auth segments | STEP0: IEFBR14; STEP01: EXEC PGM=DFSRRC00 → PAUDBUNL |

#### Transaction Type DB2 Jobs (`app/app-transaction-type-db2/jcl/`) — 3 Jobs

| Job Name | Purpose | Key Steps |
|----------|---------|-----------|
| CREADB21.jcl | Create DB2 tables for transaction types | DB2 DDL execution via IKJEFT01 |
| MNTTRDB2.jcl | Maintain transaction type DB2 data | STEP1: EXEC PGM=IKJEFT01 → COBTUPDT batch update |
| TRANEXTR.jcl | Extract transaction type data from DB2 | DB2 UNLOAD to sequential file |

---

## 4. External Dependencies

| Dependency | Type | Called By | Purpose |
|-----------|------|-----------|---------|
| COBDATFT | Assembler | CBACT01C | Date formatting routine |
| CEE3ABD | LE Runtime | CBACT01C, CBACT02C, CBACT03C, CBCUS01C, CBSTM03A | Abnormal termination handler |
| CEEDAYS | LE Runtime | CSUTLDTC | Date conversion (Lilian days) |
| MVSWAIT | Assembler | COBSWAIT | System wait (centiseconds) |
| CBLTDLI | IMS DL/I | PAUDBLOD, PAUDBUNL, DBUNLDGS | IMS database calls (GU, GN, GNP, ISRT, DLET, REPL) |
| MQOPEN/MQGET/MQPUT/MQCLOSE | MQ API | COPAUA0C, COACCT01, CODATE01 | MQ message queue operations |
| DFHCSDUP | CICS Utility | CBADMCDJ (JCL) | CSD resource definition |
| DFSRRC00 | IMS Batch | IMS JCL jobs | IMS batch region controller |

---

## 5. VSAM Dataset Inventory

| Dataset Name (HLQ: AWS.M2.CARDDEMO) | Type | Key | RECL | Programs Using |
|--------------------------------------|------|-----|------|----------------|
| ACCTDATA.VSAM.KSDS | KSDS | 11,0 | 300 | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, CBEXPORT, CBIMPORT |
| CARDDATA.VSAM.KSDS (+AIX) | KSDS | 16,0 | 150 | CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC, CBEXPORT, CBIMPORT |
| CUSTDATA.VSAM.KSDS | KSDS | 9,0 | 500 | CBCUS01C, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, CBEXPORT, CBIMPORT |
| CARDXREF.VSAM.KSDS (+AIX) | KSDS | 16,0 | 50 | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COTRN02C, COBIL00C, CBEXPORT, CBIMPORT |
| TRANSACT.VSAM.KSDS (+AIX) | KSDS | 16,0 | 350 | CBTRN02C, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C, CBEXPORT, CBIMPORT |
| TCATBALF.VSAM.KSDS | KSDS | 17,0 | 50 | CBACT04C, CBTRN02C |
| DISCGRP.VSAM.KSDS | KSDS | 16,0 | 50 | CBACT04C |
| TRANTYPE.VSAM.KSDS | KSDS | 2,0 | 60 | CBTRN03C, COTRTLIC, COTRTUPC |
| TRANCATG.VSAM.KSDS | KSDS | 6,0 | 60 | CBTRN03C |
| USRSEC (PS→VSAM) | KSDS | 8,0 | 80 | COSGN00C, COUSR00C–03C |
| DALYTRAN.PS | PS | — | 350 | CBTRN01C, CBTRN02C |
| DALYREJS (GDG) | PS/GDG | — | 430 | CBTRN02C |
| TRANSACT.BKUP (GDG) | PS/GDG | — | 350 | TRANBKP, COMBTRAN, TRANREPT |
| TRANREPT (GDG) | PS/GDG | — | varies | CBTRN03C |
| STATEMNT.PS | PS | — | varies | CBSTM03A |
