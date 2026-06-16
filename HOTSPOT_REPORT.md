# HOTSPOT REPORT — Modernization Complexity & Priority Ranking

## Executive Summary

This report ranks the CardDemo COBOL programs by composite complexity to identify which programs represent the highest modernization risk and which should be targeted first. The ranking uses five weighted dimensions:

| Dimension | Weight | Rationale |
|-----------|--------|-----------|
| Lines of Code (LOC) | 25% | Raw size correlates with effort and defect density |
| Copybook References | 15% | Data coupling — more copybooks = more interfaces to maintain |
| I/O Operations | 20% | Persistence layer complexity; each I/O point becomes a service boundary |
| Business Logic Density | 25% | IF/EVALUATE nesting depth is the #1 predictor of migration difficulty |
| Inter-Program Dependencies | 15% | Integration surface — programs with many callers/callees need careful sequencing |

---

## Top 10 Programs by Composite Hotspot Score

| Rank | Program | LOC | Copybooks | I/O Ops | Logic (IF/EVAL) | Nested Blocks | Deps | Hotspot Score |
|------|---------|-----|-----------|---------|-----------------|---------------|------|---------------|
| 1 | **COACTUPC.cbl** | 4,236 | 58 | 113 | 184 | 175 | 1 | **98.7** |
| 2 | **COTRTLIC.cbl** | 2,098 | 12 | 142 | 115 | 107 | 3 | **72.1** |
| 3 | **COTRTUPC.cbl** | 1,702 | 15 | 77 | 134 | 63 | 1 | **62.4** |
| 4 | **COCRDUPC.cbl** | 1,560 | 16 | 23 | 167 | 80 | 1 | **59.8** |
| 5 | **COCRDLIC.cbl** | 1,459 | 14 | 44 | 149 | 71 | 6 | **57.2** |
| 6 | **CBSTM03A.CBL** | 924 | 5 | 158 | 20 | 23 | 15 | **51.6** |
| 7 | **CBTRN03C.cbl** | 649 | 6 | 81 | 79 | 40 | 1 | **44.3** |
| 8 | **CBTRN02C.cbl** | 731 | 6 | 61 | 93 | 49 | 1 | **43.9** |
| 9 | **COPAUA0C.cbl** | 1,026 | 17 | 43 | 62 | 32 | 8 | **43.5** |
| 10 | **CBACT04C.cbl** | 652 | 6 | 52 | 86 | 44 | 1 | **41.7** |

---

## Detailed Analysis

### Rank 1: COACTUPC.cbl — Account Update (Online/CICS)
**LOC: 4,236 | Hotspot Score: 98.7**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 58 (uses COPY REPLACING 3×) | 99th |
| I/O Operations | 113 (EXEC CICS READ/REWRITE) | 95th |
| Business Logic | 184 IF/EVALUATE + 175 nested blocks | 99th |
| Dependencies | 1 outbound (returns to menu) | Low |

**Why it's #1:** This single program contains 14% of the entire estate's LOC. It handles account view, update, validation (SSN, ZIP, phone, state code, date), and customer cross-reference lookups — all in one monolith. The 58 copybook references include 3 uses of CSSETATY with COPY REPLACING to manage screen attribute states, making it the most complex single unit.

**Risk Factors:**
- Deeply nested validation logic (SSN format, ZIP prefix lookup against 999-entry table)
- Mixed concerns: presentation (BMS), validation, persistence, navigation
- Modifies 3 VSAM files (ACCTFILE, CARDXREF, CUSTFILE) in a single transaction
- Uses COPY REPLACING — a COBOL metaprogramming pattern with no direct Java equivalent

**Modernization Strategy:** Decompose into 4–5 microservices: AccountController, AccountValidator, AccountRepository, CustomerRepository, CardXrefRepository. Extract validation rules into a rules engine or Spring Validator beans.

---

### Rank 2: COTRTLIC.cbl — Transaction Type List (Online/CICS+DB2)
**LOC: 2,098 | Hotspot Score: 72.1**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 12 | 70th |
| I/O Operations | 142 (EXEC SQL FETCH cursors) | 97th |
| Business Logic | 115 IF/EVALUATE + 107 nested blocks | 90th |
| Dependencies | 3 (calls DSNTIAC for error formatting) | Medium |

**Why it's #2:** Extremely high I/O operation count driven by DB2 cursor-based pagination — open/fetch/close patterns repeated per page of results. The program implements forward/backward scrolling through transaction type/category hierarchies using repositioning logic.

**Risk Factors:**
- DB2 cursor management with host variable manipulation
- Complex SQLCODE handling (0, +100, -811, etc.) with DSNTIAC error formatting
- Manual pagination state management via COMMAREA
- Transaction type/category parent-child hierarchy in single screen

**Modernization Strategy:** Map to Spring Data JPA with `Pageable` for DB2 tables. Replace cursor logic with repository `findAll(Pageable)`. The DSNTIAC utility translates to standard JPA exception handling.

---

