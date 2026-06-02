# APPLICATION INVENTORY — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System (Mainframe)  
> **Total Programs:** 44 COBOL | **Total LOC:** 30,175 | **Copybooks:** 47 | **JCL Jobs:** 46  
> **Generated:** 2026-06-02

---

## 1. Main Programs (`app/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|---------------|-------------------|---------------------|
| 1 | CBACT01C.cbl | Read account VSAM file and write to multiple output formats (flat, array, variable-length) | Batch | R: ACCTFILE (KSDS); W: OUT-FILE, ARRY-FILE, VBRC-FILE | CVACT01Y, CODATECN |
| 2 | CBACT02C.cbl | Read and print card data file | Batch | R: CARDFILE (KSDS) | CVACT02Y |
| 3 | CBACT03C.cbl | Read and print account cross-reference data file | Batch | R: XREFFILE (KSDS) | CVACT03Y |
| 4 | CBACT04C.cbl | Interest calculator — compute interest on account balances using discount groups and category balances | Batch | R: TCATBALF, XREFFILE, DISCGRP; R/W: ACCOUNT; W: TRANSACT | CVTRA01Y, CVACT03Y, CVTRA02Y, CVACT01Y, CVTRA05Y |
| 5 | CBCUS01C.cbl | Read and print customer data file | Batch | R: CUSTFILE (KSDS) | CVCUS01Y |
| 6 | CBEXPORT.cbl | Export customer data for branch migration — reads normalized files and creates multi-record export file | Batch | R: CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD; W: EXPORT-OUTPUT | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 7 | CBIMPORT.cbl | Import customer data from branch migration export — splits multi-record file into normalized targets with validation | Batch | R: EXPORT-INPUT; W: CUSTOMER, ACCOUNT, XREF, TRANSACTION, CARD, ERROR | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |
| 8 | CBSTM03A.CBL | Generate customer statements — text and HTML output from transaction history | Batch | R: XREF, CUST, ACCT, TRNX; W: STMT-FILE, HTML-FILE | CVACT01Y, CVACT03Y, CVCUS01Y, CVTRA05Y, COSTM01 |
| 9 | CBSTM03B.CBL | Statement generation I/O submodule — called by CBSTM03A for file operations | Batch (Module) | R/W: Multiple files as delegated by CBSTM03A | COSTM01 |
| 10 | CBTRN01C.cbl | Validate daily transactions against master files (pre-posting validation) | Batch | R: DALYTRAN, XREF, CARD, ACCOUNT, CUSTOMER, TRANSACT | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 11 | CBTRN02C.cbl | Post records from daily transaction file to master — update balances, write rejects | Batch | R: DALYTRAN, XREF; R/W: ACCOUNT, TCATBAL; W: TRANSACT, DALYREJS | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 12 | CBTRN03C.cbl | Print daily transaction detail report with type/category descriptions | Batch | R: TRANSACT, XREF, TRANTYPE, TRANCATG, DATE-PARMS; W: REPTFILE | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 13 | COACTUPC.cbl | Accept and process account update — exhaustive field validation (date, SSN, phone, state, ZIP) | Online (CICS) | CICS READ/REWRITE: ACCTFILE, CARDXREF, CUSTFILE | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×3) |
| 14 | COACTVWC.cbl | Accept and process account view request — read-only display of account, card, customer data | Online (CICS) | CICS READ: ACCTFILE, CARDXREF, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 15 | COADM01C.cbl | Admin menu for administrative users — routes to user CRUD and DB2 transaction type screens | Online (CICS) | CICS SEND/RECEIVE MAP | COCOM01Y, COADM02Y, COADM01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 16 | COBIL00C.cbl | Bill payment — pay account balance in full, create transaction record | Online (CICS) | CICS READ/REWRITE: ACCTFILE; CICS STARTBR/READPREV/ENDBR: TRANSACT; CICS WRITE: TRANSACT | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 17 | COBSWAIT.cbl | Utility program to wait (parameter in centiseconds) — calls assembler MVSWAIT | Batch (Utility) | None | (none) |
| 18 | COCRDLIC.cbl | List credit cards — paginated browse with STARTBR/READNEXT/ENDBR | Online (CICS) | CICS STARTBR/READNEXT/READPREV/ENDBR: CARDFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY |
| 19 | COCRDSLC.cbl | Accept and process credit card detail request — read-only view | Online (CICS) | CICS READ: CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 20 | COCRDUPC.cbl | Accept and process credit card update — validate and rewrite card record | Online (CICS) | CICS READ/REWRITE: CARDFILE, CUSTFILE | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 21 | COMEN01C.cbl | Main menu hub — central navigation for regular users (11 XCTL targets) | Online (CICS) | CICS SEND/RECEIVE MAP | COCOM01Y, COMEN02Y, COMEN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 22 | CORPT00C.cbl | Report request screen — submits batch JCL (INTRDRJ1/J2) for report generation | Online (CICS) | CICS SEND/RECEIVE MAP; submits JCL via internal reader | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 23 | COSGN00C.cbl | Sign-on entry point — authenticate user credentials, route to admin or user menu | Online (CICS) | CICS READ: USRSEC file | COCOM01Y, COSGN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 24 | COTRN00C.cbl | Transaction list — paginated browse of transactions by account | Online (CICS) | CICS STARTBR/READNEXT/ENDBR: TRANSACT | COCOM01Y, COTRN00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 25 | COTRN01C.cbl | Transaction detail view — display single transaction record | Online (CICS) | CICS READ: TRANSACT | COCOM01Y, COTRN01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 26 | COTRN02C.cbl | Transaction add — create new transaction record online | Online (CICS) | CICS READ: ACCTFILE, XREF; CICS WRITE: TRANSACT | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 27 | COUSR00C.cbl | User list — paginated browse of security users | Online (CICS) | CICS STARTBR/READNEXT/ENDBR: USRSEC | COCOM01Y, COUSR00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 28 | COUSR01C.cbl | User add — create new security user record | Online (CICS) | CICS WRITE: USRSEC | COCOM01Y, COUSR01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 29 | COUSR02C.cbl | User update — modify existing security user record | Online (CICS) | CICS READ/REWRITE: USRSEC | COCOM01Y, COUSR02, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 30 | COUSR03C.cbl | User delete — remove security user record | Online (CICS) | CICS READ/DELETE: USRSEC | COCOM01Y, COUSR03, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, DFHAID, DFHBMSCA |
| 31 | CSUTLDTC.cbl | Date utility — convert and validate dates using LE CEEDAYS | Batch (Utility) | None (called subroutine) | CSUTLDPY |

