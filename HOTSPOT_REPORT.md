# HOTSPOT REPORT — CardDemo COBOL Estate

> **Generated:** 2026-05-27  
> **Repository:** `uc-legacy-modernization-cobol-to-java`  
> **Scope:** 31 core COBOL programs in `app/cbl/`

---

## 1. Lines of Code — Top 10

| Rank | Program | LOC | Type | Purpose |
|------|---------|-----|------|---------|
| 1 | **COACTUPC.cbl** | 4,236 | CICS | Account Update (field validation, error highlighting) |
| 2 | **COCRDUPC.cbl** | 1,560 | CICS | Credit Card Update |
| 3 | **COCRDLIC.cbl** | 1,459 | CICS | Credit Card List (browse with pagination) |
| 4 | **COACTVWC.cbl** | 941 | CICS | Account View |
| 5 | **CBSTM03A.CBL** | 924 | Batch | Account Statement Generation |
| 6 | **COCRDSLC.cbl** | 887 | CICS | Credit Card Detail View |
| 7 | **COTRN02C.cbl** | 783 | CICS | Add Transaction |
| 8 | **CBTRN02C.cbl** | 731 | Batch | Post Daily Transactions |
| 9 | **COTRN00C.cbl** | 699 | CICS | Transaction List (browse) |
| 10 | **COUSR00C.cbl** | 695 | CICS | User List (browse) |

**Total estate LOC (core):** ~19,655

---

## 2. Copybook References — Top 10

| Rank | Program | Unique Copybooks | Notable Inclusions |
|------|---------|-----------------|-------------------|
| 1 | **COACTUPC.cbl** | 18 | CSLKPCDY (1,318-line lookup tables), CSSETATY (×30 REPLACE expansions), CSUTLDPY (375-line date validation) |
| 2 | **COACTVWC.cbl** | 15 | Full data model: CVACT01Y, CVACT02Y, CVACT03Y, CVCUS01Y |
| 3 | **COCRDSLC.cbl** | 13 | Card + Customer data, all CICS infrastructure |
| 4 | **COCRDUPC.cbl** | 13 | Card update with full infrastructure set |
| 5 | **COCRDLIC.cbl** | 11 | Card browse with CSSTRPFY PFKey handler |
| 6 | **COTRN02C.cbl** | 10 | Transaction add with account/xref validation |
| 7 | **COBIL00C.cbl** | 10 | Bill payment with account + transaction |
| 8 | **COADM01C.cbl** | 9 | Admin menu with full CICS infrastructure |
| 9 | **COMEN01C.cbl** | 9 | Main menu with COMEN02Y menu options |
| 10 | **COUSR00C.cbl** | 8 | User list with standard CICS infrastructure |

**Effective LOC after expansion (COACTUPC):** ~4,236 source + ~1,318 (CSLKPCDY) + ~375 (CSUTLDPY) + ~30×30 (CSSETATY) ≈ **6,829+ effective lines**

---

## 3. I/O Operations — Top 10

Counts: COBOL `READ`, `WRITE`, `REWRITE`, `DELETE` + CICS `EXEC CICS READ/WRITE/REWRITE/STARTBR/READNEXT/READPREV`

| Rank | Program | Total I/O | Breakdown | Type |
|------|---------|-----------|-----------|------|
| 1 | **CBSTM03A.CBL** | 102 | READ=4, WRITE=97, REWRITE=1 | Batch |
| 2 | **CBTRN02C.cbl** | 13 | READ=6, WRITE=5, REWRITE=2 | Batch |
| 3 | **CBTRN03C.cbl** | 9 | READ=8, WRITE=1 | Batch |
| 4 | **COCRDLIC.cbl** | 11 | READ=3, CICS_READ=4, STARTBR=2, READNEXT=2, READPREV=2 | CICS |
| 5 | **CBACT01C.cbl** | 11 | READ=2, WRITE=9 | Batch |
| 6 | **CBACT04C.cbl** | 10 | READ=7, WRITE=2, REWRITE=1 | Batch |
| 7 | **CBEXPORT.cbl** | 10 | READ=5, WRITE=5 | Batch |
| 8 | **CBIMPORT.cbl** | 8 | READ=2, WRITE=6 | Batch |
| 9 | **CBSTM03B.CBL** | 8 | READ=5, WRITE=2, REWRITE=1 | Batch |
| 10 | **COACTUPC.cbl** | 9 | WRITE=2, REWRITE=2, CICS_READ=5 | CICS |

---

## 4. Business Logic Density — Top 10

Metric: `IF` statements + `EVALUATE` blocks (proxy for branching complexity)

