# DEPENDENCY MAP — CardDemo COBOL Estate

## 1. Online Navigation Call Graph (CICS XCTL/LINK Chains)

```
COSGN00C (Sign-On Entry Point)
│
├──[Admin User]── XCTL ──► COADM01C (Admin Menu — 6 options)
│                           ├── XCTL ──► COUSR00C (User List)
│                           │             ├── XCTL ──► COUSR02C (User Update)
│                           │             └── XCTL ──► COUSR03C (User Delete)
│                           ├── XCTL ──► COUSR01C (User Add)
│                           ├── XCTL ──► COTRTLIC (Tran Type List — DB2)
│                           └── XCTL ──► COTRTUPC (Tran Type Maintenance — DB2)
│
└──[Regular User]── XCTL ──► COMEN01C (Main Menu — Hub, 11 options)
                              ├── XCTL ──► COACTVWC (Account View)
                              ├── XCTL ──► COACTUPC (Account Update — 4,236 LOC)
                              ├── XCTL ──► COCRDLIC (Card List)
                              │             ├── XCTL ──► COCRDSLC (Card View)
                              │             └── XCTL ──► COCRDUPC (Card Update)
                              ├── XCTL ──► COTRN00C (Transaction List)
                              │             └── XCTL ──► COTRN01C (Transaction View)
                              ├── XCTL ──► COTRN02C (Transaction Add)
                              │             └── CALL ──► CSUTLDTC (Date Validation)
                              ├── XCTL ──► CORPT00C (Report Request)
                              │             ├── CALL ──► CSUTLDTC (Date Validation)
                              │             └── submits JCL ──► INTRDRJ1 ──► INTRDRJ2
                              ├── XCTL ──► COBIL00C (Bill Payment)
                              └── XCTL ──► COPAUS0C (Auth Summary Browse — IMS)
                                            └── LINK ──► COPAUS1C (Auth Detail Update)
                                                          └── LINK ──► COPAUS2C (Fraud Flag — DB2)
```

### Navigation Hub Dependencies

| Hub Program | # of Outgoing XCTL | Target Programs |
|-------------|---------------------|-----------------|
| COMEN01C | 11 | COACTVWC, COACTUPC, COCRDLIC, COCRDSLC, COCRDUPC, COTRN00C, COTRN01C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C |
| COADM01C | 6 | COUSR00C, COUSR01C, COUSR02C, COUSR03C, COTRTLIC, COTRTUPC |
| COSGN00C | 2 | COADM01C (admin), COMEN01C (user) |

---

## 2. Batch Call Graph (CALL Statements)

```
CBACT01C ── CALL ──► COBDATFT (assembler: date formatting)

CBACT02C ── CALL ──► CEE3ABD  (LE: abnormal termination)
CBACT03C ── CALL ──► CEE3ABD
CBCUS01C ── CALL ──► CEE3ABD

CBSTM03A ── CALL ──► CBSTM03B (COBOL: statement I/O submodule)

COBSWAIT ── CALL ──► MVSWAIT  (assembler: timed wait routine)

CSUTLDTC ── CALL ──► CEEDAYS  (LE: date conversion)

COTRN02C ── CALL ──► CSUTLDTC (COBOL: date validation utility)
CORPT00C ── CALL ──► CSUTLDTC

COPAUA0C ── CALL ──► MQOPEN, MQGET, MQPUT1 (MQ API)
COPAUA0C ── EXEC DLI ──► IMS Database (GU/GNP/SCHD/TERM)

PAUDBLOD ── CALL ──► CBLTDLI (IMS DL/I: ISRT, GU)
PAUDBUNL ── CALL ──► CBLTDLI (IMS DL/I: GN, GNP)
DBUNLDGS ── CALL ──► CBLTDLI (IMS DL/I: GN, GNP, ISRT — via GSAM)
```

### External (Non-COBOL) Dependencies

| External Program | Type | Called By | Replacement Needed |
|-----------------|------|-----------|-------------------|
| COBDATFT | Assembler | CBACT01C | Java `DateTimeFormatter` |
| CEE3ABD | LE Runtime | CBACT02C, CBACT03C, CBCUS01C | Java exception handling |
| CEEDAYS | LE Runtime | CSUTLDTC | `java.time.LocalDate` |
| MVSWAIT | Assembler | COBSWAIT | `Thread.sleep()` |
| CBLTDLI | IMS DL/I | PAUDBLOD, PAUDBUNL, DBUNLDGS | JPA/Spring Data |
| MQOPEN/MQGET/MQPUT1 | MQ API | COPAUA0C | Spring JMS / SQS |
| DFHCSDUP | CICS Utility | CBADMCDJ.jcl | Not needed (no CSD in Java) |

