# Hotspot Report — CardDemo COBOL Estate

> Top 10 programs ranked by complexity, with modernization recommendations.

---

## 1  Ranking Criteria

Each program is scored across five dimensions:

| Dimension | Metric | Weight |
|-----------|--------|--------|
| **Size** | Lines of Code (LOC) | Larger = more effort |
| **Copybook References** | Number of COPY statements | More = wider data coupling |
| **I/O Operations** | Total READ + WRITE + REWRITE + DELETE + EXEC CICS/SQL/DLI | More = more integration points |
| **Business Logic Density** | EVALUATE + IF statement counts (proxy for branching complexity) | Higher = denser logic |
| **Inter-Program Dependencies** | Programs that CALL this program + programs this program CALLs + COMMAREA consumers | Higher = more coupling |

---

## 2  Top 10 Hotspot Programs

### Rank 1: COACTUPC.cbl — Account Update (Online)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **4,237** | #1 |
| Copybooks | **17** | #1 |
| I/O Ops | 32 (19R + 11W + 2RW) + 17 EXEC CICS = **49** | #2 |
| Logic Density | 20 EVALUATE + 328 IF = **348** | #1 |
| Dependencies | Uses COMMAREA, 13 data copybooks, CSLKPCDY validation, date utilities | #1 |

**Analysis:** Largest and most complex program in the estate. Handles full account update lifecycle with extensive field-level validation (phone area codes, state codes, ZIP codes, dates, SSN). Contains 17 copybook references spanning all major entities. The 328 IF statements reflect deep validation nesting.

---

### Rank 2: COTRTLIC.cbl — Transaction Type List (Online, DB2)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **2,099** | #2 |
| Copybooks | **11** | #4 |
| I/O Ops | 35R + 36 DEL + 12 CICS + 16 SQL = **99** | #1 |
| Logic Density | 32 EVALUATE + 172 IF = **204** | #2 |
| Dependencies | DB2 TRNTYPE + TRNTYCAT tables, COMMAREA | Medium |

**Analysis:** Second largest program. High I/O count from paginated DB2 cursor processing with inline SQL. Complex list management with scroll forward/backward, filtering, and delete operations across two DB2 tables.

---

### Rank 3: COTRTUPC.cbl — Transaction Type Update (Online, DB2)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **1,703** | #3 |
| Copybooks | **0** (all inline) | — |
| I/O Ops | 7R + 4W + 51 DEL + 12 CICS + 7 SQL = **81** | #3 |
| Logic Density | 26 EVALUATE + 98 IF = **124** | #4 |
| Dependencies | DB2 TRNTYPE + TRNTYCAT, COMMAREA | Medium |

**Analysis:** Third largest. All data structures defined inline (no copybooks), making it self-contained but harder to maintain. Heavy DELETE operations (51) suggest complex record lifecycle management. Mixed CICS + DB2 makes migration complex.

---

### Rank 4: COCRDUPC.cbl — Credit Card Update (Online)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **1,561** | #4 |
| Copybooks | **13** | #2 |
| I/O Ops | 10R + 7W + 1RW + 12 CICS = **30** | #5 |
| Logic Density | 16 EVALUATE + 147 IF = **163** | #3 |
| Dependencies | CARDDATA, ACCTDATA, CUSTDATA via CICS, COMMAREA | High |

**Analysis:** Significant validation logic for card updates. References 13 copybooks spanning card, account, and customer entities. Contains cross-entity validation (card ↔ account ↔ customer).

---

### Rank 5: COCRDLIC.cbl — Credit Card List (Online)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **1,460** | #5 |
| Copybooks | **11** | #4 |
| I/O Ops | 34R + 18 CICS = **52** | #4 |
| Logic Density | 18 EVALUATE + 120 IF = **138** | #5 |
| Dependencies | CARDDATA VSAM + AIX, COMMAREA | Medium |

**Analysis:** Complex list management with pagination, alternate index browsing, and filtering. High read count from paginated VSAM browse operations.

---

### Rank 6: COPAUA0C.cbl — Authorization Decision (Online, CICS+IMS+MQ)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **1,027** | #7 |
| Copybooks | **14** | #2 |
| I/O Ops | 23R + 2W + 12 CICS + 8 DLI + 4 MQ = **49** | #2 (tied) |
| Logic Density | 10 EVALUATE + 53 IF = **63** | #8 |
| Dependencies | IMS DB, MQ Queues, VSAM files (ACCTDATA, CUSTDATA, XREFDATA), CICS | **Highest** |

