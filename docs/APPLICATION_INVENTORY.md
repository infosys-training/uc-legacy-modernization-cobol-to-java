# Application Inventory — CardDemo COBOL Estate

> Generated from static analysis of `infosys-training/uc-legacy-modernization-cobol-to-java`

---

## 1. Online CICS Programs (`app/cbl/`)

| # | Filename | Program-ID | Purpose / Function | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|-------------------|----------------|---------------------|----------------------|
| 1 | `COSGN00C.cbl` | COSGN00C | Sign-on screen for the CardDemo application | Security / Authentication | EXEC CICS READ (USRSEC), EXEC CICS XCTL | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 2 | `COMEN01C.cbl` | COMEN01C | Main menu for regular users — dispatches to sub-programs | Navigation / Menu | EXEC CICS XCTL (dispatches to selected program) | COCOM01Y, COMEN01, COMEN02Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 3 | `COADM01C.cbl` | COADM01C | Admin menu for administrative users | Navigation / Menu | EXEC CICS XCTL (dispatches to admin sub-programs) | COADM01, COADM02Y, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 4 | `COACTVWC.cbl` | COACTVWC | View account details — read-only display | Account Management | EXEC CICS READ (ACCTDAT, CARDDAT via AIX, CUSTDAT) | CSSTRPFY, COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 5 | `COACTUPC.cbl` | COACTUPC | Update account details — full edit with validation | Account Management | EXEC CICS READ/REWRITE (ACCTDAT, CARDDAT, CUSTDAT), EXEC CICS XCTL | CSSTRPFY, CSUTLDWY, COACTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CSUTLDPY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 6 | `COCRDLIC.cbl` | COCRDLIC | List credit cards — browse with pagination | Card Management | EXEC CICS READ/STARTBR/READNEXT (CARDDAT, CARDAIX) | CSSTRPFY, COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 7 | `COCRDSLC.cbl` | COCRDSLC | View credit card details — read-only display | Card Management | EXEC CICS READ (CARDDAT, CARDAIX) | CSSTRPFY, COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 8 | `COCRDUPC.cbl` | COCRDUPC | Update credit card details — edit with validation | Card Management | EXEC CICS READ/REWRITE (CARDDAT) | CSSTRPFY, COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 9 | `COTRN00C.cbl` | COTRN00C | List transactions from TRANSACT file with browse | Transaction Management | EXEC CICS READ/STARTBR/READNEXT (TRANSACT) | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 10 | `COTRN01C.cbl` | COTRN01C | View a single transaction — read-only display | Transaction Management | EXEC CICS READ (TRANSACT) | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 11 | `COTRN02C.cbl` | COTRN02C | Add a new transaction to TRANSACT file | Transaction Management | EXEC CICS READ/WRITE (TRANSACT, ACCTDAT, XREFDAT), CALL CSUTLDTC | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 12 | `COBIL00C.cbl` | COBIL00C | Bill payment — pay account balance in full or partial | Financial Processing | EXEC CICS READ/REWRITE (ACCTDAT, XREFDAT, TRANSACT) | COBIL00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 13 | `CORPT00C.cbl` | CORPT00C | Submit batch report jobs for transaction reporting | Reporting | CALL CSUTLDTC (date validation), EXEC CICS START (batch JCL) | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 14 | `COUSR00C.cbl` | COUSR00C | List all users from USRSEC file | User / Security | EXEC CICS READ/STARTBR/READNEXT (USRSEC) | COCOM01Y, COTTL01Y, COUSR00, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 15 | `COUSR01C.cbl` | COUSR01C | Add a new regular or admin user to USRSEC file | User / Security | EXEC CICS WRITE (USRSEC) | COCOM01Y, COTTL01Y, COUSR01, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | `COUSR02C.cbl` | COUSR02C | Update an existing user in USRSEC file | User / Security | EXEC CICS READ/REWRITE (USRSEC) | COCOM01Y, COTTL01Y, COUSR02, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 17 | `COUSR03C.cbl` | COUSR03C | Delete a user from USRSEC file | User / Security | EXEC CICS DELETE (USRSEC) | COCOM01Y, COTTL01Y, COUSR03, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |

