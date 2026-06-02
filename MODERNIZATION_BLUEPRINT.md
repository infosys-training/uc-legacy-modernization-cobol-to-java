# Modernization Blueprint

## Executive Summary

This blueprint evaluates four modernization strategies for each functional area of the CardDemo application (44 COBOL programs, ~27,350 LOC). The recommended approach is a **hybrid strategy** — applying different patterns to different domains based on complexity, data coupling, and risk tolerance.

**Overall recommendation:** Rewrite for most domains (the COBOL is well-structured enough to translate cleanly to Java/Spring Boot), with Strangler pattern for the authorization subsystem due to its multi-protocol complexity.

---

## Strategy Definitions

| Strategy | Description | Best For |
|----------|-------------|----------|
| **(a) Strangler** | Wrap existing programs with APIs; route traffic incrementally to new implementation | High-risk, multi-protocol integrations |
| **(b) Replatform** | Keep COBOL code, move to cloud runtime (AWS M2, Micro Focus) | Low-change tolerance, budget constraints |
| **(c) Refactor** | Restructure COBOL for maintainability without language change | Short-term improvements, skill availability |
| **(d) Rewrite** | Translate to Java/Kotlin with Spring Boot | Well-understood logic, clean domain boundaries |

---

## Functional Area Assessments

### 1. User Authentication & Administration

| Programs | COSGN00C, COADM01C, COUSR00C, COUSR01C, COUSR02C, COUSR03C, COMEN01C |
|----------|----------------------------------------------------------------------|
| LOC | ~3,900 |
| Data | USRSEC (VSAM KSDS) — user credentials, roles |
| Coupling | Low — isolated file, no shared writes |
| Complexity | Low-Medium — CRUD operations with basic validation |

**Strategy Evaluation:**

| Strategy | Fit | Reasoning |
|----------|-----|-----------|
| Strangler | Medium | Could wrap sign-on with API, but overhead exceeds benefit for simple logic |
| Replatform | Poor | Plain-text password storage (PIC X(08)) is a security liability that replatforming preserves |
| Refactor | Poor | Fundamental security issues cannot be fixed in COBOL |
| **Rewrite** | **Best** | Replace with Spring Security + JWT; bcrypt password hashing; role-based access |

**Recommendation: Rewrite (Quick Win)**
- Business logic is simple CRUD
- Security posture demands modern authentication (plain-text passwords → bcrypt)
- Isolated data store means zero impact on other domains during migration
- Serves as proof-of-concept for entire migration
- **Team skills:** Any Java developer can implement
- **Risk:** Very Low — self-contained, reversible

---

### 2. Account Management

| Programs | COACTUPC (4,236 LOC), COACTVWC (887 LOC), COACCT01 (374 LOC) |
|----------|---------------------------------------------------------------|
| LOC | ~5,500 |
| Data | ACCTFILE, CUSTFILE, CARDXREF (VSAM KSDS) — shared with 5 other domains |
| Coupling | **High** — ACCTFILE read by 11 programs, written by 5 |
| Complexity | **Very High** — COACTUPC has 359 branching statements, 58 copybooks |

**Strategy Evaluation:**

| Strategy | Fit | Reasoning |
|----------|-----|-----------|
| **Strangler** | **Good** | Wrap with REST API; gradually route traffic to Java while COBOL handles fallback |
| Replatform | Medium | Reduces infrastructure cost but preserves 4,236 LOC maintenance burden |
| Refactor | Poor | BMS screen logic is too deeply interleaved with business rules to separate in COBOL |
| Rewrite | Good | Clean slate enables proper separation of concerns, but high risk due to complexity |

**Recommendation: Strangler + Rewrite (Hybrid)**
- Phase 1: Expose COACTVWC (read-only, 887 LOC) as REST API → low risk
- Phase 2: Extract validation logic from COACTUPC into Java service → runs in shadow mode
- Phase 3: Cut over writes once shadow mode achieves 100% parity
- **Team skills:** Requires COBOL reader + senior Java architect
- **Risk:** High — must run dual-write with reconciliation during transition
- **Justification:** ACCTFILE's shared usage (11 readers, 5 writers) demands incremental approach

---

### 3. Credit Card Management

| Programs | COCRDLIC (1,459 LOC), COCRDSLC (791 LOC), COCRDUPC (1,560 LOC) |
|----------|----------------------------------------------------------------|
| LOC | ~3,800 |
| Data | CARDFILE, CARDXREF (VSAM KSDS) |
| Coupling | Medium — CARDFILE shared with account and transaction domains |
| Complexity | Medium — paginated browse (STARTBR/READNEXT/ENDBR) is a recurring pattern |

**Strategy Evaluation:**

