# CardDemo — Dependency Map

Program-to-program call graph, program-to-dataset usage, JCL dataset lineage and the end-to-end batch pipeline.
Derived from `CALL`, `EXEC CICS XCTL/LINK`, `PERFORM`, `SELECT/ASSIGN`, `EXEC CICS READ/WRITE...FILE(...)`,
`EXEC SQL`, `EXEC DLI`, MQ calls, JCL `DD`/`EXEC` statements and the Control-M definitions.
Where a target is resolved at run time through a variable (`XCTL PROGRAM(CDEMO-TO-PROGRAM)`), the edge is taken
from the literal moved into that variable or from the menu tables `COMEN02Y` / `COADM02Y`.

Companion documents: [APPLICATION_INVENTORY.md](APPLICATION_INVENTORY.md), [DATA_DICTIONARY.md](DATA_DICTIONARY.md), [HOTSPOT_REPORT.md](HOTSPOT_REPORT.md).

## 1. Static CALL graph (COBOL `CALL` statements)

```
CBACT01C ──CALL──▶ COBDATFT   (asm/COBDATFT.asm — date reformat, parm block CODATECN)
CBACT01C ──CALL──▶ CEE3ABD    (LE abend)
CBACT02C ──CALL──▶ CEE3ABD
CBACT03C ──CALL──▶ CEE3ABD
CBCUS01C ──CALL──▶ CEE3ABD
CBACT04C ──CALL──▶ CEE3ABD
CBTRN01C ──CALL──▶ CEE3ABD
CBTRN02C ──CALL──▶ CEE3ABD
CBTRN03C ──CALL──▶ CEE3ABD
CBEXPORT ──CALL──▶ CEE3ABD
CBIMPORT ──CALL──▶ CEE3ABD
CBSTM03A ──CALL──▶ CBSTM03B   (13 call sites — file I/O dispatcher, only COBOL→COBOL static call)
CBSTM03A ──CALL──▶ CEE3ABD
COBSWAIT ──CALL──▶ MVSWAIT    (asm/MVSWAIT.asm — STIMER wait)
CSUTLDTC ──CALL──▶ CEEDAYS    (LE date → Lilian)
COTRN02C ──CALL──▶ CSUTLDTC
CORPT00C ──CALL──▶ CSUTLDTC
COACTUPC ─(COPY CSUTLDPY)─▶ CSUTLDTC   (call is inside the copied paragraphs EDIT-DATE-LE)
COPAUA0C ──CALL──▶ MQOPEN, MQGET, MQPUT1, MQCLOSE
COACCT01 ──CALL──▶ MQOPEN, MQGET, MQPUT,  MQCLOSE
CODATE01 ──CALL──▶ MQOPEN, MQGET, MQPUT,  MQCLOSE
PAUDBLOD ──CALL──▶ CBLTDLI    (ISRT)
PAUDBUNL ──CALL──▶ CBLTDLI    (GN, GNP)
DBUNLDGS ──CALL──▶ CBLTDLI    (GN, GNP, GSAM ISRT)
```

`EXEC DLI` (command-level, no CALL): `COPAUA0C`, `COPAUS0C`, `COPAUS1C`, `CBPAUP0C`.
`EXEC SQL`: `COPAUS2C`, `COTRTLIC`, `COTRTUPC`, `COBTUPDT`.

## 2. CICS control-transfer graph (XCTL / LINK)

