# CardDemo Modernization Blueprint

## 1. Executive Summary

CardDemo is a multi-tier mainframe credit card management system built on COBOL/CICS/VSAM with optional Db2, IMS DB, and MQ extensions. This blueprint evaluates four modernization strategies for each major functional area and recommends the optimal path based on complexity, risk, business value, and technical coupling.

### Application Profile

| Metric | Value |
|:-------|:------|
| Total COBOL programs (core) | 28 (16 CICS online + 12 batch) |
| Optional module programs | 16 (IMS-DB2-MQ: 8, Db2 Tran Type: 3, VSAM-MQ: 2, ASM utilities: 2, Subroutine: 1) |
| Copybooks (core) | 30 |
| BMS screen maps | 17 |
| JCL jobs | 38 |
| VSAM data stores | 8 KSDS files + AIX |
| Scheduler flows | 3 (Daily / Weekly / Monthly via Control-M) |

---

## 2. Functional Area Analysis

### Area 1 — Authentication & User Management

**Programs:** COSGN00C (signon), COUSR00C (list users), COUSR01C (add user), COUSR02C (update user), COUSR03C (delete user)
**Data stores:** USRSEC VSAM KSDS (user security file — copybook CSUSR01Y)
**BMS maps:** COSGN00, COUSR00, COUSR01, COUSR02, COUSR03
**Coupling:** Low — only touches USRSEC file; COMMAREA passes session context to all other programs

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | Feasible. Wrap signon and user CRUD with REST APIs behind an API gateway. Other modules consume user context via COMMAREA today; replace with JWT/session tokens. |
| **(b) Replatform** | Possible but wasteful — these programs are straightforward CRUD with no complex business logic worth preserving in COBOL. |
| **(c) Refactor** | Minimal value — code is already well-structured with clean COPY member separation. |
| **(d) Rewrite** | **Recommended.** Simple field-level validation and file I/O. A Spring Security / JWT module (Java) or equivalent is vastly more maintainable, testable, and integrable with modern IdP (LDAP, OAuth 2.0, SAML). |

**Recommendation: Rewrite (d)**
Justification: Low complexity, low data coupling, and the security model (plaintext passwords in VSAM) must be modernized regardless. A clean rewrite to a modern auth service is the highest-value, lowest-risk starting point.

---

### Area 2 — Account Management

**Programs:** COACTVWC (view), COACTUPC (update), CBACT01C (batch read/write accounts)
**Data stores:** ACCTDATA VSAM KSDS (CVACT01Y), CARDXREF VSAM KSDS (CVACT03Y), CUSTDATA VSAM KSDS (CVCUS01Y)
**BMS maps:** COACTVW, COACTUP
**Coupling:** High — ACCTDATA and CARDXREF are shared by transaction processing, billing, interest calc, statements, and card management. COACTUPC uses the most copybooks of any program (13+).

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | **Recommended.** Expose Account View and Update as REST APIs. Keep batch readers running against VSAM during transition with a synchronization bridge to the new data store. Allows incremental migration without disrupting downstream consumers. |
| **(b) Replatform** | Viable as interim step (AWS M2 Blu Age / Micro Focus). Preserves CICS transaction semantics. |
| **(c) Refactor** | Limited upside — the COBOL is well-factored with copybooks. The real cost is the VSAM/CICS runtime. |
| **(d) Rewrite** | High risk due to tight coupling with 6+ other programs and complex VSAM I/O patterns (KSDS keyed access, browse, AIX lookups). |

**Recommendation: Strangler (a)**
Justification: Account data is the most interconnected domain. A strangler approach wraps existing functionality with APIs while maintaining backward compatibility through dual-write or change-data-capture patterns. This de-risks migration of all downstream consumers.

---

### Area 3 — Credit Card Management

