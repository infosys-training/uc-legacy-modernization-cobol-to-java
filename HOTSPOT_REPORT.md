# HOTSPOT REPORT — CardDemo COBOL Estate

> Ranking of program complexity, dependency weight, and modernization priority.

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification | Domain |
|------|---------|-----|----------------|--------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) | Account Update |
| 2 | COTRTLIC.cbl* | 2,099 | Online (CICS/DB2) | Transaction Type List |
| 3 | COTRTUPC.cbl* | 1,703 | Online (CICS/DB2) | Transaction Type Update |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) | Credit Card Update |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) | Credit Card List |
| 6 | COACTVWC.cbl | 941 | Online (CICS) | Account View |
| 7 | CBSTM03A.CBL | 924 | Batch | Statement Generation |
| 8 | COCRDSLC.cbl | 887 | Online (CICS) | Card Detail Selection |
| 9 | COTRN02C.cbl | 784 | Online (CICS) | Transaction Add |
| 10 | CBTRN02C.cbl | 732 | Batch | Daily Transaction Posting |

*\*Sub-application programs*

---

## 2. Top 10 Programs by Number of Copybooks Referenced

| Rank | Program | Copybooks | Copybook List |
|------|---------|-----------|---------------|
| 1 | COACTUPC.cbl | 14 | COCOM01Y, COACTUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, CSSTRPFY |
| 2 | COCRDUPC.cbl | 13 | COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CSSTRPFY |
| 3 | COTRTUPC.cbl* | 13 | CSUTLDWY, CVCRD01Y, COTTL01Y, COTRTUP, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, COCOM01Y, CSSETATY, CSSTRPFY + 2 |
| 4 | COACTVWC.cbl | 11 | COCOM01Y, COACTVW, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y |
| 5 | COTRN02C.cbl | 11 | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, DFHAID, DFHBMSCA + 1 |
| 6 | COTRTLIC.cbl* | 10 | CVCRD01Y, COCOM01Y, DFHBMSCA, DFHAID, COTTL01Y, COTRTLI, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y |
| 7 | COCRDLIC.cbl | 9 | COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVCRD01Y, CVACT02Y, CVACT03Y |
| 8 | COCRDSLC.cbl | 9 | COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y |
| 9 | COBIL00C.cbl | 9 | COCOM01Y, COBIL00, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y, CSUSR01Y |
| 10 | CBEXPORT.cbl | 7 | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT + 1 |

---

## 3. Top 10 Programs by Number of I/O Operations

Counting distinct VSAM CICS operations + batch file READ/WRITE/REWRITE/DELETE operations.

| Rank | Program | I/O Ops | Details |
|------|---------|---------|---------|
| 1 | COACTUPC.cbl | 12+ | READ/REWRITE on ACCTDAT, CARDXREF, CUSTDAT, CARDDAT; STARTBR/READNEXT/ENDBR on multiple files |
| 2 | COTRN02C.cbl | 8 | READ CXACAIX, CCXREF; STARTBR/READPREV/ENDBR TRANSACT; WRITE TRANSACT |
| 3 | CBTRN02C.cbl | 7 | READ DALYTRAN, XREFFILE, ACCTFILE, TCATBALF; WRITE TRANSACT, DALYREJS; REWRITE ACCTFILE |
| 4 | CBTRN01C.cbl | 7 | READ DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE; WRITE TRANFILE |
| 5 | CBEXPORT.cbl | 6 | READ CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE; WRITE EXPFILE |
| 6 | CBIMPORT.cbl | 7 | READ EXPFILE; WRITE CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT |
| 7 | CBACT04C.cbl | 6 | READ ACCTFILE, XREFFILE, DISCGRP, TCATBALF, TRANSACT; WRITE TRANFILE |
| 8 | COBIL00C.cbl | 5 | READ ACCTDAT, CARDXREF; WRITE TRANSACT; REWRITE ACCTDAT |
| 9 | COCRDUPC.cbl | 5 | READ/REWRITE CARDDAT; READ CARDXREF, ACCTDAT |
| 10 | CBSTM03A.CBL | 4 | READ STMTFILE; WRITE HTMLFILE; CALL CBSTM03B (4 more files) |

---

## 4. Top 10 Programs by Business Logic Density

Measured by EVALUATE + IF counts and nesting depth.

