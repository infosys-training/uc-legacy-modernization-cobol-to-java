# Risk Register

## Overview

This register documents the top 10 migration risks for the CardDemo COBOL-to-Java modernization, identified through source code analysis of 44 programs, 47 copybooks, and 46 JCL jobs. Each risk is assessed for likelihood and impact, with specific mitigation strategies and early warning indicators.

---

## Risk Summary Matrix

| # | Risk | Likelihood | Impact | Severity Score | Category |
|---|------|-----------|--------|----------------|----------|
| R1 | Hidden business rules in COACTUPC validation logic | High | High | **Critical** | Tribal Knowledge |
| R2 | VSAM-to-PostgreSQL data consistency during dual-write | High | High | **Critical** | Data Coupling |
| R3 | COBOL fixed-point arithmetic precision loss | Medium | High | **High** | Environment Gap |
| R4 | IMS hierarchical-to-relational mapping data loss | Medium | High | **High** | Environment Gap |
| R5 | MQ message ordering and correlation during transition | Medium | High | **High** | Shared Coupling |
| R6 | Batch pipeline SLA degradation on new platform | Medium | Medium | **Medium** | Environment Gap |
| R7 | EBCDIC/ASCII data conversion edge cases | Medium | Medium | **Medium** | Data Testing |
| R8 | BMS screen logic interleaved with business rules | High | Medium | **Medium** | Tribal Knowledge |
| R9 | COBOL talent shortage during parallel-run period | High | Medium | **Medium** | Organizational |
| R10 | Undocumented COPY REPLACING macro behaviors | Low | High | **Medium** | Tribal Knowledge |

---

## Detailed Risk Assessments

### R1: Hidden Business Rules in COACTUPC Validation Logic

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | High |
| **Impact** | High |
| **Category** | Tribal Knowledge |
| **Affected Phase** | Phase 3 (Account Management) |

**Description:**
COACTUPC is 4,236 lines with 359 branching statements (IF/EVALUATE) and 58 copybook references. Many validation rules are implicit in the code structure rather than documented:
- State code validation against 50 US states (embedded in CSLKPCDY)
- ZIP prefix ↔ state correlation checks
- Phone area code validation against NANPA list
- Date format validation with cross-field consistency (expiry > open date)
- SSN format validation with specific prefix exclusions
- Account status transitions (active → closed → reopened) with business rules governing each transition

**Risk:** Incomplete extraction of business rules leads to the Java service accepting invalid data that COBOL would reject, or rejecting valid data that COBOL would accept.

**Mitigation Strategy:**
1. Generate exhaustive test cases BEFORE rewriting (target: 10,000+ input combinations)
2. Run COACTUPC via GnuCOBOL with test inputs; capture accept/reject decisions as baseline
3. Build Java validation service; run same inputs; compare decisions
4. Shadow mode for 2 weeks: Java validates in parallel with COBOL; log every discrepancy
5. Engage business stakeholder to confirm which rules are intentional vs. accidental

**Early Warning Indicators:**
- Shadow mode discrepancy rate > 0.1%
- Test coverage on COACTUPC < 90% branch coverage
- Validation rules discovered during shadow that weren't in test cases
- Business users reporting "false rejects" after cutover

---

### R2: VSAM-to-PostgreSQL Data Consistency During Dual-Write

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | High |
| **Impact** | High |
| **Category** | Data Coupling |
| **Affected Phase** | Phase 3 (Account + Transaction) |

**Description:**
ACCTFILE is written by 5 programs across 4 bounded contexts. TRANSACT is referenced by 21 programs. During the dual-write transition period:
- Both COBOL (VSAM) and Java (PostgreSQL) must reflect the same state
- VSAM lacks native Change Data Capture (CDC)
- Network latency between writes creates consistency windows
- Failure scenarios: Java write succeeds but VSAM write fails (or vice versa)
- Reconciliation detection may lag behind the actual divergence

**Risk:** Data divergence between VSAM and PostgreSQL during transition leads to incorrect balances, lost transactions, or duplicate postings.

