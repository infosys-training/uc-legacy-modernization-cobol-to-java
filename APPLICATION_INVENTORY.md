# APPLICATION INVENTORY — CardDemo COBOL Estate

## 1. Program Inventory

### 1.1 Core Application Programs (`app/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|--------------------|--------------------|
| 1 | CBACT01C.cbl | Read account VSAM file, write to flat/array/VBR output files | Batch | R: ACCTFILE (KSDS); W: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | Read and print card data file | Batch | R: CARDFILE (KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | Read and print account cross-reference data file | Batch | R: XREFFILE (KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | Interest calculator — compute interest on account balances by transaction category | Batch | R: TCATBALF, XREFFILE, ACCTFILE, DISCGRP; R/W: TRANSACT | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | Read and print customer data file | Batch | R: CUSTFILE (KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | Export customer data for branch migration — reads all normalized files, creates multi-record export file | Batch | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; W: EXPFILE | CVCUS01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVTRA05Y, CVEXPORT |
| 7 | CBIMPORT.cbl | Import customer data from export file — splits into normalized target files with validation | Batch | R: EXPFILE; W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVTRA05Y, CVEXPORT |
| 8 | CBSTM03A.CBL | Print account statements from transaction data in plain text and HTML formats | Batch | R: via CBSTM03B; W: STMTFILE, HTMLFILE | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| 9 | CBSTM03B.CBL | Subroutine for CBSTM03A — file I/O handler for transaction, xref, customer, account files | Batch (sub) | R/W: TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE | _(none — uses FD inline)_ |
| 10 | CBTRN01C.cbl | Post records from daily transaction file — validate and insert into master files | Batch | R: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE; W: TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | Post daily transactions — validate, reject bad records, update account balances and category balances | Batch | R: DALYTRAN, XREFFILE, ACCTFILE; R/W: TRANFILE, TCATBALF; W: DALYREJS | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y |
| 12 | CBTRN03C.cbl | Print transaction detail report with type/category lookups | Batch | R: TRANFILE, CARDXREF, TRANTYPE, TRANCATG; W: TRANREPT; R: DATEPARM | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y |
| 13 | COACTUPC.cbl | Accept and process account update — CICS online account field editing and VSAM update | Online (CICS) | R/W: ACCTDAT, CCXREF, CXACAIX (VSAM via CICS) | COACTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CSUTLDPY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 14 | COACTVWC.cbl | Accept and process account view request — display account, card, and customer details | Online (CICS) | R: ACCTDAT, CCXREF, CXACAIX (VSAM via CICS) | COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 15 | COADM01C.cbl | Admin menu for admin users — route to user management and DB2 maintenance programs | Online (CICS) | R: USRSEC (VSAM via CICS) | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | Bill payment — pay account balance in full, create payment transaction record | Online (CICS) | R/W: TRANSACT, ACCTDAT, CXACAIX (VSAM via CICS) | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | Utility — wait for specified centiseconds (calls MVSWAIT assembler routine) | Batch (utility) | _(none — ACCEPT from SYSIN)_ | _(none)_ |
| 18 | COCRDLIC.cbl | List credit cards — all cards for admin, account-specific cards for regular users | Online (CICS) | R: CARDDAT, CARDAIX, CCXREF (VSAM via CICS STARTBR/READNEXT) | COCOM01Y, COCRDLI, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 19 | COCRDSLC.cbl | Credit card detail view — display full card info with account and customer details | Online (CICS) | R: CARDDAT, ACCTDAT, CUSTDAT, CCXREF (VSAM via CICS) | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 20 | COCRDUPC.cbl | Credit card update — edit and update card details with validation | Online (CICS) | R/W: CARDDAT, ACCTDAT, CUSTDAT, CCXREF (VSAM via CICS) | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 21 | COMEN01C.cbl | Main menu for regular users — 11 options routing to various functional programs | Online (CICS) | R: USRSEC (VSAM via CICS) | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 22 | CORPT00C.cbl | Print transaction reports — submit batch JCL job from CICS using extra-partition TDQ | Online (CICS) | R: TRANSACT (VSAM via CICS); W: TDQ (internal reader) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | Sign-on screen — authenticate user against USRSEC file, route to admin/regular menu | Online (CICS) | R: USRSEC (VSAM via CICS) | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | COTRN00C.cbl | List transactions from TRANSACT file with pagination | Online (CICS) | R: TRANSACT (VSAM via CICS STARTBR/READNEXT) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN01C.cbl | View a single transaction from TRANSACT file | Online (CICS) | R: TRANSACT (VSAM via CICS) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | Add a new transaction — validate card/account, write to TRANSACT and update balances | Online (CICS) | R/W: TRANSACT, ACCTDAT, CCXREF, CXACAIX (VSAM via CICS) | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | List all users from USRSEC file with pagination | Online (CICS) | R: USRSEC (VSAM via CICS STARTBR/READNEXT) | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | Add a new regular/admin user to USRSEC file | Online (CICS) | W: USRSEC (VSAM via CICS) | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR02C.cbl | Update a user in USRSEC file | Online (CICS) | R/W: USRSEC (VSAM via CICS) | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | Delete a user from USRSEC file | Online (CICS) | R/W: USRSEC (VSAM via CICS DELETE) | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | Date validation utility — calls CEEDAYS LE API to validate date formats | Batch (utility) | _(none — callable subroutine)_ | _(none)_ |

### 1.2 Authorization Sub-Application (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|--------------------|--------------------|
| 1 | CBPAUP0C.cbl | Delete expired pending authorization messages from IMS database | Batch (IMS) | R/W: IMS DB (CBLTDLI calls) | CIPAUDTY, CIPAUSMY, PAUTBPCB, IMSFUNCS |
| 2 | COPAUA0C.cbl | Card authorization decision — approve/deny via IMS DB lookup and DB2 fraud check, MQ messaging | Online (CICS/IMS/MQ/DB2) | R: IMS DB, DB2 AUTHFRDS table; W: MQ queue | CIPAUDTY, CIPAUSMY, CCPAURQY, CCPAURLY, CCPAUERY, IMSFUNCS, PAUTBPCB |
| 3 | COPAUS0C.cbl | Summary view of authorization messages (BMS screen) | Online (CICS/IMS/BMS) | R: IMS DB (CBLTDLI) | CIPAUDTY, CIPAUSMY, COPAU00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | COPAUS1C.cbl | Detail view of a single authorization message | Online (CICS/IMS/BMS) | R: IMS DB (CBLTDLI) | CIPAUDTY, CIPAUSMY, COPAU01, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 5 | COPAUS2C.cbl | Mark authorization message as fraud via DB2 update | Online (CICS/IMS/DB2) | R/W: DB2 AUTHFRDS table | CIPAUDTY, CIPAUSMY |
| 6 | PAUDBLOD.CBL | IMS database initial load — load authorization data from flat file | Batch (IMS) | R: flat file; W: IMS DB (CBLTDLI ISRT) | PAUTBPCB, IMSFUNCS |
| 7 | PAUDBUNL.CBL | IMS database unload — extract authorization data to flat file | Batch (IMS) | R: IMS DB (CBLTDLI GN); W: flat file | PAUTBPCB, IMSFUNCS |
| 8 | DBUNLDGS.CBL | Unload GSAM data from IMS | Batch (IMS/GSAM) | R: IMS GSAM | _(IMS PCB inline)_ |

### 1.3 Transaction Type DB2 Sub-Application (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|--------------------|--------------------|
| 1 | COTRTLIC.cbl | Transaction type list with DB2 cursor-based pagination | Online (CICS/DB2) | R: DB2 TRNTYPE, TRNTYCAT tables | COTRTLI, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA |
| 2 | COTRTUPC.cbl | Transaction type update via DB2 | Online (CICS/DB2) | R/W: DB2 TRNTYPE, TRNTYCAT tables | COTRTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA |
| 3 | COBTUPDT.cbl | Batch transaction type maintenance — DB2 insert/update/delete | Batch (DB2) | R/W: DB2 TRNTYPE, TRNTYCAT tables | CSDB2RPY, CSDB2RWY |

### 1.4 VSAM-MQ Sub-Application (`app/app-vsam-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|----------------|--------------------|--------------------|
| 1 | COACCT01.cbl | Account inquiry and update via VSAM and MQ messaging | Online (CICS/MQ) | R/W: ACCTDAT (VSAM via CICS); W: MQ queue | COCOM01Y, CMQV, CMQODV, CMQMDV, CMQGMOV, CMQPMOV, CMQTML |
| 2 | CODATE01.cbl | Date conversion service with MQ interface | Online (CICS/MQ) | MQ: CMQODV, CMQMDV | COCOM01Y, CMQV, CMQODV, CMQMDV, CMQGMOV, CMQPMOV, CMQTML |

---

## 2. JCL Job Inventory

### 2.1 Core JCL Jobs (`app/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | ACCTFILE.jcl | Define and load Account VSAM file | STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO from PS to KSDS) |
| 2 | CARDFILE.jcl | Define and load Card Data VSAM file with alternate index | CLCIFIL (SDSF close CICS files), STEP05 (IDCAMS DELETE), STEP10 (IDCAMS DEFINE), STEP15 (IDCAMS REPRO), STEP40 (DEFINE AIX), STEP50 (DEFINE PATH), STEP60 (BLDINDEX), OPCIFIL (SDSF open CICS files) |
| 3 | CBADMCDJ.jcl | Create CICS resource definitions for CardDemo (programs, mapsets, transactions, files) | STEP1 (DFHCSDUP CSD batch update) |
| 4 | CBEXPORT.jcl | Export customer data for branch migration | STEP01 (IDCAMS DEFINE export cluster), STEP02 (PGM=CBEXPORT) |
| 5 | CBIMPORT.jcl | Import customer data from export file | STEP01 (PGM=CBIMPORT) |
| 6 | CLOSEFIL.jcl | Close VSAM files in CICS region | CLCIFIL (SDSF CEMT SET FIL CLO for TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC) |
| 7 | COMBTRAN.jcl | Combine and sort current + system-generated transactions, reload to master | STEP05R (SORT), STEP10 (IDCAMS REPRO to VSAM) |
| 8 | CREASTMT.JCL | Create account statements | STEP10 (PGM=CBSTM03A) |
| 9 | CUSTFILE.jcl | Define and load Customer VSAM file | Close CICS files, STEP05 (DELETE), STEP10 (DEFINE), STEP15 (REPRO), Open CICS files |
| 10 | DALYREJS.jcl | Define daily rejects sequential file | STEP05 (DELETE), STEP10 (IEFBR14 allocate) |
| 11 | DEFCUST.jcl | Define customer VSAM with AIX on SSN | STEP05–STEP60 (DELETE, DEFINE, REPRO, DEFINE AIX, DEFINE PATH, BLDINDEX) |
| 12 | DEFGDGB.jcl | Define GDG base for transaction backups | STEP05 (IDCAMS DEFINE GDG) |
| 13 | DEFGDGD.jcl | Define GDG base for daily transaction archive | STEP05 (IDCAMS DEFINE GDG) |
| 14 | DISCGRP.jcl | Define and load Discount Group VSAM file | STEP05 (DELETE), STEP10 (DEFINE), STEP15 (REPRO) |
| 15 | DUSRSECJ.jcl | Define and load User Security VSAM file | Close CICS, DELETE, DEFINE, REPRO, Open CICS |
| 16 | ESDSRRDS.jcl | Define ESDS and RRDS VSAM files (system transaction ID generators) | Multiple DEFINE steps for ESDS and RRDS clusters |
| 17 | FTPJCL.JCL | FTP data transfer — upload datasets to/from remote host | FTP step with inline control cards |
| 18 | INTCALC.jcl | Run interest calculation batch program | STEP10 (PGM=CBACT04C) |
| 19 | INTRDRJ1.JCL | Internal reader submit — POSTTRAN job | STEP1 (writes JCL to internal reader) |
| 20 | INTRDRJ2.JCL | Internal reader submit — COMBTRAN job | STEP1 (writes JCL to internal reader) |
| 21 | OPENFIL.jcl | Open VSAM files in CICS region | OPCIFIL (SDSF CEMT SET FIL OPE) |
| 22 | POSTTRAN.jcl | Post daily transactions — multi-step pipeline | STEP10 (PGM=CBTRN01C validate), STEP20 (PGM=CBTRN02C post), STEP30 (SORT + REPRO backup) |
| 23 | PRTCATBL.jcl | Print transaction category balance file | STEP10 (IDCAMS PRINT) |
| 24 | READACCT.jcl | Read and print account file | STEP10 (PGM=CBACT01C) |
| 25 | READCARD.jcl | Read and print card file | STEP10 (PGM=CBACT02C) |
| 26 | READCUST.jcl | Read and print customer file | STEP10 (PGM=CBCUS01C) |
| 27 | READXREF.jcl | Read and print cross-reference file | STEP10 (PGM=CBACT03C) |
| 28 | REPTFILE.jcl | Define report sequential output file | STEP05 (DELETE), STEP10 (IEFBR14 allocate) |
| 29 | TCATBALF.jcl | Define and load Transaction Category Balance VSAM file | STEP05 (DELETE), STEP10 (DEFINE), STEP15 (REPRO) |
| 30 | TRANBKP.jcl | Backup transaction file to GDG | STEP05 (SORT from VSAM to GDG sequential) |
| 31 | TRANCATG.jcl | Define and load Transaction Category VSAM file | STEP05 (DELETE), STEP10 (DEFINE), STEP15 (REPRO) |
| 32 | TRANFILE.jcl | Define and load Transaction VSAM file | Close CICS, DELETE, DEFINE, REPRO, Open CICS |
| 33 | TRANIDX.jcl | Define alternate index on transaction file (by card number) | STEP10 (DEFINE AIX), STEP20 (DEFINE PATH), STEP30 (BLDINDEX) |
| 34 | TRANREPT.jcl | Print transaction detail report | STEP10 (PGM=CBTRN03C) |
| 35 | TRANTYPE.jcl | Define and load Transaction Type VSAM file | STEP05 (DELETE), STEP10 (DEFINE), STEP15 (REPRO) |
| 36 | TXT2PDF1.JCL | Convert text report to PDF | STEP1 (TXT2PDF utility) |
| 37 | WAITSTEP.jcl | Job step wait utility (JCL timer) | STEP10 (PGM=COBSWAIT) |
| 38 | XREFFILE.jcl | Define and load Card Cross-Reference VSAM file with AIX | Close CICS, DELETE cluster+AIX, DEFINE, REPRO, DEFINE AIX, DEFINE PATH, BLDINDEX, Open CICS |

### 2.2 Authorization Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | CBPAUP0J.jcl | Run expired authorization cleanup | STEP10 (PGM=CBPAUP0C via IMS) |
| 2 | DBPAUTP0.jcl | IMS DBD generation for authorization database | STEP1 (DBDGEN) |
| 3 | LOADPADB.JCL | Load authorization IMS database | STEP10 (PGM=PAUDBLOD) |
| 4 | UNLDPADB.JCL | Unload authorization IMS database | STEP10 (PGM=PAUDBUNL) |
| 5 | UNLDGSAM.JCL | Unload GSAM authorization data | STEP10 (PGM=DBUNLDGS) |

### 2.3 Transaction Type DB2 JCL (`app/app-transaction-type-db2/jcl/`)

| # | Job Name | Purpose | Step Sequence |
|---|----------|---------|---------------|
| 1 | CREADB21.jcl | Create DB2 tables for transaction types and categories | STEP1 (DB2 DDL via DSNTEP2) |
| 2 | MNTTRDB2.jcl | Batch maintenance on transaction type DB2 tables | STEP10 (PGM=COBTUPDT) |
| 3 | TRANEXTR.jcl | Extract transaction type data from DB2 to flat file | STEP1 (DB2 UNLOAD via DSNTEP2) |

---

## 3. Summary Statistics

| Metric | Count |
|--------|-------|
| Total COBOL programs | 44 |
| Batch programs | 18 |
| Online (CICS) programs | 21 |
| IMS programs | 5 |
| Total JCL jobs | 46 |
| VSAM file definition jobs | 14 |
| Batch execution jobs | 16 |
| CICS administration jobs | 3 |
| Utility/infrastructure jobs | 13 |
| BMS map definitions | 21 |
| Copybooks (app/cpy/) | 30 |
| Copybooks (app/cpy-bms/) | 17 |
| Sub-application copybooks | 17 |
| Total lines of COBOL | ~24,236 |
