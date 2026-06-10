# APPLICATION INVENTORY — CardDemo COBOL Estate

## Overview

The CardDemo application is a multi-tier mainframe credit card management system built with COBOL, CICS, VSAM, IMS DB, DB2, and MQ. The estate comprises **44 COBOL programs** across the main application and three sub-applications, **30 copybooks**, **46 JCL jobs**, and supporting BMS maps, assembler routines, and scheduler definitions.

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|---------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | CBACT01C | Reads account VSAM KSDS file, writes to flat PS files in multiple formats (fixed, array, variable-length) | Batch | **Read:** ACCTFILE (VSAM KSDS) · **Write:** OUTFILE (PS fixed), ARRYFILE (PS array), VBRCFILE (PS variable) | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Reads card data VSAM file and displays records | Batch | **Read:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Reads card cross-reference VSAM file and displays records | Batch | **Read:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Interest calculation — reads transaction category balances, cross-refs, disclosure groups; updates account balances and writes new transactions | Batch | **Read:** TCATBALF, XREFFILE, DISCGRP · **Update (I-O):** ACCTFILE · **Write:** TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Reads customer data VSAM file and displays records | Batch | **Read:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Branch migration export — reads all master files and writes unified export sequential file | Batch | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · **Write:** EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Branch migration import — reads export file and splits records into individual entity output files | Batch | **Read:** EXPFILE · **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | CBSTM03A | Statement generation — produces plain-text and HTML credit card statements by reading transactions, xrefs, customers, and accounts | Batch | **Read:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE (via CBSTM03B) · **Write:** STMTFILE (text), HTMLFILE (HTML) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | CBSTM03B | File I/O subprogram for statement generation — handles open/read/close of data files on behalf of CBSTM03A | Batch (module) | **Read:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | *(none)* |
| 10 | CBTRN01C.cbl | CBTRN01C | Daily transaction validation — reads daily transactions and validates against cross-reference, account, customer, card, and transaction master files | Batch | **Read:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | CBTRN02C | Transaction posting — processes daily transactions, updates account balances and transaction category balances, writes to transaction master, rejects invalid transactions | Batch | **Read:** DALYTRAN, XREFFILE · **Update (I-O):** ACCTFILE, TCATBALF · **Write:** TRANFILE, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | CBTRN03C | Transaction report generation — produces daily transaction report with type/category lookups and date-range filtering | Batch | **Read:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM · **Write:** TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COACTUPC.cbl | COACTUPC | Online account update — CICS screen for modifying account details with extensive field-level validation | Online (CICS) | **CICS Read/Rewrite:** ACCTDATA (account), CUSTDATA (customer) via EXEC CICS READ/REWRITE | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY, CSSTRPFY, CSUTLDPY |
| 14 | COACTVWC.cbl | COACTVWC | Online account view — CICS screen for viewing account, card, and customer details (read-only) | Online (CICS) | **CICS Read:** Account, Card Xref, Customer files | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 15 | COADM01C.cbl | COADM01C | Admin menu — CICS screen presenting admin options (user management, transaction type maintenance) | Online (CICS) | **CICS:** Screen send/receive, program transfer (XCTL) | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | COBIL00C | Bill payment — CICS screen for processing bill payments against credit card accounts | Online (CICS) | **CICS Read/Rewrite/Write:** Transaction, Account, Card Xref files | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | COBSWAIT | Wait utility — calls assembler MVSWAIT to introduce a timed delay in batch processing | Batch | **Call:** MVSWAIT (assembler) | *(none)* |
| 18 | COCRDLIC.cbl | COCRDLIC | Credit card list — CICS screen displaying paginated list of credit cards with browse (STARTBR/READNEXT/READPREV) | Online (CICS) | **CICS Browse:** CARDDATA (STARTBR, READNEXT, READPREV, ENDBR) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 19 | COCRDSLC.cbl | COCRDSLC | Credit card view/detail — CICS screen displaying individual card details with linked account and customer info | Online (CICS) | **CICS Read:** Card, Account, Customer files | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 20 | COCRDUPC.cbl | COCRDUPC | Credit card update — CICS screen for modifying card details (status, embossed name, expiration date) | Online (CICS) | **CICS Read/Rewrite:** Card file | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 21 | COMEN01C.cbl | COMEN01C | Main menu — CICS screen presenting user menu options (account, card, transaction, report functions) | Online (CICS) | **CICS:** Screen send/receive, INQUIRE FILE, XCTL | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 22 | CORPT00C.cbl | CORPT00C | Report request — CICS screen for submitting batch transaction report requests with date range selection | Online (CICS) | **CICS:** WRITEQ TD (to transient data queue JOBS), calls CSUTLDTC for date validation | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | COSGN00C | Sign-on — CICS sign-on screen, authenticates user against USRSEC VSAM file | Online (CICS) | **CICS Read:** USRSEC (user security file), XCTL to COMEN01C or COADM01C | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | COTRN00C.cbl | COTRN00C | Transaction list — CICS screen displaying paginated list of transactions with browse | Online (CICS) | **CICS Browse:** Transaction file (STARTBR, READNEXT, READPREV, ENDBR) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN01C.cbl | COTRN01C | Transaction view — CICS screen for viewing individual transaction details | Online (CICS) | **CICS Read:** Transaction file | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | COTRN02C | Transaction add — CICS screen for adding new transactions with validation, calls CSUTLDTC for date checking | Online (CICS) | **CICS Read/Write:** Transaction, Account, Card Xref files; calls CSUTLDTC | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | COUSR00C | User list — CICS screen displaying paginated list of security users with browse | Online (CICS) | **CICS Browse:** USRSEC (STARTBR, READNEXT, READPREV, ENDBR) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | COUSR01C | User add — CICS screen for creating new security users | Online (CICS) | **CICS Write:** USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR02C.cbl | COUSR02C | User update — CICS screen for modifying existing security user records | Online (CICS) | **CICS Read/Rewrite:** USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | COUSR03C | User delete — CICS screen for deleting security user records | Online (CICS) | **CICS Read/Delete:** USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | CSUTLDTC | Date validation utility — validates CCYYMMDD dates using LE callable service CEEDAYS | Batch (utility) | **Call:** CEEDAYS (LE runtime) | *(none)* |

