# HOTSPOT REPORT — CardDemo COBOL Estate

## Scoring Methodology

Each program is scored across 5 dimensions (0–20 points each, max 100):

| Dimension | Weight | Metric | Scoring |
|-----------|--------|--------|---------|
| **Lines of Code** | 20 | `wc -l` | >2000 → 20, >1000 → 15, >500 → 10, >200 → 5, else 2 |
| **Copybook Count** | 20 | COPY statements | >15 → 20, >10 → 15, >7 → 10, >3 → 5, else 2 |
| **I/O Operations** | 20 | READ/WRITE/REWRITE/DELETE/STARTBR/EXEC SQL/DLI | >50 → 20, >20 → 15, >10 → 10, >3 → 5, else 2 |
| **Business Logic Density** | 20 | IF + EVALUATE count × max nesting depth | >200 → 20, >100 → 15, >50 → 10, >20 → 5, else 2 |
| **Inter-Program Dependencies** | 20 | Programs that depend on or are depended on by this program | >10 → 20, >5 → 15, >3 → 10, >1 → 5, else 2 |

---

## Top 10 Programs by Hotspot Score

| Rank | Program | LOC | Copybooks | I/O Ops | IF+EVAL | Max Nest | Logic Score | Dependencies | **Total Score** |
|------|---------|-----|-----------|---------|---------|----------|-------------|-------------|-----------------|
| **1** | **COACTUPC.cbl** | 4,236 | 58 | 23 | 184 | 4 | 736 | 16 (3 VSAM files, 13 copybook clusters, COMMAREA hub) | **95** |
| **2** | **COTRTLIC.cbl** | 2,098 | 12 | 43 | 32 | 5 | 160 | 5 (DB2 cursors, COADM01C caller, COTRTUPC sibling) | **75** |
| **3** | **COCRDUPC.cbl** | 1,560 | 16 | 5 | 88 | 3 | 264 | 6 (COCRDLIC caller, CARDDAT, CUSTDAT, COMMAREA) | **70** |
| **4** | **COTRTUPC.cbl** | 1,702 | 15 | 37 | 26 | 3 | 78 | 5 (DB2 CRUD, cascading deletes, COADM01C caller) | **70** |
| **5** | **COCRDLIC.cbl** | 1,459 | 14 | 15 | 77 | 4 | 308 | 5 (dispatches to COCRDSLC/COCRDUPC, STARTBR pattern) | **70** |
| **6** | **COPAUA0C.cbl** | 1,026 | 17 | 15 | 36 | 3 | 108 | 8 (MQ+IMS+CICS+3 VSAM files, most subsystems) | **70** |
| **7** | **COPAUS0C.cbl** | 1,032 | 15 | 4 | 47 | 4 | 188 | 5 (IMS browse, LINK to COPAUS1C, 4 VSAM files) | **65** |
| **8** | **CBSTM03A.CBL** | 924 | 5 | 118 | 24 | 3 | 72 | 3 (CALL CBSTM03B, 4 VSAM files via submodule) | **62** |
| **9** | **CBTRN02C.cbl** | 731 | 6 | 24 | 48 | 3 | 144 | 6 (4 VSAM files R/W, feeds entire downstream pipeline) | **60** |
| **10** | **COACTVWC.cbl** | 941 | 16 | 4 | 38 | 2 | 76 | 5 (reads 3 VSAM files, complex field stripping) | **55** |

---

## Detailed Hotspot Analysis

### #1 — COACTUPC.cbl (Account Update) — Score: 95/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 4,236 | 20 |
| Copybooks | 58 (incl. 13× CSSETATY via COPY REPLACING) | 20 |
| I/O Ops | 23 (CICS READ/REWRITE on ACCTDAT, CARDXREF, CUSTDAT) | 15 |
| Logic Density | 164 IF + 20 EVALUATE, max nest depth 4 → score 736 | 20 |
| Dependencies | 16 connections (3 VSAM files, called from COMEN01C, refs 13 copybook clusters) | 20 |

**Why it's #1:**
- Largest program by far (4,236 LOC — 2× the next largest)
- Contains exhaustive field-level validation: dates (CCYY-MM-DD), SSN (9-digit numeric), phone (NANPA area code), state code (50 states), ZIP prefix (state-to-ZIP table), FICO score (range 300–850)
- Uses COPY REPLACING macro pattern (CSSETATY ×13 instances) — generates repeated BMS attribute-setting code with variable substitution
- Tightly couples UI logic (BMS screen painting) with business validation
- Touches 3 core VSAM files (ACCTDAT, CARDXREF, CUSTDAT)

