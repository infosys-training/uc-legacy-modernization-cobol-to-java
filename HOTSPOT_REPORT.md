# HOTSPOT REPORT — CardDemo Modernization Priority

## Methodology

Programs are ranked by a composite score across five dimensions:
1. **Lines of Code (LOC)** — larger programs are harder to modernize and test
2. **Copybook Count** — more shared structures = higher coupling
3. **I/O Operations** — file, DB2, IMS, and MQ access points
4. **Business Logic Density** — depth of EVALUATE/IF nesting (max nesting depth observed)
5. **Inter-Program Dependencies** — programs that depend on or are depended upon by others

Each dimension is scored 1–10 relative to the estate maximum. Final rank = sum of all scores.

---

## Top 10 Hotspot Programs

| Rank | Program | LOC | Copybooks | I/O Ops | Logic Depth | Dependencies | Composite Score | Classification |
|------|---------|-----|-----------|---------|-------------|-------------|----------------|----------------|
| 1 | **COACTUPC** | 4,236 | 18 | 6 (CICS R/W × 3 files) | 8 (nested IF/EVALUATE for validation) | 5 (XCTL from COMEN01C, uses CSSETATY×12) | **47** | CICS Online |
| 2 | **COTRTLIC** | 2,098 | 14 | 5 (DB2 cursor + CICS) | 7 (cursor logic, page navigation) | 4 (XCTL from COADM01C, DB2 tables) | **38** | CICS DB2 |
| 3 | **COTRTUPC** | 1,702 | 14 | 8 (DB2 SELECT/INSERT/UPDATE/DELETE) | 7 (CRUD + validation logic) | 5 (XCTL from COADM01C, 2 DB2 tables) | **36** | CICS DB2 |
| 4 | **COCRDUPC** | 1,560 | 13 | 4 (CICS READ/REWRITE) | 7 (field-level validation) | 3 (XCTL from COCRDLIC) | **34** | CICS Online |
| 5 | **COCRDLIC** | 1,459 | 11 | 4 (CICS BROWSE, AIX) | 6 (browse/page logic) | 3 (navigates to COCRDSLC/COCRDUPC) | **31** | CICS Online |
| 6 | **COPAUS0C** | 1,032 | 14 | 5 (IMS GNP, BMS, CICS) | 6 (IMS segment navigation) | 4 (XCTL to COPAUS1C, uses COMMAREA) | **30** | CICS IMS |
| 7 | **COPAUA0C** | 1,026 | 14 | 10 (MQ GET/PUT + IMS + VSAM ×3) | 7 (auth decision tree) | 6 (MQ + IMS + VSAM + XREF + ACCT) | **37** | CICS IMS MQ |
| 8 | **COACTVWC** | 941 | 13 | 4 (CICS READ × 3 files) | 5 (display formatting) | 3 (XCTL from COMEN01C) | **26** | CICS Online |
| 9 | **CBSTM03A** | 924 | 4 | 5 (multiple file I/O via CBSTM03B) | 5 (statement generation loops) | 3 (CALLs CBSTM03B ×13) | **25** | Batch |
| 10 | **COTRN02C** | 783 | 10 | 5 (CICS WRITE + READ × 3 files) | 6 (validation + posting) | 4 (XCTL from COMEN01C, calls CSUTLDTC) | **24** | CICS Online |

---

## Detailed Analysis

### #1 — COACTUPC (Account Update) — Score: 47

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 4,236 | Largest program in the estate by far (2× next largest) |
| Copybooks | 18 | Uses CSSETATY 12 times (via COPY REPLACING for each field) |
| I/O | 6 | ACCTDAT (READ/REWRITE), CARDXREF (READ), CUSTDAT (READ) |
| Logic | 8 | Deep nested IF for field validation (zip, state, phone, date), EVALUATE for PF keys |
| Deps | 5 | Called from COMEN01C; uses shared COMMAREA, date validation (CSUTLDPY/CSUTLDWY) |

**Why modernize first:** Contains the most complex business logic (account update validation), is the single largest maintenance burden, and represents the validation rule engine for account data. Breaking this into microservices (validation service, account service) would yield the highest ROI.

---

### #2 — COTRTLIC (Transaction Type List) — Score: 38

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 2,098 | Second largest CICS program |
| Copybooks | 14 | Includes DB2 DCLGENs and BMS maps |
| I/O | 5 | DB2 cursor (DECLARE/OPEN/FETCH/CLOSE), CICS BMS |
| Logic | 7 | Cursor-based pagination, dynamic WHERE clause building |
| Deps | 4 | Navigation hub for transaction type maintenance |

**Why modernize:** Demonstrates DB2 + CICS coupling; natural candidate for a REST API with paginated queries.

---

### #3 — COTRTUPC (Transaction Type Update) — Score: 36

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,702 | Full CRUD against DB2 |
| Copybooks | 14 | Two DCLGEN includes (TRNTYPE + TRNTYCAT) |
| I/O | 8 | SELECT, INSERT, UPDATE, DELETE across 2 tables |
| Logic | 7 | Validation + existence checks + referential integrity |
| Deps | 5 | Partners with COTRTLIC; shared DB2 tables |

**Why modernize:** Together with COTRTLIC, these form a self-contained DB2 CRUD domain that maps directly to a modern REST service.

---

### #4 — COCRDUPC (Card Update) — Score: 34

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,560 | Large validation-heavy program |
| Copybooks | 13 | Shares validation infrastructure with COACTUPC |
| I/O | 4 | CARDDAT (READ/REWRITE), CUSTDAT (READ) |
| Logic | 7 | Card number validation, expiry date checks, name matching |
| Deps | 3 | Part of card management flow |

**Why modernize:** Card operations are security-sensitive; modernizing enables encryption, tokenization, and PCI compliance improvements.

