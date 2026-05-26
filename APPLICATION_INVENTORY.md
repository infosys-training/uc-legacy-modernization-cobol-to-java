# CardDemo Application Inventory

Complete inventory of the CardDemo credit card management application — an AWS-provided mainframe modernization demo consisting of batch programs, online CICS transactions, sub-application extensions, and JCL jobs.

---

## A. Batch Programs (`app/cbl/`)

| Filename | Program ID | Purpose | Classification | Key I/O (Access Mode) | Copybooks Referenced |
|:---------|:-----------|:--------|:---------------|:----------------------|:---------------------|
| CBACT01C.cbl | CBACT01C | Read account VSAM KSDS, write flat files (sequential, array, variable-length) | Batch | R: ACCTFILE (Indexed/Seq); W: OUTFILE (Seq), ARRYFILE (Seq), VBRCFILE (Seq/VB) | CVACT01Y, CODATECN |
| CBACT02C.cbl | CBACT02C | Read and print card data file | Batch | R: CARDFILE (Indexed/Seq) | CVACT02Y |
| CBACT03C.cbl | CBACT03C | Read and print account cross-reference data file | Batch | R: XREFFILE (Indexed/Seq) | CVACT03Y |
| CBACT04C.cbl | CBACT04C | Interest calculator — compute interest on category balances and post interest transactions | Batch | R: TCATBALF (Indexed/Seq), XREFFILE (Indexed/Random), DISCGRP (Indexed/Random); I-O: ACCTFILE (Indexed/Random); W: TRANSACT (Seq) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| CBCUS01C.cbl | CBCUS01C | Read and print customer data file | Batch | R: CUSTFILE (Indexed/Seq) | CVCUS01Y |
| CBEXPORT.cbl | CBEXPORT | Export all CardDemo data for branch migration — reads normalized files and creates multi-record export file | Batch | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE (all Indexed/Seq); W: EXPFILE (Indexed/Seq) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBIMPORT.cbl | CBIMPORT | Import branch migration data from export file — validates and writes to individual output files | Batch | R: EXPFILE (Indexed/Seq); W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT (all Seq) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBSTM03A.CBL | CBSTM03A | Statement generator — produces text and HTML account statements from transaction data | Batch | R: TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE (via CBSTM03B); W: STMTFILE (Seq), HTMLFILE (Seq) | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| CBSTM03B.CBL | CBSTM03B | File I/O subroutine for CBSTM03A — handles open/close/read operations on input VSAM files | Batch (Subroutine) | R: TRNXFILE (Indexed/Seq), XREFFILE (Indexed/Random), CUSTFILE (Indexed/Random), ACCTFILE (Indexed/Random) | *(none — uses LINKAGE SECTION)* |
| CBTRN01C.cbl | CBTRN01C | Post daily transactions — read daily feed and write to master transaction file | Batch | R: DALYTRAN (Seq), CUSTFILE (Indexed/Random), XREFFILE (Indexed/Random), CARDFILE (Indexed/Random), ACCTFILE (Indexed/Random); W: TRANFILE (Indexed/Seq) | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| CBTRN02C.cbl | CBTRN02C | Post daily transactions with validation — validates card/account, updates balances, rejects invalid | Batch | R: DALYTRAN (Seq), XREFFILE (Indexed/Random); I-O: ACCTFILE (Indexed/Random), TCATBALF (Indexed/Random); W: TRANFILE (Indexed/Seq), DALYREJS (Seq) | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| CBTRN03C.cbl | CBTRN03C | Transaction detail report with date-range filtering — generates formatted report with totals | Batch | R: TRANFILE (Indexed/Seq), CARDXREF (Indexed/Random), TRANTYPE (Indexed/Random), TRANCATG (Indexed/Random), DATEPARM (Seq); W: TRANREPT (Seq) | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| COBSWAIT.cbl | COBSWAIT | Wait utility — pauses execution for a specified number of centiseconds (from PARM) | Batch (Utility) | *(none)* | *(none)* |

---

## B. Online (CICS) Programs (`app/cbl/`)

