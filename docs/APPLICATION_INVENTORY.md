# Application Inventory — CardDemo COBOL Estate

> Generated: 2026-06-05 | Estate: 44 COBOL programs, 47 copybooks, 46 JCL jobs

---

## 1. Main Programs (`app/cbl/`)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read account file and write into multiple output formats (flat, array, variable-block) | Batch | **Read:** ACCTFILE; **Write:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and print card data file | Batch | **Read:** CARDFILE | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and print account cross-reference data file | Batch | **Read:** XREFFILE | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Calculate interest on account balances using disclosure group rates | Batch | **Read:** TCATBALF, XREFFILE, DISCGRP, TRANSACT; **Rewrite:** ACCTFILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and print customer data file | Batch | **Read:** CUSTFILE | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export normalized CardDemo files into a single multi-record sequential file for branch migration | Batch | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; **Write:** EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Import multi-record export file and split into individual entity files with validation | Batch | **Read:** EXPFILE; **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBTRN01C.cbl | CBTRN01C | Validate daily transactions against customer, card, and account files | Batch | **Read:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE; **Write:** TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | CBTRN02C.cbl | CBTRN02C | Post validated daily transactions to master transaction file; update account balances and category balances | Batch | **Read:** DALYTRAN, XREFFILE; **Rewrite:** ACCTFILE, TCATBALF; **Write:** TRANFILE, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | CBTRN03C | Generate daily transaction report with category/type descriptions and totals | Batch | **Read:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; **Write:** TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | CBSTM03A.CBL | CBSTM03A | Generate customer statements (text + HTML) by calling CBSTM03B for I/O | Batch | **Read:** (via CBSTM03B); **Write:** STMTFILE, HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 12 | CBSTM03B.CBL | CBSTM03B | I/O sub-module for statement generation — handles file open/close/read/write for CBSTM03A | Batch (sub-module) | **Read:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE; **Write:** (output via caller) | — |
| 13 | COACTUPC.cbl | COACTUPC | Account update — full field editing with validation (date, SSN, phone, state, ZIP) via CICS screens | Online (CICS) | **VSAM:** ACCTFILE (READ/REWRITE), CARDXREF (READ), CUSTFILE (READ) | COCOM01Y, CVACT01Y, CVACT03Y, CVCUS01Y, CVCRD01Y, CSLKPCDY, CSSETATY (×39), CSSTRPFY, CSUTLDPY, CSUTLDWY, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHBMSCA, DFHAID |
| 14 | COACTVWC.cbl | COACTVWC | Account view — read-only display of account, customer, and card data | Online (CICS) | **VSAM:** ACCTFILE (READ), CARDXREF (READNEXT), CUSTFILE (READ), CARDFILE (READ) | COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVCRD01Y, CSSTRPFY, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHBMSCA, DFHAID |
| 15 | COADM01C.cbl | COADM01C | Admin menu — routes admin users to User CRUD and Transaction Type screens | Online (CICS) | — (menu routing only) | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | COBIL00C | Bill payment — process payments against account balances | Online (CICS) | **VSAM:** ACCTFILE (READ/REWRITE), CARDXREF (READ), TRANSACT (WRITE) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | COBSWAIT | Wait/pause utility — calls assembler MVSWAIT routine | Batch (utility) | — | — |
| 18 | COCRDLIC.cbl | COCRDLIC | Credit card list — paginated browse of cards with STARTBR/READNEXT/READPREV | Online (CICS) | **VSAM:** CARDFILE (STARTBR, READNEXT, READPREV, ENDBR) | COCOM01Y, CVCRD01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, DFHBMSCA, DFHAID |
| 19 | COCRDSLC.cbl | COCRDSLC | Credit card detail view — display card, customer, and account info | Online (CICS) | **VSAM:** CARDFILE (READ), CUSTFILE (READ), ACCTFILE (READ) | COCOM01Y, CVCRD01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY, DFHBMSCA, DFHAID |
| 20 | COCRDUPC.cbl | COCRDUPC | Credit card update — modify card details with validation | Online (CICS) | **VSAM:** CARDFILE (READ/REWRITE), CUSTFILE (READ), ACCTFILE (READ) | COCOM01Y, CVCRD01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY, DFHBMSCA, DFHAID |
| 21 | COMEN01C.cbl | COMEN01C | Main menu hub — routes regular users to 11 functional screens via XCTL | Online (CICS) | — (menu routing only) | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 22 | CORPT00C.cbl | CORPT00C | Report request — accepts date range, submits batch JCL (INTRDRJ1/J2) for report generation | Online (CICS) | **Calls:** CSUTLDTC (date validation); **Submits:** JCL batch | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | COSGN00C | Sign-on screen — authenticates users and routes to admin or main menu | Online (CICS) | **VSAM:** USRSEC (READ) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | COTRN00C.cbl | COTRN00C | Transaction list — paginated browse of transaction records | Online (CICS) | **VSAM:** TRANSACT (STARTBR, READNEXT, READPREV, ENDBR) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN01C.cbl | COTRN01C | Transaction detail view — display full transaction record | Online (CICS) | **VSAM:** TRANSACT (READ) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | COTRN02C | Transaction add — create new transaction with validation | Online (CICS) | **VSAM:** TRANSACT (WRITE), ACCTFILE (READ/REWRITE), CARDXREF (READ) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | COUSR00C | User list — paginated browse of security users | Online (CICS) | **VSAM:** USRSEC (STARTBR, READNEXT, ENDBR) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | COUSR01C | User add — create new security user record | Online (CICS) | **VSAM:** USRSEC (WRITE) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR02C.cbl | COUSR02C | User update — modify existing security user record | Online (CICS) | **VSAM:** USRSEC (READ/REWRITE) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | COUSR03C | User delete — remove security user record | Online (CICS) | **VSAM:** USRSEC (READ/DELETE) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | CSUTLDTC | Date utility — converts and validates dates using LE CEEDAYS service | Batch (utility) | — (called as sub-program) | — |

