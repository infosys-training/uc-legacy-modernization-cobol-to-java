# CardDemo Cutover Plan

## Overview

This plan sequences the CardDemo modernization into five phases over an estimated 12–18 month timeline. Each phase is designed to deliver incremental business value while maintaining system continuity through temporary bridges between legacy and modern components.

The guiding principles are:
1. **Foundation first** — security and reference data before business logic
2. **Data gravity** — migrate the most-shared data stores early to reduce bridging complexity
3. **Batch follows online** — replatform batch to AWS M2 early, then progressively rewrite as data stores migrate
4. **Parallel run** — critical financial processes (interest, billing) require dual-run validation before legacy decommission

---

## Phase 0: Foundation & Replatforming (Months 1–3)

### Objective
Establish cloud infrastructure, replatform batch workloads to AWS M2, and build the modern identity service.

### Programs Migrating

| Program | Type | Target | Notes |
|---------|------|--------|-------|
| COSGN00C | CICS | **Rewrite** → `identity-service` | New Spring Security + JWT auth |
| COUSR00C–03C | CICS | **Rewrite** → `identity-service` | User CRUD API |
| All batch programs | Batch | **Replatform** → AWS M2 | Lift-and-shift to M2 runtime |
| CLOSEFIL, OPENFIL, WAITSTEP | JCL | **Replatform** → AWS M2 | File lifecycle management |
| Control-M schedules | Scheduler | **Migrate** → AWS M2 scheduler | All three cycles (daily/weekly/monthly) |

### Data Stores Affected

| Data Store | Action | Details |
|------------|--------|---------|
| USRSEC | **Migrate** | Extract user records → PostgreSQL `users` table. Map SEC-USR-TYPE (A/U) to roles. |
| All VSAM files | **Replatform** | Available on AWS M2 for replatformed batch programs. |

### Integration Bridges

| Bridge | Purpose | Implementation |
|--------|---------|----------------|
| **Auth Bridge** | CICS programs authenticate via legacy USRSEC during transition; new services use JWT | `identity-service` syncs to USRSEC VSAM via periodic export (every 5 minutes) until all CICS programs are migrated. Alternatively, a CICS adapter program forwards auth requests to the new service. |
| **M2 ↔ Original Mainframe** | If original mainframe is retained during transition | AWS M2 reads same data file formats. Data files transferred via AWS Transfer Family or direct VSAM conversion. |

### Rollback Plan
- **Identity service rollback:** Revert CICS sign-on to read USRSEC directly (original COSGN00C remains deployable on M2).
- **M2 rollback:** Batch programs can be redirected back to original mainframe. JCL and VSAM datasets are format-compatible.

### Acceptance Criteria
- [ ] All 8 compilable batch programs execute successfully on AWS M2 with identical output to mainframe runs
- [ ] `identity-service` authenticates all existing users (validate against USRSEC extract)
- [ ] User CRUD operations (add/update/delete/list) functional via REST API
- [ ] JWT tokens issued by `identity-service` are validated by a test consumer
- [ ] Control-M daily/weekly/monthly schedules replicated on AWS M2 scheduler
- [ ] Batch job outputs (reports, backups, GDG entries) match mainframe baseline within tolerance

---

## Phase 1: Core Domain APIs — Account, Card, Reference Data (Months 3–6)

### Objective
Build the account, card, and reference data microservices. Establish the relational database as the system of record for account/customer/card data. Deploy the first version of the web frontend.

### Programs Migrating

| Program | Type | Target | Notes |
|---------|------|--------|-------|
| COACTVWC | CICS | **Strangler** → `account-service` | Account View API |
| COACTUPC | CICS | **Strangler** → `account-service` | Account Update API (complex validation) |
| COCRDLIC | CICS | **Strangler** → `card-service` | Card List API |
| COCRDSLC | CICS | **Strangler** → `card-service` | Card Detail API |
| COCRDUPC | CICS | **Strangler** → `card-service` | Card Update API |
| CBACT01C | Batch | **Rewrite** → `account-service` batch | Account data reader |
| CBACT02C | Batch | **Rewrite** → `card-service` batch | Card data reader |
| CBACT03C | Batch | **Rewrite** → `account-service` batch | Cross-reference reader |
| CBCUS01C | Batch | **Rewrite** → `account-service` batch | Customer data reader |
| DISCGRP, TRANCATG, TRANTYPE JCL | JCL | **Rewrite** → `reference-data-service` | CRUD API + admin UI |
| COMEN01C | CICS | **Rewrite** → Frontend SPA | User menu |
| COADM01C | CICS | **Rewrite** → Frontend SPA | Admin menu |

### Data Stores Affected

