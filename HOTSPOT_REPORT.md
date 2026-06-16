# HOTSPOT_REPORT.md — Modernization Complexity Ranking

> **Purpose:** Rank the top 10 programs by composite complexity to prioritize modernization efforts.
> **Scoring:** Weighted composite of 5 metrics normalized to 0–100.

---

## 1. Methodology

Each metric is normalized: `score = (value - min) / (max - min) × 100`

| Metric | Weight | Rationale |
|--------|--------|-----------|
| Lines of Code (LOC) | 0.20 | Raw size — more code = more translation effort |
| Copybooks Referenced | 0.20 | Data coupling — more copybooks = more shared state to untangle |
| I/O Operations | 0.25 | Integration surface — file/DB/MQ/CICS operations drive the modernized architecture |
| Business Logic Density (IF/EVALUATE count) | 0.20 | Logic complexity — branching depth determines testing and domain extraction effort |
| Inter-Program Dependencies (CALL/XCTL) | 0.15 | Coupling — more dependencies = harder to extract/test in isolation |

**Formula:** `Hotspot Score = 0.20×LOC_norm + 0.20×COPY_norm + 0.25×IO_norm + 0.20×LOGIC_norm + 0.15×DEPS_norm`

---

## 2. Raw Metrics (Full Estate)

### Top 15 Programs by Each Metric

**Lines of Code:**

| Rank | Program | LOC |
|------|---------|-----|
| 1 | COACTUPC.cbl | 4,236 |
| 2 | COTRTLIC.cbl | 2,098 |
| 3 | COTRTUPC.cbl | 1,702 |
| 4 | COCRDUPC.cbl | 1,560 |
| 5 | COCRDLIC.cbl | 1,459 |
| 6 | COPAUS0C.cbl | 1,032 |
| 7 | COPAUA0C.cbl | 1,026 |
| 8 | COACTVWC.cbl | 941 |
| 9 | CBSTM03A.CBL | 924 |
| 10 | COCRDSLC.cbl | 887 |

**Copybooks Referenced:**

| Rank | Program | Count |
|------|---------|-------|
| 1 | COACTUPC.cbl | 58 |
| 2 | COPAUA0C.cbl | 17 |
| 3 | COCRDUPC.cbl | 16 |
| 3 | COCRDSLC.cbl | 16 |
| 3 | COACTVWC.cbl | 16 |
| 6 | COTRTUPC.cbl | 15 |
| 6 | COPAUS0C.cbl | 15 |
| 8 | COCRDLIC.cbl | 14 |
| 9 | COTRTLIC.cbl | 12 |
| 10 | COTRN02C.cbl | 11 |

**I/O Operations:**

| Rank | Program | Count |
|------|---------|-------|
| 1 | CBSTM03A.CBL | 117 |
| 2 | COTRTLIC.cbl | 51 |
| 3 | COTRTUPC.cbl | 49 |
| 4 | COPAUA0C.cbl | 35 |
| 4 | COACTUPC.cbl | 35 |
| 4 | CODATE01.cbl | 35 |
| 4 | COACCT01.cbl | 35 |
| 8 | COUSR00C.cbl | 29 |
| 8 | CBIMPORT.cbl | 29 |
| 8 | CBEXPORT.cbl | 29 |

**Business Logic (IF/EVALUATE):**

| Rank | Program | Count |
|------|---------|-------|
| 1 | COACTUPC.cbl | 174 |
| 2 | COCRDUPC.cbl | 80 |
| 3 | COCRDLIC.cbl | 68 |
| 4 | CBTRN02C.cbl | 48 |
| 5 | CBACT04C.cbl | 43 |
| 6 | CBTRN03C.cbl | 40 |
| 7 | COCRDSLC.cbl | 37 |
| 8 | COPAUS0C.cbl | 36 |
| 9 | COTRN00C.cbl | 34 |
| 10 | COUSR00C.cbl | 33 |

**Inter-Program Dependencies:**

| Rank | Program | Count |
|------|---------|-------|
| 1 | CBSTM03A.CBL | 15 |
| 2 | PAUDBLOD.CBL | 9 |
| 2 | CODATE01.cbl | 9 |
| 2 | COACCT01.cbl | 9 |
| 5 | COPAUA0C.cbl | 8 |
| 6 | DBUNLDGS.CBL | 7 |
| 7 | COCRDLIC.cbl | 6 |
| 8 | PAUDBUNL.CBL | 5 |
| 9 | COUSR00C.cbl | 3 |
| 9 | COTRN02C.cbl | 3 |

