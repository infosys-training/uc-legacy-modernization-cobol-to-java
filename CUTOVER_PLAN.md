# Cutover Plan — CardDemo COBOL-to-Java Migration

> Phased migration sequence with program mapping, data store impacts, integration bridges, rollback plans, and acceptance criteria.

---

## 1. Migration Principles

1. **Never go dark** — The production system must remain operational throughout migration. No big-bang cutover.
2. **Prove before you switch** — Each phase runs the Java service in shadow mode (parallel) before cutting over live traffic.
3. **Rollback is mandatory** — Every phase has a documented rollback procedure that restores the previous state within 30 minutes.
4. **Data owns the timeline** — Data migration complexity (not code complexity) determines phase sequencing.
5. **Test parity first** — No cutover until Java output matches COBOL output for the same input dataset.

---

## 2. Phase Overview

| Phase | Name | Duration | Programs Migrated | Key Risk |
|-------|------|----------|-------------------|----------|
| 0 | Foundation | 3–4 weeks | 0 (infrastructure only) | Schema design errors |
| 1 | Quick Wins | 4–5 weeks | 7 (User Admin + Statement Gen) | Security model change |
| 2 | Database Bridge | 5–7 weeks | 6 (DB2 programs + Card Management) | Dual-write consistency |
| 3 | Core Business | 8–10 weeks | 9 (Account + Transaction Processing) | ACCTFILE contention, validation parity |
| 4 | Integration | 6–8 weeks | 8 (Authorization IMS/MQ) | IMS data migration, MQ message format |
| 5 | Batch Pipeline | 4–5 weeks | 6 (Remaining batch + Reporting) | Batch output parity |
| 6 | Decommission | 2–3 weeks | 8 (Utilities + Data Migration → Retire) | Residual dependencies |

**Total estimated duration: 32–42 weeks (8–10 months)**

---

## 3. Phase 0 — Foundation (Weeks 1–4)

### Objective
Set up the target architecture, database schema, CI/CD pipeline, and shared libraries before migrating any program.

### Programs Migrated
None — infrastructure only.

### Deliverables

| Deliverable | Description |
|-------------|-------------|
| PostgreSQL database | Schema for all 15 target tables (see DOMAIN_DECOMPOSITION.md §8) |
| Spring Boot parent POM | Multi-module Maven/Gradle project with shared dependencies |
| `carddemo-validation` library | Java port of CSLKPCDY (state codes, ZIP, phone) + CSUTLDPY (date validation) |
| `carddemo-commons` library | Common DTOs, error handling, logging framework |
| CI/CD pipeline | GitHub Actions / Jenkins: build → test → deploy to staging |
| Staging environment | AWS ECS/EKS cluster + RDS PostgreSQL + SQS queues |
| Data migration scripts | VSAM → PostgreSQL ETL scripts (EBCDIC → UTF-8 conversion) |
| Test harness | Baseline output capture from COBOL programs (8 batch programs via GnuCOBOL) |

### Data Stores Affected
- **Create:** PostgreSQL database with all table DDL (empty tables)
- **No impact** on production COBOL system

### Integration Points
- None — no bridges needed yet

### Rollback Plan
- Delete staging environment resources
- No production impact to roll back

### Acceptance Criteria
- [ ] All 15 PostgreSQL tables created with correct column types matching COBOL PIC clauses
- [ ] `carddemo-validation` library passes unit tests for all 50 US state codes, ZIP format, date validation
- [ ] CI/CD pipeline builds and deploys a hello-world Spring Boot app to staging
- [ ] EBCDIC-to-UTF-8 conversion tested against `app/data/` sample files with zero data loss
- [ ] Baseline output captured for CBSTM03A, CBACT01C, CBACT02C, CBACT03C, CBCUS01C, CBTRN01C/02C/03C

---

## 4. Phase 1 — Quick Wins (Weeks 5–9)

### Objective
Migrate the two most isolated functional areas to prove the migration approach and deliver early value.

### Programs Migrated

