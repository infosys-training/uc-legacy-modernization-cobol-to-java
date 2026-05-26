# HOTSPOT REPORT — CardDemo COBOL Estate

## 1. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification | Domain |
|------|---------|-----|----------------|--------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) | Account Update |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS/DB2) | Tran Type List |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS/DB2) | Tran Type Update |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) | Card Update |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) | Card List |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS/IMS) | Auth Summary |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS/IMS/MQ/DB2) | Auth Decision |
| 8 | COACTVWC.cbl | 941 | Online (CICS) | Account View |
| 9 | CBSTM03A.CBL | 924 | Batch | Statement Print |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) | Card Detail |

---

## 2. Top 10 Programs by Copybook References

| Rank | Program | COPY Statements | Notable Copybooks |
|------|---------|----------------|-------------------|
| 1 | COACTUPC.cbl | 56 | CSSETATY (×40 via REPLACING), CVACT01Y, CVCUS01Y, CSLKPCDY, CSUTLDPY |
| 2 | COPAUA0C.cbl | 16 | CIPAUDTY, CIPAUSMY, CCPAURQY, CCPAURLY, PAUTBPCB, IMSFUNCS |
| 3 | COACTVWC.cbl | 15 | CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y |
| 4 | COCRDSLC.cbl | 15 | CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y |
| 5 | COCRDUPC.cbl | 15 | CVACT01Y, CVACT02Y, CVACT03Y, CVCRD01Y, CVCUS01Y |
| 6 | COPAUS0C.cbl | 14 | CIPAUDTY, CIPAUSMY, COPAU00, COCOM01Y |
| 7 | COCRDLIC.cbl | 13 | CVACT02Y, CVCRD01Y, COCOM01Y |
| 8 | COTRTUPC.cbl | 13 | CSDB2RPY, CSDB2RWY, COCOM01Y |
| 9 | COTRTLIC.cbl | 11 | CSDB2RPY, CSDB2RWY, COCOM01Y |
| 10 | COBIL00C.cbl | 10 | CVACT01Y, CVACT03Y, CVTRA05Y |

---

## 3. Top 10 Programs by I/O Operations

Counts include READ, WRITE, REWRITE, DELETE, OPEN, CLOSE, EXEC CICS READ/WRITE/READNEXT/STARTBR/DELETE operations.

| Rank | Program | I/O Ops | Primary Files | I/O Characteristics |
|------|---------|---------|---------------|-------------------|
| 1 | CBSTM03A.CBL | 158 | TRNXFILE, XREFFILE, CUSTFILE, ACCTFILE, STMTFILE, HTMLFILE | Heavy write — statement formatting with many WRITE statements |
| 2 | COTRTLIC.cbl | 103 | DB2 TRNTYPE, TRNTYCAT | DB2 cursor reads + CICS SEND/RECEIVE MAP |
| 3 | COACTUPC.cbl | 95 | ACCTDAT, CCXREF, CXACAIX (VSAM via CICS) | Many CICS READ/REWRITE + SEND MAP |
| 4 | CBTRN03C.cbl | 80 | TRANFILE, CARDXREF, TRANTYPE, TRANCATG, TRANREPT | Multi-file report with formatted output |
| 5 | CBTRN01C.cbl | 61 | DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE | 5-file validation pipeline |
| 6 | CBTRN02C.cbl | 61 | DALYTRAN, XREFFILE, ACCTFILE, TRANFILE, TCATBALF, DALYREJS | Multi-file posting with balance updates |
| 7 | COTRTUPC.cbl | 61 | DB2 TRNTYPE, TRNTYCAT | DB2 update with cursor management |
| 8 | CBACT04C.cbl | 52 | TCATBALF, XREFFILE, ACCTFILE, DISCGRP, TRANSACT | Interest calculation — multi-file reads and updates |
| 9 | CBACT01C.cbl | 41 | ACCTFILE, OUTFILE, ARRYFILE, VBRCFILE | Sequential read + multi-format writes |
| 10 | COCRDLIC.cbl | 41 | CARDDAT, CARDAIX, CCXREF | CICS browse with STARTBR/READNEXT |

---

## 4. Top 10 Programs by Business Logic Density

Business logic density is measured by combined EVALUATE + IF statement count, indicating decision complexity and validation depth.

