# CardDemo Modernization Blueprint

## Executive Summary

CardDemo is a mainframe credit card management system comprising **44 COBOL programs** (~20,650 LOC in the core module), **60+ copybooks**, **55 JCL jobs**, and **17 BMS screen maps**. The application runs on CICS with VSAM KSDS data stores and optional DB2/IMS/MQ extensions. This blueprint evaluates four modernization strategies for each functional area and recommends the optimal approach.

---

## Application Inventory Summary

| Category | Count | Notes |
|---|---|---|
| CICS Online Programs | 16 | UI-driven, COMMAREA-coupled |
| Batch Programs | 12 | File I/O, sequential processing |
| Batch Subroutines | 2 | CBSTM03B (statement file I/O), CSUTLDTC (date utility) |
| Optional Module Programs | 16 | Authorization (IMS/DB2/MQ), Transaction Type (DB2), VSAM/MQ |
| Copybooks (core) | 22 | Shared data structures |
| Copybooks (BMS) | 16 | Screen map layouts |
| Copybooks (optional) | 15 | Module-specific structures |
| JCL Jobs | 55 | Init, batch processing, utilities |
| BMS Maps | 17 | 3270 screen definitions |
| VSAM Data Files | 9 | KSDS with AIX |
| Assembler Modules | 2 | MVSWAIT (timer), COBDATFT (date format) |

---

## Functional Areas

### 1. Authentication & User Security

**Programs:** COSGN00C (260 LOC)
**Copybooks:** CSUSR01Y, COCOM01Y, COSGN00 (BMS)
**Data Stores:** USRSEC (VSAM KSDS — 80-byte records)
**Complexity:** Low

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap with REST API for login/token. Straightforward — single VSAM file, simple read-verify logic. |
| **(b) Replatform** | Move to AWS M2 as-is. Low value — security model is weak (plaintext passwords in VSAM). |
| **(c) Refactor** | Add password hashing in COBOL. Possible but adds maintenance burden to a dead-end technology. |
| **(d) Rewrite** | Replace with modern identity provider (OAuth2/OIDC, Spring Security, Keycloak). |

**Recommendation: (d) Rewrite** — Replace with a modern identity provider.
- **Justification:** The current security model stores plaintext passwords in a flat VSAM file with no encryption, hashing, or token management. No amount of wrapping or replatforming fixes this fundamental deficiency. A modern identity service (Spring Security + JWT or Keycloak) is the correct foundation for the modernized system. This is also the lowest-risk area to rewrite given its isolation from business logic.

---

### 2. User Administration (Admin Domain)

**Programs:** COADM01C (288 LOC), COUSR00C (695 LOC), COUSR01C (299 LOC), COUSR02C (414 LOC), COUSR03C (359 LOC)
**Copybooks:** COADM02Y, COCOM01Y, CSUSR01Y, COUSR00–03 (BMS)
**Data Stores:** USRSEC (VSAM KSDS)
**Complexity:** Low–Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Expose CRUD via APIs, let new UI call them. Clean seam — only touches USRSEC file. |
| **(b) Replatform** | Keep as CICS screens on cloud runtime. Low value for admin functions used infrequently. |
| **(c) Refactor** | Restructure COBOL for better maintainability. Marginal benefit for simple CRUD. |
| **(d) Rewrite** | Build as a microservice with REST API + modern UI. Standard CRUD pattern, well-understood. |

**Recommendation: (d) Rewrite** — Build as part of the Identity/User Management microservice.
- **Justification:** User administration is simple CRUD on a single VSAM file (USRSEC). The programs are small (total ~2,055 LOC) with no complex business logic. Rewriting as a Spring Boot REST service with a modern web UI is faster than wrapping CICS screens. This pairs naturally with the Authentication rewrite as a single User Management bounded context.

---

### 3. Account Management

**Programs:** COACTVWC (941 LOC), COACTUPC (4,236 LOC)
**Copybooks:** CVACT01Y (account), CVACT02Y (card), CVACT03Y (xref), COCOM01Y, CSUTLDWY (date), COACTVW/COACTUP (BMS)
**Data Stores:** ACCTDAT (VSAM KSDS — 300-byte records), CARDDAT (VSAM KSDS — 150-byte), CCXREF (VSAM KSDS — 50-byte), CXACAIX (VSAM AIX)
**Complexity:** High

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap CICS programs with APIs, incrementally replace. Feasible but COACTUPC contains extensive validation logic (SSN, phone, date, credit limit, etc.) that would need careful extraction. |
| **(b) Replatform** | Move to AWS M2. Preserves complex validation but delays modernization of tightly coupled account/card/xref data. |
| **(c) Refactor** | Restructure COACTUPC (4,236 LOC with deep validation logic). Significant effort, unclear benefit. |
| **(d) Rewrite** | Translate to Java/Kotlin service. Complex but highest long-term value. |

