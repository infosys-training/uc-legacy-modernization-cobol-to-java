# Application Inventory — CardDemo COBOL Estate

## Overview

The CardDemo application is a multi-tier mainframe credit card management system comprising **44 COBOL programs** across 4 sub-directories, **41 JCL jobs**, and supporting copybooks, BMS maps, and data files. The system manages credit card accounts, customers, transactions, authorization, and reporting.

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

### Batch Programs

| Filename | Program ID | Purpose | Key I/O Operations | Copybooks Referenced |
|----------|-----------|---------|-------------------|---------------------|
| CBACT01C.cbl | CBACT01C | Read account VSAM file and write to multiple output formats (sequential, array, variable-length) | READ ACCTFILE (KSDS), WRITE OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| CBACT02C.cbl | CBACT02C | Read and print card data file | READ CARDFILE (KSDS) | CVACT02Y |
| CBACT03C.cbl | CBACT03C | Read and print account cross-reference data file | READ XREFFILE (KSDS) | CVACT03Y |
| CBACT04C.cbl | CBACT04C | Interest calculator — compute interest on accounts based on transaction categories and discount groups | READ TCATBALF, XREFFILE, ACCTFILE, DISCGRP; WRITE TRANSACT; REWRITE ACCTFILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| CBCUS01C.cbl | CBCUS01C | Read and print customer data file | READ CUSTFILE (KSDS) | CVCUS01Y |
| CBEXPORT.cbl | CBEXPORT | Export customer data for branch migration — reads all entity files and creates a multi-record export file | READ CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; WRITE EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export — parses multi-record file and writes to individual entity files | READ EXPFILE; WRITE CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBSTM03A.CBL | CBSTM03A | Print account statements from transaction data — generates text and HTML statement files | WRITE STMTFILE, HTMLFILE; CALL CBSTM03B for file reads | COSTM01, CUSTREC, CVACT01Y, CVACT03Y |
| CBSTM03B.CBL | CBSTM03B | Subroutine for CBSTM03A — performs file I/O (open/read/close) for transactions, cross-refs, customers, and accounts | READ/OPEN/CLOSE TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | *(none — uses linkage section)* |
| CBTRN01C.cbl | CBTRN01C | Post records from daily transaction file — validate and lookup against master files | READ DALYTRAN, XREFFILE, ACCTFILE; OPEN CUSTFILE, CARDFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| CBTRN02C.cbl | CBTRN02C | Post daily transactions — update account balances, write to transaction master, generate rejects | READ DALYTRAN, XREFFILE, ACCTFILE, TCATBALF; WRITE TRANFILE, DALYREJS; REWRITE ACCTFILE, TCATBALF | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| CBTRN03C.cbl | CBTRN03C | Print transaction detail report with type/category descriptions | READ TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; WRITE TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| COBSWAIT.cbl | COBSWAIT | Utility — wait for specified centiseconds (PARM-driven delay) | CALL MVSWAIT | *(none)* |
| CSUTLDTC.cbl | CSUTLDTC | Date conversion utility — calls CEEDAYS for date formatting | CALL CEEDAYS | *(none)* |

### Online (CICS) Programs

