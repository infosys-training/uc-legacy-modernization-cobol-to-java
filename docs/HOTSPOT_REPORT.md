# HOTSPOT REPORT — CardDemo COBOL Estate

> Quantitative ranking of the top 10 programs by complexity, with modernization recommendations

---

## 1. Scoring Methodology

Each program is scored across 5 dimensions. Raw values are normalized to 0–100 per dimension and combined with equal weighting (20% each) to produce a composite **Hotspot Score**.

| Dimension | Metric | Max in Estate | Weight |
|-----------|--------|--------------|--------|
| **Size** | Lines of code | 4,236 (COACTUPC) | 20% |
| **Data Coupling** | Copybooks referenced | 58 (COACTUPC) | 20% |
| **I/O Intensity** | I/O operations (READ, WRITE, EXEC CICS, EXEC SQL, DLI, MQ calls) | 118 (CBSTM03A) | 20% |
| **Logic Density** | IF + EVALUATE branching statements | 174 (COACTUPC) | 20% |
| **Integration** | Inter-program dependencies (XCTL, LINK, CALL) | 14 (CBSTM03A) | 20% |

**Composite Score = (Size_norm + Coupling_norm + IO_norm + Logic_norm + Integration_norm) / 5**

---

## 2. Top 10 Programs Ranked by Hotspot Score

| Rank | Program | LOC | Copybooks | I/O Ops | Branch Stmts | Inter-Prog Deps | **Hotspot Score** |
|------|---------|----:|----------:|--------:|-------------:|-----------------:|------------------:|
| **1** | **COACTUPC.cbl** | 4,236 | 58 | 35 | 174 | 1 | **74.8** |
| **2** | **CBSTM03A.CBL** | 924 | 4 | 118 | 20 | 14 | **38.9** |
| **3** | **COTRTLIC.cbl** | 2,098 | 12 | 55 | 2 | 2 | **33.3** |
| **4** | **COCRDUPC.cbl** | 1,560 | 16 | 14 | 80 | 1 | **31.3** |
| **5** | **COCRDLIC.cbl** | 1,459 | 14 | 27 | 68 | 3 | **30.8** |
| **6** | **COTRTUPC.cbl** | 1,702 | 15 | 49 | 2 | 1 | **30.4** |
| **7** | **COPAUA0C.cbl** | 1,026 | 17 | 35 | 31 | 4 | **28.9** |
| **8** | **COPAUS0C.cbl** | 1,032 | 15 | 17 | 36 | 0 | **22.1** |
| **9** | **CBTRN02C.cbl** | 731 | 6 | 24 | 48 | 0 | **18.4** |
| **10** | **COCRDSLC.cbl** | 887 | 16 | 16 | 37 | 0 | **18.3** |

---

## 3. Detailed Hotspot Analysis

### #1 — COACTUPC.cbl (Account Update) — Score: 74.8

**Why it's the top hotspot:**
- **Largest program in the estate** at 4,236 LOC — 15% of total codebase
- **58 copybook references** including 31 uses of `COPY CSSETATY REPLACING` for BMS screen attribute macros — a code generation pattern with no direct Java equivalent
- **174 branching statements** — exhaustive field validation for dates (CEEDAYS), SSN, phone area codes (CSLKPCDY NANPA list), US state codes, ZIP codes
- Touches 3 VSAM files: ACCTFILE (R/W), CARDXREF (R), CUSTFILE (R)
- Uses CSUTLDWY/CSUTLDPY utility copybooks for date conversion

**Modernization complexity:** HIGH — requires extracting ~800 lines of validation logic into reusable Java validation classes, converting BMS screen interactions to REST API, and splitting into Account View + Account Edit + Validation Service components.

---

### #2 — CBSTM03A.CBL (Statement Generation) — Score: 38.9

**Why it ranks high:**
- **118 I/O operations** — the highest in the entire estate
- Generates output in two formats simultaneously (plain text + HTML)
- Calls CBSTM03B as an I/O subroutine (14 inter-program CALL references)
- Complex report formatting with page breaks, account totals, grand totals
- Uses ALTER...PROCEED TO (obsolete COBOL control flow)

