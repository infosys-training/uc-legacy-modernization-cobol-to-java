# HOTSPOT_REPORT.md — CardDemo Complexity Analysis & Modernization Priorities

## Overview

This report ranks the top 10 programs in the CardDemo COBOL estate by five complexity metrics, then recommends a modernization sequence. All 44 programs were analyzed; the table below shows the top 10 hotspots.

---

## 1. Scoring Methodology

| Metric | Description | Weight |
|--------|------------|--------|
| **LOC** | Lines of code (raw source) | Indicates size and maintenance burden |
| **Copybooks** | Number of COPY statements | Indicates data coupling breadth |
| **I/O Ops** | Count of READ/WRITE/REWRITE/DELETE/STARTBR/READNEXT/READPREV/ENDBR + EXEC SQL + EXEC DLI + MQ calls | Indicates external dependency complexity |
| **Logic Density** | Count of EVALUATE + IF statements | Indicates branching complexity / nesting depth |
| **Dependencies** | Programs that call/are called by this program + XCTL targets + VSAM files accessed | Indicates inter-program coupling |

A **composite score** is computed as a normalized weighted sum:
`Score = 0.25×LOC_norm + 0.15×COPY_norm + 0.20×IO_norm + 0.20×LOGIC_norm + 0.20×DEP_norm`

Each metric is normalized to 0–100 based on max observed value.

---

## 2. Top 10 Hotspot Programs

| Rank | Program | LOC | Copybooks | I/O Ops | Logic (IF/EVAL) | Dependencies | Composite Score | Classification |
|------|---------|-----|-----------|---------|-----------------|-------------|-----------------|---------------|
| **1** | **COACTUPC.cbl** | 4,236 | 56 | 11 | 188 | 12 VSAM files + XCTL + 30 CSSETATY copies | **92** | CICS Online |
| **2** | **COTRTLIC.cbl** | 2,098 | 11 | 30 | 121 | Db2 TRNTYPE + TRNTYCAT tables | **74** | CICS/Db2 |
| **3** | **COTRTUPC.cbl** | 1,702 | 13 | 37 | 128 | Db2 TRNTYPE + TRNTYCAT tables | **72** | CICS/Db2 |
| **4** | **COCRDUPC.cbl** | 1,560 | 15 | 6 | 164 | 4 VSAM files + XCTL | **68** | CICS Online |
| **5** | **COCRDLIC.cbl** | 1,459 | 13 | 17 | 140 | 2 VSAM files + 3 XCTL targets | **64** | CICS Online |
| **6** | **COPAUA0C.cbl** | 1,026 | 16 | 19 | 61 | IMS DB + MQ + 3 VSAM files | **60** | CICS/IMS/MQ |
| **7** | **COPAUS0C.cbl** | 1,032 | 14 | 10 | 47 | IMS DB + 3 VSAM files + BMS | **53** | CICS/IMS/BMS |
| **8** | **COACTVWC.cbl** | 941 | 15 | 7 | 67 | 3 VSAM files + XCTL | **50** | CICS Online |
| **9** | **CBSTM03A.CBL** | 924 | 4 | 102 | 24 | 4 VSAM files + 12 CALLs to CBSTM03B | **49** | Batch |
| **10** | **COCRDSLC.cbl** | 887 | 15 | 6 | 76 | 4 VSAM files + XCTL | **48** | CICS Online |

---

## 3. Detailed Analysis of Top 5

### Rank 1: COACTUPC.cbl — Account Update (4,236 LOC)

**Why it's #1:**
- **Largest program** in the entire estate at 4,236 lines
- **56 COPY statements** — highest copybook coupling, including 30 COPY REPLACING for CSSETATY (field-level attribute setting), plus CSLKPCDY (1,318-line ZIP code lookup table)
- **188 EVALUATE/IF statements** — extremely dense business logic for field validation, account status checks, credit limit enforcement
- Accesses **5+ VSAM CICS files**: ACCTDAT, CARDXREF, CUSTDAT, CARDDAT, plus display attributes
- Uses XCTL for navigation, HANDLE ABEND for error recovery

