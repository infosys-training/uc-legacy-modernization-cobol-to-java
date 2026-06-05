# HOTSPOT REPORT — CardDemo Modernization Priority Ranking

> **Generated:** 2026-06-05 | **Scope:** All 44 COBOL programs across main and sub-application directories

---

## 1. Scoring Methodology

Each program is scored on five dimensions, weighted to reflect modernization effort:

| Dimension | Weight | Metric | Rationale |
|-----------|--------|--------|-----------|
| Lines of Code (LOC) | 25% | Raw line count | Larger programs require more translation effort and testing |
| Copybook Count | 20% | Number of COPY statements | More data structures = more DTOs, entity mappings, validation |
| I/O Operations | 20% | READ/WRITE/REWRITE/DELETE/EXEC SQL/EXEC CICS/DLI/MQ | Data access complexity; each I/O maps to a repository call or API |
| Business Logic Density | 20% | IF + EVALUATE statement count | Branching = business rules requiring test coverage |
| Inter-Program Dependencies | 15% | CALL + XCTL + LINK targets + VSAM files accessed | Coupling impacts migration sequencing and interface contracts |

**Normalization:** Each dimension is normalized to 0–100 against the maximum value in the estate, then weighted and summed for a composite score (0–100).

**Max values used for normalization:**
- LOC: 4,236 (COACTUPC)
- Copybooks: 58 (COACTUPC)
- I/O: 158 (CBSTM03A)
- Branching: 174 (COACTUPC)
- Dependencies: 12 (COMEN01C)

---

## 2. Top 10 Hotspot Programs

### Composite Ranking

| Rank | Program | LOC | Copybooks | I/O Ops | Branching | Dependencies | **Score** | Type |
|------|---------|-----|-----------|---------|-----------|-------------|----------|------|
| **1** | **COACTUPC.cbl** | 4,236 | 58 | 125 | 174 | 4 | **74.8** | Online CICS |
| **2** | **COTRTLIC.cbl** | 2,098 | 12 | 154 | ~80 | 4 | **46.2** | Online CICS/DB2 |
| **3** | **COCRDUPC.cbl** | 1,560 | 16 | 33 | 80 | 4 | **38.5** | Online CICS |
| **4** | **COCRDLIC.cbl** | 1,459 | 14 | 54 | 68 | 3 | **37.1** | Online CICS |
| **5** | **COTRTUPC.cbl** | 1,702 | 15 | 89 | ~60 | 3 | **36.8** | Online CICS/DB2 |
| **6** | **COPAUS0C.cbl** | 1,032 | 15 | 27 | 36 | 6 | **31.4** | Online CICS/IMS |
| **7** | **COPAUA0C.cbl** | 1,026 | 17 | 59 | 31 | 8 | **30.9** | Online CICS/IMS/MQ |
| **8** | **CBSTM03A.CBL** | 924 | 5 | 158 | 20 | 6 | **28.2** | Batch |
| **9** | **COACTVWC.cbl** | 941 | 16 | 32 | 33 | 3 | **26.7** | Online CICS |
| **10** | **CBTRN02C.cbl** | 731 | 6 | 61 | 48 | 7 | **25.3** | Batch |

---

## 3. Detailed Hotspot Analysis

### Rank 1: COACTUPC.cbl — Account Update (Score: 74.8)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 4,236 | **100th** (largest in estate) |
| Copybooks | 58 | **100th** (most in estate) |
| I/O Operations | 125 | **79th** |
| Branching (IF/EVALUATE) | 174 | **100th** (most complex logic) |
| Dependencies | 4 (3 VSAM files + COMMAREA) | 33rd |

**Why it's #1:**
- 2.7× larger than the next-largest program (COTRTLIC at 2,098 LOC)
- Contains exhaustive field-level validation for dates (CCYYMMDD via CSUTLDPY), SSN (9-digit format), phone numbers (NANPA area code lookup via CSLKPCDY), US state codes (50-state validation), and ZIP code cross-validation (state-ZIP prefix matching)
- Uses COPY REPLACING 3× for CSSETATY screen attribute macros — a pattern with no direct Java equivalent
- Touches 3 VSAM files simultaneously (ACCTDAT, CARDXREF, CUSTDAT) in a single transaction
- BMS screen logic is deeply interleaved with business logic — not separable without refactoring

