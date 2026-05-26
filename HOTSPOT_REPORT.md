# CardDemo Hotspot Report

This report ranks CardDemo programs by multiple complexity dimensions and provides modernization recommendations.

---

## 1. Lines of Code

| Rank | Program | LOC | Classification |
|---|---|---|---|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) |
| 2 | COCRDUPC.cbl | 1,560 | Online (CICS) |
| 3 | COCRDLIC.cbl | 1,459 | Online (CICS) |
| 4 | COACTVWC.cbl | 941 | Online (CICS) |
| 5 | CBSTM03A.CBL | 924 | Batch |
| 6 | COCRDSLC.cbl | 887 | Online (CICS) |
| 7 | COTRN02C.cbl | 783 | Online (CICS) |
| 8 | CBTRN02C.cbl | 731 | Batch |
| 9 | COTRN00C.cbl | 699 | Online (CICS) |
| 10 | COUSR00C.cbl | 695 | Online (CICS) |

COACTUPC is the largest program by far — nearly 3× the second-largest. It handles both account and customer updates with extensive field-level validation.

---

## 2. COPY Statement Count (Copybook References)

| Rank | Program | COPY Statements | Unique Copybooks |
|---|---|---|---|
| 1 | COACTUPC.cbl | 58 | ~15 (COCOM01Y, COACTUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, CVCRD01Y, CSLKPCDY, CSUTLDWY, CSSTRPFY, CSSETATY) |
| 2 | COACTVWC.cbl | 16 | 12 |
| 3 | COCRDSLC.cbl | 16 | 11 |
| 4 | COCRDUPC.cbl | 16 | 11 |
| 5 | COCRDLIC.cbl | 14 | 9 |
| 6 | COBIL00C.cbl | 11 | 8 |
| 7 | COTRN02C.cbl | 11 | 8 |
| 8 | COUSR00C.cbl | 9 | 7 |
| 9 | CORPT00C.cbl | 9 | 7 |
| 10 | CBTRN02C.cbl | 6 | 5 |

COACTUPC includes the 1,318-line CSLKPCDY lookup table (North America phone area codes, state codes, ZIP prefixes) which inflates both COPY count and effective code size.

---

## 3. I/O Operation Density

Counts include EXEC CICS READ/WRITE/REWRITE/STARTBR/READNEXT/READPREV/ENDBR, COBOL READ/WRITE/REWRITE, and CALL/PERFORM for file operations.

| Rank | Program | EXEC CICS | File READ/WRITE | PERFORM | Total I/O-Related |
|---|---|---|---|---|---|
| 1 | CBSTM03A.CBL | 0 | 101 (R:4, W:97) | 33 | 134 |
| 2 | CBTRN03C.cbl | 0 | 10 (R:9, W:1) | 73 | 83 |
| 3 | CBTRN02C.cbl | 0 | 11 (R:6, W:5) | 62 | 73 |
| 4 | CBACT04C.cbl | 0 | 9 (R:7, W:2) | 57 | 66 |
| 5 | COACTUPC.cbl | 17 | 6 (R:3, W:3) | 64 | 87 |
| 6 | COTRN02C.cbl | 11 | 0 | 61 | 72 |
| 7 | COCRDLIC.cbl | 36 | 4 | 34 | 74 |
| 8 | COACTVWC.cbl | 30 | 4 | 21 | 55 |
| 9 | COCRDSLC.cbl | 28 | 4 | 19 | 51 |
| 10 | COBIL00C.cbl | 13 | 0 | 38 | 51 |

CBSTM03A has 97 WRITE statements because it generates both plain-text and HTML statement output line-by-line.

---

## 4. Business Logic Density (EVALUATE + IF)

| Rank | Program | EVALUATE | IF | Total | Key Validation Logic |
|---|---|---|---|---|---|
| 1 | COACTUPC.cbl | 20 | 174 | 194 | SSN format, phone area code (CSLKPCDY), date of birth, FICO score, account status, credit limits, expiration dates |
| 2 | COCRDUPC.cbl | 16 | 77 | 93 | Card name, status, expiry; lock/unlock/rewrite pattern |
| 3 | COCRDLIC.cbl | 18 | 70 | 88 | Pagination, card selection, forward/backward browse |
| 4 | CBTRN02C.cbl | 0 | 48 | 48 | Overlimit check, card expiration, xref validation, TCATBAL create/update |
| 5 | COACTVWC.cbl | 10 | 32 | 42 | Account/customer display, cross-reference lookup |
| 6 | CBTRN03C.cbl | 4 | 38 | 42 | Date range filtering, page/account/grand totals |
| 7 | CBACT04C.cbl | 0 | 43 | 43 | Interest rate lookup, category balance processing, account balance update |
| 8 | COCRDSLC.cbl | 8 | 37 | 45 | Card detail display, alternate index lookup |
| 9 | COTRN02C.cbl | 26 | 14 | 40 | Transaction field validation, date validation (calls CSUTLDTC) |
| 10 | COUSR00C.cbl | 16 | 25 | 41 | User list pagination, VSAM browse |

