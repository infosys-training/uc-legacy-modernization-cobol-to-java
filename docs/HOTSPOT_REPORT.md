# Hotspot Report — CardDemo COBOL Estate

> Top programs ranked by complexity and modernization priority, derived from static analysis of `infosys-training/uc-legacy-modernization-cobol-to-java`.

---

## 1. Complexity Scoring Methodology

Each program is scored across four dimensions:

| Dimension | Weight | Measurement |
|-----------|--------|-------------|
| **Logic Density** | 30% | Lines of code (LOC), EVALUATE/IF nesting depth, PERFORM branches |
| **Copybook Coupling** | 25% | Number of COPY statements, data structure dependencies |
| **I/O Operations** | 25% | Number of READ/WRITE/REWRITE/DELETE/START operations, file count |
| **Inter-Program Dependencies** | 20% | XCTL/CALL targets, menu dispatch fan-out, COMMAREA usage |

**Score Range:** 1 (low) — 10 (critical)

---

## 2. Top 10 Programs by Complexity

| Rank | Program | LOC | Copybooks | I/O Ops | Dependencies | Composite Score | Classification |
|------|---------|-----|-----------|---------|-------------|-----------------|----------------|
| 1 | **COACTUPC.cbl** | 4,236 | 18 | 6+ (READ/REWRITE × 3 files) | XCTL, CSUTLDPY, CSSETATY | **9.5** | Online / CICS |
| 2 | **COCRDUPC.cbl** | 1,560 | 12 | 4+ (READ/REWRITE CARDDAT) | XCTL return | **8.2** | Online / CICS |
| 3 | **COCRDLIC.cbl** | 1,459 | 11 | 5+ (STARTBR/READNEXT/READ) | XCTL to detail/update | **8.0** | Online / CICS |
| 4 | **CBSTM03A.CBL** | 924 | 4 | CALL CBSTM03B × 4 (delegated I/O) | CALL subroutine | **7.8** | Batch |
| 5 | **COACTVWC.cbl** | 941 | 14 | 3+ (EXEC CICS READ × 3 files) | XCTL return | **7.5** | Online / CICS |
| 6 | **COCRDSLC.cbl** | 887 | 12 | 2+ (EXEC CICS READ × 2) | XCTL return | **7.2** | Online / CICS |
| 7 | **COTRN02C.cbl** | 783 | 10 | 3+ (READ/WRITE TRANSACT, READ ACCTDAT/XREF) | CALL CSUTLDTC, XCTL | **7.0** | Online / CICS |
| 8 | **CBTRN02C.cbl** | 731 | 5 | 6+ (OPEN 4 files, READ/WRITE/REJECT) | CALL CEE3ABD | **6.8** | Batch |
| 9 | **COTRN00C.cbl** | 699 | 7 | 3+ (STARTBR/READNEXT/READ) | XCTL dispatch | **6.5** | Batch |
| 10 | **CORPT00C.cbl** | 649 | 8 | 2 (date validation, batch submit) | CALL CSUTLDTC × 2, XCTL | **6.3** | Online / CICS |

---

## 3. Detailed Hotspot Analysis

### 3.1 COACTUPC.cbl — Account Update (Score: 9.5)

**Why it's the #1 hotspot:**
- **4,236 lines** — the largest program in the entire estate by a factor of 2.7×
- **18 copybooks** — highest coupling in the system; pulls in data structures for accounts, cards, customers, cross-references, plus date validation, lookup codes, field attribute macros, and abend handling
- **Complex validation logic** — uses CSSETATY (field attribute setting via COPY REPLACING), CSUTLDPY/CSUTLDWY (date validation with century, month, day, leap-year awareness), and CSLKPCDY (phone area code + state + ZIP validation)
- **Multiple file I/O** — reads and rewrites ACCTDAT, CARDDAT (via AIX), and CUSTDAT in a single transaction
- **Heavy EVALUATE/IF nesting** — extensive field-by-field validation with error flag management
- **BMS map interaction** — SEND MAP/RECEIVE MAP with dynamic attribute manipulation

**Modernization Risk:** VERY HIGH — This program concentrates account update business rules, address validation, date validation, and multi-file transactional updates. Any defect in modernization impacts core account data integrity.

---

### 3.2 COCRDUPC.cbl — Credit Card Update (Score: 8.2)

**Why it's complex:**
- **1,560 lines** with 12 copybooks
- Multi-step card update workflow: read card → validate fields → rewrite with audit trail
- Shares validation patterns with COACTUPC but for card-specific fields (CVV, expiration, embossed name)
- EXEC CICS READ/REWRITE on CARDDAT with record-level locking
- Error handling across multiple validation scenarios

**Modernization Risk:** HIGH — Card data updates involve PCI-sensitive fields (card number, CVV). Business rules for card lifecycle management are embedded in procedural logic.

---

### 3.3 COCRDLIC.cbl — Credit Card List (Score: 8.0)

