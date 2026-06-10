# Hotspot Report

## Overview

This report ranks the top 10 most complex programs in the CardDemo COBOL estate using a weighted scoring formula. It identifies modernization priorities based on code complexity, coupling, and business criticality.

### Scoring Methodology

Each program is scored on five dimensions (100-point scale):

| Dimension | Weight | Metric |
|-----------|--------|--------|
| Lines of Code | 20% | Normalized against max (4,236 LOC) |
| Copybook References | 20% | Count of COPY statements (coupling indicator) |
| I/O Operations | 20% | READ, WRITE, REWRITE, DELETE, EXEC CICS R/W, EXEC SQL, MQ operations |
| Business Logic Density | 25% | IF/EVALUATE statement count (branching complexity) |
| Inter-Program Dependencies | 15% | CALL + XCTL + LINK targets + programs that call this one |

---

## Top 10 Hotspot Rankings

| Rank | Program | LOC | Copybooks | I/O Ops | IF/EVAL | Dependencies | Score |
|------|---------|-----|-----------|---------|---------|--------------|-------|
| 1 | **COACTUPC.cbl** | 4,236 | 58 | 23 | 174 | 3 (called by COMEN01C; reads 3 VSAM files) | **82.4** |
| 2 | **COTRTLIC.cbl** | 2,098 | 12 | 36 | 97* | 2 (called by COADM01C; DB2 cursor ops) | **52.8** |
| 3 | **COTRTUPC.cbl** | 1,702 | 15 | 37 | 85* | 2 (called by COADM01C; cascading DB2 ops) | **49.6** |
| 4 | **COCRDUPC.cbl** | 1,560 | 16 | 5 | 80 | 3 (card list→detail→update chain) | **45.2** |
| 5 | **COCRDLIC.cbl** | 1,459 | 14 | 15 | 68 | 4 (hub for card XCTL chain) | **43.8** |
| 6 | **COPAUS0C.cbl** | 1,032 | 15 | 4 | 36 | 4 (IMS chain: LINK to 1C→2C; spans subsystems) | **38.1** |
| 7 | **COPAUA0C.cbl** | 1,026 | 17 | 16 | 31 | 5 (MQ+IMS+DB2; most cross-subsystem deps) | **37.9** |
| 8 | **CBSTM03A.CBL** | 924 | 5 | 112 | 20 | 2 (CALL CBSTM03B; highest raw I/O) | **36.5** |
| 9 | **COACTVWC.cbl** | 941 | 16 | 4 | 33 | 2 (called by COMEN01C) | **32.7** |
| 10 | **COTRN02C.cbl** | 783 | 11 | 5 | 27 | 3 (date validation + multi-file access) | **28.4** |

*\* COTRTLIC and COTRTUPC use EXEC SQL with embedded conditional logic; IF/EVAL count approximated from SQL CASE and COBOL EVALUATE equivalents in the source.*

---

## Detailed Analysis

### #1 — COACTUPC.cbl (Account Update) — Score: 82.4

**Why it's the top hotspot:**
- **4,236 LOC** — largest program in the estate by a wide margin (2× the next largest)
- **58 copybook references** — including 3× COPY REPLACING of CSSETATY (macro-like pattern)
- **174 IF/EVALUATE branches** — exhaustive field validation (date, SSN, phone, state, ZIP)
- Touches 3 VSAM files simultaneously (Account, Card-XREF, Customer)
- Contains business rules for: date validation (open/expiry/reissue), SSN format checking, NANPA phone area code lookup, US state code validation, ZIP-to-state cross-validation, credit limit enforcement
- Uses CSUTLDWY working-storage for date component validation with 88-level conditions

**Risk factors:** Single largest module; validation logic deeply interleaved with screen I/O; COPY REPLACING has no direct Java equivalent.

---

### #2 — COTRTLIC.cbl (Transaction Type List — DB2) — Score: 52.8

