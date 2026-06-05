# APPLICATION INVENTORY — CardDemo COBOL Estate

## Overview

| Metric | Value |
|--------|-------|
| Total COBOL Programs | 44 |
| Main Programs (app/cbl/) | 31 |
| Sub-App Programs | 13 |
| Total Lines of Code | 30,175 |
| Copybooks | 47 |
| JCL Jobs | 46 |
| BMS Maps | 16 |

---

## 1. Main Programs (app/cbl/)

### Online (CICS) Programs

| Filename | LOC | Purpose | Key I/O | Copybooks Referenced |
|----------|-----|---------|---------|---------------------|
| COSGN00C.cbl | 260 | Sign-on screen — authenticates users against USRSEC VSAM file | VSAM READ: USRSEC | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COADM01C.cbl | 288 | Admin menu — routes to user management and DB2 transaction type screens | CICS SEND/RECEIVE MAP | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COMEN01C.cbl | 308 | Main menu hub — 11 navigation options routing to all user-facing screens | CICS XCTL to 11 programs | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COACTUPC.cbl | 4,236 | Account update — exhaustive field validation (date, SSN, phone, state, ZIP) and account/customer/card update | VSAM READ/REWRITE: ACCTFILE, CUSTFILE, CARDXREF | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×39), CSSTRPFY, CSUTLDPY |
| COACTVWC.cbl | 941 | Account view — read-only display of account, card, and customer data | VSAM READ: ACCTFILE, CARDXREF, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| COCRDLIC.cbl | 1,459 | Credit card list — paginated browse of card records with STARTBR/READNEXT/READPREV | VSAM STARTBR/READNEXT/READPREV/ENDBR: CARDFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| COCRDSLC.cbl | 887 | Credit card view — displays single card details with account lookup | VSAM READ: CARDFILE, CARDXREF (via AIX) | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| COCRDUPC.cbl | 1,560 | Credit card update — modify card details with validation and rewrite | VSAM READ/REWRITE: CARDFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| COTRN00C.cbl | 699 | Transaction list — paginated browse of transaction file | VSAM STARTBR/READNEXT/READPREV/ENDBR: TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COTRN01C.cbl | 330 | Transaction view — displays single transaction detail | VSAM READ: TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COTRN02C.cbl | 783 | Transaction add — creates new transactions with date validation | VSAM READ/WRITE: TRANSACT; READ: CARDXREF, ACCTFILE | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVTRA05Y, DFHAID, DFHBMSCA, CSUTLDWY, CSUTLDPY |
| CORPT00C.cbl | 649 | Report request — submits batch JCL for report generation via TDQ | CICS WRITEQ TD (submits INTRDRJ1/J2 JCL) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COBIL00C.cbl | 572 | Bill payment — processes bill payments against accounts | VSAM READ/REWRITE: ACCTFILE; WRITE: TRANSACT | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COUSR00C.cbl | 695 | User list — paginated browse of security user records | VSAM STARTBR/READNEXT/READPREV/ENDBR: USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR01C.cbl | 299 | User add — creates new user security records | VSAM WRITE: USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR02C.cbl | 414 | User update — modifies existing user records | VSAM READ/REWRITE: USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR03C.cbl | 359 | User delete — removes user security records | VSAM READ/DELETE: USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| CSUTLDTC.cbl | 157 | Date utility — reusable date conversion subroutine (CALL target) | CALL CEEDAYS (LE runtime) | _(none — standalone utility)_ |

### Batch Programs

