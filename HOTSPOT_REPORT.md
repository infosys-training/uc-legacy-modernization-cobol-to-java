# CardDemo Hotspot Report

Complexity analysis and modernization priority ranking for the CardDemo COBOL estate. Programs are ranked across multiple dimensions to identify the highest-risk, highest-value modernization targets.

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | Lines | Type | Purpose |
|:-----|:--------|------:|:-----|:--------|
| 1 | COACTUPC.cbl | 4,236 | Online (CAUP) | Account update with full field validation |
| 2 | COCRDUPC.cbl | 1,560 | Online (CCUP) | Card update with lock/rewrite pattern |
| 3 | COCRDLIC.cbl | 1,459 | Online (CCLI) | Card list with 7-row pagination |
| 4 | COACTVWC.cbl | 941 | Online (CAVW) | Account view (read-only) |
| 5 | CBSTM03A.CBL | 924 | Batch | Statement generator (text + HTML) |
| 6 | COCRDSLC.cbl | 887 | Online (CCDL) | Card detail view |
| 7 | COTRN02C.cbl | 783 | Online (CT02) | Add transaction with date validation |
| 8 | CBTRN02C.cbl | 731 | Batch | Post daily transactions with validation |
| 9 | COTRN00C.cbl | 699 | Online (CT00) | Transaction list with 10-row pagination |
| 10 | COUSR00C.cbl | 695 | Online (CU00) | User list with 10-row pagination |

**Notable runners-up:** CBACT04C (652), CORPT00C (649), CBTRN03C (649), CBEXPORT (582), COBIL00C (572).

---

## 2. Top 10 Programs by Copybook References

COPY statement count (includes COPY ... REPLACING expansions):

| Rank | Program | COPY Count | Key Copybooks |
|:-----|:--------|:----------:|:--------------|
| 1 | COACTUPC.cbl | 56 | CSSETATY (21x via REPLACING), CSLKPCDY, CSUTLDWY, CSUTLDPY, CVACT01Y, CVCUS01Y, CVACT03Y |
| 2 | COACTVWC.cbl | 15 | CVCRD01Y, CVACT01Y, CVACT02Y, CVCUS01Y, CVACT03Y, CSSTRPFY |
| 3 | COCRDUPC.cbl | 15 | CVCRD01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 4 | COCRDSLC.cbl | 15 | CVCRD01Y, CVACT02Y, CVCUS01Y, CSSTRPFY |
| 5 | COCRDLIC.cbl | 13 | CVCRD01Y, CVACT02Y, CSSTRPFY |
| 6 | COTRN02C.cbl | 10 | CVTRA05Y, CVACT01Y, CVACT03Y |
| 7 | COUSR00C.cbl | 8 | CSUSR01Y |
| 8 | CORPT00C.cbl | 8 | CVTRA05Y |
| 9 | CBTRN02C.cbl | 5 | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y |
| 10 | CBTRN03C.cbl | 5 | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |

COACTUPC dominates due to 21 inclusions of CSSETATY (template copybook with REPLACING for each screen field).

---

## 3. Top 10 Programs by I/O Operations

Combined count of CICS file commands (READ, WRITE, REWRITE, DELETE, STARTBR, READNEXT, READPREV, ENDBR) and batch file operations (READ, WRITE, REWRITE, OPEN, CLOSE):