**Mitigation Strategy:**
1. Use SQS FIFO queues for synchronization (exactly-once delivery, ordered)
2. Implement hourly reconciliation: row count + checksum comparison
3. Define single source of truth per entity per phase (VSAM until cutover, then PostgreSQL)
4. Keep sync lag < 10 seconds (monitored via CloudWatch alarm)
5. Take VSAM snapshots every 4 hours during dual-write for point-in-time recovery
6. Implement reverse sync (PostgreSQL → VSAM) tested and ready before Phase 3 starts
7. Auto-rollback trigger if reconciliation shows >0.01% mismatch

**Early Warning Indicators:**
- Reconciliation mismatch count > 0 (any mismatch is investigated)
- Sync lag exceeding 30 seconds sustained for > 5 minutes
- VSAM I/O errors during dual-write
- SQS dead letter queue depth > 0

---

### R3: COBOL Fixed-Point Arithmetic Precision Loss

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | Medium |
| **Impact** | High |
| **Category** | Environment Gap |
| **Affected Phase** | Phase 3 (all monetary calculations) |

**Description:**
COBOL uses `PIC S9(10)V99` for monetary values — exact decimal arithmetic with defined precision. Java's `double` and `float` types introduce rounding errors. Key concerns:
- Interest calculation (CBACT04C) compounds across thousands of accounts
- Transaction posting (CBTRN02C) aggregates running balances
- Statement totals must match to the penny
- COBOL's `COMPUTE ROUNDED` has specific rounding semantics
- Overpunch sign encoding (`}` = negative trailing zero) requires careful parsing

**Risk:** Financial calculations produce different results in Java due to floating-point approximation, leading to balance mismatches that compound over time.

**Mitigation Strategy:**
1. **MANDATORY: Use `java.math.BigDecimal` for ALL monetary fields** — never `double`/`float`
2. Map PIC S9(10)V99 → BigDecimal(precision=12, scale=2)
3. Use `RoundingMode.HALF_UP` to match COBOL ROUNDED behavior
4. Create MoneyUtils utility class with arithmetic operations
5. Run interest calculation (CBACT04C) against 10,000 accounts; compare Java vs. COBOL output penny-by-penny
6. Test overpunch decoding exhaustively: positive ({, A-I) and negative (}, J-R)
7. Validate implied decimal (`V` clause) parsing: PIC S9(10)V99 = 10 integer + 2 decimal digits

**Early Warning Indicators:**
- Any monetary assertion failure in unit/integration tests
- Batch balance reconciliation showing penny differences
- Interest calculation total off by > $0.01 from COBOL baseline
- `double` or `float` types appearing in code review for monetary fields

---

### R4: IMS Hierarchical-to-Relational Mapping Data Loss

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | Medium |
| **Impact** | High |
| **Category** | Environment Gap |
| **Affected Phase** | Phase 4 (Authorization Engine) |

**Description:**
IMS DBPAUTP0 uses a hierarchical model with Root (AUTH_SUMMARY) and Child (AUTH_DETAIL) segments. DL/I operations have positional semantics:
- `GNP` (Get Next within Parent) depends on current position in hierarchy
- `GU` with segment search arguments navigates by qualified path
- Multiple child segments under one parent maintain insertion order
- IMS twin chains (multiple children of same type) have ordering significance

**Risk:** Relational tables lose the implicit ordering and positional context that IMS provides, causing authorization history queries to return results in different order or miss parent-child relationships.

**Mitigation Strategy:**
1. Add explicit `sequence_number` column to `auth_details` table preserving IMS insertion order
2. Map all DL/I calls to SQL equivalents with ORDER BY clauses:
   - GU → SELECT WHERE with primary key
   - GN → SELECT with cursor (ORDER BY sequence)
   - GNP → SELECT WHERE parent_id = ? ORDER BY sequence LIMIT 1 OFFSET ?
   - ISRT → INSERT with auto-increment sequence
   - DLET → DELETE with FK cascade
   - REPL → UPDATE WHERE id = ?
3. Validate: Unload IMS via PAUDBUNL → load into PostgreSQL → query results must match
4. Test positional operations specifically (GNP after multiple ISRTs)

**Early Warning Indicators:**
- Authorization history displayed in different order than IMS
- Missing child records after migration (FK integrity check)
- CBPAUP0C (purge) behavior differs: purges wrong records or misses expired ones
- Row count mismatch: IMS segment count ≠ PostgreSQL row count

---

### R5: MQ Message Ordering and Correlation During Transition

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | Medium |
| **Impact** | High |
| **Category** | Shared Data Coupling |
| **Affected Phase** | Phase 4 (Authorization Engine) |

