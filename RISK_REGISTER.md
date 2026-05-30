# Risk Register — CardDemo COBOL-to-Java Migration

> Top 10 migration risks ranked by likelihood × impact, with mitigation strategies and early warning indicators.

---

## 1. Risk Scoring Framework

| Dimension | Scale | Description |
|-----------|-------|-------------|
| **Likelihood** | 1 (Rare) — 5 (Almost Certain) | Probability of the risk materializing |
| **Impact** | 1 (Negligible) — 5 (Critical) | Consequence on timeline, quality, or operations if it occurs |
| **Risk Score** | Likelihood × Impact | 1–25 scale; ≥15 = Critical, 10–14 = High, 5–9 = Medium, 1–4 = Low |

---

## 2. Risk Register

### Risk 1: COACTUPC Validation Logic Parity Failure

| Attribute | Detail |
|-----------|--------|
| **ID** | R-001 |
| **Category** | Functional Parity |
| **Likelihood** | 4 (Likely) |
| **Impact** | 5 (Critical) |
| **Risk Score** | **20 — Critical** |
| **Description** | COACTUPC.cbl (4,236 LOC) contains 359 branching statements performing field-by-field validation (date formats, SSN patterns, phone area codes, US state codes, ZIP+4 formats). Subtle differences in validation logic between Java and COBOL could cause incorrect account updates, data corruption, or rejected valid inputs. |
| **Root Cause** | COBOL's fixed-point arithmetic, implicit decimal handling (PIC S9(10)V99), and COPY REPLACING macro pattern (CSSETATY ×3) have no direct Java equivalents. Edge cases in date validation (leap years, century boundaries) may behave differently. |
| **Mitigation Strategy** | 1. Generate 10,000+ test cases from production data before rewriting. 2. Run COBOL and Java in parallel (shadow mode) for 2 weeks comparing outputs field-by-field. 3. Extract all 5 validators (date, SSN, phone, state, ZIP) as independent, heavily unit-tested Java classes before integrating. 4. Use BigDecimal (not double/float) for all monetary calculations. |
| **Early Warning Indicators** | — Unit test coverage for validation classes drops below 95%. — Shadow mode comparison reveals > 0 mismatches in first 1,000 records. — Team underestimates time for COACTUPC by > 50%. |
| **Owner** | Lead Developer |
| **Phase** | Phase 3 (Core Business) |

---

### Risk 2: VSAM-to-PostgreSQL Data Migration Data Loss

| Attribute | Detail |
|-----------|--------|
| **ID** | R-002 |
| **Category** | Data Integrity |
| **Likelihood** | 3 (Possible) |
| **Impact** | 5 (Critical) |
| **Risk Score** | **15 — Critical** |
| **Description** | 5 core VSAM KSDS files (Account 300B, Customer 500B, Card 150B, XREF 50B, Transaction 350B) must be migrated to PostgreSQL. EBCDIC-to-UTF-8 encoding conversion, packed decimal (COMP-3) to numeric conversion, and FILLER field handling could introduce data loss or corruption. |
| **Root Cause** | COBOL uses EBCDIC encoding, packed decimal (COMP-3), zoned decimal (PIC 9), and fixed-length records with FILLER padding. Misaligned byte boundaries or incorrect COMP-3 unpacking would corrupt numeric data silently. |
| **Mitigation Strategy** | 1. Build ETL pipeline with field-by-field validation (count, checksum, min/max/avg for numeric fields). 2. Run migration against `app/data/` test files first. 3. Implement record-count reconciliation (VSAM record count must equal PostgreSQL row count). 4. Maintain VSAM files as read-only backup for 30 days post-migration. 5. Use established EBCDIC libraries (jt400, mainframe-tools) rather than custom conversion. |
| **Early Warning Indicators** | — Record count mismatch > 0 after test migration. — Numeric field checksums differ between VSAM and PostgreSQL. — Special characters (accented names, ampersands) corrupted in UTF-8 conversion. |
| **Owner** | Data Migration Engineer |
| **Phase** | Phase 0 (Foundation) through Phase 5 |

---

### Risk 3: Dual-Write Consistency During Transition