---

## 2. COBOL Programs — Sub-Application: Authorization IMS-DB2-MQ (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|---------------|-------------------|---------------------|
| 32 | CBPAUP0C.cbl | CBPAUP0C | Pending authorization purge — IMS batch program that reads and deletes expired authorization records from IMS DB | Batch (IMS) | **IMS DB:** GN/GNP/DLET on PAUTBPCB | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | COPAUA0C | Authorization processor — CICS program that receives MQ authorization requests, validates against VSAM files (XREF, ACCT, CUST), processes authorizations, and sends MQ reply | Online (CICS + MQ) | **MQ:** MQOPEN/MQGET/MQPUT1/MQCLOSE · **CICS Read:** Card Xref, Account, Customer files · **CICS WRITEQ:** Error log | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 34 | COPAUS0C.cbl | COPAUS0C | Pending authorization summary list — CICS screen showing summary of pending authorizations with account/customer details | Online (CICS) | **CICS Read:** Account, Customer, Card Xref files; SEND/RECEIVE MAP | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | COPAUS1C | Pending authorization detail — CICS screen showing detailed authorization transaction data with LINK to COPAUS2C | Online (CICS) | **CICS:** SEND/RECEIVE MAP, LINK to COPAUS2C, SYNCPOINT | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | COPAUS2C | Authorization DB2 writer — CICS/DB2 program that inserts or updates authorization fraud records in AUTHFRDS DB2 table | Online (CICS + DB2) | **EXEC SQL:** INSERT/UPDATE/SELECT on AUTHFRDS | CIPAUDTY |
| 37 | DBUNLDGS.CBL | DBUNLDGS | IMS DB unload to GSAM — reads IMS authorization DB via DL/I and writes to GSAM output files (summary + detail segments) | Batch (IMS) | **IMS DB:** GN/GNP via CBLTDLI · **GSAM Write:** PASFILOP, PADFILOP | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 38 | PAUDBLOD.CBL | PAUDBLOD | IMS DB load — reads flat files and loads data into IMS authorization DB via DL/I ISRT calls | Batch (IMS) | **Read:** INFILE1, INFILE2 · **IMS DB:** ISRT/GU via CBLTDLI | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | PAUDBUNL.CBL | PAUDBUNL | IMS DB unload to flat files — reads IMS authorization DB and writes to sequential output files | Batch (IMS) | **IMS DB:** GN/GNP via CBLTDLI · **Write:** OPFILE1, OPFILE2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

