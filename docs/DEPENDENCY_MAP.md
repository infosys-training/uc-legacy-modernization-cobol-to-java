# DEPENDENCY MAP — CardDemo COBOL Estate

> Call graphs, dataset lineage, and batch pipeline flow extracted from source analysis

---

## 1. Online Call Graph (CICS XCTL / LINK Chains)

### 1.1 Navigation Tree

```
COSGN00C (Sign-On — Entry Point)
│
├─── [Admin User: CDEMO-USRTYP-ADMIN = 'A'] ──► COADM01C (Admin Menu)
│    │
│    ├── Option 1 ──XCTL──► COUSR00C (User List)
│    │                        ├──XCTL──► COUSR02C (User Update)
│    │                        └──XCTL──► COUSR03C (User Delete)
│    │
│    ├── Option 2 ──XCTL──► COUSR01C (User Add)
│    │
│    ├── Option 3 ──XCTL──► COTRTLIC (Tran Type List — DB2)
│    │
│    └── Option 4 ──XCTL──► COTRTUPC (Tran Type Update — DB2)
│
└─── [Regular User: CDEMO-USRTYP-USER = 'U'] ──► COMEN01C (Main Menu)
     │
     ├── Option 1  ──XCTL──► COACTVWC (Account View)
     ├── Option 2  ──XCTL──► COACTUPC (Account Update — 4,236 LOC)
     ├── Option 3  ──XCTL──► COCRDLIC (Card List)
     │                        ├──XCTL──► COCRDSLC (Card Detail View)
     │                        └──XCTL──► COCRDUPC (Card Update)
     ├── Option 4  ──XCTL──► COBIL00C (Bill Payment)
     ├── Option 5  ──XCTL──► COTRN00C (Transaction List)
     │                        └──XCTL──► COTRN01C (Transaction View)
     ├── Option 6  ──XCTL──► COTRN02C (Transaction Add)
     ├── Option 7  ──XCTL──► CORPT00C (Report Request)
     │                        └──TDQ──► INTRDRJ1/J2.JCL (batch submit)
     ├── Option 8  ──XCTL──► COPAUS0C (Auth Summary — IMS)
     │                        ├──XCTL──► COPAUS1C (Auth Detail — IMS)
     │                        │           └──LINK──► COPAUS2C (Fraud Mark — DB2)
     │                        └── (paginated IMS browse)
     ├── Option 9  ──XCTL──► COACCT01 (Account Inquiry — MQ)
     ├── Option 10 ──XCTL──► CODATE01 (Date Inquiry — MQ)
     └── Option 11 ──XCTL──► COPAUA0C (Auth Decision — MQ/IMS)
```

### 1.2 Inter-Program CALL Dependencies (Batch)

```
CBACT01C ──CALL──► COBDATFT     (assembler date formatter)

CBSTM03A ──CALL──► CBSTM03B    (file I/O subroutine — open/read/write/close)

CBACT02C ──CALL──► CEE3ABD     (LE abnormal termination handler)
CBACT03C ──CALL──► CEE3ABD
CBCUS01C ──CALL──► CEE3ABD

COBSWAIT ──CALL──► MVSWAIT     (assembler wait routine)

CSUTLDTC ──CALL──► CEEDAYS    (LE date conversion API)

COPAUA0C ──CALL──► MQOPEN     (MQ API — open queue)
         ──CALL──► MQGET      (MQ API — read message)
         ──CALL──► MQPUT1     (MQ API — write response)

DBUNLDGS ──CALL──► CBLTDLI    (IMS DL/I call interface — GN, GNP, ISRT)
```

### 1.3 Dependency Matrix (Adjacency)