---

## 2. Sub-Application Programs

### 2.1 Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|-------------------|---------------------|
| 32 | CBPAUP0C.cbl | CBPAUP0C | Delete expired pending authorization messages from IMS database | Batch (IMS) | **IMS DL/I:** GN, GNP, DLET on DBPAUTP0 | IMSFUNCS, CIPAUDTY, CIPAUSMY, PAUTBPCB |
| 33 | COPAUA0C.cbl | COPAUA0C | Card authorization decision — reads MQ request, checks IMS + DB2, sends MQ response | Online (CICS/IMS/MQ/DB2) | **MQ:** MQOPEN, MQGET, MQPUT1; **IMS:** GU, SCHD, TERM; **DB2:** INSERT AUTHFRDS | CCPAURQY, CCPAURLY, CCPAUERY, CIPAUDTY, CIPAUSMY, IMSFUNCS |
| 34 | COPAUS0C.cbl | COPAUS0C | Authorization summary browse — paginated view of pending auths from IMS | Online (CICS/IMS) | **IMS DL/I:** GU, GNP on summary segment | COCOM01Y, CIPAUSMY, CIPAUDTY, IMSFUNCS, PAUTBPCB |
| 35 | COPAUS1C.cbl | COPAUS1C | Authorization detail view with update capability | Online (CICS/IMS) | **IMS DL/I:** GU, GNP, REPL on detail segment | COCOM01Y, CIPAUDTY, CIPAUSMY, IMSFUNCS |
| 36 | COPAUS2C.cbl | COPAUS2C | Mark authorization message as fraud — updates IMS + inserts DB2 fraud record | Online (CICS/IMS/DB2) | **IMS:** REPL; **DB2:** INSERT AUTHFRDS | CIPAUDTY, CCPAUERY |
| 37 | DBUNLDGS.CBL | DBUNLDGS | GSAM (Generalized Sequential Access Method) database unload utility | Batch (IMS) | **IMS DL/I:** GN, GNP, ISRT (GSAM) | PAUTBPCB, CIPAUDTY, CIPAUSMY |
| 38 | PAUDBLOD.CBL | PAUDBLOD | Load pending authorization IMS database from sequential input files | Batch (IMS) | **Read:** INFILE1, INFILE2; **IMS DL/I:** ISRT, GU | CIPAUSMY, CIPAUDTY, PAUTBPCB, PADFLPCB, PASFLPCB |
| 39 | PAUDBUNL.CBL | PAUDBUNL | Unload pending authorization IMS database to sequential output files | Batch (IMS) | **IMS DL/I:** GN, GNP; **Write:** OUTFIL1, OUTFIL2 | CIPAUSMY, CIPAUDTY, PAUTBPCB, PADFLPCB, PASFLPCB |