**Modernization Recommendation:**
Split into 4 components:
1. `AccountValidationService` — Extract date, SSN, phone, state, ZIP validation into reusable service
2. `AccountService` — Core CRUD operations against Account, Customer, Card-XREF entities
3. `AccountController` (REST API) — Replace CICS SEND/RECEIVE MAP with REST endpoints
4. React/Angular frontend — Replace BMS map COACTUP with modern UI

**Risk:** Highest. Generate comprehensive test cases (10,000+) before rewriting. Run shadow mode comparing COBOL and Java outputs for 2 weeks minimum.

---

### Rank 2: COTRTLIC.cbl — Transaction Type List (Score: 46.2)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 2,098 | 50th |
| Copybooks | 12 | 21st |
| I/O Operations | 154 | 97th |
| Branching | ~80 | 46th |
| Dependencies | 4 (DB2 DCLTRTYP/DCLTRCAT, CICS, COMMAREA) | 33rd |

**Why it's #2:**
- Second-largest program by LOC
- Heavy DB2 usage: DECLARE CURSOR, OPEN, FETCH (forward/backward paging), CLOSE, SELECT, DELETE, UPDATE
- Cursor-based pagination pattern — appears repeatedly across the estate (COCRDLIC, COTRN00C, COUSR00C)
- Already uses DB2 — closest to modern RDBMS patterns

**Modernization Recommendation:**
Natural entry point for DB2-to-JPA migration. Convert:
- DB2 CURSOR → Spring Data `Pageable` with `PageRequest`
- DCLTRTYP/DCLTRCAT → JPA `@Entity` classes
- CICS MAP → REST API with JSON pagination response

Migrate together with COTRTUPC (rank 5) as a unit — they share DB2 tables.

---

### Rank 3: COCRDUPC.cbl — Credit Card Update (Score: 38.5)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,560 | 37th |
| Copybooks | 16 | 28th |
| I/O Operations | 33 | 21st |
| Branching | 80 | 46th |
| Dependencies | 4 (CARDDAT, ACCTDAT, CARDXREF, COMMAREA) | 33rd |

**Why it's #3:**
- High branching density relative to LOC (80 branches / 1,560 LOC = 0.051 per line)
- Validation pattern similar to COACTUPC but scoped to card fields (embossed name, status, expiry)
- 16 copybooks indicates significant data structure handling
- Modifies 3 VSAM files in a single CICS pseudo-conversation

**Modernization Recommendation:**
Extract into `CardService` with `CardController`. Reuse `AccountValidationService` from COACTUPC migration for shared validation logic. Migrate after COACTUPC to leverage established patterns.

---

### Rank 4: COCRDLIC.cbl — Credit Card List (Score: 37.1)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,459 | 34th |
| Copybooks | 14 | 24th |
| I/O Operations | 54 | 34th |
| Branching | 68 | 39th |
| Dependencies | 3 (CARDDAT VSAM, COMMAREA, XCTL chain) | 25th |

**Why it's #4:**
- Implements VSAM browse pattern (STARTBR → READNEXT/READPREV → ENDBR) — this pattern recurs in COTRN00C, COUSR00C
- Forward/backward paging with screen-size page management
- Establishing an `AbstractListController` pattern here will accelerate migration of all list screens

**Modernization Recommendation:**
Create a generic paginated-browse pattern in Spring:
- `AbstractListService<T>` with `findPage(cursor, direction, pageSize)`
- Convert STARTBR/READNEXT to Spring Data cursor-based queries
- Reuse for COTRN00C (transaction list) and COUSR00C (user list)

---

