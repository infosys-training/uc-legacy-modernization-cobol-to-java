# Modernization Blueprint — CardDemo COBOL Estate

> Strategy evaluation for each functional area with recommended modernization approach.

---

## 1. Strategy Definitions

| Strategy | Description | When to Use | Effort | Risk |
|----------|------------|-------------|--------|------|
| **Strangler Pattern** | Wrap legacy with APIs; incrementally replace behind the façade | When the system must remain live during migration; when interfaces are well-defined | Medium | Low — gradual rollback is easy |
| **Replatform** | Keep COBOL source; move to cloud runtime (AWS M2, Micro Focus, UniKix) | When time-to-cloud is critical and business logic is stable | Low | Low — no code changes |
| **Refactor** | Restructure COBOL for maintainability without rewriting (modularize, decouple) | When long-term COBOL retention is planned; when programs are too complex to rewrite safely | Medium | Medium — still mainframe-tied |
| **Rewrite** | Translate to modern language (Java/Kotlin/Spring Boot) | When long-term maintainability, talent pool, and modern tooling are priorities | High | High — functional equivalence must be proven |

---

## 2. Functional Area: Account Management

**Programs:** COACTUPC.cbl (4,236 LOC), COACTVWC.cbl (941 LOC), COACCT01.cbl (320 LOC)
**Technology:** CICS + VSAM + MQ (COACCT01)
**Data:** ACCTFILE (VSAM KSDS), CARDXREF, CUSTFILE — 3 shared VSAM datasets
**Complexity:** Very High — COACTUPC has 359 branching statements, 58 copybook references

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| Strangler | ✓ High | Wrap CICS with REST API façade; replace screens incrementally; live system stays operational | Requires API gateway and dual-write during transition |
| Replatform | ✓ Medium | Quick win — COACTUPC runs on AWS M2 as-is | 4,236 LOC of tightly coupled BMS logic remains unmaintainable |
| Refactor | ✗ Low | Could split COACTUPC into sub-programs | Still COBOL; doesn't solve talent shortage or BMS dependency |
| **Rewrite** | **✓ Recommended** | Extract 5 validation modules (date, SSN, phone, state, ZIP) into reusable Java classes; decouple BMS from business logic; enable modern UI | Highest risk — largest program in estate; extensive regression testing required |

### **Recommendation: Rewrite (with Strangler wrapper during transition)**

**Justification:** COACTUPC is the #1 hotspot (score 74.8/100). Its 4,236 LOC contains deeply embedded field validation that is duplicated nowhere else. Rewriting allows extracting validation into reusable Java utility classes that benefit all downstream service development. Use the Strangler pattern during transition: wrap with a REST API, route new traffic to Java, keep COBOL fallback until parity is proven.

**Target Architecture:**
- `AccountService` (Spring Boot) — business logic
- `AccountValidationService` — reusable date/SSN/phone/state/ZIP validators
- `AccountController` (REST API) — replaces BMS screens
- PostgreSQL `accounts` table — replaces VSAM ACCTFILE

---

## 3. Functional Area: Credit Card Management

**Programs:** COCRDLIC.cbl (1,459 LOC), COCRDSLC.cbl (887 LOC), COCRDUPC.cbl (1,560 LOC)
**Technology:** CICS + VSAM
**Data:** CARDFILE, CARDXREF, ACCTFILE, CUSTFILE — 4 shared VSAM datasets
**Complexity:** High — paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) used across 5+ programs

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| Strangler | ✓ High | Wrap card browse/update with REST; reuse pagination pattern | Dual-system state sync needed |
| Replatform | ✓ Medium | Quick lift to M2 | Doesn't address browse pattern complexity |
| Refactor | ✓ Low | Modularize browse into common routine | Still COBOL with BMS dependency |
| **Rewrite** | **✓ Recommended** | Create generic `AbstractListController` with Spring Data paging; reuse for all list screens | Need to replicate VSAM browse semantics exactly |

### **Recommendation: Rewrite**

