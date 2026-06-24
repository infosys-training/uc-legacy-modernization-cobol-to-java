# CardDemo Migration Risk Register

> Top 10 migration risks ranked by composite severity (Likelihood × Impact), with mitigation strategies and early warning indicators.

---

## Risk Severity Matrix

| | Impact: Low | Impact: Medium | Impact: High |
|---|---|---|---|
| **Likelihood: High** | Monitor | Mitigate | **Critical** |
| **Likelihood: Medium** | Accept | Mitigate | Mitigate |
| **Likelihood: Low** | Accept | Monitor | Mitigate |

---

## Risk Register

### R1: Hidden Business Rules in COACTUPC (Tribal Knowledge)

| Attribute | Value |
|-----------|-------|
| **Category** | Hidden Business Rules |
| **Likelihood** | **High** |
| **Impact** | **High** |
| **Severity** | **CRITICAL** |
| **Affected Phase** | Phase 3b (Account Update Migration) |

**Description:** COACTUPC contains 4,236 LOC with 185 branching statements and 58 copybooks. The validation logic (date, SSN, phone, state, ZIP, amount limits) encodes business rules that may not be documented anywhere outside the COBOL source. The 3× COPY REPLACING macro usage (CSSETATY) generates repeated code with different field prefixes — the generated behavior varies based on which fields are substituted. Edge cases in date validation (leap year + century boundary + DOB-in-past combinations) may contain rules that only a long-tenured COBOL developer understands.

**Concrete example:** The date validation in CSUTLDPY checks `WS-EDIT-DATE-CC` for `THIS-CENTURY (VALUE 20)` and `LAST-CENTURY (VALUE 19)` — what happens for century 21? Is that intentionally rejected or a Y2.1K bug? Without tribal knowledge, this is ambiguous.

**Mitigation Strategy:**
1. **Before rewriting:** Generate a comprehensive test matrix from COACTUPC's branching paths. Aim for 10,000+ test cases covering all 185 branches.
2. Run shadow mode (COBOL primary, Java shadow) for 2+ weeks. Log every input and output pair.
3. Identify all 88-level conditions (named values) in copybooks — these encode business rules implicitly.
4. Interview available COBOL SMEs before migration begins. Document findings in a rules catalog.
5. Any discrepancy between COBOL and Java output during shadow mode → investigate before cutover.

**Early Warning Indicators:**
- Shadow mode match rate drops below 99.9%
- Test case generation discovers branches with no obvious business justification
- COBOL SME unavailable or unable to explain specific validation rules
- COPY REPLACING macro generates unexpected field combinations in certain contexts

---

### R2: ACCTFILE Shared-Write Consistency During Dual-Write

| Attribute | Value |
|-----------|-------|
| **Category** | Shared Data Coupling |
| **Likelihood** | **High** |
| **Impact** | **High** |
| **Severity** | **CRITICAL** |

**Description:** ACCTDATA.VSAM.KSDS is the most shared file in the estate — written by 5 programs across 4 contexts (COACTUPC, CBACT04C, CBTRN02C, COBIL00C, CBIMPORT). During the Phase 3 Strangler migration, some programs will write to VSAM while others write to PostgreSQL. VSAM lacks native Change Data Capture (CDC), making real-time sync inherently difficult. Race conditions between COBOL VSAM writes and Java PostgreSQL writes can result in balance discrepancies — which in a financial application means real monetary errors.

**Mitigation Strategy:**
1. Use SQS FIFO queues for VSAM↔PostgreSQL synchronization
2. Run hourly reconciliation: count + checksum comparison across both stores
3. Define **single source of truth per entity** at each point in the migration (never dual-primary)
4. Take VSAM snapshots every 4 hours as rollback safety net
5. Keep sync lag < 10 seconds; alert on > 30 seconds
6. Implement reverse sync (PostgreSQL → VSAM) for the transition period when Java becomes primary

**Early Warning Indicators:**
- Reconciliation count mismatch between VSAM and PostgreSQL
- Sync lag exceeds 30 seconds sustained
- Balance discrepancy detected in any account during dual-write period
- SQS FIFO queue depth growing (messages not being consumed)

---

### R3: COBOL Fixed-Point Arithmetic → Java Rounding Errors

| Attribute | Value |
|-----------|-------|
| **Category** | Data Representativeness |
| **Likelihood** | **High** |
| **Impact** | **High** |
| **Severity** | **CRITICAL** |

