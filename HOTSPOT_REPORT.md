# Hotspot Report — CardDemo

> Complexity analysis, composite scoring, and modernization priority recommendations.

---

## Table of Contents

1. [Top 10 by Lines of Code](#1-top-10-by-lines-of-code)
2. [Top 10 by Copybooks Referenced](#2-top-10-by-copybooks-referenced)
3. [Top 10 by I/O Operations](#3-top-10-by-io-operations)
4. [Top 10 by Business Logic Density](#4-top-10-by-business-logic-density)
5. [Top 10 by Inter-Program Dependencies](#5-top-10-by-inter-program-dependencies)
6. [Composite Complexity Score](#composite-complexity-score)
7. [Modernization Priority](#modernization-priority)

---

## 1. Top 10 by Lines of Code

| Rank | Program | Lines | Classification |
|------|---------|-------|---------------|
| 1 | COACTUPC.cbl | 4,236 | CICS Online (Account Update) |
| 2 | COTRTLIC.cbl | 2,098 | CICS Online / DB2 (Tran Type List) |
| 3 | COTRTUPC.cbl | 1,702 | CICS Online / DB2 (Tran Type Update) |
| 4 | COCRDUPC.cbl | 1,560 | CICS Online (Card Update) |
| 5 | COCRDLIC.cbl | 1,459 | CICS Online (Card List) |
| 6 | COPAUS0C.cbl | 1,032 | CICS Online / Auth (Auth Summary) |
| 7 | COPAUA0C.cbl | 1,026 | CICS Online / Auth MQ (Auth Engine) |
| 8 | COACTVWC.cbl | 941 | CICS Online (Account View) |
| 9 | CBSTM03A.CBL | 924 | Batch (Statement Creation) |
| 10 | COCRDSLC.cbl | 887 | CICS Online (Card Detail) |

## 2. Top 10 by Copybooks Referenced

Counted from COPY statements in each program (excluding DFHAID/DFHBMSCA system copies):

| Rank | Program | Copybook Count | Copybooks |
|------|---------|---------------|-----------|
| 1 | COACTUPC.cbl | 16 | CSSTRPFY, CSUTLDWY, COACTUP, COCOM01Y, COTTL01Y, CSDAT01Y, CSLKPCDY, CSMSG01Y, CSMSG02Y, CSSETATY, CSUSR01Y, CSUTLDPY, CVACT01Y, CVACT03Y, CVCRD01Y, CVCUS01Y |
| 2 | COACTVWC.cbl | 13 | CSSTRPFY, COACTVW, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y |
| 3 | COPAUA0C.cbl | 14 | CCPAUERY, CCPAURLY, CCPAURQY, CIPAUDTY, CIPAUSMY, CMQGMOV, CMQMDV, CMQODV, CMQPMOV, CMQTML, CMQV, CVACT01Y, CVACT03Y, CVCUS01Y |
| 4 | COPAUS0C.cbl | 12 | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU00, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 5 | COCRDSLC.cbl | 11 | CSSTRPFY, COCOM01Y, COCRDSL, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y |
| 6 | COCRDUPC.cbl | 11 | CSSTRPFY, COCOM01Y, COCRDUP, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCRD01Y, CVCUS01Y |
| 7 | COCRDLIC.cbl | 9 | CSSTRPFY, COCOM01Y, COCRDLI, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CVCRD01Y |
| 8 | COPAUS1C.cbl | 8 | CIPAUDTY, CIPAUSMY, COCOM01Y, COPAU01, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y |
| 9 | COBIL00C.cbl | 8 | COBIL00, COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y |
| 10 | COTRN02C.cbl | 8 | COCOM01Y, COTRN02, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y |

## 3. Top 10 by I/O Operations

I/O operations counted: EXEC CICS READ/WRITE/REWRITE/DELETE/STARTBR/READNEXT, EXEC SQL, SEND MAP, RECEIVE MAP, file OPEN/CLOSE/READ/WRITE, and MQ CALL statements.

| Rank | Program | EXEC CICS | EXEC SQL | SEND/RECV MAP | MQ CALLs | File I/O | Total I/O |
|------|---------|-----------|----------|--------------|----------|----------|-----------|
| 1 | COCRDLIC.cbl | 18 | 0 | 2 | 0 | 0 | 20 |
| 2 | COTRTLIC.cbl | 12 | 16 | 2 | 0 | 0 | 30 |
| 3 | COTRTUPC.cbl | 12 | 7 | 2 | 0 | 0 | 21 |
| 4 | COACTUPC.cbl | 17 | 0 | 2 | 0 | 0 | 19 |
| 5 | COACTVWC.cbl | 15 | 0 | 2 | 0 | 0 | 17 |
| 6 | COCRDSLC.cbl | 14 | 0 | 2 | 0 | 0 | 16 |
| 7 | COBIL00C.cbl | 13 | 0 | 0 | 0 | 0 | 13 |
| 8 | COPAUA0C.cbl | 12 | 0 | 0 | 4 | 0 | 16 |
| 9 | COCRDUPC.cbl | 12 | 0 | 2 | 0 | 0 | 14 |
| 10 | COUSR00C.cbl | 11 | 0 | 0 | 0 | 0 | 11 |

## 4. Top 10 by Business Logic Density

Business logic = EVALUATE + IF statements. Nesting depth estimated from code structure complexity.

| Rank | Program | IF Count | EVALUATE Count | Total Logic | Est. Max Nesting |
|------|---------|----------|---------------|-------------|-----------------|
| 1 | COACTUPC.cbl | 174 | 10 | 184 | 6+ |
| 2 | COTRTLIC.cbl | 98 | 16 | 114 | 5+ |
| 3 | COCRDUPC.cbl | 77 | 16 | 93 | 4+ |
| 4 | COCRDLIC.cbl | 70 | 18 | 88 | 4+ |
| 5 | COTRTUPC.cbl | 58 | 26 | 84 | 4+ |
| 6 | CBTRN02C.cbl | 48 | 0 | 48 | 3+ |
| 7 | CBACT04C.cbl | 43 | 0 | 43 | 3+ |
| 8 | CBTRN03C.cbl | 38 | 4 | 42 | 3+ |
| 9 | COCRDSLC.cbl | 37 | 8 | 45 | 3+ |
| 10 | CBTRN01C.cbl | 33 | 0 | 33 | 3 |

## 5. Top 10 by Inter-Program Dependencies

Dependencies include: outgoing CALLs, EXEC CICS LINK/XCTL, plus incoming references from other programs (callers and navigations).

| Rank | Program | Outgoing CALLs | LINK/XCTL | Incoming Refs | COMMAREA Nav | Total Deps |
|------|---------|---------------|-----------|---------------|-------------|------------|
| 1 | COACTUPC.cbl | 0 | 1 (dynamic) | 1 (COMEN01C) | Self + return | 4 |
| 2 | COMEN01C.cbl | 0 | 1 (dynamic) | 1 (COSGN00C) | Routes to 11 programs | 14 |
| 3 | COSGN00C.cbl | 0 | 2 (COADM01C, COMEN01C) | 0 (entry point) | — | 3 |
| 4 | COCRDLIC.cbl | 0 | 2 (dynamic) | 1 (COMEN01C) | COCRDSLC, COCRDUPC | 6 |
| 5 | COADM01C.cbl | 0 | 0 | 1 (COSGN00C) | Routes to 6 programs | 8 |
| 6 | COPAUA0C.cbl | 4 (MQ) | 0 | 0 | — | 4 |
| 7 | CBSTM03A.CBL | 1 (CBSTM03B) | 0 | 1 (CREASTMT JCL) | — | 3 |
| 8 | CSUTLDTC.cbl | 1 (CEEDAYS) | 0 | 2 (CORPT00C, COTRN02C) | — | 4 |
| 9 | COTRTLIC.cbl | 0 | 2 (dynamic) | 1 (COADM01C) | Self + COTRTUPC | 5 |
| 10 | COTRN02C.cbl | 1 (CSUTLDTC) | 0 | 1 (COMEN01C) | — | 3 |

---

## Composite Complexity Score

Scoring methodology (each dimension normalized to 10 points, max composite = 50):
- **LOC**: 10 pts for ≥4000, 8 for ≥2000, 6 for ≥1000, 4 for ≥500, 2 for <500
- **Copybooks**: 10 pts for ≥14, 8 for ≥10, 6 for ≥8, 4 for ≥4, 2 for <4
- **I/O Ops**: 10 pts for ≥25, 8 for ≥17, 6 for ≥12, 4 for ≥6, 2 for <6
- **Logic Density**: 10 pts for ≥100, 8 for ≥70, 6 for ≥40, 4 for ≥20, 2 for <20
- **Dependencies**: 10 pts for ≥10, 8 for ≥6, 6 for ≥4, 4 for ≥2, 2 for <2

| Rank | Program | LOC Score | Copy Score | I/O Score | Logic Score | Dep Score | **Composite** |
|------|---------|-----------|-----------|-----------|-------------|-----------|:-------------:|
| **1** | **COACTUPC.cbl** | 10 | 10 | 8 | 10 | 6 | **44** |
| **2** | **COTRTLIC.cbl** | 8 | 2 | 10 | 10 | 6 | **36** |
| **3** | **COCRDLIC.cbl** | 6 | 8 | 8 | 8 | 8 | **38** |
| **4** | **COCRDUPC.cbl** | 6 | 8 | 6 | 8 | 4 | **32** |
| **5** | **COTRTUPC.cbl** | 8 | 2 | 8 | 8 | 4 | **30** |
| **6** | **COACTVWC.cbl** | 4 | 10 | 8 | 4 | 4 | **30** |
| **7** | **COPAUA0C.cbl** | 6 | 10 | 8 | 4 | 6 | **34** |
| **8** | **COCRDSLC.cbl** | 4 | 8 | 8 | 6 | 4 | **30** |
| **9** | **CBTRN02C.cbl** | 4 | 4 | 4 | 6 | 4 | **22** |
| **10** | **CBACT04C.cbl** | 4 | 4 | 4 | 6 | 2 | **20** |

### Sorted by Composite Score

| Rank | Program | Composite | Type |
|------|---------|:---------:|------|
| 1 | COACTUPC.cbl | **44** | CICS Online |
| 2 | COCRDLIC.cbl | **38** | CICS Online |
| 3 | COTRTLIC.cbl | **36** | CICS/DB2 |
| 4 | COPAUA0C.cbl | **34** | CICS/MQ/Auth |
| 5 | COCRDUPC.cbl | **32** | CICS Online |
| 6 | COACTVWC.cbl | **30** | CICS Online |
| 7 | COTRTUPC.cbl | **30** | CICS/DB2 |
| 8 | COCRDSLC.cbl | **30** | CICS Online |
| 9 | CBTRN02C.cbl | **22** | Batch |
| 10 | CBACT04C.cbl | **20** | Batch |

---

## Modernization Priority

### Tier 1: Start with Batch Programs (Lower Risk, High Value)

#### 1. CBTRN02C.cbl — Daily Transaction Posting (Score: 22)

**Why prioritize**: This is the core daily batch program that posts transactions to accounts. It reads daily transactions, validates against cross-references and accounts, updates account balances, and writes rejects. Although its composite score is moderate, it is **central to the daily pipeline** (called by POSTTRAN JCL in the CA-7 chain).

**Modernization approach**: Convert to a Spring Batch job with step-based processing. The VSAM file I/O maps well to JPA repositories. Reject handling becomes exception-based flow.

**Risks**:
- Must maintain exact decimal arithmetic (PIC S9(09)V99 → BigDecimal)
- Transaction atomicity across multiple VSAM files needs equivalent DB transaction scope
- GDG output for rejects needs a replacement archival strategy

#### 2. CBACT04C.cbl — Interest Calculator (Score: 20)

**Why prioritize**: Monthly interest calculation reads 5 VSAM files (TCATBALF, CARDXREF via AIX, ACCTDATA, DISCGRP, TRANSACT) and computes interest based on disclosure group rates. It is a self-contained batch process with clear inputs/outputs.

**Modernization approach**: Convert to a Spring Batch step that reads account-category balances, looks up interest rates, computes interest, and writes interest transactions. The disclosure group lookup maps naturally to a database join.

**Risks**:
- Complex multi-file cross-referencing (account → xref → card → category → disclosure group → rate)
- AIX (Alternate Index) access path must be replicated with proper secondary indexing
- COMP-3 packed decimal fields require careful conversion

#### 3. CBSTM03A.CBL + CBSTM03B.CBL — Statement Creation (Score: moderate)

**Why prioritize**: Statement generation is a batch reporting process with clear I/O boundaries. CBSTM03A calls CBSTM03B as a subroutine, making it a good candidate to demonstrate call graph modernization.

**Modernization approach**: Single Spring Batch job; the subroutine relationship maps to service method calls. HTML output already exists, simplifying modern report generation.

**Risks**:
- The sort/transform step (CREASTMT JCL uses SORT) must be replicated
- CBSTM03B manages multiple file opens/closes that need connection management

### Tier 2: CICS Programs (Higher Complexity)

#### 4. COACTUPC.cbl — Account Update (Score: 44, #1 Hotspot)

**Why prioritize**: This is the most complex program in the entire system at 4,236 lines with 174 IF statements, 64 PERFORMs, and access to 5 VSAM files. It is the primary account maintenance program for all users.

**Modernization approach**: Decompose into microservices — an Account API (CRUD), a Validation Service (the extensive field validation logic), and a UI layer. The 16+ copybooks indicate deep data coupling that must be resolved.

**Risks**:
- **Highest risk program** — 174 conditional branches mean extensive test coverage is needed before refactoring
- Field-level validation (ZIP codes, state codes via CSLKPCDY lookup tables) must be ported exactly
- BMS map interaction (SEND/RECEIVE MAP) must be translated to REST API + frontend
- COMMAREA navigation (CCARD-NEXT-PROG) couples this program to the entire CICS navigation framework
- Estimated 500+ test cases needed for full coverage

#### 5. COCRDLIC.cbl — Credit Card List (Score: 38)

**Why prioritize**: Central navigation hub — routes to card detail (COCRDSLC) and card update (COCRDUPC). It implements browse/search with pagination against VSAM STARTBR/READNEXT.

**Modernization approach**: Convert to a REST API with paging (offset/limit) backed by a database query. The browse logic with STARTBR/READNEXT maps to cursor-based pagination.

**Risks**:
- 18 EXEC CICS commands with complex browse state management
- Dynamic XCTL navigation to dependent programs must be replaced with routing

#### 6. COTRTLIC.cbl — Transaction Type List (Score: 36)

**Why prioritize**: Already uses DB2 (16 EXEC SQL statements), making the database layer partially modernized. The CICS layer is the main conversion target.

**Modernization approach**: Keep DB2 backend (migrate to modern RDBMS), convert CICS presentation to REST/HTML. The 16 SQL statements are likely SELECT/UPDATE/DELETE operations that map directly to Spring Data JPA.

**Risks**:
- Mixed CICS + DB2 means both presentation and data access must be migrated simultaneously
- 98 IF statements indicate complex business rules around type management

#### 7. COPAUA0C.cbl — Authorization Engine (Score: 34)

**Why prioritize**: This is the real-time authorization processor using MQ for request/response. It represents the most modern integration pattern in the application.

**Modernization approach**: Convert to a Spring Boot microservice consuming from a message queue (RabbitMQ/Kafka). The MQ CALL pattern maps directly to Spring AMQP or Spring Kafka listeners.

**Risks**:
- MQ-based request/response pattern requires exact message format compatibility during migration
- Real-time authorization has strict latency requirements
- 14 copybooks indicate heavy data structure dependencies

### Tier 3: Supporting Programs

#### 8. COMEN01C.cbl — Main Menu (Score: moderate, 14 dependencies)

**Why**: Despite low complexity, it has the **most outgoing dependencies** (routes to 11 programs). Must be modernized as the routing layer when converting CICS navigation to a web UI.

#### 9. COSGN00C.cbl — Sign-on (Score: low, entry point)

**Why**: Entry point for all user sessions. Simple credential check against USRSEC. Natural candidate for Spring Security/OAuth2 replacement.

#### 10. CSUTLDTC.cbl — Date Utility (Score: low, called by 2 programs)

**Why**: Shared utility called by CORPT00C and COTRN02C. Must be converted as a dependency before those programs. Maps naturally to `java.time` APIs.

---

## Summary

| Priority | Program | Composite | Type | Recommended Approach | Est. Effort |
|----------|---------|-----------|------|---------------------|-------------|
| 1 | CBTRN02C | 22 | Batch | Spring Batch job | Medium |
| 2 | CBACT04C | 20 | Batch | Spring Batch job | Medium |
| 3 | CBSTM03A/B | — | Batch | Spring Batch + Service | Medium |
| 4 | COACTUPC | 44 | CICS | Microservice decomposition | Very High |
| 5 | COCRDLIC | 38 | CICS | REST API + pagination | High |
| 6 | COTRTLIC | 36 | CICS/DB2 | REST API (keep DB2 layer) | High |
| 7 | COPAUA0C | 34 | CICS/MQ | Spring Boot + messaging | High |
| 8 | COMEN01C | — | CICS | Web UI routing layer | Low |
| 9 | COSGN00C | — | CICS | Spring Security | Low |
| 10 | CSUTLDTC | — | Utility | java.time utility class | Low |

**Recommended Strategy**: Begin with **batch programs** (Tier 1) to build confidence and establish patterns for VSAM-to-RDBMS migration, decimal arithmetic, and file I/O. Then tackle **CICS programs** (Tier 2) in order of decreasing complexity, ensuring COACTUPC has exhaustive test coverage before refactoring. Finally, convert the **routing/utility layer** (Tier 3) as the navigation framework is replaced by a web frontend.
