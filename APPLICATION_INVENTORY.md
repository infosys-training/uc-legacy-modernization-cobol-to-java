# Application Inventory

Complete catalog of all programs, copybooks, and JCL in the CardDemo COBOL estate.

---

## Online (CICS) Programs — `app/cbl/`

| File | Lines | Trans ID | Purpose | Key I/O (VSAM / TDQ / TS) | Copybooks |
|------|------:|----------|---------|---------------------------|-----------|
| COSGN00C.cbl | 260 | CC00 | Sign-on screen; authenticates user against USRSEC, XCTLs to COADM01C (admin) or COMEN01C (regular) | Reads USRSEC | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COMEN01C.cbl | 308 | CM00 | Regular-user main menu; XCTLs to feature programs based on selection | — | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COADM01C.cbl | 288 | CA00 | Admin menu; XCTLs to admin feature programs (user CRUD, DB2 tran-type mgmt) | — | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COACTUPC.cbl | 4236 | CAUP | Account update; reads/writes ACCTDAT, CUSTDAT, CARDDAT, CARDAIX, CXACAIX. Deep validation with EVALUATE/IF nesting | R/W ACCTDAT, CUSTDAT, CARDDAT; R CARDAIX, CXACAIX | CSUTLDWY, CVCRD01Y, CSLKPCDY, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY |
| COACTVWC.cbl | 941 | CAVW | Account view; reads ACCTDAT, CUSTDAT, CXACAIX for display | R ACCTDAT, CUSTDAT, CXACAIX | CVCRD01Y, COCOM01Y, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| COTRN00C.cbl | 699 | CT00 | List transactions from TRANSACT with forward/backward pagination | R TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| COTRN01C.cbl | 330 | CT01 | View single transaction detail from TRANSACT | R TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| COTRN02C.cbl | 783 | CT02 | Add new transaction; reads CXACAIX/CCXREF for card validation, writes TRANSACT, CALLs CSUTLDTC for date validation | R CXACAIX, CCXREF; W TRANSACT | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y |
| COCRDLIC.cbl | 1459 | CCLI | List credit cards with pagination; reads CARDDAT/CARDAIX, XCTLs to COCRDSLC/COCRDUPC | R CARDDAT, CARDAIX | CVCRD01Y, COCOM01Y, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| COCRDSLC.cbl | 887 | CCDL | Credit card detail view; reads CARDDAT/CARDAIX | R CARDDAT, CARDAIX | CVCRD01Y, COCOM01Y, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| COCRDUPC.cbl | 1560 | CCUP | Credit card update; reads/rewrites CARDDAT with lock/rewrite pattern | R/W CARDDAT | CVCRD01Y, COCOM01Y, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| COBIL00C.cbl | 572 | CB00 | Bill payment; reads/writes ACCTDAT, TRANSACT; reads CXACAIX for card-account lookup | R/W ACCTDAT, TRANSACT; R CXACAIX | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y |
| CORPT00C.cbl | 649 | CR00 | Transaction report submission via TDQ(JOBS); CALLs CSUTLDTC for date validation; builds JCL dynamically | W TDQ(JOBS) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| COUSR00C.cbl | 695 | CU00 | List users from USRSEC with pagination; XCTLs to COUSR02C (update) / COUSR03C (delete) | R USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COUSR01C.cbl | 299 | CU01 | Add user; writes new record to USRSEC | W USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COUSR02C.cbl | 414 | CU02 | Update user; reads/rewrites USRSEC record | R/W USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COUSR03C.cbl | 359 | CU03 | Delete user; reads/deletes from USRSEC | R/D USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |

### Online Utility Programs — `app/cbl/`

| File | Lines | Purpose | Interface |
|------|------:|---------|-----------|
| CSUTLDTC.cbl | 157 | Date validation utility; called via CALL by COTRN02C and CORPT00C | CALL 'CEEDAYS' — uses LE date services |

---

## Batch Programs — `app/cbl/`