**Description:**
Authorization flow uses MQ request/reply pattern:
- Transaction program → MQPUT1 → Auth Request Queue → COPAUA0C → MQPUT1 → Auth Reply Queue
- Correlation ID links request to reply
- COBOL messages are fixed-length records (CCPAURQY: 200 bytes, CCPAURLY: 150 bytes)
- MQ guarantees in-order delivery within a queue
- During transition, both MQ and SQS must receive and process messages

**Risk:** Message duplication, loss, or mis-correlation during MQ-to-SQS transition causes authorization decisions to be delayed, lost, or applied to wrong transactions.

**Mitigation Strategy:**
1. Use SQS FIFO queues (not standard) for ordering guarantee
2. Map MQ correlation IDs to SQS MessageGroupId + MessageDeduplicationId
3. Build translation layer: COBOL fixed-length record ↔ JSON (deployed as Lambda)
4. Dual-publish during transition: every MQ message also published to SQS
5. Java service consumes from SQS; compare decisions to COBOL (shadow mode)
6. Only cut over consumer once shadow mode shows 100% decision parity for 7 days
7. Keep MQ infrastructure running (read-only) for 30 days post-cutover as replay source

**Early Warning Indicators:**
- SQS dead letter queue messages appearing
- Correlation mismatch: reply arrives with no matching request
- Message age in queue exceeding 5 seconds (latency indicator)
- Decision mismatch between MQ (COBOL) and SQS (Java) paths

---

### R6: Batch Pipeline SLA Degradation on New Platform

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | Medium |
| **Impact** | Medium |
| **Category** | Environment Gap |
| **Affected Phase** | Phase 3, Phase 5 |

**Description:**
Daily batch pipeline (POSTTRAN → INTCALC → CREASTMT → TRANRPT) has established mainframe SLA. Performance factors:
- Mainframe I/O subsystem optimized for sequential VSAM access
- COBOL PERFORM loops are highly optimized by the compiler
- Spring Batch introduces JVM overhead (startup, GC pauses)
- PostgreSQL I/O characteristics differ from VSAM
- CBSTM03A performs 115 I/O operations per account (statement generation)

**Risk:** Java batch pipeline runs slower than COBOL mainframe, causing the daily processing window to overrun and impact next-day online operations.

**Mitigation Strategy:**
1. Benchmark early: run CBACT04C (interest calc) via GnuCOBOL with production-volume data
2. Set target: Java batch ≤ 110% of COBOL runtime
3. Tune PostgreSQL: create indexes matching VSAM key structures; configure HikariCP pool ≥ 20
4. Use JdbcBatchItemWriter with chunk size ≥ 1,000 (not row-by-row)
5. Convert CBSTM03A's 115 individual WRITEs to template-based bulk generation
6. If needed: parallel partition processing (Spring Batch partitioning)
7. Monitor: Java heap, GC time, PostgreSQL query plan, connection pool exhaustion

**Early Warning Indicators:**
- Java batch exceeds 150% of COBOL runtime in staging
- GC pauses > 2 seconds during batch execution
- PostgreSQL connection pool exhaustion during peak processing
- Step Functions timeout on any batch step
- Batch completion time trending upward week-over-week

---

### R7: EBCDIC/ASCII Data Conversion Edge Cases

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | Medium |
| **Impact** | Medium |
| **Category** | Data Representativeness |
| **Affected Phase** | Phase 0 (baseline), Phase 3 (migration) |

**Description:**
Source data in `app/data/` is EBCDIC-encoded with:
- Packed decimal fields (COMP-3): 2 digits per byte + sign nibble
- Overpunch sign encoding in DISPLAY fields: trailing characters {A-I (positive), }J-R (negative)
- REDEFINES creating polymorphic records (same bytes, different interpretation)
- Low-value/high-value sentinels (X'00', X'FF') used as delimiters
- Mainframe collation sequence differs from ASCII (numbers sort after letters in EBCDIC)

**Risk:** Edge cases in data conversion produce corrupted records, incorrect sort orders, or misinterpreted numeric values that only surface under specific data patterns.

