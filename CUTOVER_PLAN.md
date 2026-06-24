# CardDemo Cutover Plan

> Phased migration sequence with program scope, data impact, integration bridges, rollback plans, and acceptance criteria for each phase.

---

## Migration Principles

1. **Lowest-risk, highest-value first** — Start with isolated, self-contained modules that prove patterns without risking production data
2. **No big bang** — Each phase delivers a working increment; COBOL and Java run in parallel during transition
3. **Data migration follows code migration** — Move data stores only after the consuming code is proven in Java
4. **Every phase has a rollback plan** — Always maintain the ability to revert to COBOL within 4 hours

**Total estimated timeline: 32–42 weeks (6–9 months) with a 4-person team**

---

## Phase 0: Foundation (Weeks 1–4)

### Objective
Stand up the target infrastructure and shared libraries before any program migration.

### Scope

| Deliverable | Details |
|------------|---------|
| PostgreSQL (Aurora) instance | Schema-per-service design; Flyway for DDL management |
| Spring Boot parent POM | Common dependency management, logging, monitoring |
| `carddemo-validation` library | Extract from CSLKPCDY + CSUTLDPY/CSUTLDWY: DateValidator, PhoneValidator, AddressValidator, SSNValidator |
| `carddemo-commons` library | DTOs from COCOM01Y (COMMAREA), error structures from CSMSG01Y/02Y, MoneyUtils (BigDecimal wrappers) |
| CI/CD pipeline | Build, test, deploy pipeline for all services |
| API gateway | Route configuration for parallel COBOL/Java operation |
| Monitoring & observability | Logging, metrics, alerting for new Java services |

### Data Stores Affected
- Create empty PostgreSQL schemas for all 8 services
- **No production data migration yet**

### Integration Bridges
- None needed — no COBOL replacement in this phase

### Rollback Plan
- Delete infrastructure if abandoned — no production impact

### Acceptance Criteria
- [ ] All shared libraries compile and pass unit tests
- [ ] PostgreSQL instance running with Flyway migrations applied
- [ ] CI/CD pipeline successfully builds and deploys a hello-world service
- [ ] `DateValidator` passes all CSUTLDPY validation cases (century, month, day, leap year, DOB)
- [ ] `MoneyUtils` correctly handles COBOL PIC S9(10)V99 ↔ BigDecimal conversion including overpunch encoding
- [ ] API gateway routes traffic to a test endpoint

---

## Phase 1: Pilot — Reporting & Transaction Types (Weeks 5–10)

### Objective
Prove the migration approach with the two lowest-risk, most isolated functional areas.

### Programs Migrating

| Service | COBOL Programs | LOC | Strategy | Risk |
|---------|---------------|-----|----------|------|
| **reporting-service** | CBSTM03A (924), CBSTM03B (230), CBTRN03C (649), CORPT00C (649) | 2,452 | Rewrite | Low |
| **transaction-type-service** | COTRTLIC (2,098), COTRTUPC (1,702), COBTUPDT (237) | 4,037 | Rewrite | Low |

### Data Stores Affected

| Current | Migration Action | Target |
|---------|-----------------|--------|
| TRANTYPE.VSAM.KSDS | Migrate data → `transaction_types` table | PostgreSQL |
| TRANCATG.VSAM.KSDS | Migrate data → `transaction_categories` table | PostgreSQL |
| STATEMNT.PS / .HTML | Generate to S3 bucket | S3 |
| REPTFILE | Generate to S3 bucket | S3 |

### Integration Bridges

| Bridge | Description | Duration |
|--------|-------------|----------|
| **VSAM Read Proxy** | Reporting-service reads ACCTFILE, CUSTFILE, XREFFILE, TRANSACT via VSAM (not yet migrated). Temporary JDBC adapter or direct VSAM file access from Java. | Until Phase 3 migrates Account + Transaction data |
| **DB2 → PostgreSQL** | Transaction type data migrated from DB2 to PostgreSQL. COBOL programs that referenced DB2 tables (CBTRN03C) now read from PostgreSQL via JDBC. | Permanent |
| **JCL → Spring Batch** | CREASTMT.JCL and TRANREPT.jcl replaced by Spring Batch jobs triggered via REST API or scheduler. | Permanent |