| Program | LOC | Target Service | Strategy |
|---------|-----|----------------|----------|
| COSGN00C.cbl | 260 | user-auth-service | Rewrite |
| COUSR00C.cbl | 695 | user-auth-service | Rewrite |
| COUSR01C.cbl | 299 | user-auth-service | Rewrite |
| COUSR02C.cbl | 414 | user-auth-service | Rewrite |
| COUSR03C.cbl | 359 | user-auth-service | Rewrite |
| CBSTM03A.CBL | 924 | reporting-service | Rewrite |
| CBSTM03B.CBL | 230 | reporting-service | Rewrite |

**Total: 3,181 LOC migrated (12% of estate)**

### Data Stores Affected

| Source | Target | Migration Method | Dual-Write? |
|--------|--------|-----------------|-------------|
| USRSEC (VSAM KSDS, 80B records) | `users` table (PostgreSQL) | One-time ETL from EBCDIC flat file | No — clean cutover (user data is small) |
| Statement output files | `statements` table + S3 storage | N/A — new output format | No — batch job runs in one system only |

### Integration Points & Temporary Bridges

| Bridge | Purpose | Duration |
|--------|---------|----------|
| **Auth Token Bridge** | CICS programs check JWT validity via HTTP call to user-auth-service; Java services use JWT natively | Until all CICS programs are migrated (Phases 2–4) |
| **Menu Routing** | New React/Angular frontend proxies to CICS for un-migrated screens; handles auth screens natively | Until all online screens are migrated |

### Rollback Plan
1. Revert DNS/routing to point authentication back to CICS COSGN00C
2. Restore USRSEC VSAM file from backup (taken before migration)
3. Disable user-auth-service in load balancer
4. Statement generation: revert JCL to execute CBSTM03A
5. **Time to rollback: < 15 minutes**

### Acceptance Criteria
- [ ] User login via Java service returns same access for all test accounts as CICS sign-on
- [ ] User CRUD operations (create, list, update, delete) produce identical results
- [ ] Passwords migrated with bcrypt hashing; old plain-text passwords no longer stored
- [ ] Statement generation output matches COBOL baseline character-by-character for 100 test accounts
- [ ] JWT tokens accepted by remaining CICS programs via auth bridge
- [ ] Performance: login latency < 200ms (p99), statement generation throughput ≥ COBOL baseline

---

## 5. Phase 2 — Database Bridge (Weeks 10–16)

### Objective
Migrate programs already using DB2 (lowest friction database change) and the Card Management domain (establishing the reusable list/update pattern).

### Programs Migrated

| Program | LOC | Target Service | Strategy |
|---------|-----|----------------|----------|
| COTRTLIC.cbl | 2,098 | transaction-type-service | Rewrite |
| COTRTUPC.cbl | 1,702 | transaction-type-service | Rewrite |
| COBTUPDT.cbl | 237 | transaction-type-service | Rewrite |
| COCRDLIC.cbl | 1,459 | card-service | Rewrite |
| COCRDSLC.cbl | 887 | card-service | Rewrite |
| COCRDUPC.cbl | 1,560 | card-service | Rewrite |

**Total: 7,943 LOC migrated (cumulative: 11,124 LOC, 41% of estate)**

### Data Stores Affected

| Source | Target | Migration Method | Dual-Write? |
|--------|--------|-----------------|-------------|
| DB2 TRANSACTION_TYPE | `transaction_types` (PostgreSQL) | DB2 export → pg_restore | No — clean cutover |
| DB2 TRANSACTION_CATEGORY | `transaction_categories` (PostgreSQL) | DB2 export → pg_restore | No — clean cutover |
| CARDFILE (VSAM KSDS) | `cards` (PostgreSQL) | EBCDIC ETL | **Yes — dual-write bridge** |
| CARDXREF (VSAM KSDS) | `card_xref` (PostgreSQL) | EBCDIC ETL | **Yes — dual-write bridge** |

### Integration Points & Temporary Bridges

| Bridge | Purpose | Duration |
|--------|---------|----------|
| **VSAM-DB Sync (Card)** | Change Data Capture: writes to CARDFILE by remaining COBOL programs are replicated to `cards` table in near-real-time | Until all card writers are migrated (already done in this phase for COCRDUPC; CBIMPORT in Phase 6) |
| **Card API Gateway** | CICS programs that READ CARDFILE continue as-is; new Java services read from `cards` table | Until all card readers are migrated |
| **DB2 → PostgreSQL** | Transaction type lookups by CBTRN03C (still COBOL) redirected to PostgreSQL via ODBC bridge | Until CBTRN03C migrates in Phase 5 |