| Filename | LOC | Purpose | Key I/O | Copybooks Referenced |
|----------|-----|---------|---------|---------------------|
| CBACT01C.cbl | 430 | Read account VSAM file and write to sequential output formats (PS, array, VB) | READ: ACCTFILE(VSAM); WRITE: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| CBACT02C.cbl | 178 | Read and print card data file | READ: CARDFILE(VSAM) | CVACT02Y |
| CBACT03C.cbl | 178 | Read and print card-account cross-reference file | READ: XREFFILE(VSAM) | CVACT03Y |
| CBACT04C.cbl | 652 | Interest calculation — applies interest rates to account balances per category | READ: TCATBALF, XREFFILE, DISCGRP; I-O: ACCTFILE; WRITE: TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| CBCUS01C.cbl | 178 | Read and print customer data file | READ: CUSTFILE(VSAM) | CVCUS01Y |
| CBEXPORT.cbl | 582 | Export all CardDemo data to sequential file for branch migration | READ: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; WRITE: EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBIMPORT.cbl | 487 | Import data from export file back into individual entity files | READ: EXPFILE; WRITE: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBTRN01C.cbl | 494 | Validate daily transactions against xref, account, and card files | READ: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| CBTRN02C.cbl | 731 | Post daily transactions — validate and write to master, reject invalid | READ: DALYTRAN, XREFFILE; I-O: ACCTFILE, TCATBALF; WRITE: TRANFILE, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| CBTRN03C.cbl | 649 | Transaction report — generates daily transaction report with totals | READ: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; WRITE: TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| CBSTM03A.CBL | 924 | Statement generation — produces text and HTML customer statements | READ (via CBSTM03B): TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE; WRITE: STMTFILE, HTMLFILE | COSTM01 |
| CBSTM03B.CBL | 230 | Statement I/O submodule — file open/read/close operations for CBSTM03A | READ: TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | _(inline FD definitions)_ |
| COBSWAIT.cbl | 41 | Wait utility — calls assembler wait routine (batch scheduling aid) | CALL MVSWAIT | _(none)_ |

---

## 2. Sub-Application Programs

### app/app-authorization-ims-db2-mq/ (IMS/DB2/MQ Authorization)

| Filename | LOC | Purpose | Classification | Key I/O | Copybooks Referenced |
|----------|-----|---------|----------------|---------|---------------------|
| COPAUA0C.cbl | 1,026 | Authorization decision engine — processes auth requests via MQ, validates against IMS and DB2 | Online (CICS + IMS + MQ) | MQ: MQOPEN/MQGET/MQPUT1; IMS DL/I; CICS RETRIEVE | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| COPAUS0C.cbl | 1,032 | Pending authorization summary — browse IMS auth records with CICS screen | Online (CICS + IMS) | IMS DL/I: GNP; VSAM READ: CARDXREF | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| COPAUS1C.cbl | 604 | Pending authorization detail — view/update individual auth records in IMS | Online (CICS + IMS) | IMS DL/I: GU, GNP, REPL, SCHD, TERM; CICS LINK | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| COPAUS2C.cbl | 244 | Fraud marking — inserts fraud records into DB2 AUTHFRDS table | Online (CICS + DB2) | EXEC SQL INSERT/SELECT on AUTHFRDS | CIPAUDTY |
| CBPAUP0C.cbl | 386 | Expired authorization purge — batch deletion of expired IMS records | Batch (IMS) | IMS DL/I: GN, GNP, DLET, CHKP | CIPAUSMY, CIPAUDTY |
| PAUDBLOD.CBL | 369 | IMS database load — bulk insert of auth records from sequential files | Batch (IMS) | READ: INFILE1, INFILE2; IMS: GU, ISRT | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| PAUDBUNL.CBL | 317 | IMS database unload — extract auth records to sequential files | Batch (IMS) | IMS: GN, GNP; WRITE: OPFILE1, OPFILE2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| DBUNLDGS.CBL | 366 | GSAM unload — IMS unload via GSAM access method | Batch (IMS/GSAM) | IMS: GN, GNP (via CBLTDLI) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### app/app-transaction-type-db2/ (DB2 Transaction Type Management)