---

## 2. Batch Programs (`app/cbl/`)

| # | Filename | Program-ID | Purpose / Function | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|------------|-------------------|----------------|---------------------|----------------------|
| 1 | `CBACT01C.cbl` | CBACT01C | Read account file and write to output files | Account Processing | OPEN/READ ACCTFILE, WRITE OUT-ACCT-REC, CALL COBDATFT (date formatting) | CODATECN, CVACT01Y |
| 2 | `CBACT02C.cbl` | CBACT02C | Read and print card data file | Card Reporting | OPEN/READ CARDFILE, DISPLAY (print) | CVACT02Y |
| 3 | `CBACT03C.cbl` | CBACT03C | Read and print account cross-reference data file | Cross-Reference Reporting | OPEN/READ XREFFILE, DISPLAY (print) | CVACT03Y |
| 4 | `CBACT04C.cbl` | CBACT04C | Interest calculator — compute and apply interest charges | Financial Processing | OPEN TCATBAL, XREF, DISCGRP (INPUT), ACCOUNT (I-O), READ/REWRITE | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA02Y, CVTRA05Y |
| 5 | `CBCUS01C.cbl` | CBCUS01C | Read and print customer data file | Customer Reporting | OPEN/READ CUSTFILE, DISPLAY (print) | CVCUS01Y |
| 6 | `CBTRN01C.cbl` | CBTRN01C | Post daily transaction file records to master | Transaction Posting | READ DALYTRAN, READ XREF/ACCOUNT, WRITE/REWRITE TRANSACT | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVTRA05Y, CVTRA06Y |
| 7 | `CBTRN02C.cbl` | CBTRN02C | Post daily transactions — categorize and reject invalid | Transaction Posting | OPEN DALYTRAN (INPUT), TRANSACT/DALYREJS (OUTPUT), READ XREF | CVACT01Y, CVACT03Y, CVTRA01Y, CVTRA05Y, CVTRA06Y |
| 8 | `CBTRN03C.cbl` | CBTRN03C | Print transaction detail report with totals | Transaction Reporting | READ DATE-PARMS, READ TRANSACT, WRITE REPTFILE | CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA05Y, CVTRA07Y |
| 9 | `CBSTM03A.CBL` | CBSTM03A | Print account statements from transaction data | Statement Generation | CALL CBSTM03B (file I/O subroutine), READ/WRITE via subroutine | COSTM01, CUSTREC, CVACT01Y, CVACT03Y |
| 10 | `CBSTM03B.CBL` | CBSTM03B | File processing subroutine for statement report | Statement Support Module | OPEN/CLOSE/READ/WRITE/REWRITE (delegated I/O operations) | _(none — inline definitions)_ |
| 11 | `CBEXPORT.cbl` | CBEXPORT | Export customer data for branch migration | Data Migration | OPEN CUSTOMER/ACCOUNT/CARD/XREF/TRAN (INPUT), WRITE EXPORT-OUTPUT | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 12 | `CBIMPORT.cbl` | CBIMPORT | Import customer data from branch migration export | Data Migration | OPEN EXPORT-INPUT (INPUT), WRITE CUSTOMER/ACCOUNT/CARD/XREF/TRAN (OUTPUT) | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVEXPORT, CVTRA05Y |
| 13 | `COBSWAIT.cbl` | COBSWAIT | Utility — wait for specified centiseconds | Utility | CALL MVSWAIT | _(none)_ |
| 14 | `CSUTLDTC.cbl` | CSUTLDTC | Date utility — convert dates via CEEDAYS LE callable | Utility | CALL CEEDAYS (Language Environment) | _(none)_ |

---

## 3. Sub-Application Programs

