# CardDemo Migration Risk Register

## Risk Scoring

| Rating | Likelihood | Impact |
|:-------|:-----------|:-------|
| 5 | Almost certain (>90%) | Catastrophic — data loss, financial misstatement, or extended outage |
| 4 | Likely (60–90%) | Major — significant delay, partial data corruption, or degraded service |
| 3 | Possible (30–60%) | Moderate — manageable delay, workaround available |
| 2 | Unlikely (10–30%) | Minor — cosmetic issues, brief delay |
| 1 | Rare (<10%) | Negligible — trivial impact |

**Risk Score = Likelihood × Impact** (range 1–25)

---

## Top 10 Migration Risks

### Risk 1: Financial Calculation Discrepancy in Interest/Fee Processing

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-001 |
| **Category** | Data Integrity |
| **Description** | CBACT04C performs interest and fee calculations using COMP-3 packed decimal arithmetic, disclosure group lookups, and category balance updates. Rewriting this in Java (BigDecimal or double) may produce rounding differences due to COBOL's fixed-point arithmetic semantics vs. IEEE 754 floating-point or Java BigDecimal rounding modes. |
| **Affected Phase** | Phase 4 (Batch Rewrite) |
| **Likelihood** | 4 — Likely |
| **Impact** | 5 — Catastrophic (financial misstatement has regulatory consequences) |
| **Risk Score** | **20** |
| **Mitigation Strategy** | (1) Use Java BigDecimal with explicit HALF_EVEN rounding matching COBOL ROUNDED. (2) Build a penny-level comparison harness that runs both COBOL and Java against identical inputs for 12+ monthly cycles before cutover. (3) Engage auditors early to define acceptable tolerance (recommend zero tolerance). (4) Replatform on M2 first (Phase 2) to establish a stable baseline before rewriting. |
| **Early Warning Indicators** | — Discrepancies in parallel-run reconciliation reports > 0 cents — BigDecimal rounding mode mismatches discovered in unit tests — Disclosure group rate lookup returns different precision than COBOL PIC clause |
| **Owner** | Financial Systems Lead / QA Lead |

---

### Risk 2: VSAM-to-PostgreSQL Data Synchronization Failure During Dual-Write

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-002 |
| **Category** | Data Integrity |
| **Description** | During Phases 2–3, the CDC bridge must keep VSAM and PostgreSQL in sync bidirectionally. Race conditions between batch posting (CBTRN02C writing VSAM) and online API writes (to PostgreSQL) could cause data conflicts, especially for TRANSACT and ACCTDATA where both paths perform writes. |
| **Affected Phase** | Phase 2–3 (Core APIs & Batch Replatform) |
| **Likelihood** | 4 — Likely |
| **Impact** | 4 — Major (conflicting account balances or duplicate/lost transactions) |
| **Risk Score** | **16** |
| **Mitigation Strategy** | (1) Define clear write-ownership per file during each phase (e.g., Phase 2: ACCTDATA writes go through API only; batch reads from VSAM via CDC). (2) Implement optimistic locking with version counters. (3) Use a single writer pattern — avoid true bidirectional writes; prefer one system of record with replicated read copies. (4) Build a nightly reconciliation job that compares VSAM and PostgreSQL row counts and checksums. |
| **Early Warning Indicators** | — Reconciliation job reports row count mismatch — CDC lag exceeds 5 seconds during peak hours — Conflict resolution events appear in dead-letter queue — Account balance differences between VSAM and PostgreSQL |
| **Owner** | Data Engineering Lead |

---

### Risk 3: CICS Transaction Semantics Lost in API Translation

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-003 |
| **Category** | Functional Parity |
| **Description** | CICS provides pseudo-conversational transaction management, COMMAREA state preservation across interactions, and HANDLE ABEND recovery. The strangler-pattern APIs must replicate this behavior. Specific risk areas: (a) COACTUPC uses EXEC CICS HANDLE ABEND for rollback — the API equivalent needs explicit transaction management. (b) Multi-map navigation (XCTL between programs) carries state that must be preserved in the web session or API calls. (c) File-level locking via CICS file control has no direct API equivalent. |
| **Affected Phase** | Phase 2–3 (Strangler APIs) |
| **Likelihood** | 3 — Possible |
| **Impact** | 4 — Major (data corruption from incomplete transactions; UX regressions) |
| **Risk Score** | **12** |
| **Mitigation Strategy** | (1) Map each CICS transaction to a well-defined API operation with explicit DB transaction boundaries (@Transactional in Spring). (2) Replace COMMAREA state with server-side session or stateless JWT claims. (3) Replace CICS file locking with database row-level locking (SELECT FOR UPDATE). (4) Implement compensating transactions for multi-step operations. (5) Test each CICS screen flow end-to-end through the API equivalent. |
| **Early Warning Indicators** | — Partial-update records appearing in database (missing fields that COMMAREA would have carried) — Deadlocks in PostgreSQL corresponding to CICS file contention patterns — User reports of "lost" data between screen navigations |
| **Owner** | Application Architecture Lead |

