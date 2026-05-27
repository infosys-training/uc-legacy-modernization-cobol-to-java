# APPLICATION INVENTORY

## Section A: Programs in app/cbl/ (31 Programs)

### Batch Programs (14)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|---------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | Read the account file and write into files | Batch | ACCTFILE (KSDS, read), OUTFILE (seq, write), ARRYFILE (seq, write), VBRCFILE (seq, write) | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | Read and print card data file | Batch | CARDFILE (KSDS, read) | CVACT02Y |
| 3 | CBACT03C.cbl | Read and print account cross-reference data file | Batch | XREFFILE (KSDS, read) | CVACT03Y |
| 4 | CBACT04C.cbl | Interest calculator program | Batch | TCATBALF (KSDS, read), XREFFILE (KSDS, read), ACCTFILE (KSDS, read), DISCGRP (KSDS, read), TRANSACT (seq, write) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | Read and print customer data file | Batch | CUSTFILE (KSDS, read) | CVCUS01Y |
| 6 | CBEXPORT.cbl | Export customer data for branch migration | Batch | CUSTFILE (KSDS, read), ACCTFILE (KSDS, read), XREFFILE (KSDS, read), TRANSACT (KSDS, read), CARDFILE (KSDS, read), EXPFILE (seq, write) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | Import customer data from branch migration export file | Batch | EXPFILE (KSDS, read), CUSTOUT (seq, write), ACCTOUT (seq, write), XREFOUT (seq, write), TRNXOUT (seq, write), CARDOUT (seq, write), ERROUT (seq, write) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | Print account statements in plain text and HTML formats | Batch | STMTFILE (seq, write), HTMLFILE (seq, write). Calls CBSTM03B subroutine for file I/O | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | Subroutine for CBSTM03A — does file I/O for transaction report | Batch (Subroutine) | TRNXFILE (KSDS, read), XREFFILE (KSDS, read), CUSTFILE (KSDS, read), ACCTFILE (KSDS, read) | *(none in source — uses structures from caller)* |
| 10 | CBTRN01C.cbl | Post records from daily transaction file (version 1) | Batch | DALYTRAN (seq, read), CUSTFILE (KSDS, read), XREFFILE (KSDS, read), CARDFILE (KSDS, read), ACCTFILE (KSDS, read) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | Post records from daily transaction file (version 2) | Batch | DALYTRAN (seq, read), TRANFILE (KSDS, read/write), XREFFILE (KSDS, read), DALYREJS (seq, write), ACCTFILE (KSDS, read/write), TCATBALF (KSDS, read/write) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | Print the transaction detail report | Batch | TRANFILE (seq, read), CARDXREF (KSDS, read), TRANTYPE (KSDS, read), TRANCATG (KSDS, read), DATEPARM (seq, read), TRANREPT (seq, write) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | Utility program to wait (parameter in centiseconds) | Batch | No file I/O. Calls MVSWAIT (system utility) | *(none)* |
| 14 | CSUTLDTC.cbl | Date validation utility using CEEDAYS API | Batch (Utility) | No file I/O. Calls CEEDAYS | *(none)* |

### Online / CICS Programs (17)

