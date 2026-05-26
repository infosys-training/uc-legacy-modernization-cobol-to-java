# Hotspot Report — CardDemo COBOL Estate

> Programs ranked by complexity metrics to prioritize modernization.

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification | Sub-App |
|------|---------|-----|----------------|---------|
| 1 | **COACTUPC.cbl** | 4,236 | Online (CICS) | Core |
| 2 | **COTRTLIC.cbl** | 2,098 | Online (CICS+DB2) | tran-type-db2 |
| 3 | **COTRTUPC.cbl** | 1,702 | Online (CICS+DB2) | tran-type-db2 |
| 4 | **COCRDUPC.cbl** | 1,560 | Online (CICS) | Core |
| 5 | **COCRDLIC.cbl** | 1,459 | Online (CICS) | Core |
| 6 | **COPAUS0C.cbl** | 1,032 | Online (CICS) | auth-ims-db2-mq |
| 7 | **COPAUA0C.cbl** | 1,026 | Online (CICS+MQ) | auth-ims-db2-mq |
| 8 | **COACTVWC.cbl** | 941 | Online (CICS) | Core |
| 9 | **CBSTM03A.CBL** | 924 | Batch | Core |
| 10 | **COCRDSLC.cbl** | 887 | Online (CICS) | Core |

---

## 2. Top 10 Programs by Copybooks Referenced

| Rank | Program | Copybooks | Key Copybooks |
|------|---------|-----------|---------------|
| 1 | **COACTUPC.cbl** | 58 | CSUTLDWY, CVCRD01Y, CSLKPCDY, CSSETATY, COCOM01Y, CVACT01Y, CVCUS01Y + many BMS copies |
| 2 | **COPAUA0C.cbl** | 17 | CMQODV, CMQMDV, CMQV, CMQTML, CCPAURQY, CCPAURLY, CIPAUSMY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 3 | **COCRDUPC.cbl** | 16 | CVCRD01Y, COCOM01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 4 | **COACTVWC.cbl** | 16 | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY |
| 5 | **COCRDSLC.cbl** | 16 | CVCRD01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 6 | **COPAUS0C.cbl** | 15 | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY |
| 7 | **COTRTUPC.cbl** | 15 | CSUTLDWY, CVCRD01Y, CSSETATY, CSSTRPFY, DCLTRTYP, DCLTRCAT |
| 8 | **COCRDLIC.cbl** | 14 | CVCRD01Y, CVACT02Y, CSSTRPFY |
| 9 | **COTRTLIC.cbl** | 12 | CVCRD01Y, CVACT02Y, CSSTRPFY, CSDB2RWY, CSDB2RPY |
| 10 | **COTRN02C.cbl** | 11 | COCOM01Y, CVTRA05Y, CVACT01Y, CVACT03Y |

> Note: COACTUPC's 58 copybook references include multiple COPY CSSETATY REPLACING directives (one per validated screen field), which inflates the count. Unique copybooks referenced: ~16.

---

## 3. Top 10 Programs by I/O Operations

Counts include: READ, WRITE, OPEN, CLOSE, EXEC CICS READ/WRITE/REWRITE/DELETE/STARTBR, EXEC SQL.

| Rank | Program | I/O Ops | I/O Profile |
|------|---------|---------|-------------|
| 1 | **CBSTM03A.CBL** | 117 | OPEN/CLOSE + many WRITE (statement lines to STMTFILE + HTMLFILE) |
| 2 | **CBEXPORT.cbl** | 29 | READ (5 VSAM files) + WRITE (export file) |
| 3 | **CBIMPORT.cbl** | 29 | READ (export file) + WRITE (6 output files) |
| 4 | **COTRTLIC.cbl** | 29 | EXEC SQL cursor OPEN/FETCH/CLOSE + CICS SEND/RECEIVE |
| 5 | **CBTRN02C.cbl** | 23 | READ/WRITE VSAM files + I-O updates |
| 6 | **COACTUPC.cbl** | 23 | CICS READ/REWRITE on 3 VSAM files |
| 7 | **CBTRN03C.cbl** | 22 | READ (5 input files) + WRITE (report) |
| 8 | **COACCT01.cbl** | 20 | MQ OPEN/GET/PUT/CLOSE (3 queues) + CICS READ |
| 9 | **CBACT04C.cbl** | 19 | READ/WRITE/I-O on multiple VSAM files |
| 10 | **CODATE01.cbl** | 18 | MQ OPEN/GET/PUT/CLOSE (3 queues) |

