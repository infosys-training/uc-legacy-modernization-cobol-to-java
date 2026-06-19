# APPLICATION INVENTORY — CardDemo COBOL Estate

## Executive Summary

| Metric | Count |
|--------|-------|
| Total COBOL Programs | 44 |
| Main Programs (app/cbl/) | 31 |
| Sub-Application Programs | 13 |
| JCL Jobs (app/jcl/) | 38 |
| Sub-Application JCL | 8 |
| Copybooks (all directories) | 47 |
| BMS Screen Maps | 19 |
| Total Lines of Code | ~27,350 |

---

## 1. Main COBOL Programs (`app/cbl/`)

### Batch Programs

| Filename | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|----------|-----|---------|-------------------|---------------------|
| CBACT01C.cbl | 430 | Read Account VSAM file and write to multiple output formats (flat, array, variable-length) | R: ACCTFILE (KSDS); W: OUTFILE, ARRYFILE, VBRCFILE | CVACT01Y, CODATECN |
| CBACT02C.cbl | 178 | Read and print Card data file sequentially | R: CARDFILE (KSDS) | CVACT02Y |
| CBACT03C.cbl | 178 | Read and print Account Cross-Reference data file | R: XREFFILE (KSDS) | CVACT03Y |
| CBACT04C.cbl | 652 | Interest calculator — compute interest/fees per transaction category balance | R: TCATBALF, XREFFILE, ACCTFILE, DISCGRP; W: TRANSACT, ACCTFILE (update) | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| CBCUS01C.cbl | 178 | Read and print Customer data file | R: CUSTFILE (KSDS) | CVCUS01Y |
| CBEXPORT.cbl | 582 | Export customer data for branch migration — reads all CardDemo files and creates multi-record export | R: CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; W: EXPFILE (KSDS) | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| CBIMPORT.cbl | 487 | Import customer data from export file — splits multi-record layout into normalized target files with validation | R: EXPFILE; W: CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT | CVEXPORT |
| CBSTM03A.CBL | 924 | Statement generation — reads customer/account/transaction data and produces text + HTML statements | R: XREFFILE, CUSTFILE, ACCTFILE, TRANFILE; W: STMTFILE, HTMLFILE; CALL: CBSTM03B | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| CBSTM03B.CBL | 230 | Statement I/O submodule — handles physical file writes for CBSTM03A | W: STMTFILE (sequential) | (inherits from CBSTM03A) |
| CBTRN01C.cbl | 494 | Post records from daily transaction file — validate and apply transactions | R: DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE; W: TRANFILE | CVTRA05Y, CVTRA06Y, CVACT03Y, CVCUS01Y, CVACT02Y, CVACT01Y |
| CBTRN02C.cbl | 731 | Post daily transactions — validate against cross-ref, update account balances, write rejects | R: DALYTRAN, XREFFILE, ACCTFILE; W: TRANFILE, DALYREJS, TCATBALF | CVTRA05Y, CVTRA06Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| CBTRN03C.cbl | 649 | Print daily transaction detail report | R: TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM; W: TRANREPT | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| COBSWAIT.cbl | 41 | Wait routine — wrapper to CALL assembler MVSWAIT | CALL: MVSWAIT | (none) |
| CSUTLDTC.cbl | 157 | Date utility — convert/validate dates using LE CEEDAYS service | CALL: CEEDAYS | CSUTLDPY, CSUTLDWY |

### Online (CICS) Programs

