# HOTSPOT REPORT — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System  
> **Repo:** `infosys-training/uc-legacy-modernization-cobol-to-java`  
> **Generated:** 2026-06-16

---

## Methodology

Programs are ranked by a **composite hotspot score** (0–100) computed from five normalized metrics:

| Metric | Weight | Measurement |
|--------|--------|-------------|
| Lines of Code (LOC) | 25% | `wc -l` |
| Copybooks Referenced | 20% | `grep -c "COPY "` |
| I/O Operations | 20% | Count of READ/WRITE/REWRITE/DELETE/START/STARTBR/READNEXT/READPREV/ENDBR/EXEC SQL/OPEN/CLOSE |
| Business Logic Density | 25% | Count of IF/EVALUATE statements (proxy for branching complexity) |
| Inter-Program Dependencies | 10% | Outbound XCTL/LINK/CALL + inbound callers + subsystem count |

Each metric is normalized as `(value - min) / (max - min) × 100`, then weighted.

---

## Top 10 Hotspot Programs

| Rank | Program | LOC | COPYs | I/O Ops | IF/EVAL | Deps | Score | Classification |
|------|---------|-----|-------|---------|---------|------|-------|----------------|
| 1 | **COACTUPC.cbl** | 4,236 | 56 | 7 | 174 | 6 (hub target, 3 VSAM) | **82.3** | Online CICS |
| 2 | **COTRTLIC.cbl** | 2,098 | 11 | 28 | 102 | 3 (DB2 cursor) | **55.9** | Online CICS+DB2 |
| 3 | **COTRTUPC.cbl** | 1,702 | 13 | 28 | 62 | 3 (DB2 cascade) | **49.2** | Online CICS+DB2 |
| 4 | **COCRDUPC.cbl** | 1,560 | 15 | 3 | 80 | 4 (2 VSAM browse) | **46.8** | Online CICS |
| 5 | **COCRDLIC.cbl** | 1,459 | 13 | 12 | 68 | 3 (paginated browse) | **43.5** | Online CICS |
| 6 | **COPAUS0C.cbl** | 1,032 | 14 | 3 | 36 | 5 (IMS+LINK chain) | **35.2** | Online CICS+IMS |
| 7 | **COPAUA0C.cbl** | 1,026 | 16 | 15 | 31 | 7 (IMS+MQ+CICS+DB2) | **38.4** | Online CICS+IMS+MQ+DB2 |
| 8 | **CBSTM03A.CBL** | 924 | 4 | 118 | 20 | 2 (calls CBSTM03B) | **36.1** | Batch |
| 9 | **COACTVWC.cbl** | 941 | 15 | 3 | 33 | 3 (3 VSAM reads) | **31.4** | Online CICS |
| 10 | **CBTRN02C.cbl** | 731 | 5 | 24 | 48 | 4 (4 files written) | **30.7** | Batch |

---

## Detailed Hotspot Analysis

### #1 — COACTUPC.cbl (Account Update) — Score: 82.3

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 4,236 | **100th** (largest in estate) |
| COPY statements | 56 | **100th** (most copybooks) |
| I/O operations | 7 | 15th |
| IF/EVALUATE | 174 | **100th** (most branching) |
| Subsystems | CICS, 3 VSAM files | — |

**Why it's #1:** This is the most complex program by every structural metric. It contains:
- Exhaustive field-level validation: date (via CSUTLDTC), SSN, phone (NANPA), state code, ZIP prefix — all using the 1,318-line CSLKPCDY lookup copybook
- 3× `COPY CSSETATY REPLACING` for BMS attribute macros (screen field formatting with different prefixes)
- Reads/writes ACCTFILE, CUSTFILE, XREFFILE
- Deeply nested `EVALUATE TRUE` → `IF` → `EVALUATE` control flow for multi-field update logic

**Risk factors:**
- Single most complex module; failure here drops confidence in migration
- Business logic tightly coupled with BMS screen I/O
- COPY REPLACING has no direct Java equivalent

---

### #2 — COTRTLIC.cbl (Transaction Type List — DB2) — Score: 55.9

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 2,098 | 95th |
| COPY statements | 11 | 53rd |
| I/O operations | 28 | **100th** (tied highest) |
| IF/EVALUATE | 102 | 96th |
| Subsystems | CICS, DB2 | — |

**Why it's #2:** Cursor-based paginated browse (DECLARE/OPEN/FETCH/CLOSE) with forward and backward navigation over DB2 TRANSACTION_TYPE table. 28 I/O operations (tied highest with COTRTUPC) due to repeated cursor operations. Complex screen state management for pagination.