**Description:** COBOL PIC S9(10)V99 provides exact decimal arithmetic with implied decimal point. Java `double` and `float` use IEEE 754 floating-point, which introduces rounding errors on monetary values. For example, `0.1 + 0.2 != 0.3` in floating-point. In a credit card system processing millions of transactions, even sub-penny rounding errors compound into real financial discrepancies. Additionally, COBOL overpunch encoding (trailing `{`, `}`, `A`-`I`, `J`-`R`) must be correctly decoded — a prior migration attempt showed `00000001940}` was incorrectly decoded as -0.00 instead of -194.00.

**Mitigation Strategy:**
1. **MANDATORY:** Use `java.math.BigDecimal` for ALL monetary fields. Never use `double` or `float`.
2. Map PIC S9(n)V99 → `BigDecimal(precision=n+2, scale=2)` with `RoundingMode.HALF_UP`
3. Create `MoneyUtils` utility class with centralized arithmetic operations
4. Test overpunch decoding exhaustively: both positive (`{`, `A`-`I`) and negative (`}`, `J`-`R`) with various digit combinations including trailing zeros
5. Validate all monetary calculations by comparing COBOL and Java output to the exact penny

**Early Warning Indicators:**
- Any `double` or `float` variable appearing in monetary code during code review
- Penny-level discrepancies in balance comparison tests
- Overpunch test failures on values ending in zero (the most common mistake)
- Interest calculation (CBACT04C) producing different results for the same input data

---

### R4: IMS Hierarchical-to-Relational Mapping Loss

| Attribute | Value |
|-----------|-------|
| **Category** | Environment Gap (Mainframe-Specific Runtime) |
| **Likelihood** | **Medium** |
| **Impact** | **High** |
| **Severity** | **HIGH** |

**Description:** 7 programs use IMS DL/I calls (GU, GN, GNP, DLET, REPL, ISRT) for hierarchical data navigation. The `GNP` (Get Next within Parent) operation is positional — it returns the next child segment under the current parent without needing an explicit foreign key. SQL has no direct equivalent for positional navigation within a parent context. The IMS hierarchy for authorization data (AUTH_SUMMARY parent → AUTH_DETAIL children) must be correctly flattened into relational tables while preserving the parent-child traversal order.

**Mitigation Strategy:**
1. Map IMS 2-level hierarchy to 2 relational tables: `auth_summary` (parent) and `auth_detail` (child with FK + sequence number)
2. Replace `GNP` with `SELECT * FROM auth_detail WHERE summary_id = ? ORDER BY sequence_num`
3. Use PAUDBUNL to export IMS data; build Java loader with explicit sequence numbering
4. Validate by comparing IMS unload output with PostgreSQL query results for all parent-child traversals

**Early Warning Indicators:**
- `GNP` equivalent query returns child records in different order than IMS
- Authorization detail records appear duplicated or missing after migration
- IMS segment lengths don't match expected record layouts in CIPAUSMY/CIPAUDTY copybooks

---

### R5: MQ-to-SQS Message Format Translation

| Attribute | Value |
|-----------|-------|
| **Category** | Environment Gap |
| **Likelihood** | **Medium** |
| **Impact** | **Medium** |
| **Severity** | **MEDIUM** |

**Description:** 4 programs (COPAUA0C, COACCT01, CODATE01) use IBM MQ with EBCDIC-encoded fixed-length COBOL record payloads. SQS uses UTF-8 JSON. MQ features — correlation IDs, message priority, message grouping — have different SQS equivalents. The translation layer must preserve all semantic meaning while converting between fundamentally different message formats. COPAUA0C uses MQPUT1 (put-one, fire-and-forget) which has no direct SQS analog.

**Mitigation Strategy:**
1. Build bidirectional translation layer: COBOL record (EBCDIC, fixed-length) ↔ JSON (UTF-8)
2. Map MQ correlation IDs → SQS MessageDeduplicationId + custom attributes
3. Use SQS FIFO for ordering guarantees (replaces MQ message grouping)
4. Run MQ and SQS in parallel for 1 week before cutover — publish to both, consume from SQS, compare results
5. MQPUT1 → SQS SendMessage with a response listener (add acknowledgment pattern)

**Early Warning Indicators:**
- Character encoding errors in translated messages (EBCDIC special characters → UTF-8)
- SQS message ordering differs from MQ consumption order
- Correlation ID mapping fails for multi-step request/response flows
- Message loss during parallel operation (MQ message count ≠ SQS message count)

---

### R6: Batch Pipeline SLA Degradation

| Attribute | Value |
|-----------|-------|
| **Category** | Performance |
| **Likelihood** | **Medium** |
| **Impact** | **Medium** |
| **Severity** | **MEDIUM** |

