# DEPENDENCY MAP — CardDemo COBOL Estate

> Inter-program call graph, dataset lineage, and end-to-end batch pipeline flow.

---

## 1. Inter-Program Call Graph

### 1.1 CALL Dependencies (static program invocation)

```
CBACT01C ──CALL──► COBDATFT  (assembler date formatting utility)
         ──CALL──► CEE3ABD   (LE runtime abend)

CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD

CBSTM03A ──CALL──► CBSTM03B  (statement subroutine: file open/read)
         ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT   (assembler wait service)

CORPT00C ──CALL──► CSUTLDTC  (date utility)
COTRN02C ──CALL──► CSUTLDTC  (date utility)

COPAUA0C ──CALL──► MQOPEN, MQGET, MQPUT1, MQCLOSE  (MQ Series API)
COACCT01 ──CALL──► MQOPEN (×3), MQGET, MQPUT (×2), MQCLOSE (×3)
CODATE01 ──CALL──► MQOPEN (×3), MQGET, MQPUT (×2), MQCLOSE (×3)

CBPAUP0C ──CALL──► CBLTDLI   (IMS DL/I: GN, GNP operations)
DBUNLDGS ──CALL──► CBLTDLI   (IMS DL/I: GU, GN operations)
COPAUS0C ──CALL──► CBLTDLI   (IMS DL/I)
COPAUS1C ──CALL──► CBLTDLI   (IMS DL/I)
```

### 1.2 CICS XCTL Navigation (online screen flow)

```
COSGN00C ──XCTL──► COMEN01C  (regular user → main menu)
         ──XCTL──► COADM01C  (admin user → admin menu)

COMEN01C ──XCTL──► COACTVWC  (option 1: Account View)
         ──XCTL──► COACTUPC  (option 2: Account Update)
         ──XCTL──► COCRDLIC  (option 3: Credit Card List)
         ──XCTL──► COCRDSLC  (option 4: Credit Card View)
         ──XCTL──► COCRDUPC  (option 5: Credit Card Update)
         ──XCTL──► COTRN00C  (option 6: Transaction List)
         ──XCTL──► COTRN01C  (option 7: Transaction View)
         ──XCTL──► COTRN02C  (option 8: Transaction Add)
         ──XCTL──► CORPT00C  (option 9: Transaction Reports)
         ──XCTL──► COBIL00C  (option 10: Bill Payment)
         ──XCTL──► COPAUS0C  (option 11: Auth View)

COADM01C ──XCTL──► COUSR00C  (option 1: User List)
         ──XCTL──► COUSR01C  (option 2: User Add)
         ──XCTL──► COUSR02C  (option 3: User Update)
         ──XCTL──► COUSR03C  (option 4: User Delete)
         ──XCTL──► COTRTLIC  (option 5: Tran Type List/Update)
         ──XCTL──► COTRTUPC  (option 6: Tran Type Maintenance)

COCRDLIC ──XCTL──► COCRDSLC  (select card → view details)
         ──XCTL──► COCRDUPC  (select card → update)

COPAUS0C ──XCTL──► COPAUS1C  (select auth summary → view detail)
```

### 1.3 Complete Online Screen Navigation Map

```
┌─────────────┐
│  COSGN00C   │  Sign-On Screen
│  (Sign-On)  │
└──────┬──────┘
       │
   ┌───┴───┐
   ▼       ▼
┌──────┐ ┌──────┐
│COMEN │ │COADM │
│ 01C  │ │ 01C  │  Main / Admin Menu
│(Menu)│ │(Admin│
└──┬───┘ └──┬───┘
   │        │
   │        ├──► COUSR00C (User List)
   │        ├──► COUSR01C (User Add)
   │        ├──► COUSR02C (User Update)
   │        ├──► COUSR03C (User Delete)
   │        ├──► COTRTLIC (Tran Type List) ──► DB2
   │        └──► COTRTUPC (Tran Type Update) ──► DB2
   │
   ├──► COACTVWC (Account View)
   ├──► COACTUPC (Account Update)
   ├──► COCRDLIC (Card List) ──► COCRDSLC / COCRDUPC
   ├──► COCRDSLC (Card Search/View)
   ├──► COCRDUPC (Card Update)
   ├──► COTRN00C (Transaction List)
   ├──► COTRN01C (Transaction View)
   ├──► COTRN02C (Transaction Add)
   ├──► CORPT00C (Reports) ──submits──► Batch JCL
   ├──► COBIL00C (Bill Payment)
   └──► COPAUS0C (Auth Summary) ──► COPAUS1C (Auth Detail)
                                         │
                                    ┌────┘
                                    ▼
                               COPAUS2C (Auth DB2 Query)
```