### 3.1 Authorization / IMS-DB2-MQ (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Program-ID | Purpose / Function | Classification | Key Dependencies |
|---|----------|------------|-------------------|----------------|------------------|
| 1 | `COPAUS0C.cbl` | COPAUS0C | Summary view of pending authorization messages | Online / CICS | IMS DB, DB2, MQ |
| 2 | `COPAUS1C.cbl` | COPAUS1C | Detail view of a single authorization message | Online / CICS | IMS DB, DB2 |
| 3 | `COPAUS2C.cbl` | COPAUS2C | Mark authorization message as fraud | Online / CICS | IMS DB, DB2, MQ |
| 4 | `COPAUA0C.cbl` | COPAUA0C | Card authorization decision program | Online / CICS | IMS DB, DB2, MQ |
| 5 | `CBPAUP0C.cbl` | CBPAUP0C | Delete expired pending authorization messages | Batch | IMS DB |
| 6 | `DBUNLDGS.CBL` | DBUNLDGS | Database unload utility (general segments) | Utility | IMS DB |
| 7 | `PAUDBLOD.CBL` | PAUDBLOD | Load authorization data into IMS database | Utility | IMS DB |
| 8 | `PAUDBUNL.CBL` | PAUDBUNL | Unload authorization data from IMS database | Utility | IMS DB |

### 3.2 Transaction Type / DB2 (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Program-ID | Purpose / Function | Classification | Key Dependencies |
|---|----------|------------|-------------------|----------------|------------------|
| 1 | `COTRTLIC.cbl` | COTRTLIC | List transaction types for updates and deletes | Online / CICS | DB2 |
| 2 | `COTRTUPC.cbl` | COTRTUPC | Accept and process transaction type updates | Online / CICS | DB2 |
| 3 | `COBTUPDT.cbl` | COBTUPDT | Batch update of transaction types from user input | Batch | DB2 |

### 3.3 VSAM-MQ (`app/app-vsam-mq/cbl/`)

| # | Filename | Program-ID | Purpose / Function | Classification | Key Dependencies |
|---|----------|------------|-------------------|----------------|------------------|
| 1 | `COACCT01.cbl` | COACCT01 | Account processing with MQ integration | Online / CICS | VSAM, MQ |
| 2 | `CODATE01.cbl` | CODATE01 | Date processing utility with MQ integration | Utility | VSAM, MQ |

---

## 4. JCL Job Catalog (`app/jcl/`)

