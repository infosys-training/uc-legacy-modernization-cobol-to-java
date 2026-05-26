# Hotspot Report — CardDemo COBOL Estate

## 1. Ranking by Lines of Code (LOC)

| Rank | Program | LOC | Classification |
|------|---------|-----|---------------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS/DB2) |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS/DB2) |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS/IMS) |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS/IMS/MQ) |
| 8 | COACTVWC.cbl | 941 | Online (CICS) |
| 9 | CBSTM03A.CBL | 924 | Batch |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) |

---

## 2. Ranking by Number of Copybooks Referenced

| Rank | Program | Unique Copybooks | Key Copybooks |
|------|---------|-----------------|---------------|
| 1 | COACTUPC.cbl | 18 | CSUTLDWY, CSUTLDPY, CSLKPCDY, CSSETATY (×25), CVACT01Y, CVCUS01Y |
| 2 | COACTVWC.cbl | 15 | CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y |
| 3 | COPAUS0C.cbl | 14 | CIPAUSMY, CIPAUDTY, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 4 | COPAUA0C.cbl | 14 | CMQODV, CMQMDV, CMQV, CIPAUSMY, CIPAUDTY, CVACT03Y, CVACT01Y |
| 5 | COCRDUPC.cbl | 13 | CVACT02Y, CVCRD01Y, CVCUS01Y |
| 6 | COCRDSLC.cbl | 13 | CVACT02Y, CVCRD01Y, CVCUS01Y |
| 7 | COTRTUPC.cbl | 13 | CSUTLDWY, CVCRD01Y, CSSETATY |
| 8 | COCRDLIC.cbl | 11 | CVACT02Y, CVCRD01Y |
| 9 | COTRTLIC.cbl | 11 | CVCRD01Y, COCOM01Y |
| 10 | COTRN02C.cbl | 10 | CVACT01Y, CVACT03Y, CVTRA05Y |

---

## 3. Ranking by Number of I/O Operations

| Rank | Program | I/O Ops | Type of I/O |
|------|---------|---------|-------------|
| 1 | CBSTM03A.CBL | 118 | File OPEN/CLOSE/READ/WRITE (master + subroutine calls) |
| 2 | COTRTUPC.cbl | 30 | DB2 SELECT/UPDATE/INSERT, CICS SEND/RECEIVE |
| 3 | CBEXPORT.cbl | 29 | VSAM READ (5 files) + seq WRITE |
| 4 | CBIMPORT.cbl | 29 | VSAM READ + 6 output WRITE + error |
| 5 | COACCT01.cbl | 29 | MQ OPEN/GET/PUT/CLOSE + VSAM READ |
| 6 | COTRTLIC.cbl | 27 | DB2 FETCH/DELETE + CICS SEND/RECEIVE |
| 7 | CODATE01.cbl | 27 | MQ OPEN/GET/PUT/CLOSE |
| 8 | CBTRN02C.cbl | 24 | VSAM READ/WRITE/REWRITE (5 files) |
| 9 | CBTRN03C.cbl | 23 | VSAM READ (4 files) + seq WRITE |
| 10 | COACTUPC.cbl | 23 | CICS READ/REWRITE (3 VSAM files) |

---

## 4. Ranking by Business Logic Density (Max IF/EVALUATE Nesting Depth)

| Rank | Program | Max Nesting | EVALUATE Count | IF Count | Total Logic Stmts |
|------|---------|-------------|----------------|----------|-------------------|
| 1 | COTRTLIC.cbl | 20 | 32 | 88 | 120 |
| 2 | COTRTUPC.cbl | 16 | 26 | 52 | 78 |
| 3 | COACTUPC.cbl | 15 | 20 | 168 | 188 |
| 4 | COTRN02C.cbl | 14 | 26 | 14 | 40 |
| 5 | COCRDLIC.cbl | 13 | 18 | 61 | 79 |
| 6 | COCRDUPC.cbl | 12 | 16 | 74 | 90 |
| 7 | CBSTM03A.CBL | 6 | 9 | 15 | 24 |
| 8 | CBTRN03C.cbl | 5 | 4 | 38 | 42 |
| 9 | CBACT04C.cbl | 4 | 0 | 43 | 43 |
| 10 | CBTRN02C.cbl | 3 | 0 | 48 | 48 |

---

## 5. Ranking by Inter-Program Dependencies

| Rank | Program | Dependencies (calls/called by/XCTL) | Role |
|------|---------|-------------------------------------|------|
| 1 | COMEN01C.cbl | 11+ outbound XCTL targets | Central router — all user-facing programs depend on it |
| 2 | COADM01C.cbl | 6+ outbound XCTL targets | Admin router |
| 3 | CBSTM03A.CBL | 12 CALLs to CBSTM03B + 1 to CEE3ABD | Statement generator; tightly coupled with subroutine |
| 4 | CBSTM03B.CBL | Called by CBSTM03A (13 entry points) | File I/O services; single caller |
| 5 | COPAUA0C.cbl | MQ calls + VSAM lookups + IMS writes | Integration hub for authorization |
| 6 | CSUTLDTC.cbl | Called by CORPT00C, COTRN02C | Shared date utility |
| 7 | COSGN00C.cbl | XCTL to COADM01C or COMEN01C | Entry point for all CICS sessions |
| 8 | CORPT00C.cbl | Calls CSUTLDTC; submits JCL via TDQ | Online-to-batch bridge |
| 9 | CBTRN02C.cbl | Called by POSTTRAN.jcl; writes to 3 VSAM files | Core batch posting engine |
| 10 | CBACT04C.cbl | Called by INTCALC.jcl; reads 4 files, writes 1 | Financial calculation engine |