---

### Risk 4: Batch Processing Window Exceeded on Cloud

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-004 |
| **Category** | Performance |
| **Description** | The mainframe batch pipeline (CLOSEFIL → POSTTRAN → TRANBKP → COMBTRAN → INTCALC → OPENFIL) runs within a defined nightly window. Replatforming to M2 or rewriting to Spring Batch may not achieve the same throughput due to: (a) M2 I/O performance differences vs. native VSAM. (b) Network latency for cloud-hosted PostgreSQL vs. local VSAM. (c) SORT utility (DFSORT/SYNCSORT) performance vs. Java stream sorting. (d) COBSWAIT/MVSWAIT timer semantics. |
| **Affected Phase** | Phase 2 (Replatform), Phase 4 (Rewrite) |
| **Likelihood** | 3 — Possible |
| **Impact** | 3 — Moderate (delayed batch = stale data for next business day) |
| **Risk Score** | **9** |
| **Mitigation Strategy** | (1) Benchmark batch pipeline on M2 during Phase 0 with production-volume data. (2) Profile I/O patterns — identify whether VSAM sequential reads or keyed access are the bottleneck. (3) Use database connection pooling and batch inserts (JDBC batching with 1000+ row batches). (4) Replace SORT with database-side ORDER BY or parallel merge sort. (5) Add batch scaling: partition daily transactions by account range for parallel processing. |
| **Early Warning Indicators** | — M2 batch elapsed time > 120% of mainframe baseline during Phase 0 testing — POSTTRAN step timeout on M2 — SORT step for COMBTRAN exceeds 2x mainframe timing — Database connection pool exhaustion during batch runs |
| **Owner** | Performance Engineering Lead |

---

### Risk 5: EBCDIC/COMP-3 Data Conversion Errors

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-005 |
| **Category** | Data Integrity |
| **Description** | CardDemo uses EBCDIC encoding, packed decimal (COMP-3), binary (COMP), zoned decimal, and signed fields across its VSAM files and sequential datasets. Migration to PostgreSQL requires converting all fields to Unicode/ASCII and standard numeric types. Specific risks: (a) COMP-3 fields with implied decimal points (PIC S9(13)V99 COMP-3) must be correctly scaled. (b) Signed zoned decimal with trailing sign overpunch may be misinterpreted. (c) REDEFINES clauses create polymorphic records that must be correctly parsed. (d) The EXPORT.DATA multi-record format (CVEXPORT copybook) uses record-type discriminators. |
| **Affected Phase** | Phase 0–2 (Data migration and CDC bridge) |
| **Likelihood** | 3 — Possible |
| **Impact** | 5 — Catastrophic (silently corrupted financial data) |
| **Risk Score** | **15** |
| **Mitigation Strategy** | (1) Build a COBOL copybook parser that generates conversion code from PIC clauses. (2) Validate every field of every record type against ASCII test data already provided in `app/data/ASCII/`. (3) Use the ASCII reference files as golden test data: convert EBCDIC → ASCII and compare byte-by-byte. (4) Implement field-level checksums in the CDC bridge. (5) Special attention to CVTRA05Y (350-byte transaction record with multiple COMP-3 fields) and CVACT01Y (300-byte account record). |
| **Early Warning Indicators** | — ASCII conversion output differs from provided `app/data/ASCII/` reference files — Negative amounts appearing as positive (sign handling error) — Account balance totals differ between VSAM and PostgreSQL by powers of 10 (decimal point misalignment) — REDEFINES fields contain garbage data after conversion |
| **Owner** | Data Engineering Lead |

---

### Risk 6: Loss of Mainframe Operational Knowledge

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-006 |
| **Category** | Organizational |
| **Description** | Mainframe COBOL expertise is scarce and aging. During the 12–14 month migration, the team needs mainframe knowledge for: (a) Understanding undocumented business rules embedded in COBOL code. (b) Interpreting JCL job dependencies and VSAM file characteristics. (c) Diagnosing issues during parallel-run reconciliation. (d) Supporting the replatform phase on M2. If key personnel leave or are unavailable, the migration stalls. |
| **Affected Phase** | All phases |
| **Likelihood** | 3 — Possible |
| **Impact** | 4 — Major (schedule delays, incorrect business rule translation) |
| **Risk Score** | **12** |
| **Mitigation Strategy** | (1) Conduct knowledge extraction sessions before Phase 1 — document every business rule in COBOL programs with line-level annotations. (2) Record walkthroughs of each batch job and CICS transaction flow. (3) Retain at least one mainframe SME on contract through Phase 4. (4) Use automated code analysis tools (e.g., CAST, SonarQube for COBOL, AWS M2 Analyzer) to supplement human knowledge. (5) Create a decision log for every ambiguous business rule interpretation. |
| **Early Warning Indicators** | — Mainframe SME announces departure or reduced availability — Business rules documented in migration specs are challenged during UAT — Undocumented COBOL paragraphs discovered during rewrite that nobody can explain — Reconciliation discrepancies that require mainframe debugging to diagnose |
| **Owner** | Program Manager |