**Mitigation Strategy:**
1. Use `wc -c` and `cut -c` to verify every record layout against copybook PIC clauses
2. Build comprehensive overpunch lookup table: test with all 20 characters ({A-I, }J-R)
3. Test COMP-3 packed decimal conversion with: zero, positive, negative, max value, low-values
4. Validate sort order: generate sorted output from COBOL SORT, compare with Java `ORDER BY`
5. Test REDEFINES scenarios: same byte pattern interpreted as multiple field layouts
6. Sample 10% of production data for conversion testing (not just dev sample data)
7. Create data validation job: post-conversion integrity checks on all records

**Early Warning Indicators:**
- Record length mismatches after conversion (expected 300 bytes, got different)
- Negative numbers appearing as very large positive numbers (overpunch parsing failure)
- Sort order differences in paginated results
- NULL/empty fields where data is expected (COMP-3 parsing failure)
- Character corruption in name/address fields (EBCDIC → UTF-8 issues)

---

### R8: BMS Screen Logic Interleaved with Business Rules

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | High |
| **Impact** | Medium |
| **Category** | Tribal Knowledge |
| **Affected Phase** | Phase 2, Phase 3 |

**Description:**
All 17 CICS online programs mix presentation logic with business logic:
- Screen attribute manipulation (CICS SEND MAP with MAPONLY/DATAONLY)
- Cursor positioning based on validation errors
- Field highlighting (BMS DFHBMSCA attributes) used to signal validation state
- EVALUATE TRUE blocks combine input validation with screen redisplay
- Error message assembly references both business rules and screen layout

**Risk:** Extracting business rules from BMS-coupled code misses validation that's expressed only as screen behavior (e.g., "highlight field red" implies "value is invalid" — but the validation condition is only in the screen-handling paragraph).

**Mitigation Strategy:**
1. For each CICS program, catalog all EVALUATE/IF blocks that reference both BMS fields AND data files
2. Create a "validation extraction" document mapping: BMS attribute change → business rule
3. Test strategy: for every screen field with DFHBMSCA highlighting, trace back to the validation rule
4. Build REST API error responses that carry equivalent information (field name + error code)
5. Develop React/Angular form with client-side validation matching server-side rules
6. Cross-reference: every `MOVE DFHBMFSE TO {field}A` (error attribute) must have a corresponding Java validation annotation

**Early Warning Indicators:**
- Undocumented validation rules discovered after API deployment (user reports)
- Frontend/backend validation disagreement (client accepts, server rejects, or vice versa)
- CICS programs with > 20 BMS attribute manipulations (high coupling indicator)
- Tests that only verify data correctness but not screen behavior equivalence

---

### R9: COBOL Talent Shortage During Parallel-Run Period

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | High |
| **Impact** | Medium |
| **Category** | Organizational |
| **Affected Phase** | All Phases (especially Phase 3, Phase 4) |

**Description:**
Migration requires 32–42 weeks with COBOL system running in production throughout:
- Must maintain COBOL production support while building Java replacement
- COBOL developers are scarce and expensive (average age 55+, shrinking pool)
- Knowledge of CardDemo-specific business rules is concentrated in few individuals
- Phase 3 and 4 require deep COBOL reading skills to verify Java parity
- If COBOL SME leaves mid-migration, critical business rule knowledge is lost

**Risk:** Loss of COBOL expertise during migration stalls progress, leaves business rules undocumented, and creates a period where neither old nor new system can be properly maintained.

**Mitigation Strategy:**
1. Designate 1 dedicated COBOL SME for production support (NOT assigned to migration coding)
2. Cross-train 2 Java developers on COBOL reading (not writing) — focus on: EVALUATE/IF, COPY, PERFORM, CICS commands
3. Document ALL business rules before migration begins (not during) — extract to specification docs
4. Create detailed program-level documentation for top 10 hotspot programs
5. Video record COBOL SME walkthroughs of critical programs (COACTUPC, CBTRN02C, COPAUA0C)
6. Establish production runbooks for COBOL operational issues
7. Consider external COBOL consulting firm as backup resource

**Early Warning Indicators:**
- COBOL SME utilization > 80% (burnout risk)
- Undocumented business rules discovered during testing (knowledge gap)
- Production incidents requiring escalation to COBOL SME increasing
- COBOL SME resignation or retirement announced
- Java team asking repeated questions about COBOL behavior (knowledge transfer incomplete)

---

### R10: Undocumented COPY REPLACING Macro Behaviors