**Description:** The daily batch pipeline (POSTTRAN → INTCALC → CREASTMT → TRANREPT) runs on mainframe hardware optimized for sequential I/O. Mainframe I/O subsystems can process VSAM sequential reads at rates that commodity hardware may not match. CBSTM03A alone performs 115 I/O operations. Spring Batch + PostgreSQL on cloud infrastructure may have different I/O characteristics, especially for large sequential scans.

**Mitigation Strategy:**
1. Use `JdbcBatchItemWriter` with batch size ≥ 1,000 (avoid row-by-row inserts)
2. Tune PostgreSQL: HikariCP connection pool sizing, appropriate indexes matching VSAM key structures
3. Convert CBSTM03A's 115 individual WRITEs to template-based bulk generation
4. Target: Java batch ≤ 110% of COBOL runtime (allow 10% overhead for cloud I/O)
5. If SLA is exceeded, implement partitioned processing (split data by account range across multiple workers)

**Early Warning Indicators:**
- Phase 1 pilot (CBSTM03A) Java runtime exceeds 120% of COBOL runtime
- PostgreSQL query plan shows sequential scans where VSAM used keyed access
- Connection pool exhaustion under load
- GC pauses during large batch runs

---

### R7: CICS COMMAREA State Management Loss

| Attribute | Value |
|-----------|-------|
| **Category** | Hidden Business Rules |
| **Likelihood** | **Medium** |
| **Impact** | **Medium** |
| **Severity** | **MEDIUM** |

**Description:** CICS programs pass state via COMMAREA (COCOM01Y — 200+ bytes). This includes navigation context (FROM-PROGRAM, TO-PROGRAM), user context (USER-ID, USER-TYPE), and business context (ACCT-ID, CARD-NUM, CUST-ID). The hub-and-spoke pattern means COMEN01C populates COMMAREA fields that downstream programs depend on. In a REST architecture, this becomes stateless — but some programs may implicitly rely on COMMAREA state that was set by a *previous* program in the navigation chain, creating hidden dependencies.

**Concrete example:** COACTUPC reads `CDEMO-ACCT-ID` and `CDEMO-CARD-NUM` from COMMAREA — these were set by COMEN01C based on user input. If the Java API receives a direct REST call without going through the "menu," the account/card context may be missing.

**Mitigation Strategy:**
1. Map all COMMAREA fields to JWT claims (user context) + request parameters (business context)
2. Trace each COMMAREA field's writer and reader programs to identify implicit state dependencies
3. Ensure every REST endpoint explicitly requires all needed context as parameters (no implicit state)
4. Test navigation flows end-to-end: sign-on → menu → program → sub-program → return

**Early Warning Indicators:**
- REST endpoint returns unexpected results when called directly (bypassing menu flow)
- Null/missing context fields that were always present in CICS but absent in REST calls
- Programs that check `CDEMO-PGM-CONTEXT` (0 = initial, 1 = re-entry) behave differently as REST endpoints

---

### R8: BMS Screen Logic Coupling

| Attribute | Value |
|-----------|-------|
| **Category** | Shared Data Coupling / Architecture |
| **Likelihood** | **Medium** |
| **Impact** | **Medium** |
| **Severity** | **MEDIUM** |

**Description:** 21 online CICS programs each have a paired BMS map (16 BMS files in `app/bms/`). Business logic and screen presentation logic are interleaved in the same COBOL program — COACTUPC contains both account validation rules AND BMS SEND/RECEIVE MAP commands. The COPY REPLACING macro (CSSETATY) dynamically sets screen field attributes (color to red for errors, asterisk for blank fields) based on validation results. Extracting business logic from screen logic requires careful identification of which code paths affect data vs. display.

**Mitigation Strategy:**
1. For each CICS program, classify every paragraph as: business logic, screen I/O, or mixed
2. Extract business logic into service classes first; leave screen I/O as a thin wrapper
3. Replace BMS SEND/RECEIVE MAP with REST API request/response
4. CSSETATY (screen attribute macros) → validation error response DTOs with field-level error flags
5. Test business logic independently from screen rendering

**Early Warning Indicators:**
- Business logic extraction introduces regressions (logic depended on screen state)
- CSSETATY REPLACING macros generate code that modifies business variables (not just display attributes)
- Mixed paragraphs that both validate data AND set screen attributes simultaneously

---

### R9: COBOL Talent Availability During Migration

| Attribute | Value |
|-----------|-------|
| **Category** | Team / Organizational |
| **Likelihood** | **High** |
| **Impact** | **Medium** |
| **Severity** | **HIGH** |

**Description:** The migration spans 8–10 months. During this period, the COBOL system remains in production and requires maintenance. COBOL developer talent is shrinking globally. If the team's COBOL SME becomes unavailable (departure, illness, competing priorities), the ability to diagnose production issues, explain business rules, and validate migration parity is severely impacted.

