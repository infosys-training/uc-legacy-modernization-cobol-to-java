# HOTSPOT REPORT — CardDemo COBOL Estate

> Programs ranked by complexity metrics to guide modernization prioritization.  
> Metrics collected via static analysis of all 44 COBOL programs.

---

## 1. Top 10 Programs — Composite Ranking

Programs ranked by a weighted composite score across five dimensions:

| Rank | Program | LOC | Copybooks | I/O Ops (CICS+SQL+File) | Logic Density (IF/EVALUATE) | Inter-Program Deps (CALL/XCTL targets) | Composite Score |
|------|---------|-----|-----------|------------------------|----------------------------|-----------------------------------------|-----------------|
| **1** | **COACTUPC** | 4,236 | 54 | 17 CICS | 174 | 0 CALL, many XCTL targets | **★★★★★** |
| **2** | **COTRTLIC** | 2,098 | ~12 | 12 CICS + 16 SQL = 28 | ~60 | 1 CALL, XCTL to COTRTUPC | **★★★★☆** |
| **3** | **COTRTUPC** | 1,702 | ~14 | 12 CICS + 7 SQL = 19 | ~55 | 0 | **★★★★☆** |
| **4** | **COCRDUPC** | 1,560 | 12 | 12 CICS | 80 | 0 | **★★★☆☆** |
| **5** | **COCRDLIC** | 1,459 | 10 | 18 CICS | 68 | 3 CALL | **★★★☆☆** |
| **6** | **COPAUS0C** | 1,032 | 14 | 10 CICS | 36 | 0 | **★★★☆☆** |
| **7** | **COPAUA0C** | 1,026 | 16 | 12 CICS | 31 | 8 CALL (MQ API) | **★★★☆☆** |
| **8** | **COACTVWC** | 941 | 14 | 15 CICS | 33 | 0 | **★★☆☆☆** |
| **9** | **CBSTM03A** | 924 | 4 | 117 file I/O | 20 | 14 CALL (CBSTM03B×13 + CEE3ABD) | **★★☆☆☆** |
| **10** | **COCRDSLC** | 887 | 12 | 14 CICS | 37 | 0 | **★★☆☆☆** |

---

## 2. Detailed Metric Rankings

### 2.1 By Lines of Code (LOC)

| Rank | Program | LOC | Classification |
|------|---------|-----|----------------|
| 1 | COACTUPC | 4,236 | Online (CICS) |
| 2 | COTRTLIC | 2,098 | Online (CICS+DB2) |
| 3 | COTRTUPC | 1,702 | Online (CICS+DB2) |
| 4 | COCRDUPC | 1,560 | Online (CICS) |
| 5 | COCRDLIC | 1,459 | Online (CICS) |
| 6 | COPAUS0C | 1,032 | Online (CICS) |
| 7 | COPAUA0C | 1,026 | Online (CICS+MQ+IMS) |
| 8 | COACTVWC | 941 | Online (CICS) |
| 9 | CBSTM03A | 924 | Batch |
| 10 | COCRDSLC | 887 | Online (CICS) |

### 2.2 By Copybooks Referenced

| Rank | Program | Count | Copybooks |
|------|---------|-------|-----------|
| 1 | COACTUPC | 54 | CSUTLDWY, CVCRD01Y, CSLKPCDY, DFHBMSCA, DFHAID, COTTL01Y, COACTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, COCOM01Y, CSSETATY (many multi-line COPY with data) |
| 2 | COPAUA0C | 16 | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y, + MQ headers |
| 3 | COACTVWC | 14 | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COACTVW, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 4 | COPAUS0C | 14 | COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CIPAUSMY, CIPAUDTY, DFHAID, DFHBMSCA |
| 5 | COCRDUPC | 12 | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y |
| 5 | COCRDSLC | 12 | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDSL, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y |
| 7 | COBIL00C | 10 | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA |
| 7 | COCRDLIC | 10 | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COCRDLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y |
| 7 | COTRN02C | 10 | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA |
| 10 | CORPT00C | 8 | COCOM01Y, CORPT00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, DFHAID, DFHBMSCA |

### 2.3 By I/O Operations

