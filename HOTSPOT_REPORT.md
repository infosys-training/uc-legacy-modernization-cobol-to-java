# Hotspot Report — CardDemo COBOL Estate

## Overview

This report ranks programs by complexity metrics to identify modernization priorities. Metrics include: lines of code (LOC), number of copybooks referenced, I/O operation count, business logic density (IF/EVALUATE nesting depth), and inter-program dependencies.

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification | Purpose |
|------|---------|-----|---------------|---------|
| 1 | COACTUPC.cbl | 4,237 | Online/CICS | Account Update |
| 2 | COTRTLIC.cbl | 2,099 | Online/CICS+DB2 | Transaction Type List |
| 3 | COTRTUPC.cbl | 1,703 | Online/CICS+DB2 | Transaction Type Update |
| 4 | COCRDUPC.cbl | 1,561 | Online/CICS | Credit Card Update |
| 5 | COCRDLIC.cbl | 1,460 | Online/CICS | Credit Card List |
| 6 | COPAUS0C.cbl | 1,033 | Online/CICS+IMS | Auth Summary View |
| 7 | COPAUA0C.cbl | 1,027 | Online/CICS+MQ | Auth Decision |
| 8 | COACTVWC.cbl | 942 | Online/CICS | Account View |
| 9 | CBSTM03A.CBL | 925 | Batch | Statement Generation |
| 10 | COCRDSLC.cbl | 888 | Online/CICS | Credit Card Detail |

---

## 2. Top 10 Programs by Copybooks Referenced

| Rank | Program | # Copybooks | Key Copybooks |
|------|---------|------------|---------------|
| 1 | COACTUPC.cbl | 18 | COACTUP, COCOM01Y, CSUTLDWY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, + 9 more |
| 2 | COACTVWC.cbl | 15 | COACTVW, COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y, + 8 more |
| 3 | COCRDSLC.cbl | 15 | COCRDSL, COCOM01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, DFHAID, DFHBMSCA, + 8 more |
| 4 | COCRDUPC.cbl | 15 | COCRDUP, COCOM01Y, CVACT02Y, CVCRD01Y, CVCUS01Y, + 10 more |
| 5 | COPAUA0C.cbl | 14 | CCPAURQY, CCPAURLY, CIPAUSMY, CMQV, CVACT01Y, CVACT03Y, CVCUS01Y, + 7 MQ copybooks |
| 6 | COPAUS0C.cbl | 14 | CIPAUSMY, CIPAUDTY, COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, + 7 more |
| 7 | COCRDLIC.cbl | 13 | COCRDLI, COCOM01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA, + 7 more |
| 8 | COTRTUPC.cbl | 13 | COTRTUP, COCOM01Y, CSUTLDWY, CVCRD01Y, DFHAID, DFHBMSCA, + 7 more |
| 9 | COTRTLIC.cbl | 11 | COTRTLI, COCOM01Y, CVACT02Y, CVCRD01Y, DFHAID, DFHBMSCA, + 5 more |
| 10 | COBIL00C.cbl | 10 | COBIL00, COCOM01Y, CVACT01Y, CVACT03Y, CVTRA05Y, DFHAID, DFHBMSCA, + 3 more |

---

## 3. Top 10 Programs by I/O Operations Count

| Rank | Program | # I/O Ops | I/O Types |
|------|---------|----------|-----------|
| 1 | CBSTM03A.CBL | 151 | WRITE (statement/HTML lines), OPEN, CLOSE, READ (via CBSTM03B) |
| 2 | COACTUPC.cbl | 102 | CICS READ, REWRITE (Account, Xref, Customer, Card) |
| 3 | COTRTLIC.cbl | 89 | EXEC SQL OPEN/FETCH/CLOSE CURSOR (DB2 pagination) |
| 4 | CBTRN03C.cbl | 72 | READ (TRANFILE, XREF, TRANTYPE, TRANCATG), WRITE (report) |
| 5 | COTRTUPC.cbl | 60 | EXEC SQL SELECT/UPDATE/INSERT, CICS SEND/RECEIVE |
| 6 | CBTRN01C.cbl | 53 | READ (DALYTRAN, XREF, ACCT), OPEN/CLOSE (6 files) |
| 7 | CBTRN02C.cbl | 53 | READ, WRITE, REWRITE (DALYTRAN, TRANSACT, ACCT, TCATBALF, REJS) |
| 8 | CBACT04C.cbl | 42 | READ (TCATBALF, XREF, ACCT, DISCGRP), WRITE, REWRITE |
| 9 | CBEXPORT.cbl | 41 | READ (5 input files), WRITE (export file) |
| 10 | CBACT01C.cbl | 39 | READ (ACCTFILE), WRITE (3 output files) |