| Filename | Program ID | Purpose | Key CICS Operations | Copybooks Referenced |
|----------|-----------|---------|-------------------|---------------------|
| COACTUPC.cbl | COACTUPC | Account Update — accept and process account field changes with validation | READ, REWRITE (Account, Xref, Customer files); SEND/RECEIVE MAP | COACTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSSTRPFY, CSUSR01Y, CSUTLDPY, CSUTLDWY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| COACTVWC.cbl | COACTVWC | Account View — display account details with card/customer cross-reference | READ (Account, Card, Customer, Xref files); SEND/RECEIVE MAP | COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| COADM01C.cbl | COADM01C | Admin Menu — display administrative menu options for admin users | SEND/RECEIVE MAP; XCTL to sub-programs | COADM01, COADM02Y, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COBIL00C.cbl | COBIL00C | Bill Payment — pay account balance in full or partial; create payment transaction | READ, REWRITE (Account); WRITE (Transaction); STARTBR/READPREV/ENDBR | COBIL00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COCRDLIC.cbl | COCRDLIC | Credit Card List — browse and paginate credit card records | STARTBR, READNEXT, READPREV, ENDBR (Card file); SEND/RECEIVE MAP | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| COCRDSLC.cbl | COCRDSLC | Credit Card Detail — view credit card details with account/customer info | READ (Card, Account, Customer files); SEND/RECEIVE MAP | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| COCRDUPC.cbl | COCRDUPC | Credit Card Update — modify credit card details with validation | READ, REWRITE (Card file); SEND/RECEIVE MAP | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| COMEN01C.cbl | COMEN01C | Main Menu — display main menu for regular users, route to sub-programs | SEND/RECEIVE MAP; XCTL to selected programs | COCOM01Y, COMEN01, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| CORPT00C.cbl | CORPT00C | Report Submission — submit batch transaction reports via TDQ | WRITEQ TD (JOBS); CALL CSUTLDTC; SEND/RECEIVE MAP | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COSGN00C.cbl | COSGN00C | Sign-on — authenticate users and route to appropriate menu | READ (USRSEC file); SEND/RECEIVE MAP; XCTL to menu | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COTRN00C.cbl | COTRN00C | Transaction List — browse and paginate transaction records | STARTBR, READNEXT, READPREV, ENDBR (Transaction file); SEND/RECEIVE MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COTRN01C.cbl | COTRN01C | Transaction View — display single transaction details | READ (Transaction file); SEND/RECEIVE MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COTRN02C.cbl | COTRN02C | Transaction Add — create a new transaction with validation | READ (Account, Xref files); WRITE (Transaction file); SEND/RECEIVE MAP | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| COUSR00C.cbl | COUSR00C | User List — browse and paginate user security records | STARTBR, READNEXT, READPREV (USRSEC file); SEND/RECEIVE MAP | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR01C.cbl | COUSR01C | User Add — create a new regular or admin user in USRSEC file | WRITE (USRSEC file); SEND/RECEIVE MAP | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR02C.cbl | COUSR02C | User Update — modify existing user record in USRSEC file | READ, REWRITE (USRSEC file); SEND/RECEIVE MAP | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR03C.cbl | COUSR03C | User Delete — remove user record from USRSEC file | READ, DELETE (USRSEC file); SEND/RECEIVE MAP | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### Authorization Sub-App (`app/app-authorization-ims-db2-mq/cbl/`)

| Filename | Program ID | Classification | Purpose | Key I/O | Copybooks |
|----------|-----------|---------------|---------|---------|-----------|
| CBPAUP0C.cbl | CBPAUP0C | Batch/IMS | Delete expired pending authorization messages from IMS DB | IMS DL/I calls (GN, GNP) | CIPAUSMY, CIPAUDTY |
| COPAUA0C.cbl | COPAUA0C | Online/CICS+MQ | Card authorization decision — read request from MQ, validate against VSAM, send reply | MQ OPEN/GET/PUT/CLOSE; CICS READ (Xref, Account, Customer) | CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CVACT01Y, CVACT03Y, CVCUS01Y |
| COPAUS0C.cbl | COPAUS0C | Online/CICS+BMS | Summary view of authorization messages — display paginated list | CICS READ (Card, Account, Customer); SEND/RECEIVE MAP | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| COPAUS1C.cbl | COPAUS1C | Online/CICS+BMS | Detail view of authorization message | CICS LINK, SEND/RECEIVE MAP | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| COPAUS2C.cbl | COPAUS2C | Online/CICS+DB2 | Mark authorization message as fraud — insert fraud record into DB2 | EXEC SQL INSERT/SELECT (AUTHFRDS table) | CIPAUDTY, DFHBMSCA |
| DBUNLDGS.CBL | DBUNLDGS | Batch/IMS | Unload IMS GSAM segments to sequential files | IMS DL/I calls (GN, GNP, ISRT) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| PAUDBLOD.CBL | PAUDBLOD | Batch/IMS | Load IMS database from sequential input files | READ INFILE1/INFILE2; IMS DL/I calls (ISRT, GU) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| PAUDBUNL.CBL | PAUDBUNL | Batch/IMS | Unload IMS database to sequential output files | WRITE OPFILE1/OPFILE2; IMS DL/I calls (GN, GNP) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