---

## 3. COBOL Programs — Sub-Application: Transaction Type DB2 (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|---------------|-------------------|---------------------|
| 40 | COBTUPDT.cbl | COBTUPDT | Batch DB2 transaction type loader — reads flat file and inserts/updates transaction type records in DB2 TRANSACTION_TYPE table | Batch (DB2) | **Read:** INPFILE · **EXEC SQL:** SELECT, INSERT, DELETE on CARDDEMO.TRANSACTION_TYPE | DCLTRTYP (SQL INCLUDE) |
| 41 | COTRTLIC.cbl | COTRTLIC | Transaction type list/update — CICS/DB2 screen for browsing and modifying transaction type records with cursor-based pagination | Online (CICS + DB2) | **EXEC SQL:** SELECT, UPDATE, DELETE on TRANSACTION_TYPE, TRANSACTION_TYPE_CATEGORY · **CICS:** SEND/RECEIVE MAP, SYNCPOINT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, SQLCA, DCLTRTYP, CSDB2RPY |
| 42 | COTRTUPC.cbl | COTRTUPC | Transaction type add/update/delete — CICS/DB2 screen for CRUD operations on individual transaction type and category records | Online (CICS + DB2) | **EXEC SQL:** SELECT, INSERT, UPDATE, DELETE on TRANSACTION_TYPE, TRANSACTION_TYPE_CATEGORY · **CICS:** SEND/RECEIVE MAP, SYNCPOINT | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT |

---

## 4. COBOL Programs — Sub-Application: VSAM-MQ (`app/app-vsam-mq/cbl/`)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|---------------|-------------------|---------------------|
| 43 | COACCT01.cbl | COACCT01 | Account inquiry MQ service — CICS MQ listener that receives account lookup requests via MQ, reads account data via CICS, and returns response/error via MQ | Online (CICS + MQ) | **MQ:** MQOPEN/MQGET/MQPUT/MQCLOSE · **CICS Read:** Account file | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 44 | CODATE01.cbl | CODATE01 | Date/time MQ service — CICS MQ listener that receives date/time requests via MQ, retrieves system time via CICS ASKTIME/FORMATTIME, and returns response via MQ | Online (CICS + MQ) | **MQ:** MQOPEN/MQGET/MQPUT/MQCLOSE · **CICS:** ASKTIME, FORMATTIME | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 5. JCL Jobs — Main Application (`app/jcl/`)

