# HOTSPOT REPORT — CardDemo COBOL Estate

> **Generated:** 2026-06-16 | **Programs Analyzed:** 44 | **Scoring Methodology:** Weighted multi-factor

---

## Table of Contents

1. [Scoring Methodology](#scoring-methodology)
2. [Top 10 Hotspot Programs](#top-10-hotspot-programs)
3. [Detailed Program Profiles](#detailed-program-profiles)
4. [Modernization Priority Recommendations](#modernization-priority-recommendations)
5. [Risk Assessment Matrix](#risk-assessment-matrix)
6. [Recommended Migration Waves](#recommended-migration-waves)

---

## Scoring Methodology

Each program is scored across five dimensions (total possible = 100):

| Dimension | Weight | Metric | Scoring |
|-----------|:------:|--------|---------|
| **Lines of Code** | 25 | Raw LOC count | > 2000 = 25, > 1000 = 20, > 500 = 15, > 200 = 10, else = 5 |
| **Copybooks Referenced** | 20 | Count of COPY statements | > 15 = 20, > 10 = 16, > 7 = 12, > 4 = 8, else = 4 |
| **I/O Operations** | 20 | READ + WRITE + REWRITE + DELETE + STARTBR + READNEXT/PREV + ENDBR + OPEN + CLOSE + EXEC SQL + DL/I calls | > 20 = 20, > 10 = 16, > 5 = 12, > 2 = 8, else = 4 |
| **Business Logic Density** | 20 | Count of EVALUATE + IF statements (proxy for nesting complexity) | > 100 = 20, > 50 = 16, > 25 = 12, > 10 = 8, else = 4 |
| **Inter-Program Dependencies** | 15 | CALL + XCTL + LINK (outgoing) + programs that call/link to this one (incoming) | > 8 = 15, > 5 = 12, > 3 = 9, > 1 = 6, else = 3 |

---

## Top 10 Hotspot Programs

| Rank | Program | LOC | Copybooks | I/O Ops | Logic (IF/EVAL) | Dependencies | **Score** | Classification |
|:----:|---------|----:|:---------:|:-------:|:----------------:|:------------:|:---------:|----------------|
| **1** | **COACTUPC.cbl** | 4,236 | 56 | 14 | 188 | 3 out + 8 in | **96** | Online (CICS) |
| **2** | **COTRTLIC.cbl** | 2,098 | 11 | 21 | 121 | 2 out + 2 in | **88** | Online (CICS/DB2) |
| **3** | **COTRTUPC.cbl** | 1,702 | 13 | 21 | 128 | 1 out + 2 in | **88** | Online (CICS/DB2) |
| **4** | **COCRDUPC.cbl** | 1,560 | 15 | 10 | 164 | 1 out + 2 in | **85** | Online (CICS) |
| **5** | **COCRDLIC.cbl** | 1,459 | 13 | 14 | 140 | 3 out + 2 in | **85** | Online (CICS) |
| **6** | **COPAUA0C.cbl** | 1,026 | 16 | 12 | 61 | 4 out + 2 in | **83** | Online (CICS/IMS/MQ) |
| **7** | **COPAUS0C.cbl** | 1,032 | 14 | 10 | 47 | 2 out + 2 in | **80** | Online (CICS/IMS) |
| **8** | **CBSTM03A.CBL** | 924 | 4 | 117 | 33 | 14 out + 1 in | **79** | Batch |
| **9** | **COACTVWC.cbl** | 941 | 15 | 8 | 67 | 1 out + 2 in | **77** | Online (CICS) |
| **10** | **COTRN02C.cbl** | 783 | 10 | 15 | 40 | 4 out + 2 in | **73** | Online (CICS) |

### Honorable Mentions (11–15)

| Rank | Program | LOC | Score | Notes |
|:----:|---------|----:|:-----:|-------|
| 11 | CBTRN02C.cbl | 731 | 71 | Batch — core transaction posting |
| 12 | COTRN00C.cbl | 699 | 68 | Online — transaction list browse |
| 13 | COUSR00C.cbl | 695 | 68 | Online — user list browse |
| 14 | CBACT04C.cbl | 652 | 68 | Batch — interest calculation |
| 15 | CBTRN03C.cbl | 649 | 67 | Batch — transaction report |

---

## Detailed Program Profiles

### #1 — COACTUPC.cbl (Account Update) — Score: 96

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 4,236 | **Largest in estate** — nearly 2× the next largest |
| Copybooks | 56 COPY statements (18 unique; CSSETATY used 38× via COPY REPLACING) | Highest in estate |
| I/O Operations | 5 EXEC CICS READ + REWRITE operations across 3 VSAM files | Moderate |
| Logic Density | 188 IF/EVALUATE statements | **Highest** — deep validation logic |
| Dependencies | XCTL to COMEN01C; called from COMEN01C; reads ACCTFILE, CARDXREF, CUSTFILE | Moderate but central |

**Why it's #1:** This program concentrates the most complex business validation in the estate. It validates dates (CCYYMMDD with leap-year awareness via CSUTLDPY), SSN format, phone numbers against state area codes (via CSLKPCDY — 1,318-line lookup table), US state codes, and ZIP code prefixes. The 38× COPY REPLACING pattern for screen attribute management is an unusual technique that complicates static analysis. The sheer size (4,236 LOC) makes it the highest-risk single migration target.

**Modernization Strategy:** Decompose into AccountUpdateService + AccountValidationService. Extract CSLKPCDY lookup into a reference data service or static configuration. Implement Bean Validation (JSR 380) annotations for field-level validation.

---

### #2 — COTRTLIC.cbl (Transaction Type List) — Score: 88

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 2,098 | Second largest |
| Copybooks | 11 (includes CSDB2RPY, CSDB2RWY — DB2 procedures) | Moderate |
| I/O Operations | 21 (DB2 cursor operations: DECLARE, OPEN, FETCH, CLOSE) | High |
| Logic Density | 121 IF/EVALUATE | High — cursor-based pagination logic |
| Dependencies | XCTL to COTRTUPC; DB2 TRANSACTION_TYPE table | DB2-coupled |

**Why it's #2:** Cursor-based DB2 pagination is non-trivial to migrate correctly. The program implements manual forward/backward paging over a DB2 result set — a pattern that maps directly to Spring Data JPA `Pageable` but requires careful translation of cursor state management.

**Modernization Strategy:** Spring Data JPA with `Page<TransactionType>` + Spring MVC pagination. DB2 tables map directly to JPA entities — this is the most straightforward DB access to migrate.

---

### #3 — COTRTUPC.cbl (Transaction Type Update) — Score: 88

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 1,702 | Third largest |
| Copybooks | 13 | Moderate |
| I/O Operations | 21 (DB2 SELECT, INSERT, UPDATE, DELETE) | High |
| Logic Density | 128 IF/EVALUATE | High — cascading delete logic |
| Dependencies | XCTL from COTRTLIC; DB2 TRANSACTION_TYPE + TRANSACTION_CATEGORY | DB2-coupled |

**Why it's #3:** Contains cascading delete logic — deleting a transaction type also removes dependent transaction categories. This maps to JPA `@OneToMany(cascade = CascadeType.ALL)` or explicit service-layer cascade.

---

### #4 — COCRDUPC.cbl (Credit Card Update) — Score: 85

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 1,560 | Fourth largest |
| Copybooks | 15 (unique) | High |
| I/O Operations | 10 (CICS READ/REWRITE on CARDFILE, CUSTFILE) | Moderate |
| Logic Density | 164 IF/EVALUATE | Very High — field validation |
| Dependencies | XCTL from COCRDLIC | Low |

**Modernization Strategy:** Similar pattern to COACTUPC but for card entity. Migrate as CardUpdateService with Bean Validation.

---

### #5 — COCRDLIC.cbl (Credit Card List) — Score: 85

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 1,459 | |
| Copybooks | 13 | Moderate |
| I/O Operations | 14 (STARTBR/READNEXT/READPREV/ENDBR browse pattern) | Moderate |
| Logic Density | 140 IF/EVALUATE | High — browse/pagination |
| Dependencies | XCTL to COCRDSLC, COCRDUPC, COMEN01C | Hub-like |

**Modernization Strategy:** VSAM STARTBR/READNEXT pagination → Spring Data JPA `Pageable` with cursor-based keyset pagination for the card list view.

---

### #6 — COPAUA0C.cbl (Authorization Decision) — Score: 83

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 1,026 | |
| Copybooks | 16 (MQ + IMS + VSAM copybooks — most diverse) | **Most diverse subsystem mix** |
| I/O Operations | 12 (MQ OPEN/GET/PUT + IMS DL/I GU/SCHD/TERM + CICS READ) | Multi-subsystem |
| Logic Density | 61 IF/EVALUATE | Moderate |
| Dependencies | Spans CICS + IMS + MQ — reads MQ queue, queries IMS DB, accesses VSAM | **Highest integration risk** |

**Why it's #6 (but highest risk):** This is the single most architecturally complex program. It spans three subsystems (CICS, IMS, MQ) in a single transaction. The authorization decision logic reads from an MQ request queue, validates against account/card/customer data via IMS, and posts a response. Migration requires coordinating Spring JMS, JPA (for IMS-to-relational mapping), and REST API layers.

**Modernization Strategy:** Implement as an event-driven authorization microservice using Spring JMS/AMQP for message consumption, Spring Data JPA for the IMS database (mapped to 2 relational tables: auth_summary + auth_detail), and REST API for the response.

---

### #7 — COPAUS0C.cbl (Authorization Summary View) — Score: 80

IMS browse pattern (GU + GNP) for paginated authorization summary display. Tightly coupled to COPAUS1C and COPAUS2C — must migrate as a unit.

### #8 — CBSTM03A.CBL (Statement Generation) — Score: 79

| Metric | Value | Assessment |
|--------|-------|------------|
| LOC | 924 | |
| Copybooks | 4 | Low |
| I/O Operations | 117 (file OPEN/CLOSE/READ/WRITE across multiple files) | **Highest I/O in estate** |
| Logic Density | 33 IF/EVALUATE | Moderate |
| Dependencies | CALL CBSTM03B (tightly coupled subroutine) | Must migrate pair |

**Why it's notable:** Generates both plain-text and HTML output from transaction data. Uses ALTER/GO-TO (non-structured programming) and 2D arrays. The 117 I/O operations reflect intensive multi-file processing.

**Modernization Strategy:** Spring Batch job with Thymeleaf or Jasper Reports for HTML statement generation.

### #9 — COACTVWC.cbl (Account View) — Score: 77

Read-only view joining account + customer + card data. Lower risk than update programs.

### #10 — COTRN02C.cbl (Transaction Add) — Score: 73

Creates new transaction records with card/account validation and auto-generated sequence numbers.

---

## Modernization Priority Recommendations

### Priority 1: DB2 Programs (Lowest Risk, Quickest Win)

**Programs:** COTRTLIC (#2), COTRTUPC (#3), COBTUPDT  
**Rationale:**
- Already use SQL (DB2) — closest to modern relational paradigm
- Direct mapping to Spring Data JPA entities
- Cursor-based pagination → `Pageable` interface
- Cascading deletes → JPA cascade annotations
- **Estimated effort:** 2–3 weeks for 2 developers

### Priority 2: User Security Module (Quick Win + Security Fix)

**Programs:** COSGN00C, COUSR00C, COUSR01C, COUSR02C, COUSR03C  
**Rationale:**
- Simple CRUD operations on user security file
- ⚠️ Passwords stored in plain text — critical security risk
- Small programs (260–695 LOC) — low complexity
- Migration enables Spring Security integration (bcrypt hashing, RBAC)
- **Estimated effort:** 1–2 weeks for 2 developers

### Priority 3: Card Module (Medium Complexity)

**Programs:** COCRDLIC (#5), COCRDSLC, COCRDUPC (#4)  
**Rationale:**
- Well-bounded VSAM operations (single entity focus)
- Browse pattern (STARTBR/READNEXT) maps to cursor pagination
- Moderate validation logic
- **Estimated effort:** 3–4 weeks for 2 developers

### Priority 4: Transaction Module (Core Business Logic)

**Programs:** COTRN00C, COTRN01C, COTRN02C (#10), COBIL00C  
**Batch:** CBTRN01C, CBTRN02C, CBTRN03C  
**Rationale:**
- Core revenue-generating functionality
- Batch pipeline (daily posting) is high-value automation
- Multiple file interactions but well-understood patterns
- **Estimated effort:** 4–6 weeks for 2 developers

### Priority 5: Account Update (Highest Complexity)

**Programs:** COACTUPC (#1), COACTVWC (#9)  
**Rationale:**
- COACTUPC is the largest and most complex program in the estate
- 38× COPY REPLACING pattern requires careful decomposition
- 1,318-line state/ZIP/phone validation lookup
- Should be deferred until team has migration experience from earlier waves
- **Estimated effort:** 4–5 weeks for 2 developers

### Priority 6: Batch Pipeline (Phased)

**Programs:** CBACT04C (interest calc), CBSTM03A/B (#8, statement gen), CBEXPORT, CBIMPORT  
**Rationale:**
- Spring Batch framework provides good pattern mapping (JCL → Step/Tasklet)
- Statement generation requires HTML template engine
- Export/import involves multi-record polymorphic structure (CVEXPORT.cpy)
- **Estimated effort:** 4–6 weeks for 2 developers

### Priority 7: Authorization Module (Highest Integration Risk)

**Programs:** COPAUA0C (#6), COPAUS0C (#7), COPAUS1C, COPAUS2C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS  
**Rationale:**
- Spans three subsystems (CICS + IMS + MQ)
- IMS hierarchical model requires relational mapping (2 tables)
- MQ integration requires Spring JMS/AMQP adapter
- Must migrate COPAUS0C/1C/2C as a unit (LINK chain)
- **Estimated effort:** 5–7 weeks for 3 developers

---

## Risk Assessment Matrix

| Risk Factor | Programs Affected | Impact | Mitigation |
|-------------|-------------------|--------|------------|
| **Plain-text passwords** | CSUSR01Y / COUSR* | Critical (security) | Implement bcrypt hashing in Priority 2 |
| **COPY REPLACING pattern** | COACTUPC (38×) | High (parsing) | Manual decomposition; extract to validation methods |
| **IMS DL/I → relational mapping** | COPAUA0C, COPAUS0C/1C, CBPAUP0C, PAUDBLOD, PAUDBUNL, DBUNLDGS | High (structural) | Map IMS segments to 2 JPA entities (summary + detail) |
| **MQ integration** | COPAUA0C, COACCT01, CODATE01 | Medium (infrastructure) | Spring JMS/AMQP adapter |
| **ALTER/GO-TO** | CBSTM03A | Medium (code quality) | Refactor to structured control flow |
| **Assembler dependencies** | COBDATFT, MVSWAIT | Low (replaceable) | `java.time` + `Thread.sleep()` |
| **GDG versioning** | TRANBKP JCL | Low (infrastructure) | Timestamped file naming / S3 versioning |
| **EBCDIC data files** | `app/data/` | Low (one-time) | Convert during data migration |
| **PCI-sensitive data** | CARD-CVV-CD, CARD-NUM | Medium (compliance) | Encryption at rest, tokenization |

---

## Recommended Migration Waves

```
Wave 1 (Weeks 1–4): Foundation
├── Priority 2: User/Security Module (5 programs, 2,057 LOC)
├── Priority 1: DB2 Transaction Types (3 programs, 4,037 LOC)
└── Deliverable: Auth + Reference Data services running

Wave 2 (Weeks 5–10): Core CRUD
├── Priority 3: Card Module (3 programs, 3,906 LOC)
├── Priority 4: Transaction Online (4 programs, 2,384 LOC)
└── Deliverable: Card + Transaction services running

Wave 3 (Weeks 11–16): Complex Programs
├── Priority 5: Account Update (2 programs, 5,177 LOC)
├── Priority 4b: Transaction Batch (3 programs, 1,874 LOC)
└── Deliverable: Account service + batch pipeline

Wave 4 (Weeks 17–24): Integration & Batch
├── Priority 6: Statement Gen + Export/Import (4 programs, 2,223 LOC)
├── Priority 7: Authorization Module (8 programs, 4,979 LOC)
└── Deliverable: Full estate migrated

Wave 5 (Weeks 25–28): Hardening
├── Performance testing
├── Data migration (VSAM → RDBMS, IMS → RDBMS)
├── BMS screen replacement with modern UI
└── Deliverable: Production-ready Java application
```

**Total Estimated Duration:** 6–7 months with a 4-person team (2 pairs)  
**Total LOC to Migrate:** ~27,350 COBOL → estimated ~40,000 Java (1.5× expansion factor)

---

*End of Hotspot Report*