---

## 2. Dataset Lineage

### 2.1 VSAM KSDS Datasets — Lifecycle

| Dataset (DSN suffix) | Definition JCL | Load Source | Programs That READ | Programs That WRITE/UPDATE | CICS Programs |
|---------------------|---------------|------------|-------------------|--------------------------|---------------|
| ACCTDATA.VSAM.KSDS | ACCTFILE.jcl | ACCTDATA.PS | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBEXPORT | CBIMPORT, CBTRN02C | COACTUPC, COACTVWC, COBIL00C, COTRN02C |
| CARDDATA.VSAM.KSDS | CARDFILE.jcl | CARDDATA.PS | CBACT02C, CBTRN01C, CBEXPORT | CBIMPORT | COCRDLIC, COCRDSLC, COCRDUPC |
| CUSTDATA.VSAM.KSDS | CUSTFILE.jcl | CUSTDATA.PS | CBCUS01C, CBTRN01C, CBEXPORT | CBIMPORT | COACTUPC, COACTVWC, COCRDSLC, COCRDUPC |
| CARDXREF.VSAM.KSDS | XREFFILE.jcl | CARDXREF.PS | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT | CBIMPORT | COACTUPC, COACTVWC, COTRN02C |
| TRANSACT.VSAM.KSDS | TRANFILE.jcl | DALYTRAN.PS.INIT | CBTRN01C, CBTRN03C, CBEXPORT | CBTRN02C, CBIMPORT | COTRN00C, COTRN01C, COTRN02C, COBIL00C |
| TRANTYPE.VSAM.KSDS | TRANTYPE.jcl | TRANTYPE.PS | CBTRN03C | COBTUPDT | COTRTLIC, COTRTUPC |
| TRANCATG.VSAM.KSDS | TRANCATG.jcl | TRANCATG.PS | CBTRN03C | — | — |
| TCATBALF.VSAM.KSDS | TCATBALF.jcl | TCATBALF.PS | CBACT04C | CBTRN02C | — |
| DISCGRP.VSAM.KSDS | DISCGRP.jcl | DISCGRP.PS | CBACT04C | — | — |
| USRSEC.VSAM.KSDS | DUSRSECJ.jcl | USRSEC.PS | — | — | COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C |

### 2.2 Sequential / GDG Datasets

| Dataset | Produced By | Consumed By | Purpose |
|---------|------------|-------------|---------|
| DALYTRAN.PS | External (online transactions) | CBTRN01C, CBTRN02C, COMBTRAN.jcl | Daily transaction input file |
| DALYREJS (GDG) | CBTRN02C (via POSTTRAN.jcl) | Audit/review | Rejected daily transactions |
| TRANSACT.BKUP (GDG) | TRANBKP.jcl (IDCAMS REPRO) | Recovery | Transaction master backup |
| SYSTRAN (GDG) | CBACT04C (via INTCALC.jcl) | COMBTRAN.jcl | Interest-generated transactions |
| EXPFILE | CBEXPORT (via CBEXPORT.jcl) | CBIMPORT (via CBIMPORT.jcl) | Branch migration export |
| STATEMNT.PS | CBSTM03A (via CREASTMT.JCL) | TXT2PDF1.JCL | Account statement (text) |
| HTMLFILE | CBSTM03A (via CREASTMT.JCL) | End user | Account statement (HTML) |
| TCATBALF.REPT | PRTCATBL.jcl (SORT) | End user | Tran category balance report |
| TRANSACT.DALY (GDG) | TRANREPT.jcl (SORT) | CBTRN03C | Sorted daily tran for reporting |
| TRANREPT | CBTRN03C (via TRANREPT.jcl) | End user | Transaction report |

### 2.3 JCL Job → Program → Dataset Mapping