---

## 6. Composite Hotspot Score

Weighted composite ranking (LOC: 20%, Copybooks: 15%, I/O: 20%, Logic Density: 25%, Dependencies: 20%):

| Rank | Program | Composite Score | Key Risk Factors |
|------|---------|----------------|-----------------|
| **1** | **COACTUPC.cbl** | **95/100** | Largest program (4236 LOC), most copybooks (18), deep nesting (15), 168 IF statements, 25× CSSETATY expansions |
| **2** | **COTRTLIC.cbl** | **82/100** | Deepest nesting (20), DB2 cursors, complex paging logic, 2098 LOC |
| **3** | **COTRTUPC.cbl** | **78/100** | Deep nesting (16), DB2 CRUD, 13 copybooks, 1702 LOC |
| **4** | **CBSTM03A.CBL** | **72/100** | Highest I/O (118 ops), tight coupling with CBSTM03B, ALTER/GO TO, 2D arrays |
| **5** | **COCRDLIC.cbl** | **68/100** | Deep nesting (13), AIX browse logic, 1459 LOC |
| **6** | **COCRDUPC.cbl** | **67/100** | Deep nesting (12), field validation, 1560 LOC |
| **7** | **COPAUA0C.cbl** | **65/100** | Multi-technology (CICS+IMS+MQ+VSAM), 14 copybooks, authorization logic |
| **8** | **CBTRN02C.cbl** | **60/100** | Core posting engine, writes to 3 files, reject processing |
| **9** | **CBACT04C.cbl** | **58/100** | Financial calculations, reads 4 files, interest rate logic |
| **10** | **COACTVWC.cbl** | **55/100** | 15 copybooks, multi-entity display, 941 LOC |

---

## 7. Modernization Recommendations

### Priority 1 — Modernize First

| Program | Rationale |
|---------|-----------|
| **COACTUPC.cbl** | Single largest program at 4,236 LOC with 188 conditional statements. Contains 25 COPY...REPLACING expansions of CSSETATY for validation, making it extremely difficult to maintain. The program handles account updates — a high-business-value function — and its complexity creates high defect risk. Decompose into: (a) validation service, (b) data access layer, (c) UI controller. |
| **CBSTM03A.CBL + CBSTM03B.CBL** | 1,154 combined LOC with obsolete ALTER/GO TO control flow and tight CALL-based coupling. Uses 2D arrays and unconventional file I/O delegation. Replaces naturally with a template-based reporting microservice (HTML generation maps well to modern templating). |
| **CBTRN02C.cbl** | Core daily batch engine — every transaction flows through this program. Failure here impacts all downstream processing. Relatively small (731 LOC) but writes to 3 VSAM files and has category-balance update logic that is the heart of the financial pipeline. Good candidate for event-driven architecture. |

### Priority 2 — High Value, Moderate Complexity

| Program | Rationale |
|---------|-----------|
| **COTRTLIC.cbl / COTRTUPC.cbl** | Already use DB2, making them natural candidates for migration to a JDBC/JPA-based service. The DB2 cursor paging logic maps directly to SQL pagination. Combined 3,800 LOC with clear CRUD semantics. |
| **COPAUA0C.cbl** | Authorization decision engine using MQ for request/response — a natural fit for a microservice with message queue integration. Handles real-time auth decisions, so latency-sensitive modernization. |
| **CBACT04C.cbl** | Self-contained interest calculator. Pure computational logic with clear inputs/outputs — ideal for extraction as a stateless calculation service. |

### Priority 3 — Lower Risk, Template Programs

| Program | Rationale |
|---------|-----------|
| **COCRDLIC/COCRDSLC/COCRDUPC** | Card management trio follows consistent patterns (list/detail/update). Modernize as a unified Card service with REST API. |
| **COUSR00C-03C** | User management CRUD — simple pattern, low complexity, maps directly to a user management microservice. |
| **COTRN00C/01C/02C** | Transaction viewing/entry — standard list/detail/add patterns. |

### Key Modernization Principles

1. **Start with COACTUPC** — greatest risk reduction per effort (most complex, most copybooks, most business logic)
2. **Extract shared services early** — CSUTLDTC (date validation), lookup tables (CSLKPCDY) as reusable microservices
3. **Preserve the batch pipeline** — POSTTRAN → INTCALC → TRANREPT → CREASTMT sequence should be preserved as an orchestrated workflow
4. **CBEXPORT/CBIMPORT are migration accelerators** — their multi-record format documents the canonical data model for all entities
5. **Use the COMMAREA pattern** as the API contract blueprint — COCOM01Y already defines the inter-program interface
