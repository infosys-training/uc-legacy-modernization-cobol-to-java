# DEPENDENCY_MAP.md — CardDemo Inter-Program Call Graph & Dataset Lineage

> **Total Programs:** 44 &nbsp;|&nbsp; **Inter-program CALLs:** 28+ &nbsp;|&nbsp; **CICS XCTLs:** 17 &nbsp;|&nbsp; **VSAM Datasets:** 9 &nbsp;|&nbsp; **End-to-End Pipelines:** 2

---

## 1. Online Navigation Graph (CICS XCTL Chain)

The online application is a tree rooted at `COSGN00C`. Programs transfer control via `EXEC CICS XCTL` (one-way) or `EXEC CICS LINK` (call/return).

```
COSGN00C (Sign-On)
    │
    ├──[Admin]──► COADM01C (Admin Menu)
    │                 ├──► COUSR00C (User List)
    │                 │         ├──► COUSR01C (User Add)
    │                 │         ├──► COUSR02C (User Update)
    │                 │         └──► COUSR03C (User Delete)
    │                 ├──► COTRTLIC (Tran Type List — DB2)
    │                 │         └──► COTRTUPC (Tran Type Update — DB2)
    │                 └──[all menu options return to COADM01C via COMEN01C]
    │
    └──[User]───► COMEN01C (Main Menu — 11 options)
                      ├── 1 ► COACTVWC (Account View)
                      ├── 2 ► COACTUPC (Account Update)
                      ├── 3 ► COCRDLIC (Card List)
                      │           ├──► COCRDSLC (Card View)
                      │           └──► COCRDUPC (Card Update)
                      ├── 6 ► COTRN00C (Transaction List)
                      │           └──► COTRN01C (Transaction View)
                      ├── 8 ► COTRN02C (Transaction Add)
                      ├── 9 ► CORPT00C (Report Request)
                      ├──10 ► COBIL00C (Bill Payment)
                      └──11 ► COPAUS0C (Auth Summary — IMS)
                                  └──LINK──► COPAUS1C (Auth Detail)
```

### XCTL/LINK Edges (Complete)

| Source Program | Target Program | Mechanism | Condition |
|---------------|---------------|-----------|-----------|
| COSGN00C | COADM01C | XCTL | User type = 'A' (Admin) |
| COSGN00C | COMEN01C | XCTL | User type = 'U' (User) |
| COADM01C | COUSR00C | XCTL | Option 1 |
| COADM01C | COUSR01C | XCTL | Option 2 |
| COADM01C | COUSR02C | XCTL | Option 3 |
| COADM01C | COUSR03C | XCTL | Option 4 |
| COADM01C | COTRTLIC | XCTL | Option 5 |
| COADM01C | COTRTUPC | XCTL | Option 6 |
| COMEN01C | COACTVWC | XCTL | Option 1 |
| COMEN01C | COACTUPC | XCTL | Option 2 |
| COMEN01C | COCRDLIC | XCTL | Option 3 |
| COMEN01C | COCRDSLC | XCTL | Option 4 |
| COMEN01C | COCRDUPC | XCTL | Option 5 |
| COMEN01C | COTRN00C | XCTL | Option 6 |
| COMEN01C | COTRN01C | XCTL | Option 7 |
| COMEN01C | COTRN02C | XCTL | Option 8 |
| COMEN01C | CORPT00C | XCTL | Option 9 |
| COMEN01C | COBIL00C | XCTL | Option 10 |
| COMEN01C | COPAUS0C | XCTL | Option 11 |
| COCRDLIC | COCRDSLC | XCTL | Select card |
| COCRDLIC | COCRDUPC | XCTL | Update card |
| COTRN00C | COTRN01C | XCTL | Select transaction |
| COPAUS0C | COPAUS1C | LINK | View auth detail |
| COTRTLIC | COTRTUPC | XCTL | Update transaction type |
| All CICS programs | COMEN01C / COADM01C | XCTL | PF3 (return to menu) |

---

## 2. Batch CALL Graph

### Direct CALLs (CALL 'program')

