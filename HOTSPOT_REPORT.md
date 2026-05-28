# HOTSPOT REPORT — CardDemo Modernization Priorities

> Top programs ranked by complexity, dependency weight, and business criticality.
> Use this report to sequence a modernization roadmap.

---

## 1. Composite Hotspot Ranking — Top 10

Programs scored across five dimensions. Rank 1 = highest modernization priority.

| Rank | Program | LOC | Copybooks | I/O Ops | Logic Density | Inter-Deps | Composite Score | Classification |
|------|---------|-----|-----------|---------|---------------|------------|-----------------|----------------|
| 1 | **COACTUPC** | 4,236 | 18 (+38 COPY REPLACING) | 3 VSAM + 1 BMS | 33 EVALUATE / 213 IF | Called by COMEN01C; calls none | **98** | Online (CICS) |
| 2 | **COTRTLIC** | 2,098 | 0 (inline SQL) | 3 DB2 ops + 1 BMS | 26 EVALUATE / 138 IF | Called by COADM01C; uses DB2 | **82** | Online (CICS+DB2) |
| 3 | **COTRTUPC** | 1,702 | 0 (inline SQL) | 5 DB2 ops + 1 BMS | 17 EVALUATE / 109 IF | Called by COADM01C; uses DB2 | **76** | Online (CICS+DB2) |
| 4 | **COCRDUPC** | 1,560 | 10 | 2 VSAM + 1 BMS | 13 EVALUATE / 92 IF | Called by COMEN01C, COCRDLIC | **71** | Online (CICS) |
| 5 | **COCRDLIC** | 1,459 | 9 | 4 VSAM browse + 1 BMS | 9 EVALUATE / 72 IF | Called by COMEN01C; XCTLs to COCRDSLC/COCRDUPC | **67** | Online (CICS) |
| 6 | **COPAUA0C** | 1,026 | 8 | 4 MQ + IMS DL/I | 10 EVALUATE / 26 IF | MQ queue integration; IMS + CICS | **65** | Online (CICS+IMS+MQ) |
| 7 | **CBSTM03A** | 924 | 4 | 4 VSAM (via CBSTM03B) + 2 output | 5 EVALUATE / 31 IF | CALLs CBSTM03B ×13; ALTER/GO TO | **62** | Batch |
| 8 | **COACTVWC** | 941 | 13 | 3 VSAM + 1 BMS | 8 EVALUATE / 24 IF | Called by COMEN01C | **58** | Online (CICS) |
| 9 | **CBTRN02C** | 731 | 5 | 3 VSAM R + 3 VSAM W/U | 6 EVALUATE / 38 IF | Core posting; feeds TCATBALF + TRANSACT | **57** | Batch |
| 10 | **COTRN02C** | 783 | 8 | 4 VSAM + 1 BMS | 4 EVALUATE / 27 IF | Called by COMEN01C; CALLs CSUTLDTC | **54** | Online (CICS) |

### Scoring Methodology

Each dimension is normalized 0–20 and summed for a composite score out of 100:

| Dimension | Weight | Scoring |
|-----------|--------|---------|
| **Lines of Code** | 20 | Linear scale: 4,236 LOC = 20, 100 LOC = 1 |
| **Copybook Count** | 20 | 18+ = 20, 0 = 5 (inline SQL = moderate), proportional |
| **I/O Operations** | 20 | Count of distinct file/DB/MQ/BMS operations |
| **Logic Density** | 20 | (EVALUATE count × 2 + IF count) / LOC × normalization factor |
| **Inter-Program Dependencies** | 20 | Sum of: programs that call this + programs this calls + technology layers (CICS/IMS/MQ/DB2) |

---

## 2. Dimension-by-Dimension Rankings

### 2.1 Lines of Code (LOC)

| Rank | Program | LOC | Classification |
|------|---------|-----|----------------|
| 1 | COACTUPC | 4,236 | Online (CICS) |
| 2 | COTRTLIC | 2,098 | Online (CICS+DB2) |
| 3 | COTRTUPC | 1,702 | Online (CICS+DB2) |
| 4 | COCRDUPC | 1,560 | Online (CICS) |
| 5 | COCRDLIC | 1,459 | Online (CICS) |
| 6 | COPAUS0C | 1,032 | Online (CICS+IMS) |
| 7 | COPAUA0C | 1,026 | Online (CICS+IMS+MQ) |
| 8 | COACTVWC | 941 | Online (CICS) |
| 9 | CBSTM03A | 924 | Batch |
| 10 | COCRDSLC | 887 | Online (CICS) |

