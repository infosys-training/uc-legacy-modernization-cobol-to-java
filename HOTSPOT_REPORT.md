# Hotspot Report — CardDemo COBOL Estate

> Top programs ranked by complexity, dependency, and modernization priority.

---

## 1. Metric Definitions

| Metric | Description | How Measured |
|--------|------------|--------------|
| **LOC** | Lines of code | `wc -l` on source file |
| **Copybooks** | Number of COPY statements | Count of `COPY` directives |
| **I/O Ops** | Total I/O operations | Count of READ, WRITE, REWRITE, DELETE, EXEC CICS SEND/RECEIVE, EXEC SQL, CALL MQ* |
| **Logic Density** | Business logic complexity | Count of EVALUATE + IF/END-IF blocks |
| **Dependencies** | Inter-program links | Count of programs that CALL, XCTL, or LINK to this program + programs it calls/transfers to |

---

## 2. Top 10 Programs by Lines of Code

| Rank | Program | LOC | Classification |
|------|---------|-----|----------------|
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

## 3. Top 10 Programs by Copybook References

| Rank | Program | Copybooks | Key Copybooks |
|------|---------|-----------|---------------|
| 1 | COACTUPC.cbl | 58 | CSSETATY (×3 via COPY REPLACING), COCOM01Y, CVCRD01Y, CVACT01Y, etc. |
| 2 | COPAUA0C.cbl | 17 | CMQODV, CMQMDV, CMQV, CCPAURQY, CIPAUSMY, CIPAUDTY |
| 3 | COACTVWC.cbl | 16 | COCOM01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 4 | COCRDSLC.cbl | 16 | COCOM01Y, CVCRD01Y, CVACT02Y, CVCUS01Y |
| 5 | COCRDUPC.cbl | 16 | COCOM01Y, CVCRD01Y, CVACT02Y, CVCUS01Y |
| 6 | COPAUS0C.cbl | 15 | COCOM01Y, CIPAUSMY, CIPAUDTY, CVACT01Y, CVCUS01Y |
| 7 | COTRTUPC.cbl | 15 | COCOM01Y, CVCRD01Y, DCLTRTYP, DCLTRCAT (DB2 SQL INCLUDE) |
| 8 | COCRDLIC.cbl | 14 | COCOM01Y, CVCRD01Y, CVACT02Y |
| 9 | COTRTLIC.cbl | 12 | COCOM01Y, CVCRD01Y, CSDB2RWY, DCLTRTYP (DB2 SQL INCLUDE) |
| 10 | COBIL00C.cbl | 11 | COCOM01Y, CVACT01Y, CVACT03Y, CVTRA05Y |

---

## 4. Top 10 Programs by I/O Operations

| Rank | Program | Total I/O | Breakdown |
|------|---------|-----------|-----------|
| 1 | CBSTM03A.CBL | 115 | R:4, W:97, CALL:14 (97 WRITEs for statement generation lines) |
| 2 | COTRTUPC.cbl | 40 | R:1, W:1, D:28, S:2, RV:1, SQL:7 (28 DELETEs for cascading DB2 cleanup) |
| 3 | COTRTLIC.cbl | 34 | R:4, D:10, S:3, RV:1, SQL:16 (16 SQL cursor operations) |
| 4 | CBPAUP0C.cbl | 21 | R:12, D:9 (IMS segment reads and deletes) |
| 5 | COPAUA0C.cbl | 16 | R:8, W:4, CALL:4 (MQ + CICS + IMS reads) |
| 6 | COACTUPC.cbl | 14 | R:8, W:3, S:2, RV:1 (multi-file account update) |
| 7 | CBACT01C.cbl | 13 | R:2, W:9, CALL:2 (account file splitter) |
| 8 | COCRDLIC.cbl | 12 | R:8, S:3, RV:1 (paginated browse with STARTBR/READNEXT) |
| 9 | CBEXPORT.cbl | 12 | R:6, W:5, CALL:1 (5-file export) |
| 10 | CBTRN02C.cbl | 12 | R:6, W:5, CALL:1 (transaction posting) |

---

## 5. Top 10 Programs by Business Logic Density

| Rank | Program | EVALUATE | IF/END-IF | Total | Key Logic |
|------|---------|----------|-----------|-------|-----------|
| 1 | COACTUPC.cbl | 20 | 339 | 359 | Field-by-field validation (date, SSN, phone, state, ZIP); multi-screen flow control |
| 2 | COTRTLIC.cbl | 32 | 184 | 216 | DB2 cursor navigation; inline update/delete with multi-row selection |
| 3 | COCRDUPC.cbl | 16 | 151 | 167 | Card field validation; expiry date checking; status transitions |
| 4 | COCRDLIC.cbl | 18 | 131 | 149 | Paginated browse logic; forward/backward navigation; selection handling |
| 5 | COTRTUPC.cbl | 26 | 108 | 134 | DB2 CRUD with cascading deletes; delete confirmation workflow |
| 6 | CBTRN02C.cbl | 0 | 96 | 96 | Transaction validation: card status, account balance, credit limit checks |
| 7 | CBACT04C.cbl | 0 | 86 | 86 | Interest calculation: rate lookup, prorating, fee computation, balance update |
| 8 | CBTRN03C.cbl | 4 | 75 | 79 | Report formatting: page breaks, subtotals, control breaks by account |
| 9 | COCRDSLC.cbl | 8 | 72 | 80 | Card detail display with multi-file lookup and error handling |
| 10 | COPAUS0C.cbl | 22 | 50 | 72 | IMS browse with cursor positioning; multi-page navigation; sync points |