---

### Risk 7: Incomplete Test Coverage for Edge Cases

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-007 |
| **Category** | Quality |
| **Description** | CardDemo has no existing automated test suite. The repository contains sample test data but not comprehensive edge-case scenarios. The COBOL programs handle numerous edge cases: (a) CBTRN02C rejects transactions to DALYREJS based on multiple validation rules. (b) CBACT04C has complex conditional logic for different disclosure groups and interest rate tiers. (c) COCRDLIC displays cards differently for admin vs. regular users. (d) COSGN00C handles multiple authentication failure scenarios. Without comprehensive tests, rewrites may silently drop edge-case handling. |
| **Affected Phase** | All phases (but most critical for Phase 3–4 rewrites) |
| **Likelihood** | 4 — Likely |
| **Impact** | 3 — Moderate (bugs discovered in production post-migration) |
| **Risk Score** | **12** |
| **Mitigation Strategy** | (1) Phase 0: Build golden test datasets from production data covering normal, boundary, and error cases. (2) Create BDD-style test scenarios (Cucumber/Gherkin) for every CICS screen and batch job based on code path analysis. (3) Use code coverage tools during parallel run to identify untested paths. (4) For each COBOL program, enumerate all EVALUATE/IF branches and create test inputs exercising each path. (5) Leverage the `uc-bdd-test-generation-cucumber` framework in the organization for test generation. |
| **Early Warning Indicators** | — Code coverage < 80% on rewritten services — UAT discovers functionality present in COBOL but missing in Java — DALYREJS reject scenarios not reproducible in new system — Parallel-run discrepancies traced to untested code paths |
| **Owner** | QA Lead |

---

### Risk 8: IMS DB Hierarchical-to-Relational Schema Migration

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-008 |
| **Category** | Technical Complexity |
| **Description** | The optional Authorization module uses IMS DB with hierarchical structures (DBPAUTP0, DBPAUTX0 with parent/child segments defined in DBD/PSB files). Converting hierarchical data to relational tables requires: (a) Denormalization decisions that may not have unique answers. (b) PCB/PSB navigation logic (GU, GN, GNP calls) translated to SQL joins. (c) Segment search arguments (SSA) mapped to WHERE clauses. (d) Twin segment chains that have no direct relational equivalent. The DLIGSAMP.PSB and multiple DBD files suggest a non-trivial hierarchy. |
| **Affected Phase** | Phase 3 (Optional Modules) |
| **Likelihood** | 3 — Possible |
| **Impact** | 3 — Moderate (authorization module migration delayed) |
| **Risk Score** | **9** |
| **Mitigation Strategy** | (1) Use the IMS DB UNLOAD utilities (PAUDBUNL, DBUNLDGS) to extract data in flat format; analyze the output to understand the actual data model. (2) Map each PCB view to a relational view/query. (3) Consider using a document database (DynamoDB) for the authorization store if the hierarchy is deep, to avoid forced denormalization. (4) Prototype the migration early in Phase 2 as a spike to identify schema complexity. (5) The authorization module is self-contained — delays here don't block other phases. |
| **Early Warning Indicators** | — IMS UNLOAD produces records with unexpected twin chains — PCB navigation cannot be expressed as a single SQL query — Authorization response times increase > 2x after migration to relational — Data integrity constraints violated during IMS → PostgreSQL load |
| **Owner** | Data Architect |

---

