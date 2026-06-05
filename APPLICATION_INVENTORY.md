# Application Inventory — CardDemo COBOL Estate

> **Total Programs:** 44 | **Total LOC:** 27,350 | **Copybooks:** 47 | **JCL Jobs:** 43 | **Procedures:** 2

---

## 1. Main Programs — `app/cbl/`

### 1.1 Online (CICS) Programs

| # | File | Program-ID | Purpose | LOC | Key I/O | Copybooks |
|---|------|-----------|---------|-----|---------|-----------|
| 1 | `app/cbl/COSGN00C.cbl` | COSGN00C | Sign-on screen — authenticates users, reads USRSEC file, XCTLs to COADM01C (admin) or COMEN01C (regular user) | 260 | VSAM: USRSEC (READ) | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, COSGN00 |
| 2 | `app/cbl/COADM01C.cbl` | COADM01C | Admin menu — displays admin options (user CRUD, DB2 tran type mgmt), XCTLs to selected program | 288 | None (menu only) | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | `app/cbl/COMEN01C.cbl` | COMEN01C | Main menu — hub for regular users with 11 options (account, card, transaction, report, bill pay, auth view), XCTLs to function programs | 308 | None (menu only) | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | `app/cbl/COACTVWC.cbl` | COACTVWC | Account view — displays account details, card info, and customer data for a given account ID using STARTBR/READNEXT browsing | 941 | VSAM: ACCTDAT (READ), CARDFILE (STARTBR/READNEXT/READPREV/ENDBR), CUSTFILE (READ), CARDXREF (READ) | COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, COACTVW, CSSTRPFY, CVCRD01Y |
| 5 | `app/cbl/COACTUPC.cbl` | COACTUPC | Account update — largest program in estate; exhaustive field-level validation (date, SSN, phone, state, ZIP), reads/rewrites account/card/customer records. Uses COPY REPLACING macro (CSSETATY) 113+ times for screen attributes | 4,236 | VSAM: ACCTDAT (READ/REWRITE), CARDFILE (STARTBR/READNEXT/READPREV/ENDBR), CUSTFILE (READ), CARDXREF (READ) | COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHAID, DFHBMSCA, COACTVW, CSSTRPFY, CSSETATY (×113), CSUTLDWY, CSUTLDPY, CSLKPCDY, CVCRD01Y, CODATECN |
| 6 | `app/cbl/COCRDLIC.cbl` | COCRDLIC | Credit card list — paginated browse of credit cards with STARTBR/READNEXT/READPREV, supports filter by account for non-admin | 1,459 | VSAM: CARDFILE (STARTBR/READNEXT/READPREV/ENDBR) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | `app/cbl/COCRDSLC.cbl` | COCRDSLC | Credit card view — displays card details with account and customer lookup | 887 | VSAM: CARDFILE (READ), ACCTDAT (READ), CUSTFILE (READ) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | `app/cbl/COCRDUPC.cbl` | COCRDUPC | Credit card update — updates card details (status, expiry, name) with validation | 1,560 | VSAM: CARDFILE (READ/REWRITE), ACCTDAT (READ) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | `app/cbl/COTRN00C.cbl` | COTRN00C | Transaction list — paginated browse of transactions with STARTBR/READNEXT/READPREV pattern, filters by card/account | 699 | VSAM: TRANSACT (STARTBR/READNEXT/READPREV/ENDBR), CARDXREF (READ) | COCOM01Y, CVTRA05Y, CVACT01Y, CVACT03Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, COTRN00, CSSTRPFY, CVCRD01Y |
| 10 | `app/cbl/COTRN01C.cbl` | COTRN01C | Transaction view — displays single transaction detail | 330 | VSAM: TRANSACT (READ) | COCOM01Y, CVTRA05Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, COTRN01, CSSTRPFY, CVCRD01Y |
| 11 | `app/cbl/COTRN02C.cbl` | COTRN02C | Transaction add — allows adding new transactions with validation of card number, amount, type, and category | 783 | VSAM: TRANSACT (WRITE), CARDXREF (READ), CARDFILE (READ), TRANTYPE (READ), TRANCATG (READ) | COCOM01Y, CVTRA05Y, CVACT03Y, CVACT02Y, CVTRA03Y, CVTRA04Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHAID, DFHBMSCA, COTRN02, CSSTRPFY, CVCRD01Y |
| 12 | `app/cbl/CORPT00C.cbl` | CORPT00C | Transaction reports — submits batch JCL via CICS WRITEQ TD (extra-partition TDQ) for INTRDRJ1/INTRDRJ2 | 649 | TDQ: WRITEQ TD (INTRDR queue) | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, CORPT00 |
| 13 | `app/cbl/COBIL00C.cbl` | COBIL00C | Bill payment — processes bill payments by updating account balance | 572 | VSAM: ACCTDAT (READ/REWRITE), TRANSACT (WRITE), CARDXREF (READ) | COCOM01Y, CVACT01Y, CVACT03Y, CVTRA05Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, COBIL00 |
| 14 | `app/cbl/COUSR00C.cbl` | COUSR00C | User list — paginated browse of security user records with STARTBR/READNEXT/READPREV | 695 | VSAM: USRSEC (STARTBR/READNEXT/READPREV/ENDBR) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | `app/cbl/COUSR01C.cbl` | COUSR01C | User add — adds new admin/regular user to USRSEC file | 299 | VSAM: USRSEC (WRITE) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | `app/cbl/COUSR02C.cbl` | COUSR02C | User update — updates existing user credentials and type | 414 | VSAM: USRSEC (READ/REWRITE) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | `app/cbl/COUSR03C.cbl` | COUSR03C | User delete — deletes user from USRSEC file with confirmation | 359 | VSAM: USRSEC (READ/DELETE) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 1.2 Batch Programs