```
COSGN00C (CC00)  sign-on
 ├─ XCTL ─▶ COADM01C (CA00)        when SEC-USR-TYPE = 'A'
 │           ├─ XCTL ─▶ COUSR00C (CU00) ─▶ COUSR02C (CU02) [select 'U']
 │           │                          └▶ COUSR03C (CU03) [select 'D']
 │           ├─ XCTL ─▶ COUSR01C (CU01)
 │           ├─ XCTL ─▶ COUSR02C (CU02)
 │           ├─ XCTL ─▶ COUSR03C (CU03)
 │           ├─ XCTL ─▶ COTRTLIC (CTLI) ─▶ COTRTUPC (CTTU)   [DB2]
 │           └─ XCTL ─▶ COTRTUPC (CTTU)
 └─ XCTL ─▶ COMEN01C (CM00)        when SEC-USR-TYPE = 'U'  (INQUIRE PROGRAM before XCTL)
             ├─ XCTL ─▶ COACTVWC (CAVW) ─▶ COCRDLIC / COCRDSLC / COCRDUPC (PF keys)
             ├─ XCTL ─▶ COACTUPC (CAUP)
             ├─ XCTL ─▶ COCRDLIC (CCLI) ─▶ COCRDSLC (CCDL) [select 'S'] / COCRDUPC (CCUP) [select 'U']
             ├─ XCTL ─▶ COCRDSLC (CCDL)
             ├─ XCTL ─▶ COCRDUPC (CCUP)
             ├─ XCTL ─▶ COTRN00C (CT00) ─▶ COTRN01C (CT01)
             ├─ XCTL ─▶ COTRN01C (CT01)
             ├─ XCTL ─▶ COTRN02C (CT02)
             ├─ XCTL ─▶ CORPT00C (CR00) ══ WRITEQ TD 'JOBS' ══▶ batch job TRNRPT00 (PROC TRANREPT → CBTRN03C)
             ├─ XCTL ─▶ COBIL00C (CB00)
             └─ XCTL ─▶ COPAUS0C (CPVS) ─▶ COPAUS1C (CPVD) ── LINK ──▶ COPAUS2C (DB2 AUTHFRDS)
```

Return edges: every screen program XCTLs back to its caller (`CDEMO-FROM-PROGRAM`) on PF3 and to `COSGN00C`
when the COMMAREA is empty (`EIBCALEN = 0`). `COPAUS1C → COPAUS2C` is the **only LINK** in the estate; every other
transfer is XCTL with `CARDDEMO-COMMAREA` (`COCOM01Y`) as the contract.

MQ-triggered CICS programs (no XCTL parents; started by MQ trigger monitor `CKTI` via `EXEC CICS RETRIEVE`):
`COPAUA0C` (CP00), `COACCT01` (CDRA), `CODATE01` (CDRD).

## 3. Program → dataset / table matrix

R = read, W = write/insert, U = rewrite/update, D = delete, B = browse (STARTBR/READNEXT/READPREV).

### 3.1 VSAM files

| Program | ACCTDATA | CUSTDATA | CARDDATA | CARDXREF (+AIX) | TRANSACT | TCATBALF | DISCGRP | TRANTYPE | TRANCATG | USRSEC |
|---|---|---|---|---|---|---|---|---|---|---|
| CBACT01C | R | | | | | | | | | |
| CBACT02C | | | R | | | | | | | |
| CBACT03C | | | | R | | | | | | |
| CBCUS01C | | R | | | | | | | | |
| CBACT04C | R U | | | R (AIX by acct) | W (to SYSTRAN GDG) | R | R | | | |
| CBTRN01C | R | R | R | R | R | | | | | |
| CBTRN02C | R U | | | R | W | R W U | | | | |
| CBTRN03C | | | | R | R (DALY extract) | | | R | R | |
| CBSTM03A/B | R | R | | R | R (TRXFL re-keyed copy) | | | | | |
| CBEXPORT | R | R | R | R | R | | | | | |
| CBIMPORT | W (ACCTOUT) | W (CUSTOUT) | W (CARDOUT) | W (XREFOUT) | W (TRNXOUT) | | | | | |
| COSGN00C | | | | | | | | | | R |
| COUSR00C | | | | | | | | | | B |
| COUSR01C | | | | | | | | | | W |
| COUSR02C | | | | | | | | | | R U |
| COUSR03C | | | | | | | | | | R D |
| COACTVWC | R | R | | R (CXACAIX) | | | | | | |
| COACTUPC | R U | R U | | R (CXACAIX) | | | | | | |
| COCRDLIC | | | B | | | | | | | |
| COCRDSLC | | | R | | | | | | | |
| COCRDUPC | | | R U | | | | | | | |
| COTRN00C | | | | | B | | | | | |
| COTRN01C | | | | | R | | | | | |
| COTRN02C | | | | R (CCXREF, CXACAIX) | B W | | | | | |
| COBIL00C | R U | | | R (CXACAIX) | B W | | | | | |
| COPAUA0C | R | R | | R (CCXREF) | | | | | | |
| COPAUS0C | R | R | | R (CXACAIX) | | | | | | |
| COACCT01 | R | | | | | | | | | |

