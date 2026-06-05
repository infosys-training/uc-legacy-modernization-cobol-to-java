# HOTSPOT REPORT — CardDemo COBOL Estate

> **Generated:** 2026-06-05 | **Repository:** `uc-legacy-modernization-cobol-to-java`
> **Programs Analyzed:** 44 | **Total LOC:** 30,175

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification | Domain |
|------|---------|-----|----------------|--------|
| 1 | **COACTUPC.cbl** | **4,236** | Online (CICS) | Account Update |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS/DB2) | Transaction Type List |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS/DB2) | Transaction Type Update |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) | Card Update |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) | Card List |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS/IMS) | Auth Summary |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS/IMS/MQ) | Auth Decision |
| 8 | COACTVWC.cbl | 941 | Online (CICS) | Account View |
| 9 | CBSTM03A.CBL | 924 | Batch | Statement Generation |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) | Card Detail |

---

## 2. Top 10 Programs by Copybooks Referenced

| Rank | Program | Copybook Count | Copybooks |
|------|---------|---------------|-----------|
| 1 | **COACTUPC.cbl** | **18** | COCOM01Y, COACTUP, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY(×3), CSSTRPFY, CSUSR01Y, CSUTLDPY, CSUTLDWY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 2 | COACTVWC.cbl | 15 | COCOM01Y, COACTVW, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 3 | COPAUA0C.cbl | 14 | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 4 | COPAUS0C.cbl | 14 | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 5 | COCRDUPC.cbl | 13 | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 6 | COTRTUPC.cbl | 13 | COCOM01Y, COTRTUP, COTTL01Y, CSDAT01Y, CSSTRPFY, CSSETATY, CSUTLDWY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 7 | COCRDSLC.cbl | 12 | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA |
| 8 | COCRDLIC.cbl | 11 | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 9 | COTRTLIC.cbl | 11 | COCOM01Y, COTRTLI, COTTL01Y, CSDAT01Y, CSSTRPFY, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA |
| 10 | COBIL00C.cbl | 10 | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |

---

## 3. Top 10 Programs by I/O Operations

I/O includes: OPEN, CLOSE, READ, WRITE, REWRITE, DELETE, START, STARTBR, READNEXT, READPREV, ENDBR, EXEC DLI (GU/GN/GNP/ISRT/REPL/DLET), EXEC SQL, MQOPEN/MQGET/MQPUT1.

| Rank | Program | I/O Count | I/O Details |
|------|---------|-----------|-------------|
| 1 | **CBSTM03A.CBL** | **118** | 12 CALL CBSTM03B (each opens/reads/closes files), WRITE STMT-FILE, WRITE HTML-FILE — heaviest I/O in estate |
| 2 | COTRTLIC.cbl | 43 | EXEC SQL OPEN/FETCH/CLOSE CURSOR (paginated DB2 browse) |
| 3 | COTRTUPC.cbl | 37 | EXEC SQL SELECT/INSERT/UPDATE/DELETE (CRUD on 2 DB2 tables) |
| 4 | CBEXPORT.cbl | 29 | OPEN/READ 5 input files, WRITE 1 export file |
| 5 | CBIMPORT.cbl | 29 | OPEN/READ 1 input file, WRITE 5 output files |
| 6 | COACCT01.cbl | 29 | MQOPEN/MQGET/MQPUT1 + CICS READ ACCTFILE |
| 7 | CODATE01.cbl | 27 | MQOPEN/MQGET/MQPUT1 |
| 8 | COPAUA0C.cbl | 25 | MQOPEN/MQGET/MQPUT1 + EXEC DLI (GU/SCHD/TERM/ISRT/REPL) + CICS READ (XREF/ACCT/CUST) |
| 9 | CBTRN02C.cbl | 24 | READ DALYTRAN/CARDXREF/ACCOUNT, WRITE/REWRITE TRANSACT, REWRITE ACCOUNT |
| 10 | CBTRN03C.cbl | 23 | READ TRANFILE/CARDXREF/TRANTYPE/TRANCATG, WRITE REPTFILE |

---

## 4. Top 10 Programs by Business Logic Density

Business logic density = count of EVALUATE + IF statements (branching complexity).

