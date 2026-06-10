# Application Inventory

## Overview

**Application:** CardDemo — Credit Card Management System  
**Platform:** IBM Mainframe (z/OS)  
**Technologies:** COBOL, CICS, VSAM, DB2, IMS, MQ, BMS, JCL  
**Total Programs:** 44 (31 in `app/cbl/`, 8 in `app-authorization-ims-db2-mq/`, 3 in `app-transaction-type-db2/`, 2 in `app-vsam-mq/`)

---

## Programs — `app/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | 430 | Read account VSAM file and write to multiple output formats (PS, array, variable-block) | Batch | READ ACCTFILE; WRITE OUT-FILE, ARRY-FILE, VBRC-FILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Read and print card data file | Batch | READ CARDFILE | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Read and print account cross-reference data file | Batch | READ XREFFILE | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Interest calculation — apply rates per category to account balances | Batch | READ TCATBAL, XREF, DISCGRP, ACCOUNT; REWRITE ACCOUNT; WRITE TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Read and print customer data file | Batch | READ CUSTFILE | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Export customer profiles — consolidate all entity files into single export stream | Batch | READ CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD; WRITE EXPORT-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Import from export file — parse and distribute records to individual entity files | Batch | READ EXPORT-INPUT; WRITE CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD, ERROR | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | 924 | Statement generation — produce text and HTML customer statements | Batch | OPEN STMT-FILE, HTML-FILE; CALL CBSTM03B for file I/O (TRNX, XREF, CUST, ACCT) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | 230 | I/O sub-module for statement generation (called by CBSTM03A) | Batch (Module) | OPEN/READ TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE | (none — receives data via USING) |
| 10 | CBTRN01C.cbl | 494 | Transaction validation — validate daily transactions against master files | Batch | READ DALYTRAN, XREF, ACCOUNT, CUSTOMER, CARD, TRANSACT | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | 731 | Post daily transactions — apply validated transactions to master files | Batch | READ DALYTRAN, XREF, ACCOUNT; WRITE TRANSACT, DALYREJS; REWRITE ACCOUNT, TCATBAL | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | 649 | Transaction reporting — generate categorized transaction reports | Batch | READ TRANSACT, XREF, TRANTYPE, TRANCATG, DATE-PARMS; WRITE REPORT-FILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COACTUPC.cbl | 4236 | Account update — full-screen account maintenance with field validation | Online (CICS) | EXEC CICS READ/REWRITE ACCTFILE, CARDFILE, CUSTFILE | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY |
| 14 | COACTVWC.cbl | 941 | Account view — read-only display of account details | Online (CICS) | EXEC CICS READ ACCTFILE, CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 15 | COADM01C.cbl | 288 | Admin menu — navigation hub for administrative functions | Online (CICS) | EXEC CICS SEND/RECEIVE MAP | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | 572 | Bill payment — process account payments with balance updates | Online (CICS) | EXEC CICS READ/REWRITE ACCTFILE; EXEC CICS WRITE TRANSACT; STARTBR/READPREV/ENDBR | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | 41 | Wait utility — pause execution for specified interval | Batch (Utility) | CALL 'MVSWAIT' | (none) |
| 18 | COCRDLIC.cbl | 1459 | Credit card list — paginated browse of cards with filtering | Online (CICS) | EXEC CICS STARTBR/READNEXT/ENDBR CARDFILE; EXEC CICS XCTL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 19 | COCRDSLC.cbl | 887 | Credit card view — display card details with customer and account info | Online (CICS) | EXEC CICS READ CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 20 | COCRDUPC.cbl | 1560 | Credit card update — modify card attributes with validation | Online (CICS) | EXEC CICS READ/REWRITE CARDFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 21 | COMEN01C.cbl | 308 | Main menu — central navigation hub routing to all user functions (11 targets) | Online (CICS) | EXEC CICS SEND/RECEIVE MAP; EXEC CICS XCTL | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 22 | CORPT00C.cbl | 649 | Report request — collect date parameters and submit batch report JCL | Online (CICS) | EXEC CICS WRITEQ TD (submit JCL); CALL CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | 260 | Sign-on — authenticate users and route to admin/user menu | Online (CICS) | EXEC CICS READ USRSEC; EXEC CICS XCTL | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | COTRN00C.cbl | 699 | Transaction list — paginated browse of account transactions | Online (CICS) | EXEC CICS STARTBR/READNEXT/READPREV/ENDBR TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN01C.cbl | 330 | Transaction view — display single transaction detail | Online (CICS) | EXEC CICS READ TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | 783 | Transaction add — create new transaction with validation | Online (CICS) | EXEC CICS READ XREF, ACCT; EXEC CICS WRITE TRANSACT; STARTBR/READPREV/ENDBR | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | 695 | User list — paginated browse of security users | Online (CICS) | EXEC CICS STARTBR/READNEXT/READPREV/ENDBR USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | 299 | User add — create new security user record | Online (CICS) | EXEC CICS WRITE USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR02C.cbl | 414 | User update — modify existing security user | Online (CICS) | EXEC CICS READ/REWRITE USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | 359 | User delete — remove security user record | Online (CICS) | EXEC CICS READ/DELETE USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | 157 | Date utility — validate and convert date formats using LE services | Batch (Utility) | CALL 'CEEDAYS' | (none — receives data via USING) |

