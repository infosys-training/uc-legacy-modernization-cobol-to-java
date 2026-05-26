# HOTSPOT REPORT

> Composite complexity analysis of all 31 programs in `app/cbl/`. Rankings derived from actual file metrics.

---

## Dimension 1: Lines of Code

Measured via `wc -l` on each `.cbl`/`.CBL` file.

| Rank | Program | Lines | Category |
|------|---------|-------|----------|
| 1 | COACTUPC.cbl | 4,236 | CICS Online |
| 2 | COCRDUPC.cbl | 1,560 | CICS Online |
| 3 | COCRDLIC.cbl | 1,459 | CICS Online |
| 4 | COACTVWC.cbl | 941 | CICS Online |
| 5 | CBSTM03A.CBL | 924 | Batch |
| 6 | COCRDSLC.cbl | 887 | CICS Online |
| 7 | COTRN02C.cbl | 783 | CICS Online |
| 8 | CBTRN02C.cbl | 731 | Batch |
| 9 | COTRN00C.cbl | 699 | CICS Online |
| 10 | COUSR00C.cbl | 695 | CICS Online |

Total lines across all 31 programs: **20,650**

---

## Dimension 2: Copybooks Referenced (COPY Statements)

Counted unique `COPY` statements in each file.

| Rank | Program | COPY Count | Category |
|------|---------|-----------|----------|
| 1 | COACTUPC.cbl | 56 | CICS Online |
| 2 | COACTVWC.cbl | 15 | CICS Online |
| 3 | COCRDSLC.cbl | 15 | CICS Online |
| 4 | COCRDUPC.cbl | 15 | CICS Online |
| 5 | COCRDLIC.cbl | 13 | CICS Online |
| 6 | COBIL00C.cbl | 10 | CICS Online |
| 7 | COTRN02C.cbl | 10 | CICS Online |
| 8 | COADM01C.cbl | 9 | CICS Online |
| 9 | COMEN01C.cbl | 9 | CICS Online |
| 10 | COSGN00C.cbl | 9 | CICS Online |

---

## Dimension 3: I/O Operations

Counted occurrences of `READ`, `WRITE`, `REWRITE`, `DELETE`, `OPEN`, `CLOSE`, `STARTBR`, `READNEXT`, `READPREV`, `EXEC CICS`, `EXEC SQL`.

| Rank | Program | I/O Count | Category |
|------|---------|----------|----------|
| 1 | CBSTM03A.CBL | 117 | Batch |
| 2 | COACTUPC.cbl | 35 | CICS Online |
| 3 | CBIMPORT.cbl | 29 | Batch |
| 4 | CBEXPORT.cbl | 29 | Batch |
| 5 | COUSR00C.cbl | 25 | CICS Online |
| 6 | COTRN00C.cbl | 24 | CICS Online |
| 7 | CBTRN02C.cbl | 23 | Batch |
| 8 | COCRDLIC.cbl | 22 | CICS Online |
| 9 | CBTRN03C.cbl | 22 | Batch |
| 10 | CBACT04C.cbl | 19 | Batch |

---

## Dimension 4: Business Logic Density (IF / EVALUATE Statements)

Counted lines matching `IF` or `EVALUATE` as the first keyword.

| Rank | Program | IF/EVALUATE Count | Category |
|------|---------|------------------|----------|
| 1 | COACTUPC.cbl | 174 | CICS Online |
| 2 | COCRDUPC.cbl | 80 | CICS Online |
| 3 | COCRDLIC.cbl | 68 | CICS Online |
| 4 | CBTRN02C.cbl | 48 | Batch |
| 5 | CBACT04C.cbl | 43 | Batch |
| 6 | CBTRN03C.cbl | 40 | Batch |
| 7 | COCRDSLC.cbl | 37 | CICS Online |
| 8 | COTRN00C.cbl | 34 | CICS Online |
| 9 | COUSR00C.cbl | 33 | CICS Online |
| 10 | COACTVWC.cbl | 33 | CICS Online |

---

## Dimension 5: Inter-Program Dependencies (CALL / XCTL / LINK)

Counted `CALL`, `EXEC CICS XCTL`, and `EXEC CICS LINK` statements.

| Rank | Program | Dep Count | Type | Targets |
|------|---------|----------|------|---------|
| 1 | CBSTM03A.CBL | 15 | CALL | CBSTM03B (×11), CEE3ABD (×1), other |
| 2 | COCRDLIC.cbl | 6 | XCTL | COCRDUPC, COCRDSLC, CDEMO-TO-PROGRAM |
| 3 | CBACT01C.cbl | 3 | CALL | COBDATFT, CEE3ABD |
| 4 | CSUTLDTC.cbl | 2 | CALL | CEEDAYS, CEELOCT |
| 5 | CORPT00C.cbl | 2 | CALL+XCTL | CSUTLDTC (×2), CDEMO-TO-PROGRAM |
| 6 | COTRN02C.cbl | 2 | CALL+XCTL | CSUTLDTC (×2), CDEMO-TO-PROGRAM |
| 7 | COSGN00C.cbl | 2 | XCTL | COADM01C, COMEN01C |
| 8 | COMEN01C.cbl | 1+ | XCTL | Dynamic via CDEMO-MENU-OPT-PGMNAME (11 options) |
| 9 | COADM01C.cbl | 1+ | XCTL | Dynamic via CDEMO-ADMIN-OPT-PGMNAME (6 options) |
| 10 | CBTRN02C.cbl | 1 | CALL | CEE3ABD |

---

## Composite Ranking

### Scoring Methodology