### Rank 3: COTRTUPC.cbl — Transaction Type Update (Online/CICS+DB2)
**LOC: 1,702 | Hotspot Score: 62.4**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 15 | 75th |
| I/O Operations | 77 (EXEC SQL INSERT/UPDATE/DELETE/SELECT) | 85th |
| Business Logic | 134 IF/EVALUATE + 63 nested blocks | 92nd |
| Dependencies | 1 | Low |

**Risk Factors:**
- Full CRUD with cascading deletes (type → category FK)
- DB2 optimistic locking via SQLCODE checks
- Complex screen state machine: View → Confirm → Execute → Result

**Modernization Strategy:** Standard Spring Boot CRUD controller with @Transactional cascading. Map screen states to REST endpoints: GET/POST/PUT/DELETE.

---

### Rank 4: COCRDUPC.cbl — Credit Card Update (Online/CICS)
**LOC: 1,560 | Hotspot Score: 59.8**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 16 | 78th |
| I/O Operations | 23 | 50th |
| Business Logic | 167 IF/EVALUATE + 80 nested blocks | 96th |
| Dependencies | 1 | Low |

**Risk Factors:**
- Very high business logic density relative to I/O — dense validation
- Card number Luhn check, expiry date validation, embossed name rules
- Field-level attribute management using CSSETATY COPY REPLACING

**Modernization Strategy:** Extract card validation as reusable `CardValidator` bean. Map to Spring MVC @Controller with Bean Validation annotations (@CreditCardNumber, @Future).

---

### Rank 5: COCRDLIC.cbl — Credit Card List (Online/CICS)
**LOC: 1,459 | Hotspot Score: 57.2**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 14 | 72nd |
| I/O Operations | 44 (STARTBR/READNEXT/READPREV/ENDBR) | 70th |
| Business Logic | 149 IF/EVALUATE + 71 nested blocks | 94th |
| Dependencies | 6 (XCTL to COCRDSLC, COCRDUPC, returns to menu) | High |

**Risk Factors:**
- VSAM browse pattern (STARTBR/READNEXT/READPREV) for pagination
- Complex screen navigation with PF7/PF8 page forward/backward
- Multiple XCTL targets from selection

**Modernization Strategy:** Spring Data with `Slice`/`Page` for paginated card list. Selection routing becomes REST links or controller redirect.

---

### Rank 6: CBSTM03A.CBL — Statement Generation (Batch)
**LOC: 924 | Hotspot Score: 51.6**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 5 | 40th |
| I/O Operations | 158 (highest absolute I/O count) | **99th** |
| Business Logic | 20 IF/EVALUATE + 23 nested blocks | 25th |
| Dependencies | 15 (calls CBSTM03B repeatedly as I/O submodule) | **Highest** |

**Why it matters:** Highest I/O operation count in the entire estate. The 158 operations reflect the volume of file reads/writes needed to cross-reference transactions × accounts × customers and produce per-account statements. Calls CBSTM03B (I/O submodule) for every file operation — 15 distinct CALL sites.

**Risk Factors:**
- Tight coupling to CBSTM03B — must migrate both as a unit
- Generates both text (PS) and HTML output simultaneously
- Sequential file processing with accumulator logic

**Modernization Strategy:** **Recommended pilot candidate** — self-contained batch with clear inputs/outputs. Map to Spring Batch `ItemReader`/`ItemProcessor`/`ItemWriter` with chunk processing. HTML generation becomes a template engine (Thymeleaf).

---

### Rank 7: CBTRN03C.cbl — Daily Transaction Report (Batch)
**LOC: 649 | Hotspot Score: 44.3**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 6 | 45th |
| I/O Operations | 81 | 87th |
| Business Logic | 79 IF/EVALUATE + 40 nested blocks | 80th |
| Dependencies | 1 | Low |

**Risk Factors:**
- Complex report formatting with page headers, subtotals, grand totals
- Multi-file cross-reference (transactions + card xref + type codes + category codes)
- SORT prerequisite in JCL before program execution

**Modernization Strategy:** Spring Batch with JasperReports or custom `FlatFileItemWriter`. The JCL SORT step becomes an ORDER BY clause or in-memory sort.

---

### Rank 8: CBTRN02C.cbl — Post Daily Transactions (Batch)
**LOC: 731 | Hotspot Score: 43.9**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 6 | 45th |
| I/O Operations | 61 | 82nd |
| Business Logic | 93 IF/EVALUATE + 49 nested blocks | 84th |
| Dependencies | 1 | Low |

**Risk Factors:**
- Critical business process — updates master transaction file and account balances
- Reject handling with error codes written to DALYREJS GDG
- Updates 3 files atomically: TRANSACT, ACCTFILE, XREFFILE
- Data integrity critical — must not lose transactions

**Modernization Strategy:** Spring Batch with @Transactional chunk processing. The multi-file update becomes a single database transaction. Reject records → error table or dead-letter queue.

---

### Rank 9: COPAUA0C.cbl — Authorization Engine (Online/CICS+IMS+MQ)
**LOC: 1,026 | Hotspot Score: 43.5**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 17 | 80th |
| I/O Operations | 43 (MQ + VSAM + IMS) | 68th |
| Business Logic | 62 IF/EVALUATE + 32 nested blocks | 70th |
| Dependencies | 8 (MQ API calls + VSAM reads + IMS DL/I) | **Very High** |