| Filename | LOC | Purpose | Classification | Key I/O | Copybooks Referenced |
|----------|-----|---------|----------------|---------|---------------------|
| COTRTLIC.cbl | 2,098 | Transaction type list — cursor-based DB2 pagination with CICS screen | Online (CICS + DB2) | EXEC SQL: SELECT with CURSOR on TRNTYPE; CICS SEND/RECEIVE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSDB2RWY (SQL INCLUDE), DCLTRTYP |
| COTRTUPC.cbl | 1,702 | Transaction type update/delete — CRUD operations on DB2 TRNTYPE table | Online (CICS + DB2) | EXEC SQL: SELECT/UPDATE/DELETE/INSERT on TRNTYPE/TRNTYCAT | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CSDB2RPY, DCLTRTYP, DCLTRCAT |
| COBTUPDT.cbl | 237 | Batch transaction type update — loads transaction types from file to DB2 | Batch (DB2) | READ: INPFILE; EXEC SQL: INSERT/UPDATE on TRNTYPE | DCLTRTYP (SQL INCLUDE) |

### app/app-vsam-mq/ (VSAM/MQ Integration)

| Filename | LOC | Purpose | Classification | Key I/O | Copybooks Referenced |
|----------|-----|---------|----------------|---------|---------------------|
| COACCT01.cbl | 620 | Account inquiry via MQ — responds to account lookup requests from queue | Online (CICS + MQ) | MQ: MQOPEN/MQGET/MQPUT; VSAM READ: ACCTFILE | _(MQ copybooks)_ |
| CODATE01.cbl | 524 | Date inquiry via MQ — provides date formatting service over MQ | Online (CICS + MQ) | MQ: MQOPEN/MQGET/MQPUT | _(MQ copybooks)_ |

---

## 3. JCL Job Catalog (app/jcl/)

### Data Setup Jobs (VSAM Cluster Definition via IDCAMS)

| Job | Steps | Purpose | Datasets |
|-----|-------|---------|----------|
| ACCTFILE.jcl | STEP05→STEP10→STEP15 | Define/delete/repro ACCTDATA KSDS cluster | AWS.M2.CARDDEMO.ACCTDATA.PS → .VSAM.KSDS |
| CARDFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→STEP40→STEP50→STEP60→OPCIFIL | Define CARDDATA KSDS + AIX clusters | AWS.M2.CARDDEMO.CARDDATA.PS → .VSAM.KSDS + AIX |
| CUSTFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→OPCIFIL | Define CUSTDATA KSDS cluster | AWS.M2.CARDDEMO.CUSTDATA.PS → .VSAM.KSDS |
| XREFFILE.jcl | STEP05→STEP10→STEP15→STEP20→STEP25→STEP30 | Define CARDXREF KSDS + AIX clusters | AWS.M2.CARDDEMO.CARDXREF.PS → .VSAM.KSDS + AIX |
| TRANFILE.jcl | CLCIFIL→STEP05→STEP10→STEP15→STEP20→STEP25→STEP30→OPCIFIL | Define TRANSACT KSDS + AIX clusters | AWS.M2.CARDDEMO.DALYTRAN.PS → .TRANSACT.VSAM.KSDS |
| TCATBALF.jcl | STEP05→STEP10→STEP15 | Define TCATBALF (category balance) KSDS | AWS.M2.CARDDEMO.TCATBALF.PS → .VSAM.KSDS |
| TRANTYPE.jcl | STEP05→STEP10→STEP15 | Define TRANTYPE KSDS cluster | AWS.M2.CARDDEMO.TRANTYPE.PS → .VSAM.KSDS |
| TRANCATG.jcl | STEP05→STEP10→STEP15 | Define TRANCATG KSDS cluster | AWS.M2.CARDDEMO.TRANCATG.PS → .VSAM.KSDS |
| DISCGRP.jcl | STEP05→STEP10→STEP15 | Define DISCGRP (disclosure group) KSDS | AWS.M2.CARDDEMO.DISCGRP.PS → .VSAM.KSDS |
| DALYREJS.jcl | STEP05 | Define daily rejects GDG cluster | AWS.M2.CARDDEMO.DALYREJS |
| REPTFILE.jcl | STEP05 | Define report output sequential file | AWS.M2.CARDDEMO.TRANREPT |
| DEFCUST.jcl | STEP05 (×2) | Define alternate customer cluster | AWS.M2.CARDDEMO.CUSTDATA |
| DEFGDGB.jcl | STEP05 | Define GDG base for transaction backups | AWS.M2.CARDDEMO.TRANSACT.BKUP |
| DEFGDGD.jcl | STEP10→STEP20→STEP30→STEP40→STEP50→STEP60 | Define GDG bases + backup TRANTYPE/TRANCATG/DISCGRP | AWS.M2.CARDDEMO.TRANTYPE.BKUP, .TRANCATG.PS.BKUP, .DISCGRP.BKUP |
| DUSRSECJ.jcl | PREDEL→STEP01→STEP02→STEP03 | Create and load USRSEC security VSAM file | AWS.M2.CARDDEMO.USRSEC.PS → .VSAM.KSDS |
| ESDSRRDS.jcl | PREDEL→STEP01→STEP02→STEP03→STEP04→STEP05 | Define ESDS/RRDS demo clusters | AWS.M2.CARDDEMO.ESDSRRDS.PS → ESDS/RRDS |
| TRANIDX.jcl | STEP20→STEP25→STEP30 | Define alternate indexes for TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM AIX |

