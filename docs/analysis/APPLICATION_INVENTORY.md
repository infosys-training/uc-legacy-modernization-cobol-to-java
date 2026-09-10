# CardDemo — Application Inventory

Static analysis of every COBOL program, JCL job, BMS map and supporting asset under `app/`.
Purpose statements are inferred from program header comments and procedure logic; I/O is taken from
`SELECT ... ASSIGN`, `EXEC CICS READ/WRITE/...`, `EXEC SQL`, `EXEC DLI` and MQ `CALL` statements.

Companion documents: [DATA_DICTIONARY.md](DATA_DICTIONARY.md), [DEPENDENCY_MAP.md](DEPENDENCY_MAP.md), [HOTSPOT_REPORT.md](HOTSPOT_REPORT.md).

## 1. Estate summary

| Asset | Location | Count |
|---|---|---|
| COBOL programs — core | `app/cbl/` | 31 |
| COBOL programs — Authorization (IMS/DB2/MQ) | `app/app-authorization-ims-db2-mq/cbl/` | 8 |
| COBOL programs — Transaction Type (DB2) | `app/app-transaction-type-db2/cbl/` | 3 |
| COBOL programs — VSAM/MQ | `app/app-vsam-mq/cbl/` | 2 |
| **COBOL programs total** | | **44** (30,175 physical lines) |
| Record/working-storage copybooks — core | `app/cpy/` | 30 |
| Record/working-storage copybooks — sub-apps | `app/app-*/cpy/` | 11 (9 auth + 2 DB2) |
| BMS symbolic-map copybooks | `app/cpy-bms/`, `app/app-*/cpy-bms/` | 21 (17 + 2 + 2) |
| BMS map source | `app/bms/`, `app/app-*/bms/` | 21 (17 + 2 + 2) |
| JCL jobs — core | `app/jcl/` | 38 |
| JCL jobs — sub-apps | `app/app-*/jcl/` | 8 (5 auth + 3 DB2) |
| **JCL jobs total** | | **46** |
| Cataloged procedures | `app/proc/` | 2 (`REPROC`, `TRANREPT`) |
| Assembler routines + macros | `app/asm/`, `app/maclib/` | 2 + 2 (`COBDATFT`, `MVSWAIT`) |
| CICS CSD definitions | `app/csd/`, `app/app-*/csd/` | 4 |
| DB2 DDL / DCLGEN | `app/app-*/ddl/`, `app/app-*/dcl/` | 6 DDL + 3 DCL |
| IMS DBD / PSB | `app/app-authorization-ims-db2-mq/ims/` | 8 |
| Sample EBCDIC datasets | `app/data/EBCDIC/` | 13 |
| Scheduler definitions | `app/scheduler/` | 2 (`CardDemo.controlm`, `CardDemo.ca7`) |

Classification by execution model (44 programs): **26 online/CICS** (incl. 3 MQ-triggered CICS
programs), **17 batch**, **1 batch subroutine** (`CBSTM03B`).

## 2. Program inventory — core (`app/cbl/`)

### 2.1 Batch programs