---

## 5. Inter-Program Dependencies

| Rank | Program | Dependents / Connections | Role |
|---|---|---|---|
| 1 | COMEN01C | 11 programs route through it | Navigation hub for all regular user functions |
| 2 | COSGN00C | Entry point → COADM01C or COMEN01C | Application entry point, authentication gateway |
| 3 | COADM01C | 6 programs route through it | Admin function hub |
| 4 | COCRDLIC | Calls COCRDSLC (view) and COCRDUPC (update) | Card management entry point |
| 5 | CSUTLDTC | Called by COTRN02C and CORPT00C | Shared date validation utility |
| 6 | COACTVWC | Cross-navigates to COCRDLIC, COCRDSLC, COCRDUPC | Account view with card navigation |
| 7 | COACTUPC | Cross-navigates to COCRDUPC, COCRDLIC, COCRDSLC | Account update with card navigation |
| 8 | COUSR00C | Routes to COUSR02C (update) and COUSR03C (delete) | User list management hub |
| 9 | CBSTM03A | Calls CBSTM03B (subroutine) | Statement generation pipeline |
| 10 | COTRN00C | Routes to COTRN01C (view detail) | Transaction list entry point |

---

## 6. Composite Complexity Score

Weighted composite: LOC (30%) + COPY count (15%) + I/O density (20%) + business logic (25%) + dependencies (10%).

| Rank | Program | LOC | COPY | I/O | Logic | Deps | Composite |
|---|---|---|---|---|---|---|---|
| 1 | **COACTUPC** | 4,236 | 58 | 87 | 194 | High | **Highest** |
| 2 | **COCRDUPC** | 1,560 | 16 | 51 | 93 | Medium | High |
| 3 | **COCRDLIC** | 1,459 | 14 | 74 | 88 | Medium | High |
| 4 | **CBTRN02C** | 731 | 6 | 73 | 48 | Low | Medium-High |
| 5 | **CBSTM03A** | 924 | 5 | 134 | 24 | Low | Medium-High |
| 6 | **COACTVWC** | 941 | 16 | 55 | 42 | Medium | Medium |
| 7 | **COTRN02C** | 783 | 11 | 72 | 40 | Low | Medium |
| 8 | **CBTRN03C** | 649 | 6 | 83 | 42 | Low | Medium |
| 9 | **CBACT04C** | 652 | 6 | 66 | 43 | Low | Medium |
| 10 | **COCRDSLC** | 887 | 16 | 51 | 45 | Low | Medium |

---

## 7. Technical Complexity Notes

### CBSTM03A — Statement Generator
- **ALTER / GO TO**: Uses the obsolete ALTER statement to modify GO TO targets at runtime, making control flow non-deterministic from static analysis
- **PSA / TCB / TIOT addressing**: Directly addresses z/OS control blocks (Prefixed Save Area, Task Control Block, Task Input/Output Table) to extract the JCL job name at runtime
- **2D arrays**: Uses two-dimensional tables for statement formatting
- **Subroutine pattern**: All file I/O delegated to CBSTM03B via CALL, creating a tightly coupled pair
- **Modernization risk**: ALTER statements and control block addressing require special handling — most automated COBOL-to-Java tools cannot convert these patterns

### COACTUPC — Account Update
- **Dual-entity monolith**: Single program handles both account and customer updates (should be split into AccountService + CustomerService)
- **113 COPY references**: Highest copybook density in the application, including the 1,318-line CSLKPCDY phone area code table
- **Deep validation nesting**: SSN, phone numbers (area code lookup), dates (via CSUTLDWY), FICO scores, account status, credit limits — all validated inline
- **Lock/rewrite pattern**: Uses EXEC CICS READ UPDATE / REWRITE for pessimistic locking

### CBTRN02C (Batch) — Post Transactions
- **Core pipeline program**: First step in the daily batch pipeline; its output feeds CBACT04C and CBTRN03C
- **TCATBAL management**: Creates new TRAN-CAT-BAL records or updates existing ones, which are critical for interest calculation
- **Reject handling**: Writes failed transactions to DALYREJS with reason codes
- **Account balance updates**: Modifies ACCT-CURR-BAL, ACCT-CURR-CYC-CREDIT, ACCT-CURR-CYC-DEBIT

