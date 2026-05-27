# HOTSPOT REPORT

## Overview

This report identifies the top 10 most complex programs in the CardDemo estate, ranked across five dimensions: lines of code, copybook references, I/O operations, business logic density (EVALUATE/IF nesting), and inter-program dependencies. It concludes with modernization priority recommendations.

---

## 1. Rankings by Dimension

### 1.1 Lines of Code (LOC)

| Rank | Program | LOC | Classification |
|------|---------|-----|----------------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS+DB2) |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS+DB2) |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) |
| 6 | COPAUA0C.cbl | 1,026 | Online (CICS+MQ) |
| 7 | COPAUS0C.cbl | 1,032 | Online (CICS) |
| 8 | COACTVWC.cbl | 941 | Online (CICS) |
| 9 | CBSTM03A.CBL | 924 | Batch |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) |

### 1.2 Number of Copybooks Referenced

| Rank | Program | Unique Copybooks | Notes |
|------|---------|-----------------|-------|
| 1 | COACTUPC.cbl | 17 (56 COPY stmts) | 39 uses of CSSETATY via REPLACING |
| 2 | COPAUA0C.cbl | 16 | Authorization module |
| 3 | COACTVWC.cbl | 15 | Account view |
| 4 | COCRDSLC.cbl | 15 | Card detail view |
| 5 | COCRDUPC.cbl | 15 | Card update |
| 6 | COPAUS0C.cbl | 14 | Auth summary view |
| 7 | COCRDLIC.cbl | 13 | Card list |
| 8 | COTRTUPC.cbl | 13 | Trans type update (DB2) |
| 9 | COTRTLIC.cbl | 11 | Trans type list (DB2) |
| 10 | COBIL00C.cbl | 10 | Bill payment |

### 1.3 Number of I/O Operations (EXEC CICS READ/WRITE/STARTBR/READNEXT + file OPEN/READ/WRITE)

| Rank | Program | I/O Ops | Type |
|------|---------|---------|------|
| 1 | COACTUPC.cbl | 17 EXEC CICS | VSAM Read/Write across 3 files |
| 2 | COCRDLIC.cbl | 18 EXEC CICS | STARTBR + READNEXT browsing |
| 3 | COPAUA0C.cbl | 20 EXEC (CICS+SQL+MQ) | MQ + DB2 + VSAM combined |
| 4 | COPAUS0C.cbl | 16 EXEC CICS | VSAM browsing |
| 5 | COPAUS1C.cbl | 15 EXEC CICS | VSAM read |
| 6 | COACTVWC.cbl | 15 EXEC CICS | Read 3 VSAM files |
| 7 | COCRDSLC.cbl | 14 EXEC CICS | Read 4 VSAM files |
| 8 | COBIL00C.cbl | 13 EXEC CICS | Read/Write account + transaction |
| 9 | COCRDUPC.cbl | 12 EXEC CICS | Read/Write card data |
| 10 | COTRN02C.cbl | 11 EXEC CICS | Write transaction + reads |

### 1.4 Business Logic Density (EVALUATE + IF statements)

| Rank | Program | EVALUATE/IF Count | LOC | Density (per 100 LOC) |
|------|---------|-------------------|-----|----------------------|
| 1 | COACTUPC.cbl | 188 | 4,236 | 4.4 |
| 2 | COCRDUPC.cbl | 164 | 1,560 | 10.5 |
| 3 | COCRDLIC.cbl | 140 | 1,459 | 9.6 |
| 4 | CBTRN02C.cbl | 93 | 731 | 12.7 |
| 5 | CBACT04C.cbl | 86 | 652 | 13.2 |
| 6 | CBTRN03C.cbl | 79 | 649 | 12.2 |
| 7 | COCRDSLC.cbl | 76 | 887 | 8.6 |
| 8 | COACTVWC.cbl | 67 | 941 | 7.1 |
| 9 | COTRN00C.cbl | 42 | 699 | 6.0 |
| 10 | COUSR00C.cbl | 41 | 695 | 5.9 |