| Filename | Program ID | Trans ID | Purpose | Key VSAM Files | Copybooks |
|:---------|:-----------|:---------|:--------|:---------------|:----------|
| COSGN00C.cbl | COSGN00C | CC00 | Signon/authentication — validates user credentials from USRSEC file, routes to admin or regular menu | USRSEC | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COADM01C.cbl | COADM01C | CA00 | Admin menu — displays admin-only menu options (user management, DB2 transaction types) | *(none — navigation only)* | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COMEN01C.cbl | COMEN01C | CM00 | Regular user menu — displays main menu options (account, card, transaction, report, bill) | *(none — navigation only)* | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COACTVWC.cbl | COACTVWC | CAVW | Account view — displays account details, card info, and customer data in read-only mode | ACCTFILE, CARDFILE, CUSTFILE, XREFFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| COACTUPC.cbl | COACTUPC | CAUP | Account update — most complex program (~4236 lines); accepts and validates account/customer field changes with extensive validation rules | ACCTFILE, CARDFILE, CUSTFILE, XREFFILE | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY, CSSTRPFY, CSUTLDPY |
| COBIL00C.cbl | COBIL00C | CB00 | Bill payment — pay account balance in full; creates payment transaction and updates account balance | ACCTFILE, TRANSACT, XREFFILE | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COCRDLIC.cbl | COCRDLIC | CCLI | Credit card list with pagination — browse and select cards for view or update | CARDFILE, ACCTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| COCRDSLC.cbl | COCRDSLC | CCDL | Card detail view — read-only display of card and associated customer information | CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| COCRDUPC.cbl | COCRDUPC | CCUP | Card update — lock, validate, and rewrite card record with complex update/confirmation flow | CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| COTRN00C.cbl | COTRN00C | CT00 | Transaction list — browse transactions with selection for view | TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COTRN01C.cbl | COTRN01C | CT01 | Transaction view — display full transaction details in read-only mode | TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COTRN02C.cbl | COTRN02C | CT02 | Add transaction — validate and write new transaction record with date validation | TRANSACT, ACCTFILE, XREFFILE | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| CORPT00C.cbl | CORPT00C | CR00 | Report submission — submits batch transaction report jobs via internal reader with date-range parameters | *(uses WRITEQ TD for internal reader)* | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COUSR00C.cbl | COUSR00C | CU00 | User list — browse users with selection for update or delete | USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR01C.cbl | COUSR01C | CU01 | Add user — create new Regular or Admin user in USRSEC file | USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR02C.cbl | COUSR02C | CU02 | Update user — modify user profile (name, password, type) in USRSEC file | USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR03C.cbl | COUSR03C | CU03 | Delete user — remove user from USRSEC file with confirmation | USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| CSUTLDTC.cbl | CSUTLDTC | *(called)* | Date validation utility — validates CCYYMMDD dates using LE callable services (CEEDAYS) | *(none)* | *(none — uses LINKAGE SECTION)* |

---

## C. Sub-Application Programs

### C.1 Credit Card Authorizations (`app/app-authorization-ims-db2-mq/`)

IMS DB, DB2, and MQ integration extension for real-time credit card authorization processing.

| Filename | Program ID | Type | Purpose |
|:---------|:-----------|:-----|:--------|
| COPAUA0C.cbl | COPAUA0C | CICS (Trans: CP00) | Authorization request processor — processes MQ-triggered authorization requests |
| COPAUS0C.cbl | COPAUS0C | CICS (Trans: CPVS) | Authorization summary display — shows pending authorizations with account details |
| COPAUS1C.cbl | COPAUS1C | CICS (Trans: CPVD) | Authorization detail display — detailed view of a specific authorization |
| COPAUS2C.cbl | COPAUS2C | CICS (Called) | Fraud marking and DB2 update — marks authorization as fraudulent and stores in DB2 |
| CBPAUP0C.cbl | CBPAUP0C | Batch | Expired authorization purge — removes expired authorizations and adjusts credit |
| DBUNLDGS.CBL | DBUNLDGS | Batch | IMS database unload utility |
| PAUDBLOD.CBL | PAUDBLOD | Batch | IMS authorization database load |
| PAUDBUNL.CBL | PAUDBUNL | Batch | IMS authorization database unload |

**Copybooks:** CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, IMSFUNCS, PADFLPCB, PASFLPCB, PAUTBPCB

**DB2 Declaration:** AUTHFRDS (fraud tracking table with card/merchant/authorization fields)

### C.2 Transaction Type Management (`app/app-transaction-type-db2/`)

DB2 integration extension for managing transaction type reference data using embedded static SQL.

| Filename | Program ID | Type | Purpose |
|:---------|:-----------|:-----|:--------|
| COTRTLIC.cbl | COTRTLIC | CICS (Trans: CTLI) | Transaction type list — browse, update, and delete transaction types with DB2 cursors |
| COTRTUPC.cbl | COTRTUPC | CICS (Trans: CTTU) | Transaction type add/edit — add new or edit existing transaction types in DB2 |
| COBTUPDT.cbl | COBTUPDT | Batch | Batch maintenance program for transaction type updates in DB2 |

