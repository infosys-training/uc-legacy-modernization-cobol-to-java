# Cutover Plan

## Overview

This plan sequences the CardDemo migration into 6 phases over 32–42 weeks. Each phase specifies which programs migrate, affected data stores, integration bridges, rollback plans, and acceptance criteria.

**Guiding principles:**
- Lowest-risk, highest-value extractions first
- Never break production — always have a rollback path
- Dual-run periods validate parity before full cutover
- Each phase delivers independently usable functionality

---

## Phase Summary

| Phase | Name | Duration | Programs | Risk |
|-------|------|----------|----------|------|
| P0 | Foundation & Infrastructure | 3–4 weeks | 0 (platform setup) | Very Low |
| P1 | Quick Wins: Auth + Reporting Pilot | 4–5 weeks | 11 programs | Low |
| P2 | DB2 Programs + Card Management | 5–7 weeks | 6 programs | Low-Medium |
| P3 | Core: Account + Transaction | 8–10 weeks | 9 programs | High |
| P4 | Authorization Engine (IMS/MQ) | 6–8 weeks | 8 programs | Very High |
| P5 | Batch Pipeline + Decommission | 4–5 weeks | 7 programs + retire | Medium |

**Total: 30–39 weeks (+ 2–3 weeks buffer = 32–42 weeks)**

---

## Phase 0: Foundation & Infrastructure

**Duration:** 3–4 weeks
**Objective:** Establish the target platform, CI/CD, observability, and shared libraries before migrating any business logic.

### Deliverables

| Item | Description |
|------|-------------|
| Cloud infrastructure | AWS VPC, Aurora PostgreSQL, SQS queues, ECS/EKS cluster |
| CI/CD pipeline | GitHub Actions: build → test → deploy (staging + prod) |
| Shared libraries | `carddemo-commons` (DTOs, error handling), `carddemo-validation` (state/ZIP/phone/date) |
| Schema foundation | Flyway baseline: all target tables created (empty) |
| Observability | Prometheus + Grafana + CloudWatch; distributed tracing (X-Ray) |
| API Gateway | Spring Cloud Gateway with route stubs for each future service |
| Test infrastructure | EBCDIC sample data converted to PostgreSQL seed scripts |
| COBOL baseline | Run all batch programs via GnuCOBOL; capture output as golden files |

### Integration Bridges
- None required (no production traffic routed yet)

### Rollback Plan
- Infrastructure-as-code (Terraform) — destroy and recreate
- No production impact (greenfield environment)

### Acceptance Criteria
- [ ] All target PostgreSQL tables created via Flyway migration
- [ ] CI/CD pipeline deploys sample Spring Boot service to staging
- [ ] GnuCOBOL baseline outputs generated for 8 compilable batch programs
- [ ] Monitoring dashboards operational
- [ ] Shared libraries published to artifact repository

---

## Phase 1: Quick Wins — Auth + Reporting Pilot

**Duration:** 4–5 weeks
**Objective:** Deliver two low-risk, high-visibility migrations that prove the approach and build team confidence.

### Programs Migrating

| Program | LOC | Target Service | Strategy |
|---------|-----|---------------|----------|
| COSGN00C | 520 | user-auth-service | Rewrite |
| COADM01C | 340 | user-auth-service | Rewrite |
| COMEN01C | 370 | user-auth-service | Rewrite |
| COUSR00C | 505 | user-auth-service | Rewrite |
| COUSR01C | 556 | user-auth-service | Rewrite |
| COUSR02C | 457 | user-auth-service | Rewrite |
| COUSR03C | 400 | user-auth-service | Rewrite |
| CBSTM03A | 924 | reporting-service | Rewrite |
| CBSTM03B | — | reporting-service | Rewrite (I/O module) |
| CBTRN03C | 686 | reporting-service | Rewrite |
| CORPT00C | 600 | reporting-service | Rewrite |

### Data Stores Affected

| Store | Action | Migration Method |
|-------|--------|-----------------|
| USRSEC (VSAM) | Migrate to PostgreSQL `users` table | One-time ETL from EBCDIC data |
| Statement output files | Replace with PDF/HTML generation | Thymeleaf templates |
| Report output files | Replace with database-stored reports | PostgreSQL + S3 |