**Highest density** (logic per 100 LOC): CBACT04C (13.2), CBTRN02C (12.7), CBTRN03C (12.2), COCRDUPC (10.5)

### 1.5 Inter-Program Dependencies (programs that call/are called by others + XCTL targets)

| Rank | Program | Inbound | Outbound | Total | Role |
|------|---------|---------|----------|-------|------|
| 1 | COMEN01C | 1 (from COSGN00C) | 11 (XCTL to menu options) | 12 | Central dispatcher |
| 2 | COADM01C | 1 (from COSGN00C) | 6 (XCTL to admin options) | 7 | Admin dispatcher |
| 3 | CSUTLDTC | 0 | 1 (CEEDAYS) | calledBy: CORPT00C, COTRN02C | Shared utility |
| 4 | CBSTM03B | 0 | 0 | calledBy: CBSTM03A (×13) | Statement subroutine |
| 5 | COSGN00C | 0 (entry point) | 2 (XCTL) | 2 | Application entry |
| 6 | COCRDLIC | 1 (from COMEN01C) | 3 (XCTL) | 4 | Hub for card screens |
| 7 | COACTUPC | 1+ (from COMEN01C) | 1 (XCTL return) | 2 | Heavy data access |
| 8 | COPAUA0C | 1 (from MQ trigger) | 4 (MQOPEN/GET/PUT/CLOSE) | 5 | Auth decision engine |
| 9 | CBTRN02C | 1 (from POSTTRAN JCL) | 6 files accessed | 7 | Core batch posting |
| 10 | CBACT04C | 1 (from INTCALC JCL) | 5 files accessed | 6 | Interest calc engine |

---

## 2. Composite Hotspot Score

Combining all five dimensions (normalized 0-10 scale per dimension):

| Rank | Program | LOC | Copies | I/O | Logic | Deps | **Total (50)** | Classification |
|------|---------|-----|--------|-----|-------|------|----------------|----------------|
| **1** | **COACTUPC.cbl** | 10 | 10 | 9 | 10 | 6 | **45** | Online (CICS) |
| **2** | **COCRDLIC.cbl** | 6 | 7 | 10 | 7 | 7 | **37** | Online (CICS) |
| **3** | **COCRDUPC.cbl** | 7 | 8 | 6 | 9 | 5 | **35** | Online (CICS) |
| **4** | **COPAUA0C.cbl** | 5 | 9 | 10 | 5 | 7 | **36** | Online (CICS+MQ+DB2) |
| **5** | **COTRTLIC.cbl** | 9 | 6 | 8 | 5 | 4 | **32** | Online (CICS+DB2) |
| **6** | **CBTRN02C.cbl** | 4 | 5 | 5 | 8 | 8 | **30** | Batch |
| **7** | **COTRTUPC.cbl** | 8 | 7 | 7 | 4 | 4 | **30** | Online (CICS+DB2) |
| **8** | **COACTVWC.cbl** | 4 | 8 | 8 | 5 | 5 | **30** | Online (CICS) |
| **9** | **CBACT04C.cbl** | 3 | 5 | 5 | 9 | 7 | **29** | Batch |
| **10** | **CBSTM03A.CBL** | 4 | 4 | 5 | 4 | 5 | **22** | Batch |

---

## 3. Modernization Priority Recommendations

### Priority 1: COACTUPC.cbl (Account Update) — Modernize First

**Rationale:**
- **Largest program** in the estate (4,236 LOC) — highest maintenance cost
- **Highest complexity** — 188 conditional statements, 56 COPY statements (39 via REPLACING pattern)
- **Central business function** — account updates are the most frequent write operation
- **Multiple VSAM file access** — reads/writes across Account, Card XREF, and Customer files
- **High field-level validation** — extensive date, SSN, phone, state, ZIP validation using CSLKPCDY (1,318-line lookup table)
- **Modernization benefit:** Decomposing into microservices (Account Service + Validation Service) yields immediate maintainability gains

### Priority 2: CBTRN02C.cbl (Transaction Posting) — Modernize Second