---

### #3 — COTRTUPC.cbl (Transaction Type Update — DB2) — Score: 49.2

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,702 | 90th |
| COPY statements | 13 | 60th |
| I/O operations | 28 | **100th** (tied highest) |
| IF/EVALUATE | 62 | 85th |
| Subsystems | CICS, DB2 | — |

**Why it's #3:** Full CRUD (INSERT/UPDATE/DELETE) on TRANSACTION_TYPE and TRANSACTION_CATEGORY with cascading deletes. When a type is deleted, all child categories must be removed first. DSNTIAC error formatting adds complexity.

---

### #4 — COCRDUPC.cbl (Card Update) — Score: 46.8

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,560 | 87th |
| COPY statements | 15 | 70th |
| I/O operations | 3 | 7th |
| IF/EVALUATE | 80 | 92nd |
| Subsystems | CICS, 2 VSAM | — |

**Why it's #4:** High branching density (80 IF/EVALUATE) relative to its LOC. Contains card-level validation, date validation (via CSUTLDPY COPY'd procedures), and multi-step VSAM browse (STARTBR/READNEXT/READPREV/ENDBR). Screen state management for card detail editing.

---

### #5 — COCRDLIC.cbl (Card List — Paginated Browse) — Score: 43.5

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,459 | 85th |
| COPY statements | 13 | 60th |
| I/O operations | 12 | 38th |
| IF/EVALUATE | 68 | 88th |
| Subsystems | CICS, VSAM browse | — |

**Why it's #5:** Implements the paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) that appears in 5+ programs across the estate. Converting this pattern to Spring Data pagination would create a reusable `AbstractListController` template.

---

### #6 — COPAUS0C.cbl (Auth Summary — IMS Browse) — Score: 35.2

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,032 | 72nd |
| COPY statements | 14 | 65th |
| I/O operations | 3 | 7th |
| IF/EVALUATE | 36 | 67th |
| Subsystems | CICS, IMS DL/I | — |

**Why it's #6:** IMS DL/I calls (GU, GNP) for hierarchical data browse. Part of the COPAUS0C → COPAUS1C → COPAUS2C chain that must be migrated as a unit. IMS hierarchical-to-relational mapping is required.

---

### #7 — COPAUA0C.cbl (Authorization Decision — IMS+MQ+CICS+DB2) — Score: 38.4

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,026 | 71st |
| COPY statements | 16 | 73rd |
| I/O operations | 15 | 45th |
| IF/EVALUATE | 31 | 62nd |
| Subsystems | **CICS + IMS + MQ + DB2** (4 subsystems) | — |

**Why it's #7:** Most architecturally complex program despite moderate LOC. Spans all four subsystems:
1. MQ — MQGET request, MQPUT1 response
2. IMS — DL/I lookup of authorization history
3. CICS — Transaction context and XREF/ACCT/CUST file reads
4. DB2 — Fraud reference table lookup

This is the key integration point for real-time authorization decisions.

---

### #8 — CBSTM03A.CBL (Statement Generation) — Score: 36.1

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 924 | 68th |
| COPY statements | 4 | 20th |
| I/O operations | 118 | **100th** (absolute highest) |
| IF/EVALUATE | 20 | 46th |
| Subsystems | Batch (pure sequential) | — |

**Why it's #8:** Highest I/O count in the entire estate (118 operations) due to extensive WRITE statements for statement formatting (text + HTML output). Self-contained batch with no CICS dependency — **ideal pilot candidate** for proving the migration approach.

---

### #9 — COACTVWC.cbl (Account View) — Score: 31.4

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 941 | 69th |
| COPY statements | 15 | 70th |
| I/O operations | 3 | 7th |
| IF/EVALUATE | 33 | 64th |
| Subsystems | CICS, 3 VSAM | — |

**Why it's #9:** Read-only account detail screen touching 3 VSAM files (ACCTFILE, CUSTFILE, XREFFILE). Lower risk than update programs. Shares most copybooks with COACTUPC — migrating this first provides a simpler read-only validation before tackling the complex update logic.

---

### #10 — CBTRN02C.cbl (Post Daily Transactions) — Score: 30.7

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 731 | 58th |
| COPY statements | 5 | 25th |
| I/O operations | 24 | 78th |
| IF/EVALUATE | 48 | 78th |
| Subsystems | Batch (4 output files) | — |

**Why it's #10:** Core batch processing: reads DALYTRAN, validates via XREFFILE, posts to TRANSACT, updates ACCTFILE balances, writes TCATBALF category balances, and captures rejects to DALYREJS. This is Step 1 of the daily batch pipeline — all downstream jobs depend on its correct execution.

