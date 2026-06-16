# HOTSPOT REPORT — AWS CardDemo

> Identification of high-complexity, high-risk programs for modernization prioritization.

---

## 1. Lines of Code Ranking

| Rank | Program | LOC | Classification | Sub-System |
|------|---------|-----|----------------|------------|
| 1 | COACTUPC.cbl | 4,236 | Online (CICS) | Main |
| 2 | COTRTLIC.cbl | 2,098 | Online (CICS+DB2) | Transaction Type |
| 3 | COTRTUPC.cbl | 1,702 | Online (CICS+DB2) | Transaction Type |
| 4 | COCRDUPC.cbl | 1,560 | Online (CICS) | Main |
| 5 | COCRDLIC.cbl | 1,459 | Online (CICS) | Main |
| 6 | COPAUS0C.cbl | 1,032 | Online (CICS+IMS) | Authorization |
| 7 | COPAUA0C.cbl | 1,026 | Online (CICS+IMS+DB2+MQ) | Authorization |
| 8 | COACTVWC.cbl | 941 | Online (CICS) | Main |
| 9 | CBSTM03A.CBL | 924 | Batch | Main |
| 10 | COCRDSLC.cbl | 887 | Online (CICS) | Main |
| 11 | COTRN02C.cbl | 783 | Online (CICS) | Main |
| 12 | CBTRN02C.cbl | 731 | Batch | Main |
| 13 | COTRN00C.cbl | 699 | Online (CICS) | Main |
| 14 | COUSR00C.cbl | 695 | Online (CICS) | Main |
| 15 | CBACT04C.cbl | 652 | Batch | Main |

---

## 2. Copybook Reference Count

| Rank | Program | Copybooks Referenced | Count |
|------|---------|---------------------|-------|
| 1 | COACTUPC.cbl | COCOM01Y, CSUTLDWY, CVCRD01Y, CSLKPCDY, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT03Y, CVCUS01Y, CSSTRPFY, CSSETATY, CSUTLDPY | 15 |
| 2 | COACTVWC.cbl | COCOM01Y, CVCRD01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CSSTRPFY | 12 |
| 3 | COCRDSLC.cbl | COCOM01Y, CVCRD01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY | 10 |
| 4 | COCRDUPC.cbl | COCOM01Y, CVCRD01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSMSG02Y, CSUSR01Y, CVACT02Y, CVCUS01Y, CSSTRPFY | 10 |
| 5 | COCRDLIC.cbl | COCOM01Y, CVCRD01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CSUSR01Y, CVACT02Y, CSSTRPFY | 8 |
| 6 | COBIL00C.cbl | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVACT01Y, CVACT03Y, CVTRA05Y | 7 |
| 7 | COTRN02C.cbl | COCOM01Y, COTTL01Y, CSDAT01Y, CSMSG01Y, CVTRA05Y, CVACT01Y, CVACT03Y | 7 |
| 8 | CBEXPORT.cbl | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT | 6 |
| 7 | CBIMPORT.cbl | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT | 6 |
| 8 | CBTRN01C.cbl | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y | 6 |
| 9 | COPAUA0C.cbl | COCOM01Y, CCPAURQY, CCPAURLY, CIPAUSMY, CIPAUDTY, IMSFUNCS | 6 |
| 10 | CBTRN02C.cbl | CVTRA06Y, CVTRA05Y, CVACT03Y, CVACT01Y, CVTRA01Y | 5 |

---

## 3. I/O Operations Density

