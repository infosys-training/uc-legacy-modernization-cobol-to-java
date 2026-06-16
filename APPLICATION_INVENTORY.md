# APPLICATION INVENTORY — AWS CardDemo

> Credit Card Management System — Online (CICS) and Batch (JCL/COBOL)

## Summary

| Metric | Value |
|--------|-------|
| Total COBOL Programs | 44 (31 main + 13 sub-app) |
| Online (CICS) Programs | 21 |
| Batch Programs | 16 |
| Sub-Application Programs | 13 (IMS/DB2/MQ/VSAM) |
| Copybooks | 47 |
| JCL Jobs | 38 |
| BMS Maps | 17 |
| Total Lines of Code | ~27,350 |

---

## 1. Main COBOL Programs (`app/cbl/`)

### 1.1 Batch Programs (CB* prefix)

| Filename | Program-ID | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|----------|-----------|---------|----------------|-------------------|---------------------|
| CBACT01C.cbl | CBACT01C | Read account VSAM file, write to output/array/VBR files | Batch | R: ACCTFILE; W: OUTFILE, ARRYFILE, VBRCFILE; CALL COBDATFT | CVACT01Y, CODATECN |
| CBACT02C.cbl | CBACT02C | Read and print card data file | Batch | R: CARDFILE; CALL CEE3ABD | CVACT02Y |
| CBACT03C.cbl | CBACT03C | Read and print account cross-reference file | Batch | R: XREFFILE; CALL CEE3ABD | CVACT03Y |
| CBACT04C.cbl | CBACT04C | Interest calculator — reads tran cat balance, xref, discgrp, account; computes interest; writes transaction records | Batch | R: TCATBALF, XREFFILE, DISCGRP; I-O: ACCTFILE; W: TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| CBCUS01C.cbl | CBCUS01C | Read and print customer data file | Batch | R: CUSTFILE; CALL CEE3ABD | CVCUS01Y |
| CBEXPORT.cbl | CBEXPORT | Export customer data for branch migration — reads all normalized files, writes multi-record export | Batch | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; W: EXPFILE | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBIMPORT.cbl | CBIMPORT | Import customer data from branch migration export — splits export into normalized files | Batch | R: EXPFILE; W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBSTM03A.CBL | CBSTM03A | Statement generation — reads customer/account/transaction data, generates text+HTML statements | Batch | R: XREFFILE, CUSTFILE, ACCTFILE, TRANSACT; W: STMTFILE, HTMLFILE; CALL CBSTM03B | COSTM01, CVACT01Y, CVCUS01Y, CVACT03Y, CVTRA05Y |
| CBSTM03B.CBL | CBSTM03B | I/O submodule for statement generation (called by CBSTM03A) | Batch (Module) | File I/O delegated from CBSTM03A | COSTM01 |
| CBTRN01C.cbl | CBTRN01C | Post records from daily transaction file — validates via xref/account lookup | Batch | R: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| CBTRN02C.cbl | CBTRN02C | Post daily transactions with full validation, reject handling, account/tcatbal updates | Batch | R: DALYTRAN, XREFFILE; I-O: ACCTFILE, TCATBALF; W: TRANFILE, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| CBTRN03C.cbl | CBTRN03C | Print transaction detail report with date filtering | Batch | R: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; W: TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| COBSWAIT.cbl | COBSWAIT | Utility — wait for specified centiseconds | Batch (Utility) | CALL MVSWAIT | (none) |
| CSUTLDTC.cbl | CSUTLDTC | Date validation utility — calls CEEDAYS for date conversion | Batch (Utility) | CALL CEEDAYS | (none) |

### 1.2 Online Programs (CO* prefix, CICS)