```
CBSTM03A ──CALL──► CBSTM03B   (13 invocations: open/read/close file I/O delegation)

CBACT01C ──CALL──► COBDATFT   (date formatting utility)
           CALL──► CEE3ABD    (LE abend handler)

CBACT02C ──CALL──► CEE3ABD
CBACT03C ──CALL──► CEE3ABD
CBACT04C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD
CBTRN01C ──CALL──► CEE3ABD
CBTRN02C ──CALL──► CEE3ABD
CBTRN03C ──CALL──► CEE3ABD
CBEXPORT ──CALL──► CEE3ABD
CBIMPORT ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT    (assembler wait macro)

COTRN02C ──CALL──► CSUTLDTC   (date validation; online→batch utility call)
CORPT00C ──CALL──► CSUTLDTC
CSUTLDTC ──CALL──► CEEDAYS    (LE date conversion intrinsic)

COPAUA0C ──CALL──► MQOPEN, MQGET, MQPUT1, MQCLOSE   (MQ API)
COACCT01 ──CALL──► MQOPEN, MQGET, MQPUT, MQCLOSE     (MQ API)
CODATE01 ──CALL──► MQOPEN, MQGET, MQPUT, MQCLOSE     (MQ API)

PAUDBLOD ──CALL──► CBLTDLI    (IMS DL/I API: ISRT, GU)
PAUDBUNL ──CALL──► CBLTDLI    (IMS DL/I API: GN, GNP)
DBUNLDGS ──CALL──► CBLTDLI    (IMS DL/I API: GN, GNP, ISRT)
```

### Inline COPY (Procedure Division)

| Using Program | COPY Statement | Purpose |
|--------------|---------------|---------|
| COACTUPC.cbl | COPY CSSETATY REPLACING (×38) | Set field attributes dynamically |
| COACTUPC.cbl | COPY CSUTLDPY | Inline date validation procedures |
| COACTVWC.cbl | COPY CSSTRPFY | String processing procedures |
| COCRDLIC.cbl | COPY CSSTRPFY | String processing procedures |
| COCRDSLC.cbl | COPY CSSTRPFY | String processing procedures |
| COCRDUPC.cbl | COPY CSSTRPFY | String processing procedures |
| COTRTLIC.cbl | COPY CSSTRPFY | String processing procedures |
| COTRTLIC.cbl | COPY CSDB2RPY | DB2 error handling procedures |
| COTRTUPC.cbl | COPY CSSETATY REPLACING | Field attribute setting |
| COTRTUPC.cbl | COPY CSDB2RPY | DB2 error handling procedures |

---

## 3. Dataset Lineage (VSAM & Sequential)

### 3.1 Core VSAM Files — Read/Write Access Map

| Dataset (Short Name) | DSN | Programs That READ | Programs That WRITE/REWRITE |
|---------------------|-----|-------------------|---------------------------|
| **ACCTFILE** | AWS.M2.CARDDEMO.ACCTDATA.VSAM.KSDS | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBEXPORT, CBSTM03B, COACTUPC, COACTVWC, COTRN02C, COBIL00C, COPAUA0C, COPAUS0C | CBACT04C (REWRITE), CBTRN02C (REWRITE), COACTUPC (REWRITE), COBIL00C (REWRITE) |
| **CARDFILE** | AWS.M2.CARDDEMO.CARDDATA.VSAM.KSDS | CBACT02C, CBTRN01C, CBEXPORT, COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COPAUS0C | COCRDUPC (REWRITE) |
| **CUSTFILE** | AWS.M2.CARDDEMO.CUSTDATA.VSAM.KSDS | CBCUS01C, CBTRN01C, CBEXPORT, CBSTM03B, COACTUPC, COACTVWC, COCRDSLC, COPAUA0C, COPAUS0C | COACTUPC (REWRITE) |
| **XREFFILE** | AWS.M2.CARDDEMO.CARDXREF.VSAM.KSDS | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, CBSTM03B, COTRN02C, COBIL00C, COPAUA0C | _(read-only)_ |
| **TRANSACT** | AWS.M2.CARDDEMO.TRANSACT.VSAM.KSDS | CBTRN03C, CBEXPORT, COTRN00C, COTRN01C | CBTRN02C (WRITE), CBACT04C (WRITE), COTRN02C (WRITE), COBIL00C (WRITE) |
| **TCATBALF** | AWS.M2.CARDDEMO.TCATBALF.VSAM.KSDS | CBACT04C, CBTRN02C | CBTRN02C (REWRITE), CBACT04C (I-O) |
| **DISCGRP** | AWS.M2.CARDDEMO.DISCGRP.VSAM.KSDS | CBACT04C | _(read-only)_ |
| **TRANTYPE** | AWS.M2.CARDDEMO.TRANTYPE.VSAM.KSDS | CBTRN03C | _(read-only; DB2 in sub-app)_ |
| **TRANCATG** | AWS.M2.CARDDEMO.TRANCATG.VSAM.KSDS | CBTRN03C | _(read-only)_ |
| **USRSEC** | AWS.M2.CARDDEMO.USRSEC.VSAM.KSDS | COSGN00C, COUSR00C | COUSR01C (WRITE), COUSR02C (REWRITE), COUSR03C (DELETE) |

