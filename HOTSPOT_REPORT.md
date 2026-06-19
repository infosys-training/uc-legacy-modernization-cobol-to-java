# HOTSPOT REPORT — CardDemo Modernization Prioritization

## Methodology

Each program is scored across five dimensions:

| Dimension | Weight | Measurement |
|-----------|--------|-------------|
| Lines of Code | 25% | Raw LOC (larger = more effort) |
| Copybooks Referenced | 20% | Count of COPY statements (coupling) |
| I/O Operations | 20% | File/DB/MQ/CICS I/O statement count |
| Business Logic Density | 20% | IF + EVALUATE statement count (branching complexity) |
| Inter-Program Dependencies | 15% | Programs that call or are called by this program |

Scores are normalized to 0–100 scale where 100 = highest complexity in the estate.

---

## Top 10 Hotspot Programs (Ranked by Composite Score)

| Rank | Program | LOC | Copybooks | I/O Ops | Logic (IF+EVAL) | Dependencies | Composite Score |
|------|---------|-----|-----------|---------|----------------|--------------|----------------|
| 1 | **COACTUPC.cbl** | 4,236 | 56 | 10 | 188 | 6 | **74.8** |
| 2 | **COTRTLIC.cbl** | 2,098 | 11 | 11 | 120 | 4 | **42.3** |
| 3 | **COTRTUPC.cbl** | 1,702 | 13 | 9 | 78 | 4 | **36.1** |
| 4 | **COCRDUPC.cbl** | 1,560 | 15 | 9 | 90 | 4 | **34.8** |
| 5 | **COCRDLIC.cbl** | 1,459 | 13 | 14 | 79 | 4 | **33.5** |
| 6 | **COPAUA0C.cbl** | 1,026 | 16 | 15 | 61 | 5 | **31.2** |
| 7 | **COPAUS0C.cbl** | 1,032 | 14 | 8 | 47 | 3 | **28.6** |
| 8 | **CBSTM03A.CBL** | 924 | 4 | 117 | 24 | 3 | **28.2** |
| 9 | **COACTVWC.cbl** | 941 | 15 | 8 | 39 | 5 | **27.4** |
| 10 | **COTRN02C.cbl** | 783 | 10 | 7 | 40 | 4 | **24.1** |

---

## Detailed Hotspot Analysis

### 1. COACTUPC.cbl — Account Update (Score: 74.8)

**Why it's #1:** The single most complex program in the estate by every metric.

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 4,236 | 100th (largest) |
| Copybooks | 56 | 100th (most) |
| IF+EVALUATE | 188 | 100th (most branching) |
| CICS I/O | 10 (READ/REWRITE on 3 files) | 60th |
| Dependencies | 6 (called by COMEN01C; touches ACCTFILE, CARDXREF, CUSTFILE) | 85th |

**Complexity Drivers:**
- Exhaustive field validation: date format, SSN format, phone area code (350+ codes), US state code, ZIP code
- 3× `COPY REPLACING` for CSSETATY screen attribute macros (unique in estate)
- Manages 3 VSAM files simultaneously (account, card-xref, customer)
- 20 EVALUATE blocks for multi-path screen handling
- Mixed concerns: BMS screen logic interleaved with business validation

**Modernization Risk:** HIGH — Any mistake in validation logic can corrupt account data.

---

### 2. COTRTLIC.cbl — Transaction Type List / DB2 (Score: 42.3)

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 2,098 | 95th |
| Copybooks | 11 | 70th |
| IF+EVALUATE | 120 | 95th |
| DB2 I/O | 11 (CURSOR pagination) | 75th |
| Dependencies | 4 | 60th |

**Complexity Drivers:**
- DB2 cursor-based pagination with dynamic WHERE clauses
- 32 EVALUATE blocks for screen state management
- Complex error handling for SQLCODE interpretation
- Already uses SQL — closest to modern data access patterns

---

### 3. COTRTUPC.cbl — Transaction Type Update / DB2 (Score: 36.1)

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 1,702 | 90th |
| Copybooks | 13 | 75th |
| IF+EVALUATE | 78 | 85th |
| DB2 I/O | 9 (UPDATE/DELETE cascade) | 70th |
| Dependencies | 4 | 60th |