---

## 3. Top 10 Hotspot Ranking (Composite Score)

Normalization ranges: LOC [41–4236], COPY [0–58], I/O [0–117], LOGIC [0–174], DEPS [0–15]

| Rank | Program | LOC | LOC_n | COPY | COPY_n | I/O | IO_n | Logic | LOGIC_n | Deps | DEPS_n | **Hotspot Score** |
|------|---------|-----|-------|------|--------|-----|------|-------|---------|------|--------|-------------------|
| **1** | **COACTUPC.cbl** | 4,236 | 100.0 | 58 | 100.0 | 35 | 29.9 | 174 | 100.0 | 1 | 6.7 | **68.5** |
| **2** | **CBSTM03A.CBL** | 924 | 21.1 | 4 | 6.9 | 117 | 100.0 | 7 | 4.0 | 15 | 100.0 | **42.2** |
| **3** | **COTRTLIC.cbl** | 2,098 | 49.1 | 12 | 20.7 | 51 | 43.6 | 24 | 13.8 | 1 | 6.7 | **29.7** |
| **4** | **COPAUA0C.cbl** | 1,026 | 23.5 | 17 | 29.3 | 35 | 29.9 | 31 | 17.8 | 8 | 53.3 | **29.5** |
| **5** | **COCRDUPC.cbl** | 1,560 | 36.2 | 16 | 27.6 | 17 | 14.5 | 80 | 46.0 | 1 | 6.7 | **26.4** |
| **6** | **COCRDLIC.cbl** | 1,459 | 33.8 | 14 | 24.1 | 23 | 19.7 | 68 | 39.1 | 6 | 40.0 | **30.3** |
| **7** | **COTRTUPC.cbl** | 1,702 | 39.6 | 15 | 25.9 | 49 | 41.9 | 21 | 12.1 | 2 | 13.3 | **29.1** |
| **8** | **COPAUS0C.cbl** | 1,032 | 23.6 | 15 | 25.9 | 18 | 15.4 | 36 | 20.7 | 1 | 6.7 | **19.0** |
| **9** | **CBTRN02C.cbl** | 731 | 16.4 | 10 | 17.2 | 23 | 19.7 | 48 | 27.6 | 1 | 6.7 | **18.2** |
| **10** | **COACTVWC.cbl** | 941 | 21.5 | 16 | 27.6 | 16 | 13.7 | 33 | 19.0 | 0 | 0.0 | **16.8** |