---

## Programs — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 32 | CBPAUP0C.cbl | 386 | Purge expired pending authorizations from IMS database | Batch (IMS) | EXEC DLI GN, GNP, DLET, CHKP | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | 1026 | Authorization decision engine — process auth requests via MQ, IMS lookup, DB2 logging | Online (CICS/IMS/MQ) | MQOPEN, MQGET, MQPUT1; IMS GU/SCHD/TERM; EXEC SQL INSERT | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y |
| 34 | COPAUS0C.cbl | 1032 | Pending authorization summary — paginated browse of auth records | Online (CICS/IMS) | EXEC CICS SEND/RECEIVE; IMS GU/GNP | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | 604 | Pending authorization detail — view and update individual auth record | Online (CICS/IMS) | EXEC CICS SEND/LINK; IMS GU/GNP/REPL | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | 244 | Fraud marking — flag authorization as fraudulent via DB2 | Online (CICS/DB2) | EXEC SQL SELECT, INSERT; EXEC CICS ASKTIME/FORMATTIME | CIPAUDTY |
| 37 | DBUNLDGS.CBL | 366 | GSAM unload — extract IMS data to sequential file via GSAM | Batch (IMS) | CALL CBLTDLI GN, GNP, ISRT | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 38 | PAUDBLOD.CBL | 369 | IMS database load — populate pending authorization IMS DB from flat file | Batch (IMS) | CALL CBLTDLI ISRT, GU | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | PAUDBUNL.CBL | 317 | IMS database unload — extract pending auths to flat file | Batch (IMS) | CALL CBLTDLI GN, GNP | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

---

## Programs — `app/app-transaction-type-db2/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 40 | COBTUPDT.cbl | 237 | Batch transaction type update — bulk maintenance of type/category tables via DB2 | Batch (DB2) | EXEC SQL SELECT, INSERT, UPDATE, DELETE | (DB2 DCL includes) |
| 41 | COTRTLIC.cbl | 2098 | Transaction type list — cursor-based paginated browse of DB2 type/category tables | Online (CICS/DB2) | EXEC SQL DECLARE CURSOR, OPEN, FETCH, CLOSE | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, DB2 DCL |
| 42 | COTRTUPC.cbl | 1702 | Transaction type maintenance — add/update/delete with cascading deletes | Online (CICS/DB2) | EXEC SQL SELECT, INSERT, UPDATE, DELETE | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, DB2 DCL |

---

## Programs — `app/app-vsam-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | 620 | Account inquiry via MQ — retrieve account data and respond via message queue | Online (CICS/MQ) | EXEC CICS READ ACCTFILE; MQ GET/PUT | COCOM01Y, CVACT01Y, MQ copybooks |
| 44 | CODATE01.cbl | 524 | Date inquiry via MQ — provide formatted date info via message queue | Online (CICS/MQ) | MQ GET/PUT | COCOM01Y, CSDAT01Y, MQ copybooks |

---

## JCL Job Catalog — `app/jcl/`

