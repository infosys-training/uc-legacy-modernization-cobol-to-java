# HOTSPOT REPORT

> **Application:** CardDemo -- Credit Card Management System (Mainframe)
> **Repository:** infosys-training/uc-legacy-modernization-cobol-to-java
> **Generated:** 2026-06-09

---

## Methodology

Each program is scored across five dimensions. Raw values are normalized to a 0-20 scale per dimension, then summed for a composite score (max 100). Programs are ranked by composite score to identify modernization hotspots.

| Dimension | Weight | What It Measures | Normalization |
|-----------|--------|-----------------|---------------|
| Lines of Code (LOC) | 20 | Raw size / maintenance burden | (LOC / max_LOC) x 20 |
| Copybook Count | 20 | Coupling to shared data structures | (copies / max_copies) x 20 |
| I/O Operations | 20 | Data access breadth (file, DB2, MQ, IMS) | (ios / max_ios) x 20 |
| Business Logic Density | 20 | EVALUATE + IF statement count (branching complexity) | (branches / max_branches) x 20 |
| Dependencies | 20 | Programs called/calling + subsystems touched | (deps / max_deps) x 20 |

---

## Top 10 Programs by Composite Hotspot Score

| Rank | Program | LOC | Copybooks | I/O Ops | Branch Stmts | Dependencies | Composite Score | Classification |
|------|---------|-----|-----------|---------|-------------|-------------|-----------------|----------------|
| **1** | **COACTUPC.cbl** | 4,236 | 58 | 23 | 194 | 3 files + CICS | **74.8** | Online (CICS) |
| **2** | **COTRTLIC.cbl** | 2,098 | 12 | 43 | 131 | DB2 cursors + CICS | **48.4** | Online (CICS/DB2) |
| **3** | **COCRDUPC.cbl** | 1,560 | 16 | 5 | 167 | 2 files + CICS | **42.2** | Online (CICS) |
| **4** | **COTRTUPC.cbl** | 1,702 | 15 | 37 | 134 | DB2 + CICS | **41.8** | Online (CICS/DB2) |
| **5** | **COCRDLIC.cbl** | 1,459 | 14 | 15 | 149 | STARTBR/READNEXT + CICS | **38.6** | Online (CICS) |
| **6** | **COPAUS0C.cbl** | 1,032 | 15 | 4 | 47 | IMS + CICS | **29.0** | Online (CICS/IMS) |
| **7** | **COPAUA0C.cbl** | 1,026 | 17 | 18 | 62 | MQ + IMS + CICS + 3 VSAM | **28.8** | Online (CICS/MQ/IMS) |
| **8** | **CBSTM03A.CBL** | 924 | 5 | 118 | 24 | CALL CBSTM03B + 4 files | **28.2** | Batch |
| **9** | **COACTVWC.cbl** | 941 | 16 | 4 | 70 | 4 files + CICS | **27.4** | Online (CICS) |
| **10** | **CBTRN02C.cbl** | 731 | 6 | 24 | 93 | 5 files | **24.6** | Batch |

---

## Detailed Hotspot Analysis

### #1 -- COACTUPC.cbl (Account Update) -- Score: 74.8/100

**Why it's the top hotspot:**
- **Largest program in the estate** at 4,236 LOC -- nearly 7x the average (686 LOC)
- **58 copybook references** -- most in estate; includes 3x `COPY REPLACING` for CSSETATY screen attribute macros
- **194 branching statements** (EVALUATE + IF) -- exhaustive field-level validation
- Validates dates (CCYYMMDD), SSN format, phone area codes (NANPA), US state codes, ZIP-to-state cross-validation
- Touches 3 VSAM files simultaneously (ACCTFILE, CARDXREF, CUSTFILE)
- BMS screen logic tightly coupled with business validation

**Modernization recommendation:** **Priority 1 -- Decompose first.** Extract validation into reusable service classes (AccountValidationService for date/SSN/phone/state/ZIP). Separate BMS screen logic into REST API + frontend. Split into at minimum 3 components: AccountService, AccountValidationService, AccountController. Generate extensive test cases before rewriting -- the validation logic is the highest-risk area.

---

### #2 -- COTRTLIC.cbl (Transaction Type List -- DB2) -- Score: 48.4/100

