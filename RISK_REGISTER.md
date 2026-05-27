# CardDemo Migration Risk Register

## Risk Assessment Framework

**Likelihood Scale:** 1 (Rare) → 5 (Almost Certain)
**Impact Scale:** 1 (Negligible) → 5 (Critical)
**Risk Score:** Likelihood × Impact (1–25)

| Score Range | Risk Level | Action |
|-------------|------------|--------|
| 1–5 | Low | Monitor |
| 6–12 | Medium | Active mitigation required |
| 13–19 | High | Escalation and dedicated mitigation |
| 20–25 | Critical | Immediate executive attention |

---

## Top 10 Migration Risks

### Risk 1: Data Integrity Loss During VSAM-to-Relational Migration

| Attribute | Details |
|-----------|---------|
| **Description** | VSAM KSDS files use EBCDIC encoding, packed decimal (COMP-3), and binary (COMP) numeric formats. The CVEXPORT copybook demonstrates complex REDEFINES structures where the same bytes represent different data types depending on record type. Incorrect conversion of these formats during migration to PostgreSQL could silently corrupt financial data (account balances, transaction amounts, interest calculations). |
| **Likelihood** | 4 — High. COMP-3 packed decimal and EBCDIC-to-ASCII conversion errors are among the most common mainframe migration defects. The CardDemo application uses COMP-3 in CVEXPORT (EXP-ACCT-CURR-BAL, EXP-CUST-FICO-CREDIT-SCORE), COMP in CVEXPORT (EXP-TRAN-MERCHANT-ID, EXP-ACCT-CURR-CYC-DEBIT), and signed decimal (S9V99) throughout. |
| **Impact** | 5 — Critical. Financial data corruption affects account balances, interest calculations, and regulatory reporting. Could result in monetary loss and compliance violations. |
| **Risk Score** | **20 — Critical** |
| **Mitigation Strategy** | 1. Build a comprehensive data validation framework that compares every migrated record against the VSAM source byte-by-byte. 2. Create COBOL extraction programs that output all numeric fields in human-readable format for comparison. 3. Run CBACT01C (account reader), CBCUS01C (customer reader), CBACT02C (card reader), CBACT03C (xref reader) against both VSAM and PostgreSQL data to produce comparison reports. 4. Zero-tolerance policy: any discrepancy blocks phase progression. |
| **Early Warning Indicators** | — Account balance totals differ between VSAM and PostgreSQL extracts. — Interest calculation parallel run (Phase 3) produces different results. — CBEXPORT output from M2 differs from database export. — COMP-3 fields show unexpected values in PostgreSQL (negative numbers appearing positive, decimal point misalignment). |

---

### Risk 2: Interest Calculation Financial Equivalence Failure

| Attribute | Details |
|-----------|---------|
| **Description** | CBACT04C performs monthly interest calculation using transaction category balances (TCATBALF), disclosure group rates (DISCGRP), and account data. The calculation involves multiple VSAM file reads via alternate indexes (CXACAIX for CCXREF) and produces interest charge transactions that update account balances. Rewriting this in Java must produce mathematically identical results, but differences in floating-point arithmetic, rounding behavior, and COBOL's fixed-point decimal semantics (PIC S9(10)V99) could cause discrepancies. |
| **Likelihood** | 4 — High. COBOL COMP-3 arithmetic truncates rather than rounds, and operates in fixed-point BCD. Java's BigDecimal with different rounding modes will produce different results unless carefully calibrated. |
| **Impact** | 5 — Critical. Interest calculation errors directly affect customer charges and regulatory compliance. Even $0.01 differences across thousands of accounts aggregate to significant discrepancies. |
| **Risk Score** | **20 — Critical** |
| **Mitigation Strategy** | 1. Replatform CBACT04C to AWS M2 first (Phase 0) to maintain exact equivalence. 2. In Phase 3, rewrite using Java BigDecimal with ROUND_DOWN (truncation) to match COBOL behavior. 3. Execute parallel runs for 3 consecutive months before cutover. 4. Compare results at account level: must match within ±$0.01 per account. 5. Retain M2 version as fallback for 6 months post-rewrite. |
| **Early Warning Indicators** | — Parallel run produces any account with >$0.01 variance. — Total interest charged differs between M2 and Java versions. — Category balance totals diverge over successive monthly runs. — Edge cases: zero-balance accounts, accounts at credit limit, negative balance accounts. |

---