### Rollback Plan
- **Reporting:** Revert to COBOL batch jobs (CREASTMT.JCL, TRANREPT.jcl). Statement/report output is regenerable — no data loss.
- **Transaction Types:** Restore DB2 tables from DEFGDGD.jcl backup. Re-enable COBOL COTRTLIC/COTRTUPC in CICS CSD.
- **Rollback time:** < 2 hours (re-enable JCL jobs + restore DB2 backup)

### Acceptance Criteria
- [ ] Spring Batch statement generation produces byte-identical text output compared to CBSTM03A
- [ ] HTML statement output matches COBOL-generated HTML layout
- [ ] Transaction report (CBTRN03C equivalent) matches COBOL output: page totals, account totals, grand totals
- [ ] Transaction type CRUD operations (list, add, update, delete with cascade) pass all regression tests
- [ ] DB2 cursor-based pagination behavior matches COTRTLIC scrolling (forward + backward)
- [ ] Performance: Java batch ≤ 110% of COBOL runtime for equivalent dataset size
- [ ] No regressions in remaining COBOL programs that read transaction type reference data

---

## Phase 2: User Security & Card Management (Weeks 11–17)

### Objective
Migrate the two cleanest CRUD domains and eliminate the critical plain-text password vulnerability.

### Programs Migrating

| Service | COBOL Programs | LOC | Strategy | Risk |
|---------|---------------|-----|----------|------|
| **user-auth-service** | COSGN00C (260), COUSR00C (695), COUSR01C (299), COUSR02C (414), COUSR03C (359), COADM01C (288), COMEN01C (308) | 2,623 | Rewrite | Low |
| **card-service** | COCRDLIC (1,459), COCRDSLC (887), COCRDUPC (1,560), CBACT02C (178), CBACT03C (178) | 4,262 | Rewrite | Medium |

### Data Stores Affected

| Current | Migration Action | Target |
|---------|-----------------|--------|
| USRSEC.VSAM.KSDS | Migrate users → `users` table; **discard plain-text passwords**; require password reset | PostgreSQL |
| CARDDATA.VSAM.KSDS | Migrate data → `cards` table | PostgreSQL |
| CARDXREF.VSAM.KSDS | Migrate data → `card_xref` table | PostgreSQL |

### Integration Bridges

| Bridge | Description | Duration |
|--------|-------------|----------|
| **JWT Authentication** | Replace CICS COSGN00C sign-on with JWT token issuance. All remaining COBOL programs must accept JWT-authenticated API calls OR continue using CICS COMMAREA sign-on in parallel. | Until all CICS programs are migrated |
| **CARDXREF API Facade** | Card-service exposes REST API for card→account→customer lookups. Remaining COBOL programs still read CARDXREF via VSAM; dual-read until Account and Transaction migrate. | Until Phase 3 |
| **Card Data Dual-Write** | During transition, card updates write to both PostgreSQL (primary) and VSAM (for remaining COBOL consumers). | Until Phase 3 |

### Rollback Plan
- **User Security:** Restore USRSEC.VSAM.KSDS from DUSRSECJ.jcl. Re-enable COSGN00C in CICS. Users must re-create passwords (cannot restore plain-text).
- **Card Management:** Restore CARDDATA and CARDXREF from CARDFILE.jcl and XREFFILE.jcl backups. Re-enable COCRDLIC/COCRDSLC/COCRDUPC in CICS CSD.
- **Rollback time:** < 4 hours (VSAM restore + CICS CSD reload)

### Acceptance Criteria
- [ ] JWT-based authentication works for all user types (Admin='A', User='U')
- [ ] User CRUD operations (list, add, update, delete) pass all regression tests
- [ ] **Passwords stored as bcrypt hashes** — no plain text in database
- [ ] Card list pagination matches COCRDLIC behavior (forward/backward scrolling)
- [ ] Card update validates expiration date, embossed name, status transitions
- [ ] CARDXREF lookups via REST API return results matching VSAM reads (100% match on sample of 10,000 records)
- [ ] Remaining COBOL programs function correctly with dual-write card data

---

## Phase 3: Account & Transaction Processing (Weeks 18–27)

