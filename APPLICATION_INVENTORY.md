# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Generated:** 2026-05-27  
> **Repository:** `uc-legacy-modernization-cobol-to-java`  
> **Application:** AWS CardDemo — Credit Card Management System  
> **Total Programs:** 42 COBOL (31 core + 11 sub-application) | 38 JCL Jobs | 21 BMS Maps | 30 Copybooks

---

## 1. Core COBOL Programs (`app/cbl/`)

### 1.1 Batch Programs

| # | Filename | Lines | Purpose | Key I/O (Files Read/Written) | Copybooks Referenced |
|---|----------|-------|---------|------------------------------|---------------------|
| 1 | **CBACT01C.cbl** | 430 | Read Account VSAM file; write to flat, array, and variable-length output files | R: ACCTFILE (KSDS) · W: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | **CBACT02C.cbl** | 178 | Read and display Card data file | R: CARDFILE (KSDS) | CVACT02Y |
| 3 | **CBACT03C.cbl** | 178 | Read and display Card-Account Cross-Reference file | R: XREFFILE (KSDS) | CVACT03Y |
| 4 | **CBACT04C.cbl** | 652 | Interest Calculator — compute interest on account balances by transaction category, using disclosure group rates | R: TCATBALF, XREFFILE, ACCTFILE, DISCGRP · W: TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | **CBCUS01C.cbl** | 178 | Read and display Customer data file | R: CUSTFILE (KSDS) | CVCUS01Y |
| 6 | **CBEXPORT.cbl** | 582 | Export all CardDemo data (customers, accounts, xrefs, transactions, cards) into a multi-record VSAM export file for branch migration | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · W: EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | **CBIMPORT.cbl** | 487 | Import multi-record export file back into normalized target files with validation and error reporting | R: EXPFILE · W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | **CBTRN01C.cbl** | 494 | Post daily transactions — validate each daily transaction record against customer, card, account, and xref files | R: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE · W: TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | **CBTRN02C.cbl** | 731 | Post daily transactions — update account balances, transaction category balances, and reject invalid transactions | R: DALYTRAN, XREFFILE, ACCTFILE · W: TRANFILE, DALYREJS, TCATBALF | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | **CBTRN03C.cbl** | 649 | Print transaction detail report with type/category lookups and page/account/grand totals | R: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM · W: TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | **CBSTM03A.CBL** | 924 | Print account statements from transaction data — produces text and HTML statement output | R: (via CBSTM03B) TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE · W: STMTFILE, HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 12 | **CBSTM03B.CBL** | 230 | Subroutine for CBSTM03A — performs file I/O for transaction statement processing | R: TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | _(none in app/cpy)_ |
| 13 | **COBSWAIT.cbl** | 41 | Utility — wait/sleep for a specified number of centiseconds (scheduling delay) | _(none)_ | _(none)_ |
| 14 | **CSUTLDTC.cbl** | 157 | Date utility — convert dates using IBM CEEDAYS intrinsic | _(none)_ | _(none)_ |

### 1.2 Online (CICS) Programs

