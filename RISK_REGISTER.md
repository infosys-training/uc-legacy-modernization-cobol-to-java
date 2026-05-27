# CardDemo Migration Risk Register

## Overview

This document identifies the top 10 migration risks for the CardDemo modernization, based on analysis of the COBOL codebase, data coupling patterns, platform dependencies, and common mainframe migration failure modes. Each risk is assessed for likelihood and impact, with specific mitigation strategies and early warning indicators.

---

## Risk Matrix Summary

| # | Risk | Likelihood | Impact | Priority |
|---|---|---|---|---|
| R-01 | Hidden business rules in COACTUPC validation logic | High | High | **Critical** |
| R-02 | COMP-3/packed decimal precision loss in financial calculations | High | High | **Critical** |
| R-03 | Shared VSAM data coupling prevents clean domain extraction | High | Medium | **High** |
| R-04 | CICS-specific runtime behavior not reproducible | Medium | High | **High** |
| R-05 | Insufficient test data representativeness | High | Medium | **High** |
| R-06 | Mainframe-specific assembler dependencies (MVSWAIT, COBDATFT) | Medium | Medium | **Medium** |
| R-07 | IMS hierarchical data model mismatch with relational target | Medium | High | **High** |
| R-08 | COBOL skill attrition during multi-phase migration | High | Medium | **High** |
| R-09 | Batch job timing and sequencing dependencies | Medium | Medium | **Medium** |
| R-10 | BMS screen map / 3270 UI logic interleaved with business rules | Medium | Medium | **Medium** |

---

## Detailed Risk Analysis

### R-01: Hidden Business Rules in COACTUPC Validation Logic

**Category:** Business Logic Complexity

**Description:**
COACTUPC is the largest program (4,236 LOC) and contains extensive field-level validation for account updates: US phone number format validation (area code + exchange + number with specific delimiter checking), SSN validation (with IRS-mandated exclusion of area numbers 000, 666, and 900–999), date validation (via CEEDAYS Language Environment call), credit limit enforcement, leap year calculation, and mandatory field checks. These rules are deeply embedded in COBOL paragraph logic and not documented externally. Several validation rules use 88-level condition names with complex REDEFINES, making them easy to misinterpret during translation.

**Evidence from codebase:**
- `COACTUPC.cbl:117–146` — SSN validation with `INVALID-SSN-PART1 VALUES 0, 666, 900 THRU 999`
- `COACTUPC.cbl:82–115` — US phone number format with 3-part numeric validation
- `COACTUPC.cbl:166` — COPY 'CSUTLDWY' for date validation via CEEDAYS
- `COACTUPC.cbl:56–80` — Signed number validation with blank/invalid/valid flag states

**Likelihood:** High — Every mainframe migration encounters undocumented business rules. COACTUPC's 4,236 LOC of interleaved UI and validation logic is especially prone to misinterpretation.

**Impact:** High — Incorrect validation allows bad data into the system (e.g., invalid SSNs, malformed phone numbers) or rejects valid updates that COBOL would have accepted.

**Mitigation Strategy:**
1. **Extract validation rules catalog:** Before rewriting, create a formal specification document for every validation in COACTUPC by tracing each 88-level condition and PERFORM paragraph.
2. **Generate test cases from production data:** Run the COBOL program with a representative set of production account updates and capture the accept/reject decisions as golden test cases.
3. **Property-based testing:** For each validation rule, generate boundary-value test cases (e.g., SSN 000-xx-xxxx, 666-xx-xxxx, 899-xx-xxxx, 900-xx-xxxx).
4. **Shadow mode deployment:** Run the Java validation alongside the COBOL validation for 30 days, comparing results for every transaction.

**Early Warning Indicators:**
- Test case count for COACTUPC replacement is less than 500 (insufficient coverage)
- Shadow mode comparison shows >0.1% disagreement rate
- Java implementation has fewer validation rules than the number of 88-level conditions in COACTUPC

---

### R-02: COMP-3/Packed Decimal Precision Loss in Financial Calculations

**Category:** Environment Gap (Mainframe-Specific Runtime)

