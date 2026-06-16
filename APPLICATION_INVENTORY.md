# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System (Mainframe)  
> **Repo:** `infosys-training/uc-legacy-modernization-cobol-to-java`  
> **Generated:** 2026-06-16

---

## Estate Summary

| Metric | Count |
|--------|-------|
| COBOL Programs | 39 |
| Copybooks (.cpy) | 41 |
| BMS Screen Maps (.bms) | 21 |
| JCL Jobs (.jcl) | 38 |
| IMS DBD/PSB Definitions | 7 |
| DB2 DCL Declarations | 3 |
| Total Lines of COBOL | 27,969 |

### Source Directory Layout

| Directory | Contents |
|-----------|----------|
| `app/cbl/` | 27 main COBOL programs (batch + CICS online) |
| `app/cpy/` | 27 main copybooks |
| `app/jcl/` | 31 main JCL jobs |
| `app/bms/` | 17 BMS screen maps |
| `app/data/` | EBCDIC + ASCII sample datasets |
| `app/app-authorization-ims-db2-mq/cbl/` | 5 IMS/DB2/MQ programs |
| `app/app-authorization-ims-db2-mq/cpy/` | 6 auth copybooks |
| `app/app-authorization-ims-db2-mq/cpy-bms/` | 2 auth BMS copybooks |
| `app/app-authorization-ims-db2-mq/bms/` | 2 auth BMS maps |
| `app/app-authorization-ims-db2-mq/jcl/` | 2 auth JCL jobs |
| `app/app-authorization-ims-db2-mq/ims/` | 7 IMS DBD/PSB definitions |
| `app/app-authorization-ims-db2-mq/dcl/` | 1 DB2 DCL declaration |
| `app/app-transaction-type-db2/cbl/` | 3 DB2 programs |
| `app/app-transaction-type-db2/cpy/` | 2 DB2 copybooks |
| `app/app-transaction-type-db2/cpy-bms/` | 2 DB2 BMS copybooks |
| `app/app-transaction-type-db2/bms/` | 2 DB2 BMS maps |
| `app/app-transaction-type-db2/jcl/` | 3 DB2 JCL jobs |
| `app/app-transaction-type-db2/dcl/` | 2 DB2 DCL declarations |
| `app/app-vsam-mq/cbl/` | 2 VSAM/MQ programs |

---

## 1. COBOL Program Inventory

