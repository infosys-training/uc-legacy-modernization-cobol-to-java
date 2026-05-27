# APPLICATION INVENTORY — CardDemo COBOL Estate

> Auto-generated analysis of the AWS CardDemo mainframe credit-card management application.

---

## 1. COBOL Programs — Core (`app/cbl/`)

### 1.1 Batch Programs

| # | Filename | Purpose | Lines | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|-------|---------------------|----------------------|
| 1 | **CBACT01C.cbl** | Read Account VSAM file and write to flat-file / array / VBR formats | 430 | READ ACCTFILE; WRITE OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | **CBACT02C.cbl** | Read and print Card data file | 178 | READ CARDFILE | CVACT02Y |
| 3 | **CBACT03C.cbl** | Read and print Account Cross-Reference data file | 178 | READ XREFFILE | CVACT03Y |
| 4 | **CBACT04C.cbl** | Interest calculator — computes interest on accounts using category balances and discount groups | 652 | READ TCATBALF, XREFFILE, DISCGRP; REWRITE ACCTFILE; WRITE TRANSACT | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y |
| 5 | **CBCUS01C.cbl** | Read and print Customer data file | 178 | READ CUSTFILE | CVCUS01Y |
| 6 | **CBEXPORT.cbl** | Export all customer/account/card/transaction data for branch migration | 582 | READ CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; WRITE EXPFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 7 | **CBIMPORT.cbl** | Import customer data from branch migration export file | 487 | READ EXPFILE; WRITE CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 8 | **CBTRN01C.cbl** | Post records from daily transaction file to master | 494 | READ DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE; WRITE TRANFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVTRA06Y |
| 9 | **CBTRN02C.cbl** | Post records from daily transaction file (variant with reject handling) | 731 | READ DALYTRAN, TRANFILE, XREFFILE, ACCTFILE; WRITE DALYREJS, TCATBALF | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y |
| 10 | **CBTRN03C.cbl** | Print transaction detail report | 649 | READ TRANFILE, CARDXREF, TRANTYPE, TRANCATG; WRITE TRANREPT; READ DATEPARM | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y |
| 11 | **CBSTM03A.CBL** | Print account statements from transaction data (main driver) | 924 | READ STMTFILE, HTMLFILE via CBSTM03B; TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE | COSTM01, CUSTREC, CVACT01Y, CVACT03Y |
| 12 | **CBSTM03B.CBL** | File-processing subroutine for statement report (called by CBSTM03A) | 230 | READ/WRITE TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | _(none in app/cpy/)_ |
| 13 | **COBSWAIT.cbl** | Utility — wait for specified centiseconds (parm-driven delay) | 41 | _(none)_ | _(none)_ |
| 14 | **CSUTLDTC.cbl** | Date validation utility — validate and convert CCYYMMDD dates | 157 | _(none)_ | _(none)_ |

### 1.2 Online (CICS) Programs