| Strategy | Fit | Reasoning |
|----------|-----|-----------|
| Strangler | Medium | Could work but adds unnecessary complexity for well-understood CRUD |
| Replatform | Poor | BMS screen coupling still requires modernization effort later |
| Refactor | Poor | Cannot extract paginated browse pattern improvement from COBOL |
| **Rewrite** | **Best** | Spring Data pagination replaces VSAM browse pattern cleanly |

**Recommendation: Rewrite**
- Paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) maps directly to Spring Data `Pageable`
- Same pattern appears in 5+ programs — create `AbstractListController` once, reuse
- Card data has clear ownership boundary (only card programs write CARDFILE)
- **Team skills:** Standard Spring Data JPA skills
- **Risk:** Low-Medium — depends on ACCTFILE migration timing (reads account data)

---

### 4. Transaction Processing (Online)

| Programs | COTRN00C, COTRN01C, COTRN02C, COBIL00C (online); CBTRN01C, CBTRN02C (batch) |
|----------|-----------------------------------------------------------------------------|
| LOC | ~3,900 |
| Data | TRANSACT, DALYTRAN, TCATBALF (VSAM) |
| Coupling | **High** — TRANSACT referenced by 21 programs; daily batch pipeline critical |
| Complexity | Medium-High — CBTRN02C is the core batch posting program |

**Strategy Evaluation:**

| Strategy | Fit | Reasoning |
|----------|-----|-----------|
| **Strangler** | **Best** | Transaction data is the most shared entity; must migrate incrementally |
| Replatform | Medium | Preserves batch SLA but locks in COBOL maintenance |
| Refactor | Poor | Sequential file processing model doesn't benefit from COBOL restructuring |
| Rewrite | Good for batch | Spring Batch is mature and well-suited |

**Recommendation: Strangler (online) + Rewrite (batch)**
- Online COTRN00C/01C/02C: Wrap with API, route new transactions to Java service
- Batch CBTRN02C: Rewrite as Spring Batch job (read-validate-write pattern maps cleanly)
- Dual-write TRANSACT during transition with SQS FIFO for consistency
- **Team skills:** Spring Batch expertise required for batch programs
- **Risk:** High — 21-program dependency on TRANSACT demands careful sequencing
- **Justification:** Batch pipeline has strict SLA; must validate Java performance matches COBOL

---

### 5. Transaction Type Management (DB2)

| Programs | COTRTLIC (2,098 LOC), COTRTUPC (1,702 LOC), COBTUPDT (batch) |
|----------|--------------------------------------------------------------|
| LOC | ~3,800 |
| Data | DB2 tables: TRANSACTION_TYPE, TRANSACTION_CATEGORY |
| Coupling | Low — isolated DB2 tables, only these programs access them |
| Complexity | Medium — already uses SQL; cursor-based pagination |

**Strategy Evaluation:**

| Strategy | Fit | Reasoning |
|----------|-----|-----------|
| Strangler | Medium | Unnecessary complexity for already-SQL code |
| Replatform | Medium | Could run on AWS M2 but wastes DB2 → PostgreSQL opportunity |
| Refactor | Poor | DB2 SQL doesn't benefit from COBOL restructuring |
| **Rewrite** | **Best** | DB2 SQL translates to JPA with minimal changes; cleanest migration path |

**Recommendation: Rewrite (Natural Entry Point)**
- Already uses SQL — lowest translation effort of any domain
- DB2 cursor-based pagination → Spring Data `Pageable` is nearly 1:1
- Isolated data (no VSAM dependencies) → can migrate independently
- **Team skills:** JPA/Hibernate standard skills
- **Risk:** Very Low — SQL semantics preserved; easy to verify output equivalence

---

### 6. Authorization Decision Engine (IMS/DB2/MQ)

| Programs | COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS |
|----------|---------------------------------------------------------------------------------|
| LOC | ~3,600 |
| Data | IMS DBPAUTP0 (hierarchical), DB2 AUTHFRDS, MQ queues |
| Coupling | **Very High** — spans 3 subsystems (IMS + DB2 + MQ); real-time message flow |
| Complexity | **Very High** — COPAUA0C alone touches MQ, IMS, CICS, and DB2 |

**Strategy Evaluation:**

| Strategy | Fit | Reasoning |
|----------|-----|-----------|
| **Strangler** | **Best** | Multi-protocol complexity demands incremental approach; cannot do big-bang |
| Replatform | Medium | IMS runtime on cloud is expensive and limiting |
| Refactor | Poor | Cannot simplify IMS/MQ integration within COBOL |
| Rewrite | Risky | Too many integration points to rewrite simultaneously |

