# HOTSPOT REPORT — CardDemo COBOL Estate

> Programs ranked by complexity metrics to identify modernization priorities.

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification | Module |
|------|---------|-----|----------------|--------|
| 1 | COACTUPC | 4,236 | Online (CICS) | Core |
| 2 | COTRTLIC | 2,098 | Online (CICS/DB2) | Transaction Type |
| 3 | COTRTUPC | 1,702 | Online (CICS/DB2) | Transaction Type |
| 4 | COCRDUPC | 1,560 | Online (CICS) | Core |
| 5 | COCRDLIC | 1,459 | Online (CICS) | Core |
| 6 | COPAUS0C | 1,032 | Online (CICS/IMS) | Authorization |
| 7 | COPAUA0C | 1,026 | Online (CICS/IMS/MQ) | Authorization |
| 8 | CBSTM03A | 924 | Batch | Core |
| 9 | COACTVWC | 941 | Online (CICS) | Core |
| 10 | COCRDSLC | 887 | Online (CICS) | Core |

---

## 2. Top 10 Programs by Copybook References

| Rank | Program | Copybooks | Notable Includes |
|------|---------|-----------|------------------|
| 1 | COACTUPC | 18 | COACTUP, COCOM01Y, CVACT01Y, CVCUS01Y, CSLKPCDY, CSSETATY, CSSTRPFY, CSUTLDPY, CSUTLDWY + BMS maps |
| 2 | COPAUA0C | 17 | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, CVACT01Y, CVACT03Y, CVCUS01Y + MQ includes |
| 3 | COACTVWC | 16 | COACTVW, COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVCRD01Y + BMS maps |
| 4 | COCRDSLC | 16 | COCRDSL, COCOM01Y, CVACT02Y, CVCRD01Y, CVCUS01Y + BMS maps |
| 5 | COCRDUPC | 16 | COCRDUP, COCOM01Y, CVACT02Y, CVCRD01Y, CVCUS01Y + BMS maps |
| 6 | COPAUS0C | 15 | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU00, CVACT01Y-03Y, CVCUS01Y + BMS maps |
| 7 | COTRTUPC | 15 | COTRTUP, COCOM01Y, CSDB2RWY, CSSETATY, CSUTLDWY + BMS maps |
| 8 | COCRDLIC | 14 | COCRDLI, COCOM01Y, CVACT02Y, CVCRD01Y + BMS maps |
| 9 | COTRTLIC | 12 | COTRTLI, COCOM01Y, CSDB2RPY, CVCRD01Y + BMS maps |
| 10 | COBIL00C | 11 | COBIL00, COCOM01Y, CVACT01Y, CVACT03Y, CVTRA05Y + BMS maps |

---

## 3. Top 10 Programs by I/O Operations

Counts include CICS READ/WRITE/REWRITE/DELETE/START/STARTBR/READNEXT/READPREV/ENDBR, file OPEN/CLOSE/READ/WRITE, IMS DL/I calls, MQ GET/PUT, and DB2 SQL operations.

| Rank | Program | I/O Ops | Primary I/O Type |
|------|---------|---------|------------------|
| 1 | CBSTM03A | 118 | File READ/WRITE (statement generation with many record reads) |
| 2 | COTRTUPC | 47 | DB2 SELECT/INSERT/UPDATE + CICS BMS SEND/RECEIVE |
| 3 | COTRTLIC | 33 | DB2 CURSOR FETCH/DELETE + CICS BMS SEND/RECEIVE |
| 4 | CBEXPORT | 29 | VSAM READ (5 files) + sequential WRITE |
| 5 | CBIMPORT | 29 | Sequential READ + WRITE to 6 output files |
| 6 | CBTRN02C | 24 | VSAM READ/REWRITE (account update) + sequential READ/WRITE |
| 7 | CBTRN03C | 23 | VSAM READ (4 reference files) + sequential WRITE |
| 8 | CBPAUP0C | 23 | IMS DL/I GN/GNP/DLET (scan and purge expired records) |
| 9 | COTRN00C | 22 | CICS STARTBR/READNEXT/READPREV/ENDBR (paginated browse) |
| 10 | COUSR00C | 22 | CICS STARTBR/READNEXT/READPREV/ENDBR (paginated browse) |

