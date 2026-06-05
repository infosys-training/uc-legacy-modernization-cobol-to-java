# HOTSPOT REPORT — CardDemo Modernization Priorities

## Scoring Methodology

Each program is scored across 5 dimensions (0–20 points each, max 100):

| Dimension | Weight | Measurement |
|-----------|--------|-------------|
| Lines of Code (LOC) | 20 | Normalized: (LOC / max_LOC) × 20 |
| Copybook Count | 20 | Normalized: (count / max_count) × 20 |
| I/O Operations | 20 | Count of READ, WRITE, REWRITE, DELETE, STARTBR, READNEXT, READPREV, EXEC SQL, DL/I calls |
| Business Logic Density | 20 | Count of IF + EVALUATE statements (branching complexity) |
| Inter-Program Dependencies | 20 | Programs that call or are called by this program + files shared |

---

## Top 10 Hotspot Programs

### #1 — COACTUPC.cbl (Score: 87.4/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 4,236 | 20.0 |
| Copybooks | 39 (with REPLACING expansions) | 20.0 |
| I/O Operations | 47 (VSAM READ/REWRITE × 3 files + CICS SEND/RECEIVE) | 17.8 |
| Business Logic | 359 IF/EVALUATE statements (exhaustive validation) | 20.0 |
| Dependencies | 3 VSAM files, CSUTLDTC call, COMEN01C caller | 9.6 |
| **Total** | | **87.4** |

**Why modernize first:** Largest program in the estate (4,236 LOC). Contains the most complex business logic — field-level validation for dates, SSN, phone area codes, state codes, ZIP prefixes. Uses COPY REPLACING 39 times to set BMS attributes. High maintenance cost and highest defect risk. Modularizing the validation rules into a service layer would eliminate ~60% of the code.

---

### #2 — COTRTLIC.cbl (Score: 52.1/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 2,098 | 9.9 |
| Copybooks | 12 | 6.2 |
| I/O Operations | 38 (EXEC SQL CURSOR operations + CICS MAP I/O) | 14.4 |
| Business Logic | 187 IF/EVALUATE statements | 10.4 |
| Dependencies | DB2 TRNTYPE table, CICS, COADM01C caller | 11.2 |
| **Total** | | **52.1** |

**Why modernize:** DB2 cursor-based pagination with CICS screen — natural fit for a REST API + paginated UI. Isolated DB2 dependency makes it a clean extraction candidate.

---

### #3 — COTRTUPC.cbl (Score: 48.7/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,702 | 8.0 |
| Copybooks | 14 | 7.2 |
| I/O Operations | 32 (EXEC SQL INSERT/UPDATE/DELETE + CICS) | 12.1 |
| Business Logic | 168 IF/EVALUATE statements | 9.4 |
| Dependencies | DB2 TRNTYPE + TRNTYCAT tables, COADM01C caller | 12.0 |
| **Total** | | **48.7** |

**Why modernize:** Pairs naturally with COTRTLIC as a CRUD service. DB2 operations map directly to JPA/Spring Data. Can be modernized as a single microservice with COTRTLIC.

---

### #4 — COCRDUPC.cbl (Score: 44.8/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,560 | 7.4 |
| Copybooks | 13 | 6.7 |
| I/O Operations | 29 (VSAM READ/REWRITE + CICS MAP I/O) | 11.0 |
| Business Logic | 142 IF/EVALUATE statements | 7.9 |
| Dependencies | CARDFILE (VSAM), linked from COCRDLIC/COCRDSLC | 11.8 |
| **Total** | | **44.8** |

**Why modernize:** Card management is a bounded context (COCRDLIC → COCRDSLC → COCRDUPC). All three can form a Card Service microservice. VSAM reads map to simple key-value lookups.

---

### #5 — COCRDLIC.cbl (Score: 43.2/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,459 | 6.9 |
| Copybooks | 11 | 5.6 |
| I/O Operations | 34 (STARTBR/READNEXT/READPREV/ENDBR pagination) | 12.9 |
| Business Logic | 119 IF/EVALUATE statements | 6.6 |
| Dependencies | CARDFILE (VSAM via AIX), navigates to COCRDSLC/COCRDUPC | 11.2 |
| **Total** | | **43.2** |

**Why modernize:** Complex VSAM browse logic with alternate index navigation. Pagination pattern maps to Spring Data pageable queries. Part of Card Service bounded context.

---

### #6 — COPAUS0C.cbl (Score: 41.6/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,032 | 4.9 |
| Copybooks | 14 | 7.2 |
| I/O Operations | 26 (IMS DL/I GNP + CICS + VSAM READ) | 9.9 |
| Business Logic | 98 IF/EVALUATE statements | 5.5 |
| Dependencies | IMS PAUTH DB, CARDXREF, links COPAUS1C/COPAUS2C | 14.1 |
| **Total** | | **41.6** |

**Why modernize:** IMS database dependency is the highest-risk technology to retain. Converting IMS hierarchical data to relational (already partially done with DB2 in COPAUS2C) would eliminate the most exotic runtime dependency.

---

### #7 — COPAUA0C.cbl (Score: 40.9/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 1,026 | 4.8 |
| Copybooks | 15 | 7.7 |
| I/O Operations | 28 (MQ OPEN/GET/PUT + IMS DL/I + CICS) | 10.6 |
| Business Logic | 87 IF/EVALUATE statements | 4.8 |
| Dependencies | MQ queues (2), IMS DB, CVACT03Y/01Y/CVCUS01Y, CICS | 13.0 |
| **Total** | | **40.9** |