| # | Filename | Job Name | Purpose | Step Sequence |
|---|----------|----------|---------|---------------|
| 1 | ACCTFILE.jcl | ACCTFILE | Delete and define account VSAM KSDS cluster, load from PS | STEP05: IDCAMS (delete) → STEP10: IDCAMS (define) → STEP15: IDCAMS (REPRO load) |
| 2 | CARDFILE.jcl | CARDFILE | Close CICS files, delete/define card VSAM KSDS + AIX clusters, load data, reopen CICS | CLCIFIL: SDSF (close) → STEP05–STEP15: IDCAMS (delete/define/load primary) → STEP40–STEP60: IDCAMS (define/build/define path AIX) → OPCIFIL: SDSF (open) |
| 3 | CBADMCDJ.jcl | CBADMCDJ | Update CICS CSD (System Definition) with CardDemo program/transaction definitions | STEP1: DFHCSDUP (CSD batch utility) |
| 4 | CBEXPORT.jcl | CBEXPORT | Export all master data files into unified export PS file for branch migration | STEP01: IDCAMS (define export dataset) → STEP02: CBEXPORT (run export program) |
| 5 | CBIMPORT.jcl | CBIMPORT | Import unified export file into individual entity output files | STEP01: CBIMPORT (run import program) |
| 6 | CLOSEFIL.jcl | CLOSEFIL | Close all CICS-managed files via SDSF operator commands | CLCIFIL: SDSF |
| 7 | COMBTRAN.jcl | COMBTRAN | Sort daily transactions and combine into transaction master VSAM | STEP05R: SORT (sort by card + ID) → STEP10: IDCAMS (REPRO into VSAM) |
| 8 | CREASTMT.JCL | CREASTMT | End-to-end statement creation pipeline — sort transactions, load to VSAM, generate statements | DELDEF01: IDCAMS (cleanup) → STEP010: SORT → STEP020: IDCAMS (REPRO) → STEP030: IEFBR14 (allocate output) → STEP040: CBSTM03A (generate statements) |
| 9 | CUSTFILE.jcl | CUSTFILE | Close CICS, delete/define customer VSAM KSDS, load data, reopen CICS | CLCIFIL: SDSF → STEP05–STEP15: IDCAMS → OPCIFIL: SDSF |
| 10 | DALYREJS.jcl | DALYREJS | Define GDG base for daily rejection files | STEP05: IDCAMS (DEFINE GDG) |
| 11 | DEFCUST.jcl | DEFCUST | Define customer data file VSAM cluster | STEP05: IDCAMS (delete) → STEP05: IDCAMS (define) |
| 12 | DEFGDGB.jcl | DEFGDGB | Define GDG bases for backup generations (statements, reports, rejects) | STEP05: IDCAMS (DEFINE GDG — multiple entries) |
| 13 | DEFGDGD.jcl | DEFGDGD | Define DB2-related GDG bases and seed initial backup generations | STEP10–STEP60: IDCAMS/IEBGENER (define GDG, copy initial data for TRANTYPE, TRANCATG, DISCGRP) |
| 14 | DISCGRP.jcl | DISCGRP | Delete/define disclosure group VSAM KSDS and load from PS | STEP05–STEP15: IDCAMS (delete/define/REPRO) |
| 15 | DUSRSECJ.jcl | DUSRSECJ | Create and load user security VSAM file from inline data | PREDEL: IEFBR14 → STEP01: IEBGENER (create PS) → STEP02: IDCAMS (define VSAM) → STEP03: IDCAMS (REPRO) |
| 16 | ESDSRRDS.jcl | ESDSRRDS | Define ESDS and RRDS VSAM files for testing alternate access methods | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02–STEP05: IDCAMS (define ESDS/RRDS, REPRO) |
| 17 | FTPJCL.JCL | FTPJCLS | FTP utility job to receive files from remote host | STEP1: FTP |
| 18 | INTCALC.jcl | INTCALC | Run interest calculation batch program | STEP15: CBACT04C (interest calculator) |
| 19 | INTRDRJ1.JCL | INTRDRJ1 | Internal reader job — backs up dataset and triggers INTRDRJ2 via internal reader | IDCAMS: IDCAMS (backup) → STEP01: IEBGENER (submit INTRDRJ2) |
| 20 | INTRDRJ2.JCL | INTRDRJ2 | Internal reader target job — creates physical VSAM file for IMS demo DB | IDCAMS: IDCAMS |
| 21 | OPENFIL.jcl | OPENFIL | Open all CICS-managed files via SDSF operator commands | OPCIFIL: SDSF |
| 22 | POSTTRAN.jcl | POSTTRAN | Run daily transaction posting batch program | STEP15: CBTRN02C (post transactions) |
| 23 | PRTCATBL.jcl | PRTCATBL | Print transaction category balance report | DELDEF: IEFBR14 → STEP05R: REPROC (REPRO extract) → STEP10R: SORT |
| 24 | READACCT.jcl | READACCT | Read/extract account data to flat files | PREDEL: IEFBR14 → STEP05: CBACT01C |
| 25 | READCARD.jcl | READCARD | Read/display card data | STEP05: CBACT02C |
| 26 | READCUST.jcl | READCUST | Read/display customer data | STEP05: CBCUS01C |
| 27 | READXREF.jcl | READXREF | Read/display cross-reference data | STEP05: CBACT03C |
| 28 | REPTFILE.jcl | REPTFILE | Define GDG base for report output files | STEP05: IDCAMS (DEFINE GDG) |
| 29 | TCATBALF.jcl | TCATBALF | Delete/define transaction category balance VSAM KSDS and load | STEP05–STEP15: IDCAMS |
| 30 | TRANBKP.jcl | TRANBKP | Backup and clear transaction master VSAM | STEP05R: REPROC (REPRO backup) → STEP05: IDCAMS (delete) → STEP10: IDCAMS (redefine) |
| 31 | TRANCATG.jcl | TRANCATG | Delete/define transaction category VSAM KSDS and load | STEP05–STEP15: IDCAMS |
| 32 | TRANFILE.jcl | TRANFILE | Close CICS, delete/define transaction master VSAM KSDS + AIX, load data, reopen CICS | CLCIFIL: SDSF → STEP05–STEP30: IDCAMS → OPCIFIL: SDSF |
| 33 | TRANIDX.jcl | TRANIDX | Define alternate index (AIX) on transaction master VSAM | STEP20–STEP30: IDCAMS (define AIX, build path, define path) |
| 34 | TRANREPT.jcl | TRANREPT | Run transaction report — extract, sort, then generate report | STEP05R: REPROC/SORT → STEP10R: CBTRN03C |
| 35 | TRANTYPE.jcl | TRANTYPE | Delete/define transaction type VSAM KSDS and load | STEP05–STEP15: IDCAMS |
| 36 | TXT2PDF1.JCL | TXT2PDF1 | Convert text statement file to PDF format | TXT2PDF: IKJEFT1B (TSO REXX exec) |
| 37 | WAITSTEP.jcl | WAITSTEP | Execute wait/delay program for batch scheduling | WAIT: COBSWAIT |
| 38 | XREFFILE.jcl | XREFFILE | Delete/define card cross-reference VSAM KSDS + AIX and load | STEP05–STEP30: IDCAMS |