**Copybooks:** CSDB2RPY (DB2 read parameters), CSDB2RWY (DB2 read/write parameters)

**DB2 Declarations:** DCLTRTYP (TRANSACTION_TYPE table), DCLTRCAT (TRANSACTION_TYPE_CATEGORY table)

### C.3 Account Extractions via MQ (`app/app-vsam-mq/`)

MQ integration extension for asynchronous account data extraction.

| Filename | Program ID | Type | Purpose |
|:---------|:-----------|:-----|:--------|
| COACCT01.cbl | COACCT01 | CICS (Trans: CDRA) | Account details inquiry via MQ — retrieves account information through MQ request/response |
| CODATE01.cbl | CODATE01 | CICS (Trans: CDRD) | System date inquiry via MQ — queries system date through MQ request/response pattern |

---

## D. JCL Jobs (`app/jcl/`)

### D.1 Dataset Definition and Loading Jobs

| JCL File | Purpose | Step Sequence |
|:---------|:--------|:--------------|
| ACCTFILE.jcl | Delete/define/load account VSAM KSDS | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER (KEYS 11 0, RECSIZE 300); STEP15: IDCAMS REPRO from ACCTDATA.PS |
| CARDFILE.jcl | Delete/define/load card VSAM KSDS with AIX | CLCIFIL: SDSF CLOSE; STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO from CARDDATA.PS; STEP40-60: IDCAMS define AIX, build path; OPCIFIL: SDSF OPEN |
| CUSTFILE.jcl | Delete/define/load customer VSAM KSDS | CLCIFIL: SDSF CLOSE; STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO from CUSTDATA.PS; OPCIFIL: SDSF OPEN |
| XREFFILE.jcl | Delete/define/load cross-reference VSAM KSDS with AIX | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO from XREFDATA.PS; STEP20-30: IDCAMS define AIX, build path |
| TRANFILE.jcl | Delete/define/load transaction VSAM KSDS with AIX | CLCIFIL: SDSF CLOSE; STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO from TRANSACT.PS; STEP20-30: IDCAMS AIX/PATH; OPCIFIL: SDSF OPEN |
| TCATBALF.jcl | Delete/define/load transaction category balance VSAM KSDS | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO from TCATBAL.PS |
| DISCGRP.jcl | Delete/define/load discount group VSAM KSDS | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO from DISCGRP.PS |
| TRANTYPE.jcl | Delete/define/load transaction type VSAM KSDS | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO from TRANTYPE.PS |
| TRANCATG.jcl | Delete/define/load transaction category VSAM KSDS | STEP05: IDCAMS DELETE; STEP10: IDCAMS DEFINE CLUSTER; STEP15: IDCAMS REPRO from TRANCATG.PS |
| DALYREJS.jcl | Delete/define daily rejects sequential file | STEP05: IDCAMS DELETE/DEFINE |
| REPTFILE.jcl | Delete/define report sequential file | STEP05: IDCAMS DELETE/DEFINE |
| DUSRSECJ.jcl | Delete/define/load user security VSAM KSDS | PREDEL: IEFBR14 delete PS; STEP01: IEBGENER create PS from inline; STEP02: IDCAMS DELETE/DEFINE KSDS; STEP03: IDCAMS REPRO from USRSEC.PS |
| ESDSRRDS.jcl | Create ESDS and RRDS user security files | PREDEL: IEFBR14 delete PS; STEP01: IEBGENER create PS; STEP02-04: IDCAMS define ESDS/RRDS, REPRO data |
| DEFCUST.jcl | Define customer VSAM cluster (alternate layout) | STEP05: IDCAMS DELETE; STEP05: IDCAMS DEFINE CLUSTER |
| TRANIDX.jcl | Define transaction AIX and path | STEP20: IDCAMS DEFINE AIX; STEP25: IDCAMS DEFINE PATH; STEP30: IDCAMS BLDINDEX |

### D.2 Batch Processing Jobs