**Programs:** COCRDLIC (list cards), COCRDSLC (view card detail), COCRDUPC (update card)
**Data stores:** CARDDATA VSAM KSDS (CVACT02Y), CARDXREF (CVACT03Y), ACCTDATA (CVACT01Y), CUSTDATA (CVCUS01Y)
**BMS maps:** COCRDLI, COCRDSL, COCRDUP
**Coupling:** Medium — reads from shared VSAM files but does not write to transaction files.

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | Feasible. Card CRUD can be API-wrapped after Account APIs exist. |
| **(b) Replatform** | Viable with M2. Cards are tightly tied to CICS screen flows. |
| **(c) Refactor** | Minor benefit. The screen navigation via XCTL/RETURN is standard. |
| **(d) Rewrite** | **Recommended.** Business logic is straightforward lookup/update. Once Account APIs exist (Area 2), card management can be rewritten as a microservice consuming those APIs. The COPY REPLACING and CSSTRPFY patterns are boilerplate that disappear in a modern framework. |

**Recommendation: Rewrite (d) — after Account APIs are available**
Justification: Card management is a natural second-wave rewrite candidate. It has moderate coupling that is resolved once Account data is API-accessible.

---

### Area 4 — Transaction Processing (Online)

**Programs:** COTRN00C (list), COTRN01C (view), COTRN02C (add transaction)
**Data stores:** TRANSACT VSAM KSDS (CVTRA05Y), ACCTDATA (CVACT01Y), CARDXREF (CVACT03Y)
**BMS maps:** COTRN00, COTRN01, COTRN02
**Coupling:** High — feeds into batch posting (CBTRN01C/02C), interest calc (CBACT04C), statements (CBSTM03A), and reporting (CBTRN03C).

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | **Recommended.** The online transaction entry can be API-wrapped. Transaction records flow into batch jobs via VSAM files — a CDC or event bridge can replicate writes to both legacy VSAM and the new data store during migration. |
| **(b) Replatform** | Viable as interim for the complete batch-online interaction. |
| **(c) Refactor** | Low value — the code quality is acceptable. |
| **(d) Rewrite** | Risky due to deep dependency chain into batch processing. |

**Recommendation: Strangler (a)**
Justification: Transaction entry is a critical write path. A strangler pattern with dual-write ensures zero data loss while allowing new consumers to read from the modernized store.

---

### Area 5 — Batch Transaction Processing & Posting

**Programs:** CBTRN01C (post daily transactions), CBTRN02C (post and update balances), CBTRN03C (transaction detail report)
**JCL:** POSTTRAN, TRANREPT, DALYREJS
**Data stores:** DALYTRAN (CVTRA06Y), TRANSACT (CVTRA05Y), ACCTDATA (CVACT01Y), CARDXREF (CVACT03Y), TCATBALF (CVTRA01Y)
**Scheduler:** DAILY-TransactionBackup chain (CLOSEFIL → TRANBKP → WAITSTEP → OPENFIL)
**Coupling:** Very high — core batch pipeline. POSTTRAN writes to TRANSACT, ACCTDATA, TCATBALF, and produces DALYREJS rejects.

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | Difficult — batch jobs read/write multiple files atomically. Hard to intercept mid-pipeline. |
| **(b) Replatform** | **Recommended.** Move the batch pipeline to AWS M2 or a cloud COBOL runtime (Micro Focus / Blu Age). Preserves the proven Close-Process-Open pattern and GDG management while gaining cloud scaling and observability. |
| **(c) Refactor** | Could improve CBTRN02C (the most complex batch program with multi-file updates) but doesn't address the runtime dependency. |
| **(d) Rewrite** | High risk — the posting logic with reject handling, category balance updates, and multi-file coordination is the most complex business logic in the system. A rewrite requires exhaustive functional parity testing. |

**Recommendation: Replatform (b) — then rewrite in a later phase**
Justification: Batch posting is the system's core financial engine. Replatforming preserves correctness while moving to cloud infrastructure. A rewrite can be planned after comprehensive test harnesses are established.

---

### Area 6 — Interest Calculation & Financial Processing

