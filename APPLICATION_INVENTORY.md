# APPLICATION INVENTORY — CardDemo COBOL Estate

## Overview

| Metric | Value |
|--------|-------|
| Total COBOL Programs | 44 |
| Total Lines of Code | ~27,350 |
| Online (CICS) Programs | 21 (48%) |
| Batch Programs | 16 (36%) |
| Sub-Application (IMS/DB2/MQ) | 7 (16%) |
| Copybooks | 47 |
| JCL Jobs | 46 |
| BMS Screen Maps | 16 |

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

### 1.1 Online (CICS) Programs

| Filename | LOC | Purpose | Copybooks | Key I/O Operations |
|----------|-----|---------|-----------|-------------------|
| COSGN00C.cbl | 263 | Sign-on screen — authenticates users, routes Admin → COADM01C, Regular → COMEN01C | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | EXEC CICS READ (USRSEC file) |
| COADM01C.cbl | 288 | Admin menu — presents 6 admin options (user CRUD, DB2 tran type mgmt) | COADM01, COADM02Y, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | EXEC CICS XCTL to admin sub-programs |
| COMEN01C.cbl | 308 | Main menu hub — presents 11 options for regular users (account, card, transaction, reports, bill pay, auth view) | COCOM01Y, COMEN01, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | EXEC CICS XCTL to 11 sub-programs |
| COACTVWC.cbl | 941 | Account view — displays account details with linked cards and customer info | COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA | EXEC CICS READ: ACCTFILE, CUSTFILE, CARDXREF, CARDFILE |
| COACTUPC.cbl | 4,236 | Account update — full CRUD with exhaustive field validation (date, SSN, phone, state, ZIP). Largest program in estate | COACTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSSTRPFY, CSUSR01Y, CSUTLDPY, CSUTLDWY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA | EXEC CICS READ/REWRITE: ACCTFILE, CUSTFILE, CARDXREF |
| COCRDLIC.cbl | 1,459 | Credit card list — paginated browse (STARTBR/READNEXT/READPREV) with filter by account/card | COCRDLI, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA | EXEC CICS STARTBR/READNEXT/READPREV/ENDBR: CARDFILE |
| COCRDSLC.cbl | 887 | Credit card detail view — displays card, account, and customer data | COCRDSL, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA | EXEC CICS READ: CARDFILE, ACCTFILE, CUSTFILE, CARDXREF |
| COCRDUPC.cbl | 1,560 | Credit card update — edit card details with validation | COCRDUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSTRPFY, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA | EXEC CICS READ/REWRITE: CARDFILE, ACCTFILE, CUSTFILE |
| COTRN00C.cbl | 699 | Transaction list — paginated browse of TRANSACT file | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA | EXEC CICS STARTBR/READNEXT/READPREV: TRANSACT |
| COTRN01C.cbl | 330 | Transaction detail view — displays single transaction record | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA | EXEC CICS READ: TRANSACT |
| COTRN02C.cbl | 783 | Transaction add — creates new transaction with validation; calls CSUTLDTC for date conversion | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA | EXEC CICS READ: CARDXREF, CXACAIX; STARTBR/READPREV/WRITE: TRANSACT |
| CORPT00C.cbl | 649 | Report request screen — submits batch JCL (INTRDRJ1/J2) for transaction reports; calls CSUTLDTC for date validation | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA | EXEC CICS STARTBR/READPREV: TRANSACT; submits batch JCL |
| COBIL00C.cbl | 1,026 | Bill payment — processes full-balance payment, creates payment transaction, updates account balance | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA | EXEC CICS READ/REWRITE: ACCTFILE; READ: CXACAIX; STARTBR/READPREV/WRITE: TRANSACT |
| COUSR00C.cbl | 695 | User list — paginated browse of USRSEC file (admin only) | COCOM01Y, COTTL01Y, COUSR00, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | EXEC CICS STARTBR/READNEXT/READPREV: USRSEC |
| COUSR01C.cbl | 299 | User add — creates new Regular/Admin user in USRSEC | COCOM01Y, COTTL01Y, COUSR01, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | EXEC CICS WRITE: USRSEC |
| COUSR02C.cbl | 414 | User update — modifies user record in USRSEC | COCOM01Y, COTTL01Y, COUSR02, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | EXEC CICS READ/REWRITE: USRSEC |
| COUSR03C.cbl | 359 | User delete — removes user from USRSEC with confirmation | COCOM01Y, COTTL01Y, COUSR03, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA | EXEC CICS READ/DELETE: USRSEC |