| Attribute | Detail |
|-----------|--------|
| **ID** | R-003 |
| **Category** | Data Integrity |
| **Likelihood** | 4 (Likely) |
| **Impact** | 4 (Major) |
| **Risk Score** | **16 — Critical** |
| **Description** | During Phases 2–4, some programs write to VSAM while others write to PostgreSQL. The VSAM-DB sync bridges must replicate changes in near-real-time. Sync failures, race conditions, or ordering issues could cause data divergence between the two stores. |
| **Root Cause** | VSAM doesn't support native Change Data Capture (CDC). Sync must be implemented via file monitoring, log scraping, or periodic polling — all of which have latency and ordering limitations. |
| **Mitigation Strategy** | 1. Implement sync with guaranteed-delivery queue (SQS FIFO) between VSAM and PostgreSQL. 2. Add reconciliation job that runs hourly, comparing record counts and checksums. 3. Define a single source of truth for each entity (VSAM until cutover, then PostgreSQL). 4. If sync lag > 10 seconds, alert and pause new writes until caught up. 5. Minimize dual-write duration — each phase should complete within planned weeks. |
| **Early Warning Indicators** | — Sync lag exceeds 5 seconds for > 1 minute. — Hourly reconciliation reports any count or checksum mismatch. — Batch jobs produce different results when reading VSAM vs. PostgreSQL. |
| **Owner** | Data Migration Engineer + Platform Lead |
| **Phase** | Phases 2–5 |

---

### Risk 4: IMS Hierarchical-to-Relational Mapping Errors

| Attribute | Detail |
|-----------|--------|
| **ID** | R-004 |
| **Category** | Data Architecture |
| **Likelihood** | 3 (Possible) |
| **Impact** | 4 (Major) |
| **Risk Score** | **12 — High** |
| **Description** | The IMS database stores authorization data in a hierarchical structure (AUTH_SUMMARY parent → AUTH_DETAIL children) accessed via DL/I calls (GU, GN, GNP, DLET, REPL, ISRT). Mapping this to relational tables may lose navigational semantics (e.g., GNP "get next within parent" cursor behavior) or introduce performance regressions. |
| **Root Cause** | IMS DL/I's segment-level locking, parent-child pointer chains, and position-dependent retrieval (GNP) don't have direct relational equivalents. JOIN-based queries may not replicate the exact traversal order. |
| **Mitigation Strategy** | 1. Map IMS hierarchy to 2 PostgreSQL tables with foreign key: `authorization_summary` (parent) and `authorization_detail` (child). 2. Implement GNP-equivalent as `SELECT ... WHERE summary_id = ? ORDER BY detail_seq ASC` with cursor. 3. Test with full IMS database unload (PAUDBUNL) → load into PostgreSQL → compare COPAUS0C browse output. 4. Keep IMS running in read-only mode for 2 weeks after migration for comparison. |
| **Early Warning Indicators** | — Browse query returns records in different order than IMS GNP. — Parent-child integrity violations (orphaned detail records). — Query performance > 2× IMS response time for same dataset. |
| **Owner** | Lead Developer |
| **Phase** | Phase 4 (Integration) |

---

### Risk 5: MQ Message Format Incompatibility

| Attribute | Detail |
|-----------|--------|
| **ID** | R-005 |
| **Category** | Integration |
| **Likelihood** | 3 (Possible) |
| **Impact** | 4 (Major) |
| **Risk Score** | **12 — High** |
| **Description** | COPAUA0C uses IBM MQ with specific message descriptor formats (MQMD), object descriptors (MQOD), and get/put message options (MQGMO/MQPMO) defined in copybooks CMQMDV, CMQODV, CMQV, CMQGMOV, CMQPMOV. Replacing MQ with Amazon SQS requires message format translation that may lose metadata or change semantics. |
| **Root Cause** | IBM MQ messages carry EBCDIC-encoded payloads with fixed-length COBOL record structures. SQS uses UTF-8 JSON/XML. Translation between formats must preserve every field. MQ features like message grouping, correlation IDs, and priority queues need SQS equivalents. |
| **Mitigation Strategy** | 1. Build a message translation layer that converts COBOL record format to JSON and back. 2. Map MQ correlation IDs to SQS message attributes. 3. Use SQS FIFO queues to preserve message ordering. 4. Run MQ and SQS in parallel with message comparison for 1 week before cutover. 5. Retain MQ connection for rollback during Phase 4. |
| **Early Warning Indicators** | — Message translation produces different field values for test messages. — SQS message size exceeds 256KB limit (unlikely for auth messages but check). — Correlation ID mapping loses request-response pairing. |
| **Owner** | Integration Engineer |
| **Phase** | Phase 4 (Integration) |