### 3.2 Sequential / GDG Files

| Dataset | Producer | Consumer | Purpose |
|---------|----------|----------|---------|
| DALYTRAN.PS | External (POS/ATM) | CBTRN01C, CBTRN02C | Daily transaction input |
| DALYREJS(+1) | CBTRN02C | _(archive)_ | Rejected daily transactions |
| SYSTRAN(+1) | CBACT04C | COMBTRAN.jcl | System-generated interest transactions |
| TRANSACT.BKUP(+1) | TRANBKP.jcl | COMBTRAN.jcl, TRANREPT.jcl | Transaction master backup |
| TRANSACT.COMBINED(+1) | COMBTRAN.jcl (SORT) | _(REPRO to VSAM)_ | Merged transactions |
| TRANSACT.DALY(+1) | TRANREPT.jcl (SORT) | CBTRN03C | Sorted daily transactions |
| STATEMNT.PS | CBSTM03A | TXT2PDF1.JCL | Text statement output |
| STATEMNT.HTML | CBSTM03A | _(web delivery)_ | HTML statement output |
| TCATBALF.REPT | PRTCATBL.jcl (SORT) | _(print)_ | Category balance report |
| EXPORT.DATA | CBEXPORT | CBIMPORT | Data migration file |
| IMPORT.ERRORS | CBIMPORT | _(review)_ | Import validation errors |
| PAUTDB.ROOT.FILEO | PAUDBUNL/UNLDPADB | PAUDBLOD/LOADPADB | IMS root segment flat file |
| PAUTDB.CHILD.FILEO | PAUDBUNL/UNLDPADB | PAUDBLOD/LOADPADB | IMS child segment flat file |
| PAUTDB.ROOT.GSAM | DBUNLDGS | _(analysis)_ | GSAM root segment extract |
| PAUTDB.CHILD.GSAM | DBUNLDGS | _(analysis)_ | GSAM child segment extract |

---

## 4. JCL → Program → Dataset Flow

### 4.1 VSAM Setup Pipeline

Each data setup JCL follows the same pattern:

```
[VSAM-SETUP.jcl]
  STEP05: IDCAMS DELETE CLUSTER    ──► Remove old VSAM
  STEP10: IDCAMS DEFINE CLUSTER    ──► Create new VSAM (KSDS)
  STEP15: IDCAMS REPRO             ──► Load from flat PS file
```

| JCL | Flat File Input | VSAM Output | Record Key |
|-----|----------------|-------------|-----------|
| ACCTFILE.jcl | ACCTDATA.PS | ACCTDATA.VSAM.KSDS | KEYS(11,0) |
| CARDFILE.jcl | CARDDATA.PS | CARDDATA.VSAM.KSDS | KEYS(16,0) + AIX |
| CUSTFILE.jcl | CUSTDATA.PS | CUSTDATA.VSAM.KSDS | _(standard)_ |
| XREFFILE.jcl | CARDXREF.PS | CARDXREF.VSAM.KSDS | + AIX + PATH |
| TRANFILE.jcl | DALYTRAN.PS.INIT | TRANSACT.VSAM.KSDS | + IDCAMS LISTCAT |
| TCATBALF.jcl | TCATBALF.PS | TCATBALF.VSAM.KSDS | _(standard)_ |
| DISCGRP.jcl | DISCGRP.PS | DISCGRP.VSAM.KSDS | _(standard)_ |
| TRANTYPE.jcl | TRANTYPE.PS | TRANTYPE.VSAM.KSDS | _(standard)_ |
| TRANCATG.jcl | TRANCATG.PS | TRANCATG.VSAM.KSDS | _(standard)_ |