---

## 2. Sub-Application Programs

### 2.1 Authorization Module (`app/app-authorization-ims-db2-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|---------------|-------------------|---------------------|
| 32 | CBPAUP0C.cbl | Delete expired pending authorization messages from IMS database | Batch (IMS) | DLI GN, GNP, DLET on PAUT-PCB | CIPAUSMY, CIPAUDTY |
| 33 | COPAUA0C.cbl | Card authorization decision — read MQ request, check IMS/VSAM, write MQ reply | Online (CICS/IMS/MQ) | MQOPEN, MQGET, MQPUT1, MQCLOSE; DLI SCHD, GU, TERM; CICS READ: XREF, ACCT, CUST | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 34 | COPAUS0C.cbl | Summary view of authorization messages — IMS browse with BMS screen | Online (CICS/IMS) | DLI GU, GNP, SCHD, TERM; CICS READ: ACCT, CARD, CUST; CICS SEND/RECEIVE MAP | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 35 | COPAUS1C.cbl | Detail view of authorization message with update capability | Online (CICS/IMS) | DLI GU, GNP, REPL, SCHD, TERM; CICS SEND/RECEIVE MAP; CICS LINK | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 36 | COPAUS2C.cbl | Mark authorization message as fraud — insert fraud record into DB2 | Online (CICS/IMS/DB2) | EXEC SQL INSERT/SELECT; CICS ASKTIME/FORMATTIME | CIPAUDTY |
| 37 | DBUNLDGS.CBL | GSAM unload utility — unload IMS segments to sequential file via GSAM | Batch (IMS) | DLI GN (CBLTDLI), GNP, ISRT (GSAM) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB, PASFLPCB, PADFLPCB |
| 38 | PAUDBLOD.CBL | IMS database load — load pending authorization data from flat files into IMS | Batch (IMS) | R: INFILE1, INFILE2; DLI ISRT, GU (CBLTDLI) | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |
| 39 | PAUDBUNL.CBL | IMS database unload — extract all IMS segments to flat files | Batch (IMS) | DLI GN, GNP (CBLTDLI); W: OPFILE1, OPFILE2 | IMSFUNCS, CIPAUSMY, CIPAUDTY, PAUTBPCB |

