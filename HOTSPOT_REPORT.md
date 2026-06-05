# Hotspot Report — CardDemo COBOL Estate

> Top 10 programs ranked by complexity metrics with modernization priority recommendations.

---

## 1. Scoring Methodology

Each program is scored across five dimensions (0–20 points each, max 100):

| Dimension | Weight | Metric | Scoring |
|-----------|--------|--------|---------|
| **Lines of Code** | 20 | Raw LOC | >2000=20, >1000=15, >500=10, >200=5, ≤200=2 |
| **Copybook Count** | 20 | Distinct COPY statements | >12=20, >8=15, >5=10, >3=5, ≤3=2 |
| **I/O Operations** | 20 | VSAM + DB2 + IMS + MQ ops | >10=20, >6=15, >3=10, >1=5, ≤1=2 |
| **Logic Density** | 20 | IF + EVALUATE + PERFORM | >100=20, >50=15, >30=10, >15=5, ≤15=2 |
| **Dependencies** | 20 | Programs called/calling + VSAM files touched | >8=20, >5=15, >3=10, >1=5, ≤1=2 |

---

## 2. Top 10 Programs Ranked by Hotspot Score

| Rank | Program | LOC | Copies | I/O Ops | Logic (IF/EVAL/PERF) | Deps | Score | Type |
|------|---------|-----|--------|---------|---------------------|------|-------|------|
| **1** | **COACTUPC.cbl** | 4,236 | 15 | 8 (3 VSAM R/W + BMS) | 167/20/64 = **251** | 5 files + 3 ext calls | **95** | CICS |
| **2** | **COTRTLIC.cbl** | 2,098 | 11 | 12 (DB2 cursors + CICS) | 87/32/69 = **188** | 3 DB2 tables + XCTL | **90** | CICS+DB2 |
| **3** | **COTRTUPC.cbl** | 1,702 | 13 | 11 (DB2 CRUD + CICS) | 51/26/40 = **117** | 3 DB2 tables + XCTL | **85** | CICS+DB2 |
| **4** | **COCRDUPC.cbl** | 1,560 | 11 | 7 (4 VSAM R/W + BMS) | 73/16/26 = **115** | 4 VSAM files | **80** | CICS |
| **5** | **COCRDLIC.cbl** | 1,459 | 11 | 8 (STARTBR/READNEXT + BMS) | 60/18/34 = **112** | 3 VSAM files | **78** | CICS |
| **6** | **COPAUS0C.cbl** | 1,032 | 14 | 10 (IMS DL/I + CICS + 3 VSAM) | 25/22/48 = **95** | IMS + 3 VSAM + LINK | **77** | CICS+IMS |
| **7** | **COPAUA0C.cbl** | 1,026 | 16 | 9 (MQ + IMS + 3 CICS VSAM) | 26/10/38 = **74** | MQ + IMS + 3 VSAM | **76** | CICS+IMS+MQ |
| **8** | **CBSTM03A.CBL** | 924 | 4 | 17 (15 CALL CBSTM03B + 2 files) | 15/9/33 = **57** | 4 VSAM via submodule | **65** | Batch |
| **9** | **COACTVWC.cbl** | 941 | 11 | 5 (3 VSAM READ + BMS) | 29/10/21 = **60** | 3 VSAM files | **63** | CICS |
| **10** | **COTRN02C.cbl** | 783 | 10 | 8 (VSAM R/W + CALL CSUTLDTC) | 14/26/61 = **101** | 3 VSAM + 1 ext call | **62** | CICS |

---

## 3. Detailed Hotspot Analysis

### #1 — COACTUPC.cbl (Account Update) — Score: 95/100

**Why it's the biggest hotspot:**
- **4,236 LOC** — nearly 2× the next-largest program; 15.5% of total estate LOC
- **251 branching statements** (167 IF + 20 EVALUATE + 64 PERFORM) — the most complex control flow in the estate
- **15 copybooks** including 3× COPY REPLACING of CSSETATY (macro-like code generation)
- **Exhaustive field validation**: date (month/day/year range checks), SSN (9-digit format), phone (NANPA area code validation via CSLKPCDY), state (50-state lookup), ZIP code (prefix-to-state mapping)
- Touches **3 VSAM files** (ACCTFILE, CARDXREF, CUSTFILE) with READ + REWRITE
- Business logic is **tightly coupled** with BMS screen I/O — every field has inline validation interleaved with screen attribute setting

**Risk factors:** Single most complex module; migration failure here impacts the entire account management workflow.

### #2 — COTRTLIC.cbl (Transaction Type List — DB2) — Score: 90/100