### Rollback Plan
1. Disable card-service and transaction-type-service in load balancer
2. Restore CARDFILE and CARDXREF VSAM from backup
3. Restore DB2 tables from backup
4. Re-enable CICS programs for card management screens
5. Stop VSAM-DB sync bridge
6. **Time to rollback: < 30 minutes**

### Acceptance Criteria
- [ ] Transaction type list pagination matches COTRTLIC output exactly (same ordering, same page size)
- [ ] Cascading delete in transaction-type-service matches COTRTUPC behavior (category records deleted with parent type)
- [ ] Card list pagination matches COCRDLIC STARTBR/READNEXT behavior (key-based cursor, same sort order)
- [ ] Card update matches COCRDUPC validation rules (expiry date check, status transitions)
- [ ] `AbstractListController` pattern validated and reusable (tested with both card list and tran-type list)
- [ ] VSAM-DB sync lag < 5 seconds for card data
- [ ] All 16 SQL statements from COTRTLIC translated and producing identical result sets

---

## 6. Phase 3 — Core Business (Weeks 17–26)

### Objective
Migrate the highest-complexity domain (Account Management) and the core Transaction Processing programs. This is the highest-risk phase.

### Programs Migrated

| Program | LOC | Target Service | Strategy |
|---------|-----|----------------|----------|
| COACTUPC.cbl | 4,236 | account-service | Rewrite + Strangler |
| COACTVWC.cbl | 941 | account-service | Rewrite |
| COACCT01.cbl | 620 | account-service | Rewrite |
| COTRN00C.cbl | 699 | transaction-service | Strangler |
| COTRN01C.cbl | 330 | transaction-service | Strangler |
| COTRN02C.cbl | 783 | transaction-service | Strangler |
| COBIL00C.cbl | 572 | transaction-service | Rewrite |
| COMEN01C.cbl | 308 | Frontend (React/Angular) | Rewrite |
| COADM01C.cbl | 288 | Frontend (React/Angular) | Rewrite |

**Total: 8,777 LOC migrated (cumulative: 19,901 LOC, 73% of estate)**

### Data Stores Affected

| Source | Target | Migration Method | Dual-Write? |
|--------|--------|-----------------|-------------|
| ACCTFILE (VSAM KSDS) | `accounts` (PostgreSQL) | EBCDIC ETL | **Yes — dual-write (most critical)** |
| CUSTFILE (VSAM KSDS) | `customers` (PostgreSQL) | EBCDIC ETL | **Yes — dual-write** |
| TRANSACT (VSAM KSDS) | `transactions` (PostgreSQL) | EBCDIC ETL | **Yes — dual-write** |
| DALYTRAN (sequential) | `daily_transactions` (staging) | File → DB loader | Batch-only — no dual-write |
| TCATBALF (VSAM KSDS) | `tran_category_balances` | EBCDIC ETL | Batch-only |

### Integration Points & Temporary Bridges

| Bridge | Purpose | Duration |
|--------|---------|----------|
| **VSAM-DB Sync (Account)** | Critical: ACCTFILE writes by remaining batch COBOL programs (CBACT04C, CBTRN02C) replicated to `accounts` table | Until batch programs migrate in Phase 5 |
| **VSAM-DB Sync (Transaction)** | TRANSACT writes by batch COBOL programs replicated to `transactions` table | Until batch programs migrate in Phase 5 |
| **Account API Strangler** | REST API wraps COACTUPC; new UI calls Java; COBOL serves as fallback for 2 weeks | Disabled after validation |
| **MQ Bridge (COACCT01)** | Account inquiry MQ messages handled by account-service via SQS adapter | Until authorization-service migrates in Phase 4 |

### Rollback Plan
1. Route all traffic back to CICS programs via load balancer
2. Restore ACCTFILE, CUSTFILE, TRANSACT VSAM files from backup taken at phase start
3. Disable account-service and transaction-service
4. Stop all VSAM-DB sync bridges
5. Restore frontend to CICS BMS screens
6. **Time to rollback: < 30 minutes** (VSAM restore is the bottleneck)

