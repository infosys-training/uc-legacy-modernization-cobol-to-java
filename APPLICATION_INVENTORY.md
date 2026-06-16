# APPLICATION INVENTORY — CardDemo COBOL Estate

## Executive Summary

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Main programs (`app/cbl/`) | 27 |
| Sub-application programs | 13 |
| Total lines of COBOL | ~30,175 |
| Copybooks | 47 |
| JCL jobs | 51 |
| BMS screen maps | 17 |

---

## 1. COBOL Programs — Main Application (`app/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | Read account VSAM file and write to multiple output formats (flat, array, VB records) | Batch | **R:** ACCTFILE (VSAM KSDS) **W:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | Read and display card data file | Batch | **R:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | Read and display account cross-reference data file | Batch | **R:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | Interest calculation — compute interest and fees on account balances | Batch | **R:** TCATBALF, XREFFILE, DISCGRP, TRANSACT **RW:** ACCTFILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | Read and display customer data file | Batch | **R:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | Export all master data (customer, account, card, xref, transactions) to a single export file for branch migration | Batch | **R:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE **W:** EXPFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVEXPORT |
| 7 | CBIMPORT.cbl | Import data from export file with validation; split into entity-specific output files | Batch | **R:** EXPFILE **W:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVEXPORT |
| 8 | CBSTM03A.CBL | Generate customer account statements (text + HTML) — main driver | Batch | **R:** TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE **W:** STMTFILE, HTML-FILE | CVACT01Y, CVACT03Y, CVCUS01Y, CVTRA05Y, COSTM01 |
| 9 | CBSTM03B.CBL | Statement generation I/O submodule — called by CBSTM03A for file operations | Batch (submodule) | **R:** TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE **W:** STMTFILE | CVACT01Y, CVACT03Y, CVCUS01Y, CVTRA05Y |
| 10 | CBTRN01C.cbl | Read and validate daily transaction input file | Batch | **R:** DALYTRAN | CVTRA05Y, CVTRA06Y |
| 11 | CBTRN02C.cbl | Post daily transactions to master transaction file; update account balances | Batch | **R:** DALYTRAN **RW:** TRANSACT, ACCTFILE, XREFFILE | CVTRA05Y, CVTRA06Y, CVACT01Y, CVACT03Y |
| 12 | CBTRN03C.cbl | Generate daily transaction report with totals by account | Batch | **R:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG **W:** REPTFILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | Wait routine — calls assembler MVSWAIT for timed delays | Batch (utility) | None | None |
| 14 | CSUTLDTC.cbl | Date conversion utility — calls LE CEEDAYS for Lilian date computation | Batch (utility) | None | CSUTLDWY, CSUTLDPY |
| 15 | COACTUPC.cbl | Account update — CICS screen for viewing/modifying account, customer, and card data with extensive field validation | Online (CICS) | **RW:** ACCTFILE, CARDXREF, CUSTFILE via EXEC CICS READ/REWRITE | COCOM01Y, COACTVW (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSETATY (×3 REPLACING), CSLKPCDY, CSUTLDPY, CSUTLDWY, DFHAID, DFHBMSCA |
| 16 | COACTVWC.cbl | Account view — CICS screen for read-only display of account details | Online (CICS) | **R:** ACCTFILE, CARDXREF, CUSTFILE via EXEC CICS READ | COCOM01Y, COACTVW (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 17 | COADM01C.cbl | Admin menu — CICS screen displaying admin options, routing to sub-programs via XCTL | Online (CICS) | None (navigation only) | COCOM01Y, COADM01 (BMS), COADM02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA |
| 18 | COBIL00C.cbl | Bill payment — CICS screen for processing payments against account balances | Online (CICS) | **RW:** ACCTFILE, CARDXREF via EXEC CICS READ/REWRITE **W:** TRANSACT | COCOM01Y, COBIL00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 19 | COCRDLIC.cbl | Credit card list — CICS screen with paginated browse (STARTBR/READNEXT/READPREV) | Online (CICS) | **R:** CARDFILE via EXEC CICS STARTBR/READNEXT/READPREV/ENDBR | COCOM01Y, COCRDLI (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT02Y, DFHAID, DFHBMSCA |
| 20 | COCRDSLC.cbl | Credit card view — CICS screen for displaying single card details | Online (CICS) | **R:** CARDFILE, ACCTFILE, XREFFILE via EXEC CICS READ | COCOM01Y, COCRDSL (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT02Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 21 | COCRDUPC.cbl | Credit card update — CICS screen for modifying card attributes (status, embossed name, expiry) | Online (CICS) | **RW:** CARDFILE via EXEC CICS READ/REWRITE | COCOM01Y, COCRDUP (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT02Y, CSSETATY, DFHAID, DFHBMSCA |
| 22 | COMEN01C.cbl | Main menu hub — CICS screen with 11 navigation options, routing via XCTL | Online (CICS) | None (navigation only) | COCOM01Y, COMEN01 (BMS), COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA |
| 23 | CORPT00C.cbl | Report request — CICS screen that submits batch JCL (INTRDRJ1/J2) via internal reader | Online (CICS) | **W:** Submits JCL to INTRDR | COCOM01Y, CORPT00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA |
| 24 | COSGN00C.cbl | Sign-on entry point — CICS screen for user authentication | Online (CICS) | **R:** USRSEC (user security file) via EXEC CICS READ | COCOM01Y, COSGN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 25 | COTRN00C.cbl | Transaction list — CICS screen with paginated transaction browse | Online (CICS) | **R:** TRANSACT via EXEC CICS STARTBR/READNEXT/READPREV/ENDBR | COCOM01Y, COTRN00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVTRA05Y, CVACT03Y, DFHAID, DFHBMSCA |
| 26 | COTRN01C.cbl | Transaction view — CICS screen for displaying single transaction details | Online (CICS) | **R:** TRANSACT via EXEC CICS READ | COCOM01Y, COTRN01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | COTRN02C.cbl | Transaction add — CICS screen for entering new transactions with validation | Online (CICS) | **W:** TRANSACT, DALYTRAN via EXEC CICS WRITE **RW:** ACCTFILE, CARDXREF | COCOM01Y, COTRN02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVTRA05Y, CVACT01Y, CVACT03Y, CSSETATY, DFHAID, DFHBMSCA |
| 28 | COUSR00C.cbl | User list — CICS admin screen with paginated user browse | Online (CICS) | **R:** USRSEC via EXEC CICS STARTBR/READNEXT | COCOM01Y, COUSR00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR01C.cbl | User add — CICS admin screen for creating new security users | Online (CICS) | **W:** USRSEC via EXEC CICS WRITE | COCOM01Y, COUSR01 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR02C.cbl | User update — CICS admin screen for modifying user credentials and type | Online (CICS) | **RW:** USRSEC via EXEC CICS READ/REWRITE | COCOM01Y, COUSR02 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | COUSR03C.cbl | User delete — CICS admin screen for removing security users | Online (CICS) | **RW:** USRSEC via EXEC CICS READ/DELETE | COCOM01Y, COUSR03 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module — IMS/DB2/MQ (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 32 | COPAUA0C.cbl | Authorization decision engine — reads MQ requests, queries IMS/VSAM, processes auth decisions, writes MQ responses | Online (CICS+IMS+MQ) | **R:** MQ request queue (MQGET), XREFFILE, ACCTFILE, CUSTFILE, IMS auth DB **W:** MQ response queue (MQPUT1) | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | COPAUS0C.cbl | Pending authorization summary — CICS screen displaying auth summaries from IMS DB | Online (CICS+IMS) | **R:** IMS PAUTDB (GU/GNP), ACCTFILE, CARDFILE, XREFFILE, CUSTFILE | COCOM01Y, COPAU00 (BMS), COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | COPAUS1C.cbl | Pending authorization detail — CICS screen for viewing/updating individual auth records in IMS | Online (CICS+IMS) | **RW:** IMS PAUTDB (GU/GNP/REPL) | COCOM01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS2C.cbl | Fraud flag update — CICS screen for marking authorizations as fraudulent (writes to DB2) | Online (CICS+DB2) | **W:** DB2 FRAUD_FLAGS table (EXEC SQL INSERT) | CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | CBPAUP0C.cbl | Expired authorization purge — batch IMS program that scans and deletes expired auth records | Batch (IMS) | **RW:** IMS PAUTDB (GN/GNP/DLET, checkpoint) | CIPAUSMY, CIPAUDTY |
| 37 | PAUDBLOD.CBL | IMS database load — batch utility to load auth summary and detail segments into IMS DB | Batch (IMS) | **R:** INFILE1, INFILE2 **W:** IMS PAUTDB (ISRT/GU) | CIPAUSMY, CIPAUDTY, PADFLPCB, PASFLPCB, PAUTBPCB, IMSFUNCS |
| 38 | PAUDBUNL.CBL | IMS database unload — batch utility to extract auth records from IMS to flat files | Batch (IMS) | **R:** IMS PAUTDB (GN/GNP) **W:** OUTFIL1, OUTFIL2 | CIPAUSMY, CIPAUDTY, PADFLPCB, PASFLPCB, PAUTBPCB, IMSFUNCS |
| 39 | DBUNLDGS.CBL | GSAM unload — batch utility using GSAM for IMS data extraction | Batch (IMS/GSAM) | **R:** IMS PAUTDB (GN/GNP) **W:** PASFILOP, PADFILOP (GSAM) | CIPAUSMY, CIPAUDTY, PADFLPCB, PASFLPCB, PAUTBPCB, IMSFUNCS |

### 2.2 Transaction Type Module — DB2 (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 40 | COTRTLIC.cbl | Transaction type list — CICS screen with DB2 cursor-based pagination of transaction types/categories | Online (CICS+DB2) | **R:** DB2 TRAN_TYPE, TRAN_CATEGORY tables (EXEC SQL DECLARE CURSOR/FETCH) | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA |
| 41 | COTRTUPC.cbl | Transaction type update — CICS screen for CRUD operations on transaction types with cascading deletes | Online (CICS+DB2) | **RW:** DB2 TRAN_TYPE, TRAN_CATEGORY (EXEC SQL SELECT/INSERT/UPDATE/DELETE) | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA |
| 42 | COBTUPDT.cbl | Batch transaction type update — standalone batch update utility for transaction types via DB2 | Batch (DB2) | **RW:** DB2 TRAN_TYPE table (EXEC SQL) | CSDB2RPY, CSDB2RWY |

### 2.3 VSAM/MQ Module (`app/app-vsam-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|-------------------|---------------------|
| 43 | COACCT01.cbl | Account inquiry via MQ — reads account data from VSAM and publishes to MQ | Online (CICS+MQ) | **R:** ACCTFILE (VSAM) **W:** MQ response queue | CVACT01Y, CMQODV, CMQMDV |
| 44 | CODATE01.cbl | Date service via MQ — responds to date-format requests over MQ | Online (CICS+MQ) | **R/W:** MQ request/response queues | CSDAT01Y, CMQODV, CMQMDV |

---

## 3. BMS Screen Maps (`app/bms/`)

| BMS Map | Paired Program | Screen Function |
|---------|---------------|-----------------|
| COACTUP.bms | COACTUPC | Account update form |
| COACTVW.bms | COACTVWC | Account view display |
| COADM01.bms | COADM01C | Admin menu |
| COBIL00.bms | COBIL00C | Bill payment form |
| COCRDLI.bms | COCRDLIC | Credit card list |
| COCRDSL.bms | COCRDSLC | Credit card view |
| COCRDUP.bms | COCRDUPC | Credit card update form |
| COMEN01.bms | COMEN01C | Main menu |
| CORPT00.bms | CORPT00C | Report request form |
| COSGN00.bms | COSGN00C | Sign-on screen |
| COTRN00.bms | COTRN00C | Transaction list |
| COTRN01.bms | COTRN01C | Transaction view |
| COTRN02.bms | COTRN02C | Transaction add form |
| COUSR00.bms | COUSR00C | User list |
| COUSR01.bms | COUSR01C | User add form |
| COUSR02.bms | COUSR02C | User update form |
| COUSR03.bms | COUSR03C | User delete confirmation |

---

## 4. JCL Job Catalog (`app/jcl/`)

### 4.1 Data Setup Jobs (VSAM Cluster Definition)

| JCL Job | Purpose | Steps |
|---------|---------|-------|
| ACCTFILE.jcl | Define account VSAM KSDS cluster and load from PS | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER → STEP15: IDCAMS REPRO (PS→VSAM) |
| CARDFILE.jcl | Define card VSAM KSDS + AIX and load from PS | CLCIFIL: Close CICS files → STEP05: DELETE cluster+AIX → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP40: DEFINE AIX → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: Reopen CICS |
| CUSTFILE.jcl | Define customer VSAM KSDS and load from PS | CLCIFIL: Close CICS → STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → OPCIFIL: Reopen |
| XREFFILE.jcl | Define card cross-reference VSAM KSDS + AIX | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO → STEP20: DEFINE AIX → STEP25: DEFINE PATH → STEP30: BLDINDEX |
| TRANFILE.jcl | Define transaction VSAM KSDS | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO |
| DISCGRP.jcl | Define disclosure group VSAM KSDS | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO |
| TCATBALF.jcl | Define transaction category balance VSAM KSDS | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO |
| TRANTYPE.jcl | Define transaction type VSAM KSDS | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO |
| TRANCATG.jcl | Define transaction category VSAM KSDS | STEP05: DELETE → STEP10: DEFINE CLUSTER → STEP15: REPRO |
| DEFCUST.jcl | Alternative customer file definition (backup) | STEP05: DELETE → STEP05: DEFINE CLUSTER |
| DEFGDGB.jcl | Define GDG base entries for versioned datasets | STEP05: IDCAMS DEFINE GDG (TRANSACT.BKUP, TRANSACT.DALY, SYSTRAN, TRANSACT.COMBINED) |
| DEFGDGD.jcl | Define GDG base for daily rejects | STEP05: IDCAMS DEFINE GDG (DALYREJS) |
| DALYREJS.jcl | Define GDG base for daily rejection file | STEP05: IDCAMS DEFINE GDG |
| REPTFILE.jcl | Define report output PS file | STEP05: IDCAMS DELETE/DEFINE |
| READACCT.jcl | Test read of account file | STEP01: PGM=CBACT01C |
| READCARD.jcl | Test read of card file | STEP01: PGM=CBACT02C |
| READCUST.jcl | Test read of customer file | STEP01: PGM=CBCUS01C |
| READXREF.jcl | Test read of cross-reference file | STEP01: PGM=CBACT03C |
| TRANIDX.jcl | Define transaction file alternate index | STEP05–STEP30: DEFINE AIX/PATH/BLDINDEX |
| ESDSRRDS.jcl | Define ESDS and RRDS VSAM clusters | STEP05–STEP20: DEFINE CLUSTER (ESDS, RRDS) |

### 4.2 Batch Processing Jobs

| JCL Job | Purpose | Steps |
|---------|---------|-------|
| POSTTRAN.jcl | Post daily transactions to master file | STEP05R: SORT daily input → STEP10R: PGM=CBTRN02C (post transactions) |
| INTCALC.jcl | Calculate interest on account balances | STEP01: PGM=CBACT04C |
| CREASTMT.JCL | Generate customer statements | DELDEF01: IDCAMS setup → STEP010: SORT transactions → STEP020: REPRO to VSAM → STEP030: Delete old output → STEP040: PGM=CBSTM03A |
| TRANREPT.jcl | Generate daily transaction report | STEP05R: REPROC backup → STEP05R: SORT → STEP10R: PGM=CBTRN03C |
| CBEXPORT.jcl | Export all master data for migration | STEP01: IDCAMS verify → STEP02: PGM=CBEXPORT |
| CBIMPORT.jcl | Import and validate data from export file | STEP01: PGM=CBIMPORT |
| COMBTRAN.jcl | Combine transaction backup + system transactions | STEP05R: SORT merge → STEP10: IDCAMS REPRO to VSAM |
| TRANBKP.jcl | Backup transaction VSAM to GDG PS | STEP05R: REPROC VSAM→PS(+1) |
| PRTCATBL.jcl | Print category balance file | STEP01: PGM=CBACT01C variant |
| WAITSTEP.jcl | Timed wait utility | WAIT: PGM=COBSWAIT |
| TXT2PDF1.JCL | Convert text statement to PDF | TXT2PDF: PGM=IKJEFT1B (TXT2PDF REXX) |

### 4.3 CICS Administration Jobs

| JCL Job | Purpose | Steps |
|---------|---------|-------|
| CLOSEFIL.jcl | Close VSAM files in CICS region | CLCIFIL: SDSF CEMT SET FIL CLO |
| OPENFIL.jcl | Open VSAM files in CICS region | OPCIFIL: SDSF CEMT SET FIL OPE |
| CBADMCDJ.jcl | Define CICS CSD resources (transactions, programs) | STEP1: PGM=DFHCSDUP |
| DUSRSECJ.jcl | Define user security file in CSD | STEP1: PGM=DFHCSDUP |
| FTPJCL.JCL | FTP file transfer utility | STEP1: PGM=FTP |
| INTRDRJ1.JCL | Internal reader job 1 — backup and submit INTRDRJ2 | IDCAMS: REPRO backup → STEP01: IEBGENER to INTRDR |
| INTRDRJ2.JCL | Internal reader job 2 — second-stage report processing | IDCAMS: REPRO processing |

### 4.4 Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`)

| JCL Job | Purpose | Steps |
|---------|---------|-------|
| CBPAUP0J.jcl | Execute expired authorization purge (IMS BMP) | STEP01: PGM=DFSRRC00 (BMP, runs CBPAUP0C) |
| DBPAUTP0.jcl | Unload IMS auth database to flat file | STEPDEL: IEFBR14 cleanup → UNLOAD: PGM=DFSRRC00 (runs PAUDBUNL) |
| LOADPADB.JCL | Load IMS auth database from flat files | STEP01: PGM=DFSRRC00 (runs PAUDBLOD) |
| UNLDGSAM.JCL | GSAM unload of IMS auth database | STEP01: PGM=DFSRRC00 (runs DBUNLDGS) |
| UNLDPADB.JCL | Alternative IMS auth unload to multiple files | STEP0: IEFBR14 alloc → STEP01: PGM=DFSRRC00 (runs PAUDBUNL) |

### 4.5 Sub-Application JCL (`app/app-transaction-type-db2/jcl/`)

| JCL Job | Purpose | Steps |
|---------|---------|-------|
| CREADB21.jcl | Create DB2 objects (tables, indexes, plans) for transaction types | FREEPLN: free plan → DRPTBL: drop tables → CRETBL: create tables → BIND: bind plans |
| MNTTRDB2.jcl | Maintain transaction type DB2 data | STEP01: PGM=COBTUPDT |
| TRANEXTR.jcl | Extract transaction types from DB2 to flat file | STEP01: DB2 UNLOAD utility |

---

## 5. Classification Summary

| Category | Count | Programs |
|----------|-------|----------|
| Online (CICS only) | 16 | COSGN00C, COADM01C, COMEN01C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COUSR00C–03C |
| Online (CICS+IMS) | 3 | COPAUS0C, COPAUS1C, COPAUS2C |
| Online (CICS+DB2) | 2 | COTRTLIC, COTRTUPC |
| Online (CICS+MQ) | 2 | COACCT01, CODATE01 |
| Online (CICS+IMS+MQ+DB2) | 1 | COPAUA0C |
| Batch (VSAM) | 12 | CBACT01C–04C, CBCUS01C, CBEXPORT, CBIMPORT, CBSTM03A/B, CBTRN01C–03C |
| Batch (IMS) | 4 | CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS |
| Batch (DB2) | 1 | COBTUPDT |
| Utility | 2 | COBSWAIT, CSUTLDTC |
| **Total** | **44** | |