**Recommendation: Strangler (Phased)**
- Phase 1: Deploy MQ adapter (Spring JMS) alongside existing queues; new consumers read from both
- Phase 2: Migrate IMS hierarchical data to 2 PostgreSQL tables (auth_summary, auth_detail)
- Phase 3: Replace COPAUA0C decision logic with Java service; keep COBOL as fallback
- Phase 4: Retire IMS once Java service proves stable under production load
- **Team skills:** Requires IMS expertise (rare) + messaging specialist
- **Risk:** Very High — production authorization flow; failure impacts real-time transactions
- **Justification:** This is the most architecturally complex subsystem; premature rewrite risks service outage

---

### 7. Reporting & Statement Generation

| Programs | CORPT00C, CBSTM03A, CBTRN03C |
|----------|-------------------------------|
| LOC | ~2,200 |
| Data | Reads all VSAM files; writes report output files |
| Coupling | Read-only consumers — no write contention |
| Complexity | Low-Medium — sequential read + format + write |

**Strategy Evaluation:**

| Strategy | Fit | Reasoning |
|----------|-----|-----------|
| Strangler | Poor | No benefit to wrapping read-only output programs |
| Replatform | Medium | Could run on M2 cheaply but locks in COBOL |
| Refactor | Poor | Already straightforward sequential processing |
| **Rewrite** | **Best** | Self-contained batch; ideal pilot to prove migration approach |

**Recommendation: Rewrite (Pilot Program)**
- CBSTM03A is the ideal first program to migrate: self-contained, no CICS, measurable output
- Generate baseline output with GnuCOBOL, compare byte-for-byte with Java output
- Proves Spring Batch + Thymeleaf approach before tackling complex programs
- **Team skills:** Spring Batch basics
- **Risk:** Very Low — read-only, no system-of-record changes, easy rollback

---

### 8. Data Migration Tools

| Programs | CBEXPORT (579 LOC), CBIMPORT (484 LOC) |
|----------|----------------------------------------|
| LOC | ~1,060 |
| Data | All VSAM files (export); export file format (CVEXPORT) |
| Coupling | Low — utility programs, run ad-hoc |
| Complexity | Low — sequential read/write with validation |

**Strategy Evaluation:**

| Strategy | Fit | Reasoning |
|----------|-----|-----------|
| Strangler | N/A | Not a runtime service |
| **Replatform** | **Best (interim)** | Keep running during migration for data movement; retire afterward |
| Refactor | Poor | No long-term value |
| Rewrite | Unnecessary | Programs become obsolete once VSAM is decommissioned |

**Recommendation: Replatform (then Retire)**
- Needed during migration for VSAM↔PostgreSQL data movement
- Not worth rewriting since they become obsolete post-migration
- Run on AWS M2 or GnuCOBOL during transition period
- **Team skills:** None required (maintain as-is)
- **Risk:** None — utility programs with no business logic dependencies

---

## Strategy Summary Matrix

| Functional Area | Strategy | Risk | Priority | Effort |
|----------------|----------|------|----------|--------|
| User Auth & Admin | Rewrite | Very Low | P1 (Quick Win) | 4-5 weeks |
| Reporting & Statements | Rewrite | Very Low | P1 (Pilot) | 4-5 weeks |
| Transaction Types (DB2) | Rewrite | Very Low | P2 | 5-7 weeks |
| Credit Card Mgmt | Rewrite | Low-Medium | P2 | 5-7 weeks |
| Account Management | Strangler+Rewrite | High | P3 | 8-10 weeks |
| Transaction Processing | Strangler+Rewrite | High | P3 | 8-10 weeks |
| Authorization Engine | Strangler | Very High | P4 | 6-8 weeks |
| Data Migration Tools | Replatform→Retire | None | P5 | 0 weeks |

---

## Decision Criteria Applied

| Factor | Weight | How Applied |
|--------|--------|-------------|
| Business logic complexity | 30% | High complexity → Strangler (incremental); Low → Rewrite |
| Data coupling | 25% | Shared VSAM files (ACCTFILE: 11 programs) → Strangler with dual-write |
| Team skill availability | 25% | DB2 programs rewrite first (SQL skills transferable); IMS programs last (rare skill) |
| Risk tolerance | 20% | Revenue-impacting flows (authorization) → Strangler; Back-office (reports) → Rewrite |

---

## Technology Target State

| Current | Target |
|---------|--------|
| COBOL/CICS | Java 17 / Spring Boot 3.x |
| BMS 3270 screens | REST APIs + React/Angular frontend |
| VSAM KSDS | PostgreSQL (Aurora) with schema-per-service |
| DB2 | PostgreSQL (same instance, separate schema) |
| IMS DL/I | PostgreSQL relational tables (2 tables replace hierarchy) |
| IBM MQ | Amazon SQS (FIFO queues for ordering) |
| JCL + Control-M | AWS Step Functions + Spring Batch |
| COMMAREA | REST DTOs / gRPC messages |
| GnuCOBOL batch | Spring Batch with JdbcBatchItemWriter |
| Assembler utilities | Java utility classes (DateFormatter, WaitUtil) |
