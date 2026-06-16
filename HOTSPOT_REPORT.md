# HOTSPOT REPORT — CardDemo Modernization Priority Ranking

## Scoring Methodology

Each program is scored across five dimensions, normalized to 0–100, then weighted:

| Dimension | Weight | What It Measures | Why It Matters |
|-----------|--------|-----------------|----------------|
| Lines of Code (LOC) | 40% | Raw program size | Largest programs carry the most business logic and migration effort |
| Copybook References | 25% | Data coupling breadth | More copybooks = more shared data structures to map |
| I/O Operations | 20% | File/DB/MQ interaction density | Each I/O point becomes a service call, DB query, or message in Java |
| Business Logic Density | 10% | IF/EVALUATE branching count | Deep branching = complex decision trees to preserve |
| Inter-Program Dependencies | 5% | CALL/XCTL/LINK + inbound dependencies | Tightly coupled programs must migrate together |

**Normalization:** Each dimension is scored as `(program_value / max_value_in_estate) × 100`.

---

## Top 10 Hotspot Ranking

### 1. COACTUPC.cbl — Account Update ⟨ Score: 77.5 / 100 ⟩

| Dimension | Raw Value | Max in Estate | Normalized | Weighted |
|-----------|-----------|--------------|------------|----------|
| LOC | 4,236 | 4,236 | 100.0 | 40.0 |
| Copybooks | 58 | 58 | 100.0 | 25.0 |
| I/O Ops | 11 (CICS READ/REWRITE on 3 VSAM files) | 101 | 10.9 | 2.2 |
| Branches | 174 (IF) + 185 (EVALUATE) = 359 total | 359 | 100.0 | 10.0 |
| Dependencies | 4 (ACCTFILE, CUSTFILE, CARDXREF + return to COMEN01C) | — | 6.7 | 0.3 |
| **Total** | | | | **77.5** |

**Profile:**
- **Largest program in the estate** — 4,236 lines, nearly 2× the next largest
- Exhaustive field validation: dates (CSUTLDPY), SSN, phone area codes (CSLKPCDY), state codes, ZIP prefixes
- 39 instances of `COPY CSSETATY REPLACING` for BMS screen attribute macros
- Reads/writes 3 VSAM files: ACCTFILE, CUSTFILE, CARDXREF
- **Risk:** Single point of failure for all account maintenance; highest migration effort

---

### 2. COTRTLIC.cbl — Transaction Type List (DB2) ⟨ Score: 33.1 / 100 ⟩

| Dimension | Raw Value | Normalized | Weighted |
|-----------|-----------|------------|----------|
| LOC | 2,098 | 49.5 | 19.8 |
| Copybooks | 12 (includes SQLCA, DCLTRTYP) | 20.7 | 5.2 |
| I/O Ops | 30 (EXEC SQL DECLARE CURSOR, OPEN, FETCH, CLOSE) | 29.7 | 5.9 |
| Branches | 16 | 4.5 | 0.4 |
| Dependencies | 3 (DB2 tables + COADM01C caller) | 20.0 | 1.0 |
| **Total** | | | **32.3** |

**Profile:**
- Cursor-based paginated browse of DB2 TRNTYPE table
- Already uses SQL — most natural entry point for database modernization
- Paired with COTRTUPC for full CRUD on transaction types
- **Risk:** Low — DB2 SQL translates nearly 1:1 to JPA/Spring Data

---

### 3. COTRTUPC.cbl — Transaction Type Update (DB2) ⟨ Score: 31.7 / 100 ⟩

| Dimension | Raw Value | Normalized | Weighted |
|-----------|-----------|------------|----------|
| LOC | 1,702 | 40.2 | 16.1 |
| Copybooks | 15 (includes DCLTRTYP, DCLTRCAT, CSSETATY) | 25.9 | 6.5 |
| I/O Ops | 37 (SELECT, INSERT, UPDATE, DELETE with cascading) | 36.6 | 7.3 |
| Branches | 26 | 7.2 | 0.7 |
| Dependencies | 1 (COADM01C caller) | 6.7 | 0.3 |
| **Total** | | | **30.9** |

**Profile:**
- Full CRUD on DB2 TRANSACTION_TYPE and TRANSACTION_CATEGORY tables
- Implements cascading deletes (delete type → delete all categories)
- Uses `COPY CSSETATY REPLACING` for screen attribute validation
- **Risk:** Low — standard CRUD; migrate with COTRTLIC as a pair

---

### 4. COCRDLIC.cbl — Credit Card List ⟨ Score: 28.6 / 100 ⟩