| # | Filename | Lines | Purpose | CICS Cmds | Key I/O (VSAM via CICS) | Copybooks Referenced |
|---|----------|-------|---------|-----------|------------------------|---------------------|
| 15 | **COSGN00C.cbl** | 260 | Sign-on screen — authenticate users against USRSEC file | 10 | R: USRSEC | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | **COMEN01C.cbl** | 308 | Main menu for regular users — route to 11 sub-functions | 7 | _(navigation only)_ | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | **COADM01C.cbl** | 288 | Admin menu for administrator users — route to 6 admin functions | 7 | _(navigation only)_ | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | **COACTVWC.cbl** | 941 | View account details with card and customer information | 15 | R: ACCTDAT, CARDDAT, CUSTDAT, CARDAIX | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 19 | **COACTUPC.cbl** | 4236 | Update account details — full field-level validation with attribute-based error highlighting | 17 | R/W: ACCTDAT, CARDDAT, CUSTDAT, CARDAIX | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY, CSSTRPFY, CSUTLDPY |
| 20 | **COCRDLIC.cbl** | 1459 | List credit cards with browse/page forward/backward | 18 | R: CARDDAT, CARDAIX | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 21 | **COCRDSLC.cbl** | 887 | View credit card details with customer information | 14 | R: CARDDAT, CUSTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 22 | **COCRDUPC.cbl** | 1560 | Update credit card details with validation | 12 | R/W: CARDDAT, CUSTDAT | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 23 | **COTRN00C.cbl** | 699 | List transactions from TRANSACT file with browse | 10 | R: TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | **COTRN01C.cbl** | 330 | View a single transaction detail | 5 | R: TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | **COTRN02C.cbl** | 783 | Add a new transaction to the TRANSACT file | 11 | R: ACCTDAT, CARDXREF · W: TRANSACT | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 26 | **COBIL00C.cbl** | 572 | Bill payment — pay account balance in full and create payment transaction | 13 | R/W: ACCTDAT, TRANSACT · R: CARDXREF | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | **CORPT00C.cbl** | 649 | Submit batch transaction reports from online — invokes CSUTLDTC for date validation | 7 | W: TRANSACT (report params) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 28 | **COUSR00C.cbl** | 695 | List all users from USRSEC file | 11 | R: USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | **COUSR01C.cbl** | 299 | Add a new user (regular or admin) to USRSEC file | 5 | W: USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | **COUSR02C.cbl** | 414 | Update an existing user in USRSEC file | 6 | R/W: USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | **COUSR03C.cbl** | 359 | Delete a user from USRSEC file | 6 | R/W: USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### 2.1 Authorization / IMS / Db2 / MQ (`app/app-authorization-ims-db2-mq/`)

| # | Filename | Lines | Type | Purpose | Technologies |
|---|----------|-------|------|---------|-------------|
| 32 | **CBPAUP0C.cbl** | 386 | Batch IMS | Delete expired pending authorization messages from IMS database | IMS DL/I |
| 33 | **COPAUA0C.cbl** | 1026 | CICS IMS MQ | Card authorization decision — reads authorization request from MQ, validates against IMS DB, returns approval/decline | CICS, IMS DL/I, MQ |
| 34 | **COPAUS0C.cbl** | 1032 | CICS IMS BMS | Summary view of pending authorization messages | CICS, IMS DL/I, BMS |
| 35 | **COPAUS1C.cbl** | 604 | CICS IMS BMS | Detail view of a single authorization message | CICS, IMS DL/I, BMS |
| 36 | **COPAUS2C.cbl** | 244 | CICS IMS Db2 | Mark authorization message as fraud via Db2 update | CICS, Db2 SQL |
| 37 | **DBUNLDGS.CBL** | 366 | Batch IMS | Generic IMS segment unload utility | IMS DL/I |
| 38 | **PAUDBLOD.CBL** | 369 | Batch IMS | Load authorization data into IMS database | IMS DL/I |
| 39 | **PAUDBUNL.CBL** | 317 | Batch IMS | Unload authorization data from IMS database | IMS DL/I |

### 2.2 Transaction Type / Db2 (`app/app-transaction-type-db2/`)

| # | Filename | Lines | Type | Purpose | Technologies |
|---|----------|-------|------|---------|-------------|
| 40 | **COTRTLIC.cbl** | 2098 | CICS Db2 | List transaction types from Db2 for update/delete | CICS, Db2 SQL |
| 41 | **COTRTUPC.cbl** | 1702 | CICS Db2 | Accept and process transaction type updates in Db2 | CICS, Db2 SQL |
| 42 | **COBTUPDT.cbl** | 237 | Batch Db2 | Batch update of transaction types in Db2 | Db2 SQL |

---

## 3. JCL Job Catalog (`app/jcl/`)

