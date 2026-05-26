# Hotspot Report — CardDemo COBOL Estate

## Overview

This report ranks the top 10 programs across the CardDemo COBOL estate by complexity metrics: lines of code, copybook references, I/O operations, business logic density (EVALUATE/IF nesting depth), and inter-program dependencies. Programs are scored using a weighted composite and recommended for modernization priority.

**Scoring methodology:**
| Metric | Weight | Rationale |
|--------|--------|-----------|
| Lines of Code | 25% | Larger programs = more migration effort |
| Copybooks Referenced | 15% | More data coupling = higher integration risk |
| I/O Operations | 20% | More data access = more persistence logic to map |
| Logic Density (max nesting depth) | 25% | Deep nesting = hardest to refactor accurately |
| Inter-Program Dependencies | 15% | More connections = higher regression risk |

---

## Top 10 Programs by Composite Score

| Rank | Program | Module | LOC | Copybooks | I/O Ops | Max Nesting | Dependencies* | Composite Score |
|------|---------|--------|-----|-----------|---------|-------------|---------------|-----------------|
| **1** | **COACTUPC.cbl** | Core (CICS) | 4,236 | 56 | 20 | 5 | 7 callers | **96** |
| **2** | **COTRTLIC.cbl** | Trans-Type-DB2 (CICS) | 2,098 | 11 | 16 | 18 | 2 callers | **89** |
| **3** | **COTRTUPC.cbl** | Trans-Type-DB2 (CICS) | 1,702 | 13 | 9 | 72 | 2 callers | **87** |
| **4** | **COCRDUPC.cbl** | Core (CICS) | 1,560 | 15 | 5 | 6 | 3 callers | **72** |
| **5** | **COCRDLIC.cbl** | Core (CICS) | 1,459 | 13 | 8 | 6 | 3 callers | **69** |
| **6** | **COPAUS0C.cbl** | Auth-IMS (CICS) | 1,032 | 14 | 4 | 4 | 2 callers | **62** |
| **7** | **COPAUA0C.cbl** | Auth-IMS (CICS/MQ) | 1,026 | 16 | 11 | 3 | 1 caller | **61** |
| **8** | **COACTVWC.cbl** | Core (CICS) | 941 | 15 | 4 | 2 | 2 callers | **55** |
| **9** | **CBSTM03A.CBL** | Core (Batch) | 924 | 4 | 97 | 3 | 0 | **54** |
| **10** | **COCRDSLC.cbl** | Core (CICS) | 887 | 15 | 4 | 5 | 2 callers | **51** |

\* Dependencies = number of programs that call/XCTL to this program + programs this program calls/XCTLs to.

---

## Detailed Program Analysis

### 1. COACTUPC.cbl — Account Update (Rank #1, Score: 96)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 4,236 | **Largest in entire estate** |
| Copybooks | 56 | **Highest** — references nearly every shared copybook |
| I/O Operations | 20 CICS READ/WRITE/REWRITE | High |
| Max Nesting Depth | 5 levels (EVALUATE + nested IF) | Moderate |
| EVALUATE Statements | 20 | Highest in core module |
| Dependencies | XCTL from 6 screens + XCTL to COMEN01C | Highest |

**Why modernize first:**
- At 4,236 lines, this is nearly 3× the next-largest core program — a single monolithic module handling all account CRUD operations, field validation, screen formatting, and error handling.
- References 56 copybooks including all account/card structures, both BMS maps, all validation libraries (phone area codes, state codes, date validation), and message structures.
- Contains the most complex business logic: credit limit changes, balance recalculations, account status transitions, expiration date management.
- Every account-related screen depends on this program — a change here ripples to the entire account management subsystem.

**Recommended approach:** Decompose into microservices: AccountReadService, AccountUpdateService, AccountValidationService. Extract validation rules into a shared library.

---

### 2. COTRTLIC.cbl — Transaction Type List (Rank #2, Score: 89)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 2,098 | 2nd largest |
| Copybooks | 11 | Moderate |
| I/O Operations | 16 (DB2 cursor operations) | High |
| Max Nesting Depth | 18 levels | **Very High** |
| EVALUATE Statements | 32 | **Highest in entire estate** |
| Dependencies | XCTL from COADM01C, XCTL to COTRTUPC | Low |