### Risk 3: CICS Transaction Semantics Lost in Microservice Translation

| Attribute | Details |
|-----------|---------|
| **Description** | CICS provides pseudo-conversational transaction management, automatic resource recovery, and the COMMAREA mechanism for state passing between program invocations. The CardDemo COMMAREA (COCOM01Y) carries user context, navigation state, and entity identifiers across program boundaries. Translating this to stateless REST APIs risks losing transactional guarantees, especially for multi-step operations like COACTUPC (account update) which reads account, validates, and writes in a single CICS logical unit of work. |
| **Likelihood** | 3 — Moderate. Well-understood problem in mainframe migrations, but requires careful design of distributed transaction patterns. |
| **Impact** | 4 — High. Lost transactions, partial updates, or inconsistent state in account/card data could cause operational errors and customer impact. |
| **Risk Score** | **12 — Medium** |
| **Mitigation Strategy** | 1. Map each CICS program's transaction boundary to equivalent microservice patterns: use database transactions for single-service operations, saga patterns for cross-service operations. 2. COACTUPC's account update becomes a single database transaction within `account-service` (no saga needed — all data is in the same bounded context). 3. COBIL00C's bill payment (which crosses account and transaction contexts) uses the saga pattern with compensating transactions. 4. Implement idempotency keys for all write operations to handle retry scenarios. |
| **Early Warning Indicators** | — Integration tests show partial updates (account balance changed but transaction not recorded). — Concurrent update conflicts not handled (lost updates). — COMMAREA state fields (CDEMO-PGM-CONTEXT) not properly mapped to API session state. — Bill payment creates transaction but fails to update account balance (or vice versa). |

---

### Risk 4: Batch Window Elimination Causes Operational Disruption

| Attribute | Details |
|-----------|---------|
| **Description** | The current architecture requires a daily batch window where CICS files are closed (CLOSEFIL), batch processing runs (TRANBKP, POSTTRAN), and files are reopened (OPENFIL). During this window, online transaction entry is unavailable. The modernization plan eliminates this window by moving to a relational database with concurrent access. However, the transition period (when some programs run on M2 with VSAM and others use PostgreSQL) requires careful coordination to avoid data conflicts. |
| **Likelihood** | 3 — Moderate. The transition period is the riskiest time; post-migration, concurrent access is handled natively by PostgreSQL. |
| **Impact** | 4 — High. If batch and online programs access the same data without proper coordination, data corruption or lost updates result. |
| **Risk Score** | **12 — Medium** |
| **Mitigation Strategy** | 1. During transition phases, maintain the batch window for M2 VSAM programs. Only eliminate it after all programs accessing a given VSAM file are migrated off M2. 2. Track file-by-file migration status: a VSAM file's batch window is eliminated only when zero M2 programs read/write it. 3. The VSAM-to-DB sync bridge must be paused during M2 batch runs to avoid conflicts. 4. Implement a "batch mode" flag in the sync bridge that coordinates with the M2 scheduler. |
| **Early Warning Indicators** | — VSAM-to-DB sync conflicts (same record updated in both directions). — M2 batch jobs fail with VSAM file status errors (file locked by sync process). — Transaction counts differ between VSAM TRANSACT and PostgreSQL `transactions` table after daily reconciliation. — CLOSEFIL/OPENFIL scripts fail because migrated files no longer exist on M2. |

---

### Risk 5: COBOL Business Rule Misinterpretation During Rewrite