### 3.1 VSAM File Definition & Data Load Jobs

| # | Job Name | Lines | Purpose | Step Sequence | Key Datasets |
|---|----------|-------|---------|--------------|-------------|
| 1 | **ACCTFILE.jcl** | 65 | Delete/define/load Account VSAM KSDS | STEP05→IDCAMS(delete) · STEP10→IDCAMS(define) · STEP15→IDCAMS(repro) | ACCTDATA.PS → ACCTDATA.VSAM.KSDS |
| 2 | **CARDFILE.jcl** | 128 | Delete/define/load Card VSAM KSDS + AIX on ACCT-ID | CLCIFIL→SDSF(close) · STEP05→IDCAMS(del) · STEP10→IDCAMS(def) · STEP15→IDCAMS(repro) · STEP40→IDCAMS(AIX def) · STEP50→IDCAMS(PATH def) · STEP60→IDCAMS(BLDINDEX) · OPCIFIL→SDSF(open) | CARDDATA.PS → CARDDATA.VSAM.KSDS + AIX |
| 3 | **CUSTFILE.jcl** | 84 | Delete/define/load Customer VSAM KSDS | CLCIFIL→SDSF(close) · STEP05→IDCAMS(del) · STEP10→IDCAMS(def) · STEP15→IDCAMS(repro) · OPCIFIL→SDSF(open) | CUSTDATA.PS → CUSTDATA.VSAM.KSDS |
| 4 | **XREFFILE.jcl** | 106 | Delete/define/load Cross-Reference VSAM KSDS + AIX on ACCT-ID | STEP05–30→IDCAMS (del/def/repro/AIX/PATH/BLDINDEX) | CARDXREF.PS → CARDXREF.VSAM.KSDS + AIX |
| 5 | **TRANFILE.jcl** | 125 | Delete/define/load Transaction Master VSAM KSDS + AIX | CLCIFIL→SDSF(close) · STEP05–30→IDCAMS · OPCIFIL→SDSF(open) | DALYTRAN.PS.INIT → TRANSACT.VSAM.KSDS + AIX |
| 6 | **TCATBALF.jcl** | 65 | Delete/define/load Transaction Category Balance VSAM KSDS | STEP05→IDCAMS(del) · STEP10→IDCAMS(def) · STEP15→IDCAMS(repro) | TCATBALF.PS → TCATBALF.VSAM.KSDS |
| 7 | **DISCGRP.jcl** | 65 | Delete/define/load Disclosure Group VSAM KSDS | STEP05→IDCAMS(del) · STEP10→IDCAMS(def) · STEP15→IDCAMS(repro) | DISCGRP.PS → DISCGRP.VSAM.KSDS |
| 8 | **TRANTYPE.jcl** | 65 | Delete/define/load Transaction Type VSAM KSDS | STEP05→IDCAMS(del) · STEP10→IDCAMS(def) · STEP15→IDCAMS(repro) | TRANTYPE.PS → TRANTYPE.VSAM.KSDS |
| 9 | **TRANCATG.jcl** | 65 | Delete/define/load Transaction Category VSAM KSDS | STEP05→IDCAMS(del) · STEP10→IDCAMS(def) · STEP15→IDCAMS(repro) | TRANCATG.PS → TRANCATG.VSAM.KSDS |
| 10 | **DUSRSECJ.jcl** | 92 | Delete/define/load User Security VSAM KSDS | PREDEL→IEFBR14 · STEP01→IEBGENER · STEP02→IDCAMS(def) · STEP03→IDCAMS(repro) | USRSEC.PS → USRSEC.VSAM.KSDS |
| 11 | **DEFCUST.jcl** | 47 | Define Customer data file (alternate definition) | STEP05→IDCAMS × 2 | CUSTDATA cluster |
| 12 | **ESDSRRDS.jcl** | 124 | Define ESDS and RRDS VSAM variants for testing | PREDEL→IEFBR14 · STEP01–05 | ESDSRRDS, USRSEC.VSAM.ESDS/RRDS |
| 13 | **TRANIDX.jcl** | 58 | Define Alternate Index on Transaction Master | STEP20–30→IDCAMS | TRANSACT.VSAM.AIX |

