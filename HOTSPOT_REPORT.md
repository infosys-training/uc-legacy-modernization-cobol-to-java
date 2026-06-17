# HOTSPOT REPORT — CardDemo COBOL Estate Modernization Analysis

> **Purpose:** Rank the top 10 programs by complexity metrics and recommend modernization priority.
> **Metrics:** Lines of code, copybooks referenced, I/O operations, business logic density (IF/EVALUATE nesting), inter-program dependencies.

---

## 1. Scoring Methodology

Each program is scored across 5 dimensions (0–20 points each, max 100):

| Dimension | Weight | Metric | Scoring |
|-----------|--------|--------|---------|
| **Size** | 20 pts | Lines of Code | 20 if ≥3,000; 15 if ≥1,500; 10 if ≥1,000; 5 if ≥500; 2 otherwise |
| **Data Coupling** | 20 pts | Copybooks referenced | 20 if ≥40; 15 if ≥15; 10 if ≥10; 5 if ≥5; 2 otherwise |
| **I/O Complexity** | 20 pts | File/DB/MQ operations | 20 if ≥50; 15 if ≥20; 10 if ≥10; 5 if ≥5; 2 otherwise |
| **Logic Density** | 20 pts | IF + EVALUATE count | 20 if ≥100; 15 if ≥50; 10 if ≥20; 5 if ≥10; 2 otherwise |
| **Dependencies** | 20 pts | Programs calling/called by + XCTL + subsystems | 20 if ≥8; 15 if ≥5; 10 if ≥3; 5 if ≥2; 2 otherwise |

---

## 2. Top 10 Hotspot Rankings

### Rank 1: COACTUPC.cbl — Account Update (Score: 90/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **4,236** | 20 |
| Copybooks | **56** (incl. 25× CSSETATY via COPY REPLACING) | 20 |
| I/O Operations | 5 CICS READ/REWRITE (ACCTFILE, XREFFILE, CUSTFILE) | 5 |
| Logic Density | **164 IF + 10 EVALUATE = 174** | 20 |
| Dependencies | XCTL return, CICS VSAM (3 files), CSUTLDPY date validation, CSLKPCDY lookups | 15 |
| **TOTAL** | | **80** |
| **Subsystem Bonus** | +10: Heaviest field-level validation in estate (date, SSN, phone, state, ZIP) | **90** |

**Why modernize first:**
- Largest program by far (4,236 LOC = 15% of entire estate)
- 164 IF statements with nested validation chains — highest branching complexity
- 25 uses of COPY REPLACING macro (CSSETATY) — no direct Java equivalent; must convert to parameterized validation methods
- Touches 3 core VSAM files — natural integration point for account microservice
- Business-critical: account data integrity depends on this program's validation logic

**Recommended approach:**
Split into 4 Java components:
1. `AccountController` — REST API (replaces BMS COACTUP map interactions)
2. `AccountService` — Core update logic
3. `AccountValidationService` — Extract all field validation (dates, SSN, phone area codes, state+ZIP) into reusable validators
4. `AccountView` — Modern UI (React/Angular) replacing 3270 terminal screens

Generate extensive test cases from existing EBCDIC data before rewriting. Run shadow mode for 2 weeks post-migration.

---

### Rank 2: COTRTLIC.cbl — Transaction Type List (Score: 72/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **2,098** | 15 |
| Copybooks | 11 (CSDB2RPY, CSDB2RWY, COTRTLI, DCLTRTYP, DCLTRCAT + CICS standard) | 10 |
| I/O Operations | **24** (EXEC SQL DECLARE/OPEN/FETCH/CLOSE cursors, multiple SELECTs) | 15 |
| Logic Density | 0 IF + 16 EVALUATE = 16 | 5 |
| Dependencies | XCTL to COTRTUPC + return; DB2 subsystem; CICS | 10 |
| **TOTAL** | | **55** |
| **Subsystem Bonus** | +17: DB2 cursor pagination — natural entry point for JPA migration | **72** |

**Why modernize early:**
- Already uses DB2 SQL — closest to modern relational patterns
- Cursor-based pagination (`DECLARE CURSOR ... FETCH NEXT`) maps directly to Spring Data JPA `Pageable`
- Paired with COTRTUPC — migrate as a unit for complete transaction type CRUD
- Low-risk pilot: isolated DB2 tables (TRNTYPE, TRNCATG) with no VSAM dependencies