**Why it scores high:**
- 2,098 LOC -- second largest program
- 43 I/O operations -- extensive DB2 cursor-based pagination
- 131 branching statements for complex list management
- Already uses DB2 -- most natural entry point for database modernization

**Modernization recommendation:** **Priority 2 -- Early migration candidate.** DB2 SQL maps almost directly to JPA/JDBC. Convert DECLARE CURSOR pagination to Spring Data Page/Pageable. Migrate alongside COTRTUPC as a unit.

---

### #3 -- COCRDUPC.cbl (Credit Card Update) -- Score: 42.2/100

**Why it scores high:**
- 1,560 LOC with 167 branching statements (second-highest branching density)
- Card field validation: account linkage, card number, name, status, expiry month/year
- Shares paginated browse pattern with COCRDLIC

**Modernization recommendation:** **Priority 3.** Create AbstractListController/AbstractUpdateController patterns from COCRDLIC + COCRDUPC pair. The paginated VSAM browse pattern (STARTBR/READNEXT/READPREV/ENDBR) appears in 5+ programs -- extract once, reuse everywhere.

---

### #4 -- COTRTUPC.cbl (Transaction Type Update -- DB2) -- Score: 41.8/100

**Why it scores high:**
- 1,702 LOC, 134 branching statements
- Cascading delete logic across TRAN_TYPE and TRAN_CATEGORY tables
- Complex DB2 CRUD operations

**Modernization recommendation:** **Priority 2 -- Migrate with COTRTLIC as a pair.** JPA cascade annotations replace manual cascading delete logic. Natural fit for Spring Data JPA entities.

---

### #5 -- COCRDLIC.cbl (Credit Card List) -- Score: 38.6/100

**Why it scores high:**
- 1,459 LOC, 149 branching statements
- Implements paginated VSAM browse (STARTBR/READNEXT/READPREV/ENDBR) -- a pattern used by multiple programs
- Complex forward/backward pagination state management

**Modernization recommendation:** **Priority 3.** Extract the pagination pattern into a reusable generic list service. Spring Data `Pageable` replaces the entire STARTBR/READNEXT/ENDBR cycle.

---

### #6 -- COPAUS0C.cbl (Pending Auth Summary -- IMS) -- Score: 29.0/100

**Why it scores high:**
- 1,032 LOC spanning CICS + IMS subsystems
- 15 copybooks bridging IMS data with CICS screen display
- Part of 3-program chain (COPAUS0C → COPAUS1C → COPAUS2C) that must migrate as a unit

**Modernization recommendation:** **Priority 4 -- Migrate as a unit with COPAUS1C and COPAUS2C.** IMS hierarchy maps to 2 relational tables (auth_summary, auth_detail). Most architecturally complex due to cross-subsystem integration.

---

### #7 -- COPAUA0C.cbl (Authorization Decision -- MQ/IMS/CICS) -- Score: 28.8/100

**Why it scores high:**
- 1,026 LOC spanning **three subsystems** (MQ + IMS + CICS) -- most complex integration point
- 17 copybooks including 6 MQ copybooks (CMQODV, CMQMDV, CMQV, etc.)
- Processes real-time auth requests: MQGET → IMS lookup → VSAM reads → MQPUT response

**Modernization recommendation:** **Priority 4 -- Strangler pattern.** Replace MQ with SQS/JMS adapter. Map IMS DL/I to Spring Data JPA. Run old and new in parallel during transition. This is the hardest integration to get right.

---

### #8 -- CBSTM03A.CBL (Statement Generation) -- Score: 28.2/100

**Why it scores high:**
- **118 I/O operations** -- highest in the entire estate
- Generates both text and HTML output (dual-format statements)
- Self-contained batch -- no CICS dependency
- Calls CBSTM03B submodule for I/O delegation

**Modernization recommendation:** **Priority 1 (Pilot).** Best candidate for **proving the migration approach** because it is self-contained, batch-only, and produces verifiable output (text + HTML). Convert to Spring Batch with Thymeleaf for HTML. Compare output byte-for-byte against COBOL baseline.

---

### #9 -- COACTVWC.cbl (Account View) -- Score: 27.4/100

**Why it scores high:**
- 941 LOC, 16 copybooks, 70 branching statements
- Read-only inquiry spanning 4 VSAM files (ACCTFILE, CARDXREF, CUSTFILE, CARDFILE)
- Paired with COACTUPC -- shares most copybooks