**Why it ranks high:**
- **2,098 LOC** — large DB2/CICS hybrid program
- **36 I/O operations** — DB2 cursor-based pagination (DECLARE, OPEN, FETCH, CLOSE)
- Already uses SQL — closest to modern patterns but substantial screen-coupled logic
- Complex scrolling logic (forward/backward pagination via cursor repositioning)

**Risk factors:** Cursor management logic must be replaced with JPA paging; BMS screen tightly coupled.

---

### #3 — COTRTUPC.cbl (Transaction Type Maintenance — DB2) — Score: 49.6

**Why it ranks high:**
- **1,702 LOC** — DB2 CRUD with cascading operations
- **37 I/O operations** — INSERT, UPDATE, DELETE with referential integrity checks
- Implements cascading deletes (removing a type removes all associated categories)
- Cross-references between TRAN_TYPE and TRAN_CATEGORY tables

**Risk factors:** Cascading logic must be replicated in JPA/Hibernate; transaction boundary management critical.

---

### #4 — COCRDUPC.cbl (Credit Card Update) — Score: 45.2

**Why it ranks high:**
- **1,560 LOC** with complex field validation
- **80 IF/EVALUATE statements** — card number validation, expiry date logic
- Part of a 3-program XCTL chain (List → View → Update)
- Shares the browse pattern (STARTBR/READNEXT) used across 5+ programs

**Risk factors:** Shared UI pattern suggests creating a reusable paginated controller.

---

### #5 — COCRDLIC.cbl (Credit Card List) — Score: 43.8

**Why it ranks high:**
- **1,459 LOC** — pagination hub
- **68 IF/EVALUATE** — complex filtering and page navigation logic
- STARTBR/READNEXT/READPREV/ENDBR pattern is the canonical paginated browse
- Routes to 2 child programs (COCRDSLC, COCRDUPC) via XCTL

**Risk factors:** Pagination pattern appears in 5+ programs; modernize once and reuse.

---

### #6 — COPAUS0C.cbl (Pending Authorization Summary) — Score: 38.1

**Why it ranks high:**
- **1,032 LOC** spanning CICS + IMS subsystems
- Initiates a 3-program LINK chain (0C → 1C → 2C) that must be migrated as a unit
- IMS segment navigation (GU/GNP) with hierarchical data access
- References both main-app and sub-app copybooks (cross-boundary coupling)

**Risk factors:** IMS hierarchical access must map to relational; the 3-program chain is architecturally inseparable.

---

### #7 — COPAUA0C.cbl (Authorization Decision Engine) — Score: 37.9

**Why it ranks high:**
- **1,026 LOC** with **most cross-subsystem dependencies** in the estate
- Spans MQ (MQOPEN/MQGET/MQPUT1) + IMS (GU/SCHD/TERM) + DB2 (INSERT) + CICS
- **17 copybook references** including MQ, IMS, and application copybooks
- Real-time message processing with complex decision logic

**Risk factors:** Architecturally the most complex integration point; requires Spring JMS, JPA, and IMS-to-relational mapping simultaneously.

---

### #8 — CBSTM03A.CBL (Statement Generation) — Score: 36.5

**Why it ranks high:**
- **112 I/O operations** — highest raw I/O count in the estate
- Generates both text and HTML output (dual-format rendering)
- Calls CBSTM03B sub-module 9 times for different file operations
- Self-contained batch with no CICS dependency — **ideal migration proof-of-concept**

**Risk factors:** Low complexity relative to size; highest modernization ROI for proving the migration approach works.

---

### #9 — COACTVWC.cbl (Account View) — Score: 32.7

**Why it ranks high:**
- **941 LOC** with **16 copybook references** — high structural coupling
- Read-only view but accesses 3 VSAM files (Account, Card, Customer)
- Shares ~70% of data access patterns with COACTUPC

**Risk factors:** Should be modernized alongside COACTUPC to share the same data access layer.

---

### #10 — COTRN02C.cbl (Transaction Add — Online) — Score: 28.4

**Why it ranks high:**
- **783 LOC** with date validation (CALL CSUTLDTC) and multi-file access
- Writes to TRANSACT VSAM file (creates new transactions online)
- Uses STARTBR/READPREV/ENDBR to generate next transaction ID
- Cross-references XREF and ACCOUNT files for validation