| Rank | Program | I/O Ops | Type | Key Operations |
|:-----|:--------|:-------:|:-----|:---------------|
| 1 | CBSTM03A.CBL | 117 | Batch | 13 CALL CBSTM03B (each does multi-file READ), plus direct I/O |
| 2 | CBTRN02C.cbl | 23 | Batch | READ DALYTRAN, READ/REWRITE ACCTFILE, READ/REWRITE TCATBALF, WRITE TRANFILE/DALYREJS |
| 3 | CBTRN03C.cbl | 22 | Batch | READ TRANFILE, READ CARDXREF/TRANTYPE/TRANCATG, WRITE TRANREPT |
| 4 | CBACT04C.cbl | 19 | Batch | READ TCATBALF/XREFFILE/DISCGRP, REWRITE ACCTFILE, WRITE TRANSACT |
| 5 | COACTUPC.cbl | 23 | Online | CICS READ/REWRITE on ACCTFILE, CUSTFILE, CARDFILE, XREFFILE |
| 6 | COCRDLIC.cbl | 11 | Online | CICS STARTBR/READNEXT/READPREV/ENDBR on CARDFILE (pagination) |
| 7 | COTRN02C.cbl | 6 | Online | CICS READ XREFFILE, READ/WRITE TRANSACT, REWRITE ACCTFILE |
| 8 | COCRDUPC.cbl | 7 | Online | CICS READ/REWRITE on CARDFILE, CUSTFILE |
| 9 | COUSR00C.cbl | 4 | Online | CICS STARTBR/READNEXT/ENDBR on USRSEC (pagination) |
| 10 | COACTVWC.cbl | 7 | Online | CICS READ on ACCTFILE, CARDFILE, CUSTFILE, XREFFILE |

---

## 4. Top 10 Programs by Business Logic Density

Ratio of procedural logic (EVALUATE, IF/ELSE, PERFORM, COMPUTE) to total lines, combined with number of distinct validation rules and business operations:

| Rank | Program | Logic Indicators | Business Rules |
|:-----|:--------|:-----------------|:---------------|
| 1 | COACTUPC.cbl | 56 COPY (incl. 21 CSSETATY field validations), leap-year checks, FICO range, phone area codes, state codes, zip prefix, date validation | SSN format, phone area code (NANPA), state code, zip-state correlation, FICO 300–850, date CCYYMMDD with leap year, credit limit > 0 |
| 2 | CBTRN02C.cbl | Card-to-account xref lookup, credit limit check, category balance update, rejection routing | Validate card exists in XREFFILE, validate account active, check transaction amount vs. credit limit, update ACCTFILE balance, update TCATBALF |
| 3 | CBACT04C.cbl | Interest rate lookup, compound interest calculation, multi-file join | Join TCATBALF → ACCTFILE via XREFFILE, lookup DISCGRP for rate, compute interest = balance × rate / 1200, post interest transaction |
| 4 | CBSTM03A.CBL | ALTER/GO TO control flow, multi-file merge, HTML generation, statement formatting | 4 ALTER statements, PSA/TCB addressing (low-level COBOL), text + HTML dual output, card-based transaction grouping |
| 5 | COCRDUPC.cbl | Lock/update/confirm workflow, field-level validation, CICS enqueing | CICS ENQ/DEQ for record locking, embossed name validation, expiration date validation, active status Y/N |
| 6 | CBTRN03C.cbl | Date-range filtering, multi-lookup joins, page/account/grand totals | Read DATEPARM for date range, join TRANTYPE + TRANCATG for descriptions, compute running totals per page and account |
| 7 | COTRN02C.cbl | Transaction creation with cross-reference validation, date validation via CSUTLDTC | Validate card via XREFFILE, validate transaction type/category, call CSUTLDTC for date validation, write to TRANSACT |
| 8 | COCRDLIC.cbl | 7-row forward/backward pagination with STARTBR/READNEXT/READPREV | Cursor-based pagination, track first/last keys for page navigation, handle boundary conditions |
| 9 | COBIL00C.cbl | Bill payment with balance update, transaction creation, timestamp generation | CICS ASKTIME/FORMATTIME, calculate payment amount, REWRITE ACCTFILE, WRITE TRANSACT, browse for latest transaction |
| 10 | CORPT00C.cbl | JCL generation and submission via CICS internal reader | Build JCL dynamically, validate date parameters via CSUTLDTC, WRITEQ TD to submit batch report job |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Count of distinct programs called or transferred to, plus programs that call this program:

| Rank | Program | Outbound | Inbound | Details |
|:-----|:--------|:--------:|:-------:|:--------|
| 1 | COMEN01C.cbl | 11 | 1 | XCTL to all 11 menu options; called from COSGN00C |
| 2 | COADM01C.cbl | 6 | 1 | XCTL to 6 admin options; called from COSGN00C |
| 3 | COSGN00C.cbl | 2 | 0 | XCTL to COADM01C or COMEN01C; entry point |
| 4 | CBSTM03A.CBL | 1 | 0 | CALL CBSTM03B (13 times); invoked by JCL |
| 5 | COCRDLIC.cbl | 2 | 1 | XCTL to COCRDSLC or COCRDUPC; called from COMEN01C |
| 6 | COTRN02C.cbl | 1 | 1 | CALL CSUTLDTC; called from COMEN01C |
| 7 | CORPT00C.cbl | 1 | 1 | CALL CSUTLDTC; called from COMEN01C |
| 8 | COTRN00C.cbl | 1 | 1 | XCTL to COTRN01C; called from COMEN01C |
| 9 | COPAUS0C.cbl | 1 | 1 | XCTL to COPAUS1C; called from COMEN01C |
| 10 | COACTUPC.cbl | 0 | 1 | No outbound; called from COMEN01C |

---

## 6. Composite Complexity Score

Weighted composite: LOC (30%) + Copybooks (20%) + I/O (20%) + Business Logic (20%) + Dependencies (10%).

| Rank | Program | LOC | Copybooks | I/O | Logic | Deps | Modernization Priority |
|:-----|:--------|----:|:---------:|:---:|:-----:|:----:|:----------------------:|
| 1 | **COACTUPC.cbl** | 4,236 | 56 | 23 | Very High | 1 out | **Critical** |
| 2 | **CBTRN02C.cbl** | 731 | 5 | 23 | High | 0 | **High** |
| 3 | **COCRDUPC.cbl** | 1,560 | 15 | 7 | High | 2 out | **High** |
| 4 | **CBACT04C.cbl** | 652 | 5 | 19 | High | 0 | **High** |
| 5 | **COCRDLIC.cbl** | 1,459 | 13 | 11 | Medium | 2 out | Medium |
| 6 | **CBTRN03C.cbl** | 649 | 5 | 22 | Medium | 0 | Medium |
| 7 | **COTRN02C.cbl** | 783 | 10 | 6 | Medium | 1 out | Medium |
| 8 | **CBSTM03A.CBL** | 924 | 4 | 117 | High | 1 out | Medium (deferred) |
| 9 | **COSGN00C.cbl** | 260 | 8 | 1 | Low | 2 out | Medium |
| 10 | **COMEN01C.cbl** | 308 | 9 | 0 | Low | 11 out | Medium |

---

## 7. Modernization Recommendations

### Priority 1 — Critical Path Programs

#### 1. COACTUPC (Account Update) — 4,236 lines
- **Why first:** Highest complexity by every metric. Contains 21 field-level validation rules spread across 56 COPY inclusions. Validates SSN, phone area codes (NANPA), state codes, zip prefixes, FICO scores (300–850), and dates with leap-year logic.
- **Strategy:** Decompose into `AccountService` (balance, limits, dates) and `CustomerService` (name, address, SSN, FICO). Extract CSSETATY/CSLKPCDY validation into a shared `ValidationService`.
- **Risk:** Highest coupling to four VSAM files and COMMAREA state machine.

#### 2. CBTRN02C (Post Daily Transactions) — 731 lines
- **Why:** Core of the daily batch pipeline. Critical validation rules (card xref lookup, credit limit check, category balance update) that must be preserved exactly.
- **Strategy:** Convert to a transactional service with clear input validation → processing → output steps. Preserve rejection routing logic.
- **Risk:** Updates ACCTFILE and TCATBALF in-place; must maintain atomicity.