---

## 3. Copybook Dependency Matrix

### Programs → Copybooks Referenced

| Program | Copybooks (COPY statements) | Count |
|---------|---------------------------|-------|
| COACTUPC | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (×13) | 58 |
| COPAUA0C | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y, COCOM01Y, COTTL01Y, CSDAT01Y | 17 |
| COACTVWC | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY | 16 |
| COCRDSLC | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY | 16 |
| COCRDUPC | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY | 16 |
| COPAUS0C | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA | 15 |
| COTRTUPC | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COTRTUP, CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA, CVCRD01Y, COADM02Y | 15 |
| COCRDLIC | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY | 14 |
| COTRTLIC | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COTRTLI, CSDB2RPY, CSDB2RWY, DFHAID, DFHBMSCA | 12 |
| COBIL00C | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA | 11 |
| COPAUS1C | COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA | 11 |
| COTRN02C | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA | 11 |

### Most-Referenced Copybooks (Shared Dependencies)

| Copybook | Referenced By (count) | Entity/Purpose |
|----------|----------------------|----------------|
| COCOM01Y | 25 programs | CICS communication area — the "session object" |
| DFHAID | 22 programs | CICS AID key definitions (IBM-supplied) |
| DFHBMSCA | 22 programs | CICS BMS screen attributes (IBM-supplied) |
| COTTL01Y | 22 programs | Screen title/branding |
| CSDAT01Y | 22 programs | Date/time working storage |
| CSMSG01Y | 21 programs | Common messages |
| CVACT01Y | 13 programs | Account record structure |
| CVACT03Y | 15 programs | Card cross-reference record |
| CVCUS01Y | 11 programs | Customer record structure |
| CVACT02Y | 8 programs | Card record structure |
| CVTRA05Y | 11 programs | Transaction record structure |
| CSUSR01Y | 11 programs | User security record |
| CVCRD01Y | 10 programs | Card working areas/AID flags |

---

## 4. VSAM File Usage — Dataset Lineage

### File Access Matrix (Programs × VSAM Files)

| VSAM File (Dataset) | Writers (WRITE/REWRITE) | Readers (READ/STARTBR) |
|---------------------|------------------------|----------------------|
| **ACCTDATA.VSAM.KSDS** | CBACT04C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE), CBTRN02C (REWRITE), CBIMPORT (WRITE) | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBSTM03A/B, CBEXPORT, COACTVWC, COACTUPC, COBIL00C, COPAUA0C, COPAUS0C, COACCT01 |
| **CARDDATA.VSAM.KSDS** | COCRDUPC (REWRITE), CBIMPORT (WRITE) | CBACT02C, CBTRN01C, COCRDLIC (STARTBR), COCRDSLC, COCRDUPC, CBEXPORT, COPAUS0C |
| **CUSTDATA.VSAM.KSDS** | COACTUPC (REWRITE), CBIMPORT (WRITE) | CBCUS01C, CBTRN01C, CBSTM03A/B, CBEXPORT, COACTVWC, COACTUPC, COCRDSLC, COPAUA0C, COPAUS0C |
| **CARDXREF.VSAM.KSDS** | CBIMPORT (WRITE) | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A/B, CBEXPORT, COACTVWC, COACTUPC, COCRDSLC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C, COACCT01 |
| **TRANSACT.VSAM.KSDS** | CBTRN02C (WRITE), COBIL00C (WRITE), CBIMPORT (WRITE) | CBTRN03C, CBSTM03A/B, CBEXPORT, COTRN00C (STARTBR), COTRN01C |
| **DALYTRAN.PS** | *(External feed)* | CBTRN01C, CBTRN02C |
| **TCATBALF.VSAM.KSDS** | CBTRN02C (REWRITE) | CBACT04C, CBTRN02C |
| **DISCGRP.VSAM.KSDS** | *(Reference data)* | CBACT04C |
| **TRANTYPE.VSAM.KSDS** | *(Reference data)* | CBTRN03C |
| **TRANCATG.VSAM.KSDS** | *(Reference data)* | CBTRN03C |
| **USRSEC.VSAM.KSDS** | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) | COSGN00C, COUSR00C (STARTBR), COUSR02C, COUSR03C |
| **EXPORT.DATA** | CBEXPORT (WRITE) | CBIMPORT |
| **SYSTRAN(+1)** GDG | CBACT04C (WRITE) | COMBTRAN.jcl (SORT input) |

### Dataset Ownership Summary