### Acceptance Criteria
- [ ] COACTUPC validation parity: all 5 field validators (date, SSN, phone, state, ZIP) produce identical accept/reject decisions for 10,000 test records
- [ ] Account update: same fields updated in same order with same error messages for 50 test scenarios
- [ ] Bill payment: COBIL00C balance deduction matches to the cent for 1,000 test transactions
- [ ] Transaction list pagination matches COTRN00C exactly
- [ ] ACCTFILE dual-write sync verified: 100% of COBOL-written records appear in PostgreSQL within 10 seconds
- [ ] Performance: account update < 500ms (p99), transaction list < 300ms (p99)
- [ ] Menu routing: all 11 COMEN01C targets accessible from new frontend; admin menu functional
- [ ] Shadow mode: Java and COBOL produce identical results for 1 week of production traffic

---

## 7. Phase 4 — Integration (Weeks 27–34)

### Objective
Migrate the most architecturally complex subsystem: Authorization Processing (IMS + MQ + CICS + DB2).

### Programs Migrated

| Program | LOC | Target Service | Strategy |
|---------|-----|----------------|----------|
| COPAUA0C.cbl | 1,026 | authorization-service | Strangler |
| COPAUS0C.cbl | 1,032 | authorization-service | Strangler |
| COPAUS1C.cbl | 604 | authorization-service | Strangler |
| COPAUS2C.cbl | 244 | authorization-service | Rewrite |
| CBPAUP0C.cbl | 386 | authorization-service | Rewrite |
| PAUDBLOD.CBL | 369 | authorization-service (data tools) | Rewrite |
| PAUDBUNL.CBL | 317 | authorization-service (data tools) | Rewrite |
| DBUNLDGS.CBL | 366 | authorization-service (data tools) | Rewrite |

**Total: 4,344 LOC migrated (cumulative: 24,245 LOC, 89% of estate)**

### Data Stores Affected

| Source | Target | Migration Method | Dual-Write? |
|--------|--------|-----------------|-------------|
| IMS AUTH database (hierarchical) | `authorization_summary`, `authorization_detail` (PostgreSQL) | Custom IMS segment → relational ETL | No — clean cutover (IMS is isolated) |
| DB2 fraud flags | `fraud_flags` (PostgreSQL) | DB2 export | No — clean cutover |
| MQ queues | Amazon SQS queues | Message format mapping | **Yes — MQ-SQS bridge during transition** |

### Integration Points & Temporary Bridges

| Bridge | Purpose | Duration |
|--------|---------|----------|
| **MQ-SQS Bridge** | Route MQ messages to SQS; authorization-service consumes from SQS; responses routed back to MQ for remaining consumers | Until all MQ consumers are migrated (completed in this phase) |
| **IMS Read Proxy** | During early Phase 4, authorization-service reads from PostgreSQL while COBOL still reads from IMS | 2–3 weeks, until IMS is decommissioned |

### IMS-to-Relational Data Mapping

```
IMS Hierarchy:                    PostgreSQL Tables:
AUTH_SUMMARY (root segment)  →    authorization_summary (
  ├── AUTH_DETAIL (child)            id, account_id, card_num,
  └── AUTH_DETAIL              →     auth_date, auth_amount, status)
                                  authorization_detail (
                                     id, summary_id FK,
                                     merchant, category, timestamp)
```

### Rollback Plan
1. Re-enable IMS database and COBOL programs
2. Stop MQ-SQS bridge; restore MQ queue configuration
3. Disable authorization-service
4. IMS data is preserved (not deleted until Phase 6 decommission)
5. **Time to rollback: < 20 minutes** (IMS is already running in parallel)

### Acceptance Criteria
- [ ] Authorization decision: COPAUA0C behavior replicated — same approve/deny decisions for 5,000 test authorization requests
- [ ] MQ message processing: request-response latency ≤ COBOL baseline
- [ ] IMS → PostgreSQL: all authorization records migrated with zero data loss (count match + checksum)
- [ ] Auth summary browse (COPAUS0C): pagination matches IMS GNP cursor behavior
- [ ] Auth detail update (COPAUS1C): same update semantics as IMS REPL
- [ ] Fraud marking (COPAUS2C): DB2 insert replicated identically in PostgreSQL
- [ ] Expired auth purge (CBPAUP0C): same records deleted for same date criteria
- [ ] SQS message throughput ≥ MQ throughput baseline