### Transaction Type DB2 Sub-App (`app/app-transaction-type-db2/cbl/`)

| Filename | Program ID | Classification | Purpose | Key I/O | Copybooks |
|----------|-----------|---------------|---------|---------|-----------|
| COBTUPDT.cbl | COBTUPDT | Batch/DB2 | Batch update of transaction types from input file — INSERT, UPDATE, DELETE on TRANSACTION_TYPE table | READ INPFILE; EXEC SQL INSERT/UPDATE/DELETE | *(inline SQL includes DCLTRTYP)* |
| COTRTLIC.cbl | COTRTLIC | Online/CICS+DB2 | List transaction types for updates and deletes — cursor-based DB2 browse | EXEC SQL DECLARE/OPEN/FETCH CURSOR on TRANSACTION_TYPE | COCOM01Y, COTRTLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| COTRTUPC.cbl | COTRTUPC | Online/CICS+DB2 | Transaction type update/add — accept and process changes to TRANSACTION_TYPE table | EXEC SQL SELECT/UPDATE/INSERT on TRANSACTION_TYPE; SEND/RECEIVE MAP | COCOM01Y, COTRTUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSETATY, CSSTRPFY, CSUSR01Y, CSUTLDWY, CVCRD01Y, DFHAID, DFHBMSCA |

### VSAM-MQ Sub-App (`app/app-vsam-mq/cbl/`)

| Filename | Program ID | Classification | Purpose | Key I/O | Copybooks |
|----------|-----------|---------------|---------|---------|-----------|
| COACCT01.cbl | COACCT01 | Online/CICS+MQ | Account inquiry via MQ — receive request, read account VSAM, send response via MQ | MQ OPEN/GET/PUT/CLOSE; CICS READ (Account file) | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| CODATE01.cbl | CODATE01 | Online/CICS+MQ | Date service via MQ — receive request, format date using CICS ASKTIME/FORMATTIME, send response | MQ OPEN/GET/PUT/CLOSE; CICS ASKTIME/FORMATTIME | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. JCL Job Catalog (`app/jcl/`)

### Data Definition & File Management Jobs