| Rank | Program | Total I/O | Breakdown |
|------|---------|-----------|-----------|
| 1 | CBSTM03A | 117 | 97 WRITE (statements+HTML), 14 CALL CBSTM03B (each does OPEN/READ/CLOSE), OPEN/CLOSE files |
| 2 | COTRTLIC | 28 | 12 EXEC CICS + 16 EXEC SQL (DECLARE/OPEN/FETCH/CLOSE CURSOR, DELETE) |
| 3 | CBTRN02C | 23 | READ/WRITE/REWRITE across 6 files (DALYTRAN, XREF, ACCOUNT, TCATBAL, TRANSACT, DALYREJS) |
| 4 | CBTRN03C | 21 | READ across 5 files (TRANSACT, XREF, TRANTYPE, TRANCATG, DATEPARMS) + WRITE REPORT |
| 5 | CBACT04C | 19 | READ across 4 files (TCATBAL, XREF, ACCOUNT, DISCGRP) + REWRITE ACCOUNT + WRITE TRANSACT |
| 6 | CBEXPORT | 22 | READ 5 VSAM masters + WRITE EXPORT sequential |
| 7 | COTRTUPC | 19 | 12 EXEC CICS + 7 EXEC SQL (SELECT/INSERT/UPDATE/DELETE) |
| 8 | COCRDLIC | 18 | 18 EXEC CICS (STARTBR, READNEXT×n, READPREV×n, ENDBR, SEND/RECEIVE MAP) |
| 9 | COACTUPC | 17 | 17 EXEC CICS (READ/REWRITE CARDXREF, ACCTDATA, CUSTDATA + MAP operations) |
| 10 | COACTVWC | 15 | 15 EXEC CICS (READ CARDXREF via AIX, ACCTDATA, CUSTDATA + MAP operations) |

### 2.4 By Business Logic Density (IF/EVALUATE Count)

| Rank | Program | IF/EVALUATE | Context |
|------|---------|-------------|---------|
| 1 | COACTUPC | 174 | Extensive field-level validation: SSN, dates, phone numbers, state codes, ZIP codes, amounts, credit limits |
| 2 | COCRDUPC | 80 | Card field validation: card number, account, name, status, expiry month/year |
| 3 | COCRDLIC | 68 | Scroll position logic, page boundary checks, card status evaluation |
| 4 | CBTRN02C | 48 | Transaction posting validation: card lookup, amount checks, category balance updates |
| 5 | CBACT04C | 43 | Interest calculation conditionals: rate lookups, balance checks, fee computations |
| 6 | CBTRN03C | 40 | Report formatting: page breaks, account totals, date range filtering |
| 7 | COCRDSLC | 37 | Card display formatting and error condition handling |
| 8 | COPAUS0C | 36 | Auth list browse with scrolling and status checking |
| 9 | COTRN00C | 34 | Transaction list browse with scroll pagination |
| 10 | COACTVWC | 33 | Account/customer display field formatting |

### 2.5 By Inter-Program Dependencies

| Rank | Program | Dep Count | Details |
|------|---------|-----------|---------|
| 1 | CBSTM03A | 14 | 13× CALL 'CBSTM03B' (I/O subroutine) + CALL 'CEE3ABD' |
| 2 | COPAUA0C | 8 | MQOPEN, MQGET, MQPUT1, MQCLOSE (×2 each for request+reply queues) |
| 3 | COMEN01C | 11 targets | XCTL to 11 menu option programs |
| 4 | COADM01C | 6 targets | XCTL to 6 admin menu option programs |
| 5 | COCRDLIC | 3 | CALL for date handling, XCTL targets |
| 6 | COTRN02C | 2 | CALL 'CSUTLDTC' ×2 (date validation) |
| 7 | CORPT00C | 2 | CALL 'CSUTLDTC' ×2 |
| 8 | CBTRN02C | 1 | CALL 'CEE3ABD' |
| 9 | CBACT04C | 1 | CALL 'CEE3ABD' |
| 10 | CBEXPORT | 1 | CALL 'CEE3ABD' |

---

## 3. Modernization Priority Recommendations

### Tier 1 — Modernize First (Highest Impact)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **P1** | **COACTUPC** (Account Update) | **Highest complexity in the estate.** 4,236 LOC with 174 IF/EVALUATE branches, 54 copybooks, and extensive validation logic (SSN, dates, phone area codes, state codes, ZIP codes, amounts). This is the core account maintenance function touching 3 VSAM files (account, customer, card xref). Its enormous validation logic makes it the highest-risk program for bugs during modernization and the highest-value target for automated testing. *Approach: decompose into microservices — separate validation, persistence, and UI layers.* |
| **P2** | **COTRTLIC** (Transaction Type List — DB2) | **Largest DB2 program.** 2,098 LOC with 16 SQL operations and 12 CICS calls. Already uses DB2, making it a natural candidate for migration to a modern RDBMS-backed service. The SQL cursor logic maps directly to JPA/JDBC patterns. *Approach: extract DB2 CRUD into a REST API; the CICS screen becomes a web UI.* |
| **P3** | **COTRTUPC** (Transaction Type Update — DB2) | **Second DB2 program.** 1,702 LOC with SQL INSERT/UPDATE/DELETE and form validation. Tightly coupled with COTRTLIC. *Approach: modernize together with COTRTLIC as a single Transaction Type management service.* |
| **P4** | **CBTRN02C** (Post Daily Transactions) | **Core batch engine.** 731 LOC but touches 6 files with 48 business logic branches. This is the heart of the daily batch cycle — it validates, posts transactions, updates account balances, and manages category balances. Critical path for daily operations. *Approach: convert to a Spring Batch job with step-based processing.* |