**Why modernize early:**
- 32 EVALUATE statements and 18-level nesting depth indicate extremely complex screen navigation logic with multiple filter/sort/page states.
- DB2 cursor management with forward/backward scrolling, inline editing, and multi-row updates.
- High business value: transaction type configuration drives interest calculation and reporting across the entire system.

**Recommended approach:** Replace with a REST API + React data grid. DB2 cursors map naturally to paginated SQL queries.

---

### 3. COTRTUPC.cbl — Transaction Type Update (Rank #3, Score: 87)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 1,702 | 3rd largest |
| Copybooks | 13 | Moderate |
| I/O Operations | 9 (DB2 CRUD) | Moderate |
| Max Nesting Depth | 72 levels | **Highest in entire estate** |
| EVALUATE Statements | 26 | Very High |
| Dependencies | XCTL from COTRTLIC | Low |

**Why modernize early:**
- The 72-level nesting depth is the highest in the entire estate, indicating deeply nested conditional logic for multi-mode screen handling (add/update/delete/confirm states).
- DB2 INSERT/UPDATE/DELETE with transaction integrity management.
- Paired with COTRTLIC — these two programs should be modernized together as a single DB2 CRUD module.

**Recommended approach:** Single Spring Data JPA entity with standard CRUD controller. The deeply nested state machine maps to a simple form with separate HTTP methods.

---

### 4. COCRDUPC.cbl — Credit Card Update (Rank #4, Score: 72)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 1,560 | 4th largest |
| Copybooks | 15 | High |
| I/O Operations | 5 CICS READ/REWRITE | Moderate |
| Max Nesting Depth | 6 | Moderate |
| EVALUATE Statements | 16 | High |
| Dependencies | XCTL from COCRDLIC, XCTL to COMEN01C | Moderate |

**Why modernize:**
- Card data update with field-level validation (card number, CVV, expiration, embossed name).
- Tightly coupled to card list (COCRDLIC) and account data (reads ACCTDAT for validation).
- Contains credit card security-sensitive fields — modernization enables better encryption at rest.

**Recommended approach:** Modernize with COCRDLIC as a paired card management service.

---

### 5. COCRDLIC.cbl — Credit Card List (Rank #5, Score: 69)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 1,459 | 5th largest |
| Copybooks | 13 | Moderate |
| I/O Operations | 8 (CICS BROWSE with AIX) | Moderate |
| Max Nesting Depth | 6 | Moderate |
| EVALUATE Statements | 18 | High |
| Dependencies | XCTL to COCRDUPC/COCRDSLC, from COMEN01C | High |

**Why modernize:**
- Uses VSAM Alternate Index (AIX) for account-based card lookup — most complex VSAM access pattern in the estate.
- Paginated browse with forward/backward navigation.
- Central hub for card management: routes to both card update and card view.

**Recommended approach:** REST API with JPA relationship mapping. AIX access pattern becomes a simple `findByAccountId()` query.

---

### 6. COPAUS0C.cbl — Pending Authorization Summary (Rank #6, Score: 62)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 1,032 | Large |
| Copybooks | 14 | High |
| I/O Operations | 4 (DB2 + VSAM) | Low |
| Max Nesting Depth | 4 | Moderate |
| EVALUATE Statements | 22 | High |
| Dependencies | XCTL to COPAUS1C, from COMEN01C | Low |

**Why modernize:**
- Bridges two data sources: DB2 (AUTHFRDS view) and VSAM (account/card lookup).
- Part of the authorization subsystem — high business criticality for fraud prevention.
- IMS/DB2/VSAM hybrid access is the most complex integration pattern to maintain.

**Recommended approach:** Unify data access behind a single JPA repository. Replace IMS segment navigation with relational joins.

---

### 7. COPAUA0C.cbl — Authorization Async Processor (Rank #7, Score: 61)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 1,026 | Large |
| Copybooks | 16 | **Highest among auth programs** |
| I/O Operations | 11 (MQ + VSAM) | High |
| Max Nesting Depth | 3 | Low |
| EVALUATE Statements | 10 | Moderate |
| Dependencies | MQ-driven (no direct program callers) | Isolated |

**Why modernize:**
- MQ message processing is a natural fit for event-driven microservices.
- Contains fraud scoring logic that would benefit from ML integration.
- Currently the only asynchronous processing pattern — modernization enables scaling.

**Recommended approach:** Spring Boot with JMS/Kafka listener. Fraud scoring extracted to a pluggable strategy pattern.

---