| Entity | Primary Dataset | Primary Writer | Backup Strategy |
|--------|----------------|---------------|-----------------|
| Account | ACCTDATA.VSAM.KSDS | CBTRN02C, CBACT04C, COACTUPC | READACCT → ACCTDATA.PSCOMP |
| Card | CARDDATA.VSAM.KSDS | COCRDUPC | READCARD (display only) |
| Customer | CUSTDATA.VSAM.KSDS | COACTUPC | READCUST (display only) |
| Cross-Reference | CARDXREF.VSAM.KSDS | CBIMPORT only | READXREF (display only) |
| Transaction | TRANSACT.VSAM.KSDS | CBTRN02C, COBIL00C | TRANBKP → TRANSACT.BKUP(+n) |

---

## 5. JCL Job → Program → Dataset Lineage

### Data Flow Through Batch Pipeline

```
                          ┌─────────────────┐
                          │  External Feed   │
                          │   DALYTRAN.PS    │
                          └────────┬────────┘
                                   │
                                   ▼
                    ┌──────────────────────────────┐
  POSTTRAN.jcl ───►│        CBTRN02C               │
                    │  (Transaction Posting)         │
                    │  Reads: DALYTRAN, CARDXREF     │
                    │  Updates: ACCTDATA, TCATBALF   │
                    │  Writes: TRANSACT, DALYREJS    │
                    └──────┬──────────────┬─────────┘
                           │              │
                    ┌──────▼──────┐  ┌────▼────────┐
                    │  TRANSACT   │  │  DALYREJS    │
                    │  VSAM.KSDS  │  │  GDG (+1)    │
                    └──────┬──────┘  └──────────────┘
                           │
              ┌────────────┼────────────┐
              │            │            │
              ▼            ▼            ▼
┌─────────────────┐ ┌──────────────┐ ┌──────────────────┐
│ INTCALC.jcl     │ │ CREASTMT.JCL │ │ TRANREPT.jcl     │
│ ───►CBACT04C    │ │ ───►SORT     │ │ ───►REPRO+SORT   │
│ (Interest Calc) │ │ ───►CBSTM03A │ │ ───►CBTRN03C     │
│                 │ │ ───►CBSTM03B │ │ (Tran Report)    │
│ Reads: TCATBALF,│ │              │ │                  │
│  XREF, DISCGRP, │ │ Reads: TRNX, │ │ Reads: TRANSACT, │
│  ACCTDATA       │ │  XREF, CUST, │ │  CARDXREF,       │
│ Rewrites: ACCT  │ │  ACCT        │ │  TRANTYPE,       │
│ Writes: SYSTRAN │ │ Writes: HTML,│ │  TRANCATG        │
│                 │ │  STATEMNT.PS │ │ Writes: REPTFILE │
└─────────────────┘ └──────────────┘ └──────────────────┘
         │
         ▼
┌─────────────────┐
│ COMBTRAN.jcl    │
│ ───►SORT        │
│ Merges:         │
│ TRANSACT.BKUP + │
│ SYSTRAN         │
│ ───►TRANSACT    │
│    .COMBINED    │
└─────────────────┘
```

### Daily Batch Pipeline Sequence (Control-M Scheduled)

```
Step 1: POSTTRAN.jcl  ──► CBTRN02C ──► Posts DALYTRAN → TRANSACT
              │                         Updates ACCTDATA balances
              │                         Updates TCATBALF category balances
              │                         Writes rejects → DALYREJS
              ▼
Step 2: INTCALC.jcl   ──► CBACT04C ──► Reads TCATBALF + DISCGRP
              │                         Computes interest/fees
              │                         Rewrites ACCTDATA
              │                         Writes system transactions → SYSTRAN
              ▼
Step 3: CREASTMT.JCL  ──► SORT + CBSTM03A/CBSTM03B
              │              Sorts TRANSACT by card+tran-id
              │              Generates text + HTML statements
              ▼
Step 4: TRANREPT.jcl  ──► REPRO + SORT + CBTRN03C
              │              Filters by date range
              │              Generates daily transaction report
              ▼
Step 5: COMBTRAN.jcl  ──► SORT
              │              Merges backups + system transactions
              ▼
Step 6: TRANBKP.jcl   ──► REPRO + IDCAMS
                             Backs up TRANSACT to GDG
```

### VSAM Setup Pipeline (One-Time / Recovery)