| # | File | Program-ID | Purpose | LOC | Key I/O | Copybooks |
|---|------|-----------|---------|-----|---------|-----------|
| 18 | `app/cbl/CBACT01C.cbl` | CBACT01C | Read account master — reads VSAM ACCTFILE, writes fixed/variable-length output files, calls COBDATFT for date formatting | 430 | VSAM: ACCTFILE (READ); Sequential: OUTFILE, ARRYFILE, VBRCFILE (WRITE) | CVACT01Y |
| 19 | `app/cbl/CBACT02C.cbl` | CBACT02C | Read card master — reads VSAM CARDFILE sequentially, displays records, calls CEE3ABD on error | 178 | VSAM: CARDFILE (READ) | CVACT02Y |
| 20 | `app/cbl/CBACT03C.cbl` | CBACT03C | Read card cross-reference — reads VSAM XREFFILE sequentially, displays records, calls CEE3ABD on error | 178 | VSAM: XREFFILE (READ) | CVACT03Y |
| 21 | `app/cbl/CBACT04C.cbl` | CBACT04C | Interest calculation — calculates interest on account balances using disclosure group rates, updates account records | 652 | VSAM: TCATBALF (READ), DISCGRP (READ), ACCTFILE (READ/REWRITE), XREFFILE (READ) | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y |
| 22 | `app/cbl/CBCUS01C.cbl` | CBCUS01C | Read customer master — reads VSAM CUSTFILE sequentially, displays records, calls CEE3ABD on error | 178 | VSAM: CUSTFILE (READ) | CVCUS01Y |
| 23 | `app/cbl/CBTRN01C.cbl` | CBTRN01C | Read daily transactions — reads DALYTRAN sequential file, displays records | 494 | Sequential: DALYTRAN (READ) | CVTRA06Y |
| 24 | `app/cbl/CBTRN02C.cbl` | CBTRN02C | Post transactions — core batch posting of daily transactions to master transaction file, updates account balances and category balances | 731 | Sequential: DALYTRAN (READ); VSAM: TRANSACT (WRITE), ACCTFILE (READ/REWRITE), XREFFILE (READ), TCATBALF (READ/REWRITE/WRITE) | CVTRA05Y, CVTRA06Y, CVACT01Y, CVACT03Y, CVTRA01Y |
| 25 | `app/cbl/CBTRN03C.cbl` | CBTRN03C | Transaction report — generates formatted daily transaction report with type/category lookup | 649 | Sequential: TRANFILE (READ), TRANREPT (WRITE); VSAM: CARDXREF (READ), TRANTYPE (READ), TRANCATG (READ); DATEPARM (READ) | CVTRA05Y, CVTRA03Y, CVTRA04Y, CVACT03Y, CVTRA07Y |
| 26 | `app/cbl/CBSTM03A.CBL` | CBSTM03A | Statement generation — generates text and HTML customer statements, calls CBSTM03B for I/O operations | 924 | VSAM: XREFFILE (READ), CUSTFILE (READ), ACCTFILE (READ), TRNXFILE (STARTBR/READNEXT/ENDBR); Sequential: STMTFILE (WRITE), HTMLFILE (WRITE) | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, COSTM01 |
| 27 | `app/cbl/CBSTM03B.CBL` | CBSTM03B | Statement I/O submodule — called by CBSTM03A, handles low-level file I/O for statement generation | 230 | Sequential: STMTFILE (WRITE), HTMLFILE (WRITE) | COSTM01, CVACT01Y, CVACT03Y, CVCUS01Y |
| 28 | `app/cbl/CBEXPORT.cbl` | CBEXPORT | Data export — exports customer, account, card, transaction, and cross-reference data to multi-record sequential file for branch migration | 582 | VSAM: CUSTFILE (READ), ACCTFILE (READ), CARDFILE (READ), TRANSACT (READ), XREFFILE (READ); Sequential: EXPFILE (WRITE) | CVCUS01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVTRA05Y, CVEXPORT |
| 29 | `app/cbl/CBIMPORT.cbl` | CBIMPORT | Data import — imports multi-record export file, splits into separate normalized files with validation and error handling | 487 | Sequential: EXPFILE (READ), CUSTOUT (WRITE), ACCTOUT (WRITE), XREFOUT (WRITE), TRNXOUT (WRITE), ERROUT (WRITE) | CVEXPORT |
| 30 | `app/cbl/COBSWAIT.cbl` | COBSWAIT | Wait utility — pauses execution for specified centiseconds using assembler MVSWAIT routine | 41 | None | (none) |
| 31 | `app/cbl/CSUTLDTC.cbl` | CSUTLDTC | Date validation utility — validates dates using LE CEEDAYS service, called from CSUTLDPY copybook | 157 | None | (none) |