| # | Filename | Purpose | Classification | Key I/O (EXEC CICS) | Copybooks Referenced |
|---|----------|---------|---------------|---------------------|---------------------|
| 1 | COACTUPC.cbl | Accept and process account update | CICS Online | READ/REWRITE ACCTDAT (via DATASET LIT-ACCTFILENAME), READ CARDXREF (via DATASET LIT-CARDXREFNAME-ACCT-PATH), READ CUSTDAT (via DATASET LIT-CUSTFILENAME) | COCOM01Y, CVCRD01Y, CVACT01Y, CVCUS01Y, CVACT03Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COACTUP, DFHAID, DFHBMSCA, CSSETATY (×38), CSSTRPFY, CSUTLDPY, CSUTLDWY, CSLKPCDY |
| 2 | COACTVWC.cbl | Accept and process account view request | CICS Online | READ CARDXREF (DATASET LIT-CARDXREFNAME-ACCT-PATH), READ ACCTDAT (DATASET LIT-ACCTFILENAME), READ CUSTDAT (DATASET LIT-CUSTFILENAME) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 3 | COADM01C.cbl | Admin menu for admin users | CICS Online | Reads USRSEC via COMMAREA | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COBIL00C.cbl | Bill payment — pay account balance and record transaction | CICS Online | READ/REWRITE ACCTDAT (WS-ACCTDAT-FILE), READ CXACAIX (WS-CXACAIX-FILE), STARTBR/READPREV/WRITE TRANSACT (WS-TRANSACT-FILE) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 5 | COCRDLIC.cbl | List credit cards (all for admin, account-specific for users) | CICS Online | STARTBR/READNEXT/READPREV CARDDAT (DATASET LIT-CARD-FILE), CARDAIX | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 6 | COCRDSLC.cbl | Credit card detail view | CICS Online | READ CARDDAT, READ ACCTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 7 | COCRDUPC.cbl | Credit card update | CICS Online | READ/REWRITE CARDDAT, READ ACCTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | COMEN01C.cbl | Main menu for regular users | CICS Online | Reads USRSEC via COMMAREA | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 9 | CORPT00C.cbl | Print transaction reports by submitting batch job via TDQ | CICS Online | WRITEQ TD (TDQ for batch submission) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COSGN00C.cbl | Sign-on screen for CardDemo application | CICS Online | READ USRSEC (DATASET WS-USRSEC-FILE) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 11 | COTRN00C.cbl | List transactions from TRANSACT file | CICS Online | STARTBR/READNEXT/READPREV TRANSACT (DATASET WS-TRANSACT-FILE), CXACAIX | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 12 | COTRN01C.cbl | View a transaction from TRANSACT file | CICS Online | READ TRANSACT (DATASET WS-TRANSACT-FILE) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | COTRN02C.cbl | Add a new transaction to TRANSACT file | CICS Online | READ CXACAIX (WS-CXACAIX-FILE), READ CCXREF (WS-CCXREF-FILE), STARTBR/READPREV/WRITE TRANSACT (WS-TRANSACT-FILE) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | List all users from USRSEC file | CICS Online | STARTBR/READNEXT/READPREV USRSEC (DATASET WS-USRSEC-FILE) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | Add a new regular/admin user to USRSEC file | CICS Online | WRITE USRSEC (DATASET WS-USRSEC-FILE) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | Update a user in USRSEC file | CICS Online | READ/REWRITE USRSEC (DATASET WS-USRSEC-FILE) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | Delete a user from USRSEC file | CICS Online | READ/DELETE USRSEC (DATASET WS-USRSEC-FILE) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## Section B: Sub-Application Programs

### app-authorization-ims-db2-mq/ (8 Programs)

These programs implement the card authorization sub-system using IMS DB, DB2, and MQ Series.

| # | Filename | Type | Purpose | Technologies | Copybooks |
|---|----------|------|---------|-------------|-----------|
| 1 | COPAUA0C.cbl | CICS COBOL IMS MQ | Card authorization decision program — receives auth requests via MQ, queries IMS DB for card/account data, returns auth decisions via MQ | CICS, IMS DL/I (SCHD/TERM), MQ (MQOPEN/MQGET/MQPUT) | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 2 | COPAUS0C.cbl | CICS COBOL IMS BMS | Summary view of authorization messages — displays pending auth list on BMS screen | CICS, IMS DL/I (GNP), BMS | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 3 | COPAUS1C.cbl | CICS COBOL IMS BMS | Detail view of authorization message — shows single auth record with option to approve/reject | CICS (LINK, SEND, RECEIVE), IMS DL/I (GU/GNP/REPL) | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 4 | COPAUS2C.cbl | CICS COBOL IMS DB2 | Mark authorization message as fraud — updates fraud flag in DB2 | CICS, DB2 (EXEC SQL), IMS | CIPAUDTY |
| 5 | CBPAUP0C.cbl | BATCH COBOL IMS | Delete expired pending authorization messages — IMS batch cleanup | IMS DL/I (GN/GNP/DLET/CHKP) | CIPAUSMY, CIPAUDTY |
| 6 | DBUNLDGS.CBL | Batch | Unload IMS GSAM database to sequential file — database migration utility | IMS DL/I (CBLTDLI GN/GNP/ISRT) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 7 | PAUDBLOD.CBL | Batch | Load authorization data into IMS database — database population utility | IMS DL/I (CBLTDLI ISRT/GU) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | PAUDBUNL.CBL | Batch | Unload authorization data from IMS database — database extraction utility | IMS DL/I (CBLTDLI GN/GNP) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