### 2.2 Copybook References

| Rank | Program | Count | Key Copybooks |
|------|---------|-------|---------------|
| 1 | COACTUPC | 18 unique (38 CSSETATY inclusions) | CVACT01Y, CVCUS01Y, CVACT03Y, CVCRD01Y, CSSETATY, CSSTRPFY, CSUTLDWY, CSUTLDPY |
| 2 | COACTVWC | 13 | CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y, CVCRD01Y |
| 3 | COCRDUPC | 10 | CVACT02Y, CVCUS01Y, CVCRD01Y |
| 4 | COCRDLIC | 9 | CVACT02Y, CVCRD01Y |
| 5 | COADM01C | 9 | COCOM01Y, COADM02Y, CSUSR01Y |
| 6 | COPAUA0C | 8 | CMQODV, CMQMDV, CIPAUSMY, CIPAUDTY, CCPAURQY, CCPAURLY |
| 7 | COPAUS0C | 8 | CIPAUSMY, CIPAUDTY, IMSFUNCS |
| 8 | COTRN02C | 8 | CVTRA05Y, CVACT01Y, CVACT03Y |
| 9 | CBTRN01C | 6 | CVTRA06Y, CVCUS01Y, CVACT03Y, CVACT02Y, CVACT01Y, CVTRA05Y |
| 10 | CBEXPORT | 6 | CVCUS01Y, CVACT01Y, CVACT03Y, CVTRA05Y, CVACT02Y, CVEXPORT |

### 2.3 I/O Operations Count

| Rank | Program | File/DB Ops | Details |
|------|---------|-------------|---------|
| 1 | CBACT04C | 5 VSAM (4 read + 1 write GDG) | TCATBALF, XREFFILE (+AIX), ACCTFILE, DISCGRP → TRANSACT(+1) |
| 2 | CBEXPORT | 6 (5 read + 1 write) | CUSTFILE, ACCTFILE, XREFFILE, TRANSACT, CARDFILE → EXPFILE |
| 3 | CBIMPORT | 7 (1 read + 6 write) | EXPFILE → CUSTOUT, ACCTOUT, XREFOUT, TRNXOUT, CARDOUT, ERROUT |
| 4 | CBTRN02C | 6 VSAM (3 read + 3 write/update) | DALYTRAN, XREFFILE, ACCTFILE → TRANFILE, DALYREJS, TCATBALF |
| 5 | CBTRN01C | 6 VSAM (6 read) | DALYTRAN, CUSTFILE, XREFFILE, CARDFILE, ACCTFILE, TRANFILE |
| 6 | CBSTM03A | 6 (4 read + 2 write) | XREFFILE, CUSTFILE, ACCTFILE, TRNXFILE → STMTFILE, HTMLFILE |
| 7 | CBTRN03C | 6 (5 read + 1 write) | TRANFILE, CARDXREF, TRANTYPE, TRANCATG, DATEPARM → TRANREPT |
| 8 | COPAUA0C | 7+ (4 MQ + IMS DL/I) | MQOPEN×1, MQGET×1, MQPUT1×1, MQCLOSE×1 + IMS DL/I calls |
| 9 | COACCT01 | 6+ (6 MQ + VSAM) | MQOPEN×3, MQGET×1, MQPUT×2, MQCLOSE×3, READ ACCTFILE |
| 10 | COTRN02C | 5 VSAM + 1 BMS | CARDXREF (AIX), TRANSACT (browse+write), ACCTFILE |

### 2.4 Business Logic Density (EVALUATE/IF nesting)