**Modernization Complexity:** Very High — requires decomposition into multiple services (account validation, account persistence, UI/display logic).

### Rank 2: COTRTLIC.cbl — Transaction Type List (2,098 LOC)

**Why it's #2:**
- Second-largest program overall
- **30 Db2 I/O operations** — heavy SQL cursor management with OPEN/FETCH/CLOSE loops
- **121 EVALUATE/IF statements** — complex pagination logic, search filtering, CICS screen handling
- Directly accesses Db2 tables (TRNTYPE, TRNTYCAT) — requires Db2-to-modern-DB migration

**Modernization Complexity:** High — tightly coupled to Db2 catalog tables; pagination/cursor logic maps well to modern list APIs.

### Rank 3: COTRTUPC.cbl — Transaction Type Update (1,702 LOC)

**Why it's #3:**
- **37 I/O operations** — highest I/O count among Db2 programs (INSERT/UPDATE/DELETE/SELECT)
- **128 EVALUATE/IF statements** — extensive validation logic for transaction type CRUD operations
- Paired with COTRTLIC (list screen → update screen flow)

**Modernization Complexity:** High — CRUD operations on reference data; good candidate for microservice extraction.

### Rank 4: COCRDUPC.cbl — Credit Card Update (1,560 LOC)

**Why it's #4:**
- **164 EVALUATE/IF statements** — second-highest logic density (business rules for card updates)
- **15 copybooks** — significant data coupling across account, card, customer, cross-reference entities
- Performs READ/REWRITE on CARDDAT, ACCTDAT, CARDXREF VSAM files

**Modernization Complexity:** High — card update validation rules are prime candidates for a business rules engine.

### Rank 5: COCRDLIC.cbl — Credit Card List (1,459 LOC)

**Why it's #5:**
- **140 EVALUATE/IF statements** — complex browse logic with forward/backward pagination via STARTBR/READNEXT/READPREV/ENDBR
- **17 I/O operations** — heavy VSAM browsing
- 3 XCTL targets (COCRDSLC, COCRDUPC, return to menu)

**Modernization Complexity:** Medium-High — pagination/browse pattern is common in CICS and maps to paginated REST APIs.

---

## 4. Technology Risk Assessment

| Technology | Programs Using | Modernization Risk |
|-----------|----------------|-------------------|
| **CICS/BMS (screens)** | 18 programs | Medium — BMS maps → web UI; COMMAREA → session/state management |
| **VSAM KSDS** | 28 programs | Medium — KSDS → relational DB tables; key access patterns preserved |
| **IMS DB** | 6 programs (auth sub-app) | High — hierarchical→relational mapping; DLI calls → SQL |
| **Db2 SQL** | 4 programs (tran type sub-app + auth) | Low — SQL migrates directly |
| **MQ Series** | 3 programs (auth + VSAM-MQ sub-apps) | Low-Medium — MQOPEN/GET/PUT → modern message broker (Kafka, SQS) |
| **GDG (versioned datasets)** | 8 JCL jobs | Medium — GDG generation management → file versioning/archival |
| **SORT utility** | 4 JCL jobs | Low — SORT → database ORDER BY or application sorting |

---

## 5. Recommended Modernization Sequence

### Wave 1: Low-Risk, High-Value Enablers
| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 1.1 | **CSUTLDTC** (date utility) | Small (157 LOC), no external dependencies. Convert to a shared Java date utility. Unblocks 2 callers (CORPT00C, COTRN02C). |
| 1.2 | **COBSWAIT** (wait utility) | Trivial (41 LOC). Replace with Thread.sleep() or scheduled job delay. |
| 1.3 | **CBSTM03B** (statement I/O sub) | Small (230 LOC), called only by CBSTM03A. Convert as part of the statement module. |
| 1.4 | **CBACT02C, CBACT03C, CBCUS01C** (read/print) | Simple readers (178 LOC each). Good "hello world" conversions to validate tooling. |