---

### #5 — COCRDLIC (Card List) — Score: 31

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,459 | Browse/pagination complexity |
| Copybooks | 11 | Uses CICS BROWSE with AIX path |
| I/O | 4 | CARDDAT + CXACAIX (alternate index browse) |
| Logic | 6 | Forward/backward paging via STARTBR/READNEXT/READPREV |
| Deps | 3 | Entry point to card management |

**Why modernize:** Alternate Index browsing is a mainframe-specific pattern that has no direct equivalent; requires redesign as indexed query.

---

### #6 — COPAUS0C (Authorization Summary) — Score: 30

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,032 | IMS segment browsing |
| Copybooks | 14 | IMS PCB masks + CICS + BMS |
| I/O | 5 | IMS GNP (browse segments), CICS BMS, COMMAREA |
| Logic | 6 | IMS navigation state management |
| Deps | 4 | Links to COPAUS1C (detail), COPAUS2C (fraud) |

**Why modernize:** IMS is the hardest technology to migrate; this is the gateway to the authorization subsystem.

---

### #7 — COPAUA0C (Authorization Engine) — Score: 37

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 1,026 | Multi-technology integration point |
| Copybooks | 14 | MQ + IMS + VSAM structures |
| I/O | 10 | MQ (GET+PUT1), IMS (SCHD+GU+ISRT+TERM), VSAM (×3 files) |
| Logic | 7 | Authorization decision tree (limits, fraud rules, status) |
| Deps | 6 | Touches MQ, IMS, and 3 VSAM files simultaneously |

**Why modernize:** Highest technology diversity in a single program (MQ + IMS + CICS + VSAM). This is the real-time authorization engine — critical path for card transactions. Modernizing unlocks event-driven architecture.

---

### #8 — COACTVWC (Account View) — Score: 26

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 941 | Read-only but complex display logic |
| Copybooks | 13 | Multiple record types for composite display |
| I/O | 4 | ACCTDAT + CARDXREF + CUSTDAT (READ) |
| Logic | 5 | Multi-file join and formatting |
| Deps | 3 | Simple navigation (view only) |

**Why modernize:** Read-only patterns are safe to extract first; natural fit for a query/read-model service.

---

### #9 — CBSTM03A (Statement Generation) — Score: 25

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 924 | Batch report driver |
| Copybooks | 4 | Lean but complex control flow |
| I/O | 5 | Calls CBSTM03B 13× per run for file access |
| Logic | 5 | Statement composition, page breaks, totals |
| Deps | 3 | CALL CBSTM03B (tight coupling to subroutine) |

**Why modernize:** Statement generation is a document-output pattern well-suited to modern template engines.

---

### #10 — COTRN02C (Add Transaction — Online) — Score: 24

| Metric | Value | Notes |
|--------|-------|-------|
| LOC | 783 | Transaction entry with validation |
| Copybooks | 10 | Transaction + account structures |
| I/O | 5 | TRANSACT (WRITE), ACCTDAT (READ), CARDXREF (READ) |
| Logic | 6 | Amount validation, balance checks, timestamp generation |
| Deps | 4 | Online entry point for new transactions |

**Why modernize:** Represents the write path for new transactions — modernizing enables real-time event streaming.

---

## Modernization Recommendations

### Priority Tier 1 — Highest ROI (Modernize First)

| Program | Rationale | Suggested Target Architecture |
|---------|-----------|------------------------------|
| **COPAUA0C** | Real-time authorization engine; MQ+IMS+VSAM = highest complexity. Critical business path. | Event-driven microservice (Kafka/SQS → Decision Engine → Response) |
| **COACTUPC** | Largest codebase (4,236 LOC), most validation logic. Maintenance nightmare. | Account Service REST API + Validation Rules Engine |
| **COTRTLIC + COTRTUPC** | Self-contained DB2 CRUD pair. Clean domain boundary. Low risk. | Transaction Type REST API with Spring Boot/JPA |

### Priority Tier 2 — High Value

| Program | Rationale | Suggested Target Architecture |
|---------|-----------|------------------------------|
| **COCRDUPC + COCRDLIC** | Card management with security implications. PCI compliance benefit. | Card Service (tokenized, encrypted) |
| **COPAUS0C/1C/2C** | IMS subsystem — hardest to maintain, specialized skills needed. | Authorization History Service (PostgreSQL + API) |

### Priority Tier 3 — Quick Wins

| Program | Rationale | Suggested Target Architecture |
|---------|-----------|------------------------------|
| **CBSTM03A + CBSTM03B** | Statement generation — isolated batch function, clear inputs/outputs. | Template-based document generation (PDF/HTML) |
| **CBEXPORT + CBIMPORT** | Branch migration — clear ETL pattern, self-contained. | AWS Glue / Spring Batch ETL pipeline |
| **CSUTLDTC** | Shared date utility — extract as library. | Java DateTimeFormatter utility class |

### Key Factors for Sequencing

1. **IMS programs (COPAUA0C, COPAUS*) should be prioritized** because IMS skills are the rarest and IMS licensing is the most expensive.
2. **DB2 programs (COTRTLIC, COTRTUPC, COBTUPDT) offer the cleanest migration path** since DB2 SQL maps directly to modern RDBMS.
3. **COACTUPC should be decomposed** rather than migrated 1:1 — its 4,236 lines contain at least 3 distinct concerns (validation, account update, navigation).
4. **Batch programs (CBACT04C interest calc, CBTRN02C posting) have well-defined boundaries** (input file → processing → output file) making them ideal for Spring Batch.
5. **Shared copybooks (COCOM01Y, CVCRD01Y) indicate cross-cutting concerns** that should become shared libraries or API contracts in the target architecture.
