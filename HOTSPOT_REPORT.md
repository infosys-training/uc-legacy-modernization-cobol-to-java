# HOTSPOT REPORT — CardDemo COBOL Estate

## Overview

This report ranks the top 10 programs in the CardDemo estate by multiple complexity dimensions to identify the highest-risk candidates for modernization. Each program is scored across five metrics: lines of code, copybook count, I/O operations, business logic density, and inter-program dependencies.

---

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification | Description |
|------|---------|-----|---------------|-------------|
| 1 | **COACTUPC.cbl** | 4,236 | Online (CICS) | Account update with full field-level validation |
| 2 | **COTRTLIC.cbl** | 2,098 | Online (CICS + DB2) | Transaction type list with cursor-based DB2 pagination |
| 3 | **COTRTUPC.cbl** | 1,702 | Online (CICS + DB2) | Transaction type CRUD with DB2 |
| 4 | **COCRDUPC.cbl** | 1,560 | Online (CICS) | Credit card update |
| 5 | **COCRDLIC.cbl** | 1,459 | Online (CICS) | Credit card list with VSAM browse |
| 6 | **COPAUS0C.cbl** | 1,032 | Online (CICS) | Pending authorization summary |
| 7 | **COPAUA0C.cbl** | 1,026 | Online (CICS + MQ) | Authorization processor with MQ integration |
| 8 | **COACTVWC.cbl** | 941 | Online (CICS) | Account view (read-only) |
| 9 | **CBSTM03A.CBL** | 924 | Batch | Statement generation |
| 10 | **COCRDSLC.cbl** | 887 | Online (CICS) | Credit card detail view |

**Total estate:** ~30,175 lines across 44 programs. The top 10 programs account for ~52% of all code.

---

## 2. Top 10 Programs by Copybook Count

| Rank | Program | Unique Copybooks | Key Copybooks |
|------|---------|-----------------|---------------|
| 1 | **COACTUPC.cbl** | 18 | CSUTLDWY, CSLKPCDY, CSSETATY (×38 REPLACING), CSUTLDPY, COACTUP, + all common |
| 2 | **COPAUA0C.cbl** | 14 | CMQODV, CMQMDV, CMQV, CMQTML, CMQPMOV, CMQGMOV, CCPAURQY, CCPAURLY, CCPAUERY, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y, CVCUS01Y |
| 3 | **COACTVWC.cbl** | 15 | CVCRD01Y, COCOM01Y, COACTVW, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, + common |
| 4 | **COCRDSLC.cbl** | 13 | COCRDSL, CVACT02Y, CVCUS01Y, + common |
| 5 | **COCRDUPC.cbl** | 13 | COCRDUP, CVACT02Y, CVCUS01Y, + common |
| 6 | **COPAUS0C.cbl** | 14 | COPAU00, CIPAUSMY, CIPAUDTY, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, + common |
| 7 | **COTRTLIC.cbl** | 15 | COTRTLI, CSDB2RWY, SQLCA, DCLTRTYP, CSDB2RPY, + common |
| 8 | **COTRTUPC.cbl** | 15 | COTRTUP, CSSETATY, DCLTRTYP, DCLTRCAT, CSDB2RWY, + common |
| 9 | **CBTRN03C.cbl** | 5 | CVTRA05Y, CVACT03Y, CVTRA03Y, CVTRA04Y, CVTRA07Y |
| 10 | **CBEXPORT.cbl** | 6 | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |

---

## 3. Top 10 Programs by I/O Operations

I/O operations include VSAM file READ/WRITE/REWRITE/DELETE, CICS file operations, EXEC SQL, and MQ calls.