---

## Modernization Priority Recommendations

### Phase 1: Prove the Approach (Weeks 1–5)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **P0** | CBSTM03A (#8) | Highest I/O but self-contained batch. No CICS, no IMS, no MQ. Pure read → format → write. Ideal pilot to validate Spring Batch approach and establish migration patterns. |
| **P0** | User programs (COUSR00C-03C, COSGN00C) | Simple CICS CRUD on single VSAM file. Low complexity, but validates CICS→REST migration pattern and establishes Spring Security foundation. |

### Phase 2: Database Modernization (Weeks 5–12)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **P1** | COTRTLIC (#2) + COTRTUPC (#3) | Already use DB2 SQL — closest to modern patterns. Cursor pagination → Spring Data paging. Validates DB2→JPA migration. |
| **P1** | COBTUPDT | Batch DB2 maintenance — simple INSERT/UPDATE/DELETE. |

### Phase 3: Core Business Logic (Weeks 12–22)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **P2** | COCRDLIC (#5) + COCRDUPC (#4) | Paginated browse pattern used by 5+ programs. Creating `AbstractListController` here yields reuse across COTRN00C, COUSR00C. |
| **P2** | COACTVWC (#9) → COACTUPC (#1) | Migrate view first (simpler, read-only), then tackle the largest program with proven patterns. Break COACTUPC into AccountService + AccountValidationService + AccountController. |
| **P2** | CBTRN02C (#10) + CBACT04C | Core batch pipeline. CBTRN02C is Step 1 of daily cycle; CBACT04C is Step 2 (interest calc). Both must be migrated together to maintain pipeline integrity. |

### Phase 4: Complex Integration (Weeks 22–30)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **P3** | COPAUA0C (#7) + COPAUS0C (#6) chain | Most architecturally complex: 4 subsystems. Requires IMS→PostgreSQL mapping, MQ→SQS adapter, and combined CICS context. Migrate COPAUS0C/1C/2C as a unit. |

### Phase 5: Remaining Batch + Decommission (Weeks 30–36)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **P4** | CBEXPORT, CBIMPORT | Data migration tools. Replatform to Java, then retire once VSAM is decommissioned. |
| **P4** | Remaining batch (CBACT01C-03C, CBCUS01C, CBTRN01C/03C) | Simple sequential file processing. Spring Batch ItemReader/Writer with minimal business logic. |

---

## Estate-Wide Complexity Distribution

```
LOC Distribution:
  > 2,000 LOC:  2 programs (COACTUPC, COTRTLIC)         ████
  1,000–2,000:  5 programs                               ██████████
  500–999:     13 programs                               ██████████████████████████
  < 500:       19 programs                               ██████████████████████████████████████

Branching (IF/EVALUATE) Distribution:
  > 100:        2 programs (COACTUPC: 174, COTRTLIC: 102) ████
  50–100:       4 programs                                ████████
  20–49:       14 programs                                ████████████████████████████
  < 20:        19 programs                                ██████████████████████████████████████

I/O Operations Distribution:
  > 50:         1 program (CBSTM03A: 118)                 ██
  20–50:        8 programs                                ████████████████
  10–19:        9 programs                                ██████████████████
  < 10:        21 programs                                ██████████████████████████████████████████
```

---

## Key Findings

1. **COACTUPC is the clear #1 hotspot** — largest by LOC (4,236), most copybooks (56), most branching (174 IF/EVALUATE). It should be decomposed into 3–4 Java services, not migrated as a monolithic class.

2. **DB2 programs (COTRTLIC, COTRTUPC) are the natural starting point** for database modernization — they already use SQL, making the translation to JPA/Spring Data straightforward.

3. **CBSTM03A has the highest I/O** (118 ops) but the simplest architecture — pure sequential batch. Ideal pilot to prove the Spring Batch approach before tackling complex CICS programs.

4. **COPAUA0C is the architectural risk** — 4 subsystems (CICS+IMS+MQ+DB2) in one program. Defer to Phase 4 and migrate the entire auth chain (COPAUS0C→1C→2C) as a unit.

5. **The paginated browse pattern** (STARTBR/READNEXT/READPREV/ENDBR) in COCRDLIC, COTRN00C, COUSR00C should be abstracted into a reusable Java controller template early in the migration.

6. **78% of programs (30/39) are under 1,000 LOC** — the long tail is manageable. Focus migration effort on the top 10 hotspots which represent 63% of total LOC.
