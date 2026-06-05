# CardDemo Hotspot Report

> Complexity ranking of all 44 COBOL programs with modernization priority recommendations.

---

## 1. Scoring Methodology

Each program is scored across five dimensions (0–100 scale each), then weighted:

| Dimension | Weight | Metric | Source |
|-----------|--------|--------|--------|
| Lines of Code (LOC) | 25% | Raw LOC normalized against max (4,236) | `wc -l` |
| Copybook Count | 20% | Number of COPY statements | `grep -c "COPY "` |
| I/O Operations | 20% | READ, WRITE, REWRITE, OPEN, CLOSE, START, READNEXT, READPREV, ENDBR, EXEC CICS READ/WRITE, EXEC DLI, MQ calls | `grep -c` combined |
| Branching Density | 20% | Count of IF/EVALUATE statements | `grep -c "IF\|EVALUATE"` |
| Inter-Program Dependencies | 15% | CALL + XCTL + LINK targets + files shared with other programs | Call graph analysis |

**Composite Score** = (LOC_norm × 0.25) + (CPY_norm × 0.20) + (IO_norm × 0.20) + (BRANCH_norm × 0.20) + (DEPS_norm × 0.15)

All scores normalized to the program with the highest value in each dimension = 100.

---

## 2. Top 10 Hotspot Programs

### Rank #1: COACTUPC.cbl — Account Update (Online/CICS)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 4,236 | 100.0 |
| Copybook refs | 58 | 100.0 |
| I/O operations | 23 | 19.5 |
| IF/EVALUATE stmts | 174 | 100.0 |
| Inter-program deps | 6 (3 VSAM files shared, XCTL to/from 2 programs, COMMAREA) | 60.0 |

**Composite Score: 80.5**

**Why it's #1:** Largest program in the estate by every measure except raw I/O count. Contains 359 branching paths (174 IF/EVALUATE statements with nesting depth up to 85). Uses `COPY REPLACING` 39 times for `CSSETATY` screen attribute macros, inflating copybook count. Performs exhaustive field validation: date (CSUTLDPY), SSN, phone area code (CSLKPCDY), US state code, ZIP prefix, FICO score range. Touches 3 VSAM files (account, card-xref, customer) with both READ and REWRITE.

**Modernization approach:** Split into 4 components — `AccountService` (business logic), `AccountValidationService` (extract all validation rules from CSLKPCDY/CSUTLDPY), `AccountController` (REST API replacing BMS), `AccountView` (modern UI replacing 3270 screen). Generate regression test suite before rewrite.

---

### Rank #2: COTRTLIC.cbl — Transaction Type List (Online/CICS+DB2)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 2,098 | 49.5 |
| Copybook refs | 12 | 20.7 |
| I/O operations | 27 | 22.9 |
| IF/EVALUATE stmts | 68 | 39.1 |
| Inter-program deps | 5 (2 DB2 tables, XCTL to/from 2 programs, COMMAREA) | 50.0 |

**Composite Score: 37.8**

**Why it's #2:** Second-largest program. Implements cursor-based DB2 pagination (DECLARE CURSOR/OPEN/FETCH/CLOSE) with forward/backward scrolling and inline update/delete. Already uses SQL — closest to modern database patterns. Complex screen state management with STARTBR/READNEXT/READPREV/ENDBR cycle.

**Modernization approach:** Natural entry point for DB2 migration. Map to JPA entities (`TransactionType`, `TransactionCategory`). Replace cursor pagination with Spring Data `Pageable`. SQL translates with minimal changes.

---

### Rank #3: COTRTUPC.cbl — Transaction Type Maintenance (Online/CICS+DB2)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 1,702 | 40.2 |
| Copybook refs | 15 | 25.9 |
| I/O operations | 30 | 25.4 |
| IF/EVALUATE stmts | 66 | 37.9 |
| Inter-program deps | 5 (2 DB2 tables, cascading delete, XCTL, COMMAREA) | 50.0 |

**Composite Score: 36.3**

