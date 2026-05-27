# APPLICATION INVENTORY

## Overview

The CardDemo application is a multi-tier mainframe credit card management system built on COBOL, CICS, VSAM, DB2, IMS, and MQ. It comprises **44 COBOL programs**, **30 copybooks**, and **46 JCL jobs** organized across a main application and three sub-applications.

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

### 1.1 Batch Programs (CB* prefix)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read account file and write into multiple output formats (fixed, array, variable-length) | Batch | **Read:** ACCTFILE (account VSAM); **Write:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and print card data file | Batch | **Read:** CARDFILE | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and print account cross-reference data file | Batch | **Read:** XREFFILE | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Interest calculator — compute interest and fees on account balances | Batch | **Read:** TCATBALF, XREFFILE, DISCGRP, ACCTFILE; **Read/Write:** TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and print customer data file | Batch | **Read:** CUSTFILE | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export customer data for branch migration — writes multi-record sequential export file | Batch | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; **Write:** EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export file | Batch | **Read:** EXPFILE; **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBTRN01C.cbl | CBTRN01C | Post records from daily transaction file — validate and write to master | Batch | **Read:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE; **Write:** TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | CBTRN02C.cbl | CBTRN02C | Post daily transactions with validation; write rejects and update category balances | Batch | **Read:** DALYTRAN, XREFFILE, ACCTFILE; **Write:** TRANFILE, DALYREJS, TCATBALF | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | CBTRN03C | Print transaction detail report with type/category lookups | Batch | **Read:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; **Write:** TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | CBSTM03A.CBL | CBSTM03A | Print account statements from transaction data | Batch | **Read:** TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE; **Write:** STESSION | COSTM01, CVTRA05Y, CVACT03Y, CVACT01Y |
| 12 | CBSTM03B.CBL | CBSTM03B | File processing subroutine for statement report (called by CBSTM03A) | Batch (subprogram) | **Write:** Statement output file | (none — uses passed area) |
| 13 | COBSWAIT.cbl | COBSWAIT | Utility program to wait (parameter in centiseconds) | Batch (utility) | None | (none) |
| 14 | CSUTLDTC.cbl | CSUTLDTC | Date/time utility — converts date formats using CEEDAYS | Batch (utility) | None | (none) |