**Recommendation: (a) Strangler Pattern** — Wrap with APIs first, then incrementally rewrite.
- **Justification:** COACTUPC is the largest and most complex program (4,236 LOC) with intricate field-level validation (US phone format, SSN validation with IRS exclusion rules, date validation via CEEDAYS, credit limit enforcement). The account domain also has the most data coupling — it touches ACCTDAT, CARDDAT, CCXREF, and the CXACAIX alternate index. Rewriting all at once carries high risk of regression. The strangler pattern allows the team to expose read operations (COACTVWC) as APIs first, then incrementally replace the update logic while maintaining the CICS programs as a fallback.

---

### 4. Credit Card Management

**Programs:** COCRDLIC (1,459 LOC), COCRDSLC (887 LOC), COCRDUPC (1,560 LOC)
**Copybooks:** CVACT02Y (card), CVACT03Y (xref), CVACT01Y (account), CVCRD01Y, COCOM01Y, COCRDLI/COCRDSL/COCRDUP (BMS)
**Data Stores:** CARDDAT, CCXREF, CXACAIX, ACCTDAT (all VSAM KSDS)
**Complexity:** High

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap list/view/update as APIs. Good fit — card operations are well-defined but share data stores with Account domain. |
| **(b) Replatform** | Move to cloud runtime. Delays necessary decoupling from account data. |
| **(c) Refactor** | Improve COBOL structure. Large programs with UI-interleaved business logic make this expensive. |
| **(d) Rewrite** | Build as a microservice. Clean domain but shares VSAM files with Account domain. |

**Recommendation: (a) Strangler Pattern** — Co-evolve with Account Management.
- **Justification:** Credit Card Management is deeply coupled to Account Management through shared VSAM files (CARDDAT, CCXREF, ACCTDAT, CXACAIX). The COCRDLIC program implements a browsable list with forward/backward pagination via CICS STARTBR/READNEXT, which is complex to replicate. The programs are large (total ~3,906 LOC) and contain UI logic interleaved with business rules. The strangler pattern allows wrapping these as APIs while keeping the shared data store intact, then extracting them alongside the Account domain when data migration occurs.

---

### 5. Transaction Processing (Online)

**Programs:** COTRN00C (699 LOC), COTRN01C (330 LOC), COTRN02C (783 LOC)
**Copybooks:** CVTRA05Y (transaction), CVACT01Y, CVACT03Y, COCOM01Y, COTRN00–02 (BMS)
**Data Stores:** TRANSACT (VSAM KSDS — 350-byte), ACCTDAT, CCXREF, CXACAIX
**Complexity:** Medium–High

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap list/view/add as APIs. Transaction creation (COTRN02C) updates both TRANSACT and ACCTDAT, requiring careful coordination. |
| **(b) Replatform** | Move to cloud runtime. Preserves VSAM-based transaction storage, which is the main modernization target. |
| **(c) Refactor** | Restructure for clarity. Moderate value — logic is fairly well-organized already. |
| **(d) Rewrite** | Build as an event-driven transaction service. Highest value — enables proper ACID guarantees, audit trail, scalability. |

**Recommendation: (d) Rewrite** — after Account/Card domains are stabilized via strangler.
- **Justification:** Transaction processing is the core revenue-generating capability. COTRN02C creates transactions by writing to TRANSACT VSAM and updating account balances in ACCTDAT — essentially a two-phase operation with no true ACID guarantees in the VSAM model. Rewriting this as an event-driven service with a relational database provides proper transactional integrity, audit logging, and scalability. However, this should happen *after* the Account and Card domains are API-wrapped, so the new Transaction service can call those APIs rather than directly accessing VSAM files.

---

### 6. Transaction Processing (Batch)