| Rank | Program | IF | EVALUATE | Total Logic Branches | PERFORM | Complexity Profile |
|------|---------|---|----------|---------------------|---------|-------------------|
| 1 | **COACTUPC.cbl** | 164 | 20 | **184** | 64 | Extremely dense — field-by-field validation, multi-entity cross-checks |
| 2 | **COCRDUPC.cbl** | 72 | 16 | **88** | 26 | High — card update validation |
| 3 | **COCRDLIC.cbl** | 59 | 18 | **77** | 34 | High — browse logic with boundary handling |
| 4 | **CBTRN02C.cbl** | 48 | 0 | **48** | 62 | Moderate — transaction posting with balance checks |
| 5 | **CBACT04C.cbl** | 43 | 0 | **43** | 57 | Moderate — interest calculation, multi-file loop |
| 6 | **COUSR00C.cbl** | 25 | 16 | **41** | 45 | Moderate — browse logic |
| 7 | **COTRN00C.cbl** | 26 | 16 | **42** | 47 | Moderate — browse logic |
| 8 | **COTRN02C.cbl** | 14 | 26 | **40** | 61 | Moderate — EVALUATE-heavy date/field validation |
| 9 | **CBTRN03C.cbl** | 38 | 4 | **42** | 73 | Moderate — report formatting with control breaks |
| 10 | **COCRDSLC.cbl** | 33 | 8 | **41** | 19 | Moderate — card detail display |

---

## 5. Inter-Program Dependencies — Top 10

Metric: Number of programs that call or are called by this program, plus XCTL navigation links.

| Rank | Program | As Caller (out) | As Callee (in) | Total Deps | Type |
|------|---------|----------------|---------------|-----------|------|
| 1 | **COMEN01C.cbl** | 11 (XCTL to any menu option) | 1 (from COSGN00C) | **12** | CICS |
| 2 | **COADM01C.cbl** | 6 (XCTL to admin options) | 1 (from COSGN00C) | **7** | CICS |
| 3 | **CBSTM03A.CBL** | 2 (CBSTM03B, CEE3ABD) | 1 (CREASTMT.JCL) | **3** | Batch |
| 4 | **COSGN00C.cbl** | 2 (→ COMEN01C or COADM01C) | 0 (entry point) | **2** | CICS |
| 5 | **CORPT00C.cbl** | 1 (CSUTLDTC) | 1 (from menu) | **2** | CICS |
| 6 | **COTRN02C.cbl** | 1 (CSUTLDTC) | 1 (from menu) | **2** | CICS |
| 7 | **CBACT01C.cbl** | 2 (COBDATFT, CEE3ABD) | 1 (READACCT.JCL) | **3** | Batch |
| 8 | **CBACT04C.cbl** | 1 (CEE3ABD) | 1 (INTCALC.JCL) | **2** | Batch |
| 9 | **CBTRN02C.cbl** | 0 | 1 (POSTTRAN.JCL) | **1** | Batch |
| 10 | **CBTRN03C.cbl** | 1 (CEE3ABD) | 1 (TRANREPT.JCL) | **2** | Batch |

---

## 6. Composite Hotspot Score

Normalized composite of all five metrics (each 0–10 scale, equally weighted):

| Rank | Program | LOC | Copybooks | I/O | Logic | Deps | **Score** | Type |
|------|---------|-----|-----------|-----|-------|------|-----------|------|
| 1 | **COACTUPC.cbl** | 10.0 | 10.0 | 4.4 | 10.0 | 3.3 | **7.5** | CICS |
| 2 | **COCRDLIC.cbl** | 3.4 | 6.1 | 5.4 | 4.2 | 1.7 | **4.2** | CICS |
| 3 | **CBSTM03A.CBL** | 2.2 | 2.2 | 10.0 | 1.3 | 2.5 | **3.6** | Batch |
| 4 | **COCRDUPC.cbl** | 3.7 | 7.2 | 2.0 | 4.8 | 1.7 | **3.9** | CICS |
| 5 | **CBTRN02C.cbl** | 1.7 | 2.8 | 6.4 | 2.6 | 0.8 | **2.9** | Batch |
| 6 | **COACTVWC.cbl** | 2.2 | 8.3 | 1.5 | 2.1 | 1.7 | **3.2** | CICS |
| 7 | **CBTRN03C.cbl** | 1.5 | 2.8 | 4.4 | 2.3 | 1.7 | **2.5** | Batch |
| 8 | **CBEXPORT.cbl** | 1.4 | 3.3 | 4.9 | 0.9 | 0.8 | **2.3** | Batch |
| 9 | **COTRN02C.cbl** | 1.8 | 5.6 | 2.9 | 2.2 | 1.7 | **2.8** | CICS |
| 10 | **CBACT04C.cbl** | 1.5 | 2.8 | 4.9 | 2.3 | 1.7 | **2.6** | Batch |