---

## 4. Top 10 Programs by Business Logic Density

Metric: Count of `EVALUATE` and `IF` statements (proxy for branching complexity and nesting depth).

| Rank | Program | IF/EVALUATE Count | Dominant Logic |
|------|---------|-------------------|----------------|
| 1 | **COACTUPC.cbl** | 194 | Field-level validation (dates, SSN, amounts, state codes, phone area codes), status checks, error handling |
| 2 | **COCRDUPC.cbl** | 167 | Card field validation (name, status, expiry month/year), error handling |
| 3 | **COCRDLIC.cbl** | 149 | Pagination logic, PF key handling, browse direction control |
| 4 | **COTRTUPC.cbl** | 134 | Transaction type field validation, DB2 SQLCODE checking, CRUD flow control |
| 5 | **COTRTLIC.cbl** | 131 | DB2 cursor management, pagination, PF key handling |
| 6 | **CBTRN02C.cbl** | 93 | Transaction validation rules, reject logic, balance update conditions |
| 7 | **CBACT04C.cbl** | 86 | Interest calculation rules, fee determination, account status checking |
| 8 | **COCRDSLC.cbl** | 80 | Card detail display logic, cross-reference resolution |
| 9 | **CBTRN03C.cbl** | 79 | Report formatting, page breaks, subtotals, date range filtering |
| 10 | **COACTVWC.cbl** | 70 | Account display logic, multi-file join, status interpretation |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Counts include: CALL statements, EXEC CICS XCTL, EXEC CICS LINK, and dependencies from other programs via CALL.

| Rank | Program | Dependencies | Details |
|------|---------|-------------|---------|
| 1 | **CBSTM03A.CBL** | 15 calls | CALL CBSTM03B (×13), CALL CEE3ABD; reads 4 VSAM files via subroutine |
| 2 | **COACCT01.cbl** | 9 calls | MQ: MQOPEN(×3), MQGET, MQPUT(×2), MQCLOSE(×3) |
| 3 | **CODATE01.cbl** | 9 calls | MQ: MQOPEN(×3), MQGET, MQPUT(×2), MQCLOSE(×3) |
| 4 | **PAUDBLOD.CBL** | 9 calls | IMS: CBLTDLI(×4) for ISRT/GU operations |
| 5 | **COPAUA0C.cbl** | 8 calls | MQ: MQOPEN, MQGET, MQPUT1, MQCLOSE + CICS operations |
| 6 | **DBUNLDGS.CBL** | 7 calls | IMS: CBLTDLI(×4) for GN/GNP/ISRT operations |
| 7 | **PAUDBUNL.CBL** | 5 calls | IMS: CBLTDLI(×2) for GN/GNP operations |
| 8 | **COCRDLIC.cbl** | 3 calls | XCTL targets from/to card detail/update screens |
| 9 | **CBACT01C.cbl** | 3 calls | Utility CALL for record processing |
| 10 | **CORPT00C.cbl** | 2 calls | CALL CSUTLDTC (date conversion) × 2 |

> Note: CICS XCTL transfers are not counted as CALL but form critical navigation chains (see DEPENDENCY_MAP.md).

---

## 6. Composite Hotspot Score

Weighted composite score: **LOC (25%) + Copybooks (15%) + I/O (20%) + Logic Density (25%) + Dependencies (15%)**

Each dimension is normalized to 0–100 scale based on the maximum value in that dimension.