**Mitigation Strategy:**
1. Designate 1 COBOL SME for production support (not assigned to migration work)
2. Document ALL business rules before migration begins (Phase 0 deliverable)
3. Cross-train 2 Java developers on COBOL reading (not writing — just comprehension)
4. Establish production runbooks for all batch jobs and common CICS issues
5. Record video walkthroughs of the most complex programs (COACTUPC, COPAUA0C)
6. Front-load COBOL SME involvement in Phase 0–1; reduce dependency by Phase 3

**Early Warning Indicators:**
- COBOL SME assigned to multiple projects or shows signs of departure risk
- Questions about business rules go unanswered for > 2 business days
- Java developers unable to trace COBOL program flow during code review
- Production COBOL issues take > 1 day to diagnose

---

### R10: Test Data Representativeness

| Attribute | Value |
|-----------|-------|
| **Category** | Data Representativeness |
| **Likelihood** | **Medium** |
| **Impact** | **High** |
| **Severity** | **HIGH** |

**Description:** The test data in `app/data/` contains EBCDIC-encoded sample records. These may not represent the full range of production data: edge cases (zero balances, maximum credit limits, expired accounts, cards with special characters in embossed names, transactions at boundary dates, accounts with 100+ cards). If the test data doesn't exercise all code paths, the migration may pass testing but fail on production data patterns.

**Specific concern:** COBOL programs handle various VSAM response codes (NOTFND, DUPKEY, NOSPACE, etc.) — the test data may never trigger these error paths, leaving error handling untested in the Java migration.

**Mitigation Strategy:**
1. Use GnuCOBOL to compile and run 8 compilable batch programs locally against test data — establish baseline output
2. Generate synthetic edge-case data: zero balances, max values (PIC 9(11) = 99999999999), expired dates, special characters
3. Specifically test VSAM error conditions: NOTFND, DUPKEY, NOSPACE equivalents in PostgreSQL (NOT FOUND, UNIQUE CONSTRAINT, DISK FULL)
4. If production data is available (anonymized), use it for shadow mode testing in Phase 3
5. Validate that all 88-level conditions (named values like CDEMO-USRTYP-ADMIN VALUE 'A') are covered by test cases

**Early Warning Indicators:**
- Code coverage < 80% on migrated Java services (branches not exercised)
- Shadow mode discovers inputs that Java handles differently (paths not covered by test data)
- VSAM error code handling in COBOL has no corresponding test case in Java
- Production data contains field values outside the range of test data (discovered during dual-write)

---

## Risk Summary Dashboard

| # | Risk | L | I | Severity | Phase | Status |
|---|------|---|---|----------|-------|--------|
| R1 | Hidden business rules (COACTUPC tribal knowledge) | H | H | **CRITICAL** | 3b | Open |
| R2 | ACCTFILE dual-write consistency | H | H | **CRITICAL** | 3a–3c | Open |
| R3 | COBOL fixed-point → Java rounding errors | H | H | **CRITICAL** | All | Open |
| R4 | IMS hierarchical-to-relational mapping | M | H | HIGH | 4a | Open |
| R5 | MQ-to-SQS message format translation | M | M | MEDIUM | 4b | Open |
| R6 | Batch pipeline SLA degradation | M | M | MEDIUM | 1, 5 | Open |
| R7 | CICS COMMAREA state management loss | M | M | MEDIUM | 2–3 | Open |
| R8 | BMS screen logic coupling | M | M | MEDIUM | 2–3 | Open |
| R9 | COBOL talent availability | H | M | HIGH | All | Open |
| R10 | Test data representativeness | M | H | HIGH | All | Open |

### Critical Risk Count by Phase

| Phase | Critical | High | Medium |
|-------|----------|------|--------|
| Phase 0 (Foundation) | 1 (R3) | 1 (R9) | 0 |
| Phase 1 (Pilot) | 1 (R3) | 1 (R10) | 1 (R6) |
| Phase 2 (Security+Cards) | 1 (R3) | 1 (R9) | 2 (R7, R8) |
| **Phase 3 (Account+Txn)** | **3 (R1, R2, R3)** | **2 (R9, R10)** | **2 (R7, R8)** |
| Phase 4 (Auth IMS/MQ) | 1 (R3) | 2 (R4, R9) | 1 (R5) |
| Phase 5 (Batch+Decommission) | 1 (R3) | 1 (R10) | 1 (R6) |

**Key insight:** Phase 3 is the riskiest phase with 3 critical + 2 high risks converging. This validates the decision to defer it until Phases 1–2 prove the migration patterns.