### 8. COACTVWC.cbl — Account View (Rank #8, Score: 55)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 941 | Moderate |
| Copybooks | 15 | High |
| I/O Operations | 4 CICS READ | Low |
| Max Nesting Depth | 2 | Low |
| EVALUATE Statements | 10 | Moderate |
| Dependencies | XCTL from COMEN01C | Low |

**Why modernize:**
- Read-only screen with relatively simple logic — good candidate for early modernization to build team confidence.
- Shares data model with COACTUPC — modernize view first, then tackle update.
- Can be converted to a simple REST GET endpoint with minimal risk.

**Recommended approach:** Thin read-only REST endpoint. Can serve as a template for other view screens.

---

### 9. CBSTM03A.CBL — Statement Generation (Rank #9, Score: 54)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 924 | Moderate |
| Copybooks | 4 | Low |
| I/O Operations | 97 | **Highest in entire estate** |
| Max Nesting Depth | 3 | Low |
| EVALUATE Statements | 9 | Moderate |
| Dependencies | Calls CBSTM03B (only inter-program batch call) |

**Why modernize:**
- 97 I/O operations — the most I/O-intensive program by far. Reads three VSAM files and writes formatted statement pages.
- The only batch program with an inter-program CALL (CBSTM03A→CBSTM03B), creating a natural module boundary.
- Statement generation is a high-visibility business function — customers see the output directly.

**Recommended approach:** Replace with a template engine (Thymeleaf/Jasper). CBSTM03B page formatting logic maps to a PDF template.

---

### 10. COCRDSLC.cbl — Credit Card View (Rank #10, Score: 51)

| Metric | Value | Percentile |
|--------|-------|-----------|
| Lines of Code | 887 | Moderate |
| Copybooks | 15 | High |
| I/O Operations | 4 CICS READ | Low |
| Max Nesting Depth | 5 | Moderate |
| EVALUATE Statements | 8 | Moderate |
| Dependencies | XCTL from COCRDLIC | Low |

**Why modernize:**
- Read-only card detail view — low risk, complements card list modernization.
- References same copybook set as COCRDLIC and COCRDUPC — modernize the trio together.

**Recommended approach:** REST GET endpoint for card detail. Part of the card management service with COCRDLIC/COCRDUPC.

---

## Modernization Priority Roadmap

### Wave 1 — Quick Wins (Low Risk, High Learning)
| Program | Effort | Rationale |
|---------|--------|-----------|
| COACTVWC | Low | Read-only, simple logic, builds team confidence |
| COCRDSLC | Low | Read-only, template for other views |
| CSUTLDTC | Low | Utility — maps to `java.time` |

### Wave 2 — Core Business Logic (Medium Risk, High Value)
| Program | Effort | Rationale |
|---------|--------|-----------|
| COCRDUPC + COCRDLIC | Medium | Paired card CRUD — self-contained subsystem |
| COTRN00C + COTRN01C + COTRN02C | Medium | Transaction screens — core business flow |
| CBSTM03A + CBSTM03B | Medium | Statement generation — high customer visibility |

### Wave 3 — Complex Programs (High Risk, High Value)
| Program | Effort | Rationale |
|---------|--------|-----------|
| COACTUPC | High | Largest program, most dependencies — defer until patterns established |
| COTRTLIC + COTRTUPC | High | DB2 programs — deepest nesting, requires DB migration |
| COPAUS0C + COPAUA0C + COPAUS1C | High | IMS/MQ/DB2 hybrid — most complex integration |

### Wave 4 — Batch Pipeline (Medium Risk, Infrastructure)
| Program | Effort | Rationale |
|---------|--------|-----------|
| CBTRN01C + CBTRN02C + CBTRN03C | Medium | Daily transaction pipeline |
| CBACT04C | Medium | Interest calculation — core financial logic |
| CBEXPORT + CBIMPORT | Low | Data migration utilities |

---

## Full Metrics Table (All 44 Programs)