| Attribute | Details |
|-----------|---------|
| **Description** | COBOL programs contain implicit business rules encoded in data validation logic, 88-level condition names, paragraph flow control, and EVALUATE statements. COACTUPC alone has ~39 COPY REPLACING invocations for field-level attribute management, complex date validation (CSUTLDPY/CSUTLDWY), state code lookups (CSLKPCDY with area codes and zip code tables), and signed decimal validation. Developers unfamiliar with COBOL may misinterpret these rules, especially: REDEFINES clauses that overlay different structures on the same memory, COMP/COMP-3 arithmetic behavior, and implicit decimal points in PIC clauses. |
| **Likelihood** | 4 — High. Business rule misinterpretation is the #1 defect category in mainframe rewrite projects. The CardDemo application intentionally uses diverse COBOL patterns to exercise analysis tooling. |
| **Impact** | 4 — High. Incorrect business rules lead to wrong validation (accepting invalid data or rejecting valid data), wrong calculations, and wrong behavior. |
| **Risk Score** | **16 — High** |
| **Mitigation Strategy** | 1. Create a comprehensive business rules catalog before rewriting any program. Extract rules from each COBOL program's PROCEDURE DIVISION, EVALUATE statements, and 88-level conditions. 2. Use automated COBOL analysis tools (e.g., AWS Mainframe Modernization code analysis, Micro Focus Enterprise Analyzer) to catalog data flows and business rules. 3. For each rewritten service, create test cases derived from the COBOL source — not from requirements documents — to ensure behavioral equivalence. 4. Engage COBOL-experienced developers for code review of every rewritten module. 5. Pay special attention to: CSSETATY REPLACING patterns, CSLKPCDY lookup tables, signed field handling (PIC S9), and date validation in CSUTLDPY. |
| **Early Warning Indicators** | — Test cases derived from COBOL logic fail in the Java implementation. — Account update rejects data that the COBOL version accepted (or vice versa). — Date validation behaves differently for edge cases (leap years, century boundaries). — Lookup code validation (state codes, zip codes, area codes) returns different results. — Numeric field precision differs (e.g., COBOL PIC S9(10)V99 vs. Java BigDecimal scale). |

---

### Risk 6: Performance Degradation in Microservice Architecture

| Attribute | Details |
|-----------|---------|
| **Description** | CICS programs access VSAM data via in-memory buffers with sub-millisecond latency. A single COACTVWC execution reads from ACCTDAT, CARDDAT, CCXREF, and CUSTDAT — four file reads in one program invocation. In the microservice architecture, this becomes REST calls to `account-service` (which reads accounts, customers, xref) plus `card-service` (which reads cards), introducing network latency, serialization overhead, and multiple database round-trips. Batch programs like CBTRN02C that process thousands of daily transactions with sequential VSAM reads could see order-of-magnitude slowdowns with per-record API calls. |
| **Likelihood** | 3 — Moderate. Network latency is predictable and can be addressed with design patterns, but batch processing performance requires careful optimization. |
| **Impact** | 3 — Moderate. Degraded online response times affect user experience; degraded batch performance could extend processing windows beyond acceptable limits. |
| **Risk Score** | **9 — Medium** |
| **Mitigation Strategy** | 1. For online operations: `account-service` owns accounts, customers, and xref in one database — no cross-service calls for the common account view pattern. `card-service` calls `account-service` only when it needs account context (denormalize card-account link into cards table to reduce calls). 2. For batch operations: use bulk/batch APIs (e.g., POST with arrays) rather than per-record API calls. Spring Batch chunk-oriented processing reads from the database directly (same service, no API overhead). 3. Establish performance baselines from mainframe metrics (transaction response times, batch elapsed times) and set SLOs. 4. Implement caching (Redis) for reference data lookups that are currently VSAM reads. |
| **Early Warning Indicators** | — API response times >500ms for operations that were <100ms on mainframe. — Batch processing elapsed time exceeds mainframe baseline by >50%. — Database connection pool exhaustion during peak load. — Cross-service API call latency dominates total response time (measurable via distributed tracing). |

---

### Risk 7: Loss of Scheduling Orchestration During Control-M Migration

| Attribute | Details |
|-----------|---------|
| **Description** | The CardDemo application uses Control-M for job scheduling with three orchestrated cycles (daily, weekly, monthly). The Control-M definitions include sophisticated dependency management: INCOND/OUTCOND chains ensure CLOSEFIL completes before processing starts, processing completes before OPENFIL runs. The weekly cycle has a SMART_FOLDER with nested dependencies (MNTTRDB2 triggers both DisclosureGroupsRefresh and TransactionTypesDBRefresh in parallel). Migrating to AWS Step Functions or EventBridge requires recreating these dependency chains, including error handling (MAXRERUN=5), time constraints (TIMETO=23:00), and cross-cycle dependencies. |
| **Likelihood** | 3 — Moderate. Scheduling migration is well-understood, but the cross-cycle dependencies and error handling require careful translation. |
| **Impact** | 4 — High. A missed or misordered batch run could result in stale data, missed interest calculations, or lost transaction backups. |
| **Risk Score** | **12 — Medium** |
| **Mitigation Strategy** | 1. Document every Control-M dependency chain (INCOND/OUTCOND) as a directed acyclic graph (DAG). 2. Map each DAG to an AWS Step Functions state machine with equivalent Wait, Choice, and Parallel states. 3. Run both Control-M (on M2) and Step Functions in parallel for 4 weeks before switching. Compare job completion times and output. 4. Implement alerting for: missed schedules, jobs exceeding TIMETO equivalent, and dependency violations. 5. Retain Control-M definitions as rollback until Step Functions are validated for 3 consecutive cycles (daily×30 + weekly×4 + monthly×3). |
| **Early Warning Indicators** | — Step Functions workflow times out (equivalent of TIMETO=23:00 breach). — Jobs execute out of order (e.g., OPENFIL runs before TRANBKP completes). — Weekly parallel branches (DisclosureGroups + TransactionTypes) don't synchronize correctly. — Monthly interest calculation misses its window. — MAXRERUN equivalent not triggered on transient failure. |