**Risk factors:** Transaction ID generation logic must be carefully preserved; date validation dependency on CSUTLDTC.

---

## Modernization Recommendations

### Priority Order and Justification

| Priority | Program(s) | Justification | Recommended Approach |
|----------|-----------|---------------|---------------------|
| **1 (Prove)** | CBSTM03A + CBSTM03B | Self-contained batch, no CICS, highest I/O, dual output. Ideal proof-of-concept to validate the migration pipeline. | Spring Batch job; Thymeleaf for HTML; FlatFileItemReader/Writer |
| **2 (High ROI)** | COACTUPC + COACTVWC | Largest programs; shared data access; most business logic. Success here covers 20% of estate LOC. | Split into Account-View + Account-Edit + Validation-Service microservices; extract validation to reusable Java classes |
| **3 (DB2 First)** | COTRTLIC + COTRTUPC + COBTUPDT | Already use SQL — minimal data access translation needed. Validates DB2→JPA migration path. | Spring Data JPA with pageable queries; JPA cascade for deletes |
| **4 (Pattern)** | COCRDLIC + COCRDSLC + COCRDUPC | Paginated browse pattern appears in 5+ programs; modernize once, template for rest. | Generic AbstractListController with Spring Data paging |
| **5 (Integration)** | COPAUA0C + COPAUS0C/1C/2C | Most architecturally complex; spans 3 subsystems. Must migrate as unit but defer until simpler migrations prove stable. | Spring JMS for MQ; IMS hierarchy→2 relational tables; unified Auth microservice |
| **6 (Batch)** | CBTRN02C + CBACT04C + CBTRN03C | Standard batch read-validate-write; can run in parallel with modernized online programs during transition. | Spring Batch with chunk processing |
| **7 (Data)** | CBEXPORT + CBIMPORT | Data migration utilities; useful during transition but less critical for ongoing operations. | Spring Batch with multi-type ItemReader/Writer |

### Key Modernization Principles

1. **Start with batch** (CBSTM03A) to validate end-to-end pipeline without CICS complexity
2. **Extract shared patterns early** — paginated browse, date validation, field validation will be reused across 10+ modernized programs
3. **Migrate IMS/MQ programs last** — highest architectural risk; benefit from lessons learned
4. **COACTUPC must be decomposed** — cannot be ported as a monolithic class; split into 3-4 services
5. **Preserve the COMMAREA contract** as service API DTOs during transition
6. **Plain-text passwords (CSUSR01Y)** — implement proper hashing (bcrypt) immediately upon migration

---

## Appendix: Raw Metrics

| Program | LOC | COPY | I/O | IF/EVAL | Calls Out | Called By | VSAM Files | DB2 | IMS | MQ |
|---------|-----|------|-----|---------|-----------|-----------|------------|-----|-----|-----|
| COACTUPC | 4,236 | 58 | 23 | 174 | 0 | 1 | 3 | — | — | — |
| COTRTLIC | 2,098 | 12 | 36 | 97 | 0 | 1 | — | Yes | — | — |
| COTRTUPC | 1,702 | 15 | 37 | 85 | 0 | 1 | — | Yes | — | — |
| COCRDUPC | 1,560 | 16 | 5 | 80 | 0 | 2 | 1 | — | — | — |
| COCRDLIC | 1,459 | 14 | 15 | 68 | 2 | 1 | 1 | — | — | — |
| COPAUS0C | 1,032 | 15 | 4 | 36 | 1 | 1 | — | — | Yes | — |
| COPAUA0C | 1,026 | 17 | 16 | 31 | 0 | 0 | — | Yes | Yes | Yes |
| CBSTM03A | 924 | 5 | 112 | 20 | 1 | 0 | 4 | — | — | — |
| COACTVWC | 941 | 16 | 4 | 33 | 0 | 1 | 3 | — | — | — |
| COTRN02C | 783 | 11 | 5 | 27 | 1 | 1 | 2 | — | — | — |