| Dimension | Raw Value | Normalized | Weighted |
|-----------|-----------|------------|----------|
| LOC | 1,459 | 34.4 | 13.8 |
| Copybooks | 14 | 24.1 | 6.0 |
| I/O Ops | 12 (STARTBR, READNEXT, READPREV, ENDBR) | 11.9 | 2.4 |
| Branches | 77 | 21.4 | 2.1 |
| Dependencies | 6 (XCTL to COCRDSLC/COCRDUPC + COMEN01C caller) | 40.0 | 2.0 |
| **Total** | | | **26.3** |

**Profile:**
- Paginated VSAM browse pattern — STARTBR/READNEXT/READPREV/ENDBR
- This pattern appears in 5+ programs (COCRDLIC, COTRN00C, COUSR00C, etc.)
- Creates reusable `AbstractListController` pattern for Java migration
- Routes to COCRDSLC (view) and COCRDUPC (update) via XCTL
- **Risk:** Medium — must solve pagination pattern once, then reuse

---

### 5. COCRDUPC.cbl — Credit Card Update ⟨ Score: 28.0 / 100 ⟩

| Dimension | Raw Value | Normalized | Weighted |
|-----------|-----------|------------|----------|
| LOC | 1,560 | 36.8 | 14.7 |
| Copybooks | 16 | 27.6 | 6.9 |
| I/O Ops | 5 (CICS READ, REWRITE on CARDFILE) | 5.0 | 1.0 |
| Branches | 88 | 24.5 | 2.5 |
| Dependencies | 1 (COCRDLIC caller) | 6.7 | 0.3 |
| **Total** | | | **25.4** |

**Profile:**
- Card update with validation; similar pattern to COACTUPC but simpler
- Paired with COCRDLIC (list) and COCRDSLC (view) — migrate as a unit
- High branch count (88) from validation logic
- **Risk:** Medium — validation patterns can be extracted from COACTUPC migration

---

### 6. CBSTM03A.CBL — Statement Generation ⟨ Score: 37.0 / 100 ⟩

| Dimension | Raw Value | Normalized | Weighted |
|-----------|-----------|------------|----------|
| LOC | 924 | 21.8 | 8.7 |
| Copybooks | 5 (COSTM01, CVACT03Y, CUSTREC, CVACT01Y) | 8.6 | 2.2 |
| I/O Ops | 101 (highest in estate — 13 calls to CBSTM03B + file writes) | 100.0 | 20.0 |
| Branches | 20 | 5.6 | 0.6 |
| Dependencies | 15 (CALL CBSTM03B ×13 + CALL CEE3ABD) | 100.0 | 5.0 |
| **Total** | | | **36.5** |

**Profile:**
- **Highest I/O density in the estate** — 101 operations in 924 lines
- Self-contained batch program — no CICS dependency
- Generates both text (STMTFILE) and HTML (HTMLFILE) output
- Calls CBSTM03B 13 times as an I/O submodule
- **Risk:** Low — **best candidate for proof-of-concept migration** due to isolation and testability

---

### 7. COPAUA0C.cbl — Authorization Decision Engine ⟨ Score: 24.1 / 100 ⟩

| Dimension | Raw Value | Normalized | Weighted |
|-----------|-----------|------------|----------|
| LOC | 1,026 | 24.2 | 9.7 |
| Copybooks | 17 (MQ: CMQODV/CMQMDV/CMQV + IMS: CIPAUSMY/CIPAUDTY + CICS files) | 29.3 | 7.3 |
| I/O Ops | 12 (MQOPEN, MQGET, MQPUT1, MQCLOSE + CICS READ + DL/I) | 11.9 | 2.4 |
| Branches | 36 | 10.0 | 1.0 |
| Dependencies | 8 (MQ calls + CICS files + IMS segments) | 53.3 | 2.7 |
| **Total** | | | **23.1** |

**Profile:**
- **Most architecturally complex program** — spans CICS + IMS + DB2 + MQ
- Receives authorization requests via MQ, processes against VSAM + IMS data, returns MQ response
- Part of chain: COPAUA0C → COPAUS0C → COPAUS1C → COPAUS2C — must migrate as unit
- **Risk:** High — requires simultaneous MQ adapter, IMS-to-RDBMS migration, and CICS replacement

---

### 8. COPAUS0C.cbl — Pending Authorization Summary ⟨ Score: 19.1 / 100 ⟩

| Dimension | Raw Value | Normalized | Weighted |
|-----------|-----------|------------|----------|
| LOC | 1,032 | 24.4 | 9.8 |
| Copybooks | 15 | 25.9 | 6.5 |
| I/O Ops | 4 (CICS READ + IMS GU/GNP) | 4.0 | 0.8 |
| Branches | 36 | 10.0 | 1.0 |
| Dependencies | 2 (LINK COPAUS1C + COMEN01C caller) | 13.3 | 0.7 |
| **Total** | | | **18.8** |