| JCL File | Job Name | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| ACCTFILE.jcl | ACCTFILE | Delete/define/load Account VSAM KSDS | STEP05: IDCAMS (delete) → STEP10: IDCAMS (define cluster) → STEP15: IDCAMS (REPRO load) |
| CARDFILE.jcl | CARDFILE | Delete/define/load Card Data VSAM KSDS with AIX | CLCIFIL: SDSF (close CICS files) → STEP05: IDCAMS (delete) → STEP10: IDCAMS (define) → STEP15: IDCAMS (load) → STEP40-60: IDCAMS (AIX) → OPCIFIL: SDSF (open CICS files) |
| CUSTFILE.jcl | CUSTFILE | Define/load Customer VSAM KSDS | CLCIFIL: SDSF → STEP05: IDCAMS (delete) → STEP10: IDCAMS (define) → STEP15: IDCAMS (load) → OPCIFIL: SDSF |
| XREFFILE.jcl | XREFFILE | Delete/define/load Cross-Reference VSAM KSDS with AIX | STEP05-30: IDCAMS (delete, define, load, AIX define, AIX build, AIX path) |
| TRANFILE.jcl | TRANFILE | Define Transaction Master VSAM KSDS | CLCIFIL → STEP05-30: IDCAMS (delete, define, load, AIX define, build, path) → OPCIFIL |
| TCATBALF.jcl | TCATBALF | Define Transaction Category Balance VSAM KSDS | STEP05: IDCAMS (delete) → STEP10: IDCAMS (define) → STEP15: IDCAMS (load) |
| TRANTYPE.jcl | TRANTYPE | Define Transaction Type VSAM KSDS | STEP05: IDCAMS (delete) → STEP10: IDCAMS (define) → STEP15: IDCAMS (load) |
| TRANCATG.jcl | TRANCATG | Define Transaction Category VSAM KSDS | STEP05: IDCAMS (delete) → STEP10: IDCAMS (define) → STEP15: IDCAMS (load) |
| DISCGRP.jcl | DISCGRP | Define Disclosure Group VSAM KSDS | STEP05: IDCAMS (delete) → STEP10: IDCAMS (define) → STEP15: IDCAMS (load) |
| DUSRSECJ.jcl | DUSRSECJ | Define User Security VSAM KSDS | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02: IDCAMS (delete) → STEP03: IDCAMS (define/load) |
| ESDSRRDS.jcl | ESDSRRDS | Define ESDS and RRDS VSAM files | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02-05: IDCAMS (define ESDS, RRDS) |
| DEFCUST.jcl | DEFCUST | Alternate customer file definition | STEP05: IDCAMS (define) |
| DEFGDGB.jcl | DEFGDGB | Define GDG bases for backup files | STEP05: IDCAMS (define GDG bases) |
| DEFGDGD.jcl | DEFGDGD | Define DB2-related GDGs and backup files | STEP10-60: IDCAMS/IEBGENER (define and backup DISCGRP, TRANCATG, TRANTYPE) |
| DALYREJS.jcl | DALYREJS | Define GDG for daily reject file | STEP05: IDCAMS (define GDG) |
| REPTFILE.jcl | REPTFILE | Define GDG for report file | STEP05: IDCAMS (define GDG) |
| TRANIDX.jcl | TRANIDX | Define AIX on Transaction Master | STEP20-30: IDCAMS (define AIX, build AIX, define path) |

### Batch Processing Jobs

| JCL File | Job Name | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| READACCT.jcl | READACCT | Read account data and produce output files | PREDEL: IEFBR14 → STEP05: CBACT01C |
| READCARD.jcl | READCARD | Read and print card data | STEP05: CBACT02C |
| READCUST.jcl | READCUST | Read and print customer data | STEP05: CBCUS01C |
| READXREF.jcl | READXREF | Read and print cross-reference data | STEP05: CBACT03C |
| POSTTRAN.jcl | POSTTRAN | Post daily transactions to master files | STEP15: CBTRN02C |
| INTCALC.jcl | INTCALC | Calculate interest on accounts | STEP15: CBACT04C (PARM='2022071800') |
| TRANREPT.jcl | TRANREPT | Generate transaction detail report | STEP05R: REPROC → STEP05R: SORT → STEP10R: CBTRN03C |
| CREASTMT.JCL | CREASTMT | Create account statements (text + HTML) | DELDEF01: IDCAMS → STEP010: SORT → STEP020: IDCAMS → STEP030: IEFBR14 → STEP040: CBSTM03A |
| COMBTRAN.jcl | COMBTRAN | Combine daily and master transactions | STEP05R: SORT → STEP10: IDCAMS (REPRO combined back) |
| TRANBKP.jcl | TRANBKP | Backup and purge transaction master | STEP05R: REPROC → STEP05: IDCAMS (REPRO backup) → STEP10: IDCAMS (delete/redefine) |
| PRTCATBL.jcl | PRTCATBL | Print transaction category balance file | DELDEF: IEFBR14 → STEP05R: REPROC → STEP10R: SORT |
| CBEXPORT.jcl | CBEXPORT | Export data for branch migration | STEP01: IDCAMS (define export file) → STEP02: CBEXPORT |
| CBIMPORT.jcl | CBIMPORT | Import data from branch migration | STEP01: CBIMPORT |
| WAITSTEP.jcl | WAITSTEP | Wait utility execution | WAIT: COBSWAIT |

### CICS & Utility Jobs