Writers per file (for dual-write planning): **ACCTDATA** — CBACT04C, CBTRN02C, COACTUPC, COBIL00C (+CBIMPORT to a copy);
**CUSTDATA** — COACTUPC; **CARDDATA** — COCRDUPC; **CARDXREF** — none online (load only); **TRANSACT** — CBTRN02C, CBACT04C
(via COMBTRAN merge), COTRN02C, COBIL00C; **TCATBALF** — CBTRN02C; **USRSEC** — COUSR01C/02C/03C.

### 3.2 Sequential / GDG datasets

| Dataset (`AWS.M2.CARDDEMO.`) | Produced by | Consumed by |
|---|---|---|
| `DALYTRAN.PS` | external feed (sample in `app/data`) | POSTTRAN → CBTRN02C, CBTRN01C |
| `DALYREJS(+1)` GDG | CBTRN02C | manual review |
| `SYSTRAN(+1)` GDG | INTCALC → CBACT04C | COMBTRAN (SORT merge) |
| `TRANSACT.BKUP(+1)` GDG | TRANBKP / TRANREPT / PRTCATBL via PROC REPROC | COMBTRAN, TRANREPT SORT |
| `TRANSACT.COMBINED(+1)` | COMBTRAN SORT | COMBTRAN REPRO → TRANSACT.VSAM.KSDS |
| `TRANSACT.DALY(+1)` | TRANREPT SORT (date filtered) | CBTRN03C |
| `TRANREPT(+1)` GDG | CBTRN03C | printing / TXT2PDF |
| `DATEPARM` | TRANREPT job (instream) / CORPT00C-generated JCL | CBTRN03C |
| `TRXFL.SEQ` → `TRXFL.VSAM.KSDS` | CREASTMT SORT + REPRO | CBSTM03A/B |
| `STATEMNT.PS`, `STATEMNT.HTML` | CBSTM03A | TXT2PDF1 |
| `TCATBALF.BKUP(+1)`, `TCATBALF.REPT` | PRTCATBL | reporting |
| `ACCTDATA.PSCOMP/.ARRYPS/.VBPS` | READACCT → CBACT01C | downstream extracts |
| `EXPORT.DATA` | CBEXPORT | CBIMPORT |
| `*.IMPORT`, `IMPORT.ERRORS` | CBIMPORT | REPRO into KSDS (manual) |
| `TRANTYPE.PS`, `TRANCATG.PS` | TRANEXTR (DB2 DSNTIAUL unload) | TRANTYPE.jcl / TRANCATG.jcl REPRO → KSDS |
| `PAUTDB.ROOT.FILEO`, `PAUTDB.CHILD.FILEO` | UNLDPADB → PAUDBUNL | LOADPADB → PAUDBLOD |
| `PAUTDB.ROOT.GSAM`, `PAUTDB.CHILD.GSAM` | UNLDGSAM → DBUNLDGS | archival |

### 3.3 DB2 tables and IMS segments

| Store | Readers | Writers |
|---|---|---|
| `CARDDEMO.TRANSACTION_TYPE` | COTRTLIC (cursors), COTRTUPC, TRANEXTR (DSNTIAUL) | COTRTLIC (D,U), COTRTUPC (I,U,D), COBTUPDT (I,U,D), CREADB21 (load) |
| `CARDDEMO.TRANSACTION_TYPE_CATEGORY` | COTRTLIC (SELECT COUNT before delete), COTRTUPC, TRANEXTR | CREADB21 (load) |
| `CARDDEMO.AUTHFRDS` | — | COPAUS2C (I,U) |
| IMS `PAUTSUM0` (root) | COPAUS0C, COPAUS1C, COPAUA0C (GU), CBPAUP0C (GN), PAUDBUNL, DBUNLDGS | COPAUA0C (ISRT/REPL), CBPAUP0C (DLET), PAUDBLOD (ISRT) |
| IMS `PAUTDTL1` (child) | COPAUS0C/1C (GNP), CBPAUP0C (GNP) | COPAUA0C (ISRT), COPAUS1C (REPL fraud flag), CBPAUP0C (DLET), PAUDBLOD |
| MQ queues | COPAUA0C (request/reply-to), COACCT01, CODATE01 | same programs (reply / error queue) |
| TD queues | `JOBS` (CORPT00C — internal reader), `CSSL` (COPAUA0C error log) | |