---

## 4. Top 10 Programs by Business Logic Density

Measured by count of IF/EVALUATE statements and maximum nesting depth.

| Rank | Program | IF/EVALUATE Count | Max Nesting Depth | Complexity Assessment |
|------|---------|------------------|-------------------|----------------------|
| 1 | COACTUPC | 174 | 4 | Extremely high — full field-level validation of 12+ account fields, credit limit checks, date validation, SSN formatting |
| 2 | COTRTUPC | 134 | 3 | Very high — DB2 transaction type field validation, category management, multi-mode (add/update/view) |
| 3 | COTRTLIC | 115 | 5 | Very high — paginated DB2 cursor browse with selection logic, delete confirmation, filter handling |
| 4 | COCRDUPC | 80 | 3 | High — card update validation (status, name, expiry date), card number formatting |
| 5 | COCRDLIC | 68 | 4 | High — paginated card browse with PFK handling, filter logic, selection dispatch |
| 6 | COPAUA0C | 62 | 3 | High — authorization decision engine: credit limit check, card validation, fraud screening |
| 7 | CBTRN02C | 48 | 3 | Moderate-high — transaction validation: card lookup, account status check, balance update logic |
| 8 | CBACT04C | 43 | 4 | Moderate-high — interest calculation: rate lookup by category, compounding logic, fee assessment |
| 9 | CBTRN03C | 40 | 4 | Moderate — report formatting with page breaks, account breaks, totals computation |
| 10 | COCRDSLC | 37 | 2 | Moderate — card detail display with customer lookup and formatting |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Counts CALL, XCTL, LINK invocations + number of programs that call/reference this program.

| Rank | Program | Outgoing | Incoming | Total | Notes |
|------|---------|----------|----------|-------|-------|
| 1 | COMEN01C | 3 XCTL | Called by COSGN00C | 4 | Menu dispatcher — central routing hub for all 11 user functions |
| 2 | COADM01C | 2 XCTL | Called by COSGN00C | 3 | Admin menu dispatcher — routes to 6 admin functions |
| 3 | COSGN00C | 2 XCTL | Entry point (CICS first program) | 3 | Authentication gateway — everyone passes through here |
| 4 | COCRDLIC | 3 XCTL + CALL | Called by COMEN01C | 4 | Card list hub — dispatches to view/update |
| 5 | COPAUS0C | 2 (context) | Called by COMEN01C | 3 | Authorization summary — launches detail views |
| 6 | CBSTM03A | 15 CALL | Called by CREASTMT JCL | 16 | Statement generation — calls subroutine CBSTM03B extensively |
| 7 | CBSTM03B | 0 | Called by CBSTM03A | 1 | Statement subroutine — pure data access module |
| 8 | CSUTLDTC | 0 | Called by CBACT04C, COACTUPC (via CSUTLDPY) | 2 | Shared date validation utility |
| 9 | COPAUA0C | 8 CALL (MQ) | Triggered by MQ | 9 | Authorization engine — calls MQ APIs + reads 3 VSAM files |
| 10 | COACCT01 | 9 CALL (MQ) | Triggered by MQ | 10 | Account inquiry MQ service — calls MQ APIs + reads VSAM |

---

## 6. Composite Complexity Score

Weighted composite score: **LOC (25%) + Copybooks (15%) + I/O Ops (20%) + Logic Density (25%) + Dependencies (15%)**

Each metric is normalized to 0–100 scale relative to the maximum observed value.