### Integration Bridges

| Bridge | Purpose | Duration |
|--------|---------|----------|
| COMMAREA → JWT adapter | Translate COBOL COMMAREA session to JWT for services still on CICS | Until Phase 3 complete |
| USRSEC read-only VSAM copy | Maintain VSAM copy for CICS programs not yet migrated | Until all online programs migrated |
| Report file → JCL internal reader | CORPT00C currently submits JCL; Java version triggers Spring Batch directly | Permanent replacement |

### Rollback Plan
- **user-auth-service:** Route CICS transactions back to COSGN00C (CICS XCTL still deployed)
- **reporting-service:** Revert to JCL-triggered CBSTM03A batch
- **Data:** USRSEC VSAM file maintained in read-only mode; reverse sync PostgreSQL → VSAM if needed
- **Timeline:** Rollback executable within 30 minutes (traffic routing change only)

### Acceptance Criteria
- [ ] All user CRUD operations functional via REST API
- [ ] JWT authentication working; passwords migrated from plaintext to bcrypt
- [ ] Admin and regular user roles enforced
- [ ] Statement generation produces output matching GnuCOBOL baseline (byte-comparable)
- [ ] Transaction report (CBTRN03C equivalent) matches baseline output
- [ ] Response time ≤ 200ms for auth operations (p95)
- [ ] 2-week shadow run with zero discrepancies before CICS sign-on disabled
- [ ] All existing user accounts successfully migrated

---

## Phase 2: DB2 Programs + Card Management

**Duration:** 5–7 weeks
**Objective:** Migrate the DB2-based transaction type programs (lowest-effort translation) and the credit card management module.

### Programs Migrating

| Program | LOC | Target Service | Strategy |
|---------|-----|---------------|----------|
| COTRTLIC | 2,098 | transaction-type-service | Rewrite |
| COTRTUPC | 1,702 | transaction-type-service | Rewrite |
| COBTUPDT | ~500 | transaction-type-service | Rewrite (batch) |
| COCRDLIC | 1,459 | card-service | Rewrite |
| COCRDSLC | 791 | card-service | Rewrite |
| COCRDUPC | 1,560 | card-service | Rewrite |

### Data Stores Affected

| Store | Action | Migration Method |
|-------|--------|-----------------|
| DB2 TRANSACTION_TYPE | Migrate to PostgreSQL | SQL export → Flyway migration |
| DB2 TRANSACTION_CATEGORY | Migrate to PostgreSQL | SQL export → Flyway migration |
| CARDFILE (VSAM) | Migrate to PostgreSQL `cards` table | ETL from EBCDIC |
| CARDXREF (VSAM) | Migrate to PostgreSQL `card_xref` table | ETL from EBCDIC |

### Integration Bridges

| Bridge | Purpose | Duration |
|--------|---------|----------|
| DB2 → PostgreSQL sync | Keep DB2 tables updated for any remaining COBOL consumers | Until Phase 3 (CBTRN02C reads TRANTYPE) |
| CARDXREF dual-read | Account programs still read CARDXREF from VSAM | Until Phase 3 |
| CICS BMS → API adapter | Card list/update screens served by Java but presented via 3270 emulator | Optional transition UX |

### Rollback Plan
- **transaction-type-service:** Revert routing to COTRTLIC/COTRTUPC CICS programs
- **card-service:** Revert to COCRDLIC/COCRDSLC/COCRDUPC on CICS
- **Data:** PostgreSQL → DB2 reverse sync for transaction types; PostgreSQL → VSAM for card data
- **Timeline:** 1-hour rollback (requires DB2 sync verification)

### Acceptance Criteria
- [ ] All CRUD operations on transaction types functional via REST
- [ ] DB2 cursor-based pagination replicated with Spring Data (same page sizes, same sort order)
- [ ] Cascading delete behavior preserved (COTRTUPC logic)
- [ ] Card list pagination matches VSAM STARTBR/READNEXT behavior
- [ ] Card update validates all fields per COCRDUPC business rules
- [ ] Integration test: create transaction type → use in transaction → appears in reports
- [ ] Load test: 100 concurrent users, p99 < 500ms
- [ ] 1-week parallel run with output comparison (zero mismatches)