**Programs:** CBACT04C (interest calculator)
**JCL:** INTCALC, COMBTRAN (SORT), CREASTMT
**Data stores:** TCATBALF (CVTRA01Y), DISCGRP (CVTRA02Y), ACCTDATA (CVACT01Y), CARDXREF (CVACT03Y + AIX), SYSTRAN (output GDG)
**Scheduler:** MONTHLY-InterestCalculation chain (CLOSEFIL → INTCALC → COMBTRAN → WAITSTEP → OPENFIL)
**Coupling:** High — reads from disclosure groups and transaction category balances; writes system-generated transactions.

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | Not practical — this is a monolithic batch calculation. |
| **(b) Replatform** | **Recommended as interim.** Move to cloud COBOL runtime with the batch pipeline. |
| **(c) Refactor** | Could modularize the interest rate logic for easier testing. |
| **(d) Rewrite** | Eventually desirable — interest/fee logic mapped to a calculation engine (Java with BigDecimal or a rules engine). But requires extensive validation against legacy output. |

**Recommendation: Replatform (b), then Rewrite (d) in later phase**
Justification: Financial calculations carry the highest correctness risk. Replatform first to de-risk infrastructure; rewrite after building a comprehensive regression test suite from production data.

---

### Area 7 — Reporting & Statements

**Programs:** CORPT00C (CICS report submission), CBSTM03A (statement generation), CBSTM03B (file processing subroutine), CBTRN03C (transaction report)
**JCL:** CREASTMT (multi-step: SORT → CBSTM03A), TRANREPT
**Data stores:** TRANSACT, CARDXREF, ACCTDATA, CUSTDATA → outputs STATEMNT.PS, STATEMNT.HTML
**Coupling:** Medium — reads from master files; produces output artifacts.

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | Partially viable — CORPT00C submits JCL from CICS via TDQ. Could be replaced by an API that triggers a cloud-native reporting job. |
| **(b) Replatform** | Works for interim. Statement output is already HTML-capable. |
| **(c) Refactor** | Moderate value — CBSTM03A/B calling convention could be simplified. |
| **(d) Rewrite** | **Recommended.** Report generation is naturally suited to modern tools (JasperReports, Apache PDFBox, or a templating engine). CBSTM03A already generates HTML, so the report model is understood. No transactional writes, making this low-risk. |

**Recommendation: Rewrite (d)**
Justification: Reporting is read-only, has clearly defined inputs/outputs, and benefits most from modern rendering engines. The existing HTML output provides a ready validation baseline.

---

### Area 8 — Data Migration & Export/Import

**Programs:** CBEXPORT (export for branch migration), CBIMPORT (import with validation)
**Data stores:** All master files → EXPORT.DATA.PS (multi-record format via CVEXPORT copybook)
**Coupling:** Low runtime coupling — utilities run independently.

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | Not applicable — these are utility programs, not ongoing services. |
| **(b) Replatform** | Overkill for utility scripts. |
| **(c) Refactor** | Minimal value. |
| **(d) Rewrite** | **Recommended.** ETL/migration utilities are ideal for modern data pipeline tools (Apache Spark, AWS Glue, or simple Java/Python batch scripts). The multi-record export format (CVEXPORT copybook) is well-documented. |

**Recommendation: Rewrite (d)**
Justification: These are utility programs with clear input/output contracts. Modern ETL tools provide better error handling, logging, and scalability.

---

### Area 9 — Admin & Navigation

**Programs:** COADM01C (admin menu), COMEN01C (main menu)
**Data stores:** None directly (pass-through navigation)
**BMS maps:** COADM01, COMEN01
**Coupling:** Low — these are UI navigation controllers only.

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | Not applicable — menus disappear when the UI is modernized. |
| **(b) Replatform** | Automatically handled if CICS is replatformed. |
| **(c) Refactor** | No value. |
| **(d) Rewrite** | **Recommended.** Menu navigation is replaced by a modern web frontend (Angular, React, etc.). These programs contain no business logic worth preserving. |

**Recommendation: Rewrite (d) — naturally replaced by modern UI**
Justification: 3270 screen navigation has no analog in modern architectures. A web frontend replaces the entire BMS/menu system.

---

### Area 10 — Optional Module: Authorization (IMS-DB2-MQ)

