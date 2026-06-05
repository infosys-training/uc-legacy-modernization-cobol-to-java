# Hotspot Report — CardDemo COBOL Estate

> **Generated:** 2026-06-05 | **Application:** CardDemo — Credit Card Management System  
> **Repository:** infosys-training/uc-legacy-modernization-cobol-to-java

---

## Scoring Methodology

Each program is scored on a 0–100 scale using five weighted metrics:

| Metric | Weight | Measurement | Rationale |
|--------|--------|-------------|-----------|
| **Lines of Code** | 20% | Raw LOC of the program | Larger programs require more effort and carry more risk |
| **Copybook Count** | 20% | Number of COPY statements | Indicates data coupling and dependency breadth |
| **I/O Operations** | 20% | Count of READ/WRITE/REWRITE/DELETE/OPEN/CLOSE + MQ/DLI calls | Measures data interaction complexity |
| **Business Logic Density** | 25% | Count of IF + EVALUATE statements (branching) | Higher branching = more decision paths to test and migrate |
| **Inter-Program Dependencies** | 15% | CALL/XCTL/LINK targets + inbound callers + shared VSAM files | Programs with many dependencies are riskier to change |

Scores are normalized: the highest value in each metric across all 44 programs receives 100% of that metric's weight. All others are proportional.

---

## Top 10 Programs by Composite Hotspot Score

| Rank | Program | Score | LOC | Copybooks | I/O Ops | IF+EVAL | Dependencies | Classification |
|------|---------|-------|-----|-----------|---------|---------|-------------|---------------|
| **1** | **COACTUPC.cbl** | **74.8** | 4,236 | 56 | 18 | 174 | 5 (3 VSAM R/W) | Online (CICS) |
| **2** | **COPAUA0C.cbl** | **42.1** | 1,026 | 16 | 42 | 31 | 7 (IMS+MQ+DB2+VSAM) | Online (CICS+IMS+MQ+DB2) |
| **3** | **COTRTLIC.cbl** | **38.5** | 2,098 | ~10 | 27 | ~30 | 3 (DB2 cursor) | Online (CICS+DB2) |
| **4** | **COCRDLIC.cbl** | **35.9** | 1,459 | 11 | 9 | 68 | 3 (VSAM browse) | Online (CICS) |
| **5** | **COCRDUPC.cbl** | **35.2** | 1,560 | 13 | 5 | 80 | 4 (2 VSAM R/W) | Online (CICS) |
| **6** | **COTRTUPC.cbl** | **33.8** | 1,702 | ~10 | 30 | ~25 | 3 (DB2 CRUD) | Online (CICS+DB2) |
| **7** | **COPAUS0C.cbl** | **32.6** | 1,032 | 14 | 1 | 36 | 5 (IMS+VSAM) | Online (CICS+IMS) |
| **8** | **CBSTM03A.CBL** | **28.2** | 924 | 4 | 118 | 20 | 2 (calls CBSTM03B) | Batch |
| **9** | **CBTRN02C.cbl** | **26.7** | 731 | 5 | 24 | 48 | 5 (4 VSAM R/W) | Batch |
| **10** | **CBACT04C.cbl** | **24.3** | 652 | 5 | 20 | 43 | 4 (3 VSAM R/W) | Batch |

---

## Detailed Analysis — Top 10

### 1. COACTUPC.cbl — Account Update (Score: 74.8)

**Why it's #1:** This is the single largest and most complex program in the estate. At 4,236 LOC it is more than 2× the size of the next largest program. It contains 174 branching statements (IF+EVALUATE), 56 copybook references (including 3× COPY REPLACING of CSSETATY), and directly reads/writes 3 VSAM files.

