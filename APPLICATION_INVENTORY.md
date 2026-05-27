# APPLICATION INVENTORY — CardDemo COBOL Estate

> Generated from analysis of the `app/` directory tree in `uc-legacy-modernization-cobol-to-java`.

---

## 1. COBOL Programs — `app/cbl/` (Core CardDemo)

| # | Filename | Program-ID | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|----------------|---------|---------------------|---------------------|
| 1 | CBACT01C.cbl | CBACT01C | Batch | Read VSAM account file and write to sequential output files (flat, array, variable-length) | **Read:** ACCTFILE (KSDS) · **Write:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Batch | Read and print card data file | **Read:** CARDFILE (KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Batch | Read and print account cross-reference data file | **Read:** XREFFILE (KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Batch | Interest calculator — compute interest and fees per account using transaction category balances and disclosure group rates | **Read:** TCATBALF, XREFFILE, ACCTFILE, DISCGRP · **Write:** TRANSACT | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Batch | Read and print customer data file | **Read:** CUSTFILE (KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Batch | Export customer, account, xref, transaction, and card data to a sequential export file for branch migration | **Read:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · **Write:** EXPFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 7 | CBIMPORT.cbl | CBIMPORT | Batch | Import data from branch migration export file into individual entity output files with validation | **Read:** EXPFILE · **Write:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 8 | CBSTM03A.CBL | CBSTM03A | Batch | Print account statements from transaction data; generates text and HTML output | **Read:** (via sub) TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE · **Write:** STMTFILE, HTMLFILE | COSTM01, CUSTREC, CVACT01Y, CVACT03Y |
| 9 | CBSTM03B.CBL | CBSTM03B | Batch (Sub) | Subroutine for CBSTM03A — opens/reads transaction, xref, customer, and account files | **Read:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | _(none — inline definitions)_ |
| 10 | CBTRN01C.cbl | CBTRN01C | Batch | Post records from daily transaction file — validate card, lookup xref, read account | **Read:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE · **Write:** TRANFILE | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVTRA06Y |
| 11 | CBTRN02C.cbl | CBTRN02C | Batch | Post daily transactions — validate, update account balances, write rejects, update tran category balances | **Read:** DALYTRAN, XREFFILE, ACCTFILE · **Write:** TRANFILE, DALYREJS, TCATBALF | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y |
| 12 | CBTRN03C.cbl | CBTRN03C | Batch | Print daily transaction detail report with type/category lookups and page/account/grand totals | **Read:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM · **Write:** TRANREPT | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | COBSWAIT | Batch (Utility) | Wait utility — pauses execution for specified centiseconds (PARM-driven) | _(none)_ | _(none)_ |
| 14 | CSUTLDTC.cbl | CSUTLDTC | Batch (Utility) | Date validation utility — validates and converts dates using LE callable service CEEDAYS | _(none)_ | _(none)_ |
| 15 | COACTUPC.cbl | COACTUPC | Online (CICS) | Accept and process account update — full field-level editing with validation (credit limits, dates, SSN, balances) | **CICS READ/REWRITE:** ACCTDAT, CARDXREF, CUSTDAT | COACTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CSUTLDPY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, CSSTRPFY, CSUTLDWY, DFHAID, DFHBMSCA |
| 16 | COACTVWC.cbl | COACTVWC | Online (CICS) | Account view — display account details with related card and customer info | **CICS READ:** ACCTDAT, CARDXREF (AIX), CUSTDAT | COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, CSSTRPFY, DFHAID, DFHBMSCA |
| 17 | COADM01C.cbl | COADM01C | Online (CICS) | Admin menu — present admin-only options (user CRUD, DB2 tran-type maintenance) | **CICS READ:** USRSEC | COADM01, COADM02Y, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 18 | COBIL00C.cbl | COBIL00C | Online (CICS) | Bill payment — pay account balance in full or partial; writes payment transaction | **CICS READ/REWRITE:** ACCTDAT, TRANSACT, CARDXREF (AIX) | COBIL00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 19 | COCRDLIC.cbl | COCRDLIC | Online (CICS) | List credit cards — paginated browse of card file with select-for-detail/update | **CICS STARTBR/READNEXT/READPREV/ENDBR:** CARDDAT, CARDAIX | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CSSTRPFY, DFHAID, DFHBMSCA |
| 20 | COCRDSLC.cbl | COCRDSLC | Online (CICS) | Credit card detail view — display card details with customer info | **CICS READ:** CARDDAT, CUSTDAT | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, CSSTRPFY, DFHAID, DFHBMSCA |
| 21 | COCRDUPC.cbl | COCRDUPC | Online (CICS) | Credit card update — edit card status, name, expiry; rewrite card record | **CICS READ/REWRITE/WRITE:** CARDDAT | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, CSSTRPFY, DFHAID, DFHBMSCA |
| 22 | COMEN01C.cbl | COMEN01C | Online (CICS) | Main menu for regular users — 11 options dispatching to sub-programs via XCTL | **CICS XCTL to:** various programs | COCOM01Y, COMEN01, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 23 | CORPT00C.cbl | CORPT00C | Online (CICS) | Transaction report submission — collect date range, submit batch JCL via Internal Reader (WRITEQ TD) | **CICS WRITEQ TD:** INTRDR | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 24 | COSGN00C.cbl | COSGN00C | Online (CICS) | Signon screen — authenticate user against USRSEC file, route to admin or regular menu | **CICS READ:** USRSEC | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 25 | COTRN00C.cbl | COTRN00C | Online (CICS) | List transactions — paginated browse of TRANSACT file | **CICS STARTBR/READNEXT/READPREV/ENDBR:** TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN01C.cbl | COTRN01C | Online (CICS) | View a single transaction from TRANSACT file | **CICS READ:** TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | COTRN02C.cbl | COTRN02C | Online (CICS) | Add a new transaction — validate card via xref, write to TRANSACT file | **CICS READ:** CARDXREF (AIX), CARDXREF · **CICS WRITE:** TRANSACT | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 28 | COUSR00C.cbl | COUSR00C | Online (CICS) | List all users from USRSEC file — paginated browse | **CICS STARTBR/READNEXT/READPREV/ENDBR:** USRSEC | COCOM01Y, COTTL01Y, COUSR00, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR01C.cbl | COUSR01C | Online (CICS) | Add a new user (regular or admin) to USRSEC file | **CICS WRITE:** USRSEC | COCOM01Y, COTTL01Y, COUSR01, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR02C.cbl | COUSR02C | Online (CICS) | Update an existing user in USRSEC file | **CICS READ/REWRITE:** USRSEC | COCOM01Y, COTTL01Y, COUSR02, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | COUSR03C.cbl | COUSR03C | Online (CICS) | Delete a user from USRSEC file | **CICS READ/DELETE:** USRSEC | COCOM01Y, COTTL01Y, COUSR03, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. COBOL Programs — `app/app-authorization-ims-db2-mq/cbl/` (Authorization Module)

| # | Filename | Program-ID | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|----------------|---------|---------------------|---------------------|
| 1 | CBPAUP0C.cbl | CBPAUP0C | Batch (IMS) | Delete expired pending authorization messages from IMS DB | **IMS DL/I:** GN/GNP/DLET on PAUTHDB | CIPAUDTY, CIPAUSMY |
| 2 | COPAUA0C.cbl | COPAUA0C | Online (CICS/IMS/MQ) | Card authorization decision engine — reads request from MQ, validates card/account/customer via CICS VSAM, makes approve/decline decision, writes IMS, replies via MQ | **MQ:** MQGET/MQPUT1 · **CICS READ:** ACCTDAT, CUSTDAT, CARDXREF · **IMS DL/I:** PAUTHDB | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, CVACT01Y, CVACT03Y, CVCUS01Y, CMQ* |
| 3 | COPAUS0C.cbl | COPAUS0C | Online (CICS/IMS/BMS) | Summary view of pending authorization messages — paginated list from IMS DB | **IMS DL/I:** GN on PAUTHDB · **CICS BMS:** SEND/RECEIVE MAP | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y-03Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 4 | COPAUS1C.cbl | COPAUS1C | Online (CICS/IMS/BMS) | Detail view of a single authorization message with option to mark as fraud | **IMS DL/I:** GU on PAUTHDB · **CICS BMS:** SEND/RECEIVE MAP · **CICS LINK** | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, DFHAID, DFHBMSCA |
| 5 | COPAUS2C.cbl | COPAUS2C | Online (CICS/IMS/DB2) | Mark authorization message as fraud — update IMS record and insert DB2 fraud record | **IMS DL/I:** GU/REPL on PAUTHDB · **DB2 SQL:** INSERT/UPDATE AUTHFRDS | CIPAUDTY |
| 6 | DBUNLDGS.CBL | DBUNLDGS | Batch (IMS) | Unload IMS PAUTHDB to GSAM files (root + child segments) | **IMS DL/I:** GN/GNP on PAUTHDB · **GSAM ISRT:** PASFILOP, PADFILOP | CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB |
| 7 | PAUDBLOD.CBL | PAUDBLOD | Batch (IMS) | Load IMS PAUTHDB from sequential input files | **Read:** INFILE1, INFILE2 · **IMS DL/I:** ISRT on PAUTHDB | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |
| 8 | PAUDBUNL.CBL | PAUDBUNL | Batch (IMS) | Unload IMS PAUTHDB to sequential flat files (root + child) | **IMS DL/I:** GN/GNP on PAUTHDB · **Write:** OUTFIL1, OUTFIL2 | CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |

---

## 3. COBOL Programs — `app/app-transaction-type-db2/cbl/` (Transaction Type DB2 Module)

| # | Filename | Program-ID | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|----------------|---------|---------------------|---------------------|
| 1 | COBTUPDT.cbl | COBTUPDT | Batch (DB2) | Batch update of transaction type DB2 table from sequential input file (add/update/delete) | **Read:** INPFILE · **DB2 SQL:** INSERT/UPDATE/DELETE on TRNTYPE | _(DCLTRTYP via SQL INCLUDE)_ |
| 2 | COTRTLIC.cbl | COTRTLIC | Online (CICS/DB2) | List transaction types from DB2 — paginated list with select-for-update/delete via cursor | **DB2 SQL:** DECLARE CURSOR, FETCH, DELETE on TRNTYPE · **CICS BMS** | COCOM01Y, COTRTLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CSSTRPFY, CSDB2RWY, DFHAID, DFHBMSCA |
| 3 | COTRTUPC.cbl | COTRTUPC | Online (CICS/DB2) | Accept and process transaction type update — field-level editing, INSERT/UPDATE on TRNTYPE and TRNTYCAT DB2 tables | **DB2 SQL:** SELECT/INSERT/UPDATE on TRNTYPE, TRNTYCAT · **CICS BMS** | COCOM01Y, COTRTUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVCRD01Y, CSSTRPFY, CSUTLDWY, CSSETATY, DFHAID, DFHBMSCA |

---

## 4. COBOL Programs — `app/app-vsam-mq/cbl/` (VSAM-MQ Bridge Module)

| # | Filename | Program-ID | Classification | Purpose | Key I/O Operations | Copybooks Referenced |
|---|----------|-----------|----------------|---------|---------------------|---------------------|
| 1 | COACCT01.cbl | COACCT01 | Online (CICS/MQ) | Account inquiry via MQ — receives request from MQ queue, reads VSAM account file via CICS, sends reply to MQ | **MQ:** MQGET/MQPUT · **CICS READ:** ACCTDAT | CVACT01Y, CMQ* (CMQV, CMQMDV, CMQODV, CMQGMOV, CMQPMOV, CMQTML) |
| 2 | CODATE01.cbl | CODATE01 | Online (CICS/MQ) | Date service via MQ — receives date format request from MQ, uses CICS ASKTIME/FORMATTIME, replies via MQ | **MQ:** MQGET/MQPUT · **CICS ASKTIME/FORMATTIME** | CMQ* (CMQV, CMQMDV, CMQODV, CMQGMOV, CMQPMOV, CMQTML) |

---

## 5. JCL Jobs — `app/jcl/` (Core Batch Jobs)

| # | JCL Member | Steps (EXEC PGM=) | Purpose |
|---|------------|-------------------|---------|
| 1 | ACCTFILE.jcl | IDCAMS ×3 | Define VSAM KSDS for account data; delete/define cluster; load from sequential PS |
| 2 | CARDFILE.jcl | SDSF, IDCAMS ×5, SDSF | Close CICS file, delete/define VSAM KSDS + AIX for card data, reopen CICS file |
| 3 | CBADMCDJ.jcl | DFHCSDUP | Define CICS CSD resources — programs, mapsets, transactions, file definitions |
| 4 | CBEXPORT.jcl | IDCAMS, CBEXPORT | Delete old export; execute CBEXPORT batch program |
| 5 | CBIMPORT.jcl | CBIMPORT | Execute CBIMPORT batch program to import from export file |
| 6 | CLOSEFIL.jcl | SDSF | Close CICS-managed VSAM files via SDSF/CEMT commands |
| 7 | COMBTRAN.jcl | SORT, IDCAMS | Merge and sort multiple daily transaction files into VSAM TRANSACT file |
| 8 | CREASTMT.JCL | IDCAMS, SORT, IDCAMS, IEFBR14, CBSTM03A | Sort transactions, load to KSDS, generate account statements via CBSTM03A |
| 9 | CUSTFILE.jcl | SDSF, IDCAMS ×3, SDSF | Close CICS file, delete/define VSAM KSDS for customer data, reopen |
| 10 | DALYREJS.jcl | IDCAMS | Define VSAM ESDS for daily transaction rejects |
| 11 | DEFCUST.jcl | IDCAMS ×2 | Define alternate VSAM cluster for customer data |
| 12 | DEFGDGB.jcl | IDCAMS | Define GDG base for backup datasets |
| 13 | DEFGDGD.jcl | IDCAMS ×3, IEBGENER ×3 | Define GDG base + initial generations for TRANTYPE, TRANCATG, DISCGRP backups |
| 14 | DISCGRP.jcl | IDCAMS ×3 | Define VSAM KSDS for disclosure group data; load from sequential PS |
| 15 | DUSRSECJ.jcl | IEFBR14, IEBGENER, IDCAMS ×2 | Create user security sequential PS file and load into VSAM KSDS |
| 16 | ESDSRRDS.jcl | IEFBR14, IEBGENER, IDCAMS ×4 | Create ESDS and RRDS VSAM clusters for demonstration/testing |
| 17 | FTPJCL.JCL | FTP | FTP file transfer job |
| 18 | INTCALC.jcl | CBACT04C | Execute interest calculation batch program |
| 19 | INTRDRJ1.JCL | IDCAMS, IEBGENER | Backup file and submit INTRDRJ2 via Internal Reader |
| 20 | INTRDRJ2.JCL | IDCAMS | Secondary Internal Reader job — copy backup file |
| 21 | OPENFIL.jcl | SDSF | Open CICS-managed VSAM files via SDSF/CEMT commands |
| 22 | POSTTRAN.jcl | CBTRN02C | Execute daily transaction posting batch program |
| 23 | PRTCATBL.jcl | IEFBR14, REPROC, SORT | Print transaction category balance report via SORT with control breaks |
| 24 | READACCT.jcl | IEFBR14, CBACT01C | Execute account file reader batch program |
| 25 | READCARD.jcl | CBACT02C | Execute card file reader batch program |
| 26 | READCUST.jcl | CBCUS01C | Execute customer file reader batch program |
| 27 | READXREF.jcl | CBACT03C | Execute cross-reference file reader batch program |
| 28 | REPTFILE.jcl | IDCAMS | Define VSAM KSDS for report output file |
| 29 | TCATBALF.jcl | IDCAMS ×3 | Define VSAM KSDS for transaction category balance file; load from PS |
| 30 | TRANBKP.jcl | REPROC, IDCAMS ×2 | Backup transaction file to GDG generation |
| 31 | TRANCATG.jcl | IDCAMS ×3 | Define VSAM KSDS for transaction category data; load from PS |
| 32 | TRANFILE.jcl | SDSF, IDCAMS ×5, SDSF | Close CICS file, delete/define VSAM KSDS + AIX for transaction data, reopen |
| 33 | TRANIDX.jcl | IDCAMS ×3 | Define VSAM AIX and PATH for transaction file alternate index |
| 34 | TRANREPT.jcl | REPROC, SORT, CBTRN03C | Backup transaction file, sort, execute transaction detail report program |
| 35 | TRANTYPE.jcl | IDCAMS ×3 | Define VSAM KSDS for transaction type reference data; load from PS |
| 36 | TXT2PDF1.JCL | IKJEFT1B | Convert text statement file to PDF using TXT2PDF REXX exec |
| 37 | WAITSTEP.jcl | COBSWAIT | Execute wait utility (pause batch pipeline between stages) |
| 38 | XREFFILE.jcl | IDCAMS ×6 | Define VSAM KSDS + AIX + PATH for card cross-reference file |

---

## 6. JCL Jobs — `app/app-authorization-ims-db2-mq/jcl/` (Authorization Module)

| # | JCL Member | Steps (EXEC PGM=) | Purpose |
|---|------------|-------------------|---------|
| 1 | CBPAUP0J.jcl | DFSRRC00 (IMS BMP) | Execute CBPAUP0C — purge expired pending authorizations |
| 2 | DBPAUTP0.jcl | IEFBR14, DFSRRC00 ×2 | Delete old unload dataset, unload IMS PAUTHDB via DL/I utility |
| 3 | LOADPADB.JCL | DFSRRC00 (IMS BMP) | Load IMS PAUTHDB from sequential files via PAUDBLOD |
| 4 | UNLDGSAM.JCL | DFSRRC00 (IMS BMP) | Unload IMS PAUTHDB to GSAM files via DBUNLDGS |
| 5 | UNLDPADB.JCL | IEFBR14, DFSRRC00 (IMS BMP) | Unload IMS PAUTHDB to sequential flat files via PAUDBUNL |

---

## 7. JCL Jobs — `app/app-transaction-type-db2/jcl/` (Transaction Type DB2 Module)

| # | JCL Member | Steps (EXEC PGM=) | Purpose |
|---|------------|-------------------|---------|
| 1 | CREADB21.jcl | IKJEFT01 ×4, IEFBR14 | Free DB2 plan, create DB2 tables (TRNTYPE, TRNTYCAT), load initial data |
| 2 | MNTTRDB2.jcl | IKJEFT01 | Execute COBTUPDT via TSO/DB2 batch — maintain transaction type DB2 table |
| 3 | TRANEXTR.jcl | IEBGENER ×2, IEFBR14, IKJEFT01 ×2 | Backup TRANTYPE/TRANCATG PS files to GDG, extract from DB2 tables to PS |

---

## 8. Batch Pipeline Schedule (Control-M — `app/scheduler/CardDemo.ca7`)

The daily batch cycle follows a Close-Process-Open pattern orchestrated by CA-7/Control-M:

```
CLOSEFIL → CBPAUP0J → POSTTRAN → WAITSTEP → OPENFIL
                                      │
                                      ├─→ CLOSEFIL1 → TRANCATG → WAITSTEP → CLOSEFIL → READACCT → READCARD → ...
                                      └─→ CLOSEFIL2 → TCATBALF  → WAITSTEP → CLOSEFIL → READACCT → ...
```

**Trigger chain:** `CLOSEFIL` → `CBPAUP0J` → `POSTTRAN` → `WAITSTEP` → `OPENFIL` (end of primary chain). Secondary chains branch from `WAITSTEP` to reload reference data (TRANTYPE, TRANCATG, TCATBALF) and run data verification reads.

---

## 9. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| — Batch programs | 16 |
| — Online (CICS) programs | 16 |
| — Online (CICS/IMS) programs | 5 |
| — Online (CICS/MQ) programs | 2 |
| — Online (CICS/DB2) programs | 3 |
| — Batch (IMS) programs | 4 |
| — Batch (DB2) programs | 1 |
| — Batch utilities | 2 |
| — Batch subroutines | 1 |
| Total copybooks | 61 |
| Total JCL jobs | 46 |
| Total BMS maps | 19 |
| Total lines of COBOL | ~30,263 |
