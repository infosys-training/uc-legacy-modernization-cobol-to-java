# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Generated:** 2026-06-16 | **Application:** CardDemo — Credit Card Management System  
> **Total Programs:** 44 | **Total JCL Jobs:** 46 | **Total BMS Maps:** 21

---

## Table of Contents

1. [Estate Summary](#estate-summary)
2. [Main COBOL Programs — `app/cbl/`](#main-cobol-programs--appcbl)
   - [Batch Programs](#batch-programs)
   - [Online (CICS) Programs](#online-cics-programs)
   - [Utility Programs](#utility-programs)
3. [Sub-Application Programs](#sub-application-programs)
   - [Authorization Module (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cbl/`](#authorization-module-imsdb2mq)
   - [Transaction Type Module (DB2) — `app/app-transaction-type-db2/cbl/`](#transaction-type-module-db2)
   - [VSAM/MQ Module — `app/app-vsam-mq/cbl/`](#vsam-mq-module)
4. [BMS Screen Maps](#bms-screen-maps)
5. [JCL Job Catalog](#jcl-job-catalog)
   - [Data Setup Jobs (VSAM Cluster Definition)](#data-setup-jobs-vsam-cluster-definition)
   - [Batch Processing Jobs](#batch-processing-jobs)
   - [CICS Administration Jobs](#cics-administration-jobs)
   - [Utility / Maintenance Jobs](#utility--maintenance-jobs)
   - [Sub-Application JCL Jobs](#sub-application-jcl-jobs)

---

## Estate Summary

| Category | Count | Source Directory |
|----------|------:|-----------------|
| Main COBOL Programs | 31 | `app/cbl/` |
| Authorization Module (IMS/DB2/MQ) | 8 | `app/app-authorization-ims-db2-mq/cbl/` |
| Transaction Type Module (DB2) | 3 | `app/app-transaction-type-db2/cbl/` |
| VSAM/MQ Module | 2 | `app/app-vsam-mq/cbl/` |
| **Total COBOL Programs** | **44** | |
| Main Copybooks | 30 | `app/cpy/` |
| Sub-App Copybooks | 11 | various `cpy/` dirs |
| BMS-Generated Copybooks | 21 | various `cpy-bms/` dirs |
| JCL Jobs (Main) | 38 | `app/jcl/` |
| JCL Jobs (Sub-App) | 8 | various `jcl/` dirs |
| BMS Maps | 21 | various `bms/` dirs |
| Total LOC (COBOL) | 30,175 | |

### Classification Breakdown

| Type | Count | % |
|------|------:|--:|
| Online (CICS) | 21 | 48% |
| Batch | 16 | 36% |
| Utility / Subroutine | 7 | 16% |

---

## Main COBOL Programs — `app/cbl/`

### Batch Programs

| # | Filename | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|----:|---------|---------------------|----------------------|
| 1 | `CBACT01C.cbl` | 430 | Read account VSAM file, format dates, write to multiple output files (sequential, array, variable-length) | **Read:** ACCTFILE (KSDS) **Write:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | `CBACT02C.cbl` | 178 | Read and print card data file (sequential dump) | **Read:** CARDFILE (KSDS) | CVACT02Y |
| 3 | `CBACT03C.cbl` | 178 | Read and print account cross-reference data file | **Read:** XREFFILE (KSDS) | CVACT03Y |
| 4 | `CBACT04C.cbl` | 652 | Interest calculator — reads transaction category balances, looks up discount groups, calculates interest, updates account balances | **Read:** TCATBALF (KSDS), XREFFILE (KSDS+AIX), DISCGRP (KSDS) **Read/Write:** ACCTFILE (KSDS) **Write:** TRANSACT (seq) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | `CBCUS01C.cbl` | 178 | Read and print customer data file (sequential dump) | **Read:** CUSTFILE (KSDS) | CVCUS01Y |
| 6 | `CBEXPORT.cbl` | 582 | Export customer data for branch migration — reads all normalized CardDemo files, creates multi-record export file with statistics | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE (all KSDS) **Write:** EXPFILE (KSDS) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | `CBIMPORT.cbl` | 487 | Import customer data from branch migration export — splits multi-record file into normalized targets with validation | **Read:** EXPFILE (KSDS) **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT (all seq) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | `CBSTM03A.CBL` | 924 | Print account statements from transaction data in plain-text and HTML formats. Uses ALTER/GO-TO, 2D arrays, COMP-3 variables | **Read:** XREF, CUST, ACCT, TRNX (via CBSTM03B) **Write:** STMTFILE, HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | `CBSTM03B.CBL` | 230 | I/O subroutine for CBSTM03A — handles file open/close/read/write/rewrite operations for multiple VSAM files | **Read/Write:** TRNXFILE, CUSTFILE, XREFFILE, ACCTFILE (all KSDS) | *(none — uses passed parameters)* |
| 10 | `CBTRN01C.cbl` | 494 | Validate and post records from daily transaction file — cross-references customer, card, account data | **Read:** DALYTRAN (seq), CUSTFILE, XREFFILE, CARDFILE, ACCTFILE (all KSDS) **Write:** TRANSACT (KSDS) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | `CBTRN02C.cbl` | 731 | Post records from daily transaction file — updates account balances with transaction amounts, maintains category balances | **Read:** DALYTRAN (seq), XREFFILE (KSDS) **Read/Write:** ACCTFILE (KSDS), TCATBALF (KSDS) **Write:** TRANSACT (KSDS) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | `CBTRN03C.cbl` | 649 | Print daily transaction detail report — joins transactions with cross-reference and type/category lookup tables | **Read:** TRANSACT (KSDS), XREFFILE (KSDS), TRANTYPE (KSDS), TRANCATG (KSDS) **Write:** REPTFILE (seq) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |

### Online (CICS) Programs

| # | Filename | LOC | Purpose | CICS Transaction | Key I/O (VSAM via CICS) | BMS Map | Copybooks Referenced |
|---|----------|----:|---------|------------------|--------------------------|---------|----------------------|
| 13 | `COSGN00C.cbl` | 260 | Sign-on screen — authenticates users, routes to Admin or User menu via XCTL | CC00 | **Read:** USRSEC (user security file) | COSGN00 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 14 | `COADM01C.cbl` | 288 | Admin menu hub — displays admin options, routes to sub-programs via XCTL | — | *(menu only)* | COADM01 | COCOM01Y, COADM02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | `COMEN01C.cbl` | 308 | Main menu hub for regular users — 11 options, routes to sub-programs via XCTL | CM00 | *(menu only)* | COMEN01 | COCOM01Y, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | `COACTVWC.cbl` | 941 | Account view — displays account, customer, and card details (read-only) | — | **Read:** ACCTFILE, CARDXREF, CUSTFILE | COACTVW | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 17 | `COACTUPC.cbl` | 4,236 | **Largest program** — Account update with exhaustive field validation (date, SSN, phone, state, ZIP, FICO). Uses 38× COPY REPLACING for screen attributes | — | **Read/Write:** ACCTFILE, CARDXREF, CUSTFILE | COACTUP | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×38), CSSTRPFY, CSUTLDPY |
| 18 | `COCRDLIC.cbl` | 1,459 | Credit card list — paginated browse (STARTBR/READNEXT/READPREV/ENDBR), routes to detail/update | — | **Browse:** CARDFILE (KSDS) | COCRDLI | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 19 | `COCRDSLC.cbl` | 887 | Credit card view — displays card details with customer info (read-only) | — | **Read:** CARDFILE, CUSTFILE | COCRDSL | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 20 | `COCRDUPC.cbl` | 1,560 | Credit card update — edit card details with validation | — | **Read/Write:** CARDFILE, CUSTFILE | COCRDUP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 21 | `COTRN00C.cbl` | 699 | Transaction list — paginated browse of TRANSACT file | — | **Browse:** TRANSACT (KSDS) | COTRN00 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 22 | `COTRN01C.cbl` | 330 | Transaction view — displays individual transaction details (read-only) | — | **Read:** TRANSACT | COTRN01 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | `COTRN02C.cbl` | 783 | Transaction add — creates new transaction records, validates card/account, generates sequence number | — | **Read:** CARDXREF, ACCTFILE **Write:** TRANSACT | COTRN02 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 24 | `CORPT00C.cbl` | 649 | Report request screen — submits batch JCL for transaction reports via extra-partition TDQ (INTRDRJ1/J2) | — | **Write:** TDQ (CICS Transient Data Queue) | CORPT00 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | `COBIL00C.cbl` | 572 | Bill payment — pays account balance in full, creates transaction record, updates account | — | **Read/Write:** ACCTFILE, CARDXREF **Write:** TRANSACT | COBIL00 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | `COUSR00C.cbl` | 695 | User list — paginated browse of user security file (admin) | — | **Browse:** USRSEC (KSDS) | COUSR00 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 27 | `COUSR01C.cbl` | 299 | User add — creates new user security record (admin) | — | **Write:** USRSEC | COUSR01 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | `COUSR02C.cbl` | 414 | User update — modifies existing user security record (admin) | — | **Read/Write:** USRSEC | COUSR02 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | `COUSR03C.cbl` | 359 | User delete — removes user security record (admin) | — | **Read/Delete:** USRSEC | COUSR03 | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### Utility Programs

| # | Filename | LOC | Purpose | Classification | Called By | Copybooks |
|---|----------|----:|---------|----------------|-----------|-----------|
| 30 | `CSUTLDTC.cbl` | 157 | Date conversion utility — calls LE CEEDAYS for Lilian date conversion | Utility (subroutine) | CORPT00C, COTRN02C | *(none)* |
| 31 | `COBSWAIT.cbl` | 41 | Wait routine wrapper — calls assembler MVSWAIT for timed delays | Utility (subroutine) | *(online testing)* | *(none)* |

---

## Sub-Application Programs

### Authorization Module (IMS/DB2/MQ)

**Directory:** `app/app-authorization-ims-db2-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O | Copybooks |
|---|----------|----:|---------|----------------|---------|-----------|
| 32 | `COPAUA0C.cbl` | 1,026 | Card authorization decision — spans CICS + IMS + MQ. Reads auth request from MQ, validates against account/card/customer via IMS, posts response | Online (CICS/IMS/MQ) | **MQ:** MQOPEN, MQGET, MQPUT1 **IMS:** GU, SCHD, TERM **VSAM:** ACCTFILE, CARDXREF, CUSTFILE | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 33 | `COPAUS0C.cbl` | 1,032 | Authorization summary view — paginated browse of pending authorizations via IMS | Online (CICS/IMS/BMS) | **IMS:** GU, GNP (PAUTH summary) | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | `COPAUS1C.cbl` | 604 | Authorization detail view — displays/updates individual authorization message | Online (CICS/IMS/BMS) | **IMS:** GU, GNP, REPL (PAUTH detail) | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | `COPAUS2C.cbl` | 244 | Fraud marking — marks authorization as fraudulent via DB2 insert | Online (CICS/DB2) | **DB2:** INSERT (fraud table) | CIPAUDTY |
| 36 | `CBPAUP0C.cbl` | 386 | Expired authorization purge — batch IMS program to delete expired pending authorizations | Batch (IMS) | **IMS:** GN, GNP, DLET (PAUTH segments) | CIPAUSMY, CIPAUDTY |
| 37 | `PAUDBLOD.CBL` | 369 | IMS database load — initial load of pending authorization database | Batch (IMS) | **IMS:** ISRT, GU (PAUTH database) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 38 | `PAUDBUNL.CBL` | 317 | IMS database unload — exports pending authorization database to sequential file | Batch (IMS) | **IMS:** GN, GNP (PAUTH segments) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | `DBUNLDGS.CBL` | 366 | GSAM unload utility — unloads IMS database using GSAM (Generalized Sequential Access Method) | Batch (IMS/GSAM) | **IMS:** GN, GNP, ISRT (GSAM output) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |

### Transaction Type Module (DB2)

**Directory:** `app/app-transaction-type-db2/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O | Copybooks |
|---|----------|----:|---------|----------------|---------|-----------|
| 40 | `COTRTLIC.cbl` | 2,098 | Transaction type list — cursor-based pagination of DB2 transaction types table | Online (CICS/DB2) | **DB2:** DECLARE CURSOR, OPEN, FETCH, CLOSE (TRANSACTION_TYPE table) | CSDB2RPY, CSDB2RWY, COCOM01Y, DFHAID, DFHBMSCA, COTTL01Y, CSDAT01Y, CSMSG01Y |
| 41 | `COTRTUPC.cbl` | 1,702 | Transaction type update — add/update/delete transaction types with cascading deletes | Online (CICS/DB2) | **DB2:** SELECT, INSERT, UPDATE, DELETE (TRANSACTION_TYPE + TRANSACTION_CATEGORY tables) | CSDB2RPY, CSDB2RWY, COCOM01Y, DFHAID, DFHBMSCA, COTTL01Y, CSDAT01Y, CSMSG01Y |
| 42 | `COBTUPDT.cbl` | 237 | Batch DB2 table update — utility to update transaction type records in batch | Batch (DB2) | **DB2:** SELECT, UPDATE (TRANSACTION_TYPE table) | CSDB2RWY |

### VSAM/MQ Module

**Directory:** `app/app-vsam-mq/cbl/`

| # | Filename | LOC | Purpose | Classification | Key I/O | Copybooks |
|---|----------|----:|---------|----------------|---------|-----------|
| 43 | `COACCT01.cbl` | 620 | Account inquiry via MQ — reads account data from VSAM in response to MQ request messages | Online (CICS/MQ) | **MQ:** MQGET, MQPUT **VSAM:** ACCTFILE (read) | COCOM01Y, CVACT01Y |
| 44 | `CODATE01.cbl` | 524 | Date inquiry via MQ — returns formatted date/time in response to MQ request messages | Online (CICS/MQ) | **MQ:** MQGET, MQPUT | COCOM01Y, CSDAT01Y |

---

## BMS Screen Maps

### Main BMS Maps — `app/bms/`

| # | Map Name | Paired Program | Screen Function |
|---|----------|----------------|-----------------|
| 1 | `COSGN00.bms` | COSGN00C | Sign-on / Login screen |
| 2 | `COADM01.bms` | COADM01C | Admin menu |
| 3 | `COMEN01.bms` | COMEN01C | Main user menu |
| 4 | `COACTVW.bms` | COACTVWC | Account view (read-only) |
| 5 | `COACTUP.bms` | COACTUPC | Account update form |
| 6 | `COCRDLI.bms` | COCRDLIC | Credit card list (browse) |
| 7 | `COCRDSL.bms` | COCRDSLC | Credit card view (detail) |
| 8 | `COCRDUP.bms` | COCRDUPC | Credit card update form |
| 9 | `COTRN00.bms` | COTRN00C | Transaction list (browse) |
| 10 | `COTRN01.bms` | COTRN01C | Transaction view (detail) |
| 11 | `COTRN02.bms` | COTRN02C | Transaction add form |
| 12 | `CORPT00.bms` | CORPT00C | Report request form |
| 13 | `COBIL00.bms` | COBIL00C | Bill payment form |
| 14 | `COUSR00.bms` | COUSR00C | User list (browse) |
| 15 | `COUSR01.bms` | COUSR01C | User add form |
| 16 | `COUSR02.bms` | COUSR02C | User update form |
| 17 | `COUSR03.bms` | COUSR03C | User delete confirmation |

### Sub-Application BMS Maps

| # | Map Name | Directory | Paired Program | Screen Function |
|---|----------|-----------|----------------|-----------------|
| 18 | `COPAU00.bms` | `app/app-authorization-ims-db2-mq/bms/` | COPAUS0C | Auth summary browse |
| 19 | `COPAU01.bms` | `app/app-authorization-ims-db2-mq/bms/` | COPAUS1C | Auth detail view |
| 20 | `COTRTLI.bms` | `app/app-transaction-type-db2/bms/` | COTRTLIC | Transaction type list |
| 21 | `COTRTUP.bms` | `app/app-transaction-type-db2/bms/` | COTRTUPC | Transaction type update |

---

## JCL Job Catalog

### Data Setup Jobs (VSAM Cluster Definition)

| # | Job Name | File | Steps | Purpose | Datasets Managed |
|---|----------|------|------:|---------|------------------|
| 1 | `ACCTFILE` | `app/jcl/ACCTFILE.jcl` | 3 | Delete/define/load account VSAM KSDS cluster | `AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS` ← `ACCTDATA.PS` |
| 2 | `CARDFILE` | `app/jcl/CARDFILE.jcl` | 7 | Delete/define/load card data KSDS with alternate index (AIX) on account ID | `AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS` + AIX + PATH |
| 3 | `CUSTFILE` | `app/jcl/CUSTFILE.jcl` | 3 | Delete/define/load customer VSAM KSDS cluster | `AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS` |
| 4 | `XREFFILE` | `app/jcl/XREFFILE.jcl` | 5 | Delete/define/load card-account cross-reference KSDS with AIX | `AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS` + AIX + PATH |
| 5 | `TRANFILE` | `app/jcl/TRANFILE.jcl` | 3 | Delete/define/load transaction VSAM KSDS cluster | `AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS` |
| 6 | `DISCGRP` | `app/jcl/DISCGRP.jcl` | 3 | Delete/define/load discount group KSDS | `AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS` |
| 7 | `TCATBALF` | `app/jcl/TCATBALF.jcl` | 3 | Delete/define/load transaction category balance KSDS | `AWS.M2.CARDDEMO.TCATBAL.VSAM.KSDS` |
| 8 | `TRANTYPE` | `app/jcl/TRANTYPE.jcl` | 3 | Delete/define/load transaction type KSDS | `AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS` |
| 9 | `TRANCATG` | `app/jcl/TRANCATG.jcl` | 3 | Delete/define/load transaction category KSDS | `AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS` |
| 10 | `DEFCUST` | `app/jcl/DEFCUST.jcl` | 2 | Define customer data cluster (alternate) | Customer cluster definition |
| 11 | `DEFGDGB` | `app/jcl/DEFGDGB.jcl` | 1 | Define GDG base for transaction backup | GDG base definition |
| 12 | `DEFGDGD` | `app/jcl/DEFGDGD.jcl` | 1 | Define GDG data entries | GDG data entries |
| 13 | `ESDSRRDS` | `app/jcl/ESDSRRDS.jcl` | 3 | Define ESDS and RRDS clusters (specialized VSAM types) | ESDS + RRDS clusters |
| 14 | `TRANIDX` | `app/jcl/TRANIDX.jcl` | 3 | Build alternate indexes on transaction file | Transaction AIX + PATH |
| 15 | `REPTFILE` | `app/jcl/REPTFILE.jcl` | 2 | Define report output file | Report output dataset |

### Batch Processing Jobs

| # | Job Name | File | Program Executed | Steps | Purpose |
|---|----------|------|------------------|------:|---------|
| 16 | `POSTTRAN` | `app/jcl/POSTTRAN.jcl` | CBTRN02C | 1 | Post daily transactions — reads DALYTRAN, writes TRANSACT, updates ACCTFILE |
| 17 | `INTCALC` | `app/jcl/INTCALC.jcl` | CBACT04C | 1 | Calculate interest on account balances |
| 18 | `CREASTMT` | `app/jcl/CREASTMT.JCL` | CBSTM03A | 1 | Generate customer statements (text + HTML) |
| 19 | `TRANREPT` | `app/jcl/TRANREPT.jcl` | CBTRN03C | 1 | Generate daily transaction detail report |
| 20 | `COMBTRAN` | `app/jcl/COMBTRAN.jcl` | CBTRN01C | 1 | Validate and combine daily transaction file |
| 21 | `CBEXPORT` | `app/jcl/CBEXPORT.jcl` | CBEXPORT | 2 | Export data for branch migration (define cluster + run) |
| 22 | `CBIMPORT` | `app/jcl/CBIMPORT.jcl` | CBIMPORT | 1 | Import data from branch migration export |
| 23 | `READACCT` | `app/jcl/READACCT.jcl` | CBACT01C | 1 | Read and dump account file |
| 24 | `READCARD` | `app/jcl/READCARD.jcl` | CBACT02C | 1 | Read and dump card file |
| 25 | `READCUST` | `app/jcl/READCUST.jcl` | CBCUS01C | 1 | Read and dump customer file |
| 26 | `READXREF` | `app/jcl/READXREF.jcl` | CBACT03C | 1 | Read and dump cross-reference file |
| 27 | `PRTCATBL` | `app/jcl/PRTCATBL.jcl` | *(IDCAMS PRINT)* | 1 | Print category balance file |
| 28 | `TRANBKP` | `app/jcl/TRANBKP.jcl` | *(IDCAMS REPRO)* | 2 | Backup transaction file (GDG versioned) |
| 29 | `DALYREJS` | `app/jcl/DALYREJS.jcl` | *(SORT/MERGE)* | 1 | Sort daily rejected transactions |
| 30 | `WAITSTEP` | `app/jcl/WAITSTEP.jcl` | COBSWAIT | 1 | Timed wait step (testing/scheduling) |

### CICS Administration Jobs

| # | Job Name | File | Steps | Purpose |
|---|----------|------|------:|---------|
| 31 | `CBADMCDJ` | `app/jcl/CBADMCDJ.jcl` | 1 | Create CICS CSD resource definitions (programs, mapsets, transactions, files) |
| 32 | `CLOSEFIL` | `app/jcl/CLOSEFIL.jcl` | 1 | Close VSAM files in CICS region (for batch updates) |
| 33 | `OPENFIL` | `app/jcl/OPENFIL.jcl` | 1 | Open VSAM files in CICS region (after batch) |
| 34 | `DUSRSECJ` | `app/jcl/DUSRSECJ.jcl` | 3 | Delete/define/load user security VSAM file |
| 35 | `INTRDRJ1` | `app/jcl/INTRDRJ1.JCL` | 1 | Internal reader JCL — submitted by CORPT00C for monthly reports |
| 36 | `INTRDRJ2` | `app/jcl/INTRDRJ2.JCL` | 1 | Internal reader JCL — submitted by CORPT00C for yearly reports |

### Utility / Maintenance Jobs

| # | Job Name | File | Steps | Purpose |
|---|----------|------|------:|---------|
| 37 | `FTPJCL` | `app/jcl/FTPJCL.JCL` | 1 | FTP file transfer utility |
| 38 | `TXT2PDF1` | `app/jcl/TXT2PDF1.JCL` | 1 | Convert text output to PDF format |

### Sub-Application JCL Jobs

#### Authorization Module (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/jcl/`

| # | Job Name | File | Program | Steps | Purpose |
|---|----------|------|---------|------:|---------|
| 39 | `CBPAUP0J` | `CBPAUP0J.jcl` | CBPAUP0C (via DFSRRC00 BMP) | 1 | Purge expired pending authorizations from IMS |
| 40 | `LOADPADB` | `LOADPADB.JCL` | PAUDBLOD (via DFSRRC00 BMP) | 1 | Initial load of pending authorization IMS database |
| 41 | `UNLDPADB` | `UNLDPADB.JCL` | PAUDBUNL (via DFSRRC00 BMP) | 1 | Unload pending authorization IMS database |
| 42 | `UNLDGSAM` | `UNLDGSAM.JCL` | DBUNLDGS (via DFSRRC00 ULU) | 1 | GSAM unload of IMS database |
| 43 | `DBPAUTP0` | `DBPAUTP0.jcl` | DFSURGU0 (via DFSRRC00 ULU) | 2 | Download DBD DBPAUTP0 (IMS DB unload utility) |

#### Transaction Type Module (DB2) — `app/app-transaction-type-db2/jcl/`

| # | Job Name | File | Steps | Purpose |
|---|----------|------|------:|---------|
| 44 | `CREADB21` | `CREADB21.jcl` | 2 | Create DB2 tables (TRANSACTION_TYPE, TRANSACTION_CATEGORY) |
| 45 | `MNTTRDB2` | `MNTTRDB2.jcl` | 1 | Maintain/populate transaction type DB2 tables |
| 46 | `TRANEXTR` | `TRANEXTR.jcl` | 1 | Extract transaction types from DB2 to sequential file |

---

*End of Application Inventory*