### 4.2 Batch Processing Job → Program → Dataset

```
┌───────────────┐     ┌──────────────┐     ┌──────────────────────┐
│  POSTTRAN.jcl │────►│  CBTRN02C    │────►│ Reads:  DALYTRAN.PS  │
│  STEP15       │     │              │     │         XREFFILE      │
│               │     │              │     │         ACCTFILE       │
│               │     │              │     │         TCATBALF       │
│               │     │              │     │ Writes: TRANSACT       │
│               │     │              │     │         DALYREJS(+1)   │
│               │     │              │     │ Rwrites: ACCTFILE      │
│               │     │              │     │          TCATBALF       │
└───────────────┘     └──────────────┘     └──────────────────────┘

┌───────────────┐     ┌──────────────┐     ┌──────────────────────┐
│  INTCALC.jcl  │────►│  CBACT04C    │────►│ Reads:  TCATBALF     │
│  STEP15       │     │  PARM=date   │     │         XREFFILE      │
│               │     │              │     │         DISCGRP        │
│               │     │              │     │         ACCTFILE       │
│               │     │              │     │ Writes: SYSTRAN(+1)   │
│               │     │              │     │ Rwrites: ACCTFILE      │
└───────────────┘     └──────────────┘     └──────────────────────┘

┌───────────────┐     ┌──────────────┐     ┌──────────────────────┐
│ CREASTMT.JCL  │────►│ SORT→IDCAMS  │────►│ TRANSACT.VSAM → TRXFL│
│ STEP010-030   │     │  (prep)      │     │ TRXFL.SEQ→TRXFL.VSAM │
│               │     │              │     │ Delete old STMTNT/HTML│
│ STEP040+      │     │ CBSTM03A     │     │ Reads: XREF,CUST,ACCT│
│               │     │  →CBSTM03B   │     │        TRNXFILE       │
│               │     │              │     │ Writes: STATEMNT.PS   │
│               │     │              │     │         STATEMNT.HTML  │
└───────────────┘     └──────────────┘     └──────────────────────┘

┌───────────────┐     ┌──────────────┐     ┌──────────────────────┐
│ TRANREPT.jcl  │────►│ REPROC→SORT  │────►│ TRANSACT→BKUP(+1)   │
│ STEP05R-10R   │     │  →CBTRN03C   │     │ →DALY(+1) (sorted)  │
│               │     │              │     │ Reads: TRANTYPE       │
│               │     │              │     │        TRANCATG       │
│               │     │              │     │        XREFFILE        │
│               │     │              │     │ Writes: REPTFILE(+1)  │
└───────────────┘     └──────────────┘     └──────────────────────┘
```

---

## 5. End-to-End Daily Batch Pipeline

The main daily batch flow runs sequentially:

```
 ══════════════════════════════════════════════════════════════
 ║                    DAILY BATCH PIPELINE                    ║
 ══════════════════════════════════════════════════════════════

     ┌─────────────────────────────────────────────────────┐
     │ 1. POSTTRAN (CBTRN02C)                              │
     │    Input:  DALYTRAN.PS (daily transaction feed)      │
     │    Process: Validate card→XREF→ACCT; post to master │
     │    Output:  TRANSACT.VSAM.KSDS (writes)             │
     │             ACCTFILE (rewrite balance)                │
     │             TCATBALF (rewrite category balance)      │
     │             DALYREJS (rejected transactions)         │
     └──────────────────────┬──────────────────────────────┘
                            │
                            ▼
     ┌─────────────────────────────────────────────────────┐
     │ 2. INTCALC (CBACT04C)                               │
     │    Input:  TCATBALF + XREFFILE + DISCGRP + ACCTFILE │
     │    Process: Calculate interest per account/type/cat  │
     │    Output:  SYSTRAN(+1) (interest transactions)     │
     │             ACCTFILE (rewrite with interest charges) │
     └──────────────────────┬──────────────────────────────┘
                            │
     ┌──────────────────────┴──────────────────────────────┐
     │ 2a. COMBTRAN (SORT+IDCAMS)                          │
     │     Merges TRANSACT.BKUP(0) + SYSTRAN(0) → COMBINED│
     │     REPRO COMBINED → TRANSACT.VSAM.KSDS             │
     └──────────────────────┬──────────────────────────────┘
                            │
                            ▼
     ┌─────────────────────────────────────────────────────┐
     │ 3. CREASTMT (CBSTM03A → CBSTM03B)                  │
     │    Input:  XREFFILE → CUSTFILE → ACCTFILE → TRNXFL │
     │    Process: Generate per-customer statement          │
     │    Output:  STATEMNT.PS (text) + STATEMNT.HTML      │
     └──────────────────────┬──────────────────────────────┘
                            │
                            ▼
     ┌─────────────────────────────────────────────────────┐
     │ 4. TRANRPT (CBTRN03C)                               │
     │    Input:  TRANSACT + XREFFILE + TRANTYPE + TRANCATG│
     │    Process: Generate daily transaction report        │
     │    Output:  REPTFILE(+1) (daily report)             │
     └──────────────────────┬──────────────────────────────┘
                            │
                            ▼
     ┌─────────────────────────────────────────────────────┐
     │ 5. TXT2PDF (TXT2PDF1.JCL)                           │
     │    Input:  STATEMNT.PS                               │
     │    Process: Convert text statement to PDF            │
     │    Output:  PDF file                                 │
     └──────────────────────┬──────────────────────────────┘
                            │
                            ▼
     ┌─────────────────────────────────────────────────────┐
     │ 6. TRANBKP (TRANBKP.jcl)                            │
     │    Input:  TRANSACT.VSAM.KSDS                        │
     │    Process: Backup + delete/redefine transaction VSAM│
     │    Output:  TRANSACT.BKUP(+1)                       │
     └─────────────────────────────────────────────────────┘
```

### Pipeline Data Dependencies

| Step | Job | Program | Depends On | Produces |
|------|-----|---------|-----------|----------|
| 1 | POSTTRAN | CBTRN02C | DALYTRAN.PS (external) | Updated TRANSACT, ACCTFILE, TCATBALF |
| 2 | INTCALC | CBACT04C | ACCTFILE (from step 1) | Updated ACCTFILE, SYSTRAN |
| 2a | COMBTRAN | SORT+IDCAMS | SYSTRAN (from step 2) | Updated TRANSACT.VSAM |
| 3 | CREASTMT | CBSTM03A/B | TRANSACT (from step 2a) | STATEMNT.PS, .HTML |
| 4 | TRANREPT | CBTRN03C | TRANSACT (from step 2a) | REPTFILE |
| 5 | TXT2PDF1 | IKJEFT1B | STATEMNT.PS (from step 3) | PDF |
| 6 | TRANBKP | IDCAMS | TRANSACT.VSAM | TRANSACT.BKUP(+1) |

---

## 6. Data Export/Import Pipeline

```
                    ┌─────────────────┐
    5 VSAM files ──►│ CBEXPORT.jcl    │──► EXPORT.DATA
    (CUST,ACCT,     │ (CBEXPORT pgm)  │    (unified sequential)
     XREF,TRAN,     └─────────────────┘
     CARD)                                       │
                                                 ▼
                    ┌─────────────────┐    ┌───────────┐
    Individual   ◄──│ CBIMPORT.jcl    │◄───│EXPORT.DATA│
    output files    │ (CBIMPORT pgm)  │    └───────────┘
    (.IMPORT)       └─────────────────┘
    + ERRORS file
```

---

## 7. IMS Database Pipeline (Authorization Sub-App)