| JCL File | Purpose | Step Sequence |
|:---------|:--------|:--------------|
| READACCT.jcl | Read and extract account data | PREDEL: IEFBR14 delete output; STEP05: PGM=CBACT01C; DD: ACCTFILE(in), OUTFILE/ARRYFILE/VBRCFILE(out) |
| READCARD.jcl | Read and print card data | STEP05: PGM=CBACT02C; DD: CARDFILE(in) |
| READCUST.jcl | Read and print customer data | STEP05: PGM=CBCUS01C; DD: CUSTFILE(in) |
| READXREF.jcl | Read and print cross-reference data | STEP05: PGM=CBACT03C; DD: XREFFILE(in) |
| POSTTRAN.jcl | Post daily transactions | STEP15: PGM=CBTRN02C; DD: DALYTRAN(in), XREFFILE(in), ACCTFILE(I-O), TCATBALF(I-O), TRANFILE(out), DALYREJS(out) |
| INTCALC.jcl | Calculate interest on balances | STEP15: PGM=CBACT04C PARM='2022071800'; DD: TCATBALF(in), XREFFILE(in), ACCTFILE(I-O), DISCGRP(in), TRANSACT(out) |
| TRANREPT.jcl | Generate transaction detail report | STEP01R: PROC=REPROC (unload TRANSACT to seq); STEP05R: PGM=SORT; STEP10R: PGM=CBTRN03C; DD: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM(in), TRANREPT(out) |
| CREASTMT.JCL | Generate account statements (text + HTML) | DELDEF01: IDCAMS delete temp; STEP010: SORT transactions; STEP020: IDCAMS REPRO to VSAM; STEP030: IEFBR14 delete old output; STEP040: PGM=CBSTM03A; DD: TRNXFILE, XREFFILE, ACCTFILE, CUSTFILE(in), STMTFILE, HTMLFILE(out) |
| CBEXPORT.jcl | Export CardDemo data for branch migration | STEP01: IDCAMS delete old export; STEP02: PGM=CBEXPORT; DD: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE(in), EXPFILE(out) |
| CBIMPORT.jcl | Import branch migration data | STEP01: PGM=CBIMPORT; DD: EXPFILE(in), CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT(out) |
| COMBTRAN.jcl | Combine transaction files | STEP05R: PGM=SORT merge; STEP10: IDCAMS REPRO merged to TRANVSAM |
| TRANBKP.jcl | Backup transaction VSAM to GDG | PROC=REPROC unload to seq; STEP05: IDCAMS DELETE old; STEP10: IDCAMS REPRO backup |
| PRTCATBL.jcl | Print category balance file | DELDEF: IEFBR14; PROC=REPROC unload; STEP10R: PGM=SORT |
| WAITSTEP.jcl | Wait utility job | WAIT: PGM=COBSWAIT |
| DEFGDGB.jcl | Define GDG base for backups | STEP05: IDCAMS DEFINE GDG |
| DEFGDGD.jcl | Backup reference data to GDG generations | STEP10: IDCAMS DEFINE GDG; STEP20: IEBGENER copy TRANTYPE; STEP30: IDCAMS DEFINE GDG; STEP40: IEBGENER copy TRANCATG; STEP50-60: IDCAMS/IEBGENER copy DISCGRP |

### D.3 Utility and Infrastructure Jobs

| JCL File | Purpose | Step Sequence |
|:---------|:--------|:--------------|
| CLOSEFIL.jcl | Close CICS files before batch processing | CLCIFIL: PGM=SDSF (SET DSCLOSE) |
| OPENFIL.jcl | Open CICS files after batch processing | OPCIFIL: PGM=SDSF (SET DSOPEN) |
| CBADMCDJ.jcl | CICS CSD administration — load resource definitions | STEP1: PGM=DFHCSDUP |
| FTPJCL.JCL | FTP file transfer utility | STEP1: PGM=FTP |
| INTRDRJ1.JCL | Internal reader job 1 — backup and chain | IDCAMS: REPRO backup; STEP01: IEBGENER submit INTRDRJ2 |
| INTRDRJ2.JCL | Internal reader job 2 — secondary backup | IDCAMS: REPRO secondary backup |
| TXT2PDF1.JCL | Convert text statement to PDF | TXT2PDF: PGM=IKJEFT1B running TXT2PDF REXX exec |

### D.4 JCL Procedures (`app/proc/`)

| Procedure | Purpose | Steps |
|:----------|:--------|:------|
| REPROC.prc | REPRO utility — generic VSAM load/unload procedure using IDCAMS REPRO | PRC001: PGM=IDCAMS; DD: FILEIN, FILEOUT (overridden by caller) |
| TRANREPT.prc | Transaction report procedure — unload transactions, filter by date, sort, and generate report | STEP01R: PROC=REPROC (unload TRANSACT); STEP05R: PGM=SORT (filter/sort by card); STEP10R: PGM=CBTRN03C (generate report) |