**Modernization recommendation:** **Priority 3 -- Migrate alongside COACTUPC** as the read-only counterpart. Simpler than COACTUPC (no writes), making it a good warm-up for the account module migration.

---

### #10 -- CBTRN02C.cbl (Transaction Posting) -- Score: 24.6/100

**Why it scores high:**
- 731 LOC, 93 branching statements
- Core daily batch job: validates and posts transactions from daily feed
- Updates 4 files (TRANSACT, ACCTFILE, TCATBAL, DALYREJS)
- Heart of the daily processing pipeline

**Modernization recommendation:** **Priority 5 -- Migrate as part of batch pipeline.** Convert to Spring Batch step. This is sequencing-critical (must complete before INTCALC). Can run in parallel with modernized online programs during transition period.

---

## Estate-Wide Complexity Distribution

### Programs by LOC Tier

| Tier | LOC Range | Count | % | Programs |
|------|-----------|-------|---|----------|
| Extra-Large | > 2,000 | 2 | 5% | COACTUPC (4,236), COTRTLIC (2,098) |
| Large | 1,000-2,000 | 4 | 9% | COTRTUPC (1,702), COCRDUPC (1,560), COCRDLIC (1,459), COPAUS0C (1,032), COPAUA0C (1,026) |
| Medium | 500-999 | 13 | 30% | CBSTM03A, COACTVWC, COCRDSLC, COTRN02C, CBTRN02C, COTRN00C, COUSR00C, CBACT04C, CORPT00C, CBTRN03C, COACCT01, COPAUS1C, CBEXPORT, COBIL00C, CODATE01 |
| Small | 200-499 | 17 | 39% | Remaining programs |
| Tiny | < 200 | 8 | 18% | CBACT02C, CBACT03C, CBCUS01C, CSUTLDTC, COBSWAIT |

### Technology Risk Map

| Risk Factor | Programs Affected | Impact |
|-------------|-------------------|--------|
| CICS BMS coupling (UI + logic in one program) | 21 programs (48%) | Must decouple UI from business logic |
| VSAM KSDS → RDBMS migration | All batch + online | Schema design required for 10 VSAM files |
| IMS DL/I → relational mapping | 7 programs (16%) | Hierarchical → relational; GNP has no SQL equivalent |
| MQ → modern messaging | 4 programs (9%) | COBOL record → JSON translation layer needed |
| DB2 SQL → JPA | 4 programs (9%) | Lowest risk -- SQL maps almost directly |
| COPY REPLACING macro pattern | COACTUPC (3 instances) | No direct Java equivalent; convert to parameterized classes |
| Assembler dependencies | 2 programs | COBDATFT, MVSWAIT need Java replacements |
| Plain-text passwords | CSUSR01Y (all auth programs) | Must add hashing (bcrypt/scrypt) in Java |

---

## Recommended Modernization Sequence

Based on the hotspot analysis, the recommended migration order optimizes for: (1) proving the approach early with low-risk pilots, (2) tackling highest-complexity items when the team has experience, and (3) minimizing dual-maintenance window.

| Phase | Programs | Rationale | Est. Weeks |
|-------|----------|-----------|------------|
| **P0: Foundation** | (infrastructure setup) | DB schema design, Spring Boot scaffold, CI/CD pipeline | 3-4 |
| **P1: Pilot** | CBSTM03A/B, COSGN00C, COUSR00C-03C | Self-contained batch (provable) + simple CRUD (user mgmt) | 4-5 |
| **P2: DB2 + Card** | COTRTLIC, COTRTUPC, COBTUPDT, COCRDLIC, COCRDSLC, COCRDUPC | Already DB2 → natural JPA fit; extract reusable patterns | 5-7 |
| **P3: Core** | COACTUPC, COACTVWC, COTRN00-02C, COBIL00C, CBTRN01-02C | Highest complexity; team has patterns from P2 | 8-10 |
| **P4: Integration** | COPAUA0C, COPAUS0-2C, COACCT01, CODATE01 | Cross-subsystem (MQ + IMS); requires parallel running | 6-8 |
| **P5: Batch Pipeline** | CBACT04C, CBTRN03C, CBEXPORT, CBIMPORT | Sequential batch; can run alongside modernized online | 4-5 |
| **P6: Decommission** | (retire COBOL runtime) | Cutover + monitoring | 2-3 |
| | | **Total estimated:** | **32-42 weeks** |