| Data Store | Action | Details |
|------------|--------|---------|
| ACCTDAT | **Migrate** → PostgreSQL `accounts` table | 300-byte VSAM records → relational. 50 records in sample data, production TBD. |
| CUSTDAT | **Migrate** → PostgreSQL `customers` table | 500-byte records with address, phone, SSN, FICO score. |
| CARDDAT | **Migrate** → PostgreSQL `cards` table | 150-byte records. CARDAIX becomes a database index on `card_num`. |
| CCXREF | **Migrate** → PostgreSQL `card_xref` table | 50-byte records. CXACAIX becomes a database index on `acct_id`. |
| DISCGRP | **Migrate** → PostgreSQL `disclosure_groups` | Small reference table. |
| TRANCATG | **Migrate** → PostgreSQL `transaction_categories` | Small reference table. |
| TRANTYPE | **Migrate** → PostgreSQL `transaction_types` | Small reference table. |

### Integration Bridges

| Bridge | Purpose | Implementation |
|--------|---------|----------------|
| **VSAM-to-DB Sync** | Batch programs on M2 still read VSAM files while new services write to PostgreSQL | Change Data Capture (CDC) from PostgreSQL → VSAM update job runs every N minutes. Alternatively, dual-write from new services to both DB and VSAM. |
| **API Gateway for CICS** | Remaining CICS programs (transaction, billing) need account/card data | API gateway routes requests: new frontend → microservices; legacy CICS → VSAM (on M2). Data consistency maintained by VSAM-to-DB sync. |
| **BMS-to-SPA Bridge** | Users can access either 3270 screens (via TN3270) or new web UI | Both interfaces hit the same backend (microservices for migrated functions, M2 CICS for remaining). Menu options progressively redirect to web UI. |

### Rollback Plan
- **Per-service rollback:** Each API can be individually reverted. API gateway routing table points back to M2 CICS programs. VSAM files remain the authoritative data source until sync is verified bidirectional.
- **Database rollback:** PostgreSQL data can be exported back to VSAM sequential files using IDCAMS REPRO on M2 (reverse of the migration path).
- **Frontend rollback:** 3270 terminal access remains available throughout. Remove SPA routing to fall back to BMS screens.

### Acceptance Criteria
- [ ] `account-service` passes integration tests covering all COACTVWC/COACTUPC functionality
- [ ] `card-service` passes integration tests covering COCRDLIC/COCRDSLC/COCRDUPC functionality
- [ ] `reference-data-service` CRUD operations verified for all three reference tables
- [ ] VSAM-to-DB sync verified: updates in PostgreSQL reflected in VSAM within SLA (≤5 min)
- [ ] Reverse sync verified: VSAM changes reflected in PostgreSQL
- [ ] Frontend SPA renders all 11 user menu options and 6 admin menu options
- [ ] Account view/update functional end-to-end through web UI
- [ ] Batch programs on M2 continue to read VSAM data correctly (regression test)
- [ ] Performance baseline: API response times ≤200ms for reads, ≤500ms for updates

---

## Phase 2: Transaction Processing & Billing (Months 6–10)

### Objective
Migrate the transaction processing pipeline from batch-centric to event-driven. Replace the daily close-process-open cycle. Migrate billing to the new platform.

### Programs Migrating

| Program | Type | Target | Notes |
|---------|------|--------|-------|
| COTRN00C | CICS | **Rewrite** → `transaction-service` | Transaction List API |
| COTRN01C | CICS | **Rewrite** → `transaction-service` | Transaction View API |
| COTRN02C | CICS | **Rewrite** → `transaction-service` | Transaction Add API |
| CBTRN01C | Batch | **Rewrite** → event consumer | Daily transaction posting |
| CBTRN02C | Batch | **Rewrite** → event consumer | Transaction categorization + posting |
| COBIL00C | CICS | **Rewrite** → `transaction-service` | Bill Payment API |
| TRANBKP JCL | Batch | **Eliminate** | No longer needed — DB handles concurrent access |
| COMBTRAN JCL | Batch | **Rewrite** → scheduled job | Transaction merging |

### Data Stores Affected

| Data Store | Action | Details |
|------------|--------|---------|
| TRANSACT | **Migrate** → PostgreSQL `transactions` table | 350-byte VSAM records. Primary key: TRAN-ID (16 chars). |
| DALYTRAN | **Migrate** → PostgreSQL `daily_transactions` table (or event queue) | Sequential PS file → database table or Kafka topic. |
| TCATBALF | **Migrate** → PostgreSQL `category_balances` table | Aggregation table updated by transaction posting. |

### Integration Bridges