**Description:**
COBOL uses `COMP-3` (packed decimal) and `COMP` (binary) storage for financial calculations. These provide exact decimal arithmetic — there is no floating-point rounding. Key financial fields include:
- `ACCT-CURR-BAL PIC S9(10)V99` (account balance — 12-digit packed decimal)
- `TRAN-AMT PIC S9(09)V99` (transaction amount)
- `DIS-INT-RATE PIC S9(04)V99` (interest rate)
- `TRAN-CAT-BAL PIC S9(09)V99` (category balance)

CBACT04C performs interest calculation by multiplying category balances by interest rates, which in COBOL produces exact intermediate results truncated to the target precision. Java `double` or `float` would introduce rounding errors; even `BigDecimal` requires careful scale and rounding mode selection to match COBOL truncation semantics.

CBSTM03A intentionally uses `COMP-3` variables and `COMP` counters as modernization test cases — the program header explicitly states it exercises "COMP and COMP-3 variables" to challenge migration tooling.

**Likelihood:** High — Precision mismatches are the most common defect in COBOL-to-Java financial migrations.

**Impact:** High — Even 1-cent discrepancies in interest calculations can compound across thousands of accounts and trigger regulatory audit findings. Statement balances that don't match ledger balances undermine customer trust.

**Mitigation Strategy:**
1. **Mandate `BigDecimal` with explicit scale:** All financial fields must use `BigDecimal` with `setScale(2, RoundingMode.DOWN)` to match COBOL truncation behavior (not banker's rounding).
2. **Parallel-run interest calculation:** Run CBACT04C (COBOL) and the Java replacement on the same data for 3 billing cycles. Compare every account's interest charge to the cent.
3. **Automated precision test suite:** For every COMP-3 field, generate test cases with values at the scale boundary (e.g., 9999999999.99, -9999999999.99, 0.01, 0.005).
4. **Code review gate:** No financial calculation code merges without a review by someone who understands both COBOL decimal semantics and Java BigDecimal behavior.

**Early Warning Indicators:**
- Any `double` or `float` usage in financial calculation code
- Java code using `BigDecimal.ROUND_HALF_UP` instead of `ROUND_DOWN` (COBOL truncates, it doesn't round)
- Parallel-run showing >0 cent-level discrepancies in any account

---

### R-03: Shared VSAM Data Coupling Prevents Clean Domain Extraction

**Category:** Data Coupling

**Description:**
Five core VSAM files (ACCTDAT, CARDDAT, CCXREF, TRANSACT, TCATBALF) are accessed by programs across 6+ bounded contexts. The most critical coupling:
- **ACCTDAT** is read/written by: Account View/Update, Card List/View/Update, Transaction Add, Bill Payment, Transaction Posting (batch), Interest Calculation, Statement Generation, Data Export — at least 15 programs.
- **CCXREF** (with CXACAIX alternate index) is the join table between cards, accounts, and customers — accessed by 10+ programs across 4 domains.
- **TRANSACT** is written by online transaction add and batch posting, read by reporting, bill payment, and statement generation.

This means no domain can be extracted to its own database without either (a) maintaining a sync mechanism to keep VSAM and RDBMS consistent, or (b) migrating all coupled domains simultaneously.

**Likelihood:** High — This is an architectural characteristic of the application, not a risk that might not materialize.

**Impact:** Medium — Does not prevent migration but significantly increases complexity, extends timeline, and introduces synchronization failure modes.

**Mitigation Strategy:**
1. **Shared database phase:** During Phases 2–3, all new services share a single PostgreSQL database with tables mapped from VSAM files. This avoids distributed data problems while still enabling API-based access.
2. **Database-per-service later:** Only after all domains are migrated (Phase 4 complete), decompose the shared PostgreSQL into per-service databases with event-driven synchronization.
3. **CXACAIX alternate index replacement:** Design the PostgreSQL `card_xref` table with indexes on both `card_number` (primary) and `account_id` (secondary) to replace the VSAM AIX.
4. **Data ownership map:** The DOMAIN_DECOMPOSITION.md assigns clear ownership of each data entity to one service. Enforce this through database-level permissions even in the shared-database phase.

**Early Warning Indicators:**
- Dual-write sync lag exceeds 5 minutes
- Data consistency checks (checksum comparisons) fail between VSAM and PostgreSQL
- New services bypass the designated owner service and access other service's tables directly

---

### R-04: CICS-Specific Runtime Behavior Not Reproducible

**Category:** Environment Gap (Mainframe-Specific Runtime)

**Description:**
The 16 CICS online programs rely on CICS runtime features that have no direct equivalent in Java/Spring:
- **COMMAREA:** Programs pass state via `DFHCOMMAREA` (defined in COCOM01Y — 200+ bytes). CICS manages the lifecycle; programs receive it via `EIBCALEN` length check.
- **Pseudo-conversational pattern:** Every program does `EXEC CICS RETURN TRANSID(...) COMMAREA(...)` — the program terminates and restarts on the next user keystroke. This is fundamentally different from a web session.
- **BMS map I/O:** Programs send/receive 3270 maps with `EXEC CICS SEND MAP / RECEIVE MAP`. Field-level attributes (protected, bright, dark, modified data tag) control the UI.
- **STARTBR/READNEXT/READPREV:** COCRDLIC and COTRN00C implement browsable lists by browsing VSAM files via CICS file control commands with positioning and paging.
- **Extra-partition TDQ:** CORPT00C submits batch JCL via TDQ and internal reader — a pattern unique to CICS.
- **RESP/RESP2 error handling:** Programs check `WS-RESP-CD` after every CICS call; specific response codes trigger specific recovery actions.

**Likelihood:** Medium — Most CICS behaviors can be emulated, but subtle edge cases (e.g., COMMAREA truncation when called from a program with a smaller COMMAREA, CICS pseudo-conversational timeout handling) may cause behavioral differences.

**Impact:** High — Incorrect session management could lead to data loss (lost updates) or security issues (session leakage).

**Mitigation Strategy:**
1. **Map COMMAREA to session state:** COCOM01Y defines the COMMAREA structure. Translate this to a server-side session object or JWT claims. Explicitly handle all fields: FROM-TRANID, FROM-PROGRAM, TO-TRANID, TO-PROGRAM, USER-ID, USER-TYPE, PGM-CONTEXT, customer/account/card context.
2. **Map pseudo-conversational to request/response:** Each CICS RETURN + TRANSID becomes a REST endpoint. The PGM-CONTEXT flag (ENTER=0, REENTER=1) becomes a request state parameter.
3. **Implement VSAM browse as paginated query:** COCRDLIC's STARTBR/READNEXT with positioning key becomes a SQL query with `WHERE key >= ? ORDER BY key LIMIT ?`.
4. **End-to-end functional tests:** Create test scripts that replicate common user flows (login → menu → list → view → update → verify) and validate identical outcomes in CICS and the new system.

**Early Warning Indicators:**
- New web application loses user context between screens (equivalent of COMMAREA corruption)
- Pagination in list views returns different record sets than CICS browse
- User can see another user's session data (COMMAREA isolation failure)

---

### R-05: Insufficient Test Data Representativeness

**Category:** Testing / Data Representativeness

**Description:**
The repository includes sample data files in `app/data/ASCII/` and `app/data/EBCDIC/` for 9 entity types. These files contain a limited set of test records. Key concerns:
- **acctdata.txt** — Sample accounts may not cover all status codes, edge-case balances (zero, negative, at credit limit), or date boundary conditions.
- **dailytran.txt** — Sample daily transactions may not include all transaction type codes, rejection scenarios, or high-volume edge cases.
- **EBCDIC data files** — Binary format; content not easily inspectable. May contain COMP-3 packed data that looks different when converted to ASCII.

Without production-representative test data, the migration team cannot validate:
- All transaction type/category combinations
- Boundary conditions in interest calculation (zero balance, maximum balance, leap year dates)
- All rejection scenarios in CBTRN02C (invalid card, expired card, over credit limit, unknown account)
- COBOL's behavior with spaces, low-values, and high-values in key fields

**Likelihood:** High — Sample data in repositories is almost never representative of production data diversity.

**Impact:** Medium — Bugs will escape to production; will be caught by users rather than tests.

**Mitigation Strategy:**
1. **Production data snapshot:** Obtain an anonymized/masked copy of production data for all 9 VSAM files. This is the single most important testing asset.
2. **Synthetic data generation:** Build a data generator that creates accounts, cards, customers, and transactions covering all known edge cases: every transaction type code, every status value, boundary balances, leap year dates, special characters in name fields.
3. **EBCDIC-to-ASCII validation:** Run every EBCDIC data file through the COBOL reader programs (CBACT01C, CBACT02C, CBACT03C, CBCUS01C) on the replatformed runtime and capture the output as reference data.
4. **Coverage tracking:** For each COBOL program, track which code paths (paragraphs) are exercised by the test data. Target >95% paragraph coverage.

**Early Warning Indicators:**
- Test data has fewer than 100 records per entity type
- No test cases for rejection scenarios in CBTRN02C
- No test data with COMP-3 packed decimal boundary values (e.g., max positive, max negative)
- Migration team has not requested production data by Phase 1

---

### R-06: Mainframe-Specific Assembler Dependencies (MVSWAIT, COBDATFT)

**Category:** Environment Gap (Mainframe-Specific Runtime)

**Description:**
The application includes two assembler modules:
- **MVSWAIT:** Timer control for batch jobs. Called by COBSWAIT.cbl (`CALL 'MVSWAIT' USING MVSWAIT-TIME`). Uses z/OS STIMER/WAIT macro to pause execution for a specified number of centiseconds.
- **COBDATFT:** Date format conversion utility. Referenced in samples. Converts between date formats using z/OS Language Environment services.

Additionally, CSUTLDTC calls `CEEDAYS` — an IBM Language Environment API that converts dates to Lilian format. This API is z/OS-specific.

GnuCOBOL (installed in this environment) can compile most COBOL programs but cannot link MVSWAIT (z/OS assembler) or CEEDAYS (LE API).

**Likelihood:** Medium — The assembler modules are used by only 2 programs. CEEDAYS is used by CSUTLDTC and called from COACTUPC for date validation.

**Impact:** Medium — MVSWAIT is easily replaced (`Thread.sleep()` in Java, `sleep` in bash). CEEDAYS date validation is more complex — the exact behavior (valid date ranges, calendar systems) must be replicated.

**Mitigation Strategy:**
1. **MVSWAIT:** Replace with `java.util.concurrent.TimeUnit.MILLISECONDS.sleep()` in Java or scheduler-based delays. No business logic is affected.
2. **CEEDAYS/COBDATFT:** Map to `java.time.LocalDate` parsing with the same format strings. Create a comprehensive test suite of valid and invalid dates (including Feb 29 in leap/non-leap years, date range boundaries) to validate behavioral equivalence.
3. **Document all external CALLs:** Grep the codebase for all CALL statements to identify any other external dependencies.

**Early Warning Indicators:**
- Java date validation accepts dates that CEEDAYS would reject (or vice versa)
- Batch jobs hang because timer replacement doesn't work as expected
- Undiscovered CALL statements to mainframe utilities found late in migration

---

### R-07: IMS Hierarchical Data Model Mismatch with Relational Target

**Category:** Data Coupling / Environment Gap

**Description:**
The Authorization module uses IMS hierarchical databases:
- **DBPAUTP0:** Primary authorization database with DL/I segments
- **DBPAUTX0:** Secondary index database
- **PSBs:** PSBPAUTB, PSBPAUTL, PAUTBUNL, DLIGSAMP — define program views of the database

IMS stores data in parent-child segment hierarchies accessed via DL/I calls (GU, GN, GNP, ISRT, DLET, REPL). This is fundamentally different from relational access:
- A single DL/I `GU` call can navigate from root to a specific child segment — equivalent to a multi-table JOIN in SQL.
- Segment search arguments (SSAs) provide filtering that doesn't map 1:1 to SQL WHERE clauses.
- IMS secondary indexes provide alternate access paths not visible in the PSB definitions.

Flattening IMS hierarchies to relational tables risks:
- Losing parent-child integrity constraints
- Creating many-to-many relationships where IMS enforced one-to-many
- Performance degradation from JOINs replacing navigational DL/I access

**Likelihood:** Medium — The Authorization module is clearly scoped (8 programs, isolated data), but the IMS data model may be more complex than the code suggests.

**Impact:** High — Incorrect data model migration could corrupt authorization data or create orphan records.

**Mitigation Strategy:**
1. **DBD analysis:** Parse the DBD (Database Description) files (DBPAUTP0.dbd, DBPAUTX0.dbd, PADFLDBD.DBD, PASFLDBD.DBD) to extract the segment hierarchy, field definitions, and access paths.
2. **Relational model design:** Map each IMS segment to a table, each parent-child relationship to a foreign key. Validate the model with domain experts who understand the authorization business process.
3. **Data migration validation:** Unload IMS data using PAUDBUNL/DBUNLDGS, load into relational tables, then verify record counts and key relationships match.
4. **Defer this module:** Per the CUTOVER_PLAN, the Authorization module is Phase 5 — last to migrate, giving the team maximum learning time.

**Early Warning Indicators:**
- DBD files reveal more segments than expected (complex hierarchy)
- DL/I calls in the COBOL programs use qualified SSAs with multiple levels (deep hierarchy navigation)
- Unload/load record count mismatch

---

### R-08: COBOL Skill Attrition During Multi-Phase Migration

**Category:** Team Skill Availability

**Description:**
The migration plan spans 48–60+ weeks across 5 phases. COBOL expertise is required throughout:
- **Phase 0:** Replatform and validate COBOL runtime behavior
- **Phases 1–3:** Maintain COBOL programs running alongside new services (strangler pattern, dual-write sync, parallel-run)
- **Phase 4:** Parallel-run batch programs (COBOL and Java) for validation
- **Phase 5:** Understand IMS/COBOL interaction for authorization migration

Meanwhile, the market for COBOL developers is shrinking. If COBOL expertise leaves the team mid-migration, the remaining phases cannot validate COBOL behavior, troubleshoot parallel-run discrepancies, or maintain the replatformed runtime.

**Likelihood:** High — COBOL developer attrition during long modernization projects is well-documented across the industry.

**Impact:** Medium — Does not stop migration but significantly slows it and increases defect risk.

**Mitigation Strategy:**
1. **Knowledge capture first:** Before any migration work, conduct structured knowledge transfer sessions with COBOL experts. Document every business rule, validation, and edge case they know that isn't in the code (tribal knowledge).
2. **Golden test suite:** Create comprehensive test suites *while COBOL experts are available* that capture expected behavior. These tests become the oracle when COBOL experts are gone.
3. **Cross-training:** Train at least 2 Java developers to read COBOL and understand VSAM/CICS concepts. They don't need to write COBOL, but they must be able to trace business logic through COBOL code.
4. **Retention incentives:** Offer retention bonuses to COBOL developers tied to Phase 4 completion (batch migration).
5. **Automated COBOL analysis tooling:** Invest in COBOL analysis tools (SonarQube COBOL plugin, Micro Focus Enterprise Analyzer) that can generate call graphs, data flow diagrams, and dead code reports — reducing dependency on human COBOL expertise.

**Early Warning Indicators:**
- COBOL developer(s) give notice during Phases 1–3
- Knowledge transfer sessions not completed by end of Phase 0
- Parallel-run discrepancies take >1 week to diagnose (lack of COBOL debugging skill)
- No Java developer can explain what a given COBOL program does by reading its source

---

### R-09: Batch Job Timing and Sequencing Dependencies

**Category:** Hidden Business Rules / Environment Gap

**Description:**
The JCL jobs reveal a specific execution sequence documented in the README:
1. DUSRSECJ → CLOSEFIL → ACCTFILE → CARDFILE → CUSTFILE → XREFFILE → TRANFILE → DISCGRP → TCATBALF → TRANCATG → TRANTYPE → OPENFIL → DEFGDGB

This initialization sequence must be preserved. Additionally, the operational batch cycle (not fully documented) likely follows a pattern:
1. CLOSEFIL (close CICS files)
2. POSTTRAN (post daily transactions — CBTRN01C or CBTRN02C)
3. INTCALC (run interest calculation — CBACT04C)
4. CREASTMT (generate statements — CBSTM03A)
5. TRANREPT (generate reports — CBTRN03C)
6. TRANBKP (backup transaction file)
7. OPENFIL (reopen CICS files)

COBSWAIT (timer utility) suggests that batch jobs may have timing dependencies — e.g., waiting for CICS files to be fully closed before batch processing begins. The `WAITSTEP.jcl` job explicitly introduces delays.

**Likelihood:** Medium — The documented sequence covers initialization. The operational sequence is inferred but may have additional hidden dependencies.

**Impact:** Medium — Running batch jobs out of sequence could produce incorrect results (e.g., interest calculated on unposted transactions) or cause file contention.

**Mitigation Strategy:**
1. **Document the complete operational batch schedule:** Interview operations team to capture the full nightly/weekly/monthly batch cycle, including timing windows and dependencies.
2. **Implement job orchestration:** Replace JCL job sequencing with a modern orchestrator (Apache Airflow, AWS Step Functions) that enforces dependencies and provides retry/alerting.
3. **Eliminate CLOSEFIL/OPENFIL pattern:** In the new architecture, there is no need to close files for batch access. The relational database supports concurrent access from online services and batch jobs.
4. **Eliminate timer waits:** Replace COBSWAIT/WAITSTEP with event-driven triggers (job A completes → trigger job B) instead of time-based delays.

**Early Warning Indicators:**
- Spring Batch jobs produce different results depending on execution order
- Database deadlocks during batch processing (replacing CLOSEFIL/OPENFIL with concurrent access)
- Operations team describes additional batch dependencies not captured in JCL files

---

### R-10: BMS Screen Map / 3270 UI Logic Interleaved with Business Rules

**Category:** Business Logic Complexity

**Description:**
The 16 CICS programs contain business logic tightly coupled with 3270 screen presentation logic. For example:
- **COACTUPC** interleaves field validation with BMS attribute byte manipulation (setting fields to bright/dark, protected/unprotected based on validation results)
- **COCRDLIC** implements paginated browsing by managing BMS screen positions and VSAM STARTBR positioning simultaneously
- **COTRN02C** validates transaction fields and sets BMS cursor position based on which field has an error

In COBOL, the BMS copybooks (COACTUP.CPY, COCRDLI.CPY, etc.) define both the screen layout AND the working storage for input/output fields. The program logic references screen field names (e.g., `USERIDL OF COSGN0AI` — the length attribute of the USERID field on screen COSGN0A) directly in business logic paragraphs.

This means extracting business logic requires carefully separating:
- **Pure business rules:** "SSN part 1 cannot be 000, 666, or 900-999"
- **Presentation logic:** "Set SSN field attribute to DFHBMBRY (bright) and position cursor to SSN field"
- **Flow control:** "If any error, PERFORM SEND-SCREEN; otherwise PERFORM PROCESS-UPDATE"

**Likelihood:** Medium — This pattern is standard in CICS COBOL applications; experienced modernization teams expect it.

**Impact:** Medium — Increases the effort to extract business rules but does not block migration.

**Mitigation Strategy:**
1. **Layer separation analysis:** For each CICS program, catalog every paragraph and classify it as: business logic, screen I/O, navigation/flow control, or error handling. This becomes the mapping guide for the Java implementation.
2. **BMS-independent validation service:** Extract all validation rules into a standalone service/library that has no UI dependencies. The Java REST controller calls the validation service; the response indicates which fields are invalid and why.
3. **Iterative extraction:** Start with read-only screens (COACTVWC, COTRN00C, COTRN01C, COCRDSLC) where the UI/logic coupling is simpler, before tackling the complex update screens.
4. **Reference the BMS maps:** The `.bms` files in `app/bms/` define the screen layout independently from the COBOL programs. Use these as the specification for the new web UI layout.

**Early Warning Indicators:**
- Java implementation has validation rules scattered across controller, service, and DTO layers (failed separation)
- New web UI field ordering doesn't match user expectations (BMS maps define the user-expected layout)
- Error messages in new UI don't match the COBOL error messages (users rely on familiar wording)

---

## Risk Prioritization Matrix

```
                    IMPACT
                    Low         Medium      High
              ┌───────────┬───────────┬───────────┐
    High      │           │ R-03      │ R-01      │
              │           │ R-05      │ R-02      │
LIKELIHOOD    │           │ R-08      │           │
              ├───────────┼───────────┼───────────┤
    Medium    │           │ R-06      │ R-04      │
              │           │ R-09      │ R-07      │
              │           │ R-10      │           │
              ├───────────┼───────────┼───────────┤
    Low       │           │           │           │
              │           │           │           │
              └───────────┴───────────┴───────────┘
```

---

## Monitoring & Review

- **Weekly:** Review early warning indicators for all in-phase risks
- **Phase Gate:** Before advancing to the next phase, all acceptance criteria must be met and all critical/high risks for the current phase must have mitigations in place
- **Monthly:** Re-assess likelihood and impact ratings based on actual experience
- **Escalation:** Any risk that moves from Medium to High impact or likelihood triggers an immediate mitigation review with project leadership