```
READACCT.jcl ──exec──► CBACT01C ──reads──► ACCTDATA.VSAM.KSDS
                                  ──writes─► ACCTDATA.PSCOMP, ARRYPS, VBPS

READCARD.jcl ──exec──► CBACT02C ──reads──► CARDDATA.VSAM.KSDS

READCUST.jcl ──exec──► CBCUS01C ──reads──► CUSTDATA.VSAM.KSDS

READXREF.jcl ──exec──► CBACT03C ──reads──► CARDXREF.VSAM.KSDS

INTCALC.jcl  ──exec──► CBACT04C ──reads──► TCATBALF, XREFFILE, ACCTFILE, DISCGRP
                                  ──writes─► TRANSACT (GDG +1)

POSTTRAN.jcl ──exec──► CBTRN02C ──reads──► DALYTRAN, XREFFILE, ACCTFILE
                                  ──writes─► TRANFILE, DALYREJS (GDG +1), TCATBALF

TRANREPT.jcl ──sort───► TRANSACT ──to──► TRANSACT.DALY (GDG +1)
             ──exec──► CBTRN03C ──reads──► TRANSACT.DALY, CARDXREF, TRANTYPE, TRANCATG
                                  ──writes─► TRANREPT

CREASTMT.JCL ──sort/idcams─► prep
             ──exec──► CBSTM03A ──calls──► CBSTM03B
                                  ──reads──► XREFFILE, CUSTFILE, ACCTFILE, TRNXFILE
                                  ──writes─► STMTFILE, HTMLFILE

CBEXPORT.jcl ──exec──► CBEXPORT ──reads──► CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE
                                  ──writes─► EXPFILE

CBIMPORT.jcl ──exec──► CBIMPORT ──reads──► EXPFILE
                                  ──writes─► CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT
```

---

## 3. End-to-End Batch Pipeline Flows

### 3.1 Daily Transaction Processing Pipeline

```
┌──────────────────────────────────────────────────────────────────────────┐
│                    DAILY BATCH PIPELINE                                  │
│                    (Control-M: DAILY-TransactionBackup)                  │
│                                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐          │
│  │CLOSEFIL  │───►│TRANBKP   │───►│WAITSTEP  │───►│OPENFIL   │          │
│  │Close CICS│    │Backup    │    │Pause     │    │Open CICS │          │
│  │files     │    │tran mstr │    │          │    │files     │          │
│  └──────────┘    └──────────┘    └──────────┘    └──────────┘          │
│                                                                          │
│  Detailed flow:                                                          │
│                                                                          │
│  1. CLOSEFIL.jcl    → SDSF: close CICS files for batch access          │
│  2. TRANBKP.jcl     → REPRO TRANSACT.VSAM.KSDS → TRANSACT.BKUP(+1)   │
│                      → DELETE all records from TRANSACT.VSAM.KSDS       │
│  3. WAITSTEP.jcl    → PGM=COBSWAIT (timed pause for I/O quiesce)      │
│  4. OPENFIL.jcl     → SDSF: reopen CICS files for online access       │
└──────────────────────────────────────────────────────────────────────────┘
```

### 3.2 Online Transaction Posting (triggered by CORPT00C or external)

```
┌──────────────────────────────────────────────────────────────────────────┐
│                TRANSACTION POSTING FLOW                                   │
│                                                                          │
│  ┌──────────────┐     ┌────────────────┐     ┌────────────────┐        │
│  │ DALYTRAN.PS  │────►│ POSTTRAN.jcl   │────►│ TRANSACT.VSAM  │        │
│  │ (daily input)│     │ PGM=CBTRN02C   │     │ (master file)  │        │
│  └──────────────┘     │                │     └────────────────┘        │
│                       │ Validates:     │                                │
│                       │  - card → xref │     ┌────────────────┐        │
│                       │  - acct exists │────►│ DALYREJS(+1)   │        │
│                       │  - acct active │     │ (rejects GDG)  │        │
│                       │                │     └────────────────┘        │
│                       │ Updates:       │                                │
│                       │  - ACCTFILE    │     ┌────────────────┐        │
│                       │  - TCATBALF    │────►│ TCATBALF.VSAM  │        │
│                       └────────────────┘     │ (cat balances) │        │
│                                              └────────────────┘        │
└──────────────────────────────────────────────────────────────────────────┘
```

### 3.3 Monthly Interest Calculation Pipeline