| Caller → | Called Program | Mechanism | Notes |
|----------|---------------|-----------|-------|
| COSGN00C | COADM01C | XCTL | Admin path |
| COSGN00C | COMEN01C | XCTL | User path |
| COADM01C | COUSR00C, COUSR01C, COTRTLIC, COTRTUPC | XCTL | Admin menu options |
| COMEN01C | COACTVWC, COACTUPC, COCRDLIC, COTRN00C, COTRN02C, CORPT00C, COBIL00C, COPAUS0C, COACCT01, CODATE01, COPAUA0C | XCTL | Main menu (11 targets) |
| COUSR00C | COUSR02C, COUSR03C | XCTL | From user list |
| COCRDLIC | COCRDSLC, COCRDUPC | XCTL | From card list |
| COTRN00C | COTRN01C | XCTL | From txn list |
| COPAUS0C | COPAUS1C | XCTL | Auth summary → detail |
| COPAUS1C | COPAUS2C | LINK | Detail → fraud marking |
| CORPT00C | INTRDRJ1/J2 | TDQ (internal reader) | Online → batch bridge |
| CBSTM03A | CBSTM03B | CALL | Statement generator → I/O sub |
| CBACT01C | COBDATFT | CALL | Date formatting |

---

## 2. Dataset Lineage

### 2.1 VSAM File → Program Access Map

```
┌──────────────────────────────────────────────────────────────────────────┐
│                        VSAM FILE ACCESS MAP                             │
├──────────────┬──────────────────────┬────────────────────────────────────┤
│ File (DD)    │ Writers              │ Readers                           │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ ACCTFILE     │ CBACT04C (REWRITE)   │ CBACT01C, COACTVWC, COACTUPC,    │
│ (Account)    │ COACTUPC (REWRITE)   │ COPAUA0C, COPAUS0C, COBIL00C,    │
│              │ COBIL00C (REWRITE)   │ CBTRN02C, CBSTM03A/B, CBEXPORT,  │
│              │ CBIMPORT (WRITE)     │ CBTRN01C, COTRN02C               │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ CARDFILE     │ COCRDUPC (REWRITE)   │ CBACT02C, COACTVWC, COCRDLIC,    │
│ (Card)       │ CBIMPORT (WRITE)     │ COCRDSLC, COCRDUPC, COPAUA0C,    │
│              │                      │ COPAUS0C, CBEXPORT               │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ CUSTFILE     │ CBIMPORT (WRITE)     │ CBCUS01C, COACTVWC, COACTUPC,    │
│ (Customer)   │                      │ COCRDSLC, COPAUA0C, COPAUS0C,    │
│              │                      │ CBSTM03A/B, CBEXPORT             │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ CARDXREF     │ CBIMPORT (WRITE)     │ CBACT03C, COACTVWC, COACTUPC,    │
│ (Xref)       │                      │ COBIL00C, COTRN02C, COPAUA0C,    │
│              │                      │ CBTRN01C, CBTRN02C, CBTRN03C,    │
│              │                      │ CBSTM03A/B, CBEXPORT             │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ TRANSACT     │ CBTRN02C (WRITE)     │ COTRN00C, COTRN01C, COBIL00C,    │
│ (Txn Master) │ COTRN02C (WRITE)     │ CBTRN03C, CBSTM03A/B, CBEXPORT  │
│              │ COBIL00C (WRITE)     │                                   │
│              │ CBIMPORT (WRITE)     │                                   │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ DALYTRAN     │ *(external feed)*    │ CBTRN01C, CBTRN02C               │
│ (Daily Txn)  │                      │                                   │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ TCATBALF     │ CBTRN02C (REWRITE)   │ CBACT04C                         │
│ (Cat Bal)    │                      │                                   │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ DISCGRP      │ *(reference data)*   │ CBACT04C                         │
│ (Disclosure) │                      │                                   │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ TRANTYPE     │ COBTUPDT (DB2 equiv) │ CBTRN03C                         │
│ (Tran Type)  │                      │                                   │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ TRANCATG     │ COBTUPDT (DB2 equiv) │ CBTRN03C                         │
│ (Tran Cat)   │                      │                                   │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ USRSEC       │ COUSR01C (WRITE)     │ COSGN00C, COUSR00C, COUSR02C,    │
│ (User Sec)   │ COUSR02C (REWRITE)   │ COUSR03C                         │
│              │ COUSR03C (DELETE)    │                                   │
├──────────────┼──────────────────────┼────────────────────────────────────┤
│ EXPORT       │ CBEXPORT (WRITE)     │ CBIMPORT                         │
│ (Migration)  │                      │                                   │
└──────────────┴──────────────────────┴────────────────────────────────────┘
```