**Why it's #3:** CRUD operations with cascading category deletes — when a transaction type is deleted, all associated categories in TRCAT are also deleted. Uses SYNCPOINT for transaction control. Tightly coupled with COTRTLIC — should migrate as a pair.

**Modernization approach:** Pair with COTRTLIC. JPA `@OneToMany` with `CascadeType.ALL` for type→category relationship. DB2 SYNCPOINT maps to `@Transactional`.

---

### Rank #4: COCRDUPC.cbl — Credit Card Update (Online/CICS)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 1,560 | 36.8 |
| Copybook refs | 16 | 27.6 |
| I/O operations | 12 | 10.2 |
| IF/EVALUATE stmts | 80 | 46.0 |
| Inter-program deps | 4 (CARDFILE R/W, XCTL to/from COMEN01C, COMMAREA) | 40.0 |

**Composite Score: 33.6**

**Why it's #4:** High branching density (80 IF/EVALUATE, nesting depth 85 — highest in estate). Validates card fields before REWRITE. Pattern is similar to COACTUPC but scoped to card entity only.

**Modernization approach:** Extract as `CardService` with `CardController`. Reuse validation pattern from COACTUPC migration.

---

### Rank #5: COCRDLIC.cbl — Credit Card List (Online/CICS)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 1,459 | 34.4 |
| Copybook refs | 14 | 24.1 |
| I/O operations | 16 | 13.6 |
| IF/EVALUATE stmts | 68 | 39.1 |
| Inter-program deps | 4 (CARDFILE browse, XCTL to COCRDSLC/COCRDUPC, COMMAREA) | 40.0 |

**Composite Score: 31.7**

**Why it's #5:** Implements the paginated browse pattern (STARTBR/READNEXT/ENDBR) used by 5+ programs. Nesting depth 73. Converting this pattern creates a reusable template for COTRN00C, COUSR00C, and COPAUS0C.

**Modernization approach:** Create generic `AbstractListController<T>` with Spring Data pagination. First instance handles cards; pattern reused for transactions, users, and authorization records.

---

### Rank #6: COPAUA0C.cbl — Authorization Decision Engine (Online/CICS+IMS+MQ)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 1,026 | 24.2 |
| Copybook refs | 17 | 29.3 |
| I/O operations | 27 | 22.9 |
| IF/EVALUATE stmts | 31 | 17.8 |
| Inter-program deps | 9 (3 MQ queues, 2 IMS segments, 3 VSAM files, COMMAREA) | 90.0 |

**Composite Score: 35.0**

**Why it's #6 (but Priority 3 for migration):** Most architecturally complex program — spans CICS, IMS, and MQ in a single execution flow. Moderate LOC but highest dependency count. Reads MQ request, queries IMS auth database (GU/REPL/ISRT), reads 3 VSAM files (XREF, ACCT, CUST), and sends MQ response. Must be migrated as a unit with COPAUS0C/1C/2C.

**Modernization approach:** Spring JMS for MQ replacement. IMS hierarchy (CIPAUSMY root → CIPAUDTY dependent) maps to 2 relational tables with FK. CICS READ calls become JPA repository lookups. Phase carefully — this is the integration boundary with external systems.

---

### Rank #7: COPAUS0C.cbl — Pending Auth Summary Browse (Online/CICS+IMS)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 1,032 | 24.4 |
| Copybook refs | 15 | 25.9 |
| I/O operations | 11 | 9.3 |
| IF/EVALUATE stmts | 36 | 20.7 |
| Inter-program deps | 6 (2 IMS segments, 3 VSAM files, LINK to COPAUS1C) | 60.0 |

**Composite Score: 27.2**

**Why it's #7:** Part of the IMS authorization chain (COPAUS0C→1C→2C). Uses IMS GU/GNP for hierarchical traversal. LINK (not XCTL) to COPAUS1C means it expects return — more complex than simple navigation.

---

### Rank #8: CBSTM03A.CBL — Statement Generation (Batch)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 924 | 21.8 |
| Copybook refs | 4 | 6.9 |
| I/O operations | 118 | 100.0 |
| IF/EVALUATE stmts | 22 | 12.6 |
| Inter-program deps | 5 (CALL CBSTM03B ×12, 4 VSAM files, 2 output files) | 50.0 |