| Filename | Program-ID | Purpose | CICS Resources | Copybooks Referenced |
|----------|-----------|---------|----------------|---------------------|
| COSGN00C.cbl | COSGN00C | Signon screen — authenticates users, routes to admin or user menu | USRSEC (file) | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COMEN01C.cbl | COMEN01C | Main menu for regular users — hub routing to 11 sub-programs | (XCTL to sub-programs) | COCOM01Y, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COADM01C.cbl | COADM01C | Admin menu for admin users — routes to user management and DB2 programs | (XCTL to sub-programs) | COCOM01Y, COADM02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COACTVWC.cbl | COACTVWC | Account view — displays account details by account ID or card number | CXACAIX, ACCTDAT, CUSTDAT | COCOM01Y, CVCRD01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| COACTUPC.cbl | COACTUPC | Account update — largest program (~4236 LOC), manages account and customer data editing with full field validation | ACCTDAT, CUSTDAT, CARDDAT, CARDAIX, CXACAIX | COCOM01Y, CSUTLDWY, CVCRD01Y, CSLKPCDY, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, CSSTRPFY, CSSETATY, CSUTLDPY |
| COCRDLIC.cbl | COCRDLIC | Credit card list — paginated browse of cards by account | CARDDAT, CARDAIX | COCOM01Y, CVCRD01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| COCRDSLC.cbl | COCRDSLC | Credit card detail view — displays full card information | CARDDAT, CARDAIX | COCOM01Y, CVCRD01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| COCRDUPC.cbl | COCRDUPC | Credit card update — edit/confirm/save workflow for card data | CARDDAT, CARDAIX | COCOM01Y, CVCRD01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| COBIL00C.cbl | COBIL00C | Bill payment — pay account balance, create transaction record | ACCTDAT, CXACAIX, TRANSACT | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y |
| COTRN00C.cbl | COTRN00C | List transactions — paginated browse of transaction history | TRANSACT | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| COTRN01C.cbl | COTRN01C | View a transaction — display single transaction detail | TRANSACT | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| COTRN02C.cbl | COTRN02C | Add a new transaction — validates card/account, creates transaction record | TRANSACT, ACCTDAT, CCXREF, CXACAIX | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y |
| CORPT00C.cbl | CORPT00C | Submit batch report job via TDQ — submits JCL for transaction reporting | TRANSACT (browse), TDQ JOBS | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y |
| COUSR00C.cbl | COUSR00C | List all users — paginated browse of security user records | USRSEC | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COUSR01C.cbl | COUSR01C | Add a new user — create security user record | USRSEC | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COUSR02C.cbl | COUSR02C | Update a user — modify existing security user record | USRSEC | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |
| COUSR03C.cbl | COUSR03C | Delete a user — remove security user record with confirmation | USRSEC | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y |

---

## 2. Sub-Application Programs

### 2.1 Authorization (IMS/DB2/MQ) — `app/app-authorization-ims-db2-mq/cbl/`

| Filename | Program-ID | LOC | Purpose | Subsystems | Copybooks |
|----------|-----------|-----|---------|------------|-----------|
| COPAUA0C.cbl | COPAUA0C | 1026 | Card authorization decision — spans IMS, DB2, and MQ | CICS + IMS + DB2 + MQ | COCOM01Y, CCPAURQY, CCPAURLY, CIPAUSMY, CIPAUDTY, IMSFUNCS |
| COPAUS0C.cbl | COPAUS0C | 1032 | Summary view of pending authorization messages (IMS browse) | CICS + IMS | COCOM01Y, CIPAUSMY, CIPAUDTY |
| COPAUS1C.cbl | COPAUS1C | 604 | Detail view of authorization message with update capability | CICS + IMS | COCOM01Y, CIPAUSMY, CIPAUDTY |
| COPAUS2C.cbl | COPAUS2C | 244 | Mark authorization message as fraud (DB2 insert) | CICS + DB2 | COCOM01Y, CIPAUDTY |
| CBPAUP0C.cbl | CBPAUP0C | 386 | Delete expired pending authorization messages (IMS batch purge) | Batch + IMS | CIPAUSMY, CIPAUDTY |
| PAUDBLOD.CBL | PAUDBLOD | 369 | IMS database load for pending authorizations | Batch + IMS | PADFLPCB, PAUTBPCB |
| PAUDBUNL.CBL | PAUDBUNL | 317 | IMS database unload for pending authorizations | Batch + IMS | PASFLPCB, PAUTBPCB |
| DBUNLDGS.CBL | DBUNLDGS | 366 | GSAM (Generalized Sequential Access Method) unload utility | Batch + IMS/GSAM | PADFLPCB |