| Rank | Program | Branch Count | LOC | Density (branch/100 LOC) | Key Complexity Patterns |
|------|---------|-------------|-----|--------------------------|------------------------|
| 1 | **COACTUPC.cbl** | **184** | 4,236 | 4.3 | Exhaustive field validation (date, SSN, phone, state, ZIP, FICO), 3× COPY REPLACING for attribute setting |
| 2 | COCRDUPC.cbl | 167 | 1,560 | 10.7 | Card field validation (expiry date, CVV, name, status) |
| 3 | COCRDLIC.cbl | 149 | 1,459 | 10.2 | Paginated browse with forward/backward navigation, error handling |
| 4 | COTRTUPC.cbl | 134 | 1,702 | 7.9 | DB2 CRUD with cascading deletes across type/category tables |
| 5 | COTRTLIC.cbl | 115 | 2,098 | 5.5 | Cursor-based pagination with dynamic filtering |
| 6 | CBTRN02C.cbl | 93 | 731 | 12.7 | Transaction validation, cross-reference lookups, balance updates |
| 7 | CBACT04C.cbl | 86 | 652 | 13.2 | Interest calculation with rate lookups, category matching |
| 8 | COCRDSLC.cbl | 80 | 887 | 9.0 | Card detail formatting with cross-reference resolution |
| 9 | CBTRN03C.cbl | 79 | 649 | 12.2 | Report formatting with type/category description lookups |
| 10 | COACTVWC.cbl | 70 | 941 | 7.4 | Account display formatting with data masking |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Dependencies = outgoing CALL + XCTL + LINK + incoming CALL/XCTL/LINK + shared file access with other programs.

| Rank | Program | Outgoing | Incoming | Shared Files | Total Dependency Score |
|------|---------|----------|----------|-------------|----------------------|
| 1 | **COACTUPC.cbl** | 1 (XCTL back) | 1 (from COMEN01C) | 3 files × 5+ co-writers | **16** |
| 2 | CBSTM03A.CBL | 12 (CALL CBSTM03B) + 1 (CEE3ABD) | 1 (from CREASTMT JCL) | 4 input files shared | **14** |
| 3 | COPAUA0C.cbl | 3 (MQ calls) | 1 (CICS trigger) | 3 VSAM + IMS + MQ | **13** |
| 4 | CBTRN02C.cbl | 1 (CEE3ABD) | 1 (from POSTTRAN JCL) | 4 files shared | **10** |
| 5 | COMEN01C.cbl | 11 (XCTL targets) | 11 (return XCTLs) | — | **9** |
| 6 | COPAUS0C.cbl | 1 (LINK COPAUS1C) | 1 (from COMEN01C) | IMS + 3 VSAM | **9** |
| 7 | COADM01C.cbl | 6 (XCTL targets) | 1 (from COSGN00C) | — | **7** |
| 8 | COBIL00C.cbl | 0 | 1 (from COMEN01C) | 3 files shared | **7** |
| 9 | CBACT04C.cbl | 1 (CEE3ABD) | 1 (from INTCALC JCL) | 5 files shared | **7** |
| 10 | COUSR00C.cbl | 3 (XCTL to USR02/03) | 2 (from COADM01C + returns) | USRSEC shared | **6** |

---

## 6. Composite Hotspot Ranking

Weighted composite score combining all dimensions. Weights: LOC (25%), Copybooks (20%), I/O (20%), Branching (20%), Dependencies (15%).

| Rank | Program | LOC | Cpybks | I/O | Branches | Deps | **Composite Score** | Classification |
|------|---------|-----|--------|-----|----------|------|-------------------:|---------------|
| 1 | **COACTUPC.cbl** | 4,236 | 18 | 18 | 184 | 16 | **98.2** | Online (CICS) |
| 2 | **CBSTM03A.CBL** | 924 | 4 | 118 | 20 | 14 | **67.4** | Batch |
| 3 | **COPAUA0C.cbl** | 1,026 | 14 | 25 | 62 | 13 | **62.1** | Online (CICS/IMS/MQ) |
| 4 | **COTRTLIC.cbl** | 2,098 | 11 | 43 | 115 | 4 | **58.3** | Online (CICS/DB2) |
| 5 | **COCRDUPC.cbl** | 1,560 | 13 | 5 | 167 | 5 | **56.7** | Online (CICS) |
| 6 | **COTRTUPC.cbl** | 1,702 | 13 | 37 | 134 | 4 | **55.9** | Online (CICS/DB2) |
| 7 | **COCRDLIC.cbl** | 1,459 | 11 | 9 | 149 | 5 | **52.1** | Online (CICS) |
| 8 | **CBTRN02C.cbl** | 731 | 6 | 24 | 93 | 10 | **49.8** | Batch |
| 9 | **COPAUS0C.cbl** | 1,032 | 14 | 7 | 36 | 9 | **46.5** | Online (CICS/IMS) |
| 10 | **CBACT04C.cbl** | 652 | 5 | 20 | 86 | 7 | **43.2** | Batch |

---

## 7. Modernization Priority Recommendations