```
┌──────────────────────────────────────────────────────────────────────────┐
│                MONTHLY BATCH PIPELINE                                    │
│                (Control-M: MONTHLY-InterestCalculation)                  │
│                                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────┐          │
│  │CLOSEFIL  │───►│INTCALC   │───►│COMBTRAN  │───►│WAITSTEP  │──►OPENFIL│
│  │Close CICS│    │Interest  │    │Combine   │    │Pause     │          │
│  │files     │    │calc      │    │trans     │    │          │          │
│  └──────────┘    └──────────┘    └──────────┘    └──────────┘          │
│                                                                          │
│  Detailed flow:                                                          │
│                                                                          │
│  1. CLOSEFIL    → Close CICS files                                      │
│  2. INTCALC.jcl → PGM=CBACT04C (PARM=date)                            │
│     Reads:  TCATBALF, XREFFILE (+ AIX), ACCTFILE, DISCGRP             │
│     Process: For each tran-cat-balance record:                          │
│              - Look up account via XREF                                 │
│              - Get discount group interest rate                          │
│              - Compute interest on balance                              │
│              - Compute fees                                             │
│              - Update account balance                                   │
│     Writes: TRANSACT (GDG +1) — new interest/fee transactions          │
│                                                                          │
│  3. COMBTRAN.jcl → SORT + IDCAMS REPRO                                 │
│     Merges new interest transactions into TRANSACT.VSAM.KSDS           │
│                                                                          │
│  4. WAITSTEP    → Timed pause                                           │
│  5. OPENFIL     → Reopen CICS files                                    │
└──────────────────────────────────────────────────────────────────────────┘
```

### 3.4 Weekly Reference Data Refresh Pipeline

```
┌──────────────────────────────────────────────────────────────────────────┐
│                WEEKLY BATCH PIPELINE                                     │
│                (Control-M: WEEKLY-TransactionTypesDBRefresh, Saturday)   │
│                                                                          │
│  ┌──────────┐                                                           │
│  │MNTTRDB2  │ (Maintain Transaction Types in DB2)                      │
│  │PGM=      │                                                           │
│  │COBTUPDT  │                                                           │
│  └────┬─────┘                                                           │
│       │                                                                  │
│  ┌────┴────────────────────┬───────────────────────┐                   │
│  ▼                         ▼                       │                    │
│  ┌─────────────────┐  ┌─────────────────┐          │                    │
│  │DisclosureGroups │  │TransactionTypes │          │                    │
│  │Refresh          │  │DBRefresh        │          │                    │
│  │                 │  │                 │          │                    │
│  │CLOSEFIL→DISCGRP │  │TRANEXTR         │          │                    │
│  │→WAITSTEP→OPENFIL│  │(extract DB2→PS) │          │                    │
│  └─────────────────┘  └─────────────────┘          │                    │
│                                                     │                    │
│  DISCGRP.jcl: Redefine and reload DISCGRP.VSAM.KSDS from PS           │
│  TRANEXTR.jcl: Extract tran types from DB2 to flat file                │
└──────────────────────────────────────────────────────────────────────────┘
```

### 3.5 Statement Generation Pipeline

```
┌──────────────────────────────────────────────────────────────────────────┐
│                STATEMENT GENERATION (ad-hoc / scheduled)                 │
│                                                                          │
│  CREASTMT.JCL:                                                          │
│  ┌──────────┐    ┌──────────┐    ┌──────────┐    ┌──────────────┐      │
│  │DELDEF01  │───►│STEP010   │───►│STEP020   │───►│STEP040       │      │
│  │IDCAMS    │    │SORT tran │    │IDCAMS    │    │PGM=CBSTM03A  │      │
│  │delete/def│    │by card#  │    │REPRO     │    │               │      │
│  └──────────┘    └──────────┘    └──────────┘    │ Calls CBSTM03B│      │
│                                                   │               │      │
│                                                   │ Reads:        │      │
│                                                   │  XREFFILE     │      │
│                                                   │  CUSTFILE     │      │
│                                                   │  ACCTFILE     │      │
│                                                   │  TRNXFILE     │      │
│                                                   │               │      │
│                                                   │ Writes:       │      │
│                                                   │  STMTFILE.PS  │──►TXT2PDF1.JCL──►PDF
│                                                   │  HTMLFILE     │      │
│                                                   └───────────────┘      │
└──────────────────────────────────────────────────────────────────────────┘
```

### 3.6 Branch Migration (Export/Import)