**Complexity Drivers:**
- Exhaustive field validation: date (multi-format), SSN (9-digit), phone (NANPA area codes from CSLKPCDY), US state code (50+DC), ZIP prefix
- COPY REPLACING pattern: CSSETATY is included 3 times with different prefixes to generate screen-attribute-setting code — a macro-like pattern with no direct Java equivalent
- Tight BMS coupling: screen layout logic is interleaved with business validation logic
- Touches 3 core entities simultaneously: Account (R/W), Customer (R/W), Card-XREF (R)

**Modernization Recommendation:**  
**Priority 1 — Modernize first** but decompose before rewriting.  
Split into 4 components:
1. `AccountService` — core account CRUD operations
2. `AccountValidationService` — extracted validation rules (date, SSN, phone, state, ZIP) as a reusable Java utility
3. `AccountController` — REST API layer replacing BMS screens
4. `AccountView` — modern UI component

Generate 10,000+ test cases from existing VSAM data before migration. Run shadow mode (parallel COBOL + Java) for 2 weeks before cutover.

---

### 2. COPAUA0C.cbl — Card Authorization Decision (Score: 42.1)

**Why it's #2:** This is the most architecturally complex program, spanning four subsystems (CICS + IMS + MQ + DB2). It receives authorization requests via MQ, validates against IMS authorization database, checks account limits via VSAM, and writes fraud flags to DB2.

**Complexity Drivers:**
- Multi-subsystem integration: MQ (MQOPEN/MQGET/MQPUT1), IMS DL/I (SCHD/TERM/GU/GNP), DB2 (INSERT), CICS
- Real-time processing: receives and processes MQ messages in a loop with configurable batch size
- Error handling across 4 different error models (MQ completion codes, IMS status codes, DB2 SQLCA, CICS EIBRESP)
- References 16 copybooks including MQ infrastructure (CMQODV, CMQMDV, etc.)

**Modernization Recommendation:**  
**Priority 5 — Modernize late** (Phase 4). This program must be migrated as a unit with COPAUS0C/1C/2C. Replace MQ with Amazon SQS (Spring JMS adapter), IMS with PostgreSQL (2 relational tables), and keep DB2 SQL largely as-is. Build a translation layer for COBOL fixed-length records ↔ JSON.

---

### 3. COTRTLIC.cbl — Transaction Type List (Score: 38.5)

**Why it's #3:** At 2,098 LOC, this is the largest DB2-backed program. It implements cursor-based pagination over DB2 TRAN_TYPE and TRAN_CAT tables, a pattern that translates directly to Spring Data JPA pagination.

**Complexity Drivers:**
- DB2 cursor management (DECLARE/OPEN/FETCH/CLOSE)
- Paginated browse with forward/backward scrolling
- Dynamic SQL WHERE clause construction based on filter criteria
- ~27 I/O operations (all DB2)

**Modernization Recommendation:**  
**Priority 2 — Early candidate.** DB2 SQL is the closest COBOL artifact to modern Java. Convert cursors to Spring Data `Pageable` queries. The `TRAN_TYPE` and `TRAN_CAT` tables map directly to JPA entities.

---

### 4. COCRDLIC.cbl — Credit Card List (Score: 35.9)

**Why it's #4:** 1,459 LOC with 68 branching statements. Implements the VSAM paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) used across 5+ programs in the estate.

**Complexity Drivers:**
- VSAM browse operations with forward/backward navigation
- Alternate index (AIX) usage for card-by-account lookup
- Complex screen pagination state management via COMMAREA
- 11 copybooks for data and screen structures

**Modernization Recommendation:**  
**Priority 2.** Create a generic `AbstractListController` pattern in Java that handles pagination; reuse across COCRDLIC, COTRN00C, COUSR00C, and COPAUS0C. This one implementation replaces the browse logic in multiple programs.

---

### 5. COCRDUPC.cbl — Credit Card Update (Score: 35.2)

**Why it's #5:** 1,560 LOC with 80 branching statements — the highest branch count after COACTUPC. Validates and updates card records with linked account/customer lookups.

