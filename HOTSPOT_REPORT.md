# HOTSPOT REPORT — CardDemo COBOL Estate

## Methodology

Each program is scored across 5 dimensions (0–20 points each, total 0–100):

| Dimension | Weight | Metric | Scoring |
|-----------|--------|--------|---------|
| Lines of Code | 20 | Raw LOC | Normalized against max (4,236) |
| Copybook References | 20 | Distinct COPY statements | Normalized against max (18) |
| I/O Operations | 20 | File/DB/IMS/MQ operations (READ, WRITE, REWRITE, DELETE, STARTBR, READNEXT, READPREV, EXEC SQL, IMS calls, MQ calls) | Normalized against max |
| Business Logic Density | 20 | EVALUATE + IF statement count (proxy for branching complexity) | Normalized against max (184) |
| Inter-Program Dependencies | 20 | Programs that call or are called by this program + VSAM files touched | Normalized against max |

---

## Top 10 Programs by Hotspot Score

| Rank | Program | LOC | Copybooks | I/O Ops | Logic (EVAL+IF) | Dependencies | Score | Classification |
|------|---------|-----|-----------|---------|-----------------|-------------|-------|---------------|
| 1 | **COACTUPC.cbl** | 4,236 | 18 | 17 CICS R/W | 184 (20E+164I) | 6 files, 2 callers | **74.8** | Online (CICS) |
| 2 | **COTRTLIC.cbl** | 2,098 | 2* | 28 SQL ops | 38 (6E+32I) | 2 DB2 tables, 2 callers | **52.1** | Online (CICS/DB2) |
| 3 | **COTRTUPC.cbl** | 1,702 | 2* | 19 SQL ops | 34 (6E+28I) | 2 DB2 tables, 2 callers | **46.3** | Online (CICS/DB2) |
| 4 | **COCRDUPC.cbl** | 1,560 | 13 | 12 CICS R/W | 88 (16E+72I) | 4 files, 2 callers | **45.7** | Online (CICS) |
| 5 | **COCRDLIC.cbl** | 1,459 | 11 | 18 CICS browse | 34 (6E+28I) | 1 file, 3 callers | **41.2** | Online (CICS) |
| 6 | **COPAUA0C.cbl** | 1,026 | 14 | 12 CICS + 5 IMS + 4 MQ | 21 (4E+17I) | 3 files, 2 IMS seg, 2 MQ queues | **40.8** | Online (CICS/IMS/MQ) |
| 7 | **COPAUS0C.cbl** | 1,032 | 14 | 10 CICS + IMS browse | 28 (6E+22I) | 2 files, 2 IMS seg, 2 callers | **38.5** | Online (CICS/IMS) |
| 8 | **COBIL00C.cbl** | 1,026 | 8 | 11 CICS R/W/Browse | 28 (18E+10I) | 3 files, 1 caller | **35.4** | Online (CICS) |
| 9 | **CBSTM03A.CBL** | 924 | 4 | 6 FD files + CALL | 24 (9E+15I) | 4 files, 1 callee | **32.6** | Batch |
| 10 | **COACTVWC.cbl** | 941 | 15 | 15 CICS READ | 38 (10E+28I) | 4 files, 1 caller | **31.9** | Online (CICS) |

_*COTRTLIC/COTRTUPC copybooks are embedded via inline SQL INCLUDE, not standard COPY — only 2 explicit COPY for DB2 utilities_

---

## Detailed Analysis per Hotspot

### Rank 1: COACTUPC.cbl — Account Update (Score: 74.8)

**Why it's the #1 hotspot:**
- **Largest program in the estate** at 4,236 LOC — nearly 2× the next largest
- **18 copybooks** — the most of any program, including CSLKPCDY (2,300+ lines of validation lookup tables)
- **184 branching statements** (20 EVALUATE + 164 IF) — exhaustive field-level validation for every account and customer field
- **3× COPY REPLACING** for screen attribute macros (CSSETATY) — generates significant code expansion
- Touches **6 VSAM files**: ACCTFILE (R/W), CUSTFILE (R/W), CARDXREF (R), CARDFILE (R), plus screen maps
- Contains validation for: dates (via CSUTLDTC), SSN format, phone area codes (NANPA), US state codes, ZIP codes, and ZIP-to-state cross-validation