| Rank | Program | I/O Operations | Type | Description |
|------|---------|---------------|------|-------------|
| 1 | CBSTM03A.CBL | ~115 | Batch | Multi-file reads (XREF, CUST, ACCT, TRAN) + dual-format writes (text + HTML) |
| 2 | COACTUPC.cbl | ~45 | Online | READ/REWRITE on 5 VSAM files (ACCTDAT, CUSTDAT, CARDDAT, CARDAIX, CXACAIX) |
| 3 | CBTRN02C.cbl | ~35 | Batch | READ DALYTRAN, READ XREFFILE, I-O ACCTFILE+TCATBALF, WRITE TRANFILE+DALYREJS |
| 4 | CBACT04C.cbl | ~30 | Batch | READ TCATBALF+XREFFILE+DISCGRP, I-O ACCTFILE, WRITE TRANSACT |
| 5 | COCRDLIC.cbl | ~28 | Online | STARTBR/READNEXT/READPREV/ENDBR on CARDDAT+CARDAIX (paginated browse) |
| 6 | CBEXPORT.cbl | ~25 | Batch | READ 5 input files, WRITE 1 multi-record export file |
| 7 | CBTRN03C.cbl | ~22 | Batch | READ TRANFILE+CARDXREF+TRANTYPE+TRANCATG+DATEPARM, WRITE TRANREPT |
| 8 | COPAUS0C.cbl | ~20 | Online | IMS GU/GN/GNP browse across summary+detail segments |
| 9 | COTRN00C.cbl | ~18 | Online | STARTBR/READNEXT/READPREV on TRANSACT (paginated) |
| 10 | COBIL00C.cbl | ~15 | Online | READ ACCTDAT+CXACAIX, WRITE TRANSACT, REWRITE ACCTDAT |

---

## 4. Business Logic Density

Measured by: EVALUATE/IF nesting depth, COMPUTE statements, and validation logic.

| Rank | Program | Branching Stmts | COMPUTEs | Key Logic |
|------|---------|----------------|----------|-----------|
| 1 | COACTUPC.cbl | ~359 | ~25 | Exhaustive field validation: SSN, phone (NANPA), date, state code, ZIP prefix; 3× COPY REPLACING for screen attributes; multi-entity update (account + customer) |
| 2 | CBTRN02C.cbl | ~85 | ~12 | Transaction validation pipeline: card lookup → account verify → balance check → category balance update → reject handling |
| 3 | CBACT04C.cbl | ~72 | ~18 | Interest rate computation: DISCGRP lookup → rate×balance÷365 → transaction generation; multi-category processing loop |
| 4 | COPAUA0C.cbl | ~65 | ~8 | Authorization decision: MQ request parse → IMS lookup → credit/cash limit check → approve/decline → MQ response |
| 5 | COTRTLIC.cbl | ~60 | ~5 | DB2 cursor management: OPEN/FETCH/CLOSE with pagination state; concurrent browse + update coordination |
| 6 | COCRDUPC.cbl | ~55 | ~6 | State machine: VIEW→EDIT→CONFIRM→SAVE workflow; field-level change tracking |
| 7 | COTRTUPC.cbl | ~50 | ~4 | Cascading delete logic: verify no dependent transactions before type/category removal |
| 8 | CBSTM03A.CBL | ~48 | ~15 | Statement formatting: date range filtering, running totals, page breaks, dual-output (text + HTML) |
| 9 | COTRN02C.cbl | ~42 | ~8 | Transaction creation: card validation → account lookup → amount verification → timestamp generation |
| 10 | COCRDLIC.cbl | ~38 | ~3 | Paginated browse: forward/backward navigation, page boundary detection, alternate index selection |

---

## 5. Inter-Program Dependencies

| Rank | Program | Outgoing Deps | Incoming Deps | Total | Role |
|------|---------|--------------|---------------|-------|------|
| 1 | COMEN01C | 11 outgoing XCTL | 1 (from COSGN00C) | 12 | Central user hub |
| 2 | COADM01C | 6 outgoing XCTL | 1 (from COSGN00C) | 7 | Admin hub |
| 3 | COACTUPC | 0 outgoing | 1 (from COMEN01C) | 1 | Leaf (but 5 VSAM file dependencies) |
| 4 | COPAUS0C | 2 outgoing LINK | 1 (from COMEN01C) | 3 | IMS authorization chain head |
| 5 | COUSR00C | 2 outgoing XCTL | 1 (from COADM01C) | 3 | User list (routes to update/delete) |
| 6 | COCRDLIC | 2 outgoing XCTL | 1 (from COMEN01C) | 3 | Card list (routes to view/update) |
| 7 | COTRN00C | 1 outgoing XCTL | 1 (from COMEN01C) | 2 | Transaction list (routes to view) |
| 8 | CBSTM03A | 1 outgoing CALL | 0 | 1 | Statement gen (calls CBSTM03B) |
| 9 | CBACT01C | 1 outgoing CALL | 0 | 1 | Account read (calls COBDATFT) |
| 10 | CORPT00C | 1 outgoing CALL | 1 (from COMEN01C) | 2 | Report (calls CSUTLDTC + submits JCL) |