---

## 2. IMS/DB2/MQ Sub-app — `app/app-authorization-ims-db2-mq/cbl/`

| # | File | Program-ID | Purpose | LOC | Classification | Key I/O | Copybooks |
|---|------|-----------|---------|-----|---------------|---------|-----------|
| 32 | `app/app-authorization-ims-db2-mq/cbl/CBPAUP0C.cbl` | CBPAUP0C | Expired authorization purge — batch IMS program that deletes expired pending authorizations using GN/GNP/DLET DL/I calls | 386 | Batch (IMS BMP) | IMS: PAUTH segments (GN, GNP, DLET) | CIPAUDTY, CIPAUSMY, CCPAUERY, IMSFUNCS |
| 33 | `app/app-authorization-ims-db2-mq/cbl/COPAUA0C.cbl` | COPAUA0C | Authorization decision — CICS program spanning IMS + DB2 + MQ; performs auth check via IMS lookup, DB2 fraud insert, MQ message exchange | 1,026 | Online (CICS + IMS + DB2 + MQ) | IMS: PAUTH (GU, SCHD, TERM); DB2: transaction_types table (EXEC SQL INSERT); MQ: MQOPEN, MQGET, MQPUT1 | CIPAUDTY, CIPAUSMY, CCPAURQY, CCPAURLY, CCPAUERY, IMSFUNCS |
| 34 | `app/app-authorization-ims-db2-mq/cbl/COPAUS0C.cbl` | COPAUS0C | Authorization summary — CICS program that browses pending authorization summary by account using IMS GU/GNP | 1,032 | Online (CICS + IMS) | IMS: PAUTH summary segments (GU, GNP) | CIPAUSMY, CIPAUDTY, CCPAUERY, IMSFUNCS, COCOM01Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 35 | `app/app-authorization-ims-db2-mq/cbl/COPAUS1C.cbl` | COPAUS1C | Authorization detail — CICS program showing auth detail records with update capability via IMS REPL | 604 | Online (CICS + IMS) | IMS: PAUTH detail segments (GU, GNP, REPL) | CIPAUDTY, CIPAUSMY, CCPAUERY, IMSFUNCS, COCOM01Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 36 | `app/app-authorization-ims-db2-mq/cbl/COPAUS2C.cbl` | COPAUS2C | Fraud marking — CICS program that marks authorization as fraud via DB2 insert | 244 | Online (CICS + DB2) | DB2: fraud reporting table (EXEC SQL INSERT) | CIPAUDTY, CCPAUERY, COCOM01Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 37 | `app/app-authorization-ims-db2-mq/cbl/PAUDBLOD.CBL` | PAUDBLOD | IMS database load — batch program to load pending authorization IMS database segments using ISRT/GU | 369 | Batch (IMS) | IMS: PAUTH segments (ISRT, GU); Sequential: input file (READ) | CIPAUDTY, CIPAUSMY, IMSFUNCS |
| 38 | `app/app-authorization-ims-db2-mq/cbl/PAUDBUNL.CBL` | PAUDBUNL | IMS database unload — batch program to unload pending authorization IMS database using GN/GNP | 317 | Batch (IMS) | IMS: PAUTH segments (GN, GNP); Sequential: output file (WRITE) | CIPAUDTY, CIPAUSMY, IMSFUNCS |
| 39 | `app/app-authorization-ims-db2-mq/cbl/DBUNLDGS.CBL` | DBUNLDGS | GSAM unload — batch program to unload IMS database via GSAM using GN/GNP/ISRT | 366 | Batch (IMS/GSAM) | IMS: PAUTH segments (GN, GNP); GSAM: output (ISRT) | CIPAUDTY, CIPAUSMY, IMSFUNCS |