**Composite Score: 36.1**

**Why it's #8 (but Priority 1 for migration pilot):** Highest I/O density in the entire estate (118 operations — 97 WRITEs for text/HTML output). Self-contained batch with no CICS dependency. Calls CBSTM03B 12 times as I/O submodule. Generates both text and HTML statements.

**Modernization approach:** **Best candidate for migration pilot** — self-contained, no CICS, measurable output. Spring Batch job with `JdbcBatchItemWriter`. Replace 97 individual WRITEs with Thymeleaf template-based bulk generation. CBSTM03B folds into the ItemReader/ItemWriter pattern.

---

### Rank #9: COACTVWC.cbl — Account View (Online/CICS)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 941 | 22.2 |
| Copybook refs | 16 | 27.6 |
| I/O operations | 8 | 6.8 |
| IF/EVALUATE stmts | 33 | 19.0 |
| Inter-program deps | 4 (3 VSAM files read-only, XCTL, COMMAREA) | 40.0 |

**Composite Score: 23.5**

**Why it's #9:** Read-only account display with card and customer info lookups. Lower risk — no writes. Shares data access patterns with COACTUPC.

**Modernization approach:** Migrate alongside COACTUPC as the read-only endpoint of `AccountService`. Simple `GET /accounts/{id}` REST endpoint.

---

### Rank #10: COCRDSLC.cbl — Credit Card Detail View (Online/CICS)

| Metric | Value | Normalized |
|--------|-------|-----------|
| LOC | 887 | 20.9 |
| Copybook refs | 16 | 27.6 |
| I/O operations | 7 | 5.9 |
| IF/EVALUATE stmts | 37 | 21.3 |
| Inter-program deps | 3 (CARDFILE + ACCTFILE read, XCTL, COMMAREA) | 30.0 |

**Composite Score: 21.6**

**Why it's #10:** Read-only card detail with cross-entity lookup (card → account → customer). Pattern matches COACTVWC.

---

## 3. Full Program Ranking (All 44)