---

## Phase 3: Core — Account + Transaction

**Duration:** 8–10 weeks
**Objective:** Migrate the most critical and most coupled programs — account management and transaction processing. This is the highest-risk phase.

### Programs Migrating

| Program | LOC | Target Service | Strategy |
|---------|-----|---------------|----------|
| COACTUPC | 4,236 | account-service | Strangler → Rewrite |
| COACTVWC | 887 | account-service | Rewrite |
| COACCT01 | 374 | account-service | Rewrite (MQ adapter) |
| COTRN00C | 514 | transaction-service | Rewrite |
| COTRN01C | 430 | transaction-service | Rewrite |
| COTRN02C | 620 | transaction-service | Rewrite |
| COBIL00C | 535 | transaction-service | Rewrite |
| CBTRN01C | 712 | transaction-service | Rewrite (batch) |
| CBTRN02C | 1,247 | transaction-service | Rewrite (batch) |

### Data Stores Affected

| Store | Action | Migration Method |
|-------|--------|-----------------|
| ACCTFILE (VSAM) | Migrate to PostgreSQL `accounts` table | ETL + dual-write during transition |
| CUSTFILE (VSAM) | Migrate to PostgreSQL `customers` table | ETL |
| TRANSACT (VSAM) | Migrate to PostgreSQL `transactions` table | ETL + dual-write |
| DALYTRAN (VSAM) | Migrate to PostgreSQL `daily_transactions` table | ETL |
| TCATBALF (VSAM) | Migrate to PostgreSQL `tran_cat_balances` table | ETL |
| DISCGRP (VSAM) | Migrate to PostgreSQL `discount_groups` table | ETL |

### Integration Bridges

| Bridge | Purpose | Duration |
|--------|---------|----------|
| **ACCTFILE dual-write** | Both COBOL and Java write to VSAM AND PostgreSQL | 4 weeks (shadow period) |
| **TRANSACT dual-write** | Transaction posting writes to both stores | 4 weeks |
| **SQS FIFO sync queue** | Maintains VSAM↔PostgreSQL consistency during dual-write | Until Phase 4 complete |
| **Reconciliation job** | Hourly: count + checksum comparison between VSAM and PostgreSQL | Until decommission |
| **Account API for auth** | Authorization engine (still on COBOL) needs account data via API | Until Phase 4 |
| **VSAM snapshot backup** | Every 4 hours during dual-write for rollback safety | During dual-write only |

### Sub-phases

**Phase 3a (weeks 1–3): Account View + Transaction Read-Only**
- Deploy `account-service` with read operations only (COACTVWC equivalent)
- Deploy `transaction-service` with list/view only (COTRN00C/01C)
- Route read traffic to Java; writes still go to COBOL
- Validates data migration accuracy under real load

**Phase 3b (weeks 4–6): Account Update + Transaction Add**
- Enable write operations (COACTUPC, COTRN02C, COBIL00C equivalents)
- Activate dual-write bridge
- Shadow mode: Java writes in parallel with COBOL; compare results

**Phase 3c (weeks 7–8): Batch Migration + Cutover**
- Migrate CBTRN02C (transaction posting) to Spring Batch
- Run both COBOL and Java batch for 1 week; compare output
- Cut over batch pipeline to Java once outputs match
- Disable COBOL writes; Java becomes system of record

**Phase 3d (weeks 9–10): Stabilization**
- Monitor for 2 weeks with Java as primary
- VSAM kept as read-only backup
- Reconciliation job confirms ongoing parity

### Rollback Plan
- **Phase 3a/3b:** Immediate rollback — COBOL still active, Java is shadow-only
- **Phase 3c (post-cutover):** Reverse sync PostgreSQL → VSAM (tested in advance); re-enable COBOL batch
- **ACCTFILE rollback:** Restore from 4-hourly VSAM snapshots; apply PostgreSQL delta
- **Timeline:** 2-hour rollback (requires VSAM restore + route change)
- **Decision point:** If reconciliation shows >0.01% mismatch rate, auto-rollback triggered