### app-transaction-type-db2/ (3 Programs)

These programs manage transaction type reference data using DB2.

| # | Filename | Type | Purpose | Technologies | Copybooks |
|---|----------|------|---------|-------------|-----------|
| 1 | COTRTLIC.cbl | CICS COBOL DB2 | List transaction types for updates and deletes — demonstrates Db2 cursor paging | CICS, DB2 (EXEC SQL cursors, INCLUDE DCLTRTYP) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY |
| 2 | COTRTUPC.cbl | CICS COBOL DB2 | Accept and process transaction type update | CICS, DB2 (EXEC SQL INCLUDE DCLTRTYP, DCLTRCAT) | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |
| 3 | COBTUPDT.cbl | Batch DB2 | Update transaction type based on user input — batch DB2 maintenance | DB2 (EXEC SQL, INCLUDE DCLTRTYP) | *(none in source)* |

### app-vsam-mq/ (2 Programs)

These programs implement VSAM-to-MQ integration for account and date services.

| # | Filename | Type | Purpose | Technologies | Copybooks |
|---|----------|------|---------|-------------|-----------|
| 1 | COACCT01.cbl | CICS MQ | Account inquiry via MQ — receives account requests from MQ queue, reads VSAM, returns results via MQ | CICS (RETRIEVE, READ, RETURN), MQ (MQOPEN/MQGET/MQPUT) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 2 | CODATE01.cbl | CICS MQ | Date service via MQ — receives date format requests, processes using CICS ASKTIME/FORMATTIME, returns via MQ | CICS (RETRIEVE, ASKTIME, FORMATTIME), MQ (MQOPEN/MQGET/MQPUT) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## Section C: JCL Jobs in app/jcl/ (38 Jobs)

### VSAM File Definition Jobs

| # | Job Name | File | Purpose | Step Sequence |
|---|----------|------|---------|---------------|
| 1 | ACCTFILE.jcl | ACCTFILE | Define account VSAM KSDS | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO from PS to VSAM) |
| 2 | CARDFILE.jcl | CARDFILE | Define card data VSAM KSDS with AIX | CLCIFIL (SDSF close CICS files), STEP05 (IDCAMS DELETE cluster+AIX), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO), STEP40 (IDCAMS DEFINE AIX), STEP50 (IDCAMS DEFINE PATH), STEP60 (IDCAMS BLDINDEX), OPCIFIL (SDSF open CICS files) |
| 3 | CUSTFILE.jcl | CUSTFILE | Define customer VSAM KSDS | CLCIFIL (SDSF close CICS files), STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO), OPCIFIL (SDSF open CICS files) |
| 4 | TRANFILE.jcl | TRANFILE | Define transaction master VSAM KSDS with AIX | CLCIFIL (SDSF close CICS files), STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO), STEP20 (IDCAMS DEFINE AIX), STEP25 (IDCAMS DEFINE PATH), STEP30 (IDCAMS BLDINDEX), OPCIFIL (SDSF open CICS files) |
| 5 | XREFFILE.jcl | XREFFILE | Define card cross-reference VSAM KSDS with AIX | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO), STEP20 (IDCAMS DEFINE AIX), STEP25 (IDCAMS DEFINE PATH), STEP30 (IDCAMS BLDINDEX) |
| 6 | TRANCATG.jcl | TRANCATG | Define transaction category VSAM KSDS | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO) |
| 7 | TRANTYPE.jcl | TRANTYPE | Define transaction type VSAM KSDS | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO) |
| 8 | DISCGRP.jcl | DISCGRP | Define disclosure group VSAM KSDS | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO) |
| 9 | TCATBALF.jcl | TCATBALF | Define transaction category balance VSAM KSDS | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO) |
| 10 | DUSRSECJ.jcl | DUSRSECJ | Define user security VSAM file | PREDEL (IEFBR14 pre-delete), STEP01 (IEBGENER copy data), STEP02 (IDCAMS DEFINE), STEP03 (IDCAMS REPRO) |