**Complexity Drivers:**
- 80 IF+EVALUATE statements (extensive field validation)
- REWRITE to both CARDFILE and ACCTFILE
- Cross-entity validation (card ↔ account ↔ customer)
- 13 copybooks

**Modernization Recommendation:**  
**Priority 2.** Migrate alongside COCRDLIC as part of the Card Service microservice. Extract validation into shared `CardValidationService`.

---

### 6. COTRTUPC.cbl — Transaction Type Update/Delete (Score: 33.8)

**Why it's #6:** 1,702 LOC of DB2 CRUD operations including cascading deletes across TRAN_TYPE and TRAN_CAT tables.

**Complexity Drivers:**
- INSERT/UPDATE/DELETE with cascading referential integrity
- DB2 error handling with DSNTIAC message formatting
- Input validation and confirmation dialogs via BMS
- ~30 I/O operations (all DB2)

**Modernization Recommendation:**  
**Priority 2.** Migrate as a pair with COTRTLIC. Convert to Spring Data JPA `@Transactional` methods with `CascadeType.ALL`. The DB2 DDL in `CREADB21.jcl` provides the target schema.

---

### 7. COPAUS0C.cbl — Authorization Summary Browse (Score: 32.6)

**Why it's #7:** 1,032 LOC combining CICS screen logic with IMS DL/I traversal. Implements paginated browse of IMS authorization summary segments.

**Complexity Drivers:**
- IMS GU/GNP segment navigation (position-dependent)
- BMS screen with scrollable list
- 14 copybooks (CICS + IMS + VSAM data structures)
- Part of the COPAUS chain (0C→1C→2C) that must migrate as a unit

**Modernization Recommendation:**  
**Priority 5.** Migrate with the authorization chain. The IMS hierarchy maps to 2 relational tables: `auth_summary` (root) and `auth_detail` (child). GNP becomes a simple JOIN query.

---

### 8. CBSTM03A.CBL — Statement Generation (Score: 28.2)

**Why it's #8:** 924 LOC but 118 I/O operations — the highest in the entire estate. Generates text and HTML customer statements by joining data from 4 VSAM files.

**Complexity Drivers:**
- 118 I/O operations (reads across XREF, CUST, ACCT, TRNX; writes to STMT + HTML)
- Calls CBSTM03B submodule for all file I/O
- Dual output format (plain text + HTML)
- Self-contained batch with no CICS — **easiest to prove migration approach**

**Modernization Recommendation:**  
**Priority 1 — Pilot candidate.** Start here to validate the migration methodology. Convert to a Spring Batch job: `ItemReader` for VSAM reads, `ItemProcessor` for business logic, `ItemWriter` with Thymeleaf for HTML output. Being self-contained batch with no CICS dependency makes it ideal for proving the approach without risk to online systems.

---

### 9. CBTRN02C.cbl — Transaction Posting (Score: 26.7)

**Why it's #9:** 731 LOC, 48 branching statements, and 24 I/O operations. This is the core daily transaction posting program that reads daily transactions, validates them, updates account balances, and writes to the master transaction file.

**Complexity Drivers:**
- Multi-file update: reads DALYTRAN, writes TRANSACT, updates ACCTFILE and TCATBALF
- Rejection handling: invalid transactions written to DALYREJS with reason codes
- Balance recalculation on each posted transaction
- 5 VSAM file interactions (R+R/W+R/W+W+W)

**Modernization Recommendation:**  
**Priority 3.** Convert to a Spring Batch `CompositeItemWriter` step. The read-validate-write pattern maps cleanly. Must run in parallel with COBOL version during transition since it modifies ACCTFILE (the most shared dataset).

---

### 10. CBACT04C.cbl — Interest Calculator (Score: 24.3)

**Why it's #10:** 652 LOC, 43 branching statements, 20 I/O operations. Reads transaction category balances, looks up discount group interest rates, calculates interest, updates account balances, and writes interest transactions.

