# HOTSPOT REPORT — CardDemo COBOL Estate

> **Top 10 programs ranked by complexity metrics with modernization priority recommendations.**

---

## 1. Scoring Methodology

Each program is scored across five dimensions, normalized to a 0–100 composite scale:

| Metric | Weight | How Measured | Max in Estate |
|--------|--------|-------------|---------------|
| **Lines of Code** | 25% | `wc -l` on source file | 4,236 (COACTUPC) |
| **Copybook Count** | 20% | Distinct COPY statements (CSSETATY REPLACING counted as 1) | 15 unique + 31 REPLACING (COACTUPC) |
| **I/O Operations** | 20% | READ + WRITE + REWRITE + OPEN + CLOSE + EXEC CICS READ/WRITE/STARTBR/READNEXT/READPREV/ENDBR + EXEC SQL | 115 (CBSTM03A) |
| **Business Logic Density** | 20% | EVALUATE count + IF count (branching statements) | 353 (COACTUPC: 20 EVALUATE + 333 IF) |
| **Inter-Program Dependencies** | 15% | CALL + XCTL + LINK targets + files shared with other programs | 12 (COMEN01C) |

**Composite Score** = Σ (normalized metric × weight) × 100

---

## 2. Top 10 Hotspot Rankings

| Rank | Program | LOC | Copybooks | I/O Ops | Branch Stmts | Dependencies | Composite Score | Classification |
|------|---------|-----|-----------|---------|-------------|-------------|----------------|---------------|
| **1** | **COACTUPC.cbl** | 4,236 | 15 + 31 REPLACING | 19 | 353 (20E + 333IF) | 5 (3 VSAM files, XCTL hub) | **74.8** | CICS Online |
| **2** | **COCRDUPC.cbl** | 1,560 | 13 | 13 | 164 (16E + 148IF) | 4 (XCTL, 2 files) | **40.2** | CICS Online |
| **3** | **COCRDLIC.cbl** | 1,459 | 11 | 14 | 140 (18E + 122IF) | 5 (3 XCTL targets) | **38.5** | CICS Online |
| **4** | **COPAUS0C.cbl** | 1,032 | 14 | 12 | 72 (22E + 50IF) | 6 (IMS DB + 3 VSAM + LINK) | **35.4** | CICS/IMS/BMS |
| **5** | **COPAUA0C.cbl** | 1,026 | 14 | 16 | 63 (10E + 53IF) | 8 (MQ×4 + IMS + 3 VSAM) | **34.9** | CICS/IMS/MQ |
| **6** | **CBSTM03A.CBL** | 924 | 4 | 115 | 39 (9E + 30IF) | 2 (CALL CBSTM03B, 2 output files) | **28.2** | Batch |
| **7** | **COACTVWC.cbl** | 941 | 15 | 15 | 67 (10E + 57IF) | 4 (3 VSAM reads, XCTL) | **28.0** | CICS Online |
| **8** | **COCRDSLC.cbl** | 887 | 13 | 13 | 76 (8E + 68IF) | 4 (XCTL, 2 VSAM reads) | **27.1** | CICS Online |
| **9** | **COTRN02C.cbl** | 783 | 10 | 12 | 54 (26E + 28IF) | 5 (CALL CSUTLDTC, 3 VSAM, WRITE) | **24.3** | CICS Online |
| **10** | **CBTRN02C.cbl** | 731 | 5 | 21 | 96 (0E + 96IF) | 4 (6 files R/W) | **22.8** | Batch |

---

## 3. Detailed Hotspot Analysis

### Rank 1: COACTUPC.cbl — Account Update (Score: 74.8)

**Why it's #1:** Largest program in the estate at 4,236 LOC with the highest branching complexity (353 statements). Performs exhaustive field-level validation of account, customer, and card data using:
- 31 instances of `COPY CSSETATY REPLACING` for BMS screen attribute macros
- Date validation via `CSUTLDWY`/`CSUTLDPY` (inline procedure division copybook)
- State code, phone area code, and ZIP prefix validation via `CSLKPCDY` (1,318-line lookup table)
- Touches 3 VSAM files: ACCTFILE (REWRITE), CUSTFILE (REWRITE), CARDXREF (READ)

**Key Risk:** This program is the central data mutation point for account/customer updates. Any bug in modernization directly impacts financial data integrity.

**Modernization Strategy:**
- Extract validation logic into reusable Java validation service classes
- Split into Account View (read-only), Account Edit (mutations), and Field Validation Service
- Convert BMS screen interaction to REST API endpoints
- Replace COPY REPLACING macro pattern with Java inheritance or composition