### Batch Processing Jobs

| Job | Steps | Program Executed | Purpose | Input Datasets | Output Datasets |
|-----|-------|-----------------|---------|----------------|-----------------|
| POSTTRAN.jcl | STEP15 | CBTRN02C | Post daily transactions | DALYTRAN.PS, CARDXREF.VSAM, ACCTDATA.VSAM, TCATBALF.VSAM | TRANSACT.VSAM, DALYREJS(+1) |
| INTCALC.jcl | STEP15 | CBACT04C | Calculate interest on accounts | TCATBALF.VSAM, CARDXREF.VSAM, DISCGRP.VSAM, ACCTDATA.VSAM | SYSTRAN(+1) |
| CREASTMT.JCL | DELDEF01→STEP010→STEP020→STEP030→STEP040 | SORT→IDCAMS→IEFBR14→CBSTM03A | Generate customer statements | TRANSACT.VSAM.KSDS, CARDXREF.VSAM, ACCTDATA.VSAM, CUSTDATA.VSAM | STATEMNT.PS, STATEMNT.HTML |
| TRANREPT.jcl | STEP05R→STEP05R→STEP10R | REPROC→SORT→CBTRN03C | Generate daily transaction report | TRANSACT.VSAM, CARDXREF.VSAM, TRANTYPE.VSAM, TRANCATG.VSAM, DATEPARM | TRANREPT(+1) |
| READACCT.jcl | PREDEL→STEP05 | CBACT01C | Read accounts to sequential format | ACCTDATA.VSAM.KSDS | ACCTDATA.PSCOMP, .ARRYPS, .VBPS |
| READCARD.jcl | STEP05 | CBACT02C | Read card data file | CARDDATA.VSAM.KSDS | _(SYSOUT display)_ |
| READCUST.jcl | STEP05 | CBCUS01C | Read customer data file | CUSTDATA.VSAM.KSDS | _(SYSOUT display)_ |
| READXREF.jcl | STEP05 | CBACT03C | Read cross-reference file | CARDXREF.VSAM.KSDS | _(SYSOUT display)_ |
| CBEXPORT.jcl | STEP01→STEP02 | IDCAMS→CBEXPORT | Export all data for branch migration | CUSTDATA, ACCTDATA, CARDXREF, TRANSACT, CARDDATA (all VSAM) | EXPORT.DATA |
| CBIMPORT.jcl | STEP01 | CBIMPORT | Import from export file | EXPORT.DATA | CUSTDATA.IMPORT, ACCTDATA.IMPORT, CARDXREF.IMPORT, TRANSACT.IMPORT |
| COMBTRAN.jcl | STEP05R→STEP10 | SORT→IDCAMS | Combine transaction backups with system trans | TRANSACT.BKUP(0), SYSTRAN(0) | TRANSACT.COMBINED(+1) → VSAM.KSDS |
| TRANBKP.jcl | STEP05R→STEP05→STEP10 | REPROC→IDCAMS | Backup transaction VSAM to GDG | TRANSACT.VSAM.KSDS | TRANSACT.BKUP(+1) |
| PRTCATBL.jcl | DELDEF→STEP05R→STEP10R | IEFBR14→REPROC→SORT | Print category balance report | TCATBALF.VSAM.KSDS | TCATBALF.REPT |
| WAITSTEP.jcl | WAIT | COBSWAIT | Introduce delay in batch scheduling | _(none)_ | _(none)_ |
| TXT2PDF1.JCL | TXT2PDF | IKJEFT1B (TXT2PDF REXX) | Convert statement text to PDF | STATEMNT.PS | _(PDF output)_ |
| FTPJCL.JCL | STEP1 | FTP | Transfer files via FTP | _(SYSIN directives)_ | _(remote)_ |
| INTRDRJ1.JCL | IDCAMS→STEP01 | IDCAMS→IEBGENER | Internal reader — submits INTRDRJ2 | FTP.TEST | FTP.TEST.BKUP; submits INTRDRJ2 |
| INTRDRJ2.JCL | IDCAMS | IDCAMS | Internal reader — secondary job | FTP.TEST.BKUP | FTP.TEST.BKUP.INTRDR |