### Tier 2 — Modernize Second (High Value, Moderate Complexity)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **P5** | **COCRDUPC** (Card Update) | 1,560 LOC with 80 IF branches. Card maintenance with field validation. Depends on CICS VSAM I/O. *Approach: pair with COCRDLIC and COCRDSLC as a Card Management microservice.* |
| **P6** | **COCRDLIC** (Card List) | 1,459 LOC. Heavy CICS browse logic (STARTBR/READNEXT/READPREV) with 68 IF branches and pagination. *Approach: replace CICS browse with paginated REST API queries.* |
| **P7** | **COPAUA0C** (Auth Engine) | 1,026 LOC spanning CICS + MQ + IMS. Most diverse technology stack in the estate. 8 MQ CALL operations + CICS VSAM reads + IMS DB writes. *Approach: event-driven microservice with message queue integration.* |
| **P8** | **CBACT04C** (Interest Calculation) | 652 LOC with 43 IF branches. Monthly interest/fee computation touching 4 VSAM files. Core financial logic. *Approach: extract calculation logic into a stateless service; batch orchestration via modern scheduler.* |

### Tier 3 — Modernize Third (Lower Complexity, Standardized Patterns)

| Priority | Program | Rationale |
|----------|---------|-----------|
| **P9** | **CBSTM03A/B** (Statement Generation) | 924+230 LOC. Highest I/O volume (117 operations) but straightforward read→format→write pipeline. CBSTM03B is a clean I/O abstraction layer. *Approach: template-based document generation service.* |
| **P10** | **CBTRN03C** (Transaction Reports) | 649 LOC. Report formatting with page/account/grand totals. Reads from 5 files. *Approach: replace with a reporting service using modern templating (JasperReports, etc.).* |

### Tier 4 — Deferred / Low Risk

All remaining programs follow standardized CICS patterns (sign-on, menu routing, CRUD screens, user management) with relatively low complexity:

- **COACTVWC** (941 LOC) — read-only account view, low risk
- **COCRDSLC** (887 LOC) — read-only card view
- **COTRN00C/01C/02C** — transaction list/view/add screens
- **CORPT00C** — report request submission
- **COBIL00C** — bill payment (572 LOC, moderate)
- **COSGN00C/COMEN01C/COADM01C** — navigation programs
- **COUSR00C/01C/02C/03C** — user CRUD (can be replaced by identity provider)
- **COPAUS0C/1C/2C** — auth viewing screens
- **Batch utilities** (CBACT01-03C, CBEXPORT, CBIMPORT, COBSWAIT, CSUTLDTC) — simple utilities

---

## 4. Risk Matrix

| Risk Factor | High Risk Programs | Mitigation |
|-------------|-------------------|------------|
| **Data integrity** | COACTUPC, CBTRN02C, CBACT04C, COBIL00C | Comprehensive regression test suites before migration; parallel-run validation |
| **Multi-technology stack** | COPAUA0C (CICS+MQ+IMS), COTRTLIC/COTRTUPC (CICS+DB2) | Phased migration — decouple middleware dependencies first |
| **High branch complexity** | COACTUPC (174), COCRDUPC (80), COCRDLIC (68) | Extract validation rules into testable business rule engines |
| **Critical batch path** | CBTRN02C, CBACT04C, CBTRN03C, CBSTM03A | Maintain batch interfaces during migration; reconciliation checks |
| **Cross-cutting copybooks** | COCOM01Y (21 programs), CVACT01Y (14 programs), CVACT03Y (14 programs) | Modernize shared data structures first as domain model classes |

---

## 5. Summary Statistics

| Metric | Value |
|--------|-------|
| Total programs analyzed | 44 |
| Total lines of COBOL | ~26,500 |
| Largest program | COACTUPC (4,236 LOC) |
| Most complex program | COACTUPC (174 IF/EVALUATE branches) |
| Most I/O intensive | CBSTM03A (117 file operations) |
| Most inter-program dependencies | CBSTM03A (14 CALL statements) |
| Most copybooks referenced | COACTUPC (54 COPY statements) |
| Programs with DB2 access | 3 (COTRTLIC, COTRTUPC, COBTUPDT, COPAUS2C) |
| Programs with MQ access | 3 (COPAUA0C, COACCT01, CODATE01) |
| Programs with IMS access | 4 (COPAUA0C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS) |
| CICS online programs | 23 |
| Batch programs | 21 |
