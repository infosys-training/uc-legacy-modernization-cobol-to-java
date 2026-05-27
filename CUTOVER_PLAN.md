# CardDemo Cutover Plan

## Overview

This plan sequences the CardDemo modernization into five phases, each building on the prior phase's deliverables. Every phase specifies the programs being migrated, affected data stores, required integration bridges, rollback procedures, and acceptance criteria.

The overall timeline assumes parallel workstreams where possible, with hard gates between phases where data dependencies exist.

---

## Phase 0 — Foundation (Weeks 1–4)

### Objective
Establish the modernization infrastructure, build regression test suites from production data, and set up the dual-run environment.

### Programs Migrated
None — this is infrastructure-only.

### Activities

| Activity | Details |
|:---------|:--------|
| Cloud infrastructure provisioning | AWS VPC, RDS PostgreSQL, ECS/EKS cluster, API Gateway, S3, CloudWatch |
| AWS M2 environment setup | Provision Micro Focus or Blu Age runtime for replatformed COBOL |
| CI/CD pipeline creation | GitHub Actions / CodePipeline for Java services + COBOL compilation |
| Test harness creation | Capture current VSAM file snapshots as golden datasets; build comparison framework |
| Data schema design | Design PostgreSQL schema mirroring VSAM record layouts (CVACT01Y → accounts table, etc.) |
| CDC bridge prototype | Build Change Data Capture adapter: VSAM ↔ PostgreSQL bidirectional sync |
| Monitoring & observability | Prometheus, Grafana, CloudWatch dashboards; distributed tracing with X-Ray |

### Data Stores Affected
- None modified; read-only access to capture golden test datasets
- New PostgreSQL database provisioned (empty schema)

### Integration Bridges Required
- **CDC Bridge (prototype):** VSAM file monitor → PostgreSQL writer (and reverse). Initially in test mode only.
- **API Gateway stub:** Route definitions for future services (returns 501 Not Implemented).

### Rollback Plan
No production changes — rollback is simply decommissioning cloud resources.

### Acceptance Criteria
- [ ] Cloud infrastructure passes security review
- [ ] AWS M2 runtime compiles and executes all 8 batch-compilable COBOL programs
- [ ] Golden test datasets captured for all 8 VSAM files + daily/monthly outputs
- [ ] PostgreSQL schema created with all tables matching VSAM record layouts
- [ ] CI/CD pipeline builds and deploys a skeleton Java service successfully
- [ ] CDC bridge passes integration test: write to VSAM → appears in PostgreSQL within 5 seconds

---

## Phase 1 — Low-Risk Rewrites & UI Foundation (Weeks 5–12)

### Objective
Rewrite the lowest-coupling, lowest-risk areas: Authentication, Admin/Menu navigation, Data Migration utilities, and infrastructure utilities. Stand up the modern web frontend.

### Programs Migrated

| Legacy Program | Target | Service |
|:---------------|:-------|:--------|
| COSGN00C (signon) | Java/Spring Security | `identity-service` |
| COUSR00C (list users) | Java/Spring REST | `identity-service` |
| COUSR01C (add user) | Java/Spring REST | `identity-service` |
| COUSR02C (update user) | Java/Spring REST | `identity-service` |
| COUSR03C (delete user) | Java/Spring REST | `identity-service` |
| COADM01C (admin menu) | Angular/React frontend | Web UI |
| COMEN01C (main menu) | Angular/React frontend | Web UI |
| CBEXPORT (export) | Java batch / AWS Glue | `migration-tools` |
| CBIMPORT (import) | Java batch / AWS Glue | `migration-tools` |
| COBSWAIT (timer utility) | Eliminated (platform-native) | N/A |
| CSUTLDTC (date utility) | java.time utilities | Shared library |

### Data Stores Affected

| Store | Action |
|:------|:-------|
| USRSEC VSAM | **Replaced** by PostgreSQL `users` table in `identity-service` |
| EXPORT.DATA PS | **Replaced** by JSON/CSV export format |

### Integration Bridges Required