### 2.2 Transaction Type Module (`app/app-transaction-type-db2/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|---------------|-------------------|---------------------|
| 40 | COBTUPDT.cbl | Batch update of transaction types from flat file into DB2 table | Batch (DB2) | R: TR-RECORD; EXEC SQL INSERT/UPDATE/DELETE on TRANSACTION_TYPE | DCLTRTYP (SQL INCLUDE) |
| 41 | COTRTLIC.cbl | List transaction types for update/delete — DB2 cursor pagination with BMS screen | Online (CICS/DB2) | EXEC SQL DECLARE/OPEN/FETCH/CLOSE cursor on TRANSACTION_TYPE; CICS SEND/RECEIVE MAP | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, DCLTRTYP |
| 42 | COTRTUPC.cbl | Accept and process transaction type update — DB2 update/delete with cascade | Online (CICS/DB2) | EXEC SQL UPDATE/DELETE on TRANSACTION_TYPE, TRANSACTION_CATEGORY; CICS SEND/RECEIVE MAP | CSUTLDWY, CVCRD01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT |

### 2.3 VSAM/MQ Module (`app/app-vsam-mq/cbl/`)

| # | Filename | Purpose | Classification | Key I/O Operations | Copybooks Referenced |
|---|----------|---------|---------------|-------------------|---------------------|
| 43 | COACCT01.cbl | Account inquiry via MQ — receive request from queue, read VSAM, send response | Online (CICS/MQ) | MQOPEN (×3), MQGET, MQPUT (×2), MQCLOSE (×3); CICS RETRIEVE | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML, CVACT01Y |
| 44 | CODATE01.cbl | Date inquiry via MQ — receive date request from queue, process, send response | Online (CICS/MQ) | MQOPEN (×3), MQGET, MQPUT (×2), MQCLOSE (×3); CICS RETRIEVE, ASKTIME | CMQGMOV, CMQPMOV, CMQMDV, CMQODV, CMQV, CMQTML |

---

## 3. Program Classification Summary

| Classification | Count | % | Programs |
|---------------|-------|---|----------|
| Online (CICS) | 16 | 36% | COACTUPC, COACTVWC, COADM01C, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, COMEN01C, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C–03C |
| Online (CICS/IMS) | 2 | 5% | COPAUS0C, COPAUS1C |
| Online (CICS/IMS/MQ) | 1 | 2% | COPAUA0C |
| Online (CICS/IMS/DB2) | 1 | 2% | COPAUS2C |
| Online (CICS/DB2) | 2 | 5% | COTRTLIC, COTRTUPC |
| Online (CICS/MQ) | 2 | 5% | COACCT01, CODATE01 |
| Batch | 13 | 30% | CBACT01C–04C, CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C–03C, CBSTM03A/B, COBSWAIT |
| Batch (IMS) | 4 | 9% | CBPAUP0C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
| Batch (DB2) | 1 | 2% | COBTUPDT |
| Batch (Utility) | 2 | 5% | COBSWAIT, CSUTLDTC |

---

## 4. JCL Job Catalog (`app/jcl/`)

### 4.1 Data Setup Jobs (VSAM Cluster Definition)