| Program | LOC | Purpose (inferred) | Key I/O (DD name → dataset) | Copybooks |
|---|---|---|---|---|
| `CBACT01C.cbl` | 430 | Sequentially read the Account KSDS and write three derived flat files (fixed, array-style and variable-blocked). Calls assembler `COBDATFT` for date formatting. | R `ACCTFILE` (ACCTDATA.VSAM.KSDS); W `OUTFILE`, `ARRYFILE`, `VBRCFILE` | `CVACT01Y`, `CODATECN` |
| `CBACT02C.cbl` | 178 | Read and DISPLAY every card record (data dump / verification). | R `CARDFILE` (CARDDATA.VSAM.KSDS) | `CVACT02Y` |
| `CBACT03C.cbl` | 178 | Read and DISPLAY every card/account/customer cross-reference record. | R `XREFFILE` (CARDXREF.VSAM.KSDS) | `CVACT03Y` |
| `CBACT04C.cbl` | 652 | **Interest calculator.** For each transaction-category balance, look up the disclosure-group rate (falls back to group `DEFAULT`), compute monthly interest `(BAL × RATE) / 1200`, write an interest transaction and update account balance. Fee computation is a stub (`1400-COMPUTE-FEES` "To be implemented"). | R `TCATBALF`, `XREFFILE` (AIX path), `DISCGRP`; R/W `ACCTFILE`; W `TRANSACT` (SYSTRAN GDG) | `CVACT01Y`, `CVACT03Y`, `CVTRA01Y`, `CVTRA02Y`, `CVTRA05Y` |
| `CBCUS01C.cbl` | 178 | Read and DISPLAY every customer record. | R `CUSTFILE` (CUSTDATA.VSAM.KSDS) | `CVCUS01Y` |
| `CBEXPORT.cbl` | 582 | Export customer, account, card, xref and transaction data into a single typed export file for "branch migration". | R `CUSTFILE`, `ACCTFILE`, `XREFFILE`, `TRANSACT`, `CARDFILE`; W `EXPFILE` (EXPORT.DATA) | `CVACT01Y`, `CVACT02Y`, `CVACT03Y`, `CVCUS01Y`, `CVTRA05Y`, `CVEXPORT` |
| `CBIMPORT.cbl` | 487 | Import the export file, validate each record type and split into per-entity import files plus an error file. | R `EXPFILE`; W `CUSTOUT`, `ACCTOUT`, `XREFOUT`, `TRNXOUT`, `CARDOUT`, `ERROUT` | `CVACT01Y`, `CVACT02Y`, `CVACT03Y`, `CVCUS01Y`, `CVTRA05Y`, `CVEXPORT` |
| `CBSTM03A.CBL` | 924 | **Statement generation.** Drives `CBSTM03B` for all KSDS reads, groups transactions per card/account, produces plain-text and HTML statements. Highest write count in the estate (97 file statements). | W `STMTFILE` (STATEMNT.PS), `HTMLFILE` (STATEMNT.HTML); via CBSTM03B: R `TRNXFILE`, `XREFFILE`, `CUSTFILE`, `ACCTFILE` | `COSTM01`, `CUSTREC`, `CVACT01Y`, `CVACT03Y` |
| `CBSTM03B.CBL` | 230 | Called subroutine: generic OPEN/READ/START/CLOSE dispatcher for the four statement input files, keyed by a request code passed from `CBSTM03A`. | R `TRNXFILE`, `XREFFILE`, `CUSTFILE`, `ACCTFILE` | none |
| `CBTRN01C.cbl` | 494 | Earlier/simpler daily-transaction poster: reads daily transactions, resolves card→account via xref and looks up account/customer/card. No writes to master files. | R `DALYTRAN`, `CUSTFILE`, `XREFFILE`, `CARDFILE`, `ACCTFILE`, `TRANFILE` | `CVACT01Y`, `CVACT02Y`, `CVACT03Y`, `CVCUS01Y`, `CVTRA05Y`, `CVTRA06Y` |
| `CBTRN02C.cbl` | 731 | **Daily transaction posting.** Validates each daily transaction (card exists → account exists → credit-limit check → account not expired), writes rejects with reason codes 100–103, otherwise posts to TRANSACT, updates category balances and account cycle credit/debit. | R `DALYTRAN`; R/W `XREFFILE`, `ACCTFILE`, `TCATBALF`; W `TRANFILE` (TRANSACT.VSAM.KSDS), `DALYREJS` (GDG) | `CVACT01Y`, `CVACT03Y`, `CVTRA01Y`, `CVTRA05Y`, `CVTRA06Y` |
| `CBTRN03C.cbl` | 649 | **Transaction detail report.** Reads the date-filtered transaction extract, enriches with xref/type/category descriptions, prints paged report with page/account/grand totals. | R `TRANFILE` (TRANSACT.DALY GDG), `CARDXREF`, `TRANTYPE`, `TRANCATG`, `DATEPARM`; W `TRANREPT` (GDG) | `CVACT03Y`, `CVTRA03Y`, `CVTRA04Y`, `CVTRA05Y`, `CVTRA07Y` |
| `COBSWAIT.cbl` | 41 | Utility: wait N centiseconds (value read from SYSIN) by calling assembler `MVSWAIT`. Used by `WAITSTEP` job between CICS file close/open. | none | none |
| `CSUTLDTC.cbl` | 157 | Date-validation subroutine (called by `COTRN02C`, `CORPT00C`): converts a date string with a given mask via LE `CEEDAYS` and returns a severity/message. | none | none |

### 2.2 Online (CICS) programs

CICS file names: `ACCTDAT` (account), `CARDDAT` (card), `CUSTDAT` (customer), `CCXREF` (card xref, key = card),
`CXACAIX` (xref alternate index, key = account), `TRANSACT` (transactions), `USRSEC` (users).