**Complexity Drivers:**
- Cascading DELETE across TRNTYPE and TRNTYCAT tables
- Referential integrity logic manually coded (no DB2 RI constraints)
- Complex screen state machine with confirm/cancel flows

---

### 4. COCRDUPC.cbl — Credit Card Update (Score: 34.8)

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 1,560 | 88th |
| Copybooks | 15 | 82nd |
| IF+EVALUATE | 90 | 88th |
| CICS I/O | 9 | 65th |
| Dependencies | 4 | 60th |

**Complexity Drivers:**
- Field-level validation for card data (expiry date, CVV, embossed name)
- Uses CSSETATY screen attribute macros (same pattern as COACTUPC)
- Cross-file validation (card → account lookup via XREF)

---

### 5. COCRDLIC.cbl — Credit Card List (Score: 33.5)

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 1,459 | 85th |
| Copybooks | 13 | 75th |
| IF+EVALUATE | 79 | 83rd |
| CICS I/O | 14 (STARTBR/READNEXT/READPREV/ENDBR) | 85th |
| Dependencies | 4 | 60th |

**Complexity Drivers:**
- Paginated VSAM browse pattern (forward/backward scrolling)
- Same browse pattern appears in 5+ programs — extract as reusable component
- Complex RESP/RESP2 error handling for browse boundary conditions

---

### 6. COPAUA0C.cbl — Authorization Decision / IMS+DB2+MQ (Score: 31.2)

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 1,026 | 75th |
| Copybooks | 16 | 85th |
| IF+EVALUATE | 61 | 75th |
| I/O (IMS+DB2+MQ) | 15 | 85th |
| Dependencies | 5 (spans 3 subsystems) | 80th |

**Complexity Drivers:**
- Spans IMS (DL/I calls), DB2 (fraud table), and MQ (request/reply) — most architecturally complex
- IMS position-dependent GU/GNP calls require careful relational mapping
- MQ correlation ID management for async request/reply
- Forms a chain with COPAUS0C/1C/2C — must migrate as a unit

---

### 7. COPAUS0C.cbl — Authorization Summary Browse / IMS (Score: 28.6)

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 1,032 | 76th |
| Copybooks | 14 | 80th |
| IF+EVALUATE | 47 | 65th |
| IMS DL/I | 8 | 60th |
| Dependencies | 3 | 45th |

**Complexity Drivers:**
- IMS hierarchical browse with segment-level positioning
- GU (Get Unique) + GNP (Get Next within Parent) patterns
- Part of authorization chain — cannot be modernized in isolation

---

### 8. CBSTM03A.CBL — Statement Generation (Score: 28.2)

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 924 | 70th |
| Copybooks | 4 | 30th |
| IF+EVALUATE | 24 | 35th |
| I/O Operations | **117** | **100th (highest)** |
| Dependencies | 3 (calls CBSTM03B) | 45th |

**Complexity Drivers:**
- Highest I/O count in the entire estate (117 file operations)
- Reads 4 VSAM files; writes text AND HTML output simultaneously
- Complex record gathering across XREF → Customer → Account → Transactions
- Self-contained batch (no CICS) — **best candidate for proof-of-concept migration**

---

### 9. COACTVWC.cbl — Account View (Score: 27.4)

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 941 | 71st |
| Copybooks | 15 | 82nd |
| IF+EVALUATE | 39 | 55th |
| CICS I/O | 8 | 55th |
| Dependencies | 5 (reads 3 files, called by hub) | 80th |

**Complexity Drivers:**
- Cross-file data assembly (account + card + customer joined for display)
- High copybook count due to multiple entity structures needed
- Template for all "view" screens — pattern extraction opportunity

---

### 10. COTRN02C.cbl — Transaction Add / Online (Score: 24.1)

| Metric | Value | Estate Percentile |
|--------|-------|-------------------|
| LOC | 783 | 60th |
| Copybooks | 10 | 65th |
| IF+EVALUATE | 40 | 57th |
| CICS I/O | 7 | 50th |
| Dependencies | 4 | 60th |

**Complexity Drivers:**
- Cross-file validation (card XREF must exist, account must be active)
- Generates unique TRAN-ID (system clock-based)
- Updates account balance in same unit of work

---

## Modernization Recommendations

### Priority 1 — Modernize First (Highest ROI)