**Analysis:** The most integration-heavy program — spans CICS, IMS DL/I, and MQ in a single transaction. Reads authorization requests from MQ, validates against IMS and VSAM data, and sends responses. Four different middleware technologies make this extremely complex to modernize.

---

### Rank 7: COPAUS0C.cbl — Pending Auth Summary View (Online, CICS+IMS)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **1,033** | #6 |
| Copybooks | **14** | #2 (tied) |
| I/O Ops | 6R + 10 CICS + 6 DLI = **22** | #8 |
| Logic Density | 22 EVALUATE + 50 IF = **72** | #7 |
| Dependencies | IMS DB, VSAM files, COMMAREA | High |

**Analysis:** Complex screen that aggregates IMS and VSAM data for display. Uses DL/I calls alongside CICS READ operations. The dual data-access pattern (IMS + VSAM) adds modernization complexity.

---

### Rank 8: COACTVWC.cbl — Account View (Online)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **942** | #8 |
| Copybooks | **15** | #2 (tied) |
| I/O Ops | 17R + 15 CICS = **32** | #6 |
| Logic Density | 10 EVALUATE + 56 IF = **66** | #8 |
| Dependencies | ACCTDATA, CARDDATA, CARDAIX, CUSTDATA, COMMAREA | High |

**Analysis:** Read-only account view spanning 4 VSAM files. Heavy copybook usage (15) for display formatting. A good candidate for early modernization as a read-only service.

---

### Rank 9: CBSTM03A.CBL — Statement Generation (Batch)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **925** | #9 |
| Copybooks | **4** | #9 |
| I/O Ops | 10R + 102W + 1RW = **113** | #1 (raw I/O) |
| Logic Density | 9 EVALUATE + 30 IF = **39** | #10 |
| Dependencies | Calls CBSTM03B, reads 4 VSAM files, writes statement + HTML | Medium |

**Analysis:** Highest raw file I/O count (113 operations) due to writing individual lines to statement and HTML output files. Report generation logic is complex but well-structured. The sub-program call pattern (CBSTM03A → CBSTM03B) represents a clean module boundary.

---

### Rank 10: COCRDSLC.cbl — Credit Card Detail View (Online)

| Metric | Value | Rank |
|--------|-------|------|
| LOC | **888** | #10 |
| Copybooks | **13** | #3 |
| I/O Ops | 11R + 14 CICS = **25** | #7 |
| Logic Density | 8 EVALUATE + 69 IF = **77** | #6 |
| Dependencies | CARDDATA, ACCTDATA, CUSTDATA, COMMAREA | Medium |

**Analysis:** Cross-entity display joining card, account, and customer data. Similar pattern to COACTVWC but for card detail. Good candidate for modernization alongside COCRDUPC.

---

## 3  Composite Scoring Summary

| Rank | Program | LOC | Copybooks | I/O+EXEC | Logic (EVAL+IF) | Integration Complexity | Composite |
|------|---------|-----|-----------|----------|-----------------|----------------------|-----------|
| 1 | **COACTUPC** | 4237 | 17 | 49 | 348 | CICS+VSAM | ★★★★★ |
| 2 | **COTRTLIC** | 2099 | 11 | 99 | 204 | CICS+DB2 | ★★★★☆ |
| 3 | **COTRTUPC** | 1703 | 0 | 81 | 124 | CICS+DB2 | ★★★★☆ |
| 4 | **COCRDUPC** | 1561 | 13 | 30 | 163 | CICS+VSAM | ★★★★☆ |
| 5 | **COCRDLIC** | 1460 | 11 | 52 | 138 | CICS+VSAM | ★★★☆☆ |
| 6 | **COPAUA0C** | 1027 | 14 | 49 | 63 | CICS+IMS+MQ | ★★★★★ |
| 7 | **COPAUS0C** | 1033 | 14 | 22 | 72 | CICS+IMS | ★★★★☆ |
| 8 | **COACTVWC** | 942 | 15 | 32 | 66 | CICS+VSAM | ★★★☆☆ |
| 9 | **CBSTM03A** | 925 | 4 | 113 | 39 | Batch+VSAM | ★★★☆☆ |
| 10 | **COCRDSLC** | 888 | 13 | 25 | 77 | CICS+VSAM | ★★★☆☆ |

---

## 4  Modernization Recommendations

### 4.1  Recommended Modernization Order