| Rank | Program | Total I/O Ops | Breakdown |
|------|---------|--------------|-----------|
| 1 | **COACTUPC.cbl** | ~18 | 5× CICS READ, 2× REWRITE, 8× SEND/RECEIVE MAP, HANDLE ABEND |
| 2 | **CBTRN03C.cbl** | 18 | 6× file READ, 6× CLOSE, 5× OPEN, 1× WRITE (report) |
| 3 | **COTRTLIC.cbl** | 23 | 11× EXEC SQL, 12× EXEC CICS (MAP, SYNCPOINT, etc.) |
| 4 | **COPAUA0C.cbl** | 17 | 4× MQ calls, 13× EXEC CICS (READ, ASKTIME, WRITEQ, etc.) |
| 5 | **COACCT01.cbl** | 14 | 8× MQ calls (OPEN/GET/PUT/CLOSE), 4× EXEC CICS (READ, RETRIEVE) |
| 6 | **CBTRN02C.cbl** | 14 | 3× OPEN, 3× CLOSE, 2× READ, 2× WRITE, 2× REWRITE, 2× file I-O |
| 7 | **COTRTUPC.cbl** | ~18 | 8× EXEC SQL, 10× EXEC CICS |
| 8 | **CBSTM03A.CBL** | 12 | Multi-file reads via CBSTM03B + 2× WRITE (text + HTML) |
| 9 | **COCRDUPC.cbl** | 14 | 4× CICS READ, 1× REWRITE, 6× MAP ops, HANDLE ABEND |
| 10 | **COBIL00C.cbl** | 13 | CICS READ, REWRITE, WRITE + MAP operations, STARTBR/READPREV |

---

## 4. Top 10 Programs by Business Logic Density

Business logic density is measured by EVALUATE + IF statement counts and nesting depth, indicating complex decision trees.

| Rank | Program | EVALUATE | IF | PERFORM | Total Logic | Density (Logic/LOC) |
|------|---------|----------|-----|---------|------------|-------------------|
| 1 | **COACTUPC.cbl** | 20 | 174 | 64 | 258 | 0.061 |
| 2 | **COTRTLIC.cbl** | 32 | 98 | 69 | 199 | 0.095 |
| 3 | **COTRTUPC.cbl** | 26 | 58 | 40 | 124 | 0.073 |
| 4 | **CBTRN03C.cbl** | 4 | 38 | 73 | 115 | 0.177 |
| 5 | **COCRDUPC.cbl** | 16 | 77 | 26 | 119 | 0.076 |
| 6 | **COCRDLIC.cbl** | 18 | 70 | 34 | 122 | 0.084 |
| 7 | **CBTRN02C.cbl** | 0 | 48 | 62 | 110 | 0.150 |
| 8 | **COTRN02C.cbl** | 26 | 14 | 61 | 101 | 0.129 |
| 9 | **COTRN00C.cbl** | 16 | 26 | 47 | 89 | 0.127 |
| 10 | **COPAUA0C.cbl** | 10 | 27 | 38 | 75 | 0.073 |

**Key insight:** COACTUPC has the highest absolute logic count (258 control statements across 4,236 lines), while CBTRN03C and CBTRN02C have the highest logic *density* — more decision logic per line of code, indicating concentrated business rules.

---

## 5. Top 10 Programs by Inter-Program Dependencies

Dependencies include: programs called, programs that call this one, copybooks shared, VSAM files accessed, DB2 tables used, and MQ queues.

| Rank | Program | Dependency Count | Dependency Details |
|------|---------|-----------------|-------------------|
| 1 | **COACTUPC.cbl** | 21 | 18 copybooks, 3 VSAM files (ACCT, CUST, XREF), called from COMEN01C, shares CSUTLDPY/CSUTLDWY with COTRTUPC |
| 2 | **COPAUA0C.cbl** | 19 | 14 copybooks, 3 VSAM files, 3 MQ queues, called from MQ trigger, IMS data structures |
| 3 | **COTRTLIC.cbl** | 18 | 15 copybooks, 2 DB2 tables, CICS maps, SQLCA, cursor management |
| 4 | **CBTRN02C.cbl** | 14 | 5 copybooks, 6 VSAM files (R/W/I-O), central batch pipeline program |
| 5 | **COACTVWC.cbl** | 18 | 15 copybooks, 3 VSAM files, pure read |
| 6 | **CBSTM03A.CBL** | 11 | 4 copybooks, calls CBSTM03B, reads 4 VSAM files, writes 2 output files |
| 7 | **COTRTUPC.cbl** | 17 | 15 copybooks, 2 DB2 tables, CSSETATY for validation |
| 8 | **CBTRN03C.cbl** | 11 | 5 copybooks, reads 6 files (trans, xref, types, categories, dates), writes report |
| 9 | **CBACT04C.cbl** | 10 | 5 copybooks, 5 VSAM files, reads TCATBALF/XREF/DISCGRP, updates ACCT/TRANSACT |
| 10 | **CBEXPORT.cbl** | 11 | 6 copybooks, reads 5 master VSAM files, writes export file |

