# HOTSPOT REPORT — CardDemo COBOL Estate

> Programs ranked by complexity metrics to identify modernization priorities.
> Composite hotspot scores weight: LOC (25%), Copybooks (20%), I/O Ops (20%), Branch Density (25%), Inter-Program Deps (10%).

---

## Table of Contents

- [1. Composite Hotspot Ranking (Top 10)](#1-composite-hotspot-ranking-top-10)
- [2. Ranking by Lines of Code](#2-ranking-by-lines-of-code)
- [3. Ranking by Copybooks Referenced](#3-ranking-by-copybooks-referenced)
- [4. Ranking by I/O Operations](#4-ranking-by-io-operations)
- [5. Ranking by Business Logic Density](#5-ranking-by-business-logic-density)
- [6. Ranking by Inter-Program Dependencies](#6-ranking-by-inter-program-dependencies)
- [7. Modernization Recommendations](#7-modernization-recommendations)
- [8. Recommended Migration Sequence](#8-recommended-migration-sequence)

---

## 1. Composite Hotspot Ranking (Top 10)

Each metric is normalized to 0–100 relative to the estate maximum, then weighted.

| Rank | Program | LOC | Copybooks | I/O Ops | Branch (IF+EVAL) | Inter-Prog Deps | **Hotspot Score** |
|------|---------|-----|-----------|---------|-------------------|-----------------|-------------------|
| **1** | **COACTUPC.cbl** | 4,236 | 18 | 113 | 174 (IF=164, EVAL=10) | 1 | **74.8** |
| **2** | **CBSTM03A.CBL** | 924 | 4 | 164 | 20 (IF=18, EVAL=2) | 14 | **35.2** |
| **3** | **COTRTLIC.cbl** | 2,098 | 2 | 18 | 55 (IF=49, EVAL=6) | 3 | **33.1** |
| **4** | **CBTRN02C.cbl** | 731 | 6 | 62 | 48 (IF=48, EVAL=0) | 2 | **28.5** |
| **5** | **COTRTUPC.cbl** | 1,702 | 2 | 15 | 40 (IF=35, EVAL=5) | 1 | **27.0** |
| **6** | **COCRDLIC.cbl** | 1,459 | 10 | 50 | 68 (IF=59, EVAL=9) | 6 | **39.2** |
| **7** | **COCRDUPC.cbl** | 1,560 | 12 | 23 | 80 (IF=72, EVAL=8) | 1 | **36.6** |
| **8** | **COPAUA0C.cbl** | 1,026 | 14 | 14 | 22 (IF=17, EVAL=5) | 8 | **24.2** |
| **9** | **CBACT04C.cbl** | 652 | 5 | 53 | 43 (IF=43, EVAL=0) | 1 | **23.8** |
| **10** | **CBTRN03C.cbl** | 649 | 5 | 86 | 19 (IF=17, EVAL=2) | 1 | **23.1** |

### Score Calculation

```
Score = (LOC/4236)*25 + (Copybooks/18)*20 + (IO/164)*20 + (Branch/174)*25 + (Deps/14)*10
```

The normalization denominators are the maximum values in the estate:
- Max LOC: 4,236 (COACTUPC)
- Max Copybooks: 18 (COACTUPC)
- Max I/O Ops: 164 (CBSTM03A)
- Max Branch Statements: 174 (COACTUPC)
- Max Inter-Program Deps: 14 (CBSTM03A)

---

## 2. Ranking by Lines of Code

| Rank | Program | LOC | Classification | Domain |
|------|---------|-----|---------------|--------|
| 1 | **COACTUPC.cbl** | 4,236 | Online (CICS) | Account Update |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS/DB2) | Tran Type List |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS/DB2) | Tran Type Update |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) | Card Update |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) | Card List |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS/IMS) | Auth Summary Browse |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS/IMS/MQ/DB2) | Auth Decision |
| 8 | COACTVWC.cbl | 941 | Online (CICS) | Account View |
| 9 | CBSTM03A.CBL | 924 | Batch | Statement Generation |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) | Card View |

**Observation:** The top 3 programs (COACTUPC, COTRTLIC, COTRTUPC) account for 29% of total LOC. COACTUPC alone is 15.5% of the entire COBOL estate.

---

## 3. Ranking by Copybooks Referenced

| Rank | Program | Copybook Count | Key Copybooks |
|------|---------|----------------|---------------|
| 1 | **COACTUPC.cbl** | 18 | CSLKPCDY (validation), CSSETATY (3× COPY REPLACING), all 6 infra copybooks |
| 2 | COPAUA0C.cbl | 14 | MQ copybooks (CMQGMOV, CMQMDV, CMQODV, CMQPMOV, CMQTML, CMQV), IMS + VSAM |
| 3 | COACTVWC.cbl | 14 | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVCRD01Y + infra |
| 4 | COCRDUPC.cbl | 12 | CVACT02Y, CVCRD01Y, CVCUS01Y + infra |
| 5 | COPAUS0C.cbl | 12 | CIPAUDTY, CIPAUSMY (IMS) + VSAM record copybooks + infra |
| 6 | COCRDLIC.cbl | 10 | CVACT02Y, CVCRD01Y + infra |
| 7 | COCRDSLC.cbl | 10 | CVACT02Y, CVCRD01Y, CVCUS01Y + infra |
| 8 | COTRN02C.cbl | 10 | CVACT01Y, CVACT03Y, CVTRA05Y + infra |
| 9 | COBIL00C.cbl | 10 | CVACT01Y, CVACT03Y, CVTRA05Y + infra |
| 10 | COMEN01C.cbl | 9 | COMEN02Y (menu options table) + infra |

**Observation:** COACTUPC's 18 copybooks include 3× COPY REPLACING of CSSETATY — a macro-like pattern that generates screen attribute-setting code for each validated field. This has no direct Java equivalent and requires conversion to parameterized utility methods.

---

## 4. Ranking by I/O Operations

I/O operations include: READ, WRITE, REWRITE, DELETE, OPEN, CLOSE, START, STARTBR, READNEXT, READPREV, ENDBR, and EXEC CICS variants.

| Rank | Program | I/O Ops | Classification | Key Files Accessed |
|------|---------|---------|---------------|--------------------|
| 1 | **CBSTM03A.CBL** | 164 | Batch | TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE, STMTFILE, HTMLFILE |
| 2 | COACTUPC.cbl | 113 | Online (CICS) | ACCTDAT, CARDXREF, CUSTDAT |
| 3 | CBTRN03C.cbl | 86 | Batch | TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM, TRANREPT |
| 4 | CBTRN02C.cbl | 62 | Batch | DALYTRAN, XREFFILE, ACCTFILE, TRANFILE, TCATBALF, DALYREJS |
| 5 | CBTRN01C.cbl | 62 | Batch | DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANSACT |
| 6 | CBEXPORT.cbl | 54 | Batch | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE, EXPFILE |
| 7 | CBACT04C.cbl | 53 | Batch | TCATBALF, XREFFILE, ACCTFILE, DISCGRP, TRANSACT |
| 8 | COCRDLIC.cbl | 50 | Online (CICS) | CARDDAT (browse via AIX) |
| 9 | CBIMPORT.cbl | 43 | Batch | EXPFILE, CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, ERROUT |
| 10 | CBACT01C.cbl | 42 | Batch | ACCTFILE, OUTFILE, ARRYFILE, VBRCFILE |

**Observation:** CBSTM03A has the highest I/O density (164 ops in 924 LOC = 0.18 I/O ops per line), making it the most I/O-bound program. Its 97 WRITE operations generate statement output line-by-line — a prime candidate for template-based bulk generation in Java.

---

## 5. Ranking by Business Logic Density

Business logic density is measured by IF + EVALUATE statement count — indicators of branching complexity, validation logic, and decision trees.

| Rank | Program | IF Count | EVALUATE Count | Total Branch | Branch per 100 LOC |
|------|---------|----------|----------------|-------------|-------------------|
| 1 | **COACTUPC.cbl** | 164 | 10 | 174 | 4.1 |
| 2 | COCRDUPC.cbl | 72 | 8 | 80 | 5.1 |
| 3 | COCRDLIC.cbl | 59 | 9 | 68 | 4.7 |
| 4 | COTRTLIC.cbl | 49 | 6 | 55 | 2.6 |
| 5 | CBTRN02C.cbl | 48 | 0 | 48 | 6.6 |
| 6 | CBACT04C.cbl | 43 | 0 | 43 | 6.6 |
| 7 | COTRTUPC.cbl | 35 | 5 | 40 | 2.3 |
| 8 | COPAUS0C.cbl | 25 | 11 | 36 | 3.5 |
| 9 | COACTVWC.cbl | 28 | 5 | 33 | 3.5 |
| 10 | CBTRN01C.cbl | 33 | 0 | 33 | 6.7 |

**Observation:** While COACTUPC has the highest absolute branch count (174), CBTRN01C and CBTRN02C have the highest branch *density* (6.6–6.7 per 100 LOC). High density in small programs means concentrated business rules — easier to extract but critical to test exhaustively.

---

## 6. Ranking by Inter-Program Dependencies

Counts CALL statements (batch) + EXEC CICS XCTL/LINK (online) per program.

| Rank | Program | CALL | XCTL/LINK | Total | Targets |
|------|---------|------|-----------|-------|---------|
| 1 | **CBSTM03A.CBL** | 14 | 0 | 14 | CBSTM03B (×12), CEE3ABD (×2) |
| 2 | PAUDBLOD.CBL | 9 | 0 | 9 | IMS DL/I calls (ISRT, GU) |
| 3 | CODATE01.cbl | 9 | 0 | 9 | MQOPEN, MQGET, MQPUT, MQCLOSE |
| 4 | COACCT01.cbl | 9 | 0 | 9 | MQOPEN, MQGET, MQPUT, MQCLOSE |
| 5 | COPAUA0C.cbl | 8 | 0 | 8 | MQOPEN, MQGET, MQPUT1, MQCLOSE |
| 6 | COCRDLIC.cbl | 3 | 3 | 6 | XCTL to COCRDSLC, COCRDUPC, menu |
| 7 | DBUNLDGS.CBL | 7 | 0 | 7 | IMS DL/I calls (GN, GNP, ISRT) |
| 8 | PAUDBUNL.CBL | 5 | 0 | 5 | IMS DL/I calls (GN, GNP) |
| 9 | COTRTLIC.cbl | 1 | 2 | 3 | XCTL to COTRTUPC, menu |
| 10 | CBACT01C.cbl | 3 | 0 | 3 | COBDATFT, CEE3ABD |

**Observation:** CBSTM03A has the highest dependency count because it calls CBSTM03B 12 times (once per I/O operation type). This tight coupling means CBSTM03A and CBSTM03B must be migrated together as a single unit.

---

## 7. Modernization Recommendations

### Program-Level Recommendations

| Rank | Program | Hotspot Score | Recommendation | Estimated Effort | Risk Level |
|------|---------|--------------|----------------|-----------------|------------|
| **1** | **COACTUPC.cbl** | 74.8 | **Decompose first, migrate second.** Extract validation into `AccountValidationService` (date, SSN, phone, state, ZIP from CSLKPCDY). Replace 3× COPY REPLACING with parameterized Java methods. Split into AccountController (REST) + AccountService + AccountView. Generate 10,000+ test cases before rewriting. Run shadow mode for 2 weeks. | 6–8 weeks | **HIGH** — Single largest risk; 15.5% of estate LOC. |
| **2** | **CBSTM03A.CBL** | 35.2 | **Pilot migration candidate.** Self-contained batch, no CICS dependency, clean I/O-only architecture. Convert to Spring Batch with `FlatFileItemReader` / template-based writer. Replace 97 line-by-line WRITEs with Thymeleaf HTML template. Migrate CBSTM03B I/O logic inline. | 2–3 weeks | **LOW** — Isolated, testable, high confidence. |
| **3** | **COCRDLIC.cbl** | 39.2 | **Extract reusable pattern.** Paginated browse (STARTBR/READNEXT/READPREV/ENDBR) appears in 5+ programs. Build generic `AbstractPaginatedListController<T>` in Java, then apply to COCRDLIC, COUSR00C, COTRN00C, COPAUS0C. | 3–4 weeks | **MEDIUM** — Pattern extraction pays dividends. |
| **4** | **COCRDUPC.cbl** | 36.6 | **Migrate with card-service.** Pair with COCRDLIC + COCRDSLC as the card-service microservice. 80 branch statements = extensive validation to extract. | 3–4 weeks | **MEDIUM** |
| **5** | **COTRTLIC.cbl** | 33.1 | **Natural DB2 entry point.** Already uses SQL cursors — closest to modern RDBMS patterns. Convert DB2 cursor pagination to Spring Data JPA `Pageable`. Migrate with COTRTUPC as transaction-type-service. | 3–4 weeks | **LOW** — SQL translates with minimal changes. |
| **6** | **COTRTUPC.cbl** | 27.0 | **Migrate with COTRTLIC.** CRUD operations on TRANSACTION_TYPE/CATEGORY DB2 tables → JPA entities. | 2–3 weeks | **LOW** |
| **7** | **CBTRN02C.cbl** | 28.5 | **Core batch pipeline — migrate in Phase 5.** Transaction posting with multi-file updates + reject handling. Must maintain dual-write during transition. Use JdbcBatchItemWriter with batch size ≥ 1,000. | 4–5 weeks | **HIGH** — Critical path; financial data. |
| **8** | **COPAUA0C.cbl** | 24.2 | **Most architecturally complex — migrate last.** Spans MQ + IMS + CICS + DB2. Replace MQ with SQS, IMS with PostgreSQL, CICS with REST. COPAUS0C→COPAUS1C→COPAUS2C chain must migrate as a unit. | 6–8 weeks | **HIGH** — Cross-subsystem integration. |
| **9** | **CBACT04C.cbl** | 23.8 | **Interest calculation — high financial accuracy requirement.** PIC S9(10)V99 → BigDecimal mandatory. Test with production-scale data (thousands of accounts). Verify interest amounts to the penny. | 3–4 weeks | **HIGH** — Monetary precision critical. |
| **10** | **CBTRN03C.cbl** | 23.1 | **Report generation — migrate with reporting-service.** Sequential file read + formatted output. Convert to Spring Batch + JasperReports or Thymeleaf templates. | 2–3 weeks | **LOW** |

### Cross-Cutting Concerns

| Concern | Impact | Recommendation |
|---------|--------|---------------|
| **COBOL fixed-point arithmetic** | All monetary fields use PIC S9(n)V99 | **MANDATORY: Use `java.math.BigDecimal` for ALL monetary fields.** Map PIC S9(10)V99 → BigDecimal(precision=12, scale=2). Use `RoundingMode.HALF_UP`. Never use `double`/`float` for money. |
| **Overpunch sign encoding** | Trailing overpunch chars ({, A-I, }, J-R) | Build reusable `OverpunchDecoder` utility. Test exhaustively: `}` = -0, `J` = -1, etc. Verify with non-zero values ending in 0 (e.g., `00000001940}` = -194.00, NOT -0.00). |
| **Plain-text passwords** | CSUSR01Y stores SEC-USR-PWD as PIC X(08) | Implement bcrypt/scrypt hashing + Spring Security. **Security gap in COBOL system.** |
| **COPY REPLACING pattern** | CSSETATY used 3× in COACTUPC | No direct Java equivalent. Convert to parameterized validation utility methods. |
| **Assembler dependencies** | COBDATFT, CEE3ABD, CEEDAYS, MVSWAIT | Replace with `java.time`, standard exception handling, and `Thread.sleep()`. |

---

## 8. Recommended Migration Sequence

### Phase 0: Foundation (Weeks 1–4)

- Set up Spring Boot project structure, PostgreSQL schema (Flyway), CI/CD pipeline
- Create shared libraries: `carddemo-validation`, `carddemo-commons`, `carddemo-security`
- Build `OverpunchDecoder` and `MoneyUtils` (BigDecimal) utilities
- Design REST API contracts from COMMAREA fields
- Generate baseline test data from EBCDIC datasets in `app/data/`

### Phase 1: Quick Wins — User Auth + Reporting Pilot (Weeks 5–9)

| Program | Score | Effort | Why First |
|---------|-------|--------|-----------|
| COSGN00C, COUSR00C–03C, COADM01C, COMEN01C | — | 4–5w | Simple CRUD, isolated data (USRSEC file), proves migration approach |
| CBSTM03A/B | 35.2 | 2–3w | Self-contained batch, highest I/O density, excellent pilot to validate Spring Batch approach |

### Phase 2: DB2 Programs + Card Service (Weeks 10–16)

| Program | Score | Effort | Why Second |
|---------|-------|--------|-----------|
| COTRTLIC, COTRTUPC, COBTUPDT | 33.1 / 27.0 | 3–4w | Already SQL-based; minimal transformation needed |
| COCRDLIC, COCRDSLC, COCRDUPC | 39.2 / 36.6 | 3–4w | Extracts reusable paginated browse pattern for later phases |

### Phase 3: Account + Transaction Services (Weeks 17–26)

| Program | Score | Effort | Why Third |
|---------|-------|--------|-----------|
| COACTUPC, COACTVWC | 74.8 | 6–8w | Highest complexity; decompose into services using patterns proven in Phase 2 |
| COTRN00C–02C, COBIL00C | 28.5 | 4–5w | Transaction processing; shares ACCTDATA with account service |

### Phase 4: Authorization Service (Weeks 27–34)

| Program | Score | Effort | Why Fourth |
|---------|-------|--------|-----------|
| COPAUA0C, COPAUS0C–2C, CBPAUP0C | 24.2 | 6–8w | Cross-subsystem (IMS+MQ+DB2+CICS); requires Phase 3 account/card services to be stable first |

### Phase 5: Batch Pipeline (Weeks 35–39)

| Program | Score | Effort | Why Last |
|---------|-------|--------|---------|
| CBTRN01C, CBTRN02C, CBACT04C, CBTRN03C | 28.5 / 23.8 | 4–5w | Core batch pipeline must migrate after online programs to maintain dual-write consistency during transition |
| CBEXPORT, CBIMPORT | — | 1–2w | Data migration tools; retire after cutover |

### Phase 6: Decommission (Weeks 40–42)

- Remove VSAM-to-PostgreSQL sync bridge
- Decommission IMS database
- Retire MQ queues (replaced by SQS)
- Archive COBOL source and JCL
- Final validation: 30-day stability observation

### Estimated Total: 32–42 weeks with a 4-person team