```
┌──────────────────────────────────────────────────────────────────────────┐
│                BRANCH MIGRATION FLOW                                     │
│                                                                          │
│  SOURCE BRANCH:                                                          │
│  CBEXPORT.jcl → PGM=CBEXPORT                                           │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │CUSTFILE  │  │ACCTFILE  │  │XREFFILE  │  │TRANSACT  │  │CARDFILE │ │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬────┘ │
│       └──────────────┴──────────────┴──────────────┴──────────────┘     │
│                                    │                                     │
│                                    ▼                                     │
│                              ┌──────────┐                               │
│                              │ EXPFILE  │  (single sequential file)     │
│                              └────┬─────┘                               │
│                                   │  (FTP / network transfer)           │
│                                   ▼                                     │
│  TARGET BRANCH:                                                          │
│  CBIMPORT.jcl → PGM=CBIMPORT                                           │
│                                    │                                     │
│       ┌──────────────┬──────────────┬──────────────┬──────────────┐     │
│       ▼              ▼              ▼              ▼              ▼     │
│  ┌─────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌─────────┐ │
│  │CUSTOUT  │  │ACCTOUT   │  │XREFOUT   │  │TRNXOUT   │  │CARDOUT  │ │
│  └─────────┘  └──────────┘  └──────────┘  └──────────┘  └─────────┘ │
│                                                           ┌─────────┐ │
│                                                           │ERROUT   │ │
│                                                           └─────────┘ │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## 4. Copybook Dependency Matrix

### 4.1 Which Programs Use Which Copybooks

| Copybook | Used By Programs |
|----------|-----------------|
| CVACT01Y | CBACT01C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN02C, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COACCT01 |
| CVACT02Y | CBACT02C, CBEXPORT, CBIMPORT, CBTRN01C, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |
| CVACT03Y | CBACT03C, CBACT04C, CBEXPORT, CBIMPORT, CBSTM03A, CBTRN01C, CBTRN02C, CBTRN03C, COACTUPC, COACTVWC, COBIL00C, COTRN02C |
| CVCUS01Y | CBCUS01C, CBEXPORT, CBIMPORT, CBTRN01C, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC |
| CVTRA05Y | CBEXPORT, CBIMPORT, CBACT04C, CBTRN02C, CBTRN03C, COBIL00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C |
| CVTRA06Y | CBTRN01C, CBTRN02C |
| COCOM01Y | COADM01C, COBIL00C, COMEN01C, COSGN00C, CORPT00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C–03C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C, COPAUS1C, COPAUS2C |
| DFHAID | All CICS online programs (17+) |
| DFHBMSCA | All CICS online programs (17+) |
| COTTL01Y | All CICS online programs |
| CSDAT01Y | All CICS online programs |
| CSMSG01Y | All CICS online programs |
| CSUSR01Y | COSGN00C, COADM01C, COMEN01C, COUSR00C–03C, COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |
| CVCRD01Y | COACTUPC, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC |
| CIPAUSMY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL |
| CIPAUDTY | CBPAUP0C, COPAUA0C, COPAUS0C, COPAUS1C, DBUNLDGS, PAUDBLOD, PAUDBUNL |

### 4.2 External Program Dependencies

| External Program | Type | Called By |
|-----------------|------|-----------|
| CEE3ABD | LE Runtime (abend) | All batch programs |
| COBDATFT | Assembler (date format) | CBACT01C |
| MVSWAIT | Assembler (wait service) | COBSWAIT |
| CBLTDLI | IMS DL/I interface | CBPAUP0C, COPAUS0C, COPAUS1C, DBUNLDGS |
| MQOPEN/MQGET/MQPUT/MQPUT1/MQCLOSE | MQ Series API | COPAUA0C, COACCT01, CODATE01 |
| DSNTIAC | DB2 message formatter | COTRTLIC, COTRTUPC (via CSDB2RPY) |
| IDCAMS | VSAM utility | All file-definition JCL |
| SORT (DFSORT) | Sort utility | COMBTRAN, CREASTMT, PRTCATBL, TRANREPT |
| SDSF | System Display Facility | CLOSEFIL, OPENFIL, CARDFILE, TRANFILE |
| IKJEFT1B | TSO batch | TXT2PDF1 (REXX TXT2PDF) |
| IEFBR14 | Null program | CREASTMT, PRTCATBL, READACCT |
| IEBGENER | Copy utility | INTRDRJ1 |
| FTP | File transfer | FTPJCL |
