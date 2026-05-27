# CardDemo Cutover Plan

## Overview

This plan sequences the CardDemo migration into five phases, ordered from lowest-risk/highest-value to highest-risk. Each phase specifies: programs migrated, data stores affected, integration bridges needed, rollback procedures, and acceptance criteria for advancing to the next phase.

---

## Phase 0: Foundation (Weeks 1–4)

### Objective
Establish the target platform, CI/CD pipelines, monitoring, and the replatformed COBOL runtime that will serve as the fallback during migration.

### Programs Migrated
None — infrastructure only.

### Actions
1. **Provision target environment:** Kubernetes cluster (EKS/GKE), PostgreSQL/Aurora database, message broker (Kafka/SQS), API gateway.
2. **Set up CI/CD:** Build pipelines for Java/Kotlin services, automated testing, deployment to staging and production.
3. **Replatform COBOL runtime:** Deploy CardDemo on AWS Mainframe Modernization (M2) or Micro Focus Enterprise Server as the reference implementation. This provides:
   - A running baseline to validate against
   - A fallback if any migrated service fails
   - A source for parallel-run comparisons
4. **Data replication pipeline:** Set up CDC (Change Data Capture) from VSAM files to PostgreSQL. All VSAM data is replicated to relational tables in near-real-time. This enables new services to read from the RDBMS while the COBOL programs continue writing to VSAM.
5. **Observability:** Deploy logging (ELK/CloudWatch), metrics (Prometheus/CloudWatch), distributed tracing (Jaeger/X-Ray).

### Data Stores Affected
- All VSAM files replicated to PostgreSQL (read replicas — COBOL remains the system of record)

### Integration Bridges
- CDC pipeline: VSAM → PostgreSQL (one-way sync)

### Rollback Plan
No production traffic changes — rollback is simply decommissioning the target environment.

### Acceptance Criteria
- [ ] AWS M2 (or equivalent) running CardDemo with all CICS transactions and batch jobs functional
- [ ] All VSAM data successfully replicated to PostgreSQL with <5 minute lag
- [ ] CI/CD pipeline deploying a sample Spring Boot service to production cluster
- [ ] Monitoring dashboards showing COBOL runtime health metrics

---

## Phase 1: Low-Risk Extractions (Weeks 5–12)

### Objective
Extract the three domains with isolated data stores: Identity/User Management, Transaction Type Reference Data, and Account Data Utilities. Build team confidence and establish patterns.

### Programs Migrated

| Legacy Program | New Service | Function |
|---|---|---|
| COSGN00C | `user-service` | Authentication (login → JWT) |
| COUSR00C | `user-service` | List users |
| COUSR01C | `user-service` | Add user |
| COUSR02C | `user-service` | Update user |
| COUSR03C | `user-service` | Delete user |
| COADM01C | `user-service` (API) + new web UI | Admin menu |
| COMEN01C | New web UI | User main menu |
| COTRTLIC | `reference-data-service` | List/update transaction types |
| COTRTUPC | `reference-data-service` | Transaction type maintenance |
| COBTUPDT | `reference-data-service` | Batch update transaction types |
| CBACT01C | Retire (replaced by SQL queries) | Read account data |
| CBACT02C | Retire (replaced by SQL queries) | Read card data |
| CBACT03C | Retire (replaced by SQL queries) | Read xref data |
| CBCUS01C | Retire (replaced by SQL queries) | Read customer data |

### Data Stores Affected

| Legacy Store | Migration Action | New Store |
|---|---|---|
| USRSEC (VSAM) | Full migration — VSAM decommissioned for this file | `users` table (PostgreSQL) |
| DB2 TRNTYPE | Schema migration to PostgreSQL | `transaction_types` table |
| DB2 TRNTYCAT | Schema migration to PostgreSQL | `transaction_categories` table |

### Integration Bridges