| Rank | Program | LOC Score | CPY Score | I/O Score | Logic Score | Dep Score | **Composite** |
|------|---------|-----------|-----------|-----------|-------------|-----------|---------------|
| 1 | **COACTUPC.cbl** | 100.0 | 100.0 | 19.7 | 100.0 | 0.0 | **75.9** |
| 2 | **COTRTLIC.cbl** | 49.5 | 20.7 | 24.8 | 67.5 | 6.7 | **40.1** |
| 3 | **COCRDUPC.cbl** | 36.8 | 27.6 | 4.3 | 86.1 | 0.0 | **38.0** |
| 4 | **COTRTUPC.cbl** | 40.2 | 25.9 | 7.7 | 69.1 | 0.0 | **34.7** |
| 5 | **COCRDLIC.cbl** | 34.4 | 24.1 | 8.5 | 76.8 | 20.0 | **35.5** |
| 6 | **CBSTM03A.CBL** | 21.8 | 8.6 | 100.0 | 12.4 | 100.0 | **46.2** |
| 7 | **CBTRN02C.cbl** | 17.3 | 10.3 | 19.7 | 47.9 | 6.7 | **22.3** |
| 8 | **COPAUA0C.cbl** | 24.2 | 29.3 | 12.8 | 32.0 | 53.3 | **28.6** |
| 9 | **CBACT04C.cbl** | 15.4 | 10.3 | 16.2 | 44.3 | 6.7 | **20.6** |
| 10 | **COACTVWC.cbl** | 22.2 | 27.6 | 3.4 | 36.1 | 0.0 | **19.8** |

---

## 7. Modernization Prioritization Recommendations

### Tier 1 — Modernize First (Highest Impact + Highest Complexity)

#### 1. **COACTUPC.cbl** (Account Update) — Composite Score: 75.9
- **Why first:** Largest program (4,236 LOC), highest business logic density (194 IF/EVALUATE), most copybooks (58 references including 16 unique). This is the most complex single program in the estate.
- **Risk:** Contains critical validation logic for accounts (date validation, SSN, state codes, phone area codes). Validation rules in CSLKPCDY, CSUTLDPY, CSSETATY must be preserved exactly.
- **Strategy:** Decompose into microservices: (1) account data retrieval service, (2) field validation service, (3) account update service. Extract CSLKPCDY lookup tables to a reference data service or database.

#### 2. **CBTRN02C.cbl** (Transaction Posting) — Composite Score: 22.3
- **Why early:** Core batch pipeline program — processes daily transactions, updates account balances, category balances, and writes rejects. High business criticality with 93 logic branches and 23 I/O operations.
- **Risk:** Updates multiple VSAM files in a single pass (ACCTFILE, TCATBALF, TRANFILE). Must maintain transactional integrity.
- **Strategy:** Convert to a transactional batch service with database transactions replacing VSAM I-O mode.

#### 3. **CBACT04C.cbl** (Interest Calculator) — Composite Score: 20.6
- **Why early:** Contains the interest and fee calculation engine — the core revenue-generating business logic. 86 logic branches for rate determination.
- **Risk:** Financial calculations must be exact. Disclosure group lookups (DISCGRP) drive rate selection. Rounding rules embedded in COBOL decimal arithmetic.
- **Strategy:** Extract as a standalone calculation engine. Map COMP-3 packed decimal logic to BigDecimal in Java. Preserve disclosure group rate tables.

### Tier 2 — Modernize Second (High Complexity, Lower Risk)

#### 4. **COTRTLIC.cbl** + **COTRTUPC.cbl** (Transaction Type DB2 Screens)
- 2,098 + 1,702 LOC with heavy DB2 integration (SQL cursors, INSERT/UPDATE/DELETE). Already use SQL, making database migration more straightforward.
- **Strategy:** Convert to REST API + web frontend. DB2 SQL can be largely reused in a modern RDBMS.

#### 5. **COCRDLIC.cbl** + **COCRDUPC.cbl** + **COCRDSLC.cbl** (Card Management Screens)
- Combined 3,906 LOC for card list/detail/update. High logic density for field validation (167 IFs in COCRDUPC).
- **Strategy:** Convert to a card management microservice with a modern web UI. Pagination logic (STARTBR/READNEXT) maps to database cursor/offset patterns.

#### 6. **COPAUA0C.cbl** (Authorization Processor)
- 1,026 LOC spanning CICS, MQ, and VSAM. High dependency count (8 external calls to MQ APIs).
- **Strategy:** Convert to an event-driven microservice consuming from a message broker (e.g., Kafka, SQS). Replace IMS auth DB with a relational or NoSQL authorization store.

### Tier 3 — Modernize Third (Lower Complexity or Lower Risk)