| Rank | Program | LOC Score | Copybook Score | I/O Score | Logic Score | Dependency Score | **Composite** |
|------|---------|-----------|---------------|-----------|-------------|-----------------|---------------|
| 1 | **COACTUPC** | 100 | 100 | 15 | 100 | 15 | **72.3** |
| 2 | **COTRTLIC** | 50 | 67 | 28 | 66 | 8 | **46.1** |
| 3 | **COTRTUPC** | 40 | 83 | 40 | 77 | 8 | **49.3** |
| 4 | **COCRDUPC** | 37 | 89 | 4 | 46 | 15 | **35.5** |
| 5 | **COPAUA0C** | 24 | 94 | 13 | 36 | 56 | **37.2** |
| 6 | **COCRDLIC** | 34 | 78 | 14 | 39 | 25 | **35.2** |
| 7 | **CBSTM03A** | 22 | 28 | 100 | 11 | 100 | **47.7** |
| 8 | **COPAUS0C** | 24 | 83 | 3 | 21 | 19 | **25.9** |
| 9 | **CBTRN02C** | 17 | 33 | 20 | 28 | 6 | **20.7** |
| 10 | **CBACT04C** | 15 | 33 | 17 | 25 | 13 | **19.7** |

---

## 7. Modernization Recommendations

### Priority 1 — Start Here (High Impact, High Complexity)

#### 1. COACTUPC — Account Update (Score: 72.3)

- **Why first:** Largest program (4,236 LOC) with the most copybook references (18) and highest business logic density (174 IF/EVALUATE statements). This is the single most complex program in the estate.
- **Risk:** Deeply coupled to VSAM I/O (3 files), BMS maps, and extensive field-level validation logic. Contains the most domain knowledge about account business rules.
- **Strategy:** Decompose into microservices: validation service, account persistence service, and UI layer. Extract the 12+ field validation rules into a reusable validation library.

#### 2. COTRTUPC — Transaction Type Update (Score: 49.3)

- **Why:** Second-highest logic density (134 IF/EVALUATE) with DB2 SQL integration. Already uses relational data, making it a natural candidate for Java/Spring migration.
- **Risk:** Tight coupling between CICS BMS screen handling and DB2 operations.
- **Strategy:** Migrate DB2 operations directly to JPA/Hibernate. The relational model is already in place — this is the lowest-risk DB2 migration.

#### 3. CBSTM03A — Statement Generation (Score: 47.7)

- **Why:** Highest I/O count (118 operations), calls CBSTM03B subroutine, reads 4 VSAM files. Critical batch process that produces customer-facing statements.
- **Risk:** Complex file-oriented logic with page-break calculations and HTML generation.
- **Strategy:** Convert to a Spring Batch job with JPA data access. The report generation can leverage modern templating (Thymeleaf/Jasper).

### Priority 2 — High Value, Moderate Complexity

#### 4. COTRTLIC — Transaction Type List (Score: 46.1)

- **Why:** Large DB2 cursor-based program (2,098 LOC) with paginated browse. Pairs with COTRTUPC — modernize both together.
- **Strategy:** Convert to REST API with pagination. DB2 cursor logic maps directly to Spring Data JPA paging.

#### 5. COPAUA0C — Authorization Engine (Score: 37.2)

- **Why:** Business-critical authorization decision engine integrating CICS + IMS + MQ. High dependency count (8 MQ CALL statements + 3 VSAM reads).
- **Risk:** Three-technology integration (CICS/IMS/MQ) makes this architecturally complex. However, the decision logic itself is relatively straightforward (credit limit checks, card validation).
- **Strategy:** Event-driven microservice consuming from message broker (replacing MQ), with separate account/card lookup services.

#### 6. COCRDUPC — Credit Card Update (Score: 35.5)

- **Why:** 1,560 LOC with 80 IF/EVALUATE statements. Core card management function.
- **Strategy:** REST API + web form. Validation rules can be extracted and shared with account update service.

### Priority 3 — Foundation Batch Programs

#### 7. CBTRN02C — Transaction Posting (Score: 20.7)

- **Why:** Core daily batch pipeline program (731 LOC). Posts transactions, updates balances, writes rejects. Part of the critical daily cycle.
- **Strategy:** Spring Batch job with chunk-oriented processing. Natural fit for modern batch frameworks.

#### 8. CBACT04C — Interest Calculation (Score: 19.7)