**Why it's complex:**
- **1,459 lines** with 11 copybooks
- Complex browse logic: EXEC CICS STARTBR/READNEXT with pagination, forward/backward scrolling
- Alternate index (AIX) navigation for account-based card lookups
- Dynamic screen population for variable-length result sets
- XCTL dispatch to card detail (COCRDSLC) or card update (COCRDUPC) based on user selection

**Modernization Risk:** HIGH — Browse/pagination patterns with VSAM STARTBR/READNEXT have no direct equivalent in modern systems. The stateful cursor management requires careful redesign.

---

### 3.4 CBSTM03A.CBL — Statement Generation (Score: 7.8)

**Why it's complex:**
- **924 lines** with 4 copybooks (COSTM01, CUSTREC, CVACT01Y, CVACT03Y)
- Orchestrator pattern: delegates all file I/O to CBSTM03B subroutine via CALL with operation codes (Open/Close/Read/Write/Rewrite)
- Multi-entity join logic: correlates transactions with accounts and customers to produce statement output
- Page-break and formatting logic for print output
- 4 separate CALL invocations to CBSTM03B with different operation parameters

**Modernization Risk:** MEDIUM-HIGH — The orchestrator/subroutine pattern maps well to a service-oriented design, but the print formatting logic needs conversion to PDF/HTML generation.

---

### 3.5 COACTVWC.cbl — Account View (Score: 7.5)

**Why it's complex:**
- **941 lines** with 14 copybooks
- Read-only but accesses 3 VSAM files in a single screen display (ACCTDAT, CARDDAT via AIX, CUSTDAT)
- Cross-reference resolution: reads XREFDATA to link card → account → customer
- BMS map population with formatted display fields
- Despite being read-only, it exercises the full entity relationship graph

**Modernization Risk:** MEDIUM — Good candidate for early modernization as a read-only API endpoint. No write operations reduce risk.

---

### 3.6 COCRDSLC.cbl — Credit Card View (Score: 7.2)

**Why it's complex:**
- **887 lines** with 12 copybooks
- Similar to COACTVWC but for card detail display
- Reads CARDDAT primary and via alternate index (ACCTID)
- Cross-references to customer data for cardholder name display
- Field-level formatting for masked card numbers, formatted dates

**Modernization Risk:** MEDIUM — Read-only display logic. Can be modernized as a card detail API with the view layer separated.

---

### 3.7 COTRN02C.cbl — Transaction Add (Score: 7.0)

**Why it's complex:**
- **783 lines** with 10 copybooks
- Full transaction creation workflow: validate card, validate account via XREF, validate dates (CALL CSUTLDTC), generate transaction ID, write to TRANSACT
- Cross-file validation: reads XREFDATA to verify card-account relationship, reads ACCTDAT to check account status
- Date validation using CSUTLDTC utility for transaction date and processing timestamp
- EXEC CICS WRITE for new record insertion

**Modernization Risk:** MEDIUM-HIGH — Core transaction creation logic with multi-file validation. Maps to a transaction service but requires careful business rule extraction.

---

### 3.8 CBTRN02C.cbl — Transaction Categorize & Post (Score: 6.8)

**Why it's complex:**
- **731 lines** with 5 copybooks
- Batch processing loop: reads daily transactions, categorizes by type/category, posts valid records to master TRANSACT file
- Reject handling: writes invalid records to DALYREJS with rejection reason codes
- Opens 4 files simultaneously (DALYTRAN input, TRANSACT/DALYREJS output, XREF input)
- Cross-reference validation for each incoming transaction
- Running counters and statistics reporting

**Modernization Risk:** MEDIUM — Well-defined input/output batch pattern. Maps to a stream processing or ETL pipeline.

---

### 3.9 COTRN00C.cbl — Transaction List (Score: 6.5)

**Why it's complex:**
- **699 lines** with 7 copybooks
- Browse/pagination pattern similar to COCRDLIC but for transaction data
- EXEC CICS STARTBR/READNEXT on TRANSACT file
- Date-range filtering logic
- Multi-column list display with BMS map population
- XCTL dispatch to transaction detail view

**Modernization Risk:** MEDIUM — Standard list/browse pattern. Converts to a paginated REST API endpoint.

---

### 3.10 CORPT00C.cbl — Transaction Reports (Score: 6.3)

**Why it's complex:**
- **649 lines** with 8 copybooks
- Report parameter collection: start date, end date validation using CSUTLDTC (called twice)
- Submits batch job for report generation (EXEC CICS START or internal reader)
- Date validation with multiple error paths
- Bridges online CICS and batch JCL execution

**Modernization Risk:** MEDIUM — The online-to-batch bridge pattern needs redesign. Report generation maps to an async job queue with PDF/HTML output.

---

## 4. Modernization Priority Recommendations

