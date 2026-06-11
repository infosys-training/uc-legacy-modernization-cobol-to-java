# CardDemo — Hotspot Report

> Top 10 programs ranked by modernization complexity across 5 dimensions:
> Lines of Code, Copybook References, I/O Operations, Business Logic Density, and Inter-Program Dependencies.

---

## 1. Scoring Methodology

Each program is scored on 5 metrics. Raw values are normalized to a 0–20 scale per metric, then summed to a composite score out of 100.

| Metric | Weight | What It Measures | How Counted |
|--------|--------|-----------------|-------------|
| **Lines of Code** | 20 | Raw program size | `wc -l` on source file |
| **Copybooks Referenced** | 20 | Data coupling / cross-cutting concerns | Count of COPY statements |
| **I/O Operations** | 20 | External interaction complexity | CICS READ/WRITE/REWRITE/STARTBR/ENDBR + VSAM OPEN/CLOSE/READ/WRITE + MQ/DB2/IMS calls |
| **Business Logic Density** | 20 | Branching / decision complexity | Count of IF + EVALUATE statements |
| **Inter-Program Dependencies** | 20 | Coupling to other programs / subsystems | CALL + XCTL + LINK targets + callers + subsystem count |

---

## 2. Top 10 Hotspot Rankings

### Summary Table

| Rank | Program | LOC | Copybooks | I/O Ops | IF+EVAL | Deps | Score | Classification |
|------|---------|-----|-----------|---------|---------|------|-------|---------------|
| **1** | **COACTUPC.cbl** | 4,236 | 58 | 18 | 184 | 4 | **89.3** | Online (CICS) |
| **2** | **COTRTLIC.cbl** | 2,098 | 14 | 22 | 32 | 4 | **52.1** | Online (CICS/DB2) |
| **3** | **COTRTUPC.cbl** | 1,702 | 15 | 18 | 26 | 4 | **46.8** | Online (CICS/DB2) |
| **4** | **COCRDUPC.cbl** | 1,560 | 13 | 10 | 88 | 3 | **45.2** | Online (CICS) |
| **5** | **COCRDLIC.cbl** | 1,459 | 11 | 14 | 77 | 4 | **43.6** | Online (CICS) |
| **6** | **COPAUS0C.cbl** | 1,032 | 14 | 12 | 47 | 3 | **36.9** | Online (CICS/IMS) |
| **7** | **COPAUA0C.cbl** | 1,026 | 14 | 16 | 36 | 5 | **37.5** | Online (CICS/MQ/IMS/DB2) |
| **8** | **CBSTM03A.CBL** | 924 | 4 | 115 | 24 | 2 | **36.2** | Batch |
| **9** | **COACTVWC.cbl** | 941 | 14 | 10 | 38 | 2 | **33.1** | Online (CICS) |
| **10** | **COCRDSLC.cbl** | 887 | 13 | 8 | 41 | 2 | **30.5** | Online (CICS) |

---

## 3. Detailed Hotspot Analysis

### Rank 1: COACTUPC.cbl — Account Update (Score: 89.3)

**The undisputed hotspot.** Largest program in the estate by every metric except I/O count.

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 4,236 | 14% of entire codebase |
| Copybooks | 58 | 39× COPY REPLACING for CSSETATY macro (field-level attribute setting) |
| I/O Operations | 18 | CICS READ/REWRITE on ACCTFILE, CUSTFILE, CARDXREF |
| Business Logic (IF+EVAL) | 184 (164 IF + 20 EVALUATE) | Exhaustive validation: dates, SSN, phone, state, ZIP, credit limits |
| Dependencies | 4 | XCTL from/to COMEN01C; reads 3 VSAM files; uses CSLKPCDY lookup tables |