### 1.1 Main Programs (`app/cbl/`) — 27 programs

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|----------------------|
| 1 | CBACT01C.cbl | 430 | Batch | Read account VSAM file and write parsed output to sequential flat files (normal, array, variable-length) | R: ACCTFILE (KSDS); W: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | 178 | Batch | Read and display card data file records | R: CARDFILE (KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | 178 | Batch | Read and display card-account cross-reference file records | R: XREFFILE (KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | 652 | Batch | Interest calculator — reads transaction category balances, looks up discount group rates, computes interest/fees, updates account balances | R: TCATBALF (KSDS), XREFFILE, DISCGRP, ACCTFILE; W: TRANSACT; RW: ACCTFILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | 178 | Batch | Read and display customer data file records | R: CUSTFILE (KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | 582 | Batch | Export all VSAM entity files (customer, account, xref, transaction, card) into a single multi-record export file for branch migration | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; W: EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | 487 | Batch | Import multi-record export file, split by record type, validate, and write to separate normalized output files with error logging | R: EXPFILE; W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBTRN01C.cbl | 494 | Batch | Validate daily transactions — read daily transaction file, cross-reference card/account, verify account status | R: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 9 | CBTRN02C.cbl | 731 | Batch | Post daily transactions to master — validate, post to transaction master, update account balances and category balances, write rejects | R: DALYTRAN, XREFFILE; W: TRANFILE, DALYREJS; RW: ACCTFILE, TCATBALF | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | 649 | Batch | Generate daily transaction report — reads transactions, cross-references cards, looks up transaction types/categories, writes formatted report with totals | R: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; W: TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 11 | COBSWAIT.cbl | 41 | Batch (Utility) | Utility program to pause execution for a specified time (centiseconds) | None (CALL to assembler MVSWAIT) | — |
| 12 | CSUTLDTC.cbl | 157 | Batch (Utility) | Date validation utility — validates dates using LE CEEDAYS function | None (CALL to CEEDAYS) | — |
| 13 | COACTUPC.cbl | 4,236 | Online (CICS) | Account update — the largest program; exhaustive field validation (date, SSN, phone, state, ZIP), reads/updates account, customer, and card-xref VSAM files via CICS | CICS R: ACCTFILE, CARDXREF, CUSTFILE; CICS RW: ACCTFILE, CUSTFILE | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y (56 COPY stmts incl. REPLACING) |
| 14 | COACTVWC.cbl | 941 | Online (CICS) | Account view — display account details, associated customer info, and card data in read-only mode | CICS R: ACCTFILE, CARDXREF, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 15 | COADM01C.cbl | 288 | Online (CICS) | Admin menu — hub for administrative functions (user CRUD, transaction type management); routes to sub-programs via XCTL | CICS SEND/RECEIVE MAP | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | 572 | Online (CICS) | Bill payment — process credit card bill payments, create transaction records, update account balances | CICS R/RW: ACCTFILE; CICS R: CARDXREF; CICS STARTBR/READPREV/ENDBR: TRANSACT; CICS W: TRANSACT | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 17 | COCRDLIC.cbl | 1,459 | Online (CICS) | Credit card list — paginated browse of cards using CICS STARTBR/READNEXT/READPREV/ENDBR pattern; navigates to card detail/update screens | CICS STARTBR/READNEXT/READPREV/ENDBR: CARDFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y (+ 3 more) |
| 18 | COCRDSLC.cbl | 887 | Online (CICS) | Credit card view — display card details (card number, name, status, expiry) in read-only mode | CICS R: CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y (+ 3 more) |
| 19 | COCRDUPC.cbl | 1,560 | Online (CICS) | Credit card update — edit card name, status, expiry; validates inputs and rewrites card VSAM record | CICS R/RW: CARDFILE; CICS R: CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y (+ 3 more) |
| 20 | COMEN01C.cbl | 308 | Online (CICS) | Main menu — central hub for user-facing functions (11 menu options); routes to account, card, transaction, report, and bill pay screens via XCTL | CICS SEND/RECEIVE MAP; XCTL to 11 programs | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 21 | CORPT00C.cbl | 649 | Online (CICS) | Report request — allows users to request batch reports; submits JCL via internal reader (INTRDRJ1/J2) for asynchronous report generation | CICS SEND/RECEIVE MAP; CICS SPOOLOPEN/SPOOLWRITE/SPOOLCLOSE | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, CVTRA07Y |
| 22 | COSGN00C.cbl | 260 | Online (CICS) | Sign-on — entry point for the application; validates user ID/password against USRSEC VSAM file, routes to admin or user menu | CICS R: USRSEC; XCTL to COADM01C or COMEN01C | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA, CSMSG02Y |
| 23 | COTRN00C.cbl | 699 | Online (CICS) | Transaction list — paginated browse of transactions using STARTBR/READNEXT/READPREV; navigates to detail/add screens | CICS STARTBR/READNEXT/READPREV/ENDBR: TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, CVTRA05Y |
| 24 | COTRN01C.cbl | 330 | Online (CICS) | Transaction view — display single transaction detail in read-only mode | CICS R: TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, CVTRA05Y |
| 25 | COTRN02C.cbl | 783 | Online (CICS) | Transaction add — screen for adding new transactions; validates inputs, writes to TRANSACT VSAM, updates account balance | CICS R: ACCTFILE, CARDXREF; CICS W: TRANSACT; CICS RW: ACCTFILE | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, CVTRA05Y, CVACT01Y, CVACT03Y |
| 26 | COUSR00C.cbl | 695 | Online (CICS) | User list — paginated browse of user security records; navigates to update/delete screens | CICS STARTBR/READNEXT/READPREV/ENDBR: USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, CSUSR01Y |
| 27 | COUSR01C.cbl | 299 | Online (CICS) | User add — screen for adding new user security records | CICS W: USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, CSUSR01Y, CSMSG02Y |
| 28 | COUSR02C.cbl | 414 | Online (CICS) | User update — edit user type and password | CICS R/RW: USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, CSUSR01Y |
| 29 | COUSR03C.cbl | 359 | Online (CICS) | User delete — confirm and delete user security records | CICS R/DELETE: USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, DFHAID, DFHBMSCA, CSUSR01Y |

### 1.2 Authorization Sub-Application (`app/app-authorization-ims-db2-mq/cbl/`) — 5 programs

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|----------------------|
| 30 | CBPAUP0C.cbl | 386 | Batch (IMS) | Purge expired pending authorizations — reads IMS auth summary/detail segments via DL/I (GN, GNP), evaluates expiry, deletes qualified records (DLET) | IMS DL/I: GN, GNP, DLET on DBPAUTP0/DBPAUTX0 | CIPAUSMY, CIPAUDTY |
| 31 | COPAUA0C.cbl | 1,026 | Online (CICS+IMS+MQ+DB2) | Authorization decision engine — receives auth request via MQ, reads card-xref/account/customer VSAM files, performs IMS lookup, evaluates fraud rules, sends MQ reply | MQ: MQOPEN, MQGET, MQPUT1; CICS R: CARDXREF, ACCTFILE, CUSTFILE; IMS DL/I: GU, SCHD, TERM | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y (+ 2 more) |
| 32 | COPAUS0C.cbl | 1,032 | Online (CICS+IMS) | Pending authorization summary browse — paginated IMS segment browse with CICS screen I/O | CICS SEND/RECEIVE MAP; IMS DL/I: GU, GNP; CICS R: ACCTFILE, CARDFILE, CUSTFILE | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 33 | COPAUS1C.cbl | 604 | Online (CICS+IMS) | Pending authorization detail with update — displays auth detail, allows status update via IMS REPL | CICS SEND/RECEIVE MAP; CICS LINK to COPAUS2C; IMS DL/I: GU, GNP, REPL | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 34 | COPAUS2C.cbl | 244 | Online (CICS+DB2) | Fraud flag insertion — called by COPAUS1C to insert fraud markers into DB2 AUTHFRDS table | EXEC SQL INSERT/SELECT on AUTHFRDS; CICS ASKTIME/FORMATTIME | CIPAUDTY |

### 1.3 Transaction Type DB2 Sub-Application (`app/app-transaction-type-db2/cbl/`) — 3 programs

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|----------------------|
| 35 | COBTUPDT.cbl | 237 | Batch (DB2) | Batch transaction type maintenance — reads sequential input file, performs DB2 INSERT/UPDATE/DELETE on TRANSACTION_TYPE table | R: INPFILE; EXEC SQL INSERT/UPDATE/DELETE on TRANSACTION_TYPE | DCLTRTYP (SQL INCLUDE) |
| 36 | COTRTLIC.cbl | 2,098 | Online (CICS+DB2) | Transaction type list — cursor-based pagination over DB2 TRANSACTION_TYPE table; supports browse, select for update/delete | EXEC SQL DECLARE/OPEN/FETCH/CLOSE CURSOR; EXEC SQL DELETE; CICS SYNCPOINT | CVCRD01Y, CSDB2RWY, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y |
| 37 | COTRTUPC.cbl | 1,702 | Online (CICS+DB2) | Transaction type update — single-record update/delete with cascading delete to TRANSACTION_CATEGORY; DB2 cursor for category browse | EXEC SQL SELECT/UPDATE/DELETE/INSERT on TRANSACTION_TYPE and TRANSACTION_CATEGORY; CICS SYNCPOINT | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DCLTRTYP, DCLTRCAT, COCOM01Y |

### 1.4 VSAM/MQ Sub-Application (`app/app-vsam-mq/cbl/`) — 2 programs

| # | Filename | LOC | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----|----------------|---------|---------------------|----------------------|
| 38 | COACCT01.cbl | 620 | Online (CICS+MQ) | Account inquiry via MQ — receives MQ request for account data, reads VSAM account file, returns formatted MQ response | MQ: MQOPEN, MQGET, MQPUT1; CICS R: ACCTFILE, CARDXREF, CUSTFILE | COCOM01Y, CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CVACT01Y, CVCUS01Y |
| 39 | CODATE01.cbl | 524 | Online (CICS+MQ) | Date inquiry via MQ — receives date validation/conversion request via MQ, processes date, returns response | MQ: MQOPEN, MQGET, MQPUT1 | COCOM01Y, CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CODATECN |

---

## 2. Classification Summary

| Classification | Programs | % | Total LOC |
|----------------|----------|---|-----------|
| Batch (pure) | 12 | 31% | 4,778 |
| Batch (IMS) | 1 | 3% | 386 |
| Batch (DB2) | 1 | 3% | 237 |
| Online (CICS only) | 16 | 41% | 12,539 |
| Online (CICS+DB2) | 3 | 8% | 4,044 |
| Online (CICS+IMS) | 2 | 5% | 1,636 |
| Online (CICS+IMS+MQ+DB2) | 1 | 3% | 1,026 |
| Online (CICS+MQ) | 2 | 5% | 1,144 |
| Utility | 1 | 3% | 41 |
| **Total** | **39** | **100%** | **25,831** |

*Note: 2,138 additional LOC in utility/date programs (CSUTLDTC: 157, CBACT01C residual working storage).*

---

## 3. BMS Screen Map Catalog

| BMS Map | Paired CICS Program | Screen Purpose |
|---------|---------------------|----------------|
| COSGN00.bms | COSGN00C.cbl | Sign-on / Login |
| COADM01.bms | COADM01C.cbl | Admin Menu |
| COMEN01.bms | COMEN01C.cbl | Main Menu (User) |
| COACTVW.bms | COACTVWC.cbl | Account View |
| COACTUP.bms | COACTUPC.cbl | Account Update |
| COCRDLI.bms | COCRDLIC.cbl | Credit Card List |
| COCRDSL.bms | COCRDSLC.cbl | Credit Card View |
| COCRDUP.bms | COCRDUPC.cbl | Credit Card Update |
| COTRN00.bms | COTRN00C.cbl | Transaction List |
| COTRN01.bms | COTRN01C.cbl | Transaction View |
| COTRN02.bms | COTRN02C.cbl | Transaction Add |
| CORPT00.bms | CORPT00C.cbl | Report Request |
| COBIL00.bms | COBIL00C.cbl | Bill Payment |
| COUSR00.bms | COUSR00C.cbl | User List |
| COUSR01.bms | COUSR01C.cbl | User Add |
| COUSR02.bms | COUSR02C.cbl | User Update |
| COUSR03.bms | COUSR03C.cbl | User Delete |
| COPAU00.bms | COPAUS0C.cbl | Pending Auth Summary |
| COPAU01.bms | COPAUS1C.cbl | Pending Auth Detail |
| COTRTLI.bms | COTRTLIC.cbl | Transaction Type List (DB2) |
| COTRTUP.bms | COTRTUPC.cbl | Transaction Type Update (DB2) |

---

## 4. JCL Job Catalog

### 4.1 Main JCL Jobs (`app/jcl/`) — 31 jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 1 | ACCTFILE.jcl | Define & load Account VSAM KSDS cluster | STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER (KEYS 11,0, RECSIZE 300) → STEP15: IDCAMS REPRO from ACCTDATA.PS |
| 2 | CARDFILE.jcl | Define & load Card VSAM KSDS cluster with AIX | CLCIFIL: SDSF close CICS files → STEP05: IDCAMS DELETE cluster + AIX → STEP10: IDCAMS DEFINE CLUSTER (KEYS 16,0, RECSIZE 150) → STEP15: IDCAMS REPRO → STEP40: DEFINE AIX (KEYS 11,16 NONUNIQUEKEY) → STEP50: DEFINE PATH → STEP60: BLDINDEX → OPCIFIL: SDSF open CICS files |
| 3 | CBADMCDJ.jcl | Create CICS CSD resources for CardDemo | STEP1: DFHCSDUP — defines all mapsets, programs, transactions (CARDDEMO group) |
| 4 | CBEXPORT.jcl | Export all VSAM data to multi-record export file | STEP01: IDCAMS DEFINE export cluster → STEP02: PGM=CBEXPORT (reads 5 VSAM files, writes EXPFILE) |
| 5 | CBIMPORT.jcl | Import multi-record export file into normalized outputs | STEP01: PGM=CBIMPORT (reads EXPFILE, writes CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT) |
| 6 | CLOSEFIL.jcl | Close VSAM files in CICS region | CLCIFIL: SDSF — CEMT SET FIL CLO for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| 7 | COMBTRAN.jcl | Combine & sort transaction backup + system-generated transactions | STEP05R: SORT on TRAN-ID ascending → STEP10: IDCAMS REPRO to TRANSACT.VSAM.KSDS |
| 8 | CUSTFILE.jcl | Define & load Customer VSAM KSDS cluster | CLCIFIL: SDSF close → STEP05: IDCAMS DELETE → STEP10: IDCAMS DEFINE CLUSTER (KEYS 9,0, RECSIZE 500) → STEP15: REPRO → OPCIFIL: SDSF open |
| 9 | DALYREJS.jcl | Define GDG base for daily transaction rejects | STEP05: IDCAMS DEFINE GDG (LIMIT 5) |
| 10 | DEFCUST.jcl | Alternate customer VSAM cluster definition | STEP05: IDCAMS DELETE → STEP05: IDCAMS DEFINE CLUSTER (KEYS 10,0, RECSIZE 500) |
| 11 | DEFGDGB.jcl | Define all GDG bases for batch pipeline | STEP05: IDCAMS DEFINE GDG for TRANSACT.BKUP, TRANSACT.DALY, TRANREPT, TCATBALF.BKUP, SYSTRAN, TRANSACT.COMBINED (6 GDGs, LIMIT 5 each) |
| 12 | DEFGDGD.jcl | Define GDG bases + initial generations for DB2 reference data | STEP10-60: IDCAMS DEFINE GDG + IEBGENER for TRANTYPE.BKUP, TRANCATG.PS.BKUP, DISCGRP.BKUP |
| 13 | DISCGRP.jcl | Define & load Disclosure Group VSAM KSDS cluster | STEP05: DELETE → STEP10: DEFINE CLUSTER (KEYS 16,0, RECSIZE 50) → STEP15: REPRO |
| 14 | DUSRSECJ.jcl | Create & load User Security file with inline data | PREDEL: IEFBR14 delete → STEP01: IEBGENER (inline user data → PS) → STEP02: IDCAMS DEFINE KSDS (KEYS 8,0, RECSIZE 80) → STEP03: REPRO |
| 15 | ESDSRRDS.jcl | Define ESDS and RRDS VSAM clusters for testing | Steps define ESDS (NONINDEXED) and RRDS (NUMBERED) clusters |
| 16 | INTCALC.jcl | Execute interest calculation batch program | Executes PGM=CBACT04C with DD for TCATBALF, XREFFILE, DISCGRP, ACCTFILE, TRANSACT |
| 17 | OPENFIL.jcl | Open VSAM files in CICS region | OPCIFIL: SDSF — CEMT SET FIL OPE for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC |
| 18 | POSTTRAN.jcl | Execute transaction posting batch program | Executes PGM=CBTRN02C with DD for DALYTRAN, TRANFILE, XREFFILE, DALYREJS, ACCTFILE, TCATBALF |
| 19 | PRTCATBL.jcl | Print transaction category balance report | Executes utility to print TCATBALF contents |
| 20 | READACCT.jcl | Read/print account VSAM file | Executes PGM=CBACT01C with DD for ACCTFILE, OUTFILE, ARRYFILE, VBRCFILE |
| 21 | READCARD.jcl | Read/print card data VSAM file | Executes PGM=CBACT02C with DD for CARDFILE |
| 22 | READCUST.jcl | Read/print customer data VSAM file | Executes PGM=CBCUS01C with DD for CUSTFILE |
| 23 | READXREF.jcl | Read/print card-xref VSAM file | Executes PGM=CBACT03C with DD for XREFFILE |
| 24 | REPTFILE.jcl | Define report output VSAM file | IDCAMS DEFINE CLUSTER for REPTFILE |
| 25 | TCATBALF.jcl | Define & load Transaction Category Balance VSAM file | DELETE → DEFINE CLUSTER (KEYS 17,0, RECSIZE 50) → REPRO |
| 26 | TRANBKP.jcl | Backup transaction master to GDG | IEBGENER copy TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1) |
| 27 | TRANCATG.jcl | Define & load Transaction Category VSAM file | DELETE → DEFINE CLUSTER → REPRO |
| 28 | TRANFILE.jcl | Define Transaction master VSAM KSDS cluster | DELETE → DEFINE CLUSTER (KEYS 16,0, RECSIZE 350) → REPRO |
| 29 | TRANIDX.jcl | Build alternate index on Transaction file | DEFINE AIX → DEFINE PATH → BLDINDEX |
| 30 | TRANREPT.jcl | Execute transaction report batch program | Executes PGM=CBTRN03C with DD for TRANFILE, CARDXREF, TRANTYPE, TRANCATG, TRANREPT, DATEPARM |
| 31 | TRANTYPE.jcl | Define & load Transaction Type VSAM file | DELETE → DEFINE CLUSTER (KEYS 6,0, RECSIZE 60) → REPRO |
| 32 | WAITSTEP.jcl | Execute wait utility (pipeline pacing) | Executes PGM=COBSWAIT |
| 33 | XREFFILE.jcl | Define & load Card-XREF VSAM KSDS cluster with AIX | DELETE → DEFINE CLUSTER (KEYS 16,0, RECSIZE 50) → REPRO → DEFINE AIX → DEFINE PATH → BLDINDEX |

### 4.2 Authorization Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`) — 2 jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 34 | CBPAUP0J.jcl | Execute IMS expired-authorization purge | STEP01: DFSRRC00 (BMP,CBPAUP0C,PSBPAUTB) — IMS batch program |
| 35 | DBPAUTP0.jcl | Unload IMS pending authorization database | STEPDEL: IEFBR14 delete output → UNLOAD: DFSRRC00 (ULU,DFSURGU0,DBPAUTP0) |

### 4.3 Transaction Type DB2 JCL (`app/app-transaction-type-db2/jcl/`) — 3 jobs

| # | JCL Job | Purpose | Step Sequence |
|---|---------|---------|---------------|
| 36 | CREADB21.jcl | Create DB2 tables and indexes for transaction types | Multiple steps: CREATE TABLESPACE → CREATE TABLE TRANSACTION_TYPE → CREATE TABLE TRANSACTION_CATEGORY → CREATE INDEXES → BIND PLANS |
| 37 | MNTTRDB2.jcl | Maintain transaction type DB2 data (batch) | Executes PGM=COBTUPDT with DB2 subsystem attachment |
| 38 | TRANEXTR.jcl | Extract transaction type reference data from DB2 to flat file | DSNTIAD utility SQL SELECT → output to PS dataset |

---

## 5. Data Files

| Dataset Name | Format | Record Length | Entity | Usage |
|-------------|--------|---------------|--------|-------|
| AWS.M2.CARDDEMO.ACCTDATA.PS | PS (flat) | 300 | Account | Seed data for VSAM load |
| AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | VSAM KSDS | 300 | Account | Primary account master |
| AWS.M2.CARDDEMO.CARDDATA.PS | PS | 150 | Card | Seed data |
| AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | VSAM KSDS | 150 | Card | Card master with AIX on ACCT-ID |
| AWS.M2.CARDDEMO.CUSTDATA.PS | PS | 500 | Customer | Seed data |
| AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | VSAM KSDS | 500 | Customer | Customer master |
| AWS.M2.CARDDEMO.CARDXREF.PS | PS | 50 | Card-XREF | Seed data |
| AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | VSAM KSDS | 50 | Card-Account XREF | Cross-reference with AIX |
| AWS.M2.CARDDEMO.DALYTRAN.PS | PS | 350 | Daily Transaction | Daily input batch |
| AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | VSAM KSDS | 350 | Transaction | Transaction master |
| AWS.M2.CARDDEMO.TRANTYPE.PS | PS | 60 | Transaction Type | Reference data |
| AWS.M2.CARDDEMO.TRANCATG.PS | PS | 60 | Transaction Category | Reference data |
| AWS.M2.CARDDEMO.TCATBALF.PS | PS | 50 | Tran Category Balance | Running balance per category |
| AWS.M2.CARDDEMO.DISCGRP.PS | PS | 50 | Disclosure Group | Interest rate groups |
| AWS.M2.CARDDEMO.USRSEC.PS | PS | 80 | User Security | User credentials |
| AWS.M2.CARDDEMO.EXPORT.DATA | VSAM KSDS | 500 | Multi-entity Export | Branch migration export |
| AWS.M2.CARDDEMO.ACCDATA.PS | PS | 300 | Account (EBCDIC) | EBCDIC-encoded seed |