### Acceptance Criteria
- [ ] All 359 branching paths in COACTUPC covered by automated tests
- [ ] Field validation (SSN, state code, ZIP, phone, date) matches COBOL behavior exactly
- [ ] Dual-write reconciliation shows 0 mismatches for 7 consecutive days
- [ ] CBTRN02C Spring Batch job completes within 110% of COBOL runtime
- [ ] Daily batch pipeline (post → interest calc → statement) runs end-to-end
- [ ] Bill payment (COBIL00C) creates transactions and updates balances atomically
- [ ] Load test: 500 concurrent users, p99 < 1s for account updates
- [ ] Zero data loss during cutover window
- [ ] VSAM backup/restore procedure tested and documented

---

## Phase 4: Authorization Engine (IMS/MQ)

**Duration:** 6–8 weeks
**Objective:** Replace the most architecturally complex subsystem — the IMS/DB2/MQ authorization decision engine — using the Strangler pattern.

### Programs Migrating

| Program | LOC | Target Service | Strategy |
|---------|-----|---------------|----------|
| COPAUA0C | 1,100 | authorization-service | Strangler |
| COPAUS0C | 680 | authorization-service | Strangler |
| COPAUS1C | 520 | authorization-service | Strangler |
| COPAUS2C | 340 | authorization-service | Strangler |
| CBPAUP0C | 450 | authorization-service | Rewrite (batch) |
| PAUDBLOD | 280 | authorization-service | Retire (one-time load) |
| PAUDBUNL | 250 | authorization-service | Retire (replaced by SQL export) |
| DBUNLDGS | 220 | authorization-service | Retire (replaced by SQL export) |

### Data Stores Affected

| Store | Action | Migration Method |
|-------|--------|-----------------|
| IMS DBPAUTP0 | Migrate to PostgreSQL (2 tables) | Hierarchical → relational mapping |
| DB2 AUTHFRDS (fraud flags) | Migrate to PostgreSQL `fraud_flags` table | SQL export |
| MQ Auth Request Queue | Replace with SQS FIFO | Message format translation layer |
| MQ Auth Reply Queue | Replace with SQS FIFO | Message format translation layer |

### IMS Hierarchical-to-Relational Mapping

```
IMS DBPAUTP0 (Hierarchical)          PostgreSQL (Relational)
┌─────────────────────┐              ┌──────────────────────────┐
│ Root: AUTH_SUMMARY   │    ──►      │ auth_summaries            │
│   KEY: ACCT-ID       │              │   id, acct_id, status,   │
│   STATUS, LAST-AUTH  │              │   last_auth_date, count  │
├─────────────────────┤              └──────────────────────────┘
│ Child: AUTH_DETAIL   │    ──►      ┌──────────────────────────┐
│   KEY: AUTH-ID       │              │ auth_details              │
│   AMOUNT, DATE, RSLT │              │   id, summary_id (FK),   │
│                      │              │   amount, date, result   │
└─────────────────────┘              └──────────────────────────┘
```

### Integration Bridges

| Bridge | Purpose | Duration |
|--------|---------|----------|
| **MQ↔SQS dual-publish** | Both MQ and SQS receive auth requests; responses go to both | 2 weeks |
| **IMS read replica** | Java service reads from PostgreSQL; COBOL still reads IMS | Until COBOL auth retired |
| **Message format translator** | Converts COBOL fixed-length records (CCPAURQY) ↔ JSON | Permanent (part of service) |
| **Correlation ID bridge** | Maps MQ correlation IDs to SQS message attributes | During dual-run |

### Sub-phases

**Phase 4a (weeks 1–2): Data Migration + Read Path**
- Migrate IMS data to PostgreSQL tables (PAUDBLOD outputs → SQL INSERT)
- Deploy authorization-service with read-only endpoints
- COPAUS0C/1C screen equivalents served by Java (browse auth history)