**Programs:** COPAUA0C (MQ trigger), COPAUS0C (summary), COPAUS1C (details), COPAUS2C, CBPAUP0C (batch purge), PAUDBLOD, PAUDBUNL, DBUNLDGS
**Data stores:** IMS DB (DBPAUTP0), Db2 (AUTHFRDS), MQ queues
**Coupling:** Self-contained module with its own data stores. Interfaces with core via CARDXREF.

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | **Recommended.** The MQ request/response pattern maps directly to a modern event-driven microservice. Wrap the authorization service with REST APIs and replace MQ with a cloud message broker (SQS, Kafka, or EventBridge). |
| **(b) Replatform** | Requires IMS and MQ runtime support, limiting cloud options. |
| **(c) Refactor** | Limited — the IMS/DB2/MQ coupling is the problem, not the code structure. |
| **(d) Rewrite** | Viable and eventually desirable, but the IMS DB schema migration adds complexity. |

**Recommendation: Strangler (a), then Rewrite (d)**
Justification: The module already uses asynchronous messaging, making it ideal for strangler-pattern migration. Replace MQ interfaces first, then migrate IMS data to a relational store.

---

### Area 11 — Optional Module: Transaction Type Management (DB2)

**Programs:** COTRTUPC (add/edit), COTRTLIC (list/delete), COBTUPDT (batch maintain)
**Data stores:** Db2 tables (TRNTYPE, TRNTYCAT), extracted to VSAM
**JCL:** MNTTRDB2, TRANEXTR, CREADB21
**Scheduler:** WEEKLY-TransactionTypesDBRefresh, WEEKLY-DisclosureGroupsRefresh

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | Feasible — expose Db2 CRUD as REST APIs. |
| **(b) Replatform** | Straightforward — Db2 maps to any cloud RDBMS. |
| **(c) Refactor** | Limited value. |
| **(d) Rewrite** | **Recommended.** The Db2 SQL (cursors, INSERT, UPDATE, DELETE) translates directly to JPA/Spring Data. The weekly refresh jobs become simple scheduled tasks. |

**Recommendation: Rewrite (d)**
Justification: Db2 SQL maps cleanly to modern ORMs. The programs are CRUD-oriented with well-defined DDL. Low risk.

---

### Area 12 — Utility Programs & Infrastructure

**Programs:** COBSWAIT (timer), CSUTLDTC (date conversion/CEEDAYS), Assembler (MVSWAIT, COBDATFT)
**JCL:** CLOSEFIL, OPENFIL, WAITSTEP, various IDCAMS jobs
**Coupling:** Cross-cutting utilities used by batch scheduling.

| Strategy | Evaluation |
|:---------|:-----------|
| **(a) Strangler** | Not applicable. |
| **(b) Replatform** | Handled automatically with batch pipeline replatform. |
| **(c) Refactor** | Not applicable. |
| **(d) Rewrite** | **Recommended.** COBSWAIT → Thread.sleep(). CSUTLDTC → java.time. CLOSEFIL/OPENFIL → cloud file management. These are platform shims that have direct modern equivalents. |

**Recommendation: Rewrite (d) — replaced by platform-native equivalents**
Justification: Utility programs exist to work around mainframe constraints that don't exist in cloud environments.

---

## 3. Strategy Summary

| Functional Area | Strategy | Phase | Risk |
|:----------------|:---------|:------|:-----|
| Authentication & User Mgmt | Rewrite | 1 | Low |
| Account Management | Strangler | 2 | Medium |
| Credit Card Management | Rewrite | 3 | Low-Medium |
| Transaction Processing (Online) | Strangler | 2 | Medium |
| Batch Transaction Processing | Replatform → Rewrite | 2/4 | High |
| Interest Calculation | Replatform → Rewrite | 2/5 | High |
| Reporting & Statements | Rewrite | 3 | Low |
| Data Migration (Export/Import) | Rewrite | 1 | Low |
| Admin & Navigation | Rewrite (UI) | 1 | Low |
| Authorization (IMS-DB2-MQ) | Strangler → Rewrite | 3/5 | Medium |
| Transaction Type Mgmt (DB2) | Rewrite | 3 | Low |
| Utilities & Infrastructure | Rewrite | 1 | Low |