### 2.2 JCL Job → File → Program Lineage

```
┌─────────────────┐      ┌───────────────┐      ┌─────────────────┐
│   JCL Job       │      │  VSAM File    │      │  COBOL Program  │
├─────────────────┤      ├───────────────┤      ├─────────────────┤
│ ACCTFILE.jcl    │─DEF─►│ ACCTFILE KSDS │◄─R───│ CBACT01C        │
│                 │─LOAD─│ (300-byte)    │◄─R/W─│ COACTUPC        │
│                 │      │               │◄─R/W─│ COBIL00C        │
│                 │      │               │◄─R/W─│ CBACT04C        │
├─────────────────┤      ├───────────────┤      ├─────────────────┤
│ CARDFILE.jcl    │─DEF─►│ CARDFILE KSDS │◄─R───│ COCRDLIC        │
│                 │─AIX─►│ + ACCTID AIX  │◄─R/W─│ COCRDUPC        │
├─────────────────┤      ├───────────────┤      ├─────────────────┤
│ CUSTFILE.jcl    │─DEF─►│ CUSTFILE KSDS │◄─R───│ COACTVWC et al. │
├─────────────────┤      ├───────────────┤      ├─────────────────┤
│ XREFFILE.jcl    │─DEF─►│ CARDXREF KSDS │◄─R───│ CBTRN02C et al. │
├─────────────────┤      ├───────────────┤      ├─────────────────┤
│ TRANFILE.jcl    │─DEF─►│ TRANSACT KSDS │◄─R/W─│ CBTRN02C        │
│ TRANIDX.jcl     │─AIX─►│ + AIX on ACCT │◄─R───│ COTRN00C et al. │
├─────────────────┤      ├───────────────┤      ├─────────────────┤
│ POSTTRAN.jcl    │─EXEC─│───────────────│──────│ CBTRN02C        │
│ INTCALC.jcl     │─EXEC─│───────────────│──────│ CBACT04C        │
│ CREASTMT.jcl    │─EXEC─│───────────────│──────│ CBSTM03A        │
│ TRANREPT.jcl    │─EXEC─│───────────────│──────│ CBTRN03C        │
├─────────────────┤      ├───────────────┤      ├─────────────────┤
│ CBEXPORT.jcl    │─EXEC─│ ALL 5 VSAM ──│──R──►│ CBEXPORT        │
│                 │      │ EXPORT FILE ──│──W──►│                 │
│ CBIMPORT.jcl    │─EXEC─│ EXPORT FILE ──│──R──►│ CBIMPORT        │
│                 │      │ 5 OUTPUT FILES│──W──►│                 │
├─────────────────┤      ├───────────────┤      ├─────────────────┤
│ COMBTRAN.jcl    │─SORT─│ TRANSACT.BKUP │      │ (SORT utility)  │
│                 │      │ + SYSTRAN     │      │                 │
│                 │─REPRO│ → TRANSACT    │      │                 │
└─────────────────┘      └───────────────┘      └─────────────────┘
```

---

## 3. End-to-End Batch Pipeline Flow

### 3.1 Daily Processing Cycle