| Rank | Program | EVALUATE | IF | Total Logic Stmts | Max Nesting | Density (stmts/100 LOC) |
|------|---------|----------|----|--------------------|-------------|------------------------|
| 1 | COTRTLIC.cbl* | 32 | 185 | 217 | deep | 10.3 |
| 2 | COTRTUPC.cbl* | 26 | 108 | 134 | deep | 7.9 |
| 3 | CBACT04C.cbl | 0 | 86 | 86 | 4 | 13.2 |
| 4 | CBTRN01C.cbl | 0 | 66 | 66 | 3 | 13.3 |
| 5 | COTRN00C.cbl | 16 | 52 | 68 | 4 | 9.7 |
| 6 | COUSR00C.cbl | 16 | 50 | 66 | 4 | 9.5 |
| 7 | COACTUPC.cbl | 14 | 34 | 48 | 4 | 1.1 |
| 8 | CBTRN02C.cbl | 16 | 30 | 46 | 4 | 6.3 |
| 9 | CBEXPORT.cbl | 0 | 32 | 32 | 1 | 5.5 |
| 10 | COTRN02C.cbl | 26 | 28 | 54 | 4 | 6.9 |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Counting: copybooks used + programs called/transferred to + VSAM files accessed.

| Rank | Program | Copybooks | Calls Out | Called By | VSAM Files | Total Deps |
|------|---------|-----------|-----------|-----------|------------|------------|
| 1 | COACTUPC.cbl | 14 | 1 (CSUTLDTC) | COMEN01C (XCTL) | 4 | 19 |
| 2 | COCRDUPC.cbl | 13 | 1 (CSUTLDTC) | COCRDLIC (XCTL) | 3 | 17 |
| 3 | COACTVWC.cbl | 11 | 0 | COMEN01C (XCTL) | 4 | 15 |
| 4 | COTRN02C.cbl | 11 | 1 (CSUTLDTC) | COTRN00C (XCTL) | 3 | 15 |
| 5 | COTRTUPC.cbl* | 13 | 0 | COTRTLIC (XCTL) | 1 (DB2) | 14 |
| 6 | COTRTLIC.cbl* | 10 | 0 | COMEN01C (XCTL) | 1 (DB2) | 11 |
| 7 | CBTRN02C.cbl | 6 | 1 (CEE3ABD) | POSTTRAN.jcl | 5 | 12 |
| 8 | CBTRN01C.cbl | 7 | 1 (CEE3ABD) | — | 6 | 14 |
| 9 | CBEXPORT.cbl | 7 | 1 (CEE3ABD) | CBEXPORT.jcl | 6 | 14 |
| 10 | COBIL00C.cbl | 9 | 0 | COMEN01C (XCTL) | 3 | 12 |

---

## 6. Composite Hotspot Score

Weighted composite: LOC (25%) + Copybooks (20%) + I/O Ops (20%) + Logic Density (20%) + Dependencies (15%).

| Rank | Program | LOC Score | Copy Score | I/O Score | Logic Score | Dep Score | **Composite** |
|------|---------|-----------|------------|-----------|-------------|-----------|---------------|
| **1** | **COACTUPC.cbl** | 10.0 | 10.0 | 10.0 | 4.8 | 10.0 | **9.0** |
| **2** | **COCRDUPC.cbl** | 3.7 | 9.3 | 4.2 | 3.5 | 8.9 | **5.7** |
| **3** | **CBTRN02C.cbl** | 1.7 | 4.3 | 5.8 | 5.5 | 6.3 | **4.6** |
| **4** | **COTRN02C.cbl** | 1.9 | 7.9 | 6.7 | 6.1 | 7.9 | **5.9** |
| **5** | **COTRTLIC.cbl*** | 5.0 | 7.1 | 3.3 | 10.0 | 5.8 | **6.2** |
| **6** | **CBACT04C.cbl** | 1.5 | 4.3 | 5.0 | 7.0 | 5.3 | **4.5** |
| **7** | **COACTVWC.cbl** | 2.2 | 7.9 | 3.3 | 3.0 | 7.9 | **4.7** |
| **8** | **CBEXPORT.cbl** | 1.4 | 5.0 | 5.0 | 3.5 | 7.4 | **4.3** |
| **9** | **CBSTM03A.CBL** | 2.2 | 3.6 | 3.3 | 3.5 | 4.7 | **3.4** |
| **10** | **COBIL00C.cbl** | 1.4 | 6.4 | 4.2 | 2.5 | 6.3 | **4.0** |

---

## 7. Modernization Priority Recommendations

### Tier 1 — Modernize First (Highest Impact)

#### 1. **COACTUPC.cbl** — Account Update Screen
- **Why first:** Largest program (4,236 LOC), highest dependency count (14 copybooks, 4 VSAM files), most complex I/O. This is the most feature-rich screen in the application — account updates touch every master file.
- **Risk:** High — central to account management workflow. Regression testing critical.
- **Approach:** Decompose into microservices: Account Read Service, Account Write Service, Customer Lookup Service, Card Lookup Service. Replace BMS screen with REST API + web UI.
- **Estimated effort:** High (most complex single program).