| Bridge | Purpose | Implementation |
|--------|---------|----------------|
| **Event-to-Batch Bridge** | During transition, new transaction events must feed into any remaining M2 batch processes | Kafka consumer writes to DALYTRAN-format sequential file on M2 for any batch programs not yet migrated. |
| **Transaction API ↔ VSAM** | Remaining M2 batch programs (interest calc, reporting) still read TRANSACT VSAM | CDC from PostgreSQL `transactions` → VSAM TRANSACT update job. |
| **CICS File Lifecycle** | CLOSEFIL/OPENFIL no longer needed for migrated files | Remove ACCTDAT, CARDDAT, CUSTDAT, CCXREF, TRANSACT from CLOSEFIL/OPENFIL scripts. Only files still on VSAM remain in the cycle. |

### Rollback Plan
- **Transaction service rollback:** Route transaction API calls back to M2 CICS (COTRN00C–02C). Resume DALYTRAN batch posting via CBTRN02C on M2.
- **Event pipeline rollback:** Disable Kafka consumers; resume sequential DALYTRAN file processing via POSTTRAN JCL on M2.
- **Billing rollback:** COBIL00C remains deployable on M2. Route billing requests back to CICS.
- **Data rollback:** Export PostgreSQL `transactions` table back to VSAM sequential file via IDCAMS REPRO.

### Acceptance Criteria
- [ ] Transaction CRUD functional via REST API (list, view, add)
- [ ] Daily transaction posting operates in near-real-time via event consumers (vs. batch window)
- [ ] Transaction categorization and balance updates match CBTRN02C output exactly (parallel run comparison)
- [ ] Bill payment end-to-end: reads account balance, creates payment transaction, updates balance
- [ ] CLOSEFIL/OPENFIL scripts updated to exclude migrated VSAM files
- [ ] Daily batch window eliminated for transaction processing
- [ ] No data loss: all transactions created during parallel run appear in both systems
- [ ] Monitoring and alerting operational for event pipeline (consumer lag, dead letters)

---

## Phase 3: Interest Calculation & Reporting (Months 10–14)

### Objective
Rewrite interest calculation and reporting to operate against the relational database. Eliminate remaining VSAM dependencies. Retire M2 for all but edge-case batch programs.

### Programs Migrating

| Program | Type | Target | Notes |
|---------|------|--------|-------|
| CBACT04C | Batch | **Rewrite** → Spring Batch job in `transaction-service` | Interest calculator |
| CBTRN03C | Batch | **Rewrite** → `reporting-service` | Transaction detail report |
| CBSTM03A/B | Batch | **Rewrite** → `reporting-service` | Statement generation |
| CORPT00C | CICS | **Rewrite** → `reporting-service` API | Report trigger (was submitting JCL) |
| INTCALC JCL | Batch | **Rewrite** → Spring Batch + AWS scheduler | Monthly interest calculation |
| TRANREPT JCL | Batch | **Rewrite** → `reporting-service` | Transaction report pipeline (replaces SORT utility) |
| CREASTMT JCL | Batch | **Rewrite** → `reporting-service` | Statement creation |

### Data Stores Affected

| Data Store | Action | Details |
|------------|--------|---------|
| DISCGRP VSAM | **Decommission** | Already migrated to PostgreSQL in Phase 1. M2 VSAM copy can be deleted. |
| TCATBALF VSAM | **Decommission** | Migrated to PostgreSQL in Phase 2. |
| TRANSACT VSAM | **Decommission** | Migrated to PostgreSQL in Phase 2. |
| GDG datasets (SYSTRAN, TRANSACT.BKUP, DALYREJS) | **Replace** | Database backups replace GDG-based file versioning. Point-in-time recovery via PostgreSQL WAL. |

### Integration Bridges

| Bridge | Purpose | Implementation |
|--------|---------|----------------|
| **Parallel Run — Interest Calc** | Validate new interest calculation matches CBACT04C output exactly | Run both CBACT04C on M2 and Spring Batch job against same month's data. Compare outputs record-by-record. Must match within ±$0.01 per account. |
| **Report Format Comparison** | Validate new report generation matches CBTRN03C output | Diff comparison of formatted report output. Allow formatting differences; validate data accuracy. |

### Rollback Plan
- **Interest calc rollback:** Re-enable INTCALC JCL on M2. TCATBALF VSAM kept as read-only snapshot for 3 months post-migration.
- **Reporting rollback:** TRANREPT JCL and CBTRN03C remain deployable on M2. SORT utility and REPROC procedure available.
- **GDG rollback:** GDG base definitions retained on M2 for 6 months. New GDG entries can be created from database exports.

### Acceptance Criteria
- [ ] Interest calculation parallel run: ≤0.01% variance in interest amounts across all accounts
- [ ] Monthly interest cycle completes within the same time window as mainframe (or faster)
- [ ] Transaction detail report output matches CBTRN03C output (data accuracy ≥99.99%)
- [ ] Statement generation produces equivalent output to CBSTM03A/B
- [ ] All GDG-based backup processes replaced with database backup procedures
- [ ] M2 batch scheduler reduced to zero active jobs (monitoring only)
- [ ] CLOSEFIL/OPENFIL scripts have no remaining files to process (can be retired)