| Program | Tran | BMS map | LOC | Purpose (inferred) | Key I/O | Copybooks |
|---|---|---|---|---|---|---|
| `COSGN00C.cbl` | CC00 | COSGN00 | 260 | Sign-on: validates user/password against USRSEC, routes to admin or user menu by `SEC-USR-TYPE`. | READ `USRSEC` | `COCOM01Y`, `COSGN00`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSUSR01Y`, `DFHAID`, `DFHATTR`, `DFHBMSCA` |
| `COMEN01C.cbl` | CM00 | COMEN01 | 308 | Main menu for regular users; 11 options table-driven from `COMEN02Y` (account view/update, card list/view/update, transaction list/view/add, reports, bill pay, authorizations); XCTL to selected program; INQUIRE checks program availability. | none (INQUIRE PROGRAM) | `COCOM01Y`, `COMEN01`, `COMEN02Y`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSUSR01Y`, `DFHAID`, `DFHBMSCA` |
| `COADM01C.cbl` | CA00 | COADM01 | 288 | Admin menu; 6 options from `COADM02Y` (user list/add/update/delete, transaction-type list/update). | none | `COCOM01Y`, `COADM01`, `COADM02Y`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSUSR01Y`, `DFHAID`, `DFHBMSCA` |
| `COACTVWC.cbl` | CAVW | COACTVW | 941 | Account view: account id → xref (AIX) → account + customer detail display. | READ `CXACAIX`, `ACCTDAT`, `CUSTDAT` | `COACTVW`, `COCOM01Y`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSMSG02Y`, `CSUSR01Y`, `CVACT01Y`, `CVACT02Y`, `CVACT03Y`, `CVCRD01Y`, `CVCUS01Y`, `DFHAID`, `DFHBMSCA` |
| `COACTUPC.cbl` | CAUP | COACTUP | 4,236 | **Account + customer update.** Full field-level edit suite (mandatory, Y/N, alpha/alphanum, numeric, signed 9V2, US phone, SSN, state, ZIP-vs-state, FICO 300–850, dates via `CSUTLDPY`), optimistic concurrency (re-read & compare before REWRITE), two-file update with SYNCPOINT/ROLLBACK. Uses `CSSETATY` COPY REPLACING to generate screen-attribute logic. | READ/READ UPDATE/REWRITE `ACCTDAT`, `CUSTDAT`; READ `CXACAIX` | `COACTUP`, `COCOM01Y`, `COTTL01Y`, `CSDAT01Y`, `CSLKPCDY`, `CSMSG01Y`, `CSMSG02Y`, `CSUSR01Y`, `CSUTLDPY`(+`CSUTLDWY`), `CSSETATY`, `CVACT01Y`, `CVACT03Y`, `CVCRD01Y`, `CVCUS01Y`, `DFHAID`, `DFHBMSCA` |
| `COCRDLIC.cbl` | CCLI | COCRDLI | 1,459 | Card list with forward/backward paging (STARTBR/READNEXT/READPREV), optional account/card filter, admin sees all cards; selection routes to view/update. | Browse `CARDDAT` | `COCOM01Y`, `COCRDLI`, `COCRDSL`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSMSG02Y`, `CSUSR01Y`, `CVACT02Y`, `CVCRD01Y`, `DFHAID`, `DFHBMSCA` |
| `COCRDSLC.cbl` | CCDL | COCRDSL | 887 | Card detail view by account + card number. | READ `CARDDAT` (and AIX path) | `COCOM01Y`, `COCRDSL`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSMSG02Y`, `CSUSR01Y`, `CVACT01Y`, `CVACT02Y`, `CVACT03Y`, `CVCRD01Y`, `CVCUS01Y`, `DFHAID`, `DFHBMSCA` |
| `COCRDUPC.cbl` | CCUP | COCRDUP | 1,560 | Card update: edits name (alpha), status (Y/N), expiry month (1–12) / year; re-read-compare-rewrite with SYNCPOINT. | READ/READ UPDATE/REWRITE `CARDDAT` | `COCOM01Y`, `COCRDUP`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSMSG02Y`, `CSUSR01Y`, `CVACT01Y`, `CVACT02Y`, `CVACT03Y`, `CVCRD01Y`, `CVCUS01Y`, `DFHAID`, `DFHBMSCA` |
| `COTRN00C.cbl` | CT00 | COTRN00 | 699 | Transaction list with paging; selection → `COTRN01C`. | Browse `TRANSACT` | `COCOM01Y`, `COTRN00`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CVTRA05Y`, `DFHAID`, `DFHBMSCA` |
| `COTRN01C.cbl` | CT01 | COTRN01 | 330 | Transaction detail view by transaction id. | READ `TRANSACT` | `COCOM01Y`, `COTRN01`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CVTRA05Y`, `DFHAID`, `DFHBMSCA` |
| `COTRN02C.cbl` | CT02 | COTRN02 | 783 | Add transaction: resolves account or card via xref, validates numeric fields, dates (`CSUTLDTC`), amount format `-99999999.99`, generates next TRAN-ID by READPREV on last key, confirm Y/N, WRITE. | READ `CCXREF`/`CXACAIX`; STARTBR/READPREV/WRITE `TRANSACT` | `COCOM01Y`, `COTRN02`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CVACT01Y`, `CVACT03Y`, `CVTRA05Y`, `DFHAID`, `DFHBMSCA` |
| `CORPT00C.cbl` | CR00 | CORPT00 | 649 | Report request: monthly / yearly / custom date range (validated via `CSUTLDTC`); builds a JCL stream (`TRNRPT00` job executing PROC `TRANREPT`) and submits it with WRITEQ TD to the `JOBS` internal-reader queue. | WRITEQ TD `JOBS` | `COCOM01Y`, `CORPT00`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CVTRA05Y`, `DFHAID`, `DFHBMSCA` |
| `COBIL00C.cbl` | CB00 | COBIL00 | 572 | Bill payment: pays current balance in full; rejects zero/negative balance; writes a `TRAN-TYPE-CD '02'`, category 2 transaction and rewrites account balance. | READ UPDATE/REWRITE `ACCTDAT`; READ `CXACAIX`; STARTBR/READPREV/WRITE `TRANSACT` | `COBIL00`, `COCOM01Y`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CVACT01Y`, `CVACT03Y`, `CVTRA05Y`, `DFHAID`, `DFHBMSCA` |
| `COUSR00C.cbl` | CU00 | COUSR00 | 695 | User list with paging; select U/D → update/delete. | Browse `USRSEC` | `COCOM01Y`, `COUSR00`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSUSR01Y`, `DFHAID`, `DFHBMSCA` |
| `COUSR01C.cbl` | CU01 | COUSR01 | 299 | Add user (id, names, password, type A/U). | WRITE `USRSEC` | `COCOM01Y`, `COUSR01`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSUSR01Y`, `DFHAID`, `DFHATTR`, `DFHBMSCA` |
| `COUSR02C.cbl` | CU02 | COUSR02 | 414 | Update user. | READ UPDATE/REWRITE `USRSEC` | `COCOM01Y`, `COUSR02`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSUSR01Y`, `DFHAID`, `DFHBMSCA` |
| `COUSR03C.cbl` | CU03 | COUSR03 | 359 | Delete user. | READ/DELETE `USRSEC` | `COCOM01Y`, `COUSR03`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSUSR01Y`, `DFHAID`, `DFHBMSCA` |

