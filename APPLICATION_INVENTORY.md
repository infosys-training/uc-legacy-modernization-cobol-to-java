# APPLICATION INVENTORY

## Overview

The CardDemo application is an AWS Mainframe Modernization reference application implementing a credit card management system. The estate comprises **44 COBOL programs** across three sub-applications, **38 JCL jobs**, and supporting copybooks, BMS maps, and control files.

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | Read account file and write into multiple output formats (PS, array, VB) | Batch | READ: ACCTFILE; WRITE: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | Read and print card data file | Batch | READ: CARDFILE | CVACT02Y |
| 3 | CBACT03C.cbl | Read and print account cross-reference data file | Batch | READ: XREFFILE | CVACT03Y |
| 4 | CBACT04C.cbl | Interest calculator — compute interest on accounts | Batch | READ: ACCTFILE, XREFFILE, TRANSACT, DISCGRP; WRITE: TCATBALF | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | Read and print customer data file | Batch | READ: CUSTFILE | CVCUS01Y |
| 6 | CBEXPORT.cbl | Export customer data for branch migration | Batch | READ: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; WRITE: EXPFILE | CVCUS01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVEXPORT, CVTRA05Y |
| 7 | CBIMPORT.cbl | Import customer data from branch migration export | Batch | READ: EXPFILE; WRITE: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVEXPORT, CVTRA05Y |
| 8 | CBTRN01C.cbl | Post records from daily transaction file (validation) | Batch | READ: DALYTRAN, ACCTFILE, CARDFILE, CUSTFILE, XREFFILE; WRITE: TRANFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVTRA06Y |
| 9 | CBTRN02C.cbl | Post records from daily transaction file (update balances) | Batch | READ: DALYTRAN, XREFFILE, ACCTFILE; WRITE: TRANFILE, DALYREJS, TCATBALF | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y |
| 10 | CBTRN03C.cbl | Print transaction detail report | Batch | READ: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; WRITE: TRANREPT | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y |
| 11 | CBSTM03A.CBL | Print account statements from transaction data | Batch | READ: (via CBSTM03B); WRITE: HTMLFILE, STMTFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 12 | CBSTM03B.CBL | File I/O subroutine for statement report | Batch (module) | READ: ACCTFILE, CUSTFILE, TRNXFILE, XREFFILE | (none — inline) |
| 13 | COBSWAIT.cbl | Utility — wait for specified centiseconds | Batch (utility) | CALL MVSWAIT | (none) |
| 14 | CSUTLDTC.cbl | Date utility — convert/validate dates via CEEDAYS | Batch (utility) | CALL CEEDAYS | (none) |
| 15 | COSGN00C.cbl | Signon screen for CardDemo application | Online (CICS) | CICS READ: USRSEC file | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COMEN01C.cbl | Main menu for regular users | Online (CICS) | CICS XCTL to selected program | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COADM01C.cbl | Admin menu for admin users | Online (CICS) | CICS XCTL to admin programs | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | COACTVWC.cbl | View account details | Online (CICS) | CICS READ: ACCTDAT, CARDDAT, CUSTDAT, CARDXREF | COCOM01Y, COACTVW, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 19 | COACTUPC.cbl | Accept and process account update | Online (CICS) | CICS READ/REWRITE: ACCTDAT, CARDDAT, CUSTDAT, CARDXREF | COCOM01Y, COACTUP, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CSUTLDPY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSETATY, CSSTRPFY, CSUTLDWY |
| 20 | COCRDLIC.cbl | List credit cards | Online (CICS) | CICS STARTBR/READNEXT/READPREV: CARDDAT | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 21 | COCRDSLC.cbl | View credit card details | Online (CICS) | CICS READ: CARDDAT, CUSTDAT, ACCTDAT | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 22 | COCRDUPC.cbl | Update credit card details | Online (CICS) | CICS READ/REWRITE: CARDDAT, CUSTDAT, ACCTDAT | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, CSSTRPFY |
| 23 | COTRN00C.cbl | List transactions from TRANSACT file | Online (CICS) | CICS STARTBR/READNEXT/READPREV: TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | COTRN01C.cbl | View a transaction from TRANSACT file | Online (CICS) | CICS READ: TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN02C.cbl | Add a new transaction to TRANSACT file | Online (CICS) | CICS READ/WRITE: TRANSACT, ACCTDAT, CARDXREF | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COBIL00C.cbl | Bill payment — pay account balance in full/partial | Online (CICS) | CICS READ/REWRITE/WRITE: ACCTDAT, TRANSACT, CARDXREF | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | CORPT00C.cbl | Submit batch transaction reports (via TD queue) | Online (CICS) | CICS WRITEQ TD (triggers batch report) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 28 | COUSR00C.cbl | List all users from USRSEC file | Online (CICS) | CICS STARTBR/READNEXT/READPREV: USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR01C.cbl | Add a new user to USRSEC file | Online (CICS) | CICS WRITE: USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR02C.cbl | Update a user in USRSEC file | Online (CICS) | CICS READ/REWRITE: USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | COUSR03C.cbl | Delete a user from USRSEC file | Online (CICS) | CICS READ/DELETE: USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. COBOL Programs — Authorization Sub-Application (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 32 | COPAUA0C.cbl | Card authorization decision program (MQ listener) | Online (CICS+MQ) | MQ GET/PUT; CICS READ: ACCTDAT, CARDXREF, CUSTDAT | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, CVACT01Y, CVACT03Y, CVCUS01Y, CMQ* |
| 33 | COPAUS0C.cbl | Summary view of authorization messages | Online (CICS) | CICS READ: PAUAUTH; CICS SYNCPOINT | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 34 | COPAUS1C.cbl | Detail view of authorization message | Online (CICS) | CICS LINK to COPAUS2C | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA |
| 35 | COPAUS2C.cbl | Mark authorization message as fraud (DB2) | Online (CICS+DB2) | EXEC SQL UPDATE | CIPAUDTY |
| 36 | CBPAUP0C.cbl | Delete expired pending authorization messages | Batch | IMS DL/I operations (via batch) | CIPAUDTY, CIPAUSMY |
| 37 | PAUDBLOD.CBL | Load authorization data into IMS database | Batch (IMS) | READ: INFILE1, INFILE2; IMS ISRT/GU | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |
| 38 | PAUDBUNL.CBL | Unload authorization data from IMS database | Batch (IMS) | IMS GN/GNP; WRITE to flat files | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB |
| 39 | DBUNLDGS.CBL | Unload IMS database to GSAM files | Batch (IMS) | IMS GN/GNP/ISRT; GSAM output | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB |

---

## 3. COBOL Programs — Transaction Type Sub-Application (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 40 | COTRTLIC.cbl | List transaction types for updates and deletes | Online (CICS+DB2) | EXEC SQL SELECT from TRTYP table | COCOM01Y, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 41 | COTRTUPC.cbl | Accept and process transaction type update | Online (CICS+DB2) | EXEC SQL UPDATE/INSERT/DELETE TRTYP | COCOM01Y, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CSSETATY, CSSTRPFY, CSUTLDWY |
| 42 | COBTUPDT.cbl | Batch update transaction type from file to DB2 | Batch (DB2) | READ: INPFILE; EXEC SQL INSERT/UPDATE TRTYP | CSDB2RPY, CSDB2RWY |

---

## 4. COBOL Programs — VSAM/MQ Sub-Application (`app/app-vsam-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | Account inquiry via MQ request/reply | Online (CICS+MQ) | CICS READ: ACCTDAT; MQ GET/PUT | CVACT01Y, CMQ* |
| 44 | CODATE01.cbl | Date service via MQ request/reply | Online (CICS+MQ) | MQ GET/PUT (date conversion service) | CMQ* |

---

## 5. JCL Jobs — Main Application (`app/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | ACCTFILE.jcl | Define/load ACCTDATA VSAM cluster | STEP05: IDCAMS (delete) → STEP10: IDCAMS (define) → STEP15: IDCAMS (repro/load) |
| 2 | CARDFILE.jcl | Define/load CARDDATA VSAM cluster | CLCIFIL: SDSF (close) → STEP05-15: IDCAMS (delete/define/load) → STEP40-60: IDCAMS (alt index) → OPCIFIL: SDSF (open) |
| 3 | CBADMCDJ.jcl | CICS CSD update for admin definitions | STEP1: DFHCSDUP |
| 4 | CBEXPORT.jcl | Export customer data for migration | STEP01: IDCAMS (prep) → STEP02: CBEXPORT |
| 5 | CBIMPORT.jcl | Import customer data from export file | STEP01: CBIMPORT |
| 6 | CLOSEFIL.jcl | Close CICS files | CLCIFIL: SDSF |
| 7 | COMBTRAN.jcl | Combine/sort daily transactions | STEP05R: SORT → STEP10: IDCAMS (repro into VSAM) |
| 8 | CREASTMT.JCL | Create account statements | DELDEF01: IDCAMS → STEP010: SORT → STEP020: IDCAMS (repro) → STEP030: IEFBR14 (cleanup) → STEP040: CBSTM03A |
| 9 | CUSTFILE.jcl | Define/load CUSTDATA VSAM cluster | CLCIFIL: SDSF → STEP05-15: IDCAMS (delete/define/load) → OPCIFIL: SDSF |
| 10 | DALYREJS.jcl | Define daily rejects dataset | STEP05: IDCAMS |
| 11 | DEFCUST.jcl | Define customer VSAM dataset (alternate) | STEP05: IDCAMS (×2) |
| 12 | DEFGDGB.jcl | Define GDG base for backups | STEP05: IDCAMS |
| 13 | DEFGDGD.jcl | Define GDG data entries | STEP10-60: IDCAMS/IEBGENER (multiple generations) |
| 14 | DISCGRP.jcl | Define/load disclosure group VSAM | STEP05-15: IDCAMS (delete/define/load) |
| 15 | DUSRSECJ.jcl | Define/load user security VSAM file | PREDEL: IEFBR14 → STEP01: IEBGENER → STEP02-03: IDCAMS |
| 16 | ESDSRRDS.jcl | Define ESDS/RRDS datasets | PREDEL: IEFBR14 → STEP01-05: IEBGENER/IDCAMS |
| 17 | FTPJCL.JCL | FTP file transfer | STEP1: FTP |
| 18 | INTCALC.jcl | Calculate interest on accounts | STEP15: CBACT04C (with date parm) |
| 19 | INTRDRJ1.JCL | Internal reader — submit nested JCL (1) | IDCAMS → STEP01: IEBGENER |
| 20 | INTRDRJ2.JCL | Internal reader — submit nested JCL (2) | IDCAMS |
| 21 | OPENFIL.jcl | Open CICS files | OPCIFIL: SDSF |
| 22 | POSTTRAN.jcl | Post daily transactions to master | STEP15: CBTRN02C |
| 23 | PRTCATBL.jcl | Print category balance report | DELDEF: IEFBR14 → STEP05R: REPROC → STEP10R: SORT |
| 24 | READACCT.jcl | Read and export account data | PREDEL: IEFBR14 → STEP05: CBACT01C |
| 25 | READCARD.jcl | Read and print card data | STEP05: CBACT02C |
| 26 | READCUST.jcl | Read and print customer data | STEP05: CBCUS01C |
| 27 | READXREF.jcl | Read and print cross-reference data | STEP05: CBACT03C |
| 28 | REPTFILE.jcl | Define report output dataset | STEP05: IDCAMS |
| 29 | TCATBALF.jcl | Define/load transaction category balance VSAM | STEP05-15: IDCAMS (delete/define/load) |
| 30 | TRANBKP.jcl | Backup transaction file (GDG) | STEP05R: REPROC → STEP05: IDCAMS (repro) → STEP10: IDCAMS (verify) |
| 31 | TRANCATG.jcl | Define/load transaction category VSAM | STEP05-15: IDCAMS (delete/define/load) |
| 32 | TRANFILE.jcl | Define/load TRANSACT VSAM cluster | CLCIFIL: SDSF → STEP05-30: IDCAMS (delete/define/load/AIX) → OPCIFIL: SDSF |
| 33 | TRANIDX.jcl | Define transaction alternate indexes | STEP20-30: IDCAMS |
| 34 | TRANREPT.jcl | Generate transaction detail report | STEP05R: REPROC/SORT → STEP10R: CBTRN03C |
| 35 | TRANTYPE.jcl | Define/load transaction type VSAM | STEP05-15: IDCAMS (delete/define/load) |
| 36 | TXT2PDF1.JCL | Convert text report to PDF | TXT2PDF: IKJEFT1B |
| 37 | WAITSTEP.jcl | Wait utility (delay processing) | WAIT: COBSWAIT |
| 38 | XREFFILE.jcl | Define/load CARDXREF VSAM cluster | STEP05-30: IDCAMS (delete/define/load/AIX) |

---

## 6. JCL Jobs — Authorization Sub-Application (`app/app-authorization-ims-db2-mq/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 39 | CBPAUP0J.jcl | Run pending auth purge via IMS BMP | STEP01: DFSRRC00 (IMS BMP → CBPAUP0C) |
| 40 | DBPAUTP0.jcl | Unload IMS auth database to flat file | STEPDEL: IEFBR14 → UNLOAD: DFSRRC00 |
| 41 | LOADPADB.JCL | Load flat files into IMS auth database | STEP01: DFSRRC00 (→ PAUDBLOD) |
| 42 | UNLDGSAM.JCL | Unload IMS auth DB to GSAM files | STEP01: DFSRRC00 (→ DBUNLDGS) |
| 43 | UNLDPADB.JCL | Unload IMS auth DB to sequential files | STEP0: IEFBR14 → STEP01: DFSRRC00 (→ PAUDBUNL) |

---

## 7. JCL Jobs — Transaction Type Sub-Application (`app/app-transaction-type-db2/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 44 | CREADB21.jcl | Create DB2 objects for transaction types | FREEPLN: IKJEFT01 (free plan) → CRCRDDB: IKJEFT01 (DDL) |
| 45 | MNTTRDB2.jcl | Bind DB2 plan for transaction type programs | STEP1: IKJEFT01 (BIND) |
| 46 | TRANEXTR.jcl | Extract transaction type/category to backup | STEP10: IEBGENER → STEP20: IEBGENER |

---

## 8. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Batch programs | 16 |
| Online (CICS) programs | 22 |
| Online (CICS+DB2) programs | 3 |
| Online (CICS+MQ) programs | 3 |
| Utility programs | 2 |
| Total JCL jobs | 46 |
| Total copybooks (app/cpy) | 29 |
| Total copybooks (sub-apps) | 11 |
| VSAM files managed | 10+ |
| DB2 tables accessed | 1 (TRTYP) |
| IMS databases | 1 (PAUTDB) |
| MQ queues | 2+ (request/reply) |