| Filename | LOC | Purpose | Key I/O Operations | Copybooks Referenced |
|----------|-----|---------|-------------------|---------------------|
| COSGN00C.cbl | 260 | Sign-on screen — user authentication, routes to Admin or Main menu | R: USRSEC (KSDS); CICS SEND/RECEIVE MAP | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, COSGN00, DFHAID, DFHBMSCA |
| COADM01C.cbl | 288 | Admin menu — hub for admin operations (User CRUD, Tran Type maintenance) | CICS XCTL to sub-programs | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COMEN01C.cbl | 308 | Main menu — central navigation hub with 11 user function targets | CICS XCTL to sub-programs | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COACTUPC.cbl | 4,236 | Account Update — full account field edit with exhaustive validation (date, SSN, phone, state, ZIP) | R/W: ACCTFILE, CARDXREF, CUSTFILE; CICS READ/REWRITE; SEND/RECEIVE MAP | COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVCRD01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CSSETATY, CSSTRPFY, COACTUP, DFHAID, DFHBMSCA |
| COACTVWC.cbl | 941 | Account View — display account details with linked card and customer info | R: ACCTFILE, CARDXREF, CUSTFILE; CICS READ | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| COCRDLIC.cbl | 1,459 | Credit Card List — paginated browse of cards with STARTBR/READNEXT/READPREV | R: CARDFILE (browse); CICS STARTBR/READNEXT/READPREV/ENDBR | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| COCRDSLC.cbl | 887 | Credit Card View — display single card details with customer lookup | R: CARDFILE, CUSTFILE; CICS READ | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| COCRDUPC.cbl | 1,560 | Credit Card Update — edit card fields with validation | R/W: CARDFILE, XREFFILE; CICS READ/REWRITE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVACT03Y, CSSTRPFY, CSSETATY |
| COTRN00C.cbl | 699 | Transaction List — paginated browse of transactions | R: TRANSACT (browse), CARDXREF; CICS STARTBR/READNEXT | COCOM01Y, CVTRA05Y, CVACT03Y, CVCRD01Y, COTTL01Y, COTRN00, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COTRN01C.cbl | 330 | Transaction View — display single transaction details | R: TRANSACT; CICS READ | COCOM01Y, CVTRA05Y, CVCRD01Y, COTTL01Y, COTRN01, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COTRN02C.cbl | 783 | Transaction Add — enter new transaction with validation | R: CARDXREF, ACCTFILE; W: TRANSACT; CICS READ/WRITE | COCOM01Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVCRD01Y, COTTL01Y, COTRN02, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, DFHAID, DFHBMSCA |
| CORPT00C.cbl | 649 | Report Request — submits batch JCL for report generation (INTRDRJ1/J2) | CICS START (internal reader); submits JCL | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| COBIL00C.cbl | 572 | Bill Payment — process bill payments against account | R/W: ACCTFILE, TRANSACT; CICS READ/REWRITE/WRITE | COCOM01Y, CVACT01Y, CVTRA05Y, CVCRD01Y, COTTL01Y, COBIL00, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR00C.cbl | 695 | User List — paginated browse of security user records | R: USRSEC (browse); CICS STARTBR/READNEXT/READPREV | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR01C.cbl | 299 | User Add — create new user security record | W: USRSEC; CICS WRITE | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR02C.cbl | 414 | User Update — modify existing user record | R/W: USRSEC; CICS READ/REWRITE | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| COUSR03C.cbl | 359 | User Delete — remove user security record | R/W: USRSEC; CICS DELETE | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Sub-Application Programs

### Authorization Module (`app/app-authorization-ims-db2-mq/cbl/`)