**Programs:** CBTRN01C (494 LOC), CBTRN02C (731 LOC), CBTRN03C (649 LOC)
**Copybooks:** CVTRA05Y, CVTRA06Y (daily tran), CVTRA01Y (cat balance), CVTRA02Y (disc group), CVTRA03Y (tran type), CVTRA04Y (tran category), CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y
**Data Stores:** DALYTRAN (sequential), TRANSACT, ACCTDAT, CARDDAT, CUSTDAT, CCXREF, TCATBALF, DISCGRP, TRANCATG, TRANTYPE, DALYREJS (sequential)
**Complexity:** High

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Difficult — batch programs are not callable via API; they are JCL-driven file processors. |
| **(b) Replatform** | Move JCL + COBOL to cloud batch runtime (AWS M2 or Micro Focus). Fastest path. |
| **(c) Refactor** | Improve COBOL structure, add better error handling. Moderate value. |
| **(d) Rewrite** | Translate to Spring Batch / modern ETL. Complex but eliminates mainframe dependency. |

**Recommendation: (b) Replatform first, then (d) Rewrite.**
- **Justification:** Batch transaction processing is the most data-coupled area in the entire system — CBTRN02C alone touches 6 VSAM files (DALYTRAN, TRANSACT, XREF, ACCTDAT, TCATBALF, DALYREJS). CBTRN03C generates reports from TRANSACT joined with XREF, TRANTYPE, and TRANCATG. These programs implement critical end-of-day processing including transaction posting, balance updates, rejection handling, and report generation. Replatforming first (running the COBOL batch on a cloud runtime) preserves correctness while the team builds parallel Spring Batch implementations that can be validated against the COBOL output. This dual-run approach is the safest path for batch workloads.

---

### 7. Interest Calculation & Financial Reporting

**Programs:** CBACT04C (652 LOC), CBSTM03A (924 LOC), CBSTM03B (230 LOC)
**Copybooks:** CVTRA01Y, CVTRA02Y, CVACT01Y, CVACT03Y, COSTM01, CVCUS01Y
**Data Stores:** TCATBALF, DISCGRP, CCXREF, ACCTDAT, TRANSACT, CUSTDAT
**Complexity:** High (financial calculations with regulatory implications)

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Not applicable — batch programs with no API surface. |
| **(b) Replatform** | Keep running on cloud. Safe for interest calculations where precision matters. |
| **(c) Refactor** | Improve COBOL — CBSTM03A uses ALTER/GO TO, COMP-3, and mainframe control block addressing intentionally as modernization test cases. |
| **(d) Rewrite** | Translate to Java with BigDecimal. Must be validated extensively for numeric precision. |

**Recommendation: (b) Replatform first, then (d) Rewrite with extensive parallel testing.**
- **Justification:** Interest calculation (CBACT04C) combines per-category balance data from TCATBALF with interest rates from DISCGRP to compute charges per account. Statement generation (CBSTM03A/B) produces both plain text and HTML output from transaction data joined with customer/account/xref data. These programs contain financial business rules where even minor precision differences can have regulatory consequences. CBSTM03A intentionally uses challenging COBOL constructs (ALTER, GO TO, COMP-3 arithmetic, 2D arrays, subroutine calls) making automated translation unreliable. Replatform first to maintain business continuity, then build a Java replacement with BigDecimal arithmetic validated against the COBOL output through parallel-run comparison.

---

### 8. Data Import/Export & Migration

**Programs:** CBEXPORT (582 LOC), CBIMPORT (487 LOC)
**Copybooks:** CVEXPORT, CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y
**Data Stores:** CUSTDAT, ACCTDAT, CCXREF, TRANSACT, EXPORT file
**Complexity:** Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap as data migration APIs. Possible but batch-oriented. |
| **(b) Replatform** | Keep on cloud runtime. Low value — these are migration utilities. |
| **(c) Refactor** | Clean up COBOL. Marginal value for one-time-use utilities. |
| **(d) Rewrite** | Build as modern ETL pipelines. Ideal for data migration during modernization. |

**Recommendation: (d) Rewrite** — as part of the data migration tooling.
- **Justification:** CBEXPORT and CBIMPORT are branch migration utilities that read/write a multi-record export file format with different record types (customer, account, xref, transaction) and validation checksums. These programs are natural candidates for rewriting as modern ETL jobs (Spring Batch, Apache Spark, or AWS Glue) that will be needed during the modernization itself — to migrate data from VSAM to relational databases. The new ETL tooling serves double duty: it replaces these programs AND provides the data migration pipeline.