**Recommended approach:**
- JPA entities: `TransactionType`, `TransactionCategory`
- Spring Data repositories with `PagingAndSortingRepository`
- REST controller replacing BMS COTRTLI map

---

### Rank 3: COTRTUPC.cbl — Transaction Type Update (Score: 68/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **1,702** | 15 |
| Copybooks | 13 | 10 |
| I/O Operations | **28** (EXEC SQL SELECT, INSERT, UPDATE, DELETE with cascading) | 15 |
| Logic Density | 0 IF + 26 EVALUATE = 26 (highest EVALUATE count) | 10 |
| Dependencies | XCTL from COTRTLIC + return; DB2 subsystem; CICS | 10 |
| **TOTAL** | | **60** |
| **Subsystem Bonus** | +8: Cascading delete (type → category) needs careful migration | **68** |

**Why modernize with COTRTLIC:**
- Cascading operations: deleting a transaction type must cascade to categories — maps to JPA `@OneToMany(cascade = CascadeType.ALL)`
- INSERT/UPDATE/DELETE on two related tables — good test of JPA entity relationships
- Highest EVALUATE count in estate (26) — pure evaluation logic, clean conversion to switch/case

---

### Rank 4: COCRDUPC.cbl — Credit Card Update (Score: 62/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **1,560** | 15 |
| Copybooks | 15 | 15 |
| I/O Operations | 3 CICS READ + REWRITE (CARDFILE, CUSTFILE) | 5 |
| Logic Density | 72 IF + 16 EVALUATE = 88 | 15 |
| Dependencies | XCTL from COCRDLIC, return to menu; CICS VSAM; CSSTRPFY | 10 |
| **TOTAL** | | **60** |
| **Subsystem Bonus** | +2: Reusable paginated browse pattern | **62** |

**Why modernize mid-priority:**
- 88 branching statements — second-highest logic density after COACTUPC
- Part of card browse-view-update trio (COCRDLIC → COCRDSLC → COCRDUPC)
- Shares pagination pattern (STARTBR/READNEXT/READPREV) with 5+ other programs — building this once creates reusable `AbstractListController`

---

### Rank 5: COPAUA0C.cbl — Authorization Decision Engine (Score: 60/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **1,026** | 10 |
| Copybooks | 16 (MQ, IMS, DB2, BMS, and application) | 15 |
| I/O Operations | 10 (MQOPEN/GET/PUT1/CLOSE + CICS READ + EXEC SQL) | 10 |
| Logic Density | ~30 IF + EVALUATE | 10 |
| Dependencies | **4 subsystems**: MQ + IMS + DB2 + CICS/VSAM — most cross-cutting | 20 |
| **TOTAL** | | **65** |
| **Subsystem Adjustment** | -5: Should migrate late (Phase 4) due to integration complexity | **60** |

**Why defer despite high score:**
- Spans MQ (message queues), IMS (hierarchical DB), DB2, and CICS/VSAM simultaneously
- Most architecturally complex program in the estate — requires all 4 subsystem replacements to be ready
- Must migrate as a unit with COPAUS0C, COPAUS1C, COPAUS2C (authorization chain)
- **Migration risk:** Real-time authorization flow; any regression = card transaction failures

**Recommended approach (Phase 4):**
- Spring JMS adapter replacing MQ API calls
- PostgreSQL tables replacing IMS hierarchy (2 tables: `auth_summary`, `auth_detail`)
- REST API replacing CICS screen interaction
- Run MQ and SQS in parallel for 1-week transition

---

### Rank 6: COCRDLIC.cbl — Credit Card List (Score: 55/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **1,459** | 10 |
| Copybooks | 13 | 10 |
| I/O Operations | 11 (STARTBR/READNEXT/READPREV/ENDBR — paginated browse) | 10 |
| Logic Density | 59 IF + 18 EVALUATE = 77 | 15 |
| Dependencies | XCTL to COCRDSLC, COCRDUPC, menu return; CICS VSAM | 10 |
| **TOTAL** | | **55** |

**Why modernize with card suite:**
- Defines the paginated browse pattern used across 5+ programs
- Converting STARTBR/READNEXT → Spring Data `Page<Card>` creates a reusable template