**Complexity Breakdown:**
| Metric | Value | % of Estate Max |
|--------|-------|----------------|
| LOC | 4,236 | 100% |
| Copybooks | 18 | 100% |
| EVALUATE | 20 | 63% |
| IF | 164 | 100% |
| CICS calls | 17 | High |
| VSAM files | 6 | Highest |

### Rank 2: COTRTLIC.cbl — Transaction Type List (Score: 52.1)

- 2,098 LOC with **28 EXEC SQL** operations (highest SQL density)
- Implements **cursor-based pagination** — DB2 DECLARE CURSOR, OPEN, FETCH, CLOSE cycle
- Handles both TRANSACTION_TYPE and TRANSACTION_CATEGORY tables
- Complex BMS screen management with dynamic attribute control
- 32 IF statements for input validation and error handling

### Rank 3: COTRTUPC.cbl — Transaction Type Update (Score: 46.3)

- 1,702 LOC with **19 EXEC SQL** operations
- Implements **cascading deletes** — delete type requires delete of all child categories
- Complex multi-mode operation: INSERT, UPDATE, DELETE with confirmation screens
- 28 IF statements for field validation

### Rank 4: COCRDUPC.cbl — Credit Card Update (Score: 45.7)

- 1,560 LOC with **13 copybooks**
- 88 branching statements (16 EVALUATE + 72 IF) — extensive validation
- Touches 4 VSAM files (CARDFILE R/W, ACCTFILE R, CUSTFILE R, CARDXREF R)
- Complex screen flow with edit/validation/confirmation cycle

### Rank 5: COCRDLIC.cbl — Credit Card List (Score: 41.2)

- 1,459 LOC implementing **paginated browse pattern** (STARTBR/READNEXT/READPREV/ENDBR)
- This browse pattern is reused in 5+ programs — extracting it enables pattern reuse
- 11 copybooks including filter/selection logic
- Row-level selection with array processing (7-row display)

### Rank 6: COPAUA0C.cbl — Authorization Decision (Score: 40.8)

- 1,026 LOC spanning **3 subsystems** (CICS + IMS + MQ)
- Most **architecturally complex** program — integration of all middleware
- 14 copybooks including MQ message structures (CMQ*)
- Reads from 3 VSAM files, 2 IMS segments, and 2 MQ queues
- Must be migrated as a unit with COPAUS0C/1C/2C

### Rank 7: COPAUS0C.cbl — Authorization Summary Browse (Score: 38.5)

- 1,032 LOC with IMS + CICS integration
- Implements paginated browse over IMS hierarchical data
- 14 copybooks — high coupling to both IMS and VSAM data structures
- Tightly coupled with COPAUS1C and COPAUS2C (CICS LINK chain)

### Rank 8: COBIL00C.cbl — Bill Payment (Score: 35.4)

- 1,026 LOC with complex business logic
- 18 EVALUATE statements — highest EVALUATE count after COACTUPC
- Creates payment transaction, updates account balance, validates card
- Multi-file operations: ACCTFILE (R/W), CARDXREF (R), TRANSACT (browse + write)

### Rank 9: CBSTM03A.CBL — Statement Generation (Score: 32.6)

- 924 LOC as the **primary batch reporting program**
- Calls CBSTM03B subroutine for file I/O (tight coupling)
- Generates **two output formats**: text statements and HTML reports
- Processes 4 input files (transactions, cross-ref, customers, accounts)
- **Highest I/O operation density** among batch programs

### Rank 10: COACTVWC.cbl — Account View (Score: 31.9)