**Why modernize:** Heaviest integration program — MQ + IMS + CICS in one module. Natural candidate for an event-driven microservice (MQ → Kafka/SQS, IMS → PostgreSQL). Eliminates 3 legacy middleware dependencies simultaneously.

---

### #8 — CBSTM03A.CBL (Score: 39.5/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 924 | 4.4 |
| Copybooks | 1 (COSTM01) | 0.5 |
| I/O Operations | 115 (highest I/O count — intensive read loops over 4 files + write 2 outputs) | 20.0 |
| Business Logic | 67 IF/EVALUATE statements | 3.7 |
| Dependencies | CBSTM03B (CALL), 4 VSAM files, 2 output files | 10.9 |
| **Total** | | **39.5** |

**Why modernize:** Highest I/O density in the estate (115 operations). Statement generation is well-suited for a batch job framework (Spring Batch). Clear input/output contract. CALL to CBSTM03B can be inlined.

---

### #9 — COACTVWC.cbl (Score: 35.8/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 941 | 4.4 |
| Copybooks | 15 | 7.7 |
| I/O Operations | 18 (VSAM READ × 3 files + CICS MAP) | 6.8 |
| Business Logic | 89 IF/EVALUATE statements | 5.0 |
| Dependencies | ACCTFILE, CARDXREF, CUSTFILE, CARDFILE; called from COMEN01C | 11.9 |
| **Total** | | **35.8** |

**Why modernize:** Read-only account view — simplest online program to extract as a read API. Accesses 3-4 entities in a single view, maps directly to a REST endpoint with JOIN query. Low risk, high learning value.

---

### #10 — CBTRN02C.cbl (Score: 35.1/100)

| Metric | Value | Score |
|--------|-------|-------|
| LOC | 731 | 3.5 |
| Copybooks | 5 | 2.6 |
| I/O Operations | 42 (READ/WRITE/REWRITE across 5 files) | 15.9 |
| Business Logic | 64 IF/EVALUATE statements | 3.6 |
| Dependencies | 5 VSAM files (DALYTRAN, XREF, ACCT, TCATBALF, TRANSACT); central batch pipeline | 9.5 |
| **Total** | | **35.1** |

**Why modernize:** Core batch posting engine — processes every daily transaction. Critical business path (pipeline blocker). Complex file interactions but well-defined contract (daily in → posted out + rejects). Spring Batch ItemReader/Processor/Writer pattern is a direct fit.

---

## Modernization Roadmap — Recommended Sequence

### Phase 1: Low-Risk Extraction (Quick Wins)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 1a | COTRTLIC + COTRTUPC + COBTUPDT | Isolated DB2 CRUD — zero VSAM coupling. Maps to Spring Boot + JPA microservice. |
| 1b | COACTVWC | Read-only aggregation of 3 entities. Simple REST GET. Validates data access layer. |

### Phase 2: Core Online Services

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 2a | COCRDLIC + COCRDSLC + COCRDUPC | Card bounded context. VSAM browse → paginated repository. |
| 2b | COTRN00C + COTRN01C + COTRN02C | Transaction bounded context. Online CRUD + validation. |
| 2c | COACTUPC | Largest program — extract validation rules as a shared library before converting. |

### Phase 3: Batch Pipeline

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 3a | CBTRN02C (POSTTRAN) | Core batch engine. Spring Batch job. |
| 3b | CBACT04C (INTCALC) | Interest calculation — pure business logic once I/O is abstracted. |
| 3c | CBSTM03A + CBSTM03B (CREASTMT) | Statement generation — high I/O but linear flow. |
| 3d | CBTRN03C (TRANREPT) | Reporting — can leverage modern reporting tools (JasperReports, etc.) |

### Phase 4: Legacy Middleware Elimination

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 4a | COPAUA0C + COPAUS0C + COPAUS1C + COPAUS2C + CBPAUP0C | IMS + MQ elimination. Convert IMS hierarchy to relational tables. Replace MQ with event-driven messaging (SQS/Kafka). |
| 4b | COACCT01 + CODATE01 | MQ-based services → REST microservices |

### Phase 5: Infrastructure & Scheduling

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| 5a | COBSWAIT, CLOSEFIL, OPENFIL | Scheduling artifacts — replaced by orchestration (AWS Step Functions, Airflow). |
| 5b | All JCL jobs | Convert to CI/CD pipelines or Spring Batch job configurations. |
| 5c | CBEXPORT + CBIMPORT | Data migration utilities — may become ETL jobs or be replaced by database replication. |

---

## Risk Assessment

| Risk Factor | Impact | Mitigation |
|-------------|--------|------------|
| COACTUPC validation rules | Breaking field validations → data corruption | Extract validation as shared library first; unit test each rule independently |
| IMS hierarchical → relational | Data model mismatch (parent-child → joins) | Design target schema with denormalized summary + normalized details |
| VSAM alternate index (AIX) | Complex access patterns hard to replicate | Map to database secondary indexes |
| GDG versioning | Batch restart/recovery semantics | Implement idempotent batch steps with checkpoint metadata |
| MQ integration | Real-time auth flow latency-sensitive | Use async messaging with dead-letter queue for failures |
| COBOL overpunch encoding | Numeric parsing errors (see knowledge base) | Use proven BigDecimal parsing with overpunch lookup table |
| COPY REPLACING macros | 39 attribute expansions in COACTUPC | Generate from BMS definitions; don't hand-translate |