---

### Risk 6: Batch Processing Performance Regression

| Attribute | Detail |
|-----------|--------|
| **ID** | R-006 |
| **Category** | Performance |
| **Likelihood** | 3 (Possible) |
| **Impact** | 3 (Moderate) |
| **Risk Score** | **9 — Medium** |
| **Description** | The daily batch pipeline (POSTTRAN → INTCALC → CREASTMT → TRANREPT) has an established runtime SLA on the mainframe. Spring Batch jobs reading from PostgreSQL instead of VSAM may have different I/O characteristics. CBSTM03A's 115 WRITE operations (one per statement line) could bottleneck if not converted to batch writes. |
| **Root Cause** | VSAM KSDS provides direct key-based access with minimal overhead. PostgreSQL queries involve network round-trips, connection pooling, and query planning overhead. Line-by-line writes in COBOL must be converted to buffered/batch writes in Java. |
| **Mitigation Strategy** | 1. Benchmark each Spring Batch job against COBOL baseline with production-sized dataset. 2. Use `JdbcBatchItemWriter` with batch size ≥ 1,000 (not single-row inserts). 3. Tune PostgreSQL: connection pool (HikariCP), indexes matching VSAM key structures, `work_mem` for sorts. 4. Convert CBSTM03A's 97 individual WRITEs to template-based bulk generation. 5. Set performance SLA: Java batch ≤ 110% of COBOL runtime. |
| **Early Warning Indicators** | — Any single Spring Batch step takes > 120% of COBOL equivalent for same data volume. — Database connection pool exhaustion during batch window. — Statement generation throughput < 100 statements/second. |
| **Owner** | Performance Engineer |
| **Phase** | Phases 1 and 5 |

---

### Risk 7: COBOL-Specific Numeric Precision Loss

| Attribute | Detail |
|-----------|--------|
| **ID** | R-007 |
| **Category** | Functional Parity |
| **Likelihood** | 4 (Likely) |
| **Impact** | 3 (Moderate) |
| **Risk Score** | **12 — High** |
| **Description** | COBOL uses fixed-point decimal arithmetic (PIC S9(10)V99) with exact precision for monetary calculations. Java's `double` and `float` types introduce floating-point rounding errors. Interest calculations (CBACT04C), balance updates, and transaction posting must match COBOL output to the cent. |
| **Root Cause** | COBOL's COMPUTE with ON SIZE ERROR and ROUNDED clause provides deterministic decimal arithmetic. Java's IEEE 754 floating-point is non-deterministic for decimal fractions (e.g., 0.1 + 0.2 ≠ 0.3). |
| **Mitigation Strategy** | 1. **Mandatory: Use `java.math.BigDecimal` for ALL monetary fields.** Never use `double` or `float`. 2. Set scale and rounding mode to match COBOL: `BigDecimal.setScale(2, RoundingMode.HALF_UP)`. 3. Map PIC S9(10)V99 to `BigDecimal` with precision 12, scale 2. 4. Create a `MoneyUtils` class that enforces rounding rules consistently. 5. Run 10,000 interest calculations comparing Java vs. COBOL output. |
| **Early Warning Indicators** | — Any code review reveals `double` or `float` used for monetary values. — Interest calculation output differs by even $0.01 from COBOL baseline. — Accumulated rounding errors across batch of > 10,000 transactions. |
| **Owner** | Lead Developer |
| **Phase** | All phases (especially Phase 3 and 5) |

---

### Risk 8: BMS Screen Logic Extraction Failure