| Rank | Program | EVALUATE | IF | Density (per 100 LOC) | Key Logic |
|------|---------|----------|----|-----------------------|-----------|
| 1 | COACTUPC | 33 | 213 | 5.8 | Field-level validation for all account fields, SSN, dates, amounts |
| 2 | COTRTLIC | 26 | 138 | 7.8 | DB2 cursor management, page navigation, delete confirmation |
| 3 | COTRTUPC | 17 | 109 | 6.8 | DB2 CRUD operations with validation |
| 4 | COCRDUPC | 13 | 92 | 6.7 | Card field validation, name, status, expiry |
| 5 | COCRDLIC | 9 | 72 | 5.6 | Paginated list with forward/backward scroll logic |
| 6 | CBTRN02C | 6 | 38 | 6.0 | Transaction validation, rejection routing, balance update |
| 7 | CBSTM03A | 5 | 31 | 3.9 | Statement formatting, multi-file merge, ALTER/GO TO |
| 8 | COTRN02C | 4 | 27 | 4.0 | Transaction entry validation |
| 9 | COPAUA0C | 10 | 26 | 3.5 | MQ message handling, auth decision tree |
| 10 | COUSR00C | 8 | 22 | 3.6 | User list pagination, delete confirmation |

### 2.5 Inter-Program Dependencies

| Rank | Program | Calls Out | Called By | Technology Layers | Total Deps |
|------|---------|-----------|----------|-------------------|------------|
| 1 | COPAUA0C | MQOPEN, MQGET, MQPUT1, MQCLOSE, CBLTDLI | COMEN01C (via COPAUS0C chain) | CICS + IMS + MQ (3 layers) | 8 |
| 2 | COACCT01 | MQOPEN×3, MQGET, MQPUT×2, MQCLOSE×3 | CICS trigger | CICS + MQ + VSAM (3 layers) | 7 |
| 3 | CBSTM03A | CBSTM03B (×13 calls), CEE3ABD | CREASTMT.JCL | Batch + sub-call (2 layers) | 6 |
| 4 | COACTUPC | none (inline CICS) | COMEN01C | CICS + VSAM (2 layers) | 5 |
| 5 | CBTRN02C | CEE3ABD | POSTTRAN.jcl | Batch + multi-file (2 layers) | 5 |
| 6 | CBACT04C | CEE3ABD | INTCALC.jcl | Batch + multi-file + AIX (2 layers) | 5 |
| 7 | CORPT00C | CSUTLDTC | COMEN01C | CICS + internal reader (submits JCL) | 4 |
| 8 | COTRN02C | CSUTLDTC | COMEN01C | CICS + VSAM + AIX (2 layers) | 4 |
| 9 | COPAUS0C | CBLTDLI | COMEN01C; XCTLs COPAUS1C | CICS + IMS (2 layers) | 4 |
| 10 | COCRDLIC | none | COMEN01C; XCTLs COCRDSLC, COCRDUPC | CICS + VSAM browse (2 layers) | 4 |

---

## 3. Modernization Recommendations

### 3.1 Priority 1: COACTUPC (Account Update) — Score 98

**Why modernize first:**
- **Largest program** in the estate (4,236 LOC) — highest effort but highest impact
- **Highest business logic density** (33 EVALUATE, 213 IF) — complex validation rules for SSN, dates, amounts, state codes
- **Most copybook dependencies** (18 unique, 38 COPY REPLACING) — centralizes field attribute management
- **Core business function** — account management is fundamental to the credit card system
- **Representative of CICS patterns** — if this program is successfully modernized, the patterns can be reused for 16 other CICS programs

**Key risks:** Heavy use of CSSETATY COPY REPLACING (38 inclusions with different field names), deep procedural nesting, BMS map integration.

**Recommended approach:** Extract validation rules into a reusable service layer first. Convert BMS screen to REST API + web frontend. Decompose into Account Service (CRUD) + Validation Service.

### 3.2 Priority 2: CBTRN02C (Transaction Posting) — Score 57

**Why modernize second:**
- **Core batch pipeline** — sits at the heart of daily transaction processing
- **Multi-file I/O** (6 VSAM operations) — writes to 3 different files including the master transaction file
- **Business-critical validation** — validates cards, accounts, and posts transactions
- **Data integrity nexus** — any error here corrupts transaction and account balance data
- **Feeds TCATBALF** — which feeds the monthly interest calculation (CBACT04C)

**Recommended approach:** Convert to a transaction posting microservice with database transactions replacing VSAM I/O. Implement idempotent processing and event sourcing.

### 3.3 Priority 3: CBSTM03A/CBSTM03B (Statement Generation) — Score 62

**Why modernize third:**
- **Multi-program architecture** (CBSTM03A calls CBSTM03B 13 times) — tests sub-program call patterns
- **Legacy coding patterns** — uses ALTER/GO TO (self-modifying code), 2D arrays via PERFORM VARYING
- **Customer-facing output** — produces statements in text and HTML
- **Good modernization candidate** — can be replaced with a template engine (PDF/HTML) consuming account data from a modern data store