**Justification:** The paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) appears in COCRDLIC, COTRN00C, COUSR00C, COPAUS0C, and COTRTLIC. Rewriting COCRDLIC first establishes the reusable `AbstractListController` pattern for all other list screens, yielding a 5× reuse multiplier. Card update (COCRDUPC, 1,560 LOC) is simpler than account update and serves as a lower-risk validation of the update-screen migration approach.

**Target Architecture:**
- `CardService` (Spring Boot) — CRUD operations
- `AbstractListController<T>` — generic paginated list (reusable)
- PostgreSQL `cards`, `card_xref` tables — replaces VSAM CARDFILE + CARDXREF

---

## 4. Functional Area: Transaction Processing

**Programs:** COTRN00C.cbl (806 LOC), COTRN01C.cbl (530 LOC), COTRN02C.cbl (710 LOC — online add), CBTRN01C.cbl (494 LOC — batch daily), CBTRN02C.cbl (731 LOC — batch posting), CBTRN03C.cbl (649 LOC — batch report)
**Technology:** CICS (online) + VSAM (batch) — no DB2/IMS/MQ dependency
**Data:** TRANSACT (VSAM KSDS), DALYTRAN (sequential), TCATBALF, DALYREJS, CARDXREF, ACCTFILE
**Complexity:** Medium — CBTRN02C has 96 IF blocks for validation; CBTRN03C has report formatting logic

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| **Strangler** | **✓ Recommended (online)** | Wrap COTRN00C/01C/02C with REST API; online screens are stateless views | Need temporary bridge for VSAM-to-DB during transition |
| Replatform | ✓ High | Entire transaction subsystem runs on M2 | Batch pipeline still file-dependent |
| Refactor | ✓ Medium | Split CBTRN02C validation into sub-programs | Doesn't solve data store modernization |
| **Rewrite** | **✓ Recommended (batch)** | CBTRN01C/02C/03C are self-contained; map cleanly to Spring Batch | Must preserve exact validation rules and rounding |

### **Recommendation: Hybrid — Strangler (online) + Rewrite (batch)**

**Justification:** Online transaction screens (COTRN00C/01C/02C) are thin CICS views that can be wrapped with REST APIs via the Strangler pattern while batch programs run in parallel. Batch programs (CBTRN01C/02C/03C) are self-contained sequential processors with no CICS dependency — they map directly to Spring Batch `ItemReader → ItemProcessor → ItemWriter` steps. The batch pipeline (POSTTRAN → INTCALC → CREASTMT → TRANREPT) can be migrated as a unit to Spring Batch with a shared `transactions` database table.

**Target Architecture:**
- `TransactionService` (Spring Boot) — online CRUD
- `TransactionPostingJob` (Spring Batch) — replaces CBTRN02C
- `InterestCalculationJob` (Spring Batch) — replaces CBACT04C
- `TransactionReportJob` (Spring Batch) — replaces CBTRN03C
- PostgreSQL `transactions`, `daily_transactions`, `tran_category_balances` tables

---

## 5. Functional Area: Statement Generation

**Programs:** CBSTM03A.CBL (924 LOC), CBSTM03B.CBL (230 LOC — I/O submodule)
**Technology:** Pure batch — no CICS, no DB2, no IMS, no MQ
**Data:** Reads 4 VSAM files (XREF, CUST, ACCT, TRNX); writes text + HTML statement files
**Complexity:** Medium LOC but highest I/O count in estate (115 operations)

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| Strangler | ✗ N/A | Batch job — no API to wrap | — |
| Replatform | ✓ Medium | Runs on M2 without changes | File-based output model doesn't scale |
| Refactor | ✓ Low | Could modularize further | Still generates text files |
| **Rewrite** | **✓ Recommended** | Replace file I/O with template engine; enable PDF/email delivery | Must replicate statement layout exactly |

### **Recommendation: Rewrite (pilot batch migration)**