| File | Lines | Purpose | Input Files | Output Files | Copybooks |
|------|------:|---------|-------------|--------------|-----------|
| CBACT01C.cbl | 430 | Read account file, write to sequential output / array / VBR files; CALLs COBDATFT (assembler) for date formatting | ACCTFILE (indexed) | OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| CBACT02C.cbl | 178 | Read and print card data file | CARDFILE (indexed) | SYSOUT | CVACT02Y |
| CBACT03C.cbl | 178 | Read and print account cross-reference file | XREFFILE (indexed) | SYSOUT | CVACT03Y |
| CBACT04C.cbl | 652 | Interest calculator; reads tran-category balances + disclosure groups, updates accounts, writes interest transactions | TCATBALF, XREFFILE, DISCGRP, ACCTFILE (I-O) | TRANSACT, ACCTFILE (I-O) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| CBCUS01C.cbl | 178 | Read and print customer data file | CUSTFILE (indexed) | SYSOUT | CVCUS01Y |
| CBTRN01C.cbl | 494 | Post records from daily transaction file to master files | DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| CBTRN02C.cbl | 731 | Transaction posting with validation; validates daily transactions, updates TRANSACT/ACCTFILE/TCATBALF, rejects to DALYREJS | DALYTRAN, XREFFILE | TRANFILE (I-O), ACCTFILE (I-O), TCATBALF (I-O), DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| CBTRN03C.cbl | 649 | Print transaction detail report with break totals by account | TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM | TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| CBSTM03A.CBL | 924 | Account statement generation (plain text + HTML); uses PSA/TCB/TIOT addressing, ALTER/GO TO, 2D arrays; CALLs CBSTM03B | via CBSTM03B | STMTFILE, HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| CBSTM03B.CBL | 230 | File I/O subroutine for CBSTM03A; opens/reads/closes data files | TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | — | — |
| COBSWAIT.cbl | 41 | Utility wait program; CALLs MVSWAIT | — | — | — |
| CBEXPORT.cbl | 582 | Export customer data for branch migration; reads all entity files, writes multi-record-type sequential export file | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE | EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBIMPORT.cbl | 487 | Import customer data from export file; reads EXPFILE, writes normalized target files with error handling | EXPFILE | CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |

---

## Sub-Application: Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cbl/`

| File | Lines | Type | Purpose | Key APIs | Copybooks |
|------|------:|------|---------|----------|-----------|
| CBPAUP0C.cbl | 386 | Batch/IMS | Purge aged pending authorizations from IMS DB; uses EXEC DLI GN/GNP/DLET with checkpoint | EXEC DLI | CIPAUSMY, CIPAUDTY |
| COPAUA0C.cbl | 1026 | Online/CICS+IMS+MQ | Authorization processor; receives MQ request, reads VSAM (ACCTDAT/CUSTDAT/CARDAIX), queries IMS DB, sends MQ reply | EXEC CICS, EXEC DLI, CALL MQOPEN/MQGET/MQPUT1 | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| COPAUS0C.cbl | 1032 | Online/CICS+IMS | Pending authorization summary screen; displays auth summaries from IMS DB with pagination | EXEC CICS, EXEC DLI GNP/GU/SCHD/TERM | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY |
| COPAUS1C.cbl | 604 | Online/CICS+IMS | Pending authorization detail edit/update screen | EXEC CICS LINK, EXEC DLI GU/GNP/REPL | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY |
| COPAUS2C.cbl | 244 | Online/CICS+DB2 | Fraud reporting; inserts/updates fraud flags via EXEC SQL | EXEC SQL, EXEC CICS | CIPAUDTY |
| DBUNLDGS.CBL | 366 | Batch/IMS | Unload IMS segments to sequential files; uses CBLTDLI GN/GNP/ISRT | CALL CBLTDLI | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| PAUDBLOD.CBL | 369 | Batch/IMS | Load IMS database from sequential input files | CALL CBLTDLI GU/ISRT | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| PAUDBUNL.CBL | 317 | Batch/IMS | Unload IMS database segments to sequential output files | CALL CBLTDLI GN/GNP | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

---

## Sub-Application: Transaction Type (DB2) — `app/app-transaction-type-db2/cbl/`

| File | Lines | Type | Purpose | Key APIs | Copybooks |
|------|------:|------|---------|----------|-----------|
| COBTUPDT.cbl | 237 | Batch/DB2 | Batch update of transaction type records via EXEC SQL | EXEC SQL | — (uses DCLTRTYP SQL INCLUDE) |
| COTRTLIC.cbl | 2098 | Online/CICS+DB2 | Transaction type list with DB2 cursor-based pagination; EXEC SQL SELECT/UPDATE/INSERT/DELETE with SYNCPOINT | EXEC CICS, EXEC SQL | CVCRD01Y, COCOM01Y, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY |
| COTRTUPC.cbl | 1702 | Online/CICS+DB2 | Transaction type update/maintenance screen; uses DB2 for reads/writes with SYNCPOINT | EXEC CICS, EXEC SQL | CSUTLDWY, CVCRD01Y, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |

---

## Sub-Application: VSAM/MQ — `app/app-vsam-mq/cbl/`

| File | Lines | Type | Purpose | Key APIs | Copybooks |
|------|------:|------|---------|----------|-----------|
| COACCT01.cbl | 620 | Online/CICS+MQ | Account inquiry via MQ; receives request from MQ queue, reads ACCTDAT, sends response via MQ | EXEC CICS RETRIEVE/READ/RETURN, CALL MQOPEN/MQGET/MQPUT/MQCLOSE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| CODATE01.cbl | 524 | Online/CICS+MQ | Date validation service via MQ; receives request, validates date, returns response via MQ | EXEC CICS RETRIEVE/ASKTIME/FORMATTIME/RETURN, CALL MQOPEN/MQGET/MQPUT/MQCLOSE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## JCL Catalog — `app/jcl/`

| JCL File | Lines | Purpose | Step Sequence |
|----------|------:|---------|---------------|
| ACCTFILE.jcl | 65 | Define and load account VSAM KSDS cluster | STEP05: IDCAMS (delete), STEP10: IDCAMS (define), STEP15: IDCAMS (repro from seq to VSAM) |
| CARDFILE.jcl | 128 | Define and load card VSAM KSDS + AIX clusters | CLCIFIL: SDSF (close files), STEP05: IDCAMS (delete), STEP10: IDCAMS (define KSDS), STEP15: IDCAMS (repro), STEP40: IDCAMS (define AIX), STEP50: IDCAMS (build AIX path) |
| CBADMCDJ.jcl | 167 | CICS CSD batch update; defines programs/transactions/groups | STEP1: DFHCSDUP (CSD update utility) |
| CBEXPORT.jcl | 72 | Export all customer data to sequential file for branch migration | STEP01: IDCAMS (allocate export file), STEP02: PGM=CBEXPORT (run export program) |
| CBIMPORT.jcl | 68 | Import customer data from export file into normalized targets | STEP01: PGM=CBIMPORT (run import program) |
| CLOSEFIL.jcl | 34 | Close CICS-managed VSAM files | CLCIFIL: SDSF (issue CEMT CLOSE commands) |
| COMBTRAN.jcl | 52 | Combine and sort transaction files, load into VSAM | STEP05R: SORT (merge+sort), STEP10: IDCAMS (repro sorted data to VSAM) |
| CREASTMT.JCL | 97 | Create account statements (text + HTML) | DELDEF01: IDCAMS, STEP010: SORT, STEP020: IDCAMS (repro to VSAM), STEP030: IEFBR14 (pre-delete), STEP040: PGM=CBSTM03A |
| CUSTFILE.jcl | 84 | Define and load customer VSAM KSDS cluster | CLCIFIL: SDSF, STEP05: IDCAMS (delete), STEP10: IDCAMS (define), STEP15: IDCAMS (repro), OPCIFIL: SDSF (open files) |
| DALYREJS.jcl | 32 | Define daily rejects VSAM cluster | STEP05: IDCAMS (define ESDS for rejected transactions) |
| DEFCUST.jcl | 47 | Define customer-related VSAM clusters (alternative) | STEP05: IDCAMS (define two clusters) |
| DEFGDGB.jcl | 63 | Define GDG base entries | STEP05: IDCAMS (define GDG bases for backup) |
| DEFGDGD.jcl | 94 | Define GDG entries and backup reference data | STEP10-STEP60: IDCAMS/IEBGENER pairs (backup TRANTYPE, TRANCATG, etc.) |
| DISCGRP.jcl | 65 | Define and load disclosure group VSAM KSDS | STEP05: IDCAMS (delete), STEP10: IDCAMS (define), STEP15: IDCAMS (repro) |
| DUSRSECJ.jcl | 92 | Define and load user security VSAM KSDS | PREDEL: IEFBR14, STEP01: IEBGENER (create seq), STEP02: IDCAMS (define KSDS), STEP03: IDCAMS (repro to VSAM) |
| ESDSRRDS.jcl | 124 | Define and load ESDS/RRDS demonstration clusters | PREDEL: IEFBR14, STEP01: IEBGENER, STEP02-05: IDCAMS (define ESDS, repro, define RRDS, repro) |
| FTPJCL.JCL | 42 | FTP file transfer utility | STEP1: PGM=FTP |
| INTCALC.jcl | 44 | Run interest calculation batch | STEP15: PGM=CBACT04C (interest calculator with date parm) |
| INTRDRJ1.JCL | 19 | Internal reader job submission chain — step 1 | IDCAMS: repro backup, STEP01: IEBGENER (submit INTRDRJ2 via INTRDR) |
| INTRDRJ2.JCL | 14 | Internal reader job submission chain — step 2 | IDCAMS: repro from backup to target |
| OPENFIL.jcl | 34 | Open CICS-managed VSAM files | OPCIFIL: SDSF (issue CEMT OPEN commands) |
| POSTTRAN.jcl | 45 | Post daily transactions with validation | STEP15: PGM=CBTRN02C (validate/post transactions) |
| PRTCATBL.jcl | 66 | Print transaction category balances | DELDEF: IEFBR14, STEP05R: REPROC (repro VSAM to seq), STEP10R: SORT (sort/format output) |
| READACCT.jcl | 50 | Read and dump account file to sequential formats | PREDEL: IEFBR14, STEP05: PGM=CBACT01C |
| READCARD.jcl | 31 | Read and print card data file | STEP05: PGM=CBACT02C |
| READCUST.jcl | 30 | Read and print customer data file | STEP05: PGM=CBCUS01C |
| READXREF.jcl | 31 | Read and print cross-reference file | STEP05: PGM=CBACT03C |
| REPTFILE.jcl | 32 | Define report output VSAM cluster | STEP05: IDCAMS (define report file) |
| TCATBALF.jcl | 65 | Define and load transaction category balance VSAM KSDS | STEP05: IDCAMS (delete), STEP10: IDCAMS (define), STEP15: IDCAMS (repro) |
| TRANBKP.jcl | 71 | Backup transaction file | STEP05R: REPROC (repro to seq), STEP05: IDCAMS (delete cluster), STEP10: IDCAMS (redefine cluster) |
| TRANCATG.jcl | 65 | Define and load transaction category VSAM KSDS | STEP05: IDCAMS (delete), STEP10: IDCAMS (define), STEP15: IDCAMS (repro) |
| TRANFILE.jcl | 125 | Define and load transaction VSAM KSDS cluster | CLCIFIL: SDSF, STEP05: IDCAMS (delete), STEP10: IDCAMS (define KSDS), STEP15: IDCAMS (repro), STEP20: IDCAMS (define AIX), STEP25: IDCAMS (build path) |
| TRANIDX.jcl | 58 | Define transaction alternate indexes | STEP20-STEP30: IDCAMS (define AIX, build index, define path) |
| TRANREPT.jcl | 84 | Generate transaction detail report | STEP05R: REPROC (repro to seq), STEP05R: SORT (sort), STEP10R: PGM=CBTRN03C (report writer) |
| TRANTYPE.jcl | 65 | Define and load transaction type VSAM KSDS | STEP05: IDCAMS (delete), STEP10: IDCAMS (define), STEP15: IDCAMS (repro) |
| TXT2PDF1.JCL | 41 | Convert text statement to PDF | TXT2PDF: PGM=IKJEFT1B (TSO batch, runs TXT2PDF REXX) |
| WAITSTEP.jcl | 27 | Wait step utility for job sequencing | WAIT: PGM=COBSWAIT |
| XREFFILE.jcl | 106 | Define and load card cross-reference VSAM KSDS + AIX | STEP05: IDCAMS (delete), STEP10: IDCAMS (define KSDS), STEP15: IDCAMS (repro), STEP20-STEP30: IDCAMS (define AIX, build, path) |

---

## Summary Statistics

| Category | Count | Total Lines |
|----------|------:|------------:|
| Online CICS programs (app/cbl/) | 17 | 12,939 |
| Batch programs (app/cbl/) | 13 | 6,156 |
| Utility programs (app/cbl/) | 1 (CSUTLDTC) | 157 |
| Sub-app: Authorization (IMS/DB2/MQ) | 8 | 3,994 |
| Sub-app: Transaction Type (DB2) | 3 | 4,037 |
| Sub-app: VSAM/MQ | 2 | 1,144 |
| Copybooks (app/cpy/) | 31 | ~2,900 |
| JCL jobs (app/jcl/) | 38 | ~2,500 |
| **Grand Total** | **113 files** | **~31,800** |