### 3.2 Batch Processing Jobs

| # | Job Name | Lines | Purpose | Step Sequence | Key Datasets |
|---|----------|-------|---------|--------------|-------------|
| 14 | **READACCT.jcl** | 50 | Read account file (runs CBACT01C) | PREDEL→IEFBR14 · STEP05→CBACT01C | ACCTDATA.VSAM.KSDS → PSCOMP, ARRYPS, VBPS |
| 15 | **READCARD.jcl** | 31 | Read card file (runs CBACT02C) | STEP05→CBACT02C | CARDDATA.VSAM.KSDS |
| 16 | **READCUST.jcl** | 30 | Read customer file (runs CBCUS01C) | STEP05→CBCUS01C | CUSTDATA.VSAM.KSDS |
| 17 | **READXREF.jcl** | 31 | Read cross-reference file (runs CBACT03C) | STEP05→CBACT03C | CARDXREF.VSAM.KSDS |
| 18 | **POSTTRAN.jcl** | 45 | Post daily transactions (runs CBTRN02C) | STEP15→CBTRN02C | DALYTRAN.PS → TRANSACT.VSAM.KSDS, DALYREJS(+1), TCATBALF.VSAM.KSDS |
| 19 | **INTCALC.jcl** | 44 | Calculate interest (runs CBACT04C) | STEP15→CBACT04C | TCATBALF, XREFFILE, ACCTFILE, DISCGRP → SYSTRAN(+1) |
| 20 | **TRANREPT.jcl** | 84 | Transaction detail report (runs CBTRN03C) | STEP05R→REPROC · STEP05R→SORT · STEP10R→CBTRN03C | TRANSACT → TRANREPT(+1) |
| 21 | **TRANBKP.jcl** | 71 | Backup and clear Transaction Master | STEP05R→REPROC · STEP05→IDCAMS(repro) · STEP10→IDCAMS(del/def) | TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1) |
| 22 | **COMBTRAN.jcl** | 52 | Combine system transactions with backup for monthly rollup | STEP05R→SORT · STEP10→IDCAMS | SYSTRAN(0) + TRANSACT.BKUP(0) → TRANSACT.COMBINED(+1) |
| 23 | **PRTCATBL.jcl** | 66 | Print Transaction Category Balance report | DELDEF→IEFBR14 · STEP05R→REPROC · STEP10R→SORT | TCATBALF.VSAM.KSDS → TCATBALF.REPT |
| 24 | **CBEXPORT.jcl** | 72 | Branch migration export (runs CBEXPORT) | STEP01→IDCAMS(def) · STEP02→CBEXPORT | All VSAM → EXPORT.DATA |
| 25 | **CBIMPORT.jcl** | 68 | Branch migration import (runs CBIMPORT) | STEP01→CBIMPORT | EXPORT.DATA → CUSTDATA/ACCTDATA/CARDXREF/TRANSACT.IMPORT |
| 26 | **CREASTMT.JCL** | 97 | Create account statements (runs CBSTM03A) | DELDEF01→IDCAMS · STEP010→SORT · STEP020→IDCAMS · STEP030→IEFBR14 · STEP040→CBSTM03A | TRANSACT → STATEMNT.PS, STATEMNT.HTML |

### 3.3 CICS Control & Infrastructure Jobs

| # | Job Name | Lines | Purpose | Step Sequence |
|---|----------|-------|---------|--------------|
| 27 | **CLOSEFIL.jcl** | 34 | Close CICS files (CEMT SET FIL CLO) | CLCIFIL→SDSF |
| 28 | **OPENFIL.jcl** | 34 | Open CICS files (CEMT SET FIL OPE) | OPCIFIL→SDSF |
| 29 | **WAITSTEP.jcl** | 27 | Wait step for job scheduling delays (runs COBSWAIT) | WAIT→COBSWAIT |
| 30 | **CBADMCDJ.jcl** | 167 | Define all CICS CSD resources (programs, mapsets, transactions, files) | STEP1→DFHCSDUP |