| # | Filename | Purpose | Lines | CICS Ops | Key I/O (VSAM via CICS) | Copybooks Referenced |
|---|----------|---------|-------|----------|-------------------------|----------------------|
| 1 | **COSGN00C.cbl** | Sign-on screen for CardDemo application | 260 | 10 | READ USRSEC | COCOM01Y, COSGN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | **COADM01C.cbl** | Admin menu — displays admin options | 288 | 7 | _(menu only)_ | COCOM01Y, COADM02Y, COADM01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | **COMEN01C.cbl** | Main menu for regular users | 308 | 7 | _(menu only)_ | COCOM01Y, COMEN02Y, COMEN01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | **COACTVWC.cbl** | View account details — read account, card, customer, cross-ref | 941 | 15 | READ ACCTDAT, CARDDAT, CARDAIX, CUSTDAT | COCOM01Y, COACTVW (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 5 | **COACTUPC.cbl** | Account update — accept and process account modifications | 4236 | 17 | READ/REWRITE ACCTDAT, CARDDAT, CUSTDAT | COCOM01Y, COACTUP (BMS), COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CSUTLDPY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 6 | **COCRDLIC.cbl** | List credit cards — browse card VSAM with filter by account | 1459 | 18 | STARTBR/READNEXT/READPREV/ENDBR CARDDAT | COCOM01Y, COCRDLI (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 7 | **COCRDSLC.cbl** | View credit card detail | 887 | 14 | READ CARDDAT, CARDAIX | COCOM01Y, COCRDSL (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 8 | **COCRDUPC.cbl** | Update credit card detail | 1560 | 12 | READ/REWRITE CARDDAT | COCOM01Y, COCRDUP (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 9 | **COTRN00C.cbl** | List transactions from TRANSACT file | 699 | 10 | STARTBR/READNEXT/READPREV/ENDBR TRANSACT | COCOM01Y, COTRN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | **COTRN01C.cbl** | View a single transaction from TRANSACT file | 330 | 5 | READ TRANSACT | COCOM01Y, COTRN01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | **COTRN02C.cbl** | Add a new transaction to TRANSACT file | 783 | 11 | READ ACCTDAT, XREFDAT; WRITE TRANSACT | COCOM01Y, COTRN02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 12 | **CORPT00C.cbl** | Print transaction reports by submitting batch job | 649 | 7 | _(submits JCL)_ | COCOM01Y, CORPT00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | **COBIL00C.cbl** | Bill payment — pay account balance in full or partial | 572 | 13 | READ/REWRITE ACCTDAT; WRITE TRANSACT | COCOM01Y, COBIL00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | **COUSR00C.cbl** | List all users from USRSEC file | 695 | 11 | STARTBR/READNEXT/READPREV/ENDBR USRSEC | COCOM01Y, COUSR00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | **COUSR01C.cbl** | Add a new user to USRSEC file | 299 | 5 | WRITE USRSEC | COCOM01Y, COUSR01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | **COUSR02C.cbl** | Update a user in USRSEC file | 414 | 6 | READ/REWRITE USRSEC | COCOM01Y, COUSR02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | **COUSR03C.cbl** | Delete a user from USRSEC file | 359 | 6 | DELETE USRSEC | COCOM01Y, COUSR03 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. COBOL Programs — Sub-Applications

### 2.1 Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | Classification | Purpose | Lines | Key I/O | Copybooks |
|---|----------|----------------|---------|-------|---------|-----------|
| 1 | **COPAUA0C.cbl** | Online (CICS/MQ) | Card authorization decision — reads MQ request, validates card/account, sends MQ reply | 1026 | MQGET/MQPUT1 (MQ); READ ACCTDAT, CARDDAT, CUSTDAT (CICS) | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, CMQGMOV, CMQMDV, CMQODV, CMQPMOV, CMQTML, CMQV, CVACT01Y, CVACT03Y, CVCUS01Y |
| 2 | **COPAUS0C.cbl** | Online (CICS/BMS) | Summary view of pending authorization messages (IMS DB) | 1032 | IMS DL/I calls via CICS | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 3 | **COPAUS1C.cbl** | Online (CICS/BMS) | Detail view of a single authorization message | 604 | EXEC CICS LINK | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA |
| 4 | **COPAUS2C.cbl** | Online (CICS/DB2) | Mark authorization message as fraud (DB2 UPDATE) | 244 | EXEC SQL UPDATE/SELECT (DB2) | CIPAUDTY |
| 5 | **CBPAUP0C.cbl** | Batch (IMS) | Delete expired pending authorization messages | 386 | IMS DL/I DLET | CIPAUDTY, CIPAUSMY |
| 6 | **DBUNLDGS.CBL** | Batch (IMS) | Unload IMS authorization DB to GSAM sequential files | 366 | DL/I GN/GNP; WRITE OUTFIL1, OUTFIL2 | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB |
| 7 | **PAUDBLOD.CBL** | Batch (IMS) | Load IMS authorization DB from sequential files | 369 | READ INFILE1, INFILE2; DL/I ISRT | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |
| 8 | **PAUDBUNL.CBL** | Batch (IMS) | Unload IMS authorization DB to sequential files | 317 | DL/I GN/GNP; WRITE OUTFIL1, OUTFIL2 | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |

### 2.2 Transaction Type (DB2) — `app/app-transaction-type-db2/cbl/`

| # | Filename | Classification | Purpose | Lines | Key I/O | Copybooks |
|---|----------|----------------|---------|-------|---------|-----------|
| 1 | **COTRTLIC.cbl** | Online (CICS/DB2) | List transaction types for updates/deletes (DB2 cursor browse) | 2098 | EXEC SQL SELECT/FETCH/CLOSE (DB2) | COCOM01Y, COTRTLI (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 2 | **COTRTUPC.cbl** | Online (CICS/DB2) | Accept and process transaction type updates (DB2 INSERT/UPDATE/DELETE) | 1702 | EXEC SQL INSERT/UPDATE/DELETE/SELECT (DB2) | COCOM01Y, COTRTUP (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 3 | **COBTUPDT.cbl** | Batch (DB2) | Update transaction type table from flat file input | 237 | READ INPFILE; EXEC SQL INSERT/UPDATE/DELETE (DB2) | _(none in app/cpy/)_ |

### 2.3 VSAM/MQ — `app/app-vsam-mq/cbl/`

| # | Filename | Classification | Purpose | Lines | Key I/O | Copybooks |
|---|----------|----------------|---------|-------|---------|-----------|
| 1 | **COACCT01.cbl** | Online (CICS/MQ) | Account inquiry via MQ — receives MQ request, reads VSAM account, sends MQ response | 620 | MQGET/MQPUT (MQ); EXEC CICS READ ACCTDAT | CVACT01Y, CMQGMOV, CMQMDV, CMQODV, CMQPMOV, CMQTML, CMQV |
| 2 | **CODATE01.cbl** | Online (CICS/MQ) | Date service via MQ — receives MQ request, processes date operations, sends MQ response | 524 | MQGET/MQPUT (MQ) | CMQGMOV, CMQMDV, CMQODV, CMQPMOV, CMQTML, CMQV |

---

## 3. Program Totals

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Batch programs | 19 |
| Online (CICS) programs | 19 |
| Online (CICS/MQ) programs | 3 |
| Online (CICS/DB2) programs | 3 |
| Total lines of code | 30,175 |

---

## 4. JCL Job Catalog (`app/jcl/`)

### 4.1 VSAM File Definition Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **ACCTFILE.jcl** | Delete/define/load Account VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO (PS→VSAM) |
| **CARDFILE.jcl** | Delete/define/load Card data VSAM KSDS + AIX | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE KSDS+AIX → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP40: DEFINE AIX → STEP50: DEFINE PATH |
| **CUSTFILE.jcl** | Delete/define/load Customer VSAM KSDS | CLCIFIL: SDSF close CICS → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → OPCIFIL: SDSF reopen |
| **XREFFILE.jcl** | Delete/define/load Card Cross-Reference VSAM KSDS + AIX | STEP05: DELETE KSDS+AIX → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX |
| **TRANFILE.jcl** | Delete/define/load Transaction Master VSAM KSDS + AIX | CLCIFIL: close CICS → STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO → STEP20: DEFINE AIX → STEP25: DEFINE PATH |
| **TRANTYPE.jcl** | Delete/define/load Transaction Type VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| **TRANCATG.jcl** | Delete/define/load Transaction Category VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| **TCATBALF.jcl** | Delete/define/load Transaction Category Balance VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| **DISCGRP.jcl** | Delete/define/load Disclosure Group VSAM KSDS | STEP05: DELETE → STEP10: DEFINE → STEP15: REPRO |
| **DUSRSECJ.jcl** | Define User Security VSAM KSDS (incl. seed data) | PREDEL: IEFBR14 → STEP01: IEBGENER seed → STEP02: IDCAMS DEFINE → STEP03: REPRO |
| **ESDSRRDS.jcl** | Define ESDS and RRDS variants of User Security file | PREDEL → STEP01: seed → STEP02: DEFINE ESDS → STEP03: REPRO → STEP04: DEFINE RRDS → STEP05: REPRO |
| **DEFCUST.jcl** | Alternative Customer file definition | STEP05: DELETE → STEP05: DEFINE CLUSTER |
| **TRANIDX.jcl** | Define AIX on Transaction Master | STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX |

### 4.2 Batch Processing Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **READACCT.jcl** | Read Account file → write to PS/ARRY/VB formats | PREDEL: IEFBR14 → STEP05: **CBACT01C** |
| **READCARD.jcl** | Read and print Card data | STEP05: **CBACT02C** |
| **READCUST.jcl** | Read and print Customer data | STEP05: **CBCUS01C** |
| **READXREF.jcl** | Read and print Cross-Reference data | STEP05: **CBACT03C** |
| **INTCALC.jcl** | Run interest calculator | STEP15: **CBACT04C** (PARM=date) |
| **POSTTRAN.jcl** | Post daily transactions | STEP15: **CBTRN02C** |
| **TRANREPT.jcl** | Generate transaction detail report | STEP05R: SORT → STEP10R: **CBTRN03C** |
| **CREASTMT.JCL** | Create account statements (text + HTML) | DELDEF01: IDCAMS → STEP010: SORT → STEP020: REPRO → STEP030: IEFBR14 delete old → STEP040: **CBSTM03A** |
| **CBEXPORT.jcl** | Export all data for branch migration | STEP01: IDCAMS DEFINE export file → STEP02: **CBEXPORT** |
| **CBIMPORT.jcl** | Import branch migration data | STEP01: **CBIMPORT** |
| **COMBTRAN.jcl** | Combine/merge transaction files | STEP05R: SORT merge → STEP10: REPRO into VSAM |
| **TRANBKP.jcl** | Backup + recreate Transaction Master | STEP05R: REPRO backup → STEP05: DELETE → STEP10: DEFINE |
| **PRTCATBL.jcl** | Print Transaction Category Balance file | DELDEF: IEFBR14 → STEP05R: REPRO → STEP10R: SORT |
| **WAITSTEP.jcl** | Utility wait job | WAIT: **COBSWAIT** |

### 4.3 GDG Definition Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **DEFGDGB.jcl** | Define GDG bases for daily transaction, reject, report, statement files | STEP05: IDCAMS DEFINE GDG (×6) |
| **DEFGDGD.jcl** | Define GDG bases for DB2 transaction type/category backups | STEP10–STEP60: DEFINE GDG + IEBGENER backup |
| **DALYREJS.jcl** | Define GDG for daily reject file | STEP05: DEFINE GDG |
| **REPTFILE.jcl** | Define GDG for report output | STEP05: DEFINE GDG |

### 4.4 CICS Admin Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **CBADMCDJ.jcl** | Define CICS CSD resources (mapsets, programs, transactions, files) | STEP1: DFHCSDUP DEFINE |
| **CLOSEFIL.jcl** | Close CICS files via SDSF/CEMT | CLCIFIL: SDSF |
| **OPENFIL.jcl** | Open CICS files via SDSF/CEMT | OPCIFIL: SDSF |

### 4.5 Utility/Misc Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **FTPJCL.JCL** | FTP file transfer utility | STEP1: FTP |
| **INTRDRJ1.JCL** | Internal reader job — REPRO + submit INTRDRJ2 | IDCAMS: REPRO → STEP01: IEBGENER to INTRDR |
| **INTRDRJ2.JCL** | Internal reader job — secondary REPRO | IDCAMS: REPRO |
| **TXT2PDF1.JCL** | Convert text statement to PDF | TXT2PDF: IKJEFT1B + TXT2PDF REXX |

### 4.6 Sub-Application JCL

#### Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/jcl/`

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **CBPAUP0J.jcl** | Execute IMS program to delete expired authorizations | STEP01: DFSRRC00 → **CBPAUP0C** (IMS BMP) |
| **DBPAUTP0.jcl** | Unload IMS authorization DB | STEPDEL: IEFBR14 delete → UNLOAD: DFSRRC00 |
| **LOADPADB.JCL** | Load IMS authorization DB from sequential files | STEP01: DFSRRC00 → **PAUDBLOD** |
| **UNLDGSAM.JCL** | Unload IMS authorization DB to GSAM | STEP01: DFSRRC00 → **DBUNLDGS** |
| **UNLDPADB.JCL** | Unload IMS authorization DB to sequential files | STEP0: IEFBR14 → STEP01: DFSRRC00 → **PAUDBUNL** |

#### Transaction Type (DB2) — `app/app-transaction-type-db2/jcl/`

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **CREADB21.jcl** | Create DB2 tables, bind plans, load data for tran-type/category | FREEPLN: IKJEFT01 → CRCRDDB: IKJEFT01 create → LDTTYPE: IEFBR14 → RUNTEP2: IKJEFT01 load type → LDTCCAT: IKJEFT01 load category |
| **MNTTRDB2.jcl** | Maintain transaction types via DB2 (batch update from file) | STEP1: IKJEFT01 → **COBTUPDT** |
| **TRANEXTR.jcl** | Extract/backup DB2 transaction type + category data | STEP10–STEP20: IEBGENER backup → STEP30–STEP50: DB2 UNLOAD |

---

## 5. VSAM Dataset Summary

| DD Name | Dataset Name | Key | Record Size | Type | Used By |
|---------|-------------|-----|-------------|------|---------|
| ACCTFILE | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | 11 bytes @ 0 | 300 | KSDS | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBSTM03A/B, COACTUPC, COACTVWC, COBIL00C, COTRN02C |
| CARDFILE | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | 16 bytes @ 0 | 150 | KSDS+AIX | CBACT02C, CBEXPORT, CBTRN01C, COCRDLIC, COCRDSLC, COCRDUPC, COACTVWC |
| CUSTFILE | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | 9 bytes @ 0 | 500 | KSDS | CBCUS01C, CBEXPORT, CBTRN01C, CBSTM03A/B, COACTUPC, COACTVWC, COCRDSLC |
| XREFFILE | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | 16 bytes @ 0 | 50 | KSDS+AIX | CBACT03C, CBACT04C, CBEXPORT, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A/B, COACTVWC |
| TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | 16 bytes @ 0 | 350 | KSDS+AIX | CBACT04C, CBEXPORT, CBTRN03C, COTRN00C, COTRN01C, COTRN02C, COBIL00C |
| USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | 8 bytes @ 0 | 80 | KSDS | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| TCATBALF | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | — | — | KSDS | CBACT04C, CBTRN02C |
| TRANTYPE | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | — | 60 | KSDS | CBTRN03C, COTRTLIC, COTRTUPC |
| TRANCATG | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | — | 60 | KSDS | CBTRN03C |
| DISCGRP | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | — | 50 | KSDS | CBACT04C |

---

*Generated: 2026-05-27 | Source: `uc-legacy-modernization-cobol-to-java`*