| # | Program | Rationale | Recommended Approach |
|---|---------|-----------|---------------------|
| 1 | **CBSTM03A** (Statement Gen) | Self-contained batch, no CICS, highest I/O. Ideal proof-of-concept because success is easily measured (output file comparison). | Spring Batch job with ItemReader/ItemWriter; Thymeleaf for HTML output. 2–3 weeks. |
| 2 | **COTRTLIC + COTRTUPC** (Tran Types) | Already use DB2 SQL — natural entry point for database modernization. SQL translates with minimal changes. | JPA entities + Spring Data; CICS screens → REST API + React. 3–4 weeks. |

### Priority 2 — High Impact, Contained Scope

| # | Program | Rationale | Recommended Approach |
|---|---------|-----------|---------------------|
| 3 | **COCRDLIC + COCRDUPC** (Card CRUD) | Paginated browse pattern shared by 5+ programs. Solving pagination once creates reusable template for all list screens. | Spring Data paging; generic AbstractListController. 4–5 weeks. |
| 4 | **COACTUPC** (Account Update) | Highest complexity (score 74.8) but also highest business value. Must decompose before migrating. | Split into 4 components: AccountService, AccountValidationService, AccountController (REST), AccountView (React). Generate 10,000+ regression test cases first. 6–8 weeks. |

### Priority 3 — Cross-Subsystem (Higher Risk)

| # | Program | Rationale | Recommended Approach |
|---|---------|-----------|---------------------|
| 5 | **COPAUA0C + COPAUS0C/1C/2C** (Authorization) | Spans IMS + DB2 + MQ — most architecturally complex. Migrate as a unit. | Spring JMS for MQ; IMS hierarchy → 2 relational tables; migrate in Phase 4. 6–8 weeks. |

### Priority 4 — Modernize Last (Simple Batch)

| # | Program | Rationale | Recommended Approach |
|---|---------|-----------|---------------------|
| 6–10 | **CBTRN02C, CBACT04C, CBTRN03C, CBEXPORT, CBIMPORT** | Sequential file processing, no CICS, standard read-validate-write. Can run in parallel with modernized online programs during transition. | Spring Batch steps; 2–3 weeks each. |

---

## Risk Matrix

| Risk | Programs Affected | Mitigation |
|------|-------------------|-----------|
| Data corruption during dual-write | COACTUPC, CBTRN02C, CBACT04C | SQS FIFO queues for sync; hourly reconciliation |
| COBOL fixed-point → Java floating-point | All monetary fields (S9(10)V99) | **Mandatory BigDecimal** for ALL monetary fields |
| IMS positional semantics lost | COPAUA0C, COPAUS0C/1C/2C | Map IMS hierarchy to 2 relational tables before migration |
| Batch SLA regression | POSTTRAN→INTCALC→CREASTMT→TRANRPT pipeline | JdbcBatchItemWriter with batch size ≥ 1,000; target ≤ 110% of COBOL runtime |
| Validation logic inconsistency | COACTUPC (350+ area codes, state codes) | Extract CSLKPCDY into shared carddemo-validation library; unit test exhaustively |

---

## Estimated Timeline

| Phase | Scope | Duration | Team |
|-------|-------|----------|------|
| P0: Foundation | Shared libraries, test framework, CI/CD | 3–4 weeks | 4 devs |
| P1: Pilot | CBSTM03A + User Auth (COUSR*) | 4–5 weeks | 2 devs |
| P2: DB2 + Card | COTRTLIC/COTRTUPC + COCRDLIC/COCRDUPC | 5–7 weeks | 3 devs |
| P3: Account + Transaction | COACTUPC/COACTVWC + COTRN* + CBTRN02C | 8–10 weeks | 4 devs |
| P4: Authorization (IMS/MQ) | COPAUA0C chain | 6–8 weeks | 3 devs |
| P5: Remaining Batch | CBACT04C, CBTRN03C, CBEXPORT/IMPORT | 4–5 weeks | 2 devs |
| P6: Decommission | Cutover + COBOL retirement | 2–3 weeks | 2 devs |
| **Total** | | **32–42 weeks** | **4 FTEs avg** |

---

*Generated: 2026-06-19 | Source: infosys-training/uc-legacy-modernization-cobol-to-java*