| Bridge | Purpose | Duration |
|---|---|---|
| **Auth Bridge:** CICS programs still using COMMAREA | New `user-service` issues JWT tokens. A thin CICS adapter program reads the JWT and populates COMMAREA fields (CDEMO-USER-ID, CDEMO-USER-TYPE) for remaining CICS programs. | Until all CICS programs are retired |
| **Menu Bridge:** CICS menu programs (COMEN01C, COADM01C) are replaced | New web UI presents menu options. Options still running on CICS are deep-linked to 3270 terminal emulator sessions. | Until all menu options are migrated |
| **Ref Data Bridge:** VSAM TRANTYPE/TRANCATG files still needed by batch | `reference-data-service` writes to PostgreSQL. A sync job copies from PostgreSQL back to VSAM files for batch programs. | Until batch programs are rewritten (Phase 4) |

### Rollback Plan
- **User Service:** Revert to CICS COSGN00C by disabling JWT auth and re-enabling VSAM-backed login. USRSEC data is maintained in both VSAM and PostgreSQL during a 2-week parallel-run period before VSAM decommission.
- **Reference Data Service:** Revert to CICS COTRTLIC/COTRTUPC and DB2 direct access. DB2 tables remain intact until PostgreSQL is validated.
- **Data Utilities:** No rollback needed — these are operational utilities, not production transactions.

### Acceptance Criteria
- [ ] Users can log in via new web UI, receive JWT, and access all remaining CICS transactions via terminal emulator deep links
- [ ] User CRUD operations (add/update/delete) work through new UI with same functionality as CICS screens
- [ ] Transaction type reference data CRUD works through new UI
- [ ] Auth bridge successfully translates JWT to COMMAREA for remaining CICS programs
- [ ] Ref data sync job maintains VSAM TRANTYPE/TRANCATG in sync with PostgreSQL (validated by diff)
- [ ] No regression in existing CICS transaction functionality
- [ ] 2-week parallel run of user auth (VSAM vs. PostgreSQL) shows 100% behavioral equivalence

---

## Phase 2: API Wrapping & Data Migration Tooling (Weeks 13–24)

### Objective
Wrap Account Management and Credit Card Management with APIs (strangler pattern). Build the data migration ETL pipelines. Migrate data import/export and reporting capabilities.

### Programs Migrated

| Legacy Program | New Service | Function |
|---|---|---|
| COACTVWC | `account-service` (read API) | View account → GET /api/accounts/{id} |
| COACTUPC | `account-service` (write API) — **strangler wrapper initially** | Update account → PUT /api/accounts/{id} |
| COCRDLIC | `card-service` (read API) | List cards → GET /api/cards |
| COCRDSLC | `card-service` (read API) | View card → GET /api/cards/{number} |
| COCRDUPC | `card-service` (write API) — **strangler wrapper initially** | Update card → PUT /api/cards/{number} |
| CBEXPORT | New ETL pipeline | Data export → Spring Batch job |
| CBIMPORT | New ETL pipeline | Data import → Spring Batch job |
| CORPT00C | `reporting-service` | Report trigger → async API call |

### Data Stores Affected

| Legacy Store | Migration Action | New Store |
|---|---|---|
| ACCTDAT (VSAM) | **Dual-write begins:** New services write to PostgreSQL; sync job writes back to VSAM for remaining COBOL programs | `accounts` table |
| CARDDAT (VSAM) | Dual-write begins | `cards` table |
| CCXREF (VSAM) | Dual-write begins | `card_xref` table |
| CXACAIX (VSAM AIX) | Replaced by database secondary index | Index on `card_xref.account_id` |
| CUSTDAT (VSAM) | Read-replicated to PostgreSQL (not yet migrated) | `customers` table (read replica) |

### Integration Bridges

| Bridge | Purpose | Duration |
|---|---|---|
| **Strangler Facade:** API gateway routes requests to new services; services initially delegate writes to CICS programs via CICS Web Services or COMMAREA adapter | Allows incremental replacement of CICS business logic | Until Phase 3 completes write migration |
| **Dual-Write Sync:** New services write to PostgreSQL. A sync job propagates changes back to VSAM for COBOL batch programs. | Keeps VSAM files current for batch processing | Until batch programs are rewritten (Phase 4) |
| **Report Bridge:** CORPT00C's TDQ-to-internal-reader mechanism replaced by API call that triggers async report generation | New reporting service handles job scheduling | Permanent replacement |