### Objective
Migrate the core financial processing — the highest-complexity, highest-risk phase. This is where the two "hard" extraction seams (S2: transaction posting → account balance, S4: bill payment → account debit) are resolved.

### Programs Migrating

| Service | COBOL Programs | LOC | Strategy | Risk |
|---------|---------------|-----|----------|------|
| **account-service** | COACTUPC (4,236), COACTVWC (941), COACCT01 (620), CBACT01C (430), CBACT04C (652) | 6,879 | Strangler | High |
| **transaction-service** | COTRN00C (699), COTRN01C (330), COTRN02C (783), CBTRN01C (494), CBTRN02C (731), COBIL00C (572) | 3,609 | Strangler + Rewrite | High |

### Sub-Phases

#### Phase 3a: Account API Facade (Weeks 18–21)

Wrap ACCTFILE with a REST API facade. COBOL continues processing; Java provides the API layer.

1. Migrate ACCTDATA, CUSTDATA, TCATBALF, DISCGRP to PostgreSQL tables
2. Implement dual-write: COBOL writes to VSAM → CDC event → PostgreSQL sync (< 10 second lag)
3. Java account-service provides REST API reading from PostgreSQL
4. COACTVWC (read-only) rewritten to call account-service API
5. Remaining COBOL programs still write to VSAM (primary source of truth)

#### Phase 3b: Account Update Migration (Weeks 22–24)

Migrate COACTUPC — the #1 hotspot (4,236 LOC, 185 branching statements).

1. Extract all validation logic into `AccountValidationService` (date, SSN, phone, state, ZIP, amount)
2. Run shadow mode: COBOL processes requests (primary), Java processes same requests (shadow). Compare outputs.
3. After 2 weeks of shadow parity at >99.99% match, cut over Java as primary
4. PostgreSQL becomes source of truth for ACCTFILE; reverse-sync to VSAM for remaining COBOL consumers

#### Phase 3c: Transaction Processing Migration (Weeks 24–27)

Migrate transaction processing once account API is stable.

1. Rewrite CBTRN01C, CBTRN02C as Spring Batch jobs
2. CBTRN02C's cross-domain write (ACCTFILE balance update) becomes an async event to account-service
3. Implement saga pattern for COBIL00C (bill payment): create transaction → call account-service to debit → compensate on failure
4. Migrate TRANSACT and DALYTRAN to PostgreSQL
5. Rewrite online COTRN00C/01C/02C as REST endpoints

### Data Stores Affected

| Current | Migration Action | Target |
|---------|-----------------|--------|
| ACCTDATA.VSAM.KSDS | Migrate → `accounts` table | PostgreSQL |
| CUSTDATA.VSAM.KSDS | Migrate → `customers` table | PostgreSQL |
| TCATBALF.VSAM.KSDS | Migrate → `tran_cat_balances` table | PostgreSQL |
| DISCGRP.VSAM.KSDS | Migrate → `disclosure_groups` table | PostgreSQL |
| TRANSACT.VSAM.KSDS | Migrate → `transactions` table | PostgreSQL |
| DALYTRAN.PS | Replace with API-based transaction ingestion | PostgreSQL |

### Integration Bridges

| Bridge | Description | Duration |
|--------|-------------|----------|
| **VSAM↔PostgreSQL Dual-Write** | SQS FIFO queues sync writes between VSAM (COBOL) and PostgreSQL (Java). Hourly reconciliation (count + checksum). | 4–6 weeks during Phase 3a–3c |
| **Shadow Mode** | COACTUPC requests processed by both COBOL (primary) and Java (shadow). Outputs compared automatically. Discrepancies logged. | 2+ weeks before cutover |
| **Saga Coordinator** | COBIL00C bill payment becomes a saga: transaction-service creates transaction, then calls account-service to update balance. Compensation on failure. | Permanent (new pattern) |
| **VSAM Snapshots** | Automated VSAM snapshots every 4 hours during dual-write period for rollback safety | Phase 3 only |