### 2.2 Transaction Type (DB2) — `app/app-transaction-type-db2/cbl/`

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|-------------------|---------------------|
| 40 | COBTUPDT.cbl | COBTUPDT | Batch update of transaction types from sequential file into DB2 | Batch (DB2) | **Read:** INPFILE; **DB2:** INSERT/UPDATE TRANSACTION_TYPE | CSDB2RPY, CSDB2RWY |
| 41 | COTRTLIC.cbl | COTRTLIC | Transaction type list — cursor-based paginated browse from DB2 | Online (CICS/DB2) | **DB2:** DECLARE CURSOR, FETCH, SELECT | COCOM01Y, CSDB2RPY, CSDB2RWY, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA |
| 42 | COTRTUPC.cbl | COTRTUPC | Transaction type update/delete — CRUD operations with cascading deletes | Online (CICS/DB2) | **DB2:** SELECT, UPDATE, DELETE (cascading) | COCOM01Y, CSDB2RPY, CSDB2RWY, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA |

### 2.3 VSAM/MQ — `app/app-vsam-mq/cbl/`

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | COACCT01 | Account inquiry via MQ — receives MQ request, reads VSAM, returns MQ response | Online (CICS/MQ) | **MQ:** MQGET, MQPUT; **VSAM:** ACCTFILE (READ) | COCOM01Y |
| 44 | CODATE01.cbl | CODATE01 | Date inquiry service via MQ — provides formatted date responses | Online (CICS/MQ) | **MQ:** MQGET, MQPUT | COCOM01Y |

---

## 3. JCL Job Catalog (`app/jcl/`)

### 3.1 Batch Processing Jobs

| Job | Steps | Programs Executed | Purpose |
|-----|-------|-------------------|---------|
| POSTTRAN.jcl | STEP15 | CBTRN02C | Post daily transactions to master file |
| INTCALC.jcl | STEP15 | CBACT04C | Calculate interest on account balances |
| CREASTMT.JCL | DELDEF01→STEP010→STEP020→STEP030→STEP040 | IDCAMS, SORT, IDCAMS, IEFBR14, CBSTM03A | Generate customer statements (sort transactions first) |
| TRANREPT.jcl | STEP05R→STEP10R | SORT (via REPROC), CBTRN03C | Generate daily transaction report |
| CBEXPORT.jcl | STEP01→STEP02 | IDCAMS, CBEXPORT | Export data for branch migration |
| CBIMPORT.jcl | STEP01 | CBIMPORT | Import data with validation |
| READACCT.jcl | PREDEL→STEP05 | IEFBR14, CBACT01C | Read accounts into output formats |
| READCARD.jcl | STEP05 | CBACT02C | Read and print card data |
| READCUST.jcl | STEP05 | CBCUS01C | Read and print customer data |
| READXREF.jcl | STEP05 | CBACT03C | Read and print cross-reference data |
| WAITSTEP.jcl | WAIT | COBSWAIT | Pause/wait utility step |
| COMBTRAN.jcl | STEP05R→STEP10 | SORT, IDCAMS | Combine and sort transaction files |
| PRTCATBL.jcl | DELDEF→STEP05R→STEP10R | IEFBR14, REPROC, SORT | Print category balance report |

### 3.2 VSAM Cluster Definition Jobs (IDCAMS)