### Rollback Plan
- **Account/Card API Wrappers:** Disable API gateway routing; traffic falls back to CICS programs directly. VSAM remains the system of record during strangler phase, so no data loss.
- **Dual-Write Sync:** If sync fails, VSAM data is still authoritative. New services can read from VSAM via the existing CDC pipeline until sync is repaired.
- **ETL Pipelines:** Fall back to CBEXPORT/CBIMPORT COBOL programs (still available on replatformed runtime).
- **Reporting:** Fall back to CORPT00C on CICS for report submission.

### Acceptance Criteria
- [ ] Account view/update accessible via REST API with same data as CICS screens
- [ ] Card list/view/update accessible via REST API
- [ ] COACTUPC validation rules (SSN, phone, date, credit limit) replicated in Java and validated against COBOL output for 100% of test cases
- [ ] Dual-write sync maintains <1 minute lag between PostgreSQL and VSAM
- [ ] ETL pipeline successfully exports/imports customer data matching CBEXPORT/CBIMPORT output byte-for-byte
- [ ] Report generation via new API produces identical output to CICS-triggered batch
- [ ] VSAM ACCTDAT, CARDDAT, CCXREF data validated against PostgreSQL tables (row-count and checksum match)

---

## Phase 3: Core Transaction Processing & Payment (Weeks 25–36)

### Objective
Rewrite online transaction processing and bill payment. Migrate the transaction data store from VSAM to PostgreSQL as the system of record. Complete the account/card domain migration by replacing strangler wrappers with native implementations.

### Programs Migrated

| Legacy Program | New Service | Function |
|---|---|---|
| COTRN00C | `transaction-service` | List transactions → GET /api/transactions |
| COTRN01C | `transaction-service` | View transaction → GET /api/transactions/{id} |
| COTRN02C | `transaction-service` | Add transaction → POST /api/transactions |
| COBIL00C | `transaction-service` (or `payment-service`) | Bill payment → POST /api/payments |
| COACTUPC | `account-service` **(native impl replaces strangler wrapper)** | Full validation logic now in Java |
| COCRDUPC | `card-service` **(native impl replaces strangler wrapper)** | Full update logic now in Java |

### Data Stores Affected

| Legacy Store | Migration Action | New Store |
|---|---|---|
| TRANSACT (VSAM) | **Cutover:** PostgreSQL becomes system of record. VSAM maintained as read-only archive. | `transactions` table |
| ACCTDAT (VSAM) | **Cutover:** PostgreSQL becomes system of record. | `accounts` table |
| CARDDAT (VSAM) | **Cutover:** PostgreSQL becomes system of record. | `cards` table |
| CCXREF (VSAM) | **Cutover:** PostgreSQL becomes system of record. | `card_xref` table |
| CUSTDAT (VSAM) | Full migration to PostgreSQL. | `customers` table |

### Integration Bridges

| Bridge | Purpose | Duration |
|---|---|---|
| **Reverse Sync:** PostgreSQL → VSAM for batch programs not yet migrated | Batch transaction posting (CBTRN01C/02C) still reads VSAM files | Until Phase 4 |
| **Transaction API → Account API:** Transaction creation calls account-service to update balance | Replaces direct VSAM ACCTDAT write | Permanent |
| **Transaction API → Card API:** Transaction creation validates card via card-service | Replaces direct VSAM CARDDAT read | Permanent |

### Rollback Plan
- **Transaction Service:** Revert API gateway to route to CICS COTRN00C/01C/02C. Restore VSAM TRANSACT as system of record from PostgreSQL snapshot. The reverse-sync bridge ensures VSAM is current.
- **Account/Card Full Migration:** Revert to strangler wrappers (Phase 2 state) that delegate to CICS programs. VSAM data preserved via reverse sync.
- **Bill Payment:** Revert to CICS COBIL00C.

**Critical:** PostgreSQL → VSAM reverse-sync must be validated daily during this phase to ensure rollback is possible.