## 3. Program inventory — sub-applications

### 3.1 Authorization module (`app/app-authorization-ims-db2-mq/cbl/`) — IMS DB + DB2 + MQ

IMS database `DBPAUTP0` (HIDAM/VSAM): root segment `PAUTSUM0` (authorization summary per account, key `ACCNTID`) with child
`PAUTDTL1` (authorization detail, key `PAUT9CTS` timestamp). DB2 table `CARDDEMO.AUTHFRDS`.

| Program | Class | Tran | LOC | Purpose (inferred) | Key I/O | Copybooks |
|---|---|---|---|---|---|---|
| `COPAUA0C.cbl` | Online (MQ-triggered CICS) | CP00 | 1,026 | **Authorization decision engine.** Triggered by MQ (CICS RETRIEVE trigger msg), reads request from request queue, looks up card→xref→account→customer, applies decline rules (card not found/inactive, account closed, insufficient funds, card/merchant fraud), replies to reply-to queue with resp code `00`/`05` + reason (`0000`,`3100`,`4100`,`4200`,`4300`,`5100`,`5200`,`9000`), inserts summary/detail into IMS and updates counters. | MQOPEN/MQGET/MQPUT1/MQCLOSE; READ `CCXREF`, `ACCTDAT`, `CUSTDAT`; DLI SCHD/GU/ISRT/REPL/TERM; WRITEQ TD `CSSL` | `CCPAUERY`, `CCPAURLY`, `CCPAURQY`, `CIPAUDTY`, `CIPAUSMY`, `CVACT01Y`, `CVACT03Y`, `CVCUS01Y`, MQ `CMQ*` |
| `COPAUS0C.cbl` | Online | CPVS | 1,032 | Pending-authorization summary screen for an account (IMS root + children browse, account/customer/card enrichment). | READ `ACCTDAT`, `CXACAIX`, `CUSTDAT`; DLI SCHD/GU/GNP/TERM | `CIPAUDTY`, `CIPAUSMY`, `COCOM01Y`, `COPAU00`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSMSG02Y`, `CVACT01Y`, `CVACT02Y`, `CVACT03Y`, `CVCUS01Y`, `DFHAID`, `DFHBMSCA` |
| `COPAUS1C.cbl` | Online | CPVD | 604 | Authorization detail view; PF-key toggles fraud flag (REPL detail segment) and LINKs to `COPAUS2C`. | DLI SCHD/GU/GNP/REPL/TERM; LINK `COPAUS2C` | `CIPAUDTY`, `CIPAUSMY`, `COCOM01Y`, `COPAU01`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSMSG02Y`, `DFHAID`, `DFHBMSCA` |
| `COPAUS2C.cbl` | Online (LINKed) | — | 244 | Persist fraud marking to DB2: INSERT into `CARDDEMO.AUTHFRDS` or UPDATE `AUTH_FRAUD`/`FRAUD_RPT_DATE`. | SQL INSERT/UPDATE `CARDDEMO.AUTHFRDS` | `DFHBMSCA`, DCLGEN `AUTHFRDS`, `SQLCA` |
| `CBPAUP0C.cbl` | Batch (IMS BMP) | — | 386 | Purge expired pending authorizations: GN root / GNP children, DLET details older than threshold, DLET empty summaries, CHKP. | DLI GN/GNP/DLET/CHKP | `CIPAUDTY`, `CIPAUSMY` |
| `PAUDBLOD.CBL` | Batch (IMS) | — | 369 | Load IMS DB from two sequential files (root + child) using `CBLTDLI` ISRT. | R `INFILE1`, `INFILE2`; DLI ISRT | `CIPAUDTY`, `CIPAUSMY`, `IMSFUNCS`, `PAUTBPCB` |
| `PAUDBUNL.CBL` | Batch (IMS) | — | 317 | Unload IMS DB to two sequential files (GN/GNP). | DLI GN/GNP; W `OUTFIL1`, `OUTFIL2` | `CIPAUDTY`, `CIPAUSMY`, `IMSFUNCS`, `PAUTBPCB` |
| `DBUNLDGS.CBL` | Batch (IMS/GSAM) | — | 366 | Unload IMS DB into GSAM datasets via DL/I ISRT on GSAM PCBs. | DLI GN/GNP; GSAM ISRT (`PASFLPCB`, `PADFLPCB`) | `CIPAUDTY`, `CIPAUSMY`, `IMSFUNCS` (+PCB copybooks) |