---

## 8. Phase 5 — Batch Pipeline (Weeks 35–39)

### Objective
Migrate remaining batch programs and complete the daily batch pipeline in Spring Batch. Retire VSAM-DB sync bridges.

### Programs Migrated

| Program | LOC | Target Service | Strategy |
|---------|-----|----------------|----------|
| CBTRN01C.cbl | 494 | transaction-service (batch) | Rewrite |
| CBTRN02C.cbl | 731 | transaction-service (batch) | Rewrite |
| CBTRN03C.cbl | 649 | reporting-service (batch) | Rewrite |
| CBACT04C.cbl | 652 | transaction-service (batch) | Rewrite |
| CORPT00C.cbl | 649 | reporting-service | Rewrite |
| CODATE01.cbl | 524 | utility (date service) | Rewrite |

**Total: 3,699 LOC migrated (cumulative: 27,944 LOC, ~100% of business logic)**

### Data Stores Affected

| Change | Detail |
|--------|--------|
| **Retire VSAM-DB sync bridges** | All programs now read/write PostgreSQL directly |
| **Retire DALYTRAN flat file** | Daily transactions loaded via REST API or database staging table |
| **Replace GDG versioning** | Database-based versioning with timestamped records |

### Daily Batch Pipeline — Before & After

```
BEFORE (JCL + COBOL):                     AFTER (Spring Batch + Step Functions):
POSTTRAN.jcl → CBTRN02C                   PostTransactionStep
     ↓                                         ↓
INTCALC.jcl → CBACT04C                    InterestCalculationStep
     ↓                                         ↓
CREASTMT.JCL → CBSTM03A                   StatementGenerationStep (Phase 1)
     ↓                                         ↓
TRANREPT.jcl → CBTRN03C                   TransactionReportStep
     ↓                                         ↓
Control-M orchestration                    AWS Step Functions orchestration
```

### Integration Points & Temporary Bridges

| Bridge | Action |
|--------|--------|
| VSAM-DB Sync (Account) | **DECOMMISSION** — CBACT04C and CBTRN02C now write to PostgreSQL directly |
| VSAM-DB Sync (Transaction) | **DECOMMISSION** — all transaction writers now use PostgreSQL |
| VSAM-DB Sync (Card) | **DECOMMISSION** — CBIMPORT (last card writer) migrates in Phase 6 |
| JCL Internal Reader (INTRDRJ1/J2) | **DECOMMISSION** — CORPT00C now triggers batch via REST API |

### Rollback Plan
1. Re-enable JCL batch pipeline with restored VSAM files
2. Re-activate VSAM-DB sync bridges
3. Disable Spring Batch jobs in Step Functions
4. **Time to rollback: < 30 minutes**

### Acceptance Criteria
- [ ] Daily batch pipeline end-to-end: input 10,000 daily transactions → same output as COBOL pipeline
- [ ] CBTRN02C parity: same transactions posted, same rejects generated, same balances updated (to the cent)
- [ ] CBACT04C parity: interest calculations match COBOL output for all rate tiers and proration scenarios
- [ ] CBTRN03C parity: report output character-by-character match for same input dataset
- [ ] Batch pipeline completes within SLA (≤ COBOL runtime + 10% margin)
- [ ] All VSAM-DB sync bridges successfully decommissioned
- [ ] Step Functions orchestration correctly handles step failures and retries

---

## 9. Phase 6 — Decommission (Weeks 40–42)

### Objective
Retire remaining utility programs, decommission VSAM files, shut down mainframe components, and archive COBOL source.

### Programs Retired

| Program | LOC | Action |
|---------|-----|--------|
| CBACT01C.cbl | 430 | Retire — replaced by SQL queries |
| CBACT02C.cbl | 178 | Retire — replaced by SQL queries |
| CBACT03C.cbl | 178 | Retire — replaced by SQL queries |
| CBCUS01C.cbl | 178 | Retire — replaced by SQL queries |
| CBEXPORT.cbl | 582 | Retire — PostgreSQL pg_dump replaces VSAM export |
| CBIMPORT.cbl | 487 | Retire — database restore replaces VSAM import |
| COBSWAIT.cbl | 41 | Retire — Thread.sleep() or scheduler wait |
| CSUTLDTC.cbl | 157 | Retire — Java LocalDate replaces LE CEEDAYS |