**Phase 4b (weeks 3–4): SQS Adapter + Shadow Mode**
- Deploy SQS adapter alongside MQ
- Java service processes auth requests in shadow mode (reads from SQS, compares decision to COBOL)
- Log any decision mismatches for analysis

**Phase 4c (weeks 5–6): Cut Over Decision Engine**
- Java auth decisions become primary (SQS)
- COBOL COPAUA0C becomes fallback (MQ path kept for rollback)
- Monitor decision parity and latency

**Phase 4d (weeks 7–8): Decommission COBOL Auth**
- Disable MQ queues and IMS access
- Retire PAUDBLOD/PAUDBUNL/DBUNLDGS (replaced by SQL)
- Migrate CBPAUP0C (purge expired) to scheduled Spring Batch job

### Rollback Plan
- **Phase 4a/4b:** No production impact — Java is shadow-only
- **Phase 4c:** Route auth requests back to MQ (COBOL COPAUA0C); IMS still running
- **Phase 4d:** Cannot easily rollback IMS decommission — maintain IMS read-only for 30 days post-cutover
- **Timeline:** 30-minute rollback for Phase 4c (MQ route switch); Phase 4d rollback requires IMS restart (4 hours)

### Acceptance Criteria
- [ ] Auth decision parity: Java matches COBOL decision for 99.99% of test cases
- [ ] Sub-second auth response time (p95 < 500ms, p99 < 1s)
- [ ] Fraud flag detection (COPAUS2C DB2 insert) working in Java
- [ ] Expired auth purge (CBPAUP0C) runs daily as Spring Batch
- [ ] IMS data fully represented in PostgreSQL (row count parity)
- [ ] MQ↔SQS message delivery: zero message loss during transition
- [ ] 2-week shadow run with <0.01% decision mismatch rate
- [ ] DL/I operations (GU, GN, GNP, DLET, REPL, ISRT) all covered by integration tests
- [ ] Load test: 1000 auth requests/minute sustained

---

## Phase 5: Batch Pipeline Consolidation + Decommission

**Duration:** 4–5 weeks
**Objective:** Migrate remaining batch programs, consolidate the full daily pipeline under Spring Batch + Step Functions, and decommission COBOL infrastructure.

### Programs Migrating

| Program | LOC | Target | Strategy |
|---------|-----|--------|----------|
| CBACT01C | 450 | account-service (batch) | Rewrite |
| CBACT02C | 200 | account-service (batch) | Rewrite |
| CBACT03C | 200 | account-service (batch) | Rewrite |
| CBACT04C | 1,050 | transaction-service (batch) | Rewrite |
| CBCUS01C | 200 | account-service (batch) | Rewrite |
| CBEXPORT | 579 | data-migration-tools | Retire |
| CBIMPORT | 484 | data-migration-tools | Retire |
| COBSWAIT | 38 | (utility) | Retire |
| CSUTLDTC | 150 | carddemo-validation library | Already migrated |

### Data Stores Affected

| Store | Action |
|-------|--------|
| All remaining VSAM files | Final decommission (read-only since Phase 3) |
| JCL jobs | Replace with AWS Step Functions workflow definitions |
| Control-M schedules | Replace with EventBridge Scheduler |

### Integration Bridges

| Bridge | Purpose | Duration |
|--------|---------|----------|
| None | All programs on Java platform by this phase | — |

### Batch Pipeline Target State

```
AWS Step Functions (Daily Workflow)
│
├── Step 1: Transaction Posting (Spring Batch — was CBTRN02C, migrated in P3)
├── Step 2: Interest Calculation (Spring Batch — CBACT04C)
│     └── Reads discount groups, calculates per-account interest
├── Step 3: Statement Generation (Spring Batch — CBSTM03A, migrated in P1)
└── Step 4: Transaction Report (Spring Batch — CBTRN03C, migrated in P1)
```

### Decommission Checklist

