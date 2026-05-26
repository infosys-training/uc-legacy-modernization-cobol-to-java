# APPLICATION INVENTORY — CardDemo COBOL Estate

> Auto-generated analysis of the CardDemo credit-card management system.

---

## 1. Program Inventory — `app/cbl/`

### 1.1 Batch Programs

| # | Filename | Program-ID | Purpose | Key I/O (Files Read / Written) | Copybooks Referenced |
|---|----------|------------|---------|-------------------------------|---------------------|
| 1 | CBACT01C.cbl | CBACT01C | Read account VSAM file and write to sequential/array/VBR output files | R: ACCTFILE (KSDS) · W: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | CBACT02C | Read and print card data file | R: CARDFILE (KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | CBACT03C | Read and print account cross-reference data file | R: XREFFILE (KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | CBACT04C | Interest calculator — compute interest charges per account using disclosure group rates | R: ACCTFILE, XREFFILE, DISCGRP, TCATBALF, TRANSACT · W: TRANFILE | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | CBCUS01C | Read and print customer data file | R: CUSTFILE (KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | CBEXPORT | Export customer, account, card, xref, and transaction data for branch migration | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE · W: EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export file | R: EXPFILE · W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | CBSTM03A | Print account statements from transaction data (generates HTML) | R: STMTFILE (KSDS) · W: HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | CBSTM03B | File processing sub-module for statement generation — reads xref, customer, account, transaction files | R: XREFFILE, CUSTFILE, ACCTFILE · W: TRNXFILE | *(inline definitions)* |
| 10 | CBTRN01C.cbl | CBTRN01C | Post records from daily transaction file — validate transactions against accounts/cards/customers | R: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE · W: TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | CBTRN02C | Post daily transactions — update account balances and transaction category balances | R: DALYTRAN, XREFFILE, ACCTFILE, TCATBALF · W: TRANSACT, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVTRA01Y, CVACT01Y |
| 12 | CBTRN03C.cbl | CBTRN03C | Print transaction detail report with type/category lookups | R: TRANSACT, XREFFILE, TRANTYPE, TRANCATG, DATEPARM · W: report output | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COBSWAIT.cbl | COBSWAIT | Utility — wait for a specified duration (parm in centiseconds) | *(none — uses CALL)* | *(none)* |
| 14 | CSUTLDTC.cbl | CSUTLDTC | Date validation utility — calls CEEDAYS to validate/convert dates | *(none — called as sub-program)* | *(none)* |

### 1.2 Online (CICS) Programs

| # | Filename | Program-ID | Purpose | VSAM Files Accessed | DB2 Tables | Copybooks Referenced |
|---|----------|------------|---------|---------------------|------------|---------------------|
| 1 | COACTUPC.cbl | COACTUPC | Accept and process account update (largest program in the estate) | ACCTDAT (R/W), CARDXREF (R), CUSTDAT (R), CARDDAT (R) | — | COCOM01Y, COACTUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, CSSTRPFY |
| 2 | COACTVWC.cbl | COACTVWC | Accept and process account view request | ACCTDAT (R), CARDXREF (R), CUSTDAT (R), CARDDAT (R) | — | COCOM01Y, COACTVW, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y |
| 3 | COADM01C.cbl | COADM01C | Admin menu for admin users | — | — | COCOM01Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, COADM02Y, CSUSR01Y |
| 4 | COBIL00C.cbl | COBIL00C | Bill payment — pay account balance in full and generate payment transactions | ACCTDAT (R/W), CARDXREF (R), TRANSACT (W) | — | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, CSUSR01Y |
| 5 | COCRDLIC.cbl | COCRDLIC | List credit cards with browse/search capability | CARDDAT (R), CARDXREF (R) | — | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVCRD01Y, CVACT02Y, CVACT03Y |
| 6 | COCRDSLC.cbl | COCRDSLC | Accept and process credit card detail selection | CARDDAT (R), CARDXREF (R), ACCTDAT (R) | — | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y |
| 7 | COCRDUPC.cbl | COCRDUPC | Accept and process credit card detail update | CARDDAT (R/W), CARDXREF (R), ACCTDAT (R) | — | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CSSTRPFY |
| 8 | COMEN01C.cbl | COMEN01C | Main menu for regular users — navigation hub | — | — | COCOM01Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, COMEN02Y, CSUSR01Y |
| 9 | CORPT00C.cbl | CORPT00C | Print transaction reports by submitting batch JCL via internal reader | — | — | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| 10 | COSGN00C.cbl | COSGN00C | Sign-on screen — authenticate users against USRSEC file | USRSEC (R) | — | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| 11 | COTRN00C.cbl | COTRN00C | List transactions from TRANSACT file with browse/page | TRANSACT (R) | — | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| 12 | COTRN01C.cbl | COTRN01C | View a transaction detail from TRANSACT file | TRANSACT (R) | — | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| 13 | COTRN02C.cbl | COTRN02C | Add a new transaction to TRANSACT file | TRANSACT (W), CXACAIX (R), CCXREF (R) | — | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y |
| 14 | COUSR00C.cbl | COUSR00C | List all users from USRSEC file | USRSEC (R) | — | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| 15 | COUSR01C.cbl | COUSR01C | Add a new regular/admin user to USRSEC file | USRSEC (W) | — | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| 16 | COUSR02C.cbl | COUSR02C | Update a user in USRSEC file | USRSEC (R/W) | — | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| 17 | COUSR03C.cbl | COUSR03C | Delete a user from USRSEC file | USRSEC (R/D) | — | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |

---

## 2. Sub-Application Programs

### 2.1 Authorization Sub-App (`app/app-authorization-ims-db2-mq/`)

IMS/DB2/MQ-based card authorization processing.

| # | Filename | Program-ID | Type | Purpose | Key I/O | Copybooks |
|---|----------|------------|------|---------|---------|-----------|
| 1 | CBPAUP0C.cbl | CBPAUP0C | Batch (IMS) | Delete expired pending authorization messages | IMS DB: DBPAUTP0 | CIPAUSMY, CIPAUDTY |
| 2 | COPAUA0C.cbl | COPAUA0C | Online (CICS/IMS/MQ) | Card authorization decision program — approve/decline transactions | IMS DB: DBPAUTP0, MQ queues | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY |
| 3 | COPAUS0C.cbl | COPAUS0C | Online (CICS/IMS/BMS) | Summary view of authorization messages | IMS DB: DBPAUTP0 (browse) | CIPAUDTY, CIPAUSMY, COPAU00 |
| 4 | COPAUS1C.cbl | COPAUS1C | Online (CICS/IMS/BMS) | Detail view of a single authorization message | IMS DB: DBPAUTP0 (read) | CIPAUDTY, CIPAUSMY, COPAU01 |
| 5 | COPAUS2C.cbl | COPAUS2C | Online (CICS/IMS/DB2) | Mark authorization message as fraud | IMS DB + DB2: AUTHFRDS | CIPAUDTY, CIPAUSMY |
| 6 | DBUNLDGS.CBL | DBUNLDGS | Batch (IMS) | Unload IMS database to GSAM sequential files | IMS DB → GSAM: OUTFIL1, OUTFIL2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 7 | PAUDBLOD.CBL | PAUDBLOD | Batch (IMS) | Load authorization data from sequential files into IMS database | R: INFILE1, INFILE2 → IMS DB | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 8 | PAUDBUNL.CBL | PAUDBUNL | Batch (IMS) | Unload authorization IMS database to sequential output files | IMS DB → OUTFIL1, OUTFIL2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

### 2.2 Transaction Type Sub-App (`app/transaction-type-db2/`)

DB2-based transaction type maintenance.

| # | Filename | Program-ID | Type | Purpose | DB2 Tables | Copybooks |
|---|----------|------------|------|---------|------------|-----------|
| 1 | COBTUPDT.cbl | COBTUPDT | Batch | Update transaction type records based on user input | CARDDEMO.TRANSACTION_TYPE | CVCRD01Y |
| 2 | COTRTLIC.cbl | COTRTLIC | Online (CICS/DB2) | List transaction types for updates and deletes | CARDDEMO.TRANSACTION_TYPE | CVCRD01Y, COCOM01Y, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 3 | COTRTUPC.cbl | COTRTUPC | Online (CICS/DB2) | Accept and process transaction type update/add/delete | CARDDEMO.TRANSACTION_TYPE | CSUTLDWY, CVCRD01Y, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY |

### 2.3 VSAM/MQ Sub-App (`app/app-vsam-mq/`)

MQ-integrated account and date processing.

| # | Filename | Program-ID | Type | Purpose | Copybooks |
|---|----------|------------|------|---------|-----------|
| 1 | COACCT01.cbl | COACCT01 | Online (CICS/MQ) | Account operations with MQ message processing | *(inline)* |
| 2 | CODATE01.cbl | CODATE01 | Online (CICS/MQ) | Date processing with MQ message handling | *(inline)* |

---

## 3. JCL Job Inventory — `app/jcl/`

### 3.1 VSAM File Definition & Load Jobs

| # | Job Name | Filename | Purpose | Step Sequence | Key Datasets |
|---|----------|----------|---------|---------------|-------------|
| 1 | ACCTFILE | ACCTFILE.jcl | Define and load Account VSAM KSDS from sequential file | STEP05 (IDCAMS: delete), STEP10 (IDCAMS: define), STEP15 (IDCAMS: repro) | AWS.M2.CARDDEMO.ACCTDATA.PS → .ACCTDATA.VSAM.KSDS |
| 2 | CARDFILE | CARDFILE.jcl | Define and load Card VSAM KSDS with AIX | CLCIFIL (SDSF: close), STEP05–STEP60 (IDCAMS: delete/define/repro/AIX), OPCIFIL (SDSF: open) | AWS.M2.CARDDEMO.CARDDATA.PS → .CARDDATA.VSAM.KSDS |
| 3 | CUSTFILE | CUSTFILE.jcl | Define and load Customer VSAM KSDS | CLCIFIL (SDSF: close), STEP05–STEP15 (IDCAMS), OPCIFIL (SDSF: open) | AWS.M2.CARDDEMO.CUSTDATA.PS → .CUSTDATA.VSAM.KSDS |
| 4 | XREFFILE | XREFFILE.jcl | Define and load Card-Xref VSAM KSDS with AIX | STEP05–STEP30 (IDCAMS: delete/define/repro/AIX) | AWS.M2.CARDDEMO.CARDXREF.PS → .CARDXREF.VSAM.KSDS |
| 5 | TRANFILE | TRANFILE.jcl | Define and load Transaction VSAM KSDS with AIX | CLCIFIL (SDSF), STEP05–STEP30 (IDCAMS), OPCIFIL (SDSF) | AWS.M2.CARDDEMO.DALYTRAN.PS.INIT → .TRANSACT.VSAM.KSDS |
| 6 | TCATBALF | TCATBALF.jcl | Define and load Transaction Category Balance VSAM KSDS | STEP05–STEP15 (IDCAMS) | AWS.M2.CARDDEMO.TCATBALF.PS → .TCATBALF.VSAM.KSDS |
| 7 | TRANTYPE | TRANTYPE.jcl | Define and load Transaction Type VSAM KSDS | STEP05–STEP15 (IDCAMS) | AWS.M2.CARDDEMO.TRANTYPE.PS → .TRANTYPE.VSAM.KSDS |
| 8 | TRANCATG | TRANCATG.jcl | Define and load Transaction Category VSAM KSDS | STEP05–STEP15 (IDCAMS) | AWS.M2.CARDDEMO.TRANCATG.PS → .TRANCATG.VSAM.KSDS |
| 9 | DISCGRP | DISCGRP.jcl | Define and load Disclosure Group VSAM KSDS | STEP05–STEP15 (IDCAMS) | AWS.M2.CARDDEMO.DISCGRP.PS → .DISCGRP.VSAM.KSDS |
| 10 | DUSRSECJ | DUSRSECJ.jcl | Define and load User Security VSAM KSDS | PREDEL (IEFBR14), STEP01 (IEBGENER), STEP02–03 (IDCAMS) | AWS.M2.CARDDEMO.USRSEC.PS → .USRSEC.VSAM.KSDS |
| 11 | DEFCUST | DEFCUST.jcl | Define Customer VSAM clusters (alternate definition) | STEP05 (IDCAMS ×2) | *(inline IDCAMS control)* |
| 12 | TRANIDX | TRANIDX.jcl | Define Transaction VSAM AIX and paths | STEP20–STEP30 (IDCAMS) | *(AIX definitions for TRANSACT)* |
| 13 | ESDSRRDS | ESDSRRDS.jcl | Define ESDS and RRDS VSAM clusters (demo/test) | PREDEL–STEP08 (IEFBR14, IEBGENER, IDCAMS) | *(ESDS/RRDS demo datasets)* |

### 3.2 Batch Processing Jobs

| # | Job Name | Filename | Purpose | Step Sequence | Programs Executed |
|---|----------|----------|---------|---------------|-------------------|
| 1 | READACCT | READACCT.jcl | Read and dump account data | PREDEL (IEFBR14), STEP05 (CBACT01C) | CBACT01C |
| 2 | READCARD | READCARD.jcl | Read and dump card data | STEP05 (CBACT02C) | CBACT02C |
| 3 | READCUST | READCUST.jcl | Read and dump customer data | STEP05 (CBCUS01C) | CBCUS01C |
| 4 | READXREF | READXREF.jcl | Read and dump cross-reference data | STEP05 (CBACT03C) | CBACT03C |
| 5 | INTCALC | INTCALC.jcl | Calculate interest charges on accounts | STEP15 (CBACT04C) | CBACT04C |
| 6 | POSTTRAN | POSTTRAN.jcl | Post daily transactions to master files | STEP15 (CBTRN02C) | CBTRN02C |
| 7 | TRANREPT | TRANREPT.jcl | Generate transaction detail report | STEP05R (REPROC), STEP05R (SORT), STEP10R (CBTRN03C) | CBTRN03C |
| 8 | DALYREJS | DALYREJS.jcl | Define daily rejects GDG dataset | STEP05 (IDCAMS) | *(IDCAMS only)* |
| 9 | CREASTMT | CREASTMT.JCL | Create account statements (HTML) | DELDEF01 (IDCAMS), STEP010 (SORT), STEP020 (IDCAMS), STEP030 (IEFBR14), STEP040 (CBSTM03A) | CBSTM03A |
| 10 | CBEXPORT | CBEXPORT.jcl | Run branch migration export | STEP01 (IDCAMS), STEP02 (CBEXPORT) | CBEXPORT |
| 11 | CBIMPORT | CBIMPORT.jcl | Run branch migration import | STEP01 (CBIMPORT) | CBIMPORT |
| 12 | WAITSTEP | WAITSTEP.jcl | Wait step utility (pause between jobs) | WAIT (COBSWAIT) | COBSWAIT |
| 13 | COMBTRAN | COMBTRAN.jcl | Combine transaction backup with system transactions | STEP05R (SORT), STEP10 (IDCAMS) | SORT |
| 14 | PRTCATBL | PRTCATBL.jcl | Print transaction category balance report | DELDEF (IEFBR14), STEP05R (REPROC), STEP10R (SORT) | SORT |

### 3.3 Backup & Maintenance Jobs

| # | Job Name | Filename | Purpose | Step Sequence |
|---|----------|----------|---------|---------------|
| 1 | TRANBKP | TRANBKP.jcl | Backup transaction VSAM to GDG | STEP05R (REPROC), STEP05 (IDCAMS), STEP10 (IDCAMS) |
| 2 | DEFGDGB | DEFGDGB.jcl | Define GDG bases for backups | STEP05 (IDCAMS) |
| 3 | DEFGDGD | DEFGDGD.jcl | Define GDG bases and seed initial generations for trantype, trancatg, discgrp | STEP10–STEP60 (IDCAMS/IEBGENER) |

### 3.4 Utility & Miscellaneous Jobs

| # | Job Name | Filename | Purpose |
|---|----------|----------|---------|
| 1 | CLOSEFIL | CLOSEFIL.jcl | Close CICS files (SDSF) before batch processing |
| 2 | OPENFIL | OPENFIL.jcl | Open CICS files (SDSF) after batch processing |
| 3 | REPTFILE | REPTFILE.jcl | Define report output VSAM file |
| 4 | CBADMCDJ | CBADMCDJ.jcl | CICS CSD batch update (DFHCSDUP) for CardDemo definitions |
| 5 | FTPJCL.JCL | FTPJCL.JCL | FTP transfer utility |
| 6 | TXT2PDF1.JCL | TXT2PDF1.JCL | Convert text statement to PDF |
| 7 | INTRDRJ1.JCL | INTRDRJ1.JCL | Internal reader job submission (template 1) |
| 8 | INTRDRJ2.JCL | INTRDRJ2.JCL | Internal reader job submission (template 2) |

### 3.5 Sub-Application JCL — `app/app-authorization-ims-db2-mq/jcl/`

| # | Job Name | Filename | Purpose | Programs |
|---|----------|----------|---------|----------|
| 1 | CBPAUP0J | CBPAUP0J.jcl | Run IMS batch — delete expired authorizations | DFSRRC00 → CBPAUP0C |
| 2 | DBPAUTP0 | DBPAUTP0.jcl | Unload IMS authorization database | DFSRRC00 → PAUDBUNL |
| 3 | LOADPADB | LOADPADB.JCL | Load authorization data into IMS DB | DFSRRC00 → PAUDBLOD |
| 4 | UNLDGSAM | UNLDGSAM.JCL | Unload IMS auth DB to GSAM files | DFSRRC00 → DBUNLDGS |
| 5 | UNLDPADB | UNLDPADB.JCL | Unload IMS auth DB to sequential files | DFSRRC00 → PAUDBUNL |

### 3.6 Sub-Application JCL — `app/app-transaction-type-db2/jcl/`

| # | Job Name | Filename | Purpose | Programs |
|---|----------|----------|---------|----------|
| 1 | CREADB21 | CREADB21.jcl | Create DB2 tables and load transaction types/categories | IKJEFT01, IEFBR14 |
| 2 | MNTTRDB2 | MNTTRDB2.jcl | Maintain transaction type DB2 tables (scheduled weekly) | *(DB2 utilities)* |
| 3 | TRANEXTR | TRANEXTR.jcl | Extract transaction type data from DB2 to sequential files | IEBGENER, IKJEFT01 |

---

## 4. Control-M Scheduler Orchestration

Source: `app/scheduler/CardDemo.controlm`

### 4.1 DAILY — Transaction Backup Pipeline

```
CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL
```

Runs daily (ALL days). Closes CICS files, backs up transaction VSAM to GDG, waits, then reopens files.

### 4.2 WEEKLY — Transaction Types DB Refresh

```
MNTTRDB2 ─┬→ CLOSEFIL → DISCGRP → WAITSTEP → OPENFIL  (DisclosureGroupsRefresh)
           └→ TRANEXTR                                    (TransactionTypesDBRefresh)
```

Runs Saturdays. Refreshes DB2 transaction types, then reloads disclosure groups into VSAM and extracts type data.

### 4.3 MONTHLY — Interest Calculation Pipeline

```
CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL
```

Runs monthly. Closes files, calculates interest (CBACT04C), combines transactions, waits, reopens files.

---

## 5. Summary Statistics

| Category | Count |
|----------|-------|
| Main COBOL programs (`app/cbl/`) | 31 |
| Sub-app COBOL programs | 13 |
| **Total COBOL programs** | **44** |
| Online (CICS) programs | 22 |
| Batch programs | 22 |
| Main copybooks (`app/cpy/`) | 30 |
| Sub-app copybooks | 15 |
| **Total copybooks** | **45** |
| Main JCL jobs (`app/jcl/`) | 38 |
| Sub-app JCL jobs | 8 |
| **Total JCL jobs** | **46** |
| Total lines of COBOL code | ~25,800 |
| BMS screen maps | 18 |
| VSAM datasets | 10 |
| DB2 tables | 2 |
| IMS databases | 1 |