```
    ┌────────────────────────────────────────────────┐
    │     IMS Pending Authorization Database          │
    │  Root Segment: CIPAUSMY (by card number)        │
    │  Child Segment: CIPAUDTY (auth details)         │
    └───────────┬────────────────────┬───────────────┘
                │                    │
     ┌──────────┴────────┐ ┌────────┴──────────┐
     │ Online Access      │ │ Batch Maintenance │
     │ COPAUA0C (auth)    │ │ CBPAUP0C (purge)  │
     │ COPAUS0C (browse)  │ │ PAUDBLOD (load)   │
     │ COPAUS1C (detail)  │ │ PAUDBUNL (unload) │
     │ COPAUS2C (fraud→DB2)│ │ DBUNLDGS (GSAM)  │
     └────────────────────┘ └───────────────────┘
                │
                ▼
     ┌──────────────────────┐
     │ MQ Interface          │
     │ COPAUA0C reads from   │
     │   AUTH.REQUEST queue   │
     │ COPAUA0C writes to    │
     │   AUTH.REPLY queue     │
     └──────────────────────┘
```

---

## 8. Shared Utility Dependencies

```
┌─────────────────────────────────────────────────────────────┐
│ CEE3ABD (LE Abend Handler) — called by ALL batch programs    │
│   ◄── CBACT01C, CBACT02C, CBACT03C, CBACT04C, CBCUS01C,   │
│       CBTRN01C, CBTRN02C, CBTRN03C, CBSTM03A, CBEXPORT,   │
│       CBIMPORT                                               │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ CSUTLDTC (Date Utility) — callable batch program             │
│   ◄── COTRN02C (CICS, validates transaction date)           │
│   ◄── CORPT00C (CICS, validates report date range)          │
│   Internally CALLs: CEEDAYS (LE date conversion)            │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ CSUTLDPY (Inline Date Validation via COPY REPLACING)         │
│   ◄── COACTUPC (validates all date fields)                  │
│   ◄── COTRTUPC (validates transaction type dates)           │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ CSSETATY (BMS Attribute Setting via COPY REPLACING)          │
│   ◄── COACTUPC (38 invocations)                             │
│   ◄── COTRTUPC (multiple invocations)                       │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ CSSTRPFY (String Processing via COPY)                        │
│   ◄── COACTVWC, COCRDLIC, COCRDSLC, COCRDUPC, COTRTLIC     │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│ MQ API (MQOPEN/MQGET/MQPUT/MQCLOSE)                          │
│   ◄── COPAUA0C (authorization decision)                     │
│   ◄── COACCT01 (account inquiry)                            │
│   ◄── CODATE01 (date inquiry)                               │
└─────────────────────────────────────────────────────────────┘
```

---

## 9. Copybook Dependency Matrix (Programs × Copybooks)

Key data-structure copybooks and their consumers:

| Copybook | Used By (programs) | Count |
|----------|-------------------|-------|
| **COCOM01Y** (COMMAREA) | COADM01C, COMEN01C, COACTUPC, COACTVWC, COBIL00C, COCRDLIC, COCRDSLC, COCRDUPC, CORPT00C, COSGN00C, COTRN00C, COTRN01C, COTRN02C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COPAUS0C, COTRTLIC, COTRTUPC | **20** |
| **DFHAID** | All 21 CICS programs | **21** |
| **DFHBMSCA** | All 21 CICS programs | **21** |
| **CVACT01Y** (Account) | CBACT01C, CBACT04C, CBTRN01C, CBTRN02C, CBEXPORT, CBIMPORT, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C | **12** |
| **CVACT03Y** (XREF) | CBACT03C, CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, CBIMPORT, CBSTM03A, COACTUPC, COACTVWC, COBIL00C, COTRN02C, COPAUA0C, COPAUS0C | **14** |
| **CVCUS01Y** (Customer) | CBCUS01C, CBTRN01C, CBEXPORT, CBIMPORT, COACTUPC, COACTVWC, COCRDSLC, COCRDUPC, COPAUA0C, COPAUS0C | **10** |
| **CVTRA05Y** (Transaction) | CBACT04C, CBTRN01C, CBTRN02C, CBTRN03C, CBEXPORT, CBIMPORT, COBIL00C, COTRN00C, COTRN01C, COTRN02C, CORPT00C | **11** |
| **COTTL01Y** (Titles) | All 21 CICS programs | **21** |
| **CSDAT01Y** (Date/Time) | All 21 CICS programs | **21** |
| **CSMSG01Y** (Messages) | All 21 CICS programs | **21** |