| Item | Action | Validation |
|------|--------|-----------|
| VSAM clusters | Delete IDCAMS definitions | Confirm no active readers for 30 days |
| CICS programs | Remove from CSD (DFHCSDUP) | Confirm zero transaction counts for 30 days |
| IMS database | Archive and delete | Confirm PostgreSQL data complete |
| MQ queues | Delete queue definitions | Confirm zero queue depth for 30 days |
| DB2 tables | Archive and drop | Confirm PostgreSQL data complete |
| JCL jobs | Archive to repository | Confirm Step Functions running successfully |
| Control-M schedules | Deactivate | Confirm EventBridge schedules active |
| Mainframe LPAR | Release capacity | Confirm all workloads migrated |

### Rollback Plan
- **Batch programs:** Revert Step Functions to trigger JCL (Control-M still configured for 30 days)
- **Decommission:** 30-day observation period before permanent deletion of any resource
- **VSAM files:** Maintain read-only copies on tape/S3 for 90 days post-decommission

### Acceptance Criteria
- [ ] CBACT04C (interest calculation) produces identical results to COBOL baseline
- [ ] Full daily pipeline completes within SLA (measured against mainframe timing)
- [ ] Step Functions orchestration handles all error scenarios (retry, skip, alert)
- [ ] CBEXPORT/CBIMPORT functionality replaced by native PostgreSQL export/import
- [ ] 30 consecutive days of Java-only batch pipeline without intervention
- [ ] Zero CICS transactions routed to COBOL programs for 30 days
- [ ] All VSAM files confirmed read-only (no writes) for 30 days
- [ ] Cost reduction validated: mainframe charges reduced by target amount
- [ ] Knowledge transfer complete: operations team trained on new platform

---

## Timeline Visualization

```
Week:  1    4    8    12   16   20   24   28   32   36   40
       │────│────│────│────│────│────│────│────│────│────│
P0:    ████                                              Foundation
P1:         ████████                                     Auth + Reports
P2:                  ██████████                          DB2 + Cards
P3:                            ████████████████████      Account + Txn
P4:                                          ████████████  Authorization
P5:                                                  ████████ Batch + Decom
       │────│────│────│────│────│────│────│────│────│────│
```

---

## Cross-Phase Dependencies

| Dependency | Blocks | Resolution |
|------------|--------|-----------|
| P0 (infra) must complete | P1 start | Sequential |
| P1 (user-auth) provides JWT | P2, P3, P4 auth | P1 first |
| P2 (card CARDXREF migration) | P3 account reads CARDXREF | P2 before P3 |
| P3 (TRANSACT migration) | P4 auth reads transactions | P3 before P4 |
| P3 (ACCTFILE migration) | P5 batch interest calc | P3 before P5 |
| P1 (report baseline) | P5 batch pipeline validation | P1 baseline available |

---

## Team Structure Recommendation

| Role | Phase 0 | Phase 1 | Phase 2 | Phase 3 | Phase 4 | Phase 5 |
|------|---------|---------|---------|---------|---------|---------|
| COBOL SME | 0.5 | 0.5 | 0.5 | 1.0 | 1.0 | 0.5 |
| Java/Spring Developers | 2 | 3 | 3 | 4 | 3 | 2 |
| DevOps/Cloud Engineer | 1 | 0.5 | 0.5 | 0.5 | 0.5 | 1 |
| QA/Test Engineer | 0.5 | 1 | 1 | 2 | 1 | 1 |
| IMS/MQ Specialist | — | — | — | — | 1 | — |
| **Total FTE** | **4** | **5** | **5** | **7.5** | **6.5** | **4.5** |

---

## Go/No-Go Decision Points

| Gate | Between | Decision Criteria |
|------|---------|-------------------|
| Gate 1 | P0 → P1 | Infrastructure operational; CI/CD working; baseline captured |
| Gate 2 | P1 → P2 | Auth working in production; report output matches baseline |
| Gate 3 | P2 → P3 | Card operations working; DB2 migration verified; team ready for high-risk phase |
| Gate 4 | P3 → P4 | Account + Transaction services stable for 2 weeks; dual-write reconciliation clean |
| Gate 5 | P4 → P5 | Auth decisions matching at 99.99%; MQ fully replaced by SQS |
| Gate 6 | P5 → Decommission | 30 days zero COBOL activity; all batch SLAs met; team sign-off |