- **Why:** Financial calculation engine (652 LOC, 43 IF/EVALUATE). Reads 4 reference files to compute interest.
- **Strategy:** Standalone calculation service. Extract rate tables to database. Unit-test extensively before migration.

### Priority 4 — Shared Utilities (Migrate Early as Foundation)

#### 9. CSUTLDTC — Date Validation Utility

- **Why:** Shared by multiple programs via CALL. Migrate first as a Java utility class that all other migrated programs can depend on.

#### 10. COCOM01Y / CVCRD01Y / CSDAT01Y — Common Copybooks

- **Why:** Referenced by 21 programs each. Convert to shared Java POJOs/DTOs first — they form the data model foundation for all other migrations.

---

## 8. Recommended Migration Sequence

```
Phase 1 — Foundation (Weeks 1–3)
├── Convert common copybooks to Java POJOs/DTOs
│   (COCOM01Y, CVACT01Y, CVCUS01Y, CVACT02Y, CVACT03Y, CVTRA05Y, CSUSR01Y)
├── Migrate CSUTLDTC → Java date utility
└── Set up Spring Boot project with JPA entities matching VSAM record structures

Phase 2 — DB2 Module (Weeks 4–6)
├── COTRTUPC → REST API + JPA (DB2 already relational)
├── COTRTLIC → REST API + Spring Data paging
└── COBTUPDT → Spring Batch job

Phase 3 — Core Batch Pipeline (Weeks 7–10)
├── CBTRN02C → Spring Batch (transaction posting)
├── CBACT04C → Spring Batch (interest calculation)
├── CBTRN03C → Spring Batch (reporting)
├── CBSTM03A/B → Spring Batch (statement generation)
└── CBEXPORT/CBIMPORT → Spring Batch (data migration)

Phase 4 — Online CICS Programs (Weeks 11–16)
├── COSGN00C → Spring Security authentication
├── COMEN01C/COADM01C → Web navigation (React/Angular)
├── COACTUPC → Account update REST API + UI
├── COACTVWC → Account view REST API + UI
├── COCRDLIC/COCRDSLC/COCRDUPC → Card management REST API + UI
├── COTRN00C/01C/02C → Transaction management REST API + UI
├── COUSR00C/01C/02C/03C → User management REST API + UI
├── COBIL00C → Bill payment REST API + UI
└── CORPT00C → Report submission REST API + UI

Phase 5 — IMS/MQ Module (Weeks 17–20)
├── COPAUA0C → Event-driven authorization microservice
├── COPAUS0C/1C/2C → Authorization view REST API + UI
├── COACCT01 → Account inquiry microservice
├── CODATE01 → Date service microservice
└── CBPAUP0C/DBUNLDGS/PAUDBLOD/PAUDBUNL → Spring Batch IMS migration utilities
```

---

## 9. Risk Matrix

| Program | Business Criticality | Technical Risk | Migration Complexity | Overall Risk |
|---------|---------------------|---------------|---------------------|--------------|
| COACTUPC | **High** — core account management | **High** — 18 copybooks, deep validation | **Very High** — 4,236 LOC | 🔴 Critical |
| COPAUA0C | **Critical** — real-time authorization | **High** — CICS + IMS + MQ integration | **High** — 3-technology stack | 🔴 Critical |
| CBTRN02C | **Critical** — daily transaction posting | **Medium** — standard batch pattern | **Medium** — 731 LOC | 🟡 High |
| CBSTM03A | **High** — customer statements | **Medium** — subroutine architecture | **High** — 118 I/O operations | 🟡 High |
| CBACT04C | **High** — financial calculations | **Low** — self-contained logic | **Medium** — 652 LOC | 🟢 Medium |
| COTRTLIC | **Medium** — reference data management | **Low** — already uses DB2 | **Medium** — 2,098 LOC | 🟢 Medium |
| COTRTUPC | **Medium** — reference data maintenance | **Low** — already uses DB2 | **Medium** — 1,702 LOC | 🟢 Medium |