**Justification:** CBSTM03A is the **ideal pilot program** for batch migration because: (1) self-contained with only one CALL dependency (CBSTM03B), (2) no CICS/DB2/IMS/MQ dependencies, (3) already generates HTML output, (4) highest I/O count (115 ops) demonstrates Spring Batch's streaming advantage. Success here proves the batch migration approach for all remaining batch programs.

**Target Architecture:**
- `StatementGenerationJob` (Spring Batch) — main job
- Thymeleaf/FreeMarker templates — replaces line-by-line WRITE statements
- PDF generation via iText/OpenPDF — new capability
- Email delivery integration — new capability

---

## 6. Functional Area: User Administration

**Programs:** COUSR00C.cbl (578 LOC), COUSR01C.cbl (498 LOC), COUSR02C.cbl (555 LOC), COUSR03C.cbl (490 LOC)
**Technology:** CICS + VSAM (user security file)
**Data:** USRSEC (VSAM KSDS, 80-byte records) — isolated, no shared data with other domains
**Complexity:** Low — standard CRUD with simple field validation

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| Strangler | ✓ High | Simple REST wrapper around user CRUD | Overkill for such a small domain |
| Replatform | ✓ High | Quick M2 lift | Perpetuates plain-text password storage |
| Refactor | ✗ Low | Cannot fix security issues without rewrite | — |
| **Rewrite** | **✓ Recommended** | Replace plain-text passwords with bcrypt; add Spring Security; modern auth flows | Small scope makes this low-risk |

### **Recommendation: Rewrite**

**Justification:** The user security file stores passwords in plain text (`SEC-USR-PWD PIC X(08)`). This is a critical security vulnerability that cannot be fixed without a rewrite. The domain is fully isolated (no shared data files with other domains) and small (4 programs, ~2,100 total LOC), making it the lowest-risk rewrite target in the estate.

**Target Architecture:**
- `UserService` (Spring Boot + Spring Security) — auth and user management
- PostgreSQL `users` table with bcrypt password hashing
- JWT/OAuth2 token-based authentication — replaces CICS sign-on

---

## 7. Functional Area: Transaction Type Administration (DB2)

**Programs:** COTRTLIC.cbl (2,098 LOC), COTRTUPC.cbl (1,702 LOC), COBTUPDT.cbl (batch updater)
**Technology:** CICS + DB2 — already using relational database
**Data:** DB2 tables: TRANSACTION_TYPE, TRANSACTION_CATEGORY — isolated from VSAM
**Complexity:** High — cursor-based pagination, cascading deletes, 7+16 SQL statements

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| Strangler | ✓ High | Wrap DB2 CRUD with REST; SQL already translatable | Need to maintain DB2 and new DB in parallel |
| Replatform | ✓ High | DB2 translates easily to RDS PostgreSQL | Still COBOL programs on top |
| Refactor | ✓ Medium | Could simplify cursor logic | Still CICS-dependent |
| **Rewrite** | **✓ Recommended** | DB2 SQL maps 1:1 to JPA/Spring Data; natural database modernization entry point | Cursor pagination semantics differ slightly from Spring Data paging |

### **Recommendation: Rewrite**

**Justification:** These programs already use DB2 with standard SQL, making them the natural entry point for database modernization. The SQL statements translate with minimal changes to JPA. COTRTLIC's cursor-based pagination (`DECLARE CURSOR`, `FETCH`, `CLOSE`) maps to Spring Data's `Pageable` interface. Cascading deletes in COTRTUPC map to JPA `@OneToMany(cascade=CascadeType.ALL)`. This domain is isolated from VSAM, so migration has no impact on other functional areas.

**Target Architecture:**
- `TransactionTypeService` (Spring Boot + Spring Data JPA) — CRUD
- PostgreSQL `transaction_types`, `transaction_categories` tables — migrated from DB2
- Flyway migrations — DDL management

---

## 8. Functional Area: Authorization Processing (IMS/MQ/CICS)