### 1.2 Batch Programs

| Filename | LOC | Purpose | Copybooks | Key I/O Operations |
|----------|-----|---------|-----------|-------------------|
| CBTRN01C.cbl | 834 | Validate and prepare daily transaction records; read daily file, cross-check against XREF, write validated transactions | CVACT01Y, CVACT03Y, CVTRA05Y, CVTRA06Y | READ: DALYTRAN (seq), XREF (KSDS); WRITE: TRANSACT (KSDS), DALYREJS (seq) |
| CBTRN02C.cbl | 799 | Post daily transactions to master file; update account balances and transaction category balances | CVACT01Y, CVACT03Y, CVTRA05Y, CVTRA06Y | READ: DALYTRAN (seq), XREF (KSDS), ACCOUNT (KSDS); WRITE: TRANSACT (KSDS), DALYREJS (seq); REWRITE: ACCOUNT, TCATBAL |
| CBTRN03C.cbl | 649 | Print daily transaction detail report with type/category descriptions | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y | READ: TRANSACT, XREF, TRANTYPE, TRANCATG, DATE-PARMS; WRITE: REPORT-FILE |
| CBACT01C.cbl | 430 | Read account file, produce fixed-length, array, and variable-length output files | CODATECN, CVACT01Y | READ: ACCTFILE (KSDS); WRITE: OUT-FILE, ARRY-FILE, VBRC-FILE; CALL: COBDATFT |
| CBACT02C.cbl | 178 | Read and print card data file | CVACT02Y | READ: CARDFILE (KSDS) |
| CBACT03C.cbl | 178 | Read and print card cross-reference data file | CVACT03Y | READ: XREFFILE (KSDS) |
| CBACT04C.cbl | 652 | Interest calculator — compute interest on accounts using disclosure group rates | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y | READ: TCATBAL, XREF, DISCGRP, ACCOUNT; WRITE: TRANSACT; REWRITE: ACCOUNT |
| CBSTM03A.CBL | 924 | Statement generation — produce account statements (text + HTML) from transaction data; calls CBSTM03B for file I/O | COSTM01, CUSTREC, CVACT01Y, CVACT03Y | READ (via CBSTM03B): TRNX, XREF, CUST, ACCT; WRITE: STMT-FILE, HTML-FILE; CALL: CBSTM03B |
| CBSTM03B.CBL | 230 | Statement I/O subroutine — file processing for CBSTM03A | _(none — uses inline FDs)_ | READ: TRNX-FILE, XREF-FILE, CUST-FILE, ACCT-FILE |
| CBCUS01C.cbl | 227 | Read and print customer data file | CVCUS01Y | READ: CUSTFILE (KSDS) |
| CBEXPORT.cbl | 614 | Export customer/account/card/transaction data to sequential file for branch migration | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT | READ: CUSTFILE, ACCTFILE, CARDFILE, XREFFILE, TRANSACT; WRITE: EXPORT-FILE |
| CBIMPORT.cbl | 455 | Import data from sequential file into VSAM datasets with validation | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT | READ: IMPORT-FILE; WRITE: ACCTFILE, CARDFILE, CUSTFILE, XREFFILE, TRANSACT |
| COBSWAIT.cbl | 41 | Utility — wait for specified centiseconds (CALL MVSWAIT) | _(none)_ | CALL: MVSWAIT (assembler) |
| CSUTLDTC.cbl | 157 | Utility — date validation/conversion via LE CEEDAYS API | _(none)_ | CALL: CEEDAYS |
| COBDATFT.cbl | _(asm)_ | External assembler date formatter (called by CBACT01C) | — | — |

### 1.3 Sub-Application Programs

#### Authorization IMS/DB2/MQ (`app/app-authorization-ims-db2-mq/cbl/`)

