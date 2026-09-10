# CardDemo — Hotspot Report

Ranks the 44 COBOL programs (30,175 physical lines) by five static metrics and recommends a modernization order.
Metrics were produced by a script that parses every `.cbl/.CBL` under `app/cbl/` and `app/app-*/cbl/`
(comment lines `*`/`/` in column 7 removed, quoted literals blanked before keyword matching).

## 1. Metric definitions and limitations

| Metric | How measured | Caveat |
|---|---|---|
| **LOC** | Physical lines in the source file (incl. comments/blank) | COACTUPC and COTRTLIC/UPC carry very long comment banners; executable LOC is ~60–70 % of physical |
| **Copybooks** | Distinct `COPY` names that resolve to a file in `cpy/`, `cpy-bms/` or a sub-app `cpy/` | Excludes `DFHAID`, `DFHBMSCA`, `SQLCA`, `CMQ*`, DCLGEN `INCLUDE`s (system/generated) |
| **I/O operations** | Count of file `OPEN/CLOSE/READ/WRITE/REWRITE/DELETE/START`, `EXEC CICS READ/WRITE/REWRITE/DELETE/STARTBR/READNEXT/READPREV/ENDBR/WRITEQ/READQ/SEND/RECEIVE`, `EXEC SQL SELECT/INSERT/UPDATE/DELETE/FETCH/OPEN/CLOSE`, `CALL 'CBLTDLI'`, `CALL 'MQ…'` | `EXEC DLI` command-level calls (COPAUA0C, COPAUS0C/1C, CBPAUP0C) are **not** counted — see §2 note |
| **Logic density** | `IF + EVALUATE + WHEN` count ("branches") and maximum `IF/EVALUATE` nesting depth (reset at each sentence period) | Nesting is a regex estimate; COBOL's period-terminated style keeps depth ≤ 4 everywhere, so branch count is the better discriminator |
| **Inter-program deps** | Distinct resolved program edges in+out (`CALL`, `XCTL`, `LINK` targets resolved from literals / menu tables, MQ API and `CBLTDLI` counted as one external edge each) | `CEE3ABD` excluded (11 batch programs, boilerplate) |

## 2. Full metric table (sorted by composite)

Composite = sum of the program's rank in each of the five metrics LOC, copybooks, I/O ops, branches, deps (lower = hotter). Ties on a raw metric share the same rank (competition ranking).