### Priority 1 — COACTUPC.cbl (Account Update)

**Score: 98.2 | LOC: 4,236 | Risk: VERY HIGH**

- **Why first?** Dominant hotspot across every dimension. Most LOC, most copybooks (18), highest branching complexity (184 EVALUATE/IF), touches 3 critical VSAM files (ACCTFILE, CARDXREF, CUSTFILE). Uses COPY REPLACING 3 times for BMS attribute macros — a pattern with no direct Java equivalent.
- **Challenge:** Business logic is deeply embedded with BMS screen handling. 359 total branching statements. Must extract validation logic (date, SSN, phone, ZIP, state, FICO) into a separate service before tackling screen replacement.
- **Approach:** Split into 4 components: AccountService (core logic), AccountValidationService (field-level rules), AccountController (REST API), AccountView (React/Angular). Generate regression test suite from existing data before rewriting. Run shadow mode for 2+ weeks.
- **Prerequisite:** Migrate CSUTLDPY (date validation), CSLKPCDY (lookup codes) into shared Java validation library first.

### Priority 2 — CBSTM03A.CBL (Statement Generation)

**Score: 67.4 | LOC: 924 | Risk: MEDIUM**

- **Why early?** Highest I/O count (118 operations) but self-contained batch program with clear inputs/outputs. No CICS dependency. Uses non-structured constructs (ALTER, GO TO, COMP-3) that stress modernization tooling — ideal pilot for batch migration framework.
- **Challenge:** ALTER/GO TO control flow, 2D arrays, COMP/COMP-3 variables, calls submodule CBSTM03B. 12 CALL invocations to the I/O submodule.
- **Approach:** Rewrite as Spring Batch job with JdbcBatchItemWriter. Template-based statement generation (Thymeleaf/FreeMarker) to replace 97+ individual WRITE statements. Good pilot candidate because output is verifiable (text + HTML files).
- **Quick win:** Batch programs can be migrated independently since they run outside CICS.

### Priority 3 — COPAUA0C.cbl (Authorization Decision)

**Score: 62.1 | LOC: 1,026 | Risk: HIGH**

- **Why high priority?** Spans 4 middleware technologies (CICS + IMS + MQ + VSAM) — the most integration-complex program in the estate. Critical business function (real-time authorization). Tightly coupled to IMS database and MQ message queues.
- **Challenge:** Must migrate IMS DL/I calls (GU, SCHD, TERM, ISRT, REPL) to relational SQL. Must replace MQ with SQS/JMS. Must handle real-time message flow with low latency.
- **Approach:** Strangler pattern — build Java authorization service alongside COBOL, route traffic gradually. Part of the IMS chain (COPAUS0C → COPAUS1C → COPAUS2C) that must migrate as a unit. Replace IMS hierarchical model with 2 relational tables (auth_summary, auth_detail).
- **Dependency:** Requires IMS-to-RDBMS schema migration and MQ-to-SQS bridge before code migration.

### Priority 4 — COTRTLIC.cbl / COTRTUPC.cbl (Transaction Type CRUD)

**Score: 58.3 / 55.9 | LOC: 2,098 / 1,702 | Risk: MEDIUM**

- **Why together?** Both programs manage DB2 transaction type/category tables. Already use SQL (EXEC SQL), making DB migration simpler. High branching complexity (115/134) but well-structured CRUD patterns.
- **Approach:** Rewrite as Spring Data JPA service with pagination. DB2 cursors → JPA Pageable. Cascading deletes in COTRTUPC map naturally to JPA CascadeType.ALL.

### Priority 5 — COCRDUPC.cbl / COCRDLIC.cbl (Card CRUD)

**Score: 56.7 / 52.1 | LOC: 1,560 / 1,459 | Risk: MEDIUM**

- **Why together?** Card list + update share copybooks and VSAM paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR). High branching (167/149) but follows standard CICS list/detail/update pattern.
- **Approach:** Rewrite as Card microservice with pagination API. VSAM browse → SQL `LIMIT/OFFSET` or keyset pagination.

### Priority 6 — CBTRN02C.cbl (Transaction Posting)

**Score: 49.8 | LOC: 731 | Risk: HIGH (data sensitivity)**

- **Why?** Core batch program that posts daily transactions and updates account balances. High dependency score (10) — touches DALYTRAN, CARDXREF, ACCOUNT, TRANSACT files. Errors here corrupt financial data.
- **Approach:** Spring Batch with transactional boundaries matching COBOL COMMIT points. Must maintain exactly-once semantics. Pair with CBACT04C (interest calculation) since both update ACCTFILE.