---

## 4. Top 10 Programs by Business Logic Density (Nesting Depth)

| Rank | Program | Max IF/EVALUATE Nesting | Complexity Indicators |
|------|---------|------------------------|----------------------|
| 1 | COACTUPC.cbl | 4+ | Extensive field validation (dates, numerics, limits), state machine for screen flow |
| 2 | CBACT04C.cbl | 4 | Interest calculation with multiple rate lookups, account type branching |
| 3 | CBTRN03C.cbl | 4 | Report formatting with conditional page breaks, type/category lookups |
| 4 | COBIL00C.cbl | 4 | Payment validation, balance checks, partial/full payment logic |
| 5 | COCRDLIC.cbl | 4 | Pagination logic, forward/backward browsing, selection handling |
| 6 | COTRN00C.cbl | 4 | Transaction list pagination with date filtering |
| 7 | COTRN01C.cbl | 4 | Transaction display with type lookup |
| 8 | COTRN02C.cbl | 4 | Transaction add with account/card validation, amount checks |
| 9 | COUSR00C.cbl | 4 | User list pagination with type filtering |
| 10 | COUSR02C.cbl | 4 | User update with type change validation |

---

## 5. Top 10 Programs by Inter-Program Dependencies

| Rank | Program | Dependency Score | Rationale |
|------|---------|-----------------|-----------|
| 1 | CBTRN02C | 9 | Reads 4 files, writes 3, rewrites 2; central to daily posting |
| 2 | CBACT04C | 8 | Reads 4 reference files, rewrites accounts, writes transactions |
| 3 | CBSTM03A | 7 | Calls CBSTM03B; reads via subroutine from 4 files; writes 2 outputs |
| 4 | CBEXPORT | 7 | Reads 5 entity files, writes 1 consolidated export |
| 5 | CBTRN03C | 7 | Reads 5 files (trans, xref, types, categories, date parms); writes report |
| 6 | COPAUA0C | 7 | MQ + CICS READ of 3 VSAM files; external MQ dependencies |
| 7 | CBIMPORT | 7 | Reads 1 file, writes to 6 output files |
| 8 | COACTUPC | 6 | CICS reads/rewrites across Account, Xref, Customer, Card |
| 9 | CBTRN01C | 6 | Reads from 6 files for transaction validation |
| 10 | COACTVWC | 5 | CICS reads from Account, Card, Customer, Xref |

---

## 6. Composite Hotspot Score

Combining all metrics (normalized 0-10 scale, weighted equally):

| Rank | Program | LOC Score | Copybook Score | I/O Score | Nesting Score | Dep Score | **Total** |
|------|---------|-----------|---------------|-----------|---------------|-----------|-----------|
| 1 | **COACTUPC.cbl** | 10.0 | 10.0 | 6.7 | 10.0 | 6.7 | **43.4** |
| 2 | **CBTRN02C.cbl** | 1.7 | 2.8 | 3.5 | 7.5 | 10.0 | **25.5** |
| 3 | **CBSTM03A.CBL** | 2.2 | 2.2 | 10.0 | 7.5 | 7.8 | **29.7** |
| 4 | **COTRTLIC.cbl** | 5.0 | 6.1 | 5.9 | 5.0 | 3.3 | **25.3** |
| 5 | **CBACT04C.cbl** | 1.5 | 2.8 | 2.8 | 10.0 | 8.9 | **26.0** |
| 6 | **COPAUA0C.cbl** | 2.4 | 7.8 | 2.4 | 7.5 | 7.8 | **27.9** |
| 7 | **COCRDUPC.cbl** | 3.7 | 8.3 | 1.2 | 7.5 | 3.3 | **24.0** |
| 8 | **CBTRN03C.cbl** | 1.5 | 2.8 | 4.8 | 10.0 | 7.8 | **26.9** |
| 9 | **COTRTUPC.cbl** | 4.0 | 7.2 | 4.0 | 5.0 | 3.3 | **23.5** |
| 10 | **COCRDLIC.cbl** | 3.4 | 7.2 | 2.2 | 10.0 | 3.3 | **26.1** |