---

## 6. Top 10 Programs by Inter-Program Dependencies

Programs ranked by total number of direct connections (calls in + calls out + transfers in + transfers out):

| Rank | Program | Total Deps | Calls/Links In | Calls/Links Out | Details |
|------|---------|------------|---------------|----------------|---------|
| 1 | COMEN01C.cbl | 12 | 1 (from COSGN00C) | 11 (XCTL to all main screens) | Hub for all user navigation |
| 2 | COADM01C.cbl | 7 | 1 (from COSGN00C) | 6 (XCTL to admin screens) | Hub for admin navigation |
| 3 | COACTUPC.cbl | 3 | 1 (from COMEN01C) | 0 (self-contained screen) + reads 3 files | Central account update |
| 4 | COCRDLIC.cbl | 4 | 1 (from COMEN01C) | 2 (XCTL to COCRDSLC, COCRDUPC) + 1 (return) | Card list hub |
| 5 | COPAUA0C.cbl | 4 | 0 (MQ-triggered) | 4 (MQOPEN/GET/PUT1 + CICS reads + IMS) | Cross-subsystem hub |
| 6 | COUSR00C.cbl | 4 | 1 (from COADM01C) | 2 (XCTL to COUSR02C, COUSR03C) + 1 (return) | User list hub |
| 7 | COPAUS0C.cbl | 3 | 1 (from COMEN01C) | 1 (LINK COPAUS1C) + IMS/CICS | Auth summary |
| 8 | CBSTM03A.CBL | 2 | 0 (JCL direct) | 1 (CALL CBSTM03B) | Statement generation |
| 9 | COSGN00C.cbl | 3 | 0 (CICS entry point) | 2 (XCTL to COADM01C, COMEN01C) | Entry point |
| 10 | CBTRN02C.cbl | 2 | 0 (JCL direct) | reads 3 files, writes 3 | Core batch posting |

---

## 7. Composite Hotspot Score

Combined weighted score: **LOC (25%) + Copybooks (20%) + I/O Ops (15%) + Logic Density (25%) + Dependencies (15%)**

Normalized to 0–100 scale (100 = most complex/critical).

| Rank | Program | LOC Score | Copy Score | I/O Score | Logic Score | Dep Score | **Composite** |
|------|---------|-----------|-----------|-----------|-------------|-----------|---------------|
| 1 | **COACTUPC.cbl** | 100 | 100 | 12 | 100 | 25 | **74.8** |
| 2 | **COTRTLIC.cbl** | 50 | 21 | 30 | 60 | 17 | **39.6** |
| 3 | **COCRDUPC.cbl** | 37 | 28 | 7 | 47 | 17 | **29.5** |
| 4 | **COCRDLIC.cbl** | 34 | 24 | 10 | 42 | 33 | **29.1** |
| 5 | **COTRTUPC.cbl** | 40 | 26 | 35 | 37 | 17 | **32.0** |
| 6 | **CBSTM03A.CBL** | 22 | 9 | 100 | 11 | 17 | **28.2** |
| 7 | **COPAUS0C.cbl** | 24 | 26 | 9 | 20 | 25 | **21.0** |
| 8 | **COPAUA0C.cbl** | 24 | 29 | 14 | 18 | 33 | **22.5** |
| 9 | **COACTVWC.cbl** | 22 | 28 | 8 | 19 | 17 | **19.4** |
| 10 | **CBTRN02C.cbl** | 17 | 10 | 10 | 27 | 17 | **16.4** |

---

## 8. Modernization Recommendations

### Priority 1 — Modernize First (Highest Impact)

#### 1. COACTUPC.cbl (Account Update) — Composite Score: 74.8

**Why first:** This is the single largest and most complex program in the estate (4,236 LOC). It has the highest logic density (359 branching statements) due to exhaustive field-by-field validation of dates, SSN, phone numbers, states, and ZIP codes. It references 58 copybooks (including 3× COPY REPLACING for CSSETATY screen attribute macros). As the central account update screen, it touches 3 VSAM files (account, card-xref, customer).

**Modernization approach:**
- Extract the validation logic (date, SSN, phone, state, ZIP) into reusable Java validation classes
- Convert BMS screen interaction to a REST API with a modern UI framework
- The CSSETATY COPY REPLACING pattern maps well to a parameterized component pattern
- Consider splitting into separate Account View, Account Edit, and Validation Service components