| Rank | Program | EVALUATE | IF | Total | Density (per 100 LOC) | Key Logic Patterns |
|------|---------|----------|-----|-------|----------------------|-------------------|
| 1 | COACTUPC.cbl | 20 | 168 | 188 | 4.4 | Extensive field-by-field validation (date, phone, state, ZIP, names) |
| 2 | COTRTLIC.cbl | 32 | 88 | 120 | 5.7 | DB2 cursor pagination with complex screen state management |
| 3 | COCRDUPC.cbl | 16 | 74 | 90 | 5.8 | Card field validation with cross-entity integrity checks |
| 4 | CBTRN02C.cbl | 0 | 48 | 48 | 6.6 | Transaction posting with multi-condition reject logic |
| 5 | COCRDLIC.cbl | 18 | 61 | 79 | 5.4 | Browse/pagination with role-based filtering |
| 6 | COTRTUPC.cbl | 26 | 52 | 78 | 4.6 | DB2 update with type validation and state transitions |
| 7 | CBTRN03C.cbl | 4 | 38 | 42 | 6.5 | Report formatting with date range and type/category lookup |
| 8 | CBACT04C.cbl | 0 | 43 | 43 | 6.6 | Interest calculation with discount group and category rules |
| 9 | COCRDSLC.cbl | 8 | 33 | 41 | 4.6 | Card detail display with multi-entity data assembly |
| 10 | COACTVWC.cbl | 10 | 29 | 39 | 4.1 | Account view with card/customer data aggregation |

---

## 5. Top 10 Programs by Inter-Program Dependencies

Dependencies include CALL targets, XCTL targets, and copybook-mediated data sharing.

| Rank | Program | Outgoing Deps | Incoming Deps | Total | Details |
|------|---------|--------------|---------------|-------|---------|
| 1 | COACTUPC.cbl | 2 (XCTL: COMEN01C; CPY: CSUTLDPY) | 3 (COMEN01C, COCRDLIC, CBADMCDJ) | 5 | Central account update hub |
| 2 | COMEN01C.cbl | 12 (XCTL to all menu targets) | 14 (all online programs return here) | 26 | Central navigation hub for regular users |
| 3 | COSGN00C.cbl | 2 (XCTL: COMEN01C, COADM01C) | 13 (all programs can return to sign-on) | 15 | Authentication gateway |
| 4 | COADM01C.cbl | 7 (XCTL: COSGN00C + 6 admin options) | 5 (admin programs return here) | 12 | Admin navigation hub |
| 5 | CBSTM03A.CBL | 1 (CALL: CBSTM03B ×12) | 1 (CREASTMT.JCL) | 2 | Tight coupling with subroutine |
| 6 | COCRDLIC.cbl | 3 (XCTL: COMEN01C, COCRDSLC, COCRDUPC) | 1 (COMEN01C) | 4 | Card list → detail/update navigation |
| 7 | COTRN00C.cbl | 2 (XCTL: COMEN01C, COTRN01C) | 1 (COMEN01C) | 3 | Transaction list → detail navigation |
| 8 | COUSR00C.cbl | 3 (XCTL: COADM01C, COUSR02C, COUSR03C) | 1 (COADM01C) | 4 | User list → edit/delete navigation |
| 9 | CORPT00C.cbl | 2 (CALL: CSUTLDTC ×2; XCTL: COMEN01C) | 1 (COMEN01C) | 3 | Online-to-batch bridge (submits JCL) |
| 10 | CBTRN01C.cbl | 1 (CALL: CEE3ABD) | 1 (POSTTRAN.jcl) | 2 | First step in daily posting pipeline |

---

## 6. Composite Hotspot Score

Weighted composite ranking (LOC: 25%, Copybooks: 20%, I/O: 20%, Logic Density: 20%, Dependencies: 15%):

| Rank | Program | LOC Rank | Cpy Rank | I/O Rank | Logic Rank | Dep Rank | Composite Score | Risk Level |
|------|---------|----------|----------|----------|------------|----------|----------------|------------|
| **1** | **COACTUPC.cbl** | 1 | 1 | 3 | 1 | 1 | **1.00** | **CRITICAL** |
| **2** | **COTRTLIC.cbl** | 2 | 9 | 2 | 2 | — | **0.78** | **HIGH** |
| **3** | **COCRDUPC.cbl** | 4 | 5 | — | 3 | — | **0.68** | **HIGH** |
| **4** | **COCRDLIC.cbl** | 5 | 7 | 10 | 5 | 6 | **0.63** | **HIGH** |
| **5** | **CBSTM03A.CBL** | 9 | — | 1 | — | 5 | **0.55** | **MEDIUM** |
| **6** | **COTRTUPC.cbl** | 3 | 8 | 7 | 6 | — | **0.53** | **MEDIUM** |
| **7** | **CBTRN02C.cbl** | — | — | 6 | 4 | — | **0.48** | **MEDIUM** |
| **8** | **COACTVWC.cbl** | 8 | 3 | — | 10 | — | **0.45** | **MEDIUM** |
| **9** | **COPAUA0C.cbl** | 7 | 2 | — | — | — | **0.42** | **MEDIUM** |
| **10** | **CBACT04C.cbl** | — | — | 8 | 8 | — | **0.38** | **MEDIUM** |