---

### 9. Account Data Utilities (Batch Readers)

**Programs:** CBACT01C (430 LOC), CBACT02C (178 LOC), CBACT03C (178 LOC), CBCUS01C (178 LOC)
**Copybooks:** CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CODATECN
**Data Stores:** ACCTDAT, CARDDAT, CCXREF, CUSTDAT
**Complexity:** Low

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Overkill — simple read-and-print utilities. |
| **(b) Replatform** | Minimal value for utility programs. |
| **(c) Refactor** | Not needed — programs are simple. |
| **(d) Rewrite** | Replace with database queries or CLI tools once data is in RDBMS. |

**Recommendation: (d) Rewrite** — replace with database queries or admin tools.
- **Justification:** These are simple sequential-read-and-display utilities used for operational monitoring. CBACT01C is slightly more complex (430 LOC) as it produces multiple output formats including variable-length records and array-based output. Once the underlying data is in a relational database, these programs become unnecessary — a SQL query or admin dashboard replaces them entirely. No special migration effort needed; they retire naturally when the data stores migrate.

---

### 10. Bill Payment

**Programs:** COBIL00C (572 LOC)
**Copybooks:** CVACT01Y, CVACT03Y, CVTRA05Y, COCOM01Y, COBIL00 (BMS)
**Data Stores:** TRANSACT, ACCTDAT, CXACAIX
**Complexity:** Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Expose as a payment API. Clean business operation with well-defined inputs/outputs. |
| **(b) Replatform** | Move CICS program to cloud. Preserves UI constraints. |
| **(c) Refactor** | Moderate benefit. |
| **(d) Rewrite** | Build as a payment service API. Natural microservice boundary. |

**Recommendation: (a) Strangler Pattern** — Expose as a Payment API, then rewrite.
- **Justification:** Bill Payment has a clean functional boundary — it reads an account balance, creates a payment transaction, and updates the account balance. However, it shares data stores with Transaction Processing and Account Management (TRANSACT, ACCTDAT, CXACAIX). The strangler pattern allows exposing this as a modern payment API while the underlying data access still goes through VSAM, then migrating to direct database access once the Account domain's data store is modernized.

---

### 11. Transaction Reports (Online Trigger)

**Programs:** CORPT00C (649 LOC)
**Copybooks:** CVTRA05Y, COCOM01Y, CORPT00 (BMS)
**Data Stores:** TRANSACT (read), Extra-partition TDQ (write — triggers batch JCL)
**Complexity:** Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Expose as a report-generation API endpoint. |
| **(b) Replatform** | Move to cloud. TDQ/internal reader mechanism is mainframe-specific. |
| **(c) Refactor** | Marginal value. |
| **(d) Rewrite** | Build as an async report service triggered by API call. |

**Recommendation: (d) Rewrite** — as an async report generation service.
- **Justification:** CORPT00C is unique in that it bridges online and batch processing — it uses an extra-partition TDQ to submit batch JCL from within a CICS transaction. This mechanism (TDQ → internal reader → JCL submission) is deeply mainframe-specific and has no direct cloud equivalent. Rewriting as an async report service (API call triggers a background job) is the cleanest approach. This pairs with the batch Transaction Processing rewrite.

---

### 12. Optional: Authorization Module (IMS/DB2/MQ)

**Programs:** COPAUA0C, COPAUS0C, COPAUS1C, COPAUS2C (CICS), CBPAUP0C (batch), PAUDBLOD, PAUDBUNL, DBUNLDGS (utilities)
**Copybooks:** CIPAUDTY, CIPAUSMY, CCPAURQY, CCPAURLY, CCPAUERY, COPAU00–01 (BMS), IMSFUNCS, PCBs
**Data Stores:** IMS DB (DBPAUTP0, DBPAUTX0), DB2 tables (AUTHFRDS), MQ queues
**Complexity:** Very High

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Very difficult — IMS/DB2/MQ integration creates deep platform coupling. |
| **(b) Replatform** | Requires IMS and MQ runtime support in cloud environment. AWS M2 supports this. |
| **(c) Refactor** | Marginal value — complexity is inherent in IMS/MQ integration. |
| **(d) Rewrite** | Build as a modern authorization service with RDBMS + message queue (Kafka/SQS). |