| Filename | LOC | Purpose | Classification | Key I/O | Copybooks |
|----------|-----|---------|---------------|---------|-----------|
| COPAUA0C.cbl | 1,026 | Authorization decision — combined IMS + DB2 + MQ processing | Online (CICS) | IMS DL/I (GU, SCHD, TERM); DB2 INSERT (AUTHFRDS); MQ (MQOPEN, MQGET, MQPUT1) | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, IMSFUNCS, PAUTBPCB |
| COPAUS0C.cbl | 1,032 | Authorization summary browse — paginated IMS segment listing | Online (CICS) | IMS DL/I (GU, GNP); CICS SEND/RECEIVE MAP | CIPAUSMY, IMSFUNCS, COPAU00 |
| COPAUS1C.cbl | 604 | Authorization detail with update capability | Online (CICS) | IMS DL/I (GU, GNP, REPL); CICS MAP | CIPAUDTY, IMSFUNCS, COPAU01 |
| COPAUS2C.cbl | 244 | Fraud marking — insert fraud flag via DB2 | Online (CICS) | DB2 INSERT | CCPAUERY |
| CBPAUP0C.cbl | 386 | Expired authorization purge | Batch (IMS) | IMS DL/I (GN, GNP, DLET) | CIPAUSMY, IMSFUNCS, PAUTBPCB |
| PAUDBLOD.CBL | 369 | IMS database load — bulk insert authorization records | Batch (IMS) | IMS DL/I (ISRT, GU); R: input file | PADFLPCB, PASFLPCB, PAUTBPCB |
| PAUDBUNL.CBL | 317 | IMS database unload — sequential dump of auth data | Batch (IMS) | IMS DL/I (GN, GNP); W: output file | PADFLPCB, PASFLPCB, PAUTBPCB |
| DBUNLDGS.CBL | 366 | GSAM unload — unload via GSAM sequential access method | Batch (IMS) | IMS DL/I (GN, GNP, ISRT via GSAM) | PADFLPCB, PASFLPCB |

### Transaction Type Module (`app/app-transaction-type-db2/cbl/`)

| Filename | LOC | Purpose | Classification | Key I/O | Copybooks |
|----------|-----|---------|---------------|---------|-----------|
| COTRTLIC.cbl | 2,098 | Transaction type list — DB2 cursor-based pagination with filtering | Online (CICS) | DB2 DECLARE CURSOR, OPEN, FETCH, CLOSE; CICS MAP | CSDB2RPY, CSDB2RWY, COTRTLI (BMS) |
| COTRTUPC.cbl | 1,702 | Transaction type update/delete — cascading DB2 operations | Online (CICS) | DB2 UPDATE, DELETE (cascading); CICS MAP | CSDB2RPY, CSDB2RWY, COTRTUP (BMS) |
| COBTUPDT.cbl | 237 | Batch transaction type update utility | Batch | DB2 UPDATE | CSDB2RPY |

### VSAM/MQ Module (`app/app-vsam-mq/cbl/`)

| Filename | LOC | Purpose | Classification | Key I/O | Copybooks |
|----------|-----|---------|---------------|---------|-----------|
| COACCT01.cbl | 620 | Account inquiry via MQ — request/reply pattern | Online (CICS) | MQ (MQOPEN, MQGET, MQPUT); R: ACCTFILE | COCOM01Y, CVACT01Y |
| CODATE01.cbl | 524 | Date inquiry via MQ — date validation/conversion service | Online (CICS) | MQ (MQOPEN, MQGET, MQPUT) | COCOM01Y, CSUTLDPY |

---

## 3. BMS Screen Maps (`app/bms/`)

| Map File | Associated Program | Screen Purpose |
|----------|-------------------|----------------|
| COSGN00.bms | COSGN00C | Sign-on / Login |
| COADM01.bms | COADM01C | Admin Menu |
| COMEN01.bms | COMEN01C | Main Menu |
| COACTUP.bms | COACTUPC | Account Update |
| COACTVW.bms | COACTVWC | Account View |
| COCRDLI.bms | COCRDLIC | Card List |
| COCRDSL.bms | COCRDSLC | Card View |
| COCRDUP.bms | COCRDUPC | Card Update |
| COTRN00.bms | COTRN00C | Transaction List |
| COTRN01.bms | COTRN01C | Transaction View |
| COTRN02.bms | COTRN02C | Transaction Add |
| CORPT00.bms | CORPT00C | Report Request |
| COBIL00.bms | COBIL00C | Bill Payment |
| COUSR00.bms | COUSR00C | User List |
| COUSR01.bms | COUSR01C | User Add |
| COUSR02.bms | COUSR02C | User Update |
| COUSR03.bms | COUSR03C | User Delete |