---

## 7. Modernization Recommendations

### Priority 1 — CRITICAL: Modernize First

#### COACTUPC.cbl (Account Update)
- **Why first**: Largest program (4,236 LOC), highest business logic density (188 decision points), most copybook references (56), touches the core account record used by the entire application. This is the single highest-risk, highest-value program to modernize.
- **Complexity drivers**: Field-by-field validation of ~20 account/customer fields using lookup tables (phone area codes, state codes, ZIP prefixes), date validation via reusable copybook, CICS VSAM I/O with cross-entity reads (account→card xref→customer).
- **Risk**: Any bug here affects all account data. Extensive use of COPY REPLACING (CSSETATY) makes static analysis difficult.
- **Recommendation**: Decompose into validation service, persistence layer, and UI controller. Extract CSSETATY template logic into a reusable validation framework.

### Priority 2 — HIGH: Modernize Early

#### COTRTLIC.cbl / COTRTUPC.cbl (Transaction Type List/Update)
- **Why early**: These are DB2 programs (2,098 / 1,702 LOC) that already use SQL, making them natural candidates for migration to a modern database access layer. High I/O counts reflect DB2 cursor management that maps directly to JPA/JDBC patterns.
- **Recommendation**: Migrate to Spring Data JPA with DB2-compatible entities. The cursor-based pagination maps to Spring Pageable.

#### COCRDUPC.cbl / COCRDLIC.cbl (Card Update/List)
- **Why early**: High logic density (90/79 decision points) with significant cross-entity validation. Card operations are central to the business domain and share data structures with account and customer programs.
- **Recommendation**: Modernize as part of an Account-Card bounded context. The CICS STARTBR/READNEXT browse pattern maps to indexed queries.

### Priority 3 — MEDIUM: Core Batch Pipeline

#### CBSTM03A.CBL + CBSTM03B.CBL (Statement Generation)
- **Why**: Highest I/O count (158 operations), uses advanced COBOL features (2D arrays, ALTER/GO TO, COMP-3, subroutine calls). The CALL-based architecture already separates I/O from business logic, simplifying migration.
- **Recommendation**: Rewrite as a Spring Batch job with a template engine (Thymeleaf/FreeMarker) for the HTML output path.

#### CBTRN02C.cbl (Transaction Posting) / CBACT04C.cbl (Interest Calculation)
- **Why**: Core batch business logic — transaction posting with multi-file updates and interest computation with discount group rules. These are the financial engine of the application.
- **Recommendation**: Implement as Spring Batch steps with transactional integrity. Interest calculation rules should become a configurable rules engine.

### Priority 4 — LOW: Navigation and Simple CRUD

#### COSGN00C.cbl, COMEN01C.cbl, COADM01C.cbl (Sign-on, Menus)
- **Why last**: These are navigation-only programs with minimal business logic. They route users to functional programs and manage session state.
- **Recommendation**: Replace with a web framework routing layer (Spring MVC/Security). COMMAREA-based session management maps to HTTP session.

#### COUSR00C–COUSR03C (User CRUD)
- **Why last**: Simple CRUD operations on a single VSAM file with standard CICS patterns. These follow a consistent template that can be code-generated.
- **Recommendation**: Auto-generate REST API endpoints from the SEC-USER-DATA copybook definition.

---

## 8. Key Risk Factors for Modernization

| Risk Factor | Affected Programs | Mitigation |
|-------------|------------------|------------|
| COPY REPLACING (CSSETATY) | All 16+ online programs | Build a reusable validation framework to replace template expansion |
| CICS pseudo-conversational model | All online programs | Map to stateless REST APIs with session tokens |
| VSAM indexed file access | All programs | Migrate to relational DB with indexed columns matching VSAM keys |
| IMS DL/I hierarchical DB | Authorization sub-app (5 programs) | Flatten IMS segments into relational tables |
| MQ messaging | Authorization + VSAM-MQ sub-apps | Map to JMS/Spring Messaging or event-driven architecture |
| GDG (Generation Data Groups) | TRANBKP, COMBTRAN | Replace with timestamped files or database versioning |
| LE services (CEEDAYS, CEE3ABD) | 12+ programs | Replace with Java date APIs and exception handling |
| Internal Reader (TDQ) batch submit | CORPT00C | Replace with job scheduler API (Spring Batch Admin / Quartz) |
