# HOTSPOT REPORT — CardDemo COBOL Estate

> **Application:** CardDemo — Credit Card Management System  
> **Generated:** 2026-06-02  
> **Methodology:** Composite scoring across 5 dimensions: LOC, copybook references, I/O density, branching complexity, and inter-program dependencies

---

## 1. Top 10 Programs — Composite Hotspot Ranking

| Rank | Program | LOC | Copybooks | I/O Ops | IF/EVALUATE | Dependencies | Hotspot Score |
|------|---------|-----|-----------|---------|-------------|--------------|---------------|
| 1 | **COACTUPC** | 4,236 | 58 | 23 | 184 | 4 (XCTL to/from) | **74.8** |
| 2 | **COTRTLIC** | 2,098 | 12 | 39 | 115 | 3 (XCTL, SQL) | **52.1** |
| 3 | **COTRTUPC** | 1,702 | 15 | 37 | 134 | 3 (XCTL, SQL) | **48.7** |
| 4 | **COCRDUPC** | 1,560 | 16 | 5 | 167 | 4 (XCTL to/from) | **44.3** |
| 5 | **COCRDLIC** | 1,459 | 14 | 11 | 149 | 4 (XCTL to/from) | **41.9** |
| 6 | **COPAUA0C** | 1,026 | 17 | 27 | 62 | 6 (MQ/IMS/CICS) | **40.5** |
| 7 | **COPAUS0C** | 1,032 | 15 | 10 | 36 | 4 (IMS/CICS/LINK) | **35.2** |
| 8 | **COACTVWC** | 941 | 16 | 4 | 70 | 3 (XCTL to/from) | **32.8** |
| 9 | **CBSTM03A** | 924 | 5 | 117 | 20 | 2 (CALL CBSTM03B) | **31.6** |
| 10 | **COCRDSLC** | 887 | 16 | 4 | 80 | 4 (XCTL to/from) | **30.4** |

### Scoring Formula

```
Hotspot Score = (LOC_norm × 0.25) + (Copybook_norm × 0.15) + (IO_norm × 0.20)
             + (Branch_norm × 0.25) + (Dependency_norm × 0.15)

Where each metric is normalized to 0-100 against the maximum observed value.
```

---

## 2. Detailed Metrics — Dimension Breakdown

### 2.1 Lines of Code (Top 10)

| Rank | Program | LOC | Classification | Ratio to Average |
|------|---------|-----|---------------|-----------------|
| 1 | COACTUPC | 4,236 | Online (CICS) | 6.8× |
| 2 | COTRTLIC | 2,098 | Online (CICS/DB2) | 3.4× |
| 3 | COTRTUPC | 1,702 | Online (CICS/DB2) | 2.7× |
| 4 | COCRDUPC | 1,560 | Online (CICS) | 2.5× |
| 5 | COCRDLIC | 1,459 | Online (CICS) | 2.3× |
| 6 | COPAUS0C | 1,032 | Online (CICS/IMS) | 1.7× |
| 7 | COPAUA0C | 1,026 | Online (CICS/IMS/MQ) | 1.6× |
| 8 | COACTVWC | 941 | Online (CICS) | 1.5× |
| 9 | CBSTM03A | 924 | Batch | 1.5× |
| 10 | COCRDSLC | 887 | Online (CICS) | 1.4× |

**Estate average:** 622 LOC/program | **Median:** 487 LOC | **Total:** 30,175 LOC

### 2.2 Copybook References (Top 10)

| Rank | Program | Copybook Count | Unique Data Entities Accessed |
|------|---------|---------------|-------------------------------|
| 1 | COACTUPC | 58 | Account, Customer, Card, XREF, Security, Validation |
| 2 | COPAUA0C | 17 | Account, Customer, XREF, Auth Request/Reply/Error, IMS, MQ |
| 3 | COACTVWC | 16 | Account, Customer, Card, XREF |
| 4 | COCRDSLC | 16 | Card, Customer, Account |
| 5 | COCRDUPC | 16 | Card, Customer, Account |
| 6 | COPAUS0C | 15 | Account, Card, Customer, XREF, Auth IMS |
| 7 | COTRTUPC | 15 | Transaction Type/Category (DB2), Card, User |
| 8 | COCRDLIC | 14 | Card, Account |
| 9 | COTRTLIC | 12 | Transaction Type (DB2), Card |
| 10 | COBIL00C | 11 | Account, XREF, Transaction |

> **Note:** COACTUPC's 58 count includes 3× CSSETATY (COPY REPLACING for field attribute groups).

### 2.3 I/O Operations (Top 10)