| Rank | Program | LOC | Copybooks | I/O Ops | IF/EVAL | Score | Classification |
|------|---------|-----|-----------|---------|---------|-------|---------------|
| 1 | COACTUPC.cbl | 4,236 | 58 | 23 | 174 | 80.5 | Online (CICS) |
| 2 | COTRTLIC.cbl | 2,098 | 12 | 27 | 68 | 37.8 | Online (CICS+DB2) |
| 3 | COTRTUPC.cbl | 1,702 | 15 | 30 | 66 | 36.3 | Online (CICS+DB2) |
| 4 | CBSTM03A.CBL | 924 | 4 | 118 | 22 | 36.1 | Batch |
| 5 | COPAUA0C.cbl | 1,026 | 17 | 27 | 31 | 35.0 | Online (CICS+IMS+MQ) |
| 6 | COCRDUPC.cbl | 1,560 | 16 | 12 | 80 | 33.6 | Online (CICS) |
| 7 | COCRDLIC.cbl | 1,459 | 14 | 16 | 68 | 31.7 | Online (CICS) |
| 8 | COPAUS0C.cbl | 1,032 | 15 | 11 | 36 | 27.2 | Online (CICS+IMS) |
| 9 | CBTRN02C.cbl | 731 | 5 | 24 | 48 | 24.3 | Batch |
| 10 | COACTVWC.cbl | 941 | 16 | 8 | 33 | 23.5 | Online (CICS) |
| 11 | CBACT04C.cbl | 652 | 5 | 20 | 43 | 22.0 | Batch |
| 12 | COCRDSLC.cbl | 887 | 16 | 7 | 37 | 21.6 | Online (CICS) |
| 13 | CBTRN03C.cbl | 649 | 5 | 23 | 40 | 21.3 | Batch |
| 14 | COTRN02C.cbl | 783 | 11 | 9 | 27 | 19.0 | Online (CICS) |
| 15 | COTRN00C.cbl | 699 | 8 | 8 | 34 | 17.5 | Online (CICS) |
| 16 | COUSR00C.cbl | 695 | 9 | 7 | 33 | 16.8 | Online (CICS) |
| 17 | CORPT00C.cbl | 649 | 8 | 5 | 25 | 14.9 | Online (CICS) |
| 18 | CBEXPORT.cbl | 582 | 6 | 29 | 14 | 14.8 | Batch |
| 19 | COPAUS1C.cbl | 604 | 11 | 8 | 18 | 14.2 | Online (CICS+IMS) |
| 20 | COACCT01.cbl | 620 | 7 | 29 | 8 | 14.0 | Online (CICS+MQ) |
| 21 | CODATE01.cbl | 524 | 6 | 27 | 6 | 12.3 | Online (CICS+MQ) |
| 22 | COBIL00C.cbl | 572 | 11 | 9 | 21 | 14.1 | Online (CICS) |
| 23 | CBIMPORT.cbl | 487 | 6 | 29 | 10 | 13.0 | Batch |
| 24 | CBTRN01C.cbl | 494 | 6 | 18 | 33 | 15.3 | Batch |
| 25 | CBACT01C.cbl | 430 | 2 | 16 | 10 | 9.7 | Batch |
| 26 | COUSR02C.cbl | 414 | 9 | 5 | 18 | 10.8 | Online (CICS) |
| 27 | CBPAUP0C.cbl | 386 | 2 | 22 | 8 | 9.5 | Batch (IMS) |
| 28 | PAUDBLOD.CBL | 369 | 4 | 14 | 11 | 9.3 | Batch (IMS) |
| 29 | DBUNLDGS.CBL | 366 | 6 | 17 | 8 | 9.6 | Batch (IMS/GSAM) |
| 30 | COUSR03C.cbl | 359 | 9 | 5 | 14 | 9.4 | Online (CICS) |
| 31 | COTRN01C.cbl | 330 | 9 | 4 | 12 | 8.5 | Online (CICS) |
| 32 | PAUDBUNL.CBL | 317 | 4 | 15 | 8 | 8.0 | Batch (IMS) |
| 33 | COMEN01C.cbl | 308 | 10 | 0 | 14 | 7.8 | Online (CICS) |
| 34 | COUSR01C.cbl | 299 | 10 | 3 | 10 | 7.4 | Online (CICS) |
| 35 | COADM01C.cbl | 288 | 10 | 0 | 8 | 6.5 | Online (CICS) |
| 36 | COSGN00C.cbl | 260 | 10 | 3 | 10 | 7.0 | Online (CICS) |
| 37 | COPAUS2C.cbl | 244 | 1 | 3 | 4 | 3.7 | Online (CICS+DB2) |
| 38 | COBTUPDT.cbl | 237 | 1 | 5 | 5 | 4.0 | Batch (DB2) |
| 39 | CBSTM03B.CBL | 230 | 0 | 17 | 3 | 5.1 | Batch (submodule) |
| 40 | CBCUS01C.cbl | 178 | 1 | 4 | 4 | 2.8 | Batch |
| 41 | CBACT03C.cbl | 178 | 1 | 4 | 12 | 3.6 | Batch |
| 42 | CBACT02C.cbl | 178 | 1 | 4 | 12 | 3.6 | Batch |
| 43 | CSUTLDTC.cbl | 157 | 0 | 0 | 4 | 1.4 | Utility |
| 44 | COBSWAIT.cbl | 41 | 0 | 0 | 0 | 0.0 | Utility |

---

## 4. Modernization Recommendations

### Phase 0: Foundation (Weeks 1–4)

**Objective:** Establish migration infrastructure before touching any business programs.

- Set up Spring Boot project structure with shared libraries
- Create `carddemo-validation` library (extract from CSLKPCDY, CSUTLDPY)
- Create `carddemo-commons` library (from COCOM01Y DTO patterns)
- Set up PostgreSQL schema (15 tables from 8 VSAM files + 2 DB2 tables + 2 IMS segments)
- Implement VSAM-to-PostgreSQL data migration scripts

### Phase 1: Pilot — Reporting Service (Weeks 5–9)