---

## Phase 4: Decommission & Cleanup (Months 14–18)

### Objective
Retire all remaining legacy components, decommission AWS M2 runtime, and finalize the modern architecture.

### Programs Migrating

| Program | Type | Target | Notes |
|---------|------|--------|-------|
| CBEXPORT | Batch | **Rewrite** → ETL pipeline | Modern format export (JSON/CSV) |
| CBIMPORT | Batch | **Rewrite** → ETL pipeline | Modern format import |
| COBSWAIT | Batch | **Eliminate** | Replaced by standard scheduling mechanisms |
| CSUTLDTC | Batch | **Eliminate** | Date logic absorbed into Java services |
| ASM routines | System | **Eliminate** | MVSWAIT→Thread.sleep; COBDATFT→java.time |

### Data Stores Affected

| Data Store | Action | Details |
|------------|--------|---------|
| All remaining VSAM files on M2 | **Decommission** | Final data verification, then delete M2 VSAM clusters. |
| AWS M2 runtime | **Decommission** | Terminate M2 managed environment. |
| Control-M on M2 | **Decommission** | All scheduling moved to AWS Step Functions / EventBridge. |

### Integration Bridges
None — all bridges retired in this phase.

### Rollback Plan
- **M2 decommission rollback:** M2 environment snapshot retained for 90 days. Can be restarted if critical issues discovered.
- **VSAM data rollback:** Final VSAM exports (sequential PS files) archived to S3 Glacier for 7 years (regulatory retention).

### Acceptance Criteria
- [ ] Zero active CICS programs on M2
- [ ] Zero active batch jobs on M2
- [ ] Zero VSAM files open on M2
- [ ] Modern ETL pipeline (CBEXPORT replacement) produces functionally equivalent output
- [ ] All regulatory data retention requirements met (archived exports in S3 Glacier)
- [ ] M2 runtime terminated
- [ ] Full system regression test passes against microservices architecture
- [ ] Load test: system handles 2x peak mainframe throughput
- [ ] Disaster recovery test: failover to secondary region within RTO/RPO targets
- [ ] Operations runbook updated for new architecture
- [ ] On-call team trained on microservices monitoring and incident response

---

## Phase Timeline Summary

```
Month:  1    2    3    4    5    6    7    8    9    10   11   12   13   14   15   16   17   18
        ├────────────┤
        Phase 0: Foundation & Replatform
        │ M2 replatform │
        │ identity-svc  │
                        ├─────────────────────┤
                        Phase 1: Core Domain APIs
                        │ account-svc          │
                        │ card-svc             │
                        │ ref-data-svc         │
                        │ frontend SPA v1      │
                                              ├─────────────────────────┤
                                              Phase 2: Transactions & Billing
                                              │ transaction-svc         │
                                              │ event pipeline          │
                                              │ billing                 │
                                                                        ├─────────────────┤
                                                                        Phase 3: Interest & Reporting
                                                                        │ interest calc    │
                                                                        │ reporting-svc    │
                                                                        │ parallel runs    │
                                                                                          ├────────────┤
                                                                                          Phase 4: Decommission
                                                                                          │ M2 retire   │
                                                                                          │ ETL rewrite  │
                                                                                          │ cleanup      │
```

---

## Key Dependencies Between Phases

| Dependency | Blocking Phase | Blocked Phase | Mitigation |
|------------|---------------|---------------|------------|
| Identity service must be operational | Phase 0 | Phase 1 | JWT auth required for all new API services |
| Account/Card database must be populated | Phase 1 | Phase 2 | Transaction service needs relational account data |
| VSAM-to-DB sync must be bidirectional | Phase 1 | Phase 2 | Batch programs on M2 still need VSAM data |
| Transaction database must be migrated | Phase 2 | Phase 3 | Interest calc and reporting read from transactions DB |
| Interest parallel run must pass | Phase 3 | Phase 4 | Cannot decommission M2 until financial calcs verified |
| All CICS programs migrated off M2 | Phases 1–3 | Phase 4 | M2 cannot be retired until zero CICS programs remain |

---

## Risk Checkpoints

At the end of each phase, a go/no-go decision is made based on the acceptance criteria. If acceptance criteria are not met:

1. **Extend the phase** by up to 4 weeks to address gaps
2. **Activate the rollback plan** for the specific component that failed
3. **Do not proceed** to the next phase until all criteria are green

A **Phase Gate Review** meeting is held with stakeholders at each transition point to review:
- Acceptance criteria results
- Performance metrics comparison (mainframe vs. new)
- Data integrity verification results
- Open defect count and severity
- Updated risk register (see RISK_REGISTER.md)