| Filename | LOC | Purpose | Classification | Subsystems | Copybooks | Key I/O |
|----------|-----|---------|---------------|------------|-----------|---------|
| COPAUA0C.cbl | 1,026 | Card authorization decision — receive MQ request, lookup IMS auth data, validate, send MQ response | Online (CICS/IMS/MQ) | CICS + IMS + MQ | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, CMQ*, CVACT01Y, CVACT03Y, CVCUS01Y | MQGET: AUTH-REQ-QUEUE; IMS GU: PAUTSMY, PAUTDTY; EXEC CICS READ: ACCTFILE, CARDXREF, CUSTFILE; MQPUT: AUTH-REPLY-QUEUE |
| COPAUS0C.cbl | 1,032 | Authorization summary browse — paginated view of pending authorizations from IMS | Online (CICS/IMS) | CICS + IMS | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA | IMS GU/GNP: PAUTSMY, PAUTDTY; EXEC CICS READ: ACCTFILE |
| COPAUS1C.cbl | 604 | Authorization detail view — display and update single authorization | Online (CICS/IMS) | CICS + IMS | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA | IMS GU/GNP/REPL: PAUTSMY, PAUTDTY |
| COPAUS2C.cbl | 244 | Mark authorization as fraud — insert fraud flag via DB2 | Online (CICS/IMS/DB2) | CICS + IMS + DB2 | CIPAUDTY | EXEC SQL INSERT/SELECT: FRAUD_FLAGS; IMS GU: PAUTDTY |
| CBPAUP0C.cbl | 386 | Batch purge of expired pending authorizations from IMS database | Batch (IMS) | IMS | CIPAUDTY, CIPAUSMY | IMS GN/GNP/DLET: PAUTSMY, PAUTDTY |
| DBUNLDGS.CBL | 366 | Batch GSAM unload — extract IMS data to sequential GSAM files | Batch (IMS) | IMS | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB | IMS GN/GNP; GSAM ISRT: PASFILOP, PADFILOP |
| PAUDBLOD.CBL | 369 | Batch IMS database load — load authorization data into IMS from GSAM files | Batch (IMS) | IMS | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB | IMS ISRT/GU: PAUTSMY, PAUTDTY; GSAM READ: INFILE1, INFILE2 |

#### Transaction Type DB2 (`app/app-transaction-type-db2/cbl/`)

| Filename | LOC | Purpose | Classification | Subsystems | Copybooks | Key I/O |
|----------|-----|---------|---------------|------------|-----------|---------|
| COTRTLIC.cbl | 2,098 | Transaction type list with DB2 cursor-based pagination — list/update/delete | Online (CICS/DB2) | CICS + DB2 | CSDB2RPY, CSDB2RWY (inline COPY) | EXEC SQL DECLARE CURSOR, OPEN, FETCH, CLOSE on TRANSACTION_TYPE, TRANSACTION_CATEGORY |
| COTRTUPC.cbl | 1,702 | Transaction type update — edit/add/delete transaction types with cascading deletes | Online (CICS/DB2) | CICS + DB2 | CSDB2RPY, CSDB2RWY (inline COPY) | EXEC SQL SELECT/INSERT/UPDATE/DELETE on TRANSACTION_TYPE, TRANSACTION_CATEGORY |
| COBTUPDT.cbl | 237 | Batch update of transaction types via DB2 | Batch (DB2) | DB2 | _(none)_ | EXEC SQL UPDATE/INSERT on TRANSACTION_TYPE |

#### VSAM/MQ (`app/app-vsam-mq/cbl/`)

| Filename | LOC | Purpose | Classification | Subsystems | Copybooks | Key I/O |
|----------|-----|---------|---------------|------------|-----------|---------|
| COACCT01.cbl | 620 | Account inquiry via MQ — receive MQ request, read account VSAM, send MQ reply | Online (CICS/MQ) | CICS + MQ | CVACT01Y | MQGET: ACCT-REQ-QUEUE; EXEC CICS READ: ACCTFILE; MQPUT: ACCT-REPLY-QUEUE |
| CODATE01.cbl | 524 | Date inquiry via MQ — receive MQ request, return current date/time via MQ reply | Online (CICS/MQ) | CICS + MQ | _(none)_ | MQGET: DATE-REQ-QUEUE; MQPUT: DATE-REPLY-QUEUE |

---

## 2. JCL Job Catalog (`app/jcl/`)

### 2.1 Batch Processing Jobs