### Rollback Plan
- **Phase 3a:** Disable dual-write CDC; revert to VSAM-only reads. PostgreSQL data is discarded. < 2 hours.
- **Phase 3b:** Re-enable COBOL COACTUPC as primary; disable Java shadow. Reverse-sync any PostgreSQL-only changes back to VSAM from latest snapshot. < 4 hours.
- **Phase 3c:** Re-enable COBOL batch jobs (POSTTRAN.jcl, INTCALC.jcl). Restore TRANSACT from TRANBKP.jcl backup. < 4 hours.

### Acceptance Criteria
- [ ] Account API returns correct data for 100% of a 50,000-record sample
- [ ] COACTUPC shadow mode: >99.99% output match rate over 2 weeks / 10,000+ transactions
- [ ] All 185 validation branches in COACTUPC have corresponding unit tests passing
- [ ] Transaction posting (CBTRN02C equivalent) produces identical account balance updates
- [ ] Bill payment saga completes successfully with rollback on simulated failures
- [ ] Interest calculation (CBACT04C equivalent) matches COBOL output to the penny for a full month's test data
- [ ] Dual-write reconciliation: zero count/checksum mismatches for 48+ consecutive hours
- [ ] Daily batch pipeline SLA: Java runtime ≤ 110% of COBOL runtime

---

## Phase 4: Authorization (IMS/MQ) Migration (Weeks 28–35)

### Objective
Migrate the most architecturally complex subsystem — IMS hierarchical data + MQ messaging + CICS online.

### Programs Migrating

| Service | COBOL Programs | LOC | Strategy | Risk |
|---------|---------------|-----|----------|------|
| **authorization-service** | COPAUA0C (1,026), COPAUS0C (1,032), COPAUS1C (604), COPAUS2C (244), CBPAUP0C (386), PAUDBLOD (369), PAUDBUNL (317), DBUNLDGS (366) | 4,344 | Strangler | High |
| **MQ adapters** | COACCT01 (620), CODATE01 (524) | 1,144 | Rewrite | Medium |

### Sub-Phases

#### Phase 4a: IMS Data Migration (Weeks 28–30)

1. Use PAUDBUNL to export IMS segments to flat files
2. Build Java loader to import into `auth_summary` and `auth_detail` PostgreSQL tables
3. IMS hierarchy (parent/child segments) → 2 relational tables with FK relationship
4. Validate: export from IMS, load to PostgreSQL, query all records, compare counts + checksums

#### Phase 4b: MQ Replacement (Weeks 30–32)

1. Build translation layer: COBOL fixed-length record (EBCDIC) ↔ JSON (UTF-8)
2. Replace MQ with SQS FIFO queues (preserve message ordering)
3. Map MQ correlation IDs to SQS message attributes
4. Run MQ and SQS in parallel for 1 week — verify message parity
5. Migrate COPAUA0C, COACCT01, CODATE01 to consume/produce SQS messages

#### Phase 4c: Authorization CICS Chain (Weeks 32–35)

1. Migrate COPAUS0C → COPAUS1C → COPAUS2C as a single unit (LINK chain, shared COMMAREA)
2. These now read from PostgreSQL instead of IMS, and from account-service/card-service APIs instead of VSAM
3. Replace CBPAUP0C (batch purge) with a scheduled Spring Batch job

### Data Stores Affected

| Current | Migration Action | Target |
|---------|-----------------|--------|
| IMS AUTH_SUMMARY segment | Export → `auth_summary` table | PostgreSQL |
| IMS AUTH_DETAIL segment | Export → `auth_detail` table | PostgreSQL |
| MQ request/response queues | Replace with SQS FIFO queues | AWS SQS |

### Integration Bridges

| Bridge | Description | Duration |
|--------|-------------|----------|
| **MQ↔SQS Parallel** | Both MQ and SQS active for 1 week. Messages published to both; consumed from SQS. MQ serves as fallback. | 1 week |
| **IMS Read Proxy** | During Phase 4a, authorization-service reads from PostgreSQL while COBOL programs (if any remain) read from IMS. | Until Phase 4c cutover |

### Rollback Plan
- **IMS:** Reload IMS database from flat file backup (PAUDBLOD). < 2 hours.
- **MQ:** Re-enable MQ consumers; disable SQS. < 1 hour.
- **CICS Chain:** Re-enable COBOL COPAUS0C/1C/2C in CICS CSD. < 1 hour.