**Profile:**
- CICS+IMS browse of pending authorizations with paginated display
- Uses `EXEC CICS LINK` to COPAUS1C (not XCTL — important for migration)
- IMS DL/I calls: GU (Get Unique), GNP (Get Next within Parent)
- **Risk:** Medium — IMS hierarchy maps to 2 relational tables (summary + detail)

---

### 9. COACTVWC.cbl — Account View ⟨ Score: 19.1 / 100 ⟩

| Dimension | Raw Value | Normalized | Weighted |
|-----------|-----------|------------|----------|
| LOC | 941 | 22.2 | 8.9 |
| Copybooks | 16 | 27.6 | 6.9 |
| I/O Ops | 4 (CICS READ on ACCTFILE, CARDXREF, CUSTFILE) | 4.0 | 0.8 |
| Branches | 38 | 10.6 | 1.1 |
| Dependencies | 1 (COMEN01C caller) | 6.7 | 0.3 |
| **Total** | | | **18.0** |

**Profile:**
- Read-only account display — simplest of the account-related programs
- Reads 3 VSAM files but never writes
- Good candidate for early API endpoint (GET /accounts/{id})
- **Risk:** Low — straightforward read operations

---

### 10. COCRDSLC.cbl — Credit Card View ⟨ Score: 18.8 / 100 ⟩

| Dimension | Raw Value | Normalized | Weighted |
|-----------|-----------|------------|----------|
| LOC | 887 | 20.9 | 8.4 |
| Copybooks | 16 | 27.6 | 6.9 |
| I/O Ops | 4 (CICS READ on CARDFILE, CARDXREF) | 4.0 | 0.8 |
| Branches | 41 | 11.4 | 1.1 |
| Dependencies | 1 (COCRDLIC caller) | 6.7 | 0.3 |
| **Total** | | | **17.5** |

**Profile:**
- Read-only card display with account, customer, and XREF lookups
- Similar pattern to COACTVWC — both are "view detail" programs
- **Risk:** Low — straightforward read operations

---

## Consolidated Ranking Table

| Rank | Program | LOC | Copybooks | I/O Ops | Branches | Deps | **Score** | Classification |
|------|---------|-----|-----------|---------|----------|------|-----------|---------------|
| 1 | COACTUPC | 4,236 | 58 | 11 | 359 | 4 | **77.5** | Online/CICS |
| 2 | CBSTM03A | 924 | 5 | 101 | 20 | 15 | **36.5** | Batch |
| 3 | COTRTLIC | 2,098 | 12 | 30 | 16 | 3 | **32.3** | Online/DB2 |
| 4 | COTRTUPC | 1,702 | 15 | 37 | 26 | 1 | **30.9** | Online/DB2 |
| 5 | COCRDLIC | 1,459 | 14 | 12 | 77 | 6 | **26.3** | Online/CICS |
| 6 | COCRDUPC | 1,560 | 16 | 5 | 88 | 1 | **25.4** | Online/CICS |
| 7 | COPAUA0C | 1,026 | 17 | 12 | 36 | 8 | **23.1** | IMS+MQ+CICS+DB2 |
| 8 | COPAUS0C | 1,032 | 15 | 4 | 36 | 2 | **18.8** | Online/CICS+IMS |
| 9 | COACTVWC | 941 | 16 | 4 | 38 | 1 | **18.0** | Online/CICS |
| 10 | COCRDSLC | 887 | 16 | 4 | 41 | 1 | **17.5** | Online/CICS |

---

## Modernization Recommendations

### Phase 1 — Proof of Concept (Month 1–2)

**Start with CBSTM03A (Statement Generation)**

| Factor | Rationale |
|--------|-----------|
| Isolation | Self-contained batch — no CICS, no IMS, no MQ dependencies |
| Testability | Clear input (VSAM files) → output (text + HTML) pipeline; compare outputs byte-for-byte |
| I/O-heavy | 101 I/O operations → demonstrates that Spring Batch ItemReader/ItemWriter pattern works |
| Quick win | 924 LOC — small enough to complete in 2–3 weeks |
| Paired submodule | CBSTM03B (230 LOC) migrates trivially as a helper class |

**Target architecture:**
```
CBSTM03A/B → Spring Batch Job
  ├── TransactionItemReader (replaces CBSTM03B file reads)
  ├── StatementProcessor (business logic from CBSTM03A)
  ├── TextStatementWriter (replaces STMTFILE writes)
  └── HtmlStatementWriter (replaces HTMLFILE writes; Thymeleaf templates)
```

### Phase 2 — Database Modernization (Month 2–4)

**Migrate COTRTLIC + COTRTUPC (Transaction Type CRUD — DB2)**

| Factor | Rationale |
|--------|-----------|
| Already SQL | DB2 EXEC SQL maps almost 1:1 to JPA/Spring Data repositories |
| Reference data | Transaction types are low-volume reference data — safe to migrate early |
| Full CRUD | Demonstrates all four operations (list/add/update/delete) in Java |
| Cascading logic | COTRTUPC's cascading delete validates that referential integrity is preserved |