| Job | Steps | Purpose | Datasets |
|-----|-------|---------|----------|
| ACCTFILE.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Account VSAM KSDS | AWS.M2.CARDDEMO.ACCTDATA.PS → .VSAM.KSDS |
| CARDFILE.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Card VSAM KSDS | AWS.M2.CARDDEMO.CARDDATA.PS → .VSAM.KSDS |
| CUSTFILE.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Customer VSAM KSDS | AWS.M2.CARDDEMO.CUSTDATA.PS → .VSAM.KSDS |
| XREFFILE.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Card-XREF VSAM KSDS | AWS.M2.CARDDEMO.CARDXREF.PS → .VSAM.KSDS |
| DISCGRP.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Discount Group VSAM | AWS.M2.CARDDEMO.DISCGRP.PS → .VSAM.KSDS |
| TCATBALF.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Transaction Category Balance VSAM | AWS.M2.CARDDEMO.TCATBALF.PS → .VSAM.KSDS |
| TRANTYPE.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Transaction Type VSAM | AWS.M2.CARDDEMO.TRANTYPE.PS → .VSAM.KSDS |
| TRANCATG.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Transaction Category VSAM | AWS.M2.CARDDEMO.TRANCATG.PS → .VSAM.KSDS |
| TRANFILE.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load Transaction Master VSAM | AWS.M2.CARDDEMO.TRANSACT.PS → .VSAM.KSDS |
| TRANIDX.jcl | STEP05→STEP10 | Define alternate index on Transaction file | AWS.M2.CARDDEMO.TRANSACT.AIX |
| DEFCUST.jcl | STEP05→STEP10 | Define Customer alternate index | AWS.M2.CARDDEMO.CUSTDATA.AIX |
| DEFGDGB.jcl | STEP10 | Define GDG base for daily transaction backup | AWS.M2.CARDDEMO.DALYTRAN.GDG |
| DEFGDGD.jcl | STEP05→STEP10 | Delete/Define GDG for daily rejects | AWS.M2.CARDDEMO.DALYREJS.GDG |
| ESDSRRDS.jcl | STEP05→STEP10→STEP15 | Define ESDS/RRDS clusters (reporting) | AWS.M2.CARDDEMO.REPTFILE |
| DUSRSECJ.jcl | STEP05→STEP10→STEP15 | Delete/Define/Load User Security VSAM | AWS.M2.CARDDEMO.USRSEC.PS → .VSAM.KSDS |

### 4.2 Batch Processing Jobs

| Job | Steps | Program Executed | Purpose |
|-----|-------|-----------------|---------|
| POSTTRAN.jcl | STEP01 | CBTRN02C | Post daily transactions to master file |
| INTCALC.jcl | STEP01 | CBACT04C | Calculate interest on account balances |
| CREASTMT.JCL | STEP01 | CBSTM03A | Generate customer statements (text + HTML) |
| TRANREPT.jcl | STEP01 | CBTRN03C | Print daily transaction detail report |
| CBEXPORT.jcl | STEP01 | CBEXPORT | Export data for branch migration |
| CBIMPORT.jcl | STEP01 | CBIMPORT | Import data from branch migration |
| DALYREJS.jcl | STEP01 | IDCAMS | Archive daily rejection file (GDG roll) |
| COMBTRAN.jcl | STEP01→STEP02 | SORT→IDCAMS | Sort and combine daily transactions |
| TRANBKP.jcl | STEP01 | IDCAMS | Backup transaction file to GDG |
| PRTCATBL.jcl | STEP01 | IDCAMS | Print category balance file |
| WAITSTEP.jcl | STEP01 | COBSWAIT | Wait step utility (inter-job delay) |
| TXT2PDF1.JCL | STEP01 | IEBGENER | Convert text report to PDF-ready format |

### 4.3 File I/O Utility Jobs