Sub-application BMS maps:
| Map File | Associated Program | Screen Purpose |
|----------|-------------------|----------------|
| COPAU00.bms | COPAUS0C | Authorization Summary |
| COPAU01.bms | COPAUS1C | Authorization Detail |
| COTRTLI.bms | COTRTLIC | Transaction Type List |
| COTRTUP.bms | COTRTUPC | Transaction Type Update |

---

## 4. JCL Job Catalog (`app/jcl/`)

### Data Setup Jobs (VSAM Cluster Definition)

| Job Name | Steps | Purpose | Datasets Managed |
|----------|-------|---------|-----------------|
| ACCTFILE.jcl | STEP05, STEP10, STEP15 | Delete/define/load Account VSAM KSDS | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS |
| CARDFILE.jcl | CLCIFIL, STEP05, STEP10, STEP15, STEP40, STEP50, STEP60, OPCIFIL | Delete/define/load Card VSAM KSDS + AIX + PATH + BLDINDEX | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS + AIX |
| CUSTFILE.jcl | CLCIFIL, STEP05, STEP10, STEP15, OPCIFIL | Delete/define/load Customer VSAM KSDS | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS |
| XREFFILE.jcl | STEP05, STEP10, STEP15, STEP40, STEP50, STEP60 | Delete/define/load Card Cross-Reference VSAM KSDS + AIX | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS + AIX |
| TRANFILE.jcl | STEP05, STEP10, STEP15, STEP40, STEP50, STEP60 | Delete/define/load Transaction Master VSAM KSDS + AIX | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS + AIX |
| TCATBALF.jcl | STEP05, STEP10, STEP15 | Delete/define/load Transaction Category Balance KSDS | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS |
| DISCGRP.jcl | STEP05, STEP10, STEP15 | Delete/define/load Disclosure Group KSDS | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS |
| TRANTYPE.jcl | STEP05, STEP10, STEP15 | Delete/define/load Transaction Type KSDS | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS |
| TRANCATG.jcl | STEP05, STEP10, STEP15 | Delete/define/load Transaction Category KSDS | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS |
| DUSRSECJ.jcl | PREDEL, STEP01, STEP02, STEP03 | Create User Security PS file from in-stream data; define/load VSAM KSDS | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS |
| DEFCUST.jcl | STEP05 (x2) | Legacy customer VSAM cluster definition | AWS.CCDA.CUSTDATA.CLUSTER |
| ESDSRRDS.jcl | PREDEL, STEP01-05 | Define ESDS + RRDS VSAM variants of User Security file | AWS.M2.CARDDEMO.USRSEC.VSAM.ESDS/RRDS |

### GDG Definition Jobs

| Job Name | Steps | Purpose | GDG Bases Defined |
|----------|-------|---------|-------------------|
| DEFGDGB.jcl | STEP05 | Define all GDG bases for batch processing | TRANSACT.BKUP, TRANSACT.DALY, TRANREPT, TCATBALF.BKUP, SYSTRAN, TRANSACT.COMBINED |
| DEFGDGD.jcl | STEP10-60 | Define GDGs for DB2 reference data + load first generation | TRANTYPE.BKUP, TRANCATG.PS.BKUP, DISCGRP.BKUP |
| DALYREJS.jcl | STEP05 | Define GDG base for daily rejected transactions | AWS.M2.CARDDEMO.DALYREJS |
| REPTFILE.jcl | STEP05 | Define GDG base for transaction reports | AWS.M2.CARDDEMO.TRANREPT |

### Batch Processing Jobs