### CICS Administration Jobs

| Job | Steps | Purpose |
|-----|-------|---------|
| CBADMCDJ.jcl | STEP1 (DFHCSDUP) | Install CICS CSD definitions (transactions, programs) |
| CLOSEFIL.jcl | CLCIFIL (SDSF) | Close CICS files for batch processing |
| OPENFIL.jcl | OPCIFIL (SDSF) | Open CICS files after batch completes |

### Sub-Application JCL (app/app-authorization-ims-db2-mq/jcl/)

| Job | Steps | Purpose |
|-----|-------|---------|
| CBPAUP0J.jcl | STEP01 (DFSRRC00→CBPAUP0C) | Run expired authorization purge under IMS |
| DBPAUTP0.jcl | STEPDEL→UNLOAD (DFSRRC00) | Unload IMS pending auth database via PSB |
| LOADPADB.JCL | STEP01 (DFSRRC00→PAUDBLOD) | Load IMS pending auth database from sequential |
| UNLDGSAM.JCL | STEP01 (DFSRRC00→DBUNLDGS) | Unload IMS database via GSAM |
| UNLDPADB.JCL | STEP0→STEP01 (DFSRRC00→PAUDBUNL) | Unload IMS database to flat files |

### Sub-Application JCL (app/app-transaction-type-db2/jcl/)

| Job | Steps | Purpose |
|-----|-------|---------|
| CREADB21.jcl | FREEPLN→CRCRDDB→BNDSTEP→LOADTYP→LOADCAT | Create DB2 tables, bind plans, load initial data |
| MNTTRDB2.jcl | LOADTYP→LOADCAT | Maintain/reload DB2 transaction type reference data |
| TRANEXTR.jcl | STEP01 (COBTUPDT) | Extract/update transaction types from file to DB2 |

---

## 4. Control-M Scheduling (app/scheduler/)

### Daily Cycle (DAILY-TransactionBackup folder)
```
CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
```

### Daily Cycle (DAILY-TransactionPosting folder)
```
CLOSEFIL → POSTTRAN → INTCALC → CREASTMT → TRANREPT → OPENFIL
```

### Dependencies
- All jobs within a folder execute sequentially via INCOND/OUTCOND conditions
- CLOSEFIL must complete before any batch job runs (ensures CICS files released)
- OPENFIL must execute last (restores CICS file access)
- WAITSTEP provides timing buffer between backup and file reopen