---

### Risk 8: VSAM Alternate Index (AIX) Behavior Not Replicated

| Attribute | Details |
|-----------|---------|
| **Description** | CardDemo uses VSAM Alternate Indexes in two critical paths: CARDAIX (alternate index on CARDDAT for card number lookup) and CXACAIX (alternate index on CCXREF for account-based lookup in interest calculation). VSAM AIX has specific behaviors that differ from standard relational indexes: AIX records can contain multiple pointers (when multiple primary records share the same alternate key), AIX maintenance happens automatically with VSAM updates, and AIX access paths support STARTBR/READNEXT browse operations. CBACT04C uses CXACAIX to find all cards linked to an account during interest calculation — this is a non-unique alternate key returning multiple records. Standard database secondary indexes handle this differently. |
| **Likelihood** | 2 — Low. Relational database indexes handle non-unique keys natively, but the STARTBR/READNEXT browse pattern requires translation to SQL cursors or range queries. |
| **Impact** | 4 — High. If the AIX-based lookup in interest calculation returns different results (wrong cards for an account), interest charges are applied to wrong accounts. |
| **Risk Score** | **8 — Medium** |
| **Mitigation Strategy** | 1. Map each VSAM AIX to a relational database index + the appropriate query pattern. CARDAIX → `CREATE INDEX idx_cards_card_num ON cards(card_num)` with `SELECT` query. CXACAIX → `CREATE INDEX idx_xref_acct_id ON card_xref(acct_id)` with `SELECT * WHERE acct_id = ?`. 2. Verify AIX-equivalent queries return the same record sets as VSAM AIX browse operations by running both paths against the same data. 3. Pay special attention to CBACT04C's STARTBR/READNEXT loop through CXACAIX — translate to a parameterized SQL query with ORDER BY to match VSAM key ordering. |
| **Early Warning Indicators** | — Interest calculation finds different card count per account between M2 and Java implementations. — Card search (COCRDLIC) returns different results for the same search criteria. — Missing or duplicate records in AIX-equivalent queries compared to VSAM browse output. |

---

### Risk 9: Regulatory and Audit Trail Gaps During Migration

| Attribute | Details |
|-----------|---------|
| **Description** | Financial applications must maintain complete audit trails for regulatory compliance. During the migration, transaction records, account changes, and interest calculations exist in two systems (VSAM on M2 and PostgreSQL). Audit queries must be able to reconstruct the complete history of any account or transaction regardless of which system processed it. The migration also involves a period where the system of record shifts from VSAM to PostgreSQL, and this handoff must be documented and auditable. Additionally, report formats generated by CBTRN03C and CBSTM03A/B may be required by regulators in specific formats. |
| **Likelihood** | 3 — Moderate. Dual-system operation creates audit complexity, but is a known challenge with established practices. |
| **Impact** | 4 — High. Regulatory non-compliance can result in fines, mandated remediation, and reputational damage. |
| **Risk Score** | **12 — Medium** |
| **Mitigation Strategy** | 1. Implement a unified audit log that records all data changes regardless of source system (M2 or microservices). 2. Timestamp every record with the processing system identifier so auditors can determine which system processed each transaction. 3. Archive all VSAM sequential backups (GDG datasets: TRANSACT.BKUP, DALYREJS, SYSTRAN) to S3 with 7-year retention. 4. Ensure new report formats (from `reporting-service`) are approved by compliance before decommissioning CBTRN03C. 5. Maintain the ability to regenerate historical reports from archived data for the full regulatory retention period. |
| **Early Warning Indicators** | — Auditor cannot trace a transaction from creation through posting to interest calculation across both systems. — Report format changes rejected by compliance review. — Gaps in transaction sequence (missing TRAN-IDs) during system-of-record handoff. — Archived GDG data not accessible or corrupted in S3. |