**Rationale:**
- **Core batch engine** — processes all daily transactions; business-critical path
- **High logic density** (12.7 per 100 LOC) — complex validation and reject handling
- **Multi-file coordination** — reads daily trans, validates against XREF, updates Account, writes master + rejects + category balances
- **Pipeline bottleneck** — all downstream processes (reports, statements, interest) depend on its output
- **Modernization benefit:** Converting to event-driven processing (Kafka/SQS) enables real-time transaction posting

### Priority 3: CBACT04C.cbl (Interest Calculator) — Modernize Third

**Rationale:**
- **Highest logic density** (13.2 per 100 LOC) — dense financial calculations
- **Business-critical** — incorrect interest calculation = regulatory/financial risk
- **5 file dependencies** — reads category balances, cross-references, disclosure groups, accounts; writes transactions
- **Modernization benefit:** Extracting to a calculation engine with unit tests ensures correctness during migration

### Priority 4: COCRDLIC.cbl + COCRDUPC.cbl (Credit Card List/Update) — Modernize Together

**Rationale:**
- Combined 3,019 LOC with heavy VSAM browse logic (STARTBR/READNEXT)
- **UI-heavy** — BMS map interaction dominates; natural fit for web UI replacement
- **Shared data model** — both operate on CARDDATA + CARDXREF
- **Modernization benefit:** Replace with REST API + React/Angular UI; eliminates 3270 terminal dependency

### Priority 5: COPAUA0C.cbl (Authorization Decision) — Modernize Fifth

**Rationale:**
- **Multi-technology integration** — CICS + MQ + DB2 + IMS in single program
- **Real-time processing** — handles card authorization decisions via message queues
- **External dependencies** — hardest to test in isolation
- **Modernization benefit:** Natural microservice boundary; isolate as Authorization Service with message queue integration

---

## 4. Modernization Wave Plan

| Wave | Programs | Effort | Risk | Business Value |
|------|----------|--------|------|---------------|
| **Wave 1** | COACTUPC, COACTVWC | High | Medium | Eliminate largest maintenance burden; prove out VSAM-to-DB migration pattern |
| **Wave 2** | CBTRN02C, CBTRN01C, CBTRN03C | High | High | Modernize core batch pipeline; enable real-time processing |
| **Wave 3** | CBACT04C, CBSTM03A/B | Medium | High | Financial calculation engine; requires extensive validation |
| **Wave 4** | COCRDLIC, COCRDSLC, COCRDUPC | Medium | Low | UI modernization; highest user visibility |
| **Wave 5** | COPAUA0C, COPAUS0C-2C, COACCT01, CODATE01 | High | Medium | MQ/IMS integration; complex middleware |
| **Wave 6** | COSGN00C, COADM01C, COMEN01C, COUSR00C-03C | Low | Low | Navigation/security shell; straightforward web replacement |
| **Wave 7** | COTRTLIC, COTRTUPC, COBTUPDT | Medium | Low | DB2 programs already closest to modern patterns |
| **Wave 8** | CBEXPORT, CBIMPORT, COBSWAIT, CSUTLDTC | Low | Low | Utility programs; ETL can use modern tools |

---

## 5. Risk Factors

| Risk | Programs Affected | Mitigation |
|------|------------------|-----------|
| Implicit decimal arithmetic (COMP-3) | CBACT04C, CBTRN02C, CBEXPORT | Use BigDecimal in Java; validate with parallel-run |
| VSAM keyed access semantics | All online programs | Map to JPA/JDBC with composite keys |
| CICS conversational state (COMMAREA) | All CO* programs | Replace with HTTP session or JWT tokens |
| BMS map field validation | COACTUPC, COCRDUPC | Re-implement as front-end validation + API validation |
| MQ message format dependencies | COPAUA0C, COACCT01, CODATE01 | Define API contracts; use message schema registry |
| IMS DL/I hierarchical data | PAUDBLOD, PAUDBUNL, DBUNLDGS | Flatten to relational model; validate with data comparison |
| GDG version management | Report/reject jobs | Replace with timestamped files or S3 versioning |
| EBCDIC/ASCII encoding | All file I/O | Handle during data migration layer; test with production data |