### Acceptance Criteria
- [ ] All online transaction operations (list, view, add) work via REST API
- [ ] Bill payment creates transaction and updates account balance atomically
- [ ] Transaction creation validates card status, account status, and credit limits — matching COBOL behavior for 100% of test scenarios
- [ ] COACTUPC's complete validation suite (25+ field validations) passes in Java implementation
- [ ] PostgreSQL is system of record for ACCTDAT, CARDDAT, CCXREF, TRANSACT, CUSTDAT
- [ ] CICS online programs fully retired (no traffic to CICS region for migrated transactions)
- [ ] End-to-end transaction flow: login → menu → add transaction → verify balance update → view transaction — all through new UI
- [ ] Performance: transaction API response time < 200ms at p99 (matching CICS response time)

---

## Phase 4: Batch Processing Migration (Weeks 37–48)

### Objective
Rewrite all batch transaction processing, interest calculation, and statement generation as Spring Batch jobs. Validate through parallel-run comparison against replatformed COBOL batch. Decommission VSAM files.

### Programs Migrated

| Legacy Program | New Service | Function |
|---|---|---|
| CBTRN01C | `transaction-service` (Spring Batch) | Post daily transactions |
| CBTRN02C | `transaction-service` (Spring Batch) | Post daily transactions (with rejections) |
| CBTRN03C | `reporting-service` (Spring Batch) | Transaction detail report |
| CBACT04C | `reporting-service` (Spring Batch) | Interest calculation |
| CBSTM03A | `reporting-service` (Spring Batch) | Statement generation (text + HTML) |
| CBSTM03B | `reporting-service` (Spring Batch) | Statement file I/O (merged into CBSTM03A replacement) |
| COBSWAIT | Retire | Timer utility (replaced by scheduler) |
| CSUTLDTC | Retire | Date validation (replaced by java.time) |

### Data Stores Affected

| Legacy Store | Migration Action | New Store |
|---|---|---|
| DALYTRAN (sequential) | Replaced by staging table or file upload | `daily_transactions` table |
| DALYREJS (sequential) | Replaced by rejected_transactions table | `rejected_transactions` table |
| TCATBALF (VSAM) | Migrated to PostgreSQL | `transaction_category_balances` table |
| DISCGRP (VSAM) | Migrated to PostgreSQL | `disclosure_groups` table |
| **All remaining VSAM files** | **Decommissioned** after parallel-run validation | PostgreSQL tables |

### Integration Bridges

| Bridge | Purpose | Duration |
|---|---|---|
| **Parallel-Run Harness:** Both COBOL batch (on M2) and Spring Batch run nightly on the same input data. Output compared automatically. | Validates correctness of batch rewrite | 4–8 weeks of parallel run |
| **Daily Transaction Ingest:** DALYTRAN sequential file replaced by file upload to S3 → Spring Batch reader | Input format adaptor | Permanent (until upstream systems modernized) |

### Rollback Plan
- **Batch Jobs:** Fall back to COBOL batch on replatformed runtime (M2). VSAM files maintained via reverse-sync until COBOL batch is fully validated as retired.
- **Interest Calculation:** This is the highest-risk batch job. Maintain COBOL version on standby for 3 billing cycles after cutover.
- **Statement Generation:** COBOL version on standby for 2 billing cycles.

### Acceptance Criteria
- [ ] Spring Batch daily posting processes same input as CBTRN01C/CBTRN02C and produces identical output (account balance updates, rejected transactions match)
- [ ] Interest calculation (CBACT04C replacement) matches COBOL output to the cent for 100% of accounts over 3 billing cycles
- [ ] Statement generation (CBSTM03A/B replacement) produces text and HTML output matching COBOL output (diff comparison)
- [ ] Transaction detail report matches CBTRN03C output
- [ ] Parallel-run comparison shows 0 discrepancies for 30 consecutive business days
- [ ] All JCL jobs have Spring Batch equivalents or are retired
- [ ] All VSAM files decommissioned (no remaining VSAM dependencies)
- [ ] AWS M2 / replatformed COBOL runtime placed in standby (not decommissioned yet)

---

## Phase 5: Optional Module Migration & Decommission (Weeks 49–60+)

