# Hotspot Report — CardDemo COBOL Estate

> Top 10 programs ranked by complexity metrics with modernization priority recommendations.

---

## 1. Ranking by Lines of Code (LOC)

| Rank | Program | LOC | Classification | Module |
|------|---------|-----|----------------|--------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) | Core |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS + DB2) | Tran-Type-DB2 |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS + DB2) | Tran-Type-DB2 |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) | Core |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) | Core |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS) | Auth-IMS-DB2-MQ |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS + MQ + IMS) | Auth-IMS-DB2-MQ |
| 8 | COACTVWC.cbl | 941 | Online (CICS) | Core |
| 9 | COCRDSLC.cbl | 887 | Online (CICS) | Core |
| 10 | COTRN02C.cbl | 783 | Online (CICS) | Core |

**Observation:** 100% of the top 10 by LOC are online CICS programs. The largest batch program is CBTRN02C at 731 LOC (ranked #11).

---

## 2. Ranking by Number of Copybooks Referenced

| Rank | Program | Copybook Count | Notable Copybooks |
|------|---------|---------------|-------------------|
| 1 | COACTUPC.cbl | 58 | CSSETATY (×3 with REPLACING), CSUTLDWY, CSLKPCDY, CVACT01Y, CVCUS01Y |
| 2 | COPAUA0C.cbl | 17 | CMQODV, CMQMDV, CMQV, CCPAURQY, CCPAURLY, CIPAUSMY, CIPAUDTY |
| 3 | COCRDUPC.cbl | 16 | CVCRD01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 4 | COCRDSLC.cbl | 16 | CVCRD01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 5 | COACTVWC.cbl | 16 | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 6 | COTRTUPC.cbl | 15 | CSUTLDWY, DCLTRTYP, DCLTRCAT (SQL INCLUDE) |
| 7 | COPAUS0C.cbl | 15 | CIPAUSMY, CIPAUDTY, CVACT01Y, CVACT03Y |
| 8 | COCRDLIC.cbl | 14 | CVCRD01Y, CVACT02Y, CSSTRPFY |
| 9 | COTRTLIC.cbl | 12 | CSDB2RWY, DCLTRTYP (SQL INCLUDE) |
| 10 | COTRN02C.cbl | 11 | CVTRA05Y, CVACT01Y, CVACT03Y |

**Observation:** COACTUPC has 3.4× more copybooks than the second-place program due to repeated CSSETATY COPY with REPLACING directives (used for field attribute mapping across 3 screen sections).

---

## 3. Ranking by Number of I/O Operations

Counts include READ, WRITE, REWRITE, DELETE, EXEC CICS READ/WRITE/STARTBR/READNEXT/READPREV, and EXEC SQL statements.

| Rank | Program | I/O Count | Types of I/O |
|------|---------|-----------|--------------|
| 1 | COTRTUPC.cbl | 53 | EXEC SQL SELECT/UPDATE/DELETE, CICS MAP send/receive |
| 2 | COTRTLIC.cbl | 38 | EXEC SQL SELECT (cursor), CICS STARTBR/READNEXT/READPREV/ENDBR |
| 3 | COACTUPC.cbl | 28 | CICS READ/REWRITE on ACCTFILE/CARDFILE/CUSTFILE, MAP I/O |
| 4 | COCRDUPC.cbl | 20 | CICS READ/REWRITE on CARDFILE, MAP I/O |
| 5 | CBPAUP0C.cbl | 17 | IMS DL/I GN/GNP/DLET across summary + detail segments |
| 6 | CBTRN02C.cbl | 13 | Batch READ/WRITE/REWRITE on DALYTRAN/XREFFILE/ACCTFILE/TCATBALF/TRANFILE |
| 7 | COBTUPDT.cbl | 13 | EXEC SQL INSERT/UPDATE/DELETE + file READ |
| 8 | COCRDLIC.cbl | 12 | CICS STARTBR/READNEXT/READPREV/ENDBR on CARDFILE |
| 9 | PAUDBLOD.CBL | 12 | IMS DL/I ISRT/GU + file READ |
| 10 | CBEXPORT.cbl | 11 | Batch OPEN/READ/WRITE across 6 files |

---

## 4. Ranking by Business Logic Density (EVALUATE/IF Count)

Higher counts indicate deeper conditional logic and more complex business rules.

| Rank | Program | EVALUATE + IF Count | Key Business Logic |
|------|---------|--------------------|--------------------|
| 1 | COACTUPC.cbl | 194 | Account validation rules: date checks, amount limits, alpha/numeric validation, state/zip/phone area code validation, cross-entity consistency |
| 2 | COCRDUPC.cbl | 167 | Card field validation: expiry date, CVV, name, optimistic lock checking |
| 3 | COCRDLIC.cbl | 149 | Pagination logic: forward/backward browse, boundary detection, filter matching |
| 4 | CBTRN02C.cbl | 93 | Transaction posting rules: card validation, account status check, balance update, reject logic |
| 5 | CBACT04C.cbl | 86 | Interest calculation: rate lookup by group/type/category, balance computation, accrual |
| 6 | COCRDSLC.cbl | 80 | Card detail display: field formatting, status interpretation, error handling |
| 7 | CBTRN03C.cbl | 79 | Report generation: type/category lookups, page break logic, total accumulation |
| 8 | COACTVWC.cbl | 70 | Account view: multi-entity join display, field formatting |
| 9 | COTRN00C.cbl | 42 | Transaction list: pagination, filter, date range validation |
| 10 | COUSR00C.cbl | 41 | User list: pagination, user type filtering |

---

## 5. Ranking by Inter-Program Dependencies

Counts program-to-program calls (XCTL, LINK, CALL), external API calls (MQ, IMS, DB2), and number of VSAM files accessed.

| Rank | Program | Dependency Score | Breakdown |
|------|---------|-----------------|-----------|
| 1 | COPAUA0C.cbl | 11 | 4 MQ calls + 3 VSAM files + 2 IMS segments + 2 CICS resources |
| 2 | COACTUPC.cbl | 8 | 1 XCTL + 3 VSAM files + 1 CALL + 3 BMS maps |
| 3 | COACCT01.cbl | 8 | 9 MQ calls + 1 VSAM file |
| 4 | CODATE01.cbl | 7 | 9 MQ calls (3 queue types) |
| 5 | CBTRN02C.cbl | 7 | 1 CALL + 5 files + 1 VSAM I-O |
| 6 | CBEXPORT.cbl | 7 | 1 CALL + 6 files |
| 7 | CBTRN01C.cbl | 7 | 1 CALL + 6 input files |
| 8 | CBACT04C.cbl | 6 | 1 CALL + 4 files + 1 VSAM I-O |
| 9 | COTRTUPC.cbl | 6 | 1 XCTL + 2 DB2 tables + 1 DSNTIAC call + 2 BMS maps |
| 10 | COCRDLIC.cbl | 5 | 3 XCTL + 1 VSAM file + 1 BMS map |

---

## 6. Composite Hotspot Score

Normalized composite ranking combining all five metrics (lower total = higher priority):

| Overall Rank | Program | LOC Rank | CPY Rank | I/O Rank | Logic Rank | Dep Rank | **Composite** |
|-------------|---------|----------|----------|----------|-----------|----------|---------------|
| **1** | **COACTUPC.cbl** | 1 | 1 | 3 | 1 | 2 | **8** |
| **2** | **COTRTUPC.cbl** | 3 | 6 | 1 | — | 9 | **19** |
| **3** | **COPAUA0C.cbl** | 7 | 2 | — | — | 1 | **10** |
| **4** | **COCRDUPC.cbl** | 4 | 3 | 4 | 2 | — | **13** |
| **5** | **COTRTLIC.cbl** | 2 | 9 | 2 | — | — | **13** |
| **6** | **COCRDLIC.cbl** | 5 | 8 | 8 | 3 | 10 | **34** |
| **7** | **CBTRN02C.cbl** | 11 | — | 6 | 4 | 5 | **26** |
| **8** | **COPAUS0C.cbl** | 6 | 7 | — | — | — | **13** |
| **9** | **COACTVWC.cbl** | 8 | 5 | — | 8 | — | **21** |
| **10** | **CBACT04C.cbl** | 14 | — | — | 5 | 8 | **27** |

---

## 7. Modernization Priority Recommendations

### Tier 1 — Modernize First (Highest Impact)

#### 1. COACTUPC.cbl — Account Update
- **Why first:** Largest program (4,236 LOC), most copybooks (58), most complex business logic (194 conditionals), touches 3 VSAM files. This is the single most complex program in the estate.
- **Risk:** Very high — deeply entangled with account, card, customer, and cross-reference entities.
- **Approach:** Decompose into microservices: Account Validation Service, Account Update Service, UI layer. Extract date validation (CSUTLDWY) and lookup codes (CSLKPCDY) as shared utilities.

#### 2. CBTRN02C.cbl — Daily Transaction Posting
- **Why early:** Core batch pipeline program with 93 conditionals and 13 I/O operations across 5 files. This is the single most critical batch program — every daily transaction flows through it.
- **Risk:** High — failure stops the entire daily batch cycle.
- **Approach:** Convert to event-driven transaction processing. The reject/accept logic maps well to a rules engine pattern.

#### 3. COPAUA0C.cbl — Card Authorization Decision
- **Why early:** Highest dependency score (11 external touchpoints: MQ, VSAM, IMS). Real-time authorization is the most latency-sensitive path.
- **Risk:** High — multi-protocol (CICS + MQ + IMS + VSAM).
- **Approach:** Rewrite as a stateless API service. Replace MQ request/reply with REST or gRPC. Replace IMS with relational database. Keep VSAM reads as database queries.

### Tier 2 — Modernize Second (High Complexity, Lower Blast Radius)

#### 4. COTRTUPC.cbl + COTRTLIC.cbl — Transaction Type Maintenance (DB2)
- **Why together:** These two programs form a complete CRUD pair for transaction types and are already DB2-based, making them the easiest path to a modern database layer.
- **Risk:** Medium — already uses SQL, so database migration is simpler.
- **Approach:** Direct port to JPA/Spring Data entities. The cursor-based pagination in COTRTLIC maps to Spring Data Pageable.

#### 5. COCRDUPC.cbl — Credit Card Update
- **Why here:** Second-highest business logic density (167 conditionals), 16 copybooks. Similar pattern to COACTUPC but smaller scope.
- **Risk:** Medium — single-entity focus (card file only).
- **Approach:** Port alongside COACTUPC as part of the card management microservice.

#### 6. CBACT04C.cbl — Interest Calculator
- **Why here:** Critical financial logic (86 conditionals) with complex rate lookups across discount groups.
- **Risk:** Medium — pure batch, well-isolated.
- **Approach:** Extract as a scheduled job/service. Rate lookup tables (DISCGRP, TCATBALF) become database tables.

### Tier 3 — Modernize Later (Moderate Complexity)

#### 7–8. COCRDLIC.cbl / COCRDSLC.cbl — Card List / Card Detail View
- Read-only programs with pagination logic. Modernize as part of card management UI.

#### 9. CBTRN03C.cbl — Transaction Report
- Report generation program. Replace with modern reporting framework (JasperReports, etc.).

#### 10. CBEXPORT.cbl / CBIMPORT.cbl — Export/Import
- Data migration utilities. Replace with ETL tools or database export/import.

### Cross-Cutting Recommendations

| Area | Recommendation |
|------|---------------|
| **Shared Utilities** | Extract CSUTLDTC (date validation), CSLKPCDY (lookup codes), CSUTLDWY (date editing) as shared Java libraries first — they are used across many programs |
| **Copybook→Java Classes** | Convert CVACT01Y, CVCUS01Y, CVACT02Y, CVTRA05Y to Java POJOs/records first — they define the core domain model |
| **COMMAREA→API** | Replace COCOM01Y (communication area) with REST API request/response DTOs |
| **BMS Maps→UI** | Replace all 18 BMS maps with modern web UI (React/Angular) — BMS maps are the display layer |
| **VSAM→Database** | Migrate all VSAM KSDS files to relational tables. The key structures in copybooks map directly to primary keys |
| **JCL→Scheduler** | Replace JCL batch pipeline with modern scheduler (Spring Batch, AWS Step Functions) |
| **IMS→RDBMS** | Replace IMS hierarchical database with relational tables — the two-level hierarchy (summary/detail) maps to a parent-child table relationship |
| **MQ→Modern Messaging** | Retain message queue pattern but migrate from IBM MQ to modern alternatives (Kafka, SQS, RabbitMQ) |