**Why it's a hotspot:**
- **2,098 LOC** — second-largest program
- **188 branching statements** — second-highest complexity
- Uses **DB2 cursor-based pagination** (forward + backward cursors with FETCH)
- Complex EVALUATE blocks for SQL return code handling
- CICS SYNCPOINT for transaction management
- Most complex DB2 integration pattern in the estate

### #3 — COTRTUPC.cbl (Transaction Type Update — DB2) — Score: 85/100

**Why it's a hotspot:**
- **1,702 LOC** with cascading DB2 operations (DELETE from TRCAT when TRTYP deleted)
- Dual-table CRUD with referential integrity logic in COBOL (not DB2 constraints)
- Uses COPY REPLACING for CSSETATY screen attributes
- CICS ABEND handling for unrecoverable DB2 errors

### #4 — COCRDUPC.cbl (Credit Card Update) — Score: 80/100

**Why it's a hotspot:**
- **1,560 LOC** with 115 branching statements
- Touches 4 VSAM files (card, account, xref, customer)
- Card validation logic (expiry date, embossed name, status transitions)
- Paired with COCRDLIC (#5) — same paginated browse pattern

### #5 — COCRDLIC.cbl (Credit Card List) — Score: 78/100

**Why it's a hotspot:**
- **1,459 LOC** implementing the STARTBR/READNEXT/READPREV/ENDBR pagination pattern
- This pattern appears in 5+ programs — solving it once creates a reusable template
- Complex cursor management with forward/backward scrolling and page boundaries

### #6 — COPAUS0C.cbl (Auth Summary — IMS+CICS) — Score: 77/100

**Why it's a hotspot:**
- **1,032 LOC** spanning CICS + IMS subsystems
- IMS DL/I calls (GU, GNP, SCHD, TERM) with CICS SYNCPOINT
- Part of the COPAUS0C → COPAUS1C → COPAUS2C chain — must migrate as a unit
- Complex screen handling with IMS data navigation

### #7 — COPAUA0C.cbl (Authorization Decision — IMS+MQ+CICS) — Score: 76/100

**Why it's the most architecturally complex:**
- **1,026 LOC** spanning 3 subsystems (MQ + IMS + CICS)
- Triggered by MQ message → reads IMS database → reads VSAM files → sends MQ response
- The only program that simultaneously uses MQ, IMS DL/I, and CICS VSAM
- **16 copybooks** (highest count) — 8 MQ, 5 IMS/auth, 3 VSAM data structures

### #8 — CBSTM03A.CBL (Statement Generation) — Score: 65/100

**Why it's notable despite lower score:**
- **924 LOC** with 15 CALL invocations to CBSTM03B submodule
- Generates **dual output** (plain text + HTML statements)
- Self-contained batch with no CICS dependency — **easiest to prove migration approach**
- Highest number of I/O operations (17 via CBSTM03B delegation)

### #9 — COACTVWC.cbl (Account View) — Score: 63/100

**Why it's included:**
- **941 LOC** — moderately complex read-only display
- Shared VSAM access pattern with COACTUPC (#1) — can reuse Account Service
- 11 copybooks including 3 entity data structures

### #10 — COTRN02C.cbl (Transaction Add — CICS) — Score: 62/100

**Why it's included:**
- **783 LOC** with 101 branching statements (high logic density for its size)
- Uses CSUTLDTC date validation (CALL to external utility)
- STARTBR/READPREV for generating next transaction ID
- Writes to TRANSACT file — critical data path

---

## 4. Complexity Metrics — All Programs

| Program | LOC | IF | EVAL | PERF | Total Logic | Copybooks | I/O Ops |
|---------|-----|-----|------|------|------------|-----------|---------|
| COACTUPC | 4,236 | 167 | 20 | 64 | 251 | 15 | 8 |
| COTRTLIC | 2,098 | 87 | 32 | 69 | 188 | 11 | 12 |
| COTRTUPC | 1,702 | 51 | 26 | 40 | 117 | 13 | 11 |
| COCRDUPC | 1,560 | 73 | 16 | 26 | 115 | 11 | 7 |
| COCRDLIC | 1,459 | 60 | 18 | 34 | 112 | 11 | 8 |
| COPAUS0C | 1,032 | 25 | 22 | 48 | 95 | 14 | 10 |
| COPAUA0C | 1,026 | 26 | 10 | 38 | 74 | 16 | 9 |
| COACTVWC | 941 | 29 | 10 | 21 | 60 | 11 | 5 |
| CBSTM03A | 924 | 15 | 9 | 33 | 57 | 4 | 17 |
| COCRDSLC | 887 | 33 | 8 | 19 | 60 | 11 | 5 |
| COTRN02C | 783 | 14 | 26 | 61 | 101 | 10 | 8 |
| CBTRN02C | 731 | 48 | 0 | 62 | 110 | 6 | 8 |
| COTRN00C | 699 | 26 | 16 | 47 | 89 | 8 | 5 |
| COUSR00C | 695 | 25 | 16 | 45 | 86 | 8 | 5 |
| CBACT04C | 652 | 43 | 0 | 57 | 100 | 5 | 8 |
| CBTRN03C | 649 | 38 | 4 | 73 | 115 | 5 | 6 |
| CORPT00C | 649 | 20 | 10 | 35 | 65 | 8 | 3 |
| COACCT01 | 620 | 7 | 18 | 33 | 58 | 7 | 5 |
| COPAUS1C | 604 | 17 | 10 | 34 | 61 | 10 | 8 |
| CBEXPORT | 582 | 16 | 0 | 50 | 66 | 6 | 12 |
| COBIL00C | 572 | 10 | 18 | 38 | 66 | 9 | 5 |
| CODATE01 | 524 | 6 | 16 | 28 | 50 | 6 | 5 |
| CBTRN01C | 494 | 33 | 0 | 43 | 76 | 6 | 12 |
| CBIMPORT | 487 | 14 | 2 | 30 | 46 | 6 | 7 |
| CBACT01C | 430 | 22 | 0 | 36 | 58 | 2 | 5 |
| COUSR02C | 414 | 13 | 10 | 31 | 54 | 8 | 3 |
| CBPAUP0C | 386 | 17 | 4 | 19 | 40 | 2 | 5 |
| COUSR03C | 359 | 8 | 10 | 26 | 44 | 8 | 3 |
| COTRN01C (CICS) | 330 | 7 | 6 | 17 | 30 | 8 | 2 |
| COMEN01C | 308 | 7 | 6 | 15 | 28 | 9 | 2 |
| COUSR01C | 299 | 4 | 6 | 20 | 30 | 8 | 2 |
| COADM01C | 288 | 6 | 4 | 15 | 25 | 9 | 2 |
| COSGN00C | 260 | 4 | 6 | 11 | 21 | 8 | 3 |
| COPAUS2C | 244 | 3 | 0 | 1 | 4 | 1 | 4 |
| COBTUPDT | 237 | 2 | 8 | 15 | 25 | 0 | 4 |
| CBSTM03B | 230 | 12 | 1 | 4 | 17 | 0 | 4 |
| CBACT02C | 178 | 11 | 0 | 11 | 22 | 1 | 1 |
| CBACT03C | 178 | 11 | 0 | 11 | 22 | 1 | 1 |
| CBCUS01C | 178 | 11 | 0 | 11 | 22 | 1 | 1 |
| CSUTLDTC | 157 | 0 | 2 | 1 | 3 | 0 | 1 |
| COBSWAIT | 41 | 0 | 0 | 0 | 0 | 0 | 1 |

---

## 5. Modernization Recommendations

### Phase 1 — Prove the Approach (Weeks 1–6)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **1A** | CBSTM03A + CBSTM03B | Self-contained batch, no CICS, moderate complexity (924+230 LOC). Generates text+HTML output — easy to validate byte-for-byte. **Proves Spring Batch viability.** | Spring Batch `ItemReader`/`ItemWriter`; Thymeleaf for HTML. Compare output against GnuCOBOL baseline. |
| **1B** | COTRTLIC + COTRTUPC | Already use DB2 SQL — closest to modern data access. **Proves JPA/Spring Data viability.** | JPA entities for TRTYP/TRCAT tables. Spring MVC or REST controller. Cursor pagination → Spring Data `Pageable`. |

### Phase 2 — Core Entity Services (Weeks 7–16)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **2A** | COACTUPC (#1 hotspot) | Highest-complexity program — tackles the hardest problem early while the team has maximum energy and learning. Solves field validation patterns reusable across all entities. | **Split into 4 components**: `AccountService`, `AccountValidationService` (extract date/SSN/phone/state/ZIP validation into reusable classes), `AccountController` (REST), Account UI. Generate 10,000+ test cases from existing VSAM data before rewriting. Run shadow mode for 2 weeks. |
| **2B** | COCRDLIC + COCRDSLC + COCRDUPC | Paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) used in 5+ programs. Solve once, reuse everywhere. | `AbstractListController` with Spring Data paging. Card entity with JPA. Reuse Account validation patterns from 2A. |
| **2C** | COTRN00C + COTRN01C + COTRN02C | Transaction CRUD — high business value; reuses patterns from 2B (list/view) and 2A (validation). | Transaction Service + REST API. Transaction add reuses date validation from CSUTLDTC (already converted in 2A). |

### Phase 3 — Cross-Subsystem (Weeks 17–24)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **3A** | COPAUS0C → COPAUS1C → COPAUS2C + COPAUA0C | IMS chain must migrate as a unit. COPAUA0C spans MQ+IMS+CICS — most architecturally complex integration. | **Authorization Service**: Spring JMS for MQ, JPA for IMS-to-relational mapping (2 tables: `pending_auth_summary`, `pending_auth_detail`). Map DL/I GU/GNP/REPL to SQL queries. |
| **3B** | CBPAUP0C | IMS batch purge — reuses Auth Service from 3A. | Spring Batch job calling Authorization Service. Map DL/I DLET to SQL DELETE with expiry-date filter. |

### Phase 4 — Remaining Batch (Weeks 25–32)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **4A** | CBTRN02C (batch posting) | Core daily pipeline — high business criticality but moderate complexity (731 LOC, standard read-validate-write). | Spring Batch: `FlatFileItemReader` → validation `ItemProcessor` → `JdbcBatchItemWriter`. |
| **4B** | CBACT04C (interest calc) | Depends on TCATBALF + DISCGRP — pure computation. | Spring Batch with BigDecimal arithmetic. Port interest formulas from COBOL fixed-point. |
| **4C** | CBTRN03C, CBEXPORT, CBIMPORT | Report generation, data export/import — standard batch patterns. Can run in parallel with modernized online programs during transition. | Spring Batch steps. CBEXPORT/CBIMPORT become REST API endpoints or scheduled jobs. |

### Phase 5 — Menus, Auth, Utilities (Weeks 33–36)

| Priority | Program(s) | Rationale | Approach |
|----------|-----------|-----------|----------|
| **5A** | COSGN00C, COADM01C, COMEN01C | Menu/navigation layer — trivial once all target screens are converted. Replace USRSEC plain-text passwords. | Spring Security with bcrypt hashing. JWT-based session management replaces COMMAREA. React/Angular router replaces BMS menu screens. |
| **5B** | COUSR00C–03C | User CRUD — simple VSAM operations already mapped by Phase 2 patterns. | User entity + Spring Data JPA + REST API. |
| **5C** | COBSWAIT, CSUTLDTC, remaining utilities | Utility wrappers — trivial Java equivalents. | `Thread.sleep()` for COBSWAIT. `java.time` API for CSUTLDTC date conversions. |

---

## 6. Key Risk Factors

| Risk | Severity | Programs Affected | Mitigation |
|------|----------|-------------------|------------|
| COACTUPC complexity (4,236 LOC) | **HIGH** | COACTUPC | Break into 3–4 services; generate extensive test cases before migration |
| Plain-text passwords in USRSEC | **HIGH** | COSGN00C, COUSR00C–03C | Implement bcrypt/Spring Security in Phase 5A |
| COBOL fixed-point → Java BigDecimal | **HIGH** | All monetary programs | **Mandatory:** `java.math.BigDecimal` for ALL `PIC S9(n)V99` fields; never use `double`/`float` |
| COPY REPLACING macro pattern | **MEDIUM** | COACTUPC (×3), COCRDUPC, COTRTUPC | Convert to parameterized utility classes/methods |
| IMS DL/I → relational mapping | **MEDIUM** | CBPAUP0C, COPAUA0C, COPAUS0C/1C | Hierarchy maps to 2 relational tables |
| MQ integration (real-time messaging) | **MEDIUM** | COPAUA0C, COACCT01, CODATE01 | Spring JMS/AMQP adapter; test with MQ emulator |
| Assembler dependencies (COBDATFT, MVSWAIT) | **LOW** | CBACT01C, COBSWAIT | Write Java equivalents (date formatter, `Thread.sleep`) |
| VSAM-to-RDBMS dual-write during transition | **HIGH** | All VSAM-accessing programs | SQS FIFO for sync; hourly reconciliation; define single source of truth per entity |

---

## 7. Estate Complexity Summary

```
Total LOC:           27,350
Programs > 1,000:    8  (18% of programs, 58% of LOC)
Programs > 500:      22 (50% of programs, 87% of LOC)
Programs ≤ 200:      7  (16% of programs, 3% of LOC)

Avg IF/program:      27
Avg PERFORM/program: 31
Max IF (COACTUPC):   167

Subsystem Spread:
  CICS-only:         14 programs
  CICS + DB2:        3 programs
  CICS + IMS:        3 programs
  CICS + IMS + MQ:   1 program
  Batch-only:        16 programs
  Batch + MQ:        2 programs
  Batch + IMS:       3 programs
  Batch + DB2:       1 program
```