| JCL File | Job Name | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| CLOSEFIL.jcl | CLOSEFIL | Close files in CICS region | CLCIFIL: SDSF (CEMT SET FIL CLO) |
| OPENFIL.jcl | OPENFIL | Open files in CICS region | OPCIFIL: SDSF (CEMT SET FIL OPE) |
| CBADMCDJ.jcl | CBADMCDJ | CSD definition for CICS programs | STEP1: DFHCSDUP |
| FTPJCL.JCL | FTPJCLS | FTP file transfer | STEP1: FTP |
| INTRDRJ1.JCL | INTRDRJ1 | Internal reader job 1 | IDCAMS: IDCAMS → STEP01: IEBGENER |
| INTRDRJ2.JCL | INTRDRJ2 | Internal reader job 2 | IDCAMS: IDCAMS |
| TXT2PDF1.JCL | TXT2PDF1 | Convert text statements to PDF | TXT2PDF: IKJEFT1B |

### Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`)

| JCL File | Job Name | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| CBPAUP0J.jcl | CBPAUP0J | Run auth purge batch (IMS BMP) | STEP01: DFSRRC00 (runs CBPAUP0C) |
| DBPAUTP0.jcl | DBPAUTP0 | Unload IMS PAUTH database | STEPDEL: IEFBR14 → UNLOAD: DFSRRC00 |
| LOADPADB.JCL | LOADPADB | Load IMS PAUTH database from files | STEP01: DFSRRC00 (runs PAUDBLOD) |
| UNLDGSAM.JCL | UNLDGSAM | Unload IMS to GSAM files | STEP01: DFSRRC00 (runs DBUNLDGS) |
| UNLDPADB.JCL | UNLDPADB | Unload IMS PAUTH DB to flat files | STEP0: IEFBR14 → STEP01: DFSRRC00 (runs PAUDBUNL) |

### Sub-Application JCL (`app/app-transaction-type-db2/jcl/`)

| JCL File | Job Name | Purpose | Step Sequence |
|----------|----------|---------|---------------|
| CREADB21.jcl | CREADB2 | Create DB2 tables and load transaction types | FREEPLN: IKJEFT01 → CRCRDDB: IKJEFT01 → LDTTYPE: IEFBR14 → RUNTEP2: IKJEFT01 → LDTCCAT: IKJEFT01 |
| MNTTRDB2.jcl | MNTTRDB2 | Maintain transaction types via DB2 (runs COBTUPDT) | STEP1: IKJEFT01 |
| TRANEXTR.jcl | TRANEXTR | Extract transaction type data for backup | STEP10: IEBGENER |

---

## 4. Key Datasets

| Dataset Name | Type | Description |
|-------------|------|-------------|
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | VSAM KSDS | Account master file (key: 11-byte account ID) |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | VSAM KSDS | Card data file (key: 16-byte card number) |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | VSAM KSDS | Customer master file (key: 9-byte customer ID) |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | VSAM KSDS | Card cross-reference (key: card number → customer + account) |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | VSAM KSDS | Transaction master file |
| AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | VSAM KSDS | Transaction category balance file |
| AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | VSAM KSDS | Transaction type reference file |
| AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | VSAM KSDS | Transaction category reference file |
| AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | VSAM KSDS | Disclosure/discount group file |
| AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | VSAM KSDS | User security file |
| AWS.M2.CARDDEMO.DALYTRAN.PS | Sequential | Daily transaction input file |
| AWS.M2.CARDDEMO.DALYREJS | GDG | Daily rejected transactions |
| AWS.M2.CARDDEMO.TRANSACT.BKUP | GDG | Transaction master backup |
| AWS.M2.CARDDEMO.EXPORT.DATA | Sequential | Branch migration export file |
| AWS.M2.CARDDEMO.STATEMNT.PS | Sequential | Account statement output (text) |
| AWS.M2.CARDDEMO.STATEMNT.HTML | Sequential | Account statement output (HTML) |
| AWS.M2.CARDDEMO.TRANREPT | GDG | Transaction detail report |
| AWS.M2.CARDDEMO.SYSTRAN | Sequential | System-generated transactions |
| OEM.IMS.IMSP.PAUTHDB | IMS DB | Pending authorization IMS database |
| CARDDEMO.TRANSACTION_TYPE | DB2 Table | Transaction type codes and descriptions |