---

## 3. Transaction Type DB2 Sub-app — `app/app-transaction-type-db2/cbl/`

| # | File | Program-ID | Purpose | LOC | Classification | Key I/O | Copybooks |
|---|------|-----------|---------|-----|---------------|---------|-----------|
| 40 | `app/app-transaction-type-db2/cbl/COBTUPDT.cbl` | COBTUPDT | DB2 table maintenance — batch utility for transaction type table maintenance via TSO | 237 | Batch (DB2) | DB2: TRANSACTION_TYPE, TRANSACTION_TYPE_CATEGORY tables (SELECT, INSERT, UPDATE, DELETE) | CSDB2RWY, CSDB2RPY |
| 41 | `app/app-transaction-type-db2/cbl/COTRTLIC.cbl` | COTRTLIC | Transaction type list — CICS program with cursor-based DB2 pagination for transaction types | 2,098 | Online (CICS + DB2) | DB2: TRANSACTION_TYPE, TRANSACTION_TYPE_CATEGORY tables (DECLARE CURSOR, OPEN, FETCH, CLOSE) | COCOM01Y, CVCRD01Y, DFHAID, DFHBMSCA, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CSDB2RWY, CSDB2RPY, CSSTRPFY, COTRTLI |
| 42 | `app/app-transaction-type-db2/cbl/COTRTUPC.cbl` | COTRTUPC | Transaction type update — CICS program for add/update/delete of transaction types with cascading category deletes | 1,702 | Online (CICS + DB2) | DB2: TRANSACTION_TYPE, TRANSACTION_TYPE_CATEGORY tables (SELECT, INSERT, UPDATE, DELETE with cascading) | COCOM01Y, CVCRD01Y, DFHAID, DFHBMSCA, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CSDB2RWY, CSDB2RPY, CSSTRPFY, COTRTUP |

---

## 4. VSAM/MQ Sub-app — `app/app-vsam-mq/cbl/`

| # | File | Program-ID | Purpose | LOC | Classification | Key I/O | Copybooks |
|---|------|-----------|---------|-----|---------------|---------|-----------|
| 43 | `app/app-vsam-mq/cbl/COACCT01.cbl` | COACCT01 | Account inquiry via MQ — receives account inquiry request from MQ queue, reads VSAM account file, sends response back via MQ | 620 | Batch (MQ + VSAM) | MQ: MQGET/MQPUT1; VSAM: ACCTFILE (READ) | CVACT01Y |
| 44 | `app/app-vsam-mq/cbl/CODATE01.cbl` | CODATE01 | Date inquiry via MQ — receives date formatting request from MQ queue, formats date, sends response back via MQ | 524 | Batch (MQ) | MQ: MQGET/MQPUT1 | (none — inline working storage) |

---

## 5. JCL Jobs

### 5.1 VSAM Data Setup Jobs (Define/Load Clusters)