| JCL File | Job Name | Steps | Programs Executed | Purpose |
|----------|----------|-------|-------------------|---------|
| POSTTRAN.jcl | POSTTRAN | STEP05R (SORT), STEP10 (CBTRN01C), STEP15 (CBTRN02C) | SORT, CBTRN01C, CBTRN02C | Daily transaction posting pipeline — sort, validate, post |
| INTCALC.jcl | INTCALC | STEP15 (CBACT04C) | CBACT04C | Interest calculation on account balances |
| CREASTMT.JCL | CREASTMT | DELDEF01 (IDCAMS), STEP010 (SORT), STEP020 (IDCAMS), STEP030 (IEFBR14), STEP040 (CBSTM03A) | SORT, CBSTM03A | Create customer statements — sort transactions, generate text + HTML |
| TRANREPT.jcl | TRANREPT | STEP05R (SORT/REPROC), STEP10R (CBTRN03C) | SORT, CBTRN03C | Daily transaction detail report |
| CBEXPORT.jcl | CBEXPORT | STEP01 (IDCAMS), STEP02 (CBEXPORT) | CBEXPORT | Export data for branch migration |
| CBIMPORT.jcl | CBIMPORT | STEP01 (CBIMPORT) | CBIMPORT | Import data from sequential file |
| WAITSTEP.jcl | WAITSTEP | WAIT (COBSWAIT) | COBSWAIT | Utility — timed wait |

### 2.2 Batch Read/Print Jobs

| JCL File | Job Name | Steps | Programs Executed | Purpose |
|----------|----------|-------|-------------------|---------|
| READACCT.jcl | READACCT | STEP05 (CBACT01C) | CBACT01C | Read account file, produce output files |
| READCARD.jcl | READCARD | STEP05 (CBACT02C) | CBACT02C | Read and print card file |
| READXREF.jcl | READXREF | STEP05 (CBACT03C) | CBACT03C | Read and print cross-reference file |
| READCUST.jcl | READCUST | STEP05 (CBCUS01C) | CBCUS01C | Read and print customer file |

### 2.3 VSAM Data Setup Jobs (IDCAMS)

| JCL File | Job Name | Steps | Purpose |
|----------|----------|-------|---------|
| ACCTFILE.jcl | ACCTFILE | STEP05/10/15 (IDCAMS) | Define/delete/repro ACCTFILE VSAM KSDS cluster |
| CARDFILE.jcl | CARDFILE | CLCIFIL, STEP05/10/15/40/50/60, OPCIFIL | Define CARDFILE VSAM KSDS + AIX (alternate index) |
| CUSTFILE.jcl | CUSTFILE | CLCIFIL, STEP05/10/15, OPCIFIL | Define CUSTFILE VSAM KSDS cluster |
| XREFFILE.jcl | XREFFILE | STEP05/10/15/20/25/30 | Define CARDXREF VSAM KSDS + AIX (CXACAIX) |
| TRANFILE.jcl | TRANFILE | CLCIFIL, STEP05/10/15/20/25/30, OPCIFIL | Define TRANSACT VSAM KSDS + AIX |
| TCATBALF.jcl | TCATBALF | STEP05/10/15 | Define TCATBAL (transaction category balance) VSAM KSDS |
| DISCGRP.jcl | DISCGRP | STEP05/10/15 | Define disclosure group VSAM KSDS |
| TRANTYPE.jcl | TRANTYPE | STEP05/10/15 | Define transaction type VSAM KSDS |
| TRANCATG.jcl | TRANCATG | STEP05/10/15 | Define transaction category VSAM KSDS |
| DUSRSECJ.jcl | DUSRSECJ | PREDEL, STEP01/02/03 | Define USRSEC (user security) VSAM KSDS from PS |
| DEFCUST.jcl | DEFCUST | STEP05 | Define customer data file alternate layout |
| ESDSRRDS.jcl | ESDSRRDS | PREDEL, STEP01–05 | Define ESDS and RRDS variants of USRSEC |
| TRANIDX.jcl | TRANIDX | STEP20/25/30 | Define AIX (alternate index) on TRANSACT file |

### 2.4 GDG (Generation Data Group) Management

| JCL File | Job Name | Steps | Purpose |
|----------|----------|-------|---------|
| DEFGDGB.jcl | DEFGDGB | STEP05 (IDCAMS) | Define GDG base for backups |
| DEFGDGD.jcl | DEFGDGD | STEP10–60 | Define GDG + backup DB2 data (TRANTYPE, TRANCATG, DISCGRP) |
| DALYREJS.jcl | DALYREJS | STEP05 | Define GDG for daily rejection files |
| REPTFILE.jcl | REPTFILE | STEP05 | Define GDG for report output files |
| TRANBKP.jcl | TRANBKP | STEP05R, STEP05, STEP10 | Backup and purge TRANSACT file (REPRO + DELETE) |
| COMBTRAN.jcl | COMBTRAN | STEP05R (SORT), STEP10 (IDCAMS) | Combine/merge transaction files |