**Modernization Recommendation:**
Split into 4 components: `AccountService` (CRUD), `AccountValidationService` (date/SSN/phone/state/ZIP rules), `AccountController` (REST API), and a modern UI view. Extract validation into reusable Java classes that can serve other services. The COPY REPLACING pattern becomes parameterized utility methods. **Migrate in Phase 3** (after foundation and pilot phases) due to high risk — generate extensive test cases against current VSAM data before rewriting.

---

### #2 — COTRTLIC.cbl (Transaction Type List — DB2) — Score: 75/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 2,098 | 20 |
| Copybooks | 12 | 15 |
| I/O Ops | 43 (EXEC SQL DECLARE/OPEN/FETCH/CLOSE cursors) | 15 |
| Logic Density | 32 EVALUATE, max nest depth 5 → score 160 | 15 |
| Dependencies | 5 (COADM01C caller, COTRTUPC sibling, DB2 tables) | 10 |

**Why it's #2:**
- Already uses DB2 SQL — closest existing code to modern RDBMS patterns
- Cursor-based pagination with forward/backward scrolling — a reusable pattern
- Deepest nesting (5 levels) in the estate due to nested EVALUATE blocks for cursor state management
- 2,098 LOC is the second-largest program

**Modernization Recommendation:**
Natural entry point for database modernization. DB2 cursors translate directly to Spring Data JPA pagination (`Pageable`/`Page<T>`). SQL statements need minimal changes. **Migrate in Phase 2** (early) — relatively low risk since DB2 SQL is already close to PostgreSQL/standard SQL. Create a generic `AbstractPaginatedListService` pattern that can be reused by COCRDLIC, COTRN00C, COUSR00C, and COPAUS0C.

---

### #3 — COCRDUPC.cbl (Credit Card Update) — Score: 70/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,560 | 15 |
| Copybooks | 16 | 20 |
| I/O Ops | 5 (CICS READ/REWRITE CARDDAT) | 5 |
| Logic Density | 72 IF + 16 EVALUATE, max nest depth 3 → score 264 | 20 |
| Dependencies | 6 (COCRDLIC dispatches to it, CARDDAT, CUSTDAT, COMMAREA) | 10 |

**Why it's #3:**
- Dense validation logic (88 branching statements) despite moderate LOC
- Card field validation (embossed name, status, expiry date) mirrors COACTUPC's pattern at smaller scale
- Good candidate for extracting a `CardValidationService` that parallels the account validation approach

**Modernization Recommendation:**
Migrate alongside COCRDLIC and COCRDSLC as the **Card Service** microservice. **Phase 2** — moderate complexity, well-contained scope. The STARTBR/READNEXT browse pattern from COCRDLIC should be generalized first.

---

### #4 — COTRTUPC.cbl (Transaction Type Maintenance — DB2) — Score: 70/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,702 | 15 |
| Copybooks | 15 | 15 |
| I/O Ops | 37 (EXEC SQL SELECT/INSERT/UPDATE/DELETE) | 15 |
| Logic Density | 26 EVALUATE, max nest depth 3 → score 78 | 10 |
| Dependencies | 5 (COADM01C caller, COTRTLIC sibling, DB2 tables with cascading delete) | 15 |

**Why it's #4:**
- Full CRUD with cascading deletes (delete type → delete all child categories)
- DB2 operations translate directly to JPA `CascadeType.REMOVE`
- Sibling to COTRTLIC — must migrate as a pair

**Modernization Recommendation:**
Migrate as a unit with COTRTLIC. **Phase 2** — JPA entities for `TransactionType` and `TransactionCategory` with `@OneToMany(cascade = CascadeType.ALL)`. The cascading delete pattern maps cleanly.

---

### #5 — COCRDLIC.cbl (Credit Card List) — Score: 70/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,459 | 15 |
| Copybooks | 14 | 15 |
| I/O Ops | 15 (CICS STARTBR/READNEXT/READPREV/ENDBR on CARDDAT) | 10 |
| Logic Density | 59 IF + 18 EVALUATE, max nest depth 4 → score 308 | 20 |
| Dependencies | 5 (dispatches to COCRDSLC/COCRDUPC, COMEN01C caller) | 10 |

**Why it's #5:**
- Implements the **paginated VSAM browse pattern** (STARTBR/READNEXT/READPREV/ENDBR) used by 5+ programs
- This is a key architectural pattern — solving it once enables reuse across COTRN00C, COUSR00C, COPAUS0C
- High branching density for scroll state management