#### 3. COCRDUPC (Card Update) — 1,560 lines
- **Why:** Complex lock/update/confirm workflow using CICS ENQ/DEQ for record-level locking.
- **Strategy:** Map to optimistic concurrency (version field) or pessimistic locking in the target platform.
- **Risk:** CICS enqueing has no direct equivalent in most modern frameworks.

### Priority 2 — High-Value Batch Programs

#### 4. CBACT04C (Interest Calculator) — 652 lines
- **Why:** Self-contained financial computation. Multi-file join (TCATBALF → XREFFILE → DISCGRP → ACCTFILE) with clear input/output boundaries.
- **Strategy:** Extract as a standalone interest calculation microservice. Well-suited for unit testing due to deterministic computation.
- **Risk:** Low — reads are idempotent, writes are append-only to TRANSACT.

#### 5. CBTRN03C (Transaction Report) — 649 lines
- **Why:** Report generation with 6 input files (TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM, TRANREPT). Complex date filtering and multi-level totals.
- **Strategy:** Convert to a reporting service using SQL joins instead of sequential file reads. Totals map naturally to GROUP BY with ROLLUP.
- **Risk:** Low — read-only; output is a flat report file.

#### 6. CBSTM03A (Statement Generator) — 924 lines, **deferred**
- **Why deferred:** Uses 4 ALTER statements (self-modifying GO TO targets) and low-level PSA/TCB addressing. These patterns are fragile and hard to test.
- **Strategy:** Rewrite from scratch using a template engine for HTML/text output. Do not attempt line-by-line conversion due to ALTER.
- **Risk:** High — ALTER makes control flow non-deterministic at code review time. Requires careful functional testing of all output paths.

### Priority 3 — Online Navigation and Utility Programs

#### 7. COCRDLIC (Card List) — 1,459 lines
- **Why:** Pagination logic (7-row STARTBR/READNEXT/READPREV/ENDBR cursor pattern) is a common CICS pattern that maps to paginated REST APIs.
- **Strategy:** Replace with offset/limit or cursor-based pagination in the target API layer.

#### 8. COTRN02C (Add Transaction, Online) — 783 lines
- **Why:** Transaction creation with cross-reference validation and date validation via CSUTLDTC CALL.
- **Strategy:** Convert to a transaction creation API endpoint. Date validation becomes a shared utility method.

#### 9. COSGN00C (Signon) — 260 lines
- **Why:** Authentication gateway — every user session starts here. Currently uses cleartext password comparison against USRSEC VSAM file.
- **Strategy:** Replace with standard authentication (OAuth2/JWT). Foundational — must be modernized early since all downstream programs depend on the COMMAREA user context it establishes.

#### 10. COMEN01C (Main Menu) — 308 lines
- **Why:** Navigation hub routing to 11 programs. Menu-driven dispatch via table-driven XCTL.
- **Strategy:** Replace with API gateway / frontend routing. The COADM02Y and COMEN02Y option tables map directly to route configurations.

### Recommended Modernization Sequence

```
Phase 1: Batch Pipeline (modernize as a unit)
  CBTRN02C → CBACT04C → CBTRN03C
  Rationale: These three programs form a sequential pipeline 
  (post → interest → report) sharing TRANSACT and ACCTFILE. 
  Modernizing together preserves data flow integrity.

Phase 2: Core Online Services
  COACTUPC → COCRDUPC → COCRDLIC
  Rationale: Account/card management — highest user-facing complexity.

Phase 3: Authentication & Navigation
  COSGN00C → COMEN01C → COADM01C
  Rationale: Foundational infrastructure — enables other programs.

Phase 4: Remaining Online Programs
  COTRN02C → COBIL00C → CORPT00C → remaining view/list programs

Phase 5: Statement Generator (rewrite)
  CBSTM03A (+ CBSTM03B)
  Rationale: Deferred due to ALTER statements. Full rewrite recommended.
```
