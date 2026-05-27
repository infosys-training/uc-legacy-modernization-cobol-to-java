# Application Inventory — CardDemo COBOL Estate

> Auto-generated analysis of the AWS CardDemo mainframe credit-card management system.

---

## 1. COBOL Programs — `app/cbl/`

### 1.1 Batch Programs

| # | Filename | Program ID | Purpose | Key I/O (files read / written) | Copybooks Referenced |
|---|----------|-----------|---------|-------------------------------|---------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read the account file and write into output files (fixed, array, variable-block formats) | **R:** ACCTFILE (VSAM KSDS) · **W:** OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and print card data file | **R:** CARDFILE (VSAM KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and print account cross-reference data file | **R:** XREFFILE (VSAM KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Interest calculator — compute interest charges per account based on disclosure group rates | **R:** TCATBALF, XREFFILE, DISCGRP, ACCTFILE (I-O) · **W:** TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and print customer data file | **R:** CUSTFILE (VSAM KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export customer/account/card/transaction/xref data for branch migration | **R:** CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · **W:** EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export file | **R:** EXPFILE · **W:** CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | CBSTM03A | Print account statements from transaction data (main driver) | **W:** STMTFILE, HTMLFILE · **Calls:** CBSTM03B | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | CBSTM03B | Subroutine — file processing for transaction/statement report | **R:** TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | *(none — inline definitions)* |
| 10 | CBTRN01C.cbl | CBTRN01C | Post records from daily transaction file (validation pass) | **R:** DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | CBTRN02C | Post records from daily transaction file (update balances, write rejects) | **R:** DALYTRAN, XREFFILE, ACCTFILE (I-O), TCATBALF (I-O) · **W:** TRANFILE, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | CBTRN03C | Print transaction detail report | **R:** TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM · **W:** TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | COBSWAIT | Utility — wait for a specified duration (parm in centiseconds) | *(none)* · **Calls:** MVSWAIT (assembler) | *(none)* |

### 1.2 Online (CICS) Programs

| # | Filename | Program ID | Purpose | CICS Operations | VSAM Datasets Accessed | Copybooks Referenced |
|---|----------|-----------|---------|-----------------|----------------------|---------------------|
| 1 | COSGN00C.cbl | COSGN00C | Sign-on screen — authenticate user credentials | RECEIVE, SEND, READ, ASSIGN, XCTL | USRSEC | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | COMEN01C.cbl | COMEN01C | Main menu for regular users — route to selected function | RECEIVE, SEND, INQUIRE, XCTL | *(none)* | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | COADM01C.cbl | COADM01C | Admin menu for admin users | RECEIVE, SEND, RETURN, XCTL | *(none)* | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COACTVWC.cbl | COACTVWC | View account details | RECEIVE, SEND, READ, XCTL, ABEND | CARDXREF, ACCTDATA, CUSTDATA | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 5 | COACTUPC.cbl | COACTUPC | Update account details (largest program — 4,237 lines) | RECEIVE, SEND, READ, REWRITE, XCTL, ABEND | CARDXREF, ACCTDATA, CUSTDATA | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY, CSSTRPFY, CSUTLDPY |
| 6 | COCRDLIC.cbl | COCRDLIC | List credit cards with browse/search | RECEIVE, SEND, STARTBR, READNEXT, READPREV, ENDBR, XCTL | CARDDATA | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 7 | COCRDSLC.cbl | COCRDSLC | View credit card detail | RECEIVE, SEND, READ, XCTL, ABEND | CARDDATA, CARDXREF, ACCTDATA, CUSTDATA | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 8 | COCRDUPC.cbl | COCRDUPC | Update credit card details | RECEIVE, SEND, READ, REWRITE, XCTL, ABEND | CARDDATA, CARDXREF, ACCTDATA, CUSTDATA | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 9 | COTRN00C.cbl | COTRN00C | List transactions with browse | RECEIVE, SEND, STARTBR, READNEXT, READPREV, ENDBR | TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | COTRN01C.cbl | COTRN01C | View a single transaction | RECEIVE, SEND, READ | TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | COTRN02C.cbl | COTRN02C | Add a new transaction | RECEIVE, SEND, READ, STARTBR, READPREV, ENDBR, WRITE | TRANSACT, CARDXREF (AIX), CARDXREF | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 12 | COBIL00C.cbl | COBIL00C | Bill payment — pay balance in full or partial amount | RECEIVE, SEND, READ, REWRITE, STARTBR, READPREV, ENDBR, WRITE | ACCTDATA, CARDXREF (AIX), TRANSACT | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | CORPT00C.cbl | CORPT00C | Print transaction reports (submits batch via WRITEQ TD) | RECEIVE, SEND, WRITEQ | *(none — invokes batch)* · **Calls:** CSUTLDTC | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | COUSR00C.cbl | COUSR00C | List all users from USRSEC file | RECEIVE, SEND, STARTBR, READNEXT, READPREV, ENDBR | USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | COUSR01C.cbl | COUSR01C | Add a new user (regular or admin) | RECEIVE, SEND, WRITE | USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COUSR02C.cbl | COUSR02C | Update an existing user | RECEIVE, SEND, READ, REWRITE | USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | COUSR03C.cbl | COUSR03C | Delete a user | RECEIVE, SEND, READ, DELETE | USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

### 1.3 Shared Utility Programs

| # | Filename | Program ID | Purpose | Classification |
|---|----------|-----------|---------|---------------|
| 1 | CSUTLDTC.cbl | CSUTLDTC | Date validation utility — calls CEEDAYS/CEEDATM LE services to validate and format dates | Subroutine (called by CORPT00C, COTRN02C) |

---

## 2. Sub-Application Programs

### 2.1 Authorization Sub-App (`app/app-authorization-ims-db2-mq/`)

Uses IMS DB, DB2, and MQ Series for pending card-authorization processing.

| # | Filename | Program ID | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----------|------|---------|---------|-----------|
| 1 | CBPAUP0C.cbl | CBPAUP0C | Batch (IMS) | Delete expired pending authorization messages from IMS DB | IMS DB segments via CBLTDLI | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 2 | COPAUA0C.cbl | COPAUA0C | Online (CICS/MQ) | Card authorization decision — receive request via MQ, approve/decline, write audit | MQ queues (MQOPEN, MQGET, MQPUT1), CICS VSAM READ | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 3 | COPAUS0C.cbl | COPAUS0C | Online (CICS/BMS) | Summary view of pending authorization messages | CICS SEND/RECEIVE MAP, READ | COCOM01Y |
| 4 | COPAUS1C.cbl | COPAUS1C | Online (CICS/BMS) | Detail view of a single authorization message | CICS SEND/RECEIVE, LINK | *(inline)* |
| 5 | COPAUS2C.cbl | COPAUS2C | Online (CICS/DB2) | Mark authorization message as fraud | CICS ASKTIME/FORMATTIME | DFHBMSCA, CIPAUDTY |
| 6 | DBUNLDGS.CBL | DBUNLDGS | Batch (IMS) | Unload IMS DB to GSAM sequential files | IMS CBLTDLI, WRITE OUTFIL1/OUTFIL2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 7 | PAUDBLOD.CBL | PAUDBLOD | Batch (IMS) | Load IMS DB from sequential input files | READ INFILE1/INFILE2, IMS CBLTDLI | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | PAUDBUNL.CBL | PAUDBUNL | Batch (IMS) | Unload IMS DB to sequential output files | IMS CBLTDLI, WRITE OUTFIL1/OUTFIL2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

### 2.2 Transaction Type Sub-App (`app/app-transaction-type-db2/`)

Uses DB2 for transaction-type and transaction-category reference data.

| # | Filename | Program ID | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----------|------|---------|---------|-----------|
| 1 | COBTUPDT.cbl | COBTUPDT | Batch (DB2) | Batch update of transaction types from input file into DB2 | READ INPFILE, EXEC SQL INSERT/UPDATE | *(inline SQL)* |
| 2 | COTRTLIC.cbl | COTRTLIC | Online (CICS/DB2) | List transaction types for update/delete | CICS SEND/RECEIVE, EXEC SQL SELECT | *(inline SQL — DCLTRTYP, DCLTRCAT)* |
| 3 | COTRTUPC.cbl | COTRTUPC | Online (CICS/DB2) | Accept and process transaction type update | CICS SEND/RECEIVE, EXEC SQL UPDATE/INSERT/DELETE | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |

### 2.3 VSAM-MQ Sub-App (`app/app-vsam-mq/`)

MQ-enabled CICS programs for account and date services.

| # | Filename | Program ID | Type | Purpose | Key I/O | Copybooks |
|---|----------|-----------|------|---------|---------|-----------|
| 1 | COACCT01.cbl | COACCT01 | Online (CICS/MQ) | Account inquiry via MQ — receive request, read VSAM, send response | MQ queues, CICS READ | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 2 | CODATE01.cbl | CODATE01 | Online (CICS/MQ) | Date/time service via MQ — respond with formatted timestamp | MQ queues, CICS ASKTIME/FORMATTIME | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. JCL Job Catalog — `app/jcl/`

### 3.1 Data Definition / VSAM Cluster Management

These jobs define, delete, and repopulate VSAM KSDS clusters and sequential datasets.

| # | Job Name | JCL File | Purpose | Key Steps |
|---|----------|----------|---------|-----------|
| 1 | ACCTFILE | ACCTFILE.jcl | Define account VSAM KSDS cluster | IDCAMS DELETE → DEFINE → REPRO |
| 2 | CARDFILE | CARDFILE.jcl | Define card VSAM KSDS + AIX clusters | SDSF CLOSE → IDCAMS DELETE/DEFINE/REPRO (KSDS + AIX + PATH) → SDSF OPEN |
| 3 | CUSTFILE | CUSTFILE.jcl | Define customer VSAM KSDS cluster | SDSF CLOSE → IDCAMS DELETE/DEFINE/REPRO → SDSF OPEN |
| 4 | XREFFILE | XREFFILE.jcl | Define card-xref VSAM KSDS + AIX clusters | IDCAMS DELETE/DEFINE/REPRO (KSDS + AIX + PATH × 2) |
| 5 | TRANFILE | TRANFILE.jcl | Define transaction VSAM KSDS + AIX clusters | SDSF CLOSE → IDCAMS DELETE/DEFINE/REPRO (KSDS + multiple AIX/PATH) → SDSF OPEN |
| 6 | TRANIDX | TRANIDX.jcl | Define transaction alternate indexes | IDCAMS DEFINE AIX + PATH × 3 |
| 7 | TRANTYPE | TRANTYPE.jcl | Define transaction-type VSAM KSDS | IDCAMS DELETE → DEFINE → REPRO |
| 8 | TRANCATG | TRANCATG.jcl | Define transaction-category VSAM KSDS | IDCAMS DELETE → DEFINE → REPRO |
| 9 | TCATBALF | TCATBALF.jcl | Define transaction-category balance VSAM KSDS | IDCAMS DELETE → DEFINE → REPRO |
| 10 | DISCGRP | DISCGRP.jcl | Define disclosure-group VSAM KSDS | IDCAMS DELETE → DEFINE → REPRO |
| 11 | DALYREJS | DALYREJS.jcl | Define daily rejects sequential file | IDCAMS DELETE/DEFINE |
| 12 | REPTFILE | REPTFILE.jcl | Define report output sequential file | IDCAMS DELETE/DEFINE |
| 13 | DEFCUST | DEFCUST.jcl | Alternate customer VSAM definitions | IDCAMS DELETE/DEFINE × 2 |
| 14 | ESDSRRDS | ESDSRRDS.jcl | Define ESDS and RRDS VSAM clusters (user security alternate formats) | IEBGENER copy → IDCAMS DEFINE ESDS → REPRO → DEFINE RRDS → REPRO |

### 3.2 Batch Processing Jobs

| # | Job Name | JCL File | Purpose | Key Steps | Datasets |
|---|----------|----------|---------|-----------|----------|
| 1 | READACCT | READACCT.jcl | Read account data into output formats | IEFBR14 (pre-delete) → **CBACT01C** | R: ACCTFILE · W: PSCOMP, ARRYPS, VBPS |
| 2 | READCARD | READCARD.jcl | Read and display card data | **CBACT02C** | R: CARDFILE |
| 3 | READCUST | READCUST.jcl | Read and display customer data | **CBCUS01C** | R: CUSTFILE |
| 4 | READXREF | READXREF.jcl | Read and display cross-reference data | **CBACT03C** | R: XREFFILE |
| 5 | POSTTRAN | POSTTRAN.jcl | Post daily transactions and update account balances | **CBTRN02C** | R: DALYTRAN, XREFFILE · W: TRANFILE, DALYREJS · I-O: ACCTFILE, TCATBALF |
| 6 | TRANREPT | TRANREPT.jcl | Generate transaction detail report | SORT → **CBTRN03C** | R: TRANFILE, CARDXREF, TRANTYPE, TRANCATG · W: TRANREPT |
| 7 | INTCALC | INTCALC.jcl | Monthly interest calculation | **CBACT04C** | R: TCATBALF, XREFFILE, DISCGRP · I-O: ACCTFILE · W: TRANSACT |
| 8 | CREASTMT | CREASTMT.JCL | Create account statements | IDCAMS → SORT → IDCAMS → IEFBR14 → **CBSTM03A** | R: TRANSACT, XREFFILE, ACCTFILE, CUSTFILE · W: STMTFILE, HTMLFILE |
| 9 | CBEXPORT | CBEXPORT.jcl | Export all data for branch migration | IDCAMS (pre-delete) → **CBEXPORT** | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · W: EXPFILE |
| 10 | CBIMPORT | CBIMPORT.jcl | Import branch migration data | **CBIMPORT** | R: EXPFILE · W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT |
| 11 | WAITSTEP | WAITSTEP.jcl | Wait utility step (delay between pipeline stages) | **COBSWAIT** | *(none)* |

### 3.3 File Maintenance Jobs

| # | Job Name | JCL File | Purpose | Key Steps |
|---|----------|----------|---------|-----------|
| 1 | CLOSEFIL | CLOSEFIL.jcl | Close CICS files for batch processing window | SDSF SET DSNAME CLOSED |
| 2 | OPENFIL | OPENFIL.jcl | Open CICS files after batch processing | SDSF SET DSNAME OPEN |
| 3 | TRANBKP | TRANBKP.jcl | Backup transaction file before processing | SORT (backup) → IDCAMS DELETE/DEFINE |
| 4 | COMBTRAN | COMBTRAN.jcl | Combine/merge transaction records | SORT (merge) → IDCAMS REPRO |
| 5 | DEFGDGB | DEFGDGB.jcl | Define GDG base for transaction backups | IDCAMS DEFINE GDG |
| 6 | DEFGDGD | DEFGDGD.jcl | Backup reference data (TRANTYPE, TRANCATG, DISCGRP) into GDG generations | IDCAMS DEFINE GDG → IEBGENER copy × 3 |
| 7 | PRTCATBL | PRTCATBL.jcl | Print category balance file | SORT print → SORT report |
| 8 | DUSRSECJ | DUSRSECJ.jcl | Define/populate user security VSAM from PS | IEFBR14 → IEBGENER → IDCAMS DEFINE → REPRO |

### 3.4 Utility / Administrative Jobs

| # | Job Name | JCL File | Purpose |
|---|----------|----------|---------|
| 1 | CBADMCDJ | CBADMCDJ.jcl | CICS CSD update — define programs, mapsets, transactions, and files |
| 2 | TXT2PDF1 | TXT2PDF1.JCL | Convert text statement file to PDF |
| 3 | FTPJCL | FTPJCL.JCL | FTP file transfer job |
| 4 | INTRDRJ1 | INTRDRJ1.JCL | Internal reader — backup and submit chained job |
| 5 | INTRDRJ2 | INTRDRJ2.JCL | Internal reader — chained backup job |

### 3.5 Sub-Application JCL

#### Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/jcl/`

| # | Job Name | JCL File | Purpose | Key Steps |
|---|----------|----------|---------|-----------|
| 1 | CBPAUP0J | CBPAUP0J.jcl | Run IMS batch to delete expired pending authorizations | DFSRRC00 (IMS BMP) |
| 2 | DBPAUTP0 | DBPAUTP0.jcl | Unload IMS pending-auth DB | IEFBR14 → DFSRRC00 (IMS unload) |
| 3 | LOADPADB | LOADPADB.JCL | Load IMS pending-auth DB from sequential files | DFSRRC00 (IMS load) |
| 4 | UNLDGSAM | UNLDGSAM.JCL | Unload IMS DB to GSAM files | DFSRRC00 (IMS unload via GSAM) |
| 5 | UNLDPADB | UNLDPADB.JCL | Unload IMS DB to sequential files | IEFBR14 → DFSRRC00 |

#### Transaction Type (DB2) — `app/app-transaction-type-db2/jcl/`

| # | Job Name | JCL File | Purpose | Key Steps |
|---|----------|----------|---------|-----------|
| 1 | CREADB21 | CREADB21.jcl | Create DB2 tables, load reference data | IKJEFT01 (DSNTEP4 bind/load) |
| 2 | MNTTRDB2 | MNTTRDB2.jcl | Bind/maintain DB2 transaction type program | IKJEFT01 (DB2 BIND) |
| 3 | TRANEXTR | TRANEXTR.jcl | Extract transaction type data from DB2 to PS backups | IEBGENER backup → IKJEFT01 SQL extract |

---

## 4. Scheduler Definitions — `app/scheduler/`

The Control-M scheduler (`CardDemo.controlm`) defines three batch pipeline cycles:

### 4.1 DAILY — Transaction Backup
```
CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
```
Runs every day. Closes CICS files, backs up transaction data, waits, then reopens files.

### 4.2 WEEKLY — Transaction Types DB Refresh + Disclosure Groups Refresh

**Pipeline 1 — TransactionTypesDBRefresh:**
```
TRANEXTR (extract from DB2)
```

**Pipeline 2 — DisclosureGroupsRefresh** (depends on Pipeline 1):
```
MNTTRDB2 → CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL
```
Runs on Saturdays. Refreshes DB2 transaction types, then reloads disclosure groups into VSAM.

### 4.3 MONTHLY — Interest Calculation
```
CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL
```
Runs monthly. Calculates interest, merges transactions, then reopens files.

---

## 5. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| — Batch programs | 16 |
| — Online (CICS) programs | 22 |
| — Shared utilities/subroutines | 6 |
| Total copybooks (app/cpy/) | 30 |
| Total BMS map copybooks (app/cpy-bms/) | 16 |
| Total JCL jobs (app/jcl/) | 37 |
| Sub-app JCL jobs | 8 |
| Total lines of COBOL | ~27,000 |
| VSAM KSDS datasets | 9 |
| BMS screen maps | 17 |