**Modernization complexity:** MEDIUM — self-contained batch program with no CICS dependency. **Best candidate for a migration pilot** because it's isolated and can validate the Spring Batch approach.

---

### #3 — COTRTLIC.cbl (Transaction Type List — DB2) — Score: 33.3

**Why it ranks high:**
- **2,098 LOC** — second-largest program
- **55 I/O operations** — heavy DB2 cursor-based pagination (DECLARE CURSOR, OPEN, FETCH, CLOSE)
- Already uses SQL — closest to modern database patterns

**Modernization complexity:** MEDIUM — DB2 SQL translates almost directly to JPA/JDBC. Cursor-based pagination maps to Spring Data `Pageable`. Good early-phase candidate because SQL migration is low-risk.

---

### #4 — COCRDUPC.cbl (Card Update) — Score: 31.3

**Why it ranks high:**
- **80 branching statements** — second-highest logic density
- **16 copybooks** — significant data coupling
- Field-level editing: card name, status, expiry month/year validation
- CICS READ/REWRITE pattern on CARDFILE

**Modernization complexity:** MEDIUM — standard CRUD pattern. Can be templated alongside COACTUPC using a shared `AbstractUpdateController` pattern.

---

### #5 — COCRDLIC.cbl (Card List) — Score: 30.8

**Why it ranks high:**
- **68 branching statements** — paginated browse with complex state management
- Uses STARTBR/READNEXT/READPREV/ENDBR — the VSAM browse pattern that appears in 5+ programs
- **14 copybooks** and forward/backward paging logic

**Modernization complexity:** MEDIUM — once the paginated browse pattern is converted to Spring Data paging, the same pattern applies to COUSR00C (user list) and COTRN00C (transaction list).

---

### #6 — COTRTUPC.cbl (Transaction Type Update — DB2) — Score: 30.4

**Why it ranks high:**
- **1,702 LOC** with **49 I/O operations** — all DB2
- Cascading deletes: when a transaction type is removed, associated categories must also be deleted
- Complex validation of type/category relationships

**Modernization complexity:** MEDIUM — pair with COTRTLIC as a single DB2 migration unit. JPA `@OneToMany(cascade = CascadeType.ALL)` handles cascading deletes natively.

---

### #7 — COPAUA0C.cbl (Authorization Decision — IMS/MQ/CICS) — Score: 28.9

**Why it ranks high:**
- **Spans 3 middleware subsystems**: MQ (MQOPEN/MQGET/MQPUT1), IMS (DLI SCHD/GU/TERM), CICS (READ files)
- **17 copybooks** — highest data coupling outside COACTUPC
- Most architecturally complex integration point in the estate

**Modernization complexity:** HIGH — requires replacing MQ with Spring JMS/SQS, mapping IMS hierarchy to relational tables, and maintaining the request-response message flow. Must be migrated as a unit with COPAUS0C/COPAUS1C/COPAUS2C.

---

### #8 — COPAUS0C.cbl (Auth Summary View — IMS/BMS) — Score: 22.1

**Why it ranks high:**
- **1,032 LOC** with IMS browse (DLI GU/GNP) and BMS screen I/O
- Part of the COPAUA0C → COPAUS0C → COPAUS1C → COPAUS2C chain

**Modernization complexity:** MEDIUM — depends on the IMS-to-relational mapping done for COPAUA0C. Once the auth tables exist, this becomes a standard paginated query.

---

### #9 — CBTRN02C.cbl (Transaction Posting — Batch) — Score: 18.4

**Why it ranks high:**
- **48 branching statements** — extensive validation of daily transactions against xref and account data
- Updates 3 files: TRANSACT (write), ACCTFILE (rewrite balance), TCATBALF (rewrite category balance)
- Writes rejected records to DALYREJS

**Modernization complexity:** LOW-MEDIUM — straightforward Spring Batch `ItemProcessor` with validation logic. Key part of the daily pipeline.

---