**Why it's critical:** Spans **four** middleware technologies in a single program: CICS, IMS, DB2, and MQ. This is the most integration-complex program in the estate. It reads authorization requests from MQ, queries IMS for auth history, checks VSAM for account/card/customer data, makes the auth decision, and writes the response back to MQ.

**Risk Factors:**
- Four-technology integration: CICS + IMS DL/I + VSAM + MQ
- Real-time authorization with latency SLA
- Complex decision logic (credit limit, card status, fraud flags, expiry)
- MQ message format parsing (CCPAURQY/CCPAURLY copybooks)

**Modernization Strategy:** **Do NOT attempt early.** Requires all dependent services (account, card, customer, IMS auth store) to be migrated first. Target as a final-phase event-driven microservice (Kafka/RabbitMQ consumer with REST calls to account/card services).

---

### Rank 10: CBACT04C.cbl — Interest Calculation (Batch)
**LOC: 652 | Hotspot Score: 41.7**

| Metric | Value | Percentile |
|--------|-------|-----------|
| Copybooks | 6 | 45th |
| I/O Operations | 52 | 76th |
| Business Logic | 86 IF/EVALUATE + 44 nested blocks | 82nd |
| Dependencies | 1 | Low |

**Risk Factors:**
- Financial calculation logic — interest rates, fee computation, balance updates
- Reads 4 reference files (category balance, xref, disclosure group, transactions)
- Updates account balances — must maintain penny-level precision (COMP-3 → BigDecimal)
- Disclosure group rate lookup logic

**Modernization Strategy:** Spring Batch with BigDecimal arithmetic. Interest calculation logic extracts as a `@Service` with unit-testable methods. Rate lookups from DISCGRP become a Spring Data repository.

---

## Modernization Sequencing Recommendation

### Phase 1 — Pilot (Low Risk, High Learning)
| Order | Program | Rationale |
|-------|---------|-----------|
| 1.1 | **CBSTM03A/B** | Self-contained batch; clear I/O boundaries; no external callers; generates measurable output (statements) for validation. Exercises file→DB mapping, batch framework, and template generation. |
| 1.2 | **CBTRN03C** | Pure reporting batch; no writes to master files; output can be validated against COBOL output byte-for-byte. Good second pilot after CBSTM03A proves the batch framework. |

### Phase 2 — Core Batch (Medium Risk)
| Order | Program | Rationale |
|-------|---------|-----------|
| 2.1 | **CBTRN02C** | Critical daily posting — migrate after batch framework proven. Enables transaction integrity testing. |
| 2.2 | **CBACT04C** | Interest calculation — financial precision critical. Migrate after CBTRN02C proves multi-file update patterns. |
| 2.3 | **CBEXPORT/CBIMPORT** | Data migration utilities — useful for populating Java-side databases. Self-contained. |

### Phase 3 — Online CICS (Medium-High Risk)
| Order | Program | Rationale |
|-------|---------|-----------|
| 3.1 | **COSGN00C + COMEN01C + COADM01C** | Navigation shell — minimal business logic, establishes routing framework (Spring Security + MVC). |
| 3.2 | **COACTVWC** | Read-only account view — tests VSAM→DB read patterns without write complexity. |
| 3.3 | **COCRDLIC + COCRDSLC** | Card list/view — tests pagination pattern (VSAM browse → Spring Data Page). |
| 3.4 | **COCRDUPC** | Card update — adds write + validation on proven patterns. |
| 3.5 | **COTRTLIC + COTRTUPC** | DB2 programs — already relational; most natural migration to JPA. |

### Phase 4 — Complex Online (High Risk)
| Order | Program | Rationale |
|-------|---------|-----------|
| 4.1 | **COACTUPC** | Highest complexity program. Migrate ONLY after account/customer/card services are stable. Decompose into 4+ microservices. |
| 4.2 | **COBIL00C + COTRN00C–02C** | Transaction processing online — depends on account update patterns being proven. |

### Phase 5 — Integration Programs (Highest Risk)
| Order | Program | Rationale |
|-------|---------|-----------|
| 5.1 | **COPAUS0C/1C/2C** | IMS-dependent — requires IMS→relational DB migration first. Migrate as a unit (LINK chain). |
| 5.2 | **COPAUA0C** | Most complex integration (4 technologies). Migrate LAST — requires all services to be running in Java before this can be replaced. |

---

## Risk Summary Matrix

| Risk Level | Programs | Key Challenge |
|------------|----------|---------------|
| 🔴 Critical | COACTUPC, COPAUA0C | Monolithic complexity; multi-technology integration |
| 🟠 High | COTRTLIC, COTRTUPC, CBTRN02C, CBACT04C | DB2 cursors; financial precision; data integrity |
| 🟡 Medium | COCRDUPC, COCRDLIC, CBTRN03C, COPAUS0C–2C | Validation density; IMS dependency |
| 🟢 Low | CBSTM03A/B, CBEXPORT, CBIMPORT, navigation programs | Self-contained; clear boundaries; good pilot candidates |