| Attribute | Detail |
|-----------|--------|
| **ID** | R-008 |
| **Category** | Architecture |
| **Likelihood** | 3 (Possible) |
| **Impact** | 3 (Moderate) |
| **Risk Score** | **9 — Medium** |
| **Description** | 20 CICS programs embed BMS screen interaction (SEND MAP, RECEIVE MAP) directly in business logic. The CSSETATY copybook uses COPY REPLACING to generate screen attribute-setting code 3 times in COACTUPC alone. Extracting business logic from screen logic is a prerequisite for API-based architecture but is error-prone. |
| **Root Cause** | COBOL's procedural structure doesn't separate UI from business logic. BMS attribute flags (DFHBMSCA) control field highlighting, protection, and cursor position — all embedded in EVALUATE/IF blocks alongside validation. |
| **Mitigation Strategy** | 1. For each CICS program, identify all paragraphs/sections that contain EXEC CICS SEND/RECEIVE and isolate them. 2. Create a "business logic core" that accepts/returns POJOs — no CICS references. 3. Map DFHBMSCA attributes to frontend CSS classes. 4. Use the existing BMS map definitions (`app/bms/`) as the source of truth for field layout. 5. Start with simpler screens (COUSR00C, 11 CICS stmts) before tackling COACTUPC (17 CICS stmts). |
| **Early Warning Indicators** | — Business logic extraction produces code with residual CICS references. — Extracted logic has different control flow than original (missing error paths). — Screen field count in React/Angular doesn't match BMS map field count. |
| **Owner** | Frontend + Backend Lead |
| **Phase** | Phases 1–4 |

---

### Risk 9: COBOL Talent Availability During Transition

| Attribute | Detail |
|-----------|--------|
| **ID** | R-009 |
| **Category** | People & Skills |
| **Likelihood** | 3 (Possible) |
| **Impact** | 4 (Major) |
| **Risk Score** | **12 — High** |
| **Description** | During the 8–10 month migration, the COBOL system must remain operational. Bug fixes, production incidents, and data corrections require COBOL expertise. If the COBOL-skilled team members are fully allocated to the Java migration, production support suffers. Conversely, if too many stay on COBOL support, migration velocity drops. |
| **Root Cause** | COBOL developer pool is shrinking globally. The team must maintain expertise in both COBOL and Java simultaneously during transition. Knowledge of CardDemo-specific business rules (validation logic, batch pipeline sequencing) is concentrated in a few people. |
| **Mitigation Strategy** | 1. Designate 1 COBOL SME for production support (not allocated to migration). 2. Document all business rules extracted from COBOL in the knowledge base before the SME's knowledge is needed. 3. Use the analysis artifacts (APPLICATION_INVENTORY, DATA_DICTIONARY, DEPENDENCY_MAP) as onboarding material. 4. Cross-train at least 2 Java developers on COBOL reading comprehension (not writing). 5. Establish runbooks for common production issues. |
| **Early Warning Indicators** | — Production incidents take > 4 hours to diagnose. — COBOL SME is pulled into migration tasks > 20% of their time. — Knowledge gaps found during Java development that require COBOL analysis. |
| **Owner** | Project Manager |
| **Phase** | All phases |

---

### Risk 10: Rollback Failure During Phase 3 (Core Business)

| Attribute | Detail |
|-----------|--------|
| **ID** | R-010 |
| **Category** | Operational |
| **Likelihood** | 2 (Unlikely) |
| **Impact** | 5 (Critical) |
| **Risk Score** | **10 — High** |
| **Description** | Phase 3 migrates the ACCTFILE (most shared dataset, written by 5 programs across 4 contexts). If the Java account-service fails in production and rollback is needed, VSAM files may be out of sync with PostgreSQL due to the dual-write period. Restoring VSAM from backup could lose transactions written since the backup. |
| **Root Cause** | ACCTFILE is the most heavily coupled dataset in the estate. During dual-write, both VSAM and PostgreSQL receive writes. A rollback to VSAM-only operation must ensure no transactions are lost that were written only to PostgreSQL. |
| **Mitigation Strategy** | 1. Take VSAM snapshots every 4 hours during Phase 3 (not just at phase start). 2. Implement reverse sync: PostgreSQL → VSAM for the rollback scenario. 3. Run a Phase 3 dress rehearsal in staging with simulated failure and rollback. 4. Define "point of no return" clearly: once 100% of traffic is on Java for > 7 days, rollback path changes to "fix forward." 5. Keep VSAM files allocated (not deleted) for 30 days post-Phase 3 cutover. |
| **Early Warning Indicators** | — VSAM snapshot job fails or takes > 30 minutes. — Reverse sync test shows data loss > 0 records. — Phase 3 dress rehearsal rollback takes > 30 minutes. |
| **Owner** | Operations Lead |
| **Phase** | Phase 3 (Core Business) |