### #10 — COCRDSLC.cbl (Card Detail View) — Score: 18.3

**Why it ranks high:**
- **16 copybooks** — same data coupling as COCRDUPC
- **37 branching statements** for input validation
- Reads card + customer data for detail display

**Modernization complexity:** LOW — read-only program, simple REST GET endpoint.

---

## 4. Modernization Priority Recommendations

### Phase 1 — Pilot & Quick Wins (Weeks 1–8)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P1-A** | **CBSTM03A + CBSTM03B** (Statement Gen) | Self-contained batch, no CICS. Proves Spring Batch approach. Highest I/O but isolated scope. |
| **P1-B** | **COUSR00C–03C + COSGN00C** (User/Auth) | Simple CRUD on single file (USRSEC). Low risk, establishes REST API patterns. |

### Phase 2 — DB2 Programs (Weeks 5–12)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P2** | **COTRTLIC + COTRTUPC + COBTUPDT** | Already use SQL — minimal data model transformation. Establishes JPA patterns for the rest of the estate. |

### Phase 3 — Core Business Logic (Weeks 10–22)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P3-A** | **COACTUPC + COACTVWC** (Account) | Highest hotspot score. Extract validation library first (reusable by all services). Requires most effort but delivers most value. |
| **P3-B** | **COCRDLIC + COCRDSLC + COCRDUPC** (Card) | Establishes the paginated browse pattern reusable across 5+ programs. |
| **P3-C** | **COTRN00C + COTRN01C + COTRN02C + COBIL00C** (Transaction) | Moderate complexity. Reuses patterns from Account and Card services. |

### Phase 4 — IMS/MQ Subsystem (Weeks 20–28)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P4** | **COPAUA0C + COPAUS0C–2C + CBPAUP0C** (Authorization) | Most architecturally complex. Migrate as unit. IMS → relational, MQ → JMS/SQS. Defer until relational patterns are proven. |

### Phase 5 — Remaining Batch (Weeks 25–32)

| Priority | Program(s) | Rationale |
|----------|-----------|-----------|
| **P5** | **CBTRN02C, CBACT04C, CBTRN03C, CBEXPORT, CBIMPORT** | Standard batch — can run in parallel with modernized online programs during transition. Spring Batch steps. |

---

## 5. Key Risk Factors

| Risk | Impact | Mitigation |
|------|--------|-----------|
| COACTUPC `COPY REPLACING` pattern (31 macro expansions) | No direct Java equivalent; generates ~600 lines of attribute-setting code | Extract into parameterized utility method; map each REPLACING instance to a method call |
| IMS hierarchical data in COPAUA0C chain | DL/I GN/GNP/GU/REPL/DLET calls with PCB status checking | Map 2 IMS segments (CIPAUSMY, CIPAUDTY) to 2 relational tables with parent-child FK |
| ALTER...PROCEED TO in CBSTM03A | Obsolete COBOL dynamic flow control | Refactor to standard if/switch before migration |
| Plaintext passwords in CSUSR01Y | `SEC-USR-PWD PIC X(08)` stored as plaintext | Replace with BCrypt hashing in Java; add migration step to hash existing passwords |
| VSAM KSDS → relational migration | 12 VSAM files with fixed-length records | Map each copybook to a JPA entity; KSDS key = primary key; FILLER fields dropped |
| Shared ACCTFILE coupling (17 programs) | Any schema change ripples across 77% of online programs | Introduce Account Service API as single access point; other services call API instead of direct file access |

---

## 6. Estate-Wide Metrics Summary

| Metric | Value |
|--------|------:|
| Total programs | 44 |
| Total LOC | 27,970 |
| Average LOC/program | 636 |
| Programs > 1,000 LOC | 8 (18%) |
| Total copybooks | 47 |
| Total JCL jobs | 51 |
| CICS programs | 23 (52%) |
| Batch programs | 16 (36%) |
| IMS programs | 7 (16%) |
| DB2 programs | 4 (9%) |
| MQ programs | 4 (9%) |
| Estimated migration effort | 32–42 weeks (4-person team) |