| Job | File | Purpose | Steps |
|-----|------|---------|-------|
| ACCTFILE | `app/jcl/ACCTFILE.jcl` | Define KSDS cluster for account data (RECLN 300, KEY 11,0) | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO (load from PS) |
| CARDFILE | `app/jcl/CARDFILE.jcl` | Define KSDS cluster for card data (RECLN 150, KEY 16,0) | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO |
| CUSTFILE | `app/jcl/CUSTFILE.jcl` | Define KSDS cluster for customer data (RECLN 500, KEY 9,0) | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO |
| XREFFILE | `app/jcl/XREFFILE.jcl` | Define KSDS cluster for card cross-reference (RECLN 50, KEY 16,0) with AIX | STEP05: IDCAMS DELETE (cluster + AIX); STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO; STEP20: DEFINE AIX; STEP25: DEFINE PATH; STEP30: BLDINDEX |
| TRANFILE | `app/jcl/TRANFILE.jcl` | Define KSDS cluster for transaction master (RECLN 350, KEY 16,0) with AIX | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO; STEP20: DEFINE AIX; STEP25: DEFINE PATH; STEP30: BLDINDEX; OPCIFIL: SDSF open files |
| TRANTYPE | `app/jcl/TRANTYPE.jcl` | Define KSDS cluster for transaction type (RECLN 60, KEY 2,0) | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO |
| TRANCATG | `app/jcl/TRANCATG.jcl` | Define KSDS cluster for transaction category (RECLN 60, KEY 6,0) | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO |
| DISCGRP | `app/jcl/DISCGRP.jcl` | Define KSDS cluster for disclosure group (RECLN 50, KEY 16,0) | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO |
| TCATBALF | `app/jcl/TCATBALF.jcl` | Define KSDS cluster for tran category balance (RECLN 50, KEY 17,0) | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO |
| DUSRSECJ | `app/jcl/DUSRSECJ.jcl` | Define KSDS cluster for user security (RECLN 80, KEY 8,0) | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO |
| DEFCUST | `app/jcl/DEFCUST.jcl` | Alternate customer file definition | IDCAMS DEFINE |
| DEFGDGB | `app/jcl/DEFGDGB.jcl` | Define GDG base for backup files | IDCAMS DEFINE GDG |
| DEFGDGD | `app/jcl/DEFGDGD.jcl` | Define GDG base for daily files | IDCAMS DEFINE GDG |
| ESDSRRDS | `app/jcl/ESDSRRDS.jcl` | Define ESDS/RRDS clusters | IDCAMS DEFINE |

### 5.2 Batch Processing Jobs

| Job | File | Purpose | Steps |
|-----|------|---------|-------|
| CLOSEFIL | `app/jcl/CLOSEFIL.jcl` | Close CICS files before batch processing | CLCIFIL: SDSF — issues CEMT SET FIL CLO for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| POSTTRAN | `app/jcl/POSTTRAN.jcl` | Post daily transactions to master file | EXEC PGM=CBTRN02C — reads DALYTRAN, writes TRANSACT, updates ACCTFILE |
| INTCALC | `app/jcl/INTCALC.jcl` | Calculate interest on accounts | EXEC PGM=CBACT04C — reads TCATBALF, DISCGRP, XREFFILE, ACCTFILE; rewrites ACCTFILE |
| TRANBKP | `app/jcl/TRANBKP.jcl` | Backup transaction file | REPROC PROC → IDCAMS REPRO from VSAM to GDG sequential |
| COMBTRAN | `app/jcl/COMBTRAN.jcl` | Combine transactions (backup + system-generated) | STEP05R: SORT merge two files; STEP10: IDCAMS REPRO to VSAM master |
| CREASTMT | `app/jcl/CREASTMT.JCL` | Create customer statements | DELDEF01: IDCAMS setup; STEP10: PGM=CBSTM03A — reads XREF, CUST, ACCT, TRNX; writes STMT + HTML files |
| TRANREPT | `app/jcl/TRANREPT.jcl` | Generate daily transaction report | STEP05R: REPROC PROC (unload VSAM); STEP05R: SORT (filter by date); STEP10R: PGM=CBTRN03C (report) |
| TRANIDX | `app/jcl/TRANIDX.jcl` | Build alternate index on transaction file | STEP20: DEFINE AIX; STEP25: DEFINE PATH; STEP30: BLDINDEX |
| OPENFIL | `app/jcl/OPENFIL.jcl` | Open CICS files after batch processing | OPCIFIL: SDSF — issues CEMT SET FIL OPE for TRANSACT, CXACAIX |
| WAITSTEP | `app/jcl/WAITSTEP.jcl` | Wait step — pauses for centiseconds (used between batch steps) | WAIT: PGM=COBSWAIT PARM=00003600 (36 seconds) |
| PRTCATBL | `app/jcl/PRTCATBL.jcl` | Print transaction category balance report | DELDEF: delete old report; STEP05R: REPROC (unload VSAM); STEP10R: SORT (format report) |
| DALYREJS | `app/jcl/DALYREJS.jcl` | Process daily rejections | SORT/filter rejected transactions |
| TXT2PDF1 | `app/jcl/TXT2PDF1.JCL` | Convert text statement to PDF | TXT2PDF: PGM=IKJEFT1B with TXT2PDF REXX exec |
| REPTFILE | `app/jcl/REPTFILE.jcl` | Define report file cluster | IDCAMS operations |
| CBADMCDJ | `app/jcl/CBADMCDJ.jcl` | Admin card job | Batch card administration |