### CBACT04C — Interest Calculator
- **Financial computation**: Core interest calculation reading 5 files simultaneously
- **Self-contained logic**: Reads TCATBALF category balances, looks up interest rates from DISCGRP, computes interest, writes interest transactions, updates account balances
- **Good microservice candidate**: Relatively isolated business function with well-defined inputs and outputs

---

## 8. Modernization Recommendations

Recommended modernization order, from highest priority to lowest:

### Priority 1 — COACTUPC (Account Update)
**Why first:** Highest complexity by every metric (4,236 LOC, 58 COPY statements, 194 business logic branches). Contains the densest concentration of business rules in the application. Handles both account and customer entities — extracting and testing these rules early de-risks the entire migration.  
**Strategy:** Split into AccountUpdateService and CustomerUpdateService. Extract validation rules (SSN, phone, date, FICO) into a shared ValidationService. Replace CSLKPCDY embedded lookup with database-driven reference data.

### Priority 2 — CBTRN02C (Batch Post Transactions)
**Why second:** Core of the daily batch pipeline. Contains critical business rules for transaction validation (overlimit, expiration, cross-reference lookup) and drives account balance updates.  
**Strategy:** Extract as TransactionPostingService. The overlimit and expiration validation rules are high-value business logic that must be precisely preserved. TCATBAL management becomes a separate CategoryBalanceService.

### Priority 3 — COCRDUPC (Card Update)
**Why third:** Second-largest online program (1,560 LOC) with complex lock/update/rewrite pattern against VSAM.  
**Strategy:** Extract as CardUpdateService. The pessimistic locking pattern maps to database-level row locks or optimistic concurrency control.

### Priority 4 — CBACT04C (Interest Calculator)
**Why fourth:** Core financial computation with well-defined inputs (5 files) and outputs (interest transactions + updated balances). Self-contained enough to extract as an independent microservice.  
**Strategy:** Extract as InterestCalculationService. The disclosure group rate lookup becomes a configuration-driven rate table. Monthly batch execution maps to a scheduled job.

### Priority 5 — COCRDLIC (Card List)
**Why fifth:** Complex CICS browse/pagination logic (1,459 LOC) but relatively self-contained.  
**Strategy:** Replace VSAM STARTBR/READNEXT/READPREV with paginated database queries. The pagination state management maps to cursor-based pagination in a REST API.

### Priority 6 — CBTRN03C (Transaction Report)
**Why sixth:** Report generation with 6 input files and date filtering. High I/O but straightforward business logic.  
**Strategy:** Extract as ReportGenerationService. Replace file-based report output with a reporting framework (e.g., JasperReports) or API-driven report generation.

### Priority 7 — COTRN02C (Online Add Transaction)
**Why seventh:** Calls CSUTLDTC for date validation, writes to TRANSACT, validates input fields.  
**Strategy:** Extract as TransactionEntryService. Date validation becomes a shared utility. CSUTLDTC (CEEDAYS wrapper) maps to java.time validation.

### Priority 8 — CBSTM03A (Statement Generator)
**Why eighth:** Technically complex due to ALTER/GO TO, PSA/TCB addressing, and 2D arrays. These patterns require special handling that most automated tools cannot convert.  
**Strategy:** Rewrite rather than convert. The statement generation logic maps to a template engine (Thymeleaf, FreeMarker). The PSA/TCB addressing for job name extraction has no Java equivalent and should be replaced with configuration or environment variables. The CBSTM03B subroutine coupling dissolves when file I/O is replaced with database access.

### Priority 9 — COSGN00C (Signon)
**Why ninth:** Authentication logic — simple but foundational. Must be modernized to support the new security model.  
**Strategy:** Replace with Spring Security or equivalent authentication framework. The USRSEC VSAM file maps to a user database table. Password storage should move from plaintext to bcrypt/scrypt.

### Priority 10 — COMEN01C (Main Menu)
**Why tenth:** Navigation hub — relatively simple XCTL routing but needs to be rearchitected for web-based navigation.  
**Strategy:** Replace with a frontend router (React Router, Angular Router). The menu option definitions in COMEN02Y become API-driven navigation configuration.

### Batch Pipeline Note
CBTRN02C → CBACT04C → CBTRN03C should be modernized as a unit since they share the transaction lifecycle. The sequential file-based data flow between them maps to a message queue or event-driven pipeline (e.g., Spring Batch with step chaining, or Kafka topics for inter-service communication).