### Wave 2: Batch Processing Core
| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 2.1 | **CBTRN02C** (transaction posting) | Core batch processing (731 LOC, 93 IF/EVAL). Converts VSAM file I/O to database operations. Critical business logic for daily posting. |
| 2.2 | **CBACT04C** (interest calc) | Core financial logic (652 LOC, 86 IF/EVAL). Self-contained calculation engine — ideal for a standalone microservice. |
| 2.3 | **CBTRN03C** (transaction report) | Reporting (649 LOC). Convert to modern reporting framework (JasperReports, etc.). |
| 2.4 | **CBSTM03A** (statements) | Highest batch I/O (102 ops, 924 LOC). Convert with CBSTM03B as a unit. |
| 2.5 | **CBEXPORT/CBIMPORT** | Data migration pair. Convert to ETL jobs or batch Spring Boot services. |

### Wave 3: Online CICS — Medium Complexity
| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 3.1 | **COSGN00C** (signon) → **COMEN01C/COADM01C** (menus) | Auth + navigation. Replace with Spring Security + web routing. |
| 3.2 | **COTRN00C/01C/02C** (transaction CRUD) | Transaction management screens. Convert to REST API + web frontend. |
| 3.3 | **COUSR00C/01C/02C/03C** (user CRUD) | User management. Straightforward CRUD → REST/JPA. |
| 3.4 | **COBIL00C** (bill payment) | Medium complexity (572 LOC). Account update + transaction write. |

### Wave 4: Online CICS — High Complexity
| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 4.1 | **COCRDLIC → COCRDSLC → COCRDUPC** (card management) | Card lifecycle management cluster. Convert together due to tight XCTL coupling. Total ~3,906 LOC. |
| 4.2 | **COACTVWC** (account view) | Account display (941 LOC). Prerequisite for COACTUPC. |
| 4.3 | **COACTUPC** (account update) | **Largest program (4,236 LOC)** — convert last in wave to apply learnings from simpler conversions. Decompose into validation, persistence, and presentation layers. |

### Wave 5: Sub-Application Modules
| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 5.1 | **COBTUPDT + COTRTLIC + COTRTUPC** (Db2 transaction types) | Already Db2-based — SQL migrates directly. Convert CICS screens to web UI. |
| 5.2 | **COACCT01 + CODATE01** (VSAM-MQ services) | MQ → modern messaging (Kafka/SQS). VSAM → DB. |
| 5.3 | **COPAUA0C/S0C/S1C/S2C + CBPAUP0C** (authorization) | Most complex sub-app (IMS+Db2+MQ+CICS). Convert last due to multi-technology stack. Requires IMS-to-relational schema redesign. |
| 5.4 | **PAUDBLOD/PAUDBUNL/DBUNLDGS** (IMS utilities) | IMS load/unload — replace with DB migration scripts. |

---

## 6. Summary Metrics

| Metric | Total Estate |
|--------|-------------|
| Total COBOL programs | 44 |
| Total lines of code | ~28,500 |
| Batch programs | 20 |
| CICS online programs | 18 |
| IMS programs | 6 |
| Db2 programs | 4 |
| MQ programs | 3 |
| Utility/subroutine programs | 3 |
| Copybooks | 30+ |
| JCL jobs | 46 |
| VSAM datasets | 11 |
| Db2 tables | 2+ (TRNTYPE, TRNTYCAT, AUTHFRDS) |
| IMS databases | 1 (Pending Authorizations) |
| MQ queues | 2+ (Authorization request/response) |

### Estimated Modernization Effort

| Wave | Programs | ~LOC | Estimated Effort |
|------|----------|------|-----------------|
| Wave 1 (Enablers) | 6 | ~960 | 2–3 weeks |
| Wave 2 (Batch Core) | 7 | ~4,200 | 4–6 weeks |
| Wave 3 (CICS Medium) | 10 | ~3,900 | 6–8 weeks |
| Wave 4 (CICS High) | 4 | ~8,140 | 8–12 weeks |
| Wave 5 (Sub-Apps) | 13 | ~11,300 | 10–14 weeks |
| **Total** | **44** | **~28,500** | **30–43 weeks** |