| Bridge | Purpose |
|:-------|:--------|
| **Auth Bridge** | During transition, CICS programs that read USRSEC for session validation must be updated to call the `identity-service` API or continue reading USRSEC with a sync from PostgreSQL → USRSEC. Recommend: keep USRSEC as read replica with nightly sync from PostgreSQL until all CICS programs are retired. |
| **COMMAREA → JWT Adapter** | CICS programs receiving COMMAREA user context must accept JWT tokens via a CICS adapter program that decodes JWT and populates COMMAREA fields. |
| **BMS → Web UI Bridge** | The new web frontend communicates with the `identity-service` directly. Legacy 3270 users continue to use COSGN00C until all screens are migrated. Both paths are active simultaneously. |

### Rollback Plan
1. **Identity service failure:** Reactivate USRSEC as primary auth source. The nightly sync keeps it current.
2. **Web UI failure:** Users fall back to 3270 terminal access (CICS still running all non-migrated programs).
3. **Migration tools failure:** Revert to CBEXPORT/CBIMPORT COBOL programs (still compiled and available).
4. **Data rollback:** PostgreSQL `users` table can be rebuilt from USRSEC VSAM snapshot at any time.

### Acceptance Criteria
- [ ] `identity-service` passes all CRUD operations with response times < 200ms
- [ ] Login via web UI authenticates against `identity-service` and returns JWT
- [ ] CICS programs continue to function with USRSEC synced from PostgreSQL
- [ ] COMMAREA adapter correctly populates user fields from JWT for remaining CICS programs
- [ ] Web frontend renders main menu and admin menu with navigation to placeholder screens
- [ ] `migration-tools` successfully exports all master files to JSON format and re-imports with zero data loss
- [ ] All legacy 3270 signon/user-management functions still work (parallel run)
- [ ] Load test: 100 concurrent logins with < 500ms p99 latency

---

## Phase 2 — Core Domain APIs & Replatform Batch (Weeks 13–24)

### Objective
Build the Account, Customer, and Transaction services (strangler pattern). Replatform the batch pipeline to AWS M2. Establish the CDC bridge for dual-write operations.

### Programs Migrated

| Legacy Program | Strategy | Target |
|:---------------|:---------|:-------|
| COACTVWC (account view) | Strangler → API | `account-service` |
| COACTUPC (account update) | Strangler → API | `account-service` |
| CBACT01C (batch account read) | Replatform | AWS M2 runtime |
| COTRN00C (transaction list) | Strangler → API | `transaction-service` |
| COTRN01C (transaction view) | Strangler → API | `transaction-service` |
| COTRN02C (transaction add) | Strangler → API | `transaction-service` |
| CBTRN01C (batch post daily) | Replatform | AWS M2 runtime |
| CBTRN02C (batch post + balance) | Replatform | AWS M2 runtime |
| CBCUS01C (customer read) | Rewrite | `customer-service` |
| CLOSEFIL / OPENFIL / WAITSTEP | Replatform | AWS M2 batch orchestration |

### Data Stores Affected

| Store | Action |
|:------|:-------|
| ACCTDATA VSAM | **Dual-write**: CDC bridge syncs to PostgreSQL `accounts` table. API reads from PostgreSQL; batch still writes to VSAM on M2. |
| CUSTDATA VSAM | **Migrated** to PostgreSQL `customers` table. One-time load + CDC sync. |
| TRANSACT VSAM | **Dual-write**: Online adds go to PostgreSQL via API; batch posting on M2 still uses VSAM; CDC syncs both directions. |
| CARDXREF VSAM | **Read replica** in PostgreSQL. Populated via CDC from VSAM. Not yet the system of record. |
| DALYTRAN PS | Unchanged — consumed by replatformed batch on M2. |
| TCATBALF VSAM | Unchanged — written by replatformed CBTRN02C on M2. |

### Integration Bridges Required

| Bridge | Purpose |
|:-------|:--------|
| **VSAM ↔ PostgreSQL CDC (full)** | Bidirectional sync for ACCTDATA, TRANSACT, CARDXREF. Conflict resolution: last-writer-wins with timestamp. |
| **API Gateway routing** | Smart routing: online requests → new Java services; batch processing → M2 COBOL runtime. |
| **CICS-to-API adapter** | Remaining CICS programs (card mgmt, billing, reporting) that need account/transaction data can either: (a) continue reading VSAM (kept in sync), or (b) call new APIs via CICS web services. Recommend (a) for this phase to minimize CICS changes. |
| **Batch orchestration bridge** | Replace Control-M DAILY chain with AWS Step Functions calling M2 batch jobs. CLOSEFIL/OPENFIL semantics are handled by M2 file management. |