---

## 7. Modernization Recommendations

### Priority 1: COACTUPC.cbl — Account Update (Score: 7.5)

**Why first:**
- **Largest program** (4,236 LOC) and most complex in the estate
- **Highest business logic density** (164 IFs + 20 EVALUATEs) — field-by-field validation with nested cross-entity checks across accounts, cards, customers
- **Most copybooks** (18 unique, including massive CSLKPCDY and CSSETATY ×30 REPLACE macro expansions)
- **Central business function** — account update is the heart of account management
- **CICS-dependent** — tightly coupled to BMS maps and COMMAREA navigation

**Modernization approach:**
- Decompose into microservices: separate validation logic (address, date, phone, state/ZIP) from CRUD operations
- Extract CSLKPCDY lookup tables into a reference data service or database
- Replace BMS screen interaction with REST API + modern frontend
- Date validation logic (CSUTLDPY/CSUTLDWY) becomes a shared utility library

### Priority 2: CBSTM03A/B — Statement Generation (Score: 3.6)

**Why second:**
- **Highest I/O** (102 write operations — generates text + HTML output)
- **Call chain dependency** (CBSTM03A calls CBSTM03B ×13 for each I/O operation type)
- **Multi-format output** (text and HTML statements) — natural candidate for template engine
- **Self-contained** — reads from 4 input files, produces 2 output files, no CICS dependency

**Modernization approach:**
- Convert to a standalone batch service (Spring Batch or similar)
- Replace COBOL WRITE statements with modern template engine (Thymeleaf, FreeMarker)
- CBSTM03A/B merge into a single service — the A/B split was a COBOL modularity workaround

### Priority 3: Transaction Pipeline (CBTRN01C → CBTRN02C → CBTRN03C)

**Why third:**
- **Core batch pipeline** — daily transaction processing is the engine of the system
- **CBTRN02C** has high I/O (13 operations) and moderate logic density (48 IF statements)
- **CBTRN03C** handles complex report generation with control breaks and multiple lookup files
- **Pipeline pattern** maps naturally to modern stream processing or ETL frameworks
- **GDG dependencies** (DALYREJS, TRANREPT) need to be mapped to modern storage

**Modernization approach:**
- Implement as a Spring Batch job chain or Apache Kafka streaming pipeline
- CBTRN01C (validation) → message validation service
- CBTRN02C (posting) → transaction posting service with database ACID guarantees
- CBTRN03C (reporting) → reporting service with JasperReports or similar

### Priority 4: COCRDLIC.cbl — Credit Card List (Score: 4.2)

**Why fourth:**
- **Highest CICS operation count** (18) — heavily uses STARTBR/READNEXT/READPREV for VSAM browsing
- **Complex pagination logic** with forward/backward browsing and boundary handling
- **Representative pattern** — once this browse pattern is modernized, it templates COTRN00C, COUSR00C

**Modernization approach:**
- Replace VSAM STARTBR/READNEXT with paginated database queries
- Implement cursor-based pagination API
- This pattern repeats across 4+ programs — build once, apply everywhere

### Priority 5: CBEXPORT/CBIMPORT — Data Migration

**Why fifth:**
- **Data portability** is critical for any migration
- Multi-record VSAM format (CVEXPORT.cpy) needs modern equivalent
- Already batch-only, no CICS dependency
- Natural candidate for ETL tool modernization

**Modernization approach:**
- Convert to modern ETL (AWS Glue, Apache Spark, or standalone Java/Python)
- Replace VSAM multi-record format with JSON/Parquet/CSV
- COMP/COMP-3 fields in CVEXPORT need careful byte-level conversion

---

## 8. Risk Factors