| Job | Steps | Purpose |
|-----|-------|---------|
| READACCT.jcl | STEP01 | Read/verify Account VSAM (CBACT01C) |
| READCARD.jcl | STEP01 | Read/verify Card VSAM (CBACT02C) |
| READCUST.jcl | STEP01 | Read/verify Customer VSAM (CBCUS01C) |
| READXREF.jcl | STEP01 | Read/verify XREF VSAM (CBACT03C) |
| REPTFILE.jcl | STEP05→STEP10→STEP15 | Define/Load Report output file |
| CLOSEFIL.jcl | STEP01 | Close CICS files for batch window |
| OPENFIL.jcl | STEP01 | Re-open CICS files after batch |

### 4.4 Administrative/Control Jobs

| Job | Steps | Purpose |
|-----|-------|---------|
| CBADMCDJ.jcl | STEP01 | Compile CardDemo admin programs |
| FTPJCL.JCL | STEP01 | FTP transfer JCL to mainframe |
| INTRDRJ1.JCL | STEP01 | Internal reader — submit report job 1 |
| INTRDRJ2.JCL | STEP01 | Internal reader — submit report job 2 |

### 4.5 Sub-Application JCL (`app/app-authorization-ims-db2-mq/jcl/`)

| Job | Steps | Purpose |
|-----|-------|---------|
| CBPAUP0J.jcl | STEP01 | Execute CBPAUP0C — purge expired authorizations |
| DBPAUTP0.jcl | STEP01→STEP02 | Define and load IMS PAUT database |
| LOADPADB.JCL | STEP01 | Load pending auth data (PAUDBLOD) |
| UNLDPADB.JCL | STEP01 | Unload pending auth data (PAUDBUNL) |
| UNLDGSAM.JCL | STEP01 | GSAM unload (DBUNLDGS) |

### 4.6 Sub-Application JCL (`app/app-transaction-type-db2/jcl/`)

| Job | Steps | Purpose |
|-----|-------|---------|
| CREADB21.jcl | STEP01→STEP02 | Create DB2 tables (TRNTYPE, TRNTYCAT) and load initial data |
| MNTTRDB2.jcl | STEP01 | Maintain transaction types in DB2 (COBTUPDT) |
| TRANEXTR.jcl | STEP01 | Extract transaction type data from DB2 |

---

## 5. Supporting Assets

### 5.1 BMS Maps (`app/bms/`) — 16 screen definitions

| Map | Associated Program | Screen Function |
|-----|--------------------|-----------------|
| COACTUP.bms | COACTUPC | Account Update screen |
| COACTVW.bms | COACTVWC | Account View screen |
| COADM01.bms | COADM01C | Admin Menu screen |
| COBIL00.bms | COBIL00C | Bill Payment screen |
| COCRDLI.bms | COCRDLIC | Card List screen |
| COCRDSL.bms | COCRDSLC | Card Detail screen |
| COCRDUP.bms | COCRDUPC | Card Update screen |
| COMEN01.bms | COMEN01C | Main Menu screen |
| CORPT00.bms | CORPT00C | Report Request screen |
| COSGN00.bms | COSGN00C | Sign-On screen |
| COTRN00.bms | COTRN00C | Transaction List screen |
| COTRN01.bms | COTRN01C | Transaction Detail screen |
| COTRN02.bms | COTRN02C | Transaction Add screen |
| COUSR00.bms | COUSR00C | User List screen |
| COUSR01.bms | COUSR01C | User Add screen |
| COUSR02.bms | COUSR02C | User Update screen |

### 5.2 Assembler Programs (`app/asm/`)

| Program | Purpose |
|---------|---------|
| COBDATFT.asm | Date formatting routine (called by CBACT01C) |
| MVSWAIT.asm | MVS STIMER wait routine (called by COBSWAIT) |

### 5.3 Scheduler Definitions (`app/scheduler/`)

| File | Purpose |
|------|---------|
| CardDemo.ca7 | CA-7 scheduling definitions |
| CardDemo.controlm | Control-M scheduling definitions |

### 5.4 Procedures (`app/proc/`)

| Proc | Purpose |
|------|---------|
| REPROC.prc | Report processing procedure |
| TRANREPT.prc | Transaction report procedure (cataloged) |