---

### Rank 7: CBSTM03A.CBL — Statement Generation (Score: 53/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **924** | 5 |
| Copybooks | 4 (COSTM01, CVACT03Y, CUSTREC, CVACT01Y) | 2 |
| I/O Operations | **101** (highest in estate — 13 CALLs to CBSTM03B + direct writes) | 20 |
| Logic Density | ~20 IF + 5 EVALUATE = ~25 | 10 |
| Dependencies | CALL CBSTM03B (13 times); no CICS dependency; self-contained batch | 10 |
| **TOTAL** | | **47** |
| **Pilot Bonus** | +6: Best candidate for migration proof-of-concept | **53** |

**Why modernize as pilot:**
- **Self-contained batch** — no CICS, no BMS, no terminal interaction
- Highest I/O operation count (101) validates I/O migration patterns
- Produces tangible output (text + HTML statements) that's easy to compare: COBOL vs Java output
- Spring Batch `ItemReader` → `ItemProcessor` → `ItemWriter` is a natural fit
- CALL-based modularity (CBSTM03A → CBSTM03B) already resembles service/repository pattern

**Recommended approach (Phase 1 — Pilot):**
- Spring Batch job with `FlatFileItemReader` for sorted transaction input
- `StatementProcessor` implementing business logic from CBSTM03A
- Thymeleaf templates for HTML statement generation
- Compare output byte-for-byte against COBOL baseline

---

### Rank 8: COPAUS0C.cbl — Pending Auth Summary Browse (Score: 48/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **1,032** | 10 |
| Copybooks | 14 | 10 |
| I/O Operations | CICS READ (ACCTFILE, XREFFILE, CUSTFILE) + IMS queries | 10 |
| Logic Density | ~25 IF + 10 EVALUATE | 10 |
| Dependencies | LINK to COPAUS1C, COPAUS2C; IMS + CICS; part of auth chain | 10 |
| **TOTAL** | | **50** |
| **Chain Adjustment** | -2: Must migrate with full auth chain (COPAUA0C suite) | **48** |

---

### Rank 9: CBTRN02C.cbl — Batch Transaction Posting (Score: 47/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **731** | 5 |
| Copybooks | 5 (CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y) | 5 |
| I/O Operations | 11 (READ DALYTRAN, XREFFILE, ACCTFILE; WRITE TRANSACT, DALYREJS) | 10 |
| Logic Density | 48 IF + 13 EVALUATE = 61 | 15 |
| Dependencies | First step in daily pipeline; 5 file dependencies | 10 |
| **TOTAL** | | **45** |
| **Pipeline Bonus** | +2: Critical pipeline entry point — failure blocks INTCALC→CREASTMT→TRANRPT | **47** |

**Why important:**
- Gateway to the daily batch pipeline — all downstream steps depend on its output
- Validates daily transaction data against cross-reference and account records
- Complex rejection logic (48 IF statements with various validation paths)

---

### Rank 10: COTRN02C.cbl — Transaction Add (Online) (Score: 45/100)

| Metric | Value | Score |
|--------|-------|-------|
| Lines of Code | **783** | 5 |
| Copybooks | 10 | 10 |
| I/O Operations | 15 (CICS READ ACCTFILE, XREFFILE; STARTBR/READPREV TRANSACT; WRITE TRANSACT) | 10 |
| Logic Density | ~30 IF + 10 EVALUATE | 10 |
| Dependencies | CALL CSUTLDTC (×2); CICS VSAM; 3 file dependencies | 10 |
| **TOTAL** | | **45** |

---

## 3. Composite Ranking Summary

| Rank | Program | LOC | Copybooks | I/O Ops | IF+EVAL | Deps | Score | Phase |
|------|---------|-----|-----------|---------|---------|------|-------|-------|
| 1 | **COACTUPC.cbl** | 4,236 | 56 | 5 | 174 | 5+ | **90** | P3 |
| 2 | **COTRTLIC.cbl** | 2,098 | 11 | 24 | 16 | 3 | **72** | P2 |
| 3 | **COTRTUPC.cbl** | 1,702 | 13 | 28 | 26 | 3 | **68** | P2 |
| 4 | **COCRDUPC.cbl** | 1,560 | 15 | 3 | 88 | 4 | **62** | P2 |
| 5 | **COPAUA0C.cbl** | 1,026 | 16 | 10 | ~30 | 8+ | **60** | P4 |
| 6 | **COCRDLIC.cbl** | 1,459 | 13 | 11 | 77 | 4 | **55** | P2 |
| 7 | **CBSTM03A.CBL** | 924 | 4 | 101 | ~25 | 2 | **53** | P1 |
| 8 | **COPAUS0C.cbl** | 1,032 | 14 | — | ~35 | 5 | **48** | P4 |
| 9 | **CBTRN02C.cbl** | 731 | 5 | 11 | 61 | 5 | **47** | P5 |
| 10 | **COTRN02C.cbl** | 783 | 10 | 15 | ~40 | 4 | **45** | P3 |