### 2.5 CICS Management Jobs

| JCL File | Job Name | Purpose |
|----------|----------|---------|
| OPENFIL.jcl | OPENFIL | Open VSAM files in CICS (SDSF) |
| CLOSEFIL.jcl | CLOSEFIL | Close VSAM files in CICS (SDSF) |
| CBADMCDJ.jcl | CBADMCDJ | CICS CSD update (DFHCSDUP) — define program/transaction resources |
| FTPJCL.JCL | FTPJCLS | FTP job to transfer files |

### 2.6 Sub-Application JCL

#### IMS Authorization (`app/app-authorization-ims-db2-mq/jcl/`)

| JCL File | Job Name | Steps | Programs Executed | Purpose |
|----------|----------|-------|-------------------|---------|
| CBPAUP0J.jcl | CBPAUP0J | STEP01 (DFSRRC00→CBPAUP0C) | CBPAUP0C | Purge expired IMS authorizations |
| LOADPADB.JCL | LOADPADB | STEP01 (DFSRRC00→PAUDBLOD) | PAUDBLOD | Load IMS pending authorization database |
| UNLDPADB.JCL | UNLDPADB | STEP01 (DFSRRC00→PAUDBUNL) | PAUDBUNL | Unload IMS pending authorization database |
| UNLDGSAM.JCL | UNLDGSAM | STEP01 (DFSRRC00→DBUNLDGS) | DBUNLDGS | Unload IMS to GSAM files |

#### DB2 Transaction Type (`app/app-transaction-type-db2/jcl/`)

| JCL File | Job Name | Steps | Purpose |
|----------|----------|-------|---------|
| DEFTYPES.JCL | DEFTYPES | Multiple IDCAMS steps | Define DB2 transaction type/category datasets |
| BINDPLAN.JCL | BINDPLAN | DSN BIND | Bind DB2 plans for COTRTLIC/COTRTUPC |

### 2.7 Online Submission JCL (Submitted by CORPT00C)

| JCL File | Purpose |
|----------|---------|
| INTRDRJ1.JCL | Internal reader job 1 — submits TRANREPT batch from online |
| INTRDRJ2.JCL | Internal reader job 2 — submits CREASTMT batch from online |

---

## 3. Summary Statistics

### Programs by Size

| Size Bracket | Count | Programs |
|--------------|-------|----------|
| > 2,000 LOC | 3 | COACTUPC (4,236), COTRTLIC (2,098), COTRTUPC (1,702) |
| 1,000–2,000 LOC | 5 | COCRDUPC (1,560), COCRDLIC (1,459), COPAUS0C (1,032), COBIL00C (1,026), COPAUA0C (1,026) |
| 500–999 LOC | 10 | COACTVWC, CBSTM03A, CBTRN01C, CBTRN02C, COTRN02C, COTRN00C, CBACT04C, CORPT00C, CBTRN03C, CBEXPORT |
| < 500 LOC | 26 | All remaining programs |

### Programs by Subsystem

| Subsystem | Count | Programs |
|-----------|-------|----------|
| Pure CICS | 17 | COSGN00C, COADM01C, COMEN01C, COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C–03C |
| CICS + DB2 | 2 | COTRTLIC, COTRTUPC |
| CICS + IMS | 2 | COPAUS0C, COPAUS1C |
| CICS + IMS + DB2 | 1 | COPAUS2C |
| CICS + IMS + MQ | 1 | COPAUA0C |
| CICS + MQ | 2 | COACCT01, CODATE01 |
| Pure Batch | 14 | CBTRN01C, CBTRN02C, CBTRN03C, CBACT01C–04C, CBSTM03A/B, CBCUS01C, CBEXPORT, CBIMPORT, COBSWAIT, CSUTLDTC |
| Batch + IMS | 3 | CBPAUP0C, PAUDBLOD, DBUNLDGS |
| Batch + DB2 | 1 | COBTUPDT |