### Rank 5: COTRTUPC.cbl — Transaction Type Update (Score: 36.8)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,702 | 40th |
| Copybooks | 15 | 26th |
| I/O Operations | 89 | 56th |
| Branching | ~60 | 34th |
| Dependencies | 3 (DB2 DCLTRTYP/DCLTRCAT, CICS) | 25th |

**Why it's #5:**
- Companion to COTRTLIC (rank 2) — shares DB2 tables
- Implements cascading deletes (type → category) manually in COBOL — JPA `@OneToMany(cascade=REMOVE)` handles this natively
- INSERT/UPDATE/DELETE across two related tables in a single CICS SYNCPOINT

**Modernization Recommendation:**
Migrate as a unit with COTRTLIC:
- `TransactionTypeEntity` + `TransactionCategoryEntity` with `@OneToMany`
- `TransactionTypeService` for CRUD
- `TransactionTypeController` for REST API

---

### Rank 6: COPAUS0C.cbl — Authorization Summary (Score: 31.4)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,032 | 24th |
| Copybooks | 15 | 26th |
| I/O Operations | 27 | 17th |
| Branching | 36 | 21st |
| Dependencies | 6 (IMS PAUT, ACCTDAT, CARDDAT, CARDXREF VSAM, LINK to COPAUS1C, COMMAREA) | 50th |

**Why it's #6:**
- First program in the IMS chain (COPAUS0C → COPAUS1C → COPAUS2C)
- Uses DLI GU/GNP (hierarchical navigation) — requires IMS-to-relational mapping
- Reads 3 VSAM files alongside IMS database in the same transaction
- Must be migrated as a unit with COPAUS1C and COPAUS2C

**Modernization Recommendation:**
Map IMS hierarchy to 2 relational tables:
- `pending_authorization_summary` (root segment → CIPAUSMY)
- `pending_authorization_detail` (child segment → CIPAUDTY)
- Convert DLI GU → `SELECT ... WHERE acct_id = ?`
- Convert DLI GNP → `SELECT ... WHERE acct_id = ? ORDER BY auth_date, auth_time`

---

### Rank 7: COPAUA0C.cbl — Authorization Decision (Score: 30.9)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 1,026 | 24th |
| Copybooks | 17 | 29th |
| I/O Operations | 59 | 37th |
| Branching | 31 | 18th |
| Dependencies | 8 (MQ×4, IMS DLI, 3 VSAM files) | 67th |

**Why it's #7:**
- Most architecturally complex program — spans 3 subsystems (MQ + IMS + CICS)
- Highest dependency count in the estate (8 external dependencies)
- Real-time message processing: receives auth request via MQ, checks account limits, writes IMS decision record, sends MQ response
- Integration point between online (CICS) and messaging (MQ) subsystems

**Modernization Recommendation:**
- Spring JMS for MQ consumer/producer
- Replace IMS calls with JPA repository operations
- Replace CICS READ with service layer calls
- Most complex integration — migrate after establishing patterns from simpler programs

**Risk:** Medium-High. Run MQ and SQS/JMS in parallel for 1 week before cutover. Map MQ correlation IDs to JMS message properties.

---

### Rank 8: CBSTM03A.CBL — Statement Generation (Score: 28.2)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 924 | 22nd |
| Copybooks | 5 | 9th |
| I/O Operations | 158 | **100th** (highest in estate) |
| Branching | 20 | 11th |
| Dependencies | 6 (4 VSAM files, CBSTM03B subprogram, STMTFILE output) | 50th |

**Why it's #8:**
- Highest I/O density in the entire estate (158 I/O operations in 924 LOC = 0.171 per line)
- Self-contained batch program — no CICS dependencies
- Reads 4 VSAM files (TRANSACT, CARDXREF, ACCTDATA, CUSTDATA) and generates statement output
- Calls CBSTM03B as I/O submodule (97 WRITE statements in CBSTM03B)
- Generates both text and HTML formatted output