| # | JCL Member | Purpose | Cycle | Programs Invoked | Key Datasets |
|---|-----------|---------|-------|------------------|--------------|
| 1 | `ACCTFILE.jcl` | Define/load VSAM KSDS for account data | Setup | IDCAMS, CBACT01C | ACCTDATA (KSDS) |
| 2 | `CARDFILE.jcl` | Define/load VSAM KSDS for card data | Setup | IDCAMS, CBACT02C | CARDDATA (KSDS) |
| 3 | `CUSTFILE.jcl` | Define/load VSAM KSDS for customer data | Setup | IDCAMS, CBCUS01C | CUSTDATA (KSDS) |
| 4 | `XREFFILE.jcl` | Define/load VSAM KSDS for card-account cross-reference | Setup | IDCAMS, CBACT03C | XREFDATA (KSDS) |
| 5 | `TRANFILE.jcl` | Define/load VSAM KSDS for transaction data | Setup | IDCAMS | TRANSACT (KSDS) |
| 6 | `TRANIDX.jcl` | Define alternate index on transaction file | Setup | IDCAMS | TRANSACT AIX |
| 7 | `CLOSEFIL.jcl` | Close VSAM files for batch processing | Daily | IDCAMS (CLOSE) | All VSAM clusters |
| 8 | `OPENFIL.jcl` | Re-open VSAM files after batch window | Daily | IDCAMS (OPEN) | All VSAM clusters |
| 9 | `WAITSTEP.jcl` | Wait/delay step between batch operations | Daily | COBSWAIT | N/A |
| 10 | `TRANBKP.jcl` | Backup daily transaction file | Daily | IDCAMS REPRO | TRANSACT → GDG backup |
| 11 | `POSTTRAN.jcl` | Post daily transactions to master file | Daily | CBTRN01C, CBTRN02C | DALYTRAN, TRANSACT |
| 12 | `DALYREJS.jcl` | Process daily rejected transactions | Daily | CBTRN02C | DALYREJS output |
| 13 | `COMBTRAN.jcl` | Combine transaction records (monthly roll-up) | Monthly | Utility | TRANSACT, archive |
| 14 | `INTCALC.jcl` | Calculate and apply monthly interest charges | Monthly | CBACT04C | ACCTDATA, TCATBAL, DISCGRP |
| 15 | `CREASTMT.JCL` | Create account statements | Monthly | CBSTM03A | TRANSACT, ACCTDATA, CUSTDATA |
| 16 | `TRANREPT.jcl` | Generate transaction detail report | On-Demand | CBTRN03C | TRANSACT, REPTFILE |
| 17 | `TRANCATG.jcl` | Maintain transaction category data | Maintenance | Utility | TCATBAL (KSDS) |
| 18 | `TCATBALF.jcl` | Define transaction category balance file | Setup | IDCAMS | TCATBAL (KSDS) |
| 19 | `DISCGRP.jcl` | Load disclosure group reference data | Weekly | Utility | DISCGRP (KSDS) |
| 20 | `TRANTYPE.jcl` | Load transaction type reference data | Weekly | Utility | TRANTYPE (KSDS) |
| 21 | `REPTFILE.jcl` | Define report output file | Setup | IDCAMS | REPTFILE |
| 22 | `DEFCUST.jcl` | Define customer VSAM cluster | Setup | IDCAMS | CUSTDATA |
| 23 | `DEFGDGB.jcl` | Define GDG base for backups | Setup | IDCAMS | GDG base |
| 24 | `DEFGDGD.jcl` | Define GDG base for daily data | Setup | IDCAMS | GDG base |
| 25 | `READACCT.jcl` | Read and display account records | Diagnostic | CBACT01C | ACCTDATA |
| 26 | `READCARD.jcl` | Read and display card records | Diagnostic | CBACT02C | CARDDATA |
| 27 | `READCUST.jcl` | Read and display customer records | Diagnostic | CBCUS01C | CUSTDATA |
| 28 | `READXREF.jcl` | Read and display cross-reference records | Diagnostic | CBACT03C | XREFDATA |
| 29 | `PRTCATBL.jcl` | Print transaction category balance data | Diagnostic | Utility | TCATBAL |
| 30 | `ESDSRRDS.jcl` | Define ESDS/RRDS dataset types | Setup | IDCAMS | ESDS/RRDS clusters |
| 31 | `CBADMCDJ.jcl` | Admin card demo utility job | Maintenance | Various | Multiple datasets |
| 32 | `CBEXPORT.jcl` | Export customer data for branch migration | Migration | CBEXPORT | All VSAM → EXPORT file |
| 33 | `CBIMPORT.jcl` | Import customer data from branch migration | Migration | CBIMPORT | EXPORT file → All VSAM |
| 34 | `DUSRSECJ.jcl` | Define user security VSAM file | Setup | IDCAMS | USRSEC (KSDS) |
| 35 | `FTPJCL.JCL` | FTP file transfer job | Utility | FTP | Various |
| 36 | `INTRDRJ1.JCL` | Internal reader job submission (1) | Utility | INTRDR | Various |
| 37 | `INTRDRJ2.JCL` | Internal reader job submission (2) | Utility | INTRDR | Various |
| 38 | `TXT2PDF1.JCL` | Convert text reports to PDF format | Utility | TXT2PDF | Report files |

---

## 5. Summary Statistics

| Category | Count |
|----------|-------|
| Online CICS Programs (core) | 17 |
| Batch Programs (core) | 14 |
| Sub-Application Programs | 13 |
| JCL Job Members | 38 |
| Copybooks (`app/cpy/`) | 31 |
| BMS Map Definitions (`app/bms/`) | Multiple |
| **Total COBOL Source Files** | **44** |