### Risk 9: Control-M to Cloud Scheduler Migration Breaks Job Dependencies

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-009 |
| **Category** | Operational |
| **Description** | The Control-M scheduler defines three chains (Daily, Weekly, Monthly) with complex INCOND/OUTCOND dependencies, SMART_FOLDERs, and calendar-based scheduling. Migrating to AWS Step Functions or EventBridge requires: (a) Replicating the CLOSEFIL → process → WAITSTEP → OPENFIL pattern (which manages CICS file availability). (b) Maintaining the dependency between WEEKLY-TransactionTypesDBRefresh → DisclosureGroupsRefresh (SMART_FOLDER with INCOND chaining). (c) Preserving MAXRERUN=5 retry semantics and MAXWAIT=7 day window. (d) The monthly chain depends on daily chain completion (implicit via file state). |
| **Affected Phase** | Phase 2 (Batch Replatform) |
| **Likelihood** | 2 — Unlikely |
| **Impact** | 4 — Major (batch jobs run out of order or don't run at all) |
| **Risk Score** | **8** |
| **Mitigation Strategy** | (1) Map every INCOND/OUTCOND to Step Functions state transitions. (2) Implement the CLOSEFIL/OPENFIL pattern as explicit pre/post steps (may become no-ops if VSAM is replaced). (3) Build a scheduler validation test that verifies job ordering matches Control-M definition. (4) Run Control-M and Step Functions in parallel for 4 weeks to compare execution schedules. (5) Implement dead-letter handling for failed steps with alerting to operations team. |
| **Early Warning Indicators** | — Batch jobs execute out of sequence during testing — OPENFIL runs before processing completes (WAITSTEP timing issue) — Weekly SMART_FOLDER doesn't trigger after MNTTRDB2 completes — Monthly chain fires on wrong calendar day |
| **Owner** | DevOps / SRE Lead |

---

### Risk 10: Regulatory & Compliance Exposure During Migration

| Attribute | Detail |
|:----------|:-------|
| **Risk ID** | R-010 |
| **Category** | Compliance |
| **Description** | Credit card management systems are subject to PCI DSS, SOX, and potentially GDPR/CCPA. During migration: (a) Card numbers (PAN) traverse new network paths and data stores — PCI DSS scope expands. (b) Dual-write means card data exists in both VSAM and PostgreSQL — doubles the attack surface. (c) Access controls must be maintained during transition (RACF → IAM/RBAC). (d) Audit trails must be continuous — no gap in transaction logging during cutover. (e) The plaintext password storage in USRSEC VSAM is a pre-existing compliance debt that migration must resolve, not replicate. |
| **Affected Phase** | All phases |
| **Likelihood** | 3 — Possible |
| **Impact** | 5 — Catastrophic (regulatory fines, audit failure, data breach) |
| **Risk Score** | **15** |
| **Mitigation Strategy** | (1) Engage compliance team in Phase 0 to review the migration plan and define control requirements. (2) Encrypt all card data at rest (PostgreSQL TDE) and in transit (TLS 1.3). (3) Tokenize PAN fields during migration — never store raw PANs in PostgreSQL. (4) Implement column-level encryption for sensitive fields. (5) Maintain continuous audit logging across both legacy and modern systems. (6) Replace plaintext USRSEC passwords with bcrypt/scrypt hashes in identity-service from day one. (7) Conduct a PCI DSS scope assessment for each phase. |
| **Early Warning Indicators** | — PCI auditor raises scope expansion concerns — Unencrypted card numbers found in PostgreSQL or application logs — Access control gap: user authorized in RACF but not in IAM (or vice versa) — Audit trail gap during parallel run (transactions not logged in either system) — Compliance review not completed before phase gate |
| **Owner** | CISO / Compliance Officer |

---

## Risk Summary Matrix

| Risk ID | Risk | L | I | Score | Phase |
|:--------|:-----|:-:|:-:|:-----:|:------|
| R-001 | Financial calculation discrepancy | 4 | 5 | **20** | 4 |
| R-002 | VSAM-PostgreSQL sync failure | 4 | 4 | **16** | 2–3 |
| R-005 | EBCDIC/COMP-3 data conversion | 3 | 5 | **15** | 0–2 |
| R-010 | Regulatory compliance exposure | 3 | 5 | **15** | All |
| R-003 | CICS transaction semantics lost | 3 | 4 | **12** | 2–3 |
| R-006 | Loss of mainframe knowledge | 3 | 4 | **12** | All |
| R-007 | Incomplete test coverage | 4 | 3 | **12** | All |
| R-004 | Batch processing window exceeded | 3 | 3 | **9** | 2, 4 |
| R-008 | IMS hierarchical schema migration | 3 | 3 | **9** | 3 |
| R-009 | Scheduler migration breaks jobs | 2 | 4 | **8** | 2 |

---

## Risk Response Plan Summary

| Priority | Action | When |
|:---------|:-------|:-----|
| 1 | Engage compliance/PCI team for scope assessment | Phase 0 Week 1 |
| 2 | Build COMP-3/EBCDIC conversion validation harness | Phase 0 Week 2 |
| 3 | Capture mainframe SME knowledge (recorded sessions) | Phase 0 Weeks 1–4 |
| 4 | Build golden test datasets for all batch jobs | Phase 0 Weeks 2–4 |
| 5 | Prototype CDC bridge with conflict detection | Phase 0 Week 3 |
| 6 | Benchmark batch pipeline on M2 with production volume | Phase 0 Week 4 |
| 7 | Define BigDecimal rounding rules matching COBOL | Phase 1 |
| 8 | Prototype IMS DB → relational schema as spike | Phase 2 |
| 9 | Map Control-M chains to Step Functions | Phase 2 |
| 10 | Run parallel environments with nightly reconciliation | Phases 2–4 |