| Rank | Program | I/O Count | Types | File/Resource Count |
|------|---------|-----------|-------|---------------------|
| 1 | CBSTM03A | 117 | READ, WRITE (text + HTML) | 6 files |
| 2 | COTRTLIC | 39 | SQL OPEN/FETCH/CLOSE, CICS SEND/RECEIVE | DB2 + BMS |
| 3 | COTRTUPC | 37 | SQL UPDATE/DELETE, CICS SEND/RECEIVE | DB2 + BMS |
| 4 | COACCT01 | 32 | MQOPEN/MQGET/MQPUT/MQCLOSE, CICS RETRIEVE | MQ + CICS |
| 5 | CODATE01 | 30 | MQOPEN/MQGET/MQPUT/MQCLOSE, CICS RETRIEVE | MQ + CICS |
| 6 | CBEXPORT | 29 | READ (5 files), WRITE (1 export file) | 6 files |
| 7 | CBIMPORT | 29 | READ (1 import), WRITE (5 files + error) | 7 files |
| 8 | COPAUA0C | 27 | MQ + IMS DLI + CICS READ | MQ + IMS + VSAM |
| 9 | CBTRN02C | 23 | READ/WRITE/REWRITE on 6 files | 6 VSAM files |
| 10 | COACTUPC | 23 | CICS READ/REWRITE on 3 VSAM files | 3 files + BMS |

### 2.4 Business Logic Density — IF/EVALUATE Count (Top 10)

| Rank | Program | IF + EVALUATE | LOC | Density (per 100 LOC) |
|------|---------|--------------|-----|-----------------------|
| 1 | COACTUPC | 184 | 4,236 | 4.3 |
| 2 | COCRDUPC | 167 | 1,560 | 10.7 |
| 3 | COCRDLIC | 149 | 1,459 | 10.2 |
| 4 | COTRTUPC | 134 | 1,702 | 7.9 |
| 5 | COTRTLIC | 115 | 2,098 | 5.5 |
| 6 | CBTRN02C | 93 | 731 | 12.7 |
| 7 | CBACT04C | 86 | 652 | 13.2 |
| 8 | COCRDSLC | 80 | 887 | 9.0 |
| 9 | CBTRN03C | 79 | 649 | 12.2 |
| 10 | COACTVWC | 70 | 941 | 7.4 |

> **Highest density:** CBACT04C (13.2 IF/EVALUATE per 100 LOC) — complex interest calculation logic.

### 2.5 Inter-Program Dependencies (Top 10)

| Rank | Program | Total Dependencies | Incoming | Outgoing | Pattern |
|------|---------|-------------------|----------|----------|---------|
| 1 | COMEN01C | 12 | 1 (from COSGN00C) | 11 (XCTL targets) | Central hub |
| 2 | COPAUA0C | 6 | 0 (triggered via MQ) | MQ(3), IMS(2), CICS READ(3) | Multi-subsystem |
| 3 | COACTUPC | 4 | 1 (from COMEN01C) | XCTL back + 3 VSAM READ | Heavy VSAM |
| 4 | COADM01C | 7 | 1 (from COSGN00C) | 6 (XCTL targets) | Admin hub |
| 5 | COCRDLIC | 4 | 1 (from COMEN01C) | XCTL to COCRDSLC/COCRDUPC + back | Navigation chain |
| 6 | CBSTM03A | 2 | 0 (batch JCL) | CALL CBSTM03B | Module pair |
| 7 | COSGN00C | 3 | 0 (entry point) | XCTL to COMEN01C or COADM01C | Entry router |
| 8 | COBIL00C | 4 | 1 (from COMEN01C) | CICS R/W ACCTFILE, TRANSACT, XREF | Multi-file update |
| 9 | COACCT01 | 4 | 0 (MQ trigger) | MQOPEN(3), MQGET, MQPUT(2), MQCLOSE(3) | MQ service |
| 10 | COTRN02C | 4 | 1 (from COMEN01C) | CICS READ ACCTFILE + XREF, WRITE TRANSACT | Online transaction |

---

## 3. Modernization Priority Recommendations

### 3.1 Priority Ranking