**Programs:** CBSTM03A (#8), CBSTM03B, CBTRN03C (#13), CORPT00C (#17)

**Why first:**
- Self-contained batch — no CICS dependencies, no dual-write risk
- CBSTM03A has highest I/O density (118 ops) — proves batch migration approach
- Output is easily comparable (text/HTML files) for validation
- Low risk — reporting is read-only against master data

### Phase 2: DB2 Programs — Transaction Type Service (Weeks 10–16)

**Programs:** COTRTLIC (#2), COTRTUPC (#3), COBTUPDT (#38)

**Why second:**
- Already use SQL — minimal query translation needed
- Creates precedent for DB2-to-PostgreSQL migration patterns
- Cursor-based pagination (COTRTLIC) maps to Spring Data `Pageable`
- Cascading deletes (COTRTUPC) map to JPA `CascadeType.ALL`

### Phase 3: Card Service + User Service (Weeks 17–23)

**Programs:** COCRDLIC (#7), COCRDSLC (#10), COCRDUPC (#6), COUSR00C-03C

**Why third:**
- Card programs share the paginated browse pattern with Phase 1
- User service (COUSR00C-03C) is architecturally simple (single VSAM file, CRUD only)
- Creates `AbstractListController` pattern reusable for remaining screens

### Phase 4: Account Service — The Big One (Weeks 24–33)

**Programs:** COACTUPC (#1), COACTVWC (#9), COACCT01 (#20)

**Why here (not earlier):**
- COACTUPC is the riskiest program — 4,236 LOC, 174 branching statements
- By Phase 4, validation library and REST patterns are battle-tested
- Dual-write to VSAM + PostgreSQL needed during transition (3 files affected)
- Requires exhaustive regression testing (generate 10,000+ test cases)
- Account file has **highest contention** — written by 5 programs across 4 contexts

### Phase 5: Authorization Service — IMS/MQ Integration (Weeks 34–41)

**Programs:** COPAUA0C (#5), COPAUS0C (#8), COPAUS1C (#19), COPAUS2C (#37), CBPAUP0C (#27), PAUDBLOD (#28), PAUDBUNL (#32), DBUNLDGS (#29)

**Why last:**
- Most architecturally complex — spans CICS + IMS + MQ
- IMS hierarchical-to-relational mapping requires careful design
- MQ replacement (SQS/JMS) needs parallel operation for validation
- Authorization is the external integration point — highest risk of business disruption

### Phase 6: Batch Pipeline + Decommission (Weeks 42–48)

**Programs:** CBTRN02C (#9), CBACT04C (#11), CBTRN01C (#24), CBEXPORT (#18), CBIMPORT (#23), COBIL00C (#22)

**Why last:**
- Batch programs can run unchanged alongside modernized online programs
- Spring Batch migration is mechanical (READ→ItemReader, WRITE→ItemWriter)
- COBIL00C (bill payment) depends on account service being stable
- CBEXPORT/CBIMPORT may be retired once PostgreSQL migration is complete

---

## 5. Risk Matrix

| Risk | Impact | Mitigation |
|------|--------|-----------|
| COACTUPC validation logic loss | HIGH | Extract all 39 CSSETATY rules + CSLKPCDY lookups into test suite before rewriting |
| ACCTFILE dual-write inconsistency | HIGH | SQS FIFO sync + hourly reconciliation (count + checksum) |
| IMS hierarchical traversal semantics | MEDIUM | Map GNP ("get next within parent") to SQL subquery with parent FK |
| COBOL fixed-point arithmetic drift | HIGH | **Mandatory**: Use `java.math.BigDecimal` for ALL monetary fields. Map PIC S9(10)V99 → BigDecimal(12,2) |
| Batch pipeline SLA regression | MEDIUM | Benchmark Spring Batch against COBOL runtime; target ≤ 110% of original |
| MQ message format change | MEDIUM | Build COBOL-record-to-JSON translation layer; run parallel for 1 week |
| Plain-text passwords in CSUSR01Y | LOW (fix) | Hash with BCrypt during migration — one-time bulk conversion |