---

## 6. JCL Jobs — Sub-Application: Authorization IMS-DB2-MQ (`app/app-authorization-ims-db2-mq/jcl/`)

| # | Filename | Job Name | Purpose | Step Sequence |
|---|----------|----------|---------|---------------|
| 39 | CBPAUP0J.jcl | CBPAUP0J | Run IMS pending authorization purge via DL/I batch | STEP01: DFSRRC00 (IMS batch region → CBPAUP0C) |
| 40 | DBPAUTP0.jcl | DBPAUTP0 | Unload IMS authorization DB to sequential dataset | STEPDEL: IEFBR14 → UNLOAD: DFSRRC00 (IMS batch unload) |
| 41 | LOADPADB.JCL | LOADPADB | Load IMS authorization DB from flat files | STEP01: DFSRRC00 (IMS batch load → PAUDBLOD) |
| 42 | UNLDGSAM.JCL | UNLDGSAM | Unload IMS authorization DB to GSAM files | STEP01: DFSRRC00 (IMS batch → DBUNLDGS) |
| 43 | UNLDPADB.JCL | UNLDPADB | Unload IMS authorization DB to flat output files | STEP0: IEFBR14 → STEP01: DFSRRC00 (IMS batch → PAUDBUNL) |

---

## 7. JCL Jobs — Sub-Application: Transaction Type DB2 (`app/app-transaction-type-db2/jcl/`)

| # | Filename | Job Name | Purpose | Step Sequence |
|---|----------|----------|---------|---------------|
| 44 | CREADB21.jcl | CREADB2 | Create DB2 objects — free plan, create tables/indexes, load transaction type and category data | FREEPLN: IKJEFT01 → CRCRDDB: IKJEFT01 → LDTTYPE: IEFBR14 → RUNTEP2: IKJEFT01 → LDTCCAT: IKJEFT01 |
| 45 | MNTTRDB2.jcl | MNTTRDB2 | Maintain DB2 transaction type table — batch insert/update/delete via TSO | STEP1: IKJEFT01 (bind + run COBTUPDT) |
| 46 | TRANEXTR.jcl | TRANEXTR | Extract DB2 transaction type/category data to sequential backup files | STEP10–STEP20: IEBGENER (backup) → STEP30: IEFBR14 → STEP40–STEP50: IKJEFT01 (SQL UNLOAD) |

---

## 8. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Batch programs | 14 |
| Online (CICS) programs | 19 |
| Online (CICS + MQ) programs | 3 |
| Online (CICS + DB2) programs | 3 |
| Batch (IMS) programs | 4 |
| Batch (DB2) programs | 1 |
| Utility programs | 2 (CSUTLDTC, COBSWAIT) |
| Total JCL jobs | 46 |
| Total copybooks (main) | 30 |
| Total copybooks (sub-apps) | 13 |
| BMS maps | 18 |
| Assembler modules | 2 |
| Total lines of COBOL | ~28,700 |