**Why it's complex:**
- Validates every editable field against business rules (SSN format, phone area code lookup via CSLKPCDY's 350+ valid codes, state codes, ZIP prefix validation)
- 39 instances of `COPY CSSETATY REPLACING` — a macro pattern that generates ~10 lines each for field-level BMS attribute coloring (red for errors, asterisk for blank required fields)
- Touches 3 core VSAM files in a single transaction: account, customer, card cross-reference
- Deep nesting: up to 6 levels of IF within PERFORM paragraphs

### Rank 2: COTRTLIC.cbl — Transaction Type List / DB2 (Score: 52.1)

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 2,098 | Second-largest program |
| Copybooks | 14 | Includes SQLCA, DCLTRTYP, CSDB2RWY, CSDB2RPY |
| I/O Operations | 22 | EXEC SQL OPEN/FETCH/CLOSE (forward + backward cursors), DELETE, SYNCPOINT |
| Business Logic (IF+EVAL) | 32 (0 IF + 32 EVALUATE) | Entirely EVALUATE-driven control flow |
| Dependencies | 4 | CICS XCTL to COMEN01C/COTRTUPC; DB2 cursor operations; SYNCPOINT |

**Why it's complex:**
- Cursor-based bidirectional pagination over DB2 TRANSACTION_TYPE table
- Manages forward cursor (C-TR-TYPE-FORWARD) and backward cursor (C-TR-TYPE-BACKWARD) independently
- Inline DELETE with cascading child record check against TRANSACTION_CATEGORY
- All control flow uses EVALUATE (no IF statements) — unusual pattern

### Rank 3: COTRTUPC.cbl — Transaction Type Update / DB2 (Score: 46.8)

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,702 | — |
| Copybooks | 15 | DCLTRTYP + DCLTRCAT (both DB2 table declarations) |
| I/O Operations | 18 | SQL SELECT/INSERT/UPDATE/DELETE + SYNCPOINT on 2 tables |
| Business Logic (IF+EVAL) | 26 (0 IF + 26 EVALUATE) | EVALUATE-driven; 9 distinct screen states |
| Dependencies | 4 | CICS XCTL; DB2 with SYNCPOINT; cascading delete logic |

**Why it's complex:**
- Full CRUD lifecycle with multi-step confirmation (delete requires F4 confirm)
- Cascading delete: checks TRANSACTION_CATEGORY child records before allowing TRANSACTION_TYPE delete
- 9-state screen FSM managed by EVALUATE on context flags
- Uses COPY REPLACING for CSSETATY field validation

### Rank 4: COCRDUPC.cbl — Card Update (Score: 45.2)

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,560 | — |
| Copybooks | 13 | Standard CICS set + CVACT02Y, CVCUS01Y |
| I/O Operations | 10 | CICS READ/REWRITE on CARDFILE |
| Business Logic (IF+EVAL) | 88 (72 IF + 16 EVALUATE) | Heavy field validation |
| Dependencies | 3 | XCTL from COCRDLIC; reads CARDFILE, ACCTFILE |

### Rank 5: COCRDLIC.cbl — Card List (Score: 43.6)

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,459 | — |
| Copybooks | 11 | — |
| I/O Operations | 14 | STARTBR/READNEXT/READPREV/ENDBR (VSAM browse) |
| Business Logic (IF+EVAL) | 77 (59 IF + 18 EVALUATE) | Pagination state management |
| Dependencies | 4 | XCTL to COCRDSLC, COCRDUPC, COMEN01C |

**Pattern note:** The STARTBR/READNEXT/READPREV/ENDBR pagination pattern appears in 5 programs (COCRDLIC, COTRN00C, COUSR00C, COBIL00C, COPAUS0C). Modernizing this pattern once as a reusable `AbstractListController` benefits all 5.

### Rank 6: COPAUS0C.cbl — Auth Summary Browse (Score: 36.9)

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,032 | — |
| Copybooks | 14 | CIPAUSMY, CIPAUDTY (IMS segments) |
| I/O Operations | 12 | CICS READ (3 files) + CICS SEND/RECEIVE + SYNCPOINT |
| Business Logic (IF+EVAL) | 47 (25 IF + 22 EVALUATE) | — |
| Dependencies | 3 | Part of IMS chain (COPAUS0C→1C→2C); reads CARDFILE, ACCTFILE, CUSTFILE |

### Rank 7: COPAUA0C.cbl — Authorization Decision Engine (Score: 37.5)

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,026 | — |
| Copybooks | 14 | MQ copybooks (CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV) + IMS + VSAM |
| I/O Operations | 16 | MQOPEN/GET/PUT1/CLOSE + CICS READ (3 files) + WRITEQ |
| Business Logic (IF+EVAL) | 36 (26 IF + 10 EVALUATE) | Authorization rule evaluation |
| Dependencies | 5 | Spans CICS + MQ + IMS + VSAM (4 subsystems); highest integration complexity |

**Why it's architecturally significant:**
- Only program that spans all 4 middleware subsystems (CICS, MQ, IMS, VSAM)
- Receives authorization requests from MQ, evaluates rules, sends responses
- Must be migrated alongside COPAUS0C/1C/2C as a unit
- **Recommend migrating last** due to cross-subsystem integration complexity

### Rank 8: CBSTM03A.CBL — Statement Generation (Score: 36.2)

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 924 | — |
| Copybooks | 4 | COSTM01, CVACT03Y, CUSTREC, CVACT01Y |
| I/O Operations | **115** | **Highest I/O count in estate** — 97× WRITE FD-STMTFILE-REC + 13× CALL CBSTM03B + HTML writes |
| Business Logic (IF+EVAL) | 24 (15 IF + 9 EVALUATE) | — |
| Dependencies | 2 | CALL CBSTM03B (I/O submodule); CALL CEE3ABD |

**Why it's the best migration proof-of-concept:**
- Self-contained batch program (no CICS, no user interaction)
- Clean CALL interface to CBSTM03B for all file I/O
- Already generates HTML output — natural fit for modern template engine
- Low dependency count, moderate LOC
- **Recommend migrating second** (after COACTUPC) as a batch-side proof point

### Rank 9: COACTVWC.cbl — Account View (Score: 33.1)

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 941 | — |
| Copybooks | 14 | Full CICS set + all 4 entity copybooks |
| I/O Operations | 10 | CICS READ on CARDFILE, ACCTFILE, CUSTFILE |
| Business Logic (IF+EVAL) | 38 (28 IF + 10 EVALUATE) | — |
| Dependencies | 2 | XCTL from/to COMEN01C; read-only |

### Rank 10: COCRDSLC.cbl — Card Detail View (Score: 30.5)

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 887 | — |
| Copybooks | 13 | — |
| I/O Operations | 8 | CICS READ on CARDFILE, ACCTFILE |
| Business Logic (IF+EVAL) | 41 (33 IF + 8 EVALUATE) | — |
| Dependencies | 2 | XCTL from COCRDLIC; read-only |

---

## 4. Modernization Recommendations

### Phase 1: High-Impact Proof Points (Months 1–3)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **1** | **COACTUPC** | Highest hotspot score (89.3); most business logic; validates every core entity. Success here proves the hardest case. | Extract validation into reusable Java service classes. Convert BMS map to REST API + modern UI. Split into AccountEditController, AccountValidationService, and entity-specific validators. |
| **2** | **CBSTM03A + CBSTM03B** | Highest I/O count (115); self-contained batch; already generates HTML. Easiest batch proof point. | Spring Batch job: `CBSTM03B` → `ItemReader`s for each file; `CBSTM03A` → `ItemProcessor` + `ItemWriter` with Thymeleaf templates for HTML. |

### Phase 2: DB2 Programs (Months 2–4)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **3** | **COTRTLIC + COTRTUPC** | Already use DB2 SQL — closest to modern relational model. Cursor pagination maps to Spring Data paging. | JPA entities for `TRANSACTION_TYPE` and `TRANSACTION_CATEGORY`. DB2 SQL translates with minimal changes. Cascading deletes become JPA `@OneToMany(cascade=REMOVE)`. |
| **4** | **COBTUPDT** | Batch DB2 maintenance — small (237 LOC) and straightforward. | Spring Batch step with JPA batch operations. |

### Phase 3: Pagination Pattern Programs (Months 3–5)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **5** | **COCRDLIC + COCRDUPC** | The STARTBR/READNEXT/READPREV/ENDBR pattern appears in 5 programs. Build the reusable pagination abstraction once. | Generic `AbstractPaginatedListController<T>` with Spring Data `Pageable`. Migrate card list/update first, then apply to COTRN00C, COUSR00C, COBIL00C, COPAUS0C. |
| **6** | **COACTVWC + COCRDSLC** | Read-only view screens — simplest online programs (no writes). | Spring MVC read-only controllers. |

### Phase 4: Remaining Batch (Months 4–6)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **7** | **CBTRN02C (POSTTRAN)** | Core pipeline step 1 — transaction posting with multi-file updates. | Spring Batch with transactional step (commit every N records). |
| **8** | **CBACT04C (INTCALC)** | Interest calculation — complex business rules but contained scope. | Spring Batch + business rule engine for rate lookup. |
| **9** | **CBTRN03C + CBEXPORT + CBIMPORT** | Remaining batch utilities. | Spring Batch steps. |

### Phase 5: Cross-Subsystem Integration (Months 6–9)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **10** | **COPAUA0C + COPAUS0C/1C/2C** | Spans CICS + MQ + IMS + DB2 (4 subsystems). Most architecturally complex. Migrate as a unit. | Spring JMS for MQ; IMS hierarchy → 2 relational tables (auth_summary, auth_detail); REST API for CICS screens. |

---

## 5. Risk Matrix

| Risk | Impact | Mitigation |
|------|--------|------------|
| COACTUPC validation rules are scattered across 4,236 lines | Missed business rules → data corruption | Extract every IF/EVALUATE block; create test cases from validation logic before migrating |
| COPY REPLACING macro (CSSETATY × 39) generates ~390 lines of screen attribute code | Hard to trace generated code | Generate a macro-expanded listing first; map each TESTVAR1/SCRNVAR2/MAPNAME3 substitution |
| CBSTM03A has 115 hardcoded WRITE statements | Fragile output formatting | Replace with Thymeleaf/Freemarker template; treat each WRITE block as a template section |
| IMS DL/I hierarchy has no SQL equivalent | Data model mismatch | Map IMS segments to relational: summary → `auth_summary` table, detail → `auth_detail` table with FK |
| Batch pipeline has strict ordering dependency | Parallel execution risks data inconsistency | Preserve Step 1→2→3→4 ordering in Spring Batch job flow; use `FlowBuilder` to enforce sequence |
| Assembler modules (COBDATFT, MVSWAIT) have no source-level equivalent | External dependency | Replace COBDATFT with `java.time.format.DateTimeFormatter`; eliminate MVSWAIT (not needed in modern scheduling) |

---

## 6. Quick-Reference Metrics Table (All 44 Programs)

| Program | LOC | IFs | EVALs | PERFORMs | Copybooks | Classification |
|---------|-----|-----|-------|----------|-----------|---------------|
| COACTUPC.cbl | 4,236 | 164 | 20 | 64 | 58 | Online (CICS) |
| COTRTLIC.cbl | 2,098 | 0 | 32 | 69 | 14 | Online (CICS/DB2) |
| COTRTUPC.cbl | 1,702 | 0 | 26 | 40 | 15 | Online (CICS/DB2) |
| COCRDUPC.cbl | 1,560 | 72 | 16 | 26 | 13 | Online (CICS) |
| COCRDLIC.cbl | 1,459 | 59 | 18 | 34 | 11 | Online (CICS) |
| COPAUS0C.cbl | 1,032 | 25 | 22 | 48 | 14 | Online (CICS/IMS) |
| COPAUA0C.cbl | 1,026 | 26 | 10 | 38 | 14 | Online (CICS/MQ/IMS) |
| COACTVWC.cbl | 941 | 28 | 10 | 21 | 14 | Online (CICS) |
| CBSTM03A.CBL | 924 | 15 | 9 | 33 | 4 | Batch |
| COCRDSLC.cbl | 887 | 33 | 8 | 19 | 13 | Online (CICS) |
| COTRN02C.cbl | 783 | 14 | 26 | 61 | 10 | Online (CICS) |
| CBTRN02C.cbl | 731 | 48 | 0 | 62 | 6 | Batch |
| COTRN00C.cbl | 699 | 26 | 16 | 47 | 8 | Online (CICS) |
| COUSR00C.cbl | 695 | 25 | 16 | 45 | 8 | Online (CICS) |
| CBACT04C.cbl | 652 | 43 | 0 | 57 | 5 | Batch |
| CORPT00C.cbl | 649 | 20 | 10 | 35 | 8 | Online (CICS) |
| CBTRN03C.cbl | 649 | 38 | 4 | 73 | 5 | Batch |
| COACCT01.cbl | 620 | 0 | 18 | 33 | 7 | Online (CICS/MQ) |
| COPAUS1C.cbl | 604 | 17 | 10 | 34 | 10 | Online (CICS/IMS) |
| CBEXPORT.cbl | 582 | 16 | 0 | 50 | 6 | Batch |
| COBIL00C.cbl | 572 | 10 | 18 | 38 | 10 | Online (CICS) |
| CODATE01.cbl | 524 | 0 | 16 | 28 | 6 | Online (CICS/MQ) |
| CBTRN01C.cbl | 494 | 33 | 0 | 43 | 6 | Batch |
| CBIMPORT.cbl | 487 | 14 | 2 | 30 | 6 | Batch |
| CBACT01C.cbl | 430 | 22 | 0 | 36 | 2 | Batch |
| COUSR02C.cbl | 414 | 13 | 10 | 31 | 8 | Online (CICS) |
| CBPAUP0C.cbl | 386 | 17 | 4 | 19 | 2 | Batch (IMS) |
| PAUDBLOD.CBL | 369 | 17 | 0 | 12 | 6 | Batch (IMS) |
| DBUNLDGS.CBL | 366 | 9 | 0 | 12 | 6 | Batch (IMS) |
| COUSR03C.cbl | 359 | 8 | 10 | 26 | 8 | Online (CICS) |
| COTRN01C.cbl | 330 | 7 | 6 | 17 | 8 | Online (CICS) |
| PAUDBUNL.CBL | 317 | 11 | 0 | 8 | 6 | Batch (IMS) |
| COMEN01C.cbl | 308 | 7 | 6 | 15 | 9 | Online (CICS) |
| COUSR01C.cbl | 299 | 4 | 6 | 20 | 8 | Online (CICS) |
| COADM01C.cbl | 288 | 6 | 4 | 15 | 9 | Online (CICS) |
| COSGN00C.cbl | 260 | 4 | 6 | 11 | 8 | Online (CICS) |
| COPAUS2C.cbl | 244 | 3 | 0 | 1 | 1 | Online (CICS/DB2) |
| COBTUPDT.cbl | 237 | 2 | 8 | 15 | 1 | Batch (DB2) |
| CBSTM03B.CBL | 230 | 12 | 1 | 4 | 0 | Batch (submodule) |
| CBCUS01C.cbl | 178 | 11 | 0 | 11 | 1 | Batch |
| CBACT02C.cbl | 178 | 11 | 0 | 11 | 1 | Batch |
| CBACT03C.cbl | 178 | 11 | 0 | 11 | 1 | Batch |
| CSUTLDTC.cbl | 38 | 0 | 2 | 1 | 0 | Utility |
| COBSWAIT.cbl | 12 | 0 | 0 | 0 | 0 | Utility |