```
                    ╔═══════════════════════════════════════════╗
                    ║        DAILY BATCH PIPELINE               ║
                    ╚═══════════════════════════════════════════╝

    ┌─────────────────────────────────────────────────────────────────┐
    │ STEP 1: Transaction Posting (POSTTRAN.jcl → CBTRN02C)          │
    │                                                                 │
    │   DALYTRAN ──READ──► CBTRN02C ──VALIDATE──► DALYREJS (rejects) │
    │   XREFFILE ──READ──┘    │                                       │
    │                         ├──WRITE──► TRANSACT (master)           │
    │                         ├──REWRITE─► ACCTFILE (update balance)  │
    │                         └──REWRITE─► TCATBALF (category bal)   │
    └────────────────────────────┬────────────────────────────────────┘
                                 │ depends on
                                 ▼
    ┌─────────────────────────────────────────────────────────────────┐
    │ STEP 2: Interest Calculation (INTCALC.jcl → CBACT04C)          │
    │                                                                 │
    │   TCATBALF ──READ──► CBACT04C ──COMPUTE──► ACCTFILE (REWRITE)  │
    │   XREFFILE ──READ──┘    │         interest   │                  │
    │   DISCGRP  ──READ──┘    └──WRITE──► TRANSACT (interest txn)    │
    │   ACCTFILE ──READ──┘                                            │
    └────────────────────────────┬────────────────────────────────────┘
                                 │ depends on
                                 ▼
    ┌─────────────────────────────────────────────────────────────────┐
    │ STEP 3: Statement Generation (CREASTMT.jcl → CBSTM03A/B)      │
    │                                                                 │
    │   XREFFILE ──READ──► CBSTM03A ──FORMAT──► STMT-FILE (text)    │
    │   CUSTFILE ──READ──┘    │                  HTML-FILE (html)    │
    │   ACCTFILE ──READ──┘    │                                      │
    │   TRANSACT ──READ──►  CBSTM03B (I/O sub via CALL)             │
    └────────────────────────────┬────────────────────────────────────┘
                                 │ depends on
                                 ▼
    ┌─────────────────────────────────────────────────────────────────┐
    │ STEP 4: Transaction Report (TRANREPT.jcl → CBTRN03C)           │
    │                                                                 │
    │   TRANSACT ──READ──► CBTRN03C ──FORMAT──► REPTFILE (report)   │
    │   XREFFILE ──READ──┘    │                                      │
    │   TRANTYPE ──READ──┘    │ (type/category desc lookups)         │
    │   TRANCATG ──READ──┘    │                                      │
    │   DATEPARM ──READ──┘    │ (date range filter)                  │
    └─────────────────────────────────────────────────────────────────┘
```

### 3.2 Periodic / On-Demand Jobs

```
    ╔═══════════════════════════════════════════════════════════════╗
    ║  WEEKLY: Data Export/Import (Branch Migration)               ║
    ╠═══════════════════════════════════════════════════════════════╣
    ║                                                               ║
    ║  CBEXPORT.jcl                    CBIMPORT.jcl                ║
    ║  ┌──────────┐                    ┌──────────┐                ║
    ║  │ CUSTFILE ─┐                   │ EXPORT ──┐                ║
    ║  │ ACCTFILE ─┤                   │   FILE   ├──► CUSTOUT     ║
    ║  │ XREFFILE ─┼──► EXPORT FILE    │          ├──► ACCTOUT     ║
    ║  │ TRANSACT ─┤                   │          ├──► XREFOUT     ║
    ║  │ CARDFILE ─┘                   │          ├──► TRNXOUT     ║
    ║  └──────────┘                    │          └──► ERROUT      ║
    ║                                  └──────────┘                ║
    ╚═══════════════════════════════════════════════════════════════╝

    ╔═══════════════════════════════════════════════════════════════╗
    ║  ON-DEMAND: Transaction Combine (End-of-Day Merge)           ║
    ╠═══════════════════════════════════════════════════════════════╣
    ║                                                               ║
    ║  COMBTRAN.jcl                                                ║
    ║  ┌────────────────────┐       ┌────────────────────┐         ║
    ║  │ TRANSACT.BKUP(0)  ─┐      │ TRANSACT.COMBINED  │         ║
    ║  │ SYSTRAN(0)        ─┼─SORT─►│ (+1 GDG generation)│         ║
    ║  └────────────────────┘       └────────┬───────────┘         ║
    ║                                        │ REPRO               ║
    ║                                        ▼                     ║
    ║                               TRANSACT.VSAM.KSDS             ║
    ╚═══════════════════════════════════════════════════════════════╝

    ╔═══════════════════════════════════════════════════════════════╗
    ║  IMS AUTHORIZATION SUBSYSTEM                                  ║
    ╠═══════════════════════════════════════════════════════════════╣
    ║                                                               ║
    ║  Online Flow:                                                 ║
    ║  MQ Request ──► COPAUA0C ──IMS/VSAM lookup──► MQ Response    ║
    ║                    │                                          ║
    ║  COPAUS0C ──IMS browse──► COPAUS1C ──IMS detail──►           ║
    ║                               └──LINK──► COPAUS2C (DB2 fraud)║
    ║                                                               ║
    ║  Batch Maintenance:                                           ║
    ║  CBPAUP0C ── IMS GN/GNP/DLET ── purge expired auths          ║
    ║  PAUDBLOD ── IMS ISRT ── load auth data                      ║
    ║  PAUDBUNL ── IMS GN/GNP ── unload auth data                 ║
    ║  DBUNLDGS ── IMS → GSAM ── unload to sequential file         ║
    ╚═══════════════════════════════════════════════════════════════╝
```