| Job Name | Steps | Program Executed | Purpose |
|----------|-------|-----------------|---------|
| POSTTRAN.jcl | STEP15 | CBTRN02C | Post daily transactions, update account balances, create rejects |
| INTCALC.jcl | STEP15 | CBACT04C | Calculate interest/fees on transaction category balances |
| CREASTMT.JCL | STEP15 | CBSTM03A | Generate customer statements (text + HTML) |
| TRANREPT.jcl | (STEP) | CBTRN03C | Generate daily transaction detail report |
| CBEXPORT.jcl | STEP01, STEP02 | CBEXPORT | Define export cluster + run export program |
| CBIMPORT.jcl | STEP01 | CBIMPORT | Import from export file into normalized outputs |
| READACCT.jcl | PREDEL, STEP05 | CBACT01C | Read account VSAM file and produce output files |
| READCARD.jcl | STEP05 | CBACT02C | Read and print card data |
| READCUST.jcl | STEP05 | CBCUS01C | Read and print customer data |
| READXREF.jcl | STEP05 | CBACT03C | Read and print cross-reference data |

### Transaction/File Maintenance Jobs

| Job Name | Steps | Purpose |
|----------|-------|---------|
| TRANBKP.jcl | STEP05R, STEP05, STEP10 | Backup transaction master (REPRO to GDG), then delete/redefine VSAM |
| COMBTRAN.jcl | STEP05R, STEP10 | Sort and combine transaction backup + system transactions; load to master |
| TRANIDX.jcl | (steps) | Rebuild transaction VSAM alternate indexes |
| PRTCATBL.jcl | DELDEF, STEP05R, STEP10R | Unload + sort + format transaction category balance report |

### CICS File Operations

| Job Name | Steps | Purpose |
|----------|-------|---------|
| CLOSEFIL.jcl | CLCIFIL | Close CICS files (TRANSACT, CCXREF, ACCTDAT, CXACAIX, USRSEC) |
| OPENFIL.jcl | OPCIFIL | Open CICS files |

### CICS Resource Definition

| Job Name | Steps | Purpose |
|----------|-------|---------|
| CBADMCDJ.jcl | STEP1 (DFHCSDUP) | Define all CICS resources — programs, mapsets, transactions for CardDemo |

### Utility/Misc Jobs

| Job Name | Steps | Purpose |
|----------|-------|---------|
| WAITSTEP.jcl | — | Wait step utility (uses COBSWAIT) |
| FTPJCL.JCL | — | FTP data transfer utility |
| TXT2PDF1.JCL | — | Text to PDF conversion |
| INTRDRJ1.JCL | — | Internal reader job 1 (submitted by CORPT00C for batch reports) |
| INTRDRJ2.JCL | — | Internal reader job 2 (submitted by CORPT00C for batch reports) |

### Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`)

| Job Name | Steps | Purpose |
|----------|-------|---------|
| CBPAUP0J.jcl | — | Execute expired authorization purge (CBPAUP0C) |
| DBPAUTP0.jcl | — | IMS DBD generation for PAUT database |
| LOADPADB.JCL | — | Load pending authorization IMS database (PAUDBLOD) |
| UNLDPADB.JCL | — | Unload pending authorization IMS database (PAUDBUNL) |
| UNLDGSAM.JCL | — | GSAM unload utility (DBUNLDGS) |

### Sub-Application JCL (`app/app-transaction-type-db2/jcl/`)

| Job Name | Steps | Purpose |
|----------|-------|---------|
| CREADB21.jcl | — | Create DB2 tables (TRNTYPE, TRNTYCAT) |
| MNTTRDB2.jcl | — | Maintain transaction type DB2 data |
| TRANEXTR.jcl | — | Extract transaction type data from DB2 |

---

## 5. Classification Summary

| Classification | Programs | % of Estate |
|---------------|----------|-------------|
| Online (CICS) | 21 | 48% |
| Pure Batch | 16 | 36% |
| Sub-app IMS/DB2/MQ | 7 | 16% |

---

*Generated: 2026-06-19 | Source: infosys-training/uc-legacy-modernization-cobol-to-java*