---

## 6. Composite Hotspot Score

Weighted formula: `Score = (LOC/100)*0.3 + (Copybooks)*2.0 + (I/O_Ops)*0.5 + (Branching/10)*1.5 + (Dependencies)*1.0`

| Rank | Program | LOC | CPY | I/O | Branch | Deps | **Score** |
|------|---------|-----|-----|-----|--------|------|-----------|
| 1 | **COACTUPC.cbl** | 4,236 | 15 | 45 | 359 | 6 | **74.8** |
| 2 | **CBTRN02C.cbl** | 731 | 5 | 35 | 85 | 6 | **35.5** |
| 3 | **COTRTLIC.cbl** | 2,098 | 6 | 18 | 60 | 7 | **33.6** |
| 4 | **CBACT04C.cbl** | 652 | 5 | 30 | 72 | 1 | **29.7** |
| 5 | **CBSTM03A.CBL** | 924 | 5 | 115 | 48 | 1 | **28.2** |
| 6 | **COPAUA0C.cbl** | 1,026 | 6 | 20 | 65 | 3 | **27.5** |
| 7 | **COCRDUPC.cbl** | 1,560 | 10 | 15 | 55 | 2 | **26.4** |
| 8 | **COCRDLIC.cbl** | 1,459 | 8 | 28 | 38 | 4 | **24.1** |
| 9 | **COTRTUPC.cbl** | 1,702 | 6 | 15 | 50 | 7 | **23.8** |
| 10 | **COACTVWC.cbl** | 941 | 12 | 12 | 30 | 2 | **22.3** |

---

## 7. Modernization Recommendations

### Priority 1 — Modernize First (Highest ROI + Risk Reduction)

#### COACTUPC.cbl — Account Update (Score: 74.8)

**Why first:**
- Largest program in estate (4,236 LOC) — single biggest maintenance burden
- Highest complexity (359 branching statements, 15 copybooks)
- Violates Single Responsibility Principle: manages both Account AND Customer updates
- Uses 3× COPY REPLACING for CSSETATY screen attribute macros (no direct Java equivalent)
- Touches 5 VSAM files simultaneously

**Modernization approach:**
- Split into: `AccountService`, `CustomerService`, `ValidationService`, `AccountController` (REST API)
- Extract validation rules (SSN, phone, date, state, ZIP) into reusable Java utility classes
- Replace BMS screen → REST endpoints + modern UI
- Generate 10,000+ test cases from production data before rewrite

---

#### CBSTM03A.CBL — Statement Generation (Score: 28.2)

**Why early:**
- **Best pilot candidate** — self-contained batch, no CICS dependencies, clear input/output
- Highest I/O operation count (115) — exercises file access patterns
- Generates both text and HTML output — validates output formatting approach
- Calls CBSTM03B submodule — tests inter-program call modernization

**Modernization approach:**
- Spring Batch job with `FlatFileItemReader` (VSAM) + `ItemProcessor` (formatting) + `ItemWriter` (text/HTML)
- Thymeleaf templates for HTML statement output
- First program to validate end-to-end batch migration patterns

---

### Priority 2 — High Complexity, Contained Scope

#### CBTRN02C.cbl — Transaction Posting Engine (Score: 35.5)

**Why:**
- Core daily business process (POSTTRAN pipeline)
- Complex validation: card lookup → account verify → balance check → multi-file update
- Updates 4 datasets atomically (ACCTFILE, TCATBALF, TRANFILE, DALYREJS)
- Highest batch business logic density

**Modernization approach:**
- Spring Batch step with transaction management (database ACID replaces VSAM file coordination)
- JPA entities for Account, Transaction, TransactionCategoryBalance
- Validation pipeline as a chain of `ItemProcessor` beans

---

#### CBACT04C.cbl — Interest Calculator (Score: 29.7)

**Why:**
- Critical financial logic — errors have direct monetary impact
- Complex computation: rate lookup by group/type/category → daily interest → balance update
- Reads from 4 files, writes to 2 — exercises multi-source batch pattern