**Target architecture:**
```
COTRTLIC/COTRTUPC → Spring Boot REST API
  ├── TransactionTypeController (@RestController)
  ├── TransactionTypeService
  ├── TransactionTypeRepository (JpaRepository)
  ├── TransactionCategoryRepository (JpaRepository)
  └── TransactionType/Category entities (@Entity)
```

### Phase 3 — Core Account Management (Month 3–6)

**Migrate COACTUPC (Account Update) — the #1 hotspot**

This is the highest-risk, highest-value migration target. Approach:

1. **Extract validation into reusable Java classes** — the 359 branching statements are mostly field validation that can become `@Valid` annotations and custom validators
2. **Convert CSSETATY COPY REPLACING** to parameterized utility methods (39 instances → 1 method with field-name parameter)
3. **Split into 3 services:**
   - `AccountService` (read/write ACCTFILE operations)
   - `CustomerService` (read/write CUSTFILE operations)
   - `ValidationService` (date, SSN, phone, state, ZIP validation — reusable across all programs)

**Also migrate in this phase:** COACTVWC (account view — read-only, simpler), COCRDLIC/COCRDSLC/COCRDUPC (card list/view/update — same patterns)

### Phase 4 — Authorization Subsystem (Month 5–7)

**Migrate COPAUA0C → COPAUS0C → COPAUS1C → COPAUS2C as a unit**

| Factor | Rationale |
|--------|-----------|
| IMS → RDBMS | Map IMS hierarchy to 2 relational tables: `pending_auth_summary`, `pending_auth_detail` |
| MQ → Spring JMS | Replace MQ calls with Spring JMS listener/producer |
| LINK chain | COPAUS0C→1C→2C use EXEC CICS LINK — migrate as a single service with 3 endpoints |
| Fraud marking | COPAUS2C uses DB2 for fraud flags — aligns with Phase 2 DB2 migration |

### Phase 5 — Batch Pipeline (Month 6–9)

**Migrate remaining batch programs last**

| Program | Spring Batch Component |
|---------|----------------------|
| CBTRN02C (Post Transactions) | Step with Tasklet: validate → update balances → write master |
| CBACT04C (Interest Calculation) | Step: read category balances → calculate interest → write transactions |
| CBTRN03C (Transaction Report) | Step: sort → format → write report |
| CBEXPORT/CBIMPORT | Data migration utilities (may become one-time scripts) |

**Rationale for last:** Batch programs are sequential file processors with no CICS dependency. They can continue running as COBOL in parallel with migrated online programs during transition.

---

## Risk Summary

| Risk | Programs Affected | Severity | Mitigation |
|------|------------------|----------|------------|
| COACTUPC complexity (4,236 LOC, 359 branches) | 1 | **HIGH** | Split into 3 services; extract validation framework; extensive regression tests |
| IMS-to-RDBMS mapping (7 programs) | COPAUA0C, COPAUS0C–2C, CBPAUP0C, PAUDBLOD/UNL | **MEDIUM** | Hierarchy maps to 2 tables; use GNP → SQL JOIN |
| MQ integration (4 programs) | COPAUA0C, COACCT01, CODATE01 | **MEDIUM** | Spring JMS adapter; test with embedded MQ broker |
| COPY REPLACING macro (CSSETATY — 40 instances) | COACTUPC, COTRTUPC | **LOW** | Convert to parameterized Java utility method |
| Plain-text passwords (CSUSR01Y) | COSGN00C, COUSR00C–03C | **HIGH** | Implement bcrypt + Spring Security in Phase 3 |
| Assembler dependencies (2 modules) | CBACT01C → COBDATFT, COBSWAIT → MVSWAIT | **LOW** | Replace with `java.time` and `Thread.sleep()` |

---

## Estimated Effort

| Phase | Programs | Estimated Weeks | Team Size |
|-------|----------|-----------------|-----------|
| Phase 1: Proof of Concept | CBSTM03A + CBSTM03B | 3 weeks | 1 dev |
| Phase 2: DB2 Migration | COTRTLIC + COTRTUPC + COBTUPDT | 4 weeks | 1 dev |
| Phase 3: Core Account | COACTUPC + COACTVWC + COCRDLIC/SLC/UPC | 10 weeks | 2 devs |
| Phase 4: Auth Subsystem | COPAUA0C + COPAUS0C/1C/2C + CBPAUP0C | 6 weeks | 2 devs |
| Phase 5: Batch Pipeline | CBTRN02C + CBACT04C + CBTRN03C + others | 8 weeks | 2 devs |
| **Total** | **44 programs** | **~31 weeks** | **4 devs peak** |

**Estimated calendar time: 6–9 months with a 4-person team.**