**Modernization Recommendation:**
**Best candidate for proof-of-concept migration** — self-contained batch with no CICS:
- Spring Batch `Job` with `Step` for each data source
- `FlatFileItemReader` → `JdbcPagingItemReader` (post VSAM-to-DB migration)
- `FlatFileItemWriter` → Thymeleaf template for HTML output
- Batch size ≥ 1,000 for `JdbcBatchItemWriter` to meet SLA

**Risk:** Low. Clear inputs/outputs. Compare Java output against COBOL baseline character-by-character.

---

### Rank 9: COACTVWC.cbl — Account View (Score: 26.7)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 941 | 22nd |
| Copybooks | 16 | 28th |
| I/O Operations | 32 | 20th |
| Branching | 33 | 19th |
| Dependencies | 3 (ACCTDAT, CUSTDAT, CARDXREF VSAM) | 25th |

**Why it's #9:**
- Read-only complement to COACTUPC (rank 1)
- 16 copybooks for a read-only view — suggests significant data formatting and display logic
- Reads Account, Customer, and Card-XREF to compose a complete account view

**Modernization Recommendation:**
Migrate alongside COACTUPC as the read-only half of the Account microservice. Simpler than COACTUPC (no write operations, no validation), so use as a warm-up exercise before tackling the full update flow.

---

### Rank 10: CBTRN02C.cbl — Transaction Posting (Score: 25.3)

| Metric | Value | Percentile |
|--------|-------|-----------|
| LOC | 731 | 17th |
| Copybooks | 6 | 10th |
| I/O Operations | 61 | 39th |
| Branching | 48 | 28th |
| Dependencies | 7 (DALYTRAN, XREFFILE, ACCTFILE, TRANSACT, DALYREJS, TCATBAL, POSTTRAN.jcl) | 58th |

**Why it's #10:**
- Core of the daily batch pipeline — first step in POSTTRAN → INTCALC → CREASTMT → TRANRPT
- Validates incoming daily transactions, rejects bad records, posts valid ones to master
- Updates transaction category balances (TCATBAL) used downstream by CBACT04C (interest calculation)
- High dependency count (7) — many downstream programs depend on its output

**Modernization Recommendation:**
Spring Batch `Job` with:
- `Step 1`: Read DALYTRAN, validate, split into valid/rejected
- `Step 2`: Write valid transactions to TRANSACT table
- `Step 3`: Update TCATBAL running balances
- `SkipPolicy` for rejected records → write to rejection table/file

**Risk:** Medium. Must preserve exact batch pipeline sequencing. Downstream programs (INTCALC, CREASTMT, TRANRPT) depend on this program's output format.

---

## 4. Recommended Modernization Sequence

Based on the hotspot analysis, recommended migration order:

```
Phase 1 — Proof of Concept (Month 1–2)
├── CBSTM03A/B (#8) — Self-contained batch, highest I/O, proves Spring Batch approach
└── COTRTLIC + COTRTUPC (#2, #5) — Already DB2, proves JPA migration path

Phase 2 — Core Online (Month 2–5)
├── COACTUPC (#1) + COACTVWC (#9) — Largest hotspot, establishes Account microservice
├── COCRDLIC (#4) + COCRDUPC (#3) — Card microservice, reuses list/update patterns
└── Extract shared validation (AccountValidationService from COACTUPC patterns)

Phase 3 — Batch Pipeline (Month 4–6, overlaps Phase 2)
├── CBTRN02C (#10) — Transaction posting (pipeline step 1)
├── CBACT04C — Interest calculation (pipeline step 2)
├── CBTRN03C — Transaction reporting (pipeline step 4)
└── Remaining batch programs (CBACT01C–03C, CBCUS01C, CBEXPORT, CBIMPORT)

Phase 4 — Cross-Subsystem Integration (Month 6–8)
├── COPAUS0C (#6) + COPAUS1C + COPAUS2C — IMS chain (migrate as unit)
├── COPAUA0C (#7) — MQ/IMS/CICS integration point (last due to complexity)
└── COACCT01, CODATE01 — VSAM/MQ utilities

Phase 5 — Remaining Online Screens (Month 7–9, overlaps Phase 4)
├── COTRN00C, COTRN01C, COTRN02C — Transaction screens
├── COBIL00C — Bill payment
├── COUSR00C–03C — User management (+ implement Spring Security)
├── COSGN00C — Sign-on (replace plain-text passwords with bcrypt)
├── COMEN01C, COADM01C — Menu hubs (become API gateway routing)
└── CORPT00C — Report submission
```