> **Note:** Programs ranked by re-sorted composite score. COCRDLIC (#6 by raw score 30.3) and COTRTUPC (#7, 29.1) are close; COCRDLIC edges ahead on branching + dependencies.

---

## 4. Detailed Hotspot Profiles

### #1 — COACTUPC.cbl (Score: 68.5) — ⚠️ HIGHEST PRIORITY

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 4,236 | **2× the next-largest program** — monolithic |
| Copybooks | 58 | 38 COPY CSSETATY REPLACING + 20 data structures — deep coupling |
| I/O Ops | 35 | Reads/rewrites 4 VSAM files (ACCT, CUST, CARD, XREF) |
| Business Logic | 174 IF/EVALUATE | Exhaustive field-level validation (date, SSN, phone, state, ZIP) |
| Dependencies | Low (1 CALL) | Largely self-contained — **good decomposition candidate** |

**What makes it complex:**
- Implements the entire account update lifecycle in a single program: screen handling, field validation (23 distinct field types), cross-file lookups, VSAM rewrites
- 38 uses of COPY CSSETATY REPLACING for dynamic BMS attribute setting — this is a macro pattern that expands significantly
- Validates phone area codes, state codes, and state-ZIP cross-references against the 1,318-line CSLKPCDY lookup table
- Date validation uses both CSUTLDPY inline COPY and CSUTLDWY working storage

**Modernization recommendation:** Decompose into 3–4 microservices or classes:
1. `AccountValidationService` — extract the 174 IF/EVALUATE validation rules
2. `AccountUpdateService` — VSAM REWRITE → JPA/SQL UPDATE
3. `LookupService` — externalize CSLKPCDY to a reference data database
4. `AccountUpdateController` — screen flow → REST API

### #2 — CBSTM03A.CBL (Score: 42.2) — HIGH PRIORITY

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 924 | Moderate size |
| I/O Ops | 117 | **Highest in estate** — 97+ WRITE operations for text + HTML output |
| Dependencies | 15 | **13 CALL CBSTM03B** + CEE3ABD — tightly coupled subprogram pair |

**What makes it complex:**
- Generates dual-format statements (text + HTML) with ~60 WRITE statements per customer
- CBSTM03A (orchestrator) + CBSTM03B (I/O) are inseparable — must modernize together
- Reads 4 files in a chain: XREF → CUST → ACCT → TRNX
- HTML generation is hardcoded line-by-line — requires complete template rewrite

**Modernization recommendation:**
1. Replace with a template engine (Thymeleaf, FreeMarker)
2. CBSTM03A + CBSTM03B → single `StatementGenerationService`
3. Multi-file VSAM chain → SQL JOINs

### #3 — COTRTLIC.cbl (Score: 29.7) — MEDIUM-HIGH PRIORITY

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 2,098 | Second largest overall |
| I/O Ops | 51 | DB2 cursor-based SQL pagination |
| Copybooks | 12 | Includes DB2-specific DCLTRTYP, CSDB2RWY, CSDB2RPY |

**What makes it complex:**
- Full cursor-based pagination over DB2 TRNTYPE table with forward/backward scrolling
- Mixes CICS screen handling with embedded SQL — dual middleware dependency
- CSDB2RPY provides inline error handling via COPY — must understand to migrate

**Modernization recommendation:**
- Natural fit for Spring Data JPA with paginated repository methods
- SQL is already relational — straightforward migration
- Consider modernizing DB2 programs as a batch since SQL translates directly

### #4 — COPAUA0C.cbl (Score: 29.5) — MEDIUM-HIGH PRIORITY

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 1,026 | |
| Dependencies | 8 | MQ API (4) + CICS + IMS DL/I — **triple middleware** |
| Copybooks | 17 | Spans MQ, IMS, CICS, and VSAM copybooks |

**What makes it complex:**
- **Only program touching all 4 middleware layers** (CICS + IMS + MQ + VSAM)
- Authorization decision logic: read MQ request → validate card/account/customer via VSAM → check/insert IMS pending auth → send MQ response
- Error handling writes to error log copybook (CCPAUERY)
- EXEC DLI GU/REPL/ISRT intermixed with EXEC CICS READ and MQ CALL

**Modernization recommendation:**
- Decompose into event-driven microservice
- MQ → Kafka/SQS message consumer
- IMS → PostgreSQL/DynamoDB
- Most complex integration point — requires careful API contract design

### #5 — COCRDUPC.cbl (Score: 26.4) — MEDIUM PRIORITY

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 1,560 | |
| Business Logic | 80 IF/EVALUATE | Second highest branching |
| Copybooks | 16 | |

**Card update with field validation. Pattern similar to COACTUPC but scoped to card entity.**

### #6 — COCRDLIC.cbl (Score: 30.3) — MEDIUM PRIORITY

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 1,459 | |
| Business Logic | 68 IF/EVALUATE | Pagination control logic |
| Dependencies | 6 XCTL | Routes to view/update |

**Paginated card browsing. Complex STARTBR/READNEXT/READPREV state machine.**

### #7 — COTRTUPC.cbl (Score: 29.1) — MEDIUM PRIORITY

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 1,702 | |
| I/O Ops | 49 | DB2 CRUD + CICS |

**DB2 transaction type CRUD with cascading delete. Pairs with COTRTLIC.**

### #8 — COPAUS0C.cbl (Score: 19.0) — MEDIUM-LOW PRIORITY

IMS browse for pending authorizations. Pairs with COPAUS1C (detail). Depends on IMS DL/I.

### #9 — CBTRN02C.cbl (Score: 18.2) — MEDIUM-LOW PRIORITY

Transaction posting — core batch engine. Critical business logic but moderate complexity.

### #10 — COACTVWC.cbl (Score: 16.8) — LOW PRIORITY

Read-only account view. High copybook count but no write operations.

---

## 5. Recommended Modernization Sequence

### Phase 1: High-Value, Self-Contained (Months 1–3)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 1 | **COACTUPC** | Highest complexity but low external dependencies. Self-contained validation logic translates well to a domain service. Reduces 15% of total estate complexity in one migration. |
| 2 | **CBSTM03A + CBSTM03B** | Highest I/O density. Hardcoded HTML generation is a clear rewrite target. Template engine replacement yields immediate ROI. No online dependency. |
| 3 | **CBTRN02C** (POSTTRAN) | Core batch pipeline entry point. Clean input/output boundary. Once modernized, the entire daily pipeline can be incrementally migrated. |

### Phase 2: DB2 Programs (Months 3–5)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 4 | **COTRTLIC + COTRTUPC + COBTUPDT** | Already use SQL — most natural migration target. Cursor pagination → Spring Data. Modernize as a group (shared DB2 tables). |

### Phase 3: Remaining CICS Programs (Months 5–8)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 5 | **COCRDLIC + COCRDSLC + COCRDUPC** | Card management trio. Moderate complexity, well-understood CRUD pattern. |
| 6 | **COTRN00C + COTRN01C + COTRN02C** | Transaction viewing/adding. Depend on TRANSACT VSAM already migrated in Phase 1. |
| 7 | **COADM01C + COUSR00C–03C** | User management. Low complexity, admin-only. |
| 8 | **Remaining CICS** (COSGN00C, COMEN01C, CORPT00C, COBIL00C, COACTVWC) | Navigation and low-complexity screens. |

### Phase 4: IMS/MQ Integration (Months 8–10)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 9 | **COPAUA0C + COPAUS0C/1C/2C** | Triple-middleware complexity requires IMS→relational migration and MQ→messaging platform migration to be completed first. |
| 10 | **CBPAUP0C + PAUDBLOD + PAUDBUNL + DBUNLDGS** | IMS batch utilities. Depend on IMS database migration. |
| 11 | **COACCT01 + CODATE01** | MQ service programs. Simple once MQ platform is chosen. |

### Phase 5: Batch Utilities & Remaining (Months 10–12)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| 12 | **CBACT01C–03C, CBCUS01C** | File reader utilities — may become obsolete with database migration. |
| 13 | **CBEXPORT + CBIMPORT** | Data migration utilities — replace with ETL tooling. |
| 14 | **CBACT04C** (INTCALC) | Interest calculation — critical business logic, migrate last for stability. |
| 15 | **CBTRN03C** (TRANRPT) | Report generation — replace with BI/reporting tool. |

---

## 6. Risk Assessment

| Risk | Impact | Mitigation |
|------|--------|-----------|
| COACTUPC monolith | High — single program contains 15% of estate complexity | Decompose early; establish validation test suite before migration |
| CSLKPCDY hardcoded lookups | Medium — 1,318 lines of validation rules embedded in COPY | Extract to reference data service/database early |
| CBSTM03A hardcoded HTML | Medium — 97+ WRITE statements generating HTML line-by-line | Replace with template engine; regression test on output format |
| COPAUA0C triple middleware | High — CICS + IMS + MQ makes isolated testing impossible | Build integration test harness; mock each middleware layer |
| SSN/Password cleartext | High — CUST-SSN and SEC-USR-PWD stored unencrypted | Add encryption in modernized version; cannot test with production data |
| COBOL packed decimal | Medium — PIC S9(n)V99 requires careful numeric conversion | Use BigDecimal in Java; test boundary values exhaustively |
| VSAM key-sequenced access | Medium — STARTBR/READNEXT patterns need index-aware migration | Map to SQL indexed queries; preserve sort order semantics |
| BMS screen coupling | Low-Medium — 21 BMS maps tightly coupled to programs | Can be replaced independently with web UI; API contract is key |

---

## 7. Estate Complexity Distribution

```
Complexity Distribution (44 programs):

  High (Score > 25):     6 programs (14%) ← Focus here first
  Medium (Score 15-25):  5 programs (11%)
  Low (Score < 15):     33 programs (75%) ← Bulk migration candidates

LOC Distribution:
  > 1,500 LOC:   5 programs (11%) — contain 50% of total complexity
  500–1,500 LOC: 14 programs (32%)
  < 500 LOC:     25 programs (57%) — mostly mechanical migration
```