| # | Program | Type | LOC | Copybooks | I/O ops | Branches | Max nest | Deps | Composite |
|---|---|---|---|---|---|---|---|---|---|
| 1 | **COACTUPC** | CICS | 4,236 | 14 | 12 | 311 | 3 | 6 (COMEN01C↔, →COCRDLIC/SLC/UPC, →CSUTLDTC via CSUTLDPY) | 16 |
| 2 | **COTRTLIC** | CICS/DB2 | 2,098 | 8 | 21 | 181 | 4 | 3 (COADM01C↔, →COTRTUPC) | 28 |
| 3 | **COCRDLIC** | CICS | 1,459 | 8 | 12 | 120 | 4 | 6 (COMEN01C↔, COACTVWC/COACTUPC→, →COCRDSLC, →COCRDUPC) | 30 |
| 4 | **COCRDUPC** | CICS | 1,560 | 10 | 7 | 147 | 3 | 5 (COMEN01C↔, COCRDLIC↔, COACTVWC/COACTUPC→) | 40 |
| 5 | **COTRTUPC** | CICS/DB2 | 1,702 | 9 | 8 | 179 | 3 | 3 (COADM01C↔, COTRTLIC→) | 43 |
| 6 | **COACTVWC** | CICS | 941 | 12 | 8 | 54 | 2 | 5 (COMEN01C↔, →COCRDLIC/SLC/UPC) | 46 |
| 7 | **COPAUA0C** | CICS/MQ/IMS | 1,026 | 8 | 8 (+EXEC DLI GU/ISRT/REPL) | 57 | 3 | 5 (MQOPEN/GET/PUT1/CLOSE, DL/I) | 48 |
| 8 | **COPAUS0C** | CICS/IMS | 1,032 | 12 | 6 (+9 EXEC DLI) | 93 | 4 | 4 (COMEN01C↔, COPAUS1C↔) | 51 |
| 9 | **COTRN02C** | CICS | 783 | 8 | 8 | 103 | 4 | 3 (COMEN01C↔, →CSUTLDTC) | 55 |
| 10 | **COCRDSLC** | CICS | 887 | 10 | 7 | 57 | 2 | 4 (COMEN01C↔, COCRDLIC↔, COACTVWC/COACTUPC→) | 56 |
| 11 | COUSR00C | CICS | 695 | 6 | 7 | 93 | 4 | 4 | 67 |
| 12 | COTRN00C | CICS | 699 | 6 | 7 | 92 | 4 | 3 | 73 |
| 13 | COBIL00C | CICS | 572 | 8 | 9 | 60 | 4 | 2 | 77 |
| 14 | CBSTM03A | Batch | 924 | 4 | **97** | 40 | 2 | 1 (→CBSTM03B) | 87 |
| 15 | CORPT00C | CICS | 649 | 6 | 4 | 49 | 3 | 3 (COMEN01C↔, →CSUTLDTC, TDQ JOBS) | 87 |
| 16 | COPAUS1C | CICS/IMS | 604 | 8 | 3 (+EXEC DLI) | 47 | 3 | 3 (COPAUS0C↔, LINK COPAUS2C) | 90 |
| 17 | CBTRN02C | Batch | 731 | 5 | 21 | 48 | 3 | 0 | 91 |
| 18 | COACCT01 | CICS/MQ | 620 | 1 | 10 | 44 | 2 | 4 (MQ API) | 92 |
| 19 | CBTRN03C | Batch | 649 | 5 | 18 | 48 | 4 | 0 | 98 |
| 20 | CBACT04C | Batch | 652 | 5 | 17 | 43 | 4 | 0 | 102 |
| 21 | COUSR02C | CICS | 414 | 6 | 4 | 43 | 4 | 3 | 102 |
| 22 | CBTRN01C | Batch | 494 | 6 | 15 | 33 | 3 | 0 | 104 |
| 23 | CBEXPORT | Batch | 582 | 6 | 22 | 16 | 1 | 0 | 107 |
| 24 | CODATE01 | CICS/MQ | 524 | 0 | 9 | 38 | 2 | 4 (MQ API) | 107 |
| 25 | CBIMPORT | Batch | 487 | 6 | 21 | 22 | 1 | 0 | 107 |
| 26 | COUSR03C | CICS | 359 | 6 | 4 | 34 | 4 | 3 | 110 |
| 27 | COMEN01C | CICS hub | 308 | 7 | 2 | 32 | 3 | **24** (COSGN00C↔, 11 targets ↔) | 111 |
| 28 | COSGN00C | CICS entry | 260 | 6 | 4 | 19 | 3 | **22** (→COADM01C, →COMEN01C, ←every screen on exit) | 114 |
| 29 | COADM01C | CICS hub | 288 | 7 | 2 | 24 | 3 | 14 (COSGN00C↔, 6 targets ↔) | 117 |
| 30 | COTRN01C | CICS | 330 | 6 | 3 | 23 | 4 | 3 | 122 |
| 31 | COUSR01C | CICS | 299 | 6 | 3 | 24 | 3 | 3 | 123 |
| 32 | CBACT01C | Batch | 430 | 2 | 10 | 22 | 2 | 1 (→COBDATFT) | 127 |
| 33 | PAUDBLOD | Batch/IMS | 369 | 4 | 9 | 17 | 2 | 1 (CBLTDLI) | 133 |
| 34 | DBUNLDGS | Batch/IMS | 366 | 6 | 4 | 9 | 2 | 1 (CBLTDLI) | 140 |
| 35 | PAUDBUNL | Batch/IMS | 317 | 4 | 8 | 11 | 2 | 1 (CBLTDLI) | 143 |
| 36 | CBSTM03B | Batch sub | 230 | 0 | 12 | 18 | 1 | 1 (←CBSTM03A) | 148 |
| 37 | CBPAUP0C | Batch/IMS | 386 | 2 | 0 (+EXEC DLI GN/GNP/DLET/CHKP) | 28 | 2 | 0 | 162 |
| 38 | COBTUPDT | Batch/DB2 | 237 | 0 | 7 | 23 | 1 | 0 | 163 |
| 39 | CSUTLDTC | Sub | 157 | 0 | 0 | 12 | 1 | 3 (←COTRN02C, ←CORPT00C, →CEEDAYS) | 176 |
| 40 | COPAUS2C | CICS/DB2 | 244 | 1 | 2 | 3 | 2 | 1 (←COPAUS1C) | 180 |
| 41 | CBACT02C | Batch | 178 | 1 | 3 | 11 | 2 | 0 | 180 |
| 42 | CBACT03C | Batch | 178 | 1 | 3 | 11 | 2 | 0 | 180 |
| 43 | CBCUS01C | Batch | 178 | 1 | 3 | 11 | 2 | 0 | 180 |
| 44 | COBSWAIT | Batch util | 41 | 0 | 0 | 0 | 0 | 1 (→MVSWAIT) | 196 |