```
ACCTFILE.jcl ──► IDCAMS DEL/DEF/REPRO ──► ACCTDATA.VSAM.KSDS
CARDFILE.jcl ──► IDCAMS DEL/DEF/REPRO + AIX ──► CARDDATA.VSAM.KSDS + AIX
CUSTFILE.jcl ──► IDCAMS DEL/DEF/REPRO ──► CUSTDATA.VSAM.KSDS
XREFFILE.jcl ──► IDCAMS DEL/DEF/REPRO + AIX ──► CARDXREF.VSAM.KSDS + AIX + PATH
TRANFILE.jcl ──► IDCAMS DEL/DEF/REPRO + AIX ──► TRANSACT.VSAM.KSDS + AIX
DISCGRP.jcl  ──► IDCAMS DEL/DEF/REPRO ──► DISCGRP.VSAM.KSDS
TCATBALF.jcl ──► IDCAMS DEL/DEF/REPRO ──► TCATBALF.VSAM.KSDS
TRANTYPE.jcl ──► IDCAMS DEL/DEF/REPRO ──► TRANTYPE.VSAM.KSDS
TRANCATG.jcl ──► IDCAMS DEL/DEF/REPRO ──► TRANCATG.VSAM.KSDS
DUSRSECJ.jcl ──► IEBGENER + IDCAMS ──► USRSEC.VSAM.KSDS
```

### Data Export/Import Pipeline

```
CBEXPORT.jcl ──► CBEXPORT ──► Reads 5 VSAM files ──► EXPORT.DATA (sequential)
                                                            │
CBIMPORT.jcl ──► CBIMPORT ◄─────────────────────────────────┘
                  │
                  ├──► CUSTDATA.IMPORT
                  ├──► ACCTDATA.IMPORT
                  ├──► CARDXREF.IMPORT
                  ├──► TRANSACT.IMPORT
                  └──► IMPORT.ERRORS
```

### IMS Sub-App Pipeline

```
LOADPADB.JCL ──► PAUDBLOD ──► Loads flat file into IMS Pending Auth DB
UNLDPADB.JCL ──► PAUDBUNL ──► Unloads IMS DB to flat file
UNLDGSAM.JCL ──► DBUNLDGS ──► Unloads via GSAM to sequential file
CBPAUP0J.jcl ──► CBPAUP0C ──► Purges expired auth segments from IMS
```

---

## 6. Inter-Program Dependency Summary

### Programs with Most Dependents (Highest Fan-In)

| Program | Depended On By | Dependency Type |
|---------|---------------|-----------------|
| COMEN01C | 11 programs | XCTL target dispatcher |
| COADM01C | 6 programs | XCTL target dispatcher |
| CSUTLDTC | 2 programs (COTRN02C, CORPT00C) | CALL for date validation |
| CBSTM03B | 1 program (CBSTM03A) | CALL for I/O operations |
| COPAUS2C | 1 program (COPAUS1C) | LINK for DB2 fraud insert |

### Programs with Most Dependencies (Highest Fan-Out)

| Program | Depends On | Dependency Type |
|---------|-----------|-----------------|
| COPAUA0C | MQ API, IMS DL/I, 3 VSAM files, 17 copybooks | MQ + IMS + CICS |
| COACTUPC | 3 VSAM files, 58 copybooks, CSSETATY (×13 REPLACING) | CICS + VSAM |
| CBSTM03A | CBSTM03B (CALL), 4 VSAM files (via submodule), 5 copybooks | Batch |
| CBTRN02C | 4 VSAM files (R/W), 6 copybooks | Batch |
| COBIL00C | 3 VSAM files, 11 copybooks | CICS |

### Shared Copybook Clusters (Tight Coupling Groups)

Programs sharing the same set of business-entity copybooks form natural coupling clusters:

| Cluster | Shared Copybooks | Programs |
|---------|-----------------|----------|
| **Account Management** | CVACT01Y, CVACT03Y, CVCUS01Y | COACTUPC, COACTVWC, COBIL00C, CBACT04C, CBTRN02C, CBSTM03A |
| **Card Management** | CVACT02Y, CVACT03Y | COCRDLIC, COCRDSLC, COCRDUPC, CBACT02C |
| **Transaction Processing** | CVTRA05Y, CVACT03Y | COTRN00C, COTRN01C, COTRN02C, CBTRN01C, CBTRN02C, CBTRN03C |
| **Authorization (IMS)** | CIPAUSMY, CIPAUDTY | COPAUA0C, COPAUS0C, COPAUS1C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS |
| **User Security** | CSUSR01Y | COSGN00C, COUSR00C–03C, COACTUPC |
| **DB2 Transaction Types** | CSDB2RPY, CSDB2RWY | COTRTLIC, COTRTUPC, COBTUPDT |