| Priority | Program | Score | Recommended Modernization Approach | Rationale |
|----------|---------|-------|-----------------------------------|-----------|
| **P1** | COACTUPC | 74.8 | **Decompose into microservice** — separate validation, account update, and customer lookup | Largest program (4,236 LOC), highest branching complexity, accesses 6 data entities. Single point of failure for account operations. Contains 480+ field-level validations that should become a validation library. |
| **P2** | COTRTLIC + COTRTUPC | 52.1/48.7 | **Migrate as DB2 CRUD pair** → Spring Data JPA REST API | Already uses SQL — cleanest DB2-to-JPA migration path. Paginated cursor logic maps to Spring Pageable. Combined 3,800 LOC. |
| **P3** | COPAUA0C | 40.5 | **Event-driven microservice** — replace MQ/IMS with Kafka + PostgreSQL | Most complex subsystem integration (MQ + IMS + CICS + VSAM). Bridges 3 middleware layers. Must modernize IMS to relational + MQ to modern messaging simultaneously. |
| **P4** | COCRDUPC + COCRDLIC + COCRDSLC | 44.3/41.9/30.4 | **Single Card Management API** — REST endpoints for list/view/update | Three programs share same data model (CVACT02Y) and form a natural CRUD boundary. High branching = complex UI validation → move to client-side. |
| **P5** | CBSTM03A/B | 31.6 | **Batch-to-cloud** — Spring Batch with template engine (Thymeleaf) | Highest I/O count (117 ops). Statement generation is ideal for batch modernization. HTML generation should use a template engine. |
| **P6** | CBTRN02C | — | **Transaction Posting Service** — event-sourced microservice | Core of daily pipeline. 93 IF/EVALUATEs encode business rules for transaction validation that should become a rules engine. |
| **P7** | CBACT04C | — | **Interest Calculation Engine** — Spring Batch step processor | Highest logic density (13.2/100 LOC). Pure calculation with clear inputs/outputs. Good candidate for isolated unit testing first. |
| **P8** | COACTVWC | 32.8 | **Read-only Account API** (subset of P1) | View-only version of COACTUPC. Can be a simple GET endpoint once P1 is modernized. |

### 3.2 Modernization Wave Plan

```
Wave 1 (Months 1-2): Foundation + Quick Wins
├── COTRTLIC + COTRTUPC + COBTUPDT → Transaction Type Microservice (DB2→JPA)
├── CBACT01C–03C + CBCUS01C → Data Export Utilities (simple batch)
└── COSGN00C + COADM01C + COUSR00C–03C → Auth/User Service (Spring Security)

Wave 2 (Months 3-4): Core Domain
├── COACTUPC + COACTVWC → Account Management Service (decompose 4,236 LOC)
├── COCRDLIC + COCRDSLC + COCRDUPC → Card Management Service
└── COBIL00C + COTRN00C–02C → Transaction Service (online operations)

Wave 3 (Months 5-6): Complex Processing
├── CBTRN02C + CBTRN01C → Transaction Posting Service (batch)
├── CBACT04C → Interest Calculation Engine (batch)
├── CBSTM03A/B → Statement Generation Service (batch)
└── CBTRN03C + CORPT00C → Reporting Service

Wave 4 (Months 7-8): Subsystem Migration
├── COPAUA0C + COPAUS0C–2C → Authorization Service (IMS→relational, MQ→Kafka)
├── COACCT01 + CODATE01 → MQ Integration Service (VSAM/MQ→REST/Kafka)
└── CBPAUP0C + PAUDBLOD/PAUDBUNL + DBUNLDGS → Auth Data Management
```

### 3.3 Risk Factors

| Risk | Affected Programs | Mitigation |
|------|-------------------|-----------|
| **Multi-subsystem coupling** | COPAUA0C (MQ+IMS+CICS) | Strangler fig pattern; facade service first |
| **Implicit field validation** | COACTUPC (CSLKPCDY lookups) | Extract validation rules as configurable JSON/YAML |
| **BMS screen coupling** | All 16 online programs | Decouple: API-first, frontend replacement separately |
| **Sequential file dependencies** | Batch pipeline (6 programs) | Event sourcing or managed file transfer |
| **Date format inconsistency** | COBDATFT, CSUTLDTC, CODATECN | Standardize on ISO-8601 in new services |
| **Plain-text passwords** | CSUSR01Y (SEC-USR-PWD) | **Critical security debt** — hash immediately in Wave 1 |

---

## 4. Complexity Distribution

```
Hotspot Score Distribution (44 programs):

  70+ ████ 1 program   (COACTUPC — extreme outlier)
50-69 ████████ 2 programs (COTRTLIC, COTRTUPC)
40-49 ████████████ 3 programs (COCRDUPC, COCRDLIC, COPAUA0C)
30-39 ████████████████ 4 programs (COPAUS0C, COACTVWC, CBSTM03A, COCRDSLC)
20-29 ████████████████████ 8 programs (mid-range online & batch)
10-19 ████████████████████████████ 14 programs (simple CICS screens)
 0-9  ████████████████████████████████████ 12 programs (utilities, data print)
```

### Estate Health Indicators

| Metric | Value | Assessment |
|--------|-------|-----------|
| Average LOC | 622 | Moderate — manageable program size |
| Max/Average ratio | 6.8× | High — COACTUPC is an outlier needing decomposition |
| Programs > 1000 LOC | 7 (16%) | Acceptable |
| Programs with DB2 | 4 (9%) | Low — most data is VSAM, DB2 migration is limited |
| Programs with IMS | 7 (16%) | Moderate — requires IMS-to-relational migration |
| Programs with MQ | 3 (7%) | Low — focused on authorization module |
| Shared copybooks (>5 users) | 10 | Core data model well-factored |
| Plain-text credentials | 1 (CSUSR01Y) | **Critical security issue** |