### Objective
Migrate the Authorization module (IMS/DB2/MQ), complete VSAM/MQ/MQ decommission, and decommission the replatformed COBOL runtime.

### Programs Migrated

| Legacy Program | New Service | Function |
|---|---|---|
| COPAUA0C | `authorization-service` | Authorization request processing |
| COPAUS0C | `authorization-service` | Pending authorization summary |
| COPAUS1C | `authorization-service` | Pending authorization detail |
| COPAUS2C | `authorization-service` | Pending authorization purge |
| CBPAUP0C | `authorization-service` (scheduled job) | Batch purge expired authorizations |
| PAUDBLOD | Retire (replaced by database migration) | IMS DB load |
| PAUDBUNL | Retire | IMS DB unload |
| DBUNLDGS | Retire | GSAM unload |
| COACCT01 (VSAM/MQ) | `account-service` event stream | Account data extraction via MQ |
| CODATE01 (VSAM/MQ) | Retire | System date inquiry via MQ |

### Data Stores Affected

| Legacy Store | Migration Action | New Store |
|---|---|---|
| IMS DB (DBPAUTP0) | Migrated to PostgreSQL | `pending_authorizations` table |
| IMS DB (DBPAUTX0) | Migrated to PostgreSQL | `authorization_index` table |
| DB2 AUTHFRDS | Migrated to PostgreSQL | `authorization_fraud_scores` table |
| MQ queues | Replaced by Kafka topics or SQS queues | Event streams |

### Integration Bridges
None required — this is the final phase. All previous bridges are decommissioned.

### Rollback Plan
- Fall back to COBOL Authorization module on replatformed runtime (M2 + IMS + DB2 + MQ).
- This is the last use of the replatformed runtime — it remains on standby until this phase passes acceptance.

### Acceptance Criteria
- [ ] Authorization requests processed via new service matching COBOL behavior
- [ ] Pending authorization lifecycle (create, view summary/detail, purge) functional
- [ ] Batch purge job runs on schedule and produces same results as CBPAUP0C
- [ ] All MQ integrations replaced by Kafka/SQS
- [ ] All IMS databases decommissioned
- [ ] All DB2 tables migrated to PostgreSQL
- [ ] Replatformed COBOL runtime (AWS M2) decommissioned
- [ ] No remaining mainframe or mainframe-emulation dependencies
- [ ] Full end-to-end regression test suite passes

---

## Timeline Summary

```
Week:  1    5    13    25    37    49    60+
       │    │     │     │     │     │     │
       ▼    ▼     ▼     ▼     ▼     ▼     ▼
       ┌────┬─────┬─────┬─────┬─────┬─────┐
Phase: │ 0  │  1  │  2  │  3  │  4  │  5  │
       │Fnd │Lo-R │API  │Core │Batch│Optl │
       │    │     │Wrap │Tran │Migr │Decom│
       └────┴─────┴─────┴─────┴─────┴─────┘

Programs retired per phase:
  Phase 0:  0
  Phase 1: 14 (auth, admin, ref data, utilities)
  Phase 2:  8 (account/card APIs, export/import, reports)
  Phase 3:  6 (online transactions, bill payment, full account/card)
  Phase 4:  8 (all batch programs)
  Phase 5:  8 (authorization module, VSAM/MQ utilities)
  Total:   44
```

---

## Risk Mitigations by Phase

| Phase | Key Risk | Mitigation |
|---|---|---|
| 0 | Replatform fails to reproduce COBOL behavior | Run full regression test suite on M2; compare all batch outputs |
| 1 | Auth bridge breaks existing CICS programs | 2-week parallel run; gradual cutover by user group |
| 2 | Dual-write sync introduces data inconsistency | Checksum validation every 15 minutes; automated alerts on mismatch |
| 3 | Transaction processing regression in financial calculations | 100% test case coverage; parallel-run for 30 days |
| 4 | Interest calculation precision mismatch (COMP-3 vs. BigDecimal) | 3 billing cycle parallel run; cent-level comparison for all accounts |
| 5 | IMS data migration loses hierarchical relationships | Pre-migration data model mapping validated by domain experts |