**Modernization Recommendation:**
Extract the browse/pagination pattern into a generic `AbstractBrowseController<T>` in Java. Spring Data's `Pageable` replaces STARTBR/READNEXT. **Phase 2** — migrate with COCRDUPC and COCRDSLC as the Card Service.

---

### #6 — COPAUA0C.cbl (Authorization Decision Engine) — Score: 70/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,026 | 15 |
| Copybooks | 17 | 20 |
| I/O Ops | 15 (MQOPEN/MQGET/MQPUT1 + DLI GU/GNP + CICS READ ×3 files) | 10 |
| Logic Density | 26 IF + 10 EVALUATE, max nest depth 3 → score 108 | 15 |
| Dependencies | 8 (spans MQ + IMS + CICS + 3 VSAM files — most subsystems of any program) | 10 |

**Why it's #6:**
- **Most architecturally complex** program — spans 3 middleware subsystems (MQ, IMS, CICS)
- Real-time authorization flow: MQ request → IMS lookup → VSAM account check → MQ response
- Must migrate COPAUS0C/1C/2C as a unit (LINK chain)
- IMS hierarchical data → relational mapping is a non-trivial transformation

**Modernization Recommendation:**
**Phase 4** (late) — highest integration risk. Replace MQ with Spring JMS or Amazon SQS. Map IMS segments (CIPAUSMY summary + CIPAUDTY detail) to 2 PostgreSQL tables. Run MQ and SQS in parallel during transition. The COPAUS0C→1C→2C LINK chain becomes a single Authorization REST service.

---

### #7 — COPAUS0C.cbl (Pending Auth Summary Browse) — Score: 65/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,032 | 15 |
| Copybooks | 15 | 15 |
| I/O Ops | 4 (DLI GU/GNP + CICS READ) | 5 |
| Logic Density | 25 IF + 22 EVALUATE, max nest depth 4 → score 188 | 15 |
| Dependencies | 5 (IMS browse, LINK to COPAUS1C, COMEN01C caller, 4 VSAM files) | 15 |

**Modernization Recommendation:**
Migrate with COPAUA0C as part of the Authorization Service. **Phase 4**. The IMS GU/GNP browse pattern becomes a JPA query with pagination.

---

### #8 — CBSTM03A.CBL (Statement Generation) — Score: 62/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 924 | 10 |
| Copybooks | 5 | 5 |
| I/O Ops | 118 (highest in estate — OPEN/CLOSE/READ/WRITE across multiple files + HTML generation) | 20 |
| Logic Density | 15 IF + 9 EVALUATE, max nest depth 3 → score 72 | 10 |
| Dependencies | 3 (CALL CBSTM03B, 4 VSAM files, no CICS) | 10 |

**Why it's #8:**
- **Highest I/O operation count** in the estate (118 ops)
- Self-contained batch program — no CICS dependency
- Generates both text and HTML output (dual-format statement)
- Calls CBSTM03B submodule for all file I/O

**Modernization Recommendation:**
**Phase 1 (Pilot candidate)** — despite ranking #8 by hotspot score, this is the **recommended first migration** because:
1. Self-contained batch with no CICS or IMS dependencies
2. Clear input/output contract (4 VSAM files → text + HTML files)
3. Proves the migration approach end-to-end
4. Spring Batch `ItemReader`/`ItemWriter` maps cleanly to the READ/WRITE pattern
5. Thymeleaf or FreeMarker replaces inline HTML generation
6. Output can be compared byte-for-byte against COBOL baseline

---

### #9 — CBTRN02C.cbl (Transaction Posting) — Score: 60/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 731 | 10 |
| Copybooks | 6 | 5 |
| I/O Ops | 24 (READ/WRITE/REWRITE across 4 VSAM files + reject file) | 15 |
| Logic Density | 48 IF, max nest depth 3 → score 144 | 15 |
| Dependencies | 6 (4 VSAM files R/W, feeds INTCALC+CREASTMT+TRANREPT downstream) | 15 |

**Why it's #9:**
- **Keystone of the daily batch pipeline** — everything downstream depends on CBTRN02C's output
- Validates and posts daily transactions; updates account balances and category balances
- Writes rejected transactions to DALYREJS for audit