### 3.4 GDG & Utility Jobs

| # | Job Name | Lines | Purpose | Step Sequence |
|---|----------|-------|---------|--------------|
| 31 | **DEFGDGB.jcl** | 63 | Define GDG bases for backup/history datasets | STEP05→IDCAMS |
| 32 | **DEFGDGD.jcl** | 94 | Define GDG bases for Db2-related reference data + initial load | Multiple IDCAMS + IEBGENER steps |
| 33 | **DALYREJS.jcl** | 32 | Define GDG base for daily rejection files | STEP05→IDCAMS |
| 34 | **REPTFILE.jcl** | 32 | Define GDG base for report output files | STEP05→IDCAMS |
| 35 | **FTPJCL.JCL** | 42 | FTP file transfer utility | STEP1→FTP |
| 36 | **INTRDRJ1.JCL** | 19 | Internal reader job chaining (step 1) | IDCAMS→IDCAMS · STEP01→IEBGENER |
| 37 | **INTRDRJ2.JCL** | 14 | Internal reader job chaining (step 2) | IDCAMS→IDCAMS |
| 38 | **TXT2PDF1.JCL** | 41 | Convert text statement to PDF | TXT2PDF→IKJEFT1B |

---

## 4. Control-M Scheduler (`app/scheduler/CardDemo.controlm`)

### Daily Cycle: `DAILY-TransactionBackup`
1. **CLOSEFIL** → Close CICS files for batch access
2. **TRANBKP** → Backup transaction master (depends on CLOSEFIL)
3. **WAITSTEP** → Scheduling delay (depends on TRANBKP)
4. **OPENFIL** → Reopen CICS files (depends on WAITSTEP)

### Weekly Cycle: `WEEKLY-TransactionTypesDBRefresh`
1. **MNTTRDB2** → Refresh Db2 transaction type tables
2. _(triggers DisclosureGroupsRefresh)_

### Weekly Cycle: `WEEKLY-DisclosureGroupsRefresh` (depends on MNTTRDB2)
1. **CLOSEFIL** → Close CICS files
2. **DISCGRP** → Refresh Disclosure Group VSAM
3. **WAITSTEP** → Scheduling delay
4. **OPENFIL** → Reopen CICS files
5. **TRANEXTR** → Extract transaction types from Db2 to VSAM

### Monthly Cycle: `MONTHLY-InterestCalculation`
1. **CLOSEFIL** → Close CICS files
2. **INTCALC** → Run interest calculation (CBACT04C)
3. **COMBTRAN** → Combine monthly transactions
4. **WAITSTEP** → Scheduling delay
5. **OPENFIL** → Reopen CICS files

---

## 5. Estate Summary

| Metric | Count |
|--------|-------|
| Total COBOL programs | 42 |
| Batch programs | 17 |
| Online (CICS) programs | 19 |
| CICS+IMS programs | 4 |
| CICS+Db2 programs | 3 |
| Batch+IMS programs | 3 |
| Utility/subroutine programs | 3 |
| BMS screen maps | 21 |
| Copybooks | 30 (core) + 13 (sub-app) |
| JCL jobs | 38 (core) + 8 (sub-app) |
| VSAM KSDS clusters | 9 |
| VSAM Alternate Indexes | 3 |
| GDG bases | 8+ |
| Db2 tables | 4 (TRNTYPE, TRNCATG, AUTHFRDS, XAUTHFRD) |
| IMS databases | 3+ (DBPAUTP0, PADFLDBD, PASFLDBD) |
| MQ queues | 2 (request/reply) |
| Total lines of COBOL | ~27,000+ |