#### 2. CBSTM03A.CBL (Statement Generation) — Composite Score: 28.2

**Why early:** Despite moderate LOC (924), it has the highest I/O count in the estate (115 operations) because it writes individual lines to statement files. It reads 4 cross-referenced files and generates both text and HTML output. This is a self-contained batch program with no CICS dependencies, making it the **easiest batch program to prove out a migration approach**.

**Modernization approach:**
- Convert to a Spring Batch job with ItemReader/ItemWriter pattern
- Replace file-based I/O with a template engine (Thymeleaf/FreeMarker) for HTML output
- Use as a pilot batch migration to validate the VSAM-to-database data access layer

### Priority 2 — Modernize Second (High Complexity, Contained Scope)

#### 3. COTRTLIC.cbl / COTRTUPC.cbl (Transaction Type CRUD — DB2)

**Why:** These programs already use DB2, making them the natural entry point for database modernization. COTRTLIC (2,098 LOC) has complex cursor-based pagination; COTRTUPC (1,702 LOC) has cascading delete logic. Together they form a complete CRUD boundary.

**Modernization approach:**
- Map DB2 tables to JPA entities (TRANSACTION_TYPE, TRANSACTION_CATEGORY)
- Convert CICS BMS screens to REST API + React/Angular components
- The DB2 SQL is already close to standard SQL and translates with minimal changes

#### 4. COCRDLIC.cbl / COCRDUPC.cbl (Credit Card List and Update)

**Why:** These are the second and third most complex CICS programs. COCRDLIC implements a paginated browse pattern (STARTBR/READNEXT/READPREV/ENDBR) that appears in 5+ programs — modernizing it establishes the reusable pagination pattern for all other list screens.

**Modernization approach:**
- Implement Spring Data paging/sorting for VSAM-to-DB list queries
- The browse pattern can become a generic AbstractListController
- Card update validation logic is simpler than account update

### Priority 3 — Modernize Third (Cross-Subsystem Integration)

#### 5. COPAUA0C.cbl (Authorization Decision Engine — IMS/MQ/CICS)

**Why:** This program spans 3 subsystems (CICS, IMS, MQ), making it the most architecturally complex integration point. It receives MQ messages, reads CICS VSAM files, queries IMS databases, and returns MQ responses. However, it should be modernized only after the core VSAM access patterns and MQ adapters are proven.

**Modernization approach:**
- Replace MQ with a message broker adapter (Spring JMS/AMQP)
- Replace IMS DL/I calls with a relational database (the hierarchical structure maps to 2 tables)
- COPAUS0C/COPAUS1C/COPAUS2C form a dependency chain and should migrate as a unit

### Priority 4 — Modernize Last (Simple Batch Programs)

#### 6–10. CBTRN02C, CBACT04C, CBTRN03C, CBEXPORT, CBIMPORT

These batch programs are structurally simple (sequential file processing with validation and accumulation). They have no CICS dependencies and follow standard read-validate-write patterns. They should be migrated last because:
- They are already well-isolated (JCL → program → files)
- They can run in parallel with modernized online programs during transition
- They map cleanly to Spring Batch steps

---

## 9. Risk Assessment

| Risk | Impact | Mitigation |
|------|--------|-----------|
| COACTUPC complexity (4,236 LOC) | High — single most complex module; if migration fails here, confidence drops | Break into 3–4 microservices; extensive test case generation before migration |
| IMS dependency (7 programs) | Medium — requires IMS-to-RDBMS migration | Migrate IMS data to PostgreSQL/Oracle with hierarchical-to-relational mapping |
| MQ integration (4 programs) | Medium — real-time message flow | Use Spring JMS adapter; test with MQ emulator during development |
| BMS screen logic (20 programs) | Medium — tightly coupled UI/business logic | Extract business logic first; replace BMS with API layer + modern UI |
| COPY REPLACING pattern (CSSETATY, CSUTLDPY) | Low — macro-like code generation | Convert to parameterized methods/decorators in Java |
| GDG (Generation Data Group) files | Low — versioned file management | Replace with timestamped file naming or database-based versioning |
| DB2 SQL (3 programs) | Low — already close to standard SQL | Direct translation with minimal changes; use Spring Data JPA |

---

## 10. Summary Metrics

| Metric | Value |
|--------|-------|
| Total programs analyzed | 44 |
| Total lines of COBOL | 27,350 |
| Programs with CICS dependency | 21 (48%) |
| Programs with DB2 dependency | 4 (9%) |
| Programs with IMS dependency | 7 (16%) |
| Programs with MQ dependency | 4 (9%) |
| Pure batch programs | 16 (36%) |
| Average LOC per program | 622 |
| Median LOC per program | 487 |
| Programs > 1,000 LOC | 8 (18%) |
| Programs with > 10 copybooks | 12 (27%) |
| Estimated modernization effort | 6–9 months (4-person team) |