---

## 6. Composite Hotspot Score

Programs are scored using a weighted composite across all five dimensions:

| Weight | Metric | Rationale |
|--------|--------|-----------|
| 25% | Lines of Code | Raw volume of code to convert/maintain |
| 20% | Copybook References | Coupling to shared data structures |
| 20% | I/O Operations | Integration complexity (files, DB2, MQ) |
| 20% | Business Logic Density | Core rules requiring careful translation |
| 15% | Inter-Program Dependencies | Ripple effect risk |

### Composite Rankings

| Rank | Program | LOC Score | Copy Score | I/O Score | Logic Score | Dep Score | **Composite** |
|------|---------|-----------|-----------|-----------|------------|-----------|--------------|
| 1 | **COACTUPC.cbl** | 10.0 | 10.0 | 9.0 | 10.0 | 10.0 | **9.85** |
| 2 | **COTRTLIC.cbl** | 8.0 | 8.5 | 10.0 | 9.5 | 9.0 | **9.00** |
| 3 | **COTRTUPC.cbl** | 7.0 | 8.5 | 9.0 | 7.5 | 8.5 | **8.05** |
| 4 | **COPAUA0C.cbl** | 5.5 | 9.5 | 8.5 | 5.0 | 9.5 | **7.48** |
| 5 | **COCRDUPC.cbl** | 6.5 | 7.0 | 7.0 | 7.0 | 6.0 | **6.75** |
| 6 | **COCRDLIC.cbl** | 6.0 | 6.0 | 5.0 | 7.5 | 5.5 | **6.05** |
| 7 | **CBTRN02C.cbl** | 3.5 | 3.0 | 7.0 | 6.5 | 7.0 | **5.25** |
| 8 | **CBTRN03C.cbl** | 3.0 | 3.0 | 9.0 | 6.0 | 5.5 | **5.15** |
| 9 | **CBSTM03A.CBL** | 4.5 | 4.0 | 6.0 | 4.0 | 5.5 | **4.75** |
| 10 | **COPAUS0C.cbl** | 5.0 | 7.5 | 5.0 | 3.5 | 5.0 | **5.15** |

---

## 7. Modernization Recommendations

### Tier 1: Modernize First (High Complexity + High Business Value)

#### 1. **COACTUPC.cbl** — Account Update (Composite: 9.85)
- **Why first:** Largest program (4,236 LOC), highest total complexity, and the most-coupled online program (18 copybooks, 38× CSSETATY REPLACING invocations for field-level validation). It exercises the most critical business entity (accounts) and touches customer data simultaneously.
- **Risk:** Heavy use of CICS REWRITE, HANDLE ABEND, and parameterized copybook REPLACING makes automated translation difficult. The 174 IF statements indicate deeply nested validation logic.
- **Recommendation:** Decompose into separate microservices: account validation service, account update service, and screen presentation layer. Extract validation rules from CSSETATY/CSUTLDPY into a shared validation library.

#### 2. **COTRTLIC.cbl** — Transaction Type List/DB2 (Composite: 9.00)
- **Why early:** Highest EVALUATE count (32) and significant DB2 integration with cursor-based pagination. This program demonstrates the pattern for all DB2-integrated CICS programs.
- **Risk:** Mixed CICS + DB2 with SYNCPOINT management, DSNTIAC error formatting, and SQL cursor lifecycle.
- **Recommendation:** Extract DB2 access into a data access layer/repository pattern. The cursor pagination can be replaced with standard paginated queries. Modernize this early to establish the DB2 migration pattern.

#### 3. **CBTRN02C.cbl** — Transaction Posting (Composite: 5.25, but critical pipeline position)
- **Why early:** Central to the daily batch pipeline — posts validated transactions, updates account and category balances, writes to transaction master, and generates rejects. Any batch modernization effort depends on this program.
- **Risk:** Touches 6 VSAM files simultaneously with mixed I-O (READ/WRITE/REWRITE). High logic density (0.150) means concentrated business rules for balance calculations.
- **Recommendation:** Ideal candidate for conversion to a stream-processing or event-driven architecture. Decompose the account balance update, category balance update, and reject handling into separate processing stages.