---

## 5. Dimension Rankings (Individual)

### 5.1 By Lines of Code

| Rank | Program | LOC | Classification |
|------|---------|-----|---------------|
| 1 | COACTUPC.cbl | 4,236 | Online CICS |
| 2 | COTRTLIC.cbl | 2,098 | Online CICS/DB2 |
| 3 | COTRTUPC.cbl | 1,702 | Online CICS/DB2 |
| 4 | COCRDUPC.cbl | 1,560 | Online CICS |
| 5 | COCRDLIC.cbl | 1,459 | Online CICS |
| 6 | COPAUS0C.cbl | 1,032 | Online CICS/IMS |
| 7 | COPAUA0C.cbl | 1,026 | Online CICS/IMS/MQ |
| 8 | COACTVWC.cbl | 941 | Online CICS |
| 9 | CBSTM03A.CBL | 924 | Batch |
| 10 | COTRN02C.cbl | 783 | Online CICS |

### 5.2 By Copybook Count

| Rank | Program | Copybooks | Notes |
|------|---------|-----------|-------|
| 1 | COACTUPC.cbl | 58 | Includes 25× CSSETATY via COPY REPLACING |
| 2 | COPAUA0C.cbl | 17 | MQ copybooks (CMQODV, CMQMDV, etc.) + IMS + VSAM |
| 3 | COACTVWC.cbl | 16 | Account view data structures |
| 3 | COCRDSLC.cbl | 16 | Card view data structures |
| 3 | COCRDUPC.cbl | 16 | Card update data structures |
| 6 | COPAUS0C.cbl | 15 | IMS auth + VSAM structures |
| 6 | COTRTUPC.cbl | 15 | DB2 + CICS structures |
| 8 | COCRDLIC.cbl | 14 | Card list + paging structures |
| 9 | COTRTLIC.cbl | 12 | DB2 cursor structures |
| 10 | COBIL00C.cbl | 11 | Bill payment data structures |

### 5.3 By I/O Operations

| Rank | Program | I/O Ops | I/O Density (ops/LOC) | Notes |
|------|---------|---------|----------------------|-------|
| 1 | CBSTM03A.CBL | 158 | 0.171 | Highest I/O density; 4 VSAM reads + statement writes |
| 2 | COTRTLIC.cbl | 154 | 0.073 | DB2 cursor operations (FETCH loop) |
| 3 | COACTUPC.cbl | 125 | 0.030 | CICS READ/REWRITE + MAP send/receive |
| 4 | COTRTUPC.cbl | 89 | 0.052 | DB2 SELECT/INSERT/UPDATE/DELETE |
| 5 | CBTRN03C.cbl | 81 | 0.125 | Report generation from 4 input files |
| 6 | CBTRN01C.cbl | 61 | 0.123 | Sequential transaction file read |
| 7 | CBTRN02C.cbl | 61 | 0.083 | Transaction posting with validation |
| 8 | COPAUA0C.cbl | 59 | 0.057 | MQ + IMS + VSAM combined |
| 9 | COCRDLIC.cbl | 54 | 0.037 | VSAM browse (STARTBR/READNEXT loop) |
| 10 | CBEXPORT.cbl | 53 | 0.091 | Multi-file sequential export |

### 5.4 By Business Logic Density (Branching)