#### Wave 1 — Read-Only Views (Low Risk, High Visibility)
| Priority | Program | Rationale |
|----------|---------|-----------|
| 1a | **COACTVWC** (Account View) | Read-only; well-defined input/output; 15 copybooks give full entity coverage; can be re-implemented as a REST API with minimal risk |
| 1b | **COCRDSLC** (Card Detail View) | Read-only; same VSAM access pattern as COACTVWC; complements account view |
| 1c | **COTRN01C** (Transaction View) | Read-only; smallest online program (331 LOC); minimal complexity |

**Why first:** Read-only programs have zero write-side risk. They validate the data access layer migration before tackling updates. These three programs together cover all major entities (Account, Card, Customer, Transaction).

#### Wave 2 — List Screens & User Management
| Priority | Program | Rationale |
|----------|---------|-----------|
| 2a | **COUSR00C–03C** (User CRUD) | Self-contained; single VSAM file (USRSEC); standard CRUD pattern; ideal for migrating to a user/auth microservice |
| 2b | **COTRN00C** (Transaction List) | Pagination logic; single VSAM file; builds on Wave 1 data layer |
| 2c | **COCRDLIC** (Card List) | Pagination + AIX browsing; validates alternate index migration |

#### Wave 3 — Core Business Logic
| Priority | Program | Rationale |
|----------|---------|-----------|
| 3a | **COTRN02C** (Transaction Add) | First write-path program; validates transaction creation pipeline |
| 3b | **COBIL00C** (Bill Payment) | Balance update + transaction write; validates financial write path |
| 3c | **COCRDUPC** (Card Update) | Cross-entity update with validation; builds on Wave 1/2 data layer |
| 3d | **COACTUPC** (Account Update) | Largest program (4,237 LOC); requires full validation logic porting; schedule last in this wave since it depends on all entity services being available |

#### Wave 4 — Batch Processing
| Priority | Program | Rationale |
|----------|---------|-----------|
| 4a | **CBTRN02C** (Post Transactions) | Core batch posting; critical daily job |
| 4b | **CBACT04C** (Interest Calculator) | Core monthly job; financial logic |
| 4c | **CBSTM03A/B** (Statement Gen) | Report generation; high I/O but structured |
| 4d | **CBTRN03C** (Transaction Report) | Reporting; can be replaced with modern reporting tools |
| 4e | **CBEXPORT/CBIMPORT** (Migration) | Data migration utilities; may be replaced by ETL tooling |

#### Wave 5 — Middleware Integration (Highest Risk)
| Priority | Program | Rationale |
|----------|---------|-----------|
| 5a | **COTRTLIC/COTRTUPC** (DB2 Programs) | Requires DB2 → modern RDBMS migration |
| 5b | **COPAUS0C/1C/2C** (IMS Programs) | Requires IMS → modern DB migration |
| 5c | **COPAUA0C** (Auth Decision) | Most complex: CICS + IMS + MQ; requires complete middleware redesign |
| 5d | **CBPAUP0C/PAUDBLOD/PAUDBUNL/DBUNLDGS** (IMS Batch) | IMS batch utilities; may be replaced by modern ETL |

### 4.2  Key Risk Factors

| Risk | Programs Affected | Mitigation |
|------|------------------|------------|
| **CSLKPCDY validation tables** (1,319 lines of lookup data) | COACTUPC | Extract to reference data service or database table |
| **COMMAREA coupling** | All 19 online programs | Replace with session management / API context |
| **Assembler dependencies** (COBDATFT, MVSWAIT) | CBACT01C, COBSWAIT | Replace with Java/modern date and timer APIs |
| **IMS DL/I access** | 7 programs in auth sub-app | Requires IMS-to-RDBMS migration or IMS Connect adapter |
| **MQ integration** | COPAUA0C, COACCT01, CODATE01 | Replace with modern messaging (Kafka, SQS, etc.) |
| **BMS maps** (18 screen definitions) | All online programs | Replace with web UI framework |
| **GDG (Generation Data Groups)** | TRANBKP, COMBTRAN, CREASTMT | Replace with timestamped file naming or S3 versioning |
| **VSAM Alternate Indexes** | CARDFILE, XREFFILE, TRANFILE | Map to database secondary indexes |

### 4.3  Quick Wins

1. **CSUTLDTC** (Date Validation, 158 LOC) — Replace with `java.time` API
2. **COBSWAIT** (Wait Utility, 42 LOC) — Replace with `Thread.sleep()`
3. **CBACT02C/03C/CBCUS01C** (Read-Print programs, ~179 LOC each) — Replace with simple data export scripts
4. **READACCT/READCARD/READCUST/READXREF JCL** — Replace with database queries