---

### Rank 2: COCRDUPC.cbl — Credit Card Update (Score: 40.2)

**Why it's #2:** 1,560 LOC with 164 branching statements. Handles card detail updates with customer lookup and card data validation. Uses `REWRITE FILE(LIT-CARDFILENAME)` — direct VSAM REWRITE by file name rather than FD, which is a less common pattern.

**Modernization Strategy:**
- JPA entity for Card with account/customer relationships
- Convert CICS READ/REWRITE to Spring Data JPA save operations
- Reuse validation framework from COACTUPC migration

---

### Rank 3: COCRDLIC.cbl — Credit Card List (Score: 38.5)

**Why it's #3:** 1,459 LOC implementing paginated browse with STARTBR/READNEXT/READPREV/ENDBR — the most complex browsing pattern in the estate. Has 3 XCTL targets (COCRDSLC, COCRDUPC, back to menu).

**Modernization Strategy:**
- Implement Spring Data `Pageable` interface
- Create generic `AbstractListController` pattern — this browse pattern appears in 5+ programs (COTRN00C, COUSR00C, COBIL00C)
- First convert COCRDLIC as the template, then apply pattern to all list screens

---

### Rank 4: COPAUS0C.cbl — Auth Summary (Score: 35.4)

**Why it's #4:** 1,032 LOC spanning CICS + IMS + BMS with 22 EVALUATE statements (highest in estate). Reads IMS hierarchical database with DL/I calls (GU, GNP) and cross-references with 3 VSAM files. Forms a LINK chain: COPAUS0C → COPAUS1C → COPAUS2C.

**Modernization Strategy:**
- Map IMS hierarchy (summary → detail segments) to 2 relational tables
- Migrate COPAUS0C/1C/2C as a unit — they share IMS PCBs and LINKAGE SECTION data
- Replace DL/I calls with JPA repository queries

---

### Rank 5: COPAUA0C.cbl — Auth Decision (Score: 34.9)

**Why it's #5:** 1,026 LOC with the highest middleware complexity — spans MQ (4 API calls), IMS (DL/I), and CICS (READ × 3). This is the single program that touches all three non-VSAM subsystems simultaneously.

**Modernization Strategy:**
- Spring JMS/AMQP adapter for MQ message consumption/production
- IMS database access via JPA (same mapping as COPAUS0C)
- CICS file reads → JPA entity lookups
- Deploy as independent Authorization microservice

---

### Rank 6: CBSTM03A.CBL — Statement Generation (Score: 28.2)

**Why it's #6:** 924 LOC with 115 I/O operations (highest in estate) — writes text statements and HTML output. Uses CALL to CBSTM03B subroutine for file I/O delegation. Pure batch with no CICS dependency.

**Modernization Strategy:**
- **Recommended as first migration proof-of-concept** — self-contained batch, no CICS, no IMS
- Spring Batch job: ItemReader (CBSTM03B reads 4 files) → ItemProcessor → ItemWriter (text + HTML)
- Replace HTML generation with Thymeleaf templates
- Validates the batch migration approach before tackling complex programs

---

### Rank 7: COACTVWC.cbl — Account View (Score: 28.0)

**Why it's #7:** 941 LOC with 15 copybooks. Read-only account viewing with 3 CICS READ operations to look up account, card, and customer data. Similar structure to COACTUPC but without the mutation complexity.

**Modernization Strategy:**
- Read-only REST endpoint — simpler than COACTUPC
- Reuse entity classes created for COACTUPC migration
- Convert BMS SEND MAP to JSON response

---

### Rank 8: COCRDSLC.cbl — Card Detail View (Score: 27.1)

**Why it's #8:** 887 LOC with 76 branching statements. Card detail display with customer name lookup. HANDLE ABEND → ABEND pattern for error recovery.

**Modernization Strategy:**
- Read-only card detail endpoint
- Reuse Card JPA entity from COCRDUPC migration

---

### Rank 9: COTRN02C.cbl — Transaction Add (Score: 24.3)

**Why it's #9:** 783 LOC with 26 EVALUATE statements. Online transaction entry with date validation (CALL CSUTLDTC), card cross-reference lookup, and direct VSAM WRITE to TRANSACT file. Uses STARTBR/READPREV/ENDBR for transaction ID generation.

**Modernization Strategy:**
- REST POST endpoint for transaction creation
- Replace VSAM ID generation with database sequence
- Reuse date validation from COACTUPC framework

---

### Rank 10: CBTRN02C.cbl — Transaction Posting (Score: 22.8)