---

## 4. Recommended Modernization Sequence

### Phase 0: Foundation (Weeks 1–4)
- Set up Spring Boot project, PostgreSQL schema, CI/CD pipeline
- Build shared libraries: `carddemo-validation` (from CSLKPCDY, CSUTLDPY), `carddemo-commons` (from COCOM01Y), `carddemo-security` (from CSUSR01Y)
- Design relational schema from 8 VSAM files + 2 DB2 tables + 2 IMS segments → 15 target tables

### Phase 1: Pilot — User Auth + Reporting (Weeks 5–9)
- **user-auth-service:** COSGN00C, COUSR00–03C, COMEN01C, COADM01C (3,897 LOC) — Quick Win, isolated USRSEC file
- **reporting-service:** CBSTM03A/B (#7), CBTRN03C, CORPT00C (2,210 LOC) — Spring Batch pilot, tangible output comparison
- **Key risk:** Replace plain-text password storage (SEC-USR-PWD) with bcrypt/Spring Security

### Phase 2: DB2 + Card (Weeks 10–16)
- **transaction-type-service:** COTRTLIC (#2), COTRTUPC (#3), COBTUPDT — Already SQL; JPA entities map directly
- **card-service:** COCRDLIC (#6), COCRDSLC, COCRDUPC (#4) — Establish paginated browse pattern for reuse

### Phase 3: Account + Transaction (Weeks 17–26)
- **account-service:** COACTUPC (#1), COACTVWC, COACCT01 — Highest complexity; extract validation into reusable services
- **transaction-service:** COTRN00–02C (#10), CBTRN01–02C, COBIL00C — Shares VSAM files with account service
- **Key risk:** COACTUPC's 174 branching statements → generate 10,000+ test cases first; run 2-week shadow mode

### Phase 4: Authorization — IMS/MQ (Weeks 27–34)
- **authorization-service:** COPAUA0C (#5), COPAUS0–2C (#8), CBPAUP0C, PAUDBLOD/UNL, DBUNLDGS — Migrate as a unit
- **Key risk:** Real-time auth flow; replace MQ with SQS, IMS with PostgreSQL; 1-week parallel operation

### Phase 5: Batch Pipeline (Weeks 35–39)
- CBTRN02C (#9), CBACT04C, CBSTM03A (if not done in P1), CBTRN03C — Spring Batch steps with Step Functions orchestration
- CBEXPORT, CBIMPORT → temporary data-migration-tools; retire after migration
- **Key risk:** Batch SLA preservation; target Java batch ≤ 110% of COBOL runtime

### Phase 6: Decommission (Weeks 40–42)
- Cut over remaining CICS transactions
- Decommission VSAM clusters, IMS databases, MQ queues
- 30-day stability observation before final mainframe shutdown

---

## 5. Risk Summary for Top 5 Hotspots

| Program | Primary Risk | Mitigation |
|---------|-------------|------------|
| COACTUPC | 174 branching paths; COPY REPLACING macro; 3-file coupling | Split into 4 components; generate extensive test suite; shadow mode |
| COTRTLIC | DB2 cursor lifecycle management | Map to Spring Data `Pageable`; verify cursor close behavior |
| COTRTUPC | Cascading delete correctness | JPA `CascadeType.ALL`; integration tests for delete chains |
| COCRDUPC | Shared VSAM file contention during dual-write | SQS FIFO sync; hourly reconciliation; single source of truth |
| COPAUA0C | 4-subsystem integration; real-time authorization SLA | Migrate last; parallel MQ+SQS; IMS→PostgreSQL with 2-table mapping |