### Batch Processing Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 11 | POSTTRAN.jcl | Post daily transactions | STEP15 (PGM=CBTRN02C) |
| 12 | INTCALC.jcl | Interest calculation | STEP15 (PGM=CBACT04C, PARM='2022071800') |
| 13 | TRANREPT.jcl | Transaction detail report | STEP05R (PROC=REPROC — unload VSAM to sequential), STEP05R (PGM=SORT — filter by date), STEP10R (PGM=CBTRN03C) |
| 14 | CREASTMT.JCL | Create account statements | DELDEF01 (IDCAMS), STEP010 (SORT), STEP020 (IDCAMS), STEP030 (IEFBR14), STEP040 (PGM=CBSTM03A) |
| 15 | CBEXPORT.jcl | Export data for branch migration | STEP01 (IDCAMS define export cluster), STEP02 (PGM=CBEXPORT) |
| 16 | CBIMPORT.jcl | Import data from migration | STEP01 (PGM=CBIMPORT) |
| 17 | COMBTRAN.jcl | Combine transactions | STEP05R (PGM=SORT), STEP10 (PGM=IDCAMS REPRO) |
| 18 | PRTCATBL.jcl | Print transaction category balance | DELDEF (IEFBR14), STEP05R (PROC=REPROC — unload), STEP10R (PGM=SORT) |

### File Verification Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 19 | READACCT.jcl | Read and verify account file | PREDEL (IEFBR14), STEP05 (PGM=CBACT01C) |
| 20 | READCARD.jcl | Read and verify card file | STEP05 (PGM=CBACT02C) |
| 21 | READCUST.jcl | Read and verify customer file | STEP05 (PGM=CBCUS01C) |
| 22 | READXREF.jcl | Read and verify xref file | STEP05 (PGM=CBACT03C) |

### CICS File Management Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 23 | CLOSEFIL.jcl | Close CICS files | CLCIFIL (PGM=SDSF — sends CEMT SET FIL CLO commands) |
| 24 | OPENFIL.jcl | Open CICS files | OPCIFIL (PGM=SDSF — sends CEMT SET FIL OPE commands) |

### Infrastructure / Utility Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 25 | ESDSRRDS.jcl | Define ESDS/RRDS datasets | PREDEL (IEFBR14), STEP01 (IEBGENER), STEP02–STEP05 (IDCAMS DEFINE) |
| 26 | DEFGDGB.jcl | Define GDG base | STEP05 (IDCAMS DEFINE GDG) |
| 27 | DEFGDGD.jcl | Define GDG datasets for DB2 | STEP10 (IDCAMS), STEP20 (IEBGENER), STEP30 (IDCAMS), STEP40 (IEBGENER), STEP50 (IDCAMS), STEP60 (IEBGENER) |
| 28 | DEFCUST.jcl | Define customer-related resources | STEP05 (IDCAMS DELETE), STEP05 (IDCAMS DEFINE) |
| 29 | DALYREJS.jcl | Define daily rejects GDG | STEP05 (IDCAMS DEFINE GDG) |
| 30 | REPTFILE.jcl | Define report file GDG | STEP05 (IDCAMS DEFINE GDG) |
| 31 | CBADMCDJ.jcl | Admin card processing — CICS resource definition | STEP1 (PGM=DFHCSDUP — CICS system definition) |
| 32 | TRANBKP.jcl | Transaction backup — REPRO and delete transaction master | STEP05R (PROC=REPROC — unload to sequential), STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE, COND) |
| 33 | TRANIDX.jcl | Transaction index operations — define AIX on transaction master | STEP20 (IDCAMS DEFINE AIX), STEP25 (IDCAMS DEFINE PATH), STEP30 (IDCAMS BLDINDEX) |
| 34 | WAITSTEP.jcl | Wait step utility | WAIT (PGM=COBSWAIT) |
| 35 | TXT2PDF1.JCL | Convert text to PDF | *(uses IEBGENER/utility — single-step)* |
| 36 | FTPJCL.JCL | FTP operations | STEP1 (PGM=FTP) |
| 37 | INTRDRJ1.JCL | Internal reader job 1 — submit JCL via internal reader | IDCAMS (IDCAMS), STEP01 (IEBGENER — write to internal reader) |
| 38 | INTRDRJ2.JCL | Internal reader job 2 — submit JCL via internal reader | IDCAMS (IDCAMS) |