| Program | Module | Type | LOC | Copybooks | I/O Ops | Nesting | EVALUATEs | CALLs/XCTLs |
|---------|--------|------|-----|-----------|---------|---------|-----------|-------------|
| COACTUPC | Core | CICS | 4,236 | 56 | 20 | 5 | 20 | 1 |
| COTRTLIC | DB2 | CICS | 2,098 | 11 | 16 | 18 | 32 | 2 |
| COTRTUPC | DB2 | CICS | 1,702 | 13 | 9 | 72 | 26 | 1 |
| COCRDUPC | Core | CICS | 1,560 | 15 | 5 | 6 | 16 | 1 |
| COCRDLIC | Core | CICS | 1,459 | 13 | 8 | 6 | 18 | 3 |
| COPAUS0C | Auth | CICS | 1,032 | 14 | 4 | 4 | 22 | 0 |
| COPAUA0C | Auth | CICS/MQ | 1,026 | 16 | 11 | 3 | 10 | 4 |
| COACTVWC | Core | CICS | 941 | 15 | 4 | 2 | 10 | 1 |
| CBSTM03A | Core | Batch | 924 | 4 | 97 | 3 | 9 | 14 |
| COCRDSLC | Core | CICS | 887 | 15 | 4 | 5 | 8 | 1 |
| COTRN02C | Core | CICS | 783 | 10 | 6 | 4 | 26 | 2 |
| CBTRN02C | Core | Batch | 731 | 5 | 21 | 3 | 0 | 1 |
| COTRN00C | Core | CICS | 699 | 8 | 4 | 4 | 16 | 0 |
| COUSR00C | Core | CICS | 695 | 8 | 4 | 4 | 16 | 0 |
| CBACT04C | Core | Batch | 652 | 5 | 17 | 4 | 0 | 1 |
| CBTRN03C | Core | Batch | 649 | 5 | 19 | 4 | 4 | 1 |
| CORPT00C | Core | CICS | 649 | 8 | 2 | 3 | 10 | 2 |
| COACCT01 | VSAM-MQ | CICS/MQ | 620 | 9 | 4 | 8 | 18 | 9 |
| COPAUS1C | Auth | CICS | 604 | 10 | 0 | — | 10 | 0 |
| CBEXPORT | Core | Batch | 582 | 6 | 29 | 1 | 0 | 1 |
| COBIL00C | Core | CICS | 572 | 10 | 7 | 4 | 18 | 0 |
| CODATE01 | VSAM-MQ | CICS/MQ | 524 | 8 | 3 | 6 | 16 | 9 |
| CBTRN01C | Core | Batch | 494 | 6 | 17 | 3 | 0 | 1 |
| CBIMPORT | Core | Batch | 487 | 6 | 28 | 1 | 2 | 1 |
| CBACT01C | Core | Batch | 430 | 2 | 15 | 2 | 0 | 2 |
| COUSR02C | Core | CICS | 414 | 8 | 0 | — | 10 | 0 |
| CBPAUP0C | Auth | Batch | 386 | 2 | 17 | — | 4 | 0 |
| PAUDBLOD | Auth | Batch | 369 | 4 | 11 | — | 0 | 0 |
| DBUNLDGS | Auth | Batch | 366 | 6 | 13 | — | 0 | 0 |
| COUSR03C | Core | CICS | 359 | 8 | 2 | — | 10 | 0 |
| COTRN01C | Core | CICS | 330 | 8 | 0 | — | 6 | 0 |
| PAUDBUNL | Auth | Batch | 317 | 4 | 13 | — | 0 | 0 |
| COMEN01C | Core | CICS | 308 | 9 | 0 | — | 6 | 1 |
| COUSR01C | Core | CICS | 299 | 9 | 0 | — | 6 | 0 |
| COADM01C | Core | CICS | 288 | 9 | 0 | — | 4 | 0 |
| COSGN00C | Core | CICS | 260 | 9 | 0 | — | 6 | 2 |
| COPAUS2C | Auth | CICS | 244 | 2 | 0 | — | 0 | 0 |
| COBTUPDT | DB2 | Batch | 237 | 0 | 7 | — | 8 | 0 |
| CBSTM03B | Core | Batch | 230 | 0 | 17 | — | 1 | 0 |
| CBACT02C | Core | Batch | 178 | 1 | 4 | — | 0 | 1 |
| CBACT03C | Core | Batch | 178 | 1 | 4 | — | 0 | 1 |
| CBCUS01C | Core | Batch | 178 | 1 | 4 | — | 0 | 1 |
| CSUTLDTC | Core | Utility | 157 | 0 | 0 | — | 2 | 0 |
| COBSWAIT | Core | Utility | 41 | 0 | 0 | — | 0 | 1 |

**Estate Totals:** 44 programs, 35,575 lines of code, 426 copybook references, 505 I/O operations.