### 2.2 Transaction Type (DB2) — `app/app-transaction-type-db2/cbl/`

| Filename | Program-ID | LOC | Purpose | Subsystems | Copybooks |
|----------|-----------|-----|---------|------------|-----------|
| COTRTLIC.cbl | COTRTLIC | 2098 | List transaction types — cursor-based DB2 pagination | CICS + DB2 | COCOM01Y, CSDB2RPY, CSDB2RWY, COTTL01Y, CSDAT01Y, CSMSG01Y |
| COTRTUPC.cbl | COTRTUPC | 1702 | Update/delete transaction types — cascading deletes in DB2 | CICS + DB2 | COCOM01Y, CSDB2RPY, CSDB2RWY, COTTL01Y, CSDAT01Y, CSMSG01Y |
| COBTUPDT.cbl | COBTUPDT | 237 | Batch update of transaction type based on user input | Batch + DB2 | CSDB2RPY |

### 2.3 VSAM/MQ — `app/app-vsam-mq/cbl/`

| Filename | Program-ID | LOC | Purpose | Subsystems | Copybooks |
|----------|-----------|-----|---------|------------|-----------|
| COACCT01.cbl | COACCT01 | 620 | Account inquiry via MQ — reads account data and sends via message queue | CICS + MQ + VSAM | COCOM01Y, CVACT01Y |
| CODATE01.cbl | CODATE01 | 524 | Date inquiry via MQ — provides date formatting services over MQ | CICS + MQ | COCOM01Y, CSDAT01Y |

---

## 3. JCL Jobs (`app/jcl/`)

### 3.1 VSAM Cluster Definition Jobs (IDCAMS)

| JCL File | Purpose | Utility | Dataset Defined |
|----------|---------|---------|-----------------|
| ACCTFILE.jcl | Delete/define account data VSAM KSDS | IDCAMS | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS |
| CARDFILE.jcl | Delete/define card data file | SDSF/IDCAMS | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS |
| CUSTFILE.jcl | Define customer file VSAM KSDS | SDSF/IDCAMS | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS |
| XREFFILE.jcl | Delete/define cross-reference file | IDCAMS | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS |
| TRANFILE.jcl | Define transaction master VSAM KSDS | SDSF/IDCAMS | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS |
| TCATBALF.jcl | Define transaction category balance | IDCAMS | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS |
| DISCGRP.jcl | Define disclosure group file | IDCAMS | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS |
| TRANCATG.jcl | Define transaction category | IDCAMS | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS |
| TRANTYPE.jcl | Define transaction type | IDCAMS | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS |
| TRANIDX.jcl | Define AIX (alternate index) on transaction master | IDCAMS | AWS.M2.CARDDEMO.TRANSACT.VSAM.AIX |
| DEFCUST.jcl | Define customer data file (alternate) | IDCAMS | AWS.M2.CARDDEMO.CUSTDATA |
| DALYREJS.jcl | Define GDG base for daily rejects | IDCAMS | AWS.M2.CARDDEMO.DALYREJS (GDG) |
| REPTFILE.jcl | Define GDG base for report file | IDCAMS | AWS.M2.CARDDEMO.REPTFILE (GDG) |
| DEFGDGB.jcl | Define GDG bases for batch output | IDCAMS | Multiple GDG bases |
| DEFGDGD.jcl | Define DB2 GDG bases | IDCAMS | DB2-related GDG bases |

