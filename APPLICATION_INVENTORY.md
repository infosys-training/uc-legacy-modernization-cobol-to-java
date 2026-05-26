# CardDemo Application Inventory

This document catalogs every program, copybook, JCL job, and procedure in the CardDemo credit card management application.

---

## Batch Programs (`app/cbl/`)

| Filename | Program ID | Purpose | Classification | Key I/O | Copybooks |
|---|---|---|---|---|---|
| CBACT01C.cbl | CBACT01C | Read account VSAM KSDS file and write to flat files (sequential, array, variable-length) | Batch | R: ACCTFILE (KSDS); W: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| CBACT02C.cbl | CBACT02C | Read and print card data file | Batch | R: CARDFILE (KSDS) | CVACT02Y |
| CBACT03C.cbl | CBACT03C | Read and print account cross-reference file | Batch | R: XREFFILE (KSDS) | CVACT03Y |
| CBACT04C.cbl | CBACT04C | Interest calculator — compute monthly interest on category balances, update accounts, write interest transactions | Batch | R: TCATBALF (KSDS), XREFFILE (KSDS), DISCGRP (KSDS); I-O: ACCTFILE; W: TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| CBCUS01C.cbl | CBCUS01C | Read and print customer data file | Batch | R: CUSTFILE (KSDS) | CVCUS01Y |
| CBEXPORT.cbl | CBEXPORT | Export customer data for branch migration — reads all CardDemo files and creates multi-record export file | Batch | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; W: EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export file, split into normalized files with validation | Batch | R: EXPFILE; W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBSTM03A.CBL | CBSTM03A | Print account statements from transaction data in plain text and HTML formats. Uses ALTER/GO TO, PSA/TCB/TIOT control blocks, 2D arrays, calls CBSTM03B subroutine | Batch | R: TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE (via CBSTM03B); W: STMTFILE, HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| CBSTM03B.CBL | CBSTM03B | Subroutine for CBSTM03A — handles file I/O operations (open/close/read/write/rewrite) for TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | Batch (sub) | Delegated VSAM I/O for 4 files | (inline FD definitions) |
| CBTRN01C.cbl | CBTRN01C | Post daily transaction file records — validate card via XREF, lookup account | Batch | R: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| CBTRN02C.cbl | CBTRN02C | Post daily transactions with validation (overlimit, expiration checks), create/update TCATBAL, update account balances, write rejects | Batch | R: DALYTRAN; I-O: ACCTFILE, TCATBALF; W: TRANFILE, DALYREJS; R: XREFFILE | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| CBTRN03C.cbl | CBTRN03C | Print transaction detail report with page/account/grand totals, date range filtering | Batch | R: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; W: TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| COBSWAIT.cbl | COBSWAIT | Utility program — wait for specified centiseconds (calls MVSWAIT) | Batch (utility) | R: SYSIN | (none) |

---

## Online (CICS) Programs (`app/cbl/`)

| Filename | Program ID | CICS Trans ID | Purpose | Key VSAM Files | Copybooks |
|---|---|---|---|---|---|
| COSGN00C.cbl | COSGN00C | CC00 | Signon screen — authenticate user, route to admin or regular menu | USRSEC | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COADM01C.cbl | COADM01C | CA00 | Admin menu — display admin options, route to sub-programs | USRSEC | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COMEN01C.cbl | COMEN01C | CM00 | Main menu for regular users — display options, route to sub-programs | (none directly) | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COACTVWC.cbl | COACTVWC | CAVW | Account view — display account and customer details | ACCTDAT, CUSTDAT, CXACAIX (xref alt index) | COCOM01Y, COACTVW, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVCRD01Y |
| COACTUPC.cbl | COACTUPC | CAUP | Account update — edit and update account/customer fields with extensive validation (SSN, phone, dates, FICO) | ACCTDAT, CUSTDAT, CXACAIX, CARDAIX | COCOM01Y, COACTUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, CVCRD01Y, CSLKPCDY, CSUTLDWY, CSSTRPFY |
| COBIL00C.cbl | COBIL00C | CB00 | Bill payment — pay account balance in full, create payment transaction | TRANSACT, ACCTDAT, CXACAIX | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y |
| COCRDLIC.cbl | COCRDLIC | CCLI | List credit cards with pagination, select for view/update | CARDDAT, CARDAIX | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CSSTRPFY |
| COCRDSLC.cbl | COCRDSLC | CCDL | Credit card detail view | CARDDAT, CARDAIX | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CVCRD01Y, CSSTRPFY |
| COCRDUPC.cbl | COCRDUPC | CCUP | Credit card update — edit name, status, expiry | CARDDAT, CARDAIX | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CVCRD01Y, CSSTRPFY |
| COTRN00C.cbl | COTRN00C | CT00 | List transactions with pagination | TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| COTRN01C.cbl | COTRN01C | CT01 | View a single transaction | TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| COTRN02C.cbl | COTRN02C | CT02 | Add a new transaction with field validation; calls CSUTLDTC for date validation | TRANSACT, ACCTDAT, CCXREF, CXACAIX | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y |
| CORPT00C.cbl | CORPT00C | CR00 | Print transaction reports — submit batch JCL via extra-partition TDQ (JOBS); calls CSUTLDTC for date validation | TRANSACT (browse only for dates) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| COUSR00C.cbl | COUSR00C | CU00 | List all users from USRSEC file with pagination | USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COUSR01C.cbl | COUSR01C | CU01 | Add new user to USRSEC file | USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COUSR02C.cbl | COUSR02C | CU02 | Update existing user in USRSEC file | USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COUSR03C.cbl | COUSR03C | CU03 | Delete user from USRSEC file | USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |

---

## Utility / Shared Programs (`app/cbl/`)

| Filename | Program ID | Purpose |
|---|---|---|
| CSUTLDTC.cbl | CSUTLDTC | Date validation utility — calls CEEDAYS LE API, returns severity and message. Called by COTRN02C and CORPT00C |

---

## Sub-Application Programs

### IMS/DB2/MQ Authorization Sub-App (`app/app-authorization-ims-db2-mq/cbl/`)

Credit card authorization processing with IMS hierarchical DB, DB2 fraud analytics, and MQ messaging.

| Filename | Program ID | Type | Purpose |
|---|---|---|---|
| COPAUA0C.cbl | COPAUA0C | CICS/IMS/MQ | Card authorization decision — processes MQ authorization requests, applies business rules, stores in IMS DB |
| COPAUS0C.cbl | COPAUS0C | CICS/IMS/BMS | Summary view of authorization messages for an account |
| COPAUS1C.cbl | COPAUS1C | CICS/IMS/BMS | Detail view of a single authorization message |
| COPAUS2C.cbl | COPAUS2C | CICS/IMS/DB2 | Mark authorization message as fraud — writes to DB2 AUTHFRDS table |
| CBPAUP0C.cbl | CBPAUP0C | Batch/IMS | Batch purge of expired authorizations from IMS DB |
| DBUNLDGS.CBL | DBUNLDGS | Batch/IMS | Unload IMS database segments to sequential file |
| PAUDBLOD.CBL | PAUDBLOD | Batch/IMS | Load authorization data from sequential file into IMS DB |
| PAUDBUNL.CBL | PAUDBUNL | Batch/IMS | Unload authorization data from IMS DB to sequential file |

### Transaction Type DB2 Sub-App (`app/app-transaction-type-db2/cbl/`)

Transaction type reference data management using embedded static SQL against DB2.

| Filename | Program ID | Type | Purpose |
|---|---|---|---|
| COTRTLIC.cbl | COTRTLIC | CICS/DB2 | List transaction types with forward/backward DB2 cursor paging, update and delete |
| COTRTUPC.cbl | COTRTUPC | CICS/DB2 | Add/edit transaction type using static embedded SQL |
| COBTUPDT.cbl | COBTUPDT | Batch/DB2 | Batch maintenance program for transaction type updates |

### VSAM/MQ Sub-App (`app/app-vsam-mq/cbl/`)

Account data extraction and system date inquiry via MQ request/response patterns.

| Filename | Program ID | Type | Purpose |
|---|---|---|---|
| COACCT01.cbl | COACCT01 | CICS/MQ | Inquire account details via MQ — reads VSAM account data, sends response via MQ |
| CODATE01.cbl | CODATE01 | CICS/MQ | Inquire system date via MQ — request/response pattern for system date retrieval |

---

## JCL Jobs (`app/jcl/`)

### VSAM File Definition and Load Jobs

| Filename | Steps | Purpose |
|---|---|---|
| ACCTFILE.jcl | IDCAMS ×3 | Define, delete/redefine, and load ACCTFILE VSAM KSDS (account master) |
| CARDFILE.jcl | SDSF, IDCAMS ×4 | Close CICS file, delete/redefine CARDFILE VSAM KSDS, load card data, build AIX |
| CUSTFILE.jcl | SDSF, IDCAMS ×3, SDSF | Close CICS file, define/load CUSTFILE VSAM KSDS (customer master), reopen |
| XREFFILE.jcl | IDCAMS ×5 | Define, load XREFFILE VSAM KSDS, build alternate index (account-ID path) |
| TRANFILE.jcl | SDSF, IDCAMS ×4 | Close CICS, define/load TRANSACT VSAM KSDS, build AIX for card-num path |
| TCATBALF.jcl | IDCAMS ×3 | Define, delete/redefine, load TCATBALF VSAM KSDS (transaction category balances) |
| DISCGRP.jcl | IDCAMS ×3 | Define, delete/redefine, load DISCGRP VSAM KSDS (disclosure/interest rate groups) |
| TRANTYPE.jcl | IDCAMS ×3 | Define, delete/redefine, load TRANTYPE VSAM KSDS (transaction type lookup) |
| TRANCATG.jcl | IDCAMS ×3 | Define, delete/redefine, load TRANCATG VSAM KSDS (transaction category lookup) |
| DALYREJS.jcl | IDCAMS | Define DALYREJS VSAM ESDS (daily rejects file) |
| DEFCUST.jcl | IDCAMS ×2 | Define customer VSAM cluster (alternate definition) |
| REPTFILE.jcl | IDCAMS | Define report output VSAM file |