---

### Risk 10: Team Skill Gap — COBOL Expertise Shortage

| Attribute | Details |
|-----------|---------|
| **Description** | Understanding the existing COBOL programs is critical for accurate migration. CardDemo intentionally incorporates diverse COBOL patterns: COPY REPLACING (39 invocations in COACTUPC), REDEFINES with OCCURS DEPENDING ON (CVEXPORT), COMP/COMP-3 packed decimal, 88-level condition names, EVALUATE statements, nested PERFORM loops, and IBM CICS API calls (EXEC CICS SEND MAP, READ FILE, STARTBR, READNEXT). Assembler modules (MVSWAIT, COBDATFT) add another skill dimension. If the migration team lacks COBOL expertise, they may misinterpret program behavior, skip edge cases, or make incorrect assumptions about data formats. |
| **Likelihood** | 4 — High. COBOL developer availability has been declining for decades. The average COBOL programmer age exceeds 55. Finding developers who understand both COBOL and modern Java/Spring frameworks is exceptionally difficult. |
| **Impact** | 3 — Moderate. Skill gaps slow the migration timeline, increase defect rates, and may require expensive external consultants. |
| **Risk Score** | **12 — Medium** |
| **Mitigation Strategy** | 1. Engage at least two COBOL-experienced developers as reviewers/advisors for the duration of the project (contract if necessary). 2. Invest in automated COBOL analysis tools that extract business rules, data flows, and program call graphs without requiring manual code reading. 3. Create a COBOL-to-Java pattern guide specific to CardDemo's coding conventions (COPY REPLACING patterns, BMS map handling, COMMAREA structure). 4. Use the existing CBEXPORT/CBIMPORT programs as Rosetta Stones — they read every entity type and demonstrate the correct interpretation of all COBOL data formats. 5. Build knowledge transfer sessions where COBOL advisors walk through the most complex programs (COACTUPC, CBACT04C, CBTRN02C). |
| **Early Warning Indicators** | — Rewritten Java code fails tests that pass on the COBOL version. — Team members unable to explain COPY REPLACING behavior in COACTUPC. — COMP-3 fields decoded incorrectly in test data. — Questions about COBOL behavior increase rather than decrease over time (indicates learning plateau). — Assembler routines (MVSWAIT, COBDATFT) treated as black boxes without understanding. |

---

## Risk Summary Matrix

| # | Risk | Likelihood | Impact | Score | Level |
|---|------|-----------|--------|-------|-------|
| 1 | Data Integrity Loss (VSAM→Relational) | 4 | 5 | **20** | Critical |
| 2 | Interest Calculation Equivalence | 4 | 5 | **20** | Critical |
| 3 | CICS Transaction Semantics Lost | 3 | 4 | **12** | Medium |
| 4 | Batch Window Elimination Disruption | 3 | 4 | **12** | Medium |
| 5 | Business Rule Misinterpretation | 4 | 4 | **16** | High |
| 6 | Performance Degradation | 3 | 3 | **9** | Medium |
| 7 | Scheduling Orchestration Loss | 3 | 4 | **12** | Medium |
| 8 | AIX Behavior Not Replicated | 2 | 4 | **8** | Medium |
| 9 | Regulatory Audit Trail Gaps | 3 | 4 | **12** | Medium |
| 10 | COBOL Expertise Shortage | 4 | 3 | **12** | Medium |

---

## Risk Heat Map

```
Impact
  5 │  ·    ·   ·   R1,R2  ·
  4 │  ·    R8  R3,R4,R7,R9  R5  ·
  3 │  ·    ·   R6  R10  ·
  2 │  ·    ·   ·   ·    ·
  1 │  ·    ·   ·   ·    ·
    └──────────────────────────
       1    2   3   4    5
                Likelihood
```

---

## Review Cadence

- **Weekly:** Review all High and Critical risks (R1, R2, R5) during project standup.
- **Bi-weekly:** Review all Medium risks during sprint retrospective.
- **Phase Gate:** Full risk register review at each phase transition (see CUTOVER_PLAN.md).
- **Ad-hoc:** Immediate review when any early warning indicator triggers.

Each review should assess:
1. Has the likelihood changed based on new information?
2. Has the mitigation strategy been activated? Is it effective?
3. Are early warning indicators being monitored? Any triggers?
4. Should any risk be escalated or de-escalated?