**Why it's #10:** 731 LOC with 96 IF statements and 21 I/O operations spanning 6 files. Core batch posting program used by POSTTRAN.jcl — reads DALYTRAN, validates via XREFFILE, updates ACCTFILE and TCATBALF, writes TRANSACT and DALYREJS (rejects).

**Modernization Strategy:**
- Spring Batch step with chunk-oriented processing
- ItemReader for DALYTRAN → ItemProcessor for validation/balance update → ItemWriter for TRANSACT
- Rejected records routed to separate error table

---

## 4. Modernization Prioritization Roadmap

### Phase 1: Prove the Approach (Batch First)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| **1** | CBSTM03A + CBSTM03B | Self-contained batch; highest I/O count proves Spring Batch migration pattern; no CICS/IMS/MQ dependencies |
| **2** | CBTRN02C | Core batch posting; validates chunk-oriented Spring Batch pattern with multi-file I/O |

### Phase 2: DB2 Programs (Natural SQL Fit)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| **3** | COTRTLIC + COTRTUPC | Already use SQL — DB2 cursor pagination maps directly to Spring Data JPA; establishes DB2 → PostgreSQL/MySQL migration pattern |
| **4** | CSDB2LOD | DB2 batch loader — validates INSERT migration |

### Phase 3: Core Online Programs (Highest Business Impact)

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| **5** | COACTUPC | Highest-complexity program; establishes validation framework, BMS→REST pattern, and VSAM→JPA pattern that all other online programs reuse |
| **6** | COCRDLIC + COCRDSLC + COCRDUPC | Card management suite; establishes paginated browse pattern (reusable in COTRN00C, COUSR00C) |
| **7** | COTRN00C + COTRN01C + COTRN02C | Transaction management; reuses browse and validation patterns from prior phases |

### Phase 4: Cross-Subsystem Integration

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| **8** | COPAUS0C + COPAUS1C + COPAUS2C | IMS LINK chain — must migrate as unit; IMS hierarchy → 2 relational tables |
| **9** | COPAUA0C | Most complex integration (MQ + IMS + CICS); depends on IMS mapping from step 8 |

### Phase 5: Remaining Batch and Utilities

| Order | Program(s) | Rationale |
|-------|-----------|-----------|
| **10** | CBACT04C, CBTRN03C | Interest calculation and reporting — standard batch |
| **11** | CBEXPORT + CBIMPORT | Data migration utilities — may be replaced entirely by modern ETL |
| **12** | COUSR00C–03C, COSGN00C, COADM01C, COMEN01C | User admin and navigation — replace with Spring Security + modern UI framework |

---

## 5. Risk Summary

| Risk Factor | Impact | Mitigation |
|-------------|--------|------------|
| ACCTFILE contention (batch vs. online) | HIGH — concurrent writes from CBTRN02C and COACTUPC | Database-level locking replaces VSAM SHAREOPTIONS |
| IMS hierarchical data model | MEDIUM — 2-segment hierarchy maps cleanly to 2 tables | Create PENDING_AUTH and PENDING_AUTH_DETAIL tables |
| 31× COPY REPLACING in COACTUPC | HIGH — generates ~930 lines of macro-expanded BMS attribute code | Replace with generic Java validation framework |
| CEE3ABD abend handling in 9 programs | LOW — replace with Java exception handling | try/catch with logging |
| CSUTLDTC date validation shared by 2 CICS programs | LOW — replace with `java.time` API | — |
| Plain-text passwords in CSUSR01Y | LOW (modernization improves security) | Spring Security with BCrypt hashing |

---

## 6. Estate Metrics Summary

| Metric | Value |
|--------|-------|
| Total programs | 44 |
| Total LOC | 27,350 |
| Average LOC/program | 622 |
| Median LOC/program | 487 |
| Programs > 1,000 LOC | 8 (18%) |
| Programs with > 10 copybooks | 12 (27%) |
| Total copybooks | 47 |
| Total JCL jobs | 46 |
| BMS maps | 16 |
| CICS programs | 21 (48%) |
| Pure batch programs | 16 (36%) |
| Sub-app programs (IMS/DB2/MQ) | 7 (16%) |
| Estimated modernization effort | 6–9 months (4-person team) |

---

*Generated from static analysis of 44 COBOL programs. Metrics extracted from source code: LOC counts, COPY statement counts, I/O operation counts (READ/WRITE/REWRITE/OPEN/CLOSE/EXEC CICS/EXEC SQL), branching statement counts (IF + EVALUATE), and inter-program dependency analysis (CALL/XCTL/LINK/shared files).*