---

## 3. Risk Heat Map

```
Impact
  5 │ R-002(15)    R-001(20)     R-010(10)
    │              
  4 │ R-004(12)    R-003(16)     R-009(12)
    │ R-005(12)
  3 │ R-006(9)     R-007(12)     R-008(9)
    │
  2 │
    │
  1 │
    └──────────────────────────────────────
      1          2          3          4          5
                      Likelihood
```

---

## 4. Risk Summary Table

| Rank | ID | Risk | Score | Category | Phase | Mitigation Priority |
|------|----|------|-------|----------|-------|-------------------|
| 1 | R-001 | COACTUPC validation parity failure | **20** | Functional Parity | P3 | Immediate — start test case generation in P0 |
| 2 | R-003 | Dual-write consistency | **16** | Data Integrity | P2–5 | Immediate — design sync architecture in P0 |
| 3 | R-002 | VSAM-to-PostgreSQL data loss | **15** | Data Integrity | P0–5 | Immediate — build ETL pipeline in P0 |
| 4 | R-004 | IMS hierarchical mapping errors | **12** | Data Architecture | P4 | High — prototype mapping in P0 |
| 5 | R-005 | MQ message format incompatibility | **12** | Integration | P4 | High — define message schema in P0 |
| 6 | R-007 | Numeric precision loss (float vs BigDecimal) | **12** | Functional Parity | All | High — enforce BigDecimal standard in P0 |
| 7 | R-009 | COBOL talent availability | **12** | People & Skills | All | Medium — designate SME and cross-train in P0 |
| 8 | R-010 | Rollback failure during Phase 3 | **10** | Operational | P3 | Medium — dress rehearsal in P2 |
| 9 | R-006 | Batch performance regression | **9** | Performance | P1, P5 | Medium — benchmark in P1 pilot |
| 10 | R-008 | BMS screen logic extraction | **9** | Architecture | P1–4 | Medium — start with simple screens in P1 |

---

## 5. Risk Response Actions by Phase

### Phase 0 (Foundation)
- [ ] Generate 10,000+ validation test cases for R-001
- [ ] Build VSAM → PostgreSQL ETL pipeline for R-002
- [ ] Design dual-write sync architecture for R-003
- [ ] Prototype IMS → relational mapping for R-004
- [ ] Define MQ → SQS message schema mapping for R-005
- [ ] Establish BigDecimal coding standard for R-007
- [ ] Designate COBOL SME and begin cross-training for R-009

### Phase 1 (Quick Wins)
- [ ] Validate BigDecimal usage in statement generation for R-007
- [ ] Benchmark CBSTM03A Spring Batch vs COBOL for R-006
- [ ] Extract COUSR00C screen logic as pilot for R-008

### Phase 2 (Database Bridge)
- [ ] Activate and test VSAM-DB sync bridges for R-003
- [ ] Run Phase 3 dress rehearsal (rollback test) for R-010
- [ ] Validate DB2 → PostgreSQL SQL translation for R-004

### Phase 3 (Core Business) — **Highest Risk Phase**
- [ ] Shadow mode for COACTUPC: compare Java vs COBOL output for R-001
- [ ] 4-hourly VSAM snapshots for R-010
- [ ] Continuous reconciliation for R-003
- [ ] Production COBOL SME on standby for R-009

### Phase 4 (Integration)
- [ ] IMS data migration with full validation for R-004
- [ ] MQ-SQS parallel testing for R-005
- [ ] Monitor SQS throughput vs MQ baseline for R-006

### Phase 5 (Batch Pipeline)
- [ ] Full pipeline benchmark for R-006
- [ ] Final VSAM-DB sync bridge decommission for R-003
- [ ] Batch numeric output comparison for R-007