---

## 4. Copybook Dependency Heatmap

### Most-Referenced Copybooks (across all 44 programs)

| Rank | Copybook | Referenced By | Business Role |
|------|----------|-------------:|---------------|
| 1 | **COCOM01Y** | 22 programs | COMMAREA — inter-program data passing |
| 2 | **DFHAID** | 17 programs | CICS attention identifier constants |
| 3 | **DFHBMSCA** | 17 programs | BMS screen attribute bytes |
| 4 | **COTTL01Y** | 17 programs | Screen title area |
| 5 | **CSDAT01Y** | 17 programs | Date/time working storage |
| 6 | **CSMSG01Y** | 17 programs | Common messages |
| 7 | **CSUSR01Y** | 11 programs | User security record |
| 8 | **CVACT01Y** | 10 programs | Account record (300 bytes) |
| 9 | **CVACT03Y** | 10 programs | Card cross-reference record |
| 10 | **CVTRA05Y** | 8 programs | Transaction record (350 bytes) |
| 11 | **CVCUS01Y** | 8 programs | Customer record (500 bytes) |
| 12 | **CVACT02Y** | 7 programs | Card record (150 bytes) |
| 13 | **CIPAUSMY** | 7 programs | IMS auth summary segment |
| 14 | **CIPAUDTY** | 7 programs | IMS auth detail segment |
| 15 | **CSMSG02Y** | 6 programs | Extended messages |

---

## 5. Shared Data Coupling Summary

### High-Coupling Files (accessed by 10+ programs)

| File | Read Count | Write Count | Total Access | Coupling Risk |
|------|-----------|------------|-------------|---------------|
| ACCTFILE | 13 | 4 | 17 | **HIGH** — shared across batch + online |
| CARDXREF | 11 | 1 | 12 | **HIGH** — central linkage table |
| CARDFILE | 8 | 2 | 10 | **MEDIUM** — mostly read-only online |
| USRSEC | 5 | 3 | 8 | **LOW** — isolated to user mgmt |
| TRANSACT | 5 | 4 | 9 | **HIGH** — written by both batch + online |

### Modernization Implication

Programs sharing the same VSAM files must coordinate during migration:
- **ACCTFILE** is the most coupled dataset — 17 programs access it. Any schema change to accounts requires coordinated updates.
- **TRANSACT** has 4 writers (CBTRN02C, COTRN02C, COBIL00C, CBIMPORT) — potential write contention that database transactions will resolve.
- **CARDXREF** is read-only except for CBIMPORT — safe to migrate early as a read replica.