- 941 LOC with **15 copybooks** — reads from all core entity files
- 38 branching statements (10 EVALUATE + 28 IF)
- Read-only but touches 4 VSAM files (ACCTFILE, CUSTFILE, CARDXREF, CARDFILE)
- Complex data assembly from multiple files to single screen

---

## Modernization Priority Recommendations

### Phase 1 — Quick Win + Proof of Concept

#### Recommended First: CBSTM03A/B (Statement Generation)
**Rationale:**
- **Self-contained batch** — no CICS dependencies, no IMS/DB2/MQ
- **Clear input/output** — reads 4 files, writes 2 outputs (text + HTML)
- LOC is manageable (924 + 230 = 1,154 total)
- **Proves the migration approach** for file-based batch processing
- Maps directly to **Spring Batch** with ItemReader/Processor/Writer pattern
- HTML output generation can use modern template engines (Thymeleaf)
- **Low risk** — no impact on online operations during migration

#### Recommended Second: COUSR00C-03C (User Security CRUD)
**Rationale:**
- **Smallest functional cluster** — 4 programs, 1 VSAM file (USRSEC)
- Simple CRUD operations, well-understood pattern
- Total ~1,767 LOC across 4 programs
- No complex subsystem dependencies (pure CICS + VSAM)
- **Validates the online migration pattern** (CICS → REST API)
- Security can be enhanced (password hashing — currently plaintext in CSUSR01Y)

### Phase 2 — DB2 Programs (Natural SQL Migration)

#### COTRTLIC + COTRTUPC + COBTUPDT (Transaction Types)
**Rationale:**
- Already use SQL — **closest to modern database access**
- DB2 SQL translates to JPA/JDBC with minimal transformation
- 2,098 + 1,702 + 237 = 4,037 LOC total
- Cursor-based pagination → Spring Data Pageable
- Cascading deletes → JPA cascade annotations or SQL CASCADE
- Establishes **database access patterns** for all subsequent migrations

### Phase 3 — Core Business Entities

#### COACTUPC (Account Update) — Address the #1 Hotspot
**Rationale:**
- Highest complexity but also highest business impact
- Contains the most comprehensive validation logic in the estate
- **Extracting validation into reusable Java classes** benefits all downstream migrations
- Should be decomposed into:
  - Account view component (read-only, from COACTVWC)
  - Account edit component (validation + update)
  - Shared validation service (reusable across card, transaction screens)