**Programs:** COPAUA0C.cbl (1,026 LOC), COPAUS0C.cbl (1,032 LOC), COPAUS1C.cbl (512 LOC), COPAUS2C.cbl (285 LOC), CBPAUP0C.cbl (350 LOC — batch purge), PAUDBLOD.CBL, PAUDBUNL.CBL, DBUNLDGS.CBL
**Technology:** CICS + IMS (DL/I) + MQ + DB2 — most complex subsystem stack
**Data:** IMS hierarchical database (authorization segments), MQ queues, DB2 fraud table, VSAM files
**Complexity:** Very High — COPAUA0C spans 3 middleware subsystems simultaneously

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| **Strangler** | **✓ Recommended** | Wrap MQ interface with adapter; keep IMS temporarily; gradually replace | Allows phased decoupling of 3 subsystems |
| **Replatform** | ✓ Medium | Move to M2 with IMS emulation | IMS emulation has limitations |
| Refactor | ✗ Low | Cannot simplify 3-subsystem integration in COBOL | — |
| Rewrite | ✓ Medium | Clean architecture possible | Highest risk — 3 subsystems to replace simultaneously |

### **Recommendation: Strangler Pattern (phased subsystem replacement)**

**Justification:** COPAUA0C is the most architecturally complex program, spanning MQ, IMS, and CICS simultaneously. A full rewrite would require replacing all three subsystems at once, which is extremely risky. The Strangler approach allows decoupling in phases:

1. **Phase A:** Wrap MQ interface with Spring JMS adapter → replace MQ first
2. **Phase B:** Migrate IMS hierarchical data to PostgreSQL (2 tables: `authorization_summary`, `authorization_detail`) → replace IMS DL/I calls with JPA
3. **Phase C:** Replace CICS screens with REST API → complete the migration

**Target Architecture:**
- `AuthorizationService` (Spring Boot) — decision engine
- Spring JMS — replaces MQ integration
- PostgreSQL `authorization_summary`, `authorization_detail` tables — replaces IMS DB
- `FraudService` — replaces COPAUS2C DB2 operations

---

## 9. Functional Area: Data Migration (Export/Import)

**Programs:** CBEXPORT.cbl (582 LOC), CBIMPORT.cbl (487 LOC)
**Technology:** Pure batch — no CICS/DB2/IMS/MQ
**Data:** Reads/writes all 5 core VSAM files + sequential export file
**Complexity:** Low — sequential file processing with record-type switching

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| Strangler | ✗ N/A | Batch job — no API to wrap | — |
| **Replatform** | **✓ Recommended (interim)** | Run on M2 during transition — these jobs are needed until VSAM is retired | No code changes needed |
| Refactor | ✗ Low | Already simple enough | — |
| Rewrite | ✓ Low priority | Will be obsolete once all data is in PostgreSQL | Wasted effort if done too early |

### **Recommendation: Replatform (interim), then Retire**

**Justification:** Export/Import jobs exist to move data between mainframe environments. Once all data is migrated to PostgreSQL, these jobs become obsolete. Replatform them to M2 during the transition period to support data migration activities, then retire them after cutover.

---

## 10. Functional Area: Reporting

**Programs:** CORPT00C.cbl (407 LOC — online report request), CBTRN03C.cbl (649 LOC — batch report generator)
**Technology:** CICS (online request) + batch (report generation) + JCL intrader (job submission)
**Data:** TRANSACT, CARDXREF, TRANTYPE, TRANCATG, DATEPARM
**Complexity:** Medium — control-break report formatting in CBTRN03C

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| Strangler | ✓ Medium | Wrap CORPT00C with REST API for report requests | Batch report generation still needed |
| Replatform | ✓ Medium | Runs on M2 | Text-based reports don't meet modern expectations |
| Refactor | ✗ Low | Cannot change output format without rewrite | — |
| **Rewrite** | **✓ Recommended** | Replace text reports with modern dashboards/PDF; eliminate JCL intrader dependency | Must replicate exact report layout for compliance |

### **Recommendation: Rewrite**

**Justification:** CORPT00C submits batch JCL via the internal reader (INTRDRJ1/J2), which has no cloud equivalent. CBTRN03C generates fixed-width text reports that must be modernized. Rewrite as a Spring Batch report job with JasperReports or HTML/PDF output, triggered via REST API instead of JCL submission.