| Attribute | Assessment |
|-----------|-----------|
| **Likelihood** | Low |
| **Impact** | High |
| **Category** | Tribal Knowledge |
| **Affected Phase** | Phase 3 (COACTUPC specifically) |

**Description:**
COACTUPC uses `COPY REPLACING` 3 times with CSSETATY copybook:
```cobol
COPY CSSETATY REPLACING ==:ESSION== BY ==ACUP==.
COPY CSSETATY REPLACING ==:ESSION== BY ==ACUP==.
```
This generates repeated code with different prefixes — a macro-like pattern that:
- Creates multiple copies of the same validation logic with different field name prefixes
- Has no direct equivalent in Java (not inheritance, not generics, not annotations)
- May contain subtle differences between copies if any were manually edited post-COPY
- The `:ESSION` vs `:ESSION` variation suggests evolution over time (possibly a bug)

**Risk:** Translating COPY REPLACING to Java incorrectly may miss that each copy handles a slightly different context, or may over-generalize behavior that should remain distinct per field set.

**Mitigation Strategy:**
1. Expand all COPY REPLACING directives: use `cobc -E` to preprocess and see generated code
2. Compare expanded copies: are they truly identical (can use single parameterized method) or subtly different?
3. If identical: create Java parameterized utility method accepting field name prefix as parameter
4. If different: create separate validation methods per context with clear documentation of differences
5. GnuCOBOL preprocessing: `cobc -E -I app/cpy COACTUPC.cbl` produces expanded source for analysis

**Early Warning Indicators:**
- Preprocessed code reveals differences between COPY instances
- Java unit tests fail for one field prefix but pass for others (indicates copy divergence)
- `:ESSION` vs `:ESSION` investigation reveals it's a typo that created an unintended copy
- More COPY REPLACING patterns discovered in other programs during migration

---

## Risk Heatmap

```
                    IMPACT
                Low     Medium      High
            ┌──────────┬──────────┬──────────┐
    High    │          │ R8, R9   │ R1, R2   │
            │          │          │          │
LIKELIHOOD  ├──────────┼──────────┼──────────┤
    Medium  │          │ R6, R7   │ R3,R4,R5 │
            │          │          │          │
            ├──────────┼──────────┼──────────┤
    Low     │          │          │ R10      │
            │          │          │          │
            └──────────┴──────────┴──────────┘
```

---

## Risk Response Plan by Phase

| Phase | Active Risks | Key Mitigation Focus |
|-------|-------------|---------------------|
| P0 (Foundation) | R7, R9 | Data conversion testing; COBOL SME onboarding |
| P1 (Auth + Reports) | R8, R9 | BMS extraction for simple programs; knowledge documentation |
| P2 (DB2 + Cards) | R3, R7, R8 | BigDecimal enforcement; card data conversion; validation extraction |
| P3 (Account + Txn) | R1, R2, R3, R6, R10 | **Critical phase** — all high-severity risks active |
| P4 (Authorization) | R4, R5, R9 | IMS mapping validation; MQ transition testing |
| P5 (Batch + Decom) | R6 | Pipeline SLA validation |

---

## Additional Risks (Monitored, Not Top 10)

| Risk | Likelihood | Impact | Status |
|------|-----------|--------|--------|
| Plain-text passwords in USRSEC | Certain | Medium | Mitigated in P1 (bcrypt migration) |
| GDG file versioning loss | Low | Low | Replaced by timestamped naming |
| Control-M scheduling logic complexity | Low | Medium | Step Functions + EventBridge |
| Assembler routine replacement (COBDATFT, MVSWAIT) | Low | Low | Java utility classes |
| Multi-timezone date handling | Low | Medium | All dates currently CCYYMMDD format |
| Network latency (mainframe → cloud) during dual-run | Medium | Low | Co-locate adapters in same region |
| Regulatory compliance (PCI-DSS for card data) | Medium | High | Separate concern; encryption at rest + transit |

---

## Risk Review Cadence

| Frequency | Activity | Audience |
|-----------|----------|----------|
| Weekly | Review active risks for current phase | Migration team |
| Bi-weekly | Update likelihood/impact based on progress | Steering committee |
| Per-phase gate | Full risk register review before Go/No-Go | All stakeholders |
| Incident-triggered | Immediate risk assessment if discrepancy detected | Migration lead + COBOL SME |