- **Risk mitigation:** Migrate COACTVWC (read-only, rank #10) first as a stepping stone

#### COCRDLIC + COCRDSLC + COCRDUPC (Card Management)
**Rationale:**
- The **paginated browse pattern** in COCRDLIC is used by 5+ programs
- Migrating it creates a reusable `AbstractListController` pattern
- Card update (COCRDUPC, rank #4) exercises similar validation patterns to COACTUPC
- 1,459 + 887 + 1,560 = 3,906 LOC total

### Phase 4 — Cross-Subsystem (Highest Architectural Risk)

#### COPAUA0C + COPAUS0C-2C (Authorization Chain)
**Rationale:**
- **Most architecturally complex** — spans CICS + IMS + MQ + DB2
- Must be migrated as a single unit (tightly coupled LINK chain)
- IMS hierarchical data → 2 relational tables (summary + detail)
- MQ message queues → Spring JMS/AMQP or Amazon SQS
- 1,026 + 1,032 + 604 + 244 = 2,906 LOC total
- **Defer until Phases 1–3 establish patterns** for each subsystem individually

### Phase 5 — Batch Pipeline

#### CBTRN01C → CBTRN02C → CBACT04C → CBTRN03C (Daily Pipeline)
**Rationale:**
- Sequential pipeline — migrate in order
- Each program has clear input/output boundaries
- Can run in parallel with modernized online programs during transition
- Maps to **Spring Batch** job with chained steps
- 834 + 799 + 652 + 649 = 2,934 LOC total

### Phase 6 — Decommission

#### CBEXPORT + CBIMPORT (Data Migration Tools)
- Temporary utilities — retire once all data is in the target database
- 614 + 455 = 1,069 LOC total

---

## Complexity Distribution Visualization

```
LOC Distribution:
════════════════════════════════════════════════════

COACTUPC  ████████████████████████████████████████████ 4,236
COTRTLIC  ██████████████████████             2,098
COTRTUPC  █████████████████                  1,702
COCRDUPC  ████████████████                   1,560
COCRDLIC  ███████████████                    1,459
COPAUS0C  ███████████                        1,032
COBIL00C  ██████████                         1,026
COPAUA0C  ██████████                         1,026
COACTVWC  ██████████                           941
CBSTM03A  █████████                            924
COCRDSLC  █████████                            887
CBTRN01C  █████████                            834
CBTRN02C  ████████                             799
COTRN02C  ████████                             783
COTRN00C  ███████                              699
COUSR00C  ███████                              695
CBACT04C  ███████                              652
CORPT00C  ███████                              649
CBTRN03C  ███████                              649
COACCT01  ██████                               620
CBEXPORT  ██████                               614
COPAUS1C  ██████                               604
CODATE01  █████                                524
CBIMPORT  █████                                455
CBACT01C  ████                                 430
COUSR02C  ████                                 414
CBPAUP0C  ████                                 386
PAUDBLOD  ████                                 369
DBUNLDGS  ████                                 366
COUSR03C  ████                                 359
COTRN01C  ███                                  330
COMEN01C  ███                                  308
COUSR01C  ███                                  299
COADM01C  ███                                  288
COSGN00C  ███                                  263
COPAUS2C  ██                                   244
COBTUPDT  ██                                   237
CBSTM03B  ██                                   230
CBCUS01C  ██                                   227
CBACT02C  ██                                   178
CBACT03C  ██                                   178
CSUTLDTC  ██                                   157
COBSWAIT  █                                     41
```

---

## Risk Assessment Summary

| Risk Factor | High Risk Programs | Mitigation |
|-------------|-------------------|------------|
| **Size** (>2K LOC) | COACTUPC, COTRTLIC, COTRTUPC | Decompose before migrating |
| **Multi-subsystem** | COPAUA0C (3 subsystems) | Migrate last; mock interfaces during phases 1–3 |
| **Data coupling** | COACTUPC (6 files), CBEXPORT (5 files) | Define clear domain boundaries; use anti-corruption layer |
| **Validation density** | COACTUPC (184 branches) | Extract to shared validation library first |
| **Browse pattern** | COCRDLIC, COTRN00C, COUSR00C, COPAUS0C | Create reusable paginated list component |
| **DB2 cursor** | COTRTLIC (28 SQL ops) | Leverage existing SQL; JPA with Pageable |
| **IMS hierarchy** | COPAUA0C, COPAUS0C/1C, CBPAUP0C | Map to 2 relational tables; test referential integrity |
| **MQ messaging** | COPAUA0C, COACCT01, CODATE01 | Spring JMS adapter; message format conversion |

### Estimated Migration Effort by Phase

| Phase | Programs | Total LOC | Estimated Weeks (4-person team) |
|-------|----------|-----------|-------------------------------|
| P1: Quick Win | CBSTM03A/B, COUSR00C-03C | 2,921 | 4–5 weeks |
| P2: DB2 | COTRTLIC, COTRTUPC, COBTUPDT | 4,037 | 5–7 weeks |
| P3: Core Entities | COACTUPC, COACTVWC, COCRDLIC/SLC/UPC | 9,083 | 8–10 weeks |
| P4: Authorization | COPAUA0C, COPAUS0C-2C, CBPAUP0C | 3,292 | 6–8 weeks |
| P5: Batch Pipeline | CBTRN01C/02C, CBACT04C, CBTRN03C | 2,934 | 4–5 weeks |
| P6: Decommission | CBEXPORT, CBIMPORT, utilities | 1,826 | 2–3 weeks |
| **Total** | **44 programs** | **~27,350** | **32–42 weeks** |