### 3.2 Batch Processing Jobs

| JCL File | Purpose | Program Executed | Key DD Names |
|----------|---------|-----------------|--------------|
| POSTTRAN.jcl | Post daily transactions to master file | CBTRN02C | TRANFILE, DALYTRAN, XREFFILE, DALYREJS, ACCTFILE, TCATBALF |
| INTCALC.jcl | Calculate interest on account balances | CBACT04C | TCATBALF, XREFFILE, XREFFIL1, ACCTFILE, DISCGRP, TRANSACT |
| TRANREPT.jcl | Generate daily transaction report | CBTRN03C (via REPROC proc) | TRANFILE, CARDXREF, TRANTYPE, TRANCATG, REPTFILE |
| CREASTMT.JCL | Generate customer statements | CBSTM03A | XREFFILE, CUSTFILE, ACCTFILE, TRANSACT, STMTFILE |
| READACCT.jcl | Read/print account data | CBACT01C | ACCTFILE, OUTFILE, ARRYFILE, VBRCFILE |
| READCARD.jcl | Read/print card data | CBACT02C | CARDFILE |
| READCUST.jcl | Read/print customer data | CBCUS01C | CUSTFILE |
| READXREF.jcl | Read/print cross-reference data | CBACT03C | XREFFILE |
| CBEXPORT.jcl | Export data for branch migration | CBEXPORT (via IDCAMS + program) | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE, EXPFILE |
| CBIMPORT.jcl | Import data from branch migration | CBIMPORT | EXPFILE, CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT |
| COMBTRAN.jcl | Combine/sort transaction files | SORT | SORTIN (transaction datasets) |
| PRTCATBL.jcl | Print transaction category balance | REPROC proc | TCATBALF |
| TRANBKP.jcl | Backup transaction file | REPROC proc | TRANSACT |
| WAITSTEP.jcl | Execute wait utility | COBSWAIT | (none) |

### 3.3 Administrative/Utility Jobs

| JCL File | Purpose | Utility/Program |
|----------|---------|-----------------|
| CBADMCDJ.jcl | CICS CSD (resource definition) update | DFHCSDUP |
| CLOSEFIL.jcl | Close CICS files (SDSF CEMT command) | SDSF |
| OPENFIL.jcl | Open CICS files (SDSF CEMT command) | SDSF |
| DUSRSECJ.jcl | Delete/recreate user security dataset | IEFBR14 + IDCAMS |
| ESDSRRDS.jcl | Create ESDS/RRDS datasets | IEFBR14 + IDCAMS |
| FTPJCL.JCL | FTP file transfer job | FTP |
| INTRDRJ1.JCL | Internal reader job 1 — copy and submit job 2 | IDCAMS + IEBGENER (INTRDR) |
| INTRDRJ2.JCL | Internal reader job 2 — backup via internal reader chain | IDCAMS |
| TXT2PDF1.JCL | Convert text report to PDF | IKJEFT1B (TSO batch) |

---

## 4. BMS Maps (`app/bms/`)

| BMS File | Associated Program | Screen Purpose |
|----------|-------------------|----------------|
| COSGN00.bms | COSGN00C | Sign-on screen |
| COADM01.bms | COADM01C | Admin menu |
| COMEN01.bms | COMEN01C | Main user menu |
| COACTUP.bms | COACTUPC | Account update form |
| COACTVW.bms | COACTVWC | Account view display |
| COCRDLI.bms | COCRDLIC | Credit card list |
| COCRDSL.bms | COCRDSLC | Credit card detail |
| COCRDUP.bms | COCRDUPC | Credit card update form |
| COBIL00.bms | COBIL00C | Bill payment |
| COTRN00.bms | COTRN00C | Transaction list |
| COTRN01.bms | COTRN01C | Transaction view |
| COTRN02.bms | COTRN02C | Transaction add |
| CORPT00.bms | CORPT00C | Report request |
| COUSR00.bms | COUSR00C | User list |
| COUSR01.bms | COUSR01C | User add |
| COUSR02.bms | COUSR02C | User update |
| COUSR03.bms | COUSR03C | User delete |