**Recommended approach:** Replace with a report/template microservice. Statement layout moves from COBOL formatting to a template engine (e.g., Thymeleaf, Jasper). CBSTM03B's file access becomes database queries.

### 3.4 Priority 4: COTRTLIC + COTRTUPC (Transaction Type DB2 Programs) — Score 82/76

**Why modernize fourth:**
- **Already use DB2** — closest to modern relational data access patterns
- **High LOC** (2,098 + 1,702 = 3,800 combined) — but inline SQL simplifies migration
- **No copybook dependencies** — self-contained, minimal coupling
- **DB2 to modern RDBMS** is the easiest migration path in the estate

**Recommended approach:** Direct SQL migration from DB2 to PostgreSQL/MySQL. CICS BMS screens become a simple CRUD web UI. These programs validate the DB2 migration strategy.

### 3.5 Priority 5: COPAUA0C (Authorization Processing) — Score 65

**Why modernize fifth:**
- **Highest technology complexity** — CICS + IMS + MQ (3 middleware layers)
- **MQ message integration** — request/reply pattern maps well to modern async messaging
- **IMS hierarchical data** — requires data model transformation to relational
- **Fraud detection** — PA-AUTH-FRAUD, PA-MATCH-STATUS fields indicate business rules for fraud detection

**Recommended approach:** Decompose into Authorization Service (handles IMS-to-RDBMS data) + Message Handler (MQ-to-Kafka/RabbitMQ). This is the most complex migration and benefits from patterns established in earlier priorities.

---

## 4. Modernization Wave Plan

| Wave | Programs | Effort | Dependencies | Success Criteria |
|------|----------|--------|-------------|-----------------|
| **Wave 1** (Foundation) | COACTUPC + shared copybooks (COCOM01Y, CVCRD01Y, CSSETATY) | High | None — defines patterns | Account CRUD via REST API |
| **Wave 2** (Core Batch) | CBTRN02C, CBACT04C, CBTRN01C | High | Wave 1 data model | Daily posting + interest calc via batch service |
| **Wave 3** (Reporting) | CBSTM03A/B, CBTRN03C | Medium | Wave 2 transaction data | PDF/HTML statements from modern data store |
| **Wave 4** (DB2 → RDBMS) | COTRTLIC, COTRTUPC, COBTUPDT | Medium | Independent | DB2 transaction types in modern RDBMS |
| **Wave 5** (IMS/MQ) | COPAUA0C, COPAUS0C/1C/2C, CBPAUP0C | High | Wave 1 + Wave 2 | Authorization via modern messaging + RDBMS |
| **Wave 6** (Remaining CICS) | COACTVWC, COCRDLIC/SLC/UPC, COTRN00C/01C/02C, COBIL00C, COUSR00C–03C, COADM01C, COMEN01C, COSGN00C, CORPT00C | Medium | Wave 1 patterns | All online functions via web UI |
| **Wave 7** (Utilities) | CBEXPORT/IMPORT, CBACT01-03C, CBCUS01C, remaining batch | Low | Wave 6 | Data migration + utilities |
| **Wave 8** (Infra) | JCL → modern scheduler, VSAM → RDBMS migration | Medium | All waves | Decommission mainframe |

---

## 5. Risk Factors

| Risk | Impact | Mitigation |
|------|--------|-----------|
| CSSETATY COPY REPLACING (38× in COACTUPC) | Compile-time code generation — no runtime equivalent | Extract to configuration-driven attribute map |
| ALTER/GO TO in CBSTM03A | Self-modifying control flow — no modern equivalent | Refactor to explicit state machine before migration |
| IMS hierarchical data model | DL/I segments don't map directly to tables | Design normalized relational schema from segment hierarchy |
| VSAM KSDS key structure | Composite keys with REDEFINES | Map to database composite primary keys + indexes |
| GDG (Generation Data Groups) | Historical file versioning | Replace with database temporal tables or versioned storage |
| BMS 3270 screen maps | No web equivalent for field-level attributes | Replace with responsive web forms; map attribute bytes to CSS |
| COMP/COMP-3 packed decimal | Binary/packed numeric storage | Use BigDecimal or equivalent in target language |
| 88-level conditions | COBOL-specific validation idiom | Convert to enum classes or validation constants |