### Rollback Plan
1. **API service failure:** API Gateway routes traffic back to CICS programs. VSAM remains the source of truth (CDC ensures it's current).
2. **M2 batch failure:** Execute batch JCL on original mainframe (if still available) or from VSAM snapshots. GDG management reverts to mainframe controls.
3. **CDC bridge failure:** Pause CDC; revert to VSAM-only operation. Resync PostgreSQL from VSAM snapshot once bridge is fixed.
4. **Full phase rollback:** Decommission APIs and M2 batch; restore all traffic to original CICS/VSAM. PostgreSQL data is discardable.

### Acceptance Criteria
- [ ] `account-service` and `transaction-service` pass all CRUD operations
- [ ] CDC bridge achieves < 2 second latency for VSAM → PostgreSQL sync
- [ ] Batch pipeline (CLOSEFIL → POSTTRAN → TRANBKP → OPENFIL) completes on M2 with identical output to mainframe golden dataset
- [ ] Online transaction adds via API produce identical TRANSACT records as COTRN02C
- [ ] COACTUPC account updates via API match COACTUPC VSAM writes field-for-field
- [ ] Remaining CICS programs (card, billing, reporting) still function correctly against VSAM
- [ ] Daily batch run on M2 completes within 110% of mainframe elapsed time
- [ ] Parallel run: 2 weeks of dual processing (mainframe + M2) with daily reconciliation showing zero discrepancies
- [ ] Step Functions orchestration triggers and monitors M2 batch jobs correctly

---

## Phase 3 — Second-Wave Rewrites & Optional Modules (Weeks 25–36)

### Objective
Rewrite Card Management, Billing, Reporting, Reference Data, and the Authorization module. Migrate CARDXREF ownership to PostgreSQL. Retire most CICS programs.

### Programs Migrated

| Legacy Program | Strategy | Target |
|:---------------|:---------|:-------|
| COCRDLIC (card list) | Rewrite | `card-service` |
| COCRDSLC (card view) | Rewrite | `card-service` |
| COCRDUPC (card update) | Rewrite | `card-service` |
| COBIL00C (bill payment) | Rewrite | `billing-service` |
| CORPT00C (report trigger) | Rewrite | `reporting-service` |
| CBSTM03A (statement gen) | Rewrite | `reporting-service` |
| CBSTM03B (file processing) | Rewrite | `reporting-service` |
| CBTRN03C (transaction report) | Rewrite | `reporting-service` |
| COTRTUPC (tran type CRUD) | Rewrite | `reference-data-service` |
| COTRTLIC (tran type list) | Rewrite | `reference-data-service` |
| COBTUPDT (batch maintain) | Rewrite | `reference-data-service` |
| COPAUA0C (auth MQ trigger) | Strangler | `authorization-service` |
| COPAUS0C (auth summary) | Rewrite | `authorization-service` |
| COPAUS1C (auth details) | Rewrite | `authorization-service` |
| COPAUS2C | Rewrite | `authorization-service` |
| CBPAUP0C (auth purge) | Rewrite | `authorization-service` |

### Data Stores Affected

| Store | Action |
|:------|:-------|
| CARDDATA VSAM | **Migrated** to PostgreSQL `cards` table. System of record moves to PostgreSQL. |
| CARDXREF VSAM | **Migrated** to PostgreSQL `card_xref` table. BC-3 (`card-service`) becomes the owner. |
| TRANSACT VSAM | CDC continues; batch on M2 still writes VSAM. |
| Db2 TRNTYPE/TRNTYCAT | **Migrated** to PostgreSQL tables in `reference-data-service`. |
| IMS DB (DBPAUTP0) | **Migrated** to PostgreSQL tables in `authorization-service`. |
| Db2 AUTHFRDS | **Migrated** to PostgreSQL in `authorization-service`. |
| MQ queues | **Replaced** by SQS/SNS or Kafka topics. |
| STATEMNT.PS / .HTML | **Replaced** by PDF/HTML generated by `reporting-service` stored in S3. |

### Integration Bridges Required

| Bridge | Purpose |
|:-------|:--------|
| **MQ → SQS/Kafka adapter** | Temporary bridge for authorization request messages during transition from MQ to cloud messaging. |
| **IMS DB → PostgreSQL migration** | One-time data migration from hierarchical IMS DB to relational tables. Requires schema denormalization. |
| **CICS retirement adapter** | Remaining CICS transactions (if any) redirect to API Gateway → microservices. |
| **Report output bridge** | Legacy consumers expecting PS/HTML files on mainframe get S3 pre-signed URLs or SFTP delivery. |

### Rollback Plan
1. **Card service failure:** Re-enable COCRDLIC/COCRDSLC/COCRDUPC in CICS. Resync CARDDATA VSAM from PostgreSQL snapshot.
2. **Billing service failure:** Re-enable COBIL00C in CICS.
3. **Reporting service failure:** CORPT00C/CBSTM03A still available on M2 batch runtime.
4. **Authorization service failure:** Re-enable MQ-based flow; IMS DB still contains data (migration is additive, not destructive).
5. **Reference data failure:** Db2 tables still intact; MNTTRDB2/TRANEXTR JCL still functional on M2.

### Acceptance Criteria
- [ ] `card-service` CRUD operations match legacy COCRDLIC/COCRDSLC/COCRDUPC behavior
- [ ] Bill payment via `billing-service` produces identical transaction records to COBIL00C
- [ ] Statement generation produces PDF equivalent to STATEMNT.PS content (field-by-field match)
- [ ] Transaction report matches CBTRN03C output
- [ ] Reference data API serves transaction types/categories with < 50ms latency
- [ ] Authorization service processes MQ-compatible messages via SQS with < 500ms end-to-end latency
- [ ] IMS DB data fully migrated with zero record loss (validated row-by-row)
- [ ] MQ → SQS bridge handles message replay and dead-letter correctly
- [ ] CICS region serves only remaining batch-support transactions (CLOSEFIL, OPENFIL if still needed)
- [ ] Weekly reference data refresh replaced by real-time API (no more TRANEXTR batch extraction)
- [ ] 2-week parallel run for card, billing, and reporting functions with daily reconciliation

---

## Phase 4 — Batch Rewrite & CICS Decommission (Weeks 37–48)

### Objective
Rewrite the replatformed batch programs from COBOL to Java. Decommission CICS and the M2 COBOL runtime. PostgreSQL becomes the sole system of record.

### Programs Migrated

| Legacy Program | Strategy | Target |
|:---------------|:---------|:-------|
| CBTRN01C (batch post daily) | Rewrite | Spring Batch job in `transaction-service` |
| CBTRN02C (batch post + balance) | Rewrite | Spring Batch job in `transaction-service` |
| CBACT01C (account batch read) | Rewrite | Spring Batch job in `account-service` |
| CBACT04C (interest calc) | Rewrite | Spring Batch job in `interest-service` |
| COMBTRAN (SORT merge) | Rewrite | Java stream merge or SQL query |
| CBACT02C (card data read) | Retire | Functionality in `card-service` |
| CBACT03C (xref data read) | Retire | Functionality in `card-service` |

### Data Stores Affected

| Store | Action |
|:------|:-------|
| All VSAM files | **Decommissioned**. PostgreSQL is sole system of record. |
| DALYTRAN PS | **Replaced** by database table / S3 file ingestion. |
| GDG files (SYSTRAN, TRANSACT.BKUP, DALYREJS) | **Replaced** by database tables with temporal versioning or S3 archives. |
| TCATBALF VSAM | **Migrated** to PostgreSQL `category_balances` table. |
| DISCGRP VSAM | **Migrated** to PostgreSQL `disclosure_groups` table. |

### Integration Bridges Required

| Bridge | Purpose |
|:-------|:--------|
| **None — all bridges retired** | All data flows are now service-to-service via APIs or events. |

### Rollback Plan
1. **Batch rewrite failure:** Re-enable M2 COBOL batch runtime. VSAM files can be rebuilt from PostgreSQL via `migration-tools` reverse export.
2. **Interest calculation discrepancy:** Run CBACT04C on M2 in parallel with Java implementation; compare outputs for N cycles before decommissioning COBOL version.
3. **Full phase rollback:** Restore M2 runtime and CDC bridges from Phase 2/3. VSAM files rebuilt from PostgreSQL.

### Acceptance Criteria
- [ ] Spring Batch POSTTRAN equivalent produces identical transaction records to CBTRN02C (validated against golden dataset)
- [ ] Interest calculation (Java) matches CBACT04C output to the penny across all test accounts
- [ ] COMBTRAN merge produces identical sorted output to mainframe SORT utility
- [ ] Daily batch cycle completes within 50% of mainframe elapsed time (cloud advantage)
- [ ] Monthly interest calculation completes within 60% of mainframe elapsed time
- [ ] GDG replacement (S3 versioned archives) preserves 12 generations of history
- [ ] VSAM files successfully decommissioned with no runtime references
- [ ] CICS region shut down with no active transactions
- [ ] M2 COBOL runtime decommissioned
- [ ] 4-week parallel run for batch processing with daily reconciliation showing zero discrepancies
- [ ] Control-M scheduler fully replaced by AWS Step Functions / EventBridge

---

## Phase 5 — Optimization & Cleanup (Weeks 49–56)

### Objective
Optimize the modernized system, remove all legacy bridges, finalize the cloud-native architecture.

### Activities

| Activity | Details |
|:---------|:--------|
| Performance optimization | Database indexing, query optimization, connection pooling, caching |
| Security hardening | OAuth 2.0 / OIDC integration, API rate limiting, WAF rules |
| Remove CDC bridges | All bidirectional sync adapters decommissioned |
| Remove COMMAREA adapters | All JWT/COMMAREA translation layers removed |
| Remove M2 runtime | Cloud COBOL runtime terminated |
| Archive legacy artifacts | COBOL source, JCL, BMS maps archived to S3 Glacier for compliance |
| Documentation update | API documentation (OpenAPI), architecture diagrams, runbooks |
| Disaster recovery testing | Full DR drill: failover to secondary region |
| Load testing | Full system load test at 3x expected peak volume |

### Rollback Plan
No legacy fallback available after this phase. Standard cloud disaster recovery applies (multi-AZ, database replication, backup/restore).

### Acceptance Criteria
- [ ] All legacy integration bridges removed
- [ ] System passes full load test at 3x peak volume with < 1s p99 latency
- [ ] DR failover completes within RTO (< 15 minutes)
- [ ] RPO verified at < 1 minute (database replication lag)
- [ ] Security penetration test passes with no critical/high findings
- [ ] API documentation published and accessible
- [ ] Legacy COBOL/JCL/BMS archived to S3 Glacier
- [ ] Cost baseline established and within budget projections
- [ ] Operations team trained and runbooks validated
- [ ] Mainframe contract termination initiated

---

## Phase Summary Timeline

```
Week:  1    4    8    12   16   20   24   28   32   36   40   44   48   52   56
       │    │    │    │    │    │    │    │    │    │    │    │    │    │    │
Ph 0:  ├────┤ Foundation
Ph 1:       ├─────────┤ Low-Risk Rewrites & UI
Ph 2:                 ├──────────────────┤ Core APIs & Batch Replatform
Ph 3:                                    ├──────────────────┤ 2nd Wave & Modules
Ph 4:                                                       ├──────────────┤ Batch Rewrite
Ph 5:                                                                      ├────┤ Optimize
       │    │    │    │    │    │    │    │    │    │    │    │    │    │    │
       Gate0 Gate1    Gate2     Gate3          Gate4         Gate5    Gate6 Done
```

### Phase Gates

| Gate | Criteria |
|:-----|:---------|
| Gate 0 | Infrastructure ready; golden test datasets captured |
| Gate 1 | Identity service live; web UI serves login; migration tools functional |
| Gate 2 | Account + Transaction APIs serving online traffic; M2 batch running |
| Gate 3 | 2-week parallel run for Phase 2 shows zero discrepancies |
| Gate 4 | Card, Billing, Reporting, Authorization services live; MQ retired |
| Gate 5 | Batch fully rewritten in Java; VSAM decommissioned; CICS shut down |
| Gate 6 | All bridges removed; load test passed; DR verified |