#### 2. **CBTRN02C.cbl** — Daily Transaction Posting
- **Why early:** Core batch processing engine. Posts all daily transactions, updates balances, writes rejects. Sits at the heart of the daily batch pipeline.
- **Risk:** Medium-high — drives financial accuracy. Must maintain exact balance calculation logic.
- **Approach:** Convert to a transactional Java/Spring Batch service with DB transaction semantics replacing VSAM I/O. Implement idempotent processing.
- **Estimated effort:** Medium-high.

#### 3. **COTRN02C.cbl** — Transaction Add Screen
- **Why early:** Online transaction entry point. Writes to TRANSACT VSAM, reads card cross-references and account AIX files. Complex validation logic (26 EVALUATE, 28 IF).
- **Risk:** Medium — user-facing, must preserve validation rules.
- **Approach:** REST API with validation layer. Replace CICS VSAM calls with database operations.
- **Estimated effort:** Medium.

### Tier 2 — Modernize Next (High Value)

#### 4. **COCRDUPC.cbl** — Credit Card Update
- **Why:** Second most copybook-heavy program (13). Complex VSAM I/O across 3 files. Card management is a core business function.
- **Approach:** Card Management microservice with CRUD operations.

#### 5. **CBACT04C.cbl** — Interest Calculator
- **Why:** Core financial logic — computes interest using disclosure group rates. Reads 5 VSAM files. Business-critical accuracy requirement.
- **Approach:** Financial calculation engine as a stateless service. Critical to get the math right.

#### 6. **CBSTM03A.CBL + CBSTM03B.CBL** — Statement Generation
- **Why:** Two-program pipeline producing HTML statements. Reads 4 VSAM files via sub-program call. Good candidate for modern templating.
- **Approach:** Replace with a document generation service (e.g., PDF/HTML templating engine). Decouple from VSAM via database queries.

#### 7. **CBEXPORT.cbl / CBIMPORT.cbl** — Branch Migration
- **Why:** Data migration utilities touching all 5 master datasets. Good target for ETL modernization.
- **Approach:** Replace with modern ETL (AWS Glue, Spring Batch) reading from modernized database.

### Tier 3 — Modernize Later (Lower Complexity)

#### 8. **COCRDLIC.cbl / COCRDSLC.cbl** — Card List & Selection
- Moderate complexity browse screens. Will benefit from Card Management microservice (Tier 2).

#### 9. **COTRN00C.cbl / COTRN01C.cbl** — Transaction List & View
- Read-only screens. Simpler than Tier 1/2 programs. Implement as query APIs.

#### 10. **COUSR00C–03C** — User Management CRUD
- Self-contained user management (4 programs). Replace with standard IAM/auth service.

#### 11. **COMEN01C / COADM01C / COSGN00C** — Navigation & Auth
- Menu and sign-on programs. Replace with modern web app routing and authentication (OAuth2/OIDC).

### Tier 4 — Utility Programs (Migrate or Replace)

#### 12. **CSUTLDTC.cbl** — Date Utility
- Replace with `java.time` or equivalent. Called by 3 programs.

#### 13. **COBSWAIT.cbl** — Wait Utility
- Replace with scheduler-native wait/sleep.

#### 14. **CBACT01C–03C / CBCUS01C** — File Dump Utilities
- Diagnostic programs. May not need migration if VSAM is replaced.

---

## 8. Key Observations

1. **COACTUPC is the "god program"** — at 4,236 lines, it does account updates, card lookups, customer lookups, cross-reference reads, date validation, and screen rendering all in one program. This should be the primary decomposition target.

2. **The daily batch pipeline is the critical path** — CLOSEFIL → TRANBKP → POSTTRAN → WAITSTEP → OPENFIL. CBTRN02C (POSTTRAN) is the highest-risk batch program.

3. **4 copybooks are used by every online program** — COCOM01Y, CSDAT01Y, CSMSG01Y, COTTL01Y form the application framework. These become the data contracts in the modernized architecture.

4. **CVACT01Y and CVACT03Y are the most cross-cutting data structures** — referenced by 16 programs each. Account and cross-reference data models should be designed first in the target architecture.

5. **The authorization sub-app is architecturally distinct** — uses IMS/DB2/MQ instead of VSAM. Consider modernizing it independently as a separate microservice.

6. **CSLKPCDY.cpy (1,318 lines) is a hardcoded lookup table** — US states, ZIP codes, area codes. Replace with a reference data service or configuration table.

7. **No automated test suite exists** — all testing is manual. Modernization should include building a test harness before refactoring, using the existing EBCDIC test data in `app/data/` as seed data.

8. **Plain-text passwords** — CSUSR01Y stores passwords in PIC X(08). The modernized system must implement proper password hashing and authentication.