### Batch Processing Jobs

| Filename | Steps | Purpose |
|---|---|---|
| POSTTRAN.jcl | CBTRN02C | Post daily transactions — validate and post, create TCATBAL entries, write rejects |
| INTCALC.jcl | CBACT04C (with PARM) | Calculate monthly interest on category balances |
| TRANREPT.jcl | REPROC, SORT, CBTRN03C | Unload transactions, filter/sort by date, produce formatted transaction report |
| CREASTMT.JCL | IDCAMS, SORT, IDCAMS, IEFBR14, CBSTM03A | Sort transactions, create temp VSAM, generate plain text and HTML statements |
| CBEXPORT.jcl | IDCAMS, CBEXPORT | Define export VSAM file, run export program |
| CBIMPORT.jcl | CBIMPORT | Import data from export file into normalized files |
| COMBTRAN.jcl | SORT, IDCAMS | Combine and sort transaction files, reload into VSAM |

### Read/Print Utility Jobs

| Filename | Steps | Purpose |
|---|---|---|
| READACCT.jcl | IEFBR14, CBACT01C | Delete prior output, read and print account file |
| READCARD.jcl | CBACT02C | Read and print card data file |
| READCUST.jcl | CBCUS01C | Read and print customer data file |
| READXREF.jcl | CBACT03C | Read and print cross-reference file |

### CICS File Management Jobs

| Filename | Steps | Purpose |
|---|---|---|
| CLOSEFIL.jcl | SDSF | Close CICS files for batch processing |
| OPENFIL.jcl | SDSF | Open CICS files after batch processing |
| CBADMCDJ.jcl | DFHCSDUP | Define CICS CSD resources for CardDemo programs and transactions |

### Index and Backup Jobs

| Filename | Steps | Purpose |
|---|---|---|
| TRANIDX.jcl | IDCAMS ×3 | Define, build, and define path for transaction alternate index |
| TRANBKP.jcl | REPROC, IDCAMS ×2 | Backup transaction file using REPRO, delete/redefine VSAM cluster |
| PRTCATBL.jcl | IEFBR14, REPROC, SORT | Print transaction category balances — unload VSAM, sort, print |

### GDG and Miscellaneous Jobs

| Filename | Steps | Purpose |
|---|---|---|
| DEFGDGB.jcl | IDCAMS | Define GDG (Generation Data Group) base for versioned file management |
| DEFGDGD.jcl | IDCAMS ×3, IEBGENER ×2 | Define GDG data entries, generate initial GDG members for transaction backup and daily files |
| DUSRSECJ.jcl | IEFBR14, IEBGENER, IDCAMS ×2 | Create user security VSAM KSDS from sequential input |
| ESDSRRDS.jcl | IEFBR14, IEBGENER, IDCAMS ×3 | Define and load ESDS and RRDS VSAM datasets |
| WAITSTEP.jcl | COBSWAIT | Execute wait utility (used for job scheduling delays) |
| FTPJCL.JCL | FTP | FTP file transfer job for data exchange |
| INTRDRJ1.JCL | IDCAMS, IEBGENER | Internal reader job — submit JCL dynamically (2 steps) |
| INTRDRJ2.JCL | IDCAMS | Internal reader job — submit JCL dynamically (1 step) |
| TXT2PDF1.JCL | IKJEFT1B | Convert text report files to PDF format using TXT2PDF REXX exec |

---

## JCL Procedures (`app/proc/`)

| Filename | Purpose |
|---|---|
| REPROC.prc | REPRO utility procedure — uses IDCAMS to load or unload a VSAM file (parameterized FILEIN/FILEOUT/CNTLLIB) |
| TRANREPT.prc | Multi-step transaction report procedure: (1) REPROC to unload TRANSACT VSAM to GDG, (2) SORT to filter by date range and sort by card number, (3) CBTRN03C to produce formatted report with TRANTYPE/TRANCATG/CARDXREF lookups |