**Modernization Recommendation:**
**Phase 5** (batch pipeline phase). Convert to Spring Batch step with `CompositeItemWriter` (writes to TRANSACT + updates ACCTDATA + updates TCATBALF). Must maintain SLA parity with mainframe. Use `JdbcBatchItemWriter` with batch size ≥ 1,000 for throughput.

---

### #10 — COACTVWC.cbl (Account View) — Score: 55/100

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 941 | 10 |
| Copybooks | 16 | 20 |
| I/O Ops | 4 (CICS READ on ACCTDAT, CARDXREF by AIX, CUSTDAT) | 5 |
| Logic Density | 28 IF + 10 EVALUATE, max nest depth 2 → score 76 | 10 |
| Dependencies | 5 (reads 3 VSAM files, COMEN01C caller, shares copybooks with COACTUPC) | 10 |

**Modernization Recommendation:**
**Phase 3** — migrate alongside COACTUPC as part of the Account Service. Since this is read-only, it's lower risk and can serve as a warmup before tackling the COACTUPC update logic. Shares the same copybook set, so the data model work done for COACTUPC applies directly.

---

## Recommended Modernization Sequence

Based on the hotspot analysis, dependency chains, and risk/reward tradeoffs:

| Phase | Programs | Rationale | Estimated Effort |
|-------|----------|-----------|-----------------|
| **P0: Foundation** | *(Infrastructure only)* | Set up Spring Boot, PostgreSQL schema, CI/CD, shared libraries (validation, date, money utils) | 3–4 weeks |
| **P1: Pilot** | CBSTM03A, CBSTM03B, COUSR00C–03C, COSGN00C | Self-contained batch (statement gen) + simple CRUD (user management) — proves the approach with lowest risk | 4–5 weeks |
| **P2: DB2 + Card** | COTRTLIC, COTRTUPC, COBTUPDT, COCRDLIC, COCRDSLC, COCRDUPC | Already SQL-based (DB2); card module is well-contained; establishes pagination pattern | 5–7 weeks |
| **P3: Account + Transaction** | COACTUPC, COACTVWC, COACCT01, COTRN00C–02C, COBIL00C | Highest-complexity programs; validation-heavy; largest LOC | 8–10 weeks |
| **P4: Authorization (IMS/MQ)** | COPAUA0C, COPAUS0C–2C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS | Cross-subsystem integration; IMS→RDBMS mapping; MQ→SQS migration | 6–8 weeks |
| **P5: Batch Pipeline** | CBTRN02C, CBACT04C, CBTRN03C, CBTRN01C, CBEXPORT, CBIMPORT | Core daily pipeline; must maintain SLA; can run in parallel with online during transition | 4–5 weeks |
| **P6: Decommission** | COBSWAIT, CSUTLDTC, COADM01C, COMEN01C, CORPT00C | Menu/utility programs retire when all targets are migrated; COBOL estate decommissioned | 2–3 weeks |

**Total estimated timeline: 32–42 weeks with a 4-person team**

---

## Key Risk Factors by Hotspot

| Risk | Affected Programs | Severity | Mitigation |
|------|-------------------|----------|------------|
| COPY REPLACING macro pattern | COACTUPC (×13 CSSETATY) | HIGH | Convert to parameterized Java methods; no direct Java equivalent |
| Plain-text passwords | CSUSR01Y → COSGN00C, COUSR00C–03C | HIGH | Implement bcrypt hashing + Spring Security in Phase 1 |
| IMS hierarchical→relational mapping | COPAUA0C, COPAUS0C–2C, CBPAUP0C | MEDIUM | 2 IMS segments (summary+detail) → 2 PostgreSQL tables with FK |
| MQ real-time message flow | COPAUA0C, COACCT01, CODATE01 | MEDIUM | Spring JMS adapter; parallel MQ+SQS during transition |
| VSAM dual-write consistency | All programs touching ACCTDATA (5 writers) | MEDIUM | SQS FIFO sync; hourly reconciliation; single source of truth per entity |
| COBOL fixed-point arithmetic | All monetary fields (PIC S9(n)V99) | HIGH | **Mandatory `BigDecimal`** for all monetary fields; never use `double`/`float`; use `RoundingMode.HALF_UP` |
| Assembler dependencies | COBDATFT, MVSWAIT, CEE3ABD, CEEDAYS | LOW | Replace with Java equivalents (`DateTimeFormatter`, `Thread.sleep()`, exceptions, `LocalDate`) |
| BMS screen↔logic coupling | All 21 CICS programs | MEDIUM | Extract business logic first; replace BMS with REST API + modern UI layer |