| # | Job Name | Filename | Steps | Purpose |
|---|----------|----------|-------|---------|
| 1 | ACCTFILE | ACCTFILE.jcl | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE CLUSTER), STEP15 (IDCAMS REPRO) | Define and load Account VSAM KSDS cluster |
| 2 | CARDFILE | CARDFILE.jcl | CLCIFIL (SDSF close), STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | Define and load Card VSAM KSDS cluster |
| 3 | CBADMCDJ | CBADMCDJ.jcl | STEP1 (DFHCSDUP) | CICS CSD update — register transactions and programs |
| 4 | CBEXPORT | CBEXPORT.jcl | STEP01 (IDCAMS DEFINE), STEP02 (PGM=CBEXPORT) | Export all entity files to consolidated export dataset |
| 5 | CBIMPORT | CBIMPORT.jcl | STEP01 (PGM=CBIMPORT) | Import from export file into individual entity datasets |
| 6 | CLOSEFIL | CLOSEFIL.jcl | CLCIFIL (SDSF) | Close CICS files via SDSF operator commands |
| 7 | COMBTRAN | COMBTRAN.jcl | STEP05R (SORT merge), STEP10 (IDCAMS REPRO) | Combine and sort transaction files into VSAM |
| 8 | CREASTMT | CREASTMT.JCL | DELDEF01 (IDCAMS), STEP010 (SORT), STEP020 (IDCAMS REPRO), STEP030 (IEFBR14) | Sort transactions and prepare for statement generation |
| 9 | CUSTFILE | CUSTFILE.jcl | CLCIFIL (SDSF), STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | Define and load Customer VSAM KSDS cluster |
| 10 | DALYREJS | DALYREJS.jcl | STEP05 (IDCAMS DEFINE) | Define VSAM cluster for daily rejected transactions |
| 11 | DEFCUST | DEFCUST.jcl | STEP05 (IDCAMS DELETE), STEP05 (IDCAMS DEFINE) | Define alternate Customer VSAM cluster |
| 12 | DEFGDGB | DEFGDGB.jcl | STEP05 (IDCAMS DEFINE GDG) | Define Generation Data Group base for backups |
| 13 | DEFGDGD | DEFGDGD.jcl | STEP10 (IDCAMS DEFINE GDG), STEP20 (IEBGENER backup), STEP30 (IDCAMS DELETE), STEP40 (IEBGENER restore) | Define GDG and manage transaction type backup/restore |
| 14 | DISCGRP | DISCGRP.jcl | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | Define and load Discount Group VSAM cluster |
| 15 | DUSRSECJ | DUSRSECJ.jcl | PREDEL (IEFBR14), STEP01 (IEBGENER), STEP02 (IDCAMS DEFINE), STEP03 (IDCAMS REPRO) | Create and load User Security VSAM KSDS |
| 16 | ESDSRRDS | ESDSRRDS.jcl | PREDEL (IEFBR14), STEP01 (IEBGENER), STEP02 (IDCAMS DEFINE ESDS), STEP03 (IDCAMS REPRO) | Define and load ESDS/RRDS test cluster |
| 17 | FTPJCL | FTPJCL.JCL | STEP1 (PGM=FTP) | FTP file transfer utility |
| 18 | INTCALC | INTCALC.jcl | STEP15 (PGM=CBACT04C) | Execute interest calculation batch program |
| 19 | INTRDRJ1 | INTRDRJ1.JCL | IDCAMS (backup), STEP01 (IEBGENER → internal reader) | Submit INTRDRJ2 via internal reader (JCL chaining) |
| 20 | INTRDRJ2 | INTRDRJ2.JCL | IDCAMS (REPRO) | Copy backup dataset (submitted by INTRDRJ1) |
| 21 | OPENFIL | OPENFIL.jcl | OPCIFIL (SDSF) | Open CICS files via SDSF operator commands |
| 22 | POSTTRAN | POSTTRAN.jcl | STEP15 (PGM=CBTRN02C) | Execute daily transaction posting batch program |
| 23 | PRTCATBL | PRTCATBL.jcl | DELDEF (IEFBR14), STEP05R (REPROC), STEP10R (SORT) | Print/sort transaction category balance report |
| 24 | READACCT | READACCT.jcl | PREDEL (IEFBR14), STEP05 (PGM=CBACT01C) | Read and convert account file to multiple formats |
| 25 | READCARD | READCARD.jcl | STEP05 (PGM=CBACT02C) | Read and print card data file |
| 26 | READCUST | READCUST.jcl | STEP05 (PGM=CBCUS01C) | Read and print customer data file |
| 27 | READXREF | READXREF.jcl | STEP05 (PGM=CBACT03C) | Read and print cross-reference file |
| 28 | REPTFILE | REPTFILE.jcl | STEP05 (IDCAMS DEFINE) | Define VSAM cluster for report output |
| 29 | TCATBALF | TCATBALF.jcl | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | Define and load Transaction Category Balance cluster |
| 30 | TRANBKP | TRANBKP.jcl | STEP05R (REPROC backup), STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE) | Backup transaction file and redefine cluster |
| 31 | TRANCATG | TRANCATG.jcl | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | Define and load Transaction Category reference cluster |
| 32 | TRANFILE | TRANFILE.jcl | CLCIFIL (SDSF), STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | Define and load Transaction VSAM KSDS cluster |
| 33 | TRANIDX | TRANIDX.jcl | STEP20 (IDCAMS DELETE AIX), STEP25 (IDCAMS DEFINE AIX), STEP30 (IDCAMS BLDINDEX) | Define alternate index on transaction file |
| 34 | TRANREPT | TRANREPT.jcl | STEP05R (REPROC/SORT), STEP10R (PGM=CBTRN03C) | Sort transactions then generate daily report |
| 35 | TRANTYPE | TRANTYPE.jcl | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | Define and load Transaction Type reference cluster |
| 36 | TXT2PDF1 | TXT2PDF1.JCL | TXT2PDF (PGM=IKJEFT1B → TXT2PDF REXX) | Convert text statement file to PDF |
| 37 | WAITSTEP | WAITSTEP.jcl | WAIT (PGM=COBSWAIT) | Execute wait utility program |
| 38 | XREFFILE | XREFFILE.jcl | STEP05–STEP25 (IDCAMS DELETE/DEFINE/REPRO/DEFINE AIX/BLDINDEX) | Define Card-Account XREF VSAM cluster with alternate indexes |

---

## Classification Summary

| Category | Count | Percentage |
|----------|-------|-----------|
| Online (CICS) | 17 | 39% |
| Online (CICS + IMS/DB2/MQ) | 8 | 18% |
| Batch (Pure) | 12 | 27% |
| Batch (IMS) | 4 | 9% |
| Batch (DB2) | 1 | 2% |
| Utility | 2 | 5% |
| **Total** | **44** | **100%** |