---

## 5. Supporting Artifacts

### 5.1 JCL Procedures (`app/proc/`)

| Procedure | Purpose |
|-----------|---------|
| REPROC.prc | Reusable procedure for report generation (unload VSAM + execute report program) |
| TRANREPT.prc | Transaction report procedure (used by TRANREPT.jcl) |

### 5.2 Scheduler Definitions (`app/scheduler/`)

| File | Purpose |
|------|---------|
| CardDemo.controlm | Control-M job scheduling definitions (daily batch pipeline) |
| CardDemo.ca7 | CA-7 scheduling definitions (alternative scheduler) |

### 5.3 Data Files (`app/data/`)

| Directory | Contents |
|-----------|----------|
| ASCII/ | ASCII-encoded sample data files for testing |
| EBCDIC/ | EBCDIC-encoded production-format data files |

---

## 6. Lines of Code Summary

| Program | LOC | Classification |
|---------|-----|----------------|
| COACTUPC.cbl | 4,236 | Online |
| COTRTLIC.cbl | 2,098 | Online (DB2) |
| COTRTUPC.cbl | 1,702 | Online (DB2) |
| COCRDUPC.cbl | 1,560 | Online |
| COCRDLIC.cbl | 1,459 | Online |
| COPAUS0C.cbl | 1,032 | Online (IMS) |
| COPAUA0C.cbl | 1,026 | Online (IMS/DB2/MQ) |
| COACTVWC.cbl | 941 | Online |
| CBSTM03A.CBL | 924 | Batch |
| COCRDSLC.cbl | 887 | Online |
| COTRN02C.cbl | 783 | Online |
| CBTRN02C.cbl | 731 | Batch |
| COTRN00C.cbl | 699 | Online |
| COUSR00C.cbl | 695 | Online |
| CBACT04C.cbl | 652 | Batch |
| CBTRN03C.cbl | 649 | Batch |
| CORPT00C.cbl | 649 | Online |
| COPAUS1C.cbl | 604 | Online (IMS) |
| COACCT01.cbl | 620 | Online (MQ) |
| CBEXPORT.cbl | 582 | Batch |
| COBIL00C.cbl | 572 | Online |
| CODATE01.cbl | 524 | Online (MQ) |
| CBTRN01C.cbl | 494 | Batch |
| CBIMPORT.cbl | 487 | Batch |
| CBACT01C.cbl | 430 | Batch |
| COUSR02C.cbl | 414 | Online |
| CBPAUP0C.cbl | 386 | Batch (IMS) |
| PAUDBLOD.CBL | 369 | Batch (IMS) |
| DBUNLDGS.CBL | 366 | Batch (IMS/GSAM) |
| COUSR03C.cbl | 359 | Online |
| COTRN01C.cbl | 330 | Online |
| PAUDBUNL.CBL | 317 | Batch (IMS) |
| COMEN01C.cbl | 308 | Online |
| COUSR01C.cbl | 299 | Online |
| COADM01C.cbl | 288 | Online |
| COSGN00C.cbl | 260 | Online |
| COPAUS2C.cbl | 244 | Online (DB2) |
| COBTUPDT.cbl | 237 | Batch (DB2) |
| CBSTM03B.CBL | 230 | Batch (Module) |
| CBACT02C.cbl | 178 | Batch |
| CBACT03C.cbl | 178 | Batch |
| CBCUS01C.cbl | 178 | Batch |
| CSUTLDTC.cbl | 157 | Batch (Utility) |
| COBSWAIT.cbl | 41 | Batch (Utility) |