**Recommendation: (d) Rewrite** — but defer to a later phase.
- **Justification:** The Authorization module is the most platform-dependent component, requiring IMS hierarchical database, DB2, and MQ — three middleware dependencies beyond what the core application needs. Replatforming requires provisioning all three middleware services in the cloud. Rewriting as a modern authorization service (Spring Boot + PostgreSQL/Aurora + Kafka/SQS) eliminates all three dependencies. However, the effort is substantial and this module is optional/additive, so it should be deferred until the core CardDemo migration is complete.

---

### 13. Optional: Transaction Type Management (DB2)

**Programs:** COTRTLIC (CICS), COTRTUPC (CICS), COBTUPDT (batch)
**Copybooks:** CSDB2RPY, CSDB2RWY, COTRTLI/COTRTUP (BMS), DCLTRTYP, DCLTRCAT (DCL)
**Data Stores:** DB2 tables (TRNTYPE, TRNTYCAT)
**Complexity:** Medium

| Strategy | Assessment |
|---|---|
| **(a) Strangler** | Wrap DB2 CRUD operations with APIs. Relatively clean since DB2 access is already SQL-based. |
| **(b) Replatform** | Move CICS+DB2 to cloud. Requires DB2 runtime. |
| **(c) Refactor** | Minimal value. |
| **(d) Rewrite** | Build as a reference data microservice with REST API. |

**Recommendation: (d) Rewrite** — as a Reference Data microservice.
- **Justification:** Transaction Type Management is already SQL-based (DB2 cursors, INSERT/UPDATE/DELETE) making it the easiest domain to translate to a modern RDBMS-backed service. The DCL copybooks (DCLTRTYP, DCLTRCAT) essentially define the database schema, providing a direct mapping to JPA entities. This is a good "quick win" for the modernization team to build confidence.

---

## Strategy Summary Matrix

| Functional Area | LOC | Recommendation | Phase | Risk |
|---|---|---|---|---|
| Authentication & Security | 260 | Rewrite | 1 | Low |
| User Administration | 2,055 | Rewrite | 1 | Low |
| Transaction Type Mgmt (DB2) | ~800 | Rewrite | 1 | Low |
| Account Data Utilities | 964 | Retire/Rewrite | 1 | Low |
| Data Import/Export | 1,069 | Rewrite (ETL) | 2 | Low |
| Transaction Reports (Online) | 649 | Rewrite | 2 | Medium |
| Account Management | 5,177 | Strangler → Rewrite | 2–3 | High |
| Credit Card Management | 3,906 | Strangler → Rewrite | 2–3 | High |
| Bill Payment | 572 | Strangler → Rewrite | 3 | Medium |
| Transaction Processing (Online) | 1,812 | Rewrite | 3 | High |
| Transaction Processing (Batch) | 1,874 | Replatform → Rewrite | 3–4 | High |
| Interest Calc & Reporting | 1,806 | Replatform → Rewrite | 4 | Very High |
| Authorization (IMS/DB2/MQ) | ~2,500 | Rewrite (deferred) | 5 | Very High |

---

## Key Decision Factors

### Business Logic Complexity
- **Highest:** COACTUPC (4,236 LOC of validation), CBACT04C (interest calc), CBSTM03A (statement generation with ALTER/GO TO patterns)
- **Medium:** Transaction posting (CBTRN01C/02C), Credit Card CRUD (COCRDLIC/COCRDSLC/COCRDUPC)
- **Lowest:** Authentication, User Admin, Data Utilities

### Data Coupling
- **Tightly Coupled:** Account, Card, Transaction, and Bill Payment all share ACCTDAT, CARDDAT, CCXREF, and TRANSACT VSAM files
- **Isolated:** USRSEC (auth/admin only), TCATBALF/DISCGRP/TRANCATG/TRANTYPE (reference data), IMS databases (authorization module only)

### Team Skill Availability
- **Java/Spring Boot:** Assumed available for the "to-Java" target
- **COBOL:** Needed during strangler/replatform phases for maintenance and parallel validation
- **Mainframe Ops (JCL/CICS/VSAM):** Critical for replatform phases; can be wound down as domains migrate

### Risk Tolerance
- **Low-risk areas first:** Authentication, User Admin, Reference Data, Utilities
- **High-risk areas last:** Financial calculations, batch transaction posting, authorization
- **Parallel-run validation:** Essential for all financial processing domains