## 4. JCL dataset lineage and end-to-end batch pipeline

### 4.1 Daily / on-demand pipeline (business order)

```
                 DALYTRAN.PS (external POS feed)
                        │
  CLOSEFIL (CEMT CLOSE CICS files) ─────────────────────────────┐
                        │                                       │
  [1] POSTTRAN ─ CBTRN02C ─┬─▶ TRANSACT.VSAM.KSDS  (W)          │
                           ├─▶ ACCTDATA.VSAM.KSDS  (U cycle credit/debit)
                           ├─▶ TCATBALF.VSAM.KSDS  (W/U)        │
                           └─▶ DALYREJS(+1)        (rejects 100–103)
                        │                                       │
  [2] INTCALC ─ CBACT04C ──┬─▶ SYSTRAN(+1)   (interest txns)    │  ← monthly (Control-M MONTHLY folder)
        (reads TCATBALF, DISCGRP, XREF AIX) └─▶ ACCTDATA (U balance)
                        │
  [2b] COMBTRAN ─ SORT(TRANSACT.BKUP(0) + SYSTRAN(0)) ─▶ TRANSACT.COMBINED(+1) ─ REPRO ─▶ TRANSACT.VSAM.KSDS
                        │
  [3] CREASTMT ─ SORT TRANSACT → TRXFL.SEQ → REPRO → TRXFL.VSAM.KSDS
                 CBSTM03A (+CBSTM03B: TRXFL, XREF, ACCT, CUST) ─▶ STATEMNT.PS, STATEMNT.HTML ─▶ TXT2PDF1
                        │
  [4] TRANREPT ─ REPROC(TRANSACT → TRANSACT.BKUP(+1)) → SORT by TRAN-PROC-TS range → TRANSACT.DALY(+1)
                 CBTRN03C (+CARDXREF, TRANTYPE, TRANCATG, DATEPARM) ─▶ TRANREPT(+1)
                        │        ▲
                        │        └── also submitted on demand from CICS by CORPT00C (TRNRPT00 job → PROC TRANREPT)
  WAITSTEP (COBSWAIT) → OPENFIL (CEMT OPEN) ◀──────────────────────┘
```

Program-to-dataset dependencies impose the ordering: `INTCALC` needs the `TCATBALF` balances written by `POSTTRAN`;
`COMBTRAN` needs both `TRANSACT.BKUP(0)` (from `TRANBKP`/`TRANREPT` REPROC) and `SYSTRAN(0)` (from `INTCALC`);
`CREASTMT` and `TRANREPT` read the fully posted `TRANSACT` KSDS.

### 4.2 Scheduler folders (`CardDemo.controlm`, mirrored in `CardDemo.ca7`)

| Folder / cycle | Chain |
|---|---|
| DAILY — TransactionBackup | `CLOSEFIL` → `TRANBKP` → `WAITSTEP` → `OPENFIL` |
| WEEKLY (Sat) — TransactionTypesDBRefresh | `MNTTRDB2` (COBTUPDT updates DB2) → `TRANEXTR` (unload DB2 → TRANTYPE.PS / TRANCATG.PS) → sub-folder DisclosureGroupsRefresh: `CLOSEFIL` → `DISCGRP` → `WAITSTEP` → `OPENFIL` |
| MONTHLY — InterestCalculation | `CLOSEFIL` → `INTCALC` → `COMBTRAN` → `WAITSTEP` → `OPENFIL` |

`POSTTRAN`, `CREASTMT`, `TRANREPT` and the setup jobs are not in the Control-M export and are run ad hoc / from CICS (`CORPT00C`).

### 4.3 One-time / setup lineage