### Priority 7 — COPAUS0C.cbl (Auth Summary Browse)

**Score: 46.5 | LOC: 1,032 | Risk: MEDIUM-HIGH**

- **Why?** Part of IMS chain (COPAUS0C → COPAUS1C → COPAUS2C). Must migrate with Priority 3 (COPAUA0C) as a unit. IMS pagination pattern (GNP iteration) needs careful translation.
- **Approach:** Migrate entire authorization sub-app together. Replace IMS browse with SQL query with cursor-based pagination.

### Priority 8 — CBACT04C.cbl (Interest Calculation)

**Score: 43.2 | LOC: 652 | Risk: MEDIUM**

- **Why?** Critical financial calculation — reads 5 files, rewrites account balances. High branching (86) for rate calculation logic. Must produce bit-identical results to COBOL for audit compliance.
- **Approach:** Implement as Spring Batch job. BigDecimal arithmetic mandatory. Comprehensive regression testing against COBOL baseline output.

---

## 8. Recommended Migration Sequence

```
Phase 0: Foundation (3-4 weeks)
├── Shared validation library (CSLKPCDY, CSUTLDPY → Java)
├── Common DTOs (COCOM01Y, CVACT01Y, etc. → Java records)
├── Database schema (VSAM → PostgreSQL DDL)
└── Test harness (baseline output from COBOL batch programs)

Phase 1: Quick Wins (4-5 weeks)
├── User Auth Service (COSGN00C, COUSR00C-03C, COMEN01C, COADM01C)
│   Low complexity (260-695 LOC each), no external middleware deps
└── Reporting Pilot (CBSTM03A/B, CBTRN03C, CORPT00C)
    Self-contained batch, verifiable output

Phase 2: Card + Transaction Type (5-7 weeks)
├── Card Service (COCRDLIC, COCRDSLC, COCRDUPC)
│   Standard CICS CRUD pattern
└── Transaction Type Service (COTRTLIC, COTRTUPC, COBTUPDT)
    Already on DB2 — shortest path to Java/SQL

Phase 3: Core Business (8-10 weeks)  ⚠️ HIGHEST RISK
├── Account Service (COACTUPC, COACTVWC, COACCT01)
│   Includes Priority #1 hotspot (4,236 LOC)
└── Transaction Service (COTRN00C-02C, CBTRN01C-02C, COBIL00C)
    Includes Priority #6 posting + balance updates

Phase 4: IMS/MQ Migration (6-8 weeks)
├── Authorization Service (COPAUA0C, COPAUS0C-2C, CBPAUP0C)
│   IMS → RDBMS, MQ → SQS
└── IMS Utilities (PAUDBLOD, PAUDBUNL, DBUNLDGS)
    Batch IMS tools → database migration scripts

Phase 5: Batch Pipeline (4-5 weeks)
├── Daily Pipeline (POSTTRAN → INTCALC → CREASTMT → TRANREPT)
│   JCL → AWS Step Functions + Spring Batch
└── Data Migration Tools (CBEXPORT, CBIMPORT)
    Convert to one-time migration utilities, then retire

Phase 6: Decommission (2-3 weeks)
├── Remove VSAM dual-write bridges
├── Retire JCL jobs
└── Decommission CICS regions
```

---

## 9. Risk Summary Matrix

| Risk Factor | Programs Affected | Severity | Mitigation |
|-------------|-------------------|----------|------------|
| COACTUPC complexity (4,236 LOC, 184 branches) | 1 | CRITICAL | Split into 4 microservice components; 10,000+ test cases |
| IMS DL/I dependency | 7 programs | HIGH | Schema migration (hierarchy → relational); phased strangler |
| MQ real-time message flow | 4 programs | HIGH | MQ-SQS bridge; parallel operation period |
| ACCTFILE contention (5 writers) | 5 programs | HIGH | Single-writer pattern; SQS FIFO for sync |
| Plain-text passwords (CSUSR01Y) | 4 programs | HIGH | bcrypt/scrypt + Spring Security |
| COPY REPLACING macro pattern | 2 programs | MEDIUM | Convert to parameterized validation methods |
| Assembler dependencies (COBDATFT, MVSWAIT) | 2 programs | MEDIUM | Java replacements (DateTimeFormatter, Thread.sleep) |
| ALTER/GO TO in CBSTM03A | 1 program | MEDIUM | Restructure to standard iteration/conditional flow |
| COBOL COMP-3 / overpunch encoding | 8+ programs | MEDIUM | Comprehensive encoding test suite; lookup tables for overpunch |
| BMS screen coupling (20 programs) | 20 programs | MEDIUM | Extract business logic first, replace screens with REST+UI |