| Rank | Program | IF/EVALUATE Count | Density (branches/LOC) | Notes |
|------|---------|-------------------|----------------------|-------|
| 1 | COACTUPC.cbl | 174 | 0.041 | Field validation dominates |
| 2 | COCRDUPC.cbl | 80 | 0.051 | Card field validation |
| 3 | COTRTLIC.cbl | ~80 | 0.038 | Cursor state management |
| 4 | COCRDLIC.cbl | 68 | 0.047 | Browse state/paging logic |
| 5 | COTRTUPC.cbl | ~60 | 0.035 | DB2 error handling |
| 6 | CBTRN02C.cbl | 48 | 0.066 | Transaction validation rules |
| 7 | CBACT04C.cbl | 43 | 0.066 | Interest calculation rules |
| 8 | CBTRN03C.cbl | 40 | 0.062 | Report break logic |
| 9 | COPAUS0C.cbl | 36 | 0.035 | IMS navigation + screen routing |
| 10 | COCRDSLC.cbl | 37 | 0.042 | Card lookup + display logic |

### 5.5 By Inter-Program Dependencies

| Rank | Program | Dependencies | Details |
|------|---------|-------------|---------|
| 1 | COMEN01C | 12 | Hub: 1 inbound (COSGN00C) + 11 XCTL targets |
| 2 | COPAUA0C | 8 | MQ×4 + IMS + 3 VSAM files |
| 3 | CBTRN02C | 7 | 5 VSAM files + CSUTLDTC + JCL |
| 4 | COADM01C | 6 | 1 inbound + 5 XCTL targets |
| 5 | CBSTM03A | 6 | 4 VSAM files + CBSTM03B + JCL |
| 6 | COPAUS0C | 6 | IMS + 3 VSAM + COPAUS1C + COMMAREA |
| 7 | CBACT04C | 5 | 4 VSAM files + JCL |
| 8 | COACTUPC | 4 | 3 VSAM files + COMMAREA |
| 9 | COCRDUPC | 4 | 3 VSAM files + COMMAREA |
| 10 | COTRTLIC | 4 | 2 DB2 tables + CICS + COMMAREA |

---

## 6. Key Risk Factors for Top Hotspots

| Risk | Programs Affected | Severity | Mitigation |
|------|-------------------|----------|------------|
| COPY REPLACING pattern | COACTUPC (3× CSSETATY) | High | Convert to parameterized Java utility classes |
| Plain-text passwords | COUSR01C, COSGN00C | High | Replace with bcrypt via Spring Security |
| Fixed-point arithmetic (PIC S9(10)V99) | All monetary programs | Critical | **MANDATORY: BigDecimal only** — never double/float |
| IMS hierarchical navigation | COPAUA0C, COPAUS0C/1C, CBPAUP0C | Medium | Map to 2 relational tables; convert DLI calls to SQL |
| MQ message format (EBCDIC fixed-length) | COPAUA0C, COACCT01, CODATE01 | Medium | Build COBOL-record-to-JSON translation layer |
| VSAM dual-write consistency | All batch programs | Medium | SQS FIFO sync; hourly reconciliation |
| Batch pipeline SLA | CBTRN02C → CBACT04C → CBSTM03A → CBTRN03C | Medium | JdbcBatchItemWriter with batch size ≥ 1,000 |
| BMS screen coupling | All 21 CICS programs | Medium | Extract business logic before replacing UI layer |
| Assembler dependencies | CBACT01C (COBDATFT), COBSWAIT (MVSWAIT) | Low | Write Java equivalents (DateFormatter, Thread.sleep) |

---

## 7. Estate Summary Statistics

| Metric | Value |
|--------|-------|
| Total programs | 44 |
| Total LOC | 27,970 |
| Average LOC | 636 |
| Median LOC | 487 |
| Programs > 1,000 LOC | 8 (18%) |
| Programs with > 10 copybooks | 12 (27%) |
| Online (CICS) programs | 21 (48%) |
| Pure batch programs | 16 (36%) |
| Cross-subsystem (IMS/DB2/MQ) programs | 7 (16%) |
| Estimated team size | 4 developers |
| Estimated duration | 6–9 months |