### 5.3 Data Utility Jobs

| Job | File | Purpose | Steps |
|-----|------|---------|-------|
| READACCT | `app/jcl/READACCT.jcl` | Read account master for diagnostics | PREDEL: IEFBR14 cleanup; STEP05: PGM=CBACT01C — reads ACCTFILE, writes PSCOMP/ARRYFILE/VBRCFILE |
| READCARD | `app/jcl/READCARD.jcl` | Read card master for diagnostics | STEP05: PGM=CBACT02C — reads CARDFILE |
| READCUST | `app/jcl/READCUST.jcl` | Read customer master for diagnostics | STEP05: PGM=CBCUS01C — reads CUSTFILE |
| READXREF | `app/jcl/READXREF.jcl` | Read card cross-reference for diagnostics | STEP05: PGM=CBACT03C — reads XREFFILE |
| CBEXPORT | `app/jcl/CBEXPORT.jcl` | Export data for branch migration | STEP01: PGM=CBEXPORT — reads all VSAM files, writes multi-record EXPFILE |
| CBIMPORT | `app/jcl/CBIMPORT.jcl` | Import data from export file | STEP01: PGM=CBIMPORT — reads EXPFILE, writes normalized CUSTOUT/ACCTOUT/XREFOUT/TRNXOUT/ERROUT |
| FTPJCL | `app/jcl/FTPJCL.JCL` | FTP file transfer | FTP step for data transfer |
| INTRDRJ1 | `app/jcl/INTRDRJ1.JCL` | Internal reader job 1 — submitted by CORPT00C via TDQ | Report generation via internal reader |
| INTRDRJ2 | `app/jcl/INTRDRJ2.JCL` | Internal reader job 2 — submitted by CORPT00C via TDQ | Report generation via internal reader |

### 5.4 Sub-Application JCL

| Job | File | Purpose | Steps |
|-----|------|---------|-------|
| CBPAUP0J | `app/app-authorization-ims-db2-mq/jcl/CBPAUP0J.jcl` | Purge expired authorizations | STEP01: PGM=DFSRRC00 PARM='BMP,CBPAUP0C,PSBPAUTB' |
| DBPAUTP0 | `app/app-authorization-ims-db2-mq/jcl/DBPAUTP0.jcl` | IMS database unload (PAUTH) | STEPDEL: IEFBR14 cleanup; UNLOAD: PGM=DFSRRC00 (ULU,DFSURGU0,DBPAUTP0) |
| CREADB21 | `app/app-transaction-type-db2/jcl/CREADB21.jcl` | Create DB2 tables | STEP10: PGM=IKJEFT01 — CREATE TABLE transaction_types/transaction_categories |
| MNTTRDB2 | `app/app-transaction-type-db2/jcl/MNTTRDB2.jcl` | Maintain transaction type DB2 data | EXEC PGM=COBTUPDT |
| TRANEXTR | `app/app-transaction-type-db2/jcl/TRANEXTR.jcl` | Extract DB2 data to VSAM | STEP40: DSNTIAUL extract from TRANSACTION_TYPE; STEP50: DSNTIAUL extract from TRANSACTION_TYPE_CATEGORY |

### 5.5 Procedures

| Proc | File | Purpose |
|------|------|---------|
| REPROC | `app/proc/REPROC.prc` | Reusable IDCAMS REPRO procedure — loads/unloads VSAM files from/to sequential datasets |
| TRANREPT | `app/proc/TRANREPT.prc` | Transaction report procedure — chains REPROC → SORT → CBTRN03C for end-to-end report generation |

---

## Summary Statistics

| Category | Count | Total LOC |
|----------|-------|-----------|
| Online (CICS) Programs | 21 | 18,274 |
| Batch Programs | 14 | 6,854 |
| Batch (IMS) Programs | 5 | 1,804 |
| Batch (MQ) Programs | 2 | 1,144 |
| Batch (DB2) Programs | 1 | 237 |
| Utility Programs | 1 | 157 |
| **Total Programs** | **44** | **27,350** (est.) |
| JCL Jobs | 43 | — |
| Procedures | 2 | — |
| Copybooks | 47 | — |
