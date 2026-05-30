# Application Inventory — CardDemo COBOL Estate

> Auto-generated analysis of the COBOL programs and JCL jobs in `uc-legacy-modernization-cobol-to-java`.

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

| # | Filename | Lines | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|----------------|---------|-------------------|---------------------|
| 1 | CBACT01C.cbl | 430 | Batch | Read account VSAM file and write to fixed-length, array, and variable-length output files | READ ACCTFILE (indexed); WRITE OUT-ACCT-REC, ARR-ARRAY-REC, VB records; CALL COBDATFT (date format) | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Batch | Read and print card data file | READ CARDFILE (indexed); CALL CEE3ABD (abend) | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Batch | Read and print account cross-reference data file | READ XREFFILE (indexed); CALL CEE3ABD | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Batch | Interest calculator — reads transaction category balances, cross-references, disclosure groups, and accounts; computes interest and fees; updates account file | READ TCATBAL, XREF, DISCGRP, ACCOUNT, TRANFILE; WRITE TRANFILE; REWRITE ACCOUNT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Batch | Read and print customer data file | READ CUSTFILE (indexed); CALL CEE3ABD | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Batch | Export complete customer profiles from CardDemo files into a single sequential export file for branch migration | READ CUSTOMER-INPUT, ACCOUNT-INPUT, XREF-INPUT, TRANSACTION-INPUT, CARD-INPUT; WRITE EXPORT-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Batch | Import records from export file back into individual VSAM files with validation | READ EXPORT-INPUT; WRITE CUSTOMER-OUTPUT, ACCOUNT-OUTPUT, XREF-OUTPUT, TRANSACTION-OUTPUT, CARD-OUTPUT, ERROR-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | 924 | Batch | Create customer account statements — reads cross-refs, customers, accounts, transactions; generates text and HTML statement files | READ XREF, CUST, ACCT, TRNX files; WRITE STMT-FILE, HTML-FILE; CALL CBSTM03B (I/O submodule) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | 230 | Batch (Module) | I/O submodule called by CBSTM03A — handles file open/read/close for transaction, xref, customer, and account files | READ/OPEN/CLOSE TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE | *(none — uses LINKAGE SECTION)* |
| 10 | CBTRN01C.cbl | 494 | Batch | Process daily transactions — reads daily transaction file, looks up cross-references, reads card and account data for validation | READ DALYTRAN, XREF, CARD, ACCOUNT, CUSTOMER files; WRITE TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | 731 | Batch | Post daily transactions — validates and posts transactions to master file; writes rejected records; updates transaction category balances and account balances | READ DALYTRAN, XREF, ACCOUNT; WRITE TRANSACT, DALYREJS, TCATBAL; REWRITE ACCOUNT | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | 649 | Batch | Transaction report generator — reads transaction master, looks up cross-refs/types/categories, generates formatted daily transaction report | READ TRANSACT, CARDXREF, TRANTYPE, TRANCATG, DATE-PARMS; WRITE REPTFILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | 41 | Batch (Utility) | Wait/delay utility — calls assembler program MVSWAIT | CALL MVSWAIT | *(none)* |
| 14 | CSUTLDTC.cbl | 157 | Batch (Utility) | Date utility — validates and converts dates using LE callable service CEEDAYS | CALL CEEDAYS | *(none)* |
| 15 | COACTUPC.cbl | 4,236 | Online (CICS) | Account update screen — displays/edits account information with full field validation (date, SSN, phone, state, zip) | EXEC CICS READ/WRITE/REWRITE ACCTDAT, CARDXREF, CUSTDAT; EXEC CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×3) |
| 16 | COACTVWC.cbl | 941 | Online (CICS) | Account view screen — read-only display of account, card cross-ref, and customer data | EXEC CICS READ CARDXREF, ACCTDAT, CUSTDAT; EXEC CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 17 | COADM01C.cbl | 288 | Online (CICS) | Admin menu screen — presents administrative options (user CRUD, DB2 transaction type maintenance) | EXEC CICS SEND/RECEIVE MAP; EXEC CICS XCTL to selected program | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | COBIL00C.cbl | 572 | Online (CICS) | Bill payment screen — processes bill payments against accounts with confirmation flow | EXEC CICS READ/WRITE ACCTDAT, CXACAIX (card xref AIX), TRANSACT; EXEC CICS STARTBR/READPREV/ENDBR TRANSACT; EXEC CICS ASKTIME | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 19 | COCRDLIC.cbl | 1,459 | Online (CICS) | Credit card list screen — paginated browse of credit cards with forward/backward navigation | EXEC CICS READ/STARTBR/READNEXT/READPREV/ENDBR CARDDAT; EXEC CICS XCTL to detail/update programs | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 20 | COCRDSLC.cbl | 887 | Online (CICS) | Credit card view/search screen — display card details with account and customer lookup | EXEC CICS READ CARDDAT, CARDXREF, ACCTDAT, CUSTDAT; EXEC CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 21 | COCRDUPC.cbl | 1,560 | Online (CICS) | Credit card update screen — edit card details (name, status, expiry) with validation and write-back | EXEC CICS READ/REWRITE/WRITE CARDDAT; EXEC CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 22 | COMEN01C.cbl | 308 | Online (CICS) | Main menu screen — presents user-level navigation options (account, card, transaction, reports, bill pay) | EXEC CICS SEND/RECEIVE MAP; EXEC CICS XCTL/INQUIRE to selected program | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 23 | CORPT00C.cbl | 649 | Online (CICS) | Transaction report request screen — accepts date range parameters and submits batch report job via internal reader | EXEC CICS WRITE to internal reader; EXEC CICS SEND/RECEIVE MAP | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | COSGN00C.cbl | 260 | Online (CICS) | Sign-on screen — authenticates users against USRSEC file; routes to admin or main menu based on user type | EXEC CICS READ USRSEC; EXEC CICS XCTL to COADM01C or COMEN01C; EXEC CICS ASSIGN | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 25 | COTRN00C.cbl | 699 | Online (CICS) | Transaction list screen — paginated browse of transaction master file | EXEC CICS STARTBR/READNEXT/ENDBR TRANSACT; EXEC CICS XCTL; EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN01C.cbl | 330 | Online (CICS) | Transaction view screen — read-only display of a single transaction record | EXEC CICS READ TRANSACT; EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | COTRN02C.cbl | 783 | Online (CICS) | Transaction add screen — add new transactions with account/card validation, confirmation flow | EXEC CICS READ CXACAIX, CARDXREF, ACCTDAT; EXEC CICS WRITE TRANSACT; EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 28 | COUSR00C.cbl | 695 | Online (CICS) | User list screen — paginated browse of USRSEC file for admin user management | EXEC CICS STARTBR/READNEXT/ENDBR USRSEC; EXEC CICS XCTL; EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR01C.cbl | 299 | Online (CICS) | User add screen — creates new user security records in USRSEC file | EXEC CICS WRITE USRSEC; EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR02C.cbl | 414 | Online (CICS) | User update screen — modifies existing user security records | EXEC CICS READ/REWRITE USRSEC; EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | COUSR03C.cbl | 359 | Online (CICS) | User delete screen — deletes user security records with confirmation | EXEC CICS READ/DELETE USRSEC; EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. COBOL Programs — Authorization Sub-Application (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Lines | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|----------------|---------|-------------------|---------------------|
| 32 | CBPAUP0C.cbl | 386 | Batch (IMS) | Delete expired pending authorization messages from IMS database | EXEC DLI GN/GNP/DLET (IMS reads and deletes) | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | 1,026 | Online (CICS/IMS/MQ) | Card authorization decision program — receives auth request via MQ, validates card/account/customer via CICS files & IMS, sends response via MQ | CALL MQOPEN/MQGET/MQPUT1 (MQ); EXEC CICS READ CARDXREF, ACCTDAT, CUSTDAT; EXEC DLI GU/SCHD/TERM (IMS) | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 34 | COPAUS0C.cbl | 1,032 | Online (CICS/IMS) | Authorization summary view — paginated BMS display of pending authorization summaries from IMS database | EXEC DLI GU/GNP/SCHD/TERM (IMS); EXEC CICS READ CARDXREF, ACCTDAT, CUSTDAT; EXEC CICS SEND/RECEIVE MAP; EXEC CICS SYNCPOINT | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | 604 | Online (CICS/IMS) | Authorization detail view — single auth message detail display from IMS with ability to mark as reviewed | EXEC DLI GU/GNP/REPL (IMS); EXEC CICS SEND/RECEIVE MAP; EXEC CICS LINK/SYNCPOINT | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | 244 | Online (CICS/DB2) | Mark authorization message as fraud — updates IMS auth detail via DB2 with fraud reporting date | EXEC SQL (4 operations); EXEC CICS ASKTIME/FORMATTIME/RETURN | CIPAUDTY |
| 37 | PAUDBLOD.CBL | 369 | Batch (IMS) | IMS database loader — reads flat files and inserts root/child segments into IMS PAUT database | READ INFILE1/INFILE2; CALL CBLTDLI (ISRT, GU) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 38 | PAUDBUNL.CBL | 317 | Batch (IMS) | IMS database unloader — reads IMS segments via GN/GNP and writes to flat output files | CALL CBLTDLI (GN, GNP); WRITE OPFIL1-REC, OPFIL2-REC | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | DBUNLDGS.CBL | 366 | Batch (IMS) | GSAM-based IMS database unloader — similar to PAUDBUNL but uses GSAM for output | CALL CBLTDLI (GN, GNP, ISRT); GSAM output | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

---

## 3. COBOL Programs — Transaction Type DB2 Sub-Application (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Lines | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|----------------|---------|-------------------|---------------------|
| 40 | COBTUPDT.cbl | 237 | Batch (DB2) | Batch update of transaction types from sequential input file to DB2 TRANSACTION_TYPE table | READ TR-RECORD; EXEC SQL INSERT/UPDATE/DELETE on CARDDEMO.TRANSACTION_TYPE | DCLTRTYP (SQL INCLUDE) |
| 41 | COTRTLIC.cbl | 2,098 | Online (CICS/DB2) | Transaction type list screen — paginated DB2 cursor-based browse with inline update/delete capability | EXEC SQL DECLARE/OPEN/FETCH/CLOSE cursors on TRANSACTION_TYPE; EXEC SQL DELETE; EXEC CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, DCLTRTYP (SQL INCLUDE) |
| 42 | COTRTUPC.cbl | 1,702 | Online (CICS/DB2) | Transaction type update screen — DB2-based CRUD for transaction type and category records with delete confirmation | EXEC SQL SELECT/UPDATE/DELETE on TRANSACTION_TYPE, TRANSACTION_CATEGORY; EXEC CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT (SQL INCLUDE) |

---

## 4. COBOL Programs — VSAM-MQ Sub-Application (`app/app-vsam-mq/cbl/`)

| # | Filename | Lines | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-------|----------------|---------|-------------------|---------------------|
| 43 | COACCT01.cbl | 620 | Online (CICS/MQ) | Account inquiry via MQ — receives account inquiry request from MQ queue, reads account VSAM file, returns response via MQ | EXEC CICS RETRIEVE; CALL MQOPEN/MQGET/MQPUT/MQCLOSE (MQ); EXEC CICS READ ACCTDAT | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 44 | CODATE01.cbl | 524 | Online (CICS/MQ) | Date inquiry via MQ — receives date inquiry request from MQ, formats date/time using CICS ASKTIME/FORMATTIME, returns response via MQ | EXEC CICS RETRIEVE/ASKTIME/FORMATTIME; CALL MQOPEN/MQGET/MQPUT/MQCLOSE (MQ) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 5. JCL Job Catalog (`app/jcl/`)

### 5.1 Data Setup / VSAM Definition Jobs

| Job Name | Purpose | Step Sequence |
|----------|---------|---------------|
| ACCTFILE.jcl | Delete, define, and load Account VSAM KSDS | STEP05: IDCAMS (DELETE) → STEP10: IDCAMS (DEFINE CLUSTER) → STEP15: IDCAMS (REPRO from PS) |
| CARDFILE.jcl | Delete, define, and load Card data VSAM KSDS + Alternate Indexes | CLCIFIL: SDSF (close CICS file) → STEP05–STEP15: IDCAMS (DELETE/DEFINE/REPRO card data) → STEP40–STEP60: IDCAMS (define/build card AIX by account) → OPCIFIL: SDSF (open CICS file) |
| CUSTFILE.jcl | Delete, define, and load Customer VSAM KSDS | CLCIFIL: SDSF → STEP05: IDCAMS (DELETE) → STEP10: IDCAMS (DEFINE) → STEP15: IDCAMS (REPRO) → OPCIFIL: SDSF |
| XREFFILE.jcl | Delete, define, and load Card Cross-Reference VSAM KSDS + AIX | STEP05–STEP15: IDCAMS (DELETE/DEFINE/REPRO KSDS) → STEP20–STEP30: IDCAMS (define/build AIX by account) |
| TRANFILE.jcl | Delete, define, and load Transaction Master VSAM KSDS + AIX | CLCIFIL: SDSF → STEP05–STEP15: IDCAMS (DELETE/DEFINE/REPRO) → STEP20–STEP30: IDCAMS (define/build AIX) → OPCIFIL: SDSF |
| TRANIDX.jcl | Define Alternate Indexes on Transaction Master | STEP20–STEP30: IDCAMS (DEFINE AIX/PATH, BLDINDEX) |
| TCATBALF.jcl | Define Transaction Category Balance VSAM KSDS | STEP05: IDCAMS (DELETE) → STEP10: IDCAMS (DEFINE) → STEP15: IDCAMS (REPRO) |
| DISCGRP.jcl | Define Disclosure Group VSAM KSDS | STEP05: IDCAMS (DELETE) → STEP10: IDCAMS (DEFINE) → STEP15: IDCAMS (REPRO) |
| TRANTYPE.jcl | Define Transaction Type VSAM KSDS | STEP05: IDCAMS (DELETE) → STEP10: IDCAMS (DEFINE) → STEP15: IDCAMS (REPRO) |
| TRANCATG.jcl | Define Transaction Category VSAM KSDS | STEP05: IDCAMS (DELETE) → STEP10: IDCAMS (DEFINE) → STEP15: IDCAMS (REPRO) |
| DEFCUST.jcl | Define Customer Data flat file | STEP05: IDCAMS (DELETE) → STEP05: IDCAMS (DEFINE) |
| DALYREJS.jcl | Define GDG base for daily rejects | STEP05: IDCAMS (DEFINE GDG) |
| DEFGDGB.jcl | Define GDG bases for batch output files | STEP05: IDCAMS (DEFINE GDG bases) |
| DEFGDGD.jcl | Define GDG bases for DB2-related datasets | STEP10–STEP60: IDCAMS/IEBGENER (DEFINE GDG, MODEL DSCB, REPRO) |
| REPTFILE.jcl | Define GDG base for report files | STEP05: IDCAMS (DEFINE GDG) |
| DUSRSECJ.jcl | Define User Security VSAM KSDS | PREDEL: IEFBR14 → STEP01: IEBGENER (create flat) → STEP02: IDCAMS (DEFINE) → STEP03: IDCAMS (REPRO) |
| ESDSRRDS.jcl | Define ESDS and RRDS VSAM datasets | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02–STEP05: IDCAMS (DEFINE/REPRO ESDS, RRDS) |

### 5.2 Batch Processing Jobs

| Job Name | Purpose | Step Sequence |
|----------|---------|---------------|
| READACCT.jcl | Read and report on Account data | PREDEL: IEFBR14 → STEP05: **CBACT01C** |
| READCARD.jcl | Read and report on Card data | STEP05: **CBACT02C** |
| READCUST.jcl | Read and report on Customer data | STEP05: **CBCUS01C** |
| READXREF.jcl | Read and report on Cross-Reference data | STEP05: **CBACT03C** |
| INTCALC.jcl | Interest and fee calculation | STEP15: **CBACT04C** (PARM='2022071800') |
| POSTTRAN.jcl | Post daily transactions to master | STEP15: **CBTRN02C** |
| TRANREPT.jcl | Generate daily transaction report | STEP05R: REPROC (proc) → STEP05R: SORT → STEP10R: **CBTRN03C** |
| CREASTMT.JCL | Create customer account statements | DELDEF01: IDCAMS → STEP010: SORT → STEP020: IDCAMS → STEP030: IEFBR14 → STEP040: **CBSTM03A** |
| CBEXPORT.jcl | Export CardDemo data for migration | STEP01: IDCAMS (verify) → STEP02: **CBEXPORT** |
| CBIMPORT.jcl | Import CardDemo data from export file | STEP01: **CBIMPORT** |
| COMBTRAN.jcl | Combine/merge transaction files | STEP05R: SORT → STEP10: IDCAMS (REPRO) |
| TRANBKP.jcl | Backup and reset Transaction Master | STEP05R: REPROC → STEP05: IDCAMS (REPRO backup) → STEP10: IDCAMS (DELETE master, cond) |
| PRTCATBL.jcl | Print Transaction Category Balance file | DELDEF: IEFBR14 → STEP05R: REPROC → STEP10R: SORT |
| WAITSTEP.jcl | Wait/delay utility job | WAIT: **COBSWAIT** |

### 5.3 CICS / Utility Jobs

| Job Name | Purpose | Step Sequence |
|----------|---------|---------------|
| OPENFIL.jcl | Open CICS files via SDSF | OPCIFIL: SDSF |
| CLOSEFIL.jcl | Close CICS files via SDSF | CLCIFIL: SDSF |
| CBADMCDJ.jcl | Load CICS CSD definitions | STEP1: DFHCSDUP |
| INTRDRJ1.JCL | Internal reader job — submit JCL for transaction report pipeline | IDCAMS → STEP01: IEBGENER |
| INTRDRJ2.JCL | Internal reader job — submit JCL for transaction report pipeline | IDCAMS |
| FTPJCL.JCL | FTP utility — transfer files | STEP1: FTP |
| TXT2PDF1.JCL | Convert text report to PDF | *(PDF conversion utility)* |

### 5.4 Sub-Application JCL

| Sub-App | Job Name | Purpose | Step Sequence |
|---------|----------|---------|---------------|
| auth-ims-db2-mq | CBPAUP0J.jcl | Run expired auth purge batch | **CBPAUP0C** |
| auth-ims-db2-mq | DBPAUTP0.jcl | IMS DB operations for auth data | IMS utility |
| auth-ims-db2-mq | LOADPADB.JCL | Load IMS pending auth database | **PAUDBLOD** |
| auth-ims-db2-mq | UNLDPADB.JCL | Unload IMS pending auth database | **PAUDBUNL** |
| auth-ims-db2-mq | UNLDGSAM.JCL | Unload IMS database via GSAM | **DBUNLDGS** |
| txn-type-db2 | CREADB21.jcl | Create DB2 tables for transaction types | DB2 DDL |
| txn-type-db2 | TRANEXTR.jcl | Extract transaction type data | DB2 UNLOAD |
| txn-type-db2 | MNTTRDB2.jcl | Maintain transaction type DB2 data | **COBTUPDT** |

---

## 6. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Batch programs | 16 |
| Online (CICS) programs | 21 |
| IMS programs | 5 |
| MQ-integrated programs | 4 |
| DB2-integrated programs | 4 |
| Utility programs | 2 |
| Total lines of COBOL | 27,350 |
| Total JCL jobs (main) | 38 |
| Total JCL jobs (sub-apps) | 8 |
| BMS map sets | 20 |
| Copybooks (main) | 30 |
| Copybooks (sub-apps) | 17 |