### 3.2 Transaction Type module (`app/app-transaction-type-db2/cbl/`) — DB2

Tables `CARDDEMO.TRANSACTION_TYPE (TR_TYPE, TR_DESCRIPTION)` and `CARDDEMO.TRANSACTION_TYPE_CATEGORY (TRC_TYPE_CODE, TRC_TYPE_CATEGORY, TRC_CAT_DATA)` with FK `ON DELETE RESTRICT`.

| Program | Class | Tran | LOC | Purpose (inferred) | Key I/O | Copybooks |
|---|---|---|---|---|---|---|
| `COTRTLIC.cbl` | Online | CTLI | 2,098 | Transaction-type list with cursor-based paging (two cursors forward/back), inline delete (checks category count first) and route to update. | SQL DECLARE/OPEN/FETCH/CLOSE cursors, SELECT COUNT, DELETE, UPDATE | `COCOM01Y`, `COTRTLI`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSUSR01Y`, `CVACT02Y`, `CVCRD01Y`, `DFHAID`, `DFHBMSCA`, `DCLTRTYP`, `CSDB2RWY`/`CSDB2RPY`, `SQLCA` |
| `COTRTUPC.cbl` | Online | CTTU | 1,702 | Transaction-type add/update/delete screen with field edits (type code numeric/non-zero, description mandatory), SELECT-then-UPDATE/INSERT, SYNCPOINT. | SQL SELECT/INSERT/UPDATE/DELETE | `COCOM01Y`, `COTRTUP`, `COTTL01Y`, `CSDAT01Y`, `CSMSG01Y`, `CSMSG02Y`, `CSUSR01Y`, `CVCRD01Y`, `DFHAID`, `DFHBMSCA`, `DCLTRTYP`, `DCLTRCAT`, `SQLCA` |
| `COBTUPDT.cbl` | Batch (DB2) | — | 237 | Batch maintenance of `TRANSACTION_TYPE` from a control file: record prefix I/U/D → INSERT/UPDATE/DELETE. | R `INPFILE`; SQL INSERT/UPDATE/DELETE | `DCLTRTYP`, `SQLCA` |

### 3.3 VSAM/MQ module (`app/app-vsam-mq/cbl/`) — MQ request/reply

| Program | Class | Tran | LOC | Purpose (inferred) | Key I/O | Copybooks |
|---|---|---|---|---|---|---|
| `COACCT01.cbl` | Online (MQ-triggered CICS) | CDRA | 620 | Account inquiry over MQ: GET request (account id) → READ `ACCTDAT` → PUT formatted reply to reply-to queue; errors to error queue. | MQOPEN/MQGET/MQPUT/MQCLOSE; READ `ACCTDAT` | `CVACT01Y`, `CMQGMOV`, `CMQMDV`, `CMQODV`, `CMQPMOV`, `CMQTML`, `CMQV` |
| `CODATE01.cbl` | Online (MQ-triggered CICS) | CDRD | 524 | System-date inquiry over MQ (ASKTIME/FORMATTIME → reply). | MQOPEN/MQGET/MQPUT/MQCLOSE | `CMQGMOV`, `CMQMDV`, `CMQODV`, `CMQPMOV`, `CMQTML`, `CMQV` |

## 4. BMS maps (CICS 3270 screens)

Each map has a `.bms` source and a generated symbolic copybook in `cpy-bms/` with the same name. One map per online screen program.

| Mapset | Program | Screen | Mapset | Program | Screen |
|---|---|---|---|---|---|
| `COSGN00` | COSGN00C | Sign-on | `COTRN00` | COTRN00C | Transaction list |
| `COMEN01` | COMEN01C | Main menu | `COTRN01` | COTRN01C | Transaction view |
| `COADM01` | COADM01C | Admin menu | `COTRN02` | COTRN02C | Transaction add |
| `COACTVW` | COACTVWC | Account view | `CORPT00` | CORPT00C | Report request |
| `COACTUP` | COACTUPC | Account update | `COBIL00` | COBIL00C | Bill payment |
| `COCRDLI` | COCRDLIC | Card list | `COUSR00`–`COUSR03` | COUSR00C–03C | User list/add/update/delete |
| `COCRDSL` | COCRDSLC | Card view | `COPAU00` / `COPAU01` (auth) | COPAUS0C / COPAUS1C | Auth summary / detail |
| `COCRDUP` | COCRDUPC | Card update | `COTRTLI` / `COTRTUP` (DB2) | COTRTLIC / COTRTUPC | Tran-type list / update |

Programs without a map: `COPAUS2C` (LINKed), `COPAUA0C`, `COACCT01`, `CODATE01` (MQ-triggered), all batch programs.

## 5. JCL catalog — core (`app/jcl/`)

Dataset HLQ is `AWS.M2.CARDDEMO`. GDG = generation data group.

| Job | Steps (in order) | Purpose |
|---|---|---|
| `ACCTFILE.jcl` | STEP05 IDCAMS (delete) → STEP10 IDCAMS (DEFINE KSDS, KEYS(11 0)) → STEP15 IDCAMS REPRO `ACCTDATA.PS` → `ACCTDATA.VSAM.KSDS` | (Re)build Account master |
| `CARDFILE.jcl` | CLCIFIL SDSF (CEMT close) → STEP05/10 IDCAMS delete+DEFINE KSDS (KEYS(16 0)) → STEP15 REPRO `CARDDATA.PS` → KSDS → STEP40/50/60 IDCAMS define AIX on account (KEYS(11 16)), BLDINDEX, DEFINE PATH → OPCIFIL SDSF (open) | (Re)build Card master + AIX |
| `CUSTFILE.jcl` | CLCIFIL SDSF → STEP05/10 IDCAMS → STEP15 REPRO `CUSTDATA.PS` → KSDS (KEYS(9 0)) → OPCIFIL SDSF | (Re)build Customer master |
| `XREFFILE.jcl` | STEP05/10 IDCAMS → STEP15 REPRO `CARDXREF.PS` → KSDS (KEYS(16 0)) → STEP20/25/30 AIX by account + BLDINDEX + PATH | (Re)build Card xref + account AIX |
| `TRANFILE.jcl` | CLCIFIL SDSF → STEP05/10 IDCAMS → STEP15 REPRO `DALYTRAN.PS.INIT` → `TRANSACT.VSAM.KSDS` → STEP20/25/30 AIX + BLDINDEX + PATH → OPCIFIL SDSF | (Re)build Transaction master |
| `TRANIDX.jcl` | STEP20/25/30 IDCAMS DEFINE AIX (KEYS(26 304) proc-timestamp), BLDINDEX, DEFINE PATH | Add alternate index to TRANSACT |
| `TRANTYPE.jcl` | STEP05/10 IDCAMS → STEP15 REPRO `TRANTYPE.PS` → KSDS (KEYS(2 0)) | Load transaction type reference |
| `TRANCATG.jcl` | STEP05/10 IDCAMS → STEP15 REPRO `TRANCATG.PS` → KSDS (KEYS(6 0)) | Load transaction category reference |
| `DISCGRP.jcl` | STEP05/10 IDCAMS → STEP15 REPRO `DISCGRP.PS` → KSDS (KEYS(16 0)) | Load disclosure/interest groups |
| `TCATBALF.jcl` | STEP05/10 IDCAMS → STEP15 REPRO `TCATBALF.PS` → KSDS (KEYS(17 0)) | Load transaction category balances |
| `DUSRSECJ.jcl` | PREDEL IEFBR14 → STEP01 IEBGENER (instream users → `USRSEC.PS`) → STEP02 IDCAMS DEFINE KSDS → STEP03 REPRO → `USRSEC.VSAM.KSDS` | Initial user security load |
| `DEFCUST.jcl` | STEP05 IDCAMS (DEFINE `AWS.CUSTDATA.CLUSTER`) ×2 | Alternate customer cluster define |
| `DEFGDGB.jcl` | STEP05 IDCAMS DEFINE GDG bases | Create GDG bases (rejects, backups, reports) |
| `DEFGDGD.jcl` | STEP10 IDCAMS GDG → STEP20 IEBGENER `TRANTYPE.PS`→`TRANTYPE.BKUP(+1)` → STEP30 IDCAMS → STEP40 IEBGENER `TRANCATG.PS`→`TRANCATG.PS.BKUP(+1)` → STEP50 IDCAMS → STEP60 IEBGENER `DISCGRP.PS`→`DISCGRP.BKUP(+1)` | GDG bases + first backup generations for DB2-related reference data |
| `DALYREJS.jcl` | STEP05 IDCAMS | Define rejects GDG base |
| `REPTFILE.jcl` | STEP05 IDCAMS | Define report GDG base |
| `ESDSRRDS.jcl` | PREDEL IEFBR14 → STEP01 IEBGENER → STEP02 IDCAMS DEFINE ESDS → STEP03 REPRO → STEP04 DEFINE RRDS → STEP05 REPRO | Demonstration ESDS/RRDS copies of USRSEC |
| `CLOSEFIL.jcl` | CLCIFIL SDSF (`CEMT SET FILE ... CLOSED`) | Close CICS files before batch |
| `OPENFIL.jcl` | OPCIFIL SDSF (`CEMT SET FILE ... OPEN`) | Re-open CICS files after batch |
| `WAITSTEP.jcl` | WAIT `COBSWAIT` (SYSIN = centiseconds) | Scheduler pause |
| `POSTTRAN.jcl` | STEP15 `CBTRN02C` — DD: `TRANFILE`=TRANSACT.VSAM.KSDS, `DALYTRAN`=DALYTRAN.PS, `XREFFILE`, `DALYREJS`=DALYREJS(+1), `ACCTFILE`, `TCATBALF` | Daily transaction posting |
| `INTCALC.jcl` | STEP15 `CBACT04C` PARM='2022071800' — DD: `TCATBALF`, `XREFFILE`(AIX PATH), `ACCTFILE`, `DISCGRP`, `TRANSACT`=SYSTRAN(+1) | Monthly interest calculation |
| `COMBTRAN.jcl` | STEP05R SORT merge `TRANSACT.BKUP(0)` + `SYSTRAN(0)` → `TRANSACT.COMBINED(+1)` → STEP10 IDCAMS REPRO → `TRANSACT.VSAM.KSDS` | Merge system (interest) transactions back into master |
| `TRANBKP.jcl` | STEP05R PROC `REPROC` (`TRANSACT.VSAM.KSDS` → `TRANSACT.BKUP(+1)`) → STEP05 IDCAMS delete/define → STEP10 IDCAMS (COND 4,LT) | Backup and reinitialise transaction master |
| `CREASTMT.JCL` | DELDEF01 IDCAMS → STEP010 SORT TRANSACT KSDS → `TRXFL.SEQ` (by card, id) → STEP020 IDCAMS REPRO → `TRXFL.VSAM.KSDS` → STEP030 IEFBR14 delete STATEMNT.PS/HTML → STEP040 `CBSTM03A` (TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE → STMTFILE, HTMLFILE) | Statement generation |
| `TRANREPT.jcl` | STEP05R PROC `REPROC` (backup KSDS) → STEP05R SORT with INCLUDE `TRAN-PROC-DT` between PARM dates → `TRANSACT.DALY(+1)` → STEP10R `CBTRN03C` (TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM → TRANREPT(+1)) | Transaction report (same flow as `proc/TRANREPT.prc`, submitted from CICS by CORPT00C) |
| `PRTCATBL.jcl` | DELDEF IEFBR14 → STEP05R PROC `REPROC` (`TCATBALF` → BKUP(+1)) → STEP10R SORT → `TCATBALF.REPT` | Category-balance report extract |
| `READACCT.jcl` | PREDEL IEFBR14 → STEP05 `CBACT01C` (ACCTFILE → OUTFILE/ARRYFILE/VBRCFILE) | Account file dump |
| `READCARD.jcl` | STEP05 `CBACT02C` | Card file dump |
| `READCUST.jcl` | STEP05 `CBCUS01C` | Customer file dump |
| `READXREF.jcl` | STEP05 `CBACT03C` | Xref file dump |
| `CBEXPORT.jcl` | STEP01 IDCAMS (delete) → STEP02 `CBEXPORT` (5 KSDS → `EXPORT.DATA`) | Branch export |
| `CBIMPORT.jcl` | STEP01 `CBIMPORT` (`EXPORT.DATA` → *.IMPORT ×4 + `IMPORT.ERRORS`) | Branch import |
| `CBADMCDJ.jcl` | STEP1 DFHCSDUP | Install CICS CSD definitions |
| `INTRDRJ1.JCL` | IDCAMS REPRO FTP.TEST → BKUP → STEP01 IEBGENER copies `JCL(INTRDRJ2)` to INTRDR | Internal-reader chaining demo |
| `INTRDRJ2.JCL` | IDCAMS REPRO BKUP → BKUP.INTRDR | Second hop of INTRDR demo |
| `FTPJCL.JCL` | STEP1 FTP | FTP transfer demo |
| `TXT2PDF1.JCL` | TXT2PDF IKJEFT1B (REXX) on `STATEMNT.PS` | Convert text statement to PDF |

Procedures: `proc/REPROC.prc` (parameterised IDCAMS REPRO, control cards `ctl/REPROCT.ctl`), `proc/TRANREPT.prc` (backup → SORT by date → `CBTRN03C`).

## 6. JCL catalog — sub-applications

| Job | Steps | Purpose |
|---|---|---|
| `app-authorization-ims-db2-mq/jcl/CBPAUP0J.jcl` | STEP01 DFSRRC00 (BMP) running `CBPAUP0C` | Purge expired authorizations |
| `.../DBPAUTP0.jcl` | STEPDEL IEFBR14 → UNLOAD DFSRRC00 (IMS HD unload `DFSURGU0` of `PAUTHDB`/`PAUTHDBX` → `IMSDATA.DBPAUTP0`) | IMS DB unload (utility) |
| `.../LOADPADB.JCL` | STEP01 DFSRRC00 `PAUDBLOD` (INFILE1 ROOT.FILEO, INFILE2 CHILD.FILEO) | Load IMS DB from files |
| `.../UNLDPADB.JCL` | STEP0 IEFBR14 → STEP01 DFSRRC00 `PAUDBUNL` (→ OUTFIL1 ROOT.FILEO, OUTFIL2 CHILD.FILEO) | Unload IMS DB to files |
| `.../UNLDGSAM.JCL` | STEP01 DFSRRC00 `DBUNLDGS` (→ ROOT.GSAM, CHILD.GSAM) | Unload IMS DB to GSAM |
| `app-transaction-type-db2/jcl/CREADB21.jcl` | FREEPLN IKJEFT01 (`DB2FREE`) → CRCRDDB IKJEFT01 DSNTIAD (`DB2CREAT` DDL) → LDTTYPE IEFBR14 → RUNTEP2 IKJEFT01 DSNTEP4 (`DB2LTTYP`) → LDTCCAT DSNTEP4 (`DB2LTCAT`) | Create DB2 database/tables and load reference rows |
| `.../MNTTRDB2.jcl` | STEP1 IKJEFT01 running `COBTUPDT` (INPFILE) | Batch maintain TRANSACTION_TYPE |
| `.../TRANEXTR.jcl` | STEP10 IEBGENER TRANTYPE.PS → BKUP(+1) → STEP20 IEBGENER TRANCATG.PS → BKUP(+1) → STEP30 IEFBR14 delete → STEP40 IKJEFT01 DSNTIAUL unload TRANSACTION_TYPE → TRANTYPE.PS → STEP50 DSNTIAUL unload TRANSACTION_TYPE_CATEGORY → TRANCATG.PS | Refresh VSAM reference files from DB2 |

## 7. Scheduler definitions (`app/scheduler/CardDemo.controlm`)

| Folder | Job sequence (IN/OUT conditions) |
|---|---|
| DAILY-TransactionBackup | CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL |
| WEEKLY-TransactionTypesDBRefresh | MNTTRDB2 → { TRANEXTR } and { DisclosureGroupsRefresh: CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL } (SMART folders, Saturdays) |
| MONTHLY-InterestCalculation | CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL |

The equivalent CA-7 definitions are in `CardDemo.ca7`. Note the `POSTTRAN → INTCALC → CREASTMT → TRANRPT` business
pipeline documented in the README is not itself expressed as one Control-M folder; see DEPENDENCY_MAP.md §4.

## 8. Other supporting assets

| Directory | Contents |
|---|---|
| `app/asm/`, `app/maclib/` | `COBDATFT.asm` (date formatter called by CBACT01C), `MVSWAIT.asm` (STIMER wait called by COBSWAIT), macros `ASMWAIT.mac`, `COCDATFT.mac` |
| `app/csd/CARDDEMO.CSD` + sub-app `csd/` | CICS program/transaction/file/mapset definitions (transactions CC00, CM00, CA00, CAVW, CAUP, CCLI, CCDL, CCUP, CT00–CT02, CR00, CB00, CU00–CU03, CDV1 (→ `COCRDSEC`, program not present in source); CPVS, CPVD, CP00; CTLI, CTTU; CDRA, CDRD) |
| `app/ims/` (auth module) | `DBPAUTP0.dbd` (HIDAM), `DBPAUTX0.dbd` (index), `PADFLDBD.DBD`/`PASFLDBD.DBD` (GSAM), PSBs `PSBPAUTB`, `PSBPAUTL`, `PAUTBUNL`, `DLIGSAMP` |
| `app/ddl/`, `app/dcl/` (sub-apps) | `AUTHFRDS.ddl`/`XAUTHFRD.ddl`, `TRNTYPE.ddl`, `TRNTYCAT.ddl` + indexes; DCLGEN copybooks `AUTHFRDS.dcl`, `DCLTRTYP.dcl`, `DCLTRCAT.dcl` |
| `app/ctl/`, sub-app `ctl/` | IDCAMS REPRO cards, DB2 DSNTEP/DSNTIAD/DSNTIAUL control statements |
| `app/data/EBCDIC/` | Sample EBCDIC PS datasets for ACCTDATA, CARDDATA, CARDXREF, CUSTDATA, DALYTRAN, DISCGRP, TCATBALF, TRANCATG, TRANTYPE, USRSEC, EXPORT.DATA |
| `app/catlg/LISTCAT.txt` | Catalog listing of the VSAM clusters |