| Factor | Impact | Programs Affected |
|--------|--------|------------------|
| **CSSETATY ×30 REPLACE** | COACTUPC expands this template 30 times at compile — source analysis underestimates effective complexity | COACTUPC |
| **CSLKPCDY (1,318 lines)** | Embedded lookup tables — any change to area codes or state codes requires recompile | COACTUPC, COPAUS0C (sub-app) |
| **Dynamic XCTL navigation** | All CICS programs use `XCTL PROGRAM(CDEMO-TO-PROGRAM)` via COMMAREA — no static analysis can fully trace the call graph | All CICS programs |
| **CEE3ABD coupling** | 6 batch programs call LE abort — need equivalent error handling strategy in modernized code | Batch programs |
| **GDG lifecycle** | Generational backup datasets have no direct modern equivalent — need retention/rotation strategy | TRANBKP, INTCALC, COMBTRAN, TRANREPT |
| **VSAM AIX dependencies** | Alternate indexes on CARDXREF and CARDFILE provide secondary access paths — must be replicated in database design | Any program using CARDAIX |
| **Plaintext passwords** | CSUSR01Y stores passwords as X(08) plaintext — modernization must add proper hashing | COSGN00C, COUSR00-03C |
| **COMP/COMP-3 in exports** | Binary and packed-decimal fields in CVEXPORT.cpy require byte-level conversion | CBEXPORT, CBIMPORT |

---

## 9. Metrics Summary Table (All 31 Programs)

| Program | LOC | Type | Copybooks | IF+EVAL | PERFORM | Total I/O | CALL/XCTL |
|---------|-----|------|-----------|---------|---------|-----------|-----------|
| COACTUPC | 4236 | CICS | 18 | 184 | 64 | 9 | 0 CALL, XCTL dynamic |
| COCRDUPC | 1560 | CICS | 13 | 88 | 26 | 4 | 0 CALL, XCTL dynamic |
| COCRDLIC | 1459 | CICS | 11 | 77 | 34 | 11 | 0 CALL, XCTL dynamic |
| COACTVWC | 941 | CICS | 15 | 38 | 21 | 6 | 0 CALL, XCTL dynamic |
| CBSTM03A | 924 | Batch | 4 | 24 | 33 | 102 | 2 (CBSTM03B, CEE3ABD) |
| COCRDSLC | 887 | CICS | 13 | 41 | 19 | 2 | 0 CALL, XCTL dynamic |
| COTRN02C | 783 | CICS | 10 | 40 | 61 | 5 | 1 (CSUTLDTC) |
| CBTRN02C | 731 | Batch | 5 | 48 | 62 | 13 | 0 |
| COTRN00C | 699 | CICS | 8 | 42 | 47 | 4 | 0 CALL, XCTL dynamic |
| COUSR00C | 695 | CICS | 8 | 41 | 45 | 4 | 0 CALL, XCTL dynamic |
| CBACT04C | 652 | Batch | 5 | 43 | 57 | 10 | 1 (CEE3ABD) |
| CBTRN03C | 649 | Batch | 5 | 42 | 73 | 9 | 1 (CEE3ABD) |
| CORPT00C | 649 | CICS | 8 | 30 | 35 | 1 | 1 (CSUTLDTC) |
| CBEXPORT | 582 | Batch | 6 | 16 | 50 | 10 | 0 |
| COBIL00C | 572 | CICS | 10 | 28 | 38 | 6 | 0 |
| CBTRN01C | 494 | Batch | 6 | 33 | 43 | 5 | 0 |
| CBIMPORT | 487 | Batch | 6 | 16 | 30 | 8 | 0 |
| CBACT01C | 430 | Batch | 2 | 22 | 36 | 11 | 2 (COBDATFT, CEE3ABD) |
| COUSR02C | 414 | CICS | 8 | 23 | 31 | 2 | 0 CALL, XCTL dynamic |
| COUSR03C | 359 | CICS | 8 | 18 | 26 | 1 | 0 CALL, XCTL dynamic |
| COTRN01C | 330 | CICS | 8 | 13 | 17 | 1 | 0 CALL, XCTL dynamic |
| COMEN01C | 308 | CICS | 9 | 13 | 15 | 0 | 0 CALL, XCTL dynamic |
| COUSR01C | 299 | CICS | 8 | 10 | 20 | 1 | 0 CALL, XCTL dynamic |
| COADM01C | 288 | CICS | 9 | 10 | 15 | 0 | 0 CALL, XCTL dynamic |
| COSGN00C | 260 | CICS | 8 | 10 | 11 | 1 | 0 CALL, XCTL dynamic |
| CBSTM03B | 230 | Batch | 0 | 13 | 4 | 8 | 0 (callee only) |
| CBACT02C | 178 | Batch | 1 | 11 | 11 | 1 | 1 (CEE3ABD) |
| CBACT03C | 178 | Batch | 1 | 11 | 11 | 1 | 0 |
| CBCUS01C | 178 | Batch | 1 | 11 | 11 | 1 | 1 (CEE3ABD) |
| CSUTLDTC | 157 | Batch | 0 | 2 | 1 | 0 | 0 (callee only) |
| COBSWAIT | 41 | Batch | 0 | 0 | 0 | 0 | 0 |