```
app/data/EBCDIC/*.PS ──REPRO (ACCTFILE, CARDFILE, CUSTFILE, XREFFILE, TRANFILE, TRANTYPE, TRANCATG, DISCGRP, TCATBALF, DUSRSECJ)──▶ *.VSAM.KSDS
CARDFILE / XREFFILE / TRANFILE / TRANIDX ──▶ alternate indexes CARDDATA.VSAM.AIX (by acct), CARDXREF.VSAM.AIX (by acct → CXACAIX), TRANSACT.VSAM.AIX (by proc-ts)
DEFGDGB / DEFGDGD / DALYREJS / REPTFILE ──▶ GDG bases (TRANSACT.BKUP, SYSTRAN, DALYREJS, TRANREPT, TRANTYPE.BKUP ...)
CREADB21 ──▶ DB2 database, TRANSACTION_TYPE + _CATEGORY tables, initial rows (ctl/DB2LTTYP, DB2LTCAT)
LOADPADB (PAUDBLOD) ◀── PAUTDB.ROOT.FILEO / CHILD.FILEO ◀── UNLDPADB (PAUDBUNL) ; UNLDGSAM (DBUNLDGS) ──▶ GSAM ; DBPAUTP0 ──▶ IMS HD unload
CBADMCDJ (DFHCSDUP) ──▶ CICS CSD groups (programs, transactions, files, mapsets)
CBEXPORT.jcl ──▶ EXPORT.DATA ──▶ CBIMPORT.jcl ──▶ *.IMPORT + IMPORT.ERRORS
```

## 5. Copybook fan-in (shared contracts)

| Copybook | Included by (count) | Why it matters |
|---|---|---|
| `COCOM01Y` (COMMAREA) | 21 online programs | Single inter-screen contract → DTO for every service call |
| `CVACT01Y` (Account) | 14 | Widest data contract; ACCTDATA has 4 online/batch writers |
| `CVACT03Y` (Xref) | 14 | Card↔account↔customer join used by nearly every flow |
| `CVTRA05Y` (Transaction) | 11 | Shared by batch posting, reporting, online add/list, bill pay, export |
| `CVCUS01Y` / `CUSTREC` | 10 + 1 | Two divergent copies of the same record |
| `CVACT02Y` (Card) | 10 | |
| `CSUSR01Y` (User) | 14 | Also carries plaintext password |
| `CSDAT01Y`, `CSMSG01Y`, `COTTL01Y` (+ CICS `DFHAID`, `DFHBMSCA`) | 21 each | Screen boilerplate, drops out when BMS is replaced |
| `CIPAUDTY` / `CIPAUSMY` | 8 / 7 | Authorization IMS segments — all auth programs |
| `CSLKPCDY`, `CSUTLDPY`/`CSUTLDWY`, `CSSETATY` | COACTUPC (CSUTLDPY also COCRDUPC/COTRN02C indirectly via CSUTLDTC) | Validation rule library to extract into a shared Java module |

## 6. External / runtime dependencies

| Dependency | Type | Used by | Java replacement direction |
|---|---|---|---|
| CICS (BMS SEND/RECEIVE, XCTL/LINK, file control, TD queues, SYNCPOINT) | TP monitor | 26 programs | Spring MVC/REST + UI; Spring transactions |
| VSAM KSDS/ESDS/RRDS + AIX paths | Data store | all batch + 20 online | Relational tables + secondary indexes |
| DB2 (`TRANSACTION_TYPE`, `_CATEGORY`, `AUTHFRDS`) | RDBMS | 4 programs + 3 JCL | JPA / JDBC (near-direct) |
| IMS DB `DBPAUTP0` (+index `DBPAUTX0`, GSAM) | Hierarchical DB | 7 programs + 5 JCL | 2 relational tables (summary/detail) |
| IBM MQ (`CMQ*` copybooks, trigger monitor) | Messaging | 3 programs | JMS / SQS adapter |
| LE runtime `CEE3ABD`, `CEEDAYS` | Runtime | 11 batch + CSUTLDTC | Exceptions; `java.time` |
| Assembler `COBDATFT`, `MVSWAIT` | Custom | CBACT01C, COBSWAIT | `DateTimeFormatter`; scheduler wait |
| DFSORT/ICETOOL, IDCAMS, IEBGENER, IEFBR14, SDSF (CEMT), FTP, TXT2PDF REXX, DSNTIAD/DSNTEP4/DSNTIAUL, DFSRRC00 | Utilities | JCL | SQL / Spring Batch steps / file ops |
| Control-M / CA-7 | Scheduler | 3 folders | Step Functions / Airflow / Spring Batch flows |