**Target Architecture:**
- `ReportService` (Spring Boot) — REST API for report requests (replaces CORPT00C + JCL intrader)
- `TransactionReportJob` (Spring Batch + JasperReports) — replaces CBTRN03C
- PDF/HTML output — replaces text reports

---

## 11. Functional Area: Batch Utilities

**Programs:** CBACT01C.cbl (430 LOC — file splitter), CBACT02C.cbl (178 LOC — card reader), CBACT03C.cbl (178 LOC — xref reader), CBCUS01C.cbl (178 LOC — customer reader), COBSWAIT.cbl (41 LOC — wait utility), CSUTLDTC.cbl (157 LOC — date utility)
**Technology:** Pure batch — simple file read/print utilities
**Data:** Individual VSAM files (one per program)
**Complexity:** Very Low — 41–430 LOC, simple sequential processing

### Strategy Evaluation

| Strategy | Feasibility | Pros | Cons |
|----------|-------------|------|------|
| **Replatform** | **✓ Recommended (interim)** | Trivial M2 lift; already GnuCOBOL-compatible | Will become obsolete |
| Rewrite | ✓ Low priority | Simple to rewrite but low business value | Wasted effort — these are diagnostic tools |

### **Recommendation: Replatform (interim), then Retire**

**Justification:** These are diagnostic/utility programs used for data verification. They have no business logic. Replatform to M2 during transition for data validation support, then retire once database access tools (SQL clients, admin panels) replace them.

---

## 12. Strategy Summary Matrix

| Functional Area | Programs | Total LOC | Strategy | Priority | Effort (weeks) |
|----------------|----------|-----------|----------|----------|----------------|
| User Administration | 4 | 2,121 | **Rewrite** | P1 — Quick Win | 3–4 |
| Statement Generation | 2 | 1,154 | **Rewrite** (Pilot) | P1 — Pilot | 3–4 |
| Transaction Type (DB2) | 3 | 3,800 | **Rewrite** | P2 | 4–6 |
| Credit Card Management | 3 | 3,906 | **Rewrite** | P2 | 5–7 |
| Account Management | 3 | 5,497 | **Rewrite** + Strangler | P3 | 8–12 |
| Transaction Processing | 6 | 3,920 | **Hybrid** (Strangler + Rewrite) | P3 | 6–8 |
| Reporting | 2 | 1,056 | **Rewrite** | P4 | 3–4 |
| Authorization (IMS/MQ) | 8 | 3,640 | **Strangler** (phased) | P5 | 10–14 |
| Data Migration | 2 | 1,069 | **Replatform** → Retire | P6 | 1–2 |
| Batch Utilities | 6 | 1,162 | **Replatform** → Retire | P6 | 1 |
| **TOTAL** | **39+** | **~27,350** | | | **44–62 weeks** |

---

## 13. Key Decision Points

| Decision | Options | Recommendation | Rationale |
|----------|---------|----------------|-----------|
| Target database | PostgreSQL vs. Oracle vs. Aurora | **PostgreSQL (Aurora)** | Open-source, AWS-native, cost-effective; DB2 SQL translates cleanly |
| Application framework | Spring Boot vs. Quarkus vs. Micronaut | **Spring Boot** | Largest ecosystem; Spring Batch for batch jobs; Spring Data for DB; Spring Security for auth |
| UI framework | React vs. Angular vs. Vue | **React or Angular** | Team preference; both support the component model needed to replace BMS screens |
| Message broker | RabbitMQ vs. Amazon SQS vs. Kafka | **Amazon SQS/SNS** | Managed service; simpler than MQ; sufficient for authorization message flow |
| Batch scheduler | Spring Batch + Scheduler vs. AWS Step Functions | **AWS Step Functions** | Replaces Control-M scheduling; native AWS integration; visual workflow |
| IMS replacement | PostgreSQL tables vs. DynamoDB | **PostgreSQL** | IMS hierarchy maps to 2 relational tables; consistent with rest of migration |