---

## 7. Modernization Recommendations

### Priority 1 — Modernize First (Highest Business Value + Complexity)

| Program | Reason | Recommended Approach |
|---------|--------|---------------------|
| **COACTUPC.cbl** (4,237 LOC) | Largest program, highest complexity, 18 copybooks, extensive validation logic. Core business function (account updates). | Decompose into microservices: Account Validation Service + Account Update Service. Extract validation rules to a rules engine. |
| **CBTRN02C.cbl** (731 LOC) | Central batch pipeline — posts ALL daily transactions, updates balances, generates rejects. Failure here blocks the entire daily cycle. | Convert to event-driven transaction processor. Implement as stateless service with database transactions. |
| **CBACT04C.cbl** (652 LOC) | Interest calculation — complex financial logic with multi-table lookups. High regulatory/audit importance. | Extract to dedicated Interest Calculation Service with parameterized rules. Critical for correctness testing. |

### Priority 2 — High Value Batch Modernization

| Program | Reason | Recommended Approach |
|---------|--------|---------------------|
| **CBSTM03A/B** (925+231 LOC) | Statement generation — customer-facing output, high I/O. Generates both text and HTML. | Replace with template-based document generation service. HTML output already hints at web readiness. |
| **CBTRN03C.cbl** (650 LOC) | Transaction reporting — complex formatting, multiple reference lookups. | Convert to reporting microservice using modern report frameworks (JasperReports, etc.). |
| **CBEXPORT/CBIMPORT** (583+487 LOC) | Data migration — reads/writes all entity types. Well-structured, clear record type routing. | Convert to ETL pipeline or data integration service. Good candidate for early migration due to clear interfaces. |

### Priority 3 — Online Program Migration

| Program | Reason | Recommended Approach |
|---------|--------|---------------------|
| **COSGN00C → COMEN01C → COADM01C** | Navigation backbone — sign-on + menus control all program routing. | Replace with Spring Security + REST API gateway. Menus become frontend routing. |
| **COCRDLIC/COCRDSLC/COCRDUPC** | Credit card CRUD — paginated list, detail view, update. Pattern repeats for all entities. | Implement as standard CRUD REST APIs with pagination. Ideal for code generation from copybook schemas. |
| **COPAUA0C** (1,027 LOC) | Authorization decision engine with MQ integration. Critical real-time path. | Convert to event-driven authorization microservice. Replace MQ with modern messaging (Kafka/SQS). |

### Priority 4 — Utility & Reference

| Program | Reason |
|---------|--------|
| CSUTLDTC, COBSWAIT, CBACT01C-03C, CBCUS01C | Simple utilities and file readers. Low complexity, minimal business logic. Modernize last or retire. |

---

## 8. Key Observations

1. **Shared State via VSAM Files**: All batch programs share state through VSAM files, creating implicit coupling. Modernization should introduce explicit APIs or database transactions.

2. **COMMAREA Pattern**: All CICS programs pass context via COCOM01Y (COMMAREA). This maps naturally to session state or JWT tokens in a modern architecture.

3. **Copybook = Data Contract**: Copybooks serve as interface definitions. These directly translate to DTOs/POJOs in Java, making them excellent starting points for API schema generation.

4. **Batch Window Dependency**: The Close→Process→Open pattern (CLOSEFIL→batch jobs→OPENFIL) indicates a tight batch window. Modernization should eliminate this constraint through online/real-time processing.

5. **DB2 Sub-App is Already Semi-Modern**: The `app-transaction-type-db2` programs already use SQL cursors and CRUD patterns that map directly to JPA/Spring Data repositories.

6. **IMS Dependency is Isolated**: Authorization IMS programs are contained in their own sub-app with clear boundaries (MQ interface). This can be modernized independently.