### 1.2 Online Programs (CO* prefix — CICS)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|-------------------|---------------------|
| 15 | COSGN00C.cbl | COSGN00C | Sign-on screen — authenticates users, routes to Admin or User menu | Online (CICS) | **VSAM Read:** USRSEC file | COCOM01Y, COSGN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COADM01C.cbl | COADM01C | Admin menu — displays options for admin users | Online (CICS) | Screen I/O only | COCOM01Y, COADM02Y, COADM01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COMEN01C.cbl | COMEN01C | Main menu for regular users — dispatches to sub-programs | Online (CICS) | Screen I/O only | COCOM01Y, COMEN02Y, COMEN01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | COACTVWC.cbl | COACTVWC | Account View — displays account details with customer/card info | Online (CICS) | **VSAM Read:** ACCTDAT, CARDXREF, CUSTDAT files | CVCRD01Y, COCOM01Y, COACTVW (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 19 | COACTUPC.cbl | COACTUPC | Account Update — accept and process account modifications | Online (CICS) | **VSAM Read/Write:** ACCTDAT, CARDXREF, CUSTDAT files | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP (BMS), CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY, CSSTRPFY, CSUTLDPY |
| 20 | COCRDLIC.cbl | COCRDLIC | Credit Card List — browse/paginate credit cards | Online (CICS) | **VSAM Read:** CARDDAT, CARDAIX files (STARTBR/READNEXT) | CVCRD01Y, COCOM01Y, COCRDLI (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 21 | COCRDSLC.cbl | COCRDSLC | Credit Card View Detail — display individual card information | Online (CICS) | **VSAM Read:** CARDDAT, CARDXREF, ACCTDAT, CUSTDAT files | CVCRD01Y, COCOM01Y, COCRDSL (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 22 | COCRDUPC.cbl | COCRDUPC | Credit Card Update — modify card details | Online (CICS) | **VSAM Read/Write:** CARDDAT, CARDXREF, ACCTDAT, CUSTDAT files | CVCRD01Y, COCOM01Y, COCRDUP (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 23 | COTRN00C.cbl | COTRN00C | Transaction List — browse transactions from TRANSACT file | Online (CICS) | **VSAM Read:** TRANSACT file (STARTBR/READNEXT) | COCOM01Y, COTRN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | COTRN01C.cbl | COTRN01C | Transaction View — display individual transaction details | Online (CICS) | **VSAM Read:** TRANSACT file | COCOM01Y, COTRN01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN02C.cbl | COTRN02C | Transaction Add — enter new transaction into TRANSACT file | Online (CICS) | **VSAM Read:** CARDXREF, ACCTDAT; **VSAM Write:** TRANSACT | COCOM01Y, COTRN02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 26 | CORPT00C.cbl | CORPT00C | Transaction Reports — submit batch report jobs from CICS | Online (CICS) | **VSAM Read:** TRANSACT; **TDQ Write:** submits batch JCL | COCOM01Y, CORPT00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | COBIL00C.cbl | COBIL00C | Bill Payment — pay account balance in full or partial amount | Online (CICS) | **VSAM Read/Write:** ACCTDAT, TRANSACT, CARDXREF | COCOM01Y, COBIL00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 28 | COUSR00C.cbl | COUSR00C | User List — display all users from USRSEC file | Online (CICS) | **VSAM Read:** USRSEC (STARTBR/READNEXT) | COCOM01Y, COUSR00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR01C.cbl | COUSR01C | User Add — create new Regular/Admin user in USRSEC file | Online (CICS) | **VSAM Write:** USRSEC | COCOM01Y, COUSR01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR02C.cbl | COUSR02C | User Update — modify existing user in USRSEC file | Online (CICS) | **VSAM Read/Write:** USRSEC | COCOM01Y, COUSR02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | COUSR03C.cbl | COUSR03C | User Delete — remove user from USRSEC file | Online (CICS) | **VSAM Read/Delete:** USRSEC | COCOM01Y, COUSR03 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. COBOL Programs — Sub-Applications

### 2.1 Authorization Module (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|-------------------|---------------------|
| 32 | COPAUA0C.cbl | COPAUA0C | Card Authorization Decision — processes authorization requests via MQ, queries DB2 fraud rules | Online (CICS + MQ) | **MQ:** MQOPEN/MQGET/MQPUT1/MQCLOSE; **DB2:** AUTHFRDS table; **IMS:** DL/I calls | CIPAUDTY, CCPAURQY, CCPAURLY, CIPAUSMY, CCPAUERY + 11 others |
| 33 | COPAUS0C.cbl | COPAUS0C | Summary View of Authorization Messages — list pending auths | Online (CICS) | **VSAM Read:** Authorization message file | CIPAUDTY, CIPAUSMY, CCPAUERY + 11 others |
| 34 | COPAUS1C.cbl | COPAUS1C | Detail View of Authorization Message | Online (CICS) | **VSAM Read:** Authorization message file | CIPAUDTY, CIPAUSMY + 8 others |
| 35 | COPAUS2C.cbl | COPAUS2C | Mark Authorization Message as Fraud | Online (CICS) | **VSAM Read/Write:** Authorization message file | CIPAUDTY, CIPAUSMY |
| 36 | CBPAUP0C.cbl | CBPAUP0C | Delete Expired Pending Authorization Messages | Batch (CICS) | **VSAM Delete:** Expired authorization records | CIPAUDTY, CIPAUSMY |
| 37 | PAUDBLOD.CBL | PAUDBLOD | Load authorization data into IMS database | Batch (IMS) | **IMS DL/I:** ISRT (insert) to IMS DB; **Read:** input file | CIPAUDTY, PADFLPCB, PAUTBPCB, IMSFUNCS |
| 38 | PAUDBUNL.CBL | PAUDBUNL | Unload authorization data from IMS database | Batch (IMS) | **IMS DL/I:** GN/GNP (get next); **Write:** output file | CIPAUDTY, PADFLPCB, PAUTBPCB, IMSFUNCS |
| 39 | DBUNLDGS.CBL | DBUNLDGS | Unload IMS database to GSAM (sequential) file | Batch (IMS) | **IMS DL/I:** GN/GNP/ISRT; **Write:** GSAM output | CIPAUDTY, PADFLPCB, PASFLPCB, PAUTBPCB, IMSFUNCS + 1 other |

### 2.2 Transaction Type DB2 Module (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|-------------------|---------------------|
| 40 | COTRTLIC.cbl | COTRTLIC | List Transaction Types for updates and deletes | Online (CICS + DB2) | **DB2:** SELECT on TRNTYPE, TRNTYCAT tables; **EXEC SQL CURSOR** | CSDB2RPY, CSDB2RWY, COTRTLI (BMS) + 8 others |
| 41 | COTRTUPC.cbl | COTRTUPC | Accept and process Transaction Type Update | Online (CICS + DB2) | **DB2:** SELECT/UPDATE/INSERT/DELETE on TRNTYPE, TRNTYCAT | CSDB2RPY, CSDB2RWY, COTRTUP (BMS) + 11 others |
| 42 | COBTUPDT.cbl | COBTUPDT | Update Transaction Type based on user input (batch DB2) | Batch (DB2) | **DB2:** UPDATE on TRNTYPE table | (none — inline SQL) |

### 2.3 VSAM-MQ Module (`app/app-vsam-mq/cbl/`)

| # | Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | COACCT01 | Account processing with MQ integration — receive/send messages | Online (CICS + MQ) | **MQ:** MQOPEN/MQGET/MQPUT/MQCLOSE; **VSAM:** Account file | 9 copybooks |
| 44 | CODATE01.cbl | CODATE01 | Date validation service with MQ integration | Online (CICS + MQ) | **MQ:** MQOPEN/MQGET/MQPUT/MQCLOSE | 8 copybooks |

---

## 3. JCL Jobs (`app/jcl/`)

### 3.1 VSAM File Definition & Loading Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | ACCTFILE.jcl | Define and load Account VSAM KSDS | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO (load from PS) |
| 2 | CARDFILE.jcl | Define and load Card Data VSAM KSDS with AIX | CLCIFIL: SDSF (close CICS files) → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO → STEP40-60: IDCAMS (AIX define/build/path) → OPCIFIL: SDSF (reopen CICS files) |
| 3 | CUSTFILE.jcl | Define and load Customer VSAM KSDS | CLCIFIL: SDSF → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO → OPCIFIL: SDSF |
| 4 | XREFFILE.jcl | Define Card Cross-Reference VSAM KSDS with AIX | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO → STEP20-30: IDCAMS (AIX) |
| 5 | TRANFILE.jcl | Define Transaction Master VSAM KSDS with AIX | CLCIFIL: SDSF → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO → STEP20-30: IDCAMS (AIX) → OPCIFIL: SDSF |
| 6 | TRANIDX.jcl | Define AIX on Transaction Master | STEP20: IDCAMS DEFINE AIX → STEP25: IDCAMS BLDINDEX → STEP30: IDCAMS DEFINE PATH |
| 7 | TCATBALF.jcl | Define Transaction Category Balance VSAM | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO |
| 8 | DISCGRP.jcl | Define Disclosure Group file | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO |
| 9 | TRANTYPE.jcl | Define Transaction Type file | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO |
| 10 | TRANCATG.jcl | Define Transaction Category file | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE → STEP15: IDCAMS REPRO |
| 11 | DEFCUST.jcl | Define Customer Data (alternate format) | STEP05: IDCAMS DELETE → STEP05: IDCAMS DEFINE |
| 12 | DUSRSECJ.jcl | Define User Security file | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02: IDCAMS DEFINE → STEP03: IDCAMS REPRO |
| 13 | ESDSRRDS.jcl | Define ESDS and RRDS VSAM files | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02-03: IDCAMS (ESDS) → STEP04-05: IDCAMS (RRDS) |

### 3.2 Batch Processing Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 14 | POSTTRAN.jcl | Post daily transactions | STEP05: PGM=CBTRN02C |
| 15 | INTCALC.jcl | Calculate interest and fees | STEP05: PGM=CBACT04C |
| 16 | TRANREPT.jcl | Generate transaction report | STEP05R: SORT (sequence transactions) → STEP10R: PGM=CBTRN03C |
| 17 | CREASTMT.JCL | Create account statements | DELDEF01: IDCAMS → STEP010: SORT → STEP020: IDCAMS REPRO → STEP030: IEFBR14 → STEP040: PGM=CBSTM03A |
| 18 | CBEXPORT.jcl | Export data for branch migration | STEP01: IDCAMS (allocate) → STEP02: PGM=CBEXPORT |
| 19 | CBIMPORT.jcl | Import data from branch migration | STEP01: PGM=CBIMPORT |
| 20 | READACCT.jcl | Read and display account file | STEP05: PGM=CBACT01C |
| 21 | READCARD.jcl | Read and display card file | STEP05: PGM=CBACT02C |
| 22 | READXREF.jcl | Read and display cross-reference file | STEP05: PGM=CBACT03C |
| 23 | READCUST.jcl | Read and display customer file | STEP05: PGM=CBCUS01C |
| 24 | COMBTRAN.jcl | Combine/sort transaction files | STEP05R: SORT → STEP10: IDCAMS REPRO |
| 25 | TRANBKP.jcl | Backup and purge Transaction Master | STEP05: IDCAMS REPRO → STEP10: IDCAMS DELETE |
| 26 | WAITSTEP.jcl | Wait step (timing utility) | WAIT: PGM=COBSWAIT |

### 3.3 GDG and Infrastructure Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 27 | DEFGDGB.jcl | Define GDG bases (for reports, rejects, statements) | STEP05: IDCAMS DEFINE GDG |
| 28 | DEFGDGD.jcl | Define DB2-related GDG bases and backup reference data | STEP10: IDCAMS → STEP20: IEBGENER → STEP30: IDCAMS → STEP40: IEBGENER → STEP50: IDCAMS → STEP60: IEBGENER |
| 29 | DALYREJS.jcl | Define GDG for daily rejects | STEP05: IDCAMS DEFINE GDG |
| 30 | REPTFILE.jcl | Define GDG for report files | STEP05: IDCAMS DEFINE GDG |

### 3.4 CICS File Management Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 31 | CLOSEFIL.jcl | Close files in CICS region | CLCIFIL: PGM=SDSF (CEMT SET FIL CLO) |
| 32 | OPENFIL.jcl | Open files in CICS region | OPCIFIL: PGM=SDSF (CEMT SET FIL OPE) |

### 3.5 Administration and Utility Jobs

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 33 | CBADMCDJ.jcl | CSD resource definitions (CICS) | STEP1: PGM=DFHCSDUP |
| 34 | FTPJCL.JCL | FTP file transfer | STEP1: PGM=FTP |
| 35 | INTRDRJ1.JCL | Internal reader — trigger downstream JCL | IDCAMS: backup → STEP01: IEBGENER (submit INTRDRJ2) |
| 36 | INTRDRJ2.JCL | Internal reader — create VSAM for IMS | IDCAMS: backup |
| 37 | TXT2PDF1.JCL | Convert text statement to PDF | TXT2PDF: PGM=IKJEFT1B |
| 38 | PRTCATBL.jcl | Print category balance report | (SORT/print) |

### 3.6 Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 39 | CBPAUP0J.jcl | Run pending auth purge batch | PGM=CBPAUP0C |
| 40 | DBPAUTP0.jcl | IMS DB processing for auth data | DL/I batch |
| 41 | LOADPADB.JCL | Load authorization IMS database | PGM=PAUDBLOD |
| 42 | UNLDPADB.JCL | Unload authorization IMS database | PGM=PAUDBUNL |
| 43 | UNLDGSAM.JCL | Unload IMS to GSAM | PGM=DBUNLDGS |

### 3.7 Sub-Application JCL (`app/app-transaction-type-db2/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 44 | CREADB21.jcl | Create DB2 tables for transaction types | DDL execution |
| 45 | TRANEXTR.jcl | Extract transaction type data from DB2 | SQL UNLOAD |
| 46 | MNTTRDB2.jcl | Maintain transaction type DB2 tables | PGM=COBTUPDT |

---

## 4. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL Programs | 44 |
| Batch Programs | 14 |
| Online (CICS) Programs | 22 |
| Online (CICS + DB2) Programs | 3 |
| Online (CICS + MQ) Programs | 3 |
| Batch (IMS) Programs | 3 |
| Total JCL Jobs | 46 |
| Total Copybooks (app/cpy/) | 30 |
| BMS Maps | 18 |
| VSAM Files Managed | 9 |
| DB2 Tables | 4 |
| IMS Databases | 3 |
| MQ Queues | 3+ |