| Priority | Programs | Rationale | Recommended Approach |
|----------|----------|-----------|---------------------|
| **P1 — Critical** | `COACTUPC.cbl` | Highest complexity (4,236 LOC, 18 copybooks). Concentrates account update business rules, multi-file transactions, and extensive validation logic. Highest risk of defects during modernization. | Extract business rules into a dedicated Account Update microservice. Implement field validation as a shared validation library. Convert VSAM I/O to JPA/JDBC repository pattern. Requires extensive unit test coverage before migration. |
| **P2 — High** | `COCRDUPC.cbl`, `COCRDLIC.cbl`, `COTRN02C.cbl` | High complexity (1,459–1,560 LOC). Card CRUD operations and transaction creation contain PCI-sensitive logic and stateful browse patterns. Core business functionality. | Group as a Card Management microservice (COCRDUPC + COCRDLIC + COCRDSLC) and Transaction Service (COTRN02C + COTRN00C + COTRN01C). Convert STARTBR/READNEXT patterns to SQL pagination. Implement PCI-DSS compliant data handling. |
| **P3 — Medium** | `CBTRN02C.cbl`, `CBACT04C.cbl`, `CBTRN01C.cbl`, `CBTRN03C.cbl`, `COACTVWC.cbl`, `COCRDSLC.cbl`, `COTRN00C.cbl`, `CORPT00C.cbl` | Moderate complexity (649–941 LOC). Batch processing programs have well-defined I/O patterns. Read-only view programs (COACTVWC, COCRDSLC) carry lower risk. | Batch programs (CBTRN01C/02C/03C, CBACT04C) → Spring Batch jobs or AWS Step Functions. View programs (COACTVWC, COCRDSLC, COTRN00C) → read-only REST endpoints as quick wins. CORPT00C → async report generation service. |
| **P4 — Fourth** | `CBSTM03A.CBL`, `CBEXPORT.cbl` | Statement generation and data export. These have moderate complexity but are good candidates for modernization as standalone batch utilities that map cleanly to microservices. CBSTM03A's orchestrator/subroutine pattern (with CBSTM03B) translates well to a statement generation service. CBEXPORT's sequential read-all/write-export pattern maps to an ETL export pipeline. Both programs are self-contained with minimal inter-program dependencies beyond their own subroutines. |

### Migration Sequence Recommendation

```
Phase 1 (Foundation):
  └─ Shared utilities: CSUTLDTC, COBSWAIT, COCOM01Y → Java/Spring common library
  └─ Data layer: VSAM KSDS → relational database (PostgreSQL/DB2)
  └─ Security: CSUSR01Y/USRSEC → Spring Security with proper password hashing

Phase 2 (Read-Only Services — Quick Wins):
  └─ COACTVWC → Account View API
  └─ COCRDSLC → Card Detail API
  └─ COTRN00C + COTRN01C → Transaction List/Detail API

Phase 3 (Write Operations):
  └─ COACTUPC → Account Update API (P1 critical)
  └─ COCRDUPC → Card Update API
  └─ COTRN02C → Transaction Creation API
  └─ COBIL00C → Bill Payment API

Phase 4 (Batch Modernization):
  └─ CBTRN01C + CBTRN02C → Transaction Posting Service (Spring Batch)
  └─ CBACT04C → Interest Calculation Service
  └─ CBTRN03C → Report Generation Service
  └─ CBSTM03A/B → Statement Generation Service
  └─ CBEXPORT/CBIMPORT → Data Migration ETL Pipeline

Phase 5 (Admin & Security):
  └─ COSGN00C → Authentication Service (OAuth2/JWT)
  └─ COADM01C + COUSR00C–03C → User Management API
  └─ COMEN01C + COADM01C → Frontend SPA (React/Angular)
```

---

## 5. Risk Summary

| Risk Category | Count | Programs |
|--------------|-------|----------|
| **VERY HIGH** (Score ≥ 9) | 1 | COACTUPC |
| **HIGH** (Score 7.5–8.9) | 5 | COCRDUPC, COCRDLIC, CBSTM03A, COACTVWC, COCRDSLC |
| **MEDIUM** (Score 6.0–7.4) | 4 | COTRN02C, CBTRN02C, COTRN00C, CORPT00C |
| **LOW** (Score < 6.0) | 20+ | Remaining batch utilities, menu programs, user CRUD |

### Key Observations

1. **Account Update (COACTUPC) is the single highest-risk program** — it contains more business logic than any other program in the estate and touches 3 VSAM files in a single logical transaction.

2. **Browse/pagination patterns (COCRDLIC, COTRN00C)** use CICS STARTBR/READNEXT which has no direct modern equivalent. These require architectural redesign to SQL-based cursor pagination.

3. **The Close-Process-Open batch pattern** (CLOSEFIL → processing → OPENFIL) is an infrastructure concern that disappears with a modern database — no need to close files for batch access.

4. **Date validation (CSUTLDPY/CSUTLDWY)** is used across multiple programs and should be extracted as a shared utility early in the migration to prevent duplicated logic.

5. **Security is rudimentary** — USRSEC stores passwords in plaintext PIC X(08). This must be redesigned with proper hashing and modern authentication (OAuth2/JWT) regardless of the modernization approach chosen.