### Acceptance Criteria
- [ ] All IMS segments successfully migrated to PostgreSQL (count match, field-level spot checks)
- [ ] IMS DL/I operations (GU, GN, GNP, ISRT, DLET, REPL) correctly emulated by SQL queries
- [ ] SQS message processing matches MQ behavior for 1 week of parallel operation (zero lost messages)
- [ ] COBOL record → JSON translation preserves all field values (including COMP-3 packed decimal)
- [ ] Authorization LINK chain (COPAUS0C→1C→2C) produces identical screen output for 1,000 test cases
- [ ] Expired authorization purge (CBPAUP0C) correctly identifies and removes expired records

---

## Phase 5: Batch Pipeline & Decommission (Weeks 36–42)

### Objective
Migrate remaining batch utilities, establish the complete Java batch pipeline, decommission COBOL infrastructure.

### Programs Migrating

| Service | COBOL Programs | LOC | Strategy | Risk |
|---------|---------------|-----|----------|------|
| **data-migration-tools** | CBEXPORT (582), CBIMPORT (487) | 1,069 | Rewrite → Retire | Low |
| **Remaining batch** | CBACT01C (430), CBACT02C (178), CBACT03C (178), CBCUS01C (178), COBSWAIT (41), CSUTLDTC (157) | 1,162 | Rewrite | Low |

### Deliverables

1. **Java Batch Pipeline** (replacing Control-M + JCL):
   ```
   AWS Step Functions (or Spring Cloud Data Flow):
     POSTTRAN (Spring Batch) → INTCALC (Spring Batch) →
     CREASTMT (Spring Batch) → TRANREPT (Spring Batch) →
     TRANBKP (SQL backup)
   ```

2. **Data Migration Utility** (one-time):
   - Java replacement for CBEXPORT/CBIMPORT
   - Used for final VSAM → PostgreSQL data migration validation
   - Retired after migration is complete

3. **COBOL Decommission**:
   - Disable all CICS programs in CSD
   - Archive VSAM files (retain for 90 days post-migration)
   - Shut down CICS region
   - Shut down IMS database
   - Disconnect MQ queues
   - Archive JCL jobs (retain for audit)

### Data Stores Affected

| Action | Details |
|--------|---------|
| Archive all VSAM files | 90-day retention in cold storage |
| Decommission IMS | After 30-day stability observation |
| Decommission MQ | After SQS-only operation confirmed |
| Decommission DB2 | After PostgreSQL-only operation confirmed |

### Rollback Plan
- **Full rollback to COBOL** is no longer available after Phase 5 decommission (by design)
- 90-day VSAM archive provides emergency data recovery
- 30-day stability observation before final decommission

### Acceptance Criteria
- [ ] Complete Java batch pipeline runs daily cycle end-to-end: POSTTRAN → INTCALC → CREASTMT → TRANREPT
- [ ] Java pipeline SLA ≤ 110% of COBOL pipeline runtime
- [ ] Zero COBOL program executions for 30 consecutive days
- [ ] All VSAM data verified migrated (full count + checksum match across all entities)
- [ ] 90-day VSAM archive created and verified recoverable
- [ ] CICS region shut down
- [ ] All 44 COBOL programs decommissioned

---

## Phase Summary Timeline

```
Week:  1    5    10   15   20   25   30   35   40
       │    │    │    │    │    │    │    │    │
Phase 0 ████                                        Foundation
Phase 1      █████████                               Pilot (Reports + TranTypes)
Phase 2                ██████████                     User Security + Cards
Phase 3                     ██████████████            Account + Transactions
Phase 4                                ██████████     Authorization (IMS/MQ)
Phase 5                                      ████████ Batch Pipeline + Decommission
```

### Parallel Work Streams

| Phase | Team A (2 devs) | Team B (2 devs) |
|-------|-----------------|-----------------|
| 0 | Shared libraries + PostgreSQL | CI/CD + API Gateway |
| 1 | reporting-service | transaction-type-service |
| 2 | user-auth-service | card-service |
| 3 | account-service (Strangler) | transaction-service (Strangler + Batch) |
| 4 | authorization-service (IMS+MQ) | MQ adapters + testing |
| 5 | Batch pipeline orchestration | Decommission + validation |