**Complexity Drivers:**
- Financial calculation: interest rate × balance × time period
- Multi-file join: TCATBAL + DISCGRP + XREF + ACCOUNT
- REWRITE to ACCTFILE (balance update) + WRITE to TRANSACT (interest transaction)
- Must produce bit-identical monetary results in Java (BigDecimal mandatory)

**Modernization Recommendation:**  
**Priority 3.** Convert to Spring Batch job. **Critical:** Use `BigDecimal` with `RoundingMode.HALF_UP` for all monetary calculations. Test with production-scale data to verify penny-level accuracy against COBOL output.

---

## Risk Summary Matrix

| Risk Factor | Affected Programs | Severity | Mitigation |
|-------------|-------------------|----------|------------|
| **COACTUPC complexity** (4,236 LOC, 174 branches) | COACTUPC | HIGH | Decompose into 4 components; 10K+ test cases |
| **Multi-subsystem integration** (CICS+IMS+MQ+DB2) | COPAUA0C | HIGH | Migrate as unit with COPAUS chain; phase 4 |
| **VSAM shared data coupling** (ACCTFILE: 19 programs) | All account-touching programs | HIGH | Dual-write with SQS FIFO sync; hourly reconciliation |
| **COPY REPLACING macro pattern** | COACTUPC (CSSETATY ×3) | MEDIUM | Convert to parameterized Java utility classes |
| **IMS DL/I position-dependent navigation** | COPAUA0C, COPAUS0C–2C, CBPAUP0C, PAUDBLOD/UNL | MEDIUM | Map to 2 relational tables; GNP → JOIN |
| **Plain-text passwords** | COUSR00C–03C (CSUSR01Y) | HIGH | Implement bcrypt/Spring Security immediately |
| **Assembler dependencies** | CBACT01C (COBDATFT), COBSWAIT (MVSWAIT) | LOW | Replace with Java `DateTimeFormatter` and `Thread.sleep()` |
| **BigDecimal accuracy** | All monetary calculations | HIGH | Mandatory BigDecimal; never double/float; test overpunch encoding |

---

## Recommended Modernization Sequence

```
Phase 0: Foundation (3-4 weeks)
  └── Set up Spring Boot project, PostgreSQL schema, CI/CD pipeline
      └── Create shared validation library from CSLKPCDY/CSUTLDPY

Phase 1: Pilot + Quick Win (4-5 weeks)
  ├── CBSTM03A/B → Spring Batch statement generation (Pilot — prove approach)
  └── COUSR00C–03C + COSGN00C → Spring Security user service (Quick Win)

Phase 2: DB2 + Card Service (5-7 weeks)
  ├── COTRTLIC + COTRTUPC + COBTUPDT → Transaction Type Service (DB2 → JPA)
  └── COCRDLIC + COCRDSLC + COCRDUPC → Card Service (paginated browse pattern)

Phase 3: Core Business Logic (8-10 weeks)
  ├── COACTUPC + COACTVWC → Account Service (decompose COACTUPC first)
  └── COTRN00C–02C + CBTRN01C–02C + COBIL00C → Transaction Service

Phase 4: Authorization Chain (6-8 weeks)
  └── COPAUA0C + COPAUS0C–2C + CBPAUP0C + PAUDBLOD/UNL → Auth Service
      └── IMS → PostgreSQL, MQ → SQS, migrate as unit

Phase 5: Batch Pipeline (4-5 weeks)
  └── CBTRN02C + CBACT04C + CBTRN03C + CBEXPORT/IMPORT → Spring Batch jobs
      └── Preserve daily pipeline SLA: POSTTRAN → INTCALC → CREASTMT → TRANREPT

Phase 6: Decommission (2-3 weeks)
  └── Remove COBOL sources, decommission VSAM files, retire mainframe
```

**Total estimated effort: 32–42 weeks with a 4-person team.**