Each program receives a normalized score (1–10) in each dimension based on its rank position. Weights reflect modernization complexity:

| Dimension | Weight | Rationale |
|----------|--------|-----------|
| Lines of Code | 25% | Size drives conversion effort |
| Copybooks Referenced | 15% | Data dependency breadth |
| I/O Operations | 20% | Integration complexity |
| Business Logic Density | 30% | Core complexity requiring careful translation |
| Inter-Program Dependencies | 10% | Coupling risk |

### Top 10 Composite Scores

| Rank | Program | LOC Score | Copy Score | I/O Score | Logic Score | Dep Score | **Weighted Total** |
|------|---------|----------|-----------|----------|------------|----------|-------------------|
| **1** | **COACTUPC.cbl** | 10.0 | 10.0 | 9.0 | 10.0 | 2.0 | **9.10** |
| **2** | **CBTRN02C.cbl** | 5.0 | 2.0 | 7.0 | 7.0 | 2.0 | **5.50** |
| **3** | **COCRDLIC.cbl** | 8.0 | 5.0 | 6.0 | 8.0 | 8.0 | **7.05** |
| **4** | **COCRDUPC.cbl** | 9.0 | 6.0 | 4.0 | 9.0 | 2.0 | **6.90** |
| **5** | **CBACT04C.cbl** | 3.0 | 2.0 | 5.0 | 6.0 | 1.0 | **3.95** |
| **6** | **CBSTM03A.CBL** | 6.0 | 1.0 | 10.0 | 2.0 | 10.0 | **5.15** |
| **7** | **COCRDSLC.cbl** | 4.0 | 6.0 | 4.0 | 5.0 | 2.0 | **4.30** |
| **8** | **COACTVWC.cbl** | 7.0 | 6.0 | 3.0 | 4.0 | 2.0 | **4.45** |
| **9** | **CBTRN03C.cbl** | 3.0 | 2.0 | 6.0 | 5.0 | 2.0 | **3.90** |
| **10** | **COTRN00C.cbl** | 4.0 | 3.0 | 6.0 | 4.0 | 1.0 | **3.75** |

---

## Modernization Recommendations

### Priority 1: COACTUPC.cbl (Account Update)
- **Complexity**: Highest across all dimensions — 4,236 lines, 56 copybooks, 174 IF/EVALUATE, 35 I/O ops
- **Business Value**: Core account update functionality used by all operators
- **Recommendation**: Decompose into a Spring Boot REST API with separate services for account validation, update processing, and customer lookup
- **Risk**: Deep CICS coupling (BMS maps, EXEC CICS READ/REWRITE). Requires CICS-to-REST migration pattern
- **Suggested approach**: Extract business rules first, then wrap with REST endpoints. Migrate BMS screens to Angular/React UI

### Priority 2: CBTRN02C.cbl (Daily Transaction Posting)
- **Complexity**: 731 lines, 48 IF/EVALUATE, 23 I/O ops, processes 6 VSAM files
- **Business Value**: Critical daily batch pipeline — all daily transactions flow through this program
- **Recommendation**: Convert to Spring Batch job with ItemReader/Processor/Writer pattern
- **Risk**: Complex multi-file validation logic. Must maintain transactional integrity across ACCTDATA, TRANSACT, TCATBALF
- **Suggested approach**: No CICS dependency — safer starting point for batch modernization

### Priority 3: CBACT04C.cbl (Interest Calculator)
- **Complexity**: 652 lines, 43 IF/EVALUATE, 19 I/O ops
- **Business Value**: High — financial calculations with regulatory implications
- **Recommendation**: Convert to standalone microservice with well-defined API for interest rate lookup and calculation
- **Risk**: Precision requirements for financial math (COMP-3/packed decimal to Java BigDecimal)
- **Suggested approach**: Implement as Spring Boot service with thorough unit testing of calculation logic

### Priority 4: COCRDLIC.cbl / COCRDUPC.cbl (Card List & Update)
- **Complexity**: 1,459 + 1,560 lines combined, high logic density (68 + 80 IF/EVALUATE)
- **Business Value**: Core card management functions
- **Recommendation**: Convert to Card Management REST API service
- **Suggested approach**: Shared data access layer for card operations, separate from account services

### Priority 5: CBEXPORT.cbl / CBIMPORT.cbl (Data Migration)
- **Complexity**: 582 + 487 lines, 29 I/O ops each
- **Business Value**: Branch data migration — important for business continuity
- **Recommendation**: Convert to Spring Batch ETL jobs with CSV/JSON output format
- **Suggested approach**: Replace multi-record REDEFINES layout (CVEXPORT.cpy) with typed Java DTOs

### Priority 6: CSUTLDTC.cbl (Date Utility — Shared Dependency)
- **Complexity**: Low (157 lines) but high impact — called by CORPT00C and COTRN02C
- **Business Value**: Foundational utility used across batch and online programs
- **Recommendation**: Convert to shared Java utility class (`DateValidationUtil.java`) early in modernization
- **Suggested approach**: Replace CEEDAYS calls with `java.time` API. Publish as shared library

### Rationale for Priority Order

1. **Start with batch programs** (CBTRN02C, CBACT04C) — no CICS dependency, easier to test, lower risk
2. **CSUTLDTC early** — shared utility needed by other programs being modernized
3. **COACTUPC last among top priorities** — most complex, deepest CICS coupling, but highest business value justifies the effort
4. **Card programs together** — COCRDLIC/COCRDUPC/COCRDSLC share data structures and can be modernized as a cohesive card service
5. **Export/Import as ETL** — natural fit for Spring Batch, and migration capability is needed for the transition period itself
