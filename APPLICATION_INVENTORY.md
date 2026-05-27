# APPLICATION_INVENTORY.md — CardDemo COBOL Estate

> Auto-generated analysis of the CardDemo credit-card management system.
> Source: `app/cbl/`, `app/cpy/`, `app/jcl/`

---

## Table of Contents

1. [System Overview](#system-overview)
2. [COBOL Programs — Batch](#cobol-programs--batch)
3. [COBOL Programs — Online (CICS)](#cobol-programs--online-cics)
4. [COBOL Programs — Utilities / Subroutines](#cobol-programs--utilities--subroutines)
5. [JCL Job Catalog](#jcl-job-catalog)
6. [Cross-Reference: Copybook Usage](#cross-reference-copybook-usage)
7. [VSAM File Inventory](#vsam-file-inventory)

---

## System Overview

CardDemo is an AWS mainframe modernization sample application implementing a **credit-card account management system**. It consists of:

| Metric | Count |
|--------|-------|
| COBOL programs | 31 |
| Copybooks | 30 |
| JCL jobs | 38 |
| Business domains | 8 (Account, Card, Customer, Transaction, Reporting, Security, Bill Pay, Data Migration) |

**Architecture:** Batch programs (prefix `CB`) run as standalone executables; online programs (prefix `CO`) execute under CICS with BMS map-driven 3270 terminal UIs. All data is stored in VSAM KSDS files — no DB2 tables are accessed.

---

## COBOL Programs — Batch

### CBACT01C.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Read account master file and produce multiple output formats (sequential, array/OCCURS, variable-length records) |
| **Classification** | Batch |
| **Lines** | 430 |
| **Files Read** | ACCTFILE (VSAM KSDS, indexed sequential access, key=ACCT-ID 11 bytes) |
| **Files Written** | OUT-FILE (sequential), ARRY-FILE (OCCURS array output), VBRC-FILE (variable-length records) |
| **Copybooks** | CVACT01Y, CODATECN |

### CBACT02C.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Read card data file sequentially and display card records |
| **Classification** | Batch |
| **Files Read** | CARDFILE (VSAM KSDS, indexed sequential, key=CARD-NUM 16 bytes) |
| **Files Written** | None (display only) |
| **Copybooks** | CVACT02Y |

### CBACT03C.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Read card-to-account cross-reference file sequentially |
| **Classification** | Batch |
| **Files Read** | XREFFILE (VSAM KSDS, indexed sequential, key=XREF-CARD-NUM 16 bytes) |
| **Files Written** | None (display only) |
| **Copybooks** | CVACT03Y |

### CBACT04C.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Calculate interest charges on account balances using disclosure group rates; post interest transactions |
| **Classification** | Batch |
| **Lines** | 652 |
| **Files Read** | TCATBALF (transaction category balance, sequential), XREFFILE (random via alternate key FD-XREF-ACCT-ID), ACCTFILE (random), DISCGRP (disclosure group rates, random) |
| **Files Written** | TRANSACT (new interest transactions, sequential write) |
| **Copybooks** | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |

### CBCUS01C.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Read customer master file sequentially and display customer records |
| **Classification** | Batch |
| **Files Read** | CUSTFILE (VSAM KSDS, indexed sequential, key=CUST-ID 9 bytes) |
| **Files Written** | None (display only) |
| **Copybooks** | CVCUS01Y |

### CBEXPORT.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Export all entity data (customers, accounts, cards, transactions, cross-references) to a single multi-record export file for branch migration |
| **Classification** | Batch |
| **Lines** | 582 |
| **Files Read** | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE (all VSAM KSDS, sequential) |
| **Files Written** | EXPFILE (500-byte multi-record export, VSAM KSDS with record-type prefix) |
| **Copybooks** | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |

### CBIMPORT.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Import data from multi-record export file, validate, and split into separate entity files for target system loading |
| **Classification** | Batch |
| **Lines** | 487 |
| **Files Read** | EXPFILE (500-byte multi-record export) |
| **Files Written** | CUSTOUT (LRECL=500), ACCTOUT (LRECL=300), XREFOUT (LRECL=50), TRNXOUT (LRECL=350), CARDOUT, ERROUT (LRECL=132, rejected records) |
| **Copybooks** | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |

### CBSTM03A.CBL
| Attribute | Value |
|-----------|-------|
| **Purpose** | Generate account statements in text and HTML format from transaction history; main driver program |
| **Classification** | Batch |
| **Lines** | 924 |
| **Files Read** | (Delegates file I/O to subroutine CBSTM03B) |
| **Files Written** | Statement output files (text and HTML) |
| **Subprogram Calls** | CBSTM03B (via CALL) |
| **Copybooks** | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |

### CBSTM03B.CBL
| Attribute | Value |
|-----------|-------|
| **Purpose** | Subroutine for CBSTM03A — performs file I/O operations for statement generation |
| **Classification** | Batch (subroutine, LINKAGE SECTION) |
| **Lines** | 230 |
| **Files Read** | TRNXFILE (sequential), XREFFILE (random), CUSTFILE (random), ACCTFILE (random) |
| **Files Written** | None (returns data via LINKAGE SECTION) |
| **Copybooks** | (inherits from caller CBSTM03A) |

### CBTRN01C.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Post daily transactions to master files — update account balances, transaction history, and cross-references |
| **Classification** | Batch |
| **Lines** | 494 |
| **Files Read** | DALYTRAN (daily transaction input, sequential) |
| **Files Updated** | CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANSACT (all random access for updates) |
| **Copybooks** | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |

### CBTRN02C.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Post daily transactions with enhanced validation — rejects invalid transactions to a rejection file; updates transaction category balances |
| **Classification** | Batch |
| **Lines** | 731 |
| **Files Read** | DALYTRAN (daily transactions, sequential), XREFFILE (random) |
| **Files Updated** | TRANSACT (random), ACCTFILE (random), TCATBAL (category balance, random) |
| **Files Written** | DALYREJS (rejected transactions with validation trailer) |
| **Copybooks** | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |

### CBTRN03C.cbl
| Attribute | Value |
|-----------|-------|
| **Purpose** | Generate daily transaction detail report with type/category descriptions, page/account/grand totals |
| **Classification** | Batch |
| **Lines** | 649 |
| **Files Read** | TRANFILE (sequential), XREFFILE (random), TRANTYPE-FILE (random), TRANCATG-FILE (random), DATE-PARMS-FILE |
| **Files Written** | TRANREPT (report output, 133-byte print lines) |
| **Copybooks** | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |

---

## COBOL Programs — Online (CICS)

All online programs use CICS EXEC commands for terminal I/O (SEND MAP / RECEIVE MAP), file access (READ / WRITE / REWRITE / DELETE), and program control (XCTL / RETURN). They share COMMAREA for inter-program state passing.

### COSGN00C.cbl — Sign-On
| Attribute | Value |
|-----------|-------|
| **Purpose** | Entry point for the application; validates user ID and password against USRSEC file; routes to regular or admin menu based on user type |
| **Classification** | Online (CICS) |
| **Lines** | 260 |
| **CICS Transaction** | CC00 |
| **Files Accessed** | USRSEC (READ, user security file) |
| **BMS Map** | COSGN00 |
| **Copybooks** | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### COMEN01C.cbl — Main Menu (Regular User)
| Attribute | Value |
|-----------|-------|
| **Purpose** | Display main menu with 11 navigation options; route to selected sub-program via XCTL |
| **Classification** | Online (CICS) |
| **Lines** | 308 |
| **Files Accessed** | None (menu navigation only) |
| **BMS Map** | COMEN01 |
| **Copybooks** | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### COADM01C.cbl — Admin Menu
| Attribute | Value |
|-----------|-------|
| **Purpose** | Display admin menu with 6 options (user CRUD, card list, card update); route to selected sub-program |
| **Classification** | Online (CICS) |
| **Lines** | 288 |
| **CICS Transaction** | CCDM |
| **Files Accessed** | None (menu navigation only) |
| **BMS Map** | COADM01 |
| **Copybooks** | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### COACTVWC.cbl — View Account
| Attribute | Value |
|-----------|-------|
| **Purpose** | Display account details with associated customer and card information; supports account filter |
| **Classification** | Online (CICS) |
| **Lines** | 941 |
| **Files Accessed** | ACCTDAT (READ), CARDDAT/CARDAIX (READ), CUSTDAT (READ), CCXREF/CXACAIX (READ) |
| **BMS Map** | COACTVW |
| **Copybooks** | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |

### COACTUPC.cbl — Update Account
| Attribute | Value |
|-----------|-------|
| **Purpose** | Update account details with extensive field validation (dates, phone numbers, state codes, credit limits); the largest program in the system |
| **Classification** | Online (CICS) |
| **Lines** | 4,236 |
| **Files Accessed** | ACCTDAT (READ/REWRITE), CUSTDAT (READ/REWRITE), CCXREF/CXACAIX (READ) |
| **BMS Map** | COACTUP |
| **Copybooks** | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY, CSSTRPFY, CSUTLDPY |

### COCRDLIC.cbl — List Credit Cards
| Attribute | Value |
|-----------|-------|
| **Purpose** | List all credit cards or filter by account; supports pagination (PF7/PF8); role-based display (admin sees all, regular user sees own cards only) |
| **Classification** | Online (CICS) |
| **Lines** | 1,459 |
| **Files Accessed** | CARDDAT (READ NEXT, browse), CARDAIX (READ via alternate index) |
| **BMS Map** | COCRDLI |
| **Copybooks** | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |

### COCRDSLC.cbl — View Credit Card Detail
| Attribute | Value |
|-----------|-------|
| **Purpose** | Display detailed information for a single credit card including associated customer data |
| **Classification** | Online (CICS) |
| **Lines** | 887 |
| **Files Accessed** | CARDDAT (READ), CUSTDAT (READ), CCXREF (READ) |
| **BMS Map** | COCRDSL |
| **Copybooks** | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |

### COCRDUPC.cbl — Update Credit Card
| Attribute | Value |
|-----------|-------|
| **Purpose** | Update credit card details (embossed name, expiration, status) with input validation |
| **Classification** | Online (CICS) |
| **Lines** | 1,560 |
| **Files Accessed** | CARDDAT (READ/REWRITE), CUSTDAT (READ), CCXREF (READ) |
| **BMS Map** | COCRDUP |
| **Copybooks** | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |

### COBIL00C.cbl — Bill Payment
| Attribute | Value |
|-----------|-------|
| **Purpose** | Process account balance payment — creates a payment transaction and updates account balance |
| **Classification** | Online (CICS) |
| **Lines** | 572 |
| **Files Accessed** | ACCTDAT (READ/REWRITE), TRANSACT (WRITE), CCXREF (READ) |
| **BMS Map** | COBIL00 |
| **Copybooks** | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |

### COTRN00C.cbl — List Transactions
| Attribute | Value |
|-----------|-------|
| **Purpose** | List transactions with pagination (PF7/PF8); filter by account or card number |
| **Classification** | Online (CICS) |
| **Lines** | 699 |
| **Files Accessed** | TRANSACT (STARTBR/READNEXT/ENDBR, browse) |
| **BMS Map** | COTRN00 |
| **Copybooks** | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |

### COTRN01C.cbl — View Transaction Detail
| Attribute | Value |
|-----------|-------|
| **Purpose** | Display detailed information for a single transaction |
| **Classification** | Online (CICS) |
| **Lines** | 330 |
| **Files Accessed** | TRANSACT (READ) |
| **BMS Map** | COTRN01 |
| **Copybooks** | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |

### COTRN02C.cbl — Add Transaction
| Attribute | Value |
|-----------|-------|
| **Purpose** | Create a new transaction with validation; calls CSUTLDTC for date conversion; updates account balance |
| **Classification** | Online (CICS) |
| **Lines** | 783 |
| **Files Accessed** | TRANSACT (WRITE), ACCTDAT (READ/REWRITE), CCXREF (READ) |
| **Subprogram Calls** | CSUTLDTC (date validation via LINK) |
| **BMS Map** | COTRN02 |
| **Copybooks** | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |

### CORPT00C.cbl — Transaction Reporting
| Attribute | Value |
|-----------|-------|
| **Purpose** | Online interface to submit batch transaction report job; writes JCL to transient data queue (TDQ) for INTRDR submission |
| **Classification** | Online (CICS) |
| **Lines** | 649 |
| **Files Accessed** | TRANSACT (STARTBR/READPREV, to find date range) |
| **TDQ Output** | Writes JCL to TDQ for internal reader job submission |
| **BMS Map** | CORPT00 |
| **Copybooks** | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |

### COUSR00C.cbl — List Users
| Attribute | Value |
|-----------|-------|
| **Purpose** | List all users from security file with pagination |
| **Classification** | Online (CICS) |
| **Lines** | 695 |
| **Files Accessed** | USRSEC (STARTBR/READNEXT/ENDBR, browse) |
| **BMS Map** | COUSR00 |
| **Copybooks** | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### COUSR01C.cbl — Add User
| Attribute | Value |
|-----------|-------|
| **Purpose** | Create a new user in the security file with validation (user ID uniqueness, required fields) |
| **Classification** | Online (CICS) |
| **Lines** | 299 |
| **Files Accessed** | USRSEC (READ/WRITE) |
| **BMS Map** | COUSR01 |
| **Copybooks** | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### COUSR02C.cbl — Update User
| Attribute | Value |
|-----------|-------|
| **Purpose** | Modify existing user details (name, password, type) in the security file |
| **Classification** | Online (CICS) |
| **Lines** | 414 |
| **Files Accessed** | USRSEC (READ/REWRITE) |
| **BMS Map** | COUSR02 |
| **Copybooks** | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### COUSR03C.cbl — Delete User
| Attribute | Value |
|-----------|-------|
| **Purpose** | Remove a user from the security file with confirmation |
| **Classification** | Online (CICS) |
| **Lines** | 359 |
| **Files Accessed** | USRSEC (READ/DELETE) |
| **BMS Map** | COUSR03 |
| **Copybooks** | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## COBOL Programs — Utilities / Subroutines

### CSUTLDTC.cbl — Date Conversion Utility
| Attribute | Value |
|-----------|-------|
| **Purpose** | Convert and validate dates using IBM CEEDAYS API (Lillian date format); returns severity-coded validation result |
| **Classification** | Batch utility (callable subroutine via LINKAGE SECTION) |
| **Lines** | 157 |
| **API Calls** | CEEDAYS (Language Environment date intrinsic) |
| **Parameters** | LS-DATE (input date), LS-DATE-FORMAT (format mask), LS-RESULT (output message) |
| **Called By** | COTRN02C |

### COBSWAIT.cbl — Wait Utility
| Attribute | Value |
|-----------|-------|
| **Purpose** | Pause execution for a specified number of centiseconds; used for job scheduling delays |
| **Classification** | Batch utility |
| **Lines** | 41 |
| **API Calls** | MVSWAIT (system wait service) |
| **Parameters** | Accepts centisecond count via PARM |

---

## JCL Job Catalog

### Data File Definition Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **ACCTFILE.jcl** | Delete/define/load Account VSAM KSDS | STEP05: IDCAMS DELETE cluster · STEP10: IDCAMS DEFINE CLUSTER (keys=11,0, recsize=300) · STEP15: IDCAMS REPRO from flat file |
| **CARDFILE.jcl** | Delete/define/load Card VSAM KSDS with alternate index | CLCIFIL: SDSF close CICS files (CARDDAT, CARDAIX) · STEP05: IDCAMS DELETE cluster + AIX · STEP10: IDCAMS DEFINE CLUSTER (keys=16,0, recsize=150) · STEP15: IDCAMS REPRO from flat file · STEP40: DEFINE ALTERNATEINDEX on acct-id (keys=11,16, nonunique) · STEP50: DEFINE PATH · STEP60: BLDINDEX · OPCIFIL: SDSF open CICS files |
| **CUSTFILE.jcl** | Delete/define/load Customer VSAM KSDS | CLCIFIL: SDSF close CICS file (CUSTDAT) · STEP05: IDCAMS DELETE · STEP10: IDCAMS DEFINE CLUSTER (keys=9,0, recsize=500) · STEP15: IDCAMS REPRO · OPCIFIL: SDSF open CICS file |
| **XREFFILE.jcl** | Delete/define/load Cross-Reference VSAM KSDS with alternate index | STEP05: IDCAMS DELETE cluster + AIX · STEP10: IDCAMS DEFINE CLUSTER (keys=16,0, recsize=50) · STEP15: IDCAMS REPRO · STEP20: DEFINE ALTERNATEINDEX on acct-id (keys=11,16) · STEP25: DEFINE PATH · STEP30: BLDINDEX |
| **TRANFILE.jcl** | Delete/define/load Transaction VSAM KSDS with alternate index | CLCIFIL: SDSF close CICS files (TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC) · STEP05: IDCAMS DELETE · STEP10: IDCAMS DEFINE CLUSTER (keys=16,0, recsize=350) · STEP15: IDCAMS REPRO · STEP20: DEFINE ALTERNATEINDEX · STEP25: DEFINE PATH · STEP30: BLDINDEX · OPCIFIL: SDSF open CICS files |
| **TCATBALF.jcl** | Delete/define/load Transaction Category Balance VSAM | STEP05: IDCAMS DELETE · STEP10: IDCAMS DEFINE CLUSTER (keys=17,0, recsize=50) · STEP15: IDCAMS REPRO |
| **DISCGRP.jcl** | Delete/define/load Disclosure Group VSAM | STEP05: IDCAMS DELETE · STEP10: IDCAMS DEFINE CLUSTER (keys=16,0, recsize=50) · STEP15: IDCAMS REPRO |
| **TRANTYPE.jcl** | Delete/define/load Transaction Type reference VSAM | STEP05: IDCAMS DELETE · STEP10: IDCAMS DEFINE CLUSTER (keys=2,0, recsize=60) · STEP15: IDCAMS REPRO |
| **TRANCATG.jcl** | Delete/define/load Transaction Category reference VSAM | STEP05: IDCAMS DELETE · STEP10: IDCAMS DEFINE CLUSTER (keys=6,0, recsize=60) · STEP15: IDCAMS REPRO |
| **DEFCUST.jcl** | Define Customer VSAM cluster (alternate naming convention) | STEP05: IDCAMS DELETE · STEP05: IDCAMS DEFINE CLUSTER |

### Batch Processing Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **POSTTRAN.jcl** | Post daily transactions to master files | STEP15: EXEC PGM=CBTRN02C (transaction posting with validation) |
| **INTCALC.jcl** | Calculate interest charges on accounts | STEP15: EXEC PGM=CBACT04C, PARM='2022071800' (date parameter) |
| **TRANREPT.jcl** | Generate daily transaction detail report | STEP05R: EXEC PROC=REPROC (REPRO transaction file) · STEP05R: SORT transactions · STEP10R: EXEC PGM=CBTRN03C (report generation) |
| **CREASTMT.JCL** | Create account statements (text + HTML) | DELDEF01: IDCAMS define statement work file · STEP010: SORT transactions · STEP020: IDCAMS REPRO sorted data · STEP030: IEFBR14 allocate output files · STEP040: EXEC PGM=CBSTM03A (statement generator) |
| **PRTCATBL.jcl** | Print transaction category balance report | DELDEF: IEFBR14 allocate output · STEP05R: EXEC PROC=REPROC (REPRO category balance) · STEP10R: SORT category balances |
| **COMBTRAN.jcl** | Combine transaction backup + system-generated transactions | STEP05R: SORT (merge TRANSACT.BKUP(0) + SYSTRAN(0) by TRAN-ID) · STEP10: IDCAMS REPRO combined file to VSAM |
| **TRANBKP.jcl** | Backup and clear transaction master | STEP05R: EXEC PROC=REPROC (REPRO to backup GDG) · STEP05: IDCAMS DELETE transaction cluster · STEP10: IDCAMS REPRO from backup |
| **CBEXPORT.jcl** | Export all data for branch migration | STEP01: IDCAMS define export VSAM cluster · STEP02: EXEC PGM=CBEXPORT |
| **CBIMPORT.jcl** | Import data from export file | STEP01: EXEC PGM=CBIMPORT |
| **WAITSTEP.jcl** | Wait/delay utility | WAIT: EXEC PGM=COBSWAIT |

### Batch Read/Verify Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **READACCT.jcl** | Read and verify account file | PREDEL: IEFBR14 · STEP05: EXEC PGM=CBACT01C |
| **READCARD.jcl** | Read and verify card file | STEP05: EXEC PGM=CBACT02C |
| **READXREF.jcl** | Read and verify cross-reference file | STEP05: EXEC PGM=CBACT03C |
| **READCUST.jcl** | Read and verify customer file | STEP05: EXEC PGM=CBCUS01C |

### GDG / Infrastructure Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **DEFGDGB.jcl** | Define GDG bases for batch processing | STEP05: IDCAMS DEFINE GDG for TRANSACT.BKUP, TRANSACT.DALY, TRANREPT, TCATBALF.BKUP, SYSTRAN, TRANSACT.COMBINED (all LIMIT=5) |
| **DEFGDGD.jcl** | Define GDGs and load first generation for reference data | STEP10: DEFINE GDG TRANTYPE.BKUP · STEP20: IEBGENER copy flat→GDG(+1) · STEP30: DEFINE GDG TRANCATG.PS.BKUP · STEP40: IEBGENER copy · STEP50: DEFINE GDG DISCGRP.BKUP · STEP60: IEBGENER copy |
| **DALYREJS.jcl** | Define GDG for daily rejection files | STEP05: IDCAMS DEFINE GDG (DALYREJS, LIMIT=5) |
| **REPTFILE.jcl** | Define GDG for report files | STEP05: IDCAMS DEFINE GDG |
| **TRANIDX.jcl** | Define alternate index on transaction master | STEP20: DEFINE ALTERNATEINDEX · STEP25: DEFINE PATH · STEP30: BLDINDEX |

### CICS Administration Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **CBADMCDJ.jcl** | Define all CICS resources (programs, mapsets, transactions, files, TDQs) for CardDemo | STEP1: EXEC PGM=DFHCSDUP — defines LIBRARY, MAPSETs, PROGRAMs, TRANSACTIONs, FILEs, TDQUEUEs in group CARDDEMO |
| **CLOSEFIL.jcl** | Close all VSAM files in CICS region | CLCIFIL: SDSF — CEMT SET FIL CLO for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| **OPENFIL.jcl** | Open all VSAM files in CICS region | OPCIFIL: SDSF — CEMT SET FIL OPE for all files |

### User Security / Miscellaneous Jobs

| Job Name | Purpose | Steps |
|----------|---------|-------|
| **DUSRSECJ.jcl** | Define user security VSAM file (ESDS/RRDS pattern) | PREDEL: IEFBR14 · STEP01: IEBGENER create flat file · STEP02: IDCAMS define VSAM · STEP03: IDCAMS REPRO load |
| **ESDSRRDS.jcl** | Define ESDS and RRDS VSAM clusters | PREDEL: IEFBR14 · STEP01: IEBGENER · STEP02-05: IDCAMS define ESDS, RRDS, REPRO |
| **INTRDRJ1.JCL** | Internal reader job — triggers another JCL via INTRDR | IDCAMS: IDCAMS · STEP01: IEBGENER (writes JCL to INTRDR) |
| **INTRDRJ2.JCL** | Internal reader job — create physical VSAM for IMS demo | IDCAMS: IDCAMS define cluster |
| **FTPJCL.JCL** | FTP file transfer job | STEP1: EXEC PGM=FTP |
| **TXT2PDF1.JCL** | Convert text to PDF | TXT2PDF: EXEC PGM=IKJEFT1B (TSO batch) |

---

## Cross-Reference: Copybook Usage

| Copybook | Programs Using It | Count |
|----------|-------------------|-------|
| COCOM01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C | 17 |
| COTTL01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C | 17 |
| CSDAT01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C | 17 |
| CSMSG01Y | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C | 17 |
| DFHAID | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C | 17 |
| DFHBMSCA | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C | 17 |
| CSUSR01Y | COACTUPC, COACTVWC, COADM01C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C | 12 |
| CVACT01Y | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C | 11 |
| CVACT03Y | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C | 12 |
| CVTRA05Y | CBACT04C, CBEXPORT, CBIMPORT, CBTRN01C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C | 11 |
| CVACT02Y | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | 8 |
| CVCUS01Y | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC | 8 |
| CSMSG02Y | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC | 4 |
| CVCRD01Y | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | 5 |
| CSSTRPFY | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC | 5 |
| CSSETATY | COACTUPC | 1 |
| CSUTLDWY | COACTUPC | 1 |
| CSUTLDPY | COACTUPC | 1 |
| CSLKPCDY | COACTUPC | 1 |
| CVTRA06Y | CBTRN01C, CBTRN02C | 2 |
| CVTRA01Y | CBACT04C, CBTRN02C | 2 |
| CVTRA02Y | CBACT04C | 1 |
| CVTRA03Y | CBTRN03C | 1 |
| CVTRA04Y | CBTRN03C | 1 |
| CVTRA07Y | CBTRN03C | 1 |
| CVEXPORT | CBEXPORT, CBIMPORT | 2 |
| CODATECN | CBACT01C | 1 |
| COSTM01 | CBSTM03A | 1 |
| CUSTREC | CBSTM03A | 1 |
| COADM02Y | COADM01C | 1 |
| COMEN02Y | COMEN01C | 1 |

---

## VSAM File Inventory

| DD Name (Batch) | CICS File Name | Dataset | Key | Record Size | Entity |
|-----------------|----------------|---------|-----|-------------|--------|
| ACCTFILE | ACCTDAT | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | ACCT-ID (11,0) | 300 | Account |
| CARDFILE | CARDDAT | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CARD-NUM (16,0) | 150 | Card |
| — | CARDAIX | AWS.M2.CARDDEMO.CARDDATA.VSAM.AIX.PATH | CARD-ACCT-ID (11,16) | — | Card (by account) |
| CUSTFILE | CUSTDAT | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CUST-ID (9,0) | 500 | Customer |
| XREFFILE | CCXREF | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | XREF-CARD-NUM (16,0) | 50 | Cross-Reference |
| — | CXACAIX | (Alternate index on XREFFILE) | XREF-ACCT-ID (11,16) | — | Cross-Reference (by account) |
| TRANSACT | TRANSACT | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | TRAN-ID (16,0) | 350 | Transaction |
| TCATBALF | — | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | TRAN-CAT-KEY (17,0) | 50 | Tran Category Balance |
| DISCGRP | — | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | DIS-GROUP-KEY (16,0) | 50 | Disclosure Group |
| TRANTYPE | — | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | TRAN-TYPE (2,0) | 60 | Transaction Type (ref) |
| TRANCATG | — | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | TRAN-CAT-KEY (6,0) | 60 | Transaction Category (ref) |
| — | USRSEC | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | SEC-USR-ID (8,0) | 80 | User Security |
| DALYTRAN | — | AWS.M2.CARDDEMO.TRANSACT.DALY(0) | — | 350 | Daily Transactions (GDG) |
| DALYREJS | — | AWS.M2.CARDDEMO.DALYREJS(0) | — | 430 | Daily Rejections (GDG) |
| EXPFILE | — | AWS.M2.CARDDEMO.EXPORT.DATA | SEQ-NUM (4,28) | 500 | Export (migration) |