#### 7. **CBSTM03A.CBL** + **CBSTM03B.CBL** (Statement Generation)
- 1,154 combined LOC. Highest I/O count (117 operations) but simpler logic (24 IFs). Generates text and HTML output.
- **Strategy:** Replace with a modern reporting/templating engine (e.g., JasperReports, PDF generation). The HTML output is already structured for web delivery.

#### 8. **CBTRN03C.cbl** (Transaction Reporting)
- 649 LOC, 22 I/O operations, 79 logic branches. Report formatting with page breaks and subtotals.
- **Strategy:** Replace with a reporting framework. Date filtering and sort logic map directly to SQL queries.

#### 9. **CBEXPORT.cbl** + **CBIMPORT.cbl** (Data Migration)
- 1,069 combined LOC. Multi-record format export/import — already designed for data portability.
- **Strategy:** Replace with ETL tools or API-based data exchange.

#### 10. **COSGN00C.cbl** + **COUSR00-03C.cbl** (User Security)
- Simple CRUD on USRSEC VSAM. Plaintext passwords indicate basic security model.
- **Strategy:** Replace with modern IAM (e.g., LDAP, OAuth, SAML). Lowest risk since no business logic.

### Tier 4 — Decommission / Low Priority

- **COBSWAIT.cbl** (41 LOC) — Simple wait utility. No business value.
- **CSUTLDTC.cbl** (157 LOC) — Date utility. Replace with `java.time` API.
- **UNUSED1Y.cpy** — Confirmed dead copybook. Safe to remove.
- **PAUDBLOD/PAUDBUNL/DBUNLDGS** — IMS load/unload utilities. Only needed during IMS decommission.

---

## 8. Key Modernization Risks

| Risk | Affected Programs | Mitigation |
|------|-------------------|------------|
| **VSAM I-O mode semantics** | CBTRN02C, CBACT04C | VSAM I-O (read-update-rewrite in single pass) has no direct SQL equivalent. Must implement pessimistic locking or serialized batch processing. |
| **Packed decimal precision** | COPAUA0C, CIPAUSMY, CIPAUDTY | COMP-3 fields store exact decimal values. Java `BigDecimal` required — never use `double`/`float`. |
| **Copybook REPLACING** | COACTUPC (CSSETATY), COTRTUPC | COPY...REPLACING is a compile-time macro. Must expand before conversion. |
| **CICS conversation state** | All 17+ CICS programs | COMMAREA (COCOM01Y) maintains state across pseudo-conversational exchanges. Map to HTTP session or JWT token state. |
| **BMS maps** | All CICS programs | Screen-level field attributes (color, protection, MDT) have no direct web equivalent. Use form validation + CSS. |
| **IMS hierarchical DB** | CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS | IMS segment navigation (GN/GNP/GU) must be redesigned as relational queries. Parent-child relationships become JOINs or normalized tables. |
| **GDG versioning** | COMBTRAN, TRANBKP, DEFGDGB | Generation Data Groups provide automatic versioned datasets. Replace with timestamped file names or database-managed versioning. |
| **SORT utility** | COMBTRAN, PRTCATBL | JCL SORT steps must be reimplemented as SQL ORDER BY or in-application sorting. |

---

## 9. Summary

| Metric | Top Program | Value |
|--------|-------------|-------|
| **Lines of Code** | COACTUPC.cbl | 4,236 |
| **Copybooks Referenced** | COACTUPC.cbl | 58 (16 unique) |
| **I/O Operations** | CBSTM03A.CBL | 117 |
| **Business Logic Density** | COACTUPC.cbl | 194 IF/EVALUATE |
| **Inter-Program Dependencies** | CBSTM03A.CBL | 15 CALLs |
| **Composite Score** | COACTUPC.cbl | 75.9 |

**Recommended modernization order:**
1. COACTUPC → CBTRN02C → CBACT04C (core business logic first)
2. COTRTLIC/COTRTUPC → COCRDLIC/COCRDUPC/COCRDSLC → COPAUA0C (UI and integration)
3. CBSTM03A/B → CBTRN03C → CBEXPORT/CBIMPORT (reporting and data movement)
4. COSGN00C/COUSR* (security — replace wholesale with IAM)