### Decommission Checklist

| Item | Action | Verification |
|------|--------|-------------|
| VSAM files (ACCTFILE, CARDFILE, CUSTFILE, CARDXREF, TRANSACT, USRSEC, TCATBALF, DISCGRP) | Archive to S3, then delete | Confirm all data in PostgreSQL matches final VSAM state |
| IMS database | Archive, then delete | Confirm all data in PostgreSQL `authorization_*` tables |
| DB2 tables (TRANSACTION_TYPE, TRANSACTION_CATEGORY) | Archive, then delete | Confirm all data in PostgreSQL |
| MQ queues | Archive configuration, delete queues | Confirm SQS queues operational |
| JCL jobs (46 files) | Archive to S3 | Document mapping to Step Functions workflows |
| BMS maps (16 files) | Archive to S3 | Confirm React/Angular screens functional |
| COBOL source (44 programs) | Archive to S3 with full Git history | Retain for compliance/audit (7 years) |
| Control-M schedules | Archive, then delete | Confirm Step Functions schedules active |
| Mainframe LPAR/region | Decommission | Final sign-off from operations |

### Rollback Plan
- **No rollback** — decommission happens only after 30-day stability observation period (post-Phase 5)
- All VSAM/IMS/DB2 data archived to S3 before deletion
- Archives retained for 7 years for regulatory compliance

### Acceptance Criteria
- [ ] 30 consecutive days of production operation on Java stack with zero P1 incidents
- [ ] All VSAM, IMS, and DB2 data archived to S3 with verified checksums
- [ ] Zero active connections to mainframe from any service
- [ ] Mainframe cost reduced to $0/month (or archive-only cost)
- [ ] COBOL source archived in Git with tags marking final production versions
- [ ] Compliance sign-off from audit team on data retention

---

## 10. Timeline Summary

```
Week:  1    5    10   17   27   35   40  42
       |    |    |    |    |    |    |   |
       ├────┤    |    |    |    |    |   |
       │ P0 │    |    |    |    |    |   |
       │Found│   |    |    |    |    |   |
       ├─────┤   |    |    |    |    |   |
       │     │P1 │    |    |    |    |   |
       │     │Quick   |    |    |    |   |
       │     │Wins│   |    |    |    |   |
       │     ├────┤   |    |    |    |   |
       │     │    │P2 │    |    |    |   |
       │     │    │DB  │   |    |    |   |
       │     │    │Bridge  |    |    |   |
       │     │    ├────┤   |    |    |   |
       │     │    │    │P3 │    |    |   |
       │     │    │    │Core    |    |   |
       │     │    │    │Business│    |   |
       │     │    │    ├────────┤    |   |
       │     │    │    │        │P4  │   |
       │     │    │    │        │Integ   |
       │     │    │    │        ├────┤   |
       │     │    │    │        │    │P5 │
       │     │    │    │        │    │Batch
       │     │    │    │        │    ├───┤
       │     │    │    │        │    │   │P6
       │     │    │    │        │    │   │Decom
       ▼     ▼    ▼    ▼        ▼    ▼   ▼

Programs:  0    7    13   22      30   36  44
Migrated
(cumul.)
```

---

## 11. Parallel Workstreams

| Workstream | Runs Across | Team | Activities |
|------------|-------------|------|-----------|
| **Data Migration** | Phases 0–5 | 1 engineer | VSAM → PostgreSQL ETL, EBCDIC conversion, data validation |
| **Frontend Development** | Phases 1–4 | 1–2 engineers | React/Angular screens replacing BMS maps |
| **API Development** | Phases 1–5 | 2 engineers | Spring Boot services, REST APIs, Spring Batch jobs |
| **Testing & Validation** | Phases 1–6 | 1 engineer | Output parity testing, performance benchmarking, regression suites |
| **Infrastructure** | Phases 0–6 | 1 engineer (part-time) | AWS setup, CI/CD, monitoring, alerting |