### Tier 2: Modernize Second (Template Programs)

#### 4. **COTRTUPC.cbl** — Transaction Type CRUD/DB2 (Composite: 8.05)
- **Why:** Paired with COTRTLIC; modernizing both establishes the complete DB2 CRUD pattern. Uses CSSETATY field validation heavily.
- **Recommendation:** Convert to a standard REST API with CRUD endpoints for transaction types and categories.

#### 5. **COPAUA0C.cbl** — Authorization Processor/MQ (Composite: 7.48)
- **Why:** The most integration-heavy program — combines CICS, MQ, and VSAM in a single real-time flow. Modernizing this establishes the MQ migration pattern.
- **Recommendation:** Convert to an event-driven microservice consuming from a message broker (e.g., Kafka, SQS) with account/customer lookup via API calls rather than VSAM reads.

#### 6. **COCRDUPC.cbl** — Card Update (Composite: 6.75)
- **Why:** Follows the same CICS screen + VSAM REWRITE pattern as COACTUPC but simpler (1,560 LOC). Good validation target after account update is modernized.
- **Recommendation:** Reuse the validation library extracted from COACTUPC.

### Tier 3: Modernize in Bulk (Common Patterns)

#### 7–8. **COCRDLIC.cbl**, **COTRN00C.cbl**, **COUSR00C.cbl** — List/Browse Screens
- These follow identical patterns: CICS STARTBR/READNEXT/READPREV/ENDBR with paginated display. Modernize one, then apply the pattern to all.

#### 9–10. **CBTRN03C.cbl**, **CBSTM03A.CBL** — Report/Statement Generation
- Pure batch with sequential file reads and formatted output. Convert to report generation services using modern templating (PDF libraries, HTML renderers).

### Modernization Order Summary

```
Phase 1 (Foundation):
  COACTUPC → establishes CICS screen + VSAM update + validation pattern
  COTRTLIC → establishes CICS + DB2 + cursor pagination pattern
  CBTRN02C → establishes batch pipeline core

Phase 2 (Pattern Reuse):
  COTRTUPC → applies DB2 CRUD pattern from COTRTLIC
  COPAUA0C → establishes MQ/event-driven pattern
  COCRDUPC → applies update pattern from COACTUPC

Phase 3 (Bulk Conversion):
  All remaining CICS list/view screens (COCRDLIC, COTRN00C, etc.)
  All remaining batch programs (CBTRN03C, CBSTM03A, etc.)
  Utility programs (CSUTLDTC, COBSWAIT)

Phase 4 (IMS Migration):
  CBPAUP0C, DBUNLDGS, PAUDBLOD, PAUDBUNL → IMS DB replacement last
  (most specialized, least reusable patterns)
```

---

## 8. Risk Summary

| Risk Factor | Programs Affected | Mitigation |
|-------------|------------------|------------|
| CICS HANDLE ABEND / pseudo-conversational state | COACTUPC, COCRDSLC, COCRDUPC, COCRDLIC | Extract state management into session/cache layer |
| CSSETATY parameterized REPLACING (38× in COACTUPC) | COACTUPC, COTRTUPC | Generate validation rules from REPLACING parameters |
| DB2 cursor lifecycle + SYNCPOINT | COTRTLIC, COTRTUPC | Map to JPA/JDBC with connection pool management |
| MQ integration (3 queue pairs) | COPAUA0C, COACCT01, CODATE01 | Replace with modern message broker (Kafka/SQS) |
| IMS DL/I hierarchical DB | CBPAUP0C, DBUNLDGS, PAUDBLOD, PAUDBUNL | Flatten hierarchy into relational tables |
| VSAM KSDS with AIX | All batch programs, CICS file operations | Map to indexed database tables (PostgreSQL/DynamoDB) |
| COMP-3 packed decimal arithmetic | Authorization sub-app, CBACT04C | Use Java BigDecimal or equivalent |
| GDG (Generation Data Groups) | Statement, report, reject files | Replace with timestamped file naming or object storage |
| REDEFINES (polymorphic records) | CVEXPORT.cpy, CODATECN.cpy | Map to type-discriminated DTOs/union types |
| Plain-text password storage | CSUSR01Y.cpy / COSGN00C | Implement proper password hashing |