Consistency checks: LOC column sums to 30,175; 26 online (21 screen programs + COPAUS2C linked module + 3 MQ-triggered + the CSUTLDTC subroutine called from CICS) + 17 batch + 1 batch subroutine (CBSTM03B) = 44, matching APPLICATION_INVENTORY.md.

## 3. Top-10 per individual metric

| Rank | Lines of code | Copybooks | I/O operations | Branches (IF+EVALUATE+WHEN) | Inter-program deps |
|---|---|---|---|---|---|
| 1 | COACTUPC 4,236 | COACTUPC 14 | CBSTM03A 97 | COACTUPC 311 | COMEN01C 24 |
| 2 | COTRTLIC 2,098 | COACTVWC 12 | CBEXPORT 22 | COTRTLIC 181 | COSGN00C 22 |
| 3 | COTRTUPC 1,702 | COPAUS0C 12 | COTRTLIC 21 | COTRTUPC 179 | COADM01C 14 |
| 4 | COCRDUPC 1,560 | COCRDSLC 10 | CBIMPORT 21 | COCRDUPC 147 | COACTUPC 6 |
| 5 | COCRDLIC 1,459 | COCRDUPC 10 | CBTRN02C 21 | COCRDLIC 120 | COCRDLIC 6 |
| 6 | COPAUS0C 1,032 | COTRTUPC 9 | CBTRN03C 18 | COTRN02C 103 | COCRDUPC 5 |
| 7 | COPAUA0C 1,026 | COTRTLIC 8 | CBACT04C 17 | COPAUS0C 93 | COACTVWC 5 |
| 8 | COACTVWC 941 | COPAUA0C 8 | CBTRN01C 15 | COUSR00C 93 | COPAUA0C 5 |
| 9 | CBSTM03A 924 | COPAUS1C 8 | CBSTM03B 12 | COTRN00C 92 | COPAUS0C 4 |
| 10 | COCRDSLC 887 | COCRDLIC 8 | COACTUPC 12 | COBIL00C 60 | COCRDSLC 4 |

Maximum nesting depth is 4 for eleven programs (COPAUS0C, COTRTLIC, CBACT04C, CBTRN03C, COBIL00C, COCRDLIC, COTRN00C,
COTRN01C, COTRN02C, COUSR00C, COUSR02C, COUSR03C) and never exceeds 4, so it does not separate the leaders; branch count is used
as the density ranking above.

Observations:
* **Size and logic concentrate in the online layer** — 9 of the 10 largest programs are CICS; the batch layer is small
  (largest batch program CBSTM03A, 924 LOC) but carries almost all file I/O (CBSTM03A, CBEXPORT, CBIMPORT, CBTRN02C, CBTRN03C, CBACT04C).
* **Hub programs have low complexity but the highest coupling** — COMEN01C, COSGN00C, COADM01C are <310 LOC each but sit on
  every navigation path; their contract is `COCOM01Y`.
* **COACTUPC is an outlier** on every dimension except I/O: 14 % of the estate's LOC, 311 branches (next: 181), 3 `COPY CSSETATY REPLACING`
  expansions plus `CSUTLDPY` and `CSLKPCDY` (1,318-line lookup tables) inlined.
