# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Generated:** 2026-06-05 | **Scope:** `app/cbl/`, `app/app-authorization-ims-db2-mq/cbl/`, `app/app-transaction-type-db2/cbl/`, `app/app-vsam-mq/cbl/`, `app/jcl/`

## Summary

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Main programs (`app/cbl/`) | 27 |
| Sub-application programs | 17 |
| Total lines of COBOL | ~27,970 |
| Average LOC per program | 636 |
| Copybooks referenced | 47 |
| JCL jobs | 46 |
| BMS maps | 16 |

---

## 1. Main Programs — `app/cbl/`

### 1.1 Online (CICS) Programs

| # | Filename | PROGRAM-ID | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|-----|---------|--------------------|--------------------|
| 1 | COACTVWC.cbl | COACTVWC | 487 | Account View — displays account details for a selected account | CICS READ (ACCTDAT, CUSTDAT, CARDXREF), CICS SEND/RECEIVE MAP | COCOM01Y, COACTVW, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 2 | COACTUPC.cbl | COACTUPC | 4236 | Account Update — full account maintenance with field-level validation (date, SSN, phone, state, ZIP) | CICS READ/REWRITE (ACCTDAT, CUSTDAT, CARDXREF), CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (3× REPLACING), CSSTRPFY, CSUTLDPY |
| 3 | COADM01C.cbl | COADM01C | 288 | Admin Menu — hub screen routing to admin sub-programs (COUSR*, COTRTLIC, COTRTUPC) | CICS SEND/RECEIVE MAP, CICS XCTL (transfer control) | COCOM01Y, COADM01, COADM02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA |
| 4 | COBIL00C.cbl | COBIL00C | 669 | Bill Payment — processes bill payments against customer accounts | CICS READ/REWRITE (ACCTDAT, TRANSACT), CICS SEND/RECEIVE MAP | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 5 | COCRDLIC.cbl | COCRDLIC | 697 | Credit Card List — paginated browse of credit cards with STARTBR/READNEXT/READPREV | CICS STARTBR/READNEXT/READPREV/ENDBR (CARDDAT), CICS SEND/RECEIVE MAP | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT02Y, DFHAID, DFHBMSCA |
| 6 | COCRDSLC.cbl | COCRDSLC | 430 | Credit Card View — displays card details for a selected card number | CICS READ (CARDDAT, CARDAIX, ACCTDAT), CICS SEND/RECEIVE MAP | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT02Y, CVACT03Y, DFHAID, DFHBMSCA |
| 7 | COCRDUPC.cbl | COCRDUPC | 681 | Credit Card Update — updates card details (embossed name, status, expiry) | CICS READ/REWRITE (CARDDAT, ACCTDAT, CARDXREF), CICS SEND/RECEIVE MAP | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 8 | COMEN01C.cbl | COMEN01C | 370 | Main Menu — hub screen for user-level navigation; routes to account, card, transaction, report functions | CICS SEND/RECEIVE MAP, CICS INQUIRE, CICS XCTL | COCOM01Y, COMEN01, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA |
| 9 | CORPT00C.cbl | CORPT00C | 649 | Transaction Reports — submits batch report job via internal reader TDQ | CICS WRITEQ TD (JOBS), CICS SEND/RECEIVE MAP; CALLs CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COSGN00C.cbl | COSGN00C | 260 | Sign-On — authenticates users against USRSEC VSAM file; routes admin vs regular users | CICS READ (USRSEC), CICS SEND/RECEIVE MAP, CICS ASSIGN, CICS XCTL | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 11 | COTRN00C.cbl | COTRN00C | 699 | Transaction List — paginated browse of transaction records with forward/backward paging | CICS STARTBR/READNEXT/READPREV/ENDBR (TRANSACT), CICS SEND/RECEIVE MAP | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 12 | COTRN01C.cbl | COTRN01C | 330 | Transaction View — displays detail for a selected transaction | CICS READ (TRANSACT), CICS SEND/RECEIVE MAP | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | COTRN02C.cbl | COTRN02C | 783 | Transaction Add — adds new transactions with validation; auto-generates transaction ID | CICS READ (CARDXREF via AIX, TRANSACT), CICS WRITE (TRANSACT), CICS STARTBR/READPREV/ENDBR; CALLs CSUTLDTC | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | COUSR00C | 695 | User List — paginated browse of user security records | CICS STARTBR/READNEXT/READPREV/ENDBR (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | COUSR01C | 299 | User Add — adds new user records to USRSEC file | CICS WRITE (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | COUSR02C | 414 | User Update — modifies existing user records (name, password, type) | CICS READ/REWRITE (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | COUSR03C | 359 | User Delete — removes user records from USRSEC file with confirmation | CICS READ/DELETE (USRSEC), CICS SEND/RECEIVE MAP | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 1.2 Batch Programs

| # | Filename | PROGRAM-ID | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|-----|---------|--------------------|--------------------|
| 18 | CBACT01C.cbl | CBACT01C | 280 | Read Accounts — reads ACCTFILE VSAM and writes to flat file; CALLs COBDATFT (assembler date formatter) | OPEN/READ (ACCTFILE), WRITE (OUTFILE, ARRYFILE, VBRCFILE); CALL COBDATFT | CVACT01Y |
| 19 | CBACT02C.cbl | CBACT02C | 202 | Read Cards — reads card VSAM file sequentially and displays records | OPEN/READ (CARDFILE); CALL CEE3ABD (LE abnormal termination) | CVACT02Y |
| 20 | CBACT03C.cbl | CBACT03C | 206 | Read Cross-Ref — reads card-account cross-reference VSAM file | OPEN/READ (XREFFILE); CALL CEE3ABD | CVACT03Y |
| 21 | CBACT04C.cbl | CBACT04C | 620 | Interest Calculator — computes interest on account balances using disclosure group rates | OPEN/READ/REWRITE (ACCTFILE, DISCGRP, TCATBALF, XREFFILE) | CVACT01Y, CVACT02Y, CVACT03Y, CVTRA01Y, CVTRA02Y |
| 22 | CBCUS01C.cbl | CBCUS01C | 188 | Read Customers — reads CUSTFILE VSAM file sequentially | OPEN/READ (CUSTFILE); CALL CEE3ABD | CVCUS01Y |
| 23 | CBEXPORT.cbl | CBEXPORT | 627 | Data Export — exports customer, account, cross-ref data to sequential file for migration | OPEN/READ (CUSTFILE, ACCTFILE, XREFFILE), WRITE (EXPFILE) | CVCUS01Y, CVACT01Y, CVACT03Y, CVEXPORT |
| 24 | CBIMPORT.cbl | CBIMPORT | 442 | Data Import — imports records from export file, splits into customer/account/xref flat files | OPEN/READ (EXPFILE), WRITE (CUSTOUT, ACCTOUT, XREFOUT) | CVCUS01Y, CVACT01Y, CVACT03Y, CVEXPORT |
| 25 | CBSTM03A.CBL | CBSTM03A | 924 | Statement Generator (Part A) — generates customer statements from transaction data | OPEN/READ (TRANFILE, XREFFILE, ACCTFILE, CUSTFILE), WRITE (STMTFILE); highest I/O density in estate | CVTRA05Y, CVACT03Y, CVACT01Y, CVCUS01Y |
| 26 | CBSTM03B.CBL | CBSTM03B | 161 | Statement Generator (Part B) — writes statement page formatting and headers | WRITE (STMTFILE) — continuation of CBSTM03A output | CVTRA05Y |
| 27 | CBTRN01C.cbl | CBTRN01C | 237 | Read Transactions — reads TRANSACT VSAM file sequentially and displays records | OPEN/READ (TRANSACT) | CVTRA05Y |
| 28 | CBTRN02C.cbl | CBTRN02C | 731 | Post Transactions — posts daily transactions to master file; validates, rejects bad records | OPEN/READ (DALYTRAN, XREFFILE, ACCTFILE), WRITE (TRANSACT, DALYREJS), REWRITE (TCATBAL) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 29 | CBTRN03C.cbl | CBTRN03C | 565 | Transaction Report — generates daily transaction report with account subtotals | OPEN/READ (TRANSACT, XREFFILE, TRANTYPE, TRANCATG), WRITE (RPTFILE) | CVTRA05Y, CVTRA03Y, CVTRA04Y, CVACT03Y, CVTRA07Y |
| 30 | COBSWAIT.cbl | COBSWAIT | 21 | Wait Routine — assembler wait wrapper; CALLs MVSWAIT | CALL MVSWAIT | _(none)_ |
| 31 | CSUTLDTC.cbl | CSUTLDTC | 157 | Date Utility — date conversion and validation; CALLs LE CEEDAYS | CALL CEEDAYS | _(none)_ |

### 1.3 Utility / Shared

| # | Filename | PROGRAM-ID | LOC | Purpose | Classification |
|---|----------|-----------|-----|---------|---------------|
| 31 | CSUTLDTC.cbl | CSUTLDTC | 157 | Shared date validation utility called by COTRN02C, CORPT00C | Callable subroutine |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module — `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | PROGRAM-ID | LOC | Purpose | Type | Key I/O Operations | Copybooks |
|---|----------|-----------|-----|---------|------|--------------------|-----------|
| 32 | CBPAUP0C.cbl | CBPAUP0C | 386 | Delete Expired Pending Authorizations — purges expired IMS auth records | Batch IMS | DLI GN, GNP, DLET, CHKP | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | COPAUA0C | 1026 | Card Authorization Decision — processes auth requests via MQ, reads VSAM accounts/cards, writes IMS auth records | Online CICS/IMS/MQ | MQ OPEN/GET/PUT1/CLOSE, CICS READ (ACCTDAT, CARDDAT, CARDXREF), DLI SCHD/TERM/GU/REPL/ISRT | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 34 | COPAUS0C.cbl | COPAUS0C | 1032 | Authorization Summary View — displays paginated list of pending authorizations from IMS | Online CICS/IMS | CICS SEND/RECEIVE, CICS READ (ACCTDAT, CARDDAT, CARDXREF), DLI SCHD/TERM/GU/GNP, CICS SYNCPOINT | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | COPAUS1C | 604 | Authorization Detail View — shows detailed authorization message and allows updates | Online CICS/IMS | CICS SEND/RECEIVE, CICS LINK, DLI GU/GNP/REPL, CICS SYNCPOINT | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | COPAUS2C | 244 | Mark Authorization Fraud — flags authorization messages as fraudulent in DB2 | Online CICS/DB2 | EXEC SQL (INSERT/UPDATE fraud flags), CICS ASKTIME/FORMATTIME | CIPAUDTY |

### 2.2 Transaction Type Module — `app/app-transaction-type-db2/cbl/`

| # | Filename | PROGRAM-ID | LOC | Purpose | Type | Key I/O Operations | Copybooks |
|---|----------|-----------|-----|---------|------|--------------------|-----------|
| 37 | COBTUPDT.cbl | COBTUPDT | 237 | Transaction Type Update (Business Logic) — DB2 CRUD on transaction type table | Batch DB2 | EXEC SQL SELECT/INSERT/UPDATE/DELETE (DCLTRTYP) | _(inline SQL INCLUDE DCLTRTYP)_ |
| 38 | COTRTLIC.cbl | COTRTLIC | 2098 | Transaction Type List — paginated CICS screen with DB2 cursor-based paging | Online CICS/DB2 | EXEC SQL (DECLARE/OPEN/FETCH/CLOSE CURSOR, SELECT, DELETE, UPDATE), CICS SEND/RECEIVE MAP, CICS XCTL, CICS SYNCPOINT | CSDB2RWY _(SQL INCLUDE: SQLCA, DCLTRTYP)_ |
| 39 | COTRTUPC.cbl | COTRTUPC | 1702 | Transaction Type Update Screen — accepts user input for transaction type/category updates | Online CICS/DB2 | EXEC SQL (SELECT/INSERT/UPDATE/DELETE on DCLTRTYP, DCLTRCAT), CICS SEND/RECEIVE MAP, CICS HANDLE ABEND, CICS SYNCPOINT | _(SQL INCLUDE: DCLTRTYP, DCLTRCAT)_ |

### 2.3 VSAM-MQ Module — `app/app-vsam-mq/cbl/`

| # | Filename | PROGRAM-ID | LOC | Purpose | Type | Key I/O Operations | Copybooks |
|---|----------|-----------|-----|---------|------|--------------------|-----------|
| 40 | COACCT01.cbl | COACCT01 | 620 | Account Inquiry via MQ — reads account data from VSAM in response to MQ requests | Online CICS/MQ | CICS RETRIEVE, CICS READ (ACCTDAT), MQ operations | CVACT01Y |
| 41 | CODATE01.cbl | CODATE01 | 524 | Date Service via MQ — processes date conversion/validation requests via MQ | Online CICS/MQ | CICS RETRIEVE, CICS ASKTIME/FORMATTIME, MQ operations | _(none identified)_ |

---

## 3. JCL Job Catalog — `app/jcl/`

### 3.1 Data Setup / File Definition Jobs

| # | Job Name | JCL File | Purpose | Steps | Key Datasets |
|---|----------|----------|---------|-------|-------------|
| 1 | ACCTFILE | ACCTFILE.jcl | Delete/define/load Account VSAM file | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO) | ACCTDATA.VSAM.KSDS, ACCTDATA.PS |
| 2 | CARDFILE | CARDFILE.jcl | Delete/define/load Card VSAM file with AIX | CLCIFIL (SDSF close), STEP05-STEP60 (IDCAMS), OPCIFIL (SDSF open) | CARDDATA.VSAM.KSDS, CARDDATA.VSAM.AIX |
| 3 | CUSTFILE | CUSTFILE.jcl | Define/load Customer VSAM file | CLCIFIL, STEP05-STEP15 (IDCAMS), OPCIFIL | CUSTDATA.VSAM.KSDS, CUSTDATA.PS |
| 4 | DEFCUST | DEFCUST.jcl | Define Customer Data file (alternate) | STEP05 (IDCAMS ×2) | CUSTDATA |
| 5 | DISCGRP | DISCGRP.jcl | Define Disclosure Group VSAM file | STEP05-STEP15 (IDCAMS) | DISCGRP.VSAM.KSDS, DISCGRP.PS |
| 6 | TRANFILE | TRANFILE.jcl | Define Transaction Master VSAM with daily tran init | CLCIFIL, STEP05-STEP30 (IDCAMS), OPCIFIL | TRANSACT.VSAM.KSDS, DALYTRAN.PS.INIT |
| 7 | TRANTYPE | TRANTYPE.jcl | Define Transaction Type VSAM file | STEP05-STEP15 (IDCAMS) | TRANTYPE.VSAM.KSDS, TRANTYPE.PS |
| 8 | TRANCATG | TRANCATG.jcl | Define Transaction Category VSAM file | STEP05-STEP15 (IDCAMS) | TRANCATG.VSAM.KSDS, TRANCATG.PS |
| 9 | TCATBALF | TCATBALF.jcl | Define Transaction Category Balance VSAM file | STEP05-STEP15 (IDCAMS) | TCATBALF.VSAM.KSDS, TCATBALF.PS |
| 10 | TRANIDX | TRANIDX.jcl | Define AIX on Transaction Master | STEP20-STEP30 (IDCAMS) | TRANSACT AIX/PATH |
| 11 | DUSRSECJ | DUSRSECJ.jcl | Define User Security VSAM file | PREDEL (IEFBR14), STEP01 (IEBGENER), STEP02-03 (IDCAMS) | USRSEC.VSAM.KSDS, USRSEC.PS |
| 12 | ESDSRRDS | ESDSRRDS.jcl | Define ESDS/RRDS demonstration files | PREDEL, STEP01-STEP05 | ESDSRRDS.PS, USRSEC.VSAM.ESDS |

### 3.2 GDG / Infrastructure Jobs

| # | Job Name | JCL File | Purpose | Steps |
|---|----------|----------|---------|-------|
| 13 | DEFGDGB | DEFGDGB.jcl | Define GDG base entries for backups | STEP05 (IDCAMS) |
| 14 | DEFGDGD | DEFGDGD.jcl | Define DB2-related GDG and backup transaction types | STEP10-STEP60 (IDCAMS, IEBGENER alternating) |
| 15 | DALYREJS | DALYREJS.jcl | Define GDG for daily rejection file | STEP05 (IDCAMS) |
| 16 | REPTFILE | REPTFILE.jcl | Define GDG for report output files | STEP05 (IDCAMS) |

### 3.3 Batch Processing Jobs

| # | Job Name | JCL File | Purpose | Steps | Program Executed |
|---|----------|----------|---------|-------|-----------------|
| 17 | POSTTRAN | POSTTRAN.jcl | Post daily transactions to master | STEP15 (PGM=CBTRN02C) | CBTRN02C |
| 18 | INTCALC | INTCALC.jcl | Calculate interest on account balances | STEP15 (PGM=CBACT04C, PARM='2022071800') | CBACT04C |
| 19 | TRANREPT | TRANREPT.jcl | Generate daily transaction report | STEP05R (SORT), STEP10R (PGM=CBTRN03C) | CBTRN03C |
| 20 | COMBTRAN | COMBTRAN.jcl | Combine/merge transaction backups | STEP05R (SORT), STEP10 (IDCAMS REPRO) | SORT, IDCAMS |
| 21 | TRANBKP | TRANBKP.jcl | Backup and purge transaction master | STEP05R (REPROC), STEP05-STEP10 (IDCAMS) | IDCAMS |
| 22 | PRTCATBL | PRTCATBL.jcl | Print transaction category balance report | DELDEF (IEFBR14), STEP05R (REPROC), STEP10R (SORT) | SORT |

### 3.4 File Reader / Diagnostic Jobs

| # | Job Name | JCL File | Purpose | Steps | Program Executed |
|---|----------|----------|---------|-------|-----------------|
| 23 | READACCT | READACCT.jcl | Read and dump Account VSAM file | PREDEL (IEFBR14), STEP05 (PGM=CBACT01C) | CBACT01C |
| 24 | READCARD | READCARD.jcl | Read and dump Card VSAM file | STEP05 (PGM=CBACT02C) | CBACT02C |
| 25 | READCUST | READCUST.jcl | Read and dump Customer VSAM file | STEP05 (PGM=CBCUS01C) | CBCUS01C |
| 26 | READXREF | READXREF.jcl | Read and dump Cross-Reference file | STEP05 (PGM=CBACT03C) | CBACT03C |

### 3.5 Data Migration Jobs

| # | Job Name | JCL File | Purpose | Steps | Program Executed |
|---|----------|----------|---------|-------|-----------------|
| 27 | CBEXPORT | CBEXPORT.jcl | Export customer/account/xref to sequential file | STEP01 (IDCAMS), STEP02 (PGM=CBEXPORT) | CBEXPORT |
| 28 | CBIMPORT | CBIMPORT.jcl | Import from export file to flat files | STEP01 (PGM=CBIMPORT) | CBIMPORT |

### 3.6 CICS File Management Jobs

| # | Job Name | JCL File | Purpose | Steps |
|---|----------|----------|---------|-------|
| 29 | CLOSEFIL | CLOSEFIL.jcl | Close all files in CICS region | CLCIFIL (SDSF — CEMT SET FIL CLO) |
| 30 | OPENFIL | OPENFIL.jcl | Open all files in CICS region | OPCIFIL (SDSF — CEMT SET FIL OPE) |
| 31 | WAITSTEP | WAITSTEP.jcl | Wait step utility (PGM=COBSWAIT) | WAIT (PGM=COBSWAIT) |

### 3.7 CICS Resource Definition

| # | Job Name | JCL File | Purpose | Steps |
|---|----------|----------|---------|-------|
| 32 | CBADMCDJ | CBADMCDJ.jcl | CSD resource definitions for CardDemo CICS region | STEP1 (PGM=DFHCSDUP) |

### 3.8 Sub-Application JCL — Authorization IMS (`app/app-authorization-ims-db2-mq/jcl/`)

| # | Job Name | JCL File | Purpose | Steps |
|---|----------|----------|---------|-------|
| 33 | CBPAUP0J | CBPAUP0J.jcl | Run IMS batch purge of expired authorizations | STEP01 (PGM=DFSRRC00 → CBPAUP0C) |
| 34 | DBPAUTP0 | DBPAUTP0.jcl | Unload IMS auth database for backup | STEPDEL (IEFBR14), UNLOAD (PGM=DFSRRC00) |
| 35 | LOADPADB | LOADPADB.JCL | Load IMS pending auth database from backup | STEP01 (PGM=DFSRRC00) |
| 36 | UNLDPADB | UNLDPADB.JCL | Unload IMS pending auth database | STEP0 (IEFBR14), STEP01 (PGM=DFSRRC00) |
| 37 | UNLDGSAM | UNLDGSAM.JCL | Unload IMS auth database to GSAM | STEP01 (PGM=DFSRRC00) |

### 3.9 Sub-Application JCL — Transaction Type DB2 (`app/app-transaction-type-db2/jcl/`)

| # | Job Name | JCL File | Purpose | Steps |
|---|----------|----------|---------|-------|
| 38 | CREADB21 | CREADB21.jcl | Create DB2 tables for transaction types/categories and load initial data | FREEPLN, CRCRDDB, LDTTYPE, RUNTEP2, LDTCCAT (IKJEFT01, IEFBR14) |
| 39 | MNTTRDB2 | MNTTRDB2.jcl | Maintain/bind DB2 transaction type programs | STEP1 (PGM=IKJEFT01) |
| 40 | TRANEXTR | TRANEXTR.jcl | Extract and backup transaction type/category data from DB2 | STEP10-STEP50 (IEBGENER, IEFBR14, IKJEFT01) |

---

## 4. VSAM File Inventory

| Dataset Name | Record Format | Key Len | Rec Len | JCL Setup | Programs Accessing |
|-------------|--------------|---------|---------|-----------|-------------------|
| ACCTDATA.VSAM.KSDS | KSDS | 11 | 300 | ACCTFILE.jcl | CBACT01C, CBACT04C, CBSTM03A, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C, CBEXPORT |
| CARDDATA.VSAM.KSDS | KSDS | 16 | 150 | CARDFILE.jcl | CBACT02C, COCRDLIC, COCRDSLC, COCRDUPC, COPAUA0C |
| CARDXREF.VSAM.KSDS | KSDS | 16 | 50 | _(CARDFILE.jcl AIX)_ | CBACT04C, CBSTM03A, CBTRN02C, COTRN02C, COPAUA0C, COPAUS0C, CBEXPORT |
| CUSTDATA.VSAM.KSDS | KSDS | 9 | 500 | CUSTFILE.jcl | CBCUS01C, CBSTM03A, COACTUPC, COACTVWC, COCRDUPC, COPAUS0C, CBEXPORT |
| TRANSACT.VSAM.KSDS | KSDS | 16 | 350 | TRANFILE.jcl | CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, COTRN00C, COTRN01C, COTRN02C, COBIL00C |
| DALYTRAN.PS | Sequential | — | 350 | TRANFILE.jcl | CBTRN02C (input) |
| USRSEC.VSAM.KSDS | KSDS | 8 | 80 | DUSRSECJ.jcl | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |
| TCATBALF.VSAM.KSDS | KSDS | 17 | 50 | TCATBALF.jcl | CBACT04C, CBTRN02C |
| DISCGRP.VSAM.KSDS | KSDS | 16 | 50 | DISCGRP.jcl | CBACT04C |
| TRANTYPE.VSAM.KSDS | KSDS | 2 | 60 | TRANTYPE.jcl | CBTRN03C |
| TRANCATG.VSAM.KSDS | KSDS | 6 | 60 | TRANCATG.jcl | CBTRN03C |