| Job | Purpose | Datasets Defined |
|-----|---------|-----------------|
| ACCTFILE.jcl | Define account KSDS cluster | AWS.M2.CARDDEMO.ACCTFILE |
| CARDFILE.jcl | Define card KSDS cluster | AWS.M2.CARDDEMO.CARDFILE |
| CUSTFILE.jcl | Define customer KSDS cluster | AWS.M2.CARDDEMO.CUSTFILE |
| XREFFILE.jcl | Define card-account cross-reference KSDS | AWS.M2.CARDDEMO.CARDXREF |
| TRANFILE.jcl | Define transaction KSDS cluster + alternates | AWS.M2.CARDDEMO.TRANSACT |
| TRANIDX.jcl | Define transaction alternate indexes | AWS.M2.CARDDEMO.TRANSACT.AIX |
| TCATBALF.jcl | Define transaction category balance KSDS | AWS.M2.CARDDEMO.TCATBALF |
| TRANTYPE.jcl | Define transaction type KSDS | AWS.M2.CARDDEMO.TRANTYPE |
| TRANCATG.jcl | Define transaction category KSDS | AWS.M2.CARDDEMO.TRANCATG |
| DISCGRP.jcl | Define disclosure/interest rate group KSDS | AWS.M2.CARDDEMO.DISCGRP |
| REPTFILE.jcl | Define report output sequential file | AWS.M2.CARDDEMO.REPTFILE |
| DALYREJS.jcl | Define daily rejects sequential file | AWS.M2.CARDDEMO.DALYREJS |

### 3.3 Data Management Jobs

| Job | Purpose |
|-----|---------|
| DEFCUST.jcl | Define customer-related datasets |
| DEFGDGB.jcl | Define GDG (Generation Data Group) base |
| DEFGDGD.jcl | Backup transaction type/category/disclosure data to GDG generations |
| TRANBKP.jcl | Backup transaction file via REPRO |
| DUSRSECJ.jcl | Create and populate user security VSAM from sequential input |
| ESDSRRDS.jcl | Define ESDS and RRDS clusters for user security |
| OPENFIL.jcl | Open CICS files (SDSF) |
| CLOSEFIL.jcl | Close CICS files (SDSF) |
| FTPJCL.JCL | FTP data transfer job |
| INTRDRJ1.JCL | Internal reader job 1 — backup + submit INTRDRJ2 |
| INTRDRJ2.JCL | Internal reader job 2 — backup continuation |
| TXT2PDF1.JCL | Convert text report to PDF |
| CBADMCDJ.jcl | CICS CSD definition update |

### 3.4 Sub-Application JCL

#### Authorization IMS (`app/app-authorization-ims-db2-mq/jcl/`)

| Job | Steps | Purpose |
|-----|-------|---------|
| CBPAUP0J.jcl | STEP01 (DFSRRC00→CBPAUP0C) | Purge expired authorizations from IMS DB |
| DBPAUTP0.jcl | STEPDEL→UNLOAD (DFSRRC00) | Delete and reload IMS pending auth DB |
| LOADPADB.JCL | STEP01 (DFSRRC00→PAUDBLOD) | Load IMS pending authorization database |
| UNLDGSAM.JCL | STEP01 (DFSRRC00→DBUNLDGS) | Unload IMS DB via GSAM |
| UNLDPADB.JCL | STEP0→STEP01 (DFSRRC00→PAUDBUNL) | Unload pending authorization IMS DB |

#### Transaction Type DB2 (`app/app-transaction-type-db2/jcl/`)

| Job | Steps | Purpose |
|-----|-------|---------|
| CREADB21.jcl | FREEPLN→CRCRDDB→LDTTYPE→RUNTEP2→LDTCCAT | Create DB2 tables, load transaction types and categories |
| MNTTRDB2.jcl | STEP1 (IKJEFT01) | Maintain/update DB2 transaction type tables |
| TRANEXTR.jcl | STEP10→STEP20→STEP30→STEP40→STEP50 | Extract transaction data from VSAM to DB2 |

---

## 4. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL Programs | 44 |
| Main Programs (`app/cbl/`) | 31 |
| Sub-App Programs | 13 |
| Online (CICS) Programs | 21 (48%) |
| Batch Programs | 16 (36%) |
| IMS Programs | 7 (16%) |
| DB2 Programs | 4 (9%) |
| MQ Programs | 4 (9%) |
| Total Lines of COBOL | ~27,350 |
| JCL Jobs (main) | 38 |
| JCL Jobs (sub-apps) | 8 |
| Total JCL Jobs | 46 |