**Modernization approach:**
- Spring Batch job: `JdbcCursorItemReader` (tran_cat_balance) → `InterestProcessor` (BigDecimal arithmetic) → `CompositeItemWriter` (update account + create transaction)
- **MANDATORY:** Use `java.math.BigDecimal` for all monetary calculations

---

### Priority 3 — Cross-Subsystem Integration

#### COPAUA0C.cbl — Authorization Decision (Score: 27.5)

**Why:**
- Spans 3 subsystems (CICS + IMS + MQ + DB2) — most architecturally complex
- COPAUS0C/COPAUS1C/COPAUS2C form a chain — must migrate as a unit (7 programs total)
- Real-time message flow with external systems

**Modernization approach:**
- Amazon SQS (replacing MQ) + Spring JMS adapter
- IMS hierarchical data → 2 relational tables (auth_summary, auth_detail)
- Migrate all 7 authorization programs together as `authorization-service` microservice

---

#### COTRTLIC.cbl + COTRTUPC.cbl — Transaction Type CRUD (Score: 33.6 + 23.8)

**Why:**
- Already use DB2 — closest to modern SQL patterns, lowest migration friction
- Cursor-based pagination maps directly to Spring Data paging
- Good validation of DB2→PostgreSQL migration patterns

**Modernization approach:**
- Spring Data JPA with `Pageable` interface
- DB2 SQL translates with minimal changes to PostgreSQL
- `transaction-type-service` microservice

---

### Priority 4 — Modernize Last (Simple Batch)

#### CBEXPORT.cbl + CBIMPORT.cbl — Data Migration (Score: Low)

**Why last:**
- Well-contained scope (export reads 5 files → 1 output; import reverses)
- Good candidate for modernization as standalone microservice or CLI tool
- May become unnecessary once all data is in PostgreSQL

**Modernization approach:**
- Spring Batch with CSV/JSON output (replacing EBCDIC 500-byte records)
- Eventually retire once mainframe decommissioned

---

## 8. Risk Summary

| Risk Factor | Programs Affected | Severity | Mitigation |
|-------------|-------------------|----------|------------|
| COACTUPC size/complexity | 1 program, but central to account management | HIGH | Split into 3-4 services; extensive test generation |
| IMS hierarchical data | 7 programs (authorization chain) | MEDIUM | Map to 2 relational tables; migrate as unit |
| MQ real-time integration | 4 programs | MEDIUM | Amazon SQS + Spring JMS; parallel run |
| BMS-coupled business logic | 21 CICS programs | MEDIUM | Extract logic layer before UI migration |
| Plain-text passwords | CSUSR01Y (all user programs) | HIGH | Spring Security + bcrypt; immediate security improvement |
| COPY REPLACING macros | COACTUPC (3 occurrences) | LOW | Convert to parameterized utility methods |
| Assembler dependencies | COBDATFT, MVSWAIT | LOW | Replace with Java DateFormatter, Thread.sleep |
| COMP-3/Packed Decimal | IMS copybooks (CIPAUSMY, CIPAUDTY) | LOW | BigDecimal with explicit scale; verify byte alignment |

---

## 9. Estimated Modernization Effort

| Phase | Programs | Estimated Duration | Team Size |
|-------|----------|-------------------|-----------|
| P0: Foundation (DB schema, shared libs) | — | 3-4 weeks | 4 |
| P1: user-auth + reporting pilot | COSGN00C, COUSR*, CBSTM03A/B, CBTRN03C | 4-5 weeks | 4 |
| P2: transaction-type + card service | COTRTLIC, COTRTUPC, COCRDLIC/SLC/UPC | 5-7 weeks | 4 |
| P3: account + transaction service | COACTUPC, COACTVWC, COTRN*, COBIL00C, CBTRN02C | 8-10 weeks | 4 |
| P4: authorization (IMS/MQ) | COPAUA0C, COPAUS0C-2C, CBPAUP0C, PAUDBLOD/UNL | 6-8 weeks | 4 |
| P5: remaining batch + data migration | CBACT04C, CBEXPORT, CBIMPORT, CBACT01-03C | 4-5 weeks | 4 |
| P6: decommission + cutover | — | 2-3 weeks | 4 |
| **Total** | **44 programs** | **32-42 weeks** | **4-person team** |