* **Sub-application programs cluster in the upper-middle**: COTRTLIC/COTRTUPC (#2/#5) because of verbose DB2 error handling and paging;
  COPAUA0C/COPAUS0C (#7/#8) because they combine CICS, IMS and (for COPAUA0C) MQ. Note COPAUA0C contains **no** `EXEC SQL`;
  DB2 in the authorization module is confined to COPAUS2C.

## 4. Recommended modernization order

Ordering balances (a) risk retirement — tackle the hottest logic while the team is fresh, (b) proving the toolchain on
something contained first, and (c) respecting dependency boundaries so that legacy and Java can coexist (strangler pattern).

### Wave 0 — Foundations (before any program)
* Relational schema for the 10 VSAM files from `DATA_DICTIONARY.md` (§1–6) with `BigDecimal` for every `S9(n)V99`, plus AIX-equivalent
  indexes (`CARDDATA` by acct, `CARDXREF` by acct, `TRANSACT` by proc-ts). `IMS PAUTSUM0/PAUTDTL1` → two tables (1:N).
* Shared **validation library** extracted from `CSLKPCDY` (state/ZIP/area-code sets), `CSUTLDPY/CSUTLDWY/CSUTLDTC` (dates, leap-year, DOB),
  and COACTUPC paragraphs `1210–1280` (SSN, FICO 300–850, phone, Y/N, signed-9V2). This single library removes the largest source of duplicated
  branching from COACTUPC, COCRDUPC, COTRN02C and COUSR0xC.
* `COCOM01Y` → session/context DTO; `CSUSR01Y` → user entity with **hashed passwords** (plaintext `SEC-USR-PWD` today).

### Wave 1 — Prove the approach on contained batch (low coupling, high I/O)
1. **CBSTM03A + CBSTM03B** (#14 composite but #1 for I/O; highest I/O, 1 dependency, no CICS). Spring Batch job: readers for TRXFL/XREF/ACCT/CUST, writers for text and HTML statements.
   Output is deterministic text → easy golden-file comparison against COBOL output from `CREASTMT`.
2. **CBTRN02C** (#17; the daily posting engine, 4 reject reasons, updates ACCTDATA/TCATBALF) and **CBACT04C** (#20; interest = `bal × rate / 1200`,
   `DEFAULT` group fallback, unimplemented `1400-COMPUTE-FEES`). These two define the money-moving rules; migrating them early
   pins down `BigDecimal` rounding behaviour for everything downstream.
3. **CBTRN03C** (#19) and the CBEXPORT/CBIMPORT pair (#23/#25) — simple read-validate-write; CBIMPORT also documents the packed/binary
   layout of `CVEXPORT` that a Java reader must honour.

### Wave 2 — Highest-risk online logic, decomposed
4. **COACTUPC** (#1). Do **not** translate as one class. Split into: AccountQuery (shared with COACTVWC #6), AccountUpdateCommand
   (optimistic-lock compare of before/after images as the COBOL does), CustomerUpdateCommand, and the Wave-0 validation library.
   Replace the three `CSSETATY` REPLACING expansions with a field-error map returned to the UI. Because COACTVWC shares the same
   read path and screen fields, migrate it in the same wave.
5. **Card cluster COCRDLIC → COCRDSLC / COCRDUPC** (#3/#10/#4). COCRDLIC's STARTBR/READNEXT/READPREV paging is the template reused by
   COTRN00C, COUSR00C and COTRTLIC; build one generic paged-list service here and reuse it.

### Wave 3 — DB2 module (natural SQL, contained)
6. **COTRTLIC / COTRTUPC / COBTUPDT** (#2/#5/#38). SQL translates almost 1:1 to JPA; the referential rule
   (`TRANSACTION_TYPE_CATEGORY … ON DELETE RESTRICT`, checked by `SELECT COUNT` before delete) becomes a FK constraint. Most of their
   LOC is `CSDB2RPY`-style error handling that collapses to exceptions, so the real effort is much lower than the LOC rank suggests.

### Wave 4 — Transaction & billing screens
7. **COTRN02C** (#9), **COBIL00C** (#13), **COTRN00C/COTRN01C** (#12/#30). All append to TRANSACT with the "READPREV last key + 1" id
   generation — replace with a sequence in Wave 0 and these become thin services over the Transaction table.
8. **CORPT00C** (#15): its JCL-generation/`WRITEQ TD 'JOBS'` bridge becomes an async job trigger for the Wave-1 report job.

### Wave 5 — Authorization module as one unit
9. **COPAUA0C + COPAUS0C + COPAUS1C + COPAUS2C + CBPAUP0C** (#7/#8/#16/#40/#37). Migrate together: they share the IMS segments
   (`CIPAUSMY`/`CIPAUDTY`), the reason-code table (`3100/4100/4200/4300/5100/5200/9000`) and the LINK chain. MQ request/reply →
   JMS/SQS listener; IMS `GU/GNP/ISRT/REPL/DLET` → repository calls on the two Wave-0 tables; purge job → scheduled task.
   Include the load/unload utilities (PAUDBLOD, PAUDBUNL, DBUNLDGS) only as one-time data-migration scripts.

### Wave 6 — Hubs, security and retirement
10. **COSGN00C / COMEN01C / COADM01C / COUSR00C–03C** (#28/#27/#29/#11,#31,#21,#26). Low logic, high fan-in: they become Spring Security
    login + role-based menu/routing and a User CRUD service. Doing them last lets the legacy menus keep routing to unmigrated screens
    during the strangler period. Retire without replacement: CBACT01C/02C/03C, CBCUS01C (print utilities), COBSWAIT/MVSWAIT (scheduler
    concern), COACCT01/CODATE01 (MQ samples), UNUSED1Y, and the duplicate `CUSTREC`.

### Mapping to target service boundaries

| Wave | Target service(s) |
|---|---|
| 1 | reporting-service (CBSTM03A/B, CBTRN03C), transaction-service batch half (CBTRN01C/02C, CBACT04C), data-migration-tools (CBEXPORT/CBIMPORT) |
| 2 | account-service (COACTUPC, COACTVWC), card-service (COCRDLIC/SLC/UPC) |
| 3 | transaction-type-service (COTRTLIC, COTRTUPC, COBTUPDT) |
| 4 | transaction-service online half (COTRN00C/01C/02C, COBIL00C), reporting-service trigger (CORPT00C) |
| 5 | authorization-service (COPAUA0C, COPAUS0C/1C/2C, CBPAUP0C + load/unload utilities) |
| 6 | user-auth-service (COSGN00C, COMEN01C, COADM01C, COUSR00C–03C); retire COACCT01/CODATE01 or fold into account-service |

Shared libraries carved out in Wave 0: `carddemo-validation` (CSLKPCDY, CSUTLDPY/WY/DTC, COACTUPC edit paragraphs), `carddemo-commons` (COCOM01Y DTOs, CSMSG/ABEND handling), `carddemo-security` (CSUSR01Y → hashed credentials / tokens).

### Why this order

| Concern | Addressed by |
|---|---|
| Biggest single risk (COACTUPC, 14 % LOC, 311 branches) | Decomposed in Wave 2 after the validation library (Wave 0) has already absorbed ~40 % of its branching |
| Fast, measurable proof of toolchain | Wave 1 batch programs have 0–1 program dependencies and file-comparable output |
| Money correctness | CBTRN02C/CBACT04C first fix `BigDecimal` semantics before online writers (COBIL00C, COTRN02C) are ported |
| Dual-write windows | Each wave migrates *all* writers of a file together: ACCTDATA writers (CBACT04C, CBTRN02C, COACTUPC, COBIL00C) span Waves 1–4, so ACCTDATA needs a sync bridge until Wave 4; CARDDATA (COCRDUPC only) and USRSEC (COUSR0xC only) need none |
| Cross-subsystem complexity (IMS+MQ+CICS) | Isolated in Wave 5 where the team already has patterns for CICS→REST and VSAM→SQL |
| Coupling hot-spots (COMEN01C 24 edges, COSGN00C 22) | Left as the last legacy shell so they keep routing to both old and new during transition |
